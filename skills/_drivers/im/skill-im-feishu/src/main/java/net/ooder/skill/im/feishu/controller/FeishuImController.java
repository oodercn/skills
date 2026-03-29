package net.ooder.skill.im.feishu.controller;

import net.ooder.skill.im.feishu.dto.MessageDTO;
import net.ooder.skill.im.feishu.dto.SendResultDTO;
import net.ooder.skill.im.feishu.service.FeishuMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/im/feishu")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FeishuImController {
    
    private static final Logger log = LoggerFactory.getLogger(FeishuImController.class);
    
    @Autowired
    private FeishuMessageService messageService;
    
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
    
    @PostMapping("/send/post")
    public Map<String, Object> sendPost(
            @RequestParam String userId,
            @RequestParam String title,
            @RequestParam String content) {
        log.info("[sendPost] Sending post to user: {}", userId);
        Map<String, Object> result = new HashMap<>();
        
        SendResultDTO sendResult = messageService.sendPost(userId, title, content);
        result.put("code", sendResult.isSuccess() ? 200 : 500);
        result.put("status", sendResult.isSuccess() ? "success" : "error");
        result.put("data", sendResult);
        result.put("message", sendResult.isSuccess() ? "富文本消息发送成功" : sendResult.getErrorMessage());
        
        return result;
    }
    
    @PostMapping("/send/interactive")
    public Map<String, Object> sendInteractive(
            @RequestParam String userId,
            @RequestParam String title,
            @RequestBody Map<String, Object> elements) {
        log.info("[sendInteractive] Sending interactive card to user: {}", userId);
        Map<String, Object> result = new HashMap<>();
        
        SendResultDTO sendResult = messageService.sendInteractive(userId, title, elements);
        result.put("code", sendResult.isSuccess() ? 200 : 500);
        result.put("status", sendResult.isSuccess() ? "success" : "error");
        result.put("data", sendResult);
        result.put("message", sendResult.isSuccess() ? "交互卡片发送成功" : sendResult.getErrorMessage());
        
        return result;
    }
}
