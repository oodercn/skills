package net.ooder.skill.audit.dto;

import java.util.Map;

public class AuditStatsDTO {
    
    private long totalEvents;
    private long successEvents;
    private long failedEvents;
    private long todayEvents;
    private long weekEvents;
    private long monthEvents;
    private Map<String, Long> eventsByType;
    private Map<String, Long> eventsByUser;
    private Map<String, Long> eventsByResource;
    private long avgResponseTime;

    public long getTotalEvents() { return totalEvents; }
    public void setTotalEvents(long totalEvents) { this.totalEvents = totalEvents; }
    public long getSuccessEvents() { return successEvents; }
    public void setSuccessEvents(long successEvents) { this.successEvents = successEvents; }
    public long getFailedEvents() { return failedEvents; }
    public void setFailedEvents(long failedEvents) { this.failedEvents = failedEvents; }
    public long getTodayEvents() { return todayEvents; }
    public void setTodayEvents(long todayEvents) { this.todayEvents = todayEvents; }
    public long getWeekEvents() { return weekEvents; }
    public void setWeekEvents(long weekEvents) { this.weekEvents = weekEvents; }
    public long getMonthEvents() { return monthEvents; }
    public void setMonthEvents(long monthEvents) { this.monthEvents = monthEvents; }
    public Map<String, Long> getEventsByType() { return eventsByType; }
    public void setEventsByType(Map<String, Long> eventsByType) { this.eventsByType = eventsByType; }
    public Map<String, Long> getEventsByUser() { return eventsByUser; }
    public void setEventsByUser(Map<String, Long> eventsByUser) { this.eventsByUser = eventsByUser; }
    public Map<String, Long> getEventsByResource() { return eventsByResource; }
    public void setEventsByResource(Map<String, Long> eventsByResource) { this.eventsByResource = eventsByResource; }
    public long getAvgResponseTime() { return avgResponseTime; }
    public void setAvgResponseTime(long avgResponseTime) { this.avgResponseTime = avgResponseTime; }
}
