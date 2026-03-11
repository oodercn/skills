package net.ooder.skill.security.dto.key;

import lombok.Data;
import net.ooder.skill.security.dto.key.KeyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class KeyCreateRequest {
    
    private String keyName;
    private KeyType keyType;
    private String provider;
    private String rawValue;
    private long expiresAt;
    private int maxUseCount;
    private List<String> allowedUsers = new ArrayList<>();
    private List<String> allowedRoles = new ArrayList<>();
    private List<String> allowedScenes = new ArrayList<>();
    private Map<String, Object> config = new HashMap<>();
}
