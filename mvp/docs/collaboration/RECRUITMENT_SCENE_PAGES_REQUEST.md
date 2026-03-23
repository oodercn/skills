# 协作需求：招聘管理场景页面完善

## 一、需求背景

**项目**：MVP (mvp-core)  
**版本**：2.3.1  
**发起方**：MVP 团队  
**接收方**：Skills 团队  
**日期**：2026-03-22  
**状态**：🔄 待处理

---

## 二、问题描述

### 2.1 问题现象

用户在场景组详情页激活招聘管理场景后，菜单可以正常添加，但点击菜单链接后返回 404 错误。

**问题 URL 示例**：
- `http://localhost:8084/console/pages/recruitment/dashboard.html?sceneGroupId=sg-1774182070728`
- `http://localhost:8084/console/pages/recruitment/statistics.html?sceneGroupId=sg-1774182070728`

### 2.2 根本原因

`pages` 目录下缺少 `recruitment` 子目录，场景模板 `recruitment-scene.yaml` 中定义的菜单 URL 对应的页面文件不存在。

**场景模板定义**（`src/main/resources/templates/recruitment-scene.yaml`）：

```yaml
menus:
  MANAGER:
    - id: recruitment-overview
      name: 招聘概览
      url: /console/pages/recruitment/dashboard.html
    - id: recruitment-statistics
      name: 数据统计
      url: /console/pages/recruitment/statistics.html
    - id: recruitment-candidates
      name: 候选人管理
      url: /console/pages/recruitment/candidates.html
      children:
        - id: candidate-list
          name: 候选人列表
          url: /console/pages/recruitment/candidate-list.html
        - id: candidate-pool
          name: 人才库
          url: /console/pages/recruitment/candidate-pool.html
    - id: recruitment-approval
      name: 审批中心
      url: /console/pages/recruitment/approval.html
    - id: recruitment-settings
      name: 招聘设置
      url: /console/pages/recruitment/settings.html
```

---

## 三、已完成工作

### 3.1 菜单层级问题修复

MVP 团队已修复 `MenuRoleConfigService.java` 中菜单层级丢失的问题：

- 修复 `convertFromMenuItem` 方法，正确处理 `children` 字段
- 修复 `convertToMenuItem` 方法，正确处理 `children` 字段

### 3.2 基础页面框架

MVP 团队已创建基础页面框架文件（占位页面）：

| 文件 | 路径 | 状态 |
|------|------|------|
| dashboard.html | `/console/pages/recruitment/dashboard.html` | ⚠️ 框架代码 |
| statistics.html | `/console/pages/recruitment/statistics.html` | ⚠️ 框架代码 |
| candidates.html | `/console/pages/recruitment/candidates.html` | ⚠️ 框架代码 |
| approval.html | `/console/pages/recruitment/approval.html` | ⚠️ 框架代码 |
| settings.html | `/console/pages/recruitment/settings.html` | ⚠️ 框架代码 |
| candidate-list.html | `/console/pages/recruitment/candidate-list.html` | ⚠️ 框架代码 |
| candidate-pool.html | `/console/pages/recruitment/candidate-pool.html` | ⚠️ 框架代码 |

---

## 四、协作需求

### 4.1 需要 Skills 团队完成的工作

请 Skills 团队完善招聘管理场景的前端页面，实现以下功能模块：

#### 4.1.1 招聘概览

**功能需求**：
- 招聘职位统计卡片（总数、进行中、已关闭）
- 候选人统计卡片（总数、待筛选、面试中、已录用）
- 今日面试安排列表
- 最近候选人动态时间线
- 招聘漏斗可视化图表

**数据接口**：
```
GET /api/v1/recruitment/{sceneGroupId}/overview
GET /api/v1/recruitment/{sceneGroupId}/statistics
GET /api/v1/recruitment/{sceneGroupId}/interviews/today
```

#### 4.1.2 数据统计

**功能需求**：
- 招聘渠道效果分析图表
- 职位招聘周期统计
- 面试通过率趋势图
- Offer 接受率统计
- 数据导出功能

**数据接口**：
```
GET /api/v1/recruitment/{sceneGroupId}/analytics/channels
GET /api/v1/recruitment/{sceneGroupId}/analytics/cycle
GET /api/v1/recruitment/{sceneGroupId}/analytics/pass-rate
```

#### 4.1.3 候选人管理

**功能需求**：
- 候选人列表（支持搜索、筛选、排序）
- 候选人详情卡片
- 候选人状态流转（待筛选 → 面试中 → Offer → 录用/拒绝）
- 简历上传和解析
- 候选人标签管理

**数据接口**：
```
GET /api/v1/recruitment/{sceneGroupId}/candidates
POST /api/v1/recruitment/{sceneGroupId}/candidates
GET /api/v1/recruitment/{sceneGroupId}/candidates/{candidateId}
PUT /api/v1/recruitment/{sceneGroupId}/candidates/{candidateId}/status
```

#### 4.1.4 候选人列表

**功能需求**：
- 候选人表格视图
- 批量操作（批量筛选、批量发送面试邀请）
- 候选人快速筛选面板（按职位、状态、来源）

#### 4.1.5 人才库

**功能需求**：
- 人才库列表（历史候选人、主动投递）
- 人才标签和分类
- 人才搜索（按技能、经验、学历）
- 人才推荐（根据职位匹配）

**数据接口**：
```
GET /api/v1/recruitment/{sceneGroupId}/talent-pool
POST /api/v1/recruitment/{sceneGroupId}/talent-pool
```

#### 4.1.6 审批中心

**功能需求**：
- 待审批列表（Offer 审批、费用审批）
- 审批详情页
- 审批操作（通过、拒绝、转交）
- 审批历史记录

**数据接口**：
```
GET /api/v1/recruitment/{sceneGroupId}/approvals/pending
POST /api/v1/recruitment/{sceneGroupId}/approvals/{approvalId}/approve
POST /api/v1/recruitment/{sceneGroupId}/approvals/{approvalId}/reject
```

#### 4.1.7 招聘设置

**功能需求**：
- 招聘流程配置
- 面试官管理
- 邮件/通知模板配置
- 招聘来源配置
- 自定义字段配置

**数据接口**：
```
GET /api/v1/recruitment/{sceneGroupId}/settings
PUT /api/v1/recruitment/{sceneGroupId}/settings
```

---

## 五、技术规范

### 5.1 前端技术栈

- HTML5 + CSS3
- 原生 JavaScript (ES6+)
- CSS 框架：Nexus CSS（项目内部框架）
- 图标库：Remix Icon
- 图表库：待定（建议 Chart.js 或 ECharts）

### 5.2 页面模板规范

每个页面应包含：

1. **统一的侧边栏导航**（使用 `#nav-menu` 容器）
2. **统一的页面头部**（用户菜单、主题切换）
3. **响应式布局**
4. **加载状态和错误处理**
5. **与场景组关联**（通过 URL 参数 `sceneGroupId`）

### 5.3 代码规范

- 使用项目已有的 CSS 类（`nx-*` 前缀）
- 遵循现有页面的代码风格
- 添加必要的错误处理和用户提示

---

## 六、参考资源

### 6.1 现有页面参考

- [scene-group-detail.html](../src/main/resources/static/console/pages/scene-group-detail.html) - 场景组详情页
- [my-capabilities.html](../src/main/resources/static/console/pages/my-capabilities.html) - 我的能力页
- [my-todos.html](../src/main/resources/static/console/pages/my-todos.html) - 我的待办页

### 6.2 场景模板

- [recruitment-scene.yaml](../src/main/resources/templates/recruitment-scene.yaml) - 招聘管理场景模板定义

### 6.3 CSS 框架

- [nexus.css](../src/main/resources/static/console/css/nexus.css) - 核心 CSS 框架
- [nx-page.css](../src/main/resources/static/console/css/nx-page.css) - 页面布局 CSS

---

## 七、时间计划

| 阶段 | 内容 | 预计完成时间 | 负责方 |
|------|------|-------------|--------|
| 阶段一 | 基础页面框架（已完成） | 2026-03-22 | MVP 团队 |
| 阶段二 | 核心功能页面（dashboard、candidates） | 待定 | Skills 团队 |
| 阶段三 | 辅助功能页面（statistics、approval、settings） | 待定 | Skills 团队 |
| 阶段四 | 子菜单页面（candidate-list、candidate-pool） | 待定 | Skills 团队 |
| 阶段五 | 联调测试 | 待定 | 双方 |

---

## 八、验收标准

### 8.1 功能验收

- [ ] 所有页面可正常访问，无 404 错误
- [ ] 页面数据可正常加载和显示
- [ ] 核心功能可正常操作（候选人管理、审批等）
- [ ] 页面与场景组正确关联

### 8.2 UI/UX 验收

- [ ] 页面风格与现有控制台一致
- [ ] 响应式布局正常
- [ ] 加载状态和错误提示友好

### 8.3 代码验收

- [ ] 代码符合项目规范
- [ ] 无 console 错误
- [ ] 无明显的性能问题

---

## 九、联系方式

**MVP 团队负责人**：[待填写]  
**Skills 团队负责人**：[待填写]  
**协作状态**：🔄 待 Skills 团队确认

---

## 十、附录

### 10.1 相关问题修复记录

1. **菜单层级丢失问题**
   - 文件：`MenuRoleConfigService.java`
   - 修复：`convertFromMenuItem` 和 `convertToMenuItem` 方法添加 children 处理
   - 状态：✅ 已修复

2. **页面文件缺失问题**
   - 文件：`/console/pages/recruitment/*.html`
   - 修复：创建基础页面框架
   - 状态：⚠️ 框架已创建，功能待完善

### 10.2 测试场景组 ID

- `sg-1774170915779`
- `sg-1774162074425`
- `sg-1774182070728`
