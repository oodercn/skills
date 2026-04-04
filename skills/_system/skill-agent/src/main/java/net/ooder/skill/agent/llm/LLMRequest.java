package net.ooder.skill.agent.llm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LLMRequest {
    
    private String agentId;
    private String conversationId;
    private String message;
    private List<Map<String, String>> history;
    private Map<String, Object> parameters;
    private String model;
    private String provider;
    private boolean stream;
    private String systemPrompt;
    private Map<String, Object> config;
    private List<Map<String, Object>> functions;
    private List<Map<String, Object>> messages;
    
    public String getAgentId() {
        return agentId;
    }
    
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
    
    public String getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<Map<String, String>> getHistory() {
        return history;
    }
    
    public void setHistory(List<Map<String, String>> history) {
        this.history = history;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public boolean isStream() {
        return stream;
    }
    
    public void setStream(boolean stream) {
        this.stream = stream;
    }
    
    public void setStreaming(boolean streaming) {
        this.stream = streaming;
    }
    
    public String getSystemPrompt() {
        return systemPrompt;
    }
    
    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
    
    public List<Map<String, Object>> getFunctions() {
        return functions;
    }
    
    public void setFunctions(List<Map<String, Object>> functions) {
        this.functions = functions;
    }
    
    public List<Map<String, Object>> getMessages() {
        return messages;
    }
    
    public void setMessages(List<Map<String, Object>> messages) {
        this.messages = messages;
    }
    
    public void addMessages(List<Map<String, Object>> newMessages) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.addAll(newMessages);
    }
}
