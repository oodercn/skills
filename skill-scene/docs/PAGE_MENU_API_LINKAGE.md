# 页面-菜单-API 关联分析报告 v2.3.1

> **分析日期**: 2026-03-16  
> **分析范围**: skill-scene 模块前端页面、菜单配置、API接口

---

## 一、整体关联概览

### 1.1 三层结构统计

| 层级 | 数量 | 说明 |
|------|:----:|------|
| **页面** | 17 | HTML页面文件 |
| **菜单项** | 24 | 菜单配置项（含子菜单） |
| **API端点** | 291 | Controller定义的API |

### 1.2 关联完整度

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        页面-菜单-API 关联矩阵                                     │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│   关联类型                  │ 数量  │ 完整度  │ 状态                           │
│   ──────────────────────────┼───────┼─────────┼────────────────────────────────│
│   页面有菜单入口            │  15   │   88%   │ ✅ 基本完整                     │
│   页面无菜单入口（孤立）     │   2   │   12%   │ ⚠️ 需要处理                     │
│   菜单项有对应页面          │  18   │   75%   │ ✅ 基本完整                     │
│   菜单项无对应页面          │   6   │   25%   │ ⚠️ 页面待开发                   │
│   页面有API支持             │  17   │  100%   │ ✅ 完整                         │
│   API有页面调用             │  180  │   62%   │ ⚠️ 部分API无前端入口            │
│   API重复定义               │  12   │   4%    │ ❌ 需要合并                     │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、页面与菜单关联分析

### 2.1 页面清单及菜单入口

| 页面文件 | 功能 | 菜单入口 | 菜单路径 | 关联状态 |
|----------|------|----------|----------|:--------:|
| `index.html` | 场景管理首页 | ✅ 场景管理 | `/console/` | ✅ |
| `scene-group-management.html` | 场景组管理 | ✅ 场景组管理 | `/console/pages/scene-group-management.html` | ✅ |
| `template-management.html` | 场景模板 | ✅ 场景模板 | `/console/pages/template-management.html` | ✅ |
| `my-scenes.html` | 我的场景 | ✅ 我的场景 | `/console/pages/my-scenes.html` | ✅ |
| `knowledge-base.html` | 知识库管理 | ✅ 知识库管理 | `/console/pages/knowledge-base.html` | ✅ |
| `llm-config.html` | LLM配置 | ✅ LLM配置 | `/console/pages/llm-config.html` | ✅ |
| `scene-management.html` | 场景管理(旧) | ❌ 无入口 | - | ⚠️ 孤立 |
| `scene/scene-group.html` | 场景组详情 | ✅ 子页面 | 从场景组管理跳转 | ✅ |
| `role-admin.html` | 管理员工作台 | ✅ 工作台(admin) | `/console/pages/role-admin.html` | ✅ |
| `role-user.html` | 用户工作台 | ✅ 工作台(user) | `/console/pages/role-user.html` | ✅ |
| `role-developer.html` | 开发者工作台 | ✅ 工作台(dev) | `/console/pages/role-developer.html` | ✅ |
| `my-todos.html` | 我的待办 | ✅ 我的待办 | `/console/pages/my-todos.html` | ✅ |
| `my-history.html` | 历史记录 | ✅ 历史记录 | `/console/pages/my-history.html` | ✅ |
| `my-capabilities.html` | 我的能力 | ✅ 我的能力 | `/console/pages/my-capabilities.html` | ✅ |
| `capability-create.html` | 创建能力 | ✅ 创建能力 | `/console/pages/capability-create.html` | ✅ |
| `org-management.html` | 组织管理 | ✅ 组织管理 | `/console/pages/org-management.html` | ✅ |
| `audit-logs.html` | 审计日志 | ✅ 审计日志 | `/console/pages/audit-logs.html` | ✅ |

### 2.2 孤立页面（无菜单入口）

| 页面文件 | 功能 | 问题 | 建议 |
|----------|------|------|------|
| `scene-management.html` | 场景管理(旧版) | 被新版替代，无菜单入口 | 删除或归档 |
| `installed-scene-capabilities.html` | 已安装能力 | 菜单配置存在但页面可能缺失 | 确认页面存在 |

### 2.3 菜单项无对应页面

| 菜单项 | 角色范围 | 状态 | 建议 |
|--------|----------|:----:|------|
| 密钥管理 | user | ⚠️ | 创建 `key-management.html` |
| 架构检查 | developer | ⚠️ | 创建 `arch-check.html` |
| 能力统计 | developer | ⚠️ | 创建 `capability-stats.html` |
| 能力发现 | admin | ⚠️ | 创建 `capability-discovery.html` |
| 系统监控 | admin | ⚠️ | 创建 `llm-monitor.html` |
| 已安装能力 | admin | ⚠️ | 确认页面存在 |

---

## 三、页面与API关联分析

### 3.1 场景组管理页面

**页面**: `scene-group-management.html`

| 功能按钮 | 调用API | 状态 |
|----------|---------|:----:|
| 创建场景组 | `POST /api/v1/scene-groups` | ✅ |
| 列表查询 | `GET /api/v1/scene-groups` | ✅ |
| 获取详情 | `GET /api/v1/scene-groups/{id}` | ✅ |
| 更新场景组 | `PUT /api/v1/scene-groups/{id}` | ✅ |
| 删除场景组 | `DELETE /api/v1/scene-groups/{id}` | ✅ |
| 激活场景组 | `POST /api/v1/scene-groups/{id}/activate` | ✅ |
| 停用场景组 | `POST /api/v1/scene-groups/{id}/deactivate` | ✅ |
| 模板列表 | `GET /api/v1/templates` | ✅ |

**缺失功能**:
- ❌ 批量操作API
- ❌ 导出功能API

### 3.2 场景组详情页面

**页面**: `scene/scene-group.html`

| 功能区域 | 调用API | 状态 |
|----------|---------|:----:|
| 基本信息 | `GET /api/v1/scene-groups/{id}` | ✅ |
| 参与者列表 | `GET /api/v1/scene-groups/{id}/participants` | ✅ |
| 添加参与者 | `POST /api/v1/scene-groups/{id}/participants` | ✅ |
| 移除参与者 | `DELETE /api/v1/scene-groups/{id}/participants/{pid}` | ✅ |
| 角色变更 | `PUT /api/v1/scene-groups/{id}/participants/{pid}/role` | ✅ |
| 能力绑定列表 | `GET /api/v1/scene-groups/{id}/capabilities` | ✅ |
| 绑定能力 | `POST /api/v1/scene-groups/{id}/capabilities` | ✅ |
| 解绑能力 | `DELETE /api/v1/scene-groups/{id}/capabilities/{bid}` | ✅ |
| 快照列表 | `GET /api/v1/scene-groups/{id}/snapshots` | ✅ |
| 创建快照 | `POST /api/v1/scene-groups/{id}/snapshots` | ✅ |
| 恢复快照 | `POST /api/v1/scene-groups/{id}/snapshots/{sid}/restore` | ✅ |

**缺失功能**:
- ❌ 知识库绑定Tab
- ❌ 执行日志Tab

### 3.3 模板管理页面

**页面**: `template-management.html`

| 功能按钮 | 调用API | 状态 |
|----------|---------|:----:|
| 模板列表 | `GET /api/v1/templates` | ✅ |
| 模板详情 | `GET /api/v1/templates/{id}` | ✅ |
| 部署模板 | `POST /api/v1/templates/{id}/deploy` | ✅ |
| 安装依赖 | `POST /api/v1/templates/{id}/install` | ✅ |
| 依赖健康检查 | `GET /api/v1/templates/{id}/dependencies/health` | ✅ |
| 缺失依赖 | `GET /api/v1/templates/{id}/dependencies/missing` | ✅ |
| 自动安装依赖 | `POST /api/v1/templates/{id}/dependencies/auto-install` | ✅ |

**缺失功能**:
- ❌ 模板创建API
- ❌ 模板删除API
- ❌ 模板更新API

### 3.4 知识库管理页面

**页面**: `knowledge-base.html`

| 功能按钮 | 调用API | 状态 |
|----------|---------|:----:|
| 知识库列表 | `GET /api/v1/knowledge-bases` | ✅ |
| 知识库详情 | `GET /api/v1/knowledge-bases/{id}` | ✅ |
| 创建知识库 | `POST /api/v1/knowledge-bases` | ✅ |
| 更新知识库 | `PUT /api/v1/knowledge-bases/{id}` | ✅ |
| 删除知识库 | `DELETE /api/v1/knowledge-bases/{id}` | ✅ |
| 重建索引 | `POST /api/v1/knowledge-bases/{id}/rebuild-index` | ✅ |

**缺失功能**:
- ❌ 文档上传API
- ❌ 文档列表API
- ❌ 搜索测试API

### 3.5 LLM配置页面

**页面**: `llm-config.html`

| 功能按钮 | 调用API | 状态 |
|----------|---------|:----:|
| 提供者列表 | `GET /api/v1/llm/providers` | ✅ |
| 提供者详情 | `GET /api/v1/llm/providers/{id}` | ✅ |
| 创建提供者 | `POST /api/v1/llm/providers` | ✅ |
| 更新提供者 | `PUT /api/v1/llm/providers/{id}` | ✅ |
| 删除提供者 | `DELETE /api/v1/llm/providers/{id}` | ✅ |
| 测试提供者 | `POST /api/v1/llm/providers/{id}/test` | ✅ |
| 模型列表 | `GET /api/v1/llm/providers/{id}/models` | ✅ |

**缺失功能**:
- ❌ 默认模型设置API
- ❌ 配额管理API

### 3.6 我的场景页面

**页面**: `my-scenes.html`

| 功能按钮 | 调用API | 状态 |
|----------|---------|:----:|
| 我创建的场景 | `GET /api/v1/scene-groups/my/created` | ✅ |
| 我参与的场景 | `GET /api/v1/scene-groups/my/participated` | ✅ |

### 3.7 我的待办页面

**页面**: `my-todos.html`

| 功能按钮 | 调用API | 状态 |
|----------|---------|:----:|
| 待办列表 | `GET /api/v1/my/todos` | ✅ |
| 待办详情 | `GET /api/v1/my/todos/{id}` | ✅ |
| 接受待办 | `POST /api/v1/my/todos/{id}/accept` | ✅ |
| 拒绝待办 | `POST /api/v1/my/todos/{id}/reject` | ✅ |
| 完成待办 | `POST /api/v1/my/todos/{id}/complete` | ✅ |
| 审批待办 | `POST /api/v1/my/todos/{id}/approve` | ✅ |

### 3.8 历史记录页面

**页面**: `my-history.html`

| 功能按钮 | 调用API | 状态 |
|----------|---------|:----:|
| 历史列表 | `GET /api/v1/my/history/scenes` | ✅ |
| 统计信息 | `GET /api/v1/my/history/statistics` | ✅ |
| 重新执行 | `POST /api/v1/my/history/{id}/rerun` | ✅ |
| 导出历史 | `GET /api/v1/my/history/export` | ✅ |

### 3.9 组织管理页面

**页面**: `org-management.html`

| 功能按钮 | 调用API | 状态 |
|----------|---------|:----:|
| 用户列表 | `GET /api/v1/org/users` | ✅ |
| 用户详情 | `GET /api/v1/org/users/{id}` | ✅ |
| 创建用户 | `POST /api/v1/org/users` | ✅ |
| 更新用户 | `PUT /api/v1/org/users/{id}` | ✅ |
| 删除用户 | `DELETE /api/v1/org/users/{id}` | ✅ |
| 部门列表 | `GET /api/v1/org/departments` | ✅ |
| 部门详情 | `GET /api/v1/org/departments/{id}` | ✅ |
| 创建部门 | `POST /api/v1/org/departments` | ✅ |
| 更新部门 | `PUT /api/v1/org/departments/{id}` | ✅ |
| 删除部门 | `DELETE /api/v1/org/departments/{id}` | ✅ |
| 组织树 | `GET /api/v1/org/tree` | ✅ |
| 角色列表 | `GET /api/v1/org/roles` | ✅ |

### 3.10 审计日志页面

**页面**: `audit-logs.html`

| 功能按钮 | 调用API | 状态 |
|----------|---------|:----:|
| 日志列表 | `GET /api/v1/audit/logs` | ✅ |
| 日志详情 | `GET /api/v1/audit/logs/{id}` | ✅ |
| 统计信息 | `GET /api/v1/audit/stats` | ✅ |
| 导出日志 | `GET /api/v1/audit/export` | ✅ |

---

## 四、API重复与冲突分析

### 4.1 严重冲突（路径完全重复）

| API路径 | Controller 1 | Controller 2 | 问题 |
|---------|--------------|--------------|------|
| `POST /api/v1/llm/execute` | LlmScriptController | LlmController | **完全重复** |
| `POST /api/v1/llm/providers` | LlmProviderController | LlmController | **完全重复** |

### 4.2 功能相似API

| 功能 | API 1 | API 2 | 建议 |
|------|-------|-------|------|
| 用户列表 | `GET /api/v1/org/users` | `GET /api/v1/role-management/users` | 合并为一个 |
| 角色列表 | `GET /api/v1/auth/roles` | `GET /api/v1/org/roles` | 合并为一个 |
| 当前用户 | `GET /api/v1/auth/session` | `GET /api/v1/auth/current-user` | 合并为一个 |
| 菜单配置 | `GET /api/v1/auth/menu-config` | `GET /api/v1/role-management/my-menus` | 明确区分用途 |
| 能力列表 | `GET /api/v1/capabilities` | `GET /api/v1/discovery/capabilities` | 明确区分用途 |
| 能力类型 | `GET /api/v1/capabilities/types` | `GET /api/v1/discovery/capabilities/types` | 合并为一个 |
| 场景组列表 | `GET /api/v1/scene-groups` | `GET /api/v1/selectors/scene-groups` | 明确区分用途 |
| 组织树 | `GET /api/v1/org/tree` | `GET /api/v1/selectors/org-tree` | 明确区分用途 |

### 4.3 HTTP方法不规范

| API | 当前方法 | 建议方法 |
|-----|----------|----------|
| `/api/scenes/get` | POST | GET |
| `/api/scenes/list` | POST | GET |
| `/api/scenes/delete` | POST | DELETE |
| `/api/scenes/state` | POST | GET |
| `/api/scenes/capabilities/list` | POST | GET |
| `/api/scenes/snapshot/list` | POST | GET |
| `/api/scenes/logs` | POST | GET |

---

## 五、技能概念与页面支持

### 5.1 核心概念页面支持矩阵

| 概念 | 列表页面 | 详情页面 | 创建页面 | 编辑页面 | API支持 |
|------|:--------:|:--------:|:--------:|:--------:|:-------:|
| **场景组** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **场景模板** | ✅ | ⚠️ | ❌ | ❌ | ⚠️ |
| **能力** | ✅ | ⚠️ | ✅ | ⚠️ | ✅ |
| **能力绑定** | ✅(嵌入) | ❌ | ✅(嵌入) | ⚠️ | ✅ |
| **参与者** | ✅(嵌入) | ❌ | ✅(嵌入) | ✅ | ✅ |
| **知识库** | ✅ | ⚠️ | ✅ | ✅ | ✅ |
| **LLM Provider** | ✅ | ⚠️ | ✅ | ✅ | ✅ |
| **快照** | ✅(嵌入) | ❌ | ✅(嵌入) | ❌ | ✅ |
| **待办事项** | ✅ | ⚠️ | ❌ | ❌ | ✅ |
| **历史记录** | ✅ | ❌ | ❌ | ❌ | ✅ |
| **审计日志** | ✅ | ✅ | ❌ | ❌ | ✅ |
| **用户** | ✅ | ⚠️ | ✅ | ✅ | ✅ |
| **部门** | ✅ | ⚠️ | ✅ | ✅ | ✅ |
| **角色** | ✅(嵌入) | ❌ | ⚠️ | ⚠️ | ✅ |

### 5.2 缺失页面功能

| 概念 | 缺失功能 | 影响 | 优先级 |
|------|----------|------|:------:|
| 场景模板 | 创建/编辑/删除 | 无法管理模板 | P1 |
| 能力 | 详情页/编辑页 | 功能不完整 | P2 |
| 知识库 | 文档管理/搜索测试 | 功能不完整 | P2 |
| 快照 | 详情页/对比功能 | 功能不完整 | P3 |
| 角色 | 独立管理页面 | 依赖其他页面 | P3 |

---

## 六、角色菜单差异

### 6.1 管理员 (admin)

| 菜单项 | 页面 | API支持 |
|--------|------|:-------:|
| 工作台 | ✅ | ✅ |
| 能力市场 | ⚠️ | ✅ |
| 已安装能力 | ⚠️ | ✅ |
| 场景管理 | ✅ | ✅ |
| 组织管理 | ✅ | ✅ |
| 系统配置 | ✅ | ✅ |
| 系统监控 | ⚠️ | ✅ |
| 审计日志 | ✅ | ✅ |

### 6.2 普通用户 (user)

| 菜单项 | 页面 | API支持 |
|--------|------|:-------:|
| 工作台 | ✅ | ✅ |
| 我的待办 | ✅ | ✅ |
| 我的场景 | ✅ | ✅ |
| 历史记录 | ✅ | ✅ |
| 密钥管理 | ⚠️ | ✅ |

### 6.3 开发者 (developer)

| 菜单项 | 页面 | API支持 |
|--------|------|:-------:|
| 工作台 | ✅ | ✅ |
| 我的能力 | ✅ | ✅ |
| 创建能力 | ✅ | ✅ |
| 架构检查 | ⚠️ | ✅ |
| 能力统计 | ⚠️ | ✅ |

---

## 七、问题汇总与建议

### 7.1 严重问题

| 问题 | 影响 | 建议 |
|------|------|------|
| API路径重复 | 运行时冲突 | 合并LLM相关Controller |
| 孤立页面存在 | 维护混乱 | 删除或归档旧版页面 |

### 7.2 中等问题

| 问题 | 影响 | 建议 |
|------|------|------|
| 菜单项无对应页面 | 功能不可用 | 开发缺失页面 |
| HTTP方法不规范 | RESTful违规 | 重构SceneController |
| 功能相似API | 维护困难 | 合并或明确区分 |

### 7.3 轻微问题

| 问题 | 影响 | 建议 |
|------|------|------|
| 详情页缺失 | 用户体验差 | 补充详情页面 |
| 子页面入口不明确 | 导航困难 | 添加面包屑导航 |

---

## 八、改进优先级

### P0 - 立即处理

1. 合并LLM相关Controller，解决API冲突
2. 删除或归档孤立页面

### P1 - 高优先级

1. 开发缺失页面（密钥管理、架构检查、能力统计）
2. 重构SceneController，规范HTTP方法

### P2 - 中优先级

1. 补充场景模板CRUD页面
2. 完善能力详情页面
3. 明确相似API用途

### P3 - 低优先级

1. 补充知识库文档管理功能
2. 完善快照对比功能
3. 添加面包屑导航

---

**文档版本**: 2.3.1  
**分析日期**: 2026-03-16
