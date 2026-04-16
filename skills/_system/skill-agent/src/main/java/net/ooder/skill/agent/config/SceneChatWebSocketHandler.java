package net.ooder.skill.agent.config;

import net.ooder.scene.websocket.auth.WebSocketAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SceneChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(SceneChatWebSocketHandler.class);

    @Autowired(required = false)
    private WebSocketAuthService webSocketAuthService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = extractToken(session);
        String sceneGroupId = extractSceneGroupId(session);

        if (token != null && webSocketAuthService != null) {
            var result = webSocketAuthService.validateToken(token);
            if (result == null || !result.isValid()) {
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            String userId = result.getUserId();
            webSocketAuthService.registerConnection(session.getId(), userId, sceneGroupId);
            log.info("[SceneChatWS] Connection established: sessionId={}, userId={}, sceneGroup={}", 
                session.getId(), userId, sceneGroupId);
        } else {
            log.info("[SceneChatWS] Anonymous connection: sessionId={}, sceneGroup={}", session.getId(), sceneGroupId);
        }

        sessions.put(session.getId(), session);
        send(session, Map.of("type", "connected", "authenticated", token != null));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) payload.get("type");

            switch (type) {
                case "ping":
                    send(session, Map.of("type", "pong"));
                    break;
                case "message":
                    broadcastToSceneGroup(session, payload);
                    break;
                case "typing":
                    broadcastToSceneGroup(session, Map.of("type", "typing", 
                        "userId", getUserId(session), "isTyping", payload.get("isTyping")));
                    break;
                case "read_ack":
                    handleReadAck(session, payload);
                    break;
                case "join_scene":
                    log.debug("[SceneChatWS] User {} joined scene {}", getUserId(session), payload.get("sceneGroupId"));
                    break;
                case "leave_scene":
                    log.debug("[SceneChatWS] User {} left scene {}", getUserId(session), payload.get("sceneGroupId"));
                    break;
                default:
                    log.warn("[SceneChatWS] Unknown message type: {}", type);
            }
        } catch (Exception e) {
            log.error("[SceneChatWS] Error handling message: {}", e.getMessage());
            sendError(session, "Failed to process message");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        if (webSocketAuthService != null) {
            webSocketAuthService.unregisterConnection(session.getId());
        }
        log.info("[SceneChatWS] Connection closed: sessionId={}, status={}", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[SceneChatWS] Transport error: sessionId={}, error={}", session.getId(), exception.getMessage());
    }

    private void broadcastToSceneGroup(WebSocketSession sender, Map<String, Object> message) {
        String sceneGroupId = extractSceneGroupId(sender);
        String senderId = sender.getId();

        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            WebSocketSession target = entry.getValue();
            if (!target.isOpen() || entry.getKey().equals(senderId)) continue;

            String targetScene = extractSceneGroupId(target);
            if (sceneGroupId.equals(targetScene)) {
                Map<String, Object> msg = new java.util.LinkedHashMap<>(message);
                msg.putIfAbsent("type", "chat_message");
                msg.put("senderId", getUserId(sender));
                msg.put("timestamp", System.currentTimeMillis());
                send(target, msg);
            }
        }
    }

    private void handleReadAck(WebSocketSession session, Map<String, Object> payload) {
        String messageId = (String) payload.get("messageId");
        log.debug("[SceneChatWS] Read ack from user {} for message {}", getUserId(session), messageId);
    }

    private void send(WebSocketSession session, Object data) {
        if (session.isOpen()) {
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(data)));
                }
            } catch (IOException e) {
                log.error("[SceneChatWS] Send error: {}", e.getMessage());
            }
        }
    }

    private void sendError(WebSocketSession session, String error) {
        send(session, Map.of("type", "error", "error", error));
    }

    private String extractToken(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if ("token".equals(kv[0]) && kv.length > 1) return kv[1];
        }
        return null;
    }

    private String extractSceneGroupId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length - 2; i++) {
            if ("scene-groups".equals(parts[i])) return parts[i + 1];
        }
        return null;
    }

    private String getUserId(WebSocketSession session) {
        if (webSocketAuthService != null) {
            var connInfo = webSocketAuthService.getConnection(session.getId());
            if (connInfo != null) return connInfo.getUserId();
        }
        return "anonymous";
    }

    public int getActiveConnections() { return sessions.size(); }
}
