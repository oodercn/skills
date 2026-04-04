package net.ooder.skill.scenes.dto;

public class SceneCapabilityDTO {
    private String capabilityId;
    private String name;
    private String category;
    private String providerType;
    private String status;

    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getProviderType() { return providerType; }
    public void setProviderType(String providerType) { this.providerType = providerType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
