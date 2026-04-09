package net.ooder.bpm.designer.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint("/ws/derivation")
public class DerivationWebSocketHandler {
    
    private static final Logger log = LoggerFactory.getLogger(DerivationWebSocketHandler.class);
    
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    
    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
        log.info("WebSocket connection opened: {}", session.getId());
        sendMessage(session, createMessage("connected", "WebSocket connected successfully"));
    }
    
    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
        log.info("WebSocket connection closed: {}", session.getId());
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        log.debug("Received message from {}: {}", session.getId(), message);
    }
    
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket error for session {}: {}", session.getId(), error.getMessage());
        sessions.remove(session.getId());
    }
    
    public void broadcastProgress(String derivationId, int progress, String stage, String message) {
        String jsonMessage = createProgressMessage(derivationId, progress, stage, message);
        broadcast(jsonMessage);
    }
    
    public void sendProgress(String sessionId, String derivationId, int progress, String stage, String message) {
        Session session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            sendMessage(session, createProgressMessage(derivationId, progress, stage, message));
        }
    }
    
    public void broadcastResult(String derivationId, String type, Object result) {
        String jsonMessage = createResultMessage(derivationId, type, result);
        broadcast(jsonMessage);
    }
    
    public void sendResult(String sessionId, String derivationId, String type, Object result) {
        Session session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            sendMessage(session, createResultMessage(derivationId, type, result));
        }
    }
    
    public void broadcastError(String derivationId, String error) {
        String jsonMessage = createErrorMessage(derivationId, error);
        broadcast(jsonMessage);
    }
    
    public void sendError(String sessionId, String derivationId, String error) {
        Session session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            sendMessage(session, createErrorMessage(derivationId, error));
        }
    }
    
    private void broadcast(String message) {
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                sendMessage(session, message);
            }
        });
    }
    
    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("Failed to send message to session {}: {}", session.getId(), e.getMessage());
        }
    }
    
    private String createMessage(String type, String message) {
        return String.format("{\"type\":\"%s\",\"message\":\"%s\"}", type, message);
    }
    
    private String createProgressMessage(String derivationId, int progress, String stage, String message) {
        return String.format(
            "{\"type\":\"progress\",\"derivationId\":\"%s\",\"progress\":%d,\"stage\":\"%s\",\"message\":\"%s\"}",
            derivationId, progress, stage, message
        );
    }
    
    private String createResultMessage(String derivationId, String resultType, Object result) {
        return String.format(
            "{\"type\":\"result\",\"derivationId\":\"%s\",\"resultType\":\"%s\",\"data\":%s}",
            derivationId, resultType, result.toString()
        );
    }
    
    private String createErrorMessage(String derivationId, String error) {
        return String.format(
            "{\"type\":\"error\",\"derivationId\":\"%s\",\"error\":\"%s\"}",
            derivationId, error
        );
    }
    
    public int getActiveConnections() {
        return sessions.size();
    }
}
