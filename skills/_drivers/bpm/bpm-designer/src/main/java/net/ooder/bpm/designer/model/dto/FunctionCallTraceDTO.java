package net.ooder.bpm.designer.model.dto;

import java.util.Map;

public class FunctionCallTraceDTO {
    
    private int sequence;
    private String functionName;
    private Map<String, Object> arguments;
    private Object result;
    private String error;
    private long executionTime;
    private String llmReasoning;
    private boolean success;
    
    public FunctionCallTraceDTO() {}
    
    public static FunctionCallTraceDTO success(int sequence, String functionName, 
            Map<String, Object> arguments, Object result, long executionTime) {
        FunctionCallTraceDTO trace = new FunctionCallTraceDTO();
        trace.setSequence(sequence);
        trace.setFunctionName(functionName);
        trace.setArguments(arguments);
        trace.setResult(result);
        trace.setExecutionTime(executionTime);
        trace.setSuccess(true);
        return trace;
    }
    
    public static FunctionCallTraceDTO failure(int sequence, String functionName, 
            Map<String, Object> arguments, String error, long executionTime) {
        FunctionCallTraceDTO trace = new FunctionCallTraceDTO();
        trace.setSequence(sequence);
        trace.setFunctionName(functionName);
        trace.setArguments(arguments);
        trace.setError(error);
        trace.setExecutionTime(executionTime);
        trace.setSuccess(false);
        return trace;
    }
    
    public int getSequence() { return sequence; }
    public void setSequence(int sequence) { this.sequence = sequence; }
    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }
    public Map<String, Object> getArguments() { return arguments; }
    public void setArguments(Map<String, Object> arguments) { this.arguments = arguments; }
    public Object getResult() { return result; }
    public void setResult(Object result) { this.result = result; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
    public String getLlmReasoning() { return llmReasoning; }
    public void setLlmReasoning(String llmReasoning) { this.llmReasoning = llmReasoning; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
