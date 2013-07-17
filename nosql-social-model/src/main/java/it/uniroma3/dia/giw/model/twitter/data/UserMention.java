package it.uniroma3.dia.giw.model.twitter.data;

public class UserMention {
    
    private int[] indices;
    private String name;
    private String screenName;
    private long id;
    
    public int[] getIndices() {
    
        return indices;
    }
    
    public void setIndices(int[] indices) {
    
        this.indices = indices;
    }
    
    public String getName() {
    
        return name;
    }
    
    public void setName(String name) {
    
        this.name = name;
    }
    
    public String getScreenName() {
    
        return screenName;
    }
    
    public void setScreenName(String screenName) {
    
        this.screenName = screenName;
    }
    
    public long getId() {
    
        return id;
    }
    
    public void setId(long id) {
    
        this.id = id;
    }
    
}
