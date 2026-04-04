package net.ooder.skill.selector.dto;

public class CapabilityItemDTO {
    private String capabilityId;
    private String name;
    private String category;
    private String providerType;
    private String description;

    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getProviderType() { return providerType; }
    public void setProviderType(String providerType) { this.providerType = providerType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
