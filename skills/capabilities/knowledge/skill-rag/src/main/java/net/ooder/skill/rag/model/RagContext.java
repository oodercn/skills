package net.ooder.skill.rag.model;

import java.util.List;
import java.util.Map;

public class RagContext {
    private String query;
    private List<String> kbIds;
    private int topK = 5;
    private double scoreThreshold = 0.3;
    private String strategy = "HYBRID";
    private Map<String, Object> params;
    private String userId;
    private String sceneId;
    
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public List<String> getKbIds() { return kbIds; }
    public void setKbIds(List<String> kbIds) { this.kbIds = kbIds; }
    
    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }
    
    public double getScoreThreshold() { return scoreThreshold; }
    public void setScoreThreshold(double scoreThreshold) { this.scoreThreshold = scoreThreshold; }
    
    public String getStrategy() { return strategy; }
    public void setStrategy(String strategy) { this.strategy = strategy; }
    
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
}
