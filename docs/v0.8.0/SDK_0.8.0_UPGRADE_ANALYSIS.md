# SDK 0.8.0 升级对比分析

## 一、版本信息

| 项目 | 当前版本 | 目标版本 |
|------|----------|----------|
| agent-sdk | 0.7.3 | 0.8.0 |
| Spring Boot | 2.7.0 | 2.7.0 (不变) |

---

## 二、SDK 0.8.0 新增/变更 API

### 2.1 核心 API (net.ooder.sdk.api)

| 模块 | 类 | 状态 | 说明 |
|------|-----|------|------|
| **agent** | Agent, EndAgent, McpAgent, RouteAgent, SceneAgent, WorkerAgent | ✅ 保留 | Agent 体系完善 |
| **capability** | CapabilityRequestApi | 🆕 新增 | 能力请求 API |
| **cmd** | CmdClientProxy | ✅ 保留 | 命令客户端 |
| **event** | EventBus, Event, EventHandler | ✅ 保留 | 事件总线 |
| **initializer** | NexusInitializer | 🆕 新增 | 初始化器 |
| **llm** | LlmService, ChatRequest, FunctionDef | ✅ 保留 | LLM 服务 |
| **memory** | MemoryBridgeApi, NlpInteractionApi | 🆕 新增 | 记忆/交互 API |
| **metadata** | FourDimensionMetadata, ChangeLogService | 🆕 新增 | 元数据服务 |
| **monitoring** | MonitoringApi | 🆕 新增 | 监控 API |
| **msg** | MsgClientProxy | ✅ 保留 | 消息客户端 |
| **network** | NetworkService, LinkInfo | ✅ 保留 | 网络服务 |
| **protocol** | ProtocolHub, CommandPacket | ✅ 保留 | 协议中心 |
| **scene** | SceneManager, SceneGroupManager, SceneStore | ✅ 增强 | 场景管理增强 |
| **scheduler** | TaskScheduler | ✅ 保留 | 任务调度 |
| **scheduling** | SchedulingApi | 🆕 新增 | 调度 API |
| **security** | SecurityApi, EncryptionService | ✅ 增强 | 安全服务增强 |
| **share** | SkillShareService | 🆕 新增 | 技能共享服务 |
| **skill** | SkillService, SkillInstaller, SkillManifest | ✅ 增强 | 技能服务增强 |
| **storage** | StorageService | ✅ 保留 | 存储服务 |

### 2.2 基础设施 (net.ooder.sdk.infra)

| 模块 | 类 | 状态 |
|------|-----|------|
| async | AsyncExecutor, AsyncTask | ✅ 保留 |
| config | SDKConfiguration, ConfigLoader | ✅ 保留 |
| exception | SDKException, SceneException, SkillException | ✅ 增强 |
| lifecycle | LifecycleManager | ✅ 保留 |
| observer | ConfigObserver | ✅ 保留 |
| retry | RetryManager, RetryStrategy | ✅ 保留 |
| utils | JsonUtils, FileUtils, NetUtils | ✅ 保留 |

---

## 三、Skills 兼容性分析

### 3.1 兼容的 Skills (可直接升级)

| Skill | 状态 | 说明 |
|-------|------|------|
| skill-common | ✅ 兼容 | 无 SDK 依赖 |
| skill-k8s | ✅ 兼容 | 无 SDK 依赖 |
| skill-scheduler-quartz | ✅ 兼容 | 无 SDK 依赖 |
| skill-market | ✅ 兼容 | 无 SDK 依赖 |
| skill-im | ✅ 兼容 | 无 SDK 依赖 |
| skill-group | ✅ 兼容 | 无 SDK 依赖 |
| skill-business | ✅ 兼容 | 无 SDK 依赖 |
| skill-msg | ✅ 兼容 | 无 SDK 依赖 |
| skill-collaboration | ✅ 兼容 | 无 SDK 依赖 |
| skill-mqtt | ✅ 兼容 | 无 SDK 依赖 |
| skill-hosting | ✅ 兼容 | 无 SDK 依赖 |
| skill-monitor | ✅ 兼容 | 无 SDK 依赖 |
| skill-security | ✅ 兼容 | 无 SDK 依赖 |
| skill-network | ✅ 兼容 | 无 SDK 依赖 |
| skill-vfs-local | ✅ 兼容 | 无 SDK 依赖 |

### 3.2 需要重构的 Skills

| Skill | 问题 | 需要修改 |
|-------|------|----------|
| **skill-a2ui** | 使用 `OoderSDK`, `EndAgent`, `SDKConfiguration`, `LifecycleManager` | ✅ 兼容 (API 保留) |
| **skill-org-dingding** | 依赖旧 API: `Org`, `Person`, `Role`, `ResultModel`, `User`, `OrgManager` | ❌ 需重构 |
| **skill-org-feishu** | 依赖旧 API: `Org`, `Person`, `Role`, `ResultModel`, `User`, `OrgManager`, `JDSException` | ❌ 需重构 |
| **skill-user-auth** | 使用 `OoderSDK`, `EndAgent`, `SDKConfiguration`, `LifecycleManager` | ✅ 兼容 (API 保留) |

---

## 四、已移除/变更的旧 API

### 4.1 已移除的包

| 旧包路径 | 说明 | 替代方案 |
|----------|------|----------|
| `net.ooder.org` | 组织模型 | 使用自定义 DTO |
| `net.ooder.annotation` | 注解 | 使用 Spring 注解 |
| `net.ooder.common` | 公共工具 | 使用 `net.ooder.sdk.infra.utils` |
| `net.ooder.config` | 配置 | 使用 `net.ooder.sdk.infra.config` |
| `net.ooder.jds.core` | JDS 核心 | 使用 `net.ooder.sdk.api.storage` |
| `net.ooder.msg` | 消息 | 使用 `net.ooder.sdk.api.msg` |
| `net.ooder.server` | 服务端 | 使用 `net.ooder.sdk.api` |
| `net.ooder.engine` | 引擎 | 使用 `net.ooder.sdk.api` |

### 4.2 已移除的类

| 旧类 | 说明 | 替代方案 |
|------|------|----------|
| `Org` | 组织实体 | 自定义 `OrgDTO` |
| `Person` | 人员实体 | 自定义 `PersonDTO` |
| `Role` | 角色实体 | 自定义 `RoleDTO` |
| `User` | 用户实体 | 自定义 `UserDTO` |
| `ResultModel` | 结果模型 | 使用 `Map<String, Object>` 或自定义 DTO |
| `OrgManager` | 组织管理器 | 使用 `net.ooder.sdk.api.scene.SceneGroupManager` |
| `UserService` | 用户服务 | 使用 `net.ooder.sdk.api.security.SecurityApi` |
| `JDSException` | JDS 异常 | 使用 `net.ooder.sdk.infra.exception.SDKException` |
| `ConfigCode` | 配置码 | 使用 `net.ooder.sdk.infra.config.SDKConfiguration` |

---

## 五、升级建议

### 5.1 立即可升级

以下 Skills 可以直接升级到 SDK 0.8.0：

```
skill-common, skill-k8s, skill-scheduler-quartz, skill-market, 
skill-im, skill-group, skill-business, skill-msg, skill-collaboration,
skill-mqtt, skill-hosting, skill-monitor, skill-security, skill-network,
skill-vfs-local, skill-a2ui, skill-user-auth
```

### 5.2 需要重构

以下 Skills 需要重构后才能升级：

#### skill-org-dingding

```java
// 旧代码
import net.ooder.org.Org;
import net.ooder.org.Person;
import net.ooder.org.Role;
import net.ooder.common.ResultModel;

// 新代码
// 创建自定义 DTO
@Data
public class OrgDTO {
    private String orgId;
    private String orgName;
    private String parentId;
    // ...
}

// 或使用 SDK 提供的 API
import net.ooder.sdk.api.scene.SceneMember;
import net.ooder.sdk.api.security.SecurityApi;
```

#### skill-org-feishu

同上，需要移除旧 API 依赖，使用自定义 DTO 或 SDK 0.8.0 新 API。

---

## 六、升级步骤

### 步骤 1: 更新 pom.xml

```xml
<properties>
    <agent-sdk.version>0.8.0</agent-sdk.version>
</properties>
```

### 步骤 2: 编译验证

```bash
mvn compile -pl skills/skill-a2ui,skills/skill-user-auth
```

### 步骤 3: 重构不兼容的 Skills

对于 `skill-org-dingding` 和 `skill-org-feishu`：

1. 移除旧 API import
2. 创建自定义 DTO 类
3. 使用 SDK 0.8.0 新 API 替代
4. 更新业务逻辑

---

## 七、总结

| 指标 | 数量 |
|------|------|
| 总 Skills 数 | 22 |
| 兼容 Skills | 20 |
| 需重构 Skills | 2 |
| 兼容率 | 91% |

**建议**: 
1. 先升级兼容的 20 个 Skills
2. 后续重构 `skill-org-dingding` 和 `skill-org-feishu`
