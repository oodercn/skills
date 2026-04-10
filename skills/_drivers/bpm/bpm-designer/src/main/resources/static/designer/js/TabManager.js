class TabManager {
    constructor(app) {
        this.app = app;
        this.tabs = new Map();
        this.activeTabId = null;
        this.container = document.getElementById('tabsList');
        this._bindEvents();
    }

    _bindEvents() {
        document.getElementById('btnNewTab').addEventListener('click', () => {
            this.app.createNewProcess();
        });
    }

    openTab(processDef, options = {}) {
        const tabId = options.tabId || processDef.processDefId;
        const tabType = options.tabType || 'main';
        const parentTabId = options.parentTabId || null;
        
        if (this.tabs.has(tabId)) {
            this.activateTab(tabId);
            return;
        }

        const tab = {
            id: tabId,
            name: processDef.name,
            processDef: processDef,
            isDirty: false,
            tabType: tabType,
            parentTabId: parentTabId,
            subFlowConfig: options.subFlowConfig || null,
            activitySetConfig: options.activitySetConfig || null
        };

        this.tabs.set(tabId, tab);
        this._renderTab(tab);
        this.activateTab(tabId);
        this._saveLastProcess(tabId);
    }

    _renderTab(tab) {
        const tabEl = document.createElement('button');
        tabEl.className = 'd-tab';
        tabEl.dataset.tabId = tab.id;
        tabEl.dataset.tabType = tab.tabType;
        
        const icon = this._getTabIcon(tab.tabType);
        const typeLabel = this._getTabTypeLabel(tab.tabType);
        
        tabEl.innerHTML = `
            ${icon ? `<span class="d-tab-icon">${icon}</span>` : ''}
            <span class="d-tab-name">${tab.name}</span>
            <span class="d-tab-type">${typeLabel}</span>
            <span class="d-tab-dirty" style="display: none;"></span>
            <span class="d-tab-close">${IconManager.render('close', 12)}</span>
        `;

        tabEl.addEventListener('click', (e) => {
            if (!e.target.closest('.d-tab-close')) {
                this.activateTab(tab.id);
            }
        });

        tabEl.querySelector('.d-tab-close').addEventListener('click', (e) => {
            e.stopPropagation();
            this.closeTab(tab.id);
        });

        this.container.appendChild(tabEl);
    }
    
    _getTabIcon(tabType) {
        const icons = {
            'main': IconManager.render('process', 14),
            'subprocess': IconManager.render('subprocess', 14),
            'activityset': IconManager.render('block', 14),
            'outflow': IconManager.render('outflow', 14)
        };
        return icons[tabType] || null;
    }
    
    _getTabTypeLabel(tabType) {
        const labels = {
            'main': '',
            'subprocess': '[子流程]',
            'activityset': '[活动块]',
            'outflow': '[外部]'
        };
        return labels[tabType] || '';
    }

    _saveCurrentTabData() {
        if (!this.activeTabId) return;
        
        const currentTab = this.tabs.get(this.activeTabId);
        if (!currentTab) return;
        
        const process = this.app.store.getProcess();
        if (process) {
            currentTab.processDef = process.toJSON();
            currentTab.isDirty = this.app.store.isDirty();
            console.log('[TabManager] Saved current tab data:', this.activeTabId, 'activities:', currentTab.processDef.activities?.length);
        }
    }

    activateTab(tabId) {
        if (!this.tabs.has(tabId)) return;

        if (this.activeTabId && this.activeTabId !== tabId) {
            this._saveCurrentTabData();
            this._autoSaveBeforeSwitch();
        }

        this.container.querySelectorAll('.d-tab').forEach(el => {
            el.classList.toggle('active', el.dataset.tabId === tabId);
        });

        this.activeTabId = tabId;
        const tab = this.tabs.get(tabId);
        
        console.log('[TabManager] Activating tab:', tabId, 'activities:', tab.processDef?.activities?.length);
        
        this.app._loadProcessContent(tab.processDef);
        this._saveLastProcess(tabId);
        
        document.getElementById('processName').textContent = tab.name;
    }

    /**
     * Tab切换时自动保存到服务器
     * 当当前Tab失去焦点时，如果有脏数据则自动保存
     */
    async _autoSaveBeforeSwitch() {
        if (!this.activeTabId) return;

        const currentTab = this.tabs.get(this.activeTabId);
        if (!currentTab) return;

        const store = this.app.store;
        if (!store || !store.isDirty()) {
            console.log('[TabManager] No dirty data, skip auto-save on tab switch');
            return;
        }

        try {
            console.log('[TabManager] Auto-saving before tab switch:', this.activeTabId);
            await store.save();
            currentTab.isDirty = false;
            this.setTabDirty(this.activeTabId, false);
            console.log('[TabManager] Auto-save on tab switch completed');
        } catch (error) {
            console.error('[TabManager] Auto-save on tab switch failed:', error);
        }
    }

    async closeTab(tabId) {
        const tab = this.tabs.get(tabId);
        if (!tab) return;

        // 先保存当前Tab数据
        if (this.activeTabId === tabId) {
            this._saveCurrentTabData();
        }

        // 如果有脏数据，自动保存到服务器
        const store = this.app.store;
        if (store && store.isDirty()) {
            try {
                console.log('[TabManager] Auto-saving before close tab:', tabId);
                await store.save();
                tab.isDirty = false;
                console.log('[TabManager] Auto-save before close completed');
            } catch (error) {
                console.error('[TabManager] Auto-save before close failed:', error);
                if (!confirm(`流程 "${tab.name}" 自动保存失败，确定要关闭吗？`)) {
                    return;
                }
            }
        }

        // 如果子流程，保存父流程
        if (tab.tabType === 'subprocess' && tab.parentTabId) {
            try {
                await this._saveParentProcess(tab);
            } catch (error) {
                console.error('[TabManager] Save parent process failed:', error);
            }
        }

        this.tabs.delete(tabId);
        this.container.querySelector(`[data-tab-id="${tabId}"]`)?.remove();

        if (this.activeTabId === tabId) {
            const remainingTabs = Array.from(this.tabs.keys());
            if (remainingTabs.length > 0) {
                this.activateTab(remainingTabs[remainingTabs.length - 1]);
            } else {
                this.activeTabId = null;
                this.app.createNewProcess();
            }
        }
    }

    /**
     * 保存父流程（主流程）并更新子流程配置引用
     */
    async _saveParentProcess(tab) {
        if (!tab.parentTabId) return;
        
        const parentTab = this.tabs.get(tab.parentTabId);
        if (!parentTab) return;
        
        // 更新主流程活动中子流程的 processDefId 引用
        this._updateParentActivitySubFlow(tab, parentTab);
        
        // 如果父流程有未保存的更改，先保存父流程
        if (parentTab.isDirty) {
            console.log('[TabManager] Saving parent process before subprocess:', tab.parentTabId);
            await this.app.api.saveProcess(parentTab.processDef);
            parentTab.isDirty = false;
            console.log('[TabManager] Saved parent process successfully');
        }
    }
    
    /**
     * 更新父流程活动中子流程的 processDefId 引用
     */
    _updateParentActivitySubFlow(subTab, parentTab) {
        // 从子流程标签页ID中提取活动ID (sub_activityId)
        const activityId = subTab.id.replace('sub_', '');
        const subProcessDefId = subTab.processDef.processDefId;
        
        // 在父流程中查找对应的活动
        const parentProcess = parentTab.processDef;
        if (!parentProcess.activities) return;
        
        const activity = parentProcess.activities.find(a => a.activityDefId === activityId);
        if (!activity) return;
        
        // 确保 subFlow 对象存在
        if (!activity.subFlow) {
            activity.subFlow = {};
        }
        
        // 更新子流程ID引用
        const oldProcessDefId = activity.subFlow.processDefId;
        activity.subFlow.processDefId = subProcessDefId;
        
        console.log('[TabManager] Updated activity subFlow.processDefId:', activityId, 
                    'from:', oldProcessDefId, 'to:', subProcessDefId);
        
        // 标记父流程为已修改
        parentTab.isDirty = true;
    }

    updateTabName(tabId, name) {
        const tab = this.tabs.get(tabId);
        if (!tab) return;

        tab.name = name;
        const tabEl = this.container.querySelector(`[data-tab-id="${tabId}"]`);
        if (tabEl) {
            tabEl.querySelector('.d-tab-name').textContent = name;
        }
    }

    setTabDirty(tabId, isDirty) {
        const tab = this.tabs.get(tabId);
        if (!tab) return;

        tab.isDirty = isDirty;
        const tabEl = this.container.querySelector(`[data-tab-id="${tabId}"]`);
        if (tabEl) {
            const dirtyEl = tabEl.querySelector('.d-tab-dirty');
            dirtyEl.style.display = isDirty ? 'inline-block' : 'none';
        }
    }

    getActiveTab() {
        return this.tabs.get(this.activeTabId);
    }

    getTab(tabId) {
        return this.tabs.get(tabId);
    }

    _saveLastProcess(processId) {
        try {
            localStorage.setItem('bpm_last_process', processId);
        } catch (e) {
            console.warn('[TabManager] Failed to save last process:', e);
        }
    }

    getLastProcess() {
        try {
            return localStorage.getItem('bpm_last_process');
        } catch (e) {
            return null;
        }
    }

    hasTabs() {
        return this.tabs.size > 0;
    }
    
    updateCurrentTabData() {
        this._saveCurrentTabData();
    }
}

window.TabManager = TabManager;
