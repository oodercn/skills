package net.ooder.skill.llm.context.service.impl;

import net.ooder.skill.llm.context.model.ContextRequest;
import net.ooder.skill.llm.context.service.ContextExtractor;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class SystemContextExtractor implements ContextExtractor {
    
    private static final int PRIORITY = 10;
    private static final double CHARS_PER_TOKEN = 1.5;
    
    @Override
    public String getType() {
        return "system";
    }
    
    @Override
    public int getPriority() {
        return PRIORITY;
    }
    
    @Override
    public String extract(ContextRequest request) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("## 系统信息\n");
        sb.append("- 当前时间: ").append(formatDate(new Date())).append("\n");
        sb.append("- 系统版本: ooder v1.0.0\n");
        
        if (request.getParams() != null && !request.getParams().isEmpty()) {
            sb.append("- 请求参数: ").append(formatParams(request.getParams())).append("\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public int estimateTokens(String content) {
        if (content == null || content.isEmpty()) return 0;
        return (int) Math.ceil(content.length() / CHARS_PER_TOKEN);
    }
    
    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
    
    private String formatParams(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }
}
