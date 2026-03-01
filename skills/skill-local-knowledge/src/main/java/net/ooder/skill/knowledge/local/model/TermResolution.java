package net.ooder.skill.knowledge.local.model;

import java.util.List;
import java.util.Map;

public class TermResolution {
    
    private String originalText;
    private List<ResolvedTerm> resolvedTerms;
    private IntentInfo intent;
    private Map<String, Object> context;

    public String getOriginalText() { return originalText; }
    public void setOriginalText(String originalText) { this.originalText = originalText; }
    public List<ResolvedTerm> getResolvedTerms() { return resolvedTerms; }
    public void setResolvedTerms(List<ResolvedTerm> resolvedTerms) { this.resolvedTerms = resolvedTerms; }
    public IntentInfo getIntent() { return intent; }
    public void setIntent(IntentInfo intent) { this.intent = intent; }
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
}
