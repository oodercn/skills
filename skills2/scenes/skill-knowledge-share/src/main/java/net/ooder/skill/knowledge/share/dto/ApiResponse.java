package net.ooder.skill.knowledge.share.dto;

public class ApiResponse<T> {
    private int code;
    private String status;
    private String message;
    private T data;
    private String timestamp;

    public ApiResponse() {
        this.code = 200;
        this.status = "success";
        this.message = "操作成功";
        this.timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date());
    }

    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
