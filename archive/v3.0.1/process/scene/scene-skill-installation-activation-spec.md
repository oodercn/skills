# 场景技能安装与激活全流程规范

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-09 |
| 所属模块 | skill-scene |
| 状态 | 规范定义 |

---

## 一、问题域分析

### 1.1 核心问题

| 层次 | 问题域 | 关键问题 |
|------|--------|---------|
| **应用层** | 功能模块→用户应用 | 需要哪些个性化配置？ |
| **技术层** | 技术依赖管理 | MQTT/DB/Redis如何自动配置？ |
| **LLM层** | LLM协调与降级 | 安装时LLM如何参与？降级如何处理？ |
| **交互层** | 自动/手动任务 | 哪些自动完成？哪些用户参与？ |
| **声明层** | 开发者描述 | 如何在配置中声明这些需求？ |

### 1.2 典型应用场景

| 应用场景 | 类型 | 参与角色 | LLM需求 | 技术依赖 |
|---------|------|---------|---------|---------|
| 日志汇报 | TBS | 领导+员工 | 智能生成、分析 | MQTT、DB |
| 招聘管理 | TBS | HR+候选人+面试官 | 简历筛选、面试题生成 | MQTT、DB、邮件 |
| 绩效管理 | TBS | 领导+员工+HR | 智能评估、报告生成 | DB、通知 |
| 文档助手 | ABS | 单用户 | RAG问答 | 向量库、LLM |
| 新人培训 | ABS | 新员工+HR | 智能问答、路径生成 | 知识库、LLM |

---

## 二、应用层配置需求

### 2.1 通用应用配置模型

```yaml
ApplicationConfig:
  # 1. 基础信息配置
  basic:
    name: "日志汇报"
    description: "团队日志汇报管理"
    icon: "ri-file-list-3-line"
    category: "team-management"
    
  # 2. 角色配置
  roles:
    - name: MANAGER
      displayName: "管理者"
      description: "场景管理者，可配置和管理"
      permissions: [READ, WRITE, CONFIG, DELETE]
      minCount: 1
      maxCount: 1
    - name: EMPLOYEE
      displayName: "员工"
      description: "普通员工，提交日志"
      permissions: [READ, WRITE]
      minCount: 1
      maxCount: 100
      
  # 3. 功能配置
  features:
    - id: daily-remind
      name: "每日提醒"
      enabled: true
      configRequired: true
    - id: ai-generate
      name: "AI智能生成"
      enabled: true
      llmRequired: true
    - id: team-summary
      name: "团队汇总"
      enabled: true
      
  # 4. 菜单配置
  menus:
    MANAGER:
      - id: dashboard
        name: "管理看板"
        icon: "ri-dashboard-line"
        url: "/console/pages/daily-report-dashboard.html"
      - id: team-logs
        name: "团队日志"
        icon: "ri-team-line"
        url: "/console/pages/daily-report-team.html"
      - id: config
        name: "场景配置"
        icon: "ri-settings-3-line"
        url: "/console/pages/daily-report-config.html"
    EMPLOYEE:
      - id: my-log
        name: "我的日志"
        icon: "ri-edit-line"
        url: "/console/pages/daily-report-form.html"
      - id: history
        name: "历史记录"
        icon: "ri-history-line"
        url: "/console/pages/daily-report-history.html"
```

### 2.2 不同应用类型的配置差异

#### 日志汇报 (TBS)

```yaml
DailyReportConfig:
  # 业务配置
  business:
    reportTime: "17:00"           # 提交时间
    remindBefore: 30              # 提前提醒(分钟)
    allowLateSubmit: true         # 允许补交
    latePenalty: 0.5              # 迟交扣分
    
  # LLM配置
  llm:
    features:
      - id: ai-generate
        name: "AI生成日志"
        promptTemplate: "daily-report-generate"
        fallback: "manual-input"
      - id: ai-analyze
        name: "AI分析"
        promptTemplate: "daily-report-analyze"
        fallback: "skip"
        
  # 通知配置
  notification:
    channels: [mqtt, email]
    events:
      - name: remind
        trigger: "schedule"
        cron: "0 30 16 * * ?"
      - name: summary
        trigger: "schedule"
        cron: "0 0 9 * * ?"
```

#### 招聘管理 (TBS)

```yaml
RecruitmentConfig:
  # 业务配置
  business:
    positions: []                 # 招聘职位列表
    interviewRounds: 3            # 面试轮次
    evaluationCriteria: []        # 评估标准
    
  # LLM配置
  llm:
    features:
      - id: resume-screening
        name: "简历筛选"
        promptTemplate: "resume-screening"
        fallback: "manual-review"
      - id: interview-questions
        name: "生成面试题"
        promptTemplate: "interview-questions"
        fallback: "template-questions"
        
  # 流程配置
  workflow:
    stages:
      - name: "简历筛选"
        autoAdvance: true
        llmAssist: true
      - name: "初试"
        autoAdvance: false
        llmAssist: false
      - name: "复试"
        autoAdvance: false
        llmAssist: false
```

#### 绩效管理 (TBS)

```yaml
PerformanceConfig:
  # 业务配置
  business:
    cycle: "monthly"              # 考核周期
    dimensions: []                # 考核维度
    weights: {}                   # 权重配置
    
  # LLM配置
  llm:
    features:
      - id: smart-evaluation
        name: "智能评估"
        promptTemplate: "performance-evaluate"
        fallback: "manual-score"
      - id: report-generate
        name: "报告生成"
        promptTemplate: "performance-report"
        fallback: "template-report"
```

---

## 三、技术层依赖管理

### 3.1 技术依赖分类

```yaml
TechnicalDependencies:
  # 必需依赖 - 安装前必须满足
  required:
    - id: database
      type: infrastructure
      description: "数据存储"
      checkMethod: "connection-test"
      autoConfig: true
      
    - id: mqtt
      type: infrastructure
      description: "消息推送"
      checkMethod: "connection-test"
      autoConfig: true
      
  # 可选依赖 - 增强功能
  optional:
    - id: redis
      type: infrastructure
      description: "缓存服务"
      checkMethod: "connection-test"
      autoConfig: true
      fallback: "memory-cache"
      
    - id: vector-db
      type: infrastructure
      description: "向量数据库"
      checkMethod: "connection-test"
      autoConfig: false
      fallback: "sqlite-vector"
      
  # LLM依赖 - 智能功能
  llm:
    - id: llm-service
      type: ai-service
      description: "LLM服务"
      checkMethod: "api-test"
      autoConfig: true
      fallback: "disable-ai-features"
```

### 3.2 依赖检查与自动配置

```yaml
DependencyCheck:
  # 检查顺序
  checkOrder:
    - infrastructure    # 先检查基础设施
    - ai-service        # 再检查AI服务
    - external-api      # 最后检查外部API
    
  # 检查配置
  checkConfig:
    database:
      timeout: 5s
      retryCount: 3
      retryInterval: 1s
      autoCreate: true
      createScript: "init-db.sql"
      
    mqtt:
      timeout: 3s
      retryCount: 2
      retryInterval: 1s
      autoSubscribe: true
      topics:
        - "scene/{sceneGroupId}/notify"
        - "scene/{sceneGroupId}/command"
        
    llm-service:
      timeout: 10s
      retryCount: 2
      retryInterval: 2s
      testPrompt: "Hello, are you available?"
      expectedResponse: "Yes"
```

### 3.3 降级配置策略

```yaml
DegradationStrategy:
  # LLM降级策略
  llm:
    levels:
      - level: 0
        name: "完整功能"
        condition: "llm-available"
        features: [ai-generate, ai-analyze, ai-chat]
        
      - level: 1
        name: "基础功能"
        condition: "llm-slow"
        features: [ai-chat]
        degraded: [ai-generate, ai-analyze]
        
      - level: 2
        name: "无AI功能"
        condition: "llm-unavailable"
        features: []
        degraded: [ai-generate, ai-analyze, ai-chat]
        fallback:
          ai-generate: "manual-input"
          ai-analyze: "skip"
          ai-chat: "disable"
          
  # 基础设施降级策略
  infrastructure:
    redis:
      fallback: "memory-cache"
      impact: "性能下降，缓存失效时间缩短"
    vector-db:
      fallback: "sqlite-vector"
      impact: "向量检索性能下降"
```

---

## 四、LLM协调机制

### 4.1 安装过程中的LLM角色

```yaml
LLMRoleInInstallation:
  # LLM参与的安装阶段
  stages:
    - name: "依赖分析"
      llmAction: "analyze-dependencies"
      description: "分析技能依赖，生成安装计划"
      required: false
      fallback: "rule-based-analysis"
      
    - name: "配置推荐"
      llmAction: "recommend-config"
      description: "根据用户场景推荐配置"
      required: false
      fallback: "default-config"
      
    - name: "激活引导"
      llmAction: "guide-activation"
      description: "引导用户完成激活步骤"
      required: false
      fallback: "wizard-guide"
```

### 4.2 LLM协调流程

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        LLM协调安装流程                                            │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  Step 1: 检查LLM可用性                                                           │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  LLM可用?                                                                │    │
│  │  ├── YES → 使用LLM增强安装流程                                           │    │
│  │  └── NO  → 使用规则引擎安装流程                                          │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│                                    ▼                                            │
│  Step 2: 依赖分析                                                                │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  LLM模式:                                                                │    │
│  │  ├── 分析用户场景和需求                                                   │    │
│  │  ├── 推荐最优依赖配置                                                    │    │
│  │  └── 生成个性化安装计划                                                  │    │
│  │                                                                         │    │
│  │  规则模式:                                                               │    │
│  │  ├── 读取skill.yaml依赖声明                                              │    │
│  │  ├── 按规则检查依赖                                                      │    │
│  │  └── 生成标准安装计划                                                   │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│                                    ▼                                            │
│  Step 3: 配置生成                                                                │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  LLM模式:                                                                │    │
│  │  ├── 根据用户输入生成配置                                                │    │
│  │  ├── 智能推荐参数值                                                     │    │
│  │  └── 解释配置含义                                                       │    │
│  │                                                                         │    │
│  │  规则模式:                                                               │    │
│  │  ├── 使用默认配置模板                                                   │    │
│  │  ├── 用户手动填写参数                                                   │    │
│  │  └── 验证配置有效性                                                     │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 4.3 LLM降级处理

```yaml
LLMDegradation:
  # 降级触发条件
  triggers:
    - condition: "llm-timeout"
      threshold: 30s
      action: "fallback-to-rule"
      
    - condition: "llm-error"
      threshold: 3
      action: "fallback-to-rule"
      
    - condition: "llm-unavailable"
      action: "disable-ai-features"
      
  # 降级后的功能映射
  featureMapping:
    ai-generate:
      available: "llm"
      degraded: "template"
      unavailable: "manual"
      
    ai-analyze:
      available: "llm"
      degraded: "rule-based"
      unavailable: "skip"
      
    ai-chat:
      available: "llm"
      degraded: "keyword-match"
      unavailable: "disable"
```

---

## 五、自动任务与手动任务

### 5.1 任务分类

```yaml
TaskClassification:
  # 自动任务 - 后台自动完成
  auto:
    - id: check-dependencies
      name: "检查依赖"
      description: "自动检查技术依赖是否满足"
      trigger: "on-install-start"
      timeout: 30s
      
    - id: init-database
      name: "初始化数据库"
      description: "自动创建数据表和初始数据"
      trigger: "after-dependencies-ok"
      timeout: 60s
      
    - id: register-capabilities
      name: "注册能力"
      description: "自动注册场景能力到能力注册表"
      trigger: "after-database-ok"
      timeout: 10s
      
    - id: bind-capabilities
      name: "绑定能力"
      description: "自动绑定能力到场景组"
      trigger: "on-activation-complete"
      timeout: 10s
      
    - id: generate-menus
      name: "生成菜单"
      description: "根据角色配置自动生成菜单"
      trigger: "on-activation-complete"
      timeout: 5s
      
  # 手动任务 - 需要用户参与
  manual:
    - id: select-participants
      name: "选择参与者"
      description: "用户选择场景参与者"
      trigger: "on-activation-step"
      uiType: "user-selector"
      required: true
      
    - id: config-llm-provider
      name: "配置LLM"
      description: "用户选择或配置LLM Provider"
      trigger: "on-activation-step"
      uiType: "form"
      required: false
      skipable: true
      
    - id: authorize-external
      name: "外部授权"
      description: "用户授权访问外部服务"
      trigger: "on-activation-step"
      uiType: "oauth"
      required: false
      skipable: true
      
    - id: confirm-activation
      name: "确认激活"
      description: "用户确认激活配置"
      trigger: "on-activation-step"
      uiType: "confirm"
      required: true
```

### 5.2 任务执行流程

```yaml
TaskExecution:
  # 执行顺序
  sequence:
    # 阶段1: 安装前检查（自动）
    - phase: "pre-install"
      tasks:
        - check-dependencies
      type: "auto"
      onFail: "abort"
      
    # 阶段2: 安装（自动）
    - phase: "install"
      tasks:
        - init-database
        - register-capabilities
      type: "auto"
      onFail: "rollback"
      
    # 阶段3: 激活（混合）
    - phase: "activation"
      tasks:
        - select-participants      # 手动
        - config-llm-provider      # 手动(可跳过)
        - authorize-external       # 手动(可跳过)
        - confirm-activation       # 手动
      type: "mixed"
      onFail: "pause"
      
    # 阶段4: 激活后（自动）
    - phase: "post-activation"
      tasks:
        - bind-capabilities
        - generate-menus
      type: "auto"
      onFail: "retry"
```

---

## 六、场景配置声明规范

### 6.1 完整配置声明示例

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-daily-report
  name: 日志汇报
  version: 2.3.0
  description: 团队日志汇报管理场景
  author: ooder Team
  type: scene-skill
  category: tbs

spec:
  type: scene-skill
  
  # ==================== 依赖声明 ====================
  dependencies:
    # 必需依赖
    required:
      - id: database
        type: infrastructure
        version: ">=1.0.0"
        description: "数据存储服务"
        autoConfig: true
        checkScript: "check-db-connection"
        initScript: "init-db-tables"
        
      - id: mqtt
        type: infrastructure
        version: ">=1.0.0"
        description: "消息推送服务"
        autoConfig: true
        checkScript: "check-mqtt-connection"
        
    # 可选依赖
    optional:
      - id: redis
        type: infrastructure
        version: ">=1.0.0"
        description: "缓存服务"
        autoConfig: true
        fallback: "memory-cache"
        
      - id: email-service
        type: external-api
        version: ">=1.0.0"
        description: "邮件服务"
        autoConfig: false
        fallback: "mqtt-only"
        
    # LLM依赖
    llm:
      - id: llm-service
        type: ai-service
        version: ">=1.0.0"
        description: "LLM服务"
        autoConfig: true
        required: false
        features:
          - id: ai-generate
            name: "AI生成日志"
            promptTemplate: "daily-report-generate"
            fallback: "manual-input"
          - id: ai-analyze
            name: "AI分析"
            promptTemplate: "daily-report-analyze"
            fallback: "skip"

  # ==================== 角色配置 ====================
  roles:
    - name: MANAGER
      displayName: "管理者"
      description: "场景管理者，可配置和管理"
      permissions: [READ, WRITE, CONFIG, DELETE]
      minCount: 1
      maxCount: 1
      menuItems:
        - id: dashboard
          name: "管理看板"
          icon: "ri-dashboard-line"
          url: "/console/pages/daily-report-dashboard.html"
          order: 1
        - id: team-logs
          name: "团队日志"
          icon: "ri-team-line"
          url: "/console/pages/daily-report-team.html"
          order: 2
        - id: config
          name: "场景配置"
          icon: "ri-settings-3-line"
          url: "/console/pages/daily-report-config.html"
          order: 3
          
    - name: EMPLOYEE
      displayName: "员工"
      description: "普通员工，提交日志"
      permissions: [READ, WRITE]
      minCount: 1
      maxCount: 100
      menuItems:
        - id: my-log
          name: "我的日志"
          icon: "ri-edit-line"
          url: "/console/pages/daily-report-form.html"
          order: 1
        - id: history
          name: "历史记录"
          icon: "ri-history-line"
          url: "/console/pages/daily-report-history.html"
          order: 2

  # ==================== 激活流程配置 ====================
  activationSteps:
    MANAGER:
      - stepId: select-participants
        name: "选择参与者"
        description: "选择场景的参与者"
        type: SELECT_USERS
        required: true
        config:
          multiSelect: true
          roleFilter: [EMPLOYEE]
          
      - stepId: config-remind
        name: "配置提醒"
        description: "配置日志提醒时间和方式"
        type: CONFIG_FORM
        required: true
        config:
          fields:
            - name: remindTime
              type: time
              label: "提醒时间"
              default: "16:30"
            - name: remindChannels
              type: multi-select
              label: "提醒方式"
              options: [mqtt, email]
              default: [mqtt]
              
      - stepId: config-llm
        name: "配置AI功能"
        description: "配置LLM服务（可选）"
        type: LLM_CONFIG
        required: false
        skipable: true
        config:
          features: [ai-generate, ai-analyze]
          
      - stepId: authorize-email
        name: "授权邮件服务"
        description: "授权访问邮件服务（可选）"
        type: OAUTH
        required: false
        skipable: true
        config:
          service: email-service
          scope: [send, read]
          
      - stepId: confirm
        name: "确认激活"
        description: "确认所有配置并激活"
        type: CONFIRM
        required: true
        
      - stepId: notify-participants
        name: "通知参与者"
        description: "发送激活通知给参与者"
        type: AUTO
        required: true
        config:
          channels: [mqtt, email]
          template: "scene-activation-invite"
          
    EMPLOYEE:
      - stepId: accept-invite
        name: "接受邀请"
        description: "接受场景邀请"
        type: CONFIRM
        required: true
        
      - stepId: config-private
        name: "配置私有能力"
        description: "配置个人私有能力（可选）"
        type: SELECT_CAPABILITIES
        required: false
        skipable: true
        config:
          capabilities:
            - id: git-integration
              name: "Git集成"
              description: "自动获取代码提交记录"
            - id: calendar-integration
              name: "日历集成"
              description: "自动获取日程安排"
              
      - stepId: confirm
        name: "确认加入"
        description: "确认加入场景"
        type: CONFIRM
        required: true

  # ==================== 默认能力配置 ====================
  defaultCapabilities:
    # LLM能力（常驻）
    llm:
      - id: ai-assistant
        name: "AI助手"
        type: CHAT
        description: "场景内置AI助手"
        autoStart: true
        config:
          model: "gpt-4"
          systemPrompt: "daily-report-assistant"
          
    # 知识库能力（常驻）
    knowledge:
      - id: scene-kb
        name: "场景知识库"
        type: RAG
        description: "场景内置知识库"
        autoStart: true
        config:
          embeddingModel: "text-embedding-ada-002"
          chunkSize: 500
          
  # ==================== 功能配置 ====================
  features:
    - id: daily-remind
      name: "每日提醒"
      enabled: true
      llmRequired: false
      config:
        time: "${config.remindTime}"
        channels: "${config.remindChannels}"
        
    - id: ai-generate
      name: "AI生成日志"
      enabled: true
      llmRequired: true
      degradeTo: "manual-input"
      config:
        promptTemplate: "daily-report-generate"
        
    - id: ai-analyze
      name: "AI分析"
      enabled: true
      llmRequired: true
      degradeTo: "skip"
      config:
        promptTemplate: "daily-report-analyze"
        
    - id: team-summary
      name: "团队汇总"
      enabled: true
      llmRequired: false
      schedule: "0 0 9 * * ?"

  # ==================== 降级配置 ====================
  degradation:
    llm:
      strategy: "graceful"
      levels:
        - level: 0
          condition: "llm-available"
          features: [ai-generate, ai-analyze, ai-assistant]
        - level: 1
          condition: "llm-slow"
          features: [ai-assistant]
          degraded: [ai-generate, ai-analyze]
        - level: 2
          condition: "llm-unavailable"
          features: []
          degraded: [ai-generate, ai-analyze, ai-assistant]
          fallback:
            ai-generate: "manual-input"
            ai-analyze: "skip"
            ai-assistant: "disable"
            
    infrastructure:
      redis:
        fallback: "memory-cache"
      email-service:
        fallback: "mqtt-only"

  # ==================== 安装配置 ====================
  installation:
    # 安装前检查
    preCheck:
      - type: llm
        action: "ping"
        timeout: 10s
        required: false
      - type: database
        action: "connection-test"
        timeout: 5s
        required: true
      - type: mqtt
        action: "connection-test"
        timeout: 3s
        required: true
        
    # LLM辅助安装
    llmAssist:
      enabled: true
      fallback: "rule-based"
      tasks:
        - id: analyze-scene
          prompt: "分析用户场景需求，推荐最优配置"
        - id: recommend-config
          prompt: "根据用户输入推荐配置参数"
          
    # 自动配置
    autoConfig:
      database:
        enabled: true
        scripts:
          - "create-tables.sql"
          - "init-data.sql"
      mqtt:
        enabled: true
        topics:
          subscribe: ["scene/{sceneGroupId}/notify"]
          publish: ["scene/{sceneGroupId}/command"]
```

### 6.2 配置声明关键字段说明

| 字段 | 类型 | 说明 | 必需 |
|------|------|------|------|
| `dependencies.required` | List | 必需依赖，不满足则安装失败 | 是 |
| `dependencies.optional` | List | 可选依赖，不满足则降级 | 否 |
| `dependencies.llm` | List | LLM依赖声明 | 否 |
| `roles` | List | 角色定义和菜单配置 | 是 |
| `activationSteps` | Map | 按角色的激活流程配置 | 是 |
| `defaultCapabilities` | Map | 默认常驻能力配置 | 否 |
| `features` | List | 功能特性配置 | 是 |
| `degradation` | Map | 降级策略配置 | 是 |
| `installation` | Map | 安装配置 | 是 |

---

## 七、实施建议

### 7.1 第一阶段：基础框架

| 任务 | 优先级 | 说明 |
|------|--------|------|
| 实现 ActivationStep 模型 | P0 | 支持激活流程配置 |
| 实现 MenuConfig 模型 | P0 | 支持菜单配置 |
| 实现依赖检查服务 | P0 | 支持依赖自动检查 |
| 实现降级策略服务 | P1 | 支持LLM降级 |

### 7.2 第二阶段：LLM协调

| 任务 | 优先级 | 说明 |
|------|--------|------|
| 实现LLM可用性检测 | P0 | 检测LLM是否可用 |
| 实现LLM辅助安装 | P1 | 使用LLM增强安装体验 |
| 实现LLM降级处理 | P1 | LLM不可用时的降级 |

### 7.3 第三阶段：用户交互

| 任务 | 优先级 | 说明 |
|------|--------|------|
| 实现激活流程向导 | P0 | 引导用户完成激活 |
| 实现菜单生成服务 | P0 | 根据角色生成菜单 |
| 实现配置表单服务 | P1 | 动态生成配置表单 |

---

**文档状态**: 规范定义  
**下一步**: 根据规范实现相关功能
