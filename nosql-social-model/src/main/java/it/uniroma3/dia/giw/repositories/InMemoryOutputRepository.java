package it.uniroma3.dia.giw.repositories;

import it.uniroma3.dia.giw.model.ContextTweet;
import it.uniroma3.dia.giw.model.ContextUser;
import it.uniroma3.dia.giw.model.OutputRepository;
import it.uniroma3.dia.giw.model.StringOccurrences;
import it.uniroma3.dia.giw.model.monitoring.MonitoringActivityId;
import it.uniroma3.dia.giw.model.twitter.data.HashTag;
import it.uniroma3.dia.giw.model.twitter.data.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.google.common.collect.Maps;

public class InMemoryOutputRepository implements OutputRepository {
    
    private static final Logger LOGGER = Logger.getLogger(InMemoryInputRepository.class);
    
    private final Map<MonitoringActivityId, List<ContextTweet>> streams;
    
    private final Map<Long, ContextUser> usersFollowing = Maps.newHashMap();
    
    public InMemoryOutputRepository(Map<MonitoringActivityId, List<ContextTweet>> streams) {
    
        this.streams = streams;
    }
    
    public StringOccurrences authorOccurrences(final MonitoringActivityId monitoringActivityId,
            final DateTime startDate, final DateTime endDate, final int maxAuthors) {
    
        final Map<String, Integer> occurrences = Maps.newHashMap();
        for (ContextTweet contextTweet : streams.get(monitoringActivityId)) {
            
            if (containedInRange(contextTweet.getTweet().getCreatedAt(), startDate, endDate)) {
                String author = contextTweet.getTweet().getUser().getScreenName();
                Integer existingOccurrences = occurrences.get(author);
                if (existingOccurrences == null) {
                    occurrences.put(author, 1);
                    if (occurrences.keySet().size() == maxAuthors) {
                        break;
                    }
                } else {
                    occurrences.put(author, ++existingOccurrences);
                }
                
            }
        }
        
        return new StringOccurrences(occurrences);
    }
    
    private boolean containedInRange(final Date createdAt, final DateTime startDate,
            final DateTime endDate) {
    
        long dateInMillis = createdAt.getTime();
        if (dateInMillis >= startDate.getMillis() && dateInMillis <= endDate.getMillis()) {
            return true;
        }
        return false;
    }
    
    public StringOccurrences hashTagsOccurrences(MonitoringActivityId monitoringActivityId,
            DateTime startDate, DateTime endDate, int maxHashTags) {
    
        final Map<String, Integer> occurrences = Maps.newHashMap();
        for (ContextTweet contextTweet : streams.get(monitoringActivityId)) {
            
            if (containedInRange(contextTweet.getTweet().getCreatedAt(), startDate, endDate)) {
                List<HashTag> hashTags = contextTweet.getTweet().getEntities().hashtags;
                for (HashTag hashTag : hashTags) {
                    
                    Integer existingOccurrences = occurrences.get(hashTag);
                    if (existingOccurrences == null) {
                        occurrences.put(hashTag.getText(), 1);
                        if (occurrences.keySet().size() == maxHashTags) {
                            break;
                        }
                    } else {
                        occurrences.put(hashTag.getText(), ++existingOccurrences);
                    }
                }
            }
        }
        
        return new StringOccurrences(occurrences);
    }
    
    public StringOccurrences urlsOccurrences(MonitoringActivityId monitoringActivityId,
            DateTime startDate, DateTime endDate, int maxUrls) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public Map<User, Integer> getWeightedUsersMentioning(
            List<MonitoringActivityId> monitoringActivityIds, DateTime startDate, DateTime endDate,
            String screenName) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public Map<User, Integer> getWeightedUsersMentionedBy(
            List<MonitoringActivityId> monitoringActivityIds, DateTime startDate, DateTime endDate,
            String screenName) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
    public StringOccurrences hashTagsOccurrences(User timelineAuthor, DateTime startDate,
            DateTime endDate, int maxHashTags) {
    
        // TODO Auto-generated method stub
        return null;
    }
    
}
