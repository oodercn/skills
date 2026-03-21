# SE 团队协作请求：KeyManagement 持久化集成

## 一、问题描述

### 1.1 现象

KeyManagement 页面创建的密钥在服务重启后全部丢失。

### 1.2 根本原因

**KeyManagementServiceImpl 使用内存存储，未集成 SE 提供的持久化服务**

查看源码 `KeyManagementServiceImpl.java` 第 20-22 行：

```java
@Service
public class KeyManagementServiceImpl implements KeyManagementService {

    private Map<String, KeyInfo> keyStore = new ConcurrentHashMap<>();  // 内存存储！
    private Map<String, List<String>> userKeys = new ConcurrentHashMap<>();
    private Map<String, List<String>> sceneKeys = new ConcurrentHashMap<>();
    
    // ...
}
```

**问题**：数据仅存储在内存中，服务重启后数据丢失。

---

## 二、SE 项目已提供的持久化能力

### 2.1 持久化服务

SE 项目已在 SDK 2.3.1 中提供了完整的持久化能力：

| 功能 | 实现类 | 方法 |
|------|--------|------|
| 密钥存储 | `JsonKeyStorageService` | `saveKey()`, `loadKey()`, `deleteKey()` |
| 入网请求 | `JsonKeyStorageService` | `saveRequest()`, `loadRequest()` |
| 规则管理 | `JsonKeyStorageService` | `saveRule()`, `loadRule()` |
| 使用日志 | `JsonKeyStorageService` | `saveUsageLog()`, `loadUsageLogs()` |
| 自动配置 | `KeyManagementAutoConfiguration` | Bean 注册 |

### 2.2 自动配置

SE 提供了 `KeyManagementAutoConfiguration`，会自动注册相关 Bean。

---

## 三、任务清单

### 3.1 核心任务（必须完成）

| 序号 | 任务 | 优先级 | 说明 |
|------|------|--------|------|
| 1 | 注入 JsonKeyStorageService | P0 | 替换内存存储 |
| 2 | 使用 saveKey() 保存密钥 | P0 | generateKey() 方法改造 |
| 3 | 使用 loadKey() 加载密钥 | P0 | getKey() 方法改造 |
| 4 | 使用 deleteKey() 删除密钥 | P0 | revokeKey() 方法改造 |
| 5 | 添加 @PostConstruct 初始化加载 | P0 | 服务启动时恢复所有密钥 |

### 3.2 功能完善任务（建议完成）

| 序号 | 任务 | 优先级 | 说明 |
|------|------|--------|------|
| 6 | 添加 allowedUsers 字段 | P1 | 授权用户列表 |
| 7 | 添加 allowedRoles 字段 | P1 | 授权角色列表 |
| 8 | 添加 allowedScenes 字段 | P1 | 授权场景列表 |
| 9 | 添加 keyName 字段映射 | P2 | 前端 keyName 与后端映射 |
| 10 | 添加 provider 字段 | P2 | 服务提供商信息 |

### 3.3 扩展功能任务（可选）

| 序号 | 任务 | 优先级 | 说明 |
|------|------|--------|------|
| 11 | 集成 KeyUsageLog 持久化 | P2 | 密钥使用日志 |
| 12 | 集成 KeyRule 持久化 | P3 | 密钥规则管理 |
| 13 | 集成 NetworkJoinRequest 持久化 | P3 | 入网审批流程 |

---

## 四、实现方案

### 4.1 方案一：使用 JsonKeyStorageService（推荐）

SE 已提供专门的 `JsonKeyStorageService`，直接注入使用：

```java
@Service
public class KeyManagementServiceImpl implements KeyManagementService {

    private static final Logger log = LoggerFactory.getLogger(KeyManagementServiceImpl.class);
    
    @Autowired(required = false)
    private JsonKeyStorageService jsonKeyStorageService;
    
    private Map<String, KeyInfo> keyStore = new ConcurrentHashMap<>();
    private Map<String, List<String>> userKeys = new ConcurrentHashMap<>();
    private Map<String, List<String>> sceneKeys = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        loadAllKeys();
    }
    
    private void loadAllKeys() {
        if (jsonKeyStorageService == null) {
            log.info("[loadAllKeys] JsonKeyStorageService not available, using memory-only mode");
            return;
        }
        
        try {
            List<KeyInfo> allKeys = jsonKeyStorageService.loadAllKeys();
            if (allKeys != null) {
                for (KeyInfo key : allKeys) {
                    keyStore.put(key.getKeyId(), key);
                    
                    String userId = key.getUserId();
                    if (userId != null) {
                        userKeys.computeIfAbsent(userId, k -> new ArrayList<>()).add(key.getKeyId());
                    }
                    
                    String sceneGroupId = key.getSceneGroupId();
                    if (sceneGroupId != null) {
                        sceneKeys.computeIfAbsent(sceneGroupId, k -> new ArrayList<>()).add(key.getKeyId());
                    }
                }
                log.info("[loadAllKeys] Loaded {} keys from storage", keyStore.size());
            }
        } catch (Exception e) {
            log.error("[loadAllKeys] Failed to load keys: {}", e.getMessage());
        }
    }
    
    @Override
    public KeyInfo generateKey(KeyGenerateRequest request) {
        // ... existing code for generating key ...
        
        keyStore.put(keyId, keyInfo);
        
        userKeys.computeIfAbsent(request.getUserId(), k -> new ArrayList<>()).add(keyId);
        if (request.getSceneGroupId() != null) {
            sceneKeys.computeIfAbsent(request.getSceneGroupId(), k -> new ArrayList<>()).add(keyId);
        }
        
        if (jsonKeyStorageService != null) {
            jsonKeyStorageService.saveKey(keyInfo);
        }
        
        log.info("[generateKey] Key generated: {} for user: {}", keyId, request.getUserId());
        
        return result;
    }
    
    @Override
    public KeyInfo getKey(String keyId) {
        KeyInfo keyInfo = keyStore.get(keyId);
        if (keyInfo == null) {
            return null;
        }
        
        if (keyInfo.isExpired() && keyInfo.getStatus() == KeyInfo.KeyStatus.ACTIVE) {
            keyInfo.setStatus(KeyInfo.KeyStatus.EXPIRED);
            if (jsonKeyStorageService != null) {
                jsonKeyStorageService.saveKey(keyInfo);
            }
            log.info("[getKey] Key expired: {}", keyId);
        }
        
        return keyInfo;
    }
    
    @Override
    public boolean revokeKey(String keyId) {
        KeyInfo keyInfo = keyStore.get(keyId);
        if (keyInfo == null) {
            log.warn("[revokeKey] Key not found: {}", keyId);
            return false;
        }
        
        keyInfo.setStatus(KeyInfo.KeyStatus.REVOKED);
        
        if (jsonKeyStorageService != null) {
            jsonKeyStorageService.saveKey(keyInfo);
        }
        
        log.info("[revokeKey] Key revoked: {}", keyId);
        return true;
    }
    
    @Override
    public KeyInfo refreshKey(String keyId) {
        KeyInfo keyInfo = keyStore.get(keyId);
        if (keyInfo == null) {
            log.warn("[refreshKey] Key not found: {}", keyId);
            return null;
        }
        
        if (keyInfo.getStatus() == KeyInfo.KeyStatus.REVOKED) {
            log.warn("[refreshKey] Cannot refresh revoked key: {}", keyId);
            return null;
        }
        
        keyInfo.setExpireTime(System.currentTimeMillis() + DEFAULT_EXPIRE_MS);
        keyInfo.setStatus(KeyInfo.KeyStatus.ACTIVE);
        
        if (jsonKeyStorageService != null) {
            jsonKeyStorageService.saveKey(keyInfo);
        }
        
        log.info("[refreshKey] Key refreshed: {}", keyId);
        return keyInfo;
    }
    
    // ... other methods ...
}
```

### 4.2 方案二：使用 JsonStorageService（通用）

如果 `JsonKeyStorageService` 不可用，可使用通用的 `JsonStorageService`：

```java
@Service
public class KeyManagementServiceImpl implements KeyManagementService {

    private static final String KEYS_STORAGE_KEY = "key-management-keys";
    
    @Autowired(required = false)
    private JsonStorageService jsonStorageService;
    
    @PostConstruct
    public void init() {
        loadFromStorage();
    }
    
    @PreDestroy
    public void shutdown() {
        persistToStorage();
    }
    
    private void loadFromStorage() {
        if (jsonStorageService == null) {
            log.info("[loadFromStorage] JsonStorageService not available");
            return;
        }
        
        try {
            List<KeyInfo> storedKeys = jsonStorageService.loadList(KEYS_STORAGE_KEY, KeyInfo.class);
            if (storedKeys != null) {
                for (KeyInfo key : storedKeys) {
                    keyStore.put(key.getKeyId(), key);
                }
                log.info("[loadFromStorage] Loaded {} keys", keyStore.size());
            }
        } catch (Exception e) {
            log.error("[loadFromStorage] Failed: {}", e.getMessage());
        }
    }
    
    private void persistToStorage() {
        if (jsonStorageService == null) return;
        
        try {
            jsonStorageService.saveList(KEYS_STORAGE_KEY, new ArrayList<>(keyStore.values()));
            log.info("[persistToStorage] Saved {} keys", keyStore.size());
        } catch (Exception e) {
            log.error("[persistToStorage] Failed: {}", e.getMessage());
        }
    }
}
```

---

## 五、KeyInfo 模型扩展

### 5.1 当前模型

```java
public static class KeyInfo {
    private String keyId;
    private String keyValue;
    private String userId;
    private String sceneGroupId;
    private String installId;
    private String scope;
    private KeyStatus status;
    private long createTime;
    private long expireTime;
    private long lastAccessTime;
    private int accessCount;
    private Map<String, Object> permissions;
    private String description;
}
```

### 5.2 建议扩展字段

```java
public static class KeyInfo {
    // ... existing fields ...
    
    private String keyName;              // 密钥名称（前端显示）
    private String keyType;              // 密钥类型（LLM_API_KEY, CLOUD_API_KEY 等）
    private String provider;             // 服务提供商（OpenAI, Aliyun 等）
    private List<String> allowedUsers;   // 授权用户列表
    private List<String> allowedRoles;   // 授权角色列表
    private List<String> allowedScenes;  // 授权场景列表
    private String issuerId;             // 签发者ID
    private long issuedAt;               // 签发时间
    private long updatedAt;              // 更新时间
}
```

---

## 六、验证方式

### 6.1 集成测试

1. 启动 MVP 服务
2. 访问 key-management.html 页面
3. 创建一个新密钥
4. 重启 MVP 服务
5. 再次访问页面，确认密钥仍然存在

### 6.2 API 测试

```bash
# 创建密钥
curl -X POST http://localhost:8084/api/v1/keys \
  -H "Content-Type: application/json" \
  -d '{"keyName":"Test Key","keyType":"LLM_API_KEY","rawValue":"sk-test-123"}'

# 重启服务后验证
curl http://localhost:8084/api/v1/keys
```

---

## 七、相关文件

| 文件 | 路径 | 说明 |
|------|------|------|
| KeyManagementServiceImpl | `src/main/java/net/ooder/mvp/skill/scene/capability/service/impl/KeyManagementServiceImpl.java` | 需要改造 |
| KeyManagementService | `src/main/java/net/ooder/mvp/skill/scene/capability/service/KeyManagementService.java` | 接口定义 |
| KeyManagementController | `src/main/java/net/ooder/mvp/skill/scene/capability/controller/KeyManagementController.java` | REST API |
| JsonKeyStorageService | SE SDK 2.3.1 | SE 提供的持久化服务 |
| KeyManagementAutoConfiguration | SE SDK 2.3.1 | SE 提供的自动配置 |

---

## 八、优先级说明

| 优先级 | 说明 |
|--------|------|
| P0 | 核心功能，必须完成，阻塞发布 |
| P1 | 重要功能，建议完成 |
| P2 | 增强功能，可后续迭代 |
| P3 | 扩展功能，按需实现 |

---

## 九、预期交付

### 9.1 核心交付物

- [ ] KeyManagementServiceImpl 集成 JsonKeyStorageService
- [ ] 密钥持久化功能正常
- [ ] 集成测试验证

### 9.2 建议交付物

- [ ] KeyInfo 模型扩展
- [ ] API 字段映射完善
- [ ] 前端字段适配

---

**创建时间**: 2026-03-21  
**状态**: 待处理  
**指派**: SE 团队  
**优先级**: P0（核心功能）
