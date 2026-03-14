package net.ooder.skill.scene.config.controller;

import net.ooder.skill.scene.config.sdk.ConfigNode;
import net.ooder.skill.scene.config.service.ConfigInheritanceChain;
import net.ooder.skill.scene.config.service.ConfigLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/config")
public class ConfigController {

    private final ConfigLoaderService configLoader;

    @Autowired
    public ConfigController(ConfigLoaderService configLoader) {
        this.configLoader = configLoader;
    }

    @GetMapping("/system")
    public ResponseEntity<ConfigNode> getSystemConfig() {
        ConfigNode config = configLoader.loadSystemConfig();
        return ResponseEntity.ok(config);
    }

    @GetMapping("/system/capabilities/{address}")
    public ResponseEntity<Map<String, Object>> getCapabilityConfig(@PathVariable String address) {
        Map<String, Object> config = configLoader.getCapabilityConfig("system", "system", address);
        return ResponseEntity.ok(config);
    }

    @PutMapping("/system/capabilities/{address}")
    public ResponseEntity<Void> updateCapabilityConfig(
            @PathVariable String address,
            @RequestBody Map<String, Object> config) {
        configLoader.updateCapabilityConfig("system", "system", address, config);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/skills/{skillId}")
    public ResponseEntity<ConfigNode> getSkillConfig(
            @PathVariable String skillId,
            @RequestParam(defaultValue = "true") boolean resolveInheritance) {
        ConfigNode config = configLoader.loadSkillConfig(skillId, resolveInheritance);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/skills/{skillId}/inheritance")
    public ResponseEntity<ConfigInheritanceChain> getSkillInheritanceChain(@PathVariable String skillId) {
        ConfigInheritanceChain chain = configLoader.getInheritanceChain("skill", skillId);
        return ResponseEntity.ok(chain);
    }

    @PutMapping("/skills/{skillId}")
    public ResponseEntity<Void> updateSkillConfig(
            @PathVariable String skillId,
            @RequestBody ConfigNode config) {
        configLoader.saveConfig("skill", skillId, config);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/skills/{skillId}/keys/{key}")
    public ResponseEntity<Void> resetSkillConfig(
            @PathVariable String skillId,
            @PathVariable String key) {
        configLoader.resetConfig("skill", skillId, key);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/scenes/{sceneId}")
    public ResponseEntity<ConfigNode> getSceneConfig(
            @PathVariable String sceneId,
            @RequestParam(defaultValue = "true") boolean resolveInheritance) {
        ConfigNode config = configLoader.loadSceneConfig(sceneId, resolveInheritance);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/scenes/{sceneId}/inheritance")
    public ResponseEntity<ConfigInheritanceChain> getSceneInheritanceChain(@PathVariable String sceneId) {
        ConfigInheritanceChain chain = configLoader.getInheritanceChain("scene", sceneId);
        return ResponseEntity.ok(chain);
    }

    @GetMapping("/scenes/{sceneId}/skills/{skillId}")
    public ResponseEntity<ConfigNode> getInternalSkillConfig(
            @PathVariable String sceneId,
            @PathVariable String skillId) {
        ConfigNode config = configLoader.loadInternalSkillConfig(sceneId, skillId);
        return ResponseEntity.ok(config);
    }

    @PutMapping("/scenes/{sceneId}")
    public ResponseEntity<Void> updateSceneConfig(
            @PathVariable String sceneId,
            @RequestBody ConfigNode config) {
        configLoader.saveConfig("scene", sceneId, config);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/scenes/{sceneId}/keys/{key}")
    public ResponseEntity<Void> resetSceneConfig(
            @PathVariable String sceneId,
            @PathVariable String key) {
        configLoader.resetConfig("scene", sceneId, key);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/inheritance/{targetType}/{targetId}")
    public ResponseEntity<ConfigInheritanceChain> getInheritanceChain(
            @PathVariable String targetType,
            @PathVariable String targetId) {
        ConfigInheritanceChain chain = configLoader.getInheritanceChain(targetType, targetId);
        return ResponseEntity.ok(chain);
    }

    @PostMapping("/preview")
    public ResponseEntity<ConfigNode> previewMergedConfig(@RequestBody ConfigPreviewRequest request) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResult> validateConfig(@RequestBody ConfigValidateRequest request) {
        return ResponseEntity.ok().build();
    }

    public static class ConfigPreviewRequest {
        private String targetType;
        private String targetId;
        private ConfigNode config;

        public String getTargetType() { return targetType; }
        public void setTargetType(String targetType) { this.targetType = targetType; }
        public String getTargetId() { return targetId; }
        public void setTargetId(String targetId) { this.targetId = targetId; }
        public ConfigNode getConfig() { return config; }
        public void setConfig(ConfigNode config) { this.config = config; }
    }

    public static class ConfigValidateRequest {
        private String targetType;
        private String targetId;
        private ConfigNode config;

        public String getTargetType() { return targetType; }
        public void setTargetType(String targetType) { this.targetType = targetType; }
        public String getTargetId() { return targetId; }
        public void setTargetId(String targetId) { this.targetId = targetId; }
        public ConfigNode getConfig() { return config; }
        public void setConfig(ConfigNode config) { this.config = config; }
    }

    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;
        private List<String> warnings;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    }
}
