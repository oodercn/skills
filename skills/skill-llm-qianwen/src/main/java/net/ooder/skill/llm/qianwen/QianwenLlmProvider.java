package net.ooder.skill.llm.qianwen;

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
public class QianwenLlmProvider implements LlmProvider {
    
    private static final String DEFAULT_BASE_URL = "https://dashscope.aliyuncs.com/api/v1";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient;
    
    private String apiKey;
    private String baseUrl = DEFAULT_BASE_URL;
    
    public QianwenLlmProvider() {
        this.apiKey = System.getenv("DASHSCOPE_API_KEY");
        this.httpClient = createHttpClient();
    }
    
    public QianwenLlmProvider(String apiKey) {
        this.apiKey = apiKey;
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
            "qwen2.5-7b-instruct",
            "text-embedding-v1",
            "text-embedding-v2",
            "text-embedding-v3"
        );
    }
    
    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Qianwen API key not configured, returning mock response");
            return createMockChatResponse(model);
        }
        
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            
            ObjectNode inputNode = requestBody.putObject("input");
            ArrayNode messagesArray = inputNode.putArray("messages");
            
            for (Map<String, Object> msg : messages) {
                ObjectNode msgNode = messagesArray.addObject();
                msgNode.put("role", (String) msg.get("role"));
                msgNode.put("content", (String) msg.get("content"));
            }
            
            ObjectNode parametersNode = requestBody.putObject("parameters");
            if (options != null) {
                if (options.containsKey("temperature")) {
                    parametersNode.put("temperature", ((Number) options.get("temperature")).doubleValue());
                }
                if (options.containsKey("max_tokens")) {
                    parametersNode.put("max_tokens", ((Number) options.get("max_tokens")).intValue());
                }
                if (options.containsKey("top_p")) {
                    parametersNode.put("top_p", ((Number) options.get("top_p")).doubleValue());
                }
                if (options.containsKey("result_format")) {
                    parametersNode.put("result_format", (String) options.get("result_format"));
                }
            } else {
                parametersNode.put("result_format", "message");
            }
            
            Request request = new Request.Builder()
                .url(baseUrl + "/services/aigc/text-generation/generation")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    log.error("Qianwen API error: {} - {}", response.code(), errorBody);
                    throw new RuntimeException("Qianwen API error: " + response.code() + " - " + errorBody);
                }
                
                String responseBody = response.body().string();
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                
                Map<String, Object> result = new HashMap<>();
                result.put("request_id", jsonResponse.path("request_id").asText());
                result.put("code", jsonResponse.path("code").asText());
                result.put("message", jsonResponse.path("message").asText());
                
                JsonNode outputNode = jsonResponse.path("output");
                Map<String, Object> output = new HashMap<>();
                
                JsonNode choicesNode = outputNode.path("choices");
                if (choicesNode.isArray() && choicesNode.size() > 0) {
                    JsonNode choice = choicesNode.get(0);
                    JsonNode messageNode = choice.path("message");
                    Map<String, Object> message = new HashMap<>();
                    message.put("role", messageNode.path("role").asText());
                    message.put("content", messageNode.path("content").asText());
                    
                    List<Map<String, Object>> choices = new ArrayList<>();
                    Map<String, Object> choiceMap = new HashMap<>();
                    choiceMap.put("index", 0);
                    choiceMap.put("message", message);
                    choiceMap.put("finish_reason", choice.path("finish_reason").asText());
                    choices.add(choiceMap);
                    output.put("choices", choices);
                    output.put("text", messageNode.path("content").asText());
                } else {
                    output.put("text", outputNode.path("text").asText());
                    output.put("finish_reason", outputNode.path("finish_reason").asText());
                }
                
                result.put("output", output);
                
                JsonNode usageNode = jsonResponse.path("usage");
                Map<String, Object> usage = new HashMap<>();
                usage.put("input_tokens", usageNode.path("input_tokens").asInt());
                usage.put("output_tokens", usageNode.path("output_tokens").asInt());
                usage.put("total_tokens", usageNode.path("total_tokens").asInt());
                result.put("usage", usage);
                
                log.info("Qianwen chat completion success: model={}, tokens={}", model, usage.get("total_tokens"));
                return result;
            }
            
        } catch (IOException e) {
            log.error("Qianwen API call failed", e);
            throw new RuntimeException("Qianwen API call failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void chatStream(String model, List<Map<String, Object>> messages, Map<String, Object> options, StreamHandler handler) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Qianwen API key not configured, sending mock stream");
            handler.onChunk("Mock response - API key not configured");
            handler.onComplete(new HashMap<>());
            return;
        }
        
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            
            ObjectNode inputNode = requestBody.putObject("input");
            ArrayNode messagesArray = inputNode.putArray("messages");
            
            for (Map<String, Object> msg : messages) {
                ObjectNode msgNode = messagesArray.addObject();
                msgNode.put("role", (String) msg.get("role"));
                msgNode.put("content", (String) msg.get("content"));
            }
            
            ObjectNode parametersNode = requestBody.putObject("parameters");
            parametersNode.put("incremental_output", true);
            if (options != null) {
                if (options.containsKey("temperature")) {
                    parametersNode.put("temperature", ((Number) options.get("temperature")).doubleValue());
                }
                if (options.containsKey("max_tokens")) {
                    parametersNode.put("max_tokens", ((Number) options.get("max_tokens")).intValue());
                }
            }
            
            Request request = new Request.Builder()
                .url(baseUrl + "/services/aigc/text-generation/generation")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-DashScope-SSE", "enable")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
            
            EventSource.Factory factory = EventSources.createFactory(httpClient);
            factory.newEventSource(request, new EventSourceListener() {
                private Map<String, Object> metadata = new HashMap<>();
                
                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    try {
                        JsonNode json = objectMapper.readTree(data);
                        JsonNode output = json.path("output");
                        
                        JsonNode choicesNode = output.path("choices");
                        if (choicesNode.isArray() && choicesNode.size() > 0) {
                            JsonNode delta = choicesNode.get(0).path("message");
                            String content = delta.path("content").asText("");
                            if (!content.isEmpty()) {
                                handler.onChunk(content);
                            }
                        } else {
                            String text = output.path("text").asText("");
                            if (!text.isEmpty()) {
                                handler.onChunk(text);
                            }
                        }
                        
                        if ("stop".equals(output.path("finish_reason").asText(""))) {
                            JsonNode usageNode = json.path("usage");
                            metadata.put("input_tokens", usageNode.path("input_tokens").asInt());
                            metadata.put("output_tokens", usageNode.path("output_tokens").asInt());
                            handler.onComplete(metadata);
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
            log.error("Qianwen stream call failed", e);
            handler.onError(e);
        }
    }
    
    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(createMessage("user", prompt));
        
        Map<String, Object> result = chat(model, messages, options);
        
        Map<String, Object> output = (Map<String, Object>) result.get("output");
        if (output.containsKey("choices")) {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        }
        
        return (String) output.get("text");
    }
    
    @Override
    public List<double[]> embed(String model, List<String> texts) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Qianwen API key not configured, returning mock embeddings");
            return createMockEmbeddings(texts.size(), 1536);
        }
        
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            
            ObjectNode inputNode = requestBody.putObject("input");
            ArrayNode textsArray = inputNode.putArray("texts");
            for (String text : texts) {
                textsArray.add(text);
            }
            
            ObjectNode parametersNode = requestBody.putObject("parameters");
            parametersNode.put("text_type", "query");
            
            Request request = new Request.Builder()
                .url(baseUrl + "/services/embeddings/text-embedding/text-embedding")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    throw new RuntimeException("Qianwen Embedding API error: " + response.code());
                }
                
                String responseBody = response.body().string();
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                
                List<double[]> embeddings = new ArrayList<>();
                JsonNode embeddingsNode = jsonResponse.path("output").path("embeddings");
                
                if (embeddingsNode.isArray()) {
                    for (JsonNode item : embeddingsNode) {
                        JsonNode embeddingNode = item.path("embedding");
                        double[] embedding = new double[embeddingNode.size()];
                        for (int i = 0; i < embeddingNode.size(); i++) {
                            embedding[i] = embeddingNode.get(i).asDouble();
                        }
                        embeddings.add(embedding);
                    }
                }
                
                log.info("Qianwen embeddings success: model={}, count={}", model, embeddings.size());
                return embeddings;
            }
            
        } catch (IOException e) {
            log.error("Qianwen embedding API call failed", e);
            throw new RuntimeException("Qianwen embedding API call failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String translate(String model, String text, String targetLanguage, String sourceLanguage) {
        String prompt = String.format("将以下文本从%s翻译成%s，只输出翻译结果：\n\n%s",
            sourceLanguage != null ? sourceLanguage : "自动检测",
            targetLanguage,
            text);
        return complete(model, prompt, null);
    }
    
    @Override
    public String summarize(String model, String text, int maxLength) {
        String prompt = String.format("请用不超过%d个字总结以下内容，只输出总结：\n\n%s",
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
        result.put("request_id", UUID.randomUUID().toString());
        result.put("code", "Success");
        result.put("message", "Successful");
        
        Map<String, Object> output = new HashMap<>();
        output.put("text", "Mock response - Please configure DASHSCOPE_API_KEY for real responses.");
        output.put("finish_reason", "stop");
        result.put("output", output);
        
        Map<String, Object> usage = new HashMap<>();
        usage.put("input_tokens", 10);
        usage.put("output_tokens", 20);
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
}
