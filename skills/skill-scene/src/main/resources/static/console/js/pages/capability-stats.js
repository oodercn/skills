/**
 * 能力统计页面脚本
 */
(function() {
    'use strict';

    let currentUser = null;

    async function checkLogin() {
        try {
            const response = await fetch('/api/v1/auth/session');
            const result = await response.json();
            if (result.status === 'success' && result.data) {
                currentUser = result.data;
                document.getElementById('user-name').textContent = currentUser.name;
                await loadMenu();
                await loadStats();
            } else {
                window.location.href = '/console/pages/login.html';
            }
        } catch (e) {
            console.error('Session check failed:', e);
            window.location.href = '/console/pages/login.html';
        }
    }

    async function loadMenu() {
        try {
            const response = await fetch('/api/v1/auth/menu-config');
            const result = await response.json();
            if (result.status === 'success' && result.data) {
                renderMenu(result.data);
            }
        } catch (e) {
            console.error('Failed to load menu:', e);
        }
    }

    function renderMenu(menuItems) {
        const menuEl = document.getElementById('nav-menu');
        if (!menuEl) return;
        
        menuEl.innerHTML = menuItems.map(item => `
            <li class="nav-menu__item ${item.active ? 'nav-menu__item--active' : ''}">
                <a href="${item.url}"><i class="${item.icon}"></i> ${item.name}</a>
            </li>
        `).join('');
    }

    async function loadStats() {
        try {
            const response = await fetch('/api/v1/capabilities/stats');
            const result = await response.json();
            if (result.status === 'success' && result.data) {
                renderStats(result.data);
            }
        } catch (e) {
            console.error('Failed to load stats:', e);
            renderMockStats();
        }
    }

    function renderStats(data) {
        if (data.totalCalls) {
            document.getElementById('totalCalls').textContent = data.totalCalls.toLocaleString();
        }
        if (data.successRate) {
            document.getElementById('successRate').textContent = data.successRate + '%';
        }
        if (data.avgResponse) {
            document.getElementById('avgResponse').textContent = data.avgResponse + 'ms';
        }
        if (data.activeCount) {
            document.getElementById('activeCount').textContent = data.activeCount;
        }
    }

    function renderMockStats() {
        document.getElementById('totalCalls').textContent = '12,458';
        document.getElementById('successRate').textContent = '98.5%';
        document.getElementById('avgResponse').textContent = '156ms';
        document.getElementById('activeCount').textContent = '24';
        
        renderMockCharts();
    }

    function renderMockCharts() {
        const callsChart = document.getElementById('callsChart');
        if (callsChart) {
            const heights = [30, 45, 60, 40, 70, 50, 80];
            callsChart.innerHTML = heights.map(h => 
                `<div class="chart-bar" style="height: ${h}%"></div>`
            ).join('');
        }

        const capabilityRankChart = document.getElementById('capabilityRankChart');
        if (capabilityRankChart) {
            const capabilities = [
                { name: '日志提交', value: 85, color: 'blue' },
                { name: '日志提醒', value: 72, color: 'green' },
                { name: '日志汇总', value: 65, color: 'yellow' },
                { name: '日志分析', value: 58, color: 'purple' },
                { name: '邮件通知', value: 45, color: 'red' }
            ];
            
            capabilityRankChart.innerHTML = capabilities.map(cap => `
                <div class="bar-item">
                    <div class="bar-label">${cap.name}</div>
                    <div class="bar-track">
                        <div class="bar-fill ${cap.color}" style="width: ${cap.value}%">${cap.value}%</div>
                    </div>
                </div>
            `).join('');
        }

        const topCapabilityList = document.getElementById('topCapabilityList');
        if (topCapabilityList) {
            const tops = [
                { name: '日志提交', type: 'SERVICE', value: '3,245' },
                { name: '日志提醒', type: 'COMMUNICATION', value: '2,876' },
                { name: '日志汇总', type: 'SERVICE', value: '2,134' },
                { name: '日志分析', type: 'AI', value: '1,892' },
                { name: '邮件通知', type: 'COMMUNICATION', value: '1,543' }
            ];
            
            topCapabilityList.innerHTML = tops.map((cap, i) => `
                <div class="rank-item">
                    <div class="rank-num ${i === 0 ? 'top1' : i === 1 ? 'top2' : i === 2 ? 'top3' : ''}">${i + 1}</div>
                    <div class="rank-info">
                        <div class="rank-name">${cap.name}</div>
                        <div class="rank-type">${cap.type}</div>
                    </div>
                    <div class="rank-value">${cap.value}</div>
                </div>
            `).join('');
        }

        const errorList = document.getElementById('errorList');
        if (errorList) {
            const errors = [
                { name: '日志提交', msg: '连接超时', time: '2分钟前' },
                { name: '邮件通知', msg: 'SMTP认证失败', time: '15分钟前' }
            ];
            
            errorList.innerHTML = errors.map(err => `
                <div class="error-item">
                    <div class="error-icon"><i class="ri-error-warning-line"></i></div>
                    <div class="error-info">
                        <div class="error-name">${err.name}</div>
                        <div class="error-msg">${err.msg}</div>
                    </div>
                    <div class="error-time">${err.time}</div>
                </div>
            `).join('');
        }

        const typeDistChart = document.getElementById('typeDistChart');
        if (typeDistChart) {
            const types = [
                { name: 'SERVICE', value: 40, color: 'blue' },
                { name: 'COMMUNICATION', value: 30, color: 'green' },
                { name: 'AI', value: 20, color: 'purple' },
                { name: 'STORAGE', value: 10, color: 'yellow' }
            ];
            
            typeDistChart.innerHTML = types.map(type => `
                <div class="bar-item">
                    <div class="bar-label">${type.name}</div>
                    <div class="bar-track">
                        <div class="bar-fill ${type.color}" style="width: ${type.value}%">${type.value}%</div>
                    </div>
                </div>
            `).join('');
        }

        const logTableBody = document.getElementById('logTableBody');
        if (logTableBody) {
            const logs = [
                { time: '10:23:45', capability: '日志提交', level: 'success', duration: '45ms', msg: '提交成功' },
                { time: '10:22:30', capability: '日志提醒', level: 'info', duration: '12ms', msg: '提醒已发送' },
                { time: '10:21:15', capability: '日志汇总', level: 'warn', duration: '230ms', msg: '响应较慢' },
                { time: '10:20:00', capability: '日志提交', level: 'error', duration: '5000ms', msg: '连接超时' }
            ];
            
            logTableBody.innerHTML = logs.map(log => `
                <tr>
                    <td>${log.time}</td>
                    <td>${log.capability}</td>
                    <td><span class="log-level ${log.level}">${log.level.toUpperCase()}</span></td>
                    <td>${log.duration}</td>
                    <td>${log.msg}</td>
                </tr>
            `).join('');
        }
    }

    function setTimeRange(range) {
        document.querySelectorAll('.time-btn').forEach(btn => btn.classList.remove('active'));
        event.target.classList.add('active');
        loadStats();
    }

    function refreshStats() {
        loadStats();
    }

    function initThemeToggle() {
        const toggleBtn = document.querySelector('[data-nx-theme-toggle]');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', function() {
                const html = document.documentElement;
                const icon = this.querySelector('i');
                
                if (html.classList.contains('light-theme')) {
                    html.classList.remove('light-theme');
                    localStorage.setItem('theme', 'dark');
                    icon.className = 'ri-sun-line';
                } else {
                    html.classList.add('light-theme');
                    localStorage.setItem('theme', 'light');
                    icon.className = 'ri-moon-line';
                }
            });

            const savedTheme = localStorage.getItem('theme');
            if (savedTheme === 'light') {
                document.documentElement.classList.add('light-theme');
                toggleBtn.querySelector('i').className = 'ri-moon-line';
            }
        }
    }

    window.setTimeRange = setTimeRange;
    window.refreshStats = refreshStats;

    document.addEventListener('DOMContentLoaded', function() {
        checkLogin();
        initThemeToggle();
        renderMockCharts();
    });

})();
