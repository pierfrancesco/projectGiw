package it.uniroma3.dia.giw.repositories;

import it.uniroma3.dia.giw.model.OutputRepository;
import it.uniroma3.dia.giw.model.StringOccurrences;
import it.uniroma3.dia.giw.model.monitoring.MonitoringActivityId;
import it.uniroma3.dia.giw.model.twitter.data.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.google.gson.Gson;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ElasticSearchOutputRepository implements OutputRepository {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ElasticSearchOutputRepository.class);
    
    private final Client client;
    
    private final ObjectMapper objectMapper;
    
    public ElasticSearchOutputRepository(Node node, ObjectMapper objectMapper) {
    
        this.client = node.client();
        this.objectMapper = objectMapper;
    }
    
    public StringOccurrences authorOccurrences(MonitoringActivityId monitoringActivityId,
            DateTime startDate, DateTime endDate, int maxItems) {
        //System.out.println("QUESTO è il monitor"+startDate+"\n"+endDate);


        // queries
        final QueryBuilder beFromThisMonitoringActivity = buildInQuery(monitoringActivityId);
        final QueryBuilder beInTimeWindow = buildInclusiveRangeQuery("createdAt", startDate,
                endDate);

        
        // AND query
        final QueryBuilder tweetsInTimeWindowAndFromMonitoringActivity = QueryBuilders.boolQuery()
                .must(beFromThisMonitoringActivity).must(beInTimeWindow);
        
        // facet
        final String screenNameFacetName = "screenName";
        final String screenNameField = "screenName";
        final TermsFacetBuilder authorFacet = buildFacetOnField(screenNameFacetName,
                screenNameField);


        Map<String, Integer> freq = new HashMap<String, Integer>();


        final SearchResponse response0 = this.client
                .prepareSearch(ElasticSearchInputRepository.DATABASE_NAME).setTypes("occurences")
                .setQuery(beInTimeWindow).setSize(maxItems).execute().actionGet();

                if(response0.hits().getTotalHits() != 0){

        final Iterator<SearchHit> resultsIterator = response0.getHits().iterator();
        //System.out.println("\n\nQUI CI SEI ARRIVATO\n\n\n"+response0);
                            while (resultsIterator.hasNext()) {
                                final SearchHit currentResult = resultsIterator.next();
                                final String jsonContextTweet = currentResult.getSourceAsString();
                                            try {
                                             JSONObject json = objectMapper.readValue(jsonContextTweet, JSONObject.class);



                                             ArrayList<LinkedHashMap> author = (ArrayList<LinkedHashMap>) json.get("author");
                                            //System.out.println("Sto stampando CON IL Array\n\n"+author);
                                            //System.out.println("Sto stampando CON IL Array\n\n"+author.size());

                                            

                                            //
                                            for(int s=0; s < author.size();s++ ){
                                                //System.out.println("Sto stampando CON IL GET\n\n"+author.get(s).get("screenName"));
                                                final String stringValue = (String) author.get(s).get("screenName");
                                                Integer count = freq.get(stringValue);
                                                if (count == null) {
                                                    freq.put(stringValue, 1);
                                                }
                                                else {
                                                    freq.put(stringValue, count + 1);
                                                }
                                            }
                                            //   
                                        } catch (final Exception e) {
                                            System.out.println("Sto stampando E"+e);
                                        }
                                        
                            }
                        }
                        //System.out.println("\n\nRESPOMS 0\n\n\n"+freq);
                        return new StringOccurrences(freq);
        
        /*final SearchResponse response = this.client
                .prepareSearch(ElasticSearchInputRepository.DATABASE_NAME).setTypes(ElasticSearchInputRepository.CONTEXT_TWEET_TYPE)
                .setQuery(tweetsInTimeWindowAndFromMonitoringActivity).addFacet(authorFacet)
                .setSize(maxItems).execute().actionGet();

        LOGGER.debug("found '" + response.hits().getTotalHits() + "' hits");
        
        // convert results
        final TermsFacet authorFacets = (TermsFacet) response.facets().facetsAsMap()
                .get(screenNameFacetName);
        final Map<String, Integer> screenNameOccurrences = convertToMap(authorFacets);
        return new StringOccurrences(screenNameOccurrences);*/
        
    }
    
    public StringOccurrences hashTagsOccurrences(final MonitoringActivityId monitoringActivityId,
            final DateTime startDate, final DateTime endDate, final int maxItems) {
    
        // queries
        final QueryBuilder beFromThisMonitoringActivity = buildInQuery(monitoringActivityId);
        final QueryBuilder beInTimeWindow = buildInclusiveRangeQuery("createdAt", startDate,
                endDate);
        
        // AND query
        final QueryBuilder tweetsInTimeWindowAndFromMonitoringActivity = QueryBuilders.boolQuery()
                .must(beFromThisMonitoringActivity).must(beInTimeWindow);
        
        // facet
        final String hashTagsFacetName = "hashtagText";
        final String hashTagsField = "text";
        final TermsFacetBuilder hashTagsFacet = buildFacetOnField(hashTagsFacetName, hashTagsField);
        
        // convert results
        final SearchResponse response = this.client
                .prepareSearch(ElasticSearchInputRepository.DATABASE_NAME)
                .setQuery(tweetsInTimeWindowAndFromMonitoringActivity).addFacet(hashTagsFacet)
                .setSize(maxItems).execute().actionGet();
        
        LOGGER.debug("found '" + response.hits().getTotalHits() + "' hits");
        final TermsFacet hashTagsFacets = (TermsFacet) response.facets().facetsAsMap()
                .get(hashTagsFacetName);
        final Map<String, Integer> hashtags = convertToMap(hashTagsFacets);
        return new StringOccurrences(hashtags);
    }
    
    public StringOccurrences hashTagsOccurrences(User timelineAuthor, DateTime startDate,
            DateTime endDate, int maxHashTags) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public StringOccurrences urlsOccurrences(MonitoringActivityId monitoringActivityId,
            DateTime startDate, DateTime endDate, int maxUrls) {
    
        throw new UnsupportedOperationException("NIY");
    }
    
    public Map<User, Integer> getWeightedUsersMentioning(
            List<MonitoringActivityId> monitoringActivityIds, DateTime startDate, DateTime endDate,
            String screenName) {
    
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }
    
    public Map<User, Integer> getWeightedUsersMentionedBy(
            List<MonitoringActivityId> monitoringActivityIds, DateTime startDate, DateTime endDate,
            String screenName) {
    
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }
    
    private Map<String, Integer> convertToMap(TermsFacet facet) {
    
        LOGGER.debug("building query result into output format");
        final Map<String, Integer> occs = Maps.newHashMap();
        final Iterator<TermsFacet.Entry> facetsIterator = facet.iterator();
        while (facetsIterator.hasNext()) {
            final TermsFacet.Entry entry = facetsIterator.next();
            occs.put(entry.getTerm(), entry.count());
        }
        return occs;
    }
    
    private TermsFacetBuilder buildFacetOnField(String facetName, String fieldName) {
    
        return FacetBuilders.termsFacet(facetName).field(fieldName);
    }
    
    private TermsQueryBuilder buildInQuery(MonitoringActivityId monitoringActivityId) {
    
        return QueryBuilders.inQuery(ElasticSearchInputRepository.MONITORING_ACTIVITY_ID,
                monitoringActivityId.getValue());
    }
    
    private RangeQueryBuilder buildInclusiveRangeQuery(String fieldName, DateTime startDate,
            DateTime endDate) {
    
        return QueryBuilders.rangeQuery(fieldName).from(startDate.getMillis())
                .to(endDate.getMillis()).includeLower(true).includeUpper(true);
    }
    
}
