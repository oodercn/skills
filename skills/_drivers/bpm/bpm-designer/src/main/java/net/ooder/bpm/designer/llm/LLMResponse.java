package net.ooder.bpm.designer.llm;

import java.util.List;
import java.util.Map;

public class LLMResponse {
    
    private boolean success;
    private String content;
    private List<FunctionCall> functionCalls;
    private Map<String, Object> metadata;
    private String error;
    private int tokensUsed;
    private long executionTime;
    
    public static LLMResponse success(String content) {
        LLMResponse response = new LLMResponse();
        response.setSuccess(true);
        response.setContent(content);
        return response;
    }
    
    public static LLMResponse withFunctionCalls(List<FunctionCall> functionCalls) {
        LLMResponse response = new LLMResponse();
        response.setSuccess(true);
        response.setFunctionCalls(functionCalls);
        return response;
    }
    
    public static LLMResponse failure(String error) {
        LLMResponse response = new LLMResponse();
        response.setSuccess(false);
        response.setError(error);
        return response;
    }
    
    public boolean hasFunctionCalls() {
        return functionCalls != null && !functionCalls.isEmpty();
    }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<FunctionCall> getFunctionCalls() { return functionCalls; }
    public void setFunctionCalls(List<FunctionCall> functionCalls) { this.functionCalls = functionCalls; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public int getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }
    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
}
