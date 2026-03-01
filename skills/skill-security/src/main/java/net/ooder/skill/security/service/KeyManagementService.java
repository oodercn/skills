package net.ooder.skill.security.service;

import net.ooder.skill.security.dto.key.*;

import java.util.List;

public interface KeyManagementService {
    
    ApiKeyDTO createKey(KeyCreateRequest request);
    
    ApiKeyDTO getKey(String keyId);
    
    ApiKeyDTO getKeyByName(String keyName);
    
    List<ApiKeyDTO> listKeys(KeyType type, String status);
    
    ApiKeyDTO updateKey(String keyId, ApiKeyDTO key);
    
    boolean deleteKey(String keyId);
    
    boolean revokeKey(String keyId);
    
    ApiKeyDTO rotateKey(String keyId, String newValue);
    
    String useKey(String keyId, String userId, String sceneId);
    
    boolean grantAccess(String keyId, KeyGrantRequest request);
    
    boolean revokeAccess(String keyId, String principalId, String principalType);
    
    boolean checkAccess(String keyId, String userId, String sceneId);
    
    KeyUsageStats getUsageStats(String keyId);
    
    String generateKeyId();
    
    String generateKey();
}
