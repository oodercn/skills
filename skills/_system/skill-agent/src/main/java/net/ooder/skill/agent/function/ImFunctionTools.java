package net.ooder.skill.agent.function;

import net.ooder.skill.agent.llm.LLMService;
import net.ooder.skill.agent.llm.LLMRequest;
import net.ooder.skill.agent.llm.LLMResponse;
import net.ooder.skill.agent.llm.FunctionCall;
import net.ooder.skill.agent.llm.FunctionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ImFunctionTools {

    private static final Logger log = LoggerFactory.getLogger(ImFunctionTools.class);

    @Autowired
    private LLMService llmService;

    @Autowired
    private FunctionCallingService functionCallingService;

    private Map<String, List<FunctionResult>> conversationResults = new ConcurrentHashMap<>();

    public LLMResponse processWithFunctionCalling(LLMRequest request) {
        log.info("[processWithFunctionCalling] Processing request for agent: {}", request.getAgentId());
        
        List<Map<String, Object>> functionSchemas = functionCallingService.getFunctionSchemas();
        request.setFunctions(functionSchemas);
        
        LLMResponse response = llmService.chat(request);
        
        if (response.hasFunctionCall()) {
            log.info("[processWithFunctionCalling] Function call detected: {}", 
                response.getFunctionCalls().get(0).getName());
            
            List<FunctionCall> functionCalls = response.getFunctionCalls();
            List<FunctionResult> results = functionCallingService.processFunctionCalls(functionCalls);
            
            String conversationId = request.getConversationId();
            conversationResults.computeIfAbsent(conversationId, k -> new ArrayList<>()).addAll(results);
            
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

    public List<FunctionResult> getConversationResults(String conversationId) {
        return conversationResults.getOrDefault(conversationId, Collections.emptyList());
    }

    public void clearConversationResults(String conversationId) {
        conversationResults.remove(conversationId);
    }

    public Map<String, Object> buildFunctionCallPrompt(String context, String task) {
        Map<String, Object> prompt = new HashMap<>();
        prompt.put("context", context);
        prompt.put("task", task);
        prompt.put("available_functions", functionCallingService.getFunctionSchemas());
        return prompt;
    }

    public boolean shouldUseFunctionCalling(String userMessage) {
        if (userMessage == null) return false;
        
        String lowerMessage = userMessage.toLowerCase();
        return lowerMessage.contains("agent") || 
               lowerMessage.contains("status") ||
               lowerMessage.contains("send") ||
               lowerMessage.contains("message") ||
               lowerMessage.contains("collaborate") ||
               lowerMessage.contains("list") ||
               lowerMessage.contains("available");
    }

    public List<String> suggestFunctions(String userMessage) {
        List<String> suggestions = new ArrayList<>();
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("status") && lowerMessage.contains("agent")) {
            suggestions.add("get_agent_status");
        }
        if (lowerMessage.contains("send") || lowerMessage.contains("message")) {
            suggestions.add("send_message_to_agent");
        }
        if (lowerMessage.contains("list") && lowerMessage.contains("agent")) {
            suggestions.add("list_available_agents");
        }
        if (lowerMessage.contains("collaborate") || lowerMessage.contains("collaboration")) {
            suggestions.add("request_collaboration");
        }
        
        return suggestions;
    }
}
