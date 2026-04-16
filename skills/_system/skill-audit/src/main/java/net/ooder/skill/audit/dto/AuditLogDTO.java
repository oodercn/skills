package net.ooder.skill.audit.dto;

public class AuditLogDTO {
    
    private String recordId;
    private EventType eventType;
    private Result result;
    private long timestamp;
    private String userId;
    private String agentId;
    private String resourceType;
    private String resourceId;
    private String action;
    private String detail;
    private String ipAddress;
    private String sessionId;
    private String userAgent;
    private String requestId;
    private long duration;

    public enum EventType {
        AUTH("认证事件"),
        SCENE("场景事件"),
        CAPABILITY("能力事件"),
        AGENT("代理事件"),
        SYSTEM("系统事件"),
        USER("用户事件"),
        DATA("数据事件");

        private final String name;

        EventType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Result {
        SUCCESS("成功"),
        FAILURE("失败"),
        ERROR("错误");

        private final String name;

        Result(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public Result getResult() { return result; }
    public void setResult(Result result) { this.result = result; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
}
