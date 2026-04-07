package net.ooder.skill.workflow.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResultModel<T> {
    
    private int code;
    private String status;
    private String message;
    private T data;
    private String timestamp;

    public ResultModel() {
        this.code = 200;
        this.status = "success";
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public ResultModel(int code, String message, T data, boolean success) {
        this.code = code;
        this.status = success ? "success" : "error";
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public static <T> ResultModel<T> success(T data) {
        return new ResultModel<>(200, "OK", data, true);
    }

    public static <T> ResultModel<T> success(T data, String message) {
        return new ResultModel<>(200, message, data, true);
    }

    public static <T> ResultModel<T> success() {
        return new ResultModel<>(200, "OK", null, true);
    }

    public static <T> ResultModel<T> fail(String message) {
        return new ResultModel<>(500, message, null, false);
    }

    public static <T> ResultModel<T> fill(String message) {
        return new ResultModel<>(200, message, null, true);
    }
}
