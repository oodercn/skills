package net.ooder.skill.tenant.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TenantCreateRequest {

    @NotBlank(message = "租户名称不能为空")
    @Size(max = 100, message = "租户名称最长100字符")
    private String name;

    @Size(max = 50, message = "租户代码最长50字符")
    private String code;

    @Size(max = 500, message = "描述最长500字符")
    private String description;

    private String planType;

    private Integer maxUsers;

    private Long maxStorageMB;

    @Size(max = 200, message = "域名最长200字符")
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
