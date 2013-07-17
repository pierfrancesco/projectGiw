package it.uniroma3.dia.giw.model;

import it.uniroma3.dia.giw.model.twitter.data.User;

import java.util.Arrays;

import org.joda.time.DateTime;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class RelatedUsersPage {
    
    private final long[] relatedUserIds;
    private final User user;
    private final DateTime startedDownloadingAt;
    private final DateTime finishedDownloadingAt;
    
    public RelatedUsersPage(long[] relatedUserIds, User user, DateTime startedDownloadingAt,
            DateTime finishedDownloadingAt) {
    
        this.relatedUserIds = relatedUserIds;
        this.user = user;
        this.startedDownloadingAt = startedDownloadingAt;
        this.finishedDownloadingAt = finishedDownloadingAt;
    }
    
    public User getUser() {
    
        return user;
    }
    
    public long[] getRelatedUserIds() {
    
        return relatedUserIds;
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
        result = prime * result + Arrays.hashCode(relatedUserIds);
        result = prime * result
                + (startedDownloadingAt == null ? 0 : startedDownloadingAt.hashCode());
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
        RelatedUsersPage other = (RelatedUsersPage) obj;
        if (finishedDownloadingAt == null) {
            if (other.finishedDownloadingAt != null) {
                return false;
            }
        } else if (!finishedDownloadingAt.equals(other.finishedDownloadingAt)) {
            return false;
        }
        if (!Arrays.equals(relatedUserIds, other.relatedUserIds)) {
            return false;
        }
        if (startedDownloadingAt == null) {
            if (other.startedDownloadingAt != null) {
                return false;
            }
        } else if (!startedDownloadingAt.equals(other.startedDownloadingAt)) {
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
    
        return "RelatedUsersPage [relatedUserIds=" + Arrays.toString(relatedUserIds) + ", user="
                + user + ", startedDownloadingAt=" + startedDownloadingAt
                + ", finishedDownloadingAt=" + finishedDownloadingAt + "]";
    }
    
}
