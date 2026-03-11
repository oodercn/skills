package net.ooder.skill.security.dto.key;

import lombok.Data;
import net.ooder.skill.security.dto.key.KeyType;
import net.ooder.skill.security.dto.key.KeyStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ApiKeyDTO {
    
    private String keyId;
    private String keyName;
    private KeyType keyType;
    private String provider;
    private String status;
    private long createdAt;
    private long expiresAt;
    private long lastUsedAt;
    private int useCount;
    private int maxUseCount;
    private String createdBy;
    private List<String> allowedUsers = new ArrayList<>();
    private List<String> allowedRoles = new ArrayList<>();
    private List<String> allowedScenes = new ArrayList<>();
    private Map<String, Object> config = new HashMap<>();
    
    public ApiKeyDTO() {
        this.status = KeyStatus.ACTIVE.getCode();
        this.createdAt = System.currentTimeMillis();
        this.useCount = 0;
        this.maxUseCount = -1;
    }
    
    public boolean isExpired() {
        return expiresAt > 0 && System.currentTimeMillis() > expiresAt;
    }
    
    public boolean isRevoked() {
        return KeyStatus.REVOKED.getCode().equals(status);
    }
    
    public boolean isActive() {
        return KeyStatus.ACTIVE.getCode().equals(status) && !isExpired();
    }
    
    public boolean canUse() {
        if (!isActive()) return false;
        if (maxUseCount > 0 && useCount >= maxUseCount) return false;
        return true;
    }
}
