package net.ooder.skill.test.dto;

import java.util.List;
import java.util.Map;

public class ApprovalFormDTO {
    private String id;
    private String type;
    private String title;
    private String description;
    private Map<String, Object> formConfig;
    private List<Map<String, Object>> fields;
    private String status;
    private Long createdAt;
    private Long updatedAt;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, Object> getFormConfig() { return formConfig; }
    public void setFormConfig(Map<String, Object> formConfig) { this.formConfig = formConfig; }
    public List<Map<String, Object>> getFields() { return fields; }
    public void setFields(List<Map<String, Object>> fields) { this.fields = fields; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
