# Engine与SDK协作规范文档

> **文档版本**: 1.0.0  
> **编写日期**: 2026-03-09  
> **适用范围**: skill-scene场景技能安装激活系统  
> **协作方**: Engine Team, SDK Team, Skills Team

---

## 一、协作概述

### 1.1 协作目标

为支持场景技能的LLM引导安装与激活，明确Engine、SDK与Skills之间的协作边界和接口需求。

### 1.2 协作架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         场景技能安装激活协作架构                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                        Skills Layer (skill-scene)                    │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐      │    │
│  │  │  Installation   │  │   Activation    │  │  Configuration  │      │    │
│  │  │     Service     │  │     Service     │  │    Service      │      │    │
│  │  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘      │    │
│  └───────────┼────────────────────┼────────────────────┼────────────────┘    │
│              │                    │                    │                     │
│              └────────────────────┼────────────────────┘                     │
│                                   │                                          │
│                                   ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                        Engine Layer (scene-engine)                   │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐      │    │
│  │  │ SceneCapability │  │  Capability     │  │   LLM Context   │      │    │
│  │  │    Manager      │  │    Invoker      │  │    Manager      │      │    │
│  │  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘      │    │
│  └───────────┼────────────────────┼────────────────────┼────────────────┘    │
│              │                    │                    │                     │
│              └────────────────────┼────────────────────┘                     │
│                                   │                                          │
│                                   ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                          SDK Layer                                   │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐      │    │
│  │  │    LLM SDK      │  │   Agent SDK     │  │  Context SDK    │      │    │
│  │  │  (结构化输出)    │  │  (命令通信)      │  │  (上下文管理)    │      │    │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘      │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、Engine协作需求

### 2.1 现有能力评估

| 组件 | 当前版本 | 满足度 | 评估结论 |
|------|---------|--------|---------|
| SceneCapability Manager | 1.0.0 | 60% | 需扩展支持安装流程 |
| Capability Invoker | 1.0.0 | 70% | 需支持LLM工具调用 |
| Provider Interface | 2.3 | 80% | 基本满足，需新增接口 |
| Health Check Provider | 0.9.0 | 100% | 满足技能健康检查 |
| Network Config Provider | 0.8.0 | 100% | 满足网络配置需求 |

### 2.2 需要Engine扩展的能力

#### ENGINE-EXT-001: 场景技能生命周期管理扩展

**需求描述**: Engine需要支持场景技能的安装-激活完整生命周期管理

**需要新增的接口**:

```java
// scene-engine/src/main/java/net/ooder/scene/capability/SceneSkillLifecycleManager.java

public interface SceneSkillLifecycleManager {
    
    /**
     * 安装场景技能
     */
    Result<InstallationResult> installSkill(String skillId, InstallationConfig config);
    
    /**
     * 激活场景技能
     */
    Result<ActivationResult> activateSkill(String skillId, ActivationConfig config);
    
    /**
     * 获取技能安装状态
     */
    Result<InstallationStatus> getInstallationStatus(String skillId);
    
    /**
     * 执行安装步骤
     */
    Result<StepResult> executeInstallationStep(String skillId, String stepId, Map<String, Object> params);
    
    /**
     * 验证技能配置
     */
    Result<ValidationResult> validateSkillConfig(String skillId, Map<String, Object> config);
}
```

**优先级**: P0  
**依赖**: scene-engine 1.1.0+

---

#### ENGINE-EXT-002: LLM上下文隔离管理

**需求描述**: 安装过程中需要特殊的LLM上下文隔离，避免影响用户正常对话

**需要新增的接口**:

```java
// scene-engine/src/main/java/net/ooder/scene/llm/LlmContextManager.java

public interface LlmContextManager {
    
    /**
     * 创建隔离的LLM上下文（用于安装流程）
     */
    Result<LlmContext> createIsolatedContext(String contextType, String referenceId);
    
    /**
     * 在隔离上下文中执行LLM调用
     */
    Result<LlmResponse> callInIsolatedContext(String contextId, LlmRequest request);
    
    /**
     * 销毁隔离上下文
     */
    Result<Void> destroyIsolatedContext(String contextId);
    
    /**
     * 获取上下文状态
     */
    Result<ContextStatus> getContextStatus(String contextId);
}
```

**上下文类型定义**:
| 类型 | 说明 | 生命周期 |
|------|------|---------|
| INSTALLATION | 技能安装上下文 | 安装流程期间 |
| ACTIVATION | 技能激活上下文 | 激活流程期间 |
| CONFIGURATION | 配置向导上下文 | 配置流程期间 |

**优先级**: P0  
**依赖**: scene-engine 1.1.0+

---

#### ENGINE-EXT-003: 结构化输出支持

**需求描述**: Engine需要支持强制LLM输出符合JSON Schema

**需要新增的接口**:

```java
// scene-engine/src/main/java/net/ooder/scene/llm/StructuredOutputManager.java

public interface StructuredOutputManager {
    
    /**
     * 注册输出Schema
     */
    Result<Void> registerSchema(String schemaId, JsonSchema schema);
    
    /**
     * 使用Schema约束调用LLM
     */
    Result<StructuredResponse> callWithSchema(String schemaId, LlmRequest request);
    
    /**
     * 验证输出是否符合Schema
     */
    Result<ValidationResult> validateOutput(String schemaId, String output);
    
    /**
     * 获取Schema列表
     */
    Result<List<SchemaInfo>> listSchemas();
}
```

**需要预置的Schema**:
| Schema ID | 用途 | 字段 |
|-----------|------|------|
| installation-step | 安装步骤输出 | step, action, params, nextStep |
| activation-flow | 激活流程输出 | role, action, required, nextRole |
| config-validation | 配置验证输出 | valid, errors, suggestions |
| tool-call | 工具调用输出 | tool, params, reasoning |

**优先级**: P0  
**依赖**: scene-engine 1.1.0+

---

#### ENGINE-EXT-004: 工具调用注册中心

**需求描述**: Engine需要提供工具注册和调用机制，供LLM使用

**需要新增的接口**:

```java
// scene-engine/src/main/java/net/ooder/scene/tool/ToolRegistry.java

public interface ToolRegistry {
    
    /**
     * 注册工具
     */
    Result<Void> registerTool(ToolDefinition tool);
    
    /**
     * 获取工具定义（用于LLM function calling）
     */
    Result<ToolDefinition> getToolDefinition(String toolId);
    
    /**
     * 列出可用工具
     */
    Result<List<ToolDefinition>> listTools(String category);
    
    /**
     * 执行工具调用
     */
    Result<ToolResult> executeTool(String toolId, Map<String, Object> params);
    
    /**
     * 批量执行工具
     */
    Result<List<ToolResult>> executeTools(List<ToolCall> toolCalls);
}
```

**工具分类**:
| 分类 | 说明 | 示例工具 |
|------|------|---------|
| CONFIG | 配置类工具 | readConfig, writeConfig, validateConfig |
| CHECK | 检查类工具 | checkDependency, checkPermission, checkResource |
| QUERY | 查询类工具 | queryUser, queryOrg, querySkill |
| ACTION | 操作类工具 | bindCapability, createMenu, setPermission |

**优先级**: P1  
**依赖**: scene-engine 1.1.0+

---

### 2.3 Engine协作任务清单

| 任务ID | 任务名称 | 优先级 | 预计工时 | 负责方 |
|--------|---------|--------|---------|--------|
| ENGINE-EXT-001 | 场景技能生命周期管理扩展 | P0 | 5天 | Engine Team |
| ENGINE-EXT-002 | LLM上下文隔离管理 | P0 | 4天 | Engine Team |
| ENGINE-EXT-003 | 结构化输出支持 | P0 | 4天 | Engine Team |
| ENGINE-EXT-004 | 工具调用注册中心 | P1 | 3天 | Engine Team |
| ENGINE-EXT-005 | 安装状态持久化 | P1 | 2天 | Engine Team |

**总计**: 18天

---

## 三、SDK协作需求

### 3.1 LLM SDK评估

#### 3.1.1 现有能力

根据 [LLM_REQUIREMENTS_SPECIFICATION.md](e:/github/ooder-skills/docs/LLM_REQUIREMENTS_SPECIFICATION.md) 分析：

| 功能 | 状态 | 满足度 | 说明 |
|------|------|--------|------|
| 基础对话 | ✅ 已实现 | 100% | chat/complete/stream |
| Provider管理 | ✅ 已实现 | 100% | OpenAI/千问/DeepSeek等 |
| 上下文构建 | 🟡 部分实现 | 60% | 需要扩展安装专用上下文 |
| 结构化输出 | ❌ 未实现 | 0% | 需要新增 |
| 工具调用 | ❌ 未实现 | 0% | 需要新增 |
| 向量检索 | 🟡 部分实现 | 30% | SQLite-Vec需完善 |

#### 3.1.2 需要LLM SDK扩展的能力

##### LLM-SDK-EXT-001: 结构化输出强制

**需求描述**: SDK需要支持强制LLM输出符合指定JSON Schema

**需要新增的API**:

```java
// llm-sdk/src/main/java/net/ooder/sdk/llm/StructuredLlmClient.java

public interface StructuredLlmClient {
    
    /**
     * 使用Schema约束调用LLM
     */
    <T> T callWithSchema(LlmRequest request, Class<T> outputClass);
    
    /**
     * 使用Schema约束调用LLM（带重试）
     */
    <T> T callWithSchema(LlmRequest request, Class<T> outputClass, int maxRetries);
    
    /**
     * 流式调用并实时验证Schema
     */
    <T> Stream<T> streamWithSchema(LlmRequest request, Class<T> outputClass);
}
```

**优先级**: P0  
**依赖**: llm-sdk 2.4.0+

---

##### LLM-SDK-EXT-002: 工具调用支持

**需求描述**: SDK需要支持OpenAI格式的Function Calling

**需要新增的API**:

```java
// llm-sdk/src/main/java/net/ooder/sdk/llm/FunctionCallingClient.java

public interface FunctionCallingClient {
    
    /**
     * 注册函数定义
     */
    void registerFunction(FunctionDefinition function);
    
    /**
     * 调用LLM并处理函数调用
     */
    FunctionCallResult callWithFunctions(LlmRequest request, List<String> functionIds);
    
    /**
     * 执行函数调用循环（自动处理多轮调用）
     */
    <T> T executeFunctionLoop(LlmRequest request, Class<T> finalOutputClass);
}
```

**优先级**: P0  
**依赖**: llm-sdk 2.4.0+

---

##### LLM-SDK-EXT-003: 安装专用上下文管理

**需求描述**: SDK需要支持安装流程专用的上下文管理

**需要新增的API**:

```java
// llm-sdk/src/main/java/net/ooder/sdk/llm/context/InstallationContextManager.java

public interface InstallationContextManager {
    
    /**
     * 创建安装上下文
     */
    InstallationContext createContext(String skillId, String userId);
    
    /**
     * 添加上下文消息
     */
    void addMessage(String contextId, Message message);
    
    /**
     * 获取完整上下文
     */
    List<Message> getContext(String contextId);
    
    /**
     * 保存检查点
     */
    void saveCheckpoint(String contextId, String stepId, Map<String, Object> state);
    
    /**
     * 恢复到检查点
     */
    Map<String, Object> restoreCheckpoint(String contextId, String stepId);
    
    /**
     * 导出上下文（用于降级）
     */
    InstallationContext exportContext(String contextId);
}
```

**优先级**: P1  
**依赖**: llm-sdk 2.4.0+

---

### 3.2 Agent SDK评估

#### 3.2.1 现有能力

根据 [SDK-Usage-Guide.md](e:/github/ooder-skills/temp/protocol-release/v0.6.5/sdk/SDK-Usage-Guide.md) 分析：

| 功能 | 状态 | 满足度 | 说明 |
|------|------|--------|------|
| Agent生命周期 | ✅ 已实现 | 100% | 初始化/启动/停止 |
| 命令发送接收 | ✅ 已实现 | 100% | CommandPacket |
| 网络链路管理 | ✅ 已实现 | 100% | NetworkLink |
| 心跳机制 | ✅ 已实现 | 100% | Heartbeat |
| LLM交互管理 | 🟡 Mock实现 | 30% | 需要真实实现 |
| 场景协作命令 | ❌ 未实现 | 0% | 需要新增 |

#### 3.2.2 需要Agent SDK扩展的能力

##### AGENT-SDK-EXT-001: 场景协作命令

**需求描述**: SDK需要支持场景间协作的命令类型

**需要新增的命令类型**:

```java
// agent-sdk/src/main/java/net/ooder/sdk/enums/CommandType.java

public enum CommandType {
    // 现有命令...
    
    // 场景协作命令
    SCENE_INVITE("SCENE_INVITE", "场景邀请"),
    SCENE_JOIN("SCENE_JOIN", "加入场景"),
    SCENE_LEAVE("SCENE_LEAVE", "离开场景"),
    SCENE_INSTALL("SCENE_INSTALL", "安装场景技能"),
    SCENE_ACTIVATE("SCENE_ACTIVATE", "激活场景技能"),
    SCENE_CONFIG("SCENE_CONFIG", "配置场景技能"),
    
    // 安装协作命令
    INSTALL_STEP_START("INSTALL_STEP_START", "开始安装步骤"),
    INSTALL_STEP_COMPLETE("INSTALL_STEP_COMPLETE", "完成安装步骤"),
    INSTALL_STEP_FAIL("INSTALL_STEP_FAIL", "安装步骤失败"),
    INSTALL_ROLLBACK("INSTALL_ROLLBACK", "回滚安装"),
    
    // 激活协作命令
    ACTIVATION_START("ACTIVATION_START", "开始激活"),
    ACTIVATION_ROLE_ACTION("ACTIVATION_ROLE_ACTION", "角色执行动作"),
    ACTIVATION_COMPLETE("ACTIVATION_COMPLETE", "激活完成");
    
    // ...
}
```

**优先级**: P0  
**依赖**: agent-sdk 0.7.0+

---

##### AGENT-SDK-EXT-002: 安装状态同步

**需求描述**: SDK需要支持安装状态的实时同步

**需要新增的API**:

```java
// agent-sdk/src/main/java/net/ooder/sdk/install/InstallationSyncManager.java

public interface InstallationSyncManager {
    
    /**
     * 广播安装状态变更
     */
    void broadcastStatusChange(String skillId, InstallationStatus status);
    
    /**
     * 订阅安装状态
     */
    void subscribeToInstallation(String skillId, StatusChangeCallback callback);
    
    /**
     * 获取远程安装状态
     */
    InstallationStatus getRemoteStatus(String skillId, String targetAgentId);
    
    /**
     * 同步安装进度
     */
    void syncProgress(String skillId, ProgressInfo progress);
}
```

**优先级**: P1  
**依赖**: agent-sdk 0.7.0+

---

##### AGENT-SDK-EXT-003: LLM服务发现

**需求描述**: SDK需要支持LLM服务的自动发现

**需要新增的API**:

```java
// agent-sdk/src/main/java/net/ooder/sdk/llm/LlmServiceDiscovery.java

public interface LlmServiceDiscovery {
    
    /**
     * 发现可用的LLM Provider
     */
    List<LlmProviderInfo> discoverProviders();
    
    /**
     * 获取最佳LLM端点
     */
    LlmEndpoint getBestEndpoint(String providerType);
    
    /**
     * 注册本地LLM服务
     */
    void registerLocalLlmService(String providerId, LlmEndpoint endpoint);
    
    /**
     * 监听LLM服务变更
     */
    void onLlmServiceChange(Consumer<LlmServiceEvent> callback);
}
```

**优先级**: P2  
**依赖**: agent-sdk 0.7.0+

---

### 3.3 SDK协作任务清单

| 任务ID | 任务名称 | 优先级 | 预计工时 | 负责方 |
|--------|---------|--------|---------|--------|
| LLM-SDK-EXT-001 | 结构化输出强制 | P0 | 4天 | SDK Team |
| LLM-SDK-EXT-002 | 工具调用支持 | P0 | 5天 | SDK Team |
| LLM-SDK-EXT-003 | 安装专用上下文管理 | P1 | 3天 | SDK Team |
| AGENT-SDK-EXT-001 | 场景协作命令 | P0 | 3天 | SDK Team |
| AGENT-SDK-EXT-002 | 安装状态同步 | P1 | 2天 | SDK Team |
| AGENT-SDK-EXT-003 | LLM服务发现 | P2 | 2天 | SDK Team |

**总计**: 19天

---

## 四、协作接口定义

### 4.1 Engine-Skills接口

```yaml
# scene-engine/src/main/resources/interface/installation-provider.yaml
apiVersion: engine.ooder.net/v1
kind: InterfaceDefinition

metadata:
  name: installation-provider
  version: 1.0.0
  
spec:
  interfaces:
    - name: SceneSkillLifecycleManager
      methods:
        - name: installSkill
          input:
            skillId: string
            config: InstallationConfig
          output: InstallationResult
          
        - name: activateSkill
          input:
            skillId: string
            config: ActivationConfig
          output: ActivationResult
          
        - name: getInstallationStatus
          input:
            skillId: string
          output: InstallationStatus
          
    - name: LlmContextManager
      methods:
        - name: createIsolatedContext
          input:
            contextType: string
            referenceId: string
          output: LlmContext
          
        - name: callInIsolatedContext
          input:
            contextId: string
            request: LlmRequest
          output: LlmResponse
          
    - name: StructuredOutputManager
      methods:
        - name: registerSchema
          input:
            schemaId: string
            schema: JsonSchema
          output: void
          
        - name: callWithSchema
          input:
            schemaId: string
            request: LlmRequest
          output: StructuredResponse
```

### 4.2 SDK-Skills接口

```yaml
# llm-sdk/src/main/resources/interface/llm-client.yaml
apiVersion: sdk.ooder.net/v1
kind: InterfaceDefinition

metadata:
  name: llm-installation-client
  version: 2.4.0
  
spec:
  interfaces:
    - name: StructuredLlmClient
      methods:
        - name: callWithSchema
          input:
            request: LlmRequest
            outputClass: Class<T>
            maxRetries: int
          output: T
          
    - name: FunctionCallingClient
      methods:
        - name: callWithFunctions
          input:
            request: LlmRequest
            functionIds: List<string>
          output: FunctionCallResult
          
        - name: executeFunctionLoop
          input:
            request: LlmRequest
            finalOutputClass: Class<T>
          output: T
          
    - name: InstallationContextManager
      methods:
        - name: createContext
          input:
            skillId: string
            userId: string
          output: InstallationContext
          
        - name: saveCheckpoint
          input:
            contextId: string
            stepId: string
            state: Map<string, object>
          output: void
```

---

## 五、版本依赖矩阵

### 5.1 版本兼容性

| 组件 | 当前版本 | 目标版本 | 兼容性 |
|------|---------|---------|--------|
| scene-engine | 1.0.0 | 1.1.0 | 向后兼容 |
| llm-sdk | 2.3.0 | 2.4.0 | 向后兼容 |
| agent-sdk | 0.6.5 | 0.7.0 | 向后兼容 |
| skill-scene | 0.7.3 | 1.0.0 | - |

### 5.2 发布计划

| 阶段 | 内容 | 版本 | 日期 |
|------|------|------|------|
| Phase 1 | Engine扩展发布 | scene-engine 1.1.0 | Week 1 |
| Phase 2 | SDK扩展发布 | llm-sdk 2.4.0, agent-sdk 0.7.0 | Week 2 |
| Phase 3 | Skills集成测试 | skill-scene 1.0.0-beta | Week 3 |
| Phase 4 | 正式发布 | 全部 1.0.0 | Week 4 |

---

## 六、协作验收标准

### 6.1 Engine验收标准

- [ ] SceneSkillLifecycleManager接口完整实现
- [ ] LLM上下文隔离正常工作
- [ ] 结构化输出Schema验证通过
- [ ] 工具注册中心可正常注册和调用工具
- [ ] 所有接口单元测试覆盖率≥80%

### 6.2 SDK验收标准

- [ ] StructuredLlmClient可强制输出符合Schema
- [ ] FunctionCallingClient支持多轮工具调用
- [ ] 场景协作命令可正常发送和接收
- [ ] 安装状态同步延迟<100ms
- [ ] 所有接口单元测试覆盖率≥80%

### 6.3 集成验收标准

- [ ] Engine-SDK-Skills端到端流程测试通过
- [ ] LLM引导安装流程完整运行
- [ ] 降级方案（无LLM）正常工作
- [ ] 性能测试：安装流程<30秒
- [ ] 并发测试：10个同时安装无冲突

---

## 七、风险评估

### 7.1 技术风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|---------|
| Engine扩展接口变更 | 中 | 高 | 提前冻结接口设计 |
| SDK版本兼容性问题 | 低 | 中 | 保持向后兼容 |
| LLM结构化输出不稳定 | 中 | 高 | 增加重试和降级机制 |
| 工具调用性能瓶颈 | 低 | 中 | 异步执行+缓存 |

### 7.2 协作风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|---------|
| Engine Team资源不足 | 中 | 高 | 优先级排序，分阶段交付 |
| SDK Team排期冲突 | 中 | 中 | 提前沟通，锁定资源 |
| 接口理解不一致 | 中 | 高 | 文档评审+原型验证 |

---

## 八、附录

### 8.1 相关文档

- [collaboration-specification-v2.md](collaboration-specification-v2.md) - Skills协作规范
- [development-task-allocation.md](development-task-allocation.md) - 开发任务分配
- [llm-guided-installation-guide.md](llm-guided-installation-guide.md) - LLM安装指南

### 8.2 术语表

| 术语 | 说明 |
|------|------|
| Structured Output | LLM输出符合预定义JSON Schema |
| Function Calling | LLM调用外部工具的机制 |
| Isolated Context | 与用户对话隔离的LLM上下文 |
| Tool Registry | 工具注册和管理的中心 |
| Installation Checkpoint | 安装流程中的状态保存点 |

---

**文档状态**: 已完成  
**编写日期**: 2026-03-09  
**协作状态**: 待Engine和SDK Team评审
