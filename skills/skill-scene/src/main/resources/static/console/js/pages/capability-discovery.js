(function(global) {
    'use strict';

    var currentMethod = null;
    var isScanning = false;
    var discoveredCapabilities = [];
    var installedCapabilities = [];
    var logs = [];
    var scanStats = { scanned: 0, found: 0, new: 0, installed: 0 };

    var DISCOVERY_METHODS = [
        {
            id: 'LOCAL_FS',
            name: '本地文件系统',
            icon: 'ri-folder-line',
            desc: '扫描本地已安装的能力包',
            color: '#3b82f6',
            requiresConfig: false
        },
        {
            id: 'SKILL_CENTER',
            name: '能力中心',
            icon: 'ri-cloud-line',
            desc: '从能力中心发现可用能力',
            color: '#10b981',
            requiresConfig: true,
            configFields: [
                { name: 'centerUrl', label: '能力中心地址', type: 'text', default: 'https://skill.ooder.cn/api' }
            ]
        },
        {
            id: 'GITHUB',
            name: 'GitHub仓库',
            icon: 'ri-github-fill',
            desc: '从GitHub仓库发现能力',
            color: '#6366f1',
            requiresConfig: true,
            configFields: [
                { name: 'repoUrl', label: '仓库地址', type: 'text', placeholder: '如: https://github.com/user/skills' },
                { name: 'branch', label: '分支', type: 'text', default: 'main' },
                { name: 'token', label: '访问令牌(可选)', type: 'password' }
            ]
        },
        {
            id: 'GITEE',
            name: 'Gitee仓库',
            icon: 'ri-git-repository-line',
            desc: '从Gitee仓库发现能力',
            color: '#ef4444',
            requiresConfig: true,
            configFields: [
                { name: 'repoUrl', label: '仓库地址', type: 'text', placeholder: '如: https://gitee.com/user/skills' },
                { name: 'branch', label: '分支', type: 'text', default: 'master' },
                { name: 'token', label: '访问令牌(可选)', type: 'password' }
            ]
        },
        {
            id: 'GIT_REPOSITORY',
            name: 'Git仓库',
            icon: 'ri-git-branch-line',
            desc: '从任意Git仓库发现能力',
            color: '#f59e0b',
            requiresConfig: true,
            configFields: [
                { name: 'repoUrl', label: '仓库地址', type: 'text', placeholder: 'Git仓库URL' },
                { name: 'branch', label: '分支', type: 'text', default: 'main' },
                { name: 'username', label: '用户名(可选)', type: 'text' },
                { name: 'password', label: '密码/令牌(可选)', type: 'password' }
            ]
        },
        {
            id: 'UDP_BROADCAST',
            name: 'UDP广播',
            icon: 'ri-broadcast-line',
            desc: '通过UDP广播发现局域网能力',
            color: '#8b5cf6',
            requiresConfig: true,
            configFields: [
                { name: 'port', label: '广播端口', type: 'number', default: '8089' },
                { name: 'timeout', label: '超时时间(秒)', type: 'number', default: '10' }
            ]
        },
        {
            id: 'MDNS_DNS_SD',
            name: 'mDNS/DNS-SD',
            icon: 'ri-wifi-line',
            desc: '通过mDNS发现局域网能力',
            color: '#06b6d4',
            requiresConfig: true,
            configFields: [
                { name: 'serviceType', label: '服务类型', type: 'text', default: '_ooder-cap._tcp' },
                { name: 'timeout', label: '超时时间(秒)', type: 'number', default: '15' }
            ]
        },
        {
            id: 'DHT_KADEMLIA',
            name: 'DHT/Kademlia',
            icon: 'ri-share-line',
            desc: '通过DHT网络发现能力',
            color: '#ec4899',
            requiresConfig: true,
            configFields: [
                { name: 'bootstrapNodes', label: '引导节点', type: 'text', placeholder: '如: node1:8080,node2:8080' },
                { name: 'timeout', label: '超时时间(秒)', type: 'number', default: '30' }
            ]
        },
        {
            id: 'AUTO',
            name: '自动检测',
            icon: 'ri-magic-line',
            desc: '自动选择最佳发现方式',
            color: '#64748b',
            requiresConfig: false
        }
    ];

    var CapabilityDiscovery = {
        init: function() {
            window.onPageInit = function() {
                console.log('能力发现页面初始化完成');
                CapabilityDiscovery.renderMethods();
                CapabilityDiscovery.loadInstalled();
                CapabilityDiscovery.initFilters();
                CapabilityDiscovery.initLlmAssistant();
            };
        },

        initLlmAssistant: function() {
            if (typeof LlmAssistant !== 'undefined') {
                LlmAssistant.init();
            }
        },

        renderMethods: function() {
            var container = document.getElementById('methodList');
            if (!container) return;

            var html = '';
            DISCOVERY_METHODS.forEach(function(method) {
                html += '<div class="method-item" data-method="' + method.id + '" onclick="selectMethod(\'' + method.id + '\')">' +
                    '<div class="method-icon" style="background: ' + method.color + '20; color: ' + method.color + ';">' +
                    '<i class="' + method.icon + '"></i></div>' +
                    '<div class="method-info">' +
                    '<div class="method-name">' + method.name + '</div>' +
                    '<div class="method-desc">' + method.desc + '</div></div>' +
                    '<span class="method-badge ready" id="badge-' + method.id + '">就绪</span></div>';
            });
            container.innerHTML = html;
        },

        loadInstalled: function() {
            fetch('/api/v1/capabilities')
                .then(function(response) { return response.json(); })
                .then(function(result) {
                    if (result.code === 200 && result.data) {
                        installedCapabilities = result.data;
                        scanStats.installed = installedCapabilities.length;
                        CapabilityDiscovery.updateStats();
                    }
                })
                .catch(function(error) {
                    console.error('加载已安装能力失败:', error);
                });
        },

        selectMethod: function(methodId) {
            currentMethod = methodId;
            
            document.querySelectorAll('.method-item').forEach(function(item) {
                item.classList.remove('active');
                if (item.dataset.method === methodId) {
                    item.classList.add('active');
                }
            });

            var method = DISCOVERY_METHODS.find(function(m) { return m.id === methodId; });
            if (method) {
                document.getElementById('radarTitle').textContent = method.name + ' 扫描';
                
                if (method.requiresConfig) {
                    CapabilityDiscovery.showConfig(method);
                } else {
                    CapabilityDiscovery.hideConfig();
                }
            }
        },

        showConfig: function(method) {
            var panel = document.getElementById('configPanel');
            var form = document.getElementById('configForm');
            var title = document.getElementById('configTitle');

            title.textContent = method.name + ' 配置';

            var html = '';
            method.configFields.forEach(function(field) {
                var colClass = method.configFields.length === 1 ? 'full' : '';
                html += '<div class="form-group ' + colClass + '">' +
                    '<label class="form-label">' + field.label + '</label>';
                
                if (field.type === 'password') {
                    html += '<input type="password" class="form-input" id="config_' + field.name + '" placeholder="' + (field.placeholder || '') + '">';
                } else if (field.type === 'number') {
                    html += '<input type="number" class="form-input" id="config_' + field.name + '" value="' + (field.default || '') + '">';
                } else {
                    html += '<input type="text" class="form-input" id="config_' + field.name + '" value="' + (field.default || '') + '" placeholder="' + (field.placeholder || '') + '">';
                }
                html += '</div>';
            });
            form.innerHTML = html;
            panel.classList.add('show');
        },

        hideConfig: function() {
            document.getElementById('configPanel').classList.remove('show');
        },

        startScan: function() {
            if (!currentMethod) {
                alert('请先选择发现途径');
                return;
            }

            if (isScanning) return;
            isScanning = true;

            var sweep = document.getElementById('radarSweep');
            sweep.classList.remove('idle');

            document.getElementById('startBtn').disabled = true;
            document.getElementById('radarStatusText').textContent = '扫描中...';
            document.getElementById('badge-' + currentMethod).textContent = '扫描中';
            document.getElementById('badge-' + currentMethod).className = 'method-badge running';

            scanStats = { scanned: 0, found: 0, new: 0, installed: installedCapabilities.length };
            CapabilityDiscovery.updateStats();
            CapabilityDiscovery.addLog('info', '开始扫描: ' + currentMethod);

            var method = DISCOVERY_METHODS.find(function(m) { return m.id === currentMethod; });
            
            if (currentMethod === 'GITHUB') {
                CapabilityDiscovery.discoverFromGitService('/api/v1/discovery/github', method);
            } else if (currentMethod === 'GITEE') {
                CapabilityDiscovery.discoverFromGitService('/api/v1/discovery/gitee', method);
            } else if (currentMethod === 'GIT_REPOSITORY') {
                CapabilityDiscovery.discoverFromGitService('/api/v1/discovery/git', method);
            } else {
                var url = '/api/v1/capabilities/discovery?method=' + currentMethod;
                CapabilityDiscovery.discoverFromLocal(url);
            }
        },

        discoverFromGitService: function(apiUrl, method) {
            var config = {};
            if (method && method.configFields) {
                method.configFields.forEach(function(field) {
                    var el = document.getElementById('config_' + field.name);
                    if (el) {
                        config[field.name] = el.value;
                    }
                });
            }

            fetch(apiUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(config)
            })
            .then(function(response) { return response.json(); })
            .then(function(result) {
                if (result.code === 200 && result.data) {
                    var data = result.data;
                    var caps = data.capabilities || [];
                    
                    caps.forEach(function(cap) {
                        var exists = installedCapabilities.find(function(i) { 
                            return i.capabilityId === cap.id; 
                        });
                        if (!exists) {
                            discoveredCapabilities.push({
                                capabilityId: cap.id,
                                name: cap.name,
                                type: cap.type,
                                description: cap.description,
                                version: cap.version,
                                source: currentMethod,
                                dependencies: [],
                                provider: null
                            });
                        }
                    });

                    scanStats.found = caps.length;
                    scanStats.scanned = 100;

                    CapabilityDiscovery.addLog('success', '从 ' + currentMethod + ' 发现 ' + caps.length + ' 个能力');
                    if (data.repositories) {
                        CapabilityDiscovery.addLog('info', '扫描了 ' + data.repositories.length + ' 个仓库');
                    }
                    CapabilityDiscovery.renderResults();
                    CapabilityDiscovery.addRadarDots();
                } else {
                    CapabilityDiscovery.addLog('error', '扫描失败: ' + (result.message || '未知错误'));
                }
            })
            .catch(function(error) {
                console.error('Discovery error:', error);
                CapabilityDiscovery.addLog('error', '扫描失败: ' + error.message);
                CapabilityDiscovery.simulateDiscovery();
            })
            .finally(function() {
                CapabilityDiscovery.finishScan();
            });
        },

        discoverFromLocal: function(url) {

            fetch(url)
                .then(function(response) { return response.json(); })
                .then(function(result) {
                    if (result.code === 200 && result.data) {
                        discoveredCapabilities = result.data;
                        scanStats.found = discoveredCapabilities.length;
                        scanStats.scanned = 100;

                        discoveredCapabilities.forEach(function(cap) {
                            var exists = installedCapabilities.find(function(i) { 
                                return i.capabilityId === cap.id; 
                            });
                            if (!exists) {
                                scanStats.new++;
                            }
                        });

                        CapabilityDiscovery.addLog('success', '扫描完成，发现 ' + scanStats.found + ' 个能力');
                        CapabilityDiscovery.renderResults();
                        CapabilityDiscovery.addRadarDots();
                    } else {
                        CapabilityDiscovery.addLog('error', '扫描失败: ' + (result.message || '未知错误'));
                    }
                })
                .catch(function(error) {
                    console.error('Scan error:', error);
                    CapabilityDiscovery.addLog('error', '扫描失败: ' + error.message);
                    CapabilityDiscovery.simulateDiscovery();
                })
                .finally(function() {
                    CapabilityDiscovery.finishScan();
                });
        },

        finishScan: function() {
            isScanning = false;
            var sweep = document.getElementById('radarSweep');
            if (sweep) sweep.classList.add('idle');
            document.getElementById('startBtn').disabled = false;
            document.getElementById('radarStatusText').textContent = '扫描完成';
            document.getElementById('badge-' + currentMethod).textContent = '完成';
            document.getElementById('badge-' + currentMethod).className = 'method-badge done';
            CapabilityDiscovery.updateStats();
        },

        simulateDiscovery: function() {
            var mockData = [
                { id: 'report-analyze', name: '日志分析', type: 'AI', description: 'AI分析日志内容', version: '1.0.0', source: currentMethod },
                { id: 'data-backup', name: '数据备份', type: 'STORAGE', description: '自动备份场景数据', version: '1.0.0', source: currentMethod },
                { id: 'system-monitor', name: '系统监控', type: 'SERVICE', description: '监控系统运行状态', version: '1.0.0', source: currentMethod }
            ];

            discoveredCapabilities = mockData;
            scanStats.found = mockData.length;
            scanStats.scanned = 100;
            scanStats.new = mockData.length;

            CapabilityDiscovery.addLog('warn', '使用模拟数据');
            CapabilityDiscovery.renderResults();
            CapabilityDiscovery.addRadarDots();
        },

        addRadarDots: function() {
            var container = document.getElementById('radarDots');
            container.innerHTML = '';

            discoveredCapabilities.slice(0, 8).forEach(function(cap, i) {
                var dot = document.createElement('div');
                dot.className = 'radar-dot';
                var angle = (i / Math.min(discoveredCapabilities.length, 8)) * Math.PI * 2;
                var radius = 60 + Math.random() * 30;
                var x = 100 + Math.cos(angle) * radius;
                var y = 100 + Math.sin(angle) * radius;
                dot.style.left = x + 'px';
                dot.style.top = y + 'px';
                dot.style.animationDelay = (i * 0.2) + 's';
                dot.title = cap.name || cap.id;
                container.appendChild(dot);
            });
        },

        updateStats: function() {
            document.getElementById('statScanned').textContent = scanStats.scanned;
            document.getElementById('statFound').textContent = scanStats.found;
            document.getElementById('statNew').textContent = scanStats.new;
            document.getElementById('statInstalled').textContent = scanStats.installed;
        },

        renderResults: function() {
            var container = document.getElementById('resultsBody');
            document.getElementById('resultsCount').textContent = discoveredCapabilities.length;

            if (discoveredCapabilities.length === 0) {
                container.innerHTML = '<div class="empty-state">' +
                    '<i class="ri-inbox-line"></i>' +
                    '<div class="empty-state-title">未发现能力</div>' +
                    '<div class="empty-state-desc">尝试其他发现途径</div></div>';
                return;
            }

            var html = '';
            discoveredCapabilities.forEach(function(cap) {
                var isInstalled = installedCapabilities.find(function(i) { 
                    return i.capabilityId === cap.id; 
                });
                var typeIcon = CapabilityDiscovery.getTypeIcon(cap.type);

                html += '<div class="result-item">' +
                    '<div class="result-icon type-' + (cap.type || 'CUSTOM') + '">' +
                    '<i class="' + typeIcon + '"></i></div>' +
                    '<div class="result-info">' +
                    '<div class="result-name">' + (cap.name || cap.id) + '</div>' +
                    '<div class="result-id">' + cap.id + '</div>' +
                    '<div class="result-desc">' + (cap.description || '暂无描述') + '</div>' +
                    '<div class="result-meta">' +
                    '<span class="meta-item"><i class="ri-git-branch-line"></i> v' + (cap.version || '1.0.0') + '</span>' +
                    '<span class="meta-item"><i class="ri-cloud-line"></i> ' + (cap.source || currentMethod) + '</span>' +
                    '</div></div>' +
                    '<div class="result-actions">';

                if (isInstalled) {
                    html += '<button class="nx-btn nx-btn--secondary nx-btn--sm" disabled>已安装</button>';
                } else {
                    html += '<button class="nx-btn nx-btn--primary nx-btn--sm" onclick="installCapability(\'' + cap.id + '\')">' +
                        '<i class="ri-download-line"></i> 安装</button>';
                }

                html += '</div></div>';
            });
            container.innerHTML = html;
        },

        getTypeIcon: function(type) {
            var icons = {
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
            return icons[type] || icons['CUSTOM'];
        },

        installCap: async function(capId) {
            var cap = discoveredCapabilities.find(function(c) { return c.id === capId; });
            if (!cap) return;

            var modal = document.getElementById('installModal');
            var steps = document.getElementById('installSteps');
            var progress = document.getElementById('installProgress');

            var installSteps = [
                { name: '下载能力包', status: 'pending' },
                { name: '验证完整性', status: 'pending' },
                { name: '解析依赖', status: 'pending' },
                { name: '安装能力', status: 'pending' },
                { name: '注册服务', status: 'pending' }
            ];

            var html = '';
            installSteps.forEach(function(step, i) {
                html += '<div class="install-step" id="step-' + i + '">' +
                    '<div class="step-icon pending"><i class="ri-time-line"></i></div>' +
                    '<div class="step-info">' +
                    '<div class="step-name">' + step.name + '</div>' +
                    '<div class="step-status">等待中</div></div></div>';
            });
            steps.innerHTML = html;

            modal.classList.add('show');
            document.getElementById('installCancel').style.display = 'inline-flex';
            document.getElementById('installDone').style.display = 'none';

            var currentStep = 0;
            var stepInterval = setInterval(function() {
                if (currentStep < installSteps.length) {
                    var stepEl = document.getElementById('step-' + currentStep);
                    var icon = stepEl.querySelector('.step-icon');
                    var status = stepEl.querySelector('.step-status');

                    if (currentStep > 0) {
                        var prevStepEl = document.getElementById('step-' + (currentStep - 1));
                        prevStepEl.querySelector('.step-icon').className = 'step-icon done';
                        prevStepEl.querySelector('.step-icon').innerHTML = '<i class="ri-check-line"></i>';
                        prevStepEl.querySelector('.step-status').textContent = '完成';
                    }

                    icon.className = 'step-icon running';
                    icon.innerHTML = '<i class="ri-loader-4-line" style="animation: spin 1s linear infinite;"></i>';
                    status.textContent = '处理中...';

                    progress.style.width = ((currentStep + 1) / installSteps.length * 100) + '%';
                    currentStep++;
                }
            }, 600);

            try {
                var response = await fetch('/api/v1/discovery/install', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({
                        skillId: cap.id,
                        name: cap.name,
                        type: cap.type,
                        description: cap.description,
                        source: currentMethod
                    })
                });
                var result = await response.json();

                clearInterval(stepInterval);

                if (result.code === 200) {
                    for (var i = 0; i < installSteps.length; i++) {
                        var stepEl = document.getElementById('step-' + i);
                        if (stepEl) {
                            stepEl.querySelector('.step-icon').className = 'step-icon done';
                            stepEl.querySelector('.step-icon').innerHTML = '<i class="ri-check-line"></i>';
                            stepEl.querySelector('.step-status').textContent = '完成';
                        }
                    }
                    progress.style.width = '100%';

                    installedCapabilities.push({
                        capabilityId: cap.id,
                        name: cap.name,
                        type: cap.type,
                        description: cap.description
                    });
                    scanStats.installed = installedCapabilities.length;
                    scanStats.new = Math.max(0, scanStats.new - 1);
                    CapabilityDiscovery.updateStats();
                    CapabilityDiscovery.renderResults();
                } else {
                    for (var i = 0; i < installSteps.length; i++) {
                        var stepEl = document.getElementById('step-' + i);
                        if (stepEl && stepEl.querySelector('.step-status')) {
                            stepEl.querySelector('.step-status').textContent = '失败';
                            stepEl.querySelector('.step-icon').className = 'step-icon error';
                            stepEl.querySelector('.step-icon').innerHTML = '<i class="ri-error-warning-line"></i>';
                        }
                    }
                    CapabilityDiscovery.addLog('error', '安装失败: ' + result.message);
                }
            } catch (error) {
                clearInterval(stepInterval);
                console.error('Install failed:', error);
                CapabilityDiscovery.addLog('error', '安装失败: ' + error.message);
            }

            document.getElementById('installCancel').style.display = 'none';
            document.getElementById('installDone').style.display = 'inline-flex';
        },

        closeInstallModal: function() {
            document.getElementById('installModal').classList.remove('show');
        },

        addLog: function(level, message) {
            logs.unshift({
                time: new Date().toLocaleTimeString(),
                level: level,
                message: message
            });
            if (logs.length > 50) logs.pop();
            CapabilityDiscovery.renderLogs();
        },

        renderLogs: function() {
            var container = document.getElementById('logsBody');
            var html = '';
            logs.forEach(function(log) {
                html += '<div class="log-entry">' +
                    '<span class="log-time">' + log.time + '</span>' +
                    '<span class="log-level ' + log.level + '">' + log.level.toUpperCase() + '</span>' +
                    '<span class="log-msg">' + log.message + '</span></div>';
            });
            container.innerHTML = html;
        },

        initFilters: function() {
            document.querySelectorAll('.filter-chip').forEach(function(chip) {
                chip.addEventListener('click', function() {
                    document.querySelectorAll('.filter-chip').forEach(function(c) { 
                        c.classList.remove('active'); 
                    });
                    this.classList.add('active');
                    CapabilityDiscovery.filterResults(this.dataset.filter);
                });
            });
        },

        filterResults: function(filter) {
            var items = document.querySelectorAll('.result-item');
            items.forEach(function(item) {
                if (filter === 'all') {
                    item.style.display = 'flex';
                } else if (filter === 'new') {
                    var btn = item.querySelector('.nx-btn--primary');
                    item.style.display = btn ? 'flex' : 'none';
                } else if (filter === 'installed') {
                    var btn = item.querySelector('.nx-btn--primary');
                    item.style.display = btn ? 'none' : 'flex';
                }
            });
        }
    };

    CapabilityDiscovery.init();

    global.selectMethod = CapabilityDiscovery.selectMethod;
    global.startDiscovery = CapabilityDiscovery.startScan;
    global.applyConfig = function() {
        CapabilityDiscovery.hideConfig();
        CapabilityDiscovery.startScan();
    };
    global.hideConfig = CapabilityDiscovery.hideConfig;
    global.installCapability = CapabilityDiscovery.installCap;
    global.cancelInstall = CapabilityDiscovery.closeInstallModal;
    global.closeInstall = CapabilityDiscovery.closeInstallModal;
    global.toggleLogs = function() {
        document.getElementById('logsBody').classList.toggle('show');
    };

})(typeof window !== 'undefined' ? window : this);
