package net.ooder.skill.agent.service;

import net.ooder.skill.agent.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AgentService {
    
    private final Map<String, EndAgent> agents = new HashMap<>();
    private final List<LogEntry> logs = new ArrayList<>();
    private int totalCommands = 0;
    private int successfulCommands = 0;
    private int failedCommands = 0;
    
    public NetworkStatusData getNetworkStatus() {
        NetworkStatusData data = new NetworkStatusData();
        data.setStatus("online");
        data.setMessage("Network is operational");
        data.setTimestamp(System.currentTimeMillis());
        data.setEndAgentCount(agents.size());
        data.setRouteAgentCount(1);
        data.setTotalConnections(agents.size());
        data.setActiveConnections((int) agents.values().stream()
                .filter(a -> "active".equals(a.getStatus()))
                .count());
        data.setPacketLossRate(0.0);
        data.setAvgResponseTime(50.0);
        return data;
    }
    
    public CommandStatsData getCommandStats() {
        CommandStatsData data = new CommandStatsData();
        data.setTotalCommands(totalCommands);
        data.setSuccessfulCommands(successfulCommands);
        data.setFailedCommands(failedCommands);
        return data;
    }
    
    public List<EndAgent> getEndAgents() {
        return new ArrayList<>(agents.values());
    }
    
    public EndAgent addEndAgent(Map<String, Object> agentData) {
        EndAgent agent = new EndAgent();
        agent.setAgentId(UUID.randomUUID().toString());
        agent.setName((String) agentData.get("name"));
        agent.setType((String) agentData.getOrDefault("type", "generic"));
        agent.setStatus("active");
        agent.setIpAddress((String) agentData.get("ipAddress"));
        agent.setRouteAgentId((String) agentData.get("routeAgentId"));
        agent.setVersion((String) agentData.getOrDefault("version", "1.0.0"));
        agent.setDescription((String) agentData.get("description"));
        agent.setCreatedAt(System.currentTimeMillis());
        agent.setLastUpdated(System.currentTimeMillis());
        agents.put(agent.getAgentId(), agent);
        return agent;
    }
    
    public EndAgent editEndAgent(String agentId, Map<String, Object> agentData) {
        EndAgent agent = agents.get(agentId);
        if (agent == null) {
            return null;
        }
        if (agentData.containsKey("name")) {
            agent.setName((String) agentData.get("name"));
        }
        if (agentData.containsKey("type")) {
            agent.setType((String) agentData.get("type"));
        }
        if (agentData.containsKey("ipAddress")) {
            agent.setIpAddress((String) agentData.get("ipAddress"));
        }
        if (agentData.containsKey("description")) {
            agent.setDescription((String) agentData.get("description"));
        }
        agent.setLastUpdated(System.currentTimeMillis());
        return agent;
    }
    
    public EndAgent deleteEndAgent(String agentId) {
        return agents.remove(agentId);
    }
    
    public EndAgent getEndAgentDetails(String agentId) {
        return agents.get(agentId);
    }
    
    public TestCommandResult testCommand(Map<String, Object> commandData) {
        TestCommandResult result = new TestCommandResult();
        result.setCommand((String) commandData.get("command"));
        result.setTimestamp(System.currentTimeMillis());
        
        totalCommands++;
        
        String command = result.getCommand();
        if (command != null && !command.isEmpty()) {
            result.setSuccess(true);
            result.setOutput("Command executed successfully: " + command);
            result.setDuration(100);
            successfulCommands++;
        } else {
            result.setSuccess(false);
            result.setError("Invalid command");
            result.setDuration(0);
            failedCommands++;
        }
        
        return result;
    }
    
    public List<LogEntry> getLogList(int limit) {
        if (limit <= 0 || limit > logs.size()) {
            return new ArrayList<>(logs);
        }
        return logs.subList(0, Math.min(limit, logs.size()));
    }
    
    public void clearLog() {
        logs.clear();
    }
}
