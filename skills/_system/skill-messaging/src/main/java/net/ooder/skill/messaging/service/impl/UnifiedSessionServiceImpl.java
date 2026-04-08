package net.ooder.skill.messaging.service.impl;

import net.ooder.skill.messaging.service.UnifiedSessionService;
import net.ooder.spi.messaging.model.*;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UnifiedSessionServiceImpl implements UnifiedSessionService {
    
    private final Map<String, UnifiedSession> sessionStore = new ConcurrentHashMap<>();
    
    @Override
    public UnifiedSession createSession(CreateSessionRequest request) {
        log.info("[UnifiedSession] Creating session for user: {}", request.getUserId());
        
        UnifiedSession session = new UnifiedSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(request.getUserId());
        session.setSceneGroupId(request.getSceneGroupId());
        session.setSessionType(request.getSessionType());
        session.setTitle(request.getTitle());
        session.setParticipants(request.getParticipants() != null ? request.getParticipants() : new ArrayList<>());
        session.setContext(request.getContext() != null ? request.getContext() : new HashMap<>());
        session.setSettings(new HashMap<>());
        session.setMessageCount(0);
        session.setUnreadCount(0);
        session.setCreateTime(System.currentTimeMillis());
        session.setUpdateTime(System.currentTimeMillis());
        session.setLastActiveTime(System.currentTimeMillis());
        
        sessionStore.put(session.getSessionId(), session);
        
        log.info("[UnifiedSession] Session created successfully: {}", session.getSessionId());
        return session;
    }
    
    @Override
    public UnifiedSession getSession(String sessionId) {
        log.info("[UnifiedSession] Getting session: {}", sessionId);
        return sessionStore.get(sessionId);
    }
    
    @Override
    public List<UnifiedSession> listSessions(String userId, SessionType type, int limit) {
        log.info("[UnifiedSession] Listing sessions for user: {}", userId);
        
        return sessionStore.values().stream()
            .filter(session -> userId == null || userId.equals(session.getUserId()))
            .filter(session -> type == null || type.name().equals(session.getSessionType().name()))
            .sorted((a, b) -> Long.compare(b.getLastActiveTime(), a.getLastActiveTime()))
            .limit(limit)
            .toList();
    }
    
    @Override
    public void deleteSession(String sessionId) {
        log.info("[UnifiedSession] Deleting session: {}", sessionId);
        sessionStore.remove(sessionId);
    }
    
    @Override
    public void addParticipant(String sessionId, Participant participant) {
        log.info("[UnifiedSession] Adding participant: {} to session: {}", participant.getId(), sessionId);
        
        UnifiedSession session = sessionStore.get(sessionId);
        if (session != null) {
            session.getParticipants().add(participant);
            session.setUpdateTime(System.currentTimeMillis());
        }
    }
    
    @Override
    public void removeParticipant(String sessionId, String participantId) {
        log.info("[UnifiedSession] Removing participant: {} from session: {}", participantId, sessionId);
        
        UnifiedSession session = sessionStore.get(sessionId);
        if (session != null) {
            session.getParticipants().removeIf(p -> p.getId().equals(participantId));
            session.setUpdateTime(System.currentTimeMillis());
        }
    }
    
    @Override
    public void updateSessionContext(String sessionId, Map<String, Object> context) {
        log.info("[UnifiedSession] Updating context for session: {}", sessionId);
        
        UnifiedSession session = sessionStore.get(sessionId);
        if (session != null) {
            session.getContext().putAll(context);
            session.setUpdateTime(System.currentTimeMillis());
        }
    }
}
