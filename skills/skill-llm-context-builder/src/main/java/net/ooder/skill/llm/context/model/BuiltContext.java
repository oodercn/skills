package net.ooder.skill.llm.context.model;

import java.util.List;
import java.util.Map;

public class BuiltContext {
    
    private String id;
    private List<ContextSection> sections;
    private int totalTokens;
    private int maxTokens;
    private boolean truncated;
    private Map<String, Object> metadata;

    public static class ContextSection {
        private String sourceId;
        private String type;
        private String content;
        private int tokens;
        private int priority;

        public String getSourceId() { return sourceId; }
        public void setSourceId(String sourceId) { this.sourceId = sourceId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public int getTokens() { return tokens; }
        public void setTokens(int tokens) { this.tokens = tokens; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<ContextSection> getSections() { return sections; }
    public void setSections(List<ContextSection> sections) { this.sections = sections; }
    public int getTotalTokens() { return totalTokens; }
    public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    public boolean isTruncated() { return truncated; }
    public void setTruncated(boolean truncated) { this.truncated = truncated; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public String toPrompt() {
        StringBuilder sb = new StringBuilder();
        if (sections != null) {
            for (ContextSection section : sections) {
                if (section.getContent() != null && !section.getContent().isEmpty()) {
                    sb.append(section.getContent()).append("\n\n");
                }
            }
        }
        return sb.toString().trim();
    }
}
