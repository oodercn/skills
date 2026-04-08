# 页面架构检查报告

## 一、检查范围

新建页面：
- `scene-capabilities.html`
- `installed-scene-capabilities.html`
- `org-management.html`
- `role-admin.html`
- `role-leader.html`
- `role-collaborator.html`
- `scene-capability-detail.html`

## 二、架构规范检查

### 2.1 脚本引用规范

**标准引用顺序**:
```html
<script src="/console/js/nexus.js"></script>
<script src="/console/js/menu.js"></script>
<script src="/console/js/page-init.js" data-auto-init></script>
<script src="/console/js/api.js"></script>
```

**检查结果**:

| 页面 | nexus.js | menu.js | page-init.js | api.js | 状态 |
|------|:--------:|:-------:|:------------:|:------:|:----:|
| scene-capabilities.html | ✅ | ❌ | ❌ | ❌ | 不合规 |
| installed-scene-capabilities.html | ✅ | ✅ | ✅ | ❌ | 不合规 |
| org-management.html | ✅ | ✅ | ✅ | ✅ | 合规 |
| role-admin.html | ✅ | ✅ | ✅ | ✅ | 合规 |
| role-leader.html | ✅ | ✅ | ✅ | ✅ | 合规 |
| role-collaborator.html | ✅ | ✅ | ✅ | ✅ | 合规 |
| scene-capability-detail.html | ✅ | ✅ | ✅ | ✅ | 合规 |

### 2.2 页面结构规范

**标准结构**:
```html
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
        <div class="nx-page__main">...</div>
    </main>
</div>
```

### 2.3 图标系统规范

**要求**: 使用 Remix Icon (`ri-*` 前缀)

**检查结果**: 所有页面均使用 Remix Icon ✅

### 2.4 CSS 变量规范

**要求**: 使用 CSS 变量 (`--nx-*`, `--ns-*`)

**检查结果**: 所有页面均使用 CSS 变量 ✅

## 三、需求规格对照

### 3.1 用户故事映射

| 用户故事 | 页面 | 功能 | 状态 |
|---------|------|------|:----:|
| US-CAP-001 | scene-capabilities.html | 查看场景中的能力列表 | ⚠️ 部分 |
| US-CAP-002 | scene-capabilities.html | 按类型筛选能力 | ❌ 缺失 |
| US-CAP-003 | scene-capabilities.html | 搜索能力名称 | ❌ 缺失 |
| US-CAP-004 | scene-capability-detail.html | 查看能力详情 | ⚠️ 部分 |

### 3.2 API 调用映射

| API ID | 路径 | 页面 | 状态 |
|--------|------|------|:----:|
| API-C-001 | GET /api/scenes/capabilities/list | scene-capabilities.html | ⚠️ 使用了错误的API |
| API-C-005 | GET /api/scenes/capabilities/get | scene-capability-detail.html | ⚠️ 使用了错误的API |

## 四、三个闭环检测

### 4.1 用户流程闭环

**场景能力查看流程**:
```
用户 → 场景列表 → 场景详情 → 能力列表Tab → 能力详情
```

**问题**:
- 缺少从场景详情页跳转到能力列表的入口
- 能力详情页面缺少执行测试功能

### 4.2 数据闭环

**能力数据流**:
```
API → 前端组件 → 用户交互 → API更新
```

**问题**:
- 能力列表缺少分页功能
- 缺少能力状态实时刷新

### 4.3 页面动作事件闭环

**动作事件链**:
```
点击 → API调用 → 状态更新 → UI刷新
```

**问题**:
- 缺少加载状态指示器
- 缺少错误处理和重试机制
- 缺少操作确认弹窗

## 五、修复计划

### 5.1 高优先级修复

1. **修复脚本引用** - 所有页面使用标准脚本引用
2. **添加动态菜单** - 使用 `/api/v1/auth/menu-config` API
3. **添加登录检查** - 验证用户会话

### 5.2 中优先级修复

1. **添加搜索功能** - 能力名称搜索
2. **添加筛选功能** - 按类型筛选
3. **添加分页功能** - 列表分页

### 5.3 低优先级修复

1. **添加加载状态** - Loading 指示器
2. **添加错误处理** - 错误提示和重试
3. **添加操作确认** - 确认弹窗

## 六、知识图谱

```yaml
页面:
  scene-capabilities.html:
    用户故事: [US-CAP-001, US-CAP-002, US-CAP-003]
    API: [/api/v1/capabilities]
    元素: [能力列表, 搜索框, 筛选器]
    动作: [查看能力, 搜索能力, 筛选能力]
    
  scene-capability-detail.html:
    用户故事: [US-CAP-004]
    API: [/api/v1/capabilities/{id}]
    元素: [能力详情, 输入参数, 输出定义]
    动作: [查看详情, 执行测试]
    
  installed-scene-capabilities.html:
    用户故事: [US-CAP-001]
    API: [/api/v1/discovery/local]
    元素: [已安装能力列表]
    动作: [查看已安装能力]
```
