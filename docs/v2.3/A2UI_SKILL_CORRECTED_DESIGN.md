# A2UI Skill 设计目标修正与完整方案

## 一、设计目标修正

### 1.1 错误理解（之前的设计）

| 错误目标 | 说明 |
|----------|------|
| ❌ 从设计图生成前端代码 | React/Vue/Angular 代码生成 |
| ❌ Figma/Sketch 集成 | 设计工具 MCP 集成 |
| ❌ 组件库适配 | Ant Design/Element 适配 |

### 1.2 正确目标（实际需求）

| 正确目标 | 说明 |
|----------|------|
| ✅ **Nexus UI 模块 Skills 化** | 将现有 Nexus 前端模块采用 Skills 架构重构 |
| ✅ **UI 能力化管理** | 实现 UI 模块的能力化配置和技能化封装 |
| ✅ **NLP 生成 UI 界面** | 允许用户使用自然语言生成和动态调整页面 |
| ✅ **能力结合** | 页面组件与字典表、业务属性等自动填充结合 |

---

## 二、核心设计目标

### 2.1 目标一：Nexus UI 模块 Skills 化

将现有 Nexus 中的 120+ 个 HTML 页面转换为 Skills 架构：

```
现有架构：
Nexus/
├── src/main/resources/static/console/pages/
│   ├── admin/         # 管理后台 (6 pages)
│   ├── agent/         # 代理管理 (5 pages)
│   ├── nexus/         # Nexus核心 (13 pages)
│   └── ...            # 其他页面

目标架构：
skills/
├── skill-nexus-admin/       # 管理后台 Skill
├── skill-nexus-agent/       # 代理管理 Skill
├── skill-nexus-core/        # Nexus 核心 Skill
└── skill-nexus-custom/      # 用户自定义 Skill
```

### 2.2 目标二：UI 能力化管理

每个 UI Skill 定义清晰的能力：

```yaml
# skill-nexus-agent.yaml
spec:
  capabilities:
    - id: agent.list
      name: 代理列表
      description: 显示和管理代理列表
      type: ui-page
      
    - id: agent.detail
      name: 代理详情
      description: 查看代理详细信息
      type: ui-page
      
    - id: agent.config
      name: 代理配置
      description: 配置代理参数
      type: ui-form
```

### 2.3 目标三：NLP 生成 UI 界面

用户通过自然语言描述生成页面：

```
用户输入：
"创建一个用户管理页面，包含用户列表、搜索、新增、编辑功能"

系统输出：
1. 生成页面模板 (HTML/CSS/JS)
2. 注册能力到 CapRegistry
3. 添加菜单配置
4. 绑定 API 端点
```

### 2.4 目标四：能力结合

页面组件自动与其他能力结合：

```
┌─────────────────────────────────────────────────────────────────┐
│                    能力结合架构                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  UI 页面组件                                                    │
│      │                                                          │
│      ├── 字典表能力 → 自动填充下拉选项                          │
│      │       └── skill-dict.getDictItems("user_status")        │
│      │                                                          │
│      ├── 业务属性能力 → 自动填充表单字段                        │
│      │       └── skill-biz.getAttribute("user", "role")        │
│      │                                                          │
│      ├── 数据源能力 → 自动加载数据                              │
│      │       └── skill-data.query("users", filters)            │
│      │                                                          │
│      └── 权限能力 → 自动控制按钮显示                            │
│              └── skill-auth.checkPermission("user:edit")       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 三、技术架构

### 3.1 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                    A2UI Skill 技术架构                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    NLP 理解层                            │   │
│  │  ├── 意图识别：识别用户想创建什么类型的页面              │   │
│  │  ├── 实体提取：提取页面名称、字段、功能等                │   │
│  │  └── 模板匹配：匹配最合适的页面模板                      │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│  ┌───────────────────────────┴───────────────────────────┐    │
│  │                    页面生成层                           │    │
│  │  ├── 模板引擎：基于模板生成 HTML/CSS/JS                │    │
│  │  ├── 组件组装：组装 UI 组件                            │    │
│  │  └── 能力绑定：绑定字典表、数据源等能力                │    │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│  ┌───────────────────────────┴───────────────────────────┐    │
│  │                    能力集成层                           │    │
│  │  ├── 字典表能力：dict-skill                            │    │
│  │  ├── 业务属性能力：biz-attr-skill                      │    │
│  │  ├── 数据源能力：data-source-skill                     │    │
│  │  └── 权限能力：auth-skill                              │    │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│  ┌───────────────────────────┴───────────────────────────┐    │
│  │                    SDK 集成层                           │    │
│  │  ├── agent-sdk-core: CapRegistry, A2A 消息            │    │
│  │  ├── scene-engine: Scene 管理, Workflow               │    │
│  │  └── llm-sdk: NLP 理解, 代码生成                      │    │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 页面定义规范

按照 Nexus 架构规范 + apilist (json) + template + script 定义：

```
skill-nexus-{name}/
├── skill.yaml                    # Skill 元数据
├── api/                          # API 定义
│   └── apilist.json             # API 列表 (JSON 格式)
├── ui/                          # UI 资源
│   ├── templates/               # 页面模板
│   │   ├── list.html           # 列表页模板
│   │   ├── form.html           # 表单页模板
│   │   └── detail.html         # 详情页模板
│   ├── pages/                   # 生成的页面
│   ├── css/                     # 样式
│   └── js/                      # 脚本
├── scripts/                     # 生成脚本
│   ├── generator.js            # 页面生成器
│   └── binder.js               # 能力绑定器
└── config/                      # 配置
    ├── menu.json               # 菜单配置
    └── capabilities.json       # 能力定义
```

### 3.3 API 列表定义 (apilist.json)

```json
{
  "apiVersion": "skill.ooder.net/v1",
  "kind": "ApiList",
  "metadata": {
    "skillId": "skill-nexus-user",
    "version": "1.0.0"
  },
  "spec": {
    "apis": [
      {
        "id": "user.list",
        "path": "/api/users",
        "method": "GET",
        "description": "获取用户列表",
        "response": {
          "type": "object",
          "properties": {
            "status": { "type": "string" },
            "data": {
              "type": "object",
              "properties": {
                "items": { "type": "array" },
                "total": { "type": "integer" }
              }
            }
          }
        }
      },
      {
        "id": "user.create",
        "path": "/api/users",
        "method": "POST",
        "description": "创建用户",
        "request": {
          "type": "object",
          "properties": {
            "name": { "type": "string" },
            "email": { "type": "string" },
            "role": { "type": "string", "dict": "user_role" }
          }
        }
      }
    ]
  }
}
```

### 3.4 能力绑定配置

```json
{
  "bindings": [
    {
      "component": "select#role",
      "capability": "dict.getDictItems",
      "params": { "dictCode": "user_role" }
    },
    {
      "component": "select#status",
      "capability": "dict.getDictItems",
      "params": { "dictCode": "user_status" }
    },
    {
      "component": "select#department",
      "capability": "bizAttr.getOptions",
      "params": { "entity": "department", "attr": "name" }
    },
    {
      "component": "button#edit",
      "capability": "auth.checkPermission",
      "params": { "permission": "user:edit" }
    }
  ]
}
```

---

## 四、NLP 生成 UI 流程

### 4.1 用户输入示例

```
用户输入：
"创建一个设备管理页面，包含：
 1. 设备列表，显示设备名称、IP地址、状态、所属区域
 2. 搜索功能，支持按名称和IP搜索
 3. 新增设备按钮
 4. 编辑和删除操作
 5. 状态使用字典表 user_status"
```

### 4.2 系统处理流程

```
Step 1: NLP 理解
├── 意图识别：创建列表管理页面
├── 实体提取：
│   ├── 页面名称：设备管理
│   ├── 字段：设备名称、IP地址、状态、所属区域
│   ├── 功能：搜索、新增、编辑、删除
│   └── 字典：user_status
└── 模板匹配：list-management-template

Step 2: 页面生成
├── 选择模板：list.html
├── 替换变量：
│   ├── {{page.title}} → 设备管理
│   ├── {{columns}} → [设备名称, IP地址, 状态, 所属区域]
│   └── {{actions}} → [新增, 编辑, 删除]
└── 生成文件：device-list.html

Step 3: 能力绑定
├── 字典绑定：status → dict.getDictItems("user_status")
├── 数据源绑定：列表 → api.device.list
├── 权限绑定：编辑按钮 → auth.checkPermission("device:edit")
└── 生成配置：bindings.json

Step 4: 注册能力
├── 注册能力：device.list, device.create, device.update, device.delete
├── 注册菜单：设备管理 → /pages/device-list.html
└── 注册路由：/api/devices → skill-nexus-device
```

### 4.3 生成的页面代码

```html
<!-- device-list.html -->
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>设备管理</title>
    <link rel="stylesheet" href="https://gitee.com/ooderCN/nexus-assets/raw/main/css/nexus.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css">
</head>
<body>
    <div class="nx-page">
        <aside class="nx-page__sidebar" id="sidebar"></aside>
        <main class="nx-page__content">
            <header class="nx-page__header">
                <h1 class="nx-page__title">
                    <i class="ri-device-line"></i> 设备管理
                </h1>
            </header>
            <div class="nx-page__main">
                <div class="nx-container">
                    <!-- 搜索区域 -->
                    <div class="nx-card nx-mb-4">
                        <div class="nx-card__body">
                            <div class="nx-form-inline">
                                <div class="nx-form-group">
                                    <label>设备名称</label>
                                    <input type="text" class="nx-input" id="search-name">
                                </div>
                                <div class="nx-form-group">
                                    <label>IP地址</label>
                                    <input type="text" class="nx-input" id="search-ip">
                                </div>
                                <button class="nx-btn nx-btn--primary" onclick="searchDevices()">
                                    <i class="ri-search-line"></i> 搜索
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <!-- 操作按钮 -->
                    <div class="nx-mb-4">
                        <button class="nx-btn nx-btn--primary" onclick="openAddModal()">
                            <i class="ri-add-line"></i> 新增设备
                        </button>
                    </div>
                    
                    <!-- 数据表格 -->
                    <div class="nx-card">
                        <div class="nx-card__body">
                            <table class="nx-table" id="device-table">
                                <thead>
                                    <tr>
                                        <th>设备名称</th>
                                        <th>IP地址</th>
                                        <th>状态</th>
                                        <th>所属区域</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody id="device-list"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
    
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/nexus.js"></script>
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/api.js"></script>
    <script>
    // 能力绑定配置
    const BINDINGS = {
        status: { capability: 'dict.getDictItems', params: { dictCode: 'user_status' } },
        editBtn: { capability: 'auth.checkPermission', params: { permission: 'device:edit' } },
        deleteBtn: { capability: 'auth.checkPermission', params: { permission: 'device:delete' } }
    };
    
    // 加载字典数据
    async function loadDictData() {
        const statusOptions = await SkillApi.call(BINDINGS.status.capability, BINDINGS.status.params);
        // 渲染状态选项...
    }
    
    // 加载设备列表
    async function loadDevices() {
        const response = await Nexus.api.get('/api/devices');
        if (response.status === 'success') {
            renderDeviceList(response.data.items);
        }
    }
    
    // 初始化
    document.addEventListener('DOMContentLoaded', async () => {
        await Nexus.init();
        await loadDictData();
        await loadDevices();
    });
    </script>
</body>
</html>
```

---

## 五、与现有 Nexus 页面的集成

### 5.1 现有页面分析

| 页面类型 | 数量 | 转换难度 | 转换方式 |
|----------|------|----------|----------|
| 简单列表页 | 25 | ⭐ 低 | 自动转换 |
| 表单编辑页 | 15 | ⭐ 低 | 自动转换 |
| 仪表盘页 | 8 | ⭐⭐ 中 | 半自动转换 |
| 复杂管理页 | 35 | ⭐⭐ 中 | 半自动转换 |
| 可视化页 | 12 | ⭐⭐⭐ 高 | 手动重构 |
| IM/协作页 | 8 | ⭐⭐⭐ 高 | 手动重构 |

### 5.2 转换策略

```
Phase 1: 自动转换工具开发 (2周)
├── 开发页面解析器
├── 开发模板匹配器
├── 开发能力绑定器
└── 开发批量转换脚本

Phase 2: 简单页面转换 (3周)
├── 转换 25 个简单列表页
├── 转换 15 个表单编辑页
└── 验证功能完整性

Phase 3: 复杂页面转换 (4周)
├── 转换 35 个复杂管理页
├── 转换 8 个仪表盘页
└── 集成图表组件

Phase 4: 特殊页面重构 (4周)
├── 重构 12 个可视化页
├── 重构 8 个 IM/协作页
└── 集成特殊组件
```

---

## 六、核心能力定义

### 6.1 UI 生成能力

| 能力 ID | 能力名称 | 描述 |
|---------|----------|------|
| a2ui.generate-page | 生成页面 | 根据描述生成完整页面 |
| a2ui.generate-form | 生成表单 | 根据字段定义生成表单 |
| a2ui.generate-list | 生成列表 | 根据配置生成数据列表 |
| a2ui.modify-page | 修改页面 | 动态调整页面布局和组件 |

### 6.2 能力集成能力

| 能力 ID | 能力名称 | 描述 |
|---------|----------|------|
| a2ui.bind-dict | 绑定字典 | 组件绑定字典表数据源 |
| a2ui.bind-bizattr | 绑定业务属性 | 组件绑定业务属性 |
| a2ui.bind-datasource | 绑定数据源 | 组件绑定数据 API |
| a2ui.bind-permission | 绑定权限 | 组件绑定权限控制 |

### 6.3 模板管理能力

| 能力 ID | 能力名称 | 描述 |
|---------|----------|------|
| a2ui.list-templates | 列出模板 | 获取可用模板列表 |
| a2ui.create-template | 创建模板 | 创建新的页面模板 |
| a2ui.update-template | 更新模板 | 更新现有模板 |

---

## 七、任务分解

### 7.1 Phase 1: 基础框架（2周）

| 任务ID | 任务名称 | 工作量 | 委派团队 |
|--------|----------|--------|----------|
| A2UI-001 | A2UI Skill 项目初始化 | 2天 | Skills 团队 |
| A2UI-002 | 页面模板定义 | 3天 | Skills 团队 |
| A2UI-003 | 能力绑定框架 | 3天 | SDK 团队 |
| A2UI-004 | NLP 理解模块 | 4天 | LLM-SDK 团队 |

### 7.2 Phase 2: 核心功能（3周）

| 任务ID | 任务名称 | 工作量 | 委派团队 |
|--------|----------|--------|----------|
| A2UI-005 | 页面生成器 | 5天 | Skills 团队 |
| A2UI-006 | 字典表能力集成 | 3天 | SDK 团队 |
| A2UI-007 | 业务属性能力集成 | 3天 | SDK 团队 |
| A2UI-008 | 数据源能力集成 | 4天 | SceneEngine 团队 |

### 7.3 Phase 3: 页面转换（4周）

| 任务ID | 任务名称 | 工作量 | 委派团队 |
|--------|----------|--------|----------|
| A2UI-009 | 简单页面转换工具 | 5天 | Skills 团队 |
| A2UI-010 | 批量转换脚本 | 3天 | Skills 团队 |
| A2UI-011 | 40个简单页面转换 | 10天 | Skills 团队 |
| A2UI-012 | 功能验证 | 4天 | 测试团队 |

### 7.4 Phase 4: 高级功能（3周）

| 任务ID | 任务名称 | 工作量 | 委派团队 |
|--------|----------|--------|----------|
| A2UI-013 | 复杂页面转换 | 8天 | Skills 团队 |
| A2UI-014 | 图表组件集成 | 4天 | SceneEngine 团队 |
| A2UI-015 | 权限能力集成 | 3天 | SDK 团队 |

---

## 八、总结

### 8.1 设计目标对比

| 维度 | 之前错误设计 | 正确设计 |
|------|-------------|----------|
| **核心目标** | 从设计图生成代码 | Nexus UI 模块 Skills 化 |
| **用户群体** | 设计师 | Nexus 用户/开发者 |
| **技术重点** | Figma MCP, 代码生成 | NLP 理解, 能力绑定 |
| **输出产物** | React/Vue 代码 | Nexus 页面 Skill |
| **集成方式** | 设计工具集成 | 字典表/业务属性集成 |

### 8.2 关键差异

1. **目标用户不同**：从设计师变为 Nexus 用户
2. **技术重点不同**：从设计工具集成变为 NLP + 能力绑定
3. **输出产物不同**：从框架代码变为 Nexus 页面 Skill
4. **集成方式不同**：从 MCP 协议变为能力调用

### 8.3 下一步行动

1. **更新所有设计文档**：修正设计目标
2. **重新评估工作量**：基于正确目标重新估算
3. **调整团队分工**：重新分配任务
4. **启动 Phase 1**：开始基础框架开发

---

**文档版本**：v2.0（修正版）  
**创建日期**：2026-02-27  
**最后更新**：2026-02-27
