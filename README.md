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

### 快速开始

#### 安装技能

```bash
# 从Gitee安装（国内推荐）
ooder skill install skill-user-auth --source gitee

# 从GitHub安装
ooder skill install skill-user-auth --source github
```

#### 启动技能

```bash
ooder skill start skill-user-auth
```

#### 配置参数

```bash
ooder skill config skill-user-auth --set AUTH_TOKEN_EXPIRE=7200
```

### 目录结构

```
skills/
├── README.md                   # 仓库说明
├── skill-index.yaml           # 技能索引文件
├── skills/                    # 技能目录
│   ├── skill-org-dingding/    # 钉钉组织技能
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

### Quick Start

```bash
# Install skill
ooder skill install skill-user-auth --source gitee

# Start skill
ooder skill start skill-user-auth

# Configure
ooder skill config skill-user-auth --set AUTH_TOKEN_EXPIRE=7200
```

### License

Apache-2.0

### Author

Ooder Team

---

<div align="center">

**Made with ❤️ by Ooder Team**

</div>