package net.ooder.skill.conversation.controller;

import net.ooder.skill.conversation.model.AddMessageRequest;
import net.ooder.skill.conversation.model.Conversation;
import net.ooder.skill.conversation.model.CreateConversationRequest;
import net.ooder.skill.conversation.model.Message;
import net.ooder.skill.conversation.model.ResultModel;
import net.ooder.skill.conversation.service.ConversationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/conversation")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ConversationController {
    
    private static final Logger log = LoggerFactory.getLogger(ConversationController.class);
    
    @Autowired
    private ConversationService conversationService;

    @PostMapping
    public ResultModel<Conversation> createConversation(@RequestBody CreateConversationRequest request) {
        log.info("Create conversation request: userId={}", request.getUserId());
        
        try {
            Conversation conversation = conversationService.createConversation(request);
            return ResultModel.success("会话创建成功", conversation);
        } catch (Exception e) {
            log.error("Failed to create conversation", e);
            return ResultModel.error("创建会话失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResultModel<Conversation> getConversation(@PathVariable String id) {
        return conversationService.getConversation(id)
                .map(ResultModel::success)
                .orElse(ResultModel.notFound("会话不存在"));
    }
    
    @GetMapping("/user/{userId}")
    public ResultModel<Map<String, Object>> getUserConversations(@PathVariable String userId) {
        List<Conversation> conversations = conversationService.getUserConversations(userId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("conversations", conversations);
        data.put("total", conversations.size());
        data.put("totalTokens", conversationService.getTotalTokens(userId));
        
        return ResultModel.success(data);
    }
    
    @GetMapping("/scene/{sceneId}")
    public ResultModel<Map<String, Object>> getSceneConversations(@PathVariable String sceneId) {
        List<Conversation> conversations = conversationService.getSceneConversations(sceneId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("conversations", conversations);
        data.put("total", conversations.size());
        
        return ResultModel.success(data);
    }
    
    @PutMapping("/{id}")
    public ResultModel<Conversation> updateConversation(
            @PathVariable String id,
            @RequestBody Conversation updates) {
        log.info("Update conversation request: id={}", id);
        
        try {
            Conversation conversation = conversationService.updateConversation(id, updates);
            return ResultModel.success("会话更新成功", conversation);
        } catch (Exception e) {
            log.error("Failed to update conversation", e);
            return ResultModel.error("更新会话失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResultModel<Boolean> deleteConversation(@PathVariable String id) {
        log.info("Delete conversation request: id={}", id);
        
        try {
            conversationService.deleteConversation(id);
            return ResultModel.success("会话已删除", true);
        } catch (Exception e) {
            log.error("Failed to delete conversation", e);
            return ResultModel.error("删除会话失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/archive")
    public ResultModel<Boolean> archiveConversation(@PathVariable String id) {
        log.info("Archive conversation request: id={}", id);
        
        try {
            conversationService.archiveConversation(id);
            return ResultModel.success("会话已归档", true);
        } catch (Exception e) {
            log.error("Failed to archive conversation", e);
            return ResultModel.error("归档会话失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/messages")
    public ResultModel<Message> addMessage(
            @PathVariable String id,
            @RequestBody AddMessageRequest request) {
        log.info("Add message request: conversationId={}", id);
        
        try {
            Message message = conversationService.addMessage(
                    id,
                    request.getRole(),
                    request.getContent(),
                    request.getTokenCount(),
                    request.getMetadata()
            );
            return ResultModel.success("消息添加成功", message);
        } catch (Exception e) {
            log.error("Failed to add message", e);
            return ResultModel.error("添加消息失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/messages")
    public ResultModel<Map<String, Object>> getMessages(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "0") int limit) {
        
        List<Message> messages;
        if (limit > 0) {
            messages = conversationService.getRecentMessages(id, limit);
        } else {
            messages = conversationService.getMessages(id);
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("messages", messages);
        data.put("total", messages.size());
        
        return ResultModel.success(data);
    }
    
    @GetMapping("/{id}/context")
    public ResultModel<Map<String, Object>> getConversationContext(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "4096") int maxTokens) {
        
        List<Map<String, Object>> context = conversationService.getConversationContext(id, maxTokens);
        
        Map<String, Object> data = new HashMap<>();
        data.put("context", context);
        data.put("messageCount", context.size());
        data.put("maxTokens", maxTokens);
        
        return ResultModel.success(data);
    }
    
    @DeleteMapping("/{id}/messages")
    public ResultModel<Boolean> clearMessages(@PathVariable String id) {
        log.info("Clear messages request: conversationId={}", id);
        
        try {
            conversationService.clearMessages(id);
            return ResultModel.success("消息已清空", true);
        } catch (Exception e) {
            log.error("Failed to clear messages", e);
            return ResultModel.error("清空消息失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}/stats")
    public ResultModel<Map<String, Object>> getUserStats(@PathVariable String userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("conversationCount", conversationService.getConversationCount(userId));
        stats.put("totalTokens", conversationService.getTotalTokens(userId));
        
        return ResultModel.success(stats);
    }
}
