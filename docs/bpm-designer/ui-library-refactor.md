# UI 库重构方案

**文档版本**: v1.0  
**创建日期**: 2026-04-06  
**基于架构**: nexus + Spring Boot  
**项目路径**: E:\github\ooder-skills

---

## 目录

1. [重构目标](#1-重构目标)
2. [现有架构分析](#2-现有架构分析)
3. [重构策略](#3-重构策略)
4. [组件清单](#4-组件清单)
5. [实施计划](#5-实施计划)

---

## 1. 重构目标

### 1.1 核心目标

| 目标 | 说明 |
|------|------|
| **架构一致性** | 与现有 nexus 控制台保持技术栈一致 |
| **组件复用** | 提取可复用的 UI 组件，供 BPM 设计器使用 |
| **渐进增强** | 先实现核心组件，后期推出真正 A2UI 架构 |
| **简洁实用** | 不做太复杂，满足当前需求即可 |

### 1.2 重构原则

| 原则 | 说明 |
|------|------|
| **最小改动** | 尽量复用现有 CSS，避免大规模重写 |
| **模块化** | 组件独立，按需加载 |
| **主题兼容** | 支持深色/浅色主题切换 |
| **响应式** | 支持不同屏幕尺寸 |

---

## 2. 现有架构分析

### 2.1 nexus CSS 模块结构

```
E:\github\ooder-skills\temp\ooder-Nexus\src\main\resources\static\console\
├── css/
│   ├── theme.css              # 主题变量定义
│   ├── main.css               # 主样式入口
│   ├── nexus.css              # nexus 组件样式
│   ├── styles.css             # 通用样式
│   ├── index.css              # 首页样式
│   ├── dashboard.css          # 仪表盘样式
│   ├── remixicon/
│   │   └── remixicon.css      # 图标库
│   ├── modules/
│   │   ├── base/
│   │   │   └── _base.css      # 基础样式
│   │   ├── layout/
│   │   │   └── _layout.css    # 布局样式
│   │   ├── components/
│   │   │   └── _components.css # 组件样式
│   │   ├── features/
│   │   │   └── _features.css  # 功能样式
│   │   └── responsive/
│   │       └── _responsive.css # 响应式样式
│   └── pages/                 # 页面专用样式
│       └── *.css
```

### 2.2 现有 CSS 变量

```css
:root {
    --nx-primary: #1a73e8;
    --nx-primary-hover: #2563eb;
    --ns-dark: #f0f0f0;
    --ns-secondary: #9aa0a6;
    --ns-card-bg: #1e1e1e;
    --ns-border: #3c4043;
    --ns-background: #121212;
    --ns-input-bg: #2d2d2d;
    --ns-success: #22c55e;
    --ns-danger: #ef4444;
    --ns-warning: #f59e0b;
    --ns-radius: 6px;
    --ns-shadow: 0 2px 4px rgba(0,0,0,0.3);
    --ns-bg-hover: rgba(255,255,255,0.05);
}
```

### 2.3 现有 JS 模块

```
js/
├── common.js                  # 公共工具函数
├── api.js                     # API 封装
├── nexus.js                   # nexus 主模块
├── theme-manager.js           # 主题管理
├── menu-loader.js             # 菜单加载
├── ui.js                      # UI 工具
├── nexus/
│   ├── api.js                 # nexus API
│   ├── base.js                # 基础模块
│   ├── common.js              # 公共模块
│   ├── config-management.js   # 配置管理
│   ├── endroute.js            # 终端路由
│   ├── link-management.js     # 链路管理
│   ├── p2p-management.js      # P2P 管理
│   ├── security-management.js # 安全管理
│   ├── ui.js                  # nexus UI
│   ├── utils.js               # 工具函数
│   └── sdk/
│       ├── INexusSDK.js       # SDK 接口
│       ├── NexusSDK.js        # SDK 工厂
│       ├── NexusRealSDK.js    # 真实 SDK
│       ├── NexusMockSDK.js    # 模拟 SDK
│       └── NexusMockData.js   # 模拟数据
└── common/
    ├── api-client.js          # API 客户端
    ├── button-manager.js      # 按钮管理
    ├── form-manager.js        # 表单管理
    ├── list-manager.js        # 列表管理
    ├── modal-manager.js       # 模态框管理
    └── utils.js               # 工具函数
```

---

## 3. 重构策略

### 3.1 重构方向

```
┌─────────────────────────────────────────────────────────────┐
│                    UI 库重构策略                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   现有 nexus CSS 模块                                       │
│        │                                                    │
│        ▼                                                    │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                 提取核心组件                          │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │ 布局组件 │ │ 表单组件 │ │ 数据组件 │ │ 反馈组件 │   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   └─────────────────────────────────────────────────────┘  │
│        │                                                    │
│        ▼                                                    │
│   ┌─────────────────────────────────────────────────────┐  │
│   │                 BPM 设计器专用组件                    │  │
│   │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │  │
│   │  │ 画布组件 │ │ 面板组件 │ │ 节点组件 │ │ 工具栏   │   │  │
│   │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │  │
│   └─────────────────────────────────────────────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 组件分层

| 层级 | 组件类型 | 来源 | 说明 |
|------|----------|------|------|
| **基础层** | CSS 变量、主题 | 复用 nexus | 直接复用 |
| **核心层** | 布局、表单、按钮 | 提取 nexus | 提取封装 |
| **扩展层** | 面板、表格、树 | 扩展开发 | 基于 nexus 风格 |
| **专用层** | 画布、节点、连线 | 新增开发 | BPM 专用 |

---

## 4. 组件清单

### 4.1 基础组件（复用 nexus）

| 组件 | CSS 类 | 说明 | 状态 |
|------|--------|------|------|
| **按钮** | `.btn`, `.btn-primary`, `.btn-secondary` | 基础按钮 | ✅ 已有 |
| **卡片** | `.card`, `.card-header`, `.card-title` | 卡片容器 | ✅ 已有 |
| **表单** | `.form-group`, `.form-input`, `.form-select` | 表单元素 | ✅ 已有 |
| **表格** | `.table-container`, `table`, `th`, `td` | 数据表格 | ✅ 已有 |
| **提示** | `.alert`, `.alert-success`, `.alert-error` | 提示框 | ✅ 已有 |
| **进度条** | `.progress-bar`, `.progress-fill` | 进度显示 | ✅ 已有 |

### 4.2 扩展组件（基于 nexus 风格）

| 组件 | CSS 类前缀 | 说明 | 优先级 |
|------|------------|------|--------|
| **标签页** | `.bpm-tabs` | 选项卡切换 | 高 |
| **折叠面板** | `.bpm-collapse` | 可折叠内容 | 高 |
| **树形控件** | `.bpm-tree` | 树形结构 | 中 |
| **下拉菜单** | `.bpm-dropdown` | 下拉选择 | 中 |
| **标签输入** | `.bpm-tags` | 标签输入 | 中 |
| **滑块** | `.bpm-slider` | 数值滑块 | 低 |

### 4.3 BPM 专用组件（新增）

| 组件 | CSS 类前缀 | 说明 | 优先级 |
|------|------------|------|--------|
| **设计器布局** | `.bpm-designer` | 设计器整体布局 | 高 |
| **组件库** | `.bpm-palette` | 拖拽组件库 | 高 |
| **画布** | `.bpm-canvas` | 流程画布 | 高 |
| **属性面板** | `.bpm-panel` | 属性编辑面板 | 高 |
| **节点** | `.bpm-node` | 流程节点 | 高 |
| **连线** | `.bpm-edge` | 流程连线 | 高 |
| **工具栏** | `.bpm-toolbar` | 操作工具栏 | 中 |
| **右键菜单** | `.bpm-context-menu` | 右键菜单 | 中 |
| **小地图** | `.bpm-minimap` | 画布缩略图 | 低 |

---

## 5. 实施计划

### 5.1 阶段划分

| 阶段 | 内容 | 周期 |
|------|------|------|
| **第一阶段** | 基础组件复用 | 1天 |
| **第二阶段** | 扩展组件开发 | 2天 |
| **第三阶段** | BPM 专用组件 | 3天 |
| **第四阶段** | 组件文档 | 1天 |

### 5.2 详细计划

#### 第一阶段：基础组件复用（1天）

| 任务 | 说明 | 交付物 |
|------|------|--------|
| CSS 变量提取 | 提取 nexus CSS 变量 | `bpm-variables.css` |
| 基础样式复用 | 复用按钮、卡片、表单样式 | `bpm-base.css` |
| 主题系统集成 | 集成 nexus 主题切换 | `bpm-theme.js` |

#### 第二阶段：扩展组件开发（2天）

| 任务 | 说明 | 交付物 |
|------|------|--------|
| 标签页组件 | Tab 切换组件 | `bpm-tabs.css/js` |
| 折叠面板组件 | Collapse 组件 | `bpm-collapse.css/js` |
| 树形控件 | Tree 组件 | `bpm-tree.css/js` |
| 下拉菜单 | Dropdown 组件 | `bpm-dropdown.css/js` |

#### 第三阶段：BPM 专用组件（3天）

| 任务 | 说明 | 交付物 |
|------|------|--------|
| 设计器布局 | 设计器整体布局 | `bpm-designer.css` |
| 组件库 | Palette 组件 | `bpm-palette.css/js` |
| 属性面板 | Panel 组件 | `bpm-panel.css/js` |
| 节点样式 | Node 组件 | `bpm-node.css` |
| 工具栏 | Toolbar 组件 | `bpm-toolbar.css/js` |

#### 第四阶段：组件文档（1天）

| 任务 | 说明 | 交付物 |
|------|------|--------|
| 组件示例 | 每个组件的示例页面 | `components-demo.html` |
| 使用文档 | 组件使用说明 | `COMPONENTS.md` |

---

## 附录

### A. 文件结构

```
bpm-designer/
├── css/
│   ├── bpm-variables.css      # CSS 变量
│   ├── bpm-base.css           # 基础样式
│   ├── bpm-components.css     # 扩展组件
│   ├── bpm-designer.css       # 设计器专用
│   ├── bpm-canvas.css         # 画布样式
│   ├── bpm-panel.css          # 面板样式
│   ├── bpm-palette.css        # 组件库样式
│   ├── bpm-node.css           # 节点样式
│   └── bpm-toolbar.css        # 工具栏样式
├── js/
│   ├── bpm-components/
│   │   ├── tabs.js            # 标签页
│   │   ├── collapse.js        # 折叠面板
│   │   ├── tree.js            # 树形控件
│   │   ├── dropdown.js        # 下拉菜单
│   │   └── tags.js            # 标签输入
│   └── bpm-designer/
│       ├── designer.js        # 设计器主逻辑
│       ├── canvas.js          # 画布组件
│       ├── palette.js         # 组件库
│       ├── panel.js           # 属性面板
│       └── toolbar.js         # 工具栏
└── pages/
    └── bpm/
        ├── components-demo.html  # 组件示例
        └── designer.html         # 设计器页面
```

### B. CSS 变量扩展

```css
:root {
    --nx-primary: #1a73e8;
    --nx-primary-hover: #2563eb;
    --nx-primary-light: rgba(26, 115, 232, 0.1);
    
    --ns-dark: #f0f0f0;
    --ns-secondary: #9aa0a6;
    --ns-card-bg: #1e1e1e;
    --ns-border: #3c4043;
    --ns-background: #121212;
    --ns-input-bg: #2d2d2d;
    
    --ns-success: #22c55e;
    --ns-danger: #ef4444;
    --ns-warning: #f59e0b;
    --ns-info: #3b82f6;
    
    --ns-radius: 6px;
    --ns-radius-lg: 8px;
    --ns-radius-sm: 4px;
    
    --ns-shadow: 0 2px 4px rgba(0,0,0,0.3);
    --ns-shadow-lg: 0 4px 8px rgba(0,0,0,0.4);
    
    --ns-bg-hover: rgba(255,255,255,0.05);
    --ns-bg-active: rgba(255,255,255,0.1);
    
    --bpm-node-width: 100px;
    --bpm-node-height: 80px;
    --bpm-panel-width: 360px;
    --bpm-palette-width: 240px;
    --bpm-toolbar-height: 48px;
}
```

### C. 参考资源

| 资源 | 路径 | 说明 |
|------|------|------|
| nexus CSS 模块 | E:\github\ooder-skills\temp\ooder-Nexus\src\main\resources\static\console\css\modules\ | CSS 模块化参考 |
| nexus SDK 封装 | E:\github\ooder-skills\temp\ooder-Nexus\src\main\resources\static\console\js\nexus\sdk\ | SDK 设计模式 |
| 场景定义页面 | E:\github\ooder-skills\temp\ooder-Nexus\src\main\resources\static\console\pages\scene\scene-definition.html | 页面结构参考 |

---

**文档路径**: `E:\github\ooder-skills\docs\bpm-designer\ui-library-refactor.md`
