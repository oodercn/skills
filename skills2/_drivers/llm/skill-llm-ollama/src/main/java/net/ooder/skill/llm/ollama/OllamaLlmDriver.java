package net.ooder.skill.llm.ollama;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.drivers.llm.AbstractLlmDriver;
import net.ooder.sdk.drivers.llm.LlmDriver;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class OllamaLlmDriver extends AbstractLlmDriver {

    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    private String baseUrl = DEFAULT_BASE_URL;
    private OkHttpClient httpClient;

    @Override
    protected void doInit() {
        if (config != null && config.getBaseUrl() != null) {
            this.baseUrl = config.getBaseUrl();
        }
        
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
        setConnected(true);
        log.info("Ollama Driver initialized: baseUrl={}", baseUrl);
    }

    @Override
    protected CompletableFuture<ChatResponse> doChat(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String model = request.getModel() != null ? request.getModel() : "llama3";
                String url = baseUrl + "/api/chat";

                JSONObject requestBody = buildChatRequestBody(request, model);
                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                return parseChatResponse(responseJson, model);

            } catch (Exception e) {
                log.error("Ollama chat failed", e);
                throw new RuntimeException("Ollama chat failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected CompletableFuture<ChatResponse> doChatStream(ChatRequest request, ChatStreamHandler handler) {
        CompletableFuture<ChatResponse> future = new CompletableFuture<>();
        
        try {
            String model = request.getModel() != null ? request.getModel() : "llama3";
            String url = baseUrl + "/api/chat";

            JSONObject requestBody = buildChatRequestBody(request, model);
            requestBody.put("stream", true);

            Request httpRequest = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toJSONString(), JSON_TYPE))
                .build();

            EventSource.Factory factory = EventSources.createFactory(httpClient);
            final StringBuilder fullContent = new StringBuilder();
            
            factory.newEventSource(httpRequest, new EventSourceListener() {
                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    try {
                        JSONObject json = JSON.parseObject(data);
                        JSONObject message = json.getJSONObject("message");
                        if (message != null) {
                            String content = message.getString("content");
                            if (content != null && !content.isEmpty()) {
                                fullContent.append(content);
                                handler.onToken(content);
                            }
                        }
                        
                        if (json.getBooleanValue("done")) {
                            ChatResponse response = createChatResponse(fullContent.toString(), model);
                            handler.onComplete(response);
                            future.complete(response);
                        }
                    } catch (Exception e) {
                        log.error("Parse stream data failed: {}", data, e);
                    }
                }

                @Override
                public void onFailure(EventSource eventSource, Throwable t, Response response) {
                    String error = "Stream failed";
                    if (response != null) error += ": " + response.code();
                    log.error(error, t);
                    handler.onError(new RuntimeException(error, t));
                    future.completeExceptionally(t);
                }
            });

        } catch (Exception e) {
            log.error("Ollama stream failed", e);
            handler.onError(e);
            future.completeExceptionally(e);
        }

        return future;
    }

    @Override
    protected CompletableFuture<EmbeddingResponse> doEmbed(EmbeddingRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + "/api/embeddings";
                String model = request.getModel() != null ? request.getModel() : "nomic-embed-text";

                JSONObject requestBody = new JSONObject();
                requestBody.put("model", model);
                if (request.getInput() != null && !request.getInput().isEmpty()) {
                    requestBody.put("prompt", request.getInput().get(0));
                }

                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                return parseEmbeddingResponse(responseJson);

            } catch (Exception e) {
                log.error("Ollama embedding failed", e);
                throw new RuntimeException("Embedding failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected CompletableFuture<CompletionResponse> doComplete(CompletionRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String model = request.getModel() != null ? request.getModel() : "llama3";
                String url = baseUrl + "/api/generate";

                JSONObject requestBody = new JSONObject();
                requestBody.put("model", model);
                requestBody.put("prompt", request.getPrompt());
                requestBody.put("stream", false);
                if (request.getMaxTokens() > 0) {
                    requestBody.put("num_predict", request.getMaxTokens());
                }

                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                return parseCompletionResponse(responseJson);

            } catch (Exception e) {
                log.error("Ollama completion failed", e);
                throw new RuntimeException("Completion failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected CompletableFuture<List<String>> doListModels() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + "/api/tags";
                String responseJson = sendGetRequest(url);
                
                JSONObject obj = JSON.parseObject(responseJson);
                JSONArray models = obj.getJSONArray("models");
                List<String> modelList = new ArrayList<>();
                if (models != null) {
                    for (int i = 0; i < models.size(); i++) {
                        JSONObject model = models.getJSONObject(i);
                        modelList.add(model.getString("name"));
                    }
                }
                return modelList;
            } catch (Exception e) {
                log.error("Failed to list Ollama models", e);
                return Arrays.asList("llama3", "llama2", "mistral", "codellama", "nomic-embed-text");
            }
        });
    }

    @Override
    protected CompletableFuture<ModelInfo> doGetModelInfo(String modelId) {
        return CompletableFuture.supplyAsync(() -> {
            ModelInfo info = new ModelInfo();
            info.setId(modelId);
            info.setName("Ollama " + modelId);
            info.setProvider("ollama");
            info.setSupportsStreaming(true);
            info.setSupportsEmbeddings(modelId.contains("embed"));
            info.setSupportsFunctionCalling(false);
            info.setContextLength(4096);
            return info;
        });
    }

    @Override
    protected void doClose() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
        log.info("Ollama Driver closed");
    }

    @Override
    public String getDriverName() {
        return "ollama";
    }

    @Override
    public String getDriverVersion() {
        return "2.3.0";
    }

    @Override
    public boolean supportsFunctionCalling() {
        return false;
    }

    private JSONObject buildChatRequestBody(ChatRequest request, String model) {
        JSONObject body = new JSONObject();
        body.put("model", model);
        body.put("stream", false);

        JSONArray messages = new JSONArray();
        if (request.getMessages() != null) {
            for (ChatMessage msg : request.getMessages()) {
                JSONObject msgObj = new JSONObject();
                msgObj.put("role", msg.getRole());
                msgObj.put("content", msg.getContent());
                messages.add(msgObj);
            }
        }
        body.put("messages", messages);

        JSONObject options = new JSONObject();
        if (config != null) {
            options.put("temperature", config.getTemperature());
            if (config.getMaxTokens() > 0) {
                options.put("num_predict", config.getMaxTokens());
            }
        }
        if (request.getMaxTokens() > 0) {
            options.put("num_predict", request.getMaxTokens());
        }
        body.put("options", options);

        return body;
    }

    private String sendPostRequest(String urlString, String jsonBody) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(300000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        if (code == 200) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } else {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder error = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    error.append(line);
                }
                throw new RuntimeException("API Error " + code + ": " + error);
            }
        }
    }

    private String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);

        int code = conn.getResponseCode();
        if (code == 200) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } else {
            throw new RuntimeException("API Error " + code);
        }
    }

    private ChatResponse parseChatResponse(String responseJson, String model) {
        JSONObject obj = JSON.parseObject(responseJson);
        ChatResponse response = new ChatResponse();
        response.setModel(model);

        JSONObject message = obj.getJSONObject("message");
        if (message != null) {
            ChatMessage msg = new ChatMessage();
            msg.setRole(message.getString("role"));
            msg.setContent(message.getString("content"));
            response.setMessage(msg);
        }

        response.setFinishReason(obj.getBooleanValue("done") ? "stop" : null);

        return response;
    }

    private EmbeddingResponse parseEmbeddingResponse(String responseJson) {
        JSONObject obj = JSON.parseObject(responseJson);
        EmbeddingResponse response = new EmbeddingResponse();

        JSONArray embedding = obj.getJSONArray("embedding");
        if (embedding != null) {
            EmbeddingData data = new EmbeddingData();
            float[] emb = new float[embedding.size()];
            for (int i = 0; i < embedding.size(); i++) {
                emb[i] = embedding.getFloatValue(i);
            }
            data.setEmbedding(emb);
            data.setIndex(0);
            response.setData(Arrays.asList(data));
        }

        return response;
    }

    private CompletionResponse parseCompletionResponse(String responseJson) {
        JSONObject obj = JSON.parseObject(responseJson);
        CompletionResponse response = new CompletionResponse();
        response.setModel(obj.getString("model"));

        CompletionChoice choice = new CompletionChoice();
        choice.setText(obj.getString("response"));
        choice.setIndex(0);
        choice.setFinishReason(obj.getBooleanValue("done") ? "stop" : null);
        response.setChoices(Arrays.asList(choice));

        return response;
    }
}
