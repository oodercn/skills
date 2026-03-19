package net.ooder.mvp.api.scene.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SceneGroupDTO implements Serializable {
    private String sceneGroupId;
    private String templateId;
    private String name;
    private String description;
    private String status;
    private String creatorId;
    private String creatorType;
    private SceneGroupConfigDTO config;
    private int memberCount;
    private List<ParticipantDTO> participants;
    private List<CapabilityBindingDTO> capabilityBindings;
    private List<KnowledgeBaseBindingDTO> knowledgeBases;
    private long createTime;
    private long lastUpdateTime;
    
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public String getCreatorType() { return creatorType; }
    public void setCreatorType(String creatorType) { this.creatorType = creatorType; }
    public SceneGroupConfigDTO getConfig() { return config; }
    public void setConfig(SceneGroupConfigDTO config) { this.config = config; }
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    public List<ParticipantDTO> getParticipants() { return participants; }
    public void setParticipants(List<ParticipantDTO> participants) { this.participants = participants; }
    public List<CapabilityBindingDTO> getCapabilityBindings() { return capabilityBindings; }
    public void setCapabilityBindings(List<CapabilityBindingDTO> capabilityBindings) { this.capabilityBindings = capabilityBindings; }
    public List<KnowledgeBaseBindingDTO> getKnowledgeBases() { return knowledgeBases; }
    public void setKnowledgeBases(List<KnowledgeBaseBindingDTO> knowledgeBases) { this.knowledgeBases = knowledgeBases; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(long lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
}
