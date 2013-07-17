package it.uniroma3.dia.giw.model;

import it.uniroma3.dia.giw.model.twitter.data.User;

import java.util.List;

import com.google.common.base.Objects;

public class ContextUser {
    
    private User user;
    private List<String> monitoringActivityIds;
    private List<User> followers;
    
    public ContextUser() {
    
    }
    
    public ContextUser(User user, List<String> monitoringActivityIds) {
    
        this.user = user;
        this.monitoringActivityIds = monitoringActivityIds;
    }
    
    public User getUser() {
    
        return user;
    }
    
    public List<String> getMonitoringActivityIds() {
    
        return monitoringActivityIds;
    }
    
    @Override
    public boolean equals(final Object o) {
    
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        final ContextUser that = (ContextUser) o;
        
        return Objects.equal(user, that.user)
                && Objects.equal(monitoringActivityIds, that.monitoringActivityIds);
        
    }
    
    @Override
    public int hashCode() {
    
        return Objects.hashCode(user, monitoringActivityIds);
    }
    
    @Override
    public String toString() {
    
        return Objects.toStringHelper(this).addValue(user).addValue(monitoringActivityIds)
                .toString();
    }
    
}
