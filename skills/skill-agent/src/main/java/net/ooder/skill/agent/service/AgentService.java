package net.ooder.skill.agent.service;

import net.ooder.skill.agent.dto.*;

import java.util.List;
import java.util.Map;

public interface AgentService {
    AgentInfo registerAgent(Map<String, Object> params);
    List<AgentInfo> listAgents();
    AgentInfo getAgent(String agentId);
    boolean deleteAgent(String agentId);
    boolean heartbeat(String agentId);
    CommandResult executeCommand(String agentId, String command);
    AgentNetworkStatus getNetworkStatus(String agentId);
    AgentInfo getStatus(String agentId);
}
