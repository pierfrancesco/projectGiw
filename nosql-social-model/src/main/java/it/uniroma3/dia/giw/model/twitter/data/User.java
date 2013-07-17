package it.uniroma3.dia.giw.model.twitter.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User implements TwitterEntity {
    
    private long id = 0L;
    private Date createdAt = null;
    private boolean defaultProfile;
    private boolean defaultProfileImage;
    private String description;
    private Entities entities;
    private int favouritesCount;
    private List<User> followers = new ArrayList<User>(0);
    private int followersCount;
    private List<User> friends = new ArrayList<User>(0);
    private int friendsCount;
    private boolean isContributorsEnabled;
    private boolean isGeoEnabled;
    private boolean isProtected;
    private String lang;
    private int listedCount;
    private String location;
    private String name;
    private String screenName;
    private int statusesCount;
    private String timeZone;
    private String token;
    
    private String tokenSecret;
    private String url;
    
    private int utcOffset;
    private boolean verified;
    
    public static class Users {
        
        private final Long id;
        
        private String screenName;
        
        public Users(Long id) {
        
            this.id = id;
        }
        
        public Users withScreenName(String screenName) {
        
            this.screenName = screenName;
            return this;
        }
        
        public User build() {
        
            validateParameters();
            return new User(this.id, this.screenName);
        }
        
        private void validateParameters() {
        
            //
        }
    }
    
    public User() {
    
    }
    
    public User(final String screenName) {
    
        this.screenName = screenName;
    }
    
    public User(final long id) {
    
        this.id = id;
    }
    
    public User(final long id, String screenName) {
    
        this(id);
        this.screenName = screenName;
    }
    
    public String getDescription() {
    
        return description;
    }
    
    public Entities getEntities() {
    
        return entities;
    }
    
    public int getFavouritesCount() {
    
        return favouritesCount;
    }
    
    public List<User> getFollowers() {
    
        return followers;
    }
    
    public int getFollowersCount() {
    
        return followersCount;
    }
    
    public List<User> getFriends() {
    
        return friends;
    }
    
    public int getFriendsCount() {
    
        return friendsCount;
    }
    
    public String getLang() {
    
        return lang;
    }
    
    public int getListedCount() {
    
        return listedCount;
    }
    
    public String getLocation() {
    
        return location;
    }
    
    public String getName() {
    
        return name;
    }
    
    public String getScreenName() {
    
        return screenName;
    }
    
    public int getStatusesCount() {
    
        return statusesCount;
    }
    
    public String getTimeZone() {
    
        return timeZone;
    }
    
    public String getToken() {
    
        return token;
    }
    
    public String getTokenSecret() {
    
        return tokenSecret;
    }
    
    public String getUrl() {
    
        return url;
    }
    
    public int getUtcOffset() {
    
        return utcOffset;
    }
    
    public boolean isContributorsEnabled() {
    
        return isContributorsEnabled;
    }
    
    public boolean isDefaultProfile() {
    
        return defaultProfile;
    }
    
    public boolean isDefaultProfileImage() {
    
        return defaultProfileImage;
    }
    
    public boolean isGeoEnabled() {
    
        return isGeoEnabled;
    }
    
    public boolean isProtected() {
    
        return isProtected;
    }
    
    public boolean isVerified() {
    
        return verified;
    }
    
    public void setContributorsEnabled(final boolean isContributorsEnabled) {
    
        this.isContributorsEnabled = isContributorsEnabled;
    }
    
    public void setDefaultProfile(final boolean defaultProfile) {
    
        this.defaultProfile = defaultProfile;
    }
    
    public void setDefaultProfileImage(final boolean defaultProfileImage) {
    
        this.defaultProfileImage = defaultProfileImage;
    }
    
    public void setDescription(final String description) {
    
        this.description = description;
    }
    
    public void setEntities(final Entities entities) {
    
        this.entities = entities;
    }
    
    public void setFavouritesCount(final int favouritesCount) {
    
        this.favouritesCount = favouritesCount;
    }
    
    public void setFollowers(final List<User> followers) {
    
        this.followers = followers;
    }
    
    public void setFollowersCount(final int followersCount) {
    
        this.followersCount = followersCount;
    }
    
    public void setFriends(final List<User> friends) {
    
        this.friends = friends;
    }
    
    public void setFriendsCount(final int friendsCount) {
    
        this.friendsCount = friendsCount;
    }
    
    public void setGeoEnabled(final boolean geoEnabled) {
    
        this.isGeoEnabled = geoEnabled;
    }
    
    public void setLang(final String lang) {
    
        this.lang = lang;
    }
    
    public void setListedCount(final int listedCount) {
    
        this.listedCount = listedCount;
    }
    
    public void setLocation(final String location) {
    
        this.location = location;
    }
    
    public void setName(final String name) {
    
        this.name = name;
    }
    
    public void setProtected(final boolean isProtected) {
    
        this.isProtected = isProtected;
    }
    
    public void setScreenName(final String screenName) {
    
        this.screenName = screenName;
    }
    
    public void setStatusesCount(final int statusesCount) {
    
        this.statusesCount = statusesCount;
    }
    
    public void setTimeZone(final String timeZone) {
    
        this.timeZone = timeZone;
    }
    
    public void setToken(final String token) {
    
        this.token = token;
    }
    
    public void setTokenSecret(final String tokenSecret) {
    
        this.tokenSecret = tokenSecret;
    }
    
    public void setUrl(final String url) {
    
        this.url = url;
    }
    
    public void setUtcOffset(final int utcOffset) {
    
        this.utcOffset = utcOffset;
    }
    
    public void setVerified(final boolean verified) {
    
        this.verified = verified;
    }
    
    public long getId() {
    
        return id;
    }
    
    public void setId(long id) {
    
        this.id = id;
    }
    
    public Date getCreatedAt() {
    
        return createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
    
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        User user = (User) o;
        
        if (id != user.id) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
    
        return (int) (id ^ (id >>> 32));
    }
    
    @Override
    public String toString() {
    
        return "User [id=" + id + ", createdAt=" + createdAt + ", defaultProfile=" + defaultProfile
                + ", defaultProfileImage=" + defaultProfileImage + ", description=" + description
                + ", entities=" + entities + ", favouritesCount=" + favouritesCount
                + ", followers=" + followers + ", followersCount=" + followersCount + ", friends="
                + friends + ", friendsCount=" + friendsCount + ", isContributorsEnabled="
                + isContributorsEnabled + ", isGeoEnabled=" + isGeoEnabled + ", isProtected="
                + isProtected + ", lang=" + lang + ", listedCount=" + listedCount + ", location="
                + location + ", name=" + name + ", screenName=" + screenName + ", statusesCount="
                + statusesCount + ", timeZone=" + timeZone + ", token=" + token + ", tokenSecret="
                + tokenSecret + ", url=" + url + ", utcOffset=" + utcOffset + ", verified="
                + verified + "]";
    }
    
    public void setCreatedAt(Date createdAt) {
    
        this.createdAt = createdAt;
    }
    
}
