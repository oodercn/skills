package net.ooder.skill.test.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class LlmChatController {
    
    private static final Logger log = LoggerFactory.getLogger(LlmChatController.class);
    private static final long SSE_TIMEOUT = 120000L;
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<String, SessionInfo>();
    private final Map<String, ProviderInfo> providers = new ConcurrentHashMap<String, ProviderInfo>();
    
    private String currentProvider = "mock";
    private String currentModel = "default";
    
    @Autowired(required = false)
    private net.ooder.skill.test.service.LLMService llmService;
    
    public LlmChatController() {
        initProviders();
    }
    
    private void initProviders() {
        providers.put("mock", new ProviderInfo("mock", "Mock Provider", 
            Arrays.asList("default", "mock-v1", "mock-v2"), true, false));
        providers.put("openai", new ProviderInfo("openai", "OpenAI", 
            Arrays.asList("gpt-3.5-turbo", "gpt-4", "gpt-4-turbo"), true, true));
        providers.put("deepseek", new ProviderInfo("deepseek", "DeepSeek", 
            Arrays.asList("deepseek-chat", "deepseek-coder"), true, true));
    }
    
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String sessionId = (String) request.get("sessionId");
        String provider = (String) request.getOrDefault("provider", currentProvider);
        String model = (String) request.getOrDefault("model", currentModel);
        Double temperature = (Double) request.getOrDefault("temperature", 0.7);
        Integer maxTokens = (Integer) request.getOrDefault("maxTokens", 4096);
        
        log.info("[chat] message: {}, sessionId: {}, provider: {}, model: {}", 
            message, sessionId, provider, model);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            SessionInfo session = getOrCreateSession(sessionId);
            session.addMessage("user", message);
            
            String response;
            int tokensUsed = 0;
            
            if (llmService != null) {
                Map<String, Object> llmStatus = llmService.checkDependencies();
                boolean llmReady = (Boolean) llmStatus.get("ready");
                
                if (llmReady) {
                    response = llmService.chat(message, session.getHistory());
                    tokensUsed = response.length();
                } else {
                    response = generateMockResponse(message);
                    tokensUsed = response.length();
                }
            } else {
                response = generateMockResponse(message);
                tokensUsed = response.length();
            }
            
            session.addMessage("assistant", response);
            session.addTokens(tokensUsed);
            
            final int finalTokensUsed = tokensUsed;
            final String finalMessage = message;
            Map<String, Object> usage = new HashMap<>();
            usage.put("promptTokens", finalMessage.length());
            usage.put("completionTokens", finalTokensUsed);
            usage.put("totalTokens", finalMessage.length() + finalTokensUsed);
            
            Map<String, Object> data = new HashMap<>();
            data.put("response", response);
            data.put("sessionId", session.id);
            data.put("model", model);
            data.put("provider", provider);
            data.put("usage", usage);
            
            result.put("status", "success");
            result.put("data", data);
            
        } catch (Exception e) {
            log.error("[chat] Error", e);
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String model,
            @RequestParam(required = false, defaultValue = "0.7") Double temperature,
            @RequestParam(required = false, defaultValue = "4096") Integer maxTokens) {
        
        log.info("[chatStream] message: {}, sessionId: {}", message, sessionId);
        
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    SessionInfo session = getOrCreateSession(sessionId);
                    session.addMessage("user", message);
                    
                    String fullResponse;
                    if (llmService != null) {
                        Map<String, Object> llmStatus = llmService.checkDependencies();
                        boolean llmReady = (Boolean) llmStatus.get("ready");
                        
                        if (llmReady) {
                            fullResponse = llmService.chat(message, session.getHistory());
                        } else {
                            fullResponse = generateMockResponse(message);
                        }
                    } else {
                        fullResponse = generateMockResponse(message);
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
                    
                    session.addMessage("assistant", fullResponse);
                    session.addTokens(fullResponse.length());
                    
                    emitter.send(SseEmitter.event()
                        .name("done")
                        .data("[DONE]"));
                    emitter.complete();
                    
                } catch (IOException e) {
                    log.error("[chatStream] Error sending SSE chunk", e);
                    emitter.completeWithError(e);
                } catch (InterruptedException e) {
                    log.error("[chatStream] SSE interrupted", e);
                    emitter.completeWithError(e);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("[chatStream] Stream execution error", e);
                    emitter.completeWithError(e);
                }
            }
        });
        
        emitter.onCompletion(new Runnable() {
            @Override
            public void run() {
                log.info("[chatStream] SSE completed");
            }
        });
        
        emitter.onTimeout(new Runnable() {
            @Override
            public void run() {
                log.warn("[chatStream] SSE timeout");
                emitter.complete();
            }
        });
        
        return emitter;
    }
    
    @GetMapping("/providers")
    public ResponseEntity<Map<String, Object>> getProviders() {
        log.info("[getProviders] Getting available providers");
        
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> providerList = new ArrayList<>();
        for (ProviderInfo info : providers.values()) {
            Map<String, Object> p = new HashMap<>();
            p.put("id", info.id);
            p.put("name", info.name);
            p.put("models", info.models);
            p.put("supportsStreaming", info.supportsStreaming);
            p.put("supportsFunctionCalling", info.supportsFunctionCalling);
            providerList.add(p);
        }
        
        result.put("status", "success");
        result.put("data", providerList);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/models")
    public ResponseEntity<Map<String, Object>> getModels(@RequestParam(required = false) String provider) {
        log.info("[getModels] provider: {}", provider);
        
        Map<String, Object> result = new HashMap<>();
        
        String providerId = provider != null ? provider : currentProvider;
        ProviderInfo providerInfo = providers.get(providerId);
        
        if (providerInfo != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("provider", providerId);
            data.put("models", providerInfo.models);
            data.put("currentModel", currentModel);
            
            result.put("status", "success");
            result.put("data", data);
        } else {
            result.put("status", "error");
            result.put("message", "Provider not found: " + providerId);
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/models/set")
    public ResponseEntity<Map<String, Object>> setModel(@RequestBody Map<String, Object> request) {
        String modelId = (String) request.get("modelId");
        String providerId = (String) request.get("provider");
        
        log.info("[setModel] modelId: {}, provider: {}", modelId, providerId);
        
        Map<String, Object> result = new HashMap<>();
        
        if (providerId != null && providers.containsKey(providerId)) {
            currentProvider = providerId;
        }
        
        if (modelId != null) {
            currentModel = modelId;
        }
        
        result.put("status", "success");
        result.put("data", new HashMap<String, Object>() {{
            put("provider", currentProvider);
            put("model", currentModel);
        }});
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getSessions() {
        log.info("[getSessions] Getting all sessions");
        
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> sessionList = new ArrayList<>();
        for (SessionInfo session : sessions.values()) {
            Map<String, Object> s = new HashMap<>();
            s.put("id", session.id);
            s.put("title", session.title);
            s.put("messageCount", session.messages.size());
            s.put("totalTokens", session.totalTokens);
            s.put("createTime", session.createTime);
            s.put("updateTime", session.updateTime);
            sessionList.add(s);
        }
        
        sessionList.sort((a, b) -> Long.compare((Long) b.get("updateTime"), (Long) a.get("updateTime")));
        
        result.put("status", "success");
        result.put("data", sessionList);
        
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Map<String, Object>> deleteSession(@PathVariable String sessionId) {
        log.info("[deleteSession] sessionId: {}", sessionId);
        
        Map<String, Object> result = new HashMap<>();
        
        SessionInfo removed = sessions.remove(sessionId);
        
        result.put("status", "success");
        result.put("message", removed != null ? "会话已删除" : "会话不存在");
        result.put("sessionId", sessionId);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/sessions/{sessionId}/history")
    public ResponseEntity<Map<String, Object>> getSessionHistory(@PathVariable String sessionId) {
        log.info("[getSessionHistory] sessionId: {}", sessionId);
        
        Map<String, Object> result = new HashMap<>();
        
        SessionInfo session = sessions.get(sessionId);
        
        if (session != null) {
            result.put("status", "success");
            result.put("data", session.messages);
        } else {
            result.put("status", "error");
            result.put("message", "会话不存在");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.info("[health] LLM health check");
        
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("healthy", true);
        data.put("currentProvider", currentProvider);
        data.put("currentModel", currentModel);
        data.put("providerCount", providers.size());
        data.put("sessionCount", sessions.size());
        data.put("llmServiceReady", llmService != null);
        
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    private SessionInfo getOrCreateSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = "session_" + System.currentTimeMillis();
        }
        
        SessionInfo session = sessions.get(sessionId);
        if (session == null) {
            session = new SessionInfo(sessionId);
            sessions.put(sessionId, session);
        }
        
        return session;
    }
    
    private String generateMockResponse(String prompt) {
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
            response.append("- 进行自然语言对话\n");
            response.append("- 回答问题和提供建议\n");
            response.append("- 生成代码和文档\n");
            response.append("- 分析和处理文本\n\n");
            response.append("请问有什么可以帮您的？");
        }
        
        return response.toString();
    }
    
    private static class SessionInfo {
        String id;
        String title;
        List<Map<String, Object>> messages;
        long createTime;
        long updateTime;
        int totalTokens;
        
        SessionInfo(String id) {
            this.id = id;
            this.title = "新对话";
            this.messages = new ArrayList<>();
            this.createTime = System.currentTimeMillis();
            this.updateTime = this.createTime;
            this.totalTokens = 0;
        }
        
        void addMessage(String role, String content) {
            Map<String, Object> msg = new HashMap<>();
            msg.put("role", role);
            msg.put("content", content);
            msg.put("timestamp", System.currentTimeMillis());
            messages.add(msg);
            updateTime = System.currentTimeMillis();
            
            if (messages.size() == 1 && "user".equals(role)) {
                title = content.length() > 20 ? content.substring(0, 20) + "..." : content;
            }
        }
        
        void addTokens(int tokens) {
            totalTokens += tokens;
        }
        
        List<Map<String, Object>> getHistory() {
            return new ArrayList<>(messages);
        }
    }
    
    private static class ProviderInfo {
        String id;
        String name;
        List<String> models;
        boolean supportsStreaming;
        boolean supportsFunctionCalling;
        
        ProviderInfo(String id, String name, List<String> models, boolean supportsStreaming, boolean supportsFunctionCalling) {
            this.id = id;
            this.name = name;
            this.models = models;
            this.supportsStreaming = supportsStreaming;
            this.supportsFunctionCalling = supportsFunctionCalling;
        }
    }
}
