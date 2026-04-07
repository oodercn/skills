# Web 流程设计器设计文档

**文档版本**: v2.0  
**创建日期**: 2026-04-06  
**更新日期**: 2026-04-06  
**参考设计**: XPDL Swing 设计器 (esdbpd)  
**技术架构**: nexus + Spring Boot  
**项目路径**: E:\github\ooder-skills

---

## 目录

1. [设计目标](#1-设计目标)
2. [技术选型](#2-技术选型)
3. [整体架构](#3-整体架构)
4. [UI组件库设计](#4-ui组件库设计)
5. [面板设计](#5-面板设计)
6. [数据流设计](#6-数据流设计)
7. [API设计](#7-api设计)
8. [实施路线图](#8-实施路线图)

---

## 1. 设计目标

### 1.1 核心目标

| 目标 | 说明 |
|------|------|
| **复刻 XPDL 设计器** | 参考现有 Swing 版本设计器，实现 Web 版本 |
| **支持完整流程定义** | 流程/活动/路由/监听器/公式/参数/扩展属性 |
| **支持 Agent 扩展** | 新增 Agent 活动类型、调度策略、协作模式配置 |
| **支持 A2UI 扩展** | 新增场景定义、PageAgent 配置、Function Calling 配置 |
| **可视化设计** | 拖拽式流程设计，所见即所得 |
| **YAML 导入导出** | 支持 YAML 格式的流程定义导入导出 |
| **复用 nexus 架构** | 基于现有 nexus 前端架构，不引入 Vue 3 |

### 1.2 设计原则

| 原则 | 说明 |
|------|------|
| **架构一致性** | 与现有 nexus 控制台保持技术栈一致 |
| **组件复用** | 复用 nexus CSS 模块和 SDK 封装模式 |
| **渐进增强** | 先实现核心功能，后期推出真正 A2UI 架构 |
| **简洁实用** | 不做太复杂，满足当前需求即可 |

---

## 2. 技术选型

### 2.1 前端技术栈

| 层级 | 技术选型 | 说明 |
|------|----------|------|
| **页面结构** | HTML5 | 纯 HTML 页面，与 nexus 一致 |
| **样式系统** | CSS3 + CSS变量 | 复用 nexus CSS 模块化设计 |
| **脚本语言** | JavaScript (ES6+) | 原生 JS，模块化组织 |
| **流程图** | bpmn-js | BPMN 2.0 流程设计器核心 |
| **代码编辑** | Monaco Editor | 代码/表达式编辑 |
| **图标库** | Remix Icon | 与 nexus 一致 |
| **主题系统** | CSS 变量 + 主题切换 | 复用 nexus 主题系统 |

### 2.2 后端技术栈

| 层级 | 技术选品 | 说明 |
|------|----------|------|
| **框架** | Spring Boot 3.x | 后端框架 |
| **场景引擎** | SceneEngine | 场景定义和执行 |
| **流程引擎** | ooder BPM | 现有 BPM 引擎 |
| **数据存储** | VFS + MySQL | 文件存储 + 数据库 |
| **协议支持** | ooderSDK | MCP/ROUTE/END 协议 |

### 2.3 技术选型说明

#### 2.3.1 为什么不用 Vue 3

| 考量因素 | 说明 |
|----------|------|
| **架构一致性** | nexus 控制台使用纯 HTML + JS，引入 Vue 会增加复杂度 |
| **维护成本** | 团队熟悉现有 nexus 架构，无额外学习成本 |
| **后期规划** | 后期会推出真正的 A2UI 架构，当前版本过渡使用 |
| **性能考量** | 原生 JS 更轻量，无框架运行时开销 |

#### 2.3.2 流程图库选择

| 方案 | 优点 | 缺点 | 推荐度 |
|------|------|------|--------|
| **bpmn-js** | BPMN 2.0 标准、功能完善 | 较重量级 | ★★★★★ |
| **LogicFlow** | 国产开源、轻量级 | BPMN 支持有限 | ★★★★☆ |
| **AntV X6** | 功能强大 | 需要自行实现 BPMN 语义 | ★★★★☆ |

**结论**：推荐使用 **bpmn-js**，符合 BPMN 2.0 标准，支持完整的流程定义。

#### 2.3.3 UI 组件复用

基于现有 nexus CSS 模块设计：

```
nexus CSS 模块结构：
├── css/
│   ├── theme.css           # 主题变量
│   ├── main.css            # 主样式
│   ├── nexus.css           # nexus 组件样式
│   └── modules/
│       ├── base/           # 基础样式
│       ├── layout/         # 布局样式
│       ├── components/     # 组件样式
│       └── features/       # 功能样式
```

---

## 3. 整体架构

### 3.1 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Web 流程设计器架构                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                 前端层 (HTML + JS + CSS)              │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │ 流程画布 │ │ 属性面板 │ │ 工具栏  │ │ 组件库  │   │  │
│   │  │(bpmn-js)│ │(nexus)  │ │(Toolbar)│ │(Palette)│   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │ 监听器  │ │ 公式面板 │ │ 参数面板 │ │ 扩展属性 │   │  │
│   │  │ 面板    │ │         │ │         │ │ 面板    │   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐              │  │
│   │  │ Agent   │ │ A2UI    │ │ 场景    │              │  │
│   │  │ 面板    │ │ 面板    │ │ 面板    │              │  │
│   │  └─────────┘ └─────────┘ └─────────┘              │  │
│   │  ┌─────────────────────────────────────────────┐   │  │
│   │  │              NexusSDK (API 封装)             │   │  │
│   │  └─────────────────────────────────────────────┘   │  │
│   └─────────────────────────────────────────────────────┘  │
│                           │                                │
│                           ▼                                │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                    API 层 (REST)                     │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │/process │ │/activity│ │/route   │ │/listener│   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │/formula │ │/param   │ │/extend  │ │/scene   │   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   └─────────────────────────────────────────────────────┘  │
│                           │                                │
│                           ▼                                │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                    服务层 (Spring Boot)              │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │Process  │ │Activity │ │Route    │ │Listener │   │  │
│   │  │Service  │ │Service  │ │Service  │ │Service  │   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │SceneDef │ │AgentDef │ │Formula  │ │Extend   │   │  │
│   │  │Service  │ │Service  │ │Service  │ │Service  │   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   └─────────────────────────────────────────────────────┘  │
│                           │                                │
│                           ▼                                │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                    存储层                            │  │
│   │  ┌─────────────────┐    ┌─────────────────────┐    │  │
│   │  │     VFS 存储     │    │     MySQL 存储      │    │  │
│   │  │  (YAML 文件)     │    │  (运行期数据)       │    │  │
│   │  └─────────────────┘    └─────────────────────┘    │  │
│   └─────────────────────────────────────────────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 前端模块结构

```
bpm-designer/
├── pages/
│   └── bpm/
│       ├── designer.html              # 设计器主页面
│       ├── process-list.html          # 流程列表页面
│       └── process-import.html        # 导入导出页面
├── js/
│   ├── bpm/
│   │   ├── designer.js                # 设计器主逻辑
│   │   ├── canvas.js                  # 画布组件
│   │   ├── palette.js                 # 组件库
│   │   ├── toolbar.js                 # 工具栏
│   │   ├── panels/                    # 面板模块
│   │   │   ├── process-panel.js       # 流程属性面板
│   │   │   ├── activity-panel.js      # 活动属性面板
│   │   │   ├── route-panel.js         # 路由属性面板
│   │   │   ├── listener-panel.js      # 监听器面板
│   │   │   ├── formula-panel.js       # 公式面板
│   │   │   ├── param-panel.js         # 参数面板
│   │   │   ├── extend-panel.js        # 扩展属性面板
│   │   │   ├── agent-panel.js         # Agent面板
│   │   │   ├── a2ui-panel.js          # A2UI面板
│   │   │   └── scene-panel.js         # 场景面板
│   │   ├── models/                    # 数据模型
│   │   │   ├── ProcessDef.js          # 流程定义
│   │   │   ├── ActivityDef.js         # 活动定义
│   │   │   ├── RouteDef.js            # 路由定义
│   │   │   ├── AgentDef.js            # Agent定义
│   │   │   └── SceneDef.js            # 场景定义
│   │   ├── utils/                     # 工具函数
│   │   │   ├── yaml-converter.js      # YAML 转换
│   │   │   ├── bpmn-converter.js      # BPMN 转换
│   │   │   └── validators.js          # 验证器
│   │   └── sdk/
│   │       └── BpmSDK.js              # BPM SDK 封装
│   └── common/                        # 公共模块（复用nexus）
│       ├── utils.js
│       ├── modal-manager.js
│       ├── form-manager.js
│       └── api-client.js
├── css/
│   ├── bpm-designer.css               # 设计器样式
│   ├── canvas.css                     # 画布样式
│   ├── panels.css                     # 面板样式
│   └── palette.css                    # 组件库样式
└── lib/
    ├── bpmn-js/                       # bpmn-js 库
    └── monaco-editor/                 # Monaco 编辑器
```

### 3.3 SDK 封装设计

参考 nexus SDK 封装模式：

```javascript
class BpmSDK {
    constructor(config) {
        this.baseUrl = config.baseUrl || '/api/bpm';
        this.timeout = config.timeout || 30000;
    }

    async initialize(config) {
        console.log('[BpmSDK] Initializing...');
        return { status: 'success' };
    }

    async getProcess(processId, version) {
        return this._request('GET', `/process/${processId}/version/${version}`);
    }

    async saveProcess(processDef) {
        return this._request('POST', '/process', processDef);
    }

    async exportYaml(processId) {
        return this._request('GET', `/process/${processId}/export/yaml`);
    }

    async importYaml(yamlContent) {
        return this._request('POST', '/process/import/yaml', { yaml: yamlContent });
    }

    async _request(method, endpoint, data) {
        const url = this.baseUrl + endpoint;
        const options = {
            method: method,
            headers: { 'Content-Type': 'application/json' }
        };
        if (data) {
            options.body = JSON.stringify(data);
        }
        const response = await fetch(url, options);
        return response.json();
    }
}

const BpmSDKFactory = {
    _currentSDK: null,
    createSDK: function(config) {
        if (!this._currentSDK) {
            this._currentSDK = new BpmSDK(config || {});
        }
        return this._currentSDK;
    },
    getCurrentSDK: function() {
        return this._currentSDK || this.createSDK();
    }
};

window.bpmSDK = BpmSDKFactory.getCurrentSDK();
```

---

## 4. UI组件库设计

### 4.1 复用 nexus CSS 变量

```css
:root {
    --nx-primary: #1a73e8;
    --nx-primary-hover: #2563eb;
    --ns-dark: #f0f0f0;
    --ns-secondary: #9aa0a6;
    --ns-card-bg: #1e1e1e;
    --ns-border: #3c4043;
    --ns-background: #121212;
    --ns-input-bg: #2d2d2d;
    --ns-success: #22c55e;
    --ns-danger: #ef4444;
    --ns-warning: #f59e0b;
    --ns-radius: 6px;
    --ns-shadow: 0 2px 4px rgba(0,0,0,0.3);
}
```

### 4.2 设计器专用组件

#### 4.2.1 设计器布局组件

```css
.bpm-designer {
    display: flex;
    height: 100vh;
    background: var(--ns-background);
}

.bpm-designer__palette {
    width: 240px;
    border-right: 1px solid var(--ns-border);
    background: var(--ns-card-bg);
}

.bpm-designer__canvas {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.bpm-designer__toolbar {
    height: 48px;
    border-bottom: 1px solid var(--ns-border);
    background: var(--ns-card-bg);
}

.bpm-designer__main {
    flex: 1;
    display: flex;
}

.bpm-designer__viewport {
    flex: 1;
    position: relative;
}

.bpm-designer__panel {
    width: 360px;
    border-left: 1px solid var(--ns-border);
    background: var(--ns-card-bg);
    overflow-y: auto;
}
```

#### 4.2.2 组件库样式

```css
.bpm-palette {
    padding: 16px;
}

.bpm-palette__group {
    margin-bottom: 16px;
}

.bpm-palette__group-title {
    font-size: 12px;
    font-weight: 600;
    color: var(--ns-secondary);
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-bottom: 8px;
}

.bpm-palette__items {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
}

.bpm-palette__item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 12px 8px;
    border-radius: var(--ns-radius);
    background: var(--ns-input-bg);
    cursor: grab;
    transition: all 0.2s;
}

.bpm-palette__item:hover {
    background: var(--ns-bg-hover);
    transform: translateY(-2px);
}

.bpm-palette__item-icon {
    font-size: 24px;
    margin-bottom: 4px;
    color: var(--nx-primary);
}

.bpm-palette__item-name {
    font-size: 11px;
    color: var(--ns-dark);
    text-align: center;
}
```

#### 4.2.3 属性面板样式

```css
.bpm-panel {
    padding: 16px;
}

.bpm-panel__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--ns-border);
}

.bpm-panel__title {
    font-size: 16px;
    font-weight: 600;
    color: var(--ns-dark);
}

.bpm-panel__tabs {
    display: flex;
    gap: 4px;
    margin-bottom: 16px;
    border-bottom: 1px solid var(--ns-border);
    padding-bottom: 8px;
}

.bpm-panel__tab {
    padding: 8px 12px;
    font-size: 13px;
    color: var(--ns-secondary);
    background: transparent;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;
}

.bpm-panel__tab:hover {
    background: var(--ns-bg-hover);
}

.bpm-panel__tab.active {
    background: var(--nx-primary);
    color: white;
}

.bpm-panel__section {
    margin-bottom: 20px;
}

.bpm-panel__section-title {
    font-size: 13px;
    font-weight: 600;
    color: var(--ns-dark);
    margin-bottom: 12px;
}

.bpm-panel__row {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    margin-bottom: 12px;
}

.bpm-panel__field {
    margin-bottom: 12px;
}

.bpm-panel__label {
    display: block;
    font-size: 12px;
    font-weight: 500;
    color: var(--ns-secondary);
    margin-bottom: 4px;
}

.bpm-panel__input {
    width: 100%;
    padding: 8px 12px;
    font-size: 13px;
    color: var(--ns-dark);
    background: var(--ns-input-bg);
    border: 1px solid var(--ns-border);
    border-radius: var(--ns-radius);
}

.bpm-panel__input:focus {
    outline: none;
    border-color: var(--nx-primary);
}

.bpm-panel__select {
    width: 100%;
    padding: 8px 12px;
    font-size: 13px;
    color: var(--ns-dark);
    background: var(--ns-input-bg);
    border: 1px solid var(--ns-border);
    border-radius: var(--ns-radius);
    cursor: pointer;
}
```

### 4.3 组件库节点类型

| 分组 | 节点类型 | 图标 | CSS类 | 说明 |
|------|----------|------|-------|------|
| **基础活动** | 开始事件 | ri-play-circle-line | bpm-node--start | 流程开始 |
| | 结束事件 | ri-stop-circle-line | bpm-node--end | 流程结束 |
| | 用户任务 | ri-user-line | bpm-node--user | 人工活动 |
| | 服务任务 | ri-server-line | bpm-node--service | 自动活动 |
| | 脚本任务 | ri-code-line | bpm-node--script | 脚本活动 |
| **网关** | 排他网关 | ri-git-branch-line | bpm-node--gateway-xor | XOR 网关 |
| | 并行网关 | ri-git-merge-line | bpm-node--gateway-and | AND 网关 |
| | 包容网关 | ri-git-commit-line | bpm-node--gateway-or | OR 网关 |
| **子流程** | 调用活动 | ri-loop-left-line | bpm-node--call | 子流程调用 |
| **Agent扩展** | LLM任务 | ri-robot-line | bpm-node--llm | LLM 活动 |
| | Agent任务 | ri-cpu-line | bpm-node--agent | Agent 活动 |
| | 协调器 | ri-team-line | bpm-node--coordinator | 多 Agent 协调 |
| **场景扩展** | 场景活动 | ri-layout-grid-line | bpm-node--scene | 场景组合 |

---

## 5. 面板设计

### 5.1 流程属性面板

```
┌─────────────────────────────────────────────────────────────┐
│  流程属性                                                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 基本信息                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ 流程名称: [________________________]                │   │
│  │ 流程描述: [________________________]                │   │
│  │ 流程分类: [下拉选择____] [所属应用: 下拉选择____]   │   │
│  │ 访问级别: ○ 独立启动  ○ 子流程  ○ 流程块           │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 版本管理                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ 版本号: [1]  状态: [已发布___]                       │   │
│  │ 完成期限: [__] [日▼]                                │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 表单配置                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ 标识类型: [全流程唯一___▼]                          │   │
│  │ 锁定策略: [锁定数据___▼]                            │   │
│  │ □ 自动保存                                          │   │
│  │ 关联表名: [多选标签...]                             │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 监听器                                               │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ [+ 添加监听器]                                       │   │
│  │ ┌─────────────────────────────────────────────┐     │   │
│  │ │ 名称        事件类型      实现类             │     │   │
│  │ │ 流程启动    STARTED      com.example...     │     │   │
│  │ │ 流程完成    COMPLETED    com.example...     │     │   │
│  │ └─────────────────────────────────────────────┘     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 扩展属性                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ [+ 添加属性]                                         │   │
│  │ ┌─────────────────────────────────────────────┐     │   │
│  │ │ 属性名      属性值                           │     │   │
│  │ │ department  HR                              │     │   │
│  │ │ priority    HIGH                            │     │   │
│  │ └─────────────────────────────────────────────┘     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 活动属性面板

```
┌─────────────────────────────────────────────────────────────┐
│  活动属性                                                    │
├─────────────────────────────────────────────────────────────┤
│  [基本信息] [时限设置] [路由设置] [权限设置] [Agent] [场景] │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 基本信息                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ 活动名称: [简历筛选________]                        │   │
│  │ 活动描述: [________________]                        │   │
│  │ 活动位置: ○ 普通  ○ 起始  ○ 结束                   │   │
│  │ 活动分类: ○ 人工  ○ Agent  ○ 场景                  │   │
│  │ 实现方式: [手动活动___▼]                            │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 时限设置                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ 时间限制: [3] [日▼]                                 │   │
│  │ 报警时间: [1] [日▼]                                 │   │
│  │ 到期处理: [延期办理___▼]                            │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 路由设置                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ 等待合并: ○ 默认  ○ AND  ○ XOR                     │   │
│  │ 并行处理: ○ 默认  ○ AND  ○ XOR                     │   │
│  │ □ 允许退回  退回路径: [上一活动▼]                   │   │
│  │ □ 允许特送  特送范围: [所有人___▼]                  │   │
│  │ □ 允许补发                                          │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 权限设置                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ 办理类型: [多人办理___▼]                            │   │
│  │ 办理顺序: [同时办理___▼]                            │   │
│  │ 办理人: [+ 添加公式]                                │   │
│  │   └─ 类型: [角色▼] 值: [HR_RECRUITER]              │   │
│  │ 阅办人: [+ 添加公式]                                │   │
│  │ □ 允许代签                                          │   │
│  │ □ 允许收回                                          │   │
│  │ 办理后权限转移: [曾经办理人▼]                       │   │
│  │ 阅办后权限转移: [历史读者___▼]                      │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.3 Agent 配置面板

```
┌─────────────────────────────────────────────────────────────┐
│  Agent 配置                                                  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ Agent 类型                                           │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ ○ LLM (大语言模型)                                   │   │
│  │ ○ TASK (任务执行)                                    │   │
│  │ ○ EVENT (事件触发)                                   │   │
│  │ ○ HYBRID (混合模式)                                  │   │
│  │ ○ COORDINATOR (协调器)                               │   │
│  │ ○ TOOL (工具调用)                                    │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 调度策略                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ ○ 顺序执行 (SEQUENTIAL)                              │   │
│  │ ○ 并行执行 (PARALLEL)                                │   │
│  │ ○ 条件执行 (CONDITIONAL)                             │   │
│  │ ○ 轮询执行 (ROUND_ROBIN)                             │   │
│  │ ○ 优先级执行 (PRIORITY)                              │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 协作模式                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ ○ 独立模式 (SOLO)                                    │   │
│  │ ○ 层级模式 (HIERARCHICAL)                            │   │
│  │ ○ 对等模式 (PEER)                                    │   │
│  │ ○ 辩论模式 (DEBATE)                                  │   │
│  │ ○ 投票模式 (VOTING)                                  │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 能力配置                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ □ 邮件处理 (EMAIL)                                   │   │
│  │ □ 日程管理 (CALENDAR)                                │   │
│  │ □ 文档处理 (DOCUMENT)                                │   │
│  │ □ 数据分析 (ANALYSIS)                                │   │
│  │ □ 信息检索 (SEARCH)                                  │   │
│  │ □ 消息通知 (NOTIFICATION)                            │   │
│  │ □ 审批处理 (APPROVAL)                                │   │
│  │ □ 任务调度 (SCHEDULING)                              │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ LLM 配置                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ 模型: [gpt-4________▼]                              │   │
│  │ 温度: [0.7____]                                      │   │
│  │ 最大Token: [2000__]                                  │   │
│  │ □ 启用函数调用                                       │   │
│  │ □ 启用流式输出                                       │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.4 场景配置面板

```
┌─────────────────────────────────────────────────────────────┐
│  场景配置                                                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 场景定义                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ 场景ID: [resume-screening-scene]                    │   │
│  │ 场景名称: [简历筛选场景]                            │   │
│  │ 场景类型: [表单场景___▼]                            │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ PageAgent 配置                                       │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ Agent ID: [resume-agent]                            │   │
│  │ 页面ID: [resume-page]                               │   │
│  │ 页面类型: [form___▼]                                │   │
│  │ 模板路径: [/templates/resume-screening.html]        │   │
│  │ 样式路径: [/styles/resume-screening.css]            │   │
│  │ 脚本路径: [/scripts/resume-screening.js]            │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ Function Calling                                     │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ [+ 添加函数]                                         │   │
│  │ ┌─────────────────────────────────────────────┐     │   │
│  │ │ 函数名        类型        描述               │     │   │
│  │ │ submitResult  HYBRID     提交筛选结果        │     │   │
│  │ └─────────────────────────────────────────────┘     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 交互配置                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ A2A (Agent-Agent):                                  │   │
│  │   resume-agent → notification-agent (NOTIFICATION)  │   │
│  │ P2A (人-Agent):                                     │   │
│  │   PERFORMER → resume-agent (COMMAND)                │   │
│  │ P2P (人-人):                                        │   │
│  │   PERFORMER → SPONSOR (NOTIFICATION)                │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 存储配置                                             │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ 存储类型: ○ VFS  ○ SQL  ○ 混合存储                  │   │
│  │ VFS路径: [/skills/recruitment/scenes/...]           │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 6. 数据流设计

### 6.1 数据模型

```javascript
class ProcessDef {
    constructor() {
        this.processDefId = '';
        this.name = '';
        this.description = '';
        this.category = '';
        this.accessLevel = 'INDEPENDENT';
        this.version = 1;
        this.status = 'DRAFT';
        this.activities = [];
        this.routes = [];
        this.listeners = [];
        this.formulas = [];
        this.parameters = [];
        this.extendedAttributes = [];
        this.agentConfig = null;
        this.sceneConfig = null;
    }
}

class ActivityDef {
    constructor() {
        this.activityDefId = '';
        this.name = '';
        this.description = '';
        this.activityType = 'TASK';
        this.activityCategory = 'HUMAN';
        this.implementation = 'MANUAL';
        this.performerType = 'HUMAN';
        this.timing = new TimingConfig();
        this.routing = new RoutingConfig();
        this.right = new RightConfig();
        this.agentConfig = null;
        this.sceneConfig = null;
        this.listeners = [];
        this.extendedAttributes = [];
    }
}

class AgentDef {
    constructor() {
        this.agentId = '';
        this.agentType = 'LLM';
        this.scheduleStrategy = 'SEQUENTIAL';
        this.collaborationMode = 'SOLO';
        this.capabilities = [];
        this.llmConfig = new LLMConfig();
        this.northbound = new NorthboundConfig();
        this.southbound = new SouthboundConfig();
    }
}

class SceneDef {
    constructor() {
        this.sceneId = '';
        this.name = '';
        this.sceneType = 'FORM';
        this.pageAgent = new PageAgentConfig();
        this.functionCalling = [];
        this.interactions = [];
        this.storage = new StorageConfig();
    }
}
```

### 6.2 数据流转

```
┌─────────────────────────────────────────────────────────────┐
│                    数据流转图                                │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   用户操作                                                   │
│      │                                                      │
│      ▼                                                      │
│   ┌─────────┐                                              │
│   │ 画布组件 │                                              │
│   └────┬────┘                                              │
│        │ node-select                                        │
│        ▼                                                    │
│   ┌─────────┐      ┌─────────┐                             │
│   │ State   │─────▶│ 属性面板 │                             │
│   │ Manager │      └─────────┘                             │
│   └────┬────┘                                               │
│        │ update                                             │
│        ▼                                                    │
│   ┌─────────┐      ┌─────────┐                             │
│   │ BpmSDK  │─────▶│ 后端服务 │                             │
│   │         │      └─────────┘                             │
│   └─────────┘                                               │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 6.3 状态管理

```javascript
const DesignerState = {
    processDef: null,
    currentActivity: null,
    currentRoute: null,
    selectedNodes: [],
    clipboard: null,
    dirty: false,
    history: [],
    historyIndex: -1,
    
    setProcessDef: function(processDef) {
        this.processDef = processDef;
        this.dirty = false;
        this.notifyChange('process');
    },
    
    selectNode: function(node) {
        this.currentActivity = node;
        this.notifyChange('selection');
    },
    
    updateActivity: function(activity) {
        const index = this.processDef.activities.findIndex(
            a => a.activityDefId === activity.activityDefId
        );
        if (index >= 0) {
            this.processDef.activities[index] = activity;
            this.dirty = true;
            this.notifyChange('activity');
        }
    },
    
    notifyChange: function(type) {
        window.dispatchEvent(new CustomEvent('designer-change', {
            detail: { type: type }
        }));
    }
};
```

---

## 7. API设计

### 7.1 流程定义 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/bpm/process/{id} | 获取流程定义 |
| GET | /api/bpm/process/{id}/version/{version} | 获取流程版本 |
| POST | /api/bpm/process | 创建流程定义 |
| PUT | /api/bpm/process/{id} | 更新流程定义 |
| DELETE | /api/bpm/process/{id} | 删除流程定义 |
| POST | /api/bpm/process/{id}/activate | 激活流程版本 |
| POST | /api/bpm/process/{id}/freeze | 冻结流程版本 |
| GET | /api/bpm/process/{id}/export/yaml | 导出YAML |
| POST | /api/bpm/process/{id}/import/yaml | 导入YAML |

### 7.2 活动定义 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/bpm/process/{processId}/activities | 获取活动列表 |
| GET | /api/bpm/process/{processId}/activity/{activityId} | 获取活动详情 |
| POST | /api/bpm/process/{processId}/activity | 创建活动 |
| PUT | /api/bpm/process/{processId}/activity/{activityId} | 更新活动 |
| DELETE | /api/bpm/process/{processId}/activity/{activityId} | 删除活动 |

### 7.3 Agent 定义 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/bpm/agent/{agentId} | 获取Agent定义 |
| POST | /api/bpm/agent | 创建Agent定义 |
| PUT | /api/bpm/agent/{agentId} | 更新Agent定义 |
| DELETE | /api/bpm/agent/{agentId} | 删除Agent定义 |
| GET | /api/bpm/agent/capabilities | 获取可用能力列表 |

### 7.4 场景定义 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/bpm/scene/{sceneId} | 获取场景定义 |
| POST | /api/bpm/scene | 创建场景定义 |
| PUT | /api/bpm/scene/{sceneId} | 更新场景定义 |
| DELETE | /api/bpm/scene/{sceneId} | 删除场景定义 |
| GET | /api/bpm/scene/templates | 获取可用模板列表 |

---

## 8. 实施路线图

### 8.1 阶段划分

| 阶段 | 内容 | 周期 |
|------|------|------|
| **第一阶段** | 基础框架搭建 | 1周 |
| **第二阶段** | 流程画布开发 | 2周 |
| **第三阶段** | 属性面板开发 | 2周 |
| **第四阶段** | Agent/A2UI扩展 | 2周 |
| **第五阶段** | 测试与优化 | 1周 |

### 8.2 详细计划

#### 第一阶段：基础框架搭建（1周）

| 任务 | 说明 | 交付物 |
|------|------|--------|
| 页面结构 | HTML 页面框架 | designer.html |
| CSS 模块 | 复用 nexus CSS | bpm-designer.css |
| SDK 封装 | BpmSDK 实现 | BpmSDK.js |
| 状态管理 | DesignerState 实现 | state.js |

#### 第二阶段：流程画布开发（2周）

| 任务 | 说明 | 交付物 |
|------|------|--------|
| bpmn-js 集成 | 流程图核心库 | canvas.js |
| 自定义节点 | Agent/场景节点 | node-factory.js |
| 组件库 | Palette 组件 | palette.js |
| 工具栏 | Toolbar 组件 | toolbar.js |

#### 第三阶段：属性面板开发（2周）

| 任务 | 说明 | 交付物 |
|------|------|--------|
| 流程面板 | ProcessPanel | process-panel.js |
| 活动面板 | ActivityPanel | activity-panel.js |
| 路由面板 | RoutePanel | route-panel.js |
| 监听器面板 | ListenerPanel | listener-panel.js |
| 公式面板 | FormulaPanel | formula-panel.js |
| 扩展属性面板 | ExtendPanel | extend-panel.js |

#### 第四阶段：Agent/A2UI扩展（2周）

| 任务 | 说明 | 交付物 |
|------|------|--------|
| Agent 面板 | AgentPanel | agent-panel.js |
| A2UI 面板 | A2UIPanel | a2ui-panel.js |
| 场景面板 | ScenePanel | scene-panel.js |
| YAML 转换 | 导入导出功能 | yaml-converter.js |

#### 第五阶段：测试与优化（1周）

| 任务 | 说明 | 交付物 |
|------|------|--------|
| 功能测试 | 完整功能测试 | 测试报告 |
| 性能优化 | 加载优化 | 优化报告 |
| 文档编写 | 用户文档 | 使用手册 |

---

## 附录

### A. 参考设计

| 设计来源 | 路径 | 说明 |
|----------|------|------|
| XPDL 设计器 | E:\github\a2ui\ood-plugs\esdbpd | Swing 版本设计器 |
| nexus 控制台 | E:\github\ooder-skills\temp\ooder-Nexus | nexus 前端架构 |
| BPM 枚举定义 | E:\github\ooder-skills\skills\_drivers\bpm\bpm-web-sources\net\ooder\bpm\enums | 枚举定义 |
| 需求规格 v4 | E:\github\ooder-skills\docs\bpm-spec\v4\README.md | Agent驱动规格 |

### B. 技术文档

| 文档 | 链接 |
|------|------|
| bpmn-js 文档 | https://bpmn.io/toolkit/bpmn-js/ |
| Monaco Editor 文档 | https://microsoft.github.io/monaco-editor/ |
| Remix Icon | https://remixicon.com/ |

### C. nexus 架构参考

| 模块 | 路径 | 说明 |
|------|------|------|
| SDK 封装 | E:\github\ooder-skills\temp\ooder-Nexus\src\main\resources\static\console\js\nexus\sdk\ | SDK 设计模式 |
| CSS 模块 | E:\github\ooder-skills\temp\ooder-Nexus\src\main\resources\static\console\css\modules\ | CSS 模块化 |
| 场景定义页面 | E:\github\ooder-skills\temp\ooder-Nexus\src\main\resources\static\console\pages\scene\scene-definition.html | 页面结构参考 |

---

**文档路径**: `E:\github\ooder-skills\docs\bpm-designer\web-designer-spec.md`
