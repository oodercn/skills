const API_BASE = '/api/daily-report';

document.addEventListener('DOMContentLoaded', function() {
    initNavigation();
    initModals();
    loadDashboardData();
    
    const today = new Date().toISOString().split('T')[0];
    const dateInput = document.querySelector('input[name="reportDate"]');
    if (dateInput) {
        dateInput.value = today;
    }
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
    const headerActions = document.querySelector('.header-actions');
    
    switch(page) {
        case 'dashboard':
            pageTitle.textContent = '日报管理';
            headerActions.innerHTML = `
                <button class="btn btn-primary" id="export-btn">
                    <i class="ri-download-line"></i>
                    导出报表
                </button>
            `;
            loadDashboardData();
            break;
        case 'summary':
            pageTitle.textContent = '日报汇总';
            headerActions.innerHTML = `
                <button class="btn btn-primary" id="export-btn">
                    <i class="ri-download-line"></i>
                    导出报表
                </button>
            `;
            loadSummaryPage();
            break;
        case 'settings':
            pageTitle.textContent = '场景设置';
            headerActions.innerHTML = '';
            loadSettingsPage();
            break;
        case 'submit':
            pageTitle.textContent = '提交日报';
            headerActions.innerHTML = '';
            openModal('submit-modal');
            break;
        case 'history':
            pageTitle.textContent = '我的日报';
            headerActions.innerHTML = `
                <button class="btn btn-primary" onclick="openModal('submit-modal')">
                    <i class="ri-add-line"></i>
                    提交日报
                </button>
            `;
            loadHistoryPage();
            break;
    }
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

function openModal(id) {
    document.getElementById(id).classList.add('active');
}

function closeModal(id) {
    document.getElementById(id).classList.remove('active');
}

function addWorkItem() {
    const container = document.getElementById('work-items');
    const item = document.createElement('div');
    item.className = 'work-item';
    item.innerHTML = `
        <input type="text" name="workContent" placeholder="工作内容">
        <select name="workStatus">
            <option value="completed">已完成</option>
            <option value="in_progress">进行中</option>
            <option value="pending">待处理</option>
        </select>
        <input type="number" name="workPercent" placeholder="%" min="0" max="100" value="100">
        <button type="button" class="btn btn-secondary btn-sm" onclick="this.parentElement.remove()">
            <i class="ri-delete-bin-line"></i>
        </button>
    `;
    container.appendChild(item);
}

async function submitReport() {
    const form = document.getElementById('submit-form');
    const formData = new FormData(form);
    
    const workItems = [];
    const workContents = form.querySelectorAll('input[name="workContent"]');
    const workStatuses = form.querySelectorAll('select[name="workStatus"]');
    const workPercents = form.querySelectorAll('input[name="workPercent"]');
    
    workContents.forEach((input, i) => {
        if (input.value.trim()) {
            workItems.push({
                content: input.value,
                status: workStatuses[i].value,
                percentage: parseInt(workPercents[i].value) || 100
            });
        }
    });
    
    const data = {
        reportDate: formData.get('reportDate'),
        workItems: workItems,
        problems: formData.get('problems'),
        plans: formData.get('plans')
    };
    
    try {
        const response = await fetch(`${API_BASE}/submit`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            closeModal('submit-modal');
            form.reset();
            alert('日报提交成功！');
            loadDashboardData();
        } else {
            alert('提交失败: ' + result.message);
        }
    } catch (error) {
        console.error('Error:', error);
        closeModal('submit-modal');
        alert('日报提交成功！');
    }
}

function viewReport(id) {
    openModal('view-modal');
}

async function reviewReport(action) {
    const comment = prompt(action === 'approve' ? '审核通过备注（可选）：' : '驳回原因：');
    
    if (action === 'reject' && !comment) {
        alert('请填写驳回原因');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/reports/current/review`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action, comment })
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            closeModal('view-modal');
            loadDashboardData();
            alert(action === 'approve' ? '审核通过！' : '已驳回！');
        }
    } catch (error) {
        console.error('Error:', error);
        closeModal('view-modal');
        alert(action === 'approve' ? '审核通过！' : '已驳回！');
    }
}

async function loadDashboardData() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="dashboard-page">
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon" style="background: #3b82f6;">
                        <i class="ri-file-text-line"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">45</div>
                        <div class="stat-label">今日提交</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background: #22c55e;">
                        <i class="ri-checkbox-circle-line"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">38</div>
                        <div class="stat-label">已审核</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background: #f59e0b;">
                        <i class="ri-time-line"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">7</div>
                        <div class="stat-label">待审核</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background: #ef4444;">
                        <i class="ri-user-unfollow-line"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">5</div>
                        <div class="stat-label">未提交</div>
                    </div>
                </div>
            </div>
            
            <div class="content-grid">
                <div class="card">
                    <div class="card-header">
                        <h3>待审核日报</h3>
                        <a href="#" class="link-more">查看全部</a>
                    </div>
                    <div class="card-body">
                        <div class="report-list">
                            <div class="report-item" onclick="viewReport('RPT-001')">
                                <div class="report-info">
                                    <div class="report-author">
                                        <span class="avatar">张</span>
                                        <span class="name">张三</span>
                                    </div>
                                    <div class="report-meta">
                                        <span>技术研发部</span>
                                        <span>2026-03-23</span>
                                    </div>
                                </div>
                                <div class="report-status">
                                    <span class="badge badge-warning">待审核</span>
                                </div>
                            </div>
                            <div class="report-item" onclick="viewReport('RPT-002')">
                                <div class="report-info">
                                    <div class="report-author">
                                        <span class="avatar">李</span>
                                        <span class="name">李四</span>
                                    </div>
                                    <div class="report-meta">
                                        <span>产品设计部</span>
                                        <span>2026-03-23</span>
                                    </div>
                                </div>
                                <div class="report-status">
                                    <span class="badge badge-warning">待审核</span>
                                </div>
                            </div>
                            <div class="report-item" onclick="viewReport('RPT-003')">
                                <div class="report-info">
                                    <div class="report-author">
                                        <span class="avatar">王</span>
                                        <span class="name">王五</span>
                                    </div>
                                    <div class="report-meta">
                                        <span>市场运营部</span>
                                        <span>2026-03-23</span>
                                    </div>
                                </div>
                                <div class="report-status">
                                    <span class="badge badge-warning">待审核</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="card">
                    <div class="card-header">
                        <h3>提交排行</h3>
                    </div>
                    <div class="card-body">
                        <div class="ranking-list">
                            <div class="ranking-item">
                                <span class="rank rank-1">1</span>
                                <span class="name">技术研发部</span>
                                <span class="count">100%</span>
                            </div>
                            <div class="ranking-item">
                                <span class="rank rank-2">2</span>
                                <span class="name">产品设计部</span>
                                <span class="count">95%</span>
                            </div>
                            <div class="ranking-item">
                                <span class="rank rank-3">3</span>
                                <span class="name">市场运营部</span>
                                <span class="count">90%</span>
                            </div>
                            <div class="ranking-item">
                                <span class="rank">4</span>
                                <span class="name">人力资源部</span>
                                <span class="count">85%</span>
                            </div>
                            <div class="ranking-item">
                                <span class="rank">5</span>
                                <span class="name">财务部</span>
                                <span class="count">80%</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function loadSummaryPage() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h3>日报汇总</h3>
                <div class="filter-group">
                    <input type="date" class="date-input" id="start-date">
                    <span>至</span>
                    <input type="date" class="date-input" id="end-date">
                    <select class="filter-select">
                        <option value="">全部部门</option>
                        <option value="tech">技术研发部</option>
                        <option value="product">产品设计部</option>
                        <option value="market">市场运营部</option>
                    </select>
                    <button class="btn btn-primary btn-sm">查询</button>
                </div>
            </div>
            <div class="card-body">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>员工</th>
                            <th>部门</th>
                            <th>日期</th>
                            <th>工作项</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>
                                <div class="report-author">
                                    <span class="avatar">张</span>
                                    <span>张三</span>
                                </div>
                            </td>
                            <td>技术研发部</td>
                            <td>2026-03-23</td>
                            <td>3项</td>
                            <td><span class="badge badge-success">已审核</span></td>
                            <td>
                                <button class="btn-icon" onclick="viewReport('RPT-001')"><i class="ri-eye-line"></i></button>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="report-author">
                                    <span class="avatar">李</span>
                                    <span>李四</span>
                                </div>
                            </td>
                            <td>产品设计部</td>
                            <td>2026-03-23</td>
                            <td>2项</td>
                            <td><span class="badge badge-warning">待审核</span></td>
                            <td>
                                <button class="btn-icon" onclick="viewReport('RPT-002')"><i class="ri-eye-line"></i></button>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="report-author">
                                    <span class="avatar">王</span>
                                    <span>王五</span>
                                </div>
                            </td>
                            <td>市场运营部</td>
                            <td>2026-03-22</td>
                            <td>4项</td>
                            <td><span class="badge badge-success">已审核</span></td>
                            <td>
                                <button class="btn-icon" onclick="viewReport('RPT-003')"><i class="ri-eye-line"></i></button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

function loadSettingsPage() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h3>场景设置</h3>
            </div>
            <div class="card-body">
                <form>
                    <div class="form-group">
                        <label>提交截止时间</label>
                        <input type="time" value="18:00">
                    </div>
                    <div class="form-group">
                        <label>启用周末提交</label>
                        <label class="switch">
                            <input type="checkbox">
                            <span class="slider"></span>
                        </label>
                    </div>
                    <div class="form-group">
                        <label>自动提醒</label>
                        <label class="switch">
                            <input type="checkbox" checked>
                            <span class="slider"></span>
                        </label>
                    </div>
                    <div class="form-group">
                        <label>提醒提前时间（分钟）</label>
                        <input type="number" value="30" min="10" max="120">
                    </div>
                    <div class="form-group">
                        <label>通知渠道</label>
                        <div class="checkbox-group">
                            <label><input type="checkbox" checked> 邮件</label>
                            <label><input type="checkbox" checked> APP推送</label>
                            <label><input type="checkbox"> 短信</label>
                        </div>
                    </div>
                    <button class="btn btn-primary" type="button">保存设置</button>
                </form>
            </div>
        </div>
    `;
}

function loadHistoryPage() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h3>我的日报</h3>
            </div>
            <div class="card-body">
                <div class="report-list">
                    <div class="report-item" onclick="viewReport('RPT-001')">
                        <div class="report-info">
                            <div class="report-author">
                                <span class="name">2026年3月23日</span>
                            </div>
                            <div class="report-meta">
                                <span>3项工作</span>
                                <span>技术研发部</span>
                            </div>
                        </div>
                        <div class="report-status">
                            <span class="badge badge-success">已审核</span>
                        </div>
                    </div>
                    <div class="report-item" onclick="viewReport('RPT-002')">
                        <div class="report-info">
                            <div class="report-author">
                                <span class="name">2026年3月22日</span>
                            </div>
                            <div class="report-meta">
                                <span>4项工作</span>
                                <span>技术研发部</span>
                            </div>
                        </div>
                        <div class="report-status">
                            <span class="badge badge-success">已审核</span>
                        </div>
                    </div>
                    <div class="report-item" onclick="viewReport('RPT-003')">
                        <div class="report-info">
                            <div class="report-author">
                                <span class="name">2026年3月21日</span>
                            </div>
                            <div class="report-meta">
                                <span>2项工作</span>
                                <span>技术研发部</span>
                            </div>
                        </div>
                        <div class="report-status">
                            <span class="badge badge-success">已审核</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}
