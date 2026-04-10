/**
 * 活动定义模型 - 新版本
 * 支持完整的扩展属性组：RIGHT, FORM, SERVICE, WORKFLOW
 */
class ActivityDefNew {
    constructor(data) {
        // 基本属性
        this.activityDefId = data?.activityDefId || this._generateId();
        this.name = data?.name || '新活动';
        this.description = data?.description || '';
        
        // 活动类型和位置
        this.activityType = data?.activityType || 'TASK';
        this.position = this._normalizePosition(data?.position, this.activityType);
        
        // 坐标和参与者 (BPD属性)
        this.positionCoord = this._normalizeCoord(data?.positionCoord);
        this.participantId = data?.participantId || '';
        
        // 实现方式
        this.implementation = data?.implementation || 'No';
        
        // 时限配置
        this.limitTime = data?.limitTime || 0;
        this.alertTime = data?.alertTime || 0;
        this.durationUnit = data?.durationUnit || 'D';
        
        // 流程控制
        this.join = data?.join || 'XOR';
        this.split = data?.split || 'XOR';
        
        // 退回配置
        this.canRouteBack = data?.canRouteBack || 'NO';
        this.routeBackMethod = data?.routeBackMethod || 'PREV';
        
        // 特送配置
        this.canSpecialSend = data?.canSpecialSend || 'NO';
        this.specialScope = data?.specialScope || 'ALL';
        
        // ==================== 属性组 ====================
        // RIGHT 属性组
        this.RIGHT = data?.RIGHT || {};
        
        // FORM 属性组
        this.FORM = data?.FORM || {};
        
        // SERVICE 属性组
        this.SERVICE = data?.SERVICE || {};
        
        // WORKFLOW 属性组
        this.WORKFLOW = data?.WORKFLOW || {};
        
        // 块活动属性 (BPD)
        this.startOfBlock = data?.startOfBlock || null;
        this.endOfBlock = data?.endOfBlock || null;
        this.participantVisualOrder = data?.participantVisualOrder || '';
        
        // 其他扩展属性
        this.extendedAttributes = data?.extendedAttributes || {};
    }

    /**
     * 生成唯一ID
     */
    _generateId() {
        return 'act_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    /**
     * 标准化位置值
     */
    _normalizePosition(position, activityType) {
        // 根据 activityType 确定 position
        if (activityType === 'START') {
            return 'START';
        }
        if (activityType === 'END') {
            return 'END';
        }
        
        // 处理后端返回的 POSITION_ 前缀
        if (position === 'POSITION_START') return 'START';
        if (position === 'POSITION_END') return 'END';
        if (position === 'POSITION_NORMAL') return 'NORMAL';
        
        return position || 'NORMAL';
    }

    /**
     * 标准化坐标值
     */
    _normalizeCoord(coord) {
        if (coord && typeof coord === 'object') {
            return {
                x: parseInt(coord.x) || 0,
                y: parseInt(coord.y) || 0
            };
        }
        return { x: 0, y: 0 };
    }

    /**
     * 设置坐标
     */
    setPosition(x, y) {
        this.positionCoord = { x: parseInt(x) || 0, y: parseInt(y) || 0 };
    }

    /**
     * 判断是否为开始或结束节点
     */
    isStartOrEnd() {
        return this.position === 'START' || this.position === 'END';
    }

    /**
     * 判断是否为块活动
     */
    isBlockActivity() {
        return this.implementation === 'Block';
    }

    /**
     * 获取 RIGHT 属性值
     */
    getRightProperty(propertyName, defaultValue = '') {
        return this.RIGHT?.[propertyName] || defaultValue;
    }

    /**
     * 设置 RIGHT 属性值
     */
    setRightProperty(propertyName, value) {
        if (!this.RIGHT) {
            this.RIGHT = {};
        }
        this.RIGHT[propertyName] = value;
    }

    /**
     * 获取 FORM 属性值
     */
    getFormProperty(propertyName, defaultValue = '') {
        return this.FORM?.[propertyName] || defaultValue;
    }

    /**
     * 设置 FORM 属性值
     */
    setFormProperty(propertyName, value) {
        if (!this.FORM) {
            this.FORM = {};
        }
        this.FORM[propertyName] = value;
    }

    /**
     * 获取 SERVICE 属性值
     */
    getServiceProperty(propertyName, defaultValue = '') {
        return this.SERVICE?.[propertyName] || defaultValue;
    }

    /**
     * 设置 SERVICE 属性值
     */
    setServiceProperty(propertyName, value) {
        if (!this.SERVICE) {
            this.SERVICE = {};
        }
        this.SERVICE[propertyName] = value;
    }

    /**
     * 获取 WORKFLOW 属性值
     */
    getWorkflowProperty(propertyName, defaultValue = '') {
        return this.WORKFLOW?.[propertyName] || defaultValue;
    }

    /**
     * 设置 WORKFLOW 属性值
     */
    setWorkflowProperty(propertyName, value) {
        if (!this.WORKFLOW) {
            this.WORKFLOW = {};
        }
        this.WORKFLOW[propertyName] = value;
    }

    /**
     * 转换为 JSON (用于发送到后端)
     */
    toJSON() {
        const json = {
            // 基本属性
            activityDefId: this.activityDefId,
            name: this.name,
            description: this.description,
            position: this.position,
            activityType: this.activityType,
            
            // BPD属性
            positionCoord: this.positionCoord,
            participantId: this.participantId,
            implementation: this.implementation,
            
            // 时限配置
            limitTime: this.limitTime,
            alertTime: this.alertTime,
            durationUnit: this.durationUnit,
            
            // 流程控制
            join: this.join,
            split: this.split,
            
            // 退回配置
            canRouteBack: this.canRouteBack,
            routeBackMethod: this.routeBackMethod,
            
            // 特送配置
            canSpecialSend: this.canSpecialSend,
            specialScope: this.specialScope,
            
            // 属性组
            RIGHT: this._isEmptyObject(this.RIGHT) ? undefined : this.RIGHT,
            FORM: this._isEmptyObject(this.FORM) ? undefined : this.FORM,
            SERVICE: this._isEmptyObject(this.SERVICE) ? undefined : this.SERVICE,
            WORKFLOW: this._isEmptyObject(this.WORKFLOW) ? undefined : this.WORKFLOW,
            
            // 块活动属性
            startOfBlock: this.startOfBlock,
            endOfBlock: this.endOfBlock,
            participantVisualOrder: this.participantVisualOrder,
            
            // 扩展属性
            extendedAttributes: this._isEmptyObject(this.extendedAttributes) ? undefined : this.extendedAttributes
        };
        
        // 清理 undefined 值
        return this._cleanUndefined(json);
    }

    /**
     * 从 JSON 创建实例
     */
    static fromJSON(json) {
        return new ActivityDefNew(json);
    }

    /**
     * 创建开始节点
     */
    static createStart(x, y, name = '开始') {
        return new ActivityDefNew({
            activityType: 'START',
            position: 'START',
            name: name,
            positionCoord: { x, y },
            participantId: 'Participant_Start'
        });
    }

    /**
     * 创建结束节点
     */
    static createEnd(x, y, name = '结束') {
        return new ActivityDefNew({
            activityType: 'END',
            position: 'END',
            name: name,
            positionCoord: { x, y },
            participantId: 'Participant_End'
        });
    }

    /**
     * 创建普通任务节点
     */
    static createTask(x, y, name = '任务') {
        return new ActivityDefNew({
            activityType: 'TASK',
            position: 'NORMAL',
            name: name,
            positionCoord: { x, y },
            participantId: 'Participant_' + Date.now()
        });
    }

    /**
     * 检查对象是否为空
     */
    _isEmptyObject(obj) {
        return !obj || Object.keys(obj).length === 0;
    }

    /**
     * 清理 undefined 值
     */
    _cleanUndefined(obj) {
        const result = {};
        for (const [key, value] of Object.entries(obj)) {
            if (value !== undefined) {
                result[key] = value;
            }
        }
        return result;
    }

    /**
     * 克隆活动
     */
    clone() {
        const json = this.toJSON();
        json.activityDefId = this._generateId();
        json.name = json.name + '_复制';
        return new ActivityDefNew(json);
    }

    /**
     * 获取显示标签
     */
    getDisplayLabel() {
        if (this.isStartOrEnd()) {
            return this.name;
        }
        return `${this.name} (${this.activityType})`;
    }

    /**
     * 验证活动数据
     */
    validate() {
        const errors = [];
        
        if (!this.name || this.name.trim() === '') {
            errors.push('活动名称不能为空');
        }
        
        if (!this.activityDefId) {
            errors.push('活动ID不能为空');
        }
        
        // 验证 RIGHT 属性组
        if (this.RIGHT && this.RIGHT.performType) {
            const validPerformTypes = ['SINGLE', 'JOINTSIGN', 'COUNTERSIGN'];
            if (!validPerformTypes.includes(this.RIGHT.performType)) {
                errors.push(`无效的执行类型: ${this.RIGHT.performType}`);
            }
        }
        
        // 验证 FORM 属性组
        if (this.FORM && this.FORM.formType) {
            const validFormTypes = ['CUSTOM', 'SYSTEM', 'EXTERNAL'];
            if (!validFormTypes.includes(this.FORM.formType)) {
                errors.push(`无效的表单类型: ${this.FORM.formType}`);
            }
        }
        
        // 验证 SERVICE 属性组
        if (this.SERVICE && this.SERVICE.httpMethod) {
            const validMethods = ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'];
            if (!validMethods.includes(this.SERVICE.httpMethod)) {
                errors.push(`无效的HTTP方法: ${this.SERVICE.httpMethod}`);
            }
        }
        
        return errors;
    }

    /**
     * 检查是否有验证错误
     */
    isValid() {
        return this.validate().length === 0;
    }
}

window.ActivityDefNew = ActivityDefNew;
