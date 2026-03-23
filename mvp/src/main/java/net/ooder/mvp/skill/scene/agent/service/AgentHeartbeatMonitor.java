package net.ooder.mvp.skill.scene.agent.service;

import net.ooder.mvp.skill.scene.agent.dto.AgentSessionDTO;

import java.util.List;
import java.util.Map;

public interface AgentHeartbeatMonitor {
    
    void startMonitoring();
    
    void stopMonitoring();
    
    void checkHeartbeats();
    
    List<String> getTimedOutAgents();
    
    Map<String, Long> getAgentLastHeartbeatTimes();
    
    boolean isAgentTimedOut(String agentId);
    
    long getTimeoutThreshold();
    
    void setTimeoutThreshold(long thresholdSeconds);
}
