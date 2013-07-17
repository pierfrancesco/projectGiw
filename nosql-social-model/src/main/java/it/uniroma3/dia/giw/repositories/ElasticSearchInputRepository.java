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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

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
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTime;
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
    
    @Inject
    public ElasticSearchInputRepository(final Node node, final ObjectMapper objectMapper) {
    
        this.client = node.client();
        this.mapper = objectMapper;
    }
    
    public void storeToStream(List<Tweet> tweets, MonitoringActivityId monitoringActivityId)
            throws InputRepositoryException {
    
        final List<ContextTweetId> savedIds = new ArrayList<ContextTweetId>();
        
        for (final Tweet tweet : tweets) {
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
        
        final String generatedId = UUID.randomUUID().toString();
        final ContextTweetId contextTweetId = new ContextTweetId(generatedId);
        final ContextTweet contextTweet = new ContextTweet(contextTweetId, tweet,
                monitoringActivityId.getValue(), year, dayOfTheYear);
        
        final String contextTweetJson = serializeContextTweet(contextTweet);
        
        LOGGER.debug("Storing context tweet: " + contextTweetJson);
        
        IndexResponse indexResponse = null;
        try {
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
    }
    
    public void removeFollowers(User user) throws InputRepositoryException {
    
        // To change body of implemented methods use File | Settings | File
        // Templates.
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
    
        throw new UnsupportedOperationException("NIY");
    }
    
    public PageId storeFollowers(long[] followerIds, User user, DateTime startTime, DateTime endTime)
            throws InputRepositoryException {
    
        throw new UnsupportedOperationException("NIY");
    }
    
    public long[] getFollowers(String screenName) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public long[] getFollowing(String screenName) {
    
        // TODO Auto-generated method stub
        return null;
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
