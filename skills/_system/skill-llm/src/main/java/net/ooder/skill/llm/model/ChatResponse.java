package net.ooder.skill.llm.model;

public class ChatResponse {
    private String content;
    private String providerId;
    private String model;
    private Integer tokensUsed;
    private Long latencyMs;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(Integer tokensUsed) { this.tokensUsed = tokensUsed; }

    public Long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Long latencyMs) { this.latencyMs = latencyMs; }
}
