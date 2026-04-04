package net.ooder.skill.agent.dto;

import java.util.List;
import java.util.Map;

public class AgentAlertConfigDTO {
    private Long id;
    private String name;
    private String agentId;
    private String clusterId;
    private String alertType;
    private String condition;
    private double threshold;
    private int durationSeconds;
    private int severity;
    private boolean enabled;
    private List<String> notifyChannels;
    private List<String> notifyTargets;
    private Map<String, Object> extendedConfig;
    private long createdAt;
    private long updatedAt;

    public static final String ALERT_TYPE_CPU = "cpu_usage";
    public static final String ALERT_TYPE_MEMORY = "memory_usage";
    public static final String ALERT_TYPE_LOAD = "load_percentage";
    public static final String ALERT_TYPE_RESPONSE_TIME = "response_time";
    public static final String ALERT_TYPE_ERROR_RATE = "error_rate";
    public static final String ALERT_TYPE_OFFLINE = "offline";
    public static final String ALERT_TYPE_HEARTBEAT = "heartbeat_timeout";

    public static final int SEVERITY_INFO = 1;
    public static final int SEVERITY_WARNING = 2;
    public static final int SEVERITY_CRITICAL = 3;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    public String getClusterId() { return clusterId; }
    public void setClusterId(String clusterId) { this.clusterId = clusterId; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public List<String> getNotifyChannels() { return notifyChannels; }
    public void setNotifyChannels(List<String> notifyChannels) { this.notifyChannels = notifyChannels; }
    public List<String> getNotifyTargets() { return notifyTargets; }
    public void setNotifyTargets(List<String> notifyTargets) { this.notifyTargets = notifyTargets; }
    public Map<String, Object> getExtendedConfig() { return extendedConfig; }
    public void setExtendedConfig(Map<String, Object> extendedConfig) { this.extendedConfig = extendedConfig; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
