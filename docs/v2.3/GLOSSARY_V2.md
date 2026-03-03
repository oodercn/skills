# Ooder 核心术语表 v2.0

## 文档信息

| 项目 | 说明 |
|------|------|
| 版本 | v2.0 |
| 日期 | 2026-03-02 |
| 状态 | 正式发布 |
| 适用范围 | skill-scene模块、agent-sdk模块、南向协议、所有需求规格文档 |

---

## 一、术语分类体系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        术语分类体系                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   第一层：能力域术语                                                         │
│   ══════════════════                                                        │
│   ├── 能力定义术语：Capability, CapabilityType, CapabilityBinding          │
│   ├── 能力类型术语：ATOMIC, COMPOSITE, SCENE, DRIVER, COLLABORATIVE        │
│   └── 能力驱动术语：mainFirst, selfCheck, selfStart, selfDrive             │
│                                                                             │
│   第二层：场景域术语                                                         │
│   ══════════════════                                                        │
│   ├── 场景能力术语：SceneCapability, collaborativeCapabilities             │
│   ├── 场景组术语：SceneGroup, SceneGroupKey, SceneGroupState               │
│   └── 参与者术语：Participant, Role, PRIMARY, BACKUP                        │
│                                                                             │
│   第三层：Agent域术语                                                        │
│   ══════════════════                                                        │
│   ├── Agent类型：MCP Agent, Route Agent, End Agent, SuperAgent             │
│   ├── Agent通信：Link, CapAddress, ConnectorType                            │
│   └── Agent状态：AgentStatus, HealthStatus                                  │
│                                                                             │
│   第四层：南向协议术语                                                       │
│   ══════════════════                                                        │
│   ├── 协议接口：DiscoveryProtocol, LoginProtocol, CollaborationProtocol    │
│   ├── 离线支持：OfflineService, OfflineBuffer, ConflictResolution          │
│   └── 事件管理：EventBus, EventType, EventHandler                           │
│                                                                             │
│   第五层：Skill域术语                                                        │
│   ══════════════════                                                        │
│   ├── Skill定义：SkillManifest, SkillPackage, SkillDefinition              │
│   ├── Skill管理：SkillPackageManager, SkillService, SkillDiscoverer        │
│   └── 依赖管理：Dependency, DependencyResolver, DependencyGraph            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、能力域术语

### 2.1 能力定义术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **能力** | Capability | 系统中可被调用的功能单元，描述"能做什么" | 用户故事 | 保留 |
| **能力标识** | capabilityId | 能力的全局唯一标识，跨场景使用 | 系统设计 | 保留 |
| **场景能力标识** | capId | 能力在特定场景内的短标识，用于场景内引用 | 系统设计 | 保留 |
| **能力需求标识** | capDefId | 场景定义中描述的能力需求标识，用于匹配实际能力 | 用户故事 | 保留 |
| **能力绑定** | CapabilityBinding | 能力与场景组的关联关系，包含执行组件信息 | 用户故事 | 保留 |
| **能力绑定状态** | CapabilityBindingStatus | 能力绑定的状态：PENDING/BINDING/ACTIVE/INACTIVE/ERROR | 生命周期 | 保留 |
| **CAP地址** | CapAddress | 能力在场景内的路由地址，用于权限控制和路由 | 系统设计 | 保留 |
| **CAP地址区域** | AddressZone | CAP地址的权限区域：SYSTEM/GENERAL/EXTENSION | 权限控制 | 保留 |

### 2.2 能力类型术语（扩展）

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **原子能力** | ATOMIC_CAPABILITY | 单一功能、不可分解的能力，无子能力 | 能力驱动 | **新增** |
| **组合能力** | COMPOSITE_CAPABILITY | 组合多个原子能力的能力，无涌现行为 | 能力驱动 | **新增** |
| **场景能力** | SCENE_CAPABILITY | 自驱型SuperAgent能力，包含子能力和驱动能力，可涌现新行为 | 能力驱动 | **新增** |
| **驱动能力** | DRIVER_CAPABILITY | 提供驱动源头的特殊能力类型，包括意图/时间/事件驱动 | 用户故事 | **新增** |
| **协作能力** | COLLABORATIVE_CAPABILITY | 跨场景协作的能力，通过接口暴露 | 能力驱动 | **新增** |
| **服务能力** | SERVICE_CAPABILITY | 业务服务、API服务类能力 | 业务抽象 | 保留 |
| **AI能力** | AI_CAPABILITY | LLM、机器学习类能力 | 业务抽象 | 保留 |
| **工具能力** | TOOL_CAPABILITY | 工具类能力 | 业务抽象 | 保留 |
| **连接器能力** | CONNECTOR_CAPABILITY | 连接协议类能力 | 业务抽象 | 保留 |
| **数据能力** | DATA_CAPABILITY | 数据存储、处理类能力 | 业务抽象 | 保留 |

### 2.3 能力驱动术语（新增）

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **自驱入口** | mainFirst | 场景能力的启动入口，包含自检、自启、自驱、协作启动 | 用户故事 | **新增** |
| **自检** | selfCheck | 场景能力检查子能力就绪状态的过程 | 能力驱动 | **新增** |
| **自启** | selfStart | 场景能力初始化子能力并绑定的过程 | 能力驱动 | **新增** |
| **自驱** | selfDrive | 场景能力驱动自身运行的过程 | 能力驱动 | **新增** |
| **自愈** | selfHeal | 场景能力故障时自动恢复的过程 | 能力驱动 | **新增** |
| **意图接收** | intent-receiver | 接收用户意图并触发场景启动的驱动能力 | 用户故事 | **新增** |
| **时间驱动** | scheduler | 监听时间事件并触发能力调用的驱动能力 | 用户故事 | **新增** |
| **事件监听** | event-listener | 监听业务事件并触发能力调用的驱动能力 | 用户故事 | **新增** |
| **能力调用** | capability-invoker | 管理能力调用链的驱动能力 | 用户故事 | **新增** |
| **协作协调** | collaboration-coordinator | 协调协作场景的驱动能力 | 用户故事 | **新增** |
| **能力调用链** | capabilityChains | 能力的有序调用序列，支持条件和分支 | 用户故事 | **新增** |
| **能力涌现** | CapabilityEmergence | 场景能力协调子能力产生新行为的过程 | 用户故事 | **新增** |

---

## 三、场景域术语

### 3.1 场景能力术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **场景能力** | SceneCapability | 自驱型SuperAgent能力，包含子能力和驱动能力 | 能力驱动 | **替代SceneDefinition** |
| **场景能力定义** | SceneCapabilityDef | Skill清单中定义的场景能力配置 | 能力驱动 | **新增** |
| **协作能力引用** | CollaborativeCapabilityRef | 场景能力中引用其他协作能力的配置 | 能力驱动 | **新增** |
| **主场景** | primaryScene | 场景能力的主入口配置（旧术语，已被mainFirst替代） | 系统设计 | **废弃** |

### 3.2 场景组术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **场景组** | SceneGroup | 共享KEY和VFS资源的Agent集合，实现场景的协作和故障切换 | 南向协议 | 保留 |
| **场景组标识** | groupId | 场景组的全局唯一标识 | 南向协议 | 保留 |
| **场景组密钥** | SceneGroupKey | 场景组的共享密钥，包含权限定义和成员列表 | 南向协议 | 保留 |
| **主密钥** | masterKey | 场景组的主密钥，使用Shamir算法分片存储 | 南向协议 | 保留 |
| **访问密钥** | accessKey | 场景组的访问密钥，用于签名验证 | 南向协议 | 保留 |
| **密钥分片** | keyShare | 主密钥的分片，用于Shamir秘密共享恢复 | 南向协议 | 保留 |
| **场景组状态** | SceneGroupState | 场景组的运行时状态，包含成员状态和共享数据 | 南向协议 | 保留 |
| **场景组状态枚举** | SceneGroupStatus | 场景组生命周期状态：CREATING/ACTIVE/PAUSED/DESTROYED | 生命周期 | 保留 |

### 3.3 参与者术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **参与者** | Participant | 场景中的活动主体，可以是用户、Agent或SuperAgent | 用户故事 | 保留 |
| **用户参与者** | UserParticipant | 人类用户作为场景参与者 | 用户故事 | 保留 |
| **Agent参与者** | AgentParticipant | 单一Agent作为场景参与者，负责执行具体能力 | 用户故事 | 保留 |
| **SuperAgent参与者** | SuperAgentParticipant | SuperAgent作为场景参与者，协调多个子Agent完成复杂任务 | 用户故事 | 保留 |
| **角色** | Role | 参与者在场景中的职责定义，决定其可访问的能力 | 用户故事 | 保留 |
| **参与者状态** | ParticipantStatus | 参与者在场景中的状态：JOINED/ACTIVE/SUSPENDED/LEFT | 生命周期 | 保留 |
| **主角色** | PRIMARY | 场景组的主要RouteAgent，负责消息路由、KEY管理、任务分配 | 南向协议 | 保留 |
| **备角色** | BACKUP | 场景组的备用RouteAgent，负责状态监听、任务执行、故障接管 | 南向协议 | 保留 |

---

## 四、Agent域术语

### 4.1 Agent类型术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **Agent** | Agent | 能力执行者，负责具体业务逻辑的执行 | 系统设计 | 保留 |
| **主控智能体** | MCP Agent | 主控智能体，负责资源管理、任务调度、安全认证 | 南向协议 | 保留 |
| **路由智能体** | Route Agent | 路由智能体，负责消息路由、负载均衡、网络管理 | 南向协议 | 保留 |
| **终端智能体** | End Agent | 终端智能体，负责与外部设备和系统交互、数据采集和执行 | 南向协议 | 保留 |
| **场景Agent** | SceneAgent | 在场景上下文中运行的Agent，继承Agent接口 | 系统设计 | 保留 |
| **SuperAgent** | SuperAgent | 超级Agent，协调多个子Agent完成复杂任务 | 用户故事 | 保留 |
| **Agent状态** | AgentStatus | Agent的生命周期状态：CREATED/INITIALIZING/RUNNING/STOPPED | 生命周期 | 保留 |

### 4.2 Agent通信术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **链路** | Link | Agent间的通信链路，负责连接和数据传输 | 系统设计 | 保留 |
| **链路类型** | LinkType | 链路的通信方式：DIRECT/RELAY/TUNNEL/MULTICAST/P2P | 系统设计 | 保留 |
| **链路状态** | LinkStatus | 链路的运行状态：ACTIVE/INACTIVE/DEGRADED/FAILED | 运行时 | 保留 |
| **连接类型** | ConnectorType | 能力的连接协议：HTTP/GRPC/WEBSOCKET/LOCAL_JAR | 系统设计 | 保留 |
| **通信模式** | CommunicationMode | Agent间的通信模式：星型/链式/网状/混合 | 南向协议 | 保留 |

### 4.3 Agent健康术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **健康状态** | HealthStatus | 能力或Agent的健康程度 | 运维监控 | 保留 |
| **心跳间隔** | HeartbeatInterval | PRIMARY发送心跳的时间间隔，默认5秒 | 南向协议 | 保留 |
| **超时阈值** | TimeoutThreshold | 连续超时次数阈值，默认3次 | 南向协议 | 保留 |
| **故障检测时间** | FailureDetectionTime | 从故障发生到被检测到的时间，默认15秒 | 南向协议 | 保留 |

---

## 五、南向协议术语

### 5.1 协议接口术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **南向协议** | Southbound Protocol | Agent之间通信的底层协议 | 系统设计 | 保留 |
| **发现协议** | DiscoveryProtocol | 节点发现协议，支持UDP/DHT/SkillCenter/mDNS | 南向协议 | 保留 |
| **登录协议** | LoginProtocol | 本地认证协议，支持离线认证和会话管理 | 南向协议 | 保留 |
| **协作协议** | CollaborationProtocol | 场景组协作协议，支持任务分配和状态同步 | 南向协议 | 保留 |
| **协议版本** | protocol_version | 协议版本号，当前为"0.7.3" | 南向协议 | 保留 |
| **命令标识** | command_id | 命令唯一标识，UUID格式 | 南向协议 | 保留 |
| **操作类型** | operation | 协议操作类型，如agent.discover、agent.register等 | 南向协议 | 保留 |

### 5.2 发现方法术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **UDP广播发现** | UDP Broadcast | 通过UDP广播发现局域网节点 | 南向协议 | 保留 |
| **DHT发现** | DHT (Kademlia) | 通过DHT网络发现广域网节点 | 南向协议 | 保留 |
| **技能中心发现** | SkillCenter API | 通过中心化目录发现节点 | 南向协议 | 保留 |
| **mDNS发现** | mDNS/DNS-SD | 通过mDNS服务发现节点 | 南向协议 | 保留 |
| **发现过滤器** | DiscoveryFilter | 用于筛选发现结果的过滤条件 | 用户故事 | 保留 |
| **发现结果** | DiscoveryResult | 发现操作的返回结果 | 南向协议 | 保留 |
| **节点信息** | PeerInfo | 发现到的节点信息 | 南向协议 | 保留 |

### 5.3 离线支持术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **离线服务** | OfflineService | 离线服务，支持网络断开时的场景运行 | 南向协议 | 保留 |
| **离线模式** | offline_mode | 离线模式标识，表示当前是否处于离线状态 | 南向协议 | 保留 |
| **离线缓冲** | OfflineBuffer | 离线模式下暂存状态变更的缓冲区 | 南向协议 | 保留 |
| **待同步变更** | PendingChange | 离线期间产生的待同步变更 | 南向协议 | 保留 |
| **冲突解决策略** | ConflictResolution | 离线同步时的冲突解决策略：last-write/merge/manual | 南向协议 | 保留 |
| **同步间隔** | syncInterval | 离线模式下定时同步的时间间隔 | 南向协议 | 保留 |

### 5.4 事件管理术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **事件总线** | EventBus | 统一事件管理和模块解耦的事件总线 | 南向协议 | 保留 |
| **事件类型** | EventType | 事件的分类类型 | 南向协议 | 保留 |
| **事件处理器** | EventHandler | 处理特定类型事件的回调函数 | 南向协议 | 保留 |
| **场景组创建事件** | SceneGroupCreatedEvent | 场景组创建时发布的事件 | 南向协议 | 保留 |
| **成员加入事件** | MemberJoinedEvent | 成员加入场景组时发布的事件 | 南向协议 | 保留 |
| **成员离开事件** | MemberLeftEvent | 成员离开场景组时发布的事件 | 南向协议 | 保留 |
| **主节点变更事件** | PrimaryChangedEvent | 主节点变更时发布的事件 | 南向协议 | 保留 |
| **任务分配事件** | TaskAssignedEvent | 任务分配时发布的事件 | 南向协议 | 保留 |
| **任务完成事件** | TaskCompletedEvent | 任务完成时发布的事件 | 南向协议 | 保留 |
| **网络断开事件** | NetworkDisconnectedEvent | 网络断开时发布的事件 | 南向协议 | 保留 |
| **网络连接事件** | NetworkConnectedEvent | 网络连接时发布的事件 | 南向协议 | 保留 |
| **离线模式启用事件** | OfflineModeEnabledEvent | 离线模式启用时发布的事件 | 南向协议 | 保留 |
| **同步完成事件** | SyncCompletedEvent | 同步完成时发布的事件 | 南向协议 | 保留 |

### 5.5 任务协作术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **任务信息** | TaskInfo | 任务的数据结构，包含ID、类型、参数、截止时间等 | 南向协议 | 保留 |
| **任务标识** | taskId | 任务的唯一标识 | 南向协议 | 保留 |
| **任务类型** | TaskType | 任务的分类：DATA_PROCESSING/SKILL_INVOCATION/RESOURCE_SYNC等 | 南向协议 | 保留 |
| **任务结果** | TaskResult | 任务执行的结果数据 | 南向协议 | 保留 |
| **任务状态** | TaskStatus | 任务的状态：PENDING/RUNNING/COMPLETED/FAILED | 南向协议 | 保留 |
| **任务优先级** | priority | 任务的优先级，数值越高优先级越高 | 南向协议 | 保留 |
| **任务截止时间** | deadline | 任务的最晚完成时间 | 南向协议 | 保留 |

---

## 六、Skill域术语

### 6.1 Skill定义术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **技能** | Skill | 可安装的功能包，包含能力定义、依赖和配置 | 用户故事 | 保留 |
| **技能清单** | SkillManifest | Skill的元数据定义，包含能力、依赖、场景配置 | 系统设计 | 保留 |
| **技能包** | SkillPackage | 包含能力定义、依赖和配置的完整发布包 | 用户故事 | 保留 |
| **技能定义** | SkillDefinition | Skill的运行时定义，包含场景ID、能力列表 | 系统设计 | 保留 |
| **技能标识** | skillId | Skill的全局唯一标识 | 系统设计 | 保留 |
| **技能版本** | version | Skill的版本号 | 系统设计 | 保留 |
| **技能类型** | SkillType | Skill的分类：DRIVER/SERVICE/MANAGEMENT/AI等 | 业务抽象 | 保留 |

### 6.2 Skill管理术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **技能包管理器** | SkillPackageManager | Skill的安装、卸载、更新管理器 | 系统设计 | 保留 |
| **技能服务** | SkillService | Skill的运行时服务 | 系统设计 | 保留 |
| **技能发现器** | SkillDiscoverer | Skill发现器的接口定义，支持多种发现渠道 | 系统设计 | 保留 |
| **技能市场** | SkillMarket | Skill的在线市场，支持搜索和安装 | 用户故事 | 保留 |
| **已安装技能** | InstalledSkill | 已安装的Skill信息 | 系统设计 | 保留 |
| **安装模式** | InstallMode | Skill的安装模式：LOCAL/REMOTE/DEVELOPMENT | 系统设计 | 保留 |
| **技能状态** | SkillStatus | Skill的状态：INSTALLED/RUNNING/STOPPED/ERROR | 生命周期 | 保留 |

### 6.3 依赖管理术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **依赖** | Dependency | Skill对其他Skill或能力的依赖声明 | 用户故事 | 保留 |
| **能力依赖** | CapabilityDependency | 能力对其他能力的依赖声明 | 用户故事 | 保留 |
| **场景依赖** | SceneDependency | 场景对其他场景的依赖声明 | 能力驱动 | **新增** |
| **依赖解析器** | DependencyResolver | 解析依赖关系的组件 | 系统设计 | 保留 |
| **依赖图** | DependencyGraph | 表示依赖关系的有向图 | 系统设计 | 保留 |
| **依赖状态** | DependencyStatus | 依赖的满足情况：SATISFIED/MISSING/INCOMPATIBLE | 运行时 | 保留 |
| **版本兼容性** | VersionCompatibility | 版本匹配策略：EXACT/COMPATIBLE/RANGE/LATEST | 系统设计 | 保留 |
| **版本范围** | VersionRange | 定义最小和最大版本边界的范围对象 | 依赖管理 | 保留 |
| **拓扑排序** | TopologicalSort | 计算依赖安装顺序的算法 | 系统设计 | **新增** |
| **循环依赖** | CircularDependency | 依赖关系中存在的循环 | 系统设计 | **新增** |

---

## 七、监控与安全术语

### 7.1 监控统计术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **能力指标** | CapabilityMetrics | 能力运行的统计指标：调用量、响应时间、资源使用 | 运维监控 | 保留 |
| **执行日志** | ExecutionLog | 能力执行的日志记录 | 运维监控 | 保留 |
| **追踪标识** | trace_id | 分布式追踪的唯一标识 | 南向协议 | 保留 |
| **告警规则** | AlertRule | 监控告警的触发条件和动作 | 运维监控 | 保留 |
| **性能指标** | PerformanceMetrics | 系统性能指标：故障检测时间、切换时间、延迟等 | 南向协议 | 保留 |

### 7.2 安全审计术语

| 术语 | 英文标识 | 定义 | 来源 | 新旧 |
|------|----------|------|------|------|
| **审计日志** | AuditLog | 记录能力操作的审计信息 | 安全合规 | 保留 |
| **审计操作类型** | AuditOperationType | 审计记录的操作类型：注册/绑定/调用等 | 安全合规 | 保留 |
| **安全策略** | SecurityPolicy | 能力访问的安全规则配置 | 安全合规 | 保留 |
| **数字签名** | signature | 消息的数字签名，用于验证消息完整性 | 南向协议 | 保留 |
| **会话令牌** | token | 会话的访问令牌 | 南向协议 | 保留 |
| **身份认证** | Authentication | Agent身份验证过程 | 南向协议 | 保留 |
| **访问控制** | AccessControl | 基于角色或策略的资源访问控制 | 南向协议 | 保留 |

---

## 八、新旧术语对照表

### 8.1 核心术语变更

| 旧术语 | 新术语 | 变化说明 |
|--------|--------|----------|
| SceneDefinition | SceneCapability | 场景定义变为场景能力 |
| SceneTemplate | SceneCapability | 场景模板变为场景能力 |
| primaryScene | mainFirst | 主场景变为自驱入口 |
| collaborativeScenes | collaborativeCapabilities | 协作场景变为协作能力 |
| WorkflowDefinition | capabilityChains | 工作流变为能力调用链 |
| Trigger | DriverCapability | 触发器变为驱动能力 |
| 涌现能力 | 场景能力涌现 | 涌现能力是场景能力协调子能力产生的 |
| 场景创建 | 能力自启 | 创建场景变为能力自启动 |
| 场景激活 | 能力自驱 | 激活场景变为能力自驱运行 |

### 8.2 废弃术语

| 废弃术语 | 替代术语 | 废弃原因 |
|----------|----------|----------|
| SceneTemplate | SceneCapability | "模板"过于泛化，统一为能力概念 |
| primaryScene | mainFirst | 强调主动驱动而非被动绑定 |
| 实例 | SceneGroup或具体实体名 | "实例"过于泛化，应使用具体实体名 |

---

## 九、术语使用指南

### 9.1 文档使用指南

1. **精确性**：术语应精确描述概念，避免使用"模板"、"实例"等泛化词汇
2. **一致性**：同一概念在不同文档中使用相同术语
3. **可追溯**：术语定义应来源于用户故事或实际业务场景
4. **层次化**：术语按功能域分类，便于查找和理解

### 9.2 代码使用指南

1. **命名规范**：类名、接口名使用英文标识的驼峰形式
2. **注释规范**：代码注释使用中文术语，括号标注英文标识
3. **配置规范**：YAML/JSON配置使用英文标识的下划线或连字符形式

---

## 十、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-03-01 | 初始版本，整合场景和能力文档术语 |
| 2.0 | 2026-03-02 | 整合南向协议术语，新增能力驱动术语，细化术语分类体系 |

---

**文档维护者**: ooder开发团队  
**最后更新**: 2026-03-02
