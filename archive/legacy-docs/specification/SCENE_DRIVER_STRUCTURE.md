# 场景驱动目录结构说明

## 概述

本文档描述 ooder-skills 仓库的场景驱动目录结构，遵循方案C（混合方案）的设计。

## 仓库结构

### 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                     仓库结构设计                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ooderCN/scene-engine (SEC 核心 + 内置驱动)                      │
│  ├── scene-engine/          # SEC 核心                          │
│  ├── drivers/               # 内置场景驱动                       │
│  │   ├── org/               # ORG 驱动（内置）                   │
│  │   ├── vfs/               # VFS 驱动（内置）                   │
│  │   └── msg/               # MSG 驱动（内置）                   │
│  └── scene-gateway/                                             │
│                                                                 │
│  ooderCN/ooder-skills (扩展实现)                                 │
│  ├── skills/                                                    │
│  │   ├── org/                                                   │
│  │   │   ├── skill-org-dingtalk/  # 钉钉实现                    │
│  │   │   ├── skill-org-feishu/    # 飞书实现                    │
│  │   │   └── skill-org-wecom/     # 企业微信实现                 │
│  │   ├── vfs/                                                   │
│  │   │   ├── skill-vfs-local/     # 本地存储实现                 │
│  │   │   ├── skill-vfs-minio/     # MinIO 实现                  │
│  │   │   ├── skill-vfs-oss/       # OSS 实现                    │
│  │   │   └── skill-vfs-s3/        # S3 实现                     │
│  │   ├── msg/                                                   │
│  │   │   └── skill-mqtt/          # MQTT 实现                   │
│  │   ├── sys/                                                   │
│  │   │   ├── skill-network/       # 网络管理                    │
│  │   │   ├── skill-agent/         # 代理管理                    │
│  │   │   ├── skill-security/      # 安全管理                    │
│  │   │   ├── skill-health/        # 健康检查                    │
│  │   │   ├── skill-protocol/      # 协议管理                    │
│  │   │   ├── skill-openwrt/       # OpenWrt管理                 │
│  │   │   └── skill-hosting/       # 托管服务                    │
│  │   ├── ui/                                                    │
│  │   │   └── skill-a2ui/          # A2UI实现                    │
│  │   └── util/                                                  │
│  │       ├── skill-trae-solo/     # Trae Solo工具               │
│  │       └── skill-share/         # 技能分享                    │
│  └── skill-index.yaml        # 技能索引                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 场景驱动定义

### 内置驱动 (scene-engine仓库)

| 驱动ID | 名称 | 能力 | 位置 |
|--------|------|------|------|
| org | Organization Driver | user-auth, user-manage, org-manage, role-manage | scene-engine |
| vfs | Virtual File System Driver | file-operations, folder-operations, stream-operations | scene-engine |
| msg | Messaging Driver | message-operations, queue-operations, subscription-operations | scene-engine |

### 扩展驱动 (ooder-skills仓库)

| 驱动ID | 名称 | 能力 | 位置 |
|--------|------|------|------|
| sys | System Driver | network-management, security-management, health-monitoring | ooder-skills |

## 技能分类映射

### ORG 场景

| 技能ID | 名称 | 实现能力 |
|--------|------|----------|
| skill-org-dingtalk | 钉钉组织服务 | user-auth, org-manage |
| skill-org-feishu | 飞书组织服务 | user-auth, org-manage |
| skill-org-wecom | 企业微信组织服务 | user-auth, org-manage |
| skill-org-ldap | LDAP组织服务 | user-auth, org-manage |
| skill-user-auth | 用户认证服务 | user-auth |

### VFS 场景

| 技能ID | 名称 | 实现能力 |
|--------|------|----------|
| skill-vfs-local | 本地存储服务 | file-operations, folder-operations |
| skill-vfs-database | 数据库存储服务 | file-operations |
| skill-vfs-minio | MinIO存储服务 | file-operations, stream-operations |
| skill-vfs-oss | 阿里云OSS存储服务 | file-operations, stream-operations |
| skill-vfs-s3 | AWS S3存储服务 | file-operations, stream-operations |

### MSG 场景

| 技能ID | 名称 | 实现能力 |
|--------|------|----------|
| skill-mqtt | MQTT服务 | message-operations, subscription-operations |

### SYS 场景

| 技能ID | 名称 | 实现能力 |
|--------|------|----------|
| skill-network | 网络管理技能 | network-management |
| skill-agent | 代理管理技能 | network-management |
| skill-security | 安全管理技能 | security-management |
| skill-health | 健康检查技能 | health-monitoring |
| skill-protocol | 协议管理技能 | protocol-handling |
| skill-openwrt | OpenWrt管理技能 | network-management |
| skill-hosting | 托管服务技能 | health-monitoring |

## 接口定义

### ORG 场景能力

```yaml
capabilities:
  user-auth:
    methods: [login, logout, validateToken, refreshToken]
  user-manage:
    methods: [getUser, registerUser, updateUser, deleteUser, listUsers]
  org-manage:
    methods: [getOrgTree, getOrg, getOrgUsers]
  role-manage:
    methods: [getUserRoles]
```

### VFS 场景能力

```yaml
capabilities:
  file-operations:
    methods: [getFileInfo, createFile, deleteFile, copyFile, moveFile, renameFile]
  folder-operations:
    methods: [getFolder, createFolder, deleteFolder, listFolders]
  stream-operations:
    methods: [downloadFile, uploadFile]
  version-operations:
    methods: [getFileVersions, createVersion]
  search-operations:
    methods: [searchFiles]
```

### MSG 场景能力

```yaml
capabilities:
  message-operations:
    methods: [sendMessage, receiveMessage, acknowledgeMessage]
  queue-operations:
    methods: [createQueue, deleteQueue, listQueues]
  subscription-operations:
    methods: [subscribe, unsubscribe, listSubscriptions]
```

## 职责划分

| 责任方 | 职责 |
|--------|------|
| **SEC 团队** | 场景驱动接口定义、内置驱动实现、验证框架 |
| **Skills 团队** | 扩展实现、第三方集成、具体业务逻辑 |

## 开发规范

### 技能目录结构

```
skill-{scene}-{provider}/
├── skill-manifest.yaml      # 技能清单
├── pom.xml                  # Maven配置
├── README.md                # 说明文档
└── src/
    └── main/
        ├── java/
        │   └── net/ooder/skill/{scene}/{provider}/
        │       ├── {Scene}SkillApplication.java
        │       ├── provider/
        │       │   └── {Scene}ProviderImpl.java
        │       ├── controller/
        │       ├── service/
        │       └── model/
        └── resources/
            ├── application.yml
            └── META-INF/services/
                └── net.ooder.scene.provider.{Scene}Provider
```

### Provider实现要求

1. 实现 `BaseProvider` 接口
2. 实现场景特定的 `Provider` 接口
3. 配置 ServiceLoader
4. 添加 scene-engine 依赖

---

**文档版本**: 1.0.0  
**创建日期**: 2026-02-21  
**作者**: Skills Team
