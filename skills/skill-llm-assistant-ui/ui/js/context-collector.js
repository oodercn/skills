const ContextCollector = {
    context: null,
    pageData: {},
    
    init() {
        this.collect();
        this.setupListeners();
    },
    
    setupListeners() {
        document.addEventListener('click', (e) => {
            if (e.target.closest('[data-context-track]')) {
                this.trackAction(e);
            }
        });
        
        document.addEventListener('change', (e) => {
            if (e.target.matches('input, select, textarea')) {
                this.trackFormChange(e);
            }
        });
    },
    
    collect() {
        this.context = {
            pageType: this.getPageType(),
            pageUrl: window.location.href,
            pageTitle: document.title,
            pageData: this.getPageData(),
            selectedItems: this.getSelectedItems(),
            formData: this.getFormData(),
            userAction: this.getLastUserAction(),
            timestamp: Date.now()
        };
        
        return this.context;
    },
    
    getPageType() {
        const path = window.location.pathname;
        const match = path.match(/\/pages\/(\w+)\.html/);
        if (match) {
            return match[1];
        }
        
        const segments = path.split('/').filter(s => s);
        if (segments.length > 0) {
            return segments[segments.length - 1].replace('.html', '');
        }
        
        return 'unknown';
    },
    
    getPageData() {
        const data = {};
        
        const params = new URLSearchParams(window.location.search);
        params.forEach((value, key) => {
            data[key] = value;
        });
        
        const dataElements = document.querySelectorAll('[data-context]');
        dataElements.forEach(el => {
            const key = el.getAttribute('data-context');
            data[key] = el.textContent || el.value;
        });
        
        const sceneId = this.getSceneId();
        if (sceneId) {
            data.sceneId = sceneId;
        }
        
        const userId = this.getUserId();
        if (userId) {
            data.userId = userId;
        }
        
        return data;
    },
    
    getSceneId() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('sceneId') || urlParams.get('id') || null;
    },
    
    getUserId() {
        const meta = document.querySelector('meta[name="user-id"]');
        if (meta) {
            return meta.getAttribute('content');
        }
        
        if (window.currentUser && window.currentUser.id) {
            return window.currentUser.id;
        }
        
        return null;
    },
    
    getSelectedItems() {
        const selected = [];
        document.querySelectorAll('.selected, [data-selected="true"]').forEach(el => {
            selected.push({
                id: el.id || el.getAttribute('data-id'),
                type: el.getAttribute('data-type'),
                name: el.getAttribute('data-name') || el.textContent
            });
        });
        return selected;
    },
    
    getFormData() {
        const forms = document.querySelectorAll('form');
        const formData = {};
        forms.forEach(form => {
            const inputs = form.querySelectorAll('input, select, textarea');
            inputs.forEach(input => {
                if (input.name && input.type !== 'password') {
                    formData[input.name] = input.value;
                }
            });
        });
        return formData;
    },
    
    getLastUserAction() {
        return this.pageData.lastAction || null;
    },
    
    trackAction(event) {
        const target = event.target.closest('[data-context-track]');
        if (target) {
            this.pageData.lastAction = {
                type: target.getAttribute('data-context-track'),
                element: target.tagName,
                text: target.textContent,
                timestamp: Date.now()
            };
        }
    },
    
    trackFormChange(event) {
        const input = event.target;
        if (input.name && input.form) {
            this.pageData.lastFormChange = {
                field: input.name,
                value: input.value,
                formId: input.form.id,
                timestamp: Date.now()
            };
        }
    },
    
    async buildContext(options = {}) {
        const contextData = this.collect();
        
        const request = {
            sources: options.sources || ['page', 'user', 'scene'],
            pageContext: contextData,
            options: {
                maxTokens: options.maxTokens || 4096,
                includeSystemPrompt: options.includeSystemPrompt !== false,
                ttl: options.ttl || 3600000
            }
        };
        
        if (contextData.pageData.userId) {
            request.userId = contextData.pageData.userId;
        }
        
        if (contextData.pageData.sceneId) {
            request.sceneId = contextData.pageData.sceneId;
        }
        
        try {
            const response = await NexusApi.post('/api/v1/llm/context/build', request);
            if (response.status === 'success' && response.data) {
                return response.data;
            }
            return null;
        } catch (error) {
            console.error('Failed to build context:', error);
            return null;
        }
    },
    
    async getTokenCount(text) {
        try {
            const response = await NexusApi.post('/api/v1/llm/context/tokens/count', { text });
            if (response.status === 'success' && response.data) {
                return response.data.tokens;
            }
            return 0;
        } catch (error) {
            console.error('Failed to count tokens:', error);
            return Math.ceil(text.length / 4);
        }
    },
    
    getContext() {
        return this.context;
    },
    
    updateContext(updates) {
        this.context = { ...this.context, ...updates };
        return this.context;
    }
};

window.ContextCollector = ContextCollector;
