package net.ooder.skill.email;

import java.util.List;
import java.util.Map;

public interface EmailProvider {
    
    String getProviderType();
    
    EmailResult send(EmailRequest request);
    
    EmailResult sendBatch(List<EmailRequest> requests);
    
    EmailResult sendTemplate(String templateId, Map<String, Object> params, List<String> recipients);
    
    TemplateResult createTemplate(EmailTemplateRequest request);
    
    TemplateResult getTemplate(String templateId);
    
    List<TemplateResult> listTemplates();
    
    boolean deleteTemplate(String templateId);
    
    public static class EmailRequest {
        private String emailId;
        private List<String> to;
        private List<String> cc;
        private List<String> bcc;
        private String subject;
        private String content;
        private String contentType;
        private List<Attachment> attachments;
        private Map<String, String> headers;
        private int priority;
        
        public String getEmailId() { return emailId; }
        public void setEmailId(String emailId) { this.emailId = emailId; }
        public List<String> getTo() { return to; }
        public void setTo(List<String> to) { this.to = to; }
        public List<String> getCc() { return cc; }
        public void setCc(List<String> cc) { this.cc = cc; }
        public List<String> getBcc() { return bcc; }
        public void setBcc(List<String> bcc) { this.bcc = bcc; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public List<Attachment> getAttachments() { return attachments; }
        public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }
        public Map<String, String> getHeaders() { return headers; }
        public void setHeaders(Map<String, String> headers) { this.headers = headers; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
    }
    
    public static class Attachment {
        private String name;
        private String contentType;
        private byte[] data;
        private String url;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public byte[] getData() { return data; }
        public void setData(byte[] data) { this.data = data; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }
    
    public static class EmailResult {
        private boolean success;
        private String emailId;
        private String messageId;
        private String status;
        private List<String> sentTo;
        private List<String> failed;
        private String errorCode;
        private String errorMessage;
        private long sentAt;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getEmailId() { return emailId; }
        public void setEmailId(String emailId) { this.emailId = emailId; }
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
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
    
    public static class EmailTemplateRequest {
        private String templateId;
        private String name;
        private String subject;
        private String content;
        private String contentType;
        private Map<String, String> variables;
        
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public Map<String, String> getVariables() { return variables; }
        public void setVariables(Map<String, String> variables) { this.variables = variables; }
    }
    
    public static class TemplateResult {
        private String templateId;
        private String name;
        private String subject;
        private String content;
        private String contentType;
        private long createdAt;
        
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }
}
