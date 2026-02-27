# Nexus-UI Skill 规范

> **版本**: v1.0  
> **日期**: 2026-02-27

---

## 一、skill.yaml 规范

### 1.1 基础结构

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-{name}-nexus-ui      # 必需: Skill ID，格式: skill-{name}-nexus-ui
  name: 技能名称                   # 必需: 显示名称
  version: 1.0.0                  # 必需: 版本号
  description: 技能描述            # 可选: 描述
  author: ooder Team              # 可选: 作者
  type: nexus-ui                  # 必需: 类型固定为 nexus-ui

spec:
  type: nexus-ui                  # 必需: 类型固定为 nexus-ui
  
  nexusUi:                        # 必需: Nexus-UI 配置
    entry:                        # 入口配置
      page: index.html            # 必需: 入口页面
      title: 页面标题              # 必需: 页面标题
      icon: ri-dashboard-line     # 必需: 图标 (Remix Icon)
      
    menu:                         # 菜单配置
      position: sidebar           # sidebar | header | dropdown
      category: system            # 菜单分类
      order: 100                  # 排序权重
      
    layout:                       # 布局配置
      type: default               # default | fullscreen | embedded
      sidebar: true               # 是否显示侧边栏
      header: true                # 是否显示头部
      
  capabilities:                   # 可选: 能力列表
    - id: {name}-view
      name: 查看功能
      description: 描述
      category: ui
      
  apis:                           # 可选: API 端点
    - path: /api/{name}/list
      method: GET
      description: 获取列表
```

### 1.2 完整示例

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-nexus-dashboard-nexus-ui
  name: Nexus仪表盘
  version: 1.0.0
  description: Nexus系统仪表盘，显示终端代理、网络链路、命令执行等统计信息
  author: ooder Team
  type: nexus-ui

spec:
  type: nexus-ui
  
  nexusUi:
    entry:
      page: index.html
      title: Nexus仪表盘
      icon: ri-dashboard-line
      
    menu:
      position: sidebar
      category: nexus
      order: 10
      
    layout:
      type: default
      sidebar: true
      header: true
      
  capabilities:
    - id: nexus-dashboard-view
      name: 查看仪表盘
      description: 查看Nexus系统仪表盘
      category: ui
      
  apis:
    - path: /api/nexus/stats
      method: GET
      description: 获取统计数据
```

---

## 二、目录结构

### 2.1 标准结构

```
skill-{name}-nexus-ui/
├── skill.yaml                    # Skill 元数据
├── README.md                     # 说明文档
└── ui/                           # 前端资源
    ├── pages/                    # HTML 页面
    │   └── index.html           # 主页面（必需）
    ├── css/                     # 自定义样式（可选）
    └── js/                      # 自定义脚本（可选）
```

### 2.2 多页面结构

```
skill-{name}-nexus-ui/
├── skill.yaml
├── README.md
└── ui/
    ├── pages/
    │   ├── index.html           # 主页面
    │   ├── list.html            # 列表页面
    │   └── detail.html          # 详情页面
    ├── css/
    │   └── style.css
    └── js/
        └── app.js
```

---

## 三、页面模板

### 3.1 标准 HTML 模板

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{{skill.name}} - Nexus Console</title>
    
    <!-- Nexus 核心样式 -->
    <link rel="stylesheet" href="/console/css/remixicon/remixicon.css">
    <link rel="stylesheet" href="/console/css/nexus.css">
    
    <!-- Skill 自定义样式（可选） -->
    <link rel="stylesheet" href="../css/style.css">
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
                    <i class="{{skill.icon}}"></i>
                    {{skill.name}}
                </h1>
            </header>
            
            <div class="nx-page__main">
                <div class="nx-container">
                    <!-- Skill 内容 -->
                    <div id="app"></div>
                </div>
            </div>
        </main>
    </div>
    
    <!-- Nexus 核心脚本 -->
    <script src="/console/js/nexus.js"></script>
    <script src="/console/js/menu-loader.js"></script>
    <script src="/console/js/page-init.js"></script>
    
    <!-- Skill 自定义脚本 -->
    <script src="../js/app.js"></script>
</body>
</html>
```

---

## 四、菜单配置

### 4.1 菜单位置

| 位置 | 说明 |
|------|------|
| sidebar | 侧边栏菜单（默认） |
| header | 顶部菜单 |
| dropdown | 下拉菜单 |

### 4.2 菜单分类

| 分类 | 说明 |
|------|------|
| nexus | Nexus 管理 |
| system | 系统管理 |
| network | 网络管理 |
| storage | 存储管理 |
| security | 安全管理 |
| personal | 个人中心 |

### 4.3 图标规范

使用 Remix Icon，格式：`ri-{icon-name}-line` 或 `ri-{icon-name}-fill`

常用图标：
- `ri-dashboard-line` - 仪表盘
- `ri-server-line` - 服务器
- `ri-user-line` - 用户
- `ri-settings-line` - 设置
- `ri-list-check` - 列表

---

## 五、访问路径

### 5.1 URL 格式

```
/console/skills/{skill-id}/pages/{page}
```

### 5.2 示例

| Skill ID | 页面 | URL |
|----------|------|-----|
| skill-nexus-dashboard-nexus-ui | index.html | /console/skills/skill-nexus-dashboard-nexus-ui/pages/index.html |
| skill-nexus-dashboard-nexus-ui | detail.html | /console/skills/skill-nexus-dashboard-nexus-ui/pages/detail.html |

---

## 六、与 SDK 集成

### 6.1 发现流程

1. `SkillDiscoveryService` 扫描 `skills/` 目录
2. `SkillYamlParser` 解析 `skill.yaml`
3. 验证 `type: nexus-ui`
4. 注册到 `SkillMetadata.ui` 字段

### 6.2 SkillMetadata 扩展

SDK 的 `SkillMetadata` 已有 `ui` 字段：

```java
public class SkillMetadata {
    // ...
    private Map<String, Object> ui = new HashMap<>();
    // ...
}
```

`nexusUi` 配置将存储在 `ui` 字段中：

```java
Map<String, Object> ui = metadata.getUi();
Map<String, Object> nexusUi = (Map<String, Object>) ui.get("nexusUi");
```

---

**文档版本**: v1.0  
**创建日期**: 2026-02-27
