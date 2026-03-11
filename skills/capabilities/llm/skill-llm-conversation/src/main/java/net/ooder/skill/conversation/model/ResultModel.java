package net.ooder.skill.conversation.model;

public class ResultModel<T> {
    
    private String status;
    private String message;
    private T data;
    private Integer code;
    private Long timestamp;
    
    public ResultModel() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static <T> ResultModel<T> success(T data) {
        ResultModel<T> result = new ResultModel<>();
        result.setStatus("success");
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    
    public static <T> ResultModel<T> success(String message, T data) {
        ResultModel<T> result = success(data);
        result.setMessage(message);
        return result;
    }
    
    public static <T> ResultModel<T> error(String message) {
        ResultModel<T> result = new ResultModel<>();
        result.setStatus("error");
        result.setMessage(message);
        return result;
    }
    
    public static <T> ResultModel<T> notFound(String message) {
        ResultModel<T> result = new ResultModel<>();
        result.setStatus("error");
        result.setMessage(message);
        result.setCode(404);
        return result;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
