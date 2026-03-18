# OODER 场景检查技术文档

## 概述

本文档总结了OODER Skills项目中场景和能力模块的技术检查要求，包括页面关联检测、Nexus架构检查、JS/CSS抽取规范等内容。

---

## 一、页面分级与关联检测

### 1.1 页面分级定义

| 级别 | 说明 | 示例 |
|------|------|------|
| **一级页面** | 菜单入口页面，通过菜单配置直接访问 | `scene-management.html`, `capability-management.html` |
| **二级页面** | 从一级页面跳转的详情/管理页面 | `capability-detail.html`, `scene-group-management.html` |
| **三级页面** | 从二级页面跳转的子功能页面 | `participants.html`, `capabilities.html` |
| **四级页面** | 从三级页面跳转的深层功能页面 | `agent/detail.html`, `binding/detail.html` |

### 1.2 页面关联检测要求

#### 1.2.1 菜单配置检测

**文件位置**: `src/main/resources/static/console/menu-config.json`

**检测项**:
- [ ] 所有URL路径是否正确指向实际存在的HTML文件
- [ ] URL格式是否使用正确的skill路径前缀
- [ ] 菜单项的icon、name、sort字段是否完整

**正确格式**:
```json
{
    "menu": [
        {
            "id": "scene-management",
            "name": "场景管理",
            "icon": "ri-folder-line",
            "children": [
                {
                    "id": "scene-list",
                    "name": "场景列表",
                    "icon": "ri-folder-line",
                    "url": "/console/skills/skill-scene-management/pages/scene-management.html",
                    "sort": 1
                }
            ]
        }
    ]
}
```

**路径格式**: `/console/skills/{skill-id}/pages/{page-path}.html`

#### 1.2.2 页面跳转检测

**检测项**:
- [ ] `window.location.href` 跳转路径是否正确
- [ ] 相对路径是否基于当前页面位置正确计算
- [ ] 返回链接是否指向正确的上级页面

**同级页面跳转** (推荐使用相对路径):
```javascript
// 同目录下跳转
window.location.href = `capability-detail.html?id=${id}`;

// 上级目录跳转
window.location.href = `../scene-group-management.html`;
```

**跨目录跳转**:
```javascript
// 从 scene/participants.html 跳转到 scene/capabilities.html
window.location.href = `capabilities.html`;

// 从 scene/agent/detail.html 跳转到 scene/agent/list.html
window.location.href = `list.html`;

// 从 scene/agent/detail.html 跳转到 pages/scene-management.html
window.location.href = `../../scene-management.html`;
```

#### 1.2.3 返回链接检测

**HTML返回链接**:
```html
<!-- 从三级页面返回二级页面 -->
<a href="../scene-group-management.html">
    <i class="ri-arrow-left-line"></i> 返回场景组
</a>

<!-- 从四级页面返回三级页面 -->
<a href="list.html">
    <i class="ri-arrow-left-line"></i> 返回列表
</a>
```

---

## 二、Nexus架构检查

### 2.1 CSS架构规范

**文件位置**: `src/main/resources/static/console/css/nexus.css`

**检测项**:
- [ ] 是否使用 `nx-` 前缀的CSS类名
- [ ] 是否遵循BEM命名规范
- [ ] 页面特定样式是否抽取到独立CSS文件

**CSS类命名规范**:
```css
/* 组件命名: nx-{component} */
.nx-page { }
.nx-card { }
.nx-btn { }

/* 元素命名: nx-{component}__{element} */
.nx-page__header { }
.nx-page__content { }
.nx-card__body { }

/* 修饰符命名: nx-{component}--{modifier} */
.nx-btn--primary { }
.nx-btn--ghost { }
.nx-badge--success { }

/* 工具类: nx-{property}-{value} */
.nx-flex { }
.nx-mb-4 { }
.nx-text-primary { }
```

### 2.2 JS架构规范

**文件位置**: `src/main/resources/static/console/js/`

**检测项**:
- [ ] 页面JS是否抽取到独立文件
- [ ] 是否使用IIFE封装避免全局污染
- [ ] 是否通过window导出公共方法
- [ ] 是否使用SceneUtils工具类

**JS文件结构规范**:
```javascript
(function() {
    'use strict';

    // 从URL获取参数
    const sceneId = SceneUtils.getUrlParam('id');

    document.addEventListener('DOMContentLoaded', async function() {
        await NexusMenu.init();
        loadPageData();
    });

    // 页面方法定义
    async function loadPageData() {
        // ...
    }

    function handleAction(id) {
        // ...
    }

    // 导出公共方法
    window.PageName = {
        loadPageData,
        handleAction
    };
})();
```

### 2.3 HTML结构规范

**检测项**:
- [ ] 是否使用Nexus CSS类
- [ ] 是否正确引入CSS/JS资源
- [ ] 是否包含菜单容器

**标准页面模板**:
```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>页面标题 - Nexus Console</title>
    <link rel="stylesheet" href="/console/css/remixicon/remixicon.css">
    <link rel="stylesheet" href="/console/css/nexus.css">
</head>
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
            <!-- 页面内容 -->
        </main>
    </div>
    
    <script src="/console/js/nexus.js"></script>
    <script src="/console/js/menu.js"></script>
    <script src="/console/js/page-init.js" data-auto-init></script>
    <script src="/console/js/pages/page-name.js"></script>
</body>
</html>
```

---

## 三、JS/CSS抽取规范

### 3.1 抽取原则

1. **内联JS抽取**: 所有 `<script>` 标签内的代码抽取到独立JS文件
2. **内联CSS抽取**: 所有 `<style>` 标签内的样式抽取到独立CSS文件
3. **事件处理**: 避免使用 `onclick` 等内联事件，改用事件监听

### 3.2 JS文件组织

**目录结构**:
```
static/console/
├── js/
│   ├── nexus.js          # 核心框架
│   ├── menu.js           # 菜单组件
│   ├── api-client.js     # API客户端
│   ├── scene-utils.js    # 场景工具类
│   └── pages/            # 页面JS
│       ├── scene-management.js
│       ├── capability-detail.js
│       └── ...
├── css/
│   ├── nexus.css         # 核心样式
│   ├── scene-pages.css   # 场景页面样式
│   └── pages/            # 页面特定样式
│       └── ...
└── pages/
    └── ...
```

### 3.3 抽取检查清单

- [ ] 检查HTML中是否还有 `<script>` 内联代码
- [ ] 检查HTML中是否还有 `<style>` 内联样式
- [ ] 检查JS文件是否正确引入
- [ ] 检查CSS文件是否正确引入
- [ ] 检查页面功能是否正常

---

## 四、资源路径规范

### 4.1 路径类型

| 路径类型 | 格式 | 使用场景 |
|----------|------|----------|
| **绝对路径** | `/console/css/nexus.css` | 公共资源引用 |
| **相对路径** | `../scene-management.html` | 页面间跳转 |
| **Skill路径** | `/console/skills/{skill-id}/pages/xxx.html` | 菜单配置 |

### 4.2 资源引用规范

**公共资源** (使用绝对路径):
```html
<link rel="stylesheet" href="/console/css/remixicon/remixicon.css">
<link rel="stylesheet" href="/console/css/nexus.css">
<script src="/console/js/nexus.js"></script>
<script src="/console/js/menu.js"></script>
```

**页面资源** (使用相对路径):
```html
<!-- 在 pages/scene/ 目录下 -->
<link rel="stylesheet" href="/console/css/scene-pages.css">
<script src="/console/js/pages/participants.js"></script>
```

### 4.3 常见路径问题

**问题1**: 菜单URL路径错误
```
错误: /console/pages/topology.html
正确: /console/skills/skill-scene-management/pages/scene/agent/topology.html
```

**问题2**: 返回链接路径错误
```
错误: href="/console/pages/scene-group-management.html"
正确: href="../scene-group-management.html"
```

**问题3**: index.html重定向错误
```
错误: window.location.href = '/console/pages/scene-management.html';
正确: window.location.href = 'pages/scene-management.html';
```

---

## 五、检测工具与脚本

### 5.1 页面路径检测

```bash
# 检查菜单配置中的URL是否存在
grep -r '"url"' */src/main/resources/static/console/menu-config.json

# 检查HTML文件是否存在
find . -name "*.html" -path "*/console/pages/*"
```

### 5.2 内联代码检测

```bash
# 检查内联JS
grep -r '<script>' */src/main/resources/static/console/pages/ --include="*.html"

# 检查内联CSS
grep -r '<style>' */src/main/resources/static/console/pages/ --include="*.html"

# 检查内联事件
grep -r 'onclick=' */src/main/resources/static/console/pages/ --include="*.html"
```

### 5.3 路径规范检测

```bash
# 检查硬编码路径
grep -r '/console/pages/' */src/main/resources/static/console/ --include="*.html" --include="*.js"

# 检查正确的skill路径
grep -r '/console/skills/' */src/main/resources/static/console/ --include="*.json"
```

---

## 六、参考工程

### 6.1 标准参考

| 模块 | 路径 | 说明 |
|------|------|------|
| skill-scene-management | `skills/_system/skill-scene-management/` | 场景管理模块 |
| skill-capability | `skills/_system/skill-capability/` | 能力管理模块 |
| skill-common | `skills/_system/skill-common/` | 公共基础模块 |

### 6.2 关键文件

| 文件 | 路径 | 作用 |
|------|------|------|
| nexus.css | `static/console/css/nexus.css` | 核心CSS框架 |
| nexus.js | `static/console/js/nexus.js` | 核心JS框架 |
| menu.js | `static/console/js/menu.js` | 菜单组件 |
| scene-utils.js | `static/console/js/scene-utils.js` | 场景工具类 |
| menu-config.json | `static/console/menu-config.json` | 菜单配置 |

---

## 七、检查流程

### 7.1 新页面开发检查

1. **创建HTML文件** - 放入正确的pages子目录
2. **创建JS文件** - 放入js/pages目录
3. **创建CSS文件** - 如有特定样式，放入css/pages目录
4. **更新菜单配置** - 添加正确的URL路径
5. **测试页面跳转** - 验证所有链接正常

### 7.2 页面修改检查

1. **检查内联代码** - 确保已抽取
2. **检查路径引用** - 确保使用正确格式
3. **检查返回链接** - 确保指向正确
4. **测试功能** - 验证页面正常工作

### 7.3 发布前检查

1. 运行所有检测脚本
2. 验证菜单配置完整性
3. 测试所有页面跳转
4. 检查资源加载正常

---

## 八、常见问题与解决方案

### Q1: 页面404错误

**原因**: 路径配置错误或文件不存在

**解决**: 
1. 检查文件实际位置
2. 更新菜单配置URL
3. 使用相对路径进行页面跳转

### Q2: CSS样式不生效

**原因**: CSS文件未正确引入或路径错误

**解决**:
1. 检查CSS文件是否存在
2. 检查link标签路径
3. 确保使用绝对路径引用公共CSS

### Q3: JS功能不工作

**原因**: JS文件未正确引入或执行顺序错误

**解决**:
1. 检查JS文件是否存在
2. 确保nexus.js在页面JS之前加载
3. 检查浏览器控制台错误

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-03-16 | 初始版本 |

---

*本文档由OODER团队维护*
