package net.ooder.skill.scene.dto.scene;

import java.util.List;
import java.util.Map;

public class TriggerDefDTO {
    private String triggerId;
    private String name;
    private String type;
    private String description;
    private String condition;
    private List<ActionDefDTO> actions;
    private Map<String, Object> config;
    private boolean enabled;

    public String getTriggerId() { return triggerId; }
    public void setTriggerId(String triggerId) { this.triggerId = triggerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public List<ActionDefDTO> getActions() { return actions; }
    public void setActions(List<ActionDefDTO> actions) { this.actions = actions; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
