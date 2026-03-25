# SDK 3.0.0 接口兼容性问题协作请求

## 背景

MVP 项目在升级到 SDK 3.0.0 版本后，遇到大量接口兼容性编译错误。根据 `AGENT_SDK_V3_COLLABORATION_TASKS.md` 文档，这些变化是 v3.0 重大版本更新的预期行为。

## 核心变化说明

**v3.0 核心架构变化**：
- 技能是唯一核心实体，场景是技能的形态属性
- 分类从"运行时计算"改为"开发时声明"
- 消除模糊地带（无 PENDING/INVALID）

## 当前依赖版本

```xml
<ooder.sdk.version>3.0.0</ooder.sdk.version>
```

## 依赖包列表

| 包名 | 版本 | 说明 |
|------|------|------|
| agent-sdk-core | 3.0.0 | Agent SDK 核心包 |
| llm-sdk | 3.0.0 | LLM SDK |
| skill-common | 3.0.0 | Skill 公共包 |
| scene-engine | 3.0.0 | Scene Engine |
| skills-framework | 3.0.0 | Skills Framework |
| skill-org-base | 3.0.0 | Skill 组织基础包 |
| skill-hotplug-starter | 3.0.0 | Skill 热插拔启动器 |

---

## 新模型核心枚举（v3.0）

### SkillForm（技能形态）

```java
package net.ooder.skills.api;

public enum SkillForm {
    SCENE,      // 场景技能（容器型，可包含子技能）
    STANDALONE  // 独立技能（原子型，单一能力）
}
```

### SceneType（场景类型）

```java
package net.ooder.skills.api;

public enum SceneType {
    AUTO,    // 自主场景（自驱动，类比：源码包）
    TRIGGER, // 触发场景（被动响应，类比：资源文件夹）
    HYBRID   // 混合场景（既可主动也可被动，类比：普通文件夹）
}
```

### SkillCategory（技能分类）

```java
package net.ooder.skills.api;

public enum SkillCategory {
    KNOWLEDGE,  // 知识类
    LLM,        // AI模型类
    TOOL,       // 工具类
    WORKFLOW,   // 流程类
    DATA,       // 数据类
    SERVICE,    // 服务类
    UI,         // 界面类
    OTHER       // 其他
}
```

---

## 字段映射表

| 旧字段 (v2.x) | 新字段 (v3.0) | 说明 |
|---------------|---------------|------|
| `sceneSkill: true` | `form: SCENE` | 明确声明形态 |
| `sceneSkill: false` | `form: STANDALONE` | 明确声明形态 |
| `mainFirst: true` | `sceneType: AUTO` | 自主场景 |
| `mainFirst: false` | `sceneType: TRIGGER` | 触发场景 |
| `category: ABS/ASS` | `sceneType: AUTO` | 合并为自主场景 |
| `category: TBS` | `sceneType: TRIGGER` | 触发场景 |
| `category: NOT_SCENE_SKILL` | `form: STANDALONE` | 独立技能 |
| `type: scene-skill` | `form: SCENE` | 形态声明 |
| `sceneCapabilities` | `capabilities` | 能力列表简化 |

---

## MVP 项目需要适配的问题

### 问题 1: AgentFactory 接口新增方法

**状态**: ✅ 预期变化，需要适配

| 方法签名 | 返回类型 | 说明 |
|----------|----------|------|
| `createEndAgent(SDKConfiguration config)` | `EndAgent` | 创建终端 Agent |
| `createWorkerAgent(String workerId, String sceneGroupId, String skillId)` | `WorkerAgent` | 创建工作 Agent |
| `createSceneAgent(String sceneGroupId, String sceneId)` | `SceneAgent` | 创建场景 Agent |

**适配方案**: 已在 `AgentHeartbeatConfig.java` 中实现

### 问题 2: Agent 接口新增方法

**状态**: ✅ 预期变化，需要适配

| 方法签名 | 返回类型 | 说明 |
|----------|----------|------|
| `getAgentName()` | `String` | 获取 Agent 名称 |
| `getAgentType()` | `AgentType` | 获取 Agent 类型 |
| `getEndpoint()` | `String` | 获取端点地址 |
| `start()` | `void` | 启动 Agent |
| `stop()` | `void` | 停止 Agent |

**适配方案**: 已在 `AgentAdapter` 类中实现

### 问题 3: WorkerAgent 接口新增方法

**状态**: ✅ 预期变化，需要适配

| 方法签名 | 返回类型 | 说明 |
|----------|----------|------|
| `setCurrentTaskId(String taskId)` | `void` | 设置当前任务 ID |
| `getCurrentTaskId()` | `String` | 获取当前任务 ID |
| `getSkill()` | `SkillService` | 获取 Skill 服务 |
| `setSkill(SkillService skillService)` | `void` | 设置 Skill 服务 |

**适配方案**: 已在 `WorkerAgentAdapter` 类中实现

### 问题 4: SceneAgent 接口新增方法

**状态**: ✅ 预期变化，需要适配

| 方法签名 | 返回类型 | 说明 |
|----------|----------|------|
| `getAgentStatus()` | `AgentStatus` | 获取 Agent 状态 |
| `isRunning()` | `boolean` | 是否正在运行 |

**适配方案**: 已在 `SceneAgentAdapter` 类中实现

### 问题 5: JsonStorageService 接口变化

**状态**: ❓ 需要确认

```java
// 当前使用方式
storage.save(SNAPSHOT_KEY, "network-snapshot", snapshot);
storage.get(SNAPSHOT_KEY, "network-snapshot", AgentNetworkSnapshot.class);
```

**问题确认**:
1. `JsonStorageService` 的正确 API 是什么？
2. 是否有新的存储服务替代？

### 问题 6: AgentSessionService 接口缺少方法

**状态**: ✅ 已添加 `updateSession` 方法

---

## 请求事项

1. **确认 JsonStorageService API** - 请确认正确的存储服务 API
2. **提供抽象基类** - 建议提供 `AbstractAgent`、`AbstractWorkerAgent`、`AbstractSceneAgent` 等抽象基类
3. **验证适配方案** - 请确认 MVP 的适配方案是否正确

---

## 参考文档

- `E:\github\ooder-sdk\scene-engine\docs\AGENT_SDK_V3_COLLABORATION_TASKS.md`

---

## 联系方式

- MVP 团队
- 日期: 2026-03-25
- SDK 版本: 3.0.0
