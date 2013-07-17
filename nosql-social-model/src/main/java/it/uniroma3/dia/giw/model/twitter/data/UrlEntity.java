package it.uniroma3.dia.giw.model.twitter.data;

import java.net.URL;

/*
 * 
 * BEWARE: the jason name is urlEntities not UrlEntities
 * 
 */

public class UrlEntity {
    
    private int start;
    private int end;
    private URL url;
    private URL expandedURL;
    private String displayURL;
    
    public int getStart() {
    
        return start;
    }
    
    public void setStart(int start) {
    
        this.start = start;
    }
    
    public int getEnd() {
    
        return end;
    }
    
    public void setEnd(int end) {
    
        this.end = end;
    }
    
    public URL getUrl() {
    
        return url;
    }
    
    public void setUrl(URL url) {
    
        this.url = url;
    }
    
    public URL getExpandedURL() {
    
        return expandedURL;
    }
    
    public void setExpandedURL(URL expandedURL) {
    
        this.expandedURL = expandedURL;
    }
    
    public String getDisplayURL() {
    
        return displayURL;
    }
    
    public void setDisplayURL(String displayURL) {
    
        this.displayURL = displayURL;
    }
    
    @Override
    public boolean equals(Object o) {
    
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        final UrlEntity urlEntity = (UrlEntity) o;
        
        if (end != urlEntity.end) {
            return false;
        }
        if (start != urlEntity.start) {
            return false;
        }
        if (displayURL != null ? !displayURL.equals(urlEntity.displayURL)
                : urlEntity.displayURL != null) {
            return false;
        }
        if (expandedURL != null ? !expandedURL.equals(urlEntity.expandedURL)
                : urlEntity.expandedURL != null) {
            return false;
        }
        if (url != null ? !url.equals(urlEntity.url) : urlEntity.url != null) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
    
        int result = start;
        result = 31 * result + end;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (expandedURL != null ? expandedURL.hashCode() : 0);
        result = 31 * result + (displayURL != null ? displayURL.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
    
        return "UrlEntity{" + "start=" + start + ", end=" + end + ", url=" + url + ", expandedURL="
                + expandedURL + ", displayURL='" + displayURL + '\'' + '}';
    }
}
