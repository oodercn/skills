# Ooder Skills Repository

<div align="center">

![Ooder Skills](https://img.shields.io/badge/Ooder-Skills-blue?style=for-the-badge)
![Version](https://img.shields.io/badge/version-0.7.1-green?style=flat-square)
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

| 技能ID | 名称 | 描述 | 场景 | 版本 |
|--------|------|------|------|------|
| skill-user-auth | User Authentication Service | 用户认证服务，支持多种认证方式 | auth | 0.7.1 |
| skill-org-feishu | Feishu Organization Service | 飞书组织数据集成服务 | auth | 0.7.1 |
| skill-org-dingding | DingTalk Organization Service | 钉钉组织数据集成服务 | auth | 0.7.1 |
| skill-a2ui | A2UI Skill | A2UI图转代码技能 | ui-generation | 0.7.1 |
| skill-trae-solo | Trae Solo Service | 实用工具服务 | utility | 0.7.1 |

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
│   ├── skill-user-auth/      # 用户认证技能
│   │   ├── skill-manifest.yaml
│   │   ├── skill.yaml
│   │   ├── README.md
│   │   └── src/
│   ├── skill-org-feishu/     # 飞书组织技能
│   ├── skill-org-dingding/   # 钉钉组织技能
│   ├── skill-a2ui/           # A2UI技能
│   └── skill-trae-solo/      # Trae Solo技能
├── templates/                 # 技能模板
│   ├── skill-manifest.yaml
│   ├── skill.yaml
│   └── README.md
├── scripts/                   # 工具脚本
│   └── skill-upload.py
└── docs/                      # 文档
    ├── SKILL_DEVELOPMENT.md
    └── SCENE_DESIGN.md
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

| Skill ID | Name | Description | Scene | Version |
|----------|------|-------------|-------|---------|
| skill-user-auth | User Authentication Service | User authentication with multiple methods | auth | 0.7.1 |
| skill-org-feishu | Feishu Organization Service | Feishu organization data integration | auth | 0.7.1 |
| skill-org-dingding | DingTalk Organization Service | DingTalk organization data integration | auth | 0.7.1 |
| skill-a2ui | A2UI Skill | Design to code conversion | ui-generation | 0.7.1 |
| skill-trae-solo | Trae Solo Service | Utility service | utility | 0.7.1 |

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
