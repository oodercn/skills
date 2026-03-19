const LLMManagement = {
    configs: [],
    providers: [],
    currentConfig: null,
    deleteConfigId: null,
    
    async init() {
        await this.loadProviders();
        await this.loadConfigs();
    },
    
    promisify(obj, method, url, data) {
        return new Promise((resolve, reject) => {
            if (data) {
                obj[method](url, data, (res) => resolve(res));
            } else {
                obj[method](url, (res) => resolve(res));
            }
        });
    },
    
    async loadProviders() {
        try {
            const response = await this.promisify(NexusAPI, 'get', '/api/llm/providers');
            if (response.status === 'success' && response.data) {
                this.providers = response.data.map(p => ({
                    type: p.id,
                    name: p.name,
                    models: p.models || [],
                    supportsStreaming: p.supportsStreaming,
                    supportsFunctionCalling: p.supportsFunctionCalling
                }));
                this.renderProviders();
                this.populateProviderSelects();
            }
        } catch (error) {
            console.error('Failed to load providers:', error);
            this.providers = this.getDefaultProviders();
            this.renderProviders();
            this.populateProviderSelects();
        }
    },
    
    getDefaultProviders() {
        return [
            { type: 'deepseek', name: 'DeepSeek', models: ['deepseek-chat', 'deepseek-coder', 'deepseek-reasoner'], supportsStreaming: true },
            { type: 'baidu', name: '百度千帆', models: ['ernie-4.0-8k', 'ernie-4.0-turbo-8k', 'ernie-3.5-8k', 'ernie-3.5-turbo-8k', 'ernie-speed-8k', 'ernie-lite-8k', 'ernie-tiny-8k'], supportsStreaming: true },
            { type: 'openai', name: 'OpenAI', models: ['gpt-4o', 'gpt-4-turbo', 'gpt-3.5-turbo'], supportsStreaming: true },
            { type: 'qianwen', name: '通义千问', models: ['qwen-max', 'qwen-plus', 'qwen-turbo'], supportsStreaming: true },
            { type: 'ollama', name: 'Ollama', models: ['llama3', 'llama2', 'mistral', 'codellama'], supportsStreaming: true }
        ];
    },
    
    async loadConfigs() {
        try {
            const response = await this.promisify(NexusAPI, 'get', '/api/v1/llm/config');
            if (response.status === 'success' && response.data) {
                this.configs = response.data.configs || response.data || [];
                this.renderConfigs();
                this.updateStats();
            }
        } catch (error) {
            console.error('Failed to load configs:', error);
            this.configs = [];
            this.renderConfigs();
        }
    },
    
    renderProviders() {
        const grid = document.getElementById('provider-grid');
        if (!grid || !this.providers.length) return;
        
        grid.innerHTML = '';
        
        this.providers.forEach(provider => {
            const card = document.createElement('div');
            card.className = 'provider-card';
            card.onclick = () => this.showProviderDetail(provider.type);
            
            const iconClass = `provider-card__icon provider-card__icon--${provider.type}`;
            const iconMap = {
                'openai': 'ri-openai-fill',
                'qianwen': 'ri-baidu-fill',
                'deepseek': 'ri-brain-line',
                'baidu': 'ri-baidu-fill',
                'ollama': 'ri-robot-line',
                'volcengine': 'ri-fire-line',
                'mock': 'ri-test-tube-line'
            };
            
            card.innerHTML = `
                <div class="${iconClass}">
                    <i class="${iconMap[provider.type] || 'ri-robot-line'}"></i>
                </div>
                <div class="provider-card__name">${provider.name || this.getProviderDisplayName(provider.type)}</div>
                <div class="provider-card__models">${provider.models ? provider.models.length : 0} 个模型</div>
            `;
            
            grid.appendChild(card);
        });
    },
    
    showProviderDetail(providerType) {
        const provider = this.providers.find(p => p.type === providerType);
        if (!provider) return;
        
        const modalTitle = document.getElementById('provider-modal-title');
        const modalBody = document.getElementById('provider-modal-body');
        
        if (modalTitle) modalTitle.textContent = `${provider.name || this.getProviderDisplayName(providerType)} 详情`;
        
        const iconMap = {
            'openai': 'ri-openai-fill',
            'qianwen': 'ri-baidu-fill',
            'deepseek': 'ri-brain-line',
            'baidu': 'ri-baidu-fill',
            'ollama': 'ri-robot-line',
            'volcengine': 'ri-fire-line',
            'mock': 'ri-test-tube-line'
        };
        
        const baseUrlMap = {
            'openai': 'https://api.openai.com/v1',
            'qianwen': 'https://dashscope.aliyuncs.com/api/v1',
            'deepseek': 'https://api.deepseek.com',
            'baidu': 'https://qianfan.baidubce.com/v2',
            'ollama': 'http://localhost:11434/v1'
        };
        
        const relatedConfigs = this.configs.filter(c => c.providerType === providerType);
        
        if (modalBody) {
            modalBody.innerHTML = `
                <div style="display: flex; align-items: center; gap: 16px; margin-bottom: 24px;">
                    <div class="provider-card__icon provider-card__icon--${providerType}" style="width: 64px; height: 64px; font-size: 32px;">
                        <i class="${iconMap[providerType] || 'ri-robot-line'}"></i>
                    </div>
                    <div>
                        <h4 style="margin: 0 0 4px 0; font-size: 20px;">${provider.name || this.getProviderDisplayName(providerType)}</h4>
                        <p style="margin: 0; color: var(--nx-text-secondary);">Provider ID: ${providerType}</p>
                    </div>
                </div>
                
                <div class="nx-form-divider"><span>基本信息</span></div>
                
                <div class="nx-form-row">
                    <div class="nx-form-group">
                        <label class="nx-label">支持流式输出</label>
                        <div style="padding: var(--nx-space-2) 0;">
                            ${provider.supportsStreaming ? 
                                '<span style="color: var(--ns-success);"><i class="ri-check-line"></i> 支持</span>' : 
                                '<span style="color: var(--ns-danger);"><i class="ri-close-line"></i> 不支持</span>'}
                        </div>
                    </div>
                    <div class="nx-form-group">
                        <label class="nx-label">支持函数调用</label>
                        <div style="padding: var(--nx-space-2) 0;">
                            ${provider.supportsFunctionCalling ? 
                                '<span style="color: var(--ns-success);"><i class="ri-check-line"></i> 支持</span>' : 
                                '<span style="color: var(--ns-danger);"><i class="ri-close-line"></i> 不支持</span>'}
                        </div>
                    </div>
                </div>
                
                <div class="nx-form-group">
                    <label class="nx-label">默认API地址</label>
                    <div style="padding: var(--nx-space-2) var(--nx-space-3); background: var(--nx-bg-elevated); border-radius: var(--nx-radius); font-family: monospace; font-size: var(--nx-text-sm);">
                        ${baseUrlMap[providerType] || '自定义'}
                    </div>
                </div>
                
                <div class="nx-form-divider"><span>可用模型 (${provider.models ? provider.models.length : 0})</span></div>
                
                <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 8px;" id="model-grid-${providerType}">
                    ${(provider.models || []).map((model, index) => `
                        <div class="model-card-${providerType}" data-model="${model}" 
                             style="padding: var(--nx-space-2) var(--nx-space-3); background: var(--nx-bg-elevated); border-radius: var(--nx-radius); font-size: var(--nx-text-sm); border: 2px solid var(--nx-border); cursor: pointer; transition: all 0.2s;" 
                             onmouseover="this.style.borderColor='var(--ns-primary)'" 
                             onmouseout="this.style.borderColor='var(--nx-border)'"
                             onclick="LLMManagement.selectModel('${providerType}', '${model}')">
                            <i class="ri-cpu-line" style="color: var(--ns-primary); margin-right: 4px;"></i>
                            ${model}
                        </div>
                    `).join('')}
                </div>
                
                <div id="model-detail-container-${providerType}" style="margin-top: 16px;"></div>
                
                <div class="nx-form-divider"><span>相关配置 (${relatedConfigs.length})</span></div>
                
                ${relatedConfigs.length > 0 ? `
                    <table class="nx-table" style="font-size: var(--nx-text-sm);">
                        <thead>
                            <tr>
                                <th>配置名称</th>
                                <th>级别</th>
                                <th>模型</th>
                                <th>状态</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${relatedConfigs.map(config => `
                                <tr>
                                    <td>${config.name || '未命名'}</td>
                                    <td>${this.getLevelDisplayName(config.level)}</td>
                                    <td>${config.model || '-'}</td>
                                    <td>
                                        <span class="config-status config-status--${config.enabled ? 'active' : 'inactive'}">
                                            ${config.enabled ? '启用' : '禁用'}
                                        </span>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                ` : '<p style="color: var(--nx-text-tertiary); text-align: center; padding: var(--nx-space-4);">暂无相关配置</p>'}
            `;
        }
        
        document.getElementById('provider-modal').classList.add('nx-modal--open');
        
        if (provider.models && provider.models.length > 0) {
            setTimeout(() => this.selectModel(providerType, provider.models[0]), 100);
        }
    },
    
    selectModel(providerType, modelId) {
        const cards = document.querySelectorAll(`.model-card-${providerType}`);
        cards.forEach(card => {
            if (card.dataset.model === modelId) {
                card.style.borderColor = 'var(--ns-primary)';
                card.style.background = 'rgba(59, 130, 246, 0.05)';
            } else {
                card.style.borderColor = 'var(--nx-border)';
                card.style.background = 'var(--nx-bg-elevated)';
            }
        });
        
        this.showModelDetailInline(providerType, modelId);
    },
    
    async showModelDetailInline(providerType, modelId) {
        const container = document.getElementById(`model-detail-container-${providerType}`);
        if (!container) return;
        
        container.innerHTML = '<div style="text-align: center; padding: var(--nx-space-4);"><i class="ri-loader-4-line ri-spin"></i> 加载模型详情...</div>';
        
        try {
            const response = await this.promisify(NexusAPI, 'get', `/api/llm/model/${providerType}/${modelId}`);
            
            if (response.status !== 'success' || !response.data) {
                container.innerHTML = '<p style="color: var(--ns-danger); text-align: center;">获取模型详情失败</p>';
                return;
            }
            
            const model = response.data;
            
            container.innerHTML = `
                <div style="background: var(--nx-bg-elevated); border-radius: var(--nx-radius); border: 1px solid var(--nx-border); overflow: hidden;">
                    <div style="padding: var(--nx-space-3) var(--nx-space-4); background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(139, 92, 246, 0.1)); border-bottom: 1px solid var(--nx-border);">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <div>
                                <h4 style="margin: 0; font-size: var(--nx-text-lg);">${model.displayName || modelId}</h4>
                                <p style="margin: 4px 0 0 0; font-size: var(--nx-text-sm); color: var(--nx-text-secondary);">${model.description || ''}</p>
                            </div>
                            <span style="padding: var(--nx-space-1) var(--nx-space-3); background: var(--ns-primary); color: white; border-radius: var(--nx-radius-full); font-size: var(--nx-text-xs);">
                                ${providerType.toUpperCase()}
                            </span>
                        </div>
                    </div>
                    
                    <div style="padding: var(--nx-space-4);">
                        <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 16px;">
                            <div style="text-align: center; padding: var(--nx-space-3); background: var(--nx-bg-card); border-radius: var(--nx-radius);">
                                <div style="font-size: var(--nx-text-2xl); font-weight: 600; color: var(--ns-primary);">
                                    ${model.contextWindow ? (model.contextWindow / 1000).toFixed(0) + 'K' : '-'}
                                </div>
                                <div style="font-size: var(--nx-text-xs); color: var(--nx-text-secondary);">上下文窗口</div>
                            </div>
                            <div style="text-align: center; padding: var(--nx-space-3); background: var(--nx-bg-card); border-radius: var(--nx-radius);">
                                <div style="font-size: var(--nx-text-2xl); font-weight: 600; color: var(--ns-primary);">
                                    ${model.maxOutputTokens ? (model.maxOutputTokens / 1000).toFixed(0) + 'K' : '-'}
                                </div>
                                <div style="font-size: var(--nx-text-xs); color: var(--nx-text-secondary);">最大输出</div>
                            </div>
                            <div style="text-align: center; padding: var(--nx-space-3); background: var(--nx-bg-card); border-radius: var(--nx-radius);">
                                <div style="font-size: var(--nx-text-2xl); font-weight: 600; color: var(--ns-success);">
                                    $${model.inputPrice ? model.inputPrice.toFixed(4) : '0'}
                                </div>
                                <div style="font-size: var(--nx-text-xs); color: var(--nx-text-secondary);">输入价格/1K</div>
                            </div>
                            <div style="text-align: center; padding: var(--nx-space-3); background: var(--nx-bg-card); border-radius: var(--nx-radius);">
                                <div style="font-size: var(--nx-text-2xl); font-weight: 600; color: var(--ns-warning);">
                                    $${model.outputPrice ? model.outputPrice.toFixed(4) : '0'}
                                </div>
                                <div style="font-size: var(--nx-text-xs); color: var(--nx-text-secondary);">输出价格/1K</div>
                            </div>
                        </div>
                        
                        <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; margin-bottom: 16px;">
                            <div style="display: flex; align-items: center; justify-content: center; gap: 6px; padding: var(--nx-space-2); background: ${model.supportsStreaming ? 'rgba(34, 197, 94, 0.1)' : 'rgba(239, 68, 68, 0.1)'}; border-radius: var(--nx-radius);">
                                <i class="${model.supportsStreaming ? 'ri-check-circle-fill' : 'ri-close-circle-fill'}" style="color: ${model.supportsStreaming ? 'var(--ns-success)' : 'var(--ns-danger)'}"></i>
                                <span style="font-size: var(--nx-text-sm);">流式输出</span>
                            </div>
                            <div style="display: flex; align-items: center; justify-content: center; gap: 6px; padding: var(--nx-space-2); background: ${model.supportsFunctionCall ? 'rgba(34, 197, 94, 0.1)' : 'rgba(239, 68, 68, 0.1)'}; border-radius: var(--nx-radius);">
                                <i class="${model.supportsFunctionCall ? 'ri-check-circle-fill' : 'ri-close-circle-fill'}" style="color: ${model.supportsFunctionCall ? 'var(--ns-success)' : 'var(--ns-danger)'}"></i>
                                <span style="font-size: var(--nx-text-sm);">函数调用</span>
                            </div>
                            <div style="display: flex; align-items: center; justify-content: center; gap: 6px; padding: var(--nx-space-2); background: ${model.supportsVision ? 'rgba(34, 197, 94, 0.1)' : 'rgba(239, 68, 68, 0.1)'}; border-radius: var(--nx-radius);">
                                <i class="${model.supportsVision ? 'ri-check-circle-fill' : 'ri-close-circle-fill'}" style="color: ${model.supportsVision ? 'var(--ns-success)' : 'var(--ns-danger)'}"></i>
                                <span style="font-size: var(--nx-text-sm);">视觉理解</span>
                            </div>
                            <div style="display: flex; align-items: center; justify-content: center; gap: 6px; padding: var(--nx-space-2); background: ${model.supportsRAG ? 'rgba(34, 197, 94, 0.1)' : 'rgba(239, 68, 68, 0.1)'}; border-radius: var(--nx-radius);">
                                <i class="${model.supportsRAG ? 'ri-check-circle-fill' : 'ri-close-circle-fill'}" style="color: ${model.supportsRAG ? 'var(--ns-success)' : 'var(--ns-danger)'}"></i>
                                <span style="font-size: var(--nx-text-sm);">RAG增强</span>
                            </div>
                        </div>
                        
                        <div>
                            <div style="font-size: var(--nx-text-sm); color: var(--nx-text-secondary); margin-bottom: 8px;">支持能力</div>
                            <div style="display: flex; flex-wrap: wrap; gap: 6px;">
                                ${(model.capabilities || []).map(cap => `
                                    <span style="padding: 4px 10px; background: rgba(59, 130, 246, 0.1); color: var(--ns-primary); border-radius: var(--nx-radius-full); font-size: var(--nx-text-xs);">
                                        ${cap}
                                    </span>
                                `).join('')}
                            </div>
                        </div>
                    </div>
                </div>
            `;
        } catch (error) {
            console.error('Failed to load model detail:', error);
            container.innerHTML = '<p style="color: var(--ns-danger); text-align: center;">获取模型详情失败: ' + error.message + '</p>';
        }
    },
    
    closeProviderModal() {
        document.getElementById('provider-modal').classList.remove('nx-modal--open');
    },
    
    populateProviderSelects() {
        const filterSelect = document.getElementById('filter-provider');
        const formSelect = document.getElementById('config-provider');
        
        if (filterSelect) {
            filterSelect.innerHTML = '<option value="">全部Provider</option>';
            this.providers.forEach(p => {
                filterSelect.innerHTML += `<option value="${p.type}">${p.name || this.getProviderDisplayName(p.type)}</option>`;
            });
        }
        
        if (formSelect) {
            formSelect.innerHTML = '<option value="">请选择Provider</option>';
            this.providers.forEach(p => {
                formSelect.innerHTML += `<option value="${p.type}">${p.name || this.getProviderDisplayName(p.type)}</option>`;
            });
        }
    },
    
    getProviderDisplayName(type) {
        const names = {
            'openai': 'OpenAI',
            'qianwen': '通义千问',
            'deepseek': 'DeepSeek',
            'baidu': '百度千帆',
            'ollama': 'Ollama',
            'volcengine': '火山引擎',
            'mock': 'Mock'
        };
        return names[type] || type;
    },
    
    renderConfigs() {
        const tbody = document.getElementById('config-table-body');
        if (!tbody) return;
        
        if (!this.configs.length) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="nx-text-center nx-text-secondary nx-py-8">
                        <i class="ri-inbox-line" style="font-size: 24px;"></i>
                        <p>暂无配置数据</p>
                    </td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = '';
        
        this.configs.forEach(config => {
            const tr = document.createElement('tr');
            const isEnabled = config.enabled === true;
            tr.innerHTML = `
                <td>${config.name || '未命名配置'}</td>
                <td>
                    <span class="config-level config-level--${config.level ? config.level.toLowerCase() : 'enterprise'}">
                        ${this.getLevelDisplayName(config.level)}
                    </span>
                </td>
                <td>${this.getProviderDisplayName(config.providerType || config.provider)}</td>
                <td>${config.model || '-'}</td>
                <td>
                    <span class="config-status config-status--${isEnabled ? 'active' : 'inactive'}">
                        <i class="ri-checkbox-blank-circle-fill" style="font-size: 8px;"></i>
                        ${isEnabled ? '启用' : '禁用'}
                    </span>
                </td>
                <td>${this.formatTime(config.updatedAt || config.updateTime)}</td>
                <td>
                    <div class="action-btns">
                        <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="LLMManagement.editConfig('${config.id}')" title="编辑">
                            <i class="ri-edit-line"></i>
                        </button>
                        <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="LLMManagement.openDeleteModal('${config.id}', '${config.name || '未命名配置'}')" title="删除">
                            <i class="ri-delete-bin-line"></i>
                        </button>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });
    },
    
    getLevelDisplayName(level) {
        const names = {
            'ENTERPRISE': '企业级',
            'DEPARTMENT': '部门级',
            'PERSONAL': '个人级',
            'SCENE': '场景级',
            'SCENE_GROUP': '场景组级'
        };
        return names[level] || level;
    },
    
    formatTime(timestamp) {
        if (!timestamp) return '-';
        const date = new Date(timestamp);
        return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    },
    
    updateStats() {
        const total = this.configs.length;
        const enterprise = this.configs.filter(c => c.level === 'ENTERPRISE').length;
        const personal = this.configs.filter(c => c.level === 'PERSONAL').length;
        const providers = this.providers.length;
        
        document.getElementById('stat-total').textContent = total;
        document.getElementById('stat-enterprise').textContent = enterprise;
        document.getElementById('stat-personal').textContent = personal;
        document.getElementById('stat-providers').textContent = providers;
    },
    
    filterConfigs() {
        const levelFilter = document.getElementById('filter-level').value;
        const providerFilter = document.getElementById('filter-provider').value;
        
        let filtered = this.configs;
        
        if (levelFilter) {
            filtered = filtered.filter(c => c.level === levelFilter);
        }
        
        if (providerFilter) {
            filtered = filtered.filter(c => (c.providerType || c.provider) === providerFilter);
        }
        
        const tbody = document.getElementById('config-table-body');
        if (!tbody) return;
        
        if (!filtered.length) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="nx-text-center nx-text-secondary nx-py-8">
                        没有匹配的配置
                    </td>
                </tr>
            `;
            return;
        }
        
        const originalConfigs = this.configs;
        this.configs = filtered;
        this.renderConfigs();
        this.configs = originalConfigs;
    },
    
    openCreateModal() {
        this.currentConfig = null;
        
        const modalTitle = document.querySelector('#modal-title span');
        if (modalTitle) modalTitle.textContent = '新建配置';
        
        document.getElementById('config-form').reset();
        document.getElementById('config-id').value = '';
        document.getElementById('owner-id-group').style.display = 'none';
        document.getElementById('config-temperature').value = '0.7';
        document.getElementById('config-max-tokens').value = '4096';
        
        document.getElementById('config-modal').classList.add('nx-modal--open');
    },
    
    editConfig(id) {
        const config = this.configs.find(c => c.id === id);
        if (!config) {
            console.error('Config not found:', id);
            return;
        }
        
        console.log('Editing config:', config);
        
        this.currentConfig = config;
        
        const modalTitle = document.querySelector('#modal-title span');
        if (modalTitle) modalTitle.textContent = '编辑配置';
        
        document.getElementById('config-id').value = config.id || '';
        document.getElementById('config-name').value = config.name || '';
        document.getElementById('config-level').value = config.level || '';
        document.getElementById('config-owner-id').value = config.scopeId || '';
        document.getElementById('config-owner-id-value').value = config.scopeId || '';
        
        const providerSelect = document.getElementById('config-provider');
        providerSelect.value = config.providerType || config.provider || '';
        
        this.onProviderChange();
        
        setTimeout(() => {
            const modelSelect = document.getElementById('config-model');
            modelSelect.value = config.model || '';
            console.log('Model set to:', config.model);
        }, 100);
        
        const apiKey = config.providerConfig?.apiKey || '';
        const apiKeyInput = document.getElementById('config-api-key');
        if (apiKey) {
            apiKeyInput.value = apiKey;
            apiKeyInput.dataset.originalKey = apiKey;
        } else {
            apiKeyInput.value = '';
            apiKeyInput.dataset.originalKey = '';
        }
        
        document.getElementById('config-base-url').value = config.providerConfig?.baseUrl || '';
        document.getElementById('config-temperature').value = config.options?.temperature || 0.7;
        document.getElementById('config-max-tokens').value = config.options?.max_tokens || 4096;
        
        this.onLevelChange();
        
        document.getElementById('config-modal').classList.add('nx-modal--open');
    },
    
    closeModal() {
        document.getElementById('config-modal').classList.remove('nx-modal--open');
    },
    
    toggleApiKeyVisibility() {
        const input = document.getElementById('config-api-key');
        const icon = document.getElementById('api-key-toggle-icon');
        
        if (input.type === 'password') {
            input.type = 'text';
            icon.className = 'ri-eye-off-line';
        } else {
            input.type = 'password';
            icon.className = 'ri-eye-line';
        }
    },
    
    onLevelChange() {
        const level = document.getElementById('config-level').value;
        const ownerGroup = document.getElementById('owner-id-group');
        
        if (!ownerGroup) return;
        
        if (level === 'PERSONAL' || level === 'SCENE' || level === 'DEPARTMENT') {
            ownerGroup.style.display = 'block';
        } else {
            ownerGroup.style.display = 'none';
        }
    },
    
    onProviderChange() {
        const providerType = document.getElementById('config-provider').value;
        const modelSelect = document.getElementById('config-model');
        
        modelSelect.innerHTML = '<option value="">请选择模型</option>';
        
        if (!providerType) return;
        
        const provider = this.providers.find(p => p.type === providerType);
        if (provider && provider.models) {
            provider.models.forEach(model => {
                modelSelect.innerHTML += `<option value="${model}">${model}</option>`;
            });
        }
    },
    
    async saveConfig() {
        const id = document.getElementById('config-id').value;
        const ownerIdValue = document.getElementById('config-owner-id-value')?.value;
        const ownerIdDisplay = document.getElementById('config-owner-id')?.value;
        
        const config = {
            name: document.getElementById('config-name').value,
            level: document.getElementById('config-level').value,
            scopeId: ownerIdValue || ownerIdDisplay || 'default',
            providerType: document.getElementById('config-provider').value,
            model: document.getElementById('config-model').value,
            providerConfig: {
                apiKey: document.getElementById('config-api-key').value,
                baseUrl: document.getElementById('config-base-url').value
            },
            options: {
                temperature: parseFloat(document.getElementById('config-temperature').value) || 0.7,
                max_tokens: parseInt(document.getElementById('config-max-tokens').value) || 4096
            },
            enabled: true
        };
        
        if (!config.name || !config.level || !config.providerType || !config.model) {
            alert('请填写必填字段');
            return;
        }
        
        try {
            let response;
            if (id) {
                response = await this.promisify(NexusAPI, 'put', `/api/v1/llm/config/${id}`, config);
            } else {
                response = await this.promisify(NexusAPI, 'post', '/api/v1/llm/config', config);
            }
            
            if (response.status === 'success') {
                this.closeModal();
                await this.loadConfigs();
            } else {
                alert('保存失败: ' + (response.message || '未知错误'));
            }
        } catch (error) {
            console.error('Failed to save config:', error);
            alert('保存失败: ' + error.message);
        }
    },
    
    openDeleteModal(id, name) {
        this.deleteConfigId = id;
        document.getElementById('delete-config-name').textContent = name;
        document.getElementById('delete-modal').classList.add('nx-modal--open');
    },
    
    closeDeleteModal() {
        this.deleteConfigId = null;
        document.getElementById('delete-modal').classList.remove('nx-modal--open');
    },
    
    async confirmDelete() {
        if (!this.deleteConfigId) return;
        
        try {
            const response = await this.promisify(NexusAPI, 'delete', `/api/v1/llm/config/${this.deleteConfigId}`);
            if (response.status === 'success') {
                this.closeDeleteModal();
                await this.loadConfigs();
            } else {
                alert('删除失败: ' + (response.message || '未知错误'));
            }
        } catch (error) {
            console.error('Failed to delete config:', error);
            alert('删除失败: ' + error.message);
        }
    },
    
    showUserPicker() {
        document.getElementById('user-picker-modal').classList.add('nx-modal--open');
        this.loadUsers();
    },
    
    closeUserPicker() {
        document.getElementById('user-picker-modal').classList.remove('nx-modal--open');
    },
    
    async loadUsers(keyword = '') {
        const container = document.getElementById('user-list-container');
        container.innerHTML = '<div style="text-align: center; padding: 20px; color: var(--nx-text-secondary);"><i class="ri-loader-4-line ri-spin"></i> 加载中...</div>';
        
        try {
            let response;
            if (keyword) {
                response = await this.promisify(NexusAPI, 'post', '/api/admin/users/search', { keyword: keyword, pageNum: 1, pageSize: 50 });
            } else {
                response = await this.promisify(NexusAPI, 'post', '/api/admin/users', { pageNum: 1, pageSize: 50 });
            }
            
            const users = response.data?.list || response.data?.users || response.data || [];
            
            if (!users || users.length === 0) {
                container.innerHTML = `
                    <div style="text-align: center; padding: 20px; color: var(--nx-text-secondary);">
                        <i class="ri-user-unfollow-line" style="font-size: 32px; margin-bottom: 8px; display: block;"></i>
                        暂无人员数据
                    </div>
                `;
                return;
            }
            
            container.innerHTML = users.map(user => `
                <div class="user-item" onclick="LLMManagement.selectUser('${user.id}', '${user.name || user.username || user.id}')"
                     style="display: flex; align-items: center; gap: 12px; padding: 12px; border-radius: var(--nx-radius); cursor: pointer; transition: all 0.2s; border: 1px solid var(--nx-border); margin-bottom: 8px;"
                     onmouseover="this.style.background='var(--nx-bg-hover)'; this.style.borderColor='var(--ns-primary)';"
                     onmouseout="this.style.background='var(--nx-bg-card)'; this.style.borderColor='var(--nx-border)';">
                    <div style="width: 40px; height: 40px; border-radius: 50%; background: linear-gradient(135deg, var(--ns-primary), #8b5cf6); display: flex; align-items: center; justify-content: center; color: white; font-weight: 600;">
                        ${(user.name || user.username || user.id || '?').charAt(0).toUpperCase()}
                    </div>
                    <div style="flex: 1;">
                        <div style="font-weight: 500; color: var(--nx-text-primary);">${user.name || user.username || user.id}</div>
                        <div style="font-size: var(--nx-text-xs); color: var(--nx-text-secondary);">${user.email || user.id}</div>
                    </div>
                    <i class="ri-arrow-right-s-line" style="color: var(--nx-text-tertiary);"></i>
                </div>
            `).join('');
        } catch (error) {
            console.error('Failed to load users:', error);
            container.innerHTML = `
                <div style="text-align: center; padding: 20px; color: var(--ns-danger);">
                    <i class="ri-error-warning-line" style="font-size: 32px; margin-bottom: 8px; display: block;"></i>
                    加载失败: ${error.message}
                </div>
            `;
        }
    },
    
    searchUsers(keyword) {
        if (this.searchTimeout) {
            clearTimeout(this.searchTimeout);
        }
        this.searchTimeout = setTimeout(() => {
            this.loadUsers(keyword);
        }, 300);
    },
    
    selectUser(userId, userName) {
        document.getElementById('config-owner-id').value = userName;
        document.getElementById('config-owner-id-value').value = userId;
        this.closeUserPicker();
    }
};

window.LLMManagement = LLMManagement;
