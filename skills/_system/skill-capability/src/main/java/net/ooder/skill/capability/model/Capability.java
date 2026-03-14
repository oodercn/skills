package net.ooder.skill.capability.model;

import java.util.*;

public class Capability {
    private String capabilityId;
    private String name;
    private String description;
    private String version;
    private CapabilityType type;
    private CapabilityStatus status;
    private String icon;
    private String provider;
    private String endpoint;
    private Map<String, Object> config;
    private Map<String, Object> metadata;
    private List<String> tags;
    private Date createdAt;
    private Date updatedAt;

    public Capability() {
        this.status = CapabilityStatus.REGISTERED;
        this.config = new HashMap<>();
        this.metadata = new HashMap<>();
        this.tags = new ArrayList<>();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public CapabilityType getType() { return type; }
    public void setType(CapabilityType type) { this.type = type; }

    public CapabilityStatus getStatus() { return status; }
    public void setStatus(CapabilityStatus status) { this.status = status; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
