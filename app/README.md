# Ooder Skills App 3.0.0

核心模块发布包，包含 MVP 应用所需的基础技能模块。

## 模块说明

### skill-common
通用工具模块，提供：
- Auth API - 认证接口
- Org API - 组织机构接口
- Config API - 配置接口
- Link API - 链接接口
- Participant API - 参与者接口
- SceneContext API - 场景上下文接口
- JsonStorage - JSON 存储服务
- Discovery - 技能发现机制
- Web 控制器 - 通用控制器

### skill-org-base
组织管理基础模块，提供：
- OrgSkill - 组织管理接口
- LocalOrgSkill - 本地 JSON 存储实现
- OrgInfo - 组织信息模型
- UserInfo - 用户信息模型
- PageRequest/PageResult - 分页支持

### skill-hotplug-starter
Spring Boot Starter，提供：
- PluginClassLoader - 插件类加载器
- PluginManager - 插件管理器
- SkillLifecycle - 技能生命周期
- RouteRegistry - 路由注册
- ServiceRegistry - 服务注册
- HotPlugAutoConfiguration - 自动配置

## 技术栈

| 组件 | 版本 |
|------|------|
| JDK | 21 |
| Spring Boot | 3.2.5 |
| agent-sdk-core | 3.0.0 |
| scene-engine | 3.0.0 |
| llm-sdk | 3.0.0 |
| fastjson2 | 2.0.57 |

## 使用方式

### Maven 依赖
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-common</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-org-base</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-hotplug-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

### Gradle (Kotlin DSL)
```kotlin
implementation("net.ooder:skill-common:3.0.0")
implementation("net.ooder:skill-org-base:3.0.0")
implementation("net.ooder:skill-hotplug-starter:3.0.0")
```

## 发布到 Maven 仓库

```bash
mvn clean deploy -DskipTests
```

## 仓库配置

在 `~/.m2/settings.xml` 中配置

```xml
<settings>
    <servers>
        <server>
            <id>ooder-releases</id>
            <username>your-username</username>
            <password>your-password</password>
        </server>
    </servers>
</settings>
```

## 版本历史

- 3.0.0 - JDK 21, Spring Boot 3.2.5, Jakarta EE 迁移