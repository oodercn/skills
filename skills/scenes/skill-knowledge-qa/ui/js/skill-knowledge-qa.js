(function() {
    'use strict';
    
    var KnowledgeQA = {
        currentKb: '',
        currentModel: 'deepseek-chat',
        topK: 10,
        threshold: 0.7,
        knowledgeBases: [],
        selectedFiles: [],
        availableModels: [],
        
        init: function() {
            this.initTabs();
            this.initTheme();
            this.loadKnowledgeBases();
            this.loadDocuments();
            this.loadAvailableModels();
            this.bindEvents();
        },
        
        initTabs: function() {
            var self = this;
            var hashToTab = {
                'kb': 'kb',
                'docs': 'docs',
                'search': 'search',
                'qa': 'qa'
            };
            
            function switchTab(tabName) {
                document.querySelectorAll('.kb-tab').forEach(function(t) { t.classList.remove('active'); });
                document.querySelectorAll('.kb-tab-content').forEach(function(c) { c.classList.remove('active'); });
                var tab = document.querySelector('.kb-tab[data-tab="' + tabName + '"]');
                var content = document.getElementById('tab-' + tabName);
                if (tab) tab.classList.add('active');
                if (content) content.classList.add('active');
            }
            
            document.querySelectorAll('.kb-tab').forEach(function(tab) {
                tab.addEventListener('click', function() {
                    var tabName = this.getAttribute('data-tab');
                    switchTab(tabName);
                    window.location.hash = tabName;
                });
            });
            
            var hash = window.location.hash.slice(1);
            if (hash && hashToTab[hash]) {
                switchTab(hashToTab[hash]);
            }
            
            window.addEventListener('hashchange', function() {
                var newHash = window.location.hash.slice(1);
                if (newHash && hashToTab[newHash]) {
                    switchTab(hashToTab[newHash]);
                }
            });
        },
        
        initTheme: function() {
            var self = this;
            var theme = localStorage.getItem('nx-theme') || 'dark';
            document.documentElement.setAttribute('data-theme', theme);
            this.updateThemeIcon(theme);
            
            document.getElementById('themeToggle').addEventListener('click', function() {
                var currentTheme = document.documentElement.getAttribute('data-theme') || 'dark';
                var newTheme = currentTheme === 'dark' ? 'light' : 'dark';
                document.documentElement.setAttribute('data-theme', newTheme);
                localStorage.setItem('nx-theme', newTheme);
                self.updateThemeIcon(newTheme);
            });
        },
        
        updateThemeIcon: function(theme) {
            var icon = document.getElementById('themeIcon');
            icon.className = theme === 'dark' ? 'ri-moon-line' : 'ri-sun-line';
        },
        
        loadKnowledgeBases: function() {
            var self = this;
            NexusAPI.get('/api/kb/list', function(response) {
                if (response.status === 'success' && response.data) {
                    self.knowledgeBases = response.data.kbs || [];
                    self.renderKbGrid(self.knowledgeBases);
                    self.renderKbSelect(self.knowledgeBases);
                    self.renderUploadKbSelect(self.knowledgeBases);
                }
            });
        },
        
        renderKbGrid: function(kbs) {
            var self = this;
            var grid = document.getElementById('kbGrid');
            if (!kbs.length) {
                grid.innerHTML = '<div class="empty-state"><i class="ri-database-2-line"></i><h3>暂无知识库</h3><p>点击"新建知识库"创建您的第一个知识库</p></div>';
                return;
            }
            grid.innerHTML = kbs.map(function(kb) {
                var selected = self.currentKb === kb.id ? ' selected' : '';
                return '<div class="kb-card' + selected + '" data-id="' + kb.id + '" onclick="KnowledgeQA.selectKb(\'' + kb.id + '\')">' +
                    '<div class="kb-card-actions">' +
                    '<button class="nx-btn nx-btn--secondary" onclick="event.stopPropagation(); KnowledgeQA.editKb(\'' + kb.id + '\')"><i class="ri-edit-line"></i></button>' +
                    '<button class="nx-btn nx-btn--secondary" onclick="event.stopPropagation(); KnowledgeQA.deleteKb(\'' + kb.id + '\')"><i class="ri-delete-bin-line"></i></button>' +
                    '</div>' +
                    '<h3><i class="ri-database-2-line"></i> ' + kb.name + '</h3>' +
                    '<p>' + (kb.description || '暂无描述') + '</p>' +
                    '<div class="kb-stats">' +
                    '<span><i class="ri-file-text-line"></i> ' + kb.docCount + ' 文档</span>' +
                    '<span><i class="ri-time-line"></i> ' + kb.updateTime + '</span>' +
                    '</div></div>';
            }).join('');
        },
        
        selectKb: function(kbId) {
            this.currentKb = kbId;
            this.loadDocuments();
            this.renderKbGrid(this.knowledgeBases);
            var kb = this.knowledgeBases.find(function(k) { return k.id === kbId; });
            if (kb) {
                this.showToast('已选择知识库: ' + kb.name, 'success');
            }
        },
        
        renderKbSelect: function(kbs) {
            var select = document.getElementById('kbSelect');
            select.innerHTML = '<option value="">全部知识库</option>' +
                kbs.map(function(kb) {
                    return '<option value="' + kb.id + '">' + kb.name + '</option>';
                }).join('');
        },
        
        renderUploadKbSelect: function(kbs) {
            var select = document.getElementById('uploadKbSelect');
            select.innerHTML = '<option value="">请选择知识库</option>' +
                kbs.map(function(kb) {
                    return '<option value="' + kb.id + '">' + kb.name + '</option>';
                }).join('');
        },
        
        loadDocuments: function() {
            var self = this;
            var url = '/api/kb/docs';
            if (this.currentKb) {
                url += '?kbId=' + encodeURIComponent(this.currentKb);
            }
            NexusAPI.get(url, function(response) {
                if (response.status === 'success' && response.data) {
                    self.renderDocList(response.data.docs || []);
                }
            });
        },
        
        loadAvailableModels: function() {
            var self = this;
            var models = [];
            
            NexusAPI.get('/api/llm/deepseek/models', function(response) {
                if (response.status === 'success' && response.models) {
                    response.models.forEach(function(m) {
                        models.push({
                            id: m.id,
                            name: m.name,
                            provider: 'deepseek',
                            type: m.type
                        });
                    });
                }
                
                NexusAPI.get('/api/llm/baidu/models', function(response2) {
                    if (response2.status === 'success' && response2.models) {
                        response2.models.forEach(function(m) {
                            models.push({
                                id: m.id,
                                name: m.name,
                                provider: 'baidu',
                                type: m.type
                            });
                        });
                    }
                    
                    self.availableModels = models;
                    self.renderModelSelect(models);
                    self.updateModelStatus();
                });
            });
        },
        
        renderModelSelect: function(models) {
            var select = document.getElementById('modelSelect');
            if (!select) return;
            
            var deepseekModels = models.filter(function(m) { return m.provider === 'deepseek'; });
            var baiduModels = models.filter(function(m) { return m.provider === 'baidu'; });
            
            var html = '';
            
            if (deepseekModels.length > 0) {
                html += '<optgroup label="DeepSeek">';
                deepseekModels.forEach(function(m) {
                    var selected = m.id === this.currentModel ? ' selected' : '';
                    html += '<option value="' + m.id + '"' + selected + '>' + m.name + '</option>';
                }.bind(this));
                html += '</optgroup>';
            }
            
            if (baiduModels.length > 0) {
                html += '<optgroup label="百度千帆">';
                baiduModels.forEach(function(m) {
                    var selected = m.id === this.currentModel ? ' selected' : '';
                    html += '<option value="' + m.id + '"' + selected + '>' + m.name + '</option>';
                }.bind(this));
                html += '</optgroup>';
            }
            
            select.innerHTML = html;
        },
        
        updateModelStatus: function() {
            var statusDiv = document.getElementById('modelStatus');
            if (!statusDiv) return;
            
            var model = this.availableModels.find(function(m) { return m.id === this.currentModel; }.bind(this));
            if (model) {
                var providerName = model.provider === 'deepseek' ? 'DeepSeek' : '百度千帆';
                statusDiv.innerHTML = '<span style="color: var(--ns-success);">●</span> ' + providerName + ' - ' + model.name;
            }
        },
        
        renderDocList: function(docs) {
            var list = document.getElementById('docList');
            if (!docs.length) {
                list.innerHTML = '<div class="empty-state"><i class="ri-file-text-line"></i><h3>暂无文档</h3><p>点击"上传文档"添加您的第一个文档</p></div>';
                return;
            }
            list.innerHTML = docs.map(function(doc) {
                return '<div class="doc-item">' +
                    '<div class="doc-icon"><i class="ri-file-text-line"></i></div>' +
                    '<div class="doc-info">' +
                    '<h4>' + doc.name + '</h4>' +
                    '<p>' + doc.size + ' · 上传于 ' + doc.uploadTime + ' · ' + (doc.status === 'indexed' ? '已索引' : '索引中') + '</p>' +
                    '</div>' +
                    '<div class="doc-actions">' +
                    '<button class="nx-btn nx-btn--secondary" onclick="KnowledgeQA.viewDoc(\'' + doc.id + '\')"><i class="ri-eye-line"></i></button>' +
                    '<button class="nx-btn nx-btn--secondary" onclick="KnowledgeQA.deleteDoc(\'' + doc.id + '\')"><i class="ri-delete-bin-line"></i></button>' +
                    '</div></div>';
            }).join('');
        },
        
        doSearch: function() {
            var self = this;
            var query = document.getElementById('searchInput').value.trim();
            if (!query) {
                this.showToast('请输入搜索关键词', 'error');
                return;
            }
            
            var resultsDiv = document.getElementById('searchResults');
            resultsDiv.innerHTML = '<div class="empty-state"><div class="loading-spinner"></div><h3>搜索中...</h3></div>';
            
            NexusAPI.post('/api/kb/search', { query: query, topK: this.topK, threshold: this.threshold }, function(response) {
                if (response.status === 'success' && response.data) {
                    self.renderSearchResults(response.data.results || []);
                } else {
                    resultsDiv.innerHTML = '<div class="empty-state"><i class="ri-error-warning-line"></i><h3>搜索失败</h3><p>' + (response.message || '请稍后重试') + '</p></div>';
                }
            });
        },
        
        renderSearchResults: function(results) {
            var resultsDiv = document.getElementById('searchResults');
            if (!results.length) {
                resultsDiv.innerHTML = '<div class="empty-state"><i class="ri-search-line"></i><h3>未找到相关结果</h3><p>请尝试其他关键词</p></div>';
                return;
            }
            resultsDiv.innerHTML = '<div class="doc-list">' + results.map(function(r) {
                return '<div class="doc-item">' +
                    '<div class="doc-icon"><i class="ri-file-text-line"></i></div>' +
                    '<div class="doc-info">' +
                    '<h4>' + r.title + '</h4>' +
                    '<p>相关度: ' + Math.round(r.score * 100) + '% · 来源: ' + r.source + '</p>' +
                    '</div></div>';
            }).join('') + '</div>';
        },
        
        sendQuestion: function() {
            var self = this;
            var input = document.getElementById('questionInput');
            var question = input.value.trim();
            if (!question) {
                this.showToast('请输入问题', 'error');
                return;
            }
            
            var modelSelect = document.getElementById('modelSelect');
            var selectedModel = modelSelect ? modelSelect.value : this.currentModel;
            
            var messages = document.getElementById('chatMessages');
            messages.innerHTML += '<div class="message user"><div class="message-avatar"><i class="ri-user-line"></i></div><div class="message-content">' + this.escapeHtml(question) + '</div></div>';
            input.value = '';
            
            messages.innerHTML += '<div class="message" id="typing-indicator"><div class="message-avatar"><i class="ri-robot-line"></i></div><div class="message-content"><div class="loading-spinner"></div></div></div>';
            messages.scrollTop = messages.scrollHeight;
            
            var kbId = document.getElementById('kbSelect').value;
            NexusAPI.post('/api/kb/qa', { 
                question: question, 
                kbId: kbId, 
                topK: this.topK, 
                threshold: this.threshold,
                model: selectedModel
            }, function(response) {
                var typing = document.getElementById('typing-indicator');
                if (typing) typing.remove();
                
                if (response.status === 'success' && response.data) {
                    var answer = response.data.answer;
                    var sources = response.data.sources || [];
                    var model = response.data.model || selectedModel;
                    var sourceHtml = sources.length ? '<br><br><small style="opacity: 0.7">来源: ' + sources.map(function(s) { return s.title + ' (' + Math.round(s.score * 100) + '%)'; }).join(', ') + '</small>' : '';
                    var modelHtml = '<br><small style="opacity: 0.5; font-size: 11px;">模型: ' + model + '</small>';
                    messages.innerHTML += '<div class="message"><div class="message-avatar"><i class="ri-robot-line"></i></div><div class="message-content">' + self.escapeHtml(answer) + sourceHtml + modelHtml + '</div></div>';
                } else {
                    messages.innerHTML += '<div class="message"><div class="message-avatar"><i class="ri-robot-line"></i></div><div class="message-content">抱歉，处理您的问题时出现错误：' + (response.message || '请稍后重试') + '</div></div>';
                }
                messages.scrollTop = messages.scrollHeight;
            });
        },
        
        escapeHtml: function(text) {
            var div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        },
        
        showToast: function(message, type) {
            var toast = document.createElement('div');
            toast.className = 'toast toast--' + (type || 'info');
            toast.textContent = message;
            document.body.appendChild(toast);
            setTimeout(function() { toast.remove(); }, 3000);
        },
        
        openModal: function(id) {
            document.getElementById(id).classList.add('nx-modal--open');
        },
        
        closeModal: function(id) {
            document.getElementById(id).classList.remove('nx-modal--open');
        },
        
        editKb: function(id) {
            var kb = this.knowledgeBases.find(function(k) { return k.id === id; });
            if (kb) {
                document.getElementById('newKbName').value = kb.name;
                document.getElementById('newKbDesc').value = kb.description || '';
                this.openModal('createKbModal');
            }
        },
        
        deleteKb: function(id) {
            var self = this;
            if (confirm('确定要删除此知识库吗？')) {
                NexusAPI.delete('/api/kb/' + id, function(response) {
                    if (response.status === 'success') {
                        self.showToast('知识库已删除', 'success');
                        self.loadKnowledgeBases();
                    } else {
                        self.showToast('删除失败: ' + (response.message || '未知错误'), 'error');
                    }
                });
            }
        },
        
        viewDoc: function(id) {
            this.showToast('查看文档: ' + id, 'info');
        },
        
        deleteDoc: function(id) {
            var self = this;
            if (confirm('确定要删除此文档吗？')) {
                NexusAPI.delete('/api/kb/docs/' + id, function(response) {
                    if (response.status === 'success') {
                        self.showToast('文档已删除', 'success');
                        self.loadDocuments();
                    } else {
                        self.showToast('删除失败: ' + (response.message || '未知错误'), 'error');
                    }
                });
            }
        },
        
        bindEvents: function() {
            var self = this;
            
            document.getElementById('createKbBtn').addEventListener('click', function() {
                document.getElementById('newKbName').value = '';
                document.getElementById('newKbDesc').value = '';
                self.openModal('createKbModal');
            });
            
            document.getElementById('confirmCreateKb').addEventListener('click', function() {
                var name = document.getElementById('newKbName').value.trim();
                var desc = document.getElementById('newKbDesc').value.trim();
                if (!name) {
                    self.showToast('请输入知识库名称', 'error');
                    return;
                }
                NexusAPI.post('/api/kb/create', { name: name, description: desc }, function(response) {
                    if (response.status === 'success') {
                        self.showToast('知识库创建成功', 'success');
                        self.closeModal('createKbModal');
                        self.loadKnowledgeBases();
                    } else {
                        self.showToast('创建失败: ' + (response.message || '未知错误'), 'error');
                    }
                });
            });
            
            document.getElementById('uploadDocBtn').addEventListener('click', function() {
                self.selectedFiles = [];
                document.getElementById('fileList').innerHTML = '';
                self.openModal('uploadDocModal');
            });
            
            document.getElementById('fileUploadArea').addEventListener('click', function() {
                document.getElementById('fileInput').click();
            });
            
            document.getElementById('fileInput').addEventListener('change', function(e) {
                self.selectedFiles = Array.from(e.target.files);
                var fileList = document.getElementById('fileList');
                fileList.innerHTML = self.selectedFiles.map(function(f) {
                    return '<div class="doc-item"><div class="doc-icon"><i class="ri-file-line"></i></div><div class="doc-info"><h4>' + f.name + '</h4><p>' + (f.size / 1024).toFixed(1) + ' KB</p></div></div>';
                }).join('');
            });
            
            document.getElementById('confirmUpload').addEventListener('click', function() {
                var kbId = document.getElementById('uploadKbSelect').value;
                if (!kbId) {
                    self.showToast('请选择知识库', 'error');
                    return;
                }
                if (!self.selectedFiles.length) {
                    self.showToast('请选择要上传的文件', 'error');
                    return;
                }
                
                self.showToast('文件上传中...', 'info');
                
                var uploadPromises = self.selectedFiles.map(function(file) {
                    return new Promise(function(resolve, reject) {
                        var formData = new FormData();
                        formData.append('file', file);
                        formData.append('kbId', kbId);
                        
                        NexusAPI.upload('/api/kb/upload', formData, function(response) {
                            resolve(response);
                        });
                    });
                });
                
                Promise.all(uploadPromises).then(function(results) {
                    var successCount = results.filter(function(r) { return r.status === 'success'; }).length;
                    var failCount = results.length - successCount;
                    
                    if (failCount === 0) {
                        self.showToast('所有文件上传成功 (' + successCount + '个)', 'success');
                    } else {
                        self.showToast('上传完成: 成功' + successCount + '个, 失败' + failCount + '个', 'warning');
                    }
                    
                    self.closeModal('uploadDocModal');
                    self.loadDocuments();
                    self.loadKnowledgeBases();
                }).catch(function(error) {
                    self.showToast('上传失败: ' + error.message, 'error');
                });
            });
            
            document.getElementById('createFolderBtn').addEventListener('click', function() {
                var name = prompt('请输入文件夹名称:');
                if (name) {
                    self.showToast('文件夹 "' + name + '" 创建成功', 'success');
                }
            });
            
            document.getElementById('settingsBtn').addEventListener('click', function() {
                document.getElementById('defaultTopK').value = self.topK;
                document.getElementById('defaultThreshold').value = self.threshold * 100;
                self.openModal('settingsModal');
            });
            
            document.getElementById('saveSettings').addEventListener('click', function() {
                self.topK = parseInt(document.getElementById('defaultTopK').value) || 10;
                self.threshold = (parseInt(document.getElementById('defaultThreshold').value) || 70) / 100;
                self.closeModal('settingsModal');
                self.showToast('设置已保存', 'success');
            });
            
            document.getElementById('kbSearchBtn').addEventListener('click', function() {
                var query = document.getElementById('kbSearchInput').value.trim().toLowerCase();
                var filtered = self.knowledgeBases.filter(function(kb) {
                    return kb.name.toLowerCase().includes(query) || (kb.description || '').toLowerCase().includes(query);
                });
                self.renderKbGrid(filtered);
            });
            
            document.getElementById('kbSearchInput').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') document.getElementById('kbSearchBtn').click();
            });
            
            document.getElementById('docSearchBtn').addEventListener('click', function() {
                self.showToast('文档搜索功能', 'info');
            });
            
            document.getElementById('docSearchInput').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') document.getElementById('docSearchBtn').click();
            });
            
            document.getElementById('searchBtn').addEventListener('click', function() {
                self.doSearch();
            });
            document.getElementById('searchInput').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') self.doSearch();
            });
            
            document.getElementById('sendBtn').addEventListener('click', function() {
                self.sendQuestion();
            });
            document.getElementById('questionInput').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') self.sendQuestion();
            });
            
            document.getElementById('topKSlider').addEventListener('input', function() {
                self.topK = parseInt(this.value);
                document.getElementById('topKValue').textContent = self.topK;
            });
            
            document.getElementById('thresholdSlider').addEventListener('input', function() {
                self.threshold = parseInt(this.value) / 100;
                document.getElementById('thresholdValue').textContent = this.value + '%';
            });
            
            document.getElementById('modelSelect').addEventListener('change', function() {
                self.currentModel = this.value;
                self.updateModelStatus();
                self.showToast('已切换模型: ' + this.options[this.selectedIndex].text, 'success');
            });
        }
    };
    
    window.KnowledgeQA = KnowledgeQA;
    
    window.openModal = function(id) {
        KnowledgeQA.openModal(id);
    };
    
    window.closeModal = function(id) {
        KnowledgeQA.closeModal(id);
    };
    
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            KnowledgeQA.init();
        });
    } else {
        KnowledgeQA.init();
    }
})();
