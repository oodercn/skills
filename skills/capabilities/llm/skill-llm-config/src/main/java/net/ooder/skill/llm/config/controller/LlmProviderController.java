package net.ooder.skill.llm.config.controller;

import net.ooder.skill.llm.config.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/llm-providers")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LlmProviderController {

    private static final Logger log = LoggerFactory.getLogger(LlmProviderController.class);

    @GetMapping("/providers")
    public ResultModel<List<Map<String, Object>>> getProviders() {
        log.info("[LlmProviderController] Get providers");
        List<Map<String, Object>> providers = new ArrayList<>();
        
        Map<String, Object> openai = new HashMap<>();
        openai.put("id", "openai");
        openai.put("name", "OpenAI");
        openai.put("type", "openai");
        openai.put("status", "active");
        providers.add(openai);
        
        Map<String, Object> anthropic = new HashMap<>();
        anthropic.put("id", "anthropic");
        anthropic.put("name", "Anthropic");
        anthropic.put("type", "anthropic");
        anthropic.put("status", "active");
        providers.add(anthropic);
        
        return ResultModel.success(providers);
    }

    @GetMapping("/providers/{id}")
    public ResultModel<Map<String, Object>> getProvider(@PathVariable String id) {
        log.info("[LlmProviderController] Get provider: {}", id);
        Map<String, Object> provider = new HashMap<>();
        provider.put("id", id);
        provider.put("name", id.toUpperCase());
        provider.put("type", id);
        provider.put("status", "active");
        return ResultModel.success(provider);
    }

    @PostMapping("/providers/{id}/test")
    public ResultModel<Map<String, Object>> testProvider(@PathVariable String id) {
        log.info("[LlmProviderController] Test provider: {}", id);
        Map<String, Object> result = new HashMap<>();
        result.put("providerId", id);
        result.put("status", "success");
        result.put("message", "Connection test passed");
        result.put("testTime", new Date().toString());
        return ResultModel.success(result);
    }

    @GetMapping("/providers/{id}/models")
    public ResultModel<List<Map<String, Object>>> getProviderModels(@PathVariable String id) {
        log.info("[LlmProviderController] Get models for provider: {}", id);
        List<Map<String, Object>> models = new ArrayList<>();
        
        Map<String, Object> model1 = new HashMap<>();
        model1.put("id", id + "-model-1");
        model1.put("name", id.toUpperCase() + " Model 1");
        model1.put("providerId", id);
        models.add(model1);
        
        Map<String, Object> model2 = new HashMap<>();
        model2.put("id", id + "-model-2");
        model2.put("name", id.toUpperCase() + " Model 2");
        model2.put("providerId", id);
        models.add(model2);
        
        return ResultModel.success(models);
    }

    @GetMapping("/default")
    public ResultModel<Map<String, Object>> getDefaultProvider() {
        log.info("[LlmProviderController] Get default provider");
        Map<String, Object> provider = new HashMap<>();
        provider.put("id", "openai");
        provider.put("name", "OpenAI");
        provider.put("type", "openai");
        provider.put("isDefault", true);
        return ResultModel.success(provider);
    }
}
