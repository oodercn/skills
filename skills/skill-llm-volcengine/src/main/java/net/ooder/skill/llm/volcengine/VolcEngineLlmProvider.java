package net.ooder.skill.llm.volcengine;

import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.skill.LlmProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class VolcEngineLlmProvider implements LlmProvider {
    
    private String apiKey;
    private String endpointId;
    private String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";
    
    public VolcEngineLlmProvider() {
        this.apiKey = System.getenv("VOLCENGINE_API_KEY");
        this.endpointId = System.getenv("VOLCENGINE_ENDPOINT_ID");
    }
    
    @Override
    public String getProviderType() {
        return "volcengine";
    }
    
    @Override
    public List<String> getSupportedModels() {
        return Arrays.asList(
            "Doubao-pro-4k",
            "Doubao-pro-32k", 
            "Doubao-pro-128k",
            "Doubao-lite-4k",
            "Doubao-lite-32k",
            "Doubao-lite-128k",
            "Skylark2-pro-4k",
            "Skylark2-pro-32k",
            "Llama3-8B",
            "Llama3-70B"
        );
    }
    
    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        Map<String, Object> result = new HashMap<>();
        
        result.put("id", "chat-" + UUID.randomUUID().toString().substring(0, 8));
        result.put("object", "chat.completion");
        result.put("created", System.currentTimeMillis() / 1000);
        result.put("model", model);
        
        Map<String, Object> choice = new HashMap<>();
        choice.put("index", 0);
        Map<String, Object> message = new HashMap<>();
        message.put("role", "assistant");
        message.put("content", "This is a mock response from VolcEngine Doubao. Configure VOLCENGINE_API_KEY and VOLCENGINE_ENDPOINT_ID for real responses.");
        choice.put("message", message);
        choice.put("finish_reason", "stop");
        result.put("choices", Collections.singletonList(choice));
        
        Map<String, Object> usage = new HashMap<>();
        usage.put("prompt_tokens", 15);
        usage.put("completion_tokens", 25);
        usage.put("total_tokens", 40);
        result.put("usage", usage);
        
        log.info("VolcEngine chat completion: model={}, messages={}", model, messages.size());
        return result;
    }
    
    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        log.info("VolcEngine completion: model={}", model);
        return "Mock VolcEngine completion for: " + prompt.substring(0, Math.min(50, prompt.length()));
    }
    
    @Override
    public List<double[]> embed(String model, List<String> texts) {
        List<double[]> embeddings = new ArrayList<>();
        Random random = new Random();
        
        for (String text : texts) {
            double[] embedding = new double[2048];
            for (int i = 0; i < 2048; i++) {
                embedding[i] = random.nextGaussian();
            }
            embeddings.add(embedding);
        }
        
        log.info("VolcEngine embeddings: model={}, texts={}", model, texts.size());
        return embeddings;
    }
    
    @Override
    public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
        log.info("VolcEngine translate: model={}, target={}", model, targetLanguage);
        return "[Translated to " + targetLanguage + "]: " + text;
    }
    
    @Override
    public String summarize(String model, String text, int maxLength) {
        log.info("VolcEngine summarize: model={}, maxLength={}", model, maxLength);
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
    
    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
