package net.ooder.bpm.designer.model.dto;

import java.util.List;
import java.util.Map;

public class PerformerDerivationResultDTO {
    
    private DerivationStatus status;
    private List<PerformerCandidate> candidates;
    private String reasoning;
    private List<FunctionCallTraceDTO> functionTraces;
    private Map<String, Object> derivedConfig;
    private double confidence;
    
    public enum DerivationStatus {
        SUCCESS,
        PARTIAL,
        NEED_CLARIFICATION,
        FAILED
    }
    
    public static class PerformerCandidate {
        private String userId;
        private String userName;
        private String deptId;
        private String deptName;
        private String roleId;
        private String roleName;
        private double matchScore;
        private String matchReason;
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getDeptId() { return deptId; }
        public void setDeptId(String deptId) { this.deptId = deptId; }
        public String getDeptName() { return deptName; }
        public void setDeptName(String deptName) { this.deptName = deptName; }
        public String getRoleId() { return roleId; }
        public void setRoleId(String roleId) { this.roleId = roleId; }
        public String getRoleName() { return roleName; }
        public void setRoleName(String roleName) { this.roleName = roleName; }
        public double getMatchScore() { return matchScore; }
        public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
        public String getMatchReason() { return matchReason; }
        public void setMatchReason(String matchReason) { this.matchReason = matchReason; }
    }
    
    public static PerformerDerivationResultDTO success(List<PerformerCandidate> candidates, 
            Map<String, Object> derivedConfig, String reasoning) {
        PerformerDerivationResultDTO result = new PerformerDerivationResultDTO();
        result.setStatus(DerivationStatus.SUCCESS);
        result.setCandidates(candidates);
        result.setDerivedConfig(derivedConfig);
        result.setReasoning(reasoning);
        result.setConfidence(0.9);
        return result;
    }
    
    public static PerformerDerivationResultDTO partial(List<PerformerCandidate> candidates, 
            String reasoning) {
        PerformerDerivationResultDTO result = new PerformerDerivationResultDTO();
        result.setStatus(DerivationStatus.PARTIAL);
        result.setCandidates(candidates);
        result.setReasoning(reasoning);
        result.setConfidence(0.6);
        return result;
    }
    
    public static PerformerDerivationResultDTO needClarification(String reasoning) {
        PerformerDerivationResultDTO result = new PerformerDerivationResultDTO();
        result.setStatus(DerivationStatus.NEED_CLARIFICATION);
        result.setReasoning(reasoning);
        result.setConfidence(0.3);
        return result;
    }
    
    public static PerformerDerivationResultDTO failed(String error) {
        PerformerDerivationResultDTO result = new PerformerDerivationResultDTO();
        result.setStatus(DerivationStatus.FAILED);
        result.setReasoning(error);
        result.setConfidence(0.0);
        return result;
    }
    
    public DerivationStatus getStatus() { return status; }
    public void setStatus(DerivationStatus status) { this.status = status; }
    public List<PerformerCandidate> getCandidates() { return candidates; }
    public void setCandidates(List<PerformerCandidate> candidates) { this.candidates = candidates; }
    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    public List<FunctionCallTraceDTO> getFunctionTraces() { return functionTraces; }
    public void setFunctionTraces(List<FunctionCallTraceDTO> functionTraces) { this.functionTraces = functionTraces; }
    public Map<String, Object> getDerivedConfig() { return derivedConfig; }
    public void setDerivedConfig(Map<String, Object> derivedConfig) { this.derivedConfig = derivedConfig; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
}
