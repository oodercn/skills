# MVP 工程分析与最小 Skills 集合完整性检查

## 一、MVP 工程目的分析

### 1.1 MVP 定义

**MVP (Minimum Viable Product)** 是 OODER Skills 平台的最小可行产品工程，设计目标：

1. **极简启动** - 仅包含核心运行时，不预装任何业务模块
2. **动态安装** - 通过热插拔机制按需安装 Skills
3. **首次安装向导** - 提供安装流程引导用户完成初始化配置

### 1.2 MVP 架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        MVP 工程                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐     ┌─────────────────────────────────┐   │
│  │   Maven 依赖     │     │         功能组件                │   │
│  ├─────────────────┤     ├─────────────────────────────────┤   │
│  │ skill-common    │ ──► │ 公共基础服务 (用户/存储/配置)    │   │
│  │ skill-hotplug-  │ ──► │ 热插拔管理器 (动态加载Skills)   │   │
│  │   starter       │     │                                 │   │
│  └─────────────────┘     └─────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                     安装向导流程                          │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │  1. 欢迎页面 (welcome)                                   │   │
│  │  2. 模块选择 (modules)                                   │   │
│  │  3. 安装过程 (install)                                   │   │
│  │     - skill-capability      能力管理核心                 │   │
│  │     - skill-scene-management 场景管理UI                  │   │
│  │  4. 管理员配置 (admin)                                   │   │
│  │  5. 完成 (done)                                          │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 1.3 MVP pom.xml 依赖

```xml
<dependencies>
    <!-- 核心基础模块 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skill-common</artifactId>
        <version>${project.version}</version>
    </dependency>
    
    <!-- 热插拔支持 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skill-hotplug-starter</artifactId>
        <version>${project.version}</version>
    </dependency>
</dependencies>
```

---

## 二、最小 Skills 集合定义

### 2.1 核心依赖链

```
MVP (最小启动)
    │
    ├── skill-common (必需)
    │       ├── 用户管理服务
    │       ├── 部门管理服务
    │       ├── JSON存储服务
    │       └── SDK资源配置
    │
    └── skill-hotplug-starter (必需)
            ├── PluginManager (插件管理器)
            ├── SkillPackage (技能包模型)
            └── 热加载机制
```

### 2.2 安装流程中的 Skills

根据 `setup.js` 分析，安装流程安装以下 Skills：

| 序号 | Skill ID | 说明 | 必需性 |
|------|----------|------|--------|
| 1 | skill-capability | 能力管理核心 | **必需** |
| 2 | skill-scene-management | 场景管理UI | **必需** |

### 2.3 完整的最小集合

```
最小 Skills 集合 (按依赖顺序):

1. skill-common              # 基础服务层
   └── 无依赖

2. skill-hotplug-starter     # 热插拔支持
   └── 依赖: skill-common

3. skill-capability          # 能力管理
   └── 依赖: skill-common

4. skill-scene-management    # 场景管理UI
   └── 依赖: skill-common, skill-capability (可选)
```

---

## 三、完整性检查

### 3.1 检查项清单

| 检查项 | 状态 | 说明 |
|--------|------|------|
| MVP pom.xml 依赖正确 | ✅ | 仅依赖 skill-common 和 skill-hotplug-starter |
| skill-common 存在 | ✅ | `skills/_system/skill-common` |
| skill-hotplug-starter 存在 | ✅ | `skill-hotplug-starter` |
| skill-capability 存在 | ✅ | `skills/_system/skill-capability` |
| skill-scene-management 存在 | ✅ | `skills/_system/skill-scene-management` |
| 安装向导页面存在 | ✅ | `mvp/src/main/resources/static/setup/` |
| SetupController 存在 | ✅ | 处理安装流程API |

### 3.2 依赖关系检查

```
┌─────────────────────────────────────────────────────────────────┐
│                      依赖关系检查结果                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  MVP                                                            │
│   ├── skill-common ✅                                           │
│   │      └── 编译成功, JAR存在                                  │
│   │                                                             │
│   └── skill-hotplug-starter ✅                                  │
│          └── 编译成功, JAR存在                                  │
│                                                                 │
│  安装流程 Skills:                                               │
│   ├── skill-capability ✅                                       │
│   │      └── 编译成功, JAR存在于 target/                        │
│   │                                                             │
│   └── skill-scene-management ✅                                 │
│          └── 编译成功, JAR存在于 target/                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 3.3 安装流程检查

| 步骤 | API端点 | 状态 | 说明 |
|------|---------|------|------|
| 检查安装状态 | GET /api/v1/setup/status | ✅ | 返回 installed 状态 |
| 安装 Skill | POST /api/v1/plugin/install | ✅ | 调用 PluginManager |
| 创建管理员 | POST /api/v1/setup/admin | ✅ | 保存用户并标记已安装 |

### 3.4 文件完整性检查

```
mvp/
├── pom.xml                              ✅ 存在
├── src/main/java/
│   └── net/ooder/mvp/
│       ├── MvpCoreApplication.java      ✅ 存在
│       ├── config/
│       │   ├── MvcConfig.java           ✅ 存在
│       │   └── SetupInterceptor.java    ✅ 存在
│       └── controller/
│           └── SetupController.java     ✅ 存在
└── src/main/resources/
    ├── application.yml                  ✅ 存在
    └── static/
        ├── index.html                   ✅ 存在
        ├── setup/
        │   ├── index.html               ✅ 存在
        │   ├── setup.css                ✅ 存在
        │   └── setup.js                 ✅ 存在
        └── console/
            ├── css/                     ✅ 存在
            └── js/                      ✅ 存在
```

---

## 四、潜在问题与建议

### 4.1 发现的问题

| 问题 | 严重性 | 说明 |
|------|--------|------|
| skill-capability 菜单配置路径错误 | 中 | 需要修复为正确的 skill 路径 |
| skill-scene-management 菜单配置路径错误 | 中 | 需要修复为正确的 skill 路径 |
| 安装后跳转路径硬编码 | 低 | `/console/pages/login.html` 应改为动态路径 |

### 4.2 建议改进

1. **安装流程增强**
   - 添加 Skill 依赖检查
   - 添加安装失败回滚机制
   - 添加安装进度持久化

2. **最小集合扩展建议**
   ```
   当前最小集合:
   - skill-common
   - skill-hotplug-starter
   - skill-capability
   - skill-scene-management

   建议扩展:
   - skill-llm (LLM基础能力)
   - skill-protocol (协议支持)
   ```

3. **安装向导改进**
   - 允许用户选择要安装的 Skills
   - 显示 Skill 依赖关系
   - 提供离线安装支持

---

## 五、最小集合启动验证

### 5.1 启动命令

```bash
cd mvp
mvn spring-boot:run
```

### 5.2 验证步骤

1. 访问 http://localhost:8084/
2. 自动跳转到安装向导 /setup/index.html
3. 完成安装流程
4. 验证菜单加载
5. 验证能力管理页面
6. 验证场景管理页面

### 5.3 验证结果

| 验证项 | 状态 | 说明 |
|--------|------|------|
| 服务启动 | ✅ | 端口 8084 |
| 安装向导访问 | ✅ | /setup/index.html |
| Skill 安装 | ⚠️ | 需要验证 JAR 复制到 plugins 目录 |
| 菜单加载 | ✅ | 菜单 API 正常 |
| 页面访问 | ⚠️ | 需要修复路径问题 |

---

## 六、总结

### MVP 设计理念

MVP 工程遵循了**最小可行产品**的设计理念：
- 极简启动，仅包含运行时核心
- 动态安装，按需加载业务模块
- 引导式安装，降低用户使用门槛

### 最小 Skills 集合

```
核心层 (MVP 内置):
├── skill-common          # 基础服务
└── skill-hotplug-starter # 热插拔支持

安装层 (首次安装):
├── skill-capability          # 能力管理
└── skill-scene-management    # 场景管理UI
```

### 完整性评估

| 维度 | 评分 | 说明 |
|------|------|------|
| 架构设计 | ⭐⭐⭐⭐⭐ | 清晰的分层和职责划分 |
| 依赖管理 | ⭐⭐⭐⭐ | 最小依赖，动态加载 |
| 安装流程 | ⭐⭐⭐⭐ | 引导式安装，用户体验好 |
| 路径配置 | ⭐⭐⭐ | 需要修复菜单路径问题 |
| 文档完整 | ⭐⭐⭐⭐ | 本文档补充了完整性检查 |

---

*文档生成时间: 2026-03-16*
*检查范围: MVP 工程 + 最小 Skills 集合*
