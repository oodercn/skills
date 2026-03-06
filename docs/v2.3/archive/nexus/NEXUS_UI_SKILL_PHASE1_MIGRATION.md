# Nexus-UI Skill 第一阶段迁移方案

> **版本**: v1.0  
> **日期**: 2026-02-27  
> **目标**: 完成标准定义 + 页面迁移 + 发现/安装/运行流程

---

## 一、现有工程分析

### 1.1 前端页面结构

```
ooder-Nexus/src/main/resources/static/console/
├── pages/                      # 页面目录 (约60+页面)
│   ├── admin/                  # 管理后台 (5个)
│   ├── audit/                  # 审计日志 (1个)
│   ├── collaboration/          # 协作 (1个)
│   ├── config/                 # 配置 (1个)
│   ├── group/                  # 群组 (3个)
│   ├── im/                     # 即时通讯 (4个)
│   ├── lan/                    # 局域网 (7个)
│   ├── llm/                    # LLM (3个)
│   ├── network/                # 网络 (8个)
│   ├── nexus/                  # Nexus管理 (15个)
│   ├── openwrt/                # OpenWrt (8个)
│   ├── personal/               # 个人中心 (7个)
│   ├── protocol/               # 协议 (5个)
│   ├── scene/                  # 场景 (4个)
│   ├── security/               # 安全 (2个)
│   ├── skill/                  # 技能 (2个)
│   ├── skillcenter/            # 技能中心 (1个)
│   ├── storage/                # 存储 (3个)
│   └── task/                   # 任务 (2个)
├── js/                         # JavaScript
│   ├── nexus.js               # 核心UI框架 (NX命名空间)
│   ├── api.js                 # API请求模块
│   ├── menu-loader.js         # 菜单加载器
│   └── pages/                 # 各页面JS
├── css/                        # 样式
│   ├── nexus.css              # 核心样式
│   ├── theme.css              # 主题样式
│   └── pages/                 # 各页面样式
└── menu-config.json           # 菜单配置
```

### 1.2 现有页面模板结构

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>页面标题 - Nexus Console</title>
    <link rel="stylesheet" href="/console/css/remixicon/remixicon.css">
    <link rel="stylesheet" href="/console/css/nexus.css">
</head>
<body>
    <div class="nx-page">
        <aside class="nx-page__sidebar">...</aside>
        <main class="nx-page__content">...</main>
    </div>
    <script src="/console/js/nexus.js"></script>
    <script src="/console/js/menu-loader.js"></script>
    <script src="/console/js/page-init.js"></script>
</body>
</html>
```

### 1.3 现有 Skill 结构

```
skills/
├── skill-user-auth/
│   └── skill.yaml              # 技能定义
├── skill-org-dingding/
│   └── skill.yaml
└── skill-org-feishu/
    └── skill.yaml
```

### 1.4 现有服务

| 服务 | 功能 | 状态 |
|------|------|------|
| `InstalledSkillService` | 已安装技能管理 | ✅ 已实现 |
| `SkillMarketService` | 技能市场服务 | ✅ 已实现 |
| `SkillConfigService` | 技能配置服务 | ✅ 已实现 |
| `StaticResourceConfig` | 静态资源配置 | ✅ 已实现 |

---

## 二、Nexus-UI Skill 标准定义

### 2.1 目录结构规范

```
skill-{name}-nexus-ui/
├── skill.yaml                    # Skill 元数据定义
├── README.md                     # 说明文档
├── ui/                           # 前端资源目录
│   ├── pages/                    # HTML 页面
│   │   └── index.html           # 主页面（必需）
│   ├── css/                     # 自定义样式（可选）
│   └── js/                      # 自定义脚本（可选）
├── api/                          # API 定义（可选）
│   └── spec/openapi.yaml        # OpenAPI 规范
└── config/                       # 配置文件
    └── menu.json                # 菜单配置
```

### 2.2 skill.yaml 规范

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-{name}-nexus-ui
  name: 技能名称
  version: 1.0.0
  description: 技能描述
  type: nexus-ui                # 必需：标识为 Nexus-UI 类型
  author: ooder Team
  license: Apache-2.0

spec:
  type: nexus-ui
  
  # Nexus-UI 特有配置
  nexusUi:
    # 入口页面配置
    entry:
      page: index.html
      title: 页面标题
      icon: ri-dashboard-line    # Remix Icon
      
    # 菜单配置
    menu:
      position: sidebar          # sidebar | header | dropdown
      category: system           # 菜单分类
      order: 100                 # 排序权重
      
    # 布局配置
    layout:
      type: default              # default | fullscreen | embedded
      sidebar: true              # 是否显示侧边栏
      header: true               # 是否显示头部
      
    # 兼容性
    compatibility:
      nexusVersion: ">=2.0.0"
      
  # 能力定义
  capabilities:
    - id: {name}-view
      name: 查看功能
      description: 查看功能描述
      category: ui
      
  # API 端点
  apis:
    - path: /api/{name}/list
      method: GET
      description: 获取列表
      page: index.html
      
  # 依赖
  dependencies:
    skills: []
    nexusVersion: ">=2.0.0"
```

### 2.3 页面模板规范

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{{skill.name}} - Nexus Console</title>
    
    <!-- Nexus 核心样式 (本地路径) -->
    <link rel="stylesheet" href="/console/css/remixicon/remixicon.css">
    <link rel="stylesheet" href="/console/css/nexus.css">
    
    <!-- Skill 自定义样式（可选） -->
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>
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

### 2.4 菜单配置规范 (menu.json)

```json
{
  "id": "{name}",
  "name": "功能名称",
  "icon": "ri-dashboard-line",
  "url": "/console/skills/{skill-id}/index.html",
  "children": [
    {
      "id": "{name}-sub",
      "name": "子功能",
      "icon": "ri-list-check",
      "url": "/console/skills/{skill-id}/pages/sub.html"
    }
  ]
}
```

---

## 三、迁移策略

### 3.1 迁移原则

1. **渐进式迁移**：先迁移简单页面，逐步扩展
2. **保持兼容**：迁移后的页面与现有页面共存
3. **最小改动**：尽量复用现有代码和资源
4. **标准化**：统一使用 Nexus-UI Skill 规范

### 3.2 迁移分类

| 分类 | 页面数 | 复杂度 | 优先级 | 示例 |
|------|--------|--------|--------|------|
| 简单展示页 | 15 | 低 | P0 | dashboard, status |
| 列表管理页 | 20 | 中 | P1 | user-list, skill-list |
| 复杂交互页 | 15 | 高 | P2 | network-topology, p2p-visualization |
| 配置管理页 | 10 | 中 | P1 | config, settings |

### 3.3 首批迁移页面（P0）

| 页面 | 原路径 | 目标 Skill ID | 复杂度 |
|------|--------|---------------|--------|
| Nexus仪表盘 | /console/pages/nexus/dashboard.html | skill-nexus-dashboard-nexus-ui | 简单 |
| 系统状态 | /console/pages/nexus/system-status.html | skill-nexus-system-status-nexus-ui | 简单 |
| 个人仪表盘 | /console/pages/personal/dashboard.html | skill-personal-dashboard-nexus-ui | 简单 |
| 健康检查 | /console/pages/nexus/health-check.html | skill-nexus-health-check-nexus-ui | 简单 |
| 存储管理 | /console/pages/storage/storage-management.html | skill-storage-management-nexus-ui | 简单 |

---

## 四、发现、安装、运行流程

### 4.1 发现流程

```
┌─────────────────────────────────────────────────────────────────┐
│                      Skill 发现流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 启动扫描                                                    │
│     └─> 扫描 skills/ 目录                                       │
│     └─> 扫描 ~/.nexus/skills/ 目录                              │
│     └─> 扫描远程仓库 (可选)                                     │
│                                                                 │
│  2. 解析 skill.yaml                                             │
│     └─> 验证元数据                                              │
│     └─> 验证 type: nexus-ui                                     │
│     └─> 验证兼容性                                              │
│                                                                 │
│  3. 注册到 SkillRegistry                                         │
│     └─> 注册能力                                                │
│     └─> 注册菜单                                                │
│     └─> 注册 API 端点                                           │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 安装流程

```
┌─────────────────────────────────────────────────────────────────┐
│                      Skill 安装流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 接收安装请求                                                │
│     └─> 参数: skillId, source (local/remote)                    │
│                                                                 │
│  2. 下载/复制 Skill 包                                          │
│     └─> 本地: 复制到 skills/ 目录                               │
│     └─> 远程: 下载并解压                                        │
│                                                                 │
│  3. 验证 Skill 包                                               │
│     └─> 检查 skill.yaml                                         │
│     └─> 检查必需文件 (ui/pages/index.html)                      │
│     └─> 检查兼容性                                              │
│                                                                 │
│  4. 部署静态资源                                                │
│     └─> 复制到 static/console/skills/{skill-id}/                │
│                                                                 │
│  5. 注册菜单                                                    │
│     └─> 合并到 menu-config.json                                 │
│                                                                 │
│  6. 更新状态                                                    │
│     └─> 状态: INSTALLED                                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.3 运行流程

```
┌─────────────────────────────────────────────────────────────────┐
│                      Skill 运行流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 用户访问 Skill 页面                                         │
│     └─> URL: /console/skills/{skill-id}/index.html              │
│                                                                 │
│  2. 静态资源服务                                                │
│     └─> Spring Boot 提供 /console/skills/** 映射                │
│     └─> 返回 HTML 页面                                          │
│                                                                 │
│  3. 页面加载                                                    │
│     └─> 加载 nexus.css (核心样式)                               │
│     └─> 加载 nexus.js (核心框架)                                │
│     └─> 加载 menu-loader.js (菜单)                              │
│     └─> 加载 Skill 自定义资源                                   │
│                                                                 │
│  4. API 调用                                                    │
│     └─> Skill API: /api/skills/{skill-id}/**                    │
│     └─> Nexus API: /api/**                                      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 五、实现任务分解

### 5.1 Phase 1: 标准定义（1天）

| 任务ID | 任务名称 | 工作量 | 交付物 |
|--------|----------|--------|--------|
| MIG-001 | 定义 skill.yaml 规范 | 0.5天 | skill-yaml-spec.md |
| MIG-002 | 定义页面模板规范 | 0.5天 | page-template-spec.md |

### 5.2 Phase 2: 核心服务实现（3天）

| 任务ID | 任务名称 | 工作量 | 交付物 |
|--------|----------|--------|--------|
| MIG-003 | NexusUiSkillRegistry 实现 | 1天 | NexusUiSkillRegistry.java |
| MIG-004 | NexusUiSkillInstaller 实现 | 1天 | NexusUiSkillInstaller.java |
| MIG-005 | 静态资源映射配置 | 0.5天 | StaticResourceConfig 更新 |
| MIG-006 | 菜单动态注册 | 0.5天 | MenuRegistry.java |

### 5.3 Phase 3: 首批页面迁移（2天）

| 任务ID | 任务名称 | 工作量 | 交付物 |
|--------|----------|--------|--------|
| MIG-007 | 迁移 nexus-dashboard | 0.5天 | skill-nexus-dashboard-nexus-ui |
| MIG-008 | 迁移 system-status | 0.5天 | skill-nexus-system-status-nexus-ui |
| MIG-009 | 迁移 personal-dashboard | 0.5天 | skill-personal-dashboard-nexus-ui |
| MIG-010 | 迁移 health-check | 0.5天 | skill-nexus-health-check-nexus-ui |

### 5.4 Phase 4: 测试验证（1天）

| 任务ID | 任务名称 | 工作量 | 交付物 |
|--------|----------|--------|--------|
| MIG-011 | 发现流程测试 | 0.25天 | 测试报告 |
| MIG-012 | 安装流程测试 | 0.25天 | 测试报告 |
| MIG-013 | 运行流程测试 | 0.25天 | 测试报告 |
| MIG-014 | 文档完善 | 0.25天 | 用户指南 |

---

## 六、关键实现代码

### 6.1 NexusUiSkillRegistry.java

```java
package net.ooder.nexus.service.skill;

import net.ooder.sdk.api.skill.InstalledSkill;
import org.springframework.stereotype.Service;
import java.nio.file.*;
import java.util.*;

@Service
public class NexusUiSkillRegistry {
    
    private final Path skillsPath = Paths.get("skills");
    private final Map<String, NexusUiSkill> registry = new HashMap<>();
    
    public void discover() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(skillsPath)) {
            for (Path skillDir : stream) {
                if (Files.isDirectory(skillDir)) {
                    Path skillYaml = skillDir.resolve("skill.yaml");
                    if (Files.exists(skillYaml)) {
                        NexusUiSkill skill = parseSkill(skillYaml);
                        if (skill != null && "nexus-ui".equals(skill.getType())) {
                            registry.put(skill.getId(), skill);
                            registerMenu(skill);
                        }
                    }
                }
            }
        } catch (Exception e) {
            console.error("Skill discovery failed", e);
        }
    }
    
    public NexusUiSkill getSkill(String skillId) {
        return registry.get(skillId);
    }
    
    public List<NexusUiSkill> getAllSkills() {
        return new ArrayList<>(registry.values());
    }
    
    private void registerMenu(NexusUiSkill skill) {
        // 注册菜单到 menu-config.json
    }
}
```

### 6.2 StaticResourceConfig 更新

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 现有配置
    registry.addResourceHandler("/console/**")
            .addResourceLocations("classpath:/static/console/");
    
    // 新增: Nexus-UI Skill 静态资源
    registry.addResourceHandler("/console/skills/**")
            .addResourceLocations("file:./skills/")
            .setCachePeriod(0);
}
```

### 6.3 菜单动态注册

```java
@Service
public class MenuRegistry {
    
    private final Path menuConfigPath = Paths.get("src/main/resources/static/console/menu-config.json");
    
    public void registerSkillMenu(NexusUiSkill skill) {
        try {
            String json = Files.readString(menuConfigPath);
            JSONObject config = new JSONObject(json);
            JSONArray menu = config.getJSONArray("menu");
            
            JSONObject menuItem = new JSONObject();
            menuItem.put("id", skill.getId());
            menuItem.put("name", skill.getName());
            menuItem.put("icon", skill.getIcon());
            menuItem.put("url", "/console/skills/" + skill.getId() + "/index.html");
            menuItem.put("status", "implemented");
            menuItem.put("roles", new JSONArray().put("personal").put("mcp"));
            
            menu.put(menuItem);
            
            Files.writeString(menuConfigPath, config.toString(2));
        } catch (Exception e) {
            console.error("Menu registration failed", e);
        }
    }
}
```

---

## 七、迁移示例

### 7.1 nexus-dashboard 迁移

**原文件**: `src/main/resources/static/console/pages/nexus/dashboard.html`

**迁移后结构**:
```
skills/skill-nexus-dashboard-nexus-ui/
├── skill.yaml
├── README.md
└── ui/
    └── pages/
        └── index.html
```

**skill.yaml**:
```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-nexus-dashboard-nexus-ui
  name: Nexus仪表盘
  version: 1.0.0
  description: Nexus系统仪表盘，显示终端代理、网络链路、命令执行等统计信息
  type: nexus-ui
  author: ooder Team

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
      page: index.html
```

---

## 八、总结

### 8.1 第一阶段目标

| 目标 | 状态 | 说明 |
|------|------|------|
| 标准定义 | ✅ 完成 | skill.yaml + 页面模板规范 |
| 发现流程 | ✅ 设计完成 | NexusUiSkillRegistry |
| 安装流程 | ✅ 设计完成 | NexusUiSkillInstaller |
| 运行流程 | ✅ 设计完成 | 静态资源映射 |
| 首批迁移 | ⏳ 待执行 | 5个简单页面 |

### 8.2 工作量估算

| 阶段 | 工作量 | 主要内容 |
|------|--------|----------|
| Phase 1 | 1天 | 标准定义 |
| Phase 2 | 3天 | 核心服务实现 |
| Phase 3 | 2天 | 首批页面迁移 |
| Phase 4 | 1天 | 测试验证 |
| **总计** | **7天** | - |

### 8.3 下一步行动

1. **立即执行**: 实现 NexusUiSkillRegistry
2. **本周完成**: 核心服务 + 首批迁移
3. **下周计划**: 扩展迁移更多页面

---

**文档版本**: v1.0  
**创建日期**: 2026-02-27  
**最后更新**: 2026-02-27
