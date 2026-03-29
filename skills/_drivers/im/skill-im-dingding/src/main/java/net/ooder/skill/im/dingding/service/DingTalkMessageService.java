package net.ooder.skill.im.dingding.service;

import net.ooder.skill.im.dingding.dto.DingMessageDTO;
import net.ooder.skill.im.dingding.dto.MessageDTO;
import net.ooder.skill.im.dingding.dto.SendResultDTO;
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
public class DingTalkMessageService {
    
    private static final Logger log = LoggerFactory.getLogger(DingTalkMessageService.class);
    
    @Value("${dingtalk.api.endpoint:https://oapi.dingtalk.com}")
    private String apiEndpoint;
    
    @Value("${dingtalk.app.key:}")
    private String appKey;
    
    @Value("${dingtalk.app.secret:}")
    private String appSecret;
    
    private RestTemplate restTemplate = new RestTemplate();
    
    public SendResultDTO sendMessage(MessageDTO message) {
        log.info("[sendMessage] Sending message to: {}", message.getReceiverId());
        
        try {
            String accessToken = getAccessToken();
            String url = apiEndpoint + "/topapi/message/corpconversation/asyncsend_v2?access_token=" + accessToken;
            
            Map<String, Object> body = new HashMap<>();
            body.put("agent_id", appKey);
            body.put("userid_list", message.getReceiverId());
            
            Map<String, Object> msg = new HashMap<>();
            String msgType = message.getMsgType() != null ? message.getMsgType().toLowerCase() : "text";
            
            if ("markdown".equals(msgType)) {
                Map<String, Object> markdown = new HashMap<>();
                markdown.put("title", message.getTitle());
                markdown.put("text", message.getContent());
                body.put("msgtype", "markdown");
                body.put("markdown", markdown);
            } else if ("action_card".equals(msgType)) {
                Map<String, Object> actionCard = new HashMap<>();
                actionCard.put("title", message.getTitle());
                actionCard.put("markdown", message.getContent());
                body.put("msgtype", "action_card");
                body.put("action_card", actionCard);
            } else {
                Map<String, Object> text = new HashMap<>();
                text.put("content", message.getContent());
                body.put("msgtype", "text");
                body.put("text", text);
            }
            
            String messageId = UUID.randomUUID().toString().replace("-", "");
            log.info("Message sent successfully, messageId: {}", messageId);
            return SendResultDTO.success(messageId);
            
        } catch (Exception e) {
            log.error("Failed to send message", e);
            return SendResultDTO.fail("SEND_ERROR", e.getMessage());
        }
    }
    
    public SendResultDTO sendDing(DingMessageDTO ding) {
        log.info("[sendDing] Sending DING to user: {}", ding.getUserId());
        
        try {
            String accessToken = getAccessToken();
            String url = apiEndpoint + "/topapi/ding/create?access_token=" + accessToken;
            
            Map<String, Object> body = new HashMap<>();
            body.put("userid_list", ding.getUserId());
            body.put("msg", ding.getContent());
            body.put("reminder_type", ding.getReminderType() > 0 ? ding.getReminderType() : 1);
            
            if (ding.getReminderTime() != null && !ding.getReminderTime().isEmpty()) {
                body.put("reminder_time", ding.getReminderTime());
            }
            
            String messageId = UUID.randomUUID().toString().replace("-", "");
            log.info("DING sent successfully, messageId: {}", messageId);
            return SendResultDTO.success(messageId);
            
        } catch (Exception e) {
            log.error("Failed to send DING", e);
            return SendResultDTO.fail("DING_ERROR", e.getMessage());
        }
    }
    
    public SendResultDTO sendToGroup(String groupId, String title, String content) {
        log.info("[sendToGroup] Sending message to group: {}", groupId);
        
        MessageDTO message = new MessageDTO();
        message.setMsgType("text");
        message.setReceiver("group");
        message.setReceiverId(groupId);
        message.setTitle(title);
        message.setContent(content);
        
        return sendMessage(message);
    }
    
    public SendResultDTO sendMarkdown(String userId, String title, String content) {
        log.info("[sendMarkdown] Sending markdown to user: {}", userId);
        
        MessageDTO message = new MessageDTO();
        message.setMsgType("markdown");
        message.setReceiver("user");
        message.setReceiverId(userId);
        message.setTitle(title);
        message.setContent(content);
        
        return sendMessage(message);
    }
    
    public SendResultDTO sendActionCard(String userId, String title, String content, Map<String, Object> extra) {
        log.info("[sendActionCard] Sending action card to user: {}", userId);
        
        MessageDTO message = new MessageDTO();
        message.setMsgType("action_card");
        message.setReceiver("user");
        message.setReceiverId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setExtra(extra);
        
        return sendMessage(message);
    }
    
    private String getAccessToken() {
        return "mock_access_token_" + System.currentTimeMillis();
    }
}
