# DTO 重构与 Map 消除计划

## 一、问题概述

### 1.1 问题规模

| 指标 | 数值 | 严重程度 |
|------|:----:|:---------:|
| 使用 `Map<String, Object>` 的文件数 | **100** | 🔴 严重 |
| Controller 层 Map 使用 | 30+ | 🔴 严重 |
| Service 层 Map 使用 | 50+ | 🔴 严重 |
| DTO 层 Map 使用 | 20+ | 🟡 中等 |

### 1.2 问题影响

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Map 使用导致的问题                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 类型不安全                                                              │
│     └── Map<String, Object> 无法在编译期检查类型                            │
│                                                                             │
│  2. 属性名不一致                                                            │
│     └── 同一属性在不同位置使用不同名称（type vs capabilityType）             │
│                                                                             │
│  3. 数据流向难以追踪                                                        │
│     └── 无法通过 IDE 查找属性的使用位置                                      │
│                                                                             │
│  4. 调试困难                                                                │
│     └── 运行时才能发现属性缺失或类型错误                                     │
│                                                                             │
│  5. 文档缺失                                                                │
│     └── Map 没有结构化文档，新开发者难以理解数据结构                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、关键对象识别

### 2.1 需要抽取 DTO 的核心对象

| 对象名 | 当前形式 | 使用位置 | 优先级 |
|--------|----------|----------|:------:|
| **CapabilityData** | `Map<String, Object>` | SkillCapabilitySyncService | 🔴 高 |
| **SkillMetadata** | `Map<String, Object>` | DiscoveryController, MvpSkillIndexLoader | 🔴 高 |
| **SceneConfig** | `Map<String, Object>` | SkillCapabilitySyncService | 🔴 高 |
| **ActivationStepData** | `Map<String, Object>` | ActivationServiceImpl | 🔴 高 |
| **InstallRequest** | `Map<String, Object>` | InstallController | 🟡 中 |
| **DiscoveryRequest** | `Map<String, Object>` | DiscoveryController | 🟡 中 |
| **MenuConfig** | `Map<String, Object>` | MenuAutoRegisterService | 🟡 中 |
| **RoleConfig** | `Map<String, Object>` | SceneTemplateService | 🟡 中 |

### 2.2 已有 DTO 需要检查

| DTO 文件 | 状态 | 问题 |
|----------|:----:|------|
| `CapabilityDTO.java` | ⚠️ | 属性名与 Capability.java 不一致 |
| `LocalDiscoveryResultDTO.java` | ✅ | 正常 |
| `InstallConfig.java` | ⚠️ | 内部仍有 Map 使用 |

---

## 三、属性名称不一致问题

### 3.1 已发现的不一致

| 属性含义 | Capability.java | CapabilityDTO.java | Map 中的名称 | 问题 |
|----------|-----------------|--------------------|--------------|------|
| 能力类型 | `capabilityType` | `type` | `type` / `capabilityType` | ❌ 不一致 |
| 能力分类 | `capabilityCategory` | `category` | `category` / `capabilityCategory` | ❌ 不一致 |
| 技能形态 | `skillForm` | `skillForm` | `skillForm` / `skillFormType` | ⚠️ 可能不一致 |
| 场景类型 | `sceneType` | `sceneType` | `sceneType` / `scene_type` | ⚠️ 可能不一致 |
| 可见性 | `visibility` | `visibility` | `visibility` / `visibilityEnum` | ❌ 不一致 |

### 3.2 属性命名规范建议

| 属性类型 | 命名规范 | 示例 |
|----------|----------|------|
| 枚举类型 | 驼峰命名 | `capabilityType`, `skillForm`, `sceneType` |
| ID 类型 | 以 `Id` 结尾 | `capabilityId`, `skillId`, `sceneId` |
| 时间类型 | 以 `Time` 结尾 | `createTime`, `updateTime` |
| 布尔类型 | 以 `is/has/can` 开头 | `isEnabled`, `hasSelfDrive`, `canActivate` |
| 列表类型 | 复数形式 | `capabilities`, `dependencies`, `tags` |

---

## 四、DTO 重构计划

### 4.1 阶段一：核心 DTO 定义（优先级：高）

#### 4.1.1 SkillYamlDTO

```java
/**
 * skill.yaml 文件结构映射
 */
public class SkillYamlDTO {
    private String apiVersion;
    private String kind;
    private SkillMetadataDTO metadata;
    private SkillSpecDTO spec;
    
    // getters, setters
}

public class SkillMetadataDTO {
    private String id;                    // skillId
    private String name;
    private String version;
    private String category;              // capabilityCategory
    private String subCategory;
    private String description;
    private String author;
    private String icon;
    private List<String> tags;
    
    // getters, setters
}

public class SkillSpecDTO {
    private String skillForm;             // SCENE / STANDALONE
    private SceneConfigDTO scene;         // 仅 skillForm=SCENE 时
    private List<CapabilityDataDTO> capabilities;
    private List<DependencyDTO> dependencies;
    private List<RoleDTO> roles;          // 仅 skillForm=SCENE 时
    private Map<String, List<ActivationStepDTO>> activationSteps;
    private Map<String, List<MenuDTO>> menus;
    
    // getters, setters
}

public class SceneConfigDTO {
    private String type;                  // AUTO / TRIGGER / INTERACTIVE
    private String visibility;            // public / internal / developer
    
    // getters, setters
}

public class CapabilityDataDTO {
    private String id;                    // capabilityId
    private String name;
    private String description;
    private String type;                  // SERVICE / SCENE / ATOMIC
    private String category;
    private Boolean autoBind;
    private List<String> dependencies;
    
    // getters, setters
}
```

#### 4.1.2 ActivationStepDTO

```java
/**
 * 激活步骤数据
 */
public class ActivationStepDTO {
    private String stepId;
    private String name;
    private String description;
    private Boolean required;
    private Boolean skippable;
    private Boolean autoExecute;
    private String actionType;
    private Map<String, Object> actionConfig;
    private List<String> privateCapabilities;
    
    // getters, setters
}
```

#### 4.1.3 MenuDTO

```java
/**
 * 菜单配置数据
 */
public class MenuDTO {
    private String id;
    private String name;
    private String icon;
    private String url;
    private Integer order;
    private Boolean visible;
    private List<MenuDTO> children;
    
    // getters, setters
}
```

### 4.2 阶段二：Service 层重构（优先级：高）

#### 4.2.1 SkillCapabilitySyncService 重构

**当前代码**：
```java
private void syncSkillCapabilities(String skillId, List<Map<String, Object>> capabilities, ...) {
    for (Map<String, Object> capData : capabilities) {
        Capability cap = createCapabilityFromYaml(skillId, capData, ...);
        // ...
    }
}
```

**重构后**：
```java
private void syncSkillCapabilities(String skillId, List<CapabilityDataDTO> capabilities, ...) {
    for (CapabilityDataDTO capData : capabilities) {
        Capability cap = createCapabilityFromDTO(skillId, capData, ...);
        // ...
    }
}

private Capability createCapabilityFromDTO(String skillId, CapabilityDataDTO dto, ...) {
    Capability cap = new Capability();
    cap.setCapabilityId(dto.getId());
    cap.setName(dto.getName());
    cap.setDescription(dto.getDescription());
    cap.setCapabilityType(CapabilityType.valueOf(dto.getType()));
    // ...
    return cap;
}
```

### 4.3 阶段三：Controller 层重构（优先级：中）

#### 4.3.1 DiscoveryController 重构

**当前代码**：
```java
@PostMapping("/discover/local")
public ResultModel<LocalDiscoveryResultDTO> discoverLocal(@RequestBody(required = false) Map<String, Object> request) {
    // ...
}
```

**重构后**：
```java
@PostMapping("/discover/local")
public ResultModel<LocalDiscoveryResultDTO> discoverLocal(@RequestBody(required = false) DiscoveryRequestDTO request) {
    // ...
}

public class DiscoveryRequestDTO {
    private String category;
    private String skillForm;
    private String sceneType;
    private String keyword;
    private Integer page;
    private Integer size;
    
    // getters, setters
}
```

---

## 五、属性名称统一方案

### 5.1 统一映射表

| 模型层 (Capability.java) | DTO 层 | JSON/API 层 | 说明 |
|--------------------------|--------|-------------|------|
| `capabilityId` | `capabilityId` | `capabilityId` | ✅ 统一 |
| `name` | `name` | `name` | ✅ 统一 |
| `capabilityType` | `capabilityType` | `capabilityType` | ⚠️ 需修改 DTO |
| `capabilityCategory` | `capabilityCategory` | `capabilityCategory` | ⚠️ 需修改 DTO |
| `skillForm` | `skillForm` | `skillForm` | ✅ 统一 |
| `sceneType` | `sceneType` | `sceneType` | ✅ 统一 |
| `visibility` | `visibility` | `visibility` | ✅ 统一 |

### 5.2 DTO 属性修改

```java
// CapabilityDTO.java 需要修改
public class CapabilityDTO {
    // 修改前
    private String type;
    private String category;
    
    // 修改后
    private String capabilityType;      // 与 Capability.java 一致
    private String capabilityCategory;  // 与 Capability.java 一致
    
    // 保留兼容的 getter（标记为 @Deprecated）
    @Deprecated
    public String getType() {
        return capabilityType;
    }
    
    @Deprecated
    public String getCategory() {
        return capabilityCategory;
    }
}
```

---

## 六、实施计划

### 6.1 时间表

| 阶段 | 任务 | 负责团队 | 时间 |
|------|------|----------|------|
| 阶段1 | 定义核心 DTO 类 | MVP 团队 | 2 天 |
| 阶段2 | 重构 SkillCapabilitySyncService | MVP 团队 | 2 天 |
| 阶段3 | 重构 Controller 层 | MVP 团队 | 2 天 |
| 阶段4 | 统一属性名称 | MVP 团队 | 1 天 |
| 阶段5 | 更新前端 API 调用 | 前端团队 | 1 天 |
| 阶段6 | 测试验证 | 联合 | 1 天 |

### 6.2 验收标准

- [ ] 所有 `Map<String, Object>` 替换为强类型 DTO
- [ ] DTO 属性名与模型层一致
- [ ] 编译无错误
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] API 文档更新

---

## 七、风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 前端 API 调用失败 | 高 | 保留 @Deprecated getter 方法兼容 |
| 数据迁移问题 | 中 | 编写数据迁移脚本 |
| 性能影响 | 低 | DTO 转换使用 MapStruct |

---

## 八、从源头避免属性混乱

### 8.1 代码规范

1. **禁止在 Controller/Service 中使用 Map**
   - 使用强类型 DTO
   - 使用 Lombok 简化代码

2. **属性命名一致性检查**
   - 使用 ArchUnit 编写架构测试
   - CI/CD 中加入检查

3. **DTO 与模型属性映射**
   - 使用 MapStruct 自动映射
   - 编译期检查映射正确性

### 8.2 架构测试示例

```java
@AnalyzeClasses(packages = "net.ooder.mvp")
public class DtoNamingConventionTest {
    
    @ArchTest
    static final ArchRule dto_fields_should_match_model = 
        fields().that().areDeclaredInClassesThat()
            .resideInAPackage("..dto..")
            .should().haveNameMatching("capabilityId|name|capabilityType|capabilityCategory|skillForm|sceneType|visibility")
            .because("DTO 属性名应与模型层一致");
    
    @ArchTest
    static final ArchRule no_map_in_controllers = 
        noClasses().that().resideInAPackage("..controller..")
            .should().dependOnClassesThat().areAssignableTo(Map.class)
            .because("Controller 层禁止使用 Map");
}
```

---

**创建时间**: 2026-03-21  
**状态**: 待评审  
**文档版本**: 1.0
