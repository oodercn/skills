package net.ooder.skill.keys.controller;

import net.ooder.skill.keys.dto.ApiKeyDTO;
import net.ooder.skill.keys.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/keys")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class KeyController {

    private static final Logger log = LoggerFactory.getLogger(KeyController.class);

    private final Map<String, ApiKeyDTO> keyStore = new HashMap<>();
    private Long keyIdCounter = 1L;

    public KeyController() {
        initDefaultKeys();
    }

    private void initDefaultKeys() {
        ApiKeyDTO defaultKey = new ApiKeyDTO();
        defaultKey.setKeyId("key-1");
        defaultKey.setName("默认API Key");
        defaultKey.setUserId("current-user");
        defaultKey.setKeyPrefix("sk-ooder-****");
        defaultKey.setType("api");
        defaultKey.setStatus("active");
        defaultKey.setProvider("qianwen");
        defaultKey.setCreatedAt(System.currentTimeMillis());
        defaultKey.setLastUsedAt(System.currentTimeMillis());
        defaultKey.setUsageCount(0);
        keyStore.put(defaultKey.getKeyId(), defaultKey);
    }

    @GetMapping
    public ResultModel<List<ApiKeyDTO>> listKeys(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String status) {
        log.info("[KeyController] List keys called - userId: {}, status: {}", userId, status);

        List<ApiKeyDTO> keys = new ArrayList<>(keyStore.values());
        if (userId != null && !userId.isEmpty()) {
            keys.removeIf(k -> !userId.equals(k.getUserId()));
        }
        if (status != null && !status.isEmpty()) {
            keys.removeIf(k -> !status.equals(k.getStatus()));
        }

        return ResultModel.success(keys);
    }

    @GetMapping("/my")
    public ResultModel<List<ApiKeyDTO>> listMyKeys(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String userId = userIdHeader != null ? userIdHeader : "current-user";
        log.info("[KeyController] List my keys called - userId: {}", userId);

        List<ApiKeyDTO> keys = new ArrayList<>(keyStore.values());
        keys.removeIf(k -> !userId.equals(k.getUserId()));

        return ResultModel.success(keys);
    }

    @GetMapping("/{keyId}")
    public ResultModel<ApiKeyDTO> getKey(@PathVariable String keyId) {
        log.info("[KeyController] Get key called: {}", keyId);
        ApiKeyDTO key = keyStore.get(keyId);
        if (key == null) {
            return ResultModel.notFound("Key not found: " + keyId);
        }
        return ResultModel.success(key);
    }

    @PostMapping
    public ResultModel<ApiKeyDTO> createKey(
            @RequestBody ApiKeyDTO request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String userId = userIdHeader != null ? userIdHeader : "current-user";
        log.info("[KeyController] Create key called for user: {}", userId);

        String keyId = "key-" + keyIdCounter++;
        request.setKeyId(keyId);
        request.setUserId(userId);
        request.setKeyPrefix("sk-ooder-" + UUID.randomUUID().toString().substring(0, 8));
        request.setStatus("active");
        request.setCreatedAt(System.currentTimeMillis());

        keyStore.put(keyId, request);
        return ResultModel.success(request);
    }

    @PostMapping("/{keyId}/rotate")
    public ResultModel<ApiKeyDTO> rotateKey(
            @PathVariable String keyId,
            @RequestBody(required = false) Map<String, String> body) {
        log.info("[KeyController] Rotate key called: {}", keyId);

        ApiKeyDTO existing = keyStore.get(keyId);
        if (existing == null) {
            return ResultModel.notFound("Key not found: " + keyId);
        }

        existing.setKeyPrefix("sk-ooder-" + UUID.randomUUID().toString().substring(0, 8));
        existing.setLastUsedAt(System.currentTimeMillis());

        return ResultModel.success(existing);
    }

    @PostMapping("/{keyId}/revoke")
    public ResultModel<Boolean> revokeKey(@PathVariable String keyId) {
        log.info("[KeyController] Revoke key called: {}", keyId);

        ApiKeyDTO key = keyStore.get(keyId);
        if (key == null) {
            return ResultModel.notFound("Key not found: " + keyId);
        }

        key.setStatus("revoked");
        return ResultModel.success(true);
    }

    @DeleteMapping("/{keyId}")
    public ResultModel<Boolean> deleteKey(@PathVariable String keyId) {
        log.info("[KeyController] Delete key called: {}", keyId);

        ApiKeyDTO removed = keyStore.remove(keyId);
        if (removed == null) {
            return ResultModel.notFound("Key not found: " + keyId);
        }

        return ResultModel.success(true);
    }
}