# SDK 2.4 补充需求响应文档

## 一、响应概述

本文档为SDK团队对 `ooder-skills` 项目提交的《SDK 2.4 补充需求文档》的正式响应。

**响应状态**: 已评审  
**响应日期**: 2026-03-02  
**响应版本**: v1.0

---

## 二、需求响应明细

### 2.1 安全模块 (sdk-security)

| 接口名称 | 优先级 | 响应状态 | 计划版本 | 备注 |
|---------|--------|---------|---------|------|
| `EncryptionService` | P0 | ✅ 已规划 | 2.4.0 | 支持AES-256-GCM、RSA-2048 |
| `KeyManagementService` | P0 | ✅ 已规划 | 2.4.0 | 集成HSM支持 |
| `AuditLogService` | P1 | ✅ 已规划 | 2.4.1 | 支持结构化日志、异步写入 |
| `SecureConfigService` | P1 | ✅ 已规划 | 2.4.0 | 基于EncryptionService实现 |

**接口定义确认**:

```java
package net.ooder.sdk.security;

public interface EncryptionService {
    String encrypt(String plainText, String keyId);
    String decrypt(String cipherText, String keyId);
    String encryptObject(Object obj, String keyId);
    <T> T decryptObject(String cipherText, Class<T> clazz, String keyId);
    
    // 新增：批量加密
    Map<String, String> encryptBatch(Map<String, String> plainTexts, String keyId);
}

public interface KeyManagementService {
    String generateKey(String keyId, KeyType type, int keySize);
    void rotateKey(String keyId);
    void revokeKey(String keyId);
    KeyInfo getKeyInfo(String keyId);
    
    // 新增：密钥导入导出
    String exportKey(String keyId, String format);
    void importKey(String keyId, String keyData, String format);
}
```

---

### 2.2 HTTP客户端增强 (sdk-http)

| 接口名称 | 优先级 | 响应状态 | 计划版本 | 备注 |
|---------|--------|---------|---------|------|
| `TokenAwareHttpClient` | P0 | ✅ 已规划 | 2.4.0 | 支持OAuth2、自定义Token |
| `CircuitBreaker` | P1 | ✅ 已规划 | 2.4.1 | 基于Resilience4j |
| `RetryPolicy` | P1 | ✅ 已规划 | 2.4.0 | 支持指数退避 |
| `ResponseParser` | P2 | ⏳ 评估中 | 2.5.0 | 需进一步调研 |

**接口定义确认**:

```java
package net.ooder.sdk.http;

public interface TokenAwareHttpClient {
    <T> T execute(Request request, Class<T> responseType);
    <T> CompletableFuture<T> executeAsync(Request request, Class<T> responseType);
    void refreshToken(String tokenKey);
    boolean isTokenExpired();
    
    // 新增：Token配置
    void configureToken(TokenConfig config);
}

public interface RetryPolicy {
    <T> T executeWithRetry(Supplier<T> supplier);
    int getMaxRetries();
    long getRetryInterval();
    
    // 新增：重试条件配置
    void setRetryCondition(Predicate<Exception> condition);
}
```

---

### 2.3 配置管理增强 (sdk-config)

| 接口名称 | 优先级 | 响应状态 | 计划版本 | 备注 |
|---------|--------|---------|---------|------|
| `UnifiedConfigService` | P0 | ✅ 已规划 | 2.4.0 | 统一配置入口 |
| `ConfigHotReloader` | P1 | ✅ 已规划 | 2.4.0 | 基于WatchService |
| `MultiSourceConfigProvider` | P1 | ⏳ 评估中 | 2.4.1 | 需考虑安全性 |

**接口定义确认**:

```java
package net.ooder.sdk.config;

public interface UnifiedConfigService {
    <T> T getConfig(String key, Class<T> type);
    <T> T getConfig(String key, Class<T> type, T defaultValue);
    void setConfig(String key, Object value);
    void reload();
    void addChangeListener(String key, ConfigChangeListener listener);
    
    // 新增：命名空间支持
    <T> T getConfig(String namespace, String key, Class<T> type);
}
```

---

### 2.4 本地存储服务 (sdk-storage)

| 接口名称 | 优先级 | 响应状态 | 计划版本 | 备注 |
|---------|--------|---------|---------|------|
| `LocalStorageService` | P1 | ✅ 已规划 | 2.4.0 | 支持JSON、YAML格式 |
| `IndexService` | P1 | ✅ 已规划 | 2.4.1 | 基于Lucene |
| `CacheService` | P0 | ✅ 已规划 | 2.4.0 | 支持内存、文件缓存 |

**接口定义确认**:

```java
package net.ooder.sdk.storage;

public interface CacheService {
    void put(String key, Object value, long ttl);
    <T> T get(String key, Class<T> type);
    void invalidate(String key);
    void invalidateByPrefix(String prefix);
    
    // 新增：统计信息
    CacheStats getStats();
}
```

---

### 2.5 组织架构集成 (sdk-org-integration)

| 接口名称 | 优先级 | 响应状态 | 计划版本 | 备注 |
|---------|--------|---------|---------|------|
| `OrgIntegrationService` | P0 | ✅ 已规划 | 2.4.0 | 统一接入接口 |
| `OrgUserSyncService` | P1 | ✅ 已规划 | 2.4.0 | 支持增量同步 |
| `OrgDeptSyncService` | P1 | ✅ 已规划 | 2.4.0 | 支持层级同步 |

**接口定义确认**:

```java
package net.ooder.sdk.org;

public interface OrgIntegrationService {
    List<OrgUser> syncUsers(String orgId);
    List<OrgDepartment> syncDepartments(String orgId);
    OrgUser getUser(String orgId, String userId);
    OrgDepartment getDepartment(String orgId, String deptId);
    boolean validateToken(String orgId, String token);
    
    // 新增：事件回调
    void registerSyncCallback(String orgId, SyncCallback callback);
}
```

---

## 三、版本计划

### 3.1 SDK 2.4.0 (计划发布: 2026-03-15)

| 模块 | 接口 | 状态 |
|------|------|------|
| sdk-security | EncryptionService, KeyManagementService, SecureConfigService | 开发中 |
| sdk-http | TokenAwareHttpClient, RetryPolicy | 开发中 |
| sdk-config | UnifiedConfigService, ConfigHotReloader | 开发中 |
| sdk-storage | LocalStorageService, CacheService | 开发中 |
| sdk-org-integration | OrgIntegrationService, OrgUserSyncService, OrgDeptSyncService | 开发中 |

### 3.2 SDK 2.4.1 (计划发布: 2026-03-30)

| 模块 | 接口 | 状态 |
|------|------|------|
| sdk-security | AuditLogService | 规划中 |
| sdk-http | CircuitBreaker | 规划中 |
| sdk-storage | IndexService | 规划中 |

### 3.3 SDK 2.5.0 (计划发布: 2026-04-15)

| 模块 | 接口 | 状态 |
|------|------|------|
| sdk-http | ResponseParser | 评估中 |
| sdk-config | MultiSourceConfigProvider | 评估中 |

---

## 四、现有SDK接口扩展确认

### 4.1 LlmSdk 扩展

```java
public interface LlmSdk {
    // 现有接口
    CapabilityRequestApi getCapabilityRequestApi();
    NlpInteractionApi getNlpInteractionApi();
    MemoryBridgeApi getMemoryBridgeApi();
    MultiLlmAdapterApi getMultiLlmAdapterApi();
    
    // 2.4.0 新增
    SecurityApi getSecurityApi();
    ConfigApi getConfigApi();
    StorageApi getStorageApi();
    HttpApi getHttpApi();
}
```

**响应状态**: ✅ 已确认

### 4.2 VectorStore 扩展

```java
public interface VectorStore {
    // 现有接口
    void insert(String id, float[] vector, Map<String, Object> metadata);
    void batchInsert(List<VectorData> vectors);
    List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters);
    void delete(String id);
    void deleteByMetadata(Map<String, Object> filters);
    int getDimension();
    long count();
    void clear();
    
    // 2.4.1 新增
    void createIndex(String indexName, int dimension);
    void dropIndex(String indexName);
    List<String> listIndices();
    VectorStats getStats();
}
```

**响应状态**: ✅ 已确认

---

## 五、迁移指南

### 5.1 skill-security 迁移

**迁移前**:
```java
// 自定义实现
public class EncryptionServiceImpl implements EncryptionService {
    @Override
    public String encrypt(String plainText) {
        // 自定义XOR加密
    }
}
```

**迁移后**:
```java
// 使用SDK
@Autowired
private EncryptionService encryptionService;

public String encrypt(String plainText) {
    return encryptionService.encrypt(plainText, "default-key");
}
```

### 5.2 skill-org-* 迁移

**迁移前**:
```java
// 各自实现Token管理
public class FeishuApiClient {
    private String accessToken;
    private long tokenExpireTime;
    
    public void refreshToken() {
        // 手动刷新Token
    }
}
```

**迁移后**:
```java
// 使用SDK
@Autowired
private TokenAwareHttpClient httpClient;

public void configure() {
    TokenConfig config = TokenConfig.oauth2(
        "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal",
        appId, appSecret
    );
    httpClient.configureToken(config);
}
```

---

## 六、验收标准

### 6.1 功能验收

- [ ] 所有P0接口实现并通过单元测试
- [ ] 所有P1接口实现并通过单元测试
- [ ] 集成测试覆盖率 > 80%

### 6.2 性能验收

| 指标 | 目标值 |
|------|--------|
| EncryptionService.encrypt() | < 1ms |
| CacheService.get() | < 0.1ms |
| TokenAwareHttpClient.execute() | < 100ms (不含网络) |

### 6.3 兼容性验收

- [ ] JDK 1.8+ 兼容
- [ ] Spring Boot 2.x 兼容
- [ ] 向后兼容现有SDK接口

---

## 七、联系方式

**SDK团队**: sdk-team@ooder.net  
**需求反馈**: https://github.com/oodercn/sdk/issues  
**文档更新**: https://docs.ooder.net/sdk/2.4

---

**文档版本**: v1.0  
**响应日期**: 2026-03-02  
**状态**: 已确认，开发中
