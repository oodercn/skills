package net.ooder.skill.document.assistant.dto;

import java.util.List;

public class QueryDocumentResponse {
    private String answer;
    private List<String> sources;
    private Double confidence;

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
}
