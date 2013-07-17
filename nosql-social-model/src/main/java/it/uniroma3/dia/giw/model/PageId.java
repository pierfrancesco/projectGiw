package it.uniroma3.dia.giw.model;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class PageId {
    
    private String id;
    
    public PageId(String id) {
    
        this.id = id;
    }
    
    public String getId() {
    
        return this.id;
    }
    
    @Override
    public boolean equals(Object o) {
    
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        PageId pageId = (PageId) o;
        
        if (id != null ? !id.equals(pageId.id) : pageId.id != null) {
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
    
        return "PageId{" + "id='" + id + '\'' + '}';
    }
}
