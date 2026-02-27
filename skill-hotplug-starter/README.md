# Skill Hot Plug Starter

Ooder Skill 热插拔支持模块，提供无需重启的 Skill 动态安装、卸载、更新能力。

## 功能特性

- **动态安装**: 运行时安装新的 Skill，无需重启应用
- **动态卸载**: 安全卸载已安装的 Skill，释放资源
- **热更新**: 支持 Skill 版本更新，平滑过渡
- **类隔离**: 每个 Skill 拥有独立的 ClassLoader，避免类冲突
- **服务代理**: 动态服务注册与发现
- **路由动态注册**: Spring MVC 路由动态注册与注销

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-hotplug-starter</artifactId>
    <version>0.7.3</version>
</dependency>
```

### 2. 配置属性

```yaml
ooder:
  skill:
    hotplug:
      enabled: true              # 启用热插拔
      plugin-directory: ./plugins # 插件目录
      auto-load: true            # 自动加载插件目录中的插件
      class-loader-cache-size: 100 # 类加载器缓存大小
      isolation-enabled: true    # 启用插件隔离
```

### 3. 使用 PluginManager

```java
@Autowired
private PluginManager pluginManager;

// 安装 Skill
SkillPackage skillPackage = SkillPackage.fromFile(new File("skill-xxx.jar"));
PluginInstallResult result = pluginManager.installSkill(skillPackage);

// 卸载 Skill
PluginUninstallResult result = pluginManager.uninstallSkill("skill-xxx");

// 更新 Skill
PluginUpdateResult result = pluginManager.updateSkill("skill-xxx", newPackage);

// 获取已安装的 Skill 列表
List<PluginInfo> plugins = pluginManager.getInstalledSkills();
```

## REST API

热插拔模块提供以下 REST API：

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/plugins` | GET | 获取所有已安装的插件 |
| `/api/plugins/{skillId}` | GET | 获取指定插件信息 |
| `/api/plugins/install` | POST | 上传并安装插件 |
| `/api/plugins/{skillId}/uninstall` | POST | 卸载插件 |
| `/api/plugins/{skillId}/update` | POST | 更新插件 |
| `/api/plugins/{skillId}/status` | GET | 获取插件状态 |
| `/api/plugins/{skillId}/check` | GET | 检查插件是否已安装 |

## Skill 包结构

支持热插拔的 Skill 包需要包含 `skill.yaml` 配置文件：

```yaml
id: skill-example
name: Example Skill
version: 1.0.0
description: An example skill
author: Ooder Team
type: service

dependencies:
  - dependency1.jar
  - dependency2.jar

lifecycle:
  startup: com.example.ExampleSkillStartup
  shutdown: com.example.ExampleSkillShutdown

routes:
  - path: /api/example/hello
    method: GET
    controller: com.example.ExampleController
    methodName: hello
    produces: application/json

services:
  - name: exampleService
    interface: com.example.ExampleService
    implementation: com.example.ExampleServiceImpl
    singleton: true

ui:
  type: html
  entry: index.html
  staticResources:
    - css/
    - js/
  cdnDependencies:
    - https://cdn.example.com/lib.js
```

## 架构说明

### 核心组件

1. **PluginManager**: 插件管理器，负责安装、卸载、更新
2. **PluginClassLoader**: 插件类加载器，实现类隔离
3. **ClassLoaderManager**: 类加载器管理器
4. **RouteRegistry**: 路由注册器，动态注册 Spring MVC 路由
5. **ServiceRegistry**: 服务注册器，管理服务代理

### 类加载机制

```
Bootstrap ClassLoader
       ↑
Extension ClassLoader
       ↑
Application ClassLoader (Parent)
       ↑
PluginClassLoader (Skill 专用)
```

- 系统类（java.*, javax.*, org.springframework.* 等）由父类加载器加载
- Skill 自己的类由 PluginClassLoader 加载
- 实现类隔离，避免版本冲突

### 生命周期

```
INSTALLING → INSTALLED → STARTING → ACTIVE
                                    ↓
UNINSTALLED ← STOPPING ← STOPPED ← ERROR
```

## 注意事项

1. **内存泄漏**: 卸载 Skill 后，建议触发一次 Full GC 以确保类加载器被回收
2. **线程安全**: 卸载 Skill 前确保没有正在执行的请求
3. **资源释放**: Skill 应在 `onStop` 回调中释放所有资源
4. **数据库连接**: Skill 使用数据库连接池时，卸载时需要关闭连接

## 示例代码

### 实现生命周期接口

```java
public class MySkillLifecycle implements SkillLifecycle {
    
    @Override
    public void onStart(PluginContext context) {
        System.out.println("Skill started: " + context.getSkillId());
        // 初始化资源
    }
    
    @Override
    public void onStop(PluginContext context) {
        System.out.println("Skill stopped: " + context.getSkillId());
        // 释放资源
    }
}
```

### 监听状态变更

```java
pluginManager.addStateListener((state, context) -> {
    System.out.println("Skill " + context.getSkillId() + " state changed to: " + state);
});
```

## 版本历史

- **0.7.3**: 初始版本，支持基本的安装/卸载/更新功能
