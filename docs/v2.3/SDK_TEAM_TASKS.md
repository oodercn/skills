# SDK 团队任务清单与需求说明

## 文档信息

| 属性 | 值 |
|------|-----|
| **版本** | 2.3 |
| **目标团队** | SDK 团队 |
| **截止日期** | 2026-03-31 |
| **优先级** | P0-P3 |

---

## 一、任务总览

```
┌─────────────────────────────────────────────────────────────────┐
│                    SDK 2.3 开发任务全景                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Phase 1: 核心闭环 (2周)  ████████████░░░░░░░░░░  50%          │
│  Phase 2: 安全与更新 (2周)  ░░░░░░░░░░░░░░░░░░░░  0%           │
│  Phase 3: 发现机制 (2周)  ░░░░░░░░░░░░░░░░░░░░  0%           │
│  Phase 4: 优化完善 (2周)  ░░░░░░░░░░░░░░░░░░░░  0%           │
│                                                                 │
│  总体进度: 12.5%                                               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 二、详细任务清单

### Phase 1: 核心闭环 (Week 1-2)

#### Task 1.1: SkillRegistry 实现 [P0]

**需求说明**:
实现 Skill 注册中心，管理所有已安装 Skill 的元数据和状态。

**验收标准**:
- [ ] 支持 Skill 注册/注销
- [ ] 支持版本管理
- [ ] 支持状态查询 (HEALTHY, UNHEALTHY, FAILED)
- [ ] 支持依赖关系管理
- [ ] 提供 REST API 接口

**接口定义**:
```java
public interface SkillRegistry {
    void register(String skillId, SkillRegistration registration);
    void unregister(String skillId);
    SkillRegistration getRegistration(String skillId);
    List<String> getAllSkillIds();
    boolean hasSkill(String skillId);
    void updateHealth(String skillId, HealthStatus status);
}
```

**交付物**:
- `net.ooder.sdk.registry.SkillRegistry`
- `net.ooder.sdk.registry.SkillRegistration`
- `net.ooder.sdk.registry.HealthStatus`

**预计工时**: 3天
**负责人**: SDK 团队

---

#### Task 1.2: 生命周期管理器 [P0]

**需求说明**:
实现完整的 Skill 生命周期管理，从发现到销毁的全流程控制。

**验收标准**:
- [ ] 实现 10 个生命周期阶段
- [ ] 支持状态流转控制
- [ ] 支持事件发布
- [ ] 支持异常处理和回滚
- [ ] 提供可视化状态监控

**生命周期状态机**:
```
DISCOVERED → DOWNLOADED → VERIFIED → INSTALLED → REGISTERED 
    → STARTED → HEALTHY → UPDATING → STOPPED → UNINSTALLED → DESTROYED
```

**交付物**:
- `net.ooder.sdk.lifecycle.SkillLifecycleManager`
- `net.ooder.sdk.lifecycle.LifecycleState`
- `net.ooder.sdk.lifecycle.LifecycleEvent`

**预计工时**: 5天
**负责人**: SDK 团队

---

#### Task 1.3: ClassLoader 管理 [P0]

**需求说明**:
实现 Skill 隔离的 ClassLoader，支持动态加载和卸载。

**验收标准**:
- [ ] 支持 JAR 动态加载
- [ ] 支持类隔离
- [ ] 支持资源隔离
- [ ] 支持卸载时资源释放
- [ ] 避免类泄漏

**交付物**:
- `net.ooder.sdk.classloader.SkillClassLoader`
- `net.ooder.sdk.classloader.ClassLoaderManager`

**预计工时**: 3天
**负责人**: SDK 团队

---

#### Task 1.4: 驱动管理器 [P0]

**需求说明**:
实现 Driver Skill 的注册、管理和健康检查。

**验收标准**:
- [ ] 支持驱动注册/注销
- [ ] 支持驱动健康检查
- [ ] 支持驱动热升级
- [ ] 支持驱动依赖管理

**交付物**:
- `net.ooder.sdk.driver.DriverSkillManager`
- `net.ooder.sdk.driver.DriverSkill`
- `net.ooder.sdk.driver.DriverHealthChecker`

**预计工时**: 2天
**负责人**: SDK 团队

---

### Phase 2: 安全与更新 (Week 3-4)

#### Task 2.1: 验证机制 [P1]

**需求说明**:
实现 Skill 包的完整性验证和安全检查。

**验收标准**:
- [ ] SHA256 完整性校验
- [ ] GPG 签名验证
- [ ] 元数据校验
- [ ] 病毒扫描接口

**交付物**:
- `net.ooder.sdk.security.SkillVerifier`
- `net.ooder.sdk.security.SignatureValidator`
- `net.ooder.sdk.security.HashCalculator`

**预计工时**: 2天
**负责人**: SDK 团队 + 安全团队

---

#### Task 2.2: 自动更新 [P1]

**需求说明**:
实现 Skill 的自动检测更新和热更新。

**验收标准**:
- [ ] 版本对比检测
- [ ] 自动下载更新
- [ ] 热更新支持
- [ ] 蓝绿部署支持
- [ ] 回滚支持

**交付物**:
- `net.ooder.sdk.update.SkillUpdater`
- `net.ooder.sdk.update.UpdateChecker`
- `net.ooder.sdk.update.HotSwapManager`

**预计工时**: 3天
**负责人**: SDK 团队

---

#### Task 2.3: 卸载功能 [P1]

**需求说明**:
实现 Skill 的优雅卸载和资源清理。

**验收标准**:
- [ ] 优雅停机
- [ ] 状态保存
- [ ] 资源释放
- [ ] 配置清理
- [ ] 数据备份选项

**交付物**:
- `net.ooder.sdk.uninstall.SkillUninstaller`
- `net.ooder.sdk.uninstall.CleanupManager`

**预计工时**: 2天
**负责人**: SDK 团队

---

### Phase 3: 发现机制 (Week 5-6)

#### Task 3.1: mDNS/DNS-SD 实现 [P2]

**需求说明**:
实现基于 mDNS 的零配置发现机制。

**验收标准**:
- [ ] 支持服务广播
- [ ] 支持服务发现
- [ ] 支持服务解析
- [ ] 跨平台支持

**交付物**:
- `net.ooder.sdk.discovery.mdns.MdnsDiscoveryService`
- `net.ooder.sdk.discovery.mdns.MdnsServiceInfo`

**预计工时**: 3天
**负责人**: SDK 团队

---

#### Task 3.2: DHT 实现 [P2]

**需求说明**:
实现基于 Kademlia 的分布式发现机制。

**验收标准**:
- [ ] 实现 Kademlia 协议
- [ ] 支持节点发现
- [ ] 支持数据存储/查询
- [ ] 支持网络穿透

**交付物**:
- `net.ooder.sdk.discovery.dht.DhtDiscoveryService`
- `net.ooder.sdk.discovery.dht.KademliaNode`

**预计工时**: 5天
**负责人**: SDK 团队

---

#### Task 3.3: SkillCenter API [P2]

**需求说明**:
实现中心化的 SkillCenter 服务接口。

**验收标准**:
- [ ] 支持 Skill 上传/下载
- [ ] 支持版本管理
- [ ] 支持权限控制
- [ ] 支持审批流程

**交付物**:
- `net.ooder.sdk.discovery.center.SkillCenterClient`
- `net.ooder.sdk.discovery.center.SkillCenterApi`

**预计工时**: 3天
**负责人**: SDK 团队 + 后端团队

---

### Phase 4: 优化完善 (Week 7-8)

#### Task 4.1: 性能优化 [P3]

**需求说明**:
优化 SDK 性能，减少资源占用。

**验收标准**:
- [ ] 启动时间 < 5s
- [ ] 内存占用 < 256MB
- [ ] Skill 切换 < 1s
- [ ] 支持 100+ Skill 并发

**预计工时**: 3天
**负责人**: SDK 团队

---

#### Task 4.2: 监控完善 [P3]

**需求说明**:
完善 SDK 监控和日志。

**验收标准**:
- [ ] 支持 Metrics 上报
- [ ] 支持分布式追踪
- [ ] 支持日志聚合
- [ ] 提供监控 Dashboard

**交付物**:
- `net.ooder.sdk.monitoring.SdkMetrics`
- `net.ooder.sdk.monitoring.SdkTracer`

**预计工时**: 2天
**负责人**: SDK 团队

---

#### Task 4.3: 文档完善 [P3]

**需求说明**:
完善 SDK 开发文档和示例。

**验收标准**:
- [ ] API 文档完整
- [ ] 开发指南完整
- [ ] 示例代码完整
- [ ] 视频教程 3+

**预计工时**: 2天
**负责人**: SDK 团队 + 文档团队

---

## 三、接口契约

### 3.1 Skill 生命周期接口

```java
package net.ooder.sdk.lifecycle;

public interface SkillLifecycle {
    
    // 阶段1: 发现
    List<SkillInfo> discover();
    
    // 阶段2: 下载
    DownloadResult download(String skillId, String version);
    
    // 阶段3: 验证
    boolean verify(Path jarPath, String expectedHash);
    
    // 阶段4: 安装
    InstallResult install(String skillId, boolean autoInstallDeps);
    
    // 阶段5: 注册
    void register(String skillId, Path jarPath);
    
    // 阶段6: 启动
    void start(String skillId);
    
    // 阶段7: 健康检查
    HealthStatus checkHealth(String skillId);
    
    // 阶段8: 更新
    UpdateResult update(String skillId, boolean hotSwap);
    
    // 阶段9: 卸载
    UninstallResult uninstall(String skillId, boolean preserveData);
    
    // 阶段10: 销毁
    void destroy(String skillId);
}
```

### 3.2 驱动管理接口

```java
package net.ooder.sdk.driver;

public interface DriverSkill {
    String getDriverType();
    String getVersion();
    void initialize();
    boolean isHealthy();
    void recover();
    void shutdown();
}

public interface DriverSkillManager {
    void registerDriver(String driverType, DriverSkill driver);
    DriverSkill getDriver(String driverType);
    boolean hasDriver(String driverType);
    void healthCheck();
    void upgradeDriver(String driverType, String newVersion);
}
```

### 3.3 发现服务接口

```java
package net.ooder.sdk.discovery;

public interface DiscoveryService {
    String getDiscoveryType();
    void start();
    void stop();
    List<SkillInfo> discover();
    boolean isAvailable();
}

public interface DiscoveryManager {
    void registerDiscovery(DiscoveryService discovery);
    void unregisterDiscovery(String type);
    List<SkillInfo> discoverAll();
    List<SkillInfo> discoverByType(String type);
}
```

---

## 四、依赖关系

```
SkillLifecycleManager
    ├── SkillRegistry
    ├── ClassLoaderManager
    ├── DriverSkillManager
    │   └── DriverHealthChecker
    ├── SkillVerifier
    │   ├── SignatureValidator
    │   └── HashCalculator
    ├── SkillUpdater
    │   └── HotSwapManager
    └── DiscoveryManager
        ├── MdnsDiscoveryService
        ├── DhtDiscoveryService
        └── SkillCenterClient
```

---

## 五、测试要求

### 5.1 单元测试

- 覆盖率 > 80%
- 核心类 100% 覆盖
- 边界条件测试

### 5.2 集成测试

- Skill 完整生命周期测试
- 多 Skill 并发测试
- 故障恢复测试

### 5.3 性能测试

- 启动时间测试
- 内存占用测试
- 并发性能测试

---

## 六、里程碑

| 里程碑 | 日期 | 交付物 | 验收标准 |
|--------|------|--------|----------|
| M1 | Week 2 | 核心闭环 | 10 个生命周期阶段全部实现 |
| M2 | Week 4 | 安全更新 | 验证、更新、卸载功能完成 |
| M3 | Week 6 | 发现机制 | 9 种发现方式全部实现 |
| M4 | Week 8 | SDK 2.3 GA | 性能达标、文档完整 |

---

## 七、风险与应对

| 风险 | 概率 | 影响 | 应对措施 |
|------|------|------|----------|
| ClassLoader 内存泄漏 | 中 | 高 | 代码审查、压力测试 |
| 热更新失败 | 中 | 高 | 完善回滚机制 |
| DHT 网络不稳定 | 高 | 中 | 多源发现 fallback |
| 进度延期 | 中 | 中 | 每日站会、及时调整 |

---

## 八、沟通机制

### 8.1 日常沟通

- **每日站会**: 09:30，15分钟
- **周会**: 周五 16:00，1小时
- **紧急问题**: 即时通讯群

### 8.2 文档更新

- **技术文档**: 随代码更新
- **进度更新**: 每周五
- **里程碑评审**: 里程碑达成时

---

## 九、附录

### 9.1 参考文档

- [SDK 2.3 升级指南](./SDK_2.3_UPGRADE_GUIDE.md)
- [发现机制分析](./DISCOVERY_MECHANISM_ANALYSIS.md)
- [能力发现协议](../v0.8.0/CAPABILITY-DISCOVERY-PROTOCOL.md)

### 9.2 相关代码

- `skill-mqtt/src/main/java/net/ooder/skill/mqtt/discovery/SkillDiscoveryService.java`
- `skill-mqtt/src/main/java/net/ooder/skill/mqtt/discovery/SysConfigService.java`

---

**文档版本**: 1.0  
**最后更新**: 2026-02-24  
**下次评审**: 2026-03-03
