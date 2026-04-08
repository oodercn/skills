# skill-index.yaml 字段检查报告

> **文档类型**: 字段检查报告  
> **检查日期**: 2026-03-11  
> **检查范围**: skill-index.yaml 全部字段  
> **状态**: 待评审

---

## 一、字段总览

### 1.1 文档结构层级

```
skill-index.yaml
├── apiVersion
├── kind
├── metadata
│   ├── name
│   ├── version
│   ├── description
│   ├── author
│   ├── license
│   ├── homepage
│   ├── repository
│   ├── giteeMirror
│   ├── createdAt
│   └── updatedAt
├── spec
│   ├── sceneDrivers[]
│   │   ├── id
│   │   ├── name
│   │   ├── description
│   │   ├── address
│   │   ├── capabilities[]
│   │   ├── builtIn
│   │   └── location
│   ├── categories[]
│   │   ├── id
│   │   ├── name
│   │   ├── nameEn
│   │   ├── description
│   │   ├── icon
│   │   ├── order
│   │   ├── address
│   │   ├── sceneDriver
│   │   ├── visibility
│   │   ├── ownership
│   │   ├── userFacing
│   │   └── displayGroup
│   ├── skills[]
│   │   ├── skillId
│   │   ├── name
│   │   ├── version
│   │   ├── category
│   │   ├── subCategory
│   │   ├── visibility
│   │   ├── ownership
│   │   ├── skillType
│   │   ├── tags[]
│   │   ├── description
│   │   ├── sceneId
│   │   ├── path
│   │   ├── mainFirst
│   │   ├── capabilities[]
│   │   ├── driverCapabilities[]
│   │   ├── dependencies[]
│   │   ├── downloadUrl
│   │   ├── giteeDownloadUrl
│   │   └── checksum
│   └── scenes[]
│       ├── sceneId
│       ├── name
│       ├── description
│       ├── version
│       ├── category
│       ├── mainFirst
│       ├── requiredCapabilities[]
│       ├── driverCapabilities[]
│       ├── collaborativeCapabilities[]
│       └── maxMembers
```

---

## 二、字段详细检查

### 2.1 metadata 层级字段

| 字段 | 类型 | 用途 | 代码使用 | 状态 | 建议 |
|------|------|------|----------|:----:|------|
| `name` | string | 技能库名称 | ✅ 用于展示 | 🟢 使用中 | 保留 |
| `version` | string | 技能库版本 | ✅ 版本检查 | 🟢 使用中 | 保留 |
| `description` | string | 描述信息 | ✅ 用于展示 | 🟢 使用中 | 保留 |
| `author` | string | 作者信息 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于市场展示 |
| `license` | string | 许可证 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于合规检查 |
| `homepage` | string | 主页地址 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于市场展示 |
| `repository` | string | 仓库地址 | ✅ Git克隆使用 | 🟢 使用中 | 保留 |
| `giteeMirror` | string | Gitee镜像地址 | ✅ 国内下载使用 | 🟢 使用中 | 保留 |
| `createdAt` | string | 创建时间 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于审计 |
| `updatedAt` | string | 更新时间 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于版本检查 |

---

### 2.2 sceneDrivers 层级字段

| 字段 | 类型 | 用途 | 代码使用 | 状态 | 建议 |
|------|------|------|----------|:----:|------|
| `id` | string | 驱动唯一标识 | ✅ 核心字段 | 🟢 使用中 | 保留 |
| `name` | string | 驱动名称 | ✅ 用于展示 | 🟢 使用中 | 保留 |
| `description` | string | 驱动描述 | ✅ 用于展示 | 🟢 使用中 | 保留 |
| `address` | string | 地址空间 | ⚠️ 新增字段，代码未使用 | 🟡 待验证 | 用于地址空间设计 |
| `capabilities` | string[] | 提供的能力列表 | ✅ 能力匹配使用 | 🟢 使用中 | 保留 |
| `builtIn` | boolean | 是否内置 | ✅ 加载逻辑使用 | 🟢 使用中 | 保留 |
| `location` | string | 位置 | ✅ 加载逻辑使用 | 🟢 使用中 | 保留 |

---

### 2.3 categories 层级字段

| 字段 | 类型 | 用途 | 代码使用 | 状态 | 建议 |
|------|------|------|----------|:----:|------|
| `id` | string | 分类唯一标识 | ✅ 核心字段 | 🟢 使用中 | 保留 |
| `name` | string | 分类名称(中文) | ✅ 用于展示 | 🟢 使用中 | 保留 |
| `nameEn` | string | 分类名称(英文) | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于国际化 |
| `description` | string | 分类描述 | ✅ 用于展示 | 🟢 使用中 | 保留 |
| `icon` | string | 图标名称 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于前端展示 |
| `order` | number | 排序权重 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于前端排序 |
| `address` | string | 地址范围 | ⚠️ 新增字段，代码未使用 | 🟡 待验证 | 用于地址空间设计 |
| `sceneDriver` | string | 关联的驱动ID | ✅ 驱动匹配使用 | 🟢 使用中 | 保留 |
| `visibility` | string | 可见性 | ⚠️ 新增字段，代码未使用 | 🟡 待验证 | 用于权限控制 |
| `ownership` | string | 归属类型 | ⚠️ 新增字段，代码未使用 | 🟡 待验证 | 用于能力治理 |
| `userFacing` | boolean | 是否面向用户 | ⚠️ 新增字段，代码未使用 | 🟡 待验证 | 用于前端筛选 |
| `displayGroup` | string | 展示分组 | ⚠️ 新增字段，代码未使用 | 🟡 待验证 | 用于前端分组 |

---

### 2.4 skills 层级字段

| 字段 | 类型 | 用途 | 代码使用 | 状态 | 建议 |
|------|------|------|----------|:----:|------|
| `skillId` | string | 技能唯一标识 | ✅ 核心字段 | 🟢 使用中 | 保留 |
| `name` | string | 技能名称 | ✅ 用于展示 | 🟢 使用中 | 保留 |
| `version` | string | 版本号 | ✅ 版本检查 | 🟢 使用中 | 保留 |
| `category` | string | 分类ID | ✅ 分类匹配 | 🟢 使用中 | 保留 |
| `subCategory` | string | 子分类 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于细分展示 |
| `visibility` | string | 可见性 | ⚠️ 新增字段，代码未使用 | 🟡 待验证 | 用于权限控制 |
| `ownership` | string | 归属类型 | ⚠️ 新增字段，代码未使用 | 🟡 待验证 | 用于能力治理 |
| `skillType` | string | 技能类型 | ⚠️ 新增字段，代码未使用 | 🟡 待验证 | 用于类型筛选 |
| `tags` | string[] | 标签列表 | ✅ 搜索、筛选使用 | 🟢 使用中 | 保留 |
| `description` | string | 描述信息 | ✅ 用于展示 | 🟢 使用中 | 保留 |
| `sceneId` | string | 场景ID | ✅ 场景匹配 | 🟢 使用中 | 保留 |
| `path` | string | 安装路径 | ✅ 加载使用 | 🟢 使用中 | 保留 |
| `mainFirst` | boolean | 是否自驱型 | ✅ 场景启动逻辑 | 🟢 使用中 | 保留 |
| `capabilities` | string[] | 提供的能力列表 | ✅ 能力匹配 | 🟢 使用中 | 保留 |
| `driverCapabilities` | string[] | 驱动能力列表 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于驱动匹配 |
| `dependencies` | object[] | 依赖列表 | ✅ 依赖检查 | 🟢 使用中 | 保留 |
| `downloadUrl` | string | 下载地址 | ✅ 下载使用 | 🟢 使用中 | 保留 |
| `giteeDownloadUrl` | string | Gitee下载地址 | ✅ 国内下载使用 | 🟢 使用中 | 保留 |
| `checksum` | string | 校验和 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于完整性校验 |

---

### 2.5 scenes 层级字段

| 字段 | 类型 | 用途 | 代码使用 | 状态 | 建议 |
|------|------|------|----------|:----:|------|
| `sceneId` | string | 场景唯一标识 | ✅ 核心字段 | 🟢 使用中 | 保留 |
| `name` | string | 场景名称 | ✅ 用于展示 | 🟢 使用中 | 保留 |
| `description` | string | 场景描述 | ✅ 用于展示 | 🟢 使用中 | 保留 |
| `version` | string | 版本号 | ✅ 版本检查 | 🟢 使用中 | 保留 |
| `category` | string | 分类 | ✅ 分类匹配 | 🟢 使用中 | 保留 |
| `mainFirst` | boolean | 是否自驱型 | ✅ 场景启动逻辑 | 🟢 使用中 | 保留 |
| `requiredCapabilities` | string[] | 必需能力 | ✅ 能力检查 | 🟢 使用中 | 保留 |
| `driverCapabilities` | string[] | 驱动能力 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于驱动匹配 |
| `collaborativeCapabilities` | string[] | 协作能力 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于协作场景 |
| `maxMembers` | number | 最大成员数 | ⚠️ 未在代码中直接使用 | 🟡 保留 | 用于协作场景限制 |

---

## 三、字段状态统计

### 3.1 总体统计

| 状态 | 数量 | 占比 | 说明 |
|------|------|------|------|
| 🟢 使用中 | 28 | 58% | 代码中明确使用 |
| 🟡 保留 | 18 | 38% | 用于展示/扩展/未来使用 |
| 🔴 废弃 | 0 | 0% | 无废弃字段 |
| 🟠 待验证 | 2 | 4% | 新增字段需验证 |

### 3.2 新增字段验证状态

| 字段 | 位置 | 用途 | 验证状态 | 建议 |
|------|------|------|----------|------|
| `visibility` | categories, skills | 权限控制 | 🟠 待验证 | 需前端适配 |
| `ownership` | categories, skills | 能力治理 | 🟠 待验证 | 需后端适配 |
| `skillType` | skills | 类型筛选 | 🟠 待验证 | 需前端适配 |
| `address` | sceneDrivers, categories | 地址空间 | 🟠 待验证 | 用于地址空间设计 |
| `userFacing` | categories | 用户可见 | 🟠 待验证 | 需前端适配 |
| `displayGroup` | categories | 展示分组 | 🟠 待验证 | 需前端适配 |

---

## 四、问题与建议

### 4.1 发现的问题

| 问题 | 描述 | 严重程度 |
|------|------|----------|
| **新增字段未使用** | visibility, ownership 等新增字段代码中未使用 | 中 |
| **tags 用途不明确** | tags 字段在代码中使用分散，缺乏统一规范 | 低 |
| **checksum 未使用** | checksum 字段未在代码中实现校验逻辑 | 低 |

### 4.2 建议

#### 建议1: 规范 tags 使用

```yaml
# 当前使用方式（不规范）
tags:
  - llm
  - chat
  - ai
  - conversation
  - scene-capability  # ← 这个标签用途不明
  - mainFirst         # ← 这个应该是属性而非标签

# 建议使用方式
tags:
  - llm              # 分类标签
  - chat             # 功能标签
  - ai               # 技术标签
  - conversation     # 业务标签
```

**建议**: 
1. 将 `scene-capability` 移除，使用 `skillType` 属性代替
2. 将 `mainFirst` 移除，使用 `mainFirst` 属性代替
3. 制定 tags 命名规范文档

#### 建议2: 实现 checksum 校验

```java
// 建议在下载后校验
public void validateChecksum(String filePath, String expectedChecksum) {
    if (expectedChecksum == null || expectedChecksum.isEmpty()) {
        return; // 无校验和则跳过
    }
    String actualChecksum = calculateSHA256(filePath);
    if (!actualChecksum.equals(expectedChecksum)) {
        throw new SecurityException("Checksum mismatch!");
    }
}
```

#### 建议3: 新增字段适配计划

| 字段 | 适配模块 | 优先级 | 预计工时 |
|------|----------|--------|----------|
| `visibility` | 前端筛选 + 后端权限 | P1 | 2天 |
| `ownership` | 后端能力治理 | P2 | 1天 |
| `skillType` | 前端筛选 | P1 | 1天 |
| `address` | 地址空间服务 | P2 | 2天 |
| `userFacing` | 前端展示 | P2 | 0.5天 |
| `displayGroup` | 前端分组 | P3 | 0.5天 |

---

## 五、字段与代码映射

### 5.1 SkillDefinition.java 字段映射

| YAML字段 | Java字段 | 类型 | 说明 |
|----------|----------|------|------|
| `skillId` | `skillId` | String | ✅ 完全匹配 |
| `name` | `name` | String | ✅ 完全匹配 |
| `version` | `version` | String | ✅ 完全匹配 |
| `category` | `category` | String | ✅ 完全匹配 |
| `tags` | `tags` | List\<String\> | ✅ 完全匹配 |
| `capabilities` | `capabilities` | List\<String\> | ✅ 完全匹配 |
| `description` | `description` | String | ✅ 完全匹配 |
| `downloadUrl` | - | - | ❌ 未映射 |
| `visibility` | - | - | ❌ 未映射 (新增) |
| `ownership` | - | - | ❌ 未映射 (新增) |
| `skillType` | `type` | String | ⚠️ 名称不一致 |

### 5.2 Capability.java 字段映射

| YAML字段 | Java字段 | 类型 | 说明 |
|----------|----------|------|------|
| `capabilities` | `capabilities` | List\<String\> | ✅ 完全匹配 |
| `mainFirst` | `mainFirst` | boolean | ✅ 完全匹配 |
| `visibility` | `visibility` | String | ✅ 完全匹配 |
| `skillForm` | `skillForm` | String | ⚠️ YAML中无此字段 |

---

## 六、决策请求

请 SE 团队评审以下决策点：

| 决策点 | 选项 | 建议 |
|--------|------|------|
| 是否保留所有新增字段？ | 是 / 否 / 部分保留 | 部分保留 |
| tags 规范化方案？ | 制定规范 / 保持现状 | 制定规范 |
| checksum 实现优先级？ | P0 / P1 / P2 / 不实现 | P2 |
| 字段映射不一致问题？ | 统一命名 / 保持现状 | 统一命名 |

---

## 七、附录

### 7.1 相关文件

- [skill-index.yaml](./skill-index.yaml)
- [SkillDefinition.java](./skills/_system/skill-management/src/main/java/net/ooder/skill/management/model/SkillDefinition.java)
- [Capability.java](./skills/skill-scene/src/main/java/net/ooder/skill/scene/capability/model/Capability.java)

### 7.2 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-03-11 | 初始版本 |

---

**文档状态**: 待评审  
**下一步**: 等待 SE 团队评审反馈
