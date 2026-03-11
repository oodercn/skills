---
alwaysApply: false
description: 新建页面或技能时触发
---
# Ooder 技能开发规范

> 本文档整合前端架构、后端API、新功能开发指南等核心规范

---

## 一、前端架构规范

### 1.1 脚本引用（必须按顺序）

```html
<script src="/console/js/nexus.js"></script>
<script src="/console/js/api-client.js"></script>
<script src="/console/js/menu.js"></script>
<script src="/console/js/page-init.js" data-auto-init></script>
```

### 1.2 核心规范

| 规范项 | 正确 ✅ | 错误 ❌ |
|--------|---------|---------|
| 图标 | `ri-server-line` | `fas fa-server` |
| CSS变量 | `var(--nx-primary)` | `#3b82f6` |
| 模态框 | `classList.add('nx-modal--open')` | `style.display='block'` |
| API响应 | `response.status === 'success'` | `response.code === 200` |

### 1.3 CSS变量

| 变量 | 用途 |
|------|------|
| `--nx-primary` | 主色调 |
| `--nx-success` / `--nx-warning` / `--nx-danger` | 状态色 |
| `--nx-bg` / `--nx-bg-elevated` | 背景色 |

---

## 二、后端 API 规范

### 2.1 参数传递

```java
// 简单参数 → @RequestParam
@GetMapping("/users")
public ResultModel<List<UserDTO>> list(@RequestParam String role)

// 复杂对象 → @RequestBody DTO
@PostMapping("/users")
public ResultModel<UserDTO> create(@RequestBody UserCreateDTO request)
```

### 2.2 统一响应格式

```json
{
    "status": "success",
    "message": "操作成功",
    "data": { ... }
}
```

---

## 三、三闭环检查（新功能必检）

### 3.1 生命周期闭环

| 检查项 | 要求 |
|--------|------|
| 创建API | POST /api/v1/{resource} |
| 查询API | GET /api/v1/{resource}/{id} |
| 更新API | PUT /api/v1/{resource}/{id} |
| 删除API | DELETE /api/v1/{resource}/{id} |

### 3.2 数据实体闭环

- 实体关系图明确（1:N, N:M）
- 级联操作处理
- 操作后重新加载数据

### 3.3 按钮API闭环

```javascript
async function standardAction(params) {
    if (!confirm('确定执行？')) return;
    try {
        const response = await fetch('/api/v1/resource', { method: 'POST', body: JSON.stringify(params) });
        const result = await response.json();
        if (result.status === 'success') {
            loadData();
        }
    } catch (error) {
        alert('操作失败: ' + error.message);
    }
}
```

---

## 四、命名规范

### 4.1 类命名

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| DTO | XxxDTO | SceneGroupDTO |
| 枚举 | XxxType/XxxStatus | SceneGroupStatus |
| 控制器 | XxxController | SceneGroupController |

### 4.2 API路径

| 操作 | 路径模板 |
|------|----------|
| 列表 | GET /api/v1/{resource} |
| 详情 | GET /api/v1/{resource}/{id} |
| 创建 | POST /api/v1/{resource} |
| 更新 | PUT /api/v1/{resource}/{id} |
| 删除 | DELETE /api/v1/{resource}/{id} |

---

## 五、场景技能分类

| 分类 | 代码 | 条件 |
|------|------|------|
| 自驱业务场景 | ABS | hasSelfDrive=true + score>=8 |
| 自驱系统场景 | ASS | hasSelfDrive=true + score<8 |
| 触发业务场景 | TBS | hasSelfDrive=false + score>=8 |

---

## 六、三层知识架构

| 层级 | 名称 | 优先级 | 范围 |
|------|------|--------|------|
| GENERAL | 通用知识层 | 0 | 全局共享 |
| PROFESSIONAL | 专业模块层 | 1 | 领域共享 |
| SCENE | 场景知识层 | 2 | 场景私有 |

---

## 七、决策引擎模式

| 模式 | 说明 |
|------|------|
| ONLINE_ONLY | 仅 LLM 在线决策 |
| OFFLINE_ONLY | 仅规则引擎离线决策 |
| ONLINE_FIRST | 优先 LLM，失败降级规则（默认） |

---

## 八、新页面检查清单

- [ ] 脚本引用正确（nexus.js, api-client.js, menu.js, page-init.js）
- [ ] `data-auto-init` 属性在 page-init.js 上
- [ ] 所有图标使用 Remix Icon (ri-*)
- [ ] CSS 使用变量 (--nx-*)
- [ ] 模态框使用 classList 切换
- [ ] API 响应检查 `status === 'success'`
- [ ] 三闭环检查通过
- [ ] 菜单入口已添加到 menu-role-config.json

---

## 九、快速修复

### 修复脚本引用

```html
<!-- 错误 -->
<script src="/console/js/nexus/ui.js"></script>
<!-- 正确 -->
<script src="/console/js/nexus.js"></script>
<script src="/console/js/page-init.js" data-auto-init></script>
```

### 修复 Map 参数

```java
// 错误
public ResultModel<List<UserDTO>> list(@RequestBody Map<String, String> request)
// 正确
public ResultModel<List<UserDTO>> list(@RequestParam String role, @RequestParam String status)
```

### 修复 API 响应检查

```javascript
// 错误
if (response.code === 200)
// 正确
if (response.status === 'success' && response.data)
```

---

## 十、分级渐进页面设计模式

> 基于"我的能力"页面重构总结，适用于需要层级穿透的列表页面

### 10.1 层级结构定义

**三层架构**：

```
┌─────────────────────────────────────────────────────────────┐
│  第一层：聚合单元（如技能包）                                  │
│  - 显示统计数量                                               │
│  - 提供穿透入口                                               │
│  - 详情面板展示聚合信息                                       │
└─────────────────────────────────────────────────────────────┘
                          ↓ drillDown
┌─────────────────────────────────────────────────────────────┐
│  第二层：子项列表（如能力单元）                                │
│  - 显示具体子项详情                                           │
│  - 返回上一级按钮                                             │
│  - 子项操作入口                                               │
└─────────────────────────────────────────────────────────────┘
                          ↓ showDetail
┌─────────────────────────────────────────────────────────────┐
│  第三层：子项详情面板                                         │
│  - 完整属性展示                                               │
│  - 操作按钮                                                   │
└─────────────────────────────────────────────────────────────┘
```

### 10.2 术语规范

| 层级 | 术语 | 说明 | 示例 |
|------|------|------|------|
| 第一层 | 技能包/聚合单元 | 可安装的单元 | skill-mqtt |
| 第二层 | 能力单元/子项 | 聚合单元内的具体项 | mqtt-push |
| 第三层 | 能力详情 | 单个能力单元的完整信息 | - |

**重要**：列表中显示"X 个能力单元"而非"X 个能力"

### 10.3 穿透导航实现

**核心变量**：
```javascript
var currentDrillDown = null;  // 当前穿透状态

// 穿透到子项列表
function drillDownToCapabilities(skillId, ownership) {
    currentDrillDown = { skillId: skillId, ownership: ownership, package: pkg };
    // 渲染子项列表，包含返回按钮
}

// 返回聚合列表
function backToSkillPackages() {
    currentDrillDown = null;
    renderTable();
}
```

**返回按钮模板**：
```javascript
var html = '<tr><td colspan="8" style="background: var(--nx-bg); padding: 12px 16px;">' +
    '<div style="display: flex; align-items: center; gap: 12px;">' +
    '<button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="backToSkillPackages()">' +
    '<i class="ri-arrow-left-line"></i> 返回</button>' +
    '<span style="color: var(--nx-text-secondary);">|</span>' +
    '<span class="ownership-badge">' + config.shortName + '</span>' +
    '<span style="font-weight: 500;">' + pkg.name + '</span>' +
    '<span style="color: var(--nx-text-secondary);">包含 ' + pkg.capabilities.length + ' 个能力单元</span>' +
    '</div></td></tr>';
```

### 10.4 动态过滤器设计

**过滤器状态变量**：
```javascript
var currentFilter = 'all';           // 状态过滤
var currentTypeFilter = 'all';       // 类型过滤
var currentOwnershipFilter = 'all';  // 归属类型过滤
```

**动态更新过滤器**：
```javascript
updateFilterChips: function() {
    var statusChips = document.getElementById('statusFilterChips');
    var typeChips = document.getElementById('typeFilterChips');
    
    // 根据当前归属类型生成不同的过滤选项
    if (currentOwnershipFilter === 'SIC') {
        typeHtml = '全部, 场景特性, 原子能力';
    } else if (currentOwnershipFilter === 'IC') {
        typeHtml = '全部, 服务, AI, 连接器';
    } else if (currentOwnershipFilter === 'PC') {
        typeHtml = '全部, 服务, 数据, 安全, 管理';
    }
    // ...
}
```

**切换归属类型时重置过滤**：
```javascript
filterByOwnership: function(ownership) {
    currentOwnershipFilter = ownership;
    currentFilter = 'all';      // 重置状态过滤
    currentTypeFilter = 'all';  // 重置类型过滤
    this.updateFilterChips();   // 更新过滤器UI
    this.renderTable();         // 重新渲染
}
```

### 10.5 详情面板层级提示

**信息提示模板**：
```javascript
var html = '<div class="info-tip">' +
    '<i class="ri-information-line"></i>' +
    '<div class="info-tip-content">' +
    '<div class="info-tip-title">技能包层级</div>' +
    '<div class="info-tip-desc">技能包 → 能力列表 → 能力单元。点击"查看能力列表"可穿透查看详细信息。</div>' +
    '</div></div>';
```

### 10.6 操作按钮设计

| 层级 | 操作按钮 | 说明 |
|------|---------|------|
| 聚合列表 | 👁 详情, → 查看子项 | 详情打开面板，箭头穿透 |
| 子项列表 | 👁 详情 | 详情打开子项面板 |
| 聚合详情 | 查看子项列表, 卸载/调用 | 根据类型显示不同操作 |
| 子项详情 | 调用能力 | 执行具体能力 |

### 10.7 归属类型配置模式

**配置对象**：
```javascript
var OWNERSHIP_CONFIG = {
    'SIC': {
        name: '场景技能',
        shortName: 'SIC',
        icon: 'ri-puzzle-line',
        desc: '场景内可见，生命周期绑定场景',
        color: '#db2777',
        bgColor: '#fce7f3',
        features: ['场景内可见', '绑定场景', '生命周期绑定场景'],
        canBind: ['IC', 'PC']  // 可绑定的类型
    },
    // ... 其他类型
};
```

**能力类型配置**：
```javascript
var CAPABILITY_TYPE_CONFIG = {
    'ATOMIC': { name: '原子能力', shortName: 'AC', icon: 'ri-flashlight-line', desc: '单一功能' },
    'SCENE': { name: '场景特性', shortName: 'SC', icon: 'ri-layout-grid-line', desc: '自驱型' },
    // ... 其他类型
};
```

### 10.8 数据分类逻辑

**预定义分类列表**：
```javascript
var SIC_SKILLS = ['skill-llm-chat', 'skill-knowledge-qa', ...];
var IC_SKILLS = ['skill-mqtt', 'skill-knowledge-base', ...];
var PC_SKILLS = ['skill-user-auth', 'skill-vfs-local', ...];
var TOOL_SKILLS = ['skill-openwrt', 'skill-trae-solo', ...];
```

**分类函数**：
```javascript
getSkillOwnership: function(skillId) {
    if (SIC_SKILLS.indexOf(skillId) >= 0) return 'SIC';
    if (IC_SKILLS.indexOf(skillId) >= 0) return 'IC';
    if (PC_SKILLS.indexOf(skillId) >= 0) return 'PC';
    if (TOOL_SKILLS.indexOf(skillId) >= 0) return 'TOOL';
    return 'PC';  // 默认
}
```

### 10.9 完整页面检查清单

- [ ] 三层架构定义清晰
- [ ] 术语使用正确（能力单元 vs 能力）
- [ ] 穿透导航有返回按钮
- [ ] 过滤器随归属类型动态变化
- [ ] 详情面板有层级提示
- [ ] 操作按钮根据类型显示
- [ ] 归属类型配置完整
- [ ] 能力类型配置完整
- [ ] 数据分类逻辑正确
