package net.ooder.skill.agent.service;

import java.util.List;
import java.util.Map;

public interface AgentAlertMonitor {
    
    void startMonitoring();
    
    void stopMonitoring();
    
    boolean isMonitoring();
    
    void checkAgentAlerts(String agentId);
    
    void checkAllAgents();
    
    List<AlertEvent> getActiveAlerts();
    
    List<AlertEvent> getAlertHistory(String agentId, long startTime, long endTime);
    
    void acknowledgeAlert(String alertId);
    
    void configureAlertThreshold(String agentId, String metricType, double threshold);
    
    class AlertEvent {
        private String alertId;
        private String agentId;
        private String alertType;
        private String severity;
        private String message;
        private double value;
        private double threshold;
        private long timestamp;
        private boolean acknowledged;
        
        public String getAlertId() { return alertId; }
        public void setAlertId(String alertId) { this.alertId = alertId; }
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getAlertType() { return alertType; }
        public void setAlertType(String alertType) { this.alertType = alertType; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
        public double getThreshold() { return threshold; }
        public void setThreshold(double threshold) { this.threshold = threshold; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public boolean isAcknowledged() { return acknowledged; }
        public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
    }
}
