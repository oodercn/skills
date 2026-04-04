package net.ooder.skill.im.feishu.spi;

import net.ooder.skill.common.spi.ImService;
import net.ooder.skill.common.spi.im.MessageContent;
import net.ooder.skill.common.spi.im.MessageType;
import net.ooder.skill.common.spi.im.SendResult;
import net.ooder.skill.im.feishu.dto.SendResultDTO;
import net.ooder.skill.im.feishu.service.FeishuMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class FeishuImServiceImpl implements ImService {
    
    private static final Logger log = LoggerFactory.getLogger(FeishuImServiceImpl.class);
    
    @Autowired(required = false)
    private FeishuMessageService messageService;
    
    @Override
    public SendResult sendToUser(String platform, String userId, MessageContent content) {
        log.info("[Feishu] sendToUser: userId={}, type={}", userId, content.getType());
        
        if (messageService == null) return SendResult.failure("Feishu message service not available");
        if (content == null || content.getType() == null) return SendResult.failure("Invalid content");
        
        try {
            SendResultDTO result;
            switch (content.getType()) {
                case TEXT:
                    result = messageService.sendText(userId, content.getContent());
                    break;
                case MARKDOWN:
                    result = messageService.sendPost(userId, content.getTitle(), content.getContent());
                    break;
                default:
                    result = messageService.sendText(userId, content.getContent());
            }
            
            if (result.isSuccess()) {
                return SendResult.success(result.getMessageId());
            } else {
                return SendResult.failure(result.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Failed to send to user", e);
            return SendResult.failure(e.getMessage());
        }
    }
    
    @Override
    public SendResult sendToGroup(String platform, String groupId, MessageContent content) {
        log.info("[Feishu] sendToGroup: groupId={}", groupId);
        
        if (messageService == null) return SendResult.failure("Feishu message service not available");
        
        try {
            SendResultDTO result = messageService.sendToGroup(groupId, content.getContent());
            if (result.isSuccess()) {
                return SendResult.success(result.getMessageId());
            } else {
                return SendResult.failure(result.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Failed to send to group", e);
            return SendResult.failure(e.getMessage());
        }
    }
    
    @Override
    public SendResult sendDing(String userId, String title, String content) {
        log.info("[Feishu] sendDing (as text): userId={}", userId);
        return sendToUser("feishu", userId, new MessageContent(MessageType.TEXT, title, content));
    }
    
    @Override
    public SendResult sendMarkdown(String platform, String userId, String title, String markdown) {
        log.info("[Feishu] sendMarkdown: userId={}", userId);
        
        if (messageService == null) return SendResult.failure("Feishu message service not available");
        
        try {
            SendResultDTO result = messageService.sendPost(userId, title, markdown);
            if (result.isSuccess()) {
                return SendResult.success(result.getMessageId());
            } else {
                return SendResult.failure(result.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Failed to send markdown/post", e);
            return SendResult.failure(e.getMessage());
        }
    }
    
    @Override
    public List<String> getAvailablePlatforms() {
        return Collections.singletonList("feishu");
    }
    
    @Override
    public boolean isPlatformAvailable(String platform) {
        return "feishu".equals(platform) && messageService != null;
    }
}
