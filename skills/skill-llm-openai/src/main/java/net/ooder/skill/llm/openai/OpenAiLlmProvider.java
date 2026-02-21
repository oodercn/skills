package net.ooder.skill.llm.openai;

import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.skill.LlmProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class OpenAiLlmProvider implements LlmProvider {
    
    private String apiKey;
    private String baseUrl = "https://api.openai.com/v1";
    
    public OpenAiLlmProvider() {
        this.apiKey = System.getenv("OPENAI_API_KEY");
    }
    
    public OpenAiLlmProvider(String apiKey) {
        this.apiKey = apiKey;
    }
    
    @Override
    public String getProviderType() {
        return "openai";
    }
    
    @Override
    public List<String> getSupportedModels() {
        return Arrays.asList(
            "gpt-4", "gpt-4-turbo", "gpt-4o", "gpt-4o-mini",
            "gpt-3.5-turbo", "gpt-3.5-turbo-16k",
            "text-embedding-ada-002", "text-embedding-3-small", "text-embedding-3-large"
        );
    }
    
    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        Map<String, Object> result = new HashMap<>();
        
        result.put("id", "chatcmpl-" + UUID.randomUUID().toString().substring(0, 8));
        result.put("object", "chat.completion");
        result.put("created", System.currentTimeMillis() / 1000);
        result.put("model", model);
        
        Map<String, Object> choice = new HashMap<>();
        choice.put("index", 0);
        Map<String, Object> message = new HashMap<>();
        message.put("role", "assistant");
        message.put("content", "This is a mock response. Please configure OPENAI_API_KEY for real responses.");
        choice.put("message", message);
        choice.put("finish_reason", "stop");
        result.put("choices", Collections.singletonList(choice));
        
        Map<String, Object> usage = new HashMap<>();
        usage.put("prompt_tokens", 10);
        usage.put("completion_tokens", 20);
        usage.put("total_tokens", 30);
        result.put("usage", usage);
        
        log.info("OpenAI chat completion: model={}, messages={}", model, messages.size());
        return result;
    }
    
    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        log.info("OpenAI completion: model={}", model);
        return "Mock completion for: " + prompt.substring(0, Math.min(50, prompt.length()));
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
        String summary = text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
        return summary;
    }
    
    @Override
    public boolean supportsStreaming() {
        return true;
    }
    
    @Override
    public boolean supportsFunctionCalling() {
        return true;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
