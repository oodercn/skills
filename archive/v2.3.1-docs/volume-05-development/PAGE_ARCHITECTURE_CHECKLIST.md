# 新开发页面架构检查列表

## 一、CSS 架构检查

### 1.1 Nexus CSS 类名规范

| 检查项 | 规范 | 状态 |
|--------|------|------|
| 布局容器 | `.nx-app`, `.nx-page__sidebar`, `.nx-page__content`, `.nx-page__header`, `.nx-page__main` | ⚠️ 需检查 |
| 卡片组件 | `.nx-card`, `.nx-card__header`, `.nx-card__body`, `.nx-card__title` | ✅ |
| 表格组件 | `.nx-table` | ✅ |
| 表单组件 | `.nx-form-group`, `.nx-input`, `.nx-select` | ✅ |
| 按钮组件 | `.nx-btn`, `.nx-btn--primary`, `.nx-btn--ghost`, `.nx-btn--sm` | ✅ |
| 徽章组件 | `.nx-badge`, `.nx-badge--success`, `.nx-badge--danger`, `.nx-badge--warning` | ✅ |
| 模态框组件 | `.nx-modal`, `.nx-modal__content`, `.nx-modal__header`, `.nx-modal__body`, `.nx-modal__footer` | ✅ |
| 网格系统 | `.nx-grid`, `.nx-grid-cols-2`, `.nx-grid-cols-3`, `.nx-grid-cols-4` | ✅ |
| 工具类 | `.nx-flex`, `.nx-items-center`, `.nx-gap-3`, `.nx-mb-6` | ⚠️ 需检查 |

### 1.2 发现的 CSS 问题

| 页面 | 问题 | 修复方案 |
|------|------|---------|
| participants.html | 使用内联样式 | 抽取到公共CSS |
| capabilities.html | 使用内联样式 | 抽取到公共CSS |
| knowledge-bindings.html | 使用内联样式 | 抽取到公共CSS |
| agent/list.html | 使用内联样式 | 抽取到公共CSS |
| agent/detail.html | 使用内联样式 | 抽取到公共CSS |
| agent/topology.html | 自定义CSS | 抽取到公共CSS |

---

## 二、JS 架构检查

### 2.1 Nexus JS 模块规范

| 检查项 | 规范 | 状态 |
|--------|------|------|
| API调用 | 使用 `NX.api.get/post/put/delete` | ⚠️ 部分使用自定义 |
| 通知提示 | 使用 `NX.notify/success/error` | ⚠️ 使用alert |
| 日期格式化 | 使用 `NX.formatDate` | ⚠️ 使用原生 |
| URL参数 | 使用 `NX.getQueryParam` | ⚠️ 使用原生URLSearchParams |
| 存储操作 | 使用 `NX.storage.get/set` | ✅ |

### 2.2 发现的 JS 问题

| 问题 | 页面数 | 修复方案 |
|------|--------|---------|
| 使用alert而非NX.notify | 全部 | 替换为NX.notify |
| 使用原生Date格式化 | 全部 | 替换为NX.formatDate |
| 使用原生URLSearchParams | 全部 | 替换为NX.getQueryParam |
| 重复的getStatusBadge函数 | 多个 | 抽取到公共JS |
| 重复的getTypeBadge函数 | 多个 | 抽取到公共JS |
| 重复的formatSize函数 | 多个 | 使用NX.formatFileSize |

---

## 三、API DTO 检查

### 3.1 场景组 API DTO

| API | DTO | 状态 |
|-----|-----|------|
| GET /api/v1/scene-groups | SceneGroupDTO | ✅ 存在 |
| POST /api/v1/scene-groups | SceneGroupCreateDTO | ⚠️ 需确认 |
| GET /api/v1/scene-groups/{id}/participants | SceneParticipantDTO | ✅ 存在 |
| POST /api/v1/scene-groups/{id}/participants | ParticipantAddDTO | ⚠️ 需确认 |
| GET /api/v1/scene-groups/{id}/capabilities | CapabilityBindingDTO | ✅ 存在 |
| POST /api/v1/scene-groups/{id}/capabilities | CapabilityBindingCreateDTO | ⚠️ 需确认 |
| GET /api/v1/scene-groups/{id}/knowledge | KnowledgeBindingDTO | ⚠️ 需确认 |
| GET /api/v1/scene-groups/{id}/llm/config | SceneLlmConfigDTO | ✅ 存在 |

### 3.2 Agent API DTO

| API | DTO | 状态 |
|-----|-----|------|
| GET /api/agent/list | AgentDTO | ⚠️ 需确认 |
| GET /api/agent/{id} | AgentDetailDTO | ⚠️ 需确认 |
| GET /api/agent/stats | AgentStatsDTO | ⚠️ 需确认 |

### 3.3 知识库 API DTO

| API | DTO | 状态 |
|-----|-----|------|
| GET /api/v1/knowledge-bases | KnowledgeBaseDTO | ✅ 存在 |
| POST /api/v1/knowledge-bases/{id}/documents | DocumentUploadDTO | ⚠️ 需确认 |
| POST /api/v1/knowledge-bases/{id}/index | IndexRequestDTO | ⚠️ 需确认 |

### 3.4 LLM API DTO

| API | DTO | 状态 |
|-----|-----|------|
| GET /api/v1/llm/providers | LlmProviderDTO | ✅ 存在 |
| GET /api/v1/llm/providers/{id} | LlmProviderDetailDTO | ✅ 存在 |
| PUT /api/v1/llm/providers/{id} | LlmProviderUpdateDTO | ⚠️ 需确认 |

---

## 四、公共 JS 方法抽取

### 4.1 需要抽取的方法

| 方法名 | 说明 | 使用页面数 |
|--------|------|-----------|
| getStatusBadge(status) | 获取状态徽章类名 | 5+ |
| getAgentTypeBadge(type) | 获取Agent类型徽章类名 | 3 |
| formatSize(bytes) | 格式化文件大小 | 2 |
| showConfirm(message, onConfirm) | 显示确认对话框 | 5+ |
| loadSceneGroupName(id) | 加载场景组名称 | 4 |

### 4.2 新增公共方法

```javascript
// scene-utils.js
NX.sceneUtils = {
    getStatusBadge(status) {
        const badges = {
            'SUCCESS': 'success', 'FAILED': 'danger', 'RUNNING': 'warning',
            'ACTIVE': 'success', 'INACTIVE': 'danger', 'PENDING': 'warning',
            'ONLINE': 'success', 'OFFLINE': 'danger', 'BUSY': 'warning'
        };
        return badges[status] || 'default';
    },
    
    getAgentTypeBadge(type) {
        const badges = {
            'MAIN': 'primary', 'LLM': 'success', 'WORKER': 'info',
            'COORDINATOR': 'warning', 'PLATFORM': 'default', 'ASSISTANT': 'default'
        };
        return badges[type] || 'default';
    },
    
    getLinkTypeBadge(type) {
        const badges = {
            'DIRECT': 'success', 'RELAY': 'info', 
            'TUNNEL': 'warning', 'MULTICAST': 'primary'
        };
        return badges[type] || 'default';
    }
};
```

---

## 五、公共 CSS 样式抽取

### 5.1 需要抽取的样式

| 样式类 | 说明 | 使用页面数 |
|--------|------|-----------|
| .scene-page-header | 场景页面头部 | 5+ |
| .scene-back-link | 返回链接 | 5+ |
| .stat-card | 统计卡片 | 4 |
| .quick-link | 快捷入口 | 2 |

### 5.2 新增公共样式

```css
/* scene-pages.css */
.scene-back-link {
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--nx-text-secondary);
    text-decoration: none;
    margin-bottom: 16px;
}

.scene-back-link:hover {
    color: var(--nx-primary);
}

.stat-card {
    background: var(--nx-bg-card);
    border-radius: var(--nx-radius);
    border: 1px solid var(--nx-border);
    padding: 20px;
    text-align: center;
}

.stat-card__value {
    font-size: 24px;
    font-weight: bold;
}

.stat-card__label {
    color: var(--nx-text-secondary);
    font-size: 14px;
}
```

---

## 六、修复优先级

| 优先级 | 任务 | 页面数 |
|--------|------|--------|
| P1 | 替换alert为NX.notify | 全部 |
| P1 | 抽取公共JS方法 | 新建scene-utils.js |
| P1 | 抽取公共CSS样式 | 新建scene-pages.css |
| P2 | 确认API DTO定义 | 后端检查 |
| P2 | 统一URL参数获取方式 | 全部 |
| P3 | 优化内联样式 | 全部 |
