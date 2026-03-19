# 能力驱动架构 - API覆盖度与工作量评估

## 文档信息

| 项目 | 说明 |
|------|------|
| 版本 | v1.0 |
| 日期 | 2026-03-02 |
| 状态 | 评估报告 |

---

## 一、能力驱动架构核心需求

### 1.1 关键概念映射

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力驱动架构核心需求                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【核心概念】           【需要的能力/接口】                                  │
│   ════════════           ════════════════════                               │
│                                                                             │
│   1. SceneCapability      - 能力类型扩展 (SCENE_CAPABILITY)                │
│      场景能力              - mainFirst 配置支持                             │
│                             - 能力嵌套能力                                   │
│                                                                             │
│   2. mainFirst            - selfCheck() 自检接口                           │
│      自驱入口              - selfStart() 自启接口                           │
│                             - selfDrive() 自驱接口                          │
│                             - startCollaboration() 协作启动                 │
│                                                                             │
│   3. DRIVER_CAPABILITY    - intent-receiver 意图接收                       │
│      驱动能力              - scheduler 时间驱动                             │
│                             - event-listener 事件监听                       │
│                             - capability-invoker 能力调用                   │
│                             - collaboration-coordinator 协作协调            │
│                                                                             │
│   4. 能力类型体系          - CapabilityType 枚举扩展                        │
│      ATOMIC/COMPOSITE     - 能力组合机制                                    │
│      SCENE/DRIVER         - 能力涌现机制                                    │
│                                                                             │
│   5. 能力调用链            - 能力链定义                                      │
│      capabilityChains     - 能力链执行器                                    │
│                             - 能力链编排                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、现有API覆盖度分析

### 2.1 能力管理API覆盖度

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力管理API覆盖度                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   功能需求                  现有API                    覆盖度    工作量      │
│   ═══════════════════════════════════════════════════════════════════════   │
│                                                                             │
│   【能力注册与管理】                                                         │
│   ├── 能力注册              CapabilityService.register()    ✅ 100%   无    │
│   ├── 能力注销              CapabilityService.unregister()  ✅ 100%   无    │
│   ├── 能力查询              CapabilityService.findById()    ✅ 100%   无    │
│   ├── 能力列表              CapabilityService.findAll()     ✅ 100%   无    │
│   ├── 能力搜索              CapabilityService.search()      ✅ 100%   无    │
│   └── 能力更新              CapabilityService.update()      ✅ 100%   无    │
│                                                                             │
│   【能力绑定】                                                               │
│   ├── 绑定到场景            CapabilityBindingService.bind() ✅ 100%   无    │
│   ├── 解绑                  CapabilityBindingService.unbind()✅ 100%   无   │
│   ├── 查询绑定              listBySceneGroup()              ✅ 100%   无    │
│   └── 状态更新              updateStatus()                  ✅ 100%   无    │
│                                                                             │
│   【能力发现】                                                               │
│   ├── 发现能力              CapabilityDiscoveryService      ✅ 100%   无    │
│   ├── 按类型发现            findByType()                    ✅ 100%   无    │
│   ├── 按场景发现            findBySceneType()               ✅ 100%   无    │
│   └── 能力调用              invoke()                        ✅ 100%   无    │
│                                                                             │
│   【能力监控】                                                               │
│   ├── 监控启动              CapabilityMonService.startMonitoring() ✅ 100% │
│   ├── 监控停止              stopMonitoring()                ✅ 100%   无    │
│   ├── 执行日志              getExecutionLogs()              ✅ 100%   无    │
│   └── 告警管理              getAlerts()                     ✅ 100%   无    │
│                                                                             │
│   【能力协作】                                                               │
│   ├── 编排管理              CapabilityCoopService           ✅ 100%   无    │
│   ├── 场景组管理            createSceneGroup()              ✅ 100%   无    │
│   └── 链式调用              executeChain()                  ✅ 100%   无    │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│   能力管理API覆盖度：100%                                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 能力驱动核心API覆盖度

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力驱动核心API覆盖度                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   功能需求                  现有API                    覆盖度    工作量      │
│   ═══════════════════════════════════════════════════════════════════════   │
│                                                                             │
│   【能力类型扩展】                                                           │
│   ├── SCENE_CAPABILITY      CapabilityType枚举             ❌ 0%    小     │
│   │   场景能力类型          需新增枚举值                                     │
│   ├── DRIVER_CAPABILITY     CapabilityType枚举             ❌ 0%    小     │
│   │   驱动能力类型          需新增枚举值                                     │
│   └── 能力嵌套能力          Capability模型                 ⚠️ 30%   中     │
│       capabilities字段      需扩展支持能力引用                              │
│                                                                             │
│   【mainFirst 自驱入口】                                                     │
│   ├── mainFirst 配置        SkillManifest/SceneTemplate    ❌ 0%    中     │
│   │   自驱入口标识          需新增配置字段                                   │
│   ├── selfCheck()           无                             ❌ 0%    大     │
│   │   自检接口              需新增接口和实现                                 │
│   ├── selfStart()           无                             ❌ 0%    大     │
│   │   自启接口              需新增接口和实现                                 │
│   ├── selfDrive()           无                             ❌ 0%    大     │
│   │   自驱接口              需新增接口和实现                                 │
│   └── startCollaboration()  无                             ❌ 0%    大     │
│       协作启动              需新增接口和实现                                 │
│                                                                             │
│   【DRIVER_CAPABILITY 驱动能力】                                             │
│   ├── intent-receiver       无                             ❌ 0%    大     │
│   │   意图接收              需新增能力实现                                   │
│   ├── scheduler             无                             ❌ 0%    中     │
│   │   时间驱动              需新增能力实现                                   │
│   ├── event-listener        无                             ❌ 0%    中     │
│   │   事件监听              需新增能力实现                                   │
│   ├── capability-invoker    CapabilityRouter               ⚠️ 50%   中     │
│   │   能力调用              需扩展支持链式调用                               │
│   └── collaboration-coord   CapabilityCoopService          ⚠️ 60%   小     │
│       协作协调              需扩展支持场景能力                               │
│                                                                             │
│   【能力调用链】                                                             │
│   ├── 能力链定义            无                             ❌ 0%    中     │
│   │   capabilityChains      需新增配置格式                                   │
│   ├── 能力链执行器          CapabilityCoopService          ⚠️ 40%   中     │
│   │   executeChain()        需扩展支持条件分支                               │
│   └── 能力链编排            无                             ❌ 0%    中     │
│       可视化编排            需新增编排服务                                   │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│   能力驱动核心API覆盖度：约 25%                                              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 Skill安装与驱动API覆盖度

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Skill安装与驱动API覆盖度                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   功能需求                  现有API                    覆盖度    工作量      │
│   ═══════════════════════════════════════════════════════════════════════   │
│                                                                             │
│   【Skill安装】                                                              │
│   ├── 安装Skill             SkillPackageManager.installSkill() ✅ 100% 无   │
│   ├── 卸载Skill             uninstallSkill()                ✅ 100%   无    │
│   ├── 更新Skill             updateSkill()                   ✅ 100%   无    │
│   ├── 已安装列表            getInstalledSkills()            ✅ 100%   无    │
│   └── 发现Skill             discoverSkills()                ✅ 100%   无    │
│                                                                             │
│   【依赖管理】                                                               │
│   ├── 依赖解析              无显式接口                      ⚠️ 40%   中     │
│   │   dependencies解析      需增强解析逻辑                                   │
│   ├── 安装顺序计算          无                              ❌ 0%    中     │
│   │   拓扑排序              需新增算法实现                                   │
│   └── 循环依赖检测          无                              ❌ 0%    小     │
│       依赖图检测            需新增检测逻辑                                   │
│                                                                             │
│   【Skill运行时】                                                            │
│   ├── 启动Skill             startSkill()                    ✅ 100%   无    │
│   ├── 停止Skill             stopSkill()                     ✅ 100%   无    │
│   ├── 状态查询              getSkillStatus()                ✅ 100%   无    │
│   └── 连接测试              testConnection()                ✅ 100%   无    │
│                                                                             │
│   【场景关联】                                                               │
│   ├── 场景请求              requestScene()                  ✅ 100%   无    │
│   ├── 场景加入              joinSceneGroup()                ✅ 100%   无    │
│   └── 场景信息              getSceneInfo()                  ✅ 100%   无    │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│   Skill安装与驱动API覆盖度：约 70%                                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、API差距详细分析

### 3.1 完全缺失的API（需新建）

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        完全缺失的API（需新建）                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【一、mainFirst 自驱接口】                                                 │
│   ══════════════════════                                                     │
│                                                                             │
│   1. MainFirstService (新建)                                                │
│   ```java                                                                   │
│   public interface MainFirstService {                                       │
│       // 自检                                                               │
│       CompletableFuture<SelfCheckResult> selfCheck(String capabilityId);   │
│                                                                             │
│       // 自启                                                               │
│       CompletableFuture<SelfStartResult> selfStart(String capabilityId);   │
│                                                                             │
│       // 自驱运行                                                           │
│       CompletableFuture<Void> selfDrive(String capabilityId);              │
│                                                                             │
│       // 启动协作                                                           │
│       CompletableFuture<CollaborationResult> startCollaboration(           │
│           String capabilityId, CollaborativeConfig config);                │
│                                                                             │
│       // 监听器                                                             │
│       void addMainFirstListener(MainFirstListener listener);               │
│       void removeMainFirstListener(MainFirstListener listener);            │
│   }                                                                         │
│   ```                                                                       │
│                                                                             │
│   工作量：5人天                                                             │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【二、驱动能力接口】                                                       │
│   ═══════════════════                                                       │
│                                                                             │
│   2. IntentReceiver (新建) - 意图接收能力                                   │
│   ```java                                                                   │
│   public interface IntentReceiver {                                         │
│       CompletableFuture<IntentResult> receive(Intent intent);              │
│       CompletableFuture<IntentResult> parse(String naturalLanguage);       │
│       CompletableFuture<SceneCapability> resolveCapability(Intent intent); │
│   }                                                                         │
│   ```                                                                       │
│   工作量：3人天                                                             │
│                                                                             │
│   3. SchedulerCapability (新建) - 时间驱动能力                              │
│   ```java                                                                   │
│   public interface SchedulerCapability {                                    │
│       CompletableFuture<Void> schedule(String cron, String action);        │
│       CompletableFuture<Void> cancel(String scheduleId);                   │
│       CompletableFuture<List<ScheduleInfo>> listSchedules();               │
│       void addScheduleListener(ScheduleListener listener);                 │
│   }                                                                         │
│   ```                                                                       │
│   工作量：3人天                                                             │
│                                                                             │
│   4. EventListenerCapability (新建) - 事件监听能力                          │
│   ```java                                                                   │
│   public interface EventListenerCapability {                                │
│       CompletableFuture<Void> subscribe(String eventType, EventFilter filter);│
│       CompletableFuture<Void> unsubscribe(String subscriptionId);          │
│       CompletableFuture<List<Subscription>> listSubscriptions();           │
│       void addEventListener(EventListener listener);                        │
│   }                                                                         │
│   ```                                                                       │
│   工作量：3人天                                                             │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【三、能力调用链接口】                                                     │
│   ══════════════════════                                                     │
│                                                                             │
│   5. CapabilityChainService (新建)                                          │
│   ```java                                                                   │
│   public interface CapabilityChainService {                                 │
│       // 链定义                                                             │
│       CompletableFuture<ChainDefinition> createChain(ChainConfig config);  │
│       CompletableFuture<Void> deleteChain(String chainId);                 │
│       CompletableFuture<ChainDefinition> getChain(String chainId);         │
│       CompletableFuture<List<ChainDefinition>> listChains();               │
│                                                                             │
│       // 链执行                                                             │
│       CompletableFuture<ChainResult> executeChain(                         │
│           String chainId, Map<String, Object> input);                      │
│       CompletableFuture<ChainResult> executeChainAsync(                    │
│           String chainId, Map<String, Object> input, ChainCallback callback);│
│                                                                             │
│       // 链编排                                                             │
│       CompletableFuture<ChainDefinition> compose(                          │
│           List<String> capabilityIds, ComposeMode mode);                   │
│   }                                                                         │
│   ```                                                                       │
│   工作量：5人天                                                             │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【四、依赖解析接口】                                                       │
│   ═══════════════════                                                       │
│                                                                             │
│   6. DependencyResolverService (新建)                                       │
│   ```java                                                                   │
│   public interface DependencyResolverService {                              │
│       // 解析依赖                                                           │
│       CompletableFuture<List<Dependency>> resolve(String skillId);         │
│       CompletableFuture<List<Dependency>> resolveFromTemplate(             │
│           String templateId);                                               │
│                                                                             │
│       // 安装顺序                                                           │
│       CompletableFuture<List<String>> getInstallOrder(String templateId);  │
│                                                                             │
│       // 检测                                                               │
│       CompletableFuture<CycleDetectionResult> detectCycle(String skillId); │
│       CompletableFuture<CompatibilityResult> checkCompatibility(           │
│           String skillId, String version);                                  │
│   }                                                                         │
│   ```                                                                       │
│   工作量：4人天                                                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 需要扩展的API

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        需要扩展的API                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【一、CapabilityType 枚举扩展】                                            │
│   ════════════════════════════                                               │
│                                                                             │
│   现有：                                                                     │
│   ```java                                                                   │
│   public enum CapabilityType {                                              │
│       SERVICE, AI, TOOL, CONNECTOR, DATA                                    │
│   }                                                                         │
│   ```                                                                       │
│                                                                             │
│   扩展后：                                                                   │
│   ```java                                                                   │
│   public enum CapabilityType {                                              │
│       SERVICE, AI, TOOL, CONNECTOR, DATA,                                   │
│       ATOMIC,           // 原子能力                                         │
│       COMPOSITE,        // 组合能力                                         │
│       SCENE,            // 场景能力                                         │
│       DRIVER,           // 驱动能力                                         │
│       COLLABORATIVE     // 协作能力                                         │
│   }                                                                         │
│   ```                                                                       │
│   工作量：0.5人天                                                           │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【二、Capability 模型扩展】                                                │
│   ══════════════════════════                                                 │
│                                                                             │
│   现有：                                                                     │
│   ```java                                                                   │
│   public class Capability {                                                 │
│       private String capId;                                                 │
│       private String name;                                                  │
│       private CapabilityType type;                                          │
│       // ...                                                                │
│   }                                                                         │
│   ```                                                                       │
│                                                                             │
│   扩展后：                                                                   │
│   ```java                                                                   │
│   public class Capability {                                                 │
│       private String capId;                                                 │
│       private String name;                                                  │
│       private CapabilityType type;                                          │
│                                                                             │
│       // 新增：能力嵌套                                                     │
│       private List<String> capabilities;        // 子能力ID列表            │
│       private boolean mainFirst;                // 自驱入口标识             │
│       private MainFirstConfig mainFirstConfig;  // 自驱配置                 │
│                                                                             │
│       // 新增：协作能力                                                     │
│       private List<CollaborativeCapabilityRef> collaborativeCapabilities;   │
│                                                                             │
│       // 新增：能力链                                                       │
│       private Map<String, CapabilityChain> capabilityChains;                │
│   }                                                                         │
│   ```                                                                       │
│   工作量：2人天                                                             │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【三、SkillManifest 扩展】                                                 │
│   ══════════════════════════                                                 │
│                                                                             │
│   现有：                                                                     │
│   ```java                                                                   │
│   public class SkillManifest {                                              │
│       private String sceneId;                                               │
│       private SceneConfig primaryScene;                                     │
│       private List<String> collaborativeScenes;                             │
│   }                                                                         │
│   ```                                                                       │
│                                                                             │
│   扩展后：                                                                   │
│   ```java                                                                   │
│   public class SkillManifest {                                              │
│       private String sceneId;                                               │
│       private SceneConfig primaryScene;                                     │
│       private List<String> collaborativeScenes;                             │
│                                                                             │
│       // 新增：场景能力定义                                                 │
│       private List<SceneCapabilityDef> sceneCapabilities;                   │
│   }                                                                         │
│                                                                             │
│   public class SceneCapabilityDef {                                         │
│       private String capabilityId;                                          │
│       private boolean mainFirst;                                            │
│       private MainFirstConfig mainFirstConfig;                              │
│       private List<String> capabilities;                                    │
│       private List<CollaborativeCapabilityRef> collaborativeCapabilities;   │
│   }                                                                         │
│   ```                                                                       │
│   工作量：2人天                                                             │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【四、CapabilityRouter 扩展】                                              │
│   ════════════════════════════                                               │
│                                                                             │
│   现有：                                                                     │
│   ```java                                                                   │
│   public interface CapabilityRouter<P, D> {                                 │
│       CompletableFuture<RouteResult<D>> route(String capabilityId, ...);   │
│   }                                                                         │
│   ```                                                                       │
│                                                                             │
│   扩展后：                                                                   │
│   ```java                                                                   │
│   public interface CapabilityRouter<P, D> {                                 │
│       // 原有方法                                                           │
│       CompletableFuture<RouteResult<D>> route(String capabilityId, ...);   │
│                                                                             │
│       // 新增：链式调用                                                     │
│       CompletableFuture<ChainResult> routeChain(                           │
│           String chainId, Map<String, P> params);                          │
│                                                                             │
│       // 新增：条件路由                                                     │
│       CompletableFuture<RouteResult<D>> routeWithCondition(                │
│           String capabilityId, Map<String, P> params, RouteCondition cond);│
│   }                                                                         │
│   ```                                                                       │
│   工作量：2人天                                                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、工作量汇总

### 4.1 按模块统计

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        工作量汇总（按模块）                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   模块                      新建API    扩展API    总工作量    优先级        │
│   ═══════════════════════════════════════════════════════════════════════   │
│                                                                             │
│   1. 能力类型扩展           -         0.5人天    0.5人天     P0            │
│      CapabilityType枚举                                                      │
│                                                                             │
│   2. 能力模型扩展           -         2人天      2人天       P0            │
│      Capability类                                                            │
│                                                                             │
│   3. mainFirst自驱接口      5人天      -         5人天       P0            │
│      MainFirstService                                                        │
│                                                                             │
│   4. 驱动能力接口           9人天      -         9人天       P1            │
│      IntentReceiver/Scheduler/EventListener                                  │
│                                                                             │
│   5. 能力调用链接口         5人天      2人天      7人天       P1            │
│      CapabilityChainService                                                  │
│                                                                             │
│   6. 依赖解析接口           4人天      -         4人天       P1            │
│      DependencyResolverService                                               │
│                                                                             │
│   7. SkillManifest扩展      -         2人天      2人天       P0            │
│      场景能力定义                                                             │
│                                                                             │
│   8. REST API端点           3人天      1人天      4人天       P2            │
│      新增控制器/端点                                                         │
│                                                                             │
│   9. 单元测试               -         -         5人天       P2            │
│      测试覆盖                                                                 │
│                                                                             │
│   10. 文档更新              -         -         2人天       P2            │
│       规格文档                                                               │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│   总计                      26人天     7.5人天   40.5人天                   │
│                                                                             │
│   约 8 周（1人）或 2 周（4人团队）                                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 按优先级统计

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        工作量汇总（按优先级）                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   优先级    描述                工作量    包含内容                           │
│   ═══════════════════════════════════════════════════════════════════════   │
│                                                                             │
│   P0        核心功能（必须）    9.5人天   - 能力类型扩展 (0.5人天)           │
│            第一阶段完成                  - 能力模型扩展 (2人天)             │
│                                           - mainFirst接口 (5人天)           │
│                                           - SkillManifest扩展 (2人天)       │
│                                                                             │
│   P1        重要功能（推荐）    20人天    - 驱动能力接口 (9人天)             │
│            第二阶段完成                  - 能力调用链接口 (7人天)           │
│                                           - 依赖解析接口 (4人天)            │
│                                                                             │
│   P2        增强功能（可选）    11人天    - REST API端点 (4人天)             │
│            第三阶段完成                  - 单元测试 (5人天)                 │
│                                           - 文档更新 (2人天)                │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│   总计                          40.5人天                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 五、实施路径建议

### 5.1 分阶段实施

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        分阶段实施路径                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【第一阶段：核心能力驱动】（P0，约2周）                                     │
│   ════════════════════════════════                                           │
│                                                                             │
│   目标：实现场景能力的基本自驱                                               │
│                                                                             │
│   任务：                                                                     │
│   ├── 1. 扩展 CapabilityType 枚举                                          │
│   │    └── 新增 SCENE, DRIVER, ATOMIC, COMPOSITE                           │
│   │                                                                         │
│   ├── 2. 扩展 Capability 模型                                              │
│   │    ├── 新增 capabilities 字段                                          │
│   │    ├── 新增 mainFirst 字段                                             │
│   │    └── 新增 mainFirstConfig 字段                                       │
│   │                                                                         │
│   ├── 3. 实现 MainFirstService                                             │
│   │    ├── selfCheck(): 检查子能力就绪                                     │
│   │    ├── selfStart(): 初始化子能力                                       │
│   │    └── selfDrive(): 驱动运行（基础版）                                 │
│   │                                                                         │
│   └── 4. 扩展 SkillManifest                                                │
│        └── 新增 sceneCapabilities 定义                                     │
│                                                                             │
│   交付物：                                                                   │
│   ├── 场景能力类型定义                                                      │
│   ├── mainFirst 自驱接口                                                    │
│   ├── 基础能力驱动演示                                                      │
│   └── 单元测试                                                              │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【第二阶段：驱动能力完善】（P1，约4周）                                     │
│   ════════════════════════════════                                           │
│                                                                             │
│   目标：实现完整的驱动能力体系                                               │
│                                                                             │
│   任务：                                                                     │
│   ├── 1. 实现 IntentReceiver                                               │
│   │    └── 意图接收和解析                                                   │
│   │                                                                         │
│   ├── 2. 实现 SchedulerCapability                                          │
│   │    └── 时间驱动和调度                                                   │
│   │                                                                         │
│   ├── 3. 实现 EventListenerCapability                                      │
│   │    └── 事件订阅和监听                                                   │
│   │                                                                         │
│   ├── 4. 实现 CapabilityChainService                                       │
│   │    ├── 能力链定义                                                       │
│   │    ├── 能力链执行                                                       │
│   │    └── 条件分支                                                         │
│   │                                                                         │
│   ├── 5. 实现 DependencyResolverService                                    │
│   │    ├── 依赖解析                                                         │
│   │    ├── 拓扑排序                                                         │
│   │    └── 循环检测                                                         │
│   │                                                                         │
│   └── 6. 扩展 MainFirstService                                             │
│        └── startCollaboration(): 启动协作场景                              │
│                                                                             │
│   交付物：                                                                   │
│   ├── 五大驱动能力实现                                                      │
│   ├── 能力调用链服务                                                        │
│   ├── 依赖解析服务                                                          │
│   └── 集成测试                                                              │
│                                                                             │
│   ─────────────────────────────────────────────────────────────────────────  │
│                                                                             │
│   【第三阶段：API与文档完善】（P2，约2周）                                    │
│   ════════════════════════════════                                           │
│                                                                             │
│   目标：完善REST API和文档                                                   │
│                                                                             │
│   任务：                                                                     │
│   ├── 1. 新增 REST API 端点                                                │
│   │    ├── MainFirstController                                             │
│   │    ├── CapabilityChainController                                       │
│   │    └── DependencyController                                            │
│   │                                                                         │
│   ├── 2. 完善单元测试                                                       │
│   │    └── 测试覆盖率达到 80%                                              │
│   │                                                                         │
│   └── 3. 更新文档                                                           │
│        ├── API 文档                                                         │
│        ├── 架构文档                                                         │
│        └── 开发指南                                                         │
│                                                                             │
│   交付物：                                                                   │
│   ├── REST API 文档                                                         │
│   ├── 测试报告                                                              │
│   └── 完整文档                                                              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 六、风险评估

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        风险评估                                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   风险项                      影响    概率    缓解措施                       │
│   ═══════════════════════════════════════════════════════════════════════   │
│                                                                             │
│   1. 现有API兼容性破坏        高      中      版本化API，保持向后兼容       │
│      Capability模型扩展                                                      │
│                                                                             │
│   2. 性能影响                 中      中      能力链缓存，异步执行           │
│      能力嵌套调用                                                            │
│                                                                             │
│   3. 复杂度增加               中      高      完善文档，提供示例             │
│      概念理解成本                                                            │
│                                                                             │
│   4. 测试覆盖不足             高      中      自动化测试，集成测试           │
│      新功能测试                                                              │
│                                                                             │
│   5. 南向协议映射             中      低      参考协议文档，逐步映射         │
│      与协议不一致                                                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 七、结论

### 7.1 覆盖度总结

| 模块 | 现有覆盖度 | 需新建 | 需扩展 |
|------|-----------|--------|--------|
| 能力管理API | 100% | - | - |
| 能力驱动核心API | 25% | 6个接口 | 4个接口 |
| Skill安装与驱动API | 70% | 1个接口 | 2个接口 |

### 7.2 工作量总结

| 优先级 | 工作量 | 时间估算 |
|--------|--------|----------|
| P0（核心） | 9.5人天 | 2周 |
| P1（重要） | 20人天 | 4周 |
| P2（增强） | 11人天 | 2周 |
| **总计** | **40.5人天** | **8周** |

### 7.3 建议

1. **优先实施P0**：先实现核心能力驱动，验证架构可行性
2. **渐进式迁移**：保持现有API兼容，逐步引入新能力
3. **文档先行**：在实施前完善架构文档和API规范
4. **测试驱动**：每个阶段都要有完整的测试覆盖

---

*作者: Ooder Team*  
*更新时间: 2026-03-02*
