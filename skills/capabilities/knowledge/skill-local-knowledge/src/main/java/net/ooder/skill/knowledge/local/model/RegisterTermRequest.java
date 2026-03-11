package net.ooder.skill.knowledge.local.model;

public class RegisterTermRequest {
    private String term;
    private String systemConcept;
    private String type;
    
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public String getSystemConcept() { return systemConcept; }
    public void setSystemConcept(String systemConcept) { this.systemConcept = systemConcept; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
