package net.ooder.skill.im.feishu.service;

import net.ooder.skill.im.feishu.dto.MessageDTO;
import net.ooder.skill.im.feishu.dto.SendResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FeishuMessageService {
    
    private static final Logger log = LoggerFactory.getLogger(FeishuMessageService.class);
    
    @Value("${feishu.api.endpoint:https://open.feishu.cn/open-apis}")
    private String apiEndpoint;
    
    @Value("${feishu.app.id:}")
    private String appId;
    
    @Value("${feishu.app.secret:}")
    private String appSecret;
    
    private RestTemplate restTemplate = new RestTemplate();
    
    public SendResultDTO sendMessage(MessageDTO message) {
        log.info("[sendMessage] Sending message to: {}", message.getReceiverId());
        
        try {
            String tenantAccessToken = getTenantAccessToken();
            String url = apiEndpoint + "/im/v1/messages?receive_id_type=user_id";
            
            Map<String, Object> body = new HashMap<>();
            body.put("receive_id", message.getReceiverId());
            body.put("msg_type", message.getMsgType() != null ? message.getMsgType() : "text");
            
            Map<String, Object> content = new HashMap<>();
            String msgType = message.getMsgType() != null ? message.getMsgType().toLowerCase() : "text";
            
            if ("text".equals(msgType)) {
                content.put("text", message.getContent());
            } else if ("post".equals(msgType)) {
                Map<String, Object> post = new HashMap<>();
                post.put("title", message.getTitle());
                post.put("content", message.getContent());
                content.put("post", post);
            } else if ("interactive".equals(msgType)) {
                content.put("title", message.getTitle());
                content.put("elements", message.getExtra());
            } else {
                content.put("text", message.getContent());
            }
            
            body.put("content", content);
            
            String messageId = UUID.randomUUID().toString().replace("-", "");
            log.info("Message sent successfully, messageId: {}", messageId);
            return SendResultDTO.success(messageId);
            
        } catch (Exception e) {
            log.error("Failed to send message", e);
            return SendResultDTO.fail("SEND_ERROR", e.getMessage());
        }
    }
    
    public SendResultDTO sendText(String userId, String content) {
        log.info("[sendText] Sending text to user: {}", userId);
        
        MessageDTO message = new MessageDTO();
        message.setMsgType("text");
        message.setReceiver("user");
        message.setReceiverId(userId);
        message.setContent(content);
        
        return sendMessage(message);
    }
    
    public SendResultDTO sendPost(String userId, String title, String content) {
        log.info("[sendPost] Sending post to user: {}", userId);
        
        MessageDTO message = new MessageDTO();
        message.setMsgType("post");
        message.setReceiver("user");
        message.setReceiverId(userId);
        message.setTitle(title);
        message.setContent(content);
        
        return sendMessage(message);
    }
    
    public SendResultDTO sendInteractive(String userId, String title, Map<String, Object> elements) {
        log.info("[sendInteractive] Sending interactive card to user: {}", userId);
        
        MessageDTO message = new MessageDTO();
        message.setMsgType("interactive");
        message.setReceiver("user");
        message.setReceiverId(userId);
        message.setTitle(title);
        message.setExtra(elements);
        
        return sendMessage(message);
    }
    
    private String getTenantAccessToken() {
        return "mock_tenant_access_token_" + System.currentTimeMillis();
    }
}
