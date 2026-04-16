package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class CrossOrgRuleDTO {
    private String ruleId;
    private String name;
    private String sourceOrg;
    private String targetOrg;
    private String permission;
    private Map<String, Object> constraints;
    private boolean enabled;
    private long createTime;

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSourceOrg() { return sourceOrg; }
    public void setSourceOrg(String sourceOrg) { this.sourceOrg = sourceOrg; }
    public String getTargetOrg() { return targetOrg; }
    public void setTargetOrg(String targetOrg) { this.targetOrg = targetOrg; }
    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
    public Map<String, Object> getConstraints() { return constraints; }
    public void setConstraints(Map<String, Object> constraints) { this.constraints = constraints; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
}
