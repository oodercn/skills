package net.ooder.skill.agent.service;

import net.ooder.skill.agent.dto.FailoverRecordDTO;

import java.util.List;
import java.util.Map;

public interface FailoverService {
    
    void enableFailover(boolean enabled);
    
    boolean isFailoverEnabled();
    
    void configureFailover(FailoverConfig config);
    
    FailoverConfig getFailoverConfig();
    
    String initiateFailover(String failedAgentId, String sceneGroupId);
    
    FailoverRecordDTO getFailoverRecord(String recordId);
    
    List<FailoverRecordDTO> getFailoverHistory(String agentId, long startTime, long endTime);
    
    List<FailoverRecordDTO> getActiveFailovers();
    
    void completeFailover(String recordId);
    
    void cancelFailover(String recordId);
    
    String selectFailoverTarget(String failedAgentId, String sceneGroupId);
    
    void migrateAgentState(String fromAgentId, String toAgentId, String sceneGroupId);
    
    void notifyFailover(String recordId, List<String> notifyTargets);
    
    void setAutoFailover(boolean enabled);
    
    boolean isAutoFailoverEnabled();
    
    void setMaxRetryAttempts(int attempts);
    
    int getMaxRetryAttempts();
    
    class FailoverConfig {
        private boolean enabled;
        private boolean autoFailover;
        private int maxRetryAttempts;
        private long retryIntervalMs;
        private long healthCheckIntervalMs;
        private int offlineThreshold;
        private String selectionStrategy;
        private List<String> excludedAgents;
        private Map<String, Object> extendedConfig;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public boolean isAutoFailover() { return autoFailover; }
        public void setAutoFailover(boolean autoFailover) { this.autoFailover = autoFailover; }
        public int getMaxRetryAttempts() { return maxRetryAttempts; }
        public void setMaxRetryAttempts(int maxRetryAttempts) { this.maxRetryAttempts = maxRetryAttempts; }
        public long getRetryIntervalMs() { return retryIntervalMs; }
        public void setRetryIntervalMs(long retryIntervalMs) { this.retryIntervalMs = retryIntervalMs; }
        public long getHealthCheckIntervalMs() { return healthCheckIntervalMs; }
        public void setHealthCheckIntervalMs(long healthCheckIntervalMs) { this.healthCheckIntervalMs = healthCheckIntervalMs; }
        public int getOfflineThreshold() { return offlineThreshold; }
        public void setOfflineThreshold(int offlineThreshold) { this.offlineThreshold = offlineThreshold; }
        public String getSelectionStrategy() { return selectionStrategy; }
        public void setSelectionStrategy(String selectionStrategy) { this.selectionStrategy = selectionStrategy; }
        public List<String> getExcludedAgents() { return excludedAgents; }
        public void setExcludedAgents(List<String> excludedAgents) { this.excludedAgents = excludedAgents; }
        public Map<String, Object> getExtendedConfig() { return extendedConfig; }
        public void setExtendedConfig(Map<String, Object> extendedConfig) { this.extendedConfig = extendedConfig; }
    }
}
