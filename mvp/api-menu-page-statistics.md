# MVP API、菜单、页面统计报告

## 一、API 端点统计

### 1.1 按控制器分组统计

| 控制器 | 路径前缀 | 端点数量 |
|--------|----------|----------|
| GitDiscoveryController | `/api/v1/discovery` | 10 |
| CapabilityDiscoveryController | `/api/v1/discovery/capabilities` | 5 |
| SceneGroupController | `/api/v1/scene-groups` | 25 |
| SceneController | `/api/scenes` | 15 |
| OrgController | `/api/v1/org` | 18 |
| LlmController | `/api/llm` | 10 |
| LlmProviderController | `/api/v1/llm` | 7 |
| LlmMonitorController | `/api/v1/llm/monitor` | 5 |
| LlmScriptController | `/api/v1/llm` | 8 |
| KnowledgeBaseController | `/api/v1/knowledge-bases` | 6 |
| SceneKnowledgeController | `/api/v1/scene-groups/{sceneGroupId}/knowledge` | 5 |
| SceneLlmController | `/api/v1/scene-groups/{sceneGroupId}/llm` | 4 |
| SceneTemplateController | `/api/v1/templates` | 9 |
| SceneSkillLifecycleController | `/api/v1/scene-capabilities` | 6 |
| InstallController | `/api/v1/installs` | 12 |
| RoleManagementController | `/api/v1/role-management` | 18 |
| AuthController | `/api/v1/auth` | 8 |
| TodoController | `/api/v1/my/todos` | 5 |
| HistoryController | `/api/v1/my/history` | 4 |
| DictController | `/api/v1/dicts` | 6 |
| SelectorController | `/api/v1/selectors` | 6 |
| NetworkController | `/api/network` | 8 |
| SystemConfigController | `/api/v1/system` | 3 |
| ConfigVersionController | `/api/v1/config/versions` | 7 |
| CapabilityStatsController | `/api/v1/capabilities/stats` | 5 |
| AuditController | `/api/v1/audit` | 1 |
| InstallerController | `/api/v1/installer` | 3 |
| ReportSkillController | `/api/v1/skills` | 10 |
| DailyReportSkill | `/api/skills/daily-report` | 6 |
| ExecutionLogController | `/api/v1/scene-groups` | 2 |

**总计**: 约 230+ 个 API 端点

---

## 二、菜单配置统计

### 2.1 一级菜单

| ID | 名称 | 图标 | URL | 角色 |
|----|------|------|-----|------|
| dashboard | 首页 | ri-home-line | /console/pages/scene-management.html | personal, enterprise, admin |
| scene-group-management | 场景组管理 | ri-folder-line | /console/pages/scene-group-management.html | enterprise, admin |
| my-workspace | 我的工作台 | ri-user-line | /console/pages/my-todos.html | personal, enterprise, admin |

### 2.2 二级菜单

**场景组管理**:
| ID | 名称 | URL |
|----|------|-----|
| scene-group-list | 场景组列表 | /console/pages/scene-group-management.html |
| scene-templates | 场景模板 | /console/pages/template-management.html |
| execution-history | 执行历史 | /console/pages/my-history.html |

**我的工作台**:
| ID | 名称 | URL |
|----|------|-----|
| my-todos | 我的待办 | /console/pages/my-todos.html |
| my-scenes | 我的场景 | /console/pages/my-scenes.html |
| my-history | 已完成场景 | /console/pages/my-history.html |

---

## 三、页面文件统计

### 3.1 按功能分组

**场景管理** (7 个):
| 页面 | 文件名 |
|------|--------|
| 场景管理 | scene-management.html |
| 场景详情 | scene-detail.html |
| 场景组管理 | scene-group-management.html |
| 场景组详情 | scene-group-detail.html |
| 场景能力 | scene-capabilities.html |
| 场景能力详情 | scene-capability-detail.html |
| 场景知识库 | scene-knowledge.html |

**能力管理** (6 个):
| 页面 | 文件名 |
|------|--------|
| 能力发现 | capability-discovery.html |
| 能力详情 | capability-detail.html |
| 能力管理 | capability-management.html |
| 能力创建 | capability-create.html |
| 能力绑定 | capability-binding.html |
| 能力激活 | capability-activation.html |
| 能力统计 | capability-stats.html |

**模板管理** (2 个):
| 页面 | 文件名 |
|------|--------|
| 模板管理 | template-management.html |
| 模板详情 | template-detail.html |

**LLM 相关** (3 个):
| 页面 | 文件名 |
|------|--------|
| LLM 配置 | llm-config.html |
| LLM 监控 | llm-monitor.html |

**个人中心** (6 个):
| 页面 | 文件名 |
|------|--------|
| 我的待办 | my-todos.html |
| 我的场景 | my-scenes.html |
| 我的_capabilities | my-capabilities.html |
| 我的历史 | my-history.html |
| 我的资料 | my-profile.html |

**角色相关** (6 个):
| 页面 | 文件名 |
|------|--------|
| 管理员 | role-admin.html |
| 协作者 | role-collaborator.html |
| 开发者 | role-developer.html |
| 安装者 | role-installer.html |
| 领导 | role-leader.html |
| 用户 | role-user.html |

**系统配置** (8 个):
| 页面 | 文件名 |
|------|--------|
| 系统配置 | config-system.html |
| 组织管理 | org-management.html |
| 菜单权限 | menu-auth.html |
| 审计日志 | audit-logs.html |
| 密钥管理 | key-management.html |
| 安全配置 | security-config.html |
| 驱动配置 | driver-config.html |
| 架构检查 | arch-check.html |

**其他** (6 个):
| 页面 | 文件名 |
|------|--------|
| 登录 | login.html |
| 知识库 | knowledge-base.html |
| Agent 列表 | agent-list.html |
| 地址空间 | address-space.html |
| 链接列表 | link-list.html |
| 日报表 | daily-report-form.html |

**总计**: 44 个页面文件

---

## 四、链接关系分析

### 4.1 菜单 → 页面 → API 链接

```
首页 (dashboard)
├── 页面: scene-management.html
├── API: GET /api/v1/scene-groups
├── API: GET /api/v1/templates
└── API: GET /api/v1/discovery/statistics

场景组管理 (scene-group-management)
├── 场景组列表
│   ├── 页面: scene-group-management.html
│   ├── API: GET /api/v1/scene-groups
│   ├── API: POST /api/v1/scene-groups
│   └── API: DELETE /api/v1/scene-groups/{id}
├── 场景模板
│   ├── 页面: template-management.html
│   ├── API: GET /api/v1/templates
│   └── API: POST /api/v1/templates/{id}/deploy
└── 执行历史
    ├── 页面: my-history.html
    └── API: GET /api/v1/my/history/scenes

我的工作台 (my-workspace)
├── 我的待办
│   ├── 页面: my-todos.html
│   └── API: GET /api/v1/my/todos
├── 我的场景
│   ├── 页面: my-scenes.html
│   └── API: GET /api/v1/scene-groups/my/created
└── 已完成场景
    ├── 页面: my-history.html
    └── API: GET /api/v1/my/history/scenes
```

### 4.2 能力发现页面链接

```
能力发现 (capability-discovery.html)
├── API: POST /api/v1/discovery/local
├── API: POST /api/v1/discovery/github
├── API: POST /api/v1/discovery/gitee
├── API: POST /api/v1/discovery/install
└── 跳转: capability-detail.html?id={capabilityId}

能力详情 (capability-detail.html)
├── API: GET /api/v1/discovery/capabilities/detail/{id}
├── API: GET /api/v1/discovery/capabilities/detail/{id}/driver-conditions
└── 跳转: capability-binding.html?id={capabilityId}

能力绑定 (capability-binding.html)
├── API: POST /api/v1/scene-groups/{sceneGroupId}/capabilities
└── 跳转: capability-activation.html?installId={installId}

能力激活 (capability-activation.html)
├── API: GET /api/v1/installs/{installId}
├── API: POST /api/v1/installs/{installId}/execute
└── 跳转: scene-detail.html?sceneId={sceneId}
```

### 4.3 场景管理页面链接

```
场景组详情 (scene-group-detail.html)
├── API: GET /api/v1/scene-groups/{id}
├── API: PUT /api/v1/scene-groups/{id}
├── API: POST /api/v1/scene-groups/{id}/activate
├── API: GET /api/v1/scene-groups/{id}/capabilities
├── API: GET /api/v1/scene-groups/{id}/participants
└── 子页面:
    ├── scene-detail.html (场景详情)
    ├── scene-capabilities.html (能力列表)
    └── scene-knowledge.html (知识库)

场景详情 (scene-detail.html)
├── API: POST /api/scenes/get
├── API: POST /api/scenes/activate
├── API: POST /api/scenes/capabilities/list
└── 跳转: scene-capability-detail.html
```

### 4.4 LLM 相关页面链接

```
LLM 配置 (llm-config.html)
├── API: GET /api/v1/llm/providers
├── API: POST /api/v1/llm/providers
├── API: PUT /api/v1/llm/providers/{id}
├── API: POST /api/v1/llm/providers/{id}/test
└── API: GET /api/v1/llm/providers/{id}/models

LLM 监控 (llm-monitor.html)
├── API: GET /api/v1/llm/monitor/stats
├── API: GET /api/v1/llm/monitor/logs
├── API: GET /api/v1/llm/monitor/provider-stats
└── API: GET /api/v1/llm/monitor/engine/status
```

---

## 五、API 路径规范

### 5.1 路径层级

| 层级 | 格式 | 示例 |
|------|------|------|
| 1级 | `/api/{module}` | `/api/llm`, `/api/scenes` |
| 2级 | `/api/v1/{module}` | `/api/v1/scene-groups` |
| 3级 | `/api/v1/{module}/{id}` | `/api/v1/scene-groups/{sceneGroupId}` |
| 4级 | `/api/v1/{module}/{id}/{sub}` | `/api/v1/scene-groups/{sceneGroupId}/capabilities` |

### 5.2 HTTP 方法规范

| 方法 | 用途 | 示例 |
|------|------|------|
| GET | 查询 | GET /api/v1/scene-groups |
| POST | 创建/执行 | POST /api/v1/scene-groups |
| PUT | 更新 | PUT /api/v1/scene-groups/{id} |
| DELETE | 删除 | DELETE /api/v1/scene-groups/{id} |

---

## 六、统计汇总

| 类型 | 数量 |
|------|------|
| **API 端点** | 230+ |
| **控制器** | 30 |
| **一级菜单** | 3 |
| **二级菜单** | 6 |
| **页面文件** | 44 |
| **JavaScript 文件** | 44 |

---

## 七、问题与建议

### 7.1 发现的问题

1. **API 路径不一致**:
   - 部分使用 `/api/{module}` (如 `/api/llm`, `/api/scenes`)
   - 部分使用 `/api/v1/{module}` (如 `/api/v1/scene-groups`)

2. **菜单配置不完整**:
   - 菜单配置只有 3 个一级菜单
   - 缺少能力管理、LLM 配置、系统配置等菜单

3. **页面与菜单不匹配**:
   - 有 44 个页面，但菜单只覆盖了部分页面
   - 部分页面只能通过 URL 直接访问

### 7.2 建议

1. **统一 API 路径规范**:
   - 全部使用 `/api/v1/{module}` 格式
   - 或明确区分 v1 和无版本号的用途

2. **完善菜单配置**:
   - 添加能力管理菜单
   - 添加 LLM 配置菜单
   - 添加系统管理菜单

3. **建立页面索引**:
   - 为所有页面建立导航入口
   - 或添加搜索功能

---

**报告生成时间**: 2026-03-18  
**MVP 版本**: 2.3.1
