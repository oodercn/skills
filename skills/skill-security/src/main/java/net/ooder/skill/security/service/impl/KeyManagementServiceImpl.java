package net.ooder.skill.security.service.impl;

import net.ooder.skill.security.dto.audit.*;
import net.ooder.skill.security.dto.key.*;
import net.ooder.skill.security.service.AuditService;
import net.ooder.skill.security.service.EncryptionService;
import net.ooder.skill.security.service.KeyManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KeyManagementServiceImpl implements KeyManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(KeyManagementServiceImpl.class);
    
    private final Map<String, ApiKeyDTO> keyStore = new ConcurrentHashMap<>();
    private final Map<String, String> encryptedValues = new ConcurrentHashMap<>();
    private final Map<String, KeyUsageStats> usageStats = new ConcurrentHashMap<>();
    
    @Autowired
    private EncryptionService encryptionService;
    
    @Autowired
    private AuditService auditService;
    
    @PostConstruct
    public void init() {
        log.info("KeyManagementService initialized");
    }
    
    @Override
    public ApiKeyDTO createKey(KeyCreateRequest request) {
        String keyId = generateKeyId();
        
        ApiKeyDTO key = new ApiKeyDTO();
        key.setKeyId(keyId);
        key.setKeyName(request.getKeyName());
        key.setKeyType(request.getKeyType());
        key.setProvider(request.getProvider());
        key.setExpiresAt(request.getExpiresAt());
        key.setMaxUseCount(request.getMaxUseCount());
        key.setAllowedUsers(request.getAllowedUsers());
        key.setAllowedRoles(request.getAllowedRoles());
        key.setAllowedScenes(request.getAllowedScenes());
        key.setConfig(request.getConfig());
        key.setCreatedBy("system");
        
        String encrypted = encryptionService.encrypt(request.getRawValue());
        encryptedValues.put(keyId, encrypted);
        keyStore.put(keyId, key);
        
        KeyUsageStats stats = new KeyUsageStats();
        stats.setKeyId(keyId);
        usageStats.put(keyId, stats);
        
        auditService.log(AuditLogDTO.success(AuditEventType.KEY_CREATE, "system", keyId));
        log.info("Created key: {} type: {}", keyId, request.getKeyType());
        
        return key;
    }
    
    @Override
    public ApiKeyDTO getKey(String keyId) {
        return keyStore.get(keyId);
    }
    
    @Override
    public ApiKeyDTO getKeyByName(String keyName) {
        return keyStore.values().stream()
            .filter(k -> keyName.equals(k.getKeyName()))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public List<ApiKeyDTO> listKeys(KeyType type, String status) {
        return keyStore.values().stream()
            .filter(k -> type == null || type == k.getKeyType())
            .filter(k -> status == null || status.equals(k.getStatus()))
            .collect(Collectors.toList());
    }
    
    @Override
    public ApiKeyDTO updateKey(String keyId, ApiKeyDTO key) {
        ApiKeyDTO existing = keyStore.get(keyId);
        if (existing == null) {
            return null;
        }
        
        key.setKeyId(keyId);
        key.setCreatedAt(existing.getCreatedAt());
        key.setCreatedBy(existing.getCreatedBy());
        keyStore.put(keyId, key);
        
        log.info("Updated key: {}", keyId);
        return key;
    }
    
    @Override
    public boolean deleteKey(String keyId) {
        ApiKeyDTO removed = keyStore.remove(keyId);
        encryptedValues.remove(keyId);
        usageStats.remove(keyId);
        
        if (removed != null) {
            auditService.log(AuditLogDTO.success(AuditEventType.KEY_REVOKE, "system", keyId));
            log.info("Deleted key: {}", keyId);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean revokeKey(String keyId) {
        ApiKeyDTO key = keyStore.get(keyId);
        if (key == null) {
            return false;
        }
        
        key.setStatus(KeyStatus.REVOKED.getCode());
        auditService.log(AuditLogDTO.success(AuditEventType.KEY_REVOKE, "system", keyId));
        log.info("Revoked key: {}", keyId);
        return true;
    }
    
    @Override
    public ApiKeyDTO rotateKey(String keyId, String newValue) {
        ApiKeyDTO key = keyStore.get(keyId);
        if (key == null) {
            return null;
        }
        
        String encrypted = encryptionService.encrypt(newValue);
        encryptedValues.put(keyId, encrypted);
        
        key.setUseCount(0);
        key.setLastUsedAt(0);
        
        auditService.log(AuditLogDTO.success(AuditEventType.KEY_ROTATE, "system", keyId));
        log.info("Rotated key: {}", keyId);
        
        return key;
    }
    
    @Override
    public String useKey(String keyId, String userId, String sceneId) {
        ApiKeyDTO key = keyStore.get(keyId);
        if (key == null) {
            auditService.log(AuditLogDTO.failure(AuditEventType.KEY_USE, userId, keyId));
            return null;
        }
        
        if (!key.isActive()) {
            auditService.log(AuditLogDTO.failure(AuditEventType.KEY_USE, userId, keyId));
            return null;
        }
        
        if (!checkAccess(keyId, userId, sceneId)) {
            auditService.log(AuditLogDTO.denied(AuditEventType.KEY_USE, userId, keyId));
            return null;
        }
        
        if (!key.canUse()) {
            auditService.log(AuditLogDTO.failure(AuditEventType.KEY_USE, userId, keyId));
            return null;
        }
        
        String encrypted = encryptedValues.get(keyId);
        String rawValue = encryptionService.decrypt(encrypted);
        
        key.setUseCount(key.getUseCount() + 1);
        key.setLastUsedAt(System.currentTimeMillis());
        
        KeyUsageStats stats = usageStats.get(keyId);
        if (stats != null) {
            stats.setTotalUseCount(stats.getTotalUseCount() + 1);
            stats.setTodayUseCount(stats.getTodayUseCount() + 1);
            stats.setLastUsedAt(System.currentTimeMillis());
            stats.setLastUsedBy(userId);
            stats.setLastUsedScene(sceneId);
        }
        
        auditService.log(AuditLogDTO.success(AuditEventType.KEY_USE, userId, keyId));
        log.debug("Used key: {} by user: {}", keyId, userId);
        
        return rawValue;
    }
    
    @Override
    public boolean grantAccess(String keyId, KeyGrantRequest request) {
        ApiKeyDTO key = keyStore.get(keyId);
        if (key == null) {
            return false;
        }
        
        if (request.getUserId() != null) {
            key.getAllowedUsers().add(request.getUserId());
        }
        if (request.getRoleId() != null) {
            key.getAllowedRoles().add(request.getRoleId());
        }
        if (request.getSceneId() != null) {
            key.getAllowedScenes().add(request.getSceneId());
        }
        if (request.getUserIds() != null) {
            key.getAllowedUsers().addAll(request.getUserIds());
        }
        if (request.getRoleIds() != null) {
            key.getAllowedRoles().addAll(request.getRoleIds());
        }
        if (request.getSceneIds() != null) {
            key.getAllowedScenes().addAll(request.getSceneIds());
        }
        
        auditService.log(AuditLogDTO.success(AuditEventType.PERMISSION_GRANT, "system", keyId));
        log.info("Granted access to key: {}", keyId);
        return true;
    }
    
    @Override
    public boolean revokeAccess(String keyId, String principalId, String principalType) {
        ApiKeyDTO key = keyStore.get(keyId);
        if (key == null) {
            return false;
        }
        
        if ("user".equals(principalType)) {
            key.getAllowedUsers().remove(principalId);
        } else if ("role".equals(principalType)) {
            key.getAllowedRoles().remove(principalId);
        } else if ("scene".equals(principalType)) {
            key.getAllowedScenes().remove(principalId);
        }
        
        auditService.log(AuditLogDTO.success(AuditEventType.PERMISSION_REVOKE, "system", keyId));
        log.info("Revoked access to key: {} for {}: {}", keyId, principalType, principalId);
        return true;
    }
    
    @Override
    public boolean checkAccess(String keyId, String userId, String sceneId) {
        ApiKeyDTO key = keyStore.get(keyId);
        if (key == null) {
            return false;
        }
        
        if (key.getAllowedUsers().isEmpty() && key.getAllowedRoles().isEmpty() && key.getAllowedScenes().isEmpty()) {
            return true;
        }
        
        if (userId != null && key.getAllowedUsers().contains(userId)) {
            return true;
        }
        
        if (sceneId != null && key.getAllowedScenes().contains(sceneId)) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public KeyUsageStats getUsageStats(String keyId) {
        return usageStats.get(keyId);
    }
    
    @Override
    public String generateKeyId() {
        return "key-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Override
    public String generateKey() {
        return encryptionService.generateKey();
    }
}
