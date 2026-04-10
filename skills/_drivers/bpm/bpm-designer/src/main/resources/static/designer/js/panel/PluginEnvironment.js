/**
 * PluginEnvironment - 独立插件环境
 *
 * 整合所有插件相关组件，提供独立的插件运行环境
 * 包含：PluginDataSource、PluginDataAdapter、AbstractPluginPanel、PanelPluginManager
 *
 * @author AI Assistant
 * @version 1.0
 */

class PluginEnvironment {
    constructor(options = {}) {
        this.options = {
            // 数据源配置
            dataSource: {
                baseUrl: '/api/bpm',
                timeout: 30000,
                enableCache: true
            },

            // 适配器配置
            dataAdapter: {
                delimiter: ':',
                xmlNamespace: 'itjds'
            },

            // 插件管理器配置
            pluginManager: {
                defaultOrder: [],
                groups: {}
            },

            ...options
        };

        // 组件实例
        this.dataSource = null;
        this.dataAdapter = null;
        this.pluginManager = null;

        // 已注册的面板类
        this.panelClasses = new Map();

        // 插件实例缓存
        this.pluginInstances = new Map();

        // 初始化状态
        this.initialized = false;
    }

    /**
     * 初始化插件环境
     */
    async initialize() {
        if (this.initialized) {
            console.warn('PluginEnvironment already initialized');
            return this;
        }

        console.log('Initializing PluginEnvironment...');

        // 初始化数据源
        this._initDataSource();

        // 初始化适配器
        this._initDataAdapter();

        // 初始化插件管理器
        this._initPluginManager();

        // 注册内置面板类
        this._registerBuiltinPanels();

        this.initialized = true;
        console.log('PluginEnvironment initialized successfully');

        return this;
    }

    /**
     * 初始化数据源
     */
    _initDataSource() {
        if (window.PluginDataSource) {
            this.dataSource = PluginDataSource.getInstance(this.options.dataSource);
        } else {
            console.error('PluginDataSource not loaded');
        }
    }

    /**
     * 初始化数据适配器
     */
    _initDataAdapter() {
        if (window.PluginDataAdapter) {
            this.dataAdapter = PluginDataAdapter.getInstance(this.options.dataAdapter);
        } else {
            console.error('PluginDataAdapter not loaded');
        }
    }

    /**
     * 初始化插件管理器
     */
    _initPluginManager() {
        if (window.PanelPluginManager) {
            this.pluginManager = window.panelPluginManager;
            this.pluginManager.loadConfig(this.options.pluginManager);
        } else {
            console.error('PanelPluginManager not loaded');
        }
    }

    /**
     * 注册内置面板类
     */
    _registerBuiltinPanels() {
        // 注册基于AbstractPluginPanel的面板类
        if (window.AbstractPluginPanel) {
            this.registerPanelClass('abstract', AbstractPluginPanel);
        }

        // 注册TableListPanel
        if (window.TableListPanel) {
            this.registerPanelClass('tableList', TableListPanel);
        }

        // 注册ExternalDictionaryPanel
        if (window.ExternalDictionaryPanel) {
            this.registerPanelClass('externalDictionary', ExternalDictionaryPanel);
        }
    }

    // ==================== 面板类管理 ====================

    /**
     * 注册面板类
     * @param {string} name - 类名
     * @param {Function} panelClass - 面板类
     */
    registerPanelClass(name, panelClass) {
        this.panelClasses.set(name, panelClass);
        console.log(`Panel class registered: ${name}`);
    }

    /**
     * 获取面板类
     * @param {string} name - 类名
     * @returns {Function} - 面板类
     */
    getPanelClass(name) {
        return this.panelClasses.get(name);
    }

    /**
     * 创建面板实例
     * @param {string} className - 类名
     * @param {Object} options - 配置选项
     * @returns {Object} - 面板实例
     */
    createPanel(className, options = {}) {
        const PanelClass = this.panelClasses.get(className);
        if (!PanelClass) {
            throw new Error(`Panel class not found: ${className}`);
        }

        const instance = new PanelClass(options);
        const instanceId = `${className}_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
        this.pluginInstances.set(instanceId, instance);

        // 添加销毁时清理
        const originalDestroy = instance.destroy.bind(instance);
        instance.destroy = () => {
            originalDestroy();
            this.pluginInstances.delete(instanceId);
        };

        return instance;
    }

    // ==================== 插件注册管理 ====================

    /**
     * 注册插件
     * @param {string} id - 插件ID
     * @param {Object} plugin - 插件定义
     * @param {number} priority - 优先级
     */
    registerPlugin(id, plugin, priority = 100) {
        if (!this.pluginManager) {
            throw new Error('PluginManager not initialized');
        }

        // 包装插件以集成数据源和适配器
        const wrappedPlugin = this._wrapPlugin(plugin);
        this.pluginManager.register(id, wrappedPlugin, priority);

        return this;
    }

    /**
     * 包装插件，集成数据源和适配器
     */
    _wrapPlugin(plugin) {
        const env = this;

        return {
            ...plugin,

            // 包装render方法，注入环境
            render(container, context) {
                // 为上下文添加环境引用
                const enhancedContext = {
                    ...context,
                    $env: env,
                    $dataSource: env.dataSource,
                    $dataAdapter: env.dataAdapter
                };

                // 调用原始render
                if (typeof plugin.render === 'function') {
                    plugin.render.call(this, container, enhancedContext);
                }
            },

            // 包装onBind方法
            onBind(context) {
                const enhancedContext = {
                    ...context,
                    $env: env,
                    $dataSource: env.dataSource,
                    $dataAdapter: env.dataAdapter
                };

                if (typeof plugin.onBind === 'function') {
                    plugin.onBind.call(this, enhancedContext);
                }
            }
        };
    }

    /**
     * 获取插件
     * @param {string} id - 插件ID
     * @returns {Object} - 插件定义
     */
    getPlugin(id) {
        if (!this.pluginManager) {
            return null;
        }
        return this.pluginManager.get(id);
    }

    /**
     * 渲染插件
     * @param {string} id - 插件ID
     * @param {HTMLElement} container - 容器
     * @param {Object} context - 上下文
     */
    renderPlugin(id, container, context = {}) {
        if (!this.pluginManager) {
            throw new Error('PluginManager not initialized');
        }

        const enhancedContext = {
            ...context,
            $env: this,
            $dataSource: this.dataSource,
            $dataAdapter: this.dataAdapter
        };

        return this.pluginManager.render(id, container, enhancedContext);
    }

    /**
     * 渲染适用于模型的插件
     * @param {HTMLElement} container - 容器
     * @param {Object} model - 模型对象
     * @param {string} modelType - 模型类型
     */
    renderForModel(container, model, modelType) {
        if (!this.pluginManager) {
            throw new Error('PluginManager not initialized');
        }

        const context = {
            model,
            modelType,
            $env: this,
            $dataSource: this.dataSource,
            $dataAdapter: this.dataAdapter
        };

        return this.pluginManager.renderForModel(container, context, modelType);
    }

    // ==================== 数据操作快捷方法 ====================

    /**
     * 获取远程数据
     * @param {string} type - 数据类型
     * @param {Object} params - 参数
     */
    async fetchData(type, params = {}) {
        if (!this.dataSource) {
            throw new Error('DataSource not initialized');
        }

        switch (type) {
            case 'org':
                return this.dataSource.getOrganizations(params);
            case 'person':
                return this.dataSource.getPersons(params);
            case 'role':
                return this.dataSource.getRoles(params);
            case 'form':
                return this.dataSource.getForms(params);
            case 'service':
                return this.dataSource.getServices(params);
            case 'expressionVariables':
                return this.dataSource.getExpressionVariables(params.contextType);
            case 'expressionTemplates':
                return this.dataSource.getExpressionTemplates(params.type);
            case 'listenerConfigs':
                return this.dataSource.getListenerConfigs(params.type);
            default:
                throw new Error(`Unknown data type: ${type}`);
        }
    }

    /**
     * 序列化数据
     * @param {string} type - 数据类型
     * @param {any} data - 数据
     */
    serialize(type, data) {
        if (!this.dataAdapter) {
            throw new Error('DataAdapter not initialized');
        }
        return this.dataAdapter.toBackendJSON(type, data);
    }

    /**
     * 反序列化数据
     * @param {Object} backendData - 后端数据
     */
    deserialize(backendData) {
        if (!this.dataAdapter) {
            throw new Error('DataAdapter not initialized');
        }
        return this.dataAdapter.fromBackendJSON(backendData);
    }

    // ==================== 事件管理 ====================

    /**
     * 监听全局事件
     * @param {string} event - 事件名
     * @param {Function} handler - 处理函数
     */
    on(event, handler) {
        if (this.dataSource) {
            this.dataSource.on(event, handler);
        }
    }

    /**
     * 取消监听全局事件
     * @param {string} event - 事件名
     * @param {Function} handler - 处理函数
     */
    off(event, handler) {
        if (this.dataSource) {
            this.dataSource.off(event, handler);
        }
    }

    // ==================== 缓存管理 ====================

    /**
     * 清除缓存
     * @param {string} pattern - 匹配模式
     */
    clearCache(pattern = null) {
        if (this.dataSource) {
            this.dataSource.clearCache(pattern);
        }
    }

    /**
     * 获取缓存统计
     */
    getCacheStats() {
        if (this.dataSource) {
            return this.dataSource.getCacheStats();
        }
        return null;
    }

    // ==================== 工具方法 ====================

    /**
     * 获取环境状态
     */
    getStatus() {
        return {
            initialized: this.initialized,
            dataSource: !!this.dataSource,
            dataAdapter: !!this.dataAdapter,
            pluginManager: !!this.pluginManager,
            panelClasses: Array.from(this.panelClasses.keys()),
            pluginInstances: this.pluginInstances.size
        };
    }

    /**
     * 销毁环境
     */
    destroy() {
        // 销毁所有插件实例
        this.pluginInstances.forEach(instance => {
            if (typeof instance.destroy === 'function') {
                instance.destroy();
            }
        });
        this.pluginInstances.clear();

        // 清理引用
        this.dataSource = null;
        this.dataAdapter = null;
        this.pluginManager = null;
        this.panelClasses.clear();

        this.initialized = false;

        console.log('PluginEnvironment destroyed');
    }

    // ==================== 静态方法 ====================

    static getInstance(options) {
        if (!PluginEnvironment._instance) {
            PluginEnvironment._instance = new PluginEnvironment(options);
        }
        return PluginEnvironment._instance;
    }
}

// 创建全局实例
window.PluginEnvironment = PluginEnvironment;
window.pluginEnvironment = PluginEnvironment.getInstance();

// 自动初始化
document.addEventListener('DOMContentLoaded', async () => {
    if (window.pluginEnvironment && !window.pluginEnvironment.initialized) {
        await window.pluginEnvironment.initialize();
    }
});
