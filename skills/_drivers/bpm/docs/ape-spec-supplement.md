# BPM流程设计器 - Spec 补充建议书

> **文档版本**: 1.0
> **生成日期**: 2026-04-13
> **关联文档**: ape-spec.md v3.0.2 / ape-tasks.md v3.0.2 / ape-checklist.md v3.0.2
> **项目仓库**: gitee.com/ooderCN/ade
> **目的**: 基于 Spec 完整性审查结论，向设计团队提供结构化的补充需求清单

---

## 一、审查背景与总体结论

### 1.1 审查方法

对 `ape-spec.md` v3.0.2 进行了逐章审查，以 `ape-tasks.md`（24个任务）和 `ape-checklist.md`（165个检查项）作为验证基准，交叉比对 Spec 对每个任务目标的支撑程度。

### 1.2 总体结论

| 维度 | Spec覆盖率 | 能否支撑任务 | 评级 |
|------|-----------|-------------|------|
| 后端数据模型 | 75% | ⚠️ 需补充 | 🟡 |
| 后端API端点 | 95% | ✅ 基本可执行 | 🟢 |
| 后端服务接口 | 60% | ❌ 缺少关键定义 | 🔴 |
| 前端架构 | 30% | ❌ 严重不足 | 🔴 |
| LLM集成 | 70% | ⚠️ 需补充模板内容 | 🟡 |
| 基础设施 | 85% | ✅ 基本可执行 | 🟢 |
| 配置 | 90% | ✅ 可执行 | 🟢 |

**核心判断**: 后端骨架（模型+API+配置）覆盖充分，前端和部分服务层存在关键缺失。建议按本文档优先级补充后，再进入任务执行阶段。

---

## 二、P0 级补充项（必须补充，阻塞任务执行）

> 以下缺失项直接导致对应任务无法正确执行，设计团队必须优先补充。

### 2.1 [P0-01] 补充 ActivityDef 完整字段定义表

- **影响任务**: PH1-002（创建后端领域模型）、CL-2.2（ActivityDef模型检查）
- **当前状态**: Spec 4.2 仅列出枚举定义和属性组（RIGHT/FORM/SERVICE/WORKFLOW），缺少 ActivityDef 自身的完整字段列表
- **需补充内容**:

请按以下格式补充 ActivityDef 的完整字段定义表：

```
| 字段组 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| 基本属性 | activityDefId | String | 活动定义ID |
| 基本属性 | name | String | 活动名称 |
| 基本属性 | description | String | 活动描述 |
| 类型属性 | position | ActivityPosition | 位置枚举(START/END/NORMAL) |
| 类型属性 | activityType | ActivityType | 类型枚举(TASK/SERVICE/...) |
| 类型属性 | activityCategory | ActivityCategory | 分类枚举(HUMAN/AGENT/SCENE) |
| 坐标 | positionCoord | PositionCoord | 坐标对象{x, y} |
| 参与者 | participantId | String | 参与者泳道ID |
| 实现方式 | implementation | Implementation | 实现方式枚举 |
| 实现方式 | execClass | String | 执行类名 |
| 子配置 | timing | TimingDTO | 时限配置 |
| 子配置 | routing | RoutingDTO | 路由配置 |
| 子配置 | right | RightDTO | 权限配置 |
| 子配置 | subFlow | SubFlowDTO | 子流程配置 |
| 子配置 | device | DeviceDTO | 设备配置 |
| 子配置 | service | ServiceDTO | 服务配置 |
| 子配置 | event | EventDTO | 事件配置 |
| 子配置 | agentConfig | AgentConfigDTO | Agent配置 |
| 子配置 | sceneConfig | SceneConfigDTO | 场景配置 |
| 扩展属性 | extendedAttributes | Map | 扩展属性 |
| ... | | | |
```

**请设计团队确认**: 是否还有其他字段（如 formId, formName, formType, formUrl 等是否作为 ActivityDef 顶级字段还是嵌套在某个子配置中）。

---

### 2.2 [P0-02] 补充推导服务响应 DTO 结构

- **影响任务**: PH2-004（实现推导服务）、CL-3.4（推导服务检查）
- **当前状态**: Spec 5.3 定义了推导 API 端点，但所有响应体 DTO 结构完全缺失
- **需补充内容**:

请为以下 6 个 DTO 提供完整字段定义：

**(1) PerformerDerivationResultDTO** — 执行者推导结果
```
需定义字段，建议包含但不限于：
- 推导状态（成功/失败）
- 推荐执行者列表
- 置信度
- 推导依据/推理过程
- 候选人列表
```

**(2) CapabilityMatchingResultDTO** — 能力匹配结果
```
需定义字段，建议包含但不限于：
- 匹配状态
- 推荐能力列表
- 匹配度评分
- 绑定配置建议
```

**(3) FormMatchingResultDTO** — 表单匹配结果
```
需定义字段，建议包含但不限于：
- 匹配状态
- 推荐表单列表
- 字段映射关系
- 匹配度评分
```

**(4) PanelRenderDataDTO** — 面板渲染数据
```
需定义字段，建议包含但不限于：
- 面板类型
- Schema定义
- 默认值
- 可选项列表
- 验证规则
```

**(5) FormSchema** — 表单Schema
```
需定义字段，建议包含但不限于：
- 表单字段定义列表
- 字段类型/校验规则
- 布局信息
```

**(6) PerformerCandidate** — 执行者候选人
```
需定义字段，建议包含但不限于：
- 候选人ID/名称
- 所属部门/角色
- 匹配分数
```

---

### 2.3 [P0-03] 补充 NLP 服务内部类字段定义

- **影响任务**: PH2-003（实现NLP服务）、CL-3.3（NLP服务检查）
- **当前状态**: Spec 5.2 定义了 NLP API 端点，但 NlpResponse/NlpSuggestion/NlpIntent 内部类结构缺失
- **需补充内容**:

请为以下 3 个内部类提供完整字段定义：

**(1) NlpResponse** — NLP统一响应
```
Checklist 要求字段：intent, confidence, entities, action, actionParams, message, suggestions, success
请设计团队确认每个字段的类型和含义。
```

**(2) NlpSuggestion** — NLP建议项
```
Checklist 要求字段：type, title, description, action, params
请设计团队确认每个字段的类型和含义。
```

**(3) NlpIntent** — NLP意图
```
Checklist 要求字段：name, confidence, category, slots
请设计团队确认每个字段的类型和含义。
```

---

### 2.4 [P0-04] 补充前端 JS 数据模型规格

- **影响任务**: PH5-004（实现前端数据模型）、CL-5.5（前端数据模型检查）
- **当前状态**: Spec 第六章完全未提供前端 JS 模型的属性定义和方法签名
- **需补充内容**:

**(1) ProcessDef.js** — 前端流程定义模型

请定义以下内容：
- 完整属性列表（对应后端 ProcessDef 的前端映射）
- 方法签名：`addActivity()`, `removeActivity()`, `addRoute()`, `removeRoute()`, `validate()`, `toJSON()`, `fromJSON()`, `fromBackend()`, `toBackend()`, `clone()`, `getStatistics()`
- 每个方法的入参、返回值、行为描述

**(2) ActivityDef.js** — 前端活动定义模型

请定义以下内容：
- 完整属性列表（含 RIGHT/FORM/SERVICE/WORKFLOW 属性组）
- 方法签名：`validate()`, `toJSON()`, `fromJSON()`, `clone()`
- 属性组的便捷访问方法（如 `getRight()`, `getForm()` 等）

**(3) RouteDef.js** — 前端路由定义模型

请定义以下内容：
- 完整属性列表
- 双命名兼容规则：`from` 与 `fromActivityDefId` 的映射关系
- 方法签名：`toJSON()`, `fromJSON()`, `fromBackend()`, `toBackend()`

---

### 2.5 [P0-05] 补充 DataAdapter.js 前后端数据适配规则

- **影响任务**: PH5-004（实现前端数据模型）、CL-5.5
- **当前状态**: 完全缺失。DataAdapter 是前后端分离架构的关键桥梁
- **需补充内容**:

请定义以下转换规则：

| 方法 | 方向 | 说明 |
|------|------|------|
| `fromBackend(backendProcessDef)` | 后端→前端 | 将后端 ProcessDef JSON 转换为前端 ProcessDef.js 对象 |
| `toBackend(frontendProcessDef)` | 前端→后端 | 将前端 ProcessDef.js 对象转换为后端 API 所需的 JSON 格式 |

需明确说明：
- 字段命名映射（如有差异）
- 枚举值转换规则（前端字符串 ↔ 后端枚举）
- 日期格式转换
- 嵌套对象的递归转换规则
- null/undefined 的处理策略

---

### 2.6 [P0-06] 补充 index.html 页面布局结构

- **影响任务**: PH1-004（创建前端项目结构）、CL-5.1
- **当前状态**: 完全缺失
- **需补充内容**:

请提供 index.html 的 DOM 结构骨架，至少包含：

```
页面整体布局：
├── 左侧边栏
│   ├── 流程树导航 (Tree.js)
│   └── 拖拽元素面板 (Elements.js)
├── 中间主区域
│   ├── 工具栏 (Toolbar.js)
│   ├── 标签页管理 (TabManager.js)
│   └── SVG画布 (Canvas.js)
├── 右侧属性面板 (PanelManager.js)
│   └── 抽屉式面板容器
└── AI对话面板 (Chat.js)
    ├── 消息列表
    └── 输入区域

请补充：
- 各区域的 HTML 标签结构和 CSS class 命名
- 区域间的布局方式（flex/grid）
- 响应式策略（如有）
- 各容器元素的 ID，供 JS 初始化时引用
```

---

## 三、P1 级补充项（建议补充，影响生成质量）

> 以下缺失项不会完全阻塞任务执行，但会导致 AI 生成大量"猜测性"代码，影响最终质量。

### 3.1 [P1-01] 补充枚举 DTO 行为方法定义

- **影响任务**: PH1-003（创建DTO层）、CL-2.4
- **当前状态**: Spec 定义了枚举值，但未描述枚举类的行为方法
- **需补充内容**:

请确认枚举 DTO 的行为接口：

```java
// 请确认是否需要以下方法，以及具体签名：
public enum ActivityType {
    TASK("TASK", "用户任务"),
    SERVICE("SERVICE", "服务任务"),
    // ...

    private final String code;
    private final String label;

    public static ActivityType fromCode(String code);  // 从代码获取枚举
    public String getCode();                            // 获取代码
    public String getLabel();                           // 获取中文标签
}
// ActivityPosition、ActivityCategory 是否也遵循相同模式？
```

---

### 3.2 [P1-02] 补充 DesignerNlpService 接口方法签名

- **影响任务**: PH2-003（实现NLP服务）
- **当前状态**: Spec 仅通过 API 端点间接描述，缺少 Service 层接口定义
- **需补充内容**:

请提供 DesignerNlpService 接口的完整方法签名：

```java
public interface DesignerNlpService {
    // Checklist 要求 10 个方法，请确认每个方法的签名：
    NlpResponse chat(String message, Map<String, Object> context);
    ProcessDTO createProcessFromNlp(String description, Map<String, Object> context);
    ActivityDTO createActivityFromNlp(String description, Map<String, Object> context);
    Map<String, Object> updateAttributeFromNlp(String attributeName, Object value, Map<String, Object> context);
    List<NlpSuggestion> getSuggestions(Map<String, Object> context);
    String validateProcess(ProcessDef processDef, Map<String, Object> context);
    String describeProcess(ProcessDef processDef);
    String describeActivity(ActivityDef activityDef);
    List<NlpIntent> analyzeIntent(String userInput);
    Map<String, Object> extractEntities(String userInput, String intentType);
}
```

---

### 3.3 [P1-03] 补充 4 个 Prompt YAML 模板内容

- **影响任务**: PH3-002（实现Prompt模板系统）
- **当前状态**: Spec 7.4 列出了模板文件名，但未提供模板内容
- **需补充内容**:

请提供以下 4 个 YAML 模板的完整内容（或至少提供核心结构）：

| 文件 | 用途 | 需定义内容 |
|------|------|-----------|
| `performer-derivation.yaml` | 执行者推导 | 角色定义、上下文模板、输出格式要求、示例 |
| `capability-matching.yaml` | 能力匹配 | 角色定义、匹配规则、输出格式要求、示例 |
| `form-matching.yaml` | 表单匹配 | 角色定义、匹配规则、输出格式要求、示例 |
| `full-derivation.yaml` | 完整推导(三合一) | 综合角色定义、分步推导流程、输出格式要求 |

每个模板建议包含：
```yaml
system_prompt: |
  你是BPM流程设计助手...
variables:
  - name: context
    description: 当前上下文
  - name: activityDesc
    description: 活动描述
user_prompt_template: |
  根据以下信息...
  上下文: {{context}}
  活动描述: {{activityDesc}}
output_format: |
  请以JSON格式返回...
examples:
  - input: "..."
    output: "..."
```

---

### 3.4 [P1-04] 补充 Function Calling 函数的 Parameters Schema

- **影响任务**: PH3-003（实现Function Calling注册）
- **当前状态**: Spec 7.3 仅列出 14 个函数名称，缺少参数 Schema 定义
- **需补充内容**:

请为以下 14 个函数提供 OpenAI Function Calling 格式的 parameters 定义：

**执行者推导 (5个)**:
- `get_organization_tree` — 获取组织架构树
- `get_users_by_role` — 按角色获取用户
- `search_users` — 搜索用户
- `get_department_leader` — 获取部门负责人
- `list_roles` — 列出所有角色

**能力匹配 (4个)**:
- `list_capabilities` — 列出所有能力
- `search_capabilities` — 搜索能力
- `get_capability_detail` — 获取能力详情
- `match_capability_by_activity` — 按活动匹配能力

**表单匹配 (5个)**:
- `list_forms` — 列出所有表单
- `search_forms` — 搜索表单
- `get_form_schema` — 获取表单Schema
- `match_form_by_activity` — 按活动匹配表单
- `generate_form_schema` — 生成表单Schema

每个函数需定义：
```json
{
  "name": "function_name",
  "description": "函数描述",
  "parameters": {
    "type": "object",
    "properties": {
      "param1": { "type": "string", "description": "参数说明" }
    },
    "required": ["param1"]
  }
}
```

---

### 3.5 [P1-05] 补充 Canvas.js 详细交互规格

- **影响任务**: PH5-002（实现Canvas画布引擎）
- **当前状态**: Spec 6.2 仅列出交互类型名称，缺少详细行为规格
- **需补充内容**:

| 交互类型 | 需定义内容 |
|----------|-----------|
| 拖拽移动节点 | 触发条件、拖拽过程、释放行为、边界约束 |
| 选择/多选 | 单击选择、Ctrl+点击多选、框选范围计算 |
| 连线绘制 | 起始锚点、路径计算、终点吸附、条件设置 |
| 缩放 | 缩放比例范围、缩放中心点、快捷键 |
| 平移 | 触发方式（拖拽空白区域？中键？）、边界约束 |
| 右键菜单 | 触发条件、菜单项根据节点类型动态变化 |
| 删除 | Delete键删除选中元素、确认机制 |

---

### 3.6 [P1-06] 补充 Chat.js UI 规格和 LLM 对话流程

- **影响任务**: PH5-005（实现Chat对话管理）
- **当前状态**: Spec 6.5 仅列出本地命令类型
- **需补充内容**:

**(1) UI 规格**:
- 消息列表的展示格式（用户消息/AI消息/系统消息的区分样式）
- 输入区域布局（输入框+发送按钮+折叠/展开按钮）
- 面板折叠/展开的交互行为
- 消息中的操作按钮（如"应用建议"按钮）

**(2) LLM 对话流程**:
- 上下文构建规则：发送给 NLP API 的 context 包含哪些信息
- 意图识别后的操作执行流程
- 推导结果的展示方式
- 错误处理和重试机制

---

## 四、P2 级补充项（可选补充，完善细节）

> 以下缺失项影响较小，设计团队可根据时间安排选择性补充。

### 4.1 [P2-01] 补充 Tree/Elements/TabManager/Toolbar/ContextMenu/Theme 组件规格

- **影响任务**: PH5-006
- **需补充**: 每个组件的职责、公开方法、事件接口

### 4.2 [P2-02] 补充 CSS 样式指导

- **影响任务**: PH1-004
- **需补充**: 主题变量定义（亮色/暗色）、关键组件的样式要求、响应式断点

### 4.3 [P2-03] 补充 skill.yaml 内容

- **影响任务**: CL-6.2
- **需补充**: skill.yaml 的完整 YAML 结构和内容

### 4.4 [P2-04] 补充 Ooder 框架用法说明

- **影响任务**: 全前端任务
- **需补充**: Ooder 框架的核心 API、注解驱动方式、组件注册机制

### 4.5 [P2-05] 补充 WebSocket 端点注册方式

- **影响任务**: PH4-002
- **需补充**: WebSocketConfigurer 注册配置、CORS 处理

### 4.6 [P2-06] 补充 WebMvcConfig 详细配置

- **影响任务**: PH4-004
- **需补充**: CORS 具体规则、静态资源映射路径

---

## 五、补充执行建议

### 5.1 推荐补充顺序

```
第一批 (阻塞后端开发):
  P0-01 ActivityDef字段 → P0-02 推导DTO → P0-03 NLP内部类

第二批 (阻塞前端开发):
  P0-04 前端JS模型 → P0-05 DataAdapter → P0-06 index.html

第三批 (提升质量):
  P1-01~P1-06 按需补充

第四批 (完善细节):
  P2-01~P2-06 按需补充
```

### 5.2 补充格式建议

设计团队补充时，建议直接在 `ape-spec.md` 对应章节中追加内容，保持文档格式一致：
- 数据模型补充到 **第四章**
- API/服务接口补充到 **第五章**
- 前端规格补充到 **第六章**
- LLM 相关补充到 **第七章**
- 配置相关补充到 **第十章**

### 5.3 补充完成标志

每项补充完成后，在对应 Checklist 检查项的"备注"列标注"已补充"，便于追踪进度。

---

## 六、附录：审查覆盖率明细

### 6.1 Spec 各章节覆盖评估

| 章节 | 内容 | 覆盖率 | 评级 |
|------|------|--------|------|
| 第一章 项目概述 | 项目信息/定位/边界/术语 | 100% | 🟢 |
| 第二章 技术选型 | 后端/前端/依赖服务/Maven | 100% | 🟢 |
| 第三章 系统架构 | 整体架构/包结构/模块结构/设计原则 | 95% | 🟢 |
| 第四章 数据模型 | ProcessDef/ActivityDef/RouteDef/DTO | 75% | 🟡 |
| 第五章 API规格 | 42个API端点 | 90% | 🟢 |
| 第六章 前端架构 | App/Canvas/Store/Panel/Chat/Api | 30% | 🔴 |
| 第七章 LLM集成 | 服务/Function Calling/Prompt | 70% | 🟡 |
| 第八章 WebSocket | 路径/消息格式 | 90% | 🟢 |
| 第九章 缓存 | 服务/统计 | 100% | 🟢 |
| 第十章 配置 | application.yml/环境变量 | 90% | 🟢 |
| 第十一章 部署 | 构建/启动/健康检查 | 100% | 🟢 |
| 第十二章 兼容性说明 | Trae Solo Web | 100% | 🟢 |
| 第十三章 文件路径索引 | 后端/前端关键文件 | 100% | 🟢 |

### 6.2 缺失项影响矩阵

| 缺失项 | 受影响任务数 | 受影响检查项数 | 优先级 |
|--------|------------|--------------|--------|
| ActivityDef完整字段 | 2 | 8 | P0 |
| 推导响应DTO | 1 | 5 | P0 |
| NLP内部类 | 1 | 3 | P0 |
| 前端JS模型 | 1 | 5 | P0 |
| DataAdapter | 1 | 1 | P0 |
| index.html | 1 | 1 | P0 |
| 枚举DTO方法 | 1 | 1 | P1 |
| NLP Service接口 | 1 | 1 | P1 |
| Prompt模板 | 1 | 4 | P1 |
| Function Schema | 1 | 3 | P1 |
| Canvas交互 | 1 | 4 | P1 |
| Chat规格 | 1 | 3 | P1 |

---

*文档生成时间: 2026-04-13*
*文档版本: 1.0*
*基于: ape-spec.md v3.0.2 完整性审查结论*
