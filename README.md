# Ooder Skills

Ooder Agent Platform 技能库 - 为智能体平台提供可复用的技能模块。

## 概述

Ooder Skills 是一个模块化的技能库，为 Ooder Agent Platform 提供各种可插拔的能力扩展。每个技能（Skill）都是一个独立的微服务，通过标准化的 API 提供特定领域的功能。

## 技能列表

### 系统管理类 (sys)

| 技能 | 描述 | 场景 |
|------|------|------|
| [skill-network](skills/skill-network) | 网络管理服务 | network |
| [skill-security](skills/skill-security) | 安全管理服务 | security |
| [skill-hosting](skills/skill-hosting) | 托管服务 | hosting |
| [skill-monitor](skills/skill-monitor) | 监控服务 | monitor |
| [skill-health](skills/skill-health) | 健康检查服务 | health |
| [skill-agent](skills/skill-agent) | 代理管理服务 | agent |
| [skill-openwrt](skills/skill-openwrt) | OpenWrt路由器驱动 | openwrt |
| [skill-audit](skills/skill-audit) | 审计日志服务 | audit |
| [skill-access-control](skills/skill-access-control) | 访问控制服务 | security |
| [skill-remote-terminal](skills/skill-remote-terminal) | 远程终端服务 | remote |

### 消息通讯类 (msg)

| 技能 | 描述 | 场景 |
|------|------|------|
| [skill-im](skills/skill-im) | 即时通讯服务 | im |
| [skill-group](skills/skill-group) | 群组管理服务 | group |
| [skill-msg](skills/skill-msg) | 消息服务 | msg |
| [skill-mqtt](skills/skill-mqtt) | MQTT服务 | mqtt |

### 组织管理类 (org)

| 技能 | 描述 | 场景 |
|------|------|------|
| [skill-org-dingding](skills/skill-org-dingding) | 钉钉组织集成 | dingding |
| [skill-org-feishu](skills/skill-org-feishu) | 飞书组织集成 | feishu |
| [skill-user-auth](skills/skill-user-auth) | 用户认证服务 | auth |

### UI生成类 (ui)

| 技能 | 描述 | 场景 |
|------|------|------|
| [skill-a2ui](skills/skill-a2ui) | A2UI生成服务 | ui-generation |

### 工具类 (util)

| 技能 | 描述 | 场景 |
|------|------|------|
| [skill-scheduler-quartz](skills/skill-scheduler-quartz) | Quartz调度器 | scheduler |
| [skill-k8s](skills/skill-k8s) | Kubernetes管理 | k8s |
| [skill-vfs-local](skills/skill-vfs-local) | 本地文件系统 | vfs |

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- Spring Boot 2.7.x

### 构建项目

```bash
# 克隆仓库
git clone https://github.com/ooderCN/skills.git
cd skills

# 构建所有技能
mvn clean install

# 构建单个技能
mvn clean install -pl skills/skill-audit
```

### 运行技能

```bash
# 运行审计日志服务
java -jar skills/skill-audit/target/skill-audit-0.7.3.jar

# 指定端口运行
java -jar skills/skill-audit/target/skill-audit-0.7.3.jar --server.port=8091
```

## 技能开发指南

### 项目结构

```
skills/
├── skill-xxx/                    # 技能模块
│   ├── pom.xml                   # Maven配置
│   ├── skill-manifest.yaml       # 技能清单
│   └── src/
│       └── main/
│           ├── java/
│           │   └── net/ooder/skill/xxx/
│           │       ├── XxxSkillApplication.java  # 启动类
│           │       ├── controller/               # REST控制器
│           │       ├── service/                  # 业务服务
│           │       └── dto/                      # 数据对象
│           └── resources/
│               └── application.yml               # 应用配置
```

### 技能清单 (skill-manifest.yaml)

每个技能必须包含 `skill-manifest.yaml` 文件，定义技能的元数据和能力：

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillManifest

metadata:
  id: skill-xxx
  name: XXX Service
  version: 0.7.3
  description: 技能描述

spec:
  type: tool-skill
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
  capabilities:
    - id: xxx-capability
      name: XXX Capability
      category: xxx
  scenes:
    - name: xxx
      capabilities:
        - xxx-capability
  endpoints:
    - path: /api/xxx/action
      method: POST
      capability: xxx-capability
```

### API 规范

所有技能遵循统一的 API 规范：

1. **基础路径**: `/api/{skill-name}/{action}`
2. **请求格式**: JSON
3. **响应格式**: JSON
4. **错误处理**: 统一错误响应结构

```json
{
  "code": 400,
  "message": "错误描述",
  "timestamp": 1700000000000
}
```

## 应用场景

### 企业应用

- **权限管理**: 使用 `skill-access-control` 实现基于角色的访问控制
- **审计合规**: 使用 `skill-audit` 记录操作日志满足合规要求
- **组织集成**: 通过 `skill-org-dingding`、`skill-org-feishu` 集成企业通讯工具

### 运维管理

- **远程运维**: 使用 `skill-remote-terminal` 进行服务器远程管理
- **监控告警**: 使用 `skill-monitor` 实现系统监控
- **健康检查**: 使用 `skill-health` 进行服务健康检查

### DevOps

- **CI/CD**: 使用 `skill-scheduler-quartz` 实现定时任务调度
- **容器管理**: 使用 `skill-k8s` 管理 Kubernetes 集群
- **托管服务**: 使用 `skill-hosting` 管理云服务实例

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/new-skill`)
3. 提交更改 (`git commit -m 'feat: 添加新技能'`)
4. 推送到分支 (`git push origin feature/new-skill`)
5. 创建 Pull Request

## 许可证

Apache License 2.0

## 联系方式

- 主页: https://github.com/ooderCN/skills
- 问题反馈: https://github.com/ooderCN/skills/issues
