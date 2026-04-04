package net.ooder.skill.capability.controller;

import net.ooder.skill.capability.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/scene-capabilities")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class SceneCapabilityController {

    private static final Logger log = LoggerFactory.getLogger(SceneCapabilityController.class);

    private final Map<String, Map<String, Object>> capabilityStore = new HashMap<>();

    @GetMapping("/{id}")
    public ResultModel<Map<String, Object>> getCapability(@PathVariable String id) {
        log.info("[SceneCapabilityController] Get capability: {}", id);
        Map<String, Object> capability = capabilityStore.get(id);
        if (capability == null) {
            return ResultModel.notFound("Capability not found: " + id);
        }
        return ResultModel.success(capability);
    }

    @PostMapping("/{id}/activate")
    public ResultModel<Map<String, Object>> activateCapability(@PathVariable String id) {
        log.info("[SceneCapabilityController] Activate capability: {}", id);
        Map<String, Object> capability = capabilityStore.get(id);
        if (capability == null) {
            capability = new HashMap<>();
            capability.put("id", id);
            capabilityStore.put(id, capability);
        }
        capability.put("active", true);
        capability.put("activateTime", new Date().toString());
        return ResultModel.success(capability);
    }

    @PostMapping("/{id}/deactivate")
    public ResultModel<Map<String, Object>> deactivateCapability(@PathVariable String id) {
        log.info("[SceneCapabilityController] Deactivate capability: {}", id);
        Map<String, Object> capability = capabilityStore.get(id);
        if (capability == null) {
            capability = new HashMap<>();
            capability.put("id", id);
            capabilityStore.put(id, capability);
        }
        capability.put("active", false);
        capability.put("deactivateTime", new Date().toString());
        return ResultModel.success(capability);
    }
}
