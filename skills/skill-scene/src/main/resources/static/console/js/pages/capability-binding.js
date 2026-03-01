(function(global) {
    'use strict';

    var sceneGroups = [];
    var capabilities = [];
    var bindings = [];
    var currentSceneGroup = null;

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

    var CapabilityBinding = {
        init: function() {
            window.onPageInit = function() {
                console.log('能力绑定页面初始化完成');
                CapabilityBinding.loadSceneGroups();
                CapabilityBinding.loadCapabilities();
                CapabilityBinding.initLlmAssistant();
            };
        },

        initLlmAssistant: function() {
            if (typeof LlmAssistant !== 'undefined') {
                LlmAssistant.init();
            }
        },

        loadSceneGroups: function() {
            fetch('/api/v1/scene-groups')
                .then(function(response) { return response.json(); })
                .then(function(result) {
                    if (result.code === 200 && result.data) {
                        sceneGroups = result.data.list || result.data;
                        CapabilityBinding.renderSceneGroups();
                    }
                })
                .catch(function(error) {
                    console.error('加载场景组失败:', error);
                    CapabilityBinding.loadMockSceneGroups();
                });
        },

        loadMockSceneGroups: function() {
            sceneGroups = [
                { sceneGroupId: 'sg-daily-report', name: '日志汇报组', description: '日常工作日志汇报', status: 'ACTIVE' },
                { sceneGroupId: 'sg-project-alpha', name: '项目Alpha组', description: 'Alpha项目管理', status: 'ACTIVE' },
                { sceneGroupId: 'sg-meeting', name: '会议管理组', description: '会议安排与记录', status: 'INACTIVE' }
            ];
            CapabilityBinding.renderSceneGroups();
        },

        loadCapabilities: function() {
            fetch('/api/v1/capabilities')
                .then(function(response) { return response.json(); })
                .then(function(result) {
                    if (result.code === 200 && result.data) {
                        capabilities = result.data;
                        CapabilityBinding.renderCapabilitySelect();
                    }
                })
                .catch(function(error) {
                    console.error('加载能力失败:', error);
                    capabilities = [
                        { capabilityId: 'report-remind', name: '日志提醒', type: 'COMMUNICATION' },
                        { capabilityId: 'report-submit', name: '日志提交', type: 'SERVICE' },
                        { capabilityId: 'report-aggregate', name: '日志汇总', type: 'SERVICE' },
                        { capabilityId: 'notification-email', name: '邮件通知', type: 'COMMUNICATION' },
                        { capabilityId: 'notification-sms', name: '短信通知', type: 'COMMUNICATION' }
                    ];
                    CapabilityBinding.renderCapabilitySelect();
                });
        },

        renderSceneGroups: function() {
            var container = document.getElementById('sceneGroupList');
            if (!container) return;

            if (sceneGroups.length === 0) {
                container.innerHTML = '<div class="empty-state" style="padding: 40px 20px;">' +
                    '<i class="ri-group-line"></i>' +
                    '<div class="empty-state-title">暂无场景组</div></div>';
                return;
            }

            var html = '';
            sceneGroups.forEach(function(group) {
                var bindingCount = bindings.filter(function(b) { 
                    return b.sceneGroupId === group.sceneGroupId; 
                }).length;

                html += '<div class="scene-group-item" data-id="' + group.sceneGroupId + '" onclick="selectSceneGroup(\'' + group.sceneGroupId + '\')">' +
                    '<div class="group-icon"><i class="ri-group-line"></i></div>' +
                    '<div class="group-info">' +
                    '<div class="group-name">' + (group.name || group.sceneGroupId) + '</div>' +
                    '<div class="group-count">' + bindingCount + ' 个能力绑定</div>' +
                    '</div></div>';
            });
            container.innerHTML = html;
        },

        selectSceneGroup: function(sceneGroupId) {
            currentSceneGroup = sceneGroupId;

            document.querySelectorAll('.scene-group-item').forEach(function(item) {
                item.classList.remove('active');
                if (item.dataset.id === sceneGroupId) {
                    item.classList.add('active');
                }
            });

            var group = sceneGroups.find(function(g) { return g.sceneGroupId === sceneGroupId; });
            document.getElementById('currentGroupName').textContent = group ? (group.name || sceneGroupId) : sceneGroupId;
            document.getElementById('addBindingBtn').disabled = false;

            CapabilityBinding.loadBindings(sceneGroupId);
        },

        loadBindings: function(sceneGroupId) {
            fetch('/api/v1/capabilities/bindings?sceneGroupId=' + sceneGroupId)
                .then(function(response) { return response.json(); })
                .then(function(result) {
                    if (result.code === 200 && result.data) {
                        bindings = result.data;
                    } else {
                        bindings = [];
                    }
                    CapabilityBinding.renderBindings();
                    CapabilityBinding.renderRelationGraph();
                })
                .catch(function(error) {
                    console.error('加载绑定失败:', error);
                    bindings = [
                        {
                            bindingId: 'bind-001',
                            sceneGroupId: sceneGroupId,
                            capabilityId: 'report-remind',
                            capabilityName: '日志提醒',
                            capabilityType: 'COMMUNICATION',
                            providerType: 'SKILL',
                            priority: 1,
                            status: 'ACTIVE',
                            lastInvokeTime: Date.now() - 3600000
                        },
                        {
                            bindingId: 'bind-002',
                            sceneGroupId: sceneGroupId,
                            capabilityId: 'report-submit',
                            capabilityName: '日志提交',
                            capabilityType: 'SERVICE',
                            providerType: 'SKILL',
                            priority: 2,
                            status: 'ACTIVE',
                            lastInvokeTime: Date.now() - 7200000
                        }
                    ];
                    CapabilityBinding.renderBindings();
                    CapabilityBinding.renderRelationGraph();
                });
        },

        renderBindings: function() {
            var tbody = document.getElementById('bindingTableBody');
            var filteredBindings = bindings.filter(function(b) { 
                return b.sceneGroupId === currentSceneGroup; 
            });

            if (filteredBindings.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 40px; color: var(--nx-text-secondary);">' +
                    '<i class="ri-link" style="font-size: 32px; display: block; margin-bottom: 8px;"></i>' +
                    '暂无能力绑定，点击"添加绑定"开始配置</td></tr>';
                return;
            }

            var html = '';
            filteredBindings.forEach(function(binding) {
                var typeIcon = TYPE_ICONS[binding.capabilityType] || TYPE_ICONS['CUSTOM'];
                var statusClass = binding.status === 'ACTIVE' ? 'active' : 'inactive';
                var statusText = binding.status === 'ACTIVE' ? '已激活' : '未激活';
                var lastInvoke = binding.lastInvokeTime ? CapabilityBinding.formatTime(binding.lastInvokeTime) : '-';

                html += '<tr>' +
                    '<td><div class="cap-info">' +
                    '<div class="cap-icon type-' + (binding.capabilityType || 'CUSTOM') + '"><i class="' + typeIcon + '"></i></div>' +
                    '<div><div class="cap-name">' + (binding.capabilityName || binding.capabilityId) + '</div>' +
                    '<div class="cap-id">' + binding.capabilityId + '</div></div></div></td>' +
                    '<td>' + (binding.providerType || 'SKILL') + '</td>' +
                    '<td><span class="priority-badge">P' + (binding.priority || 1) + '</span></td>' +
                    '<td><span class="status-badge ' + statusClass + '">' + statusText + '</span></td>' +
                    '<td>' + lastInvoke + '</td>' +
                    '<td><div style="display: flex; gap: 8px;">' +
                    '<button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="editBinding(\'' + binding.bindingId + '\')" title="编辑">' +
                    '<i class="ri-edit-line"></i></button>' +
                    '<button class="nx-btn nx-btn--ghost nx-btn--sm nx-text-danger" onclick="deleteBinding(\'' + binding.bindingId + '\')" title="解绑">' +
                    '<i class="ri-link-unlink"></i></button>' +
                    '</div></td></tr>';
            });
            tbody.innerHTML = html;
        },

        renderRelationGraph: function() {
            var container = document.getElementById('relationGraph');
            var nodesContainer = document.getElementById('relationNodes');
            var filteredBindings = bindings.filter(function(b) { 
                return b.sceneGroupId === currentSceneGroup; 
            });

            if (filteredBindings.length === 0) {
                container.style.display = 'none';
                return;
            }

            container.style.display = 'block';

            var group = sceneGroups.find(function(g) { return g.sceneGroupId === currentSceneGroup; });
            var html = '<div class="relation-node primary"><i class="ri-group-line"></i> ' + (group ? group.name : currentSceneGroup) + '</div>';

            filteredBindings.forEach(function(binding, i) {
                html += '<span class="relation-arrow"><i class="ri-arrow-right-line"></i></span>';
                html += '<div class="relation-node"><i class="ri-flashlight-line"></i> ' + (binding.capabilityName || binding.capabilityId) + '</div>';
            });

            nodesContainer.innerHTML = html;
        },

        renderCapabilitySelect: function() {
            var select = document.getElementById('capabilitySelect');
            if (!select) return;

            var html = '<option value="">请选择能力</option>';
            capabilities.forEach(function(cap) {
                html += '<option value="' + cap.capabilityId + '">' + (cap.name || cap.capabilityId) + ' (' + cap.capabilityId + ')</option>';
            });
            select.innerHTML = html;
        },

        showAddModal: function() {
            if (!currentSceneGroup) {
                alert('请先选择场景组');
                return;
            }
            document.getElementById('addBindingModal').classList.add('show');
        },

        hideAddModal: function() {
            document.getElementById('addBindingModal').classList.remove('show');
        },

        createBindingRequest: function() {
            var capabilityId = document.getElementById('capabilitySelect').value;
            var providerType = document.getElementById('providerTypeSelect').value;
            var priority = document.getElementById('priorityInput').value;
            var connectorType = document.getElementById('connectorTypeSelect').value;

            if (!capabilityId) {
                alert('请选择能力');
                return;
            }

            var cap = capabilities.find(function(c) { return c.capabilityId === capabilityId; });

            var request = {
                sceneGroupId: currentSceneGroup,
                capabilityId: capabilityId,
                providerType: providerType,
                priority: parseInt(priority) || 1,
                connectorType: connectorType
            };

            fetch('/api/v1/capabilities/bindings', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(request)
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    bindings.push({
                        bindingId: result.data ? result.data.bindingId : 'bind-' + Date.now(),
                        sceneGroupId: currentSceneGroup,
                        capabilityId: capabilityId,
                        capabilityName: cap ? cap.name : capabilityId,
                        capabilityType: cap ? cap.type : 'CUSTOM',
                        providerType: providerType,
                        priority: parseInt(priority) || 1,
                        status: 'ACTIVE',
                        lastInvokeTime: null
                    });
                    CapabilityBinding.renderBindings();
                    CapabilityBinding.renderRelationGraph();
                    CapabilityBinding.hideAddModal();
                    CapabilityBinding.renderSceneGroups();
                } else {
                    alert('创建绑定失败: ' + result.message);
                }
            })
            .catch(function(error) {
                bindings.push({
                    bindingId: 'bind-' + Date.now(),
                    sceneGroupId: currentSceneGroup,
                    capabilityId: capabilityId,
                    capabilityName: cap ? cap.name : capabilityId,
                    capabilityType: cap ? cap.type : 'CUSTOM',
                    providerType: providerType,
                    priority: parseInt(priority) || 1,
                    status: 'ACTIVE',
                    lastInvokeTime: null
                });
                CapabilityBinding.renderBindings();
                CapabilityBinding.renderRelationGraph();
                CapabilityBinding.hideAddModal();
                CapabilityBinding.renderSceneGroups();
            });
        },

        deleteBindingRequest: function(bindingId) {
            if (!confirm('确定要解绑此能力吗？')) return;

            fetch('/api/v1/capabilities/bindings/' + bindingId, {
                method: 'DELETE'
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200) {
                    bindings = bindings.filter(function(b) { return b.bindingId !== bindingId; });
                    CapabilityBinding.renderBindings();
                    CapabilityBinding.renderRelationGraph();
                    CapabilityBinding.renderSceneGroups();
                }
            })
            .catch(function(error) {
                bindings = bindings.filter(function(b) { return b.bindingId !== bindingId; });
                CapabilityBinding.renderBindings();
                CapabilityBinding.renderRelationGraph();
                CapabilityBinding.renderSceneGroups();
            });
        },

        editBindingRequest: function(bindingId) {
            alert('编辑绑定: ' + bindingId + '\n\n此功能需要实现编辑弹窗');
        },

        formatTime: function(timestamp) {
            var now = Date.now();
            var diff = now - timestamp;
            
            if (diff < 60000) return '刚刚';
            if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
            if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
            return Math.floor(diff / 86400000) + '天前';
        }
    };

    CapabilityBinding.init();

    global.selectSceneGroup = CapabilityBinding.selectSceneGroup;
    global.showAddBindingModal = CapabilityBinding.showAddModal;
    global.hideAddBindingModal = CapabilityBinding.hideAddModal;
    global.createBinding = CapabilityBinding.createBindingRequest;
    global.deleteBinding = CapabilityBinding.deleteBindingRequest;
    global.editBinding = CapabilityBinding.editBindingRequest;

})(typeof window !== 'undefined' ? window : this);
