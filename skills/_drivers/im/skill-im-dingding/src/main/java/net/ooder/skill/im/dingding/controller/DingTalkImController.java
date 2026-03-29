package net.ooder.skill.im.dingding.controller;

import net.ooder.skill.im.dingding.dto.DingMessageDTO;
import net.ooder.skill.im.dingding.dto.MessageDTO;
import net.ooder.skill.im.dingding.dto.SendResultDTO;
import net.ooder.skill.im.dingding.service.DingTalkMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/im/dingding")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DingTalkImController {
    
    private static final Logger log = LoggerFactory.getLogger(DingTalkImController.class);
    
    @Autowired
    private DingTalkMessageService messageService;
    
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
    
    @PostMapping("/ding")
    public Map<String, Object> sendDing(@RequestBody DingMessageDTO ding) {
        log.info("[sendDing] Sending DING to: {}", ding.getUserId());
        Map<String, Object> result = new HashMap<>();
        
        SendResultDTO sendResult = messageService.sendDing(ding);
        result.put("code", sendResult.isSuccess() ? 200 : 500);
        result.put("status", sendResult.isSuccess() ? "success" : "error");
        result.put("data", sendResult);
        result.put("message", sendResult.isSuccess() ? "DING消息发送成功" : sendResult.getErrorMessage());
        
        return result;
    }
    
    @PostMapping("/send/user")
    public Map<String, Object> sendToUser(
            @RequestParam String userId,
            @RequestParam String content,
            @RequestParam(required = false, defaultValue = "text") String msgType) {
        log.info("[sendToUser] Sending to user: {}", userId);
        Map<String, Object> result = new HashMap<>();
        
        MessageDTO message = new MessageDTO();
        message.setMsgType(msgType);
        message.setReceiver("user");
        message.setReceiverId(userId);
        message.setContent(content);
        
        SendResultDTO sendResult = messageService.sendMessage(message);
        result.put("code", sendResult.isSuccess() ? 200 : 500);
        result.put("status", sendResult.isSuccess() ? "success" : "error");
        result.put("data", sendResult);
        result.put("message", sendResult.isSuccess() ? "消息发送成功" : sendResult.getErrorMessage());
        
        return result;
    }
    
    @PostMapping("/send/group")
    public Map<String, Object> sendToGroup(
            @RequestParam String groupId,
            @RequestParam String title,
            @RequestParam String content) {
        log.info("[sendToGroup] Sending to group: {}", groupId);
        Map<String, Object> result = new HashMap<>();
        
        SendResultDTO sendResult = messageService.sendToGroup(groupId, title, content);
        result.put("code", sendResult.isSuccess() ? 200 : 500);
        result.put("status", sendResult.isSuccess() ? "success" : "error");
        result.put("data", sendResult);
        result.put("message", sendResult.isSuccess() ? "消息发送成功" : sendResult.getErrorMessage());
        
        return result;
    }
    
    @PostMapping("/send/markdown")
    public Map<String, Object> sendMarkdown(
            @RequestParam String userId,
            @RequestParam String title,
            @RequestParam String content) {
        log.info("[sendMarkdown] Sending markdown to: {}", userId);
        Map<String, Object> result = new HashMap<>();
        
        SendResultDTO sendResult = messageService.sendMarkdown(userId, title, content);
        result.put("code", sendResult.isSuccess() ? 200 : 500);
        result.put("status", sendResult.isSuccess() ? "success" : "error");
        result.put("data", sendResult);
        result.put("message", sendResult.isSuccess() ? "Markdown消息发送成功" : sendResult.getErrorMessage());
        
        return result;
    }
    
    @PostMapping("/send/action-card")
    public Map<String, Object> sendActionCard(
            @RequestParam String userId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestBody(required = false) Map<String, Object> extra) {
        log.info("[sendActionCard] Sending action card to: {}", userId);
        Map<String, Object> result = new HashMap<>();
        
        SendResultDTO sendResult = messageService.sendActionCard(userId, title, content, extra);
        result.put("code", sendResult.isSuccess() ? 200 : 500);
        result.put("status", sendResult.isSuccess() ? "success" : "error");
        result.put("data", sendResult);
        result.put("message", sendResult.isSuccess() ? "卡片消息发送成功" : sendResult.getErrorMessage());
        
        return result;
    }
}
