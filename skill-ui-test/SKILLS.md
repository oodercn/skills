# Ooder Skills 开发规范

> **全局规范文件** - 本文档适用于所有Ooder Skills项目开发
> 
> IDE配置文件: `.trae/rules/project_rules.md`

## 概述

本文档记录了Ooder Skills项目开发中的通用架构规范和最佳实践，所有新功能开发都应遵循这些规范。

---

## 一、前端页面架构规范

### 1.1 模态框(Modal)规范

#### 类名使用

**正确**: 使用 `modal` 类名（不是 `nx-modal`）

```html
<div class="modal" id="my-modal">
    <div class="modal-content">
        <div class="modal-header">...</div>
        <div class="modal-body">...</div>
        <div class="modal-footer">...</div>
    </div>
</div>
```

#### 显示控制

**CSS**:
```css
.modal { display: none; }
.modal.modal--open { display: flex; }
```

**JavaScript**:
```javascript
// 正确 - 使用classList
document.getElementById('my-modal').classList.add('modal--open');
document.getElementById('my-modal').classList.remove('modal--open');

// 错误 - 不要使用style.display
document.getElementById('my-modal').style.display = 'block';
```

### 1.2 表单组件规范

#### 文本域宽度

**必须设置width: 100%**:
```html
<textarea class="nx-textarea" rows="4" style="width: 100%;"></textarea>
```

**CSS**:
```css
.nx-textarea {
    width: 100%;
    resize: vertical;
}
```

#### 表单组结构

```html
<div class="nx-form-group">
    <label class="nx-label">标签名</label>
    <input type="text" class="nx-input" placeholder="请输入">
</div>

<div class="nx-form-row">
    <div class="nx-form-group">...</div>
    <div class="nx-form-group">...</div>
</div>
```

### 1.3 脚本引用顺序

**必须按以下顺序引用**:
```html
<script src="/console/js/nexus.js"></script>
<script src="/console/js/menu.js"></script>
<script src="/console/js/page-init.js" data-auto-init></script>
<script src="/console/js/api.js"></script>
<script src="../js/my-module.js"></script>
```

**关键点**:
- `page-init.js` 必须有 `data-auto-init` 属性
- 业务JS放在最后

### 1.4 页面结构规范

```html
<body>
    <div class="nx-page">
        <aside class="nx-page__sidebar">
            <div class="nx-p-4 nx-mb-4">
                <h1 class="nx-text-lg nx-font-bold">
                    <i class="ri-icon-line"></i> 模块名称
                </h1>
            </div>
            <ul class="nav-menu" id="nav-menu"></ul>
        </aside>
        
        <main class="nx-page__content">
            <header class="nx-page__header">
                <div class="nx-page__title">...</div>
                <div class="nx-page__actions">...</div>
            </header>
            
            <div class="nx-page__main">
                <div class="nx-container">
                    <!-- 页面内容 -->
                </div>
            </div>
        </main>
    </div>
    
    <!-- 模态框放在页面结构外 -->
    <div class="modal" id="my-modal">...</div>
    
    <!-- 脚本引用 -->
</body>
```

### 1.5 图标系统

**只使用Remix Icon**，前缀为 `ri-`:

```html
<!-- 正确 -->
<i class="ri-home-line"></i>
<i class="ri-settings-line"></i>

<!-- 错误 -->
<i class="fas fa-home"></i>
<i class="fa fa-cog"></i>
```

### 1.6 CSS变量使用

**使用CSS变量，不要硬编码颜色**:

```css
/* 正确 */
background: var(--nx-bg-card);
border: 1px solid var(--nx-border);
color: var(--nx-text-primary);

/* 错误 */
background: #1a1a1a;
border: 1px solid #2a2a2a;
color: #ffffff;
```

**常用变量**:
| 变量名 | 用途 |
|--------|------|
| `--ns-primary` | 主色调 #3b82f6 |
| `--ns-success` | 成功色 #22c55e |
| `--ns-warning` | 警告色 #f59e0b |
| `--ns-danger` | 危险色 #ef4444 |
| `--nx-bg-card` | 卡片背景 |
| `--nx-bg-elevated` | 提升背景 |
| `--nx-border` | 边框颜色 |
| `--nx-text-primary` | 主文本色 |
| `--nx-text-secondary` | 次要文本色 |

---

## 二、API调用规范

### 2.1 响应检查

**检查 `status` 字段，不是 `code`**:

```javascript
// 正确
if (response.status === 'success' && response.data) {
    this.data = response.data.items || [];
}

// 错误
if (response.code === 200) {
    this.data = response.data;
}
```

### 2.2 promisify工具函数

```javascript
promisify(obj, method, ...args) {
    return new Promise((resolve, reject) => {
        obj[method](...args, {
            success: (data) => resolve({ status: 'success', data }),
            failure: (error) => resolve({ status: 'error', message: error })
        });
    });
}
```

### 2.3 错误处理

**始终处理错误情况**:

```javascript
async loadData() {
    try {
        const response = await this.promisify(NexusAPI, 'get', '/api/data');
        if (response.status === 'success' && response.data) {
            this.data = response.data.items || [];
        } else {
            this.data = this.getMockData(); // 使用模拟数据
        }
    } catch (error) {
        console.error('Failed to load data:', error);
        this.data = this.getMockData(); // 降级处理
    }
}
```

---

## 三、后端API规范

### 3.1 参数绑定规则

#### 简单参数 - 使用 @RequestParam

```java
@GetMapping("/list")
public ResponseEntity<Map<String, Object>> list(
    @RequestParam(required = false) String category,
    @RequestParam(required = false) String status) {
    // ...
}
```

#### 复杂对象 - 使用 @RequestBody DTO

```java
@PostMapping("/create")
public ResponseEntity<Map<String, Object>> create(@RequestBody ItemDTO item) {
    // ...
}
```

#### 禁止使用Map作为参数

```java
// 错误 - 不要使用Map
@PostMapping("/create")
public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> item) {
    // ...
}
```

### 3.2 响应格式

```java
Map<String, Object> result = new HashMap<>();
result.put("status", "success");
result.put("data", data);
result.put("message", "操作成功");
return ResponseEntity.ok(result);
```

### 3.3 Controller注解

```java
@RestController  // 类级别使用@RestController
@RequestMapping("/api/resource")
public class ResourceController {
    // 所有方法自动有@ResponseBody
}
```

---

## 四、Skill配置规范

### 4.1 skill.yaml结构

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-my-feature
  name: 功能名称
  version: 1.0.0
  description: 功能描述
  author: Team Name
  type: scene-skill
  license: Apache-2.0

spec:
  type: scene-skill
  
  dependencies:
    - id: skill-dependency
      version: ">=1.0.0"
      required: true
      autoInstall: true
      
  capabilities:
    - id: main-capability
      name: 主要能力
      description: 能力描述
      category: business
      type: COMPOSITE
      
  endpoints:
    - path: /api/my-feature
      method: GET
      description: API描述
      capability: main-capability
      
  ui:
    nexusUi:
      entry:
        page: index.html
        title: 页面标题
        icon: ri-icon-line
      menu:
        position: sidebar
        category: business
```

### 4.2 菜单配置

在 `menu-config.json` 中添加:

```json
{
  "id": "skill-my-feature",
  "name": "功能名称",
  "icon": "ri-icon-line",
  "url": "/console/skills/skill-my-feature/ui/pages/index.html",
  "status": "implemented",
  "roles": ["personal", "mcp"]
}
```

---

## 五、开发检查清单

创建新功能时，请确认以下事项：

### 前端检查

- [ ] 脚本引用正确（nexus.js, menu.js, page-init.js, api.js）
- [ ] `page-init.js` 有 `data-auto-init` 属性
- [ ] 页面结构使用 `nx-page` 模式
- [ ] 所有图标使用 Remix Icon（ri-*）
- [ ] CSS使用变量（--ns-*, --nx-*）
- [ ] 模态框使用 `modal` 类名
- [ ] 模态框通过 `modal--open` 类控制显示
- [ ] 文本域设置 `width: 100%`
- [ ] API响应检查 `status === 'success'`

### 后端检查

- [ ] 简单参数使用 `@RequestParam`
- [ ] 复杂对象使用 `@RequestBody DTO`
- [ ] 没有使用 Map 作为参数
- [ ] Controller 有 `@RestController` 或方法有 `@ResponseBody`
- [ ] 响应格式包含 `status` 字段

### 配置检查

- [ ] skill.yaml 配置完整
- [ ] 菜单配置已添加到 menu-config.json
- [ ] DTO类已创建（如需要）

---

## 六、常见错误速查

| 错误现象 | 原因 | 解决方案 |
|---------|------|---------|
| 模态框不显示 | 使用了nx-modal类名 | 改用modal类名 |
| 模态框一直显示 | 缺少display:none | 添加.modal { display: none; } |
| 文本域宽度不对 | 未设置宽度 | 添加width: 100% |
| API响应未处理 | 检查code字段 | 改为检查status字段 |
| 后端参数错误 | 使用Map作为参数 | 创建DTO类 |
| 菜单不显示 | 未添加菜单配置 | 更新menu-config.json |
| 图标不显示 | 使用了其他图标库 | 使用Remix Icon |

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2026-03-06 | 初始版本，整合前端、后端、配置规范 |
