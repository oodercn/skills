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

## 10. 相关文档

- [架构设计总览](./ARCHITECTURE-V0.8.0.md)
- [CAP 注册表规范](./CAP-REGISTRY-SPEC.md)
- [能力发现协议](./CAPABILITY-DISCOVERY-PROTOCOL.md)
