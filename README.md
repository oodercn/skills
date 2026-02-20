# Ooder Skills Repository

<div align="center">

![Ooder Skills](https://img.shields.io/badge/Ooder-Skills-blue?style=for-the-badge)
![Version](https://img.shields.io/badge/version-0.7.3-green?style=flat-square)
![License](https://img.shields.io/badge/license-Apache--2.0-orange?style=flat-square)
![Java](https://img.shields.io/badge/Java-8+-red?style=flat-square)

**Ooder Agent Platform 技能仓库**

[English](#english) | [中文](#中文)

</div>

---

## 中文

### 概述

Ooder Skills 是 Ooder Agent Platform 的官方技能仓库，提供多个可复用的技能包，支持P2P模式和组织模式下的技能发现与分发。

### v0.7.3 新特性

本版本是 Ooder Agent 协议的重要升级版本，主要实现了场景管理的全面增强：

| 特性 | 说明 |
|------|------|
| **DiscoveryProtocol** | 节点发现协议，支持局域网和广域网节点自动发现 |
| **LoginProtocol** | 本地认证协议，支持离线认证和会话管理 |
| **CollaborationProtocol** | 场景组协作协议，支持任务分配和状态同步 |
| **OfflineService** | 离线服务，支持网络断开时的场景运行和数据同步 |
| **SkillShareService** | 技能分享服务，支持跨域技能发现和调用 |
| **EventBus** | 事件总线，统一事件管理和模块解耦 |

#### 离线支持

v0.7.3 新增离线模式支持，允许在网络断开时继续运行：

- **离线发现**：使用本地缓存的技能注册表
- **离线安装**：从本地缓存安装技能包
- **自动同步**：网络恢复后自动同步数据
- **事件通知**：发布离线模式切换事件

#### 事件驱动生命周期

技能生命周期事件：

| 事件类型 | 描述 |
|----------|------|
| `SkillDownloadStartedEvent` | 技能包下载开始 |
| `SkillDownloadCompletedEvent` | 技能包下载完成 |
| `SkillInstalledEvent` | 技能安装完成 |
| `SkillUpdatedEvent` | 技能更新完成 |
| `SkillUninstalledEvent` | 技能卸载完成 |
| `SkillCacheHitEvent` | 离线缓存命中 |
| `SkillCacheMissEvent` | 离线缓存未命中 |

### 总体设计

#### 技能架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Ooder Agent Platform                      │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │  技能市场    │  │  技能中心    │  │  场景管理    │         │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘         │
│         │                │                │                 │
│         └────────────────┼────────────────┘                 │
│                          ▼                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              SkillPackageManager (SDK)                 │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │  │
│  │  │   发现器    │  │   安装器    │  │  生命周期    │   │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘   │  │
│  └───────────────────────────────────────────────────────┘  │
│                          │                                   │
│         ┌────────────────┼────────────────┐                 │
│         ▼                ▼                ▼                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ UDP Broadcast│  │SkillCenter │  │     DHT     │         │
│  │  本地网络    │  │  技能中心   │  │  (Kademlia) │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│         │                │                │                 │
│         └────────────────┼────────────────┘                 │
│                          ▼                                   │
│  ┌─────────────┐  ┌─────────────┐                           │
│  │ mDNS/DNS-SD │  │   本地缓存   │                           │
│  │  服务发现   │  │  (离线模式) │                           │
│  └─────────────┘  └─────────────┘                           │
└─────────────────────────────────────────────────────────────┘
```

#### 技能生命周期

```
  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
  │  发现   │───▶│  安装   │───▶│  运行   │───▶│  停止   │
  └─────────┘    └─────────┘    └─────────┘    └─────────┘
       │              │              │              │
       │              │              │              │
       ▼              ▼              ▼              ▼
  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
  │  配置   │    │  卸载   │    │  暂停   │    │  卸载   │
  └─────────┘    └─────────┘    └─────────┘    └─────────┘
```

#### 离线安装流程 (v0.7.3)

```
安装请求
    │
    ▼
检查网络
    │
    ├── 在线 ────────────────────────────────┐
    │                                         │
    │   从技能中心下载                         │
    │   验证并安装                             │
    │   缓存技能包                             │
    │                                         │
    └── 离线 ────────────────────────────────┤
                                              │
        检查缓存                              │
        │                                     │
        ├── 已缓存 ────▶ 从缓存安装           │
        │                                     │
        └── 未缓存                            │
                │                             │
                ▼                             │
            加入队列等待                       │
                                              │
                ▼                             │
            网络重连 ◀────────────────────────┘
                │
                ▼
            自动同步并安装
```

#### 发现方法 (DiscoveryMethod)

| 方法 | 范围 | 延迟 | 适用场景 |
|------|------|------|---------|
| `UDP_BROADCAST` | 本地网络 | 低 | LAN发现，局域网技能发现 |
| `DHT (Kademlia)` | 全球 | 中 | P2P发现，广域网技能发现 |
| `SKILL_CENTER` | 全球 | 低 | 中心化目录，企业技能市场 |
| `mDNS/DNS-SD` | 本地网络 | 低 | 服务发现，本地服务注册 |
| `LOCAL_FS` | 本地 | 极低 | 本地文件系统，开发调试 |
| `AUTO` | 自动 | - | 自动推断，根据环境选择 |

### 仓库地址

| 平台 | 地址 | 适用场景 |
|------|------|---------|
| **Gitee (国内)** | https://gitee.com/ooderCN/skills | 国内用户优先 |
| **GitHub (国际)** | https://github.com/ooderCN/skills | 国际用户 |

### 技能列表

#### 组织服务技能

| 技能ID | 名称 | 描述 | 版本 |
|--------|------|------|------|
| skill-org-dingding | DingTalk Organization Service | 钉钉组织数据集成服务 | 0.7.3 |
| skill-org-feishu | Feishu Organization Service | 飞书组织数据集成服务 | 0.7.3 |
| skill-org-wecom | WeCom Organization Service | 企业微信组织数据集成服务 | 0.7.3 |
| skill-org-ldap | LDAP Organization Service | LDAP组织服务 | 0.7.3 |
| skill-user-auth | User Authentication Service | 用户认证服务 | 0.7.3 |

#### UI生成技能

| 技能ID | 名称 | 描述 | 版本 |
|--------|------|------|------|
| skill-a2ui | A2UI Skill | A2UI图转代码技能 | 0.7.3 |

#### 通讯服务技能

| 技能ID | 名称 | 描述 | 版本 |
|--------|------|------|------|
| skill-mqtt | MQTT Service Skill | MQTT服务技能 | 0.7.3 |

#### 工具技能

| 技能ID | 名称 | 描述 | 版本 |
|--------|------|------|------|
| skill-trae-solo | Trae Solo Service | 实用工具服务 | 0.7.3 |

#### 存储服务技能 (VFS)

| 技能ID | 名称 | 描述 | 版本 |
|--------|------|------|------|
| skill-vfs-database | Database VFS Service | 数据库存储服务 | 0.7.3 |
| skill-vfs-local | Local VFS Service | 本地文件系统存储服务 | 0.7.3 |
| skill-vfs-minio | MinIO VFS Service | MinIO存储服务 | 0.7.3 |
| skill-vfs-oss | Aliyun OSS VFS Service | 阿里云OSS存储服务 | 0.7.3 |
| skill-vfs-s3 | AWS S3 VFS Service | AWS S3存储服务 | 0.7.3 |

### 场景配置说明

#### 场景分类

| 分类 | 场景ID | 描述 | 最大成员数 |
|------|--------|------|-----------|
| **通讯类** | p2p | 点对点通讯 | 2 |
| | group | 群组通讯 | 500 |
| | broadcast | 广播通讯 | 10000 |
| **业务类** | hr | 人力资源 | 1000 |
| | crm | 客户管理 | 500 |
| | finance | 财务管理 | 200 |
| | approval | 审批流程 | 1000 |
| | project | 项目管理 | 500 |
| | knowledge | 知识管理 | 1000 |
| **IoT类** | device | 设备管理 | 10000 |
| | collection | 数据采集 | 5000 |
| | edge | 边缘计算 | 1000 |
| **协作类** | meeting | 会议协作 | 100 |
| | document | 文档协作 | 500 |
| | task | 任务协作 | 500 |
| | ui-generation | UI生成 | 10 |
| **系统类** | auth | 认证授权 | 100 |
| | sys | 系统管理 | 50 |
| | monitor | 监控运维 | 100 |
| | security | 安全审计 | 50 |

#### 场景配置示例

```yaml
# scene-index.yaml
apiVersion: ooder.io/v1
kind: SceneIndex

metadata:
  name: ooder-scenes
  version: "1.0.0"

spec:
  scenes:
    - sceneId: auth
      name: Authentication
      description: 认证场景，支持用户认证和组织数据访问
      version: "1.0.0"
      category: SYSTEM
      requiredCapabilities:
        - user-auth
        - org-data-read
      maxMembers: 100
      
    - sceneId: ui-generation
      name: UI Generation
      description: UI生成场景，支持图转代码、组件生成等能力
      version: "1.0.0"
      category: COLLABORATION
      requiredCapabilities:
        - generate-ui
        - preview-ui
      maxMembers: 10
```

### 安装指南

#### 方式一：从远程仓库安装（推荐）

**从 GitHub 安装**

```bash
# 使用 ooder CLI
ooder skill install skill-org-feishu --source github --version 0.7.3

# 或使用 API
curl -X POST http://localhost:8081/api/skillcenter/installed/install \
  -H "Content-Type: application/json" \
  -d '{
    "skillId": "skill-org-feishu",
    "version": "0.7.3",
    "source": "github",
    "discoveryMethod": "GIT_REPOSITORY"
  }'
```

**从 Gitee 安装（国内推荐）**

```bash
# 使用 ooder CLI
ooder skill install skill-org-feishu --source gitee --version 0.7.3

# 或使用 API
curl -X POST http://localhost:8081/api/skillcenter/installed/install \
  -H "Content-Type: application/json" \
  -d '{
    "skillId": "skill-org-feishu",
    "version": "0.7.3",
    "source": "gitee",
    "discoveryMethod": "GIT_REPOSITORY"
  }'
```

#### 方式二：下载到本地目录安装

**步骤 1: 下载技能包**

```bash
# 从 GitHub 下载
wget https://github.com/ooderCN/skills/releases/download/v0.7.3/skill-org-feishu-0.7.3.jar

# 或从 Gitee 下载
wget https://gitee.com/ooderCN/skills/releases/download/v0.7.3/skill-org-feishu-0.7.3.jar
```

**步骤 2: 安装到本地**

```bash
# 使用 ooder CLI
ooder skill install --local ./skill-org-feishu-0.7.3.jar

# 或使用 API
curl -X POST http://localhost:8081/api/skillcenter/installed/install \
  -H "Content-Type: multipart/form-data" \
  -F "file=@skill-org-feishu-0.7.3.jar"
```

**步骤 3: 配置技能**

```bash
# 查看配置
ooder skill config skill-org-feishu --get

# 设置配置
ooder skill config skill-org-feishu --set FEISHU_APP_ID=your-app-id
ooder skill config skill-org-feishu --set FEISHU_APP_SECRET=your-app-secret
```

#### 方式三：在线雷达检索安装

通过 Nexus 控制台的技能市场进行在线安装：

1. 访问 Nexus 控制台: http://localhost:8081/console
2. 进入「技能中心」→「技能市场」
3. 搜索或浏览技能
4. 点击「安装」按钮
5. 选择版本和配置
6. 确认安装

**API 调用示例**

```bash
# 搜索技能
curl -X POST http://localhost:8081/api/skillcenter/market/search \
  -H "Content-Type: application/json" \
  -d '{"keyword": "feishu"}'

# 获取技能详情
curl -X POST http://localhost:8081/api/skillcenter/market/detail \
  -H "Content-Type: application/json" \
  -d '{"skillId": "skill-org-feishu"}'

# 从市场安装
curl -X POST http://localhost:8081/api/skillcenter/installed/install \
  -H "Content-Type: application/json" \
  -d '{
    "skillId": "skill-org-feishu",
    "version": "0.7.3",
    "source": "skill_center"
  }'
```

#### 方式四：发布到 WebDAV 服务

**步骤 1: 配置 WebDAV 服务**

```yaml
# application.yml
skill:
  discovery:
    webdav:
      enabled: true
      url: http://your-webdav-server/skills
      username: ${WEBDAV_USERNAME}
      password: ${WEBDAV_PASSWORD}
```

**步骤 2: 上传技能到 WebDAV**

```bash
# 使用 curl 上传
curl -T skill-org-feishu-0.7.3.jar \
  -u username:password \
  http://your-webdav-server/skills/skill-org-feishu-0.7.3.jar
```

**步骤 3: 从 WebDAV 安装**

```bash
# 使用 API
curl -X POST http://localhost:8081/api/skillcenter/installed/install \
  -H "Content-Type: application/json" \
  -d '{
    "skillId": "skill-org-feishu",
    "version": "0.7.3",
    "downloadUrl": "http://your-webdav-server/skills/skill-org-feishu-0.7.3.jar"
  }'
```

### 技能管理

#### 启动技能

```bash
# 使用 CLI
ooder skill start skill-org-feishu

# 使用 API
curl -X POST http://localhost:8081/api/skillcenter/installed/run \
  -H "Content-Type: application/json" \
  -d '{"skillId": "skill-org-feishu"}'
```

#### 停止技能

```bash
# 使用 CLI
ooder skill stop skill-org-feishu

# 使用 API
curl -X POST http://localhost:8081/api/skillcenter/installed/stop \
  -H "Content-Type: application/json" \
  -d '{"skillId": "skill-org-feishu"}'
```

#### 卸载技能

```bash
# 使用 CLI
ooder skill uninstall skill-org-feishu

# 使用 API
curl -X POST http://localhost:8081/api/skillcenter/installed/uninstall \
  -H "Content-Type: application/json" \
  -d '{"skillId": "skill-org-feishu"}'
```

### 目录结构

```
skills/
├── README.md                   # 仓库说明
├── skill-index.yaml           # 技能索引文件
├── skills/                    # 技能目录
│   ├── skill-org-dingding/    # 钉钉组织技能
│   │   ├── skill-manifest.yaml
│   │   ├── skill.yaml
│   │   ├── README.md
│   │   ├── pom.xml
│   │   └── src/
│   ├── skill-org-feishu/      # 飞书组织技能
│   ├── skill-org-wecom/       # 企业微信组织技能
│   ├── skill-org-ldap/        # LDAP组织技能
│   ├── skill-user-auth/       # 用户认证技能
│   ├── skill-a2ui/            # A2UI技能
│   ├── skill-mqtt/            # MQTT服务技能
│   ├── skill-trae-solo/       # Trae Solo技能
│   ├── skill-vfs-database/    # 数据库存储技能
│   ├── skill-vfs-local/       # 本地存储技能
│   ├── skill-vfs-minio/       # MinIO存储技能
│   ├── skill-vfs-oss/         # 阿里云OSS存储技能
│   └── skill-vfs-s3/          # AWS S3存储技能
├── templates/                 # 技能模板
└── docs/                      # 文档
```

### 技能发现配置

#### P2P模式（公开访问）

```properties
# 无需Token，直接访问公开仓库
skill.discovery.github.enabled=true
skill.discovery.github.default-owner=ooderCN
skill.discovery.github.skills-path=skills

skill.discovery.gitee.enabled=true
skill.discovery.gitee.default-owner=ooderCN
skill.discovery.gitee.skills-path=skills
```

#### 组织模式（私有仓库）

```properties
# 配置Token访问私有仓库
skill.discovery.github.token=${GITHUB_TOKEN}
skill.discovery.gitee.token=${GITEE_TOKEN}
```

### 技能开发

参考 [SKILL_DEVELOPMENT.md](docs/SKILL_DEVELOPMENT.md) 了解如何开发新技能。

### 场景设计

参考 [SCENE_DESIGN.md](docs/SCENE_DESIGN.md) 了解场景设计规范。

---

## English

### Overview

Ooder Skills is the official skill repository for Ooder Agent Platform, providing multiple reusable skill packages with support for P2P and organizational skill discovery and distribution.

### v0.7.3 New Features

This version is a major upgrade to the Ooder Agent protocol, implementing comprehensive scene management enhancements:

| Feature | Description |
|---------|-------------|
| **DiscoveryProtocol** | Node discovery protocol for LAN and WAN node auto-discovery |
| **LoginProtocol** | Local authentication protocol for offline auth and session management |
| **CollaborationProtocol** | Scene group collaboration protocol for task distribution and state sync |
| **OfflineService** | Offline service for scene execution and data sync when disconnected |
| **SkillShareService** | Skill sharing service for cross-domain skill discovery and invocation |
| **EventBus** | Event bus for unified event management and module decoupling |

#### Offline Support

v0.7.3 adds offline mode support, allowing continued operation when disconnected:

- **Offline Discovery**: Use cached skill registry
- **Offline Install**: Install skill packages from local cache
- **Auto Sync**: Automatic data sync when network reconnects
- **Event Notification**: Publish offline mode switch events

#### Event-Driven Lifecycle

Skill lifecycle events:

| Event Type | Description |
|------------|-------------|
| `SkillDownloadStartedEvent` | Package download started |
| `SkillDownloadCompletedEvent` | Package download completed |
| `SkillInstalledEvent` | Installation completed |
| `SkillUpdatedEvent` | Update completed |
| `SkillUninstalledEvent` | Uninstall completed |
| `SkillCacheHitEvent` | Offline cache hit |
| `SkillCacheMissEvent` | Offline cache miss |

### Architecture Design

#### Skill Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Ooder Agent Platform                      │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ Skill Market│  │Skill Center │  │Scene Manager│         │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘         │
│         │                │                │                 │
│         └────────────────┼────────────────┘                 │
│                          ▼                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              SkillPackageManager (SDK)                 │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │  │
│  │  │  Discoverer │  │  Installer  │  │  Lifecycle  │   │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘   │  │
│  └───────────────────────────────────────────────────────┘  │
│                          │                                   │
│         ┌────────────────┼────────────────┐                 │
│         ▼                ▼                ▼                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ UDP Broadcast│  │SkillCenter │  │     DHT     │         │
│  │ Local Network│  │Skill Center│  │  (Kademlia) │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│         │                │                │                 │
│         └────────────────┼────────────────┘                 │
│                          ▼                                   │
│  ┌─────────────┐  ┌─────────────┐                           │
│  │ mDNS/DNS-SD │  │ Local Cache │                           │
│  │  Discovery  │  │(Offline Mode)│                           │
│  └─────────────┘  └─────────────┘                           │
└─────────────────────────────────────────────────────────────┘
```

#### Skill Lifecycle

```
  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
  │ Discover│───▶│ Install │───▶│  Run    │───▶│  Stop   │
  └─────────┘    └─────────┘    └─────────┘    └─────────┘
       │              │              │              │
       │              │              │              │
       ▼              ▼              ▼              ▼
  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
  │ Config  │    │Uninstall│    │  Pause  │    │Uninstall│
  └─────────┘    └─────────┘    └─────────┘    └─────────┘
```

#### Offline Install Flow (v0.7.3)

```
Install Request
    │
    ▼
Check Network
    │
    ├── Online ────────────────────────────────┐
    │                                           │
    │   Download from SkillCenter               │
    │   Verify and Install                      │
    │   Cache Package                           │
    │                                           │
    └── Offline ────────────────────────────────┤
                                                │
        Check Cache                             │
        │                                       │
        ├── Cached ────▶ Install from Cache    │
        │                                       │
        └── Not Cached                          │
                │                               │
                ▼                               │
            Queue for Later                     │
                                                │
                ▼                               │
            Network Reconnected ◀───────────────┘
                │
                ▼
            Auto Sync and Install
```

#### Discovery Methods

| Method | Scope | Latency | Use Case |
|--------|-------|---------|----------|
| `UDP_BROADCAST` | Local Network | Low | LAN discovery |
| `DHT (Kademlia)` | Global | Medium | P2P discovery |
| `SKILL_CENTER` | Global | Low | Centralized catalog |
| `mDNS/DNS-SD` | Local Network | Low | Service discovery |
| `LOCAL_FS` | Local | Very Low | Local filesystem, development |
| `AUTO` | Auto | - | Auto inference by environment |

### Repository URLs

| Platform | URL | Region |
|----------|-----|--------|
| **Gitee** | https://gitee.com/ooderCN/skills | China |
| **GitHub** | https://github.com/ooderCN/skills | Global |

### Skills List

#### Organization Skills

| Skill ID | Name | Description | Version |
|----------|------|-------------|---------|
| skill-org-dingding | DingTalk Organization Service | DingTalk organization data integration | 0.7.3 |
| skill-org-feishu | Feishu Organization Service | Feishu organization data integration | 0.7.3 |
| skill-org-wecom | WeCom Organization Service | WeCom organization data integration | 0.7.3 |
| skill-org-ldap | LDAP Organization Service | LDAP organization service | 0.7.3 |
| skill-user-auth | User Authentication Service | User authentication with multiple methods | 0.7.3 |

#### UI Generation Skills

| Skill ID | Name | Description | Version |
|----------|------|-------------|---------|
| skill-a2ui | A2UI Skill | Design to code conversion | 0.7.3 |

#### Communication Skills

| Skill ID | Name | Description | Version |
|----------|------|-------------|---------|
| skill-mqtt | MQTT Service Skill | MQTT service skill | 0.7.3 |

#### Utility Skills

| Skill ID | Name | Description | Version |
|----------|------|-------------|---------|
| skill-trae-solo | Trae Solo Service | Utility service | 0.7.3 |

#### Storage Skills (VFS)

| Skill ID | Name | Description | Version |
|----------|------|-------------|---------|
| skill-vfs-database | Database VFS Service | Database storage service | 0.7.3 |
| skill-vfs-local | Local VFS Service | Local file system storage service | 0.7.3 |
| skill-vfs-minio | MinIO VFS Service | MinIO storage service | 0.7.3 |
| skill-vfs-oss | Aliyun OSS VFS Service | Aliyun OSS storage service | 0.7.3 |
| skill-vfs-s3 | AWS S3 VFS Service | AWS S3 storage service | 0.7.3 |

### Installation Guide

#### Method 1: Install from Remote Repository (Recommended)

```bash
# From GitHub
ooder skill install skill-org-feishu --source github --version 0.7.3

# From Gitee (Recommended for China)
ooder skill install skill-org-feishu --source gitee --version 0.7.3
```

#### Method 2: Download and Install Locally

```bash
# Download
wget https://github.com/ooderCN/skills/releases/download/v0.7.3/skill-org-feishu-0.7.3.jar

# Install
ooder skill install --local ./skill-org-feishu-0.7.3.jar
```

#### Method 3: Install via Nexus Console

1. Access Nexus Console: http://localhost:8081/console
2. Navigate to「Skill Center」→「Skill Market」
3. Search or browse skills
4. Click「Install」button
5. Select version and configure
6. Confirm installation

#### Method 4: Publish to WebDAV

```bash
# Upload to WebDAV
curl -T skill-org-feishu-0.7.3.jar \
  -u username:password \
  http://your-webdav-server/skills/skill-org-feishu-0.7.3.jar

# Install from WebDAV
curl -X POST http://localhost:8081/api/skillcenter/installed/install \
  -H "Content-Type: application/json" \
  -d '{
    "skillId": "skill-org-feishu",
    "version": "0.7.3",
    "downloadUrl": "http://your-webdav-server/skills/skill-org-feishu-0.7.3.jar"
  }'
```

### Skill Management

```bash
# Start skill
ooder skill start skill-org-feishu

# Stop skill
ooder skill stop skill-org-feishu

# Uninstall skill
ooder skill uninstall skill-org-feishu

# Configure skill
ooder skill config skill-org-feishu --set FEISHU_APP_ID=your-app-id
```

### License

Apache-2.0

### Author

Ooder Team

---

<div align="center">

**Made with ❤️ by Ooder Team**

</div>