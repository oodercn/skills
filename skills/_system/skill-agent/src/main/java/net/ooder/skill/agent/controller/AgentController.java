package net.ooder.skill.agent.controller;

import net.ooder.skill.agent.dto.AgentAlertDTO;
import net.ooder.skill.agent.dto.AgentDTO;
import net.ooder.skill.agent.dto.AgentStatsDTO;
import net.ooder.skill.agent.dto.AgentStatusDTO;
import net.ooder.skill.agent.dto.AgentTopologyDTO;
import net.ooder.skill.agent.model.ResultModel;
import net.ooder.skill.agent.service.AgentConverter;
import net.ooder.skill.agent.service.AgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    private final Map<String, AgentDTO> agentStore = new HashMap<>();

    @jakarta.annotation.PostConstruct
    public void init() {
        AgentDTO agent1 = AgentConverter.createDefaultAgent(
            "agent-001", "Default Agent", "assistant", "active");
        agentStore.put("agent-001", agent1);
        log.info("[AgentController] Initialized with default agent: agent-001");
    }

    @GetMapping("/list")
    public ResultModel<List<AgentDTO>> listAgents() {
        log.info("[AgentController] List agents");
        return ResultModel.success(new ArrayList<>(agentStore.values()));
    }

    @GetMapping("/{id}")
    public ResultModel<AgentDTO> getAgent(@PathVariable String id) {
        log.info("[AgentController] Get agent: {}", id);
        AgentDTO agent = agentStore.get(id);
        if (agent == null) {
            return ResultModel.notFound("Agent not found: " + id);
        }
        return ResultModel.success(agent);
    }

    @PostMapping("/{id}/heartbeat")
    public ResultModel<AgentDTO> heartbeat(@PathVariable String id) {
        log.info("[AgentController] Heartbeat for agent: {}", id);
        AgentDTO agent = agentStore.get(id);
        if (agent == null) {
            agent = AgentConverter.createAgentWithHeartbeat(id);
            agentStore.put(id, agent);
        } else {
            AgentConverter.updateHeartbeat(agent);
        }
        return ResultModel.success(agent);
    }

    @GetMapping("/{id}/status")
    public ResultModel<AgentStatusDTO> getStatus(@PathVariable String id) {
        log.info("[AgentController] Get status for agent: {}", id);
        AgentStatusDTO status = AgentConverter.createAgentStatus(id, "active");
        return ResultModel.success(status);
    }

    @GetMapping("/alerts")
    public ResultModel<List<AgentAlertDTO>> getAlerts() {
        log.info("[AgentController] Get alerts");
        List<AgentAlertDTO> alerts = new ArrayList<>();
        return ResultModel.success(alerts);
    }

    @GetMapping("/stats")
    public ResultModel<AgentStatsDTO> getStats() {
        log.info("[AgentController] Get overall stats");
        AgentStatsDTO stats = agentService.getOverallStats();
        return ResultModel.success(stats);
    }

    @GetMapping("/topology")
    public ResultModel<AgentTopologyDTO> getTopology() {
        log.info("[AgentController] Get topology");
        AgentTopologyDTO topology = agentService.getTopology();
        return ResultModel.success(topology);
    }
}
