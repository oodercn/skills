# 场景模板与南向协议深度分析

## 文档信息

| 项目 | 说明 |
|------|------|
| 版本 | v1.0 |
| 日期 | 2026-03-02 |
| 状态 | 分析报告 |

---

## 一、核心概念定义

### 1.1 SDK层定义

#### SkillManifest (Skill清单)

```java
public class SkillManifest {
    private String skillId;                          // Skill唯一标识
    private String sceneId;                          // 关联场景ID
    private List<Capability> capabilities;           // 提供的能力列表
    private List<Dependency> dependencies;           // Skill层技术依赖
    private List<String> collaborativeScenes;        // 协作场景列表
    private List<SceneDependency> collaborativeSceneDependencies;  // 协作场景依赖
    private SceneConfig primaryScene;                // 主场景配置
    private List<String> providedInterfaces;         // 提供的接口
    private List<String> requiredInterfaces;         // 需要的接口
}
```

**关键属性解析**：

| 属性 | 类型 | 用途 | 层级 |
|------|------|------|------|
| dependencies | List<Dependency> | Skill技术依赖 | Skill层 |
| collaborativeScenes | List<String> | 协作场景名称列表 | 场景层 |
| collaborativeSceneDependencies | List<SceneDependency> | 协作场景依赖详情 | 场景层 |
| primaryScene | SceneConfig | 主场景配置 | 场景层 |
| requiredInterfaces | List<String> | 需要的接口 | 接口层 |

#### SceneTemplate (场景模板)

```java
public class SceneTemplate {
    private String templateId;                       // 模板唯一标识
    private List<SkillRef> skills;                   // Skill引用列表
    private List<CapabilityBinding> capabilityBindings;  // 能力绑定配置
    private List<CollaborativeSceneRef> collaborativeScenes;  // 协作场景引用
    private SceneConfig sceneConfig;                 // 场景配置
}

public static class SkillRef {
    private String skillId;
    private String version;
    private boolean required;
    private Map<String, Object> config;
}

public static class CollaborativeSceneRef {
    private String sceneId;
    private String relation;
    private boolean bidirectional;
}
```

#### SceneDependencyResolver (场景依赖解析器)

```java
public interface SceneDependencyResolver {
    // 从Skill清单解析依赖
    List<SceneDependency> resolve(SkillManifest manifest);
    
    // 从场景模板解析依赖
    List<SceneDependency> resolveFromTemplate(SceneTemplate template);
    
    // 获取安装顺序（拓扑排序）
    List<String> getInstallOrder(SceneTemplate template);
    
    // 检查所有依赖状态
    Map<String, DependencyStatus> checkAllDependencies(SceneTemplate template);
}
```

#### SceneDependency (场景依赖)

```java
public interface SceneDependency {
    String getSceneName();
    String getDescription();
    boolean isRequired();
    List<String> getRequiredCapabilities();
    Map<String, Object> getConfig();
    DependencyStatus getStatus();
    
    enum DependencyStatus {
        PENDING, RESOLVING, RESOLVED, FAILED
    }
}
```

---

### 1.2 南向协议层定义

#### SceneGroupInfo (场景组信息)

```java
public class SceneGroupInfo {
    private String groupId;              // 场景组唯一标识
    private String groupName;            // 场景组名称
    private String sceneId;              // 关联场景ID
    private String primaryId;            // 主节点ID
    private List<String> memberIds;      // 成员ID列表
    private int memberCount;             // 成员数量
    private String status;               // 状态
    private long createdAt;              // 创建时间
}
```

#### SceneGroupKey (场景组密钥)

```yaml
groupId: string                    # 场景组唯一标识
sceneName: string                  # 场景名称
version: integer                   # KEY版本

masterKey: string                  # 主密钥（加密存储）
accessKey: string                  # 访问密钥

permissions:                       # 权限定义
  vfs:                             # VFS权限
    "path": ["read", "write", "delete"]
  capabilities:                    # 能力权限
    "capabilityId": true

members:                           # 成员列表
  - agentId: string                # Agent标识
    role: PRIMARY | BACKUP         # 角色
    publicKey: string              # 公钥
    keyShare: string               # 密钥分片

offlineConfig:                     # 离线配置
  enabled: true
  syncInterval: 60000
  conflictResolution: last-write
```

#### CollaborationProtocol (协作协议)

```java
public interface CollaborationProtocol {
    // 场景组管理
    CompletableFuture<SceneGroupInfo> joinSceneGroup(String groupId, JoinRequest request);
    CompletableFuture<Void> leaveSceneGroup(String groupId);
    
    // 任务协作
    CompletableFuture<TaskInfo> receiveTask(String groupId);
    CompletableFuture<Void> submitTaskResult(String groupId, String taskId, TaskResult result);
    
    // 状态同步
    CompletableFuture<Void> syncState(String groupId, SceneGroupState state);
    CompletableFuture<SceneGroupState> getState(String groupId);
    
    // 成员管理
    CompletableFuture<List<MemberInfo>> getGroupMembers(String groupId);
}
```

#### 成员角色

| 角色 | 职责 | 状态 | 数量 |
|------|------|------|------|
| PRIMARY | 消息路由、KEY管理、链路维护、任务分配 | ACTIVE | 1个 |
| BACKUP | 状态监听、链路同步、准备接管、任务执行 | STANDBY | 0-N个 |

---

## 二、两种场景模式对比

### 2.1 场景模板模式 (SceneTemplate Mode)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        场景模板模式（静态定义）                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   SceneTemplate (YAML)                                                      │
│        │                                                                    │
│        ├── skills: [skill-knowledge-base, skill-knowledge-ui, skill-rag]   │
│        │                                                                    │
│        ├── capabilities: [kb-management, kb-search, rag-retrieval]         │
│        │                                                                    │
│        └── sceneConfig: { type: knowledge, ... }                           │
│                                                                             │
│   部署流程:                                                                  │
│   ─────────                                                                 │
│   1. 读取模板 → 解析 skills 列表                                            │
│   2. 检查依赖 → SceneDependencyResolver.resolveFromTemplate()              │
│   3. 安装Skills → SkillPackageManager.installWithDependencies()            │
│   4. 创建场景 → SceneService.create()                                       │
│   5. 绑定能力 → CapabilityBindingService.bind()                            │
│                                                                             │
│   特点:                                                                      │
│   ├── 静态配置，预先定义                                                    │
│   ├── 用户驱动，手动部署                                                    │
│   ├── 依赖安装模式                                                          │
│   └── 适用于业务场景快速部署                                                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 场景组模式 (SceneGroup Mode)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        场景组模式（运行时协作）                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   SceneGroup (运行时)                                                       │
│        │                                                                    │
│        ├── SceneGroupKey (共享密钥)                                         │
│        │    ├── masterKey, accessKey                                       │
│        │    └── permissions: { vfs, capabilities }                         │
│        │                                                                    │
│        ├── Members: [PRIMARY, BACKUP*]                                      │
│        │    ├── PRIMARY: 消息路由、任务分配                                 │
│        │    └── BACKUP: 状态监听、任务执行                                  │
│        │                                                                    │
│        └── CollaborationProtocol                                            │
│             ├── receiveTask() / submitTaskResult()                         │
│             └── syncState() / getState()                                   │
│                                                                             │
│   创建流程:                                                                  │
│   ─────────                                                                 │
│   1. 场景激活 → 检查 collaborativeScenes                                    │
│   2. 创建场景组 → SceneGroupInfo                                            │
│   3. 生成密钥 → SceneGroupKey                                               │
│   4. 成员加入 → CollaborationProtocol.joinSceneGroup()                     │
│   5. 建立通信 → PRIMARY/BACKUP 心跳                                         │
│                                                                             │
│   特点:                                                                      │
│   ├── 动态创建，运行时建立                                                  │
│   ├── Agent驱动，自动协作                                                  │
│   ├── 协作模式                                                              │
│   └── 适用于Agent间协同工作                                                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 核心差异对比

| 维度 | 场景模板模式 | 场景组模式 |
|------|-------------|-----------|
| **定义位置** | YAML静态配置 | 运行时动态创建 |
| **创建时机** | 用户部署时 | 场景激活时 |
| **主要用途** | Skill依赖安装 | Agent协作通信 |
| **依赖处理** | 拓扑排序安装 | 密钥分片共享 |
| **成员关系** | Skill间依赖 | Agent间协作 |
| **资源管理** | 能力绑定 | KEY/VFS共享 |
| **故障处理** | 安装失败回滚 | PRIMARY/BACKUP切换 |
| **适用场景** | 业务场景部署 | Agent高可用 |

---

## 三、应用场景定位分析

### 3.1 场景定义 vs 场景组

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        场景实体层次结构                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   第一层：场景定义 (SceneDefinition)                                         │
│   ─────────────────────────────────                                         │
│   ├── 静态模板，定义能力需求和角色                                           │
│   ├── 来源：SceneTemplate 部署                                              │
│   └── 状态：DRAFT → ACTIVE → DEPRECATED                                     │
│                                                                             │
│   第二层：场景组 (SceneGroup)                                                │
│   ─────────────────────────────────                                         │
│   ├── 运行时实体，绑定参与者和能力实例                                       │
│   ├── 创建时机：场景激活时（如有协作场景）                                   │
│   └── 状态：CREATING → ACTIVE → DESTROYED                                   │
│                                                                             │
│   第三层：场景组成员 (SceneGroupMember)                                      │
│   ─────────────────────────────────                                         │
│   ├── Agent实例，执行具体任务                                               │
│   ├── 角色：PRIMARY / BACKUP                                                │
│   └── 通信：CollaborationProtocol                                           │
│                                                                             │
│   关系：                                                                     │
│   SceneTemplate ──部署──► SceneDefinition ──激活──► SceneGroup             │
│                              │                        │                     │
│                              │                        ├── Member (PRIMARY)  │
│                              │                        └── Member (BACKUP)   │
│                              │                                              │
│                              └── 无协作场景 ──► 单场景运行                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 依赖配置位置分析

根据源码分析，依赖配置分为两层：

#### Skill层（技术依赖）

```yaml
# skill-manifest.yaml
spec:
  dependencies:
    skills:
      - skillId: skill-knowledge-base
        versionRange: ">=1.0.0"
        required: true
        
  collaborativeScenes:
    - knowledge-indexing
    - knowledge-retrieval
    
  collaborativeSceneDependencies:
    - sceneName: knowledge-indexing
      required: true
      requiredCapabilities: [indexing-service]
```

#### 场景模板层（业务组合）

```yaml
# template.yaml
spec:
  skills:
    - id: skill-knowledge-base
      version: ">=1.0.0"
      required: true
      
    - id: skill-knowledge-ui
      version: ">=1.0.0"
      required: true
      
  collaborativeScenes:
    - sceneId: knowledge-indexing
      relation: provides
      bidirectional: true
```

### 3.3 两种模式的协作关系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        场景模板与场景组协作关系                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   用户部署场景模板                                                           │
│        │                                                                    │
│        ▼                                                                    │
│   ┌─────────────────┐                                                       │
│   │ SceneTemplate   │                                                       │
│   │ (YAML配置)      │                                                       │
│   └────────┬────────┘                                                       │
│            │                                                                │
│            ▼                                                                │
│   ┌─────────────────┐     ┌─────────────────────────────────┐              │
│   │ 解析依赖        │────►│ SceneDependencyResolver         │              │
│   │                 │     │ resolveFromTemplate(template)   │              │
│   └────────┬────────┘     └─────────────────────────────────┘              │
│            │                                                                │
│            ▼                                                                │
│   ┌─────────────────┐     ┌─────────────────────────────────┐              │
│   │ 安装Skills      │────►│ SkillPackageManager             │              │
│   │                 │     │ installWithDependencies()       │              │
│   └────────┬────────┘     └─────────────────────────────────┘              │
│            │                                                                │
│            ▼                                                                │
│   ┌─────────────────┐                                                       │
│   │ 创建场景定义     │                                                       │
│   │ SceneDefinition │                                                       │
│   └────────┬────────┘                                                       │
│            │                                                                │
│            ▼                                                                │
│   ┌─────────────────┐                                                       │
│   │ 用户激活场景     │                                                       │
│   └────────┬────────┘                                                       │
│            │                                                                │
│            ├── 无协作场景 ──► 单场景运行                                     │
│            │                                                                │
│            └── 有协作场景                                                    │
│                 │                                                           │
│                 ▼                                                           │
│   ┌─────────────────────────────────────────────────────────────┐          │
│   │                    创建场景组                                │          │
│   │  ┌─────────────────┐                                        │          │
│   │  │ SceneGroupInfo  │ ◄── CollaborationProtocol              │          │
│   │  └────────┬────────┘                                        │          │
│   │           │                                                 │          │
│   │           ├── 生成 SceneGroupKey                            │          │
│   │           │                                                 │          │
│   │           ├── PRIMARY Agent 加入                            │          │
│   │           │                                                 │          │
│   │           └── BACKUP Agent 加入 (可选)                      │          │
│   │                                                             │          │
│   │  运行时协作:                                                 │          │
│   │  ───────────                                                │          │
│   │  ├── 任务分配: receiveTask() / submitTaskResult()           │          │
│   │  ├── 状态同步: syncState() / getState()                     │          │
│   │  ├── 故障切换: PRIMARY ↔ BACKUP                             │          │
│   │  └── 离线支持: OfflineService                               │          │
│   └─────────────────────────────────────────────────────────────┘          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、闭环实现方案

### 4.1 零配置安装闭环

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        零配置安装完整闭环                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   输入: SceneTemplate (YAML)                                                │
│                                                                             │
│   Phase 1: 依赖解析                                                         │
│   ────────────────────                                                      │
│   SceneDependencyResolver.resolveFromTemplate(template)                     │
│   ├── 解析 skills 列表                                                      │
│   ├── 解析每个 Skill 的 dependencies                                        │
│   ├── 解析 collaborativeSceneDependencies                                   │
│   └── 构建依赖图                                                            │
│                                                                             │
│   Phase 2: 安装顺序计算                                                     │
│   ────────────────────                                                      │
│   SceneDependencyResolver.getInstallOrder(template)                         │
│   ├── 拓扑排序                                                              │
│   ├── 检测循环依赖                                                          │
│   └── 返回安装顺序列表                                                      │
│                                                                             │
│   Phase 3: Skill安装                                                        │
│   ────────────────────                                                      │
│   SkillPackageManager.installWithDependencies(skillId, mode)                │
│   ├── 按顺序安装                                                            │
│   ├── 递归处理子依赖                                                        │
│   ├── 版本兼容性检查                                                        │
│   └── 安装结果汇总                                                          │
│                                                                             │
│   Phase 4: 场景创建                                                         │
│   ────────────────────                                                      │
│   SceneService.create(SceneDefinitionDTO)                                   │
│   ├── 创建 SceneDefinition                                                  │
│   ├── 绑定能力                                                              │
│   └── 配置参数                                                              │
│                                                                             │
│   Phase 5: 场景激活                                                         │
│   ────────────────────                                                      │
│   SceneService.activate(sceneId)                                            │
│   ├── 检查 collaborativeScenes                                              │
│   ├── 有协作场景 → 创建 SceneGroup                                          │
│   │    ├── CollaborationProtocol.joinSceneGroup()                          │
│   │    ├── 生成 SceneGroupKey                                               │
│   │    └── 建立 PRIMARY/BACKUP 关系                                         │
│   └── 无协作场景 → 单场景运行                                               │
│                                                                             │
│   输出: 激活的场景 (可运行)                                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 场景组协作闭环

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        场景组协作完整闭环                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   输入: 激活的场景 (含 collaborativeScenes)                                  │
│                                                                             │
│   Phase 1: 场景组创建                                                       │
│   ────────────────────                                                      │
│   SceneManager.createSceneGroup(sceneId)                                    │
│   ├── 创建 SceneGroupInfo                                                   │
│   ├── 生成 SceneGroupKey                                                    │
│   │    ├── masterKey (Shamir分片)                                          │
│   │    ├── accessKey                                                        │
│   │    └── permissions                                                      │
│   └── 设置 PRIMARY Agent                                                    │
│                                                                             │
│   Phase 2: 成员加入                                                         │
│   ────────────────────                                                      │
│   CollaborationProtocol.joinSceneGroup(groupId, request)                    │
│   ├── 验证权限                                                              │
│   ├── 分配角色 (BACKUP)                                                     │
│   ├── 分发 keyShare                                                         │
│   └── 同步状态                                                              │
│                                                                             │
│   Phase 3: 任务协作                                                         │
│   ────────────────────                                                      │
│   PRIMARY:                          BACKUP:                                 │
│   ├── 分配任务                       ├── 接收任务                           │
│   │  receiveTask()                  │  receiveTask()                        │
│   ├── 监控执行                       ├── 执行任务                           │
│   └── 验证结果                       └── 提交结果                           │
│      submitTaskResult()               submitTaskResult()                    │
│                                                                             │
│   Phase 4: 状态同步                                                         │
│   ────────────────────                                                      │
│   CollaborationProtocol.syncState(groupId, state)                           │
│   ├── 全量同步 (成员加入时)                                                 │
│   ├── 增量同步 (状态变更时)                                                 │
│   ├── 心跳同步 (定时)                                                       │
│   └── 离线同步 (网络恢复时)                                                 │
│                                                                             │
│   Phase 5: 故障切换                                                         │
│   ────────────────────                                                      │
│   故障检测:                                                                  │
│   ├── PRIMARY 心跳超时 (15秒)                                               │
│   ├── BACKUP 向 MCP Agent 确认                                              │
│   └── 收到 FAILOVER_APPROVE                                                 │
│                                                                             │
│   故障切换:                                                                  │
│   ├── 恢复 masterKey (Shamir算法)                                          │
│   ├── 继承 SceneGroupKey                                                    │
│   ├── 更新角色为 PRIMARY                                                    │
│   ├── 恢复链路表                                                            │
│   └── 广播 ROUTE_UPDATE                                                     │
│                                                                             │
│   输出: 高可用场景组                                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.3 关键接口映射

| 功能 | SDK层接口 | 南向协议接口 |
|------|----------|-------------|
| 场景模板解析 | SceneDependencyResolver.resolveFromTemplate() | - |
| 依赖安装 | SkillPackageManager.installWithDependencies() | - |
| 场景创建 | SceneService.create() | SceneManager.create() |
| 场景激活 | SceneService.activate() | - |
| 场景组创建 | SceneGroupService.create() | CollaborationProtocol.joinSceneGroup() |
| 成员管理 | SceneGroupService.join() | CollaborationProtocol.joinSceneGroup() |
| 任务协作 | - | CollaborationProtocol.receiveTask() |
| 状态同步 | - | CollaborationProtocol.syncState() |
| 故障切换 | - | SceneGroupKey继承机制 |

---

## 五、结论与建议

### 5.1 核心结论

1. **两种模式互补**：
   - 场景模板模式：解决Skill依赖安装问题
   - 场景组模式：解决Agent协作通信问题

2. **创建时机不同**：
   - SceneDefinition：用户部署时创建
   - SceneGroup：场景激活时创建（如有协作场景）

3. **依赖配置分层**：
   - Skill层：技术依赖（skill-manifest.yaml）
   - 模板层：业务组合（template.yaml）

4. **协作关系清晰**：
   - collaborativeScenes：定义协作场景名称
   - SceneGroup：运行时协作实体

### 5.2 实现建议

1. **增强 SceneDependencyResolver**：
   - 支持从 SceneTemplate 解析依赖
   - 实现拓扑排序计算安装顺序
   - 检测循环依赖

2. **完善场景激活流程**：
   - 激活时检查 collaborativeScenes
   - 自动创建 SceneGroup（如有协作场景）
   - 调用 CollaborationProtocol 建立协作

3. **统一配置格式**：
   - Skill层配置技术依赖
   - 模板层配置业务组合
   - 避免重复配置

4. **实现闭环验证**：
   - 安装结果验证
   - 场景激活验证
   - 协作通信验证

---

## 六、相关文档

- [场景需求规格说明书](../SCENE_REQUIREMENT_SPEC.md)
- [能力管理需求规格说明书](../CAPABILITY_REQUIREMENT_SPEC.md)
- [场景组协议 v0.7.3](../../temp/protocol-release/v0.8.0/agent/scene-group-protocol.md)
- [Agent协议 v0.7.3](../../temp/protocol-release/v0.8.0/agent/agent-protocol.md)
- [SDK协作团队任务分解](./SDK_TEAM_TASKS.md)

---

*作者: Ooder Team*  
*更新时间: 2026-03-02*
