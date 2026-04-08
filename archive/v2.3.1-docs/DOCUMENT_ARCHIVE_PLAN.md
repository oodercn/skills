# 文档归档方案

> **版本**: v1.0.0  
> **日期**: 2026-03-18  
> **目标**: 整理散乱文档，建立清晰的文档结构

---

## 一、当前文档分布分析

### 1.1 文档统计

| 位置 | 文档数量 | 状态 |
|------|:--------:|:----:|
| `docs/v2.3.1/` | ~120 | ✅ 最新版本，保留 |
| `docs/v2.3/` | ~50 | ⚠️ 旧版本，归档 |
| `docs/` 根目录 | ~100 | ❌ 散乱，归档 |
| `mvp/docs/` | 4 | ❌ 子项目，归档 |
| `skill-scene/docs/` | 7 | ❌ 子项目，归档 |
| `skill-ui-test/docs/` | 5 | ❌ 子项目，归档 |
| `temp/` | ~20 | ❌ 临时文件，归档/删除 |
| `releases/` | 2 | ✅ 发布文档，保留 |
| **总计** | **~300** | - |

### 1.2 问题识别

1. **docs/ 根目录散乱**: 100+ 个文档无组织
2. **多版本并存**: v2.3 和 v2.3.1 混杂
3. **子项目文档分散**: mvp、skill-scene、skill-ui-test 各自维护文档
4. **临时文件未清理**: temp/ 目录存在大量临时文档
5. **归档目录已存在但未使用**: v2.3.1/archive/ 已有结构但文档分散

---

## 二、归档方案

### 2.1 新建归档目录结构

```
e:\github\ooder-skills\
├── archive/                          # 新建根目录归档
│   ├── v2.3/                         # v2.3 版本文档
│   │   ├── docs/                     # 从 docs/v2.3/ 移入
│   │   └── merged/                   # 合并后的文档
│   ├── legacy-docs/                  # 散乱文档归档
│   │   ├── analysis/                 # 分析报告
│   │   ├── collaboration/            # 协作文档
│   │   ├── specification/            # 规范文档
│   │   └── reports/                  # 各类报告
│   ├── subprojects/                  # 子项目文档归档
│   │   ├── mvp/                      # MVP 项目文档
│   │   ├── skill-scene/              # Skill-Scene 项目文档
│   │   └── skill-ui-test/            # Skill-UI-Test 项目文档
│   └── temp/                         # 临时文件归档
│       └── ooder-Nexus/              # Nexus 临时文档
```

### 2.2 保留的文档结构

```
e:\github\ooder-skills\
├── docs/
│   └── v2.3.1/                       # ✅ 唯一保留的文档目录
│       ├── README.md                 # 版本说明
│       ├── SKILL_CATEGORY_SPECIFICATION.md
│       ├── UPDATE_GUIDE_FRONTEND_BACKEND.md
│       ├── SKILL_CATEGORY_STATS_FRONTEND.md
│       ├── DOCUMENT_CONFLICT_ANALYSIS.md
│       ├── volume-01-specification/  # 规范卷
│       ├── volume-02-classification/ # 分类卷
│       ├── volume-04-architecture/   # 架构卷
│       ├── volume-05-development/    # 开发卷
│       ├── volume-06-user-stories/   # 用户故事卷
│       └── archive/                  # v2.3.1 内部归档
├── releases/
│   └── README.md                     # 发布说明
├── templates/
│   └── README.md                     # 模板说明
└── README.md                         # 项目主 README
```

---

## 三、归档执行计划

### 3.1 阶段一：创建归档目录

```bash
mkdir -p archive/v2.3/docs
mkdir -p archive/legacy-docs/{analysis,collaboration,specification,reports}
mkdir -p archive/subprojects/{mvp,skill-scene,skill-ui-test}
mkdir -p archive/temp/ooder-Nexus
```

### 3.2 阶段二：移动旧版本文档

| 源路径 | 目标路径 | 操作 |
|--------|----------|------|
| `docs/v2.3/*` | `archive/v2.3/docs/` | 移动 |
| `docs/*.md` | `archive/legacy-docs/` | 按类型分类移动 |

### 3.3 阶段三：移动子项目文档

| 源路径 | 目标路径 |
|--------|----------|
| `mvp/docs/*` | `archive/subprojects/mvp/` |
| `skill-scene/docs/*` | `archive/subprojects/skill-scene/` |
| `skill-ui-test/docs/*` | `archive/subprojects/skill-ui-test/` |

### 3.4 阶段四：清理临时文件

| 源路径 | 操作 |
|--------|------|
| `temp/ooder-Nexus/*.md` | 移动到 `archive/temp/ooder-Nexus/` |
| `temp/protocol-release/` | 移动到 `archive/temp/` |

---

## 四、docs/ 根目录散乱文档分类

### 4.1 分析报告类 (移入 archive/legacy-docs/analysis/)

- ARCHITECTURE_COMPLIANCE_REPORT.md
- CAPABILITY-STRUCTURE-ANALYSIS.md
- CAPABILITY_SCENE_CLOSED_LOOP_STATISTICS.md
- CLOSED_LOOP_VERIFICATION_REPORT.md
- FEATURE_MATCH_ANALYSIS.md
- MAP_DTO_USAGE_REPORT.md
- MVP-ANALYSIS-AND-CHECK.md
- NEXUS_ARCHITECTURE_CHECK_REPORT.md
- NEXUS_SKILL_UI_COVERAGE_ANALYSIS.md
- SKILLS_ANALYSIS_REPORT.md
- SKILLS_CLOSED_LOOP_AVAILABILITY_REPORT.md
- SKILLS_COMPLETION_REPORT.md
- SECURITY_COVERAGE_REPORT.md
- SKILL_SPECIFICATION_CHECK_REPORT.md
- SKILLS-4LEVEL-CHECK-REPORT.md
- SKILLS-DEPENDENCY-ANALYSIS.md
- SKILLS-DIRECTORY-ANALYSIS.md
- SKILLS-PREBUILD-CHECK.md

### 4.2 协作文档类 (移入 archive/legacy-docs/collaboration/)

- COLLABORATION_SKILLS_REQUIREMENTS.md
- ENGINE_COLLABORATION_REQUEST.md
- ENGINE_COLLABORATION_REQUEST_ANALYSIS.md
- EXTERNAL_API_DELEGATION_TASKS.md
- LLM_CONTEXT_COLLABORATION_REQUIREMENTS.md
- SDK_COLLABORATION.md
- SDK_COLLABORATION_REQUIREMENTS.md
- SDK_DELEGATION_DOCUMENT.md
- SDK_SECURITY_EXTENSION_DELEGATION.md
- SDK_SUPPLEMENT_REQUIREMENTS.md
- SDK_SUPPLEMENT_RESPONSE.md
- SE_SKILL_COLLABORATION.md
- SE_SERVICE_EXPOSURE_ISSUE.md
- SE_CONVERSATION_ARCHITECTURE.md

### 4.3 规范文档类 (移入 archive/legacy-docs/specification/)

- CAPABILITY_CONCEPT_CLARIFICATION.md
- CAPABILITY_REQUIREMENT_SPEC.md
- COMMON_TECHNICAL_SPECIFICATION.md
- DICT_SPECIFICATION.md
- GLOSSARY.md
- LLM_REQUIREMENTS_SPECIFICATION.md
- MODULE_MODEL_SPEC.md
- SCENE_DESIGN.md
- SCENE_DRIVER_STRUCTURE.md
- SCENE_REQUIREMENT_SPEC.md
- SECURITY_MODULE_ARCHITECTURE.md
- SECURITY_MODULE_REQUIREMENTS.md
- SKILL-SPECIFICATION-V2.3.md
- SKILL_DEVELOPMENT.md
- SKILL_HOT_PLUG_ARCHITECTURE.md
- SKILL_SYSTEM_HEALTH_DESIGN.md
- SKILL_UI_A2A_SPEC.md
- SKILL_UI_UNIFIED_SPEC.md

### 4.4 报告类 (移入 archive/legacy-docs/reports/)

- DEV_TASKS_OVERVIEW.md
- DAILY_REPORT_USER_STORY_ANALYSIS.md
- ENX-IM-2026-001-TASK-PROGRESS.md
- INSTALLATION-CLOSED-LOOP-ANALYSIS.md
- KNOWLEDGE_DEVELOPMENT_TASKS.md
- LLM_DEVELOPMENT_TASKS.md
- LLM_DISCUSSION_PROGRESS.md
- LLM_ENGINE_INTEGRATION_PLAN.md
- MODULE_SKILLS_MAPPING.md
- NEW_PAGES_ARCHITECTURE_CHECK.md
- CONSOLE_PAGES_ARCHITECTURE_CHECK.md
- NEXUS_ARCH_CHECKER_UPDATED.md
- NEXUS_SKILL_UI_OPTIMIZATION.md
- NEXUS_SKILL_UI_THREE_PHASE_REFACTORING.md
- OODER_SKILLS_ARCHITECTURE.md
- OODER_SKILLS_DEEP_REVEAL.md
- SCENE-CHECK-TECHNICAL-DOC.md
- SCENE_PAGE_CLOSED_LOOP_ANALYSIS.md
- SDK_COVERAGE_ANALYSIS.md
- SKILL-REAL-IMPL-PROGRESS.md
- SKILL_SCENE_MODULE_INVENTORY.md
- UPGRADE_PLAN.md
- skill-faq.md
- skill-mqtt-ARCHITECTURE_ANALYSIS.md
- skills-category-proposal.md

---

## 五、v2.3.1 未决事项

### 5.1 待完成文档

| 文档 | 状态 | 说明 |
|------|:----:|------|
| `volume-03-lifecycle/` | ❌ 空 | 需要补充生命周期文档 |
| `volume-01-specification/CAPABILITY_ADDRESS_SPACE.md` | ⚠️ | 需要更新为最新规范 |
| `volume-02-classification/SCENE_SKILL_CLASSIFICATION.md` | ⚠️ | 需要更新为 biz 分类 |

### 5.2 待确认事项

| 事项 | 问题 | 决策 |
|------|------|------|
| `LLM_CONFIG_TEMPLATE.md` | 是否保留在 v2.3.1 根目录？ | ❓ 待定 |
| `LLM_CONFIG_AUDIT_REPORT.md` | 是否移入 archive？ | ❓ 待定 |
| `DISCOVERY_SERVICE_COLLABORATION.md` | 是否移入 archive？ | ❓ 待定 |
| `CATEGORY_MIGRATION_STATUS.md` | 是否移入 archive？ | ❓ 待定 |
| `CATEGORY_MIGRATION_NOTICE.md` | 是否移入 archive？ | ❓ 待定 |

### 5.3 文档重复问题

| 重复文档 | 位置 | 建议 |
|----------|------|------|
| `SKILL_CATEGORY_STATS.md` | v2.3.1 根目录 | 与 `SKILL_CATEGORY_STATS_FRONTEND.md` 合并 |
| `SKILLS_SYSTEM_PLANNING.md` | v2.3.1 根目录 | 移入 `volume-04-architecture/` |

---

## 六、执行确认

请确认以下事项后开始执行：

1. ✅ 归档目录结构是否正确？
2. ✅ docs/ 根目录文档分类是否正确？
3. ✅ v2.3.1 未决事项如何处理？
4. ✅ 是否需要保留某些文档在原位置？

---

**文档维护者**: Skills Team  
**最后更新**: 2026-03-18  
**版本**: v1.0.0
