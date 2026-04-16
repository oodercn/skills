package net.ooder.skill.agent.spi.impl;

import net.ooder.skill.agent.spi.LocalSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Deprecated
public class LocalSessionManagerImpl implements LocalSessionManager {

    private static final Logger log = LoggerFactory.getLogger(LocalSessionManagerImpl.class);
    
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sceneSessionIndex = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userSessionIndex = new ConcurrentHashMap<>();

    @Override
    public String createSession(String sceneGroupId, String userId, SessionType type) {
        String sessionId = UUID.randomUUID().toString();
        
        SessionInfo session = new SessionInfo();
        session.setSessionId(sessionId);
        session.setSceneGroupId(sceneGroupId);
        session.setUserId(userId);
        session.setType(type);
        session.setState("ACTIVE");
        session.setCreateTime(System.currentTimeMillis());
        session.setUpdateTime(System.currentTimeMillis());
        session.setMetadata(new HashMap<>());
        
        sessions.put(sessionId, session);
        sceneSessionIndex.computeIfAbsent(sceneGroupId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        userSessionIndex.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        
        log.info("[createSession] Created session: {} for user: {} in scene: {}", sessionId, userId, sceneGroupId);
        return sessionId;
    }

    @Override
    public SessionInfo getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public List<SessionInfo> getActiveSessionsByScene(String sceneGroupId) {
        Set<String> sessionIds = sceneSessionIndex.getOrDefault(sceneGroupId, Collections.emptySet());
        return sessionIds.stream()
            .map(sessions::get)
            .filter(Objects::nonNull)
            .filter(s -> "ACTIVE".equals(s.getState()))
            .collect(Collectors.toList());
    }

    @Override
    public void closeSession(String sessionId) {
        SessionInfo session = sessions.get(sessionId);
        if (session != null) {
            session.setState("CLOSED");
            session.setUpdateTime(System.currentTimeMillis());
            log.info("[closeSession] Closed session: {}", sessionId);
        }
    }

    @Override
    public void updateSessionState(String sessionId, String state) {
        SessionInfo session = sessions.get(sessionId);
        if (session != null) {
            session.setState(state);
            session.setUpdateTime(System.currentTimeMillis());
            log.debug("[updateSessionState] Updated session: {} to state: {}", sessionId, state);
        }
    }
}
