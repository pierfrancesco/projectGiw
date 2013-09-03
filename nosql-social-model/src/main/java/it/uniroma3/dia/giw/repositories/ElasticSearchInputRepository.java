package it.uniroma3.dia.giw.repositories;

import it.uniroma3.dia.giw.exceptions.InputRepositoryException;
import it.uniroma3.dia.giw.model.ContextTweet;
import it.uniroma3.dia.giw.model.ContextTweetId;
import it.uniroma3.dia.giw.model.ContextUser;
import it.uniroma3.dia.giw.model.InputRepository;
import it.uniroma3.dia.giw.model.PageId;
import it.uniroma3.dia.giw.model.TimedValue;
import it.uniroma3.dia.giw.model.monitoring.MonitoringActivityId;
import it.uniroma3.dia.giw.model.twitter.call.TwitterAPIMethod;
import it.uniroma3.dia.giw.model.twitter.data.Tweet;
import it.uniroma3.dia.giw.model.twitter.data.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.common.io.stream.Streamable;
import org.elasticsearch.common.xcontent.ToXContent;
import org.joda.time.DateTime;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ElasticSearchInputRepository implements InputRepository {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ElasticSearchInputRepository.class);
    
    // db
    public static final String DATABASE_NAME = "twitter";
    
    // types
    public static final String CONTEXT_TWEET_TYPE = "context_tweet";
    public static final String USER_TYPE = "user";
    
    // field names
    public static final String CONTEXT_TWEET_ID = "context_tweet_id";
    public static final String MONITORING_ACTIVITY_ID = "monitoringActivityId";
    public static final String TWEET_FIELD = "tweet";
    public static final String USER_FIELD = "user";
    
    private final Client client;
    
    private final ObjectMapper mapper;

    protected InputRepository writeRepository;
    
    @Inject
    public ElasticSearchInputRepository(final Node node, final ObjectMapper objectMapper) {
    
        this.client = node.client();
        this.mapper = objectMapper;
        //System.out.println("THIS CLIENT"+this.client);
    }
    
    public void storeToStream(List<Tweet> tweets, MonitoringActivityId monitoringActivityId)
            throws InputRepositoryException {
    
        final List<ContextTweetId> savedIds = new ArrayList<ContextTweetId>();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        //System.out.println("STAK TRACE__________________________"+stackTraceElements[1]);
        
        for (final Tweet tweet : tweets) {
            System.out.println("Sono DENTRO a ElasticSearchInputRepository con la lista che gli viene passata ***************** ");
                //System.out.println(this);
            final ContextTweetId savedId = storeToStream(tweet, monitoringActivityId);
            // TODO manage exceptions
            savedIds.add(savedId);
        }
        
        // refresh indices after writing
        this.client.admin().indices().prepareRefresh().execute().actionGet();
        LOGGER.debug("saved '" + savedIds.size() + "' tweets for monitoringActivityId '"
                + monitoringActivityId.getValue() + "'");
    }
    
    public ContextTweetId storeToStream(Tweet tweet, MonitoringActivityId monitoringActivityId)
            throws InputRepositoryException {
        
        final DateTime createdAt = new DateTime(tweet.getCreatedAt());
        final int dayOfTheYear = createdAt.getDayOfYear();
        final int year = createdAt.getYear();

        final DateTime createdAtMod = new DateTime(createdAt.year().get(),createdAt.monthOfYear().get(),createdAt.dayOfMonth().get(),createdAt.getHourOfDay(),30);
        final String dateIdString = createdAtMod.toString();
        
        //qui ora voglio inserire un metodo che mi dice: crea un indice parallelo
                final SearchResponse response = this.client
                .prepareSearch(ElasticSearchInputRepository.DATABASE_NAME).setTypes("occurences")
                .setQuery(QueryBuilders.termQuery("_id",dateIdString))
                .setSize(1).execute().actionGet();
        if(response.hits().getTotalHits() != 0){

            
            //final String userId = 
            final String script = "{\""+tweet.getUser().getId()+"\":\"1\"}";
            //final String script2 = "\""+tweet.getUser().getId()+"\"";
            System.out.println("\nora NOci sono\n+++++++++++++++++++++++++++++++++"+response);
                    final String toStore = String.valueOf(tweet.getUser().getId());
                    final String script2 = "{\"id\":\""+toStore+"\",\"count\":\"1\"}";
                    this.client.prepareUpdate("twitter", "occurences", dateIdString)
                    .setScript("if (ctx._source.author.id.contains("+toStore+")) { ctx.op=\"none\" }  else { ctx._source.author += "+script2+" }")
                    //.setScript("ctx._source.author += {\"id\" :"+tweet.getUser().getId()+",\"count\": 1 }")
                    .execute().actionGet(); 

        } else {
                    System.out.println("\n\n\n\n\n\n\n\n\n\nora ci sono\n\n\n\n\n\n+++++++++++++++++++++++++++++++++\n\n\n\n\n\n\n");

                    //final String toStore = "{\"time\":\""+dateIdString+"\",\"author\":["+tweet.getUser().getId()+"]}";
                    final String toStore = "{\"author\":[{\"id\" :\""+tweet.getUser().getId()+"\",\"count\": \"1\" }]}";
                    //final String toStore = "{\"author\":[{\""+tweet.getUser().getId()+"\": \"1\"}]}";
                    IndexResponse indexResponse0 = null;
                    indexResponse0 = client
                    .prepareIndex(DATABASE_NAME, "occurences",dateIdString)
                    .setSource(toStore).execute().actionGet();       
        }
        final String generatedId = UUID.randomUUID().toString();
        final ContextTweetId contextTweetId = new ContextTweetId(generatedId);
        final ContextTweet contextTweet = new ContextTweet(contextTweetId, tweet,
                monitoringActivityId.getValue(), year, dayOfTheYear);
        
        final String contextTweetJson = serializeContextTweet(contextTweet);
        
        LOGGER.debug("Storing context tweet: " + contextTweetJson);
        //System.out.println("IL JSON"+contextTweetJson);

        
        IndexResponse indexResponse = null;
        try {
            //System.out.println("qui dentro sei arrivato");
            //System.out.println(contextTweetId.getId());

            indexResponse = client
                    .prepareIndex(DATABASE_NAME, CONTEXT_TWEET_TYPE, contextTweetId.getId())
                    .setSource(contextTweetJson).execute().actionGet();
        } catch (ElasticSearchException e) {
            // logs warning, does nothing
            LOGGER.warn("error while saving tweet: '" + contextTweetId
                    + "' for monitoringActivityId '" + monitoringActivityId.getValue() + "'");
        }
        
        LOGGER.debug("Tweet stored with index: " + indexResponse.getId());
        return contextTweetId;
    }
    
    private String serializeContextTweet(ContextTweet contextTweet) throws InputRepositoryException {
    
        final String contextTweetJson;
        try {
            contextTweetJson = this.mapper.writeValueAsString(contextTweet);
        } catch (final Exception e) {
            final String emsg = "cant serialise json from context tweet '" + contextTweet + "'";
            throw new InputRepositoryException(emsg, e);
        }
        return contextTweetJson;
    }
    
    public void removeStream(MonitoringActivityId monitoringActivityId)
            throws InputRepositoryException {
    
        final QueryBuilder theMonitoringActivity = QueryBuilders.fieldQuery(
                ElasticSearchInputRepository.MONITORING_ACTIVITY_ID,
                monitoringActivityId.getValue());
        
        try {
            final DeleteByQueryResponse removeTweetsResponse = this.client
                    .prepareDeleteByQuery(DATABASE_NAME).setTypes(CONTEXT_TWEET_TYPE)
                    .setQuery(theMonitoringActivity).execute().actionGet();
            LOGGER.debug("all tweets deleted of monitoring activity: " + monitoringActivityId
                    + " - response: " + removeTweetsResponse);
        } catch (final IndexMissingException e) {
            // if it's missing there's no tweet to remove
            LOGGER.warn("non existing index: '" + e.getMessage() + "'");
        }
    }
    
    public List<ContextTweet> getTweets(MonitoringActivityId monitoringActivityId, int limit)
            throws InputRepositoryException {
    
        final QueryBuilder theMonitoringActivity = QueryBuilders.fieldQuery(
                ElasticSearchInputRepository.MONITORING_ACTIVITY_ID,
                monitoringActivityId.getValue());
        
        final SearchResponse searchResponse = this.client.prepareSearch(DATABASE_NAME)
                .setTypes(CONTEXT_TWEET_TYPE).setQuery(theMonitoringActivity).setSize(limit)
                .execute().actionGet();
        
        final Iterator<SearchHit> resultsIterator = searchResponse.getHits().iterator();
        
        final List<ContextTweet> tweets = new ArrayList<ContextTweet>();
        
        while (resultsIterator.hasNext()) {
            final SearchHit currentResult = resultsIterator.next();
            final String jsonContextTweet = currentResult.getSourceAsString();
            final ContextTweet contextTweet = deserializeContextTweet(jsonContextTweet);
            tweets.add(contextTweet);
        }
        
        return tweets;
    }
    
    public ContextTweet getByIdFromStream(ContextTweetId id) throws InputRepositoryException {
    
        GetResponse getResponse;
        
        try {
            getResponse = this.client.prepareGet(DATABASE_NAME, CONTEXT_TWEET_TYPE, id.getId())
                    .execute().actionGet();
        } catch (ElasticSearchException e) {
            throw new InputRepositoryException("can't get tweet by id '" + id.getId() + "'", e);
        }
        LOGGER.debug("getResponse: " + getResponse.getSourceAsString());
        
        ContextTweet contextTweet = null;
        
        if (getResponse.exists()) {
            contextTweet = deserializeContextTweet(getResponse.getSourceAsString());
        }
        
        return contextTweet;
    }
    
    private ContextTweet deserializeContextTweet(String contextTweetJson)
            throws InputRepositoryException {
    
        final ContextTweet contextTweet;
        try {
            contextTweet = mapper.readValue(contextTweetJson, ContextTweet.class);
        } catch (final Exception e) {
            final String emsg = "cant deserialise tweet from json '" + contextTweetJson + "'";
            throw new InputRepositoryException(emsg, e);
        }
        
        return contextTweet;
    }
    
    public void removeFollowing(User user) throws InputRepositoryException {
    
        // To change body of implemented methods use File | Settings | File
        // Templates.
                final String newTitle = "";

        this.client.prepareUpdate("twitter", "friends", user.getScreenName()) 
                    .setScript("ctx._source.list=\"" + newTitle + "\"") 
                    .execute() 
                    .actionGet(); 
    }
    
    public void removeFollowers(User user) throws InputRepositoryException {


        //Questo metodo aggiorna dopo che ho fatto store follower
        /*SearchResponse response1 = this.client.prepareSearch("twitter").setTypes("followers").setSize(1).execute().actionGet();
        System.out.println("Sto stampando da dentro l'iterator"+ response1);

                    try
            {
               
                Thread.sleep(1000);
            } catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }*/

        final String newTitle = "";

        this.client.prepareUpdate("twitter", "followers", user.getScreenName()) 
                    .setScript("ctx._source.list=\"" + newTitle + "\"") 
                    .execute() 
                    .actionGet(); 
    

        //SearchResponse response = this.client.prepareSearch("twitter").setTypes("followers").setQuery(QueryBuilders.termQuery("_id", user.getScreenName())).setSize(1).execute().actionGet();
        //SearchResponse response = this.client.prepareSearch("twitter").setTypes("followers").setSize(1).execute().actionGet();
        //System.out.println("Sto stampando da dentro l'iterator"+ response);

        /*QUI CANCELLO IL TWEET E LO RIMETTO PRIVO DELLA LISTA DEI FOLLOWER
        IL METODO VA PERFEZIONATO CON L'UTILIZZO DI UPDATE.
        */
        /*SearchResponse response = this.client.prepareSearch("twitter").setQuery(QueryBuilders.termQuery("screenName", user.getScreenName())).setSize(1).execute().actionGet();

       final Iterator<SearchHit> resultsIterator = response.getHits().iterator();
       String currentElement = "";
            final ArrayList<User> emptyUser = new ArrayList<User>(0);
            final Tweet toStore = new Tweet();
            ContextTweet deserialized = new ContextTweet();
            String monitoringActivityIdtoStore = "";
            while (resultsIterator.hasNext()) {
            //System.out.println("Sto stampando da dentro l'iterator");
            final SearchHit currentResult = resultsIterator.next();
            currentElement = currentResult.getId();
            final String jsonContextTweet = currentResult.getSourceAsString();
            deserialized = deserializeContextTweet(jsonContextTweet);
            monitoringActivityIdtoStore = deserialized.getMonitoringActivityId();
            final Tweet tweeet = deserialized.getTweet();
            final User useer = tweeet.getUser();
            final long idd = tweeet.getId();
            final String text = tweeet.getText();
            final String source = tweeet.getSource();
            final Date createdAt = tweeet.getCreatedAt();
            //System.out.println("Sto stampando il numero degli user PRIMA"+useer.getFollowers());
            useer.setFollowers(emptyUser);
            //System.out.println("Sto stampando il numero degli user DOPO"+useer.getFollowers());
            //qui ora devo ricreare il tweet privo di tutti i followers e poi risalvarlo;
            toStore.setUser(useer);
            toStore.setId(idd);
            toStore.setText(text);
            toStore.setSource(source);
            toStore.setCreatedAt(createdAt);
            //System.out.println("Sto stampando IDDDDDDDDDDDDD "+currentElement);
            final DateTime createdAt2 = new DateTime(toStore.getCreatedAt());
            final int dayOfTheYear = createdAt2.getDayOfYear();
            final int year = createdAt2.getYear();
            
            final String generatedId = UUID.randomUUID().toString();
            final ContextTweetId contextTweetId = new ContextTweetId(generatedId);
            
            final String contextTweetJson = serializeContextTweet(deserialized);
            //this.client.prepareUpdate("twitter", "contex_tweet", currentElement).addScriptParam("contextTweetJson", contextTweetJson).setScript("ctx._source = contextTweetJson").execute().actionGet();
            
            final DeleteByQueryResponse usersResponse = client.prepareDeleteByQuery(DATABASE_NAME)
                        .setQuery(QueryBuilders.matchAllQuery()).setTypes(CONTEXT_TWEET_TYPE).execute()
                        .actionGet();
            try
            {
               
                Thread.sleep(1000);
            } catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
        IndexResponse indexResponse = null;
        try {
            
            //System.out.println(contextTweetId.getId());

            indexResponse = client
                    .prepareIndex(DATABASE_NAME, CONTEXT_TWEET_TYPE, contextTweetId.getId())
                    .setSource(contextTweetJson).execute().actionGet();
        } catch (ElasticSearchException e) {
            System.out.println("ERRORE");

        }

        }
        

        /*try
            {
               
                Thread.sleep(1000);
            } catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
                    SearchResponse response2 = this.client.prepareSearch().setQuery(QueryBuilders.termQuery("screenName", user.getScreenName())).setSize(1).execute().actionGet();
                    final Iterator<SearchHit> resultsIterator2 = response2.getHits().iterator();
                    System.out.println("qui dentro sei arrivato");
                                        while (resultsIterator2.hasNext()) {
                                final SearchHit currentResult = resultsIterator2.next();
                                final String jsonContextTweet = currentResult.getSourceAsString();
                                System.out.println("Sto stampando il XXXXXXXXXXXXXXXXXXXXXXX "+response2);
                                //this.client.prepareUpdate("twitter", "contex_tweet", currentElement).setScript("ctx._source.place=ITALIA").execute().actionGet();
                            }*/
    }
    
    @Deprecated
    private void storeUser(final User user, List<String> monitoringActivityIds,
            TwitterAPIMethod method, long userId) throws InputRepositoryException {
    
        ContextUser contextUser = new ContextUser(user, monitoringActivityIds);
        
        String contextUserJson = serializeContextUser(contextUser);
        
        final IndexResponse indexResponse;
        try {
            indexResponse = this.client
                    .prepareIndex(DATABASE_NAME, USER_TYPE, String.valueOf(user.getId()))
                    .setSource(contextUserJson).execute().actionGet();
        } catch (ElasticSearchException e) {
            throw new InputRepositoryException("can't index user " + user.getId(), e);
        }
        LOGGER.info("User stored with index: " + indexResponse.getId());
    }
    
    public void removeFollowing(MonitoringActivityId monitoringActivityId)
            throws InputRepositoryException {
    
        try {
            final DeleteByQueryResponse usersResponse = client.prepareDeleteByQuery(DATABASE_NAME)
                    .setQuery(QueryBuilders.matchAllQuery()).setTypes(USER_TYPE).execute()
                    .actionGet();
            LOGGER.info("all users deleted of monitoring activity: " + monitoringActivityId
                    + " - response: " + usersResponse);
        } catch (final IndexMissingException e) {
            // if it's missing there's no user to remove
        }
    }
    
    private String serializeContextUser(ContextUser contextUser) throws InputRepositoryException {
    
        String contextUserJson;
        try {
            contextUserJson = mapper.writeValueAsString(contextUser);
        } catch (final Exception e) {
            final String emsg = "cant serialise json from context user '" + contextUser + "'";
            throw new InputRepositoryException(emsg, e);
        }
        return contextUserJson;
    }
    
    private ContextUser deserializeContextUser(String contextUserJson)
            throws InputRepositoryException {
    
        try {
            return this.mapper.readValue(contextUserJson, ContextUser.class);
        } catch (IOException e) {
            throw new InputRepositoryException("cant deserialize '" + contextUserJson
                    + "' to contextUser", e);
        }
    }
    
    public void shutDownRepository() {
    
        client.close();
    }
    
    public PageId storeTimeline(List<Tweet> tweets, User user, DateTime startTime, DateTime endTime)
            throws InputRepositoryException {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public void removeTimeline(User user) throws InputRepositoryException {
    
        // TODO Auto-generated method stub
        
    }
    
    public PageId storeFollowing(long[] followingIds, User user, DateTime startedAt,
            DateTime finishedAt) throws InputRepositoryException {
                String followersJson = "";
                final String screenName = user.getScreenName();
        try {
            followersJson = this.mapper.writeValueAsString(followingIds);
        } catch (final Exception e) {
            final String emsg = "cant serialise json from context tweet '" + followersJson + "'";
            throw new InputRepositoryException(emsg, e);
        }
        IndexResponse indexResponse = null;

        try {
             
             String toPass = "{\"list\":"+followersJson+"}";
             //System.out.println(toPass);
            indexResponse = client
                    .prepareIndex(DATABASE_NAME, "friends", String.valueOf(user.getScreenName()))
                    .setSource(toPass).execute().actionGet();
        } catch (ElasticSearchException e) {
            System.out.println("ERRORE"+e);

        }
                   /*try
            {
               
                Thread.sleep(10000);
            } catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
                           SearchResponse response = client.prepareSearch("twitter").setTypes("friends").execute().actionGet();
       final Iterator<SearchHit> resultsIterator = response.getHits().iterator();
       while (resultsIterator.hasNext()) {
                                final SearchHit currentResult = resultsIterator.next();
                                final String jsonContextTweet = currentResult.getSourceAsString();
                                System.out.println("Sto stampando il XXXXXXXXXXXXXXXXXXXXXXX "+response);
                            }*/


        PageId _id = new PageId(String.valueOf(user.getId()));
        return _id;

    }
    
    public PageId storeFollowers(long[] followerIds, User user, DateTime startTime, DateTime endTime)

            throws InputRepositoryException {
                String followersJson = "";
                final String screenName = user.getScreenName();
        try {
            followersJson = this.mapper.writeValueAsString(followerIds);
        } catch (final Exception e) {
            final String emsg = "cant serialise json from context tweet '" + followersJson + "'";
            throw new InputRepositoryException(emsg, e);
        }
        IndexResponse indexResponse = null;

        try {
             
             String toPass = "{\"list\":"+followersJson+"}";
             //System.out.println(toPass);
            indexResponse = client
                    .prepareIndex(DATABASE_NAME, "followers", String.valueOf(user.getScreenName()))
                    .setSource(toPass).execute().actionGet();
        } catch (ElasticSearchException e) {
            System.out.println("ERRORE"+e);

        }
                   /* 
                    questa parte serve a mostrare che li ha effettivamente inseriti
                   try
            {
               
                Thread.sleep(10000);
            } catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
                           SearchResponse response = client.prepareSearch().execute().actionGet();
       final Iterator<SearchHit> resultsIterator = response.getHits().iterator();
       while (resultsIterator.hasNext()) {
                                final SearchHit currentResult = resultsIterator.next();
                                final String jsonContextTweet = currentResult.getSourceAsString();
                                System.out.println("Sto stampando il XXXXXXXXXXXXXXXXXXXXXXX "+response);
                            }*/


        PageId _id = new PageId(String.valueOf(user.getId()));
        return _id;
    }
    
    public long[] getFollowers(String screenName) throws InputRepositoryException{

                //questo è il metodo vecchio che esamina la stuttura classica di ogni tweet e ne estrae la lista dei follower.
                // è possibile fare un benchmark fra i 2 metodi
        
       /*SearchResponse response = this.client.prepareSearch().setQuery(QueryBuilders.termQuery("screenName", screenName)).setSize(1).execute().actionGet();
       final Iterator<SearchHit> resultsIterator = response.getHits().iterator();
            ArrayList<Long> idss = new ArrayList<Long>();
            while (resultsIterator.hasNext()) {
            final SearchHit currentResult = resultsIterator.next();
            final String jsonContextTweet = currentResult.getSourceAsString();
            final ContextTweet deserialized = deserializeContextTweet(jsonContextTweet);
            final Tweet tweeet = deserialized.getTweet();
            final User useer = tweeet.getUser();
            final ArrayList<User> follower = (ArrayList<User>) useer.getFollowers();
            
            for(User s : follower){
                
                idss.add(s.getId());
            }
            
        }
        
        //String name = response.toString();
        long[] result = new long[idss.size()];
        int t=0;
          for(long s : idss){
                
                result[t] = s;
                t++;
            }
        
       //System.out.println("********************\n\n"+result);

        
        return result;*/

        //da qui inizia la versione che prende il documento salvato dal test pierfrancesco 4. Questo test salva in maniera ad hoc i followers per ogni utente
        //in modo da facilitarne l'estrazione.
                   /*try
            {
               
                Thread.sleep(1000);
            } catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }*/

        SearchResponse response = client.prepareSearch("twitter").setTypes("followers").setQuery(QueryBuilders.termQuery("_id", screenName)).setSize(1).execute().actionGet();
       final Iterator<SearchHit> resultsIterator = response.getHits().iterator();
       //ArrayList<Long> temp = new ArrayList<Long>();
       ArrayList<Long> idss = new ArrayList<Long>();
       JSONObject json = new JSONObject();
       System.out.println(response);
       while (resultsIterator.hasNext()) {
                                final SearchHit currentResult = resultsIterator.next();
                                final String jsonContextTweet = currentResult.getSourceAsString();
                                        try {
                                            JSONObject resultJSON = this.mapper.readValue(jsonContextTweet, JSONObject.class);
                                            idss = (ArrayList<Long>)resultJSON.get("list") ;
                                    } catch (final Exception e) {System.out.println(e);}

                                
                            }
                                    long[] id = new long[idss.size()];
                                          for(int t=0; t < idss.size() ;t++){
                                                
                                                Long l = Long.parseLong(String.valueOf(idss.get(t)));
                                                id[t] = l;
                                            }
                            

                            return id ;

    }
    
    public long[] getFollowing(String screenName) {
    
        SearchResponse response = client.prepareSearch("twitter").setTypes("friends").setQuery(QueryBuilders.termQuery("_id", screenName)).setSize(1).execute().actionGet();
       final Iterator<SearchHit> resultsIterator = response.getHits().iterator();
       //ArrayList<Long> temp = new ArrayList<Long>();
       ArrayList<Long> idss = new ArrayList<Long>();
       JSONObject json = new JSONObject();
       System.out.println(response);
       while (resultsIterator.hasNext()) {
                                final SearchHit currentResult = resultsIterator.next();
                                final String jsonContextTweet = currentResult.getSourceAsString();
                                        try {
                                            JSONObject resultJSON = this.mapper.readValue(jsonContextTweet, JSONObject.class);
                                            idss = (ArrayList<Long>)resultJSON.get("list") ;
                                    } catch (final Exception e) {System.out.println(e);}

                                
                            }
                                    long[] id = new long[idss.size()];
                                          for(int t=0; t < idss.size() ;t++){
                                                
                                                Long l = Long.parseLong(String.valueOf(idss.get(t)));
                                                id[t] = l;
                                            }
                            

                            return id ;
    }
    
    public TimedValue getTimedCount(long userId, TwitterAPIMethod externalMethod) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public TimedValue getTimedCount(String screenName, TwitterAPIMethod externalMethod) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public int getPagesAmount(long userId, TwitterAPIMethod method) {
    
        // TODO Auto-generated method stub
        return 0;
    }
    
    public int getPagesAmount(String screenName, TwitterAPIMethod method) {
    
        // TODO Auto-generated method stub
        return 0;
    }
    
}
