# ENGINE_COLLABORATION_REQUEST 需求覆盖分析

## 文档信息

| 项目 | 内容 |
|------|------|
| 分析日期 | 2026-02-22 |
| 分析范围 | ENGINE_COLLABORATION_REQUEST.md |
| 分析目标 | 确定现有场景/Skills是否已覆盖需求 |

---

## 一、需求清单

### 1.1 SEC 通用接口需求

| Provider | 描述 | 状态 |
|----------|------|------|
| NetworkConfigProvider | 网络配置管理 | 需Engine Team定义 |
| DeviceManagementProvider | 设备管理 | 需Engine Team定义 |
| SecurityConfigProvider | 安全配置管理 | 需Engine Team定义 |
| HealthCheckProvider | 健康检查 | 需Engine Team定义 |

### 1.2 驱动特有接口需求

| Driver | 描述 | 状态 |
|--------|------|------|
| OpenWrtDriver | OpenWrt路由器驱动 | 需Skills Team实现 |
| KubernetesDriver | K8s容器驱动 | 需Skills Team实现 |
| AliyunDriver | 阿里云驱动 | 需Skills Team实现 |

---

## 二、现有场景覆盖分析

### 2.1 场景覆盖情况

| 需求 | 现有场景 | 覆盖状态 | 说明 |
|------|----------|----------|------|
| 网络配置管理 | `network` | ✅ 已覆盖 | 网络管理场景已定义 |
| 设备管理 | `device` | ✅ 已覆盖 | 设备管理场景已定义 |
| 安全配置管理 | `security` | ✅ 已覆盖 | 安全审计场景已定义 |
| 健康检查 | `health` | ✅ 已覆盖 | 健康检查场景已定义 |
| OpenWrt管理 | `openwrt` | ✅ 已覆盖 | OpenWrt场景已定义 |
| 代理管理 | `agent` | ✅ 已覆盖 | 代理管理场景已定义 |
| 协议管理 | `protocol` | ✅ 已覆盖 | 协议管理场景已定义 |
| 托管服务 | `hosting` | ✅ 已覆盖 | 托管服务场景已定义 |
| 监控运维 | `monitor` | ✅ 已覆盖 | 监控运维场景已定义 |

### 2.2 场景定义详情

```yaml
# 网络管理场景
- sceneId: network
  name: Network Management
  description: 网络管理场景，提供网络配置、IP管理、设备扫描能力
  category: SYSTEM
  requiredCapabilities:
    - network-config
    - ip-management
    - device-scanning

# 安全审计场景
- sceneId: security
  name: Security & Audit
  description: 安全审计场景，提供安全、审计、合规等安全能力
  category: SYSTEM
  requiredCapabilities:
    - security-auth
    - audit-log
    - compliance-check

# 健康检查场景
- sceneId: health
  name: Health Check
  description: 健康检查场景，提供系统健康检查、服务检查、报告生成能力
  category: SYSTEM
  requiredCapabilities:
    - health-check
    - service-check
    - report-generation

# OpenWrt管理场景
- sceneId: openwrt
  name: OpenWrt Management
  description: OpenWrt管理场景，提供OpenWrt路由器连接、配置、命令执行能力
  category: SYSTEM
  requiredCapabilities:
    - router-connection
    - uci-config
    - command-execution
```

---

## 三、现有Skills覆盖分析

### 3.1 Skills覆盖情况

| 需求 | 现有Skill | 覆盖状态 | 说明 |
|------|-----------|----------|------|
| NetworkProvider | `skill-network` | ✅ 已实现 | NetworkProviderImpl已完成 |
| SecurityProvider | `skill-security` | ✅ 已实现 | SecurityProviderImpl已完成 |
| HostingProvider | `skill-hosting` | ✅ 已实现 | HostingProviderImpl已完成 |
| MonitorProvider | `skill-monitor` | ✅ 已实现 | 监控服务已完成 |
| HealthProvider | - | ⬜ 待实现 | 需新增skill-health |
| OpenWrtDriver | - | ⬜ 待实现 | 需新增skill-openwrt |
| AgentProvider | - | ⬜ 待实现 | 需新增skill-agent |

### 3.2 已实现Provider详情

#### NetworkProviderImpl
- **位置**: `skills/skill-network/src/main/java/net/ooder/skill/network/provider/NetworkProviderImpl.java`
- **状态**: ✅ 已完成
- **方法**: getStatus, getStats, listLinks, getLink, disconnectLink, reconnectLink, listRoutes, getRoute, findRoute, getTopology, getQuality

#### SecurityProviderImpl
- **位置**: `skills/skill-security/src/main/java/net/ooder/skill/security/provider/SecurityProviderImpl.java`
- **状态**: ✅ 已完成
- **方法**: getStatus, getStats, listPolicies, createPolicy, updatePolicy, deletePolicy, enablePolicy, disablePolicy, listAcls, createAcl, deleteAcl, checkPermission, listThreats, getThreat, resolveThreat, runSecurityScan, toggleFirewall, isFirewallEnabled

#### HostingProviderImpl
- **位置**: `skills/skill-hosting/src/main/java/net/ooder/skill/hosting/provider/HostingProviderImpl.java`
- **状态**: ✅ 已完成
- **方法**: getAllInstances, getInstances, getInstance, createInstance, updateInstance, deleteInstance, startInstance, stopInstance, restartInstance, scaleInstance, getHealth, getMetrics, getLogs, executeCommand, getSupportedTypes, getQuota

---

## 四、结论

### 4.1 场景层面

**结论**: ✅ **现有场景已完全覆盖协作请求需求**

| 需求类型 | 场景覆盖 | 状态 |
|----------|----------|------|
| 网络配置管理 | network | ✅ |
| 设备管理 | device | ✅ |
| 安全配置管理 | security | ✅ |
| 健康检查 | health | ✅ |
| OpenWrt管理 | openwrt | ✅ |
| 代理管理 | agent | ✅ |
| 协议管理 | protocol | ✅ |

### 4.2 Skills层面

**结论**: ⚠️ **部分Skills已实现，部分待实现**

| Provider | 场景 | Skill | 实现状态 |
|----------|------|-------|----------|
| NetworkProvider | network | skill-network | ✅ 已实现 |
| SecurityProvider | security | skill-security | ✅ 已实现 |
| HostingProvider | hosting | skill-hosting | ✅ 已实现 |
| MonitorProvider | monitor | skill-monitor | ✅ 已实现 |
| HealthProvider | health | - | ⬜ 待实现 |
| AgentProvider | agent | - | ⬜ 待实现 |
| OpenWrtDriver | openwrt | - | ⬜ 待实现 |

### 4.3 接口层面

**结论**: ⚠️ **SEC通用接口需Engine Team定义**

| 接口 | 定义方 | 状态 |
|------|--------|------|
| NetworkConfigProvider | Engine Team | ⬜ 待定义 |
| DeviceManagementProvider | Engine Team | ⬜ 待定义 |
| SecurityConfigProvider | Engine Team | ⬜ 待定义 |
| HealthCheckProvider | Engine Team | ⬜ 待定义 |

---

## 五、建议

### 5.1 无需新增场景

现有场景已完全覆盖协作请求中的需求，无需新增场景。

### 5.2 需新增Skills

| 优先级 | Skill | 场景 | 说明 |
|--------|-------|------|------|
| P0 | skill-health | health | 健康检查服务 |
| P1 | skill-agent | agent | 代理管理服务 |
| P1 | skill-openwrt | openwrt | OpenWrt驱动实现 |

### 5.3 需Engine Team配合

| 任务 | 负责方 | 说明 |
|------|--------|------|
| 定义SEC通用接口 | Engine Team | NetworkConfigProvider, DeviceManagementProvider, SecurityConfigProvider, HealthCheckProvider |
| 发布scene-engine 0.8.0 | Engine Team | 包含新接口定义 |

---

## 六、行动计划

### 6.1 立即可执行（Skills Team）

| 任务 | 状态 |
|------|------|
| 创建 skill-health | ⬜ 待开始 |
| 创建 skill-agent | ⬜ 待开始 |
| 创建 skill-openwrt | ⬜ 待开始 |

### 6.2 需等待Engine Team

| 任务 | 依赖 |
|------|------|
| 实现 NetworkConfigProvider | scene-engine 0.8.0 |
| 实现 DeviceManagementProvider | scene-engine 0.8.0 |
| 实现 SecurityConfigProvider | scene-engine 0.9.0 |
| 实现 HealthCheckProvider | scene-engine 0.9.0 |

---

## 七、总结

| 维度 | 结论 |
|------|------|
| 场景覆盖 | ✅ 完全覆盖，无需新增 |
| Skills实现 | ⚠️ 部分完成，需补充3个 |
| 接口定义 | ⬜ 需Engine Team配合 |

**最终结论**: 协作请求中的需求在**场景层面已完全覆盖**，在**Skills层面需补充实现**。建议按优先级逐步完成skill-health、skill-agent、skill-openwrt的开发。

---

*分析完成时间: 2026-02-22*
*分析人: Skills Team*
