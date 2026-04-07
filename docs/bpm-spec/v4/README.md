# BPM 流程定义需求规格说明书（Agent 驱动版）

**文档版本**: v4.0  
**创建日期**: 2026-04-06  
**设计理念**: Agent 驱动 + 场景驱动 + A2UI 标准化  
**项目路径**: E:\github\ooder-skills

---

## 设计理念转变

### 从 XPDL 复刻到 Agent 驱动

```
┌─────────────────────────────────────────────────────────────┐
│                    设计理念转变                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   【旧设计 - XPDL 复刻】                                     │
│                                                             │
│   ProcessDef ──▶ ActivityDef ──▶ RouteDef                  │
│        │              │              │                      │
│        ▼              ▼              ▼                      │
│   FormClassBean   权限定义      条件表达式                   │
│   (表单绑定)       (人工为主)    (简单路由)                   │
│                                                             │
│   问题：                                                     │
│   - 以人工办理为主，Agent/设备为辅                           │
│   - 设备命令、端点定义分散                                   │
│   - 缺乏统一的协议规范                                       │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   【新设计 - Agent 驱动】                                    │
│                                                             │
│   ProcessDef ──▶ SceneDef ──▶ ActivityBlock                │
│        │              │              │                      │
│        ▼              ▼              ▼                      │
│   AgentDef        A2UI配置      Agent 能力                  │
│   (主体定义)       (标准化)       (能力扩展)                  │
│                                                             │
│   优势：                                                     │
│   - Agent 作为活动主体支撑                                   │
│   - 统一的南/北向协议规范                                    │
│   - A2UI/A2A/P2A/P2P 四层交互模型                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 第一册：Agent 驱动的流程定义规格

### 1. 核心概念重构

#### 1.1 活动主体类型

```java
public enum PerformerType {
    HUMAN("HUMAN", "人工", "人工办理，通过 P2A 与 Agent 协作"),
    AGENT("AGENT", "Agent", "Agent 办理，支持 LLM/Task/Event/Coordinator"),
    DEVICE("DEVICE", "设备", "设备执行，通过 ooderAgent 南向协议控制")
}
```

#### 1.2 Agent 定义模型

```java
public class AgentDef {
    // 基础属性
    private String agentId;                    // Agent 唯一标识
    private String agentName;                  // Agent 名称
    private AgentType agentType;               // Agent 类型
    private String description;                // 描述
    
    // 能力定义
    private List<AgentCapability> capabilities; // 能力列表
    private CapabilityConfig capabilityConfig;  // 能力配置
    
    // 协议配置
    private NorthboundProtocolConfig northbound; // 北向协议配置
    private SouthboundProtocolConfig southbound; // 南向协议配置
    
    // LLM 配置
    private LLMConfig llmConfig;               // LLM 配置
    
    // 协作配置
    private CollaborationConfig collaboration; // 协作配置
    
    // A2UI 配置
    private A2UIConfig a2uiConfig;             // A2UI 配置
}
```

#### 1.3 Agent 类型

```java
public enum AgentType {
    LLM("LLM", "大语言模型", "LLM 推理执行，支持 Function Calling"),
    TASK("TASK", "任务执行", "任务自动化执行"),
    EVENT("EVENT", "事件触发", "事件驱动执行"),
    HYBRID("HYBRID", "混合模式", "多模式组合执行"),
    COORDINATOR("COORDINATOR", "协调器", "多 Agent 协调"),
    TOOL("TOOL", "工具调用", "工具/插件调用"),
    DEVICE("DEVICE", "设备代理", "设备代理 Agent，南向协议控制")
}
```

### 2. ooderAgent 南/北向协议规范

#### 2.1 协议架构

```
┌─────────────────────────────────────────────────────────────┐
│                    ooderAgent 协议架构                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                    北向协议 (Northbound)              │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │  A2UI   │ │  A2A    │ │  P2A    │ │  P2P    │   │  │
│   │  │ UI标准化│ │Agent交互│ │人-Agent │ │ 人-人   │   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   └─────────────────────────────────────────────────────┘  │
│                           │                                │
│                           ▼                                │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                    Agent 核心                        │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │ LLM引擎 │ │ 任务引擎 │ │ 事件引擎 │ │ 协调引擎 │   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐              │  │
│   │  │ 能力库  │ │ 知识库  │ │ 工具库  │              │  │
│   │  └─────────┘ └─────────┘ └─────────┘              │  │
│   └─────────────────────────────────────────────────────┘  │
│                           │                                │
│                           ▼                                │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                    南向协议 (Southbound)              │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │  MCP    │ │  Route  │ │  End    │ │ Custom  │   │  │
│   │  │ 主控协议│ │ 路由协议│ │ 终端协议│ │ 自定义  │   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   └─────────────────────────────────────────────────────┘  │
│                           │                                │
│                           ▼                                │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                    设备层                            │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │ IoT设备 │ │ 网络设备 │ │ 传感器  │ │ 执行器  │   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   └─────────────────────────────────────────────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### 2.2 北向协议定义

```java
public class NorthboundProtocolConfig {
    // A2UI 配置
    private A2UIConfig a2uiConfig;
    
    // A2A 配置
    private A2AConfig a2aConfig;
    
    // P2A 配置
    private P2AConfig p2aConfig;
    
    // P2P 配置
    private P2PConfig p2pConfig;
    
    // 协议端点
    private List<ProtocolEndpoint> endpoints;
}
```

#### 2.3 南向协议定义

```java
public class SouthboundProtocolConfig {
    // 协议类型
    private ProtocolType protocolType;  // MCP, ROUTE, END, CUSTOM
    
    // 设备端点
    private List<DeviceEndpoint> deviceEndpoints;
    
    // 命令定义
    private List<CommandDef> commands;
    
    // 安全配置
    private SecurityConfig securityConfig;
}
```

#### 2.4 设备端点定义（替代原 DeviceEndPoint）

```java
public class DeviceEndpoint {
    // 基础属性
    private String endpointId;              // 端点ID
    private String endpointName;            // 端点名称
    private EndpointType endpointType;      // 端点类型
    
    // 协议配置
    private String protocolType;            // 协议类型 (MCP/ROUTE/END)
    private String address;                 // 地址
    private int port;                       // 端口
    
    // 能力声明
    private List<DeviceCapability> capabilities; // 设备能力
    
    // 安全配置
    private AuthConfig authConfig;          // 认证配置
    
    // 状态
    private EndpointStatus status;          // 状态
}
```

### 3. 流程定义（Agent 驱动）

#### 3.1 流程定义模型

```java
public class ProcessDef {
    // 基础属性
    private String processDefId;            // 流程UUID
    private String name;                    // 流程名称
    private String description;             // 流程描述
    private String classification;          // 流程分类
    private String systemCode;              // 所属应用系统
    private ProcessDefAccess accessLevel;   // 访问级别
    
    // Agent 配置（新增：流程级 Agent）
    private AgentDef processAgent;          // 流程主 Agent
    
    // 版本管理
    private List<ProcessDefVersion> versions;
}
```

#### 3.2 流程版本模型

```java
public class ProcessDefVersion {
    // 基础属性
    private String processDefVersionId;     // 版本UUID
    private int version;                    // 版本号
    private ProcessDefVersionStatus status; // 版本状态
    
    // Agent 配置（新增）
    private AgentConfig agentConfig;        // Agent 配置
    
    // 场景定义（新增：替代原 FormClassBean）
    private List<SceneDef> scenes;          // 场景列表
    
    // 活动定义
    private List<ActivityDef> activities;   // 活动列表
    
    // 路由定义
    private List<RouteDef> routes;          // 路由列表
    
    // 监听器
    private List<ListenerDef> listeners;    // 监听器列表
    
    // 扩展属性
    private Map<String, Object> extendedAttributes;
}
```

### 4. 场景定义（替代原 FormClassBean）

#### 4.1 场景定义模型

```java
public class SceneDef {
    // 基础属性
    private String sceneId;                 // 场景ID
    private String name;                    // 场景名称
    private String description;             // 场景描述
    private SceneType sceneType;            // 场景类型
    
    // 活动块定义
    private ActivityBlock activityBlock;    // 活动块
    
    // Agent 配置（新增）
    private AgentDef sceneAgent;            // 场景 Agent
    
    // A2UI 配置
    private A2UIConfig a2uiConfig;          // A2UI 配置
    
    // 交互配置
    private InteractionConfig interactionConfig; // 交互配置
    
    // 存储配置
    private StorageConfig storageConfig;    // 存储配置
}
```

#### 4.2 场景类型

```java
public enum SceneType {
    FORM("FORM", "表单场景", "数据录入和编辑场景"),
    LIST("LIST", "列表场景", "数据展示和查询场景"),
    DASHBOARD("DASHBOARD", "仪表盘场景", "数据可视化场景"),
    WORKFLOW("WORKFLOW", "流程场景", "业务流程处理场景"),
    COLLABORATION("COLLABORATION", "协作场景", "多人协作场景"),
    AGENT("AGENT", "Agent场景", "Agent 自动化场景"),
    DEVICE("DEVICE", "设备场景", "设备控制场景")
}
```

### 5. 活动定义（Agent 能力扩展）

#### 5.1 活动定义模型

```java
public class ActivityDef {
    // 基础属性
    private String activityDefId;           // 活动UUID
    private String name;                    // 活动名称
    private String description;             // 活动描述
    
    // 活动分类（新增）
    private ActivityCategory category;      // 活动分类
    
    // 活动主体（新增）
    private PerformerDef performer;         // 活动主体定义
    
    // 实现方式
    private ActivityDefImpl implementation; // 实现方式
    
    // Agent 配置（新增：活动级 Agent）
    private AgentDef activityAgent;         // 活动 Agent
    
    // 能力配置（新增）
    private List<CapabilityRef> capabilities; // 能力引用
    
    // 时限属性
    private TimingConfig timing;            // 时限配置
    
    // 路由属性
    private RoutingConfig routing;          // 路由配置
    
    // 权限属性
    private RightConfig right;              // 权限配置
    
    // 场景绑定
    private SceneBinding sceneBinding;      // 场景绑定
}
```

#### 5.2 活动主体定义

```java
public class PerformerDef {
    // 主体类型
    private PerformerType performerType;    // HUMAN/AGENT/DEVICE
    
    // 人工配置
    private HumanPerformerConfig humanConfig; // 人工配置
    
    // Agent 配置
    private AgentPerformerConfig agentConfig; // Agent 配置
    
    // 设备配置
    private DevicePerformerConfig deviceConfig; // 设备配置
}
```

#### 5.3 Agent 执行者配置

```java
public class AgentPerformerConfig {
    // Agent 引用
    private String agentId;                 // Agent ID
    
    // Agent 类型
    private AgentType agentType;            // Agent 类型
    
    // 调度策略
    private ScheduleStrategy scheduleStrategy; // 调度策略
    
    // 协作模式
    private CollaborationMode collaborationMode; // 协作模式
    
    // 能力配置
    private List<CapabilityRef> capabilities; // 能力引用
    
    // LLM 配置
    private LLMConfig llmConfig;            // LLM 配置
    
    // Function Calling 配置
    private List<FunctionDef> functions;    // 函数定义
}
```

#### 5.4 设备执行者配置（使用南向协议）

```java
public class DevicePerformerConfig {
    // 设备端点（使用南向协议）
    private DeviceEndpoint endpoint;        // 设备端点
    
    // 命令配置
    private List<CommandBinding> commands;  // 命令绑定
    
    // 协议配置
    private SouthboundProtocolConfig protocol; // 南向协议配置
    
    // 执行策略
    private ExecutionStrategy strategy;     // 执行策略
}
```

### 6. 能力定义

#### 6.1 能力模型

```java
public class CapabilityDef {
    // 基础属性
    private String capabilityId;            // 能力ID
    private String name;                    // 能力名称
    private String description;             // 能力描述
    private CapabilityType capabilityType;  // 能力类型
    
    // 输入输出
    private Schema inputSchema;             // 输入 Schema
    private Schema outputSchema;            // 输出 Schema
    
    // 执行配置
    private ExecutionConfig execution;      // 执行配置
    
    // 协议绑定
    private ProtocolBinding protocolBinding; // 协议绑定
}
```

#### 6.2 能力类型

```java
public enum CapabilityType {
    // 通用能力
    EMAIL("EMAIL", "邮件处理", "邮件收发处理能力"),
    CALENDAR("CALENDAR", "日程管理", "日程安排管理能力"),
    DOCUMENT("DOCUMENT", "文档处理", "文档生成处理能力"),
    ANALYSIS("ANALYSIS", "数据分析", "数据分析处理能力"),
    SEARCH("SEARCH", "信息检索", "信息搜索检索能力"),
    NOTIFICATION("NOTIFICATION", "消息通知", "消息推送通知能力"),
    
    // 流程能力
    APPROVAL("APPROVAL", "审批处理", "审批流程处理能力"),
    SCHEDULING("SCHEDULING", "任务调度", "任务调度管理能力"),
    
    // 设备能力（南向协议）
    DEVICE_CONTROL("DEVICE_CONTROL", "设备控制", "设备控制能力"),
    DEVICE_MONITOR("DEVICE_MONITOR", "设备监控", "设备监控能力"),
    DEVICE_COMMAND("DEVICE_COMMAND", "设备命令", "设备命令执行能力")
}
```

### 7. 交互模型

#### 7.1 交互配置

```java
public class InteractionConfig {
    // A2A 配置
    private List<A2AInteraction> a2a;       // Agent-Agent 交互
    
    // P2A 配置
    private List<P2AInteraction> p2a;       // 人-Agent 交互
    
    // P2P 配置
    private List<P2PInteraction> p2p;       // 人-人交互
    
    // D2A 配置（新增：设备-Agent 交互）
    private List<D2AInteraction> d2a;       // 设备-Agent 交互
    
    // A2D 配置（新增：Agent-设备 交互）
    private List<A2DInteraction> a2d;       // Agent-设备 交互
}
```

#### 7.2 对话类型

```java
public enum ConversationType {
    // Agent 相关
    A2A("A2A", "Agent-to-Agent", "Agent 之间的对话"),
    A2D("A2D", "Agent-to-Device", "Agent 与设备的对话"),
    
    // 人相关
    P2A("P2A", "Participant-to-Agent", "参与者与 Agent 的对话"),
    P2P("P2P", "Participant-to-Participant", "参与者之间的对话"),
    
    // 设备相关
    D2A("D2A", "Device-to-Agent", "设备与 Agent 的对话"),
    D2P("D2P", "Device-to-Participant", "设备与参与者的对话"),
    
    // 多方
    MULTI("MULTI", "多方对话", "多方参与的对话")
}
```

---

## 第二册：YAML 格式标准（Agent 驱动版）

### 1. 流程定义 YAML

```yaml
apiVersion: bpm.ooder.net/v2
kind: ProcessDef
metadata:
  id: recruitment-process
  name: 招聘流程
  description: 企业招聘管理流程
  classification: HR
  systemCode: HRM
spec:
  accessLevel: Public
  
  # 流程级 Agent 配置（新增）
  agent:
    agentId: recruitment-process-agent
    agentType: COORDINATOR
    capabilities:
      - EMAIL
      - NOTIFICATION
      - SCHEDULING
    collaborationMode: HIERARCHICAL
    
  version:
    version: 1
    status: RELEASED
    
  # 场景定义（替代原 FormClassBean）
  scenes:
    - sceneId: resume-screening-scene
      name: 简历筛选场景
      sceneType: FORM
      agent:
        agentId: resume-agent
        agentType: LLM
        capabilities:
          - EMAIL
          - DOCUMENT
          - ANALYSIS
        llmConfig:
          model: gpt-4
          temperature: 0.7
        a2uiConfig:
          pageAgent:
            agentId: resume-page-agent
            pageId: resume-page
            pageType: form
      interactionConfig:
        a2a:
          - fromAgent: resume-agent
            toAgent: notification-agent
            messageType: NOTIFICATION
        p2a:
          - participantType: PERFORMER
            agentId: resume-agent
            messageType: COMMAND
            
  activities:
    - id: start
      name: 开始
      category: EVENT
      position: START
      
    - id: resume-screening
      name: 简历筛选
      category: SCENE
      position: NORMAL
      
      # 活动主体定义（新增）
      performer:
        performerType: AGENT
        agentConfig:
          agentId: resume-agent
          agentType: LLM
          scheduleStrategy: PARALLEL
          collaborationMode: HIERARCHICAL
          capabilities:
            - EMAIL
            - DOCUMENT
            - ANALYSIS
          llmConfig:
            model: gpt-4
            temperature: 0.7
            functionCallingEnabled: true
            
      # 能力配置（新增）
      capabilities:
        - capabilityId: email-notify
          capabilityType: EMAIL
          autoBind: true
        - capabilityId: doc-analysis
          capabilityType: ANALYSIS
          autoBind: true
          
      # 场景绑定
      sceneBinding:
        sceneId: resume-screening-scene
        sceneType: FORM
        
      timing:
        limit: 3
        durationUnit: D
        deadlineOperation: DELAY
        
      routing:
        join: AND
        split: AND
        canRouteBack: true
        routeBackMethod: LAST
        
    - id: device-notification
      name: 设备通知
      category: AGENT
      position: NORMAL
      
      # 设备执行者配置（使用南向协议）
      performer:
        performerType: DEVICE
        deviceConfig:
          endpoint:
            endpointId: notification-device
            endpointType: END
            protocolType: END
            address: 192.168.1.100
            port: 8080
            capabilities:
              - DEVICE_NOTIFICATION
          commands:
            - commandId: send-notification
              commandType: END_COMMAND
              parameters:
                type: push
          protocol:
            protocolType: END
            securityConfig:
              authType: TOKEN
              
    - id: end
      name: 结束
      category: EVENT
      position: END
      
  routes:
    - id: r1
      name: 开始到简历筛选
      from: start
      to: resume-screening
      routeDirection: FORWARD
      routeConditionType: CONDITION
      routeCondition: "true"
```

### 2. Agent 定义 YAML

```yaml
apiVersion: agent.ooder.net/v1
kind: AgentDef
metadata:
  agentId: resume-agent
  agentName: 简历筛选 Agent
  agentType: LLM
  description: HR 简历筛选智能 Agent
spec:
  # 能力配置
  capabilities:
    - capabilityId: email
      capabilityType: EMAIL
      config:
        smtpHost: smtp.example.com
        smtpPort: 587
    - capabilityId: document
      capabilityType: DOCUMENT
      config:
        supportedFormats:
          - pdf
          - docx
    - capabilityId: analysis
      capabilityType: ANALYSIS
      config:
        model: gpt-4
        
  # 北向协议配置
  northbound:
    a2uiConfig:
      pageAgent:
        agentId: resume-page-agent
        pageId: resume-page
        pageType: form
        templatePath: /templates/resume-screening.html
        functions:
          - name: submitResult
            functionType: HYBRID
            description: 提交筛选结果
            parameters:
              result:
                type: string
                enum:
                  - PASS
                  - REJECT
                  
    a2aConfig:
      agents:
        - agentId: notification-agent
          messageType: NOTIFICATION
          trigger: onResultSubmit
          
    p2aConfig:
      participants:
        - participantType: PERFORMER
          messageType: COMMAND
          trigger: onManualReview
          
  # 南向协议配置
  southbound:
    protocolType: END
    deviceEndpoints:
      - endpointId: email-server
        endpointType: END
        address: smtp.example.com
        port: 587
        capabilities:
          - EMAIL_SEND
          - EMAIL_RECEIVE
        authConfig:
          authType: TOKEN
          
  # LLM 配置
  llmConfig:
    model: gpt-4
    temperature: 0.7
    maxTokens: 2000
    functionCallingEnabled: true
    streamingEnabled: true
    
  # 协作配置
  collaboration:
    mode: HIERARCHICAL
    coordinator: recruitment-process-agent
    peers:
      - notification-agent
      - calendar-agent
```

---

## 第三册：南向协议命令规范

### 1. 命令分类

```
MCP Commands (主控协议命令)
├── MCP_REGISTER      - MCP 节点注册
├── MCP_DEREGISTER    - MCP 节点注销
├── MCP_HEARTBEAT     - 心跳保活
├── MCP_STATUS        - 状态上报
├── MCP_DISCOVER      - 设备发现
└── MCP_CONFIG        - 配置下发

Route Commands (路由协议命令)
├── ROUTE_REGISTER    - 路由节点注册
├── ROUTE_UPDATE      - 路由表更新
├── ROUTE_QUERY       - 路由查询
├── ROUTE_STATUS      - 路由状态
└── ROUTE_CONFIG      - 路由配置

End Commands (终端协议命令)
├── END_REGISTER      - 终端注册
├── END_CAPABILITY    - 能力上报
├── END_STATUS        - 状态上报
├── END_COMMAND       - 命令执行
└── END_RESULT        - 结果上报
```

### 2. 命令消息格式

```json
{
  "header": {
    "version": "2.0",
    "commandType": "END_COMMAND",
    "commandId": "cmd-uuid-001",
    "timestamp": 1707312000000,
    "sourceId": "agent-001",
    "targetId": "device-001",
    "priority": "NORMAL",
    "ttl": 30
  },
  "payload": {
    "action": "send_notification",
    "parameters": {
      "type": "push",
      "message": "简历筛选完成"
    }
  },
  "signature": {
    "algorithm": "SHA256withRSA",
    "value": "base64-encoded-signature"
  }
}
```

---

## 第四册：技术架构规范

### 1. 技术选型

#### 1.1 前端技术栈

| 层级 | 技术选型 | 说明 |
|------|----------|------|
| **页面结构** | HTML5 | 纯 HTML 页面，与 nexus 一致 |
| **样式系统** | CSS3 + CSS变量 | 复用 nexus CSS 模块化设计 |
| **脚本语言** | JavaScript (ES6+) | 原生 JS，模块化组织 |
| **流程图** | bpmn-js | BPMN 2.0 流程设计器核心 |
| **代码编辑** | Monaco Editor | 代码/表达式编辑 |
| **图标库** | Remix Icon | 与 nexus 一致 |
| **主题系统** | CSS 变量 + 主题切换 | 复用 nexus 主题系统 |

#### 1.2 后端技术栈

| 层级 | 技术选品 | 说明 |
|------|----------|------|
| **框架** | Spring Boot 3.x | 后端框架 |
| **场景引擎** | SceneEngine | 场景定义和执行 |
| **流程引擎** | ooder BPM | 现有 BPM 引擎 |
| **数据存储** | VFS + MySQL | 文件存储 + 数据库 |
| **协议支持** | ooderSDK | MCP/ROUTE/END 协议 |

#### 1.3 技术选型说明

**为什么不用 Vue 3**：

| 考量因素 | 说明 |
|----------|------|
| **架构一致性** | nexus 控制台使用纯 HTML + JS，引入 Vue 会增加复杂度 |
| **维护成本** | 团队熟悉现有 nexus 架构，无额外学习成本 |
| **后期规划** | 后期会推出真正的 A2UI 架构，当前版本过渡使用 |
| **性能考量** | 原生 JS 更轻量，无框架运行时开销 |

### 2. 架构设计

#### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                    BPM 设计器架构                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                 前端层 (HTML + JS + CSS)              │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │ 流程画布 │ │ 属性面板 │ │ 工具栏  │ │ 组件库  │   │  │
│   │  │(bpmn-js)│ │(nexus)  │ │(Toolbar)│ │(Palette)│   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   │  ┌─────────────────────────────────────────────┐   │  │
│   │  │              BpmSDK (API 封装)               │   │  │
│   │  └─────────────────────────────────────────────┘   │  │
│   └─────────────────────────────────────────────────────┘  │
│                           │                                │
│                           ▼                                │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                    API 层 (REST)                     │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │/process │ │/activity│ │/agent   │ │/scene   │   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   └─────────────────────────────────────────────────────┘  │
│                           │                                │
│                           ▼                                │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                    服务层 (Spring Boot)              │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │Process  │ │Activity │ │Agent    │ │Scene    │   │  │
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

#### 2.2 SDK 封装模式

参考 nexus SDK 封装模式：

```javascript
class BpmSDK {
    constructor(config) {
        this.baseUrl = config.baseUrl || '/api/bpm';
        this.timeout = config.timeout || 30000;
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

### 3. UI 组件复用

#### 3.1 复用 nexus CSS 变量

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

#### 3.2 组件分层

| 层级 | 组件类型 | 来源 | 说明 |
|------|----------|------|------|
| **基础层** | CSS 变量、主题 | 复用 nexus | 直接复用 |
| **核心层** | 布局、表单、按钮 | 提取 nexus | 提取封装 |
| **扩展层** | 面板、表格、树 | 扩展开发 | 基于 nexus 风格 |
| **专用层** | 画布、节点、连线 | 新增开发 | BPM 专用 |

### 4. 实施路线

| 阶段 | 内容 | 周期 |
|------|------|------|
| **第一阶段** | 基础框架搭建 | 1周 |
| **第二阶段** | 流程画布开发 | 2周 |
| **第三阶段** | 属性面板开发 | 2周 |
| **第四阶段** | Agent/A2UI扩展 | 2周 |
| **第五阶段** | 测试与优化 | 1周 |

### 5. 参考资源

| 资源 | 路径 | 说明 |
|------|------|------|
| nexus 控制台 | E:\github\ooder-skills\temp\ooder-Nexus | nexus 前端架构 |
| nexus SDK 封装 | E:\github\ooder-skills\temp\ooder-Nexus\src\main\resources\static\console\js\nexus\sdk\ | SDK 设计模式 |
| nexus CSS 模块 | E:\github\ooder-skills\temp\ooder-Nexus\src\main\resources\static\console\css\modules\ | CSS 模块化 |
| 场景定义页面 | E:\github\ooder-skills\temp\ooder-Nexus\src\main\resources\static\console\pages\scene\scene-definition.html | 页面结构参考 |

---

## 附录：新旧对照表

| 原概念 | 新概念 | 说明 |
|--------|--------|------|
| FormClassBean | SceneDef | 场景定义替代表单定义 |
| ActivityDefImpl | ActivityCategory + PerformerType | 活动分类 + 主体类型 |
| DeviceEndPoint | DeviceEndpoint (南向协议) | 使用南向协议规范 |
| Command (设备命令) | SouthboundProtocol | 使用南向协议命令体系 |
| 办理人/阅办人 | PerformerDef | 统一的活动主体定义 |
| 监听器 | ListenerDef + Agent 能力 | 监听器与 Agent 能力结合 |
| 公式 | Function Calling | 公式扩展为 Function Calling |

---

**文档路径**: `E:\github\ooder-skills\docs\bpm-spec\v4\README.md`
