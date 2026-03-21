# 配置属性治理分析报告

## 一、问题概述

当前系统存在大量**重复、歧义、兼容性**配置属性，导致：
1. 开发者不知道该使用哪个属性
2. 配置在不同位置有不同含义
3. 兼容逻辑分散在多处，难以维护
4. 数据同步时属性丢失或冲突

---

## 二、配置属性全流程位置分析

### 2.1 配置存在的位置

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           配置属性存在的位置                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  位置1: skill.yaml (技能包定义)                                              │
│  ═════════════════════════════                                              │
│  metadata:                                                                  │
│    id, name, version, category, subCategory, description, author, icon      │
│  spec:                                                                      │
│    skillForm: SCENE                                                         │
│    scene:                                                                   │
│      type: INTERACTIVE                                                      │
│      visibility: public                                                     │
│    capabilities:                                                            │
│      - id, name, description, category, type, skillForm, sceneType...       │
│                                                                             │
│  位置2: capabilities.json (运行时数据)                                       │
│  ═════════════════════════════════                                          │
│  {                                                                          │
│    capabilityId, name, description, type, version,                          │
│    skillForm, sceneType, visibility, capabilityCategory,                    │
│    category, businessCategory, subCategory,                                 │
│    metadata: { skillForm, sceneType, visibility, category... }              │
│  }                                                                          │
│                                                                             │
│  位置3: Capability.java (内存模型)                                           │
│  ════════════════════════════════                                           │
│  capabilityId, name, description, type, version                             │
│  skillForm (enum), sceneType (enum), visibility (enum)                      │
│  capabilityCategory (enum), category (String, computed)                     │
│  businessCategory, subCategory                                              │
│  metadata: Map<String, Object>                                              │
│                                                                             │
│  位置4: 场景模板 (templates/*.yaml)                                          │
│  ════════════════════════════════                                           │
│  spec:                                                                      │
│    skills: [{ id, version }]                                                │
│    scene: { type, visibility }                                              │
│    roles, activationSteps, menus                                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 数据流向

```
skill.yaml ──→ SkillCapabilitySyncService ──→ Capability.java ──→ capabilities.json
     │                  │                          │
     │                  │                          │
     └──────────────────┴──────────────────────────┘
                    属性映射、转换、兼容
```

---

## 三、重复和歧义属性识别

### 3.1 严重问题：分类属性重复

| 属性名 | 位置1 | 位置2 | 位置3 | 问题 |
|--------|-------|-------|-------|------|
| **category** | `metadata.category` | `capabilities[].category` | `Capability.category` | ❌ **三处定义，含义不同** |
| **capabilityCategory** | - | `capabilityCategory` | `Capability.capabilityCategory` | ✅ 标准分类 |
| **businessCategory** | - | `businessCategory` | `Capability.businessCategory` | ⚠️ 用途不明 |
| **subCategory** | `metadata.subCategory` | `subCategory` | `Capability.subCategory` | ⚠️ 用途不明 |

**问题详解**：

```yaml
# skill.yaml 中的 category
metadata:
  category: biz          # ← 技能包的业务分类

spec:
  capabilities:
    - category: service  # ← 能力的技术分类（service/ai等）
```

```json
// capabilities.json 中的 category
{
  "capabilityCategory": "BIZ",   // ← 标准分类（枚举）
  "category": "biz",            // ← 从 capabilityCategory 复制
  "businessCategory": null,     // ← 未使用
  "subCategory": "hr"           // ← 从 metadata.subCategory 复制
}
```

### 3.2 严重问题：场景类型属性重复

| 属性名 | skill.yaml | capabilities.json | Capability.java | 问题 |
|--------|------------|-------------------|-----------------|------|
| **skillForm** | `spec.skillForm` | `skillForm` | `skillForm` (enum) | ✅ 标准 |
| **sceneType** | `spec.scene.type` | `sceneType` | `sceneType` (enum) | ✅ 标准 |
| **type** | `spec.capabilities[].type` | `type` | `type` (enum) | ⚠️ 与 sceneType 混淆 |
| **sceneSkill** | - | `metadata.sceneSkill` | - | ❌ 废弃但兼容代码存在 |

### 3.3 严重问题：可见性属性重复

| 属性名 | skill.yaml | capabilities.json | Capability.java | 问题 |
|--------|------------|-------------------|-----------------|------|
| **visibility** | `spec.scene.visibility` | `visibility` | `visibility` (enum) | ✅ 标准 |
| **visibilityEnum** | - | `visibilityEnum` | - | ❌ 冗余 |
| **internalVisible** | - | `internalVisible` | `isInternalVisible()` | ❌ 计算属性存储 |
| **publicVisible** | - | `publicVisible` | `isPublicVisible()` | ❌ 计算属性存储 |
| **developerVisible** | - | `developerVisible` | `isDeveloperVisible()` | ❌ 计算属性存储 |

---

## 四、属性必要性划分

### 4.1 定义级（必须）

这些属性是能力的核心标识，**缺失则能力无法正常工作**：

| 属性 | 类型 | 来源 | 说明 |
|------|------|------|------|
| `capabilityId` | String | skill.yaml `capabilities[].id` | 唯一标识 |
| `name` | String | skill.yaml `capabilities[].name` | 显示名称 |
| `skillId` | String | skill.yaml `metadata.id` | 所属技能 |
| `type` | Enum | skill.yaml `capabilities[].type` | 能力类型 |
| `skillForm` | Enum | skill.yaml `spec.skillForm` | 技能形态 |
| `capabilityCategory` | Enum | skill.yaml `metadata.category` | 能力分类 |

### 4.2 描述级（推荐）

这些属性提供能力的描述信息，**影响用户体验**：

| 属性 | 类型 | 来源 | 说明 |
|------|------|------|------|
| `description` | String | skill.yaml `capabilities[].description` | 功能描述 |
| `version` | String | skill.yaml `metadata.version` | 版本号 |
| `icon` | String | skill.yaml `metadata.icon` | 图标 |
| `tags` | List | skill.yaml `metadata.tags` | 标签 |
| `dependencies` | List | skill.yaml `spec.dependencies` | 依赖 |

### 4.3 扩展级（可选）

这些属性提供扩展功能，**按需配置**：

| 属性 | 类型 | 来源 | 说明 |
|------|------|------|------|
| `sceneType` | Enum | skill.yaml `spec.scene.type` | 场景类型（仅SCENE形态） |
| `visibility` | Enum | skill.yaml `spec.scene.visibility` | 可见性 |
| `roles` | List | skill.yaml `spec.roles` | 角色定义 |
| `activationSteps` | Map | skill.yaml `spec.activationSteps` | 激活步骤 |
| `menus` | Map | skill.yaml `spec.menus` | 菜单配置 |
| `configSchema` | Object | skill.yaml `spec.configSchema` | 配置模式 |

---

## 五、属性使用者分析

### 5.1 安装流程使用者

| 属性 | 使用位置 | 用途 |
|------|----------|------|
| `skillForm` | `InstallServiceImpl.determineNextSteps()` | 决定安装步骤 |
| `sceneType` | `InstallServiceImpl.determinePostInstallStatus()` | 决定安装后状态 |
| `visibility` | `InstallServiceImpl.determinePostInstallStatus()` | 决定可见性 |
| `capabilityCategory` | `InstallServiceImpl.installDependency()` | 查找依赖 |

### 5.2 激活流程使用者

| 属性 | 使用位置 | 用途 |
|------|----------|------|
| `skillForm` | `ActivationServiceImpl` | 判断是否需要激活 |
| `roles` | `ActivationServiceImpl` | 获取角色定义 |
| `activationSteps` | `ActivationServiceImpl` | 获取激活步骤 |
| `menus` | `MenuAutoRegisterService` | 注册菜单 |

### 5.3 同步流程使用者

| 属性 | 使用位置 | 用途 |
|------|----------|------|
| `skillForm` | `SkillCapabilitySyncService` | 判断是否创建主能力 |
| `scene.type` | `SkillCapabilitySyncService` | 设置场景类型 |
| `scene.visibility` | `SkillCapabilitySyncService` | 设置可见性 |
| `category` | `SkillCapabilitySyncService` | 设置分类 |

### 5.4 发现流程使用者

| 属性 | 使用位置 | 用途 |
|------|----------|------|
| `capabilityCategory` | `DiscoveryController` | 分类过滤 |
| `skillForm` | `DiscoveryController` | 形态过滤 |
| `visibility` | `DiscoveryController` | 可见性过滤 |
| `tags` | `DiscoveryController` | 标签搜索 |

---

## 六、兼容代码清理建议

### 6.1 需要移除的兼容代码

| 文件 | 兼容代码 | 说明 |
|------|----------|------|
| `CapabilityCategory.java` | `CODE_MAPPING` (100+行) | 各种历史命名的兼容映射 |
| `MetadataCompat.java` | 整个文件 | 废弃属性兼容逻辑 |
| `SkillForm.java` | `PROVIDER`, `DRIVER`, `INTERNAL` | 废弃的形态类型 |
| `SceneType.java` | `fromLegacyCode()` | 废弃的类型编码 |
| `Capability.java` | `getCategory()` | 使用 `capabilityCategory` |

### 6.2 需要移除的冗余属性

| 属性 | 位置 | 原因 |
|------|------|------|
| `category` | capabilities.json | 与 `capabilityCategory` 重复 |
| `visibilityEnum` | capabilities.json | 与 `visibility` 重复 |
| `sceneTypeEnum` | capabilities.json | 与 `sceneType` 重复 |
| `internalVisible` | capabilities.json | 计算属性不应存储 |
| `publicVisible` | capabilities.json | 计算属性不应存储 |
| `developerVisible` | capabilities.json | 计算属性不应存储 |
| `sceneInternal` | capabilities.json | 计算属性不应存储 |
| `businessCategory` | capabilities.json | 未使用 |
| `metadata.sceneSkill` | capabilities.json | 废弃属性 |

---

## 七、标准化配置规范

### 7.1 skill.yaml 标准格式

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillPackage

metadata:
  id: skill-recruitment-management    # 【必须】技能唯一标识
  name: 招聘管理系统                   # 【必须】显示名称
  version: "2.3.1"                    # 【必须】版本号
  category: biz                       # 【必须】能力分类（见 CapabilityCategory 枚举）
  description: 企业招聘全流程管理      # 【推荐】描述
  author: Ooder Team                  # 【推荐】作者
  icon: ri-team-line                  # 【推荐】图标

spec:
  # 【必须】技能形态：SCENE | STANDALONE
  skillForm: SCENE
  
  # 【可选】场景配置（仅 skillForm=SCENE 时需要）
  scene:
    type: INTERACTIVE                 # 场景类型：AUTO | TRIGGER | INTERACTIVE
    visibility: public                # 可见性：public | internal | developer
  
  # 【必须】能力列表
  capabilities:
    - id: job-management              # 【必须】能力ID
      name: 职位管理                   # 【必须】显示名称
      description: 创建、发布、管理招聘职位  # 【推荐】描述
      type: SERVICE                   # 【必须】能力类型（见 CapabilityType 枚举）
  
  # 【可选】角色定义（仅 skillForm=SCENE 时需要）
  roles:
    - id: hr-manager
      name: HR管理员
      description: 管理招聘流程和职位
      permissions: [manage-jobs, view-all-resumes]
  
  # 【可选】激活步骤（仅 skillForm=SCENE 时需要）
  activationSteps:
    hr-manager:
      - step: 1
        action: configure-departments
        title: 配置部门
        required: true
  
  # 【可选】菜单配置（仅 skillForm=SCENE 时需要）
  menus:
    hr-manager:
      - id: dashboard
        name: 招聘概览
        icon: ri-dashboard-line
        path: /recruitment/dashboard
  
  # 【可选】依赖
  dependencies:
    - skillId: skill-vfs-base
      version: ">=2.3.1"
      required: true
```

### 7.2 capabilities.json 标准格式

```json
{
  "capabilityId": "skill-recruitment-management",
  "name": "招聘管理系统",
  "description": "企业招聘全流程管理",
  "type": "SCENE",
  "version": "2.3.1",
  "skillId": "skill-recruitment-management",
  
  "skillForm": "SCENE",
  "sceneType": "INTERACTIVE",
  "visibility": "public",
  "capabilityCategory": "BIZ",
  
  "status": "REGISTERED",
  "createTime": 1774088004961,
  "updateTime": 1774088217725,
  
  "dependencies": ["skill-vfs-base", "skill-approval-form"],
  "tags": ["recruitment", "hr"],
  "icon": "ri-team-line"
}
```

---

## 八、现有 Skills 修改方式判断

### 8.1 修改类型分类

| 类型 | 说明 | 工作量 | 影响范围 |
|------|------|--------|----------|
| **仅配置修改** | 修改 skill.yaml 格式 | 低 | 无代码改动 |
| **程序配合修改** | 修改同步逻辑+配置 | 中 | 同步服务改动 |
| **模型数据同步修改** | 修改数据模型+程序+配置 | 高 | 全链路改动 |

### 8.2 具体修改建议

#### 类型1：仅配置修改（Skills 团队负责）

| 修改项 | 说明 |
|--------|------|
| 移除 `capabilities[].category` | 使用 `type` 替代 |
| 移除 `spec.capabilities[].skillForm` | 继承 `spec.skillForm` |
| 移除 `spec.capabilities[].sceneType` | 继承 `spec.scene.type` |
| 移除 `spec.capabilities[].visibility` | 继承 `spec.scene.visibility` |
| 规范 `metadata.category` | 使用标准分类代码 |

#### 类型2：程序配合修改（MVP 团队负责）

| 修改项 | 说明 |
|--------|------|
| 移除 `CapabilityCategory.CODE_MAPPING` | 不再兼容历史命名 |
| 移除 `MetadataCompat.java` | 不再兼容废弃属性 |
| 移除 `SkillForm.PROVIDER/DRIVER/INTERNAL` | 只保留 SCENE/STANDALONE |
| 移除 `Capability.getCategory()` | 使用 `getCapabilityCategory()` |
| 清理 `SkillCapabilitySyncService` 兼容逻辑 | 严格按新格式解析 |

#### 类型3：模型数据同步修改（联合负责）

| 修改项 | 说明 |
|--------|------|
| 移除 `Capability.category` 字段 | 使用 `capabilityCategory` |
| 移除 `Capability.metadata` 中的冗余属性 | 只保留扩展信息 |
| 清理 `capabilities.json` 冗余属性 | 按标准格式存储 |
| 数据迁移脚本 | 历史数据转换 |

---

## 九、实施计划

### 阶段1：定义标准（1天）

1. 确认 skill.yaml 标准格式
2. 确认 capabilities.json 标准格式
3. 确认 Capability.java 模型字段
4. 输出《配置属性规范文档》

### 阶段2：程序修改（2天）

1. 清理 `CapabilityCategory.CODE_MAPPING`
2. 清理 `MetadataCompat.java`
3. 清理 `SkillForm` 废弃枚举值
4. 修改 `SkillCapabilitySyncService` 严格解析
5. 添加配置验证逻辑

### 阶段3：Skills 配置修改（2天）

1. 按新格式修改所有 skill.yaml
2. 验证配置完整性
3. 测试同步流程

### 阶段4：数据迁移（1天）

1. 编写数据迁移脚本
2. 执行 capabilities.json 清理
3. 验证数据完整性

---

## 十、经验总结

### 10.1 问题根因

1. **历史遗留**：系统演进过程中不断添加兼容逻辑
2. **职责不清**：配置属性没有明确的责任人
3. **文档缺失**：没有统一的配置规范文档
4. **验证不足**：配置同步时缺少完整性验证

### 10.2 改进建议

1. **建立配置治理机制**：配置变更需评审
2. **完善验证逻辑**：同步时严格验证配置完整性
3. **定期清理**：每季度清理废弃属性和兼容代码
4. **文档先行**：配置变更先更新文档再实施

---

**创建时间**: 2026-03-21  
**状态**: 待评审  
**文档版本**: 1.0
