package net.ooder.skill.security.dto.key;

import lombok.Data;

@Data
public class KeyUseRequest {
    
    private String keyId;
    private String userId;
    private String sceneId;
    private String purpose;
}
