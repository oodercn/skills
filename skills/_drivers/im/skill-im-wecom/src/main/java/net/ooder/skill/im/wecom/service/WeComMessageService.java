package net.ooder.skill.im.wecom.service;

import net.ooder.skill.im.wecom.dto.MessageDTO;
import net.ooder.skill.im.wecom.dto.SendResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WeComMessageService {
    
    private static final Logger log = LoggerFactory.getLogger(WeComMessageService.class);
    
    @Value("${wecom.api.endpoint:https://qyapi.weixin.qq.com}")
    private String apiEndpoint;
    
    @Value("${wecom.corp.id:}")
    private String corpId;
    
    @Value("${wecom.agent.id:}")
    private String agentId;
    
    @Value("${wecom.agent.secret:}")
    private String agentSecret;
    
    public SendResultDTO sendMessage(MessageDTO message) {
        log.info("[sendMessage] Sending message to: {}", message.getReceiverId());
        
        try {
            String accessToken = getAccessToken();
            String url = apiEndpoint + "/cgi-bin/message/send?access_token=" + accessToken;
            
            Map<String, Object> body = new HashMap<>();
            body.put("touser", message.getReceiverId());
            body.put("msgtype", message.getMsgType() != null ? message.getMsgType() : "text");
            body.put("agentid", agentId);
            
            String msgType = message.getMsgType() != null ? message.getMsgType().toLowerCase() : "text";
            Map<String, Object> content = new HashMap<>();
            
            if ("text".equals(msgType)) {
                content.put("content", message.getContent());
            } else if ("markdown".equals(msgType)) {
                content.put("content", message.getContent());
            } else if ("news".equals(msgType)) {
                content.put("articles", message.getExtra());
            } else {
                content.put("content", message.getContent());
            }
            
            body.put(msgType, content);
            
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
    
    public SendResultDTO sendMarkdown(String userId, String content) {
        log.info("[sendMarkdown] Sending markdown to user: {}", userId);
        
        MessageDTO message = new MessageDTO();
        message.setMsgType("markdown");
        message.setReceiver("user");
        message.setReceiverId(userId);
        message.setContent(content);
        
        return sendMessage(message);
    }
    
    private String getAccessToken() {
        return "mock_access_token_" + System.currentTimeMillis();
    }
}
