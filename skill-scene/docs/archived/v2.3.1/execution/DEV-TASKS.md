# skill-scene 页面开发任务清单

## 一、菜单配置与页面对照表

### 1. 系统安装者 (installer)

| 菜单项 | 页面文件 | 状态 | 说明 |
|--------|----------|------|------|
| 工作台 | role-installer.html | ✅ 已完成 | 安装检查清单、进度统计 |
| 技能市场 | capability-discovery.html | ✅ 已完成 | 技能发现与安装 |
| 已安装技能 | my-capabilities.html | ✅ 已完成 | 已安装技能列表 |
| 安装日志 | audit-logs.html | ✅ 已完成 | 安装日志查看 |

### 2. 系统管理员 (admin)

| 菜单项 | 页面文件 | 状态 | 说明 |
|--------|----------|------|------|
| 工作台 | role-admin.html | ✅ 已完成 | 闭环二流程、分发任务 |
| 场景能力 | scene-capabilities.html | ✅ 已完成 | 场景能力管理 |
| 发现场景 | capability-discovery.html | ✅ 已完成 | 场景发现 |
| 场景组管理 | scene-group-management.html | ✅ 已完成 | 场景组CRUD |
| 能力统计 | capability-stats.html | ✅ 已完成 | 统计图表 |
| 组织管理 | org-management.html | ✅ 已完成 | 组织架构管理 |
| 架构检查 | arch-check.html | ✅ 已完成 | 架构规范检查 |

### 3. 主导者 (leader)

| 菜单项 | 页面文件 | 状态 | 说明 |
|--------|----------|------|------|
| 工作台 | role-leader.html | ✅ 已完成 | 激活流程、待处理场景 |
| 待激活场景 | my-todos.html | ✅ 已完成 | 待办任务列表 |
| 我的场景 | my-scenes.html | ✅ 已完成 | 参与的场景 |
| 密钥管理 | key-management.html | ✅ 已完成 | 密钥生成与管理 |

### 4. 协作者 (collaborator)

| 菜单项 | 页面文件 | 状态 | 说明 |
|--------|----------|------|------|
| 工作台 | role-collaborator.html | ✅ 已完成 | 待办任务、参与场景 |
| 我的待办 | my-todos.html | ✅ 已完成 | 待办任务列表 |
| 参与场景 | my-scenes.html | ✅ 已完成 | 参与的场景列表 |
| 历史记录 | my-history.html | ✅ 已完成 | 历史操作记录 |

---

## 二、待开发任务

### ~~高优先级~~

| 任务ID | 任务名称 | 页面文件 | 说明 | 优先级 | 状态 |
|--------|----------|----------|------|--------|------|
| ~~TASK-001~~ | ~~组织管理页面~~ | ~~org-management.html~~ | ~~组织架构管理、用户管理、角色绑定~~ | ~~高~~ | ✅ 已完成 |

### 中优先级

| 任务ID | 任务名称 | 说明 | 优先级 |
|--------|----------|------|--------|
| TASK-002 | 页面内联样式清理 | 移除role-*.html中的内联<style>，使用role-pages.css | 中 |
| TASK-003 | 页面内联脚本重构 | 使用RolePage公共模块简化页面JS | 中 |

---

## 三、任务详情

### TASK-001: 组织管理页面

**页面文件**: `org-management.html`

**功能需求**:
1. 组织架构树形展示
2. 部门CRUD操作
3. 用户列表管理
4. 角色绑定功能

**API接口**:
- `GET /api/v1/role-management/roles` - 获取角色列表
- `GET /api/v1/role-management/users` - 获取用户列表
- `POST /api/v1/role-management/users` - 创建用户
- `POST /api/v1/role-management/users/{userId}/bind-role/{roleId}` - 绑定角色

**UI组件**:
- 组织树组件 (左侧)
- 用户列表表格 (右侧)
- 用户创建/编辑弹窗
- 角色选择下拉框

**参考页面**: 
- `role-admin.html` (布局结构)
- `scene-group-management.html` (树形组件)

---

### TASK-002: 页面内联样式清理

**目标文件**:
- `role-installer.html`
- `role-admin.html`
- `role-leader.html`
- `role-collaborator.html`

**清理内容**:
- 移除页面内的 `<style>` 标签
- 确保所有样式已在 `role-pages.css` 中定义
- 验证页面显示正常

---

### TASK-003: 页面内联脚本重构

**目标文件**:
- `role-installer.html`
- `role-admin.html`
- `role-leader.html`
- `role-collaborator.html`

**重构内容**:
- 使用 `RolePage.init()` 替代重复的登录检查代码
- 使用 `RolePage.fetchApi()` 替代重复的API调用
- 使用 `RolePage.showToast()` 替代重复的提示消息

---

## 四、开发进度

| 阶段 | 任务 | 状态 | 完成日期 |
|------|------|------|----------|
| 第一阶段 | 角色页面创建 | ✅ 完成 | 2026-03-06 |
| 第二阶段 | CSS/JS抽取 | ✅ 完成 | 2026-03-06 |
| 第三阶段 | 组织管理页面 | ✅ 完成 | 2026-03-06 |
| 第四阶段 | 页面优化 | ⏳ 待开发 | - |

---

## 五、验收标准

### 组织管理页面验收标准
- [ ] 组织树正确显示
- [ ] 用户列表正确显示
- [ ] 创建用户功能正常
- [ ] 角色绑定功能正常
- [ ] 符合架构规范（脚本引用、CSS变量、图标系统）

### 页面优化验收标准
- [ ] 无内联样式
- [ ] 使用公共JS模块
- [ ] 编译无错误
- [ ] 页面显示正常
