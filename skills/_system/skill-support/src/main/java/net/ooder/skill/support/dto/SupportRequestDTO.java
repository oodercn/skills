package net.ooder.skill.support.dto;

public class SupportRequestDTO {
    private String id;
    private String subject;
    private String description;
    private String status;
    private String priority;
    private Long createTime;
    private Long updateTime;
    private Long closeTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
    
    public Long getUpdateTime() { return updateTime; }
    public void setUpdateTime(Long updateTime) { this.updateTime = updateTime; }
    
    public Long getCloseTime() { return closeTime; }
    public void setCloseTime(Long closeTime) { this.closeTime = closeTime; }
}
