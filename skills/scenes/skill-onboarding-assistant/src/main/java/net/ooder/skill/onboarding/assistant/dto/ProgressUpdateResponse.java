package net.ooder.skill.onboarding.assistant.dto;

public class ProgressUpdateResponse {
    private String status;
    private Integer progress;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
