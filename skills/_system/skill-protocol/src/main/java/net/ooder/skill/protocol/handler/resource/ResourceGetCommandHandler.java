package net.ooder.skill.protocol.handler.resource;

import net.ooder.skill.common.sdk.resource.ResourceManager;
import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ResourceGetCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private ResourceManager resourceManager;
    
    @Override
    public String getCommand() {
        return "resource.get";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String resourceId = getParamAsString(message, "resource_id");
        String resourceType = getParamAsString(message, "resource_type");
        
        if (resourceId == null || resourceId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: resource_id");
        }
        
        Map<String, Object> response = new HashMap<>();
        
        if ("quota".equals(resourceType)) {
            ResourceManager.ResourceQuota quota = resourceManager.getQuota(resourceId);
            if (quota == null) {
                return buildErrorResponse(message, ErrorCodes.RESOURCE_NOT_FOUND, 
                    "Resource quota not found: " + resourceId);
            }
            
            response.put("type", "quota");
            response.put("skill_id", resourceId);
            response.put("storage_quota", quota.getStorageQuota());
            response.put("max_cpu_percent", quota.getMaxCpuPercent());
            response.put("max_memory_bytes", quota.getMaxMemoryBytes());
            response.put("max_connections", quota.getMaxConnections());
            response.put("max_bandwidth_kbps", quota.getMaxBandwidthKbps());
            
            ResourceManager.ResourceUsage usage = resourceManager.getUsage(resourceId);
            if (usage != null) {
                response.put("usage", Map.of(
                    "storage_used", usage.getStorageUsed(),
                    "memory_used", usage.getMemoryUsed(),
                    "cpu_percent", usage.getCpuPercent(),
                    "active_connections", usage.getActiveConnections(),
                    "timestamp", usage.getTimestamp()
                ));
            }
        } else {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Unsupported resource type: " + resourceType + ". Supported types: quota");
        }
        
        return buildSuccessResponse(message, response);
    }
}
