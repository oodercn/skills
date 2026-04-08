# Nexus 前端架构规范

## 1. 脚本引用（必须）

每个页面必须按以下顺序引用脚本：

```html
<script src="/console/js/nexus.js"></script>
<script src="/console/js/menu.js"></script>
<script src="/console/js/page-init.js" data-auto-init></script>
<script src="/console/js/api.js"></script>
```

**注意事项**：
- `page-init.js` 必须有 `data-auto-init` 属性
- 脚本引用顺序不能颠倒
- 所有4个脚本都必须引用

## 2. 页面结构（必须）

```html
<body>
    <div class="nx-page">
        <aside class="nx-page__sidebar">
            <div class="nx-p-4 nx-mb-4">
                <h1 class="nx-text-lg nx-font-bold">
                    <i class="ri-bolt-line"></i> Ooder
                </h1>
                <p class="nx-text-xs nx-text-secondary">页面标题</p>
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

## 3. 图标系统（必须）

仅使用 Remix Icon，前缀为 `ri-`：

```html
<!-- 正确 -->
<i class="ri-server-line"></i>
<i class="ri-settings-4-line"></i>

<!-- 错误 -->
<i class="fas fa-server"></i>
<i class="fa fa-cog"></i>
```

## 4. CSS变量（必须）

使用CSS变量，不要硬编码颜色值：

```css
/* 正确 */
background: var(--ns-card-bg);
border: 1px solid var(--ns-border);
color: var(--ns-dark);

/* 错误 */
background: #121212;
border: 1px solid #2a2a2a;
color: #ffffff;
```

**核心变量**：
| 变量名 | 用途 | 默认值 |
|--------|------|--------|
| `--ns-card-bg` | 卡片背景 | rgba(30, 41, 59, 0.8) |
| `--ns-border` | 边框颜色 | rgba(148, 163, 184, 0.1) |
| `--ns-dark` | 主文本色 | #f8fafc |
| `--ns-secondary` | 次要文本色 | #94a3b8 |
| `--ns-primary` | 主色调 | #3b82f6 |
| `--ns-success` | 成功色 | #22c55e |
| `--ns-danger` | 危险色 | #ef4444 |
| `--ns-warning` | 警告色 | #f59e0b |

## 5. 组件类（必须）

使用Nexus组件类：

| 组件 | 类名 |
|------|------|
| 卡片 | `nx-card`, `nx-card__header`, `nx-card__body` |
| 按钮 | `nx-btn`, `nx-btn--primary`, `nx-btn--secondary` |
| 模态框 | `nx-modal`, `nx-modal--open`, `nx-modal__content` |
| 输入框 | `nx-input`, `nx-select`, `nx-textarea` |
| 徽章 | `nx-badge`, `nx-badge--success` |
| 统计卡片 | `nx-stat-card`, `nx-stat-card__icon` |

## 6. 模态框控制（必须）

使用CSS类切换，不要使用style.display：

```javascript
// 正确
document.getElementById('modal').classList.add('nx-modal--open');
document.getElementById('modal').classList.remove('nx-modal--open');

// 错误
document.getElementById('modal').style.display = 'block';
document.getElementById('modal').style.display = 'none';
```

## 7. API响应处理（必须）

检查响应使用 `status` 字段：

```javascript
// 正确
if (response.status === 'success' && response.data) {
    this.data = response.data.skills || [];
}

// 错误
if (response.code === 200) {
    this.data = response.data;
}
```

**响应结构**：
```json
{
    "status": "success",
    "message": "操作成功",
    "data": { ... },
    "code": null,
    "timestamp": 1709000000000
}
```

## 8. 菜单加载（必须）

每个页面必须加载菜单：

```javascript
async function loadMenu() {
    try {
        const response = await fetch('/api/v1/auth/menu-config');
        const result = await response.json();
        if (result.status === 'success' && result.data) {
            renderMenu(result.data);
        }
    } catch (e) {
        console.error('Failed to load menu:', e);
    }
}

function renderMenu(menuItems) {
    const menuEl = document.getElementById('nav-menu');
    menuEl.innerHTML = menuItems.map(item => `
        <li class="nav-menu__item ${item.active ? 'nav-menu__item--active' : ''}">
            <a href="${item.url}"><i class="${item.icon}"></i> ${item.name}</a>
        </li>
    `).join('');
}
```

## 9. 登录检查（必须）

需要登录的页面必须检查session：

```javascript
async function checkLogin() {
    try {
        const response = await fetch('/api/v1/auth/session');
        const result = await response.json();
        if (result.status === 'success' && result.data) {
            currentUser = result.data;
            // 检查角色
            if (currentUser.roleType !== 'required_role') {
                window.location.href = '/console/pages/login.html';
                return;
            }
            document.getElementById('user-name').textContent = currentUser.name;
            await loadMenu();
        } else {
            window.location.href = '/console/pages/login.html';
        }
    } catch (e) {
        console.error('Session check failed:', e);
        window.location.href = '/console/pages/login.html';
    }
}
```

## 10. 样式表引用（必须）

按以下顺序引用样式表：

```html
<link rel="stylesheet" href="/console/css/theme.css">
<link rel="stylesheet" href="/console/css/remixicon/remixicon.css">
<link rel="stylesheet" href="/console/css/nexus.css">
<link rel="stylesheet" href="/console/css/app.css">
```

## 检查清单

新页面创建时，必须检查以下项目：

- [ ] 脚本引用正确（nexus.js, menu.js, page-init.js, api.js）
- [ ] `data-auto-init` 属性在 page-init.js 上
- [ ] 页面结构遵循 nx-page 模式
- [ ] 所有图标使用 Remix Icon (ri-*)
- [ ] CSS使用变量 (--ns-*, --nx-*)
- [ ] 模态框使用 classList toggle
- [ ] API响应检查 `status === 'success'`
- [ ] 菜单加载逻辑存在
- [ ] 登录检查逻辑存在（如需要）
