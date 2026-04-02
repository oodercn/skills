# OS Skills 合并方案任务列表

> 文档路径: `e:\github\ooder-skills\docs\v3.0.1\MERGE_TASK_LIST.md`
> 创建时间: 2026-04-03
> 状态: 执行中

---

## 一、任务概览

### 1.1 项目背景
- **源库**: `E:\apex\os\skills` (DEV开发库)
- **目标库**: `e:\github\ooder-skills` (发布库)
- **迁移方向**: OS → Skills (开发调试完成后发布到Skills)

### 1.2 总体进度

| 阶段 | 状态 | 完成度 |
|------|------|--------|
| Phase 0: 分析与规划 | ✅ 已完成 | 100% |
| Phase 1: 核心功能补齐 | ✅ 已完成 | 100% |
| Phase 2: UI配置处理 | ⏳ 待执行 | 0% |
| Phase 3: 性能优化 | ⏳ 待执行 | 0% |
| Phase 4: 技能迁移 | ⏳ 待执行 | 0% |
| Phase 5: 测试与验证 | ⏳ 待执行 | 0% |

---

## 二、Phase 0: 分析与规划 (已完成)

### 2.1 文档创建 ✅

| 任务 | 状态 | 产出文档 |
|------|------|----------|
| OS Skills对比分析 | ✅ 完成 | [OS_SKILLS_COMPARISON_ANALYSIS.md](file:///e:/github/ooder-skills/docs/v3.0.1/OS_SKILLS_COMPARISON_ANALYSIS.md) |
| 热插拔机制分析 | ✅ 完成 | [OS_MIGRATION_AND_HOTPLUG_ANALYSIS.md](file:///e:/github/ooder-skills/docs/v3.0.1/OS_MIGRATION_AND_HOTPLUG_ANALYSIS.md) |
| 驱动对比分析 | ✅ 完成 | [DRIVER_COMPARISON_ANALYSIS.md](file:///e:/github/ooder-skills/docs/v3.0.1/DRIVER_COMPARISON_ANALYSIS.md) |
| 页面迁移分析 | ✅ 完成 | [PAGE_MIGRATION_ANALYSIS.md](file:///e:/github/ooder-skills/docs/v3.0.1/PAGE_MIGRATION_ANALYSIS.md) |
| 迁移指南编写 | ✅ 完成 | [OS_TO_SKILLS_MIGRATION_GUIDE.md](file:///e:/github/ooder-skills/docs/v3.0.1/OS_TO_SKILLS_MIGRATION_GUIDE.md) |
| 代码与设计差距分析 | ✅ 完成 | [CODE_VS_DESIGN_GAP_ANALYSIS.md](file:///e:/github/ooder-skills/docs/v3.0.1/CODE_VS_DESIGN_GAP_ANALYSIS.md) |
| 能力库规划 | ✅ 完成 | [CAPABILITY_LIBRARY_PLANNING.md](file:///e:/github/ooder-skills/docs/v3.0.1/CAPABILITY_LIBRARY_PLANNING.md) |
| 能力库实现计划 | ✅ 完成 | [CAPABILITY_LIBRARY_IMPLEMENTATION_PLAN.md](file:///e:/github/ooder-skills/docs/v3.0.1/CAPABILITY_LIBRARY_IMPLEMENTATION_PLAN.md) |
| JAR打包发布指南 | ✅ 完成 | [SKILL_JAR_PACKAGE_AND_PUBLISH_GUIDE.md](file:///e:/github/ooder-skills/docs/v3.0.1/SKILL_JAR_PACKAGE_AND_PUBLISH_GUIDE.md) |
| 文档合并计划 | ✅ 完成 | [DOCUMENT_MERGE_PLAN.md](file:///e:/github/ooder-skills/docs/v3.0.1/DOCUMENT_MERGE_PLAN.md) |

### 2.2 分类体系更新 ✅

| 任务 | 状态 | 说明 |
|------|------|------|
| 废弃 ABS/ASS/TBS 分类 | ✅ 完成 | 更新 skill-classification.yaml |
| 采用 SceneType + visibility | ✅ 完成 | AUTO/TRIGGER + public/internal |

---

## 三、Phase 1: 核心功能补齐 (已完成)

### 3.1 静态资源处理 ✅

| 任务 | 状态 | 文件路径 |
|------|------|----------|
| 创建 SkillResourceController | ✅ 完成 | [SkillResourceController.java](file:///e:/github/ooder-skills/skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/controller/SkillResourceController.java) |
| 扩展 PluginContext | ✅ 完成 | [PluginContext.java](file:///e:/github/ooder-skills/skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/model/PluginContext.java) |
| 扩展 PluginManager | ✅ 完成 | [PluginManager.java](file:///e:/github/ooder-skills/skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/PluginManager.java) |

**功能说明**:
- 支持 `/skill/{skillId}/**` 路径访问Skill内嵌静态资源
- 资源从JAR包的 `static/` 目录读取
- 支持CSS、JS、HTML、图片等常见资源类型

### 3.2 表单解析支持 ✅

| 任务 | 状态 | 文件路径 |
|------|------|----------|
| 创建 SkillForm | ✅ 完成 | [SkillForm.java](file:///e:/github/ooder-skills/skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/SkillForm.java) |
| 创建 SkillFormResolver | ✅ 完成 | [SkillFormResolver.java](file:///e:/github/ooder-skills/skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/SkillFormResolver.java) |

### 3.3 类型转换器 ✅

| 任务 | 状态 | 文件路径 |
|------|------|----------|
| 创建 DefaultConverters | ✅ 完成 | [DefaultConverters.java](file:///e:/github/ooder-skills/skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/converter/DefaultConverters.java) |
| 创建 SkillPackageConverter | ✅ 完成 | [SkillPackageConverter.java](file:///e:/github/ooder-skills/skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/converter/SkillPackageConverter.java) |

### 3.4 发现与过滤 ✅

| 任务 | 状态 | 文件路径 |
|------|------|----------|
| 创建 SkillDiscoveryFilter | ✅ 完成 | [SkillDiscoveryFilter.java](file:///e:/github/ooder-skills/skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/discovery/SkillDiscoveryFilter.java) |
| 创建 CategoryResolver | ✅ 完成 | [CategoryResolver.java](file:///e:/github/ooder-skills/skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/CategoryResolver.java) |

### 3.5 代码提交 ✅

| 任务 | 状态 | Commit |
|------|------|--------|
| Git提交 | ✅ 完成 | `97f989b` - feat: 添加静态资源处理功能 |

---

## 四、Phase 2: UI配置处理 (待执行)

### 4.1 UI配置加载机制

| 任务 | 优先级 | 状态 | 说明 |
|------|--------|------|------|
| 实现 UI配置解析器 | P0 | ⏳ 待执行 | 解析skill.yaml中的ui配置 |
| 实现页面路由注册 | P0 | ⏳ 待执行 | 将UI页面注册到前端路由 |
| 实现组件动态加载 | P1 | ⏳ 待执行 | 支持远程组件加载 |

### 4.2 页面资源处理

| 任务 | 优先级 | 状态 | 说明 |
|------|--------|------|------|
| 页面资源打包规范 | P0 | ⏳ 待执行 | 定义页面资源在JAR中的存放规范 |
| 页面缓存策略 | P1 | ⏳ 待执行 | 实现页面资源缓存优化 |
| 页面版本管理 | P1 | ⏳ 待执行 | 支持页面版本控制 |

---

## 五、Phase 3: 性能优化 (待执行)

### 5.1 加载优化

| 任务 | 优先级 | 状态 | 说明 |
|------|--------|------|------|
| Skill懒加载 | P0 | ⏳ 待执行 | 按需加载Skill JAR |
| 资源预加载 | P1 | ⏳ 待执行 | 预加载常用资源 |
| 类加载优化 | P1 | ⏳ 待执行 | 优化PluginClassLoader |

### 5.2 缓存优化

| 任务 | 优先级 | 状态 | 说明 |
|------|--------|------|------|
| 元数据缓存 | P0 | ⏳ 待执行 | 缓存SkillMetadata |
| 路由缓存 | P1 | ⏳ 待执行 | 缓存路由映射 |
| 服务缓存 | P1 | ⏳ 待执行 | 缓存服务实例 |

---

## 六、Phase 4: 技能迁移 (待执行)

### 6.1 OS独有技能迁移

以下技能在OS中存在但Skills中缺失，需要迁移：

| 技能ID | 类型 | 优先级 | 状态 | 说明 |
|--------|------|--------|------|------|
| skill-scenes | Scene | P0 | ⏳ 待迁移 | 场景组管理，80个API |
| skill-todo | Tool | P1 | ⏳ 待迁移 | 待办工具 |
| skill-calendar | Tool | P1 | ⏳ 待迁移 | 日历服务 |

### 6.2 冲突技能处理

以下技能在两个库中都存在，需要合并：

| 技能ID | 冲突类型 | 优先级 | 状态 | 处理策略 |
|--------|----------|--------|------|----------|
| skill-llm-base | 版本差异 | P0 | ⏳ 待处理 | 合并最新功能 |
| skill-email | 功能差异 | P1 | ⏳ 待处理 | 合并API |

### 6.3 驱动迁移

| 驱动类型 | OS数量 | Skills数量 | 状态 | 说明 |
|----------|--------|------------|------|------|
| LLM驱动 | 6 | 6 | ✅ 一致 | 无需迁移 |
| 媒体驱动 | 5 | 5 | ✅ 一致 | 无需迁移 |
| 组织驱动 | 4 | 4 | ✅ 一致 | 无需迁移 |
| 支付驱动 | 3 | 3 | ✅ 一致 | 无需迁移 |
| VFS驱动 | 6 | 6 | ✅ 一致 | 无需迁移 |

---

## 七、Phase 5: 测试与验证 (待执行)

### 7.1 单元测试

| 任务 | 优先级 | 状态 | 说明 |
|------|--------|------|------|
| SkillResourceController测试 | P0 | ⏳ 待执行 | 静态资源访问测试 |
| PluginManager测试 | P0 | ⏳ 待执行 | 插件管理测试 |
| RouteRegistry测试 | P0 | ⏳ 待执行 | 路由注册测试 |

### 7.2 集成测试

| 任务 | 优先级 | 状态 | 说明 |
|------|--------|------|------|
| Skill安装流程测试 | P0 | ⏳ 待执行 | 完整安装流程 |
| Skill卸载流程测试 | P0 | ⏳ 待执行 | 完整卸载流程 |
| Skill更新流程测试 | P1 | ⏳ 待执行 | 版本更新流程 |

### 7.3 性能测试

| 任务 | 优先级 | 状态 | 说明 |
|------|--------|------|------|
| 加载性能测试 | P1 | ⏳ 待执行 | 测试加载时间 |
| 内存占用测试 | P1 | ⏳ 待执行 | 测试内存使用 |
| 并发测试 | P1 | ⏳ 待执行 | 测试并发处理 |

---

## 八、关键文件清单

### 8.1 核心代码文件

| 文件 | 路径 | 说明 |
|------|------|------|
| SkillResourceController | `skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/controller/SkillResourceController.java` | 静态资源处理 |
| PluginManager | `skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/PluginManager.java` | 插件管理 |
| PluginContext | `skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/model/PluginContext.java` | 插件上下文 |
| RouteRegistry | `skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/registry/RouteRegistry.java` | 路由注册 |
| SkillPackage | `skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/model/SkillPackage.java` | 技能包模型 |

### 8.2 配置文件

| 文件 | 路径 | 说明 |
|------|------|------|
| skill-classification.yaml | `skill-classification.yaml` | 技能分类定义 |

### 8.3 文档文件

| 文件 | 路径 | 说明 |
|------|------|------|
| 合并任务列表 | `docs/v3.0.1/MERGE_TASK_LIST.md` | 本文档 |
| 迁移指南 | `docs/v3.0.1/OS_TO_SKILLS_MIGRATION_GUIDE.md` | 迁移操作指南 |
| 差距分析 | `docs/v3.0.1/CODE_VS_DESIGN_GAP_ANALYSIS.md` | 代码与设计差距 |

---

## 九、风险与依赖

### 9.1 风险项

| 风险 | 等级 | 缓解措施 |
|------|------|----------|
| 类加载冲突 | 高 | 使用独立的PluginClassLoader |
| 资源路径冲突 | 中 | 使用 `/skill/{skillId}/` 前缀隔离 |
| 版本兼容性 | 中 | 严格的版本号管理 |

### 9.2 外部依赖

| 依赖 | 说明 | 状态 |
|------|------|------|
| Maven本地仓库 | `D:\maven\.m2` | ✅ 可用 |
| Gitee发布仓库 | 技能发布目标 | ✅ 可用 |

---

## 十、下一步行动

### 立即执行 (本周)

1. **Phase 2: UI配置处理**
   - [ ] 实现UI配置解析器
   - [ ] 实现页面路由注册
   - [ ] 定义页面资源打包规范

### 短期计划 (本月)

1. **Phase 3: 性能优化**
   - [ ] 实现Skill懒加载
   - [ ] 实现元数据缓存

2. **Phase 4: 技能迁移**
   - [ ] 迁移skill-scenes
   - [ ] 处理冲突技能

### 中期计划 (下月)

1. **Phase 5: 测试与验证**
   - [ ] 完成单元测试
   - [ ] 完成集成测试
   - [ ] 完成性能测试

---

## 十一、变更记录

| 日期 | 版本 | 变更内容 | 作者 |
|------|------|----------|------|
| 2026-04-03 | v1.0 | 初始创建，完成Phase 0和Phase 1任务整理 | AI Assistant |
