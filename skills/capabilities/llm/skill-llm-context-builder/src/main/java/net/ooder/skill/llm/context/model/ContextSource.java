package net.ooder.skill.llm.context.model;

import java.util.List;
import java.util.Map;

public class ContextSource {
    
    private String id;
    private String type;
    private int priority;
    private int maxTokens;
    private boolean enabled;
    private Map<String, Object> config;

    public enum SourceType {
        SYSTEM("system", "系统上下文"),
        USER("user", "用户上下文"),
        SCENE("scene", "场景上下文"),
        PAGE("page", "页面上下文"),
        SKILL("skill", "技能上下文"),
        KNOWLEDGE("knowledge", "知识库上下文"),
        HISTORY("history", "历史对话上下文");

        private final String code;
        private final String description;

        SourceType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
}
