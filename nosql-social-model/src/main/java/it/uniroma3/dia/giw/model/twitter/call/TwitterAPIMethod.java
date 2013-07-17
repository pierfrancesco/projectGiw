package it.uniroma3.dia.giw.model.twitter.call;

import java.io.Serializable;

public class TwitterAPIMethod implements Serializable {
    
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    
    private String name;
    
    private static final String GET_FOLLOWERS_URL = "/followers/ids";
    
    private static final String GET_FOLLOWINGS_URL = "/friends/ids";
    
    private static final String GET_USER_TIMELINE_URL = "/statuses/user_timeline";
    
    private static final String SHOW_USER_URL = "/users/show/:id";
    
    private static final String STATUSES_FILTER_URL = "/statuses/filter";
    
    public static TwitterAPIMethod SHOW_USER = new TwitterAPIMethod(SHOW_USER_URL);
    
    public static TwitterAPIMethod GET_FOLLOWERS = new TwitterAPIMethod(GET_FOLLOWERS_URL);
    
    public static TwitterAPIMethod GET_FOLLOWINGS = new TwitterAPIMethod(GET_FOLLOWINGS_URL);
    
    public static TwitterAPIMethod GET_USER_TIMELINE = new TwitterAPIMethod(GET_USER_TIMELINE_URL);
    
    public static TwitterAPIMethod STATUSES_FILTER = new TwitterAPIMethod(STATUSES_FILTER_URL);
    
    public TwitterAPIMethod() {
    
    }
    
    public TwitterAPIMethod(String name) {
    
        this.name = name;
    }
    
    public String getName() {
    
        return name;
    }
    
    public void setName(String name) {
    
        this.name = name;
    }
    
    @Override
    public int hashCode() {
    
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
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
        TwitterAPIMethod other = (TwitterAPIMethod) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
    
        return name;
    }
}
