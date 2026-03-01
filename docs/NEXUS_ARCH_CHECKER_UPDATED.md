---
name: "nexus-arch-checker"
description: "Checks Nexus frontend pages and API architecture compliance. Invoke when creating new pages, reviewing code, or user asks for architecture validation."
---

# Nexus Architecture Checker

This skill provides comprehensive architecture validation for Nexus Enterprise frontend pages and backend APIs.

## When to Invoke

- Creating new HTML pages in `/console/pages/`
- Reviewing frontend code for compliance
- User asks for architecture check
- Before committing new UI components
- Validating API response format
- Creating or reviewing backend Controller APIs

## Architecture Rules

### 1. Script References (Required)

Every page MUST include these scripts in order:

```html
<script src="/console/js/nexus.js"></script>
<script src="/console/js/menu.js"></script>
<script src="/console/js/page-init.js" data-auto-init></script>
<script src="/console/js/api.js"></script>
```

**Common Error**: Using `/console/js/nexus/ui.js` - this file does not exist.

### 2. Page Structure (Required)

```html
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

### 3. Icon System (Required)

Use ONLY Remix Icon with `ri-` prefix:

```html
<!-- Correct -->
<i class="ri-server-line"></i>
<i class="ri-settings-4-line"></i>
<i class="ri-refresh-line"></i>

<!-- Wrong -->
<i class="fas fa-server"></i>
<i class="fa fa-cog"></i>
```

### 4. CSS Variables (Required)

Use CSS variables, NOT hardcoded values:

```css
/* Correct */
background: var(--ns-card-bg);
border: 1px solid var(--ns-border);
color: var(--ns-dark);

/* Wrong */
background: #121212;
border: 1px solid #2a2a2a;
color: #ffffff;
```

**Core Variables**:
- `--ns-card-bg`: Card background
- `--ns-border`: Border color
- `--ns-dark`: Primary text color
- `--ns-secondary`: Secondary text color
- `--ns-primary`: Primary color (#3b82f6)
- `--ns-success`: Success color (#22c55e)
- `--ns-danger`: Danger color (#ef4444)
- `--ns-warning`: Warning color (#f59e0b)

### 5. Component Classes (Required)

Use Nexus component classes:

| Component | Class |
|-----------|-------|
| Card | `nx-card`, `nx-card__header`, `nx-card__body` |
| Button | `nx-btn`, `nx-btn--primary`, `nx-btn--secondary` |
| Modal | `nx-modal`, `nx-modal--open`, `nx-modal__content` |
| Input | `nx-input`, `nx-select`, `nx-textarea` |
| Badge | `nx-badge`, `nx-badge--success` |
| Stat Card | `nx-stat-card`, `nx-stat-card__icon` |

### 6. Modal Control (Required)

Use CSS class toggle, NOT style.display:

```javascript
// Correct
document.getElementById('modal').classList.add('nx-modal--open');
document.getElementById('modal').classList.remove('nx-modal--open');

// Wrong
document.getElementById('modal').style.display = 'block';
document.getElementById('modal').style.display = 'none';
```

### 7. API Response Format (Required)

Check response using `status` field:

```javascript
// Correct
if (response.status === 'success' && response.data) {
    this.data = response.data.skills || [];
}

// Wrong
if (response.code === 200) {
    this.data = response.data;
}
```

**Response Structure**:
```json
{
    "status": "success",
    "message": "操作成功",
    "data": { ... },
    "code": null,
    "timestamp": 1709000000000
}
```

### 8. Data Extraction (Required)

Extract nested data correctly:

```javascript
// List data
this.skills = response.data.skills || [];
this.events = response.data.events || [];

// Single object
this.skill = response.data;
```

### 9. API Parameter Binding (Required)

**核心原则：禁止使用 Map 作为 Controller 方法参数**

#### 9.1 简单参数 - 使用 @RequestParam

当参数数量较少（1-3个简单字段如 id、name、key）时，直接使用 `@RequestParam`：

```java
// ✅ Correct - Simple parameters use @RequestParam
@PostMapping("/get")
@ResponseBody
public ResultModel<SkillDTO> getSkill(@RequestParam String skillId) {
    // ...
}

@PostMapping("/list")
@ResponseBody
public ResultModel<List<TaskDTO>> listTasks(
    @RequestParam(required = false) String groupId,
    @RequestParam(required = false) String status) {
    // ...
}

// ❌ Wrong - Do NOT use Map
public ResultModel<List<TaskDTO>> listTasks(@RequestBody Map<String, String> request) { }

// ❌ Wrong - Do NOT create DTO wrapper for simple params
public ResultModel<List<TaskDTO>> listTasks(@RequestBody TaskListRequestDTO request) { }
```

#### 9.2 复杂对象 - 使用 @RequestBody DTO

当参数为复杂对象（包含嵌套结构、列表、多个相关字段）时，使用 DTO：

```java
// ✅ Correct - Complex objects use @RequestBody DTO
@PostMapping("/create")
@ResponseBody
public ResultModel<SkillDTO> createSkill(@RequestBody SkillDTO request) {
    // SkillDTO has multiple fields, nested objects, lists, etc.
}

@PostMapping("/execute")
@ResponseBody
public ResultModel<ExecutionResult> execute(@RequestBody ExecutionRequestDTO request) {
    // ExecutionRequestDTO has parameters Map, configurations list, etc.
}
```

#### 9.3 Parameter Selection Guide

| Scenario | Approach | Example |
|----------|----------|---------|
| Single ID query | `@RequestParam String id` | `getSkill(@RequestParam String skillId)` |
| 2-3 simple filters | Multiple `@RequestParam` | `list(groupId, status, assignedTo)` |
| Create/Update | `@RequestBody DTO` | `createSkill(@RequestBody SkillDTO)` |
| Lists/Nested objects | `@RequestBody DTO` | `batchDelete(@RequestBody BatchRequestDTO)` |
| File upload | `@RequestParam MultipartFile` | `upload(@RequestParam MultipartFile file)` |

### 10. Response Body Annotation (Required)

All Controller methods MUST have `@ResponseBody` or use `@RestController`:

```java
// ✅ Correct - @RestController at class level
@RestController
@RequestMapping("/api/skill")
public class SkillController {
    @PostMapping("/get")
    public ResultModel<SkillDTO> getSkill(@RequestParam String skillId) {
        // ...
    }
}

// ✅ Correct - @ResponseBody at method level
@Controller
@RequestMapping("/api/skill")
public class SkillController {
    @PostMapping("/get")
    @ResponseBody
    public ResultModel<SkillDTO> getSkill(@RequestParam String skillId) {
        // ...
    }
}
```

### 11. No Inline Scripts (Required)

**禁止在HTML中使用内嵌`<script>`标签编写业务逻辑**

所有JavaScript代码必须抽取到独立的`.js`文件中：

```html
<!-- ❌ Wrong - Inline script in HTML -->
<script>
    document.addEventListener('DOMContentLoaded', () => {
        MyComponent.init();
    });
</script>

<!-- ✅ Correct - External script file -->
<script src="/console/js/pages/my-component-init.js"></script>
```

**目录结构规范**：
```
ui/
├── pages/
│   └── index.html          # HTML页面，无内嵌脚本
├── js/
│   ├── components/          # 组件逻辑
│   │   └── my-component.js
│   └── pages/               # 页面初始化脚本
│       └── my-component-init.js
└── css/
    └── components/          # 组件样式
        └── my-component.css
```

**页面初始化脚本模板** (`/console/js/pages/xxx-init.js`)：
```javascript
document.addEventListener('DOMContentLoaded', () => {
    // 初始化逻辑
    MyComponent.init();
});
```

### 12. No Inline Styles (Required)

**禁止在HTML中使用内嵌`<style>`标签**

所有CSS样式必须抽取到独立的`.css`文件中：

```html
<!-- ❌ Wrong - Inline style in HTML -->
<style>
    .my-custom-class {
        background: var(--ns-card-bg);
    }
</style>

<!-- ✅ Correct - External stylesheet -->
<link rel="stylesheet" href="/console/css/components/my-component.css">
```

**CSS文件命名规范**：
- 组件样式: `/console/css/components/{component-name}.css`
- 页面样式: `/console/css/pages/{page-name}.css`

### 13. Script and Style Extraction Checklist

当检查新增页面时，确保：

| 检查项 | 要求 |
|--------|------|
| 内嵌`<script>` | 仅允许引用外部JS文件，禁止内嵌代码 |
| 内嵌`<style>` | 禁止使用，必须抽取到外部CSS文件 |
| 初始化脚本 | 放置在`/console/js/pages/`目录 |
| 组件脚本 | 放置在`/console/js/components/`目录 |
| 组件样式 | 放置在`/console/css/components/`目录 |

**抽取步骤**：

1. **抽取内嵌脚本**：
   - 创建 `/console/js/pages/{page-name}-init.js`
   - 将内嵌脚本内容移入该文件
   - HTML中替换为 `<script src="/console/js/pages/{page-name}-init.js"></script>`

2. **抽取内嵌样式**：
   - 创建 `/console/css/components/{component-name}.css`
   - 将内嵌样式内容移入该文件
   - HTML中替换为 `<link rel="stylesheet" href="/console/css/components/{component-name}.css">`

## Checklist for New Pages

- [ ] Script references correct (nexus.js, menu.js, page-init.js, api.js)
- [ ] `data-auto-init` attribute on page-init.js
- [ ] Page structure follows nx-page pattern
- [ ] All icons use Remix Icon (ri-*)
- [ ] CSS uses variables (--ns-*, --nx-*)
- [ ] Modal uses classList toggle
- [ ] API response checks `status === 'success'`
- [ ] Menu entry added to menu-config.json
- [ ] No inline `<script>` tags (use external JS files)
- [ ] No inline `<style>` tags (use external CSS files)

## Checklist for Backend APIs

- [ ] Simple parameters use `@RequestParam`, NOT Map or DTO wrapper
- [ ] Complex objects use `@RequestBody DTO`
- [ ] No `Map<String, Object>` or `Map<String, String>` as parameters
- [ ] Return value has `@ResponseBody` or class has `@RestController`
- [ ] Response format uses unified `ResultModel`

## Quick Fix Commands

### Fix Script References
```html
<!-- Replace -->
<script src="/console/js/nexus/ui.js"></script>
<!-- With -->
<script src="/console/js/nexus.js"></script>
<script src="/console/js/menu.js"></script>
<script src="/console/js/page-init.js" data-auto-init></script>
<script src="/console/js/api.js"></script>
```

### Fix Modal Control
```javascript
// Replace style.display with classList
element.style.display = 'block'  ->  element.classList.add('nx-modal--open')
element.style.display = 'none'   ->  element.classList.remove('nx-modal--open')
```

### Fix API Response Check
```javascript
// Replace code check with status check
response.code === 200  ->  response.status === 'success' && response.data
```

### Fix Map Parameter
```java
// Replace Map with DTO
@RequestBody Map<String, Object> request  ->  @RequestBody MyRequestDTO request
```

### Fix Inline Script
```html
<!-- Replace inline script with external file -->
<script>
    document.addEventListener('DOMContentLoaded', () => {
        MyComponent.init();
    });
</script>
<!-- With -->
<script src="/console/js/pages/my-component-init.js"></script>
```

### Fix Inline Style
```html
<!-- Replace inline style with external file -->
<style>
    .my-class { ... }
</style>
<!-- With -->
<link rel="stylesheet" href="/console/css/components/my-component.css">
```
