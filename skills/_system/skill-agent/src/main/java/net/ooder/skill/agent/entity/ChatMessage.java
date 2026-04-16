package net.ooder.skill.agent.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_chat_message", indexes = {
    @Index(name = "idx_msg_scene_group", columnList = "sceneGroupId"),
    @Index(name = "idx_msg_create_time", columnList = "createTime"),
    @Index(name = "idx_msg_type", columnList = "messageType"),
    @Index(name = "idx_msg_sender", columnList = "senderId"),
    @Index(name = "idx_msg_tenant", columnList = "tenantId")
})
public class ChatMessage {

    @Id
    @Column(length = 36)
    private String id;

    @Column(length = 36, nullable = false)
    private String sceneGroupId;

    @Column(length = 20)
    private String messageType;

    @Column(length = 36)
    private String senderId;

    @Column(length = 100)
    private String senderName;

    @Column(length = 20)
    private String senderType;

    @Column(length = 36)
    private String receiverId;

    @Column(length = 100)
    private String receiverName;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 20)
    private String status;

    @Column
    private Integer priority;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column(columnDefinition = "TEXT")
    private String attachmentsJson;

    @Column(columnDefinition = "TEXT")
    private String reactionsJson;

    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    @Column(length = 64)
    private String tenantId;

    public ChatMessage() {
        this.status = "SENT";
        this.priority = 5;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PrePersist
    protected void onPersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public String getAttachmentsJson() { return attachmentsJson; }
    public void setAttachmentsJson(String attachmentsJson) { this.attachmentsJson = attachmentsJson; }

    public String getReactionsJson() { return reactionsJson; }
    public void setReactionsJson(String reactionsJson) { this.reactionsJson = reactionsJson; }

    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    @PreUpdate
    protected void onUpdate() { this.updateTime = LocalDateTime.now(); }

    public static class Builder {
        private final ChatMessage msg = new ChatMessage();

        public Builder id(String id) { msg.id = id; return this; }
        public Builder sceneGroupId(String sceneGroupId) { msg.sceneGroupId = sceneGroupId; return this; }
        public Builder messageType(String messageType) { msg.messageType = messageType; return this; }
        public Builder senderId(String senderId) { msg.senderId = senderId; return this; }
        public Builder senderName(String senderName) { msg.senderName = senderName; return this; }
        public Builder senderType(String senderType) { msg.senderType = senderType; return this; }
        public Builder receiverId(String receiverId) { msg.receiverId = receiverId; return this; }
        public Builder receiverName(String receiverName) { msg.receiverName = receiverName; return this; }
        public Builder content(String content) { msg.content = content; return this; }
        public Builder status(String status) { msg.status = status; return this; }
        public Builder priority(Integer priority) { msg.priority = priority; return this; }
        public Builder attachmentsJson(String json) { msg.attachmentsJson = json; return this; }
        public Builder reactionsJson(String json) { msg.reactionsJson = json; return this; }
        public Builder metadataJson(String json) { msg.metadataJson = json; return this; }
        public Builder tenantId(String tenantId) { msg.tenantId = tenantId; return this; }

        public ChatMessage build() { return msg; }
    }
}
