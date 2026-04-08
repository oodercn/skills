package net.ooder.skill.security.dto;

public class SecurityStatsDTO {
    
    private int totalPolicies;
    private long activePolicies;
    private boolean enabled;
    private long timestamp;

    public int getTotalPolicies() { return totalPolicies; }
    public void setTotalPolicies(int totalPolicies) { this.totalPolicies = totalPolicies; }
    public long getActivePolicies() { return activePolicies; }
    public void setActivePolicies(long activePolicies) { this.activePolicies = activePolicies; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
