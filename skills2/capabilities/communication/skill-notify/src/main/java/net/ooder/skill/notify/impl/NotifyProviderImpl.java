package net.ooder.skill.notify.impl;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.notify.NotifyProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class NotifyProviderImpl implements NotifyProvider {
    
    private final Map<String, TemplateResult> templates = new ConcurrentHashMap<>();
    private final Map<String, SubscriptionResult> subscriptions = new ConcurrentHashMap<>();
    
    @Override
    public String getProviderType() {
        return "notify";
    }
    
    @Override
    public List<String> getSupportedChannels() {
        return Arrays.asList("email", "sms", "push", "webhook", "wechat", "dingtalk", "feishu");
    }
    
    @Override
    public NotifyResult send(NotifyRequest request) {
        log.info("Notify send: channel={}, recipients={}", request.getChannel(), request.getRecipients());
        
        NotifyResult result = new NotifyResult();
        result.setNotifyId(request.getNotifyId() != null ? request.getNotifyId() : UUID.randomUUID().toString());
        result.setSuccess(true);
        result.setStatus("sent");
        result.setSentTo(request.getRecipients());
        result.setFailed(new ArrayList<>());
        result.setSentAt(System.currentTimeMillis());
        
        return result;
    }
    
    @Override
    public NotifyResult sendBatch(List<NotifyRequest> requests) {
        log.info("Notify sendBatch: count={}", requests.size());
        
        NotifyResult result = new NotifyResult();
        result.setNotifyId(UUID.randomUUID().toString());
        result.setSuccess(true);
        result.setStatus("sent");
        result.setSentTo(new ArrayList<>());
        result.setFailed(new ArrayList<>());
        result.setSentAt(System.currentTimeMillis());
        
        for (NotifyRequest request : requests) {
            result.getSentTo().addAll(request.getRecipients());
        }
        
        return result;
    }
    
    @Override
    public NotifyResult sendTemplate(String templateId, Map<String, Object> params, List<String> recipients) {
        log.info("Notify sendTemplate: templateId={}, recipients={}", templateId, recipients);
        
        TemplateResult template = templates.get(templateId);
        if (template == null) {
            NotifyResult result = new NotifyResult();
            result.setSuccess(false);
            result.setErrorCode("TEMPLATE_NOT_FOUND");
            result.setErrorMessage("Template not found: " + templateId);
            return result;
        }
        
        String content = template.getContent();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            content = content.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        
        NotifyRequest request = new NotifyRequest();
        request.setNotifyId(UUID.randomUUID().toString());
        request.setChannel(template.getChannel());
        request.setSubject(template.getSubject());
        request.setContent(content);
        request.setRecipients(recipients);
        
        return send(request);
    }
    
    @Override
    public TemplateResult createTemplate(TemplateRequest request) {
        log.info("Notify createTemplate: templateId={}", request.getTemplateId());
        
        TemplateResult result = new TemplateResult();
        result.setTemplateId(request.getTemplateId());
        result.setName(request.getName());
        result.setChannel(request.getChannel());
        result.setSubject(request.getSubject());
        result.setContent(request.getContent());
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
        log.info("Notify deleteTemplate: templateId={}", templateId);
        return templates.remove(templateId) != null;
    }
    
    @Override
    public SubscriptionResult subscribe(String userId, String channel, Map<String, Object> config) {
        log.info("Notify subscribe: userId={}, channel={}", userId, channel);
        
        String key = userId + ":" + channel;
        SubscriptionResult result = new SubscriptionResult();
        result.setUserId(userId);
        result.setChannel(channel);
        result.setEnabled(true);
        result.setConfig(config);
        result.setSubscribedAt(System.currentTimeMillis());
        
        subscriptions.put(key, result);
        
        return result;
    }
    
    @Override
    public boolean unsubscribe(String userId, String channel) {
        log.info("Notify unsubscribe: userId={}, channel={}", userId, channel);
        String key = userId + ":" + channel;
        return subscriptions.remove(key) != null;
    }
    
    @Override
    public List<SubscriptionResult> getSubscriptions(String userId) {
        List<SubscriptionResult> result = new ArrayList<>();
        for (SubscriptionResult sub : subscriptions.values()) {
            if (userId.equals(sub.getUserId())) {
                result.add(sub);
            }
        }
        return result;
    }
}
