package net.ooder.skill.todo.dto;

public class TaskDTO {
    
    private String id;
    private String status;
    private String completedTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCompletedTime() { return completedTime; }
    public void setCompletedTime(String completedTime) { this.completedTime = completedTime; }
}
