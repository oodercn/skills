# MVP 页面完整度检查报告

## 一、检查范围

| 检查项 | 说明 |
|--------|------|
| 页面文件 | 44 个 HTML 文件 |
| JavaScript 文件 | 44 个 JS 文件 |
| API 调用 | 100+ 个 API 端点 |
| 菜单配置 | 3 个一级菜单，6 个二级菜单 |

---

## 二、API 路径问题

### 2.1 路径不一致问题

| 页面 | 当前路径 | 建议路径 | 问题 |
|------|----------|----------|------|
| scene-management.js | `/api/scenes/list` | `/api/v1/scenes/list` | 缺少 v1 前缀 |
| scene-management.js | `/api/scenes/create` | `/api/v1/scenes/create` | 缺少 v1 前缀 |
| scene-management.js | `/api/scenes/delete` | `/api/v1/scenes/delete` | 缺少 v1 前缀 |
| scene-detail.js | `/api/scenes/get` | `/api/v1/scenes/get` | 缺少 v1 前缀 |
| scene-detail.js | `/api/scenes/activate` | `/api/v1/scenes/activate` | 缺少 v1 前缀 |
| scene-detail.js | `/api/scenes/capabilities/list` | `/api/v1/scenes/capabilities/list` | 缺少 v1 前缀 |
| capability-binding.js | `/api/selector/scene-groups` | `/api/v1/selectors/scene-groups` | 路径错误 |
| dashboard.js | `/api/dashboard/stats` | `/api/v1/dashboard/stats` | 缺少 v1 前缀 |
| security-config.js | `/api/security/config` | `/api/v1/security/config` | 缺少 v1 前缀 |

### 2.2 可能的空伪 API

| 页面 | API 路径 | 状态 | 问题 |
|------|----------|------|------|
| dashboard.js | `/api/dashboard/stats` | ⚠️ 待验证 | 后端可能未实现 |
| dashboard.js | `/api/dashboard/execution-stats` | ⚠️ 待验证 | 后端可能未实现 |
| dashboard.js | `/api/dashboard/market-stats` | ⚠️ 待验证 | 后端可能未实现 |
| dashboard.js | `/api/dashboard/system-stats` | ⚠️ 待验证 | 后端可能未实现 |
| security-config.js | `/api/security/config` | ⚠️ 待验证 | 后端可能未实现 |
| security-config.js | `/api/security/policies` | ⚠️ 待验证 | 后端可能未实现 |
| security-config.js | `/api/security/stats` | ⚠️ 待验证 | 后端可能未实现 |
| address-space.js | `/api/v1/config/addresses` | ⚠️ 待验证 | 后端可能未实现 |
| capability-binding.js | `/api/selector/scene-groups` | ❌ 错误路径 | 应为 `/api/v1/selectors/scene-groups` |

---

## 三、页面分级检查

### 3.1 一级页面（首页）

| 页面 | 文件 | API 调用 | 状态 |
|------|------|----------|------|
| 场景管理（首页） | scene-management.html | `/api/scenes/list` | ⚠️ API 路径不规范 |

**问题**：
1. 使用 `/api/scenes/list` 而非 `/api/v1/scenes/list`
2. 创建场景、删除场景 API 路径也不规范

### 3.2 能力中心页面

| 页面 | 文件 | API 调用 | 状态 |
|------|------|----------|------|
| 能力发现 | capability-discovery.html | `/api/v1/discovery/local` | ✅ 正常 |
| 能力详情 | capability-detail.html | `/api/v1/capabilities/{id}` | ✅ 正常 |
| 能力绑定 | capability-binding.html | `/api/v1/capabilities/bindings` | ⚠️ 有错误路径 |
| 能力激活 | capability-activation.html | `/api/v1/installs/{id}` | ✅ 正常 |
| 能力管理 | capability-management.html | `/api/v1/capabilities` | ✅ 正常 |
| 能力统计 | capability-stats.html | 无 API 调用 | ⚠️ 空页面？ |
| 我的能力 | my-capabilities.html | 无 API 调用 | ⚠️ 空页面？ |

**问题**：
1. `capability-binding.js` 使用错误路径 `/api/selector/scene-groups`
2. `capability-stats.js` 和 `my-capabilities.js` 无 API 调用，可能是空页面

### 3.3 场景管理页面

| 页面 | 文件 | API 调用 | 状态 |
|------|------|----------|------|
| 场景组管理 | scene-group-management.html | `/api/v1/scene-groups` | ✅ 正常 |
| 场景组详情 | scene-group-detail.html | `/api/v1/scene-groups/{id}` | ✅ 正常 |
| 场景详情 | scene-detail.html | `/api/scenes/get` | ⚠️ API 路径不规范 |
| 场景能力 | scene-capabilities.html | 无 API 调用 | ⚠️ 空页面？ |
| 场景能力详情 | scene-capability-detail.html | `/api/v1/scene-capabilities/{id}` | ✅ 正常 |
| 场景知识库 | scene-knowledge.html | `/api/v1/knowledge-bases` | ✅ 正常 |
| 模板管理 | template-management.html | `/api/v1/scene-templates` | ⚠️ API 路径可能错误 |
| 模板详情 | template-detail.html | `/api/v1/scene-templates/{id}` | ⚠️ API 路径可能错误 |

**问题**：
1. `scene-detail.html` 使用不规范路径
2. `scene-capabilities.html` 无 API 调用
3. 模板相关 API 路径需要验证后端是否存在

### 3.4 LLM 服务页面

| 页面 | 文件 | API 调用 | 状态 |
|------|------|----------|------|
| LLM 配置 | llm-config.html | 未检查 | ⚠️ 待验证 |
| LLM 监控 | llm-monitor.html | 未检查 | ⚠️ 待验证 |

### 3.5 我的工作台页面

| 页面 | 文件 | API 调用 | 状态 |
|------|------|----------|------|
| 我的待办 | my-todos.html | `/api/v1/my/todos` | ✅ 正常 |
| 我的场景 | my-scenes.html | `/api/v1/scene-groups/my/created` | ✅ 正常 |
| 我的历史 | my-history.html | `/api/v1/my/history/scenes` | ✅ 正常 |

### 3.6 系统管理页面

| 页面 | 文件 | API 调用 | 状态 |
|------|------|----------|------|
| 组织管理 | org-management.html | `/api/v1/org/users` | ✅ 正常 |
| 角色权限 | role-admin.html | 未检查 | ⚠️ 待验证 |
| 菜单权限 | menu-auth.html | 未检查 | ⚠️ 待验证 |
| 系统配置 | config-system.html | 未检查 | ⚠️ 待验证 |
| 审计日志 | audit-logs.html | `/api/v1/audit/logs` | ✅ 正常 |
| 密钥管理 | key-management.html | `/api/v1/keys` | ✅ 正常 |
| 安全配置 | security-config.html | `/api/security/config` | ⚠️ API 路径不规范 |

---

## 四、页面重复检查

### 4.1 可能重复的页面

| 页面 1 | 页面 2 | 功能相似度 | 建议 |
|--------|--------|------------|------|
| my-history.html | execution-history.html | 高 | 合并为一个页面 |
| scene-capabilities.html | installed-scene-capabilities.html | 高 | 合并为一个页面 |
| capability-stats.html | dashboard.html | 中 | 检查是否有重复功能 |

### 4.2 角色页面重复

| 页面 | 用途 | 建议 |
|------|------|------|
| role-admin.html | 管理员角色 | 可能可以合并为统一的角色管理页面 |
| role-leader.html | 领导角色 | |
| role-developer.html | 开发者角色 | |
| role-collaborator.html | 协作者角色 | |
| role-installer.html | 安装者角色 | |
| role-user.html | 用户角色 | |

---

## 五、链接有效性检查

### 5.1 菜单链接

| 菜单 | 链接 | 页面存在 | 状态 |
|------|------|----------|------|
| 首页 | /console/pages/scene-management.html | ✅ | 正常 |
| 场景组列表 | /console/pages/scene-group-management.html | ✅ | 正常 |
| 场景模板 | /console/pages/template-management.html | ✅ | 正常 |
| 执行历史 | /console/pages/my-history.html | ✅ | 正常 |
| 我的待办 | /console/pages/my-todos.html | ✅ | 正常 |
| 我的场景 | /console/pages/my-scenes.html | ✅ | 正常 |
| 已完成场景 | /console/pages/my-history.html | ✅ | 正常 |

### 5.2 缺失菜单入口的页面

以下页面存在但无菜单入口：

| 页面 | 建议菜单位置 |
|------|--------------|
| capability-discovery.html | 能力中心 |
| capability-detail.html | 能力中心（跳转） |
| capability-binding.html | 能力中心（跳转） |
| capability-activation.html | 能力中心（跳转） |
| capability-stats.html | 能力中心 |
| my-capabilities.html | 能力中心 |
| llm-config.html | LLM 服务 |
| llm-monitor.html | LLM 服务 |
| knowledge-base.html | 场景管理 |
| org-management.html | 系统管理 |
| role-admin.html | 系统管理 |
| menu-auth.html | 系统管理 |
| config-system.html | 系统管理 |
| audit-logs.html | 系统管理 |
| key-management.html | 系统管理 |
| security-config.html | 系统管理 |

---

## 六、问题汇总

### 6.1 高优先级问题

| 问题 | 影响 | 数量 |
|------|------|------|
| API 路径不规范 | 前端调用可能失败 | 15+ |
| 页面无菜单入口 | 用户无法访问 | 16 |
| 空页面/无 API 调用 | 功能不完整 | 5+ |

### 6.2 中优先级问题

| 问题 | 影响 | 数量 |
|------|------|------|
| 可能的空伪 API | 功能不可用 | 9 |
| 页面功能重复 | 维护困难 | 8 |

### 6.3 低优先级问题

| 问题 | 影响 | 数量 |
|------|------|------|
| 角色页面过多 | 代码冗余 | 6 |

---

## 七、修复建议

### 7.1 立即修复

1. **统一 API 路径**
   - 将所有 `/api/scenes/*` 改为 `/api/v1/scenes/*`
   - 将所有 `/api/dashboard/*` 改为 `/api/v1/dashboard/*`
   - 将所有 `/api/security/*` 改为 `/api/v1/security/*`
   - 修复错误路径 `/api/selector/` → `/api/v1/selectors/`

2. **完善菜单配置**
   - 添加能力中心菜单
   - 添加 LLM 服务菜单
   - 添加系统管理菜单

### 7.2 后续优化

1. **验证空伪 API**
   - 检查后端是否实现了 dashboard 相关 API
   - 检查后端是否实现了 security 相关 API

2. **合并重复页面**
   - 合并角色相关页面
   - 合并历史记录相关页面

---

## 八、检查结果统计

| 检查项 | 总数 | 正常 | 问题 | 问题率 |
|--------|------|------|------|--------|
| 页面文件 | 44 | 28 | 16 | 36% |
| API 调用 | 100+ | 75+ | 25+ | 25% |
| 菜单链接 | 7 | 7 | 0 | 0% |
| 菜单入口覆盖 | 44 | 7 | 37 | 84% |

---

**报告生成时间**: 2026-03-18  
**MVP 版本**: 2.3.1  
**检查状态**: 初步完成，需要进一步验证
