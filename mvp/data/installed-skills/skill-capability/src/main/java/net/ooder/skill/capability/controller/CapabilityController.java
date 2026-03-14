package net.ooder.skill.capability.controller;

import net.ooder.skill.common.model.ResultModel;
import net.ooder.skill.capability.model.Capability;
import net.ooder.skill.capability.model.CapabilityBinding;
import net.ooder.skill.capability.model.CapabilityType;
import net.ooder.skill.capability.model.CapabilityStatus;
import net.ooder.skill.capability.model.CapabilityBindingStatus;
import net.ooder.skill.capability.service.CapabilityService;
import net.ooder.skill.capability.service.CapabilityBindingService;

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

    @GetMapping
    public ResultModel<List<Capability>> listCapabilities(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        
        log.info("List capabilities - type: {}, keyword: {}, status: {}", type, keyword, status);
        
        List<Capability> capabilities = capabilityService.findAll();
        
        if (type != null && !type.isEmpty()) {
            capabilities = capabilityService.findByType(type);
        }
        
        if (status != null && !status.isEmpty()) {
            CapabilityStatus statusEnum = CapabilityStatus.valueOf(status.toUpperCase());
            capabilities = capabilityService.findByStatus(statusEnum);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            capabilities = capabilityService.search(keyword);
        }
        
        return ResultModel.success(capabilities);
    }

    @GetMapping("/types")
    public ResultModel<List<Map<String, Object>>> listCapabilityTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        
        for (CapabilityType type : CapabilityType.values()) {
            Map<String, Object> typeInfo = new HashMap<>();
            typeInfo.put("id", type.name());
            typeInfo.put("name", type.getDisplayName());
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
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        capabilityService.updateStatus(capabilityId, status);
        return ResultModel.success(true);
    }

    @GetMapping("/{capabilityId}/bindings")
    public ResultModel<List<CapabilityBinding>> getCapabilityBindings(@PathVariable String capabilityId) {
        List<CapabilityBinding> bindings = bindingService.listByCapability(capabilityId);
        return ResultModel.success(bindings);
    }

    @PostMapping("/bindings")
    public ResultModel<CapabilityBinding> createBinding(@RequestBody Map<String, Object> request) {
        String capabilityId = (String) request.get("capabilityId");
        String agentId = (String) request.get("agentId");
        String linkId = (String) request.get("linkId");
        
        log.info("Create capability binding: capabilityId={}, agentId={}, linkId={}", capabilityId, agentId, linkId);
        CapabilityBinding binding = bindingService.bind(capabilityId, agentId, linkId);
        return ResultModel.success(binding);
    }

    @DeleteMapping("/bindings/{bindingId}")
    public ResultModel<Boolean> deleteBinding(@PathVariable String bindingId) {
        log.info("Delete binding: {}", bindingId);
        bindingService.unbind(bindingId);
        return ResultModel.success(true);
    }

    @GetMapping("/bindings/by-agent/{agentId}")
    public ResultModel<List<CapabilityBinding>> listBindingsByAgent(@PathVariable String agentId) {
        log.info("List bindings by agent: {}", agentId);
        List<CapabilityBinding> bindings = bindingService.listByAgent(agentId);
        return ResultModel.success(bindings);
    }
}
