# SDK 0.8.0 架构分析与 Skills 匹配度

## 一、SDK 拆分架构

### 1.1 三大核心模块

```
┌─────────────────────────────────────────────────────────────────┐
│                    Ooder SDK 0.8.0 架构                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  agent-sdk (Agent 核心)                          │  │
│  │  Maven: net.ooder:agent-sdk:0.8.0            │  │
│  │  职责: Agent 生命周期、通信、能力管理            │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  scene-engine (场景引擎)                          │  │
│  │  Maven: net.ooder:scene-engine:0.8.0           │  │
│  │  职责: 场景管理、Provider 注册、Skill 生命周期    │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  llm-sdk (LLM 服务)                             │  │
│  │  Maven: net.ooder:llm-sdk:0.8.0               │  │
│  │  职责: LLM 模型管理、NLP、记忆、调度           │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 二、agent-sdk 模块详解

### 2.1 包结构

| 包名 | 职责 | 主要类 |
|------|--------|--------|
| `net.ooder.sdk.api.agent` | Agent 管理 | EndAgent, RouteAgent, WorkerAgent, SceneAgent |
| `net.ooder.sdk.api.capability` | 能力请求 | CapabilityRequestApi, CapabilityInfo |
| `net.ooder.sdk.api.cmd` | 命令客户端 | CmdClientProxy, CmdClientConfig |
| `net.ooder.sdk.api.event` | 事件总线 | EventBus, Event, EventHandler |
| `net.ooder.sdk.api.initializer` | 初始化器 | NexusInitializer |
| `net.ooder.sdk.api.llm` | LLM 服务 | LlmService, ChatRequest, FunctionDef |
| `net.ooder.sdk.api.memory` | 记忆桥接 | MemoryBridgeApi, NlpInteractionApi |
| `net.ooder.sdk.api.metadata` | 元数据服务 | FourDimensionMetadata, ChangeLogService |
| `net.ooder.sdk.api.monitoring` | 监控 API | MonitoringApi, AlertDefinition |
| `net.ooder.sdk.api.msg` | 消息客户端 | MsgClientProxy, MsgClientConfig |
| `net.ooder.sdk.api.network` | 网络服务 | NetworkService, LinkInfo |
| `net.ooder.sdk.api.protocol` | 协议中心 | ProtocolHub, CommandPacket |
| `net.ooder.sdk.api.scene` | 场景管理 | SceneGroupManager, SceneMember, SceneSnapshot |
| `net.ooder.sdk.api.scheduler` | 任务调度 | TaskScheduler, TaskInfo |
| `net.ooder.sdk.api.scheduling` | 调度 API | SchedulingApi, ScheduleConfig |
| `net.ooder.sdk.api.security` | 安全服务 | SecurityApi, EncryptionService |
| `net.ooder.sdk.api.share` | 技能共享 | SkillShareService |
| `net.ooder.sdk.api.skill` | 技能服务 | SkillService, SkillInstaller, SkillManifest |
| `net.ooder.sdk.api.storage` | 存储服务 | StorageService |

### 2.2 核心类

```java
// Agent 核心
public interface EndAgent {
    void start();
    void stop();
    AgentState getState();
}

public interface SceneAgent {
    String getAgentId();
    String getSceneId();
    void mountSkill(Skill skill);
    void unmountSkill(String skillId);
}

// 能力调用
public interface CapabilityRequestApi {
    Result<CapabilityResponse> request(CapabilityRequest request);
}

// 事件总线
public interface EventBus {
    void publish(Event event);
    void subscribe(String eventType, EventHandler handler);
}

// 场景管理
public interface SceneGroupManager {
    SceneGroup createSceneGroup(SceneGroupConfig config);
    void joinSceneGroup(SceneGroupKey key);
    void leaveSceneGroup();
}
```

---

## 三、scene-engine 模块详解

### 3.1 包结构

| 包名 | 职责 | 主要类 |
|------|--------|--------|
| `net.ooder.scene.core` | 核心引擎 | SceneEngine, SceneAgent, Result, PageResult |
| `net.ooder.scene.core.provider` | Provider 接口 | BaseProvider, AgentProvider, SecurityProvider, HealthProvider, NetworkProvider, ProtocolProvider, SkillShareProvider |
| `net.ooder.scene.core.skill` | Skill 接口 | LlmProvider, HttpClientProvider, StorageProvider, SchedulerProvider |
| `net.ooder.scene.discovery` | 能力发现 | CapabilityDiscoveryService, DiscoveryProvider |
| `net.ooder.scene.engine` | 引擎管理 | Engine, EngineManager, EngineStats |
| `net.ooder.scene.protocol` | 协议适配 | DiscoveryProtocolAdapter, LoginProtocolAdapter |
| `net.ooder.scene.provider.model` | Provider 模型 | agent, config, health, network, protocol, share, user |

### 3.2 核心 Provider 接口

```java
// 基础 Provider
public interface BaseProvider {
    String getProviderName();
    String getVersion();
    void initialize(SceneEngine engine);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();
}

// Agent Provider
public interface AgentProvider extends BaseProvider {
    Result<EndAgent> createEndAgent(EndAgentInfo info);
    Result<TestCommandResult> testCommand(String agentId, String command);
}

// Security Provider
public interface SecurityProvider extends BaseProvider {
    SecurityStatus getStatus();
    SecurityStats getStats();
    List<SecurityPolicy> listPolicies();
    boolean checkPermission(String userId, String resource, String action);
}

// LLM Provider
public interface LlmProvider {
    String getProviderType();
    List<String> getSupportedModels();
    Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options);
    String complete(String model, String prompt, Map<String, Object> options);
    List<double[]> embed(String model, List<String> texts);
}

// Storage Provider
public interface StorageProvider {
    StorageQuota getQuota();
    List<FileInfo> listFiles(String path);
    FileInfo uploadFile(String path, byte[] data);
    byte[] downloadFile(String path);
}

// Scheduler Provider
public interface SchedulerProvider {
    TaskListResult listTasks();
    TaskExecutionResult executeTask(TaskInfo task);
    TaskScheduleResult scheduleTask(TaskInfo task);
}
```

---

## 四、llm-sdk 模块详解

### 4.1 包结构

| 包名 | 职责 | 主要类 |
|------|--------|--------|
| `sdk.llm` | LLM 核心 | LlmSdk, LlmSdkFactory |
| `sdk.llm.adapter` | LLM 适配器 | MultiLlmAdapterApi, ModelInfo, ProviderInfo |
| `sdk.llm.capability` | 能力 API | CapabilityRequestApi, CapabilityResponse |
| `sdk.llm.memory` | 记忆 API | MemoryBridgeApi, MemoryContent, MemoryQuery |
| `sdk.llm.monitoring` | 监控 API | MonitoringApi, MetricsData, HealthStatus |
| `sdk.llm.nlp` | NLP API | NlpInteractionApi, Entity, Intent, SentimentResult |
| `sdk.llm.scheduling` | 调度 API | SchedulingApi, ResourceAllocation, LoadBalanceResult |
| `sdk.llm.security` | 安全 API | SecurityApi, AuthRequest, AuthResult |

### 4.2 核心服务

```java
// LLM SDK
public interface LlmSdk {
    LlmService getLlmService();
    MemoryBridgeApi getMemoryBridgeApi();
    MonitoringApi getMonitoringApi();
    NlpInteractionApi getNlpInteractionApi();
    SchedulingApi getSchedulingApi();
    SecurityApi getSecurityApi();
}

// 多模型适配
public interface MultiLlmAdapterApi {
    RegisterResult registerProvider(ProviderInfo provider);
    RouteResult routeRequest(RouteRequest request);
    ReleaseResponse releaseProvider(String providerId);
}

// 记忆桥接
public interface MemoryBridgeApi {
    UpdateResult update(MemoryUpdate request);
    ShareResult share(ShareRequest request);
    SyncResult sync(SyncRequest request);
}

// NLP 交互
public interface NlpInteractionApi {
    NlpResponse parse(NlpResponseRequest request);
    SentimentResult analyzeSentiment(String text);
    Entity extractEntities(String text);
}
```

---

## 五、Skills 与 SDK 关系

### 5.1 依赖关系

```
┌─────────────────────────────────────────────────────────────────┐
│                    Skills 依赖关系                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  无 SDK 依赖的 Skills (13个)                    │  │
│  │  skill-common, skill-k8s, skill-scheduler-quartz,  │  │
│  │  skill-market, skill-im, skill-group, skill-business,  │  │
│  │  skill-msg, skill-collaboration, skill-mqtt,       │  │
│  │  skill-hosting, skill-monitor, skill-vfs-local         │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  依赖 agent-sdk 的 Skills (4个)                    │  │
│  │  skill-a2ui, skill-user-auth, skill-org-dingding,  │  │
│  │  skill-org-feishu                                     │  │
│  │  使用: net.ooder.sdk.api.agent.EndAgent            │  │
│  │        net.ooder.sdk.api.OoderSDK                  │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  依赖 scene-engine 的 Skills (15个)                 │  │
│  │  skill-share, skill-security, skill-protocol,        │  │
│  │  skill-network, skill-hosting, skill-health,          │  │
│  │  skill-agent, skill-openwrt, skill-llm-*,        │  │
│  │  skill-httpclient-okhttp                             │  │
│  │  使用: net.ooder.scene.core.Result               │  │
│  │        net.ooder.scene.core.SceneEngine            │  │
│  │        net.ooder.scene.provider.*Provider            │  │
│  │        net.ooder.scene.skill.LlmProvider            │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 5.2 Skills 分类

| 分类 | Skills 数量 | SDK 依赖 | 主要接口 |
|------|-----------|-----------|----------|
| **无 SDK 依赖** | 13 | 无 | 无 |
| **agent-sdk 依赖** | 4 | agent-sdk | EndAgent, OoderSDK |
| **scene-engine 依赖** | 15 | scene-engine | Result, SceneEngine, *Provider, LlmProvider |
| **llm-sdk 依赖** | 0 | llm-sdk | 无 |

---

## 六、接口匹配度分析

### 6.1 scene-engine Provider 接口匹配

| 旧接口 | 新接口 | 匹配度 | 说明 |
|--------|--------|--------|------|
| `BaseProvider` | 无直接对应 | ❌ 0% | 需要适配层 |
| `AgentProvider` | `net.ooder.sdk.api.agent.SceneAgent` | 🔶 60% | 概念变更 |
| `SecurityProvider` | `net.ooder.sdk.api.security.SecurityApi` | 🔶 70% | 方法签名类似 |
| `HealthProvider` | `net.ooder.sdk.api.monitoring.MonitoringApi` | 🔶 50% | 功能合并 |
| `NetworkProvider` | `net.ooder.sdk.api.network.NetworkService` | 🔶 70% | 方法签名类似 |
| `ProtocolProvider` | `net.ooder.sdk.api.protocol.ProtocolHub` | 🔶 60% | 重构较大 |
| `SkillShareProvider` | `net.ooder.sdk.api.share.SkillShareService` | 🔶 70% | 方法签名类似 |
| `LlmProvider` | `net.ooder.sdk.api.llm.LlmService` | ✅ 90% | 方法签名兼容 |
| `HttpClientProvider` | 无直接对应 | ❌ 0% | 需要新实现 |
| `StorageProvider` | `net.ooder.sdk.api.storage.StorageService` | 🔶 70% | 方法签名类似 |
| `SchedulerProvider` | `net.ooder.sdk.api.scheduler.TaskScheduler` | 🔶 70% | 方法签名类似 |

### 6.2 核心类匹配

| 旧类 | 新类 | 匹配度 | 说明 |
|------|------|--------|------|
| `Result` | `net.ooder.sdk.infra.utils.Result` | ✅ 95% | 包名变更 |
| `PageResult` | `net.ooder.sdk.infra.utils.PageResult` | ✅ 95% | 包名变更 |
| `SceneEngine` | `net.ooder.sdk.api.scene.SceneGroupManager` | 🔶 60% | 重构 |
| `SceneAgent` | `net.ooder.sdk.api.agent.SceneAgent` | ✅ 85% | 包名变更 |
| `SceneAgentState` | `net.ooder.sdk.api.agent.SceneAgent$SceneAgentStatus` | ✅ 90% | 枚举名变更 |

---

## 七、迁移方案

### 7.1 场景 A: Skills 继续使用 scene-engine

**适用场景**: Skills 已经稳定，无需迁移

**依赖配置**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>0.8.0</version>
</dependency>
```

**优点**:
- Skills 无需修改
- 保持现有功能

**缺点**:
- 无法使用 agent-sdk 新功能
- 技术债务

### 7.2 场景 B: Skills 迁移到 agent-sdk + llm-sdk

**适用场景**: 需要使用新功能

**依赖配置**:
```xml
<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk</artifactId>
        <version>0.8.0</version>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <version>0.8.0</version>
    </dependency>
</dependencies>
```

**Import 映射**:
```java
// 旧 → 新
import net.ooder.scene.core.Result;
    → import net.ooder.sdk.infra.utils.Result;

import net.ooder.scene.core.SceneEngine;
    → import net.ooder.sdk.api.scene.SceneGroupManager;

import net.ooder.scene.provider.SecurityProvider;
    → import net.ooder.sdk.api.security.SecurityApi;

import net.ooder.scene.provider.AgentProvider;
    → import net.ooder.sdk.api.agent.SceneAgent;

import net.ooder.scene.skill.LlmProvider;
    → import net.ooder.sdk.api.llm.LlmService;
```

### 7.3 场景 C: 混合方案 (推荐)

**策略**:
1. **短期**: scene-engine 添加兼容层，支持旧接口
2. **中期**: 逐个迁移 Skills 到 agent-sdk + llm-sdk
3. **长期**: 移除兼容层，统一使用新 SDK

---

## 八、开发指导

### 8.1 新 Skill 开发

**推荐依赖**:
```xml
<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk</artifactId>
        <version>0.8.0</version>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <version>0.8.0</version>
        <optional>true</optional>
    </dependency>
</dependencies>
```

**代码示例**:
```java
@Component
public class MySkill implements BaseProvider {
    
    private SceneGroupManager sceneManager;
    private LlmService llmService;
    
    @Override
    public void initialize(SceneEngine engine) {
        this.sceneManager = engine.getSceneGroupManager();
        this.llmService = engine.getLlmService();
    }
    
    @Override
    public String getProviderName() {
        return "my-skill";
    }
    
    @Override
    public void start() {
        // 业务逻辑
    }
}
```

### 8.2 现有 Skill 迁移

**步骤 1**: 更新 pom.xml
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>0.8.0</version>
</dependency>
```

**步骤 2**: 更新 import
```java
// 旧
import net.ooder.scene.core.Result;
import net.ooder.scene.provider.SecurityProvider;

// 新
import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.security.SecurityApi;
```

**步骤 3**: 适配接口方法
```java
// 旧
@Override
public SecurityStatus getStatus() {
    // 实现
}

// 新
@Override
public SecurityStatus getStatus() {
    // 实现
}
```

---

## 九、总结

| 指标 | 数值 |
|------|------|
| 总 Skills 数 | 30 |
| 无 SDK 依赖 | 13 (43%) |
| agent-sdk 依赖 | 4 (13%) |
| scene-engine 依赖 | 15 (50%) |
| llm-sdk 依赖 | 0 (0%) |
| 完全兼容 | 13 (43%) |
| 需要迁移 | 17 (57%) |

**推荐方案**: 场景 C (混合方案)

1. 保持 scene-engine 0.8.0 兼容性
2. 新 Skills 使用 agent-sdk + llm-sdk
3. 逐步迁移现有 Skills
