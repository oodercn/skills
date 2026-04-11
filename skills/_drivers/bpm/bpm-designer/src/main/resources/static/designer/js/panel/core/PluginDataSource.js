/**
 * PluginDataSource - 统一插件数据源
 * 
 * 参考原有Swing设计中的数据获取模式：
 * - OrgManager.getTopOrgs() - 获取顶级组织
 * - OrgManager.getPersonByID() - 获取人员
 * - FormulaService.validate() - 验证表达式
 * 
 * 实现UI与数据的完全分离，提供统一的数据获取接口
 * 
 * @author AI Assistant
 * @version 1.0
 */

class PluginDataSource {
    constructor(options = {}) {
        this.options = {
            baseUrl: '/bpm/api/dictionary',
            bpmBaseUrl: '/bpm/api/processdef',
            timeout: 30000,
            enableCache: true,
            cacheExpire: 5 * 60 * 1000,
            headers: {
                'Content-Type': 'application/json'
            },
            ...options
        };
        
        // 缓存存储
        this.cache = new Map();
        
        // 请求拦截器
        this.requestInterceptors = [];
        
        // 响应拦截器
        this.responseInterceptors = [];
        
        // 事件总线
        this.eventBus = new EventTarget();
    }

    // ==================== 核心数据获取方法 ====================

    /**
     * 获取组织机构数据
     * 对应Swing: OrgManager.getTopOrgs(), org.getChildrenList()
     */
    async getOrganizations(params = {}) {
        const { parentId, sysId, lazy = true } = params;
        
        const cacheKey = `org_${parentId || 'root'}_${sysId || 'default'}`;
        
        return this._fetchWithCache(cacheKey, async () => {
            const response = await this._request('/org/tree', {
                method: 'GET',
                params: { parentId, lazy }
            });
            return this._normalizeOrgData(response.data);
        });
    }

    /**
     * 获取人员数据
     * 对应Swing: OrgManager.getPersonByID(), org.getPersonList()
     */
    async getPersons(params = {}) {
        const { orgId, personId, search } = params;
        
        // 根据ID获取单个人员
        if (personId) {
            const cacheKey = `person_${personId}`;
            return this._fetchWithCache(cacheKey, async () => {
                const response = await this._request(`/person/${personId}`, {
                    method: 'GET'
                });
                return this._normalizePersonData(response.data);
            });
        }
        
        // 获取组织下的人员列表
        const cacheKey = `persons_${orgId}_${search || ''}`;
        return this._fetchWithCache(cacheKey, async () => {
            const response = await this._request('/person/list', {
                method: 'GET',
                params: { orgId, search }
            });
            return response.data.map(p => this._normalizePersonData(p));
        });
    }

    /**
     * 获取角色数据
     */
    async getRoles(params = {}) {
        const cacheKey = `roles_${JSON.stringify(params)}`;
        
        return this._fetchWithCache(cacheKey, async () => {
            const response = await this._request('/role/list', {
                method: 'GET',
                params
            });
            return response.data;
        });
    }

    /**
     * 验证表达式
     * 对应Swing: FormulaService.validate()
     */
    async validateExpression(expression, context = {}) {
        const response = await this._request('/expression/validate', {
            method: 'POST',
            data: { expression, context }
        });
        return response.data;
    }

    /**
     * 获取表达式变量
     */
    async getExpressionVariables(contextType = 'process') {
        const cacheKey = `expr_vars_${contextType}`;
        
        return this._fetchWithCache(cacheKey, async () => {
            const response = await this._request('/expression/variables', {
                method: 'GET',
                params: { processDefId: contextType }
            });
            return response.data;
        });
    }

    /**
     * 获取表达式模板
     */
    async getExpressionTemplates(type = 'common') {
        const cacheKey = `expr_templates_${type}`;
        
        return this._fetchWithCache(cacheKey, async () => {
            const response = await this._request('/expression/templates', {
                method: 'GET'
            });
            return response.data;
        });
    }

    /**
     * 获取监听器配置列表
     */
    async getListenerConfigs(type = 'process') {
        const cacheKey = `listeners_${type}`;
        
        return this._fetchWithCache(cacheKey, async () => {
            const response = await this._request('/listener/list', {
                method: 'GET',
                params: { type }
            });
            return response.data;
        });
    }

    /**
     * 获取表单列表
     */
    async getForms(params = {}) {
        const cacheKey = `forms_${JSON.stringify(params)}`;
        
        return this._fetchWithCache(cacheKey, async () => {
            const response = await this._request('/form/list', {
                method: 'GET',
                params
            });
            return response.data;
        });
    }

    /**
     * 获取服务列表
     */
    async getServices(params = {}) {
        const cacheKey = `services_${JSON.stringify(params)}`;
        
        return this._fetchWithCache(cacheKey, async () => {
            const response = await this._request('/service/list', {
                method: 'GET',
                params
            });
            return response.data;
        });
    }

    /**
     * 获取IoT设备数据
     */
    async getIotDevices(params = {}) {
        const { type, areaId, gatewayId } = params;
        
        const cacheKey = `iot_${type}_${areaId}_${gatewayId}`;
        
        return this._fetchWithCache(cacheKey, async () => {
            const response = await this._request('/iot/devices', {
                method: 'GET',
                params
            });
            return response.data;
        });
    }

    /**
     * 获取ESD组件数据
     */
    async getEsdComponents(params = {}) {
        const cacheKey = `esd_${JSON.stringify(params)}`;
        
        return this._fetchWithCache(cacheKey, async () => {
            const response = await this._request('/esd/components', {
                method: 'GET',
                params
            });
            return response.data;
        });
    }

    /**
     * 获取数据库表数据
     */
    async getDbTables(params = {}) {
        const cacheKey = `db_tables_${JSON.stringify(params)}`;
        
        return this._fetchWithCache(cacheKey, async () => {
            const response = await this._request('/db/tables', {
                method: 'GET',
                params
            });
            return response.data;
        });
    }

    /**
     * 获取数据库字段
     */
    async getDbFields(tableName) {
        const cacheKey = `db_fields_${tableName}`;
        
        return this._fetchWithCache(cacheKey, async () => {
            const response = await this._request(`/db/fields/${tableName}`, {
                method: 'GET'
            });
            return response.data;
        });
    }

    // ==================== 数据转换方法 ====================

    /**
     * 规范化组织数据
     * 对应Swing: Org对象
     */
    _normalizeOrgData(data) {
        if (!data) return null;
        
        return {
            id: data.id || data.orgId,
            code: data.code || data.orgCode,
            name: data.name || data.orgName,
            parentId: data.parentId,
            type: data.type || 'org',
            leaf: data.leaf !== undefined ? data.leaf : !data.hasChildren,
            icon: data.icon || (data.hasChildren ? 'folder' : 'building'),
            children: data.children ? data.children.map(c => this._normalizeOrgData(c)) : undefined,
            // 原始数据保留
            _raw: data
        };
    }

    /**
     * 规范化人员数据
     * 对应Swing: Person对象
     */
    _normalizePersonData(data) {
        if (!data) return null;
        
        return {
            id: data.id || data.personId,
            code: data.code || data.personCode,
            name: data.name || data.personName,
            orgId: data.orgId,
            orgName: data.orgName,
            deptId: data.deptId,
            deptName: data.deptName,
            role: data.role,
            email: data.email,
            phone: data.phone,
            type: 'person',
            icon: 'user',
            leaf: true,
            // 原始数据保留
            _raw: data
        };
    }

    // ==================== 缓存管理 ====================

    /**
     * 带缓存的数据获取
     */
    async _fetchWithCache(cacheKey, fetchFn) {
        // 检查缓存
        if (this.options.enableCache && this.cache.has(cacheKey)) {
            const cached = this.cache.get(cacheKey);
            if (Date.now() - cached.timestamp < this.options.cacheExpire) {
                this._emit('cache:hit', { key: cacheKey, data: cached.data });
                return cached.data;
            }
            // 缓存过期，删除
            this.cache.delete(cacheKey);
        }
        
        // 执行请求
        const data = await fetchFn();
        
        // 存入缓存
        if (this.options.enableCache) {
            this.cache.set(cacheKey, {
                data,
                timestamp: Date.now()
            });
        }
        
        this._emit('cache:miss', { key: cacheKey, data });
        return data;
    }

    /**
     * 清除缓存
     */
    clearCache(pattern = null) {
        if (!pattern) {
            this.cache.clear();
        } else {
            for (const key of this.cache.keys()) {
                if (key.includes(pattern)) {
                    this.cache.delete(key);
                }
            }
        }
    }

    /**
     * 获取缓存统计
     */
    getCacheStats() {
        return {
            size: this.cache.size,
            keys: Array.from(this.cache.keys())
        };
    }

    // ==================== HTTP请求方法 ====================

    /**
     * 发送HTTP请求
     */
    async _request(url, options = {}) {
        const { method = 'GET', params, data, headers = {} } = options;
        
        // 构建完整URL
        let fullUrl = `${this.options.baseUrl}${url}`;
        
        // 添加查询参数
        if (params) {
            const queryString = new URLSearchParams(params).toString();
            if (queryString) {
                fullUrl += `?${queryString}`;
            }
        }
        
        // 请求配置
        const config = {
            method,
            headers: {
                ...this.options.headers,
                ...headers
            },
            signal: AbortSignal.timeout(this.options.timeout)
        };
        
        // 添加请求体
        if (data && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
            config.body = JSON.stringify(data);
        }
        
        // 执行请求拦截器
        let finalConfig = config;
        for (const interceptor of this.requestInterceptors) {
            finalConfig = await interceptor(fullUrl, finalConfig);
        }
        
        try {
            this._emit('request:start', { url: fullUrl, config: finalConfig });
            
            const response = await fetch(fullUrl, finalConfig);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const result = await response.json();
            
            // 执行响应拦截器
            let finalResult = result;
            for (const interceptor of this.responseInterceptors) {
                finalResult = await interceptor(finalResult, response);
            }
            
            this._emit('request:success', { url: fullUrl, data: finalResult });
            
            return finalResult;
        } catch (error) {
            this._emit('request:error', { url: fullUrl, error });
            throw error;
        }
    }

    // ==================== 拦截器管理 ====================

    /**
     * 添加请求拦截器
     */
    addRequestInterceptor(interceptor) {
        this.requestInterceptors.push(interceptor);
        return () => {
            const index = this.requestInterceptors.indexOf(interceptor);
            if (index > -1) {
                this.requestInterceptors.splice(index, 1);
            }
        };
    }

    /**
     * 添加响应拦截器
     */
    addResponseInterceptor(interceptor) {
        this.responseInterceptors.push(interceptor);
        return () => {
            const index = this.responseInterceptors.indexOf(interceptor);
            if (index > -1) {
                this.responseInterceptors.splice(index, 1);
            }
        };
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

    // ==================== 静态实例 ====================

    static getInstance(options) {
        if (!PluginDataSource._instance) {
            PluginDataSource._instance = new PluginDataSource(options);
        }
        return PluginDataSource._instance;
    }
}

// 创建全局实例
window.PluginDataSource = PluginDataSource;
window.pluginDataSource = PluginDataSource.getInstance();
