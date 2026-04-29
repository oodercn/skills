package net.ooder.bpm.controller;

import net.ooder.bpm.service.SceneConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/scene")
public class SceneConfigController {

    private static final Logger log = LoggerFactory.getLogger(SceneConfigController.class);

    @Autowired
    private SceneConfigService sceneConfigService;

    @GetMapping("/config/{processDefId}")
    public ResponseEntity<Map<String, Object>> getSceneConfig(@PathVariable String processDefId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> config = sceneConfigService.getSceneConfig(processDefId);
            if (config == null) {
                response.put("code", 404);
                response.put("message", "场景配置不存在: " + processDefId);
                return ResponseEntity.status(404).body(response);
            }
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", config);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get scene config: {}", processDefId, e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/config")
    public ResponseEntity<Map<String, Object>> createSceneConfig(@RequestBody Map<String, Object> configData) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> created = sceneConfigService.createSceneConfig(configData);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to create scene config", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/config/{sceneConfigId}")
    public ResponseEntity<Map<String, Object>> updateSceneConfig(
            @PathVariable String sceneConfigId,
            @RequestBody Map<String, Object> configData) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            configData.put("sceneConfigId", sceneConfigId);
            Map<String, Object> updated = sceneConfigService.updateSceneConfig(configData);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to update scene config: {}", sceneConfigId, e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/config/{sceneConfigId}")
    public ResponseEntity<Map<String, Object>> deleteSceneConfig(@PathVariable String sceneConfigId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            sceneConfigService.deleteSceneConfig(sceneConfigId);
            response.put("code", 200);
            response.put("message", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to delete scene config: {}", sceneConfigId, e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
