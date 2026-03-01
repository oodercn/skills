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
public class SceneSecurityIntegration {
    
    private static final Logger log = LoggerFactory.getLogger(SceneSecurityIntegration.class);
    
    @Autowired
    private KeyManagementService keyService;
    
    @Autowired
    private AuditService auditService;
    
    public void setupSceneSecurity(String sceneId, SceneSecurityConfig config) {
        log.info("Setting up security for scene: {}", sceneId);
        
        if (config != null && config.getRequiredKeys() != null) {
            for (String keyId : config.getRequiredKeys()) {
                KeyGrantRequest request = new KeyGrantRequest();
                request.setKeyId(keyId);
                request.setSceneId(sceneId);
                keyService.grantAccess(keyId, request);
            }
        }
        
        auditService.log(AuditLogDTO.success(AuditEventType.SCENE_CREATE, "system", sceneId));
    }
    
    public void cleanupSceneSecurity(String sceneId) {
        log.info("Cleaning up security for scene: {}", sceneId);
        
        List<ApiKeyDTO> keys = keyService.listKeys(null, "ACTIVE");
        for (ApiKeyDTO key : keys) {
            if (key.getAllowedScenes() != null && key.getAllowedScenes().contains(sceneId)) {
                keyService.revokeAccess(key.getKeyId(), sceneId, "scene");
            }
        }
        
        auditService.log(AuditLogDTO.success(AuditEventType.SCENE_END, "system", sceneId));
    }
    
    public boolean checkSceneAccess(String userId, String sceneId, String action) {
        log.debug("Checking scene access: userId={}, sceneId={}, action={}", userId, sceneId, action);
        
        auditService.log(AuditLogDTO.of(
            AuditEventType.SCENE_START,
            userId,
            sceneId,
            AuditResult.SUCCESS
        ));
        
        return true;
    }
    
    public String getSceneKey(String sceneId, String keyType, String userId) {
        String keyName = keyType.toLowerCase() + "-" + sceneId;
        ApiKeyDTO key = keyService.getKeyByName(keyName);
        
        if (key == null) {
            log.warn("Scene key not found: {}", keyName);
            return null;
        }
        
        String rawKey = keyService.useKey(key.getKeyId(), userId, sceneId);
        
        if (rawKey != null) {
            auditService.log(AuditLogDTO.success(AuditEventType.KEY_USE, userId, key.getKeyId()));
        } else {
            auditService.log(AuditLogDTO.denied(AuditEventType.KEY_USE, userId, key.getKeyId()));
        }
        
        return rawKey;
    }
    
    public void logSceneEvent(String sceneId, String userId, AuditEventType eventType, AuditResult result) {
        AuditLogDTO logEntry = new AuditLogDTO();
        logEntry.setEventType(eventType);
        logEntry.setUserId(userId);
        logEntry.setResourceId(sceneId);
        logEntry.setResourceType("SCENE");
        logEntry.setResult(result);
        logEntry.setTimestamp(System.currentTimeMillis());
        
        auditService.log(logEntry);
    }
    
    public SceneSecurityReport getSecurityReport(String sceneId) {
        SceneSecurityReport report = new SceneSecurityReport();
        report.setSceneId(sceneId);
        
        List<ApiKeyDTO> allKeys = keyService.listKeys(null, null);
        List<String> sceneKeys = new ArrayList<>();
        
        for (ApiKeyDTO key : allKeys) {
            if (key.getAllowedScenes() != null && key.getAllowedScenes().contains(sceneId)) {
                sceneKeys.add(key.getKeyId());
            }
        }
        
        report.setAuthorizedKeys(sceneKeys);
        report.setKeyCount(sceneKeys.size());
        
        return report;
    }
    
    public static class SceneSecurityConfig {
        private List<String> requiredKeys;
        private boolean dataIsolation;
        private boolean crossOrgAllowed;
        private List<String> allowedAgents;
        private int maxConcurrency;
        
        public List<String> getRequiredKeys() { return requiredKeys; }
        public void setRequiredKeys(List<String> requiredKeys) { this.requiredKeys = requiredKeys; }
        public boolean isDataIsolation() { return dataIsolation; }
        public void setDataIsolation(boolean dataIsolation) { this.dataIsolation = dataIsolation; }
        public boolean isCrossOrgAllowed() { return crossOrgAllowed; }
        public void setCrossOrgAllowed(boolean crossOrgAllowed) { this.crossOrgAllowed = crossOrgAllowed; }
        public List<String> getAllowedAgents() { return allowedAgents; }
        public void setAllowedAgents(List<String> allowedAgents) { this.allowedAgents = allowedAgents; }
        public int getMaxConcurrency() { return maxConcurrency; }
        public void setMaxConcurrency(int maxConcurrency) { this.maxConcurrency = maxConcurrency; }
    }
    
    public static class SceneSecurityReport {
        private String sceneId;
        private List<String> authorizedKeys;
        private int keyCount;
        private long lastAuditTime;
        
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public List<String> getAuthorizedKeys() { return authorizedKeys; }
        public void setAuthorizedKeys(List<String> authorizedKeys) { this.authorizedKeys = authorizedKeys; }
        public int getKeyCount() { return keyCount; }
        public void setKeyCount(int keyCount) { this.keyCount = keyCount; }
        public long getLastAuditTime() { return lastAuditTime; }
        public void setLastAuditTime(long lastAuditTime) { this.lastAuditTime = lastAuditTime; }
    }
}
