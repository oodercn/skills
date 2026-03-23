package net.ooder.skill.approval.dto;

import java.util.List;
import java.util.Map;

public class ApprovalCreateRequest {
    private String type;
    private String priority;
    private String reason;
    private String approver;
    private Map<String, Object> detail;
    private List<String> attachments;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }
    public Map<String, Object> getDetail() { return detail; }
    public void setDetail(Map<String, Object> detail) { this.detail = detail; }
    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
}
