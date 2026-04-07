# 场景驱动表单架构设计 - A2UI 标准化分离方案

**文档版本**: v1.0  
**创建日期**: 2026-04-06  
**项目路径**: E:\github\ooder-skills  
**协作文档输出路径**: E:\github\ooder-skills\docs\场景驱动表单架构设计-A2UI标准化.md

---

## 一、概述

本文档基于 Ooder 框架和场景驱动模型，提出了一套将表单应用程序分解为 A2UI、A2A、P2A、P2P 四层架构的标准化方案。该方案将页面本身的端点、动作、事件进行 A2UI 标准化分离，实现 UI 组件与业务逻辑的完全解耦。

---

## 二、核心概念

### 2.1 四层架构定义

#### 2.1.1 A2UI (Agent-to-UI)
**定义**: Agent 与 UI 之间的通信协议层

**职责**:
- UI 组件的标准化定义
- UI 事件的标准化处理
- UI 数据的标准化绑定
- UI 状态的标准化管理

**核心特征**:
- 基于 Ooder 框架的组件化设计
- 使用 `ood.Class()` 定义组件
- 使用 `ood.create()` 实例化组件
- 组件属性、事件、方法的标准化声明

#### 2.1.2 A2A (Agent-to-Agent)
**定义**: Agent 之间的通信协议层

**职责**:
- Agent 间的消息传递
- Agent 间的任务协作
- Agent 间的数据共享
- Agent 间的状态同步

**核心特征**:
- 基于 A2AProtocolService 的消息路由
- 支持请求-响应模式
- 支持发布-订阅模式
- 支持广播消息

#### 2.1.3 P2A (Person-to-Agent)
**定义**: 人与 Agent 之间的交互协议层

**职责**:
- 用户请求的接收和解析
- 用户意图的理解和转换
- Agent 响应的生成和返回
- 用户会话的管理和维护

**核心特征**:
- 基于 AgentChatService 的对话管理
- 支持多轮对话
- 支持上下文保持
- 支持知识库增强

#### 2.1.4 P2P (Person-to-Person)
**定义**: 人与人之间的协作协议层

**职责**:
- 用户间的实时通信
- 用户间的任务分配
- 用户间的权限管理
- 用户间的协作流程

**核心特征**:
- 基于 P2PServiceImpl 的节点发现
- 支持消息广播
- 支持心跳检测
- 支持网络拓扑管理

---

## 三、现有表单系统分析

### 3.1 端点分析

**当前实现**:
```javascript
// 直接在 JavaScript 中调用 API
const approvalData = {
    id: 'APP-' + new Date().toISOString().slice(0, 10).replace(/-/g, ''),
    type, priority, reason, approver,
    applicant: '当前用户',
    applyTime: new Date().toLocaleString('zh-CN'),
    status: 'pending'
};
// 直接添加到列表
this.submittedList.unshift(approvalData);
```

**问题**:
- 端点调用与业务逻辑耦合
- 缺乏统一的 API 管理层
- 缺乏错误处理机制
- 缺乏请求/响应标准化

### 3.2 动作分析

**当前实现**:
```html
<!-- 直接在 HTML 中绑定 onclick 事件 -->
<button onclick="ApprovalForm.openCreateModal()">
    <i class="ri-add-line"></i> 发起审批
</button>
<button onclick="ApprovalForm.switchTab('pending')">
    <i class="ri-time-line"></i> 待办审批
</button>
```

**问题**:
- 动作与 UI 组件耦合
- 缺乏动作的标准化定义
- 缺乏动作的权限控制
- 缺乏动作的日志记录

### 3.3 事件分析

**当前实现**:
```javascript
// 直接使用 addEventListener 绑定事件
const fileUpload = document.getElementById('file-upload');
fileUpload.addEventListener('click', () => fileInput.click());
fileUpload.addEventListener('dragover', (e) => {
    e.preventDefault();
    fileUpload.style.borderColor = '#3b82f6';
});
```

**问题**:
- 事件处理与 DOM 元素耦合
- 缺乏事件的标准化流程
- 缺乏事件的冒泡控制
- 缺乏事件的性能优化

---

## 四、A2UI 标准化分离方案

### 4.1 架构总览

```
┌─────────────────────────────────────────────────────────────┐
│                    P2P 层 (人与人协作)                         │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  用户协作管理器 (UserCollaborationManager)            │  │
│  │  - 实时通信  - 任务分配  - 权限管理  - 协作流程      │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    P2A 层 (人与 Agent 交互)                    │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Agent 交互管理器 (AgentInteractionManager)          │  │
│  │  - 意图理解  - 对话管理  - 响应生成  - 会话维护      │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    A2A 层 (Agent 间通信)                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Agent 协作管理器 (AgentCollaborationManager)        │  │
│  │  - 消息路由  - 任务协作  - 数据共享  - 状态同步      │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    A2UI 层 (Agent 与 UI 通信)                  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  UI 组件管理器 (UIComponentManager)                   │  │
│  │  - 组件定义  - 事件处理  - 数据绑定  - 状态管理      │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 A2UI 层标准化设计

#### 4.2.1 UI 组件标准化定义

**基于 Ooder 框架的组件定义规范**:

```javascript
/**
 * 审批表单组件 - 基于 Ooder 框架
 * Approval Form Component - Based on Ooder Framework
 */
ood.Class('ApprovalFormComponent', ood.UI.Widget, {
    // 组件属性定义
    properties: {
        // 审批类型
        approvalType: {
            type: 'string',
            default: '',
            observer: '_onApprovalTypeChange'
        },
        // 审批优先级
        priority: {
            type: 'string',
            default: 'normal',
            observer: '_onPriorityChange'
        },
        // 审批状态
        status: {
            type: 'string',
            default: 'pending',
            observer: '_onStatusChange'
        },
        // 当前审批人
        currentApprover: {
            type: 'string',
            default: ''
        },
        // 审批数据
        approvalData: {
            type: 'object',
            default: {}
        }
    },

    // 组件事件定义
    events: {
        // 提交审批事件
        onSubmit: {},
        // 审批通过事件
        onApprove: {},
        // 审批拒绝事件
        onReject: {},
        // 表单验证事件
        onValidate: {},
        // 数据变更事件
        onDataChange: {}
    },

    // 组件方法定义
    methods: {
        // 初始化
        init: function(config) {
            this._super(config);
            this._initUI();
            this._bindEvents();
            this._loadData();
        },

        // 初始化 UI
        _initUI: function() {
            this._createFormLayout();
            this._createInputFields();
            this._createButtons();
        },

        // 绑定事件
        _bindEvents: function() {
            this._bindFormEvents();
            this._bindButtonEvents();
            this._bindValidationEvents();
        },

        // 加载数据
        _loadData: function() {
            this._loadApprovalTypes();
            this._loadApprovers();
            this._loadTemplates();
        },

        // 提交审批
        submit: function() {
            if (!this._validateForm()) {
                return;
            }
            
            var data = this._collectFormData();
            this.fireEvent('onSubmit', { data: data });
        },

        // 验证表单
        _validateForm: function() {
            var isValid = true;
            var errors = [];
            
            // 验证必填字段
            if (!this.get('approvalType')) {
                errors.push('请选择审批类型');
                isValid = false;
            }
            
            if (!this.get('approvalData.reason')) {
                errors.push('请填写审批事由');
                isValid = false;
            }
            
            if (!this.get('currentApprover')) {
                errors.push('请选择审批人');
                isValid = false;
            }
            
            this.fireEvent('onValidate', { 
                isValid: isValid, 
                errors: errors 
            });
            
            return isValid;
        },

        // 收集表单数据
        _collectFormData: function() {
            return {
                type: this.get('approvalType'),
                priority: this.get('priority'),
                data: this.get('approvalData'),
                approver: this.get('currentApprover')
            };
        }
    }
});
```

#### 4.2.2 端点标准化定义

**API 端点管理器**:

```javascript
/**
 * API 端点管理器 - A2UI 标准化
 * API Endpoint Manager - A2UI Standardization
 */
ood.Class('APIEndpointManager', ood.absObj, {
    properties: {
        baseUrl: {
            type: 'string',
            default: '/api/v1'
        },
        timeout: {
            type: 'number',
            default: 30000
        }
    },

    methods: {
        // 定义审批相关端点
        _defineEndpoints: function() {
            this._endpoints = {
                // 审批表单相关
                approval: {
                    list: {
                        path: '/approval/forms',
                        method: 'GET',
                        description: '获取审批表单列表'
                    },
                    create: {
                        path: '/approval/forms',
                        method: 'POST',
                        description: '创建审批表单'
                    },
                    get: {
                        path: '/approval/forms/{id}',
                        method: 'GET',
                        description: '获取表单详情'
                    },
                    update: {
                        path: '/approval/forms/{id}',
                        method: 'PUT',
                        description: '更新表单'
                    },
                    delete: {
                        path: '/approval/forms/{id}',
                        method: 'DELETE',
                        description: '删除表单'
                    }
                },

                // 审批流程相关
                process: {
                    list: {
                        path: '/approval/processes',
                        method: 'GET',
                        description: '获取审批流程列表'
                    },
                    start: {
                        path: '/approval/processes',
                        method: 'POST',
                        description: '发起审批流程'
                    },
                    approve: {
                        path: '/approval/processes/{id}/approve',
                        method: 'PUT',
                        description: '审批通过'
                    },
                    reject: {
                        path: '/approval/processes/{id}/reject',
                        method: 'PUT',
                        description: '审批拒绝'
                    },
                    transfer: {
                        path: '/approval/processes/{id}/transfer',
                        method: 'PUT',
                        description: '转交审批'
                    }
                },

                // 审批模板相关
                template: {
                    list: {
                        path: '/approval/templates',
                        method: 'GET',
                        description: '获取审批模板列表'
                    },
                    create: {
                        path: '/approval/templates',
                        method: 'POST',
                        description: '创建审批模板'
                    }
                },

                // 审批记录相关
                record: {
                    list: {
                        path: '/approval/records',
                        method: 'GET',
                        description: '获取审批记录'
                    }
                },

                // 审批统计相关
                statistics: {
                    get: {
                        path: '/approval/statistics',
                        method: 'GET',
                        description: '获取审批统计'
                    }
                }
            };
        },

        // 调用端点
        call: function(endpointPath, params, data) {
            var endpoint = this._getEndpoint(endpointPath);
            if (!endpoint) {
                return Promise.reject(new Error('Endpoint not found: ' + endpointPath));
            }

            var url = this._buildUrl(endpoint.path, params);
            var options = {
                method: endpoint.method,
                headers: {
                    'Content-Type': 'application/json'
                }
            };

            if (data && (endpoint.method === 'POST' || endpoint.method === 'PUT')) {
                options.body = JSON.stringify(data);
            }

            return fetch(url, options)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('HTTP error! status: ' + response.status);
                    }
                    return response.json();
                })
                .catch(error => {
                    console.error('API call error:', error);
                    throw error;
                });
        },

        // 获取端点定义
        _getEndpoint: function(path) {
            var parts = path.split('.');
            var current = this._endpoints;
            
            for (var i = 0; i < parts.length; i++) {
                if (!current[parts[i]]) {
                    return null;
                }
                current = current[parts[i]];
            }
            
            return current;
        },

        // 构建 URL
        _buildUrl: function(path, params) {
            var url = this.get('baseUrl') + path;
            
            if (params) {
                for (var key in params) {
                    if (params.hasOwnProperty(key)) {
                        url = url.replace('{' + key + '}', params[key]);
                    }
                }
            }
            
            return url;
        }
    }
});
```

#### 4.2.3 动作标准化定义

**动作管理器**:

```javascript
/**
 * 动作管理器 - A2UI 标准化
 * Action Manager - A2UI Standardization
 */
ood.Class('ActionManager', ood.absObj, {
    properties: {
        // 动作历史记录
        actionHistory: {
            type: 'array',
            default: []
        }
    },

    methods: {
        // 定义审批相关动作
        _defineActions: function() {
            this._actions = {
                // 审批表单动作
                'approval.form.create': {
                    name: '创建审批表单',
                    description: '打开创建审批表单的模态框',
                    handler: this._actionCreateForm,
                    permission: 'approval:create',
                    log: true
                },
                'approval.form.submit': {
                    name: '提交审批',
                    description: '提交审批表单数据',
                    handler: this._actionSubmitForm,
                    permission: 'approval:submit',
                    log: true,
                    validate: true
                },
                'approval.form.cancel': {
                    name: '取消审批',
                    description: '取消当前审批操作',
                    handler: this._actionCancelForm,
                    permission: 'approval:cancel',
                    log: false
                },

                // 审批流程动作
                'approval.process.approve': {
                    name: '审批通过',
                    description: '通过当前审批',
                    handler: this._actionApprove,
                    permission: 'approval:approve',
                    log: true,
                    confirm: true
                },
                'approval.process.reject': {
                    name: '审批拒绝',
                    description: '拒绝当前审批',
                    handler: this._actionReject,
                    permission: 'approval:reject',
                    log: true,
                    confirm: true
                },
                'approval.process.transfer': {
                    name: '转交审批',
                    description: '将审批转交给其他人',
                    handler: this._actionTransfer,
                    permission: 'approval:transfer',
                    log: true
                },

                // 视图切换动作
                'view.tab.switch': {
                    name: '切换标签页',
                    description: '切换到指定的标签页',
                    handler: this._actionSwitchTab,
                    permission: null,
                    log: false
                },
                'view.filter.apply': {
                    name: '应用筛选',
                    description: '应用筛选条件',
                    handler: this._actionApplyFilter,
                    permission: null,
                    log: false
                },

                // 数据导出动作
                'data.export': {
                    name: '导出数据',
                    description: '导出审批数据报表',
                    handler: this._actionExportData,
                    permission: 'approval:export',
                    log: true
                }
            };
        },

        // 执行动作
        execute: function(actionName, params) {
            var action = this._actions[actionName];
            if (!action) {
                console.error('Action not found:', actionName);
                return Promise.reject(new Error('Action not found: ' + actionName));
            }

            // 检查权限
            if (action.permission && !this._checkPermission(action.permission)) {
                console.warn('Permission denied:', action.permission);
                return Promise.reject(new Error('Permission denied'));
            }

            // 验证参数
            if (action.validate && !this._validateParams(params)) {
                console.warn('Invalid parameters:', params);
                return Promise.reject(new Error('Invalid parameters'));
            }

            // 确认操作
            if (action.confirm && !this._confirmAction(action)) {
                console.log('Action cancelled by user');
                return Promise.resolve({ cancelled: true });
            }

            // 记录日志
            if (action.log) {
                this._logAction(actionName, params);
            }

            // 执行动作
            try {
                var result = action.handler.call(this, params);
                
                // 添加到历史记录
                this._addToHistory(actionName, params, result);
                
                return Promise.resolve(result);
            } catch (error) {
                console.error('Action execution error:', error);
                return Promise.reject(error);
            }
        },

        // 检查权限
        _checkPermission: function(permission) {
            // 实际应用中应该调用权限管理服务
            return true;
        },

        // 验证参数
        _validateParams: function(params) {
            return params && typeof params === 'object';
        },

        // 确认操作
        _confirmAction: function(action) {
            return confirm('确定要执行操作: ' + action.name + '?');
        },

        // 记录日志
        _logAction: function(actionName, params) {
            console.log('[Action]', actionName, params);
        },

        // 添加到历史记录
        _addToHistory: function(actionName, params, result) {
            var history = this.get('actionHistory');
            history.push({
                action: actionName,
                params: params,
                result: result,
                timestamp: new Date().toISOString()
            });
            this.set('actionHistory', history);
        },

        // 动作处理器
        _actionCreateForm: function(params) {
            // 打开创建表单模态框
            var modal = ood.create('Modal', {
                title: '发起审批',
                content: ood.create('ApprovalFormComponent', params)
            });
            modal.show();
            return { modal: modal };
        },

        _actionSubmitForm: function(params) {
            // 提交审批表单
            var form = params.form;
            if (!form || !form.submit) {
                throw new Error('Invalid form parameter');
            }
            return form.submit();
        },

        _actionApprove: function(params) {
            // 审批通过
            var processId = params.processId;
            var comment = params.comment;
            
            return this._apiManager.call('process.approve', { id: processId }, {
                comment: comment
            });
        },

        _actionReject: function(params) {
            // 审批拒绝
            var processId = params.processId;
            var comment = params.comment;
            
            return this._apiManager.call('process.reject', { id: processId }, {
                comment: comment
            });
        },

        _actionSwitchTab: function(params) {
            // 切换标签页
            var tabName = params.tab;
            var tabManager = this._getTabManager();
            tabManager.switchTo(tabName);
            return { tab: tabName };
        }
    }
});
```

#### 4.2.4 事件标准化定义

**事件管理器**:

```javascript
/**
 * 事件管理器 - A2UI 标准化
 * Event Manager - A2UI Standardization
 */
ood.Class('EventManager', ood.absObj, {
    properties: {
        // 事件监听器映射
        listeners: {
            type: 'object',
            default: {}
        },
        // 事件队列
        eventQueue: {
            type: 'array',
            default: []
        },
        // 是否启用事件队列
        enableQueue: {
            type: 'boolean',
            default: false
        }
    },

    methods: {
        // 定义审批相关事件
        _defineEvents: function() {
            this._eventTypes = {
                // 表单事件
                'form.submit': {
                    name: '表单提交',
                    description: '用户提交审批表单',
                    bubble: false,
                    async: true
                },
                'form.validate': {
                    name: '表单验证',
                    description: '表单数据验证',
                    bubble: true,
                    async: false
                },
                'form.reset': {
                    name: '表单重置',
                    description: '重置表单数据',
                    bubble: true,
                    async: false
                },

                // 审批事件
                'approval.approve': {
                    name: '审批通过',
                    description: '审批通过操作',
                    bubble: true,
                    async: true
                },
                'approval.reject': {
                    name: '审批拒绝',
                    description: '审批拒绝操作',
                    bubble: true,
                    async: true
                },
                'approval.transfer': {
                    name: '审批转交',
                    description: '审批转交操作',
                    bubble: true,
                    async: true
                },

                // 数据事件
                'data.load': {
                    name: '数据加载',
                    description: '数据加载完成',
                    bubble: true,
                    async: false
                },
                'data.change': {
                    name: '数据变更',
                    description: '数据发生变更',
                    bubble: true,
                    async: false
                },
                'data.error': {
                    name: '数据错误',
                    description: '数据处理错误',
                    bubble: true,
                    async: false
                },

                // UI 事件
                'ui.click': {
                    name: 'UI 点击',
                    description: 'UI 元素点击事件',
                    bubble: true,
                    async: false
                },
                'ui.change': {
                    name: 'UI 变更',
                    description: 'UI 元素变更事件',
                    bubble: true,
                    async: false
                },
                'ui.focus': {
                    name: 'UI 聚焦',
                    description: 'UI 元素聚焦事件',
                    bubble: true,
                    async: false
                },
                'ui.blur': {
                    name: 'UI 失焦',
                    description: 'UI 元素失焦事件',
                    bubble: true,
                    async: false
                }
            };
        },

        // 注册事件监听器
        on: function(eventType, listener, options) {
            if (!this._eventTypes[eventType]) {
                console.warn('Unknown event type:', eventType);
                return;
            }

            var listeners = this.get('listeners');
            if (!listeners[eventType]) {
                listeners[eventType] = [];
            }

            listeners[eventType].push({
                listener: listener,
                options: options || {}
            });

            this.set('listeners', listeners);
        },

        // 移除事件监听器
        off: function(eventType, listener) {
            var listeners = this.get('listeners');
            if (!listeners[eventType]) {
                return;
            }

            var index = listeners[eventType].findIndex(function(item) {
                return item.listener === listener;
            });

            if (index !== -1) {
                listeners[eventType].splice(index, 1);
            }

            this.set('listeners', listeners);
        },

        // 触发事件
        trigger: function(eventType, eventData) {
            var eventDef = this._eventTypes[eventType];
            if (!eventDef) {
                console.warn('Unknown event type:', eventType);
                return;
            }

            // 如果启用队列，将事件加入队列
            if (this.get('enableQueue')) {
                var queue = this.get('eventQueue');
                queue.push({
                    type: eventType,
                    data: eventData,
                    timestamp: new Date().toISOString()
                });
                this.set('eventQueue', queue);
                return;
            }

            // 构建事件对象
            var event = {
                type: eventType,
                data: eventData,
                timestamp: new Date().toISOString(),
                target: this,
                preventDefault: false,
                stopPropagation: false
            };

            // 执行监听器
            var listeners = this.get('listeners')[eventType] || [];
            
            if (eventDef.async) {
                // 异步执行
                setTimeout(function() {
                    this._executeListeners(event, listeners);
                }.bind(this), 0);
            } else {
                // 同步执行
                this._executeListeners(event, listeners);
            }
        },

        // 执行监听器
        _executeListeners: function(event, listeners) {
            for (var i = 0; i < listeners.length; i++) {
                if (event.stopPropagation && !this._eventTypes[event.type].bubble) {
                    break;
                }

                try {
                    var item = listeners[i];
                    item.listener.call(item.options.context || this, event);
                } catch (error) {
                    console.error('Event listener error:', error);
                }
            }
        },

        // 处理队列中的事件
        processQueue: function() {
            var queue = this.get('eventQueue');
            while (queue.length > 0) {
                var eventItem = queue.shift();
                this.trigger(eventItem.type, eventItem.data);
            }
            this.set('eventQueue', queue);
        }
    }
});
```

---

## 五、场景驱动的表单架构

### 5.1 场景定义

**审批表单场景**:

```javascript
/**
 * 审批表单场景 - 场景驱动架构
 * Approval Form Scene - Scene-Driven Architecture
 */
ood.Class('ApprovalFormScene', ood.absObj, {
    properties: {
        // 场景 ID
        sceneId: {
            type: 'string',
            default: 'approval-form-scene'
        },
        // 场景名称
        sceneName: {
            type: 'string',
            default: '审批表单场景'
        },
        // 场景状态
        sceneState: {
            type: 'string',
            default: 'initialized' // initialized, activated, deactivated
        },
        // 参与者
        participants: {
            type: 'array',
            default: []
        },
        // 能力绑定
        capabilities: {
            type: 'array',
            default: []
        }
    },

    methods: {
        // 初始化场景
        init: function(config) {
            this._super(config);
            
            // 初始化管理器
            this._initManagers();
            
            // 初始化组件
            this._initComponents();
            
            // 绑定事件
            this._bindSceneEvents();
            
            // 加载数据
            this._loadSceneData();
        },

        // 初始化管理器
        _initManagers: function() {
            // A2UI 层管理器
            this._uiManager = ood.create('UIComponentManager');
            this._apiManager = ood.create('APIEndpointManager');
            this._actionManager = ood.create('ActionManager');
            this._eventManager = ood.create('EventManager');

            // A2A 层管理器
            this._a2aManager = ood.create('AgentCollaborationManager');

            // P2A 层管理器
            this._p2aManager = ood.create('AgentInteractionManager');

            // P2P 层管理器
            this._p2pManager = ood.create('UserCollaborationManager');
        },

        // 初始化组件
        _initComponents: function() {
            // 创建审批表单组件
            this._approvalForm = ood.create('ApprovalFormComponent', {
                id: 'main-approval-form',
                sceneId: this.get('sceneId')
            });

            // 创建审批列表组件
            this._approvalList = ood.create('ApprovalListComponent', {
                id: 'approval-list',
                sceneId: this.get('sceneId')
            });

            // 创建审批统计组件
            this._approvalStats = ood.create('ApprovalStatsComponent', {
                id: 'approval-stats',
                sceneId: this.get('sceneId')
            });
        },

        // 绑定场景事件
        _bindSceneEvents: function() {
            // 表单提交事件
            this._approvalForm.on('onSubmit', function(event) {
                this._handleFormSubmit(event.data);
            }.bind(this));

            // 审批通过事件
            this._approvalList.on('onApprove', function(event) {
                this._handleApprovalApprove(event.data);
            }.bind(this));

            // 审批拒绝事件
            this._approvalList.on('onReject', function(event) {
                this._handleApprovalReject(event.data);
            }.bind(this));

            // 数据变更事件
            this._eventManager.on('data.change', function(event) {
                this._handleDataChange(event.data);
            }.bind(this));
        },

        // 加载场景数据
        _loadSceneData: function() {
            // 加载审批列表
            this._apiManager.call('approval.list')
                .then(function(data) {
                    this._approvalList.set('data', data);
                    this._eventManager.trigger('data.load', { type: 'approval-list', data: data });
                }.bind(this))
                .catch(function(error) {
                    this._eventManager.trigger('data.error', { type: 'approval-list', error: error });
                }.bind(this));

            // 加载审批统计
            this._apiManager.call('statistics.get')
                .then(function(data) {
                    this._approvalStats.set('data', data);
                    this._eventManager.trigger('data.load', { type: 'approval-stats', data: data });
                }.bind(this))
                .catch(function(error) {
                    this._eventManager.trigger('data.error', { type: 'approval-stats', error: error });
                }.bind(this));
        },

        // 激活场景
        activate: function() {
            this.set('sceneState', 'activated');
            this._eventManager.trigger('scene.activate', { sceneId: this.get('sceneId') });
        },

        // 停用场景
        deactivate: function() {
            this.set('sceneState', 'deactivated');
            this._eventManager.trigger('scene.deactivate', { sceneId: this.get('sceneId') });
        },

        // 处理表单提交
        _handleFormSubmit: function(formData) {
            // 通过 P2A 层理解用户意图
            this._p2aManager.understandIntent(formData)
                .then(function(intent) {
                    // 通过 A2A 层协调 Agent 处理
                    return this._a2aManager.coordinateAgents(intent);
                }.bind(this))
                .then(function(result) {
                    // 通过 API 提交数据
                    return this._apiManager.call('process.start', null, result);
                }.bind(this))
                .then(function(response) {
                    // 更新 UI
                    this._eventManager.trigger('data.change', { type: 'approval-submitted', data: response });
                    this._actionManager.execute('view.tab.switch', { tab: 'submitted' });
                }.bind(this))
                .catch(function(error) {
                    this._eventManager.trigger('data.error', { type: 'form-submit', error: error });
                }.bind(this));
        },

        // 处理审批通过
        _handleApprovalApprove: function(data) {
            this._actionManager.execute('approval.process.approve', {
                processId: data.processId,
                comment: data.comment
            })
                .then(function(result) {
                    // 通过 P2P 层通知相关人员
                    this._p2pManager.notifyUsers({
                        type: 'approval-approved',
                        processId: data.processId,
                        users: data.stakeholders
                    });
                    
                    // 更新 UI
                    this._eventManager.trigger('data.change', { type: 'approval-approved', data: result });
                }.bind(this))
                .catch(function(error) {
                    this._eventManager.trigger('data.error', { type: 'approval-approve', error: error });
                }.bind(this));
        },

        // 处理审批拒绝
        _handleApprovalReject: function(data) {
            this._actionManager.execute('approval.process.reject', {
                processId: data.processId,
                comment: data.comment
            })
                .then(function(result) {
                    // 通过 P2P 层通知相关人员
                    this._p2pManager.notifyUsers({
                        type: 'approval-rejected',
                        processId: data.processId,
                        users: data.stakeholders
                    });
                    
                    // 更新 UI
                    this._eventManager.trigger('data.change', { type: 'approval-rejected', data: result });
                }.bind(this))
                .catch(function(error) {
                    this._eventManager.trigger('data.error', { type: 'approval-reject', error: error });
                }.bind(this));
        },

        // 处理数据变更
        _handleDataChange: function(data) {
            // 刷新相关组件
            if (data.type === 'approval-submitted' || 
                data.type === 'approval-approved' || 
                data.type === 'approval-rejected') {
                this._loadSceneData();
            }
        }
    }
});
```

### 5.2 四层协作流程

#### 5.2.1 审批提交流程

```
用户操作 (P2P)
    ↓
P2A 层: 理解用户意图
    ↓
A2A 层: 协调 Agent 处理
    ↓
A2UI 层: 更新 UI 组件
    ↓
API 端点: 提交数据到后端
```

**详细流程**:

1. **P2P 层**: 用户点击"提交审批"按钮
   ```javascript
   // P2P 层触发
   _p2pManager.notifyUsers({
       type: 'user-action',
       action: 'submit-approval'
   });
   ```

2. **P2A 层**: 理解用户意图
   ```javascript
   // P2A 层处理
   _p2aManager.understandIntent({
       action: 'submit-approval',
       formData: formData
   })
   .then(function(intent) {
       // intent = { type: 'submit', data: formData }
       return intent;
   });
   ```

3. **A2A 层**: 协调 Agent 处理
   ```javascript
   // A2A 层处理
   _a2aManager.coordinateAgents({
       type: 'submit',
       data: formData
   })
   .then(function(result) {
       // result = { validated: true, data: processedData }
       return result;
   });
   ```

4. **A2UI 层**: 更新 UI 组件
   ```javascript
   // A2UI 层处理
   _uiManager.updateComponent({
       componentId: 'approval-list',
       action: 'add',
       data: result.data
   });
   ```

5. **API 端点**: 提交数据到后端
   ```javascript
   // API 调用
   _apiManager.call('process.start', null, result.data)
   .then(function(response) {
       // 处理响应
   });
   ```

---

## 六、实施建议

### 6.1 迁移步骤

#### Phase 1: A2UI 层标准化 (2-3 周)
1. **组件重构**
   - 将现有 HTML/JS 组件转换为 Ooder 组件
   - 标准化组件属性、事件、方法定义
   - 实现组件缓存和验证机制

2. **端点标准化**
   - 创建 APIEndpointManager
   - 定义所有审批相关端点
   - 实现统一的错误处理机制

3. **动作标准化**
   - 创建 ActionManager
   - 定义所有审批相关动作
   - 实现权限控制和日志记录

4. **事件标准化**
   - 创建 EventManager
   - 定义所有审批相关事件
   - 实现事件队列和异步处理

#### Phase 2: A2A 层集成 (2-3 周)
1. **Agent 协作管理器**
   - 实现 AgentCollaborationManager
   - 集成 A2AProtocolService
   - 实现 Agent 间消息路由

2. **Agent 能力注册**
   - 注册审批相关 Agent 能力
   - 实现 Agent 能力匹配
   - 实现 Agent 负载均衡

#### Phase 3: P2A 层集成 (2-3 周)
1. **Agent 交互管理器**
   - 实现 AgentInteractionManager
   - 集成 AgentChatService
   - 实现意图理解和转换

2. **对话管理**
   - 实现多轮对话支持
   - 实现上下文保持
   - 集成知识库增强

#### Phase 4: P2P 层集成 (2-3 周)
1. **用户协作管理器**
   - 实现 UserCollaborationManager
   - 集成 P2PServiceImpl
   - 实现实时通信

2. **协作流程**
   - 实现任务分配
   - 实现权限管理
   - 实现协作流程编排

### 6.2 技术选型

**前端技术栈**:
- 框架: Ooder Framework
- 组件: ood.UI.* 系列
- 状态管理: Ooder Properties System
- 事件系统: Ooder Event System

**后端技术栈**:
- 框架: Spring Boot 3.x
- 工作流引擎: ooder-bpm-web
- Agent 服务: skill-agent
- 场景服务: skill-scenes

**通信协议**:
- A2UI: Ooder Component Protocol
- A2A: A2AProtocolService
- P2A: AgentChatService
- P2P: P2PServiceImpl

---

## 七、总结与展望

### 7.1 总结

本架构设计基于 Ooder 框架和场景驱动模型，提出了一套完整的 A2UI 标准化分离方案：

1. **A2UI 层**: 实现 UI 组件的标准化定义和管理
2. **A2A 层**: 实现 Agent 间的协作和通信
3. **P2A 层**: 实现人与 Agent 的交互和理解
4. **P2P 层**: 实现人与人之间的协作和通信

通过四层架构的有机结合，实现了从用户操作到系统响应的完整链路，实现了 UI 组件与业务逻辑的完全解耦。

### 7.2 展望

未来，我们将继续完善和优化场景驱动架构，重点关注以下方向：

1. **智能化**: 深度集成 AI 能力，实现智能意图理解、智能任务分配
2. **可视化**: 提供可视化的场景编排工具，降低使用门槛
3. **标准化**: 制定 A2UI、A2A、P2A、P2P 的行业标准
4. **生态化**: 开放架构，建设开发者社区，构建场景应用生态

---

## 八、附录

### 8.1 关键文件路径

**Ooder 组件开发技能**:
- 技能路径: `C:\Users\Administrator\.trae-cn\skills\ooder组件开发`
- 组件文档: `./docs/components/`
- 示例代码: `./examples/`

**审批表单示例**:
- HTML: `E:\github\ooder-skills\skill-ui-test\skills\skill-approval-form\ui\pages\index.html`
- JavaScript: `E:\github\ooder-skills\skill-ui-test\skills\skill-approval-form\ui\js\approval-form.js`
- Skill 定义: `E:\github\ooder-skills\skill-ui-test\skills\skill-approval-form\skill.yaml`

### 8.2 参考资料

1. Ooder 组件开发技能指南: `C:\Users\Administrator\.trae-cn\skills\ooder组件开发`
2. 流程应用产品设计规划: `E:\github\ooder-skills\docs\流程应用产品设计规划.md`
3. OoderAgent(Nexus) Skills 移植完成总结报告: `E:\github\ooder-skills\docs\v3.0.1\OODER_AGENT_MIGRATION_SUMMARY.md`

---

**文档维护**: 本文档应在后续开发过程中持续更新。

**变更记录**:
- 2026-04-06 v1.0: 初始版本创建，完成场景驱动表单架构设计
