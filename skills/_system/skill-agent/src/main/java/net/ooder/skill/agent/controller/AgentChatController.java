package net.ooder.skill.agent.controller;

import net.ooder.skill.agent.config.UnifiedInterfaceConfig.UnifiedInterfaceAdapter;
import net.ooder.skill.agent.dto.AgentChatMessageDTO;
import net.ooder.skill.agent.dto.SceneChatContextDTO;
import net.ooder.skill.agent.service.AgentChatService;
import net.ooder.skill.agent.dto.PageResult;
import net.ooder.skill.scene.dto.todo.TodoDTO;
import net.ooder.skill.agent.spi.UserService;
import net.ooder.scene.websocket.auth.WebSocketAuthService;
import net.ooder.scene.websocket.auth.WebSocketToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scene-groups/{sceneGroupId}/chat")
@Deprecated(since = "2.0.0", forRemoval = true)
public class AgentChatController {

    private static final Logger log = LoggerFactory.getLogger(AgentChatController.class);
    
    private static final long DEFAULT_TOKEN_EXPIRE_SECONDS = 3600;

    @Autowired
    private AgentChatService chatService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UnifiedInterfaceAdapter unifiedAdapter;

    @GetMapping("/context")
    public ResponseEntity<Map<String, Object>> getChatContext(
            @PathVariable String sceneGroupId) {
        
        log.info("Getting chat context for sceneGroupId: {}", sceneGroupId);
        String userId = getCurrentUserId();
        log.debug("Current userId: {}", userId);
        SceneChatContextDTO context = chatService.getChatContext(sceneGroupId, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", context);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages(
            @PathVariable String sceneGroupId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Long after,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "50") int pageSize) {
        
        log.info("Getting messages for sceneGroupId: {}, type: {}, pageNum: {}, pageSize: {}", 
            sceneGroupId, type, pageNum, pageSize);
        String userId = getCurrentUserId();
        PageResult<AgentChatMessageDTO> messages = chatService.getMessages(
            sceneGroupId, userId, type, before, after, pageNum, pageSize);
        log.debug("Retrieved {} messages", messages.getTotal());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", messages);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Map<String, Object>> getMessage(
            @PathVariable String sceneGroupId,
            @PathVariable String messageId) {
        
        AgentChatMessageDTO message = chatService.getMessage(messageId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", message);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @PathVariable String sceneGroupId,
            @RequestBody AgentChatMessageDTO message) {
        
        log.info("Sending message to sceneGroupId: {}, type: {}", sceneGroupId, message.getMessageType());
        String messageId = chatService.sendMessage(sceneGroupId, message);
        log.info("Message sent successfully, messageId: {}", messageId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", Map.of("messageId", messageId));
        return ResponseEntity.ok(result);
    }

    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable String sceneGroupId,
            @PathVariable String messageId) {
        
        String userId = getCurrentUserId();
        chatService.markAsRead(sceneGroupId, userId, messageId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/messages/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(
            @PathVariable String sceneGroupId,
            @RequestParam(required = false) String type) {
        
        String userId = getCurrentUserId();
        chatService.markAllAsRead(sceneGroupId, userId, type);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/unread-counts")
    public ResponseEntity<Map<String, Object>> getUnreadCounts(
            @PathVariable String sceneGroupId) {
        
        String userId = getCurrentUserId();
        Map<String, Integer> counts = chatService.getUnreadCounts(sceneGroupId, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", counts);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/messages/{messageId}/action")
    public ResponseEntity<Map<String, Object>> executeAction(
            @PathVariable String sceneGroupId,
            @PathVariable String messageId,
            @RequestBody Map<String, Object> body) {
        
        log.info("Executing action for messageId: {}", messageId);
        String userId = getCurrentUserId();
        String actionId = (String) body.get("actionId");
        log.debug("Action: {}, userId: {}", actionId, userId);
        @SuppressWarnings("unchecked")
        Map<String, Object> actionData = (Map<String, Object>) body.get("actionData");
        
        Object actionResult = chatService.executeMessageAction(
            sceneGroupId, messageId, userId, actionId, actionData);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", actionResult);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/todos")
    public ResponseEntity<Map<String, Object>> getTodos(
            @PathVariable String sceneGroupId,
            @RequestParam(required = false) String status) {
        
        String userId = getCurrentUserId();
        List<TodoDTO> todos = chatService.getTodos(sceneGroupId, userId, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", todos);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/todos/{todoId}/accept")
    public ResponseEntity<Map<String, Object>> acceptTodo(
            @PathVariable String sceneGroupId,
            @PathVariable String todoId) {
        
        String userId = getCurrentUserId();
        boolean success = chatService.acceptTodo(userId, todoId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", success ? "success" : "error");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/todos/{todoId}/reject")
    public ResponseEntity<Map<String, Object>> rejectTodo(
            @PathVariable String sceneGroupId,
            @PathVariable String todoId,
            @RequestBody(required = false) Map<String, Object> body) {
        
        String userId = getCurrentUserId();
        String reason = body != null ? (String) body.get("reason") : null;
        boolean success = chatService.rejectTodo(userId, todoId, reason);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", success ? "success" : "error");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/todos/{todoId}/delegate")
    public ResponseEntity<Map<String, Object>> delegateTodo(
            @PathVariable String sceneGroupId,
            @PathVariable String todoId,
            @RequestBody Map<String, Object> body) {
        
        String userId = getCurrentUserId();
        String toUserId = (String) body.get("toUserId");
        boolean success = chatService.delegateTodo(userId, todoId, toUserId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", success ? "success" : "error");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/todos/{todoId}/complete")
    public ResponseEntity<Map<String, Object>> completeTodo(
            @PathVariable String sceneGroupId,
            @PathVariable String todoId) {
        
        String userId = getCurrentUserId();
        boolean success = chatService.completeTodo(userId, todoId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", success ? "success" : "error");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/messages/{messageId}/reactions")
    public ResponseEntity<Map<String, Object>> addReaction(
            @PathVariable String sceneGroupId,
            @PathVariable String messageId,
            @RequestBody Map<String, String> body) {
        
        String userId = getCurrentUserId();
        String emoji = body.get("emoji");
        chatService.addReaction(messageId, userId, emoji);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/messages/{messageId}/reactions")
    public ResponseEntity<Map<String, Object>> removeReaction(
            @PathVariable String sceneGroupId,
            @PathVariable String messageId,
            @RequestParam String emoji) {
        
        String userId = getCurrentUserId();
        chatService.removeReaction(messageId, userId, emoji);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        return ResponseEntity.ok(result);
    }

    private String getCurrentUserId() {
        try {
            return userService.getCurrentUserId();
        } catch (Exception e) {
            return "anonymous";
        }
    }
    
    @PostMapping("/ws-token")
    public ResponseEntity<Map<String, Object>> generateWsToken(
            @PathVariable String sceneGroupId,
            @RequestBody(required = false) Map<String, Object> body) {
        
        String userId = getCurrentUserId();
        log.info("Generating WebSocket token for user: {}, sceneGroup: {}", userId, sceneGroupId);
        
        if (!unifiedAdapter.hasWebSocketAuthService()) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "WebSocket authentication not available");
            return ResponseEntity.ok(result);
        }
        
        WebSocketAuthService authService = unifiedAdapter.getWebSocketAuthService();
        
        if (!authService.checkConnectionPermission(userId, sceneGroupId)) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "No permission to access this scene group");
            return ResponseEntity.status(403).body(result);
        }
        
        long expireSeconds = DEFAULT_TOKEN_EXPIRE_SECONDS;
        if (body != null && body.containsKey("expireSeconds")) {
            expireSeconds = ((Number) body.get("expireSeconds")).longValue();
        }
        
        WebSocketToken token = authService.generateToken(userId, sceneGroupId, expireSeconds);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", Map.of(
            "token", token.getToken(),
            "tokenId", token.getTokenId(),
            "expireAt", token.getExpireAt(),
            "sceneGroupId", sceneGroupId
        ));
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/ws-token/refresh")
    public ResponseEntity<Map<String, Object>> refreshWsToken(
            @PathVariable String sceneGroupId,
            @RequestBody Map<String, Object> body) {
        
        String currentToken = (String) body.get("token");
        if (currentToken == null || currentToken.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "Token is required");
            return ResponseEntity.badRequest().body(result);
        }
        
        log.info("Refreshing WebSocket token for sceneGroup: {}", sceneGroupId);
        
        if (!unifiedAdapter.hasWebSocketAuthService()) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "WebSocket authentication not available");
            return ResponseEntity.ok(result);
        }
        
        WebSocketAuthService authService = unifiedAdapter.getWebSocketAuthService();
        
        try {
            WebSocketToken newToken = authService.refreshToken(currentToken);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("data", Map.of(
                "token", newToken.getToken(),
                "tokenId", newToken.getTokenId(),
                "expireAt", newToken.getExpireAt(),
                "sceneGroupId", newToken.getSceneGroupId()
            ));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.warn("Failed to refresh token: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "Invalid or expired token");
            return ResponseEntity.status(401).body(result);
        }
    }
}
