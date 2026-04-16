package net.ooder.skill.scene.dto.scene;

public class TriggerDefinitionDTO {
    private String id;
    private String type;
    private String name;
    private String description;
    private String condition;
    private String action;
    private boolean enabled;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
