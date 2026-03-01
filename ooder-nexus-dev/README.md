# Ooder Nexus Development Platform

统一开发平台，支持 Nexus-Lite（个人版）、Nexus-Enterprise（企业版）、SkillCenter（技能中心）三种产品组装。

## 项目结构

```
ooder-nexus-dev/
├── modules/                     # 公共模块
│   ├── nexus-common/            # 公共工具类
│   ├── nexus-protocol/          # 协议层 (DTO/API/Event)
│   └── nexus-sdk-adapter/       # SDK适配器 (P2P/Scene/A2A)
│
├── products/                    # 产品组装
│   ├── nexus-lite/              # 个人版 (端口 8081)
│   ├── nexus-enterprise/        # 企业版 (端口 8082)
│   └── skillcenter/             # 技能中心 (端口 8083)
│
├── ui/                          # 前端资源
│   ├── console/                 # 管理控制台
│   └── components/              # Web Components
│
├── docs/                        # 文档
└── scripts/                     # 构建脚本
```

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+

### 构建项目

```bash
# 构建所有模块
mvn clean package

# 仅构建特定产品
mvn clean package -pl products/nexus-lite
```

### 运行产品

```bash
# 运行 Nexus Lite
java -jar products/nexus-lite/target/nexus-lite-2.3.0-SNAPSHOT.jar

# 运行 Nexus Enterprise
java -jar products/nexus-enterprise/target/nexus-enterprise-2.3.0-SNAPSHOT.jar

# 运行 SkillCenter
java -jar products/skillcenter/target/skillcenter-2.3.0-SNAPSHOT.jar
```

## 产品对比

| 特性 | Nexus Lite | Nexus Enterprise | SkillCenter |
|------|------------|------------------|-------------|
| 定位 | 个人版 | 企业版 | 技能中心 |
| 端口 | 8081 | 8082 | 8083 |
| P2P | ✅ | ✅ | ✅ |
| 推送 | ❌ | ✅ | ✅ |
| LLM | ❌ | ✅ | ❌ |
| K8s | ❌ | ❌ | ✅ |
| 云托管 | ❌ | ❌ | ✅ |

## 技术栈

- Java 8
- Spring Boot 2.7.0
- Agent SDK 2.3.0

## 文档

- [迁移规划](../skill-ui-test/docs/THREE_PROJECTS_MIGRATION_PLAN.md)
- [三项目分析](../skill-ui-test/docs/THREE_PROJECTS_ANALYSIS.md)
- [团队任务分配](../skill-ui-test/docs/FEATURE_BREAKDOWN_TEAM_TASKS.md)

## License

Apache License 2.0
