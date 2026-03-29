package net.ooder.skill.msg.push.controller;

import net.ooder.skill.msg.push.dto.PushRequestDTO;
import net.ooder.skill.msg.push.dto.PushResultDTO;
import net.ooder.skill.msg.push.service.MsgPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/msg/push")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MsgPushController {
    
    private static final Logger log = LoggerFactory.getLogger(MsgPushController.class);
    
    @Autowired
    private MsgPushService msgPushService;
    
    @GetMapping("/channels")
    public Map<String, Object> getChannels() {
        log.info("[getChannels] Getting available channels");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("status", "success");
        result.put("data", msgPushService.getAvailableChannels());
        return result;
    }
    
    @PostMapping("/send")
    public Map<String, Object> send(@RequestBody PushRequestDTO request) {
        log.info("[send] Sending message via channel: {}", request.getChannel());
        Map<String, Object> result = new HashMap<>();
        
        PushResultDTO pushResult = msgPushService.send(request);
        result.put("code", pushResult.isSuccess() ? 200 : 500);
        result.put("status", pushResult.isSuccess() ? "success" : "error");
        result.put("data", pushResult);
        result.put("message", pushResult.isSuccess() ? "发送成功" : pushResult.getError());
        
        return result;
    }
    
    @PostMapping("/send/user")
    public Map<String, Object> sendToUser(
            @RequestParam String channel,
            @RequestParam String userId,
            @RequestParam String title,
            @RequestParam String content) {
        log.info("[sendToUser] Sending to user {} via {}", userId, channel);
        Map<String, Object> result = new HashMap<>();
        
        PushResultDTO pushResult = msgPushService.sendToUser(channel, userId, title, content);
        result.put("code", pushResult.isSuccess() ? 200 : 500);
        result.put("status", pushResult.isSuccess() ? "success" : "error");
        result.put("data", pushResult);
        result.put("message", pushResult.isSuccess() ? "发送成功" : pushResult.getError());
        
        return result;
    }
    
    @PostMapping("/send/group")
    public Map<String, Object> sendToGroup(
            @RequestParam String channel,
            @RequestParam String groupId,
            @RequestParam String title,
            @RequestParam String content) {
        log.info("[sendToGroup] Sending to group {} via {}", groupId, channel);
        Map<String, Object> result = new HashMap<>();
        
        PushResultDTO pushResult = msgPushService.sendToGroup(channel, groupId, title, content);
        result.put("code", pushResult.isSuccess() ? 200 : 500);
        result.put("status", pushResult.isSuccess() ? "success" : "error");
        result.put("data", pushResult);
        result.put("message", pushResult.isSuccess() ? "发送成功" : pushResult.getError());
        
        return result;
    }
    
    @PostMapping("/broadcast")
    public Map<String, Object> broadcast(
            @RequestBody PushRequestDTO request,
            @RequestParam List<String> channels) {
        log.info("[broadcast] Broadcasting to {} channels", channels.size());
        Map<String, Object> result = new HashMap<>();
        
        List<PushResultDTO> results = msgPushService.broadcast(request, channels);
        long successCount = results.stream().filter(PushResultDTO::isSuccess).count();
        
        result.put("code", 200);
        result.put("status", "success");
        result.put("data", Map.of(
            "total", results.size(),
            "success", successCount,
            "failed", results.size() - successCount,
            "results", results
        ));
        result.put("message", String.format("广播完成: 成功%d, 失败%d", successCount, results.size() - successCount));
        
        return result;
    }
    
    @PostMapping("/dingtalk/ding")
    public Map<String, Object> sendDing(
            @RequestParam String userId,
            @RequestParam String title,
            @RequestParam String content) {
        log.info("[sendDing] Sending DING to user: {}", userId);
        Map<String, Object> result = new HashMap<>();
        
        PushRequestDTO request = new PushRequestDTO();
        request.setChannel("dingtalk");
        request.setMsgType("DING");
        request.setReceiver("user");
        request.setReceiverId(userId);
        request.setTitle(title);
        request.setContent(content);
        
        PushResultDTO pushResult = msgPushService.send(request);
        result.put("code", pushResult.isSuccess() ? 200 : 500);
        result.put("status", pushResult.isSuccess() ? "success" : "error");
        result.put("data", pushResult);
        result.put("message", pushResult.isSuccess() ? "DING消息发送成功" : pushResult.getError());
        
        return result;
    }
}
