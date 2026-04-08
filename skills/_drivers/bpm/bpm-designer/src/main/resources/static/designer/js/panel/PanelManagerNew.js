/**
 * 面板管理器 - 插件架构版本
 * 支持动态注册和卸载面板插件
 */
class PanelManagerNew {
    constructor(container, store) {
        this.container = container;
        this.store = store;
        this.plugins = new Map();
        this.currentPlugin = null;
        this.currentData = null;
        
        this._init();
    }
    
    _init() {
        // 创建面板结构
        this._createPanelStructure();
        
        // 注册默认插件
        this._registerDefaultPlugins();
        
        // 绑定事件
        this._bindEvents();
    }
    
    _createPanelStructure() {
        if (!this.container) return;
        
        this.container.innerHTML = `
            <div class="d-panel-header">
                <h3 id="panelTitle">属性面板</h3>
                <div class="d-panel-actions">
                    <button id="btnPanelPin" class="d-btn d-btn-icon" title="固定面板">
                        <span class="d-icon">📌</span>
                    </button>
                    <button id="btnPanelClose" class="d-btn d-btn-icon" title="关闭面板">
                        <span class="d-icon">✕</span>
                    </button>
                </div>
            </div>
            <div class="d-panel-tabs" id="panelTabs"></div>
            <div class="d-panel-content" id="panelContent">
                <div class="d-empty">请选择元素查看属性</div>
            </div>
        `;
        
        this.tabsContainer = this.container.querySelector('#panelTabs');
        this.contentContainer = this.container.querySelector('#panelContent');
        this.titleElement = this.container.querySelector('#panelTitle');
    }
    
    _registerDefaultPlugins() {
        // 注册流程面板插件
        this.register('process', new ProcessPanelPlugin());
        
        // 注册活动面板插件
        this.register('activity', new ActivityPanelPlugin());
        
        // 注册路由面板插件
        this.register('route', new RoutePanelPlugin());
    }
    
    _bindEvents() {
        // 面板关闭按钮
        const btnClose = this.container.querySelector('#btnPanelClose');
        if (btnClose) {
            btnClose.addEventListener('click', () => this.hide());
        }
        
        // 面板固定按钮
        const btnPin = this.container.querySelector('#btnPanelPin');
        if (btnPin) {
            btnPin.addEventListener('click', () => this.togglePin());
        }
    }
    
    /**
     * 注册面板插件
     * @param {string} type - 面板类型
     * @param {PanelPlugin} plugin - 面板插件实例
     */
    register(type, plugin) {
        if (!plugin || typeof plugin.render !== 'function') {
            console.error(`[PanelManager] Invalid plugin for type: ${type}`);
            return;
        }
        
        this.plugins.set(type, plugin);
        console.log(`[PanelManager] Plugin registered: ${type}`);
    }
    
    /**
     * 卸载面板插件
     * @param {string} type - 面板类型
     */
    unregister(type) {
        const plugin = this.plugins.get(type);
        if (plugin) {
            plugin.destroy();
            this.plugins.delete(type);
            console.log(`[PanelManager] Plugin unregistered: ${type}`);
        }
    }
    
    /**
     * 渲染面板
     * @param {string} type - 面板类型
     * @param {Object} data - 数据对象
     */
    render(type, data) {
        console.log(`[PanelManager] Rendering panel: ${type}`, data);
        
        const plugin = this.plugins.get(type);
        if (!plugin) {
            console.error(`[PanelManager] No plugin found for type: ${type}`);
            this._showEmpty('未知的面板类型');
            return;
        }
        
        // 验证数据
        if (!plugin.validate(data)) {
            console.warn(`[PanelManager] Invalid data for panel: ${type}`);
            this._showEmpty('无效的数据');
            return;
        }
        
        // 销毁当前插件
        if (this.currentPlugin && this.currentPlugin !== plugin) {
            this.currentPlugin.destroy();
        }
        
        // 更新当前状态
        this.currentPlugin = plugin;
        this.currentData = data;
        
        // 更新标题
        if (this.titleElement) {
            this.titleElement.textContent = plugin.getTitle(data);
        }
        
        // 渲染标签页
        this._renderTabs(plugin, data);
        
        // 渲染内容
        plugin.init(this.contentContainer);
        plugin.render(data);
        
        // 显示面板
        this.show();
        
        console.log(`[PanelManager] Panel rendered: ${type}`);
    }
    
    _renderTabs(plugin, data) {
        if (!this.tabsContainer) return;
        
        // 获取标签页配置
        const tabs = plugin.getTabs ? plugin.getTabs(data) : [];
        
        if (tabs.length === 0) {
            this.tabsContainer.innerHTML = '';
            return;
        }
        
        // 渲染标签页
        this.tabsContainer.innerHTML = tabs.map((tab, index) => `
            <div class="d-panel-tab ${index === 0 ? 'active' : ''}" data-tab="${tab.id}">
                <span class="d-icon">${tab.icon || ''}</span>
                <span>${tab.name}</span>
            </div>
        `).join('');
        
        // 绑定标签页点击事件
        this.tabsContainer.querySelectorAll('.d-panel-tab').forEach(tabEl => {
            tabEl.addEventListener('click', (e) => {
                const tabId = e.currentTarget.dataset.tab;
                this._switchTab(tabId);
            });
        });
    }
    
    _switchTab(tabId) {
        // 更新标签页状态
        this.tabsContainer.querySelectorAll('.d-panel-tab').forEach(tab => {
            tab.classList.toggle('active', tab.dataset.tab === tabId);
        });
        
        // 切换内容
        if (this.currentPlugin && this.currentPlugin.switchTab) {
            this.currentPlugin.switchTab(tabId);
        }
    }
    
    _showEmpty(message) {
        if (this.contentContainer) {
            this.contentContainer.innerHTML = `<div class="d-empty">${message || '请选择元素查看属性'}</div>`;
        }
    }
    
    /**
     * 显示面板
     */
    show() {
        if (this.container) {
            this.container.style.display = 'flex';
        }
    }
    
    /**
     * 隐藏面板
     */
    hide() {
        if (this.container) {
            this.container.style.display = 'none';
        }
    }
    
    /**
     * 切换面板固定状态
     */
    togglePin() {
        const btnPin = this.container.querySelector('#btnPanelPin');
        if (btnPin) {
            btnPin.classList.toggle('pinned');
        }
    }
    
    /**
     * 获取当前数据
     * @returns {Object} 当前数据
     */
    getCurrentData() {
        return this.currentData;
    }
    
    /**
     * 获取当前插件
     * @returns {PanelPlugin} 当前插件
     */
    getCurrentPlugin() {
        return this.currentPlugin;
    }
    
    /**
     * 销毁面板管理器
     */
    destroy() {
        // 销毁所有插件
        this.plugins.forEach(plugin => plugin.destroy());
        this.plugins.clear();
        
        // 清空容器
        if (this.container) {
            this.container.innerHTML = '';
        }
        
        this.currentPlugin = null;
        this.currentData = null;
    }
}

// 导出面板管理器
window.PanelManagerNew = PanelManagerNew;
