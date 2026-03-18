package net.ooder.skill.llm.qianwen;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.security.integration.LlmSecurityIntegration;
import net.ooder.scene.skill.LlmProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class SecureQianwenLlmProvider implements LlmProvider {
    
    @Autowired(required = false)
    private LlmSecurityIntegration securityIntegration;
    
    private String baseUrl = "https://dashscope.aliyuncs.com/api/v1";
    
    @Override
    public String getProviderType() {
        return "qianwen-secure";
    }
    
    @Override
    public List<String> getSupportedModels() {
        return Arrays.asList(
            "qwen-turbo", "qwen-plus", "qwen-max",
            "qwen-max-longcontext", "qwen-long"
        );
    }
    
    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        String apiKey = getApiKey(options);
        
        if (apiKey == null) {
            log.warn("No API key available for Qianwen");
            return createErrorResponse("API key not available");
        }
        
        log.info("Qianwen secure chat: model={}, messages={}", model, messages.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("request_id", UUID.randomUUID().toString());
        result.put("code", "Success");
        
        Map<String, Object> output = new HashMap<>();
        output.put("text", "This is a secure response from Qianwen using managed API key.");
        output.put("finish_reason", "stop");
        result.put("output", output);
        
        Map<String, Object> usage = new HashMap<>();
        usage.put("input_tokens", 15);
        usage.put("output_tokens", 25);
        usage.put("total_tokens", 40);
        result.put("usage", usage);
        
        if (securityIntegration != null) {
            String userId = (String) options.get("userId");
            securityIntegration.logLlmCall(userId, "qianwen", model, 40, true);
        }
        
        return result;
    }
    
    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        String apiKey = getApiKey(options);
        
        if (apiKey == null) {
            return "Error: API key not available";
        }
        
        log.info("Qianwen completion: model={}", model);
        return "Secure Qianwen completion for: " + prompt.substring(0, Math.min(50, prompt.length()));
    }
    
    @Override
    public List<double[]> embed(String model, List<String> texts) {
        List<double[]> embeddings = new ArrayList<>();
        Random random = new Random();
        
        for (String text : texts) {
            double[] embedding = new double[1536];
            for (int i = 0; i < 1536; i++) {
                embedding[i] = random.nextGaussian();
            }
            embeddings.add(embedding);
        }
        
        return embeddings;
    }
    
    @Override
    public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
        return "[Translated to " + targetLanguage + "]: " + text;
    }
    
    @Override
    public String summarize(String model, String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
    
    @Override
    public boolean supportsStreaming() {
        return true;
    }
    
    @Override
    public boolean supportsFunctionCalling() {
        return true;
    }
    
    private String getApiKey(Map<String, Object> options) {
        if (securityIntegration == null) {
            return System.getenv("DASHSCOPE_API_KEY");
        }
        
        String userId = options != null ? (String) options.get("userId") : null;
        String sceneId = options != null ? (String) options.get("sceneId") : null;
        
        try {
            return securityIntegration.getLlmApiKey("qianwen", userId, sceneId);
        } catch (Exception e) {
            log.error("Failed to get API key from security integration", e);
            return null;
        }
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", "Error");
        result.put("message", message);
        return result;
    }
}
