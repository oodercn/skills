package net.ooder.skill.agent.service;

import net.ooder.skill.agent.dto.AgentAlertConfigDTO;
import net.ooder.skill.agent.dto.AgentBatchOperationDTO;
import net.ooder.skill.agent.dto.AgentDTO;
import net.ooder.skill.agent.dto.AgentMetricsDTO;
import net.ooder.skill.agent.dto.AgentStatsDTO;
import net.ooder.skill.agent.dto.AgentTopologyDTO;
import net.ooder.skill.agent.dto.PageResult;

import java.util.List;
import java.util.Map;

public interface AgentService {
    
    AgentDTO registerAgent(AgentDTO agent);
    
    boolean unregisterAgent(String agentId);
    
    AgentDTO getAgent(String agentId);
    
    PageResult<AgentDTO> listAgents(int pageNum, int pageSize);
    
    List<AgentDTO> searchAgents(String keyword);
    
    List<AgentDTO> listByType(String agentType);
    
    List<AgentDTO> listByStatus(String status);
    
    List<AgentDTO> listByCluster(String clusterId);
    
    List<AgentDTO> listByCapability(String capability);
    
    List<AgentDTO> listByTag(String tagKey, String tagValue);
    
    boolean sendHeartbeat(String agentId);
    
    boolean updateAgentStatus(String agentId, String status);
    
    int getBindingCount(String agentId);
    
    boolean updateAgentConfig(String agentId, Map<String, Object> config);
    
    AgentStatsDTO getOverallStats();
    
    Map<String, Object> getClusterStats(String clusterId);
    
    AgentTopologyDTO getTopology();
    
    AgentTopologyDTO getTopologyByCluster(String clusterId);
    
    AgentMetricsDTO getAgentMetrics(String agentId);
    
    List<AgentMetricsDTO> getAllMetrics();
    
    void updateAgentMetrics(String agentId, Map<String, Object> metrics);
    
    Map<String, Object> healthCheck(String agentId);
    
    AgentBatchOperationDTO executeBatchOperation(AgentBatchOperationDTO operation);
    
    AgentBatchOperationDTO getBatchOperationStatus(String operationId);
    
    List<AgentAlertConfigDTO> listAlertConfigs();
    
    List<AgentAlertConfigDTO> getAlertConfigsByAgent(String agentId);
    
    AgentAlertConfigDTO createAlertConfig(AgentAlertConfigDTO config);
    
    AgentAlertConfigDTO updateAlertConfig(Long id, AgentAlertConfigDTO config);
    
    boolean deleteAlertConfig(Long id);
    
    boolean enableAlertConfig(Long id);
    
    boolean disableAlertConfig(Long id);
}
