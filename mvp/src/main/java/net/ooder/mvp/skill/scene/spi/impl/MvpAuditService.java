package net.ooder.mvp.skill.scene.spi.impl;

import net.ooder.mvp.skill.scene.spi.AuditService;
import net.ooder.mvp.skill.scene.spi.audit.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MvpAuditService implements AuditService {
    
    private static final Logger log = LoggerFactory.getLogger(MvpAuditService.class);
    
    private final List<AuditEvent> auditLog = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, List<AuditEvent>> userAuditLogs = new ConcurrentHashMap<>();
    private final Map<String, List<AuditEvent>> sceneAuditLogs = new ConcurrentHashMap<>();
    
    @Override
    public void log(AuditEvent event) {
        if (event == null) {
            return;
        }
        
        if (event.getTimestamp() == 0) {
            event.setTimestamp(System.currentTimeMillis());
        }
        
        auditLog.add(event);
        
        if (event.getUserId() != null) {
            userAuditLogs.computeIfAbsent(event.getUserId(), k -> new ArrayList<>()).add(event);
        }
        
        if (event.getSceneId() != null) {
            sceneAuditLogs.computeIfAbsent(event.getSceneId(), k -> new ArrayList<>()).add(event);
        }
        
        log.info("[AUDIT] {} - {} - {} - {} - {}", 
            event.getEventType(), 
            event.getUserId(), 
            event.getAction(), 
            event.getMessage(),
            event.isSuccess() ? "SUCCESS" : "FAILURE");
    }
    
    @Override
    public void log(String eventType, String userId, String action, String message) {
        AuditEvent event = AuditEvent.builder()
            .eventType(eventType)
            .userId(userId)
            .action(action)
            .message(message)
            .success(true)
            .build();
        
        log(event);
    }
    
    @Override
    public void logSuccess(String eventType, String userId, String action, String message) {
        AuditEvent event = AuditEvent.builder()
            .eventType(eventType)
            .userId(userId)
            .action(action)
            .message(message)
            .success(true)
            .build();
        
        log(event);
    }
    
    @Override
    public void logFailure(String eventType, String userId, String action, String message, String error) {
        AuditEvent event = AuditEvent.builder()
            .eventType(eventType)
            .userId(userId)
            .action(action)
            .message(message + " - Error: " + error)
            .success(false)
            .build();
        
        log(event);
    }
    
    public List<AuditEvent> getAuditLogs() {
        return new ArrayList<>(auditLog);
    }
    
    public List<AuditEvent> getUserAuditLogs(String userId) {
        return new ArrayList<>(userAuditLogs.getOrDefault(userId, Collections.emptyList()));
    }
    
    public List<AuditEvent> getSceneAuditLogs(String sceneId) {
        return new ArrayList<>(sceneAuditLogs.getOrDefault(sceneId, Collections.emptyList()));
    }
    
    public List<AuditEvent> getAuditLogsByType(String eventType) {
        return auditLog.stream()
            .filter(e -> eventType.equals(e.getEventType()))
            .collect(Collectors.toList());
    }
    
    public void clearLogs() {
        auditLog.clear();
        userAuditLogs.clear();
        sceneAuditLogs.clear();
        log.info("Audit logs cleared");
    }
}
