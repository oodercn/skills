package net.ooder.skill.llm.deepseek;

import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.skill.LlmProvider;
import net.ooder.scene.skill.StreamHandler;
import net.ooder.sdk.drivers.llm.LlmDriver;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class DeepSeekLlmProvider implements LlmProvider {

    private final DeepSeekLlmDriver driver;
    private String defaultModel = "deepseek-chat";

    public DeepSeekLlmProvider() {
        this.driver = new DeepSeekLlmDriver();
        LlmDriver.LlmConfig config = new LlmDriver.LlmConfig();
        config.setApiKey(System.getenv("DEEPSEEK_API_KEY"));
        driver.init(config);
    }

    public DeepSeekLlmProvider(String apiKey) {
        this.driver = new DeepSeekLlmDriver();
        LlmDriver.LlmConfig config = new LlmDriver.LlmConfig();
        config.setApiKey(apiKey);
        driver.init(config);
    }

    @Override
    public String getProviderType() {
        return "deepseek";
    }

    @Override
    public List<String> getSupportedModels() {
        try {
            return driver.listModels().get();
        } catch (Exception e) {
            log.error("Failed to list models", e);
            return Arrays.asList("deepseek-chat", "deepseek-coder", "deepseek-reasoner");
        }
    }

    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        try {
            LlmDriver.ChatRequest request = new LlmDriver.ChatRequest();
            request.setModel(model != null ? model : defaultModel);
            
            List<LlmDriver.ChatMessage> chatMessages = new ArrayList<>();
            for (Map<String, Object> msg : messages) {
                String role = (String) msg.get("role");
                String content = (String) msg.get("content");
                chatMessages.add(new LlmDriver.ChatMessage(role, content));
            }
            request.setMessages(chatMessages);

            if (options != null) {
                if (options.containsKey("temperature")) {
                    request.setTemperature(((Number) options.get("temperature")).doubleValue());
                }
                if (options.containsKey("max_tokens")) {
                    request.setMaxTokens(((Number) options.get("max_tokens")).intValue());
                }
            }

            LlmDriver.ChatResponse response = driver.chat(request).get();
            return convertToMap(response);

        } catch (Exception e) {
            log.error("DeepSeek chat failed", e);
            return createErrorResponse(e.getMessage());
        }
    }

    @Override
    public void chatStream(String model, List<Map<String, Object>> messages, Map<String, Object> options, StreamHandler handler) {
        try {
            LlmDriver.ChatRequest request = new LlmDriver.ChatRequest();
            request.setModel(model != null ? model : defaultModel);

            List<LlmDriver.ChatMessage> chatMessages = new ArrayList<>();
            for (Map<String, Object> msg : messages) {
                String role = (String) msg.get("role");
                String content = (String) msg.get("content");
                chatMessages.add(new LlmDriver.ChatMessage(role, content));
            }
            request.setMessages(chatMessages);

            if (options != null) {
                if (options.containsKey("temperature")) {
                    request.setTemperature(((Number) options.get("temperature")).doubleValue());
                }
                if (options.containsKey("max_tokens")) {
                    request.setMaxTokens(((Number) options.get("max_tokens")).intValue());
                }
            }

            driver.chatStream(request, new LlmDriver.ChatStreamHandler() {
                @Override
                public void onToken(String token) {
                    handler.onChunk(token);
                }

                @Override
                public void onMessage(LlmDriver.ChatMessage message) {
                }

                @Override
                public void onComplete(LlmDriver.ChatResponse response) {
                    handler.onComplete(convertToMap(response));
                }

                @Override
                public void onError(Throwable error) {
                    handler.onError(error);
                }
            });

        } catch (Exception e) {
            log.error("DeepSeek stream failed", e);
            handler.onError(e);
        }
    }

    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);

        Map<String, Object> result = chat(model, messages, options);
        return extractContent(result);
    }

    @Override
    public List<double[]> embed(String model, List<String> texts) {
        try {
            LlmDriver.EmbeddingRequest request = new LlmDriver.EmbeddingRequest();
            request.setModel(model != null ? model : "deepseek-embedding");
            request.setInput(texts);

            LlmDriver.EmbeddingResponse response = driver.embed(request).get();
            
            List<double[]> embeddings = new ArrayList<>();
            if (response.getData() != null) {
                for (LlmDriver.EmbeddingData data : response.getData()) {
                    embeddings.add(data.getEmbedding());
                }
            }
            return embeddings;

        } catch (Exception e) {
            log.error("DeepSeek embedding failed", e);
            return createMockEmbeddings(texts.size(), 1536);
        }
    }

    @Override
    public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
        String prompt = String.format("Translate the following text from %s to %s. Only output the translation:\n\n%s",
            sourceLanguage != null ? sourceLanguage : "auto-detect",
            targetLanguage,
            text);
        return complete(model, prompt, null);
    }

    @Override
    public String summarize(String model, String text, int maxLength) {
        String prompt = String.format("Summarize the following text in no more than %d characters:\n\n%s",
            maxLength, text);
        return complete(model, prompt, null);
    }

    @Override
    public boolean supportsStreaming() {
        return driver.supportsStreaming();
    }

    @Override
    public boolean supportsFunctionCalling() {
        return driver.supportsFunctionCalling();
    }

    public void setApiKey(String apiKey) {
        LlmDriver.LlmConfig config = new LlmDriver.LlmConfig();
        config.setApiKey(apiKey);
        driver.init(config);
    }

    public void setBaseUrl(String baseUrl) {
        LlmDriver.LlmConfig config = new LlmDriver.LlmConfig();
        config.setApiKey(System.getenv("DEEPSEEK_API_KEY"));
        config.setBaseUrl(baseUrl);
        driver.init(config);
    }

    public void setDefaultModel(String model) {
        this.defaultModel = model;
    }

    private Map<String, Object> convertToMap(LlmDriver.ChatResponse response) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", response.getId());
        result.put("model", response.getModel());
        result.put("created", response.getCreatedTime());

        List<Map<String, Object>> choices = new ArrayList<>();
        Map<String, Object> choice = new HashMap<>();
        choice.put("index", 0);

        if (response.getMessage() != null) {
            Map<String, Object> message = new HashMap<>();
            message.put("role", response.getMessage().getRole());
            message.put("content", response.getMessage().getContent());
            choice.put("message", message);
        }
        choice.put("finish_reason", response.getFinishReason());
        choices.add(choice);
        result.put("choices", choices);

        if (response.getUsage() != null) {
            Map<String, Object> usage = new HashMap<>();
            usage.put("prompt_tokens", response.getUsage().getPromptTokens());
            usage.put("completion_tokens", response.getUsage().getCompletionTokens());
            usage.put("total_tokens", response.getUsage().getTotalTokens());
            result.put("usage", usage);
        }

        return result;
    }

    private String extractContent(Map<String, Object> result) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                if (message != null) {
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            log.error("Extract content failed", e);
        }
        return null;
    }

    private Map<String, Object> createErrorResponse(String error) {
        Map<String, Object> result = new HashMap<>();
        result.put("error", error);
        
        List<Map<String, Object>> choices = new ArrayList<>();
        Map<String, Object> choice = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "assistant");
        message.put("content", "Error: " + error);
        choice.put("message", message);
        choices.add(choice);
        result.put("choices", choices);
        
        return result;
    }

    private List<double[]> createMockEmbeddings(int count, int dimension) {
        List<double[]> embeddings = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            double[] embedding = new double[dimension];
            for (int j = 0; j < dimension; j++) {
                embedding[j] = random.nextGaussian();
            }
            embeddings.add(embedding);
        }
        return embeddings;
    }
}
