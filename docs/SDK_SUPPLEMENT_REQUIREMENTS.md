# SDK 2.4 补充需求文档

## 一、背景

通过对 `ooder-skills` 项目中所有模块的分析，发现以下功能点在应用层重复实现，建议SDK团队补充相应接口以提高代码复用率和SDK利用率。

---

## 二、急需补充的核心接口

### 1. 安全模块 (sdk-security)

| 接口名称 | 功能描述 | 优先级 | 影响模块 |
|---------|---------|--------|---------|
| `EncryptionService` | 标准加密/解密服务，支持AES、RSA等算法 | **P0** | skill-security |
| `KeyManagementService` | 密钥生命周期管理（生成、存储、轮换、销毁） | **P0** | skill-security |
| `AuditLogService` | 审计日志统一接口，支持结构化日志记录 | **P1** | skill-security, skill-audit |
| `SecureConfigService` | 敏感配置加密存储 | **P1** | skill-llm-config-manager |

**建议接口定义：**

```java
package net.ooder.sdk.security;

public interface EncryptionService {
    String encrypt(String plainText, String keyId);
    String decrypt(String cipherText, String keyId);
    String encryptObject(Object obj, String keyId);
    <T> T decryptObject(String cipherText, Class<T> clazz, String keyId);
}

public interface KeyManagementService {
    String generateKey(String keyId, KeyType type, int keySize);
    void rotateKey(String keyId);
    void revokeKey(String keyId);
    KeyInfo getKeyInfo(String keyId);
}

public interface AuditLogService {
    void log(String action, String resource, String userId, Map<String, Object> details);
    void logSecurityEvent(String eventType, String userId, boolean success, String details);
    List<AuditLog> query(AuditQuery query);
}
```

---

### 2. HTTP客户端增强 (sdk-http)

| 接口名称 | 功能描述 | 优先级 | 影响模块 |
|---------|---------|--------|---------|
| `TokenAwareHttpClient` | 自动Token管理的HTTP客户端 | **P0** | skill-org-feishu, skill-org-dingding, skill-org-wecom |
| `CircuitBreaker` | 熔断器模式实现 | **P1** | 所有HTTP调用模块 |
| `RetryPolicy` | 统一的重试策略 | **P1** | 所有HTTP调用模块 |
| `ResponseParser` | 统一响应解析工具 | **P2** | 所有HTTP调用模块 |

**建议接口定义：**

```java
package net.ooder.sdk.http;

public interface TokenAwareHttpClient {
    <T> T execute(Request request, Class<T> responseType);
    <T> CompletableFuture<T> executeAsync(Request request, Class<T> responseType);
    void refreshToken(String tokenKey);
    boolean isTokenExpired();
}

public interface CircuitBreaker {
    <T> T execute(Supplier<T> supplier);
    void recordSuccess();
    void recordFailure();
    CircuitState getState();
}

public interface RetryPolicy {
    <T> T executeWithRetry(Supplier<T> supplier);
    int getMaxRetries();
    long getRetryInterval();
}
```

---

### 3. 配置管理增强 (sdk-config)

| 接口名称 | 功能描述 | 优先级 | 影响模块 |
|---------|---------|--------|---------|
| `UnifiedConfigService` | 统一配置服务接口 | **P0** | skill-common, skill-scene |
| `ConfigHotReloader` | 配置热更新机制 | **P1** | skill-scene |
| `MultiSourceConfigProvider` | 多配置源支持（文件、环境变量、远程） | **P1** | skill-common |

**建议接口定义：**

```java
package net.ooder.sdk.config;

public interface UnifiedConfigService {
    <T> T getConfig(String key, Class<T> type);
    <T> T getConfig(String key, Class<T> type, T defaultValue);
    void setConfig(String key, Object value);
    void reload();
    void addChangeListener(String key, ConfigChangeListener listener);
}

public interface ConfigHotReloader {
    void startWatching();
    void stopWatching();
    void registerCallback(String configKey, Runnable callback);
}
```

---

### 4. 本地存储服务 (sdk-storage)

| 接口名称 | 功能描述 | 优先级 | 影响模块 |
|---------|---------|--------|---------|
| `LocalStorageService` | 本地文件存储服务 | **P1** | skill-scene, skill-knowledge-base |
| `IndexService` | 本地索引服务 | **P1** | skill-local-knowledge, skill-vector-sqlite |
| `CacheService` | 统一缓存服务接口 | **P0** | skill-scene, skill-common |

**建议接口定义：**

```java
package net.ooder.sdk.storage;

public interface LocalStorageService {
    void save(String key, Object data);
    <T> T load(String key, Class<T> type);
    void delete(String key);
    boolean exists(String key);
    List<String> listKeys(String prefix);
}

public interface CacheService {
    void put(String key, Object value, long ttl);
    <T> T get(String key, Class<T> type);
    void invalidate(String key);
    void invalidateByPrefix(String prefix);
}
```

---

### 5. 组织架构集成 (sdk-org-integration)

| 接口名称 | 功能描述 | 优先级 | 影响模块 |
|---------|---------|--------|---------|
| `OrgIntegrationService` | 统一的第三方组织架构接入接口 | **P0** | skill-org-feishu, skill-org-dingding, skill-org-wecom |
| `OrgUserSyncService` | 用户同步服务 | **P1** | skill-org-* |
| `OrgDeptSyncService` | 部门同步服务 | **P1** | skill-org-* |

**建议接口定义：**

```java
package net.ooder.sdk.org;

public interface OrgIntegrationService {
    List<OrgUser> syncUsers(String orgId);
    List<OrgDepartment> syncDepartments(String orgId);
    OrgUser getUser(String orgId, String userId);
    OrgDepartment getDepartment(String orgId, String deptId);
    boolean validateToken(String orgId, String token);
}

public interface OrgUserSyncService {
    SyncResult syncFromSource(String sourceType, Map<String, Object> config);
    SyncResult incrementalSync(String sourceType, long lastSyncTime);
}
```

---

## 三、现有SDK接口扩展建议

### 1. LlmSdk 扩展

```java
public interface LlmSdk {
    // 现有接口...
    
    // 建议新增
    SecurityApi getSecurityApi();
    ConfigApi getConfigApi();
    StorageApi getStorageApi();
    HttpApi getHttpApi();
}
```

### 2. VectorStore 扩展

```java
public interface VectorStore {
    // 现有接口...
    
    // 建议新增
    void createIndex(String indexName, int dimension);
    void dropIndex(String indexName);
    List<String> listIndices();
    VectorStats getStats();
}
```

---

## 四、优先级说明

| 优先级 | 说明 | 建议完成时间 |
|--------|------|-------------|
| **P0** | 核心功能，影响多个模块，急需补充 | 1周内 |
| **P1** | 重要功能，可显著减少重复代码 | 2周内 |
| **P2** | 增强功能，提升开发体验 | 1月内 |

---

## 五、影响评估

### 补充后的收益预估

| 指标 | 当前状态 | 补充后预估 |
|------|---------|-----------|
| SDK利用率 | 60-70% | **85-95%** |
| 重复代码量 | ~3000行 | **<500行** |
| 维护成本 | 高 | **低** |
| 新模块开发效率 | 中等 | **高** |

### 受影响的模块数量

- 直接受益模块: 15个
- 间接受益模块: 20+个

---

## 六、附录：各模块自定义服务清单

| 模块 | 自定义服务数量 | 主要自定义服务 |
|------|---------------|---------------|
| skill-security | 4 | SecurityService, EncryptionService, KeyManagementService, AuditService |
| skill-scene | 10 | SceneService, SceneGroupService, CapabilityService, TodoService, HistoryService等 |
| skill-knowledge-base | 2 | KnowledgeBaseService, DocumentIndexService |
| skill-local-knowledge | 2 | LocalIndexService, TermMappingService |
| skill-vector-sqlite | 1 | EmbeddingService |
| skill-llm-config-manager | 1 | LlmConfigService |
| skill-org-feishu | 3 | FeishuApiClient, FeishuUserService, FeishuDeptService |
| skill-org-dingding | 3 | DingdingApiClient, DingdingUserService, DingdingDeptService |
| skill-org-wecom | 3 | WeComApiClient, WeComUserService, WeComDeptService |

---

**文档版本**: v1.0  
**创建日期**: 2026-03-02  
**创建人**: AI Assistant  
**状态**: 待SDK团队评审
