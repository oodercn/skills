# 现有 API 和前端规范总结

## 一、前端技术栈

### 1.1 基础技术
- **CSS框架**: Tailwind CSS 2.2.19
- **图标库**: Font Awesome 6.0.0 / Remix Icon
- **图表库**: Chart.js 3.9.1
- **UI组件**: 自定义CSS + Tailwind

### 1.2 项目结构
```
console/
├── index.html              # 主入口
├── menu-config.json        # 菜单配置
├── pages/                  # 页面目录
│   ├── dashboard.html
│   ├── skill-runtime-monitor.html
│   ├── scene/
│   │   ├── list.html
│   │   ├── dashboard.html
│   │   └── ...
│   └── ...
├── js/                     # JavaScript目录
│   ├── api.js             # 基础API模块
│   ├── skill-management.js # Skill管理
│   ├── ui.js              # UI工具
│   └── ...
├── css/                    # 样式目录
│   ├── styles.css
│   └── theme.css
└── data/                   # 静态数据
    └── skills-data.json
```

---

## 二、API 规范

### 2.1 基础请求模块 (`js/api.js`)
```javascript
// 异步获取数据
async function fetchData(url, options = {})

// 使用示例
const data = await fetchData('/api/test/capability/skills/list', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({})
});
```

### 2.2 响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": { ... }
}
```

### 2.3 现有 API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/test/capability/skills/list` | POST | 获取所有 Skills |
| `/api/test/capability/skills/detail` | POST | 获取 Skill 详情 |
| `/api/scene/runtime/status` | POST | 获取运行时状态 |
| `/api/scene/runtime/capabilities` | POST | 获取能力列表 |
| `/api/analysis/lifecycle/data` | POST | 获取生命周期分析数据 |
| `/api/analysis/lifecycle/loops` | POST | 分析3个关键闭环 |
| `/api/analysis/requirements/spec` | POST | 生成需求规格书 |
| `/api/test/sdk-native/status` | POST | 获取 SDK 原生事件状态 |
| `/api/test/sdk-native/events` | POST | 获取 SDK 原生事件记录 |
| `/api/test/sdk-native/skills` | POST | 获取 Skill 状态快照 |

---

## 三、前端组件规范

### 3.1 状态徽章
```html
<span class="bg-green-100 text-green-800 px-2 py-1 rounded-full text-xs">
    运行中
</span>
```

### 3.2 卡片组件
```html
<div class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition">
    <h3 class="text-lg font-semibold">标题</h3>
    <p class="text-gray-600">内容</p>
</div>
```

### 3.3 按钮样式
```html
<!-- 主要按钮 -->
<button class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">
    <i class="fas fa-sync-alt mr-2"></i>刷新
</button>

<!-- 次要按钮 -->
<button class="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300">
    取消
</button>

<!-- 危险按钮 -->
<button class="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700">
    删除
</button>
```

### 3.4 表格样式
```html
<table class="min-w-full divide-y divide-gray-200">
    <thead class="bg-gray-50">
        <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">列名</th>
        </tr>
    </thead>
    <tbody class="bg-white divide-y divide-gray-200">
        <tr>
            <td class="px-6 py-4 whitespace-nowrap">数据</td>
        </tr>
    </tbody>
</table>
```

---

## 四、菜单配置规范

### 4.1 菜单结构 (`menu-config.json`)
```json
{
    "id": "menu-id",
    "name": "菜单名称",
    "icon": "ri-icon-name",
    "url": "/console/pages/path.html",
    "status": "implemented",
    "roles": ["home", "lan", "enterprise"],
    "children": [ ... ]
}
```

### 4.2 图标规范
- 使用 Remix Icon (`ri-` 前缀)
- 常用图标:
  - 仪表盘: `ri-dashboard-line`
  - 列表: `ri-list-check`
  - 设置: `ri-settings-4-line`
  - 监控: `ri-pulse-line`
  - 安全: `ri-shield-keyhole-line`
  - 网络: `ri-network-line`

---

## 五、JavaScript 类规范

### 5.1 SkillManagement 类模式
```javascript
class SkillManagement {
    constructor() {
        this.api = new API();
        this.data = [];
        this.filter = {};
    }

    async init() {
        await this.loadData();
        this.bindEvents();
    }

    async loadData() {
        // API调用 + 降级到本地数据
    }

    render() {
        // 渲染逻辑
    }

    bindEvents() {
        // 事件绑定
    }
}
```

### 5.2 错误处理模式
```javascript
try {
    const response = await api.call();
    if (response.code === 200) {
        // 成功处理
    } else {
        // 业务错误
    }
} catch (error) {
    console.error('Error:', error);
    // 降级到本地数据
}
```

---

## 六、样式规范

### 6.1 颜色系统
```css
/* 主色调 */
--primary: #3B82F6;        /* 蓝色 */
--success: #10B981;        /* 绿色 */
--warning: #F59E0B;        /* 黄色 */
--danger: #EF4444;         /* 红色 */
--info: #8B5CF6;           /* 紫色 */

/* 状态色 */
--status-active: #10B981;
--status-inactive: #9CA3AF;
--status-error: #EF4444;
--status-warning: #F59E0B;
```

### 6.2 间距规范
```css
--space-xs: 0.25rem;   /* 4px */
--space-sm: 0.5rem;    /* 8px */
--space-md: 1rem;      /* 16px */
--space-lg: 1.5rem;    /* 24px */
--space-xl: 2rem;      /* 32px */
```

### 6.3 圆角规范
```css
--radius-sm: 0.375rem;  /* 6px */
--radius-md: 0.5rem;    /* 8px */
--radius-lg: 0.75rem;   /* 12px */
--radius-xl: 1rem;      /* 16px */
```

---

## 七、响应式断点

| 断点 | 宽度 | 说明 |
|------|------|------|
| `sm` | 640px | 小屏手机 |
| `md` | 768px | 平板 |
| `lg` | 1024px | 小桌面 |
| `xl` | 1280px | 大桌面 |
| `2xl` | 1536px | 超大屏 |

---

## 八、动画规范

### 8.1 过渡动画
```css
/* 卡片悬停 */
.skill-card {
    transition: all 0.3s ease;
}
.skill-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
}

/* 状态闪烁 */
@keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.7; }
}
.status-badge {
    animation: pulse 2s infinite;
}
```

### 8.2 入场动画
```css
@keyframes slideIn {
    from { opacity: 0; transform: translateX(-20px); }
    to { opacity: 1; transform: translateX(0); }
}
.event-item {
    animation: slideIn 0.3s ease;
}
```

---

## 九、现有页面参考

### 9.1 skill-runtime-monitor.html
- 实时监控仪表盘
- 指标卡片 + 图表 + 事件流
- 使用 Chart.js 绘制图表
- 定时刷新机制 (5秒)

### 9.2 scene/dashboard.html
- 场景监控页面
- 状态网格 + 拓扑图
- 实时数据推送

### 9.3 llm-integration/skill-management.html
- Skill 管理列表
- 筛选 + 搜索 + 分页
- 卡片式布局

---

## 十、常见页面/API 问题

### 10.1 脚本引用错误

**问题**: 引用不存在的脚本文件或使用错误的文件名
```html
<!-- 错误 -->
<script src="/console/js/nexus/ui.js"></script>
<script src="/console/js/menu.js"></script>

<!-- 正确 -->
<script src="/console/js/nexus.js"></script>
<script src="/console/js/nexus-menu.js"></script>
<script src="/console/js/page-init.js" data-auto-init></script>
<script src="/console/js/api.js"></script>
```

**说明**: 
- `nexus.js` 是核心 UI 框架，提供 NX 全局对象
- `nexus-menu.js` 提供菜单加载功能（注意：不是 menu.js）
- `page-init.js` 提供页面自动初始化，需添加 `data-auto-init` 属性
- `api.js` 提供 `fetchData()` 函数

---

### 10.2 API 响应格式处理错误

**问题**: 使用错误的响应字段判断
```javascript
// 错误 - 使用 code 字段
if (response.code === 200) {
    this.data = response.data;
}

// 正确 - 使用 status 字段
if (response.status === 'success' && response.data) {
    this.data = response.data.skills || [];
}
```

**API 响应格式**:
```json
{
    "status": "success",
    "message": "操作成功",
    "data": { ... },
    "code": null,
    "timestamp": 1709000000000
}
```

**错误响应**:
```json
{
    "status": "error",
    "message": "错误信息",
    "data": null,
    "code": "ERROR_CODE",
    "timestamp": 1709000000000
}
```

---

### 10.3 模态框控制方式错误

**问题**: 使用 style.display 直接控制
```javascript
// 错误
document.getElementById('modal').style.display = 'block';
document.getElementById('modal').style.display = 'none';

// 正确 - 使用 CSS 类控制
document.getElementById('modal').classList.add('nx-modal--open');
document.getElementById('modal').classList.remove('nx-modal--open');
```

**说明**: nexus.css 已定义模态框动画，使用类切换可保持动画效果

---

### 10.4 图标系统不统一

**问题**: 混用 Font Awesome 和 Remix Icon
```html
<!-- 错误 -->
<i class="fas fa-server"></i>
<i class="fa fa-cog"></i>

<!-- 正确 - 统一使用 Remix Icon -->
<i class="ri-server-line"></i>
<i class="ri-settings-4-line"></i>
```

**常用 Remix Icon 对照**:
| 用途 | Font Awesome | Remix Icon |
|------|-------------|------------|
| 服务器 | fa-server | ri-server-line |
| 设置 | fa-cog | ri-settings-4-line |
| 刷新 | fa-sync | ri-refresh-line |
| 删除 | fa-trash | ri-delete-bin-line |
| 列表 | fa-list | ri-list-check |
| 监控 | fa-chart-line | ri-pulse-line |

---

### 10.5 CSS 变量使用错误

**问题**: 硬编码颜色值
```css
/* 错误 */
.card {
    background: #121212;
    border: 1px solid #2a2a2a;
    color: #ffffff;
}

/* 正确 - 使用 CSS 变量 */
.card {
    background: var(--ns-card-bg);
    border: 1px solid var(--ns-border);
    color: var(--ns-dark);
}
```

**核心 CSS 变量**:
| 变量 | 用途 | 暗色值 | 亮色值 |
|------|------|--------|--------|
| `--ns-card-bg` | 卡片背景 | #121212 | #f1f5f9 |
| `--ns-border` | 边框颜色 | #2a2a2a | #e2e8f0 |
| `--ns-dark` | 主文字色 | #ffffff | #1e293b |
| `--ns-secondary` | 次要文字 | #94a3b8 | #64748b |
| `--ns-primary` | 主色调 | #3b82f6 | #3b82f6 |
| `--ns-success` | 成功色 | #22c55e | #22c55e |
| `--ns-danger` | 危险色 | #ef4444 | #ef4444 |
| `--ns-warning` | 警告色 | #f59e0b | #f59e0b |

---

### 10.6 页面结构不规范

**问题**: 缺少必要的页面结构
```html
<!-- 错误 - 缺少侧边栏和标准结构 -->
<body>
    <div class="content">...</div>
</body>

<!-- 正确 - 标准页面结构 -->
<body>
    <div class="nx-page">
        <aside class="nx-page__sidebar">
            <div class="nx-p-4 nx-mb-4">
                <h1 class="nx-text-lg nx-font-bold">
                    <i class="ri-server-line"></i> Nexus Console
                </h1>
            </div>
            <ul class="nav-menu" id="nav-menu"></ul>
        </aside>
        
        <main class="nx-page__content">
            <header class="nx-page__header">...</header>
            <div class="nx-page__main">
                <div class="nx-container">...</div>
            </div>
        </main>
    </div>
</body>
```

---

### 10.7 API 路径命名不规范

**问题**: API 路径层级过深或不一致
```java
// 不推荐 - 层级过深
@RequestMapping("/api/analysis/lifecycle")
@PostMapping("/data")

// 推荐 - 简洁明了
@RequestMapping("/api/skill-lifecycle")
@PostMapping("/list")
@PostMapping("/detail")
```

**命名规范**:
- 使用名词复数: `/api/scenes`, `/api/skills`
- 动作作为路径: `/api/scene/start`, `/api/scene/stop`
- 保持一致性: 同类资源使用相同前缀

---

### 10.8 数据提取路径错误

**问题**: 未正确解析嵌套数据结构
```javascript
// 错误 - 直接使用 data
this.skills = response.data;

// 正确 - 根据实际结构提取
this.skills = response.data.skills || [];
this.events = response.data.events || [];
```

**常见数据结构**:
```javascript
// 列表数据
{ data: { skills: [...], totalCount: 10 } }

// 事件数据
{ data: { events: [...], totalCount: 50 } }

// 单个对象
{ data: { skillId: "...", skillName: "..." } }
```

---

## 十一、开发建议

### 11.1 新增页面步骤
1. 在 `pages/` 下创建 HTML 文件
2. 引入基础样式和脚本（参考标准模板）
3. 在 `menu-config.json` 中添加菜单项
4. 创建对应的 JS 文件（如需要）
5. 使用 `page-init.js` 自动初始化

### 11.2 API 调用建议
1. 优先使用 API 获取数据
2. 提供本地 mock 数据降级
3. 添加 loading 状态
4. 统一错误处理
5. 正确解析响应格式

### 11.3 性能优化
1. 使用虚拟滚动处理大量数据
2. 图表使用懒加载
3. API 请求防抖/节流
4. 图片懒加载

---

*文档生成时间: 2026-02-25*
*最后更新: 2026-02-25*
