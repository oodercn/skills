package net.ooder.skill.install.scene.model;

public class ResultModel<T> {

    private int code;
    private String message;
    private T data;
    private boolean success;
    private String requestId;

    private static final int CODE_SUCCESS = 200;
    private static final int CODE_ERROR = 500;
    private static final int CODE_NOT_FOUND = 404;

    public ResultModel() {}

    public ResultModel(int code, String message, T data, boolean success, String requestId) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
        this.requestId = requestId;
    }

    public static <T> ResultModel<T> success(T data) {
        return new ResultModel<>(CODE_SUCCESS, "success", data, true, generateRequestId());
    }

    public static <T> ResultModel<T> error(String message) {
        return new ResultModel<>(CODE_ERROR, message, null, false, generateRequestId());
    }

    public static <T> ResultModel<T> notFound(String message) {
        return new ResultModel<>(CODE_NOT_FOUND, message, null, false, generateRequestId());
    }

    private static String generateRequestId() {
        return "REQ_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
}