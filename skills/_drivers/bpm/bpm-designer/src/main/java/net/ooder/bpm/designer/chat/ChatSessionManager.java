package net.ooder.bpm.designer.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionManager {
    
    private static final Logger log = LoggerFactory.getLogger(ChatSessionManager.class);
    
    private static final long SESSION_TIMEOUT_MS = 30 * 60 * 1000;
    private static final int MAX_SESSIONS = 100;
    
    private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();
    
    public ChatSession getOrCreateSession(String sessionId, String userId, String skillId) {
        cleanupExpiredSessions();
        
        ChatSession session = sessions.get(sessionId);
        if (session == null) {
            if (sessions.size() >= MAX_SESSIONS) {
                evictOldestSession();
            }
            session = new ChatSession(sessionId, userId, skillId);
            sessions.put(sessionId, session);
            log.info("Created new chat session: {} for user: {}", sessionId, userId);
        }
        
        return session;
    }
    
    public ChatSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
    
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
        log.info("Removed chat session: {}", sessionId);
    }
    
    public void updateContext(String sessionId, Map<String, Object> context) {
        ChatSession session = sessions.get(sessionId);
        if (session != null) {
            session.setContext(context);
        }
    }
    
    private void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        sessions.entrySet().removeIf(entry -> {
            boolean expired = (now - entry.getValue().getLastActiveAt()) > SESSION_TIMEOUT_MS;
            if (expired) {
                log.info("Expired session: {}", entry.getKey());
            }
            return expired;
        });
    }
    
    private void evictOldestSession() {
        String oldestKey = null;
        long oldestTime = Long.MAX_VALUE;
        
        for (Map.Entry<String, ChatSession> entry : sessions.entrySet()) {
            if (entry.getValue().getLastActiveAt() < oldestTime) {
                oldestTime = entry.getValue().getLastActiveAt();
                oldestKey = entry.getKey();
            }
        }
        
        if (oldestKey != null) {
            sessions.remove(oldestKey);
            log.info("Evicted oldest session: {}", oldestKey);
        }
    }
    
    public int getActiveSessionCount() {
        return sessions.size();
    }
}
