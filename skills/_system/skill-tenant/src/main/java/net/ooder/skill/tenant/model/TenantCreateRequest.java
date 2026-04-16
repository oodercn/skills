package net.ooder.skill.tenant.model;

public class TenantCreateRequest {
    private String name;
    private String code;
    private String description;
    private String planType;
    private Integer maxUsers;
    private Long maxStorageMB;
    private String domain;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }
    public Integer getMaxUsers() { return maxUsers; }
    public void setMaxUsers(Integer maxUsers) { this.maxUsers = maxUsers; }
    public Long getMaxStorageMB() { return maxStorageMB; }
    public void setMaxStorageMB(Long maxStorageMB) { this.maxStorageMB = maxStorageMB; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
}
