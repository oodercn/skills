package net.ooder.skill.onboarding.assistant.dto;

import java.util.List;

public class LearningPathResponse {
    private String pathId;
    private String employeeId;
    private String position;
    private String department;
    private List<LearningStageResponse> stages;
    private Integer totalDuration;

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<LearningStageResponse> getStages() {
        return stages;
    }

    public void setStages(List<LearningStageResponse> stages) {
        this.stages = stages;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }
}
