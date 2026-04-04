package net.ooder.skill.protocol.handler.cap;

import net.ooder.skill.capability.model.Capability;
import net.ooder.skill.capability.model.CapabilityType;
import net.ooder.skill.capability.model.CapabilityCategory;
import net.ooder.skill.capability.model.CapabilityOwnership;
import net.ooder.skill.capability.service.CapabilityService;
import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CapDeclareCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private CapabilityService capabilityService;
    
    @Override
    public String getCommand() {
        return "cap.declare";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String capId = getParamAsString(message, "cap_id");
        String name = getParamAsString(message, "name");
        String description = getParamAsString(message, "description");
        String type = getParamAsString(message, "type");
        String category = getParamAsString(message, "category");
        String version = getParamAsString(message, "version");
        
        if (capId == null || capId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: cap_id");
        }
        
        if (name == null || name.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: name");
        }
        
        Capability existingCap = capabilityService.findById(capId);
        if (existingCap != null) {
            return buildErrorResponse(message, ErrorCodes.CONFLICT, 
                "Capability already declared: " + capId);
        }
        
        Capability capability = new Capability();
        capability.setCapabilityId(capId);
        capability.setName(name);
        capability.setDescription(description != null ? description : "");
        
        if (type != null) {
            try {
                capability.setCapabilityType(CapabilityType.valueOf(type.toUpperCase()));
            } catch (IllegalArgumentException e) {
                capability.setCapabilityType(CapabilityType.PROVIDER);
            }
        }
        
        if (category != null) {
            try {
                capability.setCapabilityCategory(CapabilityCategory.valueOf(category.toUpperCase()));
            } catch (IllegalArgumentException e) {
                capability.setCapabilityCategory(CapabilityCategory.SYS);
            }
        }
        
        capability.setVersion(version != null ? version : "1.0.0");
        
        Capability registeredCap = capabilityService.register(capability);
        
        Map<String, Object> response = new HashMap<>();
        response.put("cap_id", registeredCap.getCapabilityId());
        response.put("name", registeredCap.getName());
        response.put("status", "declared");
        response.put("message", "Capability declared successfully");
        
        return buildSuccessResponse(message, response);
    }
}
