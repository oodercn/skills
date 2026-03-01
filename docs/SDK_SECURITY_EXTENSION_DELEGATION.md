# SDK安全模块扩展委托开发文档

> **版本**: v1.0  
> **日期**: 2026-03-01  
> **委托方**: 安全控制模块开发组  
> **受托方**: SDK核心团队  
> **优先级**: P0

---

## 一、背景与目标

### 1.1 背景

当前SDK安全模块（skill-security）提供了基础的安全能力：
- 安全策略管理（SecurityPolicy）
- 访问控制列表（AccessControl）
- 威胁检测管理（ThreatInfo）
- 防火墙管理（FirewallRule）
- 审计日志（AuditLog）

但缺少以下关键能力：
1. **API密钥管理** - 无密钥生命周期管理能力
2. **加密服务** - 无统一加密服务接口
3. **Agent认证** - 无Agent身份认证机制
4. **场景安全集成** - 无场景级安全钩子
5. **能力安全集成** - 无能力级安全策略

### 1.2 目标

委托SDK核心团队扩展以下能力：
1. 新增 `KeyManagementApi` - 密钥管理API
2. 新增 `EncryptionApi` - 加密服务API
3. 新增 `AgentAuthApi` - Agent认证API
4. 扩展 `SecurityApi` - 增加场景/能力安全集成点

---

## 二、现有SDK安全能力分析

### 2.1 现有API接口

| API接口 | 包路径 | 功能 | 状态 |
|---------|--------|------|------|
| SecurityApi | net.ooder.skill.security.api | 安全策略、ACL、威胁、防火墙 | ✅ 已实现 |
| AuditApi | net.ooder.skill.audit.api | 审计日志 | ✅ 已实现 |
| AccessControlApi | net.ooder.skill.access.control.api | 权限、角色管理 | ✅ 已实现 |

### 2.2 现有API方法清单

#### SecurityApi 方法

| 方法 | 说明 | 是否满足需求 |
|------|------|-------------|
| `getStatus()` | 获取安全状态 | ✅ 满足 |
| `getStats()` | 获取统计信息 | ✅ 满足 |
| `createPolicy()` | 创建策略 | ✅ 满足 |
| `updatePolicy()` | 更新策略 | ✅ 满足 |
| `deletePolicy()` | 删除策略 | ✅ 满足 |
| `getPolicy()` | 获取策略 | ✅ 满足 |
| `listPolicies()` | 列出策略 | ✅ 满足 |
| `enablePolicy()` | 启用策略 | ✅ 满足 |
| `disablePolicy()` | 禁用策略 | ✅ 满足 |
| `createAcl()` | 创建ACL | ✅ 满足 |
| `updateAcl()` | 更新ACL | ✅ 满足 |
| `deleteAcl()` | 删除ACL | ✅ 满足 |
| `getAcl()` | 获取ACL | ✅ 满足 |
| `listAcls()` | 列出ACL | ✅ 满足 |
| `checkPermission()` | 检查权限 | ✅ 满足 |
| `reportThreat()` | 报告威胁 | ✅ 满足 |
| `getThreat()` | 获取威胁 | ✅ 满足 |
| `listThreats()` | 列出威胁 | ✅ 满足 |
| `resolveThreat()` | 解决威胁 | ✅ 满足 |
| `enableFirewall()` | 启用防火墙 | ✅ 满足 |
| `disableFirewall()` | 禁用防火墙 | ✅ 满足 |
| `getFirewallStatus()` | 获取防火墙状态 | ✅ 满足 |
| `addFirewallRule()` | 添加防火墙规则 | ✅ 满足 |
| `removeFirewallRule()` | 移除防火墙规则 | ✅ 满足 |
| `listFirewallRules()` | 列出防火墙规则 | ✅ 满足 |
| `queryAuditLogs()` | 查询审计日志 | ✅ 满足 |
| `getAuditStats()` | 获取审计统计 | ✅ 满足 |

### 2.3 缺失能力分析

| 能力 | 说明 | 影响 |
|------|------|------|
| **密钥管理** | 无API密钥生命周期管理 | LLM API密钥无法安全存储 |
| **加密服务** | 无统一加密接口 | 密钥无法加密存储 |
| **Agent认证** | 无Agent身份认证 | Agent无法安全接入 |
| **场景安全钩子** | 无场景生命周期安全集成 | 场景无法自动配置安全 |
| **能力安全策略** | 无能力级安全策略 | 能力无法细粒度控制 |

---

## 三、SDK扩展需求

### 3.1 新增 KeyManagementApi

#### 接口定义

```java
package net.ooder.skill.security.api;

import net.ooder.skill.security.dto.key.*;
import net.ooder.sdk.infra.utils.Result;

import java.util.List;

/**
 * 密钥管理API
 * 
 * <p>提供API密钥的完整生命周期管理能力</p>
 * 
 * @since 2.4
 */
public interface KeyManagementApi {

    String getApiName();
    String getVersion();
    void initialize(Map<String, Object> context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();

    // ==================== 密钥生命周期 ====================

    /**
     * 创建密钥
     */
    Result<ApiKeyDTO> createKey(KeyCreateRequest request);

    /**
     * 获取密钥（脱敏）
     */
    Result<ApiKeyDTO> getKey(String keyId);

    /**
     * 按名称获取密钥
     */
    Result<ApiKeyDTO> getKeyByName(String keyName);

    /**
     * 列出密钥
     */
    Result<List<ApiKeyDTO>> listKeys(String keyType, String status);

    /**
     * 更新密钥
     */
    Result<ApiKeyDTO> updateKey(String keyId, ApiKeyDTO key);

    /**
     * 删除密钥
     */
    Result<Boolean> deleteKey(String keyId);

    /**
     * 撤销密钥
     */
    Result<Boolean> revokeKey(String keyId);

    /**
     * 轮换密钥
     */
    Result<ApiKeyDTO> rotateKey(String keyId, String newValue);

    // ==================== 密钥使用 ====================

    /**
     * 使用密钥（返回原始值）
     */
    Result<String> useKey(String keyId, String userId, String sceneId);

    /**
     * 检查密钥访问权限
     */
    Result<Boolean> checkAccess(String keyId, String userId, String sceneId);

    // ==================== 密钥授权 ====================

    /**
     * 授权密钥访问
     */
    Result<Boolean> grantAccess(String keyId, KeyGrantRequest request);

    /**
     * 撤销密钥访问
     */
    Result<Boolean> revokeAccess(String keyId, String principalId, String principalType);

    // ==================== 密钥统计 ====================

    /**
     * 获取密钥使用统计
     */
    Result<KeyUsageStats> getUsageStats(String keyId);

    /**
     * 生成密钥ID
     */
    String generateKeyId();
}
```

#### DTO定义

```java
package net.ooder.skill.security.dto.key;

/**
 * API密钥DTO
 */
public class ApiKeyDTO {
    private String keyId;              // 密钥ID
    private String keyName;            // 密钥名称
    private String keyType;            // 密钥类型
    private String provider;           // 服务提供商
    private String status;             // 状态
    private long createdAt;            // 创建时间
    private long expiresAt;            // 过期时间
    private long lastUsedAt;           // 最后使用时间
    private int useCount;              // 使用次数
    private int maxUseCount;           // 最大使用次数
    private String createdBy;          // 创建者
    private List<String> allowedUsers; // 授权用户
    private List<String> allowedRoles; // 授权角色
    private List<String> allowedScenes;// 授权场景
    private Map<String, Object> config;// 配置信息
}

/**
 * 密钥创建请求
 */
public class KeyCreateRequest {
    private String keyName;
    private String keyType;
    private String provider;
    private String rawValue;           // 原始密钥值
    private long expiresAt;
    private int maxUseCount;
    private List<String> allowedUsers;
    private List<String> allowedRoles;
    private List<String> allowedScenes;
    private Map<String, Object> config;
}

/**
 * 密钥授权请求
 */
public class KeyGrantRequest {
    private String keyId;
    private String userId;
    private String roleId;
    private String sceneId;
    private List<String> userIds;
    private List<String> roleIds;
    private List<String> sceneIds;
}

/**
 * 密钥使用统计
 */
public class KeyUsageStats {
    private String keyId;
    private int totalUseCount;
    private int todayUseCount;
    private int weekUseCount;
    private int monthUseCount;
    private int failedCount;
    private int deniedCount;
    private long lastUsedAt;
    private String lastUsedBy;
    private String lastUsedScene;
}
```

### 3.2 新增 EncryptionApi

#### 接口定义

```java
package net.ooder.skill.security.api;

import net.ooder.sdk.infra.utils.Result;

/**
 * 加密服务API
 * 
 * <p>提供统一的加密解密服务</p>
 * 
 * @since 2.4
 */
public interface EncryptionApi {

    String getApiName();
    String getVersion();
    void initialize(Map<String, Object> context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();

    /**
     * 加密数据
     */
    Result<String> encrypt(String plainText);

    /**
     * 解密数据
     */
    Result<String> decrypt(String cipherText);

    /**
     * 生成随机密钥
     */
    Result<String> generateKey();

    /**
     * 哈希数据
     */
    Result<String> hash(String plainText);

    /**
     * 验证哈希
     */
    Result<Boolean> verifyHash(String plainText, String hash);

    /**
     * 生成令牌
     */
    Result<String> generateToken(String payload, long expiresAt);

    /**
     * 验证令牌
     */
    Result<String> verifyToken(String token);
}
```

### 3.3 新增 AgentAuthApi

#### 接口定义

```java
package net.ooder.skill.security.api;

import net.ooder.skill.security.dto.agent.*;
import net.ooder.sdk.infra.utils.Result;

import java.util.List;

/**
 * Agent认证API
 * 
 * <p>提供Agent身份认证和授权能力</p>
 * 
 * @since 2.4
 */
public interface AgentAuthApi {

    String getApiName();
    String getVersion();
    void initialize(Map<String, Object> context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();

    // ==================== Agent注册 ====================

    /**
     * 注册Agent
     */
    Result<AgentCredentialDTO> registerAgent(AgentRegisterRequest request);

    /**
     * 注销Agent
     */
    Result<Boolean> unregisterAgent(String agentId);

    /**
     * 获取Agent信息
     */
    Result<AgentInfoDTO> getAgent(String agentId);

    /**
     * 列出所有Agent
     */
    Result<List<AgentInfoDTO>> listAgents();

    // ==================== Agent认证 ====================

    /**
     * 认证Agent
     */
    Result<AgentSessionDTO> authenticate(String agentId, String credential);

    /**
     * 验证会话
     */
    Result<Boolean> validateSession(String sessionId);

    /**
     * 注销会话
     */
    Result<Boolean> invalidateSession(String sessionId);

    // ==================== Agent权限 ====================

    /**
     * 检查Agent权限
     */
    Result<Boolean> checkPermission(String agentId, String resource, String action);

    /**
     * 授予Agent权限
     */
    Result<Boolean> grantPermission(String agentId, String resource, String action);

    /**
     * 撤销Agent权限
     */
    Result<Boolean> revokePermission(String agentId, String resource, String action);
}
```

#### DTO定义

```java
package net.ooder.skill.security.dto.agent;

/**
 * Agent注册请求
 */
public class AgentRegisterRequest {
    private String agentId;
    private String agentName;
    private String agentType;
    private String description;
    private Map<String, Object> metadata;
}

/**
 * Agent凭证DTO
 */
public class AgentCredentialDTO {
    private String agentId;
    private String credential;         // 密钥凭证
    private long createdAt;
    private long expiresAt;
}

/**
 * Agent信息DTO
 */
public class AgentInfoDTO {
    private String agentId;
    private String agentName;
    private String agentType;
    private String status;
    private long registeredAt;
    private long lastAuthenticated;
    private Map<String, Object> metadata;
}

/**
 * Agent会话DTO
 */
public class AgentSessionDTO {
    private String sessionId;
    private String agentId;
    private long createdAt;
    private long expiresAt;
    private String token;
}
```

### 3.4 扩展 SecurityApi

#### 新增方法

```java
// ==================== 场景安全集成 ====================

/**
 * 设置场景安全配置
 */
Result<Boolean> setupSceneSecurity(String sceneId, SceneSecurityConfig config);

/**
 * 清理场景安全配置
 */
Result<Boolean> cleanupSceneSecurity(String sceneId);

/**
 * 检查场景访问权限
 */
Result<Boolean> checkSceneAccess(String userId, String sceneId, String action);

/**
 * 获取场景安全报告
 */
Result<SceneSecurityReport> getSceneSecurityReport(String sceneId);

// ==================== 能力安全集成 ====================

/**
 * 注册能力安全策略
 */
Result<Boolean> registerCapabilitySecurity(String capabilityId, CapabilitySecurityPolicy policy);

/**
 * 注销能力安全策略
 */
Result<Boolean> unregisterCapabilitySecurity(String capabilityId);

/**
 * 检查能力访问权限
 */
Result<Boolean> checkCapabilityAccess(String userId, String capabilityId, String action);

/**
 * 获取能力安全报告
 */
Result<CapabilitySecurityReport> getCapabilitySecurityReport(String capabilityId);

// ==================== LLM密钥集成 ====================

/**
 * 获取LLM API密钥
 */
Result<String> getLlmApiKey(String provider, String userId, String sceneId);

/**
 * 检查LLM访问权限
 */
Result<Boolean> checkLlmAccess(String userId, String provider, String sceneId);

/**
 * 记录LLM调用
 */
Result<Boolean> logLlmCall(String userId, String provider, String model, int tokenCount, boolean success);
```

---

## 四、扩展设计思路

### 4.1 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                    SDK安全扩展层                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │
│  │KeyManagement│ │EncryptionApi│ │AgentAuthApi │            │
│  │Api          │ │             │ │             │            │
│  └─────────────┘ └─────────────┘ └─────────────┘            │
│  ┌─────────────────────────────────────────────┐            │
│  │          SecurityApi (扩展)                 │            │
│  │  + 场景安全集成                              │            │
│  │  + 能力安全集成                              │            │
│  │  + LLM密钥集成                               │            │
│  └─────────────────────────────────────────────┘            │
└─────────────────────────────────────────────────────────────┘
          │               │               │
          ▼               ▼               ▼
┌─────────────────────────────────────────────────────────────┐
│                    业务应用层                                │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │
│  │场景模块     │ │能力模块     │ │LLM模块      │            │
│  │skill-scene  │ │capability   │ │llm-provider │            │
│  └─────────────┘ └─────────────┘ └─────────────┘            │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 模块依赖关系

```
skill-security (扩展后)
    ├── skill-security-api (新增)
    │   ├── KeyManagementApi
    │   ├── EncryptionApi
    │   └── AgentAuthApi
    ├── skill-security-dto (扩展)
    │   ├── key/*.java
    │   ├── agent/*.java
    │   └── integration/*.java
    ├── skill-security-impl (扩展)
    │   ├── KeyManagementServiceImpl
    │   ├── EncryptionServiceImpl
    │   └── AgentAuthServiceImpl
    └── skill-security-integration (新增)
        ├── SceneSecurityIntegration
        ├── CapabilitySecurityIntegration
        └── LlmSecurityIntegration
```

### 4.3 存储扩展

建议SDK团队提供存储接口抽象：

```java
/**
 * 密钥存储接口
 */
public interface KeyStorage {
    void store(String keyId, ApiKeyDTO key, String encryptedValue);
    ApiKeyDTO load(String keyId);
    String loadEncryptedValue(String keyId);
    void delete(String keyId);
    List<ApiKeyDTO> listAll();
}

/**
 * 默认内存存储实现
 */
@Component
@ConditionalOnMissingBean(KeyStorage.class)
public class MemoryKeyStorage implements KeyStorage {
    // 内存实现
}

/**
 * 数据库存储实现（可选）
 */
@Component
@ConditionalOnProperty(name = "security.storage.type", havingValue = "database")
public class DatabaseKeyStorage implements KeyStorage {
    // 数据库实现
}
```

---

## 五、交付要求

### 5.1 代码要求

1. **Java版本**: 必须支持Java 8
2. **代码规范**: 遵循Ooder代码规范
3. **注释**: 所有公共接口必须有Javadoc注释
4. **单元测试**: 核心功能需要单元测试覆盖

### 5.2 接口要求

1. 所有API接口继承基础生命周期方法
2. 返回值统一使用 `Result<T>` 包装
3. 异常处理遵循SDK统一规范

### 5.3 文档要求

1. 提供API接口文档
2. 提供集成示例代码
3. 提供配置说明文档

---

## 六、验收标准

### 6.1 功能验收

| 验收项 | 验收标准 |
|--------|---------|
| 密钥创建 | 能创建并加密存储密钥 |
| 密钥使用 | 能解密并返回密钥原始值 |
| 密钥授权 | 能按用户/角色/场景授权 |
| 密钥轮换 | 能安全轮换密钥 |
| Agent注册 | 能生成Agent凭证 |
| Agent认证 | 能验证Agent身份 |
| 场景安全 | 能自动配置场景安全 |
| 能力安全 | 能检查能力访问权限 |

### 6.2 性能验收

| 指标 | 要求 |
|------|------|
| 密钥获取延迟 | < 10ms |
| 加密/解密延迟 | < 5ms |
| 权限检查延迟 | < 3ms |

### 6.3 安全验收

| 验收项 | 要求 |
|--------|------|
| 密钥存储 | 必须加密存储 |
| 密钥传输 | 内存中使用后清除 |
| 审计日志 | 所有操作可追溯 |

---

## 七、时间计划

| 阶段 | 内容 | 预计时间 |
|------|------|---------|
| 设计评审 | 评审扩展设计 | 2天 |
| 接口开发 | 开发新API接口 | 5天 |
| 实现开发 | 开发默认实现 | 5天 |
| 单元测试 | 编写单元测试 | 3天 |
| 集成测试 | 与业务模块集成测试 | 3天 |
| 文档编写 | 编写接口文档 | 2天 |
| **总计** | | **20天** |

---

## 八、联系方式

- **委托方**: 安全控制模块开发组
- **技术对接人**: [待指定]
- **文档维护**: docs/SECURITY_MODULE_REQUIREMENTS.md

---

## 附录A：现有SDK安全模块文件清单

```
skills/skill-security/
├── src/main/java/net/ooder/skill/security/
│   ├── api/
│   │   ├── SecurityApi.java          # 现有接口
│   │   └── SecurityApiImpl.java      # 现有实现
│   ├── controller/
│   │   └── SecurityController.java   # REST控制器
│   ├── dto/
│   │   ├── SecurityPolicy.java
│   │   ├── SecurityStatus.java
│   │   ├── SecurityStats.java
│   │   ├── AccessControl.java
│   │   ├── ThreatInfo.java
│   │   ├── FirewallRule.java
│   │   ├── FirewallStatus.java
│   │   ├── AuditLog.java
│   │   └── AuditStats.java
│   ├── provider/
│   │   └── SecurityProviderImpl.java
│   └── service/
│       ├── SecurityService.java
│       └── impl/SecurityServiceImpl.java
└── src/test/java/net/ooder/skill/security/
    ├── api/SecurityApiTest.java
    └── lifecycle/SecuritySkillLifecycleTest.java
```

## 附录B：扩展后SDK安全模块文件清单

```
skills/skill-security/
├── src/main/java/net/ooder/skill/security/
│   ├── api/
│   │   ├── SecurityApi.java          # 扩展
│   │   ├── SecurityApiImpl.java      # 扩展
│   │   ├── KeyManagementApi.java     # 新增
│   │   ├── KeyManagementApiImpl.java # 新增
│   │   ├── EncryptionApi.java        # 新增
│   │   ├── EncryptionApiImpl.java    # 新增
│   │   ├── AgentAuthApi.java         # 新增
│   │   └── AgentAuthApiImpl.java     # 新增
│   ├── dto/
│   │   ├── ... (现有DTO)
│   │   ├── key/                      # 新增
│   │   │   ├── ApiKeyDTO.java
│   │   │   ├── KeyCreateRequest.java
│   │   │   ├── KeyGrantRequest.java
│   │   │   ├── KeyUsageStats.java
│   │   │   ├── KeyType.java
│   │   │   └── KeyStatus.java
│   │   ├── agent/                    # 新增
│   │   │   ├── AgentRegisterRequest.java
│   │   │   ├── AgentCredentialDTO.java
│   │   │   ├── AgentInfoDTO.java
│   │   │   └── AgentSessionDTO.java
│   │   └── integration/              # 新增
│   │       ├── SceneSecurityConfig.java
│   │       ├── SceneSecurityReport.java
│   │       ├── CapabilitySecurityPolicy.java
│   │       └── CapabilitySecurityReport.java
│   ├── integration/                  # 新增
│   │   ├── SceneSecurityIntegration.java
│   │   ├── CapabilitySecurityIntegration.java
│   │   ├── LlmSecurityIntegration.java
│   │   └── AgentSecurityIntegration.java
│   ├── storage/                      # 新增
│   │   ├── KeyStorage.java
│   │   └── MemoryKeyStorage.java
│   └── controller/
│       ├── SecurityController.java   # 扩展
│       ├── KeyController.java        # 新增
│       └── AgentAuthController.java  # 新增
└── src/test/java/net/ooder/skill/security/
    ├── api/
    │   ├── SecurityApiTest.java
    │   ├── KeyManagementApiTest.java # 新增
    │   ├── EncryptionApiTest.java    # 新增
    │   └── AgentAuthApiTest.java     # 新增
    └── integration/                  # 新增
        ├── SceneSecurityIntegrationTest.java
        └── CapabilitySecurityIntegrationTest.java
```
