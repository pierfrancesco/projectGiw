package it.uniroma3.dia.giw.model.twitter.data;

import java.util.Arrays;

public class Coordinate {
    
    public float[] coordinates;
    public String type;
    
    public Coordinate() {
    
    }
    
    @Override
    public int hashCode() {
    
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(coordinates);
        result = prime * result + (type == null ? 0 : type.hashCode());
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
        Coordinate other = (Coordinate) obj;
        if (!Arrays.equals(coordinates, other.coordinates)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
    
        return "Coordinate [coordinates=" + Arrays.toString(coordinates) + ", type=" + type + "]";
    }
    
}
