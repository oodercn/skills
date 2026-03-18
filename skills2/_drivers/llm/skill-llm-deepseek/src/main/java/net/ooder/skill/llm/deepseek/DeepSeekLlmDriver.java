package net.ooder.skill.llm.deepseek;

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
public class DeepSeekLlmDriver extends AbstractLlmDriver {

    private static final String DEFAULT_BASE_URL = "https://api.deepseek.com/v1";
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    private String apiKey;
    private String baseUrl = DEFAULT_BASE_URL;
    private OkHttpClient httpClient;

    @Override
    protected void doInit() {
        if (config != null) {
            this.apiKey = config.getApiKey();
            if (config.getBaseUrl() != null) {
                this.baseUrl = config.getBaseUrl();
            }
        }
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("DEEPSEEK_API_KEY");
        }
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
        setConnected(true);
        log.info("DeepSeek Driver initialized: baseUrl={}", baseUrl);
    }

    @Override
    protected CompletableFuture<ChatResponse> doChat(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String model = request.getModel() != null ? request.getModel() : "deepseek-chat";
                String url = baseUrl + "/chat/completions";

                JSONObject requestBody = buildChatRequestBody(request, model);
                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                return parseChatResponse(responseJson, model);

            } catch (Exception e) {
                log.error("DeepSeek chat failed", e);
                throw new RuntimeException("DeepSeek chat failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected CompletableFuture<ChatResponse> doChatStream(ChatRequest request, ChatStreamHandler handler) {
        CompletableFuture<ChatResponse> future = new CompletableFuture<>();
        
        try {
            String model = request.getModel() != null ? request.getModel() : "deepseek-chat";
            String url = baseUrl + "/chat/completions";

            JSONObject requestBody = buildChatRequestBody(request, model);
            requestBody.put("stream", true);

            Request httpRequest = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toJSONString(), JSON_TYPE))
                .build();

            EventSource.Factory factory = EventSources.createFactory(httpClient);
            final StringBuilder fullContent = new StringBuilder();
            
            factory.newEventSource(httpRequest, new EventSourceListener() {
                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    if ("[DONE]".equals(data)) {
                        ChatResponse response = createChatResponse(fullContent.toString(), model);
                        handler.onComplete(response);
                        future.complete(response);
                        return;
                    }

                    try {
                        JSONObject json = JSON.parseObject(data);
                        JSONArray choices = json.getJSONArray("choices");
                        if (choices != null && !choices.isEmpty()) {
                            JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
                            String content = delta.getString("content");
                            if (content != null && !content.isEmpty()) {
                                fullContent.append(content);
                                handler.onToken(content);
                            }
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
            log.error("DeepSeek stream failed", e);
            handler.onError(e);
            future.completeExceptionally(e);
        }

        return future;
    }

    @Override
    protected CompletableFuture<EmbeddingResponse> doEmbed(EmbeddingRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + "/embeddings";
                String model = request.getModel() != null ? request.getModel() : "deepseek-embedding";

                JSONObject requestBody = new JSONObject();
                requestBody.put("model", model);
                requestBody.put("input", request.getInput());

                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                return parseEmbeddingResponse(responseJson);

            } catch (Exception e) {
                log.error("DeepSeek embedding failed", e);
                throw new RuntimeException("Embedding failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected CompletableFuture<CompletionResponse> doComplete(CompletionRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            ChatRequest chatRequest = new ChatRequest();
            chatRequest.setModel(request.getModel());
            chatRequest.setMessages(Arrays.asList(
                ChatMessage.user(request.getPrompt())
            ));
            chatRequest.setMaxTokens(request.getMaxTokens());

            ChatResponse chatResponse = doChat(chatRequest).join();
            return convertToCompletionResponse(chatResponse);
        });
    }

    @Override
    protected CompletableFuture<List<String>> doListModels() {
        return CompletableFuture.completedFuture(Arrays.asList(
            "deepseek-chat",
            "deepseek-coder",
            "deepseek-reasoner",
            "deepseek-reasoner-r1"
        ));
    }

    @Override
    protected CompletableFuture<ModelInfo> doGetModelInfo(String modelId) {
        return CompletableFuture.supplyAsync(() -> {
            ModelInfo info = new ModelInfo();
            info.setId(modelId);
            info.setName("DeepSeek " + modelId);
            info.setProvider("deepseek");
            info.setSupportsStreaming(true);
            info.setSupportsEmbeddings(modelId.contains("embedding"));
            info.setSupportsFunctionCalling(true);

            switch (modelId) {
                case "deepseek-chat":
                case "deepseek-coder":
                    info.setContextLength(64000);
                    break;
                case "deepseek-reasoner":
                case "deepseek-reasoner-r1":
                    info.setContextLength(128000);
                    break;
                default:
                    info.setContextLength(4096);
            }
            return info;
        });
    }

    @Override
    protected void doClose() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
        log.info("DeepSeek Driver closed");
    }

    @Override
    public String getDriverName() {
        return "deepseek";
    }

    @Override
    public String getDriverVersion() {
        return "2.3.0";
    }

    @Override
    public boolean supportsFunctionCalling() {
        return true;
    }

    private JSONObject buildChatRequestBody(ChatRequest request, String model) {
        JSONObject body = new JSONObject();
        body.put("model", model);

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

        if (config != null) {
            body.put("temperature", config.getTemperature());
            body.put("max_tokens", config.getMaxTokens());
        }
        if (request.getMaxTokens() > 0) {
            body.put("max_tokens", request.getMaxTokens());
        }

        return body;
    }

    private String sendPostRequest(String urlString, String jsonBody) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(120000);

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

    private ChatResponse parseChatResponse(String responseJson, String model) {
        JSONObject obj = JSON.parseObject(responseJson);
        ChatResponse response = new ChatResponse();
        response.setId(obj.getString("id"));
        response.setModel(obj.getString("model"));
        response.setCreatedTime(obj.getLongValue("created"));

        JSONArray choices = obj.getJSONArray("choices");
        if (choices != null && !choices.isEmpty()) {
            JSONObject choice = choices.getJSONObject(0);
            JSONObject message = choice.getJSONObject("message");
            
            ChatMessage msg = new ChatMessage();
            msg.setRole(message.getString("role"));
            msg.setContent(message.getString("content"));
            response.setMessage(msg);
            response.setFinishReason(choice.getString("finish_reason"));
        }

        JSONObject usage = obj.getJSONObject("usage");
        if (usage != null) {
            UsageInfo info = new UsageInfo();
            info.setPromptTokens(usage.getIntValue("prompt_tokens"));
            info.setCompletionTokens(usage.getIntValue("completion_tokens"));
            info.setTotalTokens(usage.getIntValue("total_tokens"));
            response.setUsage(info);
        }

        return response;
    }

    private EmbeddingResponse parseEmbeddingResponse(String responseJson) {
        JSONObject obj = JSON.parseObject(responseJson);
        EmbeddingResponse response = new EmbeddingResponse();
        response.setModel(obj.getString("model"));

        JSONArray data = obj.getJSONArray("data");
        List<EmbeddingData> dataList = new ArrayList<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                JSONObject item = data.getJSONObject(i);
                EmbeddingData ed = new EmbeddingData();
                ed.setIndex(item.getIntValue("index"));
                
                JSONArray arr = item.getJSONArray("embedding");
                float[] embedding = new float[arr.size()];
                for (int j = 0; j < arr.size(); j++) {
                    embedding[j] = arr.getFloatValue(j);
                }
                ed.setEmbedding(embedding);
                dataList.add(ed);
            }
        }
        response.setData(dataList);

        return response;
    }

    private CompletionResponse convertToCompletionResponse(ChatResponse chatResponse) {
        CompletionResponse response = new CompletionResponse();
        response.setId(chatResponse.getId());
        response.setModel(chatResponse.getModel());

        CompletionChoice choice = new CompletionChoice();
        choice.setText(chatResponse.getMessage() != null ? chatResponse.getMessage().getContent() : "");
        choice.setIndex(0);
        choice.setFinishReason(chatResponse.getFinishReason());
        response.setChoices(Arrays.asList(choice));
        response.setUsage(chatResponse.getUsage());

        return response;
    }
}
