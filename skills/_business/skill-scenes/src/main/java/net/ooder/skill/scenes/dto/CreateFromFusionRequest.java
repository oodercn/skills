package net.ooder.skill.scenes.dto;

import java.util.Map;

public class CreateFromFusionRequest {
    
    private String fusionId;
    private Map<String, Object> options;

    public String getFusionId() { return fusionId; }
    public void setFusionId(String fusionId) { this.fusionId = fusionId; }
    public Map<String, Object> getOptions() { return options; }
    public void setOptions(Map<String, Object> options) { this.options = options; }
}
