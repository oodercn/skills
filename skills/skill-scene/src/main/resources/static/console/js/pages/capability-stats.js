(function(global) {
    'use strict';

    var currentTimeRange = 'today';
    var overviewData = null;
    var rankData = [];
    var errorData = [];
    var logData = [];

    var CapabilityStats = {
        init: function() {
            window.onPageInit = function() {
                console.log('能力统计页面初始化完成');
                CapabilityStats.loadStats();
                CapabilityStats.initLlmAssistant();
            };
        },

        initLlmAssistant: function() {
            if (typeof LlmAssistant !== 'undefined') {
                LlmAssistant.init();
            }
        },

        loadStats: async function() {
            try {
                var results = await Promise.all([
                    ApiClient.get('/api/v1/capabilities/stats/overview'),
                    ApiClient.get('/api/v1/capabilities/stats/rank?sortBy=invokeCount&limit=10'),
                    ApiClient.get('/api/v1/capabilities/stats/errors?limit=5'),
                    ApiClient.get('/api/v1/capabilities/stats/logs?limit=20')
                ]);

                if (results[0].code === 200) {
                    overviewData = results[0].data;
                }
                if (results[1].code === 200) {
                    rankData = results[1].data || [];
                }
                if (results[2].code === 200) {
                    errorData = results[2].data || [];
                }
                if (results[3].code === 200) {
                    logData = results[3].data || [];
                }

                CapabilityStats.renderOverviewCharts();
                CapabilityStats.renderCapabilityRank();
                CapabilityStats.renderTopCapabilities();
                CapabilityStats.renderErrors();
                CapabilityStats.renderTypeDistribution();
                CapabilityStats.renderLogs();
            } catch (error) {
                console.error('Failed to load stats:', error);
                CapabilityStats.renderOverviewCharts();
                CapabilityStats.renderCapabilityRank();
                CapabilityStats.renderTopCapabilities();
                CapabilityStats.renderErrors();
                CapabilityStats.renderTypeDistribution();
                CapabilityStats.renderLogs();
            }
        },

        renderOverviewCharts: function() {
            var charts = ['callsChart', 'successChart', 'responseChart', 'activeChart'];
            charts.forEach(function(chartId) {
                var container = document.getElementById(chartId);
                if (!container) return;
                
                var html = '';
                for (var i = 0; i < 20; i++) {
                    var height = Math.random() * 30 + 10;
                    html += '<div class="chart-bar" style="height: ' + height + 'px;"></div>';
                }
                container.innerHTML = html;
            });

            if (overviewData) {
                var totalEl = document.getElementById('totalCapabilities');
                var activeEl = document.getElementById('activeCapabilities');
                var invocationsEl = document.getElementById('totalInvocations');
                var successRateEl = document.getElementById('successRate');
                
                if (totalEl) totalEl.textContent = overviewData.totalCapabilities || 0;
                if (activeEl) activeEl.textContent = overviewData.activeCapabilities || 0;
                if (invocationsEl) invocationsEl.textContent = overviewData.totalInvocations || 0;
                if (successRateEl) {
                    var rate = overviewData.totalInvocations > 0 
                        ? Math.round(overviewData.successInvocations / overviewData.totalInvocations * 100) 
                        : 0;
                    successRateEl.textContent = rate + '%';
                }
            }
        },

        renderCapabilityRank: function() {
            var container = document.getElementById('capabilityRankChart');
            if (!container) return;

            var data = rankData.length > 0 ? rankData.map(function(item) {
                return { name: item.name, value: item.invokeCount, color: 'blue' };
            }) : [
                { name: '日志提交', value: 3456, color: 'blue' },
                { name: '日志提醒', value: 2890, color: 'green' },
                { name: '邮件通知', value: 2134, color: 'yellow' },
                { name: '日志汇总', value: 1876, color: 'purple' },
                { name: '短信通知', value: 1234, color: 'red' },
                { name: '数据分析', value: 987, color: 'blue' },
                { name: '报告生成', value: 654, color: 'green' }
            ];

            var maxValue = Math.max.apply(null, data.map(function(d) { return d.value; }));

            var html = '';
            data.forEach(function(item) {
                var width = (item.value / maxValue * 100).toFixed(1);
                html += '<div class="bar-item">' +
                    '<span class="bar-label">' + item.name + '</span>' +
                    '<div class="bar-track">' +
                    '<div class="bar-fill ' + item.color + '" style="width: ' + width + '%;">' + item.value + '</div>' +
                    '</div></div>';
            });
            container.innerHTML = html;
        },

        renderTopCapabilities: function() {
            var container = document.getElementById('topCapabilityList');
            if (!container) return;

            var data = rankData.length > 0 ? rankData.slice(0, 5).map(function(item) {
                return { name: item.name, type: item.type, calls: item.invokeCount };
            }) : [
                { name: '日志提交', type: 'SERVICE', calls: 3456 },
                { name: '日志提醒', type: 'COMMUNICATION', calls: 2890 },
                { name: '邮件通知', type: 'COMMUNICATION', calls: 2134 },
                { name: '日志汇总', type: 'SERVICE', calls: 1876 },
                { name: '短信通知', type: 'COMMUNICATION', calls: 1234 }
            ];

            var html = '';
            data.forEach(function(item, i) {
                var rankClass = i === 0 ? 'top1' : i === 1 ? 'top2' : i === 2 ? 'top3' : '';
                html += '<div class="rank-item">' +
                    '<span class="rank-num ' + rankClass + '">' + (i + 1) + '</span>' +
                    '<div class="rank-info">' +
                    '<div class="rank-name">' + item.name + '</div>' +
                    '<div class="rank-type">' + item.type + '</div></div>' +
                    '<span class="rank-value">' + item.calls.toLocaleString() + '</span></div>';
            });
            container.innerHTML = html;
        },

        renderErrors: function() {
            var container = document.getElementById('errorList');
            if (!container) return;

            var data = errorData.length > 0 ? errorData.map(function(item) {
                var parts = item.split(' ');
                return { name: parts[3] || '未知', msg: parts.slice(4).join(' ') || item, time: parts[0] + ' ' + parts[1] };
            }) : [
                { name: '短信通知', msg: '连接超时', time: '10分钟前' },
                { name: '数据分析', msg: '内存不足', time: '1小时前' },
                { name: '报告生成', msg: '文件不存在', time: '2小时前' }
            ];

            if (data.length === 0) {
                container.innerHTML = '<div style="text-align: center; padding: 20px; color: var(--nx-text-secondary);">暂无错误记录</div>';
                return;
            }

            var html = '';
            data.forEach(function(item) {
                html += '<div class="error-item">' +
                    '<div class="error-icon"><i class="ri-error-warning-line"></i></div>' +
                    '<div class="error-info">' +
                    '<div class="error-name">' + item.name + '</div>' +
                    '<div class="error-msg">' + item.msg + '</div></div>' +
                    '<span class="error-time">' + item.time + '</span></div>';
            });
            container.innerHTML = html;
        },

        renderTypeDistribution: function() {
            var container = document.getElementById('typeDistChart');
            if (!container) return;

            var data = [
                { name: 'SERVICE', value: 35, color: 'blue' },
                { name: 'COMMUNICATION', value: 28, color: 'green' },
                { name: 'AI', value: 18, color: 'purple' },
                { name: 'STORAGE', value: 12, color: 'yellow' },
                { name: '其他', value: 7, color: 'red' }
            ];

            var html = '';
            data.forEach(function(item) {
                html += '<div class="bar-item">' +
                    '<span class="bar-label">' + item.name + '</span>' +
                    '<div class="bar-track">' +
                    '<div class="bar-fill ' + item.color + '" style="width: ' + item.value + '%;">' + item.value + '%</div>' +
                    '</div></div>';
            });
            container.innerHTML = html;
        },

        renderLogs: function() {
            var container = document.getElementById('logTableBody');
            if (!container) return;

            var data = logData.length > 0 ? logData.map(function(item) {
                return {
                    time: new Date(item.time).toLocaleTimeString(),
                    capability: item.capabilityId,
                    level: item.level.toLowerCase(),
                    duration: '-',
                    msg: item.message
                };
            }) : [
                { time: '14:32:15', capability: '日志提交', level: 'success', duration: '45ms', msg: '调用成功' },
                { time: '14:31:58', capability: '邮件通知', level: 'info', duration: '120ms', msg: '邮件已发送' },
                { time: '14:31:42', capability: '短信通知', level: 'error', duration: '5000ms', msg: '连接超时' },
                { time: '14:31:30', capability: '日志汇总', level: 'success', duration: '230ms', msg: '汇总完成' },
                { time: '14:31:15', capability: '日志提醒', level: 'warn', duration: '89ms', msg: '部分用户未配置联系方式' },
                { time: '14:30:58', capability: '数据分析', level: 'success', duration: '1560ms', msg: '分析完成' },
                { time: '14:30:42', capability: '报告生成', level: 'info', duration: '340ms', msg: '生成PDF报告' },
                { time: '14:30:25', capability: '日志提交', level: 'success', duration: '52ms', msg: '调用成功' }
            ];

            var html = '';
            data.forEach(function(item) {
                html += '<tr>' +
                    '<td>' + item.time + '</td>' +
                    '<td>' + item.capability + '</td>' +
                    '<td><span class="log-level ' + item.level + '">' + item.level.toUpperCase() + '</span></td>' +
                    '<td>' + item.duration + '</td>' +
                    '<td>' + item.msg + '</td></tr>';
            });
            container.innerHTML = html;
        },

        setTimeRange: function(range) {
            currentTimeRange = range;
            document.querySelectorAll('.time-btn').forEach(function(btn) {
                btn.classList.remove('active');
            });
            event.target.classList.add('active');
            CapabilityStats.loadStats();
        },

        refresh: function() {
            CapabilityStats.loadStats();
        }
    };

    CapabilityStats.init();

    global.setTimeRange = CapabilityStats.setTimeRange;
    global.refreshStats = CapabilityStats.refresh;

})(typeof window !== 'undefined' ? window : this);
