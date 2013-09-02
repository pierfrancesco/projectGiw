package it.uniroma3.dia.giw.repositories;

import it.uniroma3.dia.giw.exceptions.InputRepositoryException;
import it.uniroma3.dia.giw.model.ContextTweet;
import it.uniroma3.dia.giw.model.ContextTweetId;
import it.uniroma3.dia.giw.model.InputRepository;
import it.uniroma3.dia.giw.model.PageId;
import it.uniroma3.dia.giw.model.RelatedUsersPage;
import it.uniroma3.dia.giw.model.TimedValue;
import it.uniroma3.dia.giw.model.TweetsPage;
import it.uniroma3.dia.giw.model.monitoring.MonitoringActivityId;
import it.uniroma3.dia.giw.model.twitter.call.TwitterAPIMethod;
import it.uniroma3.dia.giw.model.twitter.data.Tweet;
import it.uniroma3.dia.giw.model.twitter.data.User;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.collect.Maps;
import org.elasticsearch.common.inject.Inject;
import org.joda.time.DateTime;

public class InMemoryInputRepositoryNew implements InputRepository {
    
    private static final Logger LOGGER = Logger.getLogger(InMemoryInputRepository.class);
    private final Map<MonitoringActivityId, List<ContextTweet>> streams;
    private final Map<PageId, RelatedUsersPage> followingPages;
    private final Map<PageId, RelatedUsersPage> followerPages;
    private final Map<PageId, TweetsPage> timelinePages;
    
    @Inject
    public InMemoryInputRepositoryNew(Map<MonitoringActivityId, List<ContextTweet>> streams,
            Map<PageId, TweetsPage> timelinePages, Map<PageId, RelatedUsersPage> followerPages,
            Map<PageId, RelatedUsersPage> followingPages) {
    
        this.streams = streams;
        this.timelinePages = timelinePages;
        this.followerPages = followerPages;
        this.followingPages = followingPages;
    }
    
    public InMemoryInputRepositoryNew() {
    
        this.streams = Maps.newHashMap();
        this.timelinePages = Maps.newHashMap();
        this.followerPages = Maps.newHashMap();
        this.followingPages = Maps.newHashMap();
    }
    
    public void storeToStream(List<Tweet> tweets, MonitoringActivityId monitoringActivityId)
            throws InputRepositoryException {
    
        for (final Tweet tweet : tweets) {
            
            storeToStream(tweet, monitoringActivityId);
        }
        
        LOGGER.info(tweets.size() + " tweets stored in repository");
    }
    
    public ContextTweetId storeToStream(final Tweet tweet,
            final MonitoringActivityId monitoringActivityId) throws InputRepositoryException {
    
        final DateTime creationTimestamp = new DateTime(tweet.getCreatedAt());
        final int dayOfTheYear = creationTimestamp.getDayOfYear();
        final int year = creationTimestamp.getYear();
        
        final String id = UUID.randomUUID().toString();
        final ContextTweetId contextTweetId = new ContextTweetId(id);
        
        final ContextTweet contextTweet = new ContextTweet(contextTweetId, tweet,
                monitoringActivityId.getValue(), year, dayOfTheYear);
        
        List<ContextTweet> existingData = streams.get(monitoringActivityId);
        if (existingData == null) {
            existingData = Lists.newArrayList(contextTweet);
        } else {
            existingData.add(contextTweet);
        }
        streams.put(monitoringActivityId, existingData);
        
        return contextTweetId;
    }
    
    public void removeStream(final MonitoringActivityId monitoringActivityId)
            throws InputRepositoryException {
    
        streams.remove(monitoringActivityId);
    }
    
    public ContextTweet getByIdFromStream(ContextTweetId id) throws InputRepositoryException {
    
        for (List<ContextTweet> currentContextTweets : streams.values()) {
            
            for (ContextTweet contextTweet : currentContextTweets) {
                if (contextTweet.getId().equals(contextTweet.getId())) {
                    return contextTweet;
                }
            }
        }
        return null;
    }
    
    public PageId storeFollowing(final long[] userIds, final User user, final DateTime startedAt,
            final DateTime finishedAt) throws InputRepositoryException {
    
        final String id = UUID.randomUUID().toString();
        final PageId pageId = new PageId(id);
        
        // build a Page
        final RelatedUsersPage page = new RelatedUsersPage(userIds, user, startedAt, finishedAt);
        final RelatedUsersPage insertedFollowingPage = followingPages.put(pageId, page);
        LOGGER.debug(pageId + " following page saved: " + insertedFollowingPage);
        return pageId;
    }
    
    public void removeFollowing(final User user) throws InputRepositoryException {
    
        // remove all pages referring to User following
        removePages(user, this.followingPages.keySet(), this.followingPages);
    }
    
    private void removePages(User user, Set<PageId> followingPageids,
            Map<PageId, RelatedUsersPage> pages) {
    
        for (PageId pageId : followingPageids) {
            final RelatedUsersPage followingPage = pages.get(pageId);
            
            if (followingPage.getUser().equals(user)) {
                pages.remove(pageId);
            }
        }
    }
    
    private TimedValue countUsersAndLeastUpdatedTime(String screenName,
            final Map<PageId, RelatedUsersPage> pages) {
    
        int usersAmount = 0;
        DateTime leastUpdatedTime = new DateTime();
        
        for (PageId pageId : pages.keySet()) {
            
            final RelatedUsersPage relatedUsersPage = pages.get(pageId);
            DateTime currentDateTime = relatedUsersPage.getFinishedDownloadingAt();
            if (currentDateTime.isBefore(leastUpdatedTime)) {
                leastUpdatedTime = currentDateTime;
            }
            if (relatedUsersPage.getUser().getScreenName().equals(screenName)) {
                usersAmount += relatedUsersPage.getRelatedUserIds().length;
            }
        }
        if (usersAmount == 0) {
            return new TimedValue(0, new DateTime(0));
        } else {
            return new TimedValue(usersAmount, leastUpdatedTime);
        }
    }
    
    private TimedValue countTweetsAndLeastUpdatedTime(String screenName,
            final Map<PageId, TweetsPage> pages) {
    
        int tweetsAmount = 0;
        DateTime leastUpdatedTime = new DateTime();
        
        final Set<PageId> pageIds = pages.keySet();
        for (PageId pageId : pageIds) {
            final TweetsPage tweetsPage = pages.get(pageId);
            DateTime currentDateTime = tweetsPage.getFinishedDownloadingAt();
            if (currentDateTime.isBefore(leastUpdatedTime)) {
                leastUpdatedTime = currentDateTime;
            }
            if (tweetsPage.getUser().getScreenName().equals(screenName)) {
                tweetsAmount += tweetsPage.getTimelineTweets().size();
            }
        }
        if (tweetsAmount == 0) {
            return new TimedValue(0, new DateTime(0));
        } else {
            return new TimedValue(tweetsAmount, leastUpdatedTime);
        }
    }
    
    private TimedValue countUsersAndLeastUpdatedTime(long userId,
            final Map<PageId, RelatedUsersPage> pages) {
    
        int usersAmount = 0;
        DateTime leastUpdatedTime = new DateTime();
        
        for (PageId pageId : pages.keySet()) {
            
            final RelatedUsersPage relatedUsersPage = pages.get(pageId);
            DateTime currentDateTime = relatedUsersPage.getFinishedDownloadingAt();
            if (currentDateTime.isBefore(leastUpdatedTime)) {
                leastUpdatedTime = currentDateTime;
            }
            if (relatedUsersPage.getUser().getId() == userId) {
                usersAmount += relatedUsersPage.getRelatedUserIds().length;
            }
        }
        if (usersAmount == 0) {
            return new TimedValue(0, new DateTime(0));
        } else {
            return new TimedValue(usersAmount, leastUpdatedTime);
        }
    }
    
    private TimedValue countTweetsAndLeastUpdatedTime(long userId,
            final Map<PageId, TweetsPage> pages) {
    
        int tweetsAmount = 0;
        DateTime leastUpdatedTime = new DateTime();
        
        final Set<PageId> pageIds = pages.keySet();
        for (PageId pageId : pageIds) {
            final TweetsPage tweetsPage = pages.get(pageId);
            DateTime currentDateTime = tweetsPage.getFinishedDownloadingAt();
            if (currentDateTime.isBefore(leastUpdatedTime)) {
                leastUpdatedTime = currentDateTime;
            }
            if (tweetsPage.getUser().getId() == userId) {
                tweetsAmount += tweetsPage.getTimelineTweets().size();
            }
        }
        if (tweetsAmount == 0) {
            return new TimedValue(0, new DateTime(0));
        } else {
            return new TimedValue(tweetsAmount, leastUpdatedTime);
        }
    }
    
    public PageId storeFollowers(final long[] userIds, final User user,
            final DateTime startedDownloadingAt, final DateTime finishedDownloadingAt)
            throws InputRepositoryException {
    
        final String id = UUID.randomUUID().toString();
        final PageId pageId = new PageId(id);
        
        // build a Page
        final RelatedUsersPage page = new RelatedUsersPage(userIds, user, startedDownloadingAt,
                finishedDownloadingAt);
        RelatedUsersPage insertedPage = followerPages.put(pageId, page);
        LOGGER.debug(pageId + " follower page saved: " + insertedPage);
        return pageId;
        
    }
    
    public void removeFollowers(User user) throws InputRepositoryException {
    
    }
    
    public PageId storeTimeline(List<Tweet> tweets, User user, DateTime startTime, DateTime endTime)
            throws InputRepositoryException {
    
        final String id = UUID.randomUUID().toString();
        final PageId pageId = new PageId(id);
        
        // build a Page
        final TweetsPage page = new TweetsPage(tweets, user, startTime, endTime);
        final TweetsPage insertedTimelinePage = timelinePages.put(pageId, page);
        LOGGER.debug(pageId + " timeline page saved: " + insertedTimelinePage);
        return pageId;
    }
    
    public void removeTimeline(User user) throws InputRepositoryException {
    
        // TODO Auto-generated method stub
        
    }
    
    public void shutDownRepository() {
    
    }
    
    public long[] getFollowing(String screenName) {
    
        return getUserIds(screenName, this.followingPages);
    }
    
    public long[] getFollowers(String screenName) {
    
        return getUserIds(screenName, this.followerPages);
    }
    
    private long[] getUserIds(String screenName, Map<PageId, RelatedUsersPage> pages) {
    
        long[] userIds = new long[0];
        
        for (PageId pageId : pages.keySet()) {
            
            final RelatedUsersPage relatedUsersPage = pages.get(pageId);
            
            if (relatedUsersPage.getUser().getScreenName().equals(screenName)) {
                
                userIds = ArrayUtils.addAll(userIds, relatedUsersPage.getRelatedUserIds());
            }
        }
        
        return userIds;
    }
    
    public TimedValue getTimedCount(long userId, TwitterAPIMethod method) {
    
        if (method.equals(TwitterAPIMethod.GET_FOLLOWERS)) {
            return countUsersAndLeastUpdatedTime(userId, this.followerPages);
        } else if (method.equals(TwitterAPIMethod.GET_FOLLOWINGS)) {
            return countUsersAndLeastUpdatedTime(userId, this.followingPages);
        } else if (method.equals(TwitterAPIMethod.GET_USER_TIMELINE)) {
            return countTweetsAndLeastUpdatedTime(userId, this.timelinePages);
        } else {
            throw new UnsupportedOperationException(method + " not allowed");
        }
    }
    
    public TimedValue getTimedCount(String screenName, TwitterAPIMethod method) {
    
        if (method.equals(TwitterAPIMethod.GET_FOLLOWERS)) {
            return countUsersAndLeastUpdatedTime(screenName, this.followerPages);
        } else if (method.equals(TwitterAPIMethod.GET_FOLLOWINGS)) {
            return countUsersAndLeastUpdatedTime(screenName, this.followingPages);
        } else if (method.equals(TwitterAPIMethod.GET_USER_TIMELINE)) {
            return countTweetsAndLeastUpdatedTime(screenName, this.timelinePages);
        } else {
            throw new UnsupportedOperationException(method + " not allowed");
        }
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
