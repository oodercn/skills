package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.capability.service.CapabilityDiscoveryService;
import net.ooder.skill.scene.capability.service.CapabilityDiscoveryService.*;
import net.ooder.skill.scene.capability.model.CapabilityType;
import net.ooder.skill.scene.model.ResultModel;
import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.scene.CapabilityInfoDTO;
import net.ooder.skill.scene.capability.service.CapabilityDiscoveryService.CapabilityItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/discovery/capabilities")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CapabilityDiscoveryController {

    private static final Logger log = LoggerFactory.getLogger(CapabilityDiscoveryController.class);

    @Autowired
    private CapabilityDiscoveryService discoveryService;

    @GetMapping("/discover")
    public ResultModel<DiscoveryResult> discoverCapabilities(
            @RequestParam(required = false, defaultValue = "AUTO") String method,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String sceneType,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        
        log.info("[discoverCapabilities] method={}, query={}, type={}, sceneType={}", 
            method, query, type, sceneType);
        
        DiscoveryRequest request = new DiscoveryRequest();
        request.setMethod(DiscoveryMethod.valueOf(method.toUpperCase()));
        request.setQuery(query);
        if (type != null && !type.isEmpty()) {
            request.setType(CapabilityType.valueOf(type.toUpperCase()));
        }
        request.setSceneType(sceneType);
        request.setPage(page);
        request.setSize(size);
        
        DiscoveryResult result = discoveryService.discoverCapabilities(request);
        return ResultModel.success(result);
    }

    @GetMapping("/detail/{capabilityId}")
    public ResultModel<CapabilityDetail> getCapabilityDetail(
            @PathVariable String capabilityId) {
        
        log.info("[getCapabilityDetail] capabilityId={}", capabilityId);
        
        CapabilityDetail detail = discoveryService.getCapabilityDetail(capabilityId);
        if (detail == null) {
            return ResultModel.notFound("Capability not found: " + capabilityId);
        }
        return ResultModel.success(detail);
    }

    @GetMapping("/detail/{capabilityId}/driver-conditions")
    public ResultModel<List<net.ooder.skill.scene.capability.driver.DriverCondition>> getDriverConditions(
            @PathVariable String capabilityId) {
        
        log.info("[getDriverConditions] capabilityId={}", capabilityId);
        
        List<net.ooder.skill.scene.capability.driver.DriverCondition> conditions = 
            discoveryService.getDriverConditions(capabilityId);
        return ResultModel.success(conditions);
    }

    @GetMapping("/types")
    public ResultModel<List<CapabilityTypeInfo>> getCapabilityTypes() {
        List<CapabilityTypeInfo> types = new java.util.ArrayList<>();
        
        for (CapabilityType type : CapabilityType.values()) {
            CapabilityTypeInfo info = new CapabilityTypeInfo();
            info.setCode(type.getCode());
            info.setName(type.getName());
            info.setDescription(type.getDescription());
            info.setIcon(type.getIcon());
            info.setCategory(type.getCategory().getName());
            info.setSceneType(type == CapabilityType.SCENE);
            types.add(info);
        }
        
        return ResultModel.success(types);
    }

    @GetMapping
    public ResultModel<PageResult<CapabilityItem>> getCapabilities(
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "20") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String query) {
        
        log.info("[getCapabilities] pageNum={}, pageSize={}, type={}, status={}, query={}", 
            pageNum, pageSize, type, status, query);
        
        DiscoveryRequest request = new DiscoveryRequest();
        request.setMethod(DiscoveryMethod.AUTO);
        request.setPage(pageNum - 1);
        request.setSize(pageSize);
        if (type != null && !type.isEmpty()) {
            request.setType(CapabilityType.valueOf(type.toUpperCase()));
        }
        
        DiscoveryResult discoveryResult = discoveryService.discoverCapabilities(request);
        
        List<CapabilityItem> allCapabilities = new java.util.ArrayList<>();
        allCapabilities.addAll(discoveryResult.getSceneCapabilities());
        allCapabilities.addAll(discoveryResult.getCollaborationCapabilities());
        
        PageResult<CapabilityItem> pageResult = new PageResult<>();
        pageResult.setList(allCapabilities);
        pageResult.setTotal(allCapabilities.size());
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        
        return ResultModel.success(pageResult);
    }

    @PostMapping("/invoke")
    public ResultModel<Object> invokeCapability(@RequestBody InvokeRequest request) {
        log.info("[invokeCapability] capabilityId={}, params={}", request.getCapabilityId(), request.getParams());
        
        try {
            Object result = discoveryService.invokeCapability(request.getCapabilityId(), request.getParams());
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("[invokeCapability] Failed to invoke capability: {}", e.getMessage());
            return ResultModel.error(500, "Failed to invoke capability: " + e.getMessage());
        }
    }

    public static class InvokeRequest {
        private String capabilityId;
        private java.util.Map<String, Object> params;

        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public java.util.Map<String, Object> getParams() { return params; }
        public void setParams(java.util.Map<String, Object> params) { this.params = params; }
    }

    public static class CapabilityTypeInfo {
        private String code;
        private String name;
        private String description;
        private String icon;
        private String category;
        private boolean sceneType;
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public boolean isSceneType() { return sceneType; }
        public void setSceneType(boolean sceneType) { this.sceneType = sceneType; }
    }
}
