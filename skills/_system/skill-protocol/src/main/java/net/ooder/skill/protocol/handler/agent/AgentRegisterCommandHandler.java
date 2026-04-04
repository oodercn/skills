package net.ooder.skill.protocol.handler.agent;

import net.ooder.skill.agent.dto.AgentDTO;
import net.ooder.skill.agent.service.AgentService;
import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AgentRegisterCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private AgentService agentService;
    
    @Override
    public String getCommand() {
        return "agent.register";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String agentId = getParamAsString(message, "agent_id");
        String agentName = getParamAsString(message, "agent_name");
        String agentType = getParamAsString(message, "agent_type");
        String ipAddress = getParamAsString(message, "ip_address");
        Integer port = getParamAsInteger(message, "port");
        String version = getParamAsString(message, "version");
        String description = getParamAsString(message, "description");
        String clusterId = getParamAsString(message, "cluster_id");
        
        if (agentId == null || agentId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: agent_id");
        }
        
        if (agentName == null || agentName.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: agent_name");
        }
        
        AgentDTO existingAgent = agentService.getAgent(agentId);
        if (existingAgent != null) {
            return buildErrorResponse(message, ErrorCodes.CONFLICT, 
                "Agent already registered: " + agentId);
        }
        
        AgentDTO agent = new AgentDTO();
        agent.setAgentId(agentId);
        agent.setAgentName(agentName);
        agent.setAgentType(agentType != null ? agentType : "end");
        agent.setIpAddress(ipAddress);
        agent.setPort(port != null ? port : 0);
        agent.setVersion(version != null ? version : "1.0.0");
        agent.setDescription(description);
        agent.setClusterId(clusterId);
        agent.setStatus("online");
        
        AgentDTO registeredAgent = agentService.registerAgent(agent);
        
        Map<String, Object> response = new HashMap<>();
        response.put("agent_id", registeredAgent.getAgentId());
        response.put("agent_name", registeredAgent.getAgentName());
        response.put("status", "registered");
        response.put("message", "Agent registered successfully");
        
        return buildSuccessResponse(message, response);
    }
}
