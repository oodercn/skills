const FloatingAssistant = {
    isOpen: false,
    isMinimized: false,
    messages: [],
    context: null,
    isStreaming: false,
    
    init() {
        this.loadState();
        ContextCollector.init();
        
        if (this.isOpen) {
            this.open();
        }
        
        this.refreshContext();
    },
    
    loadState() {
        const saved = localStorage.getItem('llm-floating-state');
        if (saved) {
            try {
                const state = JSON.parse(saved);
                this.isOpen = state.isOpen || false;
                this.messages = state.messages || [];
            } catch (e) {
                console.error('Failed to load state:', e);
            }
        }
    },
    
    saveState() {
        localStorage.setItem('llm-floating-state', JSON.stringify({
            isOpen: this.isOpen,
            messages: this.messages.slice(-10)
        }));
    },
    
    toggle() {
        if (this.isOpen) {
            this.close();
        } else {
            this.open();
        }
    },
    
    open() {
        const panel = document.getElementById('llm-panel');
        const trigger = document.getElementById('llm-trigger');
        
        if (panel) {
            panel.classList.add('llm-panel--open');
            this.isOpen = true;
            this.saveState();
            
            if (trigger) {
                trigger.style.display = 'none';
            }
            
            this.renderMessages();
            this.refreshContext();
        }
    },
    
    close() {
        const panel = document.getElementById('llm-panel');
        const trigger = document.getElementById('llm-trigger');
        
        if (panel) {
            panel.classList.remove('llm-panel--open');
            this.isOpen = false;
            this.saveState();
            
            if (trigger) {
                trigger.style.display = 'flex';
            }
        }
    },
    
    minimize() {
        const panel = document.getElementById('llm-panel');
        const body = document.getElementById('llm-body');
        const footer = panel.querySelector('.llm-floating-footer');
        
        if (panel) {
            panel.classList.toggle('llm-panel--minimized');
            this.isMinimized = panel.classList.contains('llm-panel--minimized');
            
            if (body) body.style.display = this.isMinimized ? 'none' : 'flex';
            if (footer) footer.style.display = this.isMinimized ? 'none' : 'block';
        }
    },
    
    async refreshContext() {
        const statusEl = document.getElementById('llm-context-status');
        
        if (statusEl) {
            statusEl.innerHTML = '<i class="ri-loader-4-line ri-spin"></i><span>获取上下文...</span>';
        }
        
        try {
            this.context = await ContextCollector.buildContext();
            
            if (statusEl) {
                statusEl.innerHTML = '<i class="ri-context-line"></i><span>已获取上下文</span>';
            }
        } catch (error) {
            console.error('Failed to refresh context:', error);
            if (statusEl) {
                statusEl.innerHTML = '<i class="ri-error-warning-line"></i><span>上下文获取失败</span>';
            }
        }
    },
    
    handleKeydown(event) {
        if (event.key === 'Enter') {
            event.preventDefault();
            this.send();
        }
    },
    
    send() {
        const input = document.getElementById('llm-input');
        const message = input.value.trim();
        
        if (!message || this.isStreaming) return;
        
        input.value = '';
        this.addMessage('user', message);
        this.showTyping();
        this.sendMessage(message);
    },
    
    sendQuick(message) {
        const input = document.getElementById('llm-input');
        input.value = message;
        this.send();
    },
    
    async sendMessage(message) {
        this.isStreaming = true;
        
        const request = {
            prompt: message,
            provider: 'mock',
            model: 'default',
            temperature: 0.7,
            maxTokens: 2048
        };
        
        if (this.context && this.context.formattedPrompt) {
            request.systemPrompt = this.context.formattedPrompt;
        }
        
        try {
            const response = await NexusApi.post('/api/llm/chat', request);
            
            this.hideTyping();
            
            if (response.status === 'success' && response.data) {
                this.addMessage('assistant', response.data.response);
            } else {
                this.showError(response.message || '请求失败');
            }
        } catch (error) {
            this.hideTyping();
            this.showError(error.message || '网络错误');
        } finally {
            this.isStreaming = false;
        }
    },
    
    addMessage(role, content) {
        this.messages.push({
            role: role,
            content: content,
            timestamp: Date.now()
        });
        
        this.renderMessages();
        this.saveState();
    },
    
    renderMessages() {
        const container = document.getElementById('llm-messages');
        if (!container) return;
        
        if (this.messages.length === 0) {
            container.innerHTML = `
                <div class="llm-welcome llm-welcome--compact">
                    <div class="llm-welcome__icon llm-welcome__icon--sm">
                        <i class="ri-robot-2-line"></i>
                    </div>
                    <h4>您好！我是智能助手</h4>
                    <p>有什么可以帮您的？</p>
                </div>
            `;
            return;
        }
        
        container.innerHTML = '';
        
        this.messages.slice(-10).forEach(msg => {
            const div = document.createElement('div');
            div.className = `llm-floating-message llm-floating-message--${msg.role}`;
            div.innerHTML = `
                <div class="llm-floating-bubble">${this.formatContent(msg.content)}</div>
            `;
            container.appendChild(div);
        });
        
        this.scrollToBottom();
    },
    
    formatContent(content) {
        if (!content) return '';
        
        content = content.replace(/```(\w*)\n([\s\S]*?)```/g, '<pre><code>$2</code></pre>');
        content = content.replace(/`([^`]+)`/g, '<code>$1</code>');
        content = content.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');
        content = content.replace(/\n/g, '<br>');
        
        return content;
    },
    
    showTyping() {
        const container = document.getElementById('llm-messages');
        if (!container) return;
        
        const welcome = container.querySelector('.llm-welcome');
        if (welcome) {
            welcome.remove();
        }
        
        const typing = document.createElement('div');
        typing.className = 'llm-floating-message llm-floating-message--assistant';
        typing.id = 'llm-typing';
        typing.innerHTML = `
            <div class="llm-floating-bubble">
                <div class="llm-floating-typing">
                    <span></span>
                    <span></span>
                    <span></span>
                </div>
            </div>
        `;
        container.appendChild(typing);
        this.scrollToBottom();
    },
    
    hideTyping() {
        const typing = document.getElementById('llm-typing');
        if (typing) {
            typing.remove();
        }
    },
    
    showError(message) {
        const container = document.getElementById('llm-messages');
        if (!container) return;
        
        const error = document.createElement('div');
        error.className = 'llm-floating-error';
        error.textContent = message;
        container.appendChild(error);
        
        setTimeout(() => error.remove(), 5000);
    },
    
    scrollToBottom() {
        const container = document.getElementById('llm-messages');
        if (container) {
            container.scrollTop = container.scrollHeight;
        }
    },
    
    clear() {
        this.messages = [];
        this.renderMessages();
        this.saveState();
    }
};

window.FloatingAssistant = FloatingAssistant;
