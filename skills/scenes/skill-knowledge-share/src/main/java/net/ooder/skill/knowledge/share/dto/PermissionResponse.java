package net.ooder.skill.knowledge.share.dto;

public class PermissionResponse {
    private String permissionId;
    private String kbId;
    private String userId;
    private String permissionType;
    private String permissionName;
    private String grantedBy;
    private Long grantedAt;

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getKbId() {
        return kbId;
    }

    public void setKbId(String kbId) {
        this.kbId = kbId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getGrantedBy() {
        return grantedBy;
    }

    public void setGrantedBy(String grantedBy) {
        this.grantedBy = grantedBy;
    }

    public Long getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(Long grantedAt) {
        this.grantedAt = grantedAt;
    }
}
