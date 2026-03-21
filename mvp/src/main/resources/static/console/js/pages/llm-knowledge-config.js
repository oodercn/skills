(function() {
    'use strict';
    
    var LlmKnowledgeConfigPage = {
        embeddingConfig: null,
        dictionaryTerms: [],
        synonymMappings: [],
        llmInterfaces: [],
        promptTemplates: [],
        
        init: function() {
            this.loadEmbeddingConfig();
            this.loadDictionaries();
            this.loadSynonyms();
            this.loadInterfaces();
            this.loadPromptTemplate();
        },
        
        loadEmbeddingConfig: function() {
            var self = this;
            ApiClient.get('/api/v1/embedding/config')
                .then(function(result) {
                    if (result && result.status === 'success' && result.data) {
                        self.embeddingConfig = result.data;
                        var modelSelect = document.getElementById('embeddingModel');
                        if (modelSelect) modelSelect.value = result.data.currentModel || 'text-embedding-ada-002';
                        var dimInput = document.getElementById('vectorDimension');
                        if (dimInput) dimInput.value = result.data.dimensions || 1536;
                        var chunkInput = document.getElementById('chunkSize');
                        if (chunkInput) chunkInput.value = result.data.defaultChunkSize || 500;
                        var overlapInput = document.getElementById('chunkOverlap');
                        if (overlapInput) overlapInput.value = result.data.defaultChunkOverlap || 50;
                    }
                })
                .catch(function(error) {
                    console.warn('Failed to load embedding config:', error);
                });
        },
        
        loadDictionaries: function() {
            var self = this;
            ApiClient.get('/api/v1/llm-knowledge-config/dictionaries')
                .then(function(result) {
                    if (result && result.status === 'success' && result.data) {
                        self.dictionaryTerms = result.data;
                        self.renderDictionaries();
                    }
                })
                .catch(function(error) {
                    console.warn('Failed to load dictionaries:', error);
                });
        },
        
        renderDictionaries: function() {
            var container = document.getElementById('dictTableBody');
            if (!container) return;
            
            if (!this.dictionaryTerms || this.dictionaryTerms.length === 0) {
                container.innerHTML = '<tr><td colspan="5" style="text-align: center; color: var(--nx-text-secondary);">暂无术语</td></tr>';
                return;
            }
            
            var categoryNames = { tech: '技术术语', business: '业务术语', ai: 'AI术语', other: '其他' };
            
            container.innerHTML = this.dictionaryTerms.map(function(term) {
                return '<tr>' +
                    '<td><span class="dict-key">' + (term.term || '') + '</span></td>' +
                    '<td class="dict-desc">' + (term.fullName || term.description || '') + '</td>' +
                    '<td><span class="layer-badge ' + (term.category === 'ai' ? 'professional' : 'general') + '">' + (categoryNames[term.category] || term.category || '其他') + '</span></td>' +
                    '<td>' + (term.kbName || '-') + '</td>' +
                    '<td class="dict-actions">' +
                        '<button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="editDictTerm(\'' + term.termId + '\')"><i class="ri-edit-line"></i></button>' +
                        '<button class="nx-btn nx-btn--ghost nx-btn--sm nx-btn--danger" onclick="deleteDictTerm(\'' + term.termId + '\')"><i class="ri-delete-bin-line"></i></button>' +
                    '</td>' +
                '</tr>';
            }).join('');
        },
        
        loadSynonyms: function() {
            var self = this;
            ApiClient.get('/api/v1/llm-knowledge-config/synonyms')
                .then(function(result) {
                    if (result && result.status === 'success' && result.data) {
                        self.synonymMappings = result.data;
                    }
                })
                .catch(function(error) {
                    console.warn('Failed to load synonyms:', error);
                });
        },
        
        loadInterfaces: function() {
            var self = this;
            ApiClient.get('/api/v1/llm-knowledge-config/interfaces')
                .then(function(result) {
                    if (result && result.status === 'success' && result.data) {
                        self.llmInterfaces = result.data;
                    }
                })
                .catch(function(error) {
                    console.warn('Failed to load interfaces:', error);
                });
        },
        
        loadPromptTemplate: function() {
            var templateEl = document.getElementById('promptTemplate');
            if (!templateEl) return;
            
            ApiClient.get('/api/v1/llm-knowledge-config/prompt-templates/default')
                .then(function(result) {
                    if (result && result.status === 'success' && result.data && result.data.content) {
                        templateEl.value = result.data.content;
                    }
                })
                .catch(function(error) {
                    console.warn('Failed to load prompt template:', error);
                });
        },
        
        testEmbedding: function() {
            var textEl = document.getElementById('testText');
            var modelEl = document.getElementById('embeddingModel');
            var text = textEl ? textEl.value : '';
            var modelId = modelEl ? modelEl.value : 'text-embedding-ada-002';
            
            if (!text) {
                alert('请输入测试文本');
                return;
            }
            
            var result = document.getElementById('testResult');
            if (!result) return;
            
            result.style.display = 'block';
            result.innerHTML = '<i class="ri-loader-4-line ri-spin"></i> 正在向量化...';
            
            ApiClient.post('/api/v1/embedding/test', { modelId: modelId, text: text })
                .then(function(response) {
                    if (response && response.status === 'success' && response.data) {
                        var data = response.data;
                        result.innerHTML = '<div style="margin-bottom: 8px;"><strong>向量维度:</strong> ' + (data.dimensions || 1536) + '</div>' +
                            '<div style="margin-bottom: 8px;"><strong>前10维:</strong> [' + (data.sampleVector || []).map(function(v) { return v.toFixed(4); }).join(', ') + ', ...]</div>' +
                            '<div><strong>文本长度:</strong> ' + (data.textLength || text.length) + ' 字符</div>';
                    } else {
                        result.innerHTML = '<div style="color: red;">测试失败: ' + (response.message || '未知错误') + '</div>';
                    }
                })
                .catch(function(error) {
                    result.innerHTML = '<div style="color: red;">测试失败: ' + (error.message || '网络错误') + '</div>';
                });
        },
        
        addDictionaryTerm: function(term) {
            var self = this;
            return ApiClient.post('/api/v1/llm-knowledge-config/dictionaries', term)
                .then(function(result) {
                    if (result && result.status === 'success' && result.data) {
                        self.dictionaryTerms.push(result.data);
                        self.renderDictionaries();
                        return true;
                    }
                    alert('添加失败: ' + (result && result.message ? result.message : '未知错误'));
                    return false;
                })
                .catch(function(error) {
                    alert('添加失败: ' + (error.message || '网络错误'));
                    return false;
                });
        },
        
        deleteDictionaryTerm: function(termId) {
            var self = this;
            return ApiClient.delete('/api/v1/llm-knowledge-config/dictionaries/' + termId)
                .then(function(result) {
                    if (result && result.status === 'success') {
                        self.dictionaryTerms = self.dictionaryTerms.filter(function(t) { return t.termId !== termId; });
                        self.renderDictionaries();
                        return true;
                    }
                    return false;
                })
                .catch(function(error) {
                    console.error('Delete failed:', error);
                    return false;
                });
        },
        
        savePromptTemplate: function() {
            var templateEl = document.getElementById('promptTemplate');
            var content = templateEl ? templateEl.value : '';
            
            return ApiClient.post('/api/v1/llm-knowledge-config/prompt-templates', {
                name: '知识增强回答模板',
                description: '用于知识检索后的回答生成',
                content: content
            }).then(function(result) {
                if (result && result.status === 'success') {
                    alert('提示词模板已保存');
                    return true;
                }
                alert('保存失败: ' + (result && result.message ? result.message : '未知错误'));
                return false;
            }).catch(function(error) {
                alert('保存失败: ' + (error.message || '网络错误'));
                return false;
            });
        }
    };
    
    window.switchConfigTab = function(tab) {
        document.querySelectorAll('.config-tab').forEach(function(el) { el.classList.remove('active'); });
        var activeTab = document.querySelector('.config-tab[onclick="switchConfigTab(\'' + tab + '\')"]');
        if (activeTab) activeTab.classList.add('active');
        
        document.querySelectorAll('.tab-content').forEach(function(el) { el.classList.remove('active'); });
        var tabContent = document.getElementById('tab-' + tab);
        if (tabContent) tabContent.classList.add('active');
    };
    
    window.toggleInterface = function(header) {
        var body = header.nextElementSibling;
        if (body) body.classList.toggle('open');
    };
    
    window.testEmbedding = function() {
        LlmKnowledgeConfigPage.testEmbedding();
    };
    
    window.showAddDictModal = function() {
        var modal = document.getElementById('addDictModal');
        if (modal) modal.classList.add('open');
    };
    
    window.showAddSynonymModal = function() {
        alert('添加同义词映射功能开发中...');
    };
    
    window.showAddInterfaceModal = function() {
        alert('添加接口定义功能开发中...');
    };
    
    window.closeModal = function(id) {
        var modal = document.getElementById(id);
        if (modal) modal.classList.remove('open');
    };
    
    window.addDictTerm = function() {
        var termEl = document.getElementById('dictTerm');
        var fullEl = document.getElementById('dictFull');
        var descEl = document.getElementById('dictDesc');
        var categoryEl = document.getElementById('dictCategory');
        
        var term = termEl ? termEl.value.trim() : '';
        var full = fullEl ? fullEl.value.trim() : '';
        
        if (!term || !full) {
            alert('请填写术语和全称');
            return;
        }
        
        LlmKnowledgeConfigPage.addDictionaryTerm({
            term: term,
            fullName: full,
            description: descEl ? descEl.value.trim() : '',
            category: categoryEl ? categoryEl.value : 'other'
        }).then(function(success) {
            if (success) {
                closeModal('addDictModal');
                if (termEl) termEl.value = '';
                if (fullEl) fullEl.value = '';
                if (descEl) descEl.value = '';
            }
        });
    };
    
    window.editDictTerm = function(termId) {
        console.log('Edit dict term:', termId);
        alert('编辑功能开发中...');
    };
    
    window.deleteDictTerm = function(termId) {
        if (confirm('确定要删除此术语吗？')) {
            LlmKnowledgeConfigPage.deleteDictionaryTerm(termId);
        }
    };
    
    window.savePromptTemplate = function() {
        LlmKnowledgeConfigPage.savePromptTemplate();
    };
    
    window.resetPromptTemplate = function() {
        if (confirm('确定要重置为默认模板吗？')) {
            ApiClient.get('/api/v1/llm-knowledge-config/prompt-templates/default')
                .then(function(result) {
                    if (result && result.status === 'success' && result.data) {
                        var templateEl = document.getElementById('promptTemplate');
                        if (templateEl) templateEl.value = result.data.content || '';
                    }
                })
                .catch(function(error) {
                    console.error('Reset failed:', error);
                });
        }
    };
    
    document.addEventListener('DOMContentLoaded', LlmKnowledgeConfigPage.init.bind(LlmKnowledgeConfigPage));
})();
