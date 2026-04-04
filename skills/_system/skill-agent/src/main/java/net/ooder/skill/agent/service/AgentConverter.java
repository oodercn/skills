package net.ooder.skill.agent.service;

import net.ooder.skill.agent.dto.AgentDTO;
import net.ooder.skill.agent.dto.AgentStatusDTO;

public class AgentConverter {
    
    public static AgentDTO createDefaultAgent(String agentId, String name, String type, String status) {
        AgentDTO agent = new AgentDTO();
        agent.setAgentId(agentId);
        agent.setAgentName(name);
        agent.setAgentType(type);
        agent.setStatus(status);
        agent.setEnabled(true);
        agent.setRegisterTime(System.currentTimeMillis());
        agent.setLastHeartbeat(System.currentTimeMillis());
        return agent;
    }
    
    public static AgentDTO createAgentWithHeartbeat(String agentId) {
        AgentDTO agent = new AgentDTO();
        agent.setAgentId(agentId);
        agent.setStatus("active");
        agent.setEnabled(true);
        agent.setLastHeartbeat(System.currentTimeMillis());
        return agent;
    }
    
    public static AgentStatusDTO createAgentStatus(String agentId, String status) {
        AgentStatusDTO statusDTO = new AgentStatusDTO();
        statusDTO.setAgentId(agentId);
        statusDTO.setStatus(status);
        statusDTO.setLastCheck(new java.util.Date().toString());
        statusDTO.setEnabled(true);
        statusDTO.setHealthStatus("healthy");
        return statusDTO;
    }
    
    public static void updateHeartbeat(AgentDTO agent) {
        if (agent != null) {
            agent.setLastHeartbeat(System.currentTimeMillis());
            agent.setStatus("active");
        }
    }
}
