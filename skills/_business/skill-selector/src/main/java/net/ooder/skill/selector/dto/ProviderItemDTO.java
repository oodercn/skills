package net.ooder.skill.selector.dto;

import java.util.List;

public class ProviderItemDTO {

    private String providerId;
    private String name;
    private String type;
    private String icon;
    private boolean enabled;
    private List<String> models;
    private String status;

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public List<String> getModels() { return models; }
    public void setModels(List<String> models) { this.models = models; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}