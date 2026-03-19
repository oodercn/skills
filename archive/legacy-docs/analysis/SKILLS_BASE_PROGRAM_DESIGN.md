# Skills 底座程序设计

## 一、Skills 运行的最小需求

### 1.1 必备依赖（Maven）

```xml
<!-- 1. Spring Boot 基础 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- 2. Agent SDK API - 技能定义接口 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-api</artifactId>
    <version>2.3</version>
</dependency>

<!-- 3. Agent SDK Core - 技能执行核心 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3</version>
</dependency>

<!-- 4. Skills Framework - 技能框架 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skills-framework</artifactId>
    <version>2.3</version>
</dependency>

<!-- 5. 热插拔支持 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-hotplug-starter</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 1.2 必备资源（前端）

| 资源 | 说明 | 来源 |
|------|------|------|
| nexus.css | 核心框架样式 | skill-scene 提供 |
| nexus.js | 核心JS（主题、侧边栏） | skill-scene 提供 |
| remixicon/ | 图标字体 | skill-scene 提供 |
| menu.js | 动态菜单 | skill-scene 提供 |

### 1.3 必备服务（后端）

| 服务 | 说明 | 来源 |
|------|------|------|
| PluginManager | 热插拔管理 | skill-hotplug-starter |
| SkillManager | 技能管理 | skill-management |
| SkillLifecycleManager | 生命周期管理 | skill-management |

---

## 二、依赖关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Skills 底座程序 (MVP)                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      运行时层 (Runtime)                              │   │
│  │  ┌───────────────────┐ ┌───────────────────┐ ┌───────────────────┐  │   │
│  │  │   Spring Boot     │ │   Agent SDK API   │ │  Agent SDK Core   │  │   │
│  │  │   (Web容器)       │ │   (技能接口)       │ │   (技能执行)       │  │   │
│  │  └───────────────────┘ └───────────────────┘ └───────────────────┘  │   │
│  │  ┌───────────────────┐ ┌───────────────────┐                        │   │
│  │  │  Skills Framework │ │  Hot Plug Starter │                        │   │
│  │  │   (技能框架)       │ │   (热插拔)         │                        │   │
│  │  └───────────────────┘ └───────────────────┘                        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    │ 提供                                   │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      引导安装层 (Bootstrap)                          │   │
│  │  ┌─────────────────────────────────────────────────────────────┐    │   │
│  │  │  setup.html + SetupController                               │    │   │
│  │  │  - 检测系统状态                                              │    │   │
│  │  │  - 引导安装 skill-scene                                      │    │   │
│  │  │  - 初始化管理员账户                                          │    │   │
│  │  └─────────────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    │ 安装后提供                             │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      功能层 (Features) - 来自 skill-scene            │   │
│  │  ┌───────────────────┐ ┌───────────────────┐ ┌───────────────────┐  │   │
│  │  │ capability-       │ │ scene-            │ │ llm-config        │  │   │
│  │  │ discovery         │ │ management        │ │                   │  │   │
│  │  │ (发现安装)         │ │ (场景管理)         │ │ (LLM配置)         │  │   │
│  │  └───────────────────┘ └───────────────────┘ └───────────────────┘  │   │
│  │  ┌───────────────────┐ ┌───────────────────┐ ┌───────────────────┐  │   │
│  │  │ nexus.css         │ │ nexus.js          │ │ menu.js           │  │   │
│  │  │ (核心样式)         │ │ (核心JS)           │ │ (动态菜单)         │  │   │
│  │  └───────────────────┘ └───────────────────┘ └───────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、引导安装器需求分析

### 3.1 引导安装器需要什么？

| 组件 | 说明 | 内置/安装 |
|------|------|-----------|
| **Spring Boot** | 运行容器 | 内置 |
| **Agent SDK** | 技能执行基础 | 内置（Maven依赖） |
| **Hot Plug Starter** | 热插拔管理 | 内置（Maven依赖） |
| **setup.html** | 引导页面 | 内置 |
| **SetupController** | 安装控制器 | 内置 |
| **nexus.css** | 页面样式 | ❌ 问题：需要但不在内置 |

### 3.2 发现问题

**问题**：setup.html 需要样式，但 nexus.css 在 skill-scene 中

**解决方案**：

```
方案A：setup.html 使用内联样式（简单、独立）
方案B：内置最小化 bootstrap.css（约 50KB）
方案C：内置精简版 nexus-core.css（仅包含基础样式）
```

**推荐方案C**：提取 nexus.css 的核心部分（主题变量、基础布局、按钮、表单）

---

## 四、Skills 底座程序设计

### 4.1 底座程序结构

```
mvp/
├── pom.xml
│   └── dependencies:
│       ├── spring-boot-starter-web
│       ├── agent-sdk-api (2.3)
│       ├── agent-sdk-core (2.3)
│       ├── skills-framework (2.3)
│       └── skill-hotplug-starter (2.3.1)
│
├── src/main/java/net/ooder/mvp/
│   ├── MvpCoreApplication.java      # 启动类
│   ├── config/
│   │   ├── MvcConfig.java          # MVC配置
│   │   └── HotPlugConfig.java      # 热插拔配置
│   └── controller/
│       └── SetupController.java    # 引导安装控制器
│
└── src/main/resources/
    ├── application.yml              # 应用配置
    └── static/
        ├── setup/                   # 引导安装页面
        │   ├── index.html          # 引导页面
        │   ├── setup.css           # 精简样式（~10KB）
        │   └── setup.js            # 安装逻辑
        └── skills/                  # Skills 安装目录
            └── (空，安装后填充)
```

### 4.2 底座程序代码量

| 组件 | 代码量 |
|------|--------|
| MvpCoreApplication.java | ~30行 |
| MvcConfig.java | ~50行 |
| HotPlugConfig.java | ~30行 |
| SetupController.java | ~80行 |
| setup/index.html | ~150行 |
| setup/setup.css | ~200行（精简样式） |
| setup/setup.js | ~100行 |
| application.yml | ~30行 |
| **总计** | **~670行** |

### 4.3 精简样式内容

```css
/* setup/setup.css - 从 nexus.css 提取的核心样式 */

/* 1. CSS 变量（主题） */
:root {
    --nx-primary: #6366f1;
    --nx-bg: #0a0a0a;
    --nx-text: #ffffff;
    /* ... 约 50 个变量 */
}

[data-theme="light"] {
    --nx-bg: #ffffff;
    --nx-text: #1e293b;
    /* ... */
}

/* 2. 基础布局 */
.setup-page { min-height: 100vh; display: flex; }
.setup-container { max-width: 480px; margin: auto; }

/* 3. 按钮 */
.nx-btn { padding: 10px 20px; border-radius: 8px; }
.nx-btn--primary { background: var(--nx-primary); color: white; }

/* 4. 表单 */
.nx-input { padding: 12px; border: 1px solid var(--nx-border); }
.nx-select { /* ... */ }

/* 5. 进度条 */
.progress-bar { height: 8px; background: var(--nx-bg-elevated); }
.progress { background: var(--nx-primary); }

/* 总计约 200 行 CSS */
```

---

## 五、闭环流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Skills 底座程序启动流程                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. Spring Boot 启动                                                        │
│         │                                                                   │
│         ▼                                                                   │
│  2. PluginManager 初始化                                                    │
│         │                                                                   │
│         ▼                                                                   │
│  3. 检测 skills/ 目录                                                       │
│         │                                                                   │
│         ├── 有已安装 Skill ──────────────────────────────────────────────┐  │
│         │                                                               │  │
│         │                     ▼                                         │  │
│         │              4a. 加载已安装 Skills                             │  │
│         │                     │                                         │  │
│         │                     ▼                                         │  │
│         │              5a. 注册路由和菜单                                 │  │
│         │                     │                                         │  │
│         │                     ▼                                         │  │
│         │              6a. 显示登录页（来自 skill-scene）                  │  │
│         │                                                               │  │
│         └── 无已安装 Skill ─────────────────────────────────────────────┘  │
│                     │                                                       │
│                     ▼                                                       │
│              4b. 显示引导安装页（setup/index.html）                          │
│                     │                                                       │
│                     ▼                                                       │
│              5b. 用户选择安装 skill-scene                                    │
│                     │                                                       │
│                     ▼                                                       │
│              6b. PluginManager.installSkill("skill-scene")                  │
│                     │                                                       │
│                     ▼                                                       │
│              7b. 安装完成，刷新页面                                          │
│                     │                                                       │
│                     ▼                                                       │
│              8b. 显示登录页（来自 skill-scene）                              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 六、总结

### 6.1 Skills 底座程序 = MVP 最小实现

| 层级 | 组件 | 代码量 |
|------|------|--------|
| **运行时层** | Spring Boot + Agent SDK + Hot Plug | Maven依赖（0行代码） |
| **引导层** | setup.html + SetupController | ~430行 |
| **配置层** | application.yml + Config | ~110行 |
| **样式层** | setup.css（精简版） | ~200行 |
| **总计** | | **~740行** |

### 6.2 底座程序职责

1. **提供运行环境**：Spring Boot + Agent SDK
2. **提供热插拔能力**：PluginManager
3. **提供引导安装**：setup.html
4. **提供基础样式**：精简版 CSS

### 6.3 安装 skill-scene 后获得

1. **完整前端框架**：nexus.css, nexus.js, menu.js
2. **发现安装能力**：capability-discovery
3. **场景管理能力**：scene-management
4. **LLM配置能力**：llm-config
5. **50+ 页面**：登录、工作台、配置等

---

## 七、下一步行动

1. **创建 setup.css** - 从 nexus.css 提取核心样式（~200行）
2. **创建 setup/index.html** - 引导安装页面（~150行）
3. **创建 SetupController.java** - 安装控制器（~80行）
4. **创建 SetupInterceptor.java** - 状态检测拦截器（~30行）
5. **更新 MVP pom.xml** - 添加必备依赖
