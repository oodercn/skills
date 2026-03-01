const LLMAssistant = {
    messages: [],
    context: null,
    settings: {
        provider: 'mock',
        model: 'default',
        temperature: 0.7,
        maxTokens: 4096,
        systemPrompt: '',
        autoContext: true,
        stream: true
    },
    providers: [],
    models: [],
    isStreaming: false,
    messageCount: 0,
    totalTokens: 0,
    
    init() {
        this.loadSettings();
        this.loadProviders();
        ContextCollector.init();
        
        if (this.settings.autoContext) {
            this.refreshContext();
        }
        
        this.loadHistory();
    },
    
    async loadProviders() {
        try {
            const response = await NexusApi.post('/api/llm/providers');
            if (response.status === 'success' && response.data) {
                this.providers = response.data;
                this.renderProviders();
            }
        } catch (error) {
            console.error('Failed to load providers:', error);
        }
    },
    
    renderProviders() {
        const providerSelect = document.getElementById('provider-select');
        const modelSelect = document.getElementById('model-select');
        
        if (!providerSelect || !modelSelect) return;
        
        providerSelect.innerHTML = '';
        this.providers.forEach(provider => {
            const option = document.createElement('option');
            option.value = provider.type;
            option.textContent = this.getProviderDisplayName(provider.type);
            if (provider.type === this.settings.provider) {
                option.selected = true;
            }
            providerSelect.appendChild(option);
        });
        
        this.updateModelSelect();
    },
    
    getProviderDisplayName(type) {
        const names = {
            'openai': 'OpenAI',
            'qianwen': '通义千问',
            'deepseek': 'DeepSeek',
            'ollama': 'Ollama',
            'volcengine': '火山引擎',
            'mock': 'Mock'
        };
        return names[type] || type;
    },
    
    updateModelSelect() {
        const providerSelect = document.getElementById('provider-select');
        const modelSelect = document.getElementById('model-select');
        
        if (!providerSelect || !modelSelect) return;
        
        const selectedProvider = providerSelect.value;
        const provider = this.providers.find(p => p.type === selectedProvider);
        
        modelSelect.innerHTML = '';
        if (provider && provider.models) {
            provider.models.forEach(model => {
                const option = document.createElement('option');
                option.value = model;
                option.textContent = model;
                if (model === this.settings.model) {
                    option.selected = true;
                }
                modelSelect.appendChild(option);
            });
        }
    },
    
    changeProvider() {
        const providerSelect = document.getElementById('provider-select');
        this.settings.provider = providerSelect.value;
        this.updateModelSelect();
        this.saveSettings();
    },
    
    changeModel() {
        const modelSelect = document.getElementById('model-select');
        this.settings.model = modelSelect.value;
        this.saveSettings();
    },
    
    async refreshContext() {
        const contextInfo = document.getElementById('context-info');
        const contextStatus = document.getElementById('context-status');
        
        if (contextStatus) {
            contextStatus.textContent = '正在获取上下文...';
        }
        
        try {
            this.context = await ContextCollector.buildContext();
            
            if (this.context && contextInfo) {
                const pageType = document.getElementById('ctx-page-type');
                const pageTitle = document.getElementById('ctx-page-title');
                const userId = document.getElementById('ctx-user-id');
                const sceneId = document.getElementById('ctx-scene-id');
                const tokens = document.getElementById('ctx-tokens');
                
                if (pageType) pageType.textContent = ContextCollector.context.pageType || '-';
                if (pageTitle) pageTitle.textContent = ContextCollector.context.pageTitle || '-';
                if (userId) userId.textContent = ContextCollector.context.pageData?.userId || '-';
                if (sceneId) sceneId.textContent = ContextCollector.context.pageData?.sceneId || '-';
                if (tokens) tokens.textContent = this.context.tokenCount || '0';
                
                if (contextStatus) {
                    contextStatus.textContent = '已获取页面上下文';
                }
            }
        } catch (error) {
            console.error('Failed to refresh context:', error);
            if (contextStatus) {
                contextStatus.textContent = '获取上下文失败';
            }
        }
    },
    
    handleKeydown(event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            this.sendMessage();
        }
    },
    
    autoResize(textarea) {
        textarea.style.height = 'auto';
        textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px';
    },
    
    async sendMessage() {
        const input = document.getElementById('chat-input');
        const message = input.value.trim();
        
        if (!message || this.isStreaming) return;
        
        input.value = '';
        input.style.height = 'auto';
        
        this.addMessage('user', message);
        this.showTyping();
        
        try {
            if (this.settings.stream) {
                await this.sendStreamMessage(message);
            } else {
                await this.sendNormalMessage(message);
            }
        } catch (error) {
            this.hideTyping();
            this.showError('发送失败', error.message);
        }
    },
    
    async sendNormalMessage(message) {
        const request = {
            prompt: message,
            provider: this.settings.provider,
            model: this.settings.model,
            temperature: this.settings.temperature,
            maxTokens: this.settings.maxTokens
        };
        
        if (this.settings.systemPrompt) {
            request.systemPrompt = this.settings.systemPrompt;
        }
        
        if (this.context && this.context.formattedPrompt) {
            request.systemPrompt = (request.systemPrompt || '') + '\n\n' + this.context.formattedPrompt;
        }
        
        const response = await NexusApi.post('/api/llm/chat', request);
        
        this.hideTyping();
        
        if (response.status === 'success' && response.data) {
            this.addMessage('assistant', response.data.response);
            this.updateStats(response.data);
        } else {
            this.showError('请求失败', response.message || '未知错误');
        }
    },
    
    async sendStreamMessage(message) {
        this.isStreaming = true;
        
        const request = {
            prompt: message,
            provider: this.settings.provider,
            model: this.settings.model,
            temperature: this.settings.temperature,
            maxTokens: this.settings.maxTokens
        };
        
        if (this.settings.systemPrompt) {
            request.systemPrompt = this.settings.systemPrompt;
        }
        
        if (this.context && this.context.formattedPrompt) {
            request.systemPrompt = (request.systemPrompt || '') + '\n\n' + this.context.formattedPrompt;
        }
        
        const messageDiv = this.createMessageElement('assistant', '');
        const contentDiv = messageDiv.querySelector('.llm-message__bubble');
        
        try {
            const response = await fetch('/api/llm/chat/stream', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(request)
            });
            
            this.hideTyping();
            
            const reader = response.body.getReader();
            const decoder = new TextDecoder();
            let fullContent = '';
            
            while (true) {
                const { done, value } = await reader.read();
                if (done) break;
                
                const chunk = decoder.decode(value);
                const lines = chunk.split('\n');
                
                for (const line of lines) {
                    if (line.startsWith('data:')) {
                        const data = line.slice(5);
                        if (data === '[DONE]') {
                            break;
                        }
                        fullContent += data;
                        contentDiv.innerHTML = this.formatMessage(fullContent);
                        this.scrollToBottom();
                    }
                }
            }
            
            this.messages.push({
                role: 'assistant',
                content: fullContent,
                timestamp: Date.now()
            });
            
            this.messageCount++;
            this.updateStatsDisplay();
            this.saveHistory();
            
        } catch (error) {
            console.error('Stream error:', error);
            contentDiv.innerHTML = `<div class="llm-error">流式输出失败: ${error.message}</div>`;
        } finally {
            this.isStreaming = false;
        }
    },
    
    sendQuickMessage(message) {
        const input = document.getElementById('chat-input');
        input.value = message;
        this.sendMessage();
    },
    
    addMessage(role, content) {
        const messagesContainer = document.getElementById('chat-messages');
        
        const welcomeEl = messagesContainer.querySelector('.llm-welcome');
        if (welcomeEl) {
            welcomeEl.remove();
        }
        
        const messageDiv = this.createMessageElement(role, content);
        messagesContainer.appendChild(messageDiv);
        
        this.messages.push({
            role: role,
            content: content,
            timestamp: Date.now()
        });
        
        this.messageCount++;
        this.updateStatsDisplay();
        this.scrollToBottom();
        this.saveHistory();
    },
    
    createMessageElement(role, content) {
        const div = document.createElement('div');
        div.className = `llm-message llm-message--${role}`;
        
        const icon = role === 'assistant' ? 'ri-robot-line' : 'ri-user-line';
        
        div.innerHTML = `
            <div class="llm-message__avatar">
                <i class="${icon}"></i>
            </div>
            <div class="llm-message__content">
                <div class="llm-message__bubble">${this.formatMessage(content)}</div>
                <div class="llm-message__time">${this.formatTime(new Date())}</div>
            </div>
        `;
        
        return div;
    },
    
    formatMessage(content) {
        if (!content) return '';
        
        content = content.replace(/```(\w*)\n([\s\S]*?)```/g, (match, lang, code) => {
            return `<pre><code class="language-${lang}">${this.escapeHtml(code.trim())}</code></pre>`;
        });
        
        content = content.replace(/`([^`]+)`/g, '<code>$1</code>');
        
        content = content.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');
        content = content.replace(/\*([^*]+)\*/g, '<em>$1</em>');
        
        content = content.replace(/^### (.+)$/gm, '<h4>$1</h4>');
        content = content.replace(/^## (.+)$/gm, '<h3>$1</h3>');
        content = content.replace(/^# (.+)$/gm, '<h2>$1</h2>');
        
        content = content.replace(/^- (.+)$/gm, '<li>$1</li>');
        content = content.replace(/(<li>.*<\/li>\n?)+/g, '<ul>$&</ul>');
        
        content = content.replace(/\n\n/g, '</p><p>');
        content = content.replace(/\n/g, '<br>');
        
        return `<p>${content}</p>`;
    },
    
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },
    
    formatTime(date) {
        return date.toLocaleTimeString('zh-CN', {
            hour: '2-digit',
            minute: '2-digit'
        });
    },
    
    showTyping() {
        const messagesContainer = document.getElementById('chat-messages');
        const typingDiv = document.createElement('div');
        typingDiv.className = 'llm-message llm-message--assistant';
        typingDiv.id = 'typing-indicator';
        typingDiv.innerHTML = `
            <div class="llm-message__avatar">
                <i class="ri-robot-line"></i>
            </div>
            <div class="llm-message__content">
                <div class="llm-message__bubble">
                    <div class="llm-typing">
                        <span></span>
                        <span></span>
                        <span></span>
                    </div>
                </div>
            </div>
        `;
        messagesContainer.appendChild(typingDiv);
        this.scrollToBottom();
    },
    
    hideTyping() {
        const typingIndicator = document.getElementById('typing-indicator');
        if (typingIndicator) {
            typingIndicator.remove();
        }
    },
    
    showError(title, message) {
        const messagesContainer = document.getElementById('chat-messages');
        const errorDiv = document.createElement('div');
        errorDiv.className = 'llm-error';
        errorDiv.innerHTML = `
            <div class="llm-error__title">${title}</div>
            <div class="llm-error__message">${message}</div>
        `;
        messagesContainer.appendChild(errorDiv);
        this.scrollToBottom();
    },
    
    scrollToBottom() {
        const messagesContainer = document.getElementById('chat-messages');
        if (messagesContainer) {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        }
    },
    
    clearChat() {
        const messagesContainer = document.getElementById('chat-messages');
        messagesContainer.innerHTML = `
            <div class="llm-welcome">
                <div class="llm-welcome__icon">
                    <i class="ri-robot-2-line"></i>
                </div>
                <h3>您好！我是 Ooder 智能助手</h3>
                <p>我可以帮助您：</p>
                <ul class="llm-welcome__features">
                    <li><i class="ri-file-text-line"></i> 自动配置场景和能力</li>
                    <li><i class="ri-bar-chart-line"></i> 分析页面数据</li>
                    <li><i class="ri-code-line"></i> 生成代码片段</li>
                    <li><i class="ri-question-line"></i> 解答使用问题</li>
                </ul>
            </div>
        `;
        
        this.messages = [];
        this.messageCount = 0;
        this.totalTokens = 0;
        this.updateStatsDisplay();
        this.clearHistory();
    },
    
    updateStats(data) {
        if (data && data.tokenUsage) {
            this.totalTokens += data.tokenUsage.totalTokens || 0;
        }
        this.updateStatsDisplay();
    },
    
    updateStatsDisplay() {
        const statMessages = document.getElementById('stat-messages');
        const statTokens = document.getElementById('stat-tokens');
        
        if (statMessages) statMessages.textContent = this.messageCount;
        if (statTokens) statTokens.textContent = this.totalTokens;
    },
    
    openSettings() {
        const modal = document.getElementById('settings-modal');
        modal.classList.add('nx-modal--open');
        
        document.getElementById('setting-temperature').value = this.settings.temperature;
        document.getElementById('temperature-value').textContent = this.settings.temperature;
        document.getElementById('setting-max-tokens').value = this.settings.maxTokens;
        document.getElementById('setting-system-prompt').value = this.settings.systemPrompt;
        document.getElementById('setting-auto-context').checked = this.settings.autoContext;
        document.getElementById('setting-stream').checked = this.settings.stream;
    },
    
    closeSettings() {
        const modal = document.getElementById('settings-modal');
        modal.classList.remove('nx-modal--open');
    },
    
    saveSettings() {
        this.settings.temperature = parseFloat(document.getElementById('setting-temperature').value);
        this.settings.maxTokens = parseInt(document.getElementById('setting-max-tokens').value);
        this.settings.systemPrompt = document.getElementById('setting-system-prompt').value;
        this.settings.autoContext = document.getElementById('setting-auto-context').checked;
        this.settings.stream = document.getElementById('setting-stream').checked;
        
        localStorage.setItem('llm-assistant-settings', JSON.stringify(this.settings));
        this.closeSettings();
    },
    
    loadSettings() {
        const saved = localStorage.getItem('llm-assistant-settings');
        if (saved) {
            try {
                this.settings = { ...this.settings, ...JSON.parse(saved) };
            } catch (e) {
                console.error('Failed to load settings:', e);
            }
        }
        
        const temperatureInput = document.getElementById('setting-temperature');
        if (temperatureInput) {
            temperatureInput.addEventListener('input', (e) => {
                document.getElementById('temperature-value').textContent = e.target.value;
            });
        }
    },
    
    saveHistory() {
        const history = this.messages.slice(-20);
        localStorage.setItem('llm-assistant-history', JSON.stringify(history));
    },
    
    loadHistory() {
        const saved = localStorage.getItem('llm-assistant-history');
        if (saved) {
            try {
                this.messages = JSON.parse(saved);
                this.renderHistory();
            } catch (e) {
                console.error('Failed to load history:', e);
            }
        }
    },
    
    renderHistory() {
        const historyList = document.getElementById('history-list');
        if (!historyList) return;
        
        if (this.messages.length === 0) {
            historyList.innerHTML = `
                <li class="llm-history-item llm-history-item--empty">
                    <i class="ri-chat-off-line"></i>
                    <span>暂无历史对话</span>
                </li>
            `;
            return;
        }
        
        historyList.innerHTML = '';
        const userMessages = this.messages.filter(m => m.role === 'user').slice(-5).reverse();
        
        userMessages.forEach(msg => {
            const li = document.createElement('li');
            li.className = 'llm-history-item';
            li.innerHTML = `
                <div class="llm-history-item__title">${msg.content.substring(0, 30)}...</div>
                <div class="llm-history-item__time">${this.formatTime(new Date(msg.timestamp))}</div>
            `;
            historyList.appendChild(li);
        });
    },
    
    clearHistory() {
        localStorage.removeItem('llm-assistant-history');
        this.renderHistory();
    }
};

window.LLMAssistant = LLMAssistant;
