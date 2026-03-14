/**
 * LLM Assistant - LLM 助手模块
 * 提供与 LLM 交互的功能
 */

const LlmAssistant = {
    initialized: false,
    config: {
        enabled: true,
        endpoint: '/api/v1/llm',
        model: 'default'
    },

    async init() {
        console.log('[LlmAssistant] Initializing...');
        this.initialized = true;
        console.log('[LlmAssistant] Initialized successfully');
        return true;
    },

    async chat(message, context = {}) {
        if (!this.initialized) {
            await this.init();
        }

        try {
            const response = await fetch(`${this.config.endpoint}/chat`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    message,
                    context,
                    model: this.config.model
                })
            });

            const result = await response.json();
            return result;
        } catch (error) {
            console.error('[LlmAssistant] Chat error:', error);
            return {
                code: 500,
                message: 'LLM 服务暂时不可用',
                data: null
            };
        }
    },

    isEnabled() {
        return this.config.enabled;
    },

    setEnabled(enabled) {
        this.config.enabled = enabled;
    }
};

window.LlmAssistant = LlmAssistant;

document.addEventListener('DOMContentLoaded', () => {
    LlmAssistant.init().catch(err => {
        console.warn('[LlmAssistant] Failed to initialize:', err);
    });
});
