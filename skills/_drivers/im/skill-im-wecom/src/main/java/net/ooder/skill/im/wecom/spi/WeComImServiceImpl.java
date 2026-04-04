package net.ooder.skill.im.wecom.spi;

import net.ooder.skill.common.spi.ImService;
import net.ooder.skill.common.spi.im.MessageContent;
import net.ooder.skill.common.spi.im.MessageType;
import net.ooder.skill.common.spi.im.SendResult;
import net.ooder.skill.im.wecom.dto.SendResultDTO;
import net.ooder.skill.im.wecom.service.WeComMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class WeComImServiceImpl implements ImService {
    
    private static final Logger log = LoggerFactory.getLogger(WeComImServiceImpl.class);
    
    @Autowired(required = false)
    private WeComMessageService messageService;
    
    @Override
    public SendResult sendToUser(String platform, String userId, MessageContent content) {
        log.info("[WeCom] sendToUser: userId={}, type={}", userId, content.getType());
        
        if (messageService == null) return SendResult.failure("WeCom message service not available");
        if (content == null || content.getType() == null) return SendResult.failure("Invalid content");
        
        try {
            SendResultDTO result;
            switch (content.getType()) {
                case TEXT:
                    result = messageService.sendText(userId, content.getContent());
                    break;
                case MARKDOWN:
                    result = messageService.sendMarkdown(userId, content.getTitle(), content.getContent());
                    break;
                case LINK:
                    result = messageService.sendNews(userId,
                        content.getTitle() != null ? content.getTitle() : "链接消息",
                        content.getContent(),
                        content.getUrl() != null ? content.getUrl() : "",
                        "");
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
        log.info("[WeCom] sendToGroup: groupId={}", groupId);
        
        if (messageService == null) return SendResult.failure("WeCom message service not available");
        
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
        log.info("[WeCom] sendDing (as text): userId={}", userId);
        return sendToUser("wecom", userId, new MessageContent(MessageType.TEXT, title, content));
    }
    
    @Override
    public SendResult sendMarkdown(String platform, String userId, String title, String markdown) {
        log.info("[WeCom] sendMarkdown: userId={}", userId);
        
        if (messageService == null) return SendResult.failure("WeCom message service not available");
        
        try {
            SendResultDTO result = messageService.sendMarkdown(userId, markdown);
            if (result.isSuccess()) {
                return SendResult.success(result.getMessageId());
            } else {
                return SendResult.failure(result.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Failed to send markdown", e);
            return SendResult.failure(e.getMessage());
        }
    }
    
    @Override
    public List<String> getAvailablePlatforms() {
        return Collections.singletonList("wecom");
    }
    
    @Override
    public boolean isPlatformAvailable(String platform) {
        return "wecom".equals(platform) && messageService != null;
    }
}
