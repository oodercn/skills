class App {
    constructor() {
        this.store = null;
        this.api = null;
        this.tree = null;
        this.elements = null;
        this.canvas = null;
        this.chat = null;
        this.panelManager = null;
        this.theme = null;
        this.tabManager = null;
        this.panelVisible = true;
        this.panelPinned = true;
    }

    async init() {
        console.log('[App] Initializing BPM Designer...');

        this._initIcons();
        
        this.theme = ThemeFactory.get();
        this.store = new Store();
        this.api = ApiFactory.create({ baseUrl: '/api/bpm' });
        this.store.setApi(this.api);

        this._initSidebar();
        this._initCanvas();
        await this._initPanel();
        this._initChat();
        this._initToolbar();
        this._initTabManager();
        this._bindGlobalEvents();

        console.log('[App] BPM Designer initialized');
    }

    _initTabManager() {
        this.tabManager = new TabManager(this);
        
        this.store.on('dirty:change', (dirty) => {
            const activeTab = this.tabManager.getActiveTab();
            if (activeTab) {
                this.tabManager.setTabDirty(activeTab.id, dirty);
            }
        });
    }

    _initIcons() {
        const iconMap = {
            'btnTheme': 'moon',
            'btnImport': 'upload',
            'btnExport': 'download',
            'btnSave': 'save',
            'btnUndo': 'undo',
            'btnRedo': 'redo',
            'btnZoomIn': 'zoomin',
            'btnZoomOut': 'zoomout',
            'btnFit': 'grid',
            'btnAlignLeft': 'menu',
            'btnAlignCenter': 'menu',
            'btnAlignRight': 'menu',
            'btnDelete': 'delete',
            'btnNewProcess': 'plus',
            'btnPanelPin': 'shield',
            'btnPanelClose': 'close',
            'btnChatToggle': 'minus',
            'btnChatSend': 'route',
            'btnChatOpen': 'robot',
            'btnModalClose': 'close'
        };

        for (const [id, iconName] of Object.entries(iconMap)) {
            const el = document.getElementById(id);
            if (el) {
                el.innerHTML = IconManager.render(iconName, 18, `icon-${id}`);
            }
        }

        const sidebarTabs = document.querySelectorAll('.d-sidebar-tab');
        const tabIcons = ['folder', 'grid'];
        sidebarTabs.forEach((tab, i) => {
            tab.innerHTML = IconManager.render(tabIcons[i] || 'file', 20);
        });

        const chatIcon = document.querySelector('.chat-icon');
        if (chatIcon) {
            chatIcon.innerHTML = IconManager.render('robot', 18);
        }

        const welcomeIcon = document.querySelector('.welcome-icon');
        if (welcomeIcon) {
            welcomeIcon.innerHTML = IconManager.render('robot', 48);
        }
    }

    _initSidebar() {
        this.tree = new Tree(document.getElementById('processTree'), this.store);
        this.elements = new Elements(document.getElementById('elementGroups'), this.store);

        const sidebarTabs = document.querySelectorAll('.d-sidebar-tab');
        sidebarTabs.forEach(tab => {
            tab.addEventListener('click', () => {
                sidebarTabs.forEach(t => t.classList.remove('active'));
                tab.classList.add('active');
                
                const tabName = tab.dataset.tab;
                document.querySelectorAll('.d-sidebar-page').forEach(page => {
                    page.classList.toggle('active', page.id === 'page' + tabName.charAt(0).toUpperCase() + tabName.slice(1));
                });
            });
        });

        const btnNewProcess = document.getElementById('btnNewProcess');
        if (btnNewProcess) {
            btnNewProcess.addEventListener('click', () => {
                this.newProcess();
            });
        }
    }

    _initCanvas() {
        this.canvas = new Canvas(document.getElementById('canvas'), this.store, this);
    }

    async _initPanel() {
        console.log('[App] Initializing panel with new plugin system...');
        const panelEl = document.getElementById('panel');
        console.log('[App] Panel element:', panelEl);
        
        // 设置全局store引用，供PanelPlugin使用
        window.store = this.store;
        console.log('[App] window.store set');
        
        // 初始化插件环境
        if (typeof PluginEnvironment !== 'undefined') {
            window.pluginEnvironment = new PluginEnvironment({
                store: this.store,
                api: this.api
            });
            console.log('[App] PluginEnvironment initialized');
        }
        
        // 初始化面板插件管理器
        if (typeof PanelPluginManager !== 'undefined') {
            window.panelPluginManager = new PanelPluginManager({
                environment: window.pluginEnvironment
            });
            console.log('[App] PanelPluginManager initialized');
        }
        
        // 初始化面板插件
        if (window.panelInitializer) {
            await window.panelInitializer.initialize();
            console.log('[App] PanelInitializer completed');
        }
        
        // 使用插件架构面板管理器
        this.panelManager = new PanelManager(panelEl, this.store);
        console.log('[App] PanelManager initialized');

        this.store.on('activity:select', (activity) => {
            console.log('[App] activity:select event received:', activity);
            if (activity) {
                console.log('[App] Rendering activity panel with new plugin...');
                this.panelManager.render('activity', activity);
                console.log('[App] Activity panel rendered');
            }
        });

        this.store.on('route:select', (route) => {
            console.log('[App] route:select event received:', route);
            if (route) {
                const fromActivity = this.store.getActivity(route.from);
                const toActivity = this.store.getActivity(route.to);
                
                const routeData = {
                    ...route,
                    fromName: fromActivity ? fromActivity.name : route.from,
                    toName: toActivity ? toActivity.name : route.to
                };
                
                this.panelManager.render('route', routeData);
                console.log('[App] Route panel rendered');
            }
        });

        this.store.on('process:change', (process) => {
            if (process) {
                this.panelManager.render('process', process);
            }
        });
    }

    _showPanel() {
        const panel = document.getElementById('panel');
        if (panel) {
            panel.style.display = '';
            this.panelVisible = true;
        }
    }

    _hidePanel() {
        if (!this.panelPinned) {
            const panel = document.getElementById('panel');
            if (panel) {
                panel.style.display = 'none';
                this.panelVisible = false;
            }
        }
    }

    _initChat() {
        this.chat = new Chat(document.getElementById('chatPanel'), this.store, this.api);

        const btnChatOpen = document.getElementById('btnChatOpen');
        if (btnChatOpen) {
            btnChatOpen.addEventListener('click', () => {
                this.chat.toggle();
            });
        }
    }

    _initToolbar() {
        const btnUndo = document.getElementById('btnUndo');
        const btnRedo = document.getElementById('btnRedo');
        const btnZoomIn = document.getElementById('btnZoomIn');
        const btnZoomOut = document.getElementById('btnZoomOut');
        const btnFit = document.getElementById('btnFit');
        const btnSave = document.getElementById('btnSave');
        const btnImport = document.getElementById('btnImport');
        const btnExport = document.getElementById('btnExport');
        const btnDelete = document.getElementById('btnDelete');
        const btnTheme = document.getElementById('btnTheme');

        btnUndo?.addEventListener('click', () => this.store.undo());
        btnRedo?.addEventListener('click', () => this.store.redo());
        btnZoomIn?.addEventListener('click', () => this.canvas.zoomIn());
        btnZoomOut?.addEventListener('click', () => this.canvas.zoomOut());
        btnFit?.addEventListener('click', () => this.canvas.fitToScreen());
        btnSave?.addEventListener('click', () => this._save());
        btnImport?.addEventListener('click', () => this._showImportModal());
        btnExport?.addEventListener('click', () => this._showExportModal());
        btnDelete?.addEventListener('click', () => this._deleteSelected());
        btnTheme?.addEventListener('click', () => this.theme.toggle());

        this._initMenuBar();

        this.store.on('dirty:change', (dirty) => {
            if (btnSave) btnSave.disabled = !dirty;
        });

        this.store.on('process:change', (process) => {
            const nameEl = document.getElementById('processName');
            if (nameEl && process) {
                nameEl.textContent = process.name;
            }
        });
    }

    _initMenuBar() {
        const menuOptions = document.querySelectorAll('.d-menu-option');
        menuOptions.forEach(option => {
            option.addEventListener('click', () => {
                const action = option.dataset.action;
                switch (action) {
                    case 'new':
                        this.createNewProcess();
                        break;
                    case 'open':
                        this._toast('打开流程功能开发中', 'info');
                        break;
                    case 'save':
                        this._save();
                        break;
                    case 'saveAs':
                        this._toast('另存为功能开发中', 'info');
                        break;
                    case 'import':
                        this._showImportModal();
                        break;
                    case 'export':
                        this._showExportModal();
                        break;
                    case 'undo':
                        this.store.undo();
                        break;
                    case 'redo':
                        this.store.redo();
                        break;
                    case 'copy':
                        this._copySelected();
                        break;
                    case 'paste':
                        this._pasteSelected();
                        break;
                    case 'delete':
                        this._deleteSelected();
                        break;
                    case 'zoomIn':
                        this.canvas.zoomIn();
                        break;
                    case 'zoomOut':
                        this.canvas.zoomOut();
                        break;
                    case 'fit':
                        this.canvas.fitToScreen();
                        break;
                    case 'togglePanel':
                        this._togglePanel();
                        break;
                }
            });
        });
    }

    _copySelected() {
        this._toast('复制功能开发中', 'info');
    }

    _pasteSelected() {
        this._toast('粘贴功能开发中', 'info');
    }

    _bindGlobalEvents() {
        document.addEventListener('keydown', (e) => {
            if (e.ctrlKey || e.metaKey) {
                switch (e.key) {
                    case 's':
                        e.preventDefault();
                        this._save();
                        break;
                    case 'z':
                        e.preventDefault();
                        if (e.shiftKey) {
                            this.store.redo();
                        } else {
                            this.store.undo();
                        }
                        break;
                    case 'y':
                        e.preventDefault();
                        this.store.redo();
                        break;
                }
            }

            if (e.key === 'Delete' || e.key === 'Backspace') {
                if (document.activeElement.tagName !== 'INPUT' && 
                    document.activeElement.tagName !== 'TEXTAREA') {
                    e.preventDefault();
                    this._deleteSelected();
                }
            }
        });

        window.addEventListener('beforeunload', (e) => {
            if (this.store.isDirty()) {
                e.preventDefault();
                e.returnValue = '有未保存的更改，确定要离开吗？';
            }
        });

        this.store.on('activity:add', (activity) => {
            this.tree.addProcess(this.store.getProcess());
        });

        this.store.on('tree:open-process', (processData) => {
            console.log('[App] Received tree:open-process:', processData);
            this._loadProcessToCanvas(processData);
        });

        this.store.on('tree:select-activity', (activityId) => {
            console.log('[App] Select activity:', activityId);
            const activity = this.store.getActivity(activityId);
            if (activity) {
                this.store.selectActivity(activityId);
                this.canvas.selectNode(activityId);
            }
        });

        this.store.on('subprocess:open', (data) => {
            console.log('[App] Opening subprocess:', data);
            this.tabManager.openTab(data.processDef, {
                tabType: 'subprocess',
                tabId: 'sub_' + data.activityId,
                parentTabId: data.parentTabId
            });
        });

        this.store.on('subprocess:open-existing', async (data) => {
            console.log('[App] Opening existing subprocess:', data);
            try {
                const response = await this.api.getProcess(data.processDefId, data.version);
                if (response && response.data) {
                    this.tabManager.openTab(response.data, {
                        tabType: 'subprocess',
                        tabId: 'sub_' + data.activityId,
                        parentTabId: data.parentTabId
                    });
                }
            } catch (error) {
                console.error('[App] Failed to load subprocess:', error);
                this._toast('加载子流程失败: ' + error.message, 'error');
            }
        });

        this.store.on('outflow:open', async (data) => {
            console.log('[App] Opening outflow:', data);
            try {
                const response = await this.api.getProcess(data.processDefId);
                if (response && response.data) {
                    this.tabManager.openTab(response.data, {
                        tabType: 'outflow',
                        tabId: 'out_' + data.activityId,
                        parentTabId: data.parentTabId
                    });
                }
            } catch (error) {
                console.error('[App] Failed to load outflow:', error);
                this._toast('加载外部流程失败: ' + error.message, 'error');
            }
        });
    }

    _loadProcessToCanvas(processData) {
        console.log('[App] Loading process to canvas:', processData);
        this.tabManager.openTab(processData);
    }

    _loadProcessContent(processData) {
        console.log('[App] Loading process content to canvas:', processData);
        console.log('[App] processData.activities:', processData.activities);
        console.log('[App] processData.routes:', processData.routes);
        
        try {
            const process = new ProcessDef(processData);
            console.log('[App] Process created:', process);
            console.log('[App] process.activities:', process.activities);
            console.log('[App] process.routes:', process.routes);
            console.log('[App] process.activities length:', process.activities?.length);
            
            if (process.activities && process.activities.length > 0) {
                process.activities.forEach((act, i) => {
                    console.log(`[App] Activity ${i}:`, act.activityDefId, act.name, 'positionCoord:', act.positionCoord);
                });
            }
            
            console.log('[App] Calling store.setProcess...');
            this.store.setProcess(process);
            console.log('[App] Calling canvas.loadProcess...');
            this.canvas.loadProcess(process);
            console.log('[App] canvas.loadProcess completed');
            
            document.getElementById('processName').textContent = process.name;
        } catch (error) {
            console.error('[App] Error in _loadProcessContent:', error);
            this._toast('加载流程失败: ' + error.message, 'error');
        }
    }

    loadProcessToCanvas(processData) {
        console.log('[App] loadProcessToCanvas called:', processData);
        console.log('[App] processData.activities:', processData.activities);
        console.log('[App] processData.routes:', processData.routes);
        
        try {
            const process = new ProcessDef(processData);
            console.log('[App] Process created:', process);
            console.log('[App] process.activities:', process.activities);
            console.log('[App] process.routes:', process.routes);
            console.log('[App] process.activities length:', process.activities?.length);
            
            if (process.activities && process.activities.length > 0) {
                process.activities.forEach((act, i) => {
                    console.log(`[App] Activity ${i}:`, act.activityDefId, act.name, 'positionCoord:', act.positionCoord);
                });
            }
            
            console.log('[App] Calling store.setProcess...');
            this.store.setProcess(process);
            console.log('[App] Calling canvas.loadProcess...');
            this.canvas.loadProcess(process);
            console.log('[App] canvas.loadProcess completed');
            
            document.getElementById('processName').textContent = process.name;
            this._toast(`已加载流程: ${process.name}`, 'success');
        } catch (error) {
            console.error('[App] Error in loadProcessToCanvas:', error);
            this._toast('加载流程失败: ' + error.message, 'error');
        }
    }

    _deleteSelected() {
        let deleted = false;
        
        // 删除选中的节点
        if (this.canvas.selectedNodes && this.canvas.selectedNodes.size > 0) {
            const selectedIds = Array.from(this.canvas.selectedNodes);
            selectedIds.forEach(id => {
                this.store.removeActivity(id);
                this.canvas.removeNode(id);
            });
            this.canvas.selectedNodes.clear();
            deleted = true;
        }
        
        // 删除选中的路由
        if (this.canvas.selectedEdges && this.canvas.selectedEdges.size > 0) {
            const selectedEdgeIds = Array.from(this.canvas.selectedEdges);
            selectedEdgeIds.forEach(id => {
                this.store.removeRoute(id);
                this.canvas.removeEdge(id);
            });
            this.canvas.selectedEdges.clear();
            deleted = true;
        }
        
        // 如果没有选中任何元素，但当前有活动被选中，删除当前活动
        if (!deleted && this.store.currentActivity) {
            const activity = this.store.currentActivity;
            this.store.removeActivity(activity.activityDefId);
            this.canvas.removeNode(activity.activityDefId);
            deleted = true;
        }
        
        // 如果没有选中任何元素，但当前有路由被选中，删除当前路由
        if (!deleted && this.store.currentRoute) {
            const route = this.store.currentRoute;
            this.store.removeRoute(route.routeDefId);
            this.canvas.removeEdge(route.routeDefId);
            deleted = true;
        }
        
        if (deleted) {
            document.getElementById('panelContent').innerHTML = `
                <div class="d-empty">
                    <p>请选择元素查看属性</p>
                </div>
            `;
            this._toast('删除成功', 'success');
        }
    }

    async _save() {
        const process = this.store.getProcess();
        if (!process) {
            this._toast('没有流程可保存', 'warning');
            return;
        }

        try {
            const processData = process.toJSON();
            console.log('[App] Saving process data:', JSON.stringify(processData, null, 2));
            console.log('[App] Activities positionCoord:', processData.activities?.map(a => ({ id: a.activityDefId, coord: a.positionCoord })));
            const response = await this.api.saveProcess(processData);
            
            // 保存成功后，使用返回的数据更新 store
            if (response && response.data) {
                console.log('[App] Save response:', response);
                const savedProcess = new ProcessDef(response.data);
                this.store.setProcess(savedProcess);
                
                // 更新当前标签页的 processDef
                const activeTab = this.tabManager.getActiveTab();
                if (activeTab) {
                    activeTab.processDef = savedProcess;
                }
            }
            
            this.store.setDirty(false);
            this._toast('保存成功', 'success');
        } catch (error) {
            console.error('[App] Save failed:', error);
            this._toast('保存失败: ' + error.message, 'error');
        }
    }

    _showImportModal() {
        const modal = document.getElementById('modalYaml');
        const textarea = document.getElementById('yamlContent');
        const confirmBtn = document.getElementById('btnYamlConfirm');
        const cancelBtn = document.getElementById('btnYamlCancel');
        const closeBtn = document.getElementById('btnModalClose');

        if (!modal || !textarea) return;

        textarea.value = '';
        modal.classList.add('show');

        const closeModal = () => {
            modal.classList.remove('show');
        };

        const handleConfirm = () => {
            const yaml = textarea.value.trim();
            if (yaml) {
                try {
                    const process = ProcessDef.fromYaml(yaml);
                    this.store.setProcess(process);
                    this.canvas.loadProcess(process);
                    this._toast('导入成功', 'success');
                } catch (error) {
                    this._toast('导入失败: ' + error.message, 'error');
                }
            }
            closeModal();
        };

        confirmBtn?.addEventListener('click', handleConfirm, { once: true });
        cancelBtn?.addEventListener('click', closeModal, { once: true });
        closeBtn?.addEventListener('click', closeModal, { once: true });
    }

    _showExportModal() {
        const process = this.store.getProcess();
        if (!process) {
            this._toast('没有流程可导出', 'warning');
            return;
        }

        const modal = document.getElementById('modalYaml');
        const textarea = document.getElementById('yamlContent');
        const confirmBtn = document.getElementById('btnYamlConfirm');
        const cancelBtn = document.getElementById('btnYamlCancel');
        const closeBtn = document.getElementById('btnModalClose');

        if (!modal || !textarea) return;

        textarea.value = process.toYAML();
        modal.classList.add('show');

        confirmBtn.textContent = '复制';
        confirmBtn.onclick = () => {
            textarea.select();
            document.execCommand('copy');
            this._toast('已复制到剪贴板', 'success');
        };

        const closeModal = () => {
            modal.classList.remove('show');
            confirmBtn.textContent = '确认';
            confirmBtn.onclick = null;
        };

        cancelBtn?.addEventListener('click', closeModal, { once: true });
        closeBtn?.addEventListener('click', closeModal, { once: true });
    }

    _toast(message, type) {
        const toast = document.getElementById('toast');
        if (!toast) return;

        toast.textContent = message;
        toast.className = 'd-toast show ' + type;

        setTimeout(() => {
            toast.classList.remove('show');
        }, 3000);
    }

    loadProcess(processId, version) {
        this.api.getProcess(processId, version).then(response => {
            // 后端返回 { code, message, data } 格式，需要提取 data
            const processData = response.data || response;
            const process = new ProcessDef(processData);
            this.tabManager.openTab(process);
        }).catch(error => {
            console.error('[App] Load process failed:', error);
        });
    }

    newProcess() {
        this.createNewProcess();
    }

    createNewProcess() {
        const process = new ProcessDef({
            processDefId: 'new_' + Date.now(),
            name: '新流程',
            description: ''
        });
        this.tabManager.openTab(process);
        this.tree.refresh();
    }
}

window.App = App;
