package net.ooder.skill.llm.base;

public class LlmResponse {
    
    private String id;
    private String model;
    private String content;
    private LlmUsage usage;
    private String finishReason;
    private long created;
    private String providerId;
    
    public LlmResponse() {
        this.created = System.currentTimeMillis() / 1000;
    }
    
    public static LlmResponse of(String content) {
        LlmResponse response = new LlmResponse();
        response.setContent(content);
        return response;
    }
    
    public static LlmResponse of(String model, String content) {
        LlmResponse response = new LlmResponse();
        response.setModel(model);
        response.setContent(content);
        return response;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LlmUsage getUsage() { return usage; }
    public void setUsage(LlmUsage usage) { this.usage = usage; }
    public String getFinishReason() { return finishReason; }
    public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
    public long getCreated() { return created; }
    public void setCreated(long created) { this.created = created; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
}
