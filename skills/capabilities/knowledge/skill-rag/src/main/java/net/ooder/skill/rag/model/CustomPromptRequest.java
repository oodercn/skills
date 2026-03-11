package net.ooder.skill.rag.model;

public class CustomPromptRequest {
    private String query;
    private String context;
    private String systemPrompt;
    
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    
    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
}
