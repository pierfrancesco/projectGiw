package it.uniroma3.dia.giw.repositories;

import it.uniroma3.dia.giw.model.OutputRepository;
import it.uniroma3.dia.giw.model.StringOccurrences;
import it.uniroma3.dia.giw.model.monitoring.MonitoringActivityId;
import it.uniroma3.dia.giw.model.twitter.data.User;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        System.out.println("QUESTO è il monitor"+startDate+"\n"+endDate);


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
        final String screenNameField2 = "author";
        final TermsFacetBuilder authorFacet = buildFacetOnField(screenNameFacetName,
                screenNameField);
        final TermsFacetBuilder authorFacet2 = buildFacetOnField(screenNameFacetName,
                screenNameField2);

    final SearchResponse response0 = this.client
                .prepareSearch(ElasticSearchInputRepository.DATABASE_NAME).setTypes("occurences")
                .setQuery(beInTimeWindow)
                .setSize(maxItems).execute().actionGet();
                TermsFacet f = (TermsFacet) response0.facets().facetsAsMap().get("screenName");
                System.out.println("\n\nRESPOMS 0\n\n\n"+response0);
        
        final SearchResponse response = this.client
                .prepareSearch(ElasticSearchInputRepository.DATABASE_NAME).setTypes(ElasticSearchInputRepository.CONTEXT_TWEET_TYPE)
                .setQuery(tweetsInTimeWindowAndFromMonitoringActivity).addFacet(authorFacet)
                .setSize(maxItems).execute().actionGet();

        /*final SearchResponse response = this.client
                .prepareSearch(ElasticSearchInputRepository.DATABASE_NAME)
                .setSize(maxItems).execute().actionGet();*/
        //System.out.println("QUESTO"+response);
        LOGGER.debug("found '" + response.hits().getTotalHits() + "' hits");
        
        // convert results
        final TermsFacet authorFacets = (TermsFacet) response.facets().facetsAsMap()
                .get(screenNameFacetName);
        final Map<String, Integer> screenNameOccurrences = convertToMap(authorFacets);
        return new StringOccurrences(screenNameOccurrences);
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
