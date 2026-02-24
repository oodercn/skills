# Ooder SDK 2.3 Skills 升级指南

## 一、SDK 2.3 版本概述

### 1.1 版本信息

| 属性 | 值 |
|------|-----|
| **版本号** | 2.3 |
| **发布日期** | 2026-02-24 |
| **状态** | 正式发布 |
| **基础版本** | 基于 v0.8.0 重构 |

### 1.2 主要变更

```
┌─────────────────────────────────────────────────────────┐
│                    SDK 2.3 核心变更                      │
├─────────────────────────────────────────────────────────┤
│                                                                 │
│  ✅ 代码精简: 删除约70个冗余文件                          │
│  ✅ 架构统一: 标准化API接口                              │
│  ✅ 能力扩展: 新增AI能力模块(skill-ai)                   │
│  ✅ 版本统一: 所有模块版本统一为2.3                      │
│                                                                 │
└─────────────────────────────────────────────────────────┘
```

### 1.3 模块版本统一

| 模块 | 旧版本 | 新版本 | 变更 |
|------|--------|--------|------|
| agent-sdk | 0.8.0 | 2.3 | 删除冗余包 |
| scene-engine | 0.8.0 | 2.3 | 删除drivers/ |
| llm-sdk | 0.8.0 | 2.3 | 保持不变 |
| skill-ai | - | 2.3 | 新增 |

---

## 二、Breaking Changes

### 2.1 删除的代码

#### scene-engine: 删除 drivers/ 目录 (~50文件)

```
删除:
- drivers/vfs/ → 使用 skill-vfs
- drivers/org/ → 使用 skill-org  
- drivers/msg/ → 使用 skill-msg
- drivers/mqtt/ → 使用 skill-mqtt
```

#### agent-sdk: 删除旧包 (~16文件)

```
删除:
- net.ooder.sdk.cmd.* → 使用 net.ooder.sdk.api.cmd.*
- net.ooder.sdk.msg.* → 使用 net.ooder.sdk.api.msg.*
```

### 2.2 Import 变更

| 旧 Import | 新 Import |
|-----------|-----------|
| `net.ooder.sdk.cmd.CmdClientConfig` | `net.ooder.sdk.api.cmd.CmdClientConfig` |
| `net.ooder.sdk.msg.MsgClientConfig` | `net.ooder.sdk.api.msg.MsgClientConfig` |
| `net.ooder.scene.drivers.vfs.*` | `net.ooder.scene.skills.vfs.*` |
| `net.ooder.scene.drivers.org.*` | `net.ooder.scene.skills.org.*` |

---

## 三、Skills 依赖分析

### 3.1 当前依赖状况

```
ooder-skills (30个 skills)
├── 无 SDK 依赖: 13个
│   └── skill-common, skill-k8s, skill-scheduler-quartz, skill-market,
│       skill-im, skill-group, skill-business, skill-msg, skill-collaboration,
│       skill-mqtt, skill-hosting, skill-monitor, skill-vfs-local
│
├── agent-sdk 依赖: 4个
│   └── skill-a2ui, skill-user-auth, skill-org-dingding, skill-org-feishu
│       (使用: OoderSDK, EndAgent)
│
└── scene-engine 依赖: 15个 (需要关注!)
    └── skill-share, skill-security, skill-protocol, skill-network,
        skill-hosting, skill-health, skill-agent, skill-openwrt,
        skill-llm-*, skill-httpclient-okhttp
        (使用: Result, SceneEngine, *Provider, LlmProvider)
```

### 3.2 兼容性评估

| Skill 类型 | 数量 | 兼容性 | 迁移成本 |
|-----------|------|--------|----------|
| 无 SDK 依赖 | 13 | ✅ 100% | 无 |
| agent-sdk 依赖 | 4 | 🔶 90% | 低 (仅import变更) |
| scene-engine 依赖 | 15 | ❌ 0% | 高 (需适配) |
| **总计** | **32** | **56%** | **中-高** |

---

## 四、Skills 全生命周期方案

### 4.1 生命周期阶段

```
┌─────────────────────────────────────────────────────────────────┐
│                    Skills 全生命周期                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 发现 (Discovery)                                            │
│     ├── 从 SkillCenter 获取技能索引                            │
│     ├── 解析 skill.yaml 元数据                                  │
│     └── 验证依赖兼容性                                          │
│                                                                 │
│  2. 安装 (Installation)                                         │
│     ├── 下载 JAR 包                                             │
│     ├── 验证签名和完整性                                        │
│     ├── 注册到 SkillRegistry                                    │
│     └── 初始化配置                                              │
│                                                                 │
│  3. 启动 (Startup)                                              │
│     ├── 加载类到 ClassLoader                                    │
│     ├── 执行 @PostConstruct                                     │
│     ├── 注册 Provider 到 SceneEngine                            │
│     └── 加入场景组 (SceneGroup)                                 │
│                                                                 │
│  4. 运行 (Runtime)                                              │
│     ├── 处理 CAP 调用                                           │
│     ├── 事件监听与处理                                          │
│     └── 健康检查                                                │
│                                                                 │
│  5. 升级 (Upgrade)                                              │
│     ├── 检测新版本                                              │
│     ├── 热更新 (Hot Swap)                                       │
│     └── 数据迁移                                                │
│                                                                 │
│  6. 销毁 (Destruction)                                          │
│     ├── 优雅停机                                                │
│     ├── 保存状态                                                │
│     ├── 注销 Provider                                           │
│     └── 清理资源                                                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 SDK 2.3 生命周期支持

```java
// Skill 生命周期接口 (SDK 2.3)
public interface SkillLifecycle {
    // 阶段1: 发现
    SkillMetadata discover(String skillId);
    
    // 阶段2: 安装
    InstallResult install(InstallRequest request);
    
    // 阶段3: 启动
    void initialize(SkillContext context);
    void start();
    
    // 阶段4: 运行
    void handleCap(CapRequest request);
    void handleEvent(Event event);
    
    // 阶段5: 升级
    UpgradeResult upgrade(UpgradeRequest request);
    
    // 阶段6: 销毁
    void stop();
    void destroy();
}
```

---

## 五、升级方案

### 5.1 方案 A: 保持 scene-engine 2.3 (推荐短期)

**适用**: 需要快速升级，暂不迁移 API

**pom.xml 变更**:
```xml
<properties>
    <agent-sdk.version>2.3</agent-sdk.version>
    <scene-engine.version>2.3</scene-engine.version>
</properties>

<dependencies>
    <!-- scene-engine 依赖的 skills -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine</artifactId>
        <version>${scene-engine.version}</version>
    </dependency>
    
    <!-- agent-sdk 依赖的 skills -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk</artifactId>
        <version>${agent-sdk.version}</version>
    </dependency>
</dependencies>
```

**优点**:
- 15个 scene-engine 依赖的 skills 无需修改
- 快速升级

**缺点**:
- 无法使用 agent-sdk 2.3 新功能
- 技术债务

### 5.2 方案 B: 迁移到 agent-sdk 2.3 (推荐长期)

**适用**: 需要使用 AI 能力 (skill-ai)

**迁移步骤**:

#### 步骤1: 更新 pom.xml
```xml
<properties>
    <agent-sdk.version>2.3</agent-sdk.version>
    <llm-sdk.version>2.3</llm-sdk.version>
</properties>

<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk</artifactId>
        <version>${agent-sdk.version}</version>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <version>${llm-sdk.version}</version>
        <optional>true</optional>
    </dependency>
    <!-- 新增 AI 能力 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skill-ai</artifactId>
        <version>2.3</version>
        <optional>true</optional>
    </dependency>
</dependencies>
```

#### 步骤2: 更新 Import
```java
// 旧 (scene-engine)
import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.SecurityProvider;
import net.ooder.scene.skill.LlmProvider;

// 新 (agent-sdk 2.3)
import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SceneGroupManager;
import net.ooder.sdk.api.security.SecurityApi;
import net.ooder.sdk.api.llm.LlmService;
```

#### 步骤3: 适配 Provider 接口
```java
// 旧
@Component
public class SecurityProviderImpl implements SecurityProvider {
    @Override
    public void initialize(SceneEngine engine) { }
}

// 新
@Component
public class SecurityServiceImpl implements SecurityApi {
    @Override
    public void initialize(SceneGroupManager manager) { }
}
```

#### 步骤4: 使用 AI 能力 (可选)
```java
@Autowired
private AISkill aiSkill;

// 文本生成
AIGCResult result = aiSkill.generateText("gpt-4", "Hello", params).join();

// MCP 工具调用
MCPConfig config = new MCPConfig();
config.setClientId("my-client");
aiSkill.registerMCPClient("my-client", config);
MCPResult result = aiSkill.callMCPTool("my-client", "search", params).join();

// 工作流执行
WorkflowDefinition workflow = new WorkflowDefinition();
workflow.setWorkflowId("my-workflow");
aiSkill.registerWorkflow(workflow);
WorkflowResult result = aiSkill.executeWorkflow("my-workflow", params).join();
```

### 5.3 方案 C: 混合方案 (推荐)

**策略**:
1. **Phase 1** (1周): 升级到 scene-engine 2.3，保持现状
2. **Phase 2** (2周): 新 skills 使用 agent-sdk 2.3
3. **Phase 3** (1个月): 逐个迁移现有 skills
4. **Phase 4** (3个月): 完全迁移到 agent-sdk 2.3

---

## 六、场景配置示例

### 6.1 完整场景配置

```yaml
# scene.yaml - 文件共享场景
apiVersion: scene.ooder.net/v1
kind: Scene

metadata:
  id: scene-file-sharing
  name: 文件共享场景
  version: 1.0.0

spec:
  description: 企业文件共享与协作场景
  
  # 依赖的 skills
  skills:
    - id: skill-vfs-web
      version: ">=2.3.0"
      deployment: standalone
      
    - id: skill-org-web
      version: ">=2.3.0"
      deployment: embedded
      
    - id: skill-vfs-database
      version: ">=2.3.0"
      condition: "${vfs.provider} == 'database'"
      
    - id: skill-ai
      version: ">=2.3.0"
      optional: true
      description: AI 辅助文件分析
  
  # 能力配置
  capabilities:
    vfs:
      provider: database
      config:
        db.provider: h2
        
    org:
      provider: dingding
      fallback: local-json
      
    ai:
      enabled: true
      models:
        - gpt-4
        - deepseek-chat
  
  # 迁移配置
  migration:
    enabled: true
    autoInit: true
    providers:
      - id: h2
        type: embedded
        priority: 100
```

### 6.2 Skill 生命周期配置

```yaml
# skill.yaml - VFS Database Skill
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-vfs-database
  version: 2.3.0

spec:
  type: driver-skill
  
  lifecycle:
    # 发现阶段
    discovery:
      enabled: true
      catalog: vfs-drivers
      
    # 安装阶段
    installation:
      dependencies:
        - skill-database >=2.3.0
      autoInit: true
      
    # 启动阶段
    startup:
      order: 100
      healthCheck:
        enabled: true
        interval: 30s
        
    # 升级阶段
    upgrade:
      strategy: rolling
      rollbackOnFailure: true
      
    # 销毁阶段
    destruction:
      gracefulShutdown: true
      timeout: 30s
  
  migration:
    sources:
      - id: skill-vfs-local
        version: ">=2.0.0"
        autoMigrate: false
```

---

## 七、迁移检查清单

### 7.1 升级前检查

- [ ] 备份现有代码
- [ ] 检查所有 skills 的依赖关系
- [ ] 确认 scene-engine 2.3 可用
- [ ] 确认 agent-sdk 2.3 可用
- [ ] 确认 llm-sdk 2.3 可用

### 7.2 升级中检查

- [ ] 更新根 pom.xml 版本号
- [ ] 更新所有 skill pom.xml
- [ ] 编译通过
- [ ] 单元测试通过
- [ ] 集成测试通过

### 7.3 升级后检查

- [ ] 场景启动正常
- [ ] Skills 注册成功
- [ ] CAP 调用正常
- [ ] 事件处理正常
- [ ] 监控数据正常

---

## 八、API 符合度评估

### 8.1 scene-engine 2.3 API

| API | 稳定性 | 兼容性 | 说明 |
|-----|--------|--------|------|
| Result | ✅ 稳定 | 100% | 无变更 |
| PageResult | ✅ 稳定 | 100% | 无变更 |
| SceneEngine | ✅ 稳定 | 100% | 无变更 |
| BaseProvider | ✅ 稳定 | 100% | 无变更 |
| *Provider | ✅ 稳定 | 100% | 无变更 |
| LlmProvider | ✅ 稳定 | 100% | 无变更 |

**结论**: scene-engine 2.3 与 0.8.0 API 完全兼容

### 8.2 agent-sdk 2.3 API

| API | 稳定性 | 兼容性 | 说明 |
|-----|--------|--------|------|
| OoderSDK | ✅ 稳定 | 100% | 无变更 |
| EndAgent | ✅ 稳定 | 100% | 无变更 |
| SceneAgent | ✅ 稳定 | 100% | 无变更 |
| LlmService | ✅ 稳定 | 100% | 无变更 |
| SecurityApi | ✅ 稳定 | 100% | 无变更 |
| **AISkill** | 🆕 新增 | - | 新增 AI 能力 |
| **MCP** | 🆕 新增 | - | 新增 MCP 支持 |
| **Workflow** | 🆕 新增 | - | 新增工作流 |

**结论**: agent-sdk 2.3 向后兼容，新增 AI 能力

### 8.3 总体符合度

| 模块 | 符合度 | 状态 |
|------|--------|------|
| scene-engine 2.3 | 100% | ✅ 完全兼容 |
| agent-sdk 2.3 | 100% | ✅ 向后兼容 |
| llm-sdk 2.3 | 100% | ✅ 完全兼容 |
| **总体** | **100%** | **✅ 可升级** |

---

## 九、执行计划

### 9.1 立即执行 (今天)

1. 统一 pom.xml 版本到 2.3
2. 编译验证
3. 提交代码

### 9.2 本周完成

1. 升级所有 skills 到 2.3
2. 验证场景运行
3. 更新文档

### 9.3 本月完成

1. 评估 AI 能力集成
2. 新 skills 使用 agent-sdk 2.3
3. 制定迁移计划

---

## 十、问题反馈

如有问题，请联系:
- **GitHub Issues**: https://github.com/oodercn/ooder-skills/issues
- **邮箱**: team@ooder.net

---

**文档版本**: 1.0  
**最后更新**: 2026-02-24
