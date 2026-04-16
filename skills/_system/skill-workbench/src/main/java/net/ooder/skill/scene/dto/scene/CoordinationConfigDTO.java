package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class CoordinationConfigDTO {
    private String coordinationId;
    private String type;
    private String mode;
    private int timeout;
    private int retryCount;
    private String failureStrategy;
    private Map<String, Object> parameters;

    public String getCoordinationId() { return coordinationId; }
    public void setCoordinationId(String coordinationId) { this.coordinationId = coordinationId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    public String getFailureStrategy() { return failureStrategy; }
    public void setFailureStrategy(String failureStrategy) { this.failureStrategy = failureStrategy; }
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}
