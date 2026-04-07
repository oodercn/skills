package net.ooder.skill.workflow.dto;

import java.util.List;

public class CopyToRequest {
    
    private String activityInstId;
    private List<String> readerIds;

    public String getActivityInstId() { return activityInstId; }
    public void setActivityInstId(String activityInstId) { this.activityInstId = activityInstId; }
    public List<String> getReaderIds() { return readerIds; }
    public void setReaderIds(List<String> readerIds) { this.readerIds = readerIds; }
}
