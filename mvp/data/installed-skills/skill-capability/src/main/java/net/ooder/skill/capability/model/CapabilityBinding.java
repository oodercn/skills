package net.ooder.skill.capability.model;

import java.util.*;

public class CapabilityBinding {
    private String bindingId;
    private String capabilityId;
    private String agentId;
    private String linkId;
    private CapabilityBindingStatus status;
    private Map<String, Object> config;
    private Date createdAt;
    private Date updatedAt;

    public CapabilityBinding() {
        this.status = CapabilityBindingStatus.ACTIVE;
        this.config = new HashMap<>();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public String getBindingId() { return bindingId; }
    public void setBindingId(String bindingId) { this.bindingId = bindingId; }

    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getLinkId() { return linkId; }
    public void setLinkId(String linkId) { this.linkId = linkId; }

    public CapabilityBindingStatus getStatus() { return status; }
    public void setStatus(CapabilityBindingStatus status) { this.status = status; }

    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
