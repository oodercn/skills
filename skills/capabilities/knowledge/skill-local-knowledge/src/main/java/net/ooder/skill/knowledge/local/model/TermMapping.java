package net.ooder.skill.knowledge.local.model;

public class TermMapping {
    
    private String term;
    private String systemConcept;
    private String type;
    private String description;
    private double confidence;
    private String domain;

    public TermMapping() {}
    
    public TermMapping(String term, String systemConcept, String type) {
        this.term = term;
        this.systemConcept = systemConcept;
        this.type = type;
        this.confidence = 1.0;
    }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public String getSystemConcept() { return systemConcept; }
    public void setSystemConcept(String systemConcept) { this.systemConcept = systemConcept; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
}
