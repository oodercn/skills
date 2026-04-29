class PanelManager {
    constructor(container, store) {
        this.container = container;
        this.store = store;
        this.plugins = new Map();
        this.subPlugins = new Map();
        this.currentPlugin = null;
        this.currentData = null;
        this.currentType = null;
        this.currentGroup = null;
        this.autoSaveEnabled = true;
        this.expandedDrawers = new Set();
        
        this._init();
    }
    
    _init() {
        this._initContainers();
        this._registerDefaultPlugins();
        this._bindEvents();
    }
    
    _initContainers() {
        if (!this.container) {
            console.error('[PanelManager] Container not found');
            return;
        }
        
        this.tabsContainer = this.container.querySelector('#panelTabs');
        this.contentContainer = this.container.querySelector('#panelContent');
        this.titleElement = this.container.querySelector('#panelTitle');
        
        if (!this.contentContainer) {
            this.contentContainer = this.container;
        }
        
        console.log('[PanelManager] Containers initialized:', {
            tabs: !!this.tabsContainer,
            content: !!this.contentContainer,
            title: !!this.titleElement
        });
    }
    
    _registerDefaultPlugins() {
        if (typeof ProcessPanelPlugin !== 'undefined') {
            this.register('process', new ProcessPanelPlugin());
        }
        
        // 使用新的 ActivityPropertyPlugin 替代旧的 ActivityPanelPlugin
        if (typeof ActivityPropertyPlugin !== 'undefined') {
            console.log('[PanelManager] Registering ActivityPropertyPlugin');
            this.register('activity', new ActivityPropertyPlugin());
        } else if (typeof ActivityPanelPlugin !== 'undefined') {
            console.log('[PanelManager] ActivityPropertyPlugin not found, falling back to ActivityPanelPlugin');
            this.register('activity', new ActivityPanelPlugin());
        }
        
        if (typeof RoutePanelPlugin !== 'undefined') {
            this.register('route', new RoutePanelPlugin());
        }
        
        this._registerSubPlugins();
        
        console.log('[PanelManager] Registered plugins:', Array.from(this.plugins.keys()));
        console.log('[PanelManager] Registered subPlugins:', Array.from(this.subPlugins.keys()));
    }
    
    _registerSubPlugins() {
        const allActivityPlugins = [];
        
        if (typeof ActivityPanelPlugins !== 'undefined') {
            console.log('[PanelManager] ActivityPanelPlugins found, keys:', Object.keys(ActivityPanelPlugins));
            const pluginOrder = ['BasicPanelPlugin', 'TimingPanelPlugin', 'FlowControlPanelPlugin', 
                               'RightPanelPlugin', 'FormPanelPlugin', 'ServicePanelPlugin',
                               'KnowledgePanelPlugin', 'DataPanelPlugin', 'CommPanelPlugin',
                               'AgentPanelPlugin', 'ScenePanelPlugin'];
            
            pluginOrder.forEach(name => {
                if (ActivityPanelPlugins[name]) {
                    const category = this._getPluginCategory(name);
                    console.log(`[PanelManager] Registering plugin: ${name}, category: ${category}`);
                    allActivityPlugins.push({
                        id: `activity-${name.toLowerCase().replace('panelplugin', '')}`,
                        name: ActivityPanelPlugins[name].name || name,
                        icon: ActivityPanelPlugins[name].icon || '',
                        plugin: ActivityPanelPlugins[name],
                        applicable: ActivityPanelPlugins[name].applicable,
                        category: category
                    });
                } else {
                    console.warn(`[PanelManager] Plugin not found: ${name}`);
                }
            });
        } else {
            console.error('[PanelManager] ActivityPanelPlugins is not defined!');
        }

        if (typeof AgentPanelPlugins !== 'undefined') {
            const pluginOrder = ['AgentBasicPanelPlugin', 'LLMPanelPlugin', 'AgentToolsPanelPlugin'];
            
            pluginOrder.forEach(name => {
                if (AgentPanelPlugins[name]) {
                    allActivityPlugins.push({
                        id: `agent-${name.toLowerCase().replace('panelplugin', '')}`,
                        name: AgentPanelPlugins[name].name || name,
                        icon: AgentPanelPlugins[name].icon || '',
                        plugin: AgentPanelPlugins[name],
                        applicable: AgentPanelPlugins[name].applicable,
                        category: this._getPluginCategory(name)
                    });
                }
            });
        }

        if (typeof ScenePanelPlugins !== 'undefined') {
            const pluginOrder = ['SceneBasicPanelPlugin', 'SceneEnginePanelPlugin', 
                               'SceneParamsPanelPlugin', 'SceneCapabilityPanelPlugin'];
            
            pluginOrder.forEach(name => {
                if (ScenePanelPlugins[name]) {
                    allActivityPlugins.push({
                        id: `scene-${name.toLowerCase().replace('panelplugin', '')}`,
                        name: ScenePanelPlugins[name].name || name,
                        icon: ScenePanelPlugins[name].icon || '',
                        plugin: ScenePanelPlugins[name],
                        applicable: ScenePanelPlugins[name].applicable,
                        category: this._getPluginCategory(name)
                    });
                }
            });
        }

        if (typeof ListenerPanelPlugins !== 'undefined') {
            const pluginOrder = ['ActivityListenerPanelPlugin', 'ExecutionListenerPanelPlugin', 
                               'ExpressionPanelPlugin', 'MultiInstancePanelPlugin', 'ExtensionPropertiesPanelPlugin'];
            
            pluginOrder.forEach(name => {
                if (ListenerPanelPlugins[name]) {
                    allActivityPlugins.push({
                        id: `listener-${name.toLowerCase().replace('panelplugin', '')}`,
                        name: ListenerPanelPlugins[name].name || name,
                        icon: ListenerPanelPlugins[name].icon || '',
                        plugin: ListenerPanelPlugins[name],
                        applicable: ListenerPanelPlugins[name].applicable,
                        category: this._getPluginCategory(name)
                    });
                }
            });
        }
        
        this.subPlugins.set('activity', allActivityPlugins);
        this.subPlugins.set('activity-agent', allActivityPlugins);
        this.subPlugins.set('activity-scene', allActivityPlugins);
        
        if (typeof ProcessPanelPlugins !== 'undefined') {
            const processSubPlugins = [];
            const pluginOrder = ['ProcessBasicPanelPlugin', 'StartNodePanelPlugin', 'EndNodesPanelPlugin',
                               'ListenersPanelPlugin', 'RightGroupsPanelPlugin', 'ProcessVariablesPanelPlugin',
                               'ProcessTimingPanelPlugin'];
            
            pluginOrder.forEach(name => {
                if (ProcessPanelPlugins[name]) {
                    processSubPlugins.push({
                        id: `process-${name.toLowerCase().replace('panelplugin', '')}`,
                        name: ProcessPanelPlugins[name].name || name,
                        icon: ProcessPanelPlugins[name].icon || '',
                        plugin: ProcessPanelPlugins[name],
                        applicable: ProcessPanelPlugins[name].applicable,
                        category: this._getPluginCategory(name)
                    });
                }
            });
            
            this.subPlugins.set('process', processSubPlugins);
        }
    }
    
    _getPluginCategory(pluginName) {
        // 流程标签：流程控制、时序、路由等流程相关配置
        const flowPlugins = ['TimingPanelPlugin', 'FlowControlPanelPlugin', 
                            'ListenersPanelPlugin', 'ProcessVariablesPanelPlugin', 
                            'ProcessTimingPanelPlugin', 'RoutePanelPlugin'];
        
        // 技能标签：三维度分类、技能相关配置（表单、服务、知识、数据、通讯等）
        const skillPlugins = ['BasicPanelPlugin', 'FormPanelPlugin', 'ServicePanelPlugin', 
                             'KnowledgePanelPlugin', 'DataPanelPlugin', 'CommPanelPlugin',
                             'LLMPanelPlugin', 'ScenePanelPlugin'];
        
        // Agent标签：Agent相关配置
        const agentPlugins = ['AgentPanelPlugin', 'RightPanelPlugin', 
                             'AgentToolsPanelPlugin', 'SceneEnginePanelPlugin', 
                             'SceneParamsPanelPlugin', 'SceneCapabilityPanelPlugin',
                             'ActivityListenerPanelPlugin', 'ExecutionListenerPanelPlugin',
                             'ExpressionPanelPlugin', 'MultiInstancePanelPlugin', 
                             'ExtensionPropertiesPanelPlugin'];
        
        if (flowPlugins.some(p => pluginName.includes(p))) {
            return 'flow';
        } else if (skillPlugins.some(p => pluginName.includes(p))) {
            return 'skill';
        } else if (agentPlugins.some(p => pluginName.includes(p))) {
            return 'agent';
        }
        return 'skill'; // 默认归类到技能
    }
    
    _bindEvents() {
        const btnClose = this.container.querySelector('#btnPanelClose');
        if (btnClose) {
            btnClose.addEventListener('click', () => {
                this._triggerAutoSave();
                this.hide();
            });
        }
        
        const btnPin = this.container.querySelector('#btnPanelPin');
        if (btnPin) {
            btnPin.addEventListener('click', () => this.togglePin());
        }
        
        document.addEventListener('click', (e) => {
            if (this.currentData && this.container && !this.container.contains(e.target)) {
                this._triggerAutoSave();
            }
        });
    }
    
    _triggerAutoSave() {
        if (!this.autoSaveEnabled || !this.currentPlugin) return;
        
        const pluginData = this.currentPlugin.getCurrentData ? this.currentPlugin.getCurrentData() : this.currentData;
        
        if (pluginData && pluginData.activityDefId && this.store) {
            this.store.updateActivity(pluginData);
        } else if (pluginData && pluginData.routeDefId && this.store) {
            this.store.updateRoute(pluginData);
        } else if (pluginData && pluginData.processDefId && this.store) {
            this.store.setDirty(true);
        }
    }
    
    register(type, plugin) {
        if (!plugin || typeof plugin.render !== 'function') {
            console.error(`[PanelManager] Invalid plugin for type: ${type}`);
            return;
        }
        
        this.plugins.set(type, plugin);
        console.log(`[PanelManager] Plugin registered: ${type}`);
    }
    
    unregister(type) {
        const plugin = this.plugins.get(type);
        if (plugin) {
            plugin.destroy();
            this.plugins.delete(type);
            console.log(`[PanelManager] Plugin unregistered: ${type}`);
        }
    }

    getActivityGroup(activity) {
        if (!activity) return 'activity';
        
        const category = activity.activityCategory || 'HUMAN';
        
        switch (category) {
            case 'AGENT':
                return 'activity-agent';
            case 'SCENE':
                return 'activity-scene';
            default:
                return 'activity';
        }
    }
    
    render(type, data) {
        console.log(`[PanelManager] Rendering panel: ${type}`, data);
        
        let actualType = type;
        let group = type;

        if (type === 'activity' && data) {
            group = this.getActivityGroup(data);
            actualType = group;
            console.log(`[PanelManager] Activity group determined: ${group}`);
        }
        
        const plugin = this.plugins.get(type) || this.plugins.get('activity');
        if (!plugin) {
            console.error(`[PanelManager] No plugin found for type: ${type}`);
            this._showEmpty('未知的面板类型');
            return;
        }
        
        if (plugin.validate && !plugin.validate(data)) {
            console.warn(`[PanelManager] Invalid data for panel: ${type}`, data);
            this._showEmpty('无效的数据');
            return;
        }
        
        if (this.currentPlugin && this.currentPlugin !== plugin) {
            this.currentPlugin.destroy();
        }
        
        this.currentPlugin = plugin;
        this.currentData = data;
        this.currentType = type;
        this.currentGroup = group;
        
        if (this.titleElement) {
            this.titleElement.textContent = plugin.getTitle(data);
        }
        
        const subPlugins = this.subPlugins.get(group);
        if (subPlugins && subPlugins.length > 0) {
            this._renderCategoryTabs(subPlugins, data);
        } else {
            this._renderTabs(plugin, data);
            
            if (!this.contentContainer) {
                console.error('[PanelManager] contentContainer is null');
                return;
            }
            
            plugin.init(this.contentContainer);
            plugin.render(data);
        }
        
        this.show();
        
        console.log(`[PanelManager] Panel rendered: ${type}, group: ${group}`);
    }
    
    _renderCategoryTabs(subPlugins, data) {
        if (!this.tabsContainer || !this.contentContainer) return;

        const applicablePlugins = subPlugins.filter(sp => {
            if (sp.applicable) {
                const result = sp.applicable(data);
                console.log(`[PanelManager] Plugin ${sp.name} applicable: ${result}`, data);
                return result;
            }
            return true;
        });
        
        console.log(`[PanelManager] Applicable plugins: ${applicablePlugins.length}/${subPlugins.length}`);
        console.log('[PanelManager] Applicable plugins by category:', {
            common: applicablePlugins.filter(p => p.category === 'common').map(p => p.name),
            business: applicablePlugins.filter(p => p.category === 'business').map(p => p.name),
            plugin: applicablePlugins.filter(p => p.category === 'plugin').map(p => p.name)
        });
        
        if (applicablePlugins.length === 0) {
            this.tabsContainer.innerHTML = '';
            this._showEmpty('无可用配置项');
            return;
        }

        const categories = [
            { id: 'flow', name: '流程', icon: '◈' },
            { id: 'skill', name: '技能', icon: '◆' },
            { id: 'agent', name: 'Agent', icon: '◇' }
        ];
        
        const pluginsByCategory = {
            flow: applicablePlugins.filter(p => p.category === 'flow'),
            skill: applicablePlugins.filter(p => p.category === 'skill'),
            agent: applicablePlugins.filter(p => p.category === 'agent')
        };

        this.tabsContainer.innerHTML = categories.map((cat, index) => {
            const hasPlugins = pluginsByCategory[cat.id].length > 0;
            return `
                <div class="d-category-tab ${index === 0 && hasPlugins ? 'active' : ''} ${!hasPlugins ? 'disabled' : ''}" 
                     data-category="${cat.id}">
                    <span class="d-category-tab-icon">${cat.icon}</span>
                    <span class="d-category-tab-label">${cat.name}</span>
                </div>
            `;
        }).join('');

        this.contentContainer.innerHTML = categories.map((cat, index) => {
            const plugins = pluginsByCategory[cat.id];
            const isActive = index === 0 && plugins.length > 0;
            return `
                <div class="d-category-content ${isActive ? 'active' : ''}" data-category="${cat.id}">
                    <div class="d-drawer-list">
                        ${plugins.map(sp => `
                            <div class="d-drawer ${this.expandedDrawers.has(sp.id) ? 'expanded' : ''}" data-plugin-id="${sp.id}">
                                <div class="d-drawer-header">
                                    <div class="d-drawer-title">
                                        <span class="d-drawer-icon">${sp.icon || ''}</span>
                                        <span>${sp.name}</span>
                                    </div>
                                    <div class="d-drawer-toggle">▶</div>
                                </div>
                                <div class="d-drawer-content" id="drawer-content-${sp.id}"></div>
                            </div>
                        `).join('')}
                    </div>
                </div>
            `;
        }).join('');

        this.tabsContainer.querySelectorAll('.d-category-tab').forEach(tab => {
            tab.addEventListener('click', (e) => {
                const categoryId = e.currentTarget.dataset.category;
                this._switchCategory(categoryId);
            });
        });

        this.contentContainer.querySelectorAll('.d-drawer-header').forEach(header => {
            header.addEventListener('click', (e) => {
                const drawer = e.currentTarget.closest('.d-drawer');
                const pluginId = drawer.dataset.pluginId;
                this._toggleDrawer(pluginId, applicablePlugins, data);
            });
        });

        const firstCategory = categories.find(c => pluginsByCategory[c.id].length > 0);
        if (firstCategory) {
            const firstPlugin = pluginsByCategory[firstCategory.id][0];
            if (firstPlugin && firstPlugin.plugin) {
                this._renderDrawerContent(firstPlugin, data);
                this.expandedDrawers.add(firstPlugin.id);
                const drawer = this.contentContainer.querySelector(`[data-plugin-id="${firstPlugin.id}"]`);
                if (drawer) drawer.classList.add('expanded');
            }
        }
    }
    
    _switchCategory(categoryId) {
        this.tabsContainer.querySelectorAll('.d-category-tab').forEach(tab => {
            tab.classList.toggle('active', tab.dataset.category === categoryId);
        });
        
        this.contentContainer.querySelectorAll('.d-category-content').forEach(content => {
            content.classList.toggle('active', content.dataset.category === categoryId);
        });
    }
    
    _toggleDrawer(pluginId, plugins, data) {
        const drawer = this.contentContainer.querySelector(`[data-plugin-id="${pluginId}"]`);
        if (!drawer) return;
        
        const isExpanded = drawer.classList.contains('expanded');
        
        if (!isExpanded) {
            const pluginInfo = plugins.find(p => p.id === pluginId);
            if (pluginInfo && pluginInfo.plugin) {
                this._renderDrawerContent(pluginInfo, data);
            }
            drawer.classList.add('expanded');
            this.expandedDrawers.add(pluginId);
        } else {
            drawer.classList.remove('expanded');
            this.expandedDrawers.delete(pluginId);
        }
    }
    
    _renderDrawerContent(pluginInfo, data) {
        const contentEl = this.contentContainer.querySelector(`#drawer-content-${pluginInfo.id}`);
        if (!contentEl || !pluginInfo.plugin) return;
        
        contentEl.innerHTML = '';
        
        if (pluginInfo.plugin.render) {
            pluginInfo.plugin.render(contentEl, data);
        }
    }
    
    _renderTabsWithSubPlugins(subPlugins, data) {
        this._renderCategoryTabs(subPlugins, data);
    }
    
    _switchTab(tabId, index, subPlugins, data) {
        this.tabsContainer.querySelectorAll('.d-panel-tab').forEach(tab => {
            tab.classList.toggle('active', tab.dataset.tab === tabId);
        });
        
        if (subPlugins[index] && subPlugins[index].plugin) {
            this._renderSubPlugin(subPlugins[index].plugin, data);
        }
    }
    
    _renderSubPlugin(subPlugin, data) {
        if (!this.contentContainer || !subPlugin) return;
        
        this.contentContainer.innerHTML = '';
        
        if (subPlugin.render) {
            subPlugin.render(this.contentContainer, data);
        }
    }
    
    _renderTabs(plugin, data) {
        if (!this.tabsContainer) return;
        
        const tabs = plugin.getTabs ? plugin.getTabs(data) : [];
        
        if (tabs.length === 0) {
            this.tabsContainer.innerHTML = '';
            return;
        }
        
        this.tabsContainer.innerHTML = tabs.map((tab, index) => `
            <div class="d-panel-tab ${index === 0 ? 'active' : ''}" data-tab="${tab.id}">
                <span class="d-icon">${tab.icon || ''}</span>
                <span>${tab.name}</span>
            </div>
        `).join('');
        
        this.tabsContainer.querySelectorAll('.d-panel-tab').forEach(tabEl => {
            tabEl.addEventListener('click', (e) => {
                const tabId = e.currentTarget.dataset.tab;
                this._switchTabDefault(tabId);
            });
        });
    }
    
    _switchTabDefault(tabId) {
        this.tabsContainer.querySelectorAll('.d-panel-tab').forEach(tab => {
            tab.classList.toggle('active', tab.dataset.tab === tabId);
        });
        
        if (this.currentPlugin && this.currentPlugin.switchTab) {
            this.currentPlugin.switchTab(tabId);
        }
    }
    
    _showEmpty(message) {
        if (this.contentContainer) {
            this.contentContainer.innerHTML = `<div class="d-empty">${message || '请选择元素查看属性'}</div>`;
        }
    }
    
    show() {
        if (this.container) {
            this.container.style.display = 'flex';
        }
    }
    
    hide() {
        if (this.container) {
            this.container.style.display = 'none';
        }
    }
    
    togglePin() {
        const btnPin = this.container.querySelector('#btnPanelPin');
        if (btnPin) {
            btnPin.classList.toggle('pinned');
        }
    }
    
    getCurrentData() {
        if (this.currentPlugin && this.currentPlugin.getCurrentData) {
            return this.currentPlugin.getCurrentData();
        }
        return this.currentData;
    }
    
    getCurrentPlugin() {
        return this.currentPlugin;
    }
    
    setAutoSave(enabled) {
        this.autoSaveEnabled = enabled;
    }
    
    refreshPlugins(data) {
        console.log('[PanelManager] Refreshing plugins with data:', data);
        
        if (!this.currentType || !data) return;
        
        const group = this.getActivityGroup(data);
        console.log(`[PanelManager] Refreshing for group: ${group}`);
        
        const subPlugins = this.subPlugins.get(group);
        if (subPlugins && subPlugins.length > 0) {
            this._renderCategoryTabs(subPlugins, data);
        }
    }
    
    destroy() {
        this.plugins.forEach(plugin => plugin.destroy());
        this.plugins.clear();
        this.subPlugins.clear();
        
        this.currentPlugin = null;
        this.currentData = null;
    }
}

window.PanelManager = PanelManager;
