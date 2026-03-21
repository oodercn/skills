# DTO 重构方案：抽象与统一

## 一、现有 DTO 重复分析

### 1.1 DTO 数量统计

| 分类 | 数量 | 说明 |
|------|:----:|------|
| discovery 包 | 12 | 能力发现相关 |
| scene 包 | 50+ | 场景相关 |
| llm 包 | 15 | LLM 相关 |
| knowledge 包 | 10 | 知识库相关 |
| 其他 | 20+ | 杂项 |
| **总计** | **110+** | |

### 1.2 重复属性识别

| 属性 | 出现的 DTO | 重复次数 |
|------|-----------|:--------:|
| `id` / `capabilityId` / `capId` | CapabilityDTO, CapabilityInfoDTO, SceneInfoDTO... | 15+ |
| `name` | 几乎所有 DTO | 50+ |
| `description` | 几乎所有 DTO | 40+ |
| `status` | CapabilityDTO, SceneInfoDTO, SkillInfoDTO... | 20+ |
| `createdAt` / `updatedAt` | 多个 DTO | 15+ |
| `type` / `capabilityType` | CapabilityDTO, CapabilityInfoDTO | 5+ |
| `category` / `capabilityCategory` | CapabilityDTO | 3+ |

### 1.3 属性命名不一致

| DTO | 属性1 | 属性2 | 问题 |
|-----|-------|-------|------|
| CapabilityDTO | `id` | `capabilityId` | ❌ 重复 |
| CapabilityDTO | `type` | - | ⚠️ 应为 `capabilityType` |
| CapabilityDTO | `category` | `capabilityCategory` | ❌ 重复 |
| CapabilityInfoDTO | `capId` | - | ⚠️ 应为 `capabilityId` |
| SceneInfoDTO | `sceneId` | - | ✅ 正确 |
| SkillInfoDTO | `skillId` | - | ✅ 正确 |

---

## 二、DTO 抽象基类设计

### 2.1 基类层次结构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           DTO 抽象层次结构                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                      ┌───────────────────┐                                  │
│                      │   BaseDTO (抽象)   │                                  │
│                      │───────────────────│                                  │
│                      │ id: String        │                                  │
│                      │ name: String      │                                  │
│                      │ description: String│                                 │
│                      └─────────┬─────────┘                                  │
│                                │                                            │
│          ┌─────────────────────┼─────────────────────┐                      │
│          │                     │                     │                      │
│          ▼                     ▼                     ▼                      │
│  ┌───────────────┐    ┌───────────────┐    ┌───────────────┐               │
│  │IdentifiedDTO  │    │ StatusDTO     │    │ TimestampedDTO │               │
│  │───────────────│    │───────────────│    │───────────────│               │
│  │ + xxxId       │    │ + status      │    │ + createdAt   │               │
│  │               │    │               │    │ + updatedAt   │               │
│  └───────┬───────┘    └───────┬───────┘    └───────┬───────┘               │
│          │                    │                    │                        │
│          └────────────────────┼────────────────────┘                        │
│                               │                                             │
│                               ▼                                             │
│                    ┌─────────────────────┐                                  │
│                    │  FullDTO (组合抽象)  │                                  │
│                    │─────────────────────│                                  │
│                    │ 继承所有基类属性     │                                  │
│                    └──────────┬──────────┘                                  │
│                               │                                             │
│          ┌────────────────────┼────────────────────┐                        │
│          │                    │                    │                        │
│          ▼                    ▼                    ▼                        │
│  ┌───────────────┐    ┌───────────────┐    ┌───────────────┐               │
│  │CapabilityDTO  │    │  SkillDTO     │    │  SceneDTO     │               │
│  │───────────────│    │───────────────│    │───────────────│               │
│  │ + 特有属性    │    │ + 特有属性    │    │ + 特有属性    │               │
│  └───────────────┘    └───────────────┘    └───────────────┘               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 基类定义

#### BaseDTO.java

```java
package net.ooder.mvp.skill.scene.dto.base;

public abstract class BaseDTO {
    protected String id;
    protected String name;
    protected String description;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
```

#### IdentifiedDTO.java

```java
package net.ooder.mvp.skill.scene.dto.base;

public abstract class IdentifiedDTO extends BaseDTO {
    protected String code;
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
```

#### StatusDTO.java

```java
package net.ooder.mvp.skill.scene.dto.base;

public abstract class StatusDTO extends BaseDTO {
    protected String status;
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

#### TimestampedDTO.java

```java
package net.ooder.mvp.skill.scene.dto.base;

public abstract class TimestampedDTO extends BaseDTO {
    protected Long createdAt;
    protected Long updatedAt;
    
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
```

#### FullDTO.java (组合基类)

```java
package net.ooder.mvp.skill.scene.dto.base;

public abstract class FullDTO extends BaseDTO {
    protected String status;
    protected Long createdAt;
    protected Long updatedAt;
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
```

---

## 三、技能和场景 DTO 重构

### 3.1 SkillDTO 重构

**重构前** (SkillInfoDTO.java):
```java
public class SkillInfoDTO {
    private String skillId;
    private String name;
    private String version;
    private String description;
    private String author;
    private String category;
    private String status;
    private Long installedAt;
    private Long updatedAt;
    // ...
}
```

**重构后**:
```java
package net.ooder.mvp.skill.scene.dto.skill;

import net.ooder.mvp.skill.scene.dto.base.FullDTO;
import java.util.List;

public class SkillDTO extends FullDTO {
    
    private String skillId;
    private String version;
    private String author;
    private String icon;
    private String category;
    private String subCategory;
    private List<String> tags;
    private Long installedAt;
    
    public SkillDTO() {
        super();
    }
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { 
        this.skillId = skillId; 
        this.id = skillId;
    }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Long getInstalledAt() { return installedAt; }
    public void setInstalledAt(Long installedAt) { this.installedAt = installedAt; }
}
```

### 3.2 SceneDTO 重构

**重构前** (SceneInfoDTO.java):
```java
public class SceneInfoDTO {
    private String sceneId;
    private String name;
    private String description;
    private String status;
    private Long createdAt;
    private Long updatedAt;
    // ...
}
```

**重构后**:
```java
package net.ooder.mvp.skill.scene.dto.scene;

import net.ooder.mvp.skill.scene.dto.base.FullDTO;
import java.util.List;

public class SceneDTO extends FullDTO {
    
    private String sceneId;
    private String skillId;
    private String sceneType;
    private String visibility;
    private String skillForm;
    private List<String> capabilities;
    private List<String> dependencies;
    
    public SceneDTO() {
        super();
    }
    
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { 
        this.sceneId = sceneId; 
        this.id = sceneId;
    }
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    
    public String getSceneType() { return sceneType; }
    public void setSceneType(String sceneType) { this.sceneType = sceneType; }
    
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    
    public String getSkillForm() { return skillForm; }
    public void setSkillForm(String skillForm) { this.skillForm = skillForm; }
    
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
}
```

### 3.3 CapabilityDTO 重构

**重构前** (CapabilityDTO.java - 161行):
```java
public class CapabilityDTO {
    private String id;
    private String capabilityId;  // 重复
    private String name;
    private String description;
    private String type;          // 应为 capabilityType
    private String category;      // 应为 capabilityCategory
    private String capabilityCategory;
    // ... 大量属性
}
```

**重构后**:
```java
package net.ooder.mvp.skill.scene.dto.capability;

import net.ooder.mvp.skill.scene.dto.base.FullDTO;
import java.util.List;

public class CapabilityDTO extends FullDTO {
    
    private String capabilityId;
    private String capabilityType;
    private String capabilityCategory;
    private String skillId;
    private String skillForm;
    private String sceneType;
    private String visibility;
    private String version;
    private String icon;
    private String ownership;
    private String subCategory;
    private List<String> tags;
    private List<String> capabilities;
    private List<String> dependencies;
    private boolean sceneCapability;
    private boolean hasSelfDrive;
    private boolean mainFirst;
    private Boolean installed;
    
    public CapabilityDTO() {
        super();
    }
    
    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { 
        this.capabilityId = capabilityId; 
        this.id = capabilityId;
    }
    
    public String getCapabilityType() { return capabilityType; }
    public void setCapabilityType(String capabilityType) { this.capabilityType = capabilityType; }
    
    public String getCapabilityCategory() { return capabilityCategory; }
    public void setCapabilityCategory(String capabilityCategory) { this.capabilityCategory = capabilityCategory; }
    
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    
    public String getSkillForm() { return skillForm; }
    public void setSkillForm(String skillForm) { this.skillForm = skillForm; }
    
    public String getSceneType() { return sceneType; }
    public void setSceneType(String sceneType) { this.sceneType = sceneType; }
    
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getOwnership() { return ownership; }
    public void setOwnership(String ownership) { this.ownership = ownership; }
    
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public boolean isSceneCapability() { return sceneCapability; }
    public void setSceneCapability(boolean sceneCapability) { this.sceneCapability = sceneCapability; }
    
    public boolean isHasSelfDrive() { return hasSelfDrive; }
    public void setHasSelfDrive(boolean hasSelfDrive) { this.hasSelfDrive = hasSelfDrive; }
    
    public boolean isMainFirst() { return mainFirst; }
    public void setMainFirst(boolean mainFirst) { this.mainFirst = mainFirst; }
    
    public Boolean getInstalled() { return installed; }
    public void setInstalled(Boolean installed) { this.installed = installed; }
    
    public boolean isInstalled() { 
        return installed != null && installed; 
    }
    
    // 兼容方法（标记废弃）
    @Deprecated
    public String getType() { return capabilityType; }
    @Deprecated
    public void setType(String type) { this.capabilityType = type; }
    
    @Deprecated
    public String getCategory() { return capabilityCategory; }
    @Deprecated
    public void setCategory(String category) { this.capabilityCategory = category; }
    
    @Deprecated
    public void setCapId(String capId) { this.capabilityId = capId; }
    @Deprecated
    public String getCapId() { return this.capabilityId; }
}
```

---

## 四、属性命名统一规范

### 4.1 ID 属性规范

| 实体类型 | ID 属性名 | 说明 |
|----------|-----------|------|
| 能力 | `capabilityId` | 统一使用 capabilityId |
| 技能 | `skillId` | 统一使用 skillId |
| 场景 | `sceneId` | 统一使用 sceneId |
| 用户 | `userId` | 统一使用 userId |
| 组织 | `orgId` | 统一使用 orgId |

### 4.2 类型属性规范

| 属性含义 | 属性名 | 枚举类型 |
|----------|--------|----------|
| 能力类型 | `capabilityType` | CapabilityType |
| 场景类型 | `sceneType` | SceneType |
| 技能形态 | `skillForm` | SkillForm |
| 能力分类 | `capabilityCategory` | CapabilityCategory |

### 4.3 禁止的命名

| 禁止命名 | 正确命名 | 原因 |
|----------|----------|------|
| `type` | `capabilityType` | 歧义 |
| `category` | `capabilityCategory` | 歧义 |
| `capId` | `capabilityId` | 缩写不一致 |
| `id` (单独使用) | `xxxId` | 语义不明确 |

---

## 五、重构实施计划

### 5.1 阶段一：创建基类（1天）

1. 创建 `dto/base/` 目录
2. 实现 BaseDTO、StatusDTO、TimestampedDTO、FullDTO
3. 编写单元测试

### 5.2 阶段二：重构核心 DTO（2天）

1. 重构 CapabilityDTO
2. 重构 SkillDTO
3. 重构 SceneDTO
4. 添加兼容方法（@Deprecated）

### 5.3 阶段三：更新调用方（2天）

1. 更新 Controller 层
2. 更新 Service 层
3. 更新前端 API 调用

### 5.4 阶段四：清理废弃方法（1天）

1. 移除 @Deprecated 方法
2. 更新文档

---

## 六、验收标准

- [ ] 所有 DTO 继承自基类
- [ ] 属性命名符合规范
- [ ] 无重复属性
- [ ] 编译无错误
- [ ] 单元测试通过
- [ ] API 文档更新

---

**创建时间**: 2026-03-21  
**状态**: 待评审  
**文档版本**: 1.0
