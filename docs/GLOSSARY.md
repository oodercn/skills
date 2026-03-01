# Ooder 核心术语表

> **文档版本**: 1.0  
> **创建日期**: 2026-03-01  
> **适用范围**: skill-scene模块、agent-sdk模块、所有需求规格文档

---

## 一、术语命名原则

1. **精确性**：术语应精确描述概念，避免使用"模板"、"实例"等泛化词汇
2. **一致性**：同一概念在不同文档中使用相同术语
3. **可追溯**：术语定义应来源于用户故事或实际业务场景
4. **层次化**：术语按功能域分类，便于查找和理解

---

## 二、场景域术语

### 2.1 场景定义与运行

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **场景定义** | SceneDefinition | 场景的静态描述，包含能力需求、角色定义、工作流配置等元数据 | 用户故事：领导创建日志汇报场景 |
| **场景组** | SceneGroup | 场景的运行时实体，绑定具体参与者和能力实例 | 用户故事：员工加入日志汇报场景 |
| **场景类型** | SceneType | 场景的分类，如PRIMARY（主场景）、SUB（子场景）、CROSS_ORG（跨组织场景） | 业务抽象 |
| **场景状态** | SceneGroupStatus | 场景组的生命周期状态，如CREATED、ACTIVE、PAUSED、DESTROYED | 生命周期管理 |

### 2.2 参与者模型

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **参与者** | Participant | 场景中的活动主体，可以是用户、Agent或SuperAgent | 用户故事：领导、员工、LLM都是参与者 |
| **用户参与者** | UserParticipant | 人类用户作为场景参与者 | 用户故事：领导创建场景、员工提交日志 |
| **Agent参与者** | AgentParticipant | 单一Agent作为场景参与者，负责执行具体能力 | 用户故事：邮件发送Agent |
| **SuperAgent参与者** | SuperAgentParticipant | SuperAgent作为场景参与者，协调多个子Agent完成复杂任务 | 用户故事：LLM协调日志汇总和分析 |
| **角色** | Role | 参与者在场景中的职责定义，决定其可访问的能力 | 用户故事：领导角色可汇总分析，员工角色可提交日志 |
| **参与者状态** | ParticipantStatus | 参与者在场景中的状态，如JOINED、ACTIVE、SUSPENDED、LEFT | 生命周期管理 |

### 2.3 工作流

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **工作流定义** | WorkflowDefinition | 场景中任务执行的流程编排定义 | 用户故事：日志提醒→提交→汇总→分析 |
| **工作流实例** | WorkflowInstance | 工作流的运行时实例，记录执行状态和结果 | 运行时管理 |
| **工作流步骤** | WorkflowStep | 工作流中的单个执行单元 | 业务抽象 |
| **触发器** | Trigger | 启动工作流执行的条件或事件 | 用户故事：定时触发日志提醒 |

---

## 三、能力域术语

### 3.1 能力定义

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **能力** | Capability | 系统中可被调用的功能单元，描述"能做什么" | 用户故事：日志提醒、邮件发送都是能力 |
| **能力标识** | capabilityId | 能力的全局唯一标识，跨场景使用 | 系统设计 |
| **场景能力标识** | capId | 能力在特定场景内的短标识，用于场景内引用 | 系统设计 |
| **能力需求标识** | capDefId | 场景定义中描述的能力需求标识，用于匹配实际能力 | 用户故事：场景定义需要"日志提醒"能力 |
| **能力类型** | CapabilityType | 能力的功能分类，如DRIVER、SERVICE、AI、SKILL等 | 业务抽象 |
| **能力提供者类型** | CapabilityProviderType | 能力的来源类型，如SKILL、AGENT、SUPER_AGENT、DEVICE等 | 用户故事：能力可来自技能包、Agent或设备 |

### 3.2 能力实例化

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **能力绑定** | CapabilityBinding | 能力与场景组的关联关系，包含执行组件信息 | 用户故事：将邮件能力添加到日志汇报场景 |
| **能力绑定状态** | CapabilityBindingStatus | 能力绑定的状态，如PENDING、BINDING、ACTIVE、INACTIVE、ERROR | 生命周期管理 |
| **CAP地址** | CapAddress | 能力在场景内的路由地址，用于权限控制和路由 | 系统设计 |
| **CAP地址区域** | AddressZone | CAP地址的权限区域，分为系统区、通用区、扩展区 | 权限控制设计 |

### 3.3 能力发现

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **发现方法** | DiscoveryMethod | 能力发现的渠道类型，如LOCAL_FS、UDP_BROADCAST、SKILL_CENTER等 | 用户故事：能力雷达扫描 |
| **技能发现器** | SkillDiscoverer | 能力发现器的接口定义，支持多种发现渠道 | 系统设计 |
| **技能包** | SkillPackage | 包含能力定义、依赖和配置的完整发布包 | 用户故事：从能力市场下载安装 |
| **发现过滤器** | DiscoveryFilter | 用于筛选能力发现结果的过滤条件 | 用户故事：按场景类型筛选能力 |

---

## 四、Agent与通信术语

### 4.1 Agent模型

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **Agent** | 能力执行者，负责具体业务逻辑的执行 | 系统设计 |
| **场景Agent** | SceneAgent | 在场景上下文中运行的Agent，继承Agent接口 | 系统设计 |
| **Agent状态** | AgentStatus | Agent的生命周期状态，如CREATED、INITIALIZING、RUNNING、STOPPED | 生命周期管理 |
| **SuperAgent** | 超级Agent，协调多个子Agent完成复杂任务 | 用户故事：LLM协调日志汇总和分析 |
| **涌现能力** | EmergentCapability | SuperAgent协调多个能力组合产生的新能力 | 用户故事：LLM动态创建日志汇报能力链 |

### 4.2 通信链路

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **链路** | Link | Agent间的通信链路，负责连接和数据传输 | 系统设计 |
| **链路类型** | LinkType | 链路的通信方式，如DIRECT、RELAY、TUNNEL、MULTICAST、P2P | 系统设计 |
| **链路状态** | LinkStatus | 链路的运行状态，如ACTIVE、INACTIVE、DEGRADED、FAILED | 运行时管理 |
| **连接类型** | ConnectorType | 能力的连接协议，如HTTP、GRPC、WEBSOCKET、LOCAL_JAR | 系统设计 |

---

## 五、版本与依赖术语

### 5.1 版本管理

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **版本兼容性** | VersionCompatibility | 版本匹配策略，如EXACT、COMPATIBLE、RANGE、LATEST | 系统设计 |
| **版本范围** | VersionRange | 定义最小和最大版本边界的范围对象 | 依赖管理 |

### 5.2 依赖管理

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **能力依赖** | CapabilityDependency | 能力对其他能力的依赖声明 | 用户故事：日志汇报能力依赖邮件发送能力 |
| **依赖解析器** | DependencyResolver | 解析能力依赖关系的组件 | 系统设计 |
| **依赖图** | DependencyGraph | 表示能力间依赖关系的有向图 | 系统设计 |
| **依赖状态** | DependencyStatus | 依赖的满足情况，如satisfied、missing、incompatible | 运行时管理 |

---

## 六、监控与安全术语

### 6.1 监控统计

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **能力指标** | CapabilityMetrics | 能力运行的统计指标，如调用量、响应时间、资源使用 | 运维监控 |
| **健康状态** | HealthStatus | 能力或Agent的健康程度 | 运维监控 |
| **告警规则** | AlertRule | 监控告警的触发条件和动作 | 运维监控 |

### 6.2 安全审计

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **审计日志** | AuditLog | 记录能力操作的审计信息 | 安全合规 |
| **审计操作类型** | AuditOperationType | 审计记录的操作类型，如注册、绑定、调用等 | 安全合规 |
| **安全策略** | SecurityPolicy | 能力访问的安全规则配置 | 安全合规 |

---

## 七、服务接口术语

| 术语 | 英文标识 | 定义 | 来源 |
|------|----------|------|------|
| **场景定义服务** | SceneDefinitionService | 场景定义的CRUD操作服务 | 系统设计 |
| **场景组服务** | SceneGroupService | 场景组的生命周期管理服务 | 系统设计 |
| **参与者服务** | ParticipantService | 场景参与者的管理服务 | 系统设计 |
| **能力服务** | CapabilityService | 能力的CRUD操作服务 | 系统设计 |
| **能力绑定服务** | CapabilityBindingService | 能力与场景绑定关系的管理服务 | 系统设计 |
| **能力路由器** | CapabilityRouter | 能力调用的路由和转发组件 | 系统设计 |
| **链路服务** | LinkService | 通信链路的管理服务 | 系统设计 |
| **发现服务** | DiscoveryService | 统一管理多种发现渠道的服务 | 系统设计 |

---

## 八、术语对照表

### 8.1 废弃术语映射

| 废弃术语 | 替代术语 | 原因 |
|----------|----------|------|
| SceneTemplate（场景模板） | SceneDefinition（场景定义） | "模板"过于泛化，"定义"更精确 |
| 实例 | SceneGroup（场景组）或具体实体名 | "实例"过于泛化，应使用具体实体名 |

### 8.2 跨文档术语统一

| 场景文档术语 | 能力文档术语 | 统一术语 |
|--------------|--------------|----------|
| SceneTemplate | - | SceneDefinition |
| SceneGroup | SceneGroup | SceneGroup |
| Participant | - | Participant |
| Capability | Capability | Capability |
| Skill | SkillPackage | SkillPackage |
| Role | - | Role |
| Workflow | WorkflowDefinition | WorkflowDefinition |

---

## 九、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-03-01 | 初始版本，整合场景和能力文档术语 |

---

**文档维护者**: ooder开发团队  
**最后更新**: 2026-03-01
