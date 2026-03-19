# 深度揭秘 Ooder Skills：构建智能 Agent 的技能生态系统

> **作者**: Ooder Team  
> **发布日期**: 2026-02-20  
> **版本**: 0.7.3

---

## 前言

在 AI Agent 快速发展的今天，如何让 Agent 具备丰富的能力成为关键挑战。Ooder Skills 作为 Ooder Agent Platform 的官方技能仓库，提供了一套完整的技能发现、分发和管理机制。本文将深入揭秘 Ooder Skills 的设计原理、资源体系和工具链。

---

## 一、原理篇：技能驱动的 Agent 架构

### 1.1 核心设计理念

Ooder Skills 的设计遵循以下核心原则：

```
┌─────────────────────────────────────────────────────────────┐
│                    设计理念金字塔                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│                      ┌─────────┐                            │
│                      │  可扩展  │                            │
│                      └────┬────┘                            │
│                     ┌─────┴─────┐                           │
│                     │  可发现   │                            │
│                     └─────┬─────┘                           │
│                ┌──────────┴──────────┐                      │
│                │      可复用          │                      │
│                └──────────┬──────────┘                      │
│           ┌───────────────┴───────────────┐                 │
│           │          标准化               │                  │
│           └───────────────────────────────┘                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**标准化**：统一的技能定义规范（skill.yaml、skill-manifest.yaml）

**可复用**：技能独立打包，一次开发多处使用

**可发现**：支持多种发现机制（Git仓库、本地文件、WebDAV、UDP广播）

**可扩展**：插件化架构，按需加载技能

### 1.2 技能架构全景图

```
┌─────────────────────────────────────────────────────────────────┐
│                    Ooder Agent Platform                          │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │  技能市场    │  │  技能中心    │  │  场景管理    │             │
│  │ Skill Market│  │Skill Center │  │Scene Manager│             │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘             │
│         │                │                │                     │
│         └────────────────┼────────────────┘                     │
│                          ▼                                       │
│  ┌───────────────────────────────────────────────────────┐      │
│  │              SkillPackageManager (SDK)                 │      │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │      │
│  │  │   发现器    │  │   安装器    │  │  生命周期    │   │      │
│  │  │ Discoverer  │  │  Installer  │  │  Lifecycle  │   │      │
│  │  └─────────────┘  └─────────────┘  └─────────────┘   │      │
│  └───────────────────────────────────────────────────────┘      │
│                          │                                       │
│         ┌────────────────┼────────────────┐                     │
│         ▼                ▼                ▼                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │ GitHub/Gitee│  │  本地文件   │  │  WebDAV     │             │
│  │  远程仓库    │  │   系统     │  │   服务      │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
```

### 1.3 技能生命周期

```
  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
  │  发现   │───▶│  安装   │───▶│  运行   │───▶│  停止   │
  │ Discover│    │ Install │    │  Run    │    │  Stop   │
  └─────────┘    └─────────┘    └─────────┘    └─────────┘
       │              │              │              │
       │              │              │              │
       ▼              ▼              ▼              ▼
  ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
  │  配置   │    │  卸载   │    │  暂停   │    │  卸载   │
  │ Config  │    │Uninstall│    │ Pause   │    │Uninstall│
  └─────────┘    └─────────┘    └─────────┘    └─────────┘
```

### 1.4 发现方法 (DiscoveryMethod)

| 方法 | 代码 | 说明 | 适用场景 |
|------|------|------|---------|
| 本地文件系统 | `LOCAL_FS` | 从本地目录发现技能 | 开发调试、离线环境 |
| Git仓库 | `GIT_REPOSITORY` | 从 GitHub/Gitee 发现 | 远程安装、版本管理 |
| 技能中心 | `SKILL_CENTER` | 从企业技能中心发现 | 企业内部技能市场 |
| UDP广播 | `UDP_BROADCAST` | P2P 网络发现 | 局域网技能共享 |
| 自动推断 | `AUTO` | 根据来源自动选择 | 智能发现 |

### 1.5 场景驱动设计

Ooder Skills 采用场景驱动的设计模式，每个技能绑定特定场景：

```
┌─────────────────────────────────────────────────────────────┐
│                      场景分类体系                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  通讯类       │  │  业务类       │  │  IoT类       │      │
│  │ COMMUNICATION │  │  BUSINESS    │  │    IOT       │      │
│  ├──────────────┤  ├──────────────┤  ├──────────────┤      │
│  │ p2p          │  │ hr           │  │ device       │      │
│  │ group        │  │ crm          │  │ collection   │      │
│  │ broadcast    │  │ finance      │  │ edge         │      │
│  │ mqtt-messaging│ │ approval     │  │              │      │
│  └──────────────┘  │ project      │  └──────────────┘      │
│                    │ knowledge    │                         │
│  ┌──────────────┐  └──────────────┘  ┌──────────────┐      │
│  │  协作类       │                    │  系统类       │      │
│  │COLLABORATION │                    │   SYSTEM     │      │
│  ├──────────────┤                    ├──────────────┤      │
│  │ meeting      │                    │ auth         │      │
│  │ document     │                    │ sys          │      │
│  │ task         │                    │ monitor      │      │
│  │ ui-generation│                    │ security     │      │
│  └──────────────┘                    └──────────────┘      │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 二、资源篇：技能仓库全景

### 2.1 技能清单

#### 组织服务技能

| 技能ID | 名称 | 描述 | 场景 |
|--------|------|------|------|
| `skill-org-dingding` | 钉钉组织服务 | 钉钉组织架构同步和用户认证 | auth |
| `skill-org-feishu` | 飞书组织服务 | 飞书组织架构同步和用户认证 | auth |
| `skill-org-wecom` | 企业微信组织服务 | 企业微信组织架构同步和用户认证 | auth |
| `skill-org-ldap` | LDAP组织服务 | 基于 LDAP 的组织架构管理和用户认证 | auth |
| `skill-user-auth` | 用户认证服务 | 支持多种认证方式的用户认证 | auth |

#### UI生成技能

| 技能ID | 名称 | 描述 | 场景 |
|--------|------|------|------|
| `skill-a2ui` | A2UI 技能 | 设计图转前端代码 | ui-generation |

#### 通讯服务技能

| 技能ID | 名称 | 描述 | 场景 |
|--------|------|------|------|
| `skill-mqtt` | MQTT 服务技能 | 轻量级 MQTT Broker 能力 | mqtt-messaging |

#### 工具技能

| 技能ID | 名称 | 描述 | 场景 |
|--------|------|------|------|
| `skill-trae-solo` | Trae Solo 服务 | 项目管理与协作工具集 | utility |

#### 存储服务技能 (VFS)

| 技能ID | 名称 | 描述 | 场景 |
|--------|------|------|------|
| `skill-vfs-database` | 数据库存储服务 | 文件存储在数据库中 | vfs-database |
| `skill-vfs-local` | 本地存储服务 | 本地文件系统存储 | vfs-local |
| `skill-vfs-minio` | MinIO 存储服务 | MinIO 对象存储 | vfs-minio |
| `skill-vfs-oss` | 阿里云 OSS 存储服务 | 阿里云 OSS 对象存储 | vfs-oss |
| `skill-vfs-s3` | AWS S3 存储服务 | AWS S3 对象存储 | vfs-s3 |

### 2.2 技能目录结构

```
ooder-skills/
├── README.md                   # 仓库说明
├── skill-index.yaml           # 技能索引文件
├── pom.xml                    # Maven 父 POM
│
├── skills/                    # 技能目录
│   ├── skill-a2ui/            # A2UI 技能
│   │   ├── skill-manifest.yaml
│   │   ├── skill.yaml
│   │   ├── README.md
│   │   ├── pom.xml
│   │   └── src/main/
│   │       ├── java/
│   │       └── resources/
│   │
│   ├── skill-org-dingding/    # 钉钉组织技能
│   ├── skill-org-feishu/      # 飞书组织技能
│   ├── skill-org-wecom/       # 企业微信组织技能
│   ├── skill-org-ldap/        # LDAP 组织技能
│   ├── skill-user-auth/       # 用户认证技能
│   ├── skill-mqtt/            # MQTT 服务技能
│   ├── skill-trae-solo/       # Trae Solo 技能
│   │
│   ├── skill-vfs-database/    # 数据库存储技能
│   ├── skill-vfs-local/       # 本地存储技能
│   ├── skill-vfs-minio/       # MinIO 存储技能
│   ├── skill-vfs-oss/         # 阿里云 OSS 存储技能
│   └── skill-vfs-s3/          # AWS S3 存储技能
│
├── templates/                 # 技能模板
│   ├── README.md
│   ├── skill-manifest.yaml
│   └── skill.yaml
│
├── docs/                      # 文档
│   ├── SKILL_DEVELOPMENT.md
│   └── SCENE_DESIGN.md
│
└── scripts/                   # 脚本工具
    └── skill-upload.py
```

### 2.3 技能定义规范

#### skill-manifest.yaml（技能清单）

```yaml
apiVersion: ooder.io/v1
kind: SkillPackage

metadata:
  name: skill-xxx                    # 技能ID
  version: 0.7.3                     # 版本号
  description: 技能描述               # 描述
  author: Ooder Team                 # 作者
  license: Apache-2.0                # 许可证
  homepage: https://gitee.com/ooderCN/skills
  repository: https://gitee.com/ooderCN/skills.git
  keywords:
    - keyword1
    - keyword2

spec:
  type: service-skill                # 技能类型
  
  capabilities:                      # 能力列表
    - capability-id-1
    - capability-id-2

  scenes:                            # 场景列表
    - scene-name

  parameters:                        # 配置参数
    - name: PARAM_NAME
      type: string
      required: true
      description: 参数描述
      secret: false

  execution:                         # 执行配置
    timeout: 30000
    memoryLimit: 256M
    cpuLimit: 1

  distribution:                      # 分发配置
    format: jar
    entrypoint: net.ooder.skill.xxx.SkillApplication
    assets:
      - name: skill-xxx-0.7.3.jar
        platform: all
        url: https://gitee.com/ooderCN/skills/releases/download/v0.7.3/skill-xxx-0.7.3.jar

  compatibility:                     # 兼容性
    sdkVersion: ">=0.7.0"
    javaVersion: ">=8"
```

#### skill.yaml（技能定义）

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-xxx
  name: Skill Display Name
  version: 0.7.3
  description: 技能描述
  author: Ooder Team
  license: Apache-2.0

spec:
  type: service-skill
  
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
    mainClass: net.ooder.skill.xxx.SkillApplication
  
  capabilities:
    - id: capability-id
      name: Capability Name
      description: 能力描述
      category: category-name
      parameters:
        - name: param1
          type: string
          required: true
      returns:
        type: object
  
  scenes:
    - name: scene-name
      description: 场景描述
      capabilities:
        - capability-id
  
  config:
    required:
      - name: REQUIRED_PARAM
        type: string
        secret: true
    optional:
      - name: OPTIONAL_PARAM
        type: string
        default: default_value
  
  endpoints:
    - path: /api/info
      method: GET
      description: 获取技能信息
    - path: /api/execute
      method: POST
      description: 执行能力
      capability: capability-id
  
  resources:
    cpu: "250m"
    memory: "256Mi"
    storage: "512Mi"

  deployment:
    modes:
      - local-deployed
      - remote-hosted
    singleton: false
```

### 2.4 仓库地址

| 平台 | 地址 | 适用场景 |
|------|------|---------|
| **Gitee (国内)** | https://gitee.com/ooderCN/skills | 国内用户优先 |
| **GitHub (国际)** | https://github.com/ooderCN/skills | 国际用户 |

---

## 三、工具链篇：开发与管理

### 3.1 开发工具链

#### 技能开发模板

```bash
# 创建技能目录
mkdir -p skills/skill-xxx/src/main/java/net/ooder/skill/xxx
mkdir -p skills/skill-xxx/src/main/resources

# 复制模板文件
cp templates/skill-manifest.yaml skills/skill-xxx/
cp templates/skill.yaml skills/skill-xxx/
```

#### Maven 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <parent>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-skills</artifactId>
        <version>0.7.3</version>
    </parent>
    
    <artifactId>skill-xxx</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>agent-sdk</artifactId>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

#### 技能代码模板

```java
package net.ooder.skill.xxx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class SkillApplication {
    
    public static void main(String[] args) {
        System.setProperty("server.port", "808x");
        SpringApplication.run(SkillApplication.class, args);
    }
    
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("skillId", "skill-xxx");
        info.put("name", "Skill Name");
        info.put("version", "0.7.3");
        info.put("capabilities", new String[]{"capability-1", "capability-2"});
        return info;
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        return health;
    }
    
    @PostMapping("/capability/{capabilityId}")
    public Map<String, Object> executeCapability(
            @PathVariable String capabilityId,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("data", null);
        return result;
    }
}
```

### 3.2 构建工具链

#### 本地构建

```bash
# 构建单个技能
cd skills/skill-xxx
mvn clean package -DskipTests

# 构建所有技能
cd ooder-skills
mvn clean package -DskipTests
```

#### 发布到仓库

```bash
# 使用上传脚本
python scripts/skill-upload.py \
    --skill skill-xxx \
    --version 0.7.3 \
    --jar target/skill-xxx-0.7.3.jar \
    --token ${GITEE_TOKEN}

# 或手动发布
mvn deploy -Dgpg.skip=true
```

### 3.3 安装工具链

#### 方式一：从远程仓库安装

```bash
# 使用 ooder CLI
ooder skill install skill-org-feishu --source github --version 0.7.3
ooder skill install skill-org-feishu --source gitee --version 0.7.3

# 使用 API
curl -X POST http://localhost:8081/api/skillcenter/installed/install \
  -H "Content-Type: application/json" \
  -d '{
    "skillId": "skill-org-feishu",
    "version": "0.7.3",
    "source": "github",
    "discoveryMethod": "GIT_REPOSITORY"
  }'
```

#### 方式二：本地安装

```bash
# 下载技能包
wget https://github.com/ooderCN/skills/releases/download/v0.7.3/skill-org-feishu-0.7.3.jar

# 安装
ooder skill install --local ./skill-org-feishu-0.7.3.jar

# 或使用 API
curl -X POST http://localhost:8081/api/skillcenter/installed/install \
  -H "Content-Type: multipart/form-data" \
  -F "file=@skill-org-feishu-0.7.3.jar"
```

#### 方式三：WebDAV 安装

```bash
# 上传到 WebDAV
curl -T skill-xxx-0.7.3.jar \
  -u username:password \
  http://webdav-server/skills/skill-xxx-0.7.3.jar

# 从 WebDAV 安装
curl -X POST http://localhost:8081/api/skillcenter/installed/install \
  -H "Content-Type: application/json" \
  -d '{
    "skillId": "skill-xxx",
    "version": "0.7.3",
    "downloadUrl": "http://webdav-server/skills/skill-xxx-0.7.3.jar"
  }'
```

### 3.4 管理工具链

#### 技能生命周期管理

```bash
# 启动技能
ooder skill start skill-org-feishu

# 停止技能
ooder skill stop skill-org-feishu

# 卸载技能
ooder skill uninstall skill-org-feishu

# 查看技能状态
ooder skill status skill-org-feishu
```

#### 技能配置管理

```bash
# 查看配置
ooder skill config skill-org-feishu --get

# 设置配置
ooder skill config skill-org-feishu --set FEISHU_APP_ID=your-app-id
ooder skill config skill-org-feishu --set FEISHU_APP_SECRET=your-app-secret

# 查看日志
ooder skill logs skill-org-feishu --tail 100
```

#### 技能市场操作

```bash
# 搜索技能
curl -X POST http://localhost:8081/api/skillcenter/market/search \
  -H "Content-Type: application/json" \
  -d '{"keyword": "feishu"}'

# 获取技能详情
curl -X POST http://localhost:8081/api/skillcenter/market/detail \
  -H "Content-Type: application/json" \
  -d '{"skillId": "skill-org-feishu"}'

# 列出已安装技能
curl -X GET http://localhost:8081/api/skillcenter/installed/list
```

### 3.5 开发最佳实践

#### 1. 版本管理

```
版本号格式: MAJOR.MINOR.PATCH

MAJOR: 不兼容的 API 变更
MINOR: 向后兼容的功能新增
PATCH: 向后兼容的问题修复

示例:
- 0.7.0 -> 0.7.1: Bug 修复
- 0.7.1 -> 0.8.0: 新功能
- 0.8.0 -> 1.0.0: 重大变更
```

#### 2. 文档规范

```markdown
# skill-xxx

## 概述
技能简介

## 能力列表
- capability-1: 能力1描述
- capability-2: 能力2描述

## 配置参数
| 参数 | 类型 | 必需 | 说明 |
|------|------|------|------|
| PARAM_1 | string | 是 | 参数1说明 |

## API 端点
| 端点 | 方法 | 说明 |
|------|------|------|
| /api/info | GET | 获取信息 |
| /api/execute | POST | 执行能力 |

## 使用示例
代码示例

## 变更日志
版本历史
```

#### 3. 错误处理

```java
public class SkillErrorResponse {
    private boolean success = false;
    private String errorCode;
    private String errorMessage;
    private long timestamp;
    
    public static SkillErrorResponse of(String code, String message) {
        SkillErrorResponse response = new SkillErrorResponse();
        response.errorCode = code;
        response.errorMessage = message;
        response.timestamp = System.currentTimeMillis();
        return response;
    }
}
```

#### 4. 健康检查

```java
@GetMapping("/health")
public Map<String, Object> health() {
    Map<String, Object> health = new LinkedHashMap<>();
    health.put("status", "UP");
    health.put("skillId", "skill-xxx");
    health.put("version", "0.7.3");
    health.put("uptime", getUptime());
    health.put("dependencies", checkDependencies());
    return health;
}
```

---

## 四、总结

Ooder Skills 通过标准化的技能定义、灵活的发现机制和完善的工具链，为 Agent 平台提供了强大的能力扩展能力。无论是企业级应用集成还是个人工具开发，都能在 Ooder Skills 中找到合适的解决方案。

### 关键特性

| 特性 | 说明 |
|------|------|
| **标准化** | 统一的技能定义规范 |
| **可发现** | 多种发现机制支持 |
| **可扩展** | 插件化架构设计 |
| **可复用** | 一次开发多处使用 |
| **可管理** | 完整的生命周期管理 |

### 快速开始

```bash
# 1. 克隆仓库
git clone https://gitee.com/ooderCN/skills.git

# 2. 安装技能
ooder skill install skill-org-feishu --source gitee

# 3. 配置技能
ooder skill config skill-org-feishu --set FEISHU_APP_ID=xxx

# 4. 启动技能
ooder skill start skill-org-feishu

# 5. 调用能力
curl http://localhost:808x/api/capability/org-query
```

---

**参考资料**

- [技能开发指南](docs/SKILL_DEVELOPMENT.md)
- [场景设计规范](docs/SCENE_DESIGN.md)
- [Ooder Agent Platform](https://github.com/ooderCN/agent-sdk)

---

<div align="center">

**Made with ❤️ by Ooder Team**

[GitHub](https://github.com/ooderCN/skills) | [Gitee](https://gitee.com/ooderCN/skills) | [Documentation](https://ooder.net/docs)

</div>
