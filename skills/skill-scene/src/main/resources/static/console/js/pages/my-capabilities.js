(function(global) {
    'use strict';

    var capabilities = [];
    var bindings = [];
    var currentFilter = 'all';
    var searchKeyword = '';

    var TYPE_ICONS = {
        'DRIVER': 'ri-hard-drive-2-line',
        'SERVICE': 'ri-server-line',
        'MANAGEMENT': 'ri-settings-3-line',
        'AI': 'ri-brain-line',
        'STORAGE': 'ri-database-2-line',
        'COMMUNICATION': 'ri-message-3-line',
        'SECURITY': 'ri-shield-check-line',
        'MONITORING': 'ri-pulse-line',
        'SKILL': 'ri-flashlight-line',
        'SCENE': 'ri-layout-grid-line',
        'CUSTOM': 'ri-tools-line'
    };

    var TYPE_NAMES = {
        'ATOMIC': '原子能力',
        'COMPOSITE': '组合能力',
        'SCENE': '场景能力',
        'DRIVER': '驱动能力',
        'COLLABORATIVE': '协作能力',
        'SERVICE': '服务能力',
        'AI': 'AI能力',
        'TOOL': '工具能力',
        'CONNECTOR': '连接器能力',
        'DATA': '数据能力',
        'MANAGEMENT': '管理能力',
        'COMMUNICATION': '通信能力',
        'SECURITY': '安全能力',
        'MONITORING': '监控能力',
        'SKILL': '技能能力',
        'CUSTOM': '自定义能力'
    };

    var MyCapabilities = {
        init: function() {
            window.onPageInit = function() {
                console.log('我的能力页面初始化完成');
                MyCapabilities.loadCapabilities();
                MyCapabilities.loadBindings();
                MyCapabilities.initLlmAssistant();
            };
        },

        initLlmAssistant: function() {
            if (typeof LlmAssistant !== 'undefined') {
                LlmAssistant.init();
            }
        },

        loadCapabilities: function() {
            ApiClient.get('/api/v1/capabilities')
                .then(function(result) {
                    if (result.code === 200 && result.data) {
                        capabilities = result.data;
                        MyCapabilities.renderTable();
                        MyCapabilities.updateStats();
                    }
                })
                .catch(function(error) {
                    console.error('加载能力失败:', error);
                    MyCapabilities.loadMockData();
                });
        },

        loadBindings: function() {
            ApiClient.get('/api/v1/capabilities/bindings')
                .then(function(result) {
                    if (result.code === 200 && result.data) {
                        bindings = result.data;
                    }
                })
                .catch(function(error) {
                    console.error('加载绑定失败:', error);
                });
        },

        loadMockData: function() {
            capabilities = [
                {
                    capabilityId: 'report-remind',
                    name: '日志提醒',
                    type: 'COMMUNICATION',
                    status: 'ENABLED',
                    version: '1.0.0',
                    description: '定时提醒员工提交工作日志',
                    lastInvokeTime: Date.now() - 3600000,
                    invokeCount: 156
                },
                {
                    capabilityId: 'report-submit',
                    name: '日志提交',
                    type: 'SERVICE',
                    status: 'ENABLED',
                    version: '1.0.0',
                    description: '员工提交工作日志的表单能力',
                    lastInvokeTime: Date.now() - 7200000,
                    invokeCount: 89
                },
                {
                    capabilityId: 'report-aggregate',
                    name: '日志汇总',
                    type: 'SERVICE',
                    status: 'ENABLED',
                    version: '1.0.0',
                    description: '汇总所有员工提交的日志',
                    lastInvokeTime: Date.now() - 86400000,
                    invokeCount: 45
                },
                {
                    capabilityId: 'notification-email',
                    name: '邮件通知',
                    type: 'COMMUNICATION',
                    status: 'ENABLED',
                    version: '2.1.0',
                    description: '发送邮件通知',
                    lastInvokeTime: Date.now() - 1800000,
                    invokeCount: 234
                },
                {
                    capabilityId: 'notification-sms',
                    name: '短信通知',
                    type: 'COMMUNICATION',
                    status: 'DISABLED',
                    version: '1.5.0',
                    description: '发送短信通知',
                    lastInvokeTime: Date.now() - 172800000,
                    invokeCount: 12
                }
            ];
            MyCapabilities.renderTable();
            MyCapabilities.updateStats();
        },

        updateStats: function() {
            var total = capabilities.length;
            var active = capabilities.filter(function(c) { return c.status === 'ENABLED'; }).length;
            var inactive = capabilities.filter(function(c) { return c.status === 'DISABLED'; }).length;
            var error = capabilities.filter(function(c) { return c.status === 'ERROR'; }).length;
            var calls = capabilities.reduce(function(sum, c) { return sum + (c.invokeCount || 0); }, 0);

            document.getElementById('statTotal').textContent = total;
            document.getElementById('statActive').textContent = active;
            document.getElementById('statInactive').textContent = inactive;
            document.getElementById('statError').textContent = error;
            document.getElementById('statCalls').textContent = calls;
        },

        renderTable: function() {
            var tbody = document.getElementById('capabilityTableBody');
            
            var filtered = capabilities.filter(function(cap) {
                var statusMatch = currentFilter === 'all' || cap.status === currentFilter;
                var searchMatch = !searchKeyword || 
                    (cap.name && cap.name.toLowerCase().indexOf(searchKeyword.toLowerCase()) >= 0) ||
                    (cap.capabilityId && cap.capabilityId.toLowerCase().indexOf(searchKeyword.toLowerCase()) >= 0);
                return statusMatch && searchMatch;
            });

            if (filtered.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; padding: 40px; color: var(--nx-text-secondary);">' +
                    '<i class="ri-inbox-line" style="font-size: 32px; display: block; margin-bottom: 8px;"></i>' +
                    '暂无能力数据</td></tr>';
                return;
            }

            var html = '';
            filtered.forEach(function(cap) {
                var typeIcon = TYPE_ICONS[cap.type] || TYPE_ICONS['CUSTOM'];
                var typeName = TYPE_NAMES[cap.type] || '自定义类型';
                var statusClass = cap.status === 'ENABLED' ? 'enabled' : cap.status === 'DISABLED' ? 'disabled' : 'error';
                var statusText = cap.status === 'ENABLED' ? '已启用' : cap.status === 'DISABLED' ? '已停用' : '异常';
                var lastInvoke = cap.lastInvokeTime ? MyCapabilities.formatTime(cap.lastInvokeTime) : '-';
                var bindingCount = bindings.filter(function(b) { return b.capabilityId === cap.capabilityId; }).length;
                
                var activationStatus = cap.activationStatus || 'INACTIVE';
                var activationClass = activationStatus === 'ACTIVATED' ? 'activated' : activationStatus === 'PENDING' ? 'pending' : 'inactive';
                var activationText = activationStatus === 'ACTIVATED' ? '已激活' : activationStatus === 'PENDING' ? '待激活' : '未激活';

                html += '<tr>' +
                    '<td><div class="cap-info">' +
                    '<div class="cap-icon type-' + (cap.type || 'CUSTOM') + '"><i class="' + typeIcon + '"></i></div>' +
                    '<div><div class="cap-name">' + (cap.name || cap.capabilityId) + '</div>' +
                    '<div class="cap-id">' + cap.capabilityId + '</div></div></div></td>' +
                    '<td>' + typeName + '</td>' +
                    '<td><span class="status-badge ' + statusClass + '"><span class="status-dot"></span>' + statusText + '</span></td>' +
                    '<td><span class="activation-badge ' + activationClass + '">' + activationText + '</span></td>' +
                    '<td>v' + (cap.version || '1.0.0') + '</td>' +
                    '<td>' + bindingCount + ' 个场景</td>' +
                    '<td>' + lastInvoke + '</td>' +
                    '<td><div class="action-btns">';
                
                if (activationStatus === 'PENDING') {
                    html += '<button class="nx-btn nx-btn--primary nx-btn--sm" onclick="goToActivation(\'' + cap.installId + '\')" title="激活">' +
                        '<i class="ri-key-2-line"></i></button>';
                }
                
                html += '<button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="showDetail(\'' + cap.capabilityId + '\')" title="详情">' +
                    '<i class="ri-eye-line"></i></button>' +
                    '<button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="toggleStatus(\'' + cap.capabilityId + '\')" title="' + (cap.status === 'ENABLED' ? '停用' : '启用') + '">' +
                    '<i class="ri-' + (cap.status === 'ENABLED' ? 'pause' : 'play') + '-line"></i></button>' +
                    '<button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="invokeCapability(\'' + cap.capabilityId + '\')" title="调用">' +
                    '<i class="ri-play-line"></i></button>' +
                    '<button class="nx-btn nx-btn--ghost nx-btn--sm nx-text-danger" onclick="uninstallCapability(\'' + cap.capabilityId + '\')" title="卸载">' +
                    '<i class="ri-delete-bin-line"></i></button>' +
                    '</div></td></tr>';
            });
            tbody.innerHTML = html;
        },

        formatTime: function(timestamp) {
            var now = Date.now();
            var diff = now - timestamp;
            
            if (diff < 60000) return '刚刚';
            if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
            if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
            return Math.floor(diff / 86400000) + '天前';
        },

        showDetailPanel: function(capabilityId) {
            var cap = capabilities.find(function(c) { return c.capabilityId === capabilityId; });
            if (!cap) return;

            var typeIcon = TYPE_ICONS[cap.type] || TYPE_ICONS['CUSTOM'];
            var typeName = TYPE_NAMES[cap.type] || '自定义类型';
            var capBindings = bindings.filter(function(b) { return b.capabilityId === capabilityId; });

            document.getElementById('detailTitle').textContent = cap.name || cap.capabilityId;

            var html = '<div class="detail-section">' +
                '<div class="detail-section-title"><i class="ri-information-line"></i> 基本信息</div>' +
                '<div class="detail-row"><span class="detail-row-label">能力ID</span><span class="detail-row-value">' + cap.capabilityId + '</span></div>' +
                '<div class="detail-row"><span class="detail-row-label">类型</span><span class="detail-row-value">' + typeName + '</span></div>' +
                '<div class="detail-row"><span class="detail-row-label">版本</span><span class="detail-row-value">v' + (cap.version || '1.0.0') + '</span></div>' +
                '<div class="detail-row"><span class="detail-row-label">状态</span><span class="detail-row-value">' + (cap.status === 'ENABLED' ? '已启用' : '已停用') + '</span></div>' +
                '<div class="detail-row"><span class="detail-row-label">调用次数</span><span class="detail-row-value">' + (cap.invokeCount || 0) + '</span></div>' +
                '</div>';

            html += '<div class="detail-section">' +
                '<div class="detail-section-title"><i class="ri-file-text-line"></i> 描述</div>' +
                '<p style="font-size: 14px; line-height: 1.6;">' + (cap.description || '暂无描述') + '</p>' +
                '</div>';

            html += '<div class="detail-section">' +
                '<div class="detail-section-title"><i class="ri-settings-3-line"></i> 配置</div>' +
                '<div class="config-form">' +
                '<div class="config-field">' +
                '<label class="config-label">超时时间(毫秒)</label>' +
                '<input type="number" class="config-input" value="30000">' +
                '<div class="config-hint">能力调用的超时时间</div>' +
                '</div>' +
                '<div class="config-field">' +
                '<label class="config-label">重试次数</label>' +
                '<input type="number" class="config-input" value="3">' +
                '<div class="config-hint">调用失败后的重试次数</div>' +
                '</div>' +
                '</div></div>';

            html += '<div class="detail-section">' +
                '<div class="detail-section-title"><i class="ri-link"></i> 绑定场景 (' + capBindings.length + ')</div>';

            if (capBindings.length > 0) {
                html += '<div class="binding-list">';
                capBindings.forEach(function(b) {
                    html += '<div class="binding-item">' +
                        '<div class="binding-info">' +
                        '<div class="binding-icon"><i class="ri-group-line"></i></div>' +
                        '<div><div class="binding-name">' + (b.sceneGroupId || '未知场景组') + '</div>' +
                        '<div class="binding-scene">优先级: ' + (b.priority || 1) + '</div></div></div>' +
                        '<button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="unbindCapability(\'' + b.bindingId + '\')">' +
                        '<i class="ri-link-unlink"></i></button></div>';
                });
                html += '</div>';
            } else {
                html += '<p style="color: var(--nx-text-secondary); font-size: 14px;">尚未绑定到任何场景组</p>';
            }

            html += '<div style="margin-top: 12px;">' +
                '<button class="nx-btn nx-btn--secondary nx-btn--sm" onclick="showBindingModal(\'' + capabilityId + '\')">' +
                '<i class="ri-add-line"></i> 添加绑定</button></div></div>';

            html += '<div class="detail-section">' +
                '<div class="detail-section-title"><i class="ri-tools-line"></i> 操作</div>' +
                '<div style="display: flex; gap: 8px; flex-wrap: wrap;">' +
                '<button class="nx-btn nx-btn--primary" onclick="invokeCapability(\'' + capabilityId + '\')">' +
                '<i class="ri-play-line"></i> 调用能力</button>' +
                '<button class="nx-btn nx-btn--secondary" onclick="toggleStatus(\'' + capabilityId + '\')">' +
                '<i class="ri-refresh-line"></i> ' + (cap.status === 'ENABLED' ? '停用' : '启用') + '</button>' +
                '<button class="nx-btn nx-btn--danger" onclick="uninstallCapability(\'' + capabilityId + '\')">' +
                '<i class="ri-delete-bin-line"></i> 卸载</button>' +
                '</div></div>';

            document.getElementById('detailBody').innerHTML = html;
            document.getElementById('detailPanel').classList.add('open');
            document.getElementById('overlay').classList.add('open');
        },

        closeDetail: function() {
            document.getElementById('detailPanel').classList.remove('open');
            document.getElementById('overlay').classList.remove('open');
        },

        toggleCapabilityStatus: function(capabilityId) {
            var cap = capabilities.find(function(c) { return c.capabilityId === capabilityId; });
            if (!cap) return;

            var newStatus = cap.status === 'ENABLED' ? 'DISABLED' : 'ENABLED';

            ApiClient.post('/api/v1/capabilities/' + capabilityId + '/status', { status: newStatus })
                .then(function(result) {
                    if (result.code === 200) {
                        cap.status = newStatus;
                        MyCapabilities.renderTable();
                        MyCapabilities.updateStats();
                    }
                })
                .catch(function(error) {
                    cap.status = newStatus;
                    MyCapabilities.renderTable();
                    MyCapabilities.updateStats();
                });
        },

        invokeCap: function(capabilityId) {
            var cap = capabilities.find(function(c) { return c.capabilityId === capabilityId; });
            if (!cap) return;
            
            var params = {};
            var paramsStr = prompt('请输入调用参数(JSON格式):', '{}');
            if (paramsStr) {
                try {
                    params = JSON.parse(paramsStr);
                } catch (e) {
                    alert('参数格式错误，请使用有效的JSON格式');
                    return;
                }
            }
            
            ApiClient.post('/api/v1/capabilities/discovery/invoke', {
                capabilityId: capabilityId,
                params: params
            })
                .then(function(result) {
                    if (result.code === 200) {
                        alert('调用成功!\n\n结果: ' + JSON.stringify(result.data, null, 2));
                        cap.invokeCount = (cap.invokeCount || 0) + 1;
                        cap.lastInvokeTime = Date.now();
                        MyCapabilities.renderTable();
                        MyCapabilities.updateStats();
                    } else {
                        alert('调用失败: ' + result.message);
                    }
                })
                .catch(function(error) {
                    alert('调用失败: ' + error.message);
                });
        },

        uninstallCap: function(capabilityId) {
            if (!confirm('确定要卸载能力 "' + capabilityId + '" 吗？')) return;

            ApiClient.delete('/api/v1/capabilities/' + capabilityId)
                .then(function(result) {
                    if (result.code === 200) {
                        capabilities = capabilities.filter(function(c) { return c.capabilityId !== capabilityId; });
                        MyCapabilities.renderTable();
                        MyCapabilities.updateStats();
                        MyCapabilities.closeDetail();
                    }
                })
                .catch(function(error) {
                    capabilities = capabilities.filter(function(c) { return c.capabilityId !== capabilityId; });
                    MyCapabilities.renderTable();
                    MyCapabilities.updateStats();
                    MyCapabilities.closeDetail();
                });
        },

        filter: function() {
            MyCapabilities.renderTable();
        },

        filterByStatus: function(status) {
            currentFilter = status;
            document.querySelectorAll('.filter-chip').forEach(function(chip) {
                chip.classList.toggle('active', chip.dataset.status === status);
            });
            MyCapabilities.renderTable();
        }
    };

    MyCapabilities.init();

    global.showDetail = MyCapabilities.showDetailPanel;
    global.closeDetailPanel = MyCapabilities.closeDetail;
    global.toggleStatus = MyCapabilities.toggleCapabilityStatus;
    global.invokeCapability = MyCapabilities.invokeCap;
    global.uninstallCapability = MyCapabilities.uninstallCap;
    global.filterCapabilities = MyCapabilities.filter;
    global.filterByStatus = MyCapabilities.filterByStatus;
    global.refreshCapabilities = MyCapabilities.loadCapabilities;
    global.goToActivation = function(installId) {
        window.location.href = '/console/pages/capability-activation.html?installId=' + installId;
    };
    global.showBindingModal = function(capabilityId) {
        window.location.href = '/console/pages/capability-binding.html?capabilityId=' + capabilityId;
    };
    global.unbindCapability = function(bindingId) {
        if (!confirm('确定要解绑此能力吗？')) return;
        bindings = bindings.filter(function(b) { return b.bindingId !== bindingId; });
        MyCapabilities.showDetailPanel(document.getElementById('detailTitle').textContent);
    };

})(typeof window !== 'undefined' ? window : this);
