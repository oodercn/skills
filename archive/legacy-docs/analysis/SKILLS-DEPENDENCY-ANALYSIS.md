# Skills 依赖关系分析报告

## 一、依赖关系总览

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Skills 依赖关系图                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                        ┌─────────────┐                                      │
│                        │ skill-common│                                      │
│                        │  (基础服务)  │                                      │
│                        └──────┬──────┘                                      │
│                               │                                             │
│           ┌───────────────────┼───────────────────┐                        │
│           │                   │                   │                        │
│           ▼                   ▼                   ▼                        │
│    ┌─────────────┐     ┌─────────────┐     ┌─────────────┐                 │
│    │ skill-llm   │     │skill-protocol│    │skill-llm-chat│                │
│    │ (LLM服务)   │     │ (协议服务)   │     │ (LLM聊天)    │                │
│    └──────┬──────┘     └─────────────┘     └─────────────┘                 │
│           │                                                                 │
│           ├─────────────────────┐                                          │
│           │                     │                                          │
│           ▼                     ▼                                          │
│    ┌─────────────────┐   ┌─────────────────────┐                           │
│    │skill-scene-     │   │ skill-capability    │                           │
│    │  management     │◄──│ (能力管理)          │                           │
│    │ (场景管理)      │   │                     │                           │
│    └─────────────────┘   └─────────────────────┘                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 二、模块依赖详情

### 2.1 skill-common (基础服务)

**依赖**: 无

**被依赖**:
- skill-capability
- skill-scene-management
- skill-llm
- skill-llm-chat
- skill-protocol
- skill-management

**提供能力**:
- 用户管理服务
- 部门管理服务
- JSON存储服务
- SDK资源配置
- 菜单管理
- 认证服务

### 2.2 skill-llm (LLM服务)

**依赖**:
- skill-common (required)

**被依赖**:
- skill-scene-management (optional)
- skill-capability (optional)

**提供能力**:
- LLM对话调用
- 向量嵌入
- LLM监控
- LLM配置管理

### 2.3 skill-protocol (协议服务)

**依赖**: 无

**被依赖**: 可选依赖

**提供能力**:
- 协议解析 (JSON/XML/YAML/Protobuf)
- 协议适配
- 协议转换

### 2.4 skill-scene-management (场景管理)

**当前依赖**:
- skill-common (required)

**建议依赖**:
- skill-common (required)
- skill-llm (optional) - LLM配置功能

**被依赖**:
- skill-capability (required)

**提供能力**:
- 场景组生命周期管理
- 参与者管理
- 能力绑定
- 知识库绑定
- LLM配置 (SceneLlmController)
- 快照管理

### 2.5 skill-capability (能力管理)

**当前依赖**:
- skill-common (required)

**建议依赖**:
- skill-common (required)
- skill-scene-management (required) - 安装向导配置保存
- skill-llm (optional) - LLM配置功能

**提供能力**:
- 能力发现
- 能力注册
- 链路管理
- 密钥管理
- 能力统计

---

## 三、安装顺序

### 3.1 推荐安装顺序

```
1. skill-common        (基础服务，无依赖)
2. skill-protocol      (协议服务，无依赖)
3. skill-llm           (LLM服务，依赖skill-common)
4. skill-scene-management (场景管理，依赖skill-common)
5. skill-capability    (能力管理，依赖skill-common和skill-scene-management)
```

### 3.2 MVP最小安装集合

```
MVP内置:
├── skill-common
└── skill-hotplug-starter

首次安装:
├── skill-scene-management
└── skill-capability
```

---

## 四、skill.yaml 更新建议

### 4.1 skill-capability 更新

```yaml
dependencies:
  - id: skill-common
    version: ">=1.0.0"
    required: true
    description: "公共基础服务"
  - id: skill-scene-management
    version: ">=1.0.0"
    required: true
    description: "场景管理服务 - 提供LLM配置、知识库绑定等API"
  - id: skill-llm
    version: ">=1.0.0"
    required: false
    description: "LLM服务 - 提供LLM配置和调用能力"
```

### 4.2 skill-scene-management 更新

```yaml
dependencies:
  - id: skill-common
    version: ">=1.0.0"
    required: true
    description: "公共基础服务"
  - id: skill-llm
    version: ">=1.0.0"
    required: false
    description: "LLM服务 - 提供LLM配置和调用能力"
```

---

## 五、内置能力定义

### 5.1 系统级能力 (SYS)

| 能力代码 | 地址 | 模块 | 说明 |
|----------|------|------|------|
| SYS_COMMON | 0x01 | skill-common | 公共基础服务 |
| SYS_PROTOCOL | 0x03 | skill-protocol | 协议处理服务 |
| SYS_LLM | 0x12 | skill-llm | LLM服务 |
| SYS_SCENE_MGMT | 0x10 | skill-scene-management | 场景管理服务 |
| SYS_CAPABILITY | 0x11 | skill-capability | 能力服务 |

### 5.2 能力依赖矩阵

| 能力 | SYS_COMMON | SYS_PROTOCOL | SYS_LLM | SYS_SCENE_MGMT | SYS_CAPABILITY |
|------|:----------:|:------------:|:-------:|:--------------:|:--------------:|
| SYS_COMMON | - | ✗ | ✗ | ✗ | ✗ |
| SYS_PROTOCOL | ✗ | - | ✗ | ✗ | ✗ |
| SYS_LLM | ✓ | ✗ | - | ✗ | ✗ |
| SYS_SCENE_MGMT | ✓ | ✗ | ○ | - | ✗ |
| SYS_CAPABILITY | ✓ | ✗ | ○ | ✓ | - |

图例: ✓ 必需依赖, ○ 可选依赖, ✗ 无依赖

---

## 六、知识库依赖

### 6.1 知识库模块

| 模块 | 位置 | 说明 |
|------|------|------|
| skill-knowledge-base | capabilities/knowledge/ | 知识库管理 |
| skill-rag | capabilities/knowledge/ | RAG检索 |
| skill-vector-sqlite | capabilities/knowledge/ | 向量存储 |

### 6.2 知识库与场景绑定

```
场景组 (SceneGroup)
    ├── 知识库绑定 (SceneKnowledgeController)
    │   ├── GENERAL 层 - 通用知识库
    │   ├── PROFESSIONAL 层 - 专业知识库
    │   └── CUSTOM 层 - 自定义知识库
    └── RAG配置
        ├── topK
        ├── threshold
        └── crossLayerSearch
```

---

## 七、总结

### 7.1 核心依赖链

```
skill-capability
    └── skill-scene-management (required)
            └── skill-common (required)
    └── skill-llm (optional)
            └── skill-common (required)
```

### 7.2 安装建议

1. **MVP最小安装**: skill-common + skill-scene-management + skill-capability
2. **完整安装**: 添加 skill-llm + skill-protocol
3. **知识库扩展**: 添加 skill-knowledge-base + skill-rag

---

*分析时间: 2026-03-16*
*分析范围: skills/_system 所有模块*
