# BPM流程设计器 - 功能检查清单 (面向 Trae Solo Web)

> **文档版本**: 3.0.2  
> **生成日期**: 2026-04-13  
> **项目仓库**: gitee.com/ooderCN/ade  
> **测试目标**: Trae Solo Web 长任务一文到底能力极限测试

---

## 1. 项目结构检查

### 1.1 Maven项目

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ pom.xml存在 | 待验证 | 文件存在检查 | Spring Boot 3.4.4 parent |
| ☐ Java 21配置 | 待验证 | pom.xml properties | maven.compiler.source/target=21 |
| ☐ 所有依赖声明 | 待验证 | pom.xml dependencies | spring-boot-starter-web/websocket/thymeleaf/validation, fastjson2, jackson-dataformat-yaml, lombok |
| ☐ Maven编译插件 | 待验证 | pom.xml build | maven-compiler-plugin 3.11.0 |
| ☐ Maven测试插件 | 待验证 | pom.xml build | maven-surefire-plugin 3.2.5 |
| ☐ Spring Boot打包插件 | 待验证 | pom.xml build | spring-boot-maven-plugin |

### 1.2 包结构

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ BpmDesignerApplication.java | 待验证 | 文件存在 | @SpringBootApplication |
| ☐ cache包 | 待验证 | 目录存在 | CacheConfig, CacheKeyGenerator, CacheService, CacheStats |
| ☐ config包 | 待验证 | 目录存在 | WebMvcConfig |
| ☐ controller包 | 待验证 | 目录存在 | 4个Controller |
| ☐ datasource包 | 待验证 | 目录存在 | 3个Adapter |
| ☐ dto包 | 待验证 | 目录存在 | 4个核心DTO + dto/sub/ 9个子DTO |
| ☐ function包 | 待验证 | 目录存在 | 3个函数相关类 |
| ☐ llm包 | 待验证 | 目录存在 | 4个LLM相关类 |
| ☐ model包 | 待验证 | 目录存在 | 4个Model类 |
| ☐ prompt包 | 待验证 | 目录存在 | 3个Prompt相关类 |
| ☐ service包 | 待验证 | 目录存在 | 6个Service接口 |
| ☐ websocket包 | 待验证 | 目录存在 | DerivationWebSocketHandler |

### 1.3 资源文件

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ application.yml | 待验证 | 文件存在 | 端口8088 |
| ☐ prompts目录 | 待验证 | 目录存在 | 4个YAML模板 |
| ☐ static/designer目录 | 待验证 | 目录存在 | 前端资源 |
| ☐ skill.yaml | 待验证 | 文件存在 | Skill配置 |

---

## 2. 后端模型检查

### 2.1 ProcessDef模型

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ 基本属性(processDefId,name,description) | 待验证 | 代码审查 | |
| ☐ 分类属性(classification,systemCode,accessLevel) | 待验证 | 代码审查 | |
| ☐ 版本信息(version,status,creator/modify) | 待验证 | 代码审查 | |
| ☐ 时限配置(limit,durationUnit) | 待验证 | 代码审查 | |
| ☐ 开始节点(startNode) | 待验证 | 代码审查 | Map类型 |
| ☐ 结束节点列表(endNodes) | 待验证 | 代码审查 | List<Map> |
| ☐ 活动列表(activities) | 待验证 | 代码审查 | List<ActivityDef> |
| ☐ 路由列表(routes) | 待验证 | 代码审查 | List<RouteDef> |
| ☐ 监听器列表(listeners) | 待验证 | 代码审查 | List<Map> |
| ☐ 权限组列表(rightGroups) | 待验证 | 代码审查 | List<Map> |
| ☐ 扩展属性(extendedAttributes,agentConfig,sceneConfig) | 待验证 | 代码审查 | |

### 2.2 ActivityDef模型

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ 基本属性(activityDefId,name,description) | 待验证 | 代码审查 | |
| ☐ 类型属性(position,activityType,activityCategory) | 待验证 | 代码审查 | |
| ☐ 坐标和参与者(positionCoord,participantId) | 待验证 | 代码审查 | |
| ☐ 实现方式(implementation,execClass) | 待验证 | 代码审查 | |
| ☐ 子配置timing | 待验证 | 代码审查 | Map类型 |
| ☐ 子配置routing | 待验证 | 代码审查 | Map类型 |
| ☐ 子配置right | 待验证 | 代码审查 | Map类型 |
| ☐ 子配置subFlow | 待验证 | 代码审查 | Map类型 |
| ☐ 子配置device | 待验证 | 代码审查 | Map类型 |
| ☐ 子配置service | 待验证 | 代码审查 | Map类型 |
| ☐ 子配置event | 待验证 | 代码审查 | Map类型 |
| ☐ 子配置agentConfig | 待验证 | 代码审查 | Map类型 |
| ☐ 子配置sceneConfig | 待验证 | 代码审查 | Map类型 |
| ☐ 扩展属性(extendedAttributes) | 待验证 | 代码审查 | |

### 2.3 RouteDef模型

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ 基本属性(routeDefId,name,description) | 待验证 | 代码审查 | |
| ☐ 连接信息(from,to) | 待验证 | 代码审查 | |
| ☐ 路由属性(routeOrder,routeDirection) | 待验证 | 代码审查 | |
| ☐ 条件配置(routeConditionType,condition) | 待验证 | 代码审查 | |
| ☐ 扩展属性(extendedAttributes) | 待验证 | 代码审查 | |

### 2.4 DTO完整性

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ ProcessDTO字段完整 | 待验证 | 代码审查 | 对照Spec第四章 |
| ☐ ActivityDTO字段完整 | 待验证 | 代码审查 | 含枚举类型转换 |
| ☐ RouteDTO字段完整 | 待验证 | 代码审查 | |
| ☐ PositionCoordDTO | 待验证 | 代码审查 | x(Double), y(Double) |
| ☐ TimingDTO | 待验证 | 代码审查 | 7个字段 |
| ☐ RoutingDTO | 待验证 | 代码审查 | 9个字段 |
| ☐ RightDTO | 待验证 | 代码审查 | 14个字段 |
| ☐ SubFlowDTO | 待验证 | 代码审查 | 7个字段 |
| ☐ DeviceDTO | 待验证 | 代码审查 | 7个字段 |
| ☐ ServiceDTO | 待验证 | 代码审查 | 11个字段 |
| ☐ EventDTO | 待验证 | 代码审查 | 9个字段 |
| ☐ AgentConfigDTO | 待验证 | 代码审查 | 13个字段 |
| ☐ SceneConfigDTO | 待验证 | 代码审查 | 12个字段 |
| ☐ 枚举类(ActivityPosition/ActivityType/ActivityCategory) | 待验证 | 代码审查 | fromCode()/getCode()/getLabel() |

---

## 3. 后端服务检查

### 3.1 DesignerService

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ getProcess() | 待验证 | 单元测试 | DTO→Model转换 |
| ☐ saveProcess() | 待验证 | 单元测试 | Model→DTO转换 |
| ☐ getProcessList() | 待验证 | 单元测试 | |
| ☐ deleteProcess() | 待验证 | 单元测试 | |
| ☐ getProcessTree() | 待验证 | 单元测试 | |
| ☐ addActivity() | 待验证 | 单元测试 | |
| ☐ updateActivity() | 待验证 | 单元测试 | |
| ☐ removeActivity() | 待验证 | 单元测试 | |
| ☐ addRoute() | 待验证 | 单元测试 | |
| ☐ updateRoute() | 待验证 | 单元测试 | |
| ☐ removeRoute() | 待验证 | 单元测试 | |
| ☐ getEnumOptions() | 待验证 | 单元测试 | 3种枚举 |
| ☐ getCapabilities() | 待验证 | 单元测试 | 8种能力 |
| ☐ exportYaml() | 待验证 | 单元测试 | 返回空字符串 |
| ☐ importYaml() | 待验证 | 单元测试 | 返回null |
| ☐ DTO↔Map转换方法(9对) | 待验证 | 代码审查 | Timing/Routing/Right/SubFlow/Device/Service/Event/AgentConfig/SceneConfig |

### 3.2 DesignerController

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ @RequestMapping("/api/bpm") | 待验证 | 代码审查 | |
| ☐ @CrossOrigin(origins = "*") | 待验证 | 代码审查 | |
| ☐ 17个API端点 | 待验证 | 代码审查 | 对照Spec第五章 |
| ☐ ApiResponse统一封装 | 待验证 | 代码审查 | |
| ☐ 异常处理 | 待验证 | 代码审查 | try-catch返回error |

### 3.3 NLP服务

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ DesignerNlpService接口 | 待验证 | 代码审查 | 10个方法 |
| ☐ NlpResponse内部类 | 待验证 | 代码审查 | intent/confidence/entities/action/actionParams/message/suggestions/success |
| ☐ NlpSuggestion内部类 | 待验证 | 代码审查 | type/title/description/action/params |
| ☐ NlpIntent内部类 | 待验证 | 代码审查 | name/confidence/category/slots |
| ☐ DesignerNlpController | 待验证 | 代码审查 | 10个端点 |

### 3.4 推导服务

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ PerformerDerivationService | 待验证 | 代码审查 | 3个方法 |
| ☐ CapabilityMatchingService | 待验证 | 代码审查 | 4个方法 |
| ☐ FormMatchingService | 待验证 | 代码审查 | 4个方法 |
| ☐ PanelRenderService | 待验证 | 代码审查 | 4个方法 |
| ☐ DesignerDerivationController | 待验证 | 代码审查 | 15个端点 |

### 3.5 LLM服务

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ LLMService接口 | 待验证 | 代码审查 | 7个方法 |
| ☐ LLMServiceImpl | 待验证 | 代码审查 | OpenAI兼容API |
| ☐ Bearer Auth认证 | 待验证 | 代码审查 | headers.setBearerAuth |
| ☐ Function Calling支持 | 待验证 | 代码审查 | functions + function_call=auto |
| ☐ 函数结果回传 | 待验证 | 代码审查 | chatWithFunctionResult |
| ☐ LLMResponse模型 | 待验证 | 代码审查 | success/withFunctionCalls/failure |
| ☐ FunctionCall模型 | 待验证 | 代码审查 | name + arguments |
| ☐ isAvailable()检查 | 待验证 | 代码审查 | config + apiKey + restTemplate |

### 3.6 Prompt系统

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ DesignerPromptBuilder | 待验证 | 代码审查 | 5个build方法 |
| ☐ PromptTemplate | 待验证 | 代码审查 | YAML加载+变量替换 |
| ☐ PromptTemplateManager | 待验证 | 代码审查 | 模板注册和获取 |
| ☐ performer-derivation.yaml | 待验证 | 文件存在 | |
| ☐ capability-matching.yaml | 待验证 | 文件存在 | |
| ☐ form-matching.yaml | 待验证 | 文件存在 | |
| ☐ full-derivation.yaml | 待验证 | 文件存在 | 三合一模板 |

### 3.7 Function Calling

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ DesignerFunctionDefinition | 待验证 | 代码审查 | |
| ☐ DesignerFunctionRegistry | 待验证 | 代码审查 | 按类别管理 |
| ☐ 执行者推导函数(5个) | 待验证 | 代码审查 | |
| ☐ 能力匹配函数(4个) | 待验证 | 代码审查 | |
| ☐ 表单匹配函数(5个) | 待验证 | 代码审查 | |

---

## 4. 基础设施检查

### 4.1 缓存服务

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ CacheService | 待验证 | 代码审查 | 内存缓存 |
| ☐ TTL过期机制 | 待验证 | 代码审查 | |
| ☐ 自动清理线程 | 待验证 | 代码审查 | |
| ☐ CacheStats统计 | 待验证 | 代码审查 | hit/miss/size/eviction |
| ☐ CacheConfig配置 | 待验证 | 代码审查 | |
| ☐ CacheKeyGenerator | 待验证 | 代码审查 | |

### 4.2 WebSocket

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ DerivationWebSocketHandler | 待验证 | 代码审查 | |
| ☐ 推导进度推送 | 待验证 | 代码审查 | |
| ☐ 消息格式正确 | 待验证 | 代码审查 | type/taskId/status/step/progress |

### 4.3 数据源适配

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ DataSourceAdapter接口 | 待验证 | 代码审查 | |
| ☐ AbstractDataSourceAdapter | 待验证 | 代码审查 | |
| ☐ BpmDataSourceAdapter | 待验证 | 代码审查 | |
| ☐ Mock数据支持 | 待验证 | 代码审查 | use-real-data: false |

### 4.4 Web配置

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ WebMvcConfig | 待验证 | 代码审查 | CORS + 静态资源 |
| ☐ IndexController | 待验证 | 代码审查 | 首页路由 |

---

## 5. 前端功能检查

### 5.1 App.js

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ 初始化流程完整 | 待验证 | 代码审查 | 11步初始化 |
| ☐ 快捷键绑定 | 待验证 | 代码审查 | Ctrl+S/Z/Y, Delete |
| ☐ 图标初始化 | 待验证 | 代码审查 | RemixIcon映射 |

### 5.2 Canvas.js

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ SVG图层创建 | 待验证 | 代码审查 | 网格/边/节点三层 |
| ☐ 节点渲染(7种类型) | 待验证 | 代码审查 | START/END/TASK/SERVICE/GATEWAY/SUBPROCESS/LLM_TASK |
| ☐ 边渲染 | 待验证 | 代码审查 | |
| ☐ 拖拽移动 | 待验证 | 交互测试 | |
| ☐ 选择/多选 | 待验证 | 交互测试 | |
| ☐ 连线绘制 | 待验证 | 交互测试 | |
| ☐ 缩放/平移 | 待验证 | 交互测试 | |
| ☐ Store事件绑定 | 待验证 | 代码审查 | activity:update, route:update |

### 5.3 Store.js

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ 事件总线(on/off/_emit) | 待验证 | 代码审查 | |
| ☐ 流程管理 | 待验证 | 代码审查 | setProcess/getProcess |
| ☐ 活动管理 | 待验证 | 代码审查 | select/update/add/remove |
| ☐ 路由管理 | 待验证 | 代码审查 | select/update/add/remove |
| ☐ 撤销/重做 | 待验证 | 代码审查 | maxHistory=50 |
| ☐ 自动保存 | 待验证 | 代码审查 | delay=1000ms |
| ☐ 脏标记 | 待验证 | 代码审查 | dirty:change事件 |

### 5.4 PanelManager.js

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ 插件注册 | 待验证 | 代码审查 | register/unregister |
| ☐ 子插件系统 | 待验证 | 代码审查 | 5组子插件 |
| ☐ 分类渲染 | 待验证 | 代码审查 | common/business/plugin |
| ☐ 抽屉式面板 | 待验证 | 代码审查 | 懒加载 |
| ☐ 自动保存 | 待验证 | 代码审查 | _triggerAutoSave |
| ☐ 插件组映射 | 待验证 | 代码审查 | HUMAN/AGENT/SCENE |

### 5.5 前端数据模型

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ ProcessDef.js完整 | 待验证 | 代码审查 | 所有属性+方法 |
| ☐ ActivityDef.js完整 | 待验证 | 代码审查 | 属性组(RIGHT/FORM/SERVICE/WORKFLOW) |
| ☐ RouteDef.js双命名兼容 | 待验证 | 代码审查 | from/fromActivityDefId |
| ☐ DataAdapter.js | 待验证 | 代码审查 | fromBackend/toBackend |
| ☐ EnumMapping.js | 待验证 | 代码审查 | |

### 5.6 Chat.js

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ 本地命令处理 | 待验证 | 代码审查 | 创建/删除/导出/帮助 |
| ☐ LLM对话 | 待验证 | 代码审查 | 上下文构建+API调用 |
| ☐ UI交互 | 待验证 | 交互测试 | 消息列表+输入框 |

### 5.7 其他前端组件

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ Tree.js | 待验证 | 代码审查 | 流程树导航 |
| ☐ Elements.js | 待验证 | 代码审查 | 拖拽元素面板 |
| ☐ TabManager.js | 待验证 | 代码审查 | 多标签页 |
| ☐ Toolbar.js | 待验证 | 代码审查 | 工具栏 |
| ☐ ContextMenu.js | 待验证 | 代码审查 | 右键菜单 |
| ☐ Theme.js | 待验证 | 代码审查 | 亮色/暗色主题 |

---

## 6. 配置检查

### 6.1 应用配置

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ 端口8088 | 待验证 | application.yml | |
| ☐ bpm.server.url | 待验证 | application.yml | http://127.0.0.1:8084/bpm |
| ☐ llm配置完整 | 待验证 | application.yml | provider/model/temperature/max-tokens |
| ☐ datasource配置 | 待验证 | application.yml | 4个服务URL |
| ☐ cache配置 | 待验证 | application.yml | enabled/ttl |
| ☐ management端口8089 | 待验证 | application.yml | actuator |

### 6.2 Skill配置

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ skill.yaml存在 | 待验证 | 文件存在 | |
| ☐ apiVersion/metadata | 待验证 | 代码审查 | |
| ☐ capabilities(3个) | 待验证 | 代码审查 | flow-design/form-binding/node-config |

---

## 7. Trae Solo Web测试专项检查

### 7.1 一文到底测试

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ Spec文档可被完整读取 | 待验证 | Trae Solo Web输入 | 12000+字 |
| ☐ 256K上下文窗口可用 | 待验证 | Trae Solo Web测试 | |
| ☐ 一次性生成所有后端代码 | 待验证 | Trae Solo Web生成 | |
| ☐ 一次性生成所有前端代码 | 待验证 | Trae Solo Web生成 | |
| ☐ 一次性生成所有配置文件 | 待验证 | Trae Solo Web生成 | |

### 7.2 架构理解测试

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ 插件化面板架构正确 | 待验证 | 生成代码审查 | PanelManager + SubPlugins |
| ☐ 事件驱动架构正确 | 待验证 | 生成代码审查 | Store事件总线 |
| ☐ DTO双向转换正确 | 待验证 | 生成代码审查 | Model↔DTO↔Map |
| ☐ 前后端数据适配正确 | 待验证 | 生成代码审查 | DataAdapter |
| ☐ LLM集成架构正确 | 待验证 | 生成代码审查 | Function Calling |

### 7.3 多文件生成测试

| 检查项 | 状态 | 验证方法 | 备注 |
|--------|------|----------|------|
| ☐ Java后端文件(20+) | 待验证 | 文件计数 | |
| ☐ JavaScript前端文件(20+) | 待验证 | 文件计数 | |
| ☐ CSS样式文件(8) | 待验证 | 文件计数 | |
| ☐ YAML配置文件(5) | 待验证 | 文件计数 | |
| ☐ HTML入口文件(1) | 待验证 | 文件计数 | |

---

## 检查统计

| 类别 | 总检查项 | 待验证 | 完成率 |
|------|----------|--------|--------|
| 项目结构 | 22 | 22 | 0% |
| 后端模型 | 31 | 31 | 0% |
| 后端服务 | 42 | 42 | 0% |
| 基础设施 | 13 | 13 | 0% |
| 前端功能 | 35 | 35 | 0% |
| 配置 | 9 | 9 | 0% |
| Trae Solo Web测试 | 13 | 13 | 0% |
| **总计** | **165** | **165** | **0%** |

---

*文档生成时间: 2026-04-13*  
*文档版本: 3.0.2*  
*测试平台: Trae Solo Web (字节跳动)*
