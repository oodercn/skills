# BPM流程设计器 - Spec 补充建议书

> **文档版本**: 2.0
> **生成日期**: 2026-04-13
> **关联文档**: ape-spec-v3.1.md / ape-tasks.md v3.0.2 / ape-checklist.md v3.0.2
> **项目仓库**: gitee.com/ooderCN/ade
> **目的**: 基于 Spec 完整性审查结论，向设计团队提供结构化的补充需求清单

---

## 一、审查背景与总体结论

### 1.1 审查方法

对 `ape-spec.md` v3.0.2 进行了逐章审查，以 `ape-tasks.md`（24个任务）和 `ape-checklist.md`（165个检查项）作为验证基准，交叉比对 Spec 对每个任务目标的支撑程度。

### 1.2 v3.0.2 初审结论（已归档）

| 维度 | v3.0.2覆盖率 | 评级 |
|------|-------------|------|
| 后端数据模型 | 75% | 🟡 |
| 后端API端点 | 95% | 🟢 |
| 后端服务接口 | 60% | 🔴 |
| 前端架构 | 30% | 🔴 |
| LLM集成 | 70% | 🟡 |
| 基础设施 | 85% | 🟢 |
| 配置 | 90% | 🟢 |

### 1.3 v3.1.0 复审结论（当前）

设计团队已根据 v1.0 建议书完成了 **全部 12 项 P0/P1 级补充**，Spec 质量显著提升。

| 维度 | v3.1.0覆盖率 | 评级 | 变化 |
|------|-------------|------|------|
| 后端数据模型 | **95%** | 🟢 | ⬆️ +20% |
| 后端API端点 | **98%** | 🟢 | ⬆️ +3% |
| 后端服务接口 | **85%** | 🟢 | ⬆️ +25% |
| 前端架构 | **85%** | 🟢 | ⬆️ +55% |
| LLM集成 | **95%** | 🟢 | ⬆️ +25% |
| 基础设施 | **85%** | 🟢 | → 持平 |
| 配置 | **90%** | 🟢 | → 持平 |

**核心判断**: v3.1.0 的 Spec 已达到 **可执行状态**，所有 P0/P1 级缺失项已补充完毕。剩余缺失项均为 P2 级（细节完善），不阻塞任务执行。

---

## 二、P0/P1 补充项完成状态

### 2.1 P0 级 — ✅ 全部完成（6/6）

| 补充项ID | 内容 | 补充位置 | 状态 | 质量评价 |
|----------|------|----------|------|----------|
| P0-01 | ActivityDef完整字段定义表 | 4.2.1 | ✅ 已完成 | 22个字段，含必填/默认值，新增块活动属性 |
| P0-02 | 推导服务响应DTO结构 | 4.5 | ✅ 已完成 | 12个子DTO，结构清晰完整 |
| P0-03 | NLP服务内部类字段定义 | 4.6 | ✅ 已完成 | 3个类字段齐全，NlpResponse新增errorMessage |
| P0-04 | 前端JS数据模型规格 | 6.4 | ✅ 已完成 | 3个模型类完整代码实现 |
| P0-05 | DataAdapter.js适配规则 | 6.5 | ✅ 已完成 | 完整双向转换代码，含枚举/日期/null处理 |
| P0-06 | index.html页面布局结构 | 6.6 | ✅ 已完成 | 完整HTML+布局说明+响应式策略+脚本顺序 |

### 2.2 P1 级 — ✅ 全部完成（6/6）

| 补充项ID | 内容 | 补充位置 | 状态 | 质量评价 |
|----------|------|----------|------|----------|
| P1-01 | 枚举DTO行为方法定义 | 4.2.2-4.2.4 | ✅ 已完成 | 3个枚举完整Java代码，含fromCode/getCode/getLabel |
| P1-02 | DesignerNlpService接口 | 5.4 | ✅ 已完成 | 10个方法完整Java接口+Javadoc |
| P1-03 | 4个Prompt YAML模板内容 | 7.4 | ✅ 已完成 | 4个模板均有system_prompt/variables/user_prompt/output_format |
| P1-04 | Function Calling参数Schema | 7.3 | ✅ 已完成 | 14个函数完整OpenAI格式parameters定义 |
| P1-05 | Canvas.js详细交互规格 | 6.2.3 | ✅ 已完成 | 7种交互类型详细规格 |
| P1-06 | Chat.js UI规格和对话流程 | 6.8 | ✅ 已完成 | UI规格+对话流程+上下文构建+错误处理 |

---

## 三、新发现缺失项（P2 级，不阻塞执行）

> 以下为 v3.1.0 深入审查中新发现的缺失项，均为 P2 级，**不阻塞任务执行**，但建议设计团队按需补充以提升生成质量。

### 3.1 [P2-07] 补充 EnumMapping.js 前端枚举映射规格

- **影响任务**: PH5-004（实现前端数据模型）
- **当前状态**: index.html 中引用了 `js/sdk/EnumMapping.js`，但 Spec 未定义该模块的任何内容
- **需补充内容**:

```javascript
// 建议定义前端枚举映射表，与后端枚举保持一致
const EnumMapping = {
    // 活动类型
    ActivityType: {
        TASK: { code: 'TASK', label: '用户任务', icon: 'ri-user-line', color: '#2196F3' },
        SERVICE: { code: 'SERVICE', label: '服务任务', icon: 'ri-settings-line', color: '#FF9800' },
        // ... 其他类型
    },
    // 活动分类
    ActivityCategory: {
        HUMAN: { code: 'HUMAN', label: '人工活动' },
        AGENT: { code: 'AGENT', label: 'Agent活动' },
        SCENE: { code: 'SCENE', label: '场景活动' }
    },
    // 活动位置
    ActivityPosition: {
        START: { code: 'START', label: '开始位置' },
        END: { code: 'END', label: '结束位置' },
        NORMAL: { code: 'NORMAL', label: '普通位置' }
    },
    // 方法
    getLabel(enumType, code) { ... },
    getOptions(enumType) { ... },
    fromCode(enumType, code) { ... }
};
```

**请设计团队确认**:
- 每个枚举值是否需要关联 icon 和 color（用于前端渲染）
- 是否需要其他枚举映射（如路由方向、条件类型等）

---

### 3.2 [P2-08] 补充 Theme.js 主题管理规格

- **影响任务**: PH5-006（实现其他前端组件）
- **当前状态**: App.js 初始化流程中引用了 `ThemeFactory.get()`，但 Spec 未定义 Theme 类的 API
- **需补充内容**:

```javascript
// 建议定义主题管理模块
class ThemeFactory {
    static get() { ... }          // 获取当前主题实例
    static toggle() { ... }       // 切换主题
    static setTheme(name) { ... } // 设置指定主题
}

class Theme {
    constructor(name) { ... }
    name;              // 'light' | 'dark'
    apply();           // 应用主题到document.documentElement
    getCSSVar(key);    // 获取CSS变量值
}
```

**关联**: 需同时定义 var.css 中的 CSS 自定义属性列表（见 P2-09）

---

### 3.3 [P2-09] 补充 CSS 变量主题系统 (var.css)

- **影响任务**: PH1-004（创建前端项目结构）
- **当前状态**: index.html 引用了 `css/var.css`，但 Spec 未定义任何 CSS 自定义属性
- **需补充内容**:

建议至少定义以下 CSS 变量类别：

```css
:root {
    /* 布局 */
    --sidebar-width: 240px;
    --panel-width: 320px;
    --chat-width: 300px;
    --toolbar-height: 40px;
    
    /* 颜色 - 亮色主题 */
    --bg-primary: #ffffff;
    --bg-secondary: #f5f5f5;
    --text-primary: #333333;
    --text-secondary: #666666;
    --border-color: #e0e0e0;
    --primary-color: #2196F3;
    --danger-color: #F44336;
    --success-color: #4CAF50;
    --warning-color: #FFC107;
    
    /* 节点颜色 */
    --node-start: #4CAF50;
    --node-end: #F44336;
    --node-task: #2196F3;
    --node-service: #FF9800;
    --node-gateway: #FFC107;
    --node-subprocess: #673AB7;
    --node-llm: #E91E63;
    
    /* 边 */
    --edge-color: #999999;
    --edge-selected: #2196F3;
    
    /* 阴影/圆角 */
    --shadow-sm: 0 1px 3px rgba(0,0,0,0.1);
    --shadow-md: 0 4px 6px rgba(0,0,0,0.1);
    --radius-sm: 4px;
    --radius-md: 8px;
}

/* 暗色主题 */
[data-theme="dark"] {
    --bg-primary: #1e1e1e;
    --bg-secondary: #2d2d2d;
    --text-primary: #e0e0e0;
    --text-secondary: #a0a0a0;
    --border-color: #404040;
    /* ... */
}
```

---

### 3.4 [P2-10] 补充推导服务 Java 接口方法签名

- **影响任务**: PH2-004（实现推导服务）
- **当前状态**: Spec 通过 API 端点间接描述了推导功能，但 4 个 Service 接口的 Java 方法签名未定义
- **需补充内容**:

请确认以下 4 个 Service 接口的方法签名：

```java
public interface PerformerDerivationService {
    PerformerDerivationResultDTO derive(Map<String, Object> context, String activityDesc);
    PerformerDerivationResultDTO deriveWithCandidates(Map<String, Object> context, String activityDesc, List<String> candidateIds);
    List<PerformerCandidate> searchCandidates(String query, Map<String, Object> filters);
}

public interface CapabilityMatchingService {
    CapabilityMatchingResultDTO match(Map<String, Object> context, String activityDesc);
    CapabilityMatchingResultDTO smartMatch(Map<String, Object> context, String activityDesc);
    CapabilityMatchingResultDTO matchByKeywords(String activityDesc, List<String> keywords);
    Map<String, Object> buildBindingConfig(CapabilityMatchingResultDTO result);
}

public interface FormMatchingService {
    FormMatchingResultDTO match(Map<String, Object> context, String activityDesc);
    FormMatchingResultDTO smartMatch(Map<String, Object> context, String activityDesc);
    FormSchema generateSchema(Map<String, Object> context, String activityDesc);
    FormMatchingResultDTO matchByFields(String activityDesc, List<String> requiredFields);
}

public interface PanelRenderService {
    PanelRenderDataDTO buildPerformerPanel(PerformerDerivationResultDTO result);
    PanelRenderDataDTO buildCapabilityPanel(CapabilityMatchingResultDTO result);
    PanelRenderDataDTO buildFormPanel(FormMatchingResultDTO result);
    PanelRenderDataDTO buildActivityPanel(PerformerDerivationResultDTO performerResult,
                                           CapabilityMatchingResultDTO capabilityResult,
                                           FormMatchingResultDTO formResult);
}
```

---

### 3.5 [P2-11] 补充 DataSourceAdapter 接口和 Mock 数据结构

- **影响任务**: PH4-003（实现数据源适配）
- **当前状态**: Spec 提到 DataSourceAdapter/AbstractDataSourceAdapter/BpmDataSourceAdapter 但未定义接口方法
- **需补充内容**:

```java
public interface DataSourceAdapter {
    ProcessDef getProcess(String processId, Integer version);
    List<ProcessDef> getProcessList(String category, String status, int page, int size);
    List<Map<String, Object>> getProcessTree();
    ProcessDef saveProcess(ProcessDef processDef);
    void deleteProcess(String processId);
    boolean isAvailable();
}

// Mock数据示例（use-real-data: false 时使用）
// 请定义Mock数据的结构和示例值
```

---

### 3.6 [P2-12] 补充完整推导 API (/full) 响应结构

- **影响任务**: PH2-004
- **当前状态**: API 5.3 中 `/full` 端点响应写的是"完整推导结果"，但 JSON 结构未明确定义
- **需补充内容**:

建议定义为三个推导结果的组合（与 full-derivation.yaml 的 output_format 对齐）：

```json
{
    "performerDerivation": { "...PerformerDerivationResultDTO..." },
    "capabilityMatching": { "...CapabilityMatchingResultDTO..." },
    "formMatching": { "...FormMatchingResultDTO..." }
}
```

---

### 3.7 [P2-13] 补充 OptionItem 类型定义

- **影响任务**: P0-02 关联
- **当前状态**: PanelRenderDataDTO.options 字段引用了 `Map<String, List<OptionItem>>`，但 OptionItem 类型未定义
- **需补充内容**:

```java
// 或在前端定义为
{ value: String, label: String, disabled?: Boolean, group?: String }
```

---

### 3.8 [P2-14] 补充前端模块间依赖关系说明

- **影响任务**: PH5-001~006
- **当前状态**: index.html 的 script 加载顺序已给出，但模块构造函数的参数依赖未明确
- **需补充内容**:

建议明确以下依赖关系：

```
Theme.js        → 无依赖（最先加载）
EnumMapping.js  → 无依赖
DataAdapter.js  → 无依赖（纯工具函数）
Store.js        → 依赖 Api.js
Api.js          → 无依赖
ProcessDef.js   → 依赖 ActivityDef.js, DataAdapter.js
ActivityDef.js  → 依赖 DataAdapter.js
RouteDef.js     → 依赖 DataAdapter.js
PanelManager.js → 依赖 Store.js
Canvas.js       → 依赖 Store.js
Chat.js         → 依赖 Store.js, Api.js
Tree.js         → 依赖 Store.js, Api.js
Elements.js     → 依赖 Store.js
Toolbar.js      → 依赖 Store.js, Canvas.js
TabManager.js   → 依赖 Store.js
ContextMenu.js  → 依赖 Store.js, Canvas.js
App.js          → 依赖以上所有模块（最后加载）
```

---

## 四、原 P2 级补充项（保持不变）

> 以下为 v1.0 建议书中的 P2 级项，状态不变。

### 4.1 [P2-01] 补充 Tree/Elements/TabManager/Toolbar/ContextMenu 组件规格

- **影响任务**: PH5-006
- **需补充**: 每个组件的职责、公开方法、事件接口

### 4.2 [P2-02] 补充 CSS 样式指导

- **影响任务**: PH1-004
- **需补充**: 关键组件的样式要求、响应式断点（var.css 已在 P2-09 单独列出）

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

## 五、总结与建议

### 5.1 当前状态总结

| 状态 | 数量 | 说明 |
|------|------|------|
| ✅ 已完成 | 12项 | P0(6) + P1(6) 全部完成 |
| 🟡 建议补充 | 14项 | P2(6原 + 8新)，不阻塞执行 |
| **总计** | **26项** | |

### 5.2 执行建议

**✅ Spec v3.1.0 已达到可执行状态**，建议：

1. **可以开始任务执行** — P0/P1 级缺失已全部补充，核心架构和数据模型定义充分
2. **P2 级按需补充** — 8 个新发现的 P2 项可在开发过程中按需补充，或在 AI 生成代码后进行人工 review 时修正
3. **优先补充建议**（如果时间允许）:
   - P2-07 EnumMapping.js（前端枚举是高频引用模块）
   - P2-09 var.css CSS变量（主题系统基础）
   - P2-10 推导服务接口签名（后端Service层直接需要）

### 5.3 Spec 质量评分

| 维度 | v3.0.2 | v3.1.0 | 提升 |
|------|--------|--------|------|
| **综合评分** | **68/100** | **92/100** | **+24** |
| 后端数据模型 | 75 | 95 | +20 |
| 后端API | 95 | 98 | +3 |
| 后端服务接口 | 60 | 85 | +25 |
| 前端架构 | 30 | 85 | +55 |
| LLM集成 | 70 | 95 | +25 |
| 基础设施 | 85 | 85 | - |
| 配置 | 90 | 90 | - |

---

## 六、附录：审查覆盖率明细

### 6.1 Spec v3.1.0 各章节覆盖评估

| 章节 | 内容 | 覆盖率 | 评级 |
|------|------|--------|------|
| 第一章 项目概述 | 项目信息/定位/边界/术语 | 100% | 🟢 |
| 第二章 技术选型 | 后端/前端/依赖服务/Maven | 100% | 🟢 |
| 第三章 系统架构 | 整体架构/包结构/模块结构/设计原则 | 95% | 🟢 |
| 第四章 数据模型 | ProcessDef/ActivityDef/RouteDef/DTO/推导DTO/NLP类 | 95% | 🟢 |
| 第五章 API规格 | 42个API端点 + NLP Service接口 | 98% | 🟢 |
| 第六章 前端架构 | App/Canvas/Store/Panel/Chat/Api/模型/Adapter/HTML | 85% | 🟢 |
| 第七章 LLM集成 | 服务/Function Schema/Prompt模板 | 95% | 🟢 |
| 第八章 WebSocket | 路径/消息格式 | 90% | 🟢 |
| 第九章 缓存 | 服务/统计 | 100% | 🟢 |
| 第十章 配置 | application.yml/环境变量 | 90% | 🟢 |
| 第十一章 部署 | 构建/启动/健康检查 | 100% | 🟢 |
| 第十二章 兼容性说明 | Trae Solo Web | 100% | 🟢 |
| 第十三章 文件路径索引 | 后端/前端关键文件 | 100% | 🟢 |

### 6.2 全部缺失项影响矩阵

| 缺失项 | 受影响任务数 | 受影响检查项数 | 优先级 | 状态 |
|--------|------------|--------------|--------|------|
| ActivityDef完整字段 | 2 | 8 | P0 | ✅ 已完成 |
| 推导响应DTO | 1 | 5 | P0 | ✅ 已完成 |
| NLP内部类 | 1 | 3 | P0 | ✅ 已完成 |
| 前端JS模型 | 1 | 5 | P0 | ✅ 已完成 |
| DataAdapter | 1 | 1 | P0 | ✅ 已完成 |
| index.html | 1 | 1 | P0 | ✅ 已完成 |
| 枚举DTO方法 | 1 | 1 | P1 | ✅ 已完成 |
| NLP Service接口 | 1 | 1 | P1 | ✅ 已完成 |
| Prompt模板 | 1 | 4 | P1 | ✅ 已完成 |
| Function Schema | 1 | 3 | P1 | ✅ 已完成 |
| Canvas交互 | 1 | 4 | P1 | ✅ 已完成 |
| Chat规格 | 1 | 3 | P1 | ✅ 已完成 |
| EnumMapping.js | 1 | 1 | P2 | 🟡 待补充 |
| Theme.js | 1 | 1 | P2 | 🟡 待补充 |
| var.css CSS变量 | 1 | 1 | P2 | 🟡 待补充 |
| 推导服务接口签名 | 1 | 4 | P2 | 🟡 待补充 |
| DataSourceAdapter | 1 | 3 | P2 | 🟡 待补充 |
| /full 响应结构 | 1 | 1 | P2 | 🟡 待补充 |
| OptionItem类型 | 1 | 1 | P2 | 🟡 待补充 |
| 模块依赖关系 | 1 | 1 | P2 | 🟡 待补充 |
| 组件规格(Tree等) | 1 | 6 | P2 | 🟡 待补充 |
| CSS样式指导 | 1 | 8 | P2 | 🟡 待补充 |
| skill.yaml | 0 | 1 | P2 | 🟡 待补充 |
| Ooder框架 | 0 | 0 | P2 | 🟡 待补充 |
| WebSocket注册 | 1 | 1 | P2 | 🟡 待补充 |
| WebMvcConfig | 1 | 2 | P2 | 🟡 待补充 |

---

*文档生成时间: 2026-04-13*
*文档版本: 2.0*
*基于: ape-spec-v3.1.md 复审结论*
