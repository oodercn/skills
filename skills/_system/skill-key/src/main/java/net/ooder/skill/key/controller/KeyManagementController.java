package net.ooder.skill.key.controller;

import net.ooder.skill.key.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/keys")
@CrossOrigin(originPatterns = "*")
public class KeyManagementController {

    private static final Logger log = LoggerFactory.getLogger(KeyManagementController.class);

    @Autowired(required = false)
    private KeyManagementService keyService;

    @GetMapping
    public ResponseEntity<List<KeyDTO>> listKeys(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sceneGroupId) {
        
        log.info("[listKeys] type={}, status={}, userId={}, sceneGroupId={}", type, status, userId, sceneGroupId);
        
        if (keyService == null) {
            return ResponseEntity.ok(List.of());
        }
        
        List<KeyDTO> keys;
        if (userId != null) {
            keys = keyService.getKeysByUser(userId);
        } else if (sceneGroupId != null) {
            keys = keyService.getKeysByScene(sceneGroupId);
        } else {
            keys = keyService.getAllKeys();
        }
        
        List<KeyDTO> result = new ArrayList<>();
        for (KeyDTO key : keys) {
            if (type != null && !type.isEmpty() && !type.equals(key.getKeyType()) && !type.equals(key.getScope())) {
                continue;
            }
            if (status != null && !status.isEmpty() && !status.equals(key.getStatus())) {
                continue;
            }
            result.add(key);
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{keyId}")
    public ResponseEntity<KeyDTO> getKey(@PathVariable String keyId) {
        log.info("[getKey] keyId={}", keyId);
        
        if (keyService == null) {
            return ResponseEntity.notFound().build();
        }
        
        KeyDTO key = keyService.getKey(keyId);
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(key);
    }

    @PostMapping
    public ResponseEntity<KeyDTO> createKey(@RequestBody CreateKeyRequest request) {
        log.info("[createKey] request={}", request);
        
        if (keyService == null) {
            return ResponseEntity.internalServerError().build();
        }
        
        KeyGenerateRequestDTO generateRequest = new KeyGenerateRequestDTO();
        generateRequest.setUserId("current-user");
        generateRequest.setSceneGroupId(request.getSceneGroupId());
        generateRequest.setInstallId(request.getInstallId());
        generateRequest.setScope(request.getKeyType());
        generateRequest.setDescription(request.getDescription());
        generateRequest.setKeyName(request.getKeyName());
        generateRequest.setKeyType(request.getKeyType());
        generateRequest.setProvider(request.getProvider());
        generateRequest.setAllowedUsers(request.getAllowedUsers());
        generateRequest.setAllowedRoles(request.getAllowedRoles());
        generateRequest.setAllowedScenes(request.getAllowedScenes());
        
        if (request.getExpiresAt() != null) {
            long expiresTime = request.getExpiresAt() - System.currentTimeMillis();
            generateRequest.setExpireTimeMs(expiresTime);
        }
        
        if (request.getMaxUseCount() != null) {
            Map<String, Object> permissions = new HashMap<>();
            permissions.put("maxUseCount", request.getMaxUseCount());
            generateRequest.setPermissions(permissions);
        }
        
        KeyDTO key = keyService.generateKey(generateRequest);
        return ResponseEntity.ok(key);
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<KeyDTO> rotateKey(
            @PathVariable String keyId,
            @RequestBody(required = false) String newValue) {
        log.info("[rotateKey] keyId={}", keyId);
        
        if (keyService == null) {
            return ResponseEntity.notFound().build();
        }
        
        KeyDTO key = keyService.refreshKey(keyId);
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(key);
    }

    @PostMapping("/{keyId}/revoke")
    public ResponseEntity<KeyDTO> revokeKey(@PathVariable String keyId) {
        log.info("[revokeKey] keyId={}", keyId);
        
        if (keyService == null) {
            return ResponseEntity.notFound().build();
        }
        
        boolean success = keyService.revokeKey(keyId);
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        
        KeyDTO key = keyService.getKey(keyId);
        return ResponseEntity.ok(key);
    }

    @PostMapping("/{keyId}/validate")
    public ResponseEntity<KeyValidateResultDTO> validateKey(
            @PathVariable String keyId,
            @RequestParam(required = false) String scope) {
        log.info("[validateKey] keyId={}, scope={}", keyId, scope);
        
        if (keyService == null) {
            KeyValidateResultDTO result = new KeyValidateResultDTO();
            result.setKeyId(keyId);
            result.setValid(false);
            return ResponseEntity.ok(result);
        }
        
        boolean valid = keyService.validateKey(keyId, scope);
        
        KeyValidateResultDTO result = new KeyValidateResultDTO();
        result.setKeyId(keyId);
        result.setValid(valid);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{keyId}/access")
    public ResponseEntity<KeyAccessResultDTO> accessResource(
            @PathVariable String keyId,
            @RequestBody AccessResourceRequest request) {
        
        String resource = request.getResource();
        String action = request.getAction();
        
        log.info("[accessResource] keyId={}, resource={}, action={}", keyId, resource, action);
        
        if (keyService == null) {
            KeyAccessResultDTO response = new KeyAccessResultDTO();
            response.setAllowed(false);
            response.setReason("Key service not available");
            return ResponseEntity.ok(response);
        }
        
        KeyAccessResultDTO result = keyService.accessResource(keyId, resource, action);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<KeyDTO>> getKeysByUser(@PathVariable String userId) {
        log.info("[getKeysByUser] userId={}", userId);
        
        if (keyService == null) {
            return ResponseEntity.ok(List.of());
        }
        
        List<KeyDTO> keys = keyService.getKeysByUser(userId);
        return ResponseEntity.ok(keys);
    }

    @GetMapping("/by-scene/{sceneGroupId}")
    public ResponseEntity<List<KeyDTO>> getKeysByScene(@PathVariable String sceneGroupId) {
        log.info("[getKeysByScene] sceneGroupId={}", sceneGroupId);
        
        if (keyService == null) {
            return ResponseEntity.ok(List.of());
        }
        
        List<KeyDTO> keys = keyService.getKeysByScene(sceneGroupId);
        return ResponseEntity.ok(keys);
    }
}
