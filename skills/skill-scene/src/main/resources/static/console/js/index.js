/**
 * 主页面脚本
 * 处理页面类型选择和导航功能
 */

window.onload = function() {
    initThemeToggle();
    checkSavedPageType();
    loadRecentAccess();
};

function initThemeToggle() {
    const themeToggle = document.getElementById('theme-toggle');
    if (themeToggle) {
        themeToggle.addEventListener('click', function() {
            document.documentElement.classList.toggle('light-theme');
            const isLight = document.documentElement.classList.contains('light-theme');
            this.innerHTML = isLight 
                ? '<i class="ri-moon-line"></i> 深色模式'
                : '<i class="ri-sun-line"></i> 浅色模式';
            localStorage.setItem('theme', isLight ? 'light' : 'dark');
        });
        
        const savedTheme = localStorage.getItem('theme');
        if (savedTheme === 'light') {
            document.documentElement.classList.add('light-theme');
            themeToggle.innerHTML = '<i class="ri-moon-line"></i> 深色模式';
        }
    }
}

function checkSavedPageType() {
    const savedPageType = localStorage.getItem('skillcenter_page_type');
    if (savedPageType) {
        let role = 'personal';
        switch(savedPageType) {
            case 'dashboard':
            case 'personal':
            case 'market':
                role = 'personal';
                break;
            case 'admin':
                role = 'admin';
                break;
        }
        localStorage.setItem('currentRole', role);
        
        if (confirm('检测到您上次使用的是' + getPageTypeName(savedPageType) + '页面，是否直接进入？')) {
            navigateToPage(savedPageType);
        }
    }
}

function loadRecentAccess() {
    const recentAccess = localStorage.getItem('skillcenter_recent_access');
    const recentAccessEl = document.getElementById('recent-access');
    
    if (!recentAccessEl) return;
    
    if (recentAccess) {
        const recentPages = JSON.parse(recentAccess);
        let html = '<div class="recent-access-list">';
        
        recentPages.forEach(function(page) {
            html += '<div class="recent-access-item" onclick="selectPageType(\'' + page.type + '\')">';
            html += '<i class="' + getPageTypeIcon(page.type) + '"></i>';
            html += '<span>' + getPageTypeName(page.type) + '</span>';
            html += '<span class="recent-time">' + page.time + '</span>';
            html += '</div>';
        });
        
        html += '</div>';
        recentAccessEl.innerHTML = html;
    } else {
        recentAccessEl.innerHTML = '<p class="no-recent">暂无最近访问记录</p>';
    }
}

function selectPageType(pageType) {
    localStorage.setItem('skillcenter_page_type', pageType);
    
    let role = 'personal';
    switch(pageType) {
        case 'dashboard':
        case 'personal':
        case 'market':
            role = 'personal';
            break;
        case 'admin':
            role = 'admin';
            break;
    }
    localStorage.setItem('currentRole', role);
    
    updateRecentAccess(pageType);
    navigateToPage(pageType);
}

function updateRecentAccess(pageType) {
    const recentAccess = localStorage.getItem('skillcenter_recent_access');
    let recentPages = [];
    
    if (recentAccess) {
        recentPages = JSON.parse(recentAccess);
    }
    
    const now = new Date();
    const timeStr = now.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
    
    recentPages.unshift({
        type: pageType,
        time: timeStr
    });
    
    recentPages = recentPages.slice(0, 3);
    
    localStorage.setItem('skillcenter_recent_access', JSON.stringify(recentPages));
}

function navigateToPage(pageType) {
    switch(pageType) {
        case 'dashboard':
            window.location.href = '/console/pages/dashboard.html';
            break;
        case 'personal':
            window.location.href = '/console/pages/my-scenes.html';
            break;
        case 'market':
            window.location.href = '/console/pages/capability-discovery.html';
            break;
        case 'admin':
            window.location.href = '/console/pages/scene-management.html';
            break;
    }
}

function getPageTypeName(pageType) {
    switch(pageType) {
        case 'dashboard':
            return '仪表盘';
        case 'personal':
            return '个人中心';
        case 'market':
            return '技能市场';
        case 'admin':
            return '管理中心';
        default:
            return '未知';
    }
}

function getPageTypeIcon(pageType) {
    switch(pageType) {
        case 'dashboard':
            return 'ri-dashboard-line';
        case 'personal':
            return 'ri-user-line';
        case 'market':
            return 'ri-store-2-line';
        case 'admin':
            return 'ri-admin-line';
        default:
            return 'ri-bolt-line';
    }
}
