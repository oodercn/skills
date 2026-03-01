package net.ooder.skill.rag.model;

import java.util.List;

public class PromptRequest {
    private String query;
    private List<String> kbIds;
    private Integer topK = 5;
    private Double threshold = 0.3;
    private String strategy = "HYBRID";
    
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public List<String> getKbIds() { return kbIds; }
    public void setKbIds(List<String> kbIds) { this.kbIds = kbIds; }
    
    public Integer getTopK() { return topK; }
    public void setTopK(Integer topK) { this.topK = topK; }
    
    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }
    
    public String getStrategy() { return strategy; }
    public void setStrategy(String strategy) { this.strategy = strategy; }
}
