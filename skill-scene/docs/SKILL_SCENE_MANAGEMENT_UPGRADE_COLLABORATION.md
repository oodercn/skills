# skill-scene-management 场景模块升级协作任务

## 📋 任务概述

**发起方**: skill-scene 团队  
**接收方**: skills 团队 (skill-scene-management 维护者)  
**任务类型**: 场景管理模块功能升级  
**优先级**: 高  
**创建日期**: 2026-03-17

---

## 🎯 升级目标

将 `skill-scene` 中场景管理相关的高级功能模块复制到 `skill-scene-management`，使其成为独立、完整的场景管理技能模块。

---

## 📊 模块对比分析（仅场景相关）

### 1. 后端 Java 模块对比

#### 1.1 Controller 层对比

| 控制器 | skill-scene | skill-scene-management | 升级动作 |
|--------|-------------|------------------------|----------|
| SceneController | ✅ | ✅ | 已存在，需同步差异 |
| SceneGroupController | ✅ | ✅ | 已存在，需同步差异 |
| SceneKnowledgeController | ✅ | ✅ | 已存在，需同步差异 |
| SceneLlmController | ✅ | ✅ | 已存在，需同步差异 |
| SceneTemplateController | ✅ | ✅ | 已存在，需同步差异 |
| SceneSkillLifecycleController | ✅ | ❌ | **需复制** |
| TodoController | ✅ | ✅ | 已存在，需同步差异 |
| HistoryController | ✅ | ✅ | 已存在，需同步差异 |
| KnowledgeBaseController | ✅ | ✅ | 已存在，需同步差异 |

#### 1.2 Service 层对比

| 服务接口 | skill-scene | skill-scene-management | 升级动作 |
|----------|-------------|------------------------|----------|
| SceneService | ✅ | ✅ | 已存在，需同步差异 |
| SceneGroupService | ✅ | ✅ | 已存在，需同步差异 |
| SceneTemplateService | ✅ | ✅ | 已存在，需同步差异 |
| SceneWorkflowService | ✅ | ❌ | **需复制** |
| TodoService | ✅ | ✅ | 已存在，需同步差异 |
| HistoryService | ✅ | ❌ | **需复制** |
| SnapshotService | ✅ | ❌ | **需复制** |
| SceneNotificationService | ✅ | ❌ | **需复制** |

#### 1.3 Service 实现对比

| 服务实现 | skill-scene | skill-scene-management | 升级动作 |
|----------|-------------|------------------------|----------|
| SceneServiceMemoryImpl | ✅ | ❌ | **需复制** |
| SceneGroupServiceMemoryImpl | ✅ | ✅ | 已存在，需同步差异 |
| SceneGroupServiceHybridImpl | ✅ | ❌ | **需复制** |
| SceneTemplateServiceMemoryImpl | ✅ | ❌ | **需复制** |
| HistoryServiceMemoryImpl | ✅ | ❌ | **需复制** |
| TodoServiceMemoryImpl | ✅ | ❌ | **需复制** |

#### 1.4 模块目录对比

| 模块目录 | skill-scene | skill-scene-management | 升级动作 |
|----------|-------------|------------------------|----------|
| controller/ | ✅ 9 个场景控制器 | ✅ 8 个场景控制器 | 需同步差异 |
| service/ | ✅ 8 个服务 | ✅ 2 个服务 | **需复制 6 个服务** |
| dto/scene/ | ✅ 45 个 DTO | ✅ 10 个 DTO | **需复制 35 个 DTO** |
| template/ | ✅ 11 个文件 | ✅ 2 个文件 | **需复制 9 个文件** |
| snapshot/ | ✅ 1 个文件 | ❌ 无 | **需复制** |
| notification/ | ✅ 1 个文件 | ❌ 无 | **需复制** |
| lifecycle/ | ❌ 无 | ✅ 4 个文件 | 保留 |
| workflow/ | ❌ 无 | ✅ 6 个文件 | 保留 |

---

### 2. DTO 层详细对比（场景相关）

#### 2.1 scene/ 目录 DTO 对比

| DTO 文件 | skill-scene | skill-scene-management | 升级动作 |
|----------|-------------|------------------------|----------|
| ActionDefDTO.java | ✅ | ❌ | **需复制** |
| AgentParticipantDTO.java | ✅ | ❌ | **需复制** |
| AgentType.java | ✅ | ❌ | **需复制** |
| AuditLoggingConfigDTO.java | ✅ | ❌ | **需复制** |
| AutomationRuleDTO.java | ✅ | ❌ | **需复制** |
| CapabilityBindingDTO.java | ✅ | ✅ | 已存在 |
| CapabilityBindingStatus.java | ✅ | ❌ | **需复制** |
| CapabilityDefDTO.java | ✅ | ❌ | **需复制** |
| CapabilityProviderType.java | ✅ | ❌ | **需复制** |
| ConflictResolution.java | ✅ | ❌ | **需复制** |
| ConnectorType.java | ✅ | ❌ | **需复制** |
| CoordinationConfigDTO.java | ✅ | ❌ | **需复制** |
| CoordinationRuleDTO.java | ✅ | ❌ | **需复制** |
| CoordinationType.java | ✅ | ❌ | **需复制** |
| CrossOrgRuleDTO.java | ✅ | ❌ | **需复制** |
| DataIsolationRuleDTO.java | ✅ | ❌ | **需复制** |
| DeviceBindingDefDTO.java | ✅ | ❌ | **需复制** |
| ExecutionLogDTO.java | ✅ | ❌ | **需复制** |
| FailoverStatusDTO.java | ✅ | ❌ | **需复制** |
| FallbackConfigDTO.java | ✅ | ❌ | **需复制** |
| KnowledgeBindingDTO.java | ✅ | ✅ | 已存在 |
| ParameterDefDTO.java | ✅ | ❌ | **需复制** |
| ParticipantRole.java | ✅ | ❌ | **需复制** |
| ParticipantStatus.java | ✅ | ❌ | **需复制** |
| ParticipantType.java | ✅ | ❌ | **需复制** |
| ReturnDefDTO.java | ✅ | ❌ | **需复制** |
| RoleDefinitionDTO.java | ✅ | ❌ | **需复制** |
| SceneDTO.java | ✅ | ✅ | 已存在 |
| SceneGroupConfigDTO.java | ✅ | ✅ | 已存在 |
| SceneGroupDTO.java | ✅ | ✅ | 已存在 |
| SceneGroupStatus.java | ✅ | ❌ | **需复制** |
| SceneParticipantDTO.java | ✅ | ✅ | 已存在 |
| SceneSnapshotDTO.java | ✅ | ✅ | 已存在 |
| SceneTemplateDTO.java | ✅ | ❌ | **需复制** |
| SceneType.java | ✅ | ❌ | **需复制** |
| SceneWorkflowInstanceDTO.java | ✅ | ❌ | **需复制** |
| SecurityPolicyDTO.java | ✅ | ❌ | **需复制** |
| StepDefinitionDTO.java | ✅ | ❌ | **需复制** |
| SubAgentRefDTO.java | ✅ | ❌ | **需复制** |
| SuperAgentParticipantDTO.java | ✅ | ❌ | **需复制** |
| TemplateCategory.java | ✅ | ❌ | **需复制** |
| TemplateStatus.java | ✅ | ❌ | **需复制** |
| TriggerConfigDTO.java | ✅ | ❌ | **需复制** |
| TriggerDefDTO.java | ✅ | ❌ | **需复制** |
| TriggerDefinitionDTO.java | ✅ | ❌ | **需复制** |
| UserParticipantDTO.java | ✅ | ❌ | **需复制** |
| WorkflowDefDTO.java | ✅ | ❌ | **需复制** |
| WorkflowDefinitionDTO.java | ✅ | ❌ | **需复制** |
| WorkflowStepDefDTO.java | ✅ | ❌ | **需复制** |

#### 2.2 其他场景相关 DTO

| DTO 文件 | skill-scene | skill-scene-management | 升级动作 |
|----------|-------------|------------------------|----------|
| SceneDefinitionDTO.java | ✅ | ❌ | **需复制** |
| SceneGroupInfoDTO.java | ✅ | ❌ | **需复制** |
| SceneGroupKeyDTO.java | ✅ | ❌ | **需复制** |
| SceneInfoDTO.java | ✅ | ❌ | **需复制** |
| SceneMemberDTO.java | ✅ | ❌ | **需复制** |
| SceneMemberInfoDTO.java | ✅ | ❌ | **需复制** |
| SceneRoleDTO.java | ✅ | ❌ | **需复制** |
| SceneStateDTO.java | ✅ | ❌ | **需复制** |

---

### 3. 前端资源对比

#### 3.1 HTML 页面对比

| 页面 | skill-scene | skill-scene-management | 升级动作 |
|------|-------------|------------------------|----------|
| scene-management.html | ✅ | ✅ | 已存在，需同步差异 |
| scene-group-management.html | ✅ | ✅ | 已存在，需同步差异 |
| scene-group-detail.html | ✅ | ❌ | **需复制** |
| scene-detail.html | ✅ | ❌ | **需复制** |
| scene-capabilities.html | ✅ | ❌ | **需复制** |
| scene-capability-detail.html | ✅ | ❌ | **需复制** |
| scene-knowledge.html | ✅ | ❌ | **需复制** |
| template-management.html | ✅ | ✅ | 已存在，需同步差异 |
| template-detail.html | ✅ | ❌ | **需复制** |
| my-scenes.html | ✅ | ✅ | 已存在，需同步差异 |
| my-todos.html | ✅ | ❌ | **需复制** |
| my-history.html | ✅ | ❌ | **需复制** |
| snapshots.html | ❌ | ✅ | 保留 |
| participants.html | ❌ | ✅ | 保留 |
| knowledge-bindings.html | ❌ | ✅ | 保留 |

#### 3.2 JavaScript 文件对比

| JS 文件 | skill-scene | skill-scene-management | 升级动作 |
|---------|-------------|------------------------|----------|
| scene-management.js | ✅ | ❌ | **需复制** |
| scene-group-management.js | ✅ | ✅ | 已存在，需同步差异 |
| scene-group-detail.js | ✅ | ❌ | **需复制** |
| scene-detail.js | ✅ | ❌ | **需复制** |
| scene-capabilities.js | ✅ | ❌ | **需复制** |
| scene-capability-detail.js | ✅ | ❌ | **需复制** |
| scene-knowledge.js | ✅ | ❌ | **需复制** |
| template-management.js | ✅ | ❌ | **需复制** |
| template-detail.js | ✅ | ❌ | **需复制** |
| my-scenes.js | ✅ | ❌ | **需复制** |
| my-todos.js | ✅ | ❌ | **需复制** |
| my-history.js | ✅ | ❌ | **需复制** |
| snapshots.js | ❌ | ✅ | 保留 |
| participants.js | ❌ | ✅ | 保留 |
| knowledge-bindings.js | ❌ | ✅ | 保留 |

#### 3.3 CSS 文件对比

| CSS 文件 | skill-scene | skill-scene-management | 升级动作 |
|----------|-------------|------------------------|----------|
| scene-knowledge.css | ✅ | ❌ | **需复制** |
| my-scenes.css | ✅ | ✅ | 已存在，需同步差异 |
| scene-group.css | ❌ | ✅ | 保留 |
| scene-pages.css | ❌ | ✅ | 保留 |

---

## 📦 需复制的完整模块清单

### 后端 Java 模块

```
skill-scene-management/src/main/java/net/ooder/skill/scene/
├── controller/
│   └── SceneSkillLifecycleController.java    # 新增
├── service/
│   ├── SceneWorkflowService.java             # 新增
│   ├── HistoryService.java                   # 新增
│   ├── impl/
│   │   ├── SceneServiceMemoryImpl.java       # 新增
│   │   ├── SceneGroupServiceHybridImpl.java  # 新增
│   │   ├── SceneTemplateServiceMemoryImpl.java # 新增
│   │   ├── HistoryServiceMemoryImpl.java     # 新增
│   │   └── TodoServiceMemoryImpl.java        # 新增
├── snapshot/                                  # 新建目录
│   └── SnapshotService.java
├── notification/                              # 新建目录
│   └── SceneNotificationService.java
├── template/                                  # 新增文件
│   ├── ActionConfig.java
│   ├── ActivationStepConfig.java
│   ├── DependenciesConfig.java
│   ├── DependencyConfig.java
│   ├── InstallProgressCallback.java
│   ├── MenuConfig.java
│   ├── PrivateCapabilityConfig.java
│   ├── RoleConfig.java
│   ├── SceneTemplate.java
│   ├── SceneTemplateLoader.java
│   └── UiSkillConfig.java
└── dto/scene/                                 # 新增 35 个 DTO
    ├── ActionDefDTO.java
    ├── AgentParticipantDTO.java
    ├── AgentType.java
    ├── AuditLoggingConfigDTO.java
    ├── AutomationRuleDTO.java
    ├── ... (共 35 个新增文件)
```

### 前端资源

```
skill-scene-management/src/main/resources/static/console/
├── pages/
│   ├── scene-group-detail.html
│   ├── scene-detail.html
│   ├── scene-capabilities.html
│   ├── scene-capability-detail.html
│   ├── scene-knowledge.html
│   ├── template-detail.html
│   ├── my-todos.html
│   └── my-history.html
├── js/pages/
│   ├── scene-management.js
│   ├── scene-group-detail.js
│   ├── scene-detail.js
│   ├── scene-capabilities.js
│   ├── scene-capability-detail.js
│   ├── scene-knowledge.js
│   ├── template-management.js
│   ├── template-detail.js
│   ├── my-scenes.js
│   ├── my-todos.js
│   └── my-history.js
└── css/pages/
    └── scene-knowledge.css
```

---

## 🔧 功能模块说明

### 1. SceneSkillLifecycleController 场景生命周期控制器

**功能描述**: 管理场景的完整生命周期，包括创建、更新、状态转换、终止等

**核心 API**:
- `POST /api/v1/scenes` - 创建场景
- `PUT /api/v1/scenes/{sceneId}` - 更新场景
- `POST /api/v1/scenes/{sceneId}/activate` - 激活场景
- `POST /api/v1/scenes/{sceneId}/deactivate` - 停用场景
- `DELETE /api/v1/scenes/{sceneId}` - 删除场景

---

### 2. SceneWorkflowService 场景工作流服务

**功能描述**: 管理场景内的工作流执行，支持多步骤编排

**核心方法**:
- `startWorkflow(sceneId, workflowDef)` - 启动工作流
- `executeStep(sceneId, stepId)` - 执行步骤
- `getWorkflowStatus(sceneId)` - 获取工作流状态
- `cancelWorkflow(sceneId)` - 取消工作流

---

### 3. SnapshotService 快照服务

**功能描述**: 场景状态快照管理，支持状态保存与恢复

**核心方法**:
- `createSnapshot(sceneId)` - 创建快照
- `restoreSnapshot(sceneId, snapshotId)` - 恢复快照
- `listSnapshots(sceneId)` - 获取快照列表
- `deleteSnapshot(snapshotId)` - 删除快照

---

### 4. SceneNotificationService 场景通知服务

**功能描述**: 场景事件通知，支持多种通知渠道

**核心方法**:
- `notifySceneCreated(sceneId)` - 场景创建通知
- `notifySceneStateChanged(sceneId, oldState, newState)` - 状态变更通知
- `notifyParticipantJoined(sceneId, participant)` - 参与者加入通知

---

### 5. Template 模板模块

**功能描述**: 场景模板配置管理，支持模板加载、激活配置、依赖管理

**核心类**:
- `SceneTemplate` - 场景模板定义
- `SceneTemplateLoader` - 模板加载器
- `ActivationStepConfig` - 激活步骤配置
- `DependenciesConfig` - 依赖配置
- `MenuConfig` - 菜单配置
- `RoleConfig` - 角色配置

---

### 6. HistoryService 历史服务

**功能描述**: 场景操作历史记录与查询

**核心方法**:
- `recordHistory(sceneId, action, details)` - 记录历史
- `getHistory(sceneId, filters)` - 查询历史
- `getHistoryStatistics(sceneId)` - 获取统计数据

---

### 7. TodoService 待办服务

**功能描述**: 场景相关待办事项管理

**核心方法**:
- `createTodo(sceneId, todo)` - 创建待办
- `completeTodo(todoId)` - 完成待办
- `listTodos(userId, filters)` - 查询待办列表

---

## ✅ 任务检查清单

### 阶段一: 后端 Controller 复制

- [ ] 复制 SceneSkillLifecycleController.java
- [ ] 同步 SceneController.java 差异
- [ ] 同步 SceneGroupController.java 差异
- [ ] 同步 SceneKnowledgeController.java 差异
- [ ] 同步 SceneTemplateController.java 差异

### 阶段二: 后端 Service 复制

- [ ] 复制 SceneWorkflowService.java
- [ ] 复制 HistoryService.java
- [ ] 复制 SnapshotService.java
- [ ] 复制 SceneNotificationService.java
- [ ] 复制 SceneServiceMemoryImpl.java
- [ ] 复制 SceneGroupServiceHybridImpl.java
- [ ] 复制 SceneTemplateServiceMemoryImpl.java
- [ ] 复制 HistoryServiceMemoryImpl.java
- [ ] 复制 TodoServiceMemoryImpl.java

### 阶段三: 后端 DTO 复制

- [ ] 复制 dto/scene/ 目录下 35 个新增 DTO
- [ ] 复制顶层场景相关 DTO (8 个)
- [ ] 同步已存在的 DTO 差异

### 阶段四: Template 模块复制

- [ ] 复制 ActionConfig.java
- [ ] 复制 ActivationStepConfig.java
- [ ] 复制 DependenciesConfig.java
- [ ] 复制 DependencyConfig.java
- [ ] 复制 InstallProgressCallback.java
- [ ] 复制 MenuConfig.java
- [ ] 复制 PrivateCapabilityConfig.java
- [ ] 复制 RoleConfig.java
- [ ] 复制 SceneTemplate.java
- [ ] 复制 SceneTemplateLoader.java
- [ ] 复制 UiSkillConfig.java

### 阶段五: 前端页面复制

- [ ] 复制 scene-group-detail.html
- [ ] 复制 scene-detail.html
- [ ] 复制 scene-capabilities.html
- [ ] 复制 scene-capability-detail.html
- [ ] 复制 scene-knowledge.html
- [ ] 复制 template-detail.html
- [ ] 复制 my-todos.html
- [ ] 复制 my-history.html

### 阶段六: 前端 JS 复制

- [ ] 复制 scene-management.js
- [ ] 复制 scene-group-detail.js
- [ ] 复制 scene-detail.js
- [ ] 复制 scene-capabilities.js
- [ ] 复制 scene-capability-detail.js
- [ ] 复制 scene-knowledge.js
- [ ] 复制 template-management.js
- [ ] 复制 template-detail.js
- [ ] 复制 my-scenes.js
- [ ] 复制 my-todos.js
- [ ] 复制 my-history.js

### 阶段七: 前端 CSS 复制

- [ ] 复制 scene-knowledge.css
- [ ] 同步 my-scenes.css 差异

### 阶段八: 集成测试

- [ ] 更新 pom.xml 依赖
- [ ] 更新 skill.yaml 配置
- [ ] 更新 menu-config.json 菜单配置
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 前端页面功能验证

---

## 📊 统计摘要

| 项目 | 需复制数量 |
|------|-----------|
| **Controller** | 1 个新增 + 4 个同步 |
| **Service 接口** | 4 个新增 |
| **Service 实现** | 5 个新增 |
| **DTO 类** | 43 个新增 |
| **Template 类** | 11 个新增 |
| **HTML 页面** | 8 个新增 |
| **JS 文件** | 11 个新增 |
| **CSS 文件** | 1 个新增 |

---

## 📞 联系方式

如有问题请联系 skill-scene 团队。

---

**文档版本**: v1.0  
**最后更新**: 2026-03-17
