package net.ooder.mvp.skill.scene.agent.controller;

import net.ooder.mvp.skill.scene.agent.dto.AgentAlertConfigDTO;
import net.ooder.mvp.skill.scene.agent.dto.AgentBatchOperationDTO;
import net.ooder.mvp.skill.scene.agent.dto.AgentDTO;
import net.ooder.mvp.skill.scene.agent.dto.AgentMetricsDTO;
import net.ooder.mvp.skill.scene.agent.dto.AgentTopologyDTO;
import net.ooder.mvp.skill.scene.agent.service.AgentService;
import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.model.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("sceneAgentController")
@RequestMapping("/api/agent")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @GetMapping("/list")
    public ResultModel<List<AgentDTO>> listAll() {
        PageResult<AgentDTO> result = agentService.listAgents(1, 1000);
        return ResultModel.success(result.getList());
    }

    @GetMapping("/page")
    public ResultModel<PageResult<AgentDTO>> listPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResult<AgentDTO> result = agentService.listAgents(pageNum, pageSize);
        return ResultModel.success(result);
    }

    @GetMapping("/{agentId}")
    public ResultModel<AgentDTO> getAgent(@PathVariable String agentId) {
        AgentDTO agent = agentService.getAgent(agentId);
        if (agent != null) {
            return ResultModel.success(agent);
        }
        return ResultModel.error("Agent not found: " + agentId);
    }

    @GetMapping("/search")
    public ResultModel<List<AgentDTO>> searchAgents(@RequestParam String keyword) {
        List<AgentDTO> agents = agentService.searchAgents(keyword);
        return ResultModel.success(agents);
    }

    @GetMapping("/type/{agentType}")
    public ResultModel<List<AgentDTO>> listByType(@PathVariable String agentType) {
        List<AgentDTO> agents = agentService.listByType(agentType);
        return ResultModel.success(agents);
    }

    @GetMapping("/status/{status}")
    public ResultModel<List<AgentDTO>> listByStatus(@PathVariable String status) {
        List<AgentDTO> agents = agentService.listByStatus(status);
        return ResultModel.success(agents);
    }

    @GetMapping("/cluster/{clusterId}")
    public ResultModel<List<AgentDTO>> listByCluster(@PathVariable String clusterId) {
        List<AgentDTO> agents = agentService.listByCluster(clusterId);
        return ResultModel.success(agents);
    }

    @PostMapping("/{agentId}/heartbeat")
    public ResultModel<Boolean> sendHeartbeat(@PathVariable String agentId) {
        boolean success = agentService.sendHeartbeat(agentId);
        if (success) {
            return ResultModel.success(true);
        }
        return ResultModel.error("Failed to send heartbeat for agent: " + agentId);
    }

    @PutMapping("/{agentId}/status")
    public ResultModel<Boolean> updateStatus(
            @PathVariable String agentId,
            @RequestParam String status) {
        boolean success = agentService.updateAgentStatus(agentId, status);
        if (success) {
            return ResultModel.success(true);
        }
        return ResultModel.error("Failed to update status for agent: " + agentId);
    }

    @GetMapping("/{agentId}/binding-count")
    public ResultModel<Map<String, Object>> getBindingCount(@PathVariable String agentId) {
        int count = agentService.getBindingCount(agentId);
        Map<String, Object> result = new HashMap<>();
        result.put("agentId", agentId);
        result.put("bindingCount", count);
        return ResultModel.success(result);
    }

    @GetMapping("/stats")
    public ResultModel<Map<String, Object>> getStats() {
        Map<String, Object> stats = agentService.getOverallStats();
        return ResultModel.success(stats);
    }

    @GetMapping("/cluster/{clusterId}/stats")
    public ResultModel<Map<String, Object>> getClusterStats(@PathVariable String clusterId) {
        Map<String, Object> stats = agentService.getClusterStats(clusterId);
        return ResultModel.success(stats);
    }

    @PostMapping("/register")
    public ResultModel<AgentDTO> registerAgent(@RequestBody AgentDTO agent) {
        AgentDTO registered = agentService.registerAgent(agent);
        return ResultModel.success(registered);
    }

    @DeleteMapping("/{agentId}")
    public ResultModel<Boolean> unregisterAgent(@PathVariable String agentId) {
        boolean success = agentService.unregisterAgent(agentId);
        if (success) {
            return ResultModel.success(true);
        }
        return ResultModel.error("Failed to unregister agent: " + agentId);
    }

    @PutMapping("/{agentId}/config")
    public ResultModel<Boolean> updateAgentConfig(
            @PathVariable String agentId,
            @RequestBody Map<String, Object> config) {
        boolean success = agentService.updateAgentConfig(agentId, config);
        if (success) {
            return ResultModel.success(true);
        }
        return ResultModel.error("Failed to update config for agent: " + agentId);
    }

    @GetMapping("/topology")
    public ResultModel<AgentTopologyDTO> getTopology() {
        AgentTopologyDTO topology = agentService.getTopology();
        return ResultModel.success(topology);
    }

    @GetMapping("/topology/cluster/{clusterId}")
    public ResultModel<AgentTopologyDTO> getTopologyByCluster(@PathVariable String clusterId) {
        AgentTopologyDTO topology = agentService.getTopologyByCluster(clusterId);
        return ResultModel.success(topology);
    }

    @GetMapping("/{agentId}/metrics")
    public ResultModel<AgentMetricsDTO> getAgentMetrics(@PathVariable String agentId) {
        AgentMetricsDTO metrics = agentService.getAgentMetrics(agentId);
        if (metrics != null) {
            return ResultModel.success(metrics);
        }
        return ResultModel.error("Agent not found: " + agentId);
    }

    @GetMapping("/metrics/all")
    public ResultModel<List<AgentMetricsDTO>> getAllMetrics() {
        List<AgentMetricsDTO> metrics = agentService.getAllMetrics();
        return ResultModel.success(metrics);
    }

    @PostMapping("/{agentId}/metrics")
    public ResultModel<Boolean> updateAgentMetrics(
            @PathVariable String agentId,
            @RequestBody Map<String, Object> metrics) {
        agentService.updateAgentMetrics(agentId, metrics);
        return ResultModel.success(true);
    }

    @GetMapping("/{agentId}/health")
    public ResultModel<Map<String, Object>> healthCheck(@PathVariable String agentId) {
        Map<String, Object> result = agentService.healthCheck(agentId);
        return ResultModel.success(result);
    }

    @PostMapping("/batch")
    public ResultModel<AgentBatchOperationDTO> executeBatchOperation(
            @RequestBody AgentBatchOperationDTO operation) {
        AgentBatchOperationDTO result = agentService.executeBatchOperation(operation);
        return ResultModel.success(result);
    }

    @GetMapping("/batch/{operationId}")
    public ResultModel<AgentBatchOperationDTO> getBatchOperationStatus(
            @PathVariable String operationId) {
        AgentBatchOperationDTO operation = agentService.getBatchOperationStatus(operationId);
        if (operation != null) {
            return ResultModel.success(operation);
        }
        return ResultModel.error("Operation not found: " + operationId);
    }

    @GetMapping("/capability/{capability}")
    public ResultModel<List<AgentDTO>> listByCapability(@PathVariable String capability) {
        List<AgentDTO> agents = agentService.listByCapability(capability);
        return ResultModel.success(agents);
    }

    @GetMapping("/tag")
    public ResultModel<List<AgentDTO>> listByTag(
            @RequestParam String tagKey,
            @RequestParam String tagValue) {
        List<AgentDTO> agents = agentService.listByTag(tagKey, tagValue);
        return ResultModel.success(agents);
    }

    @GetMapping("/alerts")
    public ResultModel<List<AgentAlertConfigDTO>> listAlertConfigs() {
        List<AgentAlertConfigDTO> configs = agentService.listAlertConfigs();
        return ResultModel.success(configs);
    }

    @GetMapping("/{agentId}/alerts")
    public ResultModel<List<AgentAlertConfigDTO>> getAlertConfigsByAgent(
            @PathVariable String agentId) {
        List<AgentAlertConfigDTO> configs = agentService.getAlertConfigsByAgent(agentId);
        return ResultModel.success(configs);
    }

    @PostMapping("/alerts")
    public ResultModel<AgentAlertConfigDTO> createAlertConfig(
            @RequestBody AgentAlertConfigDTO config) {
        AgentAlertConfigDTO created = agentService.createAlertConfig(config);
        return ResultModel.success(created);
    }

    @PutMapping("/alerts/{id}")
    public ResultModel<AgentAlertConfigDTO> updateAlertConfig(
            @PathVariable Long id,
            @RequestBody AgentAlertConfigDTO config) {
        AgentAlertConfigDTO updated = agentService.updateAlertConfig(id, config);
        if (updated != null) {
            return ResultModel.success(updated);
        }
        return ResultModel.error("Alert config not found: " + id);
    }

    @DeleteMapping("/alerts/{id}")
    public ResultModel<Boolean> deleteAlertConfig(@PathVariable Long id) {
        boolean success = agentService.deleteAlertConfig(id);
        if (success) {
            return ResultModel.success(true);
        }
        return ResultModel.error("Failed to delete alert config: " + id);
    }

    @PostMapping("/alerts/{id}/enable")
    public ResultModel<Boolean> enableAlertConfig(@PathVariable Long id) {
        boolean success = agentService.enableAlertConfig(id);
        if (success) {
            return ResultModel.success(true);
        }
        return ResultModel.error("Failed to enable alert config: " + id);
    }

    @PostMapping("/alerts/{id}/disable")
    public ResultModel<Boolean> disableAlertConfig(@PathVariable Long id) {
        boolean success = agentService.disableAlertConfig(id);
        if (success) {
            return ResultModel.success(true);
        }
        return ResultModel.error("Failed to disable alert config: " + id);
    }
}
