package net.ooder.skill.capability;

import java.util.HashMap;
import java.util.Map;

public class CapabilityMetadata {
    
    private String capabilityId;
    private boolean shareable = false;
    private String sharePolicy = "private";
    private String[] allowedUsers;
    private double rating = 0.0;
    private int ratingCount = 0;
    private int usageCount = 0;
    private long lastUsedAt = 0;
    private long totalExecutionTime = 0;
    private String source = "local";
    private String providerId;
    private String providerName;
    private String status = "ACTIVE";
    private String statusMessage;
    private Map<String, Object> extensions = new HashMap<>();
    
    public CapabilityMetadata() {}
    
    public CapabilityMetadata(String capabilityId) {
        this.capabilityId = capabilityId;
    }
    
    public void incrementUsageCount() {
        this.usageCount++;
    }
    
    public void updateLastUsed() {
        this.lastUsedAt = System.currentTimeMillis();
    }
    
    public void addExecutionTime(long executionTime) {
        this.totalExecutionTime += executionTime;
    }
    
    public long getAverageExecutionTime() {
        if (usageCount == 0) return 0;
        return totalExecutionTime / usageCount;
    }
    
    public void addRating(double newRating) {
        double total = this.rating * this.ratingCount + newRating;
        this.ratingCount++;
        this.rating = total / this.ratingCount;
    }

    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    public boolean isShareable() { return shareable; }
    public void setShareable(boolean shareable) { this.shareable = shareable; }
    public String getSharePolicy() { return sharePolicy; }
    public void setSharePolicy(String sharePolicy) { this.sharePolicy = sharePolicy; }
    public String[] getAllowedUsers() { return allowedUsers; }
    public void setAllowedUsers(String[] allowedUsers) { this.allowedUsers = allowedUsers; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }
    public long getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(long lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    public long getTotalExecutionTime() { return totalExecutionTime; }
    public void setTotalExecutionTime(long totalExecutionTime) { this.totalExecutionTime = totalExecutionTime; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
    public Map<String, Object> getExtensions() { return extensions; }
    public void setExtensions(Map<String, Object> extensions) { this.extensions = extensions; }
}
