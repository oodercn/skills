package net.ooder.skill.agent.controller;

import net.ooder.skill.agent.dto.*;
import net.ooder.skill.agent.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @PostMapping("/register")
    public ResponseEntity<AgentInfo> registerAgent(@RequestBody Map<String, Object> params) {
        return ResponseEntity.ok(agentService.registerAgent(params));
    }

    @GetMapping("/list")
    public ResponseEntity<List<AgentInfo>> listAgents() {
        return ResponseEntity.ok(agentService.listAgents());
    }

    @GetMapping("/{agentId}")
    public ResponseEntity<AgentInfo> getAgent(@PathVariable String agentId) {
        AgentInfo agent = agentService.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(agent);
    }

    @DeleteMapping("/{agentId}")
    public ResponseEntity<Boolean> deleteAgent(@PathVariable String agentId) {
        return ResponseEntity.ok(agentService.deleteAgent(agentId));
    }

    @PostMapping("/{agentId}/heartbeat")
    public ResponseEntity<Boolean> heartbeat(@PathVariable String agentId) {
        return ResponseEntity.ok(agentService.heartbeat(agentId));
    }

    @PostMapping("/{agentId}/command")
    public ResponseEntity<CommandResult> executeCommand(
            @PathVariable String agentId,
            @RequestBody Map<String, String> params) {
        String command = params.get("command");
        return ResponseEntity.ok(agentService.executeCommand(agentId, command));
    }

    @GetMapping("/{agentId}/network")
    public ResponseEntity<AgentNetworkStatus> getNetworkStatus(@PathVariable String agentId) {
        AgentNetworkStatus status = agentService.getNetworkStatus(agentId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{agentId}/status")
    public ResponseEntity<AgentInfo> getStatus(@PathVariable String agentId) {
        AgentInfo agent = agentService.getStatus(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(agent);
    }
}
