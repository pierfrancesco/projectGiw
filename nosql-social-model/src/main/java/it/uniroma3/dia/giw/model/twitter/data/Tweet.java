package it.uniroma3.dia.giw.model.twitter.data;

import java.util.Date;

public class Tweet implements TwitterEntity {
    
    private long id = 0L;
    private Date createdAt = null;
    private Coordinate coordinate;
    private Entities entities;
    private String inReplyToScreenName;
    private long inReplyToStatusId;
    private long inReplyToUserId;
    private Place place;
    private boolean isFavorited;
    private boolean isTruncated;
    private boolean possiblySensitive;
    private int retweetCount;
    private Tweet retweetedStatus;
    private String source;
    private String text;
    private User user;
    private String lang;
    
    public static class Tweets {
        
        private final Long id;
        
        private Date createdAt = new Date(0);
        
        private String text = "";
        
        private String source = "";
        
        private Entities entities = new Entities();
        
        private User user;
        
        public Tweets(Long id) {
        
            this.id = id;
        }
        
        public Tweets createdAt(Date timestamp) {
        
            this.createdAt = timestamp;
            return this;
        }
        
        public Tweets containingText(String content) {
        
            this.text = content;
            return this;
        }
        
        public Tweets withEntities(Entities entities1) {
        
            this.entities = entities1;
            return this;
        }
        
        public Tweets authoredBy(User user) {
        
            this.user = user;
            return this;
        }
        
        public Tweet build() {
        
            validateParameters();
            return new Tweet(this.id, this.createdAt, this.text, this.source, this.entities,
                    this.user);
        }
        
        private void validateParameters() {
        
            //
        }
    }
    
    public Tweet() {
    
    }
    
    public Tweet(final long id, final Date createdAt, final String text, final String source,
            final Entities entities, final User tweetedBy) {
    
        this(id, createdAt, text, source, tweetedBy);
        this.entities = entities;
    }
    
    public Tweet(final long id, final Date createdAt, final String text, final String source,
            final User user) {
    
        this.id = id;
        this.createdAt = createdAt;
        this.text = text;
        this.source = source;
        this.user = user;
    }
    
    public Tweet(final long id, final Date createdAt, final String text, final String source) {
    
        this(id, createdAt, text, source, null);
    }
    
    public Tweet(final long id, final String text) {
    
        this(id, new Date(), text, "");
    }
    
    public Entities getEntities() {
    
        return entities;
    }
    
    public String getInReplyToScreenName() {
    
        return inReplyToScreenName;
    }
    
    public long getInReplyToStatusId() {
    
        return inReplyToStatusId;
    }
    
    public long getInReplyToUserId() {
    
        return inReplyToUserId;
    }
    
    public Place getPlace() {
    
        return place;
    }
    
    public int getRetweetCount() {
    
        return retweetCount;
    }
    
    public Tweet getRetweetedStatus() {
    
        return retweetedStatus;
    }
    
    public String getSource() {
    
        return source;
    }
    
    public String getText() {
    
        return text;
    }
    
    public User getUser() {
    
        return user;
    }
    
    public boolean isPossiblySensitive() {
    
        return possiblySensitive;
    }
    
    public boolean isTruncated() {
    
        return isTruncated;
    }
    
    public void setEntities(final Entities entities) {
    
        this.entities = entities;
    }
    
    public void setInReplyToScreenName(final String inReplyToScreenName) {
    
        this.inReplyToScreenName = inReplyToScreenName;
    }
    
    public void setInReplyToStatusId(final long inReplyToStatusId) {
    
        this.inReplyToStatusId = inReplyToStatusId;
    }
    
    public void setInReplyToUserId(final long inReplyToUserId) {
    
        this.inReplyToUserId = inReplyToUserId;
    }
    
    public void setPlace(final Place place) {
    
        this.place = place;
    }
    
    public void setPossiblySensitive(final boolean possiblySensitive) {
    
        this.possiblySensitive = possiblySensitive;
    }
    
    public void setRetweetCount(final int retweetCount) {
    
        this.retweetCount = retweetCount;
    }
    
    public void setRetweetedStatus(final Tweet retweetedStatus) {
    
        this.retweetedStatus = retweetedStatus;
    }
    
    public void setSource(final String source) {
    
        this.source = source;
    }
    
    public void setText(final String text) {
    
        this.text = text;
    }
    
    public void setTruncated(final boolean isTruncated) {
    
        this.isTruncated = isTruncated;
    }
    
    public void setUser(final User user) {
    
        this.user = user;
    }
    
    public Coordinate getCoordinate() {
    
        return coordinate;
    }
    
    public void setCoordinate(final Coordinate coordinate) {
    
        this.coordinate = coordinate;
    }
    
    public boolean isFavorited() {
    
        return isFavorited;
    }
    
    public void setFavorited(final boolean isFavorited) {
    
        this.isFavorited = isFavorited;
    }
    
    public String getLang() {
    
        return lang;
    }
    
    public void setLang(final String lang) {
    
        this.lang = lang;
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
    
    public void setCreatedAt(Date createdAt) {
    
        this.createdAt = createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
    
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        Tweet tweet = (Tweet) o;
        
        if (id != tweet.id) {
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
    
        return "Tweet [id=" + id + ", createdAt=" + createdAt + ", coordinate=" + coordinate
                + ", entities=" + entities + ", inReplyToScreenName=" + inReplyToScreenName
                + ", inReplyToStatusId=" + inReplyToStatusId + ", inReplyToUserId="
                + inReplyToUserId + ", place=" + place + ", isFavorited=" + isFavorited
                + ", isTruncated=" + isTruncated + ", possiblySensitive=" + possiblySensitive
                + ", retweetCount=" + retweetCount + ", retweetedStatus=" + retweetedStatus
                + ", source=" + source + ", text=" + text + ", user=" + user + ", lang=" + lang
                + "]";
    }
    
}
