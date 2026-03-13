# MVP 拆分方案与前端架构迁移指南

## 一、MVP 架构概述

### 1.1 核心目标

将 skill-scene (~50MB, 180+ API) 拆分为：
- **MVP Core** (~10MB): 核心功能，最小可用产品
- **Skills Layer**: 可选技能模块，按需加载

### 1.2 架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        MVP Core (2.3.1)                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │ skill-common │  │skill-capability│  │   mvp-core  │             │
│  │  (Auth/Org)  │  │  (能力管理)   │  │  (启动入口)  │             │
│  │   地址: 0x00-0x17  │  │   地址: 0x01   │  │   端口: 8084 │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Skills Layer (可选)                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │skill-llm-base│  │skill-knowledge│  │ skill-audit │             │
│  │  (LLM驱动)   │  │  (知识库)     │  │  (审计)     │             │
│  │   地址: 0x30   │  │   地址: 0x38-0x3F│  │   地址: 0x7A │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
```

---

## 二、前端架构梳理

### 2.1 现有前端资源分布

| 模块 | 路径 | 资源类型 | 大小估算 |
|------|------|---------|---------|
| skill-scene | `skills/skill-scene/src/main/resources/static/` | 完整前端 | ~5MB |
| console | `static/console/` | 管理控制台 | ~2MB |
| pages | `static/console/pages/` | HTML页面 | ~500KB |
| js | `static/console/js/` | JavaScript | ~1MB |
| css | `static/console/css/` | 样式文件 | ~300KB |

### 2.2 前端页面清单

#### 核心页面 (MVP 必需)
| 页面 | 文件 | 功能 | 优先级 |
|------|------|------|--------|
| 安装向导 | `index.html` | 系统初始化 | P0 |
| 登录页面 | `login.html` | 用户认证 | P0 |
| 首页仪表盘 | `dashboard.html` | 系统概览 | P0 |
| 能力发现 | `capability-discovery.html` | 能力浏览 | P1 |

#### 管理页面 (可选)
| 页面 | 文件 | 功能 | 优先级 |
|------|------|------|--------|
| 用户管理 | `users.html` | 用户CRUD | P2 |
| 部门管理 | `departments.html` | 组织架构 | P2 |
| 角色管理 | `roles.html` | 角色权限 | P2 |
| 配置管理 | `config-system.html` | 系统配置 | P1 |
| 驱动配置 | `driver-config.html` | LLM驱动配置 | P1 |
| 地址空间 | `address-space.html` | 地址空间管理 | P2 |

### 2.3 JavaScript 模块

```
static/console/js/
├── components/           # 公共组件
│   ├── header.js        # 头部导航
│   ├── sidebar.js       # 侧边栏
│   └── toast.js         # 消息提示
├── pages/               # 页面脚本
│   ├── capability-discovery.js
│   ├── config-system.js
│   ├── driver-config.js
│   └── my-capabilities.js
├── utils/               # 工具函数
│   ├── api.js          # API 封装
│   ├── auth.js         # 认证工具
│   └── storage.js      # 本地存储
└── app.js               # 应用入口
```

### 2.4 CSS 样式结构

```
static/console/css/
├── base/
│   ├── reset.css       # 重置样式
│   └── variables.css   # CSS变量
├── components/
│   ├── buttons.css     # 按钮样式
│   ├── cards.css       # 卡片组件
│   ├── forms.css       # 表单样式
│   ├── modals.css      # 弹窗样式
│   ├── profile-cards.css
│   ├── stat-cards.css
│   └── capability-config.css
├── pages/
│   ├── address-space.css
│   ├── config-system.css
│   └── driver-config.css
└── main.css            # 主样式入口
```

---

## 三、最小 Nexus 架构资源

### 3.1 MVP Core 最小资源清单

```
mvp/src/main/resources/
├── static/
│   ├── index.html          # 安装首页 (已创建)
│   ├── login.html          # 登录页面 (待迁移)
│   ├── dashboard.html      # 仪表盘 (待迁移)
│   ├── css/
│   │   ├── base/
│   │   │   ├── reset.css
│   │   │   └── variables.css
│   │   ├── components/
│   │   │   ├── buttons.css
│   │   │   ├── cards.css
│   │   │   └── forms.css
│   │   └── main.css
│   └── js/
│       ├── utils/
│       │   ├── api.js
│       │   └── auth.js
│       └── app.js
├── application.yml
└── skill.yaml
```

### 3.2 依赖的 API 端点

| API | 方法 | 说明 | 页面依赖 |
|-----|------|------|---------|
| `/api/v1/auth/login` | POST | 登录 | login.html |
| `/api/v1/auth/logout` | POST | 登出 | 全局 |
| `/api/v1/auth/current-user` | GET | 当前用户 | 全局 |
| `/api/v1/auth/roles` | GET | 角色列表 | 安装向导 |
| `/api/v1/org/users` | GET | 用户列表 | dashboard |
| `/api/v1/org/departments` | GET | 部门列表 | dashboard |
| `/api/v1/system/config` | GET | 系统配置 | 全局 |
| `/api/v1/mvp/health` | GET | 健康检查 | index.html |

---

## 四、迁移任务清单

### Phase 1: 核心页面迁移 (P0)

| 任务ID | 任务描述 | 源路径 | 目标路径 | 状态 |
|--------|---------|--------|---------|------|
| MIG-001 | 创建安装首页 | - | `mvp/static/index.html` | ✅ 已完成 |
| MIG-002 | 迁移登录页面 | `skill-scene/static/login.html` | `mvp/static/login.html` | 待执行 |
| MIG-003 | 创建仪表盘 | - | `mvp/static/dashboard.html` | 待执行 |
| MIG-004 | 迁移基础CSS | `skill-scene/static/css/base/` | `mvp/static/css/base/` | 待执行 |
| MIG-005 | 迁移组件CSS | `skill-scene/static/css/components/` | `mvp/static/css/components/` | 待执行 |
| MIG-006 | 创建API工具 | - | `mvp/static/js/utils/api.js` | 待执行 |
| MIG-007 | 创建认证工具 | - | `mvp/static/js/utils/auth.js` | 待执行 |

### Phase 2: 管理功能迁移 (P1)

| 任务ID | 任务描述 | 源路径 | 目标路径 | 状态 |
|--------|---------|--------|---------|------|
| MIG-008 | 迁移能力发现页面 | `skill-scene/static/pages/capability-discovery.html` | `mvp/static/pages/capability-discovery.html` | 待执行 |
| MIG-009 | 迁移配置管理页面 | `skill-scene/static/pages/config-system.html` | `mvp/static/pages/config-system.html` | 待执行 |
| MIG-010 | 迁移驱动配置页面 | `skill-scene/static/pages/driver-config.html` | `mvp/static/pages/driver-config.html` | 待执行 |
| MIG-011 | 迁移页面JS脚本 | `skill-scene/static/js/pages/` | `mvp/static/js/pages/` | 待执行 |

### Phase 3: 高级功能迁移 (P2)

| 任务ID | 任务描述 | 源路径 | 目标路径 | 状态 |
|--------|---------|--------|---------|------|
| MIG-012 | 迁移用户管理 | `skill-scene/static/pages/users.html` | `mvp/static/pages/users.html` | 待执行 |
| MIG-013 | 迁移部门管理 | `skill-scene/static/pages/departments.html` | `mvp/static/pages/departments.html` | 待执行 |
| MIG-014 | 迁移地址空间 | `skill-scene/static/pages/address-space.html` | `mvp/static/pages/address-space.html` | 待执行 |

---

## 五、Profile 配置与前端资源映射

### 5.1 Profile 资源映射

| Profile | 前端资源 | 功能范围 |
|---------|---------|---------|
| micro | index.html, login.html, dashboard.html | 核心功能 |
| small | + capability-discovery.html | + 能力发现 |
| large | + config-system.html, driver-config.html | + 配置管理 |
| enterprise | + users.html, departments.html, address-space.html | 全功能 |

### 5.2 动态加载策略

```javascript
// 根据Profile动态加载页面
const profilePages = {
  micro: ['index', 'login', 'dashboard'],
  small: ['index', 'login', 'dashboard', 'capability-discovery'],
  large: ['index', 'login', 'dashboard', 'capability-discovery', 'config-system', 'driver-config'],
  enterprise: ['all']
};
```

---

## 六、实施时间表

| 阶段 | 任务 | 预计时间 | 负责人 |
|------|------|---------|--------|
| Week 1 | Phase 1 核心页面迁移 | 3天 | Skills Team |
| Week 2 | Phase 2 管理功能迁移 | 3天 | Skills Team |
| Week 3 | Phase 3 高级功能迁移 | 2天 | Skills Team |
| Week 4 | 测试与优化 | 2天 | All |

---

## 七、验收标准

### 7.1 功能验收
- [ ] 安装向导正常显示
- [ ] 登录功能正常
- [ ] 仪表盘数据正确
- [ ] API 端点响应正常
- [ ] 页面样式正确渲染

### 7.2 性能验收
- [ ] 首页加载时间 < 2s
- [ ] API 响应时间 < 200ms
- [ ] 内存占用 < 256MB (micro profile)

### 7.3 兼容性验收
- [ ] Chrome 最新版
- [ ] Firefox 最新版
- [ ] Edge 最新版
- [ ] Safari 最新版

---

## 八、风险与缓解措施

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| 前端资源依赖冲突 | 高 | 使用模块化加载，版本锁定 |
| API 接口变更 | 中 | 保持向后兼容，版本化API |
| 样式冲突 | 低 | 使用CSS命名空间隔离 |

---

**文档版本**: v1.0
**更新日期**: 2026-03-13
**作者**: Skills Team
