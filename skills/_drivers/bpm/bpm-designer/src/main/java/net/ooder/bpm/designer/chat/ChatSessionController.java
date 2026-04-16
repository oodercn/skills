package net.ooder.bpm.designer.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.bpm.designer.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "*")
public class ChatSessionController {
    
    private static final Logger log = LoggerFactory.getLogger(ChatSessionController.class);
    
    private final ExecutorService sseExecutor = Executors.newCachedThreadPool();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private ChatSessionManager sessionManager;
    
    @PostMapping("/sessions/{sessionId}/messages")
    public ApiResponse<Map<String, Object>> sendMessage(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {
        
        try {
            String content = (String) request.get("content");
            String skillId = (String) request.getOrDefault("skillId", "bpm-designer");
            String userId = (String) request.getOrDefault("userId", "bpm-designer-user");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> context = (Map<String, Object>) request.get("context");
            
            if (content == null || content.trim().isEmpty()) {
                return ApiResponse.error(400, "Message content is required");
            }
            
            log.info("Chat message: session={}, content={}", sessionId, 
                content.length() > 50 ? content.substring(0, 50) + "..." : content);
            
            ChatService.ChatResponse chatResponse = chatService.sendMessage(
                sessionId, userId, skillId, content, context);
            
            Map<String, Object> data = new HashMap<>();
            data.put("content", chatResponse.getContent());
            data.put("sessionId", chatResponse.getSessionId());
            
            if (chatResponse.getActions() != null && !chatResponse.getActions().isEmpty()) {
                List<Map<String, Object>> actions = chatResponse.getActions().stream()
                    .map(action -> {
                        Map<String, Object> actionMap = new HashMap<>();
                        actionMap.put("type", action.getType());
                        actionMap.put("data", action.getData());
                        actionMap.put("success", action.isSuccess());
                        if (action.getError() != null) {
                            actionMap.put("error", action.getError());
                        }
                        return actionMap;
                    })
                    .toList();
                data.put("actions", actions);
            }
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("model", "qwen-plus");
            metadata.put("mode", chatService.isLLMAvailable() ? "llm" : "local");
            data.put("metadata", metadata);
            
            return ApiResponse.success(data);
            
        } catch (Exception e) {
            log.error("Error processing chat message: {}", e.getMessage(), e);
            return ApiResponse.error(500, "Chat processing failed: " + e.getMessage());
        }
    }
    
    @PostMapping(value = "/sessions/{sessionId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessageStream(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {
        
        SseEmitter emitter = new SseEmitter(120000L);
        
        sseExecutor.execute(() -> {
            try {
                String content = (String) request.get("content");
                String skillId = (String) request.getOrDefault("skillId", "bpm-designer");
                String userId = (String) request.getOrDefault("userId", "bpm-designer-user");
                
                @SuppressWarnings("unchecked")
                Map<String, Object> context = (Map<String, Object>) request.get("context");
                
                if (content == null || content.trim().isEmpty()) {
                    emitter.send(SseEmitter.event().name("error").data("{\"error\":\"Content is required\"}"));
                    emitter.complete();
                    return;
                }
                
                ChatService.ChatResponse chatResponse = chatService.sendMessage(
                    sessionId, userId, skillId, content, context);
                
                String fullContent = chatResponse.getContent();
                int chunkSize = 4;
                for (int i = 0; i < fullContent.length(); i += chunkSize) {
                    int end = Math.min(i + chunkSize, fullContent.length());
                    String chunk = fullContent.substring(i, end);
                    
                    Map<String, Object> chunkData = new HashMap<>();
                    chunkData.put("content", chunk);
                    chunkData.put("done", end >= fullContent.length());
                    
                    emitter.send(SseEmitter.event()
                        .name("content")
                        .data(objectMapper.writeValueAsString(chunkData)));
                    
                    Thread.sleep(20);
                }
                
                if (chatResponse.getActions() != null && !chatResponse.getActions().isEmpty()) {
                    List<Map<String, Object>> actions = chatResponse.getActions().stream()
                        .map(action -> {
                            Map<String, Object> actionMap = new HashMap<>();
                            actionMap.put("type", action.getType());
                            actionMap.put("data", action.getData());
                            actionMap.put("success", action.isSuccess());
                            return actionMap;
                        })
                        .toList();
                    
                    Map<String, Object> actionData = new HashMap<>();
                    actionData.put("actions", actions);
                    emitter.send(SseEmitter.event()
                        .name("actions")
                        .data(objectMapper.writeValueAsString(actionData)));
                }
                
                Map<String, Object> doneData = new HashMap<>();
                doneData.put("sessionId", chatResponse.getSessionId());
                doneData.put("mode", chatService.isLLMAvailable() ? "llm" : "local");
                emitter.send(SseEmitter.event()
                    .name("done")
                    .data(objectMapper.writeValueAsString(doneData)));
                
                emitter.complete();
                
            } catch (Exception e) {
                log.error("Error in SSE stream: {}", e.getMessage(), e);
                try {
                    emitter.send(SseEmitter.event().name("error").data("{\"error\":\"" + e.getMessage() + "\"}"));
                } catch (Exception ignored) {}
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }
    
    @GetMapping("/sessions/{sessionId}/history")
    public ApiResponse<Map<String, Object>> getSessionHistory(@PathVariable String sessionId) {
        try {
            ChatSession session = sessionManager.getSession(sessionId);
            if (session == null) {
                return ApiResponse.error(404, "Session not found");
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("sessionId", sessionId);
            data.put("messages", session.getConversationHistory());
            data.put("context", session.getContext());
            
            return ApiResponse.success(data);
        } catch (Exception e) {
            log.error("Error getting session history: {}", e.getMessage(), e);
            return ApiResponse.error(500, "Failed to get session history");
        }
    }
    
    @DeleteMapping("/sessions/{sessionId}")
    public ApiResponse<Void> clearSession(@PathVariable String sessionId) {
        try {
            sessionManager.removeSession(sessionId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("Error clearing session: {}", e.getMessage(), e);
            return ApiResponse.error(500, "Failed to clear session");
        }
    }
    
    @PostMapping("/sessions/{sessionId}/context")
    public ApiResponse<Void> updateContext(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> context) {
        try {
            sessionManager.updateContext(sessionId, context);
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("Error updating context: {}", e.getMessage(), e);
            return ApiResponse.error(500, "Failed to update context");
        }
    }
    
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("activeSessions", sessionManager.getActiveSessionCount());
        data.put("llmAvailable", chatService.isLLMAvailable());
        return ApiResponse.success(data);
    }
}
