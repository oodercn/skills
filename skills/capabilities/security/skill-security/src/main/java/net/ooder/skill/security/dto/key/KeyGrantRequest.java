package net.ooder.skill.security.dto.key;

import lombok.Data;

import java.util.List;

@Data
public class KeyGrantRequest {
    
    private String keyId;
    private String userId;
    private String roleId;
    private String sceneId;
    private List<String> userIds;
    private List<String> roleIds;
    private List<String> sceneIds;
}
