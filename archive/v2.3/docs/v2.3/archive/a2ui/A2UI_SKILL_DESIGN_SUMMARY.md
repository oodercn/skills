# A2UI Skill 设计方案汇总

> **版本**: v2.0  
> **最后更新**: 2026-02-27  
> **状态**: 已修正设计目标

---

## 一、设计目标（正确版本）

### 1.1 核心目标

A2UI Skill 的设计目标是将 **Nexus 中的前端模块采用 Skills 架构来完成**，提供一种具有 UI 功能的 Skills。

| 目标 | 描述 | 优先级 |
|------|------|--------|
| **Nexus UI 模块 Skills 化** | 将现有 120+ 个 Nexus HTML 页面转换为 Skills | P0 |
| **UI 能力化管理** | 实现技能配置中的 UI 模块能力管理 | P0 |
| **NLP 生成 UI** | 按照 nexus 架构规范 + apilist + template + script 定义，允许用户使用 NLP 生成 UI 界面 | P1 |
| **能力绑定** | 生成页面中相关组件能够和其他能力结合（字典表、业务属性、数据源、权限等） | P1 |

### 1.2 与错误目标的对比

| 维度 | ❌ 错误目标 | ✅ 正确目标 |
|------|------------|------------|
| **核心定位** | 从设计图生成前端代码 | Nexus UI 模块 Skills 化 |
| **输入来源** | Figma/Sketch 设计文件 | Nexus 现有 HTML 页面 + NLP 指令 |
| **输出产物** | React/Vue/Angular 代码 | Nexus-UI Skill 包 |
| **主要场景** | 设计师→开发者协作 | 用户自定义 Nexus 控制台页面 |
| **技术栈** | 多框架支持 | Nexus 架构规范 (HTML/CSS/JS) |

---

## 二、正确架构设计

### 2.1 架构概览

```
┌─────────────────────────────────────────────────────────────────┐
│                    A2UI Skill 正确架构                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Nexus 平台                            │   │
│  │  ┌─────────────────────────────────────────────────┐   │   │
│  │  │              Nexus-UI Skill 容器                 │   │   │
│  │  │  ┌─────────┐ ┌─────────┐ ┌─────────┐           │   │   │
│  │  │  │ 页面1   │ │ 页面2   │ │ 页面N   │           │   │   │
│  │  │  │(Skill) │ │(Skill) │ │(Skill) │           │   │   │
│  │  │  └─────────┘ └─────────┘ └─────────┘           │   │   │
│  │  └─────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ▲                                  │
│                              │ NLP 指令                         │
│                    ┌─────────┴──────────┐                      │
│                    │    A2UI Skill      │                      │
│                    │  (UI 能力管理)      │                      │
│                    └────────────────────┘                      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 核心能力定义

```java
public class A2UICapabilities {
    
    // P0: 页面生成能力
    public static final String GENERATE_PAGE = "a2ui.generate-page";
    
    // P1: 能力绑定
    public static final String BIND_DICT = "a2ui.bind-dict";           // 字典表绑定
    public static final String BIND_BIZ_ATTR = "a2ui.bind-bizattr";    // 业务属性绑定
    public static final String BIND_DATA_SOURCE = "a2ui.bind-datasource"; // 数据源绑定
    public static final String BIND_PERMISSION = "a2ui.bind-permission";  // 权限绑定
    
    // P0: 页面管理
    public static final String LIST_PAGES = "a2ui.list-pages";
    public static final String GET_PAGE = "a2ui.get-page";
    public static final String UPDATE_PAGE = "a2ui.update-page";
}
```

### 2.3 NLP 生成 UI 流程

```
用户 NLP 指令
     │
     ▼
┌─────────────────┐
│  NLP 解析引擎   │
│ (llm-sdk)      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  页面模板匹配   │
│ (template)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  能力绑定引擎   │
│ (字典表/属性)   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Nexus-UI Skill │
│  页面生成       │
└─────────────────┘
```

---

## 三、Nexus 页面清单（Skills 化目标）

### 3.1 页面分类统计

| 分类 | 页面数 | 优先级 | 示例页面 |
|------|--------|--------|----------|
| 系统管理 | 25 | P0 | 用户管理、角色管理、权限配置 |
| 组织管理 | 18 | P0 | 组织架构、人员管理、部门配置 |
| 消息中心 | 15 | P1 | 消息列表、通知配置、模板管理 |
| 文件管理 | 12 | P1 | 文件列表、上传下载、权限设置 |
| 配置中心 | 20 | P1 | 系统配置、参数管理、字典管理 |
| 监控面板 | 10 | P2 | 系统监控、日志查看、性能分析 |
| 其他 | 20+ | P2 | 自定义页面、扩展功能 |
| **总计** | **120+** | - | - |

### 3.2 首批转换页面（P0）

| 页面 ID | 页面名称 | 复杂度 | 预计工作量 |
|---------|----------|--------|------------|
| user-list | 用户列表 | 简单 | 0.5 天 |
| role-list | 角色列表 | 简单 | 0.5 天 |
| org-tree | 组织架构 | 中等 | 1 天 |
| dept-list | 部门列表 | 简单 | 0.5 天 |
| perm-config | 权限配置 | 复杂 | 2 天 |

---

## 四、技术实现方案

### 4.1 Nexus-UI Skill 结构

```
skill-{name}-nexus-ui/
├── skill.yaml                    # Skill 元数据
├── README.md                     # 说明文档（含 LLM 提示词模板）
├── ui/                           # 前端资源
│   ├── pages/                    # HTML 页面
│   │   ├── index.html           # 主页面
│   │   └── *.html               # 其他页面
│   ├── css/                     # 样式文件
│   ├── js/                      # 脚本文件
│   └── assets/                  # 静态资源
├── api/                          # API 定义
│   └── spec/openapi.yaml        # OpenAPI 规范
└── config/                       # 配置文件
    ├── menu.json                # 菜单配置
    └── routes.json              # 路由映射
```

### 4.2 skill.yaml 配置示例

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-user-management-nexus-ui
  name: 用户管理
  version: 1.0.0
  type: nexus-ui
  
spec:
  type: nexus-ui
  
  nexusUi:
    entry:
      page: index.html
      title: 用户管理
      icon: ri-user-line
      
    menu:
      position: sidebar
      category: system
      order: 10
      
    layout:
      type: default
      sidebar: true
      header: true
      
  # 能力绑定配置
  capabilities:
    - id: a2ui.bind-dict
      config:
        dictTypes:
          - user_status    # 用户状态字典
          - user_type      # 用户类型字典
          
    - id: a2ui.bind-permission
      config:
        permissions:
          - user:view
          - user:create
          - user:edit
          - user:delete
```

### 4.3 页面模板示例

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>用户管理</title>
    
    <!-- Nexus 核心资源 -->
    <link rel="stylesheet" href="https://gitee.com/ooderCN/nexus-assets/raw/main/css/nexus.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css">
</head>
<body>
    <div class="nx-page">
        <main class="nx-page__content">
            <header class="nx-page__header">
                <h1 class="nx-page__title">
                    <i class="ri-user-line"></i> 用户管理
                </h1>
            </header>
            
            <div class="nx-page__main">
                <div class="nx-container">
                    <!-- 字典表自动填充：用户状态 -->
                    <div class="nx-card">
                        <div class="nx-card__header">用户列表</div>
                        <div class="nx-card__body" id="user-list">
                            <!-- 动态加载 -->
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
    
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/nexus.js"></script>
    <script src="https://gitee.com/ooderCN/nexus-assets/raw/main/js/api.js"></script>
    <script src="../js/user-management.js"></script>
</body>
</html>
```

---

## 五、能力绑定机制

### 5.1 字典表绑定

```yaml
# skill.yaml 配置
capabilities:
  - id: a2ui.bind-dict
    config:
      dictTypes:
        - user_status     # 用户状态
        - user_type       # 用户类型
        - gender          # 性别
```

**效果**：页面中的下拉框、标签等自动填充字典数据。

### 5.2 业务属性绑定

```yaml
capabilities:
  - id: a2ui.bind-bizattr
    config:
      attributes:
        - code: USER_NAME
          type: text
          label: 用户名
          required: true
        - code: USER_EMAIL
          type: email
          label: 邮箱
          required: true
```

**效果**：表单字段自动生成，验证规则自动应用。

### 5.3 数据源绑定

```yaml
capabilities:
  - id: a2ui.bind-datasource
    config:
      datasource:
        type: api
        url: /api/v1/users
        method: GET
        pagination: true
```

**效果**：表格数据自动加载、分页、筛选。

### 5.4 权限绑定

```yaml
capabilities:
  - id: a2ui.bind-permission
    config:
      permissions:
        - code: user:view
          label: 查看用户
        - code: user:create
          label: 创建用户
        - code: user:edit
          label: 编辑用户
        - code: user:delete
          label: 删除用户
```

**效果**：按钮、操作根据权限自动显示/隐藏。

---

## 六、NLP 生成 UI

### 6.1 提示词模板

```markdown
请帮我创建一个 Nexus 控制台页面：

【架构规范】（必须严格遵守）
1. CDN 资源：
   - CSS: https://gitee.com/ooderCN/nexus-assets/raw/main/css/nexus.css
   - JS: https://gitee.com/ooderCN/nexus-assets/raw/main/js/nexus.js
   - Icon: https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css

2. 必须使用 Remix Icon (ri-* 前缀)
3. 必须使用 CSS 变量 (--ns-*)
4. 页面结构必须使用 nx-page 布局

【能力绑定】
- 字典表：user_status, user_type
- 数据源：/api/v1/users
- 权限：user:view, user:create, user:edit, user:delete

【页面功能】
- 用户列表展示（表格）
- 搜索筛选功能
- 新增/编辑用户（模态框）
- 删除用户确认

【输出】
完整的 HTML 文件
```

### 6.2 生成流程

```
1. 用户输入 NLP 指令
   └─> "创建一个用户管理页面，包含列表、搜索、新增、编辑、删除功能"

2. LLM 解析意图
   └─> 识别：页面类型=管理页面，功能=CRUD，实体=用户

3. 匹配页面模板
   └─> 选择：标准管理页面模板

4. 能力绑定
   └─> 自动绑定：字典表、数据源、权限

5. 生成 Nexus-UI Skill
   └─> 输出：skill-user-management-nexus-ui/
```

---

## 七、开发任务分解

### 7.1 Phase 1: 基础框架（1 周）

| 任务 ID | 任务名称 | 工作量 | 委派团队 |
|---------|----------|--------|----------|
| A2UI-001 | Nexus-UI Skill 容器实现 | 2 天 | Skills 团队 |
| A2UI-002 | 能力注册 (CapRegistry) | 1 天 | Skills 团队 |
| A2UI-003 | 页面模板系统 | 2 天 | Skills 团队 |

### 7.2 Phase 2: 能力绑定（1 周）

| 任务 ID | 任务名称 | 工作量 | 委派团队 |
|---------|----------|--------|----------|
| A2UI-004 | 字典表绑定实现 | 1.5 天 | Skills 团队 |
| A2UI-005 | 业务属性绑定实现 | 1.5 天 | Skills 团队 |
| A2UI-006 | 数据源绑定实现 | 1 天 | Skills 团队 |
| A2UI-007 | 权限绑定实现 | 1 天 | Skills 团队 |

### 7.3 Phase 3: NLP 生成（1 周）

| 任务 ID | 任务名称 | 工作量 | 委派团队 |
|---------|----------|--------|----------|
| A2UI-008 | NLP 解析引擎集成 | 2 天 | LLM-SDK 团队 |
| A2UI-009 | 页面生成服务 | 2 天 | Skills 团队 |
| A2UI-010 | 模板匹配引擎 | 1 天 | Skills 团队 |

### 7.4 Phase 4: 页面转换（2 周）

| 任务 ID | 任务名称 | 工作量 | 委派团队 |
|---------|----------|--------|----------|
| A2UI-011 | P0 页面转换（5 个） | 5 天 | Skills 团队 |
| A2UI-012 | P1 页面转换（10 个） | 5 天 | Skills 团队 |
| A2UI-013 | 测试与优化 | 2 天 | Skills 团队 |

---

## 八、相关文档索引

### 8.1 正确文档（保留）

| 文档 | 说明 |
|------|------|
| [A2UI_SKILL_CORRECTED_DESIGN.md](A2UI_SKILL_CORRECTED_DESIGN.md) | 修正后的设计目标 |
| [NEXUS_UI_SKILL_ARCHITECTURE.md](../NEXUS_UI_SKILL_ARCHITECTURE.md) | Nexus-UI Skill 类型规范 |
| [NEXUS_UI_SKILL_COMPLETE_SPEC.md](../NEXUS_UI_SKILL_COMPLETE_SPEC.md) | 完整技术规范 |

### 8.2 需要修正的文档

| 文档 | 问题 | 状态 |
|------|------|------|
| A2UI_SKILL_DEVELOPMENT_BREAKDOWN.md | 错误聚焦于 Figma→代码生成 | ⚠️ 待修正 |
| A2UI_SKILL_COMPLETE_ANALYSIS.md | 错误聚焦于设计工具集成 | ⚠️ 待修正 |
| SDK_2.3_A2UI_COMPARISON.md | 基于错误目标对比 | ⚠️ 待修正 |

---

## 九、总结

### 9.1 设计目标修正

| 维度 | 修正前 | 修正后 |
|------|--------|--------|
| **核心定位** | 设计图→代码生成 | Nexus UI 模块 Skills 化 |
| **主要输入** | Figma/Sketch 文件 | NLP 指令 + 现有页面 |
| **主要输出** | React/Vue 代码 | Nexus-UI Skill 包 |
| **核心能力** | 设计分析、代码生成 | 能力绑定、NLP 生成 |

### 9.2 关键要点

1. ✅ **目标明确**：将 Nexus 现有 120+ 页面转换为 Skills
2. ✅ **能力驱动**：通过能力绑定实现字典表、业务属性、数据源、权限自动填充
3. ✅ **NLP 增强**：支持用户通过自然语言生成/调整页面
4. ✅ **架构规范**：严格遵循 Nexus 架构规范（HTML/CSS/JS + CDN）

---

**文档版本**: v2.0  
**创建日期**: 2026-02-27  
**最后更新**: 2026-02-27
