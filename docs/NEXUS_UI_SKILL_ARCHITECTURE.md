# Nexus-UI Skill 类型技术方案

> **LLM 生成参考文档** | 本规范适用于文心一言、豆包、通义千问等 AI 模型生成 Nexus 控制台页面代码

## 1. 架构概述

### 1.1 设计目标
Nexus-UI Skill 是一种特殊类型的 Skill，它将前端页面（HTML/CSS/JS）与后端 API 分离封装，允许用户通过简单的文件上传方式自定义 Nexus 控制台页面功能。

### 1.2 核心原理
```
┌─────────────────────────────────────────────────────────────────┐
│                        Nexus 平台                                │
│  ┌─────────────────┐      ┌─────────────────────────────────┐  │
│  │   Web 服务器     │◄────►│      Nexus-UI Skill 容器         │  │
│  │  (静态资源服务)  │      │  ┌─────────────┐ ┌───────────┐  │  │
│  └─────────────────┘      │  │  UI 资源包   │ │  API 服务  │  │  │
│                           │  │ (html/css/js)│ │ (Java/... │  │  │
│                           │  └─────────────┘ └───────────┘  │  │
│                           └─────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ▲
                              │ 下载/安装
                    ┌─────────┴──────────┐
                    │    Skill 市场       │
                    │  (nexus-ui 类型)   │
                    └────────────────────┘
```

### 1.3 与传统 Skill 的区别

| 特性 | 传统 Skill | Nexus-UI Skill |
|------|-----------|----------------|
| 前端代码 | 内嵌在 Nexus 中 | 封装在 Skill 中 |
| 自定义方式 | 修改 Nexus 源码 | 上传 UI 文件包 |
| 更新机制 | 整体升级 | 独立更新 Skill |
| LLM 集成 | 后端能力调用 | 前端代码生成 |
| 适用场景 | 通用功能 | 定制化页面 |

---

## 2. 公共资源引用（CDN 版本）

### 2.1 必需引用的资源

所有 Nexus-UI Skill 页面**必须**使用以下 CDN 资源：

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>页面标题</title>
    
    <!-- ============================================ -->
    <!-- 1. Nexus 核心样式 (GitHub/Gitee CDN)          -->
    <!-- ============================================ -->
    <!-- GitHub Raw -->
    <link rel="stylesheet" href="https://raw.githubusercontent.com/ooderCN/nexus-assets/main/css/nexus.css">
    <!-- 或 Gitee 镜像（国内推荐） -->
    <link rel="stylesheet" href="https://gitee.com/ooderCN/nexus-assets/raw/main/css/nexus.css">
    
    <!-- ============================================ -->
    <!-- 2. Remix Icon 图标库 (CDNJS)                 -->
    <!-- ============================================ -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/remixicon/3.5.0/remixicon.min.css">
    <!-- 或 jsDelivr -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css">
    
    <!-- Skill 自定义样式（可选） -->
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>
    <!-- 页面内容 -->
    
    <!-- ============================================ -->
    <!-- 3. Nexus 核心脚本 (GitHub/Gitee CDN)          -->
    <!-- ============================================ -->
    <!-- GitHub Raw -->
    <script src="https://raw.githubusercontent.com/ooderCN/nexus-assets/main/js/nexus.js"></script>
    <script src="https://raw.githubusercontent.com/ooderCN/nexus-assets/main/js/api.js"></script>
    <!-- 或 Gitee 镜像（国内推荐） -->
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/nexus.js"></script>
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/api.js"></script>
    
    <!-- Skill 自定义脚本（可选） -->
    <script src="../js/app.js"></script>
</body>
</html>
```

### 2.2 资源引用速查表

| 资源类型 | GitHub CDN | Gitee CDN (国内推荐) |
|---------|------------|---------------------|
| nexus.css | `https://raw.githubusercontent.com/ooderCN/nexus-assets/main/css/nexus.css` | `https://gitee.com/ooderCN/nexus-assets/raw/main/css/nexus.css` |
| nexus.js | `https://raw.githubusercontent.com/ooderCN/nexus-assets/main/js/nexus.js` | `https://gitee.com/ooderCN/nexus-assets/raw/main/js/nexus.js` |
| api.js | `https://raw.githubusercontent.com/ooderCN/nexus-assets/main/js/api.js` | `https://gitee.com/ooderCN/nexus-assets/raw/main/js/api.js` |
| Remix Icon | `https://cdnjs.cloudflare.com/ajax/libs/remixicon/3.5.0/remixicon.min.css` | `https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css` |

---

## 3. 目录结构规范

### 3.1 Nexus-UI Skill 标准结构
```
skill-{name}-nexus-ui/
├── skill.yaml                    # Skill 元数据定义
├── README.md                     # 说明文档（含 LLM 提示词模板）
├── ui/                           # 前端资源目录
│   ├── pages/                    # HTML 页面
│   │   ├── index.html           # 主页面（必需）
│   │   └── *.html               # 其他页面
│   ├── css/                     # 样式文件
│   │   ├── style.css           # 自定义样式（可选）
│   │   └── theme.css           # 主题样式（可选）
│   ├── js/                      # 脚本文件
│   │   ├── app.js              # 业务逻辑（可选）
│   │   └── utils.js            # 工具函数（可选）
│   └── assets/                  # 静态资源
│       ├── images/             # 图片
│       └── fonts/              # 字体
├── api/                          # 后端 API 目录
│   ├── spec/                    # API 规范
│   │   └── openapi.yaml        # OpenAPI 定义
│   └── docs/                    # API 文档
│       └── api.md              # 接口说明
└── config/                       # 配置文件
    ├── menu.json               # 菜单配置
    └── routes.json             # 路由映射
```

### 3.2 最小化结构（必需）
```
skill-example-nexus-ui/
├── skill.yaml
├── README.md
└── ui/
    └── pages/
        └── index.html
```

---

## 4. 前端规范（强制要求）

### 4.1 HTML 页面模板

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{{skill.title}}</title>
    
    <!-- Nexus 核心样式 -->
    <link rel="stylesheet" href="https://gitee.com/ooderCN/nexus-assets/raw/main/css/nexus.css">
    
    <!-- Remix Icon -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css">
    
    <!-- Skill 自定义样式（可选） -->
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>
    <!-- ============================================ -->
    <!-- Nexus 页面结构（必须严格遵循）                -->
    <!-- ============================================ -->
    <div class="nx-page">
        <!-- 侧边栏（由 Nexus 自动注入） -->
        <aside class="nx-page__sidebar" id="sidebar">
            <div class="nx-p-4 nx-mb-4">
                <h1 class="nx-text-lg nx-font-bold">
                    <i class="ri-server-line"></i> Nexus Console
                </h1>
            </div>
            <ul class="nav-menu" id="nav-menu"></ul>
        </aside>
        
        <!-- 主内容区 -->
        <main class="nx-page__content">
            <header class="nx-page__header">
                <h1 class="nx-page__title">
                    <i class="ri-dashboard-line"></i>
                    {{skill.title}}
                </h1>
            </header>
            
            <div class="nx-page__main">
                <div class="nx-container">
                    <!-- Skill 自定义内容开始 -->
                    <div id="app">
                        <!-- 用户自定义内容 -->
                    </div>
                    <!-- Skill 自定义内容结束 -->
                </div>
            </div>
        </main>
    </div>
    
    <!-- Nexus 核心脚本 -->
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/nexus.js"></script>
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/api.js"></script>
    
    <!-- Skill 自定义脚本（可选） -->
    <script src="../js/app.js"></script>
</body>
</html>
```

### 4.2 CSS 变量规范（强制使用）

```css
/* ============================================ */
/* 必须使用 Nexus CSS 变量，禁止硬编码颜色值       */
/* ============================================ */

:root {
    /* 颜色系统 */
    --skill-primary: var(--ns-primary, #3b82f6);
    --skill-success: var(--ns-success, #22c55e);
    --skill-warning: var(--ns-warning, #f59e0b);
    --skill-danger: var(--ns-danger, #ef4444);
    
    /* 背景色 */
    --skill-bg: var(--ns-bg, #0f0f0f);
    --skill-card-bg: var(--ns-card-bg, #1a1a1a);
    
    /* 文字色 */
    --skill-text: var(--ns-dark, #ffffff);
    --skill-text-secondary: var(--ns-secondary, #a0a0a0);
    
    /* 边框 */
    --skill-border: var(--ns-border, #2a2a2a);
    --skill-border-radius: var(--ns-radius, 8px);
    
    /* 间距 */
    --skill-spacing-xs: var(--ns-spacing-xs, 4px);
    --skill-spacing-sm: var(--ns-spacing-sm, 8px);
    --skill-spacing-md: var(--ns-spacing-md, 16px);
    --skill-spacing-lg: var(--ns-spacing-lg, 24px);
}

/* 正确示例 */
.my-card {
    background: var(--ns-card-bg);
    border: 1px solid var(--ns-border);
    color: var(--ns-dark);
    border-radius: var(--ns-radius);
    padding: var(--ns-spacing-md);
}

/* 错误示例 - 禁止使用 */
.my-card-wrong {
    background: #121212;        /* 错误：硬编码颜色 */
    border: 1px solid #2a2a2a;  /* 错误：硬编码颜色 */
    color: #ffffff;             /* 错误：硬编码颜色 */
}
```

**核心 CSS 变量列表**：
- `--ns-card-bg`: 卡片背景
- `--ns-border`: 边框颜色
- `--ns-dark`: 主文字颜色
- `--ns-secondary`: 次要文字颜色
- `--ns-primary`: 主题色 (#3b82f6)
- `--ns-success`: 成功色 (#22c55e)
- `--ns-danger`: 危险色 (#ef4444)
- `--ns-warning`: 警告色 (#f59e0b)

### 4.3 图标系统规范（强制使用 Remix Icon）

```html
<!-- ============================================ -->
<!-- 必须使用 Remix Icon (ri-* 前缀)              -->
<!-- ============================================ -->

<!-- 正确示例 -->
<i class="ri-server-line"></i>
<i class="ri-settings-4-line"></i>
<i class="ri-refresh-line"></i>
<i class="ri-dashboard-line"></i>
<i class="ri-bar-chart-line"></i>
<i class="ri-user-line"></i>

<!-- 错误示例 - 禁止使用 -->
<i class="fas fa-server"></i>    <!-- FontAwesome -->
<i class="fa fa-cog"></i>        <!-- FontAwesome -->
<i class="icon-settings"></i>    <!-- 自定义图标 -->
```

### 4.4 组件类规范（强制使用）

| 组件 | 类名 | 示例 |
|------|------|------|
| 卡片 | `nx-card`, `nx-card__header`, `nx-card__body` | `<div class="nx-card"><div class="nx-card__header">标题</div><div class="nx-card__body">内容</div></div>` |
| 按钮 | `nx-btn`, `nx-btn--primary`, `nx-btn--secondary` | `<button class="nx-btn nx-btn--primary">确定</button>` |
| 模态框 | `nx-modal`, `nx-modal--open`, `nx-modal__content` | `<div class="nx-modal" id="modal"><div class="nx-modal__content">...</div></div>` |
| 输入框 | `nx-input`, `nx-select`, `nx-textarea` | `<input class="nx-input" type="text">` |
| 标签 | `nx-badge`, `nx-badge--success` | `<span class="nx-badge nx-badge--success">成功</span>` |
| 统计卡片 | `nx-stat-card`, `nx-stat-card__icon` | `<div class="nx-stat-card"><i class="nx-stat-card__icon ri-user-line"></i>...</div>` |

### 4.5 JavaScript 规范

```javascript
// ============================================
// 模态框控制（必须使用 classList，禁止 style.display）
// ============================================

// 正确示例
function openModal() {
    document.getElementById('modal').classList.add('nx-modal--open');
}

function closeModal() {
    document.getElementById('modal').classList.remove('nx-modal--open');
}

// 错误示例 - 禁止使用
function openModalWrong() {
    document.getElementById('modal').style.display = 'block';  // 错误！
}

// ============================================
// API 响应处理（必须检查 status 字段）
// ============================================

// 正确示例
async function loadData() {
    try {
        const response = await Nexus.api.get('/api/data');
        if (response.status === 'success' && response.data) {
            // 列表数据
            this.items = response.data.items || [];
            // 或单对象
            this.detail = response.data;
        }
    } catch (error) {
        Nexus.notify.error('加载失败: ' + error.message);
    }
}

// 错误示例 - 禁止使用
async function loadDataWrong() {
    const response = await Nexus.api.get('/api/data');
    if (response.code === 200) {  // 错误：不应检查 code
        this.items = response.data;
    }
}
```

**API 响应格式**：
```json
{
    "status": "success",
    "message": "操作成功",
    "data": { ... },
    "code": null,
    "timestamp": 1709000000000
}
```

---

## 5. 元数据规范 (skill.yaml)

### 5.1 Nexus-UI Skill 专用配置
```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-example-nexus-ui
  name: Example Nexus UI
  version: 1.0.0
  description: 示例 Nexus UI Skill
  type: nexus-ui              # 必需：标识为 Nexus-UI 类型
  
spec:
  type: nexus-ui
  
  # Nexus-UI 特有配置
  nexusUi:
    # 入口页面配置
    entry:
      page: index.html
      title: 示例页面
      icon: ri-dashboard-line    # Remix Icon
      
    # 菜单配置
    menu:
      position: sidebar         # sidebar | header | dropdown
      category: tools          # 菜单分类
      order: 100               # 排序权重
      
    # 布局配置
    layout:
      type: default            # default | fullscreen | embedded
      sidebar: true            # 是否显示侧边栏
      header: true             # 是否显示头部
      
    # 依赖的 Nexus 版本
    compatibility:
      nexusVersion: ">=2.3.0"
      
    # 前端资源映射
    staticResources:
      basePath: /ui/{skill-id}  # 静态资源访问路径
      cacheEnabled: true
      cacheDuration: 3600
      
    # API 代理配置
    apiProxy:
      enabled: true
      prefix: /api/skills/{skill-id}
      target: http://localhost:{port}
      
  # API 定义
  apis:
    - path: /api/data
      method: GET
      description: 获取数据
      page: index.html          # 关联的页面
      
  # 依赖的其他 Skill
  dependencies:
    skills: []
    
  # 配置项
  config:
    optional:
      - name: THEME_COLOR
        type: string
        description: 主题颜色
        default: "#3b82f6"
```

---

## 6. 用户自定义工作流

### 6.1 创建流程
```
1. 用户通过 LLM 生成页面代码
   ├─ 使用文心/豆包/千问等生成 HTML/CSS/JS
   ├─ 或使用本地 LLM 进行代码修改
   └─ 遵循 Nexus 架构规范（使用本文档作为提示词）

2. 打包为 Nexus-UI Skill
   ├─ 创建 skill.yaml 元数据文件
   ├─ 组织 ui/ 目录结构
   └─ 编写 README.md 说明文档

3. 上传到 Skill 市场
   ├─ 打包为 .skill 文件
   └─ 提交到市场审核

4. Nexus 下载并部署
   ├─ 解析 skill.yaml
   ├─ 注册菜单和路由
   ├─ 部署静态资源
   └─ 启动 API 服务（如有）
```

### 6.2 LLM 提示词模板（README.md 中使用）

```markdown
## LLM 生成提示词模板

使用以下提示词模板，通过 AI 模型（文心一言/豆包/通义千问等）生成 Nexus 控制台页面：

### 基础提示词

```
请帮我创建一个 Nexus 控制台页面，要求：

【架构规范】（必须严格遵守）
1. 使用 CDN 资源：
   - CSS: https://gitee.com/ooderCN/nexus-assets/raw/main/css/nexus.css
   - JS: https://gitee.com/ooderCN/nexus-assets/raw/main/js/nexus.js
   - JS: https://gitee.com/ooderCN/nexus-assets/raw/main/js/api.js
   - Icon: https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css

2. 使用 Remix Icon (ri-* 前缀)，如：ri-dashboard-line, ri-settings-4-line

3. 使用 CSS 变量 (--ns-*)：
   - 背景: var(--ns-card-bg)
   - 边框: var(--ns-border)
   - 文字: var(--ns-dark)
   - 主题色: var(--ns-primary)
   - 禁止硬编码颜色值！

4. 页面结构必须使用 nx-page 布局：
   - nx-page (根容器)
   - nx-page__sidebar (侧边栏)
   - nx-page__content (内容区)
   - nx-page__header (头部)
   - nx-page__main (主内容)
   - nx-container (容器)

5. 使用 Nexus 组件类：
   - 卡片: nx-card, nx-card__header, nx-card__body
   - 按钮: nx-btn, nx-btn--primary
   - 输入框: nx-input
   - 标签: nx-badge

6. JavaScript 规范：
   - 模态框使用 classList.add('nx-modal--open')，禁止 style.display
   - API 响应检查 response.status === 'success'
   - 使用 Nexus.api 进行请求

【页面功能】
- [在此描述具体功能需求]

【输出要求】
- 完整的 HTML 文件
- 必要的 CSS 样式（使用 CSS 变量）
- 交互 JavaScript 代码
- 符合上述所有架构规范
```

### 示例提示词（数据仪表盘）

```
请帮我创建一个 Nexus 控制台数据仪表盘页面：

【架构规范】（必须严格遵守）
1. CDN 资源：
   - CSS: https://gitee.com/ooderCN/nexus-assets/raw/main/css/nexus.css
   - Icon: https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css
   - JS: https://gitee.com/ooderCN/nexus-assets/raw/main/js/nexus.js
   - JS: https://gitee.com/ooderCN/nexus-assets/raw/main/js/api.js

2. 必须使用 Remix Icon，如 ri-dashboard-3-line, ri-bar-chart-line

3. 必须使用 CSS 变量：
   - var(--ns-card-bg) 作为卡片背景
   - var(--ns-border) 作为边框
   - var(--ns-dark) 作为文字颜色
   - var(--ns-primary) 作为主题色
   - 禁止硬编码 #121212, #ffffff 等颜色值

4. 页面结构：
   <div class="nx-page">
     <aside class="nx-page__sidebar">...</aside>
     <main class="nx-page__content">
       <header class="nx-page__header">...</header>
       <div class="nx-page__main">
         <div class="nx-container">...</div>
       </div>
     </main>
   </div>

5. 使用组件类：
   - 统计卡片: nx-stat-card
   - 内容卡片: nx-card
   - 按钮: nx-btn nx-btn--primary

【页面功能】
1. 顶部显示 4 个统计卡片（用户数、订单数、收入、增长率）
2. 下方左侧显示数据表格（最近订单）
3. 下方右侧显示图表占位区域
4. 使用模拟数据展示效果

【输出】
完整的 HTML 文件，包含内联 CSS 和 JavaScript
```
```

---

## 7. 完整示例

### 7.1 skill.yaml
```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-dashboard-nexus-ui
  name: 自定义仪表盘
  version: 1.0.0
  description: 用户自定义的数据仪表盘
  type: nexus-ui
  author: User
  
spec:
  type: nexus-ui
  
  nexusUi:
    entry:
      page: index.html
      title: 我的仪表盘
      icon: ri-dashboard-3-line
      
    menu:
      position: sidebar
      category: custom
      order: 50
      
    layout:
      type: default
      sidebar: true
      header: true
      
    staticResources:
      basePath: /ui/skill-dashboard-nexus-ui
      
    apiProxy:
      enabled: true
      prefix: /api/skills/skill-dashboard-nexus-ui
```

### 7.2 index.html（完整示例）
```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的仪表盘</title>
    
    <!-- Nexus 核心样式 (Gitee CDN) -->
    <link rel="stylesheet" href="https://gitee.com/ooderCN/nexus-assets/raw/main/css/nexus.css">
    
    <!-- Remix Icon (jsDelivr CDN) -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css">
    
    <!-- Skill 自定义样式 -->
    <link rel="stylesheet" href="../css/dashboard.css">
</head>
<body>
    <div class="nx-page">
        <!-- 侧边栏 -->
        <aside class="nx-page__sidebar" id="sidebar">
            <div class="nx-p-4 nx-mb-4">
                <h1 class="nx-text-lg nx-font-bold">
                    <i class="ri-server-line"></i> Nexus Console
                </h1>
            </div>
            <ul class="nav-menu" id="nav-menu"></ul>
        </aside>
        
        <!-- 主内容区 -->
        <main class="nx-page__content">
            <header class="nx-page__header">
                <h1 class="nx-page__title">
                    <i class="ri-dashboard-3-line"></i>
                    我的仪表盘
                </h1>
            </header>
            
            <div class="nx-page__main">
                <div class="nx-container">
                    <!-- 统计卡片区域 -->
                    <div class="dashboard-stats">
                        <div class="nx-stat-card">
                            <i class="nx-stat-card__icon ri-user-line"></i>
                            <div class="nx-stat-card__info">
                                <span class="nx-stat-card__value" id="user-count">1,234</span>
                                <span class="nx-stat-card__label">总用户数</span>
                            </div>
                        </div>
                        <div class="nx-stat-card">
                            <i class="nx-stat-card__icon ri-shopping-cart-line"></i>
                            <div class="nx-stat-card__info">
                                <span class="nx-stat-card__value" id="order-count">567</span>
                                <span class="nx-stat-card__label">今日订单</span>
                            </div>
                        </div>
                        <div class="nx-stat-card">
                            <i class="nx-stat-card__icon ri-money-cny-circle-line"></i>
                            <div class="nx-stat-card__info">
                                <span class="nx-stat-card__value" id="revenue">¥89,234</span>
                                <span class="nx-stat-card__label">今日收入</span>
                            </div>
                        </div>
                        <div class="nx-stat-card">
                            <i class="nx-stat-card__icon ri-line-chart-line"></i>
                            <div class="nx-stat-card__info">
                                <span class="nx-stat-card__value" id="growth">+23.5%</span>
                                <span class="nx-stat-card__label">增长率</span>
                            </div>
                        </div>
                    </div>
                    
                    <!-- 内容区域 -->
                    <div class="dashboard-content">
                        <div class="nx-card">
                            <div class="nx-card__header">
                                <h3><i class="ri-bar-chart-line"></i> 数据统计</h3>
                            </div>
                            <div class="nx-card__body" id="stats-container">
                                <!-- 动态加载 -->
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
    
    <!-- Nexus 核心脚本 (Gitee CDN) -->
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/nexus.js"></script>
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/api.js"></script>
    
    <!-- Skill 自定义脚本 -->
    <script src="../js/dashboard.js"></script>
</body>
</html>
```

### 7.3 dashboard.css（使用 CSS 变量）
```css
/* 使用 Nexus CSS 变量 */
.dashboard-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: var(--ns-spacing-md, 16px);
    margin-bottom: var(--ns-spacing-lg, 24px);
}

.dashboard-content {
    display: grid;
    gap: var(--ns-spacing-md, 16px);
}

/* 统计卡片样式 */
.nx-stat-card {
    background: var(--ns-card-bg, #1a1a1a);
    border: 1px solid var(--ns-border, #2a2a2a);
    border-radius: var(--ns-radius, 8px);
    padding: var(--ns-spacing-md, 16px);
    display: flex;
    align-items: center;
    gap: var(--ns-spacing-md, 16px);
}

.nx-stat-card__icon {
    font-size: 32px;
    color: var(--ns-primary, #3b82f6);
}

.nx-stat-card__info {
    display: flex;
    flex-direction: column;
}

.nx-stat-card__value {
    font-size: 24px;
    font-weight: bold;
    color: var(--ns-dark, #ffffff);
}

.nx-stat-card__label {
    font-size: 14px;
    color: var(--ns-secondary, #a0a0a0);
}
```

### 7.4 dashboard.js
```javascript
document.addEventListener('DOMContentLoaded', async () => {
    // 初始化 Nexus 页面
    await Nexus.init();
    
    // 加载数据
    await loadDashboardData();
});

async function loadDashboardData() {
    try {
        const response = await Nexus.api.get('/api/skills/skill-dashboard-nexus-ui/api/stats');
        if (response.status === 'success' && response.data) {
            renderStats(response.data);
        }
    } catch (error) {
        Nexus.notify.error('加载数据失败: ' + error.message);
    }
}

function renderStats(data) {
    document.getElementById('user-count').textContent = data.userCount || '0';
    document.getElementById('order-count').textContent = data.orderCount || '0';
    document.getElementById('revenue').textContent = data.revenue || '¥0';
    document.getElementById('growth').textContent = data.growth || '0%';
}
```

---

## 8. 部署与分发

### 8.1 打包格式
```
skill-{name}-nexus-ui-{version}.skill
├── META-INF/
│   └── skill.yaml
├── ui/
│   ├── pages/
│   ├── css/
│   ├── js/
│   └── assets/
├── api/          # 可选
└── README.md     # 必须包含 LLM 提示词模板
```

### 8.2 安装流程
```bash
# 安装 Nexus-UI Skill
ooder skill install skill-dashboard-nexus-ui-1.0.0.skill

# 自动执行：
# 1. 解压到 skills/ 目录
# 2. 解析 skill.yaml
# 3. 注册菜单和路由
# 4. 部署静态资源到 /console/ui/
# 5. 重启 Nexus 服务（如需要）
```

---

## 9. 总结

Nexus-UI Skill 类型实现了以下目标：

1. **前后端分离**：前端资源封装在 Skill 中，后端 API 独立部署
2. **用户自定义**：支持通过 LLM 生成和修改页面代码
3. **简单部署**：上传文件即可完成页面定制
4. **架构兼容**：完全遵循 Nexus 架构规范
5. **安全隔离**：Skill 页面运行在沙箱环境中
6. **CDN 资源**：使用 GitHub/Gitee 托管的公共资源

### 关键要点
- ✅ 使用 CDN 引用公共资源（Gitee 推荐）
- ✅ 使用 Remix Icon (`ri-*`)
- ✅ 使用 CSS 变量 (`--ns-*`)
- ✅ 使用 Nexus 组件类 (`nx-*`)
- ✅ README 中包含 LLM 提示词模板
- ❌ 禁止硬编码颜色值
- ❌ 禁止使用 `style.display` 控制模态框
- ❌ 禁止使用非 Remix Icon

---

**文档版本**: 1.0.0  
**适用 Nexus 版本**: >= 2.3.0  
**最后更新**: 2026-02-25
