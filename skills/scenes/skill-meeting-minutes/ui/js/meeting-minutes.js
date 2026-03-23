const API_BASE = '/api/meeting-minutes';

document.addEventListener('DOMContentLoaded', function() {
    initNavigation();
    initModals();
    initCreateMeeting();
    loadDashboardData();
});

function initNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            navItems.forEach(i => i.classList.remove('active'));
            this.classList.add('active');
            
            const page = this.dataset.page;
            loadPage(page);
        });
    });
}

function loadPage(page) {
    const pageTitle = document.getElementById('page-title');
    const pageContent = document.getElementById('page-content');
    const headerActions = document.querySelector('.header-actions');
    
    switch(page) {
        case 'overview':
            pageTitle.textContent = '会议概览';
            headerActions.innerHTML = `
                <button class="btn btn-primary" id="create-meeting-btn">
                    <i class="ri-add-line"></i>
                    创建会议
                </button>
            `;
            loadDashboardData();
            break;
        case 'create':
            pageTitle.textContent = '创建会议';
            openCreateMeetingModal();
            break;
        case 'minutes':
            pageTitle.textContent = '会议纪要';
            headerActions.innerHTML = '';
            loadMinutesList();
            break;
        case 'actions':
            pageTitle.textContent = '行动项管理';
            headerActions.innerHTML = '';
            loadActionsList();
            break;
        case 'templates':
            pageTitle.textContent = '纪要模板';
            headerActions.innerHTML = `
                <button class="btn btn-primary">
                    <i class="ri-add-line"></i>
                    新建模板
                </button>
            `;
            loadTemplatesList();
            break;
        case 'my-meetings':
            pageTitle.textContent = '我的会议';
            headerActions.innerHTML = '';
            loadMyMeetings();
            break;
        case 'my-minutes':
            pageTitle.textContent = '会议纪要';
            headerActions.innerHTML = '';
            loadMinutesList();
            break;
        case 'my-actions':
            pageTitle.textContent = '我的行动项';
            headerActions.innerHTML = '';
            loadMyActions();
            break;
    }
    
    initCreateMeeting();
}

function initModals() {
    document.querySelectorAll('.modal-close').forEach(btn => {
        btn.addEventListener('click', function() {
            this.closest('.modal').classList.remove('active');
        });
    });
    
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                this.classList.remove('active');
            }
        });
    });
}

function initCreateMeeting() {
    const createBtn = document.getElementById('create-meeting-btn');
    if (createBtn) {
        createBtn.addEventListener('click', openCreateMeetingModal);
    }
    
    const cancelBtn = document.getElementById('cancel-create');
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            document.getElementById('create-meeting-modal').classList.remove('active');
        });
    }
    
    const submitBtn = document.getElementById('submit-create');
    if (submitBtn) {
        submitBtn.addEventListener('click', submitMeeting);
    }
    
    const fileUpload = document.querySelector('.file-upload');
    const fileInput = document.getElementById('meeting-file');
    if (fileUpload && fileInput) {
        fileUpload.addEventListener('click', () => fileInput.click());
        fileUpload.addEventListener('dragover', (e) => {
            e.preventDefault();
            fileUpload.style.borderColor = '#3b82f6';
        });
        fileUpload.addEventListener('dragleave', () => {
            fileUpload.style.borderColor = '#e2e8f0';
        });
        fileUpload.addEventListener('drop', (e) => {
            e.preventDefault();
            fileUpload.style.borderColor = '#e2e8f0';
            handleFiles(e.dataTransfer.files);
        });
        fileInput.addEventListener('change', (e) => {
            handleFiles(e.target.files);
        });
    }
}

function handleFiles(files) {
    const hint = document.querySelector('.file-upload-hint span');
    if (files.length > 0) {
        hint.textContent = `已选择 ${files.length} 个文件`;
    }
}

function openCreateMeetingModal() {
    document.getElementById('create-meeting-modal').classList.add('active');
}

async function submitMeeting() {
    const form = document.getElementById('create-meeting-form');
    const formData = new FormData(form);
    
    const data = {
        title: formData.get('title'),
        time: formData.get('time'),
        duration: formData.get('duration'),
        attendees: formData.get('attendees').split(',').map(s => s.trim()).filter(s => s),
        content: formData.get('content')
    };
    
    try {
        const response = await fetch(`${API_BASE}/organize`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            document.getElementById('create-meeting-modal').classList.remove('active');
            showMinutes(result.data);
        } else {
            alert('创建失败: ' + result.message);
        }
    } catch (error) {
        console.error('Error:', error);
        const mockResult = generateMockMinutes(data);
        showMinutes(mockResult);
        document.getElementById('create-meeting-modal').classList.remove('active');
    }
}

function generateMockMinutes(data) {
    return {
        id: 'MIN-' + Date.now(),
        title: data.title,
        time: data.time,
        duration: data.duration,
        attendees: data.attendees,
        summary: '本次会议主要讨论了相关议题，达成了重要共识。',
        decisions: [
            '确定项目优先级和里程碑',
            '分配任务和责任人',
            '确定下次会议时间'
        ],
        actions: [
            { task: '完成需求文档', assignee: data.attendees[0] || '待分配', deadline: '2026-03-30', status: 'pending' },
            { task: '技术方案设计', assignee: data.attendees[1] || '待分配', deadline: '2026-04-02', status: 'pending' }
        ]
    };
}

function showMinutes(minutes) {
    document.getElementById('minutes-title').textContent = minutes.title;
    document.getElementById('minutes-summary').textContent = minutes.summary;
    
    const decisionsList = document.getElementById('minutes-decisions');
    decisionsList.innerHTML = minutes.decisions.map(d => `<li>${d}</li>`).join('');
    
    const actionsTable = document.getElementById('minutes-actions');
    actionsTable.innerHTML = `
        <thead>
            <tr>
                <th>任务</th>
                <th>负责人</th>
                <th>截止时间</th>
                <th>状态</th>
            </tr>
        </thead>
        <tbody>
            ${minutes.actions.map(a => `
                <tr>
                    <td>${a.task}</td>
                    <td>${a.assignee}</td>
                    <td>${a.deadline}</td>
                    <td><span class="badge badge-info">${a.status === 'pending' ? '待开始' : '进行中'}</span></td>
                </tr>
            `).join('')}
        </tbody>
    `;
    
    document.getElementById('minutes-modal').classList.add('active');
}

async function loadDashboardData() {
    try {
        const response = await fetch(`${API_BASE}/statistics`);
        const result = await response.json();
        
        if (result.status === 'success') {
            updateStatistics(result.data);
        }
    } catch (error) {
        console.error('Error loading dashboard:', error);
    }
}

function updateStatistics(data) {
    document.getElementById('stat-meetings').textContent = data.totalMeetings || 12;
    document.getElementById('stat-minutes').textContent = data.totalMinutes || 8;
    document.getElementById('stat-actions').textContent = data.pendingActions || 15;
    document.getElementById('stat-archived').textContent = data.archivedMinutes || 5;
}

function loadMinutesList() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h3>会议纪要列表</h3>
                <div class="filter-group">
                    <input type="text" placeholder="搜索纪要..." class="search-input">
                    <select class="filter-select">
                        <option value="">全部状态</option>
                        <option value="draft">草稿</option>
                        <option value="published">已发布</option>
                        <option value="archived">已归档</option>
                    </select>
                </div>
            </div>
            <div class="card-body">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>会议主题</th>
                            <th>会议时间</th>
                            <th>参会人数</th>
                            <th>行动项</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody id="minutes-table-body">
                        <tr>
                            <td>产品需求评审会</td>
                            <td>2026-03-23 14:00</td>
                            <td>8人</td>
                            <td>3项</td>
                            <td><span class="badge badge-success">已发布</span></td>
                            <td>
                                <button class="btn-icon" title="查看"><i class="ri-eye-line"></i></button>
                                <button class="btn-icon" title="编辑"><i class="ri-edit-line"></i></button>
                                <button class="btn-icon" title="导出"><i class="ri-download-line"></i></button>
                            </td>
                        </tr>
                        <tr>
                            <td>技术方案讨论</td>
                            <td>2026-03-22 10:00</td>
                            <td>5人</td>
                            <td>2项</td>
                            <td><span class="badge badge-warning">草稿</span></td>
                            <td>
                                <button class="btn-icon" title="查看"><i class="ri-eye-line"></i></button>
                                <button class="btn-icon" title="编辑"><i class="ri-edit-line"></i></button>
                                <button class="btn-icon" title="导出"><i class="ri-download-line"></i></button>
                            </td>
                        </tr>
                        <tr>
                            <td>周例会</td>
                            <td>2026-03-21 09:00</td>
                            <td>12人</td>
                            <td>5项</td>
                            <td><span class="badge badge-info">已归档</span></td>
                            <td>
                                <button class="btn-icon" title="查看"><i class="ri-eye-line"></i></button>
                                <button class="btn-icon" title="编辑"><i class="ri-edit-line"></i></button>
                                <button class="btn-icon" title="导出"><i class="ri-download-line"></i></button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

function loadActionsList() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h3>行动项管理</h3>
                <div class="filter-group">
                    <select class="filter-select">
                        <option value="">全部状态</option>
                        <option value="pending">待开始</option>
                        <option value="in_progress">进行中</option>
                        <option value="completed">已完成</option>
                    </select>
                </div>
            </div>
            <div class="card-body">
                <div class="action-list-full">
                    <div class="action-item-full">
                        <div class="action-check">
                            <input type="checkbox">
                        </div>
                        <div class="action-details">
                            <div class="action-title">完成产品PRD文档</div>
                            <div class="action-desc">根据会议讨论结果，完成V2.0版本的产品需求文档</div>
                            <div class="action-meta">
                                <span class="meeting-link">来自: 产品需求评审会</span>
                                <span class="assignee"><i class="ri-user-line"></i> 张三</span>
                                <span class="deadline"><i class="ri-time-line"></i> 截止: 2026-03-25</span>
                            </div>
                        </div>
                        <div class="action-status">
                            <span class="badge badge-warning">进行中</span>
                        </div>
                    </div>
                    <div class="action-item-full">
                        <div class="action-check">
                            <input type="checkbox">
                        </div>
                        <div class="action-details">
                            <div class="action-title">技术方案设计</div>
                            <div class="action-desc">设计用户中心重构的技术方案</div>
                            <div class="action-meta">
                                <span class="meeting-link">来自: 产品需求评审会</span>
                                <span class="assignee"><i class="ri-user-line"></i> 李四</span>
                                <span class="deadline"><i class="ri-time-line"></i> 截止: 2026-03-28</span>
                            </div>
                        </div>
                        <div class="action-status">
                            <span class="badge badge-info">待开始</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function loadTemplatesList() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="templates-grid">
            <div class="template-card">
                <div class="template-icon"><i class="ri-file-text-line"></i></div>
                <div class="template-name">标准会议纪要</div>
                <div class="template-desc">适用于一般会议，包含摘要、决策、行动项</div>
                <div class="template-actions">
                    <button class="btn btn-secondary btn-sm">使用</button>
                    <button class="btn btn-secondary btn-sm">编辑</button>
                </div>
            </div>
            <div class="template-card">
                <div class="template-icon"><i class="ri-team-line"></i></div>
                <div class="template-name">周例会模板</div>
                <div class="template-desc">适用于团队周例会，包含上周总结、本周计划</div>
                <div class="template-actions">
                    <button class="btn btn-secondary btn-sm">使用</button>
                    <button class="btn btn-secondary btn-sm">编辑</button>
                </div>
            </div>
            <div class="template-card">
                <div class="template-icon"><i class="ri-lightbulb-line"></i></div>
                <div class="template-name">头脑风暴模板</div>
                <div class="template-desc">适用于创意讨论会议，包含想法收集、投票结果</div>
                <div class="template-actions">
                    <button class="btn btn-secondary btn-sm">使用</button>
                    <button class="btn btn-secondary btn-sm">编辑</button>
                </div>
            </div>
        </div>
    `;
}

function loadMyMeetings() {
    loadMinutesList();
}

function loadMyActions() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h3>我的行动项</h3>
            </div>
            <div class="card-body">
                <div class="action-list-full">
                    <div class="action-item-full">
                        <div class="action-check">
                            <input type="checkbox">
                        </div>
                        <div class="action-details">
                            <div class="action-title">完成产品PRD文档</div>
                            <div class="action-desc">根据会议讨论结果，完成V2.0版本的产品需求文档</div>
                            <div class="action-meta">
                                <span class="meeting-link">来自: 产品需求评审会</span>
                                <span class="deadline"><i class="ri-time-line"></i> 截止: 2026-03-25</span>
                            </div>
                        </div>
                        <div class="action-status">
                            <select class="status-select">
                                <option value="pending">待开始</option>
                                <option value="in_progress" selected>进行中</option>
                                <option value="completed">已完成</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}
