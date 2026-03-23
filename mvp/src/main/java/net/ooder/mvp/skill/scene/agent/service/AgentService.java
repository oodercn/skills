package net.ooder.mvp.skill.scene.agent.service;

import net.ooder.mvp.skill.scene.agent.dto.AgentAlertConfigDTO;
import net.ooder.mvp.skill.scene.agent.dto.AgentBatchOperationDTO;
import net.ooder.mvp.skill.scene.agent.dto.AgentDTO;
import net.ooder.mvp.skill.scene.agent.dto.AgentMetricsDTO;
import net.ooder.mvp.skill.scene.agent.dto.AgentTopologyDTO;
import net.ooder.mvp.skill.scene.dto.PageResult;

import java.util.List;
import java.util.Map;

public interface AgentService {
    
    PageResult<AgentDTO> listAgents(int pageNum, int pageSize);
    
    AgentDTO getAgent(String agentId);
    
    List<AgentDTO> searchAgents(String keyword);
    
    List<AgentDTO> listByType(String agentType);
    
    List<AgentDTO> listByStatus(String status);
    
    List<AgentDTO> listByCluster(String clusterId);
    
    boolean sendHeartbeat(String agentId);
    
    boolean updateAgentStatus(String agentId, String status);
    
    int getBindingCount(String agentId);
    
    AgentDTO registerAgent(AgentDTO agent);
    
    boolean unregisterAgent(String agentId);
    
    boolean updateAgentConfig(String agentId, Map<String, Object> config);
    
    AgentTopologyDTO getTopology();
    
    AgentTopologyDTO getTopologyByCluster(String clusterId);
    
    AgentMetricsDTO getAgentMetrics(String agentId);
    
    List<AgentMetricsDTO> getAllMetrics();
    
    Map<String, Object> getClusterStats(String clusterId);
    
    Map<String, Object> getOverallStats();
    
    AgentBatchOperationDTO executeBatchOperation(AgentBatchOperationDTO operation);
    
    AgentBatchOperationDTO getBatchOperationStatus(String operationId);
    
    List<AgentAlertConfigDTO> listAlertConfigs();
    
    List<AgentAlertConfigDTO> getAlertConfigsByAgent(String agentId);
    
    AgentAlertConfigDTO createAlertConfig(AgentAlertConfigDTO config);
    
    AgentAlertConfigDTO updateAlertConfig(Long id, AgentAlertConfigDTO config);
    
    boolean deleteAlertConfig(Long id);
    
    boolean enableAlertConfig(Long id);
    
    boolean disableAlertConfig(Long id);
    
    List<AgentDTO> listByCapability(String capability);
    
    List<AgentDTO> listByTag(String tagKey, String tagValue);
    
    Map<String, Object> healthCheck(String agentId);
    
    void updateAgentMetrics(String agentId, Map<String, Object> metrics);
}
