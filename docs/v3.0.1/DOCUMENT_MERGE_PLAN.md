# 文档合并方案 v3.0.1

**版本**: 3.0.1  
**创建日期**: 2026-04-02  
**状态**: 执行中  
**目标**: 将所有版本文档合并到v3.0.1，建立唯一的文档中心

---

## 一、文档现状分析

### 1.1 版本分布

| 版本 | 位置 | 文档数量 | 状态 | 处理方案 |
|------|------|---------|------|---------|
| **v3.0.1** | docs/v3.0.1/ | ~30 | ✅ 当前版本 | 保留并扩充 |
| **v2.3.1** | docs/v2.3.1/ | ~120 | ⚠️ 上一版本 | 合并后归档 |
| **v2.3** | archive/v2.3/ | ~50 | ❌ 历史版本 | 合并后删除 |
| **legacy-docs** | archive/legacy-docs/ | ~100 | ❌ 散乱文档 | 合并后删除 |
| **subprojects** | archive/subprojects/ | ~20 | ❌ 子项目文档 | 合并后删除 |

### 1.2 关键发现

#### ABS/ASS/TBS废弃概念

**记录位置**: 
- `docs/v2.3.1/archive/scene/scene-skill-classification-spec-v2.md`
- `docs/v2.3.1/archive/scene/abs-tbs-ass-code-analysis-report.md`

**废弃说明**:
- ABS/ASS/TBS分类已废弃
- 改用 **SceneType + visibility** 二维分类体系
- 对照关系：
  - ABS → `SceneType.AUTO` + `visibility=public`
  - ASS → `SceneType.AUTO` + `visibility=internal`
  - TBS → `SceneType.TRIGGER` + `visibility=public`

**代码位置**:
- `skill-classification.yaml` 仍使用ABS/ASS/TBS，需要更新

---

## 二、文档冲突分析

### 2.1 已解决冲突

| 冲突项 | 解决方案 | 状态 |
|--------|---------|------|
| 分类体系 | 新增biz分类，统一为12个分类 | ✅ 已解决 |
| skill-index格式 | 采用includes引用格式 | ✅ 已解决 |
| 文档结构 | 建立volume分层体系 | ✅ 已解决 |

### 2.2 待解决冲突

| 冲突项 | 描述 | 优先级 | 解决方案 |
|--------|------|--------|---------|
| skill-classification.yaml | 仍使用废弃的ABS/ASS/TBS | P0 | 更新为SceneType+visibility |
| 文档版本号不一致 | v2.3.1和v3.0.1内容相同但版本号不同 | P1 | 统一为v3.0.1 |
| 重复文档 | 多个版本存在相同文档 | P1 | 合并去重 |
| 缺失文档 | volume-03-lifecycle为空 | P2 | 补充生命周期文档 |

---

## 三、合并策略

### 3.1 保留原则

1. **最新优先**: 保留最新版本的文档
2. **价值保留**: 保留有历史价值的设计文档
3. **去重合并**: 合并重复内容，保留最完整版本
4. **规范统一**: 统一文档格式和版本号

### 3.2 合并规则

| 源文档 | 目标位置 | 操作 |
|--------|---------|------|
| v2.3.1/volume-*/* | v3.0.1/volume-*/* | 合并（如v3.0.1不存在） |
| v2.3.1/README.md | v3.0.1/README.md | 更新版本号和内容 |
| v2.3.1/*.md | v3.0.1/*.md | 合并（如v3.0.1不存在） |
| archive/legacy-docs/specification/* | v3.0.1/volume-01-specification/ | 按需合并 |
| archive/legacy-docs/analysis/* | v3.0.1/process/analysis/ | 归档 |

---

## 四、执行计划

### Phase 1: 准备工作 (已完成)

- [x] 分析文档现状
- [x] 识别ABS/ASS/TBS废弃概念
- [x] 分析文档冲突点
- [x] 制定合并策略

### Phase 2: 核心文档合并 (进行中)

#### 4.2.1 更新skill-classification.yaml

**当前问题**: 仍使用废弃的ABS/ASS/TBS分类

**解决方案**: 更新为SceneType + visibility二维分类

```yaml
# 旧版本 (需更新)
sceneCategories:
  - id: abs
    name: 自驱业务场景
  - id: ass
    name: 自驱系统场景
  - id: tbs
    name: 触发业务场景

# 新版本 (建议)
sceneTypes:
  - id: auto
    name: 自驱场景
    description: 自动运行，hasSelfDrive=true
  - id: trigger
    name: 触发场景
    description: 需要触发，hasSelfDrive=false

visibilityTypes:
  - id: public
    name: 公开可见
    description: 用户可发现、可激活
  - id: internal
    name: 内部使用
    description: 后台运行，用户不可见
```

#### 4.2.2 合并volume文档

| 操作 | 源 | 目标 | 说明 |
|------|----|----|------|
| 保留 | v3.0.1/volume-01-specification/ | - | 已存在 |
| 保留 | v3.0.1/volume-02-classification/ | - | 已存在 |
| 创建 | - | v3.0.1/volume-03-lifecycle/ | 补充生命周期文档 |
| 保留 | v3.0.1/volume-04-architecture/ | - | 已存在 |
| 保留 | v3.0.1/volume-05-development/ | - | 已存在 |
| 保留 | v3.0.1/volume-06-user-stories/ | - | 已存在 |

#### 4.2.3 合并根目录文档

| 文档 | 操作 | 说明 |
|------|------|------|
| README.md | 更新 | 更新版本号和内容 |
| SKILL_CATEGORY_SPECIFICATION.md | 保留 | 已存在 |
| SKILLS_SYSTEM_PLANNING.md | 保留 | 已存在 |
| UPDATE_GUIDE_FRONTEND_BACKEND.md | 保留 | 已存在 |
| SKILL_CATEGORY_STATS_FRONTEND.md | 保留 | 已存在 |
| SKILLS_INVENTORY.md | 保留 | v3.0.1独有 |
| APEX_MVP_SKILLS_BUILD_GUIDE.md | 保留 | v3.0.1独有 |

### Phase 3: 归档历史版本

#### 4.3.1 归档v2.3.1

```bash
# 移动v2.3.1到归档目录
mv docs/v2.3.1 archive/v2.3.1-merged-20260402
```

#### 4.3.2 归档v2.3

```bash
# v2.3已在archive中，合并后删除重复内容
rm -rf archive/v2.3/docs/v2.3/volume-*  # 已合并到v3.0.1
```

#### 4.3.3 归档legacy-docs

```bash
# 保留有价值的规范文档到v3.0.1/process/
# 删除其他散乱文档
```

### Phase 4: 清理与验证

- [ ] 删除重复文档
- [ ] 验证链接有效性
- [ ] 更新所有文档版本号为v3.0.1
- [ ] 验证skill-index.yaml引用正确

---

## 五、合并后的文档结构

```
docs/v3.0.1/
├── README.md                           # 版本说明
├── DOCUMENT_MERGE_PLAN.md              # 本文档
├── SKILL_CATEGORY_SPECIFICATION.md     # 分类规范
├── SKILLS_SYSTEM_PLANNING.md           # 系统规划
├── UPDATE_GUIDE_FRONTEND_BACKEND.md    # 前后端更新指南
├── SKILL_CATEGORY_STATS_FRONTEND.md    # 分类统计前端
├── SKILLS_INVENTORY.md                 # 技能清单
├── APEX_MVP_SKILLS_BUILD_GUIDE.md      # MVP构建指南
│
├── volume-01-specification/            # 规范卷
│   ├── CAPABILITY_ADDRESS_SPACE.md
│   └── SKILL_YAML_STANDARD.md          # 从v2.3.1补充
│
├── volume-02-classification/           # 分类卷
│   └── SCENE_SKILL_CLASSIFICATION.md
│
├── volume-03-lifecycle/                # 生命周期卷 (新增)
│   ├── SCENE_LIFECYCLE.md              # 新增
│   └── CAPABILITY_LIFECYCLE.md         # 新增
│
├── volume-04-architecture/             # 架构卷
│   ├── COMPLETE_KNOWLEDGE_GRAPH.md
│   └── SKILLS_PAGE_API_KNOWLEDGE_GRAPH.md
│
├── volume-05-development/              # 开发卷
│   ├── CAPABILITY_MODULE_GUIDE.md
│   ├── SCENE_MODULE_GUIDE.md
│   ├── PAGE_ARCHITECTURE_CHECKLIST.md
│   ├── PAGE_BY_PAGE_CHECK_REPORT.md
│   └── PAGE_INSPECTION_REPORT.md
│
├── volume-06-user-stories/             # 用户故事卷
│   └── USER_CLOSED_LOOP_STORIES.md
│
└── process/                            # 过程文档归档
    ├── analysis/                       # 分析报告
    ├── collaboration/                  # 协作文档
    ├── execution/                      # 执行计划
    └── scene/                          # 场景相关
```

---

## 六、风险与应对

### 6.1 风险评估

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|---------|
| 丢失重要设计文档 | 高 | 中 | 合并前完整备份 |
| 链接失效 | 中 | 高 | 更新所有内部链接 |
| 版本混乱 | 中 | 中 | 统一版本号管理 |
| 代码与文档不一致 | 高 | 中 | 同步更新代码和文档 |

### 6.2 回滚方案

```bash
# 如需回滚，从备份恢复
git checkout backup-before-merge
```

---

## 七、后续维护

### 7.1 文档更新规范

1. **唯一版本**: 所有文档统一在v3.0.1目录维护
2. **版本号管理**: 文档版本号与代码版本号同步
3. **变更记录**: 每个文档底部维护变更历史
4. **审核流程**: 重要文档变更需要审核

### 7.2 文档分类规范

| 分类 | 位置 | 说明 |
|------|------|------|
| 规范文档 | volume-01~03 | 正式规范，长期维护 |
| 架构文档 | volume-04 | 架构设计，定期更新 |
| 开发文档 | volume-05 | 开发指南，随代码更新 |
| 用户故事 | volume-06 | 需求文档，迭代更新 |
| 过程文档 | process/ | 过程记录，归档保存 |

---

**文档维护**: Ooder Team  
**最后更新**: 2026-04-02  
**下次审核**: 2026-04-09
