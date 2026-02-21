package net.ooder.skill.agent.controller;

import net.ooder.skill.agent.model.*;
import net.ooder.skill.agent.provider.Result;
import net.ooder.skill.agent.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agents")
public class AgentController {
    
    @Autowired
    private AgentService agentService;
    
    @GetMapping("/status")
    public Result<NetworkStatusData> getNetworkStatus() {
        return Result.success(agentService.getNetworkStatus());
    }
    
    @GetMapping("/stats")
    public Result<CommandStatsData> getCommandStats() {
        return Result.success(agentService.getCommandStats());
    }
    
    @GetMapping
    public Result<List<EndAgent>> getEndAgents() {
        return Result.success(agentService.getEndAgents());
    }
    
    @PostMapping
    public Result<EndAgent> addEndAgent(@RequestBody Map<String, Object> agentData) {
        return Result.success(agentService.addEndAgent(agentData));
    }
    
    @PutMapping("/{id}")
    public Result<EndAgent> editEndAgent(
            @PathVariable String id,
            @RequestBody Map<String, Object> agentData) {
        EndAgent agent = agentService.editEndAgent(id, agentData);
        if (agent == null) {
            return Result.notFound("Agent not found");
        }
        return Result.success(agent);
    }
    
    @DeleteMapping("/{id}")
    public Result<EndAgent> deleteEndAgent(@PathVariable String id) {
        EndAgent agent = agentService.deleteEndAgent(id);
        if (agent == null) {
            return Result.notFound("Agent not found");
        }
        return Result.success(agent);
    }
    
    @GetMapping("/{id}")
    public Result<EndAgent> getEndAgentDetails(@PathVariable String id) {
        EndAgent agent = agentService.getEndAgentDetails(id);
        if (agent == null) {
            return Result.notFound("Agent not found");
        }
        return Result.success(agent);
    }
    
    @PostMapping("/test-command")
    public Result<TestCommandResult> testCommand(@RequestBody Map<String, Object> commandData) {
        return Result.success(agentService.testCommand(commandData));
    }
    
    @GetMapping("/logs")
    public Result<List<LogEntry>> getLogList(@RequestParam(defaultValue = "100") int limit) {
        return Result.success(agentService.getLogList(limit));
    }
    
    @DeleteMapping("/logs")
    public Result<Void> clearLog() {
        agentService.clearLog();
        return Result.success();
    }
}
