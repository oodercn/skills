package net.ooder.mvp.api.scene.dto;

import java.io.Serializable;
import java.util.Map;

public class CapabilityBindRequest implements Serializable {
    private String capId;
    private String capName;
    private String providerType;
    private String connectorType;
    private int priority;
    private boolean fallback;
    private Map<String, Object> connectorConfig;
    
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
    public Map<String, Object> getConnectorConfig() { return connectorConfig; }
    public void setConnectorConfig(Map<String, Object> connectorConfig) { this.connectorConfig = connectorConfig; }
}
