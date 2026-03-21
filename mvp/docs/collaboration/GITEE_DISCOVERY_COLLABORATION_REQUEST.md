# Gitee 技能发现功能协作开发请求

## 一、问题描述

### 1.1 当前状态

MVP 项目已完成 SE SDK 2.3.1 升级，但 Gitee 技能发现功能返回 0 条记录。

**日志显示**：
```
[GitRepositoryDiscovererAdapter] Discovering skills from Git repository: null/null
[DiscoveryController] Discovered 0 skills from Gitee via SE SDK
```

### 1.2 问题分析

| 问题 | 说明 |
|------|------|
| 配置未传递 | `GitRepositoryDiscovererAdapter` 显示 `null/null`，说明 owner/repo 配置未生效 |
| API 不匹配 | `OoderSdk.getUnifiedDiscoveryService()` 方法不存在 |
| Bean 注册问题 | `SkillPackageManagerImpl` 没有注册 Git 发现器的方法 |

---

## 二、MVP 项目当前实现

### 2.1 配置文件 (application.yml)

```yaml
scene:
  engine:
    discovery:
      enabled: true
      gitee:
        enabled: true
        token: f0d11903a8e10e3ce09d51bc9552b664
        default-owner: ooderCN
        default-repo: skills
        default-branch: main
        skills-path: ""
        cache-ttl-ms: 3600000
```

### 2.2 SdkConfiguration.java

```java
@Value("${scene.engine.discovery.gitee.token:}")
private String giteeToken;

@Value("${scene.engine.discovery.gitee.default-owner:ooderCN}")
private String giteeOwner;

@Value("${scene.engine.discovery.gitee.default-repo:skills}")
private String giteeSkillsRepo;

@Bean
@ConditionalOnProperty(name = "scene.engine.discovery.enabled", havingValue = "true", matchIfMissing = true)
public UnifiedDiscoveryService unifiedDiscoveryService() {
    UnifiedDiscoveryServiceImpl service = new UnifiedDiscoveryServiceImpl();
    if (giteeToken != null && !giteeToken.isEmpty()) {
        service.configureGitee(giteeToken, giteeOwner, giteeSkillsRepo, "main", "");
    }
    return service;
}
```

### 2.3 DiscoveryController.java

```java
@PostMapping("/gitee")
public ResultModel<GitDiscoveryResultDTO> discoverFromGitee(...) {
    if (unifiedDiscoveryService != null) {
        List<SkillPackage> skills = unifiedDiscoveryService
            .discoverSkills("https://gitee.com/ooderCN/skills")
            .get(60, TimeUnit.SECONDS);
    }
}
```

---

## 三、SE SDK 问题确认

### 3.1 问题1：Gitee配置默认禁用

**文件**：`DiscoveryProperties.java:20`

```java
// 修改前
private boolean enabled = false;

// 修改后
private boolean enabled = true;
```

**影响**：Gitee 发现功能默认禁用，需要手动启用。

### 3.2 问题2：CacheEntry序列化错误

**错误信息**：
```
NoClassDefFoundError: net/ooder/scene/discovery/cache/JsonFileCacheManager$CacheEntry
```

**原因**：私有内部类 + Java原生序列化导致类加载问题。

**修复方案**：
- 改用 Jackson JSON序列化 替代 Java原生序列化
- `CacheEntry` 改为 `public static class`
- 缓存文件扩展名从 `.cache` 改为 `.json`

---

## 四、修复状态

| 问题 | 状态 | 修复版本 |
|------|------|----------|
| Gitee配置默认禁用 | ✅ 已修复 | SE SDK 2.3.1 |
| CacheEntry序列化错误 | ✅ 已修复 | SE SDK 2.3.1 |
| MVP 集成 | ⏳ 待更新 | 等待 SE SDK 2.3.1 发布 |

---

## 五、MVP 项目待更新

### 5.1 更新 SE SDK 版本

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 5.2 验证配置

```yaml
scene:
  engine:
    discovery:
      enabled: true
      gitee:
        enabled: true  # 现在默认为 true
        token: ${GITEE_TOKEN:}
        default-owner: ooderCN
        default-repo: skills
```

---

## 六、相关文档

- [SE SDK Gitee 发现器文档](file:///E:/github/ooder-sdk/scene-engine/docs/v2.3.1/10-integration/04-gitee-discovery.md)
- [SE SDK 2.3.1 升级计划](file:///e:/github/ooder-skills/mvp/docs/upgrade/SE_SDK_2.3.1_UPGRADE_PLAN.md)

---

**文档版本**: 2.0  
**更新日期**: 2026-03-21  
**状态**: 🟢 SE SDK 2.3.1 已发布
