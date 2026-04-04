package net.ooder.skill.tenant.model;

public class TenantUpdateRequest {
    private String name;
    private String description;
    private String logoUrl;
    private String domain;
    private Integer maxUsers;
    private Long maxStorageMB;
    private String planType;
    private String status;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public Integer getMaxUsers() { return maxUsers; }
    public void setMaxUsers(Integer maxUsers) { this.maxUsers = maxUsers; }
    public Long getMaxStorageMB() { return maxStorageMB; }
    public void setMaxStorageMB(Long maxStorageMB) { this.maxStorageMB = maxStorageMB; }
    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
