# 安全控制模块需求规格说明

> **版本**: v1.0  
> **日期**: 2026-03-01  
> **状态**: 需求分析

---

## 一、背景与目标

### 1.1 背景

Ooder平台涉及三个核心模块的安全配置管理：
- **场景模块** - 多Agent协作场景的安全策略
- **SKILLS模块** - 技能包的安全加载与执行
- **LLM模块** - 大语言模型API密钥管理

当前各模块安全配置分散，缺乏统一管理入口，存在以下问题：
1. API Key明文存储或分散在环境变量中
2. Agent间通讯缺乏统一认证机制
3. 场景隔离策略不完善
4. 审计日志分散，难以追踪

### 1.2 目标

建立统一的安全控制模块，实现：
1. **统一密钥管理** - 集中管理各类API Key、Token
2. **访问控制** - 基于角色的资源访问控制
3. **通讯安全** - Agent间通讯加密与认证
4. **审计追踪** - 统一的安全审计日志

---

## 二、现有安全能力分析

### 2.1 已有安全模块 (skill-security)

| 组件 | 功能 | 状态 |
|------|------|------|
| SecurityApi | 安全管理API接口 | 已实现 |
| SecurityPolicy | 安全策略管理 | 已实现 |
| AccessControl | 访问控制列表 | 已实现 |
| ThreatInfo | 威胁检测与告警 | 已实现 |
| FirewallRule | 防火墙规则 | 已实现 |
| AuditLog | 审计日志 | 已实现 |

### 2.2 已有KEY管理

| 组件 | 用途 | 存储方式 |
|------|------|---------|
| KeyInfo | 密钥信息DTO | 内存/数据库 |
| SecurityToken | 安全令牌 | 内存/数据库 |
| KeyShareDTO | 场景密钥共享 | 场景配置 |
| OPENAI_API_KEY | OpenAI密钥 | 环境变量 |

### 2.3 Agent通讯机制

| 组件 | 协议 | 安全机制 |
|------|------|---------|
| P2PNodeManager | P2P | 节点发现 |
| MqttSkillController | MQTT | 可配置认证 |
| A2AAdapter | Agent-to-Agent | 待完善 |

---

## 三、用户故事

### 3.1 密钥管理用户故事

#### US-KEY-001: 统一密钥存储
**作为** 系统管理员  
**我希望** 能够在一个统一的位置管理所有API密钥  
**以便于** 集中管理、轮换和撤销密钥

**验收标准**:
- 支持添加/编辑/删除密钥
- 密钥加密存储
- 支持密钥过期设置
- 支持密钥使用统计

#### US-KEY-002: 密钥分类管理
**作为** 系统管理员  
**我希望** 能够按类型分类管理密钥  
**以便于** 区分不同服务的密钥

**密钥类型**:
| 类型 | 说明 | 示例 |
|------|------|------|
| LLM_API_KEY | 大模型API密钥 | OpenAI, 通义千问 |
| CLOUD_API_KEY | 云服务API密钥 | 阿里云, 腾讯云 |
| DATABASE_KEY | 数据库连接密钥 | MySQL, Redis |
| SERVICE_TOKEN | 服务间调用令牌 | JWT, OAuth |
| AGENT_KEY | Agent身份密钥 | Agent认证 |

#### US-KEY-003: 密钥权限控制
**作为** 安全管理员  
**我希望** 能够控制哪些用户/角色可以使用特定密钥  
**以便于** 实现最小权限原则

**验收标准**:
- 支持按用户授权
- 支持按角色授权
- 支持按场景授权
- 密钥使用需审批（可选）

#### US-KEY-004: 密钥使用审计
**作为** 安全审计员  
**我希望** 能够查看密钥的使用记录  
**以便于** 追踪异常使用和安全审计

**审计内容**:
- 使用时间
- 使用者
- 使用场景
- 调用结果

### 3.2 Agent安全用户故事

#### US-AGENT-001: Agent身份认证
**作为** 系统管理员  
**我希望** 每个Agent都有唯一的身份标识  
**以便于** 识别和管控Agent行为

**验收标准**:
- Agent注册时生成唯一ID和密钥对
- Agent通讯需身份验证
- 支持Agent证书管理

#### US-AGENT-002: Agent通讯加密
**作为** 安全管理员  
**我希望** Agent之间的通讯是加密的  
**以便于** 防止数据泄露

**加密要求**:
- TLS/SSL传输加密
- 消息体加密
- 端到端加密（敏感数据）

#### US-AGENT-003: Agent权限控制
**作为** 系统管理员  
**我希望** 能够控制Agent可访问的资源  
**以便于** 限制Agent权限范围

**权限类型**:
| 权限 | 说明 |
|------|------|
| READ | 读取资源 |
| WRITE | 写入资源 |
| EXECUTE | 执行操作 |
| DELEGATE | 委托权限 |

### 3.3 场景安全用户故事

#### US-SCENE-001: 场景隔离策略
**作为** 场景管理员  
**我希望** 不同场景之间的数据是隔离的  
**以便于** 保护数据安全

**隔离策略**:
- 数据隔离规则
- 跨组织访问规则
- 敏感数据脱敏

#### US-SCENE-002: 场景密钥共享
**作为** 场景创建者  
**我希望** 能够安全地与场景参与者共享必要的密钥  
**以便于** 场景正常运行

**共享机制**:
- 密钥分片存储
- 按需解密
- 使用后自动清除

### 3.4 LLM安全用户故事

#### US-LLM-001: LLM密钥安全配置
**作为** 系统管理员  
**我希望** LLM API密钥安全存储且可配置  
**以便于** 安全使用大模型服务

**配置项**:
| 配置项 | 说明 |
|--------|------|
| apiKey | 加密存储的API密钥 |
| baseUrl | API服务地址 |
| model | 默认模型 |
| rateLimit | 调用频率限制 |
| costLimit | 费用限制 |

#### US-LLM-002: LLM调用审计
**作为** 安全审计员  
**我希望** 记录所有LLM调用详情  
**以便于** 成本控制和安全审计

**审计内容**:
- 调用时间
- 调用者
- 模型类型
- Token消耗
- 费用

---

## 四、KEY管理详细需求

### 4.1 密钥生命周期

```
创建 → 存储 → 授权 → 使用 → 轮换 → 撤销 → 销毁
```

| 阶段 | 操作 | 说明 |
|------|------|------|
| 创建 | generateKey() | 生成或导入密钥 |
| 存储 | storeKey() | 加密存储密钥 |
| 授权 | grantAccess() | 授权用户/角色使用 |
| 使用 | useKey() | 应用获取密钥使用 |
| 轮换 | rotateKey() | 定期更换密钥 |
| 撤销 | revokeKey() | 立即禁用密钥 |
| 销毁 | destroyKey() | 永久删除密钥 |

### 4.2 密钥存储结构

```java
public class ApiKeyDTO {
    private String keyId;           // 密钥ID
    private String keyName;         // 密钥名称
    private String keyType;         // 密钥类型
    private String provider;        // 服务提供商
    private String encryptedValue;  // 加密后的密钥值
    private String status;          // 状态: ACTIVE, INACTIVE, EXPIRED
    private Date createdAt;         // 创建时间
    private Date expiresAt;         // 过期时间
    private Date lastUsedAt;        // 最后使用时间
    private int useCount;           // 使用次数
    private List<String> allowedUsers;  // 授权用户
    private List<String> allowedRoles;  // 授权角色
    private Map<String, Object> metadata; // 扩展信息
}
```

### 4.3 密钥加密方案

| 方案 | 说明 | 适用场景 |
|------|------|---------|
| AES-256 | 对称加密 | 密钥值存储 |
| RSA-2048 | 非对称加密 | 密钥传输 |
| SHA-256 | 哈希 | 密钥校验 |

### 4.4 密钥访问API

| API | 方法 | 说明 |
|-----|------|------|
| /api/v1/keys | GET | 列出密钥（脱敏） |
| /api/v1/keys | POST | 创建密钥 |
| /api/v1/keys/{keyId} | GET | 获取密钥详情 |
| /api/v1/keys/{keyId} | PUT | 更新密钥 |
| /api/v1/keys/{keyId} | DELETE | 删除密钥 |
| /api/v1/keys/{keyId}/use | POST | 使用密钥 |
| /api/v1/keys/{keyId}/rotate | POST | 轮换密钥 |
| /api/v1/keys/{keyId}/revoke | POST | 撤销密钥 |

---

## 五、安全策略配置

### 5.1 系统级安全配置

```java
public class SystemSecurityConfig {
    private boolean enableAuth;         // 启用认证
    private boolean enableEncryption;   // 启用加密
    private boolean enableAudit;        // 启用审计
    private int sessionTimeout;         // 会话超时(分钟)
    private int maxLoginAttempts;       // 最大登录尝试
    private int lockoutDuration;        // 锁定时长(分钟)
    private int keyRotationDays;        // 密钥轮换周期
    private boolean enforceMfa;         // 强制多因素认证
}
```

### 5.2 场景级安全配置

```java
public class SceneSecurityConfig {
    private String sceneId;             // 场景ID
    private boolean dataIsolation;      // 数据隔离
    private boolean crossOrgAllowed;    // 允许跨组织
    private List<String> allowedAgents; // 允许的Agent
    private List<String> allowedApis;   // 允许的API
    private int maxConcurrency;         // 最大并发
    private AuditLevel auditLevel;      // 审计级别
}
```

---

## 六、与现有模块集成

### 6.1 与场景模块集成

```
场景创建 → 自动创建安全策略
场景启动 → 加载相关密钥
场景执行 → 审计日志记录
场景结束 → 清理临时密钥
```

### 6.2 与LLM模块集成

```
LLM调用请求 → 密钥管理器获取密钥
密钥验证 → 检查权限和状态
调用LLM → 记录审计日志
返回结果 → 更新使用统计
```

### 6.3 与Agent模块集成

```
Agent注册 → 生成Agent密钥对
Agent认证 → 验证身份和权限
Agent通讯 → 加密传输
Agent注销 → 撤销相关密钥
```

---

## 七、非功能性需求

### 7.1 安全性

| 要求 | 说明 |
|------|------|
| 密钥加密 | 所有密钥必须加密存储 |
| 传输加密 | 所有网络传输使用TLS |
| 访问控制 | 基于RBAC的访问控制 |
| 审计追踪 | 所有操作可追溯 |

### 7.2 性能

| 指标 | 要求 |
|------|------|
| 密钥获取延迟 | < 10ms |
| 加密/解密延迟 | < 5ms |
| 审计日志写入 | < 20ms |

### 7.3 可用性

| 要求 | 说明 |
|------|------|
| 高可用 | 支持主备切换 |
| 备份 | 每日自动备份 |
| 恢复 | 支持密钥恢复 |

---

## 八、技术约束

1. **Java版本**: 必须支持Java 8
2. **加密库**: 使用Java内置加密库或Bouncy Castle
3. **存储**: 支持内存存储和数据库存储
4. **兼容性**: 与现有skill-security模块兼容

---

## 九、术语表

| 术语 | 说明 |
|------|------|
| API Key | 用于调用外部API的密钥 |
| Token | 用于身份认证的令牌 |
| Agent | 智能代理 |
| Scene | 多Agent协作场景 |
| RBAC | 基于角色的访问控制 |
| MFA | 多因素认证 |
