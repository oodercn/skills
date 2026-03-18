# LLM确定性增强假设分析

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-09 |
| 所属模块 | skill-scene |
| 状态 | 假设分析 |

---

## 一、核心假设

### 1.1 假设目标

**目标**：最大程度增加LLM的确定性结果

**原因**：
- LLM输出具有随机性，需要约束其输出范围
- 场景技能安装需要精确的配置，不能有歧义
- 用户期望一致的行为，不希望每次安装结果不同

### 1.2 假设方法

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    LLM确定性增强方法                                            │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  方法1：知识库约束                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  • 预制领域知识，限制LLM回答范围                                          │    │
│  │  • 提供标准答案模板，减少LLM创造性输出                                    │    │
│  │  • 使用RAG检索相关上下文，增强回答准确性                                  │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  方法2：向量库检索                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  • 预制向量数据，提供相似案例参考                                        │    │
│  │  • 语义检索相关配置，减少LLM猜测                                        │    │
│  │  • 提供历史安装记录，增强决策依据                                        │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  方法3：工具调用约束                                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  • 预定义工具接口，限制LLM调用范围                                        │    │
│  │  • 参数校验，防止LLM生成无效参数                                        │    │
│  │  • 结果验证，确保LLM输出符合预期                                        │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  方法4：结构化输出                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  • 强制LLM输出JSON/YAML格式                                              │    │
│  │  • 定义输出Schema，验证输出结构                                          │    │
│  │  • 提供输出模板，减少格式错误                                            │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、向量库假设

### 2.1 向量库作用

| 作用 | 说明 | 确定性提升 |
|------|------|-----------|
| **语义检索** | 根据用户输入检索相似案例 | 高 |
| **配置匹配** | 匹配历史成功配置 | 高 |
| **问题诊断** | 匹配历史问题和解决方案 | 中 |
| **推荐增强** | 提供相似场景的推荐 | 中 |

### 2.2 预制向量数据假设

```yaml
VectorDatabase:
  # ==================== 平台向量数据 ====================
  platform:
    # 安装场景向量
    installation:
      - id: "install-scene-skill"
        embedding: "[向量数据]"
        content: |
          场景技能安装流程：
          1. 检查依赖服务
          2. 配置场景参数
          3. 初始化数据库
          4. 注册能力
          5. 绑定能力到场景组
          6. 生成菜单
        metadata:
          type: "workflow"
          category: "installation"
          keywords: ["安装", "场景技能", "流程"]
          
      - id: "install-dependency-check"
        embedding: "[向量数据]"
        content: |
          依赖检查流程：
          - 数据库：检查连接，自动创建表
          - MQTT：检查连接，自动订阅主题
          - Redis：检查连接，可选降级
          - LLM：检查可用性，可选降级
        metadata:
          type: "workflow"
          category: "installation"
          keywords: ["依赖", "检查", "数据库", "MQTT"]
          
    # 配置场景向量
    configuration:
      - id: "config-llm-model"
        embedding: "[向量数据]"
        content: |
          LLM模型配置：
          - gpt-4：推荐用于管理者，高精度
          - ernie-bot-4：推荐用于员工，性价比高
          - qwen-turbo：推荐用于简单任务，成本低
        metadata:
          type: "config"
          category: "llm"
          keywords: ["LLM", "模型", "配置"]
          
      - id: "config-role-menu"
        embedding: "[向量数据]"
        content: |
          角色菜单配置：
          - MANAGER：管理看板、团队日志、场景配置
          - EMPLOYEE：我的日志、历史记录
          - HR：团队日志、人员统计
        metadata:
          type: "config"
          category: "menu"
          keywords: ["角色", "菜单", "配置"]
          
  # ==================== 场景向量数据 ====================
  scene:
    # 日志汇报场景
    dailyReport:
      - id: "daily-report-config"
        embedding: "[向量数据]"
        content: |
          日志汇报场景配置：
          - 提醒时间：默认16:30
          - 提醒方式：MQTT优先，邮件可选
          - AI生成：需要LLM支持
          - 团队汇总：每日9:00自动生成
        metadata:
          type: "scene-config"
          sceneId: "skill-daily-report"
          keywords: ["日志", "汇报", "配置"]
          
      - id: "daily-report-activation"
        embedding: "[向量数据]"
        content: |
          日志汇报激活步骤：
          MANAGER：
          1. 选择参与者
          2. 配置提醒时间
          3. 配置LLM（可选）
          4. 确认激活
          EMPLOYEE：
          1. 接受邀请
          2. 确认加入
        metadata:
          type: "activation"
          sceneId: "skill-daily-report"
          keywords: ["日志", "激活", "步骤"]
          
    # 招聘场景
    recruitment:
      - id: "recruitment-config"
        embedding: "[向量数据]"
        content: |
          招聘场景配置：
          - 职位管理：支持多职位
          - 简历筛选：AI辅助，需LLM
          - 面试流程：可配置轮次
          - 评估标准：自定义维度
        metadata:
          type: "scene-config"
          sceneId: "skill-recruitment"
          keywords: ["招聘", "配置", "简历"]
          
  # ==================== 问题诊断向量 ====================
  troubleshooting:
    - id: "llm-unavailable"
      embedding: "[向量数据]"
      content: |
        问题：LLM服务不可用
        原因：
        1. API Key无效
        2. 网络连接问题
        3. 服务限流
        解决方案：
        1. 检查API Key配置
        2. 检查网络连接
        3. 等待限流恢复或切换模型
        降级策略：禁用AI功能，使用手动输入
      metadata:
        type: "troubleshooting"
        category: "llm"
        keywords: ["LLM", "不可用", "降级"]
        
    - id: "database-connection-failed"
      embedding: "[向量数据]"
      content: |
        问题：数据库连接失败
        原因：
        1. 数据库服务未启动
        2. 连接参数错误
        3. 权限不足
        解决方案：
        1. 启动数据库服务
        2. 检查连接参数
        3. 检查用户权限
      metadata:
        type: "troubleshooting"
        category: "database"
        keywords: ["数据库", "连接", "失败"]
```

### 2.3 向量库使用场景

| 场景 | 查询方式 | 返回结果 | 确定性提升 |
|------|---------|---------|-----------|
| 安装流程推荐 | 语义相似度 | Top 3相似案例 | 高 |
| 配置参数推荐 | 场景ID匹配 | 标准配置模板 | 高 |
| 问题诊断 | 错误信息匹配 | 解决方案 | 中 |
| 功能推荐 | 用户需求匹配 | 功能列表 | 中 |

---

## 三、知识库假设

### 3.1 知识库作用

| 作用 | 说明 | 确定性提升 |
|------|------|-----------|
| **上下文增强** | 提供领域知识，减少LLM幻觉 | 高 |
| **标准答案** | 提供标准答案模板，减少创造性输出 | 高 |
| **约束输出** | 定义输出格式和范围 | 高 |
| **解释说明** | 提供配置说明，减少歧义 | 中 |

### 3.2 预制知识数据假设

```yaml
KnowledgeBase:
  # ==================== 平台知识 ====================
  platform:
    # 安装知识
    installation:
      - id: "installation-overview"
        title: "场景技能安装概述"
        content: |
          # 场景技能安装概述
          
          场景技能安装分为两种模式：
          
          ## LLM主导模式
          - 适用条件：LLM服务可用
          - 特点：智能推荐、个性化配置
          - 流程：LLM分析 → 配置推荐 → 自动安装
          
          ## 传统模式
          - 适用条件：LLM服务不可用
          - 特点：规则驱动、手动配置
          - 流程：依赖检查 → 配置填写 → 标准安装
          
        metadata:
          type: "document"
          category: "installation"
          
      - id: "installation-prerequisites"
        title: "安装前置条件"
        content: |
          # 安装前置条件
          
          ## 必需依赖
          | 依赖 | 说明 | 检查方法 |
          |------|------|---------|
          | 数据库 | 数据存储 | 连接测试 |
          | MQTT | 消息推送 | 连接测试 |
          
          ## 可选依赖
          | 依赖 | 说明 | 降级方案 |
          |------|------|---------|
          | Redis | 缓存服务 | 内存缓存 |
          | LLM | AI服务 | 禁用AI功能 |
          
        metadata:
          type: "document"
          category: "installation"
          
    # 配置知识
    configuration:
      - id: "config-parameters"
        title: "配置参数说明"
        content: |
          # 配置参数说明
          
          ## 基础配置
          - sceneName: 场景名称，用于显示和识别
          - sceneDescription: 场景描述，用于说明场景用途
          
          ## 角色配置
          - MANAGER: 场景管理者，拥有完整权限
          - EMPLOYEE: 普通员工，拥有基本权限
          
          ## 功能配置
          - dailyRemind: 每日提醒，需要配置提醒时间
          - aiGenerate: AI生成，需要LLM支持
          
        metadata:
          type: "document"
          category: "configuration"
          
      - id: "config-llm-models"
        title: "LLM模型选择指南"
        content: |
          # LLM模型选择指南
          
          ## 模型对比
          | 模型 | 精度 | 成本 | 推荐场景 |
          |------|------|------|---------|
          | GPT-4 | 高 | 高 | 管理者、复杂任务 |
          | 文心一言 | 中 | 低 | 员工、简单任务 |
          | 通义千问 | 中 | 低 | 批量处理 |
          
          ## 选择建议
          - 管理者推荐GPT-4，确保决策质量
          - 员工推荐文心一言，平衡成本和效果
          - 批量任务推荐通义千问，降低成本
          
        metadata:
          type: "document"
          category: "llm"
          
  # ==================== 场景知识 ====================
  scene:
    # 日志汇报场景
    dailyReport:
      - id: "daily-report-user-guide"
        title: "日志汇报使用指南"
        content: |
          # 日志汇报使用指南
          
          ## 功能说明
          日志汇报场景用于团队日常日志管理，支持：
          - 日志填写和提交
          - AI智能生成
          - 团队汇总统计
          - 定时提醒
          
          ## 角色权限
          - 管理者：查看团队日志、配置场景、生成汇总
          - 员工：填写日志、查看历史
          
        metadata:
          type: "document"
          sceneId: "skill-daily-report"
          
      - id: "daily-report-faq"
        title: "日志汇报常见问题"
        content: |
          # 日志汇报常见问题
          
          ## Q: 如何配置提醒时间？
          A: 在场景配置中设置提醒时间，默认16:30。
          
          ## Q: AI生成不可用怎么办？
          A: 检查LLM服务是否可用，或手动填写日志。
          
          ## Q: 如何查看团队日志？
          A: 管理者可在"团队日志"菜单查看所有成员日志。
          
        metadata:
          type: "faq"
          sceneId: "skill-daily-report"
          
  # ==================== 提示词模板 ====================
  prompts:
    - id: "analyze-scene-requirements"
      title: "分析场景需求"
      template: |
        你是一个专业的场景技能安装助手。
        
        ## 任务
        分析用户的需求和场景技能的配置，推荐最优的安装配置。
        
        ## 输入
        - 场景技能信息：{skillInfo}
        - 用户需求：{userRequirements}
        - 技术环境：{techEnvironment}
        
        ## 输出格式
        ```json
        {
          "requiredDependencies": ["database", "mqtt"],
          "recommendedModel": "gpt-4",
          "recommendedConfig": {
            "sceneName": "日志汇报",
            "dailyRemind": true,
            "remindTime": "16:30"
          },
          "potentialIssues": ["LLM服务可能不可用"],
          "solutions": ["准备降级方案"]
        }
        ```
        
        ## 约束
        1. 只推荐已验证的配置
        2. 必须提供降级方案
        3. 输出必须是有效的JSON格式
        
      metadata:
        type: "prompt"
        category: "installation"
        
    - id: "recommend-config"
      title: "推荐配置"
      template: |
        你是一个专业的配置推荐助手。
        
        ## 任务
        根据用户的具体情况，推荐最优的配置参数。
        
        ## 知识库上下文
        {knowledgeContext}
        
        ## 向量库参考
        {vectorContext}
        
        ## 输入
        - 用户角色：{userRole}
        - 团队规模：{teamSize}
        - 使用频率：{usageFrequency}
        
        ## 输出格式
        ```json
        {
          "config": {
            "remindTime": "16:30",
            "remindChannels": ["mqtt"],
            "aiGenerate": true
          },
          "reasons": {
            "remindTime": "默认下班前30分钟",
            "remindChannels": "MQTT推送更及时",
            "aiGenerate": "团队规模适中，推荐使用AI辅助"
          }
        }
        ```
        
      metadata:
        type: "prompt"
        category: "configuration"
```

### 3.3 知识库使用场景

| 场景 | 检索方式 | 返回结果 | 确定性提升 |
|------|---------|---------|-----------|
| 安装引导 | RAG检索 | 相关文档片段 | 高 |
| 配置说明 | 关键词匹配 | 配置说明文档 | 高 |
| 问题解答 | 语义检索 | FAQ答案 | 中 |
| 提示词增强 | 模板填充 | 完整提示词 | 高 |

---

## 四、工具假设

### 4.1 工具作用

| 作用 | 说明 | 确定性提升 |
|------|------|-----------|
| **数据获取** | 获取实时数据，减少LLM猜测 | 高 |
| **操作执行** | 执行具体操作，确保结果正确 | 高 |
| **状态检查** | 检查系统状态，提供准确信息 | 高 |
| **结果验证** | 验证LLM输出，防止错误 | 高 |

### 4.2 预制工具假设

```yaml
ToolLibrary:
  # ==================== 默认工具 ====================
  default:
    # 系统工具
    system:
      - id: "check-dependency"
        name: "依赖检查"
        description: "检查系统依赖是否满足"
        type: "system"
        parameters:
          - name: "dependencyId"
            type: "string"
            required: true
            enum: ["database", "mqtt", "redis", "llm"]
        returns:
          - name: "status"
            type: "string"
            enum: ["available", "unavailable", "degraded"]
          - name: "message"
            type: "string"
        example:
          input: {"dependencyId": "database"}
          output: {"status": "available", "message": "数据库连接正常"}
          
      - id: "get-system-info"
        name: "获取系统信息"
        description: "获取系统运行环境信息"
        type: "system"
        parameters: []
        returns:
          - name: "os"
            type: "string"
          - name: "memory"
            type: "integer"
          - name: "diskSpace"
            type: "integer"
        example:
          input: {}
          output: {"os": "Linux", "memory": 8192, "diskSpace": 102400}
          
    # 数据工具
    data:
      - id: "query-config"
        name: "查询配置"
        description: "查询场景配置信息"
        type: "data"
        parameters:
          - name: "sceneId"
            type: "string"
            required: true
          - name: "configKey"
            type: "string"
            required: false
        returns:
          - name: "config"
            type: "object"
        example:
          input: {"sceneId": "skill-daily-report"}
          output: {"config": {"remindTime": "16:30", "aiGenerate": true}}
          
      - id: "save-config"
        name: "保存配置"
        description: "保存场景配置信息"
        type: "data"
        parameters:
          - name: "sceneId"
            type: "string"
            required: true
          - name: "config"
            type: "object"
            required: true
        returns:
          - name: "success"
            type: "boolean"
          - name: "message"
            type: "string"
        example:
          input: {"sceneId": "skill-daily-report", "config": {"remindTime": "17:00"}}
          output: {"success": true, "message": "配置保存成功"}
          
    # 网络工具
    network:
      - id: "http-get"
        name: "HTTP GET请求"
        description: "发送HTTP GET请求"
        type: "network"
        parameters:
          - name: "url"
            type: "string"
            required: true
          - name: "headers"
            type: "object"
            required: false
        returns:
          - name: "status"
            type: "integer"
          - name: "body"
            type: "object"
        example:
          input: {"url": "https://api.example.com/data"}
          output: {"status": 200, "body": {"data": "value"}}
          
      - id: "http-post"
        name: "HTTP POST请求"
        description: "发送HTTP POST请求"
        type: "network"
        parameters:
          - name: "url"
            type: "string"
            required: true
          - name: "body"
            type: "object"
            required: true
          - name: "headers"
            type: "object"
            required: false
        returns:
          - name: "status"
            type: "integer"
          - name: "body"
            type: "object"
          
    # 消息工具
    messaging:
      - id: "send-notification"
        name: "发送通知"
        description: "发送通知消息给用户"
        type: "messaging"
        parameters:
          - name: "userId"
            type: "string"
            required: true
          - name: "message"
            type: "string"
            required: true
          - name: "channels"
            type: "array"
            required: false
            default: ["mqtt"]
        returns:
          - name: "success"
            type: "boolean"
          - name: "deliveredChannels"
            type: "array"
        example:
          input: {"userId": "user123", "message": "安装完成", "channels": ["mqtt", "email"]}
          output: {"success": true, "deliveredChannels": ["mqtt"]}
          
  # ==================== 扩展工具 ====================
  extension:
    # LLM工具
    llm:
      - id: "llm-chat"
        name: "LLM对话"
        description: "与LLM进行对话"
        type: "llm"
        parameters:
          - name: "prompt"
            type: "string"
            required: true
          - name: "model"
            type: "string"
            required: false
            default: "gpt-4"
          - name: "temperature"
            type: "number"
            required: false
            default: 0.7
        returns:
          - name: "response"
            type: "string"
          - name: "tokens"
            type: "integer"
          
      - id: "llm-structured-output"
        name: "LLM结构化输出"
        description: "获取LLM的结构化输出"
        type: "llm"
        parameters:
          - name: "prompt"
            type: "string"
            required: true
          - name: "schema"
            type: "object"
            required: true
          - name: "model"
            type: "string"
            required: false
        returns:
          - name: "data"
            type: "object"
        example:
          input:
            prompt: "推荐日志汇报场景的配置"
            schema:
              type: "object"
              properties:
                remindTime: {type: "string"}
                aiGenerate: {type: "boolean"}
          output:
            data: {remindTime: "16:30", aiGenerate: true}
            
    # 向量库工具
    vector:
      - id: "vector-search"
        name: "向量检索"
        description: "在向量库中检索相似内容"
        type: "vector"
        parameters:
          - name: "query"
            type: "string"
            required: true
          - name: "collection"
            type: "string"
            required: true
          - name: "topK"
            type: "integer"
            required: false
            default: 3
        returns:
          - name: "results"
            type: "array"
        example:
          input: {"query": "如何配置日志提醒", "collection": "scene", "topK": 3}
          output: {"results": [{"content": "...", "score": 0.95}]}
          
    # 知识库工具
    knowledge:
      - id: "knowledge-query"
        name: "知识库查询"
        description: "在知识库中查询相关内容"
        type: "knowledge"
        parameters:
          - name: "query"
            type: "string"
            required: true
          - name: "knowledgeBase"
            type: "string"
            required: true
          - name: "topK"
            type: "integer"
            required: false
            default: 5
        returns:
          - name: "results"
            type: "array"
        example:
          input: {"query": "安装流程", "knowledgeBase": "platform-docs"}
          output: {"results": [{"title": "...", "content": "..."}]}
```

### 4.3 工具使用场景

| 场景 | 工具组合 | 确定性提升 |
|------|---------|-----------|
| 依赖检查 | check-dependency + get-system-info | 高 |
| 配置推荐 | vector-search + knowledge-query + llm-structured-output | 高 |
| 安装执行 | save-config + send-notification | 高 |
| 问题诊断 | vector-search + knowledge-query | 中 |

---

## 五、确定性增强总结

### 5.1 增强方法对比

| 方法 | 实现成本 | 确定性提升 | 适用场景 |
|------|---------|-----------|---------|
| **知识库约束** | 中 | 高 | 所有场景 |
| **向量库检索** | 高 | 高 | 配置推荐、问题诊断 |
| **工具调用约束** | 中 | 高 | 操作执行、状态检查 |
| **结构化输出** | 低 | 高 | 所有LLM输出 |
| **提示词工程** | 低 | 中 | 所有LLM交互 |

### 5.2 推荐组合

```yaml
Recommendation:
  # 安装场景
  installation:
    - method: "知识库约束"
      weight: 0.3
      reason: "提供安装流程知识"
    - method: "向量库检索"
      weight: 0.3
      reason: "匹配相似安装案例"
    - method: "工具调用约束"
      weight: 0.2
      reason: "确保操作正确"
    - method: "结构化输出"
      weight: 0.2
      reason: "确保输出格式正确"
      
  # 配置推荐场景
  configuration:
    - method: "向量库检索"
      weight: 0.4
      reason: "匹配历史配置"
    - method: "知识库约束"
      weight: 0.3
      reason: "提供配置说明"
    - method: "结构化输出"
      weight: 0.3
      reason: "确保配置格式正确"
```

### 5.3 实施优先级

| 优先级 | 任务 | 工作量 | 确定性提升 |
|--------|------|--------|-----------|
| P0 | 实现结构化输出 | 1周 | 高 |
| P0 | 实现知识库预制 | 2周 | 高 |
| P1 | 实现向量库预制 | 3周 | 高 |
| P1 | 实现工具调用约束 | 2周 | 高 |
| P2 | 优化提示词工程 | 持续 | 中 |

---

## 六、假设验证

### 6.1 验证方法

| 假设 | 验证方法 | 验证指标 |
|------|---------|---------|
| 知识库约束有效 | 对比有无知识库的LLM输出 | 准确率提升 |
| 向量库检索有效 | 对比有无向量库的推荐质量 | 推荐命中率 |
| 工具调用有效 | 对比有无工具的操作成功率 | 操作成功率 |
| 结构化输出有效 | 对比有无约束的格式正确率 | 格式正确率 |

### 6.2 预期效果

| 指标 | 无增强 | 有增强 | 提升 |
|------|--------|--------|------|
| 配置准确率 | 70% | 95% | +25% |
| 格式正确率 | 60% | 99% | +39% |
| 问题解决率 | 50% | 85% | +35% |
| 用户满意度 | 60% | 90% | +30% |

---

**文档状态**: 假设分析  
**下一步**: 根据假设实施相关功能并验证
