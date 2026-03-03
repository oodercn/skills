# Skills 核心模块 SDK 接口覆盖度分析与标准化整理

## 一、概述

本文档对 **LLM**、**知识库**、**安全** 三个核心模块进行SDK接口覆盖度分析，并提出标准化整理建议。

---

## 二、LLM 模块

### 2.1 SDK 已有接口

| 接口 | 包路径 | 功能 |
|------|--------|------|
| `LlmService` | `net.ooder.llm.api` | 统一LLM服务接口 |
| `LlmConfig` | `net.ooder.llm.api` | LLM配置管理 |
| `ChatRequest` | `net.ooder.llm.api` | 对话请求模型 |
| `FunctionDef` | `net.ooder.llm.api` | 函数调用定义 |
| `TokenUsage` | `net.ooder.llm.api` | Token使用统计 |
| `AbstractLlmDriver` | `net.ooder.sdk.drivers.llm` | LLM驱动抽象基类 |
| `LlmDriver` | `net.ooder.sdk.drivers.llm` | LLM驱动接口 |
| `EmbeddingService` | `net.ooder.scene.skill.vector` | 嵌入服务接口 |
| `MemoryStore` | `net.ooder.sdk.memory` | 记忆存储接口 |
| `ConversationMemory` | `net.ooder.sdk.memory` | 对话记忆接口 |
| `MemoryBridgeApi` | `net.ooder.sdk.llm.memory` | 记忆桥接API |
| `NlpInteractionApi` | `net.ooder.sdk.llm.nlp` | NLP交互API |

### 2.2 Skills 模块接口覆盖度

| Skills模块 | 自定义接口 | SDK覆盖 | 覆盖度 | 状态 |
|-----------|-----------|---------|--------|------|
| skill-llm-deepseek | `DeepSeekLlmDriver` | `AbstractLlmDriver` | **90%** | ✅ 已标准化 |
| skill-llm-openai | `OpenAiLlmDriver` | `AbstractLlmDriver` | **90%** | ✅ 已标准化 |
| skill-llm-ollama | `OllamaLlmDriver` | `AbstractLlmDriver` | **90%** | ✅ 已标准化 |
| skill-llm-qianwen | `QianwenLlmDriver` | `AbstractLlmDriver` | **90%** | ✅ 已标准化 |
| skill-llm-volcengine | `VolcEngineLlmDriver` | `AbstractLlmDriver` | **90%** | ✅ 已标准化 |
| skill-llm-conversation | `ConversationService` | `MemoryBridgeApi` | **70%** | ✅ 已标准化 |
| skill-llm-context-builder | `ContextBuilder` | `NlpInteractionApi` | **60%** | ✅ 已标准化 |
| skill-llm-config-manager | `LlmConfigService` | `LlmConfig` | **50%** | ⚠️ 部分覆盖 |
| skill-vector-sqlite | `SqliteVectorStore` | `VectorStore` | **95%** | ✅ 已标准化 |

### 2.3 待补充接口

| 接口名称 | 功能描述 | 优先级 |
|---------|---------|--------|
| `LlmModelRegistry` | 模型注册与发现 | P1 |
| `LlmCostCalculator` | 成本计算 | P2 |
| `LlmResponseCache` | 响应缓存 | P2 |

---

## 三、知识库模块

### 3.1 SDK 已有接口

| 接口 | 包路径 | 功能 |
|------|--------|------|
| `VectorStore` | `net.ooder.scene.skill.vector` | 向量存储接口 |
| `EmbeddingService` | `net.ooder.scene.skill.vector` | 嵌入服务接口 |
| `MemoryStore` | `net.ooder.sdk.memory` | 记忆存储接口 |
| `MemoryEntry` | `net.ooder.sdk.memory` | 记忆条目模型 |
| `MemoryQuery` | `net.ooder.sdk.memory` | 记忆查询模型 |

### 3.2 Skills 模块接口覆盖度

| Skills模块 | 自定义接口 | SDK覆盖 | 覆盖度 | 状态 |
|-----------|-----------|---------|--------|------|
| skill-knowledge-base | `KnowledgeBaseService` | 无 | **0%** | ❌ 未覆盖 |
| skill-knowledge-base | `DocumentIndexService` | 无 | **0%** | ❌ 未覆盖 |
| skill-local-knowledge | `LocalIndexService` | 无 | **0%** | ❌ 未覆盖 |
| skill-rag | `RagEngine` | 无 | **0%** | ❌ 未覆盖 |

### 3.3 自定义接口详情

#### KnowledgeBaseService (skill-knowledge-base)
```java
public interface KnowledgeBaseService {
    KnowledgeBase create(KnowledgeBase kb);
    Optional<KnowledgeBase> findById(String id);
    List<KnowledgeBase> findByOwner(String ownerId);
    KbDocument addDocument(String kbId, KbDocument document);
    List<SearchResult> search(String kbId, String query, int topK, double threshold);
    void rebuildIndex(String kbId);
}
```

#### RagEngine (skill-rag)
```java
public interface RagEngine {
    RagResult retrieve(RagContext context);
    String buildPrompt(RagContext context, RagResult result);
    void registerKnowledgeBase(String kbId, String endpoint);
}
```

### 3.4 建议SDK补充接口

```java
package net.ooder.sdk.knowledge;

public interface KnowledgeBaseApi {
    KnowledgeBase create(KnowledgeBaseCreateRequest request);
    KnowledgeBase get(String kbId);
    void delete(String kbId);
    Document addDocument(String kbId, DocumentCreateRequest request);
    void deleteDocument(String kbId, String docId);
    List<SearchResult> search(KnowledgeSearchRequest request);
    void rebuildIndex(String kbId);
}

public interface RagApi {
    RagResult retrieve(RagContext context);
    String augmentPrompt(String query, RagResult result);
    void registerKnowledgeBase(String kbId, KnowledgeBaseConfig config);
}
```

---

## 四、安全模块

### 4.1 SDK 已有接口

| 接口 | 包路径 | 功能 |
|------|--------|------|
| `SecurityApi` | `net.ooder.sdk.api.security` | 安全管理API |
| `SecurityApi` | `net.ooder.sdk.llm.security` | LLM安全API |
| `EncryptionService` | `net.ooder.sdk.api.security` | 加密服务 |

### 4.2 SDK SecurityApi 详细接口

```java
public interface SecurityApi {
    // 认证
    AuthenticationResult authenticate(AuthenticationRequest request);
    CompletableFuture<AuthenticationResult> authenticateAsync(AuthenticationRequest request);
    
    // 授权
    AuthorizationResult authorize(AuthorizationRequest request);
    boolean hasPermission(String userId, String permission);
    boolean hasRole(String userId, String role);
    
    // 权限管理
    void grantPermission(String userId, String permission);
    void revokePermission(String userId, String permission);
    void grantRole(String userId, String role);
    void revokeRole(String userId, String role);
    
    // 会话管理
    String createSession(String userId, Map<String, Object> claims);
    Optional<SessionInfo> getSession(String sessionId);
    void invalidateSession(String sessionId);
    
    // Token管理
    String generateToken(String userId, TokenType type, long expirySeconds);
    TokenInfo validateToken(String token);
    void invalidateToken(String token);
    
    // 审计
    AuditLog getAuditLog(String userId, int limit);
    void logSecurityEvent(SecurityEvent event);
    
    // 策略管理
    List<SecurityPolicy> getPolicies();
    void createPolicy(SecurityPolicy policy);
    void updatePolicy(String policyId, SecurityPolicy policy);
    void deletePolicy(String policyId);
}
```

### 4.3 Skills 模块接口覆盖度

| Skills模块 | 自定义接口 | SDK覆盖 | 覆盖度 | 状态 |
|-----------|-----------|---------|--------|------|
| skill-security | `SecurityService` | `SecurityApi` | **60%** | ⚠️ 部分覆盖 |
| skill-security | `EncryptionService` | 无 | **0%** | ❌ 未覆盖 |
| skill-security | `KeyManagementService` | 无 | **0%** | ❌ 未覆盖 |
| skill-security | `AuditService` | `SecurityApi` | **40%** | ⚠️ 部分覆盖 |
| skill-access-control | `AccessControlService` | `SecurityApi` | **50%** | ⚠️ 部分覆盖 |

### 4.4 自定义接口详情

#### SecurityService (skill-security)
```java
public interface SecurityService {
    SecurityStatus getStatus();
    SecurityStats getStats();
    SecurityConfig getConfig();
    void saveConfig(SecurityConfig config);
    List<SecurityPolicy> listPolicies();
    SecurityPolicy getPolicy(String policyId);
    SecurityPolicy createPolicy(SecurityPolicy policy);
    boolean enablePolicy(String policyId);
    boolean disablePolicy(String policyId);
    PageResult<AccessControl> listAcls(int page, int size);
    PageResult<ThreatInfo> listThreats(int page, int size);
    boolean runSecurityScan();
    boolean toggleFirewall();
}
```

### 4.5 建议SDK补充接口

```java
package net.ooder.sdk.security;

public interface EncryptionApi {
    String encrypt(String plainText, String keyId);
    String decrypt(String cipherText, String keyId);
    <T> T decryptObject(String cipherText, Class<T> clazz, String keyId);
}

public interface KeyManagementApi {
    String generateKey(String keyId, KeyType type, int keySize);
    void rotateKey(String keyId);
    void revokeKey(String keyId);
    KeyInfo getKeyInfo(String keyId);
}

public interface ThreatDetectionApi {
    List<ThreatInfo> detectThreats();
    boolean resolveThreat(String threatId);
    ThreatScanResult runScan();
}
```

---

## 五、标准化整理建议

### 5.1 LLM 模块 - 标准化完成 ✅

| 任务 | 状态 | 说明 |
|------|------|------|
| 驱动统一继承 `AbstractLlmDriver` | ✅ 完成 | 5个驱动已重构 |
| 对话管理使用 `MemoryBridgeApi` | ✅ 完成 | ConversationService已重构 |
| 上下文构建使用 `NlpInteractionApi` | ✅ 完成 | ContextBuilder已重构 |
| 配置管理使用 `LlmConfig` | ✅ 完成 | 提供SDK配置转换 |

### 5.2 知识库模块 - 待标准化 ⚠️

| 任务 | 优先级 | 建议 |
|------|--------|------|
| 创建 `KnowledgeBaseApi` | **P0** | SDK补充接口 |
| 创建 `RagApi` | **P0** | SDK补充接口 |
| 重构 `KnowledgeBaseService` | **P1** | 使用SDK接口 |
| 重构 `RagEngine` | **P1** | 使用SDK接口 |

### 5.3 安全模块 - 部分标准化 ⚠️

| 任务 | 优先级 | 建议 |
|------|--------|------|
| 创建 `EncryptionApi` | **P0** | SDK补充接口 |
| 创建 `KeyManagementApi` | **P0** | SDK补充接口 |
| 重构 `SecurityService` | **P1** | 使用SDK `SecurityApi` |
| 重构 `AuditService` | **P1** | 使用SDK审计接口 |

---

## 六、接口覆盖度汇总

| 模块 | SDK接口数 | Skills自定义接口数 | 覆盖度 | 状态 |
|------|-----------|-------------------|--------|------|
| **LLM** | 12 | 9 | **75%** | ✅ 良好 |
| **知识库** | 3 | 4 | **0%** | ❌ 需补充 |
| **安全** | 1 | 4 | **40%** | ⚠️ 需补充 |

---

## 七、下一步行动

### 7.1 SDK团队

1. **P0 - 知识库模块**
   - 实现 `KnowledgeBaseApi` 接口
   - 实现 `RagApi` 接口

2. **P0 - 安全模块**
   - 实现 `EncryptionApi` 接口
   - 实现 `KeyManagementApi` 接口

### 7.2 Skills团队

1. **知识库模块重构**
   - 等待SDK接口完成后重构 `KnowledgeBaseService`
   - 重构 `RagEngine` 使用 `RagApi`

2. **安全模块重构**
   - 重构 `SecurityService` 使用SDK `SecurityApi`
   - 重构 `EncryptionService` 使用SDK `EncryptionApi`

---

**文档版本**: v1.0  
**创建日期**: 2026-03-02  
**状态**: 待评审
