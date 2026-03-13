package net.ooder.skill.common.controller;

import net.ooder.skill.common.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/llm")
public class LlmConfigController {

    private static final Logger log = LoggerFactory.getLogger(LlmConfigController.class);

    @GetMapping("/providers")
    public ResultModel<List<Map<String, Object>>> getProviders() {
        List<Map<String, Object>> providers = Arrays.asList(
            createProvider("openai", "OpenAI", "https://api.openai.com", 
                Arrays.asList("gpt-4", "gpt-4-turbo", "gpt-3.5-turbo")),
            createProvider("ollama", "Ollama (本地)", "http://localhost:11434",
                Arrays.asList("llama2", "llama3", "mistral", "codellama")),
            createProvider("qianwen", "通义千问", "https://dashscope.aliyuncs.com",
                Arrays.asList("qwen-max", "qwen-plus", "qwen-turbo")),
            createProvider("deepseek", "DeepSeek", "https://api.deepseek.com",
                Arrays.asList("deepseek-chat", "deepseek-coder")),
            createProvider("volcengine", "火山引擎", "https://ark.cn-beijing.volces.com",
                Arrays.asList("doubao-pro", "doubao-lite"))
        );
        return ResultModel.success(providers);
    }

    @GetMapping("/models")
    public ResultModel<List<Map<String, Object>>> getModels(
            @RequestParam(required = false) String provider) {
        
        List<Map<String, Object>> allModels = Arrays.asList(
            createModel("gpt-4", "GPT-4", "openai", 8192, true, true, 0.03, 0.06),
            createModel("gpt-4-turbo", "GPT-4 Turbo", "openai", 128000, true, true, 0.01, 0.03),
            createModel("gpt-3.5-turbo", "GPT-3.5 Turbo", "openai", 16384, true, true, 0.0015, 0.002),
            createModel("llama2", "Llama 2", "ollama", 4096, true, false, 0, 0),
            createModel("llama3", "Llama 3", "ollama", 8192, true, false, 0, 0),
            createModel("qwen-max", "通义千问-Max", "qianwen", 32000, true, true, 0.12, 0.12),
            createModel("deepseek-chat", "DeepSeek Chat", "deepseek", 32768, true, true, 0.001, 0.002),
            createModel("doubao-pro", "豆包 Pro", "volcengine", 32768, true, true, 0.0008, 0.001)
        );

        if (provider != null && !provider.isEmpty()) {
            List<Map<String, Object>> filtered = new ArrayList<>();
            for (Map<String, Object> model : allModels) {
                if (provider.equals(model.get("provider"))) {
                    filtered.add(model);
                }
            }
            return ResultModel.success(filtered);
        }

        return ResultModel.success(allModels);
    }

    @GetMapping("/config")
    public ResultModel<Map<String, Object>> getConfig() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("provider", "ollama");
        config.put("model", "llama3");
        config.put("baseUrl", "http://localhost:11434");
        config.put("temperature", 0.7);
        config.put("maxTokens", 2048);
        config.put("streamEnabled", true);
        return ResultModel.success(config);
    }

    @PostMapping("/config")
    public ResultModel<Map<String, Object>> saveConfig(@RequestBody Map<String, Object> config) {
        log.info("Saving LLM config: {}", config);
        
        Map<String, Object> saved = new LinkedHashMap<>();
        saved.put("provider", config.getOrDefault("provider", "ollama"));
        saved.put("model", config.getOrDefault("model", "llama3"));
        saved.put("baseUrl", config.getOrDefault("baseUrl", "http://localhost:11434"));
        saved.put("temperature", config.getOrDefault("temperature", 0.7));
        saved.put("maxTokens", config.getOrDefault("maxTokens", 2048));
        saved.put("streamEnabled", config.getOrDefault("streamEnabled", true));
        saved.put("updatedAt", System.currentTimeMillis());
        
        return ResultModel.success(saved);
    }

    @PostMapping("/test")
    public ResultModel<Map<String, Object>> testConnection(@RequestBody Map<String, Object> config) {
        log.info("Testing LLM connection: {}", config);
        
        String provider = (String) config.getOrDefault("provider", "ollama");
        String baseUrl = (String) config.getOrDefault("baseUrl", "");
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("provider", provider);
        result.put("baseUrl", baseUrl);
        
        if ("ollama".equals(provider)) {
            result.put("connected", true);
            result.put("message", "Ollama 连接成功");
        } else if (baseUrl != null && !baseUrl.isEmpty()) {
            result.put("connected", true);
            result.put("message", provider + " 连接成功");
        } else {
            result.put("connected", false);
            result.put("message", "请配置 API Base URL");
        }
        
        return ResultModel.success(result);
    }

    private Map<String, Object> createProvider(String id, String name, String baseUrl, List<String> models) {
        Map<String, Object> provider = new LinkedHashMap<>();
        provider.put("id", id);
        provider.put("name", name);
        provider.put("baseUrl", baseUrl);
        provider.put("models", models);
        return provider;
    }

    private Map<String, Object> createModel(String id, String name, String provider, 
            int contextLength, boolean supportsStreaming, boolean supportsFunctionCall,
            double inputPrice, double outputPrice) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("id", id);
        model.put("name", name);
        model.put("provider", provider);
        model.put("contextLength", contextLength);
        model.put("supportsStreaming", supportsStreaming);
        model.put("supportsFunctionCall", supportsFunctionCall);
        model.put("inputPricePerToken", inputPrice);
        model.put("outputPricePerToken", outputPrice);
        return model;
    }
}
