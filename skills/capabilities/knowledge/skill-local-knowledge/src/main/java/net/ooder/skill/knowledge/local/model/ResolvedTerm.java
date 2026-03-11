package net.ooder.skill.knowledge.local.model;

public class ResolvedTerm {
    
    private String term;
    private String mappedTo;
    private String mappedType;
    private double confidence;
    private String description;

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public String getMappedTo() { return mappedTo; }
    public void setMappedTo(String mappedTo) { this.mappedTo = mappedTo; }
    public String getMappedType() { return mappedType; }
    public void setMappedType(String mappedType) { this.mappedType = mappedType; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
