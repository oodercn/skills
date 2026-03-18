# Skills 配置与 Engine v3.0 模型对比分析

## 一、Engine v3.0 新模型定义

### 1.1 核心概念

**来源**: [APPLICATION_INTEGRATION_GUIDE.md](file:///E:/github/ooder-sdk/scene-engine/docs/APPLICATION_INTEGRATION_GUIDE.md)

```
范式转变：技能是唯一核心实体，场景是技能的形态属性。

类比文件系统：
├── 技能 (Skill)          → 文件/文件夹
├── 技能形态 (SkillForm)   → 文件类型 (file/folder)
├── 场景类型 (SceneType)   → 文件夹类型 (源码包/资源文件夹/普通文件夹)
└── 技能分类 (SkillCategory) → 文件扩展名 (.doc/.exe/.ai 等)
```

### 1.2 枚举定义

**SkillForm（技能形态）**:
| 值 | 说明 |
|----|------|
| `SCENE` | 场景技能（容器型） |
| `STANDALONE` | 独立技能（原子型） |

**SceneType（场景类型）**:
| 值 | 说明 |
|----|------|
| `AUTO` | 自主场景，可自驱动 |
| `TRIGGER` | 触发场景，被动响应 |
| `HYBRID` | 混合场景，主动+被动 |

**SkillCategory（技能分类）**:
| 值 | 说明 |
|----|------|
| `KNOWLEDGE` | 知识类 |
| `LLM` | AI模型类 |
| `TOOL` | 工具类 |
| `WORKFLOW` | 流程类 |
| `DATA` | 数据类 |
| `SERVICE` | 服务类 |
| `UI` | 界面类 |
| `OTHER` | 其他 |

---

## 二、skill-index.yaml 现状

### 2.1 分类定义

```yaml
categories:
  - id: org          # 组织服务
  - id: vfs          # 存储服务
  - id: ui           # UI生成
  - id: msg          # 消息通讯
  - id: sys          # 系统管理
  - id: llm          # LLM服务
  - id: knowledge    # 知识服务
  - id: payment      # 支付服务
  - id: media        # 媒体发布
  - id: util         # 工具服务
  - id: nexus-ui     # Nexus界面
```

### 2.2 实际使用

```yaml
# 错误使用（场景类型作为分类）
- skillId: skill-document-assistant
  category: abs              # ❌ ABS 是场景类型，不是分类

- skillId: skill-meeting-minutes
  category: tbs              # ❌ TBS 是场景类型，不是分类

# 正确使用
- skillId: skill-knowledge-base
  category: knowledge        # ✅ 正确的分类
```

---

## 三、对比分析

### 3.1 分类对比

| Engine v3.0 SkillCategory | skill-index.yaml category | 状态 |
|---------------------------|---------------------------|:----:|
| `KNOWLEDGE` | `knowledge` | ✅ 一致 |
| `LLM` | `llm` | ✅ 一致 |
| `TOOL` | `util` | ⚠️ 名称不同 |
| `WORKFLOW` | - | ❌ 缺失 |
| `DATA` | - | ❌ 缺失 |
| `SERVICE` | - | ❌ 缺失 |
| `UI` | `ui` | ✅ 一致 |
| `OTHER` | - | ❌ 缺失 |
| - | `org` | ⚠️ Engine 未定义 |
| - | `vfs` | ⚠️ Engine 未定义 |
| - | `msg` | ⚠️ Engine 未定义 |
| - | `sys` | ⚠️ Engine 未定义 |
| - | `payment` | ⚠️ Engine 未定义 |
| - | `media` | ⚠️ Engine 未定义 |
| - | `nexus-ui` | ⚠️ Engine 未定义 |
| - | `abs/tbs/ass` | ❌ 错误使用 |

### 3.2 场景类型对比

| Engine v3.0 SceneType | 现有概念 | 映射关系 |
|-----------------------|----------|----------|
| `AUTO` | ABS (自驱业务场景) | ABS → AUTO |
| `TRIGGER` | TBS (触发业务场景) | TBS → TRIGGER |
| `HYBRID` | - | 新增 |
| - | ASS (自驱系统场景) | ASS → AUTO (内部可见) |

### 3.3 技能形态对比

| Engine v3.0 SkillForm | 现有概念 | 映射关系 |
|-----------------------|----------|----------|
| `SCENE` | scene-skill | sceneSkill=true → SCENE |
| `STANDALONE` | service-skill | sceneSkill=false → STANDALONE |

---

## 四、有争议的点

### 争议点一：分类体系不一致

**Engine 定义**: 8个分类 (KNOWLEDGE, LLM, TOOL, WORKFLOW, DATA, SERVICE, UI, OTHER)

**skill-index.yaml 定义**: 11个分类 (org, vfs, llm, knowledge, sys, msg, ui, payment, media, util, nexus-ui)

**问题**:
1. Engine 的分类更偏向"功能类型"，skill-index 的分类更偏向"业务领域"
2. Engine 缺少 `org`, `vfs`, `msg`, `sys`, `payment`, `media` 等业务分类
3. skill-index 缺少 `WORKFLOW`, `DATA`, `SERVICE` 等功能分类

**建议方案**:

| 方案 | 说明 |
|------|------|
| **方案A** | Engine 扩展分类，包含所有 skill-index 分类 |
| **方案B** | skill-index 精简分类，映射到 Engine 分类 |
| **方案C** | 两层分类：Engine 定义功能分类，skill-index 定义业务子分类 |

### 争议点二：ASS (自驱系统场景) 的归属

**Engine v3.0**: 只有 AUTO/TRIGGER/HYBRID 三种场景类型

**现有概念**: ABS/ASS/TBS 三种场景技能类型

**问题**: ASS (自驱系统场景) 在 Engine v3.0 中没有对应

**分析**:
- ASS 的特点是：自驱 + 低业务语义 + 内部可见
- Engine 的 AUTO 包含了自驱特性，但没有区分业务语义高低

**建议方案**:

| 方案 | 说明 |
|------|------|
| **方案A** | ASS 合并到 AUTO，通过 visibility 区分 |
| **方案B** | Engine 增加 SceneSubType 或 visibility 字段 |
| **方案C** | 保留 ASS，作为 AUTO 的子类型 |

### 争议点三：skill-index 中的错误分类

**问题**: `abs`, `tbs`, `ass` 被当作 category 使用

**影响**:
1. 与 Engine v3.0 模型冲突
2. 分类概念混淆
3. 数据查询异常

**建议方案**:

```yaml
# 修复前
- skillId: skill-document-assistant
  category: abs              # ❌ 错误

# 修复后
- skillId: skill-document-assistant
  category: knowledge        # ✅ 技能分类
  form: SCENE                # ✅ 技能形态
  sceneType: AUTO            # ✅ 场景类型
```

### 争议点四：nexus-ui 分类

**Engine v3.0**: UI 分类对应界面类技能

**skill-index.yaml**: nexus-ui 作为独立分类

**问题**: nexus-ui 是否应该归类到 UI？

**分析**:
- nexus-ui 技能只提供前端页面，不提供后端服务
- 加载方式、权限模型、生命周期都不同

**建议方案**:

| 方案 | 说明 |
|------|------|
| **方案A** | nexus-ui 合并到 ui 分类，通过 form 区分 |
| **方案B** | 保留 nexus-ui，作为 UI 的子分类 |
| **方案C** | Engine 增加 UIType 字段 |

---

## 五、建议的统一方案

### 5.1 分类映射

```yaml
# Engine v3.0 分类扩展
SkillCategory:
  # 功能分类 (Engine 定义)
  KNOWLEDGE: 知识类
  LLM: AI模型类
  TOOL: 工具类
  WORKFLOW: 流程类
  DATA: 数据类
  SERVICE: 服务类
  UI: 界面类
  
  # 业务分类 (扩展)
  ORG: 组织服务
  VFS: 存储服务
  MSG: 消息通讯
  SYS: 系统管理
  PAYMENT: 支付服务
  MEDIA: 媒体发布
  
  # 其他
  OTHER: 其他
```

### 5.2 字段映射

```yaml
# skill-index.yaml 字段规范
- skillId: skill-document-assistant
  # 分类 (映射到 Engine)
  category: knowledge
  
  # 技能形态 (新增)
  form: SCENE
  
  # 场景类型 (新增)
  sceneType: AUTO
  
  # 可见性 (区分 ABS/ASS)
  visibility: public
  
  # 自驱能力配置
  mainFirst: true
  mainFirstConfig: {...}
  driverConditions: [...]
  
  # 业务语义配置
  participants: [...]
  businessTags: [...]
```

### 5.3 迁移脚本

```sql
-- 数据库迁移
UPDATE skills SET 
  category = 'knowledge',
  form = 'SCENE',
  scene_type = 'AUTO',
  visibility = 'public'
WHERE category IN ('abs', 'tbs', 'ass');

-- 分类映射
UPDATE skills SET category = 'TOOL' WHERE category = 'util';
UPDATE skills SET category = 'UI' WHERE category = 'nexus-ui';
```

---

## 六、待讨论问题

### 6.1 必须讨论

| 序号 | 问题 | 选项 |
|------|------|------|
| 1 | Engine 是否扩展分类体系？ | A: 扩展 / B: 不扩展 / C: 两层分类 |
| 2 | ASS 如何处理？ | A: 合并到AUTO / B: 增加字段区分 / C: 保留子类型 |
| 3 | nexus-ui 如何归类？ | A: 合并到UI / B: 独立分类 / C: 增加UIType |

### 6.2 建议讨论

| 序号 | 问题 | 说明 |
|------|------|------|
| 1 | util vs TOOL 命名统一 | 哪个更合适？ |
| 2 | HYBRID 场景类型使用场景 | 什么时候用 HYBRID？ |
| 3 | visibility 字段是否纳入 Engine | 是否需要标准化？ |

---

## 七、下一步行动

1. **Engine 团队**: 确认是否扩展分类体系
2. **Skills 团队**: 准备数据修复脚本
3. **联合讨论**: 解决争议点，确定最终方案

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
