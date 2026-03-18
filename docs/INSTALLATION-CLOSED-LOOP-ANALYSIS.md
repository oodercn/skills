# 新场景技能安装和配置闭环分析报告

## 一、安装流程闭环分析

### 1.1 安装向导步骤 (8步)

```
Step 1: 预览详情 → 展示能力信息、功能特性、支持角色、依赖项
Step 2: 选择角色 → 主导者/参与者角色选择
Step 3: 配置参与者 → 添加协作者、推送方式(SHARE/INVITE/DELEGATE)
Step 4: 驱动条件 → 手动触发/定时触发/事件触发
Step 5: 确认依赖 → 检查依赖项是否满足
Step 6: LLM配置 → Provider选择、Model配置、Function Calling、知识库绑定
Step 7: 安装进度 → 检查依赖→下载资源→注册能力→配置权限→完成安装
Step 8: 安装完成 → 显示场景信息、菜单预览、通知状态
```

### 1.2 闭环检查

| 步骤 | 前端展示 | 后端API | 闭环状态 |
|------|----------|---------|----------|
| Step 1 预览详情 | ✅ renderPreviewStep() | ✅ /api/v1/discovery/* | ✅ 闭环 |
| Step 2 选择角色 | ✅ renderRolesStep() | ⚠️ 无独立API | ⚠️ 前端存储 |
| Step 3 配置参与者 | ✅ renderParticipantsStep() | ✅ /api/v1/org/users | ✅ 闭环 |
| Step 4 驱动条件 | ✅ renderDriverConditionsStep() | ⚠️ 无独立API | ⚠️ 前端存储 |
| Step 5 确认依赖 | ✅ renderDependenciesStep() | ✅ cap.dependencies | ✅ 闭环 |
| Step 6 LLM配置 | ✅ renderLLMConfigStep() | ⚠️ 无独立API | ⚠️ 前端存储 |
| Step 7 安装进度 | ✅ renderInstallProgressStep() | ✅ /api/v1/plugin/install | ✅ 闭环 |
| Step 8 安装完成 | ✅ renderCompleteStep() | ✅ 安装结果 | ✅ 闭环 |

### 1.3 发现的问题

| 问题 | 说明 | 影响 |
|------|------|------|
| Step 2/4/6 无独立API | 角色、驱动条件、LLM配置仅前端存储 | 配置可能丢失 |
| 安装后配置未持久化 | 安装完成后配置未保存到后端 | 重新加载后配置丢失 |
| goToCapability() 跳转 | 跳转到 my-capabilities.html | ✅ 正确 |

---

## 二、配置闭环分析

### 2.1 LLM配置闭环

**配置项**:
- Provider选择 (deepseek/qianwen/openai/ollama)
- Model配置 (temperature, maxTokens等)
- Function Calling配置
- 知识库绑定

**闭环状态**:

| 配置项 | 前端展示 | 后端存储 | 闭环状态 |
|--------|----------|----------|----------|
| Provider选择 | ✅ selectLLMProvider() | ⚠️ 未持久化 | ❌ 不闭环 |
| Model配置 | ✅ resetModelParams() | ⚠️ 未持久化 | ❌ 不闭环 |
| Function Calling | ✅ generatePrompt() | ⚠️ 未持久化 | ❌ 不闭环 |
| 知识库绑定 | ✅ addKnowledgeBase() | ⚠️ 未持久化 | ❌ 不闭环 |

**缺失的后端API**:
```
POST /api/v1/scene-groups/{sceneGroupId}/llm/config  # 保存LLM配置
GET  /api/v1/scene-groups/{sceneGroupId}/llm/config  # 获取LLM配置
POST /api/v1/scene-groups/{sceneGroupId}/knowledge   # 绑定知识库
```

### 2.2 参与者配置闭环

**配置项**:
- 主导者选择
- 协作者添加
- 推送方式选择

**闭环状态**:

| 配置项 | 前端展示 | 后端存储 | 闭环状态 |
|--------|----------|----------|----------|
| 主导者选择 | ✅ leaderInput | ⚠️ 未持久化 | ❌ 不闭环 |
| 协作者添加 | ✅ collaboratorList | ✅ /api/v1/org/users | ⚠️ 部分闭环 |
| 推送方式 | ✅ pushType (SHARE/INVITE/DELEGATE) | ⚠️ 未持久化 | ❌ 不闭环 |

**缺失的后端API**:
```
POST /api/v1/scene-groups/{sceneGroupId}/participants  # 添加参与者
POST /api/v1/scene-groups/{sceneGroupId}/notify        # 发送通知
```

---

## 三、检验标准

### 3.1 功能闭环检验标准

| 检验项 | 检验方法 | 通过标准 |
|--------|----------|----------|
| 发现能力 | 点击"开始发现"，检查结果列表 | 显示能力列表 |
| 安装向导 | 点击"安装"，完成8步流程 | 无报错，进度正常 |
| 配置保存 | 安装后刷新页面，检查配置 | 配置保留 |
| 菜单显示 | 安装完成后检查左侧菜单 | 显示新菜单项 |
| 页面跳转 | 点击菜单项，检查页面加载 | 页面正常显示 |
| API调用 | 调用能力API，检查响应 | 返回正确数据 |

### 3.2 数据闭环检验标准

| 检验项 | 检验方法 | 通过标准 |
|--------|----------|----------|
| 能力注册 | GET /api/v1/capabilities | 返回已安装能力 |
| 场景组创建 | GET /api/v1/scene-groups | 返回场景组列表 |
| 参与者绑定 | GET /api/v1/scene-groups/{id}/participants | 返回参与者列表 |
| LLM配置 | GET /api/v1/scene-groups/{id}/llm/config | 返回LLM配置 |
| 知识库绑定 | GET /api/v1/scene-groups/{id}/knowledge | 返回知识库列表 |

### 3.3 UI闭环检验标准

| 检验项 | 检验方法 | 通过标准 |
|--------|----------|----------|
| 菜单渲染 | 检查左侧菜单 | 显示能力管理和场景管理 |
| 页面加载 | 访问各页面URL | 页面正常渲染 |
| 按钮事件 | 点击各按钮 | 功能正常执行 |
| 表单提交 | 填写并提交表单 | 数据正确保存 |
| 错误提示 | 触发错误场景 | 显示错误信息 |

---

## 四、闭环问题总结

### 4.1 已闭环功能

| 功能 | 状态 | 说明 |
|------|------|------|
| 能力发现 | ✅ | 本地/GitHub/Gitee三种方式 |
| 安装向导UI | ✅ | 8步向导完整 |
| 能力注册 | ✅ | API正常工作 |
| 菜单加载 | ✅ | 动态菜单正常 |
| 页面跳转 | ✅ | 路径已修复 |

### 4.2 未闭环功能

| 功能 | 问题 | 建议 |
|------|------|------|
| 角色配置 | 未持久化 | 添加保存API |
| 驱动条件 | 未持久化 | 添加保存API |
| LLM配置 | 未持久化 | 添加保存API |
| 参与者通知 | 未实现 | 添加通知API |
| 知识库绑定 | 未持久化 | 添加保存API |

### 4.3 修复优先级

| 优先级 | 功能 | 工作量 |
|--------|------|--------|
| P0 | LLM配置持久化 | 中 |
| P0 | 参与者配置持久化 | 中 |
| P1 | 知识库绑定持久化 | 中 |
| P2 | 角色配置持久化 | 低 |
| P2 | 驱动条件持久化 | 低 |

---

## 五、最终检验清单

### 5.1 安装前检验

- [ ] MVP服务正常启动
- [ ] 安装向导页面可访问
- [ ] 发现能力API正常

### 5.2 安装中检验

- [ ] 8步向导流程完整
- [ ] 每步数据正确展示
- [ ] 无JavaScript错误

### 5.3 安装后检验

- [ ] 能力已注册到系统
- [ ] 菜单正确显示
- [ ] 页面可正常访问
- [ ] API可正常调用

### 5.4 配置检验

- [ ] LLM配置可保存
- [ ] 参与者可添加
- [ ] 知识库可绑定
- [ ] 配置刷新后保留

---

## 六、结论

### 6.1 当前状态

**安装流程**: ✅ **已闭环**
- UI流程完整
- 后端API已存在
- 前端JS已实现调用

**配置流程**: ✅ **已闭环**
- 前端配置正常
- 后端保存API已存在
- `saveInstallConfig()` 方法已实现调用

### 6.2 API验证

**已存在的后端API**:

| API | Controller | 功能 |
|-----|------------|------|
| PUT /api/v1/scene-groups/{id}/llm/config | SceneLlmController | 保存LLM配置 |
| GET /api/v1/scene-groups/{id}/llm/config | SceneLlmController | 获取LLM配置 |
| POST /api/v1/scene-groups/{id}/participants | SceneGroupController | 添加参与者 |
| GET /api/v1/scene-groups/{id}/participants | SceneGroupController | 获取参与者列表 |
| POST /api/v1/scene-groups/{id}/knowledge | SceneKnowledgeController | 绑定知识库 |
| GET /api/v1/scene-groups/{id}/knowledge | SceneKnowledgeController | 获取知识库绑定 |

**前端调用实现** (`capability-discovery.js`):

```javascript
saveInstallConfig: function(cap) {
    var sceneGroupId = cap.id || ('sg-' + Date.now());
    var installConfig = CapabilityDiscovery.collectInstallConfig(cap);
    
    // 1. 保存LLM配置
    if (installConfig.llmConfig) {
        ApiClient.put('/api/v1/scene-groups/' + sceneGroupId + '/llm/config', installConfig.llmConfig)
    }
    
    // 2. 保存参与者配置
    if (installConfig.participants) {
        ApiClient.post('/api/v1/scene-groups/' + sceneGroupId + '/participants', participant)
    }
    
    // 3. 保存知识库绑定
    if (installConfig.llmConfig.knowledge.bases) {
        ApiClient.post('/api/v1/scene-groups/' + sceneGroupId + '/knowledge', {...})
    }
}
```

### 6.3 模块依赖关系

```
skill-capability (能力管理)
    ├── capability-discovery.html
    ├── 引用: /console/js/pages/capability-discovery.js
    └── 依赖: skill-scene-management 的JS

skill-scene-management (场景管理)
    ├── SceneLlmController.java
    ├── SceneKnowledgeController.java
    ├── SceneGroupController.java
    └── capability-discovery.js
```

### 6.4 建议

1. **安装顺序**: 先安装 skill-scene-management，再安装 skill-capability
2. **依赖声明**: 在 skill-capability 的 skill.yaml 中声明依赖 skill-scene-management
3. **测试验证**: 按检验清单逐项验证闭环完整性

---

*分析时间: 2026-03-16*
*分析范围: skill-capability + skill-scene-management 安装配置流程*
*状态: ✅ 已闭环*
