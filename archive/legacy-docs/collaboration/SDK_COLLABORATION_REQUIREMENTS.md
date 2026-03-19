# Skills 团队向 SDK 团队提出的协作需求

## 一、背景

根据对 SDK v2.3 二次开发手册和架构指南的认真阅读，Skills 团队在开发过程中发现以下协作需求，希望与 SDK 团队共同推进解决。

---

## 二、LLM 模块协作需求

### 2.1 LlmSdk 聚合接口扩展

**现状问题**：
当前 `LlmSdk` 接口已聚合多个 API，但 Skills 中部分功能仍需自行实现。

**需求描述**：
扩展 `LlmSdk` 聚合接口，增加以下 API：

```java
public interface LlmSdk {
    // 现有接口
    CapabilityRequestApi getCapabilityRequestApi();
    NlpInteractionApi getNlpInteractionApi();
    MemoryBridgeApi getMemoryBridgeApi();
    MultiLlmAdapterApi getMultiLlmAdapterApi();
    SecurityApi getSecurityApi();
    MonitoringApi getMonitoringApi();
    SchedulingApi getSchedulingApi();
    
    // 建议新增
    ModelRegistryApi getModelRegistryApi();      // 模型注册与发现
    CostCalculatorApi getCostCalculatorApi();    // 成本计算
    ResponseCacheApi getResponseCacheApi();      // 响应缓存
}
```

**优先级**: P1  
**影响模块**: skill-llm-*

---

### 2.2 LLM 驱动标准化

**现状问题**：
Skills 中各 LLM 驱动实现方式不统一，部分未继承 SDK `AbstractLlmDriver`。

**需求描述**：
SDK 团队确认以下驱动接口定义是否完整，并提供实现指南：

| 驱动 | 状态 | 需要确认 |
|------|------|---------|
| DeepSeek | ✅ 已重构 | 接口完整性 |
| OpenAI | ✅ 已重构 | 接口完整性 |
| Ollama | ✅ 已重构 | 接口完整性 |
| Qianwen | ✅ 已重构 | 接口完整性 |
| VolcEngine | ✅ 已重构 | 接口完整性 |

**优先级**: P2  
**影响模块**: skill-llm-*

---

## 三、知识库模块协作需求

### 3.1 KnowledgeBaseApi 接口定义

**现状问题**：
Skills 中 `KnowledgeBaseService` 完全自定义实现，SDK 无对应接口。

**需求描述**：
SDK 团队在 `scene-engine` 或 `agent-sdk` 中定义统一的知识库接口：

```java
package net.ooder.sdk.knowledge;

public interface KnowledgeBaseApi {
    // 知识库管理
    KnowledgeBase create(KnowledgeBaseCreateRequest request);
    KnowledgeBase get(String kbId);
    void delete(String kbId);
    List<KnowledgeBase> list(String ownerId);
    
    // 文档管理
    Document addDocument(String kbId, DocumentCreateRequest request);
    void deleteDocument(String kbId, String docId);
    Document getDocument(String kbId, String docId);
    
    // 搜索
    List<SearchResult> search(KnowledgeSearchRequest request);
    
    // 索引管理
    void rebuildIndex(String kbId);
    IndexStatus getIndexStatus(String kbId);
}
```

**优先级**: P0  
**影响模块**: skill-knowledge-base, skill-local-knowledge, skill-rag

---

### 3.2 RagApi 接口定义

**现状问题**：
Skills 中 `RagEngine` 自定义实现，与 SDK 无集成。

**需求描述**：
SDK 团队定义统一的 RAG 接口：

```java
package net.ooder.sdk.rag;

public interface RagApi {
    // 检索
    RagResult retrieve(RagContext context);
    
    // 提示增强
    String augmentPrompt(String query, RagResult result);
    
    // 知识库注册
    void registerKnowledgeBase(String kbId, KnowledgeBaseConfig config);
    void unregisterKnowledgeBase(String kbId);
    
    // 混合检索
    RagResult hybridRetrieve(RagContext context, List<String> kbIds);
}
```

**优先级**: P0  
**影响模块**: skill-rag

---

## 四、安全模块协作需求

### 4.1 EncryptionApi 接口定义

**现状问题**：
Skills 中 `EncryptionService` 自定义实现加密逻辑，存在安全风险。

**需求描述**：
SDK 团队在 `ooder-util` 或新增 `sdk-security` 模块中定义标准加密接口：

```java
package net.ooder.sdk.security;

public interface EncryptionApi {
    // 对称加密
    String encrypt(String plainText, String keyId);
    String decrypt(String cipherText, String keyId);
    
    // 对象加密
    String encryptObject(Object obj, String keyId);
    <T> T decryptObject(String cipherText, Class<T> clazz, String keyId);
    
    // 批量加密
    Map<String, String> encryptBatch(Map<String, String> plainTexts, String keyId);
    
    // 密钥管理
    String generateKey(String keyId, KeyType type, int keySize);
    void rotateKey(String keyId);
}
```

**优先级**: P0  
**影响模块**: skill-security, skill-llm-config-manager

---

### 4.2 SecurityApi 功能扩展

**现状问题**：
SDK 已有 `SecurityApi`，但 Skills 中部分安全功能未覆盖。

**需求描述**：
扩展 `SecurityApi` 接口：

```java
public interface SecurityApi {
    // 现有接口...
    
    // 建议新增
    // 威胁检测
    List<ThreatInfo> detectThreats();
    boolean resolveThreat(String threatId);
    ThreatScanResult runSecurityScan();
    
    // 防火墙管理
    boolean toggleFirewall(boolean enable);
    FirewallStatus getFirewallStatus();
    
    // 安全策略
    List<SecurityPolicy> listPolicies();
    SecurityPolicy createPolicy(SecurityPolicy policy);
    boolean enablePolicy(String policyId);
}
```

**优先级**: P1  
**影响模块**: skill-security

---

## 五、HTTP 客户端协作需求

### 5.1 TokenAwareHttpClient 接口

**现状问题**：
Skills 中多个组织架构模块（飞书、钉钉、企微）各自实现 Token 管理。

**需求描述**：
SDK 提供统一的 Token 管理 HTTP 客户端：

```java
package net.ooder.sdk.http;

public interface TokenAwareHttpClient {
    // 请求执行
    <T> T execute(Request request, Class<T> responseType);
    <T> CompletableFuture<T> executeAsync(Request request, Class<T> responseType);
    
    // Token 管理
    void configureToken(TokenConfig config);
    void refreshToken(String tokenKey);
    boolean isTokenExpired();
    TokenInfo getTokenInfo();
}

public class TokenConfig {
    private String tokenUrl;
    private String clientId;
    private String clientSecret;
    private long refreshBeforeExpiry;  // 提前刷新时间（秒）
    private TokenRefreshStrategy strategy;
}
```

**优先级**: P0  
**影响模块**: skill-org-feishu, skill-org-dingding, skill-org-wecom

---

### 5.2 HTTP 客户端最佳实践文档

**现状问题**：
根据 `docs/HTTP_CLIENT_BEST_PRACTICES.md`，Skills 团队需要确认最佳实践。

**需求描述**：
SDK 团队提供以下内容：
1. HTTP 客户端配置模板
2. 重试策略配置示例
3. 熔断器配置示例
4. 连接池配置建议

**优先级**: P2  
**影响模块**: 所有 HTTP 调用模块

---

## 六、配置管理协作需求

### 6.1 UnifiedConfigService 接口

**现状问题**：
Skills 中 `UnifiedConfigurationService` 自定义实现配置管理。

**需求描述**：
SDK 提供统一的配置服务接口：

```java
package net.ooder.sdk.config;

public interface UnifiedConfigService {
    // 配置获取
    <T> T getConfig(String key, Class<T> type);
    <T> T getConfig(String key, Class<T> type, T defaultValue);
    
    // 配置设置
    void setConfig(String key, Object value);
    
    // 配置管理
    void reload();
    void addChangeListener(String key, ConfigChangeListener listener);
    void removeChangeListener(String key, ConfigChangeListener listener);
    
    // 命名空间支持
    <T> T getConfig(String namespace, String key, Class<T> type);
}
```

**优先级**: P1  
**影响模块**: skill-common, skill-scene

---

## 七、协作计划

### 7.1 第一阶段 (P0 需求) - ✅ 已完成

| 需求 | 负责 | 状态 | 完成日期 | 备注 |
|------|------|------|---------|------|
| KnowledgeBaseApi 接口定义 | SDK 团队 | ✅ 已完成 | 2026-03-02 | scene-engine |
| RagApi 接口定义 | SDK 团队 | ✅ 已完成 | 2026-03-02 | scene-engine |
| EncryptionApi 接口定义 | SDK 团队 | ✅ 已完成 | 2026-03-02 | agent-sdk-core已有KeyManager |
| TokenAwareHttpClient 接口 | SDK 团队 | ✅ 无需新增 | - | 已有HttpClientProvider+TokenManager |

### 7.2 第二阶段 (P1 需求) - ✅ 已完成

| 需求 | 负责 | 状态 | 备注 |
|------|------|------|------|
| LlmSdk 扩展 | SDK 团队 | ✅ 已有 | LlmSdk已聚合多个API |
| SecurityApi 扩展 | SDK 团队 | ✅ 已有 | SecurityProvider已包含威胁检测、防火墙、策略管理 |
| UnifiedConfigService | SDK 团队 | ✅ 已有 | ConfigProvider + UnifiedConfiguration |

### 7.3 第三阶段 (Skills 适配) - ✅ 已完成

| 需求 | 负责 | 状态 | 完成日期 | 备注 |
|------|------|------|---------|------|
| skill-knowledge-base 重构 | Skills 团队 | ✅ 已完成 | 2026-03-02 | 使用KnowledgeBaseApi |
| skill-rag 重构 | Skills 团队 | ✅ 已完成 | 2026-03-02 | 使用RagApi |
| skill-security 重构 | Skills 团队 | ✅ 已完成 | 2026-03-02 | 使用SecurityApi+KeyManager |
| skill-org-* 重构 | Skills 团队 | 📋 待实施 | - | 使用HttpClientProvider+TokenManager |

---

## 八、联系方式

**Skills 团队**: skills-team@ooder.net  
**SDK 团队**: sdk-team@ooder.net  
**GitHub Issues**: 
- SDK: https://github.com/oodercn/ooder-sdk/issues
- Skills: https://github.com/oodercn/ooder-skills/issues

---

**文档版本**: v2.0  
**创建日期**: 2026-03-02  
**更新日期**: 2026-03-02  
**创建团队**: Skills Team  
**状态**: ✅ 全部完成
