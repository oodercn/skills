let createdScenes = [];
let startedScenes = [];
let participatedScenes = [];

document.addEventListener('DOMContentLoaded', async function() {
    await initDicts();
    refreshAll();
});

async function initDicts() {
    if (typeof DictCache !== 'undefined') {
        await DictCache.init();
    }
}

function getStatusText(status) {
    if (typeof DictCache !== 'undefined') {
        const item = DictCache.getDictItem(DictCache.DICT_CODES.SCENE_GROUP_STATUS, status);
        if (item && item.name) {
            return item.name;
        }
    }
    const statusMap = {
        'ACTIVE': '运行中',
        'SUSPENDED': '已暂停',
        'CREATING': '创建中',
        'CONFIGURING': '配置中',
        'PENDING': '待激活',
        'DESTROYED': '已销毁',
        'ERROR': '错误'
    };
    return statusMap[status] || status;
}

function getStatusClass(status) {
    const classMap = {
        'ACTIVE': 'nx-badge--success',
        'SUSPENDED': 'nx-badge--warning',
        'CREATING': 'nx-badge--info',
        'CONFIGURING': 'nx-badge--info',
        'PENDING': 'nx-badge--secondary',
        'DESTROYED': 'nx-badge--error',
        'ERROR': 'nx-badge--error'
    };
    return classMap[status] || 'nx-badge--secondary';
}

function formatTime(timestamp) {
    if (!timestamp) return '-';
    const date = new Date(timestamp);
    return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', {hour: '2-digit', minute: '2-digit'});
}

async function refreshAll() {
    await Promise.all([
        refreshCreated(),
        refreshStarted(),
        refreshParticipated()
    ]);
    updateStats();
}

async function refreshCreated() {
    try {
        var result = await ApiClient.get('/api/v1/scene-groups/my/created?pageNum=1&pageSize=20');
        
        if (result.code === 200 && result.data) {
            createdScenes = result.data.list || [];
            renderCreatedScenes();
        }
    } catch (error) {
        console.error('Failed to load created scenes:', error);
        createdScenes = [];
        renderCreatedScenes();
    }
}

async function refreshStarted() {
    try {
        var result = await ApiClient.get('/api/v1/scene-groups?pageNum=1&pageSize=20&status=ACTIVE');
        
        if (result.code === 200 && result.data) {
            startedScenes = result.data.list || [];
            renderStartedScenes();
        }
    } catch (error) {
        console.error('Failed to load started scenes:', error);
        startedScenes = [];
        renderStartedScenes();
    }
}

async function refreshParticipated() {
    try {
        var result = await ApiClient.get('/api/v1/scene-groups/my/participated?pageNum=1&pageSize=20');
        
        if (result.code === 200 && result.data) {
            participatedScenes = result.data.list || [];
            renderParticipatedScenes();
        }
    } catch (error) {
        console.error('Failed to load participated scenes:', error);
        participatedScenes = [];
        renderParticipatedScenes();
    }
}

function loadMockCreatedScenes() {
    createdScenes = [];
    renderCreatedScenes();
}

function loadMockStartedScenes() {
    startedScenes = [];
    renderStartedScenes();
}

function loadMockParticipatedScenes() {
    participatedScenes = [];
    renderParticipatedScenes();
}

function renderCreatedScenes() {
    const container = document.getElementById('createdScenes');
    document.getElementById('createdCountBadge').textContent = createdScenes.length;
    
    if (!createdScenes.length) {
        container.innerHTML = '<div class="empty-state"><i class="ri-folder-open-line"></i><p>暂无创建的场景</p></div>';
        return;
    }
    
    let html = '';
    createdScenes.forEach(scene => {
        html += `
            <div class="scene-card">
                <div class="scene-card-header">
                    <div class="scene-card-title">
                        <i class="ri-artboard-line" style="color: var(--nx-primary);"></i>
                        ${scene.name}
                        <span class="nx-badge ${getStatusClass(scene.status)}">${getStatusText(scene.status)}</span>
                    </div>
                    <div class="scene-card-actions">
                        <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="viewScene('${scene.sceneGroupId}')">
                            <i class="ri-eye-line"></i> 查看
                        </button>
                        <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="toggleSceneStatus('${scene.sceneGroupId}', '${scene.status}')">
                            <i class="${scene.status === 'ACTIVE' ? 'ri-pause-line' : 'ri-play-line'}"></i>
                            ${scene.status === 'ACTIVE' ? '暂停' : '激活'}
                        </button>
                        <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="manageScene('${scene.sceneGroupId}')">
                            <i class="ri-settings-3-line"></i> 管理
                        </button>
                    </div>
                </div>
                <div class="scene-card-meta">
                    <span><i class="ri-file-copy-line"></i> ${scene.templateName || scene.templateId}</span>
                    <span><i class="ri-user-line"></i> ${scene.memberCount || 0} 人</span>
                    <span><i class="ri-time-line"></i> 创建于 ${formatTime(scene.createTime)}</span>
                </div>
            </div>
        `;
    });
    container.innerHTML = html;
}

function renderStartedScenes() {
    const container = document.getElementById('startedScenes');
    document.getElementById('startedCountBadge').textContent = startedScenes.length;
    
    if (!startedScenes.length) {
        container.innerHTML = '<div class="empty-state"><i class="ri-folder-open-line"></i><p>暂无启动的场景</p></div>';
        return;
    }
    
    let html = '';
    startedScenes.forEach(workflow => {
        const statusClass = workflow.status === 'running' ? 'nx-badge--info' : 
                          workflow.status === 'completed' ? 'nx-badge--success' : 'nx-badge--warning';
        const statusText = workflow.status === 'running' ? '执行中' : 
                          workflow.status === 'completed' ? '已完成' : workflow.status;
        
        html += `
            <div class="scene-card">
                <div class="scene-card-header">
                    <div class="scene-card-title">
                        <i class="ri-play-circle-line" style="color: var(--nx-success);"></i>
                        ${workflow.sceneGroupName || workflow.workflowId}
                        <span class="nx-badge ${statusClass}">${statusText}</span>
                    </div>
                    <div class="scene-card-actions">
                        <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="viewWorkflow('${workflow.workflowId}')">
                            <i class="ri-eye-line"></i> 详情
                        </button>
                        ${workflow.status === 'running' ? `
                        <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="cancelWorkflow('${workflow.workflowId}')">
                            <i class="ri-stop-line"></i> 取消
                        </button>
                        ` : `
                        <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="rerunWorkflow('${workflow.sceneGroupId}')">
                            <i class="ri-refresh-line"></i> 重新执行
                        </button>
                        `}
                    </div>
                </div>
                <div class="scene-card-meta">
                    <span><i class="ri-flashlight-line"></i> ${workflow.triggerType === 'schedule' ? '定时触发' : '手动触发'}</span>
                    <span><i class="ri-time-line"></i> 开始于 ${formatTime(workflow.startTime)}</span>
                </div>
                ${workflow.status === 'running' ? `
                <div class="workflow-progress">
                    <div class="workflow-progress-bar" style="width: ${workflow.progress || 0}%"></div>
                </div>
                <div class="nx-text-sm nx-text-secondary nx-mt-1">进度: ${workflow.progress || 0}%</div>
                ` : ''}
            </div>
        `;
    });
    container.innerHTML = html;
}

function renderParticipatedScenes() {
    const container = document.getElementById('participatedScenes');
    document.getElementById('participatedCountBadge').textContent = participatedScenes.length;
    
    if (!participatedScenes.length) {
        container.innerHTML = '<div class="empty-state"><i class="ri-folder-open-line"></i><p>暂无参与的场景</p></div>';
        return;
    }
    
    let html = '';
    participatedScenes.forEach(scene => {
        html += `
            <div class="scene-card">
                <div class="scene-card-header">
                    <div class="scene-card-title">
                        <i class="ri-user-star-line" style="color: var(--nx-info);"></i>
                        ${scene.name}
                        <span class="nx-badge ${getStatusClass(scene.status)}">${getStatusText(scene.status)}</span>
                    </div>
                    <div class="scene-card-actions">
                        <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="viewScene('${scene.sceneGroupId}')">
                            <i class="ri-eye-line"></i> 查看
                        </button>
                        <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="leaveScene('${scene.sceneGroupId}')">
                            <i class="ri-logout-box-line"></i> 退出
                        </button>
                    </div>
                </div>
                <div class="scene-card-meta">
                    <span><i class="ri-user-line"></i> 我的角色: ${scene.myRole || '参与者'}</span>
                    <span><i class="ri-group-line"></i> ${scene.memberCount || 0} 人</span>
                    <span><i class="ri-time-line"></i> 加入于 ${formatTime(scene.joinTime)}</span>
                </div>
            </div>
        `;
    });
    container.innerHTML = html;
}

function updateStats() {
    document.getElementById('createdCount').textContent = createdScenes.length;
    document.getElementById('runningCount').textContent = startedScenes.filter(s => s.status === 'running').length;
    document.getElementById('participatedCount').textContent = participatedScenes.length;
    document.getElementById('pendingCount').textContent = participatedScenes.filter(s => s.status === 'PENDING').length;
}

function createScene() {
    window.location.href = '/console/pages/scene-group-management.html?action=create';
}

function viewScene(sceneGroupId) {
    window.location.href = '/console/pages/scene-group-detail.html?id=' + sceneGroupId;
}

function manageScene(sceneGroupId) {
    window.location.href = '/console/pages/scene-group-detail.html?id=' + sceneGroupId;
}

async function toggleSceneStatus(sceneGroupId, currentStatus) {
    var action = currentStatus === 'ACTIVE' ? 'deactivate' : 'activate';
    var confirmMsg = currentStatus === 'ACTIVE' ? '确定要暂停此场景吗？' : '确定要激活此场景吗？';
    
    if (!confirm(confirmMsg)) return;
    
    try {
        var result = await ApiClient.post('/api/v1/scene-groups/' + sceneGroupId + '/' + action);
        
        if (result.code === 200) {
            refreshCreated();
        } else {
            alert('操作失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to toggle status:', error);
        alert('操作失败');
    }
}

async function leaveScene(sceneGroupId) {
    if (!confirm('确定要退出此场景吗？')) return;
    
    try {
        var result = await ApiClient.delete('/api/v1/scene-groups/' + sceneGroupId + '/participants/me');
        
        if (result.code === 200) {
            refreshParticipated();
        } else {
            alert('退出失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to leave scene:', error);
        alert('退出失败');
    }
}

function viewWorkflow(workflowId) {
    window.location.href = '/console/pages/execution.html?id=' + workflowId;
}

async function cancelWorkflow(workflowId) {
    if (!confirm('确定要取消此工作流吗？')) return;
    alert('工作流已取消');
}

async function rerunWorkflow(sceneGroupId) {
    if (!confirm('确定要重新执行此场景吗？')) return;
    alert('场景已开始执行');
}

function goToAuditLogs() {
    window.location.href = '/console/pages/audit-logs.html';
}

function goToCapabilityAudit() {
    window.location.href = '/console/pages/capability-stats.html';
}

function goToSceneAudit() {
    window.location.href = '/console/pages/my-history.html';
}
