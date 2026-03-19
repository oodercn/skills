package net.ooder.mvp.skill.scene.llm.controller;

import net.ooder.mvp.skill.scene.llm.model.LlmConfig;
import net.ooder.mvp.skill.scene.llm.service.LlmConfigService;
import net.ooder.mvp.skill.scene.model.ResultModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/llm-config")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class LlmConfigController {
    
    private static final Logger log = LoggerFactory.getLogger(LlmConfigController.class);
    
    @Autowired
    private LlmConfigService configService;

    @PostMapping
    public ResultModel<LlmConfig> createConfig(@RequestBody LlmConfig config) {
        log.info("Create config request: level={}, provider={}", config.getLevel(), config.getProviderType());
        
        try {
            if (!configService.validateConfig(config)) {
                return ResultModel.badRequest("Invalid config: missing required fields");
            }
            
            LlmConfig created = configService.createConfig(config);
            return ResultModel.success("Config created successfully", created);
            
        } catch (Exception e) {
            log.error("Failed to create config", e);
            return ResultModel.error("Failed to create config: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResultModel<LlmConfig> updateConfig(@PathVariable String id, @RequestBody LlmConfig config) {
        log.info("Update config request: id={}", id);
        
        try {
            LlmConfig updated = configService.updateConfig(id, config);
            return ResultModel.success("Config updated successfully", updated);
            
        } catch (Exception e) {
            log.error("Failed to update config", e);
            return ResultModel.error("Failed to update config: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResultModel<Boolean> deleteConfig(@PathVariable String id) {
        log.info("Delete config request: id={}", id);
        
        try {
            configService.deleteConfig(id);
            return ResultModel.success("Config deleted successfully", true);
            
        } catch (Exception e) {
            log.error("Failed to delete config", e);
            return ResultModel.error("Failed to delete config: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResultModel<LlmConfig> getConfig(@PathVariable String id) {
        try {
            LlmConfig config = configService.getConfig(id);
            if (config == null) {
                return ResultModel.notFound("Config not found");
            }
            return ResultModel.success(config);
        } catch (Exception e) {
            log.error("Failed to get config", e);
            return ResultModel.error("Failed to get config: " + e.getMessage());
        }
    }
    
    @GetMapping("/level/{level}")
    public ResultModel<Map<String, Object>> getConfigsByLevel(@PathVariable String level) {
        try {
            LlmConfig.ConfigLevel configLevel = LlmConfig.ConfigLevel.valueOf(level.toUpperCase());
            List<LlmConfig> configs = configService.getConfigsByLevel(configLevel);
            
            Map<String, Object> data = new HashMap<>();
            data.put("configs", configs);
            data.put("total", configs.size());
            
            return ResultModel.success(data);
            
        } catch (Exception e) {
            log.error("Failed to get configs by level", e);
            return ResultModel.error("Failed to get configs: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResultModel<Map<String, Object>> getAllConfigs() {
        try {
            List<LlmConfig> configs = configService.getAllConfigs();
            
            Map<String, Object> data = new HashMap<>();
            data.put("configs", configs);
            data.put("total", configs.size());
            
            return ResultModel.success(data);
            
        } catch (Exception e) {
            log.error("Failed to get all configs", e);
            return ResultModel.error("Failed to get configs: " + e.getMessage());
        }
    }
    
    @PostMapping("/resolve")
    public ResultModel<LlmConfig> resolveConfig(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        String sceneId = (String) request.get("sceneId");
        String departmentId = (String) request.get("departmentId");
        
        log.info("Resolve config request: userId={}, sceneId={}, departmentId={}", userId, sceneId, departmentId);
        
        try {
            LlmConfig resolved = configService.resolveConfig(userId, sceneId, departmentId, request);
            
            if (resolved == null) {
                return ResultModel.notFound("No available config found");
            }
            
            return ResultModel.success(resolved);
            
        } catch (Exception e) {
            log.error("Failed to resolve config", e);
            return ResultModel.error("Failed to resolve config: " + e.getMessage());
        }
    }
    
    @GetMapping("/enterprise")
    public ResultModel<LlmConfig> getEnterpriseConfig() {
        try {
            LlmConfig config = configService.getConfigByLevelAndScope(LlmConfig.ConfigLevel.ENTERPRISE, "default");
            return ResultModel.success(config);
            
        } catch (Exception e) {
            log.error("Failed to get enterprise config", e);
            return ResultModel.error("Failed to get config: " + e.getMessage());
        }
    }
    
    @GetMapping("/personal/{userId}")
    public ResultModel<LlmConfig> getPersonalConfig(@PathVariable String userId) {
        try {
            LlmConfig config = configService.getConfigByLevelAndScope(LlmConfig.ConfigLevel.PERSONAL, userId);
            return ResultModel.success(config);
            
        } catch (Exception e) {
            log.error("Failed to get personal config", e);
            return ResultModel.error("Failed to get config: " + e.getMessage());
        }
    }
    
    @GetMapping("/scene/{sceneId}")
    public ResultModel<LlmConfig> getSceneConfig(@PathVariable String sceneId) {
        try {
            LlmConfig config = configService.getConfigByLevelAndScope(LlmConfig.ConfigLevel.SCENE, sceneId);
            return ResultModel.success(config);
            
        } catch (Exception e) {
            log.error("Failed to get scene config", e);
            return ResultModel.error("Failed to get config: " + e.getMessage());
        }
    }
}
