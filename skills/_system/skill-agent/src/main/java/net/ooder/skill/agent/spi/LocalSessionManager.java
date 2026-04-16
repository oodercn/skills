package net.ooder.skill.agent.spi;

import java.util.List;
import java.util.Map;

@Deprecated
public interface LocalSessionManager {

    String createSession(String sceneGroupId, String userId, SessionType type);
    
    SessionInfo getSession(String sessionId);
    
    List<SessionInfo> getActiveSessionsByScene(String sceneGroupId);
    
    void closeSession(String sessionId);
    
    void updateSessionState(String sessionId, String state);
    
    enum SessionType {
        USER, AGENT, SCENE, CONVERSATION
    }
    
    class SessionInfo {
        private String sessionId;
        private String sceneGroupId;
        private String userId;
        private SessionType type;
        private String state;
        private long createTime;
        private long updateTime;
        private Map<String, Object> metadata;
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public SessionType getType() { return type; }
        public void setType(SessionType type) { this.type = type; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public long getUpdateTime() { return updateTime; }
        public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
}
