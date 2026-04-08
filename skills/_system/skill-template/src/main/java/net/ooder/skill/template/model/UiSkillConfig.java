package net.ooder.skill.template.model;

import java.util.Map;

public class UiSkillConfig {
    private String skillId;
    private String uiPath;
    private Map<String, Object> config;

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getUiPath() { return uiPath; }
    public void setUiPath(String uiPath) { this.uiPath = uiPath; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
}
