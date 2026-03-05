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
                { name: 'repoUrl', label: '仓库地址', type: 'text', default: 'https://github.com/oodercn/skills' },
                { name: 'branch', label: '分支', type: 'text', default: 'master' },
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
                { name: 'repoUrl', label: '仓库地址', type: 'text', default: 'https://gitee.com/ooderCN/skills' },
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
            ApiClient.get('/api/v1/capabilities')
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
                CapabilityDiscovery.discoverFromGitService('/api/v1/discovery/github', method, false);
            } else if (currentMethod === 'GITEE') {
                CapabilityDiscovery.discoverFromGitService('/api/v1/discovery/gitee', method, false);
            } else if (currentMethod === 'GIT_REPOSITORY') {
                CapabilityDiscovery.discoverFromGitService('/api/v1/discovery/git', method, false);
            } else if (currentMethod === 'LOCAL_FS') {
                CapabilityDiscovery.discoverFromLocalFs(false);
            } else {
                var url = '/api/v1/capabilities/discovery?method=' + currentMethod;
                CapabilityDiscovery.discoverFromLocal(url);
            }
        },

        forceRefresh: function() {
            if (!currentMethod) {
                alert('请先选择发现方式');
                return;
            }

            var method = DISCOVERY_METHODS.find(function(m) { return m.id === currentMethod; });
            
            discoveredCapabilities = [];
            scanStats = { scanned: 0, found: 0, new: 0, installed: 0 };
            isScanning = true;
            CapabilityDiscovery.updateStats();
            CapabilityDiscovery.addLog('info', '强制刷新: ' + currentMethod);

            if (currentMethod === 'GITHUB') {
                CapabilityDiscovery.discoverFromGitService('/api/v1/discovery/github', method, true);
            } else if (currentMethod === 'GITEE') {
                CapabilityDiscovery.discoverFromGitService('/api/v1/discovery/gitee', method, true);
            } else if (currentMethod === 'GIT_REPOSITORY') {
                CapabilityDiscovery.discoverFromGitService('/api/v1/discovery/git', method, true);
            } else if (currentMethod === 'LOCAL_FS') {
                CapabilityDiscovery.discoverFromLocalFs(true);
            } else {
                var url = '/api/v1/capabilities/discovery?method=' + currentMethod;
                CapabilityDiscovery.discoverFromLocal(url);
            }
        },

        discoverFromGitService: function(apiUrl, method, forceRefresh) {
            var config = {};
            if (method && method.configFields) {
                method.configFields.forEach(function(field) {
                    var el = document.getElementById('config_' + field.name);
                    if (el) {
                        config[field.name] = el.value;
                        console.log('[Discovery] config.' + field.name + ' =', el.value);
                    }
                });
            }

            var cacheKey = 'discovery_cache_' + currentMethod + '_' + (config.repoUrl || '').replace(/[^a-zA-Z0-9]/g, '_');
            
            console.log('[Discovery] forceRefresh=', forceRefresh, 'cacheKey=', cacheKey);
            
            if (!forceRefresh) {
                var cachedData = CapabilityDiscovery.getCache(cacheKey);
                if (cachedData) {
                    console.log('[Discovery] Using cached data, skip backend call');
                    CapabilityDiscovery.addLog('info', '使用缓存数据（' + cachedData.capabilities.length + ' 个能力）');
                    CapabilityDiscovery.processDiscoveryResult(cachedData);
                    CapabilityDiscovery.finishScan();
                    return;
                }
            } else {
                CapabilityDiscovery.clearCache(cacheKey);
                CapabilityDiscovery.addLog('info', '强制刷新，清除缓存');
            }

            console.log('[Discovery] Sending request to', apiUrl, 'with config:', JSON.stringify(config));

            ApiClient.post(apiUrl, config, { timeout: 120000 })
                .then(function(result) {
                    console.log('[Discovery] Response received:', result);
                    if (result.code === 200 && result.data) {
                        CapabilityDiscovery.setCache(cacheKey, result.data);
                        CapabilityDiscovery.processDiscoveryResult(result.data);
                    } else {
                        CapabilityDiscovery.addLog('error', '扫描失败: ' + (result.message || '未知错误'));
                        CapabilityDiscovery.showEmptyResult();
                    }
                })
                .catch(function(error) {
                    console.error('Discovery error:', error);
                    CapabilityDiscovery.addLog('error', '扫描失败: ' + error.message);
                    CapabilityDiscovery.showEmptyResult();
                })
                .finally(function() {
                    CapabilityDiscovery.finishScan();
                });
        },

        processDiscoveryResult: function(data) {
            var caps = data.capabilities || [];
            
            caps.forEach(function(cap) {
                var exists = installedCapabilities.find(function(i) { 
                    return i.capabilityId === cap.id; 
                });
                if (!exists) {
                    var isScene = cap.isSceneCapability === true || cap.sceneCapability === true || cap.type === 'SCENE';
                    discoveredCapabilities.push({
                        capabilityId: cap.id,
                        id: cap.id,
                        name: cap.name,
                        type: cap.type,
                        description: cap.description,
                        version: cap.version,
                        source: currentMethod,
                        isSceneCapability: isScene,
                        sceneCapability: isScene,
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
        },

        getCache: function(key) {
            try {
                var cached = localStorage.getItem(key);
                if (cached) {
                    var data = JSON.parse(cached);
                    var now = Date.now();
                    if (data.expireTime > now) {
                        console.log('[Cache] Hit:', key);
                        return data.value;
                    } else {
                        localStorage.removeItem(key);
                        console.log('[Cache] Expired:', key);
                    }
                }
            } catch (e) {
                console.warn('[Cache] Read error:', e);
            }
            return null;
        },

        setCache: function(key, value) {
            try {
                var cacheExpireHours = 24;
                var data = {
                    value: value,
                    expireTime: Date.now() + (cacheExpireHours * 60 * 60 * 1000),
                    cachedAt: new Date().toISOString()
                };
                localStorage.setItem(key, JSON.stringify(data));
                console.log('[Cache] Set:', key, 'expire in', cacheExpireHours, 'hours');
            } catch (e) {
                console.warn('[Cache] Write error:', e);
            }
        },

        clearCache: function(key) {
            try {
                if (key) {
                    localStorage.removeItem(key);
                    console.log('[Cache] Cleared:', key);
                } else {
                    var keysToRemove = [];
                    for (var i = 0; i < localStorage.length; i++) {
                        var k = localStorage.key(i);
                        if (k && k.startsWith('discovery_cache_')) {
                            keysToRemove.push(k);
                        }
                    }
                    keysToRemove.forEach(function(k) {
                        localStorage.removeItem(k);
                    });
                    console.log('[Cache] Cleared all discovery caches:', keysToRemove.length);
                }
            } catch (e) {
                console.warn('[Cache] Clear error:', e);
            }
        },

        clearAllCaches: function() {
            CapabilityDiscovery.clearCache();
            CapabilityDiscovery.addLog('info', '已清除所有缓存');
        },

        discoverFromLocal: function(url) {

            ApiClient.get(url)
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
                        CapabilityDiscovery.showEmptyResult();
                    }
                })
                .catch(function(error) {
                    console.error('Scan error:', error);
                    CapabilityDiscovery.addLog('error', '扫描失败: ' + error.message);
                    CapabilityDiscovery.showEmptyResult();
                })
                .finally(function() {
                    CapabilityDiscovery.finishScan();
                });
        },

        discoverFromLocalFs: function(forceRefresh) {
            var cacheKey = 'discovery_cache_LOCAL_FS';
            
            console.log('[Discovery LOCAL_FS] forceRefresh=', forceRefresh);
            
            if (!forceRefresh) {
                var cachedData = CapabilityDiscovery.getCache(cacheKey);
                if (cachedData) {
                    console.log('[Discovery LOCAL_FS] Using cached data');
                    CapabilityDiscovery.addLog('info', '使用缓存数据（' + cachedData.capabilities.length + ' 个能力）');
                    CapabilityDiscovery.processDiscoveryResult(cachedData);
                    CapabilityDiscovery.finishScan();
                    return;
                }
            } else {
                CapabilityDiscovery.clearCache(cacheKey);
                CapabilityDiscovery.addLog('info', '强制刷新，清除缓存');
            }

            var config = {};
            var skillsPathInput = document.getElementById('localSkillsPath');
            if (skillsPathInput && skillsPathInput.value) {
                config.skillsPath = skillsPathInput.value;
            }

            ApiClient.post('/api/v1/discovery/local', config, { timeout: 60000 })
                .then(function(result) {
                    console.log('[Discovery LOCAL_FS] Response:', result);
                    
                    if (result.code === 200 && result.data) {
                        var data = result.data;
                        if (data.capabilities && data.capabilities.length > 0) {
                            CapabilityDiscovery.setCache(cacheKey, data);
                            CapabilityDiscovery.processDiscoveryResult(data);
                            CapabilityDiscovery.addLog('success', '本地发现 ' + data.capabilities.length + ' 个已安装能力');
                        } else {
                            CapabilityDiscovery.addLog('warn', '本地未发现已安装的能力');
                            CapabilityDiscovery.showEmptyResult();
                        }
                    } else {
                        CapabilityDiscovery.addLog('error', '扫描失败: ' + (result.message || '未知错误'));
                        CapabilityDiscovery.showEmptyResult();
                    }
                })
                .catch(function(error) {
                    console.error('LOCAL_FS scan error:', error);
                    CapabilityDiscovery.addLog('error', '本地扫描失败: ' + error.message);
                    CapabilityDiscovery.showEmptyResult();
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

        showEmptyResult: function() {
            discoveredCapabilities = [];
            scanStats.found = 0;
            scanStats.scanned = 100;
            CapabilityDiscovery.renderResults();
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
                var isSceneCapability = cap.isSceneCapability === true || cap.sceneCapability === true || cap.type === 'SCENE';
                var capabilityTypeLabel = isSceneCapability ? 
                    '<span class="capability-type-badge scene"><i class="ri-layout-grid-line"></i> 场景能力</span>' : 
                    '<span class="capability-type-badge collaboration"><i class="ri-team-line"></i> 协作能力</span>';

                html += '<div class="result-item" data-scene="' + isSceneCapability + '">' +
                    '<div class="result-icon type-' + (cap.type || 'CUSTOM') + '">' +
                    '<i class="' + typeIcon + '"></i></div>' +
                    '<div class="result-info">' +
                    '<div class="result-name">' + (cap.name || cap.id) + capabilityTypeLabel + '</div>' +
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

        currentInstallCap: null,
        currentWizardStep: 1,
        selectedDriverCondition: null,
        collaborators: [],
        installId: null,

        installCap: async function(capId) {
            var cap = discoveredCapabilities.find(function(c) { return c.id === capId; });
            if (!cap) return;

            CapabilityDiscovery.currentInstallCap = cap;
            CapabilityDiscovery.currentWizardStep = 1;
            CapabilityDiscovery.selectedDriverCondition = null;
            CapabilityDiscovery.collaborators = [];
            CapabilityDiscovery.installId = null;

            document.getElementById('installModalTitle').textContent = '安装能力: ' + (cap.name || cap.id);
            
            CapabilityDiscovery.loadDriverConditions(cap.id);
            
            document.getElementById('leaderInput').value = '';
            document.getElementById('collaboratorList').innerHTML = '';
            document.getElementById('collaboratorInput').value = '';
            document.getElementById('pushType').value = 'SHARE';
            
            CapabilityDiscovery.showWizardStep(1);
            document.getElementById('installModal').classList.add('show');
        },

        loadDriverConditions: function(capabilityId) {
            var container = document.getElementById('driverConditionList');
            
            ApiClient.get('/api/v1/capabilities/detail/' + capabilityId + '/driver-conditions')
                .then(function(result) {
                    if (result.code === 200 && result.data && result.data.length > 0) {
                        var html = '';
                        result.data.forEach(function(dc, i) {
                            html += '<div class="driver-condition-item' + (i === 0 ? ' selected' : '') + '" data-id="' + dc.conditionId + '" onclick="selectDriverCondition(\'' + dc.conditionId + '\')">' +
                                '<div class="driver-condition-radio"></div>' +
                                '<div class="driver-condition-info">' +
                                '<div class="driver-condition-name">' + dc.name + '</div>' +
                                '<div class="driver-condition-desc">' + (dc.description || '默认驱动条件') + '</div>' +
                                '</div></div>';
                        });
                        container.innerHTML = html;
                        if (result.data.length > 0) {
                            CapabilityDiscovery.selectedDriverCondition = result.data[0].conditionId;
                        }
                    } else {
                        container.innerHTML = '<div class="driver-condition-item selected" data-id="default" onclick="selectDriverCondition(\'default\')">' +
                            '<div class="driver-condition-radio"></div>' +
                            '<div class="driver-condition-info">' +
                            '<div class="driver-condition-name">默认条件</div>' +
                            '<div class="driver-condition-desc">使用默认驱动条件</div>' +
                            '</div></div>';
                        CapabilityDiscovery.selectedDriverCondition = 'default';
                    }
                })
                .catch(function(error) {
                    container.innerHTML = '<div class="driver-condition-item selected" data-id="default" onclick="selectDriverCondition(\'default\')">' +
                        '<div class="driver-condition-radio"></div>' +
                        '<div class="driver-condition-info">' +
                        '<div class="driver-condition-name">默认条件</div>' +
                        '<div class="driver-condition-desc">使用默认驱动条件</div>' +
                        '</div></div>';
                    CapabilityDiscovery.selectedDriverCondition = 'default';
                });
        },

        selectDriverCondition: function(conditionId) {
            CapabilityDiscovery.selectedDriverCondition = conditionId;
            document.querySelectorAll('.driver-condition-item').forEach(function(item) {
                item.classList.toggle('selected', item.dataset.id === conditionId);
            });
        },

        cachedUsers: null,
        
        loadUsers: function(callback) {
            if (CapabilityDiscovery.cachedUsers) {
                callback(CapabilityDiscovery.cachedUsers);
                return;
            }
            
            ApiClient.get('/api/v1/org/users')
                .then(function(result) {
                    if (result.code === 200 && result.data) {
                        CapabilityDiscovery.cachedUsers = result.data;
                        callback(result.data);
                    } else {
                        callback([]);
                    }
                })
                .catch(function(error) {
                    console.error('[loadUsers] Error:', error);
                    callback([]);
                });
        },

        selectLeader: function() {
            var leaderInput = document.getElementById('leaderInput');
            
            CapabilityDiscovery.loadUsers(function(users) {
                var existingSelector = document.getElementById('leaderSelector');
                if (existingSelector) {
                    existingSelector.remove();
                }
                
                var selector = document.createElement('div');
                selector.id = 'leaderSelector';
                selector.style.cssText = 'position: absolute; background: var(--nx-bg-elevated); border: 1px solid var(--nx-border); border-radius: 6px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); z-index: 1000; max-height: 200px; overflow-y: auto; min-width: 240px;';
                
                var rect = leaderInput.getBoundingClientRect();
                selector.style.top = (rect.bottom + window.scrollY + 4) + 'px';
                selector.style.left = (rect.left + window.scrollX) + 'px';
                
                if (users.length === 0) {
                    selector.innerHTML = '<div style="padding: 12px; color: var(--nx-text-secondary); text-align: center;">暂无用户数据</div>';
                } else {
                    var html = '';
                    users.forEach(function(user) {
                        html += '<div class="leader-option" data-id="' + user.userId + '" style="padding: 8px 12px; cursor: pointer; display: flex; align-items: center; gap: 8px;">' +
                            '<i class="ri-user-line" style="color: var(--nx-text-secondary);"></i>' +
                            '<span>' + (user.name || user.userId) + '</span>' +
                            '<span style="color: var(--nx-text-secondary); font-size: 12px; margin-left: auto;">' + user.userId + '</span>' +
                            '</div>';
                    });
                    selector.innerHTML = html;
                    
                    selector.querySelectorAll('.leader-option').forEach(function(option) {
                        option.addEventListener('click', function() {
                            var userId = this.dataset.id;
                            leaderInput.value = userId;
                            CapabilityDiscovery.addLog('info', '已选择主导者: ' + userId);
                            selector.remove();
                        });
                        option.addEventListener('mouseenter', function() {
                            this.style.background = 'var(--nx-primary-light)';
                        });
                        option.addEventListener('mouseleave', function() {
                            this.style.background = 'transparent';
                        });
                    });
                }
                
                document.body.appendChild(selector);
                
                var closeSelector = function(e) {
                    if (!selector.contains(e.target) && e.target !== leaderInput) {
                        selector.remove();
                        document.removeEventListener('click', closeSelector);
                    }
                };
                
                setTimeout(function() {
                    document.addEventListener('click', closeSelector);
                }, 10);
            });
        },

        showCollaboratorSelector: function() {
            var input = document.getElementById('collaboratorInput');
            
            CapabilityDiscovery.loadUsers(function(users) {
                var filteredUsers = users.filter(function(u) {
                    return CapabilityDiscovery.collaborators.indexOf(u.userId) < 0;
                });
                
                var existingSelector = document.getElementById('collaboratorSelector');
                if (existingSelector) {
                    existingSelector.remove();
                }
                
                var selector = document.createElement('div');
                selector.id = 'collaboratorSelector';
                selector.style.cssText = 'position: absolute; background: var(--nx-bg-elevated); border: 1px solid var(--nx-border); border-radius: 6px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); z-index: 1000; max-height: 200px; overflow-y: auto; min-width: 240px;';
                
                var rect = input.getBoundingClientRect();
                selector.style.top = (rect.bottom + window.scrollY + 4) + 'px';
                selector.style.left = (rect.left + window.scrollX) + 'px';
                
                if (filteredUsers.length === 0) {
                    selector.innerHTML = '<div style="padding: 12px; color: var(--nx-text-secondary); text-align: center;">暂无可选用户</div>';
                } else {
                    var html = '';
                    filteredUsers.forEach(function(user) {
                        html += '<div class="collaborator-option" data-id="' + user.userId + '" style="padding: 8px 12px; cursor: pointer; display: flex; align-items: center; gap: 8px;">' +
                            '<i class="ri-user-line" style="color: var(--nx-text-secondary);"></i>' +
                            '<span>' + (user.name || user.userId) + '</span>' +
                            '<span style="color: var(--nx-text-secondary); font-size: 12px; margin-left: auto;">' + user.userId + '</span>' +
                            '</div>';
                    });
                    selector.innerHTML = html;
                    
                    selector.querySelectorAll('.collaborator-option').forEach(function(option) {
                        option.addEventListener('click', function() {
                            var userId = this.dataset.id;
                            CapabilityDiscovery.collaborators.push(userId);
                            CapabilityDiscovery.renderCollaborators();
                            CapabilityDiscovery.addLog('info', '已添加协作者: ' + userId);
                            selector.remove();
                        });
                        option.addEventListener('mouseenter', function() {
                            this.style.background = 'var(--nx-primary-light)';
                        });
                        option.addEventListener('mouseleave', function() {
                            this.style.background = 'transparent';
                        });
                    });
                }
                
                document.body.appendChild(selector);
                
                var closeSelector = function(e) {
                    if (!selector.contains(e.target) && e.target !== input) {
                        selector.remove();
                        document.removeEventListener('click', closeSelector);
                    }
                };
                
                setTimeout(function() {
                    document.addEventListener('click', closeSelector);
                }, 10);
            });
        },

        addCollaborator: function() {
            CapabilityDiscovery.showCollaboratorSelector();
        },

        removeCollaborator: function(userId) {
            console.log('[removeCollaborator] userId:', userId, 'before:', CapabilityDiscovery.collaborators);
            CapabilityDiscovery.collaborators = CapabilityDiscovery.collaborators.filter(function(c) { return c !== userId; });
            console.log('[removeCollaborator] after:', CapabilityDiscovery.collaborators);
            CapabilityDiscovery.renderCollaborators();
        },

        renderCollaborators: function() {
            var container = document.getElementById('collaboratorList');
            if (!container) {
                console.error('[renderCollaborators] collaboratorList element not found');
                return;
            }
            
            console.log('[renderCollaborators] Rendering', CapabilityDiscovery.collaborators.length, 'collaborators');
            
            var html = '';
            CapabilityDiscovery.collaborators.forEach(function(userId) {
                html += '<span class="participant-tag">' + userId +
                    '<i class="ri-close-line remove-btn" onclick="removeCollaborator(\'' + userId + '\')"></i></span>';
            });
            container.innerHTML = html;
            console.log('[renderCollaborators] HTML set:', html);
        },

        showWizardStep: function(step) {
            CapabilityDiscovery.currentWizardStep = step;
            
            document.querySelectorAll('.wizard-step').forEach(function(s, i) {
                s.classList.toggle('active', i + 1 === step);
            });
            
            document.getElementById('installPrev').style.display = step > 1 ? 'inline-flex' : 'none';
            document.getElementById('installNext').style.display = step < 3 ? 'inline-flex' : 'none';
            document.getElementById('installCancel').style.display = 'inline-flex';
            document.getElementById('installDone').style.display = 'none';
            
            if (step === 2) {
                CapabilityDiscovery.loadDependencies();
            }
        },

        loadDependencies: function() {
            var container = document.getElementById('dependencyList');
            var cap = CapabilityDiscovery.currentInstallCap;
            
            if (!cap || !cap.dependencies || cap.dependencies.length === 0) {
                container.innerHTML = '<div style="text-align: center; padding: 20px; color: var(--nx-text-secondary);">' +
                    '<i class="ri-checkbox-circle-line" style="font-size: 24px; display: block; margin-bottom: 8px;"></i>' +
                    '无依赖项，可直接安装</div>';
                return;
            }
            
            var html = '';
            cap.dependencies.forEach(function(dep) {
                var isInstalled = installedCapabilities.find(function(i) { return i.capabilityId === dep.id; });
                html += '<div class="dependency-item">' +
                    '<div class="dependency-icon"><i class="ri-puzzle-line"></i></div>' +
                    '<div class="dependency-info">' +
                    '<div class="dependency-name">' + (dep.name || dep.id) + '</div>' +
                    '<div class="dependency-version">v' + (dep.version || '1.0.0') + '</div>' +
                    '</div>' +
                    '<span class="dependency-status ' + (isInstalled ? 'installed' : 'pending') + '">' +
                    (isInstalled ? '已安装' : '待安装') + '</span></div>';
            });
            container.innerHTML = html;
        },

        nextStep: function() {
            var step = CapabilityDiscovery.currentWizardStep;
            
            if (step === 1) {
                var leader = document.getElementById('leaderInput').value.trim();
                if (!leader) {
                    alert('请指定主导者');
                    return;
                }
                CapabilityDiscovery.showWizardStep(2);
            } else if (step === 2) {
                CapabilityDiscovery.executeInstall();
            }
        },

        prevStep: function() {
            var step = CapabilityDiscovery.currentWizardStep;
            if (step > 1) {
                CapabilityDiscovery.showWizardStep(step - 1);
            }
        },

        executeInstall: async function() {
            CapabilityDiscovery.showWizardStep(3);
            
            var cap = CapabilityDiscovery.currentInstallCap;
            var leader = document.getElementById('leaderInput').value.trim();
            var pushType = document.getElementById('pushType').value;
            
            var steps = document.getElementById('installSteps');
            var progress = document.getElementById('installProgress');

            var installSteps = [
                { name: '创建安装配置', status: 'pending' },
                { name: '下载能力包', status: 'pending' },
                { name: '解析依赖', status: 'pending' },
                { name: '安装能力', status: 'pending' },
                { name: '推送通知', status: 'pending' }
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

            var currentStep = 0;
            var updateStep = function(stepIdx, status, statusText) {
                var stepEl = document.getElementById('step-' + stepIdx);
                if (!stepEl) return;
                var icon = stepEl.querySelector('.step-icon');
                var statusEl = stepEl.querySelector('.step-status');
                
                if (status === 'running') {
                    icon.className = 'step-icon running';
                    icon.innerHTML = '<i class="ri-loader-4-line" style="animation: spin 1s linear infinite;"></i>';
                } else if (status === 'done') {
                    icon.className = 'step-icon done';
                    icon.innerHTML = '<i class="ri-check-line"></i>';
                } else if (status === 'error') {
                    icon.className = 'step-icon error';
                    icon.innerHTML = '<i class="ri-error-warning-line"></i>';
                }
                statusEl.textContent = statusText;
                progress.style.width = ((stepIdx + 1) / installSteps.length * 100) + '%';
            };

            try {
                updateStep(0, 'running', '创建中...');
                
                var participantsData = {
                    leader: leader ? { userId: leader, role: 'LEADER' } : null,
                    collaborators: CapabilityDiscovery.collaborators.map(function(userId) {
                        return { userId: userId, role: 'COLLABORATOR' };
                    })
                };
                
                var createResult = await ApiClient.post('/api/v1/installs', {
                    capabilityId: cap.id,
                    driverCondition: CapabilityDiscovery.selectedDriverCondition,
                    participants: participantsData,
                    pushType: pushType,
                    name: cap.name,
                    type: cap.type,
                    description: cap.description,
                    source: currentMethod
                });

                if (createResult.code !== 200) {
                    throw new Error(createResult.message || '创建安装配置失败');
                }
                
                CapabilityDiscovery.installId = createResult.data.installId;
                updateStep(0, 'done', '完成');
                
                updateStep(1, 'running', '下载中...');
                await new Promise(function(r) { setTimeout(r, 500); });
                updateStep(1, 'done', '完成');
                
                updateStep(2, 'running', '解析中...');
                await new Promise(function(r) { setTimeout(r, 500); });
                updateStep(2, 'done', '完成');
                
                updateStep(3, 'running', '安装中...');
                
                var execResult = await ApiClient.post('/api/v1/installs/' + CapabilityDiscovery.installId + '/execute');
                
                if (execResult.code !== 200) {
                    throw new Error(execResult.message || '安装失败');
                }
                
                updateStep(3, 'done', '完成');
                
                updateStep(4, 'running', '推送中...');
                
                if (CapabilityDiscovery.collaborators.length > 0 || pushType === 'DELEGATE') {
                    var pushResult = await ApiClient.post('/api/v1/installs/' + CapabilityDiscovery.installId + '/push', {
                        pushType: pushType,
                        targetUsers: CapabilityDiscovery.collaborators
                    });
                }
                
                updateStep(4, 'done', '完成');
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
                
                CapabilityDiscovery.addLog('success', '安装成功: ' + (cap.name || cap.id));
                
            } catch (error) {
                console.error('Install failed:', error);
                updateStep(currentStep, 'error', '失败');
                CapabilityDiscovery.addLog('error', '安装失败: ' + error.message);
            }

            document.getElementById('installCancel').style.display = 'none';
            document.getElementById('installPrev').style.display = 'none';
            document.getElementById('installNext').style.display = 'none';
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
                } else if (filter === 'scene') {
                    item.style.display = item.dataset.scene === 'true' ? 'flex' : 'none';
                } else if (filter === 'collaboration') {
                    item.style.display = item.dataset.scene === 'false' ? 'flex' : 'none';
                }
            });
        }
    };

    CapabilityDiscovery.init();

    global.selectMethod = CapabilityDiscovery.selectMethod;
    global.startDiscovery = CapabilityDiscovery.startScan;
    global.forceRefresh = CapabilityDiscovery.forceRefresh;
    global.clearAllCaches = CapabilityDiscovery.clearAllCaches;
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
    global.selectDriverCondition = CapabilityDiscovery.selectDriverCondition;
    global.selectLeader = CapabilityDiscovery.selectLeader;
    global.addCollaborator = CapabilityDiscovery.addCollaborator;
    global.removeCollaborator = CapabilityDiscovery.removeCollaborator;
    global.nextStep = CapabilityDiscovery.nextStep;
    global.prevStep = CapabilityDiscovery.prevStep;

})(typeof window !== 'undefined' ? window : this);
