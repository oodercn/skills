package net.ooder.skill.scenes.dto;

public class CapabilityBindingDTO {
    private String bindingId;
    private String sceneGroupId;
    private String capId;
    private String capName;
    private CapabilityProviderType providerType;
    private String providerId;
    private Integer priority;
    private CapabilityBindingStatus status;
    private long bindTime;

    public String getBindingId() { return bindingId; }
    public void setBindingId(String bindingId) { this.bindingId = bindingId; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getCapId() { return capId; }
    public void setCapId(String capId) { this.capId = capId; }
    public String getCapName() { return capName; }
    public void setCapName(String capName) { this.capName = capName; }
    public CapabilityProviderType getProviderType() { return providerType; }
    public void setProviderType(CapabilityProviderType providerType) { this.providerType = providerType; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public CapabilityBindingStatus getStatus() { return status; }
    public void setStatus(CapabilityBindingStatus status) { this.status = status; }
    public long getBindTime() { return bindTime; }
    public void setBindTime(long bindTime) { this.bindTime = bindTime; }
}
