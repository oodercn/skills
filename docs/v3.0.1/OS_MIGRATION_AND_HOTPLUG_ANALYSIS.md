# OS页面迁移与Hotplug机制深度分析报告

**版本**: 3.0.1  
**创建日期**: 2026-04-02  
**分析范围**: E:\apex\os 页面、API、Hotplug机制  
**目的**: 完整统计迁移内容，理解生命周期管理机制，给出标准迁移步骤

---

## 📊 一、OS完整页面与API统计

### 1.1 页面完整清单（76个HTML页面）

#### 能力管理相关（11个页面）

| 页面名称 | 文件路径 | 功能描述 | 迁移优先级 |
|---------|---------|---------|-----------|
| capability-discovery.html | /console/pages/ | 能力发现（雷达扫描） | P0 |
| capability-management.html | /console/pages/ | 能力管理中心 | P0 |
| my-capabilities.html | /console/pages/ | 我的能力 | P0 |
| capability-detail.html | /console/pages/ | 能力详情 | P0 |
| capability-activation.html | /console/pages/ | 能力激活 | P0 |
| capability-binding.html | /console/pages/ | 能力绑定 | P0 |
| capability-create.html | /console/pages/ | 创建能力 | P0 |
| capability-dependencies.html | /console/pages/ | 能力依赖关系 | P1 |
| capability-logs.html | /console/pages/ | 能力日志 | P1 |
| capability-permissions.html | /console/pages/ | 能力权限 | P1 |
| capability-stats.html | /console/pages/ | 能力统计 | P1 |
| capability-versions.html | /console/pages/ | 能力版本管理 | P1 |

#### 场景管理相关（7个页面）

| 页面名称 | 文件路径 | 功能描述 | 迁移优先级 |
|---------|---------|---------|-----------|
| scene-management.html | /console/pages/ | 场景管理 | P0 |
| scene-detail.html | /console/pages/ | 场景详情 | P0 |
| scene-capabilities.html | /console/pages/ | 场景能力列表 | P0 |
| scene-capability-detail.html | /console/pages/ | 场景能力详情 | P0 |
| scene-knowledge.html | /console/pages/ | 场景知识库配置 | P0 |
| scene-group-management.html | /console/pages/ | 场景组管理 | P1 |
| scene-group-detail.html | /console/pages/ | 场景组详情 | P1 |

#### 系统配置相关（15个页面）

| 页面名称 | 文件路径 | 功能描述 | 迁移优先级 |
|---------|---------|---------|-----------|
| config-system.html | /console/pages/ | 系统配置 | P0 |
| auth-config.html | /console/pages/ | 认证配置 | P0 |
| org-config.html | /console/pages/ | 组织配置 | P0 |
| llm-config.html | /console/pages/ | LLM配置 | P0 |
| llm-knowledge-config.html | /console/pages/ | LLM知识库配置 | P0 |
| llm-monitor.html | /console/pages/ | LLM监控 | P1 |
| vfs-config.html | /console/pages/ | 虚拟文件系统配置 | P0 |
| db-config.html | /console/pages/ | 数据库配置 | P0 |
| comm-config.html | /console/pages/ | 通讯配置 | P0 |
| security-config.html | /console/pages/ | 安全配置 | P0 |
| driver-config.html | /console/pages/ | 驱动配置 | P0 |
| key-management.html | /console/pages/ | 密钥管理 | P0 |
| org-sync.html | /console/pages/ | 组织同步 | P1 |
| notifications.html | /console/pages/ | 通知管理 | P1 |
| audit-logs.html | /console/pages/ | 审计日志 | P1 |

#### 知识库相关（5个页面）

| 页面名称 | 文件路径 | 功能描述 | 迁移优先级 |
|---------|---------|---------|-----------|
| knowledge-center.html | /console/pages/ | 知识中心 | P0 |
| knowledge-base.html | /console/pages/ | 知识库管理 | P0 |
| knowledge-search.html | /console/pages/ | 知识搜索 | P0 |
| business-knowledge.html | /console/pages/ | 业务知识库 | P0 |
| installed-scene-capabilities.html | /console/pages/ | 已安装场景能力 | P0 |

#### Agent管理相关（8个页面）

| 页面名称 | 文件路径 | 功能描述 | 迁移优先级 |
|---------|---------|---------|-----------|
| agent-list.html | /console/pages/ | Agent列表 | P0 |
| agent-detail.html | /console/pages/ | Agent详情 | P0 |
| agent-register.html | /console/pages/ | Agent注册 | P0 |
| agent-monitor.html | /console/pages/ | Agent监控 | P0 |
| agent-topology.html | /console/pages/ | Agent拓扑 | P1 |
| agent-alert.html | /console/pages/ | Agent告警 | P1 |
| address-space.html | /console/pages/ | 地址空间管理 | P1 |
| arch-check.html | /console/pages/ | 架构检查 | P1 |

#### 组织管理相关（6个页面）

| 页面名称 | 文件路径 | 功能描述 | 迁移优先级 |
|---------|---------|---------|-----------|
| org-management.html | /console/pages/ | 组织管理 | P0 |
| role-admin.html | /console/pages/ | 管理员角色 | P0 |
| role-user.html | /console/pages/ | 用户角色 | P0 |
| role-developer.html | /console/pages/ | 开发者角色 | P0 |
| role-leader.html | /console/pages/ | 领导角色 | P0 |
| role-collaborator.html | /console/pages/ | 协作者角色 | P0 |

#### 模板管理相关（4个页面）

| 页面名称 | 文件路径 | 功能描述 | 迁移优先级 |
|---------|---------|---------|-----------|
| template-management.html | /console/pages/ | 模板管理 | P0 |
| template-detail.html | /console/pages/ | 模板详情 | P0 |
| fusion-template-list.html | /console/pages/ | 融合模板列表 | P1 |
| fusion-template-detail.html | /console/pages/ | 融合模板详情 | P1 |

#### 流程管理相关（4个页面）

| 页面名称 | 文件路径 | 功能描述 | 迁移优先级 |
|---------|---------|---------|-----------|
| execution.html | /console/pages/ | 执行管理 | P0 |
| enterprise-procedure-list.html | /console/pages/ | 企业流程列表 | P1 |
| enterprise-procedure-detail.html | /console/pages/ | 企业流程详情 | P1 |
| enterprise-procedure-create.html | /console/pages/ | 创建企业流程 | P1 |

#### 用户工作台相关（9个页面）

| 页面名称 | 文件路径 | 功能描述 | 迁移优先级 |
|---------|---------|---------|-----------|
| workbench.html | /console/pages/ | 工作台 | P0 |
| my-profile.html | /console/pages/ | 个人中心 | P0 |
| my-scenes.html | /console/pages/ | 我的场景 | P0 |
| my-todos.html | /console/pages/ | 我的待办 | P0 |
| my-history.html | /console/pages/ | 我的历史 | P0 |
| login.html | /console/pages/ | 登录页面 | P0 |
| menu-auth.html | /console/pages/ | 菜单权限 | P0 |
| daily-report-form.html | /console/pages/ | 日报表单 | P1 |
| skill-detail.html | /console/pages/ | 技能详情 | P0 |

#### 其他功能页面（7个页面）

| 页面名称 | 文件路径 | 功能描述 | 迁移优先级 |
|---------|---------|---------|-----------|
| skill-config-detail.html | /console/pages/ | 技能配置详情 | P0 |
| network-approval.html | /console/pages/ | 网络审批 | P1 |
| link-list.html | /console/pages/ | 链接列表 | P1 |
| role-installer.html | /console/pages/ | 安装者角色 | P0 |

### 1.2 API接口完整清单（按模块分类）

#### 能力发现API（6个接口）

```
GET    /api/v1/discovery/methods              # 获取发现方法列表
POST   /api/v1/discovery/local                # 本地发现
POST   /api/v1/discovery/github               # GitHub发现
POST   /api/v1/discovery/gitee                # Gitee发现
POST   /api/v1/discovery/install              # 安装能力
GET    /api/v1/discovery/capability/{id}      # 获取能力详情
```

#### 能力管理API（12个接口）

```
GET    /api/v1/capabilities                   # 获取能力列表
POST   /api/v1/capabilities                   # 注册能力
GET    /api/v1/capabilities/{id}              # 获取能力详情
PUT    /api/v1/capabilities/{id}              # 更新能力
DELETE /api/v1/capabilities/{id}              # 删除能力
POST   /api/v1/capabilities/{id}/activate     # 激活能力
POST   /api/v1/capabilities/{id}/deactivate   # 停用能力
POST   /api/v1/capabilities/{id}/bind         # 绑定能力
POST   /api/v1/capabilities/{id}/unbind       # 解绑能力
GET    /api/v1/capabilities/{id}/dependencies # 获取依赖关系
POST   /api/v1/discovery/capabilities/invoke  # 调用能力
GET    /api/v1/capabilities/{id}/stats        # 获取能力统计
```

#### 场景管理API（10个接口）

```
POST   /api/v1/scenes/list                    # 场景列表
POST   /api/v1/scenes/create                  # 创建场景
POST   /api/v1/scenes/update                  # 更新场景
POST   /api/v1/scenes/delete                  # 删除场景
GET    /api/v1/scenes/{id}                    # 场景详情
GET    /api/v1/scenes/{id}/capabilities       # 场景能力列表
POST   /api/v1/scenes/{id}/capabilities/bind  # 绑定能力
POST   /api/v1/scenes/{id}/capabilities/unbind # 解绑能力
POST   /api/v1/scenes/{id}/knowledge          # 场景知识库配置
GET    /api/v1/scenes/{id}/knowledge          # 获取知识库配置
```

#### 场景组管理API（6个接口）

```
POST   /api/v1/scene-groups/list              # 场景组列表
POST   /api/v1/scene-groups/create            # 创建场景组
POST   /api/v1/scene-groups/update            # 更新场景组
POST   /api/v1/scene-groups/delete            # 删除场景组
GET    /api/v1/scene-groups/{id}              # 场景组详情
POST   /api/v1/scene-groups/{id}/scenes/bind  # 绑定场景
```

#### 系统配置API（15个接口）

```
GET    /api/v1/system/config                  # 获取系统配置
PUT    /api/v1/system/config                  # 更新系统配置
GET    /api/v1/system/health                  # 健康检查
GET    /api/v1/system/syscode                 # 获取系统代码
GET    /api/v1/system/check                   # 检查系统
GET    /api/v1/auth/config                    # 获取认证配置
PUT    /api/v1/auth/config                    # 更新认证配置
GET    /api/v1/org/config                     # 获取组织配置
PUT    /api/v1/org/config                     # 更新组织配置
GET    /api/v1/llm/config                     # 获取LLM配置
PUT    /api/v1/llm/config                     # 更新LLM配置
GET    /api/v1/vfs/config                     # 获取VFS配置
PUT    /api/v1/vfs/config                     # 更新VFS配置
GET    /api/v1/security/config                # 获取安全配置
PUT    /api/v1/security/config                # 更新安全配置
```

#### 知识库API（8个接口）

```
GET    /api/v1/knowledge-bases                # 获取知识库列表
POST   /api/v1/knowledge-bases                # 创建知识库
GET    /api/v1/knowledge-bases/{kbId}         # 获取知识库详情
PUT    /api/v1/knowledge-bases/{kbId}         # 更新知识库
DELETE /api/v1/knowledge-bases/{kbId}         # 删除知识库
POST   /api/v1/knowledge-bases/{kbId}/documents # 上传文档
GET    /api/v1/knowledge-bases/{kbId}/documents # 获取文档列表
DELETE /api/v1/knowledge-bases/{kbId}/documents/{docId} # 删除文档
```

#### Agent管理API（10个接口）

```
GET    /api/v1/agents                         # 获取Agent列表
POST   /api/v1/agents/register                # 注册Agent
GET    /api/v1/agents/{agentId}               # 获取Agent详情
PUT    /api/v1/agents/{agentId}               # 更新Agent
DELETE /api/v1/agents/{agentId}               # 删除Agent
POST   /api/v1/agents/{agentId}/start         # 启动Agent
POST   /api/v1/agents/{agentId}/stop          # 停止Agent
GET    /api/v1/agents/{agentId}/status        # 获取Agent状态
GET    /api/v1/agents/{agentId}/logs          # 获取Agent日志
GET    /api/v1/agents/topology                # 获取Agent拓扑
```

#### 用户认证API（6个接口）

```
POST   /api/v1/auth/login                     # 用户登录
POST   /api/v1/auth/logout                    # 用户登出
GET    /api/v1/auth/session                   # 获取会话信息
GET    /api/v1/auth/current-user              # 获取当前用户
GET    /api/v1/auth/roles                     # 获取可用角色列表
GET    /api/v1/auth/check-permission          # 检查权限
```

#### 组织管理API（10个接口）

```
GET    /api/v1/org/users/current              # 获取当前用户
GET    /api/v1/org/users                      # 列出用户
POST   /api/v1/org/users                      # 创建用户
GET    /api/v1/org/users/{userId}             # 获取用户
PUT    /api/v1/org/users/{userId}             # 更新用户
DELETE /api/v1/org/users/{userId}             # 删除用户
GET    /api/v1/org/departments                # 列出部门
POST   /api/v1/org/departments                # 创建部门
GET    /api/v1/org/tree                       # 获取组织树
GET    /api/v1/org/roles                      # 获取角色列表
```

#### 插件管理API（7个接口）

```
GET    /api/plugins                           # 获取所有已安装的插件
GET    /api/plugins/{skillId}                 # 获取指定插件信息
POST   /api/plugins/install                   # 上传并安装插件
POST   /api/plugins/{skillId}/uninstall       # 卸载插件
POST   /api/plugins/{skillId}/update          # 更新插件
GET    /api/plugins/{skillId}/status          # 获取插件状态
GET    /api/plugins/{skillId}/check           # 检查插件是否已安装
```

---

## 🔄 二、技能生命周期管理机制详解

### 2.1 生命周期状态机

```
┌─────────────────────────────────────────────────────────────────┐
│                     技能生命周期状态机                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────┐    install()    ┌──────────┐                     │
│  │  CREATED │ ───────────────→│INSTALLING│                     │
│  └──────────┘                 └──────────┘                     │
│                                    │                            │
│                         createClassLoader()                     │
│                         loadConfiguration()                     │
│                         registerServices()                      │
│                         registerRoutes()                        │
│                                    │                            │
│                                    ▼                            │
│                               ┌──────────┐                     │
│                               │INSTALLED │                     │
│                               └──────────┘                     │
│                                    │                            │
│                            startSkill()                         │
│                                    │                            │
│                                    ▼                            │
│                              ┌──────────┐                      │
│                              │ STARTING │                      │
│                              └──────────┘                      │
│                                    │                            │
│                          lifecycle.onStart()                    │
│                                    │                            │
│                                    ▼                            │
│                               ┌──────────┐                     │
│                               │  ACTIVE  │ ←── 运行中           │
│                               └──────────┘                     │
│                                    │                            │
│                      uninstall() or update()                    │
│                                    │                            │
│                                    ▼                            │
│                              ┌──────────┐                      │
│                              │ STOPPING │                      │
│                              └──────────┘                      │
│                                    │                            │
│                          lifecycle.onStop()                     │
│                          unregisterRoutes()                     │
│                          unregisterServices()                   │
│                          destroyClassLoader()                   │
│                                    │                            │
│                                    ▼                            │
│                             ┌──────────┐                       │
│                             │STOPPED   │                       │
│                             └──────────┘                       │
│                                    │                            │
│                                    ▼                            │
│                            ┌──────────┐                        │
│                            │UNINSTALLED│                        │
│                            └──────────┘                        │
│                                                                 │
│  错误状态: ERROR (任何阶段都可能转入)                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 安装流程详解

```java
/**
 * 安装Skill的完整流程
 * 位置: PluginManager.installSkill()
 */
public synchronized PluginInstallResult installSkill(SkillPackage skillPackage) {
    String skillId = skillPackage.getMetadata().getId();
    
    // ==================== 阶段1: 前置检查 ====================
    
    // 1.1 检查是否已存在
    if (activePlugins.containsKey(skillId)) {
        return PluginInstallResult.failure(skillId, "Skill already installed: " + skillId);
    }
    
    // ==================== 阶段2: 创建类加载器 ====================
    
    // 2.1 创建独立的PluginClassLoader
    // 这实现了类隔离，每个Skill有自己的类加载器
    PluginClassLoader classLoader = classLoaderManager.createClassLoader(skillPackage);
    
    // ==================== 阶段3: 加载配置 ====================
    
    // 3.1 从skill.yaml加载配置
    SkillConfiguration config = loadSkillConfiguration(skillPackage, classLoader);
    
    // 3.2 配置包含:
    // - metadata (id, name, version, description, author, type)
    // - dependencies (依赖的其他Skill)
    // - lifecycle (startup, shutdown类)
    // - routes (API路由定义)
    // - services (服务定义)
    // - ui (UI配置)
    
    // ==================== 阶段4: 创建上下文 ====================
    
    // 4.1 创建插件上下文，保存所有运行时信息
    PluginContext context = new PluginContext(skillId, classLoader, config);
    
    // ==================== 阶段5: 注册服务 ====================
    
    // 5.1 注册服务到ServiceRegistry
    // 服务可以是: Spring Bean, 接口实现等
    registerServices(context);
    
    // ==================== 阶段6: 注册路由 ====================
    
    // 6.1 动态注册Spring MVC路由
    // 这是关键步骤，将Skill的Controller注册到Spring MVC
    registerRoutes(context);
    
    // ==================== 阶段7: 启动Skill ====================
    
    // 7.1 调用生命周期回调
    startSkill(context);
    
    // ==================== 阶段8: 保存到激活列表 ====================
    
    // 8.1 保存到内存
    activePlugins.put(skillId, context);
    
    // ==================== 阶段9: 通知监听器 ====================
    
    // 9.1 通知状态变更
    notifyListeners(PluginState.INSTALLED, context);
    
    return PluginInstallResult.success(skillId);
}
```

### 2.3 卸载流程详解

```java
/**
 * 卸载Skill的完整流程
 * 位置: PluginManager.uninstallSkill()
 */
public synchronized PluginUninstallResult uninstallSkill(String skillId) {
    
    // ==================== 阶段1: 查找Skill ====================
    
    PluginContext context = activePlugins.get(skillId);
    if (context == null) {
        return PluginUninstallResult.failure(skillId, "Skill not found: " + skillId);
    }
    
    // ==================== 阶段2: 停止Skill ====================
    
    // 2.1 调用生命周期回调
    stopSkill(context);
    // 这会调用: lifecycle.onStop(context)
    // Skill应该在这里释放所有资源:
    // - 关闭数据库连接
    // - 关闭文件句柄
    // - 停止定时任务
    // - 清理缓存
    
    // ==================== 阶段3: 注销路由 ====================
    
    // 3.1 从Spring MVC注销所有路由
    unregisterRoutes(context);
    // 这会调用: handlerMapping.unregisterMapping(mappingInfo)
    // 所有API接口将不再可用
    
    // ==================== 阶段4: 注销服务 ====================
    
    // 4.1 从ServiceRegistry注销所有服务
    unregisterServices(context);
    
    // ==================== 阶段5: 关闭类加载器 ====================
    
    // 5.1 销毁类加载器
    classLoaderManager.destroyClassLoader(skillId);
    // 这会释放所有加载的类
    // 注意: 需要确保没有引用残留，否则会导致内存泄漏
    
    // ==================== 阶段6: 从激活列表移除 ====================
    
    // 6.1 从内存移除
    activePlugins.remove(skillId);
    
    // ==================== 阶段7: 通知监听器 ====================
    
    // 7.1 通知状态变更
    notifyListeners(PluginState.UNINSTALLED, context);
    
    return PluginUninstallResult.success(skillId);
}
```

### 2.4 更新流程详解

```java
/**
 * 更新Skill的完整流程
 * 位置: PluginManager.updateSkill()
 */
public synchronized PluginUpdateResult updateSkill(String skillId, SkillPackage newPackage) {
    
    // ==================== 阶段1: 备份旧版本 ====================
    
    PluginContext oldContext = activePlugins.get(skillId);
    if (oldContext == null) {
        return PluginUpdateResult.failure(skillId, "Skill not found: " + skillId);
    }
    
    // 备份旧上下文
    PluginContext backup = oldContext;
    
    // ==================== 阶段2: 卸载旧版本（保留配置）====================
    
    // 2.1 停止旧版本
    stopSkill(oldContext);
    
    // 2.2 注销旧路由
    unregisterRoutes(oldContext);
    
    // 2.3 注销旧服务
    unregisterServices(oldContext);
    
    // 注意: 不销毁类加载器，因为后面可能需要回滚
    
    // ==================== 阶段3: 安装新版本 ====================
    
    // 3.1 创建新类加载器
    PluginClassLoader newClassLoader = classLoaderManager.createClassLoader(newPackage);
    
    // 3.2 加载新配置
    SkillConfiguration newConfig = loadSkillConfiguration(newPackage, newClassLoader);
    
    // 3.3 合并配置（保留用户配置）
    newConfig.merge(backup.getConfiguration());
    // 这确保用户的自定义配置不会丢失
    
    // 3.4 创建新上下文
    PluginContext newContext = new PluginContext(skillId, newClassLoader, newConfig);
    
    // ==================== 阶段4: 注册新服务 ====================
    
    registerServices(newContext);
    
    // ==================== 阶段5: 注册新路由 ====================
    
    registerRoutes(newContext);
    
    // ==================== 阶段6: 启动新版本 ====================
    
    startSkill(newContext);
    
    // ==================== 阶段7: 替换上下文 ====================
    
    activePlugins.put(skillId, newContext);
    
    // ==================== 阶段8: 销毁旧类加载器 ====================
    
    classLoaderManager.destroyClassLoader(skillId + "_old");
    
    // ==================== 阶段9: 通知监听器 ====================
    
    notifyListeners(PluginState.UPDATED, newContext);
    
    return PluginUpdateResult.success(skillId);
}
```

---

## 📄 三、页面展开与合并逻辑

### 3.1 页面资源处理机制

#### 方案A: 静态资源内嵌到JAR

```yaml
# skill.yaml 配置
ui:
  type: html
  entry: index.html
  staticResources:
    - css/
    - js/
    - pages/
  cdnDependencies:
    - https://cdn.jsdelivr.net/npm/vue@3.0.0/dist/vue.global.js
```

**处理流程**:

```
1. 打包阶段:
   Skill JAR 结构:
   ├── META-INF/
   ├── skill.yaml
   ├── com/
   │   └── example/
   │       └── MyController.class
   └── static/
       ├── index.html
       ├── css/
       │   └── style.css
       └── js/
           └── app.js

2. 安装阶段:
   PluginManager.installSkill()
   └── UIConfiguration.load()
       └── 读取ui配置

3. 访问阶段:
   浏览器请求: /skill/{skillId}/index.html
   └── SkillResourceHandler (需要实现)
       └── 从JAR中读取static/index.html
           └── 返回给浏览器
```

#### 方案B: 页面合并到主工程（推荐）

```yaml
# skill.yaml 配置
spec:
  routes:
    - path: /api/skill/my-skill/hello
      method: GET
      controller: com.example.MyController
      methodName: hello
```

**处理流程**:

```
1. 主工程提供通用页面:
   主工程结构:
   ├── src/main/resources/static/console/
   │   ├── pages/
   │   │   ├── capability-discovery.html    # 能力发现（通用）
   │   │   ├── capability-management.html   # 能力管理（通用）
   │   │   ├── my-capabilities.html         # 我的能力（通用）
   │   │   └── scene-management.html        # 场景管理（通用）
   │   └── js/
   │       ├── api-client.js                # API客户端（通用）
   │       └── skill-discovery.js           # 发现逻辑（通用）

2. Skill只提供API:
   Skill JAR 结构:
   ├── skill.yaml
   └── com/
       └── example/
           └── MyController.class           # 只提供API

3. 页面与API分离:
   页面: 主工程提供（不打包到Skill JAR）
   API: Skill提供（动态注册到Spring MVC）
   数据: 通过API交互

4. 访问流程:
   浏览器请求: /console/pages/capability-discovery.html
   └── 返回通用页面
       └── 页面加载后调用API
           └── POST /api/v1/discovery/local
               └── PluginController处理
                   └── 返回JSON数据
                       └── 页面渲染
```

### 3.2 推荐方案：页面与API分离

**优势**:

1. **页面统一管理**: 所有页面在主工程中，便于维护和升级
2. **Skill轻量化**: Skill JAR只包含业务逻辑，体积更小
3. **避免冲突**: 页面资源不会冲突
4. **热更新友好**: 更新Skill不需要更新页面

**实现步骤**:

```
Step 1: 主工程提供通用页面
├── capability-discovery.html    # 能力发现页面（支持所有Skill）
├── capability-management.html   # 能力管理页面（支持所有Skill）
└── scene-management.html        # 场景管理页面（支持所有Skill）

Step 2: Skill只提供API
├── skill.yaml                   # 定义API路由
└── Controller类                 # 实现API逻辑

Step 3: 页面通过API与Skill交互
├── 页面加载
├── 调用API获取数据
└── 渲染数据到页面
```

---

## 🚀 四、标准迁移步骤与示例代码

### 4.1 迁移OS页面到Skills库

#### Step 1: 确定迁移范围

```yaml
# 需要迁移的页面（按优先级）
P0 - 核心页面（必须迁移）:
  - capability-discovery.html
  - capability-management.html
  - my-capabilities.html
  - scene-management.html

P1 - 重要页面（建议迁移）:
  - capability-detail.html
  - capability-activation.html
  - scene-detail.html

P2 - 其他页面（可选迁移）:
  - capability-logs.html
  - capability-stats.html
```

#### Step 2: 创建Skill模块

```xml
<!-- pom.xml -->
<project>
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-skills</artifactId>
        <version>0.7.3</version>
    </parent>
    
    <artifactId>skill-capability-management</artifactId>
    <name>Skill Capability Management</name>
    <description>能力管理技能 - 提供能力发现、管理、激活等功能</description>
    
    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Hotplug支持 -->
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>skill-hotplug-starter</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### Step 3: 编写skill.yaml

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-capability-management
  name: 能力管理技能
  version: 1.0.0
  description: 提供能力发现、管理、激活、绑定等完整的能力管理功能
  author: Ooder Team
  type: MANAGEMENT
  license: Apache-2.0

spec:
  skillForm: PROVIDER
  type: service-skill
  
  ownership: platform
  
  capability:
    address: 0x10
    category: MGMT
    code: MGMT_CAPABILITY
    operations: [discovery, install, activate, bind, invoke]
  
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
  
  lifecycle:
    startup: net.ooder.skill.management.CapabilityLifecycle
    shutdown: net.ooder.skill.management.CapabilityLifecycle
  
  routes:
    # 能力发现API
    - path: /api/v1/discovery/methods
      method: GET
      controller: net.ooder.skill.management.controller.DiscoveryController
      methodName: getDiscoveryMethods
      produces: application/json
    
    - path: /api/v1/discovery/local
      method: POST
      controller: net.ooder.skill.management.controller.DiscoveryController
      methodName: discoverLocal
      produces: application/json
    
    - path: /api/v1/discovery/install
      method: POST
      controller: net.ooder.skill.management.controller.DiscoveryController
      methodName: installCapability
      produces: application/json
    
    # 能力管理API
    - path: /api/v1/capabilities
      method: GET
      controller: net.ooder.skill.management.controller.CapabilityController
      methodName: listCapabilities
      produces: application/json
    
    - path: /api/v1/capabilities
      method: POST
      controller: net.ooder.skill.management.controller.CapabilityController
      methodName: registerCapability
      produces: application/json
    
    - path: /api/v1/capabilities/{id}
      method: GET
      controller: net.ooder.skill.management.controller.CapabilityController
      methodName: getCapability
      produces: application/json
    
    - path: /api/v1/capabilities/{id}/activate
      method: POST
      controller: net.ooder.skill.management.controller.CapabilityController
      methodName: activateCapability
      produces: application/json
  
  services:
    - name: capabilityService
      interface: net.ooder.skill.management.service.CapabilityService
      implementation: net.ooder.skill.management.service.impl.CapabilityServiceImpl
      singleton: true
    
    - name: discoveryService
      interface: net.ooder.skill.management.service.DiscoveryService
      implementation: net.ooder.skill.management.service.impl.DiscoveryServiceImpl
      singleton: true
  
  config:
    optional:
      - name: DISCOVERY_TIMEOUT
        type: integer
        default: 30000
        description: 发现超时时间(毫秒)
      
      - name: MAX_CONCURRENT_INSTALLATIONS
        type: integer
        default: 5
        description: 最大并发安装数
  
  resources:
    cpu: "200m"
    memory: "256Mi"
    storage: "100Mi"
```

#### Step 4: 实现Controller

```java
package net.ooder.skill.management.controller;

import net.ooder.skill.management.service.DiscoveryService;
import net.ooder.skill.management.model.DiscoveryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 能力发现Controller
 * 
 * 注意: 不需要添加@RestController注解
 * 路由由skill.yaml中的routes配置动态注册
 */
public class DiscoveryController {
    
    @Autowired
    private DiscoveryService discoveryService;
    
    /**
     * 获取发现方法列表
     * 对应路由: GET /api/v1/discovery/methods
     */
    public ResponseEntity<List<DiscoveryMethod>> getDiscoveryMethods() {
        List<DiscoveryMethod> methods = discoveryService.getDiscoveryMethods();
        return ResponseEntity.ok(methods);
    }
    
    /**
     * 本地发现
     * 对应路由: POST /api/v1/discovery/local
     */
    public ResponseEntity<DiscoveryResult> discoverLocal(@RequestBody Map<String, Object> params) {
        DiscoveryResult result = discoveryService.discoverLocal(params);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 安装能力
     * 对应路由: POST /api/v1/discovery/install
     */
    public ResponseEntity<InstallResult> installCapability(@RequestBody Map<String, Object> params) {
        String capabilityId = (String) params.get("capabilityId");
        InstallResult result = discoveryService.installCapability(capabilityId);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
```

#### Step 5: 实现Service

```java
package net.ooder.skill.management.service.impl;

import net.ooder.skill.management.service.DiscoveryService;
import net.ooder.skill.management.model.*;
import net.ooder.skill.hotplug.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscoveryServiceImpl implements DiscoveryService {
    
    @Autowired
    private PluginManager pluginManager;
    
    @Override
    public List<DiscoveryMethod> getDiscoveryMethods() {
        return List.of(
            new DiscoveryMethod("GITHUB", "GitHub", "ri-github-fill", "#333"),
            new DiscoveryMethod("GITEE", "Gitee", "ri-git-repository-line", "#C71D23"),
            new DiscoveryMethod("SKILL_CENTER", "技能中心", "ri-app-store-line", "#1890FF"),
            new DiscoveryMethod("LOCAL_FS", "本地文件", "ri-folder-line", "#FA8C16")
        );
    }
    
    @Override
    public DiscoveryResult discoverLocal(Map<String, Object> params) {
        // 实现本地发现逻辑
        List<CapabilityInfo> capabilities = scanLocalCapabilities();
        return new DiscoveryResult(capabilities);
    }
    
    @Override
    public InstallResult installCapability(String capabilityId) {
        // 实现安装逻辑
        // 可以调用 pluginManager.installSkill()
        return InstallResult.success(capabilityId);
    }
    
    private List<CapabilityInfo> scanLocalCapabilities() {
        // 扫描本地能力
        return List.of();
    }
}
```

#### Step 6: 实现生命周期

```java
package net.ooder.skill.management;

import net.ooder.skill.hotplug.SkillLifecycle;
import net.ooder.skill.hotplug.model.PluginContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 能力管理技能生命周期
 */
public class CapabilityLifecycle implements SkillLifecycle {
    
    private static final Logger logger = LoggerFactory.getLogger(CapabilityLifecycle.class);
    
    @Override
    public void onStart(PluginContext context) {
        logger.info("Capability Management Skill starting: {}", context.getSkillId());
        
        // 初始化资源
        // 例如: 创建必要的目录、初始化缓存、建立连接等
        
        logger.info("Capability Management Skill started successfully");
    }
    
    @Override
    public void onStop(PluginContext context) {
        logger.info("Capability Management Skill stopping: {}", context.getSkillId());
        
        // 释放资源
        // 例如: 关闭连接、清理缓存、停止定时任务等
        
        logger.info("Capability Management Skill stopped successfully");
    }
}
```

### 4.2 打包与部署

#### 打包Skill JAR

```bash
# 1. 编译
mvn clean compile

# 2. 打包
mvn package

# 3. 生成的JAR
target/skill-capability-management-1.0.0.jar
```

#### 部署Skill

```bash
# 方式1: 复制到plugins目录
cp target/skill-capability-management-1.0.0.jar ./plugins/

# 方式2: 通过API安装
curl -X POST http://localhost:8080/api/plugins/install \
  -F "file=@target/skill-capability-management-1.0.0.jar"

# 方式3: 通过页面安装
# 访问: http://localhost:8080/console/pages/capability-discovery.html
# 点击"安装"按钮，上传JAR文件
```

### 4.3 页面迁移方案

#### 方案A: 页面保留在主工程（推荐）

```
主工程:
├── src/main/resources/static/console/
│   ├── pages/
│   │   ├── capability-discovery.html    # 保留在主工程
│   │   ├── capability-management.html   # 保留在主工程
│   │   └── my-capabilities.html         # 保留在主工程
│   └── js/
│       ├── api-client.js                # 保留在主工程
│       └── capability-management.js     # 保留在主工程

Skill JAR:
├── skill.yaml                           # 只包含配置
└── com/
    └── example/
        └── Controller.class             # 只包含API实现
```

**优势**:
- 页面统一管理
- Skill轻量化
- 避免资源冲突

#### 方案B: 页面内嵌到Skill JAR

```
Skill JAR:
├── skill.yaml
├── static/
│   ├── index.html
│   ├── css/
│   └── js/
└── com/
    └── example/
        └── Controller.class
```

**需要实现**:
- SkillResourceHandler (从JAR读取静态资源)
- 资源路径映射 (如 /skill/{skillId}/**)

---

## 📝 五、总结与建议

### 5.1 关键发现

1. **OS已实现完整的能力管理体系**: 76个页面 + 70+个API接口
2. **Hotplug机制成熟**: 完整的安装/卸载/更新流程
3. **页面与API分离**: 推荐页面保留在主工程，Skill只提供API
4. **生命周期管理完善**: 提供onStart/onStop回调

### 5.2 迁移建议

1. **优先迁移核心功能**: 能力发现、能力管理、场景管理
2. **页面保留在主工程**: 避免资源冲突，便于维护
3. **Skill只提供API**: 轻量化，热更新友好
4. **遵循skill.yaml规范**: 确保配置完整

### 5.3 下一步行动

1. **创建Skill模块**: 按照示例创建skill-capability-management
2. **迁移API逻辑**: 将OS中的Controller迁移到Skill
3. **测试验证**: 确保API功能正常
4. **打包部署**: 生成JAR并部署

---

**文档维护**: Ooder Team  
**最后更新**: 2026-04-02  
**下次审核**: 2026-04-09
