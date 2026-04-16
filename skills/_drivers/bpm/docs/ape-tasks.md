# BPM流程设计器 - 任务分解文档 (面向 Trae Solo Web)

> **文档版本**: 3.0.2  
> **生成日期**: 2026-04-13  
> **项目仓库**: gitee.com/ooderCN/ade  
> **测试目标**: Trae Solo Web 长任务一文到底能力极限测试

---

## 第一阶段: 项目骨架搭建

### PH1-001 创建Maven项目结构

| 属性 | 值 |
|------|------|
| 任务ID | PH1-001 |
| 优先级 | P0 |
| 状态 | 待实现 |
| 依赖 | 无 |

**任务描述**: 创建完整的Maven项目结构，包括pom.xml、包结构、资源目录

**验收标准**:
- pom.xml包含所有依赖(Spring Boot 3.4.4, FastJSON2 2.0.43, Lombok 1.18.30等)
- Java版本配置为21
- 包结构: net.ooder.bpm.designer及所有子包(cache,config,controller,datasource,dto,function,llm,model,prompt,service,websocket)
- 资源目录: resources/prompts, resources/static/designer, resources/db

**输出文件**:
- `pom.xml`
- `src/main/java/net/ooder/bpm/designer/BpmDesignerApplication.java`
- `src/main/resources/application.yml`

### PH1-002 创建后端领域模型

| 属性 | 值 |
|------|------|
| 任务ID | PH1-002 |
| 优先级 | P0 |
| 状态 | 待实现 |
| 依赖 | PH1-001 |

**任务描述**: 创建所有后端领域模型类

**验收标准**:
- ProcessDef: 包含所有基本属性、版本信息、时限配置、流程结构、监听器、权限组、扩展属性
- ActivityDef: 包含基本属性、类型属性、坐标、实现方式、所有子配置(timing/routing/right/subFlow/device/service/event/agentConfig/sceneConfig)
- RouteDef: 包含基本属性、连接信息、路由属性、条件配置
- ApiResponse<T>: 统一响应格式(code/message/data)

**输出文件**:
- `src/main/java/net/ooder/bpm/designer/model/ProcessDef.java`
- `src/main/java/net/ooder/bpm/designer/model/ActivityDef.java`
- `src/main/java/net/ooder/bpm/designer/model/RouteDef.java`
- `src/main/java/net/ooder/bpm/designer/model/ApiResponse.java`

### PH1-003 创建DTO层

| 属性 | 值 |
|------|------|
| 任务ID | PH1-003 |
| 优先级 | P0 |
| 状态 | 待实现 |
| 依赖 | PH1-002 |

**任务描述**: 创建所有DTO类，包括核心DTO和子配置DTO

**验收标准**:
- 核心DTO: ProcessDTO, ActivityDTO, RouteDTO, PositionCoordDTO
- 子配置DTO: TimingDTO, RoutingDTO, RightDTO, SubFlowDTO, DeviceDTO, ServiceDTO, EventDTO, AgentConfigDTO, SceneConfigDTO
- 枚举DTO: ActivityPosition, ActivityType, ActivityCategory
- 所有DTO使用Lombok @Data注解

**输出文件**:
- `src/main/java/net/ooder/bpm/designer/dto/*.java`
- `src/main/java/net/ooder/bpm/designer/dto/sub/*.java`

### PH1-004 创建前端项目结构

| 属性 | 值 |
|------|------|
| 任务ID | PH1-004 |
| 优先级 | P0 |
| 状态 | 待实现 |
| 依赖 | PH1-001 |

**任务描述**: 创建前端HTML/CSS/JS项目结构

**验收标准**:
- index.html: 主页面，包含侧边栏、画布区、属性面板、AI对话面板
- CSS文件: base.css, main.css, node.css, panel.css, chat.css, palette.css, tree.css, var.css
- JS核心文件: App.js, Canvas.js, Chat.js, Store.js, Api.js
- JS模型文件: ProcessDef.js, ActivityDef.js, RouteDef.js
- JS面板文件: PanelManager.js
- 图标库: RemixIcon

**输出文件**:
- `src/main/resources/static/designer/index.html`
- `src/main/resources/static/designer/css/*.css`
- `src/main/resources/static/designer/js/*.js`
- `src/main/resources/static/designer/js/model/*.js`
- `src/main/resources/static/designer/js/panel/*.js`
- `src/main/resources/static/designer/js/sdk/*.js`

---

## 第二阶段: 核心后端服务

### PH2-001 实现DesignerService

| 属性 | 值 |
|------|------|
| 任务ID | PH2-001 |
| 优先级 | P0 |
| 状态 | 待实现 |
| 依赖 | PH1-002, PH1-003 |

**任务描述**: 实现设计器核心服务，包含流程CRUD和DTO转换

**验收标准**:
- getProcess(): 从bpmserver获取流程，DTO→Model转换
- saveProcess(): 保存流程到bpmserver，Model→DTO转换
- getProcessList(): 获取流程列表
- deleteProcess(): 删除流程
- getProcessTree(): 获取流程树结构
- addActivity/updateActivity/removeActivity(): 活动管理
- addRoute/updateRoute/removeRoute(): 路由管理
- getEnumOptions(): 枚举选项获取
- 所有DTO↔Map转换方法(Timing/Routing/Right/SubFlow/Device/Service/Event/AgentConfig/SceneConfig)
- exportYaml/importYaml: 待实现(返回空/null)

**输出文件**:
- `src/main/java/net/ooder/bpm/designer/service/DesignerService.java`

### PH2-002 实现DesignerController

| 属性 | 值 |
|------|------|
| 任务ID | PH2-002 |
| 优先级 | P0 |
| 状态 | 待实现 |
| 依赖 | PH2-001 |

**任务描述**: 实现流程设计REST API控制器

**验收标准**:
- 所有17个API端点已实现
- @CrossOrigin(origins = "*") 支持跨域
- 统一使用ApiResponse封装响应
- 异常处理返回ApiResponse.error()

**输出文件**:
- `src/main/java/net/ooder/bpm/designer/controller/DesignerController.java`

### PH2-003 实现NLP服务

| 属性 | 值 |
|------|------|
| 任务ID | PH2-003 |
| 优先级 | P1 |
| 状态 | 待实现 |
| 依赖 | PH2-001 |

**任务描述**: 实现自然语言处理服务接口

**验收标准**:
- DesignerNlpService接口定义完整
- NlpResponse/NlpSuggestion/NlpIntent内部类定义完整
- 所有10个NLP API端点方法签名正确

**输出文件**:
- `src/main/java/net/ooder/bpm/designer/service/DesignerNlpService.java`
- `src/main/java/net/ooder/bpm/designer/controller/DesignerNlpController.java`

### PH2-004 实现推导服务

| 属性 | 值 |
|------|------|
| 任务ID | PH2-004 |
| 优先级 | P1 |
| 状态 | 待实现 |
| 依赖 | PH2-001 |

**任务描述**: 实现智能推导服务接口

**验收标准**:
- PerformerDerivationService: derive(), deriveWithCandidates(), searchCandidates()
- CapabilityMatchingService: match(), smartMatch(), matchByKeywords(), buildBindingConfig()
- FormMatchingService: match(), smartMatch(), generateSchema(), matchByFields()
- PanelRenderService: buildPerformerPanel(), buildCapabilityPanel(), buildFormPanel(), buildActivityPanel()
- DesignerDerivationController: 所有15个推导API端点

**输出文件**:
- `src/main/java/net/ooder/bpm/designer/service/PerformerDerivationService.java`
- `src/main/java/net/ooder/bpm/designer/service/CapabilityMatchingService.java`
- `src/main/java/net/ooder/bpm/designer/service/FormMatchingService.java`
- `src/main/java/net/ooder/bpm/designer/service/PanelRenderService.java`
- `src/main/java/net/ooder/bpm/designer/controller/DesignerDerivationController.java`

---

## 第三阶段: LLM集成

### PH3-001 实现LLM服务

| 属性 | 值 |
|------|------|
| 任务ID | PH3-001 |
| 优先级 | P1 |
| 状态 | 待实现 |
| 依赖 | PH1-001 |

**任务描述**: 实现LLM服务，支持OpenAI兼容API

**验收标准**:
- LLMService接口: chat(), chatWithFunctions(), chatWithFunctionResult(), isAvailable(), getModelName()
- LLMServiceImpl: 使用RestTemplate调用OpenAI兼容API
- LLMResponse: success(), withFunctionCalls(), failure()
- FunctionCall: name + arguments
- 支持Bearer Auth认证
- 支持Function Calling (functions + function_call=auto)
- 支持函数结果回传

**输出文件**:
- `src/main/java/net/ooder/bpm/designer/llm/LLMService.java`
- `src/main/java/net/ooder/bpm/designer/llm/LLMServiceImpl.java`
- `src/main/java/net/ooder/bpm/designer/llm/LLMResponse.java`
- `src/main/java/net/ooder/bpm/designer/llm/FunctionCall.java`

### PH3-002 实现Prompt模板系统

| 属性 | 值 |
|------|------|
| 任务ID | PH3-002 |
| 优先级 | P1 |
| 状态 | 待实现 |
| 依赖 | PH3-001 |

**任务描述**: 实现YAML格式的Prompt模板系统

**验收标准**:
- PromptTemplate: YAML模板加载和变量替换
- PromptTemplateManager: 模板注册和获取
- DesignerPromptBuilder: 动态构建系统提示词和用户提示词
- 4个YAML模板文件: performer-derivation.yaml, capability-matching.yaml, form-matching.yaml, full-derivation.yaml

**输出文件**:
- `src/main/java/net/ooder/bpm/designer/prompt/DesignerPromptBuilder.java`
- `src/main/java/net/ooder/bpm/designer/prompt/PromptTemplate.java`
- `src/main/java/net/ooder/bpm/designer/prompt/PromptTemplateManager.java`
- `src/main/resources/prompts/*.yaml`

### PH3-003 实现Function Calling注册

| 属性 | 值 |
|------|------|
| 任务ID | PH3-003 |
| 优先级 | P2 |
| 状态 | 待实现 |
| 依赖 | PH3-001 |

**任务描述**: 实现LLM Function Calling的函数注册和Schema生成

**验收标准**:
- DesignerFunctionDefinition: 函数定义(name/description/parameters/category)
- DesignerFunctionRegistry: 函数注册表，按类别管理
- FunctionCallRequest: 函数调用请求
- 执行者推导函数: get_organization_tree, get_users_by_role, search_users, get_department_leader, list_roles
- 能力匹配函数: list_capabilities, search_capabilities, get_capability_detail, match_capability_by_activity
- 表单匹配函数: list_forms, search_forms, get_form_schema, match_form_by_activity, generate_form_schema

**输出文件**:
- `src/main/java/net/ooder/bpm/designer/function/DesignerFunctionDefinition.java`
- `src/main/java/net/ooder/bpm/designer/function/DesignerFunctionRegistry.java`
- `src/main/java/net/ooder/bpm/designer/function/FunctionCallRequest.java`

---

## 第四阶段: 基础设施

### PH4-001 实现缓存服务

| 属性 | 值 |
|------|------|
| 任务ID | PH4-001 |
| 优先级 | P2 |
| 状态 | 待实现 |
| 依赖 | PH1-001 |

**验收标准**:
- CacheService: 内存缓存，TTL过期，自动清理
- CacheConfig: 缓存配置
- CacheKeyGenerator: 缓存Key生成
- CacheStats: 命中/未命中/大小/驱逐统计

### PH4-002 实现WebSocket

| 属性 | 值 |
|------|------|
| 任务ID | PH4-002 |
| 优先级 | P2 |
| 状态 | 待实现 |
| 依赖 | PH1-001 |

**验收标准**:
- DerivationWebSocketHandler: 推导进度实时推送
- 消息格式: type/taskId/status/step/progress/message/timestamp
- 消息类型: derivation_start, derivation_progress, derivation_complete, derivation_error

### PH4-003 实现数据源适配

| 属性 | 值 |
|------|------|
| 任务ID | PH4-003 |
| 优先级 | P2 |
| 状态 | 待实现 |
| 依赖 | PH2-001 |

**验收标准**:
- DataSourceAdapter: 数据源适配器接口
- AbstractDataSourceAdapter: 抽象基类
- BpmDataSourceAdapter: BPM服务数据源适配
- 支持Mock数据模式(use-real-data: false)

### PH4-004 实现Web配置

| 属性 | 值 |
|------|------|
| 任务ID | PH4-004 |
| 优先级 | P2 |
| 状态 | 待实现 |
| 依赖 | PH1-001 |

**验收标准**:
- WebMvcConfig: CORS配置，静态资源映射
- IndexController: 首页路由(重定向到designer/index.html)

---

## 第五阶段: 前端核心

### PH5-001 实现Store状态管理

| 属性 | 值 |
|------|------|
| 任务ID | PH5-001 |
| 优先级 | P0 |
| 状态 | 待实现 |
| 依赖 | PH1-004 |

**验收标准**:
- 事件总线: on/off/_emit
- 流程管理: setProcess/getProcess
- 活动管理: selectActivity/updateActivity/addActivity/removeActivity
- 路由管理: selectRoute/updateRoute/addRoute/removeRoute
- 撤销/重做: undo/redo/_saveHistory (maxHistory=50)
- 自动保存: _triggerAutoSave (delay=1000ms)
- 脏标记: setDirty/dirty:change事件

### PH5-002 实现Canvas画布引擎

| 属性 | 值 |
|------|------|
| 任务ID | PH5-002 |
| 优先级 | P0 |
| 状态 | 待实现 |
| 依赖 | PH5-001 |

**验收标准**:
- SVG图层: 网格层、边层、节点层
- 节点渲染: START(圆形绿), END(圆形红), TASK(圆角矩形蓝), GATEWAY(菱形黄)
- 交互: 拖拽移动、选择、多选、框选、连线绘制
- 缩放: zoomIn/zoomOut/fitToScreen
- Store事件绑定: activity:update, route:update

### PH5-003 实现PanelManager面板系统

| 属性 | 值 |
|------|------|
| 任务ID | PH5-003 |
| 优先级 | P0 |
| 状态 | 待实现 |
| 依赖 | PH5-001 |

**验收标准**:
- 插件注册: register/unregister
- 子插件系统: ActivityPanelPlugins, AgentPanelPlugins, ScenePanelPlugins, ListenerPanelPlugins, ProcessPanelPlugins
- 分类渲染: 通用属性/业务属性/插件属性
- 抽屉式面板: 懒加载渲染
- 自动保存: _triggerAutoSave
- 插件组映射: HUMAN→activity, AGENT→activity-agent, SCENE→activity-scene

### PH5-004 实现前端数据模型

| 属性 | 值 |
|------|------|
| 任务ID | PH5-004 |
| 优先级 | P0 |
| 状态 | 待实现 |
| 依赖 | PH1-004 |

**验收标准**:
- ProcessDef.js: 完整属性、addActivity/removeActivity/addRoute/removeRoute、validate/toJSON/fromJSON/fromBackend/toBackend/clone/getStatistics
- ActivityDef.js: 完整属性、RIGHT/FORM/SERVICE/WORKFLOW属性组、validate/toJSON/fromJSON/clone
- RouteDef.js: 双命名兼容(from/fromActivityDefId)
- DataAdapter.js: 前后端数据适配
- EnumMapping.js: 枚举映射

### PH5-005 实现Chat对话管理

| 属性 | 值 |
|------|------|
| 任务ID | PH5-005 |
| 优先级 | P1 |
| 状态 | 待实现 |
| 依赖 | PH5-001 |

**验收标准**:
- 本地命令: 创建流程/删除流程/导出YAML/帮助
- LLM对话: 上下文构建、NLP API调用、意图识别、操作执行
- UI: 消息列表、输入框、发送按钮、折叠/展开

### PH5-006 实现其他前端组件

| 属性 | 值 |
|------|------|
| 任务ID | PH5-006 |
| 优先级 | P1 |
| 状态 | 待实现 |
| 依赖 | PH5-001 |

**验收标准**:
- Tree.js: 流程树导航
- Elements.js: 拖拽元素面板
- TabManager.js: 多标签页管理
- Toolbar.js: 工具栏
- ContextMenu.js: 右键菜单
- Theme.js: 主题管理(亮色/暗色)

---

## 第六阶段: 集成测试

### PH6-001 后端单元测试

| 属性 | 值 |
|------|------|
| 任务ID | PH6-001 |
| 优先级 | P1 |
| 状态 | 待实现 |
| 依赖 | PH2-002 |

**验收标准**:
- DesignerService测试: DTO转换、流程CRUD
- LLMServiceImpl测试: API调用Mock
- CacheService测试: 缓存操作、TTL过期

### PH6-002 API集成测试

| 属性 | 值 |
|------|------|
| 任务ID | PH6-002 |
| 优先级 | P1 |
| 状态 | 待实现 |
| 依赖 | PH2-002 |

**验收标准**:
- 流程API测试: CRUD操作
- NLP API测试: 对话处理
- 推导API测试: 执行者/能力/表单推导

### PH6-003 前端测试

| 属性 | 值 |
|------|------|
| 任务ID | PH6-003 |
| 优先级 | P2 |
| 状态 | 待实现 |
| 依赖 | PH5-004 |

**验收标准**:
- enum-mapping.test.js: 枚举映射测试
- schema-consistency.test.js: Schema一致性测试
- BpmModelTest.js: BPM模型测试

---

## 任务优先级总览

### P0 - 紧急 (核心骨架)

1. PH1-001: 创建Maven项目结构
2. PH1-002: 创建后端领域模型
3. PH1-003: 创建DTO层
4. PH1-004: 创建前端项目结构
5. PH2-001: 实现DesignerService
6. PH2-002: 实现DesignerController
7. PH5-001: 实现Store状态管理
8. PH5-002: 实现Canvas画布引擎
9. PH5-003: 实现PanelManager面板系统
10. PH5-004: 实现前端数据模型

### P1 - 高优先级 (核心功能)

1. PH2-003: 实现NLP服务
2. PH2-004: 实现推导服务
3. PH3-001: 实现LLM服务
4. PH3-002: 实现Prompt模板系统
5. PH5-005: 实现Chat对话管理
6. PH5-006: 实现其他前端组件
7. PH6-001: 后端单元测试
8. PH6-002: API集成测试

### P2 - 中优先级 (增强功能)

1. PH3-003: 实现Function Calling注册
2. PH4-001: 实现缓存服务
3. PH4-002: 实现WebSocket
4. PH4-003: 实现数据源适配
5. PH4-004: 实现Web配置
6. PH6-003: 前端测试

---

*文档生成时间: 2026-04-13*  
*文档版本: 3.0.2*  
*测试平台: Trae Solo Web (字节跳动)*
