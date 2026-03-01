package net.ooder.skill.knowledge.local.model;

import java.util.List;
import java.util.Map;

public class TermMappingDTO {
    private String term;
    private String systemConcept;
    private String type;
    
    public TermMappingDTO() {}
    
    public TermMappingDTO(String term, String systemConcept, String type) {
        this.term = term;
        this.systemConcept = systemConcept;
        this.type = type;
    }
    
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public String getSystemConcept() { return systemConcept; }
    public void setSystemConcept(String systemConcept) { this.systemConcept = systemConcept; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
