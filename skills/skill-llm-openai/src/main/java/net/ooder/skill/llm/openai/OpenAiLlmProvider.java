package net.ooder.skill.llm.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.skill.LlmProvider;
import net.ooder.scene.skill.StreamHandler;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OpenAiLlmProvider implements LlmProvider {
    
    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient;
    
    private String apiKey;
    private String baseUrl = DEFAULT_BASE_URL;
    private int timeout = 60000;
    
    public OpenAiLlmProvider() {
        this.apiKey = System.getenv("OPENAI_API_KEY");
        this.httpClient = createHttpClient();
    }
    
    public OpenAiLlmProvider(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = createHttpClient();
    }
    
    public OpenAiLlmProvider(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.httpClient = createHttpClient();
    }
    
    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
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
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("OpenAI API key not configured, returning mock response");
            return createMockChatResponse(model);
        }
        
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            
            ArrayNode messagesArray = requestBody.putArray("messages");
            for (Map<String, Object> msg : messages) {
                ObjectNode msgNode = messagesArray.addObject();
                msgNode.put("role", (String) msg.get("role"));
                msgNode.put("content", (String) msg.get("content"));
            }
            
            if (options != null) {
                if (options.containsKey("temperature")) {
                    requestBody.put("temperature", ((Number) options.get("temperature")).doubleValue());
                }
                if (options.containsKey("max_tokens")) {
                    requestBody.put("max_tokens", ((Number) options.get("max_tokens")).intValue());
                }
                if (options.containsKey("top_p")) {
                    requestBody.put("top_p", ((Number) options.get("top_p")).doubleValue());
                }
            }
            
            Request request = new Request.Builder()
                .url(baseUrl + "/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    log.error("OpenAI API error: {} - {}", response.code(), errorBody);
                    throw new RuntimeException("OpenAI API error: " + response.code() + " - " + errorBody);
                }
                
                String responseBody = response.body().string();
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                
                Map<String, Object> result = new HashMap<>();
                result.put("id", jsonResponse.path("id").asText());
                result.put("object", jsonResponse.path("object").asText());
                result.put("created", jsonResponse.path("created").asLong());
                result.put("model", jsonResponse.path("model").asText());
                
                JsonNode choicesNode = jsonResponse.path("choices");
                List<Map<String, Object>> choices = new ArrayList<>();
                if (choicesNode.isArray()) {
                    for (JsonNode choice : choicesNode) {
                        Map<String, Object> choiceMap = new HashMap<>();
                        choiceMap.put("index", choice.path("index").asInt());
                        
                        JsonNode messageNode = choice.path("message");
                        Map<String, Object> messageMap = new HashMap<>();
                        messageMap.put("role", messageNode.path("role").asText());
                        messageMap.put("content", messageNode.path("content").asText());
                        choiceMap.put("message", messageMap);
                        choiceMap.put("finish_reason", choice.path("finish_reason").asText());
                        choices.add(choiceMap);
                    }
                }
                result.put("choices", choices);
                
                JsonNode usageNode = jsonResponse.path("usage");
                Map<String, Object> usage = new HashMap<>();
                usage.put("prompt_tokens", usageNode.path("prompt_tokens").asInt());
                usage.put("completion_tokens", usageNode.path("completion_tokens").asInt());
                usage.put("total_tokens", usageNode.path("total_tokens").asInt());
                result.put("usage", usage);
                
                log.info("OpenAI chat completion success: model={}, tokens={}", model, usage.get("total_tokens"));
                return result;
            }
            
        } catch (IOException e) {
            log.error("OpenAI API call failed", e);
            throw new RuntimeException("OpenAI API call failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void chatStream(String model, List<Map<String, Object>> messages, Map<String, Object> options, StreamHandler handler) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("OpenAI API key not configured, sending mock stream");
            handler.onChunk("Mock response - API key not configured");
            handler.onComplete(new HashMap<>());
            return;
        }
        
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("stream", true);
            
            ArrayNode messagesArray = requestBody.putArray("messages");
            for (Map<String, Object> msg : messages) {
                ObjectNode msgNode = messagesArray.addObject();
                msgNode.put("role", (String) msg.get("role"));
                msgNode.put("content", (String) msg.get("content"));
            }
            
            if (options != null) {
                if (options.containsKey("temperature")) {
                    requestBody.put("temperature", ((Number) options.get("temperature")).doubleValue());
                }
                if (options.containsKey("max_tokens")) {
                    requestBody.put("max_tokens", ((Number) options.get("max_tokens")).intValue());
                }
            }
            
            Request request = new Request.Builder()
                .url(baseUrl + "/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
            
            EventSource.Factory factory = EventSources.createFactory(httpClient);
            factory.newEventSource(request, new EventSourceListener() {
                private Map<String, Object> metadata = new HashMap<>();
                
                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    if ("[DONE]".equals(data)) {
                        handler.onComplete(metadata);
                        return;
                    }
                    
                    try {
                        JsonNode json = objectMapper.readTree(data);
                        JsonNode choices = json.path("choices");
                        if (choices.isArray() && choices.size() > 0) {
                            JsonNode delta = choices.get(0).path("delta");
                            String content = delta.path("content").asText("");
                            if (!content.isEmpty()) {
                                handler.onChunk(content);
                            }
                        }
                    } catch (IOException e) {
                        log.error("Failed to parse SSE data: {}", data, e);
                    }
                }
                
                @Override
                public void onFailure(EventSource eventSource, Throwable t, Response response) {
                    String errorMsg = "Stream failed";
                    if (response != null) {
                        errorMsg += ": " + response.code();
                    }
                    log.error(errorMsg, t);
                    handler.onError(new RuntimeException(errorMsg, t));
                }
            });
            
        } catch (Exception e) {
            log.error("OpenAI stream call failed", e);
            handler.onError(e);
        }
    }
    
    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(createMessage("user", prompt));
        
        Map<String, Object> result = chat(model, messages, options);
        
        List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        }
        
        return null;
    }
    
    @Override
    public List<double[]> embed(String model, List<String> texts) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("OpenAI API key not configured, returning mock embeddings");
            return createMockEmbeddings(texts.size(), 1536);
        }
        
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            
            ArrayNode inputArray = requestBody.putArray("input");
            for (String text : texts) {
                inputArray.add(text);
            }
            
            Request request = new Request.Builder()
                .url(baseUrl + "/embeddings")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    throw new RuntimeException("OpenAI Embedding API error: " + response.code());
                }
                
                String responseBody = response.body().string();
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                
                List<double[]> embeddings = new ArrayList<>();
                JsonNode dataNode = jsonResponse.path("data");
                
                if (dataNode.isArray()) {
                    for (JsonNode item : dataNode) {
                        JsonNode embeddingNode = item.path("embedding");
                        double[] embedding = new double[embeddingNode.size()];
                        for (int i = 0; i < embeddingNode.size(); i++) {
                            embedding[i] = embeddingNode.get(i).asDouble();
                        }
                        embeddings.add(embedding);
                    }
                }
                
                log.info("OpenAI embeddings success: model={}, count={}", model, embeddings.size());
                return embeddings;
            }
            
        } catch (IOException e) {
            log.error("OpenAI embedding API call failed", e);
            throw new RuntimeException("OpenAI embedding API call failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
        String prompt = String.format("Translate the following text from %s to %s. Only output the translation result:\n\n%s",
            sourceLanguage != null ? sourceLanguage : "auto-detect",
            targetLanguage,
            text);
        return complete(model, prompt, null);
    }
    
    @Override
    public String summarize(String model, String text, int maxLength) {
        String prompt = String.format("Summarize the following text in no more than %d characters. Only output the summary:\n\n%s",
            maxLength, text);
        return complete(model, prompt, null);
    }
    
    @Override
    public boolean supportsStreaming() {
        return true;
    }
    
    @Override
    public boolean supportsFunctionCalling() {
        return true;
    }
    
    private Map<String, Object> createMessage(String role, String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }
    
    private Map<String, Object> createMockChatResponse(String model) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", "chatcmpl-mock-" + UUID.randomUUID().toString().substring(0, 8));
        result.put("object", "chat.completion");
        result.put("created", System.currentTimeMillis() / 1000);
        result.put("model", model);
        
        Map<String, Object> choice = new HashMap<>();
        choice.put("index", 0);
        Map<String, Object> message = new HashMap<>();
        message.put("role", "assistant");
        message.put("content", "Mock response - Please configure OPENAI_API_KEY for real responses.");
        choice.put("message", message);
        choice.put("finish_reason", "stop");
        result.put("choices", Collections.singletonList(choice));
        
        Map<String, Object> usage = new HashMap<>();
        usage.put("prompt_tokens", 10);
        usage.put("completion_tokens", 20);
        usage.put("total_tokens", 30);
        result.put("usage", usage);
        
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
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
