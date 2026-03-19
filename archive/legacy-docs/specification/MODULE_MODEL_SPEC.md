# Ooder Module 模块模型规范

## 一、模块定义

模块（Module）是 Ooder 平台的基本功能单元，包含完整的前端资源、后端 API 和业务逻辑。

## 二、模块结构

```
module/
├── module.json              # 模块元数据定义
├── frontend/                # 前端资源
│   ├── pages/              # HTML 页面
│   ├── js/                 # JavaScript
│   │   ├── index.js        # 模块入口
│   │   ├── service.js      # 服务层
│   │   └── utils.js        # 工具函数
│   └── css/                # 样式文件
│       ├── index.css       # 主样式
│       └── components/     # 组件样式
├── backend/                 # 后端资源
│   ├── controller/         # REST API 控制器
│   ├── service/            # 业务服务
│   ├── dto/                # 数据传输对象
│   └── config/             # 配置类
└── resources/               # 其他资源
    ├── templates/          # 模板文件
    └── schemas/            # 数据模型
```

## 三、模块元数据 (module.json)

```json
{
  "id": "capability-discovery",
  "name": "能力发现",
  "version": "2.3.1",
  "type": "scene",
  "category": "core",
  "description": "扫描、发现和安装新能力",
  "author": "ooder",
  "icon": "ri-compass-discover-line",
  
  "frontend": {
    "pages": ["capability-discovery.html"],
    "js": ["capability-discovery.js", "capability-service.js"],
    "css": ["capability-discovery.css"],
    "dependencies": ["nexus.js", "menu.js", "api-client.js"]
  },
  
  "backend": {
    "controllers": ["CapabilityDiscoveryController.java"],
    "services": ["CapabilityDiscoveryService.java"],
    "dtos": ["CapabilityDTO.java", "DiscoveryResultDTO.java"],
    "apis": [
      {
        "path": "/api/v1/capability/discover",
        "method": "POST",
        "description": "扫描发现能力"
      },
      {
        "path": "/api/v1/capability/install",
        "method": "POST",
        "description": "安装能力"
      }
    ]
  },
  
  "dependencies": {
    "modules": ["llm-config", "auth"],
    "skills": ["skill-common", "skill-capability"]
  },
  
  "menu": {
    "name": "发现能力",
    "icon": "ri-compass-discover-line",
    "url": "/console/pages/capability-discovery.html",
    "roles": ["installer", "admin"]
  }
}
```

## 四、模块类型

| 类型 | 说明 | 示例 |
|------|------|------|
| `core` | 核心模块，平台基础功能 | auth, menu, config |
| `scene` | 场景模块，业务场景应用 | capability-discovery, scene-management |
| `provider` | 能力服务模块 | llm-config, knowledge-base |
| `driver` | 驱动适配模块 | vfs-local, notification-driver |
| `tool` | 工具模块 | report, document-processor |

## 五、模块分类

| 分类 | 说明 | 归属 |
|------|------|------|
| `builtin` | 内置模块，MVP 核心功能 | MVP 工程 |
| `installable` | 可安装模块，通过 skills 安装 | skills 目录 |
| `external` | 外部模块，第三方扩展 | 外部仓库 |

## 六、模块生命周期

```
[发现] → [安装] → [配置] → [激活] → [运行] → [停用] → [卸载]
```

1. **发现**：扫描 skills 目录，识别可用模块
2. **安装**：复制资源到 MVP，注册 API
3. **配置**：设置模块参数
4. **激活**：启用模块功能
5. **运行**：模块正常工作
6. **停用**：暂停模块功能
7. **卸载**：移除模块资源

## 七、模块与 Skills 的关系

```
┌─────────────────────────────────────────────────────────────┐
│                        MVP 工程                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                    内置模块 (builtin)                 │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐ │   │
│  │  │   auth   │ │   menu   │ │  config  │ │dashboard│ │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └────────┘ │   │
│  └─────────────────────────────────────────────────────┘   │
│                           ↑ 安装                            │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                   已安装模块 (installed)              │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐            │   │
│  │  │capability│ │   llm    │ │   user   │            │   │
│  │  │-discovery│ │  -config │ │management│            │   │
│  │  └──────────┘ └──────────┘ └──────────┘            │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                           ↑ 安装来源
┌─────────────────────────────────────────────────────────────┐
│                      skills 目录                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │skill-scene  │  │skill-common │  │skill-capabil│        │
│  │ ┌─────────┐ │  │ ┌─────────┐ │  │ ┌─────────┐ │        │
│  │ │capability│ │  │   auth   │ │  │capability│ │        │
│  │ │-discovery│ │  │   org    │ │  │  service │ │        │
│  │ │   ...    │ │  │   ...    │ │  │   ...    │ │        │
│  │ └─────────┘ │  └─────────┘ │  └─────────┘ │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
```

## 八、模块 API 规范

### 8.1 模块注册 API

```java
POST /api/v1/module/register
{
  "moduleId": "capability-discovery",
  "version": "2.3.1",
  "resources": {
    "frontend": {...},
    "backend": {...}
  }
}
```

### 8.2 模块发现 API

```java
GET /api/v1/module/discover?source=local|git|market
Response: {
  "status": "success",
  "data": [
    {
      "id": "capability-discovery",
      "name": "能力发现",
      "type": "scene",
      "installed": false
    }
  ]
}
```

### 8.3 模块安装 API

```java
POST /api/v1/module/install
{
  "moduleId": "capability-discovery",
  "source": "skills/skill-scene",
  "config": {
    "role": "installer",
    "participants": []
  }
}
```

## 九、模块依赖管理

### 9.1 依赖声明

```json
{
  "dependencies": {
    "modules": [
      {"id": "auth", "version": ">=2.3.0", "required": true},
      {"id": "llm-config", "version": ">=2.3.0", "required": false}
    ],
    "skills": [
      {"id": "skill-common", "version": ">=2.3.0"},
      {"id": "skill-capability", "version": ">=2.3.0"}
    ]
  }
}
```

### 9.2 依赖检查

安装模块前，系统会检查依赖是否满足：
- 必需依赖缺失 → 安装失败
- 可选依赖缺失 → 警告但继续安装
- 版本不兼容 → 提示升级或降级

## 十、模块配置继承

模块配置遵循三级继承体系：

```
系统级配置 (system.json)
    ↓ 覆盖
技能级配置 (skill-config.json)
    ↓ 覆盖
场景级配置 (scene-config.json)
```

配置优先级：场景级 > 技能级 > 系统级
