package net.ooder.skill.llm.ollama;

import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.skill.LlmProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class OllamaLlmProvider implements LlmProvider {
    
    private String baseUrl = "http://localhost:11434";
    
    public OllamaLlmProvider() {
    }
    
    public OllamaLlmProvider(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    @Override
    public String getProviderType() {
        return "ollama";
    }
    
    @Override
    public List<String> getSupportedModels() {
        return Arrays.asList(
            "llama3.1", "llama3", "llama2",
            "mistral", "mixtral",
            "codellama", "deepseek-coder",
            "phi3", "gemma", "qwen2"
        );
    }
    
    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        Map<String, Object> result = new HashMap<>();
        
        result.put("model", model);
        result.put("created_at", new Date().toString());
        
        Map<String, Object> message = new HashMap<>();
        message.put("role", "assistant");
        message.put("content", "This is a mock Ollama response. Ensure Ollama is running at " + baseUrl);
        result.put("message", message);
        result.put("done", true);
        
        Map<String, Object> evalCount = new HashMap<>();
        evalCount.put("prompt_eval_count", 10);
        evalCount.put("eval_count", 20);
        result.put("eval_count", evalCount);
        
        log.info("Ollama chat: model={}, messages={}", model, messages.size());
        return result;
    }
    
    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        log.info("Ollama completion: model={}", model);
        return "Mock Ollama completion for: " + prompt.substring(0, Math.min(50, prompt.length()));
    }
    
    @Override
    public List<double[]> embed(String model, List<String> texts) {
        List<double[]> embeddings = new ArrayList<>();
        Random random = new Random();
        
        for (String text : texts) {
            double[] embedding = new double[4096];
            for (int i = 0; i < 4096; i++) {
                embedding[i] = random.nextGaussian();
            }
            embeddings.add(embedding);
        }
        
        log.info("Ollama embeddings: model={}, texts={}", model, texts.size());
        return embeddings;
    }
    
    @Override
    public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
        log.info("Ollama translate: model={}, target={}", model, targetLanguage);
        return "[Translated to " + targetLanguage + "]: " + text;
    }
    
    @Override
    public String summarize(String model, String text, int maxLength) {
        log.info("Ollama summarize: model={}, maxLength={}", model, maxLength);
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
    
    @Override
    public boolean supportsStreaming() {
        return true;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
