package net.ooder.skill.msg.push.channel;

import net.ooder.skill.msg.push.dto.PushRequestDTO;
import net.ooder.skill.msg.push.dto.PushResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class WeComChannel implements MessageChannel {
    
    private static final Logger log = LoggerFactory.getLogger(WeComChannel.class);
    
    @Value("${channel.wecom.enabled:true}")
    private boolean enabled;
    
    @Value("${channel.wecom.endpoint:http://localhost:8084}")
    private String endpoint;
    
    private RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public String getChannelName() {
        return "wecom";
    }
    
    @Override
    public boolean isAvailable() {
        return enabled;
    }
    
    @Override
    public PushResultDTO send(PushRequestDTO request) {
        log.info("[WeCom] Sending message to: {}", request.getReceiver());
        
        if (!enabled) {
            return PushResultDTO.fail("wecom", "企业微信渠道未启用");
        }
        
        try {
            String url = endpoint + "/api/v1/im/wecom/send";
            
            Map<String, Object> body = new HashMap<>();
            body.put("receiver", request.getReceiver());
            body.put("receiverId", request.getReceiverId());
            body.put("title", request.getTitle());
            body.put("content", request.getContent());
            body.put("msgType", request.getMsgType());
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
            
            if (response.getStatusCode().is2xxBody() && response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                if ("success".equals(result.get("status")) || Integer.valueOf(200).equals(result.get("code"))) {
                    String messageId = UUID.randomUUID().toString().replace("-", "");
                    return PushResultDTO.success(messageId, "wecom");
                }
            }
            
            return PushResultDTO.fail("wecom", "发送失败");
            
        } catch (Exception e) {
            log.error("Failed to send WeCom message", e);
            return PushResultDTO.fail("wecom", e.getMessage());
        }
    }
    
    @Override
    public PushResultDTO sendToUser(String userId, String title, String content) {
        PushRequestDTO request = new PushRequestDTO();
        request.setChannel("wecom");
        request.setMsgType("text");
        request.setReceiver("user");
        request.setReceiverId(userId);
        request.setTitle(title);
        request.setContent(content);
        return send(request);
    }
    
    @Override
    public PushResultDTO sendToGroup(String groupId, String title, String content) {
        PushRequestDTO request = new PushRequestDTO();
        request.setChannel("wecom");
        request.setMsgType("text");
        request.setReceiver("group");
        request.setReceiverId(groupId);
        request.setTitle(title);
        request.setContent(content);
        return send(request);
    }
}
