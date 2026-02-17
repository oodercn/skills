# GitHub Skills 仓库结构方案

## 一、仓库地址

| 平台 | 仓库地址 | 用途 |
|------|---------|------|
| GitHub | https://github.com/ooderCN/skills | 国际主仓库 |
| Gitee | https://gitee.com/ooderCN/skills | 国内镜像 |

## 二、仓库目录结构

```
skills/
├── README.md                           # 仓库说明
├── skill-index.yaml                    # 技能索引文件
├── skills/                             # 技能目录
│   ├── skill-user-auth/               # 用户认证技能
│   │   ├── skill-manifest.yaml        # 技能清单
│   │   ├── skill.yaml                 # 技能定义
│   │   ├── README.md                  # 技能说明
│   │   └── releases/                  # 发布版本
│   │       └── v0.7.1/
│   │           └── skill-user-auth-0.7.1.jar
│   ├── skill-org-feishu/              # 飞书组织技能
│   │   ├── skill-manifest.yaml
│   │   ├── skill.yaml
│   │   ├── README.md
│   │   └── releases/
│   ├── skill-org-dingding/            # 钉钉组织技能
│   │   ├── skill-manifest.yaml
│   │   ├── skill.yaml
│   │   ├── README.md
│   │   └── releases/
│   ├── skill-a2ui/                    # A2UI图转代码技能
│   │   ├── skill-manifest.yaml
│   │   ├── skill.yaml
│   │   ├── README.md
│   │   └── releases/
│   └── skill-trae-solo/               # Trae Solo技能
│       ├── skill-manifest.yaml
│       ├── skill.yaml
│       ├── README.md
│       └── releases/
└── templates/                          # 模板目录
    └── skill-template/                # 技能模板
        ├── skill-manifest.yaml
        ├── skill.yaml
        └── README.md
```

## 三、技能清单规范 (skill-manifest.yaml)

```yaml
apiVersion: ooder.io/v1
kind: SkillPackage
metadata:
  name: skill-user-auth
  version: 0.7.1
  description: 用户认证服务，支持多种认证方式
  author: ooder Team
  license: Apache-2.0
  homepage: https://github.com/ooderCN/skills/tree/main/skills/skill-user-auth
  repository: https://github.com/ooderCN/skills.git
  keywords:
    - authentication
    - user
    - session
    - security

spec:
  type: infrastructure-skill
  capabilities:
    - user-auth
    - session-manage
    - permission-check
  scenes:
    - auth

  parameters:
    - name: AUTH_SECRET_KEY
      type: string
      required: true
      description: Secret key for JWT signing
      secret: true
    - name: SESSION_TIMEOUT
      type: number
      default: 3600
      description: Session timeout in seconds

  execution:
    timeout: 30000
    memoryLimit: 256M
    cpuLimit: 1

  distribution:
    format: jar
    entrypoint: net.ooder.skill.auth.AuthSkillApplication
    assets:
      - name: skill-user-auth-0.7.1.jar
        platform: all
        url: https://github.com/ooderCN/skills/releases/download/v0.7.1/skill-user-auth-0.7.1.jar
        checksum: sha256:abc123...

  compatibility:
    sdkVersion: ">=0.7.0"
    javaVersion: ">=8"
```

## 四、技能索引文件 (skill-index.yaml)

```yaml
apiVersion: ooder.io/v1
kind: SkillIndex
metadata:
  name: ooder-skills
  version: "0.7.1"
  description: Ooder Skills Repository
  author: ooder Team
  homepage: https://github.com/ooderCN/skills
  updatedAt: "2026-02-18T00:00:00Z"

spec:
  skills:
    - skillId: skill-user-auth
      name: User Authentication Service
      version: "0.7.1"
      description: 用户认证服务
      sceneId: auth
      path: skills/skill-user-auth
      downloadUrl: https://github.com/ooderCN/skills/releases/download/v0.7.1/skill-user-auth-0.7.1.jar
      checksum: ""
      
    - skillId: skill-org-feishu
      name: Feishu Organization Service
      version: "0.7.1"
      description: 飞书组织数据集成服务
      sceneId: auth
      path: skills/skill-org-feishu
      downloadUrl: https://github.com/ooderCN/skills/releases/download/v0.7.1/skill-org-feishu-0.7.1.jar
      checksum: ""
      
    - skillId: skill-org-dingding
      name: DingTalk Organization Service
      version: "0.7.1"
      description: 钉钉组织数据集成服务
      sceneId: auth
      path: skills/skill-org-dingding
      downloadUrl: https://github.com/ooderCN/skills/releases/download/v0.7.1/skill-org-dingding-0.7.1.jar
      checksum: ""
      
    - skillId: skill-a2ui
      name: A2UI Skill
      version: "0.7.1"
      description: A2UI图转代码技能
      sceneId: ui-generation
      path: skills/skill-a2ui
      downloadUrl: https://github.com/ooderCN/skills/releases/download/v0.7.1/skill-a2ui-0.7.1.jar
      checksum: ""
      
    - skillId: skill-trae-solo
      name: Trae Solo Service
      version: "0.7.1"
      description: Trae Solo实用功能服务
      sceneId: utility
      path: skills/skill-trae-solo
      downloadUrl: https://github.com/ooderCN/skills/releases/download/v0.7.1/skill-trae-solo-0.7.1.jar
      checksum: ""

  scenes:
    - sceneId: auth
      name: Authentication
      description: 认证场景
      version: "1.0.0"
      requiredCapabilities:
        - user-auth
        - org-data-read
      maxMembers: 100
      
    - sceneId: ui-generation
      name: UI Generation
      description: UI生成场景
      version: "1.0.0"
      requiredCapabilities:
        - generate-ui
        - preview-ui
      maxMembers: 10
      
    - sceneId: utility
      name: Utility
      description: 实用工具场景
      version: "1.0.0"
      requiredCapabilities:
        - execute-task
      maxMembers: 50
```

## 五、发现配置

### GitHub 配置
```properties
skill.discovery.github.enabled=true
skill.discovery.github.api-url=https://api.github.com
skill.discovery.github.token=${GITHUB_TOKEN}
skill.discovery.github.default-owner=ooderCN
skill.discovery.github.skills-path=skills
```

### Gitee 配置
```properties
skill.discovery.gitee.enabled=true
skill.discovery.gitee.api-url=https://gitee.com/api/v5
skill.discovery.gitee.token=${GITEE_TOKEN}
skill.discovery.gitee.default-owner=ooderCN
skill.discovery.gitee.skills-path=skills
```

## 六、上传流程

1. 准备技能包 (JAR文件)
2. 创建 skill-manifest.yaml
3. 推送到 GitHub 仓库
4. 创建 GitHub Release
5. 更新 skill-index.yaml
6. 同步到 Gitee 镜像
