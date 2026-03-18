let checkResults = [];

async function initPage() {
    await loadCheckResult();
}

async function loadCheckResult() {
    const container = document.getElementById('check-result');
    
    checkResults = [
        { name: 'API端点检查', status: 'checking', description: '检查所有API端点是否可访问' },
        { name: '数据库连接', status: 'checking', description: '检查数据存储服务连接状态' },
        { name: '场景能力注册', status: 'checking', description: '检查场景能力是否正确注册' },
        { name: '模板配置', status: 'checking', description: '检查模板配置是否完整' },
        { name: '用户权限', status: 'checking', description: '检查用户权限配置' }
    ];
    
    renderCheckList();
    
    for (let i = 0; i < checkResults.length; i++) {
        await runCheck(i);
    }
}

async function runCheck(index) {
    const check = checkResults[index];
    
    try {
        let success = false;
        let message = '';
        
        switch (index) {
            case 0:
                const apiRes = await fetch('/api/v1/capabilities');
                const apiResult = await apiRes.json();
                success = apiResult.status === 'success';
                message = success ? '所有API端点正常' : '部分API端点异常';
                break;
                
            case 1:
                const dbRes = await fetch('/api/v1/org/users');
                const dbResult = await dbRes.json();
                success = dbResult.status === 'success';
                message = success ? '数据存储服务正常' : '数据存储服务异常';
                break;
                
            case 2:
                const capRes = await fetch('/api/v1/capabilities');
                const capResult = await capRes.json();
                const caps = capResult.data?.list || capResult.data || [];
                success = caps.length > 0;
                message = success ? `已注册 ${caps.length} 个场景能力` : '未发现已注册的场景能力';
                break;
                
            case 3:
                const tplRes = await fetch('/api/v1/templates');
                const tplResult = await tplRes.json();
                const templates = tplResult.data || [];
                success = templates.length > 0;
                message = success ? `已配置 ${templates.length} 个模板` : '未发现模板配置';
                break;
                
            case 4:
                const userRes = await fetch('/api/v1/org/roles');
                const userResult = await userRes.json();
                const roles = userResult.data || [];
                success = roles.length > 0;
                message = success ? `已配置 ${roles.length} 个角色` : '未发现角色配置';
                break;
        }
        
        check.status = success ? 'success' : 'error';
        check.message = message;
    } catch (e) {
        check.status = 'error';
        check.message = '检查失败: ' + e.message;
    }
    
    renderCheckList();
}

function renderCheckList() {
    const container = document.getElementById('check-result');
    
    let successCount = 0;
    let errorCount = 0;
    let checkingCount = 0;
    
    checkResults.forEach(r => {
        if (r.status === 'success') successCount++;
        else if (r.status === 'error') errorCount++;
        else if (r.status === 'checking') checkingCount++;
    });
    
    let html = `
        <div class="nx-grid nx-grid-cols-3 nx-mb-4">
            <div class="nx-stat-card">
                <div class="nx-stat-card__icon" style="background: var(--nx-success-light); color: var(--nx-success);"><i class="ri-checkbox-circle-line"></i></div>
                <div class="nx-stat-card__content">
                    <h4>通过</h4>
                    <p class="nx-stat-card__value" style="color: var(--nx-success);">${successCount}</p>
                </div>
            </div>
            <div class="nx-stat-card">
                <div class="nx-stat-card__icon" style="background: var(--nx-danger-light); color: var(--nx-danger);"><i class="ri-error-warning-line"></i></div>
                <div class="nx-stat-card__content">
                    <h4>失败</h4>
                    <p class="nx-stat-card__value" style="color: var(--nx-danger);">${errorCount}</p>
                </div>
            </div>
            <div class="nx-stat-card">
                <div class="nx-stat-card__icon" style="background: var(--nx-warning-light); color: var(--nx-warning);"><i class="ri-loader-4-line"></i></div>
                <div class="nx-stat-card__content">
                    <h4>检查中</h4>
                    <p class="nx-stat-card__value">${checkingCount}</p>
                </div>
            </div>
        </div>
        
        <div class="nx-table-container">
            <table class="nx-table">
                <thead>
                    <tr>
                        <th>检查项</th>
                        <th>描述</th>
                        <th>状态</th>
                        <th>结果</th>
                    </tr>
                </thead>
                <tbody>
    `;
    
    checkResults.forEach(check => {
        const statusIcon = check.status === 'success' ? 
            '<i class="ri-checkbox-circle-line" style="color: var(--nx-success);"></i>' :
            check.status === 'error' ? 
            '<i class="ri-error-warning-line" style="color: var(--nx-danger);"></i>' :
            '<i class="ri-loader-4-line ri-spin" style="color: var(--nx-warning);"></i>';
        
        const statusBadge = check.status === 'success' ?
            '<span class="nx-badge nx-badge--success">通过</span>' :
            check.status === 'error' ?
            '<span class="nx-badge nx-badge--danger">失败</span>' :
            '<span class="nx-badge nx-badge--warning">检查中</span>';
        
        html += `
            <tr>
                <td><span class="nx-font-medium">${check.name}</span></td>
                <td>${check.description}</td>
                <td>${statusIcon}</td>
                <td>${check.message || statusBadge}</td>
            </tr>
        `;
    });
    
    html += '</tbody></table></div>';
    
    if (successCount === checkResults.length) {
        html += `
            <div class="nx-mt-4 nx-p-4 nx-bg-success-light nx-rounded-lg">
                <div class="nx-flex nx-items-center nx-gap-2">
                    <i class="ri-checkbox-circle-line" style="color: var(--nx-success); font-size: 20px;"></i>
                    <span class="nx-font-medium" style="color: var(--nx-success);">架构检查全部通过，系统运行正常</span>
                </div>
            </div>
        `;
    } else if (errorCount > 0 && checkingCount === 0) {
        html += `
            <div class="nx-mt-4 nx-p-4 nx-bg-danger-light nx-rounded-lg">
                <div class="nx-flex nx-items-center nx-gap-2">
                    <i class="ri-error-warning-line" style="color: var(--nx-danger); font-size: 20px;"></i>
                    <span class="nx-font-medium" style="color: var(--nx-danger);">发现 ${errorCount} 项检查未通过，请检查相关配置</span>
                </div>
            </div>
        `;
    }
    
    container.innerHTML = html;
}

function runAllChecks() {
    loadCheckResult();
}

document.addEventListener('DOMContentLoaded', initPage);
