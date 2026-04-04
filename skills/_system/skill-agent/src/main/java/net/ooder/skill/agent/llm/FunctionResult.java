package net.ooder.skill.agent.llm;

import java.util.Map;

public class FunctionResult {
    
    private String callId;
    private String functionName;
    private Object result;
    private boolean success;
    private String errorMessage;
    
    public String getCallId() {
        return callId;
    }
    
    public void setCallId(String callId) {
        this.callId = callId;
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public static FunctionResult success(Object result) {
        FunctionResult fr = new FunctionResult();
        fr.setSuccess(true);
        fr.setResult(result);
        return fr;
    }
    
    public static FunctionResult error(String errorMessage) {
        FunctionResult fr = new FunctionResult();
        fr.setSuccess(false);
        fr.setErrorMessage(errorMessage);
        return fr;
    }
    
    public String toJson() {
        if (success) {
            if (result == null) {
                return "{}";
            }
            if (result instanceof String) {
                return "\"" + result + "\"";
            }
            return result.toString();
        } else {
            return "{\"error\": \"" + errorMessage + "\"}";
        }
    }
}
