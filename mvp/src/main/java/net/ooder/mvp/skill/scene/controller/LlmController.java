package net.ooder.mvp.skill.scene.controller;

import javax.validation.Valid;
import net.ooder.config.ResultModel;
import net.ooder.scene.skill.LlmProvider;
import net.ooder.scene.skill.tool.ToolOrchestrator;
import net.ooder.scene.skill.tool.ToolRegistry;
import net.ooder.scene.llm.context.*;
import net.ooder.mvp.skill.scene.dto.llm.*;
import net.ooder.mvp.skill.scene.llm.BaiduLlmProvider;
import net.ooder.mvp.skill.scene.llm.DeepSeekLlmProvider;
import net.ooder.mvp.skill.scene.llm.AliyunBailianLlmProvider;
import net.ooder.mvp.skill.scene.llm.SkillActivationService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1/llm")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS})
public class LlmController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(LlmController.class);
    private static final long SSE_TIMEOUT = 120000L;
    
    private static final String DEFAULT_ROLE_ID = "discovery-assistant";
    
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
    
    @Value("${ooder.llm.deepseek.api-key:}")
    private String deepseekApiKey;
    
    @Value("${ooder.llm.qianwen.api-key:}")
    private String qianwenApiKey;
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private SkillActivationService skillActivationService;
    
    @Autowired
    public void setSkillActivationService(SkillActivationService skillActivationService) {
        this.skillActivationService = skillActivationService;
    }
    
    private ToolRegistry toolRegistry;
     
    @Autowired
    public void setToolRegistry(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }
    
    private ToolOrchestrator toolOrchestrator;
     
    @Autowired(required = false)
    public void setToolOrchestrator(ToolOrchestrator toolOrchestrator) {
         this.toolOrchestrator = toolOrchestrator;
     }
    
    private final Map<String, LlmProvider> providers = new ConcurrentHashMap<String, LlmProvider>();
    private String currentProviderType = "mock";
    private String currentModel = "default";

    public LlmController() {
        loadProviders();
    }
    
    @javax.annotation.PostConstruct
    public void init() {
        initBaiduProvider();
        initDeepSeekProvider();
        initQianwenProvider();
        
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
        if ("deepseek".equals(currentProviderType)) {
            log.info("DeepSeek LLM API Key configured: {}", deepseekApiKey != null && !deepseekApiKey.isEmpty() ? "yes" : "no");
        }
        if ("qianwen".equals(currentProviderType) || "aliyun-bailian".equals(currentProviderType)) {
            log.info("Qianwen LLM API Key configured: {}", qianwenApiKey != null && !qianwenApiKey.isEmpty() ? "yes" : "no");
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
    
    private void initDeepSeekProvider() {
        if (deepseekApiKey != null && !deepseekApiKey.isEmpty()) {
            DeepSeekLlmProvider deepseekProvider = new DeepSeekLlmProvider();
            deepseekProvider.setApiKey(deepseekApiKey);
            deepseekProvider.setToolRegistry(toolRegistry);
            if (toolOrchestrator != null) {
                deepseekProvider.setToolOrchestrator(toolOrchestrator);
            }
            providers.put("deepseek", deepseekProvider);
            log.info("DeepSeek LLM Provider registered with models: {}", deepseekProvider.getSupportedModels());
        }
    }
    
    private void initQianwenProvider() {
        if (qianwenApiKey != null && !qianwenApiKey.isEmpty()) {
            AliyunBailianLlmProvider qianwenProvider = new AliyunBailianLlmProvider();
            qianwenProvider.setApiKey(qianwenApiKey);
            qianwenProvider.setToolRegistry(toolRegistry);
            if (toolOrchestrator != null) {
                qianwenProvider.setToolOrchestrator(toolOrchestrator);
            }
            providers.put("qianwen", qianwenProvider);
            providers.put("aliyun-bailian", qianwenProvider);
            log.info("Qianwen LLM Provider registered with models: {}", qianwenProvider.getSupportedModels());
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
    public ResultModel<ChatResponseDTO> chat(@RequestBody @Valid ChatRequestDTO request) {
        String providerType = request.getProvider() != null ? request.getProvider() : currentProviderType;
        String model = request.getModel() != null ? request.getModel() : currentModel;
        log.info("Chat API called with provider: {}, model: {}", providerType, model);
        ResultModel<ChatResponseDTO> result = new ResultModel<ChatResponseDTO>();

        try {
            String prompt = request.getMessage();
            
            if (prompt == null || prompt.trim().isEmpty()) {
                result.setRequestStatus(400);
                result.setMessage("Message cannot be empty");
                return result;
            }
            
            LlmProvider provider = providers.get(providerType);
            
            String response;
            if (provider != null) {
                SkillActivationContext activationContext = null;
                String activationId = null;
                
                if (skillActivationService != null) {
                    ActivationRequest activationRequest = ActivationRequest.builder()
                        .skillId("skill-scene")
                        .userId("current-user")
                        .roleId(DEFAULT_ROLE_ID)
                        .build();
                    
                    activationContext = skillActivationService.activateSkill(activationRequest);
                    activationId = activationContext.getActivationId();
                    log.info("[LlmController] Skill activated: {}", activationId);
                }
                
                List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>();
                
                Map<String, Object> systemMessage = new HashMap<String, Object>();
                systemMessage.put("role", "system");
                
                String systemPrompt;
                if (activationContext != null) {
                    systemPrompt = getSystemPrompt() + "\n\n" + activationContext.buildSystemPrompt();
                } else {
                    systemPrompt = getSystemPrompt();
                }
                log.info("[LlmController] System prompt length: {}, first 100 chars: {}", 
                    systemPrompt.length(), systemPrompt.substring(0, Math.min(100, systemPrompt.length())));
                systemMessage.put("content", systemPrompt);
                messages.add(systemMessage);
                
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
                
                if (provider.supportsFunctionCalling()) {
                    List<Map<String, Object>> tools;
                    if (activationContext != null && activationContext.getFunctionContext() != null) {
                        tools = activationContext.getFunctionContext().toTools();
                        log.debug("Added {} tools from SkillActivationContext for function calling", tools.size());
                    } else if (toolRegistry != null) {
                        tools = toolRegistry.getToolDefinitions();
                        log.debug("Added {} tools from ToolRegistry for function calling", tools.size());
                    } else {
                        tools = Collections.emptyList();
                    }
                    
                    if (!tools.isEmpty()) {
                        options.put("tools", tools);
                        options.put("tool_choice", "auto");
                    }
                }
                
                if (activationId != null) {
                    options.put("activationId", activationId);
                }
                
                Map<String, Object> chatResult = provider.chat(model, messages, options);
                response = (String) chatResult.get("content");
                
                Map<String, Object> actionResult = (Map<String, Object>) chatResult.get("actionResult");
                
                log.info("LLM response received from provider: {}", providerType);
                
                if (activationId != null && skillActivationService != null) {
                    skillActivationService.deactivateContext(activationId);
                }
                
                if (actionResult != null) {
                    ChatResponseDTO responseDTO = ChatResponseDTO.success(response, model, providerType);
                    responseDTO.setAction(actionResult);
                    
                    result.setData(responseDTO);
                    result.setRequestStatus(200);
                    result.setMessage("Success");
                    return result;
                }
            } else if (mockEnabled) {
                response = getMockResponse(prompt);
                log.info("Using mock response (mockEnabled=true), provider not available: {}", providerType);
            } else {
                log.warn("No LLM provider available and mock is disabled");
                result.setRequestStatus(503);
                result.setMessage("No LLM provider available and mock is disabled");
                return result;
            }

            ChatResponseDTO responseDTO = ChatResponseDTO.success(response, model, providerType);
            
            result.setData(responseDTO);
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
                        SkillActivationContext activationContext = null;
                        String activationId = null;
                        
                        if (skillActivationService != null) {
                            ActivationRequest activationRequest = ActivationRequest.builder()
                                .skillId("skill-scene")
                                .userId("current-user")
                                .roleId(DEFAULT_ROLE_ID)
                                .build();
                            
                            activationContext = skillActivationService.activateSkill(activationRequest);
                            activationId = activationContext.getActivationId();
                            log.info("[LlmController] Skill activated for stream: {}", activationId);
                        }
                        
                        List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>();
                        
                        Map<String, Object> systemMessage = new HashMap<String, Object>();
                        systemMessage.put("role", "system");
                        String systemPrompt;
                        if (activationContext != null) {
                            systemPrompt = getSystemPrompt() + "\n\n" + activationContext.buildSystemPrompt();
                        } else {
                            systemPrompt = getSystemPrompt();
                        }
                        systemMessage.put("content", systemPrompt);
                        messages.add(systemMessage);
                        
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
                        
                        if (provider.supportsFunctionCalling()) {
                            List<Map<String, Object>> tools;
                            if (activationContext != null && activationContext.getFunctionContext() != null) {
                                tools = activationContext.getFunctionContext().toTools();
                                log.debug("Added {} tools from SkillActivationContext for stream", tools.size());
                            } else if (toolRegistry != null) {
                                tools = toolRegistry.getToolDefinitions();
                                log.debug("Added {} tools from ToolRegistry for stream", tools.size());
                            } else {
                                tools = Collections.emptyList();
                            }
                            
                            if (!tools.isEmpty()) {
                                options.put("tools", tools);
                                options.put("tool_choice", "auto");
                            }
                        }
                        
                        Map<String, Object> chatResult = provider.chat(model, messages, options);
                        fullResponse = (String) chatResult.get("content");
                        
                        Map<String, Object> actionResult = (Map<String, Object>) chatResult.get("actionResult");
                        
                        if (actionResult != null) {
                            ChatResponseDTO responseDTO = ChatResponseDTO.success(fullResponse, model, providerType);
                            responseDTO.setAction(actionResult);
                            
                            String jsonResponse = LlmController.this.objectMapper.writeValueAsString(responseDTO);
                            emitter.send(SseEmitter.event()
                                .name("action")
                                .data(jsonResponse));
                            
                            log.info("[LlmController] Action returned in stream response: {}", actionResult);
                        }
                        
                        if (activationId != null && skillActivationService != null) {
                            skillActivationService.deactivateContext(activationId);
                        }
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

    private String getProviderDisplayName(String providerId) {
        Map<String, String> names = new HashMap<String, String>();
        names.put("deepseek", "DeepSeek");
        names.put("baidu", "百度文心");
        names.put("openai", "OpenAI");
        names.put("qianwen", "通义千问");
        names.put("aliyun-bailian", "阿里云百炼");
        names.put("mock", "Mock Provider");
        return names.getOrDefault(providerId, providerId);
    }

    private String getModelDisplayName(String modelId) {
        Map<String, String> names = new HashMap<String, String>();
        names.put("deepseek-chat", "DeepSeek Chat");
        names.put("deepseek-coder", "DeepSeek Coder");
        names.put("ernie-bot-4", "ERNIE Bot 4.0");
        names.put("ernie-bot-turbo", "ERNIE Bot Turbo");
        names.put("gpt-4-turbo", "GPT-4 Turbo");
        names.put("gpt-3.5-turbo", "GPT-3.5 Turbo");
        names.put("qwen-turbo", "通义千问 Turbo");
        names.put("qwen-plus", "通义千问 Plus");
        names.put("qwen-max", "通义千问 Max");
        return names.getOrDefault(modelId, modelId);
    }

    private List<Map<String, Object>> getDefaultModelsForProvider(String provider) {
        List<Map<String, Object>> models = new ArrayList<Map<String, Object>>();
        
        Map<String, List<String[]>> providerModels = new HashMap<String, List<String[]>>();
        providerModels.put("deepseek", Arrays.asList(
            new String[]{"deepseek-chat", "DeepSeek Chat"},
            new String[]{"deepseek-coder", "DeepSeek Coder"}
        ));
        providerModels.put("baidu", Arrays.asList(
            new String[]{"ernie-bot-4", "ERNIE Bot 4.0"},
            new String[]{"ernie-bot-turbo", "ERNIE Bot Turbo"}
        ));
        providerModels.put("openai", Arrays.asList(
            new String[]{"gpt-4-turbo", "GPT-4 Turbo"},
            new String[]{"gpt-3.5-turbo", "GPT-3.5 Turbo"}
        ));
        providerModels.put("qianwen", Arrays.asList(
            new String[]{"qwen-turbo", "通义千问 Turbo"},
            new String[]{"qwen-plus", "通义千问 Plus"},
            new String[]{"qwen-max", "通义千问 Max"}
        ));
        providerModels.put("aliyun-bailian", Arrays.asList(
            new String[]{"qwen-turbo", "通义千问 Turbo"},
            new String[]{"qwen-plus", "通义千问 Plus"}
        ));
        
        List<String[]> modelList = providerModels.get(provider);
        if (modelList != null) {
            for (String[] model : modelList) {
                Map<String, Object> modelInfo = new HashMap<String, Object>();
                modelInfo.put("value", model[0]);
                modelInfo.put("label", model[1]);
                modelInfo.put("provider", provider);
                models.add(modelInfo);
            }
        }
        
        return models;
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

    private String getSystemPrompt() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是Ooder场景技能平台的智能助手。\n\n");
        prompt.append("## 平台简介\n");
        prompt.append("Ooder是一个场景驱动的技能管理平台，用户可以通过发现、安装、配置能力来构建自动化场景。\n\n");
        prompt.append("## 核心概念\n");
        prompt.append("- **能力(Capability)**: 可执行的功能单元，如发送邮件、生成报告等\n");
        prompt.append("- **场景(Scene)**: 由多个能力组成的自动化流程\n\n");
        prompt.append("## 技能分类体系 v3.0\n");
        prompt.append("### 技能形态(SkillForm)\n");
        prompt.append("- **SCENE**: 场景技能 - 具有完整场景流程的技能\n");
        prompt.append("- **STANDALONE**: 独立技能 - 独立运行的功能单元\n\n");
        prompt.append("### 场景类型(SceneType)\n");
        prompt.append("- **AUTO**: 自驱场景 - 自动驱动执行\n");
        prompt.append("- **TRIGGER**: 触发场景 - 需要外部触发\n");
        prompt.append("- **HYBRID**: 混合场景 - 结合自驱和触发\n\n");
        prompt.append("### 技能分类(SkillCategory)\n");
        prompt.append("- **AI**: AI智能技能\n");
        prompt.append("- **SERVICE**: 服务类技能\n");
        prompt.append("- **COMMUNICATION**: 通信类技能\n");
        prompt.append("- **STORAGE**: 存储类技能\n");
        prompt.append("- **CUSTOM**: 自定义技能\n\n");
        prompt.append("## 发现能力功能\n");
        prompt.append("用户可以通过以下方式发现能力：\n");
        prompt.append("1. **本地文件系统**: 扫描本地已安装的技能目录\n");
        prompt.append("2. **Gitee发现**: 从 https://gitee.com/ooderCN/skills 仓库发现技能\n");
        prompt.append("3. **GitHub发现**: 从GitHub仓库发现技能\n");
        prompt.append("4. **Git仓库**: 从任意Git仓库发现技能\n\n");
        prompt.append("## Gitee发现说明\n");
        prompt.append("当用户点击\"Gitee发现\"时，系统会从 ooderCN/skills 仓库扫描技能包(skill.yaml)，\n");
        prompt.append("识别其中的能力并展示给用户。用户可以查看能力详情、安装能力到本地。\n\n");
        prompt.append("## 回复要求\n");
        prompt.append("- 用简洁专业的中文回复\n");
        prompt.append("- 不要提及你是DeepSeek或其他AI模型\n");
        prompt.append("- 如果用户询问下载应用，请解释这是Web平台，无需下载\n");
        prompt.append("- 如果用户询问Gitee发现，请解释这是从ooderCN技能仓库发现能力，不是代码安全扫描");
        return prompt.toString();
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
