package net.ooder.bpm.designer.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.bpm.designer.llm.config.LLMConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

@Service
public class LLMServiceImpl implements LLMService {
    
    private static final Logger log = LoggerFactory.getLogger(LLMServiceImpl.class);
    
    private final LLMConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public LLMServiceImpl(LLMConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }
    
    @Override
    public LLMResponse chat(String prompt) {
        return chat(null, prompt);
    }
    
    @Override
    public LLMResponse chat(String systemPrompt, String userPrompt) {
        return chatWithFunctions(systemPrompt, userPrompt, null);
    }
    
    @Override
    public LLMResponse chatWithFunctions(String prompt, List<Map<String, Object>> functions) {
        return chatWithFunctions(null, prompt, functions);
    }
    
    @Override
    public LLMResponse chatWithFunctions(String systemPrompt, String userPrompt, List<Map<String, Object>> functions) {
        List<Map<String, Object>> messages = new ArrayList<>();
        
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(Map.of("role", "system", "content", systemPrompt));
        }
        if (userPrompt != null && !userPrompt.isEmpty()) {
            messages.add(Map.of("role", "user", "content", userPrompt));
        }
        
        List<Map<String, Object>> tools = null;
        if (functions != null && !functions.isEmpty()) {
            tools = convertFunctionsToTools(functions);
        }
        
        return chatWithMessages(messages, tools);
    }
    
    @Override
    public LLMResponse chatWithFunctionResult(String originalPrompt, String functionName, Object result) {
        if (!isAvailable()) {
            return LLMResponse.failure("LLM service is not available");
        }
        
        try {
            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", originalPrompt));
            messages.add(Map.of(
                "role", "assistant",
                "function_call", Map.of("name", functionName, "arguments", "{}")
            ));
            messages.add(Map.of(
                "role", "function",
                "name", functionName,
                "content", objectMapper.writeValueAsString(result)
            ));
            
            return chatWithMessages(messages);
        } catch (Exception e) {
            log.error("Error calling LLM with function result: {}", e.getMessage(), e);
            return LLMResponse.failure("LLM call failed: " + e.getMessage());
        }
    }
    
    @Override
    public LLMResponse chatWithMessages(List<Map<String, Object>> messages) {
        return chatWithMessages(messages, null);
    }
    
    @Override
    public LLMResponse chatWithMessages(List<Map<String, Object>> messages, List<Map<String, Object>> tools) {
        if (!isAvailable()) {
            return LLMResponse.failure("LLM service is not available");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            Map<String, Object> requestBody = buildRequestBody(messages, tools);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getApiKey());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            log.debug("Sending request to LLM: {}, messages: {}, tools: {}", 
                config.getApiEndpoint(), messages.size(), tools != null ? tools.size() : 0);
            
            ResponseEntity<String> response = restTemplate.exchange(
                config.getApiEndpoint(),
                HttpMethod.POST,
                entity,
                String.class
            );
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseResponse(response.getBody(), executionTime);
            } else {
                return LLMResponse.failure("LLM request failed: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error calling LLM: {}", e.getMessage(), e);
            return LLMResponse.failure("LLM call failed: " + e.getMessage());
        }
    }
    
    @Override
    public LLMResponse chatWithMessagesAndToolResults(
            List<Map<String, Object>> messages,
            List<Map<String, Object>> tools,
            String toolCallId,
            String functionName,
            Object toolResult) {
        if (!isAvailable()) {
            return LLMResponse.failure("LLM service is not available");
        }
        
        try {
            List<Map<String, Object>> allMessages = new ArrayList<>(messages);
            
            String resultContent;
            if (toolResult instanceof String) {
                resultContent = (String) toolResult;
            } else {
                resultContent = objectMapper.writeValueAsString(toolResult);
            }
            
            Map<String, Object> toolMessage = new HashMap<>();
            toolMessage.put("role", "tool");
            toolMessage.put("tool_call_id", toolCallId != null ? toolCallId : functionName);
            toolMessage.put("content", resultContent);
            allMessages.add(toolMessage);
            
            return chatWithMessages(allMessages, tools);
        } catch (Exception e) {
            log.error("Error calling LLM with tool results: {}", e.getMessage(), e);
            return LLMResponse.failure("LLM call failed: " + e.getMessage());
        }
    }
    
    @Override
    public void chatWithMessagesStream(
            List<Map<String, Object>> messages,
            List<Map<String, Object>> tools,
            Consumer<String> onContent,
            Consumer<List<FunctionCall>> onToolCalls,
            Consumer<LLMResponse> onComplete,
            Consumer<Exception> onError) {
        
        if (!isAvailable()) {
            onError.accept(new RuntimeException("LLM service is not available"));
            return;
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            Map<String, Object> requestBody = buildRequestBody(messages, tools);
            requestBody.put("stream", true);
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            URI uri = new URI(config.getApiEndpoint());
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
            conn.setRequestProperty("Accept", "text/event-stream");
            conn.setConnectTimeout(config.getTimeout());
            conn.setReadTimeout(config.getTimeout());
            
            conn.getOutputStream().write(jsonBody.getBytes(StandardCharsets.UTF_8));
            conn.getOutputStream().flush();
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                onError.accept(new RuntimeException("LLM stream request failed: " + responseCode));
                return;
            }
            
            StringBuilder fullContent = new StringBuilder();
            List<FunctionCall> collectedToolCalls = new ArrayList<>();
            Map<Integer, StringBuilder> toolCallArgs = new HashMap<>();
            Map<Integer, String> toolCallNames = new HashMap<>();
            Map<Integer, String> toolCallIds = new HashMap<>();
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim();
                        if ("[DONE]".equals(data)) break;
                        
                        try {
                            JsonNode chunk = objectMapper.readTree(data);
                            JsonNode choices = chunk.path("choices");
                            if (choices.isArray() && choices.size() > 0) {
                                JsonNode delta = choices.get(0).path("delta");
                                
                                String content = delta.path("content").asText(null);
                                if (content != null && !content.isEmpty()) {
                                    fullContent.append(content);
                                    onContent.accept(content);
                                }
                                
                                JsonNode toolCallsDelta = delta.path("tool_calls");
                                if (toolCallsDelta.isArray()) {
                                    for (JsonNode tc : toolCallsDelta) {
                                        int index = tc.path("index").asInt();
                                        String tcId = tc.path("id").asText(null);
                                        String tcName = tc.path("function").path("name").asText(null);
                                        String tcArgs = tc.path("function").path("arguments").asText(null);
                                        
                                        if (tcId != null) toolCallIds.put(index, tcId);
                                        if (tcName != null) toolCallNames.put(index, tcName);
                                        if (tcArgs != null) {
                                            toolCallArgs.computeIfAbsent(index, k -> new StringBuilder()).append(tcArgs);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.debug("Error parsing SSE chunk: {}", e.getMessage());
                        }
                    }
                }
            }
            
            if (!toolCallNames.isEmpty()) {
                for (Integer idx : toolCallNames.keySet()) {
                    String name = toolCallNames.get(idx);
                    String id = toolCallIds.getOrDefault(idx, name);
                    String argsStr = toolCallArgs.getOrDefault(idx, new StringBuilder()).toString();
                    
                    Map<String, Object> args;
                    try {
                        args = objectMapper.readValue(argsStr, Map.class);
                    } catch (Exception e) {
                        args = Map.of();
                    }
                    
                    FunctionCall call = new FunctionCall(name, args);
                    call.setId(id);
                    collectedToolCalls.add(call);
                }
                
                onToolCalls.accept(collectedToolCalls);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            LLMResponse response;
            
            if (!collectedToolCalls.isEmpty()) {
                response = LLMResponse.withFunctionCalls(collectedToolCalls);
            } else {
                response = LLMResponse.success(fullContent.toString());
            }
            response.setExecutionTime(executionTime);
            
            onComplete.accept(response);
            
        } catch (Exception e) {
            log.error("Error in LLM stream: {}", e.getMessage(), e);
            onError.accept(e);
        }
    }
    
    @Override
    public boolean isAvailable() {
        return config != null && config.isEnabled() && 
               config.getApiKey() != null && !config.getApiKey().isEmpty() &&
               restTemplate != null;
    }
    
    @Override
    public String getModelName() {
        return config != null ? config.getModel() : "unknown";
    }
    
    private List<Map<String, Object>> convertFunctionsToTools(List<Map<String, Object>> functions) {
        List<Map<String, Object>> tools = new ArrayList<>();
        for (Map<String, Object> function : functions) {
            Map<String, Object> tool = new HashMap<>();
            tool.put("type", "function");
            tool.put("function", function);
            tools.add(tool);
        }
        return tools;
    }
    
    private Map<String, Object> buildRequestBody(List<Map<String, Object>> messages, List<Map<String, Object>> tools) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        body.put("messages", messages);
        body.put("temperature", config.getTemperature());
        body.put("max_tokens", config.getMaxTokens());
        
        if (tools != null && !tools.isEmpty()) {
            body.put("tools", tools);
            body.put("tool_choice", "auto");
        }
        
        return body;
    }
    
    private LLMResponse parseResponse(String responseBody, long executionTime) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.path("message");
                
                JsonNode toolCalls = message.path("tool_calls");
                if (toolCalls.isArray() && toolCalls.size() > 0) {
                    List<FunctionCall> calls = new ArrayList<>();
                    
                    for (JsonNode toolCall : toolCalls) {
                        String callId = toolCall.path("id").asText();
                        JsonNode funcNode = toolCall.path("function");
                        String funcName = funcNode.path("name").asText();
                        String argsStr = funcNode.path("arguments").asText();
                        
                        Map<String, Object> args = objectMapper.readValue(argsStr, Map.class);
                        FunctionCall call = new FunctionCall(funcName, args);
                        call.setId(callId);
                        calls.add(call);
                    }
                    
                    LLMResponse response = LLMResponse.withFunctionCalls(calls);
                    response.setExecutionTime(executionTime);
                    
                    JsonNode usage = root.path("usage");
                    if (!usage.isMissingNode()) {
                        response.setTokensUsed(usage.path("total_tokens").asInt());
                    }
                    
                    String content = message.path("content").asText(null);
                    if (content != null && !content.isEmpty()) {
                        response.setContent(content);
                    }
                    
                    return response;
                }
                
                JsonNode functionCall = message.path("function_call");
                if (!functionCall.isMissingNode()) {
                    List<FunctionCall> calls = new ArrayList<>();
                    String funcName = functionCall.path("name").asText();
                    String argsStr = functionCall.path("arguments").asText();
                    Map<String, Object> args = objectMapper.readValue(argsStr, Map.class);
                    FunctionCall call = new FunctionCall(funcName, args);
                    call.setId("legacy_" + funcName);
                    calls.add(call);
                    
                    LLMResponse response = LLMResponse.withFunctionCalls(calls);
                    response.setExecutionTime(executionTime);
                    
                    JsonNode usage = root.path("usage");
                    if (!usage.isMissingNode()) {
                        response.setTokensUsed(usage.path("total_tokens").asInt());
                    }
                    return response;
                }
                
                String content = message.path("content").asText("");
                LLMResponse response = LLMResponse.success(content);
                response.setExecutionTime(executionTime);
                
                JsonNode usage = root.path("usage");
                if (!usage.isMissingNode()) {
                    response.setTokensUsed(usage.path("total_tokens").asInt());
                }
                
                return response;
            }
            
            return LLMResponse.failure("No valid response from LLM");
            
        } catch (Exception e) {
            log.error("Error parsing LLM response: {}", e.getMessage(), e);
            return LLMResponse.failure("Failed to parse LLM response: " + e.getMessage());
        }
    }
}
