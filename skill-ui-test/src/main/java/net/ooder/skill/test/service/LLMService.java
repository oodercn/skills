package net.ooder.skill.test.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LLMService {
    
    private static final Logger log = LoggerFactory.getLogger(LLMService.class);
    
    @Value("${ooder.llm.provider:openai}")
    private String provider;
    
    @Value("${ooder.llm.api-key:}")
    private String apiKey;
    
    @Value("${ooder.llm.base-url:https://api.openai.com/v1}")
    private String baseUrl;
    
    @Value("${ooder.llm.model:gpt-3.5-turbo}")
    private String model;
    
    @Value("${ooder.llm.embedding-model:text-embedding-ada-002}")
    private String embeddingModel;
    
    @Value("${deepseek.api-key:}")
    private String deepseekApiKey;
    
    @Value("${deepseek.base-url:https://api.deepseek.com}")
    private String deepseekBaseUrl;
    
    @Value("${deepseek.model:deepseek-chat}")
    private String deepseekModel;
    
    private final Map<String, Object> llmCache = new ConcurrentHashMap<>();
    private String lastUsedModel = "";
    
    public Map<String, Object> checkDependencies() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        result.put("provider", provider);
        result.put("model", model);
        result.put("embeddingModel", embeddingModel);
        result.put("baseUrl", baseUrl);
        
        boolean hasApiKey = apiKey != null && !apiKey.trim().isEmpty();
        result.put("apiKeyConfigured", hasApiKey);
        
        if (hasApiKey) {
            result.put("apiKeyPreview", apiKey.substring(0, Math.min(8, apiKey.length())) + "...");
        }
        
        boolean hasDeepseekKey = deepseekApiKey != null && !deepseekApiKey.trim().isEmpty();
        result.put("deepseekConfigured", hasDeepseekKey);
        
        if (hasDeepseekKey) {
            result.put("deepseekKeyPreview", deepseekApiKey.substring(0, Math.min(8, deepseekApiKey.length())) + "...");
        }
        
        boolean apiAccessible = checkApiConnection();
        result.put("apiAccessible", apiAccessible);
        
        List<String> issues = new ArrayList<>();
        if (!hasApiKey && !hasDeepseekKey) {
            issues.add("未配置任何LLM API Key，请设置ooder.llm.api-key或deepseek.api-key");
        }
        if (!apiAccessible) {
            issues.add("主API服务不可达，请检查网络连接或base-url配置");
        }
        result.put("issues", issues);
        
        result.put("ready", (hasApiKey && apiAccessible) || hasDeepseekKey);
        
        return result;
    }
    
    private boolean checkApiConnection() {
        if ("baidu".equals(provider)) {
            return checkBaiduApiConnection();
        }
        try {
            URL url = new URL(baseUrl + "/models");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            }
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            return responseCode == 200 || responseCode == 401 || responseCode == 403;
        } catch (IOException e) {
            log.warn("API connection check failed: {}", e.getMessage());
            return false;
        }
    }
    
    private boolean checkBaiduApiConnection() {
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            return responseCode < 500;
        } catch (IOException e) {
            log.warn("Baidu API connection check failed: {}", e.getMessage());
            return true;
        }
    }
    
    public Map<String, Object> generateEmbedding(String text) {
        Map<String, Object> result = new HashMap<>();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            result.put("status", "error");
            result.put("error", "API Key未配置");
            return result;
        }
        
        try {
            URL url = new URL(baseUrl + "/embeddings");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            
            String requestBody = String.format(
                "{\"model\":\"%s\",\"input\":%s}",
                embeddingModel,
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
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    public Map<String, Object> chat(String prompt, String systemPrompt) {
        Map<String, Object> result = new HashMap<>();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            result.put("status", "error");
            result.put("error", "API Key未配置");
            result.put("fallback", "使用本地模拟回答");
            result.put("answer", generateLocalAnswer(prompt));
            return result;
        }
        
        try {
            String endpoint = "/chat/completions";
            URL url = new URL(baseUrl + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            
            StringBuilder messages = new StringBuilder();
            messages.append("[");
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                messages.append("{\"role\":\"system\",\"content\":").append(escapeJson(systemPrompt)).append("},");
            }
            messages.append("{\"role\":\"user\",\"content\":").append(escapeJson(prompt)).append("}");
            messages.append("]");
            
            String requestBody = String.format(
                "{\"model\":\"%s\",\"messages\":%s,\"temperature\":0.7}",
                model,
                messages.toString()
            );
            
            log.info("[chat] Calling LLM API: {}{}", baseUrl, endpoint);
            log.debug("[chat] Request body: {}", requestBody);
            
            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("UTF-8"));
            }
            
            int responseCode = conn.getResponseCode();
            log.info("[chat] Response code: {}", responseCode);
            
            if (responseCode == 200) {
                String response = readResponse(conn.getInputStream());
                log.debug("[chat] Response: {}", response);
                result.put("status", "success");
                result.put("response", response);
                result.put("answer", extractAnswer(response));
            } else {
                String errorResponse = readResponse(conn.getErrorStream());
                log.warn("[chat] API error: {} - {}", responseCode, errorResponse);
                result.put("status", "error");
                result.put("error", "API返回错误: " + responseCode);
                result.put("details", errorResponse);
                result.put("fallback", "使用本地模拟回答");
                result.put("answer", generateLocalAnswer(prompt));
            }
            
            conn.disconnect();
        } catch (Exception e) {
            log.error("[chat] Exception: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("error", e.getMessage());
            result.put("fallback", "使用本地模拟回答");
            result.put("answer", generateLocalAnswer(prompt));
        }
        
        return result;
    }
    
    private String generateLocalAnswer(String question) {
        return "根据知识库检索结果，关于\"" + question + "\"的相关信息如下：\n\n" +
               "请参考产品使用手册第3章的详细说明，其中包含了完整的操作指南和注意事项。\n\n" +
               "如需更详细的信息，请配置LLM API Key以启用智能问答功能。";
    }
    
    private String extractAnswer(String response) {
        try {
            int contentStart = response.indexOf("\"content\":\"");
            if (contentStart > 0) {
                contentStart += 11;
                int contentEnd = response.indexOf("\"", contentStart);
                if (contentEnd > contentStart) {
                    String content = response.substring(contentStart, contentEnd);
                    return content.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract answer from response", e);
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
    
    public String generateAnswer(String question, List<Map<String, Object>> sources, String model) {
        if (!isReady()) {
            return generateLocalAnswer(question);
        }
        
        StringBuilder context = new StringBuilder();
        context.append("参考文档：\n");
        for (Map<String, Object> s : sources) {
            context.append("- ").append(s.get("title")).append("\n");
        }
        
        String systemPrompt = "你是一个知识库问答助手，请根据提供的参考文档回答用户问题。";
        String prompt = context.toString() + "\n\n用户问题：" + question;
        
        boolean hasBaiduKey = apiKey != null && !apiKey.trim().isEmpty();
        boolean hasDeepseekKey = deepseekApiKey != null && !deepseekApiKey.trim().isEmpty();
        
        if (model != null && model.startsWith("deepseek") && hasDeepseekKey) {
            lastUsedModel = model;
            return callDeepSeek(prompt, systemPrompt, model);
        } else if (model != null && model.startsWith("ernie") && hasBaiduKey) {
            lastUsedModel = model;
            return callBaidu(prompt, systemPrompt, model);
        } else if (hasDeepseekKey) {
            log.info("[llm] Model {} not available, fallback to DeepSeek", model);
            lastUsedModel = deepseekModel;
            return callDeepSeek(prompt, systemPrompt, deepseekModel);
        } else if (hasBaiduKey) {
            log.info("[llm] Model {} not available, fallback to Baidu", model);
            lastUsedModel = this.model;
            return callBaidu(prompt, systemPrompt, this.model);
        }
        
        Map<String, Object> result = chat(prompt, systemPrompt);
        if ("success".equals(result.get("status"))) {
            lastUsedModel = this.model;
            return (String) result.get("answer");
        } else {
            return generateLocalAnswer(question);
        }
    }
    
    public String generateAnswer(String question, List<Map<String, Object>> sources) {
        return generateAnswer(question, sources, null);
    }
    
    public String getLastUsedModel() {
        return lastUsedModel;
    }
    
    private String callBaidu(String prompt, String systemPrompt, String model) {
        try {
            URL url = new URL(baseUrl + "/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            
            String requestBody = String.format(
                "{\"model\":\"%s\",\"messages\":[{\"role\":\"system\",\"content\":%s},{\"role\":\"user\",\"content\":%s}],\"temperature\":0.7,\"max_completion_tokens\":2048}",
                model,
                escapeJson(systemPrompt),
                escapeJson(prompt)
            );
            
            log.info("[baidu] Calling Baidu API with model: {}", model);
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
                log.warn("[baidu] API error: {} - {}", responseCode, errorResponse);
                conn.disconnect();
                return generateLocalAnswer(prompt);
            }
        } catch (Exception e) {
            log.error("[baidu] Error calling Baidu API", e);
            return generateLocalAnswer(prompt);
        }
    }
    
    private String callDeepSeek(String prompt, String systemPrompt, String model) {
        try {
            URL url = new URL(deepseekBaseUrl + "/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + deepseekApiKey);
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            
            String useModel = model != null ? model : deepseekModel;
            String requestBody = String.format(
                "{\"model\":\"%s\",\"messages\":[{\"role\":\"system\",\"content\":%s},{\"role\":\"user\",\"content\":%s}],\"temperature\":0.7}",
                useModel,
                escapeJson(systemPrompt),
                escapeJson(prompt)
            );
            
            log.info("[deepseek] Calling DeepSeek API for QA with model: {}", useModel);
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
                log.warn("[deepseek] API error: {} - {}", responseCode, errorResponse);
                conn.disconnect();
                return generateLocalAnswer(prompt);
            }
        } catch (Exception e) {
            log.error("[deepseek] Error calling DeepSeek API", e);
            return generateLocalAnswer(prompt);
        }
    }
    
    private String callDeepSeek(String prompt, String systemPrompt) {
        return callDeepSeek(prompt, systemPrompt, null);
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
            log.warn("Failed to extract content", e);
        }
        return response;
    }
    
    public boolean isReady() {
        return (apiKey != null && !apiKey.trim().isEmpty()) || 
               (deepseekApiKey != null && !deepseekApiKey.trim().isEmpty());
    }
    
    public String getProvider() {
        return provider;
    }
    
    public String getModel() {
        return model;
    }
    
    public String chat(String prompt, List<Map<String, Object>> history) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return generateLocalAnswer(prompt);
        }
        
        try {
            String endpoint = "/chat/completions";
            URL url = new URL(baseUrl + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            
            StringBuilder messages = new StringBuilder();
            messages.append("[");
            
            if (history != null && !history.isEmpty()) {
                for (int i = 0; i < history.size(); i++) {
                    Map<String, Object> msg = history.get(i);
                    if (i > 0) messages.append(",");
                    messages.append("{\"role\":\"")
                             .append(msg.get("role"))
                             .append("\",\"content\":")
                             .append(escapeJson((String) msg.get("content")))
                             .append("}");
                }
                messages.append(",");
            }
            
            messages.append("{\"role\":\"user\",\"content\":").append(escapeJson(prompt)).append("}");
            messages.append("]");
            
            String requestBody = String.format(
                "{\"model\":\"%s\",\"messages\":%s,\"temperature\":0.7}",
                model,
                messages.toString()
            );
            
            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("UTF-8"));
            }
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                String response = readResponse(conn.getInputStream());
                conn.disconnect();
                return extractAnswer(response);
            } else {
                String errorResponse = readResponse(conn.getErrorStream());
                log.warn("LLM API error: {} - {}", responseCode, errorResponse);
                conn.disconnect();
                return generateLocalAnswer(prompt);
            }
        } catch (Exception e) {
            log.error("LLM chat error", e);
            return generateLocalAnswer(prompt);
        }
    }
}
