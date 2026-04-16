package net.ooder.bpm.designer.llm;

import java.util.List;
import java.util.Map;

public interface LLMService {
    
    LLMResponse chat(String prompt);
    
    LLMResponse chat(String systemPrompt, String userPrompt);
    
    LLMResponse chatWithFunctions(String prompt, List<Map<String, Object>> functions);
    
    LLMResponse chatWithFunctions(String systemPrompt, String userPrompt, List<Map<String, Object>> functions);
    
    LLMResponse chatWithFunctionResult(String originalPrompt, String functionName, Object result);
    
    LLMResponse chatWithMessages(List<Map<String, Object>> messages);
    
    LLMResponse chatWithMessages(List<Map<String, Object>> messages, List<Map<String, Object>> tools);
    
    LLMResponse chatWithMessagesAndToolResults(
        List<Map<String, Object>> messages,
        List<Map<String, Object>> tools,
        String toolCallId,
        String functionName,
        Object toolResult
    );
    
    void chatWithMessagesStream(
        List<Map<String, Object>> messages,
        List<Map<String, Object>> tools,
        java.util.function.Consumer<String> onContent,
        java.util.function.Consumer<List<FunctionCall>> onToolCalls,
        java.util.function.Consumer<LLMResponse> onComplete,
        java.util.function.Consumer<Exception> onError
    );
    
    boolean isAvailable();
    
    String getModelName();
}
