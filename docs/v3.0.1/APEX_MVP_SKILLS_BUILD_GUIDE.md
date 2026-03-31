# Apex MVP 最小 Skills 化工程构建方案

> 版本: 1.0.0 | 更新日期: 2026-03-30 | 适用: Apex Team

## 一、方案概述

### 1.1 目标

为 Apex MVP 版本定义最小 Skills 集合，实现：
- **快速启动**：最小依赖，快速构建
- **核心功能**：场景驱动 + Agent 对话 + 组织管理
- **可扩展性**：支持按需加载更多 Skills

### 1.2 设计原则

| 原则 | 说明 |
|------|------|
| **最小化** | 只包含运行必需的 Skills |
| **分层化** | 内置层 + 可选层 + 扩展层 |
| **渐进式** | 支持从 MVP 到完整版的平滑升级 |

---

## 二、MVP 架构分层

```
┌─────────────────────────────────────────────────────────────────┐
│                     Apex MVP Architecture                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │              Layer 0: SDK Core (pom.xml 内置)              │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │ │
│  │  │ scene-engine│ │ agent-sdk   │ │ skill-common        │  │ │
│  │  │ 场景引擎    │ │ Agent SDK   │ │ 技能公共基础        │  │ │
│  │  └─────────────┘ └─────────────┘ └─────────────────────┘  │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │ │
│  │  │skill-org-base│ │hotplug     │ │ skills-framework    │  │ │
│  │  │组织基础     │ │热插拔机制   │ │ 技能框架            │  │ │
│  │  └─────────────┘ └─────────────┘ └─────────────────────┘  │ │
│  └───────────────────────────────────────────────────────────┘ │
│                              │                                  │
│                              ▼                                  │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │              Layer 1: MVP Skills (最小集合)                │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐  │ │
│  │  │skill-llm-*  │ │ skill-im-*  │ │ skill-platform-bind │  │ │
│  │  │ LLM驱动     │ │ IM驱动      │ │ 平台绑定场景        │  │ │
│  │  │ (选1个)     │ │ (选1个)     │ │                     │  │ │
│  │  └─────────────┘ └─────────────┘ └─────────────────────┘  │ │
│  └───────────────────────────────────────────────────────────┘ │
│                              │                                  │
│                              ▼                                  │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │              Layer 2: Optional Skills (可选)               │ │
│  │  skill-calendar | skill-msg-push | skill-knowledge-*      │ │
│  └───────────────────────────────────────────────────────────┘ │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 三、最小 Skills 集合定义

### 3.1 Layer 0: SDK Core (内置，不可卸载)

这些通过 `pom.xml` 依赖，是 Apex 运行的基础：

| 模块 | Maven 坐标 | 职责 |
|------|-----------|------|
| scene-engine | `net.ooder:scene-engine:3.0.1` | 场景引擎核心 |
| agent-sdk-core | `net.ooder:agent-sdk-core:3.0.1` | Agent SDK 核心 |
| skill-common | `net.ooder:skill-common:3.0.1` | 技能公共基础 |
| skill-org-base | `net.ooder:skill-org-base:3.0.1` | 本地组织管理 |
| skill-hotplug-starter | `net.ooder:skill-hotplug-starter:3.0.1` | 热插拔机制 |
| skills-framework | `net.ooder:skills-framework:3.0.1` | 技能框架 |

### 3.2 Layer 1: MVP Skills (最小可选集合)

MVP 版本推荐的最小 Skills 集合：

#### 3.2.1 LLM 驱动 (必选 1 个)

| Skill ID | 说明 | 推荐场景 |
|----------|------|----------|
| `skill-llm-deepseek` | DeepSeek API | **推荐**：性价比高，国内可用 |
| `skill-llm-qianwen` | 通义千问 | 阿里云用户 |
| `skill-llm-openai` | OpenAI API | 海外用户 |
| `skill-llm-ollama` | Ollama 本地 | 私有化部署 |

**MVP 推荐**: `skill-llm-deepseek`

#### 3.2.2 IM 驱动 (可选 0-1 个)

| Skill ID | 说明 | 推荐场景 |
|----------|------|----------|
| `skill-im-dingding` | 钉钉 IM | 钉钉企业用户 |
| `skill-im-feishu` | 飞书 IM | 飞书企业用户 |
| `skill-im-wecom` | 企业微信 IM | 企业微信用户 |

**MVP 推荐**: 根据企业实际使用的平台选择

#### 3.2.3 场景服务 (可选)

| Skill ID | 说明 | MVP 推荐 |
|----------|------|----------|
| `skill-platform-bind` | 平台绑定场景 | ✅ 推荐 |

---

## 四、MVP 配置方案

### 4.1 最小配置 (纯对话模式)

**适用场景**: 快速体验 Apex 对话能力

```yaml
# application-mvp-minimal.yml
ooder:
  scene:
    auto-init: true
    default-group-id: sg-default
  discovery:
    use-se-sdk: true
    use-index-first: true
  llm:
    provider: deepseek
    model: deepseek-chat
    deepseek:
      api-key: ${DEEPSEEK_API_KEY:}

# MVP 最小 Skills 集合
skills:
  minimal:
    - skill-llm-deepseek
```

**启动命令**:
```bash
java -jar apex-core.jar --spring.profiles.active=mvp-minimal
```

### 4.2 标准配置 (对话 + IM)

**适用场景**: 企业内部使用，接入企业 IM

```yaml
# application-mvp-standard.yml
ooder:
  scene:
    auto-init: true
    default-group-id: sg-default
  discovery:
    use-se-sdk: true
    use-index-first: true
  llm:
    provider: deepseek
    model: deepseek-chat
    deepseek:
      api-key: ${DEEPSEEK_API_KEY:}

# 钉钉集成配置
dingtalk:
  app-key: ${DINGTALK_APP_KEY:}
  app-secret: ${DINGTALK_APP_SECRET:}

# MVP 标准 Skills 集合
skills:
  standard:
    - skill-llm-deepseek
    - skill-im-dingding
    - skill-platform-bind
```

**启动命令**:
```bash
java -jar apex-core.jar --spring.profiles.active=mvp-standard
```

### 4.3 完整配置 (对话 + IM + 工具)

**适用场景**: 完整体验 Apex 能力

```yaml
# application-mvp-full.yml
ooder:
  scene:
    auto-init: true
    default-group-id: sg-default
  discovery:
    use-se-sdk: true
    use-index-first: true
  llm:
    provider: deepseek
    model: deepseek-chat
    deepseek:
      api-key: ${DEEPSEEK_API_KEY:}

# 钉钉集成配置
dingtalk:
  app-key: ${DINGTALK_APP_KEY:}
  app-secret: ${DINGTALK_APP_SECRET:}

# MVP 完整 Skills 集合
skills:
  full:
    - skill-llm-deepseek
    - skill-im-dingding
    - skill-platform-bind
    - skill-calendar
    - skill-msg-push
```

---

## 五、MVP Skills 集合清单

### 5.1 必选 Skills

| Skill ID | 名称 | 形式 | 来源 | 说明 |
|----------|------|------|------|------|
| `skill-llm-deepseek` | DeepSeek LLM | DRIVER | 热插拔 | LLM 对话能力 |

### 5.2 可选 Skills

| Skill ID | 名称 | 形式 | 来源 | 说明 |
|----------|------|------|------|------|
| `skill-im-dingding` | 钉钉 IM | DRIVER | 热插拔 | 钉钉消息能力 |
| `skill-im-feishu` | 飞书 IM | DRIVER | 热插拔 | 飞书消息能力 |
| `skill-im-wecom` | 企业微信 IM | DRIVER | 热插拔 | 企业微信消息能力 |
| `skill-platform-bind` | 平台绑定 | SCENE | 热插拔 | 平台授权绑定 |
| `skill-calendar` | 日历服务 | PROVIDER | 热插拔 | 日程管理 |
| `skill-msg-push` | 消息推送 | PROVIDER | 热插拔 | 多渠道推送 |

### 5.3 MVP Skills 总数

| 配置方案 | Skills 数量 | 说明 |
|----------|-------------|------|
| 最小配置 | 1 | 仅 LLM 驱动 |
| 标准配置 | 3 | LLM + IM + 平台绑定 |
| 完整配置 | 6 | LLM + IM + 平台绑定 + 工具 |

---

## 六、构建步骤

### 6.1 环境准备

```bash
# 1. 确保 Maven 仓库有 SE SDK 3.0.1
ls D:\maven\.m2\repository\net\ooder\scene-engine\3.0.1\

# 2. 克隆 Skills 仓库
git clone https://gitee.com/ooderCN/skills.git
```

### 6.2 构建 MVP Skills

```bash
# 进入 skills 目录
cd skills

# 构建 MVP 必选 Skills
mvn clean install -pl skills/_drivers/llm/skill-llm-deepseek -am

# 构建可选 Skills (按需)
mvn clean install -pl skills/_drivers/im/skill-im-dingding -am
mvn clean install -pl skills/scenes/skill-platform-bind -am
```

### 6.3 配置 Apex

```bash
# 复制配置文件
cp docs/v3.0.1/profiles/application-mvp-standard.yml E:\apex\app\src\main\resources\

# 设置环境变量
export DEEPSEEK_API_KEY=your-api-key
export DINGTALK_APP_KEY=your-app-key
export DINGTALK_APP_SECRET=your-app-secret
```

### 6.4 启动 Apex MVP

```bash
cd E:\apex\app
mvn spring-boot:run -Dspring-boot.run.profiles=mvp-standard
```

---

## 七、验证清单

### 7.1 功能验证

| 功能 | 验证方法 | 预期结果 |
|------|----------|----------|
| Agent 对话 | 访问 `/agent/chat` | 可以与 Agent 对话 |
| 场景创建 | 调用场景 API | 可以创建场景 |
| 组织管理 | 访问 `/org/users` | 可以管理用户 |
| IM 集成 | 扫码绑定 | 可以绑定平台 |
| 消息发送 | 发送测试消息 | 消息成功送达 |

### 7.2 性能验证

| 指标 | MVP 目标 | 说明 |
|------|----------|------|
| 启动时间 | < 30s | 冷启动 |
| 内存占用 | < 512MB | 运行时 |
| 对话响应 | < 3s | 首字延迟 |

---

## 八、升级路径

### 8.1 MVP → Standard

```
MVP (1 Skill)
    │
    ├── 添加 skill-im-* (IM 驱动)
    │
    └── 添加 skill-platform-bind (平台绑定)
    
Standard (3 Skills)
```

### 8.2 Standard → Full

```
Standard (3 Skills)
    │
    ├── 添加 skill-calendar (日历服务)
    │
    ├── 添加 skill-msg-push (消息推送)
    │
    └── 添加 skill-todo-sync (待办同步)
    
Full (6 Skills)
```

### 8.3 Full → Enterprise

```
Full (6 Skills)
    │
    ├── 添加 skill-knowledge-* (知识服务)
    │
    ├── 添加 skill-org-* (组织扩展)
    │
    └── 添加自定义场景
    
Enterprise (N Skills)
```

---

## 九、附录

### A. MVP Skills 依赖关系

```
skill-llm-deepseek
    └── skill-common (内置)

skill-im-dingding
    ├── skill-common (内置)
    └── skill-org-base (内置)

skill-platform-bind
    ├── skill-common (内置)
    ├── skill-org-base (内置)
    └── skill-im-* (可选)
```

### B. 配置文件模板

**文件路径**: `E:\apex\app\src\main\resources\application-mvp.yml`

```yaml
server:
  port: 8084

spring:
  application:
    name: apex-mvp
  profiles:
    active: mvp

app:
  storage:
    path: ./data

org-web:
  storage-path: org
  auto-init: true

agent:
  session:
    timeout: 86400

ooder:
  scene:
    auto-init: true
    default-group-id: sg-mvp
  discovery:
    use-se-sdk: true
    use-index-first: true
  llm:
    provider: deepseek
    model: deepseek-chat
    deepseek:
      api-key: ${DEEPSEEK_API_KEY:}

scene:
  engine:
    discovery:
      enabled: true
      gitee:
        enabled: true
        token: ${GITEE_TOKEN:}
        default-owner: ooderCN
        default-repo: skills
        default-branch: master
      cache:
        enabled: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

### C. 相关文档

| 文档 | 路径 |
|------|------|
| Skills 完整清单 | `e:\github\ooder-skills\docs\v3.0.1\SKILLS_INVENTORY.md` |
| 分类定义 | `e:\github\ooder-skills\skill-index\categories.yaml` |
| Apex 架构文档 | `E:\apex\app\docs\ARCHITECTURE.md` |

---

**文档版本**: v1.0.0  
**发布日期**: 2026-03-30  
**维护团队**: Apex Team
