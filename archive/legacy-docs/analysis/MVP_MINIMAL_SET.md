# MVP 最小集合分析

## 一、设计原则

**核心理念**：MVP 是一个最小运行时容器，所有功能模块都通过 skills 安装

**优势**：
1. 升级方便 - 模块独立升级，不影响其他模块
2. 依赖解耦 - 模块按需安装，减少不必要的依赖
3. 灵活扩展 - 根据需求选择安装哪些模块
4. 版本管理 - 每个模块可以独立管理版本

## 二、MVP 运行时核心（必须内置）

这些是 MVP 运行的基础设施，无法通过 skills 安装：

| 组件 | 说明 | 必须原因 |
|------|------|----------|
| Spring Boot | 应用框架 | 运行时容器 |
| 模块加载器 | ModuleLoader | 加载其他模块的前提 |
| 配置系统 | ConfigSystem | 三级配置继承 |
| 安全框架 | SecurityFilter | 认证授权基础 |
| 静态资源服务 | StaticResources | 提供前端资源访问 |
| API 网关 | ApiGateway | 统一 API 入口 |

### 运行时核心代码量

```
mvp/
├── src/main/java/net/ooder/mvp/
│   ├── MvpCoreApplication.java      # 启动类 (~50行)
│   ├── config/
│   │   ├── MvcConfig.java          # MVC配置 (~100行)
│   │   └── SecurityConfig.java     # 安全配置 (~150行)
│   ├── loader/
│   │   └── ModuleLoader.java       # 模块加载器 (~200行)
│   └── filter/
│       └── AuthFilter.java         # 认证过滤器 (~100行)
│
└── src/main/resources/
    ├── application.yml              # 应用配置
    └── static/
        └── loader/                  # 模块加载器前端
            └── bootstrap.js         # 启动脚本
```

**预计代码量**：约 600 行 Java 代码

## 三、预装模块（首次启动自动安装）

这些模块是系统启动必需的，在首次启动时自动从 skills 安装：

| 模块 | 来源 Skill | 说明 | 预装原因 |
|------|-----------|------|----------|
| **auth** | skill-common | 认证授权 | 没有认证无法使用系统 |
| **menu** | skill-common | 菜单系统 | 导航必需 |
| **login** | skill-common | 登录页面 | 入口页面 |

### 预装流程

```
首次启动
    │
    ▼
检查模块注册表是否为空
    │
    ├── 为空 → 执行预装
    │         │
    │         ▼
    │     扫描 skills/ 目录
    │         │
    │         ▼
    │     安装 auth, menu, login
    │         │
    │         ▼
    │     初始化管理员账户
    │         │
    │         ▼
    │     跳转到登录页
    │
    └── 不为空 → 正常启动
```

## 四、可选功能模块（用户按需安装）

这些模块通过 capability-discovery 页面安装：

### 4.1 核心功能模块

| 模块 | 来源 Skill | 功能 | 依赖 |
|------|-----------|------|------|
| **capability-discovery** | skill-scene | 发现和安装能力 | auth, menu |
| **scene-management** | skill-scene | 场景管理 | auth, config |
| **llm-config** | skill-scene | LLM配置 | auth, config |
| **config-system** | skill-scene | 系统配置 | auth |
| **user-management** | skill-common | 用户管理 | auth, org |
| **org-management** | skill-common | 组织管理 | auth |

### 4.2 扩展功能模块

| 模块 | 来源 Skill | 功能 | 依赖 |
|------|-----------|------|------|
| **template-management** | skill-scene | 模板管理 | scene-management |
| **knowledge-base** | skill-knowledge | 知识库 | llm-config |
| **audit-logs** | skill-common | 审计日志 | auth |
| **my-workspace** | skill-scene | 个人工作台 | auth |

### 4.3 驱动模块

| 模块 | 来源 Skill | 功能 | 依赖 |
|------|-----------|------|------|
| **llm-deepseek** | skill-llm-deepseek | DeepSeek驱动 | llm-config |
| **llm-openai** | skill-llm-openai | OpenAI驱动 | llm-config |
| **vfs-local** | skill-vfs-local | 本地文件系统 | - |
| **notification-email** | skill-notification | 邮件通知 | - |

## 五、模块安装方式对比

### 5.1 传统方式（内置）

```
MVP 工程
├── 内置 auth 模块
├── 内置 menu 模块
├── 内置 config 模块
├── 内置 user-management 模块
└── ...

问题：
- 升级需要重新发布整个 MVP
- 模块间依赖复杂
- 不需要的模块也会被安装
```

### 5.2 Skills 方式（推荐）

```
MVP 工程（最小运行时）
├── 运行时核心
└── 模块加载器

skills/（模块仓库）
├── skill-common/
│   └── modules/
│       ├── auth/
│       ├── menu/
│       ├── login/
│       └── user-management/
├── skill-scene/
│   └── modules/
│       ├── capability-discovery/
│       ├── scene-management/
│       └── llm-config/
└── ...

优势：
- 模块独立升级
- 按需安装
- 依赖清晰
- 版本可控
```

## 六、MVP 启动流程

### 6.1 首次启动流程

```
┌─────────────────────────────────────────────────────────────────┐
│                        首次启动流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 启动 Spring Boot                                            │
│         │                                                       │
│         ▼                                                       │
│  2. 初始化模块加载器                                             │
│         │                                                       │
│         ▼                                                       │
│  3. 检查模块注册表                                               │
│         │                                                       │
│         ├── 为空 ──────────────────────────────────────────────┐│
│         │                                                     ││
│         │                     ▼                               ││
│         │              4. 扫描 skills/ 目录                    ││
│         │                     │                               ││
│         │                     ▼                               ││
│         │              5. 安装预装模块                          ││
│         │                 - auth                              ││
│         │                 - menu                              ││
│         │                 - login                             ││
│         │                     │                               ││
│         │                     ▼                               ││
│         │              6. 初始化管理员账户                      ││
│         │                     │                               ││
│         │                     ▼                               ││
│         │              7. 跳转到登录页                          ││
│         │                                                     ││
│         └── 不为空 ────────────────────────────────────────────┘│
│                     │                                           │
│                     ▼                                           │
│              8. 加载已安装模块                                   │
│                     │                                           │
│                     ▼                                           │
│              9. 启动完成                                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 6.2 正常启动流程

```
┌─────────────────────────────────────────────────────────────────┐
│                        正常启动流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 启动 Spring Boot                                            │
│         │                                                       │
│         ▼                                                       │
│  2. 初始化模块加载器                                             │
│         │                                                       │
│         ▼                                                       │
│  3. 加载模块注册表                                               │
│         │                                                       │
│         ▼                                                       │
│  4. 加载已安装模块                                               │
│         │                                                       │
│         ├── 加载 auth 模块                                      │
│         ├── 加载 menu 模块                                      │
│         ├── 加载 capability-discovery 模块                      │
│         └── ...                                                 │
│         │                                                       │
│         ▼                                                       │
│  5. 注册 API 路由                                                │
│         │                                                       │
│         ▼                                                       │
│  6. 注册菜单项                                                   │
│         │                                                       │
│         ▼                                                       │
│  7. 启动完成，显示登录页                                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 七、模块注册表设计

### 7.1 注册表结构

```json
{
  "registry": {
    "version": "1.0",
    "mvpVersion": "2.3.1",
    "initialized": true,
    "initializedAt": "2024-03-13T10:00:00Z",
    
    "modules": {
      "auth": {
        "version": "2.3.1",
        "source": "skill-common",
        "status": "active",
        "installedAt": "2024-03-13T10:00:00Z",
        "dependencies": []
      },
      "menu": {
        "version": "2.3.1",
        "source": "skill-common",
        "status": "active",
        "installedAt": "2024-03-13T10:00:00Z",
        "dependencies": ["auth"]
      },
      "capability-discovery": {
        "version": "2.3.1",
        "source": "skill-scene",
        "status": "active",
        "installedAt": "2024-03-13T11:00:00Z",
        "dependencies": ["auth", "menu"]
      }
    }
  }
}
```

### 7.2 注册表存储位置

```
# 方式一：文件存储
mvp/data/registry.json

# 方式二：数据库存储（推荐）
表：module_registry
字段：module_id, version, source, status, installed_at, dependencies
```

## 八、MVP 最小集合总结

### 8.1 必须内置（约 600 行代码）

- Spring Boot 框架
- 模块加载器
- 配置系统
- 安全过滤器
- 静态资源服务

### 8.2 预装模块（首次启动自动安装）

- auth（认证授权）
- menu（菜单系统）
- login（登录页面）

### 8.3 可选模块（用户按需安装）

| 分类 | 模块 |
|------|------|
| 核心 | capability-discovery, scene-management, llm-config, config-system |
| 用户 | user-management, org-management |
| 扩展 | template-management, knowledge-base, audit-logs |
| 驱动 | llm-deepseek, llm-openai, vfs-local |

## 九、下一步行动

1. **实现模块加载器** - ModuleLoader.java
2. **实现模块注册表** - ModuleRegistry.java
3. **实现预装流程** - 首次启动自动安装
4. **迁移预装模块** - 将 auth, menu, login 迁移到 skill-common
5. **实现安装 API** - /api/v1/module/install

---

**结论**：MVP 最小集合只需要一个运行时核心（约 600 行代码），所有功能模块都通过 skills 安装，包括预装模块。
