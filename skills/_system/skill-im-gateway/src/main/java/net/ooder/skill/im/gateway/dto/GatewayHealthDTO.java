package net.ooder.skill.im.gateway.dto;

import java.util.List;

public class GatewayHealthDTO {
    
    private String status;
    private List<String> channels;
    private String currentTenant;
    private long timestamp;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getChannels() { return channels; }
    public void setChannels(List<String> channels) { this.channels = channels; }
    public String getCurrentTenant() { return currentTenant; }
    public void setCurrentTenant(String currentTenant) { this.currentTenant = currentTenant; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
