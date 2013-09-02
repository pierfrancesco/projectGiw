package it.uniroma3.dia.giw.repositories.performance;

import it.uniroma3.dia.giw.model.ContextTweet;
import it.uniroma3.dia.giw.model.PageId;
import it.uniroma3.dia.giw.model.RelatedUsersPage;
import it.uniroma3.dia.giw.model.TweetsPage;
import it.uniroma3.dia.giw.model.monitoring.MonitoringActivityId;
import it.uniroma3.dia.giw.repositories.InMemoryInputRepositoryNew;
import it.uniroma3.dia.giw.repositories.InMemoryOutputRepository;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;

@Test
public class InMemoryInputOutputRepositoryPerformanceTestNew extends
        BaseStoreQueryRepositoryPerformanceTest {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(InMemoryInputOutputRepositoryPerformanceTestNew.class);
    
    @Override
    protected Logger getLogger() {
    
        return LOGGER;
    }
    
    @BeforeClass
    @Override
    protected void initRepositories() throws Exception {
    
        final Map<MonitoringActivityId, List<ContextTweet>> streams = Maps.newHashMap();
        final Map<PageId, RelatedUsersPage> followingPages = Maps.newHashMap();
        final Map<PageId, RelatedUsersPage> followerPages = Maps.newHashMap();
        final Map<PageId, TweetsPage> timelinePages = Maps.newHashMap();
        super.writeRepository = new InMemoryInputRepositoryNew(streams, timelinePages, followerPages,
                followingPages);
        super.readRepository = new InMemoryOutputRepository(streams);
    }
    
    @AfterClass
    @Override
    protected void tearDownRepositories() {
    
        // Nothing to do
        
    }
    
}
