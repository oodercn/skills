/**
 * PanelPluginManager - 面板插件管理器
 * 插件式架构，支持动态注册和加载面板
 * 参考原有Swing程序的XMLPanel架构设计
 */

class PanelPluginManager {
    constructor() {
        // 注册的插件
        this.plugins = new Map();
        
        // 面板实例缓存
        this.instances = new Map();
        
        // 插件配置
        this.config = {
            // 默认面板顺序
            defaultOrder: [
                'basic',      // 基本信息
                'timing',     // 时限配置
                'flow',       // 流程控制
                'right',      // 权限配置
                'form',       // 表单配置
                'service',    // 服务配置
                'listeners',  // 监听器
                'rightGroups' // 权限组
            ],
            
            // 面板分组
            groups: {
                'process': ['basic', 'timing', 'listeners', 'rightGroups'],
                'activity': ['basic', 'timing', 'flow', 'right', 'form', 'service'],
                'route': ['basic', 'condition']
            }
        };
        
        // 事件监听
        this.eventListeners = new Map();
    }

    /**
     * 注册面板插件
     * @param {string} id - 插件ID
     * @param {Object} plugin - 插件定义
     * @param {number} priority - 优先级(越小越靠前)
     */
    register(id, plugin, priority = 100) {
        if (this.plugins.has(id)) {
            console.warn(`插件 ${id} 已存在，将被覆盖`);
        }
        
        // 验证插件结构
        if (!this._validatePlugin(plugin)) {
            throw new Error(`插件 ${id} 结构无效`);
        }
        
        this.plugins.set(id, {
            id,
            priority,
            ...plugin,
            registeredAt: new Date()
        });
        
        this._emit('plugin:registered', { id, plugin });
        
        return this;
    }

    /**
     * 注销面板插件
     */
    unregister(id) {
        if (this.plugins.has(id)) {
            this.plugins.delete(id);
            this.instances.delete(id);
            this._emit('plugin:unregistered', { id });
        }
        return this;
    }

    /**
     * 获取面板插件
     */
    get(id) {
        return this.plugins.get(id);
    }

    /**
     * 获取所有已注册的插件
     */
    getAll() {
        return Array.from(this.plugins.values());
    }

    /**
     * 获取指定类型的面板
     */
    getByType(type) {
        return this.getAll()
            .filter(p => p.type === type)
            .sort((a, b) => a.priority - b.priority);
    }

    /**
     * 获取指定分组的插件
     */
    getByGroup(groupName) {
        const groupIds = this.config.groups[groupName] || [];
        return groupIds
            .map(id => this.plugins.get(id))
            .filter(p => p !== undefined)
            .sort((a, b) => a.priority - b.priority);
    }

    /**
     * 创建面板实例
     */
    create(id, container, options = {}) {
        const plugin = this.plugins.get(id);
        if (!plugin) {
            throw new Error(`插件 ${id} 未找到`);
        }

        // 检查权限
        if (plugin.permissions && !this._checkPermissions(plugin.permissions)) {
            console.warn(`没有权限使用面板 ${id}`);
            return null;
        }

        // 创建实例
        const instance = new PanelInstance(id, plugin, container, options);
        this.instances.set(id, instance);

        // 初始化
        if (typeof plugin.onInit === 'function') {
            plugin.onInit.call(instance, options);
        }

        this._emit('panel:created', { id, instance });

        return instance;
    }

    /**
     * 销毁面板实例
     */
    destroy(id) {
        const instance = this.instances.get(id);
        if (instance) {
            const plugin = this.plugins.get(id);
            if (plugin && typeof plugin.onDestroy === 'function') {
                plugin.onDestroy.call(instance);
            }
            instance.destroy();
            this.instances.delete(id);
            this._emit('panel:destroyed', { id });
        }
        return this;
    }

    /**
     * 渲染所有面板到容器
     */
    renderAll(container, context, filter = null) {
        const plugins = this.getAll()
            .filter(p => !filter || filter(p))
            .sort((a, b) => a.priority - b.priority);

        const rendered = [];
        plugins.forEach(plugin => {
            const instance = this.create(plugin.id, container, { context });
            if (instance) {
                rendered.push(instance);
            }
        });

        return rendered;
    }

    /**
     * 根据数据模型自动选择并渲染面板
     */
    renderForModel(container, model, modelType) {
        const groupIds = this.config.groups[modelType] || [];
        
        return groupIds.map(id => {
            const plugin = this.plugins.get(id);
            if (!plugin) return null;

            // 检查是否适用于当前模型
            if (plugin.applicable && !plugin.applicable(model)) {
                return null;
            }

            return this.create(id, container, { 
                context: model,
                modelType 
            });
        }).filter(i => i !== null);
    }

    /**
     * 验证插件结构
     */
    _validatePlugin(plugin) {
        // 必需字段
        if (!plugin.name || typeof plugin.name !== 'string') {
            console.error('插件缺少name字段');
            return false;
        }

        // 渲染函数
        if (!plugin.render || typeof plugin.render !== 'function') {
            console.error('插件缺少render函数');
            return false;
        }

        return true;
    }

    /**
     * 检查权限
     */
    _checkPermissions(permissions) {
        // TODO: 实现权限检查逻辑
        return true;
    }

    /**
     * 事件监听
     */
    on(event, callback) {
        if (!this.eventListeners.has(event)) {
            this.eventListeners.set(event, []);
        }
        this.eventListeners.get(event).push(callback);
        return this;
    }

    /**
     * 移除事件监听
     */
    off(event, callback) {
        if (this.eventListeners.has(event)) {
            const listeners = this.eventListeners.get(event);
            const index = listeners.indexOf(callback);
            if (index > -1) {
                listeners.splice(index, 1);
            }
        }
        return this;
    }

    /**
     * 触发事件
     */
    _emit(event, data) {
        if (this.eventListeners.has(event)) {
            this.eventListeners.get(event).forEach(callback => {
                try {
                    callback(data);
                } catch (error) {
                    console.error('事件处理错误:', error);
                }
            });
        }
    }

    /**
     * 加载插件配置
     */
    loadConfig(config) {
        Object.assign(this.config, config);
        return this;
    }

    /**
     * 导出当前配置
     */
    exportConfig() {
        return {
            plugins: this.getAll().map(p => ({
                id: p.id,
                name: p.name,
                type: p.type,
                priority: p.priority
            })),
            config: this.config
        };
    }
}

/**
 * 面板实例
 */
class PanelInstance {
    constructor(id, plugin, container, options) {
        this.id = id;
        this.plugin = plugin;
        this.container = container;
        this.options = options;
        
        // DOM元素
        this.element = null;
        this.header = null;
        this.body = null;
        
        // 状态
        this.state = {
            visible: true,
            enabled: true,
            collapsed: false,
            dirty: false
        };

        // 数据绑定
        this.data = null;
        this.bindings = new Map();

        // 子面板
        this.children = [];
    }

    /**
     * 渲染面板
     */
    render() {
        // 创建面板容器
        this.element = document.createElement('div');
        this.element.className = `panel-plugin panel-${this.id}`;
        this.element.dataset.panelId = this.id;

        // 创建头部
        if (this.plugin.showHeader !== false) {
            this._renderHeader();
        }

        // 创建内容区
        this._renderBody();

        // 添加到容器
        this.container.appendChild(this.element);

        // 调用插件渲染
        if (this.plugin.render) {
            this.plugin.render.call(this, this.body, this.options.context);
        }

        return this;
    }

    /**
     * 渲染头部
     */
    _renderHeader() {
        this.header = document.createElement('div');
        this.header.className = 'panel-header';

        // 标题
        const title = document.createElement('span');
        title.className = 'panel-title';
        title.textContent = this.plugin.name;
        this.header.appendChild(title);

        // 工具按钮
        const tools = document.createElement('div');
        tools.className = 'panel-tools';

        // 折叠按钮
        if (this.plugin.collapsible !== false) {
            const collapseBtn = document.createElement('button');
            collapseBtn.className = 'panel-btn-collapse';
            collapseBtn.innerHTML = '−';
            collapseBtn.onclick = () => this.toggleCollapse();
            tools.appendChild(collapseBtn);
        }

        // 关闭按钮
        if (this.plugin.closable) {
            const closeBtn = document.createElement('button');
            closeBtn.className = 'panel-btn-close';
            closeBtn.innerHTML = '×';
            closeBtn.onclick = () => this.close();
            tools.appendChild(closeBtn);
        }

        this.header.appendChild(tools);
        this.element.appendChild(this.header);
    }

    /**
     * 渲染内容区
     */
    _renderBody() {
        this.body = document.createElement('div');
        this.body.className = 'panel-body';
        
        if (this.plugin.bodyClass) {
            this.body.classList.add(this.plugin.bodyClass);
        }

        this.element.appendChild(this.body);
    }

    /**
     * 绑定数据
     */
    bind(data) {
        this.data = data;
        
        if (this.plugin.onBind) {
            this.plugin.onBind.call(this, data);
        }

        // 更新所有绑定
        this.bindings.forEach((binding, element) => {
            this._updateBinding(element, binding);
        });

        return this;
    }

    /**
     * 更新绑定
     */
    _updateBinding(element, binding) {
        const value = this._getValueByPath(this.data, binding.path);
        
        switch (binding.type) {
            case 'text':
                element.textContent = value;
                break;
            case 'value':
                element.value = value;
                break;
            case 'html':
                element.innerHTML = value;
                break;
            case 'attribute':
                element.setAttribute(binding.attribute, value);
                break;
            case 'class':
                if (binding.condition(value)) {
                    element.classList.add(binding.className);
                } else {
                    element.classList.remove(binding.className);
                }
                break;
        }
    }

    /**
     * 根据路径获取值
     */
    _getValueByPath(obj, path) {
        return path.split('.').reduce((o, p) => o && o[p], obj);
    }

    /**
     * 获取数据
     */
    getData() {
        return this.data;
    }

    /**
     * 设置值
     */
    setValue(path, value) {
        if (this.data) {
            const keys = path.split('.');
            const lastKey = keys.pop();
            const target = keys.reduce((o, p) => o[p] = o[p] || {}, this.data);
            target[lastKey] = value;
            
            this.state.dirty = true;
            
            if (this.plugin.onChange) {
                this.plugin.onChange.call(this, path, value, this.data);
            }
        }
        return this;
    }

    /**
     * 获取值
     */
    getValue(path) {
        return this._getValueByPath(this.data, path);
    }

    /**
     * 显示/隐藏
     */
    toggle() {
        this.state.visible = !this.state.visible;
        this.element.style.display = this.state.visible ? '' : 'none';
        return this;
    }

    /**
     * 展开/折叠
     */
    toggleCollapse() {
        this.state.collapsed = !this.state.collapsed;
        this.body.style.display = this.state.collapsed ? 'none' : '';
        
        const btn = this.header.querySelector('.panel-btn-collapse');
        if (btn) {
            btn.innerHTML = this.state.collapsed ? '+' : '−';
        }
        
        return this;
    }

    /**
     * 启用/禁用
     */
    setEnabled(enabled) {
        this.state.enabled = enabled;
        this.element.classList.toggle('panel-disabled', !enabled);
        
        const inputs = this.element.querySelectorAll('input, select, textarea, button');
        inputs.forEach(input => input.disabled = !enabled);
        
        return this;
    }

    /**
     * 关闭面板
     */
    close() {
        if (this.plugin.onClose) {
            this.plugin.onClose.call(this);
        }
        
        this.destroy();
        
        if (this.options.onClose) {
            this.options.onClose(this);
        }
    }

    /**
     * 销毁
     */
    destroy() {
        // 销毁子面板
        this.children.forEach(child => child.destroy());
        this.children = [];

        // 移除DOM
        if (this.element && this.element.parentNode) {
            this.element.parentNode.removeChild(this.element);
        }

        this.element = null;
        this.header = null;
        this.body = null;
        this.data = null;
    }

    /**
     * 验证
     */
    validate() {
        if (this.plugin.validate) {
            return this.plugin.validate.call(this, this.data);
        }
        return { valid: true, errors: [] };
    }

    /**
     * 序列化
     */
    serialize() {
        if (this.plugin.serialize) {
            return this.plugin.serialize.call(this, this.data);
        }
        return this.data;
    }
}

// 创建全局实例
window.PanelPluginManager = PanelPluginManager;
window.panelPluginManager = new PanelPluginManager();
