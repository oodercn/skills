package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.dto.*;
import net.ooder.skill.agent.service.AgentService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentServiceImpl implements AgentService {

    private final Map<String, AgentInfo> agents = new ConcurrentHashMap<>();
    private static final long OFFLINE_THRESHOLD = 90000;

    @Override
    public AgentInfo registerAgent(Map<String, Object> params) {
        AgentInfo agent = new AgentInfo();
        agent.setAgentId("agent-" + UUID.randomUUID().toString().substring(0, 8));
        agent.setAgentName((String) params.getOrDefault("name", "Unnamed Agent"));
        agent.setAgentType((String) params.getOrDefault("type", "terminal"));
        agent.setIpAddress((String) params.get("ipAddress"));
        agent.setPort(params.get("port") != null ? ((Number) params.get("port")).intValue() : 0);
        agent.setOsType((String) params.get("osType"));
        agent.setOsVersion((String) params.get("osVersion"));
        agent.setArch((String) params.get("arch"));
        agent.setVersion((String) params.get("version"));
        agent.setStatus("online");
        agent.setRegisterTime(System.currentTimeMillis());
        agent.setLastHeartbeat(System.currentTimeMillis());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) params.get("metadata");
        agent.setMetadata(metadata != null ? metadata : new HashMap<>());
        
        agents.put(agent.getAgentId(), agent);
        return agent;
    }

    @Override
    public List<AgentInfo> listAgents() {
        updateAgentStatus();
        return new ArrayList<>(agents.values());
    }

    @Override
    public AgentInfo getAgent(String agentId) {
        AgentInfo agent = agents.get(agentId);
        if (agent != null) {
            updateAgentStatus(agent);
        }
        return agent;
    }

    @Override
    public boolean deleteAgent(String agentId) {
        return agents.remove(agentId) != null;
    }

    @Override
    public boolean heartbeat(String agentId) {
        AgentInfo agent = agents.get(agentId);
        if (agent != null) {
            agent.setLastHeartbeat(System.currentTimeMillis());
            agent.setStatus("online");
            return true;
        }
        return false;
    }

    @Override
    public CommandResult executeCommand(String agentId, String command) {
        AgentInfo agent = agents.get(agentId);
        CommandResult result = new CommandResult();
        result.setAgentId(agentId);
        result.setCommand(command);
        
        if (agent == null) {
            result.setStatus("error");
            result.setError("Agent not found");
            return result;
        }
        
        if (!"online".equals(agent.getStatus())) {
            result.setStatus("error");
            result.setError("Agent is offline");
            return result;
        }
        
        result.setStatus("success");
        result.setExitCode(0);
        result.setOutput("Command executed: " + command);
        result.setEndTime(System.currentTimeMillis());
        result.setDuration(result.getEndTime() - result.getStartTime());
        
        return result;
    }

    @Override
    public AgentNetworkStatus getNetworkStatus(String agentId) {
        AgentInfo agent = agents.get(agentId);
        if (agent == null) {
            return null;
        }
        
        AgentNetworkStatus status = new AgentNetworkStatus();
        status.setAgentId(agentId);
        status.setStatus("online".equals(agent.getStatus()) ? "connected" : "disconnected");
        status.setIpAddress(agent.getIpAddress());
        status.setMacAddress("00:00:00:00:00:00");
        status.setBytesSent(System.currentTimeMillis() % 1000000);
        status.setBytesReceived(System.currentTimeMillis() % 1000000);
        status.setPacketsSent(System.currentTimeMillis() % 10000);
        status.setPacketsReceived(System.currentTimeMillis() % 10000);
        status.setLatency(10.5);
        
        return status;
    }

    @Override
    public AgentInfo getStatus(String agentId) {
        AgentInfo agent = agents.get(agentId);
        if (agent != null) {
            updateAgentStatus(agent);
        }
        return agent;
    }
    
    private void updateAgentStatus() {
        for (AgentInfo agent : agents.values()) {
            updateAgentStatus(agent);
        }
    }
    
    private void updateAgentStatus(AgentInfo agent) {
        long now = System.currentTimeMillis();
        if (now - agent.getLastHeartbeat() > OFFLINE_THRESHOLD) {
            agent.setStatus("offline");
        }
    }
}
