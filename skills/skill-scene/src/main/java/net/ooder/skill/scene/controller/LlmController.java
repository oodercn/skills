package net.ooder.skill.scene.controller;

import javax.validation.Valid;
import net.ooder.config.ResultModel;
import net.ooder.scene.skill.LlmProvider;
import net.ooder.skill.scene.dto.llm.*;
import net.ooder.skill.scene.llm.BaiduLlmProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/llm")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS})
public class LlmController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(LlmController.class);
    private static final long SSE_TIMEOUT = 120000L;
    
    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;
    
    @Value("${ooder.llm.provider:mock}")
    private String configProvider;
    
    @Value("${ooder.llm.model:default}")
    private String configModel;
    
    @Value("${ooder.llm.baidu.api-key:}")
    private String baiduApiKey;
    
    @Value("${ooder.llm.baidu.secret-key:}")
    private String baiduSecretKey;
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    private final Map<String, LlmProvider> providers = new ConcurrentHashMap<String, LlmProvider>();
    private String currentProviderType = "mock";
    private String currentModel = "default";

    public LlmController() {
        loadProviders();
    }
    
    @javax.annotation.PostConstruct
    public void init() {
        initBaiduProvider();
        
        if (configProvider != null && !configProvider.isEmpty() && !configProvider.equals("mock")) {
            currentProviderType = configProvider;
        }
        if (configModel != null && !configModel.isEmpty() && !configModel.equals("default")) {
            currentModel = configModel;
        }
        log.info("LLM initialized with provider: {}, model: {}", currentProviderType, currentModel);
        if ("baidu".equals(currentProviderType)) {
            log.info("Baidu LLM API Key configured: {}", baiduApiKey != null && !baiduApiKey.isEmpty() ? "yes" : "no");
        }
    }
    
    private void initBaiduProvider() {
        if (baiduApiKey != null && !baiduApiKey.isEmpty() && baiduSecretKey != null && !baiduSecretKey.isEmpty()) {
            BaiduLlmProvider baiduProvider = new BaiduLlmProvider();
            baiduProvider.setAccessKey(baiduApiKey);
            baiduProvider.setSecretKey(baiduSecretKey);
            providers.put("baidu", baiduProvider);
            log.info("Baidu LLM Provider registered with models: {}", baiduProvider.getSupportedModels());
        }
    }

    private void loadProviders() {
        try {
            ServiceLoader<LlmProvider> loader = ServiceLoader.load(LlmProvider.class);
            for (LlmProvider provider : loader) {
                String type = provider.getProviderType();
                providers.put(type, provider);
                log.info("Loaded LLM Provider: {} with models: {}", type, provider.getSupportedModels());
            }
            
            if (!providers.isEmpty()) {
                currentProviderType = providers.keySet().iterator().next();
                LlmProvider firstProvider = providers.get(currentProviderType);
                List<String> models = firstProvider.getSupportedModels();
                if (!models.isEmpty()) {
                    currentModel = models.get(0);
                }
            }
            
            log.info("Total LLM Providers loaded: {}", providers.size());
        } catch (Exception e) {
            log.warn("Failed to load LLM providers via SPI: {}", e.getMessage());
        }
    }

    @PostMapping("/chat")
    @ResponseBody
    public ResultModel<Map<String, Object>> chat(@RequestBody @Valid ChatRequestDTO request) {
        log.info("Chat API called with provider: {}, model: {}", currentProviderType, currentModel);
        ResultModel<Map<String, Object>> result = new ResultModel<Map<String, Object>>();

        try {
            String prompt = request.getMessage();
            String model = request.getModel() != null ? request.getModel() : currentModel;
            String providerType = currentProviderType;
            
            LlmProvider provider = providers.get(providerType);
            
            String response;
            if (provider != null) {
                List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>();
                
                Map<String, Object> userMessage = new HashMap<String, Object>();
                userMessage.put("role", "user");
                userMessage.put("content", prompt);
                messages.add(userMessage);
                
                Map<String, Object> options = new HashMap<String, Object>();
                if (request.getTemperature() != null) {
                    options.put("temperature", request.getTemperature());
                }
                if (request.getMaxTokens() != null) {
                    options.put("max_tokens", request.getMaxTokens());
                }
                
                Map<String, Object> chatResult = provider.chat(model, messages, options);
                response = (String) chatResult.get("content");
                
                log.info("LLM response received from provider: {}", providerType);
            } else if (mockEnabled) {
                response = getMockResponse(prompt);
                log.info("Using mock response (mockEnabled=true), provider not available: {}", providerType);
            } else {
                log.warn("No LLM provider available and mock is disabled");
                result.setRequestStatus(503);
                result.setMessage("No LLM provider available and mock is disabled");
                return result;
            }

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("response", response);
            data.put("model", model);
            data.put("provider", providerType);
            
            result.setData(data);
            result.setRequestStatus(200);
            result.setMessage("Success");
        } catch (Exception e) {
            log.error("Chat API error", e);
            result.setRequestStatus(500);
            result.setMessage("Chat failed: " + e.getMessage());
        }

        return result;
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestBody @Valid ChatRequestDTO request) {
        log.info("Stream Chat API called");
        
        final String prompt = request.getMessage();
        final String model = request.getModel() != null ? request.getModel() : currentModel;
        final String providerType = currentProviderType;
        
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LlmProvider provider = providers.get(providerType);
                    String fullResponse;
                    
                    if (provider != null) {
                        List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>();
                        
                        Map<String, Object> userMessage = new HashMap<String, Object>();
                        userMessage.put("role", "user");
                        userMessage.put("content", prompt);
                        messages.add(userMessage);
                        
                        Map<String, Object> options = new HashMap<String, Object>();
                        if (request.getTemperature() != null) {
                            options.put("temperature", request.getTemperature());
                        }
                        if (request.getMaxTokens() != null) {
                            options.put("max_tokens", request.getMaxTokens());
                        }
                        
                        Map<String, Object> chatResult = provider.chat(model, messages, options);
                        fullResponse = (String) chatResult.get("content");
                    } else if (mockEnabled) {
                        fullResponse = getMockResponse(prompt);
                    } else {
                        fullResponse = "Error: No LLM provider available and mock is disabled";
                        emitter.send(SseEmitter.event().name("error").data(fullResponse));
                        emitter.complete();
                        return;
                    }
                    
                    int chunkSize = 8;
                    for (int i = 0; i < fullResponse.length(); i += chunkSize) {
                        int end = Math.min(i + chunkSize, fullResponse.length());
                        String chunk = fullResponse.substring(i, end);
                        
                        emitter.send(SseEmitter.event()
                            .name("message")
                            .data(chunk));
                        
                        Thread.sleep(30);
                    }
                    
                    emitter.send(SseEmitter.event()
                        .name("done")
                        .data("[DONE]"));
                    emitter.complete();
                    
                } catch (IOException e) {
                    log.error("Error sending SSE chunk", e);
                    emitter.completeWithError(e);
                } catch (InterruptedException e) {
                    log.error("SSE interrupted", e);
                    emitter.completeWithError(e);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("Stream execution error", e);
                    emitter.completeWithError(e);
                }
            }
        });
        
        emitter.onCompletion(new Runnable() {
            @Override
            public void run() {
                log.info("SSE completed");
            }
        });
        
        emitter.onTimeout(new Runnable() {
            @Override
            public void run() {
                log.warn("SSE timeout");
                emitter.complete();
            }
        });
        
        emitter.onError(new java.util.function.Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) {
                log.error("SSE error: {}", t.getMessage());
            }
        });
        
        return emitter;
    }

    @PostMapping("/providers")
    @ResponseBody
    public ResultModel<List<Map<String, Object>>> getProviders() {
        log.info("Get Providers API called");
        ResultModel<List<Map<String, Object>>> result = new ResultModel<List<Map<String, Object>>>();

        try {
            List<Map<String, Object>> providerList = new ArrayList<Map<String, Object>>();
            
            for (Map.Entry<String, LlmProvider> entry : providers.entrySet()) {
                Map<String, Object> providerInfo = new HashMap<String, Object>();
                providerInfo.put("type", entry.getKey());
                providerInfo.put("models", entry.getValue().getSupportedModels());
                providerInfo.put("supportsStreaming", entry.getValue().supportsStreaming());
                providerInfo.put("supportsFunctionCalling", entry.getValue().supportsFunctionCalling());
                providerList.add(providerInfo);
            }
            
            if (providerList.isEmpty()) {
                Map<String, Object> mockProvider = new HashMap<String, Object>();
                mockProvider.put("type", "mock");
                mockProvider.put("models", new ArrayList<String>() {{ add("default"); }});
                mockProvider.put("supportsStreaming", true);
                mockProvider.put("supportsFunctionCalling", false);
                providerList.add(mockProvider);
            }
            
            result.setData(providerList);
            result.setRequestStatus(200);
            result.setMessage("Success");
        } catch (Exception e) {
            log.error("Get Providers API error", e);
            result.setRequestStatus(500);
            result.setMessage("Get providers failed: " + e.getMessage());
        }

        return result;
    }

    @PostMapping("/models")
    @ResponseBody
    public ResultModel<Map<String, Object>> getAvailableModels() {
        log.info("Get Available Models API called");
        ResultModel<Map<String, Object>> result = new ResultModel<Map<String, Object>>();

        try {
            List<String> allModels = new ArrayList<String>();
            Map<String, List<String>> modelsByProvider = new HashMap<String, List<String>>();
            
            for (Map.Entry<String, LlmProvider> entry : providers.entrySet()) {
                List<String> providerModels = entry.getValue().getSupportedModels();
                modelsByProvider.put(entry.getKey(), providerModels);
                allModels.addAll(providerModels);
            }
            
            if (allModels.isEmpty()) {
                allModels.add("default");
            }

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("models", allModels);
            data.put("modelsByProvider", modelsByProvider);
            data.put("currentModel", currentModel);
            data.put("currentProvider", currentProviderType);
            data.put("providers", new ArrayList<String>(providers.keySet()));
            
            result.setData(data);
            result.setRequestStatus(200);
            result.setMessage("Success");
        } catch (Exception e) {
            log.error("Get Available Models API error", e);
            result.setRequestStatus(500);
            result.setMessage("Get models failed: " + e.getMessage());
        }

        return result;
    }

    @PostMapping("/models/set")
    @ResponseBody
    public ResultModel<Boolean> setModel(@RequestBody SetModelRequestDTO request) {
        log.info("Set Model API called: modelId={}, provider={}", request.getModelId(), request.getProvider());
        ResultModel<Boolean> result = new ResultModel<Boolean>();

        try {
            String modelId = request.getModelId();
            String providerType = request.getProvider();
            
            if (providerType != null && providers.containsKey(providerType)) {
                currentProviderType = providerType;
            }
            
            if (modelId != null) {
                currentModel = modelId;
            }
            
            result.setData(true);
            result.setRequestStatus(200);
            result.setMessage("Model set successfully");
        } catch (Exception e) {
            log.error("Set Model API error", e);
            result.setRequestStatus(500);
            result.setMessage("Set model failed: " + e.getMessage());
            result.setData(false);
        }

        return result;
    }

    @PostMapping("/health")
    @ResponseBody
    public ResultModel<Map<String, Object>> health() {
        log.info("LLM Health check API called");
        ResultModel<Map<String, Object>> result = new ResultModel<Map<String, Object>>();

        try {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("healthy", true);
            data.put("currentModel", currentModel);
            data.put("currentProvider", currentProviderType);
            data.put("availableProviders", providers.keySet());
            data.put("providerCount", providers.size());
            
            result.setData(data);
            result.setRequestStatus(200);
            result.setMessage("LLM service is healthy");
        } catch (Exception e) {
            log.error("Health check error", e);
            result.setRequestStatus(500);
            result.setMessage("Health check failed: " + e.getMessage());
        }

        return result;
    }

    @PostMapping("/complete")
    @ResponseBody
    public ResultModel<String> complete(@RequestBody @Valid CompleteRequestDTO request) {
        log.info("Complete API called");
        ResultModel<String> result = new ResultModel<String>();

        try {
            String prompt = request.getPrompt();
            String model = request.getModel() != null ? request.getModel() : currentModel;
            String providerType = currentProviderType;
            
            LlmProvider provider = providers.get(providerType);
            
            String response;
            if (provider != null) {
                Map<String, Object> options = new HashMap<String, Object>();
                if (request.getTemperature() != null) {
                    options.put("temperature", request.getTemperature());
                }
                if (request.getMaxTokens() != null) {
                    options.put("max_tokens", request.getMaxTokens());
                }
                response = provider.complete(model, prompt, options);
            } else {
                response = getMockResponse(prompt);
            }
            
            result.setData(response);
            result.setRequestStatus(200);
            result.setMessage("Success");
        } catch (Exception e) {
            log.error("Complete API error", e);
            result.setRequestStatus(500);
            result.setMessage("Complete failed: " + e.getMessage());
        }

        return result;
    }

    @PostMapping("/translate")
    @ResponseBody
    public ResultModel<String> translate(@RequestBody @Valid TranslateRequestDTO request) {
        log.info("Translate API called");
        ResultModel<String> result = new ResultModel<String>();

        try {
            String text = request.getText();
            String targetLanguage = request.getTargetLang();
            String sourceLanguage = request.getSourceLang();
            String model = request.getModel() != null ? request.getModel() : currentModel;
            String providerType = currentProviderType;
            
            LlmProvider provider = providers.get(providerType);
            
            String response;
            if (provider != null) {
                response = provider.translate(model, text, targetLanguage, sourceLanguage);
            } else {
                response = "[翻译结果] " + text;
            }
            
            result.setData(response);
            result.setRequestStatus(200);
            result.setMessage("Success");
        } catch (Exception e) {
            log.error("Translate API error", e);
            result.setRequestStatus(500);
            result.setMessage("Translate failed: " + e.getMessage());
        }

        return result;
    }

    @PostMapping("/summarize")
    @ResponseBody
    public ResultModel<String> summarize(@RequestBody @Valid SummarizeRequestDTO request) {
        log.info("Summarize API called");
        ResultModel<String> result = new ResultModel<String>();

        try {
            String text = request.getText();
            Integer maxLength = request.getMaxLength() != null ? request.getMaxLength() : 200;
            String model = request.getModel() != null ? request.getModel() : currentModel;
            String providerType = currentProviderType;
            
            LlmProvider provider = providers.get(providerType);
            
            String response;
            if (provider != null) {
                response = provider.summarize(model, text, maxLength);
            } else {
                response = text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
            }
            
            result.setData(response);
            result.setRequestStatus(200);
            result.setMessage("Success");
        } catch (Exception e) {
            log.error("Summarize API error", e);
            result.setRequestStatus(500);
            result.setMessage("Summarize failed: " + e.getMessage());
        }

        return result;
    }

    private String getMockResponse(String prompt) {
        StringBuilder response = new StringBuilder();
        
        if (prompt == null) {
            prompt = "";
        }
        
        if (prompt.contains("配置") || prompt.contains("设置")) {
            response.append("我可以帮助您进行配置。\n\n");
            response.append("请告诉我您想要配置的具体内容，例如：\n");
            response.append("- 场景配置\n");
            response.append("- 能力绑定\n");
            response.append("- 参与者管理\n");
        } else if (prompt.contains("分析")) {
            response.append("我可以帮助您分析数据。\n\n");
            response.append("请提供需要分析的数据或选择要分析的内容。");
        } else if (prompt.contains("代码") || prompt.contains("生成")) {
            response.append("我可以帮助您生成代码。\n\n");
            response.append("```java\n");
            response.append("// 示例代码\n");
            response.append("public class Example {\n");
            response.append("    public void execute() {\n");
            response.append("        System.out.println(\"Hello, Ooder!\");\n");
            response.append("    }\n");
            response.append("}\n");
            response.append("```\n");
        } else if (prompt.contains("帮助") || prompt.contains("使用")) {
            response.append("## Ooder 智能助手使用指南\n\n");
            response.append("### 功能列表\n");
            response.append("1. **自动配置** - 根据当前页面内容提供配置建议\n");
            response.append("2. **数据分析** - 分析当前页面的数据\n");
            response.append("3. **代码生成** - 生成相关代码片段\n");
            response.append("4. **使用帮助** - 提供功能使用说明\n\n");
            response.append("### 快捷操作\n");
            response.append("- 点击快捷按钮快速开始对话\n");
            response.append("- 支持上下文感知，自动获取当前页面信息\n");
        } else {
            response.append("您好！我是 Ooder 智能助手。\n\n");
            response.append("我可以帮助您：\n");
            response.append("- 📝 自动配置场景和能力\n");
            response.append("- 📊 分析页面数据\n");
            response.append("- 💻 生成代码片段\n");
            response.append("- ❓ 解答使用问题\n\n");
            response.append("请问有什么可以帮您的？");
        }
        
        return response.toString();
    }
}
