# 场景引擎规范 v0.8.0

## 1. 概述

### 1.1 文档目的

本文档定义 SceneEngine 的规范，包括：
- Scene Agent 初始化流程
- Skill 挂载机制
- CAP 路由管理
- 离线模式支持

### 1.2 适用范围

- Engine 团队：实现 SceneEngine 核心逻辑
- SDK 团队：实现 Skill 挂载和调用机制
- Skills 团队：开发符合规范的 Skill

---

## 2. 核心概念

### 2.1 SceneAgent

场景是特殊的智能体，具备 Agent 属性：

```java
public class SceneAgent {
    private String agentId;              // scene-{sceneName}-{uuid}
    private String sceneId;              // 场景ID
    private AgentType type;              // PRIMARY | BACKUP | COLLABORATIVE
    private AgentStatus status;          // INITIALIZING | ACTIVE | SUSPENDED | STOPPED
    
    private SceneConfig config;          // 场景配置
    private List<CapBinding> capBindings;// CAP 绑定列表
    private Map<String, Skill> skills;   // 已挂载的 Skills
    
    private SceneGroup group;            // 所属场景组
    private EventBus eventBus;           // 事件总线
    private OfflineManager offlineManager;// 离线管理器
}
```

### 2.2 Agent 类型

| 类型 | 说明 | 使用场景 |
|------|------|----------|
| PRIMARY | 主 Agent | 场景的主要执行者 |
| BACKUP | 备份 Agent | 高可用场景，故障时接管 |
| COLLABORATIVE | 协作 Agent | 多 Agent 协作场景 |

### 2.3 Agent 状态流转

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Agent 状态流转                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐                                                           │
│  │ INITIALIZING│ ─────────────────────────────────────────┐               │
│  └─────────────┘                                          │               │
│         │                                                  │               │
│         │ 初始化完成                                        │               │
│         ▼                                                  │               │
│  ┌─────────────┐     暂停      ┌─────────────┐             │               │
│  │   ACTIVE    │ ────────────→ │  SUSPENDED  │             │               │
│  └─────────────┘               └─────────────┘             │               │
│         │                            │                      │               │
│         │ 停止                        │ 恢复                 │               │
│         ▼                            ▼                      │               │
│  ┌─────────────┐               ┌─────────────┐             │               │
│  │   STOPPED   │ ◄──────────── │   ACTIVE    │             │               │
│  └─────────────┘   错误恢复     └─────────────┘             │               │
│         │                                                  │               │
│         │ 销毁                                              │               │
│         ▼                                                  │               │
│  ┌─────────────┐                                           │               │
│  │  DESTROYED  │ ◄─────────────────────────────────────────┘               │
│  └─────────────┘                                                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. 初始化流程

### 3.1 完整流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    SceneEngine 初始化流程                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 场景加载                                                                │
│     │                                                                        │
│     ├── 下载 scene.yaml 和 scene.md                                        │
│     ├── 解析 agentConfig 配置                                              │
│     └── 验证场景依赖                                                        │
│                                                                             │
│  2. Agent 初始化                                                            │
│     │                                                                        │
│     ├── 创建 SceneAgent 实例                                               │
│     ├── 分配 AgentID (scene-{sceneName}-{uuid})                            │
│     ├── 设置 Agent 角色 (PRIMARY/BACKUP)                                   │
│     ├── 初始化 SceneGroupKey                                               │
│     └── 注册到 EventBus                                                    │
│                                                                             │
│  3. CAP 解析                                                                │
│     │                                                                        │
│     ├── 读取 requiredCapabilities                                          │
│     ├── 读取 optionalCapabilities                                          │
│     ├── 下载 cap.yaml 和 cap.md                                            │
│     └── 验证 CAP 版本兼容性                                                │
│                                                                             │
│  4. Skill 发现与匹配                                                        │
│     │                                                                        │
│     ├── 查询 SkillCenter 已注册 Skills                                     │
│     ├── 按 CAP 匹配 Skills                                                 │
│     ├── 验证 Skill 契约合规性                                              │
│     └── 按优先级排序                                                        │
│                                                                             │
│  5. Skill 挂载                                                              │
│     │                                                                        │
│     ├── 解析 CAP 调用类型                                                  │
│     │   ├── http: 远程 URL 调用                                            │
│     │   ├── local-jar: 本地 JAR 接口调用                                   │
│     │   ├── grpc: gRPC 调用                                                │
│     │   ├── websocket: WebSocket 连接                                      │
│     │   └── udp: UDP 数据报                                                │
│     │                                                                        │
│     ├── 创建 Skill 连接器                                                  │
│     │   ├── HttpSkillConnector                                             │
│     │   ├── LocalJarSkillConnector                                         │
│     │   ├── GrpcSkillConnector                                             │
│     │   ├── UdpSkillConnector                                              │
│     │   └── WebSocketSkillConnector                                        │
│     │                                                                        │
│     ├── 加载 FallbackHandler                                               │
│     └── 注册到 Agent 路由表                                                │
│                                                                             │
│  6. 场景激活                                                                │
│     │                                                                        │
│     ├── 启动 Skill 服务                                                    │
│     ├── 加入 SceneGroup                                                    │
│     ├── 发布 SceneActivatedEvent                                           │
│     └── 开始处理请求                                                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 时序图

```
┌─────────┐    ┌─────────────┐    ┌───────────┐    ┌────────────┐    ┌───────┐
│  User   │    │ SceneEngine │    │SkillCenter│    │ SkillRepo  │    │ Skill │
└────┬────┘    └──────┬──────┘    └─────┬─────┘    └──────┬─────┘    └───┬───┘
     │                │                 │                 │              │
     │ loadScene(id)  │                 │                 │              │
     │───────────────>│                 │                 │              │
     │                │                 │                 │              │
     │                │ getScene(id)    │                 │              │
     │                │────────────────>│                 │              │
     │                │                 │                 │              │
     │                │   scene.yaml    │                 │              │
     │                │<────────────────│                 │              │
     │                │                 │                 │              │
     │                │ getCaps(capIds) │                 │              │
     │                │────────────────>│                 │              │
     │                │                 │                 │              │
     │                │   cap.yaml[]    │                 │              │
     │                │<────────────────│                 │              │
     │                │                 │                 │              │
     │                │ findSkills(caps)│                 │              │
     │                │────────────────>│                 │              │
     │                │                 │                 │              │
     │                │  skillList[]    │                 │              │
     │                │<────────────────│                 │              │
     │                │                 │                 │              │
     │                │ downloadSkill() │                 │              │
     │                │─────────────────────────────────>│              │
     │                │                 │                 │              │
     │                │      skill.jar  │                 │              │
     │                │<─────────────────────────────────│              │
     │                │                 │                 │              │
     │                │ mount()         │                 │              │
     │                │─────────────────────────────────────────────────>│
     │                │                 │                 │              │
     │                │   SkillInstance │                 │              │
     │                │<─────────────────────────────────────────────────│
     │                │                 │                 │              │
     │ SceneActivated │                 │                 │              │
     │<───────────────│                 │                 │              │
     │                │                 │                 │              │
```

---

## 4. Skill 挂载机制

### 4.1 调用类型

| 类型 | 说明 | 配置参数 |
|------|------|----------|
| http | HTTP/HTTPS 远程调用 | baseUrl, timeout, headers |
| local-jar | 本地 JAR 接口调用 | jarPath, mainClass |
| grpc | gRPC 远程调用 | endpoint, tls |
| websocket | WebSocket 双向通信 | endpoint, reconnect, heartbeat |
| udp | UDP 数据报 | host, port |

### 4.2 Connector 接口

```java
public interface SkillConnector {
    
    // 初始化连接
    CompletableFuture<Void> initialize(SkillConfig config);
    
    // 调用 CAP
    <T> CompletableFuture<T> invoke(CapRequest request, Class<T> responseType);
    
    // 检查连接状态
    boolean isConnected();
    
    // 关闭连接
    void close();
}
```

### 4.3 挂载配置

```yaml
# skill-manifest.yaml
spec:
  implements:
    - capId: "40"
      version: "1.0"
      connector:
        type: http
        config:
          baseUrl: https://msg.ooder.net
          timeout: 30000
          retry: 3
          headers:
            Authorization: Bearer ${TOKEN}
      fallback:
        enabled: true
        strategy: queue
        maxQueueSize: 1000
```

---

## 5. CAP 路由管理

### 5.1 路由表结构

```java
public class CapRoutingTable {
    
    // CAP -> Skill 映射
    private Map<String, List<SkillBinding>> capBindings;
    
    // 按优先级获取 Skill
    public SkillBinding getSkill(String capId) {
        List<SkillBinding> bindings = capBindings.get(capId);
        if (bindings == null || bindings.isEmpty()) {
            return null;
        }
        // 返回优先级最高且可用的 Skill
        return bindings.stream()
            .filter(SkillBinding::isAvailable)
            .findFirst()
            .orElse(null);
    }
    
    // 添加绑定
    public void addBinding(String capId, SkillBinding binding) {
        capBindings.computeIfAbsent(capId, k -> new ArrayList<>()).add(binding);
    }
    
    // 移除绑定
    public void removeBinding(String capId, String skillId) {
        List<SkillBinding> bindings = capBindings.get(capId);
        if (bindings != null) {
            bindings.removeIf(b -> b.getSkillId().equals(skillId));
        }
    }
}
```

### 5.2 路由策略

| 策略 | 说明 |
|------|------|
| priority | 按优先级选择 |
| round-robin | 轮询选择 |
| random | 随机选择 |
| least-load | 最小负载选择 |

---

## 6. 离线模式

### 6.1 离线检测

```java
public class OfflineDetector {
    
    private volatile boolean online = true;
    private final EventBus eventBus;
    
    @Scheduled(fixedRate = 5000)
    public void checkConnection() {
        boolean wasOnline = online;
        online = testConnection();
        
        if (wasOnline && !online) {
            // 进入离线模式
            eventBus.publish(new OfflineEvent());
        } else if (!wasOnline && online) {
            // 恢复在线
            eventBus.publish(new OnlineEvent());
        }
    }
}
```

### 6.2 离线策略

| 策略 | 说明 | 适用场景 |
|------|------|----------|
| queue | 请求入队，恢复后同步 | 消息发送、任务提交 |
| cache | 缓存结果返回 | 查询类请求 |
| reject | 直接拒绝请求 | 实时性要求高 |
| custom | 自定义降级逻辑 | 特殊场景 |

### 6.3 FallbackHandler 接口

```java
public interface FallbackHandler {
    
    // 处理降级请求
    <T> CompletableFuture<T> handle(CapRequest request, Class<T> responseType);
    
    // 网络恢复后同步
    void onReconnect();
    
    // 获取队列大小
    int getQueueSize();
}
```

---

## 7. 场景组管理

### 7.1 SceneGroup 结构

```java
public class SceneGroup {
    private String groupId;               // 组ID
    private String sceneId;               // 场景ID
    private List<SceneAgent> members;     // 成员列表
    private SceneAgent leader;            // 组长
    private SceneGroupConfig config;      // 组配置
}
```

### 7.2 组操作

| 操作 | 说明 |
|------|------|
| join | Agent 加入场景组 |
| leave | Agent 离开场景组 |
| sync | 同步场景状态 |
| elect | 选举组长 |
| failover | 故障切换 |

---

## 8. 事件定义

### 8.1 场景事件

| 事件 | 说明 |
|------|------|
| SceneActivatedEvent | 场景激活 |
| SceneDeactivatedEvent | 场景停用 |
| SceneSyncEvent | 场景同步 |

### 8.2 Agent 事件

| 事件 | 说明 |
|------|------|
| AgentJoinedEvent | Agent 加入 |
| AgentLeftEvent | Agent 离开 |
| AgentFailedEvent | Agent 故障 |

### 8.3 CAP 事件

| 事件 | 说明 |
|------|------|
| CapInvokedEvent | CAP 调用 |
| CapFailedEvent | CAP 调用失败 |
| CapFallbackEvent | CAP 降级触发 |

---

## 9. API 接口

### 9.1 SceneEngine 接口

```java
public interface SceneEngine {
    
    // 初始化场景
    CompletableFuture<SceneAgent> initScene(SceneConfig config);
    
    // 挂载 Skill
    void mountSkill(SceneAgent agent, Skill skill, CapConnector connector);
    
    // 卸载 Skill
    void unmountSkill(SceneAgent agent, String skillId);
    
    // 激活场景
    void activateScene(SceneAgent agent);
    
    // 停用场景
    void deactivateScene(SceneAgent agent);
    
    // 调用 CAP
    <T> CompletableFuture<T> invokeCap(SceneAgent agent, String capId, CapRequest request, Class<T> responseType);
}
```

### 9.2 SceneAgent 接口

```java
public interface SceneAgent {
    
    // 获取 Agent 信息
    String getAgentId();
    String getSceneId();
    AgentStatus getStatus();
    
    // Skill 管理
    void mountSkill(Skill skill);
    void unmountSkill(String skillId);
    List<Skill> getMountedSkills();
    
    // CAP 调用
    <T> CompletableFuture<T> invokeCap(String capId, CapRequest request, Class<T> responseType);
    
    // 状态控制
    void suspend();
    void resume();
    void stop();
}
```

---

## 10. WorkerAgent 设计

### 10.1 WorkerAgent 概念

WorkerAgent 是用户看到的"助手"，封装 Skill + CapProvider，通过 agentId 绑定 Device。

```java
public class WorkerAgent {
    
    private String workerId;            // worker-{sceneId}-{name}-{uuid}
    private String name;                // 显示名称，如"资料助手"
    private String description;         // 描述
    
    private String sceneId;             // 所属场景
    private String skillId;             // 关联的 Skill
    private List<String> caps;          // 实现的 CAP 列表
    
    private AgentStatus status;         // IDLE | BUSY | ERROR
    private Task currentTask;           // 当前任务
    
    private String preferredDevice;     // 首选设备
    private DeviceSelector deviceSelector; // 设备选择策略
    
    private SkillConnector connector;   // Skill 连接器
    
    public <T> CompletableFuture<T> invoke(String capId, CapRequest request, Class<T> responseType) {
        return connector.invoke(capId, request, responseType);
    }
    
    private DeviceAgent selectDevice(String capId) {
        if (preferredDevice != null) {
            return deviceRegistry.get(preferredDevice);
        }
        return deviceSelector.select(capId, caps);
    }
}
```

### 10.2 WorkerAgent 与 SceneAgent 关系

```
SceneAgent (场景代理)
├── 包含多个 WorkerAgent
├── 编排 Workflow
├── 决策协调
└── 用户交互

WorkerAgent (工作代理)
├── 属于某个 SceneAgent
├── 执行具体任务
├── 调用 CAP 能力
└── 绑定到 Device
```

---

## 11. Workflow 编排

### 11.1 Workflow 定义

```yaml
scene:
  id: "blog-publish"
  name: "博文发布"
  
  agents:
    - id: research-agent
      name: "资料助手"
      skill: skill-research
      caps: ["A0", "A1"]
      
    - id: writer-agent
      name: "写作助手"
      skill: skill-writer
      caps: ["B0"]
      
    - id: image-agent
      name: "图片助手"
      skill: skill-image
      caps: ["C0"]
      
    - id: publish-agent
      name: "发布助手"
      skill: skill-publisher
      caps: ["A0-A4"]
  
  workflow:
    steps:
      - id: collect
        agent: research-agent
        cap: "A0"
        action: searchMaterials
        output: materials
        
      - id: write
        agent: writer-agent
        cap: "B0"
        action: writeArticle
        input: materials
        output: article
        dependsOn: [collect]
        
      - id: image
        agent: image-agent
        cap: "C0"
        action: generateCover
        input: article
        output: coverImage
        dependsOn: [write]
        
      - id: publish
        agent: publish-agent
        cap: "A0"
        action: publishToPlatforms
        input: [article, coverImage]
        output: publishResult
        dependsOn: [write, image]
        
    triggers:
      - type: schedule
        cron: "0 10 * * *"
      - type: manual
      - type: chat
        intent: "发布博文"
```

### 11.2 WorkflowEngine 接口

```java
public class WorkflowEngine {
    
    private Map<String, WorkflowDefinition> workflows;
    
    public CompletableFuture<WorkflowResult> execute(String sceneId, WorkflowContext context) {
        WorkflowDefinition workflow = workflows.get(sceneId);
        
        for (WorkflowStep step : workflow.getSteps()) {
            WorkerAgent agent = getAgent(step.getAgentId());
            
            if (!checkDependencies(step, context)) {
                continue;
            }
            
            StepResult result = agent.invoke(
                step.getCapId(), 
                context.getInput(step),
                StepResult.class
            ).join();
            
            context.setOutput(step.getId(), result.getOutput());
        }
        
        return CompletableFuture.completedFuture(context.getResult());
    }
    
    private boolean checkDependencies(WorkflowStep step, WorkflowContext context) {
        for (String depId : step.getDependsOn()) {
            if (!context.hasOutput(depId)) {
                return false;
            }
        }
        return true;
    }
}
```

### 11.3 WorkflowStep 定义

```java
public class WorkflowStep {
    
    private String id;                  // 步骤ID
    private String agentId;             // 执行的 WorkerAgent
    private String capId;               // 调用的 CAP
    private String action;              // 执行的动作
    private Object input;               // 输入
    private String output;              // 输出变量名
    private List<String> dependsOn;     // 依赖步骤
    private StepConfig config;          // 步骤配置
}
```

---

## 12. 场景决策机制

### 12.1 决策类型

| 类型 | 说明 | 示例 |
|------|------|------|
| 自动决策 | 有明确规则，自动执行 | 负载均衡、故障转移 |
| 半自动决策 | 用户预设规则，按规则执行 | 设备优先级、时间段规则 |
| 手动决策 | 需要用户确认 | 敏感操作、新设备入网 |

### 12.2 决策触发条件

```
场景需要决策时：
├── 场景有直接支持的 CAP → 直接执行
├── 场景没有直接支持的 CAP → 向 McpAgent 请求
└── 需要用户参与 → McpAgent 协调

用户参与方式：
├── 预设规则：用户提前设置决策规则
├── 实时确认：场景暂停，等待用户确认
└── 异步通知：场景继续，同时通知用户
```

---

## 13. 设备绑定管理

### 13.1 agentId 与 deviceId 绑定

```
设计原则：
├── agentId: 逻辑标识，稳定不变
├── deviceId: 物理标识，可能变化
└── agentId ↔ deviceId 绑定，解决设备更替问题

设备更换流程：
├── 旧设备: agentId ↔ deviceId-old
├── 更换后: agentId ↔ deviceId-new (agentId 不变)
└── 场景配置引用 agentId，无需修改
```

### 13.2 绑定类型

| 类型 | 说明 | 处理 |
|------|------|------|
| 强绑定 | 不可拆分，只能故障设定 | 灯↔开关、温湿度传感器↔采集器 |
| 弱绑定 | 用户可调整 | 场景↔设备 |

### 13.3 强绑定设备故障处理

```
故障检测：
├── 设备离线/无响应
├── RouteAgent 上报 McpAgent
└── McpAgent 标记设备故障

用户介入：
├── McpAgent 通知用户
├── 用户确认故障
└── 用户选择处理方式

处理选项：
├── 更换设备：新设备继承旧设备绑定关系
├── 移除设备：解除所有绑定关系
└── 临时禁用：保留绑定，标记禁用
```

---

## 14. 相关文档

- [架构设计总览](./ARCHITECTURE-V0.8.0.md)
- [CAP 注册表规范](./CAP-REGISTRY-SPEC.md)
- [能力发现协议](./CAPABILITY-DISCOVERY-PROTOCOL.md)
