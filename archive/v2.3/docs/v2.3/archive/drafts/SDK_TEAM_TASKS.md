# SDK 协作团队开发任务分解

## 项目信息

| 项目 | 说明 |
|------|------|
| 项目名称 | Ooder 零配置安装重构 |
| 版本 | v2.3 |
| 开始日期 | 2026-03-02 |
| 预计完成 | 2026-03-09 |

---

## 一、团队与职责

| 团队 | 职责 | 主要交付物 |
|------|------|-----------|
| **SDK 团队** | 核心框架开发 | SkillPackageManager, SceneDependencyResolver |
| **Engine 团队** | 场景引擎开发 | SceneManager, SceneGroupManager |
| **Scene 团队** | 场景服务开发 | SceneTemplateService, CapabilityBinding |
| **UI 团队** | 前端界面开发 | 模板选择页、部署进度页 |

---

## 二、任务分解

### Phase 1: 核心框架 (Day 1-2)

#### SDK-001: SceneDependencyResolver 增强

**负责团队**: SDK 团队  
**优先级**: P0  
**预估工时**: 4h  
**依赖**: 无

**任务描述**:
增强 `SceneDependencyResolver` 接口，支持场景模板依赖解析。

**开发内容**:
```java
public interface SceneDependencyResolver {
    
    // 新增方法
    List<SceneDependency> resolveFromTemplate(SceneTemplate template);
    
    List<String> getInstallOrder(SceneTemplate template);
    
    Map<String, DependencyStatus> checkAllDependencies(SceneTemplate template);
}
```

**验收标准**:
- [ ] 能解析场景模板中的 skills 列表
- [ ] 能获取正确的安装顺序（拓扑排序）
- [ ] 能检查所有依赖的安装状态

---

#### SDK-002: InstallWithDependencies 增强

**负责团队**: SDK 团队  
**优先级**: P0  
**预估工时**: 4h  
**依赖**: SDK-001

**任务描述**:
增强 `SkillPackageManager.installWithDependencies()` 方法，支持场景模板安装。

**开发内容**:
```java
public interface SkillPackageManager {
    
    // 新增方法
    CompletableFuture<TemplateInstallResult> installFromTemplate(
        SceneTemplate template, 
        InstallOptions options
    );
}

public class InstallOptions {
    private boolean skipInstalled;
    private boolean autoCreateScene;
    private boolean autoActivate;
    private Map<String, Object> configOverrides;
}
```

**验收标准**:
- [ ] 能按模板安装所有 Skills
- [ ] 支持跳过已安装的 Skills
- [ ] 返回详细的安装结果

---

#### SDK-003: 版本兼容性检查

**负责团队**: SDK 团队  
**优先级**: P1  
**预估工时**: 3h  
**依赖**: SDK-001

**任务描述**:
实现 Skill 版本兼容性检查。

**开发内容**:
```java
public interface VersionCompatibilityChecker {
    
    CompatibilityResult check(String skillId, String requiredVersion);
    
    CompatibilityResult checkAll(List<Dependency> dependencies);
}

public class CompatibilityResult {
    private boolean compatible;
    private List<String> conflicts;
    private String suggestedVersion;
}
```

**验收标准**:
- [ ] 能检查版本范围是否满足
- [ ] 能检测版本冲突
- [ ] 能建议兼容版本

---

### Phase 2: 场景引擎 (Day 2-3)

#### ENG-001: SceneGroupManager 实现

**负责团队**: Engine 团队  
**优先级**: P0  
**预估工时**: 4h  
**依赖**: 无

**任务描述**:
实现场景组管理器，支持场景组自动创建。

**开发内容**:
```java
public interface SceneGroupManager {
    
    CompletableFuture<SceneGroupInfo> createGroup(SceneGroupRequest request);
    
    CompletableFuture<Void> addCollaborativeScene(String groupId, String sceneId);
    
    CompletableFuture<Void> removeCollaborativeScene(String groupId, String sceneId);
    
    CompletableFuture<SceneGroupInfo> getGroupInfo(String groupId);
}

public class SceneGroupRequest {
    private String groupId;
    private String primarySceneId;
    private List<String> collaborativeSceneIds;
    private Map<String, Object> sharedState;
}
```

**验收标准**:
- [ ] 能创建场景组
- [ ] 能添加/移除协作场景
- [ ] 能获取场景组信息

---

#### ENG-002: 场景激活流程

**负责团队**: Engine 团队  
**优先级**: P0  
**预估工时**: 3h  
**依赖**: ENG-001

**任务描述**:
实现场景激活时自动创建场景组。

**开发内容**:
```java
public class SceneActivationFlow {
    
    public CompletableFuture<ActivationResult> activate(String sceneId) {
        // 1. 检查场景状态
        // 2. 启动场景服务
        // 3. 检查是否有协作场景
        // 4. 创建场景组（如有）
        // 5. 建立场景间通信
        // 6. 同步初始状态
    }
}
```

**验收标准**:
- [ ] 场景激活时检查协作场景
- [ ] 自动创建场景组
- [ ] 建立场景间通信

---

#### ENG-003: 场景间通信

**负责团队**: Engine 团队  
**优先级**: P1  
**预估工时**: 4h  
**依赖**: ENG-002

**任务描述**:
实现场景间通信机制。

**开发内容**:
```java
public interface SceneCommunication {
    
    void sendMessage(String fromScene, String toScene, SceneMessage message);
    
    void broadcast(String groupId, SceneMessage message);
    
    void registerHandler(String sceneId, MessageHandler handler);
}
```

**验收标准**:
- [ ] 能发送消息到指定场景
- [ ] 能广播消息到场景组
- [ ] 能注册消息处理器

---

### Phase 3: 场景服务 (Day 3-4)

#### SCN-001: 场景模板解析增强

**负责团队**: Scene 团队  
**优先级**: P0  
**预估工时**: 3h  
**依赖**: SDK-001

**任务描述**:
增强场景模板解析，支持完整的模板配置。

**开发内容**:
```java
public class SceneTemplateParser {
    
    public SceneTemplate parse(InputStream yaml);
    
    public SceneTemplate parseFromYaml(String yamlContent);
}

// 模板配置增强
public class SceneTemplate {
    private List<SkillRef> skills;
    private List<CapabilityBinding> capabilityBindings;
    private List<CollaborativeSceneRef> collaborativeScenes;
    private SceneConfig sceneConfig;
}
```

**验收标准**:
- [ ] 能解析完整的模板配置
- [ ] 支持能力绑定配置
- [ ] 支持协作场景配置

---

#### SCN-002: 能力动态绑定

**负责团队**: Scene 团队  
**优先级**: P0  
**预估工时**: 4h  
**依赖**: SCN-001

**任务描述**:
实现能力动态绑定机制。

**开发内容**:
```java
public class CapabilityBindingService {
    
    public CompletableFuture<BindingResult> bindCapabilities(
        String sceneId, 
        List<CapabilityBinding> bindings,
        Map<String, Object> context
    );
    
    public CompletableFuture<Void> unbindCapability(String sceneId, String capId);
    
    public List<CapabilityBinding> evaluateConditions(
        List<CapabilityBinding> bindings,
        Map<String, Object> context
    );
}
```

**验收标准**:
- [ ] 能绑定能力到场景
- [ ] 支持条件绑定
- [ ] 能解绑能力

---

#### SCN-003: 部署流程整合

**负责团队**: Scene 团队  
**优先级**: P0  
**预估工时**: 4h  
**依赖**: SDK-002, ENG-001, SCN-002

**任务描述**:
整合完整的部署流程。

**开发内容**:
```java
public class DeploymentFlow {
    
    public CompletableFuture<DeploymentResult> deploy(DeploymentRequest request) {
        // 1. 解析模板
        // 2. 检查依赖
        // 3. 安装 Skills
        // 4. 创建场景
        // 5. 绑定能力
        // 6. 激活场景
        // 7. 创建场景组
        // 8. 返回结果
    }
}
```

**验收标准**:
- [ ] 完整的部署流程
- [ ] 每步都有进度反馈
- [ ] 失败时能回滚

---

### Phase 4: 前端界面 (Day 4-5)

#### UI-001: 场景模板列表页

**负责团队**: UI 团队  
**优先级**: P1  
**预估工时**: 4h  
**依赖**: SCN-001

**任务描述**:
实现场景模板列表页面。

**开发内容**:
- 模板卡片组件
- 分类筛选
- 搜索功能
- 详情预览

**验收标准**:
- [ ] 显示所有可用模板
- [ ] 支持分类筛选
- [ ] 支持搜索
- [ ] 显示模板详情

---

#### UI-002: 一键部署界面

**负责团队**: UI 团队  
**优先级**: P1  
**预估工时**: 4h  
**依赖**: UI-001, SCN-003

**任务描述**:
实现一键部署界面和进度显示。

**开发内容**:
- 部署确认弹窗
- 进度显示组件
- 结果展示页面

**验收标准**:
- [ ] 点击部署显示确认弹窗
- [ ] 显示安装进度
- [ ] 显示部署结果

---

#### UI-003: 场景管理页面

**负责团队**: UI 团队  
**优先级**: P2  
**预估工时**: 3h  
**依赖**: UI-002

**任务描述**:
实现场景管理页面。

**开发内容**:
- 场景列表
- 场景详情
- 能力管理
- 场景组管理

**验收标准**:
- [ ] 显示已创建场景
- [ ] 能查看场景详情
- [ ] 能管理能力绑定

---

## 三、依赖关系图

```
┌──────────────────────────────────────────────────────────────────────────┐
│                         任务依赖关系                                       │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  Phase 1 (SDK)                                                           │
│  ────────────                                                            │
│  SDK-001 ──→ SDK-002 ──→ SDK-003                                        │
│     │                                                                    │
│     └────────────────────────────────────────┐                          │
│                                              ↓                          │
│  Phase 2 (Engine)                        ENG-001 ──→ ENG-002 ──→ ENG-003 │
│  ────────────────                            ↑                          │
│                                              │                          │
│  Phase 3 (Scene)                            │                          │
│  ────────────────                            │                          │
│  SCN-001 ──→ SCN-002 ──→ SCN-003 ───────────┘                          │
│     ↑              ↑                                                    │
│     │              │                                                    │
│     └──────────────┼────────────────────────┐                          │
│                    │                        │                          │
│  Phase 4 (UI)      │                        │                          │
│  ────────────      │                        │                          │
│  UI-001 ──→ UI-002 ┴───────────────────────→ UI-003                    │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 四、里程碑

| 里程碑 | 日期 | 交付物 | 负责团队 |
|--------|------|--------|---------|
| M1: SDK 就绪 | Day 2 | SDK-001, SDK-002, SDK-003 | SDK 团队 |
| M2: Engine 就绪 | Day 3 | ENG-001, ENG-002, ENG-003 | Engine 团队 |
| M3: Scene 就绪 | Day 4 | SCN-001, SCN-002, SCN-003 | Scene 团队 |
| M4: UI 就绪 | Day 5 | UI-001, UI-002, UI-003 | UI 团队 |
| M5: 集成测试 | Day 6 | 完整流程测试 | 全部 |
| M6: 发布 | Day 7 | v2.3 发布 | 全部 |

---

## 五、验收测试

### 5.1 单元测试

每个任务完成后，负责团队需完成：
- [ ] 单元测试覆盖率 > 80%
- [ ] 所有测试通过
- [ ] 代码审查通过

### 5.2 集成测试

```bash
# 测试场景模板部署
curl -X POST http://localhost:8084/api/v1/templates/knowledge-qa/deploy

# 预期结果
{
  "code": 200,
  "data": {
    "sceneId": "knowledge-qa-xxx",
    "status": "deployed",
    "skills": ["skill-knowledge-base", "skill-knowledge-ui"],
    "capabilities": ["kb-management", "kb-search"],
    "sceneGroup": {
      "groupId": "knowledge-qa-group",
      "primarySceneId": "knowledge-qa-xxx",
      "collaborativeScenes": ["rag-scene"]
    }
  }
}
```

### 5.3 端到端测试

1. 打开场景模板列表页
2. 选择"知识问答场景"模板
3. 点击"一键部署"
4. 等待安装完成
5. 验证场景已创建
6. 验证能力已绑定
7. 验证场景组已创建

---

## 六、沟通机制

### 6.1 每日站会

- 时间：每天 10:00
- 内容：进度同步、问题讨论

### 6.2 代码审查

- 每个任务完成后提交 PR
- 至少 1 人审查通过才能合并

### 6.3 文档更新

- API 变更需要更新文档
- 新增功能需要更新用户手册

---

## 七、风险与对策

| 风险 | 影响 | 对策 | 负责人 |
|------|------|------|--------|
| SDK 接口变更 | 全部延期 | 提前定义接口，冻结 API | SDK 团队 |
| 依赖冲突 | 安装失败 | 版本兼容性检查 | SDK 团队 |
| 场景组通信失败 | 协作失效 | 降级为单场景模式 | Engine 团队 |
| 前端进度延迟 | 用户体验差 | 先完成 API，前端并行开发 | UI 团队 |

---

## 八、联系方式

| 团队 | 负责人 | 联系方式 |
|------|--------|---------|
| SDK 团队 | - | - |
| Engine 团队 | - | - |
| Scene 团队 | - | - |
| UI 团队 | - | - |
