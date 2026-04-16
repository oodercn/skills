package net.ooder.skill.management.dto;

public class SkillExecuteResultDTO {
    
    private String status;
    private Object data;
    private String message;
    private long executionTime;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
}
