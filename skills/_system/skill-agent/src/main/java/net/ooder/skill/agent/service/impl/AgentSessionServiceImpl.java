package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.dto.AgentSessionDTO;
import net.ooder.skill.agent.dto.AgentRegistrationDTO;
import net.ooder.skill.agent.service.AgentSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentSessionServiceImpl implements AgentSessionService {

    private static final Logger log = LoggerFactory.getLogger(AgentSessionServiceImpl.class);

    private Map<String, AgentSessionDTO> sessionStore = new ConcurrentHashMap<>();
    private Map<String, String> secretKeyStore = new ConcurrentHashMap<>();
    private Map<String, AgentSessionDTO> tokenStore = new ConcurrentHashMap<>();
    
    private long sessionTimeout = 3600;

    @Override
    public AgentSessionDTO register(AgentRegistrationDTO registration) {
        log.info("[register] Registering agent: {}", registration.getAgentId());
        
        if (sessionStore.containsKey(registration.getAgentId())) {
            log.warn("[register] Agent already registered: {}", registration.getAgentId());
            return null;
        }
        
        AgentSessionDTO session = new AgentSessionDTO();
        session.setAgentId(registration.getAgentId());
        session.setAgentName(registration.getAgentName());
        session.setAgentType(registration.getAgentType());
        session.setIpAddress(registration.getIpAddress());
        session.setPort(registration.getPort());
        session.setSceneGroupId(registration.getSceneGroupId());
        session.setRole(registration.getRole());
        session.setStatus("ONLINE");
        session.setLoginTime(System.currentTimeMillis());
        session.setLastHeartbeat(System.currentTimeMillis());
        session.setExpireTime(System.currentTimeMillis() + sessionTimeout * 1000);
        
        String sessionToken = UUID.randomUUID().toString();
        session.setSessionToken(sessionToken);
        
        String secretKey = registration.getSecretKey() != null ? 
            registration.getSecretKey() : UUID.randomUUID().toString();
        session.setSecretKey(secretKey);
        
        sessionStore.put(session.getAgentId(), session);
        secretKeyStore.put(session.getAgentId(), secretKey);
        tokenStore.put(sessionToken, session);
        
        log.info("[register] Agent registered successfully: {}", session.getAgentId());
        
        return session;
    }

    @Override
    public AgentSessionDTO login(String agentId, String secretKey) {
        log.info("[login] Agent login: {}", agentId);
        
        String storedSecretKey = secretKeyStore.get(agentId);
        if (storedSecretKey == null || !storedSecretKey.equals(secretKey)) {
            log.warn("[login] Invalid credentials for agent: {}", agentId);
            return null;
        }
        
        AgentSessionDTO session = sessionStore.get(agentId);
        if (session == null) {
            log.warn("[login] Session not found for agent: {}", agentId);
            return null;
        }
        
        if (session.isExpired()) {
            log.warn("[login] Session expired for agent: {}", agentId);
            sessionStore.remove(agentId);
            tokenStore.remove(session.getSessionToken());
            return null;
        }
        
        String newToken = UUID.randomUUID().toString();
        tokenStore.remove(session.getSessionToken());
        session.setSessionToken(newToken);
        tokenStore.put(newToken, session);
        
        session.setStatus("ONLINE");
        session.setLastHeartbeat(System.currentTimeMillis());
        session.setExpireTime(System.currentTimeMillis() + sessionTimeout * 1000);
        
        log.info("[login] Agent logged in successfully: {}", agentId);
        
        return session;
    }

    @Override
    public void logout(String agentId) {
        log.info("[logout] Agent logout: {}", agentId);
        
        AgentSessionDTO session = sessionStore.get(agentId);
        if (session != null) {
            session.setStatus("OFFLINE");
            tokenStore.remove(session.getSessionToken());
        }
    }

    @Override
    public void heartbeat(String agentId) {
        AgentSessionDTO session = sessionStore.get(agentId);
        if (session != null) {
            session.setLastHeartbeat(System.currentTimeMillis());
            session.setExpireTime(System.currentTimeMillis() + sessionTimeout * 1000);
            session.setStatus("ONLINE");
        }
    }

    @Override
    public void updateStatus(String agentId, String status) {
        AgentSessionDTO session = sessionStore.get(agentId);
        if (session != null) {
            session.setStatus(status);
        }
    }

    @Override
    public AgentSessionDTO getSession(String agentId) {
        AgentSessionDTO session = sessionStore.get(agentId);
        if (session != null && session.isExpired()) {
            return null;
        }
        return session;
    }

    @Override
    public AgentSessionDTO getSessionByToken(String sessionToken) {
        AgentSessionDTO session = tokenStore.get(sessionToken);
        if (session != null && session.isExpired()) {
            return null;
        }
        return session;
    }

    @Override
    public boolean isValidToken(String sessionToken) {
        AgentSessionDTO session = tokenStore.get(sessionToken);
        return session != null && !session.isExpired();
    }

    @Override
    public List<AgentSessionDTO> getActiveSessions() {
        return sessionStore.values().stream()
            .filter(s -> !s.isExpired())
            .filter(s -> "ONLINE".equals(s.getStatus()) || "BUSY".equals(s.getStatus()))
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<AgentSessionDTO> getSessionsByScene(String sceneGroupId) {
        return sessionStore.values().stream()
            .filter(s -> sceneGroupId.equals(s.getSceneGroupId()))
            .filter(s -> !s.isExpired())
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void cleanExpiredSessions() {
        log.info("[cleanExpiredSessions] Cleaning expired sessions");
        
        List<String> expiredAgents = sessionStore.values().stream()
            .filter(AgentSessionDTO::isExpired)
            .map(AgentSessionDTO::getAgentId)
            .collect(java.util.stream.Collectors.toList());
        
        for (String agentId : expiredAgents) {
            AgentSessionDTO session = sessionStore.remove(agentId);
            if (session != null) {
                tokenStore.remove(session.getSessionToken());
            }
        }
        
        log.info("[cleanExpiredSessions] Cleaned {} expired sessions", expiredAgents.size());
    }

    @Override
    public void setSessionTimeout(long timeoutSeconds) {
        this.sessionTimeout = timeoutSeconds;
        log.info("[setSessionTimeout] Session timeout set to {} seconds", timeoutSeconds);
    }

    @Override
    public long getSessionTimeout() {
        return sessionTimeout;
    }

    @Override
    public void refreshSession(String agentId) {
        AgentSessionDTO session = sessionStore.get(agentId);
        if (session != null) {
            session.setExpireTime(System.currentTimeMillis() + sessionTimeout * 1000);
            session.setLastHeartbeat(System.currentTimeMillis());
        }
    }

    @Override
    public void terminateSession(String agentId) {
        log.info("[terminateSession] Terminating session: {}", agentId);
        
        AgentSessionDTO session = sessionStore.remove(agentId);
        if (session != null) {
            tokenStore.remove(session.getSessionToken());
        }
    }

    @Override
    public void terminateAllSessions() {
        log.info("[terminateAllSessions] Terminating all sessions");
        
        sessionStore.clear();
        tokenStore.clear();
    }
}
