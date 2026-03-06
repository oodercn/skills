package net.ooder.skill.test.dto;

import java.util.List;
import java.util.Map;

public class ApprovalProcessDTO {
    private String id;
    private String formId;
    private String applicant;
    private String approver;
    private String title;
    private String type;
    private String priority;
    private Map<String, Object> formData;
    private String status;
    private String currentNode;
    private List<Map<String, Object>> timeline;
    private List<String> attachments;
    private Long createdAt;
    private Long updatedAt;
    private Long completedAt;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFormId() { return formId; }
    public void setFormId(String formId) { this.formId = formId; }
    public String getApplicant() { return applicant; }
    public void setApplicant(String applicant) { this.applicant = applicant; }
    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Map<String, Object> getFormData() { return formData; }
    public void setFormData(Map<String, Object> formData) { this.formData = formData; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCurrentNode() { return currentNode; }
    public void setCurrentNode(String currentNode) { this.currentNode = currentNode; }
    public List<Map<String, Object>> getTimeline() { return timeline; }
    public void setTimeline(List<Map<String, Object>> timeline) { this.timeline = timeline; }
    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    public Long getCompletedAt() { return completedAt; }
    public void setCompletedAt(Long completedAt) { this.completedAt = completedAt; }
}
