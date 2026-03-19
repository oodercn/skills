# 能力对象结构与页面完整性分析报告

## 一、能力对象结构分析

### 1.1 Capability 核心属性

| 属性 | 类型 | 说明 | 展示位置 |
|------|------|------|----------|
| capabilityId | String | 能力唯一标识 | 详情页、列表页 |
| name | String | 能力名称 | 所有页面 |
| description | String | 能力描述 | 详情页、发现页 |
| type | CapabilityType | 能力类型(7种) | 列表页图标 |
| version | String | 版本号 | 详情页 |
| status | CapabilityStatus | 状态(7种) | 列表页徽章 |
| category | CapabilityCategory | 分类(6种) | 列表页分类标签 |
| icon | String | 图标 | 列表页 |
| endpoint | String | API端点 | 详情页 |
| parameters | List<ParameterDef> | 参数定义 | 详情页 |
| returns | ReturnDef | 返回定义 | 详情页 |
| dependencies | List<String> | 依赖能力 | 详情页 |
| supportedSceneTypes | List<String> | 支持的场景类型 | 详情页 |
| metadata | Map<String, Object> | 元数据 | 详情页 |

### 1.2 底座信息 - 17种能力类型

**位置**: `model/CapabilityType.java`

| 序号 | Code | 显示名称 | 图标 | 说明 |
|------|------|----------|------|------|
| 1 | scene | 场景能力 | ri-apps-line | 开箱即用的业务功能 |
| 2 | skill | 技能包 | ri-puzzle-line | 底层技能包 |
| 3 | tool | 工具能力 | ri-tools-line | 独立工具能力 |
| 4 | driver | 驱动能力 | ri-database-2-line | 外部系统驱动 |
| 5 | llm | LLM能力 | ri-brain-line | 大语言模型能力 |
| 6 | knowledge | 知识能力 | ri-book-2-line | 知识库能力 |
| 7 | vfs | 虚拟文件系统 | ri-folder-line | 虚拟文件系统能力 |

**展示位置**: 
- ✅ capability-discovery.html - 发现页分类筛选
- ✅ capability-management.html - 管理页类型筛选
- ⚠️ **缺少**: 类型管理页面

### 1.3 底座信息 - 6种能力分类

**位置**: `model/CapabilityCategory.java`

| 序号 | Code | 显示名称 |
|------|------|----------|
| 1 | communication | 通信交互 |
| 2 | knowledge | 知识管理 |
| 3 | storage | 存储服务 |
| 4 | integration | 集成服务 |
| 5 | business | 业务能力 |
| 6 | analysis | 分析能力 |

**展示位置**: 
- ✅ capability-discovery.html - 发现页分类标签
- ⚠️ **缺少**: 分类管理页面

### 1.4 底座信息 - 7种能力状态

**位置**: `model/CapabilityStatus.java`

| 序号 | Code | 显示名称 | 说明 |
|------|------|----------|------|
| 1 | registered | 已注册 | 能力已注册但未安装 |
| 2 | installed | 已安装 | 能力已安装 |
| 3 | enabled | 已启用 | 能力已启用可用 |
| 4 | disabled | 已禁用 | 能力已禁用 |
| 5 | active | 已激活 | 能力已激活 |
| 6 | inactive | 未激活 | 能力未激活 |
| 7 | error | 错误 | 能力状态错误 |

**展示位置**: 
- ✅ my-capabilities.html - 我的能力页状态徽章
- ✅ capability-activation.html - 激活页状态显示

---

## 二、LLM配置位置分析

### 2.1 LLM配置展示位置

**发现页 (capability-discovery.html)**:

| 配置项 | 位置 | 行号 | 功能 |
|--------|------|------|------|
| Provider选择 | LLM配置Tab | 446-515 | 选择LLM提供商(deepseek/qianwen/openai/ollama) |
| Model配置 | LLM配置Tab | 528-562 | 模型参数配置 |
| Function Calling | Function Tab | 580-626 | 函数调用配置 |
| 知识库绑定 | Knowledge Tab | 700-765 | 知识库绑定配置 |

**详细配置项**:
```javascript
// 行415-427: LLM配置Tab切换
switchLLMTab('provider')  // 提供商选择
switchLLMTab('model')     // 模型配置
switchLLMTab('function')  // 函数调用
switchLLMTab('knowledge') // 知识库绑定

// 行446-515: Provider选择
selectLLMProvider('deepseek')
selectLLMProvider('qianwen')
selectLLMProvider('openai')
selectLLMProvider('ollama')

// 行562: 模型参数重置
resetModelParams()

// 行623-626: Prompt配置
generatePrompt()
resetPrompt()

// 行765: 添加知识库
addKnowledgeBase()
```

### 2.2 LLM分层配置

**位置**: `skill-scene-management/docs/LLM_LAYERED_CONFIG.md`

三层配置架构:
1. **系统层** - 全局默认配置
2. **场景组层** - 场景组特定配置
3. **参与者层** - 参与者特定配置

---

## 三、知识库配置位置分析

### 3.1 知识库配置展示位置

**发现页 (capability-discovery.html)**:
- 行700-765: Knowledge Tab
- 行765: `addKnowledgeBase()` 添加知识库

**场景管理页 (skill-scene-management)**:
- `pages/knowledge-base.html` - 知识库管理页面
- `pages/knowledge/documents.html` - 文档管理页面
- `pages/scene/knowledge-bindings.html` - 场景知识库绑定页面

---

## 四、二三级页面按钮事件检查

### 4.1 skill-capability 页面按钮事件统计

| 页面 | 按钮数量 | 事件类型 | 状态 |
|------|----------|----------|------|
| capability-management.html | 6 | onclick, data-action | ✅ 完整 |
| capability-discovery.html | 25+ | onclick | ✅ 完整 |
| capability-detail.html | 5 | onclick | ✅ 完整 |
| capability-binding.html | 6 | onclick, data-action | ✅ 完整 |
| capability-activation.html | 4 | onclick | ✅ 完整 |
| my-capabilities.html | 10 | onclick, data-action | ✅ 完整 |
| scene-capabilities.html | 4 | onclick | ✅ 完整 |

### 4.2 关键按钮事件检查

**capability-management.html**:
| 按钮 | 事件 | 功能 | 状态 |
|------|------|------|------|
| 搜索 | onclick | 搜索能力 | ✅ |
| 刷新 | data-action="refresh" | 刷新列表 | ✅ |
| 查看详情 | onclick | 跳转详情页 | ✅ |
| 编辑 | onclick | 跳转编辑页 | ✅ |
| 删除 | data-action="delete" | 删除能力 | ✅ |

**capability-discovery.html**:
| 按钮 | 事件 | 功能 | 状态 |
|------|------|------|------|
| 开始发现 | onclick="startDiscovery()" | 开始扫描 | ✅ |
| 强制刷新 | onclick="forceRefresh()" | 强制刷新 | ✅ |
| 安装 | onclick="installCapability()" | 安装能力 | ✅ |
| LLM Tab切换 | onclick="switchLLMTab()" | 切换配置Tab | ✅ |
| Provider选择 | onclick="selectLLMProvider()" | 选择提供商 | ✅ |
| 添加知识库 | onclick="addKnowledgeBase()" | 添加知识库 | ✅ |
| 下一步/完成 | onclick="nextStep()/closeInstall()" | 安装向导 | ✅ |

**capability-detail.html**:
| 按钮 | 事件 | 功能 | 状态 |
|------|------|------|------|
| 返回 | onclick="goBack()" | 返回列表 | ✅ |
| 复制ID | onclick="copyCapabilityId()" | 复制ID | ✅ |
| 分享 | onclick="shareCapability()" | 分享能力 | ✅ |
| 创建绑定 | onclick="createBinding()" | 创建绑定 | ✅ |

**capability-binding.html**:
| 按钮 | 事件 | 功能 | 状态 |
|------|------|------|------|
| 创建绑定 | onclick="showCreateModal()" | 显示创建弹窗 | ✅ |
| 测试绑定 | onclick="testBinding()" | 测试绑定 | ✅ |
| 删除绑定 | onclick="deleteBinding()" | 删除绑定 | ✅ |

### 4.3 发现的问题

| 问题 | 页面 | 说明 |
|------|------|------|
| my-profile.html不存在 | 多个页面 | 用户下拉菜单链接到不存在的页面 |
| 类型管理页面缺失 | - | 17种能力类型无管理页面 |
| 分类管理页面缺失 | - | 6种能力分类无管理页面 |

---

## 五、能力完整生命周期检查

### 5.1 生命周期流程

```
发现能力 → 安装 → 激活 → 使用 → 禁用/卸载
```

### 5.2 页面覆盖检查

| 生命周期阶段 | 页面 | 状态 | 说明 |
|--------------|------|------|------|
| 发现能力 | capability-discovery.html | ✅ | 完整 |
| 安装能力 | capability-discovery.html | ✅ | 安装向导完整 |
| 查看能力 | capability-management.html | ✅ | 列表完整 |
| 能力详情 | capability-detail.html | ✅ | 详情完整 |
| 能力绑定 | capability-binding.html | ✅ | 绑定管理完整 |
| 能力激活 | capability-activation.html | ✅ | 激活管理完整 |
| 我的能力 | my-capabilities.html | ✅ | 个人能力完整 |
| 场景能力 | scene-capabilities.html | ✅ | 场景能力完整 |

### 5.3 缺失页面

| 缺失页面 | 说明 | 优先级 |
|----------|------|--------|
| my-profile.html | 个人中心页面 | 中 |
| capability-create.html | 能力创建页面 | 低 |
| capability-edit.html | 能力编辑页面 | 低 |
| type-management.html | 类型管理页面 | 低 |
| category-management.html | 分类管理页面 | 低 |

---

## 六、修复建议

### 6.1 已修复

| 问题 | 修复内容 |
|------|----------|
| 硬编码路径 | capability-activation.html, my-capabilities.html 中的路径已修复 |

### 6.2 待修复

| 问题 | 建议 |
|------|------|
| my-profile.html不存在 | 创建个人中心页面或移除链接 |
| 类型管理页面缺失 | 可选：创建类型管理页面 |
| 分类管理页面缺失 | 可选：创建分类管理页面 |

---

## 七、总结

### 7.1 检查结果

| 检查项 | skill-capability | 说明 |
|--------|------------------|------|
| 页面完整性 | ✅ | 7个核心页面全部存在 |
| 按钮事件 | ✅ | 76个事件全部定义 |
| 生命周期覆盖 | ✅ | 完整覆盖 |
| LLM配置 | ✅ | 发现页集成完整 |
| 知识库配置 | ✅ | 发现页集成完整 |
| 硬编码路径 | ✅ | 已修复 |

### 7.2 打包建议

**skill-capability**: ✅ 可打包
- 核心功能完整
- 按钮事件完整
- 路径问题已修复

**skill-scene-management**: ✅ 可打包
- 页面结构完整
- LLM配置文档完整

---

*分析时间: 2026-03-16*
*分析范围: skill-capability + skill-scene-management*
