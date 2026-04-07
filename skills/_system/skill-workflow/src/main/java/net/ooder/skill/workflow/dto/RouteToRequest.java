package net.ooder.skill.workflow.dto;

import java.util.List;

public class RouteToRequest {
    
    private String activityInstId;
    private List<String> nextActivityDefIds;
    private List<String> performerIds;
    private List<String> readerIds;

    public String getActivityInstId() { return activityInstId; }
    public void setActivityInstId(String activityInstId) { this.activityInstId = activityInstId; }
    public List<String> getNextActivityDefIds() { return nextActivityDefIds; }
    public void setNextActivityDefIds(List<String> nextActivityDefIds) { this.nextActivityDefIds = nextActivityDefIds; }
    public List<String> getPerformerIds() { return performerIds; }
    public void setPerformerIds(List<String> performerIds) { this.performerIds = performerIds; }
    public List<String> getReaderIds() { return readerIds; }
    public void setReaderIds(List<String> readerIds) { this.readerIds = readerIds; }
}
