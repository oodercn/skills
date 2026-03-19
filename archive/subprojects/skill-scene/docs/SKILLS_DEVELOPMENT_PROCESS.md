# Skills 开发流程规范 v2.3.1

> **版本**: 2.3.1  
> **创建日期**: 2026-03-16  
> **适用范围**: Ooder Skills 开发全流程

---

## 一、开发流程总览

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        Skills 开发完整流程                                        │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│   Phase 1: Skills 开发                                                          │
│   ┌─────────────────────────────────────────────────────────────────────────┐   │
│   │  1.1 在对应 skills 目录下开发                                             │   │
│   │  1.2 遵循 skills 目录结构规范                                             │   │
│   │  1.3 完成后端 API 和前端页面                                              │   │
│   └─────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                          │
│                                      ▼                                          │
│   Phase 2: Nexus 架构检查                                                       │
│   ┌─────────────────────────────────────────────────────────────────────────┐   │
│   │  2.1 页面架构检查                                                        │   │
│   │  2.2 JS/CSS 抽取                                                        │   │
│   │  2.3 二级子页面递归检查                                                  │   │
│   └─────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                          │
│                                      ▼                                          │
│   Phase 3: skill.yaml 完整性检查                                                │
│   ┌─────────────────────────────────────────────────────────────────────────┐   │
│   │  3.1 基本信息                                                            │   │
│   │  3.2 能力定义                                                            │   │
│   │  3.3 依赖声明                                                            │   │
│   │  3.4 配置项                                                              │   │
│   └─────────────────────────────────────────────────────────────────────────┘   │
│                                      │                                          │
│                                      ▼                                          │
│   Phase 4: LLM 和知识库配置                                                     │
│   ┌─────────────────────────────────────────────────────────────────────────┐   │
│   │  4.1 LLM 说明书配置 (三级标准)                                           │   │
│   │  4.2 知识库配置 (三级标准)                                               │   │
│   │  4.3 RAG 配置                                                            │   │
│   └─────────────────────────────────────────────────────────────────────────┘   │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、Phase 1: Skills 开发规范

### 2.1 Skills 目录结构

```
skills/
├── _system/                     # 系统技能
│   ├── skill-capability/        # 能力服务
│   ├── skill-scene-management/  # 场景管理
│   ├── skill-common/            # 公共库
│   └── skill-protocol/          # 协议管理
│
├── _drivers/                    # 驱动技能
│   ├── llm/                     # LLM驱动
│   ├── org/                     # 组织驱动
│   ├── vfs/                     # 存储驱动
│   ├── media/                   # 媒体驱动
│   └── payment/                 # 支付驱动
│
├── capabilities/                # 能力提供者
│   ├── auth/                    # 认证能力
│   ├── communication/           # 通讯能力
│   ├── knowledge/               # 知识能力
│   ├── llm/                     # LLM能力
│   ├── monitor/                 # 监控能力
│   ├── scheduler/               # 调度能力
│   ├── search/                  # 搜索能力
│   └── security/                # 安全能力
│
├── scenes/                      # 场景技能
│   ├── skill-llm-chat/          # LLM对话场景
│   ├── skill-knowledge-qa/      # 知识问答场景
│   └── ...                      # 其他场景
│
├── tools/                       # 工具技能
│   ├── skill-market/            # 技能市场
│   ├── skill-share/             # 技能分享
│   └── skill-report/            # 报表服务
│
└── config/                      # 配置文件
    ├── categories.yaml          # 分类枚举配置
    ├── schema.yaml              # 配置规范定义
    └── addresses.yaml           # 能力地址配置
```

### 2.2 单个 Skill 目录结构

```
skill-xxx/
├── skill.yaml                   # 技能定义文件 (必须)
├── src/
│   └── main/
│       ├── java/                # Java源码
│       │   └── net/ooder/skill/xxx/
│       │       ├── XxxSkillApplication.java
│       │       ├── controller/  # 控制器
│       │       ├── service/     # 服务层
│       │       ├── model/       # 数据模型
│       │       ├── dto/         # DTO
│       │       └── config/      # 配置类
│       └── resources/
│           ├── application.yml  # 应用配置
│           └── static/
│               └── console/     # 前端资源
│                   ├── index.html
│                   ├── pages/   # 页面文件
│                   ├── js/      # JavaScript
│                   ├── css/     # 样式文件
│                   └── menu-config.json
├── data/                        # 数据目录
│   └── config/                  # 配置数据
├── docs/                        # 文档目录
│   ├── README.md
│   └── knowledge/               # 知识库文档
│       ├── overview.md
│       ├── guide.md
│       └── faq.md
└── config/                      # 技能配置
    └── skill-config.json
```

### 2.3 开发检查清单

| 检查项 | 要求 | 状态 |
|--------|------|:----:|
| **目录结构** | 符合规范 | ⬜ |
| **skill.yaml** | 存在且完整 | ⬜ |
| **后端代码** | Controller/Service/Model 齐全 | ⬜ |
| **前端页面** | HTML/JS/CSS 齐全 | ⬜ |
| **菜单配置** | menu-config.json 存在 | ⬜ |
| **知识库文档** | docs/knowledge/ 目录存在 | ⬜ |

---

## 三、Phase 2: Nexus 架构检查

### 3.1 页面架构检查清单

| 检查项 | 说明 | 状态 |
|--------|------|:----:|
| **HTML结构** | 符合语义化标准 | ⬜ |
| **CSS规范** | 使用主题变量，避免硬编码 | ⬜ |
| **JS规范** | 遵循ES6+语法，正确使用async/await | ⬜ |
| **API调用** | 使用统一的API请求封装 | ⬜ |
| **错误处理** | 所有API调用有错误处理 | ⬜ |
| **加载状态** | 有loading状态显示 | ⬜ |
| **空状态** | 有空数据提示 | ⬜ |

### 3.2 JS/CSS 抽取规范

#### 3.2.1 JS 抽取规则

```
页面结构:
├── pages/
│   └── xxx-management.html      # 页面HTML
├── js/
│   ├── common/                  # 公共JS
│   │   ├── api.js               # API请求封装
│   │   ├── utils.js             # 工具函数
│   │   └── dict-cache.js        # 字典缓存
│   └── pages/                   # 页面JS
│       └── xxx-management.js    # 页面逻辑
```

**抽取原则**:
1. 公共函数抽取到 `js/common/`
2. 页面特定逻辑保留在 `js/pages/`
3. API调用统一使用 `api.js` 封装
4. 字典使用 `dict-cache.js` 缓存

#### 3.2.2 CSS 抽取规则

```
样式结构:
├── css/
│   ├── theme-variables.css      # 主题变量 (必须)
│   ├── common.css               # 公共样式
│   └── pages/                   # 页面样式
│       └── xxx-management.css   # 页面样式
```

**抽取原则**:
1. 颜色、字体、间距使用主题变量
2. 公共组件样式抽取到 `common.css`
3. 页面特定样式保留在 `css/pages/`
4. 避免内联样式

### 3.3 二级子页面递归检查

```
页面层级检查:
├── 一级页面: xxx-management.html
│   ├── 检查HTML结构
│   ├── 检查JS抽取
│   ├── 检查CSS抽取
│   └── 检查子页面链接
│
└── 二级页面: xxx-detail.html (从一级页面跳转)
    ├── 检查HTML结构
    ├── 检查JS抽取
    ├── 检查CSS抽取
    └── 检查子页面链接 (如有三级页面继续递归)
```

**递归检查清单**:

| 层级 | 页面 | HTML | JS | CSS | 子页面 |
|:----:|------|:----:|:--:|:---:|:------:|
| 1 | xxx-management.html | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | xxx-detail.html | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | xxx-sub-detail.html | ⬜ | ⬜ | ⬜ | - |

---

## 四、Phase 3: skill.yaml 完整性检查

### 4.1 基本信息

```yaml
id: skill-xxx                    # 必填，格式: skill-{name}
name: XXX技能                     # 必填
version: 2.3.1                   # 必填，格式: x.y.z
description: 技能描述              # 必填
skillForm: SCENE                 # 必填，SCENE/PROVIDER/DRIVER/INTERNAL
visibility: public               # 必填，public/developer/internal
capabilityCategory: llm          # 必填，17个标准分类之一
```

**检查清单**:

| 字段 | 必填 | 格式验证 | 状态 |
|------|:----:|:--------:|:----:|
| id | ✅ | skill-{name} | ⬜ |
| name | ✅ | 非空 | ⬜ |
| version | ✅ | x.y.z | ⬜ |
| description | ✅ | 非空 | ⬜ |
| skillForm | ✅ | 枚举值 | ⬜ |
| visibility | ✅ | 枚举值 | ⬜ |
| capabilityCategory | ✅ | 枚举值 | ⬜ |

### 4.2 能力定义

```yaml
capabilities:
  - id: capability-xxx           # 能力ID
    name: XXX能力                 # 能力名称
    description: 能力描述          # 能力描述
    type: ATOMIC                  # 能力类型
    accessLevel: PUBLIC           # 访问级别
    address: "28:01"              # 能力地址 (分类:序号)
    parameters:                   # 参数定义
      - name: param1
        type: string
        required: true
    returns:                      # 返回定义
      type: object
```

**检查清单**:

| 检查项 | 要求 | 状态 |
|--------|------|:----:|
| 能力ID唯一 | 所有能力ID不重复 | ⬜ |
| 能力地址有效 | 符合地址空间规范 | ⬜ |
| 参数定义完整 | name/type/required | ⬜ |
| 返回定义完整 | type定义 | ⬜ |

### 4.3 依赖声明

```yaml
dependencies:
  - id: skill-yyy                # 依赖技能ID
    version: ">=1.0.0"           # 版本要求
    required: true               # 是否必需
    autoInstall: true            # 自动安装
    capabilities:                # 需要的能力
      - capability-yyy-1
      - capability-yyy-2
```

**检查清单**:

| 检查项 | 要求 | 状态 |
|--------|------|:----:|
| 依赖ID存在 | 被依赖技能存在 | ⬜ |
| 版本兼容 | 版本要求合理 | ⬜ |
| 能力匹配 | 依赖能力存在 | ⬜ |
| 循环依赖 | 无循环依赖 | ⬜ |

### 4.4 场景能力配置 (仅SCENE类型)

```yaml
sceneCapabilities:
  - id: scene-xxx
    mainFirst: true              # 是否主优先
    mainFirstConfig:
      selfCheck:                 # 自检配置
        - checkCapabilities: [cap1, cap2]
        - checkDriverCapabilities: [driver1]
      selfStart:                 # 自启动配置
        - installDependencies: auto
        - initCapabilities: [cap1]
      selfDrive:                 # 自驱配置
        eventRules:
          - event: user.action
            action: flow-xxx
```

**检查清单**:

| 检查项 | 要求 | 状态 |
|--------|------|:----:|
| mainFirst配置 | 自检/自启/自驱完整 | ⬜ |
| 能力引用 | 引用的能力已定义 | ⬜ |
| 事件规则 | 事件和动作匹配 | ⬜ |

---

## 五、Phase 4: LLM 和知识库三级配置标准

### 5.1 LLM 说明书配置 (三级标准)

#### 5.1.1 第一级：基础配置

```yaml
llm:
  enabled: true                  # 是否启用LLM
  defaultProvider: deepseek      # 默认Provider
  defaultModel: deepseek-chat    # 默认模型
```

#### 5.1.2 第二级：Persona 配置

```yaml
persona:
  enabled: true
  name: 智能助手
  description: 专业的智能助手
  expertise:
    - 领域1
    - 领域2
  boundaries:
    - 不处理敏感信息
    - 不执行危险操作
  tone: professional, friendly, concise
```

#### 5.1.3 第三级：LLM Assistant 配置

```yaml
llmAssistant:
  enabled: true
  welcomeMessage: "您好，我是智能助手..."
  suggestedQuestions:
    - "问题1"
    - "问题2"
  contextWindow: 10             # 上下文窗口大小
  maxTokens: 2000               # 最大Token数
  temperature: 0.7              # 温度参数
  topP: 0.9                     # Top P参数
  streaming: true               # 是否流式输出
  functionCalling: true         # 是否支持函数调用
```

**LLM配置检查清单**:

| 级别 | 配置项 | 必填 | 状态 |
|:----:|--------|:----:|:----:|
| 1 | llm.enabled | ✅ | ⬜ |
| 1 | llm.defaultProvider | ✅ | ⬜ |
| 1 | llm.defaultModel | ✅ | ⬜ |
| 2 | persona.enabled | ✅ | ⬜ |
| 2 | persona.name | ✅ | ⬜ |
| 2 | persona.expertise | ✅ | ⬜ |
| 2 | persona.boundaries | ✅ | ⬜ |
| 3 | llmAssistant.welcomeMessage | ✅ | ⬜ |
| 3 | llmAssistant.suggestedQuestions | ⬜ | ⬜ |
| 3 | llmAssistant.contextWindow | ✅ | ⬜ |
| 3 | llmAssistant.maxTokens | ✅ | ⬜ |

### 5.2 知识库配置 (三级标准)

#### 5.2.1 第一级：基础配置

```yaml
knowledge:
  enabled: true                  # 是否启用知识库
  indexName: "skill-xxx-knowledge"  # 索引名称
```

#### 5.2.2 第二级：文档配置

```yaml
knowledge:
  documents:
    - id: overview
      path: docs/knowledge/overview.md
      type: guide               # guide/reference/faq
      priority: 1
    - id: user-guide
      path: docs/knowledge/guide.md
      type: guide
      priority: 2
    - id: faq
      path: docs/knowledge/faq.md
      type: faq
      priority: 3
```

#### 5.2.3 第三级：RAG 配置

```yaml
knowledge:
  ragConfig:
    enabled: true
    embeddingModel: text-embedding-3-small  # 嵌入模型
    chunkSize: 1000             # 分块大小
    chunkOverlap: 200           # 分块重叠
    searchStrategy: hybrid      # 搜索策略: keyword/semantic/hybrid
    topK: 5                     # 返回结果数
    scoreThreshold: 0.7         # 分数阈值
    rerankEnabled: true         # 是否启用重排序
```

**知识库配置检查清单**:

| 级别 | 配置项 | 必填 | 状态 |
|:----:|--------|:----:|:----:|
| 1 | knowledge.enabled | ✅ | ⬜ |
| 1 | knowledge.indexName | ✅ | ⬜ |
| 2 | knowledge.documents | ✅ | ⬜ |
| 2 | 文档文件存在 | ✅ | ⬜ |
| 2 | 文档类型正确 | ✅ | ⬜ |
| 3 | knowledge.ragConfig.enabled | ✅ | ⬜ |
| 3 | knowledge.ragConfig.embeddingModel | ✅ | ⬜ |
| 3 | knowledge.ragConfig.chunkSize | ✅ | ⬜ |
| 3 | knowledge.ragConfig.searchStrategy | ✅ | ⬜ |

### 5.3 知识库文档规范

```
docs/knowledge/
├── overview.md                  # 概述文档 (必须)
│   ├── 技能简介
│   ├── 核心功能
│   └── 使用场景
│
├── guide.md                     # 使用指南 (必须)
│   ├── 快速开始
│   ├── 详细步骤
│   └── 常见问题
│
├── reference.md                 # 参考文档 (可选)
│   ├── API参考
│   ├── 配置说明
│   └── 参数说明
│
└── faq.md                       # FAQ文档 (可选)
    ├── 常见问题1
    ├── 常见问题2
    └── ...
```

**文档内容要求**:

| 文档 | 必须包含 | 字数要求 |
|------|----------|:--------:|
| overview.md | 技能简介、核心功能、使用场景 | ≥500字 |
| guide.md | 快速开始、详细步骤、常见问题 | ≥1000字 |
| reference.md | API参考、配置说明 | ≥500字 |
| faq.md | 至少5个常见问题 | ≥300字 |

---

## 六、完整开发检查清单

### 6.1 Phase 1 检查清单

| 检查项 | 状态 |
|--------|:----:|
| 在正确的 skills 目录下开发 | ⬜ |
| 目录结构符合规范 | ⬜ |
| 后端代码完整 (Controller/Service/Model) | ⬜ |
| 前端页面完整 (HTML/JS/CSS) | ⬜ |
| API端点定义正确 | ⬜ |
| 菜单配置存在 | ⬜ |

### 6.2 Phase 2 检查清单

| 检查项 | 状态 |
|--------|:----:|
| HTML结构符合语义化 | ⬜ |
| CSS使用主题变量 | ⬜ |
| JS抽取到独立文件 | ⬜ |
| API调用统一封装 | ⬜ |
| 错误处理完整 | ⬜ |
| 二级子页面检查完成 | ⬜ |

### 6.3 Phase 3 检查清单

| 检查项 | 状态 |
|--------|:----:|
| skill.yaml 基本信息 | ⬜ |
| 能力定义完整 | ⬜ |
| 依赖声明正确 | ⬜ |
| 场景能力配置 (如适用) | ⬜ |
| 能力地址有效 | ⬜ |

### 6.4 Phase 4 检查清单

| 检查项 | 状态 |
|--------|:----:|
| LLM一级配置 | ⬜ |
| LLM二级配置 | ⬜ |
| LLM三级配置 | ⬜ |
| 知识库一级配置 | ⬜ |
| 知识库二级配置 | ⬜ |
| 知识库三级配置 | ⬜ |
| 知识库文档存在 | ⬜ |

---

## 七、验收标准

### 7.1 功能验收

- [ ] 所有页面可正常访问
- [ ] 所有API可正常调用
- [ ] 所有按钮事件正常触发
- [ ] 数据增删改查正常

### 7.2 架构验收

- [ ] JS/CSS抽取完成
- [ ] 无内联样式
- [ ] 无硬编码配置
- [ ] 二级页面递归检查完成

### 7.3 配置验收

- [ ] skill.yaml完整有效
- [ ] LLM配置三级齐全
- [ ] 知识库配置三级齐全
- [ ] 知识库文档齐全

### 7.4 文档验收

- [ ] README.md存在
- [ ] 知识库文档存在
- [ ] API文档存在 (如有)

---

**文档版本**: 2.3.1  
**创建日期**: 2026-03-16
