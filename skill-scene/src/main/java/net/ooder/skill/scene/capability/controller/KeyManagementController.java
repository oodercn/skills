package net.ooder.skill.scene.capability.controller;

import net.ooder.skill.scene.capability.service.KeyManagementService;
import net.ooder.skill.scene.capability.service.KeyManagementService.KeyInfo;
import net.ooder.skill.scene.capability.service.KeyManagementService.KeyGenerateRequest;
import net.ooder.skill.scene.capability.service.KeyManagementService.KeyAccessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/keys")
@CrossOrigin(origins = "*")
public class KeyManagementController {

    private static final Logger log = LoggerFactory.getLogger(KeyManagementController.class);

    @Autowired
    private KeyManagementService keyManagementService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listKeys(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sceneGroupId) {
        
        log.info("[listKeys] type={}, status={}, userId={}, sceneGroupId={}", type, status, userId, sceneGroupId);
        
        List<KeyInfo> keys;
        if (userId != null) {
            keys = keyManagementService.getKeysByUser(userId);
        } else if (sceneGroupId != null) {
            keys = keyManagementService.getKeysByScene(sceneGroupId);
        } else {
            keys = keyManagementService.getKeysByUser("current-user");
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (KeyInfo key : keys) {
            if (type != null && !type.isEmpty() && !type.equals(key.getScope())) {
                continue;
            }
            if (status != null && !status.isEmpty() && !status.equals(key.getStatus().name())) {
                continue;
            }
            result.add(convertToMap(key));
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{keyId}")
    public ResponseEntity<Map<String, Object>> getKey(@PathVariable String keyId) {
        log.info("[getKey] keyId={}", keyId);
        
        KeyInfo key = keyManagementService.getKey(keyId);
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertToMap(key));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createKey(@RequestBody Map<String, Object> request) {
        log.info("[createKey] request={}", request);
        
        KeyGenerateRequest generateRequest = new KeyGenerateRequest();
        generateRequest.setUserId("current-user");
        generateRequest.setSceneGroupId((String) request.get("sceneGroupId"));
        generateRequest.setInstallId((String) request.get("installId"));
        generateRequest.setScope((String) request.get("keyType"));
        generateRequest.setDescription((String) request.get("description"));
        
        if (request.containsKey("expiresAt")) {
            Object expiresAt = request.get("expiresAt");
            if (expiresAt instanceof Number) {
                long expiresTime = ((Number) expiresAt).longValue() - System.currentTimeMillis();
                generateRequest.setExpireTimeMs(expiresTime);
            }
        }
        
        if (request.containsKey("maxUseCount")) {
            Map<String, Object> permissions = new HashMap<>();
            permissions.put("maxUseCount", request.get("maxUseCount"));
            generateRequest.setPermissions(permissions);
        }
        
        KeyInfo key = keyManagementService.generateKey(generateRequest);
        
        Map<String, Object> result = convertToMap(key);
        result.put("keyValue", key.getKeyValue());
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<Map<String, Object>> rotateKey(
            @PathVariable String keyId,
            @RequestBody(required = false) String newValue) {
        log.info("[rotateKey] keyId={}", keyId);
        
        KeyInfo key = keyManagementService.refreshKey(keyId);
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertToMap(key));
    }

    @PostMapping("/{keyId}/revoke")
    public ResponseEntity<Map<String, Object>> revokeKey(@PathVariable String keyId) {
        log.info("[revokeKey] keyId={}", keyId);
        
        boolean success = keyManagementService.revokeKey(keyId);
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        
        KeyInfo key = keyManagementService.getKey(keyId);
        return ResponseEntity.ok(convertToMap(key));
    }

    @PostMapping("/{keyId}/validate")
    public ResponseEntity<Map<String, Object>> validateKey(
            @PathVariable String keyId,
            @RequestParam(required = false) String scope) {
        log.info("[validateKey] keyId={}, scope={}", keyId, scope);
        
        boolean valid = keyManagementService.validateKey(keyId, scope);
        
        Map<String, Object> result = new HashMap<>();
        result.put("keyId", keyId);
        result.put("valid", valid);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{keyId}/access")
    public ResponseEntity<Map<String, Object>> accessResource(
            @PathVariable String keyId,
            @RequestBody Map<String, Object> request) {
        
        String resource = (String) request.get("resource");
        String action = (String) request.get("action");
        
        log.info("[accessResource] keyId={}, resource={}, action={}", keyId, resource, action);
        
        KeyAccessResult result = keyManagementService.accessResource(keyId, resource, action);
        
        Map<String, Object> response = new HashMap<>();
        response.put("allowed", result.isAllowed());
        response.put("reason", result.getReason());
        
        if (result.getKeyInfo() != null) {
            response.put("keyInfo", convertToMap(result.getKeyInfo()));
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getKeysByUser(@PathVariable String userId) {
        log.info("[getKeysByUser] userId={}", userId);
        
        List<KeyInfo> keys = keyManagementService.getKeysByUser(userId);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (KeyInfo key : keys) {
            result.add(convertToMap(key));
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-scene/{sceneGroupId}")
    public ResponseEntity<List<Map<String, Object>>> getKeysByScene(@PathVariable String sceneGroupId) {
        log.info("[getKeysByScene] sceneGroupId={}", sceneGroupId);
        
        List<KeyInfo> keys = keyManagementService.getKeysByScene(sceneGroupId);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (KeyInfo key : keys) {
            result.add(convertToMap(key));
        }
        
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> convertToMap(KeyInfo key) {
        Map<String, Object> map = new HashMap<>();
        map.put("keyId", key.getKeyId());
        map.put("keyName", key.getDescription() != null ? key.getDescription() : key.getKeyId());
        map.put("keyType", key.getScope());
        map.put("provider", key.getSceneGroupId());
        map.put("status", key.getStatus().name());
        map.put("createdAt", key.getCreateTime());
        map.put("expiresAt", key.getExpireTime());
        map.put("lastAccessAt", key.getLastAccessTime());
        map.put("useCount", key.getAccessCount());
        map.put("maxUseCount", key.getPermissions() != null ? key.getPermissions().get("maxUseCount") : -1);
        map.put("userId", key.getUserId());
        map.put("sceneGroupId", key.getSceneGroupId());
        map.put("installId", key.getInstallId());
        return map;
    }
}
