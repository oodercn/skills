# Skill-Scene 拆分计划

## 背景

当前 `skill-scene` 包含过多功能：
- **36 个控制器**
- **47 个 HTML 页面**
- **67 个服务类**

功能涵盖：场景管理、能力管理、组织管理、认证、LLM 集成、审计、系统配置、知识库等。

## 拆分目标

按照 MVP 最小化原则，将 skill-scene 拆分为多个独立的 skills。

## 拆分方案

### 1. skill-common (已存在 - 基础层)

**保留功能：**
- 认证管理 (AuthController, AuthService)
- 组织管理 (OrgController, OrgService)
- 菜单管理 (MenuController)
- 用户管理
- 角色管理

**页面：**
- login.html
- org-management.html
- role-*.html (所有角色页面)
- my-profile.html

---

### 2. skill-capability (能力层)

**迁移功能：**
- 能力管理 (CapabilityController, CapabilityService)
- 能力发现 (CapabilityDiscoveryController, CapabilityDiscoveryService)
- 能力绑定 (CapabilityBindingService)
- 能力激活 (ActivationController, ActivationService)
- 能力安装 (InstallController, InstallService)
- 能力统计 (CapabilityStatsController, CapabilityStatsService)
- 密钥管理 (KeyManagementService)

**页面：**
- capability-management.html
- capability-detail.html
- capability-create.html
- capability-discovery.html
- capability-activation.html
- capability-binding.html
- capability-stats.html
- my-capabilities.html
- key-management.html

**API 端点：**
- /api/v1/capabilities/**
- /api/v1/capability-discovery/**
- /api/v1/activation/**
- /api/v1/install/**
- /api/v1/capability-stats/**
- /api/v1/keys/**

---

### 3. skill-llm (LLM层)

**迁移功能：**
- LLM 管理 (LlmController)
- LLM 监控 (LlmMonitorController)
- LLM 提供者 (LlmProviderController)
- LLM 脚本 (LlmScriptController)
- 场景 LLM (SceneLlmController)

**页面：**
- llm-config.html
- llm-monitor.html

**API 端点：**
- /api/v1/llm/**
- /api/v1/llm-monitor/**
- /api/v1/llm-providers/**
- /api/v1/llm-scripts/**

---

### 4. skill-system (系统层)

**迁移功能：**
- 系统配置 (SystemConfigController)
- 审计日志 (AuditController, AuditService)
- 安全配置 (security-config.html)
- 架构检查 (ArchCheckController)
- 字典管理 (DictController, DictService)
- 配置版本 (ConfigVersionController)

**页面：**
- config-system.html
- audit-logs.html
- security-config.html
- arch-check.html

**API 端点：**
- /api/v1/system/**
- /api/v1/audit/**
- /api/v1/dict/**
- /api/v1/config-version/**

---

### 5. skill-scene (精简后 - 场景层)

**保留功能：**
- 场景管理 (SceneController, SceneService)
- 场景组管理 (SceneGroupController, SceneGroupService)
- 场景模板 (SceneTemplateController, SceneTemplateService)
- 场景知识 (SceneKnowledgeController)
- 待办事项 (TodoController, TodoService)
- 历史记录 (HistoryController, HistoryService)
- 执行日志 (ExecutionLogController, ExecutionLogService)

**页面：**
- scene-management.html
- scene-detail.html
- scene-capabilities.html
- scene-capability-detail.html
- scene-group-management.html
- scene-group-detail.html
- scene-knowledge.html
- template-management.html
- template-detail.html
- my-scenes.html
- my-todos.html
- my-history.html
- installed-scene-capabilities.html

**API 端点：**
- /api/v1/scenes/**
- /api/v1/scene-groups/**
- /api/v1/scene-templates/**
- /api/v1/todos/**
- /api/v1/history/**
- /api/v1/execution-logs/**

---

### 6. skill-knowledge (知识层) - 可选

**迁移功能：**
- 知识库 (KnowledgeBaseController)
- 场景知识绑定 (SceneKnowledgeBindingService)

**页面：**
- knowledge-base.html

---

## 依赖关系

```
skill-common (基础层)
    ↓
skill-system (系统层)
    ↓
skill-capability (能力层)
    ↓
skill-scene (场景层)
    ↓
skill-llm (LLM层)
skill-knowledge (知识层)
```

## 执行步骤

### 阶段 1: 准备工作
1. 创建新 skill 目录结构
2. 创建 pom.xml 和 skill.yaml
3. 创建基础包结构

### 阶段 2: 迁移 skill-capability
1. 迁移能力管理相关代码
2. 迁移能力相关页面
3. 更新菜单配置

### 阶段 3: 迁移 skill-llm
1. 迁移 LLM 相关代码
2. 迁移 LLM 相关页面
3. 更新菜单配置

### 阶段 4: 迁移 skill-system
1. 迁移系统管理相关代码
2. 迁移系统管理相关页面
3. 更新菜单配置

### 阶段 5: 精简 skill-scene
1. 删除已迁移的代码
2. 更新依赖
3. 更新菜单配置

### 阶段 6: 验证
1. 编译验证
2. 功能测试
3. 集成测试

## 风险评估

1. **代码依赖** - 需要仔细处理跨 skill 的依赖
2. **API 兼容性** - 确保 API 路径不变或提供兼容层
3. **菜单配置** - 需要更新菜单配置以反映新的 skill 结构
4. **数据存储** - 需要确保数据存储路径兼容

## 时间估算

- 阶段 1: 准备工作 - 1 小时
- 阶段 2: 迁移 skill-capability - 2 小时
- 阶段 3: 迁移 skill-llm - 1 小时
- 阶段 4: 迁移 skill-system - 1 小时
- 阶段 5: 精简 skill-scene - 1 小时
- 阶段 6: 验证 - 1 小时

**总计: 约 7 小时**
