# SDK 文档修订补丁

> **修订日期**: 2026-03-07  
> **目标文件**: `E:\github\ooder-sdk\scene-engine\docs\`  
> **修订原因**: 统一技能分类体系，与 `skill-classification.yaml` 保持一致

---

## 一、SECONDARY_DEVELOPMENT_GUIDE.md 第六章修订

### 修订前（旧版本）

```markdown
## 六、场景技能分类 API

### 6.1 接口说明

**接口**: `SceneSkillClassifier`

**位置**: `net.ooder.scene.skill.classification.SceneSkillClassifier`

**功能**:
- 场景技能分类检测
- 业务语义评分

### 6.2 分类类型

**v2.3.1 修订**：根据自驱能力和业务语义评分进行分类

| 分类 | 代码 | 条件 | 说明 |
|------|------|------|------|
| 自驱业务场景 | ABS | hasSelfDrive=true + score>=8 | 自动驱动，高业务语义 |
| 自驱系统场景 | ASS | hasSelfDrive=true + score<8 | 自动驱动，业务语义不足 |
| 触发业务场景 | TBS | hasSelfDrive=false + score>=8 | 外部触发，高业务语义 |
| 普通技能 | NOT_SCENE_SKILL | 不满足基本标准 或 无自驱能力且评分<8 | 非场景技能 |

**已废弃分类**（保留用于向后兼容）：

| 分类 | 代码 | 说明 |
|------|------|------|
| 待定 | PENDING | 已废弃，统一使用 NOT_SCENE_SKILL |
| 无效分类 | INVALID | 已废弃，统一使用 NOT_SCENE_SKILL |

### 6.3 自驱能力判定

必须**同时满足**以下三个条件：

1. `mainFirst = true`
2. `mainFirstConfig` 存在且非空
3. `driverConditions` 非空

### 6.4 业务语义评分

满分10分，评分项如下：

| 评分项 | 分值 | 字段 |
|--------|------|------|
| 驱动条件非空 | 3分 | `driverConditions` |
| 参与者非空 | 3分 | `participants` |
| 公开可见 | 2分 | `visibility = "public"` |
| 有协作能力 | 1分 | `collaboration` |
| 有业务标签 | 1分 | `businessTags`（兼容 `tags`） |
```

### 修订后（新版本）

```markdown
## 六、技能分类 API

### 6.1 接口说明

**接口**: `SceneSkillClassifier`

**位置**: `net.ooder.scene.skill.classification.SceneSkillClassifier`

**功能**:
- 技能分类检测（场景技能 vs 服务技能）
- 业务语义评分
- 自动分类判定

### 6.2 分类体系

**v2.3.1 修订**：采用 ABS/ASS/TBS/SVC 四大分类体系

```
┌─────────────────────────────────────────────────────────────────┐
│                        技能分类体系                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌─────────────────────────────────────────────────────────┐  │
│   │              场景技能 (Scene Skill)                      │  │
│   │   条件: hasSceneCapabilities = true                      │  │
│   │                                                          │  │
│   │   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │  │
│   │   │ ABS         │  │ ASS         │  │ TBS         │    │  │
│   │   │ 自驱业务场景 │  │ 自驱系统场景 │  │ 触发业务场景 │    │  │
│   │   │             │  │             │  │             │    │  │
│   │   │ mainFirst=T │  │ mainFirst=T │  │ mainFirst=F │    │  │
│   │   │ score>=8    │  │ score<8     │  │ score>=8    │    │  │
│   │   └─────────────┘  └─────────────┘  └─────────────┘    │  │
│   └─────────────────────────────────────────────────────────┘  │
│                                                                 │
│   ┌─────────────────────────────────────────────────────────┐  │
│   │              服务技能 (Service Skill)                    │  │
│   │   条件: hasSceneCapabilities = false                    │  │
│   │                                                          │  │
│   │   子分类: org | vfs | msg | llm | knowledge | sys | ... │  │
│   └─────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 6.3 场景技能分类

| 分类 | 代码 | 条件 | 说明 |
|------|------|------|------|
| 自驱业务场景 | ABS | hasSceneCapabilities=true + mainFirst=true + score>=8 | 自动启动、业务闭环 |
| 自驱系统场景 | ASS | hasSceneCapabilities=true + mainFirst=true + score<8 | 自动启动、系统功能 |
| 触发业务场景 | TBS | hasSceneCapabilities=true + mainFirst=false + score>=8 | 人工触发、业务功能 |

**场景技能特征**：
- 具备 `sceneCapabilities` 定义
- 参与场景协作
- 有明确的生命周期管理

### 6.4 服务技能分类 (SVC)

| 分类 | 代码 | 子分类 | 说明 |
|------|------|--------|------|
| 服务技能 | SVC | org | 组织服务：用户认证、组织架构 |
| 服务技能 | SVC | vfs | 存储服务：本地/对象存储 |
| 服务技能 | SVC | msg | 消息通讯：MQTT、IM |
| 服务技能 | SVC | llm | LLM服务：对话、嵌入 |
| 服务技能 | SVC | knowledge | 知识服务：RAG、向量 |
| 服务技能 | SVC | sys | 系统服务：监控、网络 |
| 服务技能 | SVC | payment | 支付服务：支付宝、微信 |
| 服务技能 | SVC | media | 媒体服务：公众号、微博 |
| 服务技能 | SVC | util | 工具服务：通用工具 |

**服务技能特征**：
- 无 `sceneCapabilities` 定义
- 作为能力提供者被其他技能依赖
- 单一职责、可复用

### 6.5 自驱能力判定

必须**同时满足**以下条件：

1. `hasSceneCapabilities = true`（有场景能力定义）
2. `mainFirst = true`（主入口优先）
3. `mainFirstConfig` 存在且非空
4. `driverConditions` 非空

### 6.6 业务语义评分

满分10分，评分项如下：

| 评分项 | 分值 | 字段 |
|--------|------|------|
| 驱动条件非空 | 3分 | `driverConditions` |
| 参与者非空 | 3分 | `participants` |
| 公开可见 | 2分 | `visibility = "public"` |
| 有协作能力 | 1分 | `collaboration` |
| 有业务标签 | 1分 | `businessTags`（兼容 `tags`） |
```

---

## 二、API_REFERENCE.md 第七章修订

### 修订前（旧版本）

```markdown
### 7.2 场景分类枚举

**v2.3.1 修订**：根据自驱能力和业务语义评分进行分类

```java
public enum SceneSkillCategory {
    ABS,           // Auto Business Scene - 自驱业务场景
    ASS,           // Auto System Scene - 自驱系统场景
    TBS,           // Trigger Business Scene - 触发业务场景
    PENDING,       // 待定（已废弃，保留用于兼容）
    INVALID,       // 无效分类（已废弃，保留用于兼容）
    NOT_SCENE_SKILL  // 普通技能（非场景技能）
}
```

**分类判定规则**：

| 分类 | 代码 | 条件 | 说明 |
|------|------|------|------|
| 自驱业务场景 | ABS | hasSelfDrive=true + score>=8 | 自动驱动，高业务语义 |
| 自驱系统场景 | ASS | hasSelfDrive=true + score<8 | 自动驱动，业务语义不足 |
| 触发业务场景 | TBS | hasSelfDrive=false + score>=8 | 外部触发，高业务语义 |
| 普通技能 | NOT_SCENE_SKILL | 不满足基本标准 或 无自驱能力且评分<8 | 非场景技能 |

**已废弃分类**（保留用于向后兼容）：

| 分类 | 代码 | 说明 |
|------|------|------|
| 待定 | PENDING | 已废弃，统一使用 NOT_SCENE_SKILL |
| 无效分类 | INVALID | 已废弃，统一使用 NOT_SCENE_SKILL |

**自驱能力判定**：必须同时满足 `mainFirst=true`、`mainFirstConfig` 非空、`driverConditions` 非空
```

### 修订后（新版本）

```markdown
### 7.2 技能分类枚举

**v2.3.1 修订**：采用 ABS/ASS/TBS/SVC 四大分类体系

```java
public enum SkillCategory {
    // 场景技能 - hasSceneCapabilities=true
    ABS,    // Auto Business Scene - 自驱业务场景
    ASS,    // Auto System Scene - 自驱系统场景
    TBS,    // Trigger Business Scene - 触发业务场景
    
    // 服务技能 - hasSceneCapabilities=false
    SVC     // Service Skill - 服务技能
}

// 服务技能子分类
public enum ServiceSubCategory {
    ORG,        // 组织服务
    VFS,        // 存储服务
    MSG,        // 消息通讯
    LLM,        // LLM服务
    KNOWLEDGE,  // 知识服务
    SYS,        // 系统服务
    PAYMENT,    // 支付服务
    MEDIA,      // 媒体服务
    UTIL        // 工具服务
}
```

**分类判定规则**：

| 分类 | 代码 | 条件 | 说明 |
|------|------|------|------|
| 自驱业务场景 | ABS | hasSceneCapabilities=true + mainFirst=true + score>=8 | 自动启动、业务闭环 |
| 自驱系统场景 | ASS | hasSceneCapabilities=true + mainFirst=true + score<8 | 自动启动、系统功能 |
| 触发业务场景 | TBS | hasSceneCapabilities=true + mainFirst=false + score>=8 | 人工触发、业务功能 |
| 服务技能 | SVC | hasSceneCapabilities=false | 能力提供者 |

**自驱能力判定**：必须同时满足 `hasSceneCapabilities=true`、`mainFirst=true`、`mainFirstConfig` 非空、`driverConditions` 非空

**服务技能子分类映射**：

| 子分类 | 代码 | 包含技能 |
|--------|------|----------|
| 组织服务 | org | skill-user-auth, skill-org-dingding, skill-org-feishu, skill-org-wecom, skill-org-ldap |
| 存储服务 | vfs | skill-vfs-local, skill-vfs-database, skill-vfs-minio, skill-vfs-oss, skill-vfs-s3 |
| 消息通讯 | msg | skill-mqtt, skill-msg, skill-im, skill-email, skill-notify |
| LLM服务 | llm | skill-llm-conversation, skill-llm-openai, skill-llm-qianwen, skill-llm-deepseek |
| 知识服务 | knowledge | skill-knowledge-base, skill-rag, skill-vector-sqlite, skill-search |
| 系统服务 | sys | skill-network, skill-protocol, skill-openwrt, skill-k8s |
| 支付服务 | payment | skill-payment-alipay, skill-payment-wechat, skill-payment-unionpay |
| 媒体服务 | media | skill-media-wechat, skill-media-weibo, skill-media-zhihu |
| 工具服务 | util | skill-a2ui, skill-trae-solo, skill-common |
```

---

## 三、修订要点总结

| 修订项 | 旧值 | 新值 | 原因 |
|--------|------|------|------|
| 分类名称 | NOT_SCENE_SKILL | SVC | 与 skill-classification.yaml 统一 |
| 首要条件 | hasSelfDrive | hasSceneCapabilities | 正确的判定字段 |
| 分类结构 | 仅场景分类 | 场景技能 + 服务技能 | 完整分类体系 |
| 服务子分类 | 无 | org/vfs/msg/llm/... | 新增服务技能子分类 |

---

## 四、执行步骤

1. 打开 `E:\github\ooder-sdk\scene-engine\docs\SECONDARY_DEVELOPMENT_GUIDE.md`
2. 定位到第六章（约第398行开始）
3. 用上述"修订后"内容替换整个第六章
4. 打开 `E:\github\ooder-sdk\scene-engine\docs\API_REFERENCE.md`
5. 定位到第七章（约第826行开始）
6. 用上述"修订后"内容替换场景分类枚举部分
7. 更新版本历史，添加修订记录

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-07
