package net.ooder.skill.setup.model;

public class ResultModel<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ResultModel<T> success(T data) {
        ResultModel<T> result = new ResultModel<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> ResultModel<T> error(String message) {
        ResultModel<T> result = new ResultModel<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
