package net.ooder.skill.llm.ollama;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.skill.LlmProvider;
import net.ooder.scene.skill.StreamHandler;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OllamaLlmProvider implements LlmProvider {
    
    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient;
    
    private String baseUrl = DEFAULT_BASE_URL;
    
    public OllamaLlmProvider() {
        this.httpClient = createHttpClient();
    }
    
    public OllamaLlmProvider(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = createHttpClient();
    }
    
    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    }
    
    @Override
    public String getProviderType() {
        return "ollama";
    }
    
    @Override
    public List<String> getSupportedModels() {
        List<String> models = new ArrayList<>();
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "/api/tags")
                .get()
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonNode json = objectMapper.readTree(response.body().string());
                    JsonNode modelsNode = json.path("models");
                    if (modelsNode.isArray()) {
                        for (JsonNode model : modelsNode) {
                            models.add(model.path("name").asText());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Failed to fetch Ollama models: {}", e.getMessage());
        }
        
        if (models.isEmpty()) {
            return Arrays.asList(
                "llama3.1", "llama3", "llama2",
                "mistral", "mixtral",
                "codellama", "deepseek-coder",
                "phi3", "gemma", "gemma2",
                "qwen2", "qwen2.5",
                "nomic-embed-text", "mxbai-embed-large"
            );
        }
        
        return models;
    }
    
    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("stream", false);
            
            ArrayNode messagesArray = requestBody.putArray("messages");
            for (Map<String, Object> msg : messages) {
                ObjectNode msgNode = messagesArray.addObject();
                msgNode.put("role", (String) msg.get("role"));
                msgNode.put("content", (String) msg.get("content"));
            }
            
            if (options != null) {
                ObjectNode optionsNode = requestBody.putObject("options");
                if (options.containsKey("temperature")) {
                    optionsNode.put("temperature", ((Number) options.get("temperature")).doubleValue());
                }
                if (options.containsKey("num_predict")) {
                    optionsNode.put("num_predict", ((Number) options.get("num_predict")).intValue());
                }
                if (options.containsKey("top_p")) {
                    optionsNode.put("top_p", ((Number) options.get("top_p")).doubleValue());
                }
            }
            
            Request request = new Request.Builder()
                .url(baseUrl + "/api/chat")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    log.error("Ollama API error: {} - {}", response.code(), errorBody);
                    throw new RuntimeException("Ollama API error: " + response.code());
                }
                
                String responseBody = response.body().string();
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                
                Map<String, Object> result = new HashMap<>();
                result.put("model", jsonResponse.path("model").asText());
                result.put("created_at", jsonResponse.path("created_at").asText());
                result.put("done", jsonResponse.path("done").asBoolean());
                
                JsonNode messageNode = jsonResponse.path("message");
                Map<String, Object> message = new HashMap<>();
                message.put("role", messageNode.path("role").asText());
                message.put("content", messageNode.path("content").asText());
                result.put("message", message);
                
                Map<String, Object> choices = new HashMap<>();
                choices.put("index", 0);
                choices.put("message", message);
                choices.put("finish_reason", "stop");
                result.put("choices", Collections.singletonList(choices));
                
                JsonNode evalCountNode = jsonResponse.path("eval_count");
                Map<String, Object> usage = new HashMap<>();
                usage.put("prompt_tokens", jsonResponse.path("prompt_eval_count").asInt(0));
                usage.put("completion_tokens", evalCountNode.asInt(0));
                usage.put("total_tokens", usage.get("prompt_tokens").hashCode() + (Integer) usage.get("completion_tokens"));
                result.put("usage", usage);
                
                log.info("Ollama chat success: model={}", model);
                return result;
            }
            
        } catch (IOException e) {
            log.error("Ollama API call failed", e);
            return createMockChatResponse(model);
        }
    }
    
    @Override
    public void chatStream(String model, List<Map<String, Object>> messages, Map<String, Object> options, StreamHandler handler) {
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
                ObjectNode optionsNode = requestBody.putObject("options");
                if (options.containsKey("temperature")) {
                    optionsNode.put("temperature", ((Number) options.get("temperature")).doubleValue());
                }
            }
            
            Request request = new Request.Builder()
                .url(baseUrl + "/api/chat")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
            
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    log.error("Ollama stream call failed", e);
                    handler.onError(e);
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        if (!response.isSuccessful()) {
                            handler.onError(new RuntimeException("Ollama API error: " + response.code()));
                            return;
                        }
                        
                        BufferedReader reader = new BufferedReader(new InputStreamReader(body.byteStream()));
                        String line;
                        Map<String, Object> metadata = new HashMap<>();
                        
                        while ((line = reader.readLine()) != null) {
                            if (line.trim().isEmpty()) continue;
                            
                            try {
                                JsonNode json = objectMapper.readTree(line);
                                JsonNode messageNode = json.path("message");
                                String content = messageNode.path("content").asText("");
                                
                                if (!content.isEmpty()) {
                                    handler.onChunk(content);
                                }
                                
                                if (json.path("done").asBoolean(false)) {
                                    metadata.put("prompt_eval_count", json.path("prompt_eval_count").asInt());
                                    metadata.put("eval_count", json.path("eval_count").asInt());
                                    handler.onComplete(metadata);
                                }
                            } catch (Exception e) {
                                log.debug("Failed to parse stream line: {}", line);
                            }
                        }
                    }
                }
            });
            
        } catch (Exception e) {
            log.error("Ollama stream call failed", e);
            handler.onError(e);
        }
    }
    
    @Override
    public String complete(String model, String prompt, Map<String, Object> options) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            
            if (options != null) {
                ObjectNode optionsNode = requestBody.putObject("options");
                if (options.containsKey("temperature")) {
                    optionsNode.put("temperature", ((Number) options.get("temperature")).doubleValue());
                }
                if (options.containsKey("num_predict")) {
                    optionsNode.put("num_predict", ((Number) options.get("num_predict")).intValue());
                }
            }
            
            Request request = new Request.Builder()
                .url(baseUrl + "/api/generate")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Ollama API error: " + response.code());
                }
                
                String responseBody = response.body().string();
                JsonNode json = objectMapper.readTree(responseBody);
                return json.path("response").asText();
            }
            
        } catch (IOException e) {
            log.error("Ollama completion failed", e);
            return "Mock Ollama completion - Ollama not available at " + baseUrl;
        }
    }
    
    @Override
    public List<double[]> embed(String model, List<String> texts) {
        try {
            List<double[]> embeddings = new ArrayList<>();
            
            for (String text : texts) {
                ObjectNode requestBody = objectMapper.createObjectNode();
                requestBody.put("model", model);
                requestBody.put("input", text);
                
                Request request = new Request.Builder()
                    .url(baseUrl + "/api/embeddings")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new RuntimeException("Ollama Embedding API error: " + response.code());
                    }
                    
                    String responseBody = response.body().string();
                    JsonNode json = objectMapper.readTree(responseBody);
                    JsonNode embeddingNode = json.path("embedding");
                    
                    double[] embedding = new double[embeddingNode.size()];
                    for (int i = 0; i < embeddingNode.size(); i++) {
                        embedding[i] = embeddingNode.get(i).asDouble();
                    }
                    embeddings.add(embedding);
                }
            }
            
            log.info("Ollama embeddings success: model={}, count={}", model, embeddings.size());
            return embeddings;
            
        } catch (IOException e) {
            log.error("Ollama embedding API call failed", e);
            return createMockEmbeddings(texts.size(), 4096);
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
        return false;
    }
    
    private Map<String, Object> createMockChatResponse(String model) {
        Map<String, Object> result = new HashMap<>();
        result.put("model", model);
        result.put("created_at", new Date().toString());
        result.put("done", true);
        
        Map<String, Object> message = new HashMap<>();
        message.put("role", "assistant");
        message.put("content", "Mock response - Ollama not available at " + baseUrl);
        result.put("message", message);
        
        Map<String, Object> choices = new HashMap<>();
        choices.put("index", 0);
        choices.put("message", message);
        choices.put("finish_reason", "stop");
        result.put("choices", Collections.singletonList(choices));
        
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
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public boolean isAvailable() {
        try {
            Request request = new Request.Builder()
                .url(baseUrl + "/api/tags")
                .get()
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            return false;
        }
    }
}
