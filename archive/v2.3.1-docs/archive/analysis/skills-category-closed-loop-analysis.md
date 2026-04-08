# Skills 分类与场景故事闭环分析报告

## 一、核心概念辨析

### 1.1 分类 vs 场景类型 vs 场景技能类型

| 概念 | 定义 | 用途 | 示例 |
|------|------|------|------|
| **分类 (category)** | 技能库的组织分类 | 技能市场展示、筛选 | `org`, `vfs`, `llm`, `knowledge`, `sys` |
| **场景类型 (sceneType)** | 场景的业务类型 | 场景模板匹配 | `knowledge-qa`, `llm-workspace`, `daily-report` |
| **场景技能类型** | 场景技能的激活模式 | 激活流程控制 | `ABS`, `TBS`, `ASS` |

### 1.2 三者关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        概念关系图                                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   技能库 (skill-index.yaml)                                                  │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  分类 (category)                                                      │   │
│   │  ├── org (组织服务)                                                   │   │
│   │  ├── vfs (存储服务)                                                   │   │
│   │  ├── llm (LLM服务)                                                    │   │
│   │  ├── knowledge (知识服务)                                              │   │
│   │  ├── sys (系统管理)                                                   │   │
│   │  └── ...                                                              │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                          ↓ 提供能力                                           │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  场景模板 (SceneTemplate)                                             │   │
│   │  ├── sceneType: knowledge-qa                                          │   │
│   │  ├── sceneType: llm-workspace                                         │   │
│   │  └── sceneType: daily-report                                          │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                          ↓ 定义激活模式                                       │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  场景技能类型 (SceneSkillType)                                         │   │
│   │  ├── ABS (Auto Business Scene) - 自动业务场景                          │   │
│   │  ├── TBS (Trigger Business Scene) - 触发业务场景                       │   │
│   │  └── ASS (Auto System Scene) - 自动系统场景                            │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、矛盾点分析

### 2.1 矛盾点一：场景技能类型误作分类

**现状**：
```yaml
# skill-index.yaml 中错误使用
- skillId: skill-document-assistant
  category: abs              # ❌ 错误：ABS是场景技能类型，不是分类
```

**正确用法**：
```yaml
- skillId: skill-document-assistant
  category: knowledge        # ✅ 正确：知识服务分类
  sceneType: knowledge-qa    # ✅ 正确：知识问答场景类型
  skillType: abs             # ✅ 正确：自动业务场景技能类型
```

**影响闭环**：
| 闭环 | 问题 | 影响 |
|------|------|------|
| 用户流程闭环 | 用户在技能市场找不到"知识服务"分类下的技能 | 用户体验差 |
| 数据闭环 | 分类统计不准确 | 数据分析错误 |
| 动作事件闭环 | 分类筛选功能失效 | 功能异常 |

### 2.2 矛盾点二：分类与场景驱动器不匹配

**现状**：
```yaml
# skill-index.yaml 中分类定义
categories:
  - id: llm
    name: LLM服务
    sceneDriver: null        # ⚠️ 问题：LLM分类没有对应的SceneDriver
```

**问题分析**：
| 分类 | SceneDriver | 问题 |
|------|:-----------:|------|
| `org` | `org` | ✅ 匹配 |
| `vfs` | `vfs` | ✅ 匹配 |
| `msg` | `msg` | ✅ 匹配 |
| `sys` | `sys` | ✅ 匹配 |
| `llm` | `null` | ❌ 无驱动器，场景激活时无法自动绑定能力 |
| `knowledge` | `null` | ❌ 无驱动器，知识库场景无法自动管理 |
| `ui` | `null` | ⚠️ UI生成不需要驱动器，可接受 |
| `nexus-ui` | `null` | ⚠️ UI类不需要驱动器，可接受 |

**影响闭环**：
| 闭环 | 问题 | 影响 |
|------|------|------|
| 用户流程闭环 | LLM场景激活时无法自动配置Provider | 需要手动配置 |
| 数据闭环 | 知识库场景无法自动创建知识库实例 | 数据初始化失败 |
| 动作事件闭环 | 场景激活后能力绑定不完整 | 功能缺失 |

### 2.3 矛盾点三：角色识别与分类归属

**用户故事**：
```
US-011: 作为场景管理者，系统应该自动识别我的角色
US-012: 作为普通员工，系统应该显示我专属的菜单
```

**现状问题**：
```yaml
# skill-org-base 的角色识别配置
roleDetection:
  enabled: true
  rules:
    - role: MANAGER
      condition: "user.position contains '经理'"
```

**矛盾点**：
| 问题 | 描述 | 影响 |
|------|------|------|
| 角色识别依赖组织服务 | 角色识别需要 `skill-org-base` 或 `skill-org-*` | 如果未安装组织服务，角色识别失效 |
| 分类归属不明确 | `skill-org-base` 属于 `org` 分类，但角色识别是跨分类能力 | 其他分类的场景如何使用角色识别？ |
| 菜单动态生成依赖角色 | 菜单配置需要角色信息，但角色来源不统一 | 菜单显示不一致 |

**闭环影响**：
```
用户登录 → 角色识别(依赖org服务) → 菜单生成 → 场景激活
    ↓
如果org服务未安装 → 角色识别失败 → 菜单显示错误 → 场景激活异常
```

### 2.4 矛盾点四：LLM模型配置与场景分类

**用户故事**：
```
US-019: 作为场景管理者，我想为不同用户配置不同的LLM模型
US-020: 作为场景管理者，我想配置不同模型的提示词模板
```

**现状问题**：
```yaml
# skill-llm-config-manager 的配置
configLevels:
  - level: SYSTEM
  - level: ENTERPRISE
  - level: PERSONAL
  - level: SCENE_GROUP
  - level: SCENE
  - level: SCENE_STEP
```

**矛盾点**：
| 问题 | 描述 | 影响 |
|------|------|------|
| 配置层级与分类无关 | LLM配置是跨分类的能力，但被归类为 `llm` 分类 | 其他分类的场景如何引用LLM配置？ |
| 模型选择策略未定义 | 不同场景类型应该使用什么模型？ | 成本和性能无法平衡 |
| 提示词模板管理缺失 | 不同场景需要不同的提示词模板 | 模板管理分散 |

**闭环影响**：
```
场景激活 → LLM配置检查 → 模型选择 → 提示词加载 → 对话启动
    ↓
如果配置不完整 → 使用默认配置 → 可能不符合场景需求 → 用户体验差
```

---

## 三、闭环检测矩阵

### 3.1 分类闭环检测

| 检测项 | 检测方法 | 预期结果 | 实际结果 | 状态 |
|--------|----------|----------|----------|:----:|
| 分类定义完整性 | 检查所有使用的分类是否在categories中定义 | 100%定义 | 存在未定义分类 | ❌ |
| 分类使用一致性 | 检查分类ID大小写是否一致 | 全部小写 | 存在大写 | ❌ |
| 分类与SceneDriver匹配 | 检查有SceneDriver的分类是否正确配置 | 100%匹配 | 部分缺失 | ⚠️ |
| 分类归属合理性 | 检查技能是否归属到正确的分类 | 合理归属 | 部分不合理 | ⚠️ |

### 3.2 场景故事闭环检测

| 用户故事 | 分类依赖 | 依赖技能 | 闭环状态 | 问题 |
|----------|----------|----------|:--------:|------|
| US-001: 管理场景激活流程 | `sys` | skill-scene | ⚠️ | 激活流程不完整 |
| US-011: 自动识别角色 | `org` | skill-org-base | ⚠️ | 角色识别依赖未明确 |
| US-012: 显示专属菜单 | `nexus-ui` | skill-knowledge-ui | ⚠️ | 菜单配置未集成 |
| US-019: 配置LLM模型 | `llm` | skill-llm-config-manager | ⚠️ | 跨分类引用未定义 |
| US-023: LLM调用工具 | `llm` | skill-llm-conversation | ⚠️ | 工具调用权限未定义 |

### 3.3 数据闭环检测

| 数据流 | 起点 | 终点 | 状态 | 问题 |
|--------|------|------|:----:|------|
| 技能分类 → 场景模板 | skill-index.yaml | SceneTemplate | ⚠️ | 分类与sceneType混淆 |
| 场景模板 → 激活流程 | SceneTemplate | ActivationProcess | ⚠️ | 激活步骤未完全定义 |
| 激活流程 → 能力绑定 | ActivationProcess | CapabilityBinding | ⚠️ | 部分能力未自动绑定 |
| 能力绑定 → 菜单注册 | CapabilityBinding | MenuRoleConfig | ❌ | 未集成 |

---

## 四、矛盾点讨论

### 4.1 讨论点一：分类是否需要与SceneDriver一一对应？

**观点A：需要对应**
- 分类代表一类能力，应该有统一的驱动器管理
- 便于场景激活时自动绑定能力
- 便于跨场景的能力复用

**观点B：不需要对应**
- UI类、工具类技能不需要驱动器
- 驱动器是场景引擎的概念，分类是技能库的概念
- 应该解耦，避免过度绑定

**建议方案**：
```yaml
categories:
  - id: llm
    name: LLM服务
    sceneDriver: null           # 不需要驱动器
    crossCategory: true         # 标记为跨分类能力
    referencedBy:               # 被哪些分类引用
      - knowledge
      - collaboration
      
  - id: knowledge
    name: 知识服务
    sceneDriver: knowledge      # 需要驱动器
    dependsOn:                  # 依赖哪些跨分类能力
      - llm
```

### 4.2 讨论点二：场景技能类型(ABS/TBS/ASS)应该如何处理？

**现状问题**：
- `abs`/`tbs`/`ass` 被误用作分类
- 场景技能类型定义了激活模式，但未在模板中体现

**建议方案**：
```yaml
# 场景模板中明确定义
apiVersion: scene.ooder.net/v1
kind: SceneTemplate

metadata:
  id: knowledge-qa
  name: 知识问答场景
  category: knowledge           # 分类
  sceneType: knowledge-qa       # 场景类型
  skillType: abs                # 场景技能类型

spec:
  # ABS类型的激活模式
  activationMode:
    type: abs                   # 自动业务场景
    autoStart: true             # 自动启动
    mainFirst: true             # 主导者优先
    singleUser: true            # 单用户模式
```

### 4.3 讨论点三：角色识别能力应该如何归属？

**现状问题**：
- 角色识别在 `skill-org-base` 中定义
- 其他分类的场景也需要角色识别
- 角色识别是跨分类的基础能力

**建议方案A：角色识别作为独立能力**
```yaml
# 新建 skill-role-detection
- skillId: skill-role-detection
  name: 角色识别服务
  category: sys                 # 归类到系统管理
  capabilities:
    - id: role-detection
      name: 角色识别
      category: security
```

**建议方案B：角色识别作为场景引擎内置能力**
```yaml
# 在场景引擎中内置角色识别
sceneEngine:
  builtInCapabilities:
    - role-detection
    - permission-check
    - menu-generation
```

### 4.4 讨论点四：LLM配置如何跨分类使用？

**现状问题**：
- LLM配置在 `skill-llm-config-manager` 中
- 知识服务、协作服务都需要LLM配置
- 配置引用机制不明确

**建议方案**：
```yaml
# 场景模板中引用LLM配置
spec:
  dependencies:
    crossCategory:
      - category: llm
        capability: llm-config
        required: true
        configMapping:
          provider: "${scene.config.llm.provider}"
          model: "${scene.config.llm.model}"
```

---

## 五、修复建议

### 5.1 立即修复 (P0)

| 修复项 | 修复内容 | 影响范围 |
|--------|----------|----------|
| 分类大小写统一 | 将所有大写分类改为小写 | skill-index.yaml |
| 移除场景类型作为分类 | 将 `abs`/`tbs`/`ass` 改为正确分类 | skill-index.yaml |
| 添加缺失分类定义 | 添加 `business`/`infrastructure`/`scheduler` 定义 | skill-index.yaml |

### 5.2 短期修复 (P1)

| 修复项 | 修复内容 | 影响范围 |
|--------|----------|----------|
| 分离场景技能类型 | 添加 `sceneType` 和 `skillType` 字段 | SceneTemplate |
| 定义跨分类能力 | 明确哪些能力可以跨分类使用 | skill-index.yaml |
| 完善SceneDriver | 为 `llm` 和 `knowledge` 添加驱动器 | scene-engine |

### 5.3 长期优化 (P2)

| 修复项 | 修复内容 | 影响范围 |
|--------|----------|----------|
| 角色识别独立化 | 将角色识别作为独立能力或内置能力 | 架构调整 |
| LLM配置标准化 | 定义跨分类配置引用规范 | 架构调整 |
| 菜单配置集成 | 集成菜单配置到场景模板 | SceneTemplate |

---

## 六、闭环检测结论

| 闭环类型 | 完整度 | 主要问题 | 修复优先级 |
|----------|:------:|----------|:----------:|
| 分类定义闭环 | 60% | 存在未定义分类、大小写不一致 | P0 |
| 场景故事闭环 | 50% | 角色识别、菜单配置、LLM配置未闭环 | P1 |
| 数据流转闭环 | 70% | 分类与场景类型混淆 | P0 |
| 动作事件闭环 | 40% | 激活流程、能力绑定、菜单注册未集成 | P1 |

**总体评估**: 分类体系存在严重的概念混淆问题，需要立即修复分类定义，并重新设计场景技能类型与分类的关系。

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
