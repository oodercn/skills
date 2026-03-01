package net.ooder.skill.vector.sqlite.model;

import java.util.List;
import java.util.Map;

public class VectorSearchRequest {
    private String text;
    private List<Double> embedding;
    private Integer topK = 10;
    private Map<String, Object> filter;
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public List<Double> getEmbedding() { return embedding; }
    public void setEmbedding(List<Double> embedding) { this.embedding = embedding; }
    public Integer getTopK() { return topK; }
    public void setTopK(Integer topK) { this.topK = topK; }
    public Map<String, Object> getFilter() { return filter; }
    public void setFilter(Map<String, Object> filter) { this.filter = filter; }
}
