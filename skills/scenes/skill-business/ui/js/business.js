const API_BASE = '/api/business';

document.addEventListener('DOMContentLoaded', function() {
    initNavigation();
    initModals();
    initCreateScenario();
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
    const headerActions = document.querySelector('.header-actions');
    
    switch(page) {
        case 'overview':
            pageTitle.textContent = '业务概览';
            headerActions.innerHTML = `
                <button class="btn btn-primary" id="create-scenario-btn">
                    <i class="ri-add-line"></i>
                    创建场景
                </button>
            `;
            loadDashboardData();
            break;
        case 'scenarios':
            pageTitle.textContent = '场景管理';
            headerActions.innerHTML = `
                <button class="btn btn-primary" id="create-scenario-btn">
                    <i class="ri-add-line"></i>
                    创建场景
                </button>
            `;
            loadScenariosList();
            break;
        case 'workflows':
            pageTitle.textContent = '工作流管理';
            headerActions.innerHTML = `
                <button class="btn btn-primary">
                    <i class="ri-add-line"></i>
                    创建工作流
                </button>
            `;
            loadWorkflowsList();
            break;
        case 'monitoring':
            pageTitle.textContent = '流程监控';
            headerActions.innerHTML = '';
            loadMonitoringPage();
            break;
        case 'settings':
            pageTitle.textContent = '场景设置';
            headerActions.innerHTML = '';
            loadSettingsPage();
            break;
        case 'my-tasks':
            pageTitle.textContent = '我的任务';
            headerActions.innerHTML = '';
            loadMyTasks();
            break;
        case 'my-workflows':
            pageTitle.textContent = '我的流程';
            headerActions.innerHTML = '';
            loadMyWorkflows();
            break;
        case 'view-scenarios':
            pageTitle.textContent = '场景列表';
            headerActions.innerHTML = '';
            loadScenariosList();
            break;
        case 'view-workflows':
            pageTitle.textContent = '流程查看';
            headerActions.innerHTML = '';
            loadWorkflowsList();
            break;
    }
    
    initCreateScenario();
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

function initCreateScenario() {
    const createBtn = document.getElementById('create-scenario-btn');
    if (createBtn) {
        createBtn.addEventListener('click', function() {
            document.getElementById('create-scenario-modal').classList.add('active');
        });
    }
    
    const cancelBtn = document.getElementById('cancel-create');
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            document.getElementById('create-scenario-modal').classList.remove('active');
        });
    }
    
    const submitBtn = document.getElementById('submit-create');
    if (submitBtn) {
        submitBtn.addEventListener('click', submitScenario);
    }
}

async function submitScenario() {
    const form = document.getElementById('create-scenario-form');
    const formData = new FormData(form);
    
    const triggers = [];
    document.querySelectorAll('input[name="triggers"]:checked').forEach(cb => {
        triggers.push(cb.value);
    });
    
    const data = {
        name: formData.get('name'),
        description: formData.get('description'),
        type: formData.get('type'),
        triggers: triggers
    };
    
    try {
        const response = await fetch(`${API_BASE}/scenario/create`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            document.getElementById('create-scenario-modal').classList.remove('active');
            form.reset();
            loadDashboardData();
        } else {
            alert('创建失败: ' + result.message);
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('create-scenario-modal').classList.remove('active');
        form.reset();
    }
}

async function loadDashboardData() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="overview-page">
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon" style="background: #3b82f6;">
                        <i class="ri-layout-line"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">8</div>
                        <div class="stat-label">业务场景</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background: #22c55e;">
                        <i class="ri-flow-chart"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">24</div>
                        <div class="stat-label">工作流</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background: #f59e0b;">
                        <i class="ri-play-line"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">12</div>
                        <div class="stat-label">运行中</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background: #8b5cf6;">
                        <i class="ri-checkbox-circle-line"></i>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">156</div>
                        <div class="stat-label">已完成</div>
                    </div>
                </div>
            </div>
            
            <div class="content-grid">
                <div class="card">
                    <div class="card-header">
                        <h3>活跃场景</h3>
                        <a href="#" class="link-more">查看全部</a>
                    </div>
                    <div class="card-body">
                        <div class="scenario-list">
                            <div class="scenario-item">
                                <div class="scenario-icon" style="background: #dbeafe; color: #2563eb;">
                                    <i class="ri-shopping-cart-line"></i>
                                </div>
                                <div class="scenario-info">
                                    <div class="scenario-name">订单处理流程</div>
                                    <div class="scenario-meta">
                                        <span>5个工作流</span>
                                        <span>运行中: 3</span>
                                    </div>
                                </div>
                                <div class="scenario-status">
                                    <span class="status-dot active"></span>
                                    活跃
                                </div>
                            </div>
                            <div class="scenario-item">
                                <div class="scenario-icon" style="background: #dcfce7; color: #16a34a;">
                                    <i class="ri-user-add-line"></i>
                                </div>
                                <div class="scenario-info">
                                    <div class="scenario-name">客户入驻流程</div>
                                    <div class="scenario-meta">
                                        <span>3个工作流</span>
                                        <span>运行中: 2</span>
                                    </div>
                                </div>
                                <div class="scenario-status">
                                    <span class="status-dot active"></span>
                                    活跃
                                </div>
                            </div>
                            <div class="scenario-item">
                                <div class="scenario-icon" style="background: #fef3c7; color: #d97706;">
                                    <i class="ri-file-list-3-line"></i>
                                </div>
                                <div class="scenario-info">
                                    <div class="scenario-name">合同审批流程</div>
                                    <div class="scenario-meta">
                                        <span>4个工作流</span>
                                        <span>运行中: 5</span>
                                    </div>
                                </div>
                                <div class="scenario-status">
                                    <span class="status-dot active"></span>
                                    活跃
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="card">
                    <div class="card-header">
                        <h3>最近工作流</h3>
                        <a href="#" class="link-more">查看全部</a>
                    </div>
                    <div class="card-body">
                        <div class="workflow-list">
                            <div class="workflow-item">
                                <div class="workflow-info">
                                    <div class="workflow-name">订单 #12345 处理</div>
                                    <div class="workflow-meta">
                                        <span>订单处理流程</span>
                                        <span>2026-03-23 14:30</span>
                                    </div>
                                </div>
                                <div class="workflow-progress">
                                    <div class="progress-bar">
                                        <div class="progress-fill" style="width: 60%;"></div>
                                    </div>
                                    <span class="progress-text">60%</span>
                                </div>
                            </div>
                            <div class="workflow-item">
                                <div class="workflow-info">
                                    <div class="workflow-name">客户 ABC 入驻</div>
                                    <div class="workflow-meta">
                                        <span>客户入驻流程</span>
                                        <span>2026-03-23 13:00</span>
                                    </div>
                                </div>
                                <div class="workflow-progress">
                                    <div class="progress-bar">
                                        <div class="progress-fill" style="width: 80%;"></div>
                                    </div>
                                    <span class="progress-text">80%</span>
                                </div>
                            </div>
                            <div class="workflow-item">
                                <div class="workflow-info">
                                    <div class="workflow-name">合同审批 #C20260323</div>
                                    <div class="workflow-meta">
                                        <span>合同审批流程</span>
                                        <span>2026-03-23 10:00</span>
                                    </div>
                                </div>
                                <div class="workflow-progress">
                                    <div class="progress-bar">
                                        <div class="progress-fill complete" style="width: 100%;"></div>
                                    </div>
                                    <span class="progress-text">完成</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function loadScenariosList() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h3>场景列表</h3>
                <div class="filter-group">
                    <input type="text" placeholder="搜索场景..." class="search-input">
                    <select class="filter-select">
                        <option value="">全部类型</option>
                        <option value="order">订单处理</option>
                        <option value="approval">审批流程</option>
                        <option value="customer">客户管理</option>
                    </select>
                </div>
            </div>
            <div class="card-body">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>场景名称</th>
                            <th>类型</th>
                            <th>工作流数</th>
                            <th>状态</th>
                            <th>创建时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>订单处理流程</td>
                            <td>订单处理</td>
                            <td>5</td>
                            <td><span class="status-dot active"></span> 活跃</td>
                            <td>2026-03-20</td>
                            <td>
                                <button class="btn-icon" title="编辑"><i class="ri-edit-line"></i></button>
                                <button class="btn-icon" title="配置"><i class="ri-settings-line"></i></button>
                                <button class="btn-icon" title="删除"><i class="ri-delete-bin-line"></i></button>
                            </td>
                        </tr>
                        <tr>
                            <td>客户入驻流程</td>
                            <td>客户管理</td>
                            <td>3</td>
                            <td><span class="status-dot active"></span> 活跃</td>
                            <td>2026-03-18</td>
                            <td>
                                <button class="btn-icon" title="编辑"><i class="ri-edit-line"></i></button>
                                <button class="btn-icon" title="配置"><i class="ri-settings-line"></i></button>
                                <button class="btn-icon" title="删除"><i class="ri-delete-bin-line"></i></button>
                            </td>
                        </tr>
                        <tr>
                            <td>合同审批流程</td>
                            <td>审批流程</td>
                            <td>4</td>
                            <td><span class="status-dot active"></span> 活跃</td>
                            <td>2026-03-15</td>
                            <td>
                                <button class="btn-icon" title="编辑"><i class="ri-edit-line"></i></button>
                                <button class="btn-icon" title="配置"><i class="ri-settings-line"></i></button>
                                <button class="btn-icon" title="删除"><i class="ri-delete-bin-line"></i></button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

function loadWorkflowsList() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h3>工作流列表</h3>
                <div class="filter-group">
                    <input type="text" placeholder="搜索工作流..." class="search-input">
                    <select class="filter-select">
                        <option value="">全部状态</option>
                        <option value="running">运行中</option>
                        <option value="completed">已完成</option>
                        <option value="failed">失败</option>
                    </select>
                </div>
            </div>
            <div class="card-body">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>工作流名称</th>
                            <th>所属场景</th>
                            <th>进度</th>
                            <th>状态</th>
                            <th>开始时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>订单 #12345 处理</td>
                            <td>订单处理流程</td>
                            <td>
                                <div class="workflow-progress">
                                    <div class="progress-bar">
                                        <div class="progress-fill" style="width: 60%;"></div>
                                    </div>
                                    <span class="progress-text">60%</span>
                                </div>
                            </td>
                            <td><span class="status-dot active"></span> 运行中</td>
                            <td>2026-03-23 14:30</td>
                            <td>
                                <button class="btn-icon" title="查看"><i class="ri-eye-line"></i></button>
                                <button class="btn-icon" title="暂停"><i class="ri-pause-line"></i></button>
                            </td>
                        </tr>
                        <tr>
                            <td>客户 ABC 入驻</td>
                            <td>客户入驻流程</td>
                            <td>
                                <div class="workflow-progress">
                                    <div class="progress-bar">
                                        <div class="progress-fill" style="width: 80%;"></div>
                                    </div>
                                    <span class="progress-text">80%</span>
                                </div>
                            </td>
                            <td><span class="status-dot active"></span> 运行中</td>
                            <td>2026-03-23 13:00</td>
                            <td>
                                <button class="btn-icon" title="查看"><i class="ri-eye-line"></i></button>
                                <button class="btn-icon" title="暂停"><i class="ri-pause-line"></i></button>
                            </td>
                        </tr>
                        <tr>
                            <td>合同审批 #C20260323</td>
                            <td>合同审批流程</td>
                            <td>
                                <div class="workflow-progress">
                                    <div class="progress-bar">
                                        <div class="progress-fill complete" style="width: 100%;"></div>
                                    </div>
                                    <span class="progress-text">完成</span>
                                </div>
                            </td>
                            <td><span class="status-dot active" style="background: #22c55e;"></span> 已完成</td>
                            <td>2026-03-23 10:00</td>
                            <td>
                                <button class="btn-icon" title="查看"><i class="ri-eye-line"></i></button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

function loadMonitoringPage() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h3>流程监控</h3>
            </div>
            <div class="card-body">
                <div class="workflow-designer">
                    <h4>实时监控</h4>
                    <p>流程监控功能正在开发中...</p>
                </div>
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
                        <label>默认触发方式</label>
                        <select class="filter-select">
                            <option value="manual">手动触发</option>
                            <option value="api">API触发</option>
                            <option value="schedule">定时触发</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>通知设置</label>
                        <div class="checkbox-group">
                            <label class="checkbox-label">
                                <input type="checkbox" checked>
                                流程完成通知
                            </label>
                            <label class="checkbox-label">
                                <input type="checkbox" checked>
                                流程失败通知
                            </label>
                            <label class="checkbox-label">
                                <input type="checkbox">
                                每日汇总报告
                            </label>
                        </div>
                    </div>
                    <button class="btn btn-primary" type="button">保存设置</button>
                </form>
            </div>
        </div>
    `;
}

function loadMyTasks() {
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `
        <div class="card">
            <div class="card-header">
                <h3>我的任务</h3>
            </div>
            <div class="card-body">
                <div class="workflow-list">
                    <div class="workflow-item">
                        <div class="workflow-info">
                            <div class="workflow-name">审核订单 #12345</div>
                            <div class="workflow-meta">
                                <span>订单处理流程</span>
                                <span>待处理</span>
                            </div>
                        </div>
                        <button class="btn btn-primary btn-sm">处理</button>
                    </div>
                    <div class="workflow-item">
                        <div class="workflow-info">
                            <div class="workflow-name">确认客户信息</div>
                            <div class="workflow-meta">
                                <span>客户入驻流程</span>
                                <span>待处理</span>
                            </div>
                        </div>
                        <button class="btn btn-primary btn-sm">处理</button>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function loadMyWorkflows() {
    loadWorkflowsList();
}
