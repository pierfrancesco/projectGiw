package it.uniroma3.dia.giw.model;

import it.uniroma3.dia.giw.exceptions.InputRepositoryException;
import it.uniroma3.dia.giw.model.monitoring.MonitoringActivityId;
import it.uniroma3.dia.giw.model.twitter.call.TwitterAPIMethod;
import it.uniroma3.dia.giw.model.twitter.data.Tweet;
import it.uniroma3.dia.giw.model.twitter.data.User;

import java.util.List;

import org.joda.time.DateTime;

public interface InputRepository {
    
    // Tweets
    void storeToStream(List<Tweet> tweets, MonitoringActivityId monitoringActivityId)
            throws InputRepositoryException;
    
    ContextTweetId storeToStream(Tweet tweet, MonitoringActivityId monitoringActivityId)
            throws InputRepositoryException;
    
    void removeStream(final MonitoringActivityId monitoringActivityId)
            throws InputRepositoryException;
    
    ContextTweet getByIdFromStream(ContextTweetId id) throws InputRepositoryException;
    
    // Following "pages" documents
    PageId storeFollowing(long[] followingIds, User user, DateTime startedAt, DateTime finishedAt)
            throws InputRepositoryException;
    
    void removeFollowing(User user) throws InputRepositoryException;
    
    // Followers "pages" documents
    PageId storeFollowers(long[] followerIds, User user, DateTime startTime, DateTime endTime)
            throws InputRepositoryException;
    
    void removeFollowers(User user) throws InputRepositoryException;
    
    // Timeline "pages" documents
    PageId storeTimeline(List<Tweet> tweets, User user, DateTime startTime, DateTime endTime)
            throws InputRepositoryException;
    
    void removeTimeline(User user) throws InputRepositoryException;
    
    void shutDownRepository();
    
    long[] getFollowers(String screenName);
    
    long[] getFollowing(String screenName);
    
    TimedValue getTimedCount(String screenName, TwitterAPIMethod externalMethod);
    
    TimedValue getTimedCount(long userId, TwitterAPIMethod externalMethod);
    
    int getPagesAmount(long userId, TwitterAPIMethod method);
    
    int getPagesAmount(String screenName, TwitterAPIMethod method);
    
}
