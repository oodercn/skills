let currentGroup = null;
let sceneGroupId = null;
let availableCapabilities = [];
let allUsers = [];
let selectedParticipant = null;
let selectedCapability = null;
let currentDetailBinding = null;

document.addEventListener('DOMContentLoaded', async function() {
    sceneGroupId = getUrlParam('id');
    
    initTabs();
    await loadAllUsers();
    await loadCapabilities();
    
    if (sceneGroupId) {
        loadSceneGroup(sceneGroupId);
    } else {
        loadMockSceneGroup();
    }
});

function getUrlParam(name) {
    const params = new URLSearchParams(window.location.search);
    return params.get(name);
}

function initTabs() {
    const tabs = document.querySelectorAll('.nx-tabs__tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            tabs.forEach(t => t.classList.remove('nx-tabs__tab--active'));
            this.classList.add('nx-tabs__tab--active');
            
            const tabId = this.getAttribute('data-tab');
            document.querySelectorAll('.tab-content').forEach(content => {
                content.style.display = 'none';
            });
            document.getElementById('tab-' + tabId).style.display = 'block';
            
            if (tabId === 'knowledge') {
                loadKnowledgeBindings();
            } else if (tabId === 'llm') {
                loadLlmConfig();
            }
        });
    });
}

async function loadAllUsers() {
    try {
        const response = await fetch('/api/v1/org/users');
        const result = await response.json();
        
        if (result.status === 'success') {
            allUsers = result.data || [];
            console.log('Loaded users:', allUsers.length);
        } else {
            allUsers = getDefaultUsers();
        }
    } catch (error) {
        console.error('Failed to load users:', error);
        allUsers = getDefaultUsers();
    }
}

function getDefaultUsers() {
    return [
        { userId: 'user-manager-001', name: '张经理', role: 'manager', departmentId: 'dept-rd' },
        { userId: 'user-employee-001', name: '李员工', role: 'employee', departmentId: 'dept-rd' },
        { userId: 'user-employee-002', name: '王员工', role: 'employee', departmentId: 'dept-rd' },
        { userId: 'user-employee-003', name: '赵员工', role: 'employee', departmentId: 'dept-rd' }
    ];
}

async function loadCapabilities() {
    try {
        const response = await fetch('/api/v1/capabilities');
        const result = await response.json();
        
        if (result.status === 'success' && result.data) {
            availableCapabilities = result.data.list || result.data || [];
        } else if (Array.isArray(result)) {
            availableCapabilities = result;
        } else {
            availableCapabilities = getDefaultCapabilities();
        }
    } catch (error) {
        console.error('Failed to load capabilities:', error);
        availableCapabilities = getDefaultCapabilities();
    }
}

function getDefaultCapabilities() {
    return [
        { id: 'report-remind', name: '日志提醒', type: 'COMMUNICATION', description: '定时提醒员工提交日志' },
        { id: 'report-submit', name: '日志提交', type: 'SERVICE', description: '员工提交工作日志' },
        { id: 'report-aggregate', name: '日志汇总', type: 'SERVICE', description: '汇总所有员工日志' },
        { id: 'report-analyze', name: '日志分析', type: 'AI', description: 'AI分析日志内容' }
    ];
}

async function loadSceneGroup(id) {
    try {
        const response = await fetch('/api/v1/scene-groups/' + id);
        const result = await response.json();
        
        if (result.status === 'success' && result.data) {
            currentGroup = result.data;
            
            const snapshotsResponse = await fetch('/api/v1/scene-groups/' + id + '/snapshots');
            const snapshotsResult = await snapshotsResponse.json();
            if (snapshotsResult.status === 'success') {
                currentGroup.snapshots = snapshotsResult.data || [];
            }
            
            renderSceneGroup();
        } else {
            loadMockSceneGroup();
        }
    } catch (error) {
        console.error('Failed to load scene group:', error);
        loadMockSceneGroup();
    }
}

async function loadLogs() {
    if (!sceneGroupId) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/logs/recent?limit=50');
        const result = await response.json();
        
        if (result.status === 'success' && result.data) {
            renderLogList(result.data);
        } else {
            renderMockLogs();
        }
    } catch (error) {
        console.error('Failed to load logs:', error);
        renderMockLogs();
    }
}

function renderLogList(logs) {
    const container = document.getElementById('logList');
    if (!logs || logs.length === 0) {
        container.innerHTML = '<p class="nx-text-secondary">暂无日志</p>';
        return;
    }
    
    let html = '<div class="nx-flex nx-flex-col nx-gap-2">';
    logs.forEach(log => {
        const levelClass = log.status === 'ERROR' ? 'nx-badge--danger' : 
                          log.status === 'WARN' ? 'nx-badge--warning' : 
                          log.status === 'SUCCESS' ? 'nx-badge--success' : 'nx-badge--info';
        html += '<div class="nx-flex nx-items-start nx-gap-3 nx-p-2 nx-bg-elevated nx-rounded">' +
            '<span class="nx-text-sm nx-text-secondary" style="min-width: 80px;">' + formatTime(log.timestamp) + '</span>' +
            '<span class="nx-badge ' + levelClass + '">' + (log.status || 'INFO') + '</span>' +
            '<span class="nx-text-sm" style="min-width: 100px;">[' + (log.action || '-') + ']</span>' +
            '<span class="nx-text-sm">' + (log.message || '-') + '</span>' +
            '</div>';
    });
    html += '</div>';
    container.innerHTML = html;
}

function renderMockLogs() {
    const container = document.getElementById('logList');
    const logs = [
        { time: Date.now() - 60000, level: 'INFO', source: 'scene-manager', message: '场景状态更新: 活跃' },
        { time: Date.now() - 120000, level: 'INFO', source: 'capability-exec', message: '能力执行完成' },
        { time: Date.now() - 180000, level: 'WARN', source: 'session', message: '参与者心跳超时' }
    ];
    
    let html = '<div class="nx-flex nx-flex-col nx-gap-2">';
    logs.forEach(log => {
        const levelClass = log.level === 'ERROR' ? 'nx-badge--danger' : log.level === 'WARN' ? 'nx-badge--warning' : 'nx-badge--info';
        html += '<div class="nx-flex nx-items-start nx-gap-3 nx-p-2 nx-bg-elevated nx-rounded">' +
            '<span class="nx-text-sm nx-text-secondary" style="min-width: 80px;">' + formatTime(log.time) + '</span>' +
            '<span class="nx-badge ' + levelClass + '">' + log.level + '</span>' +
            '<span class="nx-text-sm" style="min-width: 100px;">[' + log.source + ']</span>' +
            '<span class="nx-text-sm">' + log.message + '</span>' +
            '</div>';
    });
    html += '</div>';
    container.innerHTML = html;
}

function loadMockSceneGroup() {
    currentGroup = {
        sceneGroupId: sceneGroupId || 'sg-new',
        templateId: 'tpl-daily-report',
        name: '新场景组',
        description: '场景组描述',
        status: 'ACTIVE',
        creatorId: 'user-admin',
        creatorType: 'USER',
        memberCount: 0,
        createTime: Date.now(),
        participants: [],
        capabilityBindings: [],
        snapshots: []
    };
    renderSceneGroup();
}

function renderSceneGroup() {
    document.getElementById('pageTitle').textContent = currentGroup.name;
    document.getElementById('groupName').textContent = currentGroup.name;
    document.getElementById('sceneGroupId').textContent = currentGroup.sceneGroupId;
    document.getElementById('groupTemplate').textContent = currentGroup.templateId || '-';
    document.getElementById('groupDescription').textContent = currentGroup.description || '-';
    document.getElementById('groupCreator').textContent = currentGroup.creatorId || '-';
    document.getElementById('groupCreateTime').textContent = formatTime(currentGroup.createTime);
    
    const statusBadge = document.getElementById('groupStatus');
    statusBadge.textContent = getStatusText(currentGroup.status);
    statusBadge.className = 'nx-badge ' + getStatusBadgeClass(currentGroup.status);
    
    const toggleBtn = document.getElementById('toggleStatusBtn');
    if (currentGroup.status === 'ACTIVE') {
        toggleBtn.innerHTML = '<i class="ri-pause-line"></i> <span>暂停</span>';
    } else {
        toggleBtn.innerHTML = '<i class="ri-play-line"></i> <span>激活</span>';
    }
    
    document.getElementById('memberCount').textContent = currentGroup.participants?.length || 0;
    document.getElementById('bindingCount').textContent = currentGroup.capabilityBindings?.length || 0;
    
    renderParticipantOverview();
    renderParticipants();
    renderCapabilityBindings();
    renderSnapshots();
    renderLogs();
}

function getStatusText(status) {
    const statusMap = { 'ACTIVE': '运行中', 'SUSPENDED': '已暂停', 'CREATING': '创建中', 'DESTROYED': '已销毁' };
    return statusMap[status] || status;
}

function getStatusBadgeClass(status) {
    const classMap = { 'ACTIVE': 'nx-badge--success', 'SUSPENDED': 'nx-badge--warning', 'CREATING': 'nx-badge--info' };
    return classMap[status] || 'nx-badge--secondary';
}

function formatTime(timestamp) {
    if (!timestamp) return '-';
    const date = new Date(timestamp);
    return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', {hour: '2-digit', minute: '2-digit'});
}

function renderParticipantOverview() {
    const container = document.getElementById('participantOverview');
    if (!currentGroup.participants?.length) {
        container.innerHTML = '<p class="nx-text-secondary">暂无参与者</p>';
        return;
    }
    
    const byRole = {};
    currentGroup.participants.forEach(p => {
        if (!byRole[p.role]) byRole[p.role] = [];
        byRole[p.role].push(p);
    });
    
    let html = '';
    Object.entries(byRole).forEach(([role, participants]) => {
        html += '<div class="nx-mb-3">' +
            '<div class="nx-text-sm nx-font-medium nx-mb-2">' + role + ' (' + participants.length + ')</div>' +
            '<div class="nx-flex nx-flex-wrap nx-gap-2">' +
            participants.map(p => '<span class="nx-badge nx-badge--secondary">' + (p.name || p.participantId) + '</span>').join('') +
            '</div></div>';
    });
    container.innerHTML = html;
}

function renderParticipants() {
    const container = document.getElementById('participantList');
    if (!currentGroup.participants?.length) {
        container.innerHTML = '<p class="nx-text-secondary">暂无参与者</p>';
        return;
    }
    
    const users = allUsers.length > 0 ? allUsers : getDefaultUsers();
    const userMap = {};
    users.forEach(u => { userMap[u.userId] = u; });
    
    let html = '';
    currentGroup.participants.forEach(p => {
        const avatarIcon = p.participantType === 'USER' ? 'ri-user-line' : 'ri-robot-line';
        const avatarBg = p.participantType === 'USER' ? 'var(--nx-primary-light)' : 'var(--nx-success-light)';
        
        const userInfo = userMap[p.participantId];
        const displayName = p.userName || p.name || (userInfo ? userInfo.name : p.participantId);
        
        html += '<div class="nx-card nx-mb-3">' +
            '<div class="nx-card__body">' +
            '<div class="nx-flex nx-items-center nx-gap-3">' +
            '<div class="nx-flex nx-items-center nx-justify-center" style="width: 40px; height: 40px; background: ' + avatarBg + '; border-radius: 50%;">' +
            '<i class="' + avatarIcon + '"></i></div>' +
            '<div class="nx-flex-1">' +
            '<div class="nx-flex nx-items-center nx-gap-2">' +
            '<span class="nx-font-medium">' + displayName + '</span>' +
            '<span class="nx-badge nx-badge--secondary" id="role-badge-' + p.participantId + '">' + p.role + '</span>' +
            '</div>' +
            '<div class="nx-text-sm nx-text-secondary">类型: ' + p.participantType + (p.participantId !== displayName ? ' | ID: ' + p.participantId : '') + '</div>' +
            '</div>' +
            '<button class="nx-btn nx-btn--ghost nx-btn--sm nx-mr-1" onclick="showChangeRoleModal(\'' + p.participantId + '\', \'' + p.role + '\')" title="变更角色">' +
            '<i class="ri-user-settings-line"></i></button>' +
            '<button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="removeParticipant(\'' + p.participantId + '\')" title="移除">' +
            '<i class="ri-user-unfollow-line"></i></button>' +
            '</div></div></div>';
    });
    container.innerHTML = html;
}

function renderCapabilityBindings() {
    const container = document.getElementById('bindingList');
    if (!currentGroup.capabilityBindings?.length) {
        container.innerHTML = '<p class="nx-text-secondary">暂无能力绑定</p>';
        return;
    }
    
    const groupedBindings = {};
    currentGroup.capabilityBindings.forEach(b => {
        const key = b.capId;
        if (!groupedBindings[key]) {
            groupedBindings[key] = {
                capId: b.capId,
                capName: b.capName,
                providers: []
            };
        }
        groupedBindings[key].providers.push(b);
    });
    
    let html = '';
    Object.values(groupedBindings).forEach(group => {
        const mainProvider = group.providers.find(p => p.priority === 1) || group.providers[0];
        const hasMultipleProviders = group.providers.length > 1;
        
        html += '<div class="nx-card nx-mb-3">' +
            '<div class="nx-card__body">' +
            '<div class="nx-flex nx-items-start nx-justify-between">' +
            '<div class="nx-flex-1">' +
            '<div class="nx-flex nx-items-center nx-gap-2 nx-mb-2">' +
            '<i class="ri-flashlight-line" style="color: var(--nx-primary);"></i>' +
            '<span class="nx-font-medium">' + (group.capName || group.capId) + '</span>' +
            '<span class="nx-badge nx-badge--secondary">' + group.providers.length + '个提供者</span>' +
            '</div>' +
            '<div class="nx-text-sm nx-text-secondary nx-mb-2">能力ID: ' + group.capId + '</div>';
        
        html += '<div class="nx-flex nx-flex-col nx-gap-1">';
        group.providers.sort((a, b) => (a.priority || 1) - (b.priority || 1)).forEach((p, idx) => {
            const isPrimary = idx === 0;
            const statusClass = p.status === 'ACTIVE' ? 'nx-badge--success' : p.status === 'ERROR' ? 'nx-badge--danger' : 'nx-badge--secondary';
            const badge = isPrimary ? '<span class="nx-badge nx-badge--primary nx-text-xs">主</span>' : '<span class="nx-badge nx-badge--info nx-text-xs">备用</span>';
            const fallbackIcon = p.fallback ? '<i class="ri-refresh-line" title="故障转移: 已启用"></i>' : '';
            
            html += '<div class="nx-flex nx-items-center nx-gap-2 nx-p-2 nx-bg-elevated nx-rounded">' +
                '<span class="nx-text-xs nx-text-secondary" style="min-width: 20px;">#' + (idx + 1) + '</span>' +
                badge +
                '<span class="nx-text-sm">' + (p.providerType || '-') + ': ' + (p.providerId || '-') + '</span>' +
                '<span class="nx-badge ' + statusClass + ' nx-text-xs">' + (p.status || 'ACTIVE') + '</span>' +
                '<span class="nx-text-xs nx-text-secondary">P' + (p.priority || 1) + '</span>' +
                fallbackIcon +
                '</div>';
        });
        html += '</div>';
        
        html += '</div>' +
            '<div class="nx-flex nx-gap-1 nx-ml-2">' +
            '<button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="showCapabilityDetail(\'' + group.capId + '\')" title="查看详情">' +
            '<i class="ri-information-line"></i></button>' +
            '<button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="unbindCapabilityGroup(\'' + group.capId + '\')" title="解绑所有">' +
            '<i class="ri-link-unlink"></i></button>' +
            '</div>' +
            '</div></div></div>';
    });
    container.innerHTML = html;
}

function unbindCapabilityGroup(capId) {
    if (!confirm('确定要解绑该能力的所有提供者吗？')) return;
    
    const bindings = currentGroup.capabilityBindings?.filter(b => b.capId === capId) || [];
    let removed = 0;
    
    bindings.forEach(async (b) => {
        try {
            const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/capabilities/' + b.bindingId, {
                method: 'DELETE'
            });
            const result = await response.json();
            if (result.status === 'success') {
                removed++;
            }
        } catch (e) {
            console.error('Failed to unbind:', e);
        }
    });
    
    setTimeout(() => {
        loadSceneGroup(sceneGroupId);
    }, 500);
}

function renderSnapshots() {
    const container = document.getElementById('snapshotList');
    if (!currentGroup.snapshots?.length) {
        container.innerHTML = '<p class="nx-text-secondary">暂无快照</p>';
        return;
    }
    
    let html = '';
    currentGroup.snapshots.forEach(s => {
        html += '<div class="nx-card nx-mb-3">' +
            '<div class="nx-card__body">' +
            '<div class="nx-flex nx-items-center nx-justify-between">' +
            '<div>' +
            '<div class="nx-flex nx-items-center nx-gap-2">' +
            '<i class="ri-camera-line" style="color: var(--nx-primary);"></i>' +
            '<span class="nx-font-medium">' + s.snapshotId + '</span>' +
            '</div>' +
            '<div class="nx-text-sm nx-text-secondary nx-mt-1">创建: ' + formatTime(s.createTime) + '</div>' +
            '</div>' +
            '<div class="nx-flex nx-gap-2">' +
            '<button class="nx-btn nx-btn--secondary nx-btn--sm" onclick="restoreSnapshot(\'' + s.snapshotId + '\')">' +
            '<i class="ri-restore-line"></i> 恢复</button>' +
            '<button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="deleteSnapshot(\'' + s.snapshotId + '\')">' +
            '<i class="ri-delete-bin-line"></i></button>' +
            '</div></div></div></div>';
    });
    container.innerHTML = html;
}

function renderLogs() {
    loadLogs();
}

function inviteParticipant() {
    selectedParticipant = null;
    document.getElementById('participantForm').reset();
    document.getElementById('participantSelectorContainer').innerHTML = '<p class="nx-text-secondary nx-p-3">请先选择参与者类型</p>';
    document.getElementById('selectedParticipantDisplay').innerHTML = '<span class="nx-text-secondary">-</span>';
    document.getElementById('participantRole').innerHTML = '<option value="">请选择角色</option>';
    document.getElementById('participantModal').classList.add('nx-modal--open');
}

function closeParticipantModal() {
    document.getElementById('participantModal').classList.remove('nx-modal--open');
}

function onParticipantTypeChange() {
    const type = document.getElementById('participantType').value;
    const container = document.getElementById('participantSelectorContainer');
    const roleSelect = document.getElementById('participantRole');
    
    selectedParticipant = null;
    document.getElementById('selectedParticipantDisplay').innerHTML = '<span class="nx-text-secondary">-</span>';
    roleSelect.innerHTML = '<option value="">请选择角色</option>';
    
    if (type === 'USER') {
        renderUserSelector(container);
        roleSelect.innerHTML = '<option value="manager">管理者</option><option value="employee">员工</option><option value="hr">HR</option>';
    } else if (type === 'AGENT') {
        renderAgentSelector(container);
        roleSelect.innerHTML = '<option value="llm-assistant">LLM助手</option><option value="coordinator">协调Agent</option>';
    } else {
        container.innerHTML = '<p class="nx-text-secondary nx-p-3">请先选择参与者类型</p>';
    }
}

function renderUserSelector(container) {
    const users = allUsers.length > 0 ? allUsers : getDefaultUsers();
    
    let html = '<select class="nx-input" id="userSelect" onchange="onUserSelect()"><option value="">请选择用户</option>';
    users.forEach(user => {
        const exists = currentGroup.participants?.some(p => p.participantId === user.userId);
        if (!exists) {
            html += '<option value="' + user.userId + '" data-name="' + (user.name || user.userId) + '">' + (user.name || user.userId) + ' (' + user.userId + ')</option>';
        }
    });
    html += '</select>';
    container.innerHTML = html;
}

function onUserSelect() {
    const select = document.getElementById('userSelect');
    const userId = select.value;
    const userName = select.options[select.selectedIndex].getAttribute('data-name');
    
    if (userId) {
        selectedParticipant = { id: userId, name: userName, type: 'USER' };
        document.getElementById('selectedParticipantDisplay').innerHTML = 
            '<span class="nx-font-medium">' + userName + '</span> <span class="nx-text-secondary">(' + userId + ')</span>';
    } else {
        selectedParticipant = null;
        document.getElementById('selectedParticipantDisplay').innerHTML = '<span class="nx-text-secondary">-</span>';
    }
}

function renderAgentSelector(container) {
    const agents = [
        { id: 'agent-llm-001', name: 'LLM分析助手' },
        { id: 'agent-coordinator-001', name: '协调Agent' }
    ];
    
    let html = '<select class="nx-input" id="agentSelect" onchange="onAgentSelect()"><option value="">请选择Agent</option>';
    agents.forEach(agent => {
        const exists = currentGroup.participants?.some(p => p.participantId === agent.id);
        if (!exists) {
            html += '<option value="' + agent.id + '" data-name="' + agent.name + '">' + agent.name + ' (' + agent.id + ')</option>';
        }
    });
    html += '</select>';
    container.innerHTML = html;
}

function onAgentSelect() {
    const select = document.getElementById('agentSelect');
    const agentId = select.value;
    const agentName = select.options[select.selectedIndex].getAttribute('data-name');
    
    if (agentId) {
        selectedParticipant = { id: agentId, name: agentName, type: 'AGENT' };
        document.getElementById('selectedParticipantDisplay').innerHTML = 
            '<span class="nx-font-medium">' + agentName + '</span> <span class="nx-text-secondary">(' + agentId + ')</span>';
    } else {
        selectedParticipant = null;
        document.getElementById('selectedParticipantDisplay').innerHTML = '<span class="nx-text-secondary">-</span>';
    }
}

async function saveParticipant() {
    if (!selectedParticipant) {
        alert('请选择参与者');
        return;
    }
    
    const role = document.getElementById('participantRole').value;
    if (!role) {
        alert('请选择角色');
        return;
    }
    
    const participant = {
        participantId: selectedParticipant.id,
        participantType: selectedParticipant.type,
        role: role,
        name: selectedParticipant.name
    };
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/participants', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(participant)
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            closeParticipantModal();
            loadSceneGroup(sceneGroupId);
        } else {
            alert('保存失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to save participant:', error);
        alert('移除失败: ' + error.message);
    }
}

let currentEditingParticipantId = null;

function showChangeRoleModal(participantId, currentRole) {
    currentEditingParticipantId = participantId;
    
    const newRole = prompt('请输入新角色:', currentRole);
    if (newRole && newRole !== currentRole) {
        changeParticipantRole(participantId, newRole);
    }
}

async function changeParticipantRole(participantId, newRole) {
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/participants/' + participantId + '/role', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ newRole: newRole })
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            loadSceneGroup(sceneGroupId);
        } else {
            alert('角色变更失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to change role:', error);
        alert('角色变更失败: ' + error.message);
    }
}

async function removeParticipant(participantId) {
    if (!confirm('确定要移除此参与者吗？')) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/participants/' + participantId, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            loadSceneGroup(sceneGroupId);
        } else {
            alert('移除失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to remove participant:', error);
        alert('移除失败: ' + error.message);
    }
}

function bindCapability() {
    selectedCapability = null;
    document.getElementById('capabilityForm').reset();
    
    const capSelect = document.getElementById('capabilitySelect');
    capSelect.innerHTML = '<option value="">请选择能力</option>';
    
    const caps = availableCapabilities.length > 0 ? availableCapabilities : getDefaultCapabilities();
    caps.forEach(cap => {
        const exists = currentGroup.capabilityBindings?.some(b => b.capId === cap.id);
        if (!exists) {
            capSelect.innerHTML += '<option value="' + cap.id + '" data-name="' + cap.name + '">' + cap.name + ' (' + cap.type + ')</option>';
        }
    });
    
    const providerSelect = document.getElementById('providerSelect');
    providerSelect.innerHTML = '<option value="">请选择提供者</option>';
    providerSelect.innerHTML += '<option value="skill-daily-report">日报Skill</option>';
    providerSelect.innerHTML += '<option value="agent-llm">LLM Agent</option>';
    
    document.getElementById('capabilityModal').classList.add('nx-modal--open');
}

function closeCapabilityModal() {
    document.getElementById('capabilityModal').classList.remove('nx-modal--open');
}

function onCapabilitySelect() {
    const select = document.getElementById('capabilitySelect');
    const capId = select.value;
    const capName = select.options[select.selectedIndex].getAttribute('data-name');
    
    if (capId) {
        selectedCapability = { id: capId, name: capName };
    } else {
        selectedCapability = null;
    }
}

async function saveCapabilityBinding() {
    if (!selectedCapability) {
        alert('请选择能力');
        return;
    }
    
    const providerId = document.getElementById('providerSelect').value;
    if (!providerId) {
        alert('请选择提供者');
        return;
    }
    
    const priority = parseInt(document.getElementById('bindingPriority').value) || 1;
    const fallback = document.getElementById('bindingFallback').checked;
    
    const binding = {
        capId: selectedCapability.id,
        capName: selectedCapability.name,
        providerType: document.getElementById('providerType').value,
        providerId: providerId,
        priority: priority,
        fallback: fallback
    };
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/capabilities', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(binding)
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            closeCapabilityModal();
            loadSceneGroup(sceneGroupId);
        } else {
            alert('绑定失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to bind capability:', error);
        alert('绑定失败: ' + error.message);
    }
}

async function unbindCapability(bindingId) {
    if (!confirm('确定要解绑此能力吗？')) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/capabilities/' + bindingId, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            loadSceneGroup(sceneGroupId);
        } else {
            alert('解绑失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to unbind capability:', error);
        alert('解绑失败: ' + error.message);
    }
}

let currentEditingBindingId = null;

function editCapabilityBinding(bindingId) {
    const binding = currentGroup.capabilityBindings?.find(b => b.bindingId === bindingId);
    if (!binding) {
        alert('找不到能力绑定');
        return;
    }
    
    currentEditingBindingId = bindingId;
    
    document.getElementById('bindingPriority').value = binding.priority || 1;
    document.getElementById('bindingFallback').checked = binding.fallback !== false;
    
    const newPriority = prompt('请输入新优先级 (1-100):', binding.priority || 1);
    if (newPriority !== null) {
        const priority = parseInt(newPriority) || 1;
        updateCapabilityBindingConfig(bindingId, priority, binding.fallback !== false);
    }
}

async function updateCapabilityBindingConfig(bindingId, priority, fallback) {
    try {
        const binding = currentGroup.capabilityBindings?.find(b => b.bindingId === bindingId);
        if (!binding) return;
        
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/capabilities/' + bindingId, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                priority: priority,
                fallback: fallback
            })
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            loadSceneGroup(sceneGroupId);
        } else {
            alert('更新失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to update capability binding:', error);
        alert('更新失败: ' + error.message);
    }
}

function startWorkflow() {
    if (!confirm('确定要启动工作流吗？')) return;
    alert('工作流已启动');
}

async function createSnapshot() {
    const description = prompt('请输入快照描述（可选）:');
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/snapshots', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ description: description || '手动创建' })
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            loadSceneGroup(sceneGroupId);
        } else {
            alert('创建快照失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to create snapshot:', error);
        alert('创建快照失败: ' + error.message);
    }
}

async function restoreSnapshot(snapshotId) {
    if (!confirm('确定要恢复到此快照吗？')) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/snapshots/' + snapshotId + '/restore', {
            method: 'POST'
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            loadSceneGroup(sceneGroupId);
            alert('快照已恢复');
        } else {
            alert('恢复失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to restore snapshot:', error);
        alert('恢复失败: ' + error.message);
    }
}

async function deleteSnapshot(snapshotId) {
    if (!confirm('确定要删除此快照吗？')) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/snapshots/' + snapshotId, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            loadSceneGroup(sceneGroupId);
        } else {
            alert('删除失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to delete snapshot:', error);
        alert('删除失败: ' + error.message);
    }
}

async function toggleStatus() {
    const action = currentGroup.status === 'ACTIVE' ? 'deactivate' : 'activate';
    const confirmMsg = currentGroup.status === 'ACTIVE' ? '确定要暂停此场景组吗？' : '确定要激活此场景组吗？';
    
    if (!confirm(confirmMsg)) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/' + action, {
            method: 'POST'
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            currentGroup.status = action === 'activate' ? 'ACTIVE' : 'SUSPENDED';
            renderSceneGroup();
        } else {
            alert('操作失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to toggle status:', error);
        alert('操作失败: ' + error.message);
    }
}

async function showCapabilityDetail(capId) {
    const bindings = currentGroup.capabilityBindings?.filter(b => b.capId === capId) || [];
    if (bindings.length === 0) {
        alert('找不到能力绑定');
        return;
    }
    
    const group = {
        capId: capId,
        capName: bindings[0].capName,
        providers: bindings
    };
    
    currentDetailBinding = group;
    
    document.getElementById('capabilityDetailTitle').textContent = group.capName || capId;
    
    document.getElementById('detailCapId').textContent = capId;
    document.getElementById('detailCapName').textContent = group.capName || '-';
    document.getElementById('detailCapType').textContent = '-';
    document.getElementById('detailCapCategory').textContent = '-';
    document.getElementById('detailCapDescription').textContent = '能力描述信息';
    
    let providerHtml = '<table class="nx-table"><thead><tr><th>#</th><th>角色</th><th>提供者类型</th><th>提供者ID</th><th>优先级</th><th>故障转移</th><th>状态</th></tr></thead><tbody>';
    group.providers.sort((a, b) => (a.priority || 1) - (b.priority || 1)).forEach((p, idx) => {
        const isPrimary = idx === 0;
        const role = isPrimary ? '<span class="nx-badge nx-badge--primary">主</span>' : '<span class="nx-badge nx-badge--info">备用</span>';
        const statusClass = p.status === 'ACTIVE' ? 'nx-badge--success' : 'nx-badge--secondary';
        const fallback = p.fallback ? '<i class="ri-check-line" style="color: var(--nx-success)"></i>' : '<i class="ri-close-line" style="color: var(--nx-text-secondary)"></i>';
        
        providerHtml += '<tr>' +
            '<td>' + (idx + 1) + '</td>' +
            '<td>' + role + '</td>' +
            '<td>' + (p.providerType || '-') + '</td>' +
            '<td>' + (p.providerId || '-') + '</td>' +
            '<td>P' + (p.priority || 1) + '</td>' +
            '<td>' + fallback + '</td>' +
            '<td><span class="nx-badge ' + statusClass + '">' + (p.status || 'ACTIVE') + '</span></td>' +
            '</tr>';
    });
    providerHtml += '</tbody></table>';
    document.getElementById('detailParameters').innerHTML = providerHtml;
    
    try {
        const response = await fetch('/api/v1/capabilities/detail/' + capId);
        const result = await response.json();
        
        if (result.status === 'success' && result.data) {
            const cap = result.data;
            document.getElementById('detailCapType').textContent = cap.typeName || '-';
            document.getElementById('detailCapCategory').textContent = cap.categoryName || '-';
            document.getElementById('detailCapDescription').textContent = cap.description || '-';
        }
    } catch (e) {
        console.error('Failed to load capability details:', e);
    }
    
    loadCapabilityLogs(capId);
    
    document.querySelectorAll('[data-detail-tab]').forEach(tab => {
        tab.addEventListener('click', function() {
            document.querySelectorAll('[data-detail-tab]').forEach(t => t.classList.remove('nx-tabs__tab--active'));
            this.classList.add('nx-tabs__tab--active');
            
            const tabId = this.getAttribute('data-detail-tab');
            document.querySelectorAll('.detail-tab-content').forEach(content => {
                content.style.display = 'none';
            });
            document.getElementById('detail-tab-' + tabId).style.display = 'block';
        });
    });
    
    document.getElementById('capabilityDetailModal').classList.add('nx-modal--open');
}

function closeCapabilityDetailModal() {
    document.getElementById('capabilityDetailModal').classList.remove('nx-modal--open');
}

async function loadCapabilityLogs(capId) {
    if (!sceneGroupId) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/logs/recent?limit=20');
        const result = await response.json();
        
        if (result.status === 'success' && result.data) {
            const logs = result.data.filter(log => log.action && log.action.includes(capId));
            renderCapabilityLogs(logs);
        } else {
            document.getElementById('detailCapabilityLogs').innerHTML = '<p class="nx-text-secondary">暂无调用日志</p>';
        }
    } catch (error) {
        console.error('Failed to load capability logs:', error);
        document.getElementById('detailCapabilityLogs').innerHTML = '<p class="nx-text-secondary">暂无调用日志</p>';
    }
}

function renderCapabilityLogs(logs) {
    const container = document.getElementById('detailCapabilityLogs');
    if (!logs || logs.length === 0) {
        container.innerHTML = '<p class="nx-text-secondary">暂无调用日志</p>';
        return;
    }
    
    let html = '<div class="nx-flex nx-flex-col nx-gap-2" style="max-height: 300px; overflow-y: auto;">';
    logs.forEach(log => {
        const levelClass = log.status === 'ERROR' ? 'nx-badge--danger' : 
                          log.status === 'WARN' ? 'nx-badge--warning' : 
                          log.status === 'SUCCESS' ? 'nx-badge--success' : 'nx-badge--info';
        html += '<div class="nx-flex nx-items-start nx-gap-3 nx-p-2 nx-bg-elevated nx-rounded">' +
            '<span class="nx-text-sm nx-text-secondary" style="min-width: 80px;">' + formatTime(log.timestamp) + '</span>' +
            '<span class="nx-badge ' + levelClass + '">' + (log.status || 'INFO') + '</span>' +
            '<span class="nx-text-sm">' + (log.message || '-') + '</span>' +
            '</div>';
    });
    html += '</div>';
    container.innerHTML = html;
}

function refreshCapabilityLogs() {
    if (currentDetailBinding) {
        loadCapabilityLogs(currentDetailBinding.capId);
    }
}

async function bindKnowledge() {
    const kbId = prompt('请输入知识库ID:');
    if (!kbId) return;
    
    const layer = prompt('请输入知识层 (GENERAL/PROFESSIONAL/SCENE):', 'SCENE');
    if (!layer) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/knowledge', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ kbId: kbId, layer: layer.toUpperCase() })
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            loadKnowledgeBindings();
        } else {
            alert('绑定失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to bind knowledge:', error);
        alert('绑定失败: ' + error.message);
    }
}

async function loadKnowledgeBindings() {
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/knowledge');
        const result = await response.json();
        
        if (result.status === 'success' && result.data) {
            renderKnowledgeBindings(result.data);
        }
    } catch (error) {
        console.error('Failed to load knowledge bindings:', error);
    }
}

function renderKnowledgeBindings(bindings) {
    const generalList = document.getElementById('generalKnowledgeList');
    const professionalList = document.getElementById('professionalKnowledgeList');
    const sceneList = document.getElementById('sceneKnowledgeList');
    
    const byLayer = { GENERAL: [], PROFESSIONAL: [], SCENE: [] };
    (bindings || []).forEach(b => {
        if (byLayer[b.layer]) {
            byLayer[b.layer].push(b);
        }
    });
    
    generalList.innerHTML = renderKnowledgeList(byLayer.GENERAL, 'GENERAL');
    professionalList.innerHTML = renderKnowledgeList(byLayer.PROFESSIONAL, 'PROFESSIONAL');
    sceneList.innerHTML = renderKnowledgeList(byLayer.SCENE, 'SCENE');
}

function renderKnowledgeList(items, layer) {
    if (!items || items.length === 0) {
        return '<p class="nx-text-secondary nx-text-sm">暂无绑定</p>';
    }
    
    return items.map(item => 
        '<div class="nx-flex nx-items-center nx-justify-between nx-p-2 nx-bg nx-rounded nx-mb-2">' +
        '<div>' +
        '<span class="nx-font-medium">' + (item.kbName || item.kbId) + '</span>' +
        '<span class="nx-text-secondary nx-text-sm nx-ml-2">' + item.kbId + '</span>' +
        '</div>' +
        '<button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="unbindKnowledge(\'' + item.kbId + '\', \'' + layer + '\')">' +
        '<i class="ri-delete-bin-line"></i>' +
        '</button>' +
        '</div>'
    ).join('');
}

async function unbindKnowledge(kbId, layer) {
    if (!confirm('确定要解绑此知识库吗？')) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/knowledge/' + kbId + '?layer=' + layer, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            loadKnowledgeBindings();
        } else {
            alert('解绑失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to unbind knowledge:', error);
        alert('解绑失败: ' + error.message);
    }
}

async function saveKnowledgeConfig() {
    const config = {
        topK: parseInt(document.getElementById('knowledgeTopK').value) || 5,
        threshold: parseFloat(document.getElementById('knowledgeThreshold').value) || 0.7,
        crossLayerSearch: document.getElementById('crossLayerSearch').value === 'true'
    };
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/knowledge/config', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(config)
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            alert('配置已保存');
        } else {
            alert('保存失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to save knowledge config:', error);
        alert('保存失败: ' + error.message);
    }
}

async function loadLlmConfig() {
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/llm/config');
        const result = await response.json();
        
        if (result.status === 'success' && result.data) {
            const config = result.data;
            
            if (config.provider) document.getElementById('llmProvider').value = config.provider;
            if (config.model) document.getElementById('llmModel').value = config.model;
            if (config.decisionMode) document.getElementById('decisionMode').value = config.decisionMode;
            if (config.decisionTimeout) document.getElementById('decisionTimeout').value = config.decisionTimeout;
            if (config.decisionCache !== undefined) document.getElementById('decisionCache').value = config.decisionCache.toString();
            if (config.cacheTtl) document.getElementById('cacheTtl').value = config.cacheTtl;
            if (config.functionCalling !== undefined) document.getElementById('functionCalling').value = config.functionCalling.toString();
            if (config.maxIterations) document.getElementById('maxIterations').value = config.maxIterations;
            if (config.llmTimeout) document.getElementById('llmTimeout').value = config.llmTimeout;
            if (config.dailyTokenLimit) document.getElementById('dailyTokenLimit').value = config.dailyTokenLimit;
            if (config.usedTokens !== undefined) document.getElementById('usedTokens').value = config.usedTokens;
            if (config.remainingTokens !== undefined) document.getElementById('remainingTokens').value = config.remainingTokens;
        }
    } catch (error) {
        console.error('Failed to load LLM config:', error);
    }
}

function onLlmProviderChange() {
    const provider = document.getElementById('llmProvider').value;
    const modelSelect = document.getElementById('llmModel');
    
    const models = {
        deepseek: [
            { value: 'deepseek-chat', label: 'DeepSeek Chat' },
            { value: 'deepseek-coder', label: 'DeepSeek Coder' }
        ],
        baidu: [
            { value: 'ernie-bot-4', label: 'ERNIE Bot 4.0' },
            { value: 'ernie-bot-turbo', label: 'ERNIE Bot Turbo' }
        ],
        openai: [
            { value: 'gpt-4-turbo', label: 'GPT-4 Turbo' },
            { value: 'gpt-3.5-turbo', label: 'GPT-3.5 Turbo' }
        ],
        qianwen: [
            { value: 'qwen-turbo', label: '通义千问 Turbo' },
            { value: 'qwen-plus', label: '通义千问 Plus' }
        ]
    };
    
    const providerModels = models[provider] || [];
    modelSelect.innerHTML = providerModels.map(m => 
        '<option value="' + m.value + '">' + m.label + '</option>'
    ).join('');
}

async function saveLlmConfig() {
    const config = {
        provider: document.getElementById('llmProvider').value,
        model: document.getElementById('llmModel').value,
        decisionMode: document.getElementById('decisionMode').value,
        decisionTimeout: parseInt(document.getElementById('decisionTimeout').value) || 30000,
        decisionCache: document.getElementById('decisionCache').value === 'true',
        cacheTtl: parseInt(document.getElementById('cacheTtl').value) || 300000,
        functionCalling: document.getElementById('functionCalling').value === 'true',
        maxIterations: parseInt(document.getElementById('maxIterations').value) || 5,
        llmTimeout: parseInt(document.getElementById('llmTimeout').value) || 60000,
        dailyTokenLimit: parseInt(document.getElementById('dailyTokenLimit').value) || 100000
    };
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/llm/config', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(config)
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            alert('配置已保存');
        } else {
            alert('保存失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to save LLM config:', error);
        alert('保存失败: ' + error.message);
    }
}

async function testLlmConnection() {
    const provider = document.getElementById('llmProvider').value;
    const model = document.getElementById('llmModel').value;
    
    try {
        const response = await fetch('/api/v1/llm/providers/' + provider + '/test', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ model: model })
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            alert('连接测试成功');
        } else {
            alert('连接测试失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('Failed to test LLM connection:', error);
        alert('连接测试失败: ' + error.message);
    }
}
