package net.ooder.skill.approval.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Approval {
    private String id;
    private String type;
    private String typeName;
    private String applicant;
    private String applicantId;
    private LocalDateTime applyTime;
    private String currentNode;
    private String currentApprover;
    private String priority;
    private String status;
    private String reason;
    private Map<String, Object> detail;
    private List<TimelineItem> timeline;
    private List<String> attachments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static class TimelineItem {
        private String node;
        private String operator;
        private String operatorId;
        private LocalDateTime time;
        private String status;
        private String comment;

        public String getNode() { return node; }
        public void setNode(String node) { this.node = node; }
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
        public String getOperatorId() { return operatorId; }
        public void setOperatorId(String operatorId) { this.operatorId = operatorId; }
        public LocalDateTime getTime() { return time; }
        public void setTime(LocalDateTime time) { this.time = time; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public String getApplicant() { return applicant; }
    public void setApplicant(String applicant) { this.applicant = applicant; }
    public String getApplicantId() { return applicantId; }
    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }
    public LocalDateTime getApplyTime() { return applyTime; }
    public void setApplyTime(LocalDateTime applyTime) { this.applyTime = applyTime; }
    public String getCurrentNode() { return currentNode; }
    public void setCurrentNode(String currentNode) { this.currentNode = currentNode; }
    public String getCurrentApprover() { return currentApprover; }
    public void setCurrentApprover(String currentApprover) { this.currentApprover = currentApprover; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Map<String, Object> getDetail() { return detail; }
    public void setDetail(Map<String, Object> detail) { this.detail = detail; }
    public List<TimelineItem> getTimeline() { return timeline; }
    public void setTimeline(List<TimelineItem> timeline) { this.timeline = timeline; }
    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
