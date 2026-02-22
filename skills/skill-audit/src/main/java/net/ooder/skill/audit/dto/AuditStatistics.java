package net.ooder.skill.audit.dto;

import java.util.Map;

public class AuditStatistics {
    private long totalLogs;
    private long todayLogs;
    private long successCount;
    private long failureCount;
    private Map<String, Long> actionCounts;
    private Map<String, Long> resourceTypeCounts;
    private Map<String, Long> userCounts;
    private long startTime;
    private long endTime;

    public long getTotalLogs() {
        return totalLogs;
    }

    public void setTotalLogs(long totalLogs) {
        this.totalLogs = totalLogs;
    }

    public long getTodayLogs() {
        return todayLogs;
    }

    public void setTodayLogs(long todayLogs) {
        this.todayLogs = todayLogs;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    public long getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(long failureCount) {
        this.failureCount = failureCount;
    }

    public Map<String, Long> getActionCounts() {
        return actionCounts;
    }

    public void setActionCounts(Map<String, Long> actionCounts) {
        this.actionCounts = actionCounts;
    }

    public Map<String, Long> getResourceTypeCounts() {
        return resourceTypeCounts;
    }

    public void setResourceTypeCounts(Map<String, Long> resourceTypeCounts) {
        this.resourceTypeCounts = resourceTypeCounts;
    }

    public Map<String, Long> getUserCounts() {
        return userCounts;
    }

    public void setUserCounts(Map<String, Long> userCounts) {
        this.userCounts = userCounts;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
