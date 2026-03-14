package net.ooder.skill.llm.controller;

import net.ooder.skill.llm.model.*;
import net.ooder.skill.llm.service.LlmService;
import net.ooder.skill.llm.service.LlmProviderService;
import net.ooder.skill.llm.service.LlmServiceImpl;
import net.ooder.skill.llm.service.LlmProviderServiceImpl;
import net.ooder.skill.common.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

@RestController
@RequestMapping("/api/v1/llm")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LlmController {

    private static final Logger log = LoggerFactory.getLogger(LlmController.class);

    private LlmService llmService;
    private LlmProviderService providerService;

    public LlmController() {
        this.llmService = new LlmServiceImpl();
        this.providerService = new LlmProviderServiceImpl();
    }

    public void setLlmService(LlmService llmService) {
        this.llmService = llmService;
    }

    public void setLlmProviderService(LlmProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping("/chat")
    public ResultModel<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("LLM chat request: {}", request.getMessage());
        try {
            ChatResponse response = llmService.chat(request);
            return ResultModel.success(response);
        } catch (Exception e) {
            log.error("LLM chat failed", e);
            return ResultModel.error("LLM chat failed: " + e.getMessage());
        }
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody ChatRequest request) {
        log.info("LLM stream chat request: {}", request.getMessage());
        return llmService.streamChat(request);
    }

    @GetMapping("/providers")
    public ResultModel<List<LlmProvider>> listProviders() {
        List<LlmProvider> providers = providerService.findAll();
        return ResultModel.success(providers);
    }

    @GetMapping("/providers/{providerId}")
    public ResultModel<LlmProvider> getProvider(@PathVariable String providerId) {
        LlmProvider provider = providerService.findById(providerId);
        if (provider == null) {
            return ResultModel.notFound("Provider not found: " + providerId);
        }
        return ResultModel.success(provider);
    }

    @PostMapping("/providers/{providerId}/configure")
    public ResultModel<Boolean> configureProvider(
            @PathVariable String providerId,
            @RequestBody Map<String, String> config) {
        log.info("Configure LLM provider: {}", providerId);
        
        String apiKey = config.get("apiKey");
        String baseUrl = config.get("baseUrl");
        String model = config.get("model");
        
        if (llmService instanceof net.ooder.skill.llm.service.LlmServiceImpl) {
            ((net.ooder.skill.llm.service.LlmServiceImpl) llmService).configureProvider(providerId, apiKey, baseUrl);
        }
        
        LlmProvider provider = providerService.findById(providerId);
        if (provider != null) {
            provider.setEnabled(true);
            if (baseUrl != null && !baseUrl.isEmpty()) {
                provider.setEndpoint(baseUrl);
            }
            providerService.update(provider);
        }
        
        System.setProperty("llm.provider." + providerId + ".apiKey", apiKey != null ? apiKey : "");
        System.setProperty("llm.provider." + providerId + ".baseUrl", baseUrl != null ? baseUrl : "");
        System.setProperty("llm.provider." + providerId + ".model", model != null ? model : "deepseek-chat");
        System.setProperty("llm.provider." + providerId + ".configured", "true");
        
        log.info("LLM provider {} configured: baseUrl={}, model={}", providerId, baseUrl, model);
        
        return ResultModel.success(true);
    }

    @GetMapping("/models")
    public ResultModel<List<Map<String, Object>>> listModels() {
        List<Map<String, Object>> models = llmService.listAvailableModels();
        return ResultModel.success(models);
    }
}
