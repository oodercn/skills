package net.ooder.skill.access.dto;

import java.util.List;

public class PermissionCheckResult {
    private boolean allowed;
    private String userId;
    private String permissionCode;
    private List<String> matchedRoles;
    private String denialReason;

    public PermissionCheckResult() {
        this.allowed = false;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public List<String> getMatchedRoles() {
        return matchedRoles;
    }

    public void setMatchedRoles(List<String> matchedRoles) {
        this.matchedRoles = matchedRoles;
    }

    public String getDenialReason() {
        return denialReason;
    }

    public void setDenialReason(String denialReason) {
        this.denialReason = denialReason;
    }
}
