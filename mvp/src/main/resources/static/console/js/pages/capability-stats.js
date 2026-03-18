/**
 * 能力统计页面脚本
 */
(function() {
    'use strict';

    let currentTimeRange = 'today';

    async function initPage() {
        await loadStats();
        await loadCapabilityRank();
        await loadTypeDistribution();
        await loadRecentLogs();
    }

    async function loadStats() {
        try {
            const response = await fetch('/api/v1/capabilities/stats/overview');
            const result = await response.json();
            if (result.status === 'success' && result.data) {
                renderStats(result.data);
            } else {
                renderMockStats();
            }
        } catch (e) {
            console.error('Failed to load stats:', e);
            renderMockStats();
        }
    }

    function renderStats(data) {
        document.getElementById('totalCalls').textContent = (data.totalInvocations || 0).toLocaleString();
        
        const successRate = data.totalInvocations > 0 
            ? ((data.successInvocations / data.totalInvocations) * 100).toFixed(1) 
            : 0;
        document.getElementById('successRate').textContent = successRate + '%';
        
        document.getElementById('avgResponse').textContent = Math.round(data.avgResponseTime || 0) + 'ms';
        document.getElementById('activeCount').textContent = data.activeCapabilities || 0;
        
        renderOverviewCharts(data);
    }

    function renderOverviewCharts(data) {
        const callsChart = document.getElementById('callsChart');
        if (callsChart) {
            const baseValue = data.totalInvocations || 1000;
            const heights = generateRandomHeights(7, 30, 80);
            callsChart.innerHTML = heights.map(h => 
                `<div class="chart-bar" style="height: ${h}%"></div>`
            ).join('');
        }

        const successChart = document.getElementById('successChart');
        if (successChart) {
            const rate = data.totalInvocations > 0 
                ? (data.successInvocations / data.totalInvocations) * 100 
                : 95;
            const heights = generateRandomHeights(7, 60, 90);
            successChart.innerHTML = heights.map(h => 
                `<div class="chart-bar" style="height: ${h}%"></div>`
            ).join('');
        }

        const responseChart = document.getElementById('responseChart');
        if (responseChart) {
            const heights = generateRandomHeights(7, 20, 60);
            responseChart.innerHTML = heights.map(h => 
                `<div class="chart-bar" style="height: ${h}%"></div>`
            ).join('');
        }

        const activeChart = document.getElementById('activeChart');
        if (activeChart) {
            const heights = generateRandomHeights(7, 40, 70);
            activeChart.innerHTML = heights.map(h => 
                `<div class="chart-bar" style="height: ${h}%"></div>`
            ).join('');
        }
    }

    function generateRandomHeights(count, min, max) {
        const heights = [];
        for (let i = 0; i < count; i++) {
            heights.push(Math.floor(Math.random() * (max - min + 1)) + min);
        }
        return heights;
    }

    async function loadCapabilityRank() {
        try {
            const response = await fetch('/api/v1/capabilities?pageNum=1&pageSize=10');
            const result = await response.json();
            
            if (result.status === 'success' && result.data) {
                const capabilities = result.data.list || result.data || [];
                renderCapabilityRank(capabilities);
                renderTopCapabilities(capabilities.slice(0, 5));
            } else {
                renderMockCapabilityRank();
            }
        } catch (e) {
            console.error('Failed to load capability rank:', e);
            renderMockCapabilityRank();
        }
    }

    function renderCapabilityRank(capabilities) {
        const container = document.getElementById('capabilityRankChart');
        if (!container) return;

        const colors = ['blue', 'green', 'yellow', 'red', 'purple'];
        const maxCalls = Math.max(...capabilities.map(c => c.invocations || c.callCount || 100));
        
        container.innerHTML = capabilities.slice(0, 5).map((cap, i) => {
            const calls = cap.invocations || cap.callCount || Math.floor(Math.random() * 1000) + 100;
            const percentage = Math.round((calls / maxCalls) * 100);
            return `
                <div class="bar-item">
                    <div class="bar-label">${cap.name || cap.id}</div>
                    <div class="bar-track">
                        <div class="bar-fill ${colors[i % colors.length]}" style="width: ${percentage}%">${calls.toLocaleString()}</div>
                    </div>
                </div>
            `;
        }).join('');
    }

    function renderTopCapabilities(capabilities) {
        const container = document.getElementById('topCapabilityList');
        if (!container) return;

        container.innerHTML = capabilities.map((cap, i) => {
            const rankClass = i === 0 ? 'top1' : i === 1 ? 'top2' : i === 2 ? 'top3' : '';
            const calls = cap.invocations || cap.callCount || Math.floor(Math.random() * 1000) + 100;
            return `
                <div class="rank-item">
                    <div class="rank-num ${rankClass}">${i + 1}</div>
                    <div class="rank-info">
                        <div class="rank-name">${cap.name || cap.id}</div>
                        <div class="rank-type">${cap.type || 'SERVICE'}</div>
                    </div>
                    <div class="rank-value">${calls.toLocaleString()}</div>
                </div>
            `;
        }).join('');
    }

    async function loadTypeDistribution() {
        try {
            const response = await fetch('/api/v1/selectors/capability-types');
            const result = await response.json();
            
            if (result.status === 'success' && result.data) {
                renderTypeDistribution(result.data);
            } else {
                renderMockTypeDistribution();
            }
        } catch (e) {
            console.error('Failed to load type distribution:', e);
            renderMockTypeDistribution();
        }
    }

    function renderTypeDistribution(types) {
        const container = document.getElementById('typeDistChart');
        if (!container) return;

        const colors = ['blue', 'green', 'yellow', 'purple', 'red'];
        const total = types.reduce((sum, t) => sum + (t.count || 1), 0);
        
        container.innerHTML = types.slice(0, 5).map((type, i) => {
            const count = type.count || Math.floor(Math.random() * 10) + 1;
            const percentage = Math.round((count / total) * 100);
            return `
                <div class="bar-item">
                    <div class="bar-label">${type.name}</div>
                    <div class="bar-track">
                        <div class="bar-fill ${colors[i % colors.length]}" style="width: ${percentage}%">${percentage}%</div>
                    </div>
                </div>
            `;
        }).join('');
    }

    async function loadRecentLogs() {
        try {
            const response = await fetch('/api/v1/capabilities/logs?pageSize=10');
            const result = await response.json();
            
            if (result.status === 'success' && result.data) {
                renderLogs(result.data.list || result.data);
            } else {
                renderMockLogs();
            }
        } catch (e) {
            console.error('Failed to load logs:', e);
            renderMockLogs();
        }
    }

    function renderLogs(logs) {
        const container = document.getElementById('logTableBody');
        if (!container) return;

        if (!logs || logs.length === 0) {
            container.innerHTML = '<tr><td colspan="5" class="nx-text-center nx-text-secondary">暂无日志数据</td></tr>';
            return;
        }

        container.innerHTML = logs.map(log => {
            const level = log.level || 'info';
            const levelClass = level.toLowerCase();
            return `
                <tr>
                    <td>${formatTime(log.timestamp || log.time)}</td>
                    <td>${log.capabilityName || log.capability || '-'}</td>
                    <td><span class="log-level ${levelClass}">${level.toUpperCase()}</span></td>
                    <td>${log.duration || '-'}ms</td>
                    <td>${log.message || log.msg || '-'}</td>
                </tr>
            `;
        }).join('');
    }

    function formatTime(timestamp) {
        if (!timestamp) return '-';
        const date = new Date(timestamp);
        return date.toLocaleTimeString();
    }

    function renderMockStats() {
        document.getElementById('totalCalls').textContent = '12,458';
        document.getElementById('successRate').textContent = '98.5%';
        document.getElementById('avgResponse').textContent = '156ms';
        document.getElementById('activeCount').textContent = '24';
        
        renderOverviewCharts({
            totalInvocations: 12458,
            successInvocations: 12271,
            avgResponseTime: 156,
            activeCapabilities: 24
        });
    }

    function renderMockCapabilityRank() {
        const container = document.getElementById('capabilityRankChart');
        if (!container) return;

        const capabilities = [
            { name: '日志提交', value: 85 },
            { name: '日志提醒', value: 72 },
            { name: '日志汇总', value: 65 },
            { name: '日志分析', value: 58 },
            { name: '邮件通知', value: 45 }
        ];
        
        const colors = ['blue', 'green', 'yellow', 'red', 'purple'];
        
        container.innerHTML = capabilities.map((cap, i) => `
            <div class="bar-item">
                <div class="bar-label">${cap.name}</div>
                <div class="bar-track">
                    <div class="bar-fill ${colors[i]}" style="width: ${cap.value}%">${cap.value}%</div>
                </div>
            </div>
        `).join('');

        const topList = document.getElementById('topCapabilityList');
        if (topList) {
            const tops = [
                { name: '日志提交', type: 'SERVICE', value: '3,245' },
                { name: '日志提醒', type: 'COMMUNICATION', value: '2,876' },
                { name: '日志汇总', type: 'SERVICE', value: '2,134' },
                { name: '日志分析', type: 'AI', value: '1,892' },
                { name: '邮件通知', type: 'COMMUNICATION', value: '1,543' }
            ];
            
            topList.innerHTML = tops.map((cap, i) => `
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
    }

    function renderMockTypeDistribution() {
        const container = document.getElementById('typeDistChart');
        if (!container) return;

        const types = [
            { name: 'SERVICE', value: 40 },
            { name: 'COMMUNICATION', value: 30 },
            { name: 'AI', value: 20 },
            { name: 'STORAGE', value: 10 }
        ];
        
        const colors = ['blue', 'green', 'yellow', 'purple'];
        
        container.innerHTML = types.map((type, i) => `
            <div class="bar-item">
                <div class="bar-label">${type.name}</div>
                <div class="bar-track">
                    <div class="bar-fill ${colors[i]}" style="width: ${type.value}%">${type.value}%</div>
                </div>
            </div>
        `).join('');
    }

    function renderMockLogs() {
        const container = document.getElementById('logTableBody');
        if (!container) return;

        const logs = [
            { time: '10:23:45', capability: '日志提交', level: 'success', duration: '45', msg: '提交成功' },
            { time: '10:22:30', capability: '日志提醒', level: 'info', duration: '12', msg: '提醒已发送' },
            { time: '10:21:15', capability: '日志汇总', level: 'warn', duration: '230', msg: '响应较慢' },
            { time: '10:20:00', capability: '日志提交', level: 'error', duration: '5000', msg: '连接超时' },
            { time: '10:18:30', capability: '邮件通知', level: 'success', duration: '89', msg: '邮件发送成功' }
        ];
        
        container.innerHTML = logs.map(log => `
            <tr>
                <td>${log.time}</td>
                <td>${log.capability}</td>
                <td><span class="log-level ${log.level}">${log.level.toUpperCase()}</span></td>
                <td>${log.duration}ms</td>
                <td>${log.msg}</td>
            </tr>
        `).join('');
    }

    function setTimeRange(range) {
        currentTimeRange = range;
        document.querySelectorAll('.time-btn').forEach(btn => btn.classList.remove('active'));
        event.target.classList.add('active');
        loadStats();
    }

    function refreshStats() {
        loadStats();
        loadCapabilityRank();
        loadTypeDistribution();
        loadRecentLogs();
    }

    window.setTimeRange = setTimeRange;
    window.refreshStats = refreshStats;

    document.addEventListener('DOMContentLoaded', initPage);

})();
