package net.ooder.skill.procedure.dto;

import java.util.List;

public class LlmPreviewResultDTO {
    private String preview;
    private int suggestedSteps;
    private List<String> suggestions;

    public String getPreview() { return preview; }
    public void setPreview(String preview) { this.preview = preview; }
    
    public int getSuggestedSteps() { return suggestedSteps; }
    public void setSuggestedSteps(int suggestedSteps) { this.suggestedSteps = suggestedSteps; }
    
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
}
