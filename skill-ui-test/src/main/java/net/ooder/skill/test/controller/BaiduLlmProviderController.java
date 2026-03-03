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
@RequestMapping("/api/llm/baidu")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BaiduLlmProviderController {
    
    private static final Logger log = LoggerFactory.getLogger(BaiduLlmProviderController.class);
    private static final long SSE_TIMEOUT = 120000L;
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Value("${ooder.llm.api-key:}")
    private String apiKey;
    
    @Value("${ooder.llm.base-url:https://qianfan.baidubce.com/v2}")
    private String baseUrl;
    
    @Value("${ooder.llm.model:ernie-4.0-8k}")
    private String defaultModel;
    
    @Value("${ooder.llm.embedding-model:embedding-v1}")
    private String embeddingModel;
    
    @Value("${ooder.llm.timeout:60000}")
    private int timeout;
    
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String model = (String) request.getOrDefault("model", defaultModel);
        Double temperature = (Double) request.getOrDefault("temperature", 0.7);
        Integer maxTokens = (Integer) request.getOrDefault("maxTokens", 4096);
        
        log.info("[baidu/chat] model: {}, message: {}", model, message);
        
        Map<String, Object> result = new HashMap<>();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            result.put("status", "error");
            result.put("error", "API Key未配置");
            result.put("provider", "baidu");
            return ResponseEntity.ok(result);
        }
        
        try {
            String response = callBaiduApi(message, model, temperature, maxTokens);
            result.put("status", "success");
            result.put("response", response);
            result.put("provider", "baidu");
            result.put("model", model);
        } catch (Exception e) {
            log.error("[baidu/chat] Error", e);
            result.put("status", "error");
            result.put("error", e.getMessage());
            result.put("provider", "baidu");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String model = (String) request.getOrDefault("model", defaultModel);
        
        log.info("[baidu/chat/stream] model: {}, message: {}", model, message);
        
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        
        executor.execute(() -> {
            try {
                String response = callBaiduApi(message, model, 0.7, 4096);
                
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
                log.error("[baidu/chat/stream] Error", e);
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }
    
    @PostMapping("/embed")
    public ResponseEntity<Map<String, Object>> embed(@RequestBody Map<String, Object> request) {
        String text = (String) request.get("text");
        String model = (String) request.getOrDefault("model", embeddingModel);
        
        log.info("[baidu/embed] model: {}, text length: {}", model, text != null ? text.length() : 0);
        
        Map<String, Object> result = new HashMap<>();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            result.put("status", "error");
            result.put("error", "API Key未配置");
            return ResponseEntity.ok(result);
        }
        
        try {
            URL url = new URL(baseUrl + "/embeddings");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            
            String requestBody = String.format(
                "{\"model\":\"%s\",\"input\":%s}",
                model,
                escapeJson(text)
            );
            
            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("UTF-8"));
            }
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                String response = readResponse(conn.getInputStream());
                result.put("status", "success");
                result.put("response", response);
            } else {
                String errorResponse = readResponse(conn.getErrorStream());
                result.put("status", "error");
                result.put("error", "API返回错误: " + responseCode);
                result.put("details", errorResponse);
            }
            
            conn.disconnect();
        } catch (Exception e) {
            log.error("[baidu/embed] Error", e);
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/models")
    public ResponseEntity<Map<String, Object>> getModels() {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> models = new ArrayList<>();
        
        Map<String, Object> ernie4 = new HashMap<>();
        ernie4.put("id", "ernie-4.0-8k");
        ernie4.put("name", "ERNIE 4.0 8K");
        ernie4.put("type", "chat");
        ernie4.put("maxTokens", 8192);
        models.add(ernie4);
        
        Map<String, Object> ernie35 = new HashMap<>();
        ernie35.put("id", "ernie-3.5-8k");
        ernie35.put("name", "ERNIE 3.5 8K");
        ernie35.put("type", "chat");
        ernie35.put("maxTokens", 8192);
        models.add(ernie35);
        
        Map<String, Object> embed = new HashMap<>();
        embed.put("id", "embedding-v1");
        embed.put("name", "Embedding V1");
        embed.put("type", "embedding");
        embed.put("maxTokens", 4096);
        models.add(embed);
        
        result.put("status", "success");
        result.put("provider", "baidu");
        result.put("models", models);
        result.put("defaultModel", defaultModel);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("healthy", apiKey != null && !apiKey.trim().isEmpty());
        data.put("provider", "baidu");
        data.put("model", defaultModel);
        data.put("baseUrl", baseUrl);
        data.put("apiKeyConfigured", apiKey != null && !apiKey.trim().isEmpty());
        
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    private String callBaiduApi(String message, String model, Double temperature, Integer maxTokens) throws Exception {
        int actualMaxTokens = Math.min(maxTokens, 2048);
        
        URL url = new URL(baseUrl + "/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        
        String requestBody = String.format(
            "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":%s}],\"temperature\":%.1f,\"max_completion_tokens\":%d}",
            model,
            escapeJson(message),
            temperature,
            actualMaxTokens
        );
        
        log.debug("[baidu] Request: {}", requestBody);
        
        try (java.io.OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes("UTF-8"));
        }
        
        int responseCode = conn.getResponseCode();
        log.info("[baidu] Response code: {}", responseCode);
        
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
            log.warn("[baidu] Failed to extract content", e);
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
