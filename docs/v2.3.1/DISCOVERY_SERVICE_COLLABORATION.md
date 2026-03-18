# 发现程序设计与协同任务说明

**版本**: 2.3.1  
**创建日期**: 2026-03-18  
**状态**: 待评审

---

## 一、当前架构分析

### 1.1 发现机制现状

```
┌─────────────────────────────────────────────────────────────────┐
│                        发现机制架构                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────┐ │
│  │ GitDiscovery    │    │ SkillIndex      │    │ SceneEngine │ │
│  │ Controller      │───▶│ Loader          │◀───│ Integration │ │
│  └────────┬────────┘    └────────┬────────┘    └─────────────┘ │
│           │                      │                             │
│           ▼                      ▼                             │
│  ┌─────────────────┐    ┌─────────────────┐                    │
│  │ GitHubDiscoverer│    │ skill-index/    │                    │
│  │ GiteeDiscoverer │    │  ├── categories │                    │
│  │ UnifiedSceneSvc │    │  ├── skills/    │                    │
│  └─────────────────┘    │  └── scenes/    │                    │
│                         └─────────────────┘                    │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 现有组件

| 组件 | 位置 | 职责 |
|------|------|------|
| `GitDiscoveryController` | mvp/skill-scene | REST API 入口，协调发现流程 |
| `SkillIndexLoader` | 4个工程 | 加载 skill-index 目录 |
| `GitHubDiscoverer` | SDK | GitHub 仓库发现 |
| `GiteeDiscoverer` | SDK | Gitee 仓库发现 |
| `SceneEngineIntegration` | mvp/skill-scene | SE 与 Skill 集成 |
| `CapabilityDiscoveryService` | skill-capability | 能力发现服务 |

### 1.3 问题识别

| 问题 | 影响 | 优先级 |
|------|------|:------:|
| 多个 SkillIndexLoader 副本，代码重复 | 维护困难 | P1 |
| 远程发现与本地发现逻辑分散 | 一致性差 | P1 |
| SceneEngine 不直接参与发现流程 | 功能受限 | P2 |
| 缺少统一的发现器接口规范 | 扩展困难 | P2 |

---

## 二、发现程序设计

### 2.1 目标架构

```
┌─────────────────────────────────────────────────────────────────┐
│                      统一发现服务架构                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              DiscoveryOrchestrator                       │   │
│  │  - 统一发现入口                                          │   │
│  │  - 策略选择 (本地/远程/混合)                             │   │
│  │  - 结果聚合                                              │   │
│  └───────────────────────┬─────────────────────────────────┘   │
│                          │                                      │
│          ┌───────────────┼───────────────┐                     │
│          ▼               ▼               ▼                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐               │
│  │ Local       │ │ Remote      │ │ Index       │               │
│  │ Discoverer  │ │ Discoverer  │ │ Loader      │               │
│  └─────────────┘ └─────────────┘ └─────────────┘               │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              SceneEngine Discovery Hook                  │   │
│  │  - 场景能力发现                                          │   │
│  │  - 驱动条件匹配                                          │   │
│  │  - 能力注册                                              │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 核心接口设计

```java
public interface SkillDiscoverer {
    
    CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request);
    
    CompletableFuture<SkillPackage> discoverOne(String skillId);
    
    DiscoveryMethod getMethod();
    
    boolean isAvailable();
}

public class DiscoveryRequest {
    private String source;           // LOCAL, GITHUB, GITEE, GIT
    private String repoUrl;          // 远程仓库地址
    private String branch;           // 分支
    private String category;         // 分类过滤
    private String sceneType;        // 场景类型过滤
    private List<String> tags;       // 标签过滤
    private boolean useIndexFirst;   // 优先使用索引
}

public class DiscoveryResult {
    private List<CapabilityDTO> capabilities;
    private List<CapabilityDTO> scenes;
    private long scanTime;
    private boolean fromCache;
    private String errorMessage;
}
```

### 2.3 发现流程

```
┌──────────────────────────────────────────────────────────────────┐
│                        发现流程                                   │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. 请求入口                                                     │
│     └── GitDiscoveryController.discoverFromXxx()                │
│                                                                  │
│  2. 策略选择                                                     │
│     └── if (useIndexFirst) → SkillIndexLoader                   │
│     └── else → RemoteDiscoverer                                 │
│                                                                  │
│  3. 本地发现                                                     │
│     └── SkillIndexLoader.loadFromDirectory()                    │
│         ├── categories.yaml                                     │
│         ├── scene-drivers.yaml                                  │
│         ├── skills/*.yaml                                       │
│         └── scenes/*.yaml                                       │
│                                                                  │
│  4. 远程发现                                                     │
│     └── GitHubDiscoverer / GiteeDiscoverer                      │
│         ├── 获取仓库内容                                        │
│         ├── 解析 skill-index/ 目录                              │
│         └── 返回 SkillPackage 列表                              │
│                                                                  │
│  5. 结果聚合                                                     │
│     └── 合并本地 + 远程结果                                     │
│     └── 去重 + 状态标记                                        │
│                                                                  │
│  6. SceneEngine 集成                                             │
│     └── SceneEngineIntegration.registerCapabilityBinding()      │
│     └── CapabilityRegistry.register()                           │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 三、SceneEngine 修改需求

### 3.1 需要修改的内容

| 模块 | 修改内容 | 优先级 |
|------|----------|:------:|
| `SceneEngineIntegration` | 添加 `discoverFromIndex()` 方法 | P1 |
| `SceneEngineIntegration` | 支持远程目录结构解析 | P1 |
| `CapabilityRegistry` | 添加批量注册接口 | P2 |
| `SceneEngine` | 添加发现事件监听 | P2 |

### 3.2 新增接口

```java
// SceneEngineIntegration 新增方法
public class SceneEngineIntegration {
    
    public List<CapabilityDTO> discoverFromIndex(File indexDir) {
        // 解析 skill-index 目录结构
        // 返回能力列表
    }
    
    public void registerCapabilities(List<CapabilityDTO> capabilities) {
        // 批量注册能力到 SceneEngine
    }
    
    public void onSkillDiscovered(SkillPackage skill) {
        // 技能发现事件回调
    }
}
```

### 3.3 SceneEngine 发现钩子

```java
// SceneEngine 新增接口
public interface SceneEngineDiscoveryHook {
    
    void onBeforeDiscovery(DiscoveryRequest request);
    
    void onCapabilityDiscovered(CapabilityDTO capability);
    
    void onSceneDiscovered(CapabilityDTO scene);
    
    void onAfterDiscovery(DiscoveryResult result);
}
```

---

## 四、协同任务分配

### 4.1 Skills 团队任务

| 任务 | 描述 | 工作量 | 优先级 |
|------|------|:------:|:------:|
| SKILL-001 | 统一 SkillIndexLoader 到 skill-common | 4h | P1 |
| SKILL-002 | 创建 DiscoveryOrchestrator 服务 | 8h | P1 |
| SKILL-003 | 更新 GitDiscoveryController 使用新架构 | 4h | P1 |
| SKILL-004 | 添加远程目录结构解析支持 | 4h | P2 |
| SKILL-005 | 编写发现服务单元测试 | 4h | P2 |

### 4.2 SceneEngine 团队任务

| 任务 | 描述 | 工作量 | 优先级 |
|------|------|:------:|:------:|
| SE-001 | 添加 SceneEngineDiscoveryHook 接口 | 2h | P1 |
| SE-002 | 更新 SceneEngineIntegration 支持目录结构 | 4h | P1 |
| SE-003 | 添加批量能力注册接口 | 2h | P2 |
| SE-004 | 实现发现事件监听机制 | 4h | P2 |
| SE-005 | 更新 SE 文档 | 2h | P2 |

### 4.3 MVP 团队任务

| 任务 | 描述 | 工作量 | 优先级 |
|------|------|:------:|:------:|
| MVP-001 | 移除重复的 SkillIndexLoader | 2h | P1 |
| MVP-002 | 更新 GitDiscoveryController | 2h | P1 |
| MVP-003 | 集成统一发现服务 | 4h | P1 |
| MVP-004 | 验证发现流程 | 2h | P2 |

---

## 五、时间线

```
Week 1 (2026-03-18 ~ 2026-03-22)
├── Day 1-2: SKILL-001, SE-001, MVP-001
├── Day 3-4: SKILL-002, SE-002, MVP-002
└── Day 5: SKILL-003, MVP-003

Week 2 (2026-03-23 ~ 2026-03-27)
├── Day 1-2: SKILL-004, SE-003
├── Day 3-4: SKILL-005, SE-004
└── Day 5: 集成测试, MVP-004, SE-005
```

---

## 六、依赖关系

```
SKILL-001 ──▶ SKILL-002 ──▶ SKILL-003
                │
                ▼
            SE-002 ──▶ SE-003
                │
                ▼
            MVP-001 ──▶ MVP-002 ──▶ MVP-003
                                        │
                                        ▼
                                    MVP-004
```

---

## 七、验收标准

### 7.1 功能验收

- [ ] 本地发现正常工作 (skill-index 目录)
- [ ] 远程发现正常工作 (GitHub/Gitee)
- [ ] 发现结果正确聚合
- [ ] SceneEngine 正确接收发现事件
- [ ] 能力正确注册到 CapabilityRegistry

### 7.2 性能验收

- [ ] 本地发现 < 500ms
- [ ] 远程发现 < 5s (缓存命中 < 100ms)
- [ ] 内存占用 < 100MB

### 7.3 兼容性验收

- [ ] 向后兼容现有 API
- [ ] 支持单文件模式过渡期

---

## 八、风险与缓解

| 风险 | 影响 | 概率 | 缓解措施 |
|------|:----:|:----:|----------|
| ClassLoader 隔离问题 | 高 | 中 | 使用接口 + 反射调用 |
| 远程仓库格式不一致 | 中 | 低 | 添加格式校验和兼容层 |
| 发现性能下降 | 中 | 低 | 添加缓存和异步加载 |

---

## 九、联系人与资源

| 角色 | 负责人 | 职责 |
|------|--------|------|
| Skills 团队 | TBD | 发现服务开发 |
| SceneEngine 团队 | TBD | SE 集成开发 |
| MVP 团队 | TBD | 主应用集成 |
| 架构评审 | TBD | 设计评审 |

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-18
