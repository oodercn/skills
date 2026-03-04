const LLMManagement = {
    configs: [],
    providers: [],
    currentConfig: null,
    deleteConfigId: null,
    
    async init() {
        await this.loadProviders();
        await this.loadConfigs();
    },
    
    async loadProviders() {
        try {
            const response = await NexusApi.post('/api/llm/providers');
            if (response.status === 'success' && response.data) {
                this.providers = response.data;
                this.renderProviders();
                this.populateProviderSelects();
            }
        } catch (error) {
            console.error('Failed to load providers:', error);
        }
    },
    
    async loadConfigs() {
        try {
            const response = await NexusApi.get('/api/v1/llm/config');
            if (response.status === 'success' && response.data) {
                this.configs = response.data.configs || [];
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
            
            const iconClass = `provider-card__icon provider-card__icon--${provider.type}`;
            const iconMap = {
                'openai': 'ri-openai-fill',
                'qianwen': 'ri-baidu-fill',
                'deepseek': 'ri-brain-line',
                'ollama': 'ri-robot-line',
                'volcengine': 'ri-fire-line',
                'mock': 'ri-test-tube-line'
            };
            
            card.innerHTML = `
                <div class="${iconClass}">
                    <i class="${iconMap[provider.type] || 'ri-robot-line'}"></i>
                </div>
                <div class="provider-card__name">${this.getProviderDisplayName(provider.type)}</div>
                <div class="provider-card__models">${provider.models ? provider.models.length : 0} 个模型</div>
            `;
            
            grid.appendChild(card);
        });
    },
    
    populateProviderSelects() {
        const filterSelect = document.getElementById('filter-provider');
        const formSelect = document.getElementById('config-provider');
        
        if (filterSelect) {
            filterSelect.innerHTML = '<option value="">全部Provider</option>';
            this.providers.forEach(p => {
                filterSelect.innerHTML += `<option value="${p.type}">${this.getProviderDisplayName(p.type)}</option>`;
            });
        }
        
        if (formSelect) {
            formSelect.innerHTML = '<option value="">请选择Provider</option>';
            this.providers.forEach(p => {
                formSelect.innerHTML += `<option value="${p.type}">${this.getProviderDisplayName(p.type)}</option>`;
            });
        }
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
            tr.innerHTML = `
                <td>${config.name || '-'}</td>
                <td>
                    <span class="config-level config-level--${config.level ? config.level.toLowerCase() : 'enterprise'}">
                        ${this.getLevelDisplayName(config.level)}
                    </span>
                </td>
                <td>${this.getProviderDisplayName(config.providerType || config.provider)}</td>
                <td>${config.model || '-'}</td>
                <td>
                    <span class="config-status config-status--${config.status && config.status.toLowerCase() === 'active' ? 'active' : 'inactive'}">
                        <i class="ri-checkbox-blank-circle-fill" style="font-size: 8px;"></i>
                        ${config.status === 'ACTIVE' ? '启用' : '禁用'}
                    </span>
                </td>
                <td>${this.formatTime(config.updateTime)}</td>
                <td>
                    <div class="action-btns">
                        <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="LLMManagement.editConfig('${config.id}')" title="编辑">
                            <i class="ri-edit-line"></i>
                        </button>
                        <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="LLMManagement.openDeleteModal('${config.id}', '${config.name}')" title="删除">
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
        document.getElementById('modal-title').textContent = '新建配置';
        document.getElementById('config-form').reset();
        document.getElementById('config-id').value = '';
        document.getElementById('owner-id-group').style.display = 'none';
        
        document.getElementById('config-modal').classList.add('nx-modal--open');
    },
    
    editConfig(id) {
        const config = this.configs.find(c => c.id === id);
        if (!config) return;
        
        this.currentConfig = config;
        document.getElementById('modal-title').textContent = '编辑配置';
        document.getElementById('config-id').value = config.id;
        document.getElementById('config-name').value = config.name || '';
        document.getElementById('config-level').value = config.level || '';
        document.getElementById('config-owner-id').value = config.ownerId || '';
        document.getElementById('config-provider').value = config.providerType || config.provider || '';
        document.getElementById('config-model').value = config.model || '';
        document.getElementById('config-api-key').value = '';
        document.getElementById('config-base-url').value = config.baseUrl || '';
        document.getElementById('config-temperature').value = config.temperature || 0.7;
        document.getElementById('config-max-tokens').value = config.maxTokens || 4096;
        
        this.onLevelChange();
        this.onProviderChange();
        
        document.getElementById('config-modal').classList.add('nx-modal--open');
    },
    
    closeModal() {
        document.getElementById('config-modal').classList.remove('nx-modal--open');
    },
    
    onLevelChange() {
        const level = document.getElementById('config-level').value;
        const ownerGroup = document.getElementById('owner-id-group');
        
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
        const config = {
            name: document.getElementById('config-name').value,
            level: document.getElementById('config-level').value,
            ownerId: document.getElementById('config-owner-id').value || 'default',
            providerType: document.getElementById('config-provider').value,
            model: document.getElementById('config-model').value,
            apiKey: document.getElementById('config-api-key').value,
            baseUrl: document.getElementById('config-base-url').value,
            temperature: parseFloat(document.getElementById('config-temperature').value),
            maxTokens: parseInt(document.getElementById('config-max-tokens').value)
        };
        
        if (!config.name || !config.level || !config.providerType || !config.model) {
            alert('请填写必填字段');
            return;
        }
        
        try {
            let response;
            if (id) {
                response = await NexusApi.put(`/api/v1/llm/config/${id}`, config);
            } else {
                response = await NexusApi.post('/api/v1/llm/config', config);
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
            const response = await NexusApi.delete(`/api/v1/llm/config/${this.deleteConfigId}`);
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
    }
};

window.LLMManagement = LLMManagement;
