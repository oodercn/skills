# Skills 团队协作申请

## 文档信息

| 项目 | 内容 |
|------|------|
| 发起团队 | Skills Team (ooder-skills) |
| 目标团队 | Engine Team (scene-engine) |
| 创建日期 | 2026-02-21 |
| 优先级 | 高 |
| 版本 | 1.0.0 |

---

## 一、协作背景

### 1.1 项目架构

```
┌─────────────────────────────────────────────────────────────────┐
│  ooderCN/scene-engine (Engine Team 负责)                         │
│  ├── scene-engine/          # SEC 核心                          │
│  ├── drivers/               # 内置场景驱动                       │
│  │   ├── org/               # ORG 驱动                          │
│  │   ├── vfs/               # VFS 驱动                          │
│  │   └── msg/               # MSG 驱动                          │
│  └── scene-gateway/                                             │
│                                                                 │
│  ooderCN/ooder-skills (Skills Team 负责)                        │
│  └── skills/                # 具体技能实现                       │
│      ├── org/               # 组织服务技能                       │
│      ├── vfs/               # 存储服务技能                       │
│      ├── msg/               # 消息通讯技能                       │
│      ├── sys/               # 系统管理技能                       │
│      ├── ui/                # UI生成技能                        │
│      └── util/              # 工具技能                          │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 当前状态

**ooder-skills 已完成**:
- ✅ 21个技能骨架创建
- ✅ 3个Provider实现 (NetworkProvider, SecurityProvider, HostingProvider)
- ✅ scene-engine 0.7.3依赖添加
- ✅ skill-index.yaml分类支持
- ✅ 场景驱动架构文档

---

## 二、协作请求

### 2.1 需要Engine Team定义的Provider接口

| Provider | 优先级 | 用途 | 关联技能 |
|----------|--------|------|----------|
| AgentProvider | P0 | 终端代理管理 | skill-agent |
| HealthProvider | P1 | 健康检查 | skill-health |
| ProtocolProvider | P2 | 协议管理 | skill-protocol |
| OpenWrtProvider | P2 | OpenWrt路由器管理 | skill-openwrt |
| SkillShareProvider | P2 | 技能分享 | skill-share |

### 2.2 接口定义建议

#### 2.2.1 AgentProvider 接口

```java
package net.ooder.scene.provider;

/**
 * 终端代理Provider接口
 */
public interface AgentProvider extends BaseProvider {

    Result<List<EndAgent>> getEndAgents();
    Result<EndAgent> addEndAgent(Map<String, Object> agentData);
    Result<EndAgent> editEndAgent(String agentId, Map<String, Object> agentData);
    Result<EndAgent> deleteEndAgent(String agentId);
    Result<EndAgent> getEndAgentDetails(String agentId);
    
    Result<NetworkStatusData> getNetworkStatus();
    Result<CommandStatsData> getCommandStats();
    
    Result<TestCommandResult> testCommand(Map<String, Object> commandData);
}
```

#### 2.2.2 HealthProvider 接口

```java
package net.ooder.scene.provider;

/**
 * 健康检查Provider接口
 */
public interface HealthProvider extends BaseProvider {

    Result<HealthCheckResult> runHealthCheck(Map<String, Object> params);
    Result<HealthReport> exportHealthReport();
    Result<HealthCheckSchedule> scheduleHealthCheck(Map<String, Object> params);
    Result<ServiceCheckResult> checkService(String serviceName);
}
```

#### 2.2.3 ProtocolProvider 接口

```java
package net.ooder.scene.provider;

/**
 * 协议管理Provider接口
 */
public interface ProtocolProvider extends BaseProvider {

    Result<List<ProtocolHandler>> getProtocolHandlers();
    Result<ProtocolHandler> registerProtocolHandler(Map<String, Object> handlerData);
    Result<Boolean> removeProtocolHandler(String handlerType);
    Result<ProtocolCommandResult> handleProtocolCommand(Map<String, Object> commandData);
    Result<Boolean> refreshProtocolHandlers();
}
```

#### 2.2.4 OpenWrtProvider 接口

```java
package net.ooder.scene.provider;

/**
 * OpenWrt路由器管理Provider接口
 */
public interface OpenWrtProvider extends BaseProvider {

    Result<Boolean> connect(Map<String, Object> connectionData);
    Result<Boolean> disconnect();
    Result<ConnectionStatus> getConnectionStatus();
    
    Result<NetworkSetting> getNetworkSetting(String settingType);
    Result<NetworkSetting> updateNetworkSetting(String settingType, Map<String, Object> data);
    
    Result<List<IPAddress>> getIPAddresses();
    Result<List<IPBlacklist>> getIPBlacklist();
    
    Result<CommandResult> executeCommand(String command);
    Result<SystemStatus> getSystemStatus();
}
```

#### 2.2.5 SkillShareProvider 接口

```java
package net.ooder.scene.provider;

/**
 * 技能分享Provider接口
 */
public interface SkillShareProvider extends BaseProvider {

    Result<SharedSkill> shareSkill(Map<String, Object> skillData);
    Result<List<SharedSkill>> getSharedSkills();
    Result<List<ReceivedSkill>> getReceivedSkills();
    Result<Boolean> cancelShare(String shareId);
}
```

### 2.3 数据模型定义建议

```
scene-engine-core
    └── provider/
        └── model/
            ├── agent/
            │   ├── EndAgent.java
            │   ├── NetworkStatusData.java
            │   ├── CommandStatsData.java
            │   └── TestCommandResult.java
            ├── health/
            │   ├── HealthCheckResult.java
            │   ├── HealthReport.java
            │   ├── HealthCheckSchedule.java
            │   └── ServiceCheckResult.java
            ├── protocol/
            │   ├── ProtocolHandler.java
            │   └── ProtocolCommandResult.java
            ├── openwrt/
            │   ├── ConnectionStatus.java
            │   └── SystemStatus.java
            └── share/
                ├── SharedSkill.java
                └── ReceivedSkill.java
```

---

## 三、已完成的Provider实现

### 3.1 NetworkProviderImpl

**位置**: `skills/skill-network/src/main/java/net/ooder/skill/network/provider/NetworkProviderImpl.java`

**实现方法**:
- `getStatus()` - 获取网络状态
- `getStats()` - 获取网络统计
- `listLinks()` - 列出网络链路
- `getLink()` - 获取链路详情
- `disconnectLink()` - 断开链路
- `reconnectLink()` - 重连链路
- `listRoutes()` - 列出路由
- `getRoute()` - 获取路由详情
- `findRoute()` - 查找路由
- `getTopology()` - 获取网络拓扑
- `getQuality()` - 获取网络质量

**ServiceLoader配置**: ✅ 已配置

### 3.2 SecurityProviderImpl

**位置**: `skills/skill-security/src/main/java/net/ooder/skill/security/provider/SecurityProviderImpl.java`

**实现方法**:
- `getStatus()` - 获取安全状态
- `getStats()` - 获取安全统计
- `listPolicies()` - 列出安全策略
- `createPolicy()` - 创建安全策略
- `updatePolicy()` - 更新安全策略
- `deletePolicy()` - 删除安全策略
- `enablePolicy()` - 启用策略
- `disablePolicy()` - 禁用策略
- `listAcls()` - 列出访问控制
- `createAcl()` - 创建访问控制
- `deleteAcl()` - 删除访问控制
- `checkPermission()` - 检查权限
- `listThreats()` - 列出威胁
- `getThreat()` - 获取威胁详情
- `resolveThreat()` - 解决威胁
- `runSecurityScan()` - 运行安全扫描
- `toggleFirewall()` - 切换防火墙
- `isFirewallEnabled()` - 防火墙状态

**ServiceLoader配置**: ✅ 已配置

### 3.3 HostingProviderImpl

**位置**: `skills/skill-hosting/src/main/java/net/ooder/skill/hosting/provider/HostingProviderImpl.java`

**实现方法**:
- `getAllInstances()` - 获取所有实例
- `getInstances()` - 分页获取实例
- `getInstance()` - 获取实例详情
- `createInstance()` - 创建实例
- `updateInstance()` - 更新实例
- `deleteInstance()` - 删除实例
- `startInstance()` - 启动实例
- `stopInstance()` - 停止实例
- `restartInstance()` - 重启实例
- `scaleInstance()` - 扩缩容实例
- `getHealth()` - 获取实例健康
- `getMetrics()` - 获取实例指标
- `getLogs()` - 获取实例日志
- `executeCommand()` - 执行命令
- `getSupportedTypes()` - 获取支持类型
- `getQuota()` - 获取配额

**ServiceLoader配置**: ✅ 已配置

---

## 四、版本规划建议

### 4.1 scene-engine 版本规划

| 版本 | 发布内容 | 预计日期 |
|------|----------|----------|
| 0.7.3 | 现有Provider (Network, Security, Hosting, System, Config, Log) | 已发布 |
| 0.8.0 | AgentProvider 接口定义 | Week 2 |
| 0.9.0 | HealthProvider, ProtocolProvider 接口定义 | Week 3 |
| 1.0.0 | OpenWrtProvider, SkillShareProvider 接口定义 | Week 4 |

### 4.2 ooder-skills 版本规划

| 版本 | 发布内容 | 依赖 SEC |
|------|----------|----------|
| 0.7.3 | NetworkProviderImpl, SecurityProviderImpl, HostingProviderImpl | 0.7.3 |
| 0.8.0 | AgentProviderImpl | 0.8.0 |
| 0.9.0 | HealthProviderImpl, ProtocolProviderImpl | 0.9.0 |
| 1.0.0 | OpenWrtProviderImpl, SkillShareProviderImpl | 1.0.0 |

---

## 五、里程碑计划

| 里程碑 | 完成标准 | 负责团队 | 预计日期 |
|--------|----------|----------|----------|
| M1 | scene-engine 0.7.3发布 | Engine Team | ✅ 已完成 |
| M2 | NetworkProvider, SecurityProvider, HostingProvider 实现 | Skills Team | ✅ 已完成 |
| M3 | AgentProvider 接口定义 | Engine Team | Week 2 |
| M4 | AgentProviderImpl 实现 | Skills Team | Week 2 |
| M5 | HealthProvider, ProtocolProvider 接口定义 | Engine Team | Week 3 |
| M6 | HealthProviderImpl, ProtocolProviderImpl 实现 | Skills Team | Week 3 |
| M7 | OpenWrtProvider, SkillShareProvider 接口定义 | Engine Team | Week 4 |
| M8 | OpenWrtProviderImpl, SkillShareProviderImpl 实现 | Skills Team | Week 4 |
| M9 | 集成测试通过 | 全部 | Week 5 |

---

## 六、联系方式

### Skills Team

- **项目地址**: https://github.com/ooderCN/skills
- **负责范围**: Provider接口实现、业务逻辑、单元测试
- **当前版本**: 0.7.3

### Engine Team

- **项目地址**: https://github.com/ooderCN/scene-engine
- **负责范围**: Provider接口定义、数据模型、注册机制
- **当前版本**: 0.7.3

---

## 七、附录

### 7.1 相关文档

- [场景驱动目录结构说明](./SCENE_DRIVER_STRUCTURE.md)
- [技能分类管理方案](./skills-category-proposal.md)
- [SEC Provider 责任分解](../scene-engine/docs/SEC_PROVIDER_RESPONSIBILITY_DECOMPOSITION.md)

### 7.2 Git提交记录

```
bd6660c feat: 添加场景驱动架构支持
2a66bb1 docs: 更新README添加SEC Engine技能列表
7cbc084 feat: 新增skill-hosting技能并实现HostingProvider
834c9d2 feat: 添加scene-engine依赖和Provider实现
e9f5cda feat: 添加技能分类管理支持
d1913b1 feat: 新增SEC Engine协作技能
```

---

**文档状态**: 待Engine Team确认  
**创建日期**: 2026-02-21  
**维护团队**: Skills Team
