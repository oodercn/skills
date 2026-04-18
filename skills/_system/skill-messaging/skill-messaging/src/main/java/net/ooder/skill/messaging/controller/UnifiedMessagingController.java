package net.ooder.skill.messaging.controller;

import net.ooder.skill.messaging.dto.ExecuteActionRequest;
import net.ooder.skill.messaging.dto.WsTokenRequest;
import net.ooder.skill.messaging.dto.RefreshTokenRequest;
import net.ooder.skill.messaging.service.UnifiedMessagingService;
import net.ooder.skill.messaging.service.UnifiedSessionService;
import net.ooder.skill.messaging.service.UnifiedWebSocketService;
import net.ooder.spi.messaging.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v2/messaging")
public class UnifiedMessagingController {
    
    @Autowired
    private UnifiedMessagingService messagingService;
    
    @Autowired
    private UnifiedSessionService sessionService;
    
    @Autowired
    private UnifiedWebSocketService webSocketService;
    
    @PostMapping("/sessions")
    public ResponseEntity<UnifiedSession> createSession(@RequestBody CreateSessionRequest request) {
        log.info("[API v2] Creating session for user: {}", request.getUserId());
        UnifiedSession session = sessionService.createSession(request);
        return ResponseEntity.ok(session);
    }
    
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<UnifiedSession> getSession(@PathVariable String sessionId) {
        log.info("[API v2] Getting session: {}", sessionId);
        UnifiedSession session = sessionService.getSession(sessionId);
        return session != null ? ResponseEntity.ok(session) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/sessions")
    public ResponseEntity<List<UnifiedSession>> listSessions(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) SessionType type,
            @RequestParam(defaultValue = "20") int limit) {
        log.info("[API v2] Listing sessions for user: {}", userId);
        List<UnifiedSession> sessions = sessionService.listSessions(userId, type, limit);
        return ResponseEntity.ok(sessions);
    }
    
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId) {
        log.info("[API v2] Deleting session: {}", sessionId);
        sessionService.deleteSession(sessionId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/messages")
    public ResponseEntity<UnifiedMessage> sendMessage(@RequestBody SendMessageRequest request) {
        log.info("[API v2] Sending message to conversation: {}", request.getConversationId());
        UnifiedMessage message = messagingService.sendMessage(request);
        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<UnifiedMessage>> getMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Long after) {
        log.info("[API v2] Getting messages for conversation: {}", conversationId);
        List<UnifiedMessage> messages = messagingService.getMessages(conversationId, limit, before, after);
        return ResponseEntity.ok(messages);
    }
    
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String messageId,
            @RequestParam String conversationId,
            @RequestParam String userId) {
        log.info("[API v2] Marking message as read: {}", messageId);
        messagingService.markAsRead(conversationId, userId, messageId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/messages/{messageId}/reactions")
    public ResponseEntity<Void> addReaction(
            @PathVariable String messageId,
            @RequestParam String userId,
            @RequestParam String emoji) {
        log.info("[API v2] Adding reaction to message: {}", messageId);
        messagingService.addReaction(messageId, userId, emoji);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/messages/{messageId}/reactions")
    public ResponseEntity<Void> removeReaction(
            @PathVariable String messageId,
            @RequestParam String userId,
            @RequestParam String emoji) {
        log.info("[API v2] Removing reaction from message: {}", messageId);
        messagingService.removeReaction(messageId, userId, emoji);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/messages/{messageId}/actions")
    public ResponseEntity<Void> executeAction(
            @PathVariable String messageId,
            @RequestBody ExecuteActionRequest request) {
        log.info("[API v2] Executing action on message: {}", messageId);
        messagingService.executeAction(messageId, request.getUserId(), request.getActionId(), request.getParams());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/ws/token")
    public ResponseEntity<WsToken> generateWsToken(@RequestBody WsTokenRequest request) {
        String userId = request.getUserId();
        String sceneGroupId = request.getSceneGroupId();
        long expireSeconds = request.getExpireSeconds() > 0 ? request.getExpireSeconds() : 3600L;
        
        log.info("[API v2] Generating WebSocket token for user: {}", userId);
        WsToken token = webSocketService.generateToken(userId, sceneGroupId, expireSeconds);
        return ResponseEntity.ok(token);
    }
    
    @PostMapping("/ws/token/refresh")
    public ResponseEntity<WsToken> refreshWsToken(@RequestBody RefreshTokenRequest request) {
        String currentToken = request.getCurrentToken();
        log.info("[API v2] Refreshing WebSocket token");
        WsToken token = webSocketService.refreshToken(currentToken);
        return ResponseEntity.ok(token);
    }
}
