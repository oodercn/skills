package net.ooder.skill.test.dto;

public class ApprovalRecordDTO {
    private String id;
    private String processId;
    private String type;
    private String applicant;
    private String approver;
    private String action;
    private String comment;
    private Long createdAt;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getApplicant() { return applicant; }
    public void setApplicant(String applicant) { this.applicant = applicant; }
    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
}
