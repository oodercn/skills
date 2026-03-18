package net.ooder.mvp.skill.scene.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExecutionWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutionWebSocketHandler.class);
    
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToExecution = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> executionToSession = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        log.info("[WebSocket] Connection established: {}", sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("[WebSocket] Received message: {}", payload);
        
        if (payload.startsWith("subscribe:")) {
            String executionId = payload.substring(10);
            subscribeToExecution(session, executionId);
        } else if (payload.startsWith("unsubscribe:")) {
            String executionId = payload.substring(12);
            unsubscribeFromExecution(session, executionId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        
        String executionId = sessionToExecution.remove(sessionId);
        if (executionId != null) {
            executionToSession.remove(executionId);
        }
        
        log.info("[WebSocket] Connection closed: {}, status: {}", sessionId, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[WebSocket] Transport error for session {}: {}", session.getId(), exception.getMessage());
    }

    private void subscribeToExecution(WebSocketSession session, String executionId) {
        String sessionId = session.getId();
        sessionToExecution.put(sessionId, executionId);
        executionToSession.put(executionId, session);
        
        log.info("[WebSocket] Session {} subscribed to execution {}", sessionId, executionId);
        
        sendMessage(session, "{\"type\":\"subscribed\",\"executionId\":\"" + executionId + "\"}");
    }

    private void unsubscribeFromExecution(WebSocketSession session, String executionId) {
        String sessionId = session.getId();
        sessionToExecution.remove(sessionId);
        executionToSession.remove(executionId);
        
        log.info("[WebSocket] Session {} unsubscribed from execution {}", sessionId, executionId);
    }

    public void broadcastExecutionUpdate(String executionId, String type, Object data) {
        WebSocketSession session = executionToSession.get(executionId);
        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> message = new java.util.HashMap<>();
                message.put("type", type);
                message.put("executionId", executionId);
                message.put("data", data);
                message.put("timestamp", System.currentTimeMillis());
                
                String json = toJson(message);
                sendMessage(session, json);
            } catch (Exception e) {
                log.error("[WebSocket] Failed to broadcast update: {}", e.getMessage());
            }
        }
    }

    public void broadcastStatusChange(String executionId, String status, String message) {
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("status", status);
        data.put("message", message);
        broadcastExecutionUpdate(executionId, "status", data);
    }

    public void broadcastProgress(String executionId, int current, int total, String detail) {
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("current", current);
        data.put("total", total);
        data.put("progress", total > 0 ? (current * 100 / total) : 0);
        data.put("detail", detail);
        broadcastExecutionUpdate(executionId, "progress", data);
    }

    public void broadcastResult(String executionId, Object result) {
        broadcastExecutionUpdate(executionId, "result", result);
    }

    public void broadcastError(String executionId, String error) {
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("error", error);
        broadcastExecutionUpdate(executionId, "error", data);
    }

    private void sendMessage(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.error("[WebSocket] Failed to send message: {}", e.getMessage());
        }
    }

    private String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value == null) {
                sb.append("null");
            } else if (value instanceof String) {
                sb.append("\"").append(escapeJson((String) value)).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                sb.append(value);
            } else {
                sb.append("\"").append(escapeJson(value.toString())).append("\"");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
