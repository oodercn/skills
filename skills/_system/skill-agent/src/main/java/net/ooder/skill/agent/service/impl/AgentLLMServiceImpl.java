package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.llm.LLMRequest;
import net.ooder.skill.agent.llm.LLMResponse;
import net.ooder.skill.agent.llm.LLMService;
import net.ooder.skill.agent.llm.FunctionCall;
import net.ooder.skill.agent.llm.FunctionResult;
import net.ooder.skill.agent.function.FunctionCallingService;
import net.ooder.skill.agent.service.AgentLLMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentLLMServiceImpl implements AgentLLMService {

    private static final Logger log = LoggerFactory.getLogger(AgentLLMServiceImpl.class);

    @Autowired
    private LLMService llmService;

    @Autowired
    private FunctionCallingService functionCallingService;

    private Map<String, Map<String, Object>> agentLLMConfigs = new ConcurrentHashMap<>();
    private Map<String, String> agentSystemPrompts = new ConcurrentHashMap<>();
    private Map<String, List<Map<String, Object>>> agentCustomFunctions = new ConcurrentHashMap<>();

    @Override
    public LLMResponse chat(LLMRequest request) {
        log.debug("[chat] Agent: {}", request.getAgentId());
        return llmService.chat(request);
    }

    @Override
    public LLMResponse chatWithHistory(String agentId, String conversationId, String message,
            List<Map<String, String>> history) {
        log.debug("[chatWithHistory] Agent: {}, Conversation: {}", agentId, conversationId);
        
        LLMRequest request = new LLMRequest();
        request.setAgentId(agentId);
        request.setConversationId(conversationId);
        request.setMessage(message);
        
        if (history != null && !history.isEmpty()) {
            request.setHistory(history);
        }
        
        String systemPrompt = agentSystemPrompts.get(agentId);
        if (systemPrompt != null) {
            request.setSystemPrompt(systemPrompt);
        }
        
        Map<String, Object> config = agentLLMConfigs.get(agentId);
        if (config != null) {
            request.setConfig(config);
        }
        
        return llmService.chat(request);
    }

    @Override
    public LLMResponse chatStream(LLMRequest request) {
        log.debug("[chatStream] Agent: {}", request.getAgentId());
        request.setStreaming(true);
        return llmService.chat(request);
    }

    @Override
    public LLMResponse executeFunctionCalling(LLMRequest request) {
        log.info("[executeFunctionCalling] Agent: {}", request.getAgentId());
        
        List<Map<String, Object>> functionSchemas = functionCallingService.getFunctionSchemas();
        
        List<Map<String, Object>> customFunctions = agentCustomFunctions.get(request.getAgentId());
        if (customFunctions != null && !customFunctions.isEmpty()) {
            functionSchemas = new ArrayList<>(functionSchemas);
            functionSchemas.addAll(customFunctions);
        }
        
        request.setFunctions(functionSchemas);
        
        LLMResponse response = llmService.chat(request);
        
        if (response.hasFunctionCall()) {
            List<FunctionCall> functionCalls = response.getFunctionCalls();
            List<FunctionResult> results = functionCallingService.processFunctionCalls(functionCalls);
            
            List<Map<String, Object>> functionResults = new ArrayList<>();
            for (int i = 0; i < functionCalls.size(); i++) {
                FunctionCall call = functionCalls.get(i);
                FunctionResult result = results.get(i);
                
                Map<String, Object> functionMessage = new HashMap<>();
                functionMessage.put("role", "function");
                functionMessage.put("name", call.getName());
                functionMessage.put("content", result.toJson());
                functionResults.add(functionMessage);
            }
            
            request.addMessages(functionResults);
            
            return llmService.chat(request);
        }
        
        return response;
    }

    @Override
    public void configureAgentLLM(String agentId, Map<String, Object> llmConfig) {
        agentLLMConfigs.put(agentId, llmConfig);
        log.info("[configureAgentLLM] Configured LLM for agent: {}", agentId);
    }

    @Override
    public Map<String, Object> getAgentLLMConfig(String agentId) {
        return agentLLMConfigs.get(agentId);
    }

    @Override
    public void setSystemPrompt(String agentId, String systemPrompt) {
        agentSystemPrompts.put(agentId, systemPrompt);
        log.info("[setSystemPrompt] Set system prompt for agent: {}", agentId);
    }

    @Override
    public String getSystemPrompt(String agentId) {
        return agentSystemPrompts.get(agentId);
    }

    @Override
    public void setMaxTokens(String agentId, int maxTokens) {
        Map<String, Object> config = agentLLMConfigs.computeIfAbsent(agentId, k -> new HashMap<>());
        config.put("maxTokens", maxTokens);
        log.info("[setMaxTokens] Set max tokens for agent {}: {}", agentId, maxTokens);
    }

    @Override
    public void setTemperature(String agentId, double temperature) {
        Map<String, Object> config = agentLLMConfigs.computeIfAbsent(agentId, k -> new HashMap<>());
        config.put("temperature", temperature);
        log.info("[setTemperature] Set temperature for agent {}: {}", agentId, temperature);
    }

    @Override
    public void enableStreaming(String agentId, boolean enabled) {
        Map<String, Object> config = agentLLMConfigs.computeIfAbsent(agentId, k -> new HashMap<>());
        config.put("streaming", enabled);
        log.info("[enableStreaming] Set streaming for agent {}: {}", agentId, enabled);
    }

    @Override
    public void enableFunctionCalling(String agentId, boolean enabled) {
        Map<String, Object> config = agentLLMConfigs.computeIfAbsent(agentId, k -> new HashMap<>());
        config.put("functionCalling", enabled);
        log.info("[enableFunctionCalling] Set function calling for agent {}: {}", agentId, enabled);
    }

    @Override
    public List<Map<String, Object>> getAvailableFunctions(String agentId) {
        List<Map<String, Object>> functions = functionCallingService.getFunctionSchemas();
        
        List<Map<String, Object>> customFunctions = agentCustomFunctions.get(agentId);
        if (customFunctions != null && !customFunctions.isEmpty()) {
            functions = new ArrayList<>(functions);
            functions.addAll(customFunctions);
        }
        
        return functions;
    }

    @Override
    public void registerCustomFunction(String agentId, Map<String, Object> functionSchema) {
        agentCustomFunctions.computeIfAbsent(agentId, k -> new ArrayList<>()).add(functionSchema);
        log.info("[registerCustomFunction] Registered custom function for agent: {}", agentId);
    }

    @Override
    public void unregisterCustomFunction(String agentId, String functionName) {
        List<Map<String, Object>> functions = agentCustomFunctions.get(agentId);
        if (functions != null) {
            functions.removeIf(f -> functionName.equals(f.get("name")));
            log.info("[unregisterCustomFunction] Unregistered custom function for agent: {}", agentId);
        }
    }

    @Override
    public LLMResponse processWithRAG(String agentId, String query, List<String> knowledgeBaseIds) {
        log.info("[processWithRAG] Agent: {}, Knowledge bases: {}", agentId, knowledgeBaseIds);
        
        LLMRequest request = new LLMRequest();
        request.setAgentId(agentId);
        request.setMessage(query);
        
        Map<String, Object> config = new HashMap<>();
        config.put("useRAG", true);
        config.put("knowledgeBaseIds", knowledgeBaseIds);
        request.setConfig(config);
        
        return llmService.chat(request);
    }

    @Override
    public List<Double> getEmbedding(String agentId, String text) {
        log.debug("[getEmbedding] Agent: {}", agentId);
        return llmService.getEmbedding(text);
    }

    @Override
    public double calculateSimilarity(String agentId, String text1, String text2) {
        log.debug("[calculateSimilarity] Agent: {}", agentId);
        
        List<Double> embedding1 = llmService.getEmbedding(text1);
        List<Double> embedding2 = llmService.getEmbedding(text2);
        
        return cosineSimilarity(embedding1, embedding2);
    }

    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        if (v1 == null || v2 == null || v1.size() != v2.size()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            norm1 += v1.get(i) * v1.get(i);
            norm2 += v2.get(i) * v2.get(i);
        }
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
