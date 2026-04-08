package net.ooder.bpm.designer.model.dto;

import java.util.List;
import java.util.Map;

public class CapabilityMatchingResultDTO {
    
    private MatchingStatus status;
    private List<CapabilityMatch> matches;
    private String reasoning;
    private Map<String, Object> recommendedBinding;
    private List<FunctionCallTraceDTO> functionTraces;
    
    public enum MatchingStatus {
        EXACT_MATCH,
        PARTIAL_MATCH,
        NO_MATCH,
        NEED_CLARIFICATION
    }
    
    public static class CapabilityMatch {
        private String capId;
        private String capName;
        private String description;
        private String category;
        private double matchScore;
        private MatchDimensionScores dimensionScores;
        private String matchReason;
        private Map<String, Object> bindingConfig;
        
        public String getCapId() { return capId; }
        public void setCapId(String capId) { this.capId = capId; }
        public String getCapName() { return capName; }
        public void setCapName(String capName) { this.capName = capName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public double getMatchScore() { return matchScore; }
        public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
        public MatchDimensionScores getDimensionScores() { return dimensionScores; }
        public void setDimensionScores(MatchDimensionScores dimensionScores) { this.dimensionScores = dimensionScores; }
        public String getMatchReason() { return matchReason; }
        public void setMatchReason(String matchReason) { this.matchReason = matchReason; }
        public Map<String, Object> getBindingConfig() { return bindingConfig; }
        public void setBindingConfig(Map<String, Object> bindingConfig) { this.bindingConfig = bindingConfig; }
    }
    
    public static class MatchDimensionScores {
        private double semanticSimilarity;
        private double functionalMatch;
        private double parameterCompatibility;
        
        public double getSemanticSimilarity() { return semanticSimilarity; }
        public void setSemanticSimilarity(double semanticSimilarity) { this.semanticSimilarity = semanticSimilarity; }
        public double getFunctionalMatch() { return functionalMatch; }
        public void setFunctionalMatch(double functionalMatch) { this.functionalMatch = functionalMatch; }
        public double getParameterCompatibility() { return parameterCompatibility; }
        public void setParameterCompatibility(double parameterCompatibility) { this.parameterCompatibility = parameterCompatibility; }
    }
    
    public static CapabilityMatchingResultDTO exactMatch(List<CapabilityMatch> matches, String reasoning) {
        CapabilityMatchingResultDTO result = new CapabilityMatchingResultDTO();
        result.setStatus(MatchingStatus.EXACT_MATCH);
        result.setMatches(matches);
        result.setReasoning(reasoning);
        if (!matches.isEmpty()) {
            result.setRecommendedBinding(matches.get(0).getBindingConfig());
        }
        return result;
    }
    
    public static CapabilityMatchingResultDTO partialMatch(List<CapabilityMatch> matches, String reasoning) {
        CapabilityMatchingResultDTO result = new CapabilityMatchingResultDTO();
        result.setStatus(MatchingStatus.PARTIAL_MATCH);
        result.setMatches(matches);
        result.setReasoning(reasoning);
        return result;
    }
    
    public static CapabilityMatchingResultDTO noMatch(String reasoning) {
        CapabilityMatchingResultDTO result = new CapabilityMatchingResultDTO();
        result.setStatus(MatchingStatus.NO_MATCH);
        result.setReasoning(reasoning);
        return result;
    }
    
    public MatchingStatus getStatus() { return status; }
    public void setStatus(MatchingStatus status) { this.status = status; }
    public List<CapabilityMatch> getMatches() { return matches; }
    public void setMatches(List<CapabilityMatch> matches) { this.matches = matches; }
    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    public Map<String, Object> getRecommendedBinding() { return recommendedBinding; }
    public void setRecommendedBinding(Map<String, Object> recommendedBinding) { this.recommendedBinding = recommendedBinding; }
    public List<FunctionCallTraceDTO> getFunctionTraces() { return functionTraces; }
    public void setFunctionTraces(List<FunctionCallTraceDTO> functionTraces) { this.functionTraces = functionTraces; }
}
