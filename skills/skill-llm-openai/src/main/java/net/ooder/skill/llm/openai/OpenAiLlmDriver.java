package net.ooder.skill.llm.openai;

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
public class OpenAiLlmDriver extends AbstractLlmDriver {

    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    private String apiKey;
    private String baseUrl = DEFAULT_BASE_URL;
    private String organization;
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
            apiKey = System.getenv("OPENAI_API_KEY");
        }
        organization = System.getenv("OPENAI_ORG_ID");
        
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
        setConnected(true);
        log.info("OpenAI Driver initialized: baseUrl={}", baseUrl);
    }

    @Override
    protected CompletableFuture<ChatResponse> doChat(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String model = request.getModel() != null ? request.getModel() : "gpt-4o-mini";
                String url = baseUrl + "/chat/completions";

                JSONObject requestBody = buildChatRequestBody(request, model);
                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                return parseChatResponse(responseJson, model);

            } catch (Exception e) {
                log.error("OpenAI chat failed", e);
                throw new RuntimeException("OpenAI chat failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected CompletableFuture<ChatResponse> doChatStream(ChatRequest request, ChatStreamHandler handler) {
        CompletableFuture<ChatResponse> future = new CompletableFuture<>();
        
        try {
            String model = request.getModel() != null ? request.getModel() : "gpt-4o-mini";
            String url = baseUrl + "/chat/completions";

            JSONObject requestBody = buildChatRequestBody(request, model);
            requestBody.put("stream", true);

            Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json");
            
            if (organization != null && !organization.isEmpty()) {
                requestBuilder.addHeader("OpenAI-Organization", organization);
            }
            
            Request httpRequest = requestBuilder
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
                            if (delta != null) {
                                String content = delta.getString("content");
                                if (content != null && !content.isEmpty()) {
                                    fullContent.append(content);
                                    handler.onToken(content);
                                }
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
            log.error("OpenAI stream failed", e);
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
                String model = request.getModel() != null ? request.getModel() : "text-embedding-3-small";

                JSONObject requestBody = new JSONObject();
                requestBody.put("model", model);
                requestBody.put("input", request.getInput());

                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                return parseEmbeddingResponse(responseJson);

            } catch (Exception e) {
                log.error("OpenAI embedding failed", e);
                throw new RuntimeException("Embedding failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected CompletableFuture<CompletionResponse> doComplete(CompletionRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String model = request.getModel() != null ? request.getModel() : "gpt-4o-mini";
                String url = baseUrl + "/completions";

                JSONObject requestBody = new JSONObject();
                requestBody.put("model", model);
                requestBody.put("prompt", request.getPrompt());
                if (request.getMaxTokens() > 0) {
                    requestBody.put("max_tokens", request.getMaxTokens());
                }

                String responseJson = sendPostRequest(url, requestBody.toJSONString());
                return parseCompletionResponse(responseJson);

            } catch (Exception e) {
                log.error("OpenAI completion failed", e);
                throw new RuntimeException("Completion failed: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected CompletableFuture<List<String>> doListModels() {
        return CompletableFuture.completedFuture(Arrays.asList(
            "gpt-4o",
            "gpt-4o-mini",
            "gpt-4-turbo",
            "gpt-4",
            "gpt-3.5-turbo",
            "text-embedding-3-small",
            "text-embedding-3-large",
            "text-embedding-ada-002"
        ));
    }

    @Override
    protected CompletableFuture<ModelInfo> doGetModelInfo(String modelId) {
        return CompletableFuture.supplyAsync(() -> {
            ModelInfo info = new ModelInfo();
            info.setId(modelId);
            info.setProvider("openai");
            info.setSupportsStreaming(true);
            info.setSupportsFunctionCalling(modelId.startsWith("gpt"));

            if (modelId.startsWith("text-embedding")) {
                info.setName("OpenAI " + modelId);
                info.setSupportsEmbeddings(true);
                info.setSupportsFunctionCalling(false);
                info.setContextLength(8191);
            } else if (modelId.startsWith("gpt-4")) {
                info.setName("OpenAI GPT-4");
                info.setContextLength(128000);
            } else if (modelId.startsWith("gpt-3.5")) {
                info.setName("OpenAI GPT-3.5");
                info.setContextLength(16385);
            } else {
                info.setName("OpenAI " + modelId);
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
        log.info("OpenAI Driver closed");
    }

    @Override
    public String getDriverName() {
        return "openai";
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
        if (organization != null && !organization.isEmpty()) {
            conn.setRequestProperty("OpenAI-Organization", organization);
        }
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

    private CompletionResponse parseCompletionResponse(String responseJson) {
        JSONObject obj = JSON.parseObject(responseJson);
        CompletionResponse response = new CompletionResponse();
        response.setId(obj.getString("id"));
        response.setModel(obj.getString("model"));

        JSONArray choices = obj.getJSONArray("choices");
        List<CompletionChoice> choiceList = new ArrayList<>();
        if (choices != null) {
            for (int i = 0; i < choices.size(); i++) {
                JSONObject choice = choices.getJSONObject(i);
                CompletionChoice cc = new CompletionChoice();
                cc.setText(choice.getString("text"));
                cc.setIndex(choice.getIntValue("index"));
                cc.setFinishReason(choice.getString("finish_reason"));
                choiceList.add(cc);
            }
        }
        response.setChoices(choiceList);

        return response;
    }
}
