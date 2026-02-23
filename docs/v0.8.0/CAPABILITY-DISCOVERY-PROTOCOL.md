# 能力发现协议 v0.8.0

## 1. 概述

### 1.1 文档目的

本文档定义能力发现协议，包括：
- 发现流程设计
- 发现方式应用
- 消息协议定义
- 应用场景设计

### 1.2 适用范围

- SDK 团队：实现发现机制
- Engine 团队：实现发现服务
- 可视化团队：设计发现界面

---

## 2. 发现流程

### 2.1 新发现流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    能力发现流程 v0.8.0                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 启动雷达扫描                                                            │
│     │                                                                        │
│     ├── 同步 Scene 索引 (scene-index.yaml)                                  │
│     ├── 同步 CAP 注册表 (cap-index.yaml)                                    │
│     ├── 同步 Skill 索引 (skill-index.yaml)                                  │
│     └── 缓存到本地                                                          │
│                                                                             │
│  2. 展示选择界面                                                            │
│     │                                                                        │
│     ├── 按场景分类浏览                                                      │
│     ├── 按能力分类浏览                                                      │
│     └── 搜索功能                                                            │
│                                                                             │
│  3. 用户选择场景                                                            │
│     │                                                                        │
│     ├── 展示场景详情                                                        │
│     ├── 展示可用 Skill 列表                                                 │
│     └── 用户选择 Skill                                                      │
│                                                                             │
│  4. 自动安装场景                                                            │
│     │                                                                        │
│     ├── 下载 Skill 包                                                       │
│     ├── 验证 CAP 契约                                                       │
│     ├── 安装依赖 Skills                                                     │
│     ├── 初始化 Scene Agent                                                  │
│     └── 激活场景                                                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 已安装场景更换 Skill

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    更换 Skill 流程                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 进入已安装场景列表                                                      │
│     │                                                                        │
│     └── 展示已安装场景及当前 Skill                                          │
│                                                                             │
│  2. 选择要更换的场景                                                        │
│     │                                                                        │
│     ├── 展示场景能力列表                                                    │
│     └── 展示当前 Skill 实现的能力                                           │
│                                                                             │
│  3. 选择要更换的能力                                                        │
│     │                                                                        │
│     ├── 展示该能力的其他 Skill 实现                                         │
│     ├── 对比不同 Skill 的特性                                               │
│     │   ├── 性能对比                                                        │
│     │   ├── 离线支持                                                        │
│     │   ├── 评分/下载量                                                     │
│     │   └── 兼容性                                                          │
│     │                                                                        │
│     └── 用户选择新 Skill                                                    │
│                                                                             │
│  4. 执行更换操作                                                            │
│     │                                                                        │
│     ├── 停止当前 Skill                                                      │
│     ├── 下载新 Skill                                                        │
│     ├── 验证 CAP 契约                                                       │
│     ├── 迁移配置                                                            │
│     ├── 启动新 Skill                                                        │
│     └── 更新场景路由表                                                      │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. 发现方式应用

### 3.1 发现方式与场景映射

| 发现方式 | 个人网络 | 部门分享 | 公司管理 | 公共社区 |
|----------|----------|----------|----------|----------|
| UDP Broadcast | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐ | ⭐ |
| mDNS/DNS-SD | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐ | ⭐ |
| DHT (Kademlia) | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| SkillCenter API | ⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| GitHub/Gitee | ⭐ | ⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| Git Repository | ⭐ | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| Local FS | ⭐⭐⭐⭐⭐ | ⭐ | ⭐ | ⭐ |

### 3.2 推荐组合

| 场景 | 推荐发现方式组合 |
|------|------------------|
| 个人网络 | UDP + mDNS + Local FS |
| 部门分享 | UDP + DHT + Local FS |
| 公司管理 | SkillCenter + DHT + Git Repository |
| 公共社区 | SkillCenter + GitHub + DHT |

---

## 4. 消息协议

### 4.1 Agent 广播消息

```
# Agent 启动广播
AGENT_ANNOUNCE:{agentId};{userId};{deviceName};{capabilities};{scenes};{status};{timestamp}

示例:
AGENT_ANNOUNCE:agent-001;xiaoming;工作电脑-A;40,41,42;msg,auth;online;1700000000000
```

### 4.2 能力分享消息

```
# 能力分享广播
CAP_SHARE:{agentId};{userId};{capId};{shareScope};{permissions};{expireTime};{timestamp}

示例:
CAP_SHARE:agent-001;xiaoming;40;group:platform-team;read,execute;0;1700000000000
```

### 4.3 场景创建消息

```
# 场景创建广播
SCENE_CREATE:{agentId};{userId};{sceneId};{sceneName};{requiredCaps};{inviteList};{timestamp}

示例:
SCENE_CREATE:agent-001;xiaoming;code-review;代码审查;B0,40;hong,li,zhang;1700000000000
```

### 4.4 能力转移消息

```
# 能力转移请求
CAP_HANDOVER:{fromAgentId};{toAgentId};{capId};{reason};{timestamp}

示例:
CAP_HANDOVER:agent-001;agent-002;40;device_sleep;1700000000000
```

### 4.5 能力同步消息

```
# 能力同步请求
CAP_SYNC_REQUEST:{fromAgentId};{toAgentId};{syncType};{timestamp}

示例:
CAP_SYNC_REQUEST:agent-003;agent-002;full;1700000000000
```

---

## 5. 应用场景设计

### 5.1 Level 1: 个人网络/能力管理

#### 场景故事

| 场景 | 描述 |
|------|------|
| 早晨启动 | Agent 启动 → UDP 广播能力列表 → 发现其他设备 → 自动同步配置 |
| 离开办公室 | 检测空闲 → 广播转移请求 → 手机接管能力 → 自动切换 |
| 回家后 | 家用电脑启动 → UDP 广播 → 从手机同步 → 恢复工作状态 |

#### 推荐发现方式

- UDP Broadcast (主要)
- mDNS/DNS-SD (辅助)
- Local FS (缓存)

### 5.2 Level 2: 部门/组分享

#### 场景故事

| 场景 | 描述 |
|------|------|
| 分享能力 | 选择能力 → 发布到共享区 → UDP/DHT 广播 → 组成员发现 |
| 创建协作场景 | 定义能力需求 → 邀请成员 → 成员贡献能力 → 场景激活 |
| 组能力池 | 自动汇聚成员能力 → 权限控制 → 使用审计 |

#### 推荐发现方式

- UDP Broadcast (局域网)
- DHT (跨网段)
- Local FS (缓存)

### 5.3 Level 3: 公司体系管理

#### 场景故事

| 场景 | 描述 |
|------|------|
| 发布官方能力 | IT 开发 → 提交 SkillCenter → 审批 → DHT 广播 → 员工发现 |
| 部门能力授权 | 研发开发 → 设置权限 → 发布 → 仅部门可见 |
| 能力使用审计 | 使用记录上报 → 审计系统 → 异常告警 |

#### 推荐发现方式

- SkillCenter API (主要)
- DHT (分布式)
- Git Repository (版本控制)

---

## 6. SDK 接口

### 6.1 SceneDiscoveryService

```java
public interface SceneDiscoveryService {
    
    // 同步所有索引
    CompletableFuture<SyncResult> syncAllIndexes();
    
    // 获取场景列表
    CompletableFuture<List<SceneInfo>> listScenes(String category);
    
    // 搜索场景
    CompletableFuture<List<SceneInfo>> searchScenes(String query);
    
    // 获取场景详情
    CompletableFuture<SceneDetail> getSceneDetail(String sceneId);
    
    // 获取场景可用 Skills
    CompletableFuture<List<SkillInfo>> getAvailableSkills(String sceneId);
}
```

### 6.2 CapDiscoveryService

```java
public interface CapDiscoveryService {
    
    // 获取 CAP 列表
    CompletableFuture<List<CapInfo>> listCaps(String category);
    
    // 搜索 CAP
    CompletableFuture<List<CapInfo>> searchCaps(String query);
    
    // 获取 CAP 详情
    CompletableFuture<CapDetail> getCapDetail(String capId);
    
    // 获取 CAP 可用 Skills
    CompletableFuture<List<SkillInfo>> getAvailableSkills(String capId);
}
```

### 6.3 SkillPackageManager

```java
public interface SkillPackageManager {
    
    // 安装场景（自动安装依赖 Skills）
    CompletableFuture<InstallResult> installScene(String sceneId, SkillSelection selection);
    
    // 更换场景中的 Skill
    CompletableFuture<ReplaceResult> replaceSkill(String sceneId, String capId, String newSkillId);
    
    // 搜索 Skill
    CompletableFuture<List<SkillInfo>> searchSkills(String query);
    
    // 按 CAP 搜索 Skill
    CompletableFuture<List<SkillInfo>> searchSkillsByCap(String capId);
}
```

### 6.4 PersonalNetworkService

```java
public interface PersonalNetworkService {
    
    // 获取我的设备列表
    CompletableFuture<List<DeviceInfo>> getMyDevices();
    
    // 广播能力更新
    void broadcastCapabilityUpdate(String capId, CapabilityStatus status);
    
    // 请求能力转移
    CompletableFuture<TransferResult> requestCapabilityTransfer(String capId, String toDeviceId);
    
    // 同步配置到其他设备
    CompletableFuture<SyncResult> syncConfigToDevices(List<String> deviceIds);
}
```

### 6.5 CapabilityShareService

```java
public interface CapabilityShareService {
    
    // 分享能力
    CompletableFuture<ShareResult> shareCapability(String capId, ShareScope scope, SharePermission permission);
    
    // 取消分享
    CompletableFuture<Void> unshareCapability(String shareId);
    
    // 获取共享能力池
    CompletableFuture<List<SharedCapability>> getSharedCapabilityPool(String scope);
    
    // 使用共享能力
    CompletableFuture<UseResult> useSharedCapability(String shareId, UseRequest request);
}
```

### 6.6 CapabilityRadarService

```java
public interface CapabilityRadarService {
    
    // 启动雷达扫描
    void startRadarScan(RadarConfig config);
    
    // 停止雷达扫描
    void stopRadarScan();
    
    // 获取发现的能力
    CompletableFuture<List<DiscoveredCapability>> getDiscoveredCapabilities();
    
    // 订阅能力发现事件
    void subscribeCapabilityDiscovery(Consumer<CapabilityDiscoveryEvent> listener);
}
```

---

## 7. 状态指示规范

### 7.1 设备状态

| 状态 | 图标 | 说明 |
|------|------|------|
| 在线 | 🟢 | 设备在线且可用 |
| 忙碌 | 🟡 | 设备在线但负载高 |
| 离线 | 🔴 | 设备离线 |
| 休眠 | 🔵 | 设备休眠中，可唤醒 |
| 不可用 | ⚫ | 设备故障或禁用 |

### 7.2 能力状态

| 状态 | 图标 | 说明 |
|------|------|------|
| 可用 | ✅ | 能力可用且无限制 |
| 排队 | ⏳ | 能力繁忙，需要排队 |
| 受限 | 🔒 | 能力有使用限制 |
| 降级 | ⚠️ | 能力运行在降级模式 |
| 不可用 | ❌ | 能力不可用 |

### 7.3 分享状态

| 状态 | 图标 | 说明 |
|------|------|------|
| 公开 | 🌐 | 所有人可见可用 |
| 组内 | 👥 | 仅组内成员可见 |
| 私有 | 🔐 | 仅自己可用 |
| 限时 | ⏰ | 有时间限制的分享 |

---

## 8. 相关文档

- [架构设计总览](./ARCHITECTURE-V0.8.0.md)
- [CAP 注册表规范](./CAP-REGISTRY-SPEC.md)
- [场景引擎规范](./SCENE-ENGINE-SPEC.md)
- [团队协作任务](./TEAM-COLLABORATION-TASKS.md)
