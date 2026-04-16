class Chat {
    constructor(container, store, api) {
        this.container = container;
        this.store = store;
        this.api = api;
        this.messages = [];
        this.collapsed = container ? container.classList.contains('collapsed') : true;
        this.sessionId = 'bpm-designer-' + Date.now();
        this.llmApiUrl = '/api/v1/chat';
        this.useStream = true;
        this._init();
        console.log('[Chat] initialized, sessionId:', this.sessionId, 'collapsed:', this.collapsed);
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
        console.log('[Chat] toggle, collapsed:', this.collapsed);
        
        if (this.container) {
            if (this.collapsed) {
                this.container.classList.add('collapsed');
            } else {
                this.container.classList.remove('collapsed');
            }
        }
        
        const btn = document.getElementById('btnChatToggle');
        if (btn) {
            btn.innerHTML = IconManager.render(this.collapsed ? 'chevronRight' : 'minus', 18, 'icon-toggle');
        }
    }

    _buildContext() {
        const process = this.store.getProcess();
        const currentActivity = this.store.currentActivity;
        const currentRoute = this.store.currentRoute;
        
        const context = {
            processId: process?.processDefId,
            processName: process?.name,
            activityId: currentActivity?.activityDefId,
            activityName: currentActivity?.name,
            activityType: currentActivity?.activityType,
            activityCategory: currentActivity?.activityCategory,
            routeId: currentRoute?.routeDefId,
            activityCount: process?.activities?.length || 0,
            routeCount: process?.routes?.length || 0
        };
        
        if (process?.activities && process.activities.length > 0) {
            context.activities = process.activities.map(act => ({
                activityDefId: act.activityDefId,
                name: act.name,
                activityType: act.activityType,
                activityCategory: act.activityCategory || act.category || 'HUMAN',
                implementation: act.implementation,
                description: act.description
            }));
        }
        
        if (process?.routes && process.routes.length > 0) {
            context.routes = process.routes.map(route => ({
                routeDefId: route.routeDefId,
                name: route.name,
                from: route.from,
                to: route.to,
                condition: route.condition
            }));
        }
        
        return context;
    }

    async _sendMessage() {
        const input = document.getElementById('chatInput');
        if (!input) return;

        const message = input.value.trim();
        if (!message) return;

        this.addMessage('user', message);
        input.value = '';

        const context = this._buildContext();

        if (this.useStream) {
            await this._sendStreamMessage(message, context);
        } else {
            await this._sendNormalMessage(message, context);
        }
    }

    async _sendStreamMessage(message, context) {
        try {
            this._showTyping();

            const response = await fetch(`${this.llmApiUrl}/sessions/${this.sessionId}/stream`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    content: message,
                    skillId: 'bpm-designer',
                    userId: 'bpm-designer-user',
                    context: context
                })
            });

            this._hideTyping();

            if (!response.ok) {
                this._processMessageLocally(message);
                return;
            }

            const reader = response.body.getReader();
            const decoder = new TextDecoder();
            let buffer = '';
            let assistantContent = '';
            let msgElement = null;

            while (true) {
                const { done, value } = await reader.read();
                if (done) break;

                buffer += decoder.decode(value, { stream: true });
                const lines = buffer.split('\n');
                buffer = lines.pop() || '';

                for (const line of lines) {
                    if (line.startsWith('event:')) continue;
                    if (line.startsWith('data:')) {
                        const dataStr = line.substring(5).trim();
                        try {
                            const data = JSON.parse(dataStr);
                            
                            if (data.content !== undefined) {
                                assistantContent += data.content;
                                if (!msgElement) {
                                    msgElement = this._addStreamingMessage();
                                }
                                this._updateStreamingMessage(msgElement, assistantContent);
                            }
                        } catch (e) {
                            // skip unparseable lines
                        }
                    }
                }
            }

            // Process remaining buffer
            if (buffer.startsWith('data:')) {
                // handle last event if needed
            }

            if (assistantContent) {
                this._finalizeStreamingMessage(msgElement, assistantContent);
                this.messages.push({ role: 'assistant', content: assistantContent, time: new Date() });
            }

        } catch (e) {
            this._hideTyping();
            console.warn('[Chat] Stream failed, falling back:', e);
            await this._sendNormalMessage(message, context);
        }
    }

    async _sendNormalMessage(message, context) {
        try {
            this._showTyping();

            const response = await fetch(`${this.llmApiUrl}/sessions/${this.sessionId}/messages`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    content: message,
                    skillId: 'bpm-designer',
                    userId: 'bpm-designer-user',
                    context: context
                })
            });

            this._hideTyping();

            const result = await response.json();
            
            if (result.code === 200 && result.data) {
                const assistantMessage = result.data.content;
                this.addMessage('assistant', assistantMessage);
                
                if (result.data.actions && result.data.actions.length > 0) {
                    this._handleActions(result.data.actions);
                }

                if (result.data.metadata) {
                    console.log('[Chat] mode:', result.data.metadata.mode, 'model:', result.data.metadata.model);
                }
            } else {
                this._processMessageLocally(message);
            }
        } catch (e) {
            this._hideTyping();
            console.warn('[Chat] LLM API not available, using local processing:', e);
            this._processMessageLocally(message);
        }
    }

    _addStreamingMessage() {
        const container = document.getElementById('chatMessages');
        if (!container) return null;
        
        const msgEl = document.createElement('div');
        msgEl.className = 'd-chat-message assistant streaming';
        msgEl.innerHTML = '<div class="d-chat-bubble"><span class="streaming-cursor">▊</span></div>';
        container.appendChild(msgEl);
        container.scrollTop = container.scrollHeight;
        return msgEl;
    }

    _updateStreamingMessage(msgElement, content) {
        if (!msgElement) return;
        const bubble = msgElement.querySelector('.d-chat-bubble');
        if (bubble) {
            bubble.innerHTML = this._formatContent(content) + '<span class="streaming-cursor">▊</span>';
        }
        const container = document.getElementById('chatMessages');
        if (container) container.scrollTop = container.scrollHeight;
    }

    _finalizeStreamingMessage(msgElement, content) {
        if (!msgElement) return;
        msgElement.classList.remove('streaming');
        const bubble = msgElement.querySelector('.d-chat-bubble');
        if (bubble) {
            bubble.innerHTML = this._formatContent(content);
        }
    }

    _handleActions(actions) {
        if (!actions || !Array.isArray(actions)) return;
        
        let executedCount = 0;
        let failedCount = 0;
        
        actions.forEach(action => {
            if (action.success === false) {
                failedCount++;
                console.warn('[Chat] Action failed:', action.type, action.error);
                return;
            }
            
            switch (action.type) {
                case 'create_activity':
                    if (this._createActivity(action.data)) executedCount++;
                    break;
                case 'update_activity':
                    if (this._updateActivity(action.data)) executedCount++;
                    break;
                case 'delete_activity':
                    if (this._deleteActivity(action.data)) executedCount++;
                    break;
                case 'create_route':
                    if (this._createRoute(action.data)) executedCount++;
                    break;
                default:
                    console.log('[Chat] Unknown action type:', action.type);
            }
        });
        
        if (executedCount > 0) {
            console.log('[Chat] Executed', executedCount, 'actions successfully');
        }
    }

    _createActivity(data) {
        if (!data) return false;
        const process = this.store.getProcess();
        if (!process) return false;
        
        const activity = {
            activityDefId: data.activityDefId || 'act_' + Date.now(),
            name: data.name || '新活动',
            activityType: data.activityType || 'TASK',
            activityCategory: data.activityCategory || data.category || 'HUMAN',
            position: data.position || { x: 200 + Math.random() * 100, y: 200 + Math.random() * 100 },
            implementation: data.implementation || 'IMPL_NO',
            description: data.description || ''
        };
        
        if (data.agentConfig) activity.agentConfig = data.agentConfig;
        if (data.sceneConfig) activity.sceneConfig = data.sceneConfig;
        
        this.store.addActivity(activity);
        return true;
    }

    _updateActivity(data) {
        if (!data) return false;
        const activityDefId = data.activityDefId;
        const attribute = data.attribute;
        const value = data.value;
        
        if (!attribute || value === undefined) return false;
        
        if (activityDefId) {
            const activity = this.store.getActivity(activityDefId);
            if (activity) {
                activity[attribute] = value;
                this.store.updateActivity(activity);
                return true;
            }
        }
        
        const current = this.store.currentActivity;
        if (current) {
            current[attribute] = value;
            this.store.updateActivity(current);
            return true;
        }
        return false;
    }

    _deleteActivity(data) {
        if (!data || !data.activityId) return false;
        this.store.removeActivity(data.activityId);
        return true;
    }

    _createRoute(data) {
        const process = this.store.getProcess();
        if (!process || !data || !data.from || !data.to) return false;
        
        const route = {
            routeDefId: data.routeDefId || 'route_' + Date.now(),
            name: data.name || '',
            from: data.from,
            to: data.to,
            condition: data.condition || ''
        };
        
        this.store.addRoute(route);
        return true;
    }

    _showTyping() {
        const container = document.getElementById('chatMessages');
        if (!container) return;
        
        const typingEl = document.createElement('div');
        typingEl.className = 'd-chat-message assistant typing-indicator';
        typingEl.id = 'chatTyping';
        typingEl.innerHTML = '<div class="d-chat-bubble"><span class="dot"></span><span class="dot"></span><span class="dot"></span></div>';
        container.appendChild(typingEl);
        container.scrollTop = container.scrollHeight;
    }

    _hideTyping() {
        const typingEl = document.getElementById('chatTyping');
        if (typingEl) typingEl.remove();
    }

    _processMessageLocally(message) {
        const lowerMsg = message.toLowerCase();
        
        if (lowerMsg.includes('创建') || lowerMsg.includes('新建') || lowerMsg.includes('添加')) {
            this._handleCreateCommand(message);
        } else if (lowerMsg.includes('修改') || lowerMsg.includes('更新') || lowerMsg.includes('设置')) {
            this._handleUpdateCommand(message);
        } else if (lowerMsg.includes('删除') || lowerMsg.includes('移除')) {
            this._handleDeleteCommand(message);
        } else if (lowerMsg.includes('验证') || lowerMsg.includes('检查')) {
            this._handleValidateCommand();
        } else if (lowerMsg.includes('帮助') || lowerMsg.includes('help')) {
            this._showHelp();
        } else {
            this.addMessage('assistant', '我理解您的需求。您可以尝试以下命令：\n\n• 创建一个用户任务\n• 添加开始节点\n• 验证流程\n• 帮助');
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

    _handleValidateCommand() {
        const process = this.store.getProcess();
        if (!process) {
            this.addMessage('assistant', '当前没有打开的流程。');
            return;
        }
        
        const activities = process.activities || [];
        const routes = process.routes || [];
        const hasStart = activities.some(a => a.activityType === 'START');
        const hasEnd = activities.some(a => a.activityType === 'END');
        
        let result = '**流程验证结果**\n\n';
        result += `• 活动数量：${activities.length}\n`;
        result += `• 路由数量：${routes.length}\n`;
        if (!hasStart) result += '⚠️ 缺少开始节点\n';
        if (!hasEnd) result += '⚠️ 缺少结束节点\n';
        if (hasStart && hasEnd) result += '✅ 流程结构基本完整\n';
        
        this.addMessage('assistant', result);
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

**流程操作：**
• 验证流程 - 检查流程完整性
• 建议下一步 - 获取智能建议

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
