# SDK 协作任务清单

> **版本**: v2.0  
> **日期**: 2026-02-27  
> **目标**: 第一阶段 Nexus-UI Skill 迁移

---

## 一、SDK v2.3 已实现功能清单

### 1.1 Discovery 发现服务 ✅ 已完整实现

| 组件 | 包路径 | 状态 | 说明 |
|------|--------|------|------|
| SkillDiscoveryService | `net.ooder.sdk.discovery` | ✅ 完成 | 文件系统、mDNS、远程仓库发现 |
| DiscoveryManager | `net.ooder.sdk.discovery` | ✅ 完成 | 发现管理器接口 |
| SkillYamlParser | `net.ooder.sdk.discovery` | ✅ 完成 | skill.yaml 解析器 |
| GitHubDiscoverer | `net.ooder.sdk.discovery.git` | ✅ 完成 | GitHub 仓库发现 |
| GiteeDiscoverer | `net.ooder.sdk.discovery.git` | ✅ 完成 | Gitee 仓库发现 |

### 1.2 Capability 能力服务 ✅ 已完整实现

| 组件 | 包路径 | 状态 | 说明 |
|------|--------|------|------|
| CapabilityCenter | `net.ooder.sdk.service.capability` | ✅ 完成 | 能力中心 |
| CapabilityMgtService | `net.ooder.sdk.service.capability.mgt` | ✅ 完成 | 能力管理 |
| CapabilityDistService | `net.ooder.sdk.service.capability.dist` | ✅ 完成 | 能力分发 |
| CapabilityCoopService | `net.ooder.sdk.service.capability.coop` | ✅ 完成 | 能力协作 |
| CapabilityMonService | `net.ooder.sdk.service.capability.mon` | ✅ 完成 | 能力监控 |
| CapabilitySpecService | `net.ooder.sdk.service.capability.spec` | ✅ 完成 | 能力规范 |
| CapRegistry | `net.ooder.sdk.api.capability` | ✅ 完成 | 能力注册接口 |
| InMemoryCapRegistry | `net.ooder.sdk.core.capability.impl` | ✅ 完成 | 内存能力注册实现 |

### 1.3 Skill 服务 ✅ 已完整实现

| 组件 | 包路径 | 状态 | 说明 |
|------|--------|------|------|
| SkillService | `net.ooder.sdk.service.skill` | ✅ 完成 | Skill 生命周期管理 |
| SkillUpdater | `net.ooder.sdk.service.skill.update` | ✅ 完成 | Skill 更新服务 |
| SkillCenterService | `net.ooder.sdk.service.skillcenter` | ✅ 完成 | Skill 中心服务 |

### 1.4 Network 网络服务 ✅ 已完整实现

| 组件 | 包路径 | 状态 | 说明 |
|------|--------|------|------|
| NetworkServiceImpl | `net.ooder.sdk.service.network` | ✅ 完成 | 网络服务实现 |
| LinkManager | `net.ooder.sdk.service.network.link` | ✅ 完成 | 链路管理 |
| RouteManager | `net.ooder.sdk.service.network.route` | ✅ 完成 | 路由管理 |
| PeerDiscovery | `net.ooder.sdk.service.network.p2p` | ✅ 完成 | P2P 发现 |

### 1.5 Storage 存储服务 ✅ 已完整实现

| 组件 | 包路径 | 状态 | 说明 |
|------|--------|------|------|
| StorageServiceImpl | `net.ooder.sdk.service.storage` | ✅ 完成 | 存储服务实现 |
| CacheManager | `net.ooder.sdk.service.storage.cache` | ✅ 完成 | 缓存管理 |
| PersistenceService | `net.ooder.sdk.service.storage.persistence` | ✅ 完成 | 持久化服务 |
| VfsManager | `net.ooder.sdk.service.storage.vfs` | ✅ 完成 | VFS 管理 |

### 1.6 Security 安全服务 ✅ 已完整实现

| 组件 | 包路径 | 状态 | 说明 |
|------|--------|------|------|
| SecurityServiceImpl | `net.ooder.sdk.service.security` | ✅ 完成 | 安全服务实现 |
| AuthManager | `net.ooder.sdk.service.security.auth` | ✅ 完成 | 认证管理 |
| PermissionManager | `net.ooder.sdk.service.security.permission` | ✅ 完成 | 权限管理 |
| CertificateManager | `net.ooder.sdk.service.security.cert` | ✅ 完成 | 证书管理 |

### 1.7 Heartbeat 心跳服务 ✅ 已完整实现

| 组件 | 包路径 | 状态 | 说明 |
|------|--------|------|------|
| HeartbeatService | `net.ooder.sdk.service.heartbeat` | ✅ 完成 | 心跳服务 |
| EnhancedHeartbeatService | `net.ooder.sdk.service.heartbeat` | ✅ 完成 | 增强心跳服务 |

### 1.8 Agent 服务 ✅ 已完整实现

| 组件 | 包路径 | 状态 | 说明 |
|------|--------|------|------|
| EndAgentImpl | `net.ooder.sdk.core.agent.impl` | ✅ 完成 | 终端 Agent |
| McpAgentImpl | `net.ooder.sdk.core.agent.impl` | ✅ 完成 | MCP Agent |
| RouteAgentImpl | `net.ooder.sdk.core.agent.impl` | ✅ 完成 | 路由 Agent |
| SceneAgentImpl | `net.ooder.sdk.core.agent.impl` | ✅ 完成 | 场景 Agent |
| WorkerAgentImpl | `net.ooder.sdk.core.agent.impl` | ✅ 完成 | 工作 Agent |

---

## 二、Nexus 工程需要补充的功能

### 2.1 不需要新建的组件

| 组件 | 原计划 | 实际情况 |
|------|--------|----------|
| NexusUiSkillRegistry | ❌ 取消 | 直接使用 `SkillDiscoveryService` |
| NexusUiSkillInstaller | ❌ 取消 | 直接使用 `SkillService` |
| DiscoveryService | ❌ 取消 | SDK 已有 `SkillDiscoveryService` |

### 2.2 需要补充的配置

| 任务 | 说明 | 工作量 |
|------|------|--------|
| Nexus-UI Skill 类型配置 | 扩展 skill.yaml 支持 nexusUi 配置 | 0.5天 |
| 静态资源映射配置 | 扩展 StaticResourceConfig | 0.5天 |
| 菜单动态注册 | 基于现有菜单配置扩展 | 0.5天 |

---

## 三、协作任务分配（修正版）

### 3.1 Nexus 团队任务

| 任务ID | 任务名称 | 优先级 | 工作量 | 状态 |
|--------|----------|--------|--------|------|
| NX-001 | 扩展 skill.yaml 支持 nexusUi 配置 | P0 | 0.5天 | ⏳ 待开始 |
| NX-002 | 扩展 StaticResourceConfig 支持 skills 目录 | P0 | 0.5天 | ⏳ 待开始 |
| NX-003 | 实现菜单动态注册（基于现有 menu-config.json） | P0 | 0.5天 | ⏳ 待开始 |
| NX-004 | 集成 SDK SkillDiscoveryService | P0 | 0.5天 | ⏳ 待开始 |
| NX-005 | 集成 SDK SkillService | P0 | 0.5天 | ⏳ 待开始 |

### 3.2 Skills 团队任务

| 任务ID | 任务名称 | 优先级 | 工作量 | 状态 |
|--------|----------|--------|--------|------|
| SK-001 | 迁移 nexus-dashboard 页面 | P0 | 0.5天 | ⏳ 待开始 |
| SK-002 | 迁移 system-status 页面 | P0 | 0.5天 | ⏳ 待开始 |
| SK-003 | 迁移 personal-dashboard 页面 | P0 | 0.5天 | ⏳ 待开始 |
| SK-004 | 迁移 health-check 页面 | P0 | 0.5天 | ⏳ 待开始 |
| SK-005 | 迁移 storage-management 页面 | P0 | 0.5天 | ⏳ 待开始 |

---

## 四、SDK 集成方案

### 4.1 使用 SkillDiscoveryService

```java
// Nexus 启动时发现 Skill
@Service
public class NexusSkillManager {
    
    private final SkillDiscoveryService discoveryService;
    
    public void discoverSkills() {
        // 添加扫描目录
        discoveryService.addScanDirectory(new File("./skills"));
        
        // 启动自动扫描
        discoveryService.startAutoScan();
        
        // 获取已发现的 Skill
        List<DiscoveredSkill> skills = discoveryService.getAllDiscoveredSkills();
        
        // 注册菜单
        for (DiscoveredSkill skill : skills) {
            registerMenu(skill);
        }
    }
}
```

### 4.2 使用 SkillService

```java
// Nexus 使用 SDK SkillService
@Service
public class NexusSkillInstaller {
    
    private final SkillService skillService;
    
    public void installSkill(String skillId) {
        // 安装 Skill
        SkillInstallResult result = skillService.installSkill("nexus", skillId);
        
        // 启动 Skill
        skillService.startSkill("nexus", skillId);
    }
    
    public void uninstallSkill(String skillId) {
        // 停止 Skill
        skillService.stopSkill("nexus", skillId);
        
        // 卸载 Skill
        skillService.uninstallSkill("nexus", skillId);
    }
}
```

### 4.3 静态资源映射配置

```java
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 现有配置
        registry.addResourceHandler("/console/**")
                .addResourceLocations("classpath:/static/console/");
        
        // 新增: Nexus-UI Skill 静态资源
        registry.addResourceHandler("/console/skills/**")
                .addResourceLocations("file:./skills/")
                .setCachePeriod(0);
    }
}
```

---

## 五、任务依赖关系（修正版）

```
┌─────────────────────────────────────────────────────────────────┐
│                      任务依赖关系图                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Nexus 团队                                                     │
│  ──────────                                                     │
│  NX-001 (skill.yaml 扩展)                                       │
│      │                                                          │
│      ├──> NX-004 (集成 SkillDiscoveryService)                   │
│      │         │                                                │
│      │         └──> NX-003 (菜单动态注册)                        │
│      │                                                          │
│      └──> NX-002 (静态资源映射)                                  │
│                │                                                │
│                └──> NX-005 (集成 SkillService)                   │
│                                                                 │
│  Skills 团队                                                    │
│  ───────────                                                    │
│  NX-001 + NX-002 完成                                           │
│      │                                                          │
│      ├──> SK-001 (迁移 nexus-dashboard)                         │
│      ├──> SK-002 (迁移 system-status)                           │
│      ├──> SK-003 (迁移 personal-dashboard)                      │
│      ├──> SK-004 (迁移 health-check)                            │
│      └──> SK-005 (迁移 storage-management)                      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 六、时间计划（修正版）

### 6.1 第一周

| 日期 | Nexus 团队 | Skills 团队 |
|------|------------|-------------|
| Day 1 | NX-001, NX-002 | - |
| Day 2 | NX-003, NX-004, NX-005 | - |
| Day 3 | 测试验证 | SK-001, SK-002 |
| Day 4 | - | SK-003, SK-004 |
| Day 5 | - | SK-005, 测试 |

### 6.2 里程碑

| 里程碑 | 日期 | 交付物 |
|--------|------|--------|
| M1: SDK 集成完成 | Day 2 | SkillDiscoveryService + SkillService 集成 |
| M2: 流程跑通 | Day 3 | 发现→安装→运行流程 |
| M3: 首批迁移完成 | Day 5 | 5个 Nexus-UI Skill |

---

## 七、SDK 已实现功能汇总

### 7.1 核心服务层

```
net.ooder.sdk.service/
├── capability/           # 能力服务 ✅
│   ├── CapabilityCenter
│   ├── CapabilityMgtService
│   ├── CapabilityDistService
│   ├── CapabilityCoopService
│   ├── CapabilityMonService
│   └── CapabilitySpecService
├── skill/                # Skill 服务 ✅
│   ├── SkillService
│   └── update/SkillUpdater
├── network/              # 网络服务 ✅
│   ├── NetworkServiceImpl
│   ├── link/LinkManager
│   ├── route/RouteManager
│   └── p2p/PeerDiscovery
├── storage/              # 存储服务 ✅
│   ├── StorageServiceImpl
│   ├── cache/CacheManager
│   ├── persistence/PersistenceService
│   └── vfs/VfsManager
├── security/             # 安全服务 ✅
│   ├── SecurityServiceImpl
│   ├── auth/AuthManager
│   ├── permission/PermissionManager
│   └── cert/CertificateManager
├── heartbeat/            # 心跳服务 ✅
│   ├── HeartbeatService
│   └── EnhancedHeartbeatService
└── monitoring/           # 监控服务 ✅
    ├── health/HealthMonitor
    ├── metrics/MetricsCollector
    └── alert/AlertManager
```

### 7.2 Discovery 发现层

```
net.ooder.sdk.discovery/
├── SkillDiscoveryService    # Skill 发现服务 ✅
├── DiscoveryManager         # 发现管理器 ✅
├── SkillYamlParser          # YAML 解析器 ✅
└── git/
    ├── GitHubDiscoverer     # GitHub 发现 ✅
    ├── GiteeDiscoverer      # Gitee 发现 ✅
    └── GitRepositoryDiscoverer
```

---

## 八、总结

### 8.1 关键发现

1. **SDK 已完整实现核心功能**：Discovery、Capability、Skill、Network、Storage、Security、Heartbeat
2. **不需要重复开发**：直接集成 SDK 现有服务
3. **Nexus 只需做配置扩展**：skill.yaml 扩展、静态资源映射、菜单注册

### 8.2 工作量修正

| 项目 | 原计划 | 修正后 |
|------|--------|--------|
| Nexus 团队 | 3天 | 2天 |
| Skills 团队 | 2.5天 | 2.5天 |
| **总计** | **5.5天** | **4.5天** |

---

**文档版本**: v2.0  
**创建日期**: 2026-02-27  
**最后更新**: 2026-02-27
