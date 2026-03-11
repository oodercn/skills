package net.ooder.skill.llm.context.model;

public class TextRequest {
    private String text;
    private Integer maxTokens;
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
}
