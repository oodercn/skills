# SDK 3.0.0 Agent 接口实现协作需求

## 背景

MVP 项目在升级到 SDK 3.0.0 版本后，发现 Agent 相关接口（`SceneAgent`、`EndAgent`、`WorkerAgent`、`RouteAgent`、`McpAgent`）非常复杂，包含大量方法需要实现。

**问题**：这些接口的实现应该由 SE 团队提供默认实现或抽象基类，而不是由 MVP 项目自行实现。

---

## 当前问题

### 1. 接口复杂度过高

| 接口 | 方法数量 | 说明 |
|------|----------|------|
| `Agent` | 8+ | 基础 Agent 接口 |
| `WorkerAgent` | 20+ | 工作Agent，包含状态管理、任务执行等 |
| `SceneAgent` | 12+ | 场景Agent，包含能力注册、调用等 |
| `EndAgent` | 25+ | 终端Agent，包含技能管理、场景组管理等 |
| `RouteAgent` | ? | 路由Agent |
| `McpAgent` | ? | MCP Agent |

### 2. MVP 项目不应该实现这些接口

**原因**：
1. **职责分离** - Agent 接口是 SDK 核心概念，实现应由 SDK 团队提供
2. **维护成本** - SDK 升级时接口变化会导致 MVP 需要大量修改
3. **一致性** - 所有使用 SDK 的项目应该有统一的 Agent 实现

---

## 协作需求

### 需求 1：提供抽象基类

请 SE 团队提供以下抽象基类：

```java
// 建议的抽象基类
package net.ooder.sdk.api.agent.support;

public abstract class AbstractAgent implements Agent {
    // 提供默认实现
}

public abstract class AbstractWorkerAgent extends AbstractAgent implements WorkerAgent {
    // 提供默认实现
}

public abstract class AbstractSceneAgent extends AbstractAgent implements SceneAgent {
    // 提供默认实现
}

public abstract class AbstractEndAgent extends AbstractAgent implements EndAgent {
    // 提供默认实现
}

public abstract class AbstractRouteAgent extends AbstractAgent implements RouteAgent {
    // 提供默认实现
}

public abstract class AbstractMcpAgent extends AbstractAgent implements McpAgent {
    // 提供默认实现
}
```

### 需求 2：提供默认实现类

或者提供开箱即用的默认实现：

```java
package net.ooder.sdk.api.agent.impl;

public class DefaultWorkerAgent extends AbstractWorkerAgent {
    // 默认实现，MVP 可以直接使用或继承
}

public class DefaultSceneAgent extends AbstractSceneAgent {
    // 默认实现
}

// ... 其他默认实现
```

### 需求 3：提供 AgentFactory 默认实现

```java
package net.ooder.sdk.api.agent.support;

public class DefaultAgentFactory implements AgentFactory {
    // 提供默认的 Agent 创建逻辑
    // MVP 只需要注入使用，不需要自己实现
}
```

---

## MVP 当前临时方案

在 SE 团队提供默认实现之前，MVP 项目已经临时实现了这些接口：

- 文件：`AgentHeartbeatConfig.java`
- 实现类：`AgentAdapter`、`WorkerAgentAdapter`、`SceneAgentAdapter`、`EndAgentAdapter`、`RouteAgentAdapter`、`McpAgentAdapter`

**注意**：这些是临时实现，大部分方法返回空值或默认值，不建议在生产环境使用。

---

## 期望结果

1. **短期**：SE 团队确认是否可以提供抽象基类或默认实现
2. **中期**：SE 团队发布包含默认实现的 SDK 版本
3. **长期**：MVP 项目移除临时实现，使用 SDK 提供的默认实现

---

## 联系方式

- MVP 团队
- 日期: 2026-03-25
- SDK 版本: 3.0.0

---

## 附录：接口方法列表

### WorkerAgent 接口方法

```java
String getWorkerName();
String getDescription();
String getSceneId();
String getSkillId();
List<String> getCapabilities();
WorkerAgentStatus getWorkerStatus();
String getPreferredDevice();
void setPreferredDevice(String deviceId);
CompletableFuture<Object> execute(String capId, Map<String, Object> params);
CompletableFuture<Object> executeAsync(String capId, Map<String, Object> params);
void setIdle();
void setBusy();
void setError(String errorMessage);
boolean isIdle();
boolean isBusy();
boolean hasError();
String getCurrentTaskId();
void setCurrentTaskId(String taskId);
SkillService getSkill();
void setSkill(SkillService skillService);
```

### SceneAgent 接口方法

```java
String getSceneId();
String getDomainId();
CapRegistry getCapRegistry();
SceneContext getContext();
Object invokeCapability(String capId, Map<String, Object> params);
CompletableFuture<Object> invokeCapabilityAsync(String capId, Map<String, Object> params);
Object invokeByAddress(CapAddress address, Map<String, Object> params);
void registerCapability(Capability capability);
void unregisterCapability(String capId);
boolean isRunning();
AgentStatus getAgentStatus();
```

### EndAgent 接口方法

```java
String getRouteAgentId();
CompletableFuture<Void> register(String routeAgentId);
CompletableFuture<Void> deregister();
CompletableFuture<Void> heartbeat();
CompletableFuture<Void> installSkill(SkillPackage skillPackage);
CompletableFuture<Void> uninstallSkill(String skillId);
CompletableFuture<List<String>> listInstalledSkills();
CompletableFuture<Map<String, Object>> invokeSkill(String skillId, Map<String, Object> params);
CompletableFuture<Void> configureSkill(String skillId, Map<String, Object> config);
CompletableFuture<SkillStatus> getSkillStatus(String skillId);
CompletableFuture<Void> startSkill(String skillId);
CompletableFuture<Void> stopSkill(String skillId);
CompletableFuture<Void> joinSceneGroup(String sceneGroupId, SceneGroupKey key);
CompletableFuture<Void> leaveSceneGroup(String sceneGroupId);
CompletableFuture<String> getCurrentRole(String sceneGroupId);
CompletableFuture<Void> promoteToPrimary(String sceneGroupId);
CompletableFuture<Void> demoteToBackup(String sceneGroupId);
CompletableFuture<Void> handleFailover(String sceneGroupId, String failedMemberId);
CompletableFuture<Map<String, Object>> getStatus();
CompletableFuture<Void> updateConfig(Map<String, Object> config);
CompletableFuture<Void> reset();
CompletableFuture<Void> upgrade(String version, String upgradeUrl);
```
