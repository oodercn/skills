package net.ooder.skill.rag.strategy;

import net.ooder.skill.rag.model.RagContext;
import net.ooder.skill.rag.model.RetrievedDocument;

import java.util.List;

public interface RetrievalStrategy {
    
    String getName();
    
    List<RetrievedDocument> retrieve(RagContext context);
    
    default boolean supports(String strategyName) {
        return getName().equalsIgnoreCase(strategyName);
    }
}
