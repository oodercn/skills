# 版本迁移说明：v0.8.0 → v2.3

## 一、迁移概述

| 项目 | 内容 |
|------|------|
| 源版本 | v0.8.0 |
| 目标版本 | v2.3 |
| 迁移日期 | 2026-02-27 |
| 迁移类型 | 主版本升级 |

---

## 二、迁移原因

SDK 团队进行了大幅重构，版本号从 0.8.0 直接跳升到 2.3，主要原因：

1. **架构重构**：agent-sdk 从单一模块变更为多模块父工程
2. **版本统一**：所有模块版本统一为 2.3
3. **代码精简**：删除约 70 个冗余文件
4. **能力扩展**：新增 skill-ai 模块

---

## 三、关键变更

### 3.1 目录结构变更

```
迁移前：
docs/v0.8.0/
├── ARCHITECTURE-V0.8.0.md
├── SKILLS_V0.8.0_SUMMARY.md
├── V0.8.0-COMPREHENSIVE-PLAN.md
└── ...

迁移后：
docs/v2.3/
├── ARCHITECTURE-V2.3.md
├── SKILLS_V2.3_SUMMARY.md
├── V2.3-COMPREHENSIVE-PLAN.md
└── ...
```

### 3.2 文件重命名

| 原文件名 | 新文件名 |
|----------|----------|
| ARCHITECTURE-V0.8.0.md | ARCHITECTURE-V2.3.md |
| SKILLS_V0.8.0_SUMMARY.md | SKILLS_V2.3_SUMMARY.md |
| V0.8.0-COMPREHENSIVE-PLAN.md | V2.3-COMPREHENSIVE-PLAN.md |

### 3.3 内容版本号更新

所有文档内容中的版本号已更新：
- `v0.8.0` → `v2.3`
- `V0.8.0` → `V2.3`
- `0.8.0` → `2.3`

---

## 四、SDK 关键变更

### 4.1 agent-sdk 模块化

> ⚠️ **重要**：agent-sdk 是父工程（pom 类型），**不能作为依赖使用**！

```
agent-sdk/                     # 父工程 (pom) ❌ 不能作为依赖
├── agent-sdk-api/             # 子模块 (jar) ✅ 推荐使用
├── agent-sdk-core/            # 子模块 (jar) ✅ 完整实现
├── skills-framework/          # 子模块 (jar)
├── llm-sdk-api/               # 子模块 (jar)
└── llm-sdk/                   # 子模块 (jar)
```

### 4.2 依赖更新

```xml
<!-- ❌ 错误：agent-sdk 是父工程 -->
<dependency>
    <artifactId>agent-sdk</artifactId>
    <version>2.3</version>
</dependency>

<!-- ✅ 正确：使用子模块 -->
<dependency>
    <artifactId>agent-sdk-api</artifactId>
    <version>2.3</version>
</dependency>
```

### 4.3 新增模块

| 模块 | 说明 |
|------|------|
| skill-ai | AIGC/MCP/工作流能力 |
| agent-sdk-api | 轻量级 API 接口 |
| agent-sdk-core | 完整实现 |
| skills-framework | 技能框架 |
| llm-sdk-api | LLM 轻量级 API |

---

## 五、迁移清单

### 5.1 已完成

- [x] 创建 v2.3 目录结构
- [x] 迁移文档文件到 v2.3 目录
- [x] 重命名文件（0.8.0 → 2.3）
- [x] 更新文档内容中的版本号
- [x] 创建版本迁移说明文档

### 5.2 待完成

- [ ] 删除旧的 v0.8.0 目录（需确认）
- [ ] 更新项目根目录的 README.md
- [ ] 通知相关团队版本变更

---

## 六、影响范围

### 6.1 受影响的文档

| 文档 | 状态 | 说明 |
|------|------|------|
| TEAM-COLLABORATION-TASKS.md | ✅ 已更新 | 添加重要提示章节 |
| SDK_V2.3_UPGRADE_PLAN.md | ✅ 已更新 | 依赖示例已修正 |
| SDK_V2.3_CRITICAL_CHANGE_NOTICE.md | ✅ 新建 | 重要变更通知 |
| A2UI_SKILL_COMPLETE_ANALYSIS.md | ✅ 已更新 | 版本号已更新 |
| SDK_2.3_A2UI_COMPARISON.md | ✅ 已更新 | 模块版本表已修正 |

### 6.2 受影响的代码

| 项目 | 影响 | 操作 |
|------|------|------|
| skills/skill-a2ui | 需升级 SDK 依赖 | 更新 pom.xml |
| skills/skill-* | 需升级 SDK 依赖 | 更新 pom.xml |
| scene-engine | 需升级版本 | 更新 pom.xml |

---

## 七、后续行动

1. **验证构建**：确保所有项目能正常构建
2. **更新 CI/CD**：更新持续集成配置
3. **通知团队**：通知相关团队版本变更
4. **清理旧目录**：确认后删除 v0.8.0 目录

---

## 八、参考文档

- [SDK_V2.3_CRITICAL_CHANGE_NOTICE.md](./SDK_V2.3_CRITICAL_CHANGE_NOTICE.md)
- [SDK_V2.3_UPGRADE_PLAN.md](./SDK_V2.3_UPGRADE_PLAN.md)
- [SDK_2.3_A2UI_COMPARISON.md](./SDK_2.3_A2UI_COMPARISON.md)

---

**文档版本**：v1.0  
**创建日期**：2026-02-27  
**最后更新**：2026-02-27
