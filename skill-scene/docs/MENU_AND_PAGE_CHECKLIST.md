# 能力菜单规划与页面检查清单 v2.3.1

> **版本**: 2.3.1  
> **创建日期**: 2026-03-16  
> **依据**: 能力库分析、技能文档、三闭环检查规范

---

## 一、能力库现有能力清单

### 1.1 系统核心能力 (SYS: 0x00-0x07)

| 地址 | 代码 | 技能ID | 页面支持 | 菜单入口 |
|------|------|--------|:--------:|:--------:|
| 0x01 | SYS_COMMON | skill-common | ❌ | ❌ 内部服务 |
| 0x02 | SYS_CAPABILITY | skill-capability | ⚠️ 部分 | 能力市场 |
| 0x03 | SYS_PROTOCOL | skill-protocol | ❌ | ❌ 内部服务 |

### 1.2 组织服务能力 (ORG: 0x08-0x0F)

| 地址 | 代码 | 技能ID | 页面支持 | 菜单入口 |
|------|------|--------|:--------:|:--------:|
| 0x08 | ORG_LOCAL | skill-org-base | ✅ | 组织管理 |
| 0x09 | ORG_DINGDING | skill-org-dingding | ❌ | 需配置入口 |
| 0x0A | ORG_FEISHU | skill-org-feishu | ❌ | 需配置入口 |
| 0x0B | ORG_WECOM | skill-org-wecom | ❌ | 需配置入口 |
| 0x0C | ORG_LDAP | skill-org-ldap | ❌ | 需配置入口 |

### 1.3 LLM能力 (LLM: 0x28-0x37)

| 地址 | 代码 | 技能ID | 页面支持 | 菜单入口 |
|------|------|--------|:--------:|:--------:|
| 0x28 | LLM_OLLAMA | skill-llm-ollama | ⚠️ | 系统配置 |
| 0x29 | LLM_OPENAI | skill-llm-openai | ⚠️ | 系统配置 |
| 0x2D | LLM_CONVERSATION | skill-llm-conversation | ✅ | LLM对话 |
| 0x2E | LLM_CONTEXT_BUILDER | skill-llm-context-builder | ❌ | ❌ 内部服务 |
| 0x33 | LLM_QIANWEN | skill-llm-qianwen | ⚠️ | 系统配置 |
| 0x34 | LLM_DEEPSEEK | skill-llm-deepseek | ⚠️ | 系统配置 |

### 1.4 知识库能力 (KNOW: 0x30-0x37)

| 地址 | 代码 | 技能ID | 页面支持 | 菜单入口 |
|------|------|--------|:--------:|:--------:|
| 0x30 | KNOW_VECTOR | skill-knowledge-base | ✅ | 知识库管理 |
| 0x33 | KNOW_RAG | skill-rag | ❌ | ❌ 内部服务 |

### 1.5 监控能力 (MON: 0x58-0x5F)

| 地址 | 代码 | 技能ID | 页面支持 | 菜单入口 |
|------|------|--------|:--------:|:--------:|
| 0x58 | MON_BASE | skill-monitor | ⚠️ | 系统监控 |
| 0x59 | MON_HEALTH | skill-health | ⚠️ | 系统监控 |
| 0x5A | MON_AGENT | skill-agent | ❌ | 需配置入口 |

### 1.6 安全能力 (SEC: 0x78-0x7F)

| 地址 | 代码 | 技能ID | 页面支持 | 菜单入口 |
|------|------|--------|:--------:|:--------:|
| 0x78 | SEC_BASE | skill-security | ❌ | 需配置入口 |
| 0x79 | SEC_ACCESS | skill-access-control | ❌ | 需配置入口 |
| 0x7A | SEC_AUDIT | skill-audit | ✅ | 审计日志 |

---

## 二、能力依赖与协作关系

### 2.1 核心依赖链

```
skill-common (基础)
    │
    ├──► skill-capability (能力管理)
    │        └──► 场景能力绑定
    │
    ├──► skill-scene-management (场景管理)
    │        ├──► 参与者管理
    │        ├──► 能力绑定
    │        └──► 知识库绑定
    │
    └──► skill-protocol (协议处理)
```

### 2.2 LLM协作链

```
LLM Provider层 (互斥选择)
    │
    ├── skill-llm-ollama (本地)
    ├── skill-llm-openai (OpenAI)
    ├── skill-llm-qianwen (通义千问)
    └── skill-llm-deepseek (DeepSeek)
           │
           ▼
    skill-llm-context-builder (上下文构建)
           │
           ▼
    skill-llm-conversation (对话服务)
```

### 2.3 知识库协作链

```
skill-knowledge-base (知识库核心)
    │
    ├── 知识库管理
    ├── 文档管理
    └── 向量存储
           │
           ▼
    skill-rag (RAG检索增强)
           │
           ▼
    场景技能 (knowledge-qa, document-assistant)
```

---

## 三、重新规划的能力菜单结构

### 3.1 管理员菜单 (admin)

```yaml
admin:
  name: 管理员
  icon: ri-admin-line
  menus:
    # 工作台
    - id: admin-dashboard
      name: 工作台
      icon: ri-home-line
      url: /console/pages/role-admin.html
      sort: 1
      
    # 能力中心 (新增分组)
    - id: capability-center
      name: 能力中心
      icon: ri-puzzle-line
      sort: 2
      children:
        - id: capability-market
          name: 能力市场
          icon: ri-store-2-line
          url: /console/pages/capability-discovery.html
        - id: installed-capabilities
          name: 已安装能力
          icon: ri-download-cloud-line
          url: /console/pages/installed-scene-capabilities.html
        - id: capability-stats
          name: 能力统计
          icon: ri-bar-chart-box-line
          url: /console/pages/capability-stats.html
          
    # 场景管理 (新增子菜单)
    - id: scene-management
      name: 场景管理
      icon: ri-folder-line
      sort: 3
      children:
        - id: scene-groups
          name: 场景组管理
          icon: ri-list-check
          url: /console/pages/scene-group-management.html
        - id: scene-templates
          name: 场景模板
          icon: ri-file-copy-line
          url: /console/pages/template-management.html
        - id: scene-activation
          name: 激活管理
          icon: ri-play-circle-line
          url: /console/pages/activation-management.html
          
    # 组织管理 (新增子菜单)
    - id: org-management
      name: 组织管理
      icon: ri-organization-chart
      sort: 4
      children:
        - id: org-users
          name: 用户管理
          icon: ri-user-line
          url: /console/pages/org-management.html
        - id: org-departments
          name: 部门管理
          icon: ri-team-line
          url: /console/pages/department-management.html
        - id: org-roles
          name: 角色权限
          icon: ri-shield-user-line
          url: /console/pages/role-management.html
          
    # 系统配置 (新增子菜单)
    - id: system-config
      name: 系统配置
      icon: ri-settings-3-line
      sort: 5
      children:
        - id: llm-config
          name: LLM配置
          icon: ri-robot-line
          url: /console/pages/llm-config.html
        - id: knowledge-config
          name: 知识库配置
          icon: ri-book-2-line
          url: /console/pages/knowledge-config.html
        - id: driver-config
          name: 驱动配置
          icon: ri-plug-line
          url: /console/pages/driver-config.html
          
    # 系统监控 (新增子菜单)
    - id: system-monitor
      name: 系统监控
      icon: ri-line-chart-line
      sort: 6
      children:
        - id: system-status
          name: 系统状态
          icon: ri-heart-pulse-line
          url: /console/pages/llm-monitor.html
        - id: health-check
          name: 健康检查
          icon: ri-heart-line
          url: /console/pages/health-check.html
        - id: agent-management
          name: Agent管理
          icon: ri-robot-2-line
          url: /console/pages/agent-management.html
          
    # 安全审计 (新增子菜单)
    - id: security-audit
      name: 安全审计
      icon: ri-shield-check-line
      sort: 7
      children:
        - id: audit-logs
          name: 审计日志
          icon: ri-file-list-3-line
          url: /console/pages/audit-logs.html
        - id: access-control
          name: 访问控制
          icon: ri-lock-line
          url: /console/pages/access-control.html
```

### 3.2 普通用户菜单

```yaml
user:
  name: 普通用户
  icon: ri-user-line
  menus:
    - id: user-dashboard
      name: 工作台
      icon: ri-home-line
      url: /console/pages/role-user.html
      sort: 1
      
    - id: my-todos
      name: 我的待办
      icon: ri-task-line
      url: /console/pages/my-todos.html
      sort: 2
      
    - id: my-scenes
      name: 我的场景
      icon: ri-artboard-line
      url: /console/pages/my-scenes.html
      sort: 3
      
    - id: my-history
      name: 历史记录
      icon: ri-history-line
      url: /console/pages/my-history.html
      sort: 4
      
    - id: key-management
      name: 密钥管理
      icon: ri-key-2-line
      url: /console/pages/key-management.html
      sort: 5
```

### 3.3 开发者菜单

```yaml
developer:
  name: 开发者
  icon: ri-code-line
  menus:
    - id: developer-dashboard
      name: 工作台
      icon: ri-home-line
      url: /console/pages/role-developer.html
      sort: 1
      
    - id: my-capabilities
      name: 我的能力
      icon: ri-puzzle-line
      url: /console/pages/my-capabilities.html
      sort: 2
      
    - id: capability-create
      name: 创建能力
      icon: ri-add-circle-line
      url: /console/pages/capability-create.html
      sort: 3
      
    - id: arch-check
      name: 架构检查
      icon: ri-shield-check-line
      url: /console/pages/arch-check.html
      sort: 4
      
    - id: capability-stats
      name: 能力统计
      icon: ri-bar-chart-box-line
      url: /console/pages/capability-stats.html
      sort: 5
```

---

## 四、页面三闭环检查清单

### 4.1 检查清单模板

| 页面 | 检查项 | 状态 | 备注 |
|------|--------|:----:|------|
| **生命周期闭环** | | | |
| | 创建API存在 | ⬜ | |
| | 查询API存在 | ⬜ | |
| | 更新API存在 | ⬜ | |
| | 删除API存在 | ⬜ | |
| | 状态转换API存在 | ⬜ | |
| **数据实体闭环** | | | |
| | 实体关系明确 | ⬜ | |
| | 级联操作处理 | ⬜ | |
| | 外键验证 | ⬜ | |
| | 数据一致性保障 | ⬜ | |
| **按钮API闭环** | | | |
| | 每个按钮有API | ⬜ | |
| | 前端正确调用 | ⬜ | |
| | 错误处理完整 | ⬜ | |
| | 操作后刷新 | ⬜ | |

### 4.2 各页面检查清单

#### 4.2.1 场景组管理页面 (scene-group-management.html)

| 检查项 | 状态 | API | 备注 |
|--------|:----:|-----|------|
| **生命周期闭环** | | | |
| 创建场景组 | ⬜ | POST /api/v1/scene-groups | |
| 列表查询 | ✅ | GET /api/v1/scene-groups | |
| 详情查询 | ✅ | GET /api/v1/scene-groups/{id} | |
| 更新场景组 | ⬜ | PUT /api/v1/scene-groups/{id} | |
| 删除场景组 | ⬜ | DELETE /api/v1/scene-groups/{id} | |
| 激活场景组 | ⬜ | POST /api/v1/scene-groups/{id}/activate | |
| 停用场景组 | ⬜ | POST /api/v1/scene-groups/{id}/deactivate | |
| **按钮API闭环** | | | |
| 创建按钮 | ⬜ | | 需检查前端调用 |
| 编辑按钮 | ⬜ | | 需检查前端调用 |
| 删除按钮 | ⬜ | | 需检查前端调用 |
| 激活按钮 | ⬜ | | 需检查前端调用 |
| 停用按钮 | ⬜ | | 需检查前端调用 |
| 刷新按钮 | ⬜ | | 需检查前端调用 |
| 筛选功能 | ⬜ | | 需检查前端调用 |

#### 4.2.2 场景组详情页面

| 检查项 | 状态 | API | 备注 |
|--------|:----:|-----|------|
| **参与者管理** | | | |
| 参与者列表 | ✅ | GET /api/v1/scene-groups/{id}/participants | |
| 添加参与者 | ⬜ | POST /api/v1/scene-groups/{id}/participants | |
| 移除参与者 | ⬜ | DELETE /api/v1/scene-groups/{id}/participants/{pid} | |
| 角色变更 | ⬜ | PUT /api/v1/scene-groups/{id}/participants/{pid}/role | |
| **能力绑定** | | | |
| 能力列表 | ✅ | GET /api/v1/scene-groups/{id}/capabilities | |
| 绑定能力 | ⬜ | POST /api/v1/scene-groups/{id}/capabilities | |
| 解绑能力 | ⬜ | DELETE /api/v1/scene-groups/{id}/capabilities/{bid} | |
| **快照管理** | | | |
| 快照列表 | ✅ | GET /api/v1/scene-groups/{id}/snapshots | |
| 创建快照 | ⬜ | POST /api/v1/scene-groups/{id}/snapshots | |
| 恢复快照 | ⬜ | POST /api/v1/scene-groups/{id}/snapshots/{sid}/restore | |
| 删除快照 | ⬜ | DELETE /api/v1/scene-groups/{id}/snapshots/{sid} | |

#### 4.2.3 模板管理页面

| 检查项 | 状态 | API | 备注 |
|--------|:----:|-----|------|
| **生命周期闭环** | | | |
| 模板列表 | ✅ | GET /api/v1/templates | |
| 模板详情 | ✅ | GET /api/v1/templates/{id} | |
| 创建模板 | ❌ | 无 | 需新增API |
| 更新模板 | ❌ | 无 | 需新增API |
| 删除模板 | ❌ | 无 | 需新增API |
| **部署功能** | | | |
| 部署模板 | ✅ | POST /api/v1/templates/{id}/deploy | |
| SSE流式部署 | ✅ | GET /api/v1/templates/{id}/deploy/stream | |
| **依赖管理** | | | |
| 依赖健康检查 | ✅ | GET /api/v1/templates/{id}/dependencies/health | |
| 缺失依赖 | ✅ | GET /api/v1/templates/{id}/dependencies/missing | |
| 自动安装依赖 | ✅ | POST /api/v1/templates/{id}/dependencies/auto-install | |

#### 4.2.4 知识库管理页面

| 检查项 | 状态 | API | 备注 |
|--------|:----:|-----|------|
| **生命周期闭环** | | | |
| 知识库列表 | ✅ | GET /api/v1/knowledge-bases | |
| 知识库详情 | ✅ | GET /api/v1/knowledge-bases/{id} | |
| 创建知识库 | ⬜ | POST /api/v1/knowledge-bases | |
| 更新知识库 | ⬜ | PUT /api/v1/knowledge-bases/{id} | |
| 删除知识库 | ⬜ | DELETE /api/v1/knowledge-bases/{id} | |
| 重建索引 | ⬜ | POST /api/v1/knowledge-bases/{id}/rebuild-index | |
| **缺失功能** | | | |
| 文档上传 | ❌ | 无 | 需新增API |
| 文档列表 | ❌ | 无 | 需新增API |
| 搜索测试 | ❌ | 无 | 需新增API |

#### 4.2.5 LLM配置页面

| 检查项 | 状态 | API | 备注 |
|--------|:----:|-----|------|
| **Provider管理** | | | |
| Provider列表 | ✅ | GET /api/v1/llm/providers | |
| Provider详情 | ✅ | GET /api/v1/llm/providers/{id} | |
| 创建Provider | ⬜ | POST /api/v1/llm/providers | |
| 更新Provider | ⬜ | PUT /api/v1/llm/providers/{id} | |
| 删除Provider | ⬜ | DELETE /api/v1/llm/providers/{id} | |
| 测试Provider | ⬜ | POST /api/v1/llm/providers/{id}/test | |
| 模型列表 | ✅ | GET /api/v1/llm/providers/{id}/models | |

#### 4.2.6 我的待办页面

| 检查项 | 状态 | API | 备注 |
|--------|:----:|-----|------|
| 待办列表 | ✅ | GET /api/v1/my/todos | |
| 待办详情 | ✅ | GET /api/v1/my/todos/{id} | |
| 接受待办 | ⬜ | POST /api/v1/my/todos/{id}/accept | |
| 拒绝待办 | ⬜ | POST /api/v1/my/todos/{id}/reject | |
| 完成待办 | ⬜ | POST /api/v1/my/todos/{id}/complete | |
| 审批待办 | ⬜ | POST /api/v1/my/todos/{id}/approve | |

#### 4.2.7 历史记录页面

| 检查项 | 状态 | API | 备注 |
|--------|:----:|-----|------|
| 历史列表 | ✅ | GET /api/v1/my/history/scenes | |
| 统计信息 | ✅ | GET /api/v1/my/history/statistics | |
| 重新执行 | ⬜ | POST /api/v1/my/history/{id}/rerun | |
| 导出历史 | ⬜ | GET /api/v1/my/history/export | |

#### 4.2.8 组织管理页面

| 检查项 | 状态 | API | 备注 |
|--------|:----:|-----|------|
| **用户管理** | | | |
| 用户列表 | ✅ | GET /api/v1/org/users | |
| 用户详情 | ✅ | GET /api/v1/org/users/{id} | |
| 创建用户 | ⬜ | POST /api/v1/org/users | |
| 更新用户 | ⬜ | PUT /api/v1/org/users/{id} | |
| 删除用户 | ⬜ | DELETE /api/v1/org/users/{id} | |
| **部门管理** | | | |
| 部门列表 | ✅ | GET /api/v1/org/departments | |
| 部门详情 | ✅ | GET /api/v1/org/departments/{id} | |
| 创建部门 | ⬜ | POST /api/v1/org/departments | |
| 更新部门 | ⬜ | PUT /api/v1/org/departments/{id} | |
| 删除部门 | ⬜ | DELETE /api/v1/org/departments/{id} | |
| 组织树 | ✅ | GET /api/v1/org/tree | |
| 角色列表 | ✅ | GET /api/v1/org/roles | |

#### 4.2.9 审计日志页面

| 检查项 | 状态 | API | 备注 |
|--------|:----:|-----|------|
| 日志列表 | ✅ | GET /api/v1/audit/logs | |
| 日志详情 | ✅ | GET /api/v1/audit/logs/{id} | |
| 统计信息 | ✅ | GET /api/v1/audit/stats | |
| 导出日志 | ⬜ | GET /api/v1/audit/export | |

---

## 五、缺失页面清单

### 5.1 需要创建的页面

| 页面名称 | 菜单入口 | 角色 | 优先级 |
|----------|----------|------|:------:|
| key-management.html | 密钥管理 | user | P1 |
| arch-check.html | 架构检查 | developer | P1 |
| capability-stats.html | 能力统计 | developer | P1 |
| capability-discovery.html | 能力市场 | admin | P1 |
| llm-monitor.html | 系统监控 | admin | P1 |
| activation-management.html | 激活管理 | admin | P2 |
| department-management.html | 部门管理 | admin | P2 |
| role-management.html | 角色权限 | admin | P2 |
| knowledge-config.html | 知识库配置 | admin | P2 |
| driver-config.html | 驱动配置 | admin | P2 |
| health-check.html | 健康检查 | admin | P2 |
| agent-management.html | Agent管理 | admin | P2 |
| access-control.html | 访问控制 | admin | P3 |

### 5.2 需要删除的页面

| 页面名称 | 原因 |
|----------|------|
| scene-management.html | 被scene-group-management.html替代 |

---

## 六、API缺失清单

### 6.1 模板管理API

| API | 方法 | 说明 |
|-----|------|------|
| POST /api/v1/templates | POST | 创建模板 |
| PUT /api/v1/templates/{id} | PUT | 更新模板 |
| DELETE /api/v1/templates/{id} | DELETE | 删除模板 |

### 6.2 知识库管理API

| API | 方法 | 说明 |
|-----|------|------|
| POST /api/v1/knowledge-bases/{id}/documents | POST | 上传文档 |
| GET /api/v1/knowledge-bases/{id}/documents | GET | 文档列表 |
| DELETE /api/v1/knowledge-bases/{id}/documents/{docId} | DELETE | 删除文档 |
| POST /api/v1/knowledge-bases/{id}/search | POST | 搜索测试 |

---

## 七、执行计划

### Phase 1: 菜单配置更新 (1天)

- [ ] 更新 menu-role-config.json
- [ ] 更新 menu-config.json
- [ ] 验证菜单加载

### Phase 2: 缺失页面开发 (5天)

- [ ] 创建 key-management.html
- [ ] 创建 arch-check.html
- [ ] 创建 capability-stats.html
- [ ] 创建 capability-discovery.html
- [ ] 创建 llm-monitor.html

### Phase 3: 页面闭环检查 (3天)

- [ ] 检查所有页面按钮事件
- [ ] 验证API调用正确性
- [ ] 完善错误处理
- [ ] 添加操作后刷新

### Phase 4: API补充 (2天)

- [ ] 补充模板管理API
- [ ] 补充知识库文档API
- [ ] 解决API冲突问题

---

**文档版本**: 2.3.1  
**创建日期**: 2026-03-16
