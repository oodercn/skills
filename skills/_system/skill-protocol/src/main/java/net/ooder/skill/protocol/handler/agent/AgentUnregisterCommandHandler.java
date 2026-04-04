package net.ooder.skill.protocol.handler.agent;

import net.ooder.skill.agent.service.AgentService;
import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AgentUnregisterCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private AgentService agentService;
    
    @Override
    public String getCommand() {
        return "agent.unregister";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String agentId = getParamAsString(message, "agent_id");
        
        if (agentId == null || agentId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: agent_id");
        }
        
        boolean success = agentService.unregisterAgent(agentId);
        
        if (!success) {
            return buildErrorResponse(message, ErrorCodes.AGENT_NOT_FOUND, 
                "Agent not found: " + agentId);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("agent_id", agentId);
        response.put("status", "unregistered");
        response.put("message", "Agent unregistered successfully");
        
        return buildSuccessResponse(message, response);
    }
}
