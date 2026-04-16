package net.ooder.skill.scenes.dto;

public class SceneGroupCapabilityDTO {
    
    private String capId;
    private String sceneGroupId;
    private String name;
    private String type;
    private String status;
    private long bindTime;
    private String description;

    public String getCapId() { return capId; }
    public void setCapId(String capId) { this.capId = capId; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getBindTime() { return bindTime; }
    public void setBindTime(long bindTime) { this.bindTime = bindTime; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
