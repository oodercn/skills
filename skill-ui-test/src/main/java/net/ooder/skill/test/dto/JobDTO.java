package net.ooder.skill.test.dto;

import java.util.List;

public class JobDTO {
    private String id;
    private String title;
    private String department;
    private String location;
    private String type;
    private Integer headcount;
    private Double minSalary;
    private Double maxSalary;
    private String description;
    private List<String> requirements;
    private String status;
    private String hrName;
    private String hrContact;
    private Long createdAt;
    private Long updatedAt;
    private Long publishDate;
    private Long closeDate;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getHeadcount() { return headcount; }
    public void setHeadcount(Integer headcount) { this.headcount = headcount; }
    public Double getMinSalary() { return minSalary; }
    public void setMinSalary(Double minSalary) { this.minSalary = minSalary; }
    public Double getMaxSalary() { return maxSalary; }
    public void setMaxSalary(Double maxSalary) { this.maxSalary = maxSalary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getRequirements() { return requirements; }
    public void setRequirements(List<String> requirements) { this.requirements = requirements; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHrName() { return hrName; }
    public void setHrName(String hrName) { this.hrName = hrName; }
    public String getHrContact() { return hrContact; }
    public void setHrContact(String hrContact) { this.hrContact = hrContact; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    public Long getPublishDate() { return publishDate; }
    public void setPublishDate(Long publishDate) { this.publishDate = publishDate; }
    public Long getCloseDate() { return closeDate; }
    public void setCloseDate(Long closeDate) { this.closeDate = closeDate; }
}
