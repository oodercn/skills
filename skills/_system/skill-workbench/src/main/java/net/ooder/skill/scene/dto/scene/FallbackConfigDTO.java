package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class FallbackConfigDTO {
    private String fallbackId;
    private String type;
    private String target;
    private String condition;
    private int priority;
    private int timeout;
    private Map<String, Object> parameters;

    public String getFallbackId() { return fallbackId; }
    public void setFallbackId(String fallbackId) { this.fallbackId = fallbackId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}
