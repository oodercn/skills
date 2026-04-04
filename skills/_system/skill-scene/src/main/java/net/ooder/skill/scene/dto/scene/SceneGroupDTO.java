package net.ooder.skill.scene.dto.scene;

import java.util.List;
import java.util.Map;

public class SceneGroupDTO {
    
    private String sceneGroupId;
    private String templateId;
    private String name;
    private String description;
    private SceneGroupStatus status;
    private String creator;
    private List<SceneParticipantDTO> participants;
    private List<CapabilityBindingDTO> capabilities;
    private int memberCount;
    private long createTime;
    private long lastUpdateTime;

    public enum SceneGroupStatus {
        CREATED, ACTIVATED, DEACTIVATED, ARCHIVED
    }

    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public SceneGroupStatus getStatus() { return status; }
    public void setStatus(SceneGroupStatus status) { this.status = status; }
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
    public List<SceneParticipantDTO> getParticipants() { return participants; }
    public void setParticipants(List<SceneParticipantDTO> participants) { this.participants = participants; }
    public List<CapabilityBindingDTO> getCapabilities() { return capabilities; }
    public void setCapabilities(List<CapabilityBindingDTO> capabilities) { this.capabilities = capabilities; }
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(long lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
}
