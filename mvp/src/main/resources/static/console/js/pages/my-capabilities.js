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
            MyCapabilities.parseUrlParams();
            MyCapabilities.loadCapabilities();
            MyCapabilities.loadBindings();
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

            fetch('/api/v1/scene/capabilities', {
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
            fetch('/api/v1/scene/capabilities/bindings', {
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
            
            document.querySelectorAll('#categoryFilterChipsRow1 .filter-chip, #categoryFilterChipsRow2 .filter-chip').forEach(function(chip) {
                var cat = chip.getAttribute('data-category');
                if (cat === 'all') {
                    chip.innerHTML = '全部 (' + totalCount + ')';
                } else if (counts[cat] !== undefined) {
                    var config = CATEGORY_CONFIG[cat];
                    if (config) {
                        chip.innerHTML = config.name + ' (' + counts[cat] + ')';
                    }
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
            actionsCell.innerHTML = MyCapabilities.getActionButtons(cap);
            tr.appendChild(actionsCell);

            return tr;
        },

        getActionButtons: function(cap) {
            var buttons = '<button class="nx-btn nx-btn--sm nx-btn--secondary" onclick="event.stopPropagation(); drillDownSkill(\'' + cap.capabilityId + '\')">' +
                '<i class="ri-eye-line"></i> 详情</button> ';
            
            if (cap.installed === true) {
                buttons += '<button class="nx-btn nx-btn--sm nx-btn--primary" onclick="event.stopPropagation(); showBindDialog(\'' + cap.capabilityId + '\')">' +
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
        },

        showDetailPanel: function(cap) {
            var detailPanel = document.getElementById('detailPanel');
            var detailTitle = document.getElementById('detailTitle');
            var detailBody = document.getElementById('detailBody');
            var overlay = document.getElementById('overlay');

            if (!detailPanel || !detailBody) {
                console.error('详情面板元素未找到');
                return;
            }

            detailTitle.textContent = cap.name || '能力详情';

            var categoryConfig = CATEGORY_CONFIG[cap.category] || CATEGORY_CONFIG['util'];
            
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

            var html = '';
            
            html += '<div class="detail-section">';
            html += '<div class="detail-section-title"><i class="ri-information-line"></i> 基本信息</div>';
            html += '<div class="detail-row"><span class="detail-row-label">能力ID</span><span class="detail-row-value">' + (cap.capabilityId || '-') + '</span></div>';
            html += '<div class="detail-row"><span class="detail-row-label">描述</span><span class="detail-row-value">' + (cap.description || '-') + '</span></div>';
            html += '<div class="detail-row"><span class="detail-row-label">分类</span><span class="detail-row-value"><span style="background:' + categoryConfig.color + ';color:white;padding:2px 8px;border-radius:4px;font-size:12px;">' + categoryConfig.name + '</span></span></div>';
            html += '<div class="detail-row"><span class="detail-row-label">状态</span><span class="detail-row-value status-badge ' + statusClass + '">' + statusText + '</span></div>';
            html += '<div class="detail-row"><span class="detail-row-label">版本</span><span class="detail-row-value">' + (cap.version || '-') + '</span></div>';
            html += '<div class="detail-row"><span class="detail-row-label">类型</span><span class="detail-row-value">' + (cap.type || '-') + '</span></div>';
            html += '</div>';

            if (cap.skillId) {
                html += '<div class="detail-section">';
                html += '<div class="detail-section-title"><i class="ri-puzzle-line"></i> 技能信息</div>';
                html += '<div class="detail-row"><span class="detail-row-label">技能ID</span><span class="detail-row-value">' + cap.skillId + '</span></div>';
                html += '<div class="detail-row"><span class="detail-row-label">技能形态</span><span class="detail-row-value">' + (cap.skillForm || '-') + '</span></div>';
                html += '</div>';
            }

            if (cap.supportedSceneTypes && cap.supportedSceneTypes.length > 0) {
                html += '<div class="detail-section">';
                html += '<div class="detail-section-title"><i class="ri-artboard-line"></i> 支持场景类型</div>';
                html += '<div class="scene-types-list">';
                cap.supportedSceneTypes.forEach(function(st) {
                    html += '<span class="scene-type-tag">' + st + '</span>';
                });
                html += '</div>';
                html += '</div>';
            }

            if (cap.features && cap.features.length > 0) {
                html += '<div class="detail-section">';
                html += '<div class="detail-section-title"><i class="ri-star-line"></i> 功能特性</div>';
                html += '<div class="features-grid">';
                cap.features.forEach(function(f) {
                    html += '<span class="feature-tag"><i class="ri-check-line"></i> ' + f + '</span>';
                });
                html += '</div>';
                html += '</div>';
            }

            var capBindings = bindings.filter(function(b) { return b.capabilityId === cap.capabilityId; });
            if (capBindings.length > 0) {
                html += '<div class="detail-section">';
                html += '<div class="detail-section-title"><i class="ri-link"></i> 绑定场景 (' + capBindings.length + ')</div>';
                html += '<div class="binding-list">';
                capBindings.forEach(function(b) {
                    html += '<div class="binding-item">';
                    html += '<div class="binding-info">';
                    html += '<div class="binding-icon"><i class="ri-artboard-line"></i></div>';
                    html += '<div><div class="binding-name">' + (b.sceneName || b.sceneId) + '</div>';
                    html += '<div class="binding-scene">' + (b.sceneType || '') + '</div></div>';
                    html += '</div>';
                    html += '</div>';
                });
                html += '</div>';
                html += '</div>';
            }

            html += '<div class="detail-section">';
            html += '<div class="detail-section-title"><i class="ri-time-line"></i> 时间信息</div>';
            html += '<div class="detail-row"><span class="detail-row-label">创建时间</span><span class="detail-row-value">' + (cap.createTime ? MyCapabilities.formatTime(cap.createTime) : '-') + '</span></div>';
            html += '<div class="detail-row"><span class="detail-row-label">更新时间</span><span class="detail-row-value">' + (cap.updateTime ? MyCapabilities.formatTime(cap.updateTime) : '-') + '</span></div>';
            html += '</div>';

            detailBody.innerHTML = html;
            detailPanel.classList.add('open');
            overlay.classList.add('open');
        },

        formatTime: function(timestamp) {
            if (!timestamp) return '-';
            var date = new Date(timestamp);
            return date.toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        }
    };

    global.filterByCategory = function(category) {
        currentCategoryFilter = category;
        currentFilter = 'all';
        
        document.querySelectorAll('#categoryFilterChipsRow1 .filter-chip, #categoryFilterChipsRow2 .filter-chip').forEach(function(chip) {
            chip.classList.remove('active');
        });
        var activeChip = document.querySelector('#categoryFilterChipsRow1 .filter-chip[data-category="' + category + '"], #categoryFilterChipsRow2 .filter-chip[data-category="' + category + '"]');
        if (activeChip) activeChip.classList.add('active');
        
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

    global.drillDownSkill = function(capabilityId) {
        currentDrillDown = capabilityId;
        var cap = allCapabilities.find(function(p) { return p.capabilityId === capabilityId; });
        
        if (cap) {
            console.log('查看能力详情:', cap);
            MyCapabilities.showDetailPanel(cap);
        } else {
            console.warn('未找到能力:', capabilityId);
        }
    };

    global.showBindDialog = function(capabilityId) {
        console.log('显示绑定对话框:', capabilityId);
        var cap = allCapabilities.find(function(p) { return p.capabilityId === capabilityId; });
        if (cap) {
            alert('绑定功能开发中...\n能力: ' + cap.name);
        }
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
