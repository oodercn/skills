# SDK功能需求文档

## 文档信息

- 创建日期: 2026-02-27
- 文档版本: 1.0
- 关联项目: ooder-Nexus-Enterprise

## 概述

本文档记录前端页面已实现但SDK当前版本不支持的功能需求。这些功能需要SDK团队提供相应的API支持，以便Nexus Console能够完整运行。

---

## 一、连接测试功能

### 1.1 数据库连接测试

**需求描述**: 测试数据库连接是否可用

**前端调用位置**: 
- `config-editor.html` - 组织配置页面
- API: `/api/scene/config/test-connection`

**当前实现状态**: Mock实现

```java
// SceneConfigService.java - 当前是模拟实现
public ApiResponse<Map<String, Object>> testConnection(String type, String endpoint) {
    Thread.sleep(100);
    result.put("success", true);
    result.put("latency", (int)(Math.random() * 100) + 10);
}
```

**期望SDK接口**:

```java
public interface ConnectionTestService {
    CompletableFuture<ConnectionTestResult> testDatabaseConnection(DatabaseConfig config);
}

public class DatabaseConfig {
    private String driver;
    private String url;
    private String username;
    private String password;
}

public class ConnectionTestResult {
    private boolean success;
    private int latency;          // 延迟(ms)
    private String message;       // 结果消息
    private String serverVersion; // 数据库版本
    private String errorMessage;  // 错误信息(如果失败)
}
```

**优先级**: 高

---

### 1.2 MQTT连接测试

**需求描述**: 测试MQTT Broker连接是否可用

**前端调用位置**: 
- `config-editor.html` - 消息配置页面
- API: `/api/scene/config/test-connection`

**当前实现状态**: Mock实现

**期望SDK接口**:

```java
public interface ConnectionTestService {
    CompletableFuture<ConnectionTestResult> testMqttConnection(MqttConfig config);
}

public class MqttConfig {
    private String broker;
    private int port;
    private String clientId;
    private String username;
    private String password;
    private int qos;
}

public class MqttTestResult {
    private boolean success;
    private int latency;
    private String message;
    private String brokerInfo;
}
```

**优先级**: 高

---

### 1.3 能力端点连接测试

**需求描述**: 测试能力服务端点是否可达

**前端调用位置**: 
- `config-editor.html` - 能力配置弹窗
- API: `/api/scene/config/test-connection`

**当前实现状态**: Mock实现

**期望SDK接口**:

```java
public interface ConnectionTestService {
    CompletableFuture<ConnectionTestResult> testCapabilityEndpoint(CapabilityEndpoint endpoint);
}

public class CapabilityEndpoint {
    private String capabilityId;
    private String interfaceId;
    private String endpoint;
    private int timeout;
}
```

**优先级**: 中

---

## 二、性能监控功能

### 2.1 性能指标历史数据

**需求描述**: 获取场景运行时的性能指标历史数据

**前端调用位置**: 
- `dashboard.html` - 场景监控大屏
- API: `/api/scene/runtime/metrics`

**当前实现状态**: Mock实现，使用随机数据

```java
// SceneRuntimeService.java - 当前是模拟实现
for (int i = 20; i >= 0; i--) {
    h.setCpu(30 + Math.random() * 40);
    h.setMemory(50 + Math.random() * 30);
    h.setDisk(20 + Math.random() * 20);
    h.setNetworkIn(800 + Math.random() * 400);
}
```

**期望SDK接口**:

```java
public interface PerformanceMonitor {
    CompletableFuture<PerformanceHistory> getPerformanceHistory(
        String sceneId, 
        long startTime, 
        long endTime, 
        int interval
    );
}

public class PerformanceHistory {
    private String sceneId;
    private List<MetricPoint> cpuHistory;
    private List<MetricPoint> memoryHistory;
    private List<MetricPoint> diskHistory;
    private List<MetricPoint> networkInHistory;
    private List<MetricPoint> networkOutHistory;
}

public class MetricPoint {
    private long timestamp;
    private double value;
}
```

**优先级**: 中

---

### 2.2 实时性能指标

**需求描述**: 获取场景当前的性能指标

**前端调用位置**: 
- `dashboard.html` - 场景监控大屏
- API: `/api/scene/runtime/metrics`

**当前实现状态**: 部分实现，使用JVM运行时数据

**期望SDK接口**:

```java
public interface PerformanceMonitor {
    CompletableFuture<CurrentMetrics> getCurrentMetrics(String sceneId);
}

public class CurrentMetrics {
    private double cpuUsage;           // CPU使用率(%)
    private double memoryUsage;        // 内存使用率(%)
    private long memoryUsed;           // 已用内存(bytes)
    private long memoryTotal;          // 总内存(bytes)
    private double diskUsage;          // 磁盘使用率(%)
    private double networkInRate;      // 网络入速率(KB/s)
    private double networkOutRate;     // 网络出速率(KB/s)
    private int threadCount;           // 线程数
    private int connectionCount;       // 连接数
    private long totalRequests;        // 总请求数
    private double qps;                // QPS
    private double avgLatency;         // 平均延迟(ms)
    private long errorCount;           // 错误数
}
```

**优先级**: 中

---

## 三、流程管理功能

### 3.1 场景启动流程跟踪

**需求描述**: 跟踪场景启动的详细流程和进度

**前端调用位置**: 
- `flow-tracker.html` - 流程跟踪页面
- API: `/api/scene/flow/start`, `/api/scene/flow/status`

**当前实现状态**: 完全Mock实现

```java
// SceneService.java - 当前是模拟实现
private void simulateFlowProgress(SceneFlowStatus flow) {
    progress += 5;  // 模拟进度增加
}
```

**期望SDK接口**:

```java
public interface SceneFlowManager {
    CompletableFuture<FlowStatus> startSceneWithFlow(String sceneId);
    CompletableFuture<FlowStatus> getFlowStatus(String flowId);
    CompletableFuture<List<FlowStep>> getFlowSteps(String flowId);
}

public class FlowStatus {
    private String flowId;
    private String sceneId;
    private String status;        // running, completed, failed, paused
    private int progress;         // 0-100
    private long startTime;
    private long estimatedEndTime;
    private List<FlowStep> steps;
}

public class FlowStep {
    private int id;
    private String title;
    private String description;
    private String status;        // pending, running, completed, failed
    private long startTime;
    private long endTime;
    private List<FlowSubstep> substeps;
}

public class FlowSubstep {
    private String name;
    private String status;
    private String message;
}
```

**流程步骤定义**:

| 步骤 | 名称 | 描述 |
|------|------|------|
| 1 | 配置加载 | 加载场景配置文件 |
| 2 | 基础服务初始化 | 初始化ORG/VFS/MSG/JDS基础服务 |
| 3 | 扩展服务初始化 | 初始化扩展服务组件 |
| 4 | 能力注册 | 注册场景能力到能力注册中心 |
| 5 | 场景就绪 | 场景启动完成，进入运行状态 |

**优先级**: 高

---

### 3.2 流程暂停/恢复/回滚

**需求描述**: 控制场景启动流程的执行

**前端调用位置**: 
- `flow-tracker.html` - 流程跟踪页面
- API: `/api/scene/flow/pause`, `/api/scene/flow/resume`, `/api/scene/flow/rollback`

**当前实现状态**: 仅修改内存状态，未调用SDK

```java
// SceneService.java - 当前实现
public ApiResponse<Boolean> pauseFlow(String flowId) {
    flow.setStatus("paused");  // 仅修改内存
    return ApiResponse.success(true);
}
```

**期望SDK接口**:

```java
public interface SceneFlowManager {
    CompletableFuture<Boolean> pauseFlow(String flowId);
    CompletableFuture<Boolean> resumeFlow(String flowId);
    CompletableFuture<Boolean> rollbackFlow(String flowId);
}
```

**优先级**: 中

---

### 3.3 流程日志

**需求描述**: 获取场景启动流程的详细日志

**前端调用位置**: 
- `flow-tracker.html` - 流程跟踪页面
- API: `/api/scene/flow/logs`

**当前实现状态**: Mock实现，返回固定日志

**期望SDK接口**:

```java
public interface SceneFlowManager {
    CompletableFuture<List<FlowLog>> getFlowLogs(String flowId, int limit);
}

public class FlowLog {
    private long timestamp;
    private String level;     // INFO, DEBUG, WARN, ERROR
    private String message;
    private String step;      // 所属步骤
    private Map<String, Object> details;
}
```

**优先级**: 中

---

## 四、配置管理功能

### 4.1 配置历史

**需求描述**: 获取场景配置的历史版本

**前端调用位置**: 
- `config-history.html` - 配置历史页面
- API: `/api/scene/config/history`

**当前实现状态**: Mock实现，生成假数据

**期望SDK接口**:

```java
public interface SceneConfigManager {
    CompletableFuture<ConfigHistory> getConfigHistory(String sceneId);
}

public class ConfigHistory {
    private String sceneId;
    private List<ConfigVersion> versions;
}

public class ConfigVersion {
    private int version;
    private long createdAt;
    private String createdBy;
    private String description;
    private String changeType;    // CREATE, UPDATE, DELETE
    private String configContent; // 配置内容(JSON/YAML)
}
```

**优先级**: 低

---

### 4.2 配置回滚

**需求描述**: 将场景配置回滚到指定版本

**前端调用位置**: 
- `config-history.html` - 配置历史页面
- API: `/api/scene/config/rollback`

**当前实现状态**: Mock实现，仅返回成功

**期望SDK接口**:

```java
public interface SceneConfigManager {
    CompletableFuture<Boolean> rollbackConfig(String sceneId, int version);
}
```

**优先级**: 低

---

### 4.3 配置导入导出

**需求描述**: 导入导出场景配置

**前端调用位置**: 
- `config-editor.html` - 配置编辑器页面
- API: `/api/scene/config/export`, `/api/scene/config/import`

**当前实现状态**: Mock实现

**期望SDK接口**:

```java
public interface SceneConfigManager {
    CompletableFuture<String> exportConfig(String sceneId, String format);  // format: json, yaml
    CompletableFuture<Boolean> importConfig(String sceneId, String configContent, String format);
}
```

**优先级**: 低

---

## 五、事件和日志功能

### 5.1 运行时事件

**需求描述**: 获取场景运行时产生的事件

**前端调用位置**: 
- `dashboard.html` - 场景监控大屏
- API: `/api/scene/runtime/events`

**当前实现状态**: Mock实现，返回固定事件

**期望SDK接口**:

```java
public interface SceneEventManager {
    CompletableFuture<List<SceneEvent>> getEvents(String sceneId, int limit);
    CompletableFuture<List<SceneEvent>> getEventsByLevel(String sceneId, String level, int limit);
}

public class SceneEvent {
    private String eventId;
    private String sceneId;
    private long timestamp;
    private String type;       // SCENE_START, SERVICE_READY, CAPABILITY_REGISTER, etc.
    private String level;      // INFO, WARNING, ERROR
    private String message;
    private String source;     // 事件来源
    private Map<String, Object> details;
}
```

**优先级**: 中

---

### 5.2 运行时日志

**需求描述**: 获取场景运行时的日志

**前端调用位置**: 
- `dashboard.html` - 场景监控大屏
- API: `/api/scene/runtime/logs`

**当前实现状态**: Mock实现

**期望SDK接口**:

```java
public interface SceneLogManager {
    CompletableFuture<List<SceneLog>> getLogs(String sceneId, String level, int limit);
    CompletableFuture<List<SceneLog>> searchLogs(String sceneId, String keyword, int limit);
}

public class SceneLog {
    private long timestamp;
    private String level;
    private String message;
    private String sceneId;
    private String thread;
    private String logger;
    private String stackTrace;  // 如果是ERROR级别
}
```

**优先级**: 中

---

## 六、服务状态功能

### 6.1 服务健康状态

**需求描述**: 获取场景中各服务的健康状态

**前端调用位置**: 
- `dashboard.html` - 场景监控大屏
- API: `/api/scene/runtime/status`

**当前实现状态**: 部分实现，从活跃场景列表推断

**期望SDK接口**:

```java
public interface ServiceHealthMonitor {
    CompletableFuture<Map<String, ServiceHealth>> getServicesHealth(String sceneId);
}

public class ServiceHealth {
    private String serviceId;
    private String serviceName;
    private String type;        // ORG, VFS, MSG, JDS
    private String status;      // ACTIVE, WARNING, ERROR, STOPPED
    private long uptime;
    private int errorCount;
    private String lastError;
    private HealthCheckResult lastCheck;
}

public class HealthCheckResult {
    private long checkTime;
    private boolean healthy;
    private String message;
    private Map<String, Object> details;
}
```

**优先级**: 高

---

## 七、能力状态功能

### 7.1 能力运行状态

**需求描述**: 获取场景中各能力的运行状态

**前端调用位置**: 
- `dashboard.html` - 场景监控大屏
- API: `/api/scene/runtime/capabilities`

**当前实现状态**: 部分实现，从能力列表推断状态

**期望SDK接口**:

```java
public interface CapabilityStatusMonitor {
    CompletableFuture<Map<String, CapabilityStatus>> getCapabilitiesStatus(String sceneId);
}

public class CapabilityStatus {
    private String capabilityId;
    private String capabilityName;
    private String interfaceId;
    private String status;        // ACTIVE, INACTIVE, ERROR
    private long invokeCount;     // 调用次数
    private long errorCount;      // 错误次数
    private double successRate;   // 成功率(%)
    private double avgLatency;    // 平均延迟(ms)
    private long lastInvokeTime;  // 最后调用时间
}
```

**优先级**: 中

---

## 需求优先级汇总

### 高优先级

| 编号 | 功能 | 前端页面 | 影响 |
|------|------|---------|------|
| 1.1 | 数据库连接测试 | config-editor.html | 用户无法验证数据库配置 |
| 1.2 | MQTT连接测试 | config-editor.html | 用户无法验证消息配置 |
| 3.1 | 场景启动流程跟踪 | flow-tracker.html | 无法看到真实启动进度 |
| 6.1 | 服务健康状态 | dashboard.html | 无法看到真实服务状态 |

### 中优先级

| 编号 | 功能 | 前端页面 | 影响 |
|------|------|---------|------|
| 1.3 | 能力端点连接测试 | config-editor.html | 无法验证能力配置 |
| 2.1 | 性能指标历史数据 | dashboard.html | 显示假数据 |
| 2.2 | 实时性能指标 | dashboard.html | 显示JVM数据而非场景数据 |
| 3.2 | 流程暂停/恢复/回滚 | flow-tracker.html | 操作无效 |
| 3.3 | 流程日志 | flow-tracker.html | 显示假日志 |
| 5.1 | 运行时事件 | dashboard.html | 显示假事件 |
| 5.2 | 运行时日志 | dashboard.html | 显示假日志 |
| 7.1 | 能力运行状态 | dashboard.html | 状态不准确 |

### 低优先级

| 编号 | 功能 | 前端页面 | 影响 |
|------|------|---------|------|
| 4.1 | 配置历史 | config-history.html | 显示假历史 |
| 4.2 | 配置回滚 | config-history.html | 操作无效 |
| 4.3 | 配置导入导出 | config-editor.html | 功能不完整 |

---

## 接口设计建议

### 建议新增的SDK接口

```java
package net.ooder.sdk.api;

public interface SceneMonitor {
    ConnectionTestService getConnectionTestService();
    PerformanceMonitor getPerformanceMonitor();
    SceneFlowManager getFlowManager();
    SceneConfigManager getConfigManager();
    SceneEventManager getEventManager();
    SceneLogManager getLogManager();
    ServiceHealthMonitor getServiceHealthMonitor();
    CapabilityStatusMonitor getCapabilityStatusMonitor();
}
```

### 与现有SDK的集成

建议在 `SceneManager` 中添加获取监控接口的方法：

```java
public interface SceneManager {
    // 现有方法...
    
    SceneMonitor getMonitor(String sceneId);
}
```

---

## 附录：当前Mock实现清单

| 文件 | 方法 | Mock类型 |
|------|------|---------|
| SceneConfigService.java | testConnection() | 固定返回成功+随机延迟 |
| SceneConfigService.java | getHistory() | 生成假历史数据 |
| SceneConfigService.java | rollback() | 仅返回成功 |
| SceneConfigService.java | export() | 生成简单YAML |
| SceneConfigService.java | importConfig() | 仅返回成功 |
| SceneRuntimeService.java | getMetrics() | JVM数据+随机历史 |
| SceneRuntimeService.java | getEvents() | 固定事件列表 |
| SceneRuntimeService.java | getLogs() | 固定日志列表 |
| SceneService.java | startFlow() | 内存模拟 |
| SceneService.java | simulateFlowProgress() | 模拟进度增加 |
| SceneService.java | getFlowLogs() | 固定日志 |
| SceneService.java | pauseFlow() | 仅修改内存状态 |
| SceneService.java | resumeFlow() | 仅修改内存状态 |
| SceneService.java | rollbackFlow() | 仅修改内存状态 |

---

## 附录二：缺失的后台API Controller

以下前端页面调用的API在后台没有对应的Controller实现，需要新建Controller或确认是否为规划中的功能。

### 安全管理模块

| API路径 | 前端页面 | 状态 |
|---------|---------|------|
| `/api/enexus/security/token/list` | tokens.html | ✅ SecurityController |
| `/api/enexus/security/token/generate` | tokens.html | ✅ SecurityController |
| `/api/enexus/security/token/get` | tokens.html | ✅ SecurityController |
| `/api/enexus/security/token/revoke` | tokens.html | ✅ SecurityController |
| `/api/enexus/security/user-key/list` | user-keys.html | ✅ SecurityController |
| `/api/enexus/security/user-key/generate` | user-keys.html | ✅ SecurityController |
| `/api/enexus/security/user-key/get` | user-keys.html | ✅ SecurityController |
| `/api/enexus/security/user-key/revoke` | user-keys.html | ✅ SecurityController |
| `/api/enexus/security/scene-group-key/list` | scene-group-keys.html | ✅ SecurityController |
| `/api/enexus/security/scene-group-key/generate` | scene-group-keys.html | ✅ SecurityController |
| `/api/enexus/security/scene-group-key/get` | scene-group-keys.html | ✅ SecurityController |
| `/api/enexus/security/scene-group-key/rotate` | scene-group-keys.html | ✅ SecurityController |
| `/api/enexus/security/domain-key/list` | domain-keys.html | ✅ SecurityController |
| `/api/enexus/security/domain-key/generate` | domain-keys.html | ✅ SecurityController |
| `/api/enexus/security/domain-key/get` | domain-keys.html | ✅ SecurityController |
| `/api/enexus/security/domain-key/rotate` | domain-keys.html | ✅ SecurityController |
| `/api/enexus/security/certificate/list` | certificates.html | ✅ SecurityController |
| `/api/enexus/security/certificate/generate` | certificates.html | ✅ SecurityController |
| `/api/enexus/security/certificate/get` | certificates.html | ✅ SecurityController |
| `/api/enexus/security/certificate/revoke` | certificates.html | ✅ SecurityController |

### Nexus实例管理

| API路径 | 前端页面 | 状态 |
|---------|---------|------|
| `/api/enexus/nexus/list` | nexus-instances.html | ✅ NexusInstanceController |
| `/api/enexus/nexus/get` | nexus-instances.html | ✅ NexusInstanceController |

### MCP代理管理

| API路径 | 前端页面 | 状态 |
|---------|---------|------|
| `/api/enexus/mcp/list` | mcp-agents.html | ✅ McpAgentController |
| `/api/enexus/mcp/heartbeat/stats` | mcp-agents.html | ✅ McpAgentController |
| `/api/enexus/mcp/get` | mcp-agents.html | ✅ McpAgentController |
| `/api/enexus/mcp/toggle` | mcp-agents.html | ✅ McpAgentController |

### 通信管理

| API路径 | 前端页面 | 状态 |
|---------|---------|------|
| `/api/enexus/relay/command/send` | relay-commands.html | ✅ CommunicationController |
| `/api/enexus/p2p/monitor/list` | p2p-monitor.html | ✅ CommunicationController |
| `/api/enexus/p2p/monitor/get` | p2p-monitor.html | ✅ CommunicationController |
| `/api/enexus/p2p/monitor/close` | p2p-monitor.html | ✅ CommunicationController |
| `/api/enexus/permission/rule/list` | permission-rules.html | ✅ CommunicationController |
| `/api/enexus/permission/rule/create` | permission-rules.html | ✅ CommunicationController |
| `/api/enexus/permission/rule/get` | permission-rules.html | ✅ CommunicationController |
| `/api/enexus/permission/rule/toggle` | permission-rules.html | ✅ CommunicationController |
| `/api/enexus/p2p/approval/list` | p2p-approval.html | ✅ CommunicationController |
| `/api/enexus/p2p/approval/approve` | p2p-approval.html | ✅ CommunicationController |
| `/api/enexus/p2p/approval/reject` | p2p-approval.html | ✅ CommunicationController |
| `/api/enexus/p2p/approval/revoke` | p2p-approval.html | ✅ CommunicationController |

### 初始化包管理

| API路径 | 前端页面 | 状态 |
|---------|---------|------|
| `/api/enexus/init_package/list` | init-package.html | ✅ InitPackageController |
| `/api/enexus/init_package/generate` | init-package.html | ✅ InitPackageController |
| `/api/enexus/init_package/get` | init-package.html | ✅ InitPackageController |
| `/api/enexus/init_package/send` | init-package.html | ✅ InitPackageController |

### 能力注册中心

| API路径 | 前端页面 | 状态 |
|---------|---------|------|
| `/api/enexus/capability/list` | capability-registry.html | ✅ CapabilityRegistryController |
| `/api/enexus/capability/register` | capability-registry.html | ✅ CapabilityRegistryController |
| `/api/enexus/capability/get` | capability-registry.html | ✅ CapabilityRegistryController |
| `/api/enexus/capability/toggle` | capability-registry.html | ✅ CapabilityRegistryController |

### 数据源管理

| API路径 | 前端页面 | 状态 |
|---------|---------|------|
| `/api/enexus/datasource/test` | datasource-manager.html | ✅ DataSourceController |
| `/api/enexus/datasource/options` | datasource-manager.html | ✅ DataSourceController |
| `/api/enexus/sync/tasks` | sync-manager.html | ✅ DataSourceController |
| `/api/enexus/sync/history/all` | sync-manager.html | ✅ DataSourceController |
| `/api/enexus/sync/pause/{sceneId}` | sync-manager.html | ✅ DataSourceController |
| `/api/enexus/sync/resume/{sceneId}` | sync-manager.html | ✅ DataSourceController |
| `/api/enexus/sync/cancel/{sceneId}` | sync-manager.html | ✅ DataSourceController |

### 建议

1. **确认功能规划**：确认这些页面是否为规划中的功能，如果是，需要创建对应的Controller
2. **API路径统一**：建议将 `/api/enexus/` 统一改为 `/api/scene/` 或其他统一前缀
3. **优先级排序**：根据业务需求确定哪些Controller需要优先实现
