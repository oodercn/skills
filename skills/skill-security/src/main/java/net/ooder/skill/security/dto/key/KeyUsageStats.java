package net.ooder.skill.security.dto.key;

import lombok.Data;

@Data
public class KeyUsageStats {
    
    private String keyId;
    private int totalUseCount;
    private int todayUseCount;
    private int weekUseCount;
    private int monthUseCount;
    private int failedCount;
    private int deniedCount;
    private long lastUsedAt;
    private String lastUsedBy;
    private String lastUsedScene;
}
