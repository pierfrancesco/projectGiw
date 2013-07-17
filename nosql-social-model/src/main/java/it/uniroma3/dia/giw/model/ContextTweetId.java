package it.uniroma3.dia.giw.model;

import java.util.UUID;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ContextTweetId {
    
    private UUID id;
    
    public ContextTweetId(String id) {
    
        this.id = UUID.fromString(id);
    }
    
    public ContextTweetId() {
    
    }
    
    public String getId() {
    
        return id.toString();
    }
    
    public void setId(UUID id) {
    
        this.id = id;
    }
    
    @Override
    public boolean equals(Object o) {
    
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        ContextTweetId that = (ContextTweetId) o;
        
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
    
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
    
        return "ContextTweetId{" + "id=" + id + '}';
    }
}
