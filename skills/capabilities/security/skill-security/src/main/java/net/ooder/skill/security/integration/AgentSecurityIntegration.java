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
public class AgentSecurityIntegration {
    
    private static final Logger log = LoggerFactory.getLogger(AgentSecurityIntegration.class);
    
    @Autowired
    private KeyManagementService keyService;
    
    @Autowired
    private AuditService auditService;
    
    private final Map<String, AgentSecurityContext> agentContexts = new HashMap<>();
    
    public ApiKeyDTO registerAgent(String agentId, String agentName, String agentType) {
        log.info("Registering agent: {} ({})", agentId, agentName);
        
        String rawKey = keyService.generateKey();
        
        ApiKeyDTO key = new ApiKeyDTO();
        key.setKeyId("agent-" + agentId);
        key.setKeyName("Agent Key: " + agentName);
        key.setKeyType(KeyType.AGENT_KEY);
        key.setProvider(agentType);
        key.getAllowedUsers().add(agentId);
        
        KeyCreateRequest request = new KeyCreateRequest();
        request.setKeyName("Agent Key: " + agentName);
        request.setKeyType(KeyType.AGENT_KEY);
        request.setProvider(agentType);
        request.setRawValue(rawKey);
        request.getAllowedUsers().add(agentId);
        
        ApiKeyDTO createdKey = keyService.createKey(request);
        
        AgentSecurityContext context = new AgentSecurityContext();
        context.setAgentId(agentId);
        context.setAgentName(agentName);
        context.setAgentType(agentType);
        context.setKeyId(createdKey.getKeyId());
        context.setRegisteredAt(System.currentTimeMillis());
        agentContexts.put(agentId, context);
        
        auditService.log(AuditLogDTO.success(AuditEventType.AGENT_AUTH, agentId, createdKey.getKeyId()));
        
        return createdKey;
    }
    
    public boolean authenticateAgent(String agentId, String token) {
        AgentSecurityContext context = agentContexts.get(agentId);
        
        if (context == null) {
            log.warn("Agent not registered: {}", agentId);
            auditService.log(AuditLogDTO.failure(AuditEventType.AGENT_AUTH, agentId, "unknown"));
            return false;
        }
        
        ApiKeyDTO key = keyService.getKey(context.getKeyId());
        
        if (key == null || !key.isActive()) {
            log.warn("Agent key not active: {}", agentId);
            auditService.log(AuditLogDTO.failure(AuditEventType.AGENT_AUTH, agentId, context.getKeyId()));
            return false;
        }
        
        String rawKey = keyService.useKey(context.getKeyId(), agentId, null);
        
        if (rawKey == null) {
            log.warn("Agent key use failed: {}", agentId);
            auditService.log(AuditLogDTO.denied(AuditEventType.AGENT_AUTH, agentId, context.getKeyId()));
            return false;
        }
        
        context.setLastAuthenticated(System.currentTimeMillis());
        
        auditService.log(AuditLogDTO.success(AuditEventType.AGENT_AUTH, agentId, context.getKeyId()));
        return true;
    }
    
    public void unregisterAgent(String agentId) {
        AgentSecurityContext context = agentContexts.remove(agentId);
        
        if (context != null) {
            keyService.deleteKey(context.getKeyId());
            auditService.log(AuditLogDTO.success(AuditEventType.AGENT_AUTH, agentId, context.getKeyId()));
            log.info("Unregistered agent: {}", agentId);
        }
    }
    
    public boolean checkAgentPermission(String agentId, String resourceType, String resourceId, String action) {
        AgentSecurityContext context = agentContexts.get(agentId);
        
        if (context == null) {
            log.warn("Agent not registered: {}", agentId);
            return false;
        }
        
        boolean hasPermission = true;
        
        AuditResult result = hasPermission ? AuditResult.SUCCESS : AuditResult.DENIED;
        auditService.log(AuditLogDTO.of(AuditEventType.AGENT_ACCESS, agentId, resourceId, result));
        
        return hasPermission;
    }
    
    public void logAgentCommunication(String fromAgentId, String toAgentId, String capability, AuditResult result) {
        AuditLogDTO logEntry = new AuditLogDTO();
        logEntry.setEventType(AuditEventType.AGENT_COMM);
        logEntry.setUserId(fromAgentId);
        logEntry.setAgentId(toAgentId);
        logEntry.setResourceType("AGENT_COMM");
        logEntry.setResourceId(capability);
        logEntry.setResult(result);
        logEntry.setTimestamp(System.currentTimeMillis());
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("fromAgent", fromAgentId);
        metadata.put("toAgent", toAgentId);
        metadata.put("capability", capability);
        logEntry.setMetadata(metadata);
        
        auditService.log(logEntry);
    }
    
    public AgentSecurityReport getAgentSecurityReport(String agentId) {
        AgentSecurityReport report = new AgentSecurityReport();
        report.setAgentId(agentId);
        
        AgentSecurityContext context = agentContexts.get(agentId);
        if (context != null) {
            report.setRegistered(true);
            report.setKeyId(context.getKeyId());
            report.setAgentType(context.getAgentType());
            report.setRegisteredAt(context.getRegisteredAt());
            report.setLastAuthenticated(context.getLastAuthenticated());
            
            ApiKeyDTO key = keyService.getKey(context.getKeyId());
            if (key != null) {
                report.setKeyStatus(key.getStatus());
                report.setUseCount(key.getUseCount());
            }
        } else {
            report.setRegistered(false);
        }
        
        return report;
    }
    
    public List<String> getActiveAgents() {
        return new ArrayList<>(agentContexts.keySet());
    }
    
    public static class AgentSecurityContext {
        private String agentId;
        private String agentName;
        private String agentType;
        private String keyId;
        private long registeredAt;
        private long lastAuthenticated;
        
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getAgentName() { return agentName; }
        public void setAgentName(String agentName) { this.agentName = agentName; }
        public String getAgentType() { return agentType; }
        public void setAgentType(String agentType) { this.agentType = agentType; }
        public String getKeyId() { return keyId; }
        public void setKeyId(String keyId) { this.keyId = keyId; }
        public long getRegisteredAt() { return registeredAt; }
        public void setRegisteredAt(long registeredAt) { this.registeredAt = registeredAt; }
        public long getLastAuthenticated() { return lastAuthenticated; }
        public void setLastAuthenticated(long lastAuthenticated) { this.lastAuthenticated = lastAuthenticated; }
    }
    
    public static class AgentSecurityReport {
        private String agentId;
        private boolean registered;
        private String keyId;
        private String keyStatus;
        private String agentType;
        private long registeredAt;
        private long lastAuthenticated;
        private int useCount;
        
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public boolean isRegistered() { return registered; }
        public void setRegistered(boolean registered) { this.registered = registered; }
        public String getKeyId() { return keyId; }
        public void setKeyId(String keyId) { this.keyId = keyId; }
        public String getKeyStatus() { return keyStatus; }
        public void setKeyStatus(String keyStatus) { this.keyStatus = keyStatus; }
        public String getAgentType() { return agentType; }
        public void setAgentType(String agentType) { this.agentType = agentType; }
        public long getRegisteredAt() { return registeredAt; }
        public void setRegisteredAt(long registeredAt) { this.registeredAt = registeredAt; }
        public long getLastAuthenticated() { return lastAuthenticated; }
        public void setLastAuthenticated(long lastAuthenticated) { this.lastAuthenticated = lastAuthenticated; }
        public int getUseCount() { return useCount; }
        public void setUseCount(int useCount) { this.useCount = useCount; }
    }
}
