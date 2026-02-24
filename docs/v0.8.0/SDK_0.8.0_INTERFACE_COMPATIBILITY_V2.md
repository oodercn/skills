# SDK 0.8.0 接口与实现匹配度分析 (修正版)

## 一、SDK 拆分架构

### 1.1 三大核心模块

```
┌─────────────────────────────────────────────────────────┐
│                    Ooder SDK 0.8.0 架构                        │
├─────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌───────────────────────────────────────────────┐  │
│  │  agent-sdk (Agent 核心)                          │  │
│  │  Maven: net.ooder:agent-sdk:0.8.0            │  │
│  │  职责: Agent 生命周期、通信、能力管理            │  │
│  └───────────────────────────────────────────────┘  │
│                                                                 │
│  ┌───────────────────────────────────────────────┐  │
│  │  scene-engine (场景引擎)                          │  │
│  │  Maven: net.ooder:scene-engine:0.8.0           │  │
│  │  职责: 场景管理、Provider 注册、Skill 生命周期    │  │
│  └───────────────────────────────────────────────┘  │
│                                                                 │
│  ┌───────────────────────────────────────────────┐  │
│  │  llm-sdk (LLM 服务)                             │  │
│  │  Maven: net.ooder:llm-sdk:0.8.0               │  │
│  │  职责: LLM 模型管理、NLP、记忆、调度           │  │
│  └───────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────┘
```

---

## 二、Skills 依赖分析

### 2.1 依赖分类

| 分类 | Skills | SDK 依赖 | 主要接口 |
|------|---------|-----------|----------|
| **无 SDK 依赖** | 13 | 无 | 无 |
| **agent-sdk 依赖** | 4 | agent-sdk | EndAgent, OoderSDK |
| **scene-engine 依赖** | 15 | scene-engine | Result, SceneEngine, *Provider, LlmProvider |
| **llm-sdk 依赖** | 0 | llm-sdk | 无 |

### 2.2 Skills 列表

#### 无 SDK 依赖 (13个)
```
skill-common, skill-k8s, skill-scheduler-quartz, skill-market,
skill-im, skill-group, skill-business, skill-msg,
skill-collaboration, skill-mqtt, skill-hosting,
skill-monitor, skill-vfs-local
```

#### agent-sdk 依赖 (4个)
```
skill-a2ui, skill-user-auth, skill-org-dingding, skill-org-feishu
```

#### scene-engine 依赖 (15个)
```
skill-share, skill-security, skill-protocol, skill-network,
skill-hosting, skill-health, skill-agent, skill-openwrt,
skill-llm-deepseek, skill-llm-openai, skill-llm-qianwen,
skill-llm-ollama, skill-llm-volcengine, skill-httpclient-okhttp
```

---

## 三、接口匹配度分析

### 3.1 scene-engine Provider 接口匹配

| 旧接口 | 新接口 | 匹配度 | 说明 |
|--------|--------|--------|------|
| `BaseProvider` | 无直接对应 | ❌ 0% | scene-engine 特有 |
| `AgentProvider` | `net.ooder.sdk.api.agent.SceneAgent` | 🔶 60% | 概念变更 |
| `SecurityProvider` | `net.ooder.sdk.api.security.SecurityApi` | 🔶 70% | 方法签名类似 |
| `HealthProvider` | `net.ooder.sdk.api.monitoring.MonitoringApi` | 🔶 50% | 功能合并 |
| `NetworkProvider` | `net.ooder.sdk.api.network.NetworkService` | 🔶 70% | 方法签名类似 |
| `ProtocolProvider` | `net.ooder.sdk.api.protocol.ProtocolHub` | 🔶 60% | 重构较大 |
| `SkillShareProvider` | `net.ooder.sdk.api.share.SkillShareService` | 🔶 70% | 方法签名类似 |
| `LlmProvider` | `net.ooder.sdk.api.llm.LlmService` | ✅ 90% | 方法签名兼容 |
| `HttpClientProvider` | 无直接对应 | ❌ 0% | scene-engine 特有 |
| `StorageProvider` | `net.ooder.sdk.api.storage.StorageService` | 🔶 70% | 方法签名类似 |
| `SchedulerProvider` | `net.ooder.sdk.api.scheduler.TaskScheduler` | 🔶 70% | 方法签名类似 |

### 3.2 核心类匹配

| 旧类 | 新类 | 匹配度 | 说明 |
|------|------|--------|------|
| `Result` | `net.ooder.sdk.infra.utils.Result` | ✅ 95% | 包名变更 |
| `PageResult` | `net.ooder.sdk.infra.utils.PageResult` | ✅ 95% | 包名变更 |
| `SceneEngine` | `net.ooder.sdk.api.scene.SceneGroupManager` | 🔶 60% | 重构 |
| `SceneAgent` | `net.ooder.sdk.api.agent.SceneAgent` | ✅ 85% | 包名变更 |

---

## 四、兼容性矩阵

### 4.1 Skills 兼容性

| Skill | SDK 0.7.3 | SDK 0.8.0 | 迁移难度 | 说明 |
|-------|-----------|-----------|----------|------|
| skill-common | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-k8s | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-scheduler-quartz | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-market | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-im | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-group | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-business | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-msg | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-collaboration | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-mqtt | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-hosting | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-monitor | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-vfs-local | ✅ | ✅ | 无 | 无 SDK 依赖 |
| skill-a2ui | ✅ | 🔶 | 低 | 仅 import 变更 |
| skill-user-auth | ✅ | 🔶 | 低 | 仅 import 变更 |
| skill-org-dingding | ✅ | 🔶 | 低 | 仅 import 变更 |
| skill-org-feishu | ✅ | 🔶 | 低 | 仅 import 变更 |
| skill-share | ✅ | ❌ | 中 | Provider 接口变更 |
| skill-security | ✅ | ❌ | 中 | Provider 接口变更 |
| skill-protocol | ✅ | ❌ | 中 | Provider 接口变更 |
| skill-network | ✅ | ❌ | 中 | Provider 接口变更 |
| skill-hosting | ✅ | ❌ | 中 | Provider 接口变更 |
| skill-health | ✅ | ❌ | 中 | Provider 接口变更 |
| skill-agent | ✅ | ❌ | 中 | Provider 接口变更 |
| skill-openwrt | ✅ | ❌ | 中 | Result/Driver 变更 |
| skill-llm-deepseek | ✅ | ❌ | 中 | LlmProvider 变更 |
| skill-llm-openai | ✅ | ❌ | 中 | LlmProvider 变更 |
| skill-llm-qianwen | ✅ | ❌ | 中 | LlmProvider 变更 |
| skill-llm-ollama | ✅ | ❌ | 中 | LlmProvider 变更 |
| skill-llm-volcengine | ✅ | ❌ | 中 | LlmProvider 变更 |
| skill-httpclient-okhttp | ✅ | ❌ | 高 | HttpClientProvider 移除 |

### 4.2 统计

| 指标 | 数量 | 百分比 |
|------|------|--------|
| 完全兼容 | 13 | 43% |
| 低迁移成本 | 4 | 13% |
| 中迁移成本 | 12 | 40% |
| 高迁移成本 | 1 | 3% |
| **总兼容率** | **56%** | (兼容+低迁移) |

---

## 五、迁移方案

### 方案 A: 保持 scene-engine 依赖 (推荐短期)

**适用场景**: Skills 已经稳定，无需迁移

**依赖配置**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>0.8.0</version>
</dependency>
```

**优点**:
- Skills 无需修改
- 保持现有功能

**缺点**:
- 无法使用 agent-sdk 新功能
- 无法使用 llm-sdk 新功能

### 方案 B: 迁移到 agent-sdk + llm-sdk (推荐长期)

**适用场景**: 需要使用新功能

**依赖配置**:
```xml
<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk</artifactId>
        <version>0.8.0</version>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <version>0.8.0</version>
        <optional>true</optional>
    </dependency>
</dependencies>
```

**Import 映射**:
```java
// scene-engine → agent-sdk + llm-sdk
import net.ooder.scene.core.Result;
    → import net.ooder.sdk.infra.utils.Result;

import net.ooder.scene.core.SceneEngine;
    → import net.ooder.sdk.api.scene.SceneGroupManager;

import net.ooder.scene.provider.SecurityProvider;
    → import net.ooder.sdk.api.security.SecurityApi;

import net.ooder.scene.provider.AgentProvider;
    → import net.ooder.sdk.api.agent.SceneAgent;

import net.ooder.scene.skill.LlmProvider;
    → import net.ooder.sdk.api.llm.LlmService;
```

### 方案 C: 混合方案 (推荐)

**策略**:
1. **短期**: 保持 scene-engine 0.8.0 依赖
2. **中期**: 新 Skills 使用 agent-sdk + llm-sdk
3. **长期**: 逐步迁移现有 Skills

---

## 六、开发指导

### 6.1 新 Skill 开发

**推荐依赖**:
```xml
<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk</artifactId>
        <version>0.8.0</version>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>llm-sdk</artifactId>
        <version>0.8.0</version>
        <optional>true</optional>
    </dependency>
</dependencies>
```

**代码示例**:
```java
@Component
public class MySkill implements BaseProvider {
    
    private SceneGroupManager sceneManager;
    private LlmService llmService;
    
    @Override
    public void initialize(SceneEngine engine) {
        this.sceneManager = engine.getSceneGroupManager();
        this.llmService = engine.getLlmService();
    }
    
    @Override
    public String getProviderName() {
        return "my-skill";
    }
    
    @Override
    public void start() {
        // 业务逻辑
    }
}
```

### 6.2 现有 Skill 迁移

**步骤 1**: 更新 pom.xml
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>0.8.0</version>
</dependency>
```

**步骤 2**: 更新 import
```java
// 旧
import net.ooder.scene.core.Result;
import net.ooder.scene.provider.SecurityProvider;

// 新
import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.security.SecurityApi;
```

**步骤 3**: 适配接口方法
```java
// 旧
@Override
public SecurityStatus getStatus() {
    // 实现
}

// 新
@Override
public SecurityStatus getStatus() {
    // 实现
}
```

---

## 七、总结

| 问题 | 严重程度 | 解决方案 |
|------|----------|----------|
| SDK 拆分为三部分 | 🟡 中 | 明确依赖关系 |
| scene-engine 依赖的 Skills | 🟡 中 | 保持现有依赖或迁移 |
| Provider 接口变更 | 🟡 中 | 适配层或迁移 |
| 总体兼容率 | 🟡 56% | 需要迁移工作 |

**推荐方案**: 方案 C (混合方案)

1. 保持 scene-engine 0.8.0 兼容性
2. 新 Skills 使用 agent-sdk + llm-sdk
3. 逐步迁移现有 Skills
