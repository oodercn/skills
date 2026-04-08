package net.ooder.bpm.designer.llm;

import java.util.List;
import java.util.Map;

public interface LLMService {
    
    LLMResponse chat(String prompt);
    
    LLMResponse chat(String systemPrompt, String userPrompt);
    
    LLMResponse chatWithFunctions(String prompt, List<Map<String, Object>> functions);
    
    LLMResponse chatWithFunctions(String systemPrompt, String userPrompt, List<Map<String, Object>> functions);
    
    LLMResponse chatWithFunctionResult(String originalPrompt, String functionName, Object result);
    
    boolean isAvailable();
    
    String getModelName();
}
