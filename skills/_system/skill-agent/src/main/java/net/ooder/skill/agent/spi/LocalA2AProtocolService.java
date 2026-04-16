package net.ooder.skill.agent.spi;

import java.util.List;
import java.util.Map;

@Deprecated
public interface LocalA2AProtocolService {

    void sendA2AMessage(A2AMessage message);
    
    void broadcastToAgents(String sceneGroupId, A2AMessage message);
    
    void registerHandler(String messageType, MessageHandler handler);
    
    void unregisterHandler(String messageType, MessageHandler handler);
    
    void routeMessage(A2AMessage message);
    
    List<A2AMessage> getMessageHistory(String sceneGroupId, int limit);
    
    interface MessageHandler {
        void handle(A2AMessage message);
    }
    
    class A2AMessage {
        private String messageId;
        private String sceneGroupId;
        private String fromAgentId;
        private String fromAgentName;
        private String toAgentId;
        private String toAgentName;
        private String messageType;
        private String content;
        private Map<String, Object> payload;
        private long createTime;
        private String status;
        private int priority;
        
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
        public String getFromAgentId() { return fromAgentId; }
        public void setFromAgentId(String fromAgentId) { this.fromAgentId = fromAgentId; }
        public String getFromAgentName() { return fromAgentName; }
        public void setFromAgentName(String fromAgentName) { this.fromAgentName = fromAgentName; }
        public String getToAgentId() { return toAgentId; }
        public void setToAgentId(String toAgentId) { this.toAgentId = toAgentId; }
        public String getToAgentName() { return toAgentName; }
        public void setToAgentName(String toAgentName) { this.toAgentName = toAgentName; }
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Map<String, Object> getPayload() { return payload; }
        public void setPayload(Map<String, Object> payload) { this.payload = payload; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
    }
}
