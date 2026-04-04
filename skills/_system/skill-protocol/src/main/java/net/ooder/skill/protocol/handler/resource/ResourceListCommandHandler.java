package net.ooder.skill.protocol.handler.resource;

import net.ooder.skill.common.sdk.resource.ResourceManager;
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
public class ResourceListCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private ResourceManager resourceManager;
    
    @Override
    public String getCommand() {
        return "resource.list";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String resourceType = getParamAsString(message, "resource_type");
        
        List<Map<String, Object>> resources = new ArrayList<>();
        
        if (resourceType == null || "storage".equals(resourceType)) {
            List<ResourceManager.StorageResource> storageResources = 
                resourceManager.getAllStorageResources();
            for (ResourceManager.StorageResource storage : storageResources) {
                Map<String, Object> resourceInfo = new HashMap<>();
                resourceInfo.put("type", "storage");
                resourceInfo.put("path", storage.getPath());
                resourceInfo.put("total_space", storage.getTotalSpace());
                resourceInfo.put("usable_space", storage.getUsableSpace());
                resourceInfo.put("unallocated_space", storage.getUnallocatedSpace());
                resourceInfo.put("usage_percent", storage.getUsagePercent());
                resources.add(resourceInfo);
            }
        }
        
        if (resourceType == null || "compute".equals(resourceType)) {
            ResourceManager.ComputeResource compute = resourceManager.getComputeResource();
            Map<String, Object> resourceInfo = new HashMap<>();
            resourceInfo.put("type", "compute");
            resourceInfo.put("available_processors", compute.getAvailableProcessors());
            resourceInfo.put("system_load_average", compute.getSystemLoadAverage());
            resourceInfo.put("heap_memory_used", compute.getHeapMemoryUsed());
            resourceInfo.put("heap_memory_max", compute.getHeapMemoryMax());
            resourceInfo.put("memory_usage_percent", compute.getMemoryUsagePercent());
            resources.add(resourceInfo);
        }
        
        if (resourceType == null || "network".equals(resourceType)) {
            ResourceManager.NetworkResource network = resourceManager.getNetworkResource();
            Map<String, Object> resourceInfo = new HashMap<>();
            resourceInfo.put("type", "network");
            resourceInfo.put("hostname", network.getHostname());
            resourceInfo.put("port_range_start", network.getPortRangeStart());
            resourceInfo.put("port_range_end", network.getPortRangeEnd());
            resources.add(resourceInfo);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("resources", resources);
        response.put("total", resources.size());
        
        return buildSuccessResponse(message, response);
    }
}
