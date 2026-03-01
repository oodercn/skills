/**
 * Personal Dashboard Page Script
 */

(function() {
    'use strict';

    async function initDashboard() {
        console.log('[PersonalDashboard] 初始化个人仪表盘...');

        await loadStats();
        await loadActivities();
    }

    async function loadStats() {
        try {
            const stats = await PersonalAPI.getStats();
            
            document.getElementById('skill-count').textContent = stats.skillCount || 0;
            document.getElementById('execution-count').textContent = stats.executionCount || 0;
            document.getElementById('shared-count').textContent = stats.sharedCount || 0;
            document.getElementById('group-count').textContent = stats.groupCount || 0;
            
        } catch (error) {
            console.error('[PersonalDashboard] 加载统计数据失败:', error);
        }
    }

    async function loadActivities() {
        const activityList = document.getElementById('activity-list');
        if (!activityList) return;

        try {
            const activities = await PersonalAPI.getRecentActivities();
            
            if (!activities || activities.length === 0) {
                activityList.innerHTML = '<div class="empty-state">暂无活动记录</div>';
                return;
            }

            const html = activities.map(activity => `
                <div class="activity-item">
                    <div class="activity-icon">
                        <i class="${activity.icon || 'ri-time-line'}"></i>
                    </div>
                    <div class="activity-content">
                        <div class="activity-title">${activity.title}</div>
                        <div class="activity-time">${activity.time}</div>
                    </div>
                </div>
            `).join('');

            activityList.innerHTML = html;
            
        } catch (error) {
            console.error('[PersonalDashboard] 加载活动记录失败:', error);
            activityList.innerHTML = '<div class="error-state">加载失败</div>';
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initDashboard);
    } else {
        initDashboard();
    }

    window.onPageInit = initDashboard;

})();
