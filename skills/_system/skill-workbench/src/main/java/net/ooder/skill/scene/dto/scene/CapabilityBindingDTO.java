package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class CapabilityBindingDTO {
    
    private String bindingId;
    private String capabilityId;
    private String capabilityName;
    private String bindingType;
    private Map<String, Object> config;
    private String status;
    private long bindTime;

    public String getBindingId() { return bindingId; }
    public void setBindingId(String bindingId) { this.bindingId = bindingId; }
    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    public String getCapabilityName() { return capabilityName; }
    public void setCapabilityName(String capabilityName) { this.capabilityName = capabilityName; }
    public String getBindingType() { return bindingType; }
    public void setBindingType(String bindingType) { this.bindingType = bindingType; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getBindTime() { return bindTime; }
    public void setBindTime(long bindTime) { this.bindTime = bindTime; }
}
