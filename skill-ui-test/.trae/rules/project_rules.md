# Ooder Skills 项目开发规范

本文档定义了Ooder Skills项目的全局开发规范，所有代码生成和修改都必须遵循这些规则。

---

## 一、前端页面架构规范

### 1.1 模态框(Modal)规范

**类名使用**: 使用 `modal` 类名（不是 `nx-modal`）

```html
<div class="modal" id="my-modal">
    <div class="modal-content">
        <div class="modal-header">...</div>
        <div class="modal-body">...</div>
        <div class="modal-footer">...</div>
    </div>
</div>
```

**显示控制**:
```css
.modal { display: none; }
.modal.modal--open { display: flex; }
```

```javascript
// 正确 - 使用classList
document.getElementById('my-modal').classList.add('modal--open');
document.getElementById('my-modal').classList.remove('modal--open');

// 错误 - 不要使用style.display
document.getElementById('my-modal').style.display = 'block';
```

### 1.2 表单组件规范

**文本域宽度必须设置width: 100%**:
```html
<textarea class="nx-textarea" rows="4" style="width: 100%;"></textarea>
```

**表单组结构**:
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

**关键**: `page-init.js` 必须有 `data-auto-init` 属性

### 1.4 图标系统

**只使用Remix Icon**，前缀为 `ri-`:
```html
<!-- 正确 -->
<i class="ri-home-line"></i>
<i class="ri-settings-line"></i>

<!-- 错误 -->
<i class="fas fa-home"></i>
<i class="fa fa-cog"></i>
```

### 1.5 CSS变量使用

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

### 2.2 错误处理

**始终处理错误情况**:
```javascript
async loadData() {
    try {
        const response = await this.promisify(NexusAPI, 'get', '/api/data');
        if (response.status === 'success' && response.data) {
            this.data = response.data.items || [];
        } else {
            this.data = this.getMockData();
        }
    } catch (error) {
        console.error('Failed to load data:', error);
        this.data = this.getMockData();
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

## 四、开发检查清单

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

---

## 五、常见错误速查

| 错误现象 | 原因 | 解决方案 |
|---------|------|---------|
| 模态框不显示 | 使用了nx-modal类名 | 改用modal类名 |
| 模态框一直显示 | 缺少display:none | 添加.modal { display: none; } |
| 文本域宽度不对 | 未设置宽度 | 添加width: 100% |
| API响应未处理 | 检查code字段 | 改为检查status字段 |
| 后端参数错误 | 使用Map作为参数 | 创建DTO类 |
| 菜单不显示 | 未添加菜单配置 | 更新menu-config.json |
| 图标不显示 | 使用了其他图标库 | 使用Remix Icon |
