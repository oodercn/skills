package net.ooder.skill.llm.context.service;

import net.ooder.skill.llm.context.model.BuiltContext;
import net.ooder.skill.llm.context.model.ContextRequest;
import net.ooder.skill.llm.context.model.ContextSource;

import java.util.List;

public interface ContextExtractor {
    
    String getType();
    
    int getPriority();
    
    String extract(ContextRequest request);
    
    int estimateTokens(String content);
}
