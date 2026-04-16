package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class CoordinationRuleDTO {
    private String ruleId;
    private String name;
    private String sourceScene;
    private String targetScene;
    private String trigger;
    private String condition;
    private String action;
    private int priority;
    private boolean enabled;
    private Map<String, Object> config;

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSourceScene() { return sourceScene; }
    public void setSourceScene(String sourceScene) { this.sourceScene = sourceScene; }
    public String getTargetScene() { return targetScene; }
    public void setTargetScene(String targetScene) { this.targetScene = targetScene; }
    public String getTrigger() { return trigger; }
    public void setTrigger(String trigger) { this.trigger = trigger; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
}
