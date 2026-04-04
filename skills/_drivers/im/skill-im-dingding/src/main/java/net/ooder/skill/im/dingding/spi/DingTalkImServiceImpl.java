package net.ooder.skill.im.dingding.spi;

import net.ooder.skill.common.spi.ImService;
import net.ooder.skill.common.spi.im.MessageContent;
import net.ooder.skill.common.spi.im.MessageType;
import net.ooder.skill.common.spi.im.SendResult;
import net.ooder.skill.im.dingding.dto.SendResultDTO;
import net.ooder.skill.im.dingding.service.DingTalkMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class DingTalkImServiceImpl implements ImService {
    
    private static final Logger log = LoggerFactory.getLogger(DingTalkImServiceImpl.class);
    
    @Autowired(required = false)
    private DingTalkMessageService messageService;
    
    @Override
    public SendResult sendToUser(String platform, String userId, MessageContent content) {
        log.info("[DingTalk] sendToUser: userId={}, type={}", userId, content.getType());
        
        if (messageService == null) return SendResult.failure("DingTalk message service not available");
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
                    result = messageService.sendActionCard(userId, 
                        content.getTitle() != null ? content.getTitle() : "链接消息",
                        content.getContent(),
                        null);
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
        log.info("[DingTalk] sendToGroup: groupId={}, type={}", groupId, content.getType());
        
        if (messageService == null) return SendResult.failure("DingTalk message service not available");
        
        try {
            String title = content.getTitle();
            if (title == null && content.getType() == MessageType.MARKDOWN) {
                title = "群消息";
            }
            SendResultDTO result = messageService.sendToGroup(groupId, title, content.getContent());
            
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
        log.info("[DingTalk] sendDing: userId={}", userId);
        
        if (messageService == null) return SendResult.failure("DingTalk message service not available");
        
        try {
            var dingDto = new net.ooder.skill.im.dingding.dto.DingMessageDTO();
            dingDto.setUserId(userId);
            dingDto.setTitle(title);
            dingDto.setContent(content);
            
            SendResultDTO result = messageService.sendDing(dingDto);
            if (result.isSuccess()) {
                return SendResult.success(result.getMessageId());
            } else {
                return SendResult.failure(result.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Failed to send DING", e);
            return SendResult.failure(e.getMessage());
        }
    }
    
    @Override
    public SendResult sendMarkdown(String platform, String userId, String title, String markdown) {
        log.info("[DingTalk] sendMarkdown: userId={}", userId);
        
        if (messageService == null) return SendResult.failure("DingTalk message service not available");
        
        try {
            SendResultDTO result = messageService.sendMarkdown(userId, title, markdown);
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
        return Collections.singletonList("dingtalk");
    }
    
    @Override
    public boolean isPlatformAvailable(String platform) {
        return "dingtalk".equals(platform) && messageService != null;
    }
}
