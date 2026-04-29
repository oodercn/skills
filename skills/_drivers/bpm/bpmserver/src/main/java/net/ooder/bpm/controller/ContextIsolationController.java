package net.ooder.bpm.controller;

import net.ooder.bpm.service.ContextIsolationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/context")
public class ContextIsolationController {

    private static final Logger log = LoggerFactory.getLogger(ContextIsolationController.class);

    @Autowired
    private ContextIsolationService contextIsolationService;

    @GetMapping("/isolation/{activityDefId}")
    public ResponseEntity<Map<String, Object>> getContextIsolation(@PathVariable String activityDefId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> config = contextIsolationService.getContextIsolation(activityDefId);
            if (config == null) {
                response.put("code", 404);
                response.put("message", "上下文隔离配置不存在: " + activityDefId);
                return ResponseEntity.status(404).body(response);
            }
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", config);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get context isolation: {}", activityDefId, e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/isolation")
    public ResponseEntity<Map<String, Object>> createContextIsolation(@RequestBody Map<String, Object> configData) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> created = contextIsolationService.createContextIsolation(configData);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to create context isolation", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/isolation/{contextId}")
    public ResponseEntity<Map<String, Object>> updateContextIsolation(
            @PathVariable String contextId,
            @RequestBody Map<String, Object> configData) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            configData.put("contextId", contextId);
            Map<String, Object> updated = contextIsolationService.updateContextIsolation(configData);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to update context isolation: {}", contextId, e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/isolation/{contextId}")
    public ResponseEntity<Map<String, Object>> deleteContextIsolation(@PathVariable String contextId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            contextIsolationService.deleteContextIsolation(contextId);
            response.put("code", 200);
            response.put("message", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to delete context isolation: {}", contextId, e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
