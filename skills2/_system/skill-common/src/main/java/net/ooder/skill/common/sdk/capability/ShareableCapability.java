package net.ooder.skill.common.sdk.capability;

import lombok.Builder;
import lombok.Data;
import net.ooder.sdk.api.capability.Capability;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 鍙垎浜殑鑳藉姏灏佽
 * 
 * 鎵╁睍 SDK 2.3 鐨?Capability锛屾坊鍔犲垎浜浉鍏冲睘鎬? * 
 * @author Skills Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Data
@Builder
public class ShareableCapability {

    private Capability capability;

    @Builder.Default
    private boolean shareable = false;

    @Builder.Default
    private ShareScope shareScope = ShareScope.PRIVATE;

    private String shareToken;

    private Instant shareExpireAt;

    @Builder.Default
    private int shareLimit = -1;

    @Builder.Default
    private int sharedCount = 0;

    private List<String> allowedUsers;

    private List<String> allowedDomains;

    private Map<String, String> shareMetadata;

    public enum ShareScope {
        PRIVATE,
        PUBLIC,
        DOMAIN,
        USER_LIST,
        TOKEN_ONLY
    }

    public boolean canShare() {
        if (!shareable) {
            return false;
        }
        if (shareExpireAt != null && Instant.now().isAfter(shareExpireAt)) {
            return false;
        }
        if (shareLimit > 0 && sharedCount >= shareLimit) {
            return false;
        }
        return true;
    }

    public boolean hasAccess(String userId, String domain) {
        if (!shareable) {
            return false;
        }

        switch (shareScope) {
            case PUBLIC:
                return true;
            case DOMAIN:
                return allowedDomains != null && allowedDomains.contains(domain);
            case USER_LIST:
                return allowedUsers != null && allowedUsers.contains(userId);
            case TOKEN_ONLY:
                return true;
            case PRIVATE:
            default:
                return false;
        }
    }

    public void recordShare() {
        sharedCount++;
    }

    public String generateShareLink(String baseUrl) {
        if (!canShare()) {
            return null;
        }
        String capabilityId = capability != null ? capability.getCapabilityId() : "unknown";
        return baseUrl + "/share/capability/" + capabilityId + "?token=" + shareToken;
    }
}
