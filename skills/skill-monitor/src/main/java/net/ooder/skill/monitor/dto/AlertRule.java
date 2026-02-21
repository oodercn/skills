package net.ooder.skill.monitor.dto;

public class AlertRule {
    private String alertId;
    private String alertName;
    private String metricName;
    private String condition;
    private double threshold;
    private String severity;
    private String status;
    private String message;
    private boolean enabled;
    private long createdAt;
    private long lastTriggeredAt;

    public AlertRule() {
        this.status = "inactive";
        this.enabled = true;
        this.createdAt = System.currentTimeMillis();
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastTriggeredAt() {
        return lastTriggeredAt;
    }

    public void setLastTriggeredAt(long lastTriggeredAt) {
        this.lastTriggeredAt = lastTriggeredAt;
    }
}
