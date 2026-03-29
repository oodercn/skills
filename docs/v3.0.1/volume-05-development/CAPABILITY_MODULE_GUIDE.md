# 能力管理模块说明书 v2.3.1

## 模块概述

能力管理模块提供系统能力的发现、激活、绑定和管理功能。

---

## 一、能力列表

### 页面路径
`/console/pages/capability-management.html`

### 功能说明
展示系统中所有已注册的能力列表，支持筛选、搜索和查看详情。

### 用户故事
> 作为用户，我希望查看系统中所有可用的能力，以便了解系统能提供什么服务。

### 操作流程
1. 进入能力列表页面
2. 查看能力列表（显示名称、类型、状态、描述）
3. 使用筛选器按类型/状态筛选
4. 使用搜索框搜索能力
5. 点击能力查看详情

### API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/capabilities` | 获取能力列表 |
| GET | `/api/v1/capabilities/types` | 获取能力类型 |
| GET | `/api/v1/capabilities/stats` | 获取能力统计 |

### 页面跳转
- 点击能力 → `capability-detail.html`
- 点击"发现更多" → `capability-discovery.html`
- 点击"我的能力" → `my-capabilities.html`

---

## 二、能力发现

### 页面路径
`/console/pages/capability-discovery.html`

### 功能说明
从本地、GitHub、Gitee 等来源发现新的能力，支持安装和激活。

### 用户故事
> 作为用户，我希望发现新的能力，以便扩展系统功能。

### 操作流程
1. 进入能力发现页面
2. 选择发现来源（本地/GitHub/Gitee）
3. 浏览发现的能力列表
4. 查看能力详情
5. 选择能力进行激活

### API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/discovery/local` | 本地发现 |
| POST | `/api/v1/discovery/github` | GitHub发现 |
| POST | `/api/v1/discovery/gitee` | Gitee发现 |

### 页面跳转
- 选择能力激活 → `capability-activation.html`

---

## 三、能力激活

### 页面路径
`/console/pages/capability-activation.html`

### 功能说明
激活已发现的能力，配置必要参数，完成能力启用。

### 用户故事
> 作为用户，我希望激活能力，以便在场景中使用它们。

### 操作流程
1. 进入能力激活页面
2. 查看激活流程（5步）
3. 获取激活密钥
4. 配置必要参数
5. 执行网络动作
6. 确认激活

### API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/activations/{id}/process` | 获取激活流程 |
| POST | `/api/v1/activations/{id}/start` | 开始激活 |
| POST | `/api/v1/activations/{id}/key` | 获取密钥 |
| POST | `/api/v1/activations/{id}/activate` | 确认激活 |
| POST | `/api/v1/activations/{id}/cancel` | 取消激活 |

### 页面跳转
- 绑定到场景组 → `capability-binding.html`

---

## 四、能力绑定

### 页面路径
`/console/pages/capability-binding.html`

### 功能说明
将已激活的能力绑定到场景组，使场景组可以使用该能力。

### 用户故事
> 作为用户，我希望将能力绑定到场景组，以便场景组可以使用该能力。

### 操作流程
1. 进入能力绑定页面
2. 选择要绑定的能力
3. 选择目标场景组
4. 配置绑定参数
5. 确认绑定

### 页面跳转
- 选择场景组 → `scene-group-management.html`

---

## 五、我的能力

### 页面路径
`/console/pages/my-capabilities.html`

### 功能说明
展示用户已激活的能力，支持查看详情、修改配置、停用能力。

### 用户故事
> 作为用户，我希望管理我已激活的能力，以便查看状态和修改配置。

### 操作流程
1. 进入我的能力页面
2. 查看已激活的能力列表
3. 查看能力使用统计
4. 修改能力配置
5. 停用不需要的能力

### 页面跳转
- 查看详情 → `capability-detail.html`

---

## 六、能力详情

### 页面路径
`/console/pages/capability-detail.html`

### 功能说明
展示单个能力的详细信息，包括描述、参数、使用示例等。

### 用户故事
> 作为用户，我希望查看能力详情，以便了解如何使用该能力。

### 操作流程
1. 从能力列表或我的能力进入
2. 查看能力基本信息
3. 查看输入输出参数
4. 查看使用示例
5. 进行激活或配置操作

### API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/capabilities/{id}` | 获取能力详情 |

---

## 能力类型说明

| 类型 | 代码 | 说明 |
|------|------|------|
| 驱动能力 | driver | 连接外部系统/服务的能力 |
| 工具能力 | tool | 执行特定任务的能力 |
| LLM能力 | llm | 大语言模型相关能力 |
| 知识能力 | knowledge | 知识检索相关能力 |
| 场景能力 | scene | 场景相关能力 |

## 能力状态说明

| 状态 | 代码 | 说明 |
|------|------|------|
| 草稿 | DRAFT | 能力已注册但未激活 |
| 待激活 | PENDING | 能力等待激活 |
| 已激活 | ACTIVE | 能力已激活可用 |
| 已停用 | INACTIVE | 能力已停用 |
| 错误 | ERROR | 能力激活失败 |
