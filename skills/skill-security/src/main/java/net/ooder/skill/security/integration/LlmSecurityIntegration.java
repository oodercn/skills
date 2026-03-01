package net.ooder.skill.security.integration;

import net.ooder.skill.security.dto.audit.*;
import net.ooder.skill.security.dto.key.*;
import net.ooder.skill.security.service.AuditService;
import net.ooder.skill.security.service.KeyManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LlmSecurityIntegration {
    
    private static final Logger log = LoggerFactory.getLogger(LlmSecurityIntegration.class);
    
    @Autowired
    private KeyManagementService keyService;
    
    @Autowired
    private AuditService auditService;
    
    public String getLlmApiKey(String provider, String userId, String sceneId) {
        String keyName = "llm-" + provider.toLowerCase();
        ApiKeyDTO key = keyService.getKeyByName(keyName);
        
        if (key == null) {
            key = findKeyByProvider(provider);
        }
        
        if (key == null) {
            log.warn("LLM API key not found for provider: {}", provider);
            auditService.log(AuditLogDTO.failure(AuditEventType.LLM_KEY_USE, userId, provider));
            return null;
        }
        
        if (!keyService.checkAccess(key.getKeyId(), userId, sceneId)) {
            log.warn("Access denied to LLM API key: {} for user: {}", key.getKeyId(), userId);
            auditService.log(AuditLogDTO.denied(AuditEventType.LLM_KEY_USE, userId, key.getKeyId()));
            return null;
        }
        
        String rawKey = keyService.useKey(key.getKeyId(), userId, sceneId);
        
        if (rawKey != null) {
            auditService.log(AuditLogDTO.success(AuditEventType.LLM_KEY_USE, userId, key.getKeyId()));
        }
        
        return rawKey;
    }
    
    private ApiKeyDTO findKeyByProvider(String provider) {
        List<ApiKeyDTO> keys = keyService.listKeys(KeyType.LLM_API_KEY, "ACTIVE");
        
        for (ApiKeyDTO key : keys) {
            if (provider.equalsIgnoreCase(key.getProvider())) {
                return key;
            }
        }
        
        return null;
    }
    
    public void logLlmCall(String userId, String provider, String model, int tokenCount, AuditResult result) {
        AuditLogDTO logEntry = new AuditLogDTO();
        logEntry.setEventType(AuditEventType.LLM_CALL);
        logEntry.setUserId(userId);
        logEntry.setResourceType("LLM");
        logEntry.setResourceId(provider + "/" + model);
        logEntry.setResult(result);
        logEntry.setTimestamp(System.currentTimeMillis());
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("provider", provider);
        metadata.put("model", model);
        metadata.put("tokenCount", tokenCount);
        logEntry.setMetadata(metadata);
        
        auditService.log(logEntry);
    }
    
    public LlmSecurityReport getLlmSecurityReport(String provider) {
        LlmSecurityReport report = new LlmSecurityReport();
        report.setProvider(provider);
        
        ApiKeyDTO key = findKeyByProvider(provider);
        if (key != null) {
            report.setKeyExists(true);
            report.setKeyId(key.getKeyId());
            report.setKeyStatus(key.getStatus());
            report.setUseCount(key.getUseCount());
            
            KeyUsageStats stats = keyService.getUsageStats(key.getKeyId());
            if (stats != null) {
                report.setTodayUseCount(stats.getTodayUseCount());
                report.setTotalUseCount(stats.getTotalUseCount());
            }
        } else {
            report.setKeyExists(false);
        }
        
        return report;
    }
    
    public boolean checkLlmAccess(String userId, String provider, String sceneId) {
        ApiKeyDTO key = findKeyByProvider(provider);
        
        if (key == null) {
            return false;
        }
        
        return keyService.checkAccess(key.getKeyId(), userId, sceneId);
    }
    
    public static class LlmSecurityReport {
        private String provider;
        private boolean keyExists;
        private String keyId;
        private String keyStatus;
        private int useCount;
        private int todayUseCount;
        private int totalUseCount;
        
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public boolean isKeyExists() { return keyExists; }
        public void setKeyExists(boolean keyExists) { this.keyExists = keyExists; }
        public String getKeyId() { return keyId; }
        public void setKeyId(String keyId) { this.keyId = keyId; }
        public String getKeyStatus() { return keyStatus; }
        public void setKeyStatus(String keyStatus) { this.keyStatus = keyStatus; }
        public int getUseCount() { return useCount; }
        public void setUseCount(int useCount) { this.useCount = useCount; }
        public int getTodayUseCount() { return todayUseCount; }
        public void setTodayUseCount(int todayUseCount) { this.todayUseCount = todayUseCount; }
        public int getTotalUseCount() { return totalUseCount; }
        public void setTotalUseCount(int totalUseCount) { this.totalUseCount = totalUseCount; }
    }
}
