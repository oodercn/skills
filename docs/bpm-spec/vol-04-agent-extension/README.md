# 第四册：Agent扩展规格

**文档版本**: v3.0  
**创建日期**: 2026-04-06  
**源码路径**: `E:\github\ooder-skills\skills\_system\skill-agent`

---

## 目录

1. [活动分类扩展](#1-活动分类扩展)
2. [Agent活动类型](#2-agent活动类型)
3. [Agent调度策略](#3-agent调度策略)
4. [Agent协作模式](#4-agent协作模式)
5. [办理人类型扩展](#5-办理人类型扩展)
6. [对话类型扩展](#6-对话类型扩展)
7. [Agent能力定义](#7-agent能力定义)
8. [Agent配置模型](#8-agent配置模型)

---

## 1. 活动分类扩展

### 1.1 枚举定义

```java
public enum ActivityCategory {
    HUMAN("HUMAN", "人工活动", "人工办理的活动节点"),
    AGENT("AGENT", "Agent活动", "Agent执行的活动节点"),
    SCENE("SCENE", "场景活动", "场景组合活动节点")
}
```

### 1.2 分类说明

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| HUMAN | HUMAN | 人工活动 | 人工办理的活动节点 |
| AGENT | AGENT | Agent活动 | Agent执行的活动节点 |
| SCENE | SCENE | 场景活动 | 场景组合活动节点 |

### 1.3 分类与实现方式映射

| 活动分类 | 实现方式 | 说明 |
|----------|----------|------|
| HUMAN | IMPL_NO | 手动活动，人工办理 |
| AGENT | IMPL_TOOL, IMPL_SERVICE, IMPL_EVENT | 自动/服务/事件活动 |
| SCENE | IMPL_SUBFLOW, ACTIVITY_GROUP | 子流程/活动组 |

---

## 2. Agent活动类型

### 2.1 枚举定义

```java
public enum ActivityDefAgentType {
    LLM("LLM", "大语言模型", "LLM推理执行"),
    TASK("TASK", "任务执行", "任务自动化执行"),
    EVENT("EVENT", "事件触发", "事件驱动执行"),
    HYBRID("HYBRID", "混合模式", "多模式组合执行"),
    COORDINATOR("COORDINATOR", "协调器", "多Agent协调"),
    TOOL("TOOL", "工具调用", "工具/插件调用")
}
```

### 2.2 类型说明

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| LLM | LLM | 大语言模型 | LLM推理执行 |
| TASK | TASK | 任务执行 | 任务自动化执行 |
| EVENT | EVENT | 事件触发 | 事件驱动执行 |
| HYBRID | HYBRID | 混合模式 | 多模式组合执行 |
| COORDINATOR | COORDINATOR | 协调器 | 多Agent协调 |
| TOOL | TOOL | 工具调用 | 工具/插件调用 |

### 2.3 类型能力映射

| Agent类型 | 能力需求 | 典型场景 |
|-----------|----------|----------|
| LLM | 推理、生成、理解 | 文档分析、内容生成 |
| TASK | 执行、监控 | 数据处理、批量操作 |
| EVENT | 监听、触发 | 消息通知、状态变更 |
| HYBRID | 综合 | 复杂业务流程 |
| COORDINATOR | 协调、决策 | 多Agent协作 |
| TOOL | 调用、集成 | API调用、系统集成 |

---

## 3. Agent调度策略

### 3.1 枚举定义

```java
public enum AgentScheduleStrategy {
    SEQUENTIAL("SEQUENTIAL", "顺序执行", "按顺序依次执行"),
    PARALLEL("PARALLEL", "并行执行", "多个Agent并行执行"),
    CONDITIONAL("CONDITIONAL", "条件执行", "根据条件选择执行"),
    ROUND_ROBIN("ROUND_ROBIN", "轮询执行", "轮询分配任务"),
    PRIORITY("PRIORITY", "优先级执行", "按优先级执行")
}
```

### 3.2 策略说明

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| SEQUENTIAL | SEQUENTIAL | 顺序执行 | 按顺序依次执行 |
| PARALLEL | PARALLEL | 并行执行 | 多个Agent并行执行 |
| CONDITIONAL | CONDITIONAL | 条件执行 | 根据条件选择执行 |
| ROUND_ROBIN | ROUND_ROBIN | 轮询执行 | 轮询分配任务 |
| PRIORITY | PRIORITY | 优先级执行 | 按优先级执行 |

### 3.3 调度策略示意图

```
┌─────────────────────────────────────────────────────────────┐
│                    调度策略示意图                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   SEQUENTIAL (顺序执行):                                    │
│   ┌─────┐    ┌─────┐    ┌─────┐                           │
│   │ A-1 │───▶│ A-2 │───▶│ A-3 │                           │
│   └─────┘    └─────┘    └─────┘                           │
│                                                             │
│   PARALLEL (并行执行):                                      │
│         ┌─────┐                                            │
│      ┌─▶│ A-1 │─┐                                          │
│      │  └─────┘  │                                         │
│   ┌──┴──┐     ┌──┴──┐                                      │
│   │Start│     │ End │                                      │
│   └──┬──┘     └──┬──┘                                      │
│      │  ┌─────┐  │                                         │
│      └─▶│ A-2 │─┘                                          │
│         └─────┘                                            │
│                                                             │
│   CONDITIONAL (条件执行):                                   │
│         ┌─────┐                                            │
│      ┌─▶│ A-1 │ (条件1)                                    │
│      │  └─────┘                                            │
│   ┌──┴──┐                                                  │
│   │Start│                                                  │
│   └──┬──┘                                                  │
│      │  ┌─────┐                                            │
│      └─▶│ A-2 │ (条件2)                                    │
│         └─────┘                                            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 4. Agent协作模式

### 4.1 枚举定义

```java
public enum AgentCollaborationMode {
    SOLO("SOLO", "独立模式", "单个Agent独立工作"),
    HIERARCHICAL("HIERARCHICAL", "层级模式", "主从层级协作"),
    PEER("PEER", "对等模式", "对等节点协作"),
    DEBATE("DEBATE", "辩论模式", "多Agent辩论决策"),
    VOTING("VOTING", "投票模式", "多Agent投票决策")
}
```

### 4.2 模式说明

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| SOLO | SOLO | 独立模式 | 单个Agent独立工作 |
| HIERARCHICAL | HIERARCHICAL | 层级模式 | 主从层级协作 |
| PEER | PEER | 对等模式 | 对等节点协作 |
| DEBATE | DEBATE | 辩论模式 | 多Agent辩论决策 |
| VOTING | VOTING | 投票模式 | 多Agent投票决策 |

### 4.3 协作模式示意图

```
┌─────────────────────────────────────────────────────────────┐
│                    协作模式示意图                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   SOLO (独立模式):                                          │
│   ┌─────────────┐                                          │
│   │   Agent     │                                          │
│   │   (独立)    │                                          │
│   └─────────────┘                                          │
│                                                             │
│   HIERARCHICAL (层级模式):                                  │
│        ┌─────────┐                                         │
│        │ 主Agent │                                         │
│        └────┬────┘                                         │
│        ┌────┴────┐                                         │
│        ▼         ▼                                         │
│   ┌────────┐ ┌────────┐                                    │
│   │从Agent│ │从Agent│                                    │
│   └────────┘ └────────┘                                    │
│                                                             │
│   PEER (对等模式):                                          │
│   ┌────────┐     ┌────────┐                                │
│   │Agent A │◀───▶│Agent B │                                │
│   └────────┘     └────────┘                                │
│        ▲              ▲                                    │
│        └──────┬───────┘                                    │
│               │                                            │
│          ┌────┴────┐                                       │
│          │Agent C  │                                       │
│          └─────────┘                                       │
│                                                             │
│   DEBATE (辩论模式):                                        │
│   ┌────────┐     ┌────────┐                                │
│   │Agent A │◀──▶ │Agent B │                                │
│   └────────┘     └────────┘                                │
│        │              │                                    │
│        ▼              ▼                                    │
│   ┌─────────────────────┐                                  │
│   │      决策结果       │                                  │
│   └─────────────────────┘                                  │
│                                                             │
│   VOTING (投票模式):                                        │
│   ┌────────┐ ┌────────┐ ┌────────┐                         │
│   │Agent A │ │Agent B │ │Agent C │                         │
│   └───┬────┘ └───┬────┘ └───┬────┘                         │
│       │          │          │                              │
│       ▼          ▼          ▼                              │
│   ┌─────────────────────────┐                              │
│   │      投票统计/决策      │                              │
│   └─────────────────────────┘                              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 5. 办理人类型扩展

### 5.1 枚举定义

```java
public enum PerformerType {
    HUMAN("HUMAN", "人工", "人工办理"),
    AGENT("AGENT", "Agent", "Agent办理"),
    DEVICE("DEVICE", "设备", "设备执行")
}
```

### 5.2 类型说明

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| HUMAN | HUMAN | 人工 | 人工办理 |
| AGENT | AGENT | Agent | Agent办理 |
| DEVICE | DEVICE | 设备 | 设备执行 |

### 5.3 与 ActivityDefPerformtype 关系

| PerformerType | ActivityDefPerformtype | 说明 |
|---------------|------------------------|------|
| HUMAN | SINGLE, MULTIPLE, JOINTSIGN | 人工办理类型 |
| AGENT | SINGLE, PARALLEL | Agent办理类型 |
| DEVICE | SINGLE, PARALLEL | 设备执行类型 |

---

## 6. 对话类型扩展

### 6.1 枚举定义

**源码位置**: `net.ooder.skill.agent.dto.ConversationType`

```java
@Dict(code = "conversation_type", name = "对话类型")
public enum ConversationType implements DictItem {
    A2A("A2A", "Agent-to-Agent", "Agent之间的对话"),
    P2A("P2A", "Participant-to-Agent", "参与者与Agent的对话"),
    P2P("P2P", "Participant-to-Participant", "参与者之间的对话"),
    D2A("D2A", "Device-to-Agent", "设备与Agent的对话"),
    D2P("D2P", "Device-to-Participant", "设备与参与者的对话"),
    A2D("A2D", "Agent-to-Device", "Agent与设备的对话"),
    MULTI("MULTI", "多方对话", "多方参与的对话")
}
```

### 6.2 类型说明

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| A2A | A2A | Agent-to-Agent | Agent之间的对话 |
| P2A | P2A | Participant-to-Agent | 参与者与Agent的对话 |
| P2P | P2P | Participant-to-Participant | 参与者之间的对话 |
| D2A | D2A | Device-to-Agent | 设备与Agent的对话 |
| D2P | D2P | Device-to-Participant | 设备与参与者的对话 |
| A2D | A2D | Agent-to-Device | Agent与设备的对话 |
| MULTI | MULTI | 多方对话 | 多方参与的对话 |

### 6.3 对话类型矩阵

```
┌─────────────────────────────────────────────────────────────┐
│                    对话类型矩阵                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│              │  Agent  │ Participant │  Device  │           │
│   ───────────┼─────────┼─────────────┼──────────┤           │
│      Agent   │   A2A   │     A2P     │    A2D   │           │
│   ───────────┼─────────┼─────────────┼──────────┤           │
│   Participant│   P2A   │     P2P     │    P2D   │           │
│   ───────────┼─────────┼─────────────┼──────────┤           │
│      Device  │   D2A   │     D2P     │    D2D   │           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 7. Agent能力定义

### 7.1 枚举定义

```java
public enum AgentCapability {
    EMAIL("EMAIL", "邮件处理", "邮件收发处理能力"),
    CALENDAR("CALENDAR", "日程管理", "日程安排管理能力"),
    DOCUMENT("DOCUMENT", "文档处理", "文档生成处理能力"),
    ANALYSIS("ANALYSIS", "数据分析", "数据分析处理能力"),
    SEARCH("SEARCH", "信息检索", "信息搜索检索能力"),
    NOTIFICATION("NOTIFICATION", "消息通知", "消息推送通知能力"),
    APPROVAL("APPROVAL", "审批处理", "审批流程处理能力"),
    SCHEDULING("SCHEDULING", "任务调度", "任务调度管理能力")
}
```

### 7.2 能力说明

| 枚举值 | 代码 | 中文名 | 说明 |
|--------|------|--------|------|
| EMAIL | EMAIL | 邮件处理 | 邮件收发处理能力 |
| CALENDAR | CALENDAR | 日程管理 | 日程安排管理能力 |
| DOCUMENT | DOCUMENT | 文档处理 | 文档生成处理能力 |
| ANALYSIS | ANALYSIS | 数据分析 | 数据分析处理能力 |
| SEARCH | SEARCH | 信息检索 | 信息搜索检索能力 |
| NOTIFICATION | NOTIFICATION | 消息通知 | 消息推送通知能力 |
| APPROVAL | APPROVAL | 审批处理 | 审批流程处理能力 |
| SCHEDULING | SCHEDULING | 任务调度 | 任务调度管理能力 |

---

## 8. Agent配置模型

### 8.1 接口定义

**源码位置**: `net.ooder.skill.agent.model.AgentRoleConfig`

```java
public class AgentRoleConfig {
    private String agentId;
    private String agentType;
    private List<String> capabilities;
    private Map<String, Object> llmConfig;
    private Map<String, Object> toolConfig;
    private Map<String, Object> knowledgeConfig;
    private boolean functionCallingEnabled;
    private boolean streamingEnabled;
    private int maxRetries;
    private long timeout;
}
```

### 8.2 配置属性

| 属性名 | 类型 | 中文名 | 说明 |
|--------|------|--------|------|
| agentId | String | AgentID | Agent唯一标识 |
| agentType | String | Agent类型 | Agent类型 |
| capabilities | List\<String\> | 能力列表 | Agent具备的能力 |
| llmConfig | Map | LLM配置 | 大语言模型配置 |
| toolConfig | Map | 工具配置 | 工具调用配置 |
| knowledgeConfig | Map | 知识库配置 | 知识库配置 |
| functionCallingEnabled | boolean | 启用函数调用 | 是否启用函数调用 |
| streamingEnabled | boolean | 启用流式输出 | 是否启用流式输出 |
| maxRetries | int | 最大重试次数 | 最大重试次数 |
| timeout | long | 超时时间 | 超时时间（毫秒） |

---

## 附录

### A. 面板清单

| 面板名称 | 所属对象 | 说明 |
|----------|----------|------|
| Agent类型面板 | ActivityDef | Agent活动类型配置 |
| 调度策略面板 | ActivityDef | Agent调度策略配置 |
| 协作模式面板 | ActivityDef | Agent协作模式配置 |
| 能力配置面板 | AgentRoleConfig | Agent能力配置 |
| LLM配置面板 | AgentRoleConfig | 大语言模型配置 |

### B. 枚举清单

| 枚举名称 | 中文名 | 枚举值数量 |
|----------|--------|------------|
| ActivityCategory | 活动分类 | 3 |
| ActivityDefAgentType | Agent活动类型 | 6 |
| AgentScheduleStrategy | Agent调度策略 | 5 |
| AgentCollaborationMode | Agent协作模式 | 5 |
| PerformerType | 办理人类型 | 3 |
| ConversationType | 对话类型 | 7 |
| AgentCapability | Agent能力 | 8 |

---

**文档路径**: `E:\github\ooder-skills\docs\bpm-spec\vol-04-agent-extension\README.md`
