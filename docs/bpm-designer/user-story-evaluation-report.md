# 用户故事推导与实现评估报告

**文档版本**: 1.0  
**创建日期**: 2026-04-08  
**项目路径**: E:\github\ooder-skills\skills\_drivers\bpm\bpm-designer

---

## 📋 一、用户故事分析

### 1.1 招聘审批流程故事

**用户输入**:
> 我要建立一个招聘审批的流程？第一步 HR 筛选简历，第二步：约谈面试，第三步：领导审批，第四步，发offer，第五步 办理入职手续？

### 1.2 步骤分解

| 步骤 | 描述 | 执行者 | 需要的能力 | 数据输入 | 数据输出 |
|------|------|--------|-----------|----------|----------|
| 1. HR筛选简历 | 筛选候选人简历 | HR人员 | 简历解析、候选人评估 | 简历文件 | 候选人列表 |
| 2. 约谈面试 | 安排面试 | HR人员 | 日程管理、通知发送 | 候选人信息 | 面试安排 |
| 3. 领导审批 | 审批录用决策 | 部门领导 | 审批决策 | 面试结果 | 审批意见 |
| 4. 发Offer | 发送录用通知 | HR人员 | 邮件发送、模板生成 | 审批结果 | Offer邮件 |
| 5. 办理入职 | 新员工入职 | HR人员 | 入职流程、系统创建 | Offer接受 | 员工档案 |

---

## 📋 二、流程配置转换推导

### 2.1 流程定义 (ProcessDef)

```yaml
process:
  id: recruitment_approval
  name: 招聘审批流程
  description: 候选人招聘全流程审批
  classification: NORMAL
  systemCode: HR_SYSTEM
  accessLevel: PUBLIC
  version: 1
  publicationStatus: DRAFT
  
  timing:
    limit: 30
    durationUnit: D
    
  extendedAttributes:
    department: HR
    businessType: RECRUITMENT
    priority: HIGH
```

### 2.2 活动定义 (ActivityDef)

#### 活动1: HR筛选简历

```yaml
- id: hr_screening
  name: HR筛选简历
  type: TASK
  category: HUMAN
  position: NORMAL
  implementation: IMPL_NO
  description: HR人员筛选候选人简历
  
  timing:
    limit: 3
    durationUnit: D
    
  right:
    performType: SINGLE
    performSequence: FIRST
    performerSelectedId: hr_staff_formula
    performerSelectedAtt:
      formula: getHRStaff(dept, 'recruitment')
      formulaType: EXPRESSION
      
  extendedAttributes:
    requiredSkills:
      - resume_parsing
      - candidate_evaluation
    inputDocuments:
      - resume_file
    outputDocuments:
      - candidate_list
```

**自动筛选配置推导**:
- **办理人**: 从组织机构获取 HR 部门招聘组人员 → `getHRStaff(dept, 'recruitment')`
- **能力配置**: 
  - 简历解析能力 → `skill-resume-parser`
  - 候选人评估能力 → `skill-candidate-eval`

#### 活动2: 约谈面试

```yaml
- id: interview_schedule
  name: 约谈面试
  type: TASK
  category: HUMAN
  position: NORMAL
  implementation: IMPL_NO
  description: HR安排候选人面试
  
  timing:
    limit: 5
    durationUnit: D
    
  right:
    performType: SINGLE
    performSequence: FIRST
    performerSelectedId: hr_interviewer_formula
    performerSelectedAtt:
      formula: getInterviewer(candidate, position)
      formulaType: EXPRESSION
      
  extendedAttributes:
    requiredSkills:
      - schedule_management
      - notification_send
    inputDocuments:
      - candidate_info
    outputDocuments:
      - interview_schedule
```

**自动筛选配置推导**:
- **办理人**: 根据候选人职位匹配面试官 → `getInterviewer(candidate, position)`
- **能力配置**:
  - 日程管理能力 → `skill-schedule-manager`
  - 通知发送能力 → `skill-msg-push`

#### 活动3: 领导审批

```yaml
- id: manager_approval
  name: 领导审批
  type: TASK
  category: HUMAN
  position: NORMAL
  implementation: IMPL_NO
  description: 部门领导审批录用决策
  
  timing:
    limit: 2
    durationUnit: D
    
  right:
    performType: SINGLE
    performSequence: FIRST
    performerSelectedId: dept_manager_formula
    performerSelectedAtt:
      formula: getDepartmentManager(dept)
      formulaType: EXPRESSION
      
  extendedAttributes:
    requiredSkills:
      - approval_decision
    inputDocuments:
      - interview_result
    outputDocuments:
      - approval_opinion
```

**自动筛选配置推导**:
- **办理人**: 从组织机构获取部门经理 → `getDepartmentManager(dept)`
- **能力配置**:
  - 审批决策能力 → `skill-approval`

#### 活动4: 发Offer

```yaml
- id: send_offer
  name: 发送Offer
  type: TASK
  category: AGENT
  position: NORMAL
  implementation: IMPL_AGENT
  description: 自动发送录用通知邮件
  
  timing:
    limit: 1
    durationUnit: D
    
  agentConfig:
    agentId: offer_agent
    agentName: Offer发送助手
    agentType: LLM_AGENT
    llmModel: gpt-4
    promptTemplate: |
      你是一个HR招聘助手，请根据以下信息生成并发送Offer邮件：
      - 候选人姓名：{{candidateName}}
      - 职位：{{position}}
      - 薪资：{{salary}}
      - 入职日期：{{startDate}}
    maxTokens: 2048
    temperature: 0.7
    
  sceneConfig:
    sceneGroupId: recruitment_scene
    sceneType: AUTO
    capabilities:
      - email_send
      - template_generate
      
  extendedAttributes:
    requiredSkills:
      - email_send
      - template_generate
    inputDocuments:
      - approval_result
    outputDocuments:
      - offer_email
```

**自动筛选配置推导**:
- **办理人**: AI Agent 自动处理
- **能力配置**:
  - 邮件发送能力 → `skill-email-sender`
  - 模板生成能力 → `skill-template-engine`

#### 活动5: 办理入职

```yaml
- id: onboarding
  name: 办理入职手续
  type: TASK
  category: HUMAN
  position: NORMAL
  implementation: IMPL_NO
  description: HR办理新员工入职手续
  
  timing:
    limit: 3
    durationUnit: D
    
  right:
    performType: SINGLE
    performSequence: FIRST
    performerSelectedId: hr_onboarding_formula
    performerSelectedAtt:
      formula: getHRStaff(dept, 'onboarding')
      formulaType: EXPRESSION
      
  extendedAttributes:
    requiredSkills:
      - employee_create
      - system_setup
    inputDocuments:
      - offer_accepted
    outputDocuments:
      - employee_record
```

**自动筛选配置推导**:
- **办理人**: 从组织机构获取 HR 部门入职组人员 → `getHRStaff(dept, 'onboarding')`
- **能力配置**:
  - 员工创建能力 → `skill-employee-manager`
  - 系统配置能力 → `skill-system-setup`

### 2.3 路由定义 (RouteDef)

```yaml
routes:
  - id: route_1
    from: start
    to: hr_screening
    name: 开始到筛选简历
    
  - id: route_2
    from: hr_screening
    to: interview_schedule
    name: 筛选通过到约谈面试
    condition: "screeningResult == 'PASS'"
    
  - id: route_3
    from: interview_schedule
    to: manager_approval
    name: 面试完成到领导审批
    condition: "interviewCompleted == true"
    
  - id: route_4
    from: manager_approval
    to: send_offer
    name: 审批通过到发Offer
    condition: "approvalResult == 'APPROVE'"
    
  - id: route_5
    from: manager_approval
    to: end
    name: 审批拒绝到结束
    condition: "approvalResult == 'REJECT'"
    
  - id: route_6
    from: send_offer
    to: onboarding
    name: Offer接受到办理入职
    condition: "offerAccepted == true"
    
  - id: route_7
    from: onboarding
    to: end
    name: 入职完成到结束
```

---

## 📋 三、现有NLP实现评估

### 3.1 已实现功能

| 功能 | 实现状态 | 支持度 | 说明 |
|------|----------|--------|------|
| **意图识别** | ✅ 已实现 | 80% | 支持8种意图，但缺少能力匹配意图 |
| **流程创建** | ✅ 已实现 | 70% | 可创建基本流程，缺少复杂路由条件 |
| **活动创建** | ✅ 已实现 | 60% | 可创建基本活动，缺少能力自动匹配 |
| **属性更新** | ✅ 已实现 | 50% | 支持基本属性，缺少复杂配置 |
| **智能建议** | ✅ 已实现 | 40% | 有建议框架，缺少深度推理 |
| **流程验证** | ✅ 已实现 | 80% | 基本验证完整 |
| **RAG集成** | ✅ 已实现 | 60% | 有RAG框架，缺少领域知识 |
| **SceneEngine集成** | ✅ 已实现 | 50% | 有集成框架，缺少自动匹配 |

### 3.2 现有NLP能力

```java
// DesignerNlpServiceImpl.java 已实现的能力
public NlpResponse processNaturalLanguage(String userInput, DesignerContextDTO context) {
    // ✅ 意图分析
    List<NlpIntent> intents = analyzeIntent(userInput);
    
    // ✅ 实体提取
    Map<String, Object> entities = extractEntities(userInput, intentType);
    
    // ⚠️ 流程创建（基本）
    ProcessDefDTO process = createProcessFromNlp(description, context);
    
    // ⚠️ 活动创建（基本）
    ActivityDefDTO activity = createActivityFromNlp(description, context);
    
    // ❌ 能力匹配（未实现）
    // List<Capability> capabilities = matchCapabilities(activity);
    
    // ❌ 办理人推导（未实现）
    // String performer = inferPerformer(activity, orgContext);
    
    // ❌ 技能自动配置（未实现）
    // void autoConfigureSkills(activity, capabilities);
}
```

---

## 📋 四、实现差距分析

### 4.1 关键差距

| 差距项 | 描述 | 影响程度 | 实现难度 |
|--------|------|----------|----------|
| **能力自动匹配** | 无法根据活动描述自动匹配所需能力 | 🔴 高 | 中 |
| **办理人推导** | 无法从组织机构自动推导办理人 | 🔴 高 | 高 |
| **技能自动配置** | 无法自动配置活动所需技能 | 🔴 高 | 中 |
| **复杂路由条件** | 无法生成复杂的路由条件表达式 | 🟡 中 | 低 |
| **领域知识库** | 缺少招聘、审批等领域的知识库 | 🟡 中 | 中 |
| **LLM深度推理** | 缺少LLM深度推理和规划能力 | 🟡 中 | 高 |

### 4.2 详细差距分析

#### 差距1: 能力自动匹配

**现状**: 
- ✅ 有 SceneEngineIntegration 接口
- ✅ 有 `discoverCapabilities()` 方法
- ❌ 缺少从活动描述到能力的语义匹配

**需要实现**:
```java
public interface CapabilityMatcher {
    List<CapabilityMatch> matchCapabilities(String activityDescription, 
                                             String activityType,
                                             DesignerContextDTO context);
    
    double calculateRelevance(String description, Capability capability);
    
    List<Capability> rankCapabilities(List<CapabilityMatch> matches);
}
```

#### 差距2: 办理人推导

**现状**:
- ✅ 有权限公式配置
- ❌ 缺少从组织机构自动推导办理人
- ❌ 缺少组织机构上下文

**需要实现**:
```java
public interface PerformerInferencer {
    String inferPerformer(String activityDescription, 
                          String activityType,
                          OrgContext orgContext);
    
    List<User> queryOrgUsers(String formula, OrgContext context);
    
    String generatePerformerFormula(ActivityDefDTO activity, 
                                     OrgContext orgContext);
}
```

#### 差距3: 技能自动配置

**现状**:
- ✅ 有 agentConfig 和 sceneConfig 结构
- ❌ 缺少从能力到技能的自动映射
- ❌ 缺少技能推荐机制

**需要实现**:
```java
public interface SkillAutoConfigurator {
    Map<String, Object> configureAgentSkills(List<Capability> capabilities);
    
    Map<String, Object> configureSceneSkills(List<Capability> capabilities);
    
    List<SkillRecommendation> recommendSkills(ActivityDefDTO activity);
}
```

---

## 📋 五、LLM交互流程推导

### 5.1 交互流程设计

```
┌─────────────────────────────────────────────────────────────┐
│                    LLM 交互流程                              │
│                                                              │
│  1. 用户输入解析                                             │
│     输入: "我要建立一个招聘审批的流程..."                     │
│     输出: {                                                  │
│       intent: "create_process",                              │
│       processName: "招聘审批流程",                            │
│       steps: [                                               │
│         {name: "HR筛选简历", type: "TASK"},                   │
│         {name: "约谈面试", type: "TASK"},                     │
│         {name: "领导审批", type: "TASK"},                     │
│         {name: "发Offer", type: "TASK"},                      │
│         {name: "办理入职手续", type: "TASK"}                  │
│       ]                                                      │
│     }                                                        │
│                                                              │
│  2. 能力匹配请求                                             │
│     输入: 活动描述 + 活动类型                                 │
│     调用: SceneEngine.discoverCapabilities()                 │
│     输出: [                                                  │
│       {capability: "resume_parsing", relevance: 0.95},       │
│       {capability: "email_send", relevance: 0.90},           │
│       {capability: "schedule_management", relevance: 0.85}   │
│     ]                                                        │
│                                                              │
│  3. 办理人推导请求                                           │
│     输入: 活动描述 + 组织上下文                               │
│     调用: OrgManager.queryUsers()                            │
│     输出: {                                                  │
│       formula: "getHRStaff(dept, 'recruitment')",            │
│       candidates: ["user001", "user002"]                     │
│     }                                                        │
│                                                              │
│  4. 技能配置生成                                             │
│     输入: 能力列表 + 技能库                                   │
│     调用: SkillRegistry.findSkills()                         │
│     输出: {                                                  │
│       agentConfig: {...},                                    │
│       sceneConfig: {...}                                     │
│     }                                                        │
│                                                              │
│  5. YAML定义生成                                             │
│     输入: 流程配置数据                                       │
│     输出: 完整的YAML流程定义                                  │
│                                                              │
│  6. 扩展属性生成                                             │
│     输入: 流程上下文                                         │
│     输出: {                                                  │
│       extendedAttributes: {...},                             │
│       metadata: {...}                                        │
│     }                                                        │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 LLM交互内容

#### 交互1: 流程结构生成

**Prompt**:
```
你是一个BPM流程设计专家。请根据以下用户描述生成流程定义：

用户描述：
"我要建立一个招聘审批的流程？第一步 HR 筛选简历，第二步：约谈面试，第三部 领导审批，第四步，发offer 第五步 办理入职手续？"

请输出：
1. 流程基本信息（ID、名称、描述）
2. 活动列表（ID、名称、类型、分类）
3. 路由列表（源活动、目标活动、条件）

输出格式：JSON
```

**期望输出**:
```json
{
  "process": {
    "processDefId": "recruitment_approval",
    "name": "招聘审批流程",
    "description": "候选人招聘全流程审批"
  },
  "activities": [
    {"activityDefId": "hr_screening", "name": "HR筛选简历", "activityType": "TASK", "activityCategory": "HUMAN"},
    {"activityDefId": "interview_schedule", "name": "约谈面试", "activityType": "TASK", "activityCategory": "HUMAN"},
    {"activityDefId": "manager_approval", "name": "领导审批", "activityType": "TASK", "activityCategory": "HUMAN"},
    {"activityDefId": "send_offer", "name": "发Offer", "activityType": "TASK", "activityCategory": "AGENT"},
    {"activityDefId": "onboarding", "name": "办理入职手续", "activityType": "TASK", "activityCategory": "HUMAN"}
  ],
  "routes": [
    {"from": "start", "to": "hr_screening"},
    {"from": "hr_screening", "to": "interview_schedule"},
    {"from": "interview_schedule", "to": "manager_approval"},
    {"from": "manager_approval", "to": "send_offer"},
    {"from": "send_offer", "to": "onboarding"},
    {"from": "onboarding", "to": "end"}
  ]
}
```

#### 交互2: 能力匹配

**Prompt**:
```
你是一个能力匹配专家。请根据以下活动描述匹配所需能力：

活动描述：
- 活动名称：HR筛选简历
- 活动类型：TASK
- 活动分类：HUMAN
- 描述：HR人员筛选候选人简历

可用能力列表：
1. resume_parsing - 简历解析
2. candidate_evaluation - 候选人评估
3. email_send - 邮件发送
4. schedule_management - 日程管理
5. approval_decision - 审批决策

请输出匹配的能力列表及匹配度（0-1）。

输出格式：JSON
```

**期望输出**:
```json
{
  "matches": [
    {"capability": "resume_parsing", "relevance": 0.95, "reason": "活动涉及简历处理"},
    {"capability": "candidate_evaluation", "relevance": 0.90, "reason": "活动涉及候选人评估"}
  ]
}
```

#### 交互3: 办理人推导

**Prompt**:
```
你是一个组织架构专家。请根据以下活动描述推导办理人：

活动描述：
- 活动名称：HR筛选简历
- 活动类型：TASK
- 活动分类：HUMAN

组织架构信息：
- HR部门：张三、李四、王五
- IT部门：赵六、钱七
- 财务部门：孙八、周九

请输出办理人公式及候选人列表。

输出格式：JSON
```

**期望输出**:
```json
{
  "performerFormula": "getHRStaff(dept, 'recruitment')",
  "candidates": ["张三", "李四", "王五"],
  "reason": "活动属于HR招聘流程，应由HR部门招聘组人员处理"
}
```

### 5.3 YAML定义输出

**Prompt**:
```
你是一个YAML配置专家。请根据以下流程配置生成完整的YAML定义：

流程配置：
{
  "process": {...},
  "activities": [...],
  "routes": [...],
  "capabilities": [...],
  "performers": [...]
}

请输出完整的YAML流程定义，包含：
1. 流程基本信息
2. 活动定义（包含timing、right、agentConfig、sceneConfig）
3. 路由定义（包含条件表达式）
4. 扩展属性

输出格式：YAML
```

---

## 📋 六、其他场景推导

### 6.1 采购审批场景

**用户故事**:
> 我要建立一个采购审批流程？第一步 提交采购申请，第二步 部门审批，第三步 财务审核，第四步 采购执行，第五步 验收确认？

**自动匹配需求**:

| 步骤 | 自动匹配能力 | 自动匹配办理人 |
|------|-------------|---------------|
| 提交采购申请 | 表单填写、附件上传 | 申请人 |
| 部门审批 | 审批决策 | 部门经理 |
| 财务审核 | 财务审核、预算检查 | 财务人员 |
| 采购执行 | 采购订单、供应商管理 | 采购人员 |
| 验收确认 | 验收检查、签字确认 | 验收人员 |

### 6.2 报销审批场景

**用户故事**:
> 我要建立一个报销审批流程？第一步 提交报销申请，第二步 主管审批，第三步 财务审核，第四步 打款支付？

**自动匹配需求**:

| 步骤 | 自动匹配能力 | 自动匹配办理人 |
|------|-------------|---------------|
| 提交报销申请 | 表单填写、发票识别 | 申请人 |
| 主管审批 | 审批决策 | 直属主管 |
| 财务审核 | 财务审核、合规检查 | 财务人员 |
| 打款支付 | 支付处理、银行对接 | 财务人员 |

### 6.3 请假审批场景

**用户故事**:
> 我要建立一个请假审批流程？第一步 提交请假申请，第二步 主管审批，第三步 HR备案？

**自动匹配需求**:

| 步骤 | 自动匹配能力 | 自动匹配办理人 |
|------|-------------|---------------|
| 提交请假申请 | 表单填写、日历检查 | 申请人 |
| 主管审批 | 审批决策 | 直属主管 |
| HR备案 | 档案更新、考勤同步 | HR人员 |

---

## 📋 七、完成度支持度报告

### 7.1 现有程序实现能力

| 功能模块 | 实现状态 | 支持度 | 差距 |
|----------|----------|--------|------|
| **NLP意图识别** | ✅ 已实现 | 80% | 缺少能力匹配意图 |
| **流程结构生成** | ✅ 已实现 | 70% | 缺少复杂路由条件 |
| **活动创建** | ✅ 已实现 | 60% | 缺少能力自动匹配 |
| **枚举映射** | ✅ 已实现 | 100% | 无 |
| **RAG集成** | ✅ 已实现 | 60% | 缺少领域知识 |
| **SceneEngine集成** | ✅ 已实现 | 50% | 缺少自动匹配 |
| **能力匹配** | ❌ 未实现 | 0% | 完全缺失 |
| **办理人推导** | ❌ 未实现 | 0% | 完全缺失 |
| **技能自动配置** | ❌ 未实现 | 0% | 完全缺失 |
| **YAML生成** | ❌ 未实现 | 0% | 完全缺失 |

### 7.2 用户故事支持度

| 用户故事需求 | 现有支持度 | 说明 |
|-------------|-----------|------|
| 创建流程结构 | ✅ 70% | 可创建基本流程结构 |
| 创建活动节点 | ✅ 60% | 可创建基本活动 |
| 自动匹配能力 | ❌ 0% | 完全不支持 |
| 自动推导办理人 | ❌ 0% | 完全不支持 |
| 自动配置技能 | ❌ 0% | 完全不支持 |
| 生成YAML定义 | ❌ 0% | 完全不支持 |
| 生成扩展属性 | ⚠️ 30% | 部分支持 |

### 7.3 总体完成度

| 指标 | 完成度 |
|------|--------|
| **基础NLP能力** | 70% |
| **流程设计能力** | 60% |
| **智能匹配能力** | 0% |
| **自动配置能力** | 0% |
| **YAML生成能力** | 0% |
| **总体支持度** | **26%** |

---

## 📋 八、扩展属性LLM辅助生成

### 8.1 可由LLM直接生成的扩展属性

| 扩展属性 | 生成方式 | 示例 |
|----------|----------|------|
| **业务类型** | 从流程名称推导 | `businessType: RECRUITMENT` |
| **优先级** | 从流程描述推导 | `priority: HIGH` |
| **所需技能** | 从活动描述匹配 | `requiredSkills: [resume_parsing]` |
| **输入文档** | 从活动描述推导 | `inputDocuments: [resume_file]` |
| **输出文档** | 从活动描述推导 | `outputDocuments: [candidate_list]` |
| **通知配置** | 从流程类型推导 | `notifications: [email, sms]` |
| **审批规则** | 从流程类型推导 | `approvalRules: {...}` |

### 8.2 LLM生成示例

**输入**:
```
活动：HR筛选简历
描述：HR人员筛选候选人简历，评估候选人是否符合岗位要求
```

**LLM输出**:
```yaml
extendedAttributes:
  businessType: RECRUITMENT
  priority: MEDIUM
  requiredSkills:
    - resume_parsing
    - candidate_evaluation
  inputDocuments:
    - resume_file
    - job_description
  outputDocuments:
    - candidate_list
    - evaluation_report
  notifications:
    - type: email
      trigger: on_complete
      recipients: [manager, hr]
```

---

## 📋 九、实施建议

### 9.1 优先级排序

| 优先级 | 任务 | 预计时间 | 依赖 |
|--------|------|----------|------|
| P0 | 实现能力匹配 | 3天 | SceneEngine |
| P0 | 实现办理人推导 | 3天 | OrgManager |
| P1 | 实现技能自动配置 | 2天 | CapabilityMatcher |
| P1 | 实现YAML生成 | 2天 | 无 |
| P2 | 构建领域知识库 | 5天 | RAG Pipeline |
| P2 | 增强LLM推理 | 5天 | Prompt Engineering |

### 9.2 技术方案

1. **能力匹配**: 基于 RAG 的语义匹配 + 规则匹配
2. **办理人推导**: 组织架构查询 + 规则推理
3. **技能配置**: 能力到技能的映射表 + LLM推荐
4. **YAML生成**: 模板引擎 + LLM填充

---

**报告生成时间**: 2026-04-08  
**报告路径**: E:\github\ooder-skills\docs\bpm-designer\user-story-evaluation-report.md
