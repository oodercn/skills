/**
 * AbstractPluginPanel - 抽象插件面板基类
 * 
 * 实现UI与数据的完全分离，提供统一的插件面板架构
 * 整合PluginDataSource和PluginDataAdapter，与PanelPluginManager协作
 * 
 * 参考原有Swing设计：
 * - XMLPanel - 基础面板类
 * - XMLTablePanel - 表格面板类
 * - XMLElement - 数据元素接口
 * 
 * @author AI Assistant
 * @version 1.0
 */

class AbstractPluginPanel {
    constructor(options = {}) {
        this.options = {
            // 面板ID
            id: '',
            
            // 面板名称
            name: '',
            
            // 面板类型
            type: 'panel',
            
            // 图标
            icon: 'cube',
            
            // 优先级
            priority: 100,
            
            // 数据配置
            dataConfig: {
                // 数据键名
                dataKey: null,
                
                // 远程数据源类型
                remoteType: null,
                
                // 是否需要远程数据
                needRemoteData: false,
                
                // 数据存储格式: 'json' | 'xml' | 'multivalue'
                storageFormat: 'json',
                
                // 数据适配器类型
                adapterType: null
            },
            
            // UI配置
            uiConfig: {
                // 布局类型
                layout: 'form',
                
                // 是否可折叠
                collapsible: false,
                
                // 默认展开
                expanded: true,
                
                // 样式类名
                className: ''
            },
            
            // 事件配置
            events: {
                // 数据变更回调
                onDataChange: null,
                
                // 远程数据加载完成
                onRemoteDataLoaded: null,
                
                // 验证失败
                onValidationError: null
            },
            
            ...options
        };
        
        // 面板状态
        this.state = {
            initialized: false,
            loading: false,
            dirty: false,
            valid: true,
            errors: []
        };
        
        // 数据存储
        this.data = null;
        
        // 原始数据（用于比较变更）
        this.originalData = null;
        
        // 远程数据缓存
        this.remoteData = null;
        
        // DOM元素引用
        this.elements = {
            container: null,
            header: null,
            body: null,
            footer: null
        };
        
        // 数据源引用
        this.dataSource = window.pluginDataSource;
        this.dataAdapter = window.pluginDataAdapter;
        
        // 事件总线
        this.eventBus = new EventTarget();
    }

    // ==================== 生命周期方法 ====================

    /**
     * 初始化面板
     * 子类应重写此方法进行自定义初始化
     */
    async initialize() {
        if (this.state.initialized) {
            return;
        }
        
        this._emit('panel:initializing', { panel: this });
        
        // 加载远程数据（如果需要）
        if (this.options.dataConfig.needRemoteData) {
            await this.loadRemoteData();
        }
        
        this.state.initialized = true;
        this._emit('panel:initialized', { panel: this });
    }

    /**
     * 渲染面板
     * @param {HTMLElement} container - 容器元素
     */
    render(container) {
        if (!container) {
            throw new Error('Container is required for rendering');
        }
        
        this.elements.container = container;
        container.innerHTML = '';
        
        // 创建面板结构
        const panel = this._createPanelStructure();
        container.appendChild(panel);
        
        // 渲染内容
        this._renderContent();
        
        // 绑定事件
        this._bindEvents();
        
        this._emit('panel:rendered', { panel: this });
        
        return this;
    }

    /**
     * 销毁面板
     */
    destroy() {
        this._emit('panel:destroying', { panel: this });
        
        // 清理DOM
        if (this.elements.container) {
            this.elements.container.innerHTML = '';
        }
        
        // 清理数据
        this.data = null;
        this.originalData = null;
        this.remoteData = null;
        
        // 重置状态
        this.state = {
            initialized: false,
            loading: false,
            dirty: false,
            valid: true,
            errors: []
        };
        
        this._emit('panel:destroyed', { panel: this });
    }

    // ==================== 数据操作方法 ====================

    /**
     * 设置数据
     * @param {any} data - 数据
     * @param {boolean} silent - 是否静默（不触发事件）
     */
    setData(data, silent = false) {
        this.originalData = this._cloneData(data);
        this.data = this._cloneData(data);
        this.state.dirty = false;
        
        if (!silent) {
            this._refreshUI();
            this._emit('panel:data:set', { panel: this, data: this.data });
        }
    }

    /**
     * 获取数据
     * @returns {any} - 当前数据
     */
    getData() {
        return this._cloneData(this.data);
    }

    /**
     * 获取变更的数据
     * @returns {any} - 变更后的数据
     */
    getChangedData() {
        if (!this.state.dirty) {
            return null;
        }
        return this._cloneData(this.data);
    }

    /**
     * 更新数据字段
     * @param {string} field - 字段路径
     * @param {any} value - 新值
     */
    updateField(field, value) {
        this._setNestedValue(this.data, field, value);
        this.state.dirty = true;
        
        this._onDataChange(field, value);
        this._emit('panel:field:change', { panel: this, field, value });
    }

    /**
     * 加载远程数据
     */
    async loadRemoteData() {
        if (!this.dataSource || !this.options.dataConfig.remoteType) {
            return;
        }
        
        this.state.loading = true;
        this._emit('panel:remote:loading', { panel: this });
        
        try {
            const remoteType = this.options.dataConfig.remoteType;
            let data;
            
            switch (remoteType) {
                case 'org':
                    data = await this.dataSource.getOrganizations();
                    break;
                case 'person':
                    data = await this.dataSource.getPersons();
                    break;
                case 'role':
                    data = await this.dataSource.getRoles();
                    break;
                case 'form':
                    data = await this.dataSource.getForms();
                    break;
                case 'service':
                    data = await this.dataSource.getServices();
                    break;
                case 'expressionVariables':
                    data = await this.dataSource.getExpressionVariables();
                    break;
                case 'expressionTemplates':
                    data = await this.dataSource.getExpressionTemplates();
                    break;
                case 'listenerConfigs':
                    data = await this.dataSource.getListenerConfigs();
                    break;
                case 'agentList':
                    data = await this.dataSource.getAgentList();
                    break;
                case 'agentTypes':
                    data = await this.dataSource.getAgentTypes();
                    break;
                case 'agentRoles':
                    data = await this.dataSource.getAgentRoles();
                    break;
                case 'llmProviders':
                    data = await this.dataSource.getLlmProviders();
                    break;
                case 'agentCapabilities':
                    data = await this.dataSource.getAgentCapabilities();
                    break;
                default:
                    console.warn(`Unknown remote type: ${remoteType}`);
                    data = null;
            }
            
            this.remoteData = data;
            
            if (this.options.events.onRemoteDataLoaded) {
                this.options.events.onRemoteDataLoaded(data);
            }
            
            this._emit('panel:remote:loaded', { panel: this, data });
        } catch (error) {
            console.error('Failed to load remote data:', error);
            this._emit('panel:remote:error', { panel: this, error });
        } finally {
            this.state.loading = false;
        }
    }

    /**
     * 验证数据
     * @returns {boolean} - 是否验证通过
     */
    validate() {
        this.state.errors = [];
        
        // 子类重写此方法进行自定义验证
        const isValid = this._doValidate();
        
        this.state.valid = isValid;
        
        if (!isValid && this.options.events.onValidationError) {
            this.options.events.onValidationError(this.state.errors);
        }
        
        this._emit('panel:validated', { panel: this, valid: isValid, errors: this.state.errors });
        
        return isValid;
    }

    /**
     * 序列化数据为后端格式
     * @returns {Object} - 后端格式数据
     */
    serialize() {
        const adapterType = this.options.dataConfig.adapterType;
        const data = this.getData();
        
        if (adapterType && this.dataAdapter) {
            return this.dataAdapter.toBackendJSON(adapterType, data);
        }
        
        return {
            attributename: this.options.dataConfig.dataKey || this.options.id,
            attributevalue: typeof data === 'object' ? JSON.stringify(data) : data,
            attributetype: 'BPD'
        };
    }

    /**
     * 反序列化后端数据
     * @param {Object} backendData - 后端数据
     */
    deserialize(backendData) {
        const adapterType = this.options.dataConfig.adapterType;
        
        if (adapterType && this.dataAdapter) {
            const result = this.dataAdapter.fromBackendJSON(backendData);
            this.setData(result.data);
        } else {
            try {
                const data = JSON.parse(backendData.attributevalue);
                this.setData(data);
            } catch {
                this.setData(backendData.attributevalue);
            }
        }
    }

    // ==================== UI渲染方法（子类可重写） ====================

    /**
     * 创建面板结构
     * @returns {HTMLElement} - 面板元素
     */
    _createPanelStructure() {
        const panel = document.createElement('div');
        panel.className = `plugin-panel ${this.options.uiConfig.className}`;
        panel.dataset.panelId = this.options.id;
        
        // 头部
        if (this.options.name) {
            this.elements.header = document.createElement('div');
            this.elements.header.className = 'panel-header';
            this.elements.header.innerHTML = `
                <span class="panel-title">
                    <i class="icon-${this.options.icon}"></i>
                    ${this.options.name}
                </span>
                ${this.options.uiConfig.collapsible ? '<button class="btn-toggle"><i class="icon-chevron-down"></i></button>' : ''}
            `;
            panel.appendChild(this.elements.header);
        }
        
        // 主体
        this.elements.body = document.createElement('div');
        this.elements.body.className = 'panel-body';
        if (!this.options.uiConfig.expanded) {
            this.elements.body.style.display = 'none';
        }
        panel.appendChild(this.elements.body);
        
        // 底部
        this.elements.footer = document.createElement('div');
        this.elements.footer.className = 'panel-footer';
        panel.appendChild(this.elements.footer);
        
        return panel;
    }

    /**
     * 渲染内容（子类必须重写）
     */
    _renderContent() {
        // 子类重写此方法
        throw new Error('_renderContent must be implemented by subclass');
    }

    /**
     * 刷新UI
     */
    _refreshUI() {
        // 子类重写此方法
    }

    /**
     * 绑定事件
     */
    _bindEvents() {
        // 折叠/展开
        if (this.options.uiConfig.collapsible && this.elements.header) {
            const toggleBtn = this.elements.header.querySelector('.btn-toggle');
            if (toggleBtn) {
                toggleBtn.addEventListener('click', () => {
                    this._toggleCollapse();
                });
            }
        }
    }

    /**
     * 切换折叠状态
     */
    _toggleCollapse() {
        const isExpanded = this.elements.body.style.display !== 'none';
        this.elements.body.style.display = isExpanded ? 'none' : 'block';
        
        const icon = this.elements.header.querySelector('.btn-toggle i');
        if (icon) {
            icon.className = isExpanded ? 'icon-chevron-right' : 'icon-chevron-down';
        }
        
        this._emit('panel:toggle', { panel: this, expanded: !isExpanded });
    }

    // ==================== 数据变更处理 ====================

    /**
     * 数据变更处理（子类可重写）
     * @param {string} field - 字段
     * @param {any} value - 新值
     */
    _onDataChange(field, value) {
        if (this.options.events.onDataChange) {
            this.options.events.onDataChange(field, value, this.data);
        }
    }

    /**
     * 执行验证（子类可重写）
     * @returns {boolean} - 是否验证通过
     */
    _doValidate() {
        return true;
    }

    // ==================== 工具方法 ====================

    /**
     * 克隆数据
     */
    _cloneData(data) {
        if (data === null || data === undefined) {
            return data;
        }
        return JSON.parse(JSON.stringify(data));
    }

    /**
     * 获取嵌套属性值
     */
    _getNestedValue(obj, path) {
        return path.split('.').reduce((o, p) => o && o[p], obj);
    }

    /**
     * 设置嵌套属性值
     */
    _setNestedValue(obj, path, value) {
        const parts = path.split('.');
        const last = parts.pop();
        const target = parts.reduce((o, p) => {
            if (!o[p]) o[p] = {};
            return o[p];
        }, obj);
        target[last] = value;
    }

    // ==================== 事件管理 ====================

    /**
     * 监听事件
     */
    on(event, handler) {
        this.eventBus.addEventListener(event, handler);
        return () => this.off(event, handler);
    }

    /**
     * 取消监听
     */
    off(event, handler) {
        this.eventBus.removeEventListener(event, handler);
    }

    /**
     * 触发事件
     */
    _emit(event, data) {
        this.eventBus.dispatchEvent(new CustomEvent(event, { detail: data }));
    }

    // ==================== 静态方法 ====================

    /**
     * 检查面板是否适用
     * @param {any} context - 上下文对象
     * @returns {boolean} - 是否适用
     */
    static isApplicable(context) {
        return true;
    }
}

// 导出
window.AbstractPluginPanel = AbstractPluginPanel;
