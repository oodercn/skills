# Skills 页面与 API 知识图谱 v2.3.1

## 一、skill-capability 知识图谱

### 1.1 页面层级结构

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  skill-capability 页面层级                                                       │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  【一级页面 - 菜单入口】                                                          │
│  ├── capability-management.html      # 能力列表                                 │
│  ├── capability-discovery.html       # 能力发现                                 │
│  ├── capability-activation.html      # 能力激活                                 │
│  ├── capability-binding.html         # 能力绑定                                 │
│  └── my-capabilities.html            # 我的能力                                 │
│                                                                                 │
│  【二级页面 - 详情/子功能】                                                        │
│  └── capability-detail.html          # 能力详情（从能力列表进入）                  │
│                                                                                 │
│  【入口页面】                                                                     │
│  └── index.html                      # 重定向到 capability-management.html      │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 API 端点

| API | 方法 | 说明 | 对应页面 |
|-----|------|------|---------|
| `/api/v1/capabilities` | GET | 能力列表 | capability-management.html |
| `/api/v1/capabilities/{id}` | GET | 能力详情 | capability-detail.html |
| `/api/v1/capabilities/types` | GET | 能力类型 | capability-management.html |
| `/api/v1/capabilities/stats` | GET | 能力统计 | capability-management.html |
| `/api/v1/discovery/local` | POST | 本地发现 | capability-discovery.html |
| `/api/v1/discovery/github` | POST | GitHub发现 | capability-discovery.html |
| `/api/v1/discovery/gitee` | POST | Gitee发现 | capability-discovery.html |
| `/api/v1/activations/{id}/process` | GET | 激活流程 | capability-activation.html |
| `/api/v1/activations/{id}/start` | POST | 开始激活 | capability-activation.html |
| `/api/v1/activations/{id}/activate` | POST | 确认激活 | capability-activation.html |

### 1.3 页面关系图

```
capability-management.html (一级)
    │
    ├── [点击能力] ──→ capability-detail.html (二级)
    │                      │
    │                      └── [激活] ──→ capability-activation.html (一级)
    │
    ├── [发现更多] ──→ capability-discovery.html (一级)
    │
    └── [我的能力] ──→ my-capabilities.html (一级)

capability-discovery.html (一级)
    │
    └── [选择能力激活] ──→ capability-activation.html (一级)

capability-activation.html (一级)
    │
    └── [绑定到场景组] ──→ capability-binding.html (一级)

my-capabilities.html (一级)
    │
    └── [查看详情] ──→ capability-detail.html (二级)
```

---

## 二、skill-scene-management 知识图谱

### 2.1 页面层级结构

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  skill-scene-management 页面层级                                                 │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  【一级页面 - 菜单入口】                                                          │
│  ├── scene-management.html           # 场景列表                                 │
│  ├── scene-group-management.html     # 场景组管理                               │
│  ├── my-scenes.html                  # 我的场景                                 │
│  ├── knowledge-base.html             # 知识库                                   │
│  ├── llm-config.html                 # LLM配置                                  │
│  └── template-management.html        # 模板管理                                 │
│                                                                                 │
│  【二级页面 - 详情/子功能】                                                        │
│  └── scene/scene-group.html          # 场景组详情（从场景组管理进入）              │
│                                                                                 │
│  【入口页面】                                                                     │
│  └── index.html                      # 重定向到 scene-management.html           │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 API 端点

| API | 方法 | 说明 | 对应页面 |
|-----|------|------|---------|
| `/api/v1/scenes` | GET | 场景列表 | scene-management.html |
| `/api/v1/scenes/{id}` | GET | 场景详情 | scene-management.html |
| `/api/v1/scenes/{id}/start` | POST | 启动场景 | scene-management.html |
| `/api/v1/scenes/{id}/stop` | POST | 停止场景 | scene-management.html |
| `/api/v1/scene-groups` | GET/POST | 场景组CRUD | scene-group-management.html |
| `/api/v1/scene-groups/{id}` | GET/PUT/DELETE | 场景组详情 | scene/scene-group.html |
| `/api/v1/knowledge-bases` | GET/POST | 知识库CRUD | knowledge-base.html |
| `/api/v1/knowledge-bases/{id}` | GET/PUT/DELETE | 知识库详情 | knowledge-base.html |
| `/api/v1/llm/providers` | GET | LLM提供商列表 | llm-config.html |
| `/api/v1/llm/config` | GET/PUT | LLM配置 | llm-config.html |
| `/api/v1/llm/test` | POST | 测试LLM连接 | llm-config.html |

### 2.3 页面关系图

```
scene-management.html (一级)
    │
    ├── [创建场景组] ──→ scene-group-management.html (一级)
    │
    ├── [查看场景组] ──→ scene/scene-group.html (二级)
    │
    └── [我的场景] ──→ my-scenes.html (一级)

scene-group-management.html (一级)
    │
    ├── [查看详情] ──→ scene/scene-group.html (二级)
    │
    ├── [配置LLM] ──→ llm-config.html (一级)
    │
    └── [配置知识库] ──→ knowledge-base.html (一级)

scene/scene-group.html (二级)
    │
    ├── [配置LLM] ──→ llm-config.html (一级)
    │
    ├── [配置知识库] ──→ knowledge-base.html (一级)
    │
    └── [绑定能力] ──→ capability-binding.html (skill-capability)

my-scenes.html (一级)
    │
    └── [查看详情] ──→ scene/scene-group.html (二级)

knowledge-base.html (一级)
    │
    └── [关联场景组] ──→ scene-group-management.html (一级)

llm-config.html (一级)
    │
    └── [测试连接] ──→ 内部API调用

template-management.html (一级)
    │
    └── [使用模板创建] ──→ scene-group-management.html (一级)
```

---

## 三、跨技能页面关系

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  跨技能页面关系                                                                   │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  scene/scene-group.html (skill-scene-management)                                │
│      │                                                                          │
│      └── [绑定能力] ──→ capability-binding.html (skill-capability)              │
│                                                                                 │
│  capability-activation.html (skill-capability)                                  │
│      │                                                                          │
│      └── [绑定到场景组] ──→ scene-group-management.html (skill-scene-management) │
│                                                                                 │
│  capability-binding.html (skill-capability)                                     │
│      │                                                                          │
│      └── [选择场景组] ──→ scene-group-management.html (skill-scene-management)   │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、用户故事闭环分析

### 4.1 能力管理闭环

| 用户故事 | 入口页面 | 流程页面 | 完成页面 | 闭环状态 |
|---------|---------|---------|---------|:--------:|
| 发现能力 | capability-discovery.html | - | capability-detail.html | ✅ |
| 激活能力 | capability-activation.html | - | my-capabilities.html | ✅ |
| 查看能力 | capability-management.html | capability-detail.html | - | ✅ |
| 绑定能力 | capability-binding.html | scene-group-management.html | - | ✅ |
| 我的能力 | my-capabilities.html | capability-detail.html | - | ✅ |

### 4.2 场景管理闭环

| 用户故事 | 入口页面 | 流程页面 | 完成页面 | 闭环状态 |
|---------|---------|---------|---------|:--------:|
| 发现场景 | scene-management.html | scene/scene-group.html | - | ✅ |
| 创建场景组 | scene-group-management.html | scene/scene-group.html | my-scenes.html | ✅ |
| 配置LLM | llm-config.html | - | scene-group-management.html | ✅ |
| 配置知识库 | knowledge-base.html | - | scene-group-management.html | ✅ |
| 我的场景 | my-scenes.html | scene/scene-group.html | - | ✅ |
| 使用模板 | template-management.html | scene-group-management.html | - | ✅ |

---

## 五、缺失页面分析

### 5.1 已存在页面

**skill-capability (8个)**
- index.html
- capability-management.html ✅
- capability-discovery.html ✅
- capability-activation.html ✅
- capability-binding.html ✅
- my-capabilities.html ✅
- capability-detail.html ✅
- scene-capabilities.html ✅

**skill-scene-management (8个)**
- index.html
- scene-management.html ✅
- scene-group-management.html ✅
- my-scenes.html ✅
- knowledge-base.html ✅
- llm-config.html ✅
- template-management.html ✅
- scene/scene-group.html ✅

### 5.2 可能缺失的页面

| 页面 | 说明 | 优先级 |
|------|------|--------|
| scene-detail.html | 场景详情页（独立于场景组） | P3 |
| knowledge-detail.html | 知识库详情页 | P3 |
| llm-provider-detail.html | LLM提供商详情页 | P3 |

---

## 六、菜单配置建议

### 6.1 skill-capability 菜单

```json
{
    "menu": [
        {
            "id": "capability-management",
            "name": "能力管理",
            "icon": "ri-puzzle-line",
            "children": [
                {
                    "id": "capability-list",
                    "name": "能力列表",
                    "icon": "ri-list-check",
                    "url": "/console/pages/capability-management.html"
                },
                {
                    "id": "capability-discovery",
                    "name": "能力发现",
                    "icon": "ri-compass-discover-line",
                    "url": "/console/pages/capability-discovery.html"
                },
                {
                    "id": "capability-activation",
                    "name": "能力激活",
                    "icon": "ri-flashlight-line",
                    "url": "/console/pages/capability-activation.html"
                },
                {
                    "id": "capability-binding",
                    "name": "能力绑定",
                    "icon": "ri-link",
                    "url": "/console/pages/capability-binding.html"
                },
                {
                    "id": "my-capabilities",
                    "name": "我的能力",
                    "icon": "ri-apps-line",
                    "url": "/console/pages/my-capabilities.html"
                }
            ]
        }
    ]
}
```

### 6.2 skill-scene-management 菜单

```json
{
    "menu": [
        {
            "id": "scene-management",
            "name": "场景管理",
            "icon": "ri-folder-line",
            "children": [
                {
                    "id": "scene-list",
                    "name": "场景列表",
                    "icon": "ri-folder-line",
                    "url": "/console/pages/scene-management.html"
                },
                {
                    "id": "scene-groups",
                    "name": "场景组",
                    "icon": "ri-team-line",
                    "url": "/console/pages/scene-group-management.html"
                },
                {
                    "id": "my-scenes",
                    "name": "我的场景",
                    "icon": "ri-user-star-line",
                    "url": "/console/pages/my-scenes.html"
                },
                {
                    "id": "templates",
                    "name": "模板管理",
                    "icon": "ri-file-copy-line",
                    "url": "/console/pages/template-management.html"
                },
                {
                    "id": "knowledge-base",
                    "name": "知识库",
                    "icon": "ri-book-2-line",
                    "url": "/console/pages/knowledge-base.html"
                },
                {
                    "id": "llm-config",
                    "name": "LLM配置",
                    "icon": "ri-robot-2-line",
                    "url": "/console/pages/llm-config.html"
                }
            ]
        }
    ]
}
```
