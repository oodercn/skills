package net.ooder.skill.onboarding.assistant.dto;

import java.util.List;

public class LearningReportResponse {
    private String employeeId;
    private Integer progress;
    private Integer completedTasks;
    private Integer remainingTasks;
    private Integer assessmentScore;
    private List<LearningStageResponse> stages;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Integer getRemainingTasks() {
        return remainingTasks;
    }

    public void setRemainingTasks(Integer remainingTasks) {
        this.remainingTasks = remainingTasks;
    }

    public Integer getAssessmentScore() {
        return assessmentScore;
    }

    public void setAssessmentScore(Integer assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    public List<LearningStageResponse> getStages() {
        return stages;
    }

    public void setStages(List<LearningStageResponse> stages) {
        this.stages = stages;
    }
}
