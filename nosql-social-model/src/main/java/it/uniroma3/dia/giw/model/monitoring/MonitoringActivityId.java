package it.uniroma3.dia.giw.model.monitoring;

import java.io.Serializable;

import com.google.common.base.Objects;

public class MonitoringActivityId implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String value;
    
    public MonitoringActivityId() {
    
    }
    
    public MonitoringActivityId(String value) {
    
        this.value = value;
    }
    
    public String getValue() {
    
        return value;
    }
    
    public void setValue(String value) {
    
        this.value = value;
    }
    
    @Override
    public boolean equals(final Object o) {
    
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        final MonitoringActivityId that = (MonitoringActivityId) o;
        
        return Objects.equal(value, that.value);
        
    }
    
    @Override
    public int hashCode() {
    
        return Objects.hashCode(value);
    }
    
    @Override
    public String toString() {
    
        return Objects.toStringHelper(this).addValue(value).toString();
    }
}
