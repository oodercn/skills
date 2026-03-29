# 用户故事推导与问题分析

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-09 |
| 所属模块 | skill-scene |
| 状态 | 问题分析 |

---

## 一、用户故事推导

### 1.1 用户故事框架

#### 1.1.1 故事模板

```
作为 [角色]，我想要 [目标]，
以便 [价值]。
但是 [障碍]，
所以 [解决方案]。

验收标准：[验收标准]
```

#### 1.1.2 场景技能用户故事

| 故事ID | 角色 | 目标 | 价值 | 障碍 | 解决方案 | 验收标准 |
|---------|------|------|------|--------|----------|
| **US-001** | 场景管理者 | 管理场景激活流程 | 快速激活场景，减少配置时间 | 不知道如何激活 | 提供激活向导 | 能在5分钟内完成激活 |
| **US-002** | 场景管理者 | 配置场景参数 | 灵活配置场景能力 | 不知道参数含义 | 提供参数说明和示例 | 能理解每个参数的作用 |
| **US-003** | 员工 | 查看团队日志 | 了解团队工作进展 | 看不到团队日志 | 提供团队日志查询 | 能看到所有团队成员的日志 |
| **US-004** | 员工 | 填写日志 | 记录工作内容 | 不知道填写什么 | 提供日志模板和提示 | 能快速填写完整日志 |
| **US-005** | 员工 | 历史查询 | 查看历史记录 | 查询条件不明确 | 提供筛选条件和快捷查询 | 能快速找到需要的历史记录 |
| **US-006** | 员工 | 项目跟踪 | 跟踪项目进度 | 不知道项目状态 | 提供项目状态看板 | 能实时看到项目进度 |
| **US-007** | 员工 | 场景配置 | 配置提醒规则 | 不知道如何配置 | 提供配置向导和示例 | 能配置多种提醒规则 |
| **US-008** | 员工 | AI生成日志 | 使用AI辅助填写 | AI不可用时 | 提供手动填写方式 | 能在AI不可用时手动填写 |
| **US-009** | 员工 | AI分析日志 | 查看AI分析结果 | 不信任AI结果 | 提供分析说明和置信度 | 能理解AI分析的依据 |
| **US-010** | 员工 | 团队汇总 | 查看团队统计 | 统计数据不直观 | 提供可视化图表 | 能直观看到团队统计 |

### 1.2 角色识别与菜单推导

#### 1.2.1 角色识别问题

**问题**：系统如何知道谁是领导？谁是员工？

**推导的用户故事**：

| 故事ID | 用户故事 | 隐含问题 | 技术需求 |
|---------|---------|---------|---------|
| **US-011** | 作为场景管理者，系统应该自动识别我的角色 | 角色识别机制 | 需要角色识别配置 |
| **US-012** | 作为普通员工，系统应该显示我专属的菜单 | 角色识别机制 | 需要角色识别配置 |
| **US-013** | 作为HR，系统应该显示HR专属功能 | 角色识别机制 | 需要角色识别配置 |

**技术实现**：

```yaml
# 角色识别配置
roleDetection:
  enabled: true
  detectionMethod: "config-based"  # config-based | attribute-based | ai-based
  
  configBased:
    configKey: "user.role"
    defaultRole: "EMPLOYEE"
    roleMapping:
      - role: "MANAGER"
        configValue: "manager"
      - role: "HR"
        configValue: "hr"
      - role: "EMPLOYEE"
        configValue: "employee"
        
  attributeBased:
    attributes:
      - name: "department"
        roles:
          MANAGER: "所有部门"
          HR: ["技术部", "人事部"]
          EMPLOYEE: ["技术部"]
      - name: "position"
        roles:
          MANAGER: "所有职位"
          HR: ["部门经理"]
          EMPLOYEE: ["工程师", "专员"]
      - name: "reportingLine"
        roles:
          MANAGER: "所有汇报线"
          HR: ["技术汇报线"]
          EMPLOYEE: ["技术汇报线"]
          
  aiBased:
    enabled: false
    model: "role-classification"
    confidenceThreshold: 0.9
```

#### 1.2.2 菜单配置问题

**问题**：菜单默认会出现在哪？如何配置？

**推导的用户故事**：

| 故事ID | 用户故事 | 隐含问题 | 技术需求 |
|---------|---------|---------|---------|
| **US-014** | 作为场景管理者，我想配置菜单项 | 菜单配置机制 | 需要菜单配置API |
| **US-015** | 作为场景管理者，我想配置菜单的显示顺序 | 菜单排序机制 | 需要菜单排序配置 |
| **US-016** | 作为场景管理者，我想隐藏某些菜单项 | 菜单可见性控制 | 需要菜单权限控制 |
| **US-017** | 作为普通员工，我希望菜单项根据我的角色动态显示 | 动态菜单机制 | 需要动态菜单生成 |
| **US-018** | 作为普通员工，我希望菜单项有图标 | 菜单图标配置 | 需要菜单图标配置 |

**技术实现**：

```yaml
# 菜单配置扩展
menuConfig:
  # 基础菜单配置
  roles:
    - role: "MANAGER"
      items:
        - id: "dashboard"
          name: "管理看板"
          icon: "ri-dashboard-line"
          url: "/console/pages/daily-report-dashboard.html"
          order: 1
          visible: true
        - id: "team-logs"
          name: "团队日志"
          icon: "ri-team-line"
          url: "/console/pages/daily-report-team.html"
          order: 2
          visible: true
          
    - role: "EMPLOYEE"
      items:
        - id: "my-log"
          name: "我的日志"
          icon: "ri-edit-line"
          url: "/console/pages/daily-report-form.html"
          order: 1
          visible: true
        - id: "history"
          name: "历史记录"
          icon: "ri-history-line"
          url: "/console/pages/daily-report-history.html"
          order: 2
          visible: true
          
  # 动态菜单配置
  dynamicMenus:
    - id: "project-tracking"
      name: "项目跟踪"
      icon: "ri-line-chart-line"
      url: "/console/pages/daily-report-tracking.html"
      roles: ["MANAGER"]
      conditions:
        - configKey: "feature.project-tracking"
          value: true
          
  # 菜单权限控制
  menuPermissions:
    - id: "config"
      name: "场景配置"
      roles: ["MANAGER"]
      required: true
      
    - id: "team-logs"
      name: "团队日志"
      roles: ["MANAGER", "HR"]
      required: true
```

### 1.3 LLM模型差异问题

**问题**：用户的LLM可是不同的模型，如何组织语言让LLM来决策？

**推导的用户故事**：

| 故事ID | 用户故事 | 隐含问题 | 技术需求 |
|---------|---------|---------|---------|
| **US-019** | 作为场景管理者，我想为不同用户配置不同的LLM模型 | LLM模型配置 | 需要LLM模型配置API |
| **US-020** | 作为场景管理者，我想配置不同模型的提示词模板 | 提示词模板配置 | 需要提示词模板管理 |
| **US-021** | 作为普通用户，我希望使用更强大的模型 | 模型升级机制 | 需要模型升级流程 |
| **US-022** | 作为普通用户，我希望知道当前使用的模型 | 模型透明度 | 需要模型信息展示 |

**技术实现**：

```yaml
# LLM模型配置
llmConfig:
  # 多模型支持
  enabled: true
  
  # 模型列表
  models:
    - id: "gpt-4"
      name: "GPT-4"
      provider: "openai"
      capabilities:
        - chat
        - completion
        - embedding
      cost: 0.03
      maxTokens: 8192
      recommended: true
      
    - id: "ernie-bot-4"
      name: "文心一言"
      provider: "baichuan"
      capabilities:
        - chat
        - completion
      cost: 0.008
      maxTokens: 4096
      recommended: false
      
    - id: "qwen-turbo"
      name: "通义千问"
      provider: "alibaba"
      capabilities:
        - chat
        - completion
      cost: 0.002
      maxTokens: 6144
      recommended: false
      
  # 提示词模板管理
  promptTemplates:
    - id: "daily-report-generate"
      name: "日志生成"
      template: |
        你是一个专业的日志助手，请根据以下信息生成工作日志：
        - 工作内容：{workContent}
        - 工作时长：{workDuration}
        - 工作成果：{workResults}
        - 遇到的问题：{issues}
        - 解决方案：{solutions}
        要求：使用专业、简洁的语言，突出重点
        
    - id: "daily-report-analyze"
      name: "日志分析"
      template: |
        请分析以下工作日志，识别关键问题和改进点：
        - 效率指标：{efficiencyMetrics}
        - 质量问题：{qualityIssues}
        - 工作模式：{workPatterns}
        - 改进建议：{improvements}
        要求：提供数据支撑，给出可操作的建议
        
  # 模型分配策略
  modelAssignment:
    strategy: "cost-optimized"  # cost-optimized | performance-optimized | user-preference
    rules:
      - condition: "user.role == 'MANAGER'"
        model: "gpt-4"
        reason: "管理者需要更强大的模型"
      - condition: "user.role == 'EMPLOYEE'"
        model: "ernie-bot-4"
        reason: "员工使用性价比高的模型"
      - condition: "task.complexity == 'high'"
        model: "gpt-4"
        reason: "复杂任务需要更强大的模型"
```

### 1.4 工具调用问题

**问题**：需要默认哪些工具方便LLM来调用？

**推导的用户故事**：

| 故事ID | 用户故事 | 隐含问题 | 技术需求 |
|---------|---------|---------|---------|
| **US-023** | 作为场景管理者，我想让LLM能够调用外部API | 工具调用机制 | 需要工具调用API |
| **US-024** | 作为场景管理者，我想让LLM能够查询数据库 | 数据库查询工具 | 需要数据库查询工具 |
| **US-025** | 作为场景管理者，我想让LLM能够发送MQTT消息 | MQTT工具 | 需要MQTT工具 |
| **US-026** | 作为场景管理者，我想让LLM能够读取文件 | 文件读取工具 | 需要文件读取工具 |
| **US-027** | 作为普通用户，我希望LLM能够调用我的私有能力 | 私有能力调用 | 需要私有能力API |
| **US-028** | 作为普通用户，我希望LLM能够调用知识库 | 知识库调用 | 需要知识库API |

**技术实现**：

```yaml
# 工具调用配置
toolCapabilities:
  enabled: true
  
  # 工具定义
  tools:
    - id: "http-request"
      name: "HTTP请求"
      type: "network"
      description: "发送HTTP请求到外部API"
      parameters:
        - name: "url"
          type: "string"
          required: true
        - name: "method"
          type: "string"
          required: true
        - name: "headers"
          type: "object"
          required: false
        - name: "body"
          type: "object"
          required: false
        - name: "timeout"
          type: "integer"
          default: 5000
          
    - id: "database-query"
      name: "数据库查询"
      type: "data"
      description: "查询数据库"
      parameters:
        - name: "query"
          type: "string"
          required: true
        - name: "limit"
          type: "integer"
          default: 100
          
    - id: "mqtt-publish"
      name: "MQTT发布"
      type: "messaging"
      description: "发布MQTT消息"
      parameters:
        - name: "topic"
          type: "string"
          required: true
        - name: "payload"
          type: "object"
          required: true
        - name: "qos"
          type: "integer"
          default: 1
          
    - id: "file-read"
      name: "文件读取"
      type: "storage"
      description: "读取存储文件"
      parameters:
        - name: "path"
          type: "string"
          required: true
        - name: "encoding"
          type: "string"
          default: "utf-8"
          
  # 工具权限控制
  toolPermissions:
    - id: "http-request"
      roles: ["MANAGER"]
      required: true
      description: "外部API调用需要管理者权限"
      
    - id: "database-query"
      roles: ["MANAGER", "HR"]
      required: true
      description: "数据库查询需要管理者或HR权限"
      
    - id: "mqtt-publish"
      roles: ["MANAGER"]
      required: true
      description: "MQTT发布需要管理者权限"
      
    - id: "file-read"
      roles: ["MANAGER", "EMPLOYEE"]
      required: false
      description: "文件读取权限默认开放"
```

---

## 二、业务问题讨论清单

### 2.1 角色识别与菜单配置

| 问题ID | 问题描述 | 影响范围 | 优先级 | 讨论点 |
|---------|---------|---------|--------|----------|
| **BIZ-001** | 系统如何知道谁是领导？谁是员工？ | 所有场景技能 | P0 | 需要角色识别机制 |
| **BIZ-002** | 菜单默认会出现在哪？如何配置？ | 所有场景技能 | P0 | 需要菜单配置API |
| **BIZ-003** | 菜单项如何排序？ | 所有场景技能 | P1 | 需要菜单排序配置 |
| **BIZ-004** | 菜单项如何动态显示？ | 所有场景技能 | P1 | 需要动态菜单机制 |
| **BIZ-005** | 菜单项如何隐藏？ | 所有场景技能 | P1 | 需要菜单权限控制 |
| **BIZ-006** | 不同角色看到不同菜单？ | 所有场景技能 | P1 | 需要角色识别配置 |

### 2.2 LLM模型差异

| 问题ID | 问题描述 | 影响范围 | 优先级 | 讨论点 |
|---------|---------|---------|--------|----------|
| **BIZ-007** | 用户的LLM可是不同的模型，如何组织语言让LLM来决策？ | LLM对话场景 | P0 | 需要LLM模型配置API |
| **BIZ-008** | 如何配置不同模型的提示词？ | LLM对话场景 | P0 | 需要提示词模板管理 |
| **BIZ-009** | 模型升级如何处理？ | LLM对话场景 | P1 | 需要模型升级流程 |
| **BIZ-010** | 如何让用户知道当前使用的模型？ | LLM对话场景 | P1 | 需要模型透明度展示 |

### 2.3 工具调用

| 问题ID | 问题描述 | 影响范围 | 优先级 | 讨论点 |
|---------|---------|---------|--------|----------|
| **BIZ-011** | 需要默认哪些工具方便LLM来调用？ | 所有场景技能 | P0 | 需要工具调用机制 |
| **BIZ-012** | 工具调用如何鉴权？ | 所有场景技能 | P1 | 需要工具权限控制 |
| **BIZ-013** | 工具调用如何降级？ | 所有场景技能 | P1 | 需要工具降级策略 |
| **BIZ-014** | 工具调用如何监控？ | 所有场景技能 | P2 | 需要工具调用监控 |

---

## 三、技术问题讨论清单

### 3.1 LLM模型差异

| 问题ID | 问题描述 | 影响范围 | 优先级 | 讨论点 |
|---------|---------|---------|--------|----------|
| **TECH-001** | LLM模型配置存储在哪里？ | skill-llm-conversation | P0 | 需要统一LLM配置存储 |
| **TECH-002** | 如何在场景技能中引用LLM模型？ | skill.yaml dependencies | P0 | 需要LLM模型引用规范 |
| **TECH-003** | LLM降级时如何选择模型？ | 降级策略 | P0 | 需要模型选择算法 |
| **TECH-004** | 提示词模板如何管理？ | 提示词模板 | P1 | 需要提示词模板CRUD |

### 3.2 工具调用

| 问题ID | 问题描述 | 影响范围 | 优先级 | 讨论点 |
|---------|---------|---------|--------|----------|
| **TECH-005** | 工具调用API如何定义？ | 工具调用机制 | P0 | 需要工具API规范 |
| **TECH-006** | 工具调用如何与CapabilityBinding集成？ | CapabilityBinding | P0 | 需要工具与能力绑定集成 |
| **TECH-007** | 工具调用如何记录日志？ | 工具调用日志 | P1 | 需要工具调用日志记录 |

---

## 四、实施建议

### 4.1 第一阶段：角色识别与菜单配置

| 任务 | 优先级 | 工作量 | 说明 |
|------|--------|--------|------|
| 实现角色识别配置 | P0 | 2周 | 支持config-based和attribute-based两种方式 |
| 实现菜单配置API | P0 | 2周 | 支持菜单CRUD和排序 |
| 实现动态菜单机制 | P1 | 3周 | 支持条件菜单显示 |
| 实现菜单权限控制 | P1 | 2周 | 支持基于角色的权限控制 |

### 4.2 第二阶段：LLM模型管理

| 任务 | 优先级 | 工作量 | 说明 |
|------|--------|--------|------|
| 实现LLM模型配置API | P0 | 2周 | 支持多模型配置和切换 |
| 实现提示词模板管理 | P0 | 2周 | 支持模板CRUD和版本管理 |
| 实现模型分配策略 | P1 | 3周 | 支持基于角色和任务的模型分配 |
| 实现模型透明度展示 | P2 | 1周 | 在UI中显示当前使用的模型 |

### 4.3 第三阶段：工具调用机制

| 任务 | 优先级 | 工作量 | 说明 |
|------|--------|--------|------|
| 设计工具调用API规范 | P0 | 2周 | 定义工具接口和参数规范 |
| 实现工具权限控制 | P0 | 2周 | 支持基于角色的工具权限 |
| 实现工具降级策略 | P1 | 3周 | 支持LLM不可用时的工具降级 |
| 实现工具调用监控 | P2 | 1周 | 记录工具调用日志和性能指标 |

---

## 五、验收标准

### 5.1 角色识别与菜单

- [ ] 用户登录后，系统能够正确识别其角色
- [ ] 不同角色用户看到的菜单项符合其角色权限
- [ ] 管理者能够配置菜单项的显示顺序
- [ ] 管理者能够隐藏或显示特定菜单项
- [ ] 菜单项支持图标显示

### 5.2 LLM模型管理

- [ ] 管理者能够配置多个LLM模型
- [ ] 管理者能够为不同模型配置提示词模板
- [ ] 系统能够根据用户角色分配不同的LLM模型
- [ ] 用户能够看到当前使用的LLM模型
- [ ] LLM不可用时，系统能够自动降级到基础功能

### 5.3 工具调用

- [ ] 管理者能够配置可用的工具
- [ ] 管理者能够设置工具的权限控制
- [ ] LLM能够通过工具调用外部API
- [ ] 工具调用有日志记录
- [ ] LLM不可用时，工具调用能够自动降级

---

## 六、风险与挑战

### 6.1 技术风险

| 风险 | 影响 | 缓解措施 |
|------|------|--------|----------|
| 角色识别准确性 | 用户体验 | 多种识别方式结合，提高准确率 |
| LLM模型切换 | 服务稳定性 | 灰度切换，避免服务中断 |
| 工具调用安全性 | 数据安全 | 严格的权限控制和审计日志 |
| 工具降级复杂性 | 系统复杂度 | 简化降级逻辑，提高可维护性 |

### 6.2 业务风险

| 风险 | 影响 | 缓解措施 |
|------|------|--------|----------|
| 配置复杂度 | 用户体验 | 提供配置向导和默认值 |
| 多角色场景 | 管理复杂度 | 明确角色职责，避免权限混乱 |
| LLM成本控制 | 成本 | 模型分配策略，优化成本 |

---

**文档状态**: 问题分析  
**下一步**: 根据讨论清单进行技术方案设计
