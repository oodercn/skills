package net.ooder.skill.llm.qianwen;

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
public class QianwenLlmDriver extends AbstractLlmDriver {

    private static final String DEFAULT_BASE_URL = "https://dashscope.aliyuncs.com/api/v1";
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
            apiKey = System.getenv("DASHSCOPE_API_KEY");
        }
        
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
        setConnected(true);
        log.info("Qianwen Driver initialized: baseUrl={}", baseUrl);
    }

    @Override
    protected CompletableFuture<ChatResponse> doChat(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String model = request.getModel() != null ? request.getModel() : "qwen-turbo";
                String url = baseUrl + "/services/aigc/text-generation/generation";

                JSONObject requestBody = buildChatRequestBody(request, model);
                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                return parseChatResponse(responseJson, model);

            } catch (Exception e) {
                log.error("Qianwen chat failed", e);
                throw new RuntimeException("Qianwen chat failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected CompletableFuture<ChatResponse> doChatStream(ChatRequest request, ChatStreamHandler handler) {
        CompletableFuture<ChatResponse> future = new CompletableFuture<>();
        
        try {
            String model = request.getModel() != null ? request.getModel() : "qwen-turbo";
            String url = baseUrl + "/services/aigc/text-generation/generation";

            JSONObject requestBody = buildChatRequestBody(request, model);
            JSONObject parameters = requestBody.getJSONObject("parameters");
            if (parameters == null) {
                parameters = new JSONObject();
                requestBody.put("parameters", parameters);
            }
            parameters.put("incremental_output", true);

            Request httpRequest = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-DashScope-SSE", "enable")
                .post(RequestBody.create(requestBody.toJSONString(), JSON_TYPE))
                .build();

            EventSource.Factory factory = EventSources.createFactory(httpClient);
            final StringBuilder fullContent = new StringBuilder();
            
            factory.newEventSource(httpRequest, new EventSourceListener() {
                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    try {
                        JSONObject json = JSON.parseObject(data);
                        JSONObject output = json.getJSONObject("output");
                        if (output != null) {
                            String content = output.getString("text");
                            if (content != null && !content.isEmpty()) {
                                fullContent.append(content);
                                handler.onToken(content);
                            }
                            
                            String finishReason = output.getString("finish_reason");
                            if ("stop".equals(finishReason)) {
                                ChatResponse response = createChatResponse(fullContent.toString(), model);
                                handler.onComplete(response);
                                future.complete(response);
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
            log.error("Qianwen stream failed", e);
            handler.onError(e);
            future.completeExceptionally(e);
        }

        return future;
    }

    @Override
    protected CompletableFuture<EmbeddingResponse> doEmbed(EmbeddingRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + "/services/embeddings/text-embedding/text-embedding";
                String model = request.getModel() != null ? request.getModel() : "text-embedding-v2";

                JSONObject requestBody = new JSONObject();
                JSONObject input = new JSONObject();
                input.put("texts", request.getInput());
                requestBody.put("model", model);
                requestBody.put("input", input);

                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                return parseEmbeddingResponse(responseJson);

            } catch (Exception e) {
                log.error("Qianwen embedding failed", e);
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
            "qwen-turbo",
            "qwen-plus",
            "qwen-max",
            "qwen-max-longcontext",
            "text-embedding-v2"
        ));
    }

    @Override
    protected CompletableFuture<ModelInfo> doGetModelInfo(String modelId) {
        return CompletableFuture.supplyAsync(() -> {
            ModelInfo info = new ModelInfo();
            info.setId(modelId);
            info.setProvider("qianwen");
            info.setSupportsStreaming(true);
            info.setSupportsFunctionCalling(true);

            if (modelId.contains("embedding")) {
                info.setName("Qianwen Embedding");
                info.setSupportsEmbeddings(true);
                info.setSupportsFunctionCalling(false);
                info.setContextLength(8192);
            } else if (modelId.contains("max")) {
                info.setName("Qianwen Max");
                info.setContextLength(32000);
            } else {
                info.setName("Qianwen " + modelId);
                info.setContextLength(8192);
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
        log.info("Qianwen Driver closed");
    }

    @Override
    public String getDriverName() {
        return "qianwen";
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

        JSONObject input = new JSONObject();
        JSONArray messages = new JSONArray();
        if (request.getMessages() != null) {
            for (ChatMessage msg : request.getMessages()) {
                JSONObject msgObj = new JSONObject();
                msgObj.put("role", msg.getRole());
                msgObj.put("content", msg.getContent());
                messages.add(msgObj);
            }
        }
        input.put("messages", messages);
        body.put("input", input);

        JSONObject parameters = new JSONObject();
        if (config != null) {
            parameters.put("temperature", config.getTemperature());
            if (config.getMaxTokens() > 0) {
                parameters.put("max_tokens", config.getMaxTokens());
            }
        }
        if (request.getMaxTokens() > 0) {
            parameters.put("max_tokens", request.getMaxTokens());
        }
        body.put("parameters", parameters);

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
        response.setModel(model);

        JSONObject output = obj.getJSONObject("output");
        if (output != null) {
            ChatMessage msg = new ChatMessage();
            msg.setRole("assistant");
            msg.setContent(output.getString("text"));
            response.setMessage(msg);
            response.setFinishReason(output.getString("finish_reason"));
        }

        JSONObject usage = obj.getJSONObject("usage");
        if (usage != null) {
            UsageInfo info = new UsageInfo();
            info.setPromptTokens(usage.getIntValue("input_tokens"));
            info.setCompletionTokens(usage.getIntValue("output_tokens"));
            info.setTotalTokens(usage.getIntValue("total_tokens"));
            response.setUsage(info);
        }

        return response;
    }

    private EmbeddingResponse parseEmbeddingResponse(String responseJson) {
        JSONObject obj = JSON.parseObject(responseJson);
        EmbeddingResponse response = new EmbeddingResponse();

        JSONObject output = obj.getJSONObject("output");
        if (output != null) {
            JSONArray embeddings = output.getJSONArray("embeddings");
            List<EmbeddingData> dataList = new ArrayList<>();
            if (embeddings != null) {
                for (int i = 0; i < embeddings.size(); i++) {
                    JSONObject item = embeddings.getJSONObject(i);
                    EmbeddingData ed = new EmbeddingData();
                    ed.setIndex(item.getIntValue("text_index"));
                    
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
        }

        return response;
    }

    private CompletionResponse convertToCompletionResponse(ChatResponse chatResponse) {
        CompletionResponse response = new CompletionResponse();
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
