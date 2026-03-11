package net.ooder.skill.management.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SkillResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String skillId;
    private boolean success;
    private Object data;
    private String message;
    private String errorCode;
    private Map<String, Object> metadata;
    private long executionTime;
    private long timestamp;

    public SkillResult() {
        this.success = true;
        this.metadata = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    public static SkillResult success() {
        return new SkillResult().success(true);
    }

    public static SkillResult success(Object data) {
        return new SkillResult().success(true).data(data);
    }

    public static SkillResult success(String message) {
        return new SkillResult().success(true).message(message);
    }

    public static SkillResult success(Object data, String message) {
        return new SkillResult().success(true).data(data).message(message);
    }

    public static SkillResult failure(String message) {
        return new SkillResult().success(false).message(message);
    }

    public static SkillResult failure(String errorCode, String message) {
        return new SkillResult().success(false).errorCode(errorCode).message(message);
    }

    public static SkillResult failure(String skillId, String errorCode, String message) {
        return new SkillResult().skillId(skillId).success(false).errorCode(errorCode).message(message);
    }

    public SkillResult skillId(String skillId) {
        this.skillId = skillId;
        return this;
    }

    public SkillResult success(boolean success) {
        this.success = success;
        return this;
    }

    public SkillResult data(Object data) {
        this.data = data;
        return this;
    }

    public SkillResult message(String message) {
        this.message = message;
        return this;
    }

    public SkillResult errorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public SkillResult metadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }

    public SkillResult executionTime(long executionTime) {
        this.executionTime = executionTime;
        return this;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("skillId", skillId);
        map.put("success", success);
        map.put("data", data);
        map.put("message", message);
        map.put("errorCode", errorCode);
        map.put("executionTime", executionTime);
        map.put("timestamp", timestamp);
        return map;
    }
}
