# 协作需求：scene-engine 模块改进

## 概述

**发起方**: mvp-core 团队  
**日期**: 2026-03-24  
**优先级**: 高  
**目标**: 移除重复的存储服务定义，依赖 skill-common 提供的基础能力

---

## 当前问题

### 1. 重复的 JsonStorageService 定义

`scene-engine` 中定义了 `net.ooder.scene.skill.engine.context.impl.JsonStorageService`，与 `skill-common` 中的 `net.ooder.skill.common.storage.JsonStorageService` 产生冲突。

**冲突点**:

| 模块 | 类路径 | 注解 | 接口 |
|------|--------|------|------|
| skill-common | `net.ooder.skill.common.storage.JsonStorageService` | `@Service` | 通用存储 |
| scene-engine | `net.ooder.scene.skill.engine.context.impl.JsonStorageService` | `@Service` | `ContextStorageService` |

### 2. API 不兼容

两个 `JsonStorageService` 提供的方法不同：

**skill-common 版本**:
```java
public <T> List<T> loadList(String key, Class<T> elementClass);
public <T> void saveList(String key, List<T> list);
public <T> Map<String, T> getAll(String key);
public void put(String key, String id, T entity);
public void remove(String key, String id);
```

**scene-engine 版本**:
```java
public void saveUserContext(String userId, Map<String, Object> context);
public Map<String, Object> getUserContext(String userId);
// 实现 ContextStorageService 接口
```

---

## 需求详情

### 需求1：重命名 scene-engine 的存储服务

**修改前**:
```java
// net.ooder.scene.skill.engine.context.impl.JsonStorageService
@Service
public class JsonStorageService implements ContextStorageService {
    // ...
}
```

**修改后**:
```java
// net.ooder.scene.skill.engine.context.impl.SceneContextStorageService
@Service
public class SceneContextStorageService implements ContextStorageService {
    // ...
}
```

### 需求2：依赖 skill-common 的存储服务

如果 `scene-engine` 需要通用存储能力，应该：

```java
@Service
public class SceneContextStorageService implements ContextStorageService {
    
    @Autowired
    private net.ooder.skill.common.storage.JsonStorageService storageService;
    
    // 使用 skill-common 的存储服务实现场景上下文功能
}
```

### 需求3：更新所有引用

需要更新以下文件中的引用：

```bash
# 搜索并替换
net.ooder.scene.skill.engine.context.impl.JsonStorageService
→
net.ooder.scene.skill.engine.context.impl.SceneContextStorageService
```

---

## 架构建议

### 推荐的模块依赖关系

```
┌─────────────────────────────────────────────────────────────┐
│                        mvp-core                              │
│  (业务模块 - 使用 skill-common 的存储服务)                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 依赖
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      scene-engine                            │
│  (场景引擎 - 使用 SceneContextStorageService)                 │
│  (可选依赖 skill-common 的存储服务)                           │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 依赖
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       skill-common                           │
│  (基础模块 - 提供通用存储服务 JsonStorageService)              │
│  (使用 @ConditionalOnMissingBean 避免冲突)                    │
└─────────────────────────────────────────────────────────────┘
```

### 职责划分

| 模块 | 职责 | 提供的服务 |
|------|------|-----------|
| skill-common | 通用存储能力 | `JsonStorageService` |
| scene-engine | 场景上下文管理 | `SceneContextStorageService` (实现 `ContextStorageService`) |
| mvp-core | 业务逻辑 | 使用 `JsonStorageService` |

---

## 验收标准

| 标准 | 验证方法 |
|------|----------|
| 无 Bean 名称冲突 | 启动应用不报 `jsonStorageService` 冲突错误 |
| API 兼容 | mvp-core 编译通过 |
| 功能正常 | scene-engine 的上下文存储功能正常工作 |
| 单一依赖 | mvp-core 无需 `exclude` 配置 |

---

## 迁移步骤

1. **重命名类**: `JsonStorageService` → `SceneContextStorageService`
2. **更新引用**: 搜索并替换所有引用
3. **添加依赖**: 如需通用存储，注入 `skill-common` 的 `JsonStorageService`
4. **测试验证**: 运行单元测试确保功能正常
5. **发布新版本**: 更新版本号并发布

---

## 影响范围

- **mvp-core**: 可移除 `exclude` 配置，解决 Bean 冲突
- **scene-engine**: 需要重命名类并更新引用
- **其他依赖模块**: 检查是否有引用 `scene-engine` 的 `JsonStorageService`

---

## 联系方式

如有疑问，请联系 mvp-core 团队。

---

*文档版本: 1.0*  
*最后更新: 2026-03-24*
