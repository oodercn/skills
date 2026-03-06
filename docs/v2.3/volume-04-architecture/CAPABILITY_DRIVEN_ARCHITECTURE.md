# 能力驱动架构深度分析

## 文档信息

| 项目 | 说明 |
|------|------|
| 版本 | v1.0 |
| 日期 | 2026-03-02 |
| 状态 | 架构分析 |

---

## 一、现有定义与南向协议对比

### 1.1 现有定义中的关键概念

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        现有定义中的关键概念                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【SkillManifest 中的场景绑定】                                             │
│   ──────────────────────────                                                │
│   skillId: skill-knowledge-qa                                              │
│   sceneId: "knowledge-qa"              ← Skill绑定到场景                    │
│   primaryScene:                        ← 主场景配置                         │
│     sceneId: "knowledge-qa"                                                 │
│     autoCreate: true                   ← 自动创建场景                       │
│   collaborativeScenes: [...]           ← 协作场景列表                       │
│   capabilities: [...]                  ← Skill提供的能力                    │
│                                                                             │
│   【SceneDefinition 中的能力绑定】                                           │
│   ──────────────────────────                                                │
│   name: daily-report                                                       │
│   capabilities:                        ← 场景包含的能力                     │
│     - report-remind                                                         │
│     - report-submit                                                         │
│     - report-aggregate                                                      │
│   roles:                               ← 角色定义                           │
│     - name: manager                                                         │
│       capabilities: [report-remind, report-aggregate]  ← 角色绑定能力       │
│                                                                             │
│   【CapabilityBinding 中的绑定关系】                                         │
│   ──────────────────────────                                                │
│   bindingId: binding-remind-001                                            │
│   sceneGroupId: daily-report-group-001  ← 绑定到场景组                      │
│   capabilityId: skill-remind-001         ← 能力ID                          │
│   capId: daily-report-remind             ← 场景内短ID                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 南向协议中的场景定义

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        南向协议中的场景定义                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【SceneGroupInfo - 场景组】                                                │
│   ────────────────────────                                                  │
│   SceneGroup 是共享KEY和VFS资源的Agent集合                                   │
│   ├── groupId: 场景组唯一标识                                               │
│   ├── sceneId: 关联场景ID                                                   │
│   ├── primaryId: 主节点ID                                                   │
│   └── memberIds: 成员ID列表                                                 │
│                                                                             │
│   【SceneGroupKey - 场景组密钥】                                             │
│   ──────────────────────────                                                │
│   ├── groupId: 场景组唯一标识                                               │
│   ├── sceneName: 场景名称                                                   │
│   ├── masterKey: 主密钥                                                     │
│   ├── permissions: 权限定义                                                 │
│   │    ├── vfs: VFS权限                                                    │
│   │    └── capabilities: 能力权限                                           │
│   └── members: 成员列表（含角色）                                            │
│                                                                             │
│   【CollaborationProtocol - 协作协议】                                       │
│   ──────────────────────────                                                │
│   ├── joinSceneGroup(): 加入场景组                                          │
│   ├── receiveTask(): 接收任务                                               │
│   ├── submitTaskResult(): 提交结果                                          │
│   └── syncState(): 状态同步                                                 │
│                                                                             │
│   【关键发现】                                                               │
│   南向协议中：                                                               │
│   ├── 场景是Agent协作的上下文                                               │
│   ├── 场景组是运行时实体                                                    │
│   ├── 能力通过 permissions.capabilities 授权                                │
│   └── 没有定义"场景创建"的驱动机制                                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、冲突点深度分析

### 2.1 冲突一：场景是容器还是能力？

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        冲突一：场景的本质定位                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【现有定义】场景是能力的容器                                               │
│   ──────────────────────────────                                            │
│                                                                             │
│   SceneDefinition                                                           │
│   ├── capabilities: [report-remind, report-submit, ...]                    │
│   │    └── 场景包含多个能力                                                 │
│   ├── roles: [manager, employee]                                            │
│   │    └── 角色绑定能力                                                     │
│   └── workflow: [...]                                                       │
│        └── 工作流调用能力                                                   │
│                                                                             │
│   隐含假设：场景 > 能力（容器关系）                                          │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【新理解】场景是一种特殊的能力                                             │
│   ──────────────────────────────                                            │
│                                                                             │
│   SceneCapability                                                            │
│   ├── type: SCENE_CAPABILITY                                               │
│   ├── mainFirst: true                                                       │
│   ├── capabilities: [report-remind, report-submit, ...]                    │
│   │    └── 场景特性包含子能力                                               │
│   └── collaborativeCapabilities: [...]                                      │
│        └── 协作能力入口                                                     │
│                                                                             │
│   新假设：场景 = 能力（同一层次）                                             │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【冲突本质】                                                               │
│   ├── 现有：场景是静态容器，能力被绑定到场景                                 │
│   ├── 新理解：场景是自驱能力，能力是场景的组成部分                           │
│   └── 驱动方向相反：                                                        │
│        现有：外部驱动场景 → 场景调用能力                                     │
│        新理解：场景特性自驱 → 协调子能力                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 冲突二：primaryScene 绑定 vs 能力自驱

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        冲突二：驱动机制冲突                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【现有定义】primaryScene 绑定机制                                          │
│   ──────────────────────────────                                            │
│                                                                             │
│   SkillManifest:                                                             │
│     primaryScene:                                                            │
│       sceneId: "knowledge-qa"                                               │
│       autoCreate: true                  ← 外部触发创建                      │
│                                                                             │
│   驱动流程：                                                                 │
│   Skill安装 → 检查primaryScene.autoCreate → 创建SceneDefinition             │
│                                                                             │
│   问题：                                                                     │
│   ├── 场景是被动的，等待外部创建                                             │
│   ├── 场景没有自己的"意志"                                                  │
│   └── 场景不知道如何启动自己                                                 │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【新理解】mainFirst 自驱机制                                               │
│   ──────────────────────────────                                            │
│                                                                             │
│   SceneCapability:                                                           │
│     mainFirst: true                     ← 自驱入口                          │
│     mainFirstConfig:                                                         │
│       selfCheck: [...]                  ← 自检                              │
│       selfStart: [...]                  ← 自启                              │
│       selfDrive: [...]                  ← 自驱                              │
│                                                                             │
│   驱动流程：                                                                 │
│   Skill安装 → 发现mainFirst能力 → 能力自驱启动                              │
│                                                                             │
│   优势：                                                                     │
│   ├── 场景是主动的，知道自己如何启动                                         │
│   ├── 场景有"意志"（mainFirst入口）                                         │
│   └── 场景可以自主检查和启动                                                 │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【冲突本质】                                                               │
│   ├── primaryScene：被动绑定，外部驱动                                      │
│   ├── mainFirst：主动自驱，内部驱动                                         │
│   └── 驱动主体不同：                                                         │
│        primaryScene：安装程序是驱动者                                        │
│        mainFirst：场景特性自己是驱动者                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 冲突三：用户故事中的驱动源头

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        冲突三：用户故事驱动源头                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【用户故事一：日志汇报场景】                                                │
│   ──────────────────────────                                                │
│                                                                             │
│   "我是部门领导，我要求下属员工每天下班前5:00将工作日志发给我统计"           │
│                                                                             │
│   驱动源头：部门领导的需求                                                   │
│   驱动方式：领导创建场景 → 定义能力需求 → 员工被动接受                       │
│                                                                             │
│   问题：                                                                     │
│   ├── 谁来"创建"场景？领导还是系统？                                        │
│   ├── 场景如何"知道"要每天17:00提醒？                                       │
│   ├── 员工的"被动接受"如何实现？                                            │
│   └── LLM的"自动汇总"由谁驱动？                                             │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【用户故事二：智能家居场景】                                                │
│   ──────────────────────────                                                │
│                                                                             │
│   "我是家庭主人，我希望实现智能化的家庭安防和节能管理"                       │
│                                                                             │
│   驱动源头：家庭主人的需求                                                   │
│   驱动方式：主人配置场景 → 设备自动响应 → 事件触发动作                       │
│                                                                             │
│   问题：                                                                     │
│   ├── "离家时自动开启安防"由谁判断？                                        │
│   ├── "夜间检测异常"由谁监控？                                              │
│   ├── 设备如何"知道"场景规则？                                              │
│   └── 多设备协作由谁协调？                                                  │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【用户故事三：零配置安装】                                                  │
│   ──────────────────────────                                                │
│                                                                             │
│   "我想要从场景模板列表中选择一个模板并一键部署"                             │
│                                                                             │
│   驱动源头：用户选择模板                                                     │
│   驱动方式：用户点击 → 系统安装 → 自动创建场景                               │
│                                                                             │
│   问题：                                                                     │
│   ├── 模板是谁定义的？                                                       │
│   ├── "自动创建场景"后场景如何运行？                                         │
│   ├── 谁来驱动场景的日常运行？                                               │
│   └── 场景"激活"后谁来管理？                                                │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【冲突本质】                                                               │
│   用户故事中的驱动源头是"人的需求"，但：                                     │
│   ├── 人只能触发一次（创建/配置）                                           │
│   ├── 场景的日常运行需要持续驱动                                            │
│   ├── 多Agent协作需要协调者                                                 │
│   └── 事件响应需要监听者                                                    │
│                                                                             │
│   结论：需要一个"持续驱动者"，这个角色应该由场景特性自己承担                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、启动场景的"源头"归纳

### 3.1 所有启动源头分析

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        启动场景的所有源头                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【源头一：用户需求】                                                       │
│   ─────────────────                                                          │
│   "我是部门领导，我要求..."                                                 │
│   "我是家庭主人，我希望..."                                                 │
│   "我想要一键部署..."                                                       │
│                                                                             │
│   本质：人的意图                                                             │
│   特点：一次性触发，表达"要什么"                                             │
│   问题：无法持续驱动                                                         │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【源头二：时间触发】                                                       │
│   ─────────────────                                                          │
│   "每天下班前5:00"                                                          │
│   "每周一上午9:00"                                                           │
│   cron: "0 17 * * 1-5"                                                      │
│                                                                             │
│   本质：时间事件                                                             │
│   特点：周期性触发，驱动定时任务                                             │
│   问题：谁来监听时间？谁来执行任务？                                         │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【源头三：事件触发】                                                       │
│   ─────────────────                                                          │
│   "离家时自动开启"                                                           │
│   "夜间检测异常"                                                             │
│   "用户提交日志后"                                                           │
│                                                                             │
│   本质：状态变化事件                                                         │
│   特点：条件触发，驱动响应动作                                               │
│   问题：谁来监听事件？谁来判断条件？                                         │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【源头四：能力调用】                                                       │
│   ─────────────────                                                          │
│   "调用邮件发送能力"                                                         │
│   "调用知识检索能力"                                                         │
│   "调用LLM分析能力"                                                          │
│                                                                             │
│   本质：功能调用                                                             │
│   特点：被动响应，执行具体功能                                               │
│   问题：谁来发起调用？调用顺序如何决定？                                     │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【源头五：协作请求】                                                       │
│   ─────────────────                                                          │
│   "场景A需要场景B的索引服务"                                                 │
│   "主场景协调协作场景"                                                       │
│   "PRIMARY分配任务给BACKUP"                                                  │
│                                                                             │
│   本质：协作协议                                                             │
│   特点：跨场景通信，驱动协作                                                 │
│   问题：谁来协调？协作规则如何定义？                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 源头抽象为能力

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        源头抽象为能力驱动                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【核心洞察】                                                               │
│   所有启动源头都可以抽象为"能力"：                                           │
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │                                                                     │   │
│   │   源头一：用户需求                                                   │   │
│   │   ════════════════                                                   │   │
│   │   抽象为：intent-receiver 能力                                       │   │
│   │   ├── 接收用户意图                                                  │   │
│   │   ├── 解析需求                                                      │   │
│   │   └── 触发场景启动                                                  │   │
│   │                                                                     │   │
│   │   源头二：时间触发                                                   │   │
│   │   ════════════════                                                   │   │
│   │   抽象为：scheduler 能力                                             │   │
│   │   ├── 监听时间事件                                                  │   │
│   │   ├── 判断触发条件                                                  │   │
│   │   └── 驱动定时任务                                                  │   │
│   │                                                                     │   │
│   │   源头三：事件触发                                                   │   │
│   │   ════════════════                                                   │   │
│   │   抽象为：event-listener 能力                                        │   │
│   │   ├── 订阅事件                                                      │   │
│   │   ├── 判断条件                                                      │   │
│   │   └── 触发响应动作                                                  │   │
│   │                                                                     │   │
│   │   源头四：能力调用                                                   │   │
│   │   ════════════════                                                   │   │
│   │   抽象为：capability-invoker 能力                                    │   │
│   │   ├── 发起调用                                                      │   │
│   │   ├── 管理调用链                                                    │   │
│   │   └── 处理返回结果                                                  │   │
│   │                                                                     │   │
│   │   源头五：协作请求                                                   │   │
│   │   ════════════════                                                   │   │
│   │   抽象为：collaboration-coordinator 能力                             │   │
│   │   ├── 协调协作场景                                                  │   │
│   │   ├── 分配任务                                                      │   │
│   │   └── 同步状态                                                      │   │
│   │                                                                     │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   【统一模型】                                                               │
│   场景特性 = SuperAgent = 包含所有驱动能力                                  │
│                                                                             │
│   SceneCapability:                                                           │
│     mainFirst: true                                                          │
│     capabilities:                                                            │
│       - intent-receiver        # 接收意图                                   │
│       - scheduler              # 时间驱动                                   │
│       - event-listener         # 事件驱动                                   │
│       - capability-invoker     # 能力调用                                   │
│       - collaboration-coordinator  # 协作协调                               │
│       - ...业务能力...                                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、完整的能力驱动架构

### 4.1 能力类型体系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力类型体系（完整版）                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   CapabilityType:                                                           │
│   ══════════════                                                            │
│                                                                             │
│   1. ATOMIC_CAPABILITY (原子能力)                                           │
│      ├── 单一功能，不可分解                                                 │
│      ├── 无子能力                                                           │
│      ├── 示例：email-send, file-read, http-request                         │
│      └── 驱动方式：被动调用                                                 │
│                                                                             │
│   2. COMPOSITE_CAPABILITY (组合能力)                                        │
│      ├── 组合多个原子能力                                                   │
│      ├── 无涌现行为                                                         │
│      ├── 示例：notification-chain, data-pipeline                           │
│      └── 驱动方式：顺序调用                                                 │
│                                                                             │
│   3. SCENE_CAPABILITY (场景特性)                                            │
│      ├── 自驱型SuperAgent                                                   │
│      ├── mainFirst入口                                                      │
│      ├── 协调子能力                                                         │
│      ├── 涌现新行为                                                         │
│      ├── 可启动协作场景                                                     │
│      ├── 示例：scene-daily-report, scene-smart-home                        │
│      └── 驱动方式：自驱 + 协作                                              │
│                                                                             │
│   4. DRIVER_CAPABILITY (驱动能力) ← 新增                                    │
│      ├── intent-receiver: 接收意图                                          │
│      ├── scheduler: 时间驱动                                                │
│      ├── event-listener: 事件驱动                                           │
│      ├── capability-invoker: 能力调用                                       │
│      └── collaboration-coordinator: 协作协调                                │
│                                                                             │
│   关系：                                                                     │
│   ══════                                                                     │
│   SCENE_CAPABILITY                                                           │
│        ├── 必含 → DRIVER_CAPABILITY (驱动能力)                              │
│        ├── 可含 → ATOMIC_CAPABILITY (原子能力)                              │
│        ├── 可含 → COMPOSITE_CAPABILITY (组合能力)                           │
│        ├── 可协作 → SCENE_CAPABILITY (其他场景特性)                         │
│        └── 涌现 → 新的 SCENE_CAPABILITY                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 场景特性的完整定义

```yaml
# 场景能力完整定义
apiVersion: capability.ooder.net/v1
kind: SceneCapability

metadata:
  id: scene-daily-report
  name: 日志汇报场景特性
  type: SCENE_CAPABILITY
  mainFirst: true                    # 自驱入口

spec:
  # 驱动能力（内置）
  driverCapabilities:
    - id: intent-receiver
      description: 接收用户意图
      
    - id: scheduler
      description: 时间驱动
      config:
        timezone: "Asia/Shanghai"
        
    - id: event-listener
      description: 事件监听
      
    - id: capability-invoker
      description: 能力调用链管理
      
    - id: collaboration-coordinator
      description: 协作场景协调

  # 业务能力
  capabilities:
    - id: report-remind
      name: 日志提醒
      type: ATOMIC_CAPABILITY
      
    - id: report-submit
      name: 日志提交
      type: ATOMIC_CAPABILITY
      
    - id: report-aggregate
      name: 日志汇总
      type: COMPOSITE_CAPABILITY
      capabilities: [data-collect, data-merge]
      
    - id: report-analyze
      name: 日志分析
      type: ATOMIC_CAPABILITY

  # 协作能力入口
  collaborativeCapabilities:
    - capabilityId: scene-email-notification
      role: PROVIDER
      interface: notification-service
      autoStart: true
      
    - capabilityId: scene-llm-assistant
      role: PROVIDER
      interface: llm-analysis-service
      autoStart: false

  # mainFirst 入口定义
  mainFirstConfig:
    # 自检
    selfCheck:
      - checkCapabilities: [report-remind, report-submit, report-aggregate]
      - checkDriverCapabilities: [scheduler, event-listener]
      - checkCollaborative: [scene-email-notification]
      
    # 自启
    selfStart:
      - initDriverCapabilities: [scheduler, event-listener, capability-invoker]
      - initCapabilities: [report-remind, report-submit, report-aggregate, report-analyze]
      - bindAddresses: auto
      
    # 启动协作
    startCollaboration:
      - startScene: scene-email-notification
      - bindInterface: notification-service
      
    # 自驱运行
    selfDrive:
      # 时间驱动规则
      scheduleRules:
        - trigger: "0 17 * * 1-5"
          action: remind-flow
        - trigger: "0 18 * * 1-5"
          action: aggregate-flow
          
      # 事件驱动规则
      eventRules:
        - event: user-submitted
          condition: "all_submitted"
          action: aggregate-flow
          
      # 能力调用链
      capabilityChains:
        remind-flow:
          - capability: report-remind
            input: { targetUsers: "${role.employee}" }
            
        aggregate-flow:
          - capability: report-aggregate
          - capability: report-analyze
          - capability: report-remind
            input: { targetUsers: "${role.manager}", message: "${analysisResult}" }

  # 角色定义
  roles:
    - name: manager
      capabilities: [report-remind, report-aggregate, report-analyze]
      
    - name: employee
      capabilities: [report-submit]
```

### 4.3 能力驱动的完整流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力驱动的完整流程                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Phase 0: 意图接收                                                         │
│   ══════════════════                                                        │
│   intent-receiver 接收用户意图                                              │
│        │                                                                    │
│        ├── "我要创建日志汇报场景"                                           │
│        │                                                                    │
│        ▼                                                                    │
│   解析意图 → 选择场景特性模板 → 触发安装                                    │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   Phase 1: Skill安装                                                        │
│   ══════════════════                                                        │
│   SkillPackageManager.install(skillId)                                      │
│        │                                                                    │
│        ├── 安装 Skill                                                       │
│        ├── 解析 dependencies → 安装依赖                                     │
│        └── 解析 capabilities → 注册能力                                     │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   Phase 2: mainFirst 发现                                                   │
│   ══════════════════════                                                    │
│   扫描已注册能力                                                             │
│        │                                                                    │
│        ├── 发现 mainFirst = true 的能力                                     │
│        │                                                                    │
│        └── scene-daily-report (场景特性)                                    │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   Phase 3: mainFirst.selfCheck()                                            │
│   ════════════════════════════                                              │
│   场景特性自检                                                               │
│        │                                                                    │
│        ├── checkCapabilities: 业务能力就绪？                                │
│        ├── checkDriverCapabilities: 驱动能力就绪？                          │
│        └── checkCollaborative: 协作能力可用？                               │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   Phase 4: mainFirst.selfStart()                                            │
│   ════════════════════════════                                              │
│   场景能力自启                                                               │
│        │                                                                    │
│        ├── initDriverCapabilities: 初始化驱动能力                           │
│        │    ├── scheduler: 启动时间监听                                     │
│        │    ├── event-listener: 启动事件监听                                │
│        │    └── capability-invoker: 初始化调用链                            │
│        │                                                                    │
│        ├── initCapabilities: 初始化业务能力                                 │
│        │    ├── report-remind: 就绪                                         │
│        │    ├── report-submit: 就绪                                         │
│        │    └── ...                                                         │
│        │                                                                    │
│        └── bindAddresses: 绑定能力地址                                      │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   Phase 5: mainFirst.startCollaboration()                                   │
│   ═══════════════════════════════════                                       │
│   启动协作场景                                                               │
│        │                                                                    │
│        ├── 遍历 collaborativeCapabilities                                   │
│        │                                                                    │
│        ├── scene-email-notification                                         │
│        │    │                                                               │
│        │    ▼                                                               │
│        │    调用 scene-email-notification.mainFirst()                       │
│        │    (协作场景自驱启动)                                              │
│        │    │                                                               │
│        │    ▼                                                               │
│        │    获取 notification-service 接口                                  │
│        │                                                                    │
│        └── 建立协作链路                                                      │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   Phase 6: mainFirst.selfDrive()                                            │
│   ════════════════════════════                                              │
│   场景特性自驱运行                                                           │
│        │                                                                    │
│        ├── scheduler 监听时间事件                                           │
│        │    └── 17:00 → 触发 remind-flow                                   │
│        │                                                                    │
│        ├── event-listener 监听业务事件                                      │
│        │    └── user-submitted → 触发 aggregate-flow                       │
│        │                                                                    │
│        ├── capability-invoker 执行能力链                                    │
│        │    └── report-remind → report-aggregate → report-analyze          │
│        │                                                                    │
│        └── collaboration-coordinator 协调协作                               │
│             └── 调用 notification-service 发送通知                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 五、与南向协议的统一

### 5.1 能力驱动与南向协议的映射

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力驱动与南向协议映射                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【能力驱动概念】          【南向协议概念】         【映射关系】             │
│   ══════════════════        ══════════════════       ══════════════          │
│                                                                             │
│   SceneCapability            SceneGroupInfo          场景特性 = 场景组      │
│   ├── mainFirst              ├── PRIMARY Agent       自驱入口 = 主节点      │
│   ├── capabilities           ├── permissions.capabilities  子能力 = 权限    │
│   └── collaborativeScenes    └── memberIds           协作场景 = 成员        │
│                                                                             │
│   DRIVER_CAPABILITY          CollaborationProtocol   驱动能力 = 协作协议    │
│   ├── scheduler              ├── (时间触发)          时间驱动               │
│   ├── event-listener         ├── (事件触发)          事件驱动               │
│   ├── capability-invoker     ├── receiveTask()       能力调用 = 任务接收    │
│   └── collaboration-coord    └── syncState()         协作协调 = 状态同步    │
│                                                                             │
│   mainFirst.selfDrive()      PRIMARY Agent 职责       自驱 = 主节点职责     │
│   ├── 监听事件               ├── 消息路由                                   │
│   ├── 协调能力               ├── 任务分配                                   │
│   └── 管理协作               └── 资源协调                                   │
│                                                                             │
│   collaborativeCapability    BACKUP Agent 职责        协作能力 = 备节点     │
│   ├── autoStart              ├── 加入场景组                                  │
│   ├── interface              ├── 执行任务                                    │
│   └── role                   └── 状态同步                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 统一后的架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        统一后的能力驱动架构                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │                        用户意图                                      │   │
│   │                     intent-receiver                                 │   │
│   └──────────────────────────────┬──────────────────────────────────────┘   │
│                                  │                                          │
│                                  ▼                                          │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │                     Skill 安装                                       │   │
│   │              SkillPackageManager.install()                           │   │
│   └──────────────────────────────┬──────────────────────────────────────┘   │
│                                  │                                          │
│                                  ▼                                          │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │                   mainFirst 发现                                     │   │
│   │            扫描 mainFirst = true 的能力                              │   │
│   └──────────────────────────────┬──────────────────────────────────────┘   │
│                                  │                                          │
│                                  ▼                                          │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │                   SceneCapability                                    │   │
│   │                                                                      │   │
│   │   ┌─────────────────────────────────────────────────────────────┐   │   │
│   │   │              DRIVER_CAPABILITY (驱动能力)                    │   │   │
│   │   │                                                              │   │   │
│   │   │  scheduler          event-listener                          │   │   │
│   │   │  capability-invoker collaboration-coordinator               │   │   │
│   │   └─────────────────────────────────────────────────────────────┘   │   │
│   │                              │                                       │   │
│   │                              │ 驱动                                  │   │
│   │                              ▼                                       │   │
│   │   ┌─────────────────────────────────────────────────────────────┐   │   │
│   │   │              业务能力 (ATOMIC/COMPOSITE)                     │   │   │
│   │   │                                                              │   │   │
│   │   │  report-remind  report-submit  report-aggregate             │   │   │
│   │   └─────────────────────────────────────────────────────────────┘   │   │
│   │                              │                                       │   │
│   │                              │ 协作                                  │   │
│   │                              ▼                                       │   │
│   │   ┌─────────────────────────────────────────────────────────────┐   │   │
│   │   │              collaborativeCapabilities                       │   │   │
│   │   │                                                              │   │   │
│   │   │  scene-email-notification ──► notification-service           │   │   │
│   │   │  scene-llm-assistant ──────► llm-analysis-service           │   │   │
│   │   └─────────────────────────────────────────────────────────────┘   │   │
│   │                                                                      │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                  │                                          │
│                                  │ 映射到南向协议                           │
│                                  ▼                                          │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │                     SceneGroup (南向协议)                            │   │
│   │                                                                      │   │
│   │   SceneCapability.mainFirst ══ SceneGroup.PRIMARY                  │   │
│   │   collaborativeCapabilities ══ SceneGroup.BACKUP                    │   │
│   │   driverCapabilities ══ CollaborationProtocol                       │   │
│   │                                                                      │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 六、核心结论

### 6.1 冲突解决

| 冲突 | 原有理解 | 新理解 | 解决方案 |
|------|----------|--------|----------|
| 场景本质 | 场景是能力的容器 | 场景是一种特殊的能力 | 场景 = SCENE_CAPABILITY |
| 驱动机制 | primaryScene 被动绑定 | mainFirst 主动自驱 | mainFirst 替代 primaryScene |
| 驱动源头 | 外部驱动（用户/系统） | 能力自驱 | DRIVER_CAPABILITY 内置驱动 |
| 协作关系 | 场景组是独立概念 | 协作能力是场景特性的一部分 | collaborativeCapabilities |

### 6.2 统一模型

```
场景特性 = SuperAgent = 自驱能力容器

SceneCapability:
  ├── mainFirst: 自驱入口
  ├── DRIVER_CAPABILITY: 驱动能力（内置）
  │    ├── intent-receiver
  │    ├── scheduler
  │    ├── event-listener
  │    ├── capability-invoker
  │    └── collaboration-coordinator
  ├── 业务能力: ATOMIC/COMPOSITE
  └── collaborativeCapabilities: 协作能力入口
```

### 6.3 能力驱动闭环

```
意图接收 → Skill安装 → mainFirst发现 → 自检 → 自启 → 启动协作 → 自驱运行
    ↑                                                        │
    └─────────────────── 持续驱动 ◄───────────────────────────┘
```

---

*作者: Ooder Team*  
*更新时间: 2026-03-02*
