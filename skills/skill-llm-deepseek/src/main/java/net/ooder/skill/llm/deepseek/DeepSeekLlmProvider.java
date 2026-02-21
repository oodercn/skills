package net.ooder.skill.llm.deepseek;

import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.skill.LlmProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class DeepSeekLlmProvider implements LlmProvider {
    
    private String apiKey;
    private String baseUrl = "https://api.deepseek.com/v1";
    
    public DeepSeekLlmProvider() {
        this.apiKey = System.getenv("DEEPSEEK_API_KEY");
    }
    
    @Override
    public String getProviderType() {
        return "deepseek";
    }
    
    @Override
    public List<String> getSupportedModels() {
        return Arrays.asList(
            "deepseek-chat",
            "deepseek-coder",
            "deepseek-reasoner"
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
        message.put("content", "This is a mock response from DeepSeek. Configure DEEPSEEK_API_KEY for real responses.");
        choice.put("message", message);
        choice.put("finish_reason", "stop");
        result.put("choices", Collections.singletonList(choice));
        
        Map<String, Object> usage = new HashMap<>();
        usage.put("prompt_tokens", 12);
        usage.put("completion_tokens", 22);
        usage.put("total_tokens", 34);
        result.put("usage", usage);
        
        log.info("DeepSeek chat completion: model={}, messages={}", model, messages.size());
        return result;
    }
    
    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        log.info("DeepSeek completion: model={}", model);
        return "Mock DeepSeek completion for: " + prompt.substring(0, Math.min(50, prompt.length()));
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
        
        log.info("DeepSeek embeddings: model={}, texts={}", model, texts.size());
        return embeddings;
    }
    
    @Override
    public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
        log.info("DeepSeek translate: model={}, target={}", model, targetLanguage);
        return "[Translated to " + targetLanguage + "]: " + text;
    }
    
    @Override
    public String summarize(String model, String text, int maxLength) {
        log.info("DeepSeek summarize: model={}, maxLength={}", model, maxLength);
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
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
