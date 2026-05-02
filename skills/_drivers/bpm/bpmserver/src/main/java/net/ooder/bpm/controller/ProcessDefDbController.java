package net.ooder.bpm.controller;

import net.ooder.bpm.service.ProcessDefManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/processdef")
public class ProcessDefDbController {

    private static final Logger log = LoggerFactory.getLogger(ProcessDefDbController.class);

    @Autowired
    private ProcessDefManagerService processDefManagerService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listProcessDefs() {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> processDefs = processDefManagerService.getAllProcessDefs();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", processDefs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to list process defs", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            response.put("exception", e.getClass().getName());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{processDefId}")
    public ResponseEntity<Map<String, Object>> getProcessDef(@PathVariable String processDefId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> fullProcessDef = processDefManagerService.getFullProcessDef(processDefId);
            if (fullProcessDef == null) {
                response.put("code", 404);
                response.put("message", "流程定义不存在: " + processDefId);
                return ResponseEntity.status(404).body(response);
            }
            
            Map<String, Object> result = formatProcessDef(fullProcessDef);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveProcessDef(@RequestBody Map<String, Object> processData) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            String processDefId = (String) processData.get("processDefId");
            processDefManagerService.saveProcessDef(processData);
            
            Map<String, Object> fullProcessDef = processDefManagerService.getFullProcessDef(processDefId);
            if (fullProcessDef == null) {
                response.put("code", 500);
                response.put("message", "保存后获取流程失败");
                return ResponseEntity.internalServerError().body(response);
            }
            
            Map<String, Object> result = formatProcessDef(fullProcessDef);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @DeleteMapping("/{processDefId}")
    public ResponseEntity<Map<String, Object>> deleteProcessDef(@PathVariable String processDefId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            processDefManagerService.deleteProcessDef(processDefId);
            response.put("code", 200);
            response.put("message", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to delete process def: {}", processDefId, e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private Map<String, Object> formatProcessDef(Map<String, Object> fullProcessDef) {
        Map<String, Object> processDef = (Map<String, Object>) fullProcessDef.get("processDef");
        Map<String, Object> activeVersion = (Map<String, Object>) fullProcessDef.get("activeVersion");
        List<Map<String, Object>> activities = (List<Map<String, Object>>) fullProcessDef.get("activities");
        List<Map<String, Object>> routes = (List<Map<String, Object>>) fullProcessDef.get("routes");
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("processDefId", processDef.get("processDefId"));
        result.put("name", processDef.get("name"));
        result.put("description", processDef.get("description"));
        result.put("classification", processDef.get("classification"));
        result.put("accessLevel", processDef.get("accessLevel"));
        
        if (activeVersion != null) {
            result.put("version", activeVersion.get("version"));
            result.put("publicationStatus", activeVersion.get("state"));
        }
        
        List<Map<String, Object>> formattedActivities = new ArrayList<>();
        if (activities != null) {
            for (Map<String, Object> activity : activities) {
                Map<String, Object> formattedActivity = new LinkedHashMap<>();
                formattedActivity.put("activityDefId", activity.get("activityDefId"));
                formattedActivity.put("name", activity.get("name"));
                formattedActivity.put("description", activity.get("description"));
                
                String position = (String) activity.get("position");
                String activityType = (String) activity.get("activityType");
                String activityCategory = (String) activity.get("activityCategory");
                
                if ("POSITION_START".equals(position) || "START".equals(position)) {
                    formattedActivity.put("position", "START");
                } else if ("POSITION_END".equals(position) || "END".equals(position)) {
                    formattedActivity.put("position", "END");
                } else {
                    formattedActivity.put("position", "NORMAL");
                }
                
                if (activityType == null || activityType.isEmpty()) {
                    if ("POSITION_START".equals(position) || "START".equals(position)) {
                        activityType = "START";
                    } else if ("POSITION_END".equals(position) || "END".equals(position)) {
                        activityType = "END";
                    } else {
                        activityType = "TASK";
                    }
                }
                formattedActivity.put("activityType", activityType);
                
                // 使用数据库中的 activityCategory，如果没有则默认为 HUMAN
                if (activityCategory == null || activityCategory.isEmpty()) {
                    activityCategory = "HUMAN";
                }
                formattedActivity.put("activityCategory", activityCategory);
                
                formattedActivity.put("implementation", activity.get("implementation"));
                formattedActivity.put("positionCoord", activity.get("positionCoord"));
                formattedActivity.put("timing", new HashMap<>());
                formattedActivity.put("routing", new HashMap<>());
                formattedActivity.put("right", new HashMap<>());
                formattedActivity.put("subFlow", new HashMap<>());
                formattedActivity.put("device", new HashMap<>());
                formattedActivity.put("service", new HashMap<>());
                formattedActivity.put("event", new HashMap<>());
                formattedActivity.put("agentConfig", null);
                formattedActivity.put("sceneConfig", null);
                formattedActivity.put("extendedAttributes", new HashMap<>());
                
                formattedActivities.add(formattedActivity);
            }
        }
        result.put("activities", formattedActivities);
        
        List<Map<String, Object>> formattedRoutes = new ArrayList<>();
        if (routes != null) {
            for (Map<String, Object> route : routes) {
                Map<String, Object> formattedRoute = new LinkedHashMap<>();
                formattedRoute.put("routeDefId", route.get("routeDefId"));
                formattedRoute.put("name", route.get("name"));
                formattedRoute.put("description", route.get("description"));
                formattedRoute.put("from", route.get("fromActivityDefId"));
                formattedRoute.put("to", route.get("toActivityDefId"));
                formattedRoute.put("routeOrder", route.get("routeOrder"));
                formattedRoute.put("routeDirection", route.get("routeDirection"));
                formattedRoute.put("routeConditionType", route.get("routeConditionType"));
                formattedRoute.put("condition", route.get("routeCondition"));
                formattedRoute.put("extendedAttributes", new HashMap<>());
                
                formattedRoutes.add(formattedRoute);
            }
        }
        result.put("routes", formattedRoutes);
        result.put("listeners", new ArrayList<>());
        result.put("formulas", new ArrayList<>());
        result.put("parameters", new ArrayList<>());
        result.put("extendedAttributes", new HashMap<>());
        result.put("agentConfig", null);
        result.put("sceneConfig", null);
        result.put("createdTime", activeVersion != null ? activeVersion.get("createTime") : null);
        result.put("updatedTime", activeVersion != null ? activeVersion.get("activeTime") : null);
        
        return result;
    }

    // ==================== 前端适配 API ====================

    @GetMapping("/process/{processId}")
    public ResponseEntity<Map<String, Object>> getProcess(@PathVariable String processId) {
        return getProcessDef(processId);
    }

    @GetMapping("/process/{processId}/version/{version}")
    public ResponseEntity<Map<String, Object>> getProcessVersion(@PathVariable String processId, @PathVariable String version) {
        // 如果version是latest，返回激活版本
        if ("latest".equalsIgnoreCase(version)) {
            return getProcessDef(processId);
        }
        // 否则返回指定版本
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> versions = processDefManagerService.getProcessVersions(processId);
            Map<String, Object> targetVersion = null;
            for (Map<String, Object> v : versions) {
                if (version.equals(String.valueOf(v.get("version")))) {
                    targetVersion = v;
                    break;
                }
            }
            if (targetVersion == null) {
                response.put("code", 404);
                response.put("message", "版本不存在: " + version);
                return ResponseEntity.status(404).body(response);
            }
            // 返回流程定义（带版本信息）
            return getProcessDef(processId);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> saveProcess(@RequestBody Map<String, Object> processData) {
        return saveProcessDef(processData);
    }

    @DeleteMapping("/process/{processId}")
    public ResponseEntity<Map<String, Object>> deleteProcess(@PathVariable String processId) {
        return deleteProcessDef(processId);
    }

    @GetMapping("/process/tree")
    public ResponseEntity<Map<String, Object>> getProcessTree() {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> processDefs = processDefManagerService.getAllProcessDefs();
            // 构建树形结构
            List<Map<String, Object>> treeData = new ArrayList<>();
            for (Map<String, Object> processDef : processDefs) {
                Map<String, Object> node = new LinkedHashMap<>();
                node.put("id", processDef.get("processDefId"));
                node.put("name", processDef.get("name"));
                node.put("type", "process");
                node.put("description", processDef.get("description"));
                node.put("classification", processDef.get("classification"));
                node.put("accessLevel", processDef.get("accessLevel"));
                treeData.add(node);
            }
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", treeData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get process tree", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ==================== 版本管理 API ====================

    @GetMapping("/process/{processId}/versions")
    public ResponseEntity<?> getProcessVersions(@PathVariable String processId) {
        try {
            List<Map<String, Object>> versions = processDefManagerService.getProcessVersions(processId);
            return ResponseEntity.ok(versions);
        } catch (Exception e) {
            log.error("Failed to get versions for process: {}", processId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/process/{processId}/version/{version}/activate")
    public ResponseEntity<?> activateVersion(@PathVariable String processId, @PathVariable String version) {
        try {
            processDefManagerService.activateVersion(processId, version);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "版本激活成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to activate version {} for process: {}", version, processId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/process/{processId}/version/{version}/freeze")
    public ResponseEntity<?> freezeVersion(@PathVariable String processId, @PathVariable String version) {
        try {
            processDefManagerService.freezeVersion(processId, version);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "版本冻结成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to freeze version {} for process: {}", version, processId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/process/{processId}/version/{version}")
    public ResponseEntity<?> deleteVersion(@PathVariable String processId, @PathVariable String version) {
        try {
            processDefManagerService.deleteVersion(processId, version);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "版本删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to delete version {} for process: {}", version, processId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
