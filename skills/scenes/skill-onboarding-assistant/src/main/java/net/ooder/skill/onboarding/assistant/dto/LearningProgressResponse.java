package net.ooder.skill.onboarding.assistant.dto;

public class LearningProgressResponse {
    private String employeeId;
    private Integer completedStages;
    private Integer totalStages;
    private Integer progress;
    private Long startedAt;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getCompletedStages() {
        return completedStages;
    }

    public void setCompletedStages(Integer completedStages) {
        this.completedStages = completedStages;
    }

    public Integer getTotalStages() {
        return totalStages;
    }

    public void setTotalStages(Integer totalStages) {
        this.totalStages = totalStages;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }
}
