# 场景需求规格说明书 v2.2

> **文档版本**: v2.2  
> **发布日期**: 2026-03-02  
> **适用范围**: skill-scene 模块开发  
> **文档状态**: 正式发布

---

## 一、概述

### 1.1 文档目的

本文档定义 Ooder 场景系统的完整需求规格，包括：
- 场景实体模型与关联关系
- 场景生命周期管理
- 参与者模型（User/Agent/SuperAgent）
- 能力管理机制
- 工作流引擎规范
- 安全与权限控制

### 1.2 核心概念

本文档使用的核心术语请参考 [术语表](GLOSSARY.md)，以下仅列出场景域特有术语：

| 概念 | 定义 |
|------|------|
| **场景定义** | SceneDefinition，场景的静态描述，包含能力需求、角色定义、工作流配置等元数据 |
| **场景组** | SceneGroup，场景的运行时实体，绑定具体参与者和能力实例 |
| **参与者** | Participant，场景中的活动主体，可以是用户、Agent或SuperAgent |
| **角色** | Role，参与者在场景中的职责定义，决定其可访问的能力 |
| **工作流定义** | WorkflowDefinition，场景中任务执行的流程编排定义 |

---

## 二、核心问题讨论与结论

### 2.1 场景创建与场景组的关系

**问题**：创建场景时，是否同时创建场景组？

**结论**：否，场景组在场景激活时创建。

```
创建场景 → 绑定能力 → 激活场景 → 创建场景组（如有协作场景）
```

**设计理由**：
- 场景创建是静态配置阶段
- 场景组是运行时协作实体
- 只有激活时才需要建立场景间通信

---

### 2.2 依赖安装时机

**问题**：启动场景后，创建场景组，依赖安装是在创建前还是创建后？

**结论**：依赖安装在场景组创建前。

```
Phase 1: 安装检查 → 检查/安装 Skills
Phase 2: 场景创建 → 创建 SceneDefinition
Phase 3: 场景组创建 → 创建 SceneGroupInfo
Phase 4: 场景激活 → 启动服务、建立通信
```

**设计理由**：
- 场景组需要所有 Skills 已就绪
- 避免运行时发现依赖缺失
- 保证场景组初始化成功

---

### 2.3 依赖配置位置

**问题**：核心依赖配置是在 Skills 还是在场景模板中？

**结论**：分层配置，各司其职。

| 层级 | 位置 | 职责 | 示例 |
|------|------|------|------|
| Skill 层 | skill.yaml | 技术依赖 | skill-knowledge-ui 依赖 skill-knowledge-base |
| 场景模板层 | template.yaml | 业务组合 | 知识问答场景包含 kb + rag + llm |

**配置示例**：

```yaml
# skill.yaml (Skill 层)
dependencies:
  - skillId: skill-knowledge-base
    versionRange: ">=1.0.0"
    required: true

# template.yaml (场景模板层)
skills:
  - skill-knowledge-base
  - skill-knowledge-ui
  - skill-rag  # 可选增强
```

---

## 三、用户用例

### 3.1 用例一：日志汇报场景（流程驱动型）

#### 用户故事

> **我是部门领导**，我要求下属员工每天下班前 5:00 将工作日志发给我统计。

#### 需求分析

- 领导创建日志汇报场景，定义参与者和能力需求
- 员工被动接受"日志提醒"能力
- LLM 负责到期提醒和自动汇总分析
- 员工可创建个人场景，将自身能力加入协作

#### 场景定义

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneDefinition

metadata:
  name: daily-report
  version: "1.0"
  description: 日志汇报场景，支持日志提交、提醒、汇总和分析

spec:
  type: primary
  category: business
  
  capabilities:
    - id: report-remind
      name: 日志提醒
      description: 定时提醒员工提交日志
      category: notification
      parameters:
        - name: targetUsers
          type: array
          required: true
        - name: remindTime
          type: string
          required: true
        - name: message
          type: string
          required: false
      returns:
        type: RemindResult
      permissions:
        - report:remind:send
    
    - id: report-submit
      name: 日志提交
      description: 员工提交工作日志
      category: data-input
      parameters:
        - name: userId
          type: string
          required: true
        - name: content
          type: object
          required: true
        - name: attachments
          type: array
          required: false
      returns:
        type: SubmitResult
      permissions:
        - report:submit
    
    - id: report-aggregate
      name: 日志汇总
      description: 汇总所有员工日志
      category: data-processing
      parameters:
        - name: dateRange
          type: object
          required: true
        - name: userFilter
          type: array
          required: false
      returns:
        type: AggregateResult
      permissions:
        - report:aggregate
    
    - id: report-analyze
      name: 日志分析
      description: AI分析日志内容
      category: intelligence
      parameters:
        - name: reports
          type: array
          required: true
        - name: analyzeType
          type: string
          required: false
      returns:
        type: AnalyzeResult
      permissions:
        - report:analyze
    
    - id: report-ui-form
      name: 日志填写表单
      description: 提供日志填写的UI界面
      category: ui
      parameters:
        - name: userId
          type: string
          required: true
        - name: template
          type: string
          required: false
      returns:
        type: UIComponent
      permissions:
        - report:ui:view

  roles:
    - name: manager
      description: 场景管理者（领导）
      required: true
      minCount: 1
      maxCount: 1
      capabilities:
        - report-remind
        - report-aggregate
        - report-analyze
        - report-submit
    
    - name: employee
      description: 普通员工
      required: true
      minCount: 1
      maxCount: 100
      capabilities:
        - report-submit
        - report-ui-form
    
    - name: llm-assistant
      description: LLM分析助手
      required: false
      minCount: 0
      maxCount: 5
      capabilities:
        - report-analyze
        - report-remind
    
    - name: coordinator
      description: 协调Agent
      required: false
      minCount: 0
      maxCount: 1
      capabilities:
        - report-remind
        - report-aggregate

  workflow:
    triggers:
      - type: schedule
        cron: "0 17 * * 1-5"
        action: remind-flow
      - type: schedule
        cron: "0 18 * * 1-5"
        action: aggregate-flow
    
    steps:
      - id: remind
        name: 发送提醒
        capability: report-remind
        executor: coordinator
        input:
          targetUsers: ${role.employee}
          remindTime: "17:00"
      
      - id: wait-submit
        name: 等待提交
        type: wait
        timeout: 3600000
        condition: "all_submitted OR timeout"
      
      - id: aggregate
        name: 汇总日志
        capability: report-aggregate
        executor: coordinator
        dependsOn: [wait-submit]
      
      - id: analyze
        name: AI分析
        capability: report-analyze
        executor: llm-assistant
        dependsOn: [aggregate]
      
      - id: notify-manager
        name: 通知领导
        capability: report-remind
        executor: coordinator
        dependsOn: [analyze]
```

---

### 3.2 用例二：智能家居场景（事件驱动型）

#### 用户故事

> **我是家庭主人**，我希望实现智能化的家庭安防和节能管理。

#### 需求分析

- 离家时自动开启安防模式
- 回家时自动关闭安防、开启舒适模式
- 夜间检测到异常时自动报警
- 根据室温自动调节空调

#### 场景定义

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneDefinition

metadata:
  name: smart-home
  version: "1.0"
  description: 智能家居场景，实现设备联动、安防监控和节能管理

spec:
  type: primary
  category: iot
  
  capabilities:
    - id: device-control
      name: 设备控制
      description: 控制智能家居设备
      category: actuation
      parameters:
        - name: deviceId
          type: string
          required: true
        - name: action
          type: string
          required: true
        - name: params
          type: object
          required: false
      returns:
        type: ControlResult
    
    - id: sensor-read
      name: 传感器读取
      description: 读取传感器数据
      category: sensing
      parameters:
        - name: sensorId
          type: string
          required: true
        - name: dataType
          type: string
          required: false
      returns:
        type: SensorData
    
    - id: scene-switch
      name: 场景切换
      description: 切换家庭场景模式
      category: control
      parameters:
        - name: mode
          type: string
          required: true
          enum: [home, away, sleep, vacation]
      returns:
        type: SceneResult
    
    - id: alert-trigger
      name: 告警触发
      description: 触发安防告警
      category: security
      parameters:
        - name: alertType
          type: string
          required: true
        - name: level
          type: string
          required: true
          enum: [info, warning, critical]
        - name: message
          type: string
          required: false
      returns:
        type: AlertResult
    
    - id: rule-define
      name: 规则定义
      description: 定义自动化规则
      category: automation
      parameters:
        - name: ruleName
          type: string
          required: true
        - name: trigger
          type: object
          required: true
        - name: actions
          type: array
          required: true
      returns:
        type: RuleResult

  roles:
    - name: owner
      description: 家庭主人
      required: true
      minCount: 1
      maxCount: 5
      capabilities:
        - device-control
        - scene-switch
        - rule-define
        - alert-trigger
    
    - name: device-agent
      description: 设备代理Agent
      required: true
      minCount: 1
      maxCount: 100
      capabilities:
        - device-control
        - sensor-read
    
    - name: coordinator
      description: 协调Agent
      required: false
      minCount: 0
      maxCount: 1
      capabilities:
        - scene-switch
        - rule-define
        - alert-trigger
    
    - name: llm-assistant
      description: 智能助手
      required: false
      minCount: 0
      maxCount: 1
      capabilities:
        - rule-define

  deviceBindings:
    - deviceId: door-sensor-001
      type: sensor
      capabilities: [sensor-read]
      location: entrance
    
    - deviceId: camera-001
      type: actuator
      capabilities: [device-control, sensor-read]
      location: entrance

  automationRules:
    - name: away-mode
      trigger:
        type: geo-fence
        condition: "user.distance > 500m"
      actions:
        - capability: scene-switch
          params: { mode: away }
        - capability: device-control
          target: camera-001
          params: { action: start-recording }
    
    - name: home-mode
      trigger:
        type: geo-fence
        condition: "user.distance < 100m"
      actions:
        - capability: scene-switch
          params: { mode: home }
        - capability: device-control
          target: light-001
          params: { action: on, brightness: 80 }
    
    - name: night-alert
      trigger:
        type: sensor
        source: door-sensor-001
        condition: "value == 'open' AND time.hour >= 23 OR time.hour < 6"
      actions:
        - capability: alert-trigger
          params: 
            alertType: intrusion
            level: critical
            message: "夜间门窗异常开启"
```

---

### 3.3 用例三：内容创作协作场景（多Agent协作型）

#### 用户故事

> **我是自媒体运营者**，我需要管理多个平台的内容发布。

#### 需求分析

- 一键生成文章初稿（AI辅助）
- 多人协作编辑审核
- 一键发布到多个平台
- 发布后自动收集数据、生成分析报告

#### 场景定义

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneDefinition

metadata:
  name: content-creation
  version: "1.0"
  description: 内容创作协作场景，支持AI辅助创作、多人协作、多平台发布

spec:
  type: primary
  category: business
  
  capabilities:
    - id: content-draft
      name: 内容草稿生成
      description: AI生成内容初稿
      category: creation
      parameters:
        - name: topic
          type: string
          required: true
        - name: style
          type: string
          required: false
          enum: [formal, casual, professional]
        - name: length
          type: integer
          required: false
        - name: references
          type: array
          required: false
      returns:
        type: DraftResult
    
    - id: content-edit
      name: 内容编辑
      description: 协作编辑内容
      category: collaboration
      parameters:
        - name: contentId
          type: string
          required: true
        - name: changes
          type: object
          required: true
        - name: version
          type: integer
          required: true
      returns:
        type: EditResult
    
    - id: content-review
      name: 内容审核
      description: 审核内容质量
      category: quality
      parameters:
        - name: contentId
          type: string
          required: true
        - name: reviewType
          type: string
          required: true
          enum: [auto, manual]
      returns:
        type: ReviewResult
    
    - id: content-publish
      name: 内容发布
      description: 发布到各平台
      category: distribution
      parameters:
        - name: contentId
          type: string
          required: true
        - name: platforms
          type: array
          required: true
        - name: scheduleTime
          type: string
          required: false
      returns:
        type: PublishResult
    
    - id: data-collect
      name: 数据采集
      description: 采集发布数据
      category: analytics
      parameters:
        - name: contentId
          type: string
          required: true
        - name: metrics
          type: array
          required: false
      returns:
        type: CollectResult
    
    - id: report-generate
      name: 报告生成
      description: 生成分析报告
      category: analytics
      parameters:
        - name: period
          type: object
          required: true
        - name: format
          type: string
          required: false
          enum: [pdf, html, markdown]
      returns:
        type: ReportResult

  roles:
    - name: creator
      description: 内容创作者
      required: true
      minCount: 1
      maxCount: 10
      capabilities:
        - content-draft
        - content-edit
    
    - name: editor
      description: 编辑审核者
      required: false
      minCount: 0
      maxCount: 5
      capabilities:
        - content-edit
        - content-review
    
    - name: publisher
      description: 发布管理员
      required: false
      minCount: 0
      maxCount: 3
      capabilities:
        - content-publish
        - data-collect
    
    - name: llm-writer
      description: AI写作助手
      required: false
      minCount: 0
      maxCount: 3
      capabilities:
        - content-draft
        - content-review
    
    - name: llm-analyst
      description: AI分析助手
      required: false
      minCount: 0
      maxCount: 2
      capabilities:
        - data-collect
        - report-generate
    
    - name: platform-agent
      description: 平台发布Agent
      required: false
      minCount: 0
      maxCount: 10
      capabilities:
        - content-publish
        - data-collect

  workflow:
    triggers:
      - type: manual
        action: create-content
      - type: schedule
        cron: "0 9 * * 1"
        action: weekly-report
    
    steps:
      - id: draft
        name: 生成初稿
        capability: content-draft
        executor: llm-writer
        input:
          topic: ${input.topic}
          style: ${input.style}
      
      - id: edit
        name: 协作编辑
        capability: content-edit
        executor: creator
        dependsOn: [draft]
        type: collaborative
        timeout: 86400000
      
      - id: review
        name: 内容审核
        capability: content-review
        executor: editor
        dependsOn: [edit]
        input:
          reviewType: auto
      
      - id: publish
        name: 多平台发布
        capability: content-publish
        executor: platform-agent
        dependsOn: [review]
        parallel: true
        input:
          platforms: ${input.platforms}
      
      - id: collect
        name: 数据采集
        capability: data-collect
        executor: llm-analyst
        dependsOn: [publish]
        delay: 3600000
      
      - id: report
        name: 生成报告
        capability: report-generate
        executor: llm-analyst
        dependsOn: [collect]
```

---

### 3.4 用例四：跨组织项目协作场景（跨域协作型）

#### 用户故事

> **我是甲方项目经理**，我需要与乙方开发团队协作完成一个软件项目。

#### 需求分析

- 甲方定义需求、验收标准、里程碑
- 乙方接收任务、分配开发、提交代码
- 双方共享项目进度、风险透明
- 敏感信息需要隔离

#### 场景定义

```yaml
apiVersion: scene.ooder.net/v1
kind: SceneDefinition

metadata:
  name: cross-org-project
  version: "1.0"
  description: 跨组织项目协作场景，支持多方安全协作、权限隔离和项目治理

spec:
  type: primary
  category: business
  
  capabilities:
    - id: requirement-define
      name: 需求定义
      description: 定义项目需求
      category: planning
      parameters:
        - name: title
          type: string
          required: true
        - name: description
          type: string
          required: true
        - name: acceptance
          type: array
          required: true
        - name: priority
          type: string
          required: false
      returns:
        type: RequirementResult
      permissions:
        - project:requirement:create
    
    - id: task-assign
      name: 任务分配
      description: 分配任务给成员
      category: execution
      parameters:
        - name: taskId
          type: string
          required: true
        - name: assignee
          type: string
          required: true
        - name: deadline
          type: string
          required: true
      returns:
        type: AssignResult
      permissions:
        - project:task:assign
    
    - id: code-submit
      name: 代码提交
      description: 提交代码变更
      category: development
      parameters:
        - name: taskId
          type: string
          required: true
        - name: codeUrl
          type: string
          required: true
        - name: message
          type: string
          required: false
      returns:
        type: SubmitResult
      permissions:
        - project:code:submit
    
    - id: code-review
      name: 代码审查
      description: 审查代码变更
      category: quality
      parameters:
        - name: pullRequestId
          type: string
          required: true
        - name: reviewResult
          type: string
          required: true
          enum: [approve, reject, comment]
        - name: comments
          type: array
          required: false
      returns:
        type: ReviewResult
      permissions:
        - project:code:review
    
    - id: milestone-approve
      name: 里程碑审批
      description: 审批里程碑完成
      category: governance
      parameters:
        - name: milestoneId
          type: string
          required: true
        - name: approved
          type: boolean
          required: true
        - name: feedback
          type: string
          required: false
      returns:
        type: ApproveResult
      permissions:
        - project:milestone:approve
    
    - id: risk-report
      name: 风险上报
      description: 上报项目风险
      category: governance
      parameters:
        - name: riskType
          type: string
          required: true
        - name: severity
          type: string
          required: true
        - name: description
          type: string
          required: true
        - name: mitigation
          type: string
          required: false
      returns:
        type: RiskResult
      permissions:
        - project:risk:report
    
    - id: doc-share
      name: 文档共享
      description: 共享项目文档
      category: collaboration
      parameters:
        - name: docId
          type: string
          required: true
        - name: accessLevel
          type: string
          required: true
          enum: [read, write, admin]
        - name: targetOrg
          type: string
          required: true
      returns:
        type: ShareResult
      permissions:
        - project:doc:share
    
    - id: project-archive
      name: 项目归档
      description: 归档项目资产
      category: governance
      parameters:
        - name: archiveScope
          type: array
          required: true
        - name: retention
          type: string
          required: false
      returns:
        type: ArchiveResult
      permissions:
        - project:archive:execute

  roles:
    - name: client-pm
      description: 甲方项目经理
      required: true
      minCount: 1
      maxCount: 3
      capabilities:
        - requirement-define
        - milestone-approve
        - risk-report
        - doc-share
      organization: client
    
    - name: client-stakeholder
      description: 甲方干系人
      required: false
      minCount: 0
      maxCount: 10
      capabilities:
        - milestone-approve
        - doc-share
      organization: client
    
    - name: vendor-pm
      description: 乙方项目经理
      required: true
      minCount: 1
      maxCount: 2
      capabilities:
        - task-assign
        - risk-report
        - doc-share
      organization: vendor
    
    - name: vendor-developer
      description: 乙方开发人员
      required: true
      minCount: 1
      maxCount: 20
      capabilities:
        - code-submit
        - code-review
      organization: vendor
    
    - name: code-reviewer
      description: 代码审查员
      required: false
      minCount: 0
      maxCount: 10
      capabilities:
        - code-review
      organization: any
    
    - name: llm-assistant
      description: AI项目助手
      required: false
      minCount: 0
      maxCount: 3
      capabilities:
        - code-review
        - risk-report

  securityPolicy:
    dataIsolation:
      - domain: client-business
        access: [client-pm, client-stakeholder]
      - domain: vendor-code
        access: [vendor-pm, vendor-developer, code-reviewer]
      - domain: shared-docs
        access: [all]
    
    crossOrgRules:
      - source: client
        target: vendor
        allowedCapabilities: [requirement-define, milestone-approve]
      - source: vendor
        target: client
        allowedCapabilities: [code-submit, risk-report]
    
    auditLogging:
      - capability: "*"
        level: detailed
        retention: 365d

  workflow:
    triggers:
      - type: manual
        action: create-project
      - type: milestone
        action: milestone-review
    
    phases:
      - name: initiation
        steps:
          - id: define-requirements
            capability: requirement-define
            executor: client-pm
          - id: setup-repo
            capability: code-submit
            executor: vendor-pm
      
      - name: execution
        steps:
          - id: assign-task
            capability: task-assign
            executor: vendor-pm
          - id: develop
            capability: code-submit
            executor: vendor-developer
          - id: review
            capability: code-review
            executor: code-reviewer
            parallel: true
      
      - name: closure
        steps:
          - id: final-approve
            capability: milestone-approve
            executor: client-pm
          - id: archive
            capability: project-archive
            executor: coordinator
```

---

## 四、场景类型对比

### 3.1 四种场景类型对比

| 维度 | 日志汇报 | 智能家居 | 内容创作 | 跨组织项目 |
|------|----------|----------|----------|------------|
| **场景类型** | 流程驱动型 | 事件驱动型 | 创作协作型 | 治理管控型 |
| **触发方式** | 定时触发 | 事件/条件触发 | 手动+定时 | 里程碑触发 |
| **参与者关系** | 层级关系 | 设备+人 | 平等协作 | 跨组织边界 |
| **Agent角色** | 辅助型(LLM分析) | 执行型(设备Agent) | 创作型(LLM写作) | 治理型(审查Agent) |
| **SuperAgent涌现能力** | 智能汇总 | 预测控制 | 多平台同步 | 跨组织同步 |
| **数据流向** | 汇聚型 | 双向型 | 分发型 | 隔离型 |
| **安全需求** | 中等 | 中等 | 中等 | 高 |
| **生命周期** | 长期运行 | 持续运行 | 按需创建 | 项目周期 |

---

## 五、实体模型设计

### 4.1 实体关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              定义层                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────┐     ┌─────────────────────┐                       │
│  │  SceneDefinition    │────▶│   CapabilityDef     │                       │
│  │ ─────────────────── │     │ ─────────────────── │                       │
│  │ definitionId        │     │ capDefId            │                       │
│  │ name, version       │     │ name, type          │                       │
│  │ category            │     │ parameters[]        │                       │
│  │ type                │     │ returns             │                       │
│  │ capabilities[]      │     │ permissions[]       │                       │
│  │ roles[]             │     └─────────────────────┘                       │
│  │ workflow            │                                                   │
│  │ securityPolicy      │     ┌─────────────────────┐                       │
│  │ deviceBindings[]    │────▶│   RoleDefinition    │                       │
│  │ automationRules[]   │     │ ─────────────────── │                       │
│  └─────────────────────┘     │ roleId              │                       │
│                              │ name, description   │                       │
│                              │ required            │                       │
│                              │ minCount, maxCount  │                       │
│                              │ capabilities[]      │                       │
│                              │ organization        │                       │
│                              └─────────────────────┘                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ 创建运行实例
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              运行层                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────┐                                                   │
│  │    SceneGroup       │                                                   │
│  │ ─────────────────── │                                                   │
│  │ sceneGroupId        │                                                   │
│  │ definitionId (FK)   │                                                   │
│  │ name                │                                                   │
│  │ status              │                                                   │
│  │ creatorId           │                                                   │
│  │ creatorType         │                                                   │
│  │ config              │                                                   │
│  └─────────┬───────────┘                                                   │
│            │                                                                │
│   ┌────────┼────────┬────────────┬─────────────┐                           │
│   │        │        │            │             │                           │
│   ▼        ▼        ▼            ▼             ▼                           │
│ ┌─────┐ ┌─────┐ ┌─────────┐ ┌──────────┐ ┌───────────┐                    │
│ │Parti│ │Cap  │ │Workflow │ │Security  │ │Device     │                    │
│ │-cipa│ │Bind │ │Instance │ │Context   │ │Binding    │                    │
│ │-nt  │ │     │ │         │ │          │ │           │                    │
│ └──┬──┘ └─────┘ └─────────┘ └──────────┘ └───────────┘                    │
│    │                                                                       │
│    ▼                                                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    ParticipantDetail                                 │   │
│  │  ┌───────────────┐ ┌───────────────┐ ┌───────────────────────────┐ │   │
│  │  │UserParticipant│ │AgentParticipant│ │SuperAgentParticipant      │ │   │
│  │  │───────────────│ │───────────────│ │───────────────────────────│ │   │
│  │  │userId         │ │agentId        │ │superAgentId               │ │   │
│  │  │userName       │ │agentType:     │ │subAgents[]                │ │   │
│  │  │organization   │ │ LLM|WORKER|   │ │coordination               │ │   │
│  │  │permissions[]  │ │ DEVICE        │ │emergentCapabilities[]     │ │   │
│  │  └───────────────┘ │capabilities[] │ │selfDefined: boolean       │ │   │
│  │                    │preferredDevice│ └───────────────────────────┘ │   │
│  │                    └───────────────┘                               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 核心DTO定义

#### 4.2.1 场景定义DTO

```java
public class SceneDefinitionDTO {
    private String definitionId;
    private String name;
    private String version;
    private String description;
    private String category;
    private SceneType type;
    
    private List<CapabilityDefDTO> capabilities;
    private List<RoleDefinitionDTO> roles;
    private WorkflowDefDTO workflow;
    private SecurityPolicyDTO securityPolicy;
    private List<DeviceBindingDefDTO> deviceBindings;
    private List<AutomationRuleDTO> automationRules;
    
    private long createTime;
    private long updateTime;
    private boolean active;
}

public enum SceneType {
    PRIMARY,        // 主场景
    COLLABORATIVE   // 协作场景
}
```

#### 4.2.2 场景组DTO

```java
public class SceneGroupDTO {
    private String sceneGroupId;
    private String definitionId;
    private String name;
    private String description;
    private SceneGroupStatus status;
    
    private String creatorId;
    private ParticipantType creatorType;
    private SceneGroupConfigDTO config;
    
    private int memberCount;
    private String primaryAgentId;
    
    private long createTime;
    private long lastUpdateTime;
}

public enum SceneGroupStatus {
    DRAFT,          // 草稿
    CREATING,       // 创建中
    CONFIGURING,    // 配置中
    PENDING,        // 待激活
    ACTIVE,         // 运行中
    SUSPENDED,      // 暂停
    SCALING,        // 扩缩容
    MIGRATING,      // 迁移中
    DESTROYING,     // 销毁中
    DESTROYED,      // 已销毁
    ERROR           // 错误
}
```

#### 4.2.3 参与者DTO

```java
public class SceneParticipantDTO {
    private String participantId;
    private String sceneGroupId;
    private ParticipantType participantType;
    private String role;
    private List<String> capabilities;
    private ParticipantStatus status;
    
    private long joinTime;
    private long lastHeartbeat;
    
    private UserParticipantDTO userDetail;
    private AgentParticipantDTO agentDetail;
    private SuperAgentParticipantDTO superAgentDetail;
}

public enum ParticipantType {
    USER,           // 直接用户
    AGENT,          // 普通Agent
    SUPER_AGENT     // 超级Agent
}

public enum ParticipantStatus {
    INVITED,        // 已邀请
    JOINED,         // 已加入
    ACTIVE,         // 活跃
    IDLE,           // 空闲
    BUSY,           // 忙碌
    OFFLINE,        // 离线
    SUSPENDED,      // 暂停
    LEFT            // 已离开
}
```

#### 4.2.4 Agent参与者详情

```java
public class AgentParticipantDTO {
    private String agentId;
    private String agentName;
    private AgentType agentType;
    private List<String> capabilities;
    private String preferredDevice;
    private AgentStatus status;
}

public enum AgentType {
    LLM,            // 大语言模型Agent
    WORKER,         // 工作Agent
    DEVICE,         // 设备Agent
    PLATFORM        // 平台Agent
}
```

#### 4.2.5 SuperAgent参与者详情

```java
public class SuperAgentParticipantDTO {
    private String superAgentId;
    private String name;
    private String description;
    
    private List<SubAgentRef> subAgents;
    private CoordinationConfig coordination;
    private List<CapabilityDTO> emergentCapabilities;
    private boolean selfDefined;
    
    private SuperAgentStatus status;
}

public class SubAgentRef {
    private String agentId;
    private String role;  // COORDINATOR, EXECUTOR, MONITOR
    private List<String> capabilities;
}

public class CoordinationConfig {
    private CoordinationType type;  // SEQUENTIAL, PARALLEL, HYBRID
    private List<CoordinationRule> rules;
    private ConflictResolution conflictResolution;
    private long timeout;
}

public enum CoordinationType {
    SEQUENTIAL,     // 顺序执行
    PARALLEL,       // 并行执行
    HYBRID          // 混合模式
}

public enum ConflictResolution {
    VOTE,           // 投票决策
    PRIORITY,       // 优先级决策
    CONSENSUS       // 共识决策
}
```

#### 4.2.6 能力绑定DTO

```java
public class CapabilityBindingDTO {
    private String bindingId;
    private String sceneGroupId;
    private String capId;
    
    private CapabilityProviderType providerType;
    private String providerId;
    
    private ConnectorType connectorType;
    private Map<String, Object> connectorConfig;
    
    private int priority;
    private boolean fallback;
    private FallbackConfig fallbackConfig;
    
    private CapabilityBindingStatus status;
}

public enum CapabilityProviderType {
    SKILL,          // Skill提供
    AGENT,          // Agent提供
    SUPER_AGENT,    // SuperAgent涌现
    DEVICE,         // 设备提供
    PLATFORM,       // 平台提供
    CROSS_SCENE     // 跨场景引用
}

public enum ConnectorType {
    HTTP,           // HTTP/HTTPS远程调用
    GRPC,           // gRPC调用
    WEBSOCKET,      // WebSocket双向通信
    LOCAL_JAR,      // 本地JAR调用
    UDP,            // UDP数据报
    INTERNAL        // 内部调用
}
```

#### 4.2.7 安全策略DTO

```java
public class SecurityPolicyDTO {
    private List<DataIsolationRule> dataIsolation;
    private List<CrossOrgRule> crossOrgRules;
    private AuditLoggingConfig auditLogging;
}

public class DataIsolationRule {
    private String domain;
    private List<String> access;
}

public class CrossOrgRule {
    private String sourceOrg;
    private String targetOrg;
    private List<String> allowedCapabilities;
}

public class AuditLoggingConfig {
    private String level;  // basic, detailed, full
    private long retention;
}
```

---

## 六、场景生命周期管理

### 5.1 状态转换图

```
                              ┌─────────────┐
                              │   DRAFT     │
                              │  (草稿)     │
                              └──────┬──────┘
                                     │ 发布
                                     ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│   ┌─────────────┐      能力绑定完成      ┌─────────────┐                   │
│   │  CREATING   │ ──────────────────────▶│   PENDING   │                   │
│   │  (创建中)   │                        │  (待激活)   │                   │
│   └──────┬──────┘                        └──────┬──────┘                   │
│          │                                      │                          │
│          │ 参与者加入                           │ 激活条件满足              │
│          │ 能力绑定                             │ (minMembers达成)          │
│          ▼                                      ▼                          │
│   ┌─────────────┐                        ┌─────────────┐                   │
│   │  CONFIGURING│ ──────────────────────▶│   ACTIVE    │◄──────────────┐  │
│   │  (配置中)   │   配置完成              │  (运行中)   │               │  │
│   └─────────────┘                        └──────┬──────┘               │  │
│                                                 │                      │  │
│                          ┌────────────────────┬─┴──────────────────┐   │  │
│                          │                    │                    │   │  │
│                          ▼                    ▼                    ▼   │  │
│                   ┌─────────────┐     ┌─────────────┐     ┌─────────────┐│  │
│                   │  SUSPENDED  │     │   SCALING   │     │  MIGRATING  ││  │
│                   │  (暂停)     │     │  (扩缩容)   │     │  (迁移中)   ││  │
│                   └──────┬──────┘     └──────┬──────┘     └──────┬──────┘│  │
│                          │                   │                    │       │  │
│                          │ 恢复              │ 完成               │ 完成  │  │
│                          └───────────────────┴────────────────────┘───────┘  │
│                                                                             │
│                          ┌──────────────────────────────────────────────┐   │
│                          │                                              │   │
│                          ▼                                              │   │
│                   ┌─────────────┐                                      │   │
│                   │  DESTROYING │                                      │   │
│                   │  (销毁中)   │                                      │   │
│                   └──────┬──────┘                                      │   │
│                          │                                              │   │
│                          ▼                                              │   │
│                   ┌─────────────┐                                      │   │
│                   │  DESTROYED  │                                      │   │
│                   │  (已销毁)   │                                      │   │
│                   └─────────────┘                                      │   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 生命周期事件

```java
public class SceneLifecycleEvent {
    private String eventId;
    private String sceneGroupId;
    private SceneGroupStatus fromStatus;
    private SceneGroupStatus toStatus;
    private String trigger;  // USER, SYSTEM, AGENT, TIMEOUT
    private String operator;
    private long timestamp;
    private Map<String, Object> context;
}

public enum SceneEventType {
    SCENE_CREATED,          // 场景创建
    SCENE_ACTIVATED,        // 场景激活
    SCENE_SUSPENDED,        // 场景暂停
    SCENE_RESUMED,          // 场景恢复
    SCENE_DESTROYED,        // 场景销毁
    
    PARTICIPANT_JOINED,     // 参与者加入
    PARTICIPANT_LEFT,       // 参与者离开
    PARTICIPANT_ROLE_CHANGED, // 角色变更
    
    CAPABILITY_BOUND,       // 能力绑定
    CAPABILITY_UNBOUND,     // 能力解绑
    
    WORKFLOW_STARTED,       // 工作流启动
    WORKFLOW_COMPLETED,     // 工作流完成
    WORKFLOW_FAILED,        // 工作流失败
    
    FAILOVER_TRIGGERED,     // 故障转移触发
    FAILOVER_COMPLETED      // 故障转移完成
}
```

---

## 七、能力管理规范

### 6.1 能力类型分类

```java
public enum CapabilityCategory {
    // 感知类
    SENSING,        // 传感器读取
    
    // 执行类
    ACTUATION,      // 设备控制
    NOTIFICATION,   // 通知推送
    
    // 数据类
    DATA_INPUT,     // 数据输入
    DATA_ACCESS,    // 数据访问
    DATA_PROCESSING,// 数据处理
    
    // 协作类
    COLLABORATION,  // 协作编辑
    COMMUNICATION,  // 通信
    
    // 智能类
    INTELLIGENCE,   // AI分析
    CREATION,       // 内容生成
    
    // 管理类
    PLANNING,       // 规划
    GOVERNANCE,     // 治理
    CONTROL,        // 控制
    AUTOMATION,     // 自动化
    
    // UI类
    UI,             // 界面展示
    
    // 安全类
    SECURITY,       // 安全相关
    QUALITY         // 质量相关
}
```

### 6.2 涌现能力机制

```java
public class EmergentCapabilityDTO {
    private String emergentCapId;
    private String superAgentId;
    private String name;
    private String description;
    
    private List<String> sourceCapabilities;
    private String emergenceRule;
    
    private EmergenceType emergenceType;
    private long createTime;
}

public enum EmergenceType {
    COMPOSITION,    // 组合涌现：多能力组合产生新能力
    COORDINATION,   // 协调涌现：多Agent协调产生新能力
    ADAPTATION,     // 适应涌现：根据环境自适应产生新能力
    LEARNING        // 学习涌现：通过学习产生新能力
}
```

---

## 八、工作流引擎规范

### 7.1 工作流定义

```java
public class WorkflowDefinitionDTO {
    private String workflowId;
    private String name;
    private String description;
    
    private List<TriggerConfig> triggers;
    private List<WorkflowStepDef> steps;
    private List<String> phases;
    
    private long timeout;
    private RetryPolicy retryPolicy;
    private ErrorHandling errorHandling;
}

public class WorkflowStepDef {
    private String stepId;
    private String name;
    private String capability;
    private String executor;
    
    private StepType type;
    private Map<String, Object> input;
    private String output;
    
    private List<String> dependsOn;
    private boolean parallel;
    private long delay;
    private long timeout;
    
    private RetryPolicy retryPolicy;
}

public enum StepType {
    ACTION,         // 执行动作
    WAIT,           // 等待事件
    DECISION,       // 条件判断
    PARALLEL,       // 并行执行
    COLLABORATIVE,  // 协作执行
    LOOP            // 循环执行
}
```

### 7.2 触发器类型

```java
public enum TriggerType {
    MANUAL,         // 手动触发
    SCHEDULE,       // 定时触发
    EVENT,          // 事件触发
    SENSOR,         // 传感器触发
    GEO_FENCE,      // 地理围栏触发
    MILESTONE,      // 里程碑触发
    WEBHOOK         // Webhook触发
}
```

---

## 九、服务接口设计

### 8.1 SceneDefinitionService

```java
public interface SceneDefinitionService {
    
    SceneDefinitionDTO create(SceneDefinitionDTO definition);
    
    boolean delete(String definitionId);
    
    SceneDefinitionDTO get(String definitionId);
    
    PageResult<SceneDefinitionDTO> listAll(int pageNum, int pageSize);
    
    PageResult<SceneDefinitionDTO> listByCategory(String category, int pageNum, int pageSize);
    
    boolean activate(String definitionId);
    
    boolean deactivate(String definitionId);
    
    boolean addCapability(String definitionId, CapabilityDefDTO capability);
    
    boolean removeCapability(String definitionId, String capDefId);
    
    boolean addRole(String definitionId, RoleDefinitionDTO role);
    
    boolean removeRole(String definitionId, String roleId);
}
```

### 8.2 SceneGroupService

```java
public interface SceneGroupService {
    
    SceneGroupDTO create(String definitionId, SceneGroupConfigDTO config);
    
    boolean destroy(String sceneGroupId);
    
    SceneGroupDTO get(String sceneGroupId);
    
    PageResult<SceneGroupDTO> listAll(int pageNum, int pageSize);
    
    PageResult<SceneGroupDTO> listByDefinition(String definitionId, int pageNum, int pageSize);
    
    boolean activate(String sceneGroupId);
    
    boolean deactivate(String sceneGroupId);
    
    boolean join(String sceneGroupId, SceneParticipantDTO participant);
    
    boolean leave(String sceneGroupId, String participantId);
    
    boolean changeRole(String sceneGroupId, String participantId, String newRole);
    
    PageResult<SceneParticipantDTO> listParticipants(String sceneGroupId, int pageNum, int pageSize);
    
    SceneParticipantDTO getParticipant(String sceneGroupId, String participantId);
    
    boolean bindCapability(String sceneGroupId, CapabilityBindingDTO binding);
    
    boolean unbindCapability(String sceneGroupId, String bindingId);
    
    PageResult<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId, int pageNum, int pageSize);
    
    SceneSnapshotDTO createSnapshot(String sceneGroupId);
    
    boolean restoreSnapshot(String sceneGroupId, SceneSnapshotDTO snapshot);
    
    FailoverStatusDTO getFailoverStatus(String sceneGroupId);
    
    boolean handleFailover(String sceneGroupId, String failedParticipantId);
}
```

### 8.3 SceneWorkflowService

```java
public interface SceneWorkflowService {
    
    SceneWorkflowInstanceDTO start(String sceneGroupId, String triggerType);
    
    boolean cancel(String workflowId);
    
    SceneWorkflowInstanceDTO get(String workflowId);
    
    PageResult<SceneWorkflowInstanceDTO> listBySceneGroup(String sceneGroupId, int pageNum, int pageSize);
    
    boolean pause(String workflowId);
    
    boolean resume(String workflowId);
    
    boolean retryStep(String workflowId, String stepId);
    
    List<ExecutionLog> getExecutionLogs(String workflowId);
}
```

---

## 九、API接口规范

### 9.1 RESTful API

```yaml
# 场景定义管理
POST   /api/v1/scene-definitions                    # 创建场景定义
GET    /api/v1/scene-definitions                    # 列表查询
GET    /api/v1/scene-definitions/{definitionId}     # 获取详情
PUT    /api/v1/scene-definitions/{definitionId}     # 更新定义
DELETE /api/v1/scene-definitions/{definitionId}     # 删除定义
POST   /api/v1/scene-definitions/{definitionId}/activate   # 激活定义
POST   /api/v1/scene-definitions/{definitionId}/deactivate # 停用定义

# 场景组管理
POST   /api/v1/scene-groups                       # 创建场景组
GET    /api/v1/scene-groups                       # 列表查询
GET    /api/v1/scene-groups/{sceneGroupId}        # 获取详情
PUT    /api/v1/scene-groups/{sceneGroupId}        # 更新配置
DELETE /api/v1/scene-groups/{sceneGroupId}        # 销毁场景组
POST   /api/v1/scene-groups/{sceneGroupId}/activate   # 激活场景
POST   /api/v1/scene-groups/{sceneGroupId}/deactivate # 暂停场景

# 参与者管理
POST   /api/v1/scene-groups/{sceneGroupId}/participants        # 加入场景
GET    /api/v1/scene-groups/{sceneGroupId}/participants        # 参与者列表
GET    /api/v1/scene-groups/{sceneGroupId}/participants/{participantId}  # 参与者详情
DELETE /api/v1/scene-groups/{sceneGroupId}/participants/{participantId}  # 离开场景
PUT    /api/v1/scene-groups/{sceneGroupId}/participants/{participantId}/role  # 变更角色

# 能力绑定管理
POST   /api/v1/scene-groups/{sceneGroupId}/capabilities        # 绑定能力
GET    /api/v1/scene-groups/{sceneGroupId}/capabilities        # 能力列表
DELETE /api/v1/scene-groups/{sceneGroupId}/capabilities/{bindingId}  # 解绑能力

# 工作流管理
POST   /api/v1/scene-groups/{sceneGroupId}/workflows           # 启动工作流
GET    /api/v1/scene-groups/{sceneGroupId}/workflows           # 工作流列表
GET    /api/v1/workflows/{workflowId}                          # 工作流详情
POST   /api/v1/workflows/{workflowId}/cancel                   # 取消工作流
POST   /api/v1/workflows/{workflowId}/pause                    # 暂停工作流
POST   /api/v1/workflows/{workflowId}/resume                   # 恢复工作流

# 快照管理
POST   /api/v1/scene-groups/{sceneGroupId}/snapshots           # 创建快照
GET    /api/v1/scene-groups/{sceneGroupId}/snapshots           # 快照列表
POST   /api/v1/scene-groups/{sceneGroupId}/snapshots/{snapshotId}/restore  # 恢复快照
```

### 9.2 WebSocket事件

```yaml
# 连接URL
wss://{host}/ws/scene-groups/{sceneGroupId}?token={jwt}

# 客户端 → 服务端消息
- PARTICIPANT_HEARTBEAT    # 参与者心跳
- CAPABILITY_INVOKE        # 能力调用
- WORKFLOW_CONTROL         # 工作流控制

# 服务端 → 客户端消息
- SCENE_STATE_CHANGE       # 场景状态变更
- PARTICIPANT_CHANGE       # 参与者变更
- CAPABILITY_EVENT         # 能力事件
- WORKFLOW_PROGRESS        # 工作流进度
- ALERT_NOTIFICATION       # 告警通知
```

---

## 十一、内置固定能力

场景系统提供三个内置固定能力，作为所有场景的基础支撑能力，无需显式定义即可使用。

### 9.1 安全能力（Security）

安全能力为场景提供统一的安全保障，包括密钥管理、访问控制、审计追踪。

#### 9.1.1 能力定义

```yaml
capability:
  id: builtin-security
  name: 安全能力
  category: SECURITY
  description: 提供密钥管理、访问控制、审计追踪等安全服务
  fixed: true                    # 固定能力标识
  autoBind: true                 # 自动绑定到所有场景
```

#### 9.1.2 SDK API

```java
public interface SecurityCapability {
    
    String useKey(String keyId, String userId, String sceneId);
    
    boolean checkPermission(String userId, String resourceType, String resourceId, String action);
    
    void audit(AuditEvent event);
    
    String encrypt(String plainText);
    
    String decrypt(String cipherText);
    
    boolean authenticate(String principalId, String token);
}
```

#### 9.1.3 使用场景

| 场景 | 使用方式 |
|------|----------|
| LLM调用 | 自动获取配置的API Key |
| 数据存储 | 敏感数据加密存储 |
| 跨场景访问 | 权限验证 |
| 操作日志 | 审计记录 |

#### 9.1.4 配置示例

```yaml
scene:
  security:
    encryption:
      enabled: true
      algorithm: AES-256
    audit:
      enabled: true
      events: [KEY_USE, PERMISSION_CHECK, DATA_ACCESS]
    keys:
      - keyId: llm-openai
        allowedRoles: [manager, llm-assistant]
      - keyId: llm-qianwen
        allowedRoles: [manager, llm-assistant]
```

### 9.2 LLM能力（Intelligence）

LLM能力为场景提供智能化支持，包括对话、生成、分析、翻译等功能。

#### 9.2.1 能力定义

```yaml
capability:
  id: builtin-llm
  name: 智能能力
  category: INTELLIGENCE
  description: 提供大语言模型对话、生成、分析、翻译等智能服务
  fixed: true
  autoBind: true
```

#### 9.2.2 SDK API

```java
public interface LlmCapability {
    
    Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options);
    
    String complete(String model, String prompt, Map<String, Object> options);
    
    List<double[]> embed(String model, List<String> texts);
    
    String translate(String model, String text, String targetLanguage, String sourceLanguage);
    
    String summarize(String model, String text, int maxLength);
    
    List<String> getSupportedModels();
    
    List<String> getAvailableProviders();
}
```

#### 9.2.3 支持的Provider

| Provider | 类型 | 支持模型 |
|----------|------|----------|
| openai | 云服务 | gpt-4, gpt-4-turbo, gpt-4o, gpt-3.5-turbo |
| qianwen | 云服务 | qwen-turbo, qwen-plus, qwen-max |
| deepseek | 云服务 | deepseek-chat, deepseek-coder |
| volcengine | 云服务 | 豆包系列模型 |
| ollama | 本地部署 | llama3, mistral, qwen2.5 |

#### 9.2.4 使用场景

| 场景 | 使用方式 |
|------|----------|
| 日志分析 | 分析日志内容，提取关键信息 |
| 内容生成 | 生成报告、摘要、草稿 |
| 智能对话 | 场景助手、问答系统 |
| 多语言处理 | 翻译、跨语言理解 |

#### 9.2.5 配置示例

```yaml
scene:
  llm:
    defaultProvider: qianwen
    defaultModel: qwen-plus
    providers:
      - type: qianwen
        apiKey: ${DASHSCOPE_API_KEY}
        models: [qwen-turbo, qwen-plus, qwen-max]
      - type: deepseek
        apiKey: ${DEEPSEEK_API_KEY}
        models: [deepseek-chat, deepseek-coder]
      - type: ollama
        baseUrl: http://localhost:11434
        models: [llama3, qwen2.5]
```

### 9.3 知识图谱能力（Knowledge Graph）

知识图谱能力为场景提供专业知识支持，包括术语映射、文档检索、RAG查询等功能。

#### 9.3.1 能力定义

```yaml
capability:
  id: builtin-kg
  name: 知识图谱能力
  category: INTELLIGENCE
  description: 提供专业术语映射、文档检索、RAG查询等知识服务
  fixed: true
  autoBind: true
```

#### 9.3.2 SDK API

```java
public interface KnowledgeCapability {
    
    TermResolution resolveTerm(String text, Map<String, Object> context);
    
    IntentClassification classifyIntent(String text, Map<String, Object> context);
    
    List<SearchResult> searchLocal(String query, Map<String, Object> options);
    
    List<SearchResult> searchKnowledge(String query, List<String> kbIds, Map<String, Object> options);
    
    RagResponse ragQuery(String query, List<String> kbIds, Map<String, Object> options);
    
    FormAssistResult assistForm(String formId, String userInput, Map<String, Object> currentData);
    
    QueryBuildResult buildQuery(String text, String entityType, Map<String, Object> context);
}
```

#### 9.3.3 核心功能

| 功能 | 说明 | API |
|------|------|-----|
| 术语映射 | 用户术语→系统概念 | `resolveTerm()` |
| 意图分类 | 识别用户操作意图 | `classifyIntent()` |
| 本地检索 | 检索本地文档 | `searchLocal()` |
| 知识库检索 | 检索企业知识库 | `searchKnowledge()` |
| RAG查询 | 知识增强生成 | `ragQuery()` |
| 表单辅助 | NLP表单填充 | `assistForm()` |
| 查询构建 | 自然语言→查询条件 | `buildQuery()` |

#### 9.3.4 使用场景

| 场景 | 使用方式 |
|------|----------|
| 表单填写 | 解析用户输入，自动填充表单字段 |
| 列表检索 | 自然语言构建查询条件 |
| 专业问答 | 基于知识库回答专业问题 |
| 术语解释 | 将用户术语映射到系统概念 |

#### 9.3.5 配置示例

```yaml
scene:
  knowledge:
    termMapping:
      enabled: true
      sources: [builtin, enterprise, personal]
    localSearch:
      enabled: true
      sources: [skills.md, local-docs]
    knowledgeBase:
      enabled: false          # 企业级功能，按需开启
      defaultKb: null
```

### 9.4 三大能力协同

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         场景内置能力协同                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│   用户输入/事件                                                              │
│        │                                                                     │
│        ▼                                                                     │
│   ┌─────────────┐                                                           │
│   │ 知识图谱(KG) │ ←── 术语映射、意图识别、上下文理解                         │
│   └─────────────┘                                                           │
│        │                                                                     │
│        ▼                                                                     │
│   ┌─────────────┐                                                           │
│   │  LLM(智能)   │ ←── 对话生成、内容分析、智能决策                           │
│   └─────────────┘                                                           │
│        │                                                                     │
│        ▼                                                                     │
│   ┌─────────────┐                                                           │
│   │  安全(Security)│ ←── 权限验证、密钥获取、审计记录                         │
│   └─────────────┘                                                           │
│        │                                                                     │
│        ▼                                                                     │
│   执行结果/响应                                                              │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 9.5 能力调用示例

```java
@Component
public class SceneAssistant {
    
    @Autowired
    private SecurityCapability securityCapability;
    
    @Autowired
    private LlmCapability llmCapability;
    
    @Autowired
    private KnowledgeCapability knowledgeCapability;
    
    public String processUserInput(String sceneId, String userId, String input) {
        
        TermResolution resolution = knowledgeCapability.resolveTerm(input, 
            Map.of("sceneId", sceneId));
        
        IntentClassification intent = knowledgeCapability.classifyIntent(input,
            Map.of("sceneId", sceneId));
        
        if ("DOC_SEARCH".equals(intent.getType())) {
            List<SearchResult> results = knowledgeCapability.searchLocal(input, 
                Map.of("topK", 5));
            return formatResults(results);
        }
        
        if (!securityCapability.checkPermission(userId, "SCENE", sceneId, "CHAT")) {
            return "您没有权限使用此功能";
        }
        
        String apiKey = securityCapability.useKey("llm-qianwen", userId, sceneId);
        
        List<Map<String, Object>> messages = buildMessages(input, resolution);
        Map<String, Object> response = llmCapability.chat("qwen-plus", messages, 
            Map.of("apiKey", apiKey));
        
        securityCapability.audit(AuditEvent.builder()
            .type("LLM_CALL")
            .userId(userId)
            .sceneId(sceneId)
            .detail(input)
            .build());
        
        return (String) ((Map)response.get("choices").get(0)).get("message").get("content");
    }
}
```

---

## 十、开发指南

### 10.1 开发优先级

| 优先级 | 模块 | 说明 |
|--------|------|------|
| P0 | SceneGroupService | 核心场景组管理 |
| P0 | SceneParticipantService | 参与者管理 |
| P0 | CapabilityBindingService | 能力绑定 |
| P1 | SceneDefinitionService | 场景定义管理 |
| P1 | SceneWorkflowService | 工作流引擎 |
| P2 | SecurityService | 安全与权限 |
| P2 | SnapshotService | 快照管理 |
| P2 | FailoverService | 故障转移 |

### 10.2 测试用例

基于四个用户用例，需要实现以下测试场景：

1. **日志汇报场景测试**
   - 创建场景组、邀请员工
   - 定时触发提醒
   - 员工提交日志
   - LLM汇总分析

2. **智能家居场景测试**
   - 设备Agent注册
   - 地理围栏触发
   - 设备联动控制
   - 异常告警

3. **内容创作场景测试**
   - AI生成草稿
   - 多人协作编辑
   - 多平台并行发布
   - 数据采集分析

4. **跨组织项目场景测试**
   - 组织边界验证
   - 数据隔离测试
   - 跨组织通信
   - 项目归档

### 10.3 零配置安装验收清单

#### 10.3.1 功能验收

- [ ] 场景模板列表正确显示
- [ ] 选择模板后显示包含的 Skills
- [ ] 未安装的 Skills 自动提示安装
- [ ] 依赖自动安装成功
- [ ] 场景自动创建成功
- [ ] 能力自动绑定成功
- [ ] 场景组自动创建成功（如有协作场景）
- [ ] 场景激活后可正常使用

#### 10.3.2 接口验收

```bash
# 1. 列出模板
curl http://localhost:8084/api/v1/templates

# 2. 部署模板
curl -X POST http://localhost:8084/api/v1/templates/knowledge-qa/deploy

# 3. 安装 Skill（含依赖）
curl -X POST http://localhost:8084/api/v1/discovery/install \
  -H "Content-Type: application/json" \
  -d '{"skillId":"skill-knowledge-ui","source":"LOCAL"}'

# 4. 查看场景
curl http://localhost:8084/api/scenes/{sceneId}

# 5. 查看场景能力
curl http://localhost:8084/api/scenes/{sceneId}/capabilities
```

#### 10.3.3 风险与对策

| 风险 | 影响 | 对策 |
|------|------|------|
| 循环依赖 | 安装失败 | 编译时检测 + 强制解除 |
| 版本冲突 | 运行异常 | 版本范围约束 + 兼容性检查 |
| 网络超时 | 安装中断 | 重试机制 + 离线缓存 |
| 场景组创建失败 | 协作失效 | 降级为单场景模式 |

---

## 十三、版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-02-27 | 初始版本，基础场景定义 |
| v2.0 | 2026-02-28 | 增加四个用户用例，完善实体模型，增加SuperAgent支持 |
| v2.1 | 2026-03-01 | 术语统一：SceneTemplate→SceneDefinition，引用统一术语表 |
| v2.2 | 2026-03-02 | 新增核心问题讨论与结论章节，明确场景创建、依赖安装时机、依赖配置位置 |

---

**相关文档**:
- [术语表](./GLOSSARY.md)
- [能力需求规格说明书](./CAPABILITY_REQUIREMENT_SPEC.md)
- [场景引擎规范](./v2.3/SCENE-ENGINE-SPEC.md)
- [场景设计](./SCENE_DESIGN.md)
- [OODER架构文档](./OODER_SKILLS_ARCHITECTURE.md)

---

*作者: Ooder Team*  
*更新时间: 2026-03-01*
