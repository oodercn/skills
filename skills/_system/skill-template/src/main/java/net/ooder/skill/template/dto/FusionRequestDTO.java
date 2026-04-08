package net.ooder.skill.template.dto;

import java.util.List;
import java.util.Map;

public class FusionRequestDTO {
    
    private String skillId;
    private String procedureId;
    private String priority;
    private Map<String, Object> skillDefinition;
    private Map<String, Object> enterpriseProcedure;
    private List<String> conflictFields;

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getProcedureId() { return procedureId; }
    public void setProcedureId(String procedureId) { this.procedureId = procedureId; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Map<String, Object> getSkillDefinition() { return skillDefinition; }
    public void setSkillDefinition(Map<String, Object> skillDefinition) { this.skillDefinition = skillDefinition; }
    public Map<String, Object> getEnterpriseProcedure() { return enterpriseProcedure; }
    public void setEnterpriseProcedure(Map<String, Object> enterpriseProcedure) { this.enterpriseProcedure = enterpriseProcedure; }
    public List<String> getConflictFields() { return conflictFields; }
    public void setConflictFields(List<String> conflictFields) { this.conflictFields = conflictFields; }
}
