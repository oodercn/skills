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
public class CapabilitySecurityIntegration {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilitySecurityIntegration.class);
    
    @Autowired
    private KeyManagementService keyService;
    
    @Autowired
    private AuditService auditService;
    
    private final Map<String, CapabilitySecurityPolicy> capabilityPolicies = new HashMap<>();
    
    public void registerCapabilitySecurity(String capabilityId, CapabilitySecurityPolicy policy) {
        capabilityPolicies.put(capabilityId, policy);
        
        if (policy.getRequiredKeys() != null) {
            for (String keyId : policy.getRequiredKeys()) {
                KeyGrantRequest request = new KeyGrantRequest();
                request.setKeyId(keyId);
                keyService.grantAccess(keyId, request);
            }
        }
        
        auditService.log(AuditLogDTO.success(AuditEventType.PERMISSION_GRANT, "system", capabilityId));
        log.info("Registered security policy for capability: {}", capabilityId);
    }
    
    public void unregisterCapabilitySecurity(String capabilityId) {
        CapabilitySecurityPolicy policy = capabilityPolicies.remove(capabilityId);
        
        if (policy != null && policy.getRequiredKeys() != null) {
            for (String keyId : policy.getRequiredKeys()) {
                keyService.revokeAccess(keyId, capabilityId, "capability");
            }
        }
        
        auditService.log(AuditLogDTO.success(AuditEventType.PERMISSION_REVOKE, "system", capabilityId));
        log.info("Unregistered security policy for capability: {}", capabilityId);
    }
    
    public boolean checkCapabilityAccess(String userId, String capabilityId, String action) {
        CapabilitySecurityPolicy policy = capabilityPolicies.get(capabilityId);
        
        if (policy == null) {
            log.debug("No security policy for capability: {}, allowing access", capabilityId);
            return true;
        }
        
        if (policy.getAllowedUsers() != null && !policy.getAllowedUsers().isEmpty()) {
            if (!policy.getAllowedUsers().contains(userId)) {
                auditService.log(AuditLogDTO.denied(AuditEventType.PERMISSION_CHECK, userId, capabilityId));
                return false;
            }
        }
        
        if (policy.getAllowedActions() != null && !policy.getAllowedActions().isEmpty()) {
            if (!policy.getAllowedActions().contains(action)) {
                auditService.log(AuditLogDTO.denied(AuditEventType.PERMISSION_CHECK, userId, capabilityId));
                return false;
            }
        }
        
        auditService.log(AuditLogDTO.success(AuditEventType.PERMISSION_CHECK, userId, capabilityId));
        return true;
    }
    
    public String getCapabilityKey(String capabilityId, String keyType, String userId) {
        CapabilitySecurityPolicy policy = capabilityPolicies.get(capabilityId);
        
        if (policy == null || policy.getRequiredKeys() == null) {
            return null;
        }
        
        String targetKeyId = null;
        for (String keyId : policy.getRequiredKeys()) {
            ApiKeyDTO key = keyService.getKey(keyId);
            if (key != null && key.getKeyType() != null && key.getKeyType().getCode().equals(keyType)) {
                targetKeyId = keyId;
                break;
            }
        }
        
        if (targetKeyId == null) {
            log.warn("No key of type {} found for capability: {}", keyType, capabilityId);
            return null;
        }
        
        String rawKey = keyService.useKey(targetKeyId, userId, null);
        
        if (rawKey != null) {
            auditService.log(AuditLogDTO.success(AuditEventType.KEY_USE, userId, targetKeyId));
        } else {
            auditService.log(AuditLogDTO.denied(AuditEventType.KEY_USE, userId, targetKeyId));
        }
        
        return rawKey;
    }
    
    public void logCapabilityEvent(String capabilityId, String userId, AuditEventType eventType, AuditResult result) {
        AuditLogDTO logEntry = new AuditLogDTO();
        logEntry.setEventType(eventType);
        logEntry.setUserId(userId);
        logEntry.setResourceId(capabilityId);
        logEntry.setResourceType("CAPABILITY");
        logEntry.setResult(result);
        logEntry.setTimestamp(System.currentTimeMillis());
        
        auditService.log(logEntry);
    }
    
    public CapabilitySecurityReport getSecurityReport(String capabilityId) {
        CapabilitySecurityReport report = new CapabilitySecurityReport();
        report.setCapabilityId(capabilityId);
        
        CapabilitySecurityPolicy policy = capabilityPolicies.get(capabilityId);
        if (policy != null) {
            report.setPolicyExists(true);
            report.setRequiredKeys(policy.getRequiredKeys() != null ? policy.getRequiredKeys() : new ArrayList<>());
            report.setAllowedUsers(policy.getAllowedUsers() != null ? policy.getAllowedUsers() : new ArrayList<>());
            report.setAllowedActions(policy.getAllowedActions() != null ? policy.getAllowedActions() : new ArrayList<>());
        } else {
            report.setPolicyExists(false);
        }
        
        return report;
    }
    
    public List<String> getCapabilitiesRequiringKey(String keyId) {
        List<String> result = new ArrayList<>();
        
        for (Map.Entry<String, CapabilitySecurityPolicy> entry : capabilityPolicies.entrySet()) {
            if (entry.getValue().getRequiredKeys() != null && 
                entry.getValue().getRequiredKeys().contains(keyId)) {
                result.add(entry.getKey());
            }
        }
        
        return result;
    }
    
    public static class CapabilitySecurityPolicy {
        private List<String> requiredKeys;
        private List<String> allowedUsers;
        private List<String> allowedRoles;
        private List<String> allowedActions;
        private int maxCallsPerMinute;
        private boolean requireApproval;
        
        public List<String> getRequiredKeys() { return requiredKeys; }
        public void setRequiredKeys(List<String> requiredKeys) { this.requiredKeys = requiredKeys; }
        public List<String> getAllowedUsers() { return allowedUsers; }
        public void setAllowedUsers(List<String> allowedUsers) { this.allowedUsers = allowedUsers; }
        public List<String> getAllowedRoles() { return allowedRoles; }
        public void setAllowedRoles(List<String> allowedRoles) { this.allowedRoles = allowedRoles; }
        public List<String> getAllowedActions() { return allowedActions; }
        public void setAllowedActions(List<String> allowedActions) { this.allowedActions = allowedActions; }
        public int getMaxCallsPerMinute() { return maxCallsPerMinute; }
        public void setMaxCallsPerMinute(int maxCallsPerMinute) { this.maxCallsPerMinute = maxCallsPerMinute; }
        public boolean isRequireApproval() { return requireApproval; }
        public void setRequireApproval(boolean requireApproval) { this.requireApproval = requireApproval; }
    }
    
    public static class CapabilitySecurityReport {
        private String capabilityId;
        private boolean policyExists;
        private List<String> requiredKeys;
        private List<String> allowedUsers;
        private List<String> allowedActions;
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public boolean isPolicyExists() { return policyExists; }
        public void setPolicyExists(boolean policyExists) { this.policyExists = policyExists; }
        public List<String> getRequiredKeys() { return requiredKeys; }
        public void setRequiredKeys(List<String> requiredKeys) { this.requiredKeys = requiredKeys; }
        public List<String> getAllowedUsers() { return allowedUsers; }
        public void setAllowedUsers(List<String> allowedUsers) { this.allowedUsers = allowedUsers; }
        public List<String> getAllowedActions() { return allowedActions; }
        public void setAllowedActions(List<String> allowedActions) { this.allowedActions = allowedActions; }
    }
}
