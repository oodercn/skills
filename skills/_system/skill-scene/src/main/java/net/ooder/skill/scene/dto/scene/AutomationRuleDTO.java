package net.ooder.skill.scene.dto.scene;

import java.util.List;
import java.util.Map;

public class AutomationRuleDTO {
    private String ruleId;
    private String name;
    private String description;
    private String trigger;
    private String condition;
    private List<ActionDefDTO> actions;
    private boolean enabled;
    private int priority;
    private Map<String, Object> metadata;

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTrigger() { return trigger; }
    public void setTrigger(String trigger) { this.trigger = trigger; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public List<ActionDefDTO> getActions() { return actions; }
    public void setActions(List<ActionDefDTO> actions) { this.actions = actions; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
