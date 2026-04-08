# 能力分类方案设计 v3.0

> **文档版本**: 3.0.0  
> **创建日期**: 2026-03-11  
> **更新日期**: 2026-03-11  
> **参考标准**: SKILL_CLASSIFICATION_ENFORCEMENT_STANDARD.md v1.1.0  
> **状态**: 设计方案  

---

## 一、分类体系概述

### 1.1 四维分类模型

能力采用**四维分类模型**，通过四个独立的维度进行分类：

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        能力四维分类模型                                            │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│   维度1: SkillForm (技能形态)                                                    │
│   ├── SCENE      - 场景技能 (容器型，包含子技能)                                   │
│   ├── PROVIDER   - 能力提供者 (提供基础能力)                                       │
│   ├── DRIVER     - 驱动技能 (驱动场景运行)                                         │
│   └── INTERNAL   - 内部能力 (系统内部使用)                                         │
│                                                                                 │
│   维度2: SceneType (场景类型) - 仅 SkillForm=SCENE 时有效                          │
│   ├── AUTO       - 自驱场景 (自动运行，hasSelfDrive=true)                         │
│   └── TRIGGER    - 触发场景 (需要触发，hasSelfDrive=false)                        │
│                                                                                 │
│   维度3: Visibility (可见性)                                                      │
│   ├── public     - 普通用户可见 (所有用户可见)                                     │
│   ├── developer  - 开发者可见 (仅开发者可见)                                       │
│   └── internal   - 系统内部 (系统内部使用)                                         │
│                                                                                 │
│   维度4: CapabilityCategory (能力地址分类)                                        │
│   ├── sys        - 系统核心 (0x00)                                                │
│   ├── org        - 组织服务 (0x08)                                                │
│   ├── auth       - 认证服务 (0x10)                                                │
│   ├── vfs        - 文件存储 (0x18)                                                │
│   ├── db         - 数据库 (0x20)                                                  │
│   ├── llm        - 大语言模型 (0x28)                                              │
│   ├── know       - 知识库 (0x30)                                                  │
│   ├── payment    - 支付服务 (0x38)                                                │
│   ├── media      - 媒体服务 (0x40)                                                │
│   ├── comm       - 通讯服务 (0x48)                                                │
│   ├── mon        - 监控服务 (0x50)                                                │
│   ├── iot        - 物联网 (0x58)                                                  │
│   ├── search     - 搜索服务 (0x60)                                                │
│   ├── sched      - 调度服务 (0x68)                                                │
│   ├── sec        - 安全服务 (0x70)                                                │
│   ├── net        - 网络服务 (0x78)                                                │
│   └── util       - 工具服务 (0x08)                                                │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 与旧分类对照

| 旧分类 | 新分类 | 说明 |
|--------|--------|------|
| **ABS** (自驱业务场景) | `SCENE` + `AUTO` + `public` | 自驱场景，用户可见 |
| **ASS** (自驱系统场景) | `SCENE` + `AUTO` + `internal` | 自驱场景，后台运行 |
| **TBS** (触发业务场景) | `SCENE` + `TRIGGER` + `public` | 触发场景，用户参与 |
| **STANDALONE** | `PROVIDER` 或 `DRIVER` | 独立能力 |

---

## 二、分类维度详解

### 2.1 SkillForm (技能形态)

| 值 | 说明 | 判定条件 | 典型示例 |
|-----|------|---------|---------|
| `SCENE` | 场景技能 | `sceneSkill=true` 或 `type=scene-skill` | 日志汇报、知识问答 |
| `PROVIDER` | 能力提供者 | 提供基础能力，被其他技能依赖 | LLM服务、知识库服务 |
| `DRIVER` | 驱动技能 | 驱动场景运行 | 意图接收、调度器 |
| `INTERNAL` | 内部能力 | 系统内部使用 | 场景验证、会话管理 |

### 2.2 SceneType (场景类型)

| 值 | 说明 | 判定条件 | 典型行为 |
|-----|------|---------|---------|
| `AUTO` | 自驱场景 | `hasSelfDrive=true` | 自动运行，定时触发 |
| `TRIGGER` | 触发场景 | `hasSelfDrive=false` | 需要用户或外部触发 |

### 2.3 Visibility (可见性)

| 值 | 说明 | 发现页可见 | 安装权限 |
|-----|------|:--------:|---------|
| `public` | 普通用户可见 | ✅ | 所有用户 |
| `developer` | 开发者可见 | ✅ (开发者) | 开发者 |
| `internal` | 系统内部 | ❌ | 系统自动 |

### 2.4 CapabilityCategory (能力地址分类)

| 分类 | 名称 | 基地址 | 说明 |
|------|------|:------:|------|
| `sys` | 系统核心 | 0x00 | 系统级能力 |
| `org` | 组织服务 | 0x08 | 组织、用户管理 |
| `auth` | 认证服务 | 0x10 | 认证、授权 |
| `vfs` | 文件存储 | 0x18 | 虚拟文件系统 |
| `db` | 数据库 | 0x20 | 数据库操作 |
| `llm` | 大语言模型 | 0x28 | LLM调用 |
| `know` | 知识库 | 0x30 | 知识检索 |
| `payment` | 支付服务 | 0x38 | 支付、计费 |
| `media` | 媒体服务 | 0x40 | 音视频处理 |
| `comm` | 通讯服务 | 0x48 | 消息、通知 |
| `mon` | 监控服务 | 0x50 | 监控、告警 |
| `iot` | 物联网 | 0x58 | 设备管理 |
| `search` | 搜索服务 | 0x60 | 搜索引擎 |
| `sched` | 调度服务 | 0x68 | 定时任务 |
| `sec` | 安全服务 | 0x70 | 安全审计 |
| `net` | 网络服务 | 0x78 | 网络管理 |
| `util` | 工具服务 | 0x08 | 通用工具 |

---

## 三、业务分类枚举

### 3.1 用户可见业务分类 (public/developer)

| 枚举值 | 显示名称 | 推荐SceneType | 典型场景 |
|--------|----------|:-------------:|----------|
| `OFFICE_COLLABORATION` | 办公协作 | TRIGGER | 日志、会议、审批 |
| `HUMAN_RESOURCE` | 人力资源 | TRIGGER | 招聘、绩效、培训 |
| `AI_ASSISTANT` | 智能助手 | AUTO | 问答、客服、对话 |
| `DATA_PROCESSING` | 数据处理 | AUTO/TRIGGER | 报表、分析、同步 |
| `PROJECT_MANAGEMENT` | 项目管理 | TRIGGER | 项目跟踪、看板 |
| `MARKETING_OPERATIONS` | 营销运营 | AUTO/TRIGGER | 内容发布、活动 |
| `SYSTEM_TOOLS` | 系统工具 | AUTO | 存储、通知、备份 |

### 3.2 系统内部业务分类 (internal)

| 枚举值 | 显示名称 | 推荐SceneType | 可见性 |
|--------|----------|:-------------:|--------|
| `SYSTEM_MONITOR` | 系统监控 | AUTO | internal |
| `SECURITY_AUDIT` | 安全审计 | AUTO | internal |
| `INFRASTRUCTURE` | 基础设施 | AUTO | internal |

---

## 四、分类判定规则

### 4.1 SkillForm 判定规则

```java
public SkillForm determineSkillForm(Capability cap) {
    // 1. 如果已显式设置，直接返回
    if (cap.getSkillForm() != null) {
        return SkillForm.valueOf(cap.getSkillForm());
    }
    
    // 2. 根据类型判定
    if (cap.isSceneCapability()) {
        return SkillForm.SCENE;
    }
    
    // 3. 根据驱动类型判定
    if (cap.getDriverType() != null) {
        if (cap.getDriverType().isTrigger()) {
            return SkillForm.DRIVER;
        }
    }
    
    // 4. 根据能力地址分类判定
    if (cap.getCapabilityCategory() != null) {
        switch (cap.getCapabilityCategory()) {
            case "llm":
            case "know":
            case "db":
            case "vfs":
                return SkillForm.PROVIDER;
        }
    }
    
    // 5. 默认为 PROVIDER
    return SkillForm.PROVIDER;
}
```

### 4.2 SceneType 判定规则

```java
public SceneType determineSceneType(Capability cap, SkillForm skillForm) {
    // 仅 SCENE 类型需要判定 SceneType
    if (skillForm != SkillForm.SCENE) {
        return null;
    }
    
    // 1. 如果已显式设置，直接返回
    if (cap.getSceneType() != null) {
        return SceneType.valueOf(cap.getSceneType());
    }
    
    // 2. 根据自驱能力判定
    if (cap.isHasSelfDrive()) {
        return SceneType.AUTO;
    }
    
    // 3. 根据业务分类推荐
    if (cap.getBusinessCategory() != null) {
        switch (cap.getBusinessCategory()) {
            case "AI_ASSISTANT":
            case "SYSTEM_TOOLS":
            case "SYSTEM_MONITOR":
            case "SECURITY_AUDIT":
                return SceneType.AUTO;
            default:
                return SceneType.TRIGGER;
        }
    }
    
    // 4. 默认为 TRIGGER
    return SceneType.TRIGGER;
}
```

### 4.3 Visibility 判定规则

```java
public Visibility determineVisibility(Capability cap, SkillForm skillForm, SceneType sceneType) {
    // 1. 如果已显式设置，直接返回
    if (cap.getVisibility() != null) {
        return Visibility.valueOf(cap.getVisibility());
    }
    
    // 2. INTERNAL 类型默认 internal
    if (skillForm == SkillForm.INTERNAL) {
        return Visibility.internal;
    }
    
    // 3. 根据业务分类判定
    if (cap.getBusinessCategory() != null) {
        switch (cap.getBusinessCategory()) {
            case "SYSTEM_MONITOR":
            case "SECURITY_AUDIT":
            case "INFRASTRUCTURE":
                return Visibility.internal;
        }
    }
    
    // 4. AUTO + 低业务语义评分 = internal
    if (sceneType == SceneType.AUTO) {
        Integer score = cap.getBusinessSemanticsScore();
        if (score != null && score < 8) {
            return Visibility.internal;
        }
    }
    
    // 5. 默认为 public
    return Visibility.public;
}
```

---

## 五、数据模型设计

### 5.1 Capability 类扩展

```java
public class Capability {
    // ========== 基础信息 ==========
    private String capabilityId;
    private String name;
    private String description;
    private String version;
    
    // ========== SE三维分类 (必需) ==========
    private String skillForm;           // SCENE | PROVIDER | DRIVER | INTERNAL
    private String sceneType;           // AUTO | TRIGGER (仅SCENE时有效)
    private String visibility;          // public | developer | internal
    
    // ========== 业务分类 (必需) ==========
    private String businessCategory;    // 业务分类枚举
    private String subCategory;         // 子分类
    private List<String> tags;          // 标签
    
    // ========== 技术分类 (必需) ==========
    private String category;            // KNOWLEDGE | LLM | TOOL | WORKFLOW | DATA | SERVICE | UI | OTHER
    private String capabilityCategory;  // 能力地址分类 (17个)
    
    // ========== 能力地址配置 ==========
    private List<CapabilityAddress> requiredAddresses;
    private List<CapabilityAddress> optionalAddresses;
    
    // ========== 辅助字段 ==========
    private boolean hasSelfDrive;
    private Integer businessSemanticsScore;
    private CapabilityType type;
    private DriverType driverType;
}
```

### 5.2 枚举定义

```java
public enum SkillForm {
    SCENE("SCENE", "场景技能", "容器型技能，可包含子技能"),
    PROVIDER("PROVIDER", "能力提供者", "提供基础能力的技能"),
    DRIVER("DRIVER", "驱动技能", "驱动场景运行的技能"),
    INTERNAL("INTERNAL", "内部能力", "系统内部使用的技能");
}

public enum SceneType {
    AUTO("AUTO", "自驱场景", "自动运行，hasSelfDrive=true"),
    TRIGGER("TRIGGER", "触发场景", "需要触发，hasSelfDrive=false");
}

public enum Visibility {
    public("public", "普通用户可见", "所有用户可见"),
    developer("developer", "开发者可见", "仅开发者可见"),
    internal("internal", "系统内部", "系统内部使用");
}

public enum CapabilityCategory {
    sys("系统核心", 0x00),
    org("组织服务", 0x08),
    auth("认证服务", 0x10),
    vfs("文件存储", 0x18),
    db("数据库", 0x20),
    llm("大语言模型", 0x28),
    know("知识库", 0x30),
    payment("支付服务", 0x38),
    media("媒体服务", 0x40),
    comm("通讯服务", 0x48),
    mon("监控服务", 0x50),
    iot("物联网", 0x58),
    search("搜索服务", 0x60),
    sched("调度服务", 0x68),
    sec("安全服务", 0x70),
    net("网络服务", 0x78),
    util("工具服务", 0x08);
}
```

---

## 六、前端展示设计

### 6.1 发现页分类筛选

```
┌─────────────────────────────────────────────────────────────────┐
│  能力发现                                                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  按形态筛选:                                                      │
│  ┌─────────┐ ┌──────────┐ ┌────────┐ ┌────────┐                │
│  │ 全部    │ │ 场景技能  │ │ 能力   │ │ 驱动   │                │
│  │  (109)  │ │  (49)    │ │ (40)   │ │ (20)   │                │
│  └─────────┘ └──────────┘ └────────┘ └────────┘                │
│                                                                 │
│  按场景类型筛选 (仅场景技能):                                       │
│  ┌─────────┐ ┌────────┐ ┌────────┐                              │
│  │ 全部    │ │ 自驱   │ │ 触发   │                              │
│  │  (49)   │ │  (9)   │ │ (40)   │                              │
│  └─────────┘ └────────┘ └────────┘                              │
│                                                                 │
│  按可见性筛选:                                                    │
│  ┌─────────┐ ┌──────────┐ ┌────────┐                            │
│  │ 全部    │ │ 公开     │ │ 内部   │                            │
│  │ (109)   │ │  (85)    │ │ (24)   │                            │
│  └─────────┘ └──────────┘ └────────┘                            │
│                                                                 │
│  按能力分类筛选:                                                  │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐                  │
│  │ 全部 │ │ LLM  │ │ 知识 │ │ 存储 │ │ 通讯 │ ...               │
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 6.2 能力卡片展示

```
┌─────────────────────────────────────────────────────────────────┐
│  ┌────┐                                                         │
│  │ 🤖 │ 智能对话                                                 │
│  └────┘ llm-chat                                                │
│                                                                 │
│  基于LLM的智能对话能力，支持多轮对话和上下文理解                      │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ [场景技能] [自驱] [公开] [LLM]                              │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  能力地址: 0x28 (LLM_OLLAMA) → 0x29 (LLM_OPENAI)                │
│                                                                 │
│                                              [已安装] ✓         │
└─────────────────────────────────────────────────────────────────┘
```

---

## 七、API 设计

### 7.1 查询接口

```java
// 按分类筛选能力
GET /api/v1/capabilities?skillForm=SCENE&sceneType=AUTO&visibility=public

// 获取分类统计
GET /api/v1/capabilities/stats

// 获取能力分类信息
GET /api/v1/capabilities/{id}/classification
```

### 7.2 响应数据结构

```json
{
  "capabilityId": "llm-chat",
  "name": "智能对话",
  "description": "基于LLM的智能对话能力",
  
  "skillForm": "SCENE",
  "sceneType": "AUTO",
  "visibility": "public",
  
  "businessCategory": "AI_ASSISTANT",
  "subCategory": "智能对话",
  "tags": ["AI", "对话", "LLM"],
  
  "category": "LLM",
  "capabilityCategory": "llm",
  
  "capabilityAddresses": {
    "required": [
      { "address": "0x28", "name": "LLM_OLLAMA", "fallback": "0x29" }
    ],
    "optional": []
  }
}
```

---

## 八、迁移计划

### 8.1 数据迁移

| 阶段 | 任务 | 时间 |
|------|------|------|
| 1 | 添加新字段到 Capability | Day 1 |
| 2 | 更新分类服务逻辑 | Day 2 |
| 3 | 迁移现有数据 | Day 3 |
| 4 | 更新前端展示 | Day 4 |
| 5 | 验证测试 | Day 5 |

### 8.2 兼容性处理

```java
// 兼容旧分类字段
public String getSkillForm() {
    if (skillForm != null) {
        return skillForm;
    }
    // 兼容旧的 sceneCapability 字段
    if (isSceneCapability()) {
        return "SCENE";
    }
    return "PROVIDER";
}
```

---

## 九、验证检查清单

```markdown
## 分类验证检查清单

### SE三维分类
- [ ] `skillForm` 为 SCENE | PROVIDER | DRIVER | INTERNAL
- [ ] `sceneType` 为 AUTO | TRIGGER (仅SCENE时)
- [ ] `visibility` 为 public | developer | internal

### 业务分类
- [ ] `businessCategory` 在枚举范围内
- [ ] `subCategory` 不为空
- [ ] `tags` 至少3个

### 技术分类
- [ ] `category` 在 SkillCategory 枚举中
- [ ] `capabilityCategory` 在 17 个能力地址分类中

### 能力地址
- [ ] `requiredAddresses` 至少1个
- [ ] 所有地址在 0x00-0x7F 范围内
```

---

## 十、版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| 1.0.0 | 2026-03-11 | 初始版本 (STANDALONE/SCENE) |
| 2.0.0 | 2026-03-11 | 二维分类 (SkillForm + SceneType) |
| 3.0.0 | 2026-03-11 | 四维分类，参考强制执行标准 |

---

**文档状态**: 设计方案  
**下次评审**: 2026-03-18
