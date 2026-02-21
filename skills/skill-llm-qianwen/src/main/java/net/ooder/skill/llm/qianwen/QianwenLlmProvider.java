package net.ooder.skill.llm.qianwen;

import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.skill.LlmProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class QianwenLlmProvider implements LlmProvider {
    
    private String apiKey;
    private String baseUrl = "https://dashscope.aliyuncs.com/api/v1";
    
    public QianwenLlmProvider() {
        this.apiKey = System.getenv("DASHSCOPE_API_KEY");
    }
    
    @Override
    public String getProviderType() {
        return "qianwen";
    }
    
    @Override
    public List<String> getSupportedModels() {
        return Arrays.asList(
            "qwen-turbo",
            "qwen-plus",
            "qwen-max",
            "qwen-max-longcontext",
            "qwen-long",
            "qwen-vl-plus",
            "qwen-vl-max",
            "qwen-audio-turbo",
            "qwen2.5-72b-instruct",
            "qwen2.5-32b-instruct",
            "qwen2.5-14b-instruct",
            "qwen2.5-7b-instruct"
        );
    }
    
    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        Map<String, Object> result = new HashMap<>();
        
        result.put("request_id", UUID.randomUUID().toString());
        result.put("code", "Success");
        result.put("message", "Successful");
        
        Map<String, Object> output = new HashMap<>();
        output.put("text", "This is a mock response from Qianwen. Configure DASHSCOPE_API_KEY for real responses.");
        output.put("finish_reason", "stop");
        result.put("output", output);
        
        Map<String, Object> usage = new HashMap<>();
        usage.put("input_tokens", 15);
        usage.put("output_tokens", 25);
        usage.put("total_tokens", 40);
        result.put("usage", usage);
        
        log.info("Qianwen chat completion: model={}, messages={}", model, messages.size());
        return result;
    }
    
    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        log.info("Qianwen completion: model={}", model);
        return "Mock Qianwen completion for: " + prompt.substring(0, Math.min(50, prompt.length()));
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
        
        log.info("Qianwen embeddings: model={}, texts={}", model, texts.size());
        return embeddings;
    }
    
    @Override
    public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
        log.info("Qianwen translate: model={}, target={}", model, targetLanguage);
        return "[Translated to " + targetLanguage + "]: " + text;
    }
    
    @Override
    public String summarize(String model, String text, int maxLength) {
        log.info("Qianwen summarize: model={}, maxLength={}", model, maxLength);
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
