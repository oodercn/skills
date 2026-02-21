package net.ooder.skill.agent.provider;

public class Result<T> {
    
    private boolean success;
    private T data;
    private String error;
    private int code;
    private long timestamp;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setData(data);
        result.setCode(200);
        return result;
    }
    
    public static <T> Result<T> success() {
        return success(null);
    }
    
    public static <T> Result<T> error(String error) {
        return error(error, 500);
    }
    
    public static <T> Result<T> error(String error, int code) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setError(error);
        result.setCode(code);
        return result;
    }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public long getTimestamp() { return timestamp; }
}
