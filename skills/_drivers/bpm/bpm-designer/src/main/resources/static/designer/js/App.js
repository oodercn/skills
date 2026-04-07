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

    init() {
        console.log('[App] Initializing BPM Designer...');

        this._initIcons();
        
        this.theme = ThemeFactory.get();
        this.store = new Store();
        this.api = ApiFactory.create({ baseUrl: '/api/bpm' });

        this._initSidebar();
        this._initCanvas();
        this._initPanel();
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
        this.canvas = new Canvas(document.getElementById('canvas'), this.store);
    }

    _initPanel() {
        this.panelManager = new PanelManager(document.getElementById('panel'), this.store);

        this.store.on('activity:select', (activity) => {
            if (activity) {
                this.panelManager.render('activity', activity);
                document.getElementById('panelTitle').textContent = activity.name || '活动属性';
                this._showPanel();
            }
        });

        this.store.on('route:select', (route) => {
            if (route) {
                const fromActivity = this.store.getActivity(route.from);
                const toActivity = this.store.getActivity(route.to);
                
                const routeData = {
                    ...route,
                    fromName: fromActivity ? fromActivity.name : route.from,
                    toName: toActivity ? toActivity.name : route.to
                };
                
                this.panelManager.render('route', routeData);
                document.getElementById('panelTitle').textContent = '路由属性';
                this._showPanel();
            }
        });

        this.store.on('process:change', (process) => {
            if (process) {
                this.panelManager.render('process', process);
                document.getElementById('panelTitle').textContent = process.name || '流程属性';
            }
        });

        const btnPanelClose = document.getElementById('btnPanelClose');
        if (btnPanelClose) {
            btnPanelClose.addEventListener('click', () => {
                this._hidePanel();
            });
        }

        const btnPanelPin = document.getElementById('btnPanelPin');
        if (btnPanelPin) {
            btnPanelPin.addEventListener('click', () => {
                this.panelPinned = !this.panelPinned;
                btnPanelPin.classList.toggle('pinned', this.panelPinned);
                btnPanelPin.innerHTML = IconManager.render(this.panelPinned ? 'shield' : 'shield', 18, 'icon-pin');
            });
        }
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
    }

    _loadProcessToCanvas(processData) {
        console.log('[App] Loading process to canvas:', processData);
        this.tabManager.openTab(processData);
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
        if (this.canvas.selectedNodes.size > 0) {
            const selectedIds = Array.from(this.canvas.selectedNodes);
            selectedIds.forEach(id => {
                this.store.removeActivity(id);
                this.canvas.removeNode(id);
            });
            this.canvas.selectedNodes.clear();
            document.getElementById('panelContent').innerHTML = `
                <div class="d-empty">
                    <p>请选择元素查看属性</p>
                </div>
            `;
        } else {
            const activity = this.store.currentActivity;
            if (activity) {
                this.store.removeActivity(activity.activityDefId);
                this.canvas.removeNode(activity.activityDefId);
                document.getElementById('panelContent').innerHTML = `
                    <div class="d-empty">
                        <p>请选择元素查看属性</p>
                    </div>
                `;
            }
        }
    }

    async _save() {
        const process = this.store.getProcess();
        if (!process) {
            this._toast('没有流程可保存', 'warning');
            return;
        }

        try {
            await this.api.saveProcess(process.toJSON());
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
        this.api.getProcess(processId, version).then(data => {
            const process = new ProcessDef(data);
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
