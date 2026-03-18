# 协作任务 - MVP 架构重构：核心 Skills 内置化

## 任务概述

**提交人**: MVP Core 团队  
**提交时间**: 2026-03-18  
**优先级**: 高  
**影响范围**: MVP Core、Skills 团队

---

## 背景说明

当前 MVP 通过 `skill-hotplug-starter` 动态加载核心 Skills，导致以下问题：
1. 启动顺序复杂，依赖管理困难
2. 菜单显示不完整（部分 skills 缺少 menu-config.json）
3. 调试困难，无法直接修改 skills 代码
4. 版本不一致（plugins/ 目录和 registry.properties 不同步）

**解决方案**: 将核心 Skills 直接内置到 MVP 中，不再通过 JAR 包动态加载。

---

## 第一部分：MVP 团队工作

### 1.1 创建目录结构

在 `mvp/src/main/` 下创建：

```
java/net/ooder/mvp/builtin/
├── scene/          # 来自 skill-scene
│   ├── controller/
│   ├── service/
│   ├── model/
│   └── config/
├── capability/     # 来自 skill-capability
│   ├── controller/
│   ├── service/
│   └── model/
├── llm/            # 来自 skill-llm
│   ├── controller/
│   ├── service/
│   └── model/
├── chat/           # 来自 skill-llm-chat
│   ├── controller/
│   ├── service/
│   └── model/
└── knowledge/      # 来自 skill-knowledge-base
    ├── controller/
    ├── service/
    └── model/

resources/static/console/
├── pages/
│   ├── scene/          # 场景页面
│   ├── capability/     # 能力页面
│   ├── llm/            # LLM页面
│   ├── chat/           # 聊天页面
│   └── knowledge/      # 知识库页面
├── js/pages/
│   ├── scene.js
│   ├── capability.js
│   ├── llm.js
│   ├── chat.js
│   └── knowledge.js
├── css/
│   └── builtin/        # 内置样式
└── menu-config.json    # 合并后的菜单配置
```

### 1.2 复制文件清单

从 `skill-scene` 复制以下核心文件：

**Controllers** (必须):
- SceneController.java
- SceneGroupController.java
- CapabilityController.java
- ActivationController.java
- InstallController.java
- ConfigController.java
- OrgController.java
- LlmController.java
- KnowledgeBaseController.java

**Services** (必须):
- SceneService.java / SceneServiceImpl.java
- CapabilityService.java / CapabilityServiceImpl.java
- ActivationService.java / ActivationServiceImpl.java
- InstallService.java / InstallServiceImpl.java
- ConfigLoaderService.java

**Models** (必须):
- Scene.java
- Capability.java
- CapabilityBinding.java
- InstallConfig.java
- SceneType.java

### 1.3 包名修改

将复制的文件包名从：
```java
package net.ooder.skill.scene.xxx;
```

修改为：
```java
package net.ooder.mvp.builtin.scene.xxx;
```

### 1.4 修改 MvpCoreApplication.java

```java
@ComponentScan(
    basePackages = {
        "net.ooder.mvp",
        "net.ooder.mvp.builtin.scene",
        "net.ooder.mvp.builtin.capability",
        "net.ooder.mvp.builtin.llm",
        "net.ooder.mvp.builtin.chat",
        "net.ooder.mvp.builtin.knowledge"
    }
)
```

### 1.5 修改 pom.xml

移除以下依赖（改为直接复制代码）：
```xml
<!-- 移除这些依赖 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-capability</artifactId>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-scene-management</artifactId>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-llm</artifactId>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-llm-chat</artifactId>
</dependency>
```

保留：
```xml
<!-- 保留热插拔机制，用于业务 skills -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-hotplug-starter</artifactId>
</dependency>
```

### 1.6 合并菜单配置

创建统一的 `menu-config.json`：

```json
{
  "menu": [
    {
      "id": "scene",
      "name": "场景中心",
      "icon": "ri-apps-line",
      "children": [
        {
          "id": "scene-list",
          "name": "场景列表",
          "url": "/console/pages/scene/list.html"
        },
        {
          "id": "scene-group",
          "name": "场景分组",
          "url": "/console/pages/scene/group.html"
        }
      ]
    },
    {
      "id": "capability",
      "name": "能力中心",
      "icon": "ri-puzzle-line",
      "children": [
        {
          "id": "capability-list",
          "name": "能力列表",
          "url": "/console/pages/capability/list.html"
        },
        {
          "id": "capability-discovery",
          "name": "发现能力",
          "url": "/console/pages/capability/discovery.html"
        },
        {
          "id": "capability-binding",
          "name": "能力绑定",
          "url": "/console/pages/capability/binding.html"
        }
      ]
    },
    {
      "id": "llm",
      "name": "LLM中心",
      "icon": "ri-brain-line",
      "children": [
        {
          "id": "llm-config",
          "name": "模型配置",
          "url": "/console/pages/llm/config.html"
        },
        {
          "id": "llm-chat",
          "name": "智能对话",
          "url": "/console/pages/chat/index.html"
        }
      ]
    },
    {
      "id": "knowledge",
      "name": "知识库",
      "icon": "ri-book-open-line",
      "children": [
        {
          "id": "knowledge-list",
          "name": "知识管理",
          "url": "/console/pages/knowledge/list.html"
        }
      ]
    }
  ]
}
```

---

## 第二部分：Skills 团队工作

### 2.1 标记内置 Skills

在以下 skills 的 `skill.yaml` 中添加标记：

```yaml
id: skill-capability
name: 能力管理
# 添加以下标记
builtin: true
mvp-core-builtin: true
skip-discovery: true
```

需要标记的 skills：
- skill-capability
- skill-scene-management
- skill-llm
- skill-llm-chat
- skill-knowledge-base

### 2.2 修改发现逻辑

在 `DiscoveryController` 或相关发现服务中，添加过滤逻辑：

```java
public List<SkillInfo> discoverSkills() {
    List<SkillInfo> allSkills = loadFromIndex();
    
    // 过滤掉已内置的 skills
    return allSkills.stream()
        .filter(skill -> !skill.isBuiltin())
        .collect(Collectors.toList());
}
```

### 2.3 移除重复安装检查

在 `InstallController` 中，对内置 skills 返回特殊提示：

```java
@PostMapping("/install")
public ResultModel<InstallResult> install(@RequestBody InstallRequest request) {
    String skillId = request.getSkillId();
    
    // 检查是否是内置 skill
    if (isBuiltinSkill(skillId