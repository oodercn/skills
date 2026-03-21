package net.ooder.mvp.skill.scene.llm.controller;

import net.ooder.mvp.skill.scene.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/llm-config")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LlmConfigController {

    private static final Logger log = LoggerFactory.getLogger(LlmConfigController.class);
    
    @Value("${ooder.llm.provider:qianwen}")
    private String defaultProvider;
    
    @Value("${ooder.llm.model:qwen-plus}")
    private String defaultModel;
    
    private List<Map<String, Object>> configs = new ArrayList<>();
    private Long idCounter = 1L;

    public LlmConfigController() {
    }
    
    @javax.annotation.PostConstruct
    public void init() {
        initMockData();
    }

    private void initMockData() {
        Map<String, Object> config1 = new HashMap<>();
        config1.put("id", idCounter++);
        config1.put("name", "默认企业配置");
        config1.put("level", "ENTERPRISE");
        config1.put("scopeId", "default");
        config1.put("providerType", defaultProvider != null ? defaultProvider : "qianwen");
        config1.put("model", defaultModel != null ? defaultModel : "qwen-plus");
        config1.put("providerConfig", new HashMap<String, Object>() {{
            put("apiKey", "");
            put("baseUrl", "https://dashscope.aliyuncs.com/api/v1");
        }});
        config1.put("options", new HashMap<String, Object>() {{
            put("temperature", 0.7);
            put("max_tokens", 128000);
        }});
        config1.put("enabled", true);
        config1.put("updatedAt", System.currentTimeMillis());
        configs.add(config1);
        
        log.info("[LlmConfigController] Initialized with provider: {}, model: {}", defaultProvider, defaultModel);
    }

    @GetMapping
    public ResultModel<List<Map<String, Object>>> listConfigs() {
        log.info("[LlmConfigController] List configs called");
        return ResultModel.success(configs);
    }

    @GetMapping("/{id}")
    public ResultModel<Map<String, Object>> getConfig(@PathVariable Long id) {
        log.info("[LlmConfigController] Get config called for id: {}", id);
        
        Map<String, Object> config = configs.stream()
            .filter(c -> id.equals(c.get("id")))
            .findFirst()
            .orElse(null);
        
        if (config != null) {
            return ResultModel.success(config);
        } else {
            return ResultModel.notFound("Config not found");
        }
    }

    @PostMapping
    public ResultModel<Map<String, Object>> createConfig(@RequestBody Map<String, Object> config) {
        log.info("[LlmConfigController] Create config called: {}", config);
        
        config.put("id", idCounter++);
        config.put("updatedAt", System.currentTimeMillis());
        if (!config.containsKey("enabled")) {
            config.put("enabled", true);
        }
        configs.add(config);
        
        return ResultModel.success(config);
    }

    @PutMapping("/{id}")
    public ResultModel<Map<String, Object>> updateConfig(@PathVariable Long id, @RequestBody Map<String, Object> config) {
        log.info("[LlmConfigController] Update config called for id: {}, data: {}", id, config);
        
        for (int i = 0; i < configs.size(); i++) {
            if (id.equals(configs.get(i).get("id"))) {
                config.put("id", id);
                config.put("updatedAt", System.currentTimeMillis());
                configs.set(i, config);
                return ResultModel.success(config);
            }
        }
        
        return ResultModel.notFound("Config not found");
    }

    @DeleteMapping("/{id}")
    public ResultModel<Boolean> deleteConfig(@PathVariable Long id) {
        log.info("[LlmConfigController] Delete config called for id: {}", id);
        
        boolean removed = configs.removeIf(c -> id.equals(c.get("id")));
        
        if (removed) {
            return ResultModel.success(true);
        } else {
            return ResultModel.notFound("Config not found");
        }
    }
}
