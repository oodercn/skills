package net.ooder.skill.llm.service;

import net.ooder.skill.llm.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LlmServiceImpl implements LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmServiceImpl.class);

    private final Map<String, LlmProvider> providers = new ConcurrentHashMap<>();
    private final Map<String, String> apiKeys = new ConcurrentHashMap<>();

    public LlmServiceImpl() {
        initDefaultProviders();
    }

    private void initDefaultProviders() {
        LlmProvider deepseek = new LlmProvider();
        deepseek.setProviderId("deepseek");
        deepseek.setName("DeepSeek");
        deepseek.setType("deepseek");
        deepseek.setEndpoint("https://api.deepseek.com/v1");
        deepseek.setModels(Arrays.asList("deepseek-chat", "deepseek-coder"));
        deepseek.setEnabled(false);
        providers.put(deepseek.getProviderId(), deepseek);
    }

    public void configureProvider(String providerId, String apiKey, String baseUrl) {
        LlmProvider provider = providers.get(providerId);
        if (provider != null) {
            provider.setEnabled(true);
            if (baseUrl != null && !baseUrl.isEmpty()) {
                provider.setEndpoint(baseUrl);
            }
            apiKeys.put(providerId, apiKey);
            log.info("Configured provider: {} with endpoint: {}", providerId, provider.getEndpoint());
        }
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        log.info("Processing chat request: {}", request.getMessage());

        LlmProvider provider = getActiveProvider();
        if (provider == null || !provider.isEnabled()) {
            return createMockResponse(request.getMessage());
        }

        String apiKey = apiKeys.get(provider.getProviderId());
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("No API key configured for provider: {}", provider.getProviderId());
            return createMockResponse(request.getMessage());
        }

        try {
            return callLlmApi(provider, apiKey, request);
        } catch (Exception e) {
            log.error("LLM API call failed", e);
            ChatResponse response = new ChatResponse();
            response.setContent("抱歉，LLM API 调用失败: " + e.getMessage());
            response.setProviderId(provider.getProviderId());
            return response;
        }
    }

    private ChatResponse callLlmApi(LlmProvider provider, String apiKey, ChatRequest request) throws IOException {
        String endpoint = provider.getEndpoint();
        String url = endpoint + "/chat/completions";

        log.info("Calling LLM API: {} with model: {}", url, request.getModel());

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);

        String model = request.getModel();
        if (model == null || model.isEmpty()) {
            model = provider.getModels().get(0);
        }

        StringBuilder jsonBody = new StringBuilder();
        jsonBody.append("{\"model\":\"").append(model).append("\",");
        jsonBody.append("\"messages\":[");
        jsonBody.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(request.getMessage())).append("\"}");
        jsonBody.append("],\"max_tokens\":2048,\"temperature\":0.7}");

        log.debug("Request body: {}", jsonBody);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.toString().getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        log.info("LLM API response code: {}", responseCode);

        if (responseCode != 200) {
            String errorResponse = readStream(conn.getErrorStream());
            log.error("LLM API error: {}", errorResponse);
            throw new IOException("LLM API returned " + responseCode + ": " + errorResponse);
        }

        String response = readStream(conn.getInputStream());
        log.debug("LLM API response: {}", response);

        return parseResponse(response, provider.getProviderId(), model);
    }

    private ChatResponse parseResponse(String jsonResponse, String providerId, String model) {
        ChatResponse response = new ChatResponse();
        response.setProviderId(providerId);
        response.setModel(model);

        try {
            int contentStart = jsonResponse.indexOf("\"content\":\"");
            if (contentStart > 0) {
                contentStart += 11;
                int contentEnd = jsonResponse.indexOf("\"", contentStart);
                if (contentEnd > contentStart) {
                    String content = jsonResponse.substring(contentStart, contentEnd);
                    content = content.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
                    response.setContent(content);
                }
            }
            response.setTokensUsed(100);
            response.setLatencyMs(0L);
        } catch (Exception e) {
            log.error("Failed to parse LLM response", e);
            response.setContent("Failed to parse response: " + e.getMessage());
        }

        return response;
    }

    private String readStream(InputStream is) throws IOException {
        if (is == null) return "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private LlmProvider getActiveProvider() {
        for (LlmProvider provider : providers.values()) {
            if (provider.isEnabled()) {
                return provider;
            }
        }
        return null;
    }

    private ChatResponse createMockResponse(String message) {
        ChatResponse response = new ChatResponse();
        response.setContent("这是一个模拟的LLM响应。请配置真实的LLM提供者（如 DeepSeek）以获得真实回复。配置方法：调用 POST /api/v1/llm/providers/{providerId}/configure");
        response.setProviderId("mock");
        response.setModel("mock-model");
        response.setTokensUsed(100);
        response.setLatencyMs(50L);
        return response;
    }

    @Override
    public SseEmitter streamChat(ChatRequest request) {
        SseEmitter emitter = new SseEmitter(60000L);

        new Thread(() -> {
            try {
                ChatResponse response = chat(request);
                String content = response.getContent();
                
                String[] chars = content.split("");
                for (String c : chars) {
                    emitter.send(SseEmitter.event().data(c));
                    Thread.sleep(30);
                }

                emitter.complete();
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    @Override
    public List<Map<String, Object>> listAvailableModels() {
        List<Map<String, Object>> models = new ArrayList<>();

        for (LlmProvider provider : providers.values()) {
            for (String modelId : provider.getModels()) {
                Map<String, Object> model = new HashMap<>();
                model.put("id", modelId);
                model.put("name", modelId);
                model.put("provider", provider.getProviderId());
                model.put("enabled", provider.isEnabled());
                models.add(model);
            }
        }

        return models;
    }

    public void enableProvider(String providerId) {
        LlmProvider provider = providers.get(providerId);
        if (provider != null) {
            provider.setEnabled(true);
            log.info("Enabled provider: {}", providerId);
        }
    }

    public List<LlmProvider> listProviders() {
        return new ArrayList<>(providers.values());
    }

    public LlmProvider getProvider(String providerId) {
        return providers.get(providerId);
    }

    public void addProvider(LlmProvider provider) {
        providers.put(provider.getProviderId(), provider);
        log.info("Added provider: {}", provider.getProviderId());
    }
}
