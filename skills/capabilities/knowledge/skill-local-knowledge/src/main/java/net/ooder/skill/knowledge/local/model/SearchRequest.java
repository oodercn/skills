package net.ooder.skill.knowledge.local.model;

import java.util.Map;

public class SearchRequest {
    private String query;
    private Integer topK = 10;
    private Map<String, Object> filters;
    
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public Integer getTopK() { return topK; }
    public void setTopK(Integer topK) { this.topK = topK; }
    public Map<String, Object> getFilters() { return filters; }
    public void setFilters(Map<String, Object> filters) { this.filters = filters; }
}
