package net.ooder.skill.key.controller;

import java.util.List;
import net.ooder.skill.key.dto.*;

public interface KeyManagementService {
    
    List<KeyDTO> getAllKeys();
    
    KeyDTO getKey(String keyId);
    
    KeyDTO generateKey(KeyGenerateRequestDTO request);
    
    KeyDTO refreshKey(String keyId);
    
    boolean revokeKey(String keyId);
    
    boolean validateKey(String keyId, String scope);
    
    KeyAccessResultDTO accessResource(String keyId, String resource, String action);
    
    List<KeyDTO> getKeysByUser(String userId);
    
    List<KeyDTO> getKeysByScene(String sceneGroupId);
}
