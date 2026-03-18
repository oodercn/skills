package net.ooder.mvp.skill.scene.controller;

import net.ooder.mvp.skill.scene.dto.llm.LlmProviderConfigDTO;
import net.ooder.mvp.skill.scene.llm.DeepSeekLlmProvider;
import net.ooder.mvp.skill.scene.llm.BaiduLlmProvider;
import net.ooder.mvp.skill.scene.llm.AliyunBailianLlmProvider;
import net.ooder.mvp.skill.scene.model.ResultModel;
import net.ooder.scene.skill.LlmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/llm")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LlmProviderController {

    private static final Logger log = LoggerFactory.getLogger(LlmProviderController.class);
    
    @Value("${ooder.llm.deepseek.api-key:}")
    private String deepseekApiKey;
    
    @Value("${ooder.llm.baidu.api-key:}")
    private String baiduApiKey;
    
    @Value("${ooder.llm.baidu.secret-key:}")
    private String baiduSecretKey;
    
    @Value("${ooder.llm.qianwen.api-key:}")
    private String qianwenApiKey;
    
    @Value("${ooder.llm.provider:}")
    private String defaultProvider;
    
    @Value("${ooder.llm.model:}")
    private String defaultModel;
    
    private Map<String, LlmProviderConfigDTO> providers = new HashMap<String, LlmProviderConfigDTO>();
    
    public LlmProviderController() {
    }
    
    @javax.annotation.PostConstruct
    public void init() {
        initProviders();
    }
    
    private void initProviders() {
        LlmProviderConfigDTO qianwen = new LlmProviderConfigDTO();
        qianwen.setProviderId("qianwen");
        qianwen.setName("阿里云百炼");
        qianwen.setType("qianwen");
        qianwen.setEnabled("qianwen".equals(defaultProvider) || defaultProvider == null || defaultProvider.isEmpty());
        qianwen.setDefaultModel(defaultModel != null && !defaultModel.isEmpty() ? defaultModel : "qwen-plus");
        qianwen.setTimeout(60000);
        qianwen.setMaxRetries(3);
        qianwen.setFunctionCallingEnabled(true);
        qianwen.setMaxIterations(5);
        qianwen.setCreateTime(System.currentTimeMillis() - 86400000L * 30);
        qianwen.setUpdateTime(System.currentTimeMillis());
        qianwen.setConfigured(qianwenApiKey != null && !qianwenApiKey.isEmpty());
        
        List<LlmProviderConfigDTO.ModelConfigDTO> qianwenModels = new ArrayList<LlmProviderConfigDTO.ModelConfigDTO>();
        LlmProviderConfigDTO.ModelConfigDTO qwenPlus = new LlmProviderConfigDTO.ModelConfigDTO();
        qwenPlus.setModelId("qwen-plus");
        qwenPlus.setDisplayName("通义千问 Plus");
        qwenPlus.setMaxTokens(128000);
        qwenPlus.setTemperature(0.7);
        qwenPlus.setSupportsFunctionCalling(true);
        qwenPlus.setSupportsMultimodal(true);
        qwenPlus.setSupportsEmbedding(true);
        qwenPlus.setCostPer1kTokens(0.004);
        qianwenModels.add(qwenPlus);
        
        LlmProviderConfigDTO.ModelConfigDTO qwenTurbo = new LlmProviderConfigDTO.ModelConfigDTO();
        qwenTurbo.setModelId("qwen-turbo");
        qwenTurbo.setDisplayName("通义千问 Turbo");
        qwenTurbo.setMaxTokens(8000);
        qwenTurbo.setTemperature(0.7);
        qwenTurbo.setSupportsFunctionCalling(true);
        qwenTurbo.setSupportsMultimodal(false);
        qwenTurbo.setSupportsEmbedding(false);
        qwenTurbo.setCostPer1kTokens(0.002);
        qianwenModels.add(qwenTurbo);
        
        LlmProviderConfigDTO.ModelConfigDTO qwenMax = new LlmProviderConfigDTO.ModelConfigDTO();
        qwenMax.setModelId("qwen-max");
        qwenMax.setDisplayName("通义千问 Max");
        qwenMax.setMaxTokens(32000);
        qwenMax.setTemperature(0.7);
        qwenMax.setSupportsFunctionCalling(true);
        qwenMax.setSupportsMultimodal(true);
        qwenMax.setSupportsEmbedding(true);
        qwenMax.setCostPer1kTokens(0.02);
        qianwenModels.add(qwenMax);
        
        qianwen.setModels(qianwenModels);
        providers.put(qianwen.getProviderId(), qianwen);
        
        LlmProviderConfigDTO deepseek = new LlmProviderConfigDTO();
        deepseek.setProviderId("deepseek");
        deepseek.setName("DeepSeek");
        deepseek.setType("deepseek");
        deepseek.setEnabled("deepseek".equals(defaultProvider));
        deepseek.setDefaultModel("deepseek-chat");
        deepseek.setTimeout(60000);
        deepseek.setMaxRetries(3);
        deepseek.setFunctionCallingEnabled(true);
        deepseek.setMaxIterations(5);
        deepseek.setCreateTime(System.currentTimeMillis() - 86400000L * 30);
        deepseek.setUpdateTime(System.currentTimeMillis() - 86400000L);
        deepseek.setConfigured(deepseekApiKey != null && !deepseekApiKey.isEmpty());
        
        List<LlmProviderConfigDTO.ModelConfigDTO> deepseekModels = new ArrayList<LlmProviderConfigDTO.ModelConfigDTO>();
        LlmProviderConfigDTO.ModelConfigDTO dsChat = new LlmProviderConfigDTO.ModelConfigDTO();
        dsChat.setModelId("deepseek-chat");
        dsChat.setDisplayName("DeepSeek Chat");
        dsChat.setMaxTokens(64000);
        dsChat.setTemperature(0.7);
        dsChat.setSupportsFunctionCalling(true);
        dsChat.setSupportsMultimodal(false);
        dsChat.setSupportsEmbedding(false);
        dsChat.setCostPer1kTokens(0.001);
        deepseekModels.add(dsChat);
        
        LlmProviderConfigDTO.ModelConfigDTO dsCoder = new LlmProviderConfigDTO.ModelConfigDTO();
        dsCoder.setModelId("deepseek-coder");
        dsCoder.setDisplayName("DeepSeek Coder");
        dsCoder.setMaxTokens(16000);
        dsCoder.setTemperature(0.3);
        dsCoder.setSupportsFunctionCalling(true);
        dsCoder.setSupportsMultimodal(false);
        dsCoder.setSupportsEmbedding(false);
        dsCoder.setCostPer1kTokens(0.001);
        deepseekModels.add(dsCoder);
        
        deepseek.setModels(deepseekModels);
        providers.put(deepseek.getProviderId(), deepseek);
        
        LlmProviderConfigDTO baidu = new LlmProviderConfigDTO();
        baidu.setProviderId("baidu");
        baidu.setName("百度文心");
        baidu.setType("baidu");
        baidu.setEnabled("baidu".equals(defaultProvider));
        baidu.setDefaultModel("ernie-bot-4");
        baidu.setTimeout(60000);
        baidu.setMaxRetries(3);
        baidu.setFunctionCallingEnabled(true);
        baidu.setMaxIterations(5);
        baidu.setCreateTime(System.currentTimeMillis() - 86400000L * 20);
        baidu.setUpdateTime(System.currentTimeMillis() - 86400000L * 2);
        baidu.setConfigured(baiduApiKey != null && !baiduApiKey.isEmpty() && baiduSecretKey != null && !baiduSecretKey.isEmpty());
        
        List<LlmProviderConfigDTO.ModelConfigDTO> baiduModels = new ArrayList<LlmProviderConfigDTO.ModelConfigDTO>();
        LlmProviderConfigDTO.ModelConfigDTO ernie4 = new LlmProviderConfigDTO.ModelConfigDTO();
        ernie4.setModelId("ernie-bot-4");
        ernie4.setDisplayName("ERNIE Bot 4.0");
        ernie4.setMaxTokens(8000);
        ernie4.setTemperature(0.7);
        ernie4.setSupportsFunctionCalling(true);
        ernie4.setSupportsMultimodal(false);
        ernie4.setSupportsEmbedding(false);
        ernie4.setCostPer1kTokens(0.12);
        baiduModels.add(ernie4);
        
        LlmProviderConfigDTO.ModelConfigDTO ernieTurbo = new LlmProviderConfigDTO.ModelConfigDTO();
        ernieTurbo.setModelId("ernie-bot-turbo");
        ernieTurbo.setDisplayName("ERNIE Bot Turbo");
        ernieTurbo.setMaxTokens(4000);
        ernieTurbo.setTemperature(0.7);
        ernieTurbo.setSupportsFunctionCalling(false);
        ernieTurbo.setSupportsMultimodal(false);
        ernieTurbo.setSupportsEmbedding(false);
        ernieTurbo.setCostPer1kTokens(0.008);
        baiduModels.add(ernieTurbo);
        
        baidu.setModels(baiduModels);
        providers.put(baidu.getProviderId(), baidu);
        
        LlmProviderConfigDTO openai = new LlmProviderConfigDTO();
        openai.setProviderId("openai");
        openai.setName("OpenAI");
        openai.setType("openai");
        openai.setEnabled(false);
        openai.setDefaultModel("gpt-4-turbo");
        openai.setTimeout(60000);
        openai.setMaxRetries(3);
        openai.setFunctionCallingEnabled(true);
        openai.setMaxIterations(5);
        openai.setCreateTime(System.currentTimeMillis() - 86400000L * 10);
        openai.setUpdateTime(System.currentTimeMillis() - 86400000L * 5);
        openai.setConfigured(false);
        
        List<LlmProviderConfigDTO.ModelConfigDTO> openaiModels = new ArrayList<LlmProviderConfigDTO.ModelConfigDTO>();
        LlmProviderConfigDTO.ModelConfigDTO gpt4 = new LlmProviderConfigDTO.ModelConfigDTO();
        gpt4.setModelId("gpt-4-turbo");
        gpt4.setDisplayName("GPT-4 Turbo");
        gpt4.setMaxTokens(128000);
        gpt4.setTemperature(0.7);
        gpt4.setSupportsFunctionCalling(true);
        gpt4.setSupportsMultimodal(true);
        gpt4.setSupportsEmbedding(false);
        gpt4.setCostPer1kTokens(0.01);
        openaiModels.add(gpt4);
        
        LlmProviderConfigDTO.ModelConfigDTO gpt35 = new LlmProviderConfigDTO.ModelConfigDTO();
        gpt35.setModelId("gpt-3.5-turbo");
        gpt35.setDisplayName("GPT-3.5 Turbo");
        gpt35.setMaxTokens(16000);
        gpt35.setTemperature(0.7);
        gpt35.setSupportsFunctionCalling(true);
        gpt35.setSupportsMultimodal(false);
        gpt35.setSupportsEmbedding(false);
        gpt35.setCostPer1kTokens(0.0005);
        openaiModels.add(gpt35);
        
        openai.setModels(openaiModels);
        providers.put(openai.getProviderId(), openai);
        
        log.info("[initProviders] Initialized {} providers, default: {}, model: {}", 
                providers.size(), defaultProvider, defaultModel);
    }

    @GetMapping("/providers")
    public ResultModel<List<LlmProviderConfigDTO>> listProviders() {
        log.info("[listProviders] request start");
        return ResultModel.success(new ArrayList<LlmProviderConfigDTO>(providers.values()));
    }
    
    @GetMapping("/providers/{providerId}")
    public ResultModel<LlmProviderConfigDTO> getProvider(@PathVariable String providerId) {
        log.info("[getProvider] providerId: {}", providerId);
        LlmProviderConfigDTO provider = providers.get(providerId);
        if (provider == null) {
            return ResultModel.notFound("Provider not found: " + providerId);
        }
        return ResultModel.success(provider);
    }
    
    @PostMapping("/providers")
    public ResultModel<LlmProviderConfigDTO> createProvider(@RequestBody LlmProviderConfigDTO request) {
        log.info("[createProvider] name: {}", request.getName());
        
        String providerId = "provider-" + System.currentTimeMillis();
        request.setProviderId(providerId);
        request.setCreateTime(System.currentTimeMillis());
        request.setUpdateTime(System.currentTimeMillis());
        
        providers.put(providerId, request);
        
        return ResultModel.success(request);
    }
    
    @PutMapping("/providers/{providerId}")
    public ResultModel<LlmProviderConfigDTO> updateProvider(@PathVariable String providerId, @RequestBody LlmProviderConfigDTO request) {
        log.info("[updateProvider] providerId: {}", providerId);
        
        LlmProviderConfigDTO existing = providers.get(providerId);
        if (existing == null) {
            return ResultModel.notFound("Provider not found: " + providerId);
        }
        
        request.setProviderId(providerId);
        request.setUpdateTime(System.currentTimeMillis());
        request.setCreateTime(existing.getCreateTime());
        providers.put(providerId, request);
        
        return ResultModel.success(request);
    }
    
    @DeleteMapping("/providers/{providerId}")
    public ResultModel<Boolean> deleteProvider(@PathVariable String providerId) {
        log.info("[deleteProvider] providerId: {}", providerId);
        
        LlmProviderConfigDTO removed = providers.remove(providerId);
        if (removed == null) {
            return ResultModel.notFound("Provider not found: " + providerId);
        }
        
        return ResultModel.success(true);
    }
    
    @PostMapping("/providers/{providerId}/test")
    public ResultModel<Map<String, Object>> testProvider(@PathVariable String providerId) {
        log.info("[testProvider] providerId: {}", providerId);
        
        LlmProviderConfigDTO providerConfig = providers.get(providerId);
        if (providerConfig == null) {
            return ResultModel.notFound("Provider not found: " + providerId);
        }
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("providerId", providerId);
        result.put("providerName", providerConfig.getName());
        
        try {
            LlmProvider provider = createProviderInstance(providerConfig);
            
            if (provider == null) {
                result.put("success", false);
                result.put("message", "无法创建提供者实例，请检查API Key配置");
                return ResultModel.success(result);
            }
            
            String testModel = providerConfig.getDefaultModel();
            if (testModel == null || testModel.isEmpty()) {
                List<String> supportedModels = provider.getSupportedModels();
                if (!supportedModels.isEmpty()) {
                    testModel = supportedModels.get(0);
                }
            }
            
            if (testModel == null) {
                testModel = "default";
            }
            
            List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>();
            Map<String, Object> userMessage = new HashMap<String, Object>();
            userMessage.put("role", "user");
            userMessage.put("content", "Hello, this is a connection test. Please respond with 'OK'.");
            messages.add(userMessage);
            
            long startTime = System.currentTimeMillis();
            Map<String, Object> chatResult = provider.chat(testModel, messages, null);
            long elapsed = System.currentTimeMillis() - startTime;
            
            String responseContent = (String) chatResult.get("content");
            boolean hasError = chatResult.containsKey("error") && Boolean.TRUE.equals(chatResult.get("error"));
            
            if (hasError) {
                result.put("success", false);
                result.put("message", "连接失败: " + responseContent);
                result.put("elapsed", elapsed);
            } else {
                result.put("success", true);
                result.put("message", "连接成功");
                result.put("model", testModel);
                result.put("elapsed", elapsed);
                result.put("response", responseContent != null && responseContent.length() > 100 
                    ? responseContent.substring(0, 100) + "..." 
                    : responseContent);
            }
            
        } catch (Exception e) {
            log.error("[testProvider] Test failed for provider: {}", providerId, e);
            result.put("success", false);
            result.put("message", "连接测试失败: " + e.getMessage());
        }
        
        return ResultModel.success(result);
    }
    
    private LlmProvider createProviderInstance(LlmProviderConfigDTO config) {
        String type = config.getType();
        
        if ("deepseek".equals(type)) {
            DeepSeekLlmProvider provider = new DeepSeekLlmProvider();
            String apiKey = getApiKeyFromConfig(config, "deepseek");
            if (apiKey == null || apiKey.isEmpty()) {
                return null;
            }
            provider.setApiKey(apiKey);
            return provider;
        } else if ("baidu".equals(type)) {
            BaiduLlmProvider provider = new BaiduLlmProvider();
            String apiKey = getApiKeyFromConfig(config, "baidu");
            String secretKey = getSecretKeyFromConfig(config);
            if (apiKey == null || apiKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
                return null;
            }
            provider.setAccessKey(apiKey);
            provider.setSecretKey(secretKey);
            return provider;
        } else if ("qianwen".equals(type) || "aliyun-bailian".equals(type)) {
            AliyunBailianLlmProvider provider = new AliyunBailianLlmProvider();
            String apiKey = getApiKeyFromConfig(config, "qianwen");
            if (apiKey == null || apiKey.isEmpty()) {
                return null;
            }
            provider.setApiKey(apiKey);
            return provider;
        }
        
        log.warn("[createProviderInstance] Unknown provider type: {}", type);
        return null;
    }
    
    private String getApiKeyFromConfig(LlmProviderConfigDTO config, String defaultKey) {
        Map<String, Object> providerConfig = config.getProviderConfig();
        if (providerConfig != null && providerConfig.containsKey("apiKey")) {
            return (String) providerConfig.get("apiKey");
        }
        
        if ("deepseek".equals(defaultKey) && deepseekApiKey != null && !deepseekApiKey.isEmpty()) {
            return deepseekApiKey;
        }
        if ("baidu".equals(defaultKey) && baiduApiKey != null && !baiduApiKey.isEmpty()) {
            return baiduApiKey;
        }
        if ("qianwen".equals(defaultKey) && qianwenApiKey != null && !qianwenApiKey.isEmpty()) {
            return qianwenApiKey;
        }
        
        return null;
    }
    
    private String getSecretKeyFromConfig(LlmProviderConfigDTO config) {
        Map<String, Object> providerConfig = config.getProviderConfig();
        if (providerConfig != null && providerConfig.containsKey("secretKey")) {
            return (String) providerConfig.get("secretKey");
        }
        
        if (baiduSecretKey != null && !baiduSecretKey.isEmpty()) {
            return baiduSecretKey;
        }
        
        return null;
    }
    
    @GetMapping("/providers/{providerId}/models")
    public ResultModel<List<LlmProviderConfigDTO.ModelConfigDTO>> listModels(@PathVariable String providerId) {
        log.info("[listModels] providerId: {}", providerId);
        
        LlmProviderConfigDTO provider = providers.get(providerId);
        if (provider == null) {
            return ResultModel.notFound("Provider not found: " + providerId);
        }
        
        return ResultModel.success(provider.getModels());
    }
    
    @GetMapping("/default")
    public ResultModel<LlmProviderConfigDTO> getDefaultProvider() {
        log.info("[getDefaultProvider] request start");
        
        for (LlmProviderConfigDTO provider : providers.values()) {
            if (provider.isEnabled()) {
                return ResultModel.success(provider);
            }
        }
        
        return ResultModel.notFound("No default provider configured");
    }
}
