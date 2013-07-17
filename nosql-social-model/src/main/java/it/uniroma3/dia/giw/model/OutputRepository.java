package it.uniroma3.dia.giw.model;

import it.uniroma3.dia.giw.model.monitoring.MonitoringActivityId;
import it.uniroma3.dia.giw.model.twitter.data.User;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

public interface OutputRepository {
    
    StringOccurrences authorOccurrences(MonitoringActivityId monitoringActivityId,
            DateTime startDate, DateTime endDate, int maxAuthors);
    
    StringOccurrences hashTagsOccurrences(MonitoringActivityId monitoringActivityId,
            DateTime startDate, DateTime endDate, int maxHashTags);
    
    StringOccurrences hashTagsOccurrences(User timelineAuthor, DateTime startDate,
            DateTime endDate, int maxHashTags);
    
    StringOccurrences urlsOccurrences(MonitoringActivityId monitoringActivityId,
            DateTime startDate, DateTime endDate, int maxUrls);
    
    Map<User, Integer> getWeightedUsersMentioning(List<MonitoringActivityId> monitoringActivityIds,
            DateTime startDate, DateTime endDate, String screenName);
    
    Map<User, Integer> getWeightedUsersMentionedBy(
            List<MonitoringActivityId> monitoringActivityIds, DateTime startDate, DateTime endDate,
            String screenName);
}
