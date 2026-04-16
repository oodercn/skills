package net.ooder.skill.tenant.model;

public class ResultModel<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;

    public ResultModel() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResultModel<T> success(T data) {
        ResultModel<T> r = new ResultModel<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    public static <T> ResultModel<T> success(T data, String message) {
        ResultModel<T> r = new ResultModel<>();
        r.setCode(200);
        r.setMessage(message);
        r.setData(data);
        return r;
    }

    public static <T> ResultModel<T> error(String message) {
        ResultModel<T> r = new ResultModel<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
