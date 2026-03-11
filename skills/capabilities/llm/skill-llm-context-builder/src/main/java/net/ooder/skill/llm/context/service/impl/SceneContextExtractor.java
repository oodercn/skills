package net.ooder.skill.llm.context.service.impl;

import net.ooder.skill.llm.context.model.ContextRequest;
import net.ooder.skill.llm.context.service.ContextExtractor;

import org.springframework.stereotype.Component;

@Component
public class SceneContextExtractor implements ContextExtractor {
    
    private static final int PRIORITY = 30;
    private static final double CHARS_PER_TOKEN = 1.5;
    
    @Override
    public String getType() {
        return "scene";
    }
    
    @Override
    public int getPriority() {
        return PRIORITY;
    }
    
    @Override
    public String extract(ContextRequest request) {
        if (request.getSceneId() == null || request.getSceneId().isEmpty()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("## 场景信息\n");
        sb.append("- 场景ID: ").append(request.getSceneId()).append("\n");
        
        if (request.getPageType() != null) {
            sb.append("- 页面类型: ").append(request.getPageType()).append("\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public int estimateTokens(String content) {
        if (content == null || content.isEmpty()) return 0;
        return (int) Math.ceil(content.length() / CHARS_PER_TOKEN);
    }
}
