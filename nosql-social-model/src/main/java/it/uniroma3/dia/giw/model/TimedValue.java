package it.uniroma3.dia.giw.model;

import org.joda.time.DateTime;

public class TimedValue {
    
    private int value;
    private DateTime time;
    
    public TimedValue(int value, DateTime time) {
    
        this.value = value;
        this.time = time;
    }
    
    public int getValue() {
    
        return value;
    }
    
    public void setValue(int value) {
    
        this.value = value;
    }
    
    public DateTime getTime() {
    
        return time;
    }
    
    public void setTime(DateTime time) {
    
        this.time = time;
    }
    
    @Override
    public int hashCode() {
    
        final int prime = 31;
        int result = 1;
        result = prime * result + (time == null ? 0 : time.hashCode());
        result = prime * result + value;
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
        TimedValue other = (TimedValue) obj;
        if (time == null) {
            if (other.time != null) {
                return false;
            }
        } else if (!time.equals(other.time)) {
            return false;
        }
        if (value != other.value) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
    
        return "TimedValue [value=" + value + ", time=" + time + "]";
    }
    
}
