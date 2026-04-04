package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.dto.AgentDTO;
import net.ooder.skill.agent.dto.FailoverRecordDTO;
import net.ooder.skill.agent.service.AgentService;
import net.ooder.skill.agent.service.AgentSessionService;
import net.ooder.skill.agent.service.FailoverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FailoverServiceImpl implements FailoverService {

    private static final Logger log = LoggerFactory.getLogger(FailoverServiceImpl.class);

    @Autowired
    private AgentService agentService;

    @Autowired
    private AgentSessionService sessionService;

    private boolean failoverEnabled = true;
    private boolean autoFailoverEnabled = true;
    private int maxRetryAttempts = 3;
    
    private FailoverConfig failoverConfig = new FailoverConfig();
    private Map<String, FailoverRecordDTO> failoverRecords = new ConcurrentHashMap<>();

    @Override
    public void enableFailover(boolean enabled) {
        this.failoverEnabled = enabled;
        log.info("[enableFailover] Failover enabled: {}", enabled);
    }

    @Override
    public boolean isFailoverEnabled() {
        return failoverEnabled;
    }

    @Override
    public void configureFailover(FailoverConfig config) {
        this.failoverConfig = config;
        log.info("[configureFailover] Failover configured");
    }

    @Override
    public FailoverConfig getFailoverConfig() {
        return failoverConfig;
    }

    @Override
    public String initiateFailover(String failedAgentId, String sceneGroupId) {
        log.info("[initiateFailover] Initiating failover for agent: {}", failedAgentId);
        
        if (!failoverEnabled) {
            log.warn("[initiateFailover] Failover is disabled");
            return null;
        }
        
        AgentDTO failedAgent = agentService.getAgent(failedAgentId);
        if (failedAgent == null) {
            log.warn("[initiateFailover] Failed agent not found: {}", failedAgentId);
            return null;
        }
        
        String targetAgentId = selectFailoverTarget(failedAgentId, sceneGroupId);
        if (targetAgentId == null) {
            log.warn("[initiateFailover] No available target agent for failover");
            return null;
        }
        
        AgentDTO targetAgent = agentService.getAgent(targetAgentId);
        
        FailoverRecordDTO record = new FailoverRecordDTO();
        record.setRecordId(UUID.randomUUID().toString());
        record.setFailedAgentId(failedAgentId);
        record.setFailedAgentName(failedAgent.getAgentName());
        record.setTargetAgentId(targetAgentId);
        record.setTargetAgentName(targetAgent.getAgentName());
        record.setSceneGroupId(sceneGroupId);
        record.setStatus("IN_PROGRESS");
        record.setCreateTime(System.currentTimeMillis());
        record.setFailureReason("Agent offline or unresponsive");
        
        failoverRecords.put(record.getRecordId(), record);
        
        try {
            migrateAgentState(failedAgentId, targetAgentId, sceneGroupId);
            
            record.setStatus("COMPLETED");
            record.setRecoveryTime(System.currentTimeMillis());
            
            log.info("[initiateFailover] Failover completed: {} -> {}", failedAgentId, targetAgentId);
            
        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setFailureReason(e.getMessage());
            log.error("[initiateFailover] Failover failed: {}", e.getMessage(), e);
        }
        
        return record.getRecordId();
    }

    @Override
    public FailoverRecordDTO getFailoverRecord(String recordId) {
        return failoverRecords.get(recordId);
    }

    @Override
    public List<FailoverRecordDTO> getFailoverHistory(String agentId, long startTime, long endTime) {
        return failoverRecords.values().stream()
            .filter(r -> agentId.equals(r.getFailedAgentId()) || agentId.equals(r.getTargetAgentId()))
            .filter(r -> r.getCreateTime() >= startTime && r.getCreateTime() <= endTime)
            .sorted((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()))
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<FailoverRecordDTO> getActiveFailovers() {
        return failoverRecords.values().stream()
            .filter(r -> "IN_PROGRESS".equals(r.getStatus()))
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void completeFailover(String recordId) {
        FailoverRecordDTO record = failoverRecords.get(recordId);
        if (record != null && "IN_PROGRESS".equals(record.getStatus())) {
            record.setStatus("COMPLETED");
            record.setRecoveryTime(System.currentTimeMillis());
            log.info("[completeFailover] Failover completed: {}", recordId);
        }
    }

    @Override
    public void cancelFailover(String recordId) {
        FailoverRecordDTO record = failoverRecords.get(recordId);
        if (record != null && "IN_PROGRESS".equals(record.getStatus())) {
            record.setStatus("CANCELLED");
            log.info("[cancelFailover] Failover cancelled: {}", recordId);
        }
    }

    @Override
    public String selectFailoverTarget(String failedAgentId, String sceneGroupId) {
        log.debug("[selectFailoverTarget] Selecting target for: {}", failedAgentId);
        
        AgentDTO failedAgent = agentService.getAgent(failedAgentId);
        if (failedAgent == null) {
            return null;
        }
        
        List<AgentDTO> candidates = agentService.listByType(failedAgent.getAgentType());
        
        candidates = candidates.stream()
            .filter(a -> !a.getAgentId().equals(failedAgentId))
            .filter(a -> "ONLINE".equals(a.getStatus()))
            .filter(a -> a.isEnabled())
            .filter(a -> failoverConfig.getExcludedAgents() == null || 
                        !failoverConfig.getExcludedAgents().contains(a.getAgentId()))
            .collect(java.util.stream.Collectors.toList());
        
        if (candidates.isEmpty()) {
            return null;
        }
        
        candidates.sort((a, b) -> {
            double loadA = a.getLoadPercentage() != null ? a.getLoadPercentage() : 0;
            double loadB = b.getLoadPercentage() != null ? b.getLoadPercentage() : 0;
            return Double.compare(loadA, loadB);
        });
        
        return candidates.get(0).getAgentId();
    }

    @Override
    public void migrateAgentState(String fromAgentId, String toAgentId, String sceneGroupId) {
        log.info("[migrateAgentState] Migrating state: {} -> {}", fromAgentId, toAgentId);
        
        // This is a placeholder implementation
        // In a real implementation, this would migrate conversation state,
        // pending tasks, and other agent-specific data
        
    }

    @Override
    public void notifyFailover(String recordId, List<String> notifyTargets) {
        log.info("[notifyFailover] Notifying targets: {}", notifyTargets);
        
        FailoverRecordDTO record = failoverRecords.get(recordId);
        if (record == null) {
            return;
        }
        
        // This is a placeholder implementation
        // In a real implementation, this would send notifications via
        // configured channels (email, SMS, webhook, etc.)
        
    }

    @Override
    public void setAutoFailover(boolean enabled) {
        this.autoFailoverEnabled = enabled;
        log.info("[setAutoFailover] Auto failover enabled: {}", enabled);
    }

    @Override
    public boolean isAutoFailoverEnabled() {
        return autoFailoverEnabled;
    }

    @Override
    public void setMaxRetryAttempts(int attempts) {
        this.maxRetryAttempts = attempts;
        log.info("[setMaxRetryAttempts] Max retry attempts set to: {}", attempts);
    }

    @Override
    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }
}
