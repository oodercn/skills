package net.ooder.spi.vector;

import java.util.HashMap;
import java.util.Map;

public class SearchResult {
    
    private String id;
    private float[] vector;
    private float score;
    private Map<String, Object> metadata = new HashMap<>();
    
    public SearchResult() {
    }
    
    public SearchResult(String id, float score) {
        this.id = id;
        this.score = score;
    }
    
    public SearchResult(String id, float[] vector, float score, Map<String, Object> metadata) {
        this.id = id;
        this.vector = vector;
        this.score = score;
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
    
    public float getScore() {
        return score;
    }
    
    public void setScore(float score) {
        this.score = score;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public SearchResult addMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
}
