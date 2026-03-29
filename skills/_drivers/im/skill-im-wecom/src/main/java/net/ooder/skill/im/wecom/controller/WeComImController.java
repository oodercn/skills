package net.ooder.skill.im.wecom.controller;

import net.ooder.skill.im.wecom.dto.MessageDTO;
import net.ooder.skill.im.wecom.dto.SendResultDTO;
import net.ooder.skill.im.wecom.service.WeComMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/im/wecom")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WeComImController {
    
    private static final Logger log = LoggerFactory.getLogger(WeComImController.class);
    
    @Autowired
    private WeComMessageService messageService;
    
    @PostMapping("/send")
    public Map<String, Object> send(@RequestBody MessageDTO message) {
        log.info("[send] Sending message to: {}", message.getReceiverId());
        Map<String, Object> result = new HashMap<>();
        
        SendResultDTO sendResult = messageService.sendMessage(message);
        result.put("code", sendResult.isSuccess() ? 200 : 500);
        result.put("status", sendResult.isSuccess() ? "success" : "error");
        result.put("data", sendResult);
        result.put("message", sendResult.isSuccess() ? "消息发送成功" : sendResult.getErrorMessage());
        
        return result;
    }
    
    @PostMapping("/send/user")
    public Map<String, Object> sendToUser(
            @RequestParam String userId,
            @RequestParam String content) {
        log.info("[sendToUser] Sending to user: {}", userId);
        Map<String, Object> result = new HashMap<>();
        
        SendResultDTO sendResult = messageService.sendText(userId, content);
        result.put("code", sendResult.isSuccess() ? 200 : 500);
        result.put("status", sendResult.isSuccess() ? "success" : "error");
        result.put("data", sendResult);
        result.put("message", sendResult.isSuccess() ? "消息发送成功" : sendResult.getErrorMessage());
        
        return result;
    }
    
    @PostMapping("/send/markdown")
    public Map<String, Object> sendMarkdown(
            @RequestParam String userId,
            @RequestParam String content) {
        log.info("[sendMarkdown] Sending markdown to user: {}", userId);
        Map<String, Object> result = new HashMap<>();
        
        SendResultDTO sendResult = messageService.sendMarkdown(userId, content);
        result.put("code", sendResult.isSuccess() ? 200 : 500);
        result.put("status", sendResult.isSuccess() ? "success" : "error");
        result.put("data", sendResult);
        result.put("message", sendResult.isSuccess() ? "Markdown消息发送成功" : sendResult.getErrorMessage());
        
        return result;
    }
}
