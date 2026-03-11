package net.ooder.skill.knowledge.model;

public class SearchRequest {
    private String query;
    private Integer topK = 5;
    private Double threshold = 0.0;
    
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public Integer getTopK() { return topK; }
    public void setTopK(Integer topK) { this.topK = topK; }
    
    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }
}
