# SE SDK 2.3.1 协作请求：SceneGroupManager 实现缺失

## 致：SE SDK 团队

**日期**: 2026-03-19  
**发起方**: MVP前端团队  
**优先级**: 🔴 高 - 阻塞MVP启动

---

## 问题描述

MVP项目在启动时因缺少 `SceneGroupManager` bean而失败。SE SDK 2.3.1 提供了接口定义，但未提供实现类和自动配置。

### 错误信息

```
Error creating bean with name 'sceneGroupManager': 
SceneGroupManager bean not provided by SE SDK 2.3.1

Required SE SDK classes:
- net.ooder.scene.group.SceneGroupManager (interface exists ✅)
- SceneGroupManagerImpl (implementation missing ❌)
- SceneGroupManagerAutoConfiguration (auto-config missing ❌)
```

---

## 当前状态分析

| 组件 | 包路径 | 状态 | 说明 |
|------|--------|------|------|
| SceneGroupManager接口 | `net.ooder.scene.group.SceneGroupManager` | ✅ 存在 | SE SDK提供 |
| SceneGroupManagerImpl实现 | `net.ooder.scene.group.SceneGroupManagerImpl` | ❌ 缺失 | 需SE SDK提供 |
| 自动配置类 | `net.ooder.scene.group.SceneGroupManagerAutoConfiguration` | ❌ 缺失 | 需SE SDK提供 |

---

## 依赖关系

```
MVP Application
    └── SceneGroupServiceSEImpl (MVP实现)
            └── SceneGroupManager (SE SDK接口) ← 需要SE SDK提供实现
                    ├── createSceneGroup()
                    ├── getSceneGroup()
                    ├── destroySceneGroup()
                    ├── getAllSceneGroups()
                    ├── getSceneGroupsByTemplate()
                    ├── activateSceneGroup()
                    ├── suspendSceneGroup()
                    ├── addParticipant()
                    ├── removeParticipant()
                    └── getParticipants()
```

---

## 请求SE SDK团队提供

### 1. SceneGroupManagerImpl 实现类

```java
package net.ooder.scene.group;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SceneGroupManagerImpl implements SceneGroupManager {
    
    private final Map<String, SceneGroup> sceneGroups = new ConcurrentHashMap<>();

    @Override
    public SceneGroup createSceneGroup(String sceneGroupId, String templateId, 
                                        String creatorId, SceneGroup.CreatorType creatorType) {
        // SE SDK实现
    }

    @Override
    public SceneGroup getSceneGroup(String sceneGroupId) {
        // SE SDK实现
    }

    @Override
    public void destroySceneGroup(String sceneGroupId) {
        // SE SDK实现
    }

    @Override
    public List<SceneGroup> getAllSceneGroups() {
        // SE SDK实现
    }

    @Override
    public List<SceneGroup> getSceneGroupsByTemplate(String templateId) {
        // SE SDK实现
    }

    @Override
    public void activateSceneGroup(String sceneGroupId) {
        // SE SDK实现
    }

    @Override
    public void suspendSceneGroup(String sceneGroupId) {
        // SE SDK实现
    }

    @Override
    public void addParticipant(String sceneGroupId, Participant participant) {
        // SE SDK实现
    }

    @Override
    public void removeParticipant(String sceneGroupId, String participantId) {
        // SE SDK实现
    }

    @Override
    public Participant getParticipant(String sceneGroupId, String participantId) {
        // SE SDK实现
    }
}
```

### 2. 自动配置类

```java
package net.ooder.scene.group;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SceneGroupManagerAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public SceneGroupManager sceneGroupManager() {
        return new SceneGroupManagerImpl();
    }
}
```

### 3. spring.factories 注册

在 `META-INF/spring.factories` 中添加：

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  net.ooder.scene.group.SceneGroupManagerAutoConfiguration
```

---

## 影响范围

此问题阻塞以下MVP功能：

| 功能 | API路径 | 影响 |
|------|---------|------|
| 场景组管理 | `/api/v1/scene-groups` | ❌ 无法使用 |
| 场景知识绑定 | `/api/v1/scene-groups/{id}/knowledge` | ❌ 无法使用 |
| 场景LLM配置 | `/api/v1/scene-groups/{id}/llm/config` | ❌ 无法使用 |
| 参与者管理 | `/api/v1/scene-groups/{id}/participants` | ❌ 无法使用 |

---

## 临时解决方案（不推荐）

按照SE SDK分层原则，MVP不应自己实现SE SDK接口。但如需临时启动，MVP可提供fallback实现：

```java
// 仅用于开发环境临时启动，生产环境必须由SE SDK提供
@Bean
@ConditionalOnMissingBean
public SceneGroupManager sceneGroupManager() {
    log.warn("Using MVP fallback SceneGroupManager - NOT FOR PRODUCTION");
    return new MvpFallbackSceneGroupManager();
}
```

---

## 联系方式

- MVP团队文档：[MVP-INTEGRATION-GUIDE.md](file:///e:/github/ooder-skills/mvp/docs/MVP-INTEGRATION-GUIDE.md)
- 需求文档：[KNOWLEDGE_BASE_EXTENSION_REQUIREMENTS.md](file:///e:/github/ooder-skills/mvp/docs/KNOWLEDGE_BASE_EXTENSION_REQUIREMENTS.md)

---

## 相关文件链接

| 文件 | 路径 |
|------|------|
| MVP配置类 | [SceneEngineConfig.java](file:///e:/github/ooder-skills/mvp/src/main/java/net/ooder/mvp/skill/scene/config/SceneEngineConfig.java) |
| MVP服务实现 | [SceneGroupServiceSEImpl.java](file:///e:/github/ooder-skills/mvp/src/main/java/net/ooder/mvp/skill/scene/service/impl/SceneGroupServiceSEImpl.java) |
| 知识库Controller | [KnowledgeBaseController.java](file:///e:/github/ooder-skills/mvp/src/main/java/net/ooder/mvp/skill/scene/controller/KnowledgeBaseController.java) |
| 场景知识Controller | [SceneKnowledgeController.java](file:///e:/github/ooder-skills/mvp/src/main/java/net/ooder/mvp/skill/scene/controller/SceneKnowledgeController.java) |

---

**请SE SDK团队确认并提供实现时间表。**
