package net.ooder.skill.scene.agent.controller;

import net.ooder.skill.scene.agent.dto.AgentDTO;
import net.ooder.skill.scene.agent.service.AgentService;
import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.model.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agent")
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
        PageResult<AgentDTO> allAgents = agentService.listAgents(1, 1000);
        
        int total = (int) allAgents.getTotal();
        int online = 0;
        int busy = 0;
        int offline = 0;
        
        if (allAgents.getList() != null) {
            for (AgentDTO agent : allAgents.getList()) {
                String status = agent.getStatus();
                if ("online".equalsIgnoreCase(status) || "running".equalsIgnoreCase(status)) {
                    online++;
                } else if ("busy".equalsIgnoreCase(status)) {
                    busy++;
                } else {
                    offline++;
                }
            }
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("online", online);
        stats.put("busy", busy);
        stats.put("offline", offline);
        
        return ResultModel.success(stats);
    }
}
