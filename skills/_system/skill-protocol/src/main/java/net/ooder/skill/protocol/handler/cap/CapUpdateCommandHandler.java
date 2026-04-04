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
public class CapUpdateCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private CapabilityService capabilityService;
    
    @Override
    public String getCommand() {
        return "cap.update";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String capId = getParamAsString(message, "cap_id");
        String name = getParamAsString(message, "name");
        String description = getParamAsString(message, "description");
        String status = getParamAsString(message, "status");
        String version = getParamAsString(message, "version");
        
        if (capId == null || capId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: cap_id");
        }
        
        Capability existingCap = capabilityService.findById(capId);
        if (existingCap == null) {
            return buildErrorResponse(message, ErrorCodes.CAP_NOT_FOUND, 
                "Capability not found: " + capId);
        }
        
        if (name != null && !name.isEmpty()) {
            existingCap.setName(name);
        }
        
        if (description != null) {
            existingCap.setDescription(description);
        }
        
        if (version != null && !version.isEmpty()) {
            existingCap.setVersion(version);
        }
        
        Capability updatedCap = capabilityService.update(existingCap);
        
        if (status != null && !status.isEmpty()) {
            capabilityService.updateStatus(capId, status);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("cap_id", updatedCap.getCapabilityId());
        response.put("name", updatedCap.getName());
        response.put("status", "updated");
        response.put("message", "Capability updated successfully");
        
        return buildSuccessResponse(message, response);
    }
}
