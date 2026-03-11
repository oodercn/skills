(function(global) {
    'use strict';

    var allCapabilities = [];
    var skillPackages = [];
    var icSkillPackages = [];
    var pcSkillPackages = [];
    var toolSkillPackages = [];
    var bindings = [];
    var currentFilter = 'all';
    var currentTypeFilter = 'all';
    var currentOwnershipFilter = 'all';
    var searchKeyword = '';
    var currentDrillDown = null;
    var currentPage = 1;
    var pageSize = 20;
    var totalPages = 1;
    var totalCount = 0;

    var OWNERSHIP_CONFIG = {
        'SIC': {
            name: '场景技能',
            shortName: 'SIC',
            icon: 'ri-puzzle-line',
            desc: '场景内可见，生命周期绑定场景',
            color: '#db2777',
            bgColor: '#fce7f3',
            features: ['场景内可见', '绑定场景', '生命周期绑定场景', '不可跨场景调用'],
            canBind: ['IC', 'PC']
        },
        'IC': {
            name: '独立能力',
            shortName: 'IC',
            icon: 'ri-share-line',
            desc: '跨场景复用，任务间隔离',
            color: '#059669',
            bgColor: '#d1fae5',
            features: ['跨场景复用', '独立部署', '支持多场景组', '任务间隔离'],
            canBind: []
        },
        'PC': {
            name: '平台能力',
            shortName: 'PC',
            icon: 'ri-global-line',
            desc: '全局可见，无需绑定场景',
            color: '#d97706',
            bgColor: '#fef3c7',
            features: ['全局可见', '平台管理', '直接调用', '无场景限制'],
            canBind: []
        },
        'TOOL': {
            name: '工具技能',
            shortName: 'TOOL',
            icon: 'ri-tools-line',
            desc: '轻量级工具，独立运行',
            color: '#4f46e5',
            bgColor: '#e0e7ff',
            features: ['轻量级', '无状态', '独立运行', '工具功能'],
            canBind: []
        }
    };

    var CAPABILITY_TYPE_CONFIG = {
        'ATOMIC': { name: '原子能力', shortName: 'AC', icon: 'ri-flashlight-line', desc: '单一功能，不可分解' },
        'COMPOSITE': { name: '组合能力', shortName: 'COMP', icon: 'ri-stack-line', desc: '组合多个原子能力' },
        'SCENE': { name: '场景特性', shortName: 'SC', icon: 'ri-layout-grid-line', desc: '自驱型SuperAgent能力' },
        'DRIVER': { name: '驱动能力', shortName: 'DC', icon: 'ri-timer-line', desc: '意图/时间/事件驱动' },
        'SERVICE': { name: '服务能力', shortName: 'SVC', icon: 'ri-server-line', desc: '业务服务、API服务' },
        'AI': { name: 'AI能力', shortName: 'AI', icon: 'ri-brain-line', desc: 'LLM、机器学习' },
        'TOOL': { name: '工具能力', shortName: 'TOOL', icon: 'ri-tools-line', desc: '工具类功能' },
        'CONNECTOR': { name: '连接器能力', shortName: 'CONN', icon: 'ri-plug-line', desc: '连接协议类' },
        'DATA': { name: '数据能力', shortName: 'DATA', icon: 'ri-database-2-line', desc: '数据存储、处理' },
        'MANAGEMENT': { name: '管理能力', shortName: 'MGT', icon: 'ri-settings-3-line', desc: '系统管理' },
        'COMMUNICATION': { name: '通信能力', shortName: 'COMM', icon: 'ri-message-3-line', desc: '消息通信' },
        'SECURITY': { name: '安全能力', shortName: 'SEC', icon: 'ri-shield-check-line', desc: '安全认证' },
        'MONITORING': { name: '监控能力', shortName: 'MON', icon: 'ri-pulse-line', desc: '系统监控' },
        'SKILL': { name: '技能能力', shortName: 'SKILL', icon: 'ri-puzzle-line', desc: '技能包' },
        'CUSTOM': { name: '自定义能力', shortName: 'CUST', icon: 'ri-code-line', desc: '用户自定义' }
    };

    var MyCapabilities = {
        init: function() {
            window.onPageInit = function() {
                console.log('我的能力页面初始化');
                MyCapabilities.parseUrlParams();
                MyCapabilities.loadCapabilities();
                MyCapabilities.loadBindings();
            };
        },
        
        parseUrlParams: function() {
            var urlParams = new URLSearchParams(window.location.search);
            var typeParam = urlParams.get('type');
            
            if (typeParam) {
                if (typeParam === 'SCENE') {
                    currentOwnershipFilter = 'SIC';
                } else if (typeParam === 'SKILL') {
                    currentOwnershipFilter = 'IC';
                } else if (typeParam === 'TOOL') {
                    currentOwnershipFilter = 'TOOL';
                }
            }
        },

        loadCapabilities: function() {
            var filters = {
                ownership: currentOwnershipFilter !== 'all' ? currentOwnershipFilter : null,
                keyword: searchKeyword || null
            };
            
            CapabilityService.searchByFilters(filters)
                .then(function(list) {
                    allCapabilities = list || [];
                    MyCapabilities.groupBySkillPackage();
                    MyCapabilities.updateStats();
                    MyCapabilities.updateFilterChips();
                    MyCapabilities.renderTable();
                })
                .catch(function(error) {
                    console.error('加载能力列表失败:', error);
                    MyCapabilities.showError('加载能力列表失败: ' + error.message);
                });
        },

        groupBySkillPackage: function() {
            skillPackages = [];
            icSkillPackages = [];
            pcSkillPackages = [];
            toolSkillPackages = [];
            
            var sicMap = {};
            var icMap = {};
            var pcMap = {};
            var toolMap = {};

            allCapabilities.forEach(function(cap) {
                var skillId = cap.skillId || (cap.metadata && cap.metadata.skillId) || 'unknown';
                var ownership = CapabilityService._normalizeOwnership(cap.ownership);
                cap._ownership = ownership;
                cap._skillId = skillId;

                var targetMap, targetList;
                if (ownership === 'SIC') {
                    targetMap = sicMap;
                    targetList = skillPackages;
                } else if (ownership === 'IC') {
                    targetMap = icMap;
                    targetList = icSkillPackages;
                } else if (ownership === 'PC') {
                    targetMap = pcMap;
                    targetList = pcSkillPackages;
                } else if (ownership === 'TOOL') {
                    targetMap = toolMap;
                    targetList = toolSkillPackages;
                }

                if (!targetMap[skillId]) {
                    var pkg = {
                        skillId: skillId,
                        name: MyCapabilities.formatSkillName(skillId),
                        capabilityCount: 0,
                        capabilities: [],
                        ownership: ownership,
                        types: {},
                        status: 'ENABLED',
                        version: cap.version || '1.0.0'
                    };
                    targetMap[skillId] = pkg;
                    targetList.push(pkg);
                }
                targetMap[skillId].capabilityCount++;
                targetMap[skillId].capabilities.push(cap);
                if (cap.type) {
                    targetMap[skillId].types[cap.type] = true;
                }
                if (cap.status && !targetMap[skillId].status) {
                    targetMap[skillId].status = cap.status;
                }
            });

            console.log('=== 技能包分类统计 ===');
            console.log('场景技能(SIC):', skillPackages.length, '个技能包');
            console.log('独立能力(IC):', icSkillPackages.length, '个技能包');
            console.log('平台能力(PC):', pcSkillPackages.length, '个技能包');
            console.log('工具技能(TOOL):', toolSkillPackages.length, '个技能包');
        },

        formatSkillName: function(skillId) {
            return skillId
                .replace(/^skill-/, '')
                .replace(/-nexus-ui$/, '')
                .replace(/-/g, ' ')
                .replace(/\b\w/g, function(l) { return l.toUpperCase(); });
        },

        loadBindings: function() {
            ApiClient.get('/api/v1/capabilities/bindings')
                .then(function(result) {
                    if (result.data) {
                        bindings = result.data;
                    }
                })
                .catch(function(error) {
                    console.error('加载绑定失败:', error);
                    bindings = [];
                });
        },

        updateStats: function() {
            document.getElementById('statTotal').textContent = skillPackages.length + icSkillPackages.length + pcSkillPackages.length + toolSkillPackages.length;
            document.getElementById('statSIC').textContent = skillPackages.length;
            document.getElementById('statIC').textContent = icSkillPackages.length;
            document.getElementById('statPC').textContent = pcSkillPackages.length;
            document.getElementById('statTOOL').textContent = toolSkillPackages.length;
        },

        updateFilterChips: function() {
            var statusChips = document.getElementById('statusFilterChips');
            var typeChips = document.getElementById('typeFilterChips');
            
            if (!statusChips || !typeChips) return;

            var statusHtml = '<span class="filter-chip' + (currentFilter === 'all' ? ' active' : '') + '" data-status="all" onclick="filterByStatus(\'all\')">全部</span>' +
                '<span class="filter-chip' + (currentFilter === 'ENABLED' ? ' active' : '') + '" data-status="ENABLED" onclick="filterByStatus(\'ENABLED\')">已启用</span>' +
                '<span class="filter-chip' + (currentFilter === 'DISABLED' ? ' active' : '') + '" data-status="DISABLED" onclick="filterByStatus(\'DISABLED\')">已停用</span>';
            
            var typeHtml = '<span class="filter-chip' + (currentTypeFilter === 'all' ? ' active' : '') + '" data-type="all" onclick="filterByType(\'all\')">全部</span>';

            statusChips.innerHTML = statusHtml;
            typeChips.innerHTML = typeHtml;
        },

        renderTable: function() {
            if (currentOwnershipFilter === 'SIC') {
                MyCapabilities.renderSkillPackages(skillPackages, 'SIC');
            } else if (currentOwnershipFilter === 'IC') {
                MyCapabilities.renderSkillPackages(icSkillPackages, 'IC');
            } else if (currentOwnershipFilter === 'PC') {
                MyCapabilities.renderSkillPackages(pcSkillPackages, 'PC');
            } else if (currentOwnershipFilter === 'TOOL') {
                MyCapabilities.renderSkillPackages(toolSkillPackages, 'TOOL');
            } else {
                MyCapabilities.renderAllPackages();
            }
        },

        renderAllPackages: function() {
            var tbody = document.getElementById('capabilityTableBody');
            var allPackages = skillPackages.concat(icSkillPackages).concat(pcSkillPackages).concat(toolSkillPackages);
            
            var filtered = allPackages.filter(function(pkg) {
                var searchMatch = !searchKeyword || 
                    (pkg.name && pkg.name.toLowerCase().indexOf(searchKeyword.toLowerCase()) >= 0) ||
                    (pkg.skillId && pkg.skillId.toLowerCase().indexOf(searchKeyword.toLowerCase()) >= 0);
                var statusMatch = currentFilter === 'all' || pkg.status === currentFilter;
                return searchMatch && statusMatch;
            });

            totalCount = filtered.length;
            totalPages = Math.ceil(totalCount / pageSize) || 1;
            
            if (currentPage > totalPages) {
                currentPage = totalPages;
            }
            
            var startIdx = (currentPage - 1) * pageSize;
            var endIdx = Math.min(startIdx + pageSize, totalCount);
            var pagedData = filtered.slice(startIdx, endIdx);

            if (filtered.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; padding: 60px; color: var(--nx-text-secondary);">' +
                    '<i class="ri-inbox-line" style="font-size: 48px; display: block; margin-bottom: 16px; opacity: 0.5;"></i>' +
                    '<div style="font-size: 16px; font-weight: 500;">暂无技能包数据</div></td></tr>';
                MyCapabilities.renderPagination(0);
                return;
            }

            var html = '';
            pagedData.forEach(function(pkg) {
                var config = OWNERSHIP_CONFIG[pkg.ownership] || OWNERSHIP_CONFIG['PC'];
                var typeLabels = Object.keys(pkg.types).map(function(t) {
                    var tc = CAPABILITY_TYPE_CONFIG[t] || CAPABILITY_TYPE_CONFIG['CUSTOM'];
                    return tc.shortName;
                }).join(', ');
                
                html += '<tr>' +
                    '<td><div class="cap-info">' +
                    '<div class="cap-icon ownership-' + pkg.ownership + '"><i class="' + config.icon + '"></i></div>' +
                    '<div><div class="cap-name">' + pkg.name + '</div>' +
                    '<div class="cap-id">' + pkg.skillId + '</div></div></div></td>' +
                    '<td><span class="ownership-badge ' + pkg.ownership.toLowerCase() + '">' + config.shortName + '</span></td>' +
                    '<td>' + pkg.capabilityCount + '</td>' +
                    '<td>' + (typeLabels || '-') + '</td>' +
                    '<td>' + pkg.version + '</td>' +
                    '<td><span class="status-badge ' + (pkg.status === 'ENABLED' ? 'enabled' : 'disabled') + '">' + 
                    (pkg.status === 'ENABLED' ? '已启用' : '已停用') + '</span></td>' +
                    '<td>' + MyCapabilities.getActionButtons(pkg) + '</td>' +
                    '</tr>';
            });
            
            tbody.innerHTML = html;
            MyCapabilities.renderPagination(filtered.length);
        },

        renderSkillPackages: function(packages, ownership) {
            var tbody = document.getElementById('capabilityTableBody');
            
            var filtered = packages.filter(function(pkg) {
                var searchMatch = !searchKeyword || 
                    (pkg.name && pkg.name.toLowerCase().indexOf(searchKeyword.toLowerCase()) >= 0) ||
                    (pkg.skillId && pkg.skillId.toLowerCase().indexOf(searchKeyword.toLowerCase()) >= 0);
                var statusMatch = currentFilter === 'all' || pkg.status === currentFilter;
                return searchMatch && statusMatch;
            });
            
            totalCount = filtered.length;
            totalPages = Math.ceil(totalCount / pageSize) || 1;
            
            var startIdx = (currentPage - 1) * pageSize;
            var endIdx = Math.min(startIdx + pageSize, totalCount);
            var pagedData = filtered.slice(startIdx, endIdx);

            if (filtered.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; padding: 60px; color: var(--nx-text-secondary);">' +
                    '<i class="ri-inbox-line" style="font-size: 48px; display: block; margin-bottom: 16px; opacity: 0.5;"></i>' +
                    '<div style="font-size: 16px; font-weight: 500;">暂无' + (OWNERSHIP_CONFIG[ownership]?.name || '技能包') + '</div></td></tr>';
                return;
            }

            var html = '';
            pagedData.forEach(function(pkg) {
                var config = OWNERSHIP_CONFIG[pkg.ownership] || OWNERSHIP_CONFIG['PC'];
                var typeLabels = Object.keys(pkg.types).map(function(t) {
                    var tc = CAPABILITY_TYPE_CONFIG[t] || CAPABILITY_TYPE_CONFIG['CUSTOM'];
                    return tc.shortName;
                }).join(', ');
                
                html += '<tr>' +
                    '<td><div class="cap-info">' +
                    '<div class="cap-icon ownership-' + pkg.ownership + '"><i class="' + config.icon + '"></i></div>' +
                    '<div><div class="cap-name">' + pkg.name + '</div>' +
                    '<div class="cap-id">' + pkg.skillId + '</div></div></div></td>' +
                    '<td><span class="ownership-badge ' + pkg.ownership.toLowerCase() + '">' + config.shortName + '</span></td>' +
                    '<td>' + pkg.capabilityCount + '</td>' +
                    '<td>' + (typeLabels || '-') + '</td>' +
                    '<td>' + pkg.version + '</td>' +
                    '<td><span class="status-badge ' + (pkg.status === 'ENABLED' ? 'enabled' : 'disabled') + '">' + 
                    (pkg.status === 'ENABLED' ? '已启用' : '已停用') + '</span></td>' +
                    '<td>' + MyCapabilities.getActionButtons(pkg) + '</td>' +
                    '</tr>';
            });
            
            tbody.innerHTML = html;
            MyCapabilities.renderPagination(filtered.length);
        },
        
        renderPagination: function(total) {
            var container = document.getElementById('pagination-container');
            if (!container) {
                console.log('[renderPagination] Container not found');
                return;
            }
            
            console.log('[renderPagination] total:', total, 'totalPages:', totalPages, 'currentPage:', currentPage);
            
            if (totalPages <= 1) {
                container.innerHTML = '<div class="nx-flex nx-items-center nx-gap-4"><span class="nx-text-sm nx-text-secondary">共 ' + total + ' 条记录</span></div>';
                return;
            }
            
            var html = '<div class="nx-pagination">';
            
            html += '<button class="nx-pagination__btn" onclick="goToPage(1)" ' + (currentPage === 1 ? 'disabled' : '') + ' title="首页"><i class="ri-skip-back-line"></i></button>';
            html += '<button class="nx-pagination__btn" onclick="goToPage(' + (currentPage - 1) + ')" ' + (currentPage === 1 ? 'disabled' : '') + ' title="上一页"><i class="ri-arrow-left-s-line"></i></button>';
            
            var startPage = Math.max(1, currentPage - 2);
            var endPage = Math.min(totalPages, startPage + 4);
            
            for (var i = startPage; i <= endPage; i++) {
                html += '<button class="nx-pagination__btn ' + (i === currentPage ? 'nx-pagination__btn--active' : '') + '" onclick="goToPage(' + i + ')">' + i + '</button>';
            }
            
            html += '<button class="nx-pagination__btn" onclick="goToPage(' + (currentPage + 1) + ')" ' + (currentPage === totalPages ? 'disabled' : '') + ' title="下一页"><i class="ri-arrow-right-s-line"></i></button>';
            html += '<button class="nx-pagination__btn" onclick="goToPage(' + totalPages + ')" ' + (currentPage === totalPages ? 'disabled' : '') + ' title="末页"><i class="ri-skip-forward-line"></i></button>';
            
            html += '</div>';
            html += '<div class="nx-flex nx-items-center nx-gap-2 nx-ml-4">';
            html += '<span class="nx-text-sm nx-text-secondary">第 ' + currentPage + ' / ' + totalPages + ' 页</span>';
            html += '<span class="nx-text-sm nx-text-secondary">共 ' + total + ' 条</span>';
            html += '</div>';
            
            container.innerHTML = html;
        },
        
        goToPage: function(page) {
            if (page < 1 || page > totalPages || page === currentPage) return;
            currentPage = page;
            MyCapabilities.renderTable();
        },
        
        changePageSize: function(newSize) {
            pageSize = newSize;
            currentPage = 1;
            MyCapabilities.renderTable();
        },

        getActionButtons: function(pkg) {
            var buttons = '<button class="nx-btn nx-btn--sm nx-btn--secondary" onclick="drillDownSkill(\'' + pkg.skillId + '\')">' +
                '<i class="ri-eye-line"></i> 详情</button> ';
            
            if (pkg.ownership === 'SIC') {
                buttons += '<button class="nx-btn nx-btn--sm nx-btn--primary" onclick="showBindDialog(\'' + pkg.skillId + '\')">' +
                    '<i class="ri-link"></i> 绑定</button>';
            }
            
            return buttons;
        },

        showError: function(message) {
            var tbody = document.getElementById('capabilityTableBody');
            if (tbody) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; padding: 60px; color: #ef4444;">' +
                    '<i class="ri-error-warning-line" style="font-size: 48px; display: block; margin-bottom: 16px;"></i>' +
                    '<div style="font-size: 16px; font-weight: 500;">' + message + '</div></td></tr>';
            }
        }
    };

    global.filterByOwnership = function(ownership) {
        currentOwnershipFilter = ownership;
        currentFilter = 'all';
        currentTypeFilter = 'all';
        
        document.querySelectorAll('.ownership-tab').forEach(function(tab) {
            tab.classList.remove('active');
        });
        document.querySelector('.ownership-tab[data-ownership="' + ownership + '"]')?.classList.add('active');
        
        MyCapabilities.updateFilterChips();
        MyCapabilities.renderTable();
    };

    global.filterByStatus = function(status) {
        currentFilter = status;
        document.querySelectorAll('#statusFilterChips .filter-chip').forEach(function(chip) {
            chip.classList.remove('active');
        });
        document.querySelector('#statusFilterChips .filter-chip[data-status="' + status + '"]')?.classList.add('active');
        MyCapabilities.renderTable();
    };

    global.filterByType = function(type) {
        currentTypeFilter = type;
        document.querySelectorAll('#typeFilterChips .filter-chip').forEach(function(chip) {
            chip.classList.remove('active');
        });
        document.querySelector('#typeFilterChips .filter-chip[data-type="' + type + '"]')?.classList.add('active');
        MyCapabilities.renderTable();
    };

    global.searchCapabilities = function(keyword) {
        searchKeyword = keyword;
        MyCapabilities.renderTable();
    };

    global.drillDownSkill = function(skillId) {
        currentDrillDown = skillId;
        var pkg = skillPackages.find(function(p) { return p.skillId === skillId; }) ||
                  icSkillPackages.find(function(p) { return p.skillId === skillId; }) ||
                  pcSkillPackages.find(function(p) { return p.skillId === skillId; }) ||
                  toolSkillPackages.find(function(p) { return p.skillId === skillId; });
        
        if (pkg) {
            console.log('查看技能包详情:', pkg);
        }
    };

    global.showBindDialog = function(skillId) {
        console.log('显示绑定对话框:', skillId);
    };

    global.refreshCapabilities = function() {
        CapabilityService.clearCache();
        MyCapabilities.loadCapabilities();
    };

    global.goToPage = function(page) {
        MyCapabilities.goToPage(page);
    };

    global.changePageSize = function(newSize) {
        MyCapabilities.changePageSize(newSize);
    };

    MyCapabilities.init();

})(typeof window !== 'undefined' ? window : this);
