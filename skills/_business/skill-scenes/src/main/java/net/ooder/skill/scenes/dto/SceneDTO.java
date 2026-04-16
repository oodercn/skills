package net.ooder.skill.scenes.dto;

import java.util.List;
import java.util.Map;

public class SceneDTO {

    private String sceneId;
    private String name;
    private String description;
    private String type;
    private String status;
    private String ownerId;
    private String ownerName;
    private List<String> capabilityIds;
    private List<String> collaborativeUserIds;
    private Map<String, Object> config;
    private long createdAt;
    private long updatedAt;
    private long activatedAt;

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public List<String> getCapabilityIds() { return capabilityIds; }
    public void setCapabilityIds(List<String> capabilityIds) { this.capabilityIds = capabilityIds; }

    public List<String> getCollaborativeUserIds() { return collaborativeUserIds; }
    public void setCollaborativeUserIds(List<String> collaborativeUserIds) { this.collaborativeUserIds = collaborativeUserIds; }

    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public long getActivatedAt() { return activatedAt; }
    public void setActivatedAt(long activatedAt) { this.activatedAt = activatedAt; }
}