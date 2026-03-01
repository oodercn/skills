package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.model.ResultModel;
import net.ooder.skill.scene.integration.SceneEngineIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/capabilities/discovery")
public class CapabilityDiscoveryController {

    private static final Logger log = LoggerFactory.getLogger(CapabilityDiscoveryController.class);

    @Autowired
    private SceneEngineIntegration sceneEngineIntegration;

    @GetMapping
    public ResultModel<List<Map<String, Object>>> discoverByMethod(@RequestParam(required = false) String method) {
        log.info("[discoverByMethod] request start, method: {}", method);
        long start = System.currentTimeMillis();
        
        List<Map<String, Object>> capabilities = sceneEngineIntegration.discoverCapabilities();
        
        if (method != null && !method.isEmpty()) {
            capabilities = filterByMethod(capabilities, method);
        }
        
        ResultModel<List<Map<String, Object>>> result = ResultModel.success(capabilities);
        log.info("[discoverByMethod] request end, elapsed: {}ms, result count: {}", 
            System.currentTimeMillis() - start, capabilities.size());
        return result;
    }
    
    private List<Map<String, Object>> filterByMethod(List<Map<String, Object>> capabilities, String method) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        switch (method) {
            case "LOCAL_FS":
                for (Map<String, Object> cap : capabilities) {
                    Object source = cap.get("source");
                    if (source == null || "local".equals(source) || "LOCAL_FS".equals(source)) {
                        result.add(cap);
                    }
                }
                break;
            case "SKILL_CENTER":
                for (Map<String, Object> cap : capabilities) {
                    Object source = cap.get("source");
                    if ("skill_center".equals(source) || "SKILL_CENTER".equals(source)) {
                        result.add(cap);
                    }
                }
                if (result.isEmpty()) {
                    result.addAll(capabilities.subList(0, Math.min(3, capabilities.size())));
                }
                break;
            case "GITHUB":
            case "GITEE":
            case "GIT_REPOSITORY":
                for (Map<String, Object> cap : capabilities) {
                    Object source = cap.get("source");
                    if ("git".equals(source) || "GITHUB".equals(source) || "GITEE".equals(source)) {
                        result.add(cap);
                    }
                }
                if (result.isEmpty()) {
                    result.addAll(capabilities.subList(0, Math.min(2, capabilities.size())));
                }
                break;
            case "UDP_BROADCAST":
            case "MDNS_DNS_SD":
            case "DHT_KADEMLIA":
                for (Map<String, Object> cap : capabilities) {
                    Object source = cap.get("source");
                    if ("network".equals(source) || "UDP_BROADCAST".equals(source)) {
                        result.add(cap);
                    }
                }
                if (result.isEmpty()) {
                    result.addAll(capabilities.subList(0, Math.min(2, capabilities.size())));
                }
                break;
            case "AUTO":
            default:
                result.addAll(capabilities);
                break;
        }
        
        return result;
    }

    @GetMapping("/categories")
    public ResultModel<List<Map<String, Object>>> listCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        
        String[][] categoryData = {
            {"notification", "通知推送", "ri-notification-line", "支持邮件、短信、站内信等通知能力"},
            {"data-input", "数据输入", "ri-edit-line", "表单填写、文件上传、语音输入等"},
            {"data-processing", "数据处理", "ri-database-2-line", "数据转换、汇总、过滤等"},
            {"intelligence", "智能分析", "ri-brain-line", "AI分析、预测、推荐等"},
            {"collaboration", "协作", "ri-team-line", "任务分配、审批流转等"},
            {"ui", "界面展示", "ri-layout-line", "表单、图表、仪表盘等"},
            {"actuation", "设备控制", "ri-router-line", "物联网设备控制"},
            {"sensing", "传感器读取", "ri-temp-cold-line", "传感器数据采集"}
        };
        
        for (String[] data : categoryData) {
            Map<String, Object> cat = new HashMap<>();
            cat.put("id", data[0]);
            cat.put("name", data[1]);
            cat.put("icon", data[2]);
            cat.put("description", data[3]);
            categories.add(cat);
        }
        
        return ResultModel.success(categories);
    }

    @GetMapping("/{capabilityId}")
    public ResultModel<Map<String, Object>> getCapability(@PathVariable String capabilityId) {
        List<Map<String, Object>> capabilities = sceneEngineIntegration.discoverCapabilities();
        
        for (Map<String, Object> cap : capabilities) {
            if (capabilityId.equals(cap.get("id"))) {
                return ResultModel.success(cap);
            }
        }
        
        return ResultModel.notFound("Capability not found: " + capabilityId);
    }

    @PostMapping("/invoke")
    public ResultModel<Object> invokeCapability(@RequestBody Map<String, Object> request) {
        String capabilityId = (String) request.get("capabilityId");
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) request.get("params");
        
        if (capabilityId == null || capabilityId.isEmpty()) {
            return ResultModel.badRequest("capabilityId is required");
        }
        
        log.info("[invokeCapability] invoking capability: {}", capabilityId);
        
        Object result = sceneEngineIntegration.invokeCapability(capabilityId, params != null ? params : new HashMap<>());
        
        return ResultModel.success(result);
    }
}
