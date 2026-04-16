package net.ooder.spi.vector;

import java.util.HashMap;
import java.util.Map;

public class VectorData {
    
    private String id;
    private float[] vector;
    private Map<String, Object> metadata = new HashMap<>();
    
    public VectorData() {
    }
    
    public VectorData(String id, float[] vector) {
        this.id = id;
        this.vector = vector;
    }
    
    public VectorData(String id, float[] vector, Map<String, Object> metadata) {
        this.id = id;
        this.vector = vector;
        this.metadata = metadata;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public float[] getVector() {
        return vector;
    }
    
    public void setVector(float[] vector) {
        this.vector = vector;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public VectorData addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
}
