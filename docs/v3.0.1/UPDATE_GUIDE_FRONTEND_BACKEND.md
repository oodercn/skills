# Skills 分类规范更新 - 前后端更新指导说明书

> **版本**: v1.0.0  
> **发布日期**: 2026-03-18  
> **适用范围**: MVP 团队 (后端) / SE 团队 (前端)  
> **优先级**: P0 - 立即执行

---

## 一、更新概述

### 1.1 变更摘要

| 变更项 | 变更前 | 变更后 |
|--------|--------|--------|
| **标准分类数量** | 11 个 | **12 个** |
| **新增分类** | - | `biz` (业务场景) |
| **子分类定义** | 未定义 | 定义 8 个标准子分类 |
| **用户可见分类** | 未区分 | 4 个用户可见分类 |
| **废弃分类** | business, infrastructure, scheduler | 已迁移到 biz/sys |

### 1.2 影响范围

| 系统 | 影响模块 | 影响程度 |
|------|----------|:--------:|
| **MVP 后端** | SkillIndexLoader, CapabilityController | 🔴 高 |
| **SE 前端** | my-capabilities.html, 分类展示组件 | 🔴 高 |
| **Skills 库** | skill-index/*.yaml | ✅ 已完成 |

---

## 二、后端更新指南 (MVP 团队)

### 2.1 SkillIndexLoader.java 更新

#### 2.1.1 新增分类读取字段

```java
// 在 loadSkillsFromDirectory() 方法中，读取以下字段：

// 1. capabilityCategory (主要分类)
String capabilityCategory = (String) spec.get("capabilityCategory");
entry.setCapabilityCategory(capabilityCategory);

// 2. subCategory (子分类)
String subCategory = (String) spec.get("subCategory");
entry.setSubCategory(subCategory);

// 3. sceneType (场景技能类型，仅场景技能)
String sceneType = (String) spec.get("sceneType");
entry.setSceneType(sceneType);
```

#### 2.1.2 废弃字段处理

```java
// 以下字段已废弃，不再读取：
// - metadata.category
// - spec.category
// - spec.classification.category

// 如果存在，记录警告日志
if (spec.containsKey("category") && !"scene-skill".equals(spec.get("type"))) {
    log.warn("[DEPRECATED] spec.category is deprecated, use capabilityCategory instead for skill: {}", skillId);
}
```

#### 2.1.3 分类统计方法更新

```java
/**
 * 按分类统计技能数量
 */
public Map<String, Long> getSkillCountByCategory() {
    return skills.stream()
        .collect(Collectors.groupingBy(
            skill -> (String) skill.getOrDefault("capabilityCategory", "unknown"),
            Collectors.counting()
        ));
}

/**
 * 获取用户可见分类
 */
public List<Map<String, Object>> getUserFacingCategories() {
    Set<String> userFacingCategories = Set.of("llm", "knowledge", "biz", "util");
    
    return categories.stream()
        .filter(cat -> userFacingCategories.contains(cat.get("id")))
        .collect(Collectors.toList());
}

/**
 * 获取分类下的子分类
 */
public List<Map<String, Object>> getSubCategories(String categoryId) {
    return categories.stream()
        .filter(cat -> categoryId.equals(cat.get("id")))
        .map(cat -> (List<Map<String, Object>>) cat.getOrDefault("subCategories", Collections.emptyList()))
        .findFirst()
        .orElse(Collections.emptyList());
}
```

### 2.2 API 接口更新

#### 2.2.1 新增接口

```java
/**
 * GET /api/skills/categories/stats
 * 获取分类统计信息
 */
@GetMapping("/categories/stats")
public Map<String, Object> getCategoryStats() {
    Map<String, Object> result = new HashMap<>();
    result.put("total", skillIndexLoader.getSkills().size());
    result.put("categories", skillIndexLoader.getSkillCountByCategory());
    result.put("userFacingCategories", skillIndexLoader.getUserFacingCategories());
    return result;
}

/**
 * GET /api/skills/categories/{categoryId}/subcategories
 * 获取分类的子分类列表
 */
@GetMapping("/categories/{categoryId}/subcategories")
public List<Map<String, Object>> getSubCategories(@PathVariable String categoryId) {
    return skillIndexLoader.getSubCategories(categoryId);
}

/**
 * GET /api/skills?category=biz&subCategory=hr
 * 按分类/子分类筛选技能
 */
@GetMapping("/skills")
public List<Map<String, Object>> getSkills(
    @RequestParam(required = false) String category,
    @RequestParam(required = false) String subCategory,
    @RequestParam(required = false) Boolean userFacing
) {
    return skillIndexLoader.getSkills().stream()
        .filter(skill -> category == null || category.equals(skill.get("capabilityCategory")))
        .filter(skill -> subCategory == null || subCategory.equals(skill.get("subCategory")))
        .collect(Collectors.toList());
}
```

### 2.3 数据模型更新

#### 2.3.1 SkillIndexEntry 新增字段

```java
public class SkillIndexEntry {
    // 已有字段
    private String skillId;
    private String name;
    private String version;
    
    // 新增字段
    private String capabilityCategory;  // 分类 (必填)
    private String subCategory;          // 子分类 (可选)
    private String sceneType;            // 场景类型 (仅场景技能)
    
    // 废弃字段 (保留兼容，但不再使用)
    @Deprecated
    private String category;
}
```

### 2.4 CODE_MAPPING 更新

```java
// CapabilityCategory.java
public class CapabilityCategory {
    
    // 标准分类代码映射
    public static final Map<String, String> CODE_MAPPING = Map.ofEntries(
        // 标准分类 (12个)
        Map.entry("org", "组织服务"),
        Map.entry("vfs", "存储服务"),
        Map.entry("llm", "LLM服务"),
        Map.entry("knowledge", "知识服务"),
        Map.entry("biz", "业务场景"),
        Map.entry("sys", "系统管理"),
        Map.entry("msg", "消息通讯"),
        Map.entry("ui", "UI生成"),
        Map.entry("payment", "支付服务"),
        Map.entry("media", "媒体发布"),
        Map.entry("util", "工具服务"),
        Map.entry("nexus-ui", "Nexus界面"),
        
        // 废弃分类映射 (兼容处理)
        Map.entry("business", "业务场景"),      // → biz
        Map.entry("infrastructure", "系统管理"), // → sys
        Map.entry("scheduler", "系统管理"),      // → sys
        Map.entry("abs", "知识服务"),            // → knowledge (废弃)
        Map.entry("tbs", "知识服务"),            // → knowledge (废弃)
        Map.entry("ass", "知识服务"),            // → knowledge (废弃)
        
        // 大写兼容
        Map.entry("MSG", "消息通讯"),
        Map.entry("SYS", "系统管理"),
        Map.entry("VFS", "存储服务"),
        Map.entry("LLM", "LLM服务"),
        Map.entry("ORG", "组织服务"),
        Map.entry("PAYMENT", "支付服务"),
        Map.entry("MEDIA", "媒体发布")
    );
    
    // 用户可见分类
    public static final Set<String> USER_FACING_CATEGORIES = Set.of(
        "llm", "knowledge", "biz", "util"
    );
    
    // 子分类映射 (biz 分类)
    public static final Map<String, String> BIZ_SUBCATEGORY_MAPPING = Map.of(
        "hr", "人力资源",
        "crm", "客户管理",
        "finance", "财务管理",
        "approval", "审批流程",
        "project", "项目协作",
        "worklog", "工作日志",
        "qa", "质检管理",
        "scenario", "通用业务"
    );
}
```

---

## 三、前端更新指南 (SE 团队)

### 3.1 分类展示组件更新

#### 3.1.1 分类配置

```javascript
// config/categories.js

// 标准分类配置 (12个)
export const CATEGORIES = [
  { id: 'org', name: '组织服务', icon: 'users', userFacing: false, order: 1 },
  { id: 'vfs', name: '存储服务', icon: 'database', userFacing: false, order: 2 },
  { id: 'llm', name: 'LLM服务', icon: 'brain', userFacing: true, order: 3 },
  { id: 'knowledge', name: '知识服务', icon: 'book', userFacing: true, order: 4 },
  { id: 'biz', name: '业务场景', icon: 'briefcase', userFacing: true, order: 5 },
  { id: 'sys', name: '系统管理', icon: 'settings', userFacing: false, order: 6 },
  { id: 'msg', name: '消息通讯', icon: 'message', userFacing: false, order: 7 },
  { id: 'ui', name: 'UI生成', icon: 'palette', userFacing: false, order: 8 },
  { id: 'payment', name: '支付服务', icon: 'credit-card', userFacing: false, order: 9 },
  { id: 'media', name: '媒体发布', icon: 'edit', userFacing: false, order: 10 },
  { id: 'util', name: '工具服务', icon: 'tool', userFacing: true, order: 11 },
  { id: 'nexus-ui', name: 'Nexus界面', icon: 'layout', userFacing: false, order: 12 }
];

// 用户可见分类 (4个)
export const USER_FACING_CATEGORIES = CATEGORIES.filter(c => c.userFacing);

// biz 子分类配置
export const BIZ_SUBCATEGORIES = [
  { id: 'hr', name: '人力资源', icon: 'user-plus' },
  { id: 'crm', name: '客户管理', icon: 'users' },
  { id: 'finance', name: '财务管理', icon: 'dollar-sign' },
  { id: 'approval', name: '审批流程', icon: 'check-square' },
  { id: 'project', name: '项目协作', icon: 'folder' },
  { id: 'worklog', name: '工作日志', icon: 'file-text' },
  { id: 'qa', name: '质检管理', icon: 'clipboard-check' },
  { id: 'scenario', name: '通用业务', icon: 'briefcase' }
];

// 图标映射 (Lucide Icons)
export const CATEGORY_ICONS = {
  'org': 'Users',
  'vfs': 'Database',
  'llm': 'Brain',
  'knowledge': 'BookOpen',
  'biz': 'Briefcase',
  'sys': 'Settings',
  'msg': 'MessageSquare',
  'ui': 'Palette',
  'payment': 'CreditCard',
  'media': 'Edit',
  'util': 'Wrench',
  'nexus-ui': 'LayoutDashboard'
};
```

#### 3.1.2 分类卡片组件

```jsx
// components/CategoryCard.jsx

import React from 'react';
import { CATEGORIES, BIZ_SUBCATEGORIES, CATEGORY_ICONS } from '../config/categories';
import * as Icons from 'lucide-react';

const CategoryCard = ({ category, count, onSelect }) => {
  const config = CATEGORIES.find(c => c.id === category);
  const IconComponent = Icons[CATEGORY_ICONS[category]] || Icons.Folder;
  
  return (
    <div 
      className={`category-card ${config?.userFacing ? 'user-facing' : ''}`}
      onClick={() => onSelect(category)}
    >
      <div className="category-icon">
        <IconComponent size={24} />
      </div>
      <div className="category-info">
        <h3>{config?.name || category}</h3>
        <span className="count">{count} 个技能</span>
      </div>
      {config?.userFacing && (
        <span className="badge">用户可见</span>
      )}
    </div>
  );
};

// biz 分类子分类展示
const BizSubCategoryList = ({ subCategoryStats, onSelect }) => {
  return (
    <div className="subcategory-list">
      {BIZ_SUBCATEGORIES.map(sub => (
        <div 
          key={sub.id}
          className="subcategory-item"
          onClick={() => onSelect('biz', sub.id)}
        >
          <span className="name">{sub.name}</span>
          <span className="count">{subCategoryStats[sub.id] || 0}</span>
        </div>
      ))}
    </div>
  );
};
```

### 3.2 my-capabilities.html 更新

#### 3.2.1 分类统计展示

```html
<!-- my-capabilities.html -->

<div class="capabilities-page">
  <!-- 分类统计卡片 -->
  <div class="category-stats">
    <div class="stats-header">
      <h2>能力分类统计</h2>
      <span class="total-count">共 <strong id="totalCount">0</strong> 个能力</span>
    </div>
    
    <!-- 用户可见分类 (优先展示) -->
    <div class="user-facing-section">
      <h3>常用分类</h3>
      <div id="userFacingCategories" class="category-grid"></div>
    </div>
    
    <!-- 全部分类 -->
    <div class="all-categories-section">
      <h3>全部分类</h3>
      <div id="allCategories" class="category-grid"></div>
    </div>
  </div>
  
  <!-- biz 分类子分类展示 -->
  <div id="bizSubCategories" class="subcategory-panel" style="display: none;">
    <h3>业务场景分类</h3>
    <div class="subcategory-grid"></div>
  </div>
</div>
```

#### 3.2.2 JavaScript 更新

```javascript
// js/pages/my-capabilities.js

// 加载分类统计
async function loadCategoryStats() {
  try {
    const response = await fetch('/api/skills/categories/stats');
    const data = await response.json();
    
    // 更新总数
    document.getElementById('totalCount').textContent = data.total;
    
    // 渲染用户可见分类
    renderUserFacingCategories(data.categories, data.userFacingCategories);
    
    // 渲染全部分类
    renderAllCategories(data.categories);
    
  } catch (error) {
    console.error('加载分类统计失败:', error);
  }
}

// 渲染用户可见分类
function renderUserFacingCategories(categoryStats, userFacingList) {
  const container = document.getElementById('userFacingCategories');
  
  const html = userFacingList.map(cat => {
    const count = categoryStats[cat.id] || 0;
    return `
      <div class="category-card user-facing" data-category="${cat.id}">
        <div class="icon">${getCategoryIcon(cat.id)}</div>
        <div class="info">
          <span class="name">${cat.name}</span>
          <span class="count">${count} 个技能</span>
        </div>
      </div>
    `;
  }).join('');
  
  container.innerHTML = html;
}

// 渲染全部分类
function renderAllCategories(categoryStats) {
  const container = document.getElementById('allCategories');
  
  // 按分类顺序排序
  const sortedCategories = CATEGORIES.sort((a, b) => a.order - b.order);
  
  const html = sortedCategories.map(cat => {
    const count = categoryStats[cat.id] || 0;
    return `
      <div class="category-card ${cat.userFacing ? 'user-facing' : ''}" 
           data-category="${cat.id}">
        <div class="icon">${getCategoryIcon(cat.id)}</div>
        <div class="info">
          <span class="name">${cat.name}</span>
          <span class="count">${count} 个技能</span>
        </div>
      </div>
    `;
  }).join('');
  
  container.innerHTML = html;
}

// 点击 biz 分类时展示子分类
document.addEventListener('click', async (e) => {
  const card = e.target.closest('.category-card');
  if (card && card.dataset.category === 'biz') {
    await showBizSubCategories();
  }
});

async function showBizSubCategories() {
  const panel = document.getElementById('bizSubCategories');
  panel.style.display = 'block';
  
  // 加载子分类统计
  const response = await fetch('/api/skills/categories/biz/subcategories');
  const subCategories = await response.json();
  
  // 渲染子分类
  const html = BIZ_SUBCATEGORIES.map(sub => {
    const count = subCategories.find(s => s.id === sub.id)?.count || 0;
    return `
      <div class="subcategory-card" data-subcategory="${sub.id}">
        <span class="name">${sub.name}</span>
        <span class="count">${count}</span>
      </div>
    `;
  }).join('');
  
  panel.querySelector('.subcategory-grid').innerHTML = html;
}
```

### 3.3 CSS 样式更新

```css
/* css/pages/my-capabilities.css */

/* 分类卡片 */
.category-card {
  display: flex;
  align-items: center;
  padding: 16px;
  border-radius: 8px;
  background: #fff;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.2s;
}

.category-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

/* 用户可见分类高亮 */
.category-card.user-facing {
  border-color: #10b981;
  background: linear-gradient(135deg, #f0fdf4 0%, #fff 100%);
}

.category-card.user-facing .badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #10b981;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
}

/* 分类图标 */
.category-card .icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: #f3f4f6;
  margin-right: 12px;
}

/* biz 子分类面板 */
.subcategory-panel {
  margin-top: 24px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 12px;
}

.subcategory-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-top: 16px;
}

.subcategory-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: white;
  border-radius: 8px;
  cursor: pointer;
}

.subcategory-card:hover {
  background: #eff6ff;
}
```

---

## 四、测试验证清单

### 4.1 后端测试

| 测试项 | 验证方法 | 预期结果 |
|--------|----------|----------|
| 分类统计接口 | `GET /api/skills/categories/stats` | 返回 12 个分类统计 |
| 用户可见分类 | 检查 `userFacingCategories` | 返回 llm, knowledge, biz, util |
| biz 子分类 | `GET /api/skills/categories/biz/subcategories` | 返回 8 个子分类 |
| 技能筛选 | `GET /api/skills?category=biz` | 返回 5 个业务场景技能 |
| 废弃分类兼容 | `GET /api/skills?category=business` | 自动映射到 biz |

### 4.2 前端测试

| 测试项 | 验证方法 | 预期结果 |
|--------|----------|----------|
| 分类统计展示 | 打开 my-capabilities.html | 显示 12 个分类卡片 |
| 用户可见标记 | 检查分类卡片 | llm, knowledge, biz, util 有"用户可见"标记 |
| biz 子分类展开 | 点击 biz 分类卡片 | 展示 8 个子分类 |
| 技能筛选 | 点击分类卡片 | 跳转到对应技能列表 |
| 分类图标 | 检查各分类图标 | 显示正确的 Lucide 图标 |

---

## 五、迁移时间表

| 阶段 | 任务 | 负责团队 | 截止日期 |
|------|------|----------|----------|
| **阶段1** | 后端 SkillIndexLoader 更新 | MVP 团队 | 2026-03-20 |
| **阶段2** | 后端 API 接口更新 | MVP 团队 | 2026-03-21 |
| **阶段3** | 前端分类组件更新 | SE 团队 | 2026-03-23 |
| **阶段4** | 前端 my-capabilities.html 更新 | SE 团队 | 2026-03-24 |
| **阶段5** | 集成测试 | QA 团队 | 2026-03-25 |
| **阶段6** | 上线发布 | 全体 | 2026-03-26 |

---

## 六、FAQ

### Q1: 旧的 `business` 分类如何处理？
**A**: 后端自动映射到 `biz` 分类，前端无需特殊处理。

### Q2: 用户可见分类如何判断？
**A**: 通过 `categories.yaml` 中的 `userFacing: true` 字段判断。

### Q3: biz 子分类是否必须？
**A**: 非必须，但建议为业务场景技能设置子分类，便于前端筛选。

### Q4: 废弃字段何时完全移除？
**A**: 建议在 v2.4.0 版本完全移除，当前版本保留兼容。

---

**文档维护者**: Skills Team  
**最后更新**: 2026-03-18  
**版本**: v1.0.0
