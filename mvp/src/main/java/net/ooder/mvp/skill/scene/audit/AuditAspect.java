package net.ooder.mvp.skill.scene.audit;

import net.ooder.mvp.skill.scene.dto.audit.AuditEventType;
import net.ooder.mvp.skill.scene.dto.audit.AuditLogDTO;
import net.ooder.mvp.skill.scene.dto.audit.AuditResultType;
import net.ooder.mvp.skill.scene.service.AuditService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    @Autowired(required = false)
    private AuditService auditService;

    @Around("@annotation(auditable)")
    public Object auditOperation(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        long startTime = System.currentTimeMillis();
        String recordId = "audit-" + UUID.randomUUID().toString().substring(0, 8);
        
        AuditLogDTO auditLog = new AuditLogDTO();
        auditLog.setRecordId(recordId);
        auditLog.setEventType(auditable.eventType());
        auditLog.setAction(auditable.action());
        auditLog.setResourceType(auditable.resourceType());
        auditLog.setTimestamp(startTime);
        
        String userId = extractUserId(joinPoint);
        auditLog.setUserId(userId);
        
        String resourceId = extractResourceId(joinPoint);
        auditLog.setResourceId(resourceId);
        
        String ipAddress = getClientIpAddress();
        auditLog.setIpAddress(ipAddress);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("method", joinPoint.getSignature().getName());
        metadata.put("class", joinPoint.getTarget().getClass().getSimpleName());
        auditLog.setMetadata(metadata);
        
        Object result = null;
        Throwable exception = null;
        
        try {
            result = joinPoint.proceed();
            auditLog.setResult(AuditResultType.SUCCESS);
            auditLog.setDetail(auditable.action() + " 成功");
            
        } catch (Throwable e) {
            exception = e;
            auditLog.setResult(AuditResultType.FAILURE);
            auditLog.setDetail(auditable.action() + " 失败: " + e.getMessage());
            metadata.put("error", e.getClass().getSimpleName());
            metadata.put("errorMessage", e.getMessage());
        }
        
        long duration = System.currentTimeMillis() - startTime;
        metadata.put("duration", duration);
        
        if (auditService != null) {
            try {
                auditService.logEvent(auditLog);
                log.debug("[Audit] Recorded: {} - {} - {} ({}ms)", 
                    auditLog.getEventType().getName(), auditLog.getAction(), 
                    auditLog.getResult().getName(), duration);
            } catch (Exception e) {
                log.error("[Audit] Failed to record audit log: {}", e.getMessage());
            }
        }
        
        if (exception != null) {
            throw exception;
        }
        
        return result;
    }
    
    private String extractUserId(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].toLowerCase().contains("userid") || 
                paramNames[i].toLowerCase().contains("user_id") ||
                paramNames[i].equals("userId")) {
                return args[i] != null ? args[i].toString() : "unknown";
            }
        }
        
        String currentUserId = getCurrentUserId();
        return currentUserId != null ? currentUserId : "system";
    }
    
    private String extractResourceId(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < paramNames.length; i++) {
            String name = paramNames[i].toLowerCase();
            if (name.contains("id") && !name.contains("user")) {
                return args[i] != null ? args[i].toString() : null;
            }
        }
        
        return null;
    }
    
    private String getCurrentUserId() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                String userId = request.getHeader("X-User-Id");
                if (userId != null && !userId.isEmpty()) {
                    return userId;
                }
                
                Object userAttr = request.getAttribute("currentUser");
                if (userAttr != null) {
                    return userAttr.toString();
                }
            }
        } catch (Exception e) {
            log.debug("[Audit] Could not extract user ID: {}", e.getMessage());
        }
        return null;
    }
    
    private String getClientIpAddress() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                if (ip != null && ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        } catch (Exception e) {
            log.debug("[Audit] Could not extract IP address: {}", e.getMessage());
        }
        return "unknown";
    }
    
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
