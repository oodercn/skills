# Ooder Skills v2.3.1 规范文档

**版本**: 2.3.1  
**更新日期**: 2026-03-18  
**状态**: 正式发布

---

## 一、概述

本文档整合了 Ooder Skills v2.3.1 的所有核心规范，包括：
- 分类体系
- Skill 配置规范
- skill-index 拆分方案
- 加载机制

---

## 二、分类体系

### 2.1 分类定义 (17个)

| Order | ID | 名称 | 英文名 | 图标 | sceneDriver |
|:-----:|----|------|--------|:----:|:-----------:|
| 1 | `org` | 组织服务 | Organization | users | org |
| 2 | `vfs` | 存储服务 | Storage | database | vfs |
| 3 | `ui` | UI生成 | UI Generation | palette | null |
| 4 | `msg` | 消息通讯 | Messaging | message | msg |
| 5 | `sys` | 系统管理 | System | settings | sys |
| 6 | `payment` | 支付服务 | Payment | credit-card | payment |
| 7 | `media` | 媒体发布 | Media Publishing | edit | media |
| 8 | `util` | 工具服务 | Utility | tool | null |
| 9 | `nexus-ui` | Nexus界面 | Nexus UI | layout | null |
| 10 | `llm` | LLM服务 | LLM Service | robot | null |
| 11 | `knowledge` | 知识服务 | Knowledge Service | book | null |
| 12 | `iot` | 物联网服务 | IoT Service | cpu | null |
| 13 | `collaboration` | 协作服务 | Collaboration Service | users | null |
| 14 | `business` | 业务服务 | Business Service | briefcase | null |
| 15 | `infrastructure` | 基础设施 | Infrastructure | server | null |
| 16 | `scheduler` | 调度服务 | Scheduler Service | clock | null |
| 17 | `messaging` | 消息服务 | Messaging Service | send | msg |

### 2.2 分类配置格式

```yaml
categories:
  - id: org                    # 分类ID (必填，小写)
    name: 组织服务              # 中文名称 (必填)
    nameEn: Organization       # 英文名称 (必填)
    description: 企业组织架构、用户认证相关服务  # 描述 (必填)
    icon: users                # 图标名称 (必填，使用 Remix Icon)
    order: 1                   # 排序权重 (必填)
    sceneDriver: org           # 关联的场景驱动 (可选，null 表示无)
```

---

## 三、Skill 配置规范

### 3.1 必填字段

| 字段 | 层级 | 说明 |
|------|------|------|
| `metadata.id` | L1 | Skill 唯一标识，格式: `skill-{name}` |
| `metadata.name` | L1 | 中文名称 |
| `metadata.version` | L1 | 版本号，格式: `x.y.z` |
| `metadata.description` | L1 | 功能描述 |
| `spec.type` | L2 | 类型: system-skill / service-skill / scene-skill |
| `spec.runtime.language` | L2 | 开发语言 |
| `spec.capabilities` | L2 | 能力列表 (至少1个) |
| `spec.routes` | L2 | 路由列表 (至少1个) |

### 3.2 完整配置模板

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-example
  name: 示例技能
  version: 1.0.0
  description: 示例技能描述
  author: Ooder Team
  license: Apache-2.0

spec:
  type: service-skill
  skillForm: SYSTEM
  visibility: internal
  businessCategory: INFRASTRUCTURE
  ownership: system
  
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
  
  dependencies:
    - skill-common
  
  capabilities:
    - id: example-capability
      name: 示例能力
      description: 能力描述
      category: core
      type: ATOMIC
      address: "0x02"
      addressCode: sys.example
  
  routes:
    - path: /api/v1/example
      method: GET
      controller: net.ooder.example.Controller
      methodName: getExample
      capability: example-capability
      produces: application/json
  
  services:
    - name: exampleService
      interface: net.ooder.example.Service
      implementation: net.ooder.example.ServiceImpl
      singleton: true
  
  ui:
    type: html
    entry: index.html
    staticResources:
      - console/
  
  menu:
    - id: example-menu
      name: 示例菜单
      url: example.html
      icon: ri-example-line
      roles:
        - admin
      sort: 1
```

---

## 四、skill-index 拆分方案

### 4.1 目录结构

```
skill-index/
├── index.yaml                 # 主索引文件
├── categories.yaml            # 分类定义 (17个)
├── scene-drivers.yaml         # 场景驱动 (6个)
│
├── skills/                    # 技能文件 (按 capabilityCategory 分类)
│   ├── sys.yaml              # 系统管理
│   ├── llm.yaml              # LLM服务
│   ├── knowledge.yaml        # 知识服务
│   ├── org.yaml              # 组织服务
│   ├── vfs.yaml              # 存储服务
│   ├── msg.yaml              # 消息通讯
│   ├── payment.yaml          # 支付服务
│   ├── media.yaml            # 媒体发布
│   ├── util.yaml             # 工具服务
│   ├── nexus-ui.yaml         # Nexus界面
│   ├── ui.yaml               # UI生成
│   ├── iot.yaml              # 物联网
│   ├── collaboration.yaml    # 协作服务
│   ├── business.yaml         # 业务服务
│   ├── infrastructure.yaml   # 基础设施
│   ├── scheduler.yaml        # 调度服务
│   └── messaging.yaml        # 消息服务
│
└── scenes/                    # 场景文件 (按 capabilityCategory 分类)
    ├── sys-scenes.yaml
    ├── collaboration-scenes.yaml
    ├── business-scenes.yaml
    └── ...
```

### 4.2 主索引文件 (index.yaml)

```yaml
apiVersion: ooder.io/v1
kind: SkillIndex

metadata:
  name: ooder-skills
  version: "2.3.1"
  description: Ooder Skills Repository
  author: Ooder Team
  license: Apache-2.0

spec:
  includes:
    - categories.yaml
    - scene-drivers.yaml
    - skills/*.yaml
    - scenes/*.yaml
  
  statistics:
    totalSkills: 83
    totalScenes: 50
    totalCategories: 17
    totalSceneDrivers: 6
```

### 4.3 Skills 文件格式

```yaml
apiVersion: ooder.io/v1
kind: Skills
metadata:
  name: llm-skills
  category: llm
  count: 10

skills:
  - id: skill-llm-chat
    name: LLM智能对话场景能力
    version: "2.3.1"
    capabilityCategory: llm
    subCategory: chat
    tags:
      - llm
      - ai
    description: LLM智能对话场景能力
    sceneId: llm-chat
    path: skills/_system/skill-llm-chat
    dependencies:
      - id: skill-common
        version: ">=2.3.1"
        required: true
    capabilities:
      - llm-chat
      - llm-stream
```

### 4.4 Scenes 文件格式

```yaml
apiVersion: ooder.io/v1
kind: Scenes
metadata:
  name: sys-scenes
  category: sys
  count: 23

scenes:
  - sceneId: llm-chat
    name: LLM智能对话场景
    description: 提供智能对话能力
    version: "1.0.0"
    capabilityCategory: llm
    requiredCapabilities:
      - llm-chat
      - llm-stream
    maxMembers: 100
```

---

## 五、加载机制

### 5.1 SkillIndexLoader 实现

```java
@Service
public class SkillIndexLoader {

    @Value("${ooder.skill.index-dir:skill-index}")
    private String skillIndexDir;

    private List<Map<String, Object>> skills = new ArrayList<>();
    private List<Map<String, Object>> scenes = new ArrayList<>();
    private List<Map<String, Object>> categories = new ArrayList<>();
    private List<Map<String, Object>> sceneDrivers = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadSkillIndex();
    }

    private void loadSkillIndex() {
        File indexDir = findSkillIndexDir();
        if (indexDir != null) {
            loadFromDirectory(indexDir, yaml);
        }
    }

    private File findSkillIndexDir() {
        String[] searchPaths = {
            skillIndexDir,
            "../" + skillIndexDir,
            "../../" + skillIndexDir,
            "e:/github/ooder-skills/skill-index"
        };
        // ... 查找逻辑
    }

    private void loadFromDirectory(File indexDir, Yaml yaml) {
        // 加载 categories.yaml
        // 加载 scene-drivers.yaml
        // 加载 skills/*.yaml
        // 加载 scenes/*.yaml
    }
}
```

### 5.2 搜索路径

| 优先级 | 路径 | 说明 |
|:------:|------|------|
| 1 | `skill-index` | 配置路径 |
| 2 | `../skill-index` | 上级目录 |
| 3 | `../../skill-index` | 上上级目录 |
| 4 | `e:/github/ooder-skills/skill-index` | 绝对路径 |

---

## 六、场景驱动

### 6.1 内置场景驱动 (6个)

| ID | 名称 | 内置 | 位置 |
|----|------|:----:|------|
| `org` | Organization Driver | ✅ | scene-engine |
| `vfs` | Virtual File System Driver | ✅ | scene-engine |
| `msg` | Messaging Driver | ✅ | scene-engine |
| `payment` | Payment Driver | ❌ | ooder-skills |
| `media` | Media Publish Driver | ❌ | ooder-skills |
| `sys` | System Driver | ✅ | scene-engine |

### 6.2 场景驱动配置

```yaml
sceneDrivers:
  - id: org
    name: Organization Driver
    description: 组织管理场景驱动
    capabilities:
      - user-auth
      - user-manage
      - org-manage
      - role-manage
    builtIn: true
    location: scene-engine
```

---

## 七、统计信息

| 维度 | 数量 |
|------|:----:|
| Skills | 83 |
| Scenes | 50 |
| Categories | 17 |
| SceneDrivers | 6 |
| 内置 Skills | 3 |

### 7.1 内置 Skills

| Skill ID | 名称 | builtInPath |
|----------|------|-------------|
| skill-common | 公共基础服务 | - |
| skill-capability | 能力管理服务 | - |
| skill-scene-management | 场景管理服务 | net.ooder.mvp.skill.scene |

---

## 八、变更历史

| 版本 | 日期 | 变更内容 |
|------|------|----------|
| 2.3.1 | 2026-03-18 | skill-index 拆分，移除单文件支持 |
| 2.3.0 | 2026-03-01 | 新增 llm/knowledge 等8个分类 |
| 2.2.0 | 2026-02-15 | 分类体系重构 |

---

## 九、相关文档

### 9.1 规范文档
- [分类规范](./v2.3.1/volume-02-classification/SCENE_SKILL_CLASSIFICATION.md)
- [能力地址空间](./v2.3.1/volume-01-specification/CAPABILITY_ADDRESS_SPACE.md)
- [开发指南](./v2.3.1/volume-05-development/CAPABILITY_MODULE_GUIDE.md)

### 9.2 归档文档
- [分析报告](./v2.3.1/archive/analysis/)
- [协作文档](./v2.3.1/archive/collaboration/)
- [执行计划](./v2.3.1/archive/execution/)

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-18
