class PersonalDashboard {
    constructor() {
        this.init();
    }
    
    async init() {
        await this.loadStats();
        await this.loadActivities();
    }
    
    async loadStats() {
        try {
            const response = await fetch('/api/test/stats');
            if (response.ok) {
                const data = await response.json();
                document.getElementById('skill-count').textContent = data.skillCount || 0;
                document.getElementById('execution-count').textContent = data.executionCount || 0;
                document.getElementById('shared-count').textContent = data.sharedCount || 0;
                document.getElementById('group-count').textContent = data.groupCount || 0;
            }
        } catch (e) {
            console.log('Stats not available, using defaults');
            document.getElementById('skill-count').textContent = '5';
            document.getElementById('execution-count').textContent = '128';
            document.getElementById('shared-count').textContent = '3';
            document.getElementById('group-count').textContent = '2';
        }
    }
    
    async loadActivities() {
        const container = document.getElementById('activity-list');
        container.innerHTML = `
            <div class="activity-item">
                <div class="activity-icon"><i class="ri-check-line"></i></div>
                <div class="activity-content">
                    <p>系统状态检查完成</p>
                    <span class="activity-time">2分钟前</span>
                </div>
            </div>
            <div class="activity-item">
                <div class="activity-icon"><i class="ri-refresh-line"></i></div>
                <div class="activity-content">
                    <p>健康检查执行</p>
                    <span class="activity-time">5分钟前</span>
                </div>
            </div>
            <div class="activity-item">
                <div class="activity-icon"><i class="ri-download-line"></i></div>
                <div class="activity-content">
                    <p>存储管理技能安装</p>
                    <span class="activity-time">1小时前</span>
                </div>
            </div>
        `;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new PersonalDashboard();
});
