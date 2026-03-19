# 深入分析：场景技能分类体系核心问题

## 一、skillType 计算策略

### 1.1 代码中的计算逻辑

**代码位置**: [SceneSkillCategoryDetector.java](file:///e:/github/ooder-skills/skills/skill-scene/src/main/java/net/ooder/skill/scene/capability/service/SceneSkillCategoryDetector.java)

```java
// 判断是否场景技能 (标准1)
public boolean checkStandard1(SkillPackage skillPackage) {
    // 优先检查 sceneSkill 字段
    if (Boolean.TRUE.equals(metadata.get("sceneSkill"))) {
        return true;
    }
    // 兼容 type 字段
    Object type = metadata.get("type");
    return type != null && "scene-skill".equals(type.toString());
}

// 判断自驱能力 (标准3)
private boolean hasSelfDriveCapability(Capability capability) {
    return mainFirst && hasMainFirstConfig && hasDriverConditions;  // 三者必须同时满足
}

// 计算业务语义评分 (标准4)
public int calculateBusinessSemanticsScore(SkillPackage skillPackage) {
    int score = 0;
    if (hasDriverConditions) score += 3;
    if (hasParticipants) score += 3;
    if (isPublicVisibility) score += 2;
    if (hasCollaborationCapability) score += 1;
    if (hasBusinessTags) score += 1;
    return score;  // 满分10分
}
```

### 1.2 建议方案：默认自动 + 可强制覆盖

```yaml
# 方案设计
skillTypeConfig:
  mode: auto                  # auto | manual | hybrid (默认 auto)
  override: false             # 是否允许手动覆盖
  
# 自动模式 (默认)
- skillId: skill-document-assistant
  skillTypeConfig:
    mode: auto                # 自动计算
  # 系统自动计算结果: ABS

# 手动覆盖模式
- skillId: skill-custom-scene
  skillTypeConfig:
    mode: manual              # 手动指定
    skillType: TBS            # 强制指定
  # 即使计算结果不同，也使用手动指定的值

# 混合模式 (推荐)
- skillId: skill-document-assistant
  skillTypeConfig:
    mode: hybrid              # 混合模式
    skillType: ABS            # 声明期望值
  # 系统会验证: 如果计算结果与声明不符，发出警告
```

### 1.3 实现建议

| 模式 | 行为 | 使用场景 |
|------|------|----------|
| **auto** (默认) | 完全自动计算 | 大多数场景技能 |
| **manual** | 完全手动指定 | 特殊场景，需要强制类型 |
| **hybrid** | 自动计算 + 验证警告 | 需要确认类型的场景 |

---

## 二、普通技能 vs 场景技能区分

### 2.1 代码中的两种判断方式

**方式一：通过 type 字段**
```yaml
spec:
  type: scene-skill           # 场景技能
  type: service-skill         # 普通服务技能
  type: provider-skill        # Provider技能
```

**方式二：通过 sceneSkill 标志**
```yaml
spec:
  sceneSkill: true            # 是场景技能
  sceneSkill: false           # 不是场景技能 (默认)
```

### 2.2 代码中的实际处理

**代码位置**: [MetadataCompat.java](file:///e:/github/ooder-skills/skills/skill-scene/src/main/java/net/ooder/skill/scene/capability/service/MetadataCompat.java)

```java
public static boolean isSceneSkill(Map<String, Object> metadata) {
    // 优先级1: sceneSkill 字段
    Object sceneSkill = metadata.get("sceneSkill");
    if (sceneSkill instanceof Boolean) {
        return (Boolean) sceneSkill;
    }
    
    // 优先级2: type 字段 (兼容多种写法)
    Object type = metadata.get("type");
    if ("scene-skill".equals(type) || "SCENE".equals(type) || "scene".equals(type)) {
        return true;
    }
    
    // 优先级3: capabilities 字段 (隐式判断)
    Object capabilities = metadata.get("capabilities");
    if (capabilities instanceof List && !((List<?>) capabilities).isEmpty()) {
        return true;
    }
    
    return false;
}
```

### 2.3 利弊分析

| 方式 | 优点 | 缺点 | 建议 |
|------|------|------|------|
| **type: scene-skill** | 语义明确，与技能类型体系一致 | 需要定义更多 type 值 | ✅ 推荐作为主要方式 |
| **sceneSkill: true** | 简单直接，布尔值易理解 | 与 type 字段功能重叠 | ⚠️ 作为兼容/快捷方式 |

### 2.4 建议方案：两者结合

```yaml
# 推荐写法 (主要)
spec:
  type: scene-skill           # 主要标识，语义明确
  
# 兼容写法 (可选)
spec:
  type: service-skill
  sceneSkill: true            # 快捷标识，覆盖 type 判断

# 代码处理优先级
# 1. sceneSkill 字段 (最高优先级，允许覆盖)
# 2. type 字段 (标准方式)
# 3. capabilities 非空 (隐式判断)
```

---

## 三、分类体系深入分析

### 3.1 llm 和 knowledge 是否需要合并？

**代码中的使用分析**:

| 分类 | 关联技能 | 核心能力 | SceneDriver |
|------|----------|----------|:-----------:|
| **llm** | skill-llm-openai, skill-llm-qianwen, skill-llm-conversation, skill-llm-config-manager | 对话、补全、嵌入、配置管理 | null |
| **knowledge** | skill-knowledge-base, skill-rag, skill-vector-sqlite, skill-document-processor | 知识库、检索、向量存储、文档处理 | null |

**依赖关系分析**:
```yaml
# skill-knowledge-base 依赖
dependencies:
  - skillId: skill-vector-sqlite    # knowledge 分类
  - skillId: skill-llm-openai       # llm 分类 (嵌入模型)

# skill-rag 依赖
dependencies:
  - skillId: skill-knowledge-base   # knowledge 分类
  - skillId: skill-llm-openai       # llm 分类 (嵌入模型)
```

**结论**: **不建议合并**

| 理由 | 说明 |
|------|------|
| **职责不同** | llm 是"计算"，knowledge 是"存储检索" |
| **独立演进** | LLM服务可以独立升级，不影响知识库 |
| **成本控制** | LLM有成本，知识库存储成本低 |
| **依赖方向** | knowledge 依赖 llm (嵌入)，不是反向 |

### 3.2 nexus-ui 是否应该独立？

**代码中的使用分析**:

```yaml
# nexus-ui 分类下的技能
- skillId: skill-knowledge-ui
  category: nexus-ui
  description: 知识库管理界面
  
- skillId: skill-llm-management-ui
  category: nexus-ui
  description: LLM管理界面
  
- skillId: skill-nexus-dashboard
  category: nexus-ui
  description: 系统仪表盘
```

**代码中的引用**:
```javascript
// page-init.js 中的菜单加载
async function loadMenus() {
    const response = await fetch('/api/skills?category=nexus-ui');
    const skills = await response.json();
    // 动态生成菜单...
}
```

**结论**: **应该独立**

| 理由 | 说明 |
|------|------|
| **UI 特殊性** | UI 技能不提供后端能力，只提供前端页面 |
| **加载方式不同** | UI 技能需要加载静态资源，不需要启动服务 |
| **权限模型不同** | UI 技能的权限是"可见性"，不是"调用权" |
| **生命周期不同** | UI 技能随页面加载，不随场景激活 |

---

## 四、场景技能边界分析

### 4.1 代码中的分类原因

**ABS (自驱业务场景)**:
```
判定条件: hasSelfDrive=true && score>=8

为什么这样设计？
- 自驱能力: 场景可以自动启动和运行，不需要外部触发
- 高业务语义: 场景有明确的业务目标，不是纯技术任务
- 公开可见: 场景对用户可见，用户可以感知和参与

典型场景:
- 文档问答助手: 用户进入后自动启动，有明确问答目标
- 新人培训助手: 新人入职自动启动，有培训目标
```

**ASS (自驱系统场景)**:
```
判定条件: hasSelfDrive=true && score<8

为什么这样设计？
- 自驱能力: 场景可以自动运行
- 低业务语义: 场景是技术任务，用户不直接感知
- 内部可见: 场景在后台运行，用户不直接交互

典型场景:
- 日志清理: 定时清理日志，用户不感知
- 数据同步: 后台同步数据，用户不感知
```

**TBS (触发业务场景)**:
```
判定条件: hasSelfDrive=false && score>=8

为什么这样设计？
- 非自驱: 场景需要外部触发才能启动
- 高业务语义: 场景有明确业务目标
- 需要人工参与: 场景需要用户主动触发或参与

典型场景:
- 会议纪要: 需要用户上传会议录音才触发
- 项目知识: 需要用户上传项目文档才触发
- 日志汇报: 需要用户填写日志才触发
```

### 4.2 安装过程差异

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        安装过程差异                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ABS (自驱业务场景)                                                          │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  1. 安装技能包                                                        │   │
│   │  2. 自动创建场景实例 (autoStart=true)                                  │   │
│   │  3. 自动绑定能力 (CapabilityBinding)                                  │   │
│   │  4. 自动注册菜单 (MenuConfig)                                         │   │
│   │  5. 用户直接使用                                                      │   │
│   │                                                                     │   │
│   │  特点: 全自动，用户无感知                                               │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   ASS (自驱系统场景)                                                          │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  1. 安装技能包                                                        │   │
│   │  2. 后台自动启动 (不创建场景实例)                                       │   │
│   │  3. 自动绑定系统级能力                                                 │   │
│   │  4. 不注册用户菜单                                                     │   │
│   │  5. 后台静默运行                                                      │   │
│   │                                                                     │   │
│   │  特点: 后台自动，用户不可见                                             │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   TBS (触发业务场景)                                                          │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  1. 安装技能包                                                        │   │
│   │  2. 等待触发条件 (用户操作/定时任务/外部事件)                            │   │
│   │  3. 触发后创建场景实例                                                 │   │
│   │  4. 执行激活流程 (ActivationProcess)                                  │   │
│   │  5. 用户参与配置和确认                                                 │   │
│   │                                                                     │   │
│   │  特点: 需要触发，用户参与                                               │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.3 场景协作差异

| 维度 | ABS | ASS | TBS |
|------|-----|-----|-----|
| **协作模式** | 单用户主导 | 无协作 | 多角色协作 |
| **参与者** | 单一用户 | 无 | 多角色 (MANAGER, EMPLOYEE, HR) |
| **激活流程** | 无需激活 | 自动激活 | 需要激活流程 |
| **能力绑定** | 自动绑定 | 系统级绑定 | 按角色绑定 |
| **菜单配置** | 单一菜单 | 无菜单 | 按角色菜单 |

### 4.4 能力绑定差异

```yaml
# ABS 能力绑定
capabilityBinding:
  mode: auto                  # 自动绑定
  scope: personal             # 个人级别
  capabilities:
    - id: ai-assistant
      autoStart: true

# ASS 能力绑定
capabilityBinding:
  mode: system                # 系统级绑定
  scope: global               # 全局级别
  capabilities:
    - id: log-cleaner
      schedule: "0 0 2 * * ?"

# TBS 能力绑定
capabilityBinding:
  mode: role-based            # 按角色绑定
  scope: scene-group          # 场景组级别
  capabilities:
    - id: daily-report
      roles: [EMPLOYEE]
    - id: team-summary
      roles: [MANAGER]
```

### 4.5 能力依赖差异

```yaml
# ABS 依赖
dependencies:
  required:
    - id: llm-service         # LLM 是核心
    - id: vector-db           # 向量库是核心
  optional: []

# ASS 依赖
dependencies:
  required:
    - id: database            # 数据库是核心
  optional:
    - id: redis               # 缓存是可选

# TBS 依赖
dependencies:
  required:
    - id: database
    - id: mqtt                # 通知是核心
  optional:
    - id: llm-service         # LLM 是增强功能
    - id: email-service       # 邮件是可选
```

---

## 五、用户故事与分类对应

### 5.1 用户故事映射

| 用户故事 | 场景技能类型 | 原因 |
|----------|:------------:|------|
| US-001: 管理场景激活流程 | TBS | 需要用户参与激活 |
| US-003: 查看团队日志 | TBS | 多角色协作 |
| US-004: 填写日志 | TBS | 需要用户触发 |
| US-008: AI生成日志 | TBS | LLM增强，可降级 |
| US-011: 自动识别角色 | ABS/ASS | 系统自动，用户无感知 |
| US-014: 配置菜单项 | TBS | 管理者配置 |
| US-019: 配置LLM模型 | ABS/TBS | 取决于场景类型 |

### 5.2 功能需求与分类对应

| 功能需求 | ABS | ASS | TBS |
|----------|:---:|:---:|:---:|
| FR-001: 自动安装 | ✅ | ✅ | ⚠️ 需触发 |
| FR-002: 智能推荐依赖 | ✅ | ✅ | ✅ |
| FR-003: 多角色激活 | ❌ | ❌ | ✅ |
| FR-004: 菜单动态生成 | ⚠️ 单一 | ❌ | ✅ 多角色 |
| FR-005: LLM降级 | ✅ | ✅ | ✅ |
| FR-006: 能力自动绑定 | ✅ | ✅ | ⚠️ 按角色 |

---

## 六、总结与建议

### 6.1 核心结论

| 问题 | 结论 |
|------|------|
| skillType 计算 | 默认自动，允许手动覆盖，推荐 hybrid 模式 |
| 场景技能区分 | type: scene-skill 为主，sceneSkill: true 为兼容 |
| llm/knowledge 合并 | 不合并，职责不同 |
| nexus-ui 独立 | 应该独立，UI 技能特殊性 |
| 分类边界 | 基于自驱能力和业务语义评分，有明确设计原因 |

### 6.2 修复优先级

| 优先级 | 修复项 | 工作量 |
|:------:|--------|:------:|
| P0 | 修复 skill-index.yaml 中的 category 误用 | 1天 |
| P0 | 添加 skillType 字段和计算逻辑 | 2天 |
| P1 | 完善 sceneSkill/type 兼容处理 | 1天 |
| P2 | 更新文档和规范 | 1天 |

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
