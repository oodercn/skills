package net.ooder.skill.template.dto;

import java.util.Map;

public class ConflictResolutionRequestDTO {
    
    private String templateId;
    private String field;
    private Object resolvedValue;
    private String resolution;
    private Map<String, Object> resolutions;

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    public Object getResolvedValue() { return resolvedValue; }
    public void setResolvedValue(Object resolvedValue) { this.resolvedValue = resolvedValue; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public Map<String, Object> getResolutions() { return resolutions; }
    public void setResolutions(Map<String, Object> resolutions) { this.resolutions = resolutions; }
}
