# 能力管理模块使用指南

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v2.3 |
| 创建日期 | 2026-03-05 |
| 所属模块 | skill-scene |
| 文档类型 | 用户指南 |

---

## 一、模块概述

### 1.1 功能定位

能力管理模块是 Ooder 平台的核心功能，提供场景能力和技能能力的统一管理入口。

### 1.2 核心概念

| 概念 | 定义 | 示例 |
|------|------|------|
| **场景能力** | 开箱即用 + 用户交互UI + 导航菜单入口 | 日志汇报、LLM对话、知识问答 |
| **技能能力** | 底层能力组件，提供原子化功能 | 日志提醒、日志提交、邮件通知 |
| **场景组** | 场景能力的运行实例 | 研发部日志汇报组 |

### 1.3 能力分层架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           能力分层架构                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  第一层：场景能力 (Scene Capabilities)                                       │
│  - 定义：开箱即用 + 用户交互UI + 导航菜单入口                                 │
│  - 面向：终端用户                                                           │
│  - 数据来源：skill-index.yaml 中的 scenes 列表                               │
│                                                                             │
│  第二层：技能能力 (Skill Capabilities)                                       │
│  - 定义：底层能力组件，提供原子化功能                                        │
│  - 面向：开发者                                                             │
│  - 数据来源：skill-index.yaml 中的 skills 列表                               │
│                                                                             │
│  第三层：基础服务 (Infrastructure Services)                                  │
│  - 定义：系统级基础设施服务                                                  │
│  - 面向：运维人员                                                           │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、导航菜单

### 2.1 菜单结构

```
📊 首页

🎯 场景能力
   ├── 场景能力列表      → 浏览所有可用的场景能力
   └── 已安装场景能力    → 查看已安装的场景能力包

🔧 技能管理
   ├── 技能市场         → 浏览和发现技能包
   ├── 已安装技能       → 管理已安装的技能包
   ├── 能力绑定         → 配置能力与场景组的关联
   └── 创建能力         → 手动创建或LLM生成能力

📁 场景组管理
   ├── 场景组列表       → 管理所有场景组
   ├── 场景模板         → 管理场景模板
   └── 执行历史         → 查看执行历史

👤 我的工作台
   ├── 我的待办         → 协作邀请、领导委派的任务
   ├── 我的场景         → 我创建的、我启动的场景
   └── 已完成场景       → 历史场景执行记录

📊 能力统计
   └── 能力调用统计和性能监控
```

### 2.2 权限说明

| 菜单 | 普通用户 | 开发者 | 管理员 |
|------|:--------:|:------:|:------:|
| 首页 | ✓ | ✓ | ✓ |
| 场景能力 | ✓ | ✓ | ✓ |
| 技能管理 | - | ✓ | ✓ |
| 场景组管理 | - | ✓ | ✓ |
| 系统设置 | - | - | ✓ |

---

## 三、功能使用指南

### 3.1 场景能力列表

**访问地址**：`/console/pages/scene-capabilities.html`

**功能说明**：
- 浏览所有可用的场景能力
- 按分类筛选场景能力
- 搜索场景能力
- 一键安装/使用场景能力

**操作步骤**：

1. 打开"场景能力 > 场景能力列表"页面
2. 使用分类标签筛选或搜索框搜索
3. 点击"安装"按钮安装场景能力
4. 安装成功后，点击"使用"按钮启动场景

**状态说明**：

| 状态 | 说明 |
|------|------|
| 已安装 | 场景能力已安装，可以直接使用 |
| 可安装 | 场景能力未安装，需要先安装 |

### 3.2 已安装场景能力

**访问地址**：`/console/pages/installed-scene-capabilities.html`

**功能说明**：
- 查看已安装的场景能力包
- 区分场景能力和技能能力
- 快速使用已安装的能力

**常见问题**：

**Q: 为什么页面显示"暂无已安装的场景能力"？**

A: 这是正常情况，原因如下：
- 当前环境中没有安装任何技能包
- 安装功能需要 SDK 支持 `installFromUrl()` 方法
- 请先在"场景能力列表"页面安装需要的场景能力

**解决方法**：
1. 点击"去安装更多"按钮
2. 跳转到场景能力列表页面
3. 选择需要的场景能力进行安装

### 3.3 我的场景

**访问地址**：`/console/pages/my-scenes.html`

**功能说明**：
- 查看我创建的场景组
- 查看我启动的场景
- 查看我参与的场景

**数据来源**：

| 区域 | API 端点 |
|------|----------|
| 我创建的场景 | `/api/v1/scene-groups/my/created` |
| 进行中的场景 | `/api/v1/scene-groups?status=ACTIVE` |
| 我参与的场景 | `/api/v1/scene-groups/my/participated` |

---

## 四、API 接口

### 4.1 能力发现

**获取能力列表**：

```http
POST /api/v1/discovery/gitee
Content-Type: application/json

{
    "repoUrl": "https://gitee.com/ooderCN/skills"
}
```

**响应示例**：

```json
{
    "code": 200,
    "data": {
        "capabilities": [
            {
                "id": "skill-a2ui",
                "name": "A2UI Skill",
                "description": "UI Generation Skill",
                "version": "0.7.3",
                "status": "available",
                "sceneCapability": false,
                "category": "ui"
            }
        ]
    }
}
```

### 4.2 安装能力

**安装技能**：

```http
POST /api/v1/discovery/install
Content-Type: application/json

{
    "skillId": "skill-a2ui",
    "source": "GITEE"
}
```

**响应示例**：

```json
{
    "code": 200,
    "data": {
        "skillId": "skill-a2ui",
        "status": "installed",
        "message": "Skill installed successfully"
    }
}
```

### 4.3 场景组管理

**获取场景组列表**：

```http
GET /api/v1/scene-groups?pageNum=1&pageSize=10
```

**获取我创建的场景组**：

```http
GET /api/v1/scene-groups/my/created?pageNum=1&pageSize=10
```

---

## 五、常见问题

### 5.1 场景能力页面无数据

**问题**：场景能力列表页面显示空白

**原因**：
- API 方法调用错误（GET → POST）
- 网络连接问题

**解决方法**：
1. 使用 Ctrl+F5 硬刷新浏览器缓存
2. 检查网络连接
3. 查看浏览器控制台错误信息

### 5.2 我的场景页面无数据

**问题**：我的场景页面显示"暂无创建的场景"

**原因**：
- 当前用户没有创建任何场景组
- 测试数据中的 creatorId 与当前用户 ID 不匹配

**解决方法**：
1. 创建新的场景组
2. 检查后端日志确认用户 ID

### 5.3 安装失败

**问题**：点击安装按钮后显示"安装失败"

**原因**：
- SDK 不支持 `installFromUrl()` 方法
- 下载 URL 无效

**解决方法**：
- 等待 SDK 团队实现 `installFromUrl()` 方法
- 参考 `SDK_COLLABORATION_INSTALL_FROM_URL.md` 文档

---

## 六、技术架构

### 6.1 前端架构

```
/console/pages/
├── scene-capabilities.html        # 场景能力列表
├── scene-capability-detail.html   # 场景能力详情
├── installed-scene-capabilities.html  # 已安装场景能力
├── my-scenes.html                 # 我的场景
└── ...

/console/js/pages/
├── scene-capabilities.js
├── scene-capability-detail.js
├── installed-scene-capabilities.js
├── my-scenes.js
└── ...
```

### 6.2 后端架构

```
net.ooder.skill.scene
├── controller/
│   ├── GitDiscoveryController.java    # 能力发现 API
│   └── SceneGroupController.java      # 场景组管理 API
├── discovery/
│   └── SkillIndexLoader.java          # skill-index.yaml 加载器
├── service/
│   └── impl/
│       └── SceneGroupServiceMemoryImpl.java
└── dto/
    └── discovery/
        └── CapabilityDTO.java         # 能力数据传输对象
```

### 6.3 数据流

```
skill-index.yaml
       ↓
SkillIndexLoader.loadSkillIndex()
       ↓
GitDiscoveryController.discoverFromGitee()
       ↓
前端 API 调用
       ↓
页面渲染
```

---

## 七、相关文档

| 文档 | 说明 |
|------|------|
| [CAPABILITY_MODULE_REDESIGN.md](./CAPABILITY_MODULE_REDESIGN.md) | 能力管理模块功能重新规划 |
| [SDK_COLLABORATION_INSTALL_FROM_URL.md](./SDK_COLLABORATION_INSTALL_FROM_URL.md) | SDK 安装功能增强协同任务 |
| [scene-capability-requirements-specification.md](./scene-capability-requirements-specification.md) | 场景能力需求规格说明 |
| [CHANGELOG.md](./CHANGELOG.md) | 版本更新日志 |

---

## 八、联系方式

如有问题，请联系：
- 技术支持：[待指定]
- 产品负责人：[待指定]

---

**文档版本**: v2.3  
**最后更新**: 2026-03-05
