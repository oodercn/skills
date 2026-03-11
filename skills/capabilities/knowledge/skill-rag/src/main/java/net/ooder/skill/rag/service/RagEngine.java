package net.ooder.skill.rag.service;

import net.ooder.skill.rag.model.RagContext;
import net.ooder.skill.rag.model.RagResult;

import java.util.List;

public interface RagEngine {
    
    RagResult retrieve(RagContext context);
    
    String buildPrompt(RagContext context, RagResult result);
    
    String buildPromptWithContext(String query, String context, String systemPrompt);
    
    void registerKnowledgeBase(String kbId, String endpoint);
    
    void unregisterKnowledgeBase(String kbId);
    
    List<String> getAvailableStrategies();
    
    RagResult retrieveWithStrategy(RagContext context, String strategyName);
    
    RagResult hybridRetrieve(RagContext context, List<String> kbIds);
    
    String generate(String query, RagContext context);
}
