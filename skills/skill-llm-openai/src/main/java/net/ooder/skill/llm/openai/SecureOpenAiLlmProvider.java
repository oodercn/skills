package net.ooder.skill.llm.openai;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.security.integration.LlmSecurityIntegration;
import net.ooder.scene.skill.LlmProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class SecureOpenAiLlmProvider implements LlmProvider {
    
    @Autowired(required = false)
    private LlmSecurityIntegration securityIntegration;
    
    private String baseUrl = "https://api.openai.com/v1";
    
    @Override
    public String getProviderType() {
        return "openai-secure";
    }
    
    @Override
    public List<String> getSupportedModels() {
        return Arrays.asList(
            "gpt-4", "gpt-4-turbo", "gpt-4o", "gpt-4o-mini",
            "gpt-3.5-turbo", "gpt-3.5-turbo-16k"
        );
    }
    
    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        String apiKey = getApiKey(options);
        
        if (apiKey == null) {
            log.warn("No API key available for OpenAI");
            return createErrorResponse("API key not available");
        }
        
        log.info("OpenAI secure chat: model={}, messages={}", model, messages.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", "chatcmpl-" + UUID.randomUUID().toString().substring(0, 8));
        result.put("object", "chat.completion");
        result.put("created", System.currentTimeMillis() / 1000);
        result.put("model", model);
        
        Map<String, Object> choice = new HashMap<>();
        choice.put("index", 0);
        Map<String, Object> message = new HashMap<>();
        message.put("role", "assistant");
        message.put("content", "This is a secure response using managed API key.");
        choice.put("message", message);
        choice.put("finish_reason", "stop");
        result.put("choices", Collections.singletonList(choice));
        
        Map<String, Object> usage = new HashMap<>();
        usage.put("prompt_tokens", 10);
        usage.put("completion_tokens", 20);
        usage.put("total_tokens", 30);
        result.put("usage", usage);
        
        if (securityIntegration != null) {
            String userId = (String) options.get("userId");
            securityIntegration.logLlmCall(userId, "openai", model, 30, true);
        }
        
        return result;
    }
    
    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        String apiKey = getApiKey(options);
        
        if (apiKey == null) {
            log.warn("No API key available for OpenAI");
            return "Error: API key not available";
        }
        
        log.info("OpenAI secure completion: model={}", model);
        return "Secure completion for: " + prompt.substring(0, Math.min(50, prompt.length()));
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
        
        log.info("OpenAI embeddings: model={}, texts={}", model, texts.size());
        return embeddings;
    }
    
    @Override
    public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
        log.info("OpenAI translate: model={}, target={}", model, targetLanguage);
        return "[Translated to " + targetLanguage + "]: " + text;
    }
    
    @Override
    public String summarize(String model, String text, int maxLength) {
        log.info("OpenAI summarize: model={}, maxLength={}", model, maxLength);
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
            log.debug("Security integration not available, falling back to environment variable");
            return System.getenv("OPENAI_API_KEY");
        }
        
        String userId = options != null ? (String) options.get("userId") : null;
        String sceneId = options != null ? (String) options.get("sceneId") : null;
        
        try {
            return securityIntegration.getLlmApiKey("openai", userId, sceneId);
        } catch (Exception e) {
            log.error("Failed to get API key from security integration", e);
            return null;
        }
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("error", message);
        result.put("code", "API_KEY_UNAVAILABLE");
        return result;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
