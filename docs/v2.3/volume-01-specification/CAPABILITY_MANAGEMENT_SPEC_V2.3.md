# 能力管理需求规格说明书 v2.3

> **文档版本**: v2.3  
> **发布日期**: 2026-03-02  
> **文档状态**: 正式发布  
> **适用范围**: skill-scene模块、agent-sdk模块  
> **术语版本**: GLOSSARY_V2.md

---

## 一、概述

### 1.1 文档目的

本文档定义了 ooder 能力管理系统的需求规格，基于**能力驱动架构**，包括：
- 能力的定义、分类和属性
- 能力类型体系（ATOMIC/COMPOSITE/SCENE/DRIVER/COLLABORATIVE）
- 能力实例化机制（Agent + Link + Address）
- 能力驱动机制（mainFirst）
- 能力调用链与涌现能力
- 能力的生命周期管理

### 1.2 设计原则

| 原则 | 说明 |
|------|------|
| **能力独立性** | 能力是独立实体，拥有全局唯一标识，可跨场景复用 |
| **能力驱动** | 场景能力通过 mainFirst 自驱入口实现自检、自启、自驱 |
| **能力组合** | 能力可嵌套组合，形成 COMPOSITE_CAPABILITY 和 SCENE_CAPABILITY |
| **能力涌现** | 场景能力协调子能力产生涌现行为 |
| **实例化绑定** | 能力实例化 = Agent + Link + Address，场景内唯一 |
| **权限隔离** | 通过CAP地址区域和Link类型实现权限控制 |

### 1.3 术语定义

本文档使用的核心术语请参考 [术语表v2](GLOSSARY_V2.md)，以下仅列出能力域特有术语：

| 术语 | 英文标识 | 定义 |
|------|----------|------|
| **原子能力** | ATOMIC_CAPABILITY | 单一功能、不可分解的能力 |
| **组合能力** | COMPOSITE_CAPABILITY | 组合多个原子能力的能力 |
| **场景能力** | SCENE_CAPABILITY | 自驱型SuperAgent能力 |
| **驱动能力** | DRIVER_CAPABILITY | 提供驱动源头的特殊能力类型 |
| **协作能力** | COLLABORATIVE_CAPABILITY | 跨场景协作的能力 |
| **自驱入口** | mainFirst | 场景能力的启动入口 |

---

## 二、能力类型体系

### 2.1 能力类型枚举

```java
public enum CapabilityType {
    // 基础能力类型
    ATOMIC("ATOMIC", "原子能力", "单一功能，不可分解"),
    COMPOSITE("COMPOSITE", "组合能力", "组合多个原子能力"),
    
    // 核心能力类型
    SCENE("SCENE", "场景能力", "自驱型SuperAgent能力"),
    DRIVER("DRIVER", "驱动能力", "意图/时间/事件驱动"),
    COLLABORATIVE("COLLABORATIVE", "协作能力", "跨场景协作能力"),
    
    // 业务能力类型
    SERVICE("SERVICE", "服务能力", "业务服务、API服务"),
    AI("AI", "AI能力", "LLM、机器学习"),
    TOOL("TOOL", "工具能力", "工具类功能"),
    CONNECTOR("CONNECTOR", "连接器能力", "连接协议类"),
    DATA("DATA", "数据能力", "数据存储、处理"),
    CUSTOM("CUSTOM", "自定义能力", "用户自定义类型");
}
```

### 2.2 能力类型层次

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力类型层次结构                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   第一层：原子能力（ATOMIC_CAPABILITY）                                      │
│   ════════════════════════════════════                                      │
│   ├── 单一功能，不可分解                                                    │
│   ├── 无子能力                                                              │
│   └── 示例：email-send, file-read, http-request                            │
│                                                                             │
│   第二层：组合能力（COMPOSITE_CAPABILITY）                                   │
│   ══════════════════════════════════════                                    │
│   ├── 组合多个原子能力                                                      │
│   ├── 无涌现行为                                                            │
│   └── 示例：notification-chain, data-pipeline                              │
│                                                                             │
│   第三层：场景能力（SCENE_CAPABILITY）                                       │
│   ══════════════════════════════════════                                    │
│   ├── 自驱型SuperAgent                                                      │
│   ├── mainFirst 入口                                                        │
│   ├── 协调子能力                                                            │
│   ├── 涌现新行为                                                            │
│   ├── 可启动协作能力                                                        │
│   └── 示例：scene-daily-report, scene-smart-home                           │
│                                                                             │
│   第四层：驱动能力（DRIVER_CAPABILITY）                                      │
│   ══════════════════════════════════════                                    │
│   ├── intent-receiver: 接收意图                                             │
│   ├── scheduler: 时间驱动                                                   │
│   ├── event-listener: 事件监听                                              │
│   ├── capability-invoker: 能力调用                                          │
│   └── collaboration-coordinator: 协作协调                                   │
│                                                                             │
│   第五层：协作能力（COLLABORATIVE_CAPABILITY）                               │
│   ═══════════════════════════════════════════                               │
│   ├── 跨场景协作能力                                                        │
│   ├── 通过接口暴露                                                          │
│   └── 示例：indexing-service, llm-analysis-service                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、能力定义

### 3.1 能力基础模型

```java
public class Capability {
    // 基本属性
    private String capabilityId;          // 全局唯一标识
    private String name;                  // 能力名称
    private CapabilityType type;          // 能力类型
    private String version;               // 版本号
    private String description;           // 描述
    
    // 状态管理
    private CapabilityStatus status;      // 状态
    private boolean available;            // 是否可用
    
    // 配置与地址
    private Map<String, Object> config;   // 配置参数
    private CapAddress address;           // CAP地址
    
    // 关联信息
    private String skillId;               // 所属Skill
    private List<String> tags;            // 标签
    private List<String> supportedSceneTypes;  // 支持的场景类型
    
    // 能力嵌套（新增）
    private List<String> capabilities;    // 子能力ID列表
    private boolean mainFirst;            // 自驱入口标识
    private MainFirstConfig mainFirstConfig;  // 自驱配置
    
    // 协作能力（新增）
    private List<CollaborativeCapabilityRef> collaborativeCapabilities;
    
    // 时间戳
    private long registeredTime;
    private long lastHeartbeat;
}
```

### 3.2 能力ID体系

| ID类型 | 用途 | 示例 | 说明 |
|--------|------|------|------|
| **capabilityId** | 全局唯一标识 | `email-access-001` | 能力的全局唯一ID，跨场景使用 |
| **capId** | 场景内短ID | `daily-report-email` | 通过 `scenePrefix + functionName` 生成 |
| **capDefId** | 模板能力需求ID | `email-access` | 场景能力中定义的能力需求ID |

---

## 四、能力驱动机制

### 4.1 mainFirst 自驱入口

```java
public class MainFirstConfig {
    // 自检配置
    private List<CheckStep> selfCheck;
    
    // 自启配置
    private List<StartStep> selfStart;
    
    // 自驱配置
    private DriveConfig selfDrive;
    
    // 协作启动配置
    private List<CollaborationStep> startCollaboration;
}
```

### 4.2 自驱流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        mainFirst 自驱流程                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Phase 1: selfCheck（自检）                                                │
│   ────────────────────────                                                  │
│   ├── checkCapabilities: 检查子能力就绪                                     │
│   ├── checkDriverCapabilities: 检查驱动能力就绪                             │
│   └── checkCollaborative: 检查协作能力可用                                  │
│                                                                             │
│   Phase 2: selfStart（自启）                                                │
│   ────────────────────────                                                  │
│   ├── initDriverCapabilities: 初始化驱动能力                                │
│   ├── initCapabilities: 初始化子能力                                        │
│   └── bindAddresses: 绑定能力地址                                           │
│                                                                             │
│   Phase 3: startCollaboration（启动协作）                                   │
│   ────────────────────────────────────────                                  │
│   ├── startScene: 启动协作场景能力                                          │
│   └── bindInterface: 绑定协作接口                                           │
│                                                                             │
│   Phase 4: selfDrive（自驱）                                                │
│   ────────────────────────                                                  │
│   ├── scheduleRules: 时间驱动规则                                           │
│   ├── eventRules: 事件驱动规则                                              │
│   └── capabilityChains: 能力调用链                                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.3 驱动能力详解

#### 4.3.1 intent-receiver（意图接收）

```java
public interface IntentReceiver {
    CompletableFuture<IntentResult> receive(Intent intent);
    CompletableFuture<IntentResult> parse(String naturalLanguage);
    CompletableFuture<SceneCapability> resolveCapability(Intent intent);
}
```

#### 4.3.2 scheduler（时间驱动）

```java
public interface SchedulerCapability {
    CompletableFuture<Void> schedule(String cron, String action);
    CompletableFuture<Void> cancel(String scheduleId);
    CompletableFuture<List<ScheduleInfo>> listSchedules();
    void addScheduleListener(ScheduleListener listener);
}
```

#### 4.3.3 event-listener（事件监听）

```java
public interface EventListenerCapability {
    CompletableFuture<Void> subscribe(String eventType, EventFilter filter);
    CompletableFuture<Void> unsubscribe(String subscriptionId);
    CompletableFuture<List<Subscription>> listSubscriptions();
    void addEventListener(EventListener listener);
}
```

#### 4.3.4 capability-invoker（能力调用）

```java
public interface CapabilityInvoker {
    CompletableFuture<InvokeResult> invoke(String capabilityId, Map<String, Object> params);
    CompletableFuture<ChainResult> invokeChain(String chainId, Map<String, Object> params);
}
```

#### 4.3.5 collaboration-coordinator（协作协调）

```java
public interface CollaborationCoordinator {
    CompletableFuture<Void> startCollaboration(String sceneCapabilityId);
    CompletableFuture<Void> stopCollaboration(String sceneCapabilityId);
    CompletableFuture<List<CollaborationStatus>> getCollaborationStatus();
}
```

---

## 五、能力调用链

### 5.1 调用链定义

```yaml
capabilityChains:
  chain-name:
    - capability: capability-id
      input:
        key: value
      condition: "expression"
      onError: continue | stop
      
    - capability: another-capability
      input:
        data: "${previous.result}"
```

### 5.2 调用链服务

```java
public interface CapabilityChainService {
    // 链定义
    CompletableFuture<ChainDefinition> createChain(ChainConfig config);
    CompletableFuture<Void> deleteChain(String chainId);
    CompletableFuture<ChainDefinition> getChain(String chainId);
    CompletableFuture<List<ChainDefinition>> listChains();
    
    // 链执行
    CompletableFuture<ChainResult> executeChain(String chainId, Map<String, Object> input);
    CompletableFuture<ChainResult> executeChainAsync(String chainId, Map<String, Object> input, ChainCallback callback);
}
```

---

## 六、涌现能力

### 6.1 涌现机制

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力涌现机制                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   场景能力协调子能力产生涌现行为                                              │
│   ══════════════════════════════════                                        │
│                                                                             │
│   示例：日志汇报场景能力                                                     │
│   ───────────────────────                                                   │
│   子能力：                                                                   │
│   ├── report-remind: 日志提醒                                               │
│   ├── report-submit: 日志提交                                               │
│   ├── report-aggregate: 日志汇总                                            │
│   └── report-analyze: 日志分析                                              │
│                                                                             │
│   涌现行为：                                                                 │
│   └── 自动化日志汇报流程                                                     │
│       ├── 定时提醒员工                                                      │
│       ├── 自动汇总日志                                                      │
│       ├── AI分析内容                                                        │
│       └── 发送结果给领导                                                    │
│                                                                             │
│   涌现公式：                                                                 │
│   SceneCapability + 子能力协调 + 驱动能力 = 涌现行为                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 涌现能力接口

```java
public interface EmergenceService {
    CompletableFuture<EmergenceResult> analyzeEmergence(String sceneCapabilityId);
    CompletableFuture<List<EmergentBehavior>> detectEmergentBehaviors(String sceneCapabilityId);
}
```

---

## 七、能力实例化

### 7.1 实例化模型

```
能力实例 = Agent + Link + Address

┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力实例化模型                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Capability (能力定义)                                                      │
│        │                                                                    │
│        │ 实例化                                                              │
│        ▼                                                                    │
│   CapabilityBinding (能力绑定)                                              │
│   ├── bindingId: 绑定关系ID                                                 │
│   ├── capabilityId + capId: 双ID映射                                       │
│   ├── agentId: 执行者                                                       │
│   ├── linkId: 通信链路                                                      │
│   └── capAddress: CAP地址                                                   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 7.2 CAP地址

```java
public class CapAddress {
    private String zone;        // 区域：SYSTEM/GENERAL/EXTENSION
    private String domain;      // 域标识
    private String scenePrefix; // 场景前缀
    private String functionName;// 功能名称
}
```

---

## 八、能力生命周期管理

### 8.1 生命周期状态

```
REGISTERED → ACTIVE → DISABLED → UNREGISTERED
                  │
                  ▼
               DEPRECATED
```

### 8.2 状态说明

| 状态 | 说明 |
|------|------|
| REGISTERED | 已注册，未启用 |
| ACTIVE | 活跃，可调用 |
| DISABLED | 已禁用 |
| DEPRECATED | 已废弃 |
| UNREGISTERED | 已注销 |

---

## 九、能力权限控制

### 9.1 CAP地址区域

| 区域 | 地址范围 | 权限级别 | 用途 |
|------|----------|----------|------|
| SYSTEM | 0x0000-0x00FF | 最高 | 系统核心能力 |
| GENERAL | 0x0100-0x7FFF | 中等 | 通用业务能力 |
| EXTENSION | 0x8000-0xFFFF | 普通 | 扩展能力 |

### 9.2 访问级别

```java
public enum AccessLevel {
    PRIVATE,    // 私有：仅所有者及其授权Agent可访问
    PROTECTED,  // 保护：同一场景内的Agent可访问
    PUBLIC,     // 公开：所有Agent可访问
    SCOPED      // 作用域：指定范围内的Agent可访问
}
```

---

## 十、API接口规范

### 10.1 能力管理API

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/v1/capabilities` | GET | 列出能力 |
| `/api/v1/capabilities/{id}` | GET | 获取能力详情 |
| `/api/v1/capabilities` | POST | 注册能力 |
| `/api/v1/capabilities/{id}` | PUT | 更新能力 |
| `/api/v1/capabilities/{id}` | DELETE | 注销能力 |
| `/api/v1/capabilities/{id}/status` | POST | 更新能力状态 |

### 10.2 能力绑定API

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/v1/capabilities/bindings` | POST | 创建能力绑定 |
| `/api/v1/capabilities/bindings/{id}` | GET | 获取绑定详情 |
| `/api/v1/capabilities/bindings/{id}` | DELETE | 删除绑定 |

### 10.3 能力调用链API

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/v1/capability-chains` | GET | 列出能力链 |
| `/api/v1/capability-chains` | POST | 创建能力链 |
| `/api/v1/capability-chains/{id}/execute` | POST | 执行能力链 |

---

## 十一、用户故事

### 11.1 故事背景

```
角色：普通员工
需求：每天下班前上报工作日志
场景能力：日志汇报场景能力

员工需要的能力：
├── 邮件能力：发送工作日志到领导邮箱
└── GIT日志能力：读取GIT提交记录作为日志内容
```

### 11.2 能力使用完整流程

```
阶段一：意图接收
────────────────
intent-receiver 接收用户意图 "我要创建日志汇报场景能力"
    │
    ▼
阶段二：场景能力自启
────────────────────
mainFirst.selfCheck() → mainFirst.selfStart()
    │
    ▼
阶段三：驱动能力运行
────────────────────
scheduler 监听时间 → event-listener 监听事件
    │
    ▼
阶段四：能力调用链执行
──────────────────────
capability-invoker 执行能力调用链
    │
    ├── report-remind: 提醒员工
    ├── report-submit: 接收提交
    ├── report-aggregate: 汇总日志
    └── report-analyze: AI分析
```

---

## 十二、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-03-01 | 初始版本 |
| 1.1 | 2026-03-01 | 新增用户故事、能力发现渠道、界面流程 |
| 1.2 | 2026-03-01 | 新增涌现能力、版本管理、依赖管理、监控统计、安全审计 |
| 1.3 | 2026-03-01 | 完善术语定义，新增9类术语分类 |
| 1.4 | 2026-03-01 | 引用统一术语表，精简文档内术语定义 |
| 1.5 | 2026-03-02 | 新增零配置安装用户故事 |
| v2.3 | 2026-03-02 | **重大升级**：采用能力驱动架构，新增能力类型体系（ATOMIC/COMPOSITE/SCENE/DRIVER/COLLABORATIVE），新增mainFirst自驱机制，整合南向协议术语 |

---

**文档维护者**: ooder开发团队  
**最后更新**: 2026-03-02
