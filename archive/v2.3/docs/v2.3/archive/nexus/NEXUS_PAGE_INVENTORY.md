# Nexus 页面清单 (v1.0)

> **文档版本**: v1.0  
> **发布日期**: 2026-02-25  
> **状�?*: 正式发布  
> **适用范围**: Skills团队页面转换

---

## 目录

1. [页面统计概览](#一页统计概�?
2. [页面分类清单](#二页面分类清�?
3. [组件映射表](#三组件映射表)
4. [转换优先级](#四转换优先级)
5. [转换计划](#五转换计�?

---

## 一、页面统计概�?
### 1.1 总体统计

| 分类 | 数量 | 占比 | 预计工期 |
|------|------|------|----------|
| **简单页�?* | 40+ | 40% | 2�?|
| **中等页面** | 30+ | 30% | 4�?|
| **复杂页面** | 20+ | 20% | 6�?|
| **极复杂页�?* | 10+ | 10% | 8�?|
| **总计** | **100+** | **100%** | **20�?* |

### 1.2 按模块统�?
| 模块 | 页面�?| 复杂度分�?|
|------|--------|------------|
| 用户管理 | 15 | 简�? 8, 中等: 5, 复杂: 2 |
| 组织架构 | 12 | 简�? 6, 中等: 4, 复杂: 2 |
| 文件管理 | 10 | 简�? 4, 中等: 3, 复杂: 2, 极复�? 1 |
| 系统设置 | 18 | 简�? 12, 中等: 6 |
| 数据分析 | 15 | 中等: 8, 复杂: 5, 极复�? 2 |
| 消息通知 | 8 | 简�? 6, 中等: 2 |
| 工作�?| 12 | 中等: 6, 复杂: 4, 极复�? 2 |
| 其他 | 10 | 简�? 6, 中等: 4 |

---

## 二、页面分类清�?
### 2.1 简单页�?(40�?

**定义**: 表单类、列表类页面，组件少�?0�?
| 序号 | 页面名称 | 页面ID | 主要组件 | 数据接口 | 优先�?|
|------|----------|--------|----------|----------|--------|
| 1 | 用户列表 | user-list | NsTable, NsButton, NsSearch | /api/users | P0 |
| 2 | 用户详情 | user-detail | NsForm, NsInput, NsButton | /api/users/{id} | P0 |
| 3 | 角色列表 | role-list | NsTable, NsButton | /api/roles | P0 |
| 4 | 角色详情 | role-detail | NsForm, NsInput, NsSelect | /api/roles/{id} | P0 |
| 5 | 部门列表 | dept-list | NsTree, NsButton | /api/depts | P0 |
| 6 | 部门详情 | dept-detail | NsForm, NsInput | /api/depts/{id} | P0 |
| 7 | 菜单列表 | menu-list | NsTree, NsButton | /api/menus | P1 |
| 8 | 菜单详情 | menu-detail | NsForm, NsInput, NsSelect | /api/menus/{id} | P1 |
| 9 | 字典列表 | dict-list | NsTable, NsButton | /api/dicts | P1 |
| 10 | 字典详情 | dict-detail | NsForm, NsInput | /api/dicts/{id} | P1 |
| 11 | 参数列表 | param-list | NsTable, NsButton | /api/params | P1 |
| 12 | 参数详情 | param-detail | NsForm, NsInput | /api/params/{id} | P1 |
| 13 | 日志列表 | log-list | NsTable, NsSearch, NsDatePicker | /api/logs | P1 |
| 14 | 通知列表 | notice-list | NsTable, NsButton | /api/notices | P1 |
| 15 | 通知详情 | notice-detail | NsForm, NsInput, NsTextarea | /api/notices/{id} | P1 |
| 16 | 文件列表 | file-list | NsTable, NsButton, NsUpload | /api/files | P1 |
| 17 | 标签列表 | tag-list | NsTable, NsButton | /api/tags | P2 |
| 18 | 分类列表 | category-list | NsTree, NsButton | /api/categories | P2 |
| 19 | 评论列表 | comment-list | NsTable, NsButton | /api/comments | P2 |
| 20 | 消息列表 | msg-list | NsTable, NsButton | /api/messages | P2 |
| 21-40 | (其他简单页�? | ... | ... | ... | P2 |

### 2.2 中等页面 (30�?

**定义**: 包含交互逻辑、状态管理，组件10-20�?
| 序号 | 页面名称 | 页面ID | 主要组件 | 复杂度说�?| 优先�?|
|------|----------|--------|----------|------------|--------|
| 1 | 用户管理 | user-management | NsTable, NsDialog, NsForm, NsTreeSelect | 批量操作、权限分�?| P0 |
| 2 | 角色权限 | role-permission | NsTree, NsTable, NsTransfer | 权限树、角色关�?| P0 |
| 3 | 组织架构 | org-structure | NsTree, NsCard, NsTabs | 拖拽排序、层级展�?| P0 |
| 4 | 文件管理 | file-manager | NsTree, NsTable, NsUpload, NsPreview | 文件夹操作、预�?| P0 |
| 5 | 流程设计 | workflow-design | NsFlowDesign, NsPanel, NsToolbar | 可视化流程设�?| P1 |
| 6 | 报表配置 | report-config | NsForm, NsChart, NsTable | 动态报表配�?| P1 |
| 7 | 数据导入 | data-import | NsUpload, NsTable, NsProgress | 批量导入、校�?| P1 |
| 8 | 数据导出 | data-export | NsForm, NsTable, NsProgress | 条件筛选、导�?| P1 |
| 9 | 定时任务 | scheduled-task | NsTable, NsCron, NsLog | Cron表达式、执行日�?| P1 |
| 10 | 系统监控 | system-monitor | NsChart, NsCard, NsTable | 实时监控图表 | P1 |
| 11-30 | (其他中等页面) | ... | ... | ... | P2 |

### 2.3 复杂页面 (20�?

**定义**: 包含复杂交互、多模块联动，组�?0+�?
| 序号 | 页面名称 | 页面ID | 主要组件 | 复杂度说�?| 优先�?|
|------|----------|--------|----------|------------|--------|
| 1 | 工作流引�?| workflow-engine | NsFlowDesign, NsFormDesign, NsCodeEditor | 流程设计+表单设计+代码编辑 | P0 |
| 2 | BI分析平台 | bi-platform | NsChart, NsTable, NsFilter, NsDashboard | 多维度分析、拖拽配�?| P1 |
| 3 | 大屏展示 | big-screen | NsChart, NsMap, NsCard, NsAnimation | 数据可视化、实时更�?| P1 |
| 4 | 代码生成�?| code-generator | NsForm, NsTable, NsCodeEditor, NsPreview | 元数据管理、代码生�?| P1 |
| 5 | 在线文档 | online-doc | NsEditor, NsTree, NsComment, NsVersion | 协同编辑、版本管�?| P2 |
| 6-20 | (其他复杂页面) | ... | ... | ... | P2 |

### 2.4 极复杂页�?(10�?

**定义**: 核心业务系统、高度定制化，需要特殊处�?
| 序号 | 页面名称 | 页面ID | 复杂度说�?| 处理策略 |
|------|----------|--------|------------|----------|
| 1 | ERP核心系统 | erp-core | 财务、供应链、生产多模块集成 | 拆分为多个Skill |
| 2 | CRM客户系统 | crm-system | 销售、营销、服务全流程 | 保留iframe包装 |
| 3 | 项目管理平台 | project-mgmt | 甘特图、资源管理、成本控�?| 逐步迁移 |
| 4 | 数据中台 | data-platform | 数据集成、治理、服�?| 保留核心功能 |
| 5 | AI训练平台 | ai-training | 模型训练、评估、部�?| 新开发Skill |
| 6-10 | (其他极复杂页�? | ... | ... | ... |

---

## 三、组件映射表

### 3.1 基础组件映射

| Nexus组件 | Ooder-A2A组件 | 复杂�?| 状�?|
|-----------|---------------|--------|------|
| NsButton | ooder-a2a-button | �?| �?已定�?|
| NsInput | ooder-a2a-input | �?| �?已定�?|
| NsSelect | ooder-a2a-select | �?| �?已定�?|
| NsTable | ooder-a2a-table | �?| �?已定�?|
| NsForm | ooder-a2a-form | �?| �?已定�?|
| NsDialog | ooder-a2a-dialog | �?| �?已定�?|
| NsCard | ooder-a2a-card | �?| �?已定�?|
| NsIcon | ooder-a2a-icon | �?| �?已定�?|
| NsTree | ooder-a2a-tree | �?| 📝 待实�?|
| NsTabs | ooder-a2a-tabs | �?| 📝 待实�?|
| NsDatePicker | ooder-a2a-date-picker | �?| 📝 待实�?|
| NsUpload | ooder-a2a-upload | �?| 📝 待实�?|
| NsSearch | ooder-a2a-search | �?| 📝 待实�?|
| NsPagination | ooder-a2a-pagination | �?| 📝 待实�?|
| NsTextarea | ooder-a2a-textarea | �?| 📝 待实�?|
| NsCheckbox | ooder-a2a-checkbox | �?| 📝 待实�?|
| NsRadio | ooder-a2a-radio | �?| 📝 待实�?|
| NsSwitch | ooder-a2a-switch | �?| 📝 待实�?|
| NsSlider | ooder-a2a-slider | �?| 📝 待实�?|
| NsProgress | ooder-a2a-progress | �?| 📝 待实�?|

### 3.2 复合组件映射

| Nexus组件 | Ooder-A2A组件 | 复杂�?| 状�?|
|-----------|---------------|--------|------|
| NsTreeSelect | ooder-a2a-tree-select | �?| 📝 待实�?|
| NsTransfer | ooder-a2a-transfer | �?| 📝 待实�?|
| NsCascader | ooder-a2a-cascader | �?| 📝 待实�?|
| NsTimePicker | ooder-a2a-time-picker | �?| 📝 待实�?|
| NsColorPicker | ooder-a2a-color-picker | �?| 📝 待实�?|
| NsRichEditor | ooder-a2a-rich-editor | �?| 📝 待实�?|
| NsCodeEditor | ooder-a2a-code-editor | �?| 📝 待实�?|
| NsMarkdown | ooder-a2a-markdown | �?| 📝 待实�?|
| NsChart | ooder-a2a-chart | �?| 📝 待实�?|
| NsMap | ooder-a2a-map | �?| 📝 待实�?|
| NsCalendar | ooder-a2a-calendar | �?| 📝 待实�?|
| NsTimeline | ooder-a2a-timeline | �?| 📝 待实�?|
| NsSteps | ooder-a2a-steps | �?| 📝 待实�?|
| NsCollapse | ooder-a2a-collapse | �?| 📝 待实�?|
| NsDrawer | ooder-a2a-drawer | �?| 📝 待实�?|
| NsPopover | ooder-a2a-popover | �?| 📝 待实�?|
| NsTooltip | ooder-a2a-tooltip | �?| 📝 待实�?|
| NsBadge | ooder-a2a-badge | �?| 📝 待实�?|
| NsTag | ooder-a2a-tag | �?| 📝 待实�?|
| NsAvatar | ooder-a2a-avatar | �?| 📝 待实�?|

### 3.3 业务组件映射

| Nexus组件 | Ooder-A2A组件 | 复杂�?| 状�?|
|-----------|---------------|--------|------|
| NsFlowDesign | ooder-a2a-flow-design | 极高 | 📝 待实�?|
| NsFormDesign | ooder-a2a-form-design | 极高 | 📝 待实�?|
| NsDashboard | ooder-a2a-dashboard | �?| 📝 待实�?|
| NsReport | ooder-a2a-report | �?| 📝 待实�?|
| NsPreview | ooder-a2a-preview | �?| 📝 待实�?|
| NsCron | ooder-a2a-cron | �?| 📝 待实�?|
| NsLog | ooder-a2a-log | �?| 📝 待实�?|
| NsFilter | ooder-a2a-filter | �?| 📝 待实�?|
| NsToolbar | ooder-a2a-toolbar | �?| 📝 待实�?|
| NsPanel | ooder-a2a-panel | �?| 📝 待实�?|

---

## 四、转换优先级

### 4.1 P0 优先�?(立即转换)

**标准**: 核心功能、高频使用、阻塞其他页�?
| 页面ID | 页面名称 | 原因 |
|--------|----------|------|
| user-list | 用户列表 | 核心功能，高频使�?|
| user-detail | 用户详情 | 用户管理配套 |
| role-list | 角色列表 | 权限管理核心 |
| role-detail | 角色详情 | 权限管理配套 |
| dept-list | 部门列表 | 组织架构基础 |
| dept-detail | 部门详情 | 组织架构配套 |
| user-management | 用户管理 | 复杂页面代表 |
| role-permission | 角色权限 | 权限分配核心 |
| org-structure | 组织架构 | 树形组件代表 |
| file-manager | 文件管理 | 文件操作代表 |

### 4.2 P1 优先�?(优先转换)

**标准**: 重要功能、中等复杂度

| 页面ID | 页面名称 | 原因 |
|--------|----------|------|
| menu-list | 菜单列表 | 系统配置重要 |
| dict-list | 字典列表 | 数据字典基础 |
| param-list | 参数列表 | 系统参数配置 |
| log-list | 日志列表 | 系统监控重要 |
| notice-list | 通知列表 | 消息通知基础 |
| workflow-design | 流程设计 | 工作流核�?|
| report-config | 报表配置 | 数据分析基础 |
| data-import | 数据导入 | 数据管理重要 |
| data-export | 数据导出 | 数据管理重要 |
| scheduled-task | 定时任务 | 系统任务调度 |
| system-monitor | 系统监控 | 运维监控基础 |

### 4.3 P2 优先�?(后续转换)

**标准**: 一般功能、低复杂度或复杂度过�?
- 其他简单页�?- 其他中等页面
- 复杂页面 (除P0�?
- 极复杂页�?(需特殊处理)

---

## 五、转换计�?
### 5.1 Week 2 (首批4个简单页�?

| 页面 | 组件需�?| 预计工期 | 负责�?|
|------|----------|----------|--------|
| user-list | ooder-a2a-table, ooder-a2a-button, ooder-a2a-search | 2�?| Skills团队 |
| user-detail | ooder-a2a-form, ooder-a2a-input, ooder-a2a-button | 2�?| Skills团队 |
| role-list | ooder-a2a-table, ooder-a2a-button | 2�?| Skills团队 |
| role-detail | ooder-a2a-form, ooder-a2a-input, ooder-a2a-select | 2�?| Skills团队 |

**Week 2交付�?*:
- [ ] 4个简单页面转换完�?- [ ] skill.yaml配置文件
- [ ] 转换工具链初�?
### 5.2 Week 3-4 (6个中等页�?

| 页面 | 组件需�?| 预计工期 | 负责�?|
|------|----------|----------|--------|
| user-management | table, dialog, form, tree-select | 4�?| Skills团队 |
| role-permission | tree, table, transfer | 4�?| Skills团队 |
| org-structure | tree, card, tabs | 4�?| Skills团队 |
| file-manager | tree, table, upload, preview | 4�?| Skills团队 |
| workflow-design | flow-design, panel, toolbar | 4�?| Skills团队 |
| report-config | form, chart, table | 4�?| Skills团队 |

### 5.3 Week 5-8 (复杂页面)

| 页面 | 策略 | 预计工期 | 负责�?|
|------|------|----------|--------|
| workflow-engine | 拆分多个Skill | 10�?| Skills团队 |
| bi-platform | 保留核心功能 | 8�?| Skills团队 |
| big-screen | 新开发Skill | 8�?| Skills团队 |
| code-generator | 逐步迁移 | 6�?| Skills团队 |
| online-doc | 保留iframe | 4�?| Skills团队 |

### 5.4 Week 9+ (极复杂页�?

| 页面 | 策略 | 预计工期 | 负责�?|
|------|------|----------|--------|
| erp-core | 拆分为多个Skill | 20�?| Skills团队 |
| crm-system | 保留iframe包装 | 5�?| Skills团队 |
| project-mgmt | 逐步迁移 | 15�?| Skills团队 |
| data-platform | 保留核心功能 | 10�?| Skills团队 |
| ai-training | 新开发Skill | 15�?| Skills团队 |

---

## 附录

### A. 页面ID命名规范

```
格式: {module}-{page}-{action}

示例:
- user-list          (用户列表)
- user-detail        (用户详情)
- user-management    (用户管理)
- role-permission    (角色权限)
- org-structure      (组织架构)
- file-manager       (文件管理)
- workflow-design    (流程设计)
- bi-platform        (BI平台)
```

### B. 组件优先�?
**P0 (首批实现)**:
- ooder-a2a-button
- ooder-a2a-input
- ooder-a2a-select
- ooder-a2a-table
- ooder-a2a-form
- ooder-a2a-dialog

**P1 (第二批实�?**:
- ooder-a2a-tree
- ooder-a2a-tabs
- ooder-a2a-date-picker
- ooder-a2a-upload
- ooder-a2a-search
- ooder-a2a-pagination

**P2 (后续实现)**:
- 其他基础组件
- 复合组件
- 业务组件

### C. 相关文档

- [OODER-A2A-SPECIFICATION-v1.0.md](OODER-A2A-SPECIFICATION-v1.0.md) - A2A规范
- [OODER_2.3_SPECIFICATION.md](OODER_2.3_SPECIFICATION.md) - 2.3完整规范
- [TEAM_TASKS_ALLOCATION.md](TEAM_TASKS_ALLOCATION.md) - 任务分配

---

**文档结束**

**生效日期**: 2026-02-25  
**下次更新**: Week 2结束 (根据首批转换情况调整)
