package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.dto.AgentAlertConfigDTO;
import net.ooder.skill.agent.dto.AgentBatchOperationDTO;
import net.ooder.skill.agent.dto.AgentDTO;
import net.ooder.skill.agent.dto.AgentMetricsDTO;
import net.ooder.skill.agent.dto.AgentStatsDTO;
import net.ooder.skill.agent.dto.AgentTopologyDTO;
import net.ooder.skill.agent.service.AgentService;
import net.ooder.skill.agent.dto.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AgentServiceImpl implements AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentServiceImpl.class);

    private Map<String, AgentDTO> agentStore = new ConcurrentHashMap<>();
    private Map<String, AgentMetricsDTO> metricsStore = new ConcurrentHashMap<>();
    private Map<Long, AgentAlertConfigDTO> alertConfigStore = new ConcurrentHashMap<>();
    private Map<String, AgentBatchOperationDTO> batchOperationStore = new ConcurrentHashMap<>();
    private Long alertConfigIdCounter = 1L;

    @Override
    public AgentDTO registerAgent(AgentDTO agent) {
        log.info("[registerAgent] Registering agent: {}", agent.getAgentId());
        
        agent.setRegisterTime(System.currentTimeMillis());
        agent.setLastHeartbeat(System.currentTimeMillis());
        agent.setEnabled(true);
        agent.setHealthStatus("healthy");
        
        if (agent.getStatus() == null) {
            agent.setStatus("ONLINE");
        }
        if (agent.getBindingCount() == null) {
            agent.setBindingCount(0);
        }
        if (agent.getCurrentLoad() == null) {
            agent.setCurrentLoad(0);
        }
        if (agent.getTotalRequests() == null) {
            agent.setTotalRequests(0L);
        }
        if (agent.getSuccessRequests() == null) {
            agent.setSuccessRequests(0L);
        }
        if (agent.getFailedRequests() == null) {
            agent.setFailedRequests(0L);
        }
        
        agentStore.put(agent.getAgentId(), agent);
        
        AgentMetricsDTO metrics = new AgentMetricsDTO();
        metrics.setAgentId(agent.getAgentId());
        metrics.setAgentName(agent.getAgentName());
        metrics.setTimestamp(System.currentTimeMillis());
        metrics.setHealthStatus("healthy");
        metricsStore.put(agent.getAgentId(), metrics);
        
        return agent;
    }

    @Override
    public boolean unregisterAgent(String agentId) {
        log.info("[unregisterAgent] Unregistering agent: {}", agentId);
        
        AgentDTO removed = agentStore.remove(agentId);
        metricsStore.remove(agentId);
        
        return removed != null;
    }

    @Override
    public AgentDTO getAgent(String agentId) {
        return agentStore.get(agentId);
    }

    @Override
    public PageResult<AgentDTO> listAgents(int pageNum, int pageSize) {
        List<AgentDTO> allAgents = new ArrayList<>(agentStore.values());
        
        int total = allAgents.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<AgentDTO> pageData = start < total ? allAgents.subList(start, end) : new ArrayList<>();
        
        PageResult<AgentDTO> result = new PageResult<>();
        result.setList(pageData);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        return result;
    }

    @Override
    public List<AgentDTO> searchAgents(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return agentStore.values().stream()
            .filter(a -> a.getAgentId().toLowerCase().contains(lowerKeyword) ||
                        (a.getAgentName() != null && a.getAgentName().toLowerCase().contains(lowerKeyword)) ||
                        (a.getDescription() != null && a.getDescription().toLowerCase().contains(lowerKeyword)))
            .collect(Collectors.toList());
    }

    @Override
    public List<AgentDTO> listByType(String agentType) {
        return agentStore.values().stream()
            .filter(a -> agentType.equals(a.getAgentType()))
            .collect(Collectors.toList());
    }

    @Override
    public List<AgentDTO> listByStatus(String status) {
        return agentStore.values().stream()
            .filter(a -> status.equals(a.getStatus()))
            .collect(Collectors.toList());
    }

    @Override
    public List<AgentDTO> listByCluster(String clusterId) {
        return agentStore.values().stream()
            .filter(a -> clusterId.equals(a.getClusterId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<AgentDTO> listByCapability(String capability) {
        return agentStore.values().stream()
            .filter(a -> a.getCapabilities() != null && a.getCapabilities().contains(capability))
            .collect(Collectors.toList());
    }

    @Override
    public List<AgentDTO> listByTag(String tagKey, String tagValue) {
        return agentStore.values().stream()
            .filter(a -> a.getTags() != null && tagValue.equals(a.getTags().get(tagKey)))
            .collect(Collectors.toList());
    }

    @Override
    public boolean sendHeartbeat(String agentId) {
        AgentDTO agent = agentStore.get(agentId);
        if (agent != null) {
            agent.setLastHeartbeat(System.currentTimeMillis());
            agent.setStatus("ONLINE");
            return true;
        }
        return false;
    }

    @Override
    public boolean updateAgentStatus(String agentId, String status) {
        AgentDTO agent = agentStore.get(agentId);
        if (agent != null) {
            agent.setStatus(status);
            return true;
        }
        return false;
    }

    @Override
    public int getBindingCount(String agentId) {
        AgentDTO agent = agentStore.get(agentId);
        return agent != null ? agent.getBindingCount() : 0;
    }

    @Override
    public boolean updateAgentConfig(String agentId, Map<String, Object> config) {
        AgentDTO agent = agentStore.get(agentId);
        if (agent != null) {
            if (agent.getExtendedConfig() == null) {
                agent.setExtendedConfig(new HashMap<>());
            }
            agent.getExtendedConfig().putAll(config);
            return true;
        }
        return false;
    }

    @Override
    public AgentStatsDTO getOverallStats() {
        AgentStatsDTO stats = new AgentStatsDTO();
        
        int total = agentStore.size();
        long online = agentStore.values().stream().filter(a -> "ONLINE".equals(a.getStatus())).count();
        long offline = agentStore.values().stream().filter(a -> "OFFLINE".equals(a.getStatus())).count();
        
        stats.setTotalAgents(total);
        stats.setActiveAgents((int) online);
        stats.setInactiveAgents((int) offline);
        stats.setTotalConversations(0);
        stats.setTotalMessages(0L);
        
        return stats;
    }

    @Override
    public Map<String, Object> getClusterStats(String clusterId) {
        List<AgentDTO> clusterAgents = listByCluster(clusterId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("clusterId", clusterId);
        stats.put("total", clusterAgents.size());
        stats.put("online", clusterAgents.stream().filter(a -> "ONLINE".equals(a.getStatus())).count());
        stats.put("offline", clusterAgents.stream().filter(a -> "OFFLINE".equals(a.getStatus())).count());
        
        return stats;
    }

    @Override
    public AgentTopologyDTO getTopology() {
        AgentTopologyDTO topology = new AgentTopologyDTO();
        
        List<AgentTopologyDTO.AgentNode> nodes = agentStore.values().stream()
            .map(this::convertToNode)
            .collect(Collectors.toList());
        
        topology.setNodes(nodes);
        topology.setEdges(new ArrayList<>());
        topology.setTimestamp(System.currentTimeMillis());
        
        return topology;
    }

    private AgentTopologyDTO.AgentNode convertToNode(AgentDTO agent) {
        AgentTopologyDTO.AgentNode node = new AgentTopologyDTO.AgentNode();
        node.setId(agent.getAgentId());
        node.setName(agent.getAgentName());
        node.setType(agent.getAgentType());
        node.setStatus(agent.getStatus());
        node.setClusterId(agent.getClusterId());
        return node;
    }

    @Override
    public AgentTopologyDTO getTopologyByCluster(String clusterId) {
        AgentTopologyDTO topology = new AgentTopologyDTO();
        
        List<AgentTopologyDTO.AgentNode> nodes = listByCluster(clusterId).stream()
            .map(this::convertToNode)
            .collect(Collectors.toList());
        
        topology.setNodes(nodes);
        topology.setEdges(new ArrayList<>());
        topology.setTimestamp(System.currentTimeMillis());
        
        return topology;
    }

    @Override
    public AgentMetricsDTO getAgentMetrics(String agentId) {
        return metricsStore.get(agentId);
    }

    @Override
    public List<AgentMetricsDTO> getAllMetrics() {
        return new ArrayList<>(metricsStore.values());
    }

    @Override
    public void updateAgentMetrics(String agentId, Map<String, Object> metrics) {
        AgentMetricsDTO agentMetrics = metricsStore.computeIfAbsent(agentId, id -> {
            AgentMetricsDTO m = new AgentMetricsDTO();
            m.setAgentId(id);
            return m;
        });
        
        agentMetrics.setTimestamp(System.currentTimeMillis());
        
        if (metrics.containsKey("cpuUsage")) {
            agentMetrics.setCpuUsage(((Number) metrics.get("cpuUsage")).doubleValue());
        }
        if (metrics.containsKey("memoryUsage")) {
            agentMetrics.setMemoryUsage(((Number) metrics.get("memoryUsage")).doubleValue());
        }
        if (metrics.containsKey("currentLoad")) {
            agentMetrics.setCurrentLoad(((Number) metrics.get("currentLoad")).intValue());
        }
    }

    @Override
    public Map<String, Object> healthCheck(String agentId) {
        Map<String, Object> result = new HashMap<>();
        
        AgentDTO agent = agentStore.get(agentId);
        if (agent == null) {
            result.put("status", "not_found");
            return result;
        }
        
        result.put("agentId", agentId);
        result.put("status", agent.getStatus());
        result.put("healthStatus", agent.getHealthStatus());
        result.put("lastHeartbeat", agent.getLastHeartbeat());
        result.put("heartbeatAge", System.currentTimeMillis() - agent.getLastHeartbeat());
        
        return result;
    }

    @Override
    public AgentBatchOperationDTO executeBatchOperation(AgentBatchOperationDTO operation) {
        log.info("[executeBatchOperation] Type: {}, Agents: {}", 
            operation.getOperationType(), operation.getAgentIds().size());
        
        String operationId = UUID.randomUUID().toString();
        operation.setOperationId(operationId);
        operation.setCreatedAt(System.currentTimeMillis());
        operation.setStatus("PROCESSING");
        operation.setTotalCount(operation.getAgentIds().size());
        operation.setSuccessCount(0);
        operation.setFailedCount(0);
        operation.setResults(new ArrayList<>());
        
        batchOperationStore.put(operationId, operation);
        
        for (String agentId : operation.getAgentIds()) {
            AgentBatchOperationDTO.OperationResult opResult = new AgentBatchOperationDTO.OperationResult();
            opResult.setAgentId(agentId);
            
            try {
                boolean success = executeOperationOnAgent(agentId, operation.getOperationType(), 
                    operation.getParameters());
                opResult.setSuccess(success);
                opResult.setMessage(success ? "Operation completed" : "Operation failed");
                
                if (success) {
                    operation.setSuccessCount(operation.getSuccessCount() + 1);
                } else {
                    operation.setFailedCount(operation.getFailedCount() + 1);
                }
            } catch (Exception e) {
                opResult.setSuccess(false);
                opResult.setMessage(e.getMessage());
                operation.setFailedCount(operation.getFailedCount() + 1);
            }
            
            operation.getResults().add(opResult);
        }
        
        operation.setStatus("COMPLETED");
        
        return operation;
    }

    private boolean executeOperationOnAgent(String agentId, String operationType, 
            Map<String, Object> parameters) {
        AgentDTO agent = agentStore.get(agentId);
        if (agent == null) {
            return false;
        }
        
        switch (operationType) {
            case AgentBatchOperationDTO.OP_ENABLE:
                agent.setEnabled(true);
                return true;
            case AgentBatchOperationDTO.OP_DISABLE:
                agent.setEnabled(false);
                return true;
            case AgentBatchOperationDTO.OP_HEALTH_CHECK:
                return "healthy".equals(agent.getHealthStatus());
            default:
                return true;
        }
    }

    @Override
    public AgentBatchOperationDTO getBatchOperationStatus(String operationId) {
        return batchOperationStore.get(operationId);
    }

    @Override
    public List<AgentAlertConfigDTO> listAlertConfigs() {
        return new ArrayList<>(alertConfigStore.values());
    }

    @Override
    public List<AgentAlertConfigDTO> getAlertConfigsByAgent(String agentId) {
        return alertConfigStore.values().stream()
            .filter(c -> agentId.equals(c.getAgentId()))
            .collect(Collectors.toList());
    }

    @Override
    public AgentAlertConfigDTO createAlertConfig(AgentAlertConfigDTO config) {
        config.setId(alertConfigIdCounter++);
        config.setCreatedAt(System.currentTimeMillis());
        config.setUpdatedAt(System.currentTimeMillis());
        config.setEnabled(true);
        
        alertConfigStore.put(config.getId(), config);
        
        return config;
    }

    @Override
    public AgentAlertConfigDTO updateAlertConfig(Long id, AgentAlertConfigDTO config) {
        AgentAlertConfigDTO existing = alertConfigStore.get(id);
        if (existing == null) {
            return null;
        }
        
        config.setId(id);
        config.setCreatedAt(existing.getCreatedAt());
        config.setUpdatedAt(System.currentTimeMillis());
        
        alertConfigStore.put(id, config);
        
        return config;
    }

    @Override
    public boolean deleteAlertConfig(Long id) {
        return alertConfigStore.remove(id) != null;
    }

    @Override
    public boolean enableAlertConfig(Long id) {
        AgentAlertConfigDTO config = alertConfigStore.get(id);
        if (config != null) {
            config.setEnabled(true);
            config.setUpdatedAt(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    @Override
    public boolean disableAlertConfig(Long id) {
        AgentAlertConfigDTO config = alertConfigStore.get(id);
        if (config != null) {
            config.setEnabled(false);
            config.setUpdatedAt(System.currentTimeMillis());
            return true;
        }
        return false;
    }
}
