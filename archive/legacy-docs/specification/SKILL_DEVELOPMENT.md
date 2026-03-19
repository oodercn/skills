# 技能开发指南

## 概述

本文档介绍如何在 Ooder Agent Platform 上开发新技能。

## 技能类型

| 类型 | 说明 | 示例 |
|------|------|------|
| service-skill | 服务型技能，提供API服务 | skill-user-auth |
| enterprise-skill | 企业型技能，集成企业服务 | skill-org-feishu |
| tool-skill | 工具型技能，提供工具能力 | skill-a2ui |

## 技能结构

```
skill-xxx/
├── skill-manifest.yaml    # 技能清单（用于发现和分发）
├── skill.yaml             # 技能定义（详细配置）
├── README.md              # 技能说明
├── pom.xml                # Maven配置
└── src/
    └── main/
        ├── java/          # Java源码
        └── resources/
            └── application.yml  # 应用配置
```

## 技能清单规范 (skill-manifest.yaml)

```yaml
apiVersion: ooder.io/v1
kind: SkillPackage

metadata:
  name: skill-xxx                    # 技能ID
  version: 0.7.1                     # 版本号
  description: 技能描述               # 描述
  author: Ooder Team                 # 作者
  license: Apache-2.0                # 许可证
  homepage: https://gitee.com/ooderCN/skills/tree/main/skills/skill-xxx
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
      secret: false                  # 是否敏感信息

  execution:                         # 执行配置
    timeout: 30000
    memoryLimit: 256M
    cpuLimit: 1

  distribution:                      # 分发配置
    format: jar
    entrypoint: net.ooder.skill.xxx.SkillApplication
    assets:
      - name: skill-xxx-0.7.1.jar
        platform: all
        url: https://gitee.com/ooderCN/skills/releases/download/v0.7.1/skill-xxx-0.7.1.jar

  compatibility:                     # 兼容性
    sdkVersion: ">=0.7.0"
    javaVersion: ">=8"
```

## 技能定义规范 (skill.yaml)

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-xxx
  name: Skill Display Name
  version: 0.7.1
  description: 技能描述
  author: Ooder Team
  license: Apache-2.0
  homepage: https://gitee.com/ooderCN/skills/tree/main/skills/skill-xxx
  keywords:
    - keyword1

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
          description: 参数描述
      returns:
        type: object
        description: 返回值描述
  
  scenes:
    - name: scene-name
      description: 场景描述
      capabilities:
        - capability-id
  
  config:
    required:
      - name: REQUIRED_PARAM
        type: string
        description: 必需参数
        secret: true
    optional:
      - name: OPTIONAL_PARAM
        type: string
        default: default_value
        description: 可选参数
  
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

## 开发步骤

### 1. 创建技能目录

```bash
mkdir -p skills/skill-xxx/src/main/java/net/ooder/skill/xxx
mkdir -p skills/skill-xxx/src/main/resources
```

### 2. 创建技能定义

创建 `skill.yaml` 和 `skill-manifest.yaml` 文件。

### 3. 开发技能代码

```java
package net.ooder.skill.xxx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class SkillApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SkillApplication.class, args);
    }
    
    @GetMapping("/api/info")
    public Object getInfo() {
        return new Object();
    }
    
    @GetMapping("/api/health")
    public Object health() {
        return "OK";
    }
}
```

### 4. 配置Maven

```xml
<project>
    <groupId>net.ooder.skill</groupId>
    <artifactId>skill-xxx</artifactId>
    <version>0.7.1</version>
    <packaging>jar</packaging>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

### 5. 构建和测试

```bash
cd skills/skill-xxx
mvn clean package
java -jar target/skill-xxx-0.7.1.jar
```

### 6. 发布技能

```bash
# 使用上传工具
python scripts/skill-upload.py --skill skill-xxx --version 0.7.1 --jar target/skill-xxx-0.7.1.jar

# 或手动提交
git add skills/skill-xxx
git commit -m "Add skill-xxx v0.7.1"
git push
```

## 最佳实践

1. **版本管理**: 使用语义化版本号 (major.minor.patch)
2. **文档完善**: 提供详细的README和API文档
3. **配置分离**: 敏感配置使用环境变量
4. **健康检查**: 提供 `/api/health` 端点
5. **错误处理**: 统一错误响应格式
6. **日志规范**: 使用结构化日志

## 技能发现

技能发布后，可通过以下方式发现：

### P2P模式

```java
GitRepositoryDiscoverer discoverer = new GiteeDiscoverer();
List<SkillPackage> skills = discoverer.discoverSkills("ooderCN").get();
```

### 组织模式

```java
GitRepositoryDiscoverer discoverer = new GiteeDiscoverer(token, "ooderCN");
List<SkillPackage> skills = discoverer.discoverSkills().get();
```

## 参考

- [场景设计规范](SCENE_DESIGN.md)
- [技能模板](../templates/)
