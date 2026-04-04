package net.ooder.skill.tenant.model;

public class ResultModel<T> {

    private int code;
    private String status;
    private String message;
    private T data;
    private long timestamp;
    private String requestId;

    public ResultModel() {
        this.timestamp = System.currentTimeMillis();
        this.status = "success";
    }

    public static <T> ResultModel<T> success(T data) {
        ResultModel<T> r = new ResultModel<>();
        r.setCode(200);
        r.setStatus("success");
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    public static <T> ResultModel<T> success(T data, String message) {
        ResultModel<T> r = new ResultModel<>();
        r.setCode(200);
        r.setStatus("success");
        r.setMessage(message);
        r.setData(data);
        return r;
    }

    public static <T> ResultModel<T> error(String message) {
        ResultModel<T> r = new ResultModel<>();
        r.setCode(500);
        r.setStatus("error");
        r.setMessage(message);
        return r;
    }

    public static <T> ResultModel<T> error(int code, String message) {
        ResultModel<T> r = new ResultModel<>();
        r.setCode(code);
        r.setStatus("error");
        r.setMessage(message);
        return r;
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
