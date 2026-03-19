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

### 2.1 接口分层架构

```
┌─────────────────────────────────────────────────────────────────┐
│                    接口分层架构设计                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Layer 1: SEC 通用接口 (Engine Team 定义)                       │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ NetworkConfigProvider    - 网络配置管理                  │   │
│  │ DeviceManagementProvider - 设备管理                      │   │
│  │ SecurityConfigProvider   - 安全配置管理                  │   │
│  │ HealthCheckProvider      - 健康检查                      │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│                              │ 继承/扩展                        │
│                              ▼                                  │
│  Layer 2: 驱动特有接口 (Skills Team 定义)                       │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ OpenWrtDriver            - OpenWrt特有方法               │   │
│  │ KubernetesDriver         - K8s特有方法                   │   │
│  │ AliyunDriver             - 阿里云特有方法                │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│                              │ 实现                            │
│                              ▼                                  │
│  Layer 3: 驱动实现 (Skills Team 实现)                           │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ OpenWrtDriverImpl        - OpenWrt具体实现               │   │
│  │ KubernetesDriverImpl     - K8s具体实现                   │   │
│  │ AliyunDriverImpl         - 阿里云具体实现                │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 SEC 通用接口定义请求

#### 2.2.1 NetworkConfigProvider (网络配置管理)

```java
package net.ooder.scene.provider;

/**
 * 网络配置Provider接口
 * 
 * <p>定义网络配置管理的通用操作，由具体驱动实现</p>
 */
public interface NetworkConfigProvider extends BaseProvider {

    // 网络设置
    Result<NetworkSetting> getNetworkSetting(String settingType);
    Result<List<NetworkSetting>> getAllNetworkSettings();
    Result<NetworkSetting> updateNetworkSetting(String settingType, Map<String, Object> data);
    
    // IP地址管理
    Result<List<IPAddress>> getIPAddresses(String type, String status);
    Result<IPAddress> addStaticIPAddress(Map<String, Object> ipData);
    Result<IPAddress> deleteIPAddress(String ipId);
    
    // IP黑名单
    Result<List<IPBlacklist>> getIPBlacklist();
    Result<IPBlacklist> addIPToBlacklist(Map<String, Object> data);
    Result<IPBlacklist> removeIPFromBlacklist(String id);
    
    // 网络设备
    Result<List<NetworkDevice>> getNetworkDevices();
}
```

#### 2.2.2 DeviceManagementProvider (设备管理)

```java
package net.ooder.scene.provider;

/**
 * 设备管理Provider接口
 * 
 * <p>定义设备管理的通用操作，由具体驱动实现</p>
 */
public interface DeviceManagementProvider extends BaseProvider {

    // 设备连接
    Result<Boolean> connect(Map<String, Object> connectionData);
    Result<Boolean> disconnect();
    Result<ConnectionStatus> getConnectionStatus();
    
    // 设备信息
    Result<DeviceInfo> getDeviceInfo();
    Result<SystemStatus> getSystemStatus();
    Result<String> getFirmwareVersion();
    
    // 设备操作
    Result<Boolean> reboot();
    Result<Boolean> reset();
    Result<Boolean> upgradeFirmware(String firmwareUrl);
    
    // 命令执行
    Result<CommandResult> executeCommand(String command);
    Result<CommandResult> executeScript(String script);
}
```

#### 2.2.3 SecurityConfigProvider (安全配置管理)

```java
package net.ooder.scene.provider;

/**
 * 安全配置Provider接口
 * 
 * <p>定义安全配置管理的通用操作，由具体驱动实现</p>
 */
public interface SecurityConfigProvider extends BaseProvider {

    // 防火墙
    Result<FirewallStatus> getFirewallStatus();
    Result<Boolean> enableFirewall();
    Result<Boolean> disableFirewall();
    Result<List<FirewallRule>> getFirewallRules();
    Result<FirewallRule> addFirewallRule(Map<String, Object> ruleData);
    Result<Boolean> deleteFirewallRule(String ruleId);
    
    // 访问控制
    Result<List<AccessRule>> getAccessRules();
    Result<AccessRule> addAccessRule(Map<String, Object> ruleData);
    Result<Boolean> deleteAccessRule(String ruleId);
}
```

#### 2.2.4 HealthCheckProvider (健康检查)

```java
package net.ooder.scene.provider;

/**
 * 健康检查Provider接口
 * 
 * <p>定义健康检查的通用操作，由具体驱动实现</p>
 */
public interface HealthCheckProvider extends BaseProvider {

    Result<HealthCheckResult> runHealthCheck(Map<String, Object> params);
    Result<HealthReport> exportHealthReport();
    Result<HealthCheckSchedule> scheduleHealthCheck(Map<String, Object> params);
    Result<ServiceCheckResult> checkService(String serviceName);
}
```

### 2.3 驱动特有接口定义 (Skills Team负责)

#### 2.3.1 OpenWrtDriver 接口

```java
package net.ooder.skill.openwrt.driver;

import net.ooder.scene.provider.*;

/**
 * OpenWrt驱动接口
 * 
 * <p>继承SEC通用接口，定义OpenWrt特有方法</p>
 */
public interface OpenWrtDriver 
        extends NetworkConfigProvider, DeviceManagementProvider, SecurityConfigProvider {

    // OpenWrt特有方法 - UCI配置
    Result<String> getUciConfig(String configPath);
    Result<Boolean> setUciConfig(String configPath, Map<String, Object> config);
    Result<Boolean> commitUciConfig(String configPath);
    
    // OpenWrt特有方法 - 无线
    Result<List<WifiNetwork>> getWifiNetworks();
    Result<WifiNetwork> getWifiNetwork(String networkId);
    Result<Boolean> updateWifiNetwork(String networkId, Map<String, Object> config);
    Result<Boolean> scanWifiNetworks();
    
    // OpenWrt特有方法 - DHCP
    Result<List<DhcpLease>> getDhcpLeases();
    Result<List<StaticLease>> getStaticLeases();
    Result<StaticLease> addStaticLease(Map<String, Object> leaseData);
    Result<Boolean> deleteStaticLease(String leaseId);
    
    // OpenWrt特有方法 - 系统
    Result<List<PackageInfo>> listPackages();
    Result<Boolean> installPackage(String packageName);
    Result<Boolean> removePackage(String packageName);
    Result<Boolean> updatePackages();
    
    // OpenWrt特有方法 - 日志
    Result<List<LogEntry>> getSystemLogs(int lines);
    Result<List<LogEntry>> getKernelLogs(int lines);
}
```

### 2.4 interface.yaml 定义

驱动特有方法通过 `interface.yaml` 定义，供能力管理中心和调用方使用：

```yaml
# skills/skill-openwrt/src/main/resources/interface.yaml
apiVersion: agent.ooder.net/v1
kind: InterfaceDefinition

metadata:
  sceneId: scene-openwrt
  version: 1.0.0

spec:
  # 继承SEC通用接口
  extends:
    - NetworkConfigProvider
    - DeviceManagementProvider
    - SecurityConfigProvider
  
  # OpenWrt特有方法
  capabilities:
    uci-config:
      getUciConfig:
        input:
          type: object
          properties:
            configPath:
              type: string
              description: UCI配置路径
        output:
          type: string
          description: UCI配置内容
          
      setUciConfig:
        input:
          type: object
          properties:
            configPath:
              type: string
            config:
              type: object
        output:
          type: boolean
          
    wifi:
      getWifiNetworks:
        input:
          type: object
          properties: {}
        output:
          type: array
          items:
            $ref: "#/components/schemas/WifiNetwork"
            
      scanWifiNetworks:
        input:
          type: object
          properties: {}
        output:
          type: boolean
          
    dhcp:
      getDhcpLeases:
        input:
          type: object
          properties: {}
        output:
          type: array
          items:
            $ref: "#/components/schemas/DhcpLease"
            
    packages:
      listPackages:
        input:
          type: object
          properties: {}
        output:
          type: array
          items:
            $ref: "#/components/schemas/PackageInfo"
            
      installPackage:
        input:
          type: object
          properties:
            packageName:
              type: string
        output:
          type: boolean
          
  components:
    schemas:
      WifiNetwork:
        type: object
        properties:
          networkId:
            type: string
          ssid:
            type: string
          encryption:
            type: string
          channel:
            type: integer
          enabled:
            type: boolean
            
      DhcpLease:
        type: object
        properties:
          ip:
            type: string
          mac:
            type: string
          hostname:
            type: string
          leaseTime:
            type: string
            
      PackageInfo:
        type: object
        properties:
          name:
            type: string
          version:
            type: string
          installed:
            type: boolean
```

### 2.5 职责分工

| 层级 | 定义方 | 实现方 | 发布方 |
|------|--------|--------|--------|
| SEC通用接口 | Engine Team | Skills Team | scene-engine |
| 驱动特有接口 | Skills Team | Skills Team | interface.yaml |
| 驱动实现 | - | Skills Team | 独立发布到能力管理中心 |

### 2.6 版本规划

| 版本 | scene-engine | ooder-skills |
|------|--------------|--------------|
| 0.7.3 | 现有Provider | 3个Provider实现 |
| 0.8.0 | NetworkConfigProvider, DeviceManagementProvider | OpenWrtDriver接口定义 |
| 0.9.0 | SecurityConfigProvider, HealthCheckProvider | OpenWrtDriverImpl实现 |
| 1.0.0 | 接口完善 | 发布到能力管理中心 |

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
