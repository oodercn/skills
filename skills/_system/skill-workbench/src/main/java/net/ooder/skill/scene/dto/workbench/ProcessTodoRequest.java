package net.ooder.skill.scene.dto.workbench;

import java.util.List;

public class ProcessTodoRequest {
    
    private String activityInstId;
    private String action;
    private String nextActivityDefId;
    private List<String> nextActivityDefIds;
    private List<String> performerIds;
    private String historyId;

    public String getActivityInstId() { return activityInstId; }
    public void setActivityInstId(String activityInstId) { this.activityInstId = activityInstId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getNextActivityDefId() { return nextActivityDefId; }
    public void setNextActivityDefId(String nextActivityDefId) { this.nextActivityDefId = nextActivityDefId; }
    public List<String> getNextActivityDefIds() { return nextActivityDefIds; }
    public void setNextActivityDefIds(List<String> nextActivityDefIds) { this.nextActivityDefIds = nextActivityDefIds; }
    public List<String> getPerformerIds() { return performerIds; }
    public void setPerformerIds(List<String> performerIds) { this.performerIds = performerIds; }
    public String getHistoryId() { return historyId; }
    public void setHistoryId(String historyId) { this.historyId = historyId; }
}
