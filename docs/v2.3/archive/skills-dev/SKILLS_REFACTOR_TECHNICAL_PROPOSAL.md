# Ooder Skills 零配置安装重构技术方案

## 一、现有 Skills 整理

### 1.1 Skills 分类统计

| 类型 | 数量 | 说明 |
|------|------|------|
| UI Skills (nexus-ui) | 8 | 前端界面组件 |
| System Service | 25 | 后端服务 |
| NLP/AI | 6 | LLM 相关能力 |
| Storage/VFS | 6 | 存储服务 |
| Media | 5 | 媒体平台集成 |
| Payment | 3 | 支付集成 |
| Org | 4 | 组织架构集成 |
| Other | 20+ | 其他能力 |

### 1.2 适合零依赖安装的 Skills

**独立运行（无依赖）**：

| Skill | 类型 | 说明 |
|-------|------|------|
| skill-knowledge-base | system-service | 知识库核心服务 |
| skill-local-knowledge | system-service | 本地知识能力 |
| skill-vfs-local | system-service | 本地文件存储 |
| skill-security | system-service | 安全服务 |
| skill-health | system-service | 健康检查 |
| skill-monitor | system-service | 监控服务 |

**需要依赖安装的 Skills**：

| Skill | 依赖 | 说明 |
|-------|------|------|
| skill-knowledge-ui | skill-knowledge-base | 知识库管理界面 |
| skill-llm-assistant-ui | skill-llm-core, skill-local-knowledge | LLM 智能助手 |
| skill-rag | skill-knowledge-base | RAG 检索增强 |
| skill-llm-conversation | skill-llm-context-builder | LLM 对话服务 |

---

## 二、依赖安装条件配置

### 2.1 当前配置格式

```yaml
# skill-knowledge-ui/skill.yaml
dependencies:
  - skill-knowledge-base  # 缺少版本约束
```

### 2.2 建议配置格式

```yaml
# skill-knowledge-ui/skill.yaml
dependencies:
  - id: skill-knowledge-base
    version: ">=1.0.0 <2.0.0"
    required: true
    description: "知识库核心服务"
    
  - id: skill-rag
    version: ">=1.0.0"
    required: false
    description: "RAG检索增强（可选）"
```

### 2.3 需要调整配置的 Skills

| Skill | 当前状态 | 需要添加 |
|-------|---------|---------|
| skill-knowledge-ui | 缺少版本约束 | 添加完整依赖声明 |
| skill-llm-assistant-ui | 依赖声明不完整 | 添加所有依赖 |
| skill-rag | 无依赖声明 | 添加 skill-knowledge-base |
| skill-llm-conversation | 无依赖声明 | 添加 context-builder 依赖 |

---

## 三、OoderAgent 底层协议规范

### 3.1 南向协议架构

```
┌─────────────────────────────────────────────────────────────────┐
│                      OoderAgent 架构                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │  Northbound │    │   Agent     │    │  Southbound │         │
│  │  Protocol   │ ←─→│   Core      │←─→ │  Protocol   │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│         │                  │                  │                 │
│         ↓                  ↓                  ↓                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │ Observation │    │ Capability  │    │ Discovery   │         │
│  │ Protocol    │    │ Registry    │    │ Protocol    │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                            │                                    │
│                            ↓                                    │
│                     ┌─────────────┐                            │
│                     │   Scene     │                            │
│                     │  Manager    │                            │
│                     └─────────────┘                            │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 核心协议接口

**DiscoveryProtocolAdapter** - 发现协议
```java
public interface DiscoveryProtocolAdapter {
    void startDiscovery(DiscoveryConfig config);
    void stopDiscovery();
    List<DiscoveredNode> getDiscoveredNodes();
    CompletableFuture<List<DiscoveredNode>> discoverOnce(DiscoveryConfig config);
}
```

**CollaborationProtocol** - 协作协议
```java
public interface CollaborationProtocol {
    CompletableFuture<SceneGroupInfo> joinSceneGroup(String groupId, JoinRequest request);
    CompletableFuture<TaskInfo> receiveTask(String groupId);
    CompletableFuture<Void> submitTaskResult(String groupId, String taskId, TaskResult result);
    CompletableFuture<Void> syncState(String groupId, Map<String, Object> state);
}
```

**SceneManager** - 场景管理
```java
public interface SceneManager {
    CompletableFuture<SceneDefinition> create(SceneDefinition definition);
    CompletableFuture<Void> addCapability(String sceneId, Capability capability);
    CompletableFuture<List<Capability>> listCapabilities(String sceneId);
}
```

### 3.3 能力绑定机制

```java
// SceneDefinition 包含能力列表
public class SceneDefinition {
    private String sceneId;
    private List<Capability> capabilities;  // 能力列表
    private List<String> collaborativeScenes;  // 协作场景
}

// Capability 定义
public class Capability {
    private String capId;
    private String name;
    private String description;
    private String provider;  // 能力提供者
}
```

---

## 四、重构技术方案

### 4.1 零配置安装流程

```
用户选择场景模板
        ↓
┌─────────────────────────────────────────────────────────────────┐
│  TemplateService.deployTemplate(templateId)                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 解析模板定义                                                 │
│     └─ skills: [skill-knowledge-ui, skill-knowledge-base]       │
│                                                                 │
│  2. 构建依赖图                                                   │
│     └─ skill-knowledge-ui → skill-knowledge-base                │
│                                                                 │
│  3. 拓扑排序                                                    │
│     └─ [skill-knowledge-base, skill-knowledge-ui]               │
│                                                                 │
│  4. 按序安装 Skills                                             │
│     ├─ installWithDependencies(skill-knowledge-base)            │
│     └─ installWithDependencies(skill-knowledge-ui)              │
│                                                                 │
│  5. 创建场景                                                     │
│     └─ sceneManager.create(SceneDefinition)                     │
│                                                                 │
│  6. 绑定能力                                                     │
│     ├─ sceneManager.addCapability(sceneId, kb-management)       │
│     └─ sceneManager.addCapability(sceneId, kb-search)           │
│                                                                 │
│  7. 注册 UI                                                      │
│     └─ nexusUiLoader.register(skill-knowledge-ui)               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
        ↓
返回部署结果
```

### 4.2 依赖解析算法

```java
public class DependencyResolver {
    
    public List<String> topologicalSort(Map<String, List<String>> dependencyGraph) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();
        
        for (String node : dependencyGraph.keySet()) {
            if (!visited.contains(node)) {
                visit(node, dependencyGraph, visited, visiting, result);
            }
        }
        
        return result;  // 按依赖顺序排列
    }
    
    private void visit(String node, Map<String, List<String>> graph,
            Set<String> visited, Set<String> visiting, List<String> result) {
        if (visiting.contains(node)) {
            throw new CycleDependencyException("Circular dependency detected: " + node);
        }
        
        visiting.add(node);
        
        for (String dep : graph.getOrDefault(node, Collections.emptyList())) {
            if (!visited.contains(dep)) {
                visit(dep, graph, visited, visiting, result);
            }
        }
        
        visiting.remove(node);
        visited.add(node);
        result.add(node);
    }
}
```

### 4.3 Skill.yaml 规范增强

```yaml
# 完整的 skill.yaml 规范
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-knowledge-ui
  name: 知识库管理
  version: 1.0.0
  description: 知识库管理界面
  author: ooder Team
  type: nexus-ui

spec:
  type: nexus-ui
  
  # 依赖声明（增强）
  dependencies:
    - id: skill-knowledge-base
      version: ">=1.0.0 <2.0.0"
      required: true
      description: "知识库核心服务"
      capabilities:
        - kb-management
        - kb-search
    
  # 提供的接口
  providedInterfaces:
    - id: knowledge-ui
      version: "1.0"
      
  # 需要的接口
  requiredInterfaces:
    - id: knowledge-storage
      version: ">=1.0"
      optional: false
      
  # 能力声明
  capabilities:
    - id: kb-view
      name: 查看知识库
      category: ui
    - id: kb-manage
      name: 管理知识库
      category: ui
      
  # UI 配置
  nexusUi:
    entry:
      page: index.html
      title: 知识库
      icon: ri-book-3-line
    menu:
      position: sidebar
      category: knowledge
      order: 10
      
  # 场景绑定
  sceneBindings:
    - sceneType: knowledge
      autoBind: true
      capabilities:
        - kb-view
        - kb-manage
```

### 4.4 接口协议适配

```java
// Skill 安装南向协议适配
public interface SkillInstallProtocol {
    
    // 发现可用 Skills
    CompletableFuture<List<SkillInfo>> discoverSkills(DiscoveryConfig config);
    
    // 安装 Skill
    CompletableFuture<InstallResult> install(InstallRequest request);
    
    // 安装带依赖
    CompletableFuture<InstallResultWithDependencies> installWithDependencies(
        String skillId, InstallMode mode);
    
    // 卸载 Skill
    CompletableFuture<UninstallResult> uninstall(String skillId);
    
    // 检查依赖是否满足
    CompletableFuture<DependencyCheckResult> checkDependencies(String skillId);
}

// 场景绑定南向协议适配
public interface SceneBindingProtocol {
    
    // 创建场景
    CompletableFuture<SceneInfo> createScene(SceneDefinition definition);
    
    // 绑定能力
    CompletableFuture<Void> bindCapability(String sceneId, String capId);
    
    // 解绑能力
    CompletableFuture<Void> unbindCapability(String sceneId, String capId);
    
    // 获取场景能力列表
    CompletableFuture<List<CapabilityInfo>> getCapabilities(String sceneId);
}
```

### 4.5 重构实施计划

| 阶段 | 任务 | 工作量 |
|------|------|--------|
| **Phase 1** | 依赖配置规范化 | 2天 |
| - | 更新所有 skill.yaml 依赖声明 | |
| - | 添加版本约束和 required 标记 | |
| **Phase 2** | 依赖解析器实现 | 3天 |
| - | 实现 DependencyResolver | |
| - | 实现循环依赖检测 | |
| - | 实现版本兼容性检查 | |
| **Phase 3** | 安装流程重构 | 3天 |
| - | 实现 installWithDependencies | |
| - | 实现拓扑排序安装 | |
| - | 实现安装回滚机制 | |
| **Phase 4** | 场景绑定自动化 | 2天 |
| - | 实现场景自动创建 | |
| - | 实现能力自动绑定 | |
| - | 实现 UI 自动注册 | |
| **Phase 5** | 测试验证 | 2天 |
| - | 单元测试 | |
| - | 集成测试 | |
| - | 端到端测试 | |

---

## 五、预期效果

### 5.1 用户体验

**改造前**：
1. 发现 Skills
2. 手动安装依赖
3. 安装主 Skill
4. 创建场景
5. 绑定能力
6. 配置 UI

**改造后**：
1. 选择场景模板
2. 一键部署

### 5.2 技术收益

| 指标 | 改造前 | 改造后 |
|------|--------|--------|
| 安装步骤 | 6步 | 1步 |
| 依赖处理 | 手动 | 自动 |
| 版本冲突 | 运行时发现 | 安装前检测 |
| 回滚能力 | 无 | 支持 |
| 错误定位 | 困难 | 清晰 |

---

## 六、风险与对策

| 风险 | 影响 | 对策 |
|------|------|------|
| 循环依赖 | 安装失败 | 编译时检测 + 强制解除 |
| 版本冲突 | 运行异常 | 版本范围约束 + 兼容性矩阵 |
| 网络超时 | 安装中断 | 重试机制 + 离线缓存 |
| 存储空间 | 安装失败 | 预检查 + 空间估算 |
