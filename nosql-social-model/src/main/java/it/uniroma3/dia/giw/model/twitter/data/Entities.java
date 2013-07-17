package it.uniroma3.dia.giw.model.twitter.data;

import java.util.ArrayList;
import java.util.List;

public class Entities {
    
    public List<Url> urls;
    public List<HashTag> hashtags;
    public List<UserMention> userMentions;
    
    public Entities() {
    
        this.urls = new ArrayList<Url>();
        this.hashtags = new ArrayList<HashTag>();
        this.userMentions = new ArrayList<UserMention>();
    }
    
    public void addUrl(Url url) {
    
        this.urls.add(url);
        
    }
    
    public void addHashTag(HashTag tag) {
    
        this.hashtags.add(tag);
    }
    
    public void addUserMention(UserMention mention) {
    
        this.userMentions.add(mention);
    }
    
    @Override
    public int hashCode() {
    
        final int prime = 31;
        int result = 1;
        result = prime * result + (hashtags == null ? 0 : hashtags.hashCode());
        result = prime * result + (urls == null ? 0 : urls.hashCode());
        result = prime * result + (userMentions == null ? 0 : userMentions.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
    
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entities other = (Entities) obj;
        if (hashtags == null) {
            if (other.hashtags != null) {
                return false;
            }
        } else if (!hashtags.equals(other.hashtags)) {
            return false;
        }
        if (urls == null) {
            if (other.urls != null) {
                return false;
            }
        } else if (!urls.equals(other.urls)) {
            return false;
        }
        if (userMentions == null) {
            if (other.userMentions != null) {
                return false;
            }
        } else if (!userMentions.equals(other.userMentions)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
    
        return "Entities{" + "urls=" + urls + ", hashtags=" + hashtags + ", userMentions="
                + userMentions + '}';
    }
}
