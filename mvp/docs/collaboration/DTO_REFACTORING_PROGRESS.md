# DTO 重构进展报告

## 一、已完成工作

### 1.1 DTO 基类创建

| 文件 | 用途 |
|------|------|
| `dto/base/BaseDTO.java` | 抽象基类 (id, name, description) |
| `dto/base/FullDTO.java` | 组合基类 (+ status, createdAt, updatedAt) |

### 1.2 核心 DTO 重构

| 文件 | 状态 | 说明 |
|------|:----:|------|
| `CapabilityDTO.java` | ✅ 重构 | 继承 FullDTO，属性规范化 |
| `SkillDTO.java` | ✅ 新建 | 继承 FullDTO |
| `SceneDTO.java` | ✅ 新建 | 继承 FullDTO |

### 1.3 YAML DTO 创建

| 文件 | 用途 |
|------|------|
| `dto/yaml/SkillYamlDTO.java` | 技能 YAML 根对象 |
| `dto/yaml/SkillMetadataDTO.java` | 技能元数据 |
| `dto/yaml/SkillSpecDTO.java` | 技能规格 |
| `dto/yaml/SceneConfigDTO.java` | 场景配置 |
| `dto/yaml/CapabilityYamlDTO.java` | 能力定义 |
| `dto/yaml/DependencyYamlDTO.java` | 依赖定义 |
| `dto/yaml/ActivationStepYamlDTO.java` | 激活步骤 |
| `dto/yaml/MenuYamlDTO.java` | 菜单配置 |
| `dto/yaml/RoleYamlDTO.java` | 角色定义 |
| `dto/yaml/YamlDtoConverter.java` | Map → DTO 转换器 |

### 1.4 Discovery DTO 创建

| 文件 | 用途 |
|------|------|
| `dto/discovery/DiscoveryMethodDTO.java` | 发现方法 |
| `dto/discovery/ConfigFieldDTO.java` | 配置字段 |
| `dto/discovery/CapabilityDetailDTO.java` | 能力详情 |
| `dto/discovery/DiscoveryResultDTO.java` | 发现结果 |

### 1.5 Selector DTO 创建

| 文件 | 用途 |
|------|------|
| `dto/selector/CapabilityItemDTO.java` | 能力项 |
| `dto/selector/CapabilityTypeDTO.java` | 能力类型 |
| `dto/selector/ProviderItemDTO.java` | 提供者项 |
| `dto/selector/TemplateItemDTO.java` | 模板项 |
| `dto/selector/SceneGroupItemDTO.java` | 场景组项 |
| `dto/selector/OrgNodeDTO.java` | 组织节点 |
| `dto/selector/UserNodeDTO.java` | 用户节点 |

### 1.6 Install DTO 创建

| 文件 | 用途 |
|------|------|
| `dto/install/InstallRequestDTO.java` | 安装请求 |
| `dto/install/DownloadResultDTO.java` | 下载结果 |

### 1.7 Activation DTO 创建

| 文件 | 用途 |
|------|------|
| `dto/activation/ActivationStepDataDTO.java` | 激活步骤数据 |
| `dto/activation/ActivationResultDTO.java` | 激活结果 |

### 1.8 Controller 层重构

| 文件 | 状态 | 说明 |
|------|:----:|------|
| `DiscoveryController.java` | ✅ 完成 | 使用 DiscoveryMethodDTO, DiscoveryResultDTO |
| `SelectorController.java` | ✅ 完成 | 所有 Map 替换为强类型 DTO |

### 1.7 Service 层重构

| 文件 | 状态 | 说明 |
|------|:----:|------|
| `LocalDiscoveryService.java` | ✅ 完成 | 使用 CapabilityDetailDTO, DiscoveryResultDTO |

---

## 二、剩余工作

### 2.1 仍使用 Map<String, Object> 的文件 (50个)

#### 高优先级 (核心业务)

| 文件 | 原因 | 建议 |
|------|------|------|
| `SkillCapabilitySyncService.java` | YAML 解析返回 Map | 使用 YamlDtoConverter 转换 |
| `CapabilityDiscoveryServiceImpl.java` | 动态属性 | 创建 DTO |
| `SceneSkillLifecycleServiceImpl.java` | 配置数据 | 创建 DTO |
| `ActivationServiceImpl.java` | 激活步骤数据 | 使用 ActivationStepYamlDTO |
| `InstallService.java` | 安装配置 | 创建 InstallRequestDTO |

#### 中优先级 (配置相关)

| 文件 | 原因 | 建议 |
|------|------|------|
| `KeyManagementController.java` | 密钥配置 | 创建 KeyConfigDTO |
| `StatsController.java` | 统计数据 | 创建 StatsDTO |
| `LlmController.java` | LLM 配置 | 创建 LlmConfigDTO |

#### 低优先级 (内部使用)

| 文件 | 原因 | 建议 |
|------|------|------|
| `ConfigNode.java` | 配置节点 | 保留 Map 或创建 ConfigNodeDTO |
| `MultiLevelContextManager*.java` | 上下文管理 | 保留或重构 |

---

## 三、DTO 层次结构

```
BaseDTO (id, name, description)
    └── FullDTO (+ status, createdAt, updatedAt)
            ├── CapabilityDTO
            ├── SkillDTO
            └── SceneDTO

YAML DTO:
SkillYamlDTO
├── SkillMetadataDTO
└── SkillSpecDTO
    ├── SceneConfigDTO
    ├── List<CapabilityYamlDTO>
    ├── List<DependencyYamlDTO>
    ├── List<RoleYamlDTO>
    ├── Map<String, List<ActivationStepYamlDTO>>
    └── Map<String, List<MenuYamlDTO>>

Discovery DTO:
├── DiscoveryMethodDTO
├── ConfigFieldDTO
├── CapabilityDetailDTO
└── DiscoveryResultDTO

Selector DTO:
├── CapabilityItemDTO
├── CapabilityTypeDTO
├── ProviderItemDTO
├── TemplateItemDTO
├── SceneGroupItemDTO
├── OrgNodeDTO
└── UserNodeDTO

Install DTO:
├── InstallRequestDTO
│   └── ParticipantsDTO
│       └── ParticipantDTO
└── DownloadResultDTO

Activation DTO:
├── ActivationStepDataDTO
└── ActivationResultDTO
    └── StepResultDTO
```

---

## 四、属性命名规范

### 4.1 ID 属性

| 实体 | ID 属性名 |
|------|-----------|
| 能力 | `capabilityId` |
| 技能 | `skillId` |
| 场景 | `sceneId` |
| 用户 | `userId` |
| 组织 | `orgId` |

### 4.2 类型属性

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

## 五、后续计划

### 阶段一：完成核心 Service 层重构

1. 修改 `SkillCapabilitySyncService` 使用 `YamlDtoConverter`
2. 创建 `InstallRequestDTO` 重构 `InstallService`
3. 创建 `ActivationConfigDTO` 重构 `ActivationServiceImpl`

### 阶段二：完成配置相关重构

1. 创建 `KeyConfigDTO` 重构密钥管理
2. 创建 `StatsDTO` 重构统计服务
3. 创建 `LlmConfigDTO` 重构 LLM 配置

### 阶段三：清理和优化

1. 移除 `@Deprecated` 方法
2. 更新 API 文档
3. 编写单元测试

---

**创建时间**: 2026-03-21  
**状态**: 进行中  
**文档版本**: 1.0
