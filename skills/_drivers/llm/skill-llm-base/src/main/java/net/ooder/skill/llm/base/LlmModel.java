package net.ooder.skill.llm.base;

public class LlmModel {
    
    private String id;
    private String name;
    private String provider;
    private int contextLength;
    private boolean supportsStreaming;
    private boolean supportsFunctionCall;
    private boolean supportsVision;
    private double inputPricePerToken;
    private double outputPricePerToken;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public int getContextLength() { return contextLength; }
    public void setContextLength(int contextLength) { this.contextLength = contextLength; }
    public boolean isSupportsStreaming() { return supportsStreaming; }
    public void setSupportsStreaming(boolean supportsStreaming) { this.supportsStreaming = supportsStreaming; }
    public boolean isSupportsFunctionCall() { return supportsFunctionCall; }
    public void setSupportsFunctionCall(boolean supportsFunctionCall) { this.supportsFunctionCall = supportsFunctionCall; }
    public boolean isSupportsVision() { return supportsVision; }
    public void setSupportsVision(boolean supportsVision) { this.supportsVision = supportsVision; }
    public double getInputPricePerToken() { return inputPricePerToken; }
    public void setInputPricePerToken(double inputPricePerToken) { this.inputPricePerToken = inputPricePerToken; }
    public double getOutputPricePerToken() { return outputPricePerToken; }
    public void setOutputPricePerToken(double outputPricePerToken) { this.outputPricePerToken = outputPricePerToken; }
}
