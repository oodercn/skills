class Chat {
    constructor(container, store, api) {
        this.container = container;
        this.store = store;
        this.api = api;
        this.messages = [];
        this.collapsed = false;
        this.sessionId = 'bpm-designer-session';
        this.llmApiUrl = '/api/v1/chat';
        this._init();
    }

    _init() {
        this._bindEvents();
    }

    _bindEvents() {
        const toggleBtn = document.getElementById('btnChatToggle');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', () => {
                this.toggle();
            });
        }

        const sendBtn = document.getElementById('btnChatSend');
        if (sendBtn) {
            sendBtn.addEventListener('click', () => {
                this._sendMessage();
            });
        }

        const input = document.getElementById('chatInput');
        if (input) {
            input.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    this._sendMessage();
                }
            });
        }
    }

    toggle() {
        this.collapsed = !this.collapsed;
        this.container.classList.toggle('collapsed', this.collapsed);
        
        const btn = document.getElementById('btnChatToggle');
        if (btn) {
            btn.innerHTML = IconManager.render(this.collapsed ? 'chevronRight' : 'minus', 18, 'icon-toggle');
        }
    }

    async _sendMessage() {
        const input = document.getElementById('chatInput');
        if (!input) return;

        const message = input.value.trim();
        if (!message) return;

        this.addMessage('user', message);
        input.value = '';

        const process = this.store.getProcess();
        const currentActivity = this.store.currentActivity;
        
        const context = {
            processId: process?.processDefId,
            processName: process?.name,
            activityId: currentActivity?.activityDefId,
            activityName: currentActivity?.name,
            activityType: currentActivity?.activityType
        };

        try {
            const response = await fetch(`${this.llmApiUrl}/sessions/${this.sessionId}/messages`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    content: message,
                    skillId: 'bpm-designer',
                    userId: 'bpm-designer-user',
                    context: context,
                    currentPage: 'BPM Designer',
                    currentUrl: window.location.href
                })
            });

            const result = await response.json();
            
            if (result.status === 'success' && result.data) {
                const assistantMessage = result.data.content;
                this.addMessage('assistant', assistantMessage);
                
                if (result.data.actions) {
                    this._handleActions(result.data.actions);
                }
            } else {
                this._processMessageLocally(message);
            }
        } catch (e) {
            console.warn('[Chat] LLM API not available, using local processing:', e);
            this._processMessageLocally(message);
        }
    }

    _handleActions(actions) {
        if (!actions || !Array.isArray(actions)) return;
        
        actions.forEach(action => {
            switch (action.type) {
                case 'create_activity':
                    this._createActivity(action.data);
                    break;
                case 'update_activity':
                    this._updateActivity(action.data);
                    break;
                case 'delete_activity':
                    this._deleteActivity(action.data);
                    break;
                case 'create_route':
                    this._createRoute(action.data);
                    break;
                case 'export_yaml':
                    this._exportYaml();
                    break;
            }
        });
    }

    _createActivity(data) {
        const process = this.store.getProcess();
        if (!process) return;
        
        const activity = {
            activityDefId: 'act_' + Date.now(),
            name: data.name || '新活动',
            activityType: data.activityType || 'TASK',
            activityCategory: data.category || 'HUMAN',
            position: data.position || { x: 200, y: 200 },
            implementation: data.implementation || 'IMPL_NO'
        };
        
        this.store.addActivity(activity);
    }

    _updateActivity(data) {
        if (!this.store.currentActivity) return;
        this.store.updateActivity(this.store.currentActivity.activityDefId, data);
    }

    _deleteActivity(data) {
        if (!data.activityId) return;
        this.store.removeActivity(data.activityId);
    }

    _createRoute(data) {
        const process = this.store.getProcess();
        if (!process || !data.from || !data.to) return;
        
        const route = {
            routeDefId: 'route_' + Date.now(),
            name: data.name || '',
            from: data.from,
            to: data.to,
            condition: data.condition
        };
        
        this.store.addRoute(route);
    }

    _exportYaml() {
        const yaml = this.store.exportYaml();
        this.addMessage('assistant', 'YAML 定义已生成：\n\n```yaml\n' + yaml + '\n```');
    }

    _processMessageLocally(message) {
        const lowerMsg = message.toLowerCase();
        
        if (lowerMsg.includes('创建') || lowerMsg.includes('新建') || lowerMsg.includes('添加')) {
            this._handleCreateCommand(message);
        } else if (lowerMsg.includes('修改') || lowerMsg.includes('更新') || lowerMsg.includes('设置')) {
            this._handleUpdateCommand(message);
        } else if (lowerMsg.includes('删除') || lowerMsg.includes('移除')) {
            this._handleDeleteCommand(message);
        } else if (lowerMsg.includes('生成') || lowerMsg.includes('导出') || lowerMsg.includes('yaml')) {
            this._handleExportCommand(message);
        } else if (lowerMsg.includes('帮助') || lowerMsg.includes('help')) {
            this._showHelp();
        } else {
            this.addMessage('assistant', '我理解您的需求。您可以尝试以下命令：\n\n• 创建一个用户任务\n• 添加开始节点\n• 生成 YAML 定义\n• 帮助');
        }
    }

    _handleCreateCommand(message) {
        const process = this.store.getProcess();
        if (!process) {
            this.addMessage('assistant', '请先创建一个流程。');
            return;
        }

        if (message.includes('用户任务') || message.includes('任务')) {
            const activity = {
                activityDefId: 'act_' + Date.now(),
                name: '新用户任务',
                activityType: 'TASK',
                activityCategory: 'HUMAN',
                position: { x: 200, y: 200 },
                implementation: 'IMPL_NO'
            };
            this.store.addActivity(activity);
            this.addMessage('assistant', `已创建用户任务「${activity.name}」，请在画布中调整位置。`);
        } else if (message.includes('开始')) {
            const activity = {
                activityDefId: 'act_' + Date.now(),
                name: '开始',
                activityType: 'START',
                position: 'START',
                positionCoord: { x: 100, y: 100 }
            };
            this.store.addActivity(activity);
            this.addMessage('assistant', '已创建开始节点。');
        } else if (message.includes('结束')) {
            const activity = {
                activityDefId: 'act_' + Date.now(),
                name: '结束',
                activityType: 'END',
                position: 'END',
                positionCoord: { x: 300, y: 100 }
            };
            this.store.addActivity(activity);
            this.addMessage('assistant', '已创建结束节点。');
        } else if (message.includes('agent') || message.includes('llm')) {
            const activity = {
                activityDefId: 'act_' + Date.now(),
                name: 'LLM任务',
                activityType: 'LLM_TASK',
                activityCategory: 'AGENT',
                position: { x: 200, y: 200 },
                implementation: 'IMPL_TOOL',
                agentConfig: { name: 'LLM Agent' }
            };
            this.store.addActivity(activity);
            this.addMessage('assistant', `已创建 LLM 任务「${activity.name}」。`);
        } else {
            this.addMessage('assistant', '请指定要创建的元素类型，例如：创建用户任务、添加开始节点、创建 LLM 任务。');
        }
    }

    _handleUpdateCommand(message) {
        const activity = this.store.currentActivity;
        if (!activity) {
            this.addMessage('assistant', '请先在画布中选择一个活动。');
            return;
        }
        this.addMessage('assistant', `当前选中的是「${activity.name}」，请在右侧属性面板中修改属性。`);
    }

    _handleDeleteCommand(message) {
        const activity = this.store.currentActivity;
        if (!activity) {
            this.addMessage('assistant', '请先在画布中选择要删除的活动。');
            return;
        }
        const name = activity.name;
        this.store.removeActivity(activity.activityDefId);
        this.addMessage('assistant', `已删除活动「${name}」。`);
    }

    _handleExportCommand(message) {
        const yaml = this.store.exportYaml();
        this.addMessage('assistant', 'YAML 定义已生成：\n\n```yaml\n' + yaml + '\n```');
    }

    _showHelp() {
        this.addMessage('assistant', `**BPM 设计助手帮助**

**创建元素：**
• 创建用户任务
• 添加开始/结束节点
• 创建 LLM 任务
• 创建 Agent 任务

**编辑元素：**
• 选中活动后可在右侧面板修改属性
• 支持拖拽调整位置
• Delete 键删除选中元素

**导出：**
• 生成 YAML 定义
• 导出流程配置

**快捷键：**
• Ctrl+S 保存
• Ctrl+Z 撤销
• Ctrl+Y 重做
• Delete 删除`);
    }

    addMessage(role, content) {
        this.messages.push({ role, content, time: new Date() });
        this._renderMessages();
    }

    _renderMessages() {
        const container = document.getElementById('chatMessages');
        if (!container) return;

        container.innerHTML = this.messages.map(msg => `
            <div class="d-chat-message ${msg.role}">
                <div class="d-chat-bubble">${this._formatContent(msg.content)}</div>
            </div>
        `).join('');

        container.scrollTop = container.scrollHeight;
    }

    _formatContent(content) {
        return content
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/```yaml\n([\s\S]*?)\n```/g, '<pre class="d-chat-code">$1</pre>')
            .replace(/```(\w*)\n([\s\S]*?)\n```/g, '<pre class="d-chat-code">$2</pre>')
            .replace(/\n/g, '<br>');
    }
}

window.Chat = Chat;
