const KnowledgeUI = {
    knowledgeBases: [],
    currentKb: null,
    documents: [],
    searchCount: 0,
    
    async init() {
        await this.loadKnowledgeBases();
    },
    
    async loadKnowledgeBases() {
        try {
            const response = await NexusApi.get('/api/v1/kb/public');
            if (response.status === 'success' && response.data) {
                this.knowledgeBases = response.data;
                this.renderKbList();
                this.updateStats();
            }
        } catch (error) {
            console.error('Failed to load knowledge bases:', error);
        }
    },
    
    renderKbList() {
        const list = document.getElementById('kb-list');
        if (!list) return;
        
        if (!this.knowledgeBases.length) {
            list.innerHTML = `
                <li class="kb-list-item kb-list-item--empty">
                    <i class="ri-inbox-line"></i>
                    <span>暂无知识库</span>
                </li>
            `;
            return;
        }
        
        list.innerHTML = '';
        
        this.knowledgeBases.forEach(kb => {
            const li = document.createElement('li');
            li.className = 'kb-list-item';
            li.onclick = () => this.selectKb(kb.id);
            
            li.innerHTML = `
                <div class="kb-list-item__name">${kb.name}</div>
                <div class="kb-list-item__meta">
                    <span><i class="ri-file-text-line"></i> ${kb.docCount || 0} 文档</span>
                    <span><i class="ri-text-wrap"></i> ${kb.chunkCount || 0} 分块</span>
                </div>
            `;
            
            list.appendChild(li);
        });
    },
    
    async selectKb(id) {
        const items = document.querySelectorAll('.kb-list-item');
        items.forEach(item => item.classList.remove('kb-list-item--active'));
        
        event.currentTarget.classList.add('kb-list-item--active');
        
        try {
            const response = await NexusApi.get(`/api/v1/kb/${id}`);
            if (response.status === 'success' && response.data) {
                this.currentKb = response.data;
                this.showKbDetail();
                await this.loadDocuments(id);
            }
        } catch (error) {
            console.error('Failed to load knowledge base:', error);
        }
    },
    
    showKbDetail() {
        document.getElementById('kb-empty-card').style.display = 'none';
        document.getElementById('kb-detail-card').style.display = 'block';
        document.getElementById('kb-documents-card').style.display = 'block';
        
        document.getElementById('kb-detail-name').textContent = this.currentKb.name;
        document.getElementById('kb-doc-count').textContent = this.currentKb.docCount || 0;
        document.getElementById('kb-chunk-count').textContent = this.currentKb.chunkCount || 0;
        document.getElementById('kb-type').textContent = this.getTypeDisplayName(this.currentKb.type);
        document.getElementById('kb-status').textContent = this.currentKb.status || 'ACTIVE';
        document.getElementById('kb-description').textContent = this.currentKb.description || '暂无描述';
    },
    
    getTypeDisplayName(type) {
        const names = {
            'GENERAL': '通用',
            'PRODUCT': '产品',
            'TECHNICAL': '技术',
            'FAQ': 'FAQ'
        };
        return names[type] || type || '通用';
    },
    
    async loadDocuments(kbId) {
        try {
            const response = await NexusApi.get(`/api/v1/kb/${kbId}/documents`);
            if (response.status === 'success' && response.data) {
                this.documents = response.data;
                this.renderDocuments();
            }
        } catch (error) {
            console.error('Failed to load documents:', error);
        }
    },
    
    renderDocuments() {
        const tbody = document.getElementById('documents-table-body');
        if (!tbody) return;
        
        if (!this.documents.length) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="nx-text-center nx-text-secondary nx-py-4">
                        暂无文档
                    </td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = '';
        
        this.documents.forEach(doc => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${doc.title || '-'}</td>
                <td>${doc.chunkCount || 0}</td>
                <td>
                    <span class="config-status config-status--active">
                        ${doc.status || '已索引'}
                    </span>
                </td>
                <td>${this.formatTime(doc.createTime)}</td>
                <td>
                    <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="KnowledgeUI.deleteDocument('${doc.id}')" title="删除">
                        <i class="ri-delete-bin-line"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    },
    
    formatTime(timestamp) {
        if (!timestamp) return '-';
        const date = new Date(timestamp);
        return date.toLocaleDateString('zh-CN');
    },
    
    updateStats() {
        const kbCount = this.knowledgeBases.length;
        const docCount = this.knowledgeBases.reduce((sum, kb) => sum + (kb.docCount || 0), 0);
        const chunkCount = this.knowledgeBases.reduce((sum, kb) => sum + (kb.chunkCount || 0), 0);
        
        document.getElementById('stat-kb-count').textContent = kbCount;
        document.getElementById('stat-doc-count').textContent = docCount;
        document.getElementById('stat-chunk-count').textContent = chunkCount;
        document.getElementById('stat-search-count').textContent = this.searchCount;
    },
    
    openCreateKbModal() {
        document.getElementById('kb-modal-title').textContent = '新建知识库';
        document.getElementById('kb-form').reset();
        document.getElementById('kb-id').value = '';
        
        document.getElementById('kb-modal').classList.add('nx-modal--open');
    },
    
    editKb() {
        if (!this.currentKb) return;
        
        document.getElementById('kb-modal-title').textContent = '编辑知识库';
        document.getElementById('kb-id').value = this.currentKb.id;
        document.getElementById('kb-name').value = this.currentKb.name || '';
        document.getElementById('kb-type-input').value = this.currentKb.type || 'GENERAL';
        document.getElementById('kb-desc').value = this.currentKb.description || '';
        document.getElementById('kb-owner-id').value = this.currentKb.ownerId || '';
        document.getElementById('kb-public').checked = this.currentKb.visibility === 'PUBLIC';
        
        document.getElementById('kb-modal').classList.add('nx-modal--open');
    },
    
    closeKbModal() {
        document.getElementById('kb-modal').classList.remove('nx-modal--open');
    },
    
    async saveKb() {
        const id = document.getElementById('kb-id').value;
        const kb = {
            name: document.getElementById('kb-name').value,
            type: document.getElementById('kb-type-input').value,
            description: document.getElementById('kb-desc').value,
            ownerId: document.getElementById('kb-owner-id').value || 'default',
            visibility: document.getElementById('kb-public').checked ? 'PUBLIC' : 'PRIVATE'
        };
        
        if (!kb.name) {
            alert('请输入知识库名称');
            return;
        }
        
        try {
            let response;
            if (id) {
                response = await NexusApi.put(`/api/v1/kb/${id}`, kb);
            } else {
                response = await NexusApi.post('/api/v1/kb', kb);
            }
            
            if (response.status === 'success') {
                this.closeKbModal();
                await this.loadKnowledgeBases();
            } else {
                alert('保存失败: ' + (response.message || '未知错误'));
            }
        } catch (error) {
            console.error('Failed to save knowledge base:', error);
            alert('保存失败: ' + error.message);
        }
    },
    
    async deleteKb() {
        if (!this.currentKb) return;
        
        if (!confirm(`确定要删除知识库 "${this.currentKb.name}" 吗？此操作不可恢复。`)) {
            return;
        }
        
        try {
            const response = await NexusApi.delete(`/api/v1/kb/${this.currentKb.id}`);
            if (response.status === 'success') {
                this.currentKb = null;
                document.getElementById('kb-empty-card').style.display = 'block';
                document.getElementById('kb-detail-card').style.display = 'none';
                document.getElementById('kb-documents-card').style.display = 'none';
                await this.loadKnowledgeBases();
            } else {
                alert('删除失败: ' + (response.message || '未知错误'));
            }
        } catch (error) {
            console.error('Failed to delete knowledge base:', error);
            alert('删除失败: ' + error.message);
        }
    },
    
    openAddDocModal() {
        if (!this.currentKb) return;
        
        document.getElementById('doc-form') && document.getElementById('doc-form').reset();
        document.getElementById('doc-title').value = '';
        document.getElementById('doc-content').value = '';
        document.getElementById('doc-source').value = '';
        
        document.getElementById('doc-modal').classList.add('nx-modal--open');
    },
    
    closeDocModal() {
        document.getElementById('doc-modal').classList.remove('nx-modal--open');
    },
    
    async addDocument() {
        if (!this.currentKb) return;
        
        const doc = {
            title: document.getElementById('doc-title').value,
            content: document.getElementById('doc-content').value,
            source: document.getElementById('doc-source').value
        };
        
        if (!doc.title || !doc.content) {
            alert('请填写文档标题和内容');
            return;
        }
        
        try {
            const response = await NexusApi.post(`/api/v1/kb/${this.currentKb.id}/documents`, doc);
            if (response.status === 'success') {
                this.closeDocModal();
                await this.loadDocuments(this.currentKb.id);
                await this.loadKnowledgeBases();
            } else {
                alert('添加失败: ' + (response.message || '未知错误'));
            }
        } catch (error) {
            console.error('Failed to add document:', error);
            alert('添加失败: ' + error.message);
        }
    },
    
    async deleteDocument(docId) {
        if (!this.currentKb || !confirm('确定要删除此文档吗？')) return;
        
        try {
            const response = await NexusApi.delete(`/api/v1/kb/${this.currentKb.id}/documents/${docId}`);
            if (response.status === 'success') {
                await this.loadDocuments(this.currentKb.id);
                await this.loadKnowledgeBases();
            }
        } catch (error) {
            console.error('Failed to delete document:', error);
        }
    },
    
    handleSearchKeydown(event) {
        if (event.key === 'Enter') {
            this.search();
        }
    },
    
    async search() {
        if (!this.currentKb) return;
        
        const query = document.getElementById('search-input').value.trim();
        if (!query) return;
        
        try {
            const response = await NexusApi.post(`/api/v1/kb/${this.currentKb.id}/search`, {
                query: query,
                topK: 5
            });
            
            if (response.status === 'success' && response.data) {
                this.searchCount++;
                this.updateStats();
                this.renderSearchResults(response.data);
            }
        } catch (error) {
            console.error('Failed to search:', error);
        }
    },
    
    renderSearchResults(results) {
        const container = document.getElementById('search-results');
        const list = document.getElementById('search-results-list');
        
        container.style.display = 'block';
        
        if (!results.length) {
            list.innerHTML = '<p class="nx-text-secondary">没有找到相关内容</p>';
            return;
        }
        
        list.innerHTML = '';
        
        results.forEach(result => {
            const item = document.createElement('div');
            item.className = 'search-result-item';
            item.innerHTML = `
                <div class="search-result-item__title">${result.docTitle || result.title || '未知文档'}</div>
                <div class="search-result-item__content">${this.truncate(result.content || result.text, 200)}</div>
                <div class="search-result-item__score">
                    <i class="ri-checkbox-blank-circle-fill" style="font-size: 8px;"></i>
                    相关度: ${(result.score * 100).toFixed(1)}%
                </div>
            `;
            list.appendChild(item);
        });
    },
    
    truncate(text, maxLength) {
        if (!text) return '';
        if (text.length <= maxLength) return text;
        return text.substring(0, maxLength) + '...';
    }
};

window.KnowledgeUI = KnowledgeUI;
