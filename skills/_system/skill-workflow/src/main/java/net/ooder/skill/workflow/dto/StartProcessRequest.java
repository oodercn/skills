package net.ooder.skill.workflow.dto;

import java.util.Map;

public class StartProcessRequest {
    
    private String processDefVersionId;
    private String name;
    private String urgency;
    private Map<String, Object> formValues;

    public String getProcessDefVersionId() { return processDefVersionId; }
    public void setProcessDefVersionId(String processDefVersionId) { this.processDefVersionId = processDefVersionId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    public Map<String, Object> getFormValues() { return formValues; }
    public void setFormValues(Map<String, Object> formValues) { this.formValues = formValues; }
}
