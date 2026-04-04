package net.ooder.skill.agent.service;

import java.util.List;
import java.util.Map;

public interface AgentHeartbeatMonitor {
    
    void start();
    
    void stop();
    
    boolean isRunning();
    
    void registerAgent(String agentId, HeartbeatConfig config);
    
    void unregisterAgent(String agentId);
    
    void updateHeartbeat(String agentId);
    
    HeartbeatStatus getStatus(String agentId);
    
    List<HeartbeatStatus> getAllStatuses();
    
    List<String> getOfflineAgents();
    
    void setOfflineThreshold(int seconds);
    
    void setCheckInterval(int seconds);
    
    void addHeartbeatListener(HeartbeatListener listener);
    
    void removeHeartbeatListener(HeartbeatListener listener);
    
    class HeartbeatConfig {
        private int intervalSeconds;
        private int timeoutSeconds;
        private int offlineThreshold;
        private String deviceType;
        
        public int getIntervalSeconds() { return intervalSeconds; }
        public void setIntervalSeconds(int intervalSeconds) { this.intervalSeconds = intervalSeconds; }
        public int getTimeoutSeconds() { return timeoutSeconds; }
        public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
        public int getOfflineThreshold() { return offlineThreshold; }
        public void setOfflineThreshold(int offlineThreshold) { this.offlineThreshold = offlineThreshold; }
        public String getDeviceType() { return deviceType; }
        public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    }
    
    class HeartbeatStatus {
        private String agentId;
        private long lastHeartbeat;
        private long currentTime;
        private boolean online;
        private int missedCount;
        private String status;
        
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public long getLastHeartbeat() { return lastHeartbeat; }
        public void setLastHeartbeat(long lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
        public long getCurrentTime() { return currentTime; }
        public void setCurrentTime(long currentTime) { this.currentTime = currentTime; }
        public boolean isOnline() { return online; }
        public void setOnline(boolean online) { this.online = online; }
        public int getMissedCount() { return missedCount; }
        public void setMissedCount(int missedCount) { this.missedCount = missedCount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    interface HeartbeatListener {
        void onHeartbeatMissed(String agentId, int missedCount);
        void onAgentOffline(String agentId);
        void onAgentOnline(String agentId);
    }
}
