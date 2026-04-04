package net.ooder.skill.tenant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_tenant_member",
       indexes = {
               @Index(name = "idx_member_tenant_user", columnList = "tenantId,userId", unique = true),
               @Index(name = "idx_member_user", columnList = "userId"),
               @Index(name = "idx_member_status", columnList = "status")
       })
public class TenantMember {

    @Id
    @Column(length = 32)
    private String id;

    @Column(name = "tenant_id", nullable = false, length = 32)
    private String tenantId;

    @Column(name = "user_id", nullable = false, length = 32)
    private String userId;

    @Column(length = 20)
    private String role;

    @Column(length = 20)
    private String status;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "invited_by", length = 32)
    private String invitedBy;

    public enum Role {
        OWNER("owner"), ADMIN("admin"), MEMBER("member"), GUEST("guest");

        private final String code;
        Role(String code) { this.code = code; }
        public String getCode() { return code; }
    }

    public TenantMember() {
        this.id = java.util.UUID.randomUUID().toString().replace("-", "");
        this.status = "ACTIVE";
        this.joinedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public String getInvitedBy() { return invitedBy; }
    public void setInvitedBy(String invitedBy) { this.invitedBy = invitedBy; }
}
