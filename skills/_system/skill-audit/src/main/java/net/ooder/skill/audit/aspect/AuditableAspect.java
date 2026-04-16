package net.ooder.skill.audit.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.skill.audit.annotation.Auditable;
import net.ooder.skill.audit.dto.AuditLogDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

@Aspect
@Component
public class AuditableAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditableAspect.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        AuditLogDTO auditLog = new AuditLogDTO();
        auditLog.setRequestId(requestId);
        auditLog.setTimestamp(System.currentTimeMillis());
        auditLog.setAction(auditable.action().isEmpty() ? joinPoint.getSignature().getName() : auditable.action());
        auditLog.setResourceType(auditable.resourceType());

        if (auditable.logParams()) {
            try {
                String params = objectMapper.writeValueAsString(joinPoint.getArgs());
                auditLog.setDetail(params.length() > 2000 ? params.substring(0, 2000) : params);
            } catch (Exception e) {
                auditLog.setDetail("参数序列化失败: " + e.getMessage());
            }
        }

        try {
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                auditLog.setIpAddress(getClientIp(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
                String userId = request.getHeader("X-User-Id");
                if (userId != null) auditLog.setUserId(userId);
                String tenantId = request.getHeader("X-Tenant-Id");
                if (tenantId != null) auditLog.setDetail((auditLog.getDetail() != null ? auditLog.getDetail() + " | " : "") + "tenant=" + tenantId);
            }
        } catch (Exception ignored) {}

        Object result;
        try {
            result = joinPoint.proceed();
            auditLog.setResult(AuditLogDTO.Result.SUCCESS);

            if (auditable.logResult() && result != null) {
                try {
                    String resultStr = objectMapper.writeValueAsString(result);
                    auditLog.setDetail((auditLog.getDetail() != null ? auditLog.getDetail() + " | " : "") + "result=" + (resultStr.length() > 1000 ? resultStr.substring(0, 1000) : resultStr));
                } catch (Exception ignored) {}
            }

        } catch (Throwable ex) {
            auditLog.setResult(AuditLogDTO.Result.ERROR);
            auditLog.setDetail((auditLog.getDetail() != null ? auditLog.getDetail() + " | " : "") + "error=" + ex.getClass().getSimpleName() + ": " + ex.getMessage());

            if (!auditable.logException()) {
                throw ex;
            }
            result = null;
        }

        auditLog.setDuration(System.currentTimeMillis() - startTime);

        log.info("[AUDIT] {} | {} | {}ms | {} | {}",
                auditLog.getAction(),
                auditLog.getResult().getName(),
                auditLog.getDuration(),
                auditLog.getUserId(),
                auditLog.getResourceType());

        return result;
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            org.springframework.web.context.request.RequestAttributes attrs =
                    org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attrs instanceof org.springframework.web.context.request.ServletRequestAttributes) {
                return ((ServletRequestAttributes) attrs).getRequest();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
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
}
