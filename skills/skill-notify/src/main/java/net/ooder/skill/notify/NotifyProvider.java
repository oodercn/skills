package net.ooder.skill.notify;

import java.util.List;
import java.util.Map;

public interface NotifyProvider {
    
    String getProviderType();
    
    List<String> getSupportedChannels();
    
    NotifyResult send(NotifyRequest request);
    
    NotifyResult sendBatch(List<NotifyRequest> requests);
    
    NotifyResult sendTemplate(String templateId, Map<String, Object> params, List<String> recipients);
    
    TemplateResult createTemplate(TemplateRequest request);
    
    TemplateResult getTemplate(String templateId);
    
    List<TemplateResult> listTemplates();
    
    boolean deleteTemplate(String templateId);
    
    SubscriptionResult subscribe(String userId, String channel, Map<String, Object> config);
    
    boolean unsubscribe(String userId, String channel);
    
    List<SubscriptionResult> getSubscriptions(String userId);
    
    public static class NotifyRequest {
        private String notifyId;
        private String channel;
        private List<String> recipients;
        private String subject;
        private String content;
        private Map<String, Object> data;
        private int priority;
        private long expireAt;
        
        public String getNotifyId() { return notifyId; }
        public void setNotifyId(String notifyId) { this.notifyId = notifyId; }
        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }
        public List<String> getRecipients() { return recipients; }
        public void setRecipients(List<String> recipients) { this.recipients = recipients; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public long getExpireAt() { return expireAt; }
        public void setExpireAt(long expireAt) { this.expireAt = expireAt; }
    }
    
    public static class NotifyResult {
        private boolean success;
        private String notifyId;
        private String status;
        private List<String> sentTo;
        private List<String> failed;
        private String errorCode;
        private String errorMessage;
        private long sentAt;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getNotifyId() { return notifyId; }
        public void setNotifyId(String notifyId) { this.notifyId = notifyId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public List<String> getSentTo() { return sentTo; }
        public void setSentTo(List<String> sentTo) { this.sentTo = sentTo; }
        public List<String> getFailed() { return failed; }
        public void setFailed(List<String> failed) { this.failed = failed; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public long getSentAt() { return sentAt; }
        public void setSentAt(long sentAt) { this.sentAt = sentAt; }
    }
    
    public static class TemplateRequest {
        private String templateId;
        private String name;
        private String channel;
        private String subject;
        private String content;
        private Map<String, String> variables;
        
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Map<String, String> getVariables() { return variables; }
        public void setVariables(Map<String, String> variables) { this.variables = variables; }
    }
    
    public static class TemplateResult {
        private String templateId;
        private String name;
        private String channel;
        private String subject;
        private String content;
        private long createdAt;
        
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }
    
    public static class SubscriptionResult {
        private String userId;
        private String channel;
        private boolean enabled;
        private Map<String, Object> config;
        private long subscribedAt;
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        public long getSubscribedAt() { return subscribedAt; }
        public void setSubscribedAt(long subscribedAt) { this.subscribedAt = subscribedAt; }
    }
}
