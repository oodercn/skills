/**
 * LLM Chat Float Component
 * LLM聊天悬浮窗口组件
 */

(function() {
    'use strict';

    const LLMChatFloat = {
        initialized: false,
        isOpen: false,
        useKnowledge: false,
        isStreaming: false,
        config: {
            enabled: true,
            position: 'bottom-right',
            title: 'AI助手',
            sessionId: 'session-default'
        },

        async init() {
            if (this.initialized) return;

            const savedConfig = localStorage.getItem('llm-chat-float-config');
            if (savedConfig) {
                try {
                    this.config = { ...this.config, ...JSON.parse(savedConfig) };
                } catch (e) {}
            }

            if (!this.config.enabled) return;

            this.render();
            this.bindEvents();
            this.initialized = true;

            console.log('[LLMChatFloat] Initialized');
        },

        render() {
            const container = document.createElement('div');
            container.id = 'llm-chat-float-container';
            container.innerHTML = `
                <div class="llm-chat-float-btn" id="llmChatFloatBtn" title="AI助手">
                    <i class="ri-robot-2-line"></i>
                    <span class="llm-chat-float-badge" id="llmChatBadge" style="display: none;">1</span>
                </div>
                <div class="llm-chat-float-window" id="llmChatFloatWindow">
                    <div class="llm-chat-float-header">
                        <div class="llm-chat-float-title">
                            <i class="ri-robot-2-line"></i>
                            <span>${this.config.title}</span>
                        </div>
                        <div class="llm-chat-float-actions">
                            <button class="llm-chat-float-action-btn" id="llmChatKnowledgeBtn" title="知识资料库">
                                <i class="ri-book-2-line"></i>
                            </button>
                            <button class="llm-chat-float-action-btn" id="llmChatNewBtn" title="新对话">
                                <i class="ri-add-line"></i>
                            </button>
                            <button class="llm-chat-float-action-btn" id="llmChatCloseBtn" title="关闭">
                                <i class="ri-close-line"></i>
                            </button>
                        </div>
                    </div>
                    <div class="llm-chat-float-messages" id="llmChatMessages">
                        <div class="llm-chat-float-message llm-chat-float-message--assistant">
                            <div class="llm-chat-float-avatar">
                                <i class="ri-robot-2-line"></i>
                            </div>
                            <div class="llm-chat-float-content">
                                您好！我是AI助手，有什么可以帮助您的吗？
                            </div>
                        </div>
                    </div>
                    <div class="llm-chat-float-input-area">
                        <div class="llm-chat-float-knowledge-toggle" id="llmChatKnowledgeToggle">
                            <input type="checkbox" id="llmChatUseKnowledge" class="llm-chat-float-checkbox">
                            <i class="ri-database-2-line"></i>
                            <span>使用知识库</span>
                        </div>
                        <div class="llm-chat-float-input-wrapper">
                            <textarea class="llm-chat-float-input" id="llmChatInput" 
                                placeholder="输入您的问题..." rows="1"></textarea>
                            <button class="llm-chat-float-send" id="llmChatSendBtn">
                                <i class="ri-send-plane-line"></i>
                            </button>
                        </div>
                    </div>
                </div>
            `;

            document.body.appendChild(container);
            this.addStyles();
        },

        addStyles() {
            if (document.getElementById('llm-chat-float-styles')) return;

            const style = document.createElement('style');
            style.id = 'llm-chat-float-styles';
            style.textContent = `
                #llm-chat-float-container {
                    position: fixed;
                    z-index: 9999;
                    font-family: var(--nx-font-family, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif);
                }

                .llm-chat-float-btn {
                    position: fixed;
                    bottom: 24px;
                    right: 24px;
                    width: 56px;
                    height: 56px;
                    border-radius: 50%;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    cursor: pointer;
                    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
                    transition: all 0.3s ease;
                    font-size: 24px;
                }

                .llm-chat-float-btn:hover {
                    transform: scale(1.1);
                    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
                }

                .llm-chat-float-badge {
                    position: absolute;
                    top: -4px;
                    right: -4px;
                    background: #f44336;
                    color: white;
                    font-size: 12px;
                    min-width: 18px;
                    height: 18px;
                    border-radius: 9px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    padding: 0 4px;
                }

                .llm-chat-float-window {
                    position: fixed;
                    bottom: 96px;
                    right: 24px;
                    width: 380px;
                    height: 520px;
                    background: var(--nx-bg-primary, #fff);
                    border-radius: 16px;
                    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
                    display: none;
                    flex-direction: column;
                    overflow: hidden;
                    border: 1px solid var(--nx-border-color, #e0e0e0);
                    color: var(--nx-text-primary, #333);
                }

                .llm-chat-float-window.open {
                    display: flex;
                    animation: slideUp 0.3s ease;
                }

                @keyframes slideUp {
                    from { opacity: 0; transform: translateY(20px); }
                    to { opacity: 1; transform: translateY(0); }
                }

                .llm-chat-float-header {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    padding: 16px;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                }

                .llm-chat-float-title {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    font-weight: 600;
                    font-size: 16px;
                }

                .llm-chat-float-actions {
                    display: flex;
                    gap: 8px;
                }

                .llm-chat-float-action-btn {
                    background: rgba(255, 255, 255, 0.2);
                    border: none;
                    color: white;
                    width: 32px;
                    height: 32px;
                    border-radius: 8px;
                    cursor: pointer;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    transition: background 0.2s;
                }

                .llm-chat-float-action-btn:hover {
                    background: rgba(255, 255, 255, 0.3);
                }

                .llm-chat-float-messages {
                    flex: 1;
                    overflow-y: auto;
                    padding: 16px;
                    display: flex;
                    flex-direction: column;
                    gap: 12px;
                    background: var(--nx-bg-secondary, #f9f9f9);
                }

                .llm-chat-float-message {
                    display: flex;
                    gap: 10px;
                    max-width: 90%;
                }

                .llm-chat-float-message--user {
                    flex-direction: row-reverse;
                    margin-left: auto;
                }

                .llm-chat-float-avatar {
                    width: 32px;
                    height: 32px;
                    border-radius: 50%;
                    background: var(--nx-bg-tertiary, #e0e0e0);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    flex-shrink: 0;
                    font-size: 16px;
                    color: var(--nx-text-secondary, #666);
                }

                .llm-chat-float-message--user .llm-chat-float-avatar {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                }

                .llm-chat-float-content {
                    padding: 10px 14px;
                    border-radius: 12px;
                    font-size: 14px;
                    line-height: 1.6;
                    background: var(--nx-bg-elevated, #fff);
                    border: 1px solid var(--nx-border-color, #e0e0e0);
                    color: var(--nx-text-primary, #333);
                }

                .llm-chat-float-message--user .llm-chat-float-content {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                    border: none;
                }

                .llm-chat-float-input-area {
                    padding: 12px 16px;
                    border-top: 1px solid var(--nx-border-color, #e0e0e0);
                    background: var(--nx-bg-primary, #fff);
                }

                .llm-chat-float-knowledge-toggle {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    font-size: 12px;
                    color: var(--nx-text-secondary, #666);
                    cursor: pointer;
                    margin-bottom: 8px;
                    padding: 6px 10px;
                    border-radius: 6px;
                    transition: all 0.2s;
                    user-select: none;
                }

                .llm-chat-float-knowledge-toggle:hover {
                    background: var(--nx-bg-tertiary, #eee);
                }

                .llm-chat-float-knowledge-toggle.active {
                    background: rgba(102, 126, 234, 0.15);
                    color: #667eea;
                }

                .llm-chat-float-checkbox {
                    width: 16px;
                    height: 16px;
                    cursor: pointer;
                    accent-color: #667eea;
                }

                .llm-chat-float-input-wrapper {
                    display: flex;
                    gap: 8px;
                    align-items: flex-end;
                }

                .llm-chat-float-input {
                    flex: 1;
                    border: 1px solid var(--nx-border-color, #e0e0e0);
                    border-radius: 20px;
                    padding: 10px 16px;
                    font-size: 14px;
                    resize: none;
                    max-height: 100px;
                    outline: none;
                    font-family: inherit;
                    background: var(--nx-bg-secondary, #f9f9f9);
                    color: var(--nx-text-primary, #333);
                }

                .llm-chat-float-input:focus {
                    border-color: #667eea;
                    background: var(--nx-bg-primary, #fff);
                }

                .llm-chat-float-input::placeholder {
                    color: var(--nx-text-tertiary, #999);
                }

                .llm-chat-float-send {
                    width: 40px;
                    height: 40px;
                    border-radius: 50%;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                    border: none;
                    cursor: pointer;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    transition: all 0.2s;
                }

                .llm-chat-float-send:hover {
                    transform: scale(1.1);
                }

                .llm-chat-float-send:disabled {
                    opacity: 0.5;
                    cursor: not-allowed;
                    transform: none;
                }

                .llm-chat-float-typing {
                    display: flex;
                    gap: 4px;
                    padding: 8px 0;
                }

                .llm-chat-float-typing span {
                    width: 8px;
                    height: 8px;
                    background: #667eea;
                    border-radius: 50%;
                    animation: typing 1.4s infinite ease-in-out both;
                }

                .llm-chat-float-typing span:nth-child(1) { animation-delay: -0.32s; }
                .llm-chat-float-typing span:nth-child(2) { animation-delay: -0.16s; }

                @keyframes typing {
                    0%, 80%, 100% { transform: scale(0); }
                    40% { transform: scale(1); }
                }

                /* 深色主题 - 使用 CSS 变量自动适配 */
                [data-theme="dark"] .llm-chat-float-window,
                .nx-theme-dark .llm-chat-float-window {
                    background: var(--nx-bg-primary, #1a1a2e) !important;
                    border-color: var(--nx-border-color, #2a2a3e) !important;
                }

                [data-theme="dark"] .llm-chat-float-messages,
                .nx-theme-dark .llm-chat-float-messages {
                    background: var(--nx-bg-secondary, #16162a) !important;
                }

                [data-theme="dark"] .llm-chat-float-content,
                .nx-theme-dark .llm-chat-float-content {
                    background: var(--nx-bg-elevated, #1e1e32) !important;
                    border-color: var(--nx-border-color, #2a2a3e) !important;
                    color: var(--nx-text-primary, #e0e0e0) !important;
                }

                [data-theme="dark"] .llm-chat-float-input-area,
                .nx-theme-dark .llm-chat-float-input-area {
                    background: var(--nx-bg-primary, #1a1a2e) !important;
                    border-color: var(--nx-border-color, #2a2a3e) !important;
                }

                [data-theme="dark"] .llm-chat-float-input,
                .nx-theme-dark .llm-chat-float-input {
                    background: var(--nx-bg-secondary, #16162a) !important;
                    border-color: var(--nx-border-color, #2a2a3e) !important;
                    color: var(--nx-text-primary, #e0e0e0) !important;
                }

                [data-theme="dark"] .llm-chat-float-input::placeholder,
                .nx-theme-dark .llm-chat-float-input::placeholder {
                    color: var(--nx-text-tertiary, #666) !important;
                }

                [data-theme="dark"] .llm-chat-float-knowledge-toggle,
                .nx-theme-dark .llm-chat-float-knowledge-toggle {
                    color: var(--nx-text-secondary, #a0a0a0) !important;
                }

                [data-theme="dark"] .llm-chat-float-knowledge-toggle:hover,
                .nx-theme-dark .llm-chat-float-knowledge-toggle:hover {
                    background: var(--nx-bg-tertiary, #2a2a3e) !important;
                }

                [data-theme="dark"] .llm-chat-float-avatar,
                .nx-theme-dark .llm-chat-float-avatar {
                    background: var(--nx-bg-tertiary, #2a2a3e) !important;
                    color: var(--nx-text-secondary, #a0a0a0) !important;
                }
            `;

            document.head.appendChild(style);
        },

        bindEvents() {
            const btn = document.getElementById('llmChatFloatBtn');
            const closeBtn = document.getElementById('llmChatCloseBtn');
            const sendBtn = document.getElementById('llmChatSendBtn');
            const input = document.getElementById('llmChatInput');
            const knowledgeToggle = document.getElementById('llmChatKnowledgeToggle');
            const knowledgeBtn = document.getElementById('llmChatKnowledgeBtn');
            const newBtn = document.getElementById('llmChatNewBtn');
            const checkbox = document.getElementById('llmChatUseKnowledge');

            btn.addEventListener('click', () => this.toggle());
            closeBtn.addEventListener('click', () => this.close());
            sendBtn.addEventListener('click', () => this.sendMessage());
            
            input.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    this.sendMessage();
                }
            });

            knowledgeToggle.addEventListener('click', (e) => {
                if (e.target.type !== 'checkbox') {
                    checkbox.checked = !checkbox.checked;
                }
                this.useKnowledge = checkbox.checked;
                knowledgeToggle.classList.toggle('active', this.useKnowledge);
            });

            knowledgeBtn.addEventListener('click', () => {
                window.open('/console/skills/skill-llm-chat/pages/chat.html', '_blank');
            });

            newBtn.addEventListener('click', () => {
                this.clearChat();
            });
        },

        toggle() {
            this.isOpen = !this.isOpen;
            const window = document.getElementById('llmChatFloatWindow');
            window.classList.toggle('open', this.isOpen);
            
            if (this.isOpen) {
                document.getElementById('llmChatInput').focus();
            }
        },

        close() {
            this.isOpen = false;
            document.getElementById('llmChatFloatWindow').classList.remove('open');
        },

        async sendMessage() {
            if (this.isStreaming) return;
            
            const input = document.getElementById('llmChatInput');
            const content = input.value.trim();
            
            if (!content) return;

            this.addMessage('user', content);
            input.value = '';
            
            this.isStreaming = true;
            this.updateSendButton(true);

            const messageId = 'msg-' + Date.now();
            this.addStreamingMessage(messageId);

            try {
                // 直接使用非流式 API，更可靠
                const response = await fetch(`/api/v1/chat/sessions/${this.config.sessionId}/messages`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ content, useKnowledge: this.useKnowledge })
                });

                const result = await response.json();

                if (result.status === 'success' && result.data && result.data.content) {
                    // 打字机效果显示
                    await this.typeWriterEffect(messageId, result.data.content);
                } else {
                    this.updateStreamingMessage(messageId, '抱歉，未收到有效回复。');
                }

            } catch (e) {
                console.error('[LLMChatFloat] Error:', e);
                this.updateStreamingMessage(messageId, '抱歉，网络连接失败，请稍后重试。');
            } finally {
                this.isStreaming = false;
                this.updateSendButton(false);
            }
        },

        async typeWriterEffect(messageId, text) {
            const messageEl = document.getElementById(messageId);
            if (!messageEl) return;

            const contentEl = messageEl.querySelector('.llm-chat-float-content');
            if (!contentEl) return;

            let displayText = '';
            const chars = text.split('');
            
            for (let i = 0; i < chars.length; i++) {
                displayText += chars[i];
                contentEl.innerHTML = this.escapeHtml(displayText);
                
                const container = document.getElementById('llmChatMessages');
                container.scrollTop = container.scrollHeight;
                
                // 随机延迟，模拟真实打字效果
                await new Promise(resolve => setTimeout(resolve, 20 + Math.random() * 30));
            }
        },

        addMessage(role, content) {
            const container = document.getElementById('llmChatMessages');
            const isUser = role === 'user';

            const messageHtml = `
                <div class="llm-chat-float-message llm-chat-float-message--${role}">
                    <div class="llm-chat-float-avatar">
                        <i class="ri-${isUser ? 'user' : 'robot-2'}-line"></i>
                    </div>
                    <div class="llm-chat-float-content">${this.escapeHtml(content)}</div>
                </div>
            `;

            container.insertAdjacentHTML('beforeend', messageHtml);
            container.scrollTop = container.scrollHeight;
        },

        addStreamingMessage(messageId) {
            const container = document.getElementById('llmChatMessages');

            const messageHtml = `
                <div class="llm-chat-float-message llm-chat-float-message--assistant" id="${messageId}">
                    <div class="llm-chat-float-avatar">
                        <i class="ri-robot-2-line"></i>
                    </div>
                    <div class="llm-chat-float-content">
                        <div class="llm-chat-float-typing">
                            <span></span>
                            <span></span>
                            <span></span>
                        </div>
                    </div>
                </div>
            `;

            container.insertAdjacentHTML('beforeend', messageHtml);
            container.scrollTop = container.scrollHeight;
        },

        updateStreamingMessage(messageId, content) {
            const messageEl = document.getElementById(messageId);
            if (!messageEl) return;

            const contentEl = messageEl.querySelector('.llm-chat-float-content');
            if (contentEl) {
                contentEl.innerHTML = this.escapeHtml(content);
            }

            const container = document.getElementById('llmChatMessages');
            container.scrollTop = container.scrollHeight;
        },

        updateSendButton(disabled) {
            const sendBtn = document.getElementById('llmChatSendBtn');
            sendBtn.disabled = disabled;
        },

        escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML.replace(/\n/g, '<br>');
        },

        clearChat() {
            document.getElementById('llmChatMessages').innerHTML = `
                <div class="llm-chat-float-message llm-chat-float-message--assistant">
                    <div class="llm-chat-float-avatar">
                        <i class="ri-robot-2-line"></i>
                    </div>
                    <div class="llm-chat-float-content">
                        对话已清空，有什么可以帮助您的吗？
                    </div>
                </div>
            `;
        },

        enable() {
            this.config.enabled = true;
            this.saveConfig();
            if (!this.initialized) {
                this.init();
            } else {
                document.getElementById('llm-chat-float-container').style.display = '';
            }
        },

        disable() {
            this.config.enabled = false;
            this.saveConfig();
            const container = document.getElementById('llm-chat-float-container');
            if (container) {
                container.style.display = 'none';
            }
        },

        saveConfig() {
            localStorage.setItem('llm-chat-float-config', JSON.stringify(this.config));
        }
    };

    window.LLMChatFloat = LLMChatFloat;

    document.addEventListener('DOMContentLoaded', () => {
        LLMChatFloat.init();
    });

})();
