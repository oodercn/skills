package net.ooder.skill.llm.base;

import java.util.List;
import java.util.Map;

public interface LlmProvider {
    
    String getProviderId();
    
    String getProviderName();
    
    List<LlmModel> getAvailableModels();
    
    LlmResponse chat(LlmRequest request);
    
    void streamChat(LlmRequest request, LlmStreamCallback callback);
    
    LlmResponse complete(LlmRequest request);
    
    int[] getEmbedding(String text);
    
    int countTokens(String text);
    
    boolean isAvailable();
    
    Map<String, Object> getProviderConfig();
}
