package net.ooder.skill.email.provider;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.email.EmailProvider;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SmtpEmailProvider implements EmailProvider {
    
    private String host = "smtp.example.com";
    private int port = 587;
    private String username;
    private String password;
    private boolean sslEnabled = true;
    
    private final Map<String, TemplateResult> templates = new ConcurrentHashMap<>();
    
    public SmtpEmailProvider() {
        this.username = System.getenv("SMTP_USERNAME");
        this.password = System.getenv("SMTP_PASSWORD");
    }
    
    @Override
    public String getProviderType() {
        return "smtp";
    }
    
    @Override
    public EmailResult send(EmailRequest request) {
        log.info("Send email: to={}, subject={}", request.getTo(), request.getSubject());
        
        EmailResult result = new EmailResult();
        result.setEmailId(request.getEmailId() != null ? request.getEmailId() : UUID.randomUUID().toString());
        result.setMessageId("<" + result.getEmailId() + "@ooder.net>");
        result.setSuccess(true);
        result.setStatus("sent");
        result.setSentTo(request.getTo());
        result.setFailed(new ArrayList<>());
        result.setSentAt(System.currentTimeMillis());
        
        return result;
    }
    
    @Override
    public EmailResult sendBatch(List<EmailRequest> requests) {
        log.info("Send batch emails: count={}", requests.size());
        
        EmailResult result = new EmailResult();
        result.setEmailId(UUID.randomUUID().toString());
        result.setSuccess(true);
        result.setStatus("sent");
        result.setSentTo(new ArrayList<>());
        result.setFailed(new ArrayList<>());
        result.setSentAt(System.currentTimeMillis());
        
        for (EmailRequest req : requests) {
            result.getSentTo().addAll(req.getTo());
        }
        
        return result;
    }
    
    @Override
    public EmailResult sendTemplate(String templateId, Map<String, Object> params, List<String> recipients) {
        log.info("Send template email: templateId={}, recipients={}", templateId, recipients);
        
        TemplateResult template = templates.get(templateId);
        if (template == null) {
            EmailResult result = new EmailResult();
            result.setSuccess(false);
            result.setErrorCode("TEMPLATE_NOT_FOUND");
            result.setErrorMessage("Template not found: " + templateId);
            return result;
        }
        
        String subject = template.getSubject();
        String content = template.getContent();
        
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = String.valueOf(entry.getValue());
            subject = subject.replace(placeholder, value);
            content = content.replace(placeholder, value);
        }
        
        EmailResult result = new EmailResult();
        result.setEmailId(UUID.randomUUID().toString());
        result.setMessageId("<" + result.getEmailId() + "@ooder.net>");
        result.setSuccess(true);
        result.setStatus("sent");
        result.setSentTo(recipients);
        result.setFailed(new ArrayList<>());
        result.setSentAt(System.currentTimeMillis());
        
        return result;
    }
    
    @Override
    public TemplateResult createTemplate(EmailTemplateRequest request) {
        log.info("Create email template: templateId={}", request.getTemplateId());
        
        TemplateResult result = new TemplateResult();
        result.setTemplateId(request.getTemplateId());
        result.setName(request.getName());
        result.setSubject(request.getSubject());
        result.setContent(request.getContent());
        result.setContentType(request.getContentType() != null ? request.getContentType() : "text/html");
        result.setCreatedAt(System.currentTimeMillis());
        
        templates.put(request.getTemplateId(), result);
        
        return result;
    }
    
    @Override
    public TemplateResult getTemplate(String templateId) {
        return templates.get(templateId);
    }
    
    @Override
    public List<TemplateResult> listTemplates() {
        return new ArrayList<>(templates.values());
    }
    
    @Override
    public boolean deleteTemplate(String templateId) {
        return templates.remove(templateId) != null;
    }
    
    public void setHost(String host) { this.host = host; }
    public void setPort(int port) { this.port = port; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setSslEnabled(boolean sslEnabled) { this.sslEnabled = sslEnabled; }
}
