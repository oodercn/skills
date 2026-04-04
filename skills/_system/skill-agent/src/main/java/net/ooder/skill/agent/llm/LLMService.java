package net.ooder.skill.agent.llm;

import java.util.List;

public interface LLMService {
    
    LLMResponse chat(LLMRequest request);
    
    LLMResponse chatWithHistory(String agentId, String conversationId, 
            String message, java.util.List<java.util.Map<String, String>> history);
    
    void chatStream(LLMRequest request, 
            java.util.function.Consumer<String> onChunk,
            java.util.function.Consumer<LLMResponse> onComplete,
            java.util.function.Consumer<Exception> onError);
    
    default List<Double> getEmbedding(String text) {
        return List.of();
    }
}
