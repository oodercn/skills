(function() {
    'use strict';
    
    var KnowledgeBase = {
        knowledgeBases: [],
        currentKb: null,
        tags: [],
        
        init: function() {
            this.loadKnowledgeBases();
        },
        
        loadKnowledgeBases: function() {
            var self = this;
            
            ApiClient.get('/api/v1/knowledge-bases')
                .then(function(result) {
                    if (result.status === 'success' && result.data) {
                        self.knowledgeBases = result.data;
                        self.renderKbList();
                    } else {
                        console.error('Failed to load knowledge bases:', result.message);
                        self.knowledgeBases = [];
                        self.renderKbList();
                    }
                })
                .catch(function(error) {
                    console.error('Failed to load knowledge bases:', error);
                    self.knowledgeBases = [];
                    self.renderKbList();
                });
        },
        
        renderKbList: function() {
            var container = document.getElementById('kbList');
            var searchTerm = document.getElementById('kbSearch').value.toLowerCase();
            
            var filtered = this.knowledgeBases.filter(function(kb) {
                return kb.name.toLowerCase().includes(searchTerm) || 
                    (kb.description && kb.description.toLowerCase().includes(searchTerm));
            });
            
            if (filtered.length === 0) {
                container.innerHTML = '<div style="text-align: center; padding: 20px; color: var(--nx-text-secondary);">无匹配结果</div>';
                return;
            }
            
            var self = this;
            container.innerHTML = filtered.map(function(kb) {
                var layer = kb.layerConfig ? kb.layerConfig.layer : 'GENERAL';
                var layerIcon = layer === 'GENERAL' ? 'ri-global-line' : (layer === 'PROFESSIONAL' ? 'ri-briefcase-line' : 'ri-flashlight-line');
                var activeClass = self.currentKb && self.currentKb.kbId === kb.kbId ? 'active' : '';
                var docCount = kb.documentCount || 0;
                return '<div class="kb-item ' + activeClass + '" onclick="selectKb(\'' + kb.kbId + '\')">' +
                    '<div class="kb-item-name"><i class="' + layerIcon + '"></i> ' + kb.name + '</div>' +
                    '<div class="kb-item-desc">' + (kb.description || '无描述') + '</div>' +
                    '<div class="kb-item-meta">' +
                        '<span><i class="ri-file-text-line"></i> ' + docCount + ' 文档</span>' +
                        '<span><i class="ri-' + (kb.visibility === 'public' ? 'global' : (kb.visibility === 'team' ? 'team' : 'lock')) + '-line"></i> ' + kb.visibility + '</span>' +
                    '</div>' +
                '</div>';
            }).join('');
        },
        
        filterKbList: function() {
            this.renderKbList();
        },
        
        selectKb: function(kbId) {
            this.currentKb = this.knowledgeBases.find(function(kb) { return kb.kbId === kbId; });
            if (!this.currentKb) return;
            
            document.getElementById('emptyState').style.display = 'none';
            document.getElementById('kbDetail').style.display = 'flex';
            
            document.getElementById('kbName').textContent = this.currentKb.name;
            
            var docCount = this.currentKb.documentCount || 0;
            document.getElementById('indexDocCount').textContent = docCount + ' 文档已索引';
            
            var indexStatus = this.currentKb.indexStatus ? this.currentKb.indexStatus.status : 'pending';
            var progress = indexStatus === 'completed' ? 100 : (indexStatus === 'indexing' ? (this.currentKb.indexStatus.progress || 0) : 0);
            document.getElementById('indexProgressFill').style.width = progress + '%';
            document.getElementById('indexPercent').textContent = progress + '%';
            document.getElementById('indexStatusText').textContent = indexStatus === 'completed' ? '已完成' : (indexStatus === 'indexing' ? '索引中...' : '待索引');
            
            if (this.currentKb.embeddingModel) {
                document.getElementById('embeddingModel').value = this.currentKb.embeddingModel;
            }
            if (this.currentKb.chunkSize) {
                document.getElementById('chunkSize').value = this.currentKb.chunkSize;
            }
            if (this.currentKb.chunkOverlap) {
                document.getElementById('chunkOverlap').value = this.currentKb.chunkOverlap;
            }
            if (this.currentKb.visibility) {
                document.getElementById('visibility').value = this.currentKb.visibility;
            }
            
            this.renderKbList();
            this.renderDocuments();
        },
        
        renderDocuments: function() {
            var container = document.getElementById('docList');
            container.innerHTML = '<div style="text-align: center; padding: 40px; color: var(--nx-text-secondary);">' +
                '<i class="ri-file-list-3-line" style="font-size: 48px; display: block; margin-bottom: 12px;"></i>' +
                '<div>文档管理功能开发中</div>' +
                '<div style="font-size: 12px; margin-top: 8px;">将通过 /api/v1/knowledge-bases/{kbId}/documents API 获取文档列表</div>' +
            '</div>';
        },
        
        switchTab: function(tab) {
            document.querySelectorAll('.kb-tab').forEach(function(el) { el.classList.remove('active'); });
            document.querySelector('.kb-tab[data-tab="' + tab + '"]').classList.add('active');
            
            document.getElementById('tab-overview').style.display = tab === 'overview' ? 'block' : 'none';
            document.getElementById('tab-documents').style.display = tab === 'documents' ? 'block' : 'none';
            document.getElementById('tab-layer').style.display = tab === 'layer' ? 'block' : 'none';
            document.getElementById('tab-settings').style.display = tab === 'settings' ? 'block' : 'none';
        },
        
        showCreateKbModal: function() {
            this.tags = [];
            this.renderTags();
            document.getElementById('newKbName').value = '';
            document.getElementById('newKbDesc').value = '';
            document.getElementById('newKbLayer').value = 'GENERAL';
            document.getElementById('newKbVisibility').value = 'private';
            document.getElementById('createKbModal').classList.add('open');
        },
        
        closeModal: function(modalId) {
            document.getElementById(modalId).classList.remove('open');
        },
        
        handleTagInput: function(event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                var input = event.target;
                var value = input.value.trim();
                if (value && !this.tags.includes(value)) {
                    this.tags.push(value);
                    this.renderTags();
                }
                input.value = '';
            }
        },
        
        renderTags: function() {
            var container = document.getElementById('tagInput');
            var input = '<input type="text" placeholder="输入标签后按回车" onkeydown="handleTagInput(event)">';
            container.innerHTML = this.tags.map(function(tag) { 
                return '<span class="tag-item">' + tag + ' <span class="tag-remove" onclick="removeTag(\'' + tag + '\')">&times;</span></span>';
            }).join('') + input;
        },
        
        removeTag: function(tag) {
            this.tags = this.tags.filter(function(t) { return t !== tag; });
            this.renderTags();
        },
        
        createKb: function() {
            var self = this;
            var name = document.getElementById('newKbName').value;
            var description = document.getElementById('newKbDesc').value;
            var layer = document.getElementById('newKbLayer').value;
            var visibility = document.getElementById('newKbVisibility').value;
            
            if (!name) {
                alert('请输入知识库名称');
                return;
            }
            
            var newKb = {
                name: name,
                description: description,
                visibility: visibility,
                embeddingModel: 'text-embedding-ada-002',
                chunkSize: 500,
                chunkOverlap: 50,
                tags: this.tags.slice()
            };
            
            newKb.layerConfig = {
                layer: layer,
                priority: layer === 'GENERAL' ? 0 : (layer === 'PROFESSIONAL' ? 1 : 2),
                enabled: true
            };
            
            ApiClient.post('/api/v1/knowledge-bases', newKb)
                .then(function(result) {
                    if (result.status === 'success' && result.data) {
                        self.knowledgeBases.push(result.data);
                        self.renderKbList();
                        self.closeModal('createKbModal');
                        self.selectKb(result.data.kbId);
                    } else {
                        alert('创建失败: ' + (result.message || '未知错误'));
                    }
                })
                .catch(function(error) {
                    console.error('Create KB error:', error);
                    alert('创建失败: ' + error.message);
                });
        },
        
        rebuildIndex: function() {
            if (!this.currentKb) return;
            var self = this;
            
            ApiClient.post('/api/v1/knowledge-bases/' + this.currentKb.kbId + '/rebuild-index')
                .then(function(result) {
                    if (result.status === 'success') {
                        alert('索引重建已启动');
                        if (self.currentKb.indexStatus) {
                            self.currentKb.indexStatus.status = 'indexing';
                            self.currentKb.indexStatus.progress = 0;
                        }
                        self.selectKb(self.currentKb.kbId);
                    } else {
                        alert('重建索引失败: ' + (result.message || '未知错误'));
                    }
                })
                .catch(function(error) {
                    console.error('Rebuild index error:', error);
                    alert('重建索引失败: ' + error.message);
                });
        },
        
        showEditKbModal: function() {
            if (!this.currentKb) return;
            alert('编辑功能开发中 - 将通过 PUT /api/v1/knowledge-bases/' + this.currentKb.kbId + ' 实现');
        },
        
        deleteKb: function() {
            if (!this.currentKb) return;
            var self = this;
            
            if (confirm('确定要删除知识库 "' + this.currentKb.name + '" 吗？')) {
                ApiClient.delete('/api/v1/knowledge-bases/' + this.currentKb.kbId)
                    .then(function(result) {
                        if (result.status === 'success') {
                            self.knowledgeBases = self.knowledgeBases.filter(function(kb) { return kb.kbId !== self.currentKb.kbId; });
                            self.currentKb = null;
                            document.getElementById('emptyState').style.display = 'block';
                            document.getElementById('kbDetail').style.display = 'none';
                            self.renderKbList();
                        } else {
                            alert('删除失败: ' + (result.message || '未知错误'));
                        }
                    })
                    .catch(function(error) {
                        console.error('Delete KB error:', error);
                        alert('删除失败: ' + error.message);
                    });
            }
        },
        
        saveSettings: function() {
            if (!this.currentKb) return;
            var self = this;
            
            var updateData = {
                name: this.currentKb.name,
                description: this.currentKb.description,
                visibility: document.getElementById('visibility').value,
                embeddingModel: document.getElementById('embeddingModel').value,
                chunkSize: parseInt(document.getElementById('chunkSize').value),
                chunkOverlap: parseInt(document.getElementById('chunkOverlap').value),
                layerConfig: this.currentKb.layerConfig
            };
            
            ApiClient.put('/api/v1/knowledge-bases/' + this.currentKb.kbId, updateData)
                .then(function(result) {
                    if (result.status === 'success') {
                        alert('设置已保存');
                        var idx = self.knowledgeBases.findIndex(function(kb) { return kb.kbId === self.currentKb.kbId; });
                        if (idx >= 0) {
                            self.knowledgeBases[idx] = result.data;
                            self.currentKb = result.data;
                        }
                    } else {
                        alert('保存失败: ' + (result.message || '未知错误'));
                    }
                })
                .catch(function(error) {
                    console.error('Save settings error:', error);
                    alert('保存失败: ' + error.message);
                });
        },
        
        showAddTextModal: function() { alert('添加文本功能开发中'); },
        showUploadModal: function() { alert('上传文件功能开发中'); },
        showUrlImportModal: function() { alert('URL导入功能开发中'); }
    };
    
    window.loadKnowledgeBases = function() { KnowledgeBase.loadKnowledgeBases(); };
    window.filterKbList = function() { KnowledgeBase.filterKbList(); };
    window.selectKb = function(kbId) { KnowledgeBase.selectKb(kbId); };
    window.switchTab = function(tab) { KnowledgeBase.switchTab(tab); };
    window.showCreateKbModal = function() { KnowledgeBase.showCreateKbModal(); };
    window.closeModal = function(modalId) { KnowledgeBase.closeModal(modalId); };
    window.handleTagInput = function(event) { KnowledgeBase.handleTagInput(event); };
    window.removeTag = function(tag) { KnowledgeBase.removeTag(tag); };
    window.createKb = function() { KnowledgeBase.createKb(); };
    window.rebuildIndex = function() { KnowledgeBase.rebuildIndex(); };
    window.showEditKbModal = function() { KnowledgeBase.showEditKbModal(); };
    window.deleteKb = function() { KnowledgeBase.deleteKb(); };
    window.saveSettings = function() { KnowledgeBase.saveSettings(); };
    window.showAddTextModal = function() { KnowledgeBase.showAddTextModal(); };
    window.showUploadModal = function() { KnowledgeBase.showUploadModal(); };
    window.showUrlImportModal = function() { KnowledgeBase.showUrlImportModal(); };
    
    document.addEventListener('DOMContentLoaded', KnowledgeBase.init.bind(KnowledgeBase));
})();
