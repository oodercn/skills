# 从零开始的 SPAC 编程构建 BPM 设计器实战

> **项目背景**：将传统 Swing 桌面应用的 XPDL BPM 设计器转换为现代化的 H5 JS 实现，实现架构升级和技术栈迁移。核心目标是让 LLM 参与流程控制，让 AI 成为流程设计的核心参与者。

## 目录

1. [架构升级决策](#1-架构升级决策)
2. [XPDL 读取和翻译为标准 SPAC](#2-xpdl-读取和翻译为标准-spac)
3. [全新架构和插件理念设计](#3-全新架构和插件理念设计)
4. [LLM 驱动场景流程实现（重点）](#4-llm-驱动场景流程实现重点)
5. [流程版本与权限整合](#5-流程版本与权限整合)
6. [存储策略与数据持久化](#6-存储策略与数据持久化)
7. [总结与展望](#7-总结与展望)

---

## 1. 架构升级决策

### 1.1 为什么选择 H5 JS 架构？

**【目的】**
解决传统 Swing 桌面应用的部署困难、维护成本高、协作性差等问题，实现跨平台、零部署、支持 LLM 集成的现代化架构。

**【过程】**
在与 trae sole 的对话中，我们讨论了架构迁移方案：

```
👤 用户：我需要将 Swing 版本的 BPM 设计器迁移到 H5，应该如何设计架构？

🤖 trae sole：建议采用三层架构：
1. 前端展示层：Canvas 绘制 + Panel 属性面板
2. 业务逻辑层：Store 状态管理 + Adapter 数据适配
3. 数据持久层：REST API + XPDL 存储

关键点：
- 保留 XPDL 格式，实现前后端数据适配
- 引入插件系统，支持动态扩展
- 集成 LLM Agent，实现智能流程设计
```

**【结果】**
成功实现了 H5 JS 架构，具备以下优势：

```
┌─────────────────────────────────────────────────────────┐
│                  H5 JS 架构优势                          │
├─────────────────────────────────────────────────────────┤
│  ✅ 零部署：浏览器直接访问，无需安装                      │
│  ✅ 跨平台：PC、移动端、平板全覆盖                        │
│  ✅ 实时协作：WebSocket 支持多人协同编辑                  │
│  ✅ 插件生态：灵活的插件系统，动态加载                    │
│  ✅ LLM 集成：天然支持 AI 辅助设计                       │
└─────────────────────────────────────────────────────────┘
```

**【遇到的问题】**
- **问题1**：如何保留现有的 XPDL 格式？
  - **解决方案**：设计 DataAdapter 适配器，实现前后端数据格式转换
- **问题2**：如何实现 LLM Agent 的集成？
  - **解决方案**：设计 AgentDef 和 SceneDef 两个核心类，通过 Chat 类实现与 LLM API 的交互

### 1.2 技术栈选型

**【目的】**
选择适合 LLM 集成、支持快速迭代的技术栈。

**【过程】**
通过 trae sole 的建议，我们选择了以下技术栈：

| 层级 | 技术选型 | 说明 |
|------|---------|------|
| 前端框架 | 原生 JavaScript (ES6+) | 轻量、高性能、无框架依赖 |
| 图形引擎 | Canvas 2D | 流程图绘制，性能优异 |
| 数据管理 | Store 模式 | 单向数据流，状态管理清晰 |
| 数据适配 | Adapter 模式 | 前后端数据格式转换 |
| 插件系统 | Plugin 架构 | 动态加载，可扩展性强 |
| 后端服务 | Java Spring Boot | 兼容现有 XPDL 引擎 |
| AI 集成 | LLM Agent | 智能流程设计与优化 |

**【结果】**
技术栈选型合理，开发效率高，95% 以上的代码由 AI 生成。

**【遇到的问题】**
- **问题**：原生 JavaScript 是否能支撑复杂应用？
  - **解决方案**：通过插件架构和模块化设计，实现了良好的可维护性

### 1.3 架构迁移策略

**【目的】**
确保业务连续性，采用渐进式迁移策略。

**【过程】**
```
阶段一：数据层迁移
├── 保留 XPDL 格式存储
├── 新增 JSON Schema 定义
└── 实现双向数据转换

阶段二：业务层重构
├── Store 状态管理
├── Adapter 数据适配
└── Plugin 插件系统

阶段三：展示层升级
├── Canvas 流程图绘制
├── Panel 属性面板
└── Palette 元素面板

阶段四：AI 能力集成
├── LLM Agent 配置
├── Scene 场景定义
└── Chat 智能助手
```

**【结果】**
四个月完成迁移，AI 代码接管率达到 95% 以上。

**【遇到的问题】**
- **问题**：如何保证迁移过程中业务不中断？
  - **解决方案**：保留 XPDL 格式，通过适配器实现兼容

---

## 2. XPDL 读取和翻译为标准 SPAC

### 2.1 XPDL 格式解析

**【目的】**
理解 XPDL 格式，为数据转换奠定基础。

**【过程】**
XPDL (XML Process Definition Language) 是工作流管理联盟（WfMC）定义的流程定义标准格式。

核心结构包括：
- ProcessHeader：流程基本信息
- Activities：活动定义
- Transitions：路由定义
- ExtendedAttributes：扩展属性

**【结果】**
成功解析 XPDL 格式，理解了其数据结构。

**【遇到的问题】**
- **问题**：XPDL 格式复杂，如何简化？
  - **解决方案**：定义 SPAC 标准属性配置，简化数据结构

### 2.2 SPAC 标准属性定义

**【目的】**
定义统一的前后端数据结构，简化开发。

**【过程】**
SPAC (Standard Process Attribute Configuration) 是我们定义的标准流程属性配置格式。

核心设计原则：
1. **完整性**：覆盖 XPDL 所有属性
2. **一致性**：前后端使用相同的数据结构
3. **扩展性**：支持自定义扩展属性
4. **验证性**：基于 JSON Schema 的数据校验

**【结果】**
SPAC 数据结构清晰，易于理解和维护。

**【遇到的问题】**
- **问题**：如何处理 XPDL 的扩展属性？
  - **解决方案**：通过 PluginDataAdapter 适配器处理特殊格式

### 2.3 数据转换适配器

**【目的】**
实现前后端数据的无缝转换。

**【过程】**
在与 trae sole 的对话中：

```
👤 用户：如何实现数据变更的确认机制？

🤖 trae sole：设计 pendingChanges 机制：
1. 用户修改字段时，记录到 pendingChanges
2. 只有点击"确认"按钮，才应用到 store
3. 支持"取消"和"重置"操作
4. 深度比较原始值，避免误判
```

**【结果】**
实现了 DataAdapter 和 PluginDataAdapter，支持：
- 前端数据 -> 后端格式
- 后端数据 -> 前端格式
- 特殊格式的序列化和反序列化

**【遇到的问题】**
- **问题**：如何避免频繁重绘？
  - **解决方案**：设计 pendingChanges 机制，只有确认后才应用变更

---

## 3. 全新架构和插件理念设计

### 3.1 插件系统架构

**【目的】**
实现灵活可扩展的面板系统，支持动态加载。

**【过程】**
在与 trae sole 的对话中：

```
👤 用户：面板系统如何设计才能支持灵活扩展？

🤖 trae sole：采用微内核 + 插件架构：

核心组件：
1. PanelPlugin：插件基类，定义标准接口
2. PanelManager：插件管理器，负责注册和调度
3. PluginDataAdapter：数据适配器，处理不同数据格式

插件生命周期：
init() -> render() -> validate() -> destroy()

关键设计：
- 每个面板（流程、活动、路由）都是独立插件
- 支持动态加载和卸载
- 数据变更采用"确认机制"，避免频繁重绘
```

**【结果】**
成功实现了插件系统，支持 20+ 个插件。

**【遇到的问题】**
- **问题**：如何避免插件间的耦合？
  - **解决方案**：通过 PluginEnvironment 提供统一的运行环境

### 3.2 插件接口设计

**【目的】**
定义标准的插件接口，确保一致性。

**【过程】**
设计了 PanelPlugin 基类和 FormPanelPlugin 表单插件基类。

**【结果】**
所有插件遵循统一接口，易于维护和扩展。

**【遇到的问题】**
- **问题**：如何处理不同类型的数据？
  - **解决方案**：通过 dataConfig 配置数据源和适配器

### 3.3 插件管理器

**【目的】**
实现插件的注册、加载、调度。

**【过程】**
实现了 PanelManagerNew，支持：
- 插件注册和卸载
- 面板渲染
- 事件绑定

**【结果】**
插件管理器运行稳定，支持动态扩展。

**【遇到的问题】**
- **问题**：如何初始化插件？
  - **解决方案**：设计 PanelInitializer，统一管理插件初始化

---

## 4. LLM 驱动场景流程实现（重点）

### 4.1 LLM Agent 架构设计

**【目的】**
让 LLM 参与流程控制，实现智能流程设计。

**【过程】**
在与 trae sole 的对话中：

```
👤 用户：如何实现 LLM Agent 的集成？

🤖 trae sole：设计 AgentDef 和 SceneDef 两个核心类：
- AgentDef：定义智能体的类型、能力、协作模式
- SceneDef：定义智能体的执行环境和交互方式

通过 Chat 类实现与 LLM API 的交互，支持：
- 自然语言创建流程
- 智能配置活动属性
- 自动生成流程定义
```

**【结果】**
成功实现了 LLM Agent 架构，支持：
- Agent 类型：LLM / TASK / EVENT / HYBRID
- 调度策略：顺序/并行/条件/轮询/优先级
- 协作模式：独立/层级/对等/辩论/投票

**【遇到的问题】**
- **问题**：如何让 LLM 理解当前流程状态？
  - **解决方案**：构建上下文信息，包含流程和活动状态

### 4.2 AgentDef 核心实现

**【目的】**
定义智能体的属性和行为。

**【过程】**
实现了 AgentDef 类，包含：
- agentType：智能体类型
- scheduleStrategy：调度策略
- collaborationMode：协作模式
- capabilities：能力集合
- llmConfig：LLM 配置
- tools：工具集
- memory：记忆系统

**【结果】**
AgentDef 定义清晰，易于扩展。

**【遇到的问题】**
- **问题**：如何配置 LLM 参数？
  - **解决方案**：设计 LLMConfig 类，支持模型选择、温度、最大 Token 等配置

### 4.3 SceneDef 场景定义

**【目的】**
定义智能体的执行环境。

**【过程】**
实现了 SceneDef 类，包含：
- sceneType：场景类型（FORM/LIST/DASHBOARD/CUSTOM）
- pageAgent：PageAgent 配置
- functionCalling：函数调用配置
- interactions：交互定义
- storage：存储配置

**【结果】**
场景定义灵活，支持多种业务场景。

**【遇到的问题】**
- **问题**：如何处理场景间的交互？
  - **解决方案**：设计 InteractionDef，定义 Agent 间的交互方式

### 4.4 Chat 智能助手集成

**【目的】**
实现与 LLM 的交互，让用户通过自然语言设计流程。

**【过程】**
在与 trae sole 的对话中：

```
👤 用户：如何设计 LLM API 的交互细节？

🤖 trae sole：设计 Chat 类，实现与 LLM 的交互：

API 调用流程：
1. 用户输入消息
2. 构建上下文（当前流程、活动信息）
3. 调用 LLM API
4. 解析返回结果
5. 执行动作（创建活动、更新配置等）

动作类型：
- create_activity：创建活动
- update_activity：更新活动
- delete_activity：删除活动
- create_route：创建路由
- export_yaml：导出 YAML
```

**【结果】**
成功实现了 Chat 智能助手，支持自然语言交互。

**【遇到的问题】**
- **问题**：如何处理 LLM 返回的动作？
  - **解决方案**：设计 _handleActions 方法，根据动作类型执行相应操作

### 4.5 LLM API 交互细节

**【目的】**
详细输出 LLM API 的交互过程。

**【过程】**
**API 请求格式**：

```
POST /api/v1/chat/sessions/{sessionId}/messages

请求体：
{
  "content": "帮我创建一个请假审批流程",
  "skillId": "bpm-designer",
  "userId": "bpm-designer-user",
  "context": {
    "processId": "proc_001",
    "processName": "请假流程",
    "activityId": null,
    "activityName": null
  },
  "currentPage": "BPM Designer",
  "currentUrl": "http://localhost:8080/designer"
}

响应格式：
{
  "status": "success",
  "data": {
    "content": "已为您创建请假审批流程...",
    "actions": [
      {
        "type": "create_activity",
        "data": {
          "name": "提交申请",
          "activityType": "TASK",
          "category": "HUMAN",
          "position": { "x": 200, "y": 200 }
        }
      }
    ]
  }
}
```

**上下文构建策略**：
```javascript
const context = {
    // 流程信息
    processId: process?.processDefId,
    processName: process?.name,
    
    // 当前活动信息
    activityId: currentActivity?.activityDefId,
    activityName: currentActivity?.name,
    activityType: currentActivity?.activityType,
    
    // 用户信息
    userId: 'bpm-designer-user',
    
    // 页面信息
    currentPage: 'BPM Designer',
    currentUrl: window.location.href
};
```

**动作执行机制**：
```javascript
_handleActions(actions) {
    if (!actions || !Array.isArray(actions)) return;
    
    actions.forEach(action => {
        switch (action.type) {
            case 'create_activity':
                this._createActivity(action.data);
                break;
            case 'update_activity':
                this._updateActivity(action.data);
                break;
            case 'delete_activity':
                this._deleteActivity(action.data);
                break;
            case 'create_route':
                this._createRoute(action.data);
                break;
            case 'export_yaml':
                this._exportYaml();
                break;
        }
    });
}
```

**【结果】**
LLM API 交互流畅，支持自然语言创建和修改流程。

**【遇到的问题】**
- **问题**：如何让 LLM 准确理解用户意图？
  - **解决方案**：构建丰富的上下文信息，包含流程和活动状态

### 4.6 LLM 驱动流程设计工作流

**【目的】**
展示完整的 LLM 驱动流程设计过程。

**【过程】**
**实战对话示例 1：创建流程**

```
👤 用户：帮我设计一个请假审批流程

🤖 LLM：好的，我为您设计了一个标准的请假审批流程：

【流程结构】
1. 开始节点
2. 提交申请（表单场景）
3. 部门经理审批（Agent审批）
4. HR备案（自动任务）
5. 结束节点

【Agent配置】
- 审批Agent: LLM类型，支持EMAIL和NOTIFICATION能力
- 协作模式: 层级模式
- 调度策略: 条件执行（根据请假天数）

【场景定义】
- 表单场景: 请假申请表单
- 存储策略: VFS + SQL混合存储

是否需要调整？

[执行动作]
✓ create_activity: 提交申请
✓ create_activity: 部门经理审批
✓ create_activity: HR备案
✓ create_route: 开始 -> 提交申请
✓ create_route: 提交申请 -> 部门经理审批
✓ create_route: 部门经理审批 -> HR备案
✓ create_route: HR备案 -> 结束
```

**实战对话示例 2：优化流程**

```
👤 用户：增加一个总经理审批环节，如果请假天数超过3天

🤖 LLM：已为您更新流程：

【流程结构】
1. 开始节点
2. 提交申请（表单场景）
3. 部门经理审批（Agent审批）
4. 条件判断（请假天数 > 3天？）
   - 是: 总经理审批
   - 否: 直接到HR备案
5. HR备案（自动任务）
6. 结束节点

【路由条件】
- 部门经理审批 -> 总经理审批: ${leaveDays > 3}
- 部门经理审批 -> HR备案: ${leaveDays <= 3}

流程已更新，请查看画布。

[执行动作]
✓ create_activity: 总经理审批
✓ create_route: 部门经理审批 -> 总经理审批
✓ create_route: 部门经理审批 -> HR备案
✓ update_route: 条件配置
```

**【结果】**
LLM 能够准确理解用户需求，自动生成和优化流程。

**【遇到的问题】**
- **问题**：如何处理复杂的流程逻辑？
  - **解决方案**：通过多轮对话，逐步细化和优化流程

---

## 5. 流程版本与权限整合

### 5.1 流程版本管理

**【目的】**
实现流程版本的生命周期管理。

**【过程】**
实现了版本状态流转：DRAFT -> ACTIVE -> FROZEN -> ARCHIVED

**【结果】**
版本管理完整，支持激活、冻结、解冻、归档等操作。

**【遇到的问题】**
- **问题**：如何保证版本切换时数据一致性？
  - **解决方案**：通过数据库事务和事件分发机制保证一致性

### 5.2 权限整合机制

**【目的】**
实现细粒度的权限控制。

**【过程】**
实现了权限检查流程：用户请求 -> 获取用户信息 -> 查询权限组 -> 检查权限 -> 执行操作

**【结果】**
权限控制完善，支持 VIEW/EDIT/DELETE/EXEC 等多种权限。

**【遇到的问题】**
- **问题**：如何处理复杂的权限组合？
  - **解决方案**：通过权限组机制，支持灵活的权限组合

---

## 6. 存储策略与数据持久化

### 6.1 存储架构

**【目的】**
实现混合存储策略，支持多种数据格式。

**【过程】**
设计了三层存储架构：
- 前端数据层：Store + LocalStorage + IndexedDB
- 传输层：REST API + WebSocket + DataAdapter
- 后端存储层：关系数据库 + VFS + XPDL

**【结果】**
存储架构灵活，支持多种存储方式。

**【遇到的问题】**
- **问题**：如何实现自动保存？
  - **解决方案**：设计 autoSave 机制，延迟 1 秒后自动保存

### 6.2 数据持久化流程

**【目的】**
实现数据的可靠持久化。

**【过程】**
实现了 Store 类，支持：
- 数据更新
- 自动保存
- 历史记录

**【结果】**
数据持久化稳定可靠。

**【遇到的问题】**
- **问题**：如何避免频繁保存？
  - **解决方案**：通过 dirty 标志和延迟保存机制，减少保存次数

---

## 7. 总结与展望

### 7.1 项目成果

**【目的】**
总结项目成果，提炼经验。

**【过程】**
通过本次架构升级，我们成功实现了：

1. **架构现代化**：从 Swing 桌面应用成功迁移到 H5 Web 应用
2. **插件化设计**：实现了灵活可扩展的插件系统
3. **数据标准化**：定义了 SPAC 标准属性配置
4. **AI 能力集成**：实现了 LLM 驱动的智能流程设计
5. **用户体验提升**：零部署、跨平台、实时协作

**【结果】**
四个月完成迁移，AI 代码接管率达到 95% 以上。

**【遇到的问题】**
- **问题**：如何保证 AI 生成的代码质量？
  - **解决方案**：通过 SPAC 模式和代码审查，确保代码质量

### 7.2 技术亮点

1. **数据适配器模式**：实现了前后端数据的无缝转换
2. **插件架构**：支持动态加载和扩展
3. **LLM Agent**：实现了智能化的流程设计辅助
4. **场景驱动**：通过 Scene 定义实现灵活的业务场景
5. **版本管理**：完整的流程版本生命周期管理

### 7.3 未来展望

1. **多模型支持**：集成更多 LLM 模型（Claude、Gemini等）
2. **协作增强**：实现多人实时协同编辑
3. **模板市场**：建立流程模板共享平台
4. **智能优化**：基于历史数据的流程优化建议
5. **低代码集成**：与低代码平台深度集成

---

**项目地址**：e:\github\ooder-skills\skills\_drivers\bpm  
**开源协议**：MIT  
**Maven 中央仓库**：搜索 `net.ooder`

---

*"The best way to predict the future is to create it."*  
*"预测未来的最好方式，就是创造未来。"*
