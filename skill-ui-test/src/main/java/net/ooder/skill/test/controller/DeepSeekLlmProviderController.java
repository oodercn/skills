package net.ooder.skill.test.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/llm/deepseek")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DeepSeekLlmProviderController {
    
    private static final Logger log = LoggerFactory.getLogger(DeepSeekLlmProviderController.class);
    private static final long SSE_TIMEOUT = 120000L;
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Value("${deepseek.api-key:}")
    private String apiKey;
    
    @Value("${deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;
    
    @Value("${deepseek.model:deepseek-chat}")
    private String defaultModel;
    
    @Value("${deepseek.timeout:60000}")
    private int timeout;
    
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String model = (String) request.getOrDefault("model", defaultModel);
        Double temperature = (Double) request.getOrDefault("temperature", 0.7);
        Integer maxTokens = (Integer) request.getOrDefault("maxTokens", 4096);
        
        log.info("[deepseek/chat] model: {}, message: {}", model, message);
        
        Map<String, Object> result = new HashMap<>();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            result.put("status", "error");
            result.put("error", "API Key未配置");
            result.put("provider", "deepseek");
            return ResponseEntity.ok(result);
        }
        
        try {
            String response = callDeepSeekApi(message, model, temperature, maxTokens);
            result.put("status", "success");
            result.put("response", response);
            result.put("provider", "deepseek");
            result.put("model", model);
        } catch (Exception e) {
            log.error("[deepseek/chat] Error", e);
            result.put("status", "error");
            result.put("error", e.getMessage());
            result.put("provider", "deepseek");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String model = (String) request.getOrDefault("model", defaultModel);
        
        log.info("[deepseek/chat/stream] model: {}, message: {}", model, message);
        
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        
        executor.execute(() -> {
            try {
                String response = callDeepSeekApi(message, model, 0.7, 4096);
                
                int chunkSize = 8;
                for (int i = 0; i < response.length(); i += chunkSize) {
                    int end = Math.min(i + chunkSize, response.length());
                    String chunk = response.substring(i, end);
                    
                    emitter.send(SseEmitter.event()
                        .name("message")
                        .data(chunk));
                    
                    Thread.sleep(30);
                }
                
                emitter.send(SseEmitter.event()
                    .name("done")
                    .data("[DONE]"));
                emitter.complete();
                
            } catch (Exception e) {
                log.error("[deepseek/chat/stream] Error", e);
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }
    
    @GetMapping("/models")
    public ResponseEntity<Map<String, Object>> getModels() {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> models = new ArrayList<>();
        
        Map<String, Object> chat = new HashMap<>();
        chat.put("id", "deepseek-chat");
        chat.put("name", "DeepSeek Chat");
        chat.put("type", "chat");
        chat.put("maxTokens", 64000);
        models.add(chat);
        
        Map<String, Object> coder = new HashMap<>();
        coder.put("id", "deepseek-coder");
        coder.put("name", "DeepSeek Coder");
        coder.put("type", "chat");
        coder.put("maxTokens", 16000);
        models.add(coder);
        
        Map<String, Object> reasoner = new HashMap<>();
        reasoner.put("id", "deepseek-reasoner");
        reasoner.put("name", "DeepSeek Reasoner");
        reasoner.put("type", "chat");
        reasoner.put("maxTokens", 64000);
        models.add(reasoner);
        
        result.put("status", "success");
        result.put("provider", "deepseek");
        result.put("models", models);
        result.put("defaultModel", defaultModel);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("healthy", apiKey != null && !apiKey.trim().isEmpty());
        data.put("provider", "deepseek");
        data.put("model", defaultModel);
        data.put("baseUrl", baseUrl);
        data.put("apiKeyConfigured", apiKey != null && !apiKey.trim().isEmpty());
        
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    private String callDeepSeekApi(String message, String model, Double temperature, Integer maxTokens) throws Exception {
        URL url = new URL(baseUrl + "/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        
        String requestBody = String.format(
            "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":%s}],\"temperature\":%.1f,\"max_tokens\":%d}",
            model,
            escapeJson(message),
            temperature,
            maxTokens
        );
        
        log.debug("[deepseek] Request: {}", requestBody);
        
        try (java.io.OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes("UTF-8"));
        }
        
        int responseCode = conn.getResponseCode();
        log.info("[deepseek] Response code: {}", responseCode);
        
        if (responseCode == 200) {
            String response = readResponse(conn.getInputStream());
            conn.disconnect();
            return extractContent(response);
        } else {
            String errorResponse = readResponse(conn.getErrorStream());
            conn.disconnect();
            throw new RuntimeException("API错误 " + responseCode + ": " + errorResponse);
        }
    }
    
    private String extractContent(String response) {
        try {
            int contentStart = response.indexOf("\"content\":\"");
            if (contentStart > 0) {
                contentStart += 11;
                int contentEnd = response.indexOf("\"", contentStart);
                if (contentEnd > contentStart) {
                    String content = response.substring(contentStart, contentEnd);
                    return content
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");
                }
            }
        } catch (Exception e) {
            log.warn("[deepseek] Failed to extract content", e);
        }
        return response;
    }
    
    private String readResponse(java.io.InputStream is) throws IOException {
        if (is == null) return "";
        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(is, "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
    
    private String escapeJson(String text) {
        if (text == null) return "null";
        return "\"" + text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            + "\"";
    }
}
