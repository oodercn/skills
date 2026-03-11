package net.ooder.skill.conversation.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
public class Conversation {
    
    @Id
    @Column(length = 64)
    private String id;
    
    @Column(name = "user_id", length = 64, nullable = false)
    private String userId;
    
    @Column(name = "scene_id", length = 64)
    private String sceneId;
    
    @Column(length = 255)
    private String title;
    
    @Column(name = "system_prompt", columnDefinition = "TEXT")
    private String systemPrompt;
    
    @Column(name = "provider", length = 32)
    private String provider;
    
    @Column(length = 64)
    private String model;
    
    @Column(name = "total_tokens")
    private Integer totalTokens = 0;
    
    @Column(name = "message_count")
    private Integer messageCount = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private ConversationStatus status = ConversationStatus.ACTIVE;
    
    @Column(name = "create_time")
    private Long createTime;
    
    @Column(name = "update_time")
    private Long updateTime;
    
    @Column(name = "expire_time")
    private Long expireTime;
    
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createTime ASC")
    private List<Message> messages = new ArrayList<>();
    
    public enum ConversationStatus {
        ACTIVE,
        ARCHIVED,
        DELETED
    }
    
    public Conversation() {
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSystemPrompt() {
        return systemPrompt;
    }
    
    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Integer getTotalTokens() {
        return totalTokens;
    }
    
    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
    }
    
    public Integer getMessageCount() {
        return messageCount;
    }
    
    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }
    
    public ConversationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ConversationStatus status) {
        this.status = status;
    }
    
    public Long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    
    public Long getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
    
    public Long getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
    
    public List<Message> getMessages() {
        return messages;
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    public void addMessage(Message message) {
        messages.add(message);
        message.setConversation(this);
        this.messageCount = messages.size();
        this.updateTime = System.currentTimeMillis();
    }
    
    public void incrementTokens(int tokens) {
        this.totalTokens = (this.totalTokens == null ? 0 : this.totalTokens) + tokens;
    }
}
