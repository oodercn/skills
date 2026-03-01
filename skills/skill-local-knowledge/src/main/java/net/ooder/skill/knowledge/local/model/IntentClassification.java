package net.ooder.skill.knowledge.local.model;

import java.util.Map;

public class IntentClassification {
    
    private IntentType type;
    private double confidence;
    private String target;
    private String matchedKeyword;
    private Map<String, Object> params;
    private SuggestedAction suggestedAction;

    public IntentType getType() { return type; }
    public void setType(IntentType type) { this.type = type; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getMatchedKeyword() { return matchedKeyword; }
    public void setMatchedKeyword(String matchedKeyword) { this.matchedKeyword = matchedKeyword; }
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
    public SuggestedAction getSuggestedAction() { return suggestedAction; }
    public void setSuggestedAction(SuggestedAction suggestedAction) { this.suggestedAction = suggestedAction; }
}
