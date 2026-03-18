# skill-capability 模块升级协作任务

## 📋 任务概述

**发起方**: skill-scene 团队  
**接收方**: skills 团队 (skill-capability 维护者)  
**任务类型**: 模块功能升级  
**优先级**: 高  
**创建日期**: 2026-03-17

---

## 🎯 升级目标

将 `skill-scene` 中能力相关的高级功能模块复制到 `skill-capability`，使其成为独立、完整的能力管理技能模块。

---

## 📊 模块对比分析

### 1. 后端 Java 模块对比

#### 1.1 模块结构对比

| 模块分类 | skill-scene/capability | skill-capability | 升级动作 |
|---------|------------------------|------------------|----------|
| **activation/** | ✅ 4 个文件 | ❌ 无 | **需复制** |
| **config/** | ✅ 1 个文件 | ❌ 无 | **需复制** |
| **connector/** | ✅ 5 个文件 | ❌ 无 | **需复制** |
| **driver/** | ✅ 1 个文件 | ❌ 无 | **需复制** |
| **event/** | ✅ 2 个文件 | ❌ 无 | **需复制** |
| **fallback/** | ✅ 2 个文件 | ❌ 无 | **需复制** |
| **install/** | ✅ 4 个文件 | ❌ 无 | **需复制** |
| **invoke/** | ✅ 2 个文件 | ❌ 无 | **需复制** |
| **link/** | ❌ 无 | ✅ 4 个文件 | 保留 |
| **discovery/** | ❌ 无 | ✅ 1 个文件 | 保留 |

#### 1.2 Model 层对比

| 文件名 | skill-scene | skill-capability | 升级动作 |
|--------|-------------|------------------|----------|
| AccessLevel.java | ✅ | ✅ | 已存在，需同步差异 |
| BusinessCategory.java | ✅ | ❌ | **需复制** |
| Capability.java | ✅ | ✅ | 已存在，需同步差异 |
| CapabilityAddress.java | ✅ | ❌ | **需复制** |
| CapabilityBinding.java | ✅ | ✅ | 已存在，需同步差异 |
| CapabilityBindingStatus.java | ✅ | ✅ | 已存在 |
| CapabilityCategory.java | ✅ | ✅ | 已存在 |
| CapabilityOwnership.java | ✅ | ❌ | **需复制** |
| CapabilityProviderType.java | ✅ | ❌ | **需复制** |
| CapabilityState.java | ✅ | ❌ | **需复制** |
| CapabilityStatus.java | ✅ | ✅ | 已存在 |
| CapabilityType.java | ✅ | ✅ | 已存在 |
| CollaborativeCapabilityRef.java | ✅ | ❌ | **需复制** |
| ConnectorType.java | ✅ | ✅ | 已存在 |
| DriverType.java | ✅ | ✅ | 已存在 |
| MainFirstConfig.java | ✅ | ❌ | **需复制** |
| SceneType.java | ✅ | ❌ | **需复制** |
| SkillForm.java | ✅ | ✅ | 已存在 |
| Visibility.java | ✅ | ✅ | 已存在 |

#### 1.3 Service 层对比

| 服务接口 | skill-scene | skill-capability | 升级动作 |
|----------|-------------|------------------|----------|
| AuditService | ❌ | ✅ | 保留 |
| BusinessSemanticsScorer | ✅ | ❌ | **需复制** |
| CapabilityBindingService | ✅ | ✅ | 已存在，需同步差异 |
| CapabilityClassificationService | ✅ | ❌ | **需复制** |
| CapabilityDiscoveryService | ✅ | ✅ | 已存在，需同步差异 |
| CapabilityService | ✅ | ✅ | 已存在，需同步差异 |
| CapabilityStateService | ✅ | ❌ | **需复制** |
| CapabilityStatsService | ❌ | ✅ | 保留 |
| DependencyHealthCheckService | ✅ | ❌ | **需复制** |
| InstallLogService | ✅ | ❌ | **需复制** |
| KeyManagementService | ✅ | ✅ | 已存在，需同步差异 |
| SceneSkillLifecycleService | ✅ | ❌ | **需复制** |
| SkillCapabilitySyncService | ✅ | ❌ | **需复制** |
| SkillStatisticsService | ✅ | ❌ | **需复制** |

#### 1.4 Controller 层对比

| 控制器 | skill-scene | skill-capability | 升级动作 |
|--------|-------------|------------------|----------|
| ActivationController | ❌ | ✅ | 保留 |
| AgentController | ❌ | ✅ | 保留 |
| ArchCheckController | ❌ | ✅ | 保留 |
| AuditController | ❌ | ✅ | 保留 |
| CapabilityController | ✅ | ✅ | 已存在，需同步差异 |
| CapabilityStatsController | ❌ | ✅ | 保留 |
| DiscoveryController | ❌ | ✅ | 保留 |
| KeyManagementController | ✅ | ✅ | 已存在，需同步差异 |
| NetworkController | ❌ | ✅ | 保留 |
| OrgController | ❌ | ✅ | 保留 |
| RoleManagementController | ❌ | ✅ | 保留 |
| SelectorController | ❌ | ✅ | 保留 |

---

### 2. 前端资源对比

#### 2.1 HTML 页面对比

| 页面 | skill-scene | skill-capability | 升级动作 |
|------|-------------|------------------|----------|
| capability-activation.html | ✅ | ✅ | 已存在，需同步差异 |
| capability-binding.html | ✅ | ✅ | 已存在，需同步差异 |
| capability-detail.html | ✅ | ✅ | 已存在，需同步差异 |
| capability-discovery.html | ✅ | ✅ | 已存在，需同步差异 |
| capability-management.html | ✅ | ✅ | 已存在，需同步差异 |
| my-capabilities.html | ✅ | ✅ | 已存在，需同步差异 |
| scene-capabilities.html | ✅ | ✅ | 已存在，需同步差异 |
| capability-stats.html | ✅ | ❌ | **需复制** |
| capability-create.html | ✅ | ❌ | **需复制** |
| address-space.html | ✅ | ❌ | **需复制** |
| key-management.html | ✅ | ❌ | **需复制** |

#### 2.2 JavaScript 文件对比

| JS 文件 | skill-scene | skill-capability | 升级动作 |
|---------|-------------|------------------|----------|
| capability-activation.js | ✅ | ✅ | 已存在，需同步差异 |
| capability-binding.js | ✅ | ✅ | 已存在，需同步差异 |
| capability-detail.js | ✅ | ✅ | 已存在，需同步差异 |
| capability-discovery.js | ✅ | ✅ | 已存在，需同步差异 |
| capability-management.js | ✅ | ✅ | 已存在，需同步差异 |
| my-capabilities.js | ✅ | ✅ | 已存在，需同步差异 |
| capability-stats.js | ✅ | ❌ | **需复制** |
| capability-create.js | ✅ | ❌ | **需复制** |
| address-space.js | ✅ | ❌ | **需复制** |
| key-management.js | ✅ | ❌ | **需复制** |

#### 2.3 CSS 文件对比

| CSS 文件 | skill-scene | skill-capability | 升级动作 |
|----------|-------------|------------------|----------|
| capability-activation.css | ✅ | ✅ | 已存在，需同步差异 |
| capability-binding.css | ✅ | ✅ | 已存在，需同步差异 |
| capability-detail.css | ✅ | ✅ | 已存在，需同步差异 |
| capability-discovery.css | ✅ | ✅ | 已存在，需同步差异 |
| capability-management.css | ✅ | ✅ | 已存在，需同步差异 |
| my-capabilities.css | ✅ | ✅ | 已存在，需同步差异 |
| address-space.css | ✅ | ❌ | **需复制** |
| key-management.css | ✅ | ❌ | **需复制** |

---

## 📦 需复制的完整模块清单

### 后端 Java 模块 (需新建目录)

```
skill-capability/src/main/java/net/ooder/skill/capability/
├── activation/           # 新建目录
│   ├── ActivationProcess.java
│   ├── ActivationService.java
│   ├── ActivationServiceImpl.java
│   └── NetworkActionExecutor.java
├── config/               # 新建目录
│   └── CapabilityConfig.java
├── connector/            # 新建目录
│   ├── Connector.java
│   ├── ConnectorFactory.java
│   ├── HttpConnector.java
│   ├── InternalConnector.java
│   └── WebSocketConnector.java
├── driver/               # 新建目录
│   └── DriverCondition.java
├── event/                # 新建目录
│   ├── SceneTypeUpdateEvent.java
│   └── SceneTypeUpdateEventListener.java
├── fallback/             # 新建目录
│   ├── FallbackService.java
│   └── FallbackStrategy.java
├── install/              # 新建目录
│   ├── InstallConfig.java
│   ├── InstallService.java
│   ├── InstallServiceImpl.java
│   └── SceneCapabilityInstallLifecycle.java
├── invoke/               # 新建目录
│   ├── CapabilityInvoker.java
│   └── CapabilityInvokerImpl.java
└── model/                # 新增文件
    ├── BusinessCategory.java
    ├── CapabilityAddress.java
    ├── CapabilityOwnership.java
    ├── CapabilityProviderType.java
    ├── CapabilityState.java
    ├── CollaborativeCapabilityRef.java
    ├── MainFirstConfig.java
    └── SceneType.java
```

### 后端 Service 层 (需新增)

```
skill-capability/src/main/java/net/ooder/skill/capability/service/
├── BusinessSemanticsScorer.java
├── CapabilityClassificationService.java
├── CapabilityStateService.java
├── DependencyHealthCheckService.java
├── InstallLogService.java
├── SceneSkillLifecycleService.java
├── SkillCapabilitySyncService.java
├── SkillStatisticsService.java
└── impl/
    ├── BusinessSemanticsScorerImpl.java
    ├── CapabilityStateServiceImpl.java
    ├── DependencyHealthCheckServiceImpl.java
    ├── InstallLogServiceImpl.java
    └── SceneSkillLifecycleServiceImpl.java
```

### 前端页面 (需新增)

```
skill-capability/src/main/resources/static/console/
├── pages/
│   ├── capability-stats.html
│   ├── capability-create.html
│   ├── address-space.html
│   └── key-management.html
├── js/pages/
│   ├── capability-stats.js
│   ├── capability-create.js
│   ├── address-space.js
│   └── key-management.js
└── css/pages/
    ├── address-space.css
    └── key-management.css
```

---

## 🔧 功能模块说明

### 1. Activation 激活模块

**功能描述**: 能力激活流程管理，支持多步骤激活、网络动作执行

**核心类**:
- `ActivationProcess` - 激活流程状态机
- `ActivationService` - 激活服务接口
- `ActivationServiceImpl` - 激活服务实现
- `NetworkActionExecutor` - 网络动作执行器

**API 端点**:
- `POST /api/v1/activations/{installId}/start` - 启动激活
- `POST /api/v1/activations/{installId}/steps/{stepId}/execute` - 执行步骤
- `GET /api/v1/activations/{installId}/process` - 获取激活流程

---

### 2. Connector 连接器模块

**功能描述**: 能力连接器抽象层，支持 HTTP、WebSocket、内部调用等多种连接方式

**核心类**:
- `Connector` - 连接器接口
- `ConnectorFactory` - 连接器工厂
- `HttpConnector` - HTTP 连接器实现
- `WebSocketConnector` - WebSocket 连接器实现
- `InternalConnector` - 内部调用连接器

---

### 3. Install 安装模块

**功能描述**: 能力安装生命周期管理，支持安装配置、日志记录

**核心类**:
- `InstallConfig` - 安装配置
- `InstallService` - 安装服务接口
- `InstallServiceImpl` - 安装服务实现
- `SceneCapabilityInstallLifecycle` - 场景能力安装生命周期

---

### 4. Invoke 调用模块

**功能描述**: 能力调用抽象层，支持统一的能力调用接口

**核心类**:
- `CapabilityInvoker` - 能力调用器接口
- `CapabilityInvokerImpl` - 能力调用器实现

---

### 5. Fallback 降级模块

**功能描述**: 能力降级策略管理，支持多种降级策略

**核心类**:
- `FallbackService` - 降级服务
- `FallbackStrategy` - 降级策略

---

### 6. Event 事件模块

**功能描述**: 场景类型更新事件处理

**核心类**:
- `SceneTypeUpdateEvent` - 场景类型更新事件
- `SceneTypeUpdateEventListener` - 事件监听器

---

## ✅ 任务检查清单

### 阶段一: 后端模块复制

- [ ] 创建 activation 目录及 4 个 Java 文件
- [ ] 创建 config 目录及 1 个 Java 文件
- [ ] 创建 connector 目录及 5 个 Java 文件
- [ ] 创建 driver 目录及 1 个 Java 文件
- [ ] 创建 event 目录及 2 个 Java 文件
- [ ] 创建 fallback 目录及 2 个 Java 文件
- [ ] 创建 install 目录及 4 个 Java 文件
- [ ] 创建 invoke 目录及 2 个 Java 文件
- [ ] 复制 8 个新增 Model 类
- [ ] 复制 8 个新增 Service 接口
- [ ] 复制 5 个新增 Service 实现
- [ ] 同步已存在的类差异

### 阶段二: 前端资源复制

- [ ] 复制 capability-stats.html
- [ ] 复制 capability-create.html
- [ ] 复制 address-space.html
- [ ] 复制 key-management.html
- [ ] 复制对应的 JS 文件 (4 个)
- [ ] 复制对应的 CSS 文件 (2 个)
- [ ] 同步已存在的前端文件差异

### 阶段三: 集成测试

- [ ] 更新 pom.xml 依赖
- [ ] 更新 skill.yaml 配置
- [ ] 更新 menu-config.json 菜单配置
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 前端页面功能验证

---

## 📞 联系方式

如有问题请联系 skill-scene 团队。

---

**文档版本**: v1.0  
**最后更新**: 2026-03-17
