# 依赖冲突分析报告

## 概述

**项目**: mvp-core  
**日期**: 2026-03-24  
**状态**: 需要各团队协作解决

---

## 依赖关系图

```
mvp-core (3.0.0)
├── skill-common (3.0.0)
│   ├── net.ooder.skill.common.api.AuthApi
│   └── net.ooder.skill.common.storage.JsonStorageService
├── scene-engine (3.0.0)
│   ├── net.ooder.scene.core.auth.AuthController
│   └── net.ooder.scene.skill.engine.context.impl.JsonStorageService
├── agent-sdk-core (3.0.0)
├── skill-org-base (3.0.0)
├── skill-hotplug-starter (3.0.0)
└── skills-framework (3.0.0)
```

---

## 冲突详情

### 1. API 路径冲突 - `/api/v1/auth/login`

| 来源 | 类名 | 路径 | Bean名称 |
|------|------|------|----------|
| skill-common | `net.ooder.skill.common.api.AuthApi` | `/api/v1/auth/login` | `authApi` |
| mvp-core (已删除) | `net.ooder.mvp.skill.scene.controller.AuthController` | `/api/v1/auth/login` | `authController` |
| scene-engine | `net.ooder.scene.core.auth.AuthController` | `/api/v1/auth/roles` | - |

**问题**: `skill-common` 和 `mvp-core` 都定义了 `/api/v1/auth/login` 端点

**解决方案**: 
- 已删除 `mvp-core` 中的 `AuthController`
- 使用 `skill-common` 中的 `AuthApi`

---

### 2. JsonStorageService Bean 冲突

| 来源 | 类名 | 注解 | 实现接口 |
|------|------|------|----------|
| skill-common | `net.ooder.skill.common.storage.JsonStorageService` | `@Service` | - |
| scene-engine | `net.ooder.scene.skill.engine.context.impl.JsonStorageService` | `@Service` | `ContextStorageService` |
| mvp-core | `CapabilityConfig.jsonStorageService()` | `@Bean` | - |

**问题**: 三个不同包路径的 `JsonStorageService` 类，两个带有 `@Service` 注解

**解决方案**: 
- 移除 `mvp-core` 中的 `@Bean` 定义
- 使用 `@Autowired` 注入外部依赖中的 Bean

---

### 3. @Primary 冲突

| 来源 | Bean | 注解 |
|------|------|------|
| mvp-core | `CapabilityStateService` | `@Primary` |
| scene-engine | 多个 Bean | `@Primary` |

**问题**: 多个 `@Primary` 注解导致 Bean 选择冲突

---

## 需要各团队配合的事项

### skill-common 团队

1. **AuthApi 接口规范**
   - 确认 `/api/v1/auth/login` 接口参数和返回值
   - 确认 `/api/v1/auth/session` 接口是否存在
   - 建议: 添加接口文档说明

2. **JsonStorageService**
   - 确认 `net.ooder.skill.common.storage.JsonStorageService` 的用途
   - 建议: 考虑使用 `@ConditionalOnMissingBean` 避免冲突

### scene-engine 团队

1. **AuthController**
   - 当前只提供 `/api/v1/auth/roles` 端点
   - 建议: 与 `skill-common` 的 `AuthApi` 合并或明确分工

2. **JsonStorageService**
   - `net.ooder.scene.skill.engine.context.impl.JsonStorageService` 实现了 `ContextStorageService`
   - 建议: 重命名避免与 `skill-common` 冲突

3. **自动配置**
   - `spring.factories` 中定义了多个自动配置类
   - 建议: 提供 `exclude` 配置选项

### mvp-core 团队 (本地)

1. **已完成**
   - 删除重复的 `AuthController`
   - 修改 `CapabilityConfig` 使用 `@Autowired` 注入

2. **待处理**
   - 检查所有 `@Primary` 注解使用
   - 确认 `@Lazy` 是否影响 Native 打包

---

## 临时解决方案

### 1. 排除冲突的自动配置

```java
@SpringBootApplication(exclude = {
    // 排除冲突的自动配置类
})
```

### 2. 使用 @Qualifier 指定 Bean

```java
@Autowired
@Qualifier("jsonStorageService")
private JsonStorageService jsonStorageService;
```

### 3. 条件化 Bean 创建

```java
@Bean
@ConditionalOnMissingBean
public JsonStorageService jsonStorageService() {
    return new JsonStorageService();
}
```

---

## 文件路径参考

| 文件 | 绝对路径 |
|------|----------|
| CapabilityConfig | `e:\github\ooder-skills\mvp\src\main\java\net\ooder\mvp\skill\scene\capability\config\CapabilityConfig.java` |
| AuthMenuController | `e:\github\ooder-skills\mvp\src\main\java\net\ooder\mvp\skill\scene\controller\AuthMenuController.java` |
| SceneEngineIntegration | `e:\github\ooder-skills\mvp\src\main\java\net\ooder\mvp\skill\scene\integration\SceneEngineIntegration.java` |
| MvpCoreApplication | `e:\github\ooder-skills\mvp\src\main\java\net\ooder\mvp\MvpCoreApplication.java` |

---

## 下一步行动

1. **skill-common 团队**: 确认 API 规范，考虑添加 `@ConditionalOnMissingBean`
2. **scene-engine 团队**: 重命名 `JsonStorageService`，提供排除配置选项
3. **mvp-core 团队**: 完成本地修复，测试启动流程

---

*报告生成时间: 2026-03-24 19:10*
