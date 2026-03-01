# 能力管理需求规格说明书

> **文档版本**: 1.4  
> **创建日期**: 2026-03-01  
> **文档状态**: 正式发布  
> **适用范围**: skill-scene模块、agent-sdk模块

---

## 目录

1. [概述](#一概述)
2. [核心概念](#二核心概念)
3. [能力定义](#三能力定义)
4. [能力实例化](#四能力实例化)
5. [能力与场景集成](#五能力与场景集成)
6. [能力调用与路由](#六能力调用与路由)
7. [能力生命周期管理](#七能力生命周期管理)
8. [能力权限控制](#八能力权限控制)
9. [能力故障处理](#九能力故障处理)
10. [API接口规范](#十api接口规范)
11. [数据模型](#十一数据模型)
12. [附录](#十二附录)
13. [用户故事](#十三用户故事)
14. [能力发现渠道](#十四能力发现渠道)
15. [界面流程设计](#十五界面流程设计)
16. [能力包元数据](#十六能力包元数据)
17. [能力发现API](#十七能力发现api)
18. [涌现能力设计](#十八涌现能力设计)
19. [能力版本管理](#十九能力版本管理)
20. [能力依赖管理](#二十能力依赖管理)
21. [能力监控统计](#二十一能力监控统计)
22. [能力安全审计](#二十二能力安全审计)

---

## 一、概述

### 1.1 文档目的

本文档定义了ooder能力管理系统的需求规格，包括：
- 能力的定义、分类和属性
- 能力的实例化机制（Agent + Link）
- 能力与场景的集成方式
- 能力的调用、路由和权限控制
- 能力的生命周期管理

### 1.2 设计原则

| 原则 | 说明 |
|------|------|
| **能力独立性** | 能力是独立实体，拥有全局唯一标识，可跨场景复用 |
| **实例化绑定** | 能力实例化 = Agent + Link + Address，场景内唯一 |
| **权限隔离** | 通过CAP地址区域和Link类型实现权限控制 |
| **声明式集成** | 支持能力声明支持的场景类型，自动匹配加入 |
| **绑定式集成** | 支持手动绑定能力到场景，适用于私有或特定能力 |

### 1.3 术语定义

本文档使用的核心术语请参考 [术语表](GLOSSARY.md)，以下仅列出能力域特有术语：

| 术语 | 定义 |
|------|------|
| **能力调用器** | CapabilityInvoker，执行能力调用的组件 |
| **能力注册表** | CapabilityRegistry，管理能力的注册和查找 |
| **LLM能力网关** | LLMCapabilityGateway，解析自然语言并协调能力 |
| **SuperAgent协调器** | SuperAgentCoordinator，执行涌现能力链 |

---

## 二、核心概念

### 2.1 能力三层模型

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         能力管理三层模型                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   第一层：静态能力定义（模板层）                                          │
│   ─────────────────────────────                                         │
│   Capability (能力定义)                                                  │
│   ├── capabilityId: 全局唯一标识                                        │
│   ├── name, type, version: 基本属性                                     │
│   ├── supportedSceneTypes: 支持的场景类型                               │
│   └── accessLevel: 访问级别                                             │
│                                                                         │
│   第二层：能力实例化（运行层）                                            │
│   ─────────────────────────────                                         │
│   能力实例 = Agent + Link + Address                                     │
│   ├── Agent: 能力执行者                                                 │
│   ├── Link: 通信链路                                                    │
│   └── Address: CAP地址（权限控制）                                      │
│                                                                         │
│   第三层：能力调用（执行层）                                              │
│   ─────────────────────────────                                         │
│   CapabilityBinding (能力绑定)                                          │
│   ├── bindingId: 绑定关系ID                                             │
│   ├── capabilityId + capId: 双ID映射                                   │
│   ├── agentId + linkId: 执行者和链路                                    │
│   └── status: 绑定状态                                                  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.2 能力ID体系

| ID类型 | 用途 | 示例 | 说明 |
|--------|------|------|------|
| **capabilityId** | 全局唯一标识 | `email-access-001` | 能力的全局唯一ID，跨场景使用 |
| **capId** | 场景内短ID | `daily-report-email` | 通过 `scenePrefix + functionName` 生成 |
| **capDefId** | 模板能力需求ID | `email-access` | 场景模板中定义的能力需求ID |

**ID映射关系**：
```
场景模板.capDefId ──映射──► CapabilityBinding.capabilityId
CapabilityBinding.capabilityId ──生成──► CapabilityBinding.capId
```

---

## 三、能力定义

### 3.1 能力类型

#### 3.1.1 按功能分类

```java
public enum CapabilityType {
    DRIVER,         // 驱动类型：设备驱动、硬件接口
    SERVICE,        // 服务类型：业务服务、API服务
    MANAGEMENT,     // 管理类型：配置管理、监控管理
    AI,             // AI类型：LLM、机器学习
    STORAGE,        // 存储类型：文件存储、数据库
    COMMUNICATION,  // 通信类型：消息、通知
    SECURITY,       // 安全类型：认证、加密
    MONITORING,     // 监控类型：日志、指标
    SKILL,          // 技能类型：可安装的技能包
    SCENE,          // 场景类型：场景本身作为能力
    SCENE_GROUP,    // 场景组类型：场景组作为能力
    CAPABILITY_CHAIN, // 能力链类型：能力组合
    CUSTOM          // 自定义类型
}
```

#### 3.1.2 按访问级别分类

```java
public enum AccessLevel {
    PRIVATE,    // 私有：仅所有者及其授权Agent可访问
    DOMAIN,     // 域内：同域可访问
    SCENE,      // 场景内：同场景可访问
    PUBLIC      // 公共：全局可访问
}
```

#### 3.1.3 按提供者分类

```java
public enum CapabilityProviderType {
    SKILL,          // Skill提供
    AGENT,          // Agent提供
    SUPER_AGENT,    // SuperAgent涌现
    DEVICE,         // 设备提供
    PLATFORM,       // 平台提供
    CROSS_SCENE     // 跨场景引用
}
```

### 3.2 能力属性

```yaml
Capability:
  # 基本属性
  capabilityId: "email-access-001"      # 全局唯一标识
  name: "邮箱访问能力"                    # 能力名称
  description: "访问员工邮箱数据"          # 能力描述
  type: SERVICE                          # 能力类型
  version: "1.0.0"                       # 版本号
  
  # 场景支持
  supportedSceneTypes:                   # 支持的场景类型（声明式）
    - DAILY_REPORT
    - NOTIFICATION
  
  # 访问控制
  accessLevel: PRIVATE                   # 访问级别
  ownerId: "employee-001"                # 所有者ID
  
  # 技术属性
  connectorType: HTTP                    # 连接类型
  endpoint: "http://email-service/api"   # 服务端点
  
  # 参数定义
  parameters:
    - name: "folder"
      type: "string"
      required: false
      defaultValue: "inbox"
    - name: "limit"
      type: "integer"
      required: false
      defaultValue: 10
  
  # 返回定义
  returns:
    type: "array"
    items:
      type: "object"
      properties:
        subject: "string"
        from: "string"
        date: "datetime"
```

### 3.3 场景类型常量

```java
public final class SceneTypes {
    
    // 系统场景类型
    public static final String SWITCH = "switch";           // 开关场景
    public static final String DIMMER = "dimmer";           // 调光场景
    public static final String COLOR = "color";             // 颜色场景
    public static final String SENSOR = "sensor";           // 传感器场景
    public static final String SECURITY = "security";       // 安防场景
    
    // 业务场景类型
    public static final String DAILY_REPORT = "daily-report";           // 日志汇报
    public static final String NOTIFICATION = "notification";           // 消息通知
    public static final String DATA_PROCESSING = "data-processing";     // 数据处理
    public static final String CONTENT_CREATION = "content-creation";   // 内容创作
    public static final String PROJECT_COLLABORATION = "project-collaboration"; // 项目协作
    
    // 智能家居场景类型
    public static final String AWAY_MODE = "away-mode";     // 离家模式
    public static final String HOME_MODE = "home-mode";     // 回家模式
    public static final String SLEEP_MODE = "sleep-mode";   // 睡眠模式
    public static final String VACATION_MODE = "vacation-mode"; // 度假模式
    
    // AI场景类型
    public static final String AI_ANALYSIS = "ai-analysis";     // AI分析
    public static final String AI_WRITING = "ai-writing";       // AI写作
    public static final String AI_CONVERSATION = "ai-conversation"; // AI对话
    
    // 工具方法
    public static boolean isDeclarativeType(String sceneType);
    public static boolean isBindingType(String sceneType);
    public static boolean isValidSceneType(String sceneType);
}
```

---

## 四、能力实例化

### 4.1 实例化公式

```
能力实例 = Agent + Link + Address
```

| 组件 | 职责 | 关键属性 |
|------|------|----------|
| **Agent** | 能力执行者 | agentId, sceneId, domainId, CapRegistry |
| **Link** | 通信链路 | linkId, sourceId, targetId, type, status |
| **Address** | CAP地址 | domainId, address, zone |

### 4.2 Agent定义

```java
public interface SceneAgent extends Agent {
    
    String getSceneId();
    String getDomainId();
    CapRegistry getCapRegistry();
    SceneContext getContext();
    
    // 能力调用
    Object invokeCapability(String capId, Map<String, Object> params);
    CompletableFuture<Object> invokeCapabilityAsync(String capId, Map<String, Object> params);
    Object invokeByAddress(CapAddress address, Map<String, Object> params);
    
    // 能力管理
    void registerCapability(Capability capability);
    void unregisterCapability(String capId);
    
    // 状态管理
    boolean isRunning();
    AgentStatus getAgentStatus();
    
    enum AgentStatus {
        CREATED, INITIALIZING, RUNNING, PAUSED, STOPPING, STOPPED, FAILED
    }
}
```

### 4.3 Link定义

```java
public class Link {
    
    private String linkId;          // 链路ID
    private String sourceId;        // 源ID（Agent ID）
    private String targetId;        // 目标ID（Agent ID）
    private LinkType type;          // 链路类型
    private LinkStatus status;      // 链路状态
    private long createTime;        // 创建时间
    private long lastActive;        // 最后活跃时间
    private Map<String, Object> metadata; // 元数据
}

public enum LinkType {
    DIRECT,     // 直接连接：同域内Agent通信
    RELAY,      // 中继连接：通过中间节点转发
    TUNNEL,     // 加密隧道：跨域安全通信
    MULTICAST,  // 组播连接：一对多通知
    P2P         // 点对点连接：北向P2P通信
}

public enum LinkStatus {
    ACTIVE,     // 活跃
    INACTIVE,   // 不活跃
    DEGRADED,   // 降级
    FAILED,     // 失败
    PENDING     // 待定
}
```

### 4.4 CAP地址定义

```java
public class CapAddress {
    
    private final int address;      // 地址值 (0-255)
    private final String domainId;  // 域ID
    
    // 地址区域枚举
    public enum AddressZone {
        SYSTEM(0x00, 0x3F, "系统区"),    // 全局可访问
        GENERAL(0x40, 0x9F, "通用区"),   // 场景内可访问
        EXTENSION(0xA0, 0xFF, "扩展区"); // 同域可访问
        
        public static AddressZone fromAddress(int address);
        public boolean isAccessibleFrom(String sourceDomain, String targetDomain);
    }
    
    // 核心方法
    public AddressZone getZone();
    public boolean isAccessibleFrom(String sourceDomain);
    public String toFullString();  // 如 "default:40"
}
```

### 4.5 Link类型选择规则

| 场景 | Link类型 | 说明 |
|------|----------|------|
| 同域内Agent通信 | DIRECT | 低延迟，无中间节点 |
| 跨域通信（有网关） | RELAY | 通过中间节点转发 |
| 私有域访问公共域 | TUNNEL | 加密隧道，需权限验证 |
| 一对多通知 | MULTICAST | 组播，高效广播 |
| 北向P2P通信 | P2P | WebRTC等去中心化 |

---

## 五、能力与场景集成

### 5.1 场景模板能力需求定义

```yaml
SceneTemplate:
  templateId: "daily-report-template"
  name: "日志汇报场景"
  sceneType: PROCESS_DRIVEN
  
  capabilities:                         # 能力需求列表
    - capDefId: "remind"                # 能力需求ID
      name: "日志提醒能力"
      required: true                    # 是否必需
      sceneTypes: [NOTIFICATION]        # 声明式场景类型
      parameters:                       # 参数要求
        time: "17:00"
        message: "请提交今日工作日志"
    
    - capDefId: "summary"
      name: "日志汇总能力"
      required: true
    
    - capDefId: "analysis"
      name: "AI分析能力"
      required: false                   # 可选能力
  
  roles:                                # 角色定义
    - roleId: "manager"
      capabilities: [summary, analysis] # 该角色需要的能力
    
    - roleId: "employee"
      capabilities: [remind]            # 该角色需要的能力
```

### 5.2 两种集成方式

#### 5.2.1 声明式集成（自动匹配）

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      声明式集成流程                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   1. 能力注册                                                           │
│      Capability.supportedSceneTypes = [NOTIFICATION, DAILY_REPORT]      │
│                                                                         │
│   2. 场景匹配                                                           │
│      SceneManager 查询支持 NOTIFICATION 类型的能力                       │
│                                                                         │
│   3. 自动绑定                                                           │
│      ├── 创建 CapabilityBinding                                         │
│      ├── 分配 CAP 地址                                                  │
│      ├── 创建 Agent                                                     │
│      └── 建立 Link                                                      │
│                                                                         │
│   适用场景：                                                             │
│   - 智能家居设备入网自动加入场景                                         │
│   - 公共能力自动发现和绑定                                               │
│   - 标准化服务的即插即用                                                 │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

#### 5.2.2 绑定式集成（手动绑定）

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      绑定式集成流程                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   1. 场景创建                                                           │
│      SceneGroup 创建，能力需求处于 PENDING 状态                          │
│                                                                         │
│   2. 用户操作                                                           │
│      ├── 浏览可用能力列表                                               │
│      ├── 选择能力                                                       │
│      ├── 配置参数                                                       │
│      └── 确认绑定                                                       │
│                                                                         │
│   3. 创建绑定                                                           │
│      ├── 创建 CapabilityBinding                                         │
│      ├── 分配 CAP 地址                                                  │
│      ├── 创建 Agent                                                     │
│      └── 建立 Link                                                      │
│                                                                         │
│   适用场景：                                                             │
│   - 私有能力需要授权访问                                                 │
│   - 特定配置的能力绑定                                                   │
│   - 需要人工决策的能力选择                                               │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 5.3 能力绑定状态

```java
public enum CapabilityBindingStatus {
    PENDING,    // 等待绑定
    BINDING,    // 绑定中
    ACTIVE,     // 正常运行
    INACTIVE,   // 暂停使用
    ERROR,      // 故障状态
    RELEASED    // 已释放
}
```

### 5.4 能力绑定模型

```yaml
CapabilityBinding:
  bindingId: "binding-remind-001"            # 绑定ID
  sceneGroupId: "daily-report-group-001"     # 场景组ID
  
  # 能力标识
  capDefId: "remind"                         # 模板能力需求ID
  capabilityId: "skill-remind-001"           # 实际能力ID
  capId: "daily-report-remind"               # 场景内短ID
  capAddress: "B0:01"                        # CAP地址
  
  # 执行组件
  agentId: "agent-remind-001"                # 执行Agent
  linkId: "link-remind-001"                  # 通信链路
  
  # 提供者信息
  providerType: SKILL                        # 提供者类型
  providerId: "skill-remind-001"             # 提供者ID
  
  # 连接配置
  connectorType: HTTP                        # 连接类型
  connectorConfig:                           # 连接配置
    endpoint: "http://remind-service/api"
    timeout: 30000
  
  # 故障处理
  priority: 1                                # 优先级
  fallback: true                             # 是否允许降级
  fallbackBindingId: "binding-remind-002"    # 降级绑定ID
  
  # 状态
  status: ACTIVE
  createTime: 1704067200000
  lastInvokeTime: 1704153600000
```

---

## 六、能力调用与路由

### 6.1 调用流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      能力调用流程                                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   1. 调用发起                                                           │
│      invokeCapability(capId, params)                                    │
│                                                                         │
│   2. 解析capId                                                          │
│      └── 查找 CapabilityBinding，获取 capabilityId                      │
│                                                                         │
│   3. 获取执行组件                                                       │
│      ├── agentId: 执行Agent                                             │
│      ├── linkId: 通信链路                                               │
│      └── capAddress: CAP地址                                            │
│                                                                         │
│   4. 权限检查                                                           │
│      └── capAddress.isAccessibleFrom(sourceDomain)                      │
│                                                                         │
│   5. 链路检查                                                           │
│      └── link.status == ACTIVE                                          │
│                                                                         │
│   6. 执行调用                                                           │
│      └── agent.invokeByAddress(capAddress, params)                      │
│                                                                         │
│   7. 更新统计                                                           │
│      ├── link.addBytesSent()                                            │
│      └── link.addBytesReceived()                                        │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 6.2 路由机制

```java
public class CapabilityRouter {
    
    /**
     * 路由能力调用
     */
    public Object route(String capId, Map<String, Object> params) {
        // 1. 查找绑定
        CapabilityBinding binding = bindingStore.findByCapId(capId);
        
        // 2. 获取组件
        SceneAgent agent = agentManager.getAgent(binding.getAgentId());
        Link link = linkStore.loadLink(binding.getLinkId());
        CapAddress address = binding.getCapAddress();
        
        // 3. 权限检查
        if (!address.isAccessibleFrom(getContext().getDomainId())) {
            throw new AccessDeniedException("No permission to access capability");
        }
        
        // 4. 链路检查
        if (link.getStatus() != LinkStatus.ACTIVE) {
            // 尝试降级
            return handleFallback(binding, params);
        }
        
        // 5. 执行调用
        return agent.invokeByAddress(address, params);
    }
}
```

### 6.3 连接器类型

```java
public enum ConnectorType {
    HTTP,       // HTTP/HTTPS
    GRPC,       // gRPC
    WEBSOCKET,  // WebSocket
    LOCAL_JAR,  // 本地JAR
    UDP,        // UDP
    INTERNAL    // 内部调用
}
```

---

## 七、能力生命周期管理

### 7.1 生命周期阶段

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      能力生命周期                                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐        │
│   │  定义    │───►│  注册    │───►│  发布    │───►│  启用    │        │
│   │ (Define) │    │(Register)│    │ (Publish)│    │ (Enable) │        │
│   └──────────┘    └──────────┘    └──────────┘    └──────────┘        │
│        │                                               │               │
│        │                                               ▼               │
│        │         ┌──────────┐    ┌──────────┐    ┌──────────┐        │
│        │         │  下架    │◄───│  禁用    │◄───│  运行    │        │
│        │         │(Deprecate)│   │(Disable) │    │  (Run)   │        │
│        │         └──────────┘    └──────────┘    └──────────┘        │
│        │              │                                               │
│        │              ▼                                               │
│        │         ┌──────────┐                                        │
│        └────────►│  归档    │                                        │
│                  │ (Archive)│                                        │
│                  └──────────┘                                        │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 7.2 各阶段说明

| 阶段 | 说明 | 关键操作 |
|------|------|----------|
| **定义** | 创建能力元数据 | 定义参数、返回值、权限 |
| **注册** | 注册到CapRegistry | 分配CAP地址、建立索引 |
| **发布** | 对外发布能力 | 版本管理、文档发布 |
| **启用** | 允许能力被调用 | 状态置为ACTIVE |
| **运行** | 能力正常执行 | 监控、日志、统计 |
| **禁用** | 暂停能力调用 | 状态置为INACTIVE |
| **下架** | 标记为废弃 | 通知依赖方迁移 |
| **归档** | 归档历史记录 | 保留审计日志 |

### 7.3 场景内能力生命周期

```
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│ 场景创建 │────►│ 能力发现 │────►│ 能力绑定 │────►│ 能力运行 │────►│ 能力解绑 │
└─────────┘     └─────────┘     └─────────┘     └─────────┘     └─────────┘
     │               │               │               │               │
     ▼               ▼               ▼               ▼               ▼
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│解析模板 │     │查询匹配 │     │创建绑定 │     │调用执行 │     │释放资源 │
│能力需求 │     │可用能力 │     │分配地址 │     │监控状态 │     │删除绑定 │
│         │     │         │     │创建Agent│     │处理故障 │     │注销Agent│
│         │     │         │     │建立Link │     │         │     │断开Link │
└─────────┘     └─────────┘     └─────────┘     └─────────┘     └─────────┘
```

---

## 八、能力权限控制

### 8.1 CAP地址区域划分

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      CAP地址空间 (00-FF)                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   系统区 (00-3F) - 64个地址                                             │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 用途：核心系统能力                                               │   │
│   │ 权限：全局可访问                                                 │   │
│   │ 示例：系统管理、核心API、平台服务                                 │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│   通用区 (40-9F) - 96个地址                                             │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 用途：通用业务能力                                               │   │
│   │ 权限：场景内可访问                                               │   │
│   │ 示例：消息通讯、存储服务、监控告警                                │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│   扩展区 (A0-FF) - 96个地址                                             │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │ 用途：扩展能力、私有能力                                         │   │
│   │ 权限：同域可访问                                                 │   │
│   │ 示例：用户私有数据、定制服务、行业扩展                            │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 8.2 权限控制规则

```java
public enum AddressZone {
    SYSTEM(0x00, 0x3F),    // 全局可访问
    GENERAL(0x40, 0x9F),   // 场景内可访问
    EXTENSION(0xA0, 0xFF); // 同域可访问
    
    public boolean isAccessibleFrom(String sourceDomain, String targetDomain) {
        switch (this) {
            case SYSTEM:
                return true;  // 系统区全局可访问
            case GENERAL:
                return true;  // 通用区场景内可访问
            case EXTENSION:
                return sourceDomain.equals(targetDomain);  // 扩展区仅同域可访问
            default:
                return false;
        }
    }
}
```

### 8.3 权限控制示例

```
场景：日志汇报场景
─────────────────

员工A的私有能力：
┌─────────────────────────────────────────────────────────────────────────┐
│  邮箱访问能力                                                            │
│  capabilityId: "email-001"                                              │
│  address: A0:01 (扩展区)                                                │
│  domainId: "user-a-private"                                             │
│                                                                         │
│  访问控制：                                                              │
│  ├── 员工A的私有LLM (domain: user-a-private) → ✅ 可访问                │
│  └── 公司LLM (domain: company-shared) → ❌ 不可访问                     │
└─────────────────────────────────────────────────────────────────────────┘

公司公共能力：
┌─────────────────────────────────────────────────────────────────────────┐
│  日志提醒能力                                                            │
│  capabilityId: "remind-001"                                             │
│  address: 40:01 (通用区)                                                │
│  domainId: "company-shared"                                             │
│                                                                         │
│  访问控制：                                                              │
│  ├── 员工A的私有LLM → ✅ 可访问                                         │
│  └── 公司LLM → ✅ 可访问                                                │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 九、能力故障处理

### 9.1 故障类型

| 故障类型 | 错误码 | 说明 |
|----------|--------|------|
| 服务不可用 | 503 | 设备/程序未启动 |
| 链路错误 | 404 | 不能到达目标 |
| 执行错误 | 500 | 能力执行失败 |
| 权限错误 | 403 | 无访问权限 |
| 超时错误 | 504 | 调用超时 |

### 9.2 故障处理策略

#### 9.2.1 单一能力故障

```
单一能力故障处理：
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                         │
│   自动启用备用能力（fallback）                                           │
│                                                                         │
│   ┌─────────────┐      故障      ┌─────────────┐                       │
│   │ 主能力      │ ────────────► │ 备用能力    │                       │
│   │ priority: 1 │               │ priority: 2 │                       │
│   └─────────────┘               └─────────────┘                       │
│                                                                         │
│   条件：binding.fallback == true                                        │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

#### 9.2.2 复合能力故障

```yaml
failureStrategy:
  type: FALLBACK           # 策略类型
  fallbackCapId: "backup-email-001"
  maxRetries: 3            # 最大重试次数
  retryInterval: 1000      # 重试间隔(ms)

# 策略类型
strategyTypes:
  - SKIP:        # 跳过该能力继续执行
  - RETRY:       # 重试指定次数
  - FALLBACK:    # 使用备用能力
  - ABORT:       # 中止整个流程
  - COMPENSATE:  # 执行补偿操作
```

### 9.3 链路故障处理

```java
public class LinkFailoverHandler {
    
    public void handleLinkFailure(Link link) {
        // 1. 更新链路状态
        link.setStatus(LinkStatus.FAILED);
        
        // 2. 尝试重建链路
        if (attemptReconnect(link)) {
            link.setStatus(LinkStatus.ACTIVE);
            return;
        }
        
        // 3. 查找备用链路
        Link backupLink = findBackupLink(link);
        if (backupLink != null) {
            switchToBackupLink(link, backupLink);
            return;
        }
        
        // 4. 通知相关绑定
        notifyBindings(link.getLinkId());
    }
}
```

---

## 十、API接口规范

### 10.1 能力管理接口

```java
public interface CapabilityService {
    
    /**
     * 注册能力
     */
    Capability register(Capability capability);
    
    /**
     * 注销能力
     */
    void unregister(String capabilityId);
    
    /**
     * 查询能力
     */
    Capability findById(String capabilityId);
    
    /**
     * 按场景类型查询能力
     */
    List<Capability> findBySceneType(String sceneType);
    
    /**
     * 更新能力
     */
    Capability update(Capability capability);
}
```

### 10.2 能力绑定接口

```java
public interface CapabilityBindingService {
    
    /**
     * 创建绑定
     */
    CapabilityBinding bind(String sceneGroupId, CapabilityBindingRequest request);
    
    /**
     * 解除绑定
     */
    void unbind(String bindingId);
    
    /**
     * 查询场景组的能力绑定列表
     */
    List<CapabilityBinding> listBySceneGroup(String sceneGroupId);
    
    /**
     * 更新绑定状态
     */
    void updateStatus(String bindingId, CapabilityBindingStatus status);
}
```

### 10.3 能力调用接口

```java
public interface CapabilityInvoker {
    
    /**
     * 同步调用能力
     */
    Object invoke(String capId, Map<String, Object> params);
    
    /**
     * 异步调用能力
     */
    CompletableFuture<Object> invokeAsync(String capId, Map<String, Object> params);
    
    /**
     * 按地址调用能力
     */
    Object invokeByAddress(CapAddress address, Map<String, Object> params);
}
```

### 10.4 链路管理接口

```java
public interface LinkService {
    
    /**
     * 创建链路
     */
    Link createLink(String sourceId, String targetId, LinkType type);
    
    /**
     * 删除链路
     */
    void deleteLink(String linkId);
    
    /**
     * 查询链路
     */
    Link findById(String linkId);
    
    /**
     * 按源ID查询链路
     */
    List<Link> findBySourceId(String sourceId);
    
    /**
     * 按目标ID查询链路
     */
    List<Link> findByTargetId(String targetId);
    
    /**
     * 更新链路状态
     */
    void updateStatus(String linkId, LinkStatus status);
}
```

---

## 十一、数据模型

### 11.1 能力定义表 (capability)

| 字段 | 类型 | 说明 |
|------|------|------|
| capability_id | VARCHAR(64) | 能力ID（主键） |
| name | VARCHAR(128) | 能力名称 |
| description | TEXT | 能力描述 |
| type | VARCHAR(32) | 能力类型 |
| version | VARCHAR(32) | 版本号 |
| access_level | VARCHAR(16) | 访问级别 |
| owner_id | VARCHAR(64) | 所有者ID |
| supported_scene_types | JSON | 支持的场景类型 |
| connector_type | VARCHAR(32) | 连接类型 |
| endpoint | VARCHAR(256) | 服务端点 |
| parameters | JSON | 参数定义 |
| returns | JSON | 返回定义 |
| status | VARCHAR(16) | 状态 |
| create_time | BIGINT | 创建时间 |
| update_time | BIGINT | 更新时间 |

### 11.2 能力绑定表 (capability_binding)

| 字段 | 类型 | 说明 |
|------|------|------|
| binding_id | VARCHAR(64) | 绑定ID（主键） |
| scene_group_id | VARCHAR(64) | 场景组ID |
| cap_def_id | VARCHAR(64) | 模板能力需求ID |
| capability_id | VARCHAR(64) | 实际能力ID |
| cap_id | VARCHAR(64) | 场景内短ID |
| cap_address | VARCHAR(16) | CAP地址 |
| agent_id | VARCHAR(64) | 执行Agent ID |
| link_id | VARCHAR(64) | 通信链路ID |
| provider_type | VARCHAR(32) | 提供者类型 |
| provider_id | VARCHAR(64) | 提供者ID |
| connector_type | VARCHAR(32) | 连接类型 |
| connector_config | JSON | 连接配置 |
| priority | INT | 优先级 |
| fallback | BOOLEAN | 是否允许降级 |
| fallback_binding_id | VARCHAR(64) | 降级绑定ID |
| status | VARCHAR(16) | 状态 |
| create_time | BIGINT | 创建时间 |
| last_invoke_time | BIGINT | 最后调用时间 |

### 11.3 链路表 (link)

| 字段 | 类型 | 说明 |
|------|------|------|
| link_id | VARCHAR(64) | 链路ID（主键） |
| scene_id | VARCHAR(64) | 场景ID |
| source_id | VARCHAR(64) | 源ID |
| target_id | VARCHAR(64) | 目标ID |
| link_type | VARCHAR(32) | 链路类型 |
| direction | VARCHAR(16) | 方向 |
| status | VARCHAR(16) | 状态 |
| config | JSON | 配置 |
| create_time | BIGINT | 创建时间 |
| update_time | BIGINT | 更新时间 |

---

## 十二、附录

### 12.1 相关源码文件

| 文件 | 路径 | 说明 |
|------|------|------|
| Capability.java | agent-sdk-api/.../capability/ | 能力接口定义 |
| CapAddress.java | agent-sdk-api/.../capability/ | CAP地址定义 |
| SceneTypes.java | agent-sdk-api/.../capability/ | 场景类型常量 |
| SceneAgent.java | agent-sdk-core/.../agent/ | 场景Agent接口 |
| Link.java | agent-sdk-core/.../network/link/ | 链路定义 |
| LinkConfig.java | agent-sdk-api/.../scene/store/ | 链路配置 |
| LinkStore.java | agent-sdk-api/.../scene/store/ | 链路存储接口 |

### 12.2 相关文档

| 文档 | 路径 | 说明 |
|------|------|------|
| CAP-REGISTRY-SPEC.md | docs/v2.3/ | CAP注册表规范 |
| CAPABILITY-DISCOVERY-PROTOCOL.md | docs/v2.3/ | 能力发现协议 |
| NORTHBOUND_SOUTHBOUND_ARCHITECTURE.md | agent-sdk/docs/architecture/ | 南北向架构手册 |
| SCENE_REQUIREMENT_SPEC.md | docs/ | 场景需求规格说明书 |
| GLOSSARY.md | docs/ | 统一术语表 |

### 12.3 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-03-01 | 初始版本 |
| 1.1 | 2026-03-01 | 新增用户故事、能力发现渠道、界面流程 |
| 1.2 | 2026-03-01 | 新增涌现能力、版本管理、依赖管理、监控统计、安全审计 |
| 1.3 | 2026-03-01 | 完善术语定义，新增9类术语分类，共计50+术语 |
| 1.4 | 2026-03-01 | 引用统一术语表，精简文档内术语定义 |

---

## 十三、用户故事

### 13.1 故事背景

```
角色：普通员工
需求：每天下班前上报工作日志
场景：日志汇报场景

员工需要的能力：
├── 邮件能力：发送工作日志到领导邮箱
└── GIT日志能力：读取GIT提交记录作为日志内容
```

### 13.2 能力使用完整流程

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                          能力使用完整流程                                             │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│   阶段一：能力市场查找                                                               │
│   ────────────────────                                                              │
│   员工在能力市场搜索"邮件能力"                                                       │
│   ├── 发现结果：邮件发送能力、邮件读取能力                                           │
│   ├── 下载并安装 skill-email-send                                                   │
│   └── 如果没有找到 → 进入下一阶段                                                   │
│                                                                                     │
│   阶段二：能力雷达扫描                                                               │
│   ────────────────────                                                              │
│   开启能力雷达，扫描范围：                                                           │
│   ├── 本地GIT库（已安装能力）                                                        │
│   ├── 共享能力中心（公司/部门共享）                                                  │
│   └── 其他员工共享（同事分享的能力）                                                 │
│                                                                                     │
│   阶段三：在线创建能力（LLM辅助）                                                     │
│   ──────────────────────────────                                                    │
│   如果仍未找到，使用LLM在线创建：                                                     │
│   ├── 输入自然语言描述需求                                                          │
│   ├── LLM生成能力代码和配置                                                         │
│   └── 用户确认并发布                                                                │
│                                                                                     │
│   阶段四：能力配置                                                                   │
│   ────────────────────                                                              │
│   配置能力参数：                                                                     │
│   ├── 邮件能力：SMTP服务器、账号密码、默认收件人                                     │
│   └── GIT能力：仓库地址、分支、时间范围                                              │
│                                                                                     │
│   阶段五：绑定到场景                                                                 │
│   ────────────────────                                                              │
│   将能力添加到日志汇报场景：                                                         │
│   ├── 分配CAP地址                                                                   │
│   ├── 创建Agent执行者                                                               │
│   └── 建立Link通信链路                                                              │
│                                                                                     │
│   阶段六：场景运行                                                                   │
│   ────────────────────                                                              │
│   每日17:00自动触发：                                                                │
│   ├── 读取GIT提交日志                                                               │
│   ├── 填充邮件模板                                                                  │
│   └── 发送邮件到领导邮箱                                                            │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 13.3 用户行为与能力管理概念映射

| 用户行为 | 能力管理概念 | API/接口 |
|----------|--------------|----------|
| 下载邮件能力 | 能力注册 | `Capability.register()` |
| 配置邮箱密码 | 能力配置 | `Capability.parameters` |
| 添加到场景 | 能力绑定 | `CapabilityBinding.bind()` |
| 开启能力雷达 | 能力发现 | `DiscoveryService.discoverAll()` |
| 在线创建能力 | 能力创建 | `LLMFactory.createCapability()` |
| 场景运行 | 能力调用 | `Agent.invokeCapability()` |

---

## 十四、能力发现渠道

### 14.1 发现方法枚举

```java
public enum DiscoveryMethod {
    UDP_BROADCAST("udp_broadcast", "UDP Broadcast discovery"),      // UDP广播
    DHT_KADEMLIA("dht_kademlia", "DHT/Kademlia discovery"),        // DHT/Kademlia
    MDNS_DNS_SD("mdns_dns_sd", "mDNS/DNS-SD discovery"),           // mDNS/DNS-SD
    SKILL_CENTER("skill_center", "SkillCenter API discovery"),     // 能力中心API
    LOCAL_FS("local_fs", "Local filesystem discovery"),            // 本地文件系统
    GITHUB("github", "GitHub repository discovery"),               // GitHub仓库
    GITEE("gitee", "Gitee repository discovery"),                  // Gitee仓库
    GIT_REPOSITORY("git_repository", "Git repository discovery"),  // 通用Git仓库
    AUTO("auto", "Auto detect discovery method");                  // 自动检测
}
```

### 14.2 发现渠道分类

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                          能力发现渠道（9种）                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│   第一层：本地发现                                                                   │
│   ────────────────                                                                  │
│   1. LOCAL_FS (本地文件系统)                                                        │
│      ├── 路径: ~/.ooder/skills/                                                    │
│      ├── 已安装的技能包                                                             │
│      └── 本地开发的技能                                                             │
│                                                                                     │
│   第二层：网络发现（局域网）                                                          │
│   ────────────────────────                                                          │
│   2. UDP_BROADCAST (UDP广播)                                                        │
│      ├── 组播地址: 239.255.255.250:1900                                            │
│      └── 发现同一局域网内的能力                                                     │
│                                                                                     │
│   3. MDNS_DNS_SD (mDNS/DNS-SD)                                                      │
│      ├── 多播DNS服务发现                                                            │
│      └── 自动发现附近设备能力                                                       │
│                                                                                     │
│   4. DHT_KADEMLIA (DHT/Kademlia)                                                    │
│      ├── 分布式哈希表                                                               │
│      └── 跨网络能力发现                                                             │
│                                                                                     │
│   第三层：中心化发现                                                                 │
│   ────────────────                                                                  │
│   5. SKILL_CENTER (能力中心API)                                                     │
│      ├── API端点: /api/v1/skills                                                   │
│      └── 官方/企业能力市场                                                          │
│                                                                                     │
│   第四层：代码仓库发现                                                               │
│   ────────────────────                                                              │
│   6. GITHUB (GitHub仓库)                                                            │
│      ├── 公开技能仓库                                                               │
│      └── 需要GitHub Token访问私有仓库                                              │
│                                                                                     │
│   7. GITEE (Gitee仓库)                                                              │
│      ├── 国内代码仓库                                                               │
│      └── 需要Gitee Token访问私有仓库                                               │
│                                                                                     │
│   8. GIT_REPOSITORY (通用Git仓库)                                                   │
│      ├── 自建Git服务器                                                              │
│      └── 企业内部仓库                                                               │
│                                                                                     │
│   特殊：自动检测                                                                     │
│   ────────────────                                                                  │
│   9. AUTO (自动检测)                                                                │
│      └── 根据环境自动选择最佳发现方法                                               │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 14.3 发现渠道适用场景

| 渠道 | 个人网络 | 部门分享 | 公司管理 | 公共社区 | 可靠性 |
|------|----------|----------|----------|----------|--------|
| LOCAL_FS | ⭐⭐⭐⭐⭐ | - | - | - | 高 |
| UDP_BROADCAST | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐ | ❌ | 中 |
| MDNS_DNS_SD | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ❌ | ❌ | 中 |
| DHT_KADEMLIA | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | 中 |
| SKILL_CENTER | ❌ | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | 高 |
| GITHUB | ❌ | ❌ | ⭐⭐ | ⭐⭐⭐⭐⭐ | 高 |
| GITEE | ❌ | ❌ | ⭐⭐ | ⭐⭐⭐⭐⭐ | 高 |

### 14.4 SkillDiscoverer接口

```java
public interface SkillDiscoverer {
    
    // 核心发现方法
    CompletableFuture<List<SkillPackage>> discover();
    CompletableFuture<SkillPackage> discover(String skillId);
    
    // 按场景发现
    CompletableFuture<List<SkillPackage>> discoverByScene(String sceneId);
    
    // 搜索方法
    CompletableFuture<List<SkillPackage>> search(String query);
    CompletableFuture<List<SkillPackage>> searchByCapability(String capabilityId);
    CompletableFuture<List<SkillPackage>> discoverByCategory(String category);
    CompletableFuture<List<SkillPackage>> discoverByCategory(String category, String subCategory);
    CompletableFuture<List<SkillPackage>> searchByTags(List<String> tags);
    
    // 元数据
    DiscoveryMethod getMethod();
    boolean isAvailable();
    void setTimeout(long timeoutMs);
    void setFilter(DiscoveryFilter filter);
}
```

### 14.5 发现过滤器

```java
public class DiscoveryFilter {
    private String sceneId;              // 场景ID过滤
    private String version;              // 版本过滤
    private List<String> capabilities;   // 能力需求过滤
    private Map<String, String> labels;  // 标签过滤
    private String category;             // 类别过滤
    private String subCategory;          // 子类别过滤
    private List<String> tags;           // 标签过滤
}
```

---

## 十五、界面流程设计

### 15.1 能力市场首页

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│  能力市场                                                                            │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │  🔍 搜索能力...                                                    [搜索]   │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                     │
│  热门分类：                                                                          │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐                      │
│  │  通信   │ │  存储   │ │  AI     │ │  监控   │ │  安全   │                      │
│  │  128个  │ │  64个   │ │  256个  │ │  32个   │ │  48个   │                      │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘                      │
│                                                                                     │
│  推荐能力：                                                                          │
│  ┌───────────────────────────────────────────────────────────────────────────────┐ │
│  │ 📧 邮件发送能力           ⭐ 4.8  📥 10万+安装    [安装]                      │ │
│  │ 支持HTML邮件、附件、模板变量替换                                             │ │
│  └───────────────────────────────────────────────────────────────────────────────┘ │
│  ┌───────────────────────────────────────────────────────────────────────────────┐ │
│  │ 📊 GIT日志读取能力        ⭐ 4.6  📥 5万+安装     [安装]                      │ │
│  │ 读取GIT提交记录，支持时间范围、分支过滤                                       │ │
│  └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│  [开启能力雷达]  [在线创建能力]                                                      │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 15.2 能力雷达扫描

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│  能力雷达扫描                                                                        │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│  选择扫描范围：                                                                      │
│  ┌───────────────────────────────────────────────────────────────────────────────┐ │
│  │ ☑ 本地已安装能力 (LOCAL_FS)                                                   │ │
│  │   └── ~/.ooder/skills/ - 12个能力                                             │ │
│  │                                                                               │ │
│  │ ☑ 局域网共享 (UDP_BROADCAST)                                                  │ │
│  │   └── 扫描中... 发现3个同事共享能力                                           │ │
│  │                                                                               │ │
│  │ ☐ 分布式网络 (DHT_KADEMLIA)                                                   │ │
│  │   └── 需要配置DHT节点                                                         │ │
│  │                                                                               │ │
│  │ ☑ 公司能力中心 (SKILL_CENTER)                                                 │ │
│  │   └── https://skill.company.com - 256个能力                                   │ │
│  │                                                                               │ │
│  │ ☐ GitHub仓库 (GITHUB)                                                         │ │
│  │   └── 需要配置GitHub Token                                                    │ │
│  │                                                                               │ │
│  │ ☐ Gitee仓库 (GITEE)                                                           │ │
│  │   └── 需要配置Gitee Token                                                     │ │
│  └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│  [开始扫描]                                                                         │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 15.3 扫描结果

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│  扫描结果 (共发现 28 个能力)                                                         │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│  筛选：[全部] [邮件相关] [GIT相关] [AI相关]                                          │
│                                                                                     │
│  ┌───────────────────────────────────────────────────────────────────────────────┐ │
│  │ 📧 企业邮件能力 (同事A分享)                                                    │ │
│  │ 来源: UDP_BROADCAST | 支持HTML、附件、定时发送                                 │ │
│  │                                               [查看详情] [安装]               │ │
│  └───────────────────────────────────────────────────────────────────────────────┘ │
│  ┌───────────────────────────────────────────────────────────────────────────────┐ │
│  │ 📊 GIT提交日志能力 (部门共享)                                                  │ │
│  │ 来源: SKILL_CENTER | 读取提交记录、生成报告                                    │ │
│  │                                               [查看详情] [安装]               │ │
│  └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│  没有找到需要的？[在线创建能力]                                                      │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 15.4 在线创建能力（LLM辅助）

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│  在线创建能力                                                                        │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│  ┌───────────────────────────────────────────────────────────────────────────────┐ │
│  │ 描述您的需求：                                                                 │ │
│  │                                                                               │ │
│  │ 我需要创建一个邮件发送能力，需求是：                                           │ │
│  │ - 支持发送HTML格式的邮件                                                      │ │
│  │ - 支持附件                                                                    │ │
│  │ - 支持模板变量替换（如：${date}, ${content}）                                 │ │
│  │ - 支持定时发送                                                                │ │
│  │                                                                               │ │
│  └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│  [LLM生成] [取消]                                                                   │
│                                                                                     │
│  LLM生成结果：                                                                      │
│  ┌───────────────────────────────────────────────────────────────────────────────┐ │
│  │ capabilityId: "custom-email-html-001"                                         │ │
│  │ name: "HTML邮件发送能力"                                                       │ │
│  │ type: COMMUNICATION                                                           │ │
│  │ parameters:                                                                   │ │
│  │   - to: 收件人地址 (required)                                                 │ │
│  │   - subject: 邮件主题 (required)                                              │ │
│  │   - template: HTML模板 (optional)                                             │ │
│  │   - variables: 模板变量映射 (optional)                                        │ │
│  │   - attachments: 附件列表 (optional)                                          │ │
│  │   - scheduleTime: 定时发送时间 (optional)                                     │ │
│  └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│  [确认创建] [编辑修改] [重新生成]                                                    │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 15.5 能力配置

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│  配置能力：邮件发送能力                                                              │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│  基本信息：                                                                         │
│  ┌───────────────────────────────────────────────────────────────────────────────┐ │
│  │ 能力名称: [邮件发送能力                                      ]                │ │
│  │ 描述:     [支持发送HTML格式邮件                              ]                │ │
│  └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│  连接配置：                                                                         │
│  ┌───────────────────────────────────────────────────────────────────────────────┐ │
│  │ SMTP服务器: [smtp.company.com                                ]                │ │
│  │ 端口:       [465                                             ]                │ │
│  │ 加密方式:   [SSL ▼]                                                           │ │
│  │ 邮箱账号:   [employee@company.com                            ]                │ │
│  │ 邮箱密码:   [******                                          ]                │ │
│  └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│  默认参数：                                                                         │
│  ┌───────────────────────────────────────────────────────────────────────────────┐ │
│  │ 默认收件人: [manager@company.com                              ]               │ │
│  │ 邮件模板:   [选择模板...]                                     [编辑]         │ │
│  └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│  [测试连接] [保存配置] [取消]                                                        │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 15.6 添加到场景

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│  添加能力到场景                                                                      │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│  选择场景：                                                                         │
│  ┌───────────────────────────────────────────────────────────────────────────────┐ │
│  │ ◉ 日志汇报场景                                                  [已选]       │ │
│  │   └── 已有能力: GIT日志读取、日志模板                                          │ │
│  │                                                                               │ │
│  │ ○ 项目管理场景                                                                │ │
│  │   └── 已有能力: 任务提醒、进度跟踪                                             │ │
│  │                                                                               │ │
│  │ ○ 创建新场景...                                                               │ │
│  └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│  绑定配置：                                                                         │
│  ┌───────────────────────────────────────────────────────────────────────────────┐ │
│  │ 场景内ID: [daily-report-email                              ]                  │ │
│  │ 优先级:   [1 ▼]                                                               │ │
│  │ 允许降级: [☑]                                                                 │ │
│  └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│  [确认添加] [取消]                                                                  │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 十六、能力包元数据

### 16.1 SkillPackage结构

```yaml
SkillPackage:
  # 基本标识
  skillId: "skill-email-send-001"
  name: "邮件发送能力"
  description: "支持发送HTML格式邮件"
  version: "1.0.0"
  sceneId: "daily-report"
  
  # 来源信息
  source: "skillcenter:https://skill.ooder.cn"
  downloadUrl: "https://skill.ooder.cn/download/skill-email-send-001.zip"
  checksum: "sha256:abc123..."
  size: 1024000
  
  # 分类信息
  category: "communication"
  subCategory: "email"
  tags:
    - email
    - notification
    - html
  
  # 能力列表
  capabilities:
    - capId: "email-send"
      name: "发送邮件"
      parameters:
        - name: "to"
          type: "string"
          required: true
        - name: "subject"
          type: "string"
          required: true
        - name: "body"
          type: "string"
          required: true
  
  # 清单
  manifest:
    skillId: "skill-email-send-001"
    name: "邮件发送能力"
    version: "1.0.0"
    mainClass: "net.ooder.skills.email.EmailSkill"
    author: "ooder-team"
    license: "Apache-2.0"
```

### 16.2 能力包存储路径

```
~/.ooder/
├── skills/                           # 能力包目录
│   ├── skill-email-send-001/         # 能力包
│   │   ├── skill.json                # 能力清单
│   │   ├── lib/                      # 依赖库
│   │   └── config/                   # 配置文件
│   └── skill-git-log-001/
│       ├── skill.json
│       └── ...
├── config/                           # 全局配置
│   └── discovery.properties          # 发现配置
└── cache/                            # 缓存
    └── capability-index/             # 能力索引缓存
```

---

## 十七、能力发现API

### 17.1 DiscoveryService接口

```java
public class DiscoveryService {
    
    // 注册发现器
    void registerDiscoverer(DiscoveryMethod method, SkillDiscoverer discoverer);
    void unregisterDiscoverer(DiscoveryMethod method);
    
    // 发现能力
    CompletableFuture<List<SkillPackage>> discoverAll();
    CompletableFuture<List<SkillPackage>> discoverAll(DiscoveryMethod method);
    CompletableFuture<SkillPackage> discover(String skillId);
    CompletableFuture<SkillPackage> discover(String skillId, DiscoveryMethod method);
    
    // 按场景发现
    CompletableFuture<List<SkillPackage>> discoverByScene(String sceneId);
    CompletableFuture<List<SkillPackage>> discoverByScene(String sceneId, DiscoveryMethod method);
    
    // 搜索
    CompletableFuture<List<SkillPackage>> search(String query);
    CompletableFuture<List<SkillPackage>> search(String query, DiscoveryMethod method);
    CompletableFuture<List<SkillPackage>> searchByCapability(String capabilityId);
    CompletableFuture<List<SkillPackage>> searchByCapability(String capabilityId, DiscoveryMethod method);
    
    // 可用性检查
    boolean isMethodAvailable(DiscoveryMethod method);
    List<DiscoveryMethod> getAvailableMethods();
    
    // 配置
    void setDefaultMethod(DiscoveryMethod method);
    void setSkillCenterEndpoint(String endpoint);
}
```

### 17.2 API调用示例

```java
// 创建发现服务
DiscoveryService discoveryService = new DiscoveryService();

// 搜索能力
List<SkillPackage> results = discoveryService.search("邮件能力", DiscoveryMethod.SKILL_CENTER).join();

// 能力雷达扫描
List<SkillPackage> localSkills = discoveryService.discoverAll(DiscoveryMethod.LOCAL_FS).join();
List<SkillPackage> lanSkills = discoveryService.discoverAll(DiscoveryMethod.UDP_BROADCAST).join();
List<SkillPackage> centerSkills = discoveryService.discoverAll(DiscoveryMethod.SKILL_CENTER).join();

// 按场景发现
List<SkillPackage> sceneSkills = discoveryService.discoverByScene("daily-report", DiscoveryMethod.AUTO).join();

// 按能力搜索
List<SkillPackage> capSkills = discoveryService.searchByCapability("email-send").join();
```

---

## 十八、涌现能力设计

### 18.1 涌现能力概念

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                          涌现能力（Emergent Capability）                              │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│   定义：                                                                             │
│   涌现能力是指通过LLM/SuperAgent协调多个现有能力，组合产生的新能力。                   │
│   这种能力并非预先定义，而是在运行时根据需求动态生成。                                 │
│                                                                                     │
│   特点：                                                                             │
│   ├── 动态性：运行时按需生成，无需预定义                                              │
│   ├── 组合性：基于现有能力组合实现                                                    │
│   ├── 智能性：LLM负责理解和协调                                                       │
│   └── 临时性：可以是临时会话能力，也可以持久化                                         │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 18.2 LLM作为能力网关

```java
public interface LLMCapabilityGateway {
    
    /**
     * 解析自然语言需求，匹配合适的能力
     */
    CompletableFuture<List<CapabilityMatch>> matchCapabilities(String naturalLanguageRequest);
    
    /**
     * 协调多个能力完成复杂任务
     */
    CompletableFuture<Object> coordinateCapabilities(
        String taskId,
        List<String> capabilityIds,
        Map<String, Object> context
    );
    
    /**
     * 创建涌现能力
     */
    CompletableFuture<Capability> createEmergentCapability(
        String name,
        String description,
        List<String> baseCapabilities
    );
}
```

### 18.3 涌现能力创建流程

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                          涌现能力创建流程                                             │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│   用户输入：                                                                         │
│   "我需要一个能力，每天下班前读取我的GIT提交记录，生成工作日志，发送到领导邮箱"        │
│                                                                                     │
│   LLM解析：                                                                          │
│   ┌───────────────────────────────────────────────────────────────────────────────┐ │
│   │ 步骤1: 理解需求                                                                │ │
│   │   ├── 触发条件：每天下班前（17:00）                                            │ │
│   │   ├── 数据源：GIT提交记录                                                      │ │
│   │   ├── 处理：生成工作日志                                                       │ │
│   │   └── 输出：发送邮件                                                           │ │
│   │                                                                               │ │
│   │ 步骤2: 匹配现有能力                                                            │ │
│   │   ├── GIT日志读取能力 ✓ (已安装)                                              │ │
│   │   ├── 日志生成能力 ✓ (LLM内置)                                                │ │
│   │   └── 邮件发送能力 ✓ (已安装)                                                 │ │
│   │                                                                               │ │
│   │ 步骤3: 创建能力链                                                              │ │
│   │   git-log-read → llm-summarize → email-send                                  │ │
│   │                                                                               │ │
│   │ 步骤4: 生成涌现能力定义                                                        │ │
│   │   capabilityId: "emergent-daily-report-001"                                  │ │
│   │   type: CAPABILITY_CHAIN                                                      │ │
│   │   providerType: SUPER_AGENT                                                   │ │
│   └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│   输出涌现能力：                                                                     │
│   ┌───────────────────────────────────────────────────────────────────────────────┐ │
│   │ 涌现能力: 每日工作日志汇报                                                      │ │
│   │ capabilityId: "emergent-daily-report-001"                                     │ │
│   │ type: CAPABILITY_CHAIN                                                        │ │
│   │ providerType: SUPER_AGENT                                                     │ │
│   │                                                                               │ │
│   │ 能力链:                                                                        │ │
│   │   1. git-log-read (读取GIT提交)                                               │ │
│   │   2. llm-summarize (LLM生成摘要)                                              │ │
│   │   3. email-send (发送邮件)                                                    │ │
│   │                                                                               │ │
│   │ 触发条件: cron: "0 0 17 * * 1-5"                                              │ │
│   │ 输入参数:                                                                      │ │
│   │   - recipient: 领导邮箱                                                       │ │
│   │   - gitRepo: GIT仓库地址                                                      │ │
│   │   - template: 日志模板                                                        │ │
│   └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 18.4 SuperAgent协调机制

```java
public class SuperAgentCoordinator {
    
    private final LLMCapabilityGateway llmGateway;
    private final CapabilityRegistry registry;
    private final CapabilityInvoker invoker;
    
    /**
     * 执行涌现能力
     */
    public CompletableFuture<Object> executeEmergentCapability(
        String emergentCapId,
        Map<String, Object> params
    ) {
        // 1. 获取涌现能力定义
        Capability emergentCap = registry.findById(emergentCapId);
        
        // 2. 解析能力链
        List<String> chain = emergentCap.getCapabilityChain();
        
        // 3. 按顺序执行能力链
        CompletableFuture<Object> result = CompletableFuture.completedFuture(null);
        Map<String, Object> context = new HashMap<>(params);
        
        for (String capId : chain) {
            result = result.thenCompose(prevResult -> {
                // 将前一步结果传入上下文
                if (prevResult != null) {
                    context.put("previousResult", prevResult);
                }
                
                // LLM决定如何调用下一个能力
                return llmGateway.coordinateCapabilities(
                    emergentCapId,
                    Collections.singletonList(capId),
                    context
                );
            });
        }
        
        return result;
    }
}
```

### 18.5 涌现能力类型

| 类型 | 说明 | 示例 |
|------|------|------|
| **CAPABILITY_CHAIN** | 能力链，顺序执行多个能力 | GIT读取→LLM摘要→邮件发送 |
| **CAPABILITY_PARALLEL** | 并行能力，同时执行多个能力 | 同时查询多个数据源 |
| **CAPABILITY_CONDITIONAL** | 条件能力，根据条件选择执行 | 根据时间选择不同通知方式 |
| **CAPABILITY_LOOP** | 循环能力，重复执行直到条件满足 | 轮询直到任务完成 |

---

## 十九、能力版本管理

### 19.1 版本号规范

```
版本号格式: MAJOR.MINOR.PATCH

MAJOR: 主版本号
├── 不兼容的API变更
├── 能力接口重大调整
└── 数据模型变更

MINOR: 次版本号
├── 向后兼容的功能新增
├── 新增可选参数
└── 性能优化

PATCH: 补丁版本号
├── 向后兼容的问题修复
├── Bug修复
└── 文档更新
```

### 19.2 版本兼容性规则

```java
public enum VersionCompatibility {
    EXACT,          // 精确匹配：必须完全一致
    COMPATIBLE,     // 兼容匹配：MAJOR.MINOR相同，PATCH可不同
    RANGE,          // 范围匹配：指定版本范围
    LATEST          // 最新版本：自动选择最新兼容版本
}

public class VersionRange {
    private String minVersion;      // 最小版本（包含）
    private String maxVersion;      // 最大版本（不包含）
    private List<String> excluded;  // 排除版本
    
    public boolean isCompatible(String version) {
        return compare(version, minVersion) >= 0 
            && compare(version, maxVersion) < 0
            && !excluded.contains(version);
    }
}
```

### 19.3 版本升级策略

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                          版本升级策略                                                 │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│   场景模板版本升级：                                                                  │
│   ┌───────────────────────────────────────────────────────────────────────────────┐ │
│   │ 1. PATCH升级 (1.0.0 → 1.0.1)                                                   │ │
│   │    ├── 自动升级                                                               │ │
│   │    ├── 无需用户确认                                                           │ │
│   │    └── 保持现有绑定                                                           │ │
│   │                                                                               │ │
│   │ 2. MINOR升级 (1.0.0 → 1.1.0)                                                   │ │
│   │    ├── 通知用户                                                               │ │
│   │    ├── 可选择自动或手动升级                                                    │ │
│   │    └── 新增能力需求可选                                                       │ │
│   │                                                                               │ │
│   │ 3. MAJOR升级 (1.0.0 → 2.0.0)                                                   │ │
│   │    ├── 必须用户确认                                                           │ │
│   │    ├── 可能需要重新配置                                                       │ │
│   │    └── 可能需要迁移数据                                                       │ │
│   └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│   能力版本升级：                                                                      │
│   ┌───────────────────────────────────────────────────────────────────────────────┐ │
│   │ 1. 多版本共存                                                                  │ │
│   │    ├── 同一capabilityId可存在多个版本                                          │ │
│   │    ├── 绑定时指定版本                                                         │ │
│   │    └── 默认使用最新兼容版本                                                   │ │
│   │                                                                               │ │
│   │ 2. 灰度发布                                                                    │ │
│   │    ├── 新版本先发布给部分用户                                                  │ │
│   │    ├── 监控稳定性后逐步推广                                                   │ │
│   │    └── 可随时回滚                                                             │ │
│   │                                                                               │ │
│   │ 3. 废弃流程                                                                    │ │
│   │    ├── 标记为DEPRECATED                                                       │ │
│   │    ├── 通知依赖方迁移                                                         │ │
│   │    ├── 设置废弃日期                                                           │ │
│   │    └── 最终下架                                                               │ │
│   └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 19.4 版本管理API

```java
public interface CapabilityVersionService {
    
    /**
     * 发布新版本
     */
    Capability publishVersion(Capability capability, String version);
    
    /**
     * 获取版本列表
     */
    List<Capability> listVersions(String capabilityId);
    
    /**
     * 获取指定版本
     */
    Capability getVersion(String capabilityId, String version);
    
    /**
     * 获取最新版本
     */
    Capability getLatestVersion(String capabilityId);
    
    /**
     * 获取最新兼容版本
     */
    Capability getLatestCompatibleVersion(String capabilityId, String minVersion);
    
    /**
     * 废弃版本
     */
    void deprecateVersion(String capabilityId, String version, String reason, long deprecateDate);
    
    /**
     * 版本迁移
     */
    void migrateBindings(String capabilityId, String fromVersion, String toVersion);
}
```

---

## 二十、能力依赖管理

### 20.1 依赖声明

```yaml
Capability:
  capabilityId: "daily-report-email-001"
  name: "日志邮件汇报能力"
  version: "1.0.0"
  
  dependencies:
    # 能力依赖
    capabilities:
      - capabilityId: "git-log-read"
        version: ">=1.0.0 <2.0.0"
        required: true
        
      - capabilityId: "email-send"
        version: "^1.2.0"
        required: true
        
      - capabilityId: "llm-summarize"
        version: "latest"
        required: false
    
    # 技能包依赖
    skills:
      - skillId: "skill-email-core"
        version: ">=1.0.0"
        
    # 库依赖
    libraries:
      - groupId: "org.apache.commons"
        artifactId: "commons-email"
        version: "1.5"
```

### 20.2 依赖解析

```java
public class DependencyResolver {
    
    /**
     * 解析能力依赖
     */
    public DependencyResolution resolve(Capability capability) {
        DependencyResolution resolution = new DependencyResolution();
        
        // 1. 收集所有依赖
        Set<CapabilityDependency> allDeps = collectDependencies(capability);
        
        // 2. 构建依赖图
        DependencyGraph graph = buildDependencyGraph(allDeps);
        
        // 3. 检测循环依赖
        if (graph.hasCycle()) {
            throw new CircularDependencyException(graph.findCycle());
        }
        
        // 4. 版本冲突检测
        List<VersionConflict> conflicts = detectVersionConflicts(graph);
        if (!conflicts.isEmpty()) {
            resolution.setConflicts(conflicts);
        }
        
        // 5. 计算安装顺序
        List<Capability> installOrder = graph.topologicalSort();
        resolution.setInstallOrder(installOrder);
        
        return resolution;
    }
    
    /**
     * 检查依赖满足状态
     */
    public DependencyStatus checkStatus(String capabilityId) {
        Capability capability = registry.findById(capabilityId);
        DependencyStatus status = new DependencyStatus();
        
        for (CapabilityDependency dep : capability.getDependencies()) {
            Capability installed = registry.findById(dep.getCapabilityId());
            
            if (installed == null) {
                status.addMissing(dep);
            } else if (!dep.getVersionRange().isCompatible(installed.getVersion())) {
                status.addIncompatible(dep, installed);
            } else {
                status.addSatisfied(dep, installed);
            }
        }
        
        return status;
    }
}
```

### 20.3 依赖状态

```java
public class DependencyStatus {
    
    private List<DependencyEntry> satisfied = new ArrayList<>();    // 已满足
    private List<DependencyEntry> missing = new ArrayList<>();      // 缺失
    private List<DependencyEntry> incompatible = new ArrayList<>(); // 版本不兼容
    
    public boolean isAllSatisfied() {
        return missing.isEmpty() && incompatible.isEmpty();
    }
    
    public boolean canRun() {
        // 检查必需依赖是否都满足
        return missing.stream().noneMatch(d -> d.getDependency().isRequired())
            && incompatible.stream().noneMatch(d -> d.getDependency().isRequired());
    }
}
```

### 20.4 依赖安装流程

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                          依赖安装流程                                                 │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│   用户安装能力: daily-report-email-001                                               │
│                                                                                     │
│   步骤1: 依赖解析                                                                    │
│   ┌───────────────────────────────────────────────────────────────────────────────┐ │
│   │ 依赖树:                                                                        │ │
│   │ daily-report-email-001                                                        │ │
│   │ ├── git-log-read (>=1.0.0 <2.0.0) [required]                                  │ │
│   │ │   └── git-client (>=2.0.0) [required]                                       │ │
│   │ ├── email-send (^1.2.0) [required]                                            │ │
│   │ │   ├── smtp-client (>=1.0.0) [required]                                      │ │
│   │ │   └── template-engine (>=1.0.0) [optional]                                  │ │
│   │ └── llm-summarize (latest) [optional]                                         │ │
│   └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│   步骤2: 检查已安装                                                                  │
│   ┌───────────────────────────────────────────────────────────────────────────────┐ │
│   │ ✓ git-log-read 1.1.0 (已安装，版本兼容)                                        │ │
│   │ ✓ git-client 2.1.0 (已安装，版本兼容)                                          │ │
│   │ ✗ email-send (未安装)                                                          │ │
│   │ ✗ smtp-client (未安装)                                                         │ │
│   │ ✗ template-engine (未安装，可选)                                               │ │
│   │ ✗ llm-summarize (未安装，可选)                                                 │ │
│   └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│   步骤3: 确认安装                                                                    │
│   ┌───────────────────────────────────────────────────────────────────────────────┐ │
│   │ 需要安装以下依赖:                                                              │ │
│   │ ├── email-send 1.3.0 (必需)                                                   │ │
│   │ ├── smtp-client 1.2.0 (必需)                                                  │ │
│   │ ├── template-engine 1.0.0 (可选，推荐安装)                                    │ │
│   │ └── llm-summarize 1.0.0 (可选)                                                │ │
│   │                                                                               │ │
│   │ 总大小: 5.2MB                                                                 │ │
│   │                                                                               │ │
│   │ [安装全部] [仅安装必需] [取消]                                                 │ │
│   └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
│   步骤4: 按顺序安装                                                                  │
│   ┌───────────────────────────────────────────────────────────────────────────────┐ │
│   │ 安装顺序:                                                                      │ │
│   │ 1. smtp-client 1.2.0    [████████████████] 完成                               │ │
│   │ 2. template-engine 1.0.0 [████████████████] 完成                              │ │
│   │ 3. email-send 1.3.0    [████████████████] 完成                               │ │
│   │ 4. llm-summarize 1.0.0 [████████████████] 完成                               │ │
│   │ 5. daily-report-email-001 [████████████████] 完成                            │ │
│   └───────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 二十一、能力监控统计

### 21.1 监控指标

```java
public class CapabilityMetrics {
    
    // 调用统计
    private long totalInvocations;          // 总调用次数
    private long successInvocations;        // 成功次数
    private long failedInvocations;         // 失败次数
    private long timeoutInvocations;        // 超时次数
    
    // 性能统计
    private double avgResponseTime;         // 平均响应时间(ms)
    private double maxResponseTime;         // 最大响应时间(ms)
    private double minResponseTime;         // 最小响应时间(ms)
    private double p95ResponseTime;         // P95响应时间
    private double p99ResponseTime;         // P99响应时间
    
    // 资源统计
    private long totalBytesSent;            // 发送字节数
    private long totalBytesReceived;        // 接收字节数
    private double avgCpuUsage;             // 平均CPU使用率
    private double avgMemoryUsage;          // 平均内存使用量
    
    // 可用性统计
    private double availability;            // 可用率(%)
    private long totalDowntime;             // 总停机时间(ms)
    private int errorCount;                 // 错误计数
}
```

### 21.2 监控数据模型

```yaml
CapabilityMetricsRecord:
  # 标识
  metricId: "metric-20260301-001"
  capabilityId: "email-send-001"
  capId: "daily-report-email"
  sceneGroupId: "daily-report-group-001"
  
  # 时间
  timestamp: 1704067200000
  period: HOURLY                          # 统计周期: HOURLY, DAILY, WEEKLY, MONTHLY
  
  # 调用统计
  invocations:
    total: 1520
    success: 1500
    failed: 15
    timeout: 5
    
  # 性能统计
  performance:
    avgResponseTime: 125.5
    maxResponseTime: 3500.0
    minResponseTime: 45.0
    p95ResponseTime: 280.0
    p99ResponseTime: 1200.0
    
  # 资源统计
  resources:
    bytesSent: 5242880
    bytesReceived: 10485760
    cpuUsage: 12.5
    memoryUsage: 256.0
    
  # 可用性
  availability:
    rate: 99.02
    downtime: 45000
    errorCount: 20
```

### 21.3 监控API

```java
public interface CapabilityMonitorService {
    
    /**
     * 记录调用
     */
    void recordInvocation(InvocationRecord record);
    
    /**
     * 获取能力指标
     */
    CapabilityMetrics getMetrics(String capabilityId, TimePeriod period);
    
    /**
     * 获取场景能力指标
     */
    Map<String, CapabilityMetrics> getSceneMetrics(String sceneGroupId, TimePeriod period);
    
    /**
     * 获取健康状态
     */
    HealthStatus getHealthStatus(String capabilityId);
    
    /**
     * 设置告警规则
     */
    void setAlertRule(AlertRule rule);
    
    /**
     * 获取告警历史
     */
    List<Alert> getAlertHistory(String capabilityId, long startTime, long endTime);
}
```

### 21.4 告警规则

```yaml
AlertRule:
  ruleId: "alert-email-timeout-001"
  name: "邮件能力超时告警"
  capabilityId: "email-send-001"
  
  condition:
    metric: "timeoutInvocations"
    operator: "GREATER_THAN"
    threshold: 5
    period: HOURLY
    
  actions:
    - type: NOTIFICATION
      targets: ["admin@company.com"]
      template: "邮件能力超时次数超过阈值"
      
    - type: WEBHOOK
      url: "https://monitor.company.com/alert"
      
  severity: WARNING                        # INFO, WARNING, ERROR, CRITICAL
  enabled: true
```

---

## 二十二、能力安全审计

### 22.1 审计日志模型

```yaml
AuditLog:
  # 标识
  logId: "audit-20260301-001"
  
  # 时间
  timestamp: 1704067200000
  
  # 操作信息
  operation:
    type: CAPABILITY_INVOKE                # 操作类型
    action: "invoke"                       # 具体动作
    
  # 主体信息
  subject:
    type: AGENT                            # AGENT, USER, SYSTEM
    id: "agent-llm-001"
    domainId: "user-a-private"
    
  # 客体信息
  object:
    type: CAPABILITY
    id: "email-send-001"
    capAddress: "A0:01"
    
  # 上下文
  context:
    sceneGroupId: "daily-report-group-001"
    sourceIp: "192.168.1.100"
    userAgent: "ooder-agent/1.0"
    
  # 结果
  result:
    status: SUCCESS                        # SUCCESS, FAILURE, DENIED
    errorCode: null
    errorMessage: null
    responseTime: 125
    
  # 详细信息
  details:
    params: {                              # 调用参数（脱敏）
      "to": "m***@company.com",
      "subject": "工作日志"
    }
    metadata: {}
```

### 22.2 操作类型枚举

```java
public enum AuditOperationType {
    // 能力管理
    CAPABILITY_REGISTER,       // 注册能力
    CAPABILITY_UNREGISTER,     // 注销能力
    CAPABILITY_UPDATE,         // 更新能力
    CAPABILITY_PUBLISH,        // 发布能力
    CAPABILITY_DEPRECATE,      // 废弃能力
    
    // 能力绑定
    CAPABILITY_BIND,           // 绑定能力
    CAPABILITY_UNBIND,         // 解绑能力
    CAPABILITY_ENABLE,         // 启用能力
    CAPABILITY_DISABLE,        // 禁用能力
    
    // 能力调用
    CAPABILITY_INVOKE,         // 调用能力
    CAPABILITY_INVOKE_SUCCESS, // 调用成功
    CAPABILITY_INVOKE_FAILURE, // 调用失败
    
    // 权限管理
    PERMISSION_GRANT,          // 授权
    PERMISSION_REVOKE,         // 撤销授权
    PERMISSION_CHECK,          // 权限检查
    
    // 配置管理
    CONFIG_UPDATE,             // 更新配置
    CONFIG_RESET               // 重置配置
}
```

### 22.3 审计服务接口

```java
public interface AuditService {
    
    /**
     * 记录审计日志
     */
    void log(AuditLog auditLog);
    
    /**
     * 查询审计日志
     */
    List<AuditLog> query(AuditQuery query);
    
    /**
     * 按能力查询
     */
    List<AuditLog> queryByCapability(String capabilityId, long startTime, long endTime);
    
    /**
     * 按场景查询
     */
    List<AuditLog> queryByScene(String sceneGroupId, long startTime, long endTime);
    
    /**
     * 按主体查询
     */
    List<AuditLog> queryBySubject(String subjectId, long startTime, long endTime);
    
    /**
     * 导出审计日志
     */
    void export(AuditQuery query, OutputStream output, ExportFormat format);
    
    /**
     * 统计分析
     */
    AuditStatistics analyze(long startTime, long endTime);
}
```

### 22.4 安全策略

```yaml
SecurityPolicy:
  policyId: "policy-capability-access-001"
  name: "能力访问安全策略"
  
  # 敏感能力保护
  sensitiveCapabilities:
    - capabilityId: "email-send-*"
      accessLevel: PRIVATE
      auditLevel: FULL                    # FULL, MINIMAL, NONE
      dataMasking:
        - field: "password"
          pattern: "******"
        - field: "token"
          pattern: "***..."
          
  # 访问控制
  accessControl:
    - capabilityId: "email-*"
      allowedDomains: ["company.com"]
      allowedAgents: ["agent-*"]
      
  # 调用限制
  rateLimit:
    - capabilityId: "email-send-*"
      maxInvocations: 100
      period: HOURLY
      action: THROTTLE                    # THROTTLE, REJECT
      
  # 异常检测
  anomalyDetection:
    enabled: true
    rules:
      - type: UNUSUAL_VOLUME
        threshold: 3.0                    # 3倍标准差
        action: ALERT
      - type: UNUSUAL_TIME
        enabled: true
        action: ALERT
```

---

**文档维护者**: ooder开发团队  
**最后更新**: 2026-03-01
