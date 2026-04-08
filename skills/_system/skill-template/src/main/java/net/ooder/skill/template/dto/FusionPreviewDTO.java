package net.ooder.skill.template.dto;

import java.util.List;
import java.util.Map;

public class FusionPreviewDTO {
    
    private String skillId;
    private String procedureId;
    private Map<String, Object> mergedWorkflow;
    private List<FusedWorkflowTemplateDTO.ConflictDTO> conflicts;
    private int matchScore;
    private String matchLevel;

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getProcedureId() { return procedureId; }
    public void setProcedureId(String procedureId) { this.procedureId = procedureId; }
    public Map<String, Object> getMergedWorkflow() { return mergedWorkflow; }
    public void setMergedWorkflow(Map<String, Object> mergedWorkflow) { this.mergedWorkflow = mergedWorkflow; }
    public List<FusedWorkflowTemplateDTO.ConflictDTO> getConflicts() { return conflicts; }
    public void setConflicts(List<FusedWorkflowTemplateDTO.ConflictDTO> conflicts) { this.conflicts = conflicts; }
    public int getMatchScore() { return matchScore; }
    public void setMatchScore(int matchScore) { this.matchScore = matchScore; }
    public String getMatchLevel() { return matchLevel; }
    public void setMatchLevel(String matchLevel) { this.matchLevel = matchLevel; }
}
