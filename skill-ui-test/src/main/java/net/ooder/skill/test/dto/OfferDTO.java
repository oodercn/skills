package net.ooder.skill.test.dto;

public class OfferDTO {
    private String id;
    private String resumeId;
    private String candidateName;
    private String jobId;
    private String jobTitle;
    private Double offeredSalary;
    private String probationPeriod;
    private String entryDate;
    private String benefits;
    private String approver;
    private String status;
    private String rejectReason;
    private Long createdAt;
    private Long updatedAt;
    private Long approvedAt;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getResumeId() { return resumeId; }
    public void setResumeId(String resumeId) { this.resumeId = resumeId; }
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public Double getOfferedSalary() { return offeredSalary; }
    public void setOfferedSalary(Double offeredSalary) { this.offeredSalary = offeredSalary; }
    public String getProbationPeriod() { return probationPeriod; }
    public void setProbationPeriod(String probationPeriod) { this.probationPeriod = probationPeriod; }
    public String getEntryDate() { return entryDate; }
    public void setEntryDate(String entryDate) { this.entryDate = entryDate; }
    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }
    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    public Long getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Long approvedAt) { this.approvedAt = approvedAt; }
}
