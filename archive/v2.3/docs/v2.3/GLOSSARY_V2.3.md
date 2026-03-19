# Ooder 核心术语表 v2.3

> **文档版本**: v2.3.3  
> **发布日期**: 2026-03-08  
> **适用范围**: skill-scene模块、agent-sdk模块、南向协议、所有需求规格文档  
> **更新说明**: 新增能力治理术语、协作关系规则

---

## 术语简写对照总表

| 简写 | 全称 | 中文 |
|------|------|------|
| **ABS** | Auto Business Scene | 自驱业务场景 |
| **ASS** | Auto System Scene | 自驱系统场景 |
| **TBS** | Trigger Business Scene | 触发业务场景 |
| **SSK** | SceneSkill | 场景技能 |
| **SC** | SceneCapability | 场景特性 |
| **COMP** | CompositeCapability | 组合能力 |
| **AC** | AtomicCapability | 原子能力 |
| **DC** | DriverCapability | 驱动能力 |
| **CLC** | CollaborativeCapability | 协作能力 |
| **SG** | SceneGroup | 场景组 |
| **MF** | mainFirst | 自驱入口 |

---

## 一、场景技能分类术语（核心）

### 1.1 场景技能分类定义

| 术语 | 简写 | 英文标识 | 定义 | 标准组合 |
|------|------|----------|------|---------|
| **自驱业务场景** | **ABS** | Auto Business Scene | 自动驱动的业务场景，具备完整自驱能力和业务语义 | 1✓2✓3✓4✓ |
| **自驱系统场景** | **ASS** | Auto System Scene | 自动驱动的系统场景，具备自驱能力但无业务语义 | 1✓2✓3✓4✗ |
| **触发业务场景** | **TBS** | Trigger Business Scene | 外部触发的业务场景，具备业务语义但需要人工或API触发 | 1✓2✓3✗4✓ |

### 1.2 四项标准

| 标准 | 英文标识 | 技术标识 | 业务含义 |
|------|----------|---------|---------|
| **标准1** | Standard-1 | `metadata.type = scene-skill` | 类型声明 |
| **标准2** | Standard-2 | `spec.sceneCapabilities` 非空 | 场景特性声明 |
| **标准3** | Standard-3 | `mainFirst = true` | 自驱能力 |
| **标准4** | Standard-4 | 完整业务场景语义 | 业务价值 |

### 1.3 业务语义评分标准

| 评分项 | 分值 | 判定条件 |
|--------|------|---------|
| driverConditions 非空 | 3分 | 有驱动条件定义 |
| participants 非空 | 3分 | 有参与者定义 |
| visibility = public | 2分 | 对外可见 |
| 有协作能力 | 1分 | collaborativeCapabilities 非空 |
| 有业务标签 | 1分 | labels.scene.category 存在 |

**分类阈值**：
| 总分 | 分类 |
|------|------|
| ≥ 8分 | ABS（自驱业务场景） |
| 3-7分 | 待定（需人工判定） |
| < 3分 | ASS（自驱系统场景） |

### 1.4 场景技能分类对比

| 维度 | ABS | ASS | TBS |
|------|-----|-----|-----|
| **自驱能力** | ✓ 有 | ✓ 有 | ✗ 无 |
| **业务语义** | ✓ 强 | ✗ 弱 | ✓ 强 |
| **触发方式** | 自动 | 自动 | 人工/API |
| **参与者** | 有定义 | 无 | 有定义 |
| **可见性** | public | internal | public |
| **生命周期** | 完整 | 循环 | 一次性 |

---

## 二、能力域术语

### 2.1 能力定义术语

| 术语 | 简写 | 英文标识 | 定义 |
|------|------|----------|------|
| **能力** | - | Capability | 系统中可被调用的功能单元，描述"能做什么" |
| **能力标识** | capId | capabilityId | 能力的全局唯一标识 |
| **能力绑定** | CB | CapabilityBinding | 能力与场景组的关联关系 |
| **能力绑定状态** | CBS | CapabilityBindingStatus | PENDING/BINDING/ACTIVE/INACTIVE/ERROR |
| **CAP地址** | CA | CapAddress | 能力在场景内的路由地址 |
| **CAP地址区域** | AZ | AddressZone | SYSTEM/GENERAL/EXTENSION |

### 2.2 能力类型术语

| 术语 | 简写 | 英文标识 | 定义 |
|------|------|----------|------|
| **原子能力** | **AC** | ATOMIC_CAPABILITY | 单一功能、不可分解的能力，无子能力 |
| **组合能力** | **COMP** | COMPOSITE_CAPABILITY | 组合多个原子能力的能力，无涌现行为 |
| **场景特性** | **SC** | SCENE_CAPABILITY | 自驱型SuperAgent能力，包含子能力和驱动能力，可涌现新行为 |
| **驱动能力** | **DC** | DRIVER_CAPABILITY | 提供驱动源头的特殊能力类型 |
| **协作能力** | **CLC** | COLLABORATIVE_CAPABILITY | 跨场景协作的能力，通过接口暴露 |
| **服务能力** | SVC | SERVICE_CAPABILITY | 业务服务、API服务类能力 |
| **AI能力** | AI | AI_CAPABILITY | LLM、机器学习类能力 |
| **工具能力** | TOOL | TOOL_CAPABILITY | 工具类能力 |

### 2.3 能力归属术语（v2.3.3新增）

| 术语 | 简写 | 英文标识 | 定义 |
|------|------|----------|------|
| **场景内部能力** | **SIC** | Scene Internal Capability | 定义在场景技能包内的能力，仅场景内可见，生命周期绑定场景 |
| **独立能力** | **IC** | Independent Capability | 可独立部署的能力，支持多场景组，任务独立、数据隔离 |
| **平台能力** | **PC** | Platform Capability | 平台内置能力，全局可见，无需绑定场景组 |

**能力归属对比**：

| 维度 | 场景内部能力(SIC) | 独立能力(IC) | 平台能力(PC) |
|------|------------------|--------------|--------------|
| **定义位置** | skill.yaml的capabilities列表 | 独立skill包 | 平台内置 |
| **可见性** | 仅场景内可见 | supportedSceneTypes定义 | 全局可见 |
| **生命周期** | 绑定场景 | 独立运行 | 平台管理 |
| **数据隔离** | 场景内共享 | 任务间严格隔离 | 全局共享 |
| **多场景组** | 不支持 | 支持(隔离) | 支持 |
| **调用方式** | 场景命令+内部路由 | 加入场景组后调用 | 直接调用 |
| **示例** | 简历收集、面试邀约 | MQTT推送、PDF转换 | 文件存储、认证服务 |

### 2.4 能力驱动术语

| 术语 | 简写 | 英文标识 | 类型 | 定义 |
|------|------|----------|------|------|
| **自驱入口** | **MF** | mainFirst | - | 场景特性的启动入口 |
| **自检** | - | selfCheck | - | 场景特性检查子能力就绪状态的过程 |
| **自启** | - | selfStart | - | 场景特性初始化子能力并绑定的过程 |
| **自驱** | - | selfDrive | - | 场景特性驱动自身运行的过程 |
| **自愈** | - | selfHeal | - | 场景特性故障时自动恢复的过程 |
| **意图接收** | IR | intent-receiver | **DRIVER** | 接收用户意图并触发场景启动 |
| **时间驱动** | SCH | scheduler | **DRIVER** | 监听时间事件并触发能力调用 |
| **事件监听** | EL | event-listener | **DRIVER** | 监听业务事件并触发能力调用 |
| **能力调用** | CI | capability-invoker | **EXECUTOR** | 管理能力调用链的执行器 |
| **协作协调** | CC | collaboration-coordinator | **EXECUTOR** | 协调协作场景的执行器 |
| **能力调用链** | - | capabilityChains | - | 能力的有序调用序列 |

### 2.4 DRIVER vs EXECUTOR 区分

| 类型 | 说明 | 特征 | 示例 |
|------|------|------|------|
| **DRIVER** | 外部触发源 | 主动触发场景运行 | intent-receiver, scheduler, event-listener |
| **EXECUTOR** | 内部执行器 | 被动执行能力调用 | capability-invoker, collaboration-coordinator |

---

## 三、场景域术语

### 3.1 场景技能与场景特性

| 术语 | 简写 | 英文标识 | 定义 |
|------|------|----------|------|
| **场景技能** | **SSK** | SceneSkill | 可安装的技能包，包含场景特性 |
| **场景特性** | **SC** | SceneCapability | 场景技能内的能力定义，自驱型SuperAgent能力 |
| **场景特性定义** | SCD | SceneCapabilityDef | 场景特性的配置定义 |
| **场景特性实例** | SCI | SceneCapabilityInstance | 场景特性的运行实例 |
| **协作能力入口** | - | collaborativeCapabilities | 场景特性暴露的协作接口 |

**关系说明**：
```
场景技能 (SSK)
└── 场景特性 (SC) - 一个技能可包含多个特性
    ├── 日志提醒特性
    └── 日志汇总特性
```

### 3.2 场景组术语

| 术语 | 简写 | 英文标识 | 定义 |
|------|------|----------|------|
| **场景组** | **SG** | SceneGroup | 共享KEY和VFS资源的Agent集合 |
| **场景组标识** | SGID | SceneGroupId | 场景组的全局唯一标识 |
| **场景组密钥** | SGK | SceneGroupKey | 场景组共享的加密密钥 |
| **场景组状态** | SGS | SceneGroupState | 场景组的运行状态 |
| **场景组信息** | SGI | SceneGroupInfo | 场景组的元数据信息 |

### 3.3 参与者术语

| 术语 | 简写 | 英文标识 | 定义 |
|------|------|----------|------|
| **参与者** | - | Participant | 场景中的活动主体 |
| **角色** | - | Role | 参与者在场景中的职责定义 |
| **主导者** | - | LEADER | 场景的主导参与者，拥有最高权限 |
| **协作者** | - | COLLABORATOR | 场景的协作参与者，参与协作流程 |
| **主实例** | - | PRIMARY | 场景组中的主实例 |
| **备实例** | - | BACKUP | 场景组中的备份实例 |

---

## 四、Agent域术语

### 4.1 Agent类型

| 术语 | 简写 | 英文标识 | 定义 |
|------|------|----------|------|
| **MCP代理** | MCP | MCP Agent | 模型上下文协议代理 |
| **路由代理** | RA | Route Agent | 负责消息路由的代理 |
| **端代理** | EA | End Agent | 执行具体任务的代理 |
| **超级代理** | SA | SuperAgent | 具备自驱能力的代理 |

### 4.2 Agent通信

| 术语 | 简写 | 英文标识 | 定义 |
|------|------|----------|------|
| **链路** | - | Link | Agent之间的通信链路 |
| **连接器类型** | CT | ConnectorType | Agent连接器的类型 |
| **消息总线** | MB | MessageBus | Agent间的消息传递通道 |

### 4.3 Agent状态

| 术语 | 简写 | 英文标识 | 定义 |
|------|------|----------|------|
| **代理状态** | AS | AgentStatus | Agent的运行状态 |
| **健康状态** | HS | HealthStatus | Agent的健康检查状态 |

---

## 五、Skill域术语

### 5.1 Skill定义

| 术语 | 简写 | 英文标识 | 定义 |
|------|------|----------|------|
| **技能清单** | - | SkillManifest | 技能的元数据描述 |
| **技能包** | SP | SkillPackage | 技能的完整发布包 |
| **技能定义** | SKD | SkillDefinition | 技能的详细定义 |
| **场景技能** | **SSK** | SceneSkill | 具备场景特性的技能 |

### 5.2 Skill管理

| 术语 | 简写 | 英文标识 | 定义 |
|------|------|----------|------|
| **技能包管理器** | SPM | SkillPackageManager | 管理技能包的生命周期 |
| **技能服务** | SSKS | SkillService | 技能的核心服务 |
| **技能发现器** | SKDR | SkillDiscoverer | 发现可用技能 |

### 5.3 依赖管理

| 术语 | 简写 | 英文标识 | 定义 |
|------|------|----------|------|
| **依赖** | - | Dependency | 技能之间的依赖关系 |
| **依赖解析器** | DR | DependencyResolver | 解析技能依赖 |
| **依赖图** | DG | DependencyGraph | 技能依赖关系的图结构 |

---

## 六、生命周期状态术语

### 6.1 场景生命周期状态

| 状态 | 简写 | 适用分类 | 说明 |
|------|------|---------|------|
| **DRAFT** | D | ABS, TBS | 草稿状态 |
| **PENDING** | P | TBS | 待处理状态（等待触发） |
| **WAITING** | W | TBS | 等待状态（条件不满足） |
| **ACTIVE** | A | ABS, ASS, TBS | 激活状态 |
| **SCHEDULED** | S | ABS, ASS | 已调度状态 |
| **RUNNING** | R | ABS, ASS, TBS | 运行中状态 |
| **PAUSED** | PA | ABS, TBS | 暂停状态 |
| **ERROR** | E | ABS, ASS, TBS | 错误状态 |
| **COMPLETED** | C | ABS, TBS | 完成状态 |
| **ARCHIVED** | AR | ABS, TBS | 归档状态 |

### 6.2 PENDING vs WAITING 区分

| 状态 | 触发条件 | 说明 | 示例 |
|------|---------|------|------|
| **PENDING** | 场景配置完成后 | 等待外部触发（人工/API） | 审批流程已创建，等待提交 |
| **WAITING** | 触发后条件不满足 | 等待特定条件满足 | 审批已提交，等待审批人上线 |

### 6.3 WAITING 子状态

| 子状态 | 说明 |
|--------|------|
| WAITING_APPROVAL | 等待审批 |
| WAITING_CONDITION | 等待条件满足 |
| WAITING_RESOURCE | 等待资源可用 |
| WAITING_SCHEDULE | 等待指定时间 |

### 6.4 能力绑定状态

| 状态 | 简写 | 说明 |
|------|------|------|
| **PENDING** | P | 待绑定 |
| **BINDING** | B | 绑定中 |
| **ACTIVE** | A | 已激活 |
| **INACTIVE** | I | 未激活 |
| **ERROR** | E | 绑定错误 |

---

## 七、驱动能力类型术语

| 驱动类型 | 简写 | 类型 | 适用场景 | 说明 |
|---------|------|------|---------|------|
| **intent-receiver** | IR | DRIVER | ABS | 接收用户意图 |
| **scheduler** | SCH | DRIVER | ABS, ASS | 时间驱动 |
| **event-listener** | EL | DRIVER | ABS | 事件监听 |
| **capability-invoker** | CI | EXECUTOR | ABS, ASS, TBS | 能力调用执行器 |
| **collaboration-coordinator** | CC | EXECUTOR | ABS | 协作协调执行器 |

---

## 八、可见性术语

| 可见性 | 简写 | 适用分类 | 说明 |
|--------|------|---------|------|
| **public** | PUB | ABS, TBS | 对外可见 |
| **internal** | INT | ASS | 仅系统内部可见 |

---

## 十二、缩写索引

### 12.1 按字母排序

| 简写 | 全称 | 中文 |
|------|------|------|
| **ABS** | Auto Business Scene | 自驱业务场景 |
| **AC** | AtomicCapability | 原子能力 |
| **ASS** | Auto System Scene | 自驱系统场景 |
| **AZ** | AddressZone | CAP地址区域 |
| **CA** | CapAddress | CAP地址 |
| **CB** | CapabilityBinding | 能力绑定 |
| **CBS** | CapabilityBindingStatus | 能力绑定状态 |
| **CC** | collaboration-coordinator | 协作协调执行器 |
| **CI** | capability-invoker | 能力调用执行器 |
| **CLC** | CollaborativeCapability | 协作能力 |
| **COMP** | CompositeCapability | 组合能力 |
| **DC** | DriverCapability | 驱动能力 |
| **DG** | DependencyGraph | 依赖图 |
| **DR** | DependencyResolver | 依赖解析器 |
| **EA** | End Agent | 端代理 |
| **EL** | event-listener | 事件监听 |
| **HS** | HealthStatus | 健康状态 |
| **IC** | Independent Capability | 独立能力 |
| **IR** | intent-receiver | 意图接收 |
| **MF** | mainFirst | 自驱入口 |
| **PC** | Platform Capability | 平台能力 |
| **RA** | Route Agent | 路由代理 |
| **SA** | SuperAgent | 超级代理 |
| **SCH** | scheduler | 时间驱动 |
| **SC** | SceneCapability | 场景特性 |
| **SIC** | Scene Internal Capability | 场景内部能力 |
| **SKD** | SkillDefinition | 技能定义 |
| **SKDR** | SkillDiscoverer | 技能发现器 |
| **SG** | SceneGroup | 场景组 |
| **SGID** | SceneGroupId | 场景组标识 |
| **SGI** | SceneGroupInfo | 场景组信息 |
| **SGK** | SceneGroupKey | 场景组密钥 |
| **SGS** | SceneGroupState | 场景组状态 |
| **SP** | SkillPackage | 技能包 |
| **SPM** | SkillPackageManager | 技能包管理器 |
| **SSK** | SceneSkill | 场景技能 |
| **SSKS** | SkillService | 技能服务 |
| **SVC** | SERVICE_CAPABILITY | 服务能力 |
| **TBS** | Trigger Business Scene | 触发业务场景 |
| **TOOL** | TOOL_CAPABILITY | 工具能力 |

### 12.2 已解决的简写冲突

| 原简写 | 新简写 | 全称 | 中文 | 决策日期 |
|--------|--------|------|------|---------|
| CC | **COMP** | CompositeCapability | 组合能力 | 2026-03-05 |
| CC | **CLC** | CollaborativeCapability | 协作能力 | 2026-03-05 |
| SS | **SSK** | SceneSkill | 场景技能 | 2026-03-05 |
| SS | **SSKS** | SkillService | 技能服务 | 2026-03-05 |
| SD | **SKD** | SkillDefinition | 技能定义 | 2026-03-05 |
| SD | **SKDR** | SkillDiscoverer | 技能发现器 | 2026-03-05 |

---

## 十、协作关系规则（v2.3.3新增）

### 10.1 协作关系定义

| 协作类型 | 英文标识 | 定义 |
|---------|---------|------|
| **能力调用** | Capability Invocation | 能力之间的调用关系 |
| **Agent协作** | Agent Collaboration | Agent之间的协作关系 |
| **场景内协作** | In-Scene Collaboration | 同一场景组内的协作 |
| **场景间协作** | Cross-Scene Collaboration | 不同场景组之间的协作 |
| **能力-场景协作** | Capability-Scene Collaboration | 独立能力与场景组的关系 |

### 10.2 协作关系规则表

| 协作类型 | 是否允许 | 约束条件 |
|---------|---------|---------|
| 同场景内能力互相调用 | ✅ 允许 | 通过 CapabilityInvoker，使用 Connector |
| 跨场景能力调用 | ⚠️ 有条件 | 通过 MCP Agent 命令转发 |
| Agent直接调用能力 | ✅ 允许 | 通过场景组绑定 |
| Agent间直接协作 | ✅ 允许 | 需在同一场景组 |
| 场景间直接协作 | ❌ 禁止 | 通过 LLM 命令方式间接协作 |
| 独立能力调用场景内能力 | ✅ 允许 | 加入场景组后有状态调用 |
| 场景内能力调用独立能力 | ✅ 允许 | 绑定后通过 Invoker |
| 独立能力间调用 | ✅ 允许 | 需在同一场景组内 |
| 跨场景数据交互 | ❌ 严格禁止 | 数据层面隔离 |

### 10.3 场景间协作机制

**LLM命令方式**：
```
场景A ──LLM──▶ MCP Agent ──Command──▶ MCP Agent ──LLM──▶ 场景B
```

**支持的命令类型**：
| 命令类型 | 英文标识 | 说明 |
|---------|---------|------|
| 场景邀请 | SCENE_INVITE | 邀请加入场景 |
| 加入场景 | SCENE_JOIN | 加入指定场景 |
| 任务分配 | TASK_ASSIGN | 分配任务给Agent |
| 能力调用 | CAPABILITY_INVOKE | 调用远程能力 |
| 数据同步 | DATA_SYNC | 同步状态数据 |
| 消息发送 | MESSAGE_SEND | 发送消息 |

### 10.4 独立能力多场景组机制

```
独立能力 X
    │
    ├──▶ 场景组A ── 数据隔离 ── 状态A
    │
    ├──▶ 场景组B ── 数据隔离 ── 状态B
    │
    └──▶ 场景组C ── 数据隔离 ── 状态C

对外可见：
 - 状态: 忙/空闲
 - 任务列表: 等待中/执行中

数据层面：
 - 严格禁止跨场景交互
 - 每个场景组独立数据空间
```

### 10.5 supportedSceneTypes 动态更新机制

**混合模式**：
- **静态基础**：部署时在 skill.yaml 中定义
- **动态扩展**：运行时通过 API 添加新场景类型

**权限控制**：
| 角色 | 添加场景类型 | 移除场景类型 | 查看 |
|------|------------|------------|------|
| 能力所有者 | ✅ 申请 | ✅ 申请 | ✅ |
| 场景管理员 | ✅ 审批 | ✅ 审批 | ✅ |
| MCP Agent | ✅ 自动审批 | ⚠️ 需审批 | ✅ |
| 普通用户 | ❌ | ❌ | ✅ |

---

## 十一、中文术语对照

| 英文 | 中文 | 说明 |
|------|------|------|
| SceneSkill | 场景技能 | 可安装的技能包 |
| SceneCapability | 场景特性 | 场景技能内的能力定义 |
| Capability | 能力 | 通用能力概念 |
| Feature | 特性 | 场景特性的简称 |

---

**文档维护**: Ooder 开发团队  
**最后更新**: 2026-03-08  
**决策依据**: CONFLICTS_AND_AMBIGUITIES.md, 能力治理专项讨论
