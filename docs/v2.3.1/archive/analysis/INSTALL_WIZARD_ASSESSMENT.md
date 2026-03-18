# 能力发现安装向导摸底报告

**报告日期**: 2026-03-12  
**报告类型**: 功能完整性评估  

---

## 一、问题清单

### 1. 选择主导者功能（未实现）

**当前状态**: ❌ 占位符实现

**代码位置**: [capability-discovery.js:996-998](file:///e:/github/ooder-skills/skills/skill-scene/src/main/resources/static/console/js/pages/capability-discovery.js#L996)

```javascript
global.selectLeader = function() {
    alert('选择主导者功能开发中');
};
```

**问题分析**:
- 缺少用户选择器组件
- 未对接用户搜索API
- 未实现协作者选择功能

**需要实现**:
| 功能 | 状态 | 优先级 |
|------|------|--------|
| 用户搜索弹窗 | ❌ 未实现 | 高 |
| 用户选择器组件 | ❌ 未实现 | 高 |
| 协作者列表管理 | ⚠️ 部分实现 | 中 |

---

### 2. LLM配置界面（不符合三层规范）

**当前状态**: ⚠️ 简化实现，不符合LLM三层配置规范

**代码位置**: [capability-discovery.js:763-779](file:///e:/github/ooder-skills/skills/skill-scene/src/main/resources/static/console/js/pages/capability-discovery.js#L763)

**规范参考**: [llm-multi-level-config-example.md](file:///e:/github/ooder-skills/skills/skill-scene/docs/llm-multi-level-config-example.md)

**当前实现问题**:

| 配置项 | 规范要求 | 当前实现 | 差距 |
|--------|----------|----------|------|
| Provider选择 | 支持多Provider、多模型 | 仅3个Provider卡片 | 缺少模型选择 |
| 配置层级 | 5层优先级配置 | 无层级概念 | 完全缺失 |
| Function Calling | 工具函数配置、参数定义 | 仅一个开关 | 严重不足 |
| 知识资料库 | RAG配置、文档管理 | 完全缺失 | 未实现 |

**LLM三层配置规范摘要**:

```
配置优先级（从高到低）:
1. 用户配置 (capabilityBindings)     ← 安装向导应设置此层
2. 场景默认配置 (sceneDefaults)      ← 场景技能推荐
3. 系统环境配置 (capabilityDefaults.{env})
4. 系统规模配置 (capabilityDefaults.{tier})
5. 枚举兜底 (CapabilitySegment.fallback)
```

**需要实现的LLM配置项**:

```yaml
llmConfig:
  # Provider配置
  provider:
    id: deepseek
    model: deepseek-chat
    tier: medium
  
  # 模型参数
  parameters:
    temperature: 0.7
    maxTokens: 4096
    topP: 0.9
  
  # Function Calling配置
  functionCalling:
    enabled: true
    tools:
      - name: query_knowledge
        description: 查询知识库
        parameters: {...}
      - name: send_notification
        description: 发送通知
        parameters: {...}
  
  # 知识资料库配置
  knowledge:
    enabled: true
    bases:
      - id: kb-product-docs
        name: 产品文档库
      - id: kb-faq
        name: 常见问题库
    rag:
      topK: 5
      scoreThreshold: 0.7
      rerank: true
```

---

### 3. 安装进度（前端模拟）

**当前状态**: ⚠️ 前端模拟进度，非真实安装进度

**代码位置**: [capability-discovery.js:861-902](file:///e:/github/ooder-skills/skills/skill-scene/src/main/resources/static/console/js/pages/capability-discovery.js#L861)

```javascript
var progressSteps = [
    { name: '检查依赖', progress: 20 },
    { name: '下载资源', progress: 40 },
    { name: '注册能力', progress: 60 },
    { name: '配置权限', progress: 80 },
    { name: '完成安装', progress: 100 }
];

// 前端定时器模拟进度
stepIndex++;
if (stepIndex < progressSteps.length) {
    setTimeout(updateProgress, 300);  // ← 固定300ms间隔
}
```

**问题分析**:
- 进度条是前端定时器模拟，与后端实际安装进度无关
- 后端API是同步调用，无实时进度反馈
- 用户无法看到真实的安装状态

**后端安装流程** (GitDiscoveryController.java:368-404):
```java
InstallResult installResult = skillPackageManager.install(installRequest).get();
// ↑ 阻塞等待安装完成，无中间进度
```

**改进方案**:
| 方案 | 描述 | 复杂度 |
|------|------|--------|
| SSE推送 | 后端通过Server-Sent Events推送进度 | 高 |
| WebSocket | 双向通信，实时进度更新 | 高 |
| 轮询API | 前端轮询安装状态API | 中 |
| 异步任务 | 后端异步任务+状态查询 | 中 |

---

### 4. 菜单功能（未实现）

**当前状态**: ⚠️ 仅静态展示，未调用菜单添加API

**代码位置**: [capability-discovery.js:819-824](file:///e:/github/ooder-skills/skills/skill-scene/src/main/resources/static/console/js/pages/capability-discovery.js#L819)

```javascript
var completeMenuPreview = document.getElementById('completeMenuPreview');
if (completeMenuPreview) {
    completeMenuPreview.innerHTML = '<div class="menu-preview-item">' +
        '<i class="ri-puzzle-line"></i>' +
        '<span>' + cap.name + '</span></div>';  // ← 仅静态展示
}
```

**可用的菜单API** (RoleManagementController.java:79-80):
```java
@PostMapping("/roles/{roleId}/menus")
public ResultModel<MenuItemDTO> addMenuToRole(@PathVariable String roleId, @RequestBody MenuItemDTO menu)
```

**需要实现**:
| 功能 | 状态 | 说明 |
|------|------|------|
| 获取用户角色 | ❌ | 需要查询当前用户角色 |
| 调用addMenuToRole API | ❌ | 添加能力到用户菜单 |
| 菜单图标配置 | ❌ | 根据能力类型选择图标 |
| 菜单排序 | ❌ | 新安装能力排序 |

---

## 二、产出物清单

### 已完成产出物

| 产出物 | 路径 | 状态 |
|--------|------|------|
| 安装向导HTML | `capability-discovery.html` | ✅ 完成 |
| 安装向导JS | `capability-discovery.js` | ✅ 完成 |
| 安装向导CSS | `capability-discovery.css` | ✅ 完成 |
| 安装请求DTO | `InstallSkillRequestDTO.java` | ✅ 完成 |
| 安装结果DTO | `InstallResultDTO.java` | ✅ 完成 |
| 安装API | `GitDiscoveryController.installSkill()` | ✅ 完成 |
| SE扩展任务文档 | `COLLABORATION_TASK_SE_EXTENSION.md` | ✅ 完成 |

### 未完成产出物

| 产出物 | 缺失内容 | 优先级 |
|--------|----------|--------|
| 用户选择器组件 | 主导者/协作者选择 | 高 |
| LLM三层配置界面 | Provider/模型/工具/知识库配置 | 高 |
| 真实安装进度 | SSE/WebSocket进度推送 | 中 |
| 菜单添加功能 | 调用addMenuToRole API | 中 |
| 知识库配置界面 | RAG配置、文档管理 | 中 |

---

## 三、功能完成度评估

```
┌─────────────────────────────────────────────────────────────────┐
│                    安装向导功能完成度                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  步骤1: 预览详情     ████████████████████ 100% ✅              │
│                                                                 │
│  步骤2: 选择角色     ████████████████████ 100% ✅              │
│                                                                 │
│  步骤3: 配置参与者   ████████░░░░░░░░░░░░  40% ⚠️              │
│        - 主导者选择  ░░░░░░░░░░░░░░░░░░░░   0% ❌              │
│        - 协作者管理  ████████████████░░░░  80% ⚠️              │
│        - 推送设置    ████████████████████ 100% ✅              │
│                                                                 │
│  步骤4: 驱动条件     ████████████████████ 100% ✅              │
│                                                                 │
│  步骤5: 确认依赖     ████████████████████ 100% ✅              │
│                                                                 │
│  步骤6: LLM配置      ████░░░░░░░░░░░░░░░░  20% ❌              │
│        - Provider    ████████████████░░░░  80% ⚠️              │
│        - 模型选择    ░░░░░░░░░░░░░░░░░░░░   0% ❌              │
│        - Function    ██░░░░░░░░░░░░░░░░░░  10% ❌              │
│        - 知识库      ░░░░░░░░░░░░░░░░░░░░   0% ❌              │
│                                                                 │
│  步骤7: 安装进度     ████████░░░░░░░░░░░░  40% ⚠️              │
│        - 进度展示    ████████████████████ 100% ✅              │
│        - 真实进度    ░░░░░░░░░░░░░░░░░░░░   0% ❌              │
│                                                                 │
│  步骤8: 安装完成     ████████████░░░░░░░░  60% ⚠️              │
│        - 完成信息    ████████████████████ 100% ✅              │
│        - 菜单添加    ░░░░░░░░░░░░░░░░░░░░   0% ❌              │
│        - 通知发送    ░░░░░░░░░░░░░░░░░░░░   0% ❌              │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│  总体完成度:        ████████████░░░░░░░░  57%                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 四、改进建议

### 高优先级

1. **实现用户选择器组件**
   - 创建通用的用户搜索选择组件
   - 支持多选（协作者）和单选（主导者）
   - 对接用户搜索API

2. **重构LLM配置界面**
   - 参照 [llm-config.html](file:///e:/github/ooder-skills/skills/skill-scene/src/main/resources/static/console/pages/llm-config.html) 设计
   - 实现Provider + 模型选择
   - 添加Function Calling工具配置
   - 添加知识库绑定功能

### 中优先级

3. **实现真实安装进度**
   - 方案A: 使用SSE推送安装进度
   - 方案B: 使用异步任务 + 状态轮询

4. **实现菜单添加功能**
   - 安装完成后调用 `addMenuToRole` API
   - 根据能力类型配置菜单图标

### 低优先级

5. **完善通知功能**
   - 安装成功后发送站内通知
   - 协作者邀请通知

---

## 五、相关文档

| 文档 | 路径 |
|------|------|
| LLM三层配置规范 | [llm-multi-level-config-example.md](file:///e:/github/ooder-skills/skills/skill-scene/docs/llm-multi-level-config-example.md) |
| SE标准扩展任务 | [COLLABORATION_TASK_SE_EXTENSION.md](file:///e:/github/ooder-skills/skills/skill-scene/docs/COLLABORATION_TASK_SE_EXTENSION.md) |
| LLM配置页面 | [llm-config.html](file:///e:/github/ooder-skills/skills/skill-scene/src/main/resources/static/console/pages/llm-config.html) |
| 知识库配置页面 | [scene-knowledge.html](file:///e:/github/ooder-skills/skills/skill-scene/src/main/resources/static/console/pages/scene-knowledge.html) |

---

---

## 六、Mock/非真实实现检测

### 6.1 发现闭环 (Discovery Loop)

| 检测点 | 状态 | 说明 |
|--------|------|------|
| 前端API调用 | ✅ 正常 | `POST /api/v1/discovery/local` 正确调用 |
| 后端处理 | ✅ 正常 | `GitDiscoveryController.discoverFromLocal()` 使用真实数据源 |
| 数据返回 | ✅ 正常 | 返回 `skill-index-entry.yaml` 中的真实数据 |
| Mock回退 | ⚠️ 存在 | GitHub/Gitee发现失败时会回退到Mock数据 |

**Mock回退代码位置**:
```java
// GitDiscoveryController.java:123-125
if (capabilities.isEmpty() && mockEnabled) {
    log.info("[discoverFromGitHub] Using mock data (mockEnabled=true)");
    capabilities = getMockGitHubCapabilities(config.getRepoUrl());
}
```

### 6.2 安装闭环 (Install Loop)

| 检测点 | 状态 | 说明 |
|--------|------|------|
| 前端请求构建 | ✅ 正常 | `collectInstallConfig()` 正确收集配置 |
| API调用 | ✅ 正常 | `POST /api/v1/discovery/install` 正确调用 |
| mockEnabled检查 | ⚠️ **问题** | `mockEnabled=true` 时跳过真实安装 |
| skillPackageManager | ⚠️ 存在 | 存在但未验证真实执行结果 |
| skillService | ⚠️ 存在 | 存在但未验证真实执行结果 |
| 安装进度 | ❌ **前端模拟** | 前端定时器模拟进度，非真实反馈 |
| 菜单添加 | ❌ **未调用** | 安装完成后未调用菜单API |

**Mock跳过真实安装代码位置**:
```java
// GitDiscoveryController.java:345-354
if (mockEnabled) {
    log.info("[installSkill] Mock mode enabled, returning success for {}", request.getSkillId());
    skillIndexLoader.markAsInstalled(request.getSkillId());
    result.setStatus("installed");
    result.setMessage("Skill installed successfully (mock mode)");
    // 直接返回，跳过真实安装！
    return ResultModel.success(result);
}
```

**安装进度前端模拟代码位置**:
```javascript
// capability-discovery.js:861-902
var progressSteps = [
    { name: '检查依赖', progress: 20 },
    { name: '下载资源', progress: 40 },
    // ...
];
// 前端定时器模拟，与后端无关
stepIndex++;
if (stepIndex < progressSteps.length) {
    setTimeout(updateProgress, 300);  // 固定300ms间隔
}
```

### 6.3 菜单闭环 (Menu Loop)

| 检测点 | 状态 | 说明 |
|--------|------|------|
| 前端渲染 | ⚠️ 仅静态展示 | `completeMenuPreview` 只是静态HTML |
| API调用 | ❌ **未调用** | 未调用 `POST /api/v1/roles/{roleId}/menus` |
| 后端服务 | ✅ 已实现 | `RoleManagementController.addMenuToRole()` 已实现 |

**菜单未调用API代码位置**:
```javascript
// capability-discovery.js:819-824
var completeMenuPreview = document.getElementById('completeMenuPreview');
if (completeMenuPreview) {
    // 仅静态展示，未调用API！
    completeMenuPreview.innerHTML = '<div class="menu-preview-item">' +
        '<i class="ri-puzzle-line"></i>' +
        '<span>' + cap.name + '</span></div>';
}
```

---

## 七、问题汇总统计

### 7.1 按严重程度分类

| 严重程度 | 问题数 | 问题列表 |
|----------|--------|----------|
| **🔴 高** | 4 | mockEnabled跳过安装、菜单未添加、安装进度模拟、LLM配置不完整 |
| **🟡 中** | 3 | GitHub/Gitee Mock回退、用户选择器未实现、知识库配置缺失 |
| **🟢 低** | 2 | 选择主导者占位符、通知功能缺失 |

### 7.2 按闭环分类

| 闭环 | 状态 | 完整度 | 问题数 |
|------|------|--------|--------|
| 发现闭环 | ✅ 完整 | 100% | 0 |
| 安装闭环 | ⚠️ 部分 | 40% | 3 |
| 菜单闭环 | ❌ 断裂 | 0% | 1 |

### 7.3 Mock使用位置统计

| 位置 | 类型 | 触发条件 |
|------|------|----------|
| `GitDiscoveryController.java:123-125` | Mock回退 | GitHub发现失败 + mockEnabled |
| `GitDiscoveryController.java:229-231` | Mock回退 | Gitee发现失败 + mockEnabled |
| `GitDiscoveryController.java:254-255` | Mock数据 | Git仓库发现 + mockEnabled |
| `GitDiscoveryController.java:345-354` | Mock安装 | mockEnabled=true |
| `GitDiscoveryController.java:461-467` | Mock兜底 | skillPackageManager不可用 + mockEnabled |
| `CapabilityController.java:247-249` | Mock响应 | capabilityId匹配特定值 |
| `SceneEngineIntegration.java:117-118` | Mock结果 | SDK不可用 + mockEnabled |

---

**报告人**: AI Assistant  
**最后更新**: 2026-03-12
