package net.ooder.skill.knowledge.local.service;

import net.ooder.skill.knowledge.local.model.TermMapping;
import net.ooder.skill.knowledge.local.model.TermMappingDTO;
import net.ooder.skill.knowledge.local.model.TermResolution;

import java.util.List;
import java.util.Map;

public interface TermMappingService {
    
    TermResolution resolveTerm(String text, Map<String, Object> context);
    
    void registerTermMapping(String term, String systemConcept, TermMappingDTO options);
    
    List<TermMapping> getTermMappings(String domain);
    
    TermMapping getMappingByTerm(String term);
    
    void loadBuiltinTerms();
    
    void loadUserTerms();
}
