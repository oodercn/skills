package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class SceneKnowledgeConfigDTO {
    private String kbId;
    private String layer;
    private int priority;
    private boolean enabled;
    private int topK;
    private double threshold;
    private Map<String, Object> filters;

    public String getKbId() { return kbId; }
    public void setKbId(String kbId) { this.kbId = kbId; }
    public String getLayer() { return layer; }
    public void setLayer(String layer) { this.layer = layer; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }
    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }
    public Map<String, Object> getFilters() { return filters; }
    public void setFilters(Map<String, Object> filters) { this.filters = filters; }
}
