package net.ooder.mvp.skill.scene.agent.service;

import net.ooder.mvp.skill.scene.agent.dto.FailoverRecordDTO;

import java.util.List;
import java.util.Map;

public interface FailoverService {
    
    void detectFailure(String agentId);
    
    void reassignTasks(String failedAgentId, String targetAgentId);
    
    void reassignTasksAuto(String failedAgentId);
    
    void notifyRecovery(String agentId);
    
    List<FailoverRecordDTO> getFailoverHistory(String sceneGroupId);
    
    FailoverRecordDTO getFailoverRecord(String recordId);
    
    Map<String, Object> getFailoverStats();
    
    List<String> getAvailableAgentsForFailover(String sceneGroupId);
}
