# SDK v2.3 功能模块地址表与任务委派

## 一、SDK v2.3 架构概览

### 1.1 模块结构

```
ooder-sdk/
├── agent-sdk/                          # 父工程 (pom) ⚠️ 不能作为依赖
│   ├── agent-sdk-api/                  # API接口层 (jar) ✅
│   ├── agent-sdk-core/                 # 核心实现层 (jar) ✅
│   ├── skills-framework/               # 技能框架 (jar) ✅
│   ├── llm-sdk-api/                    # LLM轻量级API (jar) ✅
│   └── llm-sdk/                        # LLM完整实现 (jar) ✅
├── scene-engine/                       # 场景引擎 (jar) ✅
├── ooder-annotation/                   # 注解模块 (jar) ✅
├── ooder-common/                       # 通用组件父工程 (pom)
│   ├── ooder-config/                   # 配置模块 (jar) ✅
│   ├── ooder-common-client/            # 客户端 (jar) ✅
│   ├── ooder-server/                   # 服务端 (jar) ✅
│   ├── ooder-vfs-web/                  # VFS Web (jar) ✅
│   ├── ooder-org-web/                  # 组织Web (jar) ✅
│   └── ooder-msg-web/                  # 消息Web (jar) ✅
├── ooder-api/                          # 核心API (jar) ✅
└── ooder-util/                         # 工具类 (jar) ✅
```

### 1.2 包名结构

| 模块 | 包名前缀 | 说明 |
|------|----------|------|
| agent-sdk-api | `net.ooder.sdk.api.*` | Agent、Capability、Scene 接口 |
| agent-sdk-core | `net.ooder.sdk.*` | 核心实现、编排引擎、协议适配 |
| skills-framework | `net.ooder.sdk.skill.*` | 技能加载、代码生成 |
| llm-sdk-api | `net.ooder.llm.api.*` | LLM 轻量级 API |
| llm-sdk | `net.ooder.sdk.llm.*`, `net.ooder.sdk.will.*`, `net.ooder.sdk.story.*` | LLM 完整实现、意志、故事 |
| scene-engine | `net.ooder.scene.*` | 场景引擎、发现、协议、资产 |

---

## 二、功能模块地址表

### 2.1 Agent 体系

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| McpAgent | agent-sdk-core | `net.ooder.sdk.core.agent.impl` | McpAgentImpl |
| RouteAgent | agent-sdk-core | `net.ooder.sdk.core.agent.impl` | RouteAgentImpl |
| WorkerAgent | agent-sdk-core | `net.ooder.sdk.api.agent` | WorkerAgent |
| EndAgent | agent-sdk-core | `net.ooder.sdk.api.agent` | EndAgent |
| Agent接口 | agent-sdk-api | `net.ooder.sdk.api.agent` | Agent, AgentType |

### 2.2 能力体系

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| CapRegistry | agent-sdk-core | `net.ooder.sdk.api.capability` | CapRegistry |
| CapabilityInvoker | agent-sdk-core | `net.ooder.sdk.core.capability.impl` | CapabilityInvokerImpl |
| CapabilityDeclaration | agent-sdk-core | `net.ooder.sdk.a2a.capability` | CapabilityDeclaration |
| CapabilityRouter | agent-sdk-core | `net.ooder.sdk.orchestration` | CapabilityRouter, CapabilityRouterImpl |

### 2.3 A2A 通信

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| A2A 消息 | agent-sdk-core | `net.ooder.sdk.a2a.message` | A2AMessage, TaskSendMessage, TaskGetMessage |
| SkillCard | agent-sdk-core | `net.ooder.sdk.a2a.capability` | SkillCard, SkillCardManager |
| CapabilityRegistry | agent-sdk-core | `net.ooder.sdk.a2a.capability` | CapabilityRegistry |

### 2.4 LLM 体系

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| LLM 服务 | llm-sdk | `net.ooder.sdk.llm` | LlmSdk, LlmSdkFactory |
| LLM 接口 | llm-sdk-api | `net.ooder.llm.api` | LlmConfig |
| LLM 服务实现 | agent-sdk-core | `net.ooder.sdk.service.llm` | LlmServiceImpl |
| NLP 交互 | agent-sdk-core | `net.ooder.sdk.api.nlp` | NlpInteractionApi |

### 2.5 意志与故事

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| WillManager | llm-sdk | `net.ooder.sdk.will` | WillManager, WillParser, WillExecutor |
| StoryOrchestrator | agent-sdk-core | `net.ooder.sdk.orchestration` | StoryOrchestrator, StoryOrchestratorImpl |
| UserStory | llm-sdk | `net.ooder.sdk.story` | UserStory, StoryStep |

### 2.6 技能框架

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| SkillService | agent-sdk-core | `net.ooder.sdk.service.skill` | SkillService |
| SkillDiscovery | agent-sdk-core | `net.ooder.sdk.discovery` | SkillDiscoveryService |
| SkillYamlParser | agent-sdk-core | `net.ooder.sdk.discovery` | SkillYamlParser |
| SkillInterfaceGenerator | agent-sdk-core | `net.ooder.sdk.generator` | SkillInterfaceGenerator |
| DriverGenerator | agent-sdk-core | `net.ooder.sdk.generator` | DriverGenerator |

### 2.7 场景引擎

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| SceneEngine | scene-engine | `net.ooder.scene.core` | SceneEngine, SceneEngineImpl |
| SceneClient | scene-engine | `net.ooder.scene.core` | SceneClient, SceneClientImpl |
| SceneContext | scene-engine | `net.ooder.scene.core` | SceneContext |
| SceneManager | scene-engine | `net.ooder.scene.core` | SceneLifecycleManager |

### 2.8 发现机制

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| CapabilityDiscovery | scene-engine | `net.ooder.scene.discovery` | CapabilityDiscoveryService |
| DiscoveryProvider | scene-engine | `net.ooder.scene.discovery.provider` | UdpDiscoveryProvider, MdnsDiscoveryProvider |
| GitDiscoverer | agent-sdk-core | `net.ooder.sdk.discovery.git` | GitHubDiscoverer, GiteeDiscoverer |

### 2.9 协议适配

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| LoginProtocol | scene-engine | `net.ooder.scene.protocol.impl` | LoginProtocolAdapterImpl |
| DiscoveryProtocol | scene-engine | `net.ooder.scene.protocol.impl` | DiscoveryProtocolAdapterImpl |
| MdnsDiscovery | scene-engine | `net.ooder.scene.protocol` | MdnsDiscoveryService |
| UdpDiscovery | scene-engine | `net.ooder.scene.protocol` | UdpDiscoveryService |

### 2.10 资产治理

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| AssetGovernance | scene-engine | `net.ooder.scene.asset` | AssetGovernance, AssetGovernanceImpl |
| DigitalAsset | scene-engine | `net.ooder.scene.asset` | DigitalAssetImpl, DigitalAssetBuilder |
| DeviceAsset | scene-engine | `net.ooder.scene.asset` | DeviceAssetManager, DeviceAssetManagerImpl |
| DataAsset | scene-engine | `net.ooder.scene.asset` | DataAssetManager, DataAssetManagerImpl |

### 2.11 工作流

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| WorkflowEngine | scene-engine | `net.ooder.scene.workflow` | WorkflowEngine, WorkflowEngineImpl |
| WorkflowDefinition | scene-engine | `net.ooder.scene.workflow` | WorkflowDefinition, WorkflowStep |
| WorkflowContext | scene-engine | `net.ooder.scene.workflow` | WorkflowContext, WorkflowResult |

### 2.12 安全与存储

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| SecurityService | agent-sdk-core | `net.ooder.sdk.api.security` | SecurityService, SecurityServiceImpl |
| StorageService | agent-sdk-core | `net.ooder.sdk.api.storage` | StorageService, StorageServiceImpl |
| TokenManager | scene-engine | `net.ooder.scene.session.impl` | TokenManagerImpl |
| SessionManager | scene-engine | `net.ooder.scene.session.impl` | SessionManagerImpl |

### 2.13 事件体系

| 功能 | 模块 | 包路径 | 核心类 |
|------|------|--------|--------|
| SceneEvent | scene-engine | `net.ooder.scene.event` | SceneEvent, SceneEventType |
| UserEvent | scene-engine | `net.ooder.scene.event.user` | UserEvent |
| SkillEvent | scene-engine | `net.ooder.scene.event.skill` | SkillEvent |
| CapabilityEvent | scene-engine | `net.ooder.scene.event.capability` | CapabilityEvent |

---

## 三、A2UI 开发任务委派

### 3.1 任务与模块映射

| 任务 | 所需能力 | 模块 | 包路径 | 委派团队 |
|------|----------|------|--------|----------|
| UI 代码生成 | AIGC 文本生成 | llm-sdk | `net.ooder.sdk.llm` | LLM-SDK 团队 |
| 设计工具集成 | MCP 协议 | agent-sdk-core | `net.ooder.sdk.a2a` | SDK 团队 |
| 工作流编排 | Workflow 执行 | scene-engine | `net.ooder.scene.workflow` | SceneEngine 团队 |
| 能力注册 | CapRegistry | agent-sdk-core | `net.ooder.sdk.api.capability` | SDK 团队 |
| Agent 通信 | A2A 消息 | agent-sdk-core | `net.ooder.sdk.a2a.message` | SDK 团队 |
| 技能加载 | Skill 框架 | agent-sdk-core | `net.ooder.sdk.discovery` | SDK 团队 |
| 场景管理 | Scene 引擎 | scene-engine | `net.ooder.scene.core` | SceneEngine 团队 |

### 3.2 委派任务详情

#### 3.2.1 LLM-SDK 团队任务

| 任务ID | 任务名称 | 模块 | 包路径 | 优先级 |
|--------|----------|------|--------|--------|
| LLM-001 | LLM 服务接口完善 | llm-sdk | `net.ooder.sdk.llm` | P0 |
| LLM-002 | AIGC 文本生成优化 | llm-sdk | `net.ooder.sdk.llm` | P0 |
| LLM-003 | 多模型适配器 | llm-sdk | `net.ooder.sdk.llm` | P1 |
| LLM-004 | 记忆体系实现 | llm-sdk | `net.ooder.sdk.llm` | P1 |

**交付物路径**：
- `E:\github\ooder-sdk\agent-sdk\llm-sdk\src\main\java\net\ooder\sdk\llm\`

#### 3.2.2 SDK 团队任务

| 任务ID | 任务名称 | 模块 | 包路径 | 优先级 |
|--------|----------|------|--------|--------|
| SDK-001 | CapRegistry 完善 | agent-sdk-core | `net.ooder.sdk.api.capability` | P0 |
| SDK-002 | A2A 消息处理完善 | agent-sdk-core | `net.ooder.sdk.a2a.message` | P0 |
| SDK-003 | SkillCardManager 实现 | agent-sdk-core | `net.ooder.sdk.a2a.capability` | P1 |
| SDK-004 | StoryOrchestrator 完善 | agent-sdk-core | `net.ooder.sdk.orchestration` | P1 |
| SDK-005 | CapabilityRouter 优化 | agent-sdk-core | `net.ooder.sdk.orchestration` | P1 |
| SDK-006 | SkillDiscoveryService 完善 | agent-sdk-core | `net.ooder.sdk.discovery` | P1 |

**交付物路径**：
- `E:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\`

#### 3.2.3 SceneEngine 团队任务

| 任务ID | 任务名称 | 模块 | 包路径 | 优先级 |
|--------|----------|------|--------|--------|
| SCENE-001 | SceneEngine 核心完善 | scene-engine | `net.ooder.scene.core` | P0 |
| SCENE-002 | WorkflowEngine 优化 | scene-engine | `net.ooder.scene.workflow` | P0 |
| SCENE-003 | DiscoveryProvider 完善 | scene-engine | `net.ooder.scene.discovery.provider` | P1 |
| SCENE-004 | AssetGovernance 完善 | scene-engine | `net.ooder.scene.asset` | P1 |
| SCENE-005 | SessionManager 完善 | scene-engine | `net.ooder.scene.session.impl` | P1 |

**交付物路径**：
- `E:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\`

#### 3.2.4 Skills 团队任务

| 任务ID | 任务名称 | 模块 | 包路径 | 优先级 |
|--------|----------|------|--------|--------|
| SKILL-001 | A2UI Skill 实现 | skills/skill-a2ui | `net.ooder.skill.a2ui` | P0 |
| SKILL-002 | A2UI 能力注册 | skills/skill-a2ui | `net.ooder.skill.a2ui` | P0 |
| SKILL-003 | A2UI API 端点 | skills/skill-a2ui | `net.ooder.skill.a2ui` | P0 |
| SKILL-004 | A2UI 工作流定义 | skills/skill-a2ui | `net.ooder.skill.a2ui` | P1 |

**交付物路径**：
- `e:\github\ooder-skills\skills\skill-a2ui\src\main\java\net\ooder\skill\a2ui\`

---

## 四、依赖关系图

```
┌─────────────────────────────────────────────────────────────────┐
│                    A2UI Skill 依赖关系                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  skill-a2ui                                                     │
│      │                                                          │
│      ├── agent-sdk-api (接口定义)                               │
│      │       │                                                  │
│      │       └── ooder-api, ooder-util                          │
│      │                                                          │
│      ├── agent-sdk-core (核心实现)                              │
│      │       │                                                  │
│      │       ├── llm-sdk (LLM 能力)                             │
│      │       │       │                                          │
│      │       │       └── llm-sdk-api                            │
│      │       │                                                  │
│      │       └── skills-framework (技能框架)                    │
│      │                                                          │
│      ├── scene-engine (场景引擎)                                │
│      │       │                                                  │
│      │       └── agent-sdk-core                                 │
│      │                                                          │
│      └── ooder-annotation (UI 注解)                             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 五、Maven 依赖配置

```xml
<dependencies>
    <!-- ⚠️ 重要：agent-sdk 是父工程，不能作为依赖 -->
    
    <!-- API 接口层 (轻量级) -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk-api</artifactId>
        <version>2.3</version>
    </dependency>
    
    <!-- 核心实现层 (完整功能) -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk-core</artifactId>
        <version>2.3</version>
    </dependency>
    
    <!-- LLM 完整实现 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <version>2.3</version>
    </dependency>
    
    <!-- 场景引擎 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine</artifactId>
        <version>2.3</version>
    </dependency>
    
    <!-- UI 注解 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-annotation</artifactId>
        <version>2.3</version>
    </dependency>
</dependencies>
```

---

## 六、迁移注意事项

### 6.1 包名变更

| 旧包名 | 新包名 | 说明 |
|--------|--------|------|
| `net.ooder.sdk.cmd.*` | `net.ooder.sdk.api.cmd.*` | API 层分离 |
| `net.ooder.sdk.msg.*` | `net.ooder.sdk.api.msg.*` | API 层分离 |
| `net.ooder.scene.drivers.*` | `net.ooder.scene.skills.*` | 技能化改造 |

### 6.2 模块变更

| 旧模块 | 新模块 | 说明 |
|--------|--------|------|
| `vfs-skill` | `scene-engine` | 功能合并 |
| `org-skill` | `scene-engine` | 功能合并 |
| `msg-skill` | `scene-engine` | 功能合并 |
| `mqtt-skill` | `scene-engine` | 功能合并 |

---

## 七、联系方式

| 团队 | 负责范围 | 联系方式 |
|------|----------|----------|
| SDK 团队 | agent-sdk-core, skills-framework | SDK Team |
| LLM-SDK 团队 | llm-sdk, llm-sdk-api | LLM Team |
| SceneEngine 团队 | scene-engine | Scene Team |
| Skills 团队 | skill-a2ui, skill-* | Skills Team |

---

**文档版本**：v1.0  
**创建日期**：2026-02-27  
**最后更新**：2026-02-27
