# Nexus Console 分层开发建议

## 文档信息

- 创建日期: 2026-02-28
- 文档版本: 1.0
- 关联项目: ooder-Nexus-Enterprise, ooder-sdk

---

## 一、架构分层概述

```
┌─────────────────────────────────────────────────────────────────┐
│                      Nexus Console (前端)                        │
│                    HTML/CSS/JavaScript 页面                      │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Nexus Controller 层                           │
│  SceneController, SecurityController, McpAgentController, ...   │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Nexus Service 层                              │
│     SceneService, SecurityService, McpAgentService, ...         │
└─────────────────────────────────────────────────────────────────┘
                                │
                    ┌───────────┴───────────┐
                    ▼                       ▼
┌───────────────────────────┐   ┌───────────────────────────┐
│      SDK 提供的接口        │   │    Nexus 自实现功能        │
│  (SceneMonitor,           │   │  (SecurityService,        │
│   SceneManager,           │   │   McpAgentService,        │
│   ConnectionTestService)  │   │   CommunicationService)   │
└───────────────────────────┘   └───────────────────────────┘
```

---

## 二、功能分类与责任划分

### 2.1 SDK 提供的功能（已实现）

SDK 2.3 版本已提供的接口，Nexus直接调用：

| 接口 | 包名 | 功能 | 状态 |
|------|------|------|------|
| SceneManager | net.ooder.sdk.api.scene | 场景生命周期管理 | ✅ 可用 |
| SceneMonitor | net.ooder.scene.monitor | 统一监控入口 | ✅ 可用 |
| PerformanceMonitor | net.ooder.scene.monitor | 性能监控 | ✅ 可用 |
| SceneFlowManager | net.ooder.scene.monitor | 流程管理 | ✅ 可用 |
| SceneConfigManager | net.ooder.scene.monitor | 配置管理 | ✅ 可用 |
| SceneEventManager | net.ooder.scene.monitor | 事件管理 | ✅ 可用 |
| SceneLogManager | net.ooder.scene.monitor | 日志管理 | ✅ 可用 |
| ServiceHealthMonitor | net.ooder.scene.monitor | 服务健康监控 | ✅ 可用 |
| CapabilityStatusMonitor | net.ooder.scene.monitor | 能力状态监控 | ✅ 可用 |
| ConnectionTestService | net.ooder.sdk.api.connection | 连接测试 | ✅ 可用 |
| SceneEngine | net.ooder.scene.core | 场景引擎 | ✅ 可用 |
| SceneLifecycleManager | net.ooder.scene.core | 生命周期管理 | ✅ 可用 |

### 2.2 Nexus 自实现功能（需要Nexus团队开发）

这些功能是Nexus Console特有的，不属于SDK范畴：

| Service | 功能模块 | 说明 | 优先级 |
|---------|---------|------|--------|
| SecurityService | 安全管理 | Token、密钥、证书管理 | 高 |
| McpAgentService | MCP代理管理 | MCP代理注册、心跳、状态管理 | 高 |
| CommunicationService | 通信管理 | P2P连接、中继命令、权限规则 | 中 |
| NexusInstanceService | 实例管理 | Nexus实例注册与发现 | 中 |
| InitPackageService | 初始化包管理 | 场景初始化包生成与分发 | 低 |

### 2.3 SDK已有机制可复用（无需新增接口）

SDK已提供完善的Driver和Capability机制，Nexus可直接复用：

| 功能 | SDK已有接口 | 包名 | 复用方式 |
|------|------------|------|---------|
| 数据库驱动 | DatabaseDriver | net.ooder.sdk.drivers.db | 作为Driver扩展 |
| 能力注册表 | CapabilityRegistry | net.ooder.sdk.a2a.capability | 直接注入使用 |
| 驱动注册表 | DriverRegistry | net.ooder.scene.core.driver | 直接注入使用 |

**DatabaseDriver已支持的数据库**：
- MySqlDatabaseDriver (MySQL)
- SqliteDatabaseDriver (SQLite)
- 可扩展：PostgreSQL、Oracle、MongoDB等

**CapabilityRegistry已提供的方法**：
- registerCapability() - 注册能力
- unregisterCapability() - 注销能力
- getSkillCapabilities() - 获取Skill所有能力
- searchCapabilities() - 搜索能力
- getAllCapabilities() - 获取所有能力

---

## 三、当前Mock实现清单

以下功能目前使用内存存储实现，需要根据责任划分进行开发：

### 3.1 Nexus自实现功能（Mock → 数据库持久化）

| Service | 方法 | 当前实现 | 改进方向 |
|---------|------|---------|---------|
| SecurityService | listTokens() | HashMap | 数据库持久化 |
| SecurityService | generateToken() | 内存生成 | 数据库+加密存储 |
| SecurityService | revokeToken() | 内存状态修改 | 数据库更新 |
| SecurityService | listUserKeys() | HashMap | 数据库持久化 |
| SecurityService | generateUserKey() | Mock公钥 | 真实密钥生成 |
| SecurityService | listSceneGroupKeys() | HashMap | 数据库持久化 |
| SecurityService | rotateSceneGroupKey() | 内存版本号 | 数据库+密钥轮换 |
| SecurityService | listDomainKeys() | HashMap | 数据库持久化 |
| SecurityService | listCertificates() | HashMap | 数据库持久化 |
| SecurityService | generateCertificate() | Mock证书 | 真实证书生成 |
| McpAgentService | listAgents() | HashMap | 数据库持久化 |
| McpAgentService | registerAgent() | 内存存储 | 数据库持久化 |
| McpAgentService | getHeartbeatStats() | 内存计算 | 数据库查询 |
| CommunicationService | listP2pConnections() | HashMap | 数据库持久化 |
| CommunicationService | listPermissionRules() | HashMap | 数据库持久化 |
| CommunicationService | listP2pApprovals() | HashMap | 数据库持久化 |
| NexusInstanceService | listInstances() | HashMap | 数据库持久化 |
| NexusInstanceService | registerInstance() | 内存存储 | 数据库持久化 |
| InitPackageService | listPackages() | HashMap | 数据库持久化 |

### 3.2 需要SDK支持的功能（Mock → SDK调用）

| Service | 方法 | 当前实现 | 改进方向 |
|---------|------|---------|---------|
| SceneConfigService | testConnection() | 固定延迟+随机 | SDK ConnectionTestService |
| SceneRuntimeService | getMetrics() | JVM数据+随机历史 | SDK PerformanceMonitor |
| SceneRuntimeService | getEvents() | 固定事件列表 | SDK SceneEventManager |
| SceneRuntimeService | getLogs() | 固定日志列表 | SDK SceneLogManager |
| SceneService | startFlow() | 内存模拟 | SDK SceneFlowManager |
| SceneService | getFlowLogs() | 固定日志 | SDK SceneFlowManager |

---

## 四、分层开发建议

### 4.1 第一层：SDK集成层（已完成）

**目标**：正确使用SDK提供的接口

**已完成工作**：
- SdkIntegrationService 集成所有SDK Manager
- SceneService 调用SDK进行场景管理
- SceneRuntimeService 获取场景运行状态
- SceneConfigService 获取配置信息

**验证方式**：
```bash
mvn compile  # 编译通过
mvn spring-boot:run  # 启动成功
```

### 4.2 第二层：数据库持久化层（待开发）

**目标**：将Mock实现替换为数据库持久化

**需要创建的实体类**：

```java
// 安全管理
@Entity public class SecurityToken { ... }
@Entity public class UserKey { ... }
@Entity public class SceneGroupKey { ... }
@Entity public class DomainKey { ... }
@Entity public class Certificate { ... }

// MCP代理
@Entity public class McpAgent { ... }

// 通信管理
@Entity public class P2pConnection { ... }
@Entity public class PermissionRule { ... }
@Entity public class P2pApproval { ... }

// 实例管理
@Entity public class NexusInstance { ... }

// 初始化包
@Entity public class InitPackage { ... }
```

**需要创建的Repository**：

```java
public interface SecurityTokenRepository extends JpaRepository<SecurityToken, String> { }
public interface UserKeyRepository extends JpaRepository<UserKey, String> { }
public interface McpAgentRepository extends JpaRepository<McpAgent, String> { }
// ... 其他Repository
```

**工作量估计**：
- 实体类：10个
- Repository：10个
- Service改造：6个
- 预计工时：3-5天

### 4.3 第三层：业务逻辑完善层（待开发）

**目标**：实现真实的业务逻辑

**安全管理模块**：
- 真实的Token生成与验证（JWT）
- 真实的密钥生成（RSA/AES）
- 真实的证书生成（X.509）
- Token过期自动清理

**MCP代理模块**：
- 心跳检测与超时处理
- 代理能力注册与发现
- 代理负载均衡

**通信管理模块**：
- P2P连接状态管理
- 权限规则引擎
- 审批流程引擎

**工作量估计**：
- 预计工时：5-7天

### 4.4 第四层：SDK机制复用层（推荐）

**目标**：复用SDK已有的Driver和Capability机制

**数据源管理 - 使用DatabaseDriver**：

SDK已提供DatabaseDriver接口和多种实现：
- `MySqlDatabaseDriver` - MySQL数据库
- `SqliteDatabaseDriver` - SQLite数据库
- 可扩展实现其他数据库

```java
// Nexus中复用DatabaseDriver
@Service
public class DataSourceService {
    
    @Autowired(required = false)
    private DriverRegistry driverRegistry;
    
    public ApiResponse<Map<String, Object>> testConnection(Map<String, Object> params) {
        String type = (String) params.get("type");
        DatabaseDriver driver = driverRegistry.getDriver(type, DatabaseDriver.class);
        
        if (driver != null) {
            DatabaseConfig config = new DatabaseConfig();
            config.setUrl((String) params.get("url"));
            config.setUsername((String) params.get("username"));
            config.setPassword((String) params.get("password"));
            
            driver.init(config);
            boolean connected = driver.isConnected();
            // ...
        }
    }
}
```

**能力注册中心 - 使用CapabilityRegistry**：

SDK已提供完整的CapabilityRegistry实现：

```java
// Nexus中复用CapabilityRegistry
@Service
public class CapabilityRegistryService {
    
    @Autowired(required = false)
    private net.ooder.sdk.a2a.capability.CapabilityRegistry sdkCapabilityRegistry;
    
    public ApiResponse<Map<String, Object>> listCapabilities(Map<String, Object> params) {
        if (sdkCapabilityRegistry != null) {
            List<CapabilityDeclaration> capabilities = sdkCapabilityRegistry.getAllCapabilities();
            // 转换为前端需要的格式
        }
        // fallback to local implementation
    }
}
```

**工作量估计**：
- 预计工时：1-2天（主要是接口适配）

---

## 五、开发优先级建议

### 高优先级（P0）

| 序号 | 功能 | 原因 | 负责团队 | 复用SDK |
|------|------|------|---------|---------|
| 1 | SecurityService 数据库持久化 | 核心安全功能 | Nexus团队 | 否 |
| 2 | McpAgentService 数据库持久化 | 代理管理是核心功能 | Nexus团队 | 否 |
| 3 | SceneRuntimeService SDK集成 | 监控数据需要真实来源 | SDK集成 | ✅ SceneMonitor |

### 中优先级（P1）

| 序号 | 功能 | 原因 | 负责团队 | 复用SDK |
|------|------|------|---------|---------|
| 4 | CommunicationService 数据库持久化 | P2P通信管理 | Nexus团队 | 否 |
| 5 | NexusInstanceService 数据库持久化 | 实例发现功能 | Nexus团队 | 否 |
| 6 | DataSourceService Driver集成 | 数据源管理 | Nexus团队 | ✅ DatabaseDriver |

### 低优先级（P2）

| 序号 | 功能 | 原因 | 负责团队 | 复用SDK |
|------|------|------|---------|---------|
| 7 | InitPackageService 数据库持久化 | 初始化包管理 | Nexus团队 | 否 |
| 8 | CapabilityRegistryService 集成 | 能力注册中心 | Nexus团队 | ✅ CapabilityRegistry |

---

## 六、技术选型建议

### 6.1 数据库

- **推荐**：PostgreSQL 或 MySQL
- **ORM**：Spring Data JPA
- **迁移工具**：Flyway 或 Liquibase

### 6.2 安全组件

- **Token**：JWT (jjwt 库)
- **密钥生成**：Bouncy Castle
- **证书**：Java KeyStore + Bouncy Castle

### 6.3 缓存

- **推荐**：Redis
- **用途**：Token缓存、Session管理、心跳状态

---

## 七、SDK机制复用总结

### 可直接复用的SDK组件

| 组件 | 接口/类 | 用途 | Nexus使用场景 |
|------|---------|------|--------------|
| DatabaseDriver | net.ooder.sdk.drivers.db | 数据库连接与操作 | DataSourceService |
| CapabilityRegistry | net.ooder.sdk.a2a.capability | 能力注册与发现 | CapabilityRegistryService |
| DriverRegistry | net.ooder.scene.core.driver | 驱动注册与管理 | 扩展新驱动 |
| SceneMonitor | net.ooder.scene.monitor | 场景监控统一入口 | SceneRuntimeService |
| ConnectionTestService | net.ooder.sdk.api.connection | 连接测试 | SceneConfigService |

### 已实现的DatabaseDriver

| Driver | 类名 | 数据库类型 |
|--------|------|-----------|
| MySQL | MySqlDatabaseDriver | MySQL 5.7+ |
| SQLite | SqliteDatabaseDriver | SQLite 3.x |
| PostgreSQL | 可扩展实现 | PostgreSQL |
| Oracle | 可扩展实现 | Oracle |

### 扩展新Driver的步骤

1. 实现DatabaseDriver接口
2. 添加@DriverImplementation注解
3. 注册到DriverRegistry

```java
@DriverImplementation(value = "DatabaseDriver", skillId = "skill-db-postgresql")
public class PostgreSqlDatabaseDriver implements DatabaseDriver {
    // 实现接口方法...
}
```

---

## 八、下一步行动

1. **确认开发计划**：与产品确认各功能优先级
2. **创建数据库表**：根据实体类设计数据库Schema
3. **实现Repository层**：创建JPA Repository
4. **改造Service层**：将Mock实现替换为数据库操作
5. **编写单元测试**：确保功能正确性
6. **集成测试**：前后端联调

---

## 附录：当前代码状态

### 已集成SDK的Service

| Service | SDK接口 | 状态 |
|---------|---------|------|
| SdkIntegrationService | 多个Manager | ✅ 完成 |
| SceneService | SceneManager | ✅ 完成 |
| SceneRuntimeService | SceneManager | ✅ 完成 |
| SceneConfigService | SceneManager | ✅ 完成 |

### 使用Mock实现的Service

| Service | Mock类型 | 待改进 |
|---------|---------|--------|
| SecurityService | 内存HashMap | 数据库持久化 |
| McpAgentService | 内存HashMap | 数据库持久化 |
| CommunicationService | 内存HashMap | 数据库持久化 |
| NexusInstanceService | 内存HashMap | 数据库持久化 |
| InitPackageService | 内存HashMap | 数据库持久化 |
| DataSourceService | 内存HashMap | 数据库持久化 |
| CapabilityRegistryService | 内存HashMap | 数据库持久化 |
