package it.uniroma3.dia.giw.repositories;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.testng.Assert.assertNotNull;

import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ElasticSearchInputOutputRepositoryIntegrationTestCase extends
        BaseInputOutputRepositoryIntegrationTestCase {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ElasticSearchInputOutputRepositoryIntegrationTestCase.class);
    
    private Node localNode;
    
    private Node localClientNode;
    
    @Override
    @BeforeClass
    protected void initRepositories() throws Exception {
    
        // start nodes with custom configuration
        final String elasticSearchPath = "./target/es/";
        final String dataPath = elasticSearchPath + "data";
        final String clusterName = "es";
        
        if (this.localNode == null) {
            // First, we delete old datas...
            final File dataDir = new File(dataPath);
            if (dataDir.exists()) {
                FileSystemUtils.deleteRecursively(dataDir, true);
            }
            
            // Then, we start 2 nodes for tests
            // holds data
            startLocalNode(buildSettings(elasticSearchPath, dataPath, clusterName));
            // is just a client
            startLocalClientNode(buildSettings(elasticSearchPath, dataPath, clusterName));
        }
        
        final ObjectMapper objectMapper = new ObjectMapper();
        super.writeRepository = new ElasticSearchInputRepository(localClientNode, objectMapper);
        super.readRepository = new ElasticSearchOutputRepository(localClientNode, objectMapper);
    }
    
    @Override
    @AfterClass
    public void tearDownRepositories() {
    
        this.localClientNode.close();
        this.localNode.close();
    }
    
    @Override
    @BeforeMethod
    protected void cleanIndices() throws Exception {
    
        LOGGER.debug("cleaning all indices");
        
        // We clean localNode existing indices
        try {
            this.localNode.client().admin().indices()
                    .delete(new DeleteIndexRequest(ElasticSearchInputRepository.DATABASE_NAME))
                    .actionGet();
            // We wait for one second to let ES delete
            Thread.sleep(1000);
            
        } catch (IndexMissingException e) {
            // Index does not exist... Fine
        }
        
        // We create the indices
        final CreateIndexRequest tweetsIndex = Requests
                .createIndexRequest(ElasticSearchInputRepository.DATABASE_NAME);
        
        this.localNode.client().admin().indices().create(tweetsIndex).actionGet();
        
        Thread.sleep(1000);
        
        // If a tweetMapping is defined, we will use it
        LOGGER.info("mapping: " + tweetMapping().string());
        this.localNode.client().admin().indices()
                .preparePutMapping(ElasticSearchInputRepository.DATABASE_NAME)
                .setType(ElasticSearchInputRepository.CONTEXT_TWEET_TYPE).setSource(tweetMapping())
                .execute().actionGet();
    }
    
    public XContentBuilder tweetMapping() throws Exception {
    
        // mapping for type tweet
        XContentBuilder tweetMapping = jsonBuilder()
                .startObject()
                .startObject(ElasticSearchInputRepository.CONTEXT_TWEET_TYPE)
                .startObject("properties")
                .startObject(ElasticSearchInputRepository.TWEET_FIELD)
                .startObject("properties")
                .startObject("id")
                .field("type", "long")
                .field("index", "not_analyzed")
                .endObject()
                // tweet id
                .startObject("createdAt")
                .field("type", "long")
                .field("index", "not_analyzed")
                .endObject()
                // createdAt
                .startObject(ElasticSearchInputRepository.USER_FIELD)
                .startObject("properties")
                .startObject("screenName")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()
                // screenName
                .endObject()
                // user properties
                .endObject()
                // user
                .startObject("entities")
                .startObject("properties")
                .startObject("hashtags")
                .startObject("properties")
                .startObject("text")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()
                // text
                .endObject()
                // properties
                .endObject()
                // hashtags
                .endObject()
                // properties
                .endObject()
                // entities
                .endObject()
                // tweet_field
                .endObject()
                // properties
                .startObject(ElasticSearchInputRepository.CONTEXT_TWEET_ID)
                .startObject("properties").startObject("id").field("type", "string").endObject()
                .endObject().endObject().startObject("year").field("type", "integer").endObject()
                .startObject("dayOfTheYear").field("type", "integer").endObject()
                .startObject(ElasticSearchInputRepository.MONITORING_ACTIVITY_ID)
                .field("type", "string").field("index", "not_analyzed").endObject().endObject() // context_tweet_type
                .endObject(); // enclosing object
        return tweetMapping;
    }
    
    private void startLocalNode(Settings immutableSettings) {
    
        this.localNode = NodeBuilder.nodeBuilder().settings(immutableSettings).local(true).node();
        
        // We wait now for the yellow (or green) status
        this.localNode.client().admin().cluster().prepareHealth().setWaitForYellowStatus()
                .execute().actionGet();
    }
    
    private void startLocalClientNode(Settings immutableSettings) {
    
        this.localClientNode = NodeBuilder.nodeBuilder().settings(immutableSettings).local(true)
                .client(true).node();
        
        // We wait now for the yellow (or green) status
        this.localClientNode.client().admin().cluster().prepareHealth().setWaitForYellowStatus()
                .execute().actionGet();
    }
    
    private Settings buildSettings(String elasticSearchPath, String dataPath, String clusterName) {
    
        return ImmutableSettings.settingsBuilder().put("cluster.name", clusterName)
                .put("path.data", dataPath).put("path.logs", elasticSearchPath + "logs")
                .put("path.work", elasticSearchPath + "work").build();
    }
    
    @Test
    public void shouldBeGreen() {
    
        assertNotNull(super.writeRepository);
        assertNotNull(super.readRepository);
    }
    
    @Override
    protected Logger getLogger() {
    
        return LOGGER;
    }
}
