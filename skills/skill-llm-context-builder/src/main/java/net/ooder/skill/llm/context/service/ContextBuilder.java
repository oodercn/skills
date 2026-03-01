package net.ooder.skill.llm.context.service;

import net.ooder.skill.llm.context.model.BuiltContext;
import net.ooder.skill.llm.context.model.ContextRequest;

public interface ContextBuilder {
    
    BuiltContext build(ContextRequest request);
    
    String buildPrompt(ContextRequest request);
    
    int countTokens(String text);
    
    String truncateToTokenLimit(String text, int maxTokens);
}
