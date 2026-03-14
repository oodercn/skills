package net.ooder.skill.llm.model;

import java.util.*;

public class ChatRequest {
    private String message;
    private String providerId;
    private String model;
    private List<Map<String, String>> history;
    private Map<String, Object> options;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<Map<String, String>> getHistory() { return history; }
    public void setHistory(List<Map<String, String>> history) { this.history = history; }

    public Map<String, Object> getOptions() { return options; }
    public void setOptions(Map<String, Object> options) { this.options = options; }
}
