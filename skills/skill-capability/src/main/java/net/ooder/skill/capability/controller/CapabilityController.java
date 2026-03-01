package net.ooder.skill.capability.controller;

import net.ooder.skill.capability.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/capabilities")
public class CapabilityController {

    @Autowired
    private CapabilityRegistry capabilityRegistry;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listCapabilities(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String skillId) {
        
        List<CapabilityDefinition> capabilities;
        
        if (skillId != null && !skillId.isEmpty()) {
            capabilities = capabilityRegistry.getCapabilitiesForSkill(skillId);
        } else if (category != null && !category.isEmpty()) {
            try {
                CapabilityCategory cat = CapabilityCategory.valueOf(category.toUpperCase());
                capabilities = capabilityRegistry.getCapabilitiesByCategory(cat);
            } catch (IllegalArgumentException e) {
                capabilities = capabilityRegistry.getAllCapabilities();
            }
        } else if (search != null && !search.isEmpty()) {
            capabilities = capabilityRegistry.searchCapabilities(search);
        } else {
            capabilities = capabilityRegistry.getAllCapabilities();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", capabilities);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{capabilityId}")
    public ResponseEntity<Map<String, Object>> getCapability(@PathVariable String capabilityId) {
        CapabilityDefinition capability = capabilityRegistry.getCapability(capabilityId);
        
        Map<String, Object> response = new HashMap<>();
        if (capability == null) {
            response.put("status", "error");
            response.put("message", "Capability not found");
            return ResponseEntity.status(404).body(response);
        }
        
        response.put("status", "success");
        response.put("data", capability);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{capabilityId}/metadata")
    public ResponseEntity<Map<String, Object>> getMetadata(@PathVariable String capabilityId) {
        CapabilityMetadata metadata = capabilityRegistry.getMetadata(capabilityId);
        
        Map<String, Object> response = new HashMap<>();
        if (metadata == null) {
            response.put("status", "error");
            response.put("message", "Capability metadata not found");
            return ResponseEntity.status(404).body(response);
        }
        
        response.put("status", "success");
        response.put("data", metadata);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registerCapability(@RequestBody CapabilityDefinition capability) {
        capabilityRegistry.register(capability);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Capability registered successfully");
        response.put("data", capability);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{capabilityId}/skills/{skillId}")
    public ResponseEntity<Map<String, Object>> registerForSkill(
            @PathVariable String capabilityId,
            @PathVariable String skillId) {
        capabilityRegistry.registerForSkill(skillId, capabilityId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Capability registered for skill");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{capabilityId}")
    public ResponseEntity<Map<String, Object>> unregisterCapability(@PathVariable String capabilityId) {
        capabilityRegistry.unregisterCapability(capabilityId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Capability unregistered successfully");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<Map<String, Object>> unregisterForSkill(@PathVariable String skillId) {
        capabilityRegistry.unregisterForSkill(skillId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "All capabilities unregistered for skill");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/skills/{skillId}")
    public ResponseEntity<Map<String, Object>> getCapabilitiesForSkill(@PathVariable String skillId) {
        List<CapabilityDefinition> capabilities = capabilityRegistry.getCapabilitiesForSkill(skillId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", capabilities);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = capabilityRegistry.getStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", stats);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        for (CapabilityCategory cat : CapabilityCategory.values()) {
            Map<String, Object> catInfo = new HashMap<>();
            catInfo.put("value", cat.getValue());
            catInfo.put("label", cat.getLabel());
            catInfo.put("name", cat.name());
            categories.add(catInfo);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", categories);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{capabilityId}/validate")
    public ResponseEntity<Map<String, Object>> validateParameters(
            @PathVariable String capabilityId,
            @RequestBody Map<String, Object> params) {
        
        CapabilityDefinition capability = capabilityRegistry.getCapability(capabilityId);
        
        Map<String, Object> response = new HashMap<>();
        if (capability == null) {
            response.put("status", "error");
            response.put("message", "Capability not found");
            return ResponseEntity.status(404).body(response);
        }
        
        boolean valid = capability.validateParameters(params);
        response.put("status", "success");
        response.put("data", Collections.singletonMap("valid", valid));
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{capabilityId}/usage")
    public ResponseEntity<Map<String, Object>> recordUsage(
            @PathVariable String capabilityId,
            @RequestBody Map<String, Object> body) {
        
        Long executionTime = body.get("executionTime") != null 
                ? ((Number) body.get("executionTime")).longValue() 
                : 0L;
        
        capabilityRegistry.recordUsage(capabilityId, executionTime);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Usage recorded");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
