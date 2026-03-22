package net.ooder.mvp.skill.scene.llm.controller;

import net.ooder.mvp.skill.scene.dto.llm.LlmConfigDTO;
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
    
    private List<LlmConfigDTO> configs = new ArrayList<>();
    private Long idCounter = 1L;

    public LlmConfigController() {
    }
    
    @javax.annotation.PostConstruct
    public void init() {
        initMockData();
    }

    private void initMockData() {
        LlmConfigDTO config1 = new LlmConfigDTO();
        config1.setId(idCounter++);
        config1.setName("默认企业配置");
        config1.setLevel("ENTERPRISE");
        config1.setScopeId("default");
        config1.setProviderType(defaultProvider != null ? defaultProvider : "qianwen");
        config1.setModel(defaultModel != null ? defaultModel : "qwen-plus");
        
        Map<String, Object> providerConfig = new HashMap<>();
        providerConfig.put("apiKey", "");
        providerConfig.put("baseUrl", "https://dashscope.aliyuncs.com/api/v1");
        config1.setProviderConfig(providerConfig);
        
        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.7);
        options.put("max_tokens", 128000);
        config1.setOptions(options);
        
        config1.setEnabled(true);
        config1.setUpdatedAt(System.currentTimeMillis());
        configs.add(config1);
        
        log.info("[LlmConfigController] Initialized with provider: {}, model: {}", defaultProvider, defaultModel);
    }

    @GetMapping
    public ResultModel<List<LlmConfigDTO>> listConfigs() {
        log.info("[LlmConfigController] List configs called");
        return ResultModel.success(configs);
    }

    @GetMapping("/{id}")
    public ResultModel<LlmConfigDTO> getConfig(@PathVariable Long id) {
        log.info("[LlmConfigController] Get config called for id: {}", id);
        
        LlmConfigDTO config = configs.stream()
            .filter(c -> id.equals(c.getId()))
            .findFirst()
            .orElse(null);
        
        if (config != null) {
            return ResultModel.success(config);
        } else {
            return ResultModel.notFound("Config not found");
        }
    }

    @PostMapping
    public ResultModel<LlmConfigDTO> createConfig(@RequestBody LlmConfigDTO config) {
        log.info("[LlmConfigController] Create config called: {}", config);
        
        config.setId(idCounter++);
        config.setUpdatedAt(System.currentTimeMillis());
        if (config.getEnabled() == null) {
            config.setEnabled(true);
        }
        configs.add(config);
        
        return ResultModel.success(config);
    }

    @PutMapping("/{id}")
    public ResultModel<LlmConfigDTO> updateConfig(@PathVariable Long id, @RequestBody LlmConfigDTO config) {
        log.info("[LlmConfigController] Update config called for id: {}, data: {}", id, config);
        
        for (int i = 0; i < configs.size(); i++) {
            if (id.equals(configs.get(i).getId())) {
                config.setId(id);
                config.setUpdatedAt(System.currentTimeMillis());
                configs.set(i, config);
                return ResultModel.success(config);
            }
        }
        
        return ResultModel.notFound("Config not found");
    }

    @DeleteMapping("/{id}")
    public ResultModel<Boolean> deleteConfig(@PathVariable Long id) {
        log.info("[LlmConfigController] Delete config called for id: {}", id);
        
        boolean removed = configs.removeIf(c -> id.equals(c.getId()));
        
        if (removed) {
            return ResultModel.success(true);
        } else {
            return ResultModel.notFound("Config not found");
        }
    }
}
