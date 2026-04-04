package net.ooder.skill.tenant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_tenant", indexes = {
        @Index(name = "idx_tenant_code", columnList = "code", unique = true),
        @Index(name = "idx_tenant_status", columnList = "status")
})
public class Tenant {

    @Id
    @Column(length = 32)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50, unique = true)
    private String code;

    @Column(length = 500)
    private String description;

    @Column(length = 20)
    private String status;

    @Column(length = 20)
    private String planType;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(length = 32)
    private String createdBy;

    @Column
    private Integer maxUsers;

    @Column
    private Long maxStorageMB;

    @Column(length = 200)
    private String domain;

    @Column(length = 500)
    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String settings;

    public Tenant() {
        this.id = java.util.UUID.randomUUID().toString().replace("-", "");
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Integer getMaxUsers() { return maxUsers; }
    public void setMaxUsers(Integer maxUsers) { this.maxUsers = maxUsers; }

    public Long getMaxStorageMB() { return maxStorageMB; }
    public void setMaxStorageMB(Long maxStorageMB) { this.maxStorageMB = maxStorageMB; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getSettings() { return settings; }
    public void setSettings(String settings) { this.settings = settings; }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public static class Builder {
        private final Tenant tenant = new Tenant();

        public Builder id(String id) { tenant.id = id; return this; }
        public Builder name(String name) { tenant.name = name; return this; }
        public Builder code(String code) { tenant.code = code; return this; }
        public Builder description(String desc) { tenant.description = desc; return this; }
        public Builder status(String status) { tenant.status = status; return this; }
        public Builder planType(String plan) { tenant.planType = plan; return this; }
        public Builder createdBy(String userId) { tenant.createdBy = userId; return this; }
        public Builder maxUsers(int max) { tenant.maxUsers = max; return this; }
        public Builder maxStorageMB(long mb) { tenant.maxStorageMB = mb; return this; }
        public Builder domain(String domain) { tenant.domain = domain; return this; }
        public Builder logoUrl(String url) { tenant.logoUrl = url; return this; }

        public Tenant build() {
            if (tenant.code == null && tenant.name != null) {
                tenant.code = tenant.name.toLowerCase()
                        .replaceAll("[^a-z0-9]", "-")
                        .replaceAll("-+", "-")
                        .replaceAll("^-|-$", "");
            }
            return tenant;
        }
    }
}
