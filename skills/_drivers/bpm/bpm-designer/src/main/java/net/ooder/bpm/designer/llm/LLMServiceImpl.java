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

import java.util.*;

@Service
public class LLMServiceImpl implements LLMService {
    
    private static final Logger log = LoggerFactory.getLogger(LLMServiceImpl.class);
    
    private final LLMConfig config;
    private final RestTemplate restTemplate;
    
    @Autowired
    public LLMServiceImpl(LLMConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
        if (!isAvailable()) {
            return LLMResponse.failure("LLM service is not available");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            Map<String, Object> requestBody = buildRequestBody(systemPrompt, userPrompt, functions);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getApiKey());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            log.debug("Sending request to LLM: {}", config.getApiEndpoint());
            
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
    public LLMResponse chatWithFunctionResult(String originalPrompt, String functionName, Object result) {
        if (!isAvailable()) {
            return LLMResponse.failure("LLM service is not available");
        }
        
        try {
            List<Map<String, Object>> messages = new ArrayList<>();
            
            messages.add(Map.of(
                "role", "user",
                "content", originalPrompt
            ));
            
            messages.add(Map.of(
                "role", "assistant",
                "function_call", Map.of(
                    "name", functionName,
                    "arguments", "{}"
                )
            ));
            
            messages.add(Map.of(
                "role", "function",
                "name", functionName,
                "content", objectMapper.writeValueAsString(result)
            ));
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModel());
            requestBody.put("messages", messages);
            requestBody.put("temperature", config.getTemperature());
            requestBody.put("max_tokens", config.getMaxTokens());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getApiKey());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                config.getApiEndpoint(),
                HttpMethod.POST,
                entity,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseResponse(response.getBody(), 0);
            } else {
                return LLMResponse.failure("LLM request failed: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error calling LLM with function result: {}", e.getMessage(), e);
            return LLMResponse.failure("LLM call failed: " + e.getMessage());
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
    
    private Map<String, Object> buildRequestBody(String systemPrompt, String userPrompt, 
            List<Map<String, Object>> functions) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", config.getModel());
        
        List<Map<String, Object>> messages = new ArrayList<>();
        
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(Map.of("role", "system", "content", systemPrompt));
        }
        
        if (userPrompt != null && !userPrompt.isEmpty()) {
            messages.add(Map.of("role", "user", "content", userPrompt));
        }
        
        body.put("messages", messages);
        body.put("temperature", config.getTemperature());
        body.put("max_tokens", config.getMaxTokens());
        
        if (functions != null && !functions.isEmpty()) {
            body.put("functions", functions);
            body.put("function_call", "auto");
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
                
                JsonNode functionCall = message.path("function_call");
                if (!functionCall.isMissingNode()) {
                    List<FunctionCall> calls = new ArrayList<>();
                    
                    String funcName = functionCall.path("name").asText();
                    String argsStr = functionCall.path("arguments").asText();
                    
                    Map<String, Object> args = objectMapper.readValue(argsStr, Map.class);
                    calls.add(new FunctionCall(funcName, args));
                    
                    LLMResponse response = LLMResponse.withFunctionCalls(calls);
                    response.setExecutionTime(executionTime);
                    
                    JsonNode usage = root.path("usage");
                    if (!usage.isMissingNode()) {
                        response.setTokensUsed(usage.path("total_tokens").asInt());
                    }
                    
                    return response;
                }
                
                String content = message.path("content").asText();
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
