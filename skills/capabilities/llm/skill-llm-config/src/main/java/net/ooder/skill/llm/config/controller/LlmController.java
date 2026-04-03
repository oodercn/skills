package net.ooder.skill.llm.config.controller;

import net.ooder.skill.llm.config.dto.LlmConfigDTO;
import net.ooder.skill.llm.config.model.ResultModel;
import net.ooder.skill.llm.config.service.LlmConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController("llmConfigLlmController")
@RequestMapping("/api/v1/llm-config/provider")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LlmController {

    private static final Logger log = LoggerFactory.getLogger(LlmController.class);

    @Value("${ooder.llm.provider:qianwen}")
    private String defaultProvider;

    @Value("${ooder.llm.model:qwen-plus}")
    private String defaultModel;

    @Autowired
    private LlmConfigService llmConfigService;

    private String currentProvider;
    private String currentModel;

    public LlmController() {
        this.currentProvider = "qianwen";
        this.currentModel = "qwen-plus";
    }

    @PostMapping("/models/set")
    public ResultModel<Map<String, Object>> setModel(@RequestBody SetModelRequest request) {
        log.info("[LlmController] Set model called - provider: {}, modelId: {}", request.getProvider(), request.getModelId());

        try {
            this.currentProvider = request.getProvider();
            this.currentModel = request.getModelId();

            LlmConfigDTO config = llmConfigService.getEffectiveConfig(
                "SYSTEM", "default", null, null, null);

            if (config != null) {
                config.setProviderType(request.getProvider());
                config.setModel(request.getModelId());
                config.setUpdatedAt(System.currentTimeMillis());
                llmConfigService.updateConfig(config.getId(), config, "system");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("provider", currentProvider);
            result.put("modelId", currentModel);
            result.put("message", "Model set successfully");

            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("[LlmController] Failed to set model", e);
            return ResultModel.error("Failed to set model: " + e.getMessage());
        }
    }

    @GetMapping("/models/current")
    public ResultModel<Map<String, Object>> getCurrentModel() {
        log.info("[LlmController] Get current model called");

        Map<String, Object> result = new HashMap<>();
        result.put("provider", currentProvider != null ? currentProvider : defaultProvider);
        result.put("modelId", currentModel != null ? currentModel : defaultModel);

        return ResultModel.success(result);
    }

    @GetMapping("/chat")
    public ResultModel<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        log.info("[LlmController] Chat called");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Chat endpoint - please use skill-llm-chat for actual chat functionality");
        response.put("placeholder", true);

        return ResultModel.success(response);
    }

    @GetMapping("/tools")
    public ResultModel<Map<String, Object>> getTools() {
        log.info("[LlmController] Get tools called");

        Map<String, Object> result = new HashMap<>();
        result.put("tools", new java.util.ArrayList<>());
        result.put("message", "Tools endpoint - please use skill-llm-chat for actual tools");

        return ResultModel.success(result);
    }

    @GetMapping("/tools/{name}")
    public ResultModel<Map<String, Object>> getToolByName(@PathVariable String name) {
        log.info("[LlmController] Get tool by name: {}", name);

        Map<String, Object> tool = new HashMap<>();
        tool.put("name", name);
        tool.put("type", "function");
        tool.put("description", "Function: " + name);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        parameters.put("properties", new HashMap<>());
        parameters.put("required", new java.util.ArrayList<>());
        tool.put("parameters", parameters);
        
        tool.put("createdAt", System.currentTimeMillis());
        tool.put("updatedAt", System.currentTimeMillis());

        return ResultModel.success(tool);
    }

    @GetMapping("/docs")
    public ResultModel<Map<String, Object>> getDocs() {
        log.info("[LlmController] Get docs called");

        Map<String, Object> result = new HashMap<>();
        result.put("docs", new java.util.ArrayList<>());

        return ResultModel.success(result);
    }

    public static class SetModelRequest {
        private String provider;
        private String modelId;

        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }

        public String getModelId() { return modelId; }
        public void setModelId(String modelId) { this.modelId = modelId; }
    }
}