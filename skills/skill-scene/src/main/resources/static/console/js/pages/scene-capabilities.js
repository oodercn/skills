(function(global) {
    'use strict';

    var allCapabilities = [];
    var filteredCapabilities = [];
    var currentFilter = 'all';
    var searchKeyword = '';

    var CATEGORY_CONFIG = {
        'FULL': { 
            icon: 'ri-layout-grid-line', 
            name: '完整场景技能',
            description: '具备完整自驱能力的业务场景',
            color: '#2563eb'
        },
        'TECHNICAL': { 
            icon: 'ri-settings-4-line', 
            name: '技术场景技能',
            description: '系统内部技术流程场景',
            color: '#d97706'
        },
        'SEMI_AUTO': { 
            icon: 'ri-hand-coin-line', 
            name: '半自动场景技能',
            description: '需要外部触发的业务场景',
            color: '#db2777'
        },
        'default': { 
            icon: 'ri-flashlight-line', 
            name: '其他',
            description: '',
            color: '#64748b'
        }
    };

    var SceneCapabilities = {
        init: function() {
            window.onPageInit = function() {
                console.log('[SceneCapabilities] 页面初始化完成');
                SceneCapabilities.loadCapabilities();
            };
        },

        loadCapabilities: function() {
            ApiClient.post('/api/v1/discovery/gitee', { repoUrl: 'https://gitee.com/ooderCN/skills' })
                .then(function(result) {
                    console.log('[SceneCapabilities] API response:', result);
                    if (result.code === 200 && result.data) {
                        allCapabilities = (result.data.capabilities || []).filter(function(cap) {
                            return cap.sceneCapability === true || cap.sceneCapability === 'true' || cap.type === 'SCENE';
                        });
                        console.log('[SceneCapabilities] Filtered capabilities:', allCapabilities.length);
                        SceneCapabilities.updateStats();
                        SceneCapabilities.renderCapabilities();
                    } else if (result.status === 'success' && result.data) {
                        allCapabilities = (result.data.capabilities || []).filter(function(cap) {
                            return cap.sceneCapability === true || cap.sceneCapability === 'true' || cap.type === 'SCENE';
                        });
                        console.log('[SceneCapabilities] Filtered capabilities (status):', allCapabilities.length);
                        SceneCapabilities.updateStats();
                        SceneCapabilities.renderCapabilities();
                    }
                })
                .catch(function(error) {
                    console.error('[SceneCapabilities] 加载失败:', error);
                    allCapabilities = [];
                    SceneCapabilities.updateStats();
                    SceneCapabilities.renderCapabilities();
                });
        },

        updateStats: function() {
            var installed = allCapabilities.filter(function(c) { return c.status === 'installed'; }).length;
            var available = allCapabilities.filter(function(c) { return c.status !== 'installed'; }).length;
            var fullCount = allCapabilities.filter(function(c) { return c.category === 'FULL'; }).length;
            var technicalCount = allCapabilities.filter(function(c) { return c.category === 'TECHNICAL'; }).length;
            var semiAutoCount = allCapabilities.filter(function(c) { return c.category === 'SEMI_AUTO'; }).length;

            document.getElementById('totalCount').textContent = allCapabilities.length;
            document.getElementById('installedCount').textContent = installed;
            document.getElementById('availableCount').textContent = available;
            document.getElementById('categoryCount').textContent = fullCount + technicalCount + semiAutoCount;
        },

        filterCapabilities: function() {
            filteredCapabilities = allCapabilities.filter(function(cap) {
                var matchCategory = currentFilter === 'all' || cap.category === currentFilter;
                var matchSearch = !searchKeyword || 
                    (cap.name && cap.name.toLowerCase().includes(searchKeyword.toLowerCase())) ||
                    (cap.id && cap.id.toLowerCase().includes(searchKeyword.toLowerCase())) ||
                    (cap.description && cap.description.toLowerCase().includes(searchKeyword.toLowerCase()));
                return matchCategory && matchSearch;
            });
        },

        getCategoryName: function(category) {
            var config = CATEGORY_CONFIG[category] || CATEGORY_CONFIG['default'];
            return config.name;
        },

        getCategoryIcon: function(category) {
            var config = CATEGORY_CONFIG[category] || CATEGORY_CONFIG['default'];
            return config.icon;
        },

        getInstallHint: function(cap) {
            if (cap.status === 'installed') {
                return null;
            }
            
            var category = cap.category;
            var hints = [];
            
            if (category === 'FULL') {
                hints.push('安装后将自动激活并按计划运行');
            } else if (category === 'TECHNICAL') {
                hints.push('安装后将自动执行系统维护任务');
            } else if (category === 'SEMI_AUTO') {
                hints.push('安装后需要手动触发执行');
            }
            
            if (cap.driverConditions && cap.driverConditions.length > 0) {
                hints.push('包含 ' + cap.driverConditions.length + ' 个驱动条件');
            }
            
            if (cap.participants && cap.participants.length > 0) {
                hints.push('需要配置 ' + cap.participants.length + ' 个参与者');
            }
            
            return hints.length > 0 ? hints.join('；') : null;
        },

        renderCapabilities: function() {
            SceneCapabilities.filterCapabilities();
            var container = document.getElementById('capabilitiesGrid');

            if (filteredCapabilities.length === 0) {
                container.innerHTML = '<div class="empty-state">' +
                    '<i class="ri-layout-grid-line"></i>' +
                    '<div class="empty-state-title">暂无场景能力</div>' +
                    '<div class="empty-state-desc">请检查网络连接或稍后重试</div>' +
                    '</div>';
                return;
            }

            var html = '';
            filteredCapabilities.forEach(function(cap) {
                var category = cap.category || 'default';
                var categoryConfig = CATEGORY_CONFIG[category] || CATEGORY_CONFIG['default'];
                var icon = categoryConfig.icon;
                var statusClass = cap.status === 'installed' ? 'installed' : 'available';
                var statusText = cap.status === 'installed' ? '已安装' : '可安装';
                
                var categoryBadgeHtml = category !== 'default' 
                    ? '<span class="category-badge ' + category + '"><i class="' + icon + '"></i> ' + categoryConfig.name + '</span>'
                    : '';
                
                var mainFirstBadgeHtml = cap.mainFirst !== undefined
                    ? '<span class="mainfirst-badge' + (cap.mainFirst ? '' : ' manual') + '">' +
                      '<i class="' + (cap.mainFirst ? 'ri-flashlight-line' : 'ri-hand-coin-line') + '"></i> ' +
                      (cap.mainFirst ? '自驱' : '手动') + '</span>'
                    : '';
                
                var visibilityBadgeHtml = cap.visibility 
                    ? '<span class="visibility-badge ' + cap.visibility + '">' +
                      (cap.visibility === 'internal' ? '内部' : '公开') + '</span>'
                    : '';
                
                var installHint = SceneCapabilities.getInstallHint(cap);
                var installHintHtml = installHint && cap.status !== 'installed'
                    ? '<div class="install-hint"><i class="ri-information-line"></i>' + installHint + '</div>'
                    : '';

                html += '<div class="capability-card" data-id="' + cap.id + '" data-category="' + category + '">' +
                    '<div class="card-header">' +
                    '<div class="card-icon ' + category + '"><i class="' + icon + '"></i></div>' +
                    '<div class="card-info">' +
                    '<div class="card-title">' + (cap.name || cap.id) + 
                        categoryBadgeHtml + mainFirstBadgeHtml + visibilityBadgeHtml + '</div>' +
                    '<div class="card-id">' + cap.id + '</div>' +
                    '</div>' +
                    '<span class="card-status ' + statusClass + '">' + statusText + '</span>' +
                    '</div>' +
                    '<div class="card-body">' +
                    '<div class="card-desc">' + (cap.description || '暂无描述') + '</div>' +
                    '<div class="card-meta">' +
                    '<span><i class="ri-information-line"></i> v' + (cap.version || '1.0.0') + '</span>' +
                    '<span><i class="ri-folder-line"></i> ' + categoryConfig.name + '</span>' +
                    '</div>' +
                    installHintHtml +
                    '</div>' +
                    '<div class="card-footer">' +
                    (cap.status === 'installed' 
                        ? '<button class="nx-btn nx-btn--primary" onclick="useCapability(\'' + cap.id + '\')"><i class="ri-play-line"></i> 使用</button>' +
                          '<button class="nx-btn nx-btn--secondary" onclick="viewDetail(\'' + cap.id + '\')"><i class="ri-eye-line"></i> 详情</button>'
                        : '<button class="nx-btn nx-btn--primary" onclick="installCapability(\'' + cap.id + '\')"><i class="ri-download-line"></i> 安装</button>' +
                          '<button class="nx-btn nx-btn--secondary" onclick="viewDetail(\'' + cap.id + '\')"><i class="ri-eye-line"></i> 详情</button>') +
                    '</div>' +
                    '</div>';
            });
            container.innerHTML = html;
        }
    };

    SceneCapabilities.init();

    global.filterByCategory = function(category) {
        currentFilter = category;
        document.querySelectorAll('.filter-chip').forEach(function(chip) {
            chip.classList.remove('active');
            if (chip.dataset.filter === category) {
                chip.classList.add('active');
            }
        });
        SceneCapabilities.renderCapabilities();
    };

    global.searchCapabilities = function() {
        searchKeyword = document.getElementById('searchInput').value;
        SceneCapabilities.renderCapabilities();
    };

    global.useCapability = function(capabilityId) {
        window.location.href = '/console/pages/scene-capability-detail.html?id=' + capabilityId + '&action=use';
    };

    global.viewDetail = function(capabilityId) {
        window.location.href = '/console/pages/scene-capability-detail.html?id=' + capabilityId;
    };

    global.installCapability = function(capabilityId) {
        var card = document.querySelector('.capability-card[data-id="' + capabilityId + '"]');
        var btn = card.querySelector('.nx-btn--primary');
        var originalHtml = btn.innerHTML;
        btn.innerHTML = '<i class="ri-loader-4-line ri-spin"></i> 安装中...';
        btn.disabled = true;

        var cap = allCapabilities.find(function(c) { return c.id === capabilityId; });
        var category = cap ? (cap.category || 'FULL') : 'FULL';
        
        ApiClient.post('/api/v1/discovery/install', { skillId: capabilityId, source: 'GITEE' })
            .then(function(result) {
                if ((result.status === 'success' || result.code === 200) && result.data) {
                    btn.innerHTML = '<i class="ri-check-line"></i> 已安装';
                    btn.classList.remove('nx-btn--primary');
                    btn.classList.add('nx-btn--secondary');
                    btn.onclick = function() { useCapability(capabilityId); };
                    
                    var statusBadge = card.querySelector('.card-status');
                    statusBadge.classList.remove('available');
                    statusBadge.classList.add('installed');
                    statusBadge.textContent = '已安装';

                    var hintEl = card.querySelector('.install-hint');
                    if (hintEl) {
                        var nextStep = '';
                        if (category === 'FULL') {
                            nextStep = '场景已自动激活，将按计划运行';
                        } else if (category === 'TECHNICAL') {
                            nextStep = '技术场景已激活，将自动执行';
                        } else if (category === 'SEMI_AUTO') {
                            nextStep = '请手动触发场景执行';
                        }
                        hintEl.innerHTML = '<i class="ri-checkbox-circle-line" style="color: #059669;"></i>' + nextStep;
                    }

                    if (cap) cap.status = 'installed';
                    SceneCapabilities.updateStats();
                } else {
                    btn.innerHTML = originalHtml;
                    btn.disabled = false;
                    alert('安装失败: ' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                btn.innerHTML = originalHtml;
                btn.disabled = false;
                alert('安装失败: ' + error.message);
            });
    };

})(typeof window !== 'undefined' ? window : this);
