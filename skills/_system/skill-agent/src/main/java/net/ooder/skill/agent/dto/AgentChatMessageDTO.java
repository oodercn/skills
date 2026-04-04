package net.ooder.skill.agent.dto;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class AgentChatMessageDTO {
    
    private String messageId;
    private String sceneGroupId;
    
    private String conversationType;
    private String messageType;
    
    private ParticipantInfo fromParticipant;
    private ParticipantInfo toParticipant;
    private List<ParticipantInfo> ccParticipants;
    
    private String title;
    private String content;
    private Map<String, Object> payload;
    
    private int priority;
    private String urgency;
    
    private long createTime;
    private long expireTime;
    private String status;
    
    private List<MessageReaction> reactions;
    private String threadId;
    private String parentMessageId;
    
    private boolean requiresAction;
    private List<MessageAction> availableActions;

    public AgentChatMessageDTO() {
        this.payload = new HashMap<>();
        this.ccParticipants = new ArrayList<>();
        this.reactions = new ArrayList<>();
        this.availableActions = new ArrayList<>();
        this.priority = 5;
        this.urgency = "NORMAL";
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    
    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public ParticipantInfo getFromParticipant() { return fromParticipant; }
    public void setFromParticipant(ParticipantInfo fromParticipant) { this.fromParticipant = fromParticipant; }
    
    public ParticipantInfo getToParticipant() { return toParticipant; }
    public void setToParticipant(ParticipantInfo toParticipant) { this.toParticipant = toParticipant; }
    
    public List<ParticipantInfo> getCcParticipants() { return ccParticipants; }
    public void setCcParticipants(List<ParticipantInfo> ccParticipants) { this.ccParticipants = ccParticipants; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    
    public long getExpireTime() { return expireTime; }
    public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public List<MessageReaction> getReactions() { return reactions; }
    public void setReactions(List<MessageReaction> reactions) { this.reactions = reactions; }
    
    public String getThreadId() { return threadId; }
    public void setThreadId(String threadId) { this.threadId = threadId; }
    
    public String getParentMessageId() { return parentMessageId; }
    public void setParentMessageId(String parentMessageId) { this.parentMessageId = parentMessageId; }
    
    public boolean isRequiresAction() { return requiresAction; }
    public void setRequiresAction(boolean requiresAction) { this.requiresAction = requiresAction; }
    
    public List<MessageAction> getAvailableActions() { return availableActions; }
    public void setAvailableActions(List<MessageAction> availableActions) { this.availableActions = availableActions; }

    public void addPayload(String key, Object value) {
        if (payload == null) {
            payload = new HashMap<>();
        }
        payload.put(key, value);
    }

    public Object getPayloadValue(String key) {
        return payload != null ? payload.get(key) : null;
    }

    public boolean isExpired() {
        return expireTime > 0 && System.currentTimeMillis() > expireTime;
    }

    public static class ParticipantInfo {
        private String id;
        private String name;
        private String type;
        private String role;
        private String avatar;
        private boolean online;

        public ParticipantInfo() {}

        public ParticipantInfo(String id, String name, String type, String role) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.role = role;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        
        public boolean isOnline() { return online; }
        public void setOnline(boolean online) { this.online = online; }
    }

    public static class MessageReaction {
        private String emoji;
        private String userId;
        private long createTime;

        public MessageReaction() {}

        public MessageReaction(String emoji, String userId) {
            this.emoji = emoji;
            this.userId = userId;
            this.createTime = System.currentTimeMillis();
        }

        public String getEmoji() { return emoji; }
        public void setEmoji(String emoji) { this.emoji = emoji; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
    }

    public static class MessageAction {
        private String actionId;
        private String label;
        private String icon;
        private String actionType;
        private Map<String, Object> actionConfig;

        public MessageAction() {
            this.actionConfig = new HashMap<>();
        }

        public MessageAction(String actionId, String label, String icon, String actionType) {
            this();
            this.actionId = actionId;
            this.label = label;
            this.icon = icon;
            this.actionType = actionType;
        }

        public String getActionId() { return actionId; }
        public void setActionId(String actionId) { this.actionId = actionId; }
        
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        
        public String getActionType() { return actionType; }
        public void setActionType(String actionType) { this.actionType = actionType; }
        
        public Map<String, Object> getActionConfig() { return actionConfig; }
        public void setActionConfig(Map<String, Object> actionConfig) { this.actionConfig = actionConfig; }
    }
}
