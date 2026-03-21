# Map<String, Object> 重构优先级划分

## 一、统计概览

| 指标 | 数值 |
|------|:----:|
| 使用 Map<String, Object> 的文件总数 | **100** |
| 已创建 DTO 的文件 | 8 |
| 实际需要重构的文件 | **92** |

---

## 二、优先级划分

### 🔴 P0 - 核心业务（必须重构）

**影响**: 系统核心功能，数据一致性风险高

| 文件 | 模块 | 使用场景 | 建议 DTO |
|------|------|----------|----------|
| `SkillCapabilitySyncService.java` | 能力同步 | YAML 解析数据 | 已有 YamlDtoConverter |
| `CapabilityDiscoveryServiceImpl.java` | 能力发现 | 能力数据返回 | CapabilityDetailDTO |
| `SceneSkillLifecycleServiceImpl.java` | 生命周期 | 场景状态数据 | SceneLifecycleDTO |
| `ActivationServiceImpl.java` | 激活服务 | 激活步骤数据 | ActivationStepDataDTO |
| `InstallService.java` | 安装服务 | 安装配置 | InstallRequestDTO |
| `InstallConfig.java` | 安装配置 | 配置数据 | InstallConfigDTO |
| `Capability.java` | 能力模型 | metadata 字段 | 保持 Map (扩展数据) |

**小计**: 7 个文件

---

### 🟠 P1 - 重要功能（推荐重构）

**影响**: 重要业务功能，用户体验相关

| 文件 | 模块 | 使用场景 | 建议 DTO |
|------|------|----------|----------|
| `KeyManagementController.java` | 密钥管理 | 密钥配置 | KeyConfigDTO |
| `KeyManagementServiceImpl.java` | 密钥服务 | 密钥数据 | KeyDataDTO |
| `KeyManagementService.java` | 密钥接口 | 返回数据 | KeyDataDTO |
| `KeyRuleController.java` | 密钥规则 | 规则配置 | KeyRuleDTO |
| `CapabilityStatsServiceImpl.java` | 统计服务 | 统计数据 | StatsDTO |
| `LlmCallLogServiceImpl.java` | LLM 日志 | 日志数据 | LlmCallLogDTO |
| `LlmStatsServiceImpl.java` | LLM 统计 | 统计数据 | LlmStatsDTO |
| `LlmStatsSdkAdapter.java` | LLM 适配 | 统计适配 | LlmStatsDTO |

**小计**: 9 个文件

---

### 🟡 P2 - 配置相关（建议重构）

**影响**: 配置管理，可维护性相关

| 文件 | 模块 | 使用场景 | 建议 DTO |
|------|------|----------|----------|
| `ConfigNode.java` | 配置节点 | 配置树结构 | ConfigNodeDTO |
| `ConfigLoaderService.java` | 配置加载 | 配置数据 | ConfigDataDTO |
| `ConfigLoaderServiceImpl.java` | 配置加载实现 | 配置数据 | ConfigDataDTO |
| `ConfigController.java` | 配置控制器 | 配置返回 | ConfigResponseDTO |
| `CommConfigController.java` | 通信配置 | 通信配置 | CommConfigDTO |
| `VfsConfigController.java` | VFS 配置 | VFS 配置 | VfsConfigDTO |
| `AuthConfigController.java` | 认证配置 | 认证配置 | AuthConfigDTO |
| `OrgConfigController.java` | 组织配置 | 组织配置 | OrgConfigDTO |
| `DbConfigController.java` | 数据库配置 | 数据库配置 | DbConfigDTO |

**小计**: 9 个文件

---

### 🟢 P3 - LLM 相关（可选重构）

**影响**: LLM 功能，扩展性相关

| 文件 | 模块 | 使用场景 | 建议 DTO |
|------|------|----------|----------|
| `LlmController.java` | LLM 控制器 | LLM 请求 | LlmRequestDTO |
| `LlmConfigController.java` | LLM 配置 | LLM 配置 | LlmConfigDTO |
| `LlmProviderController.java` | LLM 提供者 | 提供者配置 | LlmProviderDTO |
| `LlmMonitorController.java` | LLM 监控 | 监控数据 | LlmMonitorDTO |
| `SceneLlmController.java` | 场景 LLM | 场景 LLM | SceneLlmDTO |
| `BaiduLlmProvider.java` | 百度 LLM | LLM 响应 | LlmResponseDTO |
| `DeepSeekLlmProvider.java` | DeepSeek LLM | LLM 响应 | LlmResponseDTO |
| `AliyunBailianLlmProvider.java` | 阿里云 LLM | LLM 响应 | LlmResponseDTO |
| `SkillPromptServiceImpl.java` | 提示词服务 | 提示词数据 | PromptDTO |
| `MultiLevelContextManagerImpl.java` | 上下文管理 | 上下文数据 | ContextDTO |
| `GlobalContextConfig.java` | 全局上下文 | 配置数据 | ContextConfigDTO |

**小计**: 11 个文件

---

### ⚪ P4 - 知识库相关（可选重构）

**影响**: 知识库功能，扩展性相关

| 文件 | 模块 | 使用场景 | 建议 DTO |
|------|------|----------|----------|
| `KnowledgeBaseController.java` | 知识库 | 知识库数据 | KnowledgeBaseDTO |
| `KnowledgeOrganizationController.java` | 知识组织 | 组织数据 | KnowledgeOrgDTO |
| `SceneKnowledgeController.java` | 场景知识 | 场景知识 | SceneKnowledgeDTO |
| `KnowledgeBaseBindingController.java` | 知识绑定 | 绑定数据 | KnowledgeBindingDTO |
| `EmbeddingService.java` | 嵌入服务 | 嵌入数据 | EmbeddingDTO |
| `EmbeddingConfigController.java` | 嵌入配置 | 配置数据 | EmbeddingConfigDTO |
| `SceneKnowledgeBindingService.java` | 知识绑定服务 | 绑定数据 | KnowledgeBindingDTO |

**小计**: 7 个文件

---

### 🔵 P5 - 其他功能（低优先级）

**影响**: 辅助功能，可延后处理

| 文件 | 模块 | 使用场景 |
|------|------|----------|
| `StatsController.java` | 统计控制器 | 统计数据 |
| `SceneGroupController.java` | 场景组 | 场景组数据 |
| `SceneGroupService.java` | 场景组服务 | 场景组数据 |
| `SceneGroupServiceSEImpl.java` | 场景组实现 | 场景组数据 |
| `SceneMenuController.java` | 场景菜单 | 菜单数据 |
| `SceneTemplateService.java` | 场景模板 | 模板数据 |
| `AddressSpaceController.java` | 地址空间 | 地址数据 |
| `ArchCheckController.java` | 架构检查 | 检查结果 |
| `AuthMenuController.java` | 认证菜单 | 菜单数据 |
| `RoleManagementController.java` | 角色管理 | 角色数据 |
| `RoleManagementService.java` | 角色服务 | 角色数据 |
| `NetworkJoinController.java` | 网络加入 | 加入数据 |
| `CapabilityController.java` | 能力控制器 | 能力数据 |
| `InstallSceneController.java` | 安装场景 | 安装数据 |
| `InstallSceneService.java` | 安装场景服务 | 安装数据 |
| `SetupController.java` | 设置控制器 | 设置数据 |
| `MvpSkillIndexLoader.java` | 技能索引 | 索引数据 |
| `AuditAspect.java` | 审计切面 | 审计数据 |
| `InMemoryAuditService.java` | 审计服务 | 审计数据 |
| `AuditServiceSdkImpl.java` | 审计实现 | 审计数据 |

**小计**: 20 个文件

---

### ⚙️ P6 - 工具类（保持现状）

**影响**: 内部工具，不影响 API

| 文件 | 模块 | 使用场景 | 建议 |
|------|------|----------|------|
| `NavigateToPageTool.java` | 导航工具 | 工具参数 | 保持 Map |
| `StartScanTool.java` | 扫描工具 | 工具参数 | 保持 Map |
| `SelectCapabilityTool.java` | 选择工具 | 工具参数 | 保持 Map |
| `InstallCapabilityTool.java` | 安装工具 | 工具参数 | 保持 Map |
| `ExecutionWebSocketHandler.java` | WebSocket | 消息数据 | 保持 Map |
| `ContextEventController.java` | 上下文事件 | 事件数据 | 保持 Map |
| `PageNavigateEvent.java` | 页面导航 | 事件数据 | 保持 Map |
| `ContextUpdate.java` | 上下文更新 | 更新数据 | 保持 Map |
| `MenuKnowledgeInitService.java` | 菜单初始化 | 初始化数据 | 保持 Map |
| `PromptIndexingService.java` | 提示词索引 | 索引数据 | 保持 Map |
| `SceneEngineIntegration.java` | 场景引擎 | 集成数据 | 保持 Map |
| `SkillDownloadService.java` | 技能下载 | 下载数据 | 保持 Map |
| `SceneCapabilityInstallLifecycle.java` | 安装生命周期 | 生命周期数据 | 保持 Map |

**小计**: 13 个文件

---

### 📦 P7 - 已有 DTO（无需重构）

| 文件 | 状态 |
|------|------|
| `ActivationStepDataDTO.java` | ✅ 已创建 DTO |
| `ActivationResultDTO.java` | ✅ 已创建 DTO |
| `InstallRequestDTO.java` | ✅ 已创建 DTO |
| `YamlDtoConverter.java` | ✅ 工具类 |
| `CapabilityDTO.java` | ✅ 已重构 |
| `DiscoveryController.java` | ✅ 已重构 |
| `LocalDiscoveryService.java` | ✅ 已重构 |

**小计**: 7 个文件

---

### 📋 P8 - DTO 文件中的 Map（合理使用）

| 文件 | 使用场景 | 建议 |
|------|----------|------|
| `LlmKnowledgeConfigDTO.java` | 扩展配置 | 保持 Map |
| `DbConnectionDTO.java` | 连接参数 | 保持 Map |
| `ScriptGenerateRequestDTO.java` | 脚本参数 | 保持 Map |
| `ActionExecuteRequestDTO.java` | 执行参数 | 保持 Map |
| `ActionExecuteResultDTO.java` | 执行结果 | 保持 Map |
| `UserSceneGroupDTO.java` | 场景组配置 | 保持 Map |
| `KnowledgeDocumentDTO.java` | 文档元数据 | 保持 Map |
| `KnowledgeSearchRequestDTO.java` | 搜索参数 | 保持 Map |
| `KnowledgeSearchResultDTO.java` | 搜索结果 | 保持 Map |
| `SceneGroupConfigDTO.java` | 场景组配置 | 保持 Map |
| `LlmProviderConfigDTO.java` | LLM 配置 | 保持 Map |
| `LlmCallLogDTO.java` | 调用日志 | 保持 Map |

**小计**: 12 个文件

---

## 三、重构计划

### 阶段一：P0 核心业务（预计 3 天）

| 任务 | 文件数 | 优先级 |
|------|:------:|:------:|
| 重构 SkillCapabilitySyncService | 1 | 🔴 |
| 重构 CapabilityDiscoveryServiceImpl | 1 | 🔴 |
| 重构 SceneSkillLifecycleServiceImpl | 1 | 🔴 |
| 重构 ActivationServiceImpl | 1 | 🔴 |
| 重构 InstallService | 1 | 🔴 |
| 重构 InstallConfig | 1 | 🔴 |

### 阶段二：P1 重要功能（预计 2 天）

| 任务 | 文件数 | 优先级 |
|------|:------:|:------:|
| 重构密钥管理 | 4 | 🟠 |
| 重构统计服务 | 4 | 🟠 |

### 阶段三：P2 配置相关（预计 2 天）

| 任务 | 文件数 | 优先级 |
|------|:------:|:------:|
| 重构配置节点 | 2 | 🟡 |
| 重构配置控制器 | 7 | 🟡 |

### 阶段四：P3-P5（预计 3 天）

| 任务 | 文件数 | 优先级 |
|------|:------:|:------:|
| 重构 LLM 相关 | 11 | 🟢 |
| 重构知识库相关 | 7 | ⚪ |
| 重构其他功能 | 20 | 🔵 |

### 阶段五：P6-P8（保持现状）

| 任务 | 文件数 | 优先级 |
|------|:------:|:------:|
| 工具类保持 Map | 13 | ⚙️ |
| DTO 中合理使用 | 12 | 📦 |

---

## 四、总结

| 优先级 | 文件数 | 是否重构 | 预计时间 |
|:------:|:------:|:--------:|:--------:|
| P0 | 7 | ✅ 是 | 3 天 |
| P1 | 9 | ✅ 是 | 2 天 |
| P2 | 9 | ✅ 是 | 2 天 |
| P3 | 11 | ✅ 是 | 1.5 天 |
| P4 | 7 | ✅ 是 | 1 天 |
| P5 | 20 | ✅ 是 | 1.5 天 |
| P6 | 13 | ❌ 否 | - |
| P7 | 7 | ✅ 已完成 | - |
| P8 | 12 | ❌ 否 | - |
| **总计** | **100** | **68 需重构** | **11 天** |

---

**创建时间**: 2026-03-21  
**状态**: 待评审  
**文档版本**: 1.0
