package net.ooder.skill.agent.service;

import net.ooder.skill.agent.llm.LLMRequest;
import net.ooder.skill.agent.llm.LLMResponse;

import java.util.List;
import java.util.Map;

public interface AgentLLMService {
    
    LLMResponse chat(LLMRequest request);
    
    LLMResponse chatWithHistory(String agentId, String conversationId, String message, 
            List<Map<String, String>> history);
    
    LLMResponse chatStream(LLMRequest request);
    
    LLMResponse executeFunctionCalling(LLMRequest request);
    
    void configureAgentLLM(String agentId, Map<String, Object> llmConfig);
    
    Map<String, Object> getAgentLLMConfig(String agentId);
    
    void setSystemPrompt(String agentId, String systemPrompt);
    
    String getSystemPrompt(String agentId);
    
    void setMaxTokens(String agentId, int maxTokens);
    
    void setTemperature(String agentId, double temperature);
    
    void enableStreaming(String agentId, boolean enabled);
    
    void enableFunctionCalling(String agentId, boolean enabled);
    
    List<Map<String, Object>> getAvailableFunctions(String agentId);
    
    void registerCustomFunction(String agentId, Map<String, Object> functionSchema);
    
    void unregisterCustomFunction(String agentId, String functionName);
    
    LLMResponse processWithRAG(String agentId, String query, List<String> knowledgeBaseIds);
    
    List<Double> getEmbedding(String agentId, String text);
    
    double calculateSimilarity(String agentId, String text1, String text2);
}
