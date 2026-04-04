package net.ooder.skill.protocol.handler.cap;

import net.ooder.skill.capability.model.Capability;
import net.ooder.skill.capability.service.CapabilityService;
import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CapRemoveCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private CapabilityService capabilityService;
    
    @Override
    public String getCommand() {
        return "cap.remove";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String capId = getParamAsString(message, "cap_id");
        
        if (capId == null || capId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: cap_id");
        }
        
        Capability existingCap = capabilityService.findById(capId);
        if (existingCap == null) {
            return buildErrorResponse(message, ErrorCodes.CAP_NOT_FOUND, 
                "Capability not found: " + capId);
        }
        
        capabilityService.unregister(capId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("cap_id", capId);
        response.put("status", "removed");
        response.put("message", "Capability removed successfully");
        
        return buildSuccessResponse(message, response);
    }
}
