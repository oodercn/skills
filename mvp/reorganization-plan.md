# MVP 整理建议方案

## 一、问题总结

| 问题类型 | 当前状态 | 影响 |
|----------|----------|------|
| API 路径不一致 | 混用 `/api/` 和 `/api/v1/` | 前端调用混乱 |
| 菜单配置不完整 | 只有 3 个一级菜单 | 44 个页面大部分无入口 |
| 页面无导航 | 部分页面只能通过 URL 访问 | 用户体验差 |

---

## 二、整理方案

### 方案 A：完善菜单配置（推荐）

**目标**：为所有主要功能页面添加菜单入口

**新增菜单结构**：

```
首页
├── 概览 (scene-management.html)

能力中心
├── 能力发现 (capability-discovery.html)
├── 我的能力 (my-capabilities.html)
├── 能力绑定 (capability-binding.html)
├── 能力激活 (capability-activation.html)
└── 能力统计 (capability-stats.html)

场景管理
├── 场景组列表 (scene-group-management.html)
├── 场景模板 (template-management.html)
├── 执行历史 (my-history.html)
└── 知识库 (knowledge-base.html)

LLM 服务
├── LLM 配置 (llm-config.html)
├── LLM 监控 (llm-monitor.html)
└── 模型管理 (llm-config.html#models)

我的工作台
├── 我的待办 (my-todos.html)
├── 我的场景 (my-scenes.html)
└── 我的历史 (my-history.html)

系统管理 (admin)
├── 组织管理 (org-management.html)
├── 角色权限 (role-admin.html)
├── 菜单权限 (menu-auth.html)
├── 系统配置 (config-system.html)
├── 审计日志 (audit-logs.html)
└── 密钥管理 (key-management.html)
```

**工作量**：中等
**风险**：低
**收益**：高

---

### 方案 B：统一 API 路径规范

**目标**：统一所有 API 使用 `/api/v1/` 前缀

**需要修改的控制器**：

| 控制器 | 当前路径 | 目标路径 |
|--------|----------|----------|
| LlmController | `/api/llm` | `/api/v1/llm` |
| SceneController | `/api/scenes` | `/api/v1/scenes` |
| NetworkController | `/api/network` | `/api/v1/network` |
| DailyReportSkill | `/api/skills/daily-report` | `/api/v1/skills/daily-report` |

**工作量**：大
**风险**：中（需要同步修改前端调用）
**收益**：中

---

### 方案 C：页面分级整理

**目标**：将页面按使用频率和重要性分级

**一级页面（核心功能，必须有菜单入口）**：
- scene-management.html（首页）
- capability-discovery.html（能力发现）
- scene-group-management.html（场景组管理）
- llm-config.html（LLM 配置）

**二级页面（常用功能，通过一级页面跳转）**：
- scene-detail.html
- capability-detail.html
- template-management.html
- my-todos.html

**三级页面（辅助功能，通过二级页面跳转）**：
- capability-binding.html
- capability-activation.html
- scene-capability-detail.html

**四级页面（管理功能，仅管理员可见）**：
- org-management.html
- role-admin.html
- audit-logs.html
- config-system.html

---

## 三、推荐执行顺序

### 第一阶段：完善菜单配置（优先级：高）

1. 更新 `menu-config.json`，添加完整菜单结构
2. 添加角色权限控制
3. 测试菜单导航

### 第二阶段：页面入口整理（优先级：中）

1. 确保所有一级页面有菜单入口
2. 检查二级、三级页面的跳转链接
3. 添加面包屑导航

### 第三阶段：API 路径统一（优先级：低）

1. 创建 API 路径映射表
2. 逐步迁移旧路径
3. 添加废弃警告

---

## 四、具体实施步骤

### 步骤 1：更新菜单配置

修改 `menu-config.json`：

```json
{
  "menu": [
    {
      "id": "dashboard",
      "name": "首页",
      "icon": "ri-home-line",
      "url": "/console/pages/scene-management.html",
      "roles": ["personal", "enterprise", "admin"]
    },
    {
      "id": "capability-center",
      "name": "能力中心",
      "icon": "ri-puzzle-line",
      "roles": ["personal", "enterprise", "admin"],
      "children": [
        {
          "id": "capability-discovery",
          "name": "能力发现",
          "icon": "ri-search-line",
          "url": "/console/pages/capability-discovery.html"
        },
        {
          "id": "my-capabilities",
          "name": "我的能力",
          "icon": "ri-user-star-line",
          "url": "/console/pages/my-capabilities.html"
        },
        {
          "id": "capability-stats",
          "name": "能力统计",
          "icon": "ri-bar-chart-line",
          "url": "/console/pages/capability-stats.html"
        }
      ]
    },
    {
      "id": "scene-management",
      "name": "场景管理",
      "icon": "ri-folder-line",
      "roles": ["enterprise", "admin"],
      "children": [
        {
          "id": "scene-groups",
          "name": "场景组列表",
          "icon": "ri-list-check",
          "url": "/console/pages/scene-group-management.html"
        },
        {
          "id": "scene-templates",
          "name": "场景模板",
          "icon": "ri-file-copy-line",
          "url": "/console/pages/template-management.html"
        },
        {
          "id": "knowledge-bases",
          "name": "知识库",
          "icon": "ri-book-line",
          "url": "/console/pages/knowledge-base.html"
        }
      ]
    },
    {
      "id": "llm-service",
      "name": "LLM 服务",
      "icon": "ri-robot-line",
      "roles": ["enterprise", "admin"],
      "children": [
        {
          "id": "llm-config",
          "name": "LLM 配置",
          "icon": "ri-settings-3-line",
          "url": "/console/pages/llm-config.html"
        },
        {
          "id": "llm-monitor",
          "name": "LLM 监控",
          "icon": "ri-line-chart-line",
          "url": "/console/pages/llm-monitor.html"
        }
      ]
    },
    {
      "id": "my-workspace",
      "name": "我的工作台",
      "icon": "ri-user-line",
      "roles": ["personal", "enterprise", "admin"],
      "children": [
        {
          "id": "my-todos",
          "name": "我的待办",
          "icon": "ri-task-line",
          "url": "/console/pages/my-todos.html"
        },
        {
          "id": "my-scenes",
          "name": "我的场景",
          "icon": "ri-artboard-line",
          "url": "/console/pages/my-scenes.html"
        },
        {
          "id": "my-history",
          "name": "执行历史",
          "icon": "ri-history-line",
          "url": "/console/pages/my-history.html"
        }
      ]
    },
    {
      "id": "system-management",
      "name": "系统管理",
      "icon": "ri-settings-4-line",
      "roles": ["admin"],
      "children": [
        {
          "id": "org-management",
          "name": "组织管理",
          "icon": "ri-team-line",
          "url": "/console/pages/org-management.html"
        },
        {
          "id": "role-management",
          "name": "角色权限",
          "icon": "ri-shield-user-line",
          "url": "/console/pages/role-admin.html"
        },
        {
          "id": "menu-auth",
          "name": "菜单权限",
          "icon": "ri-menu-line",
          "url": "/console/pages/menu-auth.html"
        },
        {
          "id": "system-config",
          "name": "系统配置",
          "icon": "ri-tools-line",
          "url": "/console/pages/config-system.html"
        },
        {
          "id": "audit-logs",
          "name": "审计日志",
          "icon": "ri-file-list-line",
          "url": "/console/pages/audit-logs.html"
        }
      ]
    }
  ]
}
```

### 步骤 2：添加页面跳转关系

在相关页面中添加跳转链接：

```javascript
// capability-discovery.html → capability-detail.html
window.location.href = '/console/pages/capability-detail.html?id=' + capabilityId;

// capability-detail.html → capability-binding.html
window.location.href = '/console/pages/capability-binding.html?id=' + capabilityId;

// scene-group-management.html → scene-group-detail.html
window.location.href = '/console/pages/scene-group-detail.html?id=' + sceneGroupId;
```

### 步骤 3：添加面包屑导航

在每个页面顶部添加面包屑：

```html
<nav class="breadcrumb">
  <a href="/console/pages/scene-management.html">首页</a>
  <span>/</span>
  <a href="/console/pages/capability-discovery.html">能力发现</a>
  <span>/</span>
  <span class="current">能力详情</span>
</nav>
```

---

## 五、预期效果

| 指标 | 整理前 | 整理后 |
|------|--------|--------|
| 一级菜单 | 3 | 6 |
| 二级菜单 | 6 | 20+ |
| 页面覆盖率 | ~30% | ~90% |
| API 路径规范 | 混乱 | 统一 |

---

## 六、工作量估算

| 任务 | 预计时间 | 优先级 |
|------|----------|--------|
| 更新菜单配置 | 2 小时 | 高 |
| 添加页面跳转 | 4 小时 | 中 |
| 添加面包屑 | 3 小时 | 低 |
| API 路径统一 | 8 小时 | 低 |

**总计**：约 17 小时

---

**建议**：优先执行第一阶段（完善菜单配置），这是投入产出比最高的改进。
