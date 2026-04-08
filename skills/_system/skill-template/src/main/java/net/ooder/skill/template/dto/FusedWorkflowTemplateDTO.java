package net.ooder.skill.template.dto;

import java.util.List;
import java.util.Map;

public class FusedWorkflowTemplateDTO {
    
    private String templateId;
    private String skillId;
    private String procedureId;
    private String status;
    private String priority;
    private Map<String, Object> workflow;
    private List<ConflictDTO> conflicts;
    private long createdAt;
    private long updatedAt;

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getProcedureId() { return procedureId; }
    public void setProcedureId(String procedureId) { this.procedureId = procedureId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Map<String, Object> getWorkflow() { return workflow; }
    public void setWorkflow(Map<String, Object> workflow) { this.workflow = workflow; }
    public List<ConflictDTO> getConflicts() { return conflicts; }
    public void setConflicts(List<ConflictDTO> conflicts) { this.conflicts = conflicts; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public static class ConflictDTO {
        private String field;
        private Object skillValue;
        private Object enterpriseValue;
        private Object resolvedValue;
        private String resolution;

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public Object getSkillValue() { return skillValue; }
        public void setSkillValue(Object skillValue) { this.skillValue = skillValue; }
        public Object getEnterpriseValue() { return enterpriseValue; }
        public void setEnterpriseValue(Object enterpriseValue) { this.enterpriseValue = enterpriseValue; }
        public Object getResolvedValue() { return resolvedValue; }
        public void setResolvedValue(Object resolvedValue) { this.resolvedValue = resolvedValue; }
        public String getResolution() { return resolution; }
        public void setResolution(String resolution) { this.resolution = resolution; }
    }
}
