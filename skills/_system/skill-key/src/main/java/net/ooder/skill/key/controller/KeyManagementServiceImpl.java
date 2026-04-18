package net.ooder.skill.key.controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.ooder.skill.key.dto.*;

public class KeyManagementServiceImpl implements KeyManagementService {
    
    private final Map<String, KeyDTO> keyStore = new ConcurrentHashMap<>();
    
    @Override
    public List<KeyDTO> getAllKeys() {
        return new ArrayList<>(keyStore.values());
    }
    
    @Override
    public KeyDTO getKey(String keyId) {
        return keyStore.get(keyId);
    }
    
    @Override
    public KeyDTO generateKey(KeyGenerateRequestDTO request) {
        String keyId = "key-" + UUID.randomUUID().toString().substring(0, 8);
        
        KeyDTO key = new KeyDTO();
        key.setKeyId(keyId);
        key.setUserId(request.getUserId());
        key.setSceneGroupId(request.getSceneGroupId());
        key.setKeyName(request.getKeyName());
        key.setKeyType(request.getKeyType());
        key.setScope(request.getScope());
        key.setProvider(request.getProvider());
        key.setDescription(request.getDescription());
        key.setKeyPrefix("sk-ooder-" + UUID.randomUUID().toString().substring(0, 8));
        key.setStatus("active");
        key.setCreatedAt(System.currentTimeMillis());
        
        if (request.getExpireTimeMs() != null && request.getExpireTimeMs() > 0) {
            key.setExpiresAt(System.currentTimeMillis() + request.getExpireTimeMs());
        }
        
        keyStore.put(keyId, key);
        return key;
    }
    
    @Override
    public KeyDTO refreshKey(String keyId) {
        KeyDTO key = keyStore.get(keyId);
        if (key == null) {
            return null;
        }
        
        key.setKeyPrefix("sk-ooder-" + UUID.randomUUID().toString().substring(0, 8));
        key.setLastUsedAt(System.currentTimeMillis());
        
        return key;
    }
    
    @Override
    public boolean revokeKey(String keyId) {
        KeyDTO key = keyStore.get(keyId);
        if (key == null) {
            return false;
        }
        
        key.setStatus("revoked");
        return true;
    }
    
    @Override
    public boolean validateKey(String keyId, String scope) {
        KeyDTO key = keyStore.get(keyId);
        if (key == null) {
            return false;
        }
        
        if (!"active".equals(key.getStatus())) {
            return false;
        }
        
        if (key.getExpiresAt() != null && key.getExpiresAt() < System.currentTimeMillis()) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public KeyAccessResultDTO accessResource(String keyId, String resource, String action) {
        KeyAccessResultDTO result = new KeyAccessResultDTO();
        
        KeyDTO key = keyStore.get(keyId);
        if (key == null) {
            result.setAllowed(false);
            result.setReason("Key not found");
            return result;
        }
        
        if (!"active".equals(key.getStatus())) {
            result.setAllowed(false);
            result.setReason("Key is not active");
            return result;
        }
        
        result.setAllowed(true);
        result.setReason("Access granted");
        
        return result;
    }
    
    @Override
    public List<KeyDTO> getKeysByUser(String userId) {
        List<KeyDTO> result = new ArrayList<>();
        for (KeyDTO key : keyStore.values()) {
            if (userId.equals(key.getUserId())) {
                result.add(key);
            }
        }
        return result;
    }
    
    @Override
    public List<KeyDTO> getKeysByScene(String sceneGroupId) {
        List<KeyDTO> result = new ArrayList<>();
        for (KeyDTO key : keyStore.values()) {
            if (sceneGroupId.equals(key.getSceneGroupId())) {
                result.add(key);
            }
        }
        return result;
    }
}
