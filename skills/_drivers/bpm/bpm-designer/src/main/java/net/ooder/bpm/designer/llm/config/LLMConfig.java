package net.ooder.bpm.designer.llm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLMConfig {
    
    private boolean enabled = false;
    private String provider = "openai";
    private String model = "gpt-4";
    private String apiKey;
    private String apiEndpoint = "https://api.openai.com/v1/chat/completions";
    private double temperature = 0.7;
    private int maxTokens = 4096;
    private int timeout = 60000;
    private int maxRetries = 3;
    private int maxToolCallRounds = 5;
    private int maxContextMessages = 20;
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getApiEndpoint() { return apiEndpoint; }
    public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    public int getMaxToolCallRounds() { return maxToolCallRounds; }
    public void setMaxToolCallRounds(int maxToolCallRounds) { this.maxToolCallRounds = maxToolCallRounds; }
    public int getMaxContextMessages() { return maxContextMessages; }
    public void setMaxContextMessages(int maxContextMessages) { this.maxContextMessages = maxContextMessages; }
}
