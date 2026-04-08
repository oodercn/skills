package net.ooder.skill.template.dto;

public class TemplateVersionDTO {
    
    private int version;
    private long timestamp;
    private String changedBy;
    private String changeDescription;
    private String status;

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
