package net.ooder.mvp.skill.scene.spi.executor;

import java.util.Map;

public class StepResult {
    private String stepId;
    private boolean success;
    private String errorMessage;
    private String errorCode;
    private Map<String, Object> output;
    private boolean skippable;
    private boolean retryable;
    private int retryCount;
    
    public static StepResult success(String stepId) {
        StepResult result = new StepResult();
        result.setStepId(stepId);
        result.setSuccess(true);
        return result;
    }
    
    public static StepResult success(String stepId, Map<String, Object> output) {
        StepResult result = success(stepId);
        result.setOutput(output);
        return result;
    }
    
    public static StepResult failure(String stepId, String errorMessage) {
        StepResult result = new StepResult();
        result.setStepId(stepId);
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    public static StepResult failure(String stepId, String errorCode, String errorMessage) {
        StepResult result = failure(stepId, errorMessage);
        result.setErrorCode(errorCode);
        return result;
    }
    
    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public Map<String, Object> getOutput() { return output; }
    public void setOutput(Map<String, Object> output) { this.output = output; }
    public boolean isSkippable() { return skippable; }
    public void setSkippable(boolean skippable) { this.skippable = skippable; }
    public boolean isRetryable() { return retryable; }
    public void setRetryable(boolean retryable) { this.retryable = retryable; }
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
}
