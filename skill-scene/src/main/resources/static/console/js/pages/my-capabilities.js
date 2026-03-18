(function(global) {
    'use strict';

    var CATEGORY_CONFIG = OoderCapability.CATEGORY_CONFIG;
    var getAddressHex = OoderCapability.getAddressHex;

    var allCapabilities = [];
    var bindings = [];
    var currentFilter = 'all';
    var currentCategoryFilter = 'all';
    var searchKeyword = '';
    var currentDrillDown = null;
    var currentPage = 1;
    var pageSize = 20;
    var totalPages = 1;
    var totalCount = 0;

    var MyCapabilities = {
        init: function() {
            console.log('我的能力页面初始化');
            MyCapabilities.renderCategoryTabs();
            MyCapabilities.parseUrlParams();
            MyCapabilities.loadCapabilities();
            MyCapabilities.loadBindings();
        },

        renderCategoryTabs: function() {
            var container = document.getElementById('categoryTabs');
            if (!container) return;
            
            var html = '';
            
            html += '<div class="category-tab active" data-category="all" onclick="filterByCategory(\'all\')">' +
                '<i class="ri-apps-line"></i> 全部</div>';
            
            Object.keys(CATEGORY_CONFIG).forEach(function(cat) {
                var config = CATEGORY_CONFIG[cat];
                html += '<div class="category-tab" data-category="' + cat + '" onclick="filterByCategory(\'' + cat + '\')">' +
                    '<i class="' + config.icon + '"></i> ' + config.name + '</div>';
            });
            
            container.innerHTML = html;
        },

        parseUrlParams: function() {
            var urlParams = new URLSearchParams(window.location.search);
            var categoryParam = urlParams.get('category');
            if (categoryParam && CATEGORY_CONFIG[categoryParam]) {
                currentCategoryFilter = categoryParam;
            }
        },

        loadCapabilities: function() {
            var filters = {
                category: currentCategoryFilter !== 'all' ? currentCategoryFilter : null,
                keyword: searchKeyword || null
            };

            fetch('/api/v1/capabilities', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result && result.status === 'success' && result.data) {
                    allCapabilities = result.data;
                    totalCount = allCapabilities.length;
                    totalPages = Math.ceil(totalCount / pageSize);
                    MyCapabilities.updateStats();
                    MyCapabilities.renderTable();
                } else {
                    console.error('API返回错误:', result);
                    MyCapabilities.showError(result.message || '加载能力列表失败');
                }
            })
            .catch(function(error) {
                console.error('加载能力失败:', error);
                MyCapabilities.showError('加载能力列表失败');
            });
        },

        loadBindings: function() {
            fetch('/api/v1/capabilities/bindings', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(function(response) { return response.json(); })
            .then(function(data) {
                bindings = data || [];
            })
            .catch(function(error) {
                console.error('加载绑定失败:', error);
            });
        },

        updateStats: function() {
            document.getElementById('statTotal').textContent = totalCount;
            
            var counts = {};
            var installedCount = 0;
            var activeCount = 0;
            
            Object.keys(CATEGORY_CONFIG).forEach(function(cat) {
                counts[cat] = 0;
            });
            
            allCapabilities.forEach(function(cap) {
                var category = cap.category || 'util';
                if (counts[category] !== undefined) {
                    counts[category]++;
                }
                if (cap.installed === true) {
                    installedCount++;
                    if (cap.enabled === true) {
                        activeCount++;
                    }
                }
            });
            
            var installedEl = document.getElementById('statInstalled');
            if (installedEl) {
                installedEl.textContent = installedCount;
            }
            
            var activeEl = document.getElementById('statActive');
            if (activeEl) {
                activeEl.textContent = activeCount;
            }
            
            Object.keys(counts).forEach(function(cat) {
                var el = document.getElementById('stat' + cat.toUpperCase());
                if (el) {
                    el.textContent = counts[cat];
                }
            });
        },

        updateFilterChips: function() {
            var html = '';
            html += '<div class="filter-chip active" data-status="all" onclick="filterByStatus(\'all\')">全部</div>';
            html += '<div class="filter-chip" data-status="installed" onclick="filterByStatus(\'installed\')">已安装</div>';
            html += '<div class="filter-chip" data-status="active" onclick="filterByStatus(\'active\')">运行中</div>';
            html += '<div class="filter-chip" data-status="disabled" onclick="filterByStatus(\'disabled\')">已禁用</div>';
            
            document.getElementById('statusFilterChips').innerHTML = html;
        },

        renderTable: function() {
            var tbody = document.getElementById('capabilityTableBody');
            tbody.innerHTML = '';

            var filtered = allCapabilities;

            if (currentCategoryFilter !== 'all') {
                filtered = filtered.filter(function(cap) {
                    return cap.category === currentCategoryFilter;
                });
            }

            if (currentFilter !== 'all') {
                filtered = filtered.filter(function(cap) {
                    if (currentFilter === 'installed') {
                        return cap.installed === true;
                    } else if (currentFilter === 'active') {
                        return cap.installed === true && cap.enabled === true;
                    } else if (currentFilter === 'disabled') {
                        return cap.enabled === false;
                    }
                    return true;
                });
            }

            if (searchKeyword) {
                filtered = filtered.filter(function(cap) {
                    var name = cap.name.toLowerCase();
                    var desc = cap.description.toLowerCase();
                    return name.includes(searchKeyword) || 
                           desc.includes(searchKeyword);
                });
            }

            var start = (currentPage - 1) * pageSize;
            var end = Math.min(start + pageSize, filtered.length);

            filtered.slice(start, end).forEach(function(cap) {
                var tr = MyCapabilities.renderRow(cap);
                tbody.appendChild(tr);
            });
        },

        renderRow: function(cap) {
            var tr = document.createElement('tr');
            tr.setAttribute('data-skill-id', cap.skillId);
            tr.style.cursor = 'pointer';

            var categoryConfig = CATEGORY_CONFIG[cap.category] || CATEGORY_CONFIG['util'];
            
            var categoryBadge = '<span class="category-badge" style="background-color: ' + categoryConfig.color + '; color: white;">' + categoryConfig.name + '</span>';

            var nameCell = document.createElement('td');
            nameCell.innerHTML = '<strong>' + cap.name + '</strong>' + categoryBadge;
            nameCell.style.fontSize = '14px';
            tr.appendChild(nameCell);

            var descCell = document.createElement('td');
            descCell.innerHTML = cap.description || '-';
            descCell.style.fontSize = '13px';
            descCell.style.color = '#666';
            tr.appendChild(descCell);

            var statusCell = document.createElement('td');
            var statusText = '';
            var statusClass = '';
            
            if (cap.installed === true && cap.enabled === true) {
                statusText = '运行中';
                statusClass = 'status-active';
            } else if (cap.installed === true) {
                statusText = '已安装';
                statusClass = 'status-installed';
            } else if (cap.enabled === false) {
                statusText = '已禁用';
                statusClass = 'status-disabled';
            } else {
                statusText = cap.status || '未安装';
                statusClass = 'status-other';
            }
            
            statusCell.className = 'status-badge ' + statusClass;
            statusCell.innerHTML = statusText;
            tr.appendChild(statusCell);

            var actionsCell = document.createElement('td');
            actionsCell.style.textAlign = 'right';
            actionsCell.appendChild(MyCapabilities.getActionButtons(cap));
            tr.appendChild(actionsCell);

            return tr;
        },

        getActionButtons: function(cap) {
            var buttons = '<button class="nx-btn nx-btn--sm nx-btn--secondary" onclick="drillDownSkill(\'' + cap.skillId + '\')">' +
                '<i class="ri-eye-line"></i> 详情</button> ';
            
            if (cap.installed === true) {
                buttons += '<button class="nx-btn nx-btn--sm nx-btn--primary" onclick="showBindDialog(\'' + cap.skillId + '\')">' +
                    '<i class="ri-link"></i> 绑定</button>';
            }
            
            return buttons;
        },

        showError: function(message) {
            var tbody = document.getElementById('capabilityTableBody');
            if (tbody) {
                tbody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 60px; color: #ef4444;">' +
                    '<i class="ri-error-warning-line" style="font-size: 48px; display: block; margin-bottom: 16px;"></i>' +
                    '<div style="font-size: 16px; font-weight: 500;">' + message + '</div></td></tr>';
            }
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
        }
    };

    global.filterByCategory = function(category) {
        currentCategoryFilter = category;
        currentFilter = 'all';
        
        document.querySelectorAll('.category-tab').forEach(function(tab) {
            tab.classList.remove('active');
        });
        var activeTab = document.querySelector('.category-tab[data-category="' + category + '"]');
        if (activeTab) activeTab.classList.add('active');
        
        document.querySelectorAll('.stat-card').forEach(function(card) {
            card.classList.remove('active');
        });
        var activeCard = document.querySelector('.stat-card[data-category="' + category + '"]');
        if (activeCard) activeCard.classList.add('active');
        
        MyCapabilities.renderTable();
    };

    global.filterByStatus = function(status) {
        currentFilter = status;
        document.querySelectorAll('#statusFilterChips .filter-chip').forEach(function(chip) {
            chip.classList.remove('active');
        });
        var activeChip = document.querySelector('#statusFilterChips .filter-chip[data-status="' + status + '"]');
        if (activeChip) activeChip.classList.add('active');
        MyCapabilities.renderTable();
    };

    global.searchCapabilities = function(keyword) {
        searchKeyword = keyword;
        MyCapabilities.renderTable();
    };

    global.drillDownSkill = function(skillId) {
        currentDrillDown = skillId;
        var pkg = allCapabilities.find(function(p) { return p.skillId === skillId; });
        
        if (pkg) {
            console.log('查看技能包详情:', pkg);
        }
    };

    global.showBindDialog = function(skillId) {
        console.log('显示绑定对话框:', skillId);
    };

    global.refreshCapabilities = function() {
        MyCapabilities.loadCapabilities();
    };

    global.goToPage = function(page) {
        MyCapabilities.goToPage(page);
    };

    global.changePageSize = function(newSize) {
        MyCapabilities.changePageSize(newSize);
    };

    global.closeDetailPanel = function() {
        document.getElementById('detailPanel').classList.remove('open');
        document.getElementById('overlay').classList.remove('open');
    };

    MyCapabilities.init();

})(typeof window !== 'undefined' ? window : this);
