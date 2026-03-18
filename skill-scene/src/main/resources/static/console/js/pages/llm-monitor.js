(function() {
    'use strict';
    
    let logs = [];
    let stats = {};
    let currentPage = 1;
    let pageSize = 10;
    
    function init() {
        loadStats();
        loadLogs();
    }
    
    function loadStats() {
        fetch('/api/v1/llm/monitor/stats')
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.status === 'success' && result.data) {
                    stats = result.data;
                    renderStats();
                } else {
                    loadMockStats();
                }
            })
            .catch(function(error) {
                console.error('Failed to load stats:', error);
                loadMockStats();
            });
    }
    
    function loadMockStats() {
        stats = {
            totalCalls: 1256,
            totalTokens: 156000,
            totalCost: 12.56,
            avgLatency: 245,
            successRate: 98.5,
            errorCount: 19
        };
        renderStats();
    }
    
    function renderStats() {
        var container = document.getElementById('statsGrid');
        if (!container) return;
        
        container.innerHTML = 
            '<div class="stat-card">' +
                '<div class="stat-card-header">' +
                    '<div class="stat-card-icon calls"><i class="ri-phone-line"></i></div>' +
                    '<div class="stat-card-trend up"><i class="ri-arrow-up-line"></i> 12%</div>' +
                '</div>' +
                '<div class="stat-card-value">' + (stats.totalCalls || 0) + '</div>' +
                '<div class="stat-card-label">总调用次数</div>' +
            '</div>' +
            '<div class="stat-card">' +
                '<div class="stat-card-header">' +
                    '<div class="stat-card-icon tokens"><i class="ri-text"></i></div>' +
                    '<div class="stat-card-trend up"><i class="ri-arrow-up-line"></i> 8%</div>' +
                '</div>' +
                '<div class="stat-card-value">' + (stats.totalTokens || 0).toLocaleString() + '</div>' +
                '<div class="stat-card-label">总Token消耗</div>' +
            '</div>' +
            '<div class="stat-card">' +
                '<div class="stat-card-header">' +
                    '<div class="stat-card-icon cost"><i class="ri-money-dollar-circle-line"></i></div>' +
                    '<div class="stat-card-trend down"><i class="ri-arrow-down-line"></i> 5%</div>' +
                '</div>' +
                '<div class="stat-card-value">$' + (stats.totalCost || 0).toFixed(2) + '</div>' +
                '<div class="stat-card-label">总成本</div>' +
            '</div>' +
            '<div class="stat-card">' +
                '<div class="stat-card-header">' +
                    '<div class="stat-card-icon latency"><i class="ri-timer-line"></i></div>' +
                    '<div class="stat-card-trend up"><i class="ri-arrow-up-line"></i> 3%</div>' +
                '</div>' +
                '<div class="stat-card-value">' + (stats.avgLatency || 0) + 'ms</div>' +
                '<div class="stat-card-label">平均延迟</div>' +
            '</div>';
    }
    
    function loadLogs() {
        fetch('/api/v1/llm/monitor/logs?pageNum=' + currentPage + '&pageSize=' + pageSize)
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.status === 'success' && result.data) {
                    logs = result.data.list || result.data;
                    renderLogs();
                } else {
                    loadMockLogs();
                }
            })
            .catch(function(error) {
                console.error('Failed to load logs:', error);
                loadMockLogs();
            });
    }
    
    function loadMockLogs() {
        logs = [];
        var providers = ['deepseek', 'baidu', 'openai'];
        var models = {
            deepseek: ['deepseek-chat', 'deepseek-coder'],
            baidu: ['ernie-bot-4', 'ernie-bot-turbo'],
            openai: ['gpt-4-turbo', 'gpt-3.5-turbo']
        };
        
        for (var i = 0; i < 50; i++) {
            var provider = providers[Math.floor(Math.random() * providers.length)];
            var model = models[provider][Math.floor(Math.random() * models[provider].length)];
            var inputTokens = Math.floor(Math.random() * 2000) + 100;
            var outputTokens = Math.floor(Math.random() * 1000) + 50;
            var status = Math.random() > 0.05 ? 'success' : 'error';
            
            logs.push({
                logId: 'log-' + (i + 1),
                providerId: provider,
                providerName: provider === 'deepseek' ? 'DeepSeek' : (provider === 'baidu' ? '百度文心' : 'OpenAI'),
                model: model,
                inputTokens: inputTokens,
                outputTokens: outputTokens,
                totalTokens: inputTokens + outputTokens,
                latency: Math.floor(Math.random() * 2000) + 100,
                cost: ((inputTokens + outputTokens) * 0.00001).toFixed(4),
                status: status,
                createTime: Date.now() - Math.floor(Math.random() * 86400000)
            });
        }
        
        renderLogs();
    }
    
    function renderLogs() {
        var tbody = document.getElementById('logsBody');
        if (!tbody) return;
        
        var providerFilter = document.getElementById('providerFilter');
        var statusFilter = document.getElementById('statusFilter');
        var searchInput = document.getElementById('searchInput');
        
        var providerValue = providerFilter ? providerFilter.value : '';
        var statusValue = statusFilter ? statusFilter.value : '';
        var searchTerm = searchInput ? searchInput.value.toLowerCase() : '';
        
        var filtered = logs.filter(function(log) {
            if (providerValue && log.providerId !== providerValue) return false;
            if (statusValue && log.status !== statusValue) return false;
            if (searchTerm && !log.model.toLowerCase().includes(searchTerm)) return false;
            return true;
        });
        
        var start = (currentPage - 1) * pageSize;
        var paged = filtered.slice(start, start + pageSize);
        
        if (paged.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" style="text-align: center; padding: 40px; color: var(--nx-text-secondary);">暂无数据</td></tr>';
            return;
        }
        
        var html = '';
        paged.forEach(function(log) {
            var time = new Date(log.createTime).toLocaleString('zh-CN');
            var providerClass = log.providerId;
            html += '<tr>' +
                '<td>' + time + '</td>' +
                '<td><span class="provider-badge ' + providerClass + '">' + log.providerName + '</span></td>' +
                '<td>' + log.model + '</td>' +
                '<td>' + log.inputTokens + '</td>' +
                '<td>' + log.outputTokens + '</td>' +
                '<td>' + log.latency + '</td>' +
                '<td>$' + log.cost + '</td>' +
                '<td><span class="status-badge ' + log.status + '">' + (log.status === 'success' ? '成功' : '失败') + '</span></td>' +
                '<td><button class="nx-btn nx-btn--ghost nx-btn--icon" onclick="LLMMonitor.showLogDetail(\'' + log.logId + '\')" title="查看详情"><i class="ri-eye-line"></i></button></td>' +
            '</tr>';
        });
        
        tbody.innerHTML = html;
        renderPagination(filtered.length);
    }
    
    function renderPagination(total) {
        var totalPages = Math.ceil(total / pageSize);
        var container = document.getElementById('pagination');
        if (!container) return;
        
        var html = '';
        if (currentPage > 1) {
            html += '<button onclick="LLMMonitor.goToPage(' + (currentPage - 1) + ')"><i class="ri-arrow-left-line"></i></button>';
        }
        
        for (var i = 1; i <= Math.min(totalPages, 5); i++) {
            html += '<button class="' + (i === currentPage ? 'active' : '') + '" onclick="LLMMonitor.goToPage(' + i + ')">' + i + '</button>';
        }
        
        if (currentPage < totalPages) {
            html += '<button onclick="LLMMonitor.goToPage(' + (currentPage + 1) + ')"><i class="ri-arrow-right-line"></i></button>';
        }
        
        container.innerHTML = html;
    }
    
    function goToPage(page) {
        currentPage = page;
        renderLogs();
    }
    
    function filterLogs() {
        currentPage = 1;
        renderLogs();
    }
    
    function showLogDetail(logId) {
        var log = logs.find(function(l) { return l.logId === logId; });
        if (!log) return;
        
        var time = new Date(log.createTime).toLocaleString('zh-CN');
        
        var content = document.getElementById('logDetailContent');
        if (!content) return;
        
        content.innerHTML = 
            '<div class="log-detail-row">' +
                '<div class="log-detail-label">调用ID</div>' +
                '<div class="log-detail-value">' + log.logId + '</div>' +
            '</div>' +
            '<div class="log-detail-row">' +
                '<div class="log-detail-label">调用时间</div>' +
                '<div class="log-detail-value">' + time + '</div>' +
            '</div>' +
            '<div class="log-detail-row">' +
                '<div class="log-detail-label">提供者</div>' +
                '<div class="log-detail-value">' + log.providerName + '</div>' +
            '</div>' +
            '<div class="log-detail-row">' +
                '<div class="log-detail-label">模型</div>' +
                '<div class="log-detail-value">' + log.model + '</div>' +
            '</div>' +
            '<div class="log-detail-row">' +
                '<div class="log-detail-label">输入Token</div>' +
                '<div class="log-detail-value">' + log.inputTokens + '</div>' +
            '</div>' +
            '<div class="log-detail-row">' +
                '<div class="log-detail-label">输出Token</div>' +
                '<div class="log-detail-value">' + log.outputTokens + '</div>' +
            '</div>' +
            '<div class="log-detail-row">' +
                '<div class="log-detail-label">延迟</div>' +
                '<div class="log-detail-value">' + log.latency + ' ms</div>' +
            '</div>' +
            '<div class="log-detail-row">' +
                '<div class="log-detail-label">成本</div>' +
                '<div class="log-detail-value">$' + log.cost + '</div>' +
            '</div>' +
            '<div class="log-detail-row">' +
                '<div class="log-detail-label">状态</div>' +
                '<div class="log-detail-value"><span class="status-badge ' + log.status + '">' + (log.status === 'success' ? '成功' : '失败') + '</span></div>' +
            '</div>';
        
        var modal = document.getElementById('logDetailModal');
        if (modal) {
            modal.classList.add('open');
        }
    }
    
    function closeModal(modalId) {
        var modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('open');
        }
    }
    
    function refreshData() {
        loadStats();
        loadLogs();
        showToast('数据已刷新', 'success');
    }
    
    function showToast(message, type) {
        var toast = document.getElementById('toast');
        if (!toast) return;
        
        toast.textContent = message;
        toast.className = 'toast ' + type;
        toast.classList.add('show');
        
        setTimeout(function() {
            toast.classList.remove('show');
        }, 3000);
    }
    
    window.LLMMonitor = {
        init: init,
        loadStats: loadStats,
        loadLogs: loadLogs,
        goToPage: goToPage,
        filterLogs: filterLogs,
        showLogDetail: showLogDetail,
        closeModal: closeModal,
        refreshData: refreshData
    };
    
    window.refreshData = refreshData;
    window.filterLogs = filterLogs;
    window.closeModal = closeModal;
    
    document.addEventListener('DOMContentLoaded', init);
})();
