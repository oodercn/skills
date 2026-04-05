package net.ooder.skill.scene.controller;

import jakarta.validation.Valid;
import net.ooder.config.ResultModel;
import net.ooder.scene.skill.LlmProvider;
import net.ooder.scene.skill.tool.ToolOrchestrator;
import net.ooder.scene.skill.tool.ToolRegistry;
import net.ooder.scene.llm.context.*;
import net.ooder.skill.scene.dto.llm.*;
import net.ooder.skill.scene.llm.BaiduLlmProvider;
import net.ooder.skill.scene.llm.DeepSeekLlmProvider;
import net.ooder.skill.scene.llm.SkillActivationService;

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
@RequestMapping("/api/llm")
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
    
    @jakarta.annotation.PostConstruct
    public void init() {
        initBaiduProvider();
        initDeepSeekProvider();
        
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

    @PostMapping("/execute")
    @ResponseBody
    public ResultModel<Map<String, Object>> executeAction(@RequestBody Map<String, Object> request) {
        String action = (String) request.get("action");
        String module = (String) request.getOrDefault("module", "discovery");
        Map<String, Object> params = (Map<String, Object>) request.getOrDefault("params", new HashMap<String, Object>());
        
        log.info("[LlmController] Execute action: {}, module: {}, params: {}", action, module, params);
        
        ResultModel<Map<String, Object>> result = new ResultModel<Map<String, Object>>();
        
        try {
            Map<String, Object> actionResult = new HashMap<String, Object>();
            actionResult.put("action", action);
            actionResult.put("module", module);
            actionResult.put("params", params);
            
            if ("startScan".equals(action)) {
                String method = params.containsKey("method") ? (String) params.get("method") : "AUTO";
                actionResult.put("success", true);
                actionResult.put("message", "鎵弿宸插惎鍔? " + method);
                actionResult.put("method", method);
                log.info("[LlmController] Start scan with method: {}", method);
            } else if ("filterCapabilities".equals(action)) {
                actionResult.put("success", true);
                actionResult.put("message", "绛涢€夋潯浠跺凡搴旂敤");
                log.info("[LlmController] Filter capabilities applied");
            } else if ("selectCapability".equals(action)) {
                String capabilityId = (String) params.get("capabilityId");
                actionResult.put("success", true);
                actionResult.put("message", "宸查€夋嫨鑳藉姏: " + capabilityId);
                log.info("[LlmController] Capability selected: {}", capabilityId);
            } else if ("startInstall".equals(action)) {
                String capabilityId = (String) params.get("capabilityId");
                actionResult.put("success", true);
                actionResult.put("message", "瀹夎宸插紑濮? " + capabilityId);
                log.info("[LlmController] Install started for: {}", capabilityId);
            } else {
                actionResult.put("success", true);
                actionResult.put("message", "Action executed: " + action);
            }
            
            result.setData(actionResult);
            result.setRequestStatus(200);
            result.setMessage("Action executed successfully");
        } catch (Exception e) {
            log.error("Execute action error", e);
            result.setRequestStatus(500);
            result.setMessage("Execute action failed: " + e.getMessage());
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
                response = "[缈昏瘧缁撴灉] " + text;
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
        prompt.append("浣犳槸Ooder鍦烘櫙鎶€鑳藉钩鍙扮殑鏅鸿兘鍔╂墜銆俓n\n");
        prompt.append("## 骞冲彴绠€浠媆n");
        prompt.append("Ooder鏄竴涓満鏅┍鍔ㄧ殑鎶€鑳界鐞嗗钩鍙帮紝鐢ㄦ埛鍙互閫氳繃鍙戠幇銆佸畨瑁呫€侀厤缃兘鍔涙潵鏋勫缓鑷姩鍖栧満鏅€俓n\n");
        prompt.append("## 鏍稿績姒傚康\n");
        prompt.append("- **鑳藉姏(Capability)**: 鍙墽琛岀殑鍔熻兘鍗曞厓锛屽鍙戦€侀偖浠躲€佺敓鎴愭姤鍛婄瓑\n");
        prompt.append("- **鍦烘櫙(Scene)**: 鐢卞涓兘鍔涚粍鎴愮殑鑷姩鍖栨祦绋媆n\n");
        prompt.append("## 鎶€鑳藉垎绫讳綋绯?v3.0\n");
        prompt.append("### 鎶€鑳藉舰鎬?SkillForm)\n");
        prompt.append("- **SCENE**: 鍦烘櫙鎶€鑳?- 鍏锋湁瀹屾暣鍦烘櫙娴佺▼鐨勬妧鑳絓n");
        prompt.append("- **STANDALONE**: 鐙珛鎶€鑳?- 鐙珛杩愯鐨勫姛鑳藉崟鍏僜n\n");
        prompt.append("### 鍦烘櫙绫诲瀷(SceneType)\n");
        prompt.append("- **AUTO**: 鑷┍鍦烘櫙 - 鑷姩椹卞姩鎵ц\n");
        prompt.append("- **TRIGGER**: 瑙﹀彂鍦烘櫙 - 闇€瑕佸閮ㄨЕ鍙慭n");
        prompt.append("- **HYBRID**: 娣峰悎鍦烘櫙 - 缁撳悎鑷┍鍜岃Е鍙慭n\n");
        prompt.append("### 鎶€鑳藉垎绫?SkillCategory)\n");
        prompt.append("- **AI**: AI鏅鸿兘鎶€鑳絓n");
        prompt.append("- **SERVICE**: 鏈嶅姟绫绘妧鑳絓n");
        prompt.append("- **COMMUNICATION**: 閫氫俊绫绘妧鑳絓n");
        prompt.append("- **STORAGE**: 瀛樺偍绫绘妧鑳絓n");
        prompt.append("- **CUSTOM**: 鑷畾涔夋妧鑳絓n\n");
        prompt.append("## 鍙戠幇鑳藉姏鍔熻兘\n");
        prompt.append("鐢ㄦ埛鍙互閫氳繃浠ヤ笅鏂瑰紡鍙戠幇鑳藉姏锛歕n");
        prompt.append("1. **鏈湴鏂囦欢绯荤粺**: 鎵弿鏈湴宸插畨瑁呯殑鎶€鑳界洰褰昞n");
        prompt.append("2. **Gitee鍙戠幇**: 浠?https://gitee.com/ooderCN/skills 浠撳簱鍙戠幇鎶€鑳絓n");
        prompt.append("3. **GitHub鍙戠幇**: 浠嶨itHub浠撳簱鍙戠幇鎶€鑳絓n");
        prompt.append("4. **Git浠撳簱**: 浠庝换鎰廏it浠撳簱鍙戠幇鎶€鑳絓n\n");
        prompt.append("## Gitee鍙戠幇璇存槑\n");
        prompt.append("褰撶敤鎴风偣鍑籠"Gitee鍙戠幇\"鏃讹紝绯荤粺浼氫粠 ooderCN/skills 浠撳簱鎵弿鎶€鑳藉寘(skill.yaml)锛孿n");
        prompt.append("璇嗗埆鍏朵腑鐨勮兘鍔涘苟灞曠ず缁欑敤鎴枫€傜敤鎴峰彲浠ユ煡鐪嬭兘鍔涜鎯呫€佸畨瑁呰兘鍔涘埌鏈湴銆俓n\n");
        prompt.append("## 鍥炲瑕佹眰\n");
        prompt.append("- 鐢ㄧ畝娲佷笓涓氱殑涓枃鍥炲\n");
        prompt.append("- 涓嶈鎻愬強浣犳槸DeepSeek鎴栧叾浠朅I妯″瀷\n");
        prompt.append("- 濡傛灉鐢ㄦ埛璇㈤棶涓嬭浇搴旂敤锛岃瑙ｉ噴杩欐槸Web骞冲彴锛屾棤闇€涓嬭浇\n");
        prompt.append("- 濡傛灉鐢ㄦ埛璇㈤棶Gitee鍙戠幇锛岃瑙ｉ噴杩欐槸浠巓oderCN鎶€鑳戒粨搴撳彂鐜拌兘鍔涳紝涓嶆槸浠ｇ爜瀹夊叏鎵弿");
        return prompt.toString();
    }

    private String getMockResponse(String prompt) {
        StringBuilder response = new StringBuilder();
        
        if (prompt == null) {
            prompt = "";
        }
        
        if (prompt.contains("閰嶇疆") || prompt.contains("璁剧疆")) {
            response.append("鎴戝彲浠ュ府鍔╂偍杩涜閰嶇疆銆俓n\n");
            response.append("璇峰憡璇夋垜鎮ㄦ兂瑕侀厤缃殑鍏蜂綋鍐呭锛屼緥濡傦細\n");
            response.append("- 鍦烘櫙閰嶇疆\n");
            response.append("- 鑳藉姏缁戝畾\n");
            response.append("- 鍙備笌鑰呯鐞哱n");
        } else if (prompt.contains("鍒嗘瀽")) {
            response.append("鎴戝彲浠ュ府鍔╂偍鍒嗘瀽鏁版嵁銆俓n\n");
            response.append("璇锋彁渚涢渶瑕佸垎鏋愮殑鏁版嵁鎴栭€夋嫨瑕佸垎鏋愮殑鍐呭銆?);
        } else if (prompt.contains("浠ｇ爜") || prompt.contains("鐢熸垚")) {
            response.append("鎴戝彲浠ュ府鍔╂偍鐢熸垚浠ｇ爜銆俓n\n");
            response.append("```java\n");
            response.append("// 绀轰緥浠ｇ爜\n");
            response.append("public class Example {\n");
            response.append("    public void execute() {\n");
            response.append("        System.out.println(\"Hello, Ooder!\");\n");
            response.append("    }\n");
            response.append("}\n");
            response.append("```\n");
        } else if (prompt.contains("甯姪") || prompt.contains("浣跨敤")) {
            response.append("## Ooder 鏅鸿兘鍔╂墜浣跨敤鎸囧崡\n\n");
            response.append("### 鍔熻兘鍒楄〃\n");
            response.append("1. **鑷姩閰嶇疆** - 鏍规嵁褰撳墠椤甸潰鍐呭鎻愪緵閰嶇疆寤鸿\n");
            response.append("2. **鏁版嵁鍒嗘瀽** - 鍒嗘瀽褰撳墠椤甸潰鐨勬暟鎹甛n");
            response.append("3. **浠ｇ爜鐢熸垚** - 鐢熸垚鐩稿叧浠ｇ爜鐗囨\n");
            response.append("4. **浣跨敤甯姪** - 鎻愪緵鍔熻兘浣跨敤璇存槑\n\n");
            response.append("### 蹇嵎鎿嶄綔\n");
            response.append("- 鐐瑰嚮蹇嵎鎸夐挳蹇€熷紑濮嬪璇漒n");
            response.append("- 鏀寔涓婁笅鏂囨劅鐭ワ紝鑷姩鑾峰彇褰撳墠椤甸潰淇℃伅\n");
        } else {
            response.append("鎮ㄥソ锛佹垜鏄?Ooder 鏅鸿兘鍔╂墜銆俓n\n");
            response.append("鎴戝彲浠ュ府鍔╂偍锛歕n");
            response.append("- 馃摑 鑷姩閰嶇疆鍦烘櫙鍜岃兘鍔沑n");
            response.append("- 馃搳 鍒嗘瀽椤甸潰鏁版嵁\n");
            response.append("- 馃捇 鐢熸垚浠ｇ爜鐗囨\n");
            response.append("- 鉂?瑙ｇ瓟浣跨敤闂\n\n");
            response.append("璇烽棶鏈変粈涔堝彲浠ュ府鎮ㄧ殑锛?);
        }
        
        return response.toString();
    }
}
