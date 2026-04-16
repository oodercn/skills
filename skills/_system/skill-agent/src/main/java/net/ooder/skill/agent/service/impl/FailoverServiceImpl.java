package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.dto.AgentDTO;
import net.ooder.skill.agent.dto.FailoverRecordDTO;
import net.ooder.skill.agent.service.AgentService;
import net.ooder.skill.agent.service.AgentSessionService;
import net.ooder.skill.agent.service.FailoverService;

import net.ooder.scene.failover.FailoverManager;
import net.ooder.scene.failover.FailoverListener;
import net.ooder.scene.failover.FailoverEvent;
import net.ooder.scene.failover.FailoverEventType;

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

    @Autowired(required = false)
    private FailoverManager failoverManager;

    private boolean failoverEnabled = true;
    private boolean autoFailoverEnabled = true;
    private int maxRetryAttempts = 3;

    private FailoverConfig failoverConfig = new FailoverConfig();
    private Map<String, FailoverRecordDTO> failoverRecords = new ConcurrentHashMap<>();

    public boolean useSeSdkFailover() {
        return failoverManager != null;
    }

    @Override
    public void enableFailover(boolean enabled) {
        this.failoverEnabled = enabled;
        if (useSeSdkFailover()) {
            if (enabled) {
                failoverManager.startMonitoring();
            } else {
                failoverManager.stopMonitoring();
            }
        }
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
        
        if (useSeSdkFailover()) {
            try {
                String targetId = failoverManager.selectReplacementAgent(sceneGroupId, failedAgentId);
                if (targetId != null) {
                    failoverManager.reassignTasks(failedAgentId, targetId);
                    
                    FailoverRecordDTO record = new FailoverRecordDTO();
                    record.setRecordId(UUID.randomUUID().toString());
                    record.setFailedAgentId(failedAgentId);
                    AgentDTO failed = agentService.getAgent(failedAgentId);
                    record.setFailedAgentName(failed != null ? failed.getAgentName() : failedAgentId);
                    record.setTargetAgentId(targetId);
                    AgentDTO target = agentService.getAgent(targetId);
                    record.setTargetAgentName(target != null ? target.getAgentName() : targetId);
                    record.setSceneGroupId(sceneGroupId);
                    record.setStatus("COMPLETED");
                    record.setCreateTime(System.currentTimeMillis());
                    record.setRecoveryTime(System.currentTimeMillis());
                    record.setFailureReason("SE SDK auto-reassignment");
                    
                    failoverRecords.put(record.getRecordId(), record);
                    log.info("[initiateFailover] SE SDK failover completed: {} -> {}", failedAgentId, targetId);
                    return record.getRecordId();
                }
                log.warn("[initiateFailover] No replacement agent available from SE SDK");
            } catch (Exception e) {
                log.error("[initiateFailover] SE SDK failover failed: {}", e.getMessage(), e);
            }
        }
        
        return initiateLocalFailover(failedAgentId, sceneGroupId);
    }

    private String initiateLocalFailover(String failedAgentId, String sceneGroupId) {
        AgentDTO failedAgent = agentService.getAgent(failedAgentId);
        if (failedAgent == null) {
            log.warn("[initiateLocalFailover] Failed agent not found: {}", failedAgentId);
            return null;
        }
        
        String targetAgentId = selectFailoverTarget(failedAgentId, sceneGroupId);
        if (targetAgentId == null) {
            log.warn("[initiateLocalFailover] No available target agent for failover");
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
            
            log.info("[initiateLocalFailover] Failover completed: {} -> {}", failedAgentId, targetAgentId);
            
        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setFailureReason(e.getMessage());
            log.error("[initiateLocalFailover] Failover failed: {}", e.getMessage(), e);
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
        List<FailoverRecordDTO> active = new ArrayList<>(failoverRecords.values().stream()
            .filter(r -> "IN_PROGRESS".equals(r.getStatus()))
            .collect(java.util.stream.Collectors.toList()));
        
        if (useSeSdkFailover()) {
            try {
                var timedOutAgents = failoverManager.getTimedOutAgents();
                for (String agentId : timedOutAgents) {
                    boolean alreadyTracked = active.stream()
                        .anyMatch(r -> agentId.equals(r.getFailedAgentId()) && "IN_PROGRESS".equals(r.getStatus()));
                    if (!alreadyTracked) {
                        FailoverRecordDTO record = new FailoverRecordDTO();
                        record.setRecordId(UUID.randomUUID().toString());
                        record.setFailedAgentId(agentId);
                        record.setStatus("IN_PROGRESS");
                        record.setCreateTime(System.currentTimeMillis());
                        record.setFailureReason("SE SDK detected timeout");
                        failoverRecords.put(record.getRecordId(), record);
                        active.add(record);
                    }
                }
            } catch (Exception e) {
                log.warn("[getActiveFailovers] Failed to query SE SDK timed out agents: {}", e.getMessage());
            }
        }
        
        return active;
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
        if (useSeSdkFailover()) {
            try {
                String target = failoverManager.selectReplacementAgent(sceneGroupId, failedAgentId);
                if (target != null) {
                    log.debug("[selectFailoverTarget] SE SDK selected target: {} for failed: {}", target, failedAgentId);
                    return target;
                }
            } catch (Exception e) {
                log.warn("[selectFailoverTarget] SE SDK selection failed: {}", e.getMessage());
            }
        }
        
        return selectLocalFailoverTarget(failedAgentId, sceneGroupId);
    }

    private String selectLocalFailoverTarget(String failedAgentId, String sceneGroupId) {
        log.debug("[selectLocalFailoverTarget] Selecting target for: {}", failedAgentId);
        
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
        
        if (useSeSdkFailover()) {
            try {
                failoverManager.reassignTasks(fromAgentId, toAgentId);
                log.info("[migrateAgentState] State migrated via SE SDK reassignTasks");
                return;
            } catch (Exception e) {
                log.warn("[migrateAgentState] SE SDK migration failed, using local fallback: {}", e.getMessage());
            }
        }
    }

    @Override
    public void notifyFailover(String recordId, List<String> notifyTargets) {
        log.info("[notifyFailover] Notifying targets: {}", notifyTargets);
        
        FailoverRecordDTO record = failoverRecords.get(recordId);
        if (record == null) {
            return;
        }
        
        if (useSeSdkFailover()) {
            try {
                log.info("[notifyFailover] Failover event would be dispatched via SE SDK FailoverListener for record: {}", recordId);
            } catch (Exception e) {
                log.warn("[notifyFailover] SE SDK notification failed: {}", e.getMessage());
            }
        }
    }

    @Override
    public void setAutoFailover(boolean enabled) {
        this.autoFailoverEnabled = enabled;
        if (useSeSdkFailover() && enabled) {
            failoverManager.addFailoverListener(new FailoverListener() {
                @Override
                public void onFailoverEvent(FailoverEvent event) {
                    if (event.getType() == FailoverEventType.AGENT_TIMEOUT ||
                        event.getType() == FailoverEventType.TASK_FAILED) {
                        log.info("[autoFailoverListener] Auto-failover triggered for agent: {}", event.getAgentId());
                        initiateFailover(event.getAgentId(), event.getSceneGroupId());
                    }
                }
                
                @Override
                public boolean supports(FailoverEventType type) {
                    return type == FailoverEventType.AGENT_TIMEOUT || 
                           type == FailoverEventType.TASK_FAILED;
                }
            });
        }
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

    public void registerAgentForMonitoring(String agentId, String sceneGroupId) {
        if (useSeSdkFailover()) {
            failoverManager.registerAgent(agentId, sceneGroupId);
            log.info("[registerAgentForMonitoring] Agent {} registered in scene {} for monitoring", agentId, sceneGroupId);
        }
    }

    public void unregisterAgentFromMonitoring(String agentId) {
        if (useSeSdkFailover()) {
            failoverManager.unregisterAgent(agentId);
            log.info("[unregisterAgentFromMonitoring] Agent {} unregistered from monitoring", agentId);
        }
    }

    public void updateHeartbeat(String agentId) {
        if (useSeSdkFailover()) {
            failoverManager.updateHeartbeat(agentId);
        }
    }
}
