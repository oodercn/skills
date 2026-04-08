package net.ooder.skill.history.dto;

public class HistoryStatisticsDTO {
    private int totalExecutions;
    private int successCount;
    private int failureCount;
    private double successRate;
    private double avgDuration;
    private int todayExecutions;
    private int weeklyExecutions;
    private int monthlyExecutions;

    public int getTotalExecutions() { return totalExecutions; }
    public void setTotalExecutions(int totalExecutions) { this.totalExecutions = totalExecutions; }
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    public int getFailureCount() { return failureCount; }
    public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    public double getAvgDuration() { return avgDuration; }
    public void setAvgDuration(double avgDuration) { this.avgDuration = avgDuration; }
    public int getTodayExecutions() { return todayExecutions; }
    public void setTodayExecutions(int todayExecutions) { this.todayExecutions = todayExecutions; }
    public int getWeeklyExecutions() { return weeklyExecutions; }
    public void setWeeklyExecutions(int weeklyExecutions) { this.weeklyExecutions = weeklyExecutions; }
    public int getMonthlyExecutions() { return monthlyExecutions; }
    public void setMonthlyExecutions(int monthlyExecutions) { this.monthlyExecutions = monthlyExecutions; }
}
