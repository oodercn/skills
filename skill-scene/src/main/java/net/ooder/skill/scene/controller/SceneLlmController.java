package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/scene-groups/{sceneGroupId}/llm")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SceneLlmController {

    private static final Logger log = LoggerFactory.getLogger(SceneLlmController.class);
    
    private Map<String, LlmConfig> llmConfigs = new HashMap<String, LlmConfig>();
    
    public SceneLlmController() {
        initMockData();
    }
    
    private void initMockData() {
        LlmConfig config = new LlmConfig();
        config.setProvider("deepseek");
        config.setModel("deepseek-chat");
        config.setDecisionMode("ONLINE_FIRST");
        config.setDecisionTimeout(30000);
        config.setDecisionCache(true);
        config.setCacheTtl(300000);
        config.setFunctionCalling(true);
        config.setMaxIterations(5);
        config.setLlmTimeout(60000);
        config.setDailyTokenLimit(100000);
        config.setUsedTokens(12500);
        
        llmConfigs.put("sg-1772887335550", config);
    }

    @GetMapping("/config")
    public ResultModel<LlmConfig> getLlmConfig(@PathVariable String sceneGroupId) {
        log.info("[getLlmConfig] sceneGroupId: {}", sceneGroupId);
        
        LlmConfig config = llmConfigs.get(sceneGroupId);
        if (config == null) {
            config = new LlmConfig();
        }
        
        return ResultModel.success(config);
    }
    
    @PutMapping("/config")
    public ResultModel<LlmConfig> updateLlmConfig(
            @PathVariable String sceneGroupId,
            @RequestBody LlmConfig config) {
        log.info("[updateLlmConfig] sceneGroupId: {}, provider: {}, model: {}", sceneGroupId, config.getProvider(), config.getModel());
        
        llmConfigs.put(sceneGroupId, config);
        
        return ResultModel.success(config);
    }
    
    @GetMapping("/stats")
    public ResultModel<LlmStats> getLlmStats(@PathVariable String sceneGroupId) {
        log.info("[getLlmStats] sceneGroupId: {}", sceneGroupId);
        
        LlmConfig config = llmConfigs.get(sceneGroupId);
        LlmStats stats = new LlmStats();
        
        if (config != null) {
            stats.setDailyTokenLimit(config.getDailyTokenLimit());
            stats.setUsedTokens(config.getUsedTokens());
            stats.setRemainingTokens(config.getDailyTokenLimit() - config.getUsedTokens());
        } else {
            stats.setDailyTokenLimit(100000);
            stats.setUsedTokens(0);
            stats.setRemainingTokens(100000);
        }
        
        return ResultModel.success(stats);
    }
    
    @PostMapping("/reset-tokens")
    public ResultModel<Boolean> resetDailyTokens(@PathVariable String sceneGroupId) {
        log.info("[resetDailyTokens] sceneGroupId: {}", sceneGroupId);
        
        LlmConfig config = llmConfigs.get(sceneGroupId);
        if (config != null) {
            config.setUsedTokens(0);
        }
        
        return ResultModel.success(true);
    }
    
    public static class LlmConfig {
        private String provider = "deepseek";
        private String model = "deepseek-chat";
        private String decisionMode = "ONLINE_FIRST";
        private int decisionTimeout = 30000;
        private boolean decisionCache = true;
        private int cacheTtl = 300000;
        private boolean functionCalling = true;
        private int maxIterations = 5;
        private int llmTimeout = 60000;
        private int dailyTokenLimit = 100000;
        private int usedTokens = 0;
        
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getDecisionMode() { return decisionMode; }
        public void setDecisionMode(String decisionMode) { this.decisionMode = decisionMode; }
        public int getDecisionTimeout() { return decisionTimeout; }
        public void setDecisionTimeout(int decisionTimeout) { this.decisionTimeout = decisionTimeout; }
        public boolean isDecisionCache() { return decisionCache; }
        public void setDecisionCache(boolean decisionCache) { this.decisionCache = decisionCache; }
        public int getCacheTtl() { return cacheTtl; }
        public void setCacheTtl(int cacheTtl) { this.cacheTtl = cacheTtl; }
        public boolean isFunctionCalling() { return functionCalling; }
        public void setFunctionCalling(boolean functionCalling) { this.functionCalling = functionCalling; }
        public int getMaxIterations() { return maxIterations; }
        public void setMaxIterations(int maxIterations) { this.maxIterations = maxIterations; }
        public int getLlmTimeout() { return llmTimeout; }
        public void setLlmTimeout(int llmTimeout) { this.llmTimeout = llmTimeout; }
        public int getDailyTokenLimit() { return dailyTokenLimit; }
        public void setDailyTokenLimit(int dailyTokenLimit) { this.dailyTokenLimit = dailyTokenLimit; }
        public int getUsedTokens() { return usedTokens; }
        public void setUsedTokens(int usedTokens) { this.usedTokens = usedTokens; }
    }
    
    public static class LlmStats {
        private int dailyTokenLimit;
        private int usedTokens;
        private int remainingTokens;
        
        public int getDailyTokenLimit() { return dailyTokenLimit; }
        public void setDailyTokenLimit(int dailyTokenLimit) { this.dailyTokenLimit = dailyTokenLimit; }
        public int getUsedTokens() { return usedTokens; }
        public void setUsedTokens(int usedTokens) { this.usedTokens = usedTokens; }
        public int getRemainingTokens() { return remainingTokens; }
        public void setRemainingTokens(int remainingTokens) { this.remainingTokens = remainingTokens; }
    }
}
