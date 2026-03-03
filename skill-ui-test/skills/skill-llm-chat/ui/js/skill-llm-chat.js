var LlmChat = {
    currentSessionId: null,
    messages: [],
    settings: {
        temperature: 0.7,
        maxTokens: 4096,
        systemPrompt: '',
        streamEnabled: true,
        autoScroll: true
    },
    stats: {
        messages: 0,
        tokens: 0
    },
    currentProvider: '',
    currentModel: '',
    
    init: function() {
        this.loadSettings();
        this.bindEvents();
        this.loadProviders();
        this.loadSessions();
        this.initNewSession();
    },
    
    bindEvents: function() {
        var self = this;
        
        document.getElementById('sendBtn').addEventListener('click', function() {
            self.sendMessage();
        });
        
        document.getElementById('messageInput').addEventListener('keydown', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                self.sendMessage();
            }
        });
        
        document.getElementById('messageInput').addEventListener('input', function() {
            self.autoResize(this);
        });
        
        document.getElementById('clearBtn').addEventListener('click', function() {
            self.clearChat();
        });
        
        document.getElementById('newSessionBtn').addEventListener('click', function() {
            self.initNewSession();
        });
        
        document.getElementById('providerSelect').addEventListener('change', function() {
            self.onProviderChange(this.value);
        });
        
        document.getElementById('modelSelect').addEventListener('change', function() {
            self.currentModel = this.value;
        });
        
        document.getElementById('settingsBtn').addEventListener('click', function() {
            self.openSettings();
        });
        
        document.getElementById('closeSettingsBtn').addEventListener('click', function() {
            self.closeSettings();
        });
        
        document.getElementById('cancelSettingsBtn').addEventListener('click', function() {
            self.closeSettings();
        });
        
        document.getElementById('saveSettingsBtn').addEventListener('click', function() {
            self.saveSettings();
        });
        
        document.getElementById('temperatureSlider').addEventListener('input', function() {
            document.getElementById('temperatureValue').textContent = this.value;
        });
        
        var quickBtns = document.querySelectorAll('.llm-quick-btn');
        quickBtns.forEach(function(btn) {
            btn.addEventListener('click', function() {
                var prompt = this.getAttribute('data-prompt');
                document.getElementById('messageInput').value = prompt;
                self.sendMessage();
            });
        });
    },
    
    loadSettings: function() {
        var saved = localStorage.getItem('llm-chat-settings');
        if (saved) {
            this.settings = JSON.parse(saved);
        }
        
        document.getElementById('temperatureSlider').value = this.settings.temperature;
        document.getElementById('temperatureValue').textContent = this.settings.temperature;
        document.getElementById('maxTokensInput').value = this.settings.maxTokens;
        document.getElementById('systemPromptInput').value = this.settings.systemPrompt || '';
        document.getElementById('streamEnabled').checked = this.settings.streamEnabled;
        document.getElementById('autoScroll').checked = this.settings.autoScroll;
    },
    
    saveSettings: function() {
        this.settings.temperature = parseFloat(document.getElementById('temperatureSlider').value);
        this.settings.maxTokens = parseInt(document.getElementById('maxTokensInput').value);
        this.settings.systemPrompt = document.getElementById('systemPromptInput').value;
        this.settings.streamEnabled = document.getElementById('streamEnabled').checked;
        this.settings.autoScroll = document.getElementById('autoScroll').checked;
        
        localStorage.setItem('llm-chat-settings', JSON.stringify(this.settings));
        this.closeSettings();
    },
    
    openSettings: function() {
        document.getElementById('settingsModal').classList.add('nx-modal--open');
    },
    
    closeSettings: function() {
        document.getElementById('settingsModal').classList.remove('nx-modal--open');
    },
    
    loadProviders: function() {
        var self = this;
        NexusAPI.get('/api/llm/providers', function(res) {
            if (res.status === 'success' && res.data) {
                var select = document.getElementById('providerSelect');
                select.innerHTML = '<option value="">选择Provider</option>';
                
                res.data.forEach(function(provider) {
                    var option = document.createElement('option');
                    option.value = provider.id || provider.name;
                    option.textContent = provider.name || provider.id;
                    select.appendChild(option);
                });
                
                if (res.data.length > 0) {
                    select.value = res.data[0].id || res.data[0].name;
                    self.onProviderChange(select.value);
                }
            }
        });
    },
    
    onProviderChange: function(providerId) {
        var self = this;
        this.currentProvider = providerId;
        
        NexusAPI.get('/api/llm/models?provider=' + providerId, function(res) {
            if (res.status === 'success' && res.data) {
                var select = document.getElementById('modelSelect');
                select.innerHTML = '<option value="">选择模型</option>';
                
                res.data.forEach(function(model) {
                    var option = document.createElement('option');
                    option.value = model.id || model.name;
                    option.textContent = model.name || model.id;
                    select.appendChild(option);
                });
                
                if (res.data.length > 0) {
                    select.value = res.data[0].id || res.data[0].name;
                    self.currentModel = select.value;
                }
            }
        });
    },
    
    loadSessions: function() {
        var self = this;
        NexusAPI.get('/api/llm/sessions', function(res) {
            if (res.status === 'success' && res.data) {
                self.renderSessions(res.data);
            }
        });
    },
    
    renderSessions: function(sessions) {
        var container = document.getElementById('sessionList');
        
        if (!sessions || sessions.length === 0) {
            container.innerHTML = '<div class="llm-session-empty"><i class="ri-chat-off-line"></i><span>暂无历史会话</span></div>';
            return;
        }
        
        var html = '';
        sessions.forEach(function(session) {
            var isActive = session.id === this.currentSessionId ? 'active' : '';
            html += '<div class="llm-session-item ' + isActive + '" data-id="' + session.id + '">';
            html += '<span class="llm-session-title">' + (session.title || '新对话') + '</span>';
            html += '<span class="llm-session-time">' + this.formatTime(session.updateTime) + '</span>';
            html += '</div>';
        }.bind(this));
        
        container.innerHTML = html;
        
        var items = container.querySelectorAll('.llm-session-item');
        items.forEach(function(item) {
            item.addEventListener('click', function() {
                var id = this.getAttribute('data-id');
                this.loadSession(id);
            }.bind(this));
        }.bind(this));
    },
    
    loadSession: function(sessionId) {
        var self = this;
        this.currentSessionId = sessionId;
        
        NexusAPI.get('/api/llm/sessions/' + sessionId + '/history', function(res) {
            if (res.status === 'success' && res.data) {
                self.messages = res.data;
                self.renderMessages();
                self.updateContextInfo();
            }
        });
        
        this.loadSessions();
    },
    
    initNewSession: function() {
        this.currentSessionId = 'session_' + Date.now();
        this.messages = [];
        
        document.getElementById('chatMessages').innerHTML = '<div class="llm-welcome">' +
            '<div class="llm-welcome__icon"><i class="ri-robot-2-line"></i></div>' +
            '<h3>欢迎使用 LLM 智能对话</h3>' +
            '<p>我是您的智能助手，可以帮助您：</p>' +
            '<ul class="llm-welcome__features">' +
            '<li><i class="ri-chat-3-line"></i> 进行自然语言对话</li>' +
            '<li><i class="ri-lightbulb-line"></i> 回答问题和提供建议</li>' +
            '<li><i class="ri-code-line"></i> 生成代码和文档</li>' +
            '<li><i class="ri-file-text-line"></i> 分析和处理文本</li>' +
            '</ul></div>';
        
        this.updateContextInfo();
    },
    
    sendMessage: function() {
        var input = document.getElementById('messageInput');
        var message = input.value.trim();
        
        if (!message) return;
        
        this.addMessage('user', message);
        input.value = '';
        this.autoResize(input);
        
        this.showLoading();
        
        var self = this;
        var data = {
            message: message,
            sessionId: this.currentSessionId,
            provider: this.currentProvider,
            model: this.currentModel,
            temperature: this.settings.temperature,
            maxTokens: this.settings.maxTokens,
            systemPrompt: this.settings.systemPrompt,
            stream: this.settings.streamEnabled
        };
        
        if (this.settings.streamEnabled) {
            this.sendStreamMessage(data);
        } else {
            this.sendNormalMessage(data);
        }
    },
    
    sendNormalMessage: function(data) {
        var self = this;
        
        NexusAPI.post('/api/llm/chat', data, function(res) {
            self.hideLoading();
            
            if (res.status === 'success' && res.data) {
                self.addMessage('assistant', res.data.response || res.data.content);
                self.updateStats(res.data.usage);
            } else {
                self.addMessage('assistant', '抱歉，发生了错误：' + (res.message || '未知错误'));
            }
        });
    },
    
    sendStreamMessage: function(data) {
        var self = this;
        var container = document.getElementById('chatMessages');
        
        var messageDiv = document.createElement('div');
        messageDiv.className = 'llm-message llm-message--assistant';
        messageDiv.innerHTML = '<div class="llm-message__avatar"><i class="ri-robot-line"></i></div>' +
            '<div class="llm-message__content" id="stream-content"></div>';
        
        this.hideLoading();
        container.appendChild(messageDiv);
        
        var contentDiv = document.getElementById('stream-content');
        var fullContent = '';
        
        var eventSource = new EventSource('/api/llm/chat/stream?' + new URLSearchParams(data));
        
        eventSource.onmessage = function(event) {
            var chunk = event.data;
            if (chunk === '[DONE]') {
                eventSource.close();
                self.messages.push({ role: 'assistant', content: fullContent });
                self.updateStats({ totalTokens: fullContent.length });
                return;
            }
            
            try {
                var json = JSON.parse(chunk);
                if (json.content) {
                    fullContent += json.content;
                    contentDiv.innerHTML = self.formatContent(fullContent);
                    
                    if (self.settings.autoScroll) {
                        container.scrollTop = container.scrollHeight;
                    }
                }
            } catch (e) {
                fullContent += chunk;
                contentDiv.innerHTML = self.formatContent(fullContent);
            }
        };
        
        eventSource.onerror = function() {
            eventSource.close();
            if (!fullContent) {
                contentDiv.innerHTML = '<span style="color: var(--nx-danger);">连接中断，请重试</span>';
            }
        };
        
        if (self.settings.autoScroll) {
            container.scrollTop = container.scrollHeight;
        }
    },
    
    addMessage: function(role, content) {
        this.messages.push({ role: role, content: content });
        
        var container = document.getElementById('chatMessages');
        
        var welcomeDiv = container.querySelector('.llm-welcome');
        if (welcomeDiv) {
            welcomeDiv.remove();
        }
        
        var messageDiv = document.createElement('div');
        messageDiv.className = 'llm-message llm-message--' + role;
        
        var icon = role === 'user' ? 'ri-user-line' : 'ri-robot-line';
        messageDiv.innerHTML = '<div class="llm-message__avatar"><i class="' + icon + '"></i></div>' +
            '<div class="llm-message__content">' + this.formatContent(content) + '</div>';
        
        container.appendChild(messageDiv);
        
        if (this.settings.autoScroll) {
            container.scrollTop = container.scrollHeight;
        }
        
        this.updateContextInfo();
    },
    
    formatContent: function(content) {
        content = content.replace(/```(\w*)\n([\s\S]*?)```/g, '<pre><code class="language-$1">$2</code></pre>');
        content = content.replace(/`([^`]+)`/g, '<code>$1</code>');
        content = content.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');
        content = content.replace(/\*([^*]+)\*/g, '<em>$1</em>');
        content = content.replace(/\n/g, '<br>');
        return content;
    },
    
    showLoading: function() {
        var container = document.getElementById('chatMessages');
        
        var loadingDiv = document.createElement('div');
        loadingDiv.className = 'llm-message llm-message--assistant llm-message--loading';
        loadingDiv.id = 'loading-message';
        loadingDiv.innerHTML = '<div class="llm-message__avatar"><i class="ri-robot-line"></i></div>' +
            '<div class="llm-message__content">' +
            '<div class="llm-loading-dot"></div>' +
            '<div class="llm-loading-dot"></div>' +
            '<div class="llm-loading-dot"></div>' +
            '</div>';
        
        container.appendChild(loadingDiv);
        container.scrollTop = container.scrollHeight;
    },
    
    hideLoading: function() {
        var loadingDiv = document.getElementById('loading-message');
        if (loadingDiv) {
            loadingDiv.remove();
        }
    },
    
    renderMessages: function() {
        var container = document.getElementById('chatMessages');
        container.innerHTML = '';
        
        this.messages.forEach(function(msg) {
            var messageDiv = document.createElement('div');
            messageDiv.className = 'llm-message llm-message--' + msg.role;
            
            var icon = msg.role === 'user' ? 'ri-user-line' : 'ri-robot-line';
            messageDiv.innerHTML = '<div class="llm-message__avatar"><i class="' + icon + '"></i></div>' +
                '<div class="llm-message__content">' + this.formatContent(msg.content) + '</div>';
            
            container.appendChild(messageDiv);
        }.bind(this));
        
        container.scrollTop = container.scrollHeight;
    },
    
    clearChat: function() {
        if (confirm('确定要清空当前对话吗？')) {
            this.initNewSession();
        }
    },
    
    updateStats: function(usage) {
        if (usage) {
            this.stats.messages++;
            this.stats.tokens += usage.totalTokens || 0;
            
            document.getElementById('statMessages').textContent = this.stats.messages;
            document.getElementById('statTokens').textContent = this.stats.tokens;
            document.getElementById('tokenCount').textContent = 'Token: ' + (usage.totalTokens || 0);
        }
    },
    
    updateContextInfo: function() {
        document.getElementById('ctxSessionId').textContent = this.currentSessionId ? this.currentSessionId.substring(0, 12) + '...' : '-';
        document.getElementById('ctxMessageCount').textContent = this.messages.length;
        document.getElementById('ctxTotalTokens').textContent = this.stats.tokens;
    },
    
    autoResize: function(textarea) {
        textarea.style.height = 'auto';
        textarea.style.height = Math.min(textarea.scrollHeight, 150) + 'px';
    },
    
    formatTime: function(timestamp) {
        if (!timestamp) return '';
        var date = new Date(timestamp);
        var now = new Date();
        var diff = now - date;
        
        if (diff < 60000) return '刚刚';
        if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
        if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
        return date.toLocaleDateString();
    }
};

document.addEventListener('DOMContentLoaded', function() {
    LlmChat.init();
});
