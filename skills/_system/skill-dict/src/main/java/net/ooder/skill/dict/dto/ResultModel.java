package net.ooder.skill.dict.dto;

import java.util.Random;

public class ResultModel<T> {
    private int code;
    private String status;
    private String message;
    private T data;
    private long timestamp;
    private String requestId;

    public ResultModel() {
        this.timestamp = System.currentTimeMillis();
        this.requestId = "REQ_" + timestamp + "_" + new Random().nextInt(1000);
    }

    public static <T> ResultModel<T> success(T data) {
        ResultModel<T> result = new ResultModel<>();
        result.setCode(200);
        result.setStatus("success");
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> ResultModel<T> error(int code, String message) {
        ResultModel<T> result = new ResultModel<>();
        result.setCode(code);
        result.setStatus("error");
        result.setMessage(message);
        return result;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
}
