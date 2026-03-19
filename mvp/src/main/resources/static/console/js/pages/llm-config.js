(function() {
    'use strict';

    var LlmConfig = {
        configs: [],
        providers: [],
        currentConfig: null,
        deleteConfigId: null,
        currentProvider: null,
        currentModel: null,
        modelsByProvider: {},
        
        init: function() {
            console.log('[LlmConfig] 初始化LLM配置页面');
            LlmConfig.loadProviders();
            LlmConfig.loadConfigs();
        },
        
        loadProviders: function() {
            fetch('/api/v1/llm/models', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result && result.requestStatus === 200 && result.data) {
                    var data = result.data;
                    LlmConfig.currentProvider = data.currentProvider;
                    LlmConfig.currentModel = data.currentModel;
                    LlmConfig.modelsByProvider = data.modelsByProvider || {};
                    
                    var providers = data.providers || [];
                    LlmConfig.providers = providers.map(function(p) {
                        return {
                            type: p,
                            name: LlmConfig.getProviderDisplayName(p),
                            models: LlmConfig.modelsByProvider[p] || []
                        };
                    });
                    
                    LlmConfig.renderProviders();
                    LlmConfig.populateProviderSelects();
                    LlmConfig.updateCurrentStatus();
                }
            })
            .catch(function(error) {
                console.error('[LlmConfig] 加载Provider失败:', error);
                LlmConfig.providers = LlmConfig.getDefaultProviders();
                LlmConfig.renderProviders();
                LlmConfig.populateProviderSelects();
            });
        },
        
        loadConfigs: function() {
            fetch('/api/v1/llm-config', {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result && result.status === 'success' && result.data) {
                    LlmConfig.configs = result.data.configs || result.data || [];
                    LlmConfig.renderConfigs();
                    LlmConfig.updateStats();
                } else {
                    LlmConfig.configs = [];
                    LlmConfig.renderConfigs();
                }
            })
            .catch(function(error) {
                console.error('[LlmConfig] 加载配置失败:', error);
                LlmConfig.configs = [];
                LlmConfig.renderConfigs();
            });
        },
        
        getDefaultProviders: function() {
            return [
                { type: 'aliyun-bailian', name: '阿里百联', models: ['qwen-turbo', 'qwen-plus', 'qwen-max', 'qwen-long'] },
                { type: 'deepseek', name: 'DeepSeek', models: ['deepseek-chat', 'deepseek-coder', 'deepseek-reasoner'] },
                { type: 'baidu', name: '百度千帆', models: ['ernie-4.0-8k', 'ernie-4.0-turbo-8k', 'ernie-3.5-8k'] },
                { type: 'qianwen', name: '通义千问', models: ['qwen-max', 'qwen-plus', 'qwen-turbo'] }
            ];
        },
        
        renderProviders: function() {
            var grid = document.getElementById('provider-grid');
            if (!grid) return;
            
            grid.innerHTML = '';
            
            LlmConfig.providers.forEach(function(provider) {
                var card = document.createElement('div');
                card.className = 'provider-card';
                if (provider.type === LlmConfig.currentProvider) {
                    card.className += ' active';
                }
                card.onclick = function() { LlmConfig.showProviderDetail(provider.type); };
                
                var iconClass = 'provider-card__icon provider-card__icon--' + provider.type;
                var iconMap = {
                    'aliyun-bailian': 'ri-cloud-line',
                    'qianwen': 'ri-robot-line',
                    'deepseek': 'ri-brain-line',
                    'baidu': 'ri-baidu-fill',
                    'openai': 'ri-openai-fill',
                    'ollama': 'ri-robot-line',
                    'mock': 'ri-test-tube-line'
                };
                
                var isActive = provider.type === LlmConfig.currentProvider;
                
                card.innerHTML = '<div class="' + iconClass + '">' +
                    '<i class="' + (iconMap[provider.type] || 'ri-robot-line') + '"></i>' +
                '</div>' +
                '<div class="provider-card__name">' + provider.name + '</div>' +
                '<div class="provider-card__models">' + (provider.models ? provider.models.length : 0) + ' 个模型</div>' +
                (isActive ? '<span class="provider-badge active">当前</span>' : '');
                
                grid.appendChild(card);
            });
        },
        
        showProviderDetail: function(providerType) {
            var provider = LlmConfig.providers.find(function(p) { return p.type === providerType; });
            if (!provider) return;
            
            var modalTitle = document.getElementById('provider-modal-title');
            var modalBody = document.getElementById('provider-modal-body');
            
            if (modalTitle) modalTitle.textContent = provider.name + ' 详情';
            
            var iconMap = {
                'aliyun-bailian': 'ri-cloud-line',
                'qianwen': 'ri-robot-line',
                'deepseek': 'ri-brain-line',
                'baidu': 'ri-baidu-fill',
                'openai': 'ri-openai-fill',
                'ollama': 'ri-robot-line'
            };
            
            var baseUrlMap = {
                'aliyun-bailian': 'https://dashscope.aliyuncs.com/compatible-mode/v1',
                'qianwen': 'https://dashscope.aliyuncs.com/api/v1',
                'deepseek': 'https://api.deepseek.com',
                'baidu': 'https://qianfan.baidubce.com/v2',
                'openai': 'https://api.openai.com/v1'
            };
            
            var relatedConfigs = LlmConfig.configs.filter(function(c) { 
                return c.providerType === providerType || c.provider === providerType; 
            });
            
            if (modalBody) {
                var html = '<div style="display: flex; align-items: center; gap: 16px; margin-bottom: 24px;">' +
                    '<div class="provider-card__icon provider-card__icon--' + providerType + '" style="width: 64px; height: 64px; font-size: 32px;">' +
                        '<i class="' + (iconMap[providerType] || 'ri-robot-line') + '"></i>' +
                    '</div>' +
                    '<div>' +
                        '<h4 style="margin: 0 0 4px 0; font-size: 20px;">' + provider.name + '</h4>' +
                        '<p style="margin: 0; color: var(--nx-text-secondary);">Provider ID: ' + providerType + '</p>' +
                    '</div>' +
                '</div>' +
                '<div class="nx-form-divider"><span>可用模型 (' + (provider.models ? provider.models.length : 0) + ')</span></div>' +
                '<div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 8px;">';
                
                (provider.models || []).forEach(function(model) {
                    var isCurrentModel = providerType === LlmConfig.currentProvider && model === LlmConfig.currentModel;
                    html += '<div class="model-tag' + (isCurrentModel ? ' current' : '') + '" ' +
                        'style="padding: var(--nx-space-2) var(--nx-space-3); background: var(--nx-bg-elevated); border-radius: var(--nx-radius); font-size: var(--nx-text-sm); border: 2px solid ' + (isCurrentModel ? 'var(--nx-primary)' : 'var(--nx-border)') + '; cursor: pointer;" ' +
                        'onclick="LlmConfig.selectProviderModel(\'' + providerType + '\', \'' + model + '\')">' +
                        '<i class="ri-cpu-line" style="color: var(--nx-primary); margin-right: 4px;"></i>' +
                        model +
                        (isCurrentModel ? ' <span style="color: var(--nx-primary);">(当前)</span>' : '') +
                    '</div>';
                });
                
                html += '</div>' +
                '<div class="nx-form-divider"><span>相关配置 (' + relatedConfigs.length + ')</span></div>';
                
                if (relatedConfigs.length > 0) {
                    html += '<table class="nx-table" style="font-size: var(--nx-text-sm);">' +
                        '<thead><tr><th>配置名称</th><th>级别</th><th>模型</th><th>状态</th></tr></thead>' +
                        '<tbody>';
                    
                    relatedConfigs.forEach(function(config) {
                        html += '<tr>' +
                            '<td>' + (config.name || '未命名') + '</td>' +
                            '<td>' + LlmConfig.getLevelDisplayName(config.level) + '</td>' +
                            '<td>' + (config.model || '-') + '</td>' +
                            '<td><span class="config-status config-status--' + (config.enabled ? 'active' : 'inactive') + '">' +
                                (config.enabled ? '启用' : '禁用') + '</span></td>' +
                        '</tr>';
                    });
                    
                    html += '</tbody></table>';
                } else {
                    html += '<p style="color: var(--nx-text-tertiary); text-align: center; padding: var(--nx-space-4);">暂无相关配置</p>';
                }
                
                modalBody.innerHTML = html;
            }
            
            document.getElementById('provider-modal').classList.add('nx-modal--open');
        },
        
        selectProviderModel: function(providerType, modelId) {
            LlmConfig.setConfig(providerType, modelId);
        },
        
        closeProviderModal: function() {
            document.getElementById('provider-modal').classList.remove('nx-modal--open');
        },
        
        populateProviderSelects: function() {
            var filterSelect = document.getElementById('filter-provider');
            var formSelect = document.getElementById('config-provider');
            
            if (filterSelect) {
                filterSelect.innerHTML = '<option value="">全部Provider</option>';
                LlmConfig.providers.forEach(function(p) {
                    filterSelect.innerHTML += '<option value="' + p.type + '">' + p.name + '</option>';
                });
            }
            
            if (formSelect) {
                formSelect.innerHTML = '<option value="">请选择Provider</option>';
                LlmConfig.providers.forEach(function(p) {
                    formSelect.innerHTML += '<option value="' + p.type + '">' + p.name + '</option>';
                });
            }
        },
        
        getProviderDisplayName: function(type) {
            var names = {
                'aliyun-bailian': '阿里百联',
                'qianwen': '通义千问',
                'deepseek': 'DeepSeek',
                'baidu': '百度千帆',
                'openai': 'OpenAI',
                'ollama': 'Ollama',
                'mock': 'Mock'
            };
            return names[type] || type;
        },
        
        renderConfigs: function() {
            var tbody = document.getElementById('config-table-body');
            if (!tbody) return;
            
            if (!LlmConfig.configs.length) {
                tbody.innerHTML = '<tr>' +
                    '<td colspan="7" class="nx-text-center nx-text-secondary nx-py-8">' +
                        '<i class="ri-inbox-line" style="font-size: 24px;"></i>' +
                        '<p>暂无配置数据</p>' +
                    '</td>' +
                '</tr>';
                return;
            }
            
            tbody.innerHTML = '';
            
            LlmConfig.configs.forEach(function(config) {
                var tr = document.createElement('tr');
                var isEnabled = config.enabled === true;
                tr.innerHTML = '<td>' + (config.name || '未命名配置') + '</td>' +
                '<td><span class="config-level config-level--' + (config.level ? config.level.toLowerCase() : 'enterprise') + '">' +
                    LlmConfig.getLevelDisplayName(config.level) +
                '</span></td>' +
                '<td>' + LlmConfig.getProviderDisplayName(config.providerType || config.provider) + '</td>' +
                '<td>' + (config.model || '-') + '</td>' +
                '<td><span class="config-status config-status--' + (isEnabled ? 'active' : 'inactive') + '">' +
                    '<i class="ri-checkbox-blank-circle-fill" style="font-size: 8px;"></i> ' +
                    (isEnabled ? '启用' : '禁用') +
                '</span></td>' +
                '<td>' + LlmConfig.formatTime(config.updatedAt || config.updateTime) + '</td>' +
                '<td>' +
                    '<div class="action-btns">' +
                        '<button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="LlmConfig.editConfig(\'' + config.id + '\')" title="编辑">' +
                            '<i class="ri-edit-line"></i>' +
                        '</button>' +
                        '<button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="LlmConfig.openDeleteModal(\'' + config.id + '\', \'' + (config.name || '未命名配置') + '\')" title="删除">' +
                            '<i class="ri-delete-bin-line"></i>' +
                        '</button>' +
                    '</div>' +
                '</td>';
                tbody.appendChild(tr);
            });
        },
        
        getLevelDisplayName: function(level) {
            var names = {
                'ENTERPRISE': '企业级',
                'DEPARTMENT': '部门级',
                'PERSONAL': '个人级',
                'SCENE': '场景级',
                'SCENE_GROUP': '场景组级'
            };
            return names[level] || level;
        },
        
        formatTime: function(timestamp) {
            if (!timestamp) return '-';
            var date = new Date(timestamp);
            return date.toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        },
        
        updateStats: function() {
            var total = LlmConfig.configs.length;
            var enterprise = LlmConfig.configs.filter(function(c) { return c.level === 'ENTERPRISE'; }).length;
            var personal = LlmConfig.configs.filter(function(c) { return c.level === 'PERSONAL'; }).length;
            var providers = LlmConfig.providers.length;
            
            var totalEl = document.getElementById('stat-total');
            var enterpriseEl = document.getElementById('stat-enterprise');
            var personalEl = document.getElementById('stat-personal');
            var providersEl = document.getElementById('stat-providers');
            
            if (totalEl) totalEl.textContent = total;
            if (enterpriseEl) enterpriseEl.textContent = enterprise;
            if (personalEl) personalEl.textContent = personal;
            if (providersEl) providersEl.textContent = providers;
        },
        
        updateCurrentStatus: function() {
            var providerNameEl = document.getElementById('currentProviderName');
            var modelNameEl = document.getElementById('currentModelName');
            
            if (providerNameEl) {
                providerNameEl.textContent = LlmConfig.getProviderDisplayName(LlmConfig.currentProvider);
            }
            if (modelNameEl) {
                modelNameEl.textContent = LlmConfig.currentModel;
            }
        },
        
        filterConfigs: function() {
            var levelFilter = document.getElementById('filter-level').value;
            var providerFilter = document.getElementById('filter-provider').value;
            
            var filtered = LlmConfig.configs;
            
            if (levelFilter) {
                filtered = filtered.filter(function(c) { return c.level === levelFilter; });
            }
            
            if (providerFilter) {
                filtered = filtered.filter(function(c) { 
                    return (c.providerType || c.provider) === providerFilter; 
                });
            }
            
            var tbody = document.getElementById('config-table-body');
            if (!tbody) return;
            
            if (!filtered.length) {
                tbody.innerHTML = '<tr><td colspan="7" class="nx-text-center nx-text-secondary nx-py-8">没有匹配的配置</td></tr>';
                return;
            }
            
            var originalConfigs = LlmConfig.configs;
            LlmConfig.configs = filtered;
            LlmConfig.renderConfigs();
            LlmConfig.configs = originalConfigs;
        },
        
        openCreateModal: function() {
            LlmConfig.currentConfig = null;
            
            var modalTitle = document.querySelector('#modal-title span');
            if (modalTitle) modalTitle.textContent = '新建配置';
            
            document.getElementById('config-form').reset();
            document.getElementById('config-id').value = '';
            document.getElementById('owner-id-group').style.display = 'none';
            document.getElementById('config-temperature').value = '0.7';
            document.getElementById('config-max-tokens').value = '4096';
            
            document.getElementById('config-modal').classList.add('nx-modal--open');
        },
        
        editConfig: function(id) {
            var config = LlmConfig.configs.find(function(c) { return c.id === id; });
            if (!config) {
                console.error('Config not found:', id);
                return;
            }
            
            LlmConfig.currentConfig = config;
            
            var modalTitle = document.querySelector('#modal-title span');
            if (modalTitle) modalTitle.textContent = '编辑配置';
            
            document.getElementById('config-id').value = config.id || '';
            document.getElementById('config-name').value = config.name || '';
            document.getElementById('config-level').value = config.level || '';
            document.getElementById('config-owner-id').value = config.scopeId || '';
            
            var providerSelect = document.getElementById('config-provider');
            providerSelect.value = config.providerType || config.provider || '';
            
            LlmConfig.onProviderChange();
            
            setTimeout(function() {
                var modelSelect = document.getElementById('config-model');
                modelSelect.value = config.model || '';
            }, 100);
            
            var apiKey = config.providerConfig && config.providerConfig.apiKey ? config.providerConfig.apiKey : '';
            var apiKeyInput = document.getElementById('config-api-key');
            if (apiKey) {
                apiKeyInput.value = apiKey;
            } else {
                apiKeyInput.value = '';
            }
            
            document.getElementById('config-base-url').value = config.providerConfig && config.providerConfig.baseUrl ? config.providerConfig.baseUrl : '';
            document.getElementById('config-temperature').value = config.options && config.options.temperature ? config.options.temperature : 0.7;
            document.getElementById('config-max-tokens').value = config.options && config.options.max_tokens ? config.options.max_tokens : 4096;
            
            LlmConfig.onLevelChange();
            
            document.getElementById('config-modal').classList.add('nx-modal--open');
        },
        
        closeModal: function() {
            document.getElementById('config-modal').classList.remove('nx-modal--open');
        },
        
        toggleApiKeyVisibility: function() {
            var input = document.getElementById('config-api-key');
            var icon = document.getElementById('api-key-toggle-icon');
            
            if (input.type === 'password') {
                input.type = 'text';
                icon.className = 'ri-eye-off-line';
            } else {
                input.type = 'password';
                icon.className = 'ri-eye-line';
            }
        },
        
        onLevelChange: function() {
            var level = document.getElementById('config-level').value;
            var ownerGroup = document.getElementById('owner-id-group');
            
            if (!ownerGroup) return;
            
            if (level === 'PERSONAL' || level === 'SCENE' || level === 'DEPARTMENT') {
                ownerGroup.style.display = 'block';
            } else {
                ownerGroup.style.display = 'none';
            }
        },
        
        onProviderChange: function() {
            var providerType = document.getElementById('config-provider').value;
            var modelSelect = document.getElementById('config-model');
            
            modelSelect.innerHTML = '<option value="">请选择模型</option>';
            
            if (!providerType) return;
            
            var provider = LlmConfig.providers.find(function(p) { return p.type === providerType; });
            if (provider && provider.models) {
                provider.models.forEach(function(model) {
                    modelSelect.innerHTML += '<option value="' + model + '">' + model + '</option>';
                });
            }
        },
        
        saveConfig: function() {
            var id = document.getElementById('config-id').value;
            
            var config = {
                name: document.getElementById('config-name').value,
                level: document.getElementById('config-level').value,
                scopeId: document.getElementById('config-owner-id').value || 'default',
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
            
            var url = id ? '/api/v1/llm-config/' + id : '/api/v1/llm-config';
            var method = id ? 'PUT' : 'POST';
            
            fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(config)
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.status === 'success') {
                    LlmConfig.closeModal();
                    LlmConfig.loadConfigs();
                    LlmConfig.showSuccess('保存成功');
                } else {
                    alert('保存失败: ' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('Failed to save config:', error);
                alert('保存失败: ' + error.message);
            });
        },
        
        setConfig: function(provider, model) {
            fetch('/api/v1/llm/models/set', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    provider: provider,
                    modelId: model
                })
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result && result.requestStatus === 200) {
                    LlmConfig.currentProvider = provider;
                    LlmConfig.currentModel = model;
                    LlmConfig.renderProviders();
                    LlmConfig.updateCurrentStatus();
                    LlmConfig.closeProviderModal();
                    LlmConfig.showSuccess('配置已更新');
                } else {
                    LlmConfig.showError(result.message || '设置失败');
                }
            })
            .catch(function(error) {
                console.error('[LlmConfig] 设置异常:', error);
                LlmConfig.showError('设置配置异常');
            });
        },
        
        openDeleteModal: function(id, name) {
            LlmConfig.deleteConfigId = id;
            document.getElementById('delete-config-name').textContent = name;
            document.getElementById('delete-modal').classList.add('nx-modal--open');
        },
        
        closeDeleteModal: function() {
            LlmConfig.deleteConfigId = null;
            document.getElementById('delete-modal').classList.remove('nx-modal--open');
        },
        
        confirmDelete: function() {
            if (!LlmConfig.deleteConfigId) return;
            
            fetch('/api/v1/llm-config/' + LlmConfig.deleteConfigId, {
                method: 'DELETE'
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.status === 'success') {
                    LlmConfig.closeDeleteModal();
                    LlmConfig.loadConfigs();
                    LlmConfig.showSuccess('删除成功');
                } else {
                    alert('删除失败: ' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('Failed to delete config:', error);
                alert('删除失败: ' + error.message);
            });
        },
        
        showSuccess: function(message) {
            var toast = document.createElement('div');
            toast.className = 'toast toast-success';
            toast.innerHTML = '<i class="ri-check-line"></i> ' + message;
            document.body.appendChild(toast);
            setTimeout(function() {
                toast.remove();
            }, 3000);
        },
        
        showError: function(message) {
            var toast = document.createElement('div');
            toast.className = 'toast toast-error';
            toast.innerHTML = '<i class="ri-error-warning-line"></i> ' + message;
            document.body.appendChild(toast);
            setTimeout(function() {
                toast.remove();
            }, 3000);
        }
    };

    window.LlmConfig = LlmConfig;
    
    document.addEventListener('DOMContentLoaded', function() {
        LlmConfig.init();
    });

})();
