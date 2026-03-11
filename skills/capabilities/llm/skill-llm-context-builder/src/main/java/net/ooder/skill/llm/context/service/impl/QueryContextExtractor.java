package net.ooder.skill.llm.context.service.impl;

import net.ooder.skill.llm.context.model.ContextRequest;
import net.ooder.skill.llm.context.service.ContextExtractor;

import org.springframework.stereotype.Component;

@Component
public class QueryContextExtractor implements ContextExtractor {
    
    private static final int PRIORITY = 100;
    private static final double CHARS_PER_TOKEN = 1.5;
    
    @Override
    public String getType() {
        return "query";
    }
    
    @Override
    public int getPriority() {
        return PRIORITY;
    }
    
    @Override
    public String extract(ContextRequest request) {
        if (request.getQuery() == null || request.getQuery().isEmpty()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 用户问题\n");
        sb.append(request.getQuery()).append("\n");
        
        return sb.toString();
    }
    
    @Override
    public int estimateTokens(String content) {
        if (content == null || content.isEmpty()) return 0;
        return (int) Math.ceil(content.length() / CHARS_PER_TOKEN);
    }
}
