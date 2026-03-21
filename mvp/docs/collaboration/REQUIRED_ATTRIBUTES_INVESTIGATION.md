# 定义级属性摸底调查报告

## 一、调查概述

**调查目的**: 对定义级（必须）属性进行严格的使用情况摸底，确保不遗漏、不重复。  
**调查范围**: MVP 项目 `src/main/java` 目录下所有 Java 文件  
**调查方法**: 使用 Grep 工具搜索属性的 getter/setter 方法调用  
**调查时间**: 2026-03-21

---

## 二、定义级属性清单

| 序号 | 属性名 | 类型 | 来源 | 说明 |
|:----:|--------|------|------|------|
| 1 | `capabilityId` | String | skill.yaml `capabilities[].id` | 能力唯一标识 |
| 2 | `name` | String | skill.yaml `capabilities[].name` | 能力显示名称 |
| 3 | `skillId` | String | skill.yaml `metadata.id` | 所属技能标识 |
| 4 | `type` | Enum | skill.yaml `capabilities[].type` | 能力类型 |
| 5 | `skillForm` | Enum | skill.yaml `spec.skillForm` | 技能形态 |
| 6 | `capabilityCategory` | Enum | skill.yaml `metadata.category` | 能力分类 |

---

## 三、属性使用情况详细分析

### 3.1 capabilityId 属性

**使用统计**: 约 **100+ 处**调用

| 使用场景 | 文件 | 行号 | 用途 |
|----------|------|------|------|
| **能力查询** | `CapabilityServiceImpl.java` | 多处 | `findById(capabilityId)` |
| **能力注册** | `CapabilityRegistry.java` | 28-65 | 注册时作为 Map key |
| **安装流程** | `InstallServiceImpl.java` | 多处 | 创建安装记录、查询能力 |
| **激活流程** | `ActivationServiceImpl.java` | 多处 | 创建激活记录 |
| **发现服务** | `DiscoveryController.java` | 多处 | 返回给前端 |
| **同步服务** | `SkillCapabilitySyncService.java` | 多处 | 同步能力数据 |
| **依赖检查** | `DependencyHealthCheckService.java` | 多处 | 检查依赖状态 |

**关键代码路径**:

```
capabilityId 使用链路:
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│  skill.yaml                                                                 │
│  └── capabilities[].id ──→ SkillCapabilitySyncService ──→ Capability       │
│                                    │                                        │
│                                    ▼                                        │
│                          CapabilityRegistry.register()                      │
│                                    │                                        │
│                                    ▼                                        │
│                          capabilities.json                                  │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     使用者                                           │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │  CapabilityServiceImpl.findById()     → 查询能力                     │   │
│  │  InstallServiceImpl.createInstall()   → 创建安装记录                 │   │
│  │  ActivationServiceImpl.startProcess() → 创建激活记录                 │   │
│  │  DiscoveryController.getCapability()  → 返回能力详情                 │   │
│  │  CapabilityRegistry.getByType()       → 按类型查询                   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**验证结论**: ✅ **必须保留**，无重复定义

---

### 3.2 name 属性

**使用统计**: 约 **80+ 处**调用

| 使用场景 | 文件 | 行号 | 用途 |
|----------|------|------|------|
| **能力创建** | `SkillCapabilitySyncService.java` | 254 | 设置能力名称 |
| **发现服务** | `DiscoveryController.java` | 457 | 返回给前端显示 |
| **统计服务** | `CapabilityStatsServiceImpl.java` | 多处 | 统计报表 |
| **本地发现** | `LocalDiscoveryService.java` | 87 | 构建返回数据 |
| **能力详情** | `CapabilityDiscoveryServiceImpl.java` | 116 | 返回详情 |

**验证结论**: ✅ **必须保留**，无重复定义

---

### 3.3 skillId 属性

**使用统计**: **45 处**调用

| 使用场景 | 文件 | 行号 | 用途 |
|----------|------|------|------|
| **能力创建** | `SkillCapabilitySyncService.java` | 256 | 设置所属技能 |
| **能力注册** | `SeCapabilityServiceImpl.java` | 105 | 从技能创建能力 |
| **发现服务** | `DiscoveryController.java` | 193-247 | 返回技能信息 |
| **依赖安装** | `DependencyAutoInstallService.java` | 127-162 | 安装依赖技能 |
| **依赖检查** | `DependencyHealthCheckService.java` | 98-126 | 检查依赖状态 |
| **场景服务** | `SceneServiceImpl.java` | 334 | 设置场景ID |

**关键代码路径**:

```
skillId 使用链路:
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│  skill.yaml                                                                 │
│  └── metadata.id ──→ SkillCapabilitySyncService ──→ Capability.skillId     │
│                              │                                              │
│                              ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     使用者                                           │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │  SkillCapabilitySyncService.syncSkillCapabilities() → 同步能力       │   │
│  │  SeCapabilityServiceImpl.createFromSkill()          → 创建能力       │   │
│  │  DependencyAutoInstallService.installDependency()   → 安装依赖       │   │
│  │  DependencyHealthCheckService.checkDependency()     → 检查依赖       │   │
│  │  DiscoveryController.getSkill()                     → 返回技能       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**验证结论**: ✅ **必须保留**，无重复定义

---

### 3.4 type 属性

**使用统计**: **76 处**调用

| 使用场景 | 文件 | 行号 | 用途 |
|----------|------|------|------|
| **能力创建** | `SkillCapabilitySyncService.java` | 259,310 | 设置能力类型 |
| **类型过滤** | `SeCapabilityServiceImpl.java` | 213 | 按类型过滤 |
| **发现服务** | `DiscoveryController.java` | 458 | 返回类型信息 |
| **分类服务** | `CapabilityClassificationService.java` | 80-81 | 分类判断 |
| **注册服务** | `CapabilityRegistry.java` | 28-65 | 按类型索引 |
| **生命周期** | `SceneSkillLifecycleServiceImpl.java` | 42 | 判断场景类型 |

**⚠️ 发现问题**: `type` 属性存在歧义！

| 歧义场景 | 说明 |
|----------|------|
| `Capability.type` | 能力类型（SERVICE/SCENE/ATOMIC等） |
| `scene.type` | 场景类型（AUTO/TRIGGER/INTERACTIVE） |
| `param.type` | 参数类型（string/integer等） |
| `todo.type` | 待办类型（activation/approval等） |

**建议**: 将 `Capability.type` 重命名为 `capabilityType` 以消除歧义

**验证结论**: ⚠️ **需要澄清**，建议重命名消除歧义

---

### 3.5 skillForm 属性

**使用统计**: **34 处**调用

| 使用场景 | 文件 | 行号 | 用途 |
|----------|------|------|------|
| **能力创建** | `SkillCapabilitySyncService.java` | 267-345 | 设置技能形态 |
| **安装流程** | `InstallServiceImpl.java` | 96-113 | 决定安装步骤 |
| **发现过滤** | `SeCapabilityServiceImpl.java` | 311,333 | 按形态过滤 |
| **分类服务** | `CapabilityClassificationService.java` | 72-73 | 分类判断 |
| **生命周期** | `SceneSkillLifecycleServiceImpl.java` | 46-67 | 判断生命周期 |

**关键代码路径**:

```
skillForm 使用链路:
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│  skill.yaml                                                                 │
│  └── spec.skillForm ──→ SkillCapabilitySyncService ──→ Capability.skillForm│
│                                │                                            │
│                                ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     使用者                                           │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │  InstallServiceImpl.determineNextSteps() → 决定安装步骤              │   │
│  │  InstallServiceImpl.determinePostInstallStatus() → 决定安装后状态    │   │
│  │  CapabilityDiscoveryServiceImpl.discover() → 按形态过滤              │   │
│  │  SceneSkillLifecycleServiceImpl.determineLifecycle() → 判断生命周期  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  关键判断逻辑:                                                               │
│  if (skillForm == SCENE) → 需要激活流程                                     │
│  if (skillForm == STANDALONE) → 直接可用                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**验证结论**: ✅ **必须保留**，无重复定义

---

### 3.6 capabilityCategory 属性

**使用统计**: **23 处**调用

| 使用场景 | 文件 | 行号 | 用途 |
|----------|------|------|------|
| **能力创建** | `SkillCapabilitySyncService.java` | 298-302 | 设置能力分类 |
| **分类过滤** | `SeCapabilityServiceImpl.java` | 325,335 | 按分类过滤 |
| **发现服务** | `DiscoveryController.java` | 463 | 返回分类信息 |
| **统计服务** | `CapabilityStatsServiceImpl.java` | 108 | 统计分类 |
| **分类服务** | `CapabilityClassificationService.java` | 94-95,161-162 | 分类判断 |

**⚠️ 发现问题**: `capabilityCategory` 与 `category` 存在重复！

| 属性 | 位置 | 类型 | 说明 |
|------|------|------|------|
| `capabilityCategory` | Capability.java | Enum | 标准分类 |
| `category` | Capability.java | String | 冗余，从 capabilityCategory 复制 |
| `category` | skill.yaml metadata | String | 技能分类 |
| `category` | skill.yaml capabilities[] | String | 能力分类（废弃） |

**建议**: 移除 `Capability.category` 字段，统一使用 `capabilityCategory`

**验证结论**: ⚠️ **需要清理**，移除冗余的 `category` 字段

---

## 四、属性使用汇总表

| 属性 | 调用次数 | 使用场景数 | 是否有歧义 | 是否有重复 | 建议 |
|------|:--------:|:----------:|:----------:|:----------:|------|
| `capabilityId` | 100+ | 7 | ❌ | ❌ | ✅ 保留 |
| `name` | 80+ | 5 | ❌ | ❌ | ✅ 保留 |
| `skillId` | 45 | 6 | ❌ | ❌ | ✅ 保留 |
| `type` | 76 | 6 | ⚠️ 有 | ❌ | ⚠️ 重命名为 `capabilityType` |
| `skillForm` | 34 | 5 | ❌ | ❌ | ✅ 保留 |
| `capabilityCategory` | 23 | 5 | ❌ | ⚠️ 有 | ⚠️ 移除冗余 `category` |

---

## 五、发现的问题

### 5.1 属性歧义问题

| 问题 | 影响 | 解决方案 |
|------|------|----------|
| `type` 属性在多处有不同含义 | 代码可读性差，容易出错 | 重命名 `Capability.type` → `capabilityType` |

### 5.2 属性重复问题

| 问题 | 影响 | 解决方案 |
|------|------|----------|
| `capabilityCategory` 与 `category` 重复 | 数据不一致风险 | 移除 `Capability.category` 字段 |

### 5.3 兼容代码问题

| 文件 | 问题 | 解决方案 |
|------|------|----------|
| `CapabilityCategory.java` | `CODE_MAPPING` 兼容历史命名 | 移除兼容映射 |
| `MetadataCompat.java` | 整个文件处理废弃属性 | 删除文件 |

---

## 六、清理建议

### 6.1 立即清理（高优先级）

1. **移除 `Capability.category` 字段**
   - 文件: `Capability.java`
   - 影响: 需要同步修改所有使用 `getCategory()` 的代码
   - 替代: 使用 `getCapabilityCategory()`

2. **重命名 `Capability.type` → `capabilityType`**
   - 文件: `Capability.java`
   - 影响: 需要同步修改所有使用 `getType()` 的代码
   - 注意: 与 `SceneType` 区分

### 6.2 后续清理（中优先级）

1. **移除 `CapabilityCategory.CODE_MAPPING`**
   - 文件: `CapabilityCategory.java`
   - 影响: 历史数据可能使用旧命名
   - 需要: 数据迁移脚本

2. **删除 `MetadataCompat.java`**
   - 文件: `MetadataCompat.java`
   - 影响: 无，该文件已废弃

---

## 七、验证检查清单

### 7.1 capabilityId 检查

- [ ] 所有查询都使用 `capabilityId`
- [ ] 注册时 `capabilityId` 作为唯一 key
- [ ] 安装/激活流程正确使用 `capabilityId`
- [ ] 前端 API 返回 `capabilityId`

### 7.2 name 检查

- [ ] 能力创建时设置 `name`
- [ ] 发现服务返回 `name`
- [ ] 统计服务使用 `name`

### 7.3 skillId 检查

- [ ] 能力创建时设置 `skillId`
- [ ] 依赖安装使用 `skillId`
- [ ] 依赖检查使用 `skillId`

### 7.4 type (capabilityType) 检查

- [ ] 重命名后所有调用点更新
- [ ] 与 `SceneType` 明确区分
- [ ] 枚举值保持不变

### 7.5 skillForm 检查

- [ ] 安装流程正确判断 `skillForm`
- [ ] 激活流程正确判断 `skillForm`
- [ ] 发现服务正确过滤 `skillForm`

### 7.6 capabilityCategory 检查

- [ ] 移除 `category` 字段后所有调用点更新
- [ ] 分类过滤使用 `capabilityCategory`
- [ ] 统计服务使用 `capabilityCategory`

---

## 八、结论

### 8.1 定义级属性确认

| 属性 | 状态 | 说明 |
|------|:----:|------|
| `capabilityId` | ✅ 确认 | 唯一标识，无问题 |
| `name` | ✅ 确认 | 显示名称，无问题 |
| `skillId` | ✅ 确认 | 所属技能，无问题 |
| `type` | ⚠️ 需修改 | 建议重命名为 `capabilityType` |
| `skillForm` | ✅ 确认 | 技能形态，无问题 |
| `capabilityCategory` | ⚠️ 需清理 | 移除冗余 `category` 字段 |

### 8.2 下一步行动

1. **Skills 团队**: 更新 skill.yaml 格式，移除 `capabilities[].category`
2. **MVP 团队**: 
   - 移除 `Capability.category` 字段
   - 重命名 `Capability.type` → `capabilityType`
   - 清理兼容代码

---

**创建时间**: 2026-03-21  
**状态**: 待评审  
**文档版本**: 1.0
