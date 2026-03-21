# 密钥管理与Agent组网 - 协作需求文档

## 一、项目概述

### 1.1 背景
当前密钥管理系统存在以下问题：
- 数据存储在内存中，重启后丢失
- 缺少入网审批流程
- 无法追踪密钥使用历史
- 无法监控实时在线状态

### 1.2 目标
建立完整的密钥管理与Agent组网体系，实现：
- 静态管理：新用户/新设备的手工批准入网
- 动态监控：用户登录和Agent组网时的密钥状态查看

### 1.3 SDK已完成内容 (v2.3.1)

| 模块 | 状态 | 包路径 | 说明 |
|------|------|--------|------|
| 密钥实体模型 | ✅ 已完成 | `net.ooder.sdk.api.security` | `KeyEntity` 统一模型 |
| 入网请求模型 | ✅ 已完成 | `net.ooder.sdk.api.security` | `NetworkJoinRequest` |
| 密钥管理服务 | ✅ 已完成 | `net.ooder.sdk.api.security` | `KeyManagementService` |
| 入网审批服务 | ✅ 已完成 | `net.ooder.sdk.api.security` | `NetworkJoinService` |
| 密钥规则服务 | ✅ 已完成 | `net.ooder.sdk.api.security` | `KeyRuleService` |
| 审计日志服务 | ✅ 已完成 | `net.ooder.sdk.api.security` | `KeyUsageLogService` |
| NexusService 集成 | ✅ 已完成 | `net.ooder.sdk.nexus` | 组网接口与密钥结合 |

### 1.4 职责划分

| 职责 | SDK团队 | SE团队 | MVP团队 |
|------|---------|--------|---------|
| SDK核心模型 | ✅ 已完成 | - | - |
| JSON持久化实现 | - | ✅ | - |
| Spring Boot集成 | - | ✅ | - |
| REST API接口 | - | ✅ | - |
| 前端页面开发 | - | ✅ | - |

**说明**: 根据SDK文档，SE团队负责完整的集成开发工作，包括持久化、API和前端页面。MVP团队负责需求定义和验收。

---

## 二、SE团队需求（存储与逻辑）

### 2.1 数据模型

#### 2.1.1 密钥实体 (KeyEntity)

```java
package net.ooder.sdk.key;

public class KeyEntity {
    private String keyId;              // 密钥ID
    private String keyValue;           // 密钥值（加密存储）
    private String keyName;            // 密钥名称
    private KeyType keyType;           // 密钥类型
    private KeyStatus status;          // 状态
    
    // 签发信息
    private String issuerId;           // 签发者ID
    private long issuedAt;             // 签发时间
    
    // 持有者信息
    private String ownerId;            // 持有者ID
    private OwnerType ownerType;       // 持有者类型(USER/AGENT/DEVICE)
    
    // 有效期
    private long expiresAt;            // 过期时间
    private int maxUseCount;           // 最大使用次数
    private int usedCount;             // 已使用次数
    
    // 授权范围
    private List<String> allowedScenes;    // 授权场景
    private List<String> allowedOperations; // 授权操作
    
    // 关联信息
    private String sceneGroupId;       // 关联场景组ID
    private String agentId;            // 关联Agent ID
    private String deviceId;           // 关联设备ID
    
    // 统计信息
    private long lastUsedAt;           // 最后使用时间
    private long createdAt;            // 创建时间
    private long updatedAt;            // 更新时间
}

public enum KeyType {
    SESSION_TOKEN,         // 会话令牌
    API_KEY,               // API密钥
    SCENE_ACCESS_KEY,      // 场景访问密钥
    DEVICE_KEY,            // 设备密钥
    AGENT_KEY              // Agent密钥
}

public enum KeyStatus {
    ACTIVE,                // 激活
    INACTIVE,              // 未激活
    EXPIRED,               // 已过期
    REVOKED,               // 已撤销
    SUSPENDED              // 已暂停
}

public enum OwnerType {
    USER,                  // 用户
    AGENT,                 // Agent
    DEVICE,                // 设备
    SCENE                  // 场景
}
```

#### 2.1.2 入网申请 (NetworkJoinRequest)

```java
package net.ooder.sdk.key;

public class NetworkJoinRequest {
    private String requestId;           // 申请ID
    private RequestType type;           // 申请类型
    private RequestStatus status;       // 申请状态
    
    // 申请人信息
    private String applicantId;         // 申请人ID
    private String applicantName;       // 申请人名称
    private String applicantEmail;      // 申请人邮箱
    private String department;          // 部门
    private String reason;              // 申请理由
    
    // 申请范围
    private List<String> requestedScenes; // 申请访问的场景
    
    // 密钥规则
    private KeyRule recommendedRule;    // 推荐密钥规则
    
    // 审批信息
    private String reviewerId;          // 审批人ID
    private String reviewerName;        // 审批人名称
    private String reviewComment;       // 审批意见
    private long reviewedAt;            // 审批时间
    
    // 时间信息
    private long createdAt;             // 创建时间
    private long updatedAt;             // 更新时间
}

public enum RequestType {
    USER_JOIN,              // 用户入网
    DEVICE_JOIN,            // 设备入网
    AGENT_JOIN              // Agent入网
}

public enum RequestStatus {
    PENDING,                // 待审批
    APPROVED,               // 已批准
    REJECTED,               // 已拒绝
    CANCELLED               // 已取消
}
```

#### 2.1.3 密钥规则 (KeyRule)

```java
package net.ooder.sdk.key;

public class KeyRule {
    private String ruleId;              // 规则ID
    private String ruleName;            // 规则名称
    private String description;         // 规则描述
    
    // 有效期设置
    private int validityDays;           // 有效天数
    private boolean autoExpire;         // 自动过期
    
    // 使用限制
    private int maxUseCount;            // 最大使用次数
    private int dailyUseLimit;          // 每日使用限制
    
    // 授权范围
    private List<String> allowedScenes;     // 允许的场景
    private List<String> allowedOperations; // 允许的操作
    private List<String> allowedRoles;      // 允许的角色
    
    // 安全设置
    private boolean requireApproval;        // 需要审批
    private boolean enableAudit;            // 启用审计
    private boolean enableAlert;            // 启用告警
    
    private long createdAt;
    private long updatedAt;
}
```

#### 2.1.4 密钥使用日志 (KeyUsageLog)

```java
package net.ooder.sdk.key;

public class KeyUsageLog {
    private String logId;               // 日志ID
    private String keyId;               // 密钥ID
    
    // 调用者信息
    private String callerId;            // 调用者ID
    private String callerName;          // 调用者名称
    private OwnerType callerType;       // 调用者类型
    
    // 操作信息
    private String action;              // 操作类型
    private String targetResource;      // 目标资源
    private Map<String, Object> params; // 操作参数
    
    // 结果信息
    private boolean success;            // 是否成功
    private String errorMessage;        // 错误信息
    private long duration;              // 耗时(ms)
    
    // 关联信息
    private String sessionId;           // 会话ID
    private String sceneGroupId;        // 场景组ID
    private String ipAddress;           // IP地址
    
    private long timestamp;             // 时间戳
}
```

### 2.2 SDK服务接口

#### 2.2.1 密钥管理服务 (KeyManagementService)

```java
package net.ooder.sdk.key;

public interface KeyManagementService {
    
    // 密钥生成与查询
    KeyEntity generateKey(KeyGenerateRequest request);
    KeyEntity getKey(String keyId);
    KeyEntity getKeyByValue(String keyValue);
    List<KeyEntity> getKeysByOwner(String ownerId, OwnerType ownerType);
    List<KeyEntity> getKeysByScene(String sceneGroupId);
    List<KeyEntity> getAllKeys(KeyQueryRequest request);
    
    // 密钥验证
    KeyValidationResult validateKey(String keyId, String scope);
    KeyValidationResult validateKeyByValue(String keyValue, String scope);
    
    // 密钥操作
    boolean revokeKey(String keyId);
    boolean suspendKey(String keyId);
    boolean activateKey(String keyId);
    KeyEntity refreshKey(String keyId);
    
    // 密钥访问
    KeyAccessResult accessResource(String keyId, String resource, String action);
    void recordUsage(KeyUsageLog log);
    
    // 统计信息
    KeyStats getKeyStats();
    List<KeyUsageLog> getUsageLogs(String keyId, int limit);
}
```

#### 2.2.2 入网审批服务 (NetworkJoinService)

```java
package net.ooder.sdk.key;

public interface NetworkJoinService {
    
    // 申请管理
    NetworkJoinRequest createRequest(NetworkJoinRequest request);
    NetworkJoinRequest getRequest(String requestId);
    List<NetworkJoinRequest> getPendingRequests();
    List<NetworkJoinRequest> getRequestsByStatus(RequestStatus status);
    List<NetworkJoinRequest> getRequestsByApplicant(String applicantId);
    
    // 审批操作
    NetworkJoinRequest approve(String requestId, String reviewerId, String comment, KeyRule rule);
    NetworkJoinRequest reject(String requestId, String reviewerId, String comment);
    boolean cancelRequest(String requestId);
    
    // 统计
    int getPendingCount();
    Map<RequestStatus, Integer> getCountByStatus();
}
```

#### 2.2.3 密钥规则服务 (KeyRuleService)

```java
package net.ooder.sdk.key;

public interface KeyRuleService {
    
    // 规则管理
    KeyRule createRule(KeyRule rule);
    KeyRule getRule(String ruleId);
    List<KeyRule> getAllRules();
    KeyRule updateRule(KeyRule rule);
    boolean deleteRule(String ruleId);
    
    // 规则应用
    KeyEntity applyRule(String keyId, String ruleId);
    KeyRule getRecommendedRule(RequestType requestType, String sceneGroupId);
}
```

---

## 三、MVP团队需求（API与展现）

### 3.1 API接口规范

#### 3.1.1 密钥管理API

```
基础路径: /api/v1/keys

GET    /                           # 获取密钥列表
GET    /{keyId}                    # 获取密钥详情
POST   /                           # 创建密钥
POST   /{keyId}/revoke             # 撤销密钥
POST   /{keyId}/suspend            # 暂停密钥
POST   /{keyId}/activate           # 激活密钥
POST   /{keyId}/refresh            # 刷新密钥
POST   /{keyId}/validate           # 验证密钥
GET    /{keyId}/usage-logs         # 获取使用日志
GET    /stats                      # 获取统计信息
```

#### 3.1.2 入网审批API

```
基础路径: /api/v1/network-join

GET    /requests                   # 获取申请列表
GET    /requests/{requestId}       # 获取申请详情
POST   /requests                   # 创建申请
POST   /requests/{requestId}/approve  # 批准入网
POST   /requests/{requestId}/reject   # 拒绝申请
DELETE /requests/{requestId}       # 取消申请
GET    /pending-count              # 获取待审批数量
```

#### 3.1.3 密钥规则API

```
基础路径: /api/v1/key-rules

GET    /                           # 获取规则列表
GET    /{ruleId}                   # 获取规则详情
POST   /                           # 创建规则
PUT    /{ruleId}                   # 更新规则
DELETE /{ruleId}                   # 删除规则
GET    /recommended                # 获取推荐规则
```

### 3.2 前端页面需求

#### 3.2.1 页面清单

| 页面 | 路径 | 功能 |
|------|------|------|
| 密钥管理 | /console/pages/key-management.html | 密钥列表、创建、详情 |
| 入网审批 | /console/pages/network-approval.html | 审批列表、审批操作 |
| 密钥规则 | /console/pages/key-rules.html | 规则配置 |
| 实时监控 | /console/pages/key-monitor.html | 在线状态、活动日志 |

#### 3.2.2 密钥管理页面功能

**统计卡片**
- 总密钥数
- 激活密钥数
- 今日使用次数
- 即将过期数量

**筛选功能**
- 按类型筛选
- 按状态筛选
- 按持有者筛选
- 关键词搜索

**密钥列表**
- 密钥名称、类型、状态
- 持有者信息
- 有效期、使用次数
- 操作按钮（详情、撤销、刷新）

**密钥详情弹窗**
- 基本信息
- 关联关系图（签发者、持有者、场景、Session）
- 使用历史列表

#### 3.2.3 入网审批页面功能

**统计卡片**
- 待审批数量
- 今日已批准
- 今日已拒绝

**申请列表**
- 申请人信息
- 申请类型（用户/设备/Agent）
- 申请时间、申请理由
- 推荐密钥规则
- 操作按钮（批准、拒绝）

**审批弹窗**
- 申请详情
- 密钥规则选择
- 有效期设置
- 授权场景选择
- 审批意见输入

#### 3.2.4 实时监控页面功能

**在线状态**
- 在线用户数
- 活跃Agent数
- 活跃设备数
- 活跃场景数

**网络拓扑图**
- 节点展示（用户、Agent、设备、场景）
- 连接关系
- 状态颜色（在线、离线、繁忙）

**实时活动日志**
- 时间、实体、事件、结果
- 自动刷新

---

## 四、接口数据格式

### 4.1 统一响应格式

```json
{
    "status": "success",
    "code": 200,
    "message": "操作成功",
    "data": { ... },
    "timestamp": 1705312800000
}
```

### 4.2 分页响应格式

```json
{
    "status": "success",
    "code": 200,
    "data": {
        "items": [ ... ],
        "total": 100,
        "page": 1,
        "pageSize": 20
    }
}
```

### 4.3 错误响应格式

```json
{
    "status": "error",
    "code": 400,
    "message": "参数错误",
    "errors": [
        { "field": "keyName", "message": "密钥名称不能为空" }
    ]
}
```

---

## 五、开发计划

### 5.1 里程碑（基于SDK v2.3.1已完成）

| 阶段 | 内容 | 负责团队 | 预计时间 |
|------|------|----------|----------|
| Phase 1 | JSON持久化集成 | SE | 1天 |
| Phase 2 | Spring Boot集成 | SE | 0.5天 |
| Phase 3 | REST API接口开发 | SE | 1天 |
| Phase 4 | 前端页面开发 | SE | 2天 |
| Phase 5 | 测试与文档 | SE | 1天 |

**总计**: 5.5人天

### 5.2 依赖关系

```
SDK v2.3.1 (已完成)
    │
    ├── KeyEntity, NetworkJoinRequest, KeyRule, KeyUsageLog
    ├── KeyManagementService, NetworkJoinService, KeyRuleService
    └── NexusService (组网接口与密钥结合)
            │
            ▼
SE团队集成工作
    │
    ├── JSON持久化 → Spring Boot集成 → REST API → 前端页面
    │
    ▼
MVP团队验收
```

---

## 六、验收标准

### 6.1 SE团队交付物

- [ ] JSON持久化功能正常
- [ ] Spring Boot自动配置生效
- [ ] REST API接口完整
- [ ] 前端页面功能完整
- [ ] 单元测试覆盖率 > 70%
- [ ] API文档完整

### 6.2 MVP团队验收标准

- [ ] 密钥管理功能满足需求
- [ ] 入网审批流程完整
- [ ] 前端交互流畅
- [ ] 无严重Bug

---

## 七、配置说明

### 7.1 Maven依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 7.2 Spring Boot配置

```yaml
scene:
  engine:
    key:
      storage:
        root: data/keys  # JSON存储根目录
        
ooder:
  security:
    key-management:
      enabled: true
      default-expires-in-seconds: 86400
      default-max-use-count: 1000
```

---

## 八、参考文档

- [SDK密钥管理集成开发说明](E:\github\ooder-sdk\scene-engine\docs\SDK密钥管理集成开发说明.md)
- [JsonStorageService实现](file:///e:/github/ooder-sdk/scene-engine/src/main/java/net/ooder/scene/skill/engine/context/impl/JsonStorageService.java)
- [FileConversationStorageService实现](file:///e:/github/ooder-sdk/scene-engine/src/main/java/net/ooder/scene/skill/conversation/storage/impl/FileConversationStorageService.java)

---

## 九、联系方式

- SDK团队: [待定]
- SE团队负责人: [待定]
- MVP团队负责人: [待定]
- 协作沟通渠道: [待定]
