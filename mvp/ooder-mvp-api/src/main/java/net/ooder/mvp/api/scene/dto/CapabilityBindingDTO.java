package net.ooder.mvp.api.scene.dto;

import java.io.Serializable;
import java.util.Map;

public class CapabilityBindingDTO implements Serializable {
    private String bindingId;
    private String sceneGroupId;
    private String capId;
    private String capName;
    private String providerType;
    private String connectorType;
    private int priority;
    private boolean fallback;
    private String status;
    private Map<String, Object> connectorConfig;
    
    public String getBindingId() { return bindingId; }
    public void setBindingId(String bindingId) { this.bindingId = bindingId; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getCapId() { return capId; }
    public void setCapId(String capId) { this.capId = capId; }
    public String getCapName() { return capName; }
    public void setCapName(String capName) { this.capName = capName; }
    public String getProviderType() { return providerType; }
    public void setProviderType(String providerType) { this.providerType = providerType; }
    public String getConnectorType() { return connectorType; }
    public void setConnectorType(String connectorType) { this.connectorType = connectorType; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public boolean isFallback() { return fallback; }
    public void setFallback(boolean fallback) { this.fallback = fallback; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Map<String, Object> getConnectorConfig() { return connectorConfig; }
    public void setConnectorConfig(Map<String, Object> connectorConfig) { this.connectorConfig = connectorConfig; }
}
