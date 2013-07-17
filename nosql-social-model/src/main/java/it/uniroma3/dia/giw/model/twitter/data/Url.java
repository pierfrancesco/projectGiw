package it.uniroma3.dia.giw.model.twitter.data;

import java.util.Arrays;

public class Url {
    
    private String expandedUrl;
    private int[] indices;
    private String displayUrl;
    private String url;
    
    public Url() {
    
        this.expandedUrl = "";
        this.indices = new int[0];
        this.displayUrl = "";
        this.url = "";
    }
    
    public Url(String expandedUrl, int[] indices, String displayUrl, String url) {
    
        this.expandedUrl = expandedUrl;
        this.indices = indices;
        this.displayUrl = displayUrl;
        this.url = url;
    }
    
    public static class Urls {
        
        private final String expandedUrl;
        private int[] indices;
        private String displayUrl;
        private String url;
        
        public Urls(String expandedUrl) {
        
            this.expandedUrl = expandedUrl;
        }
        
        public Urls foundAt(int startIndex, int endIndex) {
        
            this.indices = new int[2];
            indices[0] = startIndex;
            indices[1] = endIndex;
            return this;
        }
        
        public Urls withDisplay(String displayUrl) {
        
            this.displayUrl = displayUrl;
            return this;
        }
        
        public Urls withUrl(String url) {
        
            this.url = url;
            return this;
        }
        
        public Url build() {
        
            validateParameters();
            return new Url(this.expandedUrl, this.indices, this.displayUrl, this.url);
        }
        
        private void validateParameters() {
        
            // TODO
        }
    }
    
    public String getExpandedUrl() {
    
        return expandedUrl;
    }
    
    public void setExpandedUrl(String expandedUrl) {
    
        this.expandedUrl = expandedUrl;
    }
    
    public int[] getIndices() {
    
        return indices;
    }
    
    public void setIndices(int[] indices) {
    
        this.indices = indices;
    }
    
    public String getDisplayUrl() {
    
        return displayUrl;
    }
    
    public void setDisplayUrl(String displayUrl) {
    
        this.displayUrl = displayUrl;
    }
    
    public String getUrl() {
    
        return url;
    }
    
    public void setUrl(String url) {
    
        this.url = url;
    }
    
    @Override
    public boolean equals(Object o) {
    
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        Url url1 = (Url) o;
        
        if (displayUrl != null ? !displayUrl.equals(url1.displayUrl) : url1.displayUrl != null) {
            return false;
        }
        if (expandedUrl != null ? !expandedUrl.equals(url1.expandedUrl) : url1.expandedUrl != null) {
            return false;
        }
        if (!Arrays.equals(indices, url1.indices)) {
            return false;
        }
        if (url != null ? !url.equals(url1.url) : url1.url != null) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
    
        int result = expandedUrl != null ? expandedUrl.hashCode() : 0;
        result = 31 * result + (indices != null ? Arrays.hashCode(indices) : 0);
        result = 31 * result + (displayUrl != null ? displayUrl.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
    
        return "Url{" + "expandedUrl='" + expandedUrl + '\'' + ", indices="
                + Arrays.toString(indices) + ", displayUrl='" + displayUrl + '\'' + ", url='" + url
                + '\'' + '}';
    }
}
