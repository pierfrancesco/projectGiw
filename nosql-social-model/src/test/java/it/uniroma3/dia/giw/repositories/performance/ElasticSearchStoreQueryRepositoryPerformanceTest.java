package it.uniroma3.dia.giw.repositories.performance;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import it.uniroma3.dia.giw.repositories.ElasticSearchInputRepository;
import it.uniroma3.dia.giw.repositories.ElasticSearchOutputRepository;

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
import org.testng.annotations.Test;

@Test
public class ElasticSearchStoreQueryRepositoryPerformanceTest extends
        BaseStoreQueryRepositoryPerformanceTest {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ElasticSearchStoreQueryRepositoryPerformanceTest.class);
    
    private static final String ELASTIC_SEARCH_DIR = "target/elasticsearch";
    
    private Node localNode;
    
    private Node localClientNode;
    
    @Override
    protected Logger getLogger() {
    
        return LOGGER;
    }
    
    @BeforeClass
    @Override
    protected void initRepositories() throws Exception {
    
        // start nodes with custom configuration
        final String elasticSearchPath = "./" + ELASTIC_SEARCH_DIR + "/";
        
        final String dataPath = elasticSearchPath + "data";
        final String clusterName = "es";
        
        if (this.localNode == null) {
            
            final File dataDir = new File(dataPath);
            
            // First, we delete old datas...
            if (dataDir.exists()) {
                FileSystemUtils.deleteRecursively(dataDir, true);
            }
            /*
             * String clusterPath = dataPath + "/" + clusterName; final File
             * clusterDir = new File(clusterPath); clusterDir.mkdirs();
             * Runtime.getRuntime().exec("chmod a+w " + clusterPath);
             * clusterDir.setWritable(true, false);
             */
            
            // Then, we start 2 nodes for tests
            // holds data
            startLocalNode(buildSettings(elasticSearchPath, dataPath, clusterName));
            // is just a client
            startLocalClientNode(buildSettings(elasticSearchPath, dataPath, clusterName));
            // We clean existing indices
            try {
                this.localNode.client().admin().indices().delete(new DeleteIndexRequest("_river"))
                        .actionGet();
                // We wait for one second to let ES delete the river
                Thread.sleep(1000);
            } catch (IndexMissingException e) {
                // Index does not exist... Fine
            }
            
            // We build the indices
            final CreateIndexRequest tweetsIndex = Requests
                    .createIndexRequest(ElasticSearchInputRepository.DATABASE_NAME);
            this.localNode.client().admin().indices().create(tweetsIndex).actionGet();
            Thread.sleep(1000);
            
            // If a tweetMapping is defined, we will use it
            LOGGER.info("mapping: " + tweetMapping().string());
            this.localNode.client().admin().indices()
                    .preparePutMapping(ElasticSearchInputRepository.DATABASE_NAME)
                    .setType(ElasticSearchInputRepository.CONTEXT_TWEET_TYPE)
                    .setSource(tweetMapping()).execute().actionGet();
        }
        
        final ObjectMapper objectMapper = new ObjectMapper();
        super.writeRepository = new ElasticSearchInputRepository(localClientNode, objectMapper);
        super.readRepository = new ElasticSearchOutputRepository(localClientNode, objectMapper);
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
    
    public XContentBuilder tweetMapping() throws Exception {
    
        // mapping for type tweet
        XContentBuilder tweetMapping = jsonBuilder()
                .startObject()
                .startObject(ElasticSearchInputRepository.CONTEXT_TWEET_TYPE)
                .startObject("properties")
                .startObject(ElasticSearchInputRepository.TWEET_FIELD)
                .startObject("properties")
                .startObject(ElasticSearchInputRepository.USER_FIELD)
                .startObject("properties")
                .startObject("screenName")
                .field("type", "string")
                .field("index", "not_analyzed")
                .endObject()
                // screenName
                .endObject()
                // user_field
                .endObject()
                // properties
                .endObject()
                // tweet_field
                .endObject()
                // properties
                .startObject(ElasticSearchInputRepository.CONTEXT_TWEET_ID)
                .startObject("properties").startObject("id").field("type", "string").endObject()
                .endObject().endObject().startObject("year").field("type", "integer").endObject()
                .startObject("dayOfTheYear").field("type", "integer").endObject()
                .startObject(ElasticSearchInputRepository.MONITORING_ACTIVITY_ID)
                .field("type", "string").endObject().endObject() // context_tweet_type
                .endObject(); // enclosing object
        return tweetMapping;
    }
    
    private Settings buildSettings(String elasticSearchPath, String dataPath, String clusterName) {
    
        return ImmutableSettings.settingsBuilder().put("cluster.name", clusterName)
                .put("path.data", dataPath).put("path.logs", elasticSearchPath + "logs")
                .put("path.work", elasticSearchPath + "work").build();
    }
    
    @AfterClass
    @Override
    protected void tearDownRepositories() {
    
        this.localClientNode.close();
        this.localNode.close();
        
    }
    
}
