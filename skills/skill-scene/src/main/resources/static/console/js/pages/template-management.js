let templates = [];
let currentPage = 1;
let pageSize = 10;

document.addEventListener('DOMContentLoaded', function() {
    refreshTemplates();
    initLlmAssistant();
});

function initLlmAssistant() {
    if (typeof LlmAssistant !== 'undefined') {
        LlmAssistant.init();
    }
}

async function refreshTemplates() {
    try {
        const result = await ApiClient.get('/api/v1/scene-templates?pageNum=' + currentPage + '&pageSize=' + pageSize);
        
        if (result.code === 200 && result.data) {
            templates = result.data.list || [];
            renderTemplateTable();
            updateStats();
        }
    } catch (error) {
        console.error('Failed to load templates:', error);
        loadMockData();
    }
}

function loadMockData() {
    templates = [
        {
            templateId: 'tpl-enterprise-standard',
            name: '企业标准场景能力',
            category: 'enterprise',
            version: '1.0.0',
            status: 'published',
            capabilityCount: 5,
            createTime: Date.now() - 86400000 * 7
        },
        {
            templateId: 'tpl-personal-basic',
            name: '个人基础场景能力',
            category: 'personal',
            version: '1.0.0',
            status: 'published',
            capabilityCount: 3,
            createTime: Date.now() - 86400000 * 3
        },
        {
            templateId: 'tpl-test-environment',
            name: '测试环境场景能力',
            category: 'test',
            version: '0.9.0',
            status: 'draft',
            capabilityCount: 2,
            createTime: Date.now() - 86400000
        }
    ];
    renderTemplateTable();
    updateStats();
}

function renderTemplateTable() {
    const tbody = document.getElementById('templateTableBody');
    tbody.innerHTML = '';
    
    templates.forEach(template => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>
                <div class="nx-flex nx-items-center nx-gap-2">
                    <i class="ri-file-copy-line nx-text-primary"></i>
                    <span>${template.name}</span>
                </div>
            </td>
            <td><span class="nx-badge nx-badge--secondary">${getCategoryName(template.category)}</span></td>
            <td><code>${template.version}</code></td>
            <td><span class="nx-badge ${template.status === 'published' ? 'nx-badge--success' : 'nx-badge--warning'}">${template.status === 'published' ? '已发布' : '草稿'}</span></td>
            <td>${template.capabilityCount || 0}</td>
            <td>${formatTime(template.createTime)}</td>
            <td>
                <div class="nx-flex nx-gap-2">
                    <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="viewTemplate('${template.templateId}')" title="查看">
                        <i class="ri-eye-line"></i>
                    </button>
                    <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="editTemplate('${template.templateId}')" title="编辑">
                        <i class="ri-edit-line"></i>
                    </button>
                    <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="deleteTemplate('${template.templateId}')" title="删除">
                        <i class="ri-delete-bin-line"></i>
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function updateStats() {
    document.getElementById('totalTemplates').textContent = templates.length;
    document.getElementById('publishedTemplates').textContent = templates.filter(t => t.status === 'published').length;
    document.getElementById('draftTemplates').textContent = templates.filter(t => t.status === 'draft').length;
    const categories = new Set(templates.map(t => t.category));
    document.getElementById('categoryCount').textContent = categories.size;
}

function getCategoryName(category) {
    const names = {
        'enterprise': '企业网络',
        'personal': '个人网络',
        'test': '测试网络',
        'development': '开发环境'
    };
    return names[category] || category;
}

function formatTime(timestamp) {
    if (!timestamp) return '-';
    const date = new Date(timestamp);
    return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', {hour: '2-digit', minute: '2-digit'});
}

function createTemplate() {
    document.getElementById('modalTitle').textContent = '创建模板';
    document.getElementById('templateForm').reset();
    document.getElementById('templateModal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('templateModal').style.display = 'none';
}

async function saveTemplate() {
    const template = {
        templateId: document.getElementById('templateId').value || 'tpl-' + Date.now(),
        name: document.getElementById('templateName').value,
        category: document.getElementById('templateCategory').value,
        version: document.getElementById('templateVersion').value,
        description: document.getElementById('templateDescription').value,
        status: 'draft'
    };
    
    try {
        const result = await ApiClient.post('/api/v1/scene-templates', template);
        
        if (result.code === 200) {
            closeModal();
            refreshTemplates();
        } else {
            alert('保存失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to save template:', error);
        alert('保存失败');
    }
}

function viewTemplate(templateId) {
    window.location.href = 'template-detail.html?id=' + templateId;
}

function editTemplate(templateId) {
    window.location.href = 'template-detail.html?id=' + templateId + '&edit=true';
}

async function deleteTemplate(templateId) {
    if (!confirm('确定要删除此模板吗？')) return;
    
    try {
        const result = await ApiClient.delete('/api/v1/scene-templates/' + templateId);
        
        if (result.code === 200) {
            refreshTemplates();
        } else {
            alert('删除失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to delete template:', error);
        alert('删除失败');
    }
}

async function filterByCategory() {
    const category = document.getElementById('categoryFilter').value;
    if (category) {
        const result = await ApiClient.get('/api/v1/scene-templates?pageNum=1&pageSize=10&category=' + category);
        if (result.code === 200 && result.data) {
            templates = result.data.list || [];
            renderTemplateTable();
        }
    } else {
        refreshTemplates();
    }
}
