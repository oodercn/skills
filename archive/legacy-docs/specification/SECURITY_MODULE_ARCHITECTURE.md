# 安全控制模块架构设计

> **版本**: v1.0  
> **日期**: 2026-03-01  
> **状态**: 设计中

---

## 一、模块概述

### 1.1 模块定位

安全控制模块（skill-security-control）是Ooder平台的核心安全基础设施，提供统一的密钥管理、访问控制、通讯安全和审计追踪能力。

### 1.2 与现有模块关系

```
┌─────────────────────────────────────────────────────────────┐
│                    安全控制模块                              │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌────────┐ │
│  │  密钥管理   │ │  访问控制   │ │  通讯安全   │ │ 审计   │ │
│  │ KeyManager  │ │ AccessCtrl  │ │ CommSec     │ │ Audit  │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └────────┘ │
└─────────────────────────────────────────────────────────────┘
        ↓               ↓               ↓               ↓
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ 场景模块    │ │ Agent模块   │ │ LLM模块     │ │ skill-      │
│ skill-scene │ │ skill-agent │ │ LlmProvider │ │ security    │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
```

---

## 二、模块结构

### 2.1 包结构

```
net.ooder.skill.security/
├── api/                          # API接口定义
│   ├── KeyManagementApi.java     # 密钥管理API
│   ├── AccessControlApi.java     # 访问控制API
│   ├── AgentSecurityApi.java     # Agent安全API
│   └── SecurityAuditApi.java     # 安全审计API
├── controller/                   # REST控制器
│   ├── KeyController.java        # 密钥管理控制器
│   ├── AccessController.java     # 访问控制控制器
│   └── SecurityController.java   # 安全管理控制器
├── dto/                          # 数据传输对象
│   ├── key/                      # 密钥相关DTO
│   │   ├── ApiKeyDTO.java
│   │   ├── KeyCreateRequest.java
│   │   ├── KeyUseRequest.java
│   │   └── KeyGrantRequest.java
│   ├── access/                   # 访问控制DTO
│   │   ├── PermissionDTO.java
│   │   ├── RoleDTO.java
│   │   └── ResourceDTO.java
│   └── audit/                    # 审计DTO
│       ├── AuditLogDTO.java
│       ├── AuditQueryDTO.java
│       └── AuditStatsDTO.java
├── model/                        # 领域模型
│   ├── ApiKey.java               # 密钥实体
│   ├── KeyPermission.java        # 密钥权限
│   ├── SecurityPolicy.java       # 安全策略
│   └── AuditRecord.java          # 审计记录
├── service/                      # 服务层
│   ├── KeyManagementService.java
│   ├── AccessControlService.java
│   ├── EncryptionService.java
│   └── AuditService.java
├── service/impl/                 # 服务实现
│   ├── KeyManagementServiceImpl.java
│   ├── AccessControlServiceImpl.java
│   ├── EncryptionServiceImpl.java
│   └── AuditServiceImpl.java
├── crypto/                       # 加密工具
│   ├── Encryptor.java            # 加密器接口
│   ├── AesEncryptor.java         # AES加密实现
│   └── KeyGenerator.java         # 密钥生成器
├── store/                        # 存储层
│   ├── KeyStore.java             # 密钥存储接口
│   ├── MemoryKeyStore.java       # 内存存储实现
│   └── AuditStore.java           # 审计存储
├── integration/                  # 模块集成
│   ├── SceneSecurityIntegration.java
│   ├── LlmSecurityIntegration.java
│   └── AgentSecurityIntegration.java
└── config/                       # 配置类
    ├── SecurityConfig.java
    └── EncryptionConfig.java
```

---

## 三、核心类设计

### 3.1 密钥管理

#### ApiKey 实体

```java
@Data
public class ApiKey {
    private String keyId;              // 密钥ID
    private String keyName;            // 密钥名称
    private KeyType keyType;           // 密钥类型
    private String provider;           // 服务提供商
    private String encryptedValue;     // 加密后的密钥值
    private KeyStatus status;          // 状态
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

public enum KeyType {
    LLM_API_KEY("LLM API密钥"),
    CLOUD_API_KEY("云服务API密钥"),
    DATABASE_KEY("数据库密钥"),
    SERVICE_TOKEN("服务令牌"),
    AGENT_KEY("Agent密钥"),
    ENCRYPTION_KEY("加密密钥");
}

public enum KeyStatus {
    ACTIVE, INACTIVE, EXPIRED, REVOKED
}
```

#### KeyManagementService 接口

```java
public interface KeyManagementService {
    ApiKey createKey(ApiKey key, String rawValue);
    ApiKey getKey(String keyId);
    ApiKey getKeyByName(String keyName);
    List<ApiKey> listKeys(KeyType type, KeyStatus status);
    ApiKey updateKey(String keyId, ApiKey key);
    boolean deleteKey(String keyId);
    boolean revokeKey(String keyId);
    ApiKey rotateKey(String keyId, String newValue);
    String useKey(String keyId, String userId, String sceneId);
    boolean grantAccess(String keyId, KeyGrantRequest request);
    boolean revokeAccess(String keyId, String principalId);
    boolean checkAccess(String keyId, String userId, String sceneId);
    KeyUsageStats getUsageStats(String keyId);
}
```

### 3.2 加密服务

#### EncryptionService 接口

```java
public interface EncryptionService {
    String encrypt(String plainText, String keyId);
    String decrypt(String cipherText, String keyId);
    String generateKey();
    String hash(String plainText);
    boolean verifyHash(String plainText, String hash);
    String generateToken(String payload, long expiresAt);
    String verifyToken(String token);
}
```

### 3.3 访问控制

#### Permission 模型

```java
@Data
public class Permission {
    private String permissionId;
    private String resourceType;       // 资源类型
    private String resourceId;         // 资源ID
    private String action;             // 操作: READ, WRITE, DELETE, EXECUTE
    private String principalType;      // 主体类型: USER, ROLE, AGENT
    private String principalId;        // 主体ID
    private PermissionEffect effect;   // 效果: ALLOW, DENY
    private Map<String, String> conditions; // 条件
}

public enum PermissionEffect {
    ALLOW, DENY
}
```

#### AccessControlService 接口

```java
public interface AccessControlService {
    Permission createPermission(Permission permission);
    Permission getPermission(String permissionId);
    List<Permission> listPermissions(String resourceType, String resourceId);
    boolean deletePermission(String permissionId);
    boolean checkPermission(String userId, String resourceType, String resourceId, String action);
    List<Permission> getUserPermissions(String userId);
    boolean grantRole(String userId, String roleId);
    boolean revokeRole(String userId, String roleId);
}
```

### 3.4 审计服务

#### AuditRecord 模型

```java
@Data
public class AuditRecord {
    private String recordId;
    private AuditEventType eventType;  // 事件类型
    private String userId;             // 操作用户
    private String agentId;            // 操作Agent
    private String resourceType;       // 资源类型
    private String resourceId;         // 资源ID
    private String action;             // 操作
    private AuditResult result;        // 结果
    private String detail;             // 详情
    private String ipAddress;          // IP地址
    private long timestamp;            // 时间戳
    private Map<String, Object> metadata;
}

public enum AuditEventType {
    KEY_CREATE, KEY_USE, KEY_ROTATE, KEY_REVOKE,
    PERMISSION_GRANT, PERMISSION_REVOKE, PERMISSION_CHECK,
    AGENT_AUTH, AGENT_COMM, AGENT_ACCESS,
    SCENE_CREATE, SCENE_START, SCENE_END,
    LLM_CALL, LLM_KEY_USE
}

public enum AuditResult {
    SUCCESS, FAILURE, DENIED
}
```

---

## 四、API设计

### 4.1 密钥管理API

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/keys` | GET | 列出密钥（脱敏） |
| `/api/v1/keys` | POST | 创建密钥 |
| `/api/v1/keys/{keyId}` | GET | 获取密钥详情 |
| `/api/v1/keys/{keyId}` | PUT | 更新密钥 |
| `/api/v1/keys/{keyId}` | DELETE | 删除密钥 |
| `/api/v1/keys/{keyId}/use` | POST | 使用密钥 |
| `/api/v1/keys/{keyId}/rotate` | POST | 轮换密钥 |
| `/api/v1/keys/{keyId}/revoke` | POST | 撤销密钥 |
| `/api/v1/keys/{keyId}/grant` | POST | 授权访问 |
| `/api/v1/keys/{keyId}/revoke-access` | POST | 撤销访问 |
| `/api/v1/keys/{keyId}/stats` | GET | 使用统计 |

### 4.2 访问控制API

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/permissions` | GET | 列出权限 |
| `/api/v1/permissions` | POST | 创建权限 |
| `/api/v1/permissions/{id}` | DELETE | 删除权限 |
| `/api/v1/permissions/check` | POST | 检查权限 |
| `/api/v1/roles` | GET | 列出角色 |
| `/api/v1/roles` | POST | 创建角色 |
| `/api/v1/roles/{roleId}/grant` | POST | 授予角色 |
| `/api/v1/roles/{roleId}/revoke` | POST | 撤销角色 |

### 4.3 审计API

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/v1/audit/logs` | GET | 查询审计日志 |
| `/api/v1/audit/stats` | GET | 审计统计 |
| `/api/v1/audit/export` | GET | 导出审计日志 |

---

## 五、与现有模块集成

### 5.1 与LLM模块集成

```java
@Component
public class LlmSecurityIntegration {
    
    @Autowired
    private KeyManagementService keyService;
    
    @Autowired
    private AuditService auditService;
    
    public String getLlmApiKey(String provider, String userId, String sceneId) {
        String keyName = "llm-" + provider;
        ApiKey key = keyService.getKeyByName(keyName);
        
        if (key == null) {
            throw new SecurityException("LLM API key not found: " + provider);
        }
        
        if (!keyService.checkAccess(key.getKeyId(), userId, sceneId)) {
            auditService.log(AuditRecord.builder()
                .eventType(AuditEventType.LLM_KEY_USE)
                .userId(userId)
                .resourceId(key.getKeyId())
                .result(AuditResult.DENIED)
                .build());
            throw new SecurityException("Access denied to LLM API key");
        }
        
        String rawKey = keyService.useKey(key.getKeyId(), userId, sceneId);
        
        auditService.log(AuditRecord.builder()
            .eventType(AuditEventType.LLM_KEY_USE)
            .userId(userId)
            .resourceId(key.getKeyId())
            .result(AuditResult.SUCCESS)
            .build());
        
        return rawKey;
    }
}
```

### 5.2 与Agent模块集成

```java
@Component
public class AgentSecurityIntegration {
    
    @Autowired
    private KeyManagementService keyService;
    
    @Autowired
    private AccessControlService accessService;
    
    public ApiKey generateAgentKey(String agentId) {
        String rawKey = keyService.generateKey();
        ApiKey key = new ApiKey();
        key.setKeyId("agent-" + agentId);
        key.setKeyName("Agent Key: " + agentId);
        key.setKeyType(KeyType.AGENT_KEY);
        key.setCreatedBy(agentId);
        key.getAllowedUsers().add(agentId);
        
        return keyService.createKey(key, rawKey);
    }
    
    public boolean authenticateAgent(String agentId, String token) {
        ApiKey key = keyService.getKey("agent-" + agentId);
        if (key == null || key.getStatus() != KeyStatus.ACTIVE) {
            return false;
        }
        return keyService.useKey(key.getKeyId(), agentId, null) != null;
    }
    
    public boolean checkAgentPermission(String agentId, String resource, String action) {
        return accessService.checkPermission(agentId, "AGENT", resource, action);
    }
}
```

### 5.3 与场景模块集成

```java
@Component
public class SceneSecurityIntegration {
    
    @Autowired
    private KeyManagementService keyService;
    
    @Autowired
    private AccessControlService accessService;
    
    @Autowired
    private AuditService auditService;
    
    public void setupSceneSecurity(String sceneId, SceneSecurityConfig config) {
        for (String keyId : config.getRequiredKeys()) {
            keyService.grantAccess(keyId, KeyGrantRequest.builder()
                .sceneId(sceneId)
                .build());
        }
        
        auditService.log(AuditRecord.builder()
            .eventType(AuditEventType.SCENE_CREATE)
            .resourceId(sceneId)
            .result(AuditResult.SUCCESS)
            .build());
    }
    
    public void cleanupSceneSecurity(String sceneId) {
        List<ApiKey> keys = keyService.listKeys(null, KeyStatus.ACTIVE);
        for (ApiKey key : keys) {
            if (key.getAllowedScenes().contains(sceneId)) {
                keyService.revokeAccess(key.getKeyId(), sceneId);
            }
        }
    }
}
```

---

## 六、安全设计

### 6.1 密钥加密流程

```
原始密钥 → 生成随机IV → AES-256加密 → Base64编码 → 存储
         ↓
    主密钥(环境变量/密钥库)
```

### 6.2 密钥使用流程

```
1. 用户请求使用密钥
2. 验证用户身份
3. 检查访问权限
4. 检查密钥状态(是否过期/撤销)
5. 解密密钥
6. 记录审计日志
7. 返回密钥(内存中使用后清除)
```

### 6.3 Agent认证流程

```
1. Agent注册 → 生成密钥对
2. Agent连接 → 挑战-响应认证
3. 认证成功 → 颁发会话令牌
4. 后续请求 → 验证会话令牌
5. 会话过期 → 重新认证
```

---

## 七、字典表设计

### 7.1 密钥类型枚举

```java
@Dict(code = "key_type", name = "密钥类型")
public enum KeyType implements DictItem {
    LLM_API_KEY("LLM_API_KEY", "LLM API密钥", "大语言模型API密钥", "ri-key-line", 1),
    CLOUD_API_KEY("CLOUD_API_KEY", "云服务API密钥", "云服务API密钥", "ri-cloud-line", 2),
    DATABASE_KEY("DATABASE_KEY", "数据库密钥", "数据库连接密钥", "ri-database-line", 3),
    SERVICE_TOKEN("SERVICE_TOKEN", "服务令牌", "服务间调用令牌", "ri-share-line", 4),
    AGENT_KEY("AGENT_KEY", "Agent密钥", "Agent身份密钥", "ri-robot-line", 5),
    ENCRYPTION_KEY("ENCRYPTION_KEY", "加密密钥", "数据加密密钥", "ri-lock-line", 6);
}
```

### 7.2 密钥状态枚举

```java
@Dict(code = "key_status", name = "密钥状态")
public enum KeyStatus implements DictItem {
    ACTIVE("ACTIVE", "激活", "密钥可用", "ri-check-line", 1),
    INACTIVE("INACTIVE", "未激活", "密钥未启用", "ri-pause-line", 2),
    EXPIRED("EXPIRED", "已过期", "密钥已过期", "ri-time-line", 3),
    REVOKED("REVOKED", "已撤销", "密钥已撤销", "ri-close-line", 4);
}
```

### 7.3 审计事件类型枚举

```java
@Dict(code = "audit_event_type", name = "审计事件类型")
public enum AuditEventType implements DictItem {
    KEY_CREATE("KEY_CREATE", "密钥创建", "创建新密钥", "ri-add-line", 1),
    KEY_USE("KEY_USE", "密钥使用", "使用密钥", "ri-key-line", 2),
    KEY_ROTATE("KEY_ROTATE", "密钥轮换", "轮换密钥", "ri-refresh-line", 3),
    KEY_REVOKE("KEY_REVOKE", "密钥撤销", "撤销密钥", "ri-close-circle-line", 4),
    PERMISSION_GRANT("PERMISSION_GRANT", "权限授予", "授予权限", "ri-user-add-line", 5),
    PERMISSION_REVOKE("PERMISSION_REVOKE", "权限撤销", "撤销权限", "ri-user-unfollow-line", 6),
    AGENT_AUTH("AGENT_AUTH", "Agent认证", "Agent身份认证", "ri-robot-line", 7),
    LLM_CALL("LLM_CALL", "LLM调用", "调用大语言模型", "ri-chat-ai-line", 8);
}
```

---

## 八、前端页面设计

### 8.1 页面结构

```
安全控制
├── 密钥管理
│   ├── 密钥列表
│   ├── 创建密钥
│   ├── 密钥详情
│   └── 使用统计
├── 访问控制
│   ├── 权限列表
│   ├── 角色管理
│   └── 授权管理
├── 安全策略
│   ├── 策略列表
│   └── 策略配置
└── 审计日志
    ├── 日志查询
    ├── 统计分析
    └── 日志导出
```

### 8.2 密钥管理页面

- 密钥列表：显示密钥名称、类型、状态、创建时间
- 创建密钥：选择类型、输入密钥值、设置权限
- 密钥详情：查看密钥信息、使用记录、授权列表
- 使用统计：调用次数、调用趋势、异常告警

---

## 九、部署配置

### 9.1 配置项

```yaml
security:
  encryption:
    algorithm: AES-256
    key-source: env  # env, file, vault
    master-key-env: OODER_MASTER_KEY
  key-management:
    default-expiry-days: 90
    max-use-count: 10000
    auto-rotate-days: 30
  audit:
    enabled: true
    retention-days: 90
    storage: memory  # memory, database
```

### 9.2 环境变量

| 变量名 | 说明 | 必需 |
|--------|------|------|
| OODER_MASTER_KEY | 主加密密钥 | 是 |
| OODER_ENCRYPTION_IV | 加密初始向量 | 否 |

---

## 十、扩展点

### 10.1 存储扩展

支持扩展为数据库存储：
- 实现KeyStore接口
- 配置存储类型

### 10.2 加密扩展

支持扩展为硬件加密：
- 实现Encryptor接口
- 集成HSM/KMS

### 10.3 审计扩展

支持扩展为外部审计系统：
- 实现AuditStore接口
- 对接SIEM系统

---

## 十一、场景安全嵌入式设计

### 11.1 场景安全集成架构

```
┌─────────────────────────────────────────────────────────────┐
│                     场景模块 (skill-scene)                   │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │
│  │SceneGroup   │ │Participant  │ │Capability   │            │
│  │Service      │ │Management   │ │Binding      │            │
│  └──────┬──────┘ └──────┬──────┘ └──────┬──────┘            │
└─────────┼───────────────┼───────────────┼───────────────────┘
          │               │               │
          ▼               ▼               ▼
┌─────────────────────────────────────────────────────────────┐
│              场景安全集成 (SceneSecurityIntegration)         │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │
│  │场景创建安全 │ │参与者权限   │ │密钥授权     │            │
│  │setupSecurity│ │checkAccess  │ │grantKeys    │            │
│  └─────────────┘ └─────────────┘ └─────────────┘            │
└─────────────────────────────────────────────────────────────┘
          │               │               │
          ▼               ▼               ▼
┌─────────────────────────────────────────────────────────────┐
│              安全控制模块 (skill-security)                   │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │
│  │KeyManagement│ │AuditService │ │AccessControl│            │
│  │Service      │ │             │ │Service      │            │
│  └─────────────┘ └─────────────┘ └─────────────┘            │
└─────────────────────────────────────────────────────────────┘
```

### 11.2 场景生命周期安全钩子

| 场景生命周期 | 安全钩子 | 操作 |
|-------------|---------|------|
| 创建场景 | `setupSceneSecurity()` | 初始化安全策略、授权必要密钥 |
| 激活场景 | `checkSceneAccess()` | 验证启动权限 |
| 加入参与者 | `checkParticipantAccess()` | 验证参与者权限 |
| 绑定能力 | `checkCapabilityBinding()` | 验证能力访问权限 |
| 使用密钥 | `getSceneKey()` | 获取并记录密钥使用 |
| 销毁场景 | `cleanupSceneSecurity()` | 清理安全配置、撤销授权 |

### 11.3 场景安全配置模型

```java
public class SceneSecurityConfig {
    private String sceneId;             // 场景ID
    private List<String> requiredKeys;  // 必需密钥列表
    private boolean dataIsolation;      // 数据隔离
    private boolean crossOrgAllowed;    // 允许跨组织
    private List<String> allowedAgents; // 允许的Agent
    private int maxConcurrency;         // 最大并发
    private AuditLevel auditLevel;      // 审计级别
}

public enum AuditLevel {
    NONE,       // 不记录
    BASIC,      // 基本记录
    DETAILED,   // 详细记录
    FULL        // 完整记录
}
```

### 11.4 场景安全集成示例

```java
@Service
public class SceneGroupServiceImpl implements SceneGroupService {
    
    @Autowired
    private SceneSecurityIntegration securityIntegration;
    
    @Override
    public SceneGroupDTO create(String templateId, SceneGroupConfigDTO config) {
        SceneGroupDTO scene = new SceneGroupDTO();
        
        securityIntegration.setupSceneSecurity(scene.getSceneId(), 
            SceneSecurityIntegration.SceneSecurityConfig.builder()
                .requiredKeys(config.getRequiredKeys())
                .dataIsolation(true)
                .build());
        
        return scene;
    }
    
    @Override
    public boolean destroy(String sceneGroupId) {
        securityIntegration.cleanupSceneSecurity(sceneGroupId);
        return true;
    }
}
```

---

## 十二、能力安全嵌入式设计

### 12.1 能力安全集成架构

```
┌─────────────────────────────────────────────────────────────┐
│                    能力模块 (skill-capability)               │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │
│  │Capability   │ │Capability   │ │Capability   │            │
│  │Registry     │ │Binding      │ │Execution    │            │
│  └──────┬──────┘ └──────┬──────┘ └──────┬──────┘            │
└─────────┼───────────────┼───────────────┼───────────────────┘
          │               │               │
          ▼               ▼               ▼
┌─────────────────────────────────────────────────────────────┐
│            能力安全集成 (CapabilitySecurityIntegration)      │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │
│  │能力安全注册 │ │能力访问检查 │ │能力密钥管理 │            │
│  │register     │ │checkAccess  │ │getKey       │            │
│  └─────────────┘ └─────────────┘ └─────────────┘            │
└─────────────────────────────────────────────────────────────┘
          │               │               │
          ▼               ▼               ▼
┌─────────────────────────────────────────────────────────────┐
│              安全控制模块 (skill-security)                   │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │
│  │KeyManagement│ │AuditService │ │AccessControl│            │
│  │Service      │ │             │ │Service      │            │
│  └─────────────┘ └─────────────┘ └─────────────┘            │
└─────────────────────────────────────────────────────────────┘
```

### 12.2 能力安全策略模型

```java
public class CapabilitySecurityPolicy {
    private String capabilityId;        // 能力ID
    private List<String> requiredKeys;  // 必需密钥
    private List<String> allowedUsers;  // 允许用户
    private List<String> allowedRoles;  // 允许角色
    private List<String> allowedActions;// 允许操作
    private int maxCallsPerMinute;      // 每分钟最大调用
    private boolean requireApproval;    // 是否需要审批
}
```

### 12.3 能力安全检查流程

```
能力调用请求
    │
    ▼
┌─────────────────┐
│ 检查能力安全策略 │
└────────┬────────┘
         │
    ┌────┴────┐
    │ 策略存在? │
    └────┬────┘
         │
    ┌────┴────┐    否    ┌─────────────┐
    ├─────────┼────────►│ 允许访问    │
    │         │         └─────────────┘
    │ 是      │
    ▼         │
┌─────────────────┐
│ 检查用户权限    │
└────────┬────────┘
         │
    ┌────┴────┐
    │ 用户允许? │
    └────┬────┘
         │
    ┌────┴────┐    否    ┌─────────────┐
    ├─────────┼────────►│ 拒绝访问    │
    │         │         └─────────────┘
    │ 是      │
    ▼         │
┌─────────────────┐
│ 检查操作权限    │
└────────┬────────┘
         │
    ┌────┴────┐
    │ 操作允许? │
    └────┬────┘
         │
    ┌────┴────┐    否    ┌─────────────┐
    ├─────────┼────────►│ 拒绝访问    │
    │         │         └─────────────┘
    │ 是      │
    ▼         │
┌─────────────────┐
│ 记录审计日志    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 允许访问        │
└─────────────────┘
```

### 12.4 能力安全集成示例

```java
@Service
public class CapabilityBindingServiceImpl implements CapabilityBindingService {
    
    @Autowired
    private CapabilitySecurityIntegration securityIntegration;
    
    @Override
    public CapabilityBinding bind(String sceneGroupId, CapabilityBindingRequest request) {
        if (!securityIntegration.checkCapabilityAccess(
                getCurrentUserId(), 
                request.getCapabilityId(), 
                "BIND")) {
            throw new SecurityException("No permission to bind capability");
        }
        
        CapabilityBinding binding = new CapabilityBinding();
        
        securityIntegration.logCapabilityEvent(
            request.getCapabilityId(),
            getCurrentUserId(),
            AuditEventType.PERMISSION_GRANT,
            AuditResult.SUCCESS);
        
        return binding;
    }
}
```

---

## 十三、LLM安全嵌入式设计

### 13.1 LLM密钥获取流程

```
LLM调用请求
    │
    ▼
┌─────────────────┐
│ 获取LLM密钥     │
│ getLlmApiKey()  │
└────────┬────────┘
         │
    ┌────┴────┐
    │ 密钥存在? │
    └────┬────┘
         │
    ┌────┴────┐    否    ┌─────────────┐
    ├─────────┼────────►│ 返回错误    │
    │         │         └─────────────┘
    │ 是      │
    ▼         │
┌─────────────────┐
│ 检查访问权限    │
└────────┬────────┘
         │
    ┌────┴────┐
    │ 有权限?  │
    └────┬────┘
         │
    ┌────┴────┐    否    ┌─────────────┐
    ├─────────┼────────►│ 记录拒绝    │
    │         │         └─────────────┘
    │ 是      │
    ▼         │
┌─────────────────┐
│ 使用密钥        │
│ useKey()        │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 记录审计日志    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 返回密钥        │
└─────────────────┘
```

### 13.2 LLM安全集成示例

```java
@Service
public class OpenAiLlmProvider implements LlmProvider {
    
    @Autowired
    private LlmSecurityIntegration securityIntegration;
    
    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        String apiKey = securityIntegration.getLlmApiKey("openai", getCurrentUserId(), getSceneId());
        
        if (apiKey == null) {
            throw new SecurityException("Failed to get OpenAI API key");
        }
        
        Map<String, Object> result = doChat(model, messages, options, apiKey);
        
        securityIntegration.logLlmCall(
            getCurrentUserId(),
            "openai",
            model,
            getTokenCount(result),
            AuditResult.SUCCESS);
        
        return result;
    }
}
```

---

## 十四、Agent安全嵌入式设计

### 14.1 Agent认证流程

```
Agent注册请求
    │
    ▼
┌─────────────────┐
│ 生成Agent密钥   │
│ registerAgent() │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 存储密钥        │
│ 记录审计日志    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 返回Agent凭证   │
└─────────────────┘

Agent连接请求
    │
    ▼
┌─────────────────┐
│ 验证Agent身份   │
│ authenticateAgent()│
└────────┬────────┘
         │
    ┌────┴────┐
    │ 认证成功? │
    └────┬────┘
         │
    ┌────┴────┐    否    ┌─────────────┐
    ├─────────┼────────►│ 拒绝连接    │
    │         │         └─────────────┘
    │ 是      │
    ▼         │
┌─────────────────┐
│ 建立会话        │
└─────────────────┘
```

### 14.2 Agent通讯安全

```java
@Service
public class A2AAdapter {
    
    @Autowired
    private AgentSecurityIntegration securityIntegration;
    
    public void sendA2AMessage(String fromAgentId, String toAgentId, String capability, Object payload) {
        if (!securityIntegration.checkAgentPermission(fromAgentId, "AGENT", toAgentId, "COMM")) {
            securityIntegration.logAgentCommunication(fromAgentId, toAgentId, capability, AuditResult.DENIED);
            throw new SecurityException("Agent communication not allowed");
        }
        
        doSendMessage(toAgentId, capability, payload);
        
        securityIntegration.logAgentCommunication(fromAgentId, toAgentId, capability, AuditResult.SUCCESS);
    }
}
```

---

## 十五、安全集成最佳实践

### 15.1 集成原则

1. **非侵入式**：安全集成不修改现有业务逻辑
2. **可配置**：安全策略可通过配置启用/禁用
3. **可审计**：所有安全操作记录审计日志
4. **可扩展**：支持自定义安全策略实现

### 15.2 集成检查清单

```
□ 场景创建时调用 setupSceneSecurity()
□ 场景销毁时调用 cleanupSceneSecurity()
□ 能力绑定时调用 checkCapabilityAccess()
□ LLM调用时调用 getLlmApiKey()
□ Agent注册时调用 registerAgent()
□ Agent认证时调用 authenticateAgent()
□ 所有安全操作记录审计日志
```

### 15.3 错误处理规范

```java
try {
    String key = securityIntegration.getLlmApiKey(provider, userId, sceneId);
    if (key == null) {
        log.warn("LLM key not available for provider: {}", provider);
        return fallbackResponse();
    }
    return doLlmCall(key, params);
} catch (SecurityException e) {
    securityIntegration.logLlmCall(userId, provider, model, 0, AuditResult.DENIED);
    throw new ServiceException("Security check failed", e);
}
```
