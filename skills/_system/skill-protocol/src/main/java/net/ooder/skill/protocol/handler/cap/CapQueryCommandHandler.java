package net.ooder.skill.protocol.handler.cap;

import net.ooder.skill.capability.model.Capability;
import net.ooder.skill.capability.service.CapabilityService;
import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CapQueryCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private CapabilityService capabilityService;
    
    @Override
    public String getCommand() {
        return "cap.query";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String capId = getParamAsString(message, "cap_id");
        String type = getParamAsString(message, "type");
        String category = getParamAsString(message, "category");
        
        if (capId != null && !capId.isEmpty()) {
            Capability cap = capabilityService.findById(capId);
            if (cap == null) {
                return buildErrorResponse(message, ErrorCodes.CAP_NOT_FOUND, 
                    "Capability not found: " + capId);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("cap_id", cap.getCapabilityId());
            response.put("name", cap.getName());
            response.put("description", cap.getDescription());
            response.put("type", cap.getCapabilityType() != null ? cap.getCapabilityType().name() : null);
            response.put("category", cap.getCapabilityCategory() != null ? 
                cap.getCapabilityCategory().name() : null);
            response.put("version", cap.getVersion());
            response.put("status", cap.getStatus() != null ? cap.getStatus().name() : null);
            
            return buildSuccessResponse(message, response);
        } else {
            List<Capability> capabilities;
            
            if (type != null && !type.isEmpty()) {
                try {
                    net.ooder.skill.capability.model.CapabilityType capType = 
                        net.ooder.skill.capability.model.CapabilityType.valueOf(type.toUpperCase());
                    capabilities = capabilityService.findByType(capType);
                } catch (IllegalArgumentException e) {
                    capabilities = capabilityService.findAll();
                }
            } else if (category != null && !category.isEmpty()) {
                try {
                    net.ooder.skill.capability.model.CapabilityCategory capCategory = 
                        net.ooder.skill.capability.model.CapabilityCategory.valueOf(category.toUpperCase());
                    capabilities = capabilityService.findByCapabilityCategory(capCategory);
                } catch (IllegalArgumentException e) {
                    capabilities = capabilityService.findAll();
                }
            } else {
                capabilities = capabilityService.findAll();
            }
            
            List<Map<String, Object>> capList = new ArrayList<>();
            for (Capability cap : capabilities) {
                Map<String, Object> capInfo = new HashMap<>();
                capInfo.put("cap_id", cap.getCapabilityId());
                capInfo.put("name", cap.getName());
                capInfo.put("description", cap.getDescription());
                capInfo.put("type", cap.getCapabilityType() != null ? cap.getCapabilityType().name() : null);
                capInfo.put("category", cap.getCapabilityCategory() != null ? 
                    cap.getCapabilityCategory().name() : null);
                capInfo.put("version", cap.getVersion());
                capInfo.put("status", cap.getStatus() != null ? cap.getStatus().name() : null);
                capList.add(capInfo);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("capabilities", capList);
            response.put("total", capList.size());
            
            return buildSuccessResponse(message, response);
        }
    }
}
