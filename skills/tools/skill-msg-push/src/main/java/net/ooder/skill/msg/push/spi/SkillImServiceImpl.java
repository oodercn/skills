package net.ooder.skill.msg.push.spi;

import net.ooder.skill.common.spi.ImService;
import net.ooder.skill.common.spi.im.SendResult;
import net.ooder.skill.common.spi.im.MessageContent;
import net.ooder.skill.common.spi.im.MessageType;
import net.ooder.skill.msg.push.service.MsgPushService;
import net.ooder.skill.msg.push.dto.PushRequestDTO;
import net.ooder.skill.msg.push.dto.PushResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@ConditionalOnProperty(name = "skill.im.enabled", havingValue = "true", matchIfMissing = false)
public class SkillImServiceImpl implements ImService {
    
    private static final Logger log = LoggerFactory.getLogger(SkillImServiceImpl.class);
    
    @Autowired
    private MsgPushService msgPushService;
    
    @Override
    public SendResult sendToUser(String platform, String userId, MessageContent content) {
        log.info("[sendToUser] platform={}, userId={}", platform, userId);
        try {
            PushRequestDTO request = convertToPushRequest(platform, userId, "user", content);
            PushResultDTO result = msgPushService.send(request);
            
            if (result.isSuccess()) {
                return SendResult.success(result.getMessageId());
            } else {
                return SendResult.failure(result.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("[sendToUser] Failed to send message", e);
            return SendResult.failure(e.getMessage());
        }
    }
    
    @Override
    public SendResult sendToGroup(String platform, String groupId, MessageContent content) {
        log.info("[sendToGroup] platform={}, groupId={}", platform, groupId);
        try {
            PushRequestDTO request = convertToPushRequest(platform, groupId, "group", content);
            PushResultDTO result = msgPushService.send(request);
            
            if (result.isSuccess()) {
                return SendResult.success(result.getMessageId());
            } else {
                return SendResult.failure(result.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("[sendToGroup] Failed to send message", e);
            return SendResult.failure(e.getMessage());
        }
    }
    
    @Override
    public SendResult sendDing(String userId, String title, String content) {
        log.info("[sendDing] userId={}", userId);
        try {
            MessageContent dingContent = new MessageContent(MessageType.DING, title, content);
            PushRequestDTO request = convertToPushRequest("dingtalk", userId, "user", dingContent);
            request.setMsgType("ding");
            PushResultDTO result = msgPushService.send(request);
            
            if (result.isSuccess()) {
                return SendResult.success(result.getMessageId());
            } else {
                return SendResult.failure(result.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("[sendDing] Failed to send ding", e);
            return SendResult.failure(e.getMessage());
        }
    }
    
    @Override
    public SendResult sendMarkdown(String platform, String userId, String title, String markdown) {
        log.info("[sendMarkdown] platform={}, userId={}", platform, userId);
        try {
            MessageContent content = MessageContent.markdown(title, markdown);
            PushRequestDTO request = convertToPushRequest(platform, userId, "user", content);
            request.setMsgType("markdown");
            PushResultDTO result = msgPushService.send(request);
            
            if (result.isSuccess()) {
                return SendResult.success(result.getMessageId());
            } else {
                return SendResult.failure(result.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("[sendMarkdown] Failed to send markdown", e);
            return SendResult.failure(e.getMessage());
        }
    }
    
    @Override
    public List<String> getAvailablePlatforms() {
        return msgPushService.getAvailableChannels();
    }
    
    @Override
    public boolean isPlatformAvailable(String platform) {
        List<String> available = getAvailablePlatforms();
        return available.contains(platform.toLowerCase());
    }
    
    private PushRequestDTO convertToPushRequest(String platform, String receiverId, String receiverType, MessageContent content) {
        PushRequestDTO request = new PushRequestDTO();
        request.setChannel(platform.toLowerCase());
        request.setReceiver(receiverType);
        request.setReceiverId(receiverId);
        request.setTitle(content.getTitle());
        request.setContent(content.getContent());
        
        if (content.getType() != null) {
            request.setMsgType(content.getType().name().toLowerCase());
        }
        
        return request;
    }
}
