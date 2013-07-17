package it.uniroma3.dia.giw.repositories;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import it.uniroma3.dia.giw.exceptions.InputRepositoryException;
import it.uniroma3.dia.giw.model.ContextTweet;
import it.uniroma3.dia.giw.model.ContextTweetId;
import it.uniroma3.dia.giw.model.InputRepository;
import it.uniroma3.dia.giw.model.OutputRepository;
import it.uniroma3.dia.giw.model.StringOccurrences;
import it.uniroma3.dia.giw.model.monitoring.MonitoringActivityId;
import it.uniroma3.dia.giw.model.twitter.data.Entities;
import it.uniroma3.dia.giw.model.twitter.data.HashTag;
import it.uniroma3.dia.giw.model.twitter.data.Tweet;
import it.uniroma3.dia.giw.model.twitter.data.Url;
import it.uniroma3.dia.giw.model.twitter.data.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.testng.annotations.Test;

public abstract class BaseInputOutputRepositoryIntegrationTestCase {
    
    protected InputRepository writeRepository;
    
    protected OutputRepository readRepository;
    
    protected List<String> savedMonitoringActivities = new ArrayList<String>();
    
    protected abstract Logger getLogger();
    
    protected abstract void initRepositories() throws Exception;
    
    protected abstract void tearDownRepositories();
    
    protected abstract void cleanIndices() throws Exception;
    
    @Test(enabled = true)
    public void shouldCRDSomeTweets() throws InputRepositoryException {
    
        final MonitoringActivityId monitoringActivityId = new MonitoringActivityId("123");
        final Tweet tweet = new Tweet();
        
        final long firstId = 1;
        tweet.setId(firstId);
        String expectedTweetText = "Example tweet text";
        tweet.setText(expectedTweetText);
        tweet.setCreatedAt(new Date(new DateTime(0).getMillis()));
        tweet.setSource("Example source");
        User obama = new User(2L);
        obama.setName("Barack Obama");
        obama.setScreenName("BarackObama");
        
        tweet.setUser(obama);
        
        getLogger().info(
                "Tweet to store: " + tweet + "\n For monitoring activity: " + monitoringActivityId);
        
        final ContextTweetId savedTweetId = this.writeRepository.storeToStream(tweet,
                monitoringActivityId);
        
        ContextTweetId contextTweetId = savedTweetId;
        ContextTweet storedTweet = this.writeRepository.getByIdFromStream(contextTweetId);
        assertNotNull(storedTweet);
        assertEquals(storedTweet.getTweet().getText(), expectedTweetText);
        
        this.writeRepository.removeStream(monitoringActivityId);
        final ContextTweet deletedTweet = this.writeRepository.getByIdFromStream(contextTweetId);
        assertNull(deletedTweet);
    }
    
    @Test(enabled = true)
    public void shouldStoreStreamAndThenQuery() throws InputRepositoryException {
    
        // screenNames
        final String barackScreenName = "Barack";
        final String michelleScreenName = "Michelle";
        
        final User barack = new User();
        barack.setScreenName(barackScreenName);
        
        final User michelle = new User();
        michelle.setScreenName(michelleScreenName);
        
        final Tweet firstTweet = new Tweet();
        firstTweet.setUser(barack);
        final DateTime now = new DateTime();
        final DateTime yesterday = now.minusDays(1);
        firstTweet.setCreatedAt(new Date(now.getMillis()));
        
        final Tweet secondTweet = new Tweet();
        secondTweet.setUser(michelle);
        secondTweet.setCreatedAt(new Date(now.getMillis() - 1000));
        
        final List<Tweet> tweets = new ArrayList<Tweet>();
        
        tweets.add(firstTweet);
        tweets.add(secondTweet);
        
        final MonitoringActivityId monitoringActivityId = new MonitoringActivityId("123");
        this.writeRepository.storeToStream(tweets, monitoringActivityId);
        
        final int two = 2;
        final StringOccurrences authorOccurrences = this.readRepository.authorOccurrences(
                monitoringActivityId, yesterday, now, two);
        
        final Map<String, Integer> occurrences = authorOccurrences.getOccurrences();
        assertTrue(occurrences.containsKey(barackScreenName));
        assertTrue(occurrences.containsKey(michelleScreenName));
        
        assertTrue(occurrences.keySet().size() <= two);
        assertEquals(occurrences.get(barackScreenName), new Integer(1));
        assertEquals(occurrences.get(michelleScreenName), new Integer(1));
    }
    
    @Test
    public void shouldSaveTweetToTwoMonitoringActivityIds() throws InputRepositoryException {
    
        User a = new User(123L);
        a.setScreenName("a");
        // a makes 2 tweets
        
        final Tweet one = new Tweet();
        Date now = new Date();
        one.setId(1L);
        one.setUser(a);
        one.setCreatedAt(now);
        one.setText("the first tweet by a");
        
        final Tweet two = new Tweet();
        two.setId(2L);
        two.setUser(a);
        two.setCreatedAt(now);
        two.setText("the second tweet by a");
        
        User b = new User(456L);
        b.setScreenName("b");
        // b makes 1 tweet
        
        final Tweet three = new Tweet();
        three.setId(3L);
        three.setUser(b);
        three.setCreatedAt(now);
        three.setText("the third tweet, by b");
        
        MonitoringActivityId firstMA = new MonitoringActivityId("1");
        MonitoringActivityId secondMA = new MonitoringActivityId("2");
        
        // write one AND two to 1
        final List<Tweet> firstTweetList = new ArrayList<Tweet>();
        firstTweetList.add(one);
        firstTweetList.add(two);
        this.writeRepository.storeToStream(firstTweetList, firstMA);
        
        // write three AND one to 2
        List<Tweet> secondTweetList = new ArrayList<Tweet>();
        secondTweetList.add(one);
        secondTweetList.add(three);
        this.writeRepository.storeToStream(secondTweetList, secondMA);
        
        // do the query
        final DateTime fromSeventies = new DateTime(0L);
        final DateTime reallyNow = new DateTime();
        int maxTwo = 2;
        
        // first
        final StringOccurrences screenNamesFirst = this.readRepository.authorOccurrences(firstMA,
                fromSeventies, reallyNow, maxTwo);
        Map<String, Integer> firstOccurrences = screenNamesFirst.getOccurrences();
        assertEquals(firstOccurrences.get("a"), new Integer(2));
        
        // second
        final List<MonitoringActivityId> theSecondMa = new ArrayList<MonitoringActivityId>();
        theSecondMa.add(secondMA);
        
        final StringOccurrences screenNamesSecond = this.readRepository.authorOccurrences(secondMA,
                fromSeventies, reallyNow, maxTwo);
        Map<String, Integer> secondOccurrences = screenNamesSecond.getOccurrences();
        assertEquals(secondOccurrences.get("b"), new Integer(1));
    }
    
    @Test
    public void shouldCountHashTagsOccurrences() throws InputRepositoryException {
    
        // start load test dataset
        TestDataSet testDataSet = new TestDataSet().invoke();
        Tweet googleVsMicrosoft = testDataSet.getGoogleVsMicrosoft();
        Tweet microsoftVsApple = testDataSet.getMicrosoftVsApple();
        Tweet differentTweet = testDataSet.getDifferentTweet();
        MonitoringActivityId monitoringActivityId = testDataSet.getMonitoringActivityId();
        MonitoringActivityId otherMonitoringActivityId = testDataSet.getOtherMonitoringActivityId();
        Date yesterday = testDataSet.getYesterday();
        Date now = testDataSet.getNow();
        // end load test dataset
        
        // start prepare data
        List<Tweet> theTweets = new ArrayList<Tweet>();
        theTweets.add(googleVsMicrosoft);
        theTweets.add(microsoftVsApple);
        // tweet with different hashtags
        theTweets.add(differentTweet);
        // end prepare data
        
        // store
        this.writeRepository.storeToStream(theTweets, monitoringActivityId);
        
        // store _same_ tweets to other monitoring activity
        this.writeRepository.storeToStream(theTweets, otherMonitoringActivityId);
        
        // query
        int maxTwo = 2;
        DateTime startDate = new DateTime(yesterday);
        DateTime endDate = new DateTime(now);
        StringOccurrences hashTagsOccurrences = this.readRepository.hashTagsOccurrences(
                monitoringActivityId, startDate, endDate, maxTwo);
        assertNotNull(hashTagsOccurrences.getOccurrences());
        assertEquals(hashTagsOccurrences.getOccurrences().get("google"), new Integer(1));
        assertEquals(hashTagsOccurrences.getOccurrences().get("microsoft"), new Integer(2));
        assertEquals(hashTagsOccurrences.getOccurrences().get("apple"), new Integer(1));
    }
    
    @Test
    public void shouldCountUrlsOccurrences() throws InputRepositoryException {
    
        // TODO
        
        // start load test dataset
        TestDataSet testDataSet = new TestDataSet().invoke();
        Tweet googleVsMicrosoft = testDataSet.getGoogleVsMicrosoft();
        Tweet microsoftVsApple = testDataSet.getMicrosoftVsApple();
        Tweet differentTweet = testDataSet.getDifferentTweet();
        MonitoringActivityId monitoringActivityId = testDataSet.getMonitoringActivityId();
        MonitoringActivityId otherMonitoringActivityId = testDataSet.getOtherMonitoringActivityId();
        Date yesterday = testDataSet.getYesterday();
        Date now = testDataSet.getNow();
        // end load test dataset
        
        // start prepare data
        List<Tweet> theTweets = new ArrayList<Tweet>();
        theTweets.add(googleVsMicrosoft);
        theTweets.add(microsoftVsApple);
        // tweet with different hashtags
        theTweets.add(differentTweet);
        // end prepare data
        
        // store
        this.writeRepository.storeToStream(theTweets, monitoringActivityId);
        
        // store _same_ tweets to other monitoring activity
        this.writeRepository.storeToStream(theTweets, otherMonitoringActivityId);
        
        // query
        int maxTwo = 2;
        DateTime startDate = new DateTime(yesterday);
        DateTime endDate = new DateTime(now);
        try {
            StringOccurrences hashTagsOccurrences = this.readRepository.urlsOccurrences(
                    monitoringActivityId, startDate, endDate, maxTwo);
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
        
        // assertNotNull(hashTagsOccurrences.getOccurrences());
        // assertEquals(hashTagsOccurrences.getOccurrences().get("google"), new
        // Integer(1));
        // assertEquals(hashTagsOccurrences.getOccurrences().get("microsoft"),
        // new Integer(2));
        // assertEquals(hashTagsOccurrences.getOccurrences().get("apple"), new
        // Integer(1));
        
    }
    
    private class TestDataSet {
        
        private Date now;
        private Date yesterday;
        private Tweet googleVsMicrosoft;
        private Tweet microsoftVsApple;
        private Tweet differentTweet;
        private MonitoringActivityId monitoringActivityId;
        private MonitoringActivityId otherMonitoringActivityId;
        
        public Date getNow() {
        
            return now;
        }
        
        public Date getYesterday() {
        
            return yesterday;
        }
        
        public Tweet getGoogleVsMicrosoft() {
        
            return googleVsMicrosoft;
        }
        
        public Tweet getMicrosoftVsApple() {
        
            return microsoftVsApple;
        }
        
        public Tweet getDifferentTweet() {
        
            return differentTweet;
        }
        
        public MonitoringActivityId getMonitoringActivityId() {
        
            return monitoringActivityId;
        }
        
        public MonitoringActivityId getOtherMonitoringActivityId() {
        
            return otherMonitoringActivityId;
        }
        
        public TestDataSet invoke() {
        
            final HashTag google = new HashTag(new int[] { 1, 2 }, "google");
            final HashTag microsoft = new HashTag(new int[] { 3, 4 }, "microsoft");
            final HashTag apple = new HashTag(new int[] { 5, 6 }, "apple");
            final HashTag anythingElse = new HashTag(new int[] { 7, 8 }, "anythingelse");
            
            final Url googleUrl = new Url.Urls("http://google.com/expanded").foundAt(1, 2)
                    .withDisplay("http://google.com/display").withUrl("http://google.com/url")
                    .build();
            final Url microsoftUrl = new Url.Urls("http://microsoft.com/expanded").foundAt(3, 4)
                    .withDisplay("http://microsoft.com/display")
                    .withUrl("http://microsoft.com/url").build();
            
            final Url appleUrl = new Url.Urls("http://apple.com/expanded").foundAt(5, 6)
                    .withDisplay("http://apple.com/display").withUrl("http://apple.com/url")
                    .build();
            
            Url someUrl = new Url.Urls("http://something.org/expanded").foundAt(7, 8)
                    .withDisplay("http://something.org/display")
                    .withUrl("http://something.org/url").build();
            
            final Entities twoHashTagsAndUrls = new Entities();
            // hashtags
            twoHashTagsAndUrls.addHashTag(google);
            twoHashTagsAndUrls.addHashTag(microsoft);
            
            // urls
            twoHashTagsAndUrls.addUrl(googleUrl);
            twoHashTagsAndUrls.addUrl(microsoftUrl);
            
            final Entities otherTwoHashTagsAndUrls = new Entities();
            
            // hashtags
            otherTwoHashTagsAndUrls.addHashTag(microsoft);
            otherTwoHashTagsAndUrls.addHashTag(apple);
            
            // urls
            otherTwoHashTagsAndUrls.addUrl(microsoftUrl);
            otherTwoHashTagsAndUrls.addUrl(appleUrl);
            
            final Entities notRelevantHashTagsAndUrls = new Entities();
            notRelevantHashTagsAndUrls.addHashTag(anythingElse);
            
            notRelevantHashTagsAndUrls.addUrl(someUrl);
            
            final int oneDayMsec = 24 * 60 * 60 * 1000;
            
            final User aUser = new User.Users(1L).withScreenName("aUser").build();
            now = new Date();
            yesterday = new Date(now.getTime() - oneDayMsec);
            
            googleVsMicrosoft = new Tweet.Tweets(1L)
                    .withEntities(twoHashTagsAndUrls)
                    .authoredBy(aUser)
                    .containingText(
                            "this is #google vs #microsoft topic - http://google.com/display - http://microsoft.com/display")
                    .createdAt(yesterday).build();
            
            microsoftVsApple = new Tweet.Tweets(2L)
                    .withEntities(otherTwoHashTagsAndUrls)
                    .authoredBy(aUser)
                    .containingText(
                            "this is #microsoft vs #apple topic - http://microsoft.com/display - http://apple.com/display")
                    .createdAt(now).build();
            
            differentTweet = new Tweet.Tweets(3L).withEntities(notRelevantHashTagsAndUrls)
                    .authoredBy(aUser).containingText("some text not really important")
                    .createdAt(now).build();
            
            monitoringActivityId = new MonitoringActivityId("123");
            otherMonitoringActivityId = new MonitoringActivityId("456");
            return this;
        }
    }
}
