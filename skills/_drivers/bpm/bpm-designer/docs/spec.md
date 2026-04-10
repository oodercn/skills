# BPM流程设计器 - 技术规格文档

## 1. 项目概述

### 1.1 项目信息
- **项目名称**: BPM流程设计器 (bpm-designer)
- **版本**: 3.0.2
- **描述**: 基于Ooder框架的BPM流程可视化设计器，支持自然语言交互和AI辅助设计
- **作者**: Ooder Team

### 1.2 项目定位
BPM流程设计器是一个独立的微服务应用，提供流程可视化设计、自然语言交互、AI辅助推导等功能。作为BPM工作流系统的前端设计工具，与bpmserver流程引擎协同工作。

## 2. 技术选型

### 2.1 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 21 | 主要开发语言 |
| Spring Boot | 3.4.4 | 应用框架 |
| FastJSON2 | 2.0.43 | JSON序列化 |
| Jackson | (Spring Boot内置) | YAML解析、JSON处理 |
| Lombok | 1.18.30 | 代码简化 |
| WebSocket | (Spring Boot内置) | 实时通信 |

### 2.2 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| 原生JavaScript | ES6+ | 前端开发 |
| Ooder框架 | 3.x | UI组件框架 |
| CSS3 | - | 样式设计 |
| RemixIcon | - | 图标库 |

### 2.3 依赖服务

| 服务 | 版本 | 用途 |
|------|------|------|
| bpmserver | 3.0.2 | BPM流程引擎后端 |
| LLM服务 | OpenAI兼容 | AI辅助功能 |

## 3. 系统架构

### 3.1 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                     BPM流程设计器                             │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   前端UI     │  │   画布引擎   │  │   属性面板   │         │
│  │  (Ooder)    │  │  (Canvas)   │  │  (Panel)    │         │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘         │
│         │                │                │                 │
│  ┌──────┴────────────────┴────────────────┴──────┐         │
│  │              Store (状态管理)                   │         │
│  └──────────────────────┬─────────────────────────┘         │
├─────────────────────────┼───────────────────────────────────┤
│  ┌──────────────────────┴─────────────────────────┐         │
│  │              后端API服务层                       │         │
│  ├─────────────────────────────────────────────────┤         │
│  │  DesignerController  │  DesignerNlpController   │         │
│  │  DesignerDerivationController                   │         │
│  ├─────────────────────────────────────────────────┤         │
│  │              服务层 (Service Layer)              │         │
│  │  DesignerService │ DesignerNlpService           │         │
│  │  PerformerDerivationService                     │         │
│  │  CapabilityMatchingService                      │         │
│  │  FormMatchingService │ PanelRenderService       │         │
│  ├─────────────────────────────────────────────────┤         │
│  │              LLM集成层                           │         │
│  │  LLMServiceImpl │ PromptTemplateManager         │         │
│  └──────────────────────┬──────────────────────────┘         │
└─────────────────────────┼───────────────────────────────────┘
                          │
                          ▼
              ┌───────────────────────┐
              │      bpmserver        │
              │   (流程引擎后端)        │
              └───────────────────────┘
```

### 3.2 模块划分

#### 3.2.1 后端模块

| 模块 | 包路径 | 职责 |
|------|--------|------|
| 控制器层 | controller | REST API端点定义 |
| 服务层 | service | 业务逻辑处理 |
| 数据传输 | dto | 前后端数据交互对象 |
| 模型层 | model | 领域模型定义 |
| LLM集成 | llm | 大语言模型服务集成 |
| 缓存层 | cache | 数据缓存管理 |
| 函数调用 | function | LLM Function Calling定义 |
| 提示词 | prompt | Prompt模板管理 |
| WebSocket | websocket | 实时通信支持 |

#### 3.2.2 前端模块

| 模块 | 文件 | 职责 |
|------|------|------|
| 应用入口 | App.js | 应用初始化和协调 |
| 画布引擎 | Canvas.js | 流程图渲染和交互 |
| 状态管理 | Store.js | 数据状态管理 |
| 属性面板 | PanelManagerNew.js | 属性编辑面板 |
| AI对话 | Chat.js | 自然语言交互 |
| 流程树 | Tree.js | 流程列表展示 |
| 元素面板 | Elements.js | 拖拽元素面板 |
| 标签管理 | TabManager.js | 多流程标签页 |
| 数据模型 | model/*.js | 流程/活动/路由定义 |

## 4. 已实现功能

### 4.1 流程设计核心功能

#### 4.1.1 流程管理 API
- [x] 获取流程定义 (`GET /api/bpm/process/{processId}/version/{version}`)
- [x] 获取流程列表 (`GET /api/bpm/process`)
- [x] 创建流程 (`POST /api/bpm/process`)
- [x] 更新流程 (`PUT /api/bpm/process/{processId}`)
- [x] 删除流程 (`DELETE /api/bpm/process/{processId}`)
- [x] 获取流程树 (`GET /api/bpm/process/tree`)

#### 4.1.2 活动管理 API
- [x] 添加活动 (`POST /api/bpm/process/{processId}/activity`)
- [x] 更新活动 (`PUT /api/bpm/process/{processId}/activity/{activityId}`)
- [x] 删除活动 (`DELETE /api/bpm/process/{processId}/activity/{activityId}`)

#### 4.1.3 路由管理 API
- [x] 添加路由 (`POST /api/bpm/process/{processId}/route`)
- [x] 更新路由 (`PUT /api/bpm/process/{processId}/route/{routeId}`)
- [x] 删除路由 (`DELETE /api/bpm/process/{processId}/route/{routeId}`)

#### 4.1.4 枚举选项 API
- [x] 获取枚举选项 (`GET /api/bpm/enums/{enumType}`)
  - ActivityPosition: 活动位置类型
  - ActivityType: 活动类型
  - ActivityCategory: 活动分类

### 4.2 自然语言处理 (NLP)

#### 4.2.1 NLP API
- [x] 对话处理 (`POST /api/bpm/nlp/chat`)
- [x] 从NLP创建流程 (`POST /api/bpm/nlp/process/create`)
- [x] 从NLP创建活动 (`POST /api/bpm/nlp/activity/create`)
- [x] 从NLP更新属性 (`POST /api/bpm/nlp/attribute/update`)
- [x] 获取建议 (`POST /api/bpm/nlp/suggestions`)
- [x] 验证流程 (`POST /api/bpm/nlp/validate`)
- [x] 描述流程 (`POST /api/bpm/nlp/describe/process`)
- [x] 描述活动 (`POST /api/bpm/nlp/describe/activity`)
- [x] 意图分析 (`POST /api/bpm/nlp/intent/analyze`)
- [x] 实体提取 (`POST /api/bpm/nlp/entities/extract`)

### 4.3 智能推导服务

#### 4.3.1 执行者推导
- [x] 执行者推导 (`POST /api/bpm/designer/derivation/performer`)
- [x] 候选人搜索 (`POST /api/bpm/designer/derivation/performer/search`)

#### 4.3.2 能力匹配
- [x] 能力匹配 (`POST /api/bpm/designer/derivation/capability`)
- [x] 智能匹配 (`POST /api/bpm/designer/derivation/capability/smart`)

#### 4.3.3 表单匹配
- [x] 表单匹配 (`POST /api/bpm/designer/derivation/form`)
- [x] 智能匹配 (`POST /api/bpm/designer/derivation/form/smart`)
- [x] 生成表单Schema (`POST /api/bpm/designer/derivation/form/generate`)

#### 4.3.4 面板渲染
- [x] 构建执行者面板 (`POST /api/bpm/designer/derivation/panel/performer`)
- [x] 构建能力面板 (`POST /api/bpm/designer/derivation/panel/capability`)
- [x] 构建表单面板 (`POST /api/bpm/designer/derivation/panel/form`)
- [x] 构建活动面板 (`POST /api/bpm/designer/derivation/panel/activity`)
- [x] 完整推导 (`POST /api/bpm/designer/derivation/full`)

#### 4.3.5 函数调用
- [x] 获取可用函数 (`GET /api/bpm/designer/derivation/functions`)
- [x] 获取函数Schema (`GET /api/bpm/designer/derivation/functions/schemas`)
- [x] 按类别获取函数 (`GET /api/bpm/designer/derivation/functions/category/{category}`)

### 4.4 前端功能

#### 4.4.1 画布功能
- [x] 流程图可视化渲染
- [x] 节点拖拽移动
- [x] 节点选择和多选
- [x] 路由连线绘制
- [x] 缩放和平移
- [x] 适应屏幕
- [x] 右键菜单

#### 4.4.2 属性面板
- [x] 流程属性编辑
- [x] 活动属性编辑
- [x] 路由属性编辑
- [x] 插件化面板架构
- [x] 动态Schema渲染

#### 4.4.3 AI对话
- [x] 自然语言输入
- [x] 意图识别
- [x] 流程创建命令
- [x] 活动创建命令
- [x] YAML导出命令
- [x] 帮助信息展示

#### 4.4.4 其他功能
- [x] 流程树导航
- [x] 多标签页管理
- [x] 撤销/重做
- [x] 保存流程
- [x] 导入/导出YAML
- [x] 主题切换 (亮色/暗色)
- [x] 快捷键支持

### 4.5 数据模型

#### 4.5.1 流程定义 (ProcessDef)
- [x] 基本属性: processDefId, name, description, classification
- [x] 版本信息: version, status, creatorName, modifierName
- [x] 时限配置: limit, durationUnit
- [x] 开始节点: startNode
- [x] 结束节点: endNodes (支持多个)
- [x] 监听器: listeners
- [x] 权限组: rightGroups
- [x] 活动列表: activities
- [x] 路由列表: routes
- [x] 扩展属性: extendedAttributes

#### 4.5.2 活动定义 (ActivityDef)
- [x] 基本属性: activityDefId, name, description
- [x] 类型属性: activityType, activityCategory, position
- [x] 坐标位置: positionCoord
- [x] 实现方式: implementation, execClass
- [x] 时限配置: timing
- [x] 路由配置: routing
- [x] 权限配置: right
- [x] 子流程配置: subFlow
- [x] 设备配置: device
- [x] 服务配置: service
- [x] 事件配置: event
- [x] Agent配置: agentConfig
- [x] 场景配置: sceneConfig

#### 4.5.3 路由定义 (RouteDef)
- [x] 基本属性: routeDefId, name, description
- [x] 连接信息: from, to
- [x] 路由属性: routeOrder, routeDirection
- [x] 条件配置: routeConditionType, condition

### 4.6 LLM集成

#### 4.6.1 LLM服务
- [x] 基础对话 (chat)
- [x] 带系统提示词对话
- [x] Function Calling支持
- [x] 函数结果处理
- [x] 可用性检查

#### 4.6.2 Prompt模板
- [x] 执行者推导模板 (performer-derivation.yaml)
- [x] 能力匹配模板 (capability-matching.yaml)
- [x] 表单匹配模板 (form-matching.yaml)
- [x] 完整推导模板 (full-derivation.yaml)

### 4.7 缓存服务

- [x] 内存缓存实现
- [x] TTL过期机制
- [x] 自动清理线程
- [x] 缓存统计

### 4.8 WebSocket支持

- [x] WebSocket配置
- [x] 推导进度推送
- [x] 实时状态更新

## 5. 数据传输对象 (DTO)

### 5.1 核心DTO

| DTO | 用途 |
|-----|------|
| ProcessDTO | 流程定义传输 |
| ActivityDTO | 活动定义传输 |
| RouteDTO | 路由定义传输 |
| PositionCoordDTO | 坐标位置传输 |

### 5.2 子配置DTO

| DTO | 用途 |
|-----|------|
| TimingDTO | 时限配置 |
| RoutingDTO | 路由配置 |
| RightDTO | 权限配置 |
| SubFlowDTO | 子流程配置 |
| DeviceDTO | 设备配置 |
| ServiceDTO | 服务配置 |
| EventDTO | 事件配置 |
| AgentConfigDTO | Agent配置 |
| SceneConfigDTO | 场景配置 |

### 5.3 推导结果DTO

| DTO | 用途 |
|-----|------|
| PerformerDerivationResultDTO | 执行者推导结果 |
| CapabilityMatchingResultDTO | 能力匹配结果 |
| FormMatchingResultDTO | 表单匹配结果 |
| PanelRenderDataDTO | 面板渲染数据 |

## 6. API端点汇总

### 6.1 流程设计API (`/api/bpm`)

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | /process/{processId}/version/{version} | 获取流程定义 |
| GET | /process/{processId}/version/latest | 获取最新版本流程 |
| GET | /process | 获取流程列表 |
| POST | /process | 创建流程 |
| PUT | /process/{processId} | 更新流程 |
| DELETE | /process/{processId} | 删除流程 |
| GET | /process/tree | 获取流程树 |
| POST | /process/{processId}/activity | 添加活动 |
| PUT | /process/{processId}/activity/{activityId} | 更新活动 |
| DELETE | /process/{processId}/activity/{activityId} | 删除活动 |
| POST | /process/{processId}/route | 添加路由 |
| PUT | /process/{processId}/route/{routeId} | 更新路由 |
| DELETE | /process/{processId}/route/{routeId} | 删除路由 |
| GET | /capabilities | 获取能力列表 |
| GET | /enums/{enumType} | 获取枚举选项 |

### 6.2 NLP API (`/api/bpm/nlp`)

| 方法 | 端点 | 描述 |
|------|------|------|
| POST | /chat | 对话处理 |
| POST | /process/create | 从NLP创建流程 |
| POST | /activity/create | 从NLP创建活动 |
| POST | /attribute/update | 从NLP更新属性 |
| POST | /suggestions | 获取建议 |
| POST | /validate | 验证流程 |
| POST | /describe/process | 描述流程 |
| POST | /describe/activity | 描述活动 |
| POST | /intent/analyze | 意图分析 |
| POST | /entities/extract | 实体提取 |

### 6.3 推导API (`/api/bpm/designer/derivation`)

| 方法 | 端点 | 描述 |
|------|------|------|
| POST | /performer | 执行者推导 |
| POST | /performer/search | 候选人搜索 |
| POST | /capability | 能力匹配 |
| POST | /capability/smart | 智能能力匹配 |
| POST | /form | 表单匹配 |
| POST | /form/smart | 智能表单匹配 |
| POST | /form/generate | 生成表单Schema |
| POST | /panel/performer | 构建执行者面板 |
| POST | /panel/capability | 构建能力面板 |
| POST | /panel/form | 构建表单面板 |
| POST | /panel/activity | 构建活动面板 |
| POST | /full | 完整推导 |
| GET | /functions | 获取可用函数 |
| GET | /functions/schemas | 获取函数Schema |
| GET | /functions/category/{category} | 按类别获取函数 |

## 7. 配置说明

### 7.1 应用配置 (application.yml)

```yaml
server:
  port: 8080

spring:
  application:
    name: bpm-designer

bpm:
  server:
    url: http://localhost:8080

llm:
  enabled: false
  api-endpoint: https://api.openai.com/v1/chat/completions
  api-key: ${LLM_API_KEY:}
  model: gpt-4
  temperature: 0.7
  max-tokens: 2000

cache:
  enabled: true
  default-ttl: 3600
  max-size: 1000
  cleanup-interval: 300
```

### 7.2 Skill配置 (skill.yaml)

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillPackage

metadata:
  id: bpm-designer
  name: BPM流程设计器
  version: "3.0.1"

spec:
  skillForm: PROVIDER
  skillCategory: SERVICE
  sceneType: AUTO
  
  capabilities:
    - id: flow-design
      name: 流程设计
      description: 可视化流程设计
      category: bpm
      autoBind: true
    - id: form-binding
      name: 表单绑定
      description: 流程节点表单绑定
      category: bpm
      autoBind: false
    - id: node-config
      name: 节点配置
      description: 流程节点属性配置
      category: bpm
      autoBind: false
```

## 8. 部署说明

### 8.1 构建命令

```bash
# 编译
mvn clean compile

# 打包
mvn clean package -DskipTests

# 运行
java -Dfile.encoding=UTF-8 -jar target/bpm-designer-3.0.2.jar
```

### 8.2 依赖服务

1. **bpmserver**: 需要启动bpmserver服务 (端口8082)
2. **LLM服务**: 可选，用于AI辅助功能

### 8.3 环境变量

| 变量 | 描述 | 默认值 |
|------|------|--------|
| LLM_API_KEY | LLM API密钥 | - |
| BPM_SERVER_URL | BPM服务器地址 | http://localhost:8080 |

## 9. 文件路径索引

### 9.1 关键文件

| 文件 | 绝对路径 |
|------|----------|
| 主应用入口 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\BpmDesignerApplication.java |
| 设计器服务 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\service\DesignerService.java |
| NLP服务接口 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\service\DesignerNlpService.java |
| LLM服务实现 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\llm\LLMServiceImpl.java |
| 缓存服务 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\cache\CacheService.java |
| 前端入口 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\App.js |
| 流程模型 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\model\ProcessDef.js |
| 活动模型 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\resources\static\designer\js\model\ActivityDef.js |
| POM配置 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\pom.xml |
| Skill配置 | e:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\skill.yaml |

---

*文档生成时间: 2026-04-10*
*文档版本: 1.0*
