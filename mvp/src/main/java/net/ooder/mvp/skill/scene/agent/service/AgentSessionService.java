package net.ooder.mvp.skill.scene.agent.service;

import net.ooder.mvp.skill.scene.agent.dto.AgentSessionDTO;
import net.ooder.mvp.skill.scene.agent.dto.AgentRegistrationDTO;

import java.util.List;

public interface AgentSessionService {
    
    AgentSessionDTO register(AgentRegistrationDTO registration);
    
    AgentSessionDTO login(String agentId, String secretKey);
    
    void logout(String agentId);
    
    AgentSessionDTO getSession(String agentId);
    
    AgentSessionDTO getSessionByToken(String sessionToken);
    
    boolean isValid(String agentId);
    
    boolean isValidToken(String sessionToken);
    
    void heartbeat(String agentId);
    
    void updateStatus(String agentId, String status);
    
    List<AgentSessionDTO> getActiveSessions();
    
    List<AgentSessionDTO> getSessionsByScene(String sceneGroupId);
    
    int cleanupExpiredSessions();
    
    int getSessionTimeout();
    
    void setSessionTimeout(int timeoutSeconds);
    
    void updateSession(AgentSessionDTO session);
}
