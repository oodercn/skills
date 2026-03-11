package net.ooder.skill.project.knowledge.dto;

import java.util.List;

public class ClassifyDocumentResponse {
    private String docType;
    private List<String> tags;
    private Double confidence;

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
