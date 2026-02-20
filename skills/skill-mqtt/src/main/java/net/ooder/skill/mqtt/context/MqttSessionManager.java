package net.ooder.skill.mqtt.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MQTT会话管理�? */
public class MqttSessionManager {
    
    private static final MqttSessionManager INSTANCE = new MqttSessionManager();
    
    private final Map<String, MqttContext> sessions = new ConcurrentHashMap<String, MqttContext>();
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<String, String>();
    
    private MqttSessionManager() {
    }
    
    public static MqttSessionManager getInstance() {
        return INSTANCE;
    }
    
    public MqttContext createSession(String sessionId, String clientId) {
        MqttContext context = new MqttContext(sessionId, clientId);
        sessions.put(sessionId, context);
        return context;
    }
    
    public MqttContext getSession(String sessionId) {
        return sessions.get(sessionId);
    }
    
    public MqttContext getSessionByClientId(String clientId) {
        for (MqttContext context : sessions.values()) {
            if (clientId.equals(context.getClientId())) {
                return context;
            }
        }
        return null;
    }
    
    public MqttContext getSessionByUserId(String userId) {
        String sessionId = userSessionMap.get(userId);
        if (sessionId != null) {
            return sessions.get(sessionId);
        }
        return null;
    }
    
    public void bindUser(String sessionId, String userId) {
        MqttContext context = sessions.get(sessionId);
        if (context != null) {
            context.setUserId(userId);
            userSessionMap.put(userId, sessionId);
        }
    }
    
    public void removeSession(String sessionId) {
        MqttContext context = sessions.remove(sessionId);
        if (context != null && context.getUserId() != null) {
            userSessionMap.remove(context.getUserId());
        }
    }
    
    public boolean hasSession(String sessionId) {
        return sessions.containsKey(sessionId);
    }
    
    public int getSessionCount() {
        return sessions.size();
    }
    
    public Map<String, MqttContext> getAllSessions() {
        return sessions;
    }
    
    public void updateActiveTime(String sessionId) {
        MqttContext context = sessions.get(sessionId);
        if (context != null) {
            context.updateActiveTime();
        }
    }
    
    public void clearAll() {
        sessions.clear();
        userSessionMap.clear();
    }
}
