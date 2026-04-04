# OoderAgent(Nexus) Skills 移植补充开发计划

**文档版本**: v1.0  
**创建日期**: 2026-04-04  
**项目路径**: `E:\github\ooder-skills`  
**示例工程路径**: `E:\github\super-Agent\examples`

---

## 一、项目概述

### 1.1 移植目标

将 `super-Agent/examples` 中的三个事例工程（MCP Agent、Route Agent、End Agent）的核心功能移植到 `ooder-skills` 项目中，同时避免重复开发已实现的功能。

### 1.2 移植原则

1. **避免重复开发** - 充分利用已实现的P2P、网络监控、路由器管理等功能
2. **集成优先** - 优先集成到现有SKILLS模块，避免创建新模块
3. **渐进式移植** - 分阶段实施，确保每个阶段可独立验证
4. **文档驱动** - 每个阶段完成后更新文档和测试用例

### 1.3 已实现功能确认

| 功能模块 | 实现状态 | 实现位置 | 说明 |
|---------|---------|---------|------|
| P2P网络服务 | ✅ 完整实现 | `temp/ooder-Nexus/service/impl/P2PServiceImpl.java` | 无需移植 |
| 网络监控管理 | ✅ 完整实现 | `skills/capabilities/monitor/skill-network/` | 无需移植 |
| 路由器管理 | ✅ 完整实现 | `temp/ooder-Nexus/infrastructure/openwrt/service/OpenWrtNetworkService.java` | 无需移植 |
| SDK集成 | ✅ 完整实现 | `mvp/src/main/java/net/ooder/mvp/skill/scene/integration/SceneEngineIntegration.java` | 无需移植 |
| Agent接口定义 | ✅ 已定义 | SDK 3.0.0 | 等待默认实现 |

---

## 二、移植范围和任务分解

### 2.1 第一阶段：AI Bridge Protocol 移植（核心功能）

**优先级**: 🔴 高  
**工期估算**: 10个工作日  
**目标**: 移植并集成AI Bridge Protocol到现有skill-protocol和skill-agent模块

#### 2.1.1 消息模型实现（2天）

**任务清单**:
- [ ] 创建 `AiBridgeMessage` 消息模型
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/model/AiBridgeMessage.java`
  - 参考文件: `E:\github\super-Agent\examples\end-agent\src\main\java\net\ooder\examples\endagent\model\AiBridgeMessage.java`
  
- [ ] 创建 `ErrorInfo` 错误信息模型
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/model/ErrorInfo.java`

- [ ] 创建消息构建器 `AiBridgeMessageBuilder`
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/builder/AiBridgeMessageBuilder.java`

**验收标准**:
- ✅ 消息模型包含所有必需字段
- ✅ 支持JSON序列化和反序列化
- ✅ 单元测试覆盖率 > 80%

#### 2.1.2 命令处理器实现（6天）

**任务清单**:

**Day 1-2: 技能相关命令**
- [ ] 实现 `SkillDiscoverCommandHandler`
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/handler/SkillDiscoverCommandHandler.java`
  - 集成到: `skill-management`
  
- [ ] 实现 `SkillInvokeCommandHandler`
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/handler/SkillInvokeCommandHandler.java`
  - 集成到: `skill-management`
  
- [ ] 实现 `SkillRegisterCommandHandler`
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/handler/SkillRegisterCommandHandler.java`
  - 集成到: `skill-management`

**Day 3: 智能体相关命令**
- [ ] 实现 `AgentRegisterCommandHandler`
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/handler/AgentRegisterCommandHandler.java`
  - 集成到: `skill-agent`
  
- [ ] 实现 `AgentUnregisterCommandHandler`
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/handler/AgentUnregisterCommandHandler.java`
  - 集成到: `skill-agent`

**Day 4: 场景相关命令**
- [ ] 实现 `SceneJoinCommandHandler`
  - 文件路径: `skills/_system/skill-scene/src/main/java/net/ooder/skill/scene/handler/SceneJoinCommandHandler.java`
  - 集成到: `skill-scene`
  
- [ ] 实现 `SceneLeaveCommandHandler`
  - 文件路径: `skills/_system/skill-scene/src/main/java/net/ooder/skill/scene/handler/SceneLeaveCommandHandler.java`
  - 集成到: `skill-scene`
  
- [ ] 实现 `SceneDeclareCommandHandler`
  - 文件路径: `skills/_system/skill-scene/src/main/java/net/ooder/skill/scene/handler/SceneDeclareCommandHandler.java`
  - 集成到: `skill-scene`

**Day 5: Cap和Group相关命令**
- [ ] 实现 `CapDeclareCommandHandler`
  - 文件路径: `skills/_system/skill-capability/src/main/java/net/ooder/skill/capability/handler/CapDeclareCommandHandler.java`
  - 集成到: `skill-capability`
  
- [ ] 实现 `GroupMemberAddCommandHandler`
  - 文件路径: `skills/_system/skill-scene/src/main/java/net/ooder/skill/scene/handler/GroupMemberAddCommandHandler.java`
  - 集成到: `skill-scene`

**Day 6: VFS和批量命令**
- [ ] 实现 `CapVfsSyncCommandHandler`
  - 文件路径: `skills/_drivers/vfs/skill-vfs-base/src/main/java/net/ooder/skill/vfs/handler/CapVfsSyncCommandHandler.java`
  - 集成到: `skill-vfs-base`
  
- [ ] 实现 `BatchExecuteCommandHandler`
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/handler/BatchExecuteCommandHandler.java`
  - 集成到: `skill-protocol`

**验收标准**:
- ✅ 每个命令处理器实现完整的业务逻辑
- ✅ 集成到对应的SKILL模块
- ✅ 单元测试覆盖率 > 70%
- ✅ 集成测试通过

#### 2.1.3 协议路由和分发（2天）

**任务清单**:
- [ ] 实现 `AiBridgeProtocolRouter`
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/router/AiBridgeProtocolRouter.java`
  - 功能: 根据命令类型路由到对应的处理器
  
- [ ] 实现 `AiBridgeProtocolDispatcher`
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/dispatcher/AiBridgeProtocolDispatcher.java`
  - 功能: 消息分发和结果聚合
  
- [ ] 集成到 `skill-protocol` 模块
  - 修改文件: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/config/ProtocolAutoConfiguration.java`

**验收标准**:
- ✅ 协议路由正确分发消息
- ✅ 支持同步和异步处理
- ✅ 错误处理机制完善

---

### 2.2 第二阶段：北上/南下协议实现（架构基础）

**优先级**: 🟡 中  
**工期估算**: 8个工作日  
**目标**: 实现Agent间通信协议，支持MCP-Route-End三层架构

#### 2.2.1 北上协议实现（4天）

**任务清单**:

**Day 1-2: 消息模型和序列化**
- [ ] 创建北上协议消息模型
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/model/north/`
  - 包含: `RegisterRequest`, `HeartbeatRequest`, `SkillInvokeRequest`, `SceneJoinRequest`, `StatusReport`
  
- [ ] 实现消息序列化器
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/serializer/NorthProtocolSerializer.java`

**Day 3-4: 协议处理器**
- [ ] 实现 `NorthProtocolHandler`
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/handler/north/NorthProtocolHandler.java`
  - 功能: 处理从End Agent到Route Agent到MCP Agent的消息
  
- [ ] 集成到网络层
  - 修改文件: `skills/capabilities/monitor/skill-network/src/main/java/net/ooder/skill/network/service/impl/NetworkServiceImpl.java`

**验收标准**:
- ✅ 消息正确序列化和反序列化
- ✅ 支持UDP和TCP传输
- ✅ 消息路由正确

#### 2.2.2 南下协议实现（4天）

**任务清单**:

**Day 1-2: 消息模型和序列化**
- [ ] 创建南下协议消息模型
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/model/south/`
  - 包含: `RegisterResponse`, `HeartbeatResponse`, `Command`, `SceneCreate`, `ConfigUpdate`
  
- [ ] 实现消息序列化器
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/serializer/SouthProtocolSerializer.java`

**Day 3-4: 协议处理器**
- [ ] 实现 `SouthProtocolHandler`
  - 文件路径: `skills/_system/skill-protocol/src/main/java/net/ooder/skill/protocol/handler/south/SouthProtocolHandler.java`
  - 功能: 处理从MCP Agent到Route Agent到End Agent的命令
  
- [ ] 集成到网络层
  - 修改文件: `skills/capabilities/monitor/skill-network/src/main/java/net/ooder/skill/network/service/impl/NetworkServiceImpl.java`

**验收标准**:
- ✅ 命令正确下发和执行
- ✅ 支持命令重试和故障转移
- ✅ 状态同步机制完善

---

### 2.3 第三阶段：MCP Agent架构实现（架构完善）

**优先级**: 🟡 中  
**工期估算**: 12个工作日  
**目标**: 实现三层Agent架构（MCP-Route-End），扩展现有skill-agent模块

#### 2.3.1 MCP Agent Manager实现（6天）

**任务清单**:

**Day 1-2: 核心管理器**
- [ ] 实现 `McpAgentManager`
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/mcp/McpAgentManager.java`
  - 参考文件: `E:\github\super-Agent\examples\mcp-agent\src\main\java\net\ooder\examples\mcpagent\manager\McpAgentManager.java`
  - 功能: 节点发现、注册管理、心跳检测
  
- [ ] 实现 `McpAgentConfig`
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/mcp/config/McpAgentConfig.java`

**Day 3-4: 节点管理**
- [ ] 实现 `NodeRegistry`
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/mcp/registry/NodeRegistry.java`
  - 功能: 维护Route Agent和End Agent注册信息
  
- [ ] 实现 `HeartbeatManager`
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/mcp/heartbeat/HeartbeatManager.java`
  - 功能: 心跳检测和节点状态管理

**Day 5-6: 命令路由**
- [ ] 实现 `CommandRouter`
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/mcp/router/CommandRouter.java`
  - 功能: 命令路由和转发
  
- [ ] 实现 `RouteTable`
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/mcp/router/RouteTable.java`
  - 功能: 维护路由表

**验收标准**:
- ✅ MCP Agent正确启动和监听
- ✅ 节点注册和心跳机制正常
- ✅ 命令路由正确

#### 2.3.2 Route Agent Service实现（4天）

**任务清单**:

**Day 1-2: 核心服务**
- [ ] 实现 `RouteAgentService`
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/route/RouteAgentService.java`
  - 参考文件: `E:\github\super-Agent\examples\route-agent\src\main\java\net\ooder\examples\routeagent\service\RouteAgentService.java`
  - 功能: 路由管理、连接池管理
  
- [ ] 实现 `RouteAgentConfig`
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/route/config/RouteAgentConfig.java`

**Day 3: 负载均衡**
- [ ] 实现 `LoadBalancer` 接口和实现类
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/route/balancer/`
  - 包含: `RoundRobinLoadBalancer`, `RandomLoadBalancer`, `LeastConnectionLoadBalancer`, `WeightedLoadBalancer`

**Day 4: 场景消息转发**
- [ ] 实现 `SceneMessageForwarder`
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/route/forwarder/SceneMessageForwarder.java`
  - 功能: 场景消息广播和定向发送

**验收标准**:
- ✅ Route Agent正确注册到MCP Agent
- ✅ 负载均衡算法正确
- ✅ 场景消息转发正常

#### 2.3.3 End Agent增强（2天）

**任务清单**:
- [ ] 增强 `EndAgentService`
  - 修改文件: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/service/impl/AgentServiceImpl.java`
  - 功能: 增加场景加入、技能调用、资源管理等功能
  
- [ ] 实现 `EndAgentNetworkClient`
  - 文件路径: `skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/client/EndAgentNetworkClient.java`
  - 功能: 与Route Agent的通信客户端

**验收标准**:
- ✅ End Agent正确注册到Route Agent
- ✅ 技能调用正常
- ✅ 场景加入和离开正常

---

### 2.4 第四阶段：功能增强和优化（完善阶段）

**优先级**: 🟢 低  
**工期估算**: 6个工作日  
**目标**: 增强现有skill-scene和skill-management模块

#### 2.4.1 场景管理增强（3天）

**任务清单**:
- [ ] 增强场景发现功能
  - 修改文件: `skills/_system/skill-scene/src/main/java/net/ooder/skill/scene/service/impl/SceneServiceImpl.java`
  
- [ ] 增强场景生命周期管理
  - 修改文件: `skills/_system/skill-scene/src/main/java/net/ooder/skill/scene/service/impl/SceneServiceImpl.java`
  
- [ ] 增强场景消息处理
  - 修改文件: `skills/_system/skill-scene/src/main/java/net/ooder/skill/scene/service/impl/SceneServiceImpl.java`

**验收标准**:
- ✅ 场景发现和浏览功能完善
- ✅ 场景生命周期管理完善
- ✅ 场景消息处理增强

#### 2.4.2 技能管理增强（3天）

**任务清单**:
- [ ] 增强技能发现和注册
  - 修改文件: `skills/_system/skill-management/src/main/java/net/ooder/skill/management/SkillManager.java`
  
- [ ] 增强技能元数据管理
  - 修改文件: `skills/_system/skill-management/src/main/java/net/ooder/skill/management/SkillManager.java`
  
- [ ] 增强技能调用和执行
  - 修改文件: `skills/_system/skill-management/src/main/java/net/ooder/skill/management/SkillManager.java`

**验收标准**:
- ✅ 技能发现和注册功能完善
- ✅ 技能元数据管理完善
- ✅ 技能调用和执行增强

---

## 三、技术方案和集成策略

### 3.1 AI Bridge Protocol集成方案

#### 3.1.1 集成到skill-protocol

```java
// 在 ProtocolAutoConfiguration 中注册AI Bridge Protocol
@Configuration
public class ProtocolAutoConfiguration {
    
    @Bean
    public AiBridgeProtocolRouter aiBridgeProtocolRouter() {
        return new AiBridgeProtocolRouter();
    }
    
    @Bean
    public AiBridgeProtocolDispatcher aiBridgeProtocolDispatcher() {
        return new AiBridgeProtocolDispatcher();
    }
}
```

#### 3.1.2 集成到skill-agent

```java
// 在 AgentAutoConfiguration 中注册Agent相关命令处理器
@Configuration
public class AgentAutoConfiguration {
    
    @Bean
    public AgentRegisterCommandHandler agentRegisterCommandHandler() {
        return new AgentRegisterCommandHandler();
    }
    
    @Bean
    public AgentUnregisterCommandHandler agentUnregisterCommandHandler() {
        return new AgentUnregisterCommandHandler();
    }
}
```

### 3.2 北上/南下协议集成方案

#### 3.2.1 集成到skill-protocol

```java
// 在 ProtocolAutoConfiguration 中注册北上/南下协议
@Configuration
public class ProtocolAutoConfiguration {
    
    @Bean
    public NorthProtocolHandler northProtocolHandler() {
        return new NorthProtocolHandler();
    }
    
    @Bean
    public SouthProtocolHandler southProtocolHandler() {
        return new SouthProtocolHandler();
    }
}
```

#### 3.2.2 集成到skill-network

```java
// 在 NetworkServiceImpl 中增加协议处理
@Service
public class NetworkServiceImpl implements NetworkService {
    
    @Autowired
    private NorthProtocolHandler northProtocolHandler;
    
    @Autowired
    private SouthProtocolHandler southProtocolHandler;
    
    // 增加协议处理方法
}
```

### 3.3 MCP Agent架构集成方案

#### 3.3.1 扩展skill-agent模块

```
skills/_system/skill-agent/
├── src/main/java/net/ooder/skill/agent/
│   ├── mcp/                          # 新增MCP Agent
│   │   ├── McpAgentManager.java
│   │   ├── config/
│   │   ├── registry/
│   │   ├── heartbeat/
│   │   └── router/
│   ├── route/                        # 新增Route Agent
│   │   ├── RouteAgentService.java
│   │   ├── config/
│   │   ├── balancer/
│   │   └── forwarder/
│   ├── client/                       # 新增End Agent客户端
│   │   └── EndAgentNetworkClient.java
│   └── service/impl/
│       └── AgentServiceImpl.java     # 增强现有实现
```

---

## 四、风险评估和应对措施

### 4.1 技术风险

| 风险项 | 风险等级 | 应对措施 |
|-------|---------|---------|
| SDK版本不兼容 | 🟡 中 | 优先使用SDK 3.0.0接口，必要时请求SDK团队支持 |
| 协议冲突 | 🟡 中 | 充分测试协议兼容性，建立协议版本管理机制 |
| 性能问题 | 🟢 低 | 使用性能测试工具验证，必要时优化算法 |
| 数据迁移 | 🟢 低 | 设计数据迁移脚本，确保数据完整性 |

### 4.2 项目风险

| 风险项 | 风险等级 | 应对措施 |
|-------|---------|---------|
| 工期延误 | 🟡 中 | 预留20%缓冲时间，优先完成核心功能 |
| 人员变动 | 🟢 低 | 完善文档，确保知识传承 |
| 需求变更 | 🟡 中 | 建立变更管理流程，评估影响范围 |

---

## 五、里程碑和验收标准

### 5.1 里程碑计划

| 里程碑 | 完成时间 | 交付物 | 验收标准 |
|-------|---------|--------|---------|
| M1: AI Bridge Protocol | Day 10 | 消息模型、命令处理器、协议路由 | 单元测试覆盖率>70%，集成测试通过 |
| M2: 北上/南下协议 | Day 18 | 协议消息模型、协议处理器 | 消息正确传输，路由正确 |
| M3: MCP Agent架构 | Day 30 | MCP/Route/End Agent实现 | Agent正确注册和通信 |
| M4: 功能增强 | Day 36 | 场景和技能管理增强 | 功能完善，测试通过 |

### 5.2 总体验收标准

- ✅ 所有单元测试通过，覆盖率>70%
- ✅ 所有集成测试通过
- ✅ 性能测试达标（响应时间<100ms，吞吐量>1000 TPS）
- ✅ 文档完善（API文档、架构文档、部署文档）
- ✅ 代码审查通过
- ✅ 安全审计通过

---

## 六、资源需求

### 6.1 人力资源

| 角色 | 人数 | 工作内容 |
|-----|------|---------|
| 后端开发工程师 | 2 | AI Bridge Protocol、北上/南下协议实现 |
| 架构师 | 1 | MCP Agent架构设计和技术决策 |
| 测试工程师 | 1 | 单元测试、集成测试、性能测试 |
| 文档工程师 | 1 | API文档、架构文档编写 |

### 6.2 环境需求

- 开发环境：JDK 17+, Maven 3.8+, Spring Boot 3.2.5
- 测试环境：Docker, Kubernetes (可选)
- 工具：IDEA, Postman, JMeter

---

## 七、后续维护计划

### 7.1 文档维护

- [ ] 更新API文档
- [ ] 更新架构文档
- [ ] 更新部署文档
- [ ] 更新用户手册

### 7.2 测试维护

- [ ] 定期运行单元测试
- [ ] 定期运行集成测试
- [ ] 定期运行性能测试
- [ ] 定期进行安全审计

### 7.3 版本管理

- [ ] 建立版本分支策略
- [ ] 建立发布流程
- [ ] 建立回滚机制

---

## 八、附录

### 8.1 参考文档

- [AI Bridge Protocol规范](E:\github\super-Agent\examples\end-agent\NEXUS_ARCHITECTURE.md)
- [MCP Agent架构说明](E:\github\super-Agent\examples\mcp-agent\NEXUS_ARCHITECTURE.md)
- [Route Agent架构说明](E:\github\super-Agent\examples\route-agent\NEXUS_ARCHITECTURE.md)
- [SDK 3.0.0接口文档](E:\github\ooder-skills\mvp\docs\SDK_3.0.0_AGENT_IMPLEMENTATION_REQUEST.md)

### 8.2 相关代码位置

**已实现功能**:
- P2P服务: `E:\github\ooder-skills\temp\ooder-Nexus\src\main\java\net\ooder\nexus\service\impl\P2PServiceImpl.java`
- 网络监控: `E:\github\ooder-skills\skills\capabilities\monitor\skill-network\`
- 路由器管理: `E:\github\ooder-skills\temp\ooder-Nexus\src\main\java\net\ooder\nexus\infrastructure\openwrt\service\OpenWrtNetworkService.java`
- SDK集成: `E:\github\ooder-skills\mvp\src\main\java\net\ooder\mvp\skill\scene\integration\SceneEngineIntegration.java`

**待移植功能**:
- AI Bridge Protocol: `E:\github\super-Agent\examples\end-agent\src\main\java\net\ooder\examples\endagent\service\AiBridgeProtocolService.java`
- MCP Agent Manager: `E:\github\super-Agent\examples\mcp-agent\src\main\java\net\ooder\examples\mcpagent\manager\McpAgentManager.java`
- Route Agent Service: `E:\github\super-Agent\examples\route-agent\src\main\java\net\ooder\examples\routeagent\service\RouteAgentService.java`

---

**文档维护**: 本文档应在每个阶段完成后更新，记录实际进度和遇到的问题。

**变更记录**:
- 2026-04-04: 初始版本创建
