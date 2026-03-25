# 协作需求：skill-common 模块改进

## 概述

**发起方**: mvp-core 团队  
**日期**: 2026-03-24  
**优先级**: 高  
**目标**: 解决 Bean 冲突问题，实现依赖单一加载

---

## 当前问题

### 1. Bean 名称冲突

`JsonStorageService` 在多个模块中被重复定义，导致 Spring 容器启动失败：

```
Description:
The bean 'jsonStorageService', defined in class path resource 
[net/ooder/skill/common/config/SkillCommonAutoConfiguration.class], 
could not be registered. A bean with that name has already been defined.
```

### 2. 缺少条件化加载

当前 `SkillCommonAutoConfiguration` 没有提供条件化加载机制，导致：
- 无法按需启用/禁用功能
- 与其他模块的同类 Bean 冲突
- 业务模块需要使用 `exclude` 配置绕过问题

---

## 需求详情

### 需求1：添加 `@ConditionalOnMissingBean` 注解

**文件**: `net.ooder.skill.common.config.SkillCommonAutoConfiguration`

**修改前**:
```java
@Configuration
public class SkillCommonAutoConfiguration {
    
    @Bean
    public JsonStorageService jsonStorageService() {
        return new JsonStorageService();
    }
}
```

**修改后**:
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

### 需求2：添加功能开关配置

**文件**: `META-INF/spring-configuration-metadata.json` (新增)

```json
{
  "properties": [
    {
      "name": "skill.common.enabled",
      "type": "java.lang.Boolean",
      "description": "Enable skill-common auto configuration",
      "defaultValue": true
    },
    {
      "name": "skill.common.storage.path",
      "type": "java.lang.String",
      "description": "Storage path for JSON files",
      "defaultValue": "./data"
    }
  ]
}
```

### 需求3：确保 JsonStorageService 是唯一实现

**要求**:
- `skill-common` 是 `JsonStorageService` 的**唯一提供者**
- 其他模块（如 scene-engine）不应再定义同名 Bean
- 如果其他模块需要扩展，应使用装饰器模式或继承

---

## API 规范

### JsonStorageService 接口定义

请确认 `JsonStorageService` 提供以下方法：

```java
public class JsonStorageService {
    
    // 列表操作
    public <T> List<T> loadList(String key, Class<T> elementClass);
    public <T> void saveList(String key, List<T> list);
    
    // Map 操作
    public <T> Map<String, T> getAll(String key);
    public <T> void put(String key, String id, T entity);
    public void remove(String key, String id);
    public void clear(String key);
    
    // 配置
    public String getStorageRoot();
}
```

---

## 验收标准

| 标准 | 验证方法 |
|------|----------|
| 无 Bean 冲突 | 启动应用不报错 |
| 条件化加载 | 设置 `skill.common.enabled=false` 后不加载 |
| 单一依赖 | 移除 mvp-core 中的所有 `exclude` 配置 |
| API 兼容 | mvp-core 编译通过 |

---

## 影响范围

- **mvp-core**: 可移除 `exclude` 配置
- **scene-engine**: 需移除重复的 `JsonStorageService` 定义
- **其他依赖模块**: 无影响（向后兼容）

---

## 联系方式

如有疑问，请联系 mvp-core 团队。

---

*文档版本: 1.0*  
*最后更新: 2026-03-24*
