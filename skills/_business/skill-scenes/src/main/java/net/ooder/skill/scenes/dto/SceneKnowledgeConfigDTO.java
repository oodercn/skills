package net.ooder.skill.scenes.dto;

import java.util.Map;

public class SceneKnowledgeConfigDTO {
    private Integer topK;
    private Double threshold;
    private Boolean crossLayerSearch;
    private Map<String, Object> extendedConfig;

    public Integer getTopK() { return topK; }
    public void setTopK(Integer topK) { this.topK = topK; }
    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }
    public Boolean getCrossLayerSearch() { return crossLayerSearch; }
    public void setCrossLayerSearch(Boolean crossLayerSearch) { this.crossLayerSearch = crossLayerSearch; }
    public Map<String, Object> getExtendedConfig() { return extendedConfig; }
    public void setExtendedConfig(Map<String, Object> extendedConfig) { this.extendedConfig = extendedConfig; }
}
