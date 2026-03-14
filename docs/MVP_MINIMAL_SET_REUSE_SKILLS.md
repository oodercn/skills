# MVP 最小集合设计（复用 SKILLS 机制）

## 一、设计原则

**复用现有机制**：
- ✅ skill-hotplug-starter 的 PluginManager（安装/卸载/更新）
- ✅ skill-management 的 SkillManager（注册/执行）
- ✅ skill-management 的 SkillLifecycleManager（生命周期）
- ✅ skill-scene 的 InstallService（安装流程）

**MVP 只需新增**：
- 一个初始化页面（setup.html）- 用于首次启动安装最小集合

## 二、MVP 依赖关系

```
mvp/
├── pom.xml
│   └── dependencies:
│       ├── skill-hotplug-starter    # 热插拔管理器
│       ├── skill-management         # 技能管理器
│       ├── skill-common             # 公共基础（auth, menu）
│       └── spring-boot-starter-web
│
└── src/main/resources/static/
    └── setup.html                   # 初始化页面（唯一新增）
```

## 三、MVP 启动流程

```
┌─────────────────────────────────────────────────────────────────┐
│                        MVP 启动流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. Spring Boot 启动                                             │
│         │                                                       │
│         ▼                                                       │
│  2. PluginManager 初始化                                         │
│         │                                                       │
│         ▼                                                       │
│  3. loadInstalledPlugins()                                      │
│         │                                                       │
│         ├── 有已安装插件 ──────────────────────────────────────┐│
│         │                                                     ││
│         │                     ▼                               ││
│         │              4a. 加载已安装 Skills                    ││
│         │                     │                               ││
│         │                     ▼                               ││
│         │              5a. 启动完成                             ││
│         │                     │                               ││
│         │                     ▼                               ││
│         │              6a. 显示登录页                           ││
│         │                                                     ││
│         └── 无已安装插件 ──────────────────────────────────────┘│
│                     │                                           │
│                     ▼                                           │
│              4b. 显示初始化页面 (setup.html)                     │
│                     │                                           │
│                     ▼                                           │
│              5b. 用户选择安装最小集合                             │
│                     │                                           │
│                     ▼                                           │
│              6b. 调用 PluginManager.installSkill()              │
│                     │                                           │
│                     ▼                                           │
│              7b. 安装 auth, menu, login                         │
│                     │                                           │
│                     ▼                                           │
│              8b. 初始化管理员账户                                 │
│                     │                                           │
│                     ▼                                           │
│              9b. 跳转到登录页                                     │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 四、初始化页面设计 (setup.html)

### 4.1 功能

- 检测系统状态
- 显示最小集合 Skills
- 一键安装
- 初始化管理员账户

### 4.2 页面结构

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>系统初始化 - Ooder MVP</title>
</head>
<body>
    <div class="setup-container">
        <h1>系统初始化</h1>
        
        <!-- 步骤1: 欢迎页面 -->
        <div class="step" id="step-welcome">
            <h2>欢迎使用 Ooder MVP</h2>
            <p>这是首次启动，需要安装基础模块</p>
            <button onclick="startSetup()">开始初始化</button>
        </div>
        
        <!-- 步骤2: 选择安装模块 -->
        <div class="step" id="step-select" style="display:none">
            <h2>选择基础模块</h2>
            <div class="module-list">
                <div class="module-item required">
                    <input type="checkbox" checked disabled>
                    <span>认证模块 (auth)</span>
                </div>
                <div class="module-item required">
                    <input type="checkbox" checked disabled>
                    <span>菜单系统 (menu)</span>
                </div>
                <div class="module-item required">
                    <input type="checkbox" checked disabled>
                    <span>登录页面 (login)</span>
                </div>
            </div>
            <button onclick="installModules()">安装</button>
        </div>
        
        <!-- 步骤3: 安装进度 -->
        <div class="step" id="step-install" style="display:none">
            <h2>正在安装...</h2>
            <div class="progress-bar">
                <div class="progress" id="progress"></div>
            </div>
            <div id="install-log"></div>
        </div>
        
        <!-- 步骤4: 初始化管理员 -->
        <div class="step" id="step-admin" style="display:none">
            <h2>创建管理员账户</h2>
            <form id="admin-form">
                <input type="text" placeholder="用户名" id="admin-username">
                <input type="password" placeholder="密码" id="admin-password">
                <button type="submit">创建</button>
            </form>
        </div>
        
        <!-- 步骤5: 完成 -->
        <div class="step" id="step-done" style="display:none">
            <h2>初始化完成！</h2>
            <button onclick="goToLogin()">进入系统</button>
        </div>
    </div>
    
    <script>
        // 调用现有 API
        async function installModules() {
            const modules = ['skill-common'];
            for (const module of modules) {
                await fetch('/api/v1/plugin/install', {
                    method: 'POST',
                    body: JSON.stringify({ skillId: module })
                });
            }
        }
    </script>
</body>
</html>
```

## 五、复用的 API

### 5.1 PluginManager API

```java
// 安装 Skill
POST /api/v1/plugin/install
{
    "skillId": "skill-common",
    "source": "skills/skill-common"
}

// 获取已安装 Skills
GET /api/v1/plugin/list

// 卸载 Skill
DELETE /api/v1/plugin/{skillId}
```

### 5.2 SkillLifecycleManager 事件

```java
// 生命周期事件
onSkillDiscovered(skillId, skill)
onSkillLoading(skillId)
onSkillLoaded(skillId, skill)
onSkillStarting(skillId)
onSkillStarted(skillId)
onSkillStopping(skillId)
onSkillStopped(skillId)
onSkillUnloading(skillId)
onSkillUnloaded(skillId)
```

## 六、MVP 最小集合

### 6.1 必须内置

| 组件 | 代码量 | 说明 |
|------|--------|------|
| Spring Boot 配置 | ~100行 | 启动类、MVC配置 |
| SetupController | ~50行 | 初始化页面控制器 |
| setup.html | ~200行 | 初始化页面 |
| **总计** | **~350行** | |

### 6.2 预装 Skills（通过 setup.html 安装）

| Skill | 包含模块 | 说明 |
|-------|----------|------|
| skill-common | auth, menu, login, org | 公共基础模块 |

### 6.3 可选 Skills（通过 capability-discovery 安装）

| Skill | 包含模块 | 说明 |
|-------|----------|------|
| skill-scene | capability-discovery, scene-management, llm-config | 场景引擎 |
| skill-knowledge | knowledge-base | 知识库 |
| skill-llm-deepseek | llm-deepseek | DeepSeek驱动 |

## 七、实现步骤

### Step 1: 修改 MVP pom.xml

```xml
<dependencies>
    <!-- 复用现有机制 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skill-hotplug-starter</artifactId>
        <version>2.3.1</version>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skill-management</artifactId>
        <version>2.3.1</version>
    </dependency>
</dependencies>
```

### Step 2: 创建 SetupController

```java
@Controller
public class SetupController {
    
    @Autowired
    private PluginManager pluginManager;
    
    @GetMapping("/setup")
    public String setup() {
        if (pluginManager.getInstalledSkills().isEmpty()) {
            return "setup";
        }
        return "redirect:/console/pages/login.html";
    }
    
    @PostMapping("/api/v1/setup/install")
    @ResponseBody
    public Result installBaseModules() {
        // 安装 skill-common
        pluginManager.installSkill(...);
        return Result.success();
    }
}
```

### Step 3: 创建 setup.html

（见上文设计）

### Step 4: 修改启动逻辑

```java
@Component
public class SetupInterceptor implements HandlerInterceptor {
    
    @Autowired
    private PluginManager pluginManager;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, {
        // 如果没有安装任何 skill，跳转到 setup 页面
        if (pluginManager.getInstalledSkills().isEmpty() 
            && !request.getRequestURI().startsWith("/setup")
            && !request.getRequestURI().startsWith("/api/v1/setup")) {
            response.sendRedirect("/setup");
            return false;
        }
        return true;
    }
}
```

## 八、总结

| 项目 | 传统方式 | 复用 SKILLS 机制 |
|------|----------|------------------|
| MVP 代码量 | 10000+ 行 | ~350 行 |
| 机制复用 | 无 | PluginManager, SkillManager, SkillLifecycleManager |
| 初始化页面 | 无 | setup.html（功能单一） |
| 升级方式 | 重新发布 | Skills 独立升级 |

**结论**：MVP 只需要 ~350 行代码 + 一个 setup.html 页面，完全复用 SKILLS 现有的安装和生命周期机制。
