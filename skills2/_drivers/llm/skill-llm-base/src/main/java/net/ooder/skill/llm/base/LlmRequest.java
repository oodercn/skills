package net.ooder.skill.llm.base;

import java.util.List;
import java.util.Map;

public class LlmRequest {
    
    private String model;
    private List<LlmMessage> messages;
    private double temperature;
    private int maxTokens;
    private double topP;
    private List<String> stop;
    private Map<String, Object> extraParams;
    
    public LlmRequest() {
        this.temperature = 0.7;
        this.maxTokens = 2048;
        this.topP = 1.0;
    }
    
    public static LlmRequest of(String model, String systemPrompt, String userMessage) {
        LlmRequest request = new LlmRequest();
        request.setModel(model);
        request.setMessages(java.util.Arrays.asList(
            LlmMessage.system(systemPrompt),
            LlmMessage.user(userMessage)
        ));
        return request;
    }
    
    public static LlmRequest of(String model, String userMessage) {
        LlmRequest request = new LlmRequest();
        request.setModel(model);
        request.setMessages(java.util.Collections.singletonList(
            LlmMessage.user(userMessage)
        ));
        return request;
    }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public List<LlmMessage> getMessages() { return messages; }
    public void setMessages(List<LlmMessage> messages) { this.messages = messages; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    public double getTopP() { return topP; }
    public void setTopP(double topP) { this.topP = topP; }
    public List<String> getStop() { return stop; }
    public void setStop(List<String> stop) { this.stop = stop; }
    public Map<String, Object> getExtraParams() { return extraParams; }
    public void setExtraParams(Map<String, Object> extraParams) { this.extraParams = extraParams; }
}
