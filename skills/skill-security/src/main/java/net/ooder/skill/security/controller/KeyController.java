package net.ooder.skill.security.controller;

import net.ooder.skill.security.dto.audit.*;
import net.ooder.skill.security.dto.key.*;
import net.ooder.skill.security.service.AuditService;
import net.ooder.skill.security.service.KeyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/keys")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class KeyController {
    
    @Autowired
    private KeyManagementService keyService;
    
    @Autowired
    private AuditService auditService;
    
    @GetMapping
    public ResponseEntity<List<ApiKeyDTO>> listKeys(
            @RequestParam(required = false) KeyType type,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(keyService.listKeys(type, status));
    }
    
    @PostMapping
    public ResponseEntity<ApiKeyDTO> createKey(@RequestBody KeyCreateRequest request) {
        return ResponseEntity.ok(keyService.createKey(request));
    }
    
    @GetMapping("/{keyId}")
    public ResponseEntity<ApiKeyDTO> getKey(@PathVariable String keyId) {
        ApiKeyDTO key = keyService.getKey(keyId);
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(key);
    }
    
    @PutMapping("/{keyId}")
    public ResponseEntity<ApiKeyDTO> updateKey(@PathVariable String keyId, @RequestBody ApiKeyDTO key) {
        ApiKeyDTO updated = keyService.updateKey(keyId, key);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{keyId}")
    public ResponseEntity<Boolean> deleteKey(@PathVariable String keyId) {
        return ResponseEntity.ok(keyService.deleteKey(keyId));
    }
    
    @PostMapping("/{keyId}/revoke")
    public ResponseEntity<Boolean> revokeKey(@PathVariable String keyId) {
        return ResponseEntity.ok(keyService.revokeKey(keyId));
    }
    
    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyDTO> rotateKey(@PathVariable String keyId, @RequestBody String newValue) {
        ApiKeyDTO rotated = keyService.rotateKey(keyId, newValue);
        if (rotated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rotated);
    }
    
    @PostMapping("/{keyId}/use")
    public ResponseEntity<String> useKey(@PathVariable String keyId, @RequestBody KeyUseRequest request) {
        String rawValue = keyService.useKey(keyId, request.getUserId(), request.getSceneId());
        if (rawValue == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(rawValue);
    }
    
    @PostMapping("/{keyId}/grant")
    public ResponseEntity<Boolean> grantAccess(@PathVariable String keyId, @RequestBody KeyGrantRequest request) {
        request.setKeyId(keyId);
        return ResponseEntity.ok(keyService.grantAccess(keyId, request));
    }
    
    @PostMapping("/{keyId}/revoke-access")
    public ResponseEntity<Boolean> revokeAccess(
            @PathVariable String keyId,
            @RequestParam String principalId,
            @RequestParam String principalType) {
        return ResponseEntity.ok(keyService.revokeAccess(keyId, principalId, principalType));
    }
    
    @GetMapping("/{keyId}/stats")
    public ResponseEntity<KeyUsageStats> getUsageStats(@PathVariable String keyId) {
        KeyUsageStats stats = keyService.getUsageStats(keyId);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/name/{keyName}")
    public ResponseEntity<ApiKeyDTO> getKeyByName(@PathVariable String keyName) {
        ApiKeyDTO key = keyService.getKeyByName(keyName);
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(key);
    }
}
