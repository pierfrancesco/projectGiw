package it.uniroma3.dia.giw.model;

import it.uniroma3.dia.giw.model.twitter.data.Tweet;

public class ContextTweet {
    
    private ContextTweetId id;
    private Tweet tweet;
    private String monitoringActivityId;
    private int year;
    private int dayOfTheYear;
    
    public ContextTweet(ContextTweetId id, Tweet tweet, String monitoringActivityId, int year,
            int dayOfTheYear) {
    
        this.id = id;
        this.tweet = tweet;
        this.monitoringActivityId = monitoringActivityId;
        this.year = year;
        this.dayOfTheYear = dayOfTheYear;
    }
    
    public ContextTweet() {
    
    }
    
    public ContextTweetId getId() {
    
        return id;
    }
    
    public Tweet getTweet() {
    
        return tweet;
    }
    
    public String getMonitoringActivityId() {
    
        return monitoringActivityId;
    }
    
    public int getYear() {
    
        return year;
    }
    
    public int getDayOfTheYear() {
    
        return dayOfTheYear;
    }
    
    public void setId(ContextTweetId id) {
    
        this.id = id;
    }
    
    public void setTweet(Tweet tweet) {
    
        this.tweet = tweet;
    }
    
    public void setMonitoringActivityId(String monitoringActivityId) {
    
        this.monitoringActivityId = monitoringActivityId;
    }
    
    public void setYear(int year) {
    
        this.year = year;
    }
    
    public void setDayOfTheYear(int dayOfTheYear) {
    
        this.dayOfTheYear = dayOfTheYear;
    }
    
    @Override
    public boolean equals(Object o) {
    
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        ContextTweet that = (ContextTweet) o;
        
        if (dayOfTheYear != that.dayOfTheYear) {
            return false;
        }
        if (year != that.year) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (monitoringActivityId != null ? !monitoringActivityId.equals(that.monitoringActivityId)
                : that.monitoringActivityId != null) {
            return false;
        }
        if (tweet != null ? !tweet.equals(that.tweet) : that.tweet != null) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
    
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (tweet != null ? tweet.hashCode() : 0);
        result = 31 * result + (monitoringActivityId != null ? monitoringActivityId.hashCode() : 0);
        result = 31 * result + year;
        result = 31 * result + dayOfTheYear;
        return result;
    }
    
    @Override
    public String toString() {
    
        return "ContextTweet{" + "id=" + id + ", tweet=" + tweet + ", monitoringActivityId='"
                + monitoringActivityId + '\'' + ", year=" + year + ", dayOfTheYear=" + dayOfTheYear
                + '}';
    }
}
