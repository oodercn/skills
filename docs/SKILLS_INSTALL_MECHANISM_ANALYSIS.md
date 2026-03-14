# SKILLS 安装机制分析报告

## 一、问题1：skills 发现和安装程序是内置还是安装？

### 答案：需要安装

**发现和安装程序位于 skill-scene 中**：

| 组件 | 位置 | 说明 |
|------|------|------|
| CapabilityDiscoveryServiceImpl | skill-scene | 发现能力 |
| InstallServiceImpl | skill-scene | 安装能力（依赖、进度、回滚） |
| capability-discovery.html | skill-scene | 发现页面 |
| capability-discovery.js | skill-scene | 发现逻辑 |

**依赖关系**：
```
skill-scene (包含发现和安装程序)
    ├── 依赖 agent-sdk-api
    ├── 依赖 agent-sdk-core
    ├── 依赖 scene-engine
    ├── 依赖 skill-org-base
    └── 依赖 skill-common
```

**结论**：发现和安装程序不是内置的，需要先安装 skill-scene。

---

## 二、问题2：skills 本身需要哪些内置资源？

### 答案：skill-scene 自带完整前端资源

**skill-scene 包含的前端资源**：

```
skill-scene/src/main/resources/static/console/
├── css/
│   ├── nexus.css           # 核心框架（主题、布局、组件）
│   ├── theme.css           # 主题变量
│   ├── nx-page.css         # 页面布局
│   ├── remixicon/          # 图标字体
│   ├── components/         # 组件样式
│   │   ├── buttons.css
│   │   ├── cards.css
│   │   ├── forms.css
│   │   ├── tables.css
│   │   ├── install-progress.css
│   │   └── ...
│   └── pages/              # 页面样式
│       ├── capability-discovery.css
│       ├── config-system.css
│       └── ...
├── js/
│   ├── nexus.js            # 核心JS（主题、侧边栏、用户菜单）
│   ├── menu.js             # 动态菜单
│   ├── page-init.js        # 页面初始化
│   ├── api-client.js       # API客户端
│   ├── capability-discovery.js  # 发现页面逻辑
│   ├── capability-service.js    # 能力服务
│   └── ...
└── pages/
    ├── capability-discovery.html  # 发现页面
    ├── scene-management.html      # 场景管理
    ├── llm-config.html            # LLM配置
    └── ...（50+页面）
```

**MVP 需要内置的最小资源**：
- 只需要一个 `setup.html`（初始化引导页面）
- 其他资源都随 skill-scene 安装

---

## 三、问题3：独立的安装 skills(能力)是否已经可用？

### 答案：是的，已经可用！

**InstallServiceImpl 已实现完整功能**：

| 功能 | 方法 | 状态 |
|------|------|------|
| 创建安装配置 | createInstall() | ✅ 已实现 |
| 执行安装 | executeInstall() | ✅ 已实现 |
| 依赖安装 | installDependency() | ✅ 已实现 |
| 进度跟踪 | getInstallProgress() | ✅ 已实现 |
| 回滚安装 | rollbackInstall() | ✅ 已实现 |
| 参与者管理 | addParticipant() | ✅ 已实现 |

**安装流程**：
```
1. createInstall() → 创建安装配置
2. executeInstall() → 执行安装
   ├── 解析依赖
   ├── 安装依赖能力
   ├── 更新状态
   └── 发送通知
3. getInstallProgress() → 获取进度
```

---

## 四、关键发现：鸡生蛋问题

**问题**：
- skill-scene 包含发现和安装程序
- 但 skill-scene 本身需要先被安装
- 谁来安装 skill-scene？

**解决方案**：

```
┌─────────────────────────────────────────────────────────────────┐
│                        MVP 启动流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              MVP 内置（~350行代码）                       │   │
│  │  ┌─────────────────────────────────────────────────┐    │   │
│  │  │  setup.html + SetupController                   │    │   │
│  │  │  - 检测是否已安装 skill-scene                    │    │   │
│  │  │  - 显示引导页面                                  │    │   │
│  │  │  - 调用 PluginManager.installSkill()            │    │   │
│  │  │  - 安装 skill-scene                             │    │   │
│  │  └─────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│                              │ 安装 skill-scene                 │
│                              ▼                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              skill-scene 安装后可用                      │   │
│  │  ┌─────────────────────────────────────────────────┐    │   │
│  │  │  capability-discovery.html                      │    │   │
│  │  │  - 发现其他能力                                  │    │   │
│  │  │  - 安装其他能力                                  │    │   │
│  │  │  - 管理已安装能力                                │    │   │
│  │  └─────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 五、MVP 最小实现方案

### 5.1 MVP 内置内容

| 组件 | 代码量 | 说明 |
|------|--------|------|
| SetupController.java | ~50行 | 引导安装控制器 |
| setup.html | ~200行 | 引导安装页面 |
| SetupInterceptor.java | ~30行 | 拦截器（检测是否已安装） |
| Spring Boot 配置 | ~70行 | 启动类、MVC配置 |
| **总计** | **~350行** | |

### 5.2 MVP 依赖

```xml
<dependencies>
    <!-- 热插拔管理器 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skill-hotplug-starter</artifactId>
        <version>2.3.1</version>
    </dependency>
    
    <!-- 技能管理器 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skill-management</artifactId>
        <version>2.3.1</version>
    </dependency>
</dependencies>
```

### 5.3 启动流程

```
1. Spring Boot 启动
2. PluginManager 初始化
3. 检测已安装 Skills
   ├── 有 → 加载已安装 Skills → 显示登录页
   └── 无 → 显示 setup.html
4. setup.html 引导安装 skill-scene
5. 安装完成后 → 显示登录页
6. 登录后 → 可使用 capability-discovery 安装其他能力
```

---

## 六、总结

| 问题 | 答案 |
|------|------|
| 1. skills 发现和安装程序是内置还是安装？ | **需要安装**（位于 skill-scene） |
| 2. skills 本身需要哪些内置资源？ | **skill-scene 自带完整资源**（nexus.css, nexus.js, 50+页面） |
| 3. 独立的安装 skills 是否已经可用？ | **是的**（InstallServiceImpl 已实现完整功能） |

**结论**：MVP 只需要内置一个引导安装器（~350行代码），用于安装 skill-scene。安装 skill-scene 后，就可以使用它的 capability-discovery 来发现和安装其他能力。
