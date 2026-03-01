package net.ooder.skill.vector.sqlite.model;

import java.util.List;
import java.util.Map;

public class AddVectorRequest {
    private String id;
    private String text;
    private List<Double> embedding;
    private Map<String, Object> metadata;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public List<Double> getEmbedding() { return embedding; }
    public void setEmbedding(List<Double> embedding) { this.embedding = embedding; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
