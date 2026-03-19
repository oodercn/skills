package net.ooder.skill.onboarding.assistant.dto;

import java.util.List;

public class TrainingAnswerResponse {
    private String answer;
    private List<String> sources;
    private Double confidence;
    private boolean needHumanSupport;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public boolean isNeedHumanSupport() {
        return needHumanSupport;
    }

    public void setNeedHumanSupport(boolean needHumanSupport) {
        this.needHumanSupport = needHumanSupport;
    }
}
