package net.ooder.mvp.api.scene.dto;

import java.io.Serializable;

public class SceneGroupConfigDTO implements Serializable {
    private String name;
    private String description;
    private String creatorId;
    private String creatorType;
    private Integer minMembers;
    private Integer maxMembers;
    private Integer knowledgeTopK;
    private Double knowledgeThreshold;
    private Boolean crossLayerSearch;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public String getCreatorType() { return creatorType; }
    public void setCreatorType(String creatorType) { this.creatorType = creatorType; }
    public Integer getMinMembers() { return minMembers; }
    public void setMinMembers(Integer minMembers) { this.minMembers = minMembers; }
    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    public Integer getKnowledgeTopK() { return knowledgeTopK; }
    public void setKnowledgeTopK(Integer knowledgeTopK) { this.knowledgeTopK = knowledgeTopK; }
    public Double getKnowledgeThreshold() { return knowledgeThreshold; }
    public void setKnowledgeThreshold(Double knowledgeThreshold) { this.knowledgeThreshold = knowledgeThreshold; }
    public Boolean getCrossLayerSearch() { return crossLayerSearch; }
    public void setCrossLayerSearch(Boolean crossLayerSearch) { this.crossLayerSearch = crossLayerSearch; }
}
