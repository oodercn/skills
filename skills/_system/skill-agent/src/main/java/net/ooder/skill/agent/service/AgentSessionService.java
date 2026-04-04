package net.ooder.skill.agent.service;

import net.ooder.skill.agent.dto.AgentSessionDTO;
import net.ooder.skill.agent.dto.AgentRegistrationDTO;

import java.util.List;

public interface AgentSessionService {
    
    AgentSessionDTO register(AgentRegistrationDTO registration);
    
    AgentSessionDTO login(String agentId, String secretKey);
    
    void logout(String agentId);
    
    void heartbeat(String agentId);
    
    void updateStatus(String agentId, String status);
    
    AgentSessionDTO getSession(String agentId);
    
    AgentSessionDTO getSessionByToken(String sessionToken);
    
    boolean isValidToken(String sessionToken);
    
    List<AgentSessionDTO> getActiveSessions();
    
    List<AgentSessionDTO> getSessionsByScene(String sceneGroupId);
    
    void cleanExpiredSessions();
    
    void setSessionTimeout(long timeoutSeconds);
    
    long getSessionTimeout();
    
    void refreshSession(String agentId);
    
    void terminateSession(String agentId);
    
    void terminateAllSessions();
}
