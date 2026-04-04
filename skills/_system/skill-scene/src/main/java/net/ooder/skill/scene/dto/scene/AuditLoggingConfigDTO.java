package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class AuditLoggingConfigDTO {
    private boolean enabled;
    private String level;
    private int retentionDays;
    private boolean logInput;
    private boolean logOutput;
    private boolean logErrors;
    private Map<String, Object> filters;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public int getRetentionDays() { return retentionDays; }
    public void setRetentionDays(int retentionDays) { this.retentionDays = retentionDays; }
    public boolean isLogInput() { return logInput; }
    public void setLogInput(boolean logInput) { this.logInput = logInput; }
    public boolean isLogOutput() { return logOutput; }
    public void setLogOutput(boolean logOutput) { this.logOutput = logOutput; }
    public boolean isLogErrors() { return logErrors; }
    public void setLogErrors(boolean logErrors) { this.logErrors = logErrors; }
    public Map<String, Object> getFilters() { return filters; }
    public void setFilters(Map<String, Object> filters) { this.filters = filters; }
}
