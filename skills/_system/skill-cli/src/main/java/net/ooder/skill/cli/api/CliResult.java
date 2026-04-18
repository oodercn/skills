package net.ooder.skill.cli.api;

import java.util.Map;

public class CliResult {
    
    private boolean success;
    private int exitCode;
    private String message;
    private Object data;
    private Map<String, Object> metadata;
    
    public static CliResult success() {
        CliResult result = new CliResult();
        result.setSuccess(true);
        result.setExitCode(0);
        return result;
    }
    
    public static CliResult success(String message) {
        CliResult result = success();
        result.setMessage(message);
        return result;
    }
    
    public static CliResult success(Object data) {
        CliResult result = success();
        result.setData(data);
        return result;
    }
    
    public static CliResult failure(String message) {
        CliResult result = new CliResult();
        result.setSuccess(false);
        result.setExitCode(1);
        result.setMessage(message);
        return result;
    }
    
    public static CliResult failure(int exitCode, String message) {
        CliResult result = failure(message);
        result.setExitCode(exitCode);
        return result;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public int getExitCode() {
        return exitCode;
    }
    
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
