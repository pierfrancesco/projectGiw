package it.uniroma3.dia.giw.model.twitter.data;

import java.util.Arrays;

public class HashTag {
    
    private int[] indices;
    
    private String text;
    
    public HashTag() {
    
    }
    
    public HashTag(int[] indices, String text) {
    
        this.indices = indices;
        this.text = text;
    }
    
    public int[] getIndices() {
    
        return indices;
    }
    
    public void setIndices(int[] indices) {
    
        this.indices = indices;
    }
    
    public String getText() {
    
        return text;
    }
    
    public void setText(String text) {
    
        this.text = text;
    }
    
    @Override
    public boolean equals(Object o) {
    
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        HashTag hashTag = (HashTag) o;
        
        if (!Arrays.equals(indices, hashTag.indices)) {
            return false;
        }
        if (text != null ? !text.equals(hashTag.text) : hashTag.text != null) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
    
        int result = indices != null ? Arrays.hashCode(indices) : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
    
        return "HashTag{" + "indices=" + Arrays.toString(indices) + ", text='" + text + '\'' + '}';
    }
}
