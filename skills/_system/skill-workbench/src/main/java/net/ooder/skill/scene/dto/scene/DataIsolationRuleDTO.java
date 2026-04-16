package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class DataIsolationRuleDTO {
    private String ruleId;
    private String name;
    private String description;
    private String scope;
    private String level;
    private Map<String, Object> constraints;
    private boolean enabled;

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public Map<String, Object> getConstraints() { return constraints; }
    public void setConstraints(Map<String, Object> constraints) { this.constraints = constraints; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
