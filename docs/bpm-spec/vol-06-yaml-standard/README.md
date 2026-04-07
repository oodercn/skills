# 第六册：YAML格式标准

**文档版本**: v3.0  
**创建日期**: 2026-04-06  
**项目路径**: E:\github\ooder-skills

---

## 目录

1. [YAML格式规范](#1-yaml格式规范)
2. [流程定义YAML格式](#2-流程定义yaml格式)
3. [活动定义YAML格式](#3-活动定义yaml格式)
4. [路由定义YAML格式](#4-路由定义yaml格式)
5. [场景定义YAML格式](#5-场景定义yaml格式)
6. [完整示例](#6-完整示例)

---

## 1. YAML格式规范

### 1.1 基本规则

| 规则 | 说明 |
|------|------|
| 缩进 | 使用2个空格缩进，禁止使用Tab |
| 编码 | UTF-8编码 |
| 注释 | 使用 `#` 进行注释 |
| 字符串 | 默认不需要引号，特殊字符使用单引号或双引号 |
| 布尔值 | true/false |
| 空值 | null 或 ~ |
| 日期 | ISO 8601格式 (YYYY-MM-DD) |

### 1.2 文档结构

```yaml
apiVersion: bpm.ooder.net/v1    # API版本
kind: ProcessDef                 # 资源类型
metadata:                        # 元数据
  id: string
  name: string
  ...
spec:                            # 规格定义
  ...
```

### 1.3 命名约定

| 类型 | 约定 | 示例 |
|------|------|------|
| ID | kebab-case | `recruitment-process` |
| 枚举值 | UPPER_CASE | `FORWARD`, `CONDITION` |
| 属性名 | camelCase | `processDefId`, `routeCondition` |
| 布尔属性 | is前缀或can前缀 | `iswaitreturn`, `canRouteBack` |

---

## 2. 流程定义YAML格式

### 2.1 完整格式

```yaml
apiVersion: bpm.ooder.net/v1
kind: ProcessDef
metadata:
  id: string                      # 必填，流程ID
  name: string                    # 必填，流程名称
  description: string             # 可选，流程描述
  classification: string          # 可选，流程分类
  systemCode: string              # 可选，所属应用系统
spec:
  accessLevel: Public|Private|Block  # 必填，流程访问级别
  
  version:                        # 版本配置
    version: int                  # 版本号
    publicationStatus: UNDER_REVISION|RELEASED|UNDER_TEST  # 版本状态
    limit: int                    # 完成期限
    durationUnit: Y|M|D|H|m|s|W   # 时间单位
    
  form:                           # 表单配置
    mark: ProcessInst|ActivityInst|Person|ActivityInstPerson  # 表单标识类型
    lock: Msg|Lock|Person|Last|NO # 锁定策略
    autoSave: boolean             # 自动保存
    tableNames:                   # 关联表名
      - string
    moduleNames:                  # 模块名称
      - string
      
  activities:                     # 活动列表
    - ...
    
  routes:                         # 路由列表
    - ...
    
  listeners:                      # 监听器列表
    - ...
    
  extendedAttributes:             # 扩展属性
    key: value
```

### 2.2 字段说明

| 字段路径 | 类型 | 必填 | 说明 |
|----------|------|------|------|
| metadata.id | string | 是 | 流程唯一标识 |
| metadata.name | string | 是 | 流程名称 |
| metadata.description | string | 否 | 流程描述 |
| metadata.classification | string | 否 | 流程分类 |
| metadata.systemCode | string | 否 | 所属应用系统 |
| spec.accessLevel | enum | 是 | 流程访问级别 |
| spec.version.version | int | 是 | 版本号 |
| spec.version.publicationStatus | enum | 是 | 版本状态 |
| spec.version.limit | int | 否 | 完成期限 |
| spec.version.durationUnit | enum | 否 | 时间单位 |
| spec.form.mark | enum | 否 | 表单标识类型 |
| spec.form.lock | enum | 否 | 锁定策略 |
| spec.form.autoSave | boolean | 否 | 自动保存 |

---

## 3. 活动定义YAML格式

### 3.1 完整格式

```yaml
- id: string                      # 必填，活动ID
  name: string                    # 必填，活动名称
  description: string             # 可选，活动描述
  position: NORMAL|START|END      # 必填，活动位置
  category: HUMAN|AGENT|SCENE     # 可选，活动分类（扩展）
  implementation: IMPL_NO|IMPL_TOOL|IMPL_SUBFLOW|...  # 必填，实现方式
  
  timing:                         # 时限配置
    limit: int                    # 时间限制
    alertTime: int                # 报警时间
    durationUnit: Y|M|D|H|m|s|W   # 时间单位
    deadlineOperation: DEFAULT|DELAY|TAKEBACK|SURROGATE  # 到期处理
    
  routing:                        # 路由配置
    join: DEFAULT|AND|XOR         # 等待合并
    split: DEFAULT|AND|XOR        # 并行处理
    canRouteBack: boolean         # 是否允许退回
    routeBackMethod: DEFAULT|LAST|ANY|SPECIFY  # 退回路径
    canSpecialSend: boolean       # 是否允许特送
    specialSendScope: DEFAULT|ALL|PERFORMERS  # 特送范围
    canReSend: boolean            # 是否可以补发
    
  execution:                      # 执行配置
    execution: SYNCHR|ASYNCHR     # 同步/异步
    execClass: string             # 自动活动实现类
    
  subFlow:                        # 子流程配置
    subFlowId: string             # 子流程ID
    iswaitreturn: boolean         # 是否等待返回
    
  right:                          # 权限配置
    performType: SINGLE|MULTIPLE|JOINTSIGN|...  # 办理类型
    performSequence: FIRST|SEQUENCE|MEANWHILE|...  # 办理顺序
    performerSelectedAtt:         # 办理人公式
      - type: role|person|department|...
        value: string
        formula: string
    readerSelectedAtt:            # 阅办人公式
      - ...
    insteadSignAtt:               # 代签人公式
      - ...
    canInsteadSign: boolean       # 是否能够代签
    canTakeBack: boolean          # 是否可以收回
    canReSend: boolean            # 是否可以补发
    movePerformerTo: PERFORMER|SPONSOR|READER|...  # 办理后权限转移
    moveReaderTo: PERFORMER|SPONSOR|READER|...     # 阅办后权限转移
    
  agent:                          # Agent配置（扩展）
    agentType: LLM|TASK|EVENT|HYBRID|COORDINATOR|TOOL  # Agent类型
    scheduleStrategy: SEQUENTIAL|PARALLEL|CONDITIONAL|...  # 调度策略
    collaborationMode: SOLO|HIERARCHICAL|PEER|...  # 协作模式
    capabilities:                 # 能力列表
      - EMAIL|CALENDAR|DOCUMENT|...
    llmConfig:                    # LLM配置
      model: string
      temperature: float
      maxTokens: int
      
  scene:                          # 场景配置（扩展）
    sceneId: string               # 场景ID
    sceneType: FORM|LIST|DASHBOARD|...  # 场景类型
    a2uiConfig:                   # A2UI配置
      ...
      
  listeners:                      # 监听器列表
    - ...
    
  extendedAttributes:             # 扩展属性
    key: value
```

### 3.2 字段说明

| 字段路径 | 类型 | 必填 | 说明 |
|----------|------|------|------|
| id | string | 是 | 活动唯一标识 |
| name | string | 是 | 活动名称 |
| position | enum | 是 | 活动位置 |
| implementation | enum | 是 | 实现方式 |
| timing.limit | int | 否 | 时间限制 |
| timing.deadlineOperation | enum | 否 | 到期处理 |
| routing.join | enum | 否 | 等待合并 |
| routing.split | enum | 否 | 并行处理 |
| right.performType | enum | 否 | 办理类型 |
| right.performSequence | enum | 否 | 办理顺序 |

---

## 4. 路由定义YAML格式

### 4.1 完整格式

```yaml
- id: string                      # 必填，路由ID
  name: string                    # 必填，路由名称
  description: string             # 可选，路由描述
  
  connection:                     # 连接配置
    from: string                  # 必填，起始活动ID
    to: string                    # 必填，目标活动ID
    routeOrder: int               # 路由顺序
    
  condition:                      # 条件配置
    routeDirection: FORWARD|BACK|SPECIAL  # 路由方向
    routeConditionType: CONDITION|OTHERWISE|EXCEPTION|DEFAULTEXCEPTION  # 条件类型
    routeCondition: string        # 条件表达式
    
  listeners:                      # 监听器列表
    - ...
    
  extendedAttributes:             # 扩展属性
    key: value
```

### 4.2 字段说明

| 字段路径 | 类型 | 必填 | 说明 |
|----------|------|------|------|
| id | string | 是 | 路由唯一标识 |
| name | string | 是 | 路由名称 |
| connection.from | string | 是 | 起始活动ID |
| connection.to | string | 是 | 目标活动ID |
| condition.routeDirection | enum | 是 | 路由方向 |
| condition.routeConditionType | enum | 是 | 条件类型 |
| condition.routeCondition | string | 否 | 条件表达式 |

---

## 5. 场景定义YAML格式

### 5.1 完整格式

```yaml
apiVersion: a2ui.ooder.net/v1
kind: SceneDef
metadata:
  id: string                      # 必填，场景ID
  name: string                    # 必填，场景名称
  description: string             # 可选，场景描述
spec:
  sceneType: FORM|LIST|DASHBOARD|WORKFLOW|COLLABORATION|AGENT  # 场景类型
  
  activityBlock:                  # 活动块配置
    activities:                   # 活动引用
      - activityId: string
        activityType: HUMAN|AGENT|DEVICE
        performType: HUMAN|AGENT|DEVICE
    capabilities:                 # 能力引用
      - capabilityId: string
        capabilityType: EMAIL|CALENDAR|DOCUMENT|...
        autoBind: boolean
    context:                      # 场景上下文
      contextId: string
      variables:
        key: value
        
  a2uiConfig:                     # A2UI配置
    nexusUi:                      # Nexus UI配置
      entry:
        page: string
        title: string
        icon: string
      menu:
        position: string
        category: string
        order: int
      layout:
        type: string
        sidebar: boolean
        header: boolean
        
    pageAgent:                    # PageAgent配置
      agentId: string
      pageId: string
      pageType: string
      templatePath: string
      stylePath: string
      scriptPath: string
      initialData:
        key: value
      components:                 # 组件配置
        - componentId: string
          componentType: string
          componentLabel: string
          dataBindings:
            key: value
      functions:                  # 函数定义
        - name: string
          functionType: LLM|WORKFLOW|UI|UE|HYBRID
          description: string
          parameters:
            paramName:
              type: string
              description: string
          workflowBinding:
            processDefId: string
            activityId: string
            inputMappings:
              key: value
            
  interactionConfig:              # 交互配置
    a2a:                          # Agent交互
      - fromAgent: string
        toAgent: string
        messageType: A2A
        trigger: string
    p2a:                          # 人-Agent交互
      - participantType: string
        agentId: string
        messageType: P2A
        trigger: string
    p2p:                          # 人-人交互
      - fromParticipant: string
        toParticipant: string
        messageType: P2P
        trigger: string
        
  storageConfig:                  # 存储配置
    type: VFS|SQL|MIXED
    vfsPath: string
    tableName: string
```

---

## 6. 完整示例

### 6.1 招聘流程示例

```yaml
apiVersion: bpm.ooder.net/v1
kind: ProcessDef
metadata:
  id: recruitment-process
  name: 招聘流程
  description: 企业招聘管理流程
  classification: HR
  systemCode: HRM
spec:
  accessLevel: Public
  
  version:
    version: 1
    publicationStatus: RELEASED
    limit: 30
    durationUnit: D
    
  form:
    mark: ProcessInst
    lock: Lock
    autoSave: true
    tableNames:
      - t_recruitment
      - t_candidate
    moduleNames:
      - recruitment
      - candidate
      
  activities:
    - id: start
      name: 开始
      position: START
      
    - id: resume-screening
      name: 简历筛选
      position: NORMAL
      category: SCENE
      implementation: IMPL_NO
      timing:
        limit: 3
        alertTime: 1
        durationUnit: D
        deadlineOperation: DELAY
      routing:
        join: AND
        split: AND
        canRouteBack: true
        routeBackMethod: LAST
        canSpecialSend: true
        specialSendScope: PERFORMERS
        canReSend: true
      right:
        performType: MULTIPLE
        performSequence: MEANWHILE
        performerSelectedAtt:
          - type: role
            value: HR_RECRUITER
        readerSelectedAtt:
          - type: role
            value: HR_MANAGER
        canInsteadSign: true
        canTakeBack: true
        movePerformerTo: HISTORYPERFORMER
        moveReaderTo: HISTORYREADER
      scene:
        sceneId: resume-screening-scene
        sceneType: FORM
      agent:
        agentType: LLM
        scheduleStrategy: PARALLEL
        collaborationMode: HIERARCHICAL
        capabilities:
          - EMAIL
          - DOCUMENT
          - ANALYSIS
          
    - id: interview
      name: 面试安排
      position: NORMAL
      category: SCENE
      implementation: IMPL_NO
      
    - id: onboarding
      name: 入职办理
      position: NORMAL
      category: SCENE
      implementation: IMPL_SUBFLOW
      subFlow:
        subFlowId: onboarding-subprocess
        iswaitreturn: true
      
    - id: end
      name: 结束
      position: END
      
  routes:
    - id: r1
      name: 开始到简历筛选
      connection:
        from: start
        to: resume-screening
      condition:
        routeDirection: FORWARD
        routeConditionType: CONDITION
        routeCondition: "true"
      
    - id: r2
      name: 简历筛选到面试
      connection:
        from: resume-screening
        to: interview
      condition:
        routeDirection: FORWARD
        routeConditionType: CONDITION
        routeCondition: "${resumeApproved == true}"
      
    - id: r3
      name: 面试到入职
      connection:
        from: interview
        to: onboarding
      condition:
        routeDirection: FORWARD
        routeConditionType: CONDITION
        routeCondition: "${interviewPassed == true}"
      
    - id: r4
      name: 入职到结束
      connection:
        from: onboarding
        to: end
      condition:
        routeDirection: FORWARD
        routeConditionType: CONDITION
        routeCondition: "true"
      
  listeners:
    - id: l1
      name: 流程启动监听器
      listenerEvent: Process
      realizeClass: com.example.RecruitmentStartListener
      expressionEventType: STARTED
      
  extendedAttributes:
    department: HR
    priority: HIGH
    a2uiEnabled: true
```

### 6.2 场景定义示例

```yaml
apiVersion: a2ui.ooder.net/v1
kind: SceneDef
metadata:
  id: resume-screening-scene
  name: 简历筛选场景
  description: HR筛选候选人简历的业务场景
spec:
  sceneType: FORM
  
  activityBlock:
    activities:
      - activityId: resume-review
        activityType: HUMAN
        performType: HUMAN
      - activityId: ai-screening
        activityType: AGENT
        performType: AGENT
    capabilities:
      - capabilityId: email
        capabilityType: EMAIL
        autoBind: true
      - capabilityId: document
        capabilityType: DOCUMENT
        autoBind: true
    context:
      contextId: resume-context
      variables:
        candidateId: string
        resumeUrl: string
        screeningResult: string
        
  a2uiConfig:
    nexusUi:
      entry:
        page: index.html
        title: 简历筛选
        icon: fa-filter
      menu:
        position: sidebar
        category: recruitment
        order: 1
      layout:
        type: standard
        sidebar: true
        header: true
        
    pageAgent:
      agentId: resume-agent
      pageId: resume-page
      pageType: form
      templatePath: /templates/resume-screening.html
      stylePath: /styles/resume-screening.css
      scriptPath: /scripts/resume-screening.js
      initialData:
        status: pending
      components:
        - componentId: resume-viewer
          componentType: viewer
          componentLabel: 简历预览
          dataBindings:
            resumeUrl: url
        - componentId: result-form
          componentType: form
          componentLabel: 筛选结果
          dataBindings:
            screeningResult: result
      functions:
        - name: submitResult
          functionType: HYBRID
          description: 提交筛选结果
          parameters:
            result:
              type: string
              enum:
                - PASS
                - REJECT
              description: 筛选结果
          workflowBinding:
            processDefId: recruitment-process
            activityId: resume-screening
            inputMappings:
              result: screeningResult
            
  interactionConfig:
    a2a:
      - fromAgent: resume-agent
        toAgent: notification-agent
        messageType: A2A
        trigger: onResultSubmit
    p2a:
      - participantType: PERFORMER
        agentId: resume-agent
        messageType: P2A
        trigger: onManualReview
    p2p:
      - fromParticipant: PERFORMER
        toParticipant: SPONSOR
        messageType: P2P
        trigger: onResultSubmit
        
  storageConfig:
    type: VFS
    vfsPath: /skills/recruitment/scenes/resume-screening
```

---

## 附录

### A. 枚举值速查表

| 枚举类型 | 可选值 |
|----------|--------|
| accessLevel | Public, Private, Block |
| publicationStatus | UNDER_REVISION, RELEASED, UNDER_TEST |
| durationUnit | Y, M, D, H, m, s, W |
| position | NORMAL, START, END, LAST |
| category | HUMAN, AGENT, SCENE |
| implementation | IMPL_NO, IMPL_TOOL, IMPL_SUBFLOW, IMPL_OUTFLOW, IMPL_DEVICE, IMPL_EVENT, IMPL_SERVICE |
| deadlineOperation | DEFAULT, DELAY, TAKEBACK, SURROGATE |
| join | DEFAULT, AND, XOR |
| split | DEFAULT, AND, XOR |
| routeBackMethod | DEFAULT, LAST, ANY, SPECIFY |
| specialSendScope | DEFAULT, ALL, PERFORMERS |
| performType | SINGLE, MULTIPLE, JOINTSIGN, NEEDNOTSELECT, NOSELECT, DEFAULT |
| performSequence | FIRST, SEQUENCE, MEANWHILE, AUTOSIGN, DEFAULT |
| routeDirection | FORWARD, BACK, SPECIAL |
| routeConditionType | CONDITION, OTHERWISE, EXCEPTION, DEFAULTEXCEPTION |
| mark | ProcessInst, ActivityInst, Person, ActivityInstPerson |
| lock | Msg, Lock, Person, Last, NO |

### B. 文件命名规范

| 文件类型 | 命名规范 | 示例 |
|----------|----------|------|
| 流程定义 | {process-id}.yaml | recruitment-process.yaml |
| 场景定义 | {scene-id}.yaml | resume-screening-scene.yaml |
| HTML模板 | {page-id}.html | resume-page.html |
| CSS样式 | {style-id}.css | resume-screening.css |
| JS脚本 | {script-id}.js | resume-screening.js |

---

**文档路径**: `E:\github\ooder-skills\docs\bpm-spec\vol-06-yaml-standard\README.md`
