package net.ooder.skill.scene.dto;

public class CapabilityDTO {
    private String capId;
    private String sceneId;
    private String name;
    private String description;
    private String type;
    private String category;

    public String getCapId() { return capId; }
    public void setCapId(String capId) { this.capId = capId; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
