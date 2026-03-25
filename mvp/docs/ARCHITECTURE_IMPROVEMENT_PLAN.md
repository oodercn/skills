# 架构改进方案：依赖单一加载

## 概述

**发起方**: mvp-core 团队  
**日期**: 2026-03-24  
**状态**: 待协调

---

## 问题背景

当前 `mvp-core` 模块需要使用 `exclude` 配置来避免 Bean 冲突，这不是一个可持续的解决方案：

```java
@ComponentScan(
    basePackages = {...},
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "net\\.ooder\\.skill\\.capability\\.controller\\.CapabilityStatsController"
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "net\\.ooder\\.mvp\\.skill\\.scene\\.SceneSkillApplication"
        )
    }
)
```

**问题**:
1. 依赖关系不清晰
2. 模块间耦合严重
3. 维护成本高
4. 可能隐藏其他问题

---

## 目标架构

### 模块依赖关系

```
                    ┌──────────────────┐
                    │    mvp-core      │
                    │   (业务模块)      │
                    └────────┬─────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
    ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
    │scene-engine │  │skill-common │  │skill-capability│
    │ (场景引擎)   │  │ (基础模块)   │  │  (能力模块)   │
    └──────┬──────┘  └─────────────┘  └──────────────┘
           │
           ▼
    ┌─────────────┐
    │skill-common │
    │ (基础模块)   │
    └─────────────┘
```

### 职责划分

| 模块 | 职责 | 提供的服务 |
|------|------|-----------|
| **skill-common** | 通用基础能力 | `JsonStorageService` (通用存储) |
| **scene-engine** | 场景引擎能力 | `SceneContextStorageService` (场景上下文) |
| **skill-capability** | 能力管理 | 能力注册、绑定、状态管理 |
| **mvp-core** | 业务逻辑 | 使用各模块提供的服务 |

---

## 改进方案

### 1. skill-common 改进

**目标**: 作为基础模块，提供条件化加载

```java
@Configuration
@ConditionalOnProperty(name = "skill.common.enabled", havingValue = "true", matchIfMissing = true)
public class SkillCommonAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean(JsonStorageService.class)
    public JsonStorageService jsonStorageService() {
        return new JsonStorageService();
    }
}
```

**详细需求**: 见 [COLLABORATION_REQUEST_SKILL_COMMON.md](./COLLABORATION_REQUEST_SKILL_COMMON.md)

### 2. scene-engine 改进

**目标**: 移除重复的存储服务定义

```java
// 重命名: JsonStorageService → SceneContextStorageService
@Service
public class SceneContextStorageService implements ContextStorageService {
    // 场景上下文专用存储
}
```

**详细需求**: 见 [COLLABORATION_REQUEST_SCENE_ENGINE.md](./COLLABORATION_REQUEST_SCENE_ENGINE.md)

### 3. skill-capability 改进

**目标**: 使用条件化加载控制端点

```java
@RestController
@ConditionalOnProperty(name = "skill.capability.stats.enabled", havingValue = "true", matchIfMissing = true)
public class CapabilityStatsController {
    // 统计端点
}
```

### 4. mvp-core 改进

**目标**: 移除所有 `exclude` 配置

```java
@SpringBootApplication
@EnableConfigurationProperties(GiteeDiscoveryProperties.class)
@ComponentScan(
    basePackages = {
        "net.ooder.mvp",
        "net.ooder.mvp.skill.scene",
        "net.ooder.skill.common",
        "net.ooder.skill.capability",
        "net.ooder.skill.llm",
        "net.ooder.skill.hotplug",
        "net.ooder.skill.org"
    }
    // 无需 excludeFilters
)
public class MvpCoreApplication {
    // ...
}
```

---

## 配置规范

### application.yml 配置示例

```yaml
# skill-common 配置
skill:
  common:
    enabled: true
    storage:
      path: ./data

# scene-engine 配置
scene:
  engine:
    context:
      storage:
        root: ./data/scene

# skill-capability 配置
skill:
  capability:
    stats:
      enabled: false  # 禁用统计端点
```

---

## 实施计划

| 阶段 | 任务 | 负责团队 | 预计时间 |
|------|------|----------|----------|
| 1 | skill-common 添加条件化加载 | skill-common 团队 | 1天 |
| 2 | scene-engine 重命名存储服务 | scene-engine 团队 | 1天 |
| 3 | skill-capability 添加条件化加载 | skill-capability 团队 | 0.5天 |
| 4 | mvp-core 移除 exclude 配置 | mvp-core 团队 | 0.5天 |
| 5 | 集成测试 | 所有团队 | 1天 |

---

## 验收标准

| 标准 | 验证方法 |
|------|----------|
| 无 Bean 冲突 | 启动应用不报错 |
| 无 exclude 配置 | `MvpCoreApplication` 无 `excludeFilters` |
| 配置可控 | 通过配置文件控制功能开关 |
| 功能正常 | 所有模块功能正常工作 |

---

## 相关文档

- [skill-common 协作需求](./COLLABORATION_REQUEST_SKILL_COMMON.md)
- [scene-engine 协作需求](./COLLABORATION_REQUEST_SCENE_ENGINE.md)

---

*文档版本: 1.0*  
*最后更新: 2026-03-24*
