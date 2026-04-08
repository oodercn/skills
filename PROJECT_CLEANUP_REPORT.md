# Ooder-Skills 项目目录整理报告

**生成日期**: 2026-04-08  
**检查范围**: `E:\github\ooder-skills\` 根目录（不含 .git 和 skills/ 核心目录）

---

## 一、版本一致性审计结果 ✅

| 检查项 | 结果 |
|--------|------|
| **skill.yaml 总数** | 137 个 |
| **metadata.version = "3.0.1"** | ✅ 100% (全部一致) |
| **旧版本残留 (1.0.0 / 0.7.x)** | ✅ 0 个（已全部修复） |
| **缺失 skill.yaml** | ✅ 0 个（含 _base 4个 SPI 模块） |

> 注：依赖声明中的 `version: "1.0"` 等为依赖最低版本要求，非 skill 自身版本，属正常配置。

---

## 二、冗余文件清单（建议删除）

### P0 — 确认可删除（临时/日志/备份文件）

| 序号 | 绝对路径 | 类型 | 大小估算 | 理由 |
|------|----------|------|----------|------|
| 1 | `E:\github\ooder-skills\deploy-err.txt` | 部署错误日志 | <1KB | 部署过程临时输出，无保留价值 |
| 2 | `E:\github\ooder-skills\deploy-log.txt` | 部署日志 | <1KB | 同上 |
| 3 | `E:\github\ooder-skills\deploy-out.txt` | 部署输出 | <1KB | 同上 |
| 4 | `E:\github\ooder-skills\test-process.json` | 临时测试数据 | <5KB | 测试中间产物 |
| 5 | `E:\github\ooder-skills\skill-index.yaml.bak` | 备份文件 | ~50KB | 已有正式 skill-index.yaml |
| 6 | `E:\github\ooder-skills\mvp\error_log.txt` | 错误日志 | <1KB | MVP 调试产物 |
| 7 | `E:\github\ooder-skills\mvp\test-stream.txt` | 测试流输出 | <1KB | 调试产物 |
| 8 | `E:\github\ooder-skills\skill-ui-test\test-doc.txt` | 测试文档 | <1KB | 测试项目遗留 |

**删除命令**:
```powershell
Remove-Item deploy-err.txt, deploy-log.txt, deploy-out.txt, test-process.json, skill-index.yaml.bak -Force
Remove-Item mvp\error_log.txt, mvp\test-stream.txt, skill-ui-test\test-doc.txt -Force
```

---

### P1 — 建议归档或删除（废弃/重复项目）

#### 1.1 重复项目目录

| 项目 | 根目录路径 | 对应正式位置 | 建议 |
|------|-----------|-------------|------|
| **skill-hotplug-starter** | `E:\github\ooder-skills\skill-hotplug-starter\` | `E:\github\ooder-skills\app\skill-hotplug-starter\` | 🗑️ 删除根目录副本（app/ 下为正式版） |

**对比**:
- 根目录版本：仅含 README.md + pom.xml（骨架）
- app/ 版本：含完整 src/main/java 源码（正式版）

#### 1.2 废弃/实验性项目

| 项目 | 绝对路径 | 内容 | 建议 |
|------|----------|------|------|
| **ooder-nexus-dev** | `E:\github\ooder-skills\ooder-nexus-dev\` | Nexus UI 原型（html/css/js） | 🗑️ 删除 — 已被 skills/ 替代 |
| **skill-ui-test** | `E:\github\ooder-skills\skill-ui-test\` | UI 测试项目 | 🗑️ 删除 — 实验性质，无生产价值 |
| **skill-scene** | `E:\github\ooder-skills\skill-scene\` | 场景技能早期实验 | 🗑️ 删除 — 功能已合并到 skills/scenes/ |

#### 1.3 孤立文件（非 Maven 项目结构）

| 目录 | 绝对路径 | 内容 | 建议 |
|------|----------|------|------|
| **nexus-config/** | `E:\github\ooder-skills\nexus-config\` | 仅 1 个 StaticResourceConfig.java | 🗑️ 删除 — 散落文件，无 pom.xml |
| **nexus-service/** | `E:\github\ooder-skills\nexus-service\` | 5 个散落 .java 文件 | 🗑️ 删除 — 无项目结构，代码已迁移 |

**删除命令**:
```powershell
Remove-Item -Recurse -Force skill-hotplug-starter
Remove-Item -Recurse -Force ooder-nexus-dev
Remove-Item -Recurse -Force skill-ui-test
Remove-Item -Recurse -Force skill-scene
Remove-Item -Recurse -Force nexus-config
Remove-Item -Recurse -Force nexus-service
```

---

### P2 — 建议归档（历史文档，量大但可能有参考价值）

#### 2.1 docs/v2.3.1/ — 旧版本报告（18 个文件）

| 代表文件 | 说明 |
|----------|------|
| `DOCUMENT_ARCHIVE_PLAN.md` | v2.3.1 归档计划 |
| `DOCUMENT_CONFLICT_ANALYSIS.md` | 冲突分析 |
| `SKILLS_SYSTEM_PLANNING.md` | 旧版技能规划 |
| `SKILL-DEVELOPMENT-PROGRESS.md` | 开发进度（已过时） |
| ... 等 14 个 | 均为 v2.3.1 时期文档 |

**建议**: 移入 `archive/v2.3.1/docs/` 或直接删除（archive/v2.3/ 已有归档）

#### 2.2 docs/v3.0.1/ — 审计报告（17 个文件）

| 代表文件 | 说明 |
|----------|------|
| `AUDIT_REPORT.md` | 审计报告 |
| `FINAL_AUDIT_REPORT.md` | 最终审计（与 skills/ 下的 FINAL 冗余） |
| `MERGE_TASK_LIST.md` | 合并任务列表（已完成） |
| `REMAINING_TASKS.md` | 剩余任务（已完成） |
| ... 等 13 个 | 均为过程性文档 |

**建议**: 精简保留 2-3 个核心报告（AUDIT + FINAL），其余移入 archive 或删除。

#### 2.3 docs/ 根级散落文件（~20 个）

| 类别 | 数量 | 示例 | 建议 |
|------|------|------|------|
| 审计报告 | 6 | `*_AUDIT_*.md`, `*_REPORT.md` | 📦 归档到 archive/ |
| SVG 图表 | 16 | `diagram_*.svg` | ⚠️ 保留（docs/ 下的旧版图表） |
| 设计文档 | 4 | `*.md`（A2UI/BPM/工作流相关） | ✅ 保留（仍在参考中） |

#### 2.4 archive/ 目录

| 子目录 | 内容 | 建议 |
|--------|------|------|
| `legacy-docs/reports/` | 旧 FAQ 报告 | ✅ 保留归档 |
| `v2.3/docs/v2.3/` | v2.3 术语表 | ✅ 保留归档 |
| `v3.0.1/INDEX.md` | v3.0.1 索引 | ✅ 保留归档 |
| `v3.0.1/process/` | **大量过程文档 (~80+ 个)** | 🗑️ 可安全删除（决策过程记录，结果已落地） |

---

### P3 — 保留但需关注的项目

| 项目 | 绝对路径 | 说明 | 建议 |
|------|----------|------|------|
| **mvp/** | `E:\github\ooder-skills\mvp\` | MVP 原型项目（含 FreeMarker 模板、配置等） | 🔶 保留 — 可能用于演示/参考 |
| **scripts/** | `E:\github\ooder-skills\scripts\` | 14 个 Python/PS 工具脚本 | ✅ 保留 — 构建辅助工具 |
| **templates/** | `E:\github\ooder-skills\templates\` | 5 个 YAML 模板 + README | ✅ 保留 — 技能开发模板 |
| **skill-index/** | `E:\github\ooder-skills\skill-index\` | 技能索引 YAML 定义 | ✅ 保留 — 分类索引 |
| **releases/** | `E:\github\ooder-skills\releases\` | 发布说明 | ✅ 保留 |
| **GITHUB_SKILLS_PLAN.md** | 根目录 | GitHub 规划文档 | 📦 可移入 docs/ |
| **skills_classification_report.md** | 根目录 | 分类报告 | 📦 可移入 docs/ 或 archive/ |
| **场景组持久化加载问题-优化方案讨论.md** | 根目录 | 讨论文档 | 📦 可移入 docs/ |
| **update_version.py** | 根目录 | 版本更新脚本 | ✅ 保留（已使用过） |
| **skill-classification.yaml** | 根目录 | 分类定义 | 🔶 与 skill-index/ 可能重复，需确认 |
| **RELEASE_GUIDE.md** | 根目录 | 发布指南 | ✅ 保留 |

---

## 三、推荐清理操作汇总

### 立即执行（安全删除）

```powershell
# === P0: 临时文件（8个）===
cd E:\github\ooder-skills
Remove-Item deploy-err.txt, deploy-log.txt, deploy-out.txt, test-process.json, skill-index.yaml.bak -Force
Remove-Item mvp\error_log.txt, mvp\test-stream.txt -Force
Remove-Item skill-ui-test\test-doc.txt -Force

# === P1: 废弃项目（6个目录）===
Remove-Item -Recurse -Force skill-hotplug-starter      # 重复(根目录 vs app/)
Remove-Item -Recurse -Force ooder-nexus-dev            # 废弃原型
Remove-Item -Recurse -Force skill-ui-test               # 废弃测试
Remove-Item -Recurse -Force skill-scene                 # 废弃实验
Remove-Item -Recurse -Force nexus-config                # 散落文件
Remove-Item -Recurse -Force nexus-service               # 散落文件
```

### 可选执行（归档清理）

```powershell
# === P2: 过程文档归档 ===
# 将 docs/v2.3.1/ 移入 archive/
Move-Item docs\v2.3.1 archive\v2.3.1-audit-docs -Force

# 清理 archive/v3.0.1/process/ （80+ 过程文档）
Remove-Item -Recurse -Force archive\v3.0.1\process

# 整理根目录散落 .md
Move-Item GITHUB_SKILLS_PLAN.md docs\ -Force
Move-Item skills_classification_report.md archive\ -Force
Move-Item "场景组持久化加载问题-优化方案讨论.md" docs\ -Force
```

---

## 四、清理前后对比

| 指标 | 清理前 | 清理后（预计） |
|------|--------|----------------|
| 根目录一级文件数 | ~15 个 | ~8 个 |
| 根目录一级目录数 | ~18 个 | ~12 个 |
| 废弃/重复项目 | 6 个 | 0 个 |
| 临时/日志文件 | 8 个 | 0 个 |
| 过程性文档 | ~100+ 个 | ~20 个（精简后） |

---

## 五、保留的核心目录结构（清理后预期）

```
E:\github\ooder-skills\
├── skills/              ★ 核心技能库（137+ 模块）
├── app/                应用模块（common/hotplug/org-base）
├── mvp/                MVP 原型（演示用）
├── templates/           技能开发模板
├── skill-index/         技能分类索引
├── scripts/             构建/打包工具脚本
├── docs/                设计文档 & 规范（精简后）
│   ├── bpm-designer/    BPM 设计器规范
│   ├── bpm-spec/        BPM 规格定义
│   └── [保留的设计文档]
├── archive/             历史归档
├── releases/            发布说明
├── .github/workflows/   CI/CD
├── pom.xml              Maven 父 POM
├── LICENSE              MIT License
├── README.md            项目主页（★ 本次更新）
└── RELEASE_GUIDE.md     发布指南
```
