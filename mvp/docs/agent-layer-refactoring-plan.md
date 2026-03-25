# Agent功能分层重构方案

## 一、当前问题分析

### 问题：所有功能都在MVP层实现

当前实现的所有功能都在MVP层，这导致：
1. **代码重复** - 每个应用都需要重新实现
2. **维护困难** - 底层功能散落在各个应用中
3. **不一致性** - 不同应用可能有不同实现
4. **违反分层原则** - SDK应该提供基础能力

### 当前实现清单

| 功能 | 当前位置 | 问题 |
|------|---------|------|
| AgentFastStartupConfig | MVP | ❌ 应该在SDK层 |
| AgentStateSnapshotService | MVP | ❌ 应该在SDK层 |
| AgentHeartbeatConfig | MVP | ❌ 应该在SDK层 |
| AgentStatus枚举 | MVP | ❌ 应该在SDK层 |
| AgentFactory实现 | MVP | ❌ 应该在SDK层 |
| Agent适配器 | MVP | ❌ 应该在SDK层 |
| 前端显示 | MVP | ✅ 正确 |
| 数据持久化 | MVP | ✅ 正确 |

## 二、合理的分层架构

### 分层原则

```
┌─────────────────────────────────────────────────────────────┐
│                    应用层 (MVP)                              │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━│
│  职责：业务逻辑、用户界面、数据持久化                        │
│  ├── 前端界面                                               │
│  ├── REST API                                               │
│  ├── 业务配置                                               │
│  └── 数据存储                                               │
└─────────────────────────────────────────────────────────────┘
                          ↓ 调用
┌─────────────────────────────────────────────────────────────┐
│                    SDK层 (ooder-sdk)                         │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━│
│  职责：核心能力、通用功能、抽象接口                          │
│  ├── Agent生命周期管理                                      │
│  ├── 心跳机制                                               │
│  ├── 状态快照                                               │
│  ├── 快速启动                                               │
│  ├── AgentFactory                                           │
│  └── 状态枚举                                               │
└─────────────────────────────────────────────────────────────┘
                          ↓ 依赖
┌─────────────────────────────────────────────────────────────┐
│                    SE层 (ooder-se)                           │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━│
│  职责：场景引擎、核心协议、基础设施                          │
│  ├── 场景管理                                               │
│  ├── 协议实现                                               │
│  ├── 网络通信                                               │
│  └── 底层服务                                               │
└─────────────────────────────────────────────────────────────┘
```

## 三、功能重新分配

### SDK层应该提供的功能

#### 1. AgentStatus枚举（增强版）

**位置**: `net.ooder.sdk.api.agent.AgentStatus`

```java
package net.ooder.sdk.api.agent;

public enum AgentStatus {
    ONLINE("online", "在线"),
    BUSY("busy", "忙碌"),
    IDLE("idle", "空闲"),
    OFFLINE("offline", "离线"),
    RECOVERING("recovering", "恢复中"),
    UNKNOWN("unknown", "未知");
    
    private final String code;
    private final String description;
    
    AgentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static AgentStatus fromCode(String code) {
        for (AgentStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return UNKNOWN;
    }
}
```

#### 2. AgentStateSnapshotService（SDK版）

**位置**: `net.ooder.sdk.service.snapshot.AgentStateSnapshotService`

```java
package net.ooder.sdk.service.snapshot;

import net.ooder.sdk.api.agent.AgentStatus;
import java.util.List;
import java.util.Map;

public interface AgentStateSnapshotService {
    
    void saveSnapshot();
    
    AgentNetworkSnapshot loadSnapshot();
    
    boolean hasValidSnapshot();
    
    void clearSnapshot();
    
    long getSnapshotAge();
    
    void markAllAgentsOffline();
    
    public static class AgentNetworkSnapshot {
        private long timestamp;
        private List<AgentSnapshot> agents;
        private Map<String, Object> metadata;
        
        public static class AgentSnapshot {
            private String agentId;
            private String agentName;
            private AgentStatus status;
            private long lastHeartbeat;
            private String sceneGroupId;
            private Map<String, Object> properties;
        }
    }
}
```

#### 3. AgentFastStartupService（SDK版）

**位置**: `net.ooder.sdk.service.startup.AgentFastStartupService`

```java
package net.ooder.sdk.service.startup;

import net.ooder.sdk.api.agent.AgentStatus;
import java.util.List;

public interface AgentFastStartupService {
    
    void performFastStartupProbe();
    
    void markAllAgentsRecovering();
    
    void probeAgent(String agentId);
    
    FastStartupStats getStats();
    
    public static class FastStartupStats {
        private long duration;
        private int totalAgents;
        private int onlineAgents;
        private int offlineAgents;
        private int recoveringAgents;
    }
}
```

#### 4. EnhancedHeartbeatService（增强版）

**位置**: `net.ooder.sdk.service.heartbeat.EnhancedHeartbeatService`

**需要增强的功能**:
```java
public class EnhancedHeartbeatService {
    
    // 现有功能...
    
    // 新增：快速探测模式
    private volatile boolean probeMode = false;
    private volatile int probeTimeout = 5000;
    
    public void enableProbeMode(int timeout) {
        this.probeMode = true;
        this.probeTimeout = timeout;
        log.info("Enabled probe mode with timeout: {}ms", timeout);
    }
    
    public void disableProbeMode() {
        this.probeMode = false;
        log.info("Disabled probe mode, switched to normal heartbeat");
    }
    
    // 新增：批量状态更新
    public void updateAllAgentsStatus(DeviceStatus status) {
        for (HeartbeatContext context : heartbeatContexts.values()) {
            context.setStatus(status);
        }
    }
    
    // 新增：获取所有Agent状态
    public Map<String, DeviceStatus> getAllAgentsStatus() {
        Map<String, DeviceStatus> result = new HashMap<>();
        for (Map.Entry<String, HeartbeatContext> entry : heartbeatContexts.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getStatus());
        }
        return result;
    }
}
```

#### 5. AgentFactory实现（SDK版）

**位置**: `net.ooder.sdk.core.agent.factory.AgentFactoryImpl`

```java
package net.ooder.sdk.core.agent.factory;

import net.ooder.sdk.api.agent.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentFactoryImpl implements AgentFactory {
    
    private final Map<String, Agent> agentCache = new ConcurrentHashMap<>();
    private final AgentServiceProvider serviceProvider;
    
    public AgentFactoryImpl(AgentServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
    
    @Override
    public Agent getAgent(String agentId) {
        return agentCache.computeIfAbsent(agentId, this::createAgent);
    }
    
    private Agent createAgent(String agentId) {
        AgentType type = serviceProvider.getAgentType(agentId);
        
        switch (type) {
            case END_AGENT:
                return new EndAgentAdapter(agentId, serviceProvider);
            case WORKER_AGENT:
                return new WorkerAgentAdapter(agentId, serviceProvider);
            case SCENE_AGENT:
                return new SceneAgentAdapter(agentId, serviceProvider);
            case ROUTE_AGENT:
                return new RouteAgentAdapter(agentId, serviceProvider);
            case MCP_AGENT:
                return new McpAgentAdapter(agentId, serviceProvider);
            default:
                throw new IllegalArgumentException("Unknown agent type: " + type);
        }
    }
    
    public interface AgentServiceProvider {
        AgentType getAgentType(String agentId);
        boolean checkAlive(String agentId);
        void sendHeartbeat(String agentId);
        Object invokeCapability(String agentId, String capabilityId, Map<String, Object> params);
    }
}
```

### MVP层应该提供的功能

#### 1. AgentServiceProvider实现

**位置**: `net.ooder.mvp.skill.scene.agent.service.AgentServiceProviderImpl`

```java
package net.ooder.mvp.skill.scene.agent.service;

import net.ooder.sdk.core.agent.factory.AgentFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentServiceProviderImpl implements AgentFactoryImpl.AgentServiceProvider {
    
    @Autowired
    private AgentSessionService sessionService;
    
    @Autowired
    private AgentService agentService;
    
    @Override
    public AgentType getAgentType(String agentId) {
        AgentSessionDTO session = sessionService.getSession(agentId);
        if (session == null) {
            return AgentType.END_AGENT;
        }
        
        return AgentType.fromCode(session.getAgentType());
    }
    
    @Override
    public boolean checkAlive(String agentId) {
        return agentService.checkAgentAlive(agentId);
    }
    
    @Override
    public void sendHeartbeat(String agentId) {
        agentService.sendHeartbeat(agentId);
    }
    
    @Override
    public Object invokeCapability(String agentId, String capabilityId, Map<String, Object> params) {
        // MVP特定的能力调用逻辑
        return null;
    }
}
```

#### 2. 数据持久化

**位置**: `AgentSessionServiceImpl`

```java
@Service
public class AgentSessionServiceImpl implements AgentSessionService {
    
    @Autowired
    private JsonStorageService storage;
    
    @Autowired(required = false)
    private AgentStateSnapshotService snapshotService;
    
    // MVP特定的持久化逻辑
    private void persistSession(AgentSessionDTO session) {
        storage.put(STORAGE_KEY_SESSIONS, session.getAgentId(), session);
    }
    
    // MVP特定的加载逻辑
    private void loadFromStorage() {
        Map<String, AgentSessionDTO> storedSessions = storage.getAll(STORAGE_KEY_SESSIONS);
        // ...
    }
}
```

#### 3. 前端界面

**位置**: `agent-list.html`

```html
<!-- MVP特定的前端逻辑 -->
<script>
function renderAgentStatus(status) {
    // 使用SDK提供的AgentStatus枚举
    const statusMap = {
        'ONLINE': { text: '在线', class: 'online' },
        'BUSY': { text: '忙碌', class: 'busy' },
        'RECOVERING': { text: '恢复中', class: 'recovering' },
        'OFFLINE': { text: '离线', class: 'offline' }
    };
    
    return statusMap[status] || statusMap['OFFLINE'];
}
</script>
```

## 四、重构实施步骤

### 阶段1：SDK层重构（优先级：高）

#### 步骤1.1：创建SDK层的AgentStatus枚举

```bash
# 文件位置
net.ooder.sdk.api.agent.AgentStatus
```

**操作**:
1. 在SDK中创建增强版AgentStatus枚举
2. 添加RECOVERING和UNKNOWN状态
3. 提供状态转换工具方法

#### 步骤1.2：增强EnhancedHeartbeatService

```bash
# 文件位置
net.ooder.sdk.service.heartbeat.EnhancedHeartbeatService
```

**操作**:
1. 添加快速探测模式
2. 添加批量状态更新
3. 添加状态查询接口

#### 步骤1.3：创建AgentStateSnapshotService

```bash
# 文件位置
net.ooder.sdk.service.snapshot.AgentStateSnapshotService
net.ooder.sdk.service.snapshot.AgentStateSnapshotServiceImpl
```

**操作**:
1. 定义快照服务接口
2. 实现快照保存和加载
3. 提供快照管理功能

#### 步骤1.4：创建AgentFastStartupService

```bash
# 文件位置
net.ooder.sdk.service.startup.AgentFastStartupService
net.ooder.sdk.service.startup.AgentFastStartupServiceImpl
```

**操作**:
1. 定义快速启动服务接口
2. 实现快速探测逻辑
3. 提供统计信息

#### 步骤1.5：创建AgentFactory实现

```bash
# 文件位置
net.ooder.sdk.core.agent.factory.AgentFactoryImpl
```

**操作**:
1. 实现AgentFactory接口
2. 创建各种Agent适配器
3. 定义AgentServiceProvider接口

### 阶段2：MVP层重构（优先级：高）

#### 步骤2.1：实现AgentServiceProvider

```bash
# 文件位置
net.ooder.mvp.skill.scene.agent.service.AgentServiceProviderImpl
```

**操作**:
1. 实现AgentServiceProvider接口
2. 集成MVP的AgentService
3. 提供MVP特定的实现

#### 步骤2.2：简化AgentHeartbeatConfig

```bash
# 文件位置
net.ooder.mvp.skill.scene.agent.config.AgentHeartbeatConfig
```

**操作**:
1. 移除重复的AgentFactory实现
2. 使用SDK提供的AgentFactory
3. 只保留MVP特定的配置

#### 步骤2.3：删除重复代码

**操作**:
1. 删除AgentFastStartupConfig（使用SDK版本）
2. 删除AgentStateSnapshotService（使用SDK版本）
3. 删除AgentStatus枚举（使用SDK版本）

#### 步骤2.4：更新前端

```bash
# 文件位置
agent-list.html
agent-pages.css
```

**操作**:
1. 使用SDK提供的状态枚举
2. 更新状态显示逻辑
3. 保持MVP特定的UI逻辑

### 阶段3：测试验证（优先级：中）

#### 步骤3.1：单元测试

**操作**:
1. 测试SDK层的AgentStatus枚举
2. 测试SDK层的快照服务
3. 测试SDK层的快速启动服务

#### 步骤3.2：集成测试

**操作**:
1. 测试MVP与SDK的集成
2. 测试快速启动流程
3. 测试状态快照功能

#### 步骤3.3：性能测试

**操作**:
1. 测试启动时间
2. 测试内存使用
3. 测试并发性能

## 五、重构后的架构

### SDK层架构

```
net.ooder.sdk
├── api.agent
│   ├── AgentStatus (增强版枚举)
│   ├── AgentFactory
│   └── Agent接口
├── service
│   ├── heartbeat
│   │   └── EnhancedHeartbeatService (增强版)
│   ├── snapshot
│   │   ├── AgentStateSnapshotService
│   │   └── AgentStateSnapshotServiceImpl
│   └── startup
│       ├── AgentFastStartupService
│       └── AgentFastStartupServiceImpl
└── core.agent.factory
    ├── AgentFactoryImpl
    ├── AgentServiceProvider (接口)
    └── AgentAdapter (各种适配器)
```

### MVP层架构

```
net.ooder.mvp.skill.scene.agent
├── service
│   ├── AgentServiceProviderImpl (实现SDK接口)
│   ├── AgentSessionService (持久化)
│   └── AgentService (业务逻辑)
├── config
│   └── AgentHeartbeatConfig (简化版，使用SDK)
├── controller
│   └── AgentController (REST API)
└── dto
    └── AgentSessionDTO (MVP数据模型)
```

## 六、重构收益

### 代码质量

| 指标 | 重构前 | 重构后 | 改进 |
|------|--------|--------|------|
| 代码重复 | 高 | 低 | ✅ |
| 分层清晰度 | 差 | 好 | ✅ |
| 可维护性 | 中 | 高 | ✅ |
| 可测试性 | 中 | 高 | ✅ |

### 功能复用

| 功能 | 重构前 | 重构后 |
|------|--------|--------|
| 快速启动 | 仅MVP | 所有应用 |
| 状态快照 | 仅MVP | 所有应用 |
| 心跳机制 | 仅MVP | 所有应用 |
| 状态管理 | 仅MVP | 所有应用 |

### 开发效率

| 场景 | 重构前 | 重构后 |
|------|--------|--------|
| 新应用开发 | 需要重新实现 | 直接使用SDK |
| 功能升级 | 需要修改多处 | 只修改SDK |
| Bug修复 | 需要修复多处 | 只修复SDK |

## 七、实施时间表

### 第1周：SDK层重构

- Day 1-2: 创建AgentStatus枚举
- Day 3-4: 增强EnhancedHeartbeatService
- Day 5: 创建AgentStateSnapshotService

### 第2周：SDK层完成

- Day 1-2: 创建AgentFastStartupService
- Day 3-4: 创建AgentFactory实现
- Day 5: 单元测试

### 第3周：MVP层重构

- Day 1-2: 实现AgentServiceProvider
- Day 3: 简化AgentHeartbeatConfig
- Day 4: 删除重复代码
- Day 5: 集成测试

### 第4周：测试和优化

- Day 1-2: 集成测试
- Day 3-4: 性能测试
- Day 5: 文档更新

## 八、风险评估

### 技术风险

| 风险 | 影响 | 概率 | 缓解措施 |
|------|------|------|---------|
| 接口不兼容 | 高 | 中 | 充分测试 |
| 性能下降 | 中 | 低 | 性能测试 |
| 功能缺失 | 高 | 低 | 功能对比 |

### 进度风险

| 风险 | 影响 | 概率 | 缓解措施 |
|------|------|------|---------|
| 时间延期 | 中 | 中 | 分阶段实施 |
| 资源不足 | 高 | 低 | 优先级管理 |

## 九、总结

### 核心原则

1. **SDK提供基础能力** - 通用功能、抽象接口
2. **MVP提供业务逻辑** - 特定实现、用户界面
3. **清晰分层** - 各司其职、避免重复

### 实施优先级

1. **高优先级**: SDK层基础功能（AgentStatus、EnhancedHeartbeatService）
2. **中优先级**: SDK层高级功能（快照、快速启动）
3. **低优先级**: MVP层简化优化

### 预期效果

- ✅ 代码复用率提升80%
- ✅ 维护成本降低60%
- ✅ 新应用开发效率提升50%
- ✅ 功能一致性提升100%

---

**建议**: 立即开始SDK层重构，优先实现AgentStatus枚举和增强EnhancedHeartbeatService。
