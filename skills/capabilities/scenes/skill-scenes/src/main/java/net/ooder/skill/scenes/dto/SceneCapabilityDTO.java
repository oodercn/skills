package net.ooder.skill.scenes.dto;

public class SceneCapabilityDTO {

    private String capId;
    private String sceneId;
    private String capabilityName;
    private String capabilityType;
    private String status;
    private int priority;
    private long addedAt;

    public String getCapId() { return capId; }
    public void setCapId(String capId) { this.capId = capId; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getCapabilityName() { return capabilityName; }
    public void setCapabilityName(String capabilityName) { this.capabilityName = capabilityName; }

    public String getCapabilityType() { return capabilityType; }
    public void setCapabilityType(String capabilityType) { this.capabilityType = capabilityType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long addedAt) { this.addedAt = addedAt; }
}