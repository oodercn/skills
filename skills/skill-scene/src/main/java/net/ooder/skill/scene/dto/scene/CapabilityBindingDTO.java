package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class CapabilityBindingDTO {
    private String bindingId;
    private String sceneGroupId;
    private String capId;
    private CapabilityProviderType providerType;
    private String providerId;
    private ConnectorType connectorType;
    private Map<String, Object> connectorConfig;
    private int priority;
    private boolean fallback;
    private CapabilityBindingStatus status;

    public String getBindingId() { return bindingId; }
    public void setBindingId(String bindingId) { this.bindingId = bindingId; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getCapId() { return capId; }
    public void setCapId(String capId) { this.capId = capId; }
    public CapabilityProviderType getProviderType() { return providerType; }
    public void setProviderType(CapabilityProviderType providerType) { this.providerType = providerType; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public ConnectorType getConnectorType() { return connectorType; }
    public void setConnectorType(ConnectorType connectorType) { this.connectorType = connectorType; }
    public Map<String, Object> getConnectorConfig() { return connectorConfig; }
    public void setConnectorConfig(Map<String, Object> connectorConfig) { this.connectorConfig = connectorConfig; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public boolean isFallback() { return fallback; }
    public void setFallback(boolean fallback) { this.fallback = fallback; }
    public CapabilityBindingStatus getStatus() { return status; }
    public void setStatus(CapabilityBindingStatus status) { this.status = status; }
}
