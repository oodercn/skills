# Skills 分类统计数据 - 前端数据对比

> **数据日期**: 2026-03-18  
> **数据版本**: v2.3.1  
> **数据来源**: skill-index/skills/*.yaml  
> **目标受众**: SE 团队 (前端)

---

## 一、分类统计数据总览

### 1.1 完整分类统计（12个标准分类）

| 排序 | 分类ID | 分类名称 | 英文名称 | Skills数量 | 面向用户 | 图标 |
|:----:|--------|----------|----------|:----------:|:--------:|------|
| 1 | `org` | 组织服务 | Organization | **6** | ❌ | users |
| 2 | `vfs` | 存储服务 | Storage | **6** | ❌ | database |
| 3 | `llm` | LLM服务 | LLM Services | **3** | ✅ | brain |
| 4 | `knowledge` | 知识服务 | Knowledge | **1** | ✅ | book |
| 5 | `biz` | 业务场景 | Business | **5** | ✅ | briefcase |
| 6 | `sys` | 系统管理 | System | **10** | ❌ | settings |
| 7 | `msg` | 消息通讯 | Messaging | **7** | ❌ | message |
| 8 | `ui` | UI生成 | UI Generation | **1** | ❌ | palette |
| 9 | `payment` | 支付服务 | Payment | **3** | ❌ | credit-card |
| 10 | `media` | 媒体发布 | Media Publishing | **5** | ❌ | edit |
| 11 | `util` | 工具服务 | Utility | **8** | ✅ | tool |
| 12 | `nexus-ui` | Nexus界面 | Nexus UI | **8** | ❌ | layout |
| **总计** | - | - | - | **63** | - | - |

### 1.2 用户可见分类（4个）

| 分类ID | 分类名称 | Skills数量 | 占比 |
|--------|----------|:----------:|:----:|
| `llm` | LLM服务 | 3 | 4.8% |
| `knowledge` | 知识服务 | 1 | 1.6% |
| `biz` | 业务场景 | 5 | 7.9% |
| `util` | 工具服务 | 8 | 12.7% |
| **小计** | - | **17** | **27.0%** |

### 1.3 非用户可见分类（8个）

| 分类ID | 分类名称 | Skills数量 | 占比 |
|--------|----------|:----------:|:----:|
| `sys` | 系统管理 | 10 | 15.9% |
| `nexus-ui` | Nexus界面 | 8 | 12.7% |
| `msg` | 消息通讯 | 7 | 11.1% |
| `org` | 组织服务 | 6 | 9.5% |
| `vfs` | 存储服务 | 6 | 9.5% |
| `media` | 媒体发布 | 5 | 7.9% |
| `payment` | 支付服务 | 3 | 4.8% |
| `ui` | UI生成 | 1 | 1.6% |
| **小计** | - | **46** | **73.0%** |

---

## 二、`biz` 分类子分类统计

| 子分类ID | 子分类名称 | Skills数量 | Skills列表 |
|----------|------------|:----------:|------------|
| `hr` | 人力资源 | **1** | skill-recruitment-management |
| `crm` | 客户管理 | **1** | skill-real-estate-form |
| `approval` | 审批流程 | **1** | skill-approval-form |
| `qa` | 质检管理 | **1** | skill-recording-qa |
| `scenario` | 通用业务 | **1** | skill-business |
| `finance` | 财务管理 | **0** | - |
| `project` | 项目协作 | **0** | - |
| `worklog` | 工作日志 | **0** | - |
| **总计** | - | **5** | - |

---

## 三、前端数据结构

### 3.1 API 返回格式

```json
{
  "total": 63,
  "categories": {
    "org": 6,
    "vfs": 6,
    "llm": 3,
    "knowledge": 1,
    "biz": 5,
    "sys": 10,
    "msg": 7,
    "ui": 1,
    "payment": 3,
    "media": 5,
    "util": 8,
    "nexus-ui": 8
  },
  "userFacingCategories": [
    { "id": "llm", "name": "LLM服务", "count": 3 },
    { "id": "knowledge", "name": "知识服务", "count": 1 },
    { "id": "biz", "name": "业务场景", "count": 5 },
    { "id": "util", "name": "工具服务", "count": 8 }
  ],
  "bizSubCategories": [
    { "id": "hr", "name": "人力资源", "count": 1 },
    { "id": "crm", "name": "客户管理", "count": 1 },
    { "id": "approval", "name": "审批流程", "count": 1 },
    { "id": "qa", "name": "质检管理", "count": 1 },
    { "id": "scenario", "name": "通用业务", "count": 1 },
    { "id": "finance", "name": "财务管理", "count": 0 },
    { "id": "project", "name": "项目协作", "count": 0 },
    { "id": "worklog", "name": "工作日志", "count": 0 }
  ]
}
```

### 3.2 前端组件数据

```javascript
// 分类统计卡片数据
const categoryStatsData = [
  { id: 'sys', name: '系统管理', count: 10, userFacing: false, icon: 'Settings' },
  { id: 'nexus-ui', name: 'Nexus界面', count: 8, userFacing: false, icon: 'LayoutDashboard' },
  { id: 'util', name: '工具服务', count: 8, userFacing: true, icon: 'Wrench' },
  { id: 'msg', name: '消息通讯', count: 7, userFacing: false, icon: 'MessageSquare' },
  { id: 'org', name: '组织服务', count: 6, userFacing: false, icon: 'Users' },
  { id: 'vfs', name: '存储服务', count: 6, userFacing: false, icon: 'Database' },
  { id: 'biz', name: '业务场景', count: 5, userFacing: true, icon: 'Briefcase' },
  { id: 'media', name: '媒体发布', count: 5, userFacing: false, icon: 'Edit' },
  { id: 'llm', name: 'LLM服务', count: 3, userFacing: true, icon: 'Brain' },
  { id: 'payment', name: '支付服务', count: 3, userFacing: false, icon: 'CreditCard' },
  { id: 'knowledge', name: '知识服务', count: 1, userFacing: true, icon: 'BookOpen' },
  { id: 'ui', name: 'UI生成', count: 1, userFacing: false, icon: 'Palette' }
];

// 用户可见分类数据
const userFacingData = [
  { id: 'util', name: '工具服务', count: 8, icon: 'Wrench' },
  { id: 'biz', name: '业务场景', count: 5, icon: 'Briefcase' },
  { id: 'llm', name: 'LLM服务', count: 3, icon: 'Brain' },
  { id: 'knowledge', name: '知识服务', count: 1, icon: 'BookOpen' }
];

// biz 子分类数据
const bizSubCategoryData = [
  { id: 'hr', name: '人力资源', count: 1 },
  { id: 'crm', name: '客户管理', count: 1 },
  { id: 'approval', name: '审批流程', count: 1 },
  { id: 'qa', name: '质检管理', count: 1 },
  { id: 'scenario', name: '通用业务', count: 1 },
  { id: 'finance', name: '财务管理', count: 0 },
  { id: 'project', name: '项目协作', count: 0 },
  { id: 'worklog', name: '工作日志', count: 0 }
];
```

---

## 四、数据对比（变更前后）

### 4.1 分类数量对比

| 对比项 | 变更前 | 变更后 | 变化 |
|--------|:------:|:------:|:----:|
| 标准分类数量 | 11 | **12** | +1 |
| 用户可见分类 | 未定义 | **4** | 新增 |
| 子分类定义 | 无 | **8** | 新增 |
| Skills 总数 | 63 | **63** | 不变 |

### 4.2 分类分布对比

| 分类 | 变更前 | 变更后 | 变化 |
|------|:------:|:------:|:----:|
| `biz` | 0 | **5** | +5 (新增) |
| `util` | 13 | **8** | -5 (迁移到 biz) |
| `sys` | 8 | **10** | +2 (合并 infrastructure/scheduler) |
| `business` | 5 | **0** | -5 (已废弃) |
| `infrastructure` | 1 | **0** | -1 (已废弃) |
| `scheduler` | 1 | **0** | -1 (已废弃) |

### 4.3 废弃分类映射

| 废弃分类 | 迁移目标 | 说明 |
|----------|----------|------|
| `business` | `biz` | 业务场景技能 |
| `infrastructure` | `sys` | 基础设施技能 |
| `scheduler` | `sys` | 调度服务技能 |
| `abs` | `knowledge` | 场景技能类型 (废弃) |
| `tbs` | `knowledge` | 场景技能类型 (废弃) |
| `ass` | `knowledge` | 场景技能类型 (废弃) |

---

## 五、前端展示建议

### 5.1 分类卡片排序

**推荐排序方式**：按 Skills 数量降序

```
1. sys (10)      - 系统管理
2. nexus-ui (8)  - Nexus界面
3. util (8)      - 工具服务 ★用户可见
4. msg (7)       - 消息通讯
5. org (6)       - 组织服务
6. vfs (6)       - 存储服务
7. biz (5)       - 业务场景 ★用户可见
8. media (5)     - 媒体发布
9. llm (3)       - LLM服务 ★用户可见
10. payment (3)  - 支付服务
11. knowledge (1) - 知识服务 ★用户可见
12. ui (1)       - UI生成
```

### 5.2 用户可见分类优先展示

```
┌─────────────────────────────────────────────────────────────┐
│  常用分类 (用户可见)                                          │
├─────────────────────────────────────────────────────────────┤
│  🛠️ 工具服务 (8)    💼 业务场景 (5)    🤖 LLM服务 (3)         │
│  📚 知识服务 (1)                                              │
└─────────────────────────────────────────────────────────────┘
```

### 5.3 biz 分类展开展示

```
┌─────────────────────────────────────────────────────────────┐
│  💼 业务场景 (5)                                             │
├─────────────────────────────────────────────────────────────┤
│  👥 人力资源 (1)    👤 客户管理 (1)    ✅ 审批流程 (1)         │
│  🔍 质检管理 (1)    📋 通用业务 (1)                           │
│  ─────────────────────────────────────────────────────────  │
│  💰 财务管理 (0)    📁 项目协作 (0)    📝 工作日志 (0)         │
└─────────────────────────────────────────────────────────────┘
```

---

## 六、验证检查清单

### 6.1 数据一致性检查

| 检查项 | 预期值 | 验证方法 |
|--------|--------|----------|
| Skills 总数 | 63 | `GET /api/skills` 返回数组长度 |
| 分类数量 | 12 | `GET /api/skills/categories/stats` |
| 用户可见分类 | 4 | 检查 `userFacingCategories` 数组长度 |
| biz 子分类 | 8 | `GET /api/skills/categories/biz/subcategories` |
| biz Skills 数量 | 5 | `GET /api/skills?category=biz` |

### 6.2 前端展示检查

| 检查项 | 预期结果 |
|--------|----------|
| 分类统计卡片数量 | 12 个 |
| 用户可见标记 | llm, knowledge, biz, util 有标记 |
| biz 子分类展开 | 点击 biz 后展示 8 个子分类 |
| 废弃分类处理 | business/infrastructure/scheduler 不显示 |

---

**文档维护者**: Skills Team  
**最后更新**: 2026-03-18  
**版本**: v1.0.0
