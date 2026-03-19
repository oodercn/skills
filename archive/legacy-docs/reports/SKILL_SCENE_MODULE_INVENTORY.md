# skill-scene 模块清单

## 一、前端页面模块 (pages/)

### 1. 核心页面

| 页面 | 功能 | JS依赖 | CSS依赖 | API依赖 |
|------|------|--------|---------|---------|
| login.html | 登录认证 | login.js | login.css | AuthController |
| index.html | 控制台首页 | dashboard.js | nexus.css | 多个 |

### 2. 能力管理模块

| 页面 | 功能 | JS依赖 | CSS依赖 | API依赖 |
|------|------|--------|---------|---------|
| capability-discovery.html | 发现能力 | capability-discovery.js, capability-service.js | capability-discovery.css | CapabilityDiscoveryController |
| capability-management.html | 能力管理 | capability-management.js | - | SceneController |
| capability-detail.html | 能力详情 | capability-detail.js | capability-detail.css | SceneController |
| capability-create.html | 创建能力 | capability-create.js | - | SceneController |
| capability-binding.html | 能力绑定 | capability-binding.js | - | SceneController |
| capability-stats.html | 能力统计 | capability-stats.js | - | CapabilityStatsController |
| capability-activation.html | 能力激活 | - | - | ActivationController |
| my-capabilities.html | 我的能力 | my-capabilities.js | - | SceneController |
| installed-scene-capabilities.html | 已安装能力 | installed-scene-capabilities.js | - | SceneController |

### 3. 场景管理模块

| 页面 | 功能 | JS依赖 | CSS依赖 | API依赖 |
|------|------|--------|---------|---------|
| scene-management.html | 场景管理 | scene-management.js | - | SceneController |
| scene-detail.html | 场景详情 | scene-detail.js | - | SceneController |
| scene-capabilities.html | 场景能力 | scene-capabilities.js | - | SceneController |
| scene-capability-detail.html | 场景能力详情 | scene-capability-detail.js | - | SceneController |
| scene-knowledge.html | 场景知识库 | scene-knowledge.js | scene-knowledge.css | SceneKnowledgeController |
| scene-group-management.html | 场景组管理 | scene-group-management.js | - | SceneGroupController |
| scene-group-detail.html | 场景组详情 | scene-group-detail.js | - | SceneGroupController |

### 4. LLM配置模块

| 页面 | 功能 | JS依赖 | CSS依赖 | API依赖 |
|------|------|--------|---------|---------|
| llm-config.html | LLM配置 | - | - | LlmProviderController |
| llm-monitor.html | LLM监控 | - | - | LlmMonitorController |

### 5. 系统配置模块

| 页面 | 功能 | JS依赖 | CSS依赖 | API依赖 |
|------|------|--------|---------|---------|
| config-system.html | 系统配置 | config-system.js | config-system.css | ConfigController, SystemConfigController |
| security-config.html | 安全配置 | security-config.js | security-config.css | - |
| driver-config.html | 驱动配置 | driver-config.js | driver-config.css | - |
| key-management.html | 密钥管理 | key-management.js | key-management.css | KeyManagementService |
| audit-logs.html | 审计日志 | audit-logs.js | audit-logs.css | AuditController |
| address-space.html | 地址空间 | address-space.js | address-space.css | AddressSpaceController |

### 6. 用户与组织模块

| 页面 | 功能 | JS依赖 | CSS依赖 | API依赖 |
|------|------|--------|---------|---------|
| org-management.html | 组织管理 | - | - | OrgController |
| role-admin.html | 管理员角色 | dashboard.js (admin) | role-pages.css | RoleManagementController |
| role-installer.html | 安装者角色 | role-installer.js | role-pages.css | InstallerController |
| role-leader.html | 主导者角色 | - | role-pages.css | - |
| role-collaborator.html | 协作者角色 | - | role-pages.css | - |
| role-developer.html | 开发者角色 | - | role-pages.css | - |
| role-user.html | 用户角色 | - | role-pages.css | - |
| my-profile.html | 个人中心 | - | - | - |
| my-todos.html | 我的待办 | my-todos.js | - | TodoController |
| my-history.html | 历史记录 | my-history.js | - | HistoryController |
| my-scenes.html | 我的场景 | my-scenes.js | my-scenes.css | SceneController |

### 7. 模板管理模块

| 页面 | 功能 | JS依赖 | CSS依赖 | API依赖 |
|------|------|--------|---------|---------|
| template-management.html | 模板管理 | template-management.js | - | SceneTemplateController |
| template-detail.html | 模板详情 | template-detail.js | - | SceneTemplateController |

### 8. 知识库模块

| 页面 | 功能 | JS依赖 | CSS依赖 | API依赖 |
|------|------|--------|---------|---------|
| knowledge-base.html | 知识库 | - | - | KnowledgeBaseController |

### 9. 其他模块

| 页面 | 功能 | JS依赖 | CSS依赖 | API依赖 |
|------|------|--------|---------|---------|
| install.html | 安装向导 | install.js | install.css | InstallController |
| help.html | 帮助中心 | help.js | help.css | - |
| arch-check.html | 架构检查 | arch-check.js | - | ArchCheckController |
| daily-report-form.html | 日报表单 | daily-report-form.js | daily-report-form.css | ReportSkillController |
| agent-list.html | Agent列表 | - | - | AgentController |

---

## 二、JavaScript 模块 (js/)

### 1. 核心框架

| 文件 | 功能 | 依赖 |
|------|------|------|
| nexus.js | 核心框架（主题、侧边栏、用户菜单） | - |
| menu.js | 动态菜单系统 | nexus.js |
| page-init.js | 页面初始化 | nexus.js, menu.js |
| api-client.js | API客户端 | - |

### 2. 服务层

| 文件 | 功能 | 依赖 |
|------|------|------|
| capability-service.js | 能力服务 | api-client.js |
| llm-assistant.js | LLM助手 | api-client.js |
| skill-discovery.js | 技能发现 | api-client.js |
| skill-management.js | 技能管理 | api-client.js |

### 3. 页面逻辑

| 文件 | 功能 | 页面 |
|------|------|------|
| capability-discovery.js | 能力发现逻辑 | capability-discovery.html |
| scene-management.js | 场景管理逻辑 | scene-management.html |
| config-system.js | 系统配置逻辑 | config-system.html |

### 4. 工具类

| 文件 | 功能 |
|------|------|
| common.js | 通用工具函数 |
| utils.js | 工具函数 |
| dict-cache.js | 字典缓存 |

---

## 三、CSS 模块 (css/)

### 1. 框架样式

| 文件 | 功能 |
|------|------|
| nexus.css | 核心框架样式 |
| theme.css | 主题变量 |
| nx-page.css | 页面布局 |

### 2. 组件样式

| 目录/文件 | 功能 |
|-----------|------|
| components/buttons.css | 按钮组件 |
| components/cards.css | 卡片组件 |
| components/forms.css | 表单组件 |
| components/tables.css | 表格组件 |
| components/tree.css | 树形组件 |
| components/profile-cards.css | 配置卡片 |
| components/stat-cards.css | 统计卡片 |
| components/install-progress.css | 安装进度 |
| components/lifecycle-control.css | 生命周期控制 |
| components/dependency-list.css | 依赖列表 |
| components/capability-config.css | 能力配置 |

### 3. 页面样式

| 文件 | 页面 |
|------|------|
| pages/capability-discovery.css | 能力发现 |
| pages/config-system.css | 系统配置 |
| pages/login.css | 登录页 |
| pages/my-scenes.css | 我的场景 |

---

## 四、后端 API 模块 (java/)

### 1. 控制器 (controller/)

| 控制器 | API路径 | 功能 |
|--------|---------|------|
| AuthController | /api/v1/auth/* | 认证授权 |
| CapabilityDiscoveryController | /api/v1/capability/* | 能力发现 |
| SceneController | /api/v1/scene/* | 场景管理 |
| SceneGroupController | /api/v1/scene-group/* | 场景组管理 |
| SceneTemplateController | /api/v1/template/* | 模板管理 |
| LlmProviderController | /api/v1/llm/provider/* | LLM配置 |
| LlmController | /api/v1/llm/* | LLM调用 |
| ConfigController | /api/v1/config/* | 配置管理 |
| SystemConfigController | /api/v1/system/* | 系统配置 |
| OrgController | /api/v1/org/* | 组织管理 |
| RoleManagementController | /api/v1/role/* | 角色管理 |
| InstallController | /api/v1/install/* | 安装向导 |
| ActivationController | /api/v1/activation/* | 能力激活 |
| AuditController | /api/v1/audit/* | 审计日志 |
| KnowledgeBaseController | /api/v1/knowledge/* | 知识库 |
| AgentController | /api/v1/agent/* | Agent管理 |
| NetworkController | /api/v1/network/* | 网络拓扑 |
| TodoController | /api/v1/todo/* | 待办事项 |
| HistoryController | /api/v1/history/* | 历史记录 |
| DictController | /api/v1/dict/* | 字典服务 |

### 2. 服务层 (service/)

| 服务 | 功能 |
|------|------|
| AuthService | 认证服务 |
| SceneService | 场景服务 |
| SceneTemplateService | 模板服务 |
| SceneGroupService | 场景组服务 |
| DependencyHealthCheckService | 依赖检查 |
| DependencyAutoInstallService | 自动安装依赖 |
| MenuAutoRegisterService | 菜单自动注册 |
| AuditService | 审计服务 |
| TodoService | 待办服务 |
| HistoryService | 历史服务 |
| DictService | 字典服务 |

### 3. 能力服务 (capability/service/)

| 服务 | 功能 |
|------|------|
| SkillStatisticsService | 技能统计 |
| SkillCapabilitySyncService | 能力同步 |
| SceneSkillLifecycleService | 生命周期管理 |
| SceneSkillCategoryDetector | 分类检测 |
| InstallLogService | 安装日志 |
| KeyManagementService | 密钥管理 |

### 4. 配置服务 (config/)

| 服务 | 功能 |
|------|------|
| ConfigLoaderService | 配置加载 |
| ConfigInheritanceResolver | 继承解析 |
| SystemConfigInitializer | 系统配置初始化 |

---

## 五、模块聚合建议

根据功能关联性，建议将 skill-scene 的功能聚合为以下模块：

### 模块1: capability-discovery (能力发现)
- 页面: capability-discovery.html
- JS: capability-discovery.js, capability-service.js
- CSS: capability-discovery.css
- API: CapabilityDiscoveryController
- 类型: scene
- 归属: skill-scene

### 模块2: scene-management (场景管理)
- 页面: scene-*.html (6个)
- JS: scene-management.js, scene-detail.js
- CSS: -
- API: SceneController, SceneGroupController
- 类型: scene
- 归属: skill-scene

### 模块3: llm-config (LLM配置)
- 页面: llm-config.html, llm-monitor.html
- JS: llm-assistant.js
- CSS: -
- API: LlmProviderController, LlmController, LlmMonitorController
- 类型: provider
- 归属: skill-scene

### 模块4: system-config (系统配置)
- 页面: config-system.html, security-config.html, driver-config.html, key-management.html
- JS: config-system.js, security-config.js, driver-config.js, key-management.js
- CSS: config-system.css, security-config.css, driver-config.css, key-management.css
- API: ConfigController, SystemConfigController
- 类型: core
- 归属: skill-scene

### 模块5: auth (认证授权)
- 页面: login.html, role-*.html (6个)
- JS: login.js
- CSS: login.css, role-pages.css
- API: AuthController, RoleManagementController
- 类型: core
- 归属: skill-common

### 模块6: org-management (组织管理)
- 页面: org-management.html, my-profile.html
- JS: -
- CSS: -
- API: OrgController
- 类型: core
- 归属: skill-common

### 模块7: knowledge-base (知识库)
- 页面: knowledge-base.html, scene-knowledge.html
- JS: scene-knowledge.js
- CSS: scene-knowledge.css
- API: KnowledgeBaseController, SceneKnowledgeController
- 类型: provider
- 归属: skill-knowledge

### 模块8: template-management (模板管理)
- 页面: template-management.html, template-detail.html
- JS: template-management.js, template-detail.js
- CSS: -
- API: SceneTemplateController
- 类型: scene
- 归属: skill-scene

### 模块9: audit-logs (审计日志)
- 页面: audit-logs.html, address-space.html
- JS: audit-logs.js, address-space.js
- CSS: audit-logs.css, address-space.css
- API: AuditController, AddressSpaceController
- 类型: core
- 归属: skill-common

### 模块10: personal-workspace (个人工作台)
- 页面: my-capabilities.html, my-scenes.html, my-todos.html, my-history.html
- JS: my-capabilities.js, my-scenes.js, my-todos.js, my-history.js
- CSS: my-scenes.css
- API: TodoController, HistoryController
- 类型: scene
- 归属: skill-scene
