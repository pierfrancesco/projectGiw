package it.uniroma3.dia.giw.model;

import it.uniroma3.dia.giw.model.twitter.data.Tweet;
import it.uniroma3.dia.giw.model.twitter.data.User;

import java.util.List;

import org.joda.time.DateTime;

public class TweetsPage {
    
    private final List<Tweet> timelineTweets;
    private final User user;
    private final DateTime startedDownloadingAt;
    private final DateTime finishedDownloadingAt;
    
    public TweetsPage(List<Tweet> timelineTweets, User user, DateTime startedDownloadingAt,
            DateTime finishedDownloadingAt) {
    
        this.timelineTweets = timelineTweets;
        this.user = user;
        this.startedDownloadingAt = startedDownloadingAt;
        this.finishedDownloadingAt = finishedDownloadingAt;
    }
    
    public User getUser() {
    
        return user;
    }
    
    public List<Tweet> getTimelineTweets() {
    
        return timelineTweets;
    }
    
    public DateTime getStartedDownloadingAt() {
    
        return startedDownloadingAt;
    }
    
    public DateTime getFinishedDownloadingAt() {
    
        return finishedDownloadingAt;
    }
    
    @Override
    public int hashCode() {
    
        final int prime = 31;
        int result = 1;
        result = prime * result
                + (finishedDownloadingAt == null ? 0 : finishedDownloadingAt.hashCode());
        result = prime * result
                + (startedDownloadingAt == null ? 0 : startedDownloadingAt.hashCode());
        result = prime * result + (timelineTweets == null ? 0 : timelineTweets.hashCode());
        result = prime * result + (user == null ? 0 : user.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
    
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TweetsPage other = (TweetsPage) obj;
        if (finishedDownloadingAt == null) {
            if (other.finishedDownloadingAt != null) {
                return false;
            }
        } else if (!finishedDownloadingAt.equals(other.finishedDownloadingAt)) {
            return false;
        }
        if (startedDownloadingAt == null) {
            if (other.startedDownloadingAt != null) {
                return false;
            }
        } else if (!startedDownloadingAt.equals(other.startedDownloadingAt)) {
            return false;
        }
        if (timelineTweets == null) {
            if (other.timelineTweets != null) {
                return false;
            }
        } else if (!timelineTweets.equals(other.timelineTweets)) {
            return false;
        }
        if (user == null) {
            if (other.user != null) {
                return false;
            }
        } else if (!user.equals(other.user)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
    
        return "TweetsPage [timelineTweets=" + timelineTweets + ", user=" + user
                + ", startedDownloadingAt=" + startedDownloadingAt + ", finishedDownloadingAt="
                + finishedDownloadingAt + "]";
    }
    
}
