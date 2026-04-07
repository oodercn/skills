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
    
    private Map<String, Object> formatProcessDef(Map<String, Object> fullProcessDef) {
        Map<String, Object> processDef = (Map<String, Object>) fullProcessDef.get("processDef");
        Map<String, Object> activeVersion = (Map<String, Object>) fullProcessDef.get("activeVersion");
        List<Map<String, Object>> activities = (List<Map<String, Object>>) fullProcessDef.get("activities");
        List<Map<String, Object>> routes = (List<Map<String, Object>>) fullProcessDef.get("routes");
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("processDefId", processDef.get("processDefId"));
        result.put("name", processDef.get("name"));
        result.put("description", processDef.get("description"));
        result.put("category", processDef.get("classification"));
        result.put("accessLevel", processDef.get("accessLevel"));
        
        if (activeVersion != null) {
            result.put("version", activeVersion.get("version"));
            result.put("status", activeVersion.get("state"));
        }
        
        List<Map<String, Object>> formattedActivities = new ArrayList<>();
        if (activities != null) {
            for (Map<String, Object> activity : activities) {
                Map<String, Object> formattedActivity = new LinkedHashMap<>();
                formattedActivity.put("activityDefId", activity.get("activityDefId"));
                formattedActivity.put("name", activity.get("name"));
                formattedActivity.put("description", activity.get("description"));
                
                String position = (String) activity.get("position");
                if ("POSITION_START".equals(position)) {
                    formattedActivity.put("position", "START");
                    formattedActivity.put("activityType", "START");
                } else if ("POSITION_END".equals(position)) {
                    formattedActivity.put("position", "END");
                    formattedActivity.put("activityType", "END");
                } else {
                    formattedActivity.put("position", "NORMAL");
                    formattedActivity.put("activityType", "TASK");
                    formattedActivity.put("activityCategory", "HUMAN");
                }
                
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
}
