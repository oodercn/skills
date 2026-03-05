package net.ooder.skill.scene.capability.controller;

import net.ooder.skill.scene.capability.dto.StatusUpdateRequest;
import net.ooder.skill.scene.capability.model.*;
import net.ooder.skill.scene.capability.service.CapabilityService;
import net.ooder.skill.scene.capability.service.CapabilityBindingService;
import net.ooder.skill.scene.integration.SceneEngineIntegration;
import net.ooder.skill.scene.model.ResultModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/capabilities")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CapabilityController {

    private static final Logger log = LoggerFactory.getLogger(CapabilityController.class);

    @Autowired
    private CapabilityService capabilityService;

    @Autowired
    private CapabilityBindingService bindingService;
    
    @Autowired(required = false)
    private SceneEngineIntegration sceneEngineIntegration;

    @GetMapping
    public ResultModel<List<Capability>> listCapabilities(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String sceneType,
            @RequestParam(required = false) String keyword) {
        
        log.info("List capabilities - type: {}, sceneType: {}, keyword: {}", type, sceneType, keyword);
        
        List<Capability> capabilities;
        
        if (sceneType != null && !sceneType.isEmpty()) {
            capabilities = capabilityService.findBySceneType(sceneType);
        } else if (type != null && !type.isEmpty()) {
            capabilities = capabilityService.findByType(CapabilityType.valueOf(type));
        } else if (keyword != null && !keyword.isEmpty()) {
            capabilities = capabilityService.search(keyword);
        } else {
            capabilities = capabilityService.findAll();
        }
        
        return ResultModel.success(capabilities);
    }

    @GetMapping("/types")
    public ResultModel<List<Map<String, Object>>> listCapabilityTypes() {
        List<Map<String, Object>> types = new ArrayList<Map<String, Object>>();
        
        for (CapabilityType type : CapabilityType.values()) {
            Map<String, Object> typeInfo = new HashMap<String, Object>();
            typeInfo.put("id", type.name());
            typeInfo.put("name", type.getName());
            typeInfo.put("description", type.getDescription());
            types.add(typeInfo);
        }
        
        return ResultModel.success(types);
    }

    @GetMapping("/{capabilityId}")
    public ResultModel<Capability> getCapability(@PathVariable String capabilityId) {
        Capability capability = capabilityService.findById(capabilityId);
        if (capability == null) {
            return ResultModel.notFound("Capability not found: " + capabilityId);
        }
        return ResultModel.success(capability);
    }

    @PostMapping
    public ResultModel<Capability> createCapability(@RequestBody Capability capability) {
        log.info("Create capability: {}", capability.getCapabilityId());
        Capability created = capabilityService.register(capability);
        return ResultModel.success(created);
    }

    @PutMapping("/{capabilityId}")
    public ResultModel<Capability> updateCapability(
            @PathVariable String capabilityId,
            @RequestBody Capability capability) {
        capability.setCapabilityId(capabilityId);
        Capability updated = capabilityService.update(capability);
        return ResultModel.success(updated);
    }

    @DeleteMapping("/{capabilityId}")
    public ResultModel<Boolean> deleteCapability(@PathVariable String capabilityId) {
        log.info("Delete capability: {}", capabilityId);
        capabilityService.unregister(capabilityId);
        return ResultModel.success(true);
    }

    @PostMapping("/{capabilityId}/status")
    public ResultModel<Boolean> updateStatus(
            @PathVariable String capabilityId,
            @RequestBody StatusUpdateRequest request) {
        capabilityService.updateStatus(capabilityId, request.getStatus());
        return ResultModel.success(true);
    }

    @GetMapping("/{capabilityId}/bindings")
    public ResultModel<List<CapabilityBinding>> getCapabilityBindings(@PathVariable String capabilityId) {
        List<CapabilityBinding> bindings = bindingService.listByCapability(capabilityId);
        return ResultModel.success(bindings);
    }

    @PostMapping("/bindings")
    public ResultModel<CapabilityBinding> createBinding(@RequestBody CapabilityBindingService.CapabilityBindingRequest request) {
        String sceneGroupId = request.getCapabilityId();
        log.info("Create capability binding for scene group: {}", sceneGroupId);
        CapabilityBinding binding = bindingService.bind(sceneGroupId, request);
        return ResultModel.success(binding);
    }

    @GetMapping("/bindings/{bindingId}")
    public ResultModel<CapabilityBinding> getBinding(@PathVariable String bindingId) {
        CapabilityBinding binding = bindingService.findById(bindingId);
        if (binding == null) {
            return ResultModel.notFound("Binding not found: " + bindingId);
        }
        return ResultModel.success(binding);
    }

    @DeleteMapping("/bindings/{bindingId}")
    public ResultModel<Boolean> deleteBinding(@PathVariable String bindingId) {
        log.info("Delete binding: {}", bindingId);
        bindingService.unbind(bindingId);
        return ResultModel.success(true);
    }

    @PostMapping("/bindings/{bindingId}/status")
    public ResultModel<Boolean> updateBindingStatus(
            @PathVariable String bindingId,
            @RequestBody StatusUpdateRequest request) {
        bindingService.updateStatus(bindingId, request.getStatus());
        return ResultModel.success(true);
    }

    @PostMapping("/bindings/{bindingId}/test")
    public ResultModel<Map<String, Object>> testBinding(
            @PathVariable String bindingId,
            @RequestBody(required = false) Map<String, Object> params) {
        log.info("Test binding: {}", bindingId);
        
        CapabilityBinding binding = bindingService.findById(bindingId);
        if (binding == null) {
            return ResultModel.notFound("Binding not found: " + bindingId);
        }
        
        if (binding.getStatus() != CapabilityBindingStatus.ACTIVE) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "Binding is not active");
            errorResult.put("status", binding.getStatus());
            return ResultModel.success(errorResult);
        }
        
        String capabilityId = binding.getCapabilityId();
        if (params == null) {
            params = new HashMap<>();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("bindingId", bindingId);
        result.put("capabilityId", capabilityId);
        result.put("testTime", System.currentTimeMillis());
        
        try {
            if (sceneEngineIntegration != null) {
                Object invokeResult = sceneEngineIntegration.invokeCapability(capabilityId, params);
                result.put("success", true);
                result.put("result", invokeResult);
                result.put("message", "Capability invoked successfully");
            } else {
                result.put("success", true);
                result.put("result", createMockTestResult(capabilityId));
                result.put("message", "SceneEngineIntegration not available, returning mock result");
            }
        } catch (Exception e) {
            log.error("Failed to test binding {}: {}", bindingId, e.getMessage());
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return ResultModel.success(result);
    }
    
    private Map<String, Object> createMockTestResult(String capabilityId) {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("mock", true);
        mockResult.put("capabilityId", capabilityId);
        mockResult.put("message", "This is a mock test result");
        mockResult.put("timestamp", System.currentTimeMillis());
        return mockResult;
    }
}
