# 场景技能类型归纳与初始化任务规范

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-09 |
| 所属模块 | skill-scene |
| 状态 | 规范定义 |

---

## 一、现有场景技能类型

### 1.1 场景技能分类

根据现有SE模板，场景技能分为以下类型：

| 类型 | 模板ID | 说明 | 参与者模式 |
|------|--------|------|------------|
| **知识问答型** | knowledge-qa | 基于知识库的智能问答 | 单用户 |
| **LLM工作空间型** | llm-workspace | LLM智能对话工作空间 | 单用户 |
| **日志汇报型** | daily-report | 多角色协作的日志汇报 | 多角色协作 |
| **系统监控型** | system-monitor | 系统监控和告警 | 单用户/团队 |
| **存储管理型** | storage-management | 存储资源管理 | 单用户/团队 |
| **组织集成型** | org-integration | 组织架构集成 | 单用户 |

### 1.2 现有模板配置对比

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        现有场景模板配置对比                                        │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  配置项              │ knowledge-qa │ llm-workspace │ daily-report │            │
│  ────────────────────┼──────────────┼───────────────┼──────────────┤            │
│  skills (依赖技能)    │      4       │       7       │      5       │            │
│  capabilities (能力)  │      4       │       4       │      5       │            │
│  scene.config (配置)  │      3       │       3       │      2       │            │
│  installOrder (顺序)  │      4       │       7       │      5       │            │
│  ────────────────────┼──────────────┼───────────────┼──────────────┤            │
│  activationSteps     │      ❌       │       ❌       │      ✅      │            │
│  roles (角色定义)     │      ❌       │       ❌       │      ✅      │            │
│  menus (菜单配置)     │      ❌       │       ❌       │      ✅      │            │
│  uiSkills (UI技能)    │      ❌       │       ❌       │      ✅      │            │
│  dependencies (依赖)  │      ❌       │       ❌       │      ❌      │            │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、场景技能类型详细分析

### 2.1 知识问答型 (knowledge-qa)

**场景故事**: 用户创建知识库，上传文档，进行智能问答

**参与者模式**: 单用户

**初始化任务**:

| 阶段 | 任务 | SE支持 | 说明 |
|------|------|:------:|------|
| 安装前 | 检查依赖技能 | ✅ | skill-knowledge-base, skill-knowledge-ui |
| 安装前 | 检查资源 | ✅ | CPU 500m, Memory 512Mi |
| 安装时 | 安装依赖技能 | ✅ | 按installOrder顺序安装 |
| 安装时 | 创建场景实例 | ✅ | 创建scene实例 |
| 激活时 | 创建默认知识库 | ⚠️ | 需要扩展：autoCreate配置 |
| 激活时 | 配置LLM服务 | ✅ | scene.config.llm |
| 激活时 | 注册菜单 | ❌ | 需要扩展：menus配置 |
| 激活时 | 绑定能力 | ✅ | capabilities自动绑定 |

**需要在模板中声明的配置**:

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneTemplate

metadata:
  id: knowledge-qa
  name: 知识问答场景
  category: knowledge

spec:
  # 现有配置
  skills:
    - id: skill-knowledge-base
      required: true
  
  capabilities:
    - id: kb-management
      category: service
  
  scene:
    config:
      knowledgeBase:
        autoCreate: true
  
  # 需要扩展的配置
  activationSteps:
    - stepId: create-knowledge-base
      name: 创建知识库
      required: true
      autoExecute: true
    - stepId: configure-llm
      name: 配置LLM
      required: false
    - stepId: confirm-activation
      name: 确认激活
      required: true
  
  menus:
    - id: kb-management
      name: 知识库管理
      icon: ri-book-3-line
      url: /console/pages/kb-management.html
      order: 1
    - id: document-upload
      name: 文档上传
      icon: ri-upload-line
      url: /console/pages/document-upload.html
      order: 2
    - id: qa-chat
      name: 智能问答
      icon: ri-chat-3-line
      url: /console/pages/qa-chat.html
      order: 3
```

### 2.2 LLM工作空间型 (llm-workspace)

**场景故事**: 用户使用LLM进行智能对话，管理上下文和知识增强

**参与者模式**: 单用户

**初始化任务**:

| 阶段 | 任务 | SE支持 | 说明 |
|------|------|:------:|------|
| 安装前 | 检查依赖技能 | ✅ | skill-llm-conversation, skill-llm-assistant-ui |
| 安装前 | 检查LLM服务 | ⚠️ | 需要扩展：健康检查 |
| 安装时 | 安装依赖技能 | ✅ | 按installOrder顺序安装 |
| 安装时 | 创建场景实例 | ✅ | 创建scene实例 |
| 激活时 | 配置默认Provider | ✅ | scene.config.llm.defaultProvider |
| 激活时 | 初始化上下文管理 | ⚠️ | 需要扩展：context初始化 |
| 激活时 | 注册菜单 | ❌ | 需要扩展：menus配置 |
| 激活时 | 绑定能力 | ✅ | capabilities自动绑定 |

**需要在模板中声明的配置**:

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneTemplate

metadata:
  id: llm-workspace
  name: LLM工作空间
  category: llm

spec:
  # 现有配置
  skills:
    - id: skill-llm-conversation
      required: true
  
  capabilities:
    - id: llm-chat
      category: ai
  
  scene:
    config:
      llm:
        defaultProvider: "mock"
  
  # 需要扩展的配置
  dependencies:
    required:
      - skillId: skill-llm-conversation
        healthCheck: /api/llm/health
  
  activationSteps:
    - stepId: check-llm-service
      name: 检查LLM服务
      required: true
      autoExecute: true
    - stepId: select-provider
      name: 选择LLM Provider
      required: true
    - stepId: configure-context
      name: 配置上下文
      required: false
    - stepId: confirm-activation
      name: 确认激活
      required: true
  
  menus:
    - id: llm-chat
      name: 智能对话
      icon: ri-chat-3-line
      url: /console/pages/llm-chat.html
      order: 1
    - id: context-manage
      name: 上下文管理
      icon: ri-list-check
      url: /console/pages/context-manage.html
      order: 2
    - id: llm-config
      name: LLM配置
      icon: ri-settings-3-line
      url: /console/pages/llm-config.html
      order: 3
```

### 2.3 日志汇报型 (daily-report)

**场景故事**: 领导创建日志汇报场景，邀请员工参与，员工填写日志，领导查看汇总

**参与者模式**: 多角色协作（领导、员工、HR）

**初始化任务**:

| 阶段 | 任务 | SE支持 | 说明 |
|------|------|:------:|------|
| 安装前 | 检查依赖技能 | ⚠️ | 需要扩展：dependencies配置 |
| 安装前 | 检查MQTT服务 | ⚠️ | 需要扩展：服务健康检查 |
| 安装前 | 检查邮件服务 | ⚠️ | 需要扩展：服务健康检查 |
| 安装时 | 安装依赖技能 | ✅ | 按installOrder顺序安装 |
| 安装时 | 创建场景组 | ✅ | 创建sceneGroup实例 |
| 激活时(领导) | 确认参与者 | ✅ | activationSteps |
| 激活时(领导) | 选择推送目标 | ✅ | activationSteps |
| 激活时(领导) | 配置驱动条件 | ✅ | activationSteps |
| 激活时(领导) | 获取KEY | ✅ | activationSteps |
| 激活时(领导) | 确认激活 | ✅ | activationSteps |
| 激活时(领导) | 入网动作(推送) | ✅ | activationSteps |
| 激活时(领导) | 注册领导菜单 | ❌ | 需要扩展：menus配置 |
| 激活时(员工) | 确认加入场景 | ⚠️ | 需要扩展：员工激活流程 |
| 激活时(员工) | 配置私有能力 | ⚠️ | 需要扩展：privateCapabilities |
| 激活时(员工) | 注册员工菜单 | ❌ | 需要扩展：menus配置 |

**需要在模板中声明的配置**:

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneTemplate

metadata:
  id: daily-report
  name: 日志汇报场景
  category: collaboration

spec:
  # 依赖技能配置（需要扩展）
  dependencies:
    required:
      - skillId: mqtt-push
        version: ">=1.0.0"
        autoInstall: true
        healthCheck: /api/mqtt/health
      - skillId: email-service
        version: ">=1.0.0"
        autoInstall: true
        healthCheck: /api/email/health
      - skillId: llm-service
        version: ">=1.0.0"
        autoInstall: true
    optional:
      - skillId: git-integration
        version: ">=1.0.0"
        autoInstall: false
      - skillId: calendar-integration
        version: ">=1.0.0"
        autoInstall: false
  
  # 能力配置
  capabilities:
    - id: report-remind
      name: 日志提醒
      category: notification
      autoBind: true
      dependencies: [mqtt-push]
    - id: report-submit
      name: 日志提交
      category: data-input
      autoBind: true
    - id: report-aggregate
      name: 日志汇总
      category: data-processing
      autoBind: true
    - id: report-analyze
      name: 日志分析
      category: intelligence
      autoBind: true
      dependencies: [llm-service]
  
  # 角色配置（需要扩展）
  roles:
    - name: MANAGER
      description: 场景管理者（领导）
      required: true
      minCount: 1
      maxCount: 1
    - name: EMPLOYEE
      description: 普通员工
      required: true
      minCount: 1
      maxCount: 100
    - name: HR
      description: 人力资源
      required: false
      minCount: 0
      maxCount: 10
  
  # 激活流程配置（需要扩展：按角色区分）
  activationSteps:
    MANAGER:
      - stepId: confirm-participants
        name: 确认参与者
        required: true
      - stepId: select-push-targets
        name: 选择推送目标
        required: true
      - stepId: config-conditions
        name: 配置驱动条件
        required: true
      - stepId: get-key
        name: 获取KEY
        required: false
        skippable: true
      - stepId: confirm-activation
        name: 确认激活
        required: true
      - stepId: network-actions
        name: 入网动作
        required: true
        actions:
          - type: push-notification
            target: selected-users
          - type: create-todo
            target: selected-users
    EMPLOYEE:
      - stepId: confirm-join
        name: 确认加入场景
        required: true
      - stepId: config-private-capabilities
        name: 配置私有能力
        required: false
        skippable: true
        privateCapabilities:
          - email-skill
          - git-skill
          - calendar-skill
      - stepId: confirm-activation
        name: 确认激活
        required: true
  
  # 菜单配置（需要扩展：按角色区分）
  menus:
    MANAGER:
      - id: write-log
        name: 填写日志
        icon: ri-edit-line
        url: /console/pages/daily-report-form.html
        order: 1
      - id: history-query
        name: 历史查询
        icon: ri-history-line
        url: /console/pages/daily-report-history.html
        order: 2
      - id: project-tracking
        name: 项目跟踪
        icon: ri-line-chart-line
        url: /console/pages/daily-report-tracking.html
        order: 3
      - id: team-logs
        name: 团队日志
        icon: ri-team-line
        url: /console/pages/daily-report-team.html
        order: 4
    EMPLOYEE:
      - id: log-reminder
        name: 日志提醒
        icon: ri-notification-3-line
        url: /console/pages/daily-report-reminder.html
        order: 1
      - id: write-log
        name: 填写日志
        icon: ri-edit-line
        url: /console/pages/daily-report-form.html
        order: 2
      - id: history-report
        name: 历史汇报
        icon: ri-history-line
        url: /console/pages/daily-report-history.html
        order: 3
  
  # UI技能配置（需要扩展）
  uiSkills:
    - id: report-form
      name: 日志填写表单
      entryUrl: /console/pages/daily-report-form.html
      icon: ri-edit-line
      roles: [MANAGER, EMPLOYEE]
    - id: history-query
      name: 历史查询
      entryUrl: /console/pages/daily-report-history.html
      icon: ri-history-line
      roles: [MANAGER, HR]
    - id: project-tracking
      name: 项目跟踪
      entryUrl: /console/pages/daily-report-tracking.html
      icon: ri-line-chart-line
      roles: [MANAGER]
  
  # 私有能力配置（需要扩展）
  privateCapabilities:
    - id: email-skill
      name: 邮件能力
      description: 自动获取邮件内容
      optional: true
    - id: git-skill
      name: Git Skill
      description: 自动获取代码提交记录
      optional: true
```

---

## 三、SE能力支持矩阵

### 3.1 现有SE支持情况

| 功能 | SE支持 | 说明 |
|------|:------:|------|
| **安装阶段** | | |
| 依赖技能声明 | ✅ | spec.skills |
| 依赖版本约束 | ✅ | spec.skills.version |
| 必需/可选标记 | ✅ | spec.skills.required |
| 安装顺序 | ✅ | spec.installOrder |
| 能力定义 | ✅ | spec.capabilities |
| 场景配置 | ✅ | spec.scene.config |
| 资源预估 | ✅ | spec.estimatedResources |
| **激活阶段** | | |
| 激活流程定义 | ⚠️ | ActivationProcess类存在，但模板不支持 |
| 按角色区分激活步骤 | ❌ | 需要扩展 |
| 自动执行步骤 | ❌ | 需要扩展 |
| 跳过步骤 | ✅ | skipStep方法存在 |
| **菜单注册** | | |
| 菜单配置声明 | ❌ | 需要扩展模板 |
| 按角色区分菜单 | ❌ | 需要扩展模板 |
| 菜单自动注册 | ⚠️ | MenuRoleConfigService存在，但未集成 |
| **角色管理** | | |
| 角色定义 | ❌ | 需要扩展模板 |
| 角色权限配置 | ❌ | 需要扩展模板 |
| 角色激活流程 | ❌ | 需要扩展模板 |
| **依赖管理** | | |
| 依赖健康检查 | ❌ | 需要扩展 |
| 自动安装依赖 | ⚠️ | 部分支持，需要扩展 |
| 依赖服务检查 | ❌ | 需要扩展 |

### 3.2 需要扩展的功能

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        SE需要扩展的功能                                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  1. 模板扩展                                                                     │
│     ├── dependencies 配置（替代 skills，更详细的依赖声明）                         │
│     ├── roles 配置（角色定义和权限）                                              │
│     ├── activationSteps 配置（按角色区分的激活步骤）                               │
│     ├── menus 配置（按角色区分的菜单）                                            │
│     ├── uiSkills 配置（UI技能声明）                                              │
│     └── privateCapabilities 配置（私有能力）                                     │
│                                                                                 │
│  2. 服务扩展                                                                     │
│     ├── 依赖健康检查服务                                                         │
│     ├── 自动安装依赖服务                                                         │
│     ├── 菜单自动注册服务（集成MenuRoleConfigService）                              │
│     └── 员工激活流程服务                                                         │
│                                                                                 │
│  3. 流程扩展                                                                     │
│     ├── 安装前检查流程（依赖、资源、权限）                                         │
│     ├── 按角色激活流程                                                           │
│     ├── 激活完成回调（菜单注册、通知发送）                                         │
│     └── 场景销毁清理（菜单移除、能力解绑）                                         │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、场景技能模板扩展规范

### 4.1 扩展后的模板结构

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneTemplate

metadata:
  id: string                    # 模板ID
  name: string                  # 模板名称
  description: string           # 模板描述
  category: string              # 分类：knowledge, llm, collaboration, monitoring
  icon: string                  # 图标
  version: string               # 版本
  author: string                # 作者
  participantMode: string       # 参与者模式：single-user, multi-role

spec:
  # 依赖技能配置（扩展）
  dependencies:
    required:                   # 必需依赖
      - skillId: string
        version: string
        autoInstall: boolean
        healthCheck: string     # 健康检查URL
    optional:                   # 可选依赖
      - skillId: string
        version: string
        autoInstall: boolean
  
  # 能力配置
  capabilities:
    - id: string
      name: string
      description: string
      category: string          # service, ai, notification, data-processing
      autoBind: boolean
      dependencies: [string]    # 能力依赖的技能
  
  # 角色配置（新增）
  roles:
    - name: string
      description: string
      required: boolean
      minCount: int
      maxCount: int
      permissions: [string]     # 角色权限
  
  # 激活流程配置（扩展：按角色区分）
  activationSteps:
    MANAGER:                    # 领导激活步骤
      - stepId: string
        name: string
        description: string
        required: boolean
        skippable: boolean
        autoExecute: boolean    # 是否自动执行
        actions:                # 步骤执行的动作
          - type: string
            target: string
    EMPLOYEE:                   # 员工激活步骤
      - stepId: string
        name: string
        required: boolean
        privateCapabilities: [string]  # 可配置的私有能力
  
  # 菜单配置（新增：按角色区分）
  menus:
    MANAGER:
      - id: string
        name: string
        icon: string
        url: string
        order: int
        visible: boolean
    EMPLOYEE:
      - id: string
        name: string
        icon: string
        url: string
        order: int
  
  # UI技能配置（新增）
  uiSkills:
    - id: string
      name: string
      entryUrl: string
      icon: string
      roles: [string]           # 可访问的角色
      order: int
  
  # 私有能力配置（新增）
  privateCapabilities:
    - id: string
      name: string
      description: string
      optional: boolean
      skillId: string           # 依赖的技能
  
  # 场景配置
  scene:
    type: string
    name: string
    description: string
    config:
      # 场景特定配置
  
  # 安装顺序
  installOrder: [string]
  
  # 资源预估
  estimatedResources:
    cpu: string
    memory: string
    storage: string
  
  # 预估时间
  estimatedDuration: string
```

### 4.2 初始化任务自动执行规范

| 任务类型 | 执行时机 | 执行条件 | 自动/手动 |
|----------|----------|----------|-----------|
| 依赖检查 | 安装前 | always | 自动 |
| 资源检查 | 安装前 | always | 自动 |
| 权限检查 | 安装前 | always | 自动 |
| 依赖安装 | 安装时 | autoInstall=true | 自动 |
| 场景创建 | 安装时 | always | 自动 |
| 能力绑定 | 激活时 | autoBind=true | 自动 |
| 菜单注册 | 激活完成 | always | 自动 |
| 通知发送 | 激活完成 | 有推送目标 | 自动 |
| 私有能力配置 | 员工激活 | 用户选择 | 手动 |

---

## 五、总结

### 5.1 现有SE支持度

| 功能领域 | 支持度 | 说明 |
|----------|:------:|------|
| 安装阶段 | 80% | 基本支持，需要扩展依赖健康检查 |
| 激活阶段 | 50% | 支持基本流程，需要扩展按角色区分 |
| 菜单注册 | 30% | 服务存在，但未集成到模板 |
| 角色管理 | 20% | 需要扩展模板和服务 |
| 依赖管理 | 40% | 需要扩展健康检查和自动安装 |

### 5.2 优先级建议

1. **高优先级**: 菜单配置扩展、按角色区分激活步骤
2. **中优先级**: 依赖健康检查、自动安装依赖
3. **低优先级**: 私有能力配置、UI技能声明

---

**文档状态**: 规范定义  
**下一步**: 扩展SceneTemplate类和相关服务
