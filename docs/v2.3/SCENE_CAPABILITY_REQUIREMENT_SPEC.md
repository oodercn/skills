# 场景能力需求规格说明书 v2.3

> **文档版本**: v2.3  
> **发布日期**: 2026-03-02  
> **适用范围**: skill-scene 模块开发  
> **文档状态**: 正式发布  
> **术语版本**: GLOSSARY_V2.md

---

## 一、概述

### 1.1 文档目的

本文档定义 Ooder 场景能力系统的完整需求规格，基于**能力驱动架构**，包括：
- 场景能力模型与关联关系
- 场景能力生命周期管理
- 参与者模型（User/Agent/SuperAgent）
- 能力驱动机制（mainFirst）
- 能力调用链规范
- 安全与权限控制

### 1.2 核心概念

本文档使用的核心术语请参考 [术语表v2](GLOSSARY_V2.md)，以下仅列出场景能力域核心术语：

| 概念 | 英文标识 | 定义 |
|------|----------|------|
| **场景能力** | SceneCapability | 自驱型SuperAgent能力，包含子能力和驱动能力，可涌现新行为 |
| **自驱入口** | mainFirst | 场景能力的启动入口，包含自检、自启、自驱、协作启动 |
| **场景组** | SceneGroup | 共享KEY和VFS资源的Agent集合，实现场景的协作和故障切换 |
| **参与者** | Participant | 场景中的活动主体，可以是用户、Agent或SuperAgent |
| **角色** | Role | 参与者在场景中的职责定义，决定其可访问的能力 |
| **能力调用链** | capabilityChains | 能力的有序调用序列，支持条件和分支 |

### 1.3 能力驱动架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力驱动架构核心                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   核心原则：场景即能力（Scene as Capability）                                │
│   ══════════════════════════════════                                        │
│                                                                             │
│   1. 场景能力 = SuperAgent能力                                              │
│      ├── 包含子能力（ATOMIC/COMPOSITE）                                     │
│      ├── 包含驱动能力（DRIVER_CAPABILITY）                                  │
│      └── 可涌现新行为                                                       │
│                                                                             │
│   2. 自驱机制                                                               │
│      ├── selfCheck(): 检查子能力就绪                                        │
│      ├── selfStart(): 初始化子能力                                          │
│      ├── selfDrive(): 驱动场景运行                                          │
│      └── startCollaboration(): 启动协作能力                                 │
│                                                                             │
│   3. 驱动能力（DRIVER_CAPABILITY）                                          │
│      ├── intent-receiver: 接收用户意图                                      │
│      ├── scheduler: 时间驱动                                                │
│      ├── event-listener: 事件监听                                           │
│      ├── capability-invoker: 能力调用                                       │
│      └── collaboration-coordinator: 协作协调                                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、核心问题讨论与结论

### 2.1 场景能力创建与场景组的关系

**问题**：创建场景能力时，是否同时创建场景组？

**结论**：否，场景组在场景能力激活时创建。

```
场景能力自启 → 绑定子能力 → 自驱运行 → 创建场景组（如有协作能力）
```

**设计理由**：
- 场景能力自启是静态配置阶段
- 场景组是运行时协作实体
- 只有自驱运行时才需要建立场景间通信

### 2.2 依赖安装时机

**问题**：启动场景能力后，创建场景组，依赖安装在创建前还是创建后？

**结论**：依赖安装在场景组创建前。

```
Phase 1: 安装检查 → 检查/安装 Skills
Phase 2: 场景能力自启 → selfStart()
Phase 3: 场景组创建 → 创建 SceneGroupInfo
Phase 4: 场景能力自驱 → selfDrive()
```

**设计理由**：
- 场景组需要所有 Skills 已就绪
- 避免运行时发现依赖缺失
- 保证场景组初始化成功

### 2.3 依赖配置位置

**问题**：核心依赖配置是在 Skills 还是在场景能力中？

**结论**：分层配置，各司其职。

| 层级 | 位置 | 职责 | 示例 |
|------|------|------|------|
| Skill 层 | skill-manifest.yaml | 技术依赖 | skill-knowledge-ui 依赖 skill-knowledge-base |
| 场景能力层 | SceneCapabilityDef | 业务组合 | 知识问答场景能力包含 kb + rag + llm |

---

## 三、用户用例

### 3.1 用例一：日志汇报场景能力（流程驱动型）

#### 用户故事

> **我是部门领导**，我要求下属员工每天下班前5:00将工作日志发给我统计。

#### 需求分析

- 定时提醒员工提交日志
- 员工提交后自动汇总
- LLM自动分析日志内容
- 发送汇总结果给领导

#### 场景能力定义

```yaml
apiVersion: capability.ooder.net/v1
kind: SceneCapability

metadata:
  id: scene-daily-report
  name: 日志汇报场景能力
  type: SCENE_CAPABILITY
  mainFirst: true

spec:
  # 驱动能力（内置）
  driverCapabilities:
    - id: scheduler
      config:
        timezone: "Asia/Shanghai"
    - id: event-listener
    - id: capability-invoker
    - id: collaboration-coordinator

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
      
    - id: report-analyze
      name: 日志分析
      type: ATOMIC_CAPABILITY

  # 协作能力入口
  collaborativeCapabilities:
    - capabilityId: scene-email-notification
      role: PROVIDER
      interface: notification-service
      autoStart: true

  # 自驱配置
  mainFirstConfig:
    selfCheck:
      - checkCapabilities: [report-remind, report-submit, report-aggregate]
      - checkDriverCapabilities: [scheduler, event-listener]
      
    selfStart:
      - initDriverCapabilities: [scheduler, event-listener, capability-invoker]
      - initCapabilities: [report-remind, report-submit, report-aggregate, report-analyze]
      - bindAddresses: auto
      
    startCollaboration:
      - startScene: scene-email-notification
      - bindInterface: notification-service
      
    selfDrive:
      scheduleRules:
        - trigger: "0 17 * * 1-5"
          action: remind-flow
        - trigger: "0 18 * * 1-5"
          action: aggregate-flow
          
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

---

### 3.2 用例二：智能家居场景能力（事件驱动型）

#### 用户故事

> **我是家庭主人**，我希望实现智能化的家庭安防和节能管理。

#### 需求分析

- 离家时自动开启安防模式
- 回家时自动关闭安防、开启舒适模式
- 夜间检测到异常时自动报警
- 根据室温自动调节空调

#### 场景能力定义

```yaml
apiVersion: capability.ooder.net/v1
kind: SceneCapability

metadata:
  id: scene-smart-home
  name: 智能家居场景能力
  type: SCENE_CAPABILITY
  mainFirst: true

spec:
  driverCapabilities:
    - id: event-listener
    - id: capability-invoker
      
  capabilities:
    - id: security-mode
      type: ATOMIC_CAPABILITY
    - id: comfort-mode
      type: ATOMIC_CAPABILITY
    - id: alarm-trigger
      type: ATOMIC_CAPABILITY
    - id: climate-control
      type: ATOMIC_CAPABILITY

  mainFirstConfig:
    selfDrive:
      eventRules:
        - event: location.away
          condition: "user.location == 'away'"
          action: enable-security-flow
          
        - event: location.home
          condition: "user.location == 'home'"
          action: enable-comfort-flow
          
        - event: motion.detected
          condition: "time.hour >= 22 AND time.hour <= 6"
          action: alarm-flow
          
        - event: temperature.changed
          condition: "temperature > 28 OR temperature < 18"
          action: climate-flow
```

---

### 3.3 用例三：知识问答场景能力（协作型）

#### 用户故事

> **我是知识管理员**，我希望构建一个智能问答系统，支持文档管理和语义检索。

#### 场景能力定义

```yaml
apiVersion: capability.ooder.net/v1
kind: SceneCapability

metadata:
  id: scene-knowledge-qa
  name: 知识问答场景能力
  type: SCENE_CAPABILITY
  mainFirst: true

spec:
  capabilities:
    - id: kb-management
      type: ATOMIC_CAPABILITY
    - id: kb-search
      type: ATOMIC_CAPABILITY
    - id: rag-retrieval
      type: COMPOSITE_CAPABILITY

  collaborativeCapabilities:
    - capabilityId: scene-indexing
      role: PROVIDER
      interface: indexing-service
      autoStart: true
      
    - capabilityId: scene-llm-assistant
      role: PROVIDER
      interface: llm-service
      autoStart: false

  mainFirstConfig:
    selfCheck:
      - checkCapabilities: [kb-management, kb-search]
      - checkCollaborative: [scene-indexing]
      
    startCollaboration:
      - startScene: scene-indexing
      - bindInterface: indexing-service
```

---

## 四、场景类型对比

| 类型 | 驱动方式 | 典型场景 | 关键驱动能力 |
|------|----------|----------|--------------|
| 流程驱动型 | scheduler | 日志汇报、定时任务 | scheduler, capability-invoker |
| 事件驱动型 | event-listener | 智能家居、监控告警 | event-listener, capability-invoker |
| 协作型 | collaboration-coordinator | 知识问答、多Agent协作 | collaboration-coordinator |
| 混合型 | 多驱动能力 | 复杂业务场景 | scheduler + event-listener + collaboration-coordinator |

---

## 五、实体模型设计

### 5.1 场景能力模型

```java
public class SceneCapability implements Capability {
    private String capabilityId;
    private String name;
    private CapabilityType type = CapabilityType.SCENE;
    private boolean mainFirst;
    
    // 子能力
    private List<String> capabilities;
    
    // 协作能力
    private List<CollaborativeCapabilityRef> collaborativeCapabilities;
    
    // 自驱配置
    private MainFirstConfig mainFirstConfig;
    
    // 角色定义
    private List<RoleDefinition> roles;
}

public class MainFirstConfig {
    private List<CheckStep> selfCheck;
    private List<StartStep> selfStart;
    private DriveConfig selfDrive;
    private List<CollaborationStep> startCollaboration;
}

public class DriveConfig {
    private List<ScheduleRule> scheduleRules;
    private List<EventRule> eventRules;
    private Map<String, CapabilityChain> capabilityChains;
}
```

### 5.2 场景组模型

```java
public class SceneGroup {
    private String groupId;
    private String sceneCapabilityId;
    private SceneGroupStatus status;
    private String primaryAgentId;
    private List<Participant> participants;
    private List<CapabilityBinding> capabilityBindings;
    private SceneGroupKey sceneGroupKey;
}
```

### 5.3 场景组密钥模型

```java
public class SceneGroupKey {
    private String groupId;
    private String sceneName;
    private int version;
    private String masterKey;          // 加密存储
    private String accessKey;
    private Permissions permissions;
    private List<MemberKeyShare> members;
    private OfflineConfig offlineConfig;
}
```

---

## 六、场景能力生命周期管理

### 6.1 生命周期阶段

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        场景能力生命周期                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   Phase 1: 安装阶段                                                         │
│   ─────────────────                                                          │
│   Skill安装 → 发现mainFirst → 注册场景能力                                  │
│                                                                             │
│   Phase 2: 自启阶段                                                         │
│   ─────────────────                                                          │
│   selfCheck() → selfStart() → startCollaboration()                         │
│                                                                             │
│   Phase 3: 自驱阶段                                                         │
│   ─────────────────                                                          │
│   selfDrive() → 监听驱动事件 → 执行能力调用链                               │
│                                                                             │
│   Phase 4: 场景组阶段                                                       │
│   ─────────────────                                                          │
│   创建SceneGroup → 成员加入 → 任务协作 → 状态同步                           │
│                                                                             │
│   Phase 5: 故障/恢复                                                        │
│   ─────────────────                                                          │
│   故障检测 → 主备切换 → 自愈恢复                                            │
│                                                                             │
│   Phase 6: 停止/销毁                                                        │
│   ─────────────────                                                          │
│   selfStop() → 成员离开 → 销毁SceneGroup                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 状态流转

```
场景能力状态：
INSTALLED → CHECKING → STARTING → RUNNING → STOPPING → STOPPED
                ↓           ↓
             CHECK_FAILED  START_FAILED

场景组状态：
CREATING → ACTIVE → PAUSED → DESTROYED
              ↓
           FAILOVER
```

---

## 七、能力调用链规范

### 7.1 调用链定义

```yaml
capabilityChains:
  chain-name:
    - capability: capability-id
      input:
        key: value
      condition: "expression"      # 可选
      on-error: continue | stop    # 可选
      
    - capability: another-capability
      input:
        data: "${previous.result}"  # 引用上一步结果
```

### 7.2 调用链执行

```java
public interface CapabilityChainService {
    CompletableFuture<ChainResult> executeChain(
        String chainId, 
        Map<String, Object> input
    );
    
    CompletableFuture<ChainResult> executeChainAsync(
        String chainId, 
        Map<String, Object> input, 
        ChainCallback callback
    );
}
```

---

## 八、南向协议集成

### 8.1 协议映射

| 场景能力概念 | 南向协议概念 | 映射关系 |
|-------------|-------------|----------|
| SceneCapability | SceneGroupInfo | 场景能力 = 场景组 |
| mainFirst | PRIMARY Agent | 自驱入口 = 主节点 |
| DRIVER_CAPABILITY | CollaborationProtocol | 驱动能力 = 协作协议 |
| collaborativeCapabilities | BACKUP Agent | 协作能力 = 备节点 |

### 8.2 协作协议接口

```java
public interface CollaborationProtocol {
    CompletableFuture<Void> joinSceneGroup(String groupId, JoinRequest request);
    CompletableFuture<TaskInfo> receiveTask(String groupId);
    CompletableFuture<Void> submitTaskResult(String groupId, String taskId, TaskResult result);
    CompletableFuture<Void> syncState(String groupId, SceneGroupState state);
}
```

---

## 九、API接口规范

### 9.1 场景能力API

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/v1/scene-capabilities` | GET | 列出场景能力 |
| `/api/v1/scene-capabilities/{id}` | GET | 获取场景能力详情 |
| `/api/v1/scene-capabilities/{id}/start` | POST | 启动场景能力（触发mainFirst） |
| `/api/v1/scene-capabilities/{id}/stop` | POST | 停止场景能力 |
| `/api/v1/scene-capabilities/{id}/status` | GET | 获取场景能力状态 |

### 9.2 场景组API

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/v1/scene-groups` | GET | 列出场景组 |
| `/api/v1/scene-groups/{groupId}` | GET | 获取场景组详情 |
| `/api/v1/scene-groups/{groupId}/join` | POST | 加入场景组 |
| `/api/v1/scene-groups/{groupId}/leave` | POST | 离开场景组 |
| `/api/v1/scene-groups/{groupId}/tasks` | GET | 获取任务列表 |

---

## 十、开发指南

### 10.1 开发流程

1. **定义场景能力**：创建 SceneCapability YAML 配置
2. **实现子能力**：开发 ATOMIC_CAPABILITY 或 COMPOSITE_CAPABILITY
3. **配置驱动能力**：定义 scheduler、event-listener 规则
4. **定义能力调用链**：配置 capabilityChains
5. **测试验证**：验证 mainFirst 流程

### 10.2 验收清单

#### 10.2.1 功能验收

- [ ] 场景能力自检通过
- [ ] 场景能力自启成功
- [ ] 子能力绑定成功
- [ ] 协作能力启动成功（如有）
- [ ] 场景能力自驱运行正常
- [ ] 能力调用链执行正确

#### 10.2.2 接口验收

```bash
# 1. 启动场景能力
curl -X POST http://localhost:8084/api/v1/scene-capabilities/scene-daily-report/start

# 2. 查看状态
curl http://localhost:8084/api/v1/scene-capabilities/scene-daily-report/status

# 3. 查看场景组
curl http://localhost:8084/api/v1/scene-groups/{groupId}
```

---

## 十一、版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-02-27 | 初始版本，基础场景定义 |
| v2.0 | 2026-02-28 | 增加四个用户用例，完善实体模型，增加SuperAgent支持 |
| v2.1 | 2026-03-01 | 术语统一：SceneTemplate→SceneDefinition，引用统一术语表 |
| v2.2 | 2026-03-02 | 新增核心问题讨论与结论章节 |
| v2.3 | 2026-03-02 | **重大升级**：采用能力驱动架构，SceneDefinition→SceneCapability，新增mainFirst自驱机制，整合南向协议术语 |

---

**文档维护者**: ooder开发团队  
**最后更新**: 2026-03-02
