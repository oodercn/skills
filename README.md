# Ooder Skills Repository

Ooder Agent Platform 技能仓库，包含多个可复用的技能包。

## 仓库地址

| 平台 | 地址 |
|------|------|
| **Gitee (国内)** | https://gitee.com/ooderCN/skills |
| **GitHub (国际)** | https://github.com/ooderCN/skills |

## 技能列表

| 技能ID | 名称 | 描述 | 场景 |
|--------|------|------|------|
| skill-user-auth | User Authentication Service | 用户认证服务 | auth |
| skill-org-feishu | Feishu Organization Service | 飞书组织数据集成 | auth |
| skill-org-dingding | DingTalk Organization Service | 钉钉组织数据集成 | auth |
| skill-a2ui | A2UI Skill | A2UI图转代码 | ui-generation |
| skill-trae-solo | Trae Solo Service | 实用工具服务 | utility |

## 快速开始

### 安装技能

```bash
# 从Gitee安装
ooder skill install skill-user-auth --source gitee

# 从GitHub安装
ooder skill install skill-user-auth --source github
```

### 启动技能

```bash
ooder skill start skill-user-auth
```

### 配置参数

```bash
ooder skill config skill-user-auth --set AUTH_TOKEN_EXPIRE=7200
```

## 目录结构

```
skills/
├── skill-index.yaml          # 技能索引
├── skills/                   # 技能目录
│   ├── skill-user-auth/     # 用户认证技能
│   ├── skill-org-feishu/    # 飞书组织技能
│   ├── skill-org-dingding/  # 钉钉组织技能
│   ├── skill-a2ui/          # A2UI技能
│   └── skill-trae-solo/     # Trae Solo技能
└── templates/               # 技能模板
```

## 技能开发

参考 [SKILL_DEVELOPMENT.md](docs/SKILL_DEVELOPMENT.md) 了解如何开发新技能。

## 许可证

Apache-2.0

## 作者

Ooder Team
