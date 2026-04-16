package net.ooder.skill.scenes.dto;

import java.util.List;
import java.util.Map;

public class SceneGroupConfigDTO {
    
    public enum CreatorType {
        USER, AGENT, SYSTEM
    }
    
    private String name;
    private String description;
    private String creatorId;
    private CreatorType creatorType;
    private Integer minMembers;
    private Integer maxMembers;
    private String securityPolicy;
    private Long heartbeatInterval;
    private Long heartbeatTimeout;
    private Map<String, Object> extendedConfig;
    private Integer knowledgeTopK;
    private Double knowledgeThreshold;
    private Boolean crossLayerSearch;
    private String selectedRole;
    private String leader;
    private String pushType;
    private List<String> collaborators;
    private List<String> driverConditions;
    private Map<String, Object> llmConfig;
    private List<String> capabilities;
    private String fusionTemplateId;
    private int matchScore;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public CreatorType getCreatorType() { return creatorType; }
    public void setCreatorType(CreatorType creatorType) { this.creatorType = creatorType; }
    public Integer getMinMembers() { return minMembers; }
    public void setMinMembers(Integer minMembers) { this.minMembers = minMembers; }
    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    public String getSecurityPolicy() { return securityPolicy; }
    public void setSecurityPolicy(String securityPolicy) { this.securityPolicy = securityPolicy; }
    public Long getHeartbeatInterval() { return heartbeatInterval; }
    public void setHeartbeatInterval(Long heartbeatInterval) { this.heartbeatInterval = heartbeatInterval; }
    public Long getHeartbeatTimeout() { return heartbeatTimeout; }
    public void setHeartbeatTimeout(Long heartbeatTimeout) { this.heartbeatTimeout = heartbeatTimeout; }
    public Map<String, Object> getExtendedConfig() { return extendedConfig; }
    public void setExtendedConfig(Map<String, Object> extendedConfig) { this.extendedConfig = extendedConfig; }
    public Integer getKnowledgeTopK() { return knowledgeTopK; }
    public void setKnowledgeTopK(Integer knowledgeTopK) { this.knowledgeTopK = knowledgeTopK; }
    public Double getKnowledgeThreshold() { return knowledgeThreshold; }
    public void setKnowledgeThreshold(Double knowledgeThreshold) { this.knowledgeThreshold = knowledgeThreshold; }
    public Boolean getCrossLayerSearch() { return crossLayerSearch; }
    public void setCrossLayerSearch(Boolean crossLayerSearch) { this.crossLayerSearch = crossLayerSearch; }
    public String getSelectedRole() { return selectedRole; }
    public void setSelectedRole(String selectedRole) { this.selectedRole = selectedRole; }
    public String getLeader() { return leader; }
    public void setLeader(String leader) { this.leader = leader; }
    public String getPushType() { return pushType; }
    public void setPushType(String pushType) { this.pushType = pushType; }
    public List<String> getCollaborators() { return collaborators; }
    public void setCollaborators(List<String> collaborators) { this.collaborators = collaborators; }
    public List<String> getDriverConditions() { return driverConditions; }
    public void setDriverConditions(List<String> driverConditions) { this.driverConditions = driverConditions; }
    public Map<String, Object> getLlmConfig() { return llmConfig; }
    public void setLlmConfig(Map<String, Object> llmConfig) { this.llmConfig = llmConfig; }
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    public String getFusionTemplateId() { return fusionTemplateId; }
    public void setFusionTemplateId(String fusionTemplateId) { this.fusionTemplateId = fusionTemplateId; }
    public int getMatchScore() { return matchScore; }
    public void setMatchScore(int matchScore) { this.matchScore = matchScore; }
}
