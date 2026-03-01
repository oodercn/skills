let currentGroup = null;
let sceneGroupId = null;
let availableCapabilities = [];
let orgTree = [];
let users = [];
let agents = [];
let selectedParticipant = null;
let selectedCapability = null;
let selectedCategory = null;
let capabilityTypesDict = {};

document.addEventListener('DOMContentLoaded', async function() {
    sceneGroupId = getUrlParam('id');
    
    initTabs();
    await initDicts();
    loadAvailableCapabilities();
    loadOrgData();
    initLlmAssistant();
    
    if (sceneGroupId) {
        loadSceneGroup(sceneGroupId);
    } else {
        loadMockSceneGroup();
    }
});

async function initDicts() {
    if (typeof DictCache !== 'undefined') {
        await DictCache.init();
        const dict = await DictCache.getDict(DictCache.DICT_CODES.CAPABILITY_TYPE);
        if (dict && dict.items) {
            dict.items.forEach(item => {
                capabilityTypesDict[item.code] = { name: item.name, icon: item.icon };
            });
        }
    }
}

function getCapabilityTypeInfo(type) {
    if (capabilityTypesDict[type]) {
        return capabilityTypesDict[type];
    }
    return { name: type, icon: 'ri-tools-line' };
}

function initLlmAssistant() {
    if (typeof LlmAssistant !== 'undefined') {
        LlmAssistant.init();
    }
}

function setLlmContext(type, name, data) {
    if (typeof LlmAssistant !== 'undefined') {
        LlmAssistant.setContext({ type: type, name: name, data: data });
        LlmAssistant.togglePanel();
    }
}

function getUrlParam(name) {
    const params = new URLSearchParams(window.location.search);
    return params.get(name);
}

function initTabs() {
    const tabs = document.querySelectorAll('.nx-tabs__tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            tabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');
            
            const tabId = this.getAttribute('data-tab');
            document.querySelectorAll('.tab-content').forEach(content => {
                content.classList.remove('active');
            });
            document.getElementById('tab-' + tabId).classList.add('active');
        });
    });
}

async function loadAvailableCapabilities() {
    try {
        const response = await fetch('/api/v1/capabilities');
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
            availableCapabilities = result.data;
            console.log('Loaded capabilities:', availableCapabilities.length);
        } else {
            availableCapabilities = getDefaultCapabilities();
        }
    } catch (error) {
        console.error('Failed to load capabilities:', error);
        availableCapabilities = getDefaultCapabilities();
    }
}

async function loadOrgData() {
    try {
        const [usersRes, treeRes] = await Promise.all([
            fetch('/api/v1/org/users'),
            fetch('/api/v1/org/tree')
        ]);
        
        const usersResult = await usersRes.json();
        const treeResult = await treeRes.json();
        
        if (usersResult.code === 200) {
            users = usersResult.data || [];
            console.log('Loaded users:', users.length);
        }
        if (treeResult.code === 200) {
            orgTree = treeResult.data || [];
        }
    } catch (error) {
        console.error('Failed to load org data:', error);
        users = getDefaultUsers();
        orgTree = getDefaultOrgTree();
    }
}

function getDefaultCapabilities() {
    return [
        { id: 'report-remind', name: '日志提醒', type: 'COMMUNICATION', description: '定时提醒员工提交日志' },
        { id: 'report-submit', name: '日志提交', type: 'SERVICE', description: '员工提交工作日志' },
        { id: 'report-aggregate', name: '日志汇总', type: 'SERVICE', description: '汇总所有员工日志' },
        { id: 'report-analyze', name: '日志分析', type: 'AI', description: 'AI分析日志内容' },
        { id: 'notification-email', name: '邮件通知', type: 'COMMUNICATION', description: '发送邮件通知' },
        { id: 'notification-sms', name: '短信通知', type: 'COMMUNICATION', description: '发送短信通知' },
        { id: 'data-backup', name: '数据备份', type: 'STORAGE', description: '自动备份数据' },
        { id: 'system-monitor', name: '系统监控', type: 'MONITORING', description: '监控系统运行状态' }
    ];
}

function getDefaultUsers() {
    return [
        { userId: 'user-manager-001', name: '张经理', role: 'manager', departmentId: 'dept-rd' },
        { userId: 'user-employee-001', name: '李员工', role: 'employee', departmentId: 'dept-rd' },
        { userId: 'user-employee-002', name: '王员工', role: 'employee', departmentId: 'dept-rd' },
        { userId: 'user-employee-003', name: '赵员工', role: 'employee', departmentId: 'dept-rd' },
        { userId: 'user-hr-001', name: '刘HR', role: 'hr', departmentId: 'dept-hr' }
    ];
}

function getDefaultOrgTree() {
    return [
        {
            id: 'dept-rd',
            name: '研发部',
            type: 'department',
            children: [
                { id: 'user-manager-001', name: '张经理', type: 'user', role: 'manager' },
                { id: 'user-employee-001', name: '李员工', type: 'user', role: 'employee' },
                { id: 'user-employee-002', name: '王员工', type: 'user', role: 'employee' },
                { id: 'user-employee-003', name: '赵员工', type: 'user', role: 'employee' }
            ]
        },
        {
            id: 'dept-hr',
            name: '人力资源部',
            type: 'department',
            children: [
                { id: 'user-hr-001', name: '刘HR', type: 'user', role: 'hr' }
            ]
        }
    ];
}

async function loadSceneGroup(id) {
    try {
        const response = await fetch('/api/v1/scene-groups/' + id);
        const result = await response.json();
        
        if (result.code === 200 && result.data) {
            currentGroup = result.data;
            renderSceneGroup();
        } else {
            loadMockSceneGroup();
        }
    } catch (error) {
        console.error('Failed to load scene group:', error);
        loadMockSceneGroup();
    }
}

function loadMockSceneGroup() {
    currentGroup = {
        sceneGroupId: sceneGroupId || 'sg-project-alpha',
        templateId: 'tpl-daily-report',
        name: '研发部日志汇报组',
        description: '研发部团队的日常日志汇报场景组，支持日志提交、提醒、汇总和分析。',
        status: 'ACTIVE',
        creatorId: 'user-manager-001',
        creatorType: 'USER',
        config: {
            minMembers: 2,
            maxMembers: 20
        },
        memberCount: 5,
        createTime: Date.now() - 86400000 * 5,
        participants: [
            { participantId: 'user-manager-001', participantType: 'USER', role: 'manager', status: 'ACTIVE', joinTime: Date.now() - 86400000 * 5, name: '张经理' },
            { participantId: 'user-employee-001', participantType: 'USER', role: 'employee', status: 'ACTIVE', joinTime: Date.now() - 86400000 * 4, name: '李员工' },
            { participantId: 'user-employee-002', participantType: 'USER', role: 'employee', status: 'ACTIVE', joinTime: Date.now() - 86400000 * 3, name: '王员工' },
            { participantId: 'agent-llm-001', participantType: 'AGENT', role: 'llm-assistant', status: 'ACTIVE', joinTime: Date.now() - 86400000 * 5, name: 'LLM分析助手' },
            { participantId: 'agent-coord-001', participantType: 'AGENT', role: 'coordinator', status: 'ACTIVE', joinTime: Date.now() - 86400000 * 5, name: '协调Agent' }
        ],
        capabilityBindings: [
            { bindingId: 'cb-001', capId: 'report-remind', capName: '日志提醒', providerType: 'AGENT', providerId: 'agent-coord-001', connectorType: 'INTERNAL', status: 'ACTIVE', priority: 1 },
            { bindingId: 'cb-002', capId: 'report-submit', capName: '日志提交', providerType: 'SKILL', providerId: 'skill-daily-report', connectorType: 'HTTP', status: 'ACTIVE', priority: 1 },
            { bindingId: 'cb-003', capId: 'report-aggregate', capName: '日志汇总', providerType: 'AGENT', providerId: 'agent-coord-001', connectorType: 'INTERNAL', status: 'ACTIVE', priority: 1 },
            { bindingId: 'cb-004', capId: 'report-analyze', capName: '日志分析', providerType: 'AGENT', providerId: 'agent-llm-001', connectorType: 'INTERNAL', status: 'ACTIVE', priority: 1 }
        ],
        workflows: [
            { workflowId: 'wf-001', triggerType: 'schedule', status: 'completed', startTime: Date.now() - 86400000, endTime: Date.now() - 86400000 + 3600000 }
        ],
        snapshots: [
            { snapshotId: 'snap-001', createTime: Date.now() - 86400000 * 2, status: 'valid', description: '每日自动快照' }
        ]
    };
    renderSceneGroup();
}

function renderSceneGroup() {
    document.getElementById('pageTitle').textContent = currentGroup.name;
    document.getElementById('groupName').textContent = currentGroup.name;
    document.getElementById('sceneGroupId').textContent = currentGroup.sceneGroupId;
    document.getElementById('groupTemplate').textContent = currentGroup.templateId;
    document.getElementById('groupDescription').textContent = currentGroup.description;
    document.getElementById('groupCreator').textContent = currentGroup.creatorId + ' (' + currentGroup.creatorType + ')';
    document.getElementById('groupCreateTime').textContent = formatTime(currentGroup.createTime);
    
    const statusIndicator = document.getElementById('groupStatus');
    statusIndicator.textContent = getStatusText(currentGroup.status);
    statusIndicator.className = 'status-indicator ' + getStatusClass(currentGroup.status);
    
    const toggleBtn = document.getElementById('toggleStatusBtn');
    if (currentGroup.status === 'ACTIVE') {
        toggleBtn.innerHTML = '<i class="ri-pause-line"></i> <span>暂停</span>';
    } else {
        toggleBtn.innerHTML = '<i class="ri-play-line"></i> <span>激活</span>';
    }
    
    document.getElementById('memberCount').textContent = currentGroup.memberCount || currentGroup.participants?.length || 0;
    document.getElementById('bindingCount').textContent = currentGroup.capabilityBindings?.length || 0;
    document.getElementById('workflowCount').textContent = currentGroup.workflows?.length || 0;
    
    if (currentGroup.config) {
        document.getElementById('groupConfig').innerHTML = `
            <div class="nx-flex nx-gap-4">
                <span>最小成员: ${currentGroup.config.minMembers || 1}</span>
                <span>最大成员: ${currentGroup.config.maxMembers || 100}</span>
            </div>
        `;
    }
    
    renderParticipantOverview();
    renderParticipants();
    renderCapabilityBindings();
    renderWorkflows();
    renderSnapshots();
    renderLogs();
}

function getStatusText(status) {
    if (typeof DictCache !== 'undefined') {
        const item = DictCache.getDictItem(DictCache.DICT_CODES.SCENE_GROUP_STATUS, status);
        if (item && item.name) {
            return item.name;
        }
    }
    const statusMap = { 'ACTIVE': '运行中', 'SUSPENDED': '已暂停', 'CREATING': '创建中', 'DESTROYED': '已销毁' };
    return statusMap[status] || status;
}

function getStatusClass(status) {
    const classMap = { 'ACTIVE': 'active', 'SUSPENDED': 'inactive', 'ERROR': 'error' };
    return classMap[status] || 'inactive';
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
        html += `<div class="nx-mb-3">
            <div class="nx-text-sm nx-font-medium nx-mb-2">${role} (${participants.length})</div>
            <div class="nx-flex nx-flex-wrap nx-gap-2">
                ${participants.map(p => `<span class="nx-badge nx-badge--secondary">${p.name || p.participantId}</span>`).join('')}
            </div>
        </div>`;
    });
    container.innerHTML = html;
}

function renderParticipants() {
    const container = document.getElementById('participantList');
    if (!currentGroup.participants?.length) {
        container.innerHTML = '<p class="nx-text-secondary">暂无参与者</p>';
        return;
    }
    
    let html = '';
    currentGroup.participants.forEach(p => {
        const avatarIcon = p.participantType === 'USER' ? 'ri-user-line' : 'ri-robot-line';
        const avatarBg = p.participantType === 'USER' ? 'var(--nx-primary-light)' : 'var(--nx-success-light)';
        
        html += `<div class="participant-card">
            <div class="participant-avatar" style="background: ${avatarBg};">
                <i class="${avatarIcon}"></i>
            </div>
            <div class="nx-flex-1">
                <div class="nx-flex nx-items-center nx-gap-2">
                    <span class="nx-font-medium">${p.name || p.participantId}</span>
                    <span class="nx-badge nx-badge--secondary">${p.role}</span>
                    <span class="nx-badge ${p.status === 'ACTIVE' ? 'nx-badge--success' : 'nx-badge--warning'}">${p.status}</span>
                </div>
                <div class="nx-text-sm nx-text-secondary nx-mt-1">类型: ${p.participantType} | 加入: ${formatTime(p.joinTime)}</div>
            </div>
            <div class="nx-flex nx-gap-2">
                <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="removeParticipant('${p.participantId}')"><i class="ri-user-unfollow-line"></i></button>
            </div>
        </div>`;
    });
    container.innerHTML = html;
}

function renderCapabilityBindings() {
    const container = document.getElementById('bindingList');
    if (!currentGroup.capabilityBindings?.length) {
        container.innerHTML = '<p class="nx-text-secondary">暂无能力绑定</p>';
        return;
    }
    
    let html = '';
    currentGroup.capabilityBindings.forEach(b => {
        html += `<div class="binding-card">
            <div class="nx-flex nx-items-start nx-justify-between">
                <div>
                    <div class="nx-flex nx-items-center nx-gap-2 nx-mb-2">
                        <i class="ri-flashlight-line" style="color: var(--nx-primary);"></i>
                        <span class="nx-font-medium">${b.capName || b.capId}</span>
                        <span class="nx-badge ${b.status === 'ACTIVE' ? 'nx-badge--success' : 'nx-badge--warning'}">${b.status}</span>
                    </div>
                    <div class="nx-text-sm nx-text-secondary">
                        提供者: ${b.providerType} / ${b.providerId} | 连接器: ${b.connectorType}
                    </div>
                </div>
                <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="unbindCapability('${b.bindingId}')"><i class="ri-link-unlink"></i></button>
            </div>
        </div>`;
    });
    container.innerHTML = html;
}

function renderWorkflows() {
    const container = document.getElementById('workflowList');
    if (!currentGroup.workflows?.length) {
        container.innerHTML = '<p class="nx-text-secondary">暂无工作流执行记录</p>';
        return;
    }
    
    let html = '';
    currentGroup.workflows.forEach(w => {
        html += `<div class="nx-card nx-mb-3">
            <div class="nx-card__body">
                <div class="nx-flex nx-items-center nx-justify-between">
                    <div>
                        <div class="nx-flex nx-items-center nx-gap-2">
                            <span class="nx-font-medium">${w.workflowId}</span>
                            <span class="nx-badge nx-badge--secondary">${w.triggerType}</span>
                            <span class="nx-badge ${w.status === 'completed' ? 'nx-badge--success' : 'nx-badge--warning'}">${w.status}</span>
                        </div>
                        <div class="nx-text-sm nx-text-secondary nx-mt-1">开始: ${formatTime(w.startTime)}</div>
                    </div>
                </div>
            </div>
        </div>`;
    });
    container.innerHTML = html;
}

function renderSnapshots() {
    const container = document.getElementById('snapshotList');
    if (!currentGroup.snapshots?.length) {
        container.innerHTML = '<p class="nx-text-secondary">暂无快照</p>';
        return;
    }
    
    let html = '';
    currentGroup.snapshots.forEach(s => {
        html += `<div class="nx-card nx-mb-3">
            <div class="nx-card__body">
                <div class="nx-flex nx-items-center nx-justify-between">
                    <div>
                        <div class="nx-flex nx-items-center nx-gap-2">
                            <i class="ri-camera-line" style="color: var(--nx-primary);"></i>
                            <span class="nx-font-medium">${s.snapshotId}</span>
                            <span class="nx-badge ${s.status === 'valid' ? 'nx-badge--success' : 'nx-badge--warning'}">${s.status}</span>
                        </div>
                        <div class="nx-text-sm nx-text-secondary nx-mt-1">创建: ${formatTime(s.createTime)}</div>
                    </div>
                    <div class="nx-flex nx-gap-2">
                        <button class="nx-btn nx-btn--secondary nx-btn--sm" onclick="restoreSnapshot('${s.snapshotId}')"><i class="ri-restore-line"></i> 恢复</button>
                        <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="deleteSnapshot('${s.snapshotId}')"><i class="ri-delete-bin-line"></i></button>
                    </div>
                </div>
            </div>
        </div>`;
    });
    container.innerHTML = html;
}

function renderLogs() {
    const container = document.getElementById('logList');
    const logs = [
        { time: Date.now() - 60000, level: 'INFO', source: 'scene-manager', message: '场景状态更新: 活跃' },
        { time: Date.now() - 120000, level: 'INFO', source: 'capability-exec', message: '能力执行完成: report-submit' },
        { time: Date.now() - 180000, level: 'WARN', source: 'session', message: '参与者心跳超时' }
    ];
    
    let html = '<div class="nx-flex nx-flex-col nx-gap-2">';
    logs.forEach(log => {
        html += `<div class="nx-flex nx-items-start nx-gap-3 nx-p-2" style="background: var(--nx-bg-elevated); border-radius: 6px;">
            <span class="nx-text-sm nx-text-secondary" style="min-width: 80px;">${formatTime(log.time)}</span>
            <span class="nx-badge ${log.level === 'ERROR' ? 'nx-badge--error' : log.level === 'WARN' ? 'nx-badge--warning' : 'nx-badge--info'}">${log.level}</span>
            <span class="nx-text-sm" style="min-width: 100px;">[${log.source}]</span>
            <span class="nx-text-sm">${log.message}</span>
        </div>`;
    });
    html += '</div>';
    container.innerHTML = html;
}

function inviteParticipant() {
    selectedParticipant = null;
    document.getElementById('participantForm').reset();
    document.getElementById('participantSelectorContainer').innerHTML = '<div class="nx-text-secondary nx-p-3">请先选择参与者类型</div>';
    document.getElementById('selectedParticipantDisplay').textContent = '-';
    document.getElementById('participantRole').innerHTML = '<option value="">请选择角色</option>';
    document.getElementById('participantModal').style.display = 'flex';
}

function closeParticipantModal() {
    document.getElementById('participantModal').style.display = 'none';
}

function onParticipantTypeChange() {
    const type = document.getElementById('participantType').value;
    const container = document.getElementById('participantSelectorContainer');
    const roleSelect = document.getElementById('participantRole');
    
    selectedParticipant = null;
    document.getElementById('selectedParticipantDisplay').textContent = '-';
    roleSelect.innerHTML = '<option value="">请选择角色</option>';
    
    console.log('Participant type changed:', type);
    
    if (type === 'USER') {
        container.innerHTML = '<button type="button" class="nx-btn nx-btn--secondary nx-w-100" onclick="selectUserFromTree()"><i class="ri-user-search-line"></i> 从组织机构选择用户</button>';
        roleSelect.innerHTML = '<option value="manager">管理者</option><option value="employee">员工</option>';
    } else if (type === 'AGENT') {
        container.innerHTML = '<button type="button" class="nx-btn nx-btn--secondary nx-w-100" onclick="selectAgentFromList()"><i class="ri-robot-line"></i> 选择Agent</button>';
        roleSelect.innerHTML = '<option value="llm-assistant">LLM助手</option><option value="coordinator">协调Agent</option>';
    } else if (type === 'SUPER_AGENT') {
        container.innerHTML = '<button type="button" class="nx-btn nx-btn--secondary nx-w-100" onclick="selectSuperAgentFromList()"><i class="ri-robot-2-line"></i> 选择SuperAgent</button>';
        roleSelect.innerHTML = '<option value="super-agent">超级Agent</option>';
    } else {
        container.innerHTML = '<div class="nx-text-secondary nx-p-3">请先选择参与者类型</div>';
    }
}

function selectUserFromTree() {
    NxSelectors.showTreeSelector({
        title: '选择用户',
        dataUrl: '/api/v1/selectors/org-tree',
        onSelect: function(node) {
            if (node.type === 'user') {
                selectedParticipant = { id: node.id, name: node.name, type: 'USER' };
                document.getElementById('participantIdSelect').value = node.id;
                document.getElementById('participantName').value = node.name;
                document.getElementById('selectedParticipantDisplay').textContent = node.name + ' (' + node.id + ')';
            }
        }
    });
}

function selectAgentFromList() {
    NxSelectors.showListSelector({
        title: '选择Agent',
        dataUrl: '/api/v1/selectors/providers?type=AGENT',
        valueField: 'id',
        displayField: 'name',
        descField: 'description',
        onSelect: function(item) {
            selectedParticipant = { id: item.id, name: item.name, type: 'AGENT' };
            document.getElementById('participantIdSelect').value = item.id;
            document.getElementById('participantName').value = item.name;
            document.getElementById('selectedParticipantDisplay').textContent = item.name + ' (' + item.id + ')';
        }
    });
}

function selectSuperAgentFromList() {
    NxSelectors.showListSelector({
        title: '选择SuperAgent',
        dataUrl: '/api/v1/selectors/providers?type=SUPER_AGENT',
        valueField: 'id',
        displayField: 'name',
        descField: 'description',
        onSelect: function(item) {
            selectedParticipant = { id: item.id, name: item.name, type: 'SUPER_AGENT' };
            document.getElementById('participantIdSelect').value = item.id;
            document.getElementById('participantName').value = item.name;
            document.getElementById('selectedParticipantDisplay').textContent = item.name + ' (' + item.id + ')';
        }
    });
}

function renderOrgTreeSelector(container) {
    const tree = orgTree.length > 0 ? orgTree : getDefaultOrgTree();
    
    let html = '<div class="org-tree">';
    tree.forEach(dept => {
        html += `<div class="org-tree-node dept" onclick="toggleDeptNode(this, '${dept.id}')">
            <i class="ri-folder-line"></i>${dept.name}
        </div>`;
        html += `<div class="org-tree-children" id="dept-children-${dept.id}">`;
        dept.children?.forEach(user => {
            const exists = currentGroup.participants?.some(p => p.participantId === user.id);
            if (!exists) {
                html += `<div class="org-tree-node user" onclick="selectUser('${user.id}', '${user.name}')">
                    <i class="ri-user-line"></i>${user.name}
                </div>`;
            }
        });
        html += '</div>';
    });
    html += '</div>';
    container.innerHTML = html;
}

function toggleDeptNode(element, deptId) {
    const children = document.getElementById('dept-children-' + deptId);
    if (children) {
        children.classList.toggle('expanded');
    }
    const icon = element.querySelector('i');
    if (icon) {
        icon.className = children?.classList.contains('expanded') ? 'ri-folder-open-line' : 'ri-folder-line';
    }
}

function selectUser(userId, userName) {
    document.querySelectorAll('.org-tree-node.user').forEach(node => node.classList.remove('selected'));
    event.target.closest('.org-tree-node').classList.add('selected');
    
    selectedParticipant = { id: userId, name: userName, type: 'USER' };
    document.getElementById('participantIdSelect').value = userId;
    document.getElementById('participantName').value = userName;
    document.getElementById('selectedParticipantDisplay').textContent = `${userName} (${userId})`;
}

function renderAgentSelector(container) {
    const agents = [
        { id: 'agent-llm-001', name: 'LLM分析助手' },
        { id: 'agent-coordinator-001', name: '协调Agent' }
    ];
    
    let html = '<div class="org-tree">';
    agents.forEach(agent => {
        const exists = currentGroup.participants?.some(p => p.participantId === agent.id);
        if (!exists) {
            html += `<div class="org-tree-node" onclick="selectAgent('${agent.id}', '${agent.name}')">
                <i class="ri-robot-line"></i>${agent.name}
            </div>`;
        }
    });
    html += '</div>';
    container.innerHTML = html;
}

function selectAgent(agentId, agentName) {
    document.querySelectorAll('.org-tree-node').forEach(node => node.classList.remove('selected'));
    event.target.closest('.org-tree-node').classList.add('selected');
    
    selectedParticipant = { id: agentId, name: agentName, type: 'AGENT' };
    document.getElementById('participantIdSelect').value = agentId;
    document.getElementById('participantName').value = agentName;
    document.getElementById('selectedParticipantDisplay').textContent = `${agentName} (${agentId})`;
}

function renderSuperAgentSelector(container) {
    const superAgents = [
        { id: 'super-agent-001', name: '超级Agent' }
    ];
    
    let html = '<div class="org-tree">';
    superAgents.forEach(agent => {
        html += `<div class="org-tree-node" onclick="selectSuperAgent('${agent.id}', '${agent.name}')">
            <i class="ri-robot-2-line"></i>${agent.name}
        </div>`;
    });
    html += '</div>';
    container.innerHTML = html;
}

function selectSuperAgent(agentId, agentName) {
    document.querySelectorAll('.org-tree-node').forEach(node => node.classList.remove('selected'));
    event.target.closest('.org-tree-node').classList.add('selected');
    
    selectedParticipant = { id: agentId, name: agentName, type: 'SUPER_AGENT' };
    document.getElementById('participantIdSelect').value = agentId;
    document.getElementById('participantName').value = agentName;
    document.getElementById('selectedParticipantDisplay').textContent = `${agentName} (${agentId})`;
}

async function saveParticipant() {
    if (!selectedParticipant) {
        alert('请选择参与者');
        return;
    }
    
    const participant = {
        participantId: selectedParticipant.id,
        participantType: document.getElementById('participantType').value,
        role: document.getElementById('participantRole').value,
        name: selectedParticipant.name
    };
    
    if (!participant.role) {
        alert('请选择角色');
        return;
    }
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/participants', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(participant)
        });
        const result = await response.json();
        
        if (result.code === 200) {
            closeParticipantModal();
            loadSceneGroup(sceneGroupId);
        } else {
            alert('保存失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to save participant:', error);
        alert('保存失败');
    }
}

async function removeParticipant(participantId) {
    if (!confirm('确定要移除此参与者吗？')) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/participants/' + participantId, {
            method: 'DELETE'
        });
        const result = await response.json();
        
        if (result.code === 200) {
            loadSceneGroup(sceneGroupId);
        } else {
            alert('移除失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to remove participant:', error);
        alert('移除失败');
    }
}

function bindCapability() {
    selectedCapability = null;
    selectedCategory = null;
    document.getElementById('capabilityForm').reset();
    document.getElementById('selectedCapabilityDisplay').textContent = '-';
    document.getElementById('capabilityModal').classList.add('open');
    
    renderCapabilityCategories();
}

function closeCapabilityModal() {
    document.getElementById('capabilityModal').classList.remove('open');
}

function selectProvider() {
    const providerType = document.getElementById('providerType').value;
    NxSelectors.showListSelector({
        title: '选择提供者',
        dataUrl: '/api/v1/selectors/providers?type=' + providerType,
        valueField: 'id',
        displayField: 'name',
        descField: 'description',
        onSelect: function(item) {
            document.getElementById('providerId').value = item.id;
            document.getElementById('providerName').value = item.name;
        }
    });
}

function renderCapabilityCategories() {
    const container = document.getElementById('capCategoryList');
    const capList = availableCapabilities.length > 0 ? availableCapabilities : getDefaultCapabilities();
    
    const categories = {};
    capList.forEach(cap => {
        const type = cap.type || 'CUSTOM';
        if (!categories[type]) categories[type] = [];
        categories[type].push(cap);
    });
    
    let html = '';
    Object.keys(categories).forEach(type => {
        const typeInfo = getCapabilityTypeInfo(type);
        html += `<div class="capability-category-item" onclick="selectCapabilityCategory('${type}')">
            <i class="${typeInfo.icon}"></i>
            <span>${typeInfo.name}</span>
            <span class="nx-badge nx-badge--secondary">${categories[type].length}</span>
        </div>`;
    });
    container.innerHTML = html;
}

function selectCapabilityCategory(type) {
    document.querySelectorAll('.capability-category-item').forEach(item => item.classList.remove('selected'));
    event.target.closest('.capability-category-item').classList.add('selected');
    
    selectedCategory = type;
    renderCapabilitiesByType(type);
}

function renderCapabilitiesByType(type) {
    const container = document.getElementById('capList');
    const capList = availableCapabilities.length > 0 ? availableCapabilities : getDefaultCapabilities();
    
    const filtered = capList.filter(cap => (cap.type || 'CUSTOM') === type);
    
    if (filtered.length === 0) {
        container.innerHTML = '<div class="nx-text-secondary nx-p-3">该类型暂无可用能力</div>';
        return;
    }
    
    let html = '';
    filtered.forEach(cap => {
        const exists = currentGroup.capabilityBindings?.some(b => b.capId === cap.id);
        if (!exists) {
            html += `<div class="capability-item" onclick="selectCapability('${cap.id}', '${cap.name}')">
                <div class="cap-name">${cap.name}</div>
                <div class="cap-desc">${cap.description || cap.id}</div>
            </div>`;
        }
    });
    
    if (html === '') {
        html = '<div class="nx-text-secondary nx-p-3">该类型能力已全部绑定</div>';
    }
    container.innerHTML = html;
}

function selectCapability(capId, capName) {
    document.querySelectorAll('.capability-item').forEach(item => item.classList.remove('selected'));
    event.target.closest('.capability-item').classList.add('selected');
    
    selectedCapability = { id: capId, name: capName };
    document.getElementById('capId').value = capId;
    document.getElementById('capNameDisplay').value = capName;
    document.getElementById('selectedCapabilityDisplay').textContent = `${capName} (${capId})`;
}

async function saveCapabilityBinding() {
    if (!selectedCapability) {
        alert('请选择能力');
        return;
    }
    
    const providerId = document.getElementById('providerId').value;
    const providerName = document.getElementById('providerName').value;
    
    if (!providerId) {
        alert('请选择提供者');
        return;
    }
    
    const binding = {
        capId: selectedCapability.id,
        capName: selectedCapability.name,
        providerType: document.getElementById('providerType').value,
        providerId: providerId,
        providerName: providerName,
        connectorType: document.getElementById('connectorType').value,
        priority: parseInt(document.getElementById('priority').value) || 1
    };
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/capabilities', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(binding)
        });
        const result = await response.json();
        
        if (result.code === 200) {
            closeCapabilityModal();
            loadSceneGroup(sceneGroupId);
        } else {
            alert('绑定失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to bind capability:', error);
        alert('绑定失败');
    }
}

async function unbindCapability(bindingId) {
    if (!confirm('确定要解绑此能力吗？')) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/capabilities/' + bindingId, {
            method: 'DELETE'
        });
        const result = await response.json();
        
        if (result.code === 200) {
            loadSceneGroup(sceneGroupId);
        } else {
            alert('解绑失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to unbind capability:', error);
        alert('解绑失败');
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
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ description: description || '手动创建' })
        });
        const result = await response.json();
        
        if (result.code === 200) {
            loadSceneGroup(sceneGroupId);
        } else {
            alert('创建快照失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to create snapshot:', error);
        alert('创建快照失败');
    }
}

async function restoreSnapshot(snapshotId) {
    if (!confirm('确定要恢复到此快照吗？')) return;
    
    try {
        const snapshot = currentGroup.snapshots?.find(s => s.snapshotId === snapshotId);
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/snapshots/' + snapshotId + '/restore', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(snapshot)
        });
        const result = await response.json();
        
        if (result.code === 200) {
            loadSceneGroup(sceneGroupId);
            alert('快照已恢复');
        } else {
            alert('恢复失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to restore snapshot:', error);
        alert('恢复失败');
    }
}

async function deleteSnapshot(snapshotId) {
    if (!confirm('确定要删除此快照吗？')) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/snapshots/' + snapshotId, {
            method: 'DELETE'
        });
        const result = await response.json();
        
        if (result.code === 200) {
            loadSceneGroup(sceneGroupId);
        } else {
            alert('删除失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to delete snapshot:', error);
        alert('删除失败');
    }
}

function filterLogs() { renderLogs(); }
function refreshLogs() { renderLogs(); }

async function toggleStatus() {
    const action = currentGroup.status === 'ACTIVE' ? 'deactivate' : 'activate';
    const confirmMsg = currentGroup.status === 'ACTIVE' ? '确定要暂停此场景组吗？' : '确定要激活此场景组吗？';
    
    if (!confirm(confirmMsg)) return;
    
    try {
        const response = await fetch('/api/v1/scene-groups/' + sceneGroupId + '/' + action, {
            method: 'POST'
        });
        const result = await response.json();
        
        if (result.code === 200) {
            currentGroup.status = action === 'activate' ? 'ACTIVE' : 'SUSPENDED';
            renderSceneGroup();
        } else {
            alert('操作失败: ' + result.message);
        }
    } catch (error) {
        console.error('Failed to toggle status:', error);
        alert('操作失败');
    }
}
