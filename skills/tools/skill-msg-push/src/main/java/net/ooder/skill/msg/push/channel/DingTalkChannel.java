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
public class DingTalkChannel implements MessageChannel {
    
    private static final Logger log = LoggerFactory.getLogger(DingTalkChannel.class);
    
    @Value("${channel.dingtalk.enabled:true}")
    private boolean enabled;
    
    @Value("${channel.dingtalk.endpoint:http://localhost:8082}")
    private String endpoint;
    
    private RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public String getChannelName() {
        return "dingtalk";
    }
    
    @Override
    public boolean isAvailable() {
        return enabled;
    }
    
    @Override
    public PushResultDTO send(PushRequestDTO request) {
        log.info("[DingTalk] Sending message to: {}", request.getReceiver());
        
        if (!enabled) {
            return PushResultDTO.fail("dingtalk", "钉钉渠道未启用");
        }
        
        try {
            String msgType = request.getMsgType() != null ? request.getMsgType() : "text";
            String url;
            
            if ("DING".equalsIgnoreCase(msgType)) {
                url = endpoint + "/api/v1/im/dingding/ding";
            } else {
                url = endpoint + "/api/v1/im/dingding/send";
            }
            
            Map<String, Object> body = new HashMap<>();
            body.put("receiver", request.getReceiver());
            body.put("receiverId", request.getReceiverId());
            body.put("title", request.getTitle());
            body.put("content", request.getContent());
            body.put("msgType", msgType);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
            
            if (response.getStatusCode().is2xxBody() && response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                if ("success".equals(result.get("status")) || Integer.valueOf(200).equals(result.get("code"))) {
                    String messageId = UUID.randomUUID().toString().replace("-", "");
                    return PushResultDTO.success(messageId, "dingtalk");
                }
            }
            
            return PushResultDTO.fail("dingtalk", "发送失败");
            
        } catch (Exception e) {
            log.error("Failed to send DingTalk message", e);
            return PushResultDTO.fail("dingtalk", e.getMessage());
        }
    }
    
    @Override
    public PushResultDTO sendToUser(String userId, String title, String content) {
        PushRequestDTO request = new PushRequestDTO();
        request.setChannel("dingtalk");
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
        request.setChannel("dingtalk");
        request.setMsgType("text");
        request.setReceiver("group");
        request.setReceiverId(groupId);
        request.setTitle(title);
        request.setContent(content);
        return send(request);
    }
}
