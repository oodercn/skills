let sceneGroups = [];
let templates = [];
let currentPage = 1;
let pageSize = 10;

document.addEventListener('DOMContentLoaded', function() {
    refreshSceneGroups();
    loadTemplates();
    initLlmAssistant();
});

function initLlmAssistant() {
    if (typeof LlmAssistant !== 'undefined') {
        LlmAssistant.init();
    }
}

async function loadTemplates() {
    try {
        const result = await ApiClient.get('/api/v1/scene-templates?pageNum=1&pageSize=100');
        
        if (result.code === 200 && result.data) {
            templates = result.data.list || [];
            populateTemplateSelects();
        }
    } catch (error) {
        console.error('Failed to load templates:', error);
        templates = [
            {templateId: 'tpl-enterprise-standard', name: '企业标准模板'},
            {templateId: 'tpl-personal-basic', name: '个人基础模板'}
        ];
        populateTemplateSelects();
    }
}

function populateTemplateSelects() {
    const filterSelect = document.getElementById('templateFilter');
    const formSelect = document.getElementById('groupTemplate');
    
    templates.forEach(t => {
        const opt1 = document.createElement('option');
        opt1.value = t.templateId;
        opt1.textContent = t.name;
        filterSelect.appendChild(opt1);
        
        const opt2 = document.createElement('option');
        opt2.value = t.templateId;
        opt2.textContent = t.name;
        formSelect.appendChild(opt2);
    });
}

async function refreshSceneGroups() {
    try {
        const result = await ApiClient.get('/api/v1/scene-groups?pageNum=' + currentPage + '&pageSize=' + pageSize);
        
        if (result.code === 200 && result.data) {
            sceneGroups = result.data.list || [];
            renderSceneGroupTable();
            updateStats();
        }
    } catch (error) {
        console.error('Failed to load scene groups:', error);
        loadMockData();
    }
}

function loadMockData() {
    sceneGroups = [
        {
            sceneGroupId: 'sg-project-alpha',
            name: '项目Alpha协作组',
            templateId: 'tpl-enterprise-standard',
            templateName: '企业标准模板',
            status: 'ACTIVE',
            memberCount: 5,
            creatorId: 'user-001',
            createTime: Date.now() - 86400000 * 5
        },
        {
            sceneGroupId: 'sg-dev-team',
            name: '开发团队组',
            templateId: 'tpl-enterprise-standard',
            templateName: '企业标准模板',
            status: 'ACTIVE',
            memberCount: 8,
            creatorId: 'user-002',
            createTime: Date.now() - 86400000 * 3
        },
        {
            sceneGroupId: 'sg-test-env',
            name: '测试环境组',
            templateId: 'tpl-test-environment',
            templateName: '测试环境模板',
            status: 'SUSPENDED',
            memberCount: 2,
            creatorId: 'user-001',
            createTime: Date.now() - 86400000
        }
    ];
    renderSceneGroupTable();
    updateStats();
}

function renderSceneGroupTable() {
    const tbody = document.getElementById('sceneGroupTableBody');
    tbody.innerHTML = '';
    
    sceneGroups.forEach(group => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>
                <div class="nx-flex nx-items-center nx-gap-2">
                    <i class="ri-group-line nx-text-primary"></i>
                    <span>${group.name}</span>
                </div>
            </td>
            <td><span class="nx-badge nx-badge--secondary">${group.templateName || group.templateId}</span></td>
            <td><span class="nx-badge ${getStatusBadgeClass(group.status)}">${getStatusText(group.status)}</span></td>
            <td>${group.memberCount || 0}</td>
            <td>${group.creatorId}</td>
            <td>${formatTime(group.createTime)}</td>
            <td>
                <div class="nx-flex nx-gap-2">
                    <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="viewSceneGroup('${group.sceneGroupId}')" title="查看">
                        <i class="ri-eye-line"></i>
                    </button>
                    <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="toggleStatus('${group.sceneGroupId}', '${group.status}')" title="${group.status === 'ACTIVE' ? '停用' : '激活'}">
                        <i class="${group.status === 'ACTIVE' ? 'ri-pause-line' : 'ri-play-line'}"></i>
                    </button>
                    <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="deleteSceneGroup('${group.sceneGroupId}')" title="删除">
                        <i class="ri-delete-bin-line"></i>
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function getStatusBadgeClass(status) {
    switch(status) {
        case 'ACTIVE': return 'nx-badge--success';
        case 'SUSPENDED': return 'nx-badge--warning';
        case 'CREATING': return 'nx-badge--info';
        case 'DESTROYED': return 'nx-badge--error';
        default: return 'nx-badge--secondary';
    }
}

function getStatusText(status) {
    switch(status) {
        case 'ACTIVE': return '活跃';
        case 'SUSPENDED': return '已暂停';
        case 'CREATING': return '创建中';
        case 'DESTROYED': return '已销毁';
        default: return status;
    }
}

function updateStats() {
    document.getElementById('totalGroups').textContent = sceneGroups.length;
    document.getElementById('activeGroups').textContent = sceneGroups.filter(g => g.status === 'ACTIVE').length;
    document.getElementById('totalMembers').textContent = sceneGroups.reduce((sum, g) => sum + (g.memberCount || 0), 0);
    document.getElementById('totalBindings').textContent = sceneGroups.length * 3;
}

function formatTime(timestamp) {
    if (!timestamp) return '-';
    const date = new Date(timestamp);
    return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', {hour: '2-digit', minute: '2-digit'});
}

function createSceneGroup() {
    document.getElementById('modalTitle').textContent = '创建场景组';
    document.getElementById('sceneGroupForm').reset();
    document.getElementById('sceneGroupModal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('sceneGroupModal').style.display = 'none';
}

async function saveSceneGroup() {
    const config = {
        name: document.getElementById('groupName').value,
        description: document.getElementById('groupDescription').value,
        minMembers: parseInt(document.getElementById('minMembers').value) || 1,
        maxMembers: parseInt(document.getElementById('maxMembers').value) || 100,
        creatorId: 'current-user',
        creatorType: 'USER'
    };
    
    const request = {
        templateId: document.getElementById('groupTemplate').value,
        config: config
    };
    
    try {
        const result = await ApiClient.post('/api/v1/scene-groups', request);
        
        if (result.code === 200) {
            closeModal();
            refreshSceneGroups();
        } else {
            alert('保存失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to save scene group:', error);
        alert('保存失败');
    }
}

function viewSceneGroup(sceneGroupId) {
    window.location.href = 'scene-group-detail.html?id=' + sceneGroupId;
}

async function toggleStatus(sceneGroupId, currentStatus) {
    const action = currentStatus === 'ACTIVE' ? 'deactivate' : 'activate';
    const confirmMsg = currentStatus === 'ACTIVE' ? '确定要停用此场景组吗？' : '确定要激活此场景组吗？';
    
    if (!confirm(confirmMsg)) return;
    
    try {
        const result = await ApiClient.post('/api/v1/scene-groups/' + sceneGroupId + '/' + action);
        
        if (result.code === 200) {
            refreshSceneGroups();
        } else {
            alert('操作失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to toggle status:', error);
        alert('操作失败');
    }
}

async function deleteSceneGroup(sceneGroupId) {
    if (!confirm('确定要删除此场景组吗？此操作不可恢复。')) return;
    
    try {
        const result = await ApiClient.delete('/api/v1/scene-groups/' + sceneGroupId);
        
        if (result.code === 200) {
            refreshSceneGroups();
        } else {
            alert('删除失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to delete scene group:', error);
        alert('删除失败');
    }
}

async function filterByTemplate() {
    const templateId = document.getElementById('templateFilter').value;
    if (templateId) {
        const result = await ApiClient.get('/api/v1/scene-groups?pageNum=1&pageSize=10&templateId=' + templateId);
        if (result.code === 200 && result.data) {
            sceneGroups = result.data.list || [];
            renderSceneGroupTable();
        }
    } else {
        refreshSceneGroups();
    }
}
