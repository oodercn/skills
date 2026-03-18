# 智能安装任务工程分配方案

> **文档版本**: v1.0.0  
> **编写日期**: 2026-03-09  
> **基于**: NAVIGATION.md 工程导航

---

## 一、工程结构概览

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           ooder 工程层级结构                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  应用层                                                                       │
│  ├── ooder-Nexus (开源版)                                                    │
│  └── ooder-Nexus-Enterprise (企业版)                                         │
│                                                                              │
│  北向服务层 (northbound-services)                                             │
│  ├── northbound-core (SkillService接口)                                      │
│  ├── skill-org/vfs/msg (场景入口/路由)                                       │
│  └── northbound-gateway (HTTP网关)                                           │
│                                                                              │
│  Skills实现层                                                                 │
│  ├── ooder-skills (Skills公共仓库)                                           │
│  ├── super-Agent/skill-* (具体实现)                                          │
│  └── skills-a2ui/skills-rad (前端技能)                                       │
│                                                                              │
│  SDK层                                                                       │
│  ├── agent-sdk (Agent SDK核心) ✅ 已完成                                      │
│  ├── agent-skillcenter (Skill中心服务) ✅ 已完成                              │
│  └── scene-engine (场景引擎) ✅ 已完成                                        │
│                                                                              │
│  公共库层 (客户端开发包)                                                       │
│  ├── ooder-common-client                                                     │
│  ├── ooder-config / ooder-database                                           │
│  ├── ooder-server / ooder-org-web                                            │
│  └── ooder-msg-web / ooder-vfs-web                                           │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、任务工程分配表

### 2.1 Skills Team 任务 → ooder-skills 工程

| 任务ID | 任务名称 | 目标工程 | 路径 |
|--------|---------|---------|------|
| **SKILL-MOD-001** | skill-llm-conversation扩展 | ooder-skills | `skills/skill-llm-conversation/` |
| **SKILL-MOD-002** | skill-knowledge-base扩展 | ooder-skills | `skills/skill-knowledge-base/` |
| **SKILL-MOD-003** | skill-org-base扩展 | ooder-skills | `skills/skill-org-base/` |
| **SKILL-MOD-004** | skill-llm-config-manager扩展 | ooder-skills | `skills/skill-llm-config-manager/` |
| **SKILL-MOD-005** | skill-rag扩展 | ooder-skills | `skills/skill-rag/` |
| **SKILL-NEW-001** | skill-scene-installer新增 | ooder-skills | `skills/skill-scene/` |
| **SKILL-NEW-002** | skill-scene-activator新增 | ooder-skills | `skills/skill-scene/` |
| **SKILL-NEW-003** | skill-llm-installer新增 | ooder-skills | `skills/skill-llm-installer/` |
| **SCENE-PREF-001** | 安装说明书解析器 | ooder-skills | `skills/skill-scene/src/.../parser/` |
| **SCENE-PREF-002** | 工具定义注册表 | ooder-skills | `skills/skill-scene/src/.../tool/` |
| **SCENE-PREF-003** | Schema验证器 | ooder-skills | `skills/skill-scene/src/.../schema/` |
| **SCENE-PREF-004** | 安装状态机 | ooder-skills | `skills/skill-scene/src/.../state/` |
| **SCENE-PREF-005** | 角色识别服务 | ooder-skills | `skills/skill-scene/src/.../role/` |
| **SCENE-PREF-006** | 知识库预制内容加载 | ooder-skills | `skills/skill-scene/src/.../knowledge/` |
| **SCENE-PREF-009** | 降级策略处理器 | ooder-skills | `skills/skill-scene/src/.../degradation/` |

**Skills Team 任务统计**: 15个任务, 48天

---

### 2.2 LLM/AI Team 任务 → ooder-skills/docs 工程

| 任务ID | 任务名称 | 目标工程 | 路径 |
|--------|---------|---------|------|
| **LLM-001** | 安装说明书编写 | ooder-skills | `docs/llm/installation-manual/` |
| **LLM-002** | 知识库预制内容 | ooder-skills | `docs/llm/preloaded-knowledge/` |
| **LLM-003** | 工具定义设计 | ooder-skills | `docs/llm/tool-definitions/` |
| **LLM-004** | Schema设计 | ooder-skills | `docs/llm/schemas/` |
| **LLM-005** | Prompt模板设计 | ooder-skills | `docs/llm/prompt-templates/` |

**LLM/AI Team 任务统计**: 5个任务, 15天

---

### 2.3 应用开发 Team 任务 → a2ui/ouc 工程

| 任务ID | 任务名称 | 目标工程 | 路径 |
|--------|---------|---------|------|
| **APP-001** | 安装向导UI | a2ui/ouc | `src/main/resources/pages/installation/` |
| **APP-002** | 激活流程UI | a2ui/ouc | `src/main/resources/pages/activation/` |
| **APP-003** | 配置界面生成器 | a2ui/ouc | `src/main/java/.../config/` |
| **APP-004** | 安装进度监控 | a2ui/ouc | `src/main/resources/pages/monitor/` |
| **APP-005** | 降级手动配置UI | a2ui/ouc | `src/main/resources/pages/manual-config/` |
| **APP-006** | 工具调用可视化 | a2ui/ouc | `src/main/resources/pages/tool-viz/` |

**应用开发 Team 任务统计**: 6个任务, 24天

---

### 2.4 基础设施组 任务 → 多工程协作

| 任务ID | 任务名称 | 目标工程 | 路径 |
|--------|---------|---------|------|
| **INF-001** | 数据库连接池优化 | a2ui/ooder-common | `ooder-database/` |
| **INF-002** | MQTT服务集成 | ooder-skills | `skills/skill-mqtt/` |
| **INF-003** | Redis缓存集成 | a2ui/ooder-common | `ooder-server/` |
| **INF-004** | 文件存储服务 | ooder-skills | `skills/skill-vfs/` |
| **INF-005** | LLM服务适配器 | ooder-sdk | `agent-sdk/llm-sdk/` |
| **INF-006** | 向量数据库集成 | ooder-skills | `skills/skill-knowledge-base/` |
| **INF-007** | 知识库存储集成 | ooder-skills | `skills/skill-knowledge-base/` |
| **INF-008** | 健康检查服务 | ooder-sdk | `scene-engine/` |
| **INF-009** | 监控服务集成 | ooder-Nexus | `monitoring/` |

**基础设施组 任务统计**: 9个任务, 41天

---

## 三、工程任务详情

### 3.1 ooder-skills 工程 (主要开发)

**位置**: `E:\github\ooder-skills`

**负责任务**: 20个 (Skills Team 15 + LLM/AI Team 5)

**目录结构**:
```
ooder-skills/
├── docs/
│   ├── llm/                          # LLM/AI Team 任务
│   │   ├── installation-manual/      # LLM-001
│   │   ├── preloaded-knowledge/      # LLM-002
│   │   ├── tool-definitions/         # LLM-003
│   │   ├── schemas/                  # LLM-004
│   │   └── prompt-templates/         # LLM-005
│   └── v2.3/
│
├── skills/
│   ├── skill-scene/                  # 主要开发位置
│   │   ├── src/main/java/net/ooder/skill/scene/
│   │   │   ├── install/
│   │   │   │   ├── parser/           # SCENE-PREF-001
│   │   │   │   ├── tool/             # SCENE-PREF-002
│   │   │   │   ├── schema/           # SCENE-PREF-003
│   │   │   │   ├── state/            # SCENE-PREF-004
│   │   │   │   ├── role/             # SCENE-PREF-005
│   │   │   │   ├── knowledge/        # SCENE-PREF-006
│   │   │   │   └── degradation/      # SCENE-PREF-009
│   │   │   ├── installer/            # SKILL-NEW-001
│   │   │   └── activator/            # SKILL-NEW-002
│   │   └── docs/
│   │       └── smart-installation-design.md
│   │
│   ├── skill-llm-conversation/       # SKILL-MOD-001
│   ├── skill-knowledge-base/         # SKILL-MOD-002, INF-006, INF-007
│   ├── skill-org-base/               # SKILL-MOD-003
│   ├── skill-llm-config-manager/     # SKILL-MOD-004
│   ├── skill-rag/                    # SKILL-MOD-005
│   ├── skill-llm-installer/          # SKILL-NEW-003
│   ├── skill-mqtt/                   # INF-002
│   └── skill-vfs/                    # INF-004
│
└── pom.xml
```

---

### 3.2 a2ui/ouc 工程 (前端开发)

**位置**: `E:\github\a2ui\ouc`

**负责任务**: 6个 (应用开发 Team)

**目录结构**:
```
a2ui/ouc/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── .../config/            # APP-003 配置界面生成器
│   │   └── resources/
│   │       └── pages/
│   │           ├── installation/       # APP-001 安装向导UI
│   │           ├── activation/         # APP-002 激活流程UI
│   │           ├── monitor/            # APP-004 安装进度监控
│   │           ├── manual-config/      # APP-005 降级手动配置UI
│   │           └── tool-viz/           # APP-006 工具调用可视化
│   └── test/
└── pom.xml
```

---

### 3.3 a2ui/ooder-common 工程 (基础设施)

**位置**: `E:\github\a2ui\ooder-common`

**负责任务**: 2个 (INF-001, INF-003)

**目录结构**:
```
a2ui/ooder-common/
├── ooder-database/                    # INF-001 数据库连接池优化
├── ooder-server/                      # INF-003 Redis缓存集成
├── ooder-config/
├── ooder-org-web/
├── ooder-msg-web/
└── ooder-vfs-web/
```

---

### 3.4 ooder-sdk 工程 (已完成，需扩展)

**位置**: `E:\github\ooder-sdk`

**负责任务**: 2个 (INF-005, INF-008)

**目录结构**:
```
ooder-sdk/
├── agent-sdk/
│   └── llm-sdk/                       # INF-005 LLM服务适配器
├── scene-engine/                      # INF-008 健康检查服务
└── ooder-common/
```

---

### 3.5 ooder-Nexus 工程 (监控集成)

**位置**: `E:\github\ooder-Nexus`

**负责任务**: 1个 (INF-009)

**目录结构**:
```
ooder-Nexus/
├── monitoring/                        # INF-009 监控服务集成
├── data/
├── docs/
└── release/
```

---

## 四、任务执行顺序

### 4.1 Phase 1: 基础准备 (Week 1-2)

| 优先级 | 任务 | 工程 | 依赖 |
|--------|------|------|------|
| P0 | INF-001 数据库连接池优化 | a2ui/ooder-common | 无 |
| P0 | INF-005 LLM服务适配器 | ooder-sdk | 无 |
| P0 | LLM-001 安装说明书编写 | ooder-skills | 无 |
| P0 | LLM-003 工具定义设计 | ooder-skills | 无 |
| P0 | LLM-004 Schema设计 | ooder-skills | 无 |
| P0 | SCENE-PREF-002 工具定义注册表 | ooder-skills | 无 |
| P0 | SCENE-PREF-003 Schema验证器 | ooder-skills | 无 |
| P0 | SKILL-MOD-003 skill-org-base扩展 | ooder-skills | 无 |

### 4.2 Phase 2: 核心开发 (Week 3-4)

| 优先级 | 任务 | 工程 | 依赖 |
|--------|------|------|------|
| P0 | SCENE-PREF-001 安装说明书解析器 | ooder-skills | LLM-001 |
| P0 | SCENE-PREF-004 安装状态机 | ooder-skills | 无 |
| P0 | SCENE-PREF-005 角色识别服务 | ooder-skills | SKILL-MOD-003 |
| P0 | LLM-002 知识库预制内容 | ooder-skills | 无 |
| P0 | SKILL-MOD-001 skill-llm-conversation扩展 | ooder-skills | SDK ✅ |
| P0 | SKILL-MOD-002 skill-knowledge-base扩展 | ooder-skills | INF-006 |

### 4.3 Phase 3: 功能完善 (Week 5-6)

| 优先级 | 任务 | 工程 | 依赖 |
|--------|------|------|------|
| P0 | INF-002 MQTT服务集成 | ooder-skills | 无 |
| P0 | INF-006 向量数据库集成 | ooder-skills | 无 |
| P1 | SKILL-NEW-001 skill-scene-installer | ooder-skills | Phase 1-2 |
| P1 | SKILL-NEW-002 skill-scene-activator | ooder-skills | Phase 1-2 |
| P1 | APP-001 安装向导UI | a2ui/ouc | SCENE-PREF-001 |
| P1 | APP-002 激活流程UI | a2ui/ouc | SKILL-NEW-002 |

### 4.4 Phase 4: 集成测试 (Week 7-8)

| 优先级 | 任务 | 工程 | 依赖 |
|--------|------|------|------|
| P1 | INF-003 Redis缓存集成 | a2ui/ooder-common | 无 |
| P1 | INF-007 知识库存储集成 | ooder-skills | INF-006 |
| P1 | INF-008 健康检查服务 | ooder-sdk | INF-001~004 |
| P1 | INF-009 监控服务集成 | ooder-Nexus | INF-008 |
| P1 | APP-003~006 剩余UI | a2ui/ouc | Phase 3 |
| P2 | SKILL-NEW-003 skill-llm-installer | ooder-skills | 全部 |

---

## 五、工程协作接口

### 5.1 ooder-skills ↔ ooder-sdk

```java
// ooder-skills 调用 ooder-sdk 接口
// 位置: skills/skill-scene/src/.../sdk/adapter/

public interface SdkAdapter {
    // LLM SDK
    ToolCallingApi getToolCallingApi();       // 来自 agent-sdk/llm-sdk
    StructuredOutputApi getStructuredOutputApi();
    
    // Agent SDK
    SceneCollaborationApi getSceneCollaborationApi();  // 来自 agent-sdk
    CapabilityInvocationApi getCapabilityInvocationApi();
}
```

### 5.2 ooder-skills ↔ scene-engine

```java
// ooder-skills 调用 scene-engine 接口
// 位置: skills/skill-scene/src/.../engine/adapter/

public interface EngineAdapter {
    ActivationEngine getActivationEngine();    // 激活流程引擎
    MenuGenerator getMenuGenerator();          // 菜单生成引擎
    LlmContextManager getLlmContextManager();  // LLM上下文管理
    ToolRegistry getToolRegistry();            // 工具注册中心
}
```

### 5.3 a2ui/ouc ↔ ooder-skills

```yaml
# a2ui/ouc 调用 ooder-skills API
# 位置: a2ui/ouc/src/.../api/

# 安装管理API
POST /api/v1/installations
GET  /api/v1/installations/{id}/status
POST /api/v1/installations/{id}/start

# 激活管理API
POST /api/v1/activations
POST /api/v1/activations/{id}/roles/{roleId}/start
```

---

## 六、开发环境配置

### 6.1 ooder-skills 开发环境

```bash
# 克隆仓库
cd E:\github\ooder-skills

# 编译
mvn clean install -DskipTests

# 运行测试
mvn test
```

### 6.2 a2ui/ouc 开发环境

```bash
# 克隆仓库
cd E:\github\a2ui\ouc

# 编译
mvn clean install -DskipTests

# 运行前端
mvn spring-boot:run
```

### 6.3 ooder-sdk 开发环境

```bash
# 克隆仓库
cd E:\github\ooder-sdk

# 编译
mvn clean install -DskipTests

# 发布到本地
mvn install
```

---

## 七、任务分配汇总

| 工程 | 任务数 | 工时 | 主要职责 |
|------|--------|------|---------|
| **ooder-skills** | 20 | 63天 | Skills开发 + LLM文档 |
| **a2ui/ouc** | 6 | 24天 | 前端UI开发 |
| **a2ui/ooder-common** | 2 | 6天 | 基础设施 |
| **ooder-sdk** | 2 | 8天 | SDK扩展 |
| **ooder-Nexus** | 1 | 5天 | 监控集成 |
| **总计** | **31** | **106天** | - |

---

**文档状态**: 已完成  
**下一步**: 各工程按Phase顺序启动开发
