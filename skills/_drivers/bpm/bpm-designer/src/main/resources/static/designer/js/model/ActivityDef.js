/**
 * ActivityDef - 活动定义模型 (已修复版本)
 * 支持完整的扩展属性组：RIGHT, FORM, SERVICE, WORKFLOW
 */

class ActivityDef {
    constructor(data) {
        // 基本属性
        this.activityDefId = data?.activityDefId || this._generateId();
        this.name = data?.name || '新活动';
        this.description = data?.description || '';
        
        // 活动类型和位置
        this.activityType = data?.activityType || 'TASK';
        this.position = this._normalizePosition(data?.position, this.activityType);
        
        // 活动分类 - 重要：直接存储在活动对象上
        this.activityCategory = data?.activityCategory || 'HUMAN';
        
        // 坐标和参与者 (BPD属性) - 修复：添加participantId
        this.positionCoord = this._normalizeCoord(data?.positionCoord);
        this.participantId = data?.participantId || '';
        
        // 实现方式
        this.implementation = data?.implementation || 'No';
        
        // 时限配置
        this.limitTime = data?.limitTime || data?.limit || 0;
        this.limit = this.limitTime;  // 前端使用limit
        this.alertTime = data?.alertTime || 0;
        this.durationUnit = data?.durationUnit || 'D';
        
        // 流程控制
        this.join = data?.join || 'XOR';
        this.split = data?.split || 'XOR';
        
        // 退回配置 - 修复：支持String和Boolean
        this.canRouteBack = this._normalizeYesNo(data?.canRouteBack);
        this.routeBackMethod = data?.routeBackMethod || 'PREV';
        
        // 特送配置 - 修复：支持String和Boolean
        this.canSpecialSend = this._normalizeYesNo(data?.canSpecialSend);
        this.specialScope = data?.specialScope || data?.specialSendScope || 'ALL';
        this.specialSendScope = this.specialScope;  // 前端使用specialSendScope
        
        // ==================== 属性组 - 修复：完整支持 ====================
        // RIGHT 属性组
        this.RIGHT = this._buildRightGroup(data);
        
        // FORM 属性组 - 修复：新增
        this.FORM = this._buildFormGroup(data);
        
        // SERVICE 属性组 - 修复：新增
        this.SERVICE = this._buildServiceGroup(data);
        
        // WORKFLOW 属性组 - 修复：新增
        this.WORKFLOW = this._buildWorkflowGroup(data);
        
        // 块活动属性 (BPD) - 修复：新增
        this.startOfBlock = data?.startOfBlock || null;
        this.endOfBlock = data?.endOfBlock || null;
        this.participantVisualOrder = data?.participantVisualOrder || '';
        
        // Agent配置 - 直接存储在活动对象上
        this.agentConfig = data?.agentConfig || null;
        
        // Scene配置 - 直接存储在活动对象上
        this.sceneConfig = data?.sceneConfig || null;
        
        // 其他扩展属性
        this.extendedAttributes = data?.extendedAttributes || {};
        
        // 前端特有字段（放入extendedAttributes）
        this._handleExtendedFields(data);
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
        if (activityType === 'START') return 'START';
        if (activityType === 'END') return 'END';
        
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
     * 标准化YES/NO值（支持Boolean和String）
     */
    _normalizeYesNo(value) {
        if (typeof value === 'boolean') {
            return value ? 'YES' : 'NO';
        }
        if (typeof value === 'string') {
            return value.toUpperCase() === 'YES' ? 'YES' : 'NO';
        }
        return 'NO';
    }

    /**
     * 构建RIGHT属性组 - 修复：完整支持所有字段
     */
    _buildRightGroup(data) {
        const right = {};
        
        // 从data.RIGHT提取
        if (data?.RIGHT && typeof data.RIGHT === 'object') {
            Object.assign(right, data.RIGHT);
        }
        
        // 从扁平结构提取（兼容旧数据）
        const flatFields = [
            'performType', 'performSequence', 'specialSendScope',
            'canInsteadSign', 'canTakeBack', 'canReSend',
            'insteadSignSelected', 'performerSelectedId', 'readerSelectedId',
            'movePerformerTo', 'moveSponsorTo', 'moveReaderTo',
            'surrogateId', 'surrogateName'
        ];
        
        flatFields.forEach(field => {
            if (data?.[field] !== undefined && data[field] !== null && data[field] !== '') {
                // Boolean转String YES/NO
                if (['canInsteadSign', 'canTakeBack', 'canReSend'].includes(field)) {
                    right[field] = this._normalizeYesNo(data[field]);
                } else {
                    right[field] = data[field];
                }
            }
        });
        
        // 处理performerSelectedAtt (Array -> String)
        if (data?.performerSelectedAtt && Array.isArray(data.performerSelectedAtt) && data.performerSelectedAtt.length > 0) {
            right.performerSelectedId = data.performerSelectedAtt[0];
        }
        
        return Object.keys(right).length > 0 ? right : undefined;
    }

    /**
     * 构建FORM属性组 - 修复：新增
     */
    _buildFormGroup(data) {
        const form = {};
        
        // 从data.FORM提取
        if (data?.FORM && typeof data.FORM === 'object') {
            Object.assign(form, data.FORM);
        }
        
        // 从扁平结构提取
        const fields = ['formId', 'formName', 'formType', 'formUrl'];
        fields.forEach(field => {
            if (data?.[field] !== undefined && data[field] !== null && data[field] !== '') {
                form[field] = data[field];
            }
        });
        
        return Object.keys(form).length > 0 ? form : undefined;
    }

    /**
     * 构建SERVICE属性组 - 修复：新增
     */
    _buildServiceGroup(data) {
        const service = {};
        
        // 从data.SERVICE提取
        if (data?.SERVICE && typeof data.SERVICE === 'object') {
            Object.assign(service, data.SERVICE);
        }
        
        // 从扁平结构提取
        const fields = [
            'httpMethod', 'httpUrl', 'httpRequestType',
            'httpResponseType', 'httpServiceParams', 'serviceSelectedId'
        ];
        fields.forEach(field => {
            if (data?.[field] !== undefined && data[field] !== null && data[field] !== '') {
                service[field] = data[field];
            }
        });
        
        return Object.keys(service).length > 0 ? service : undefined;
    }

    /**
     * 构建WORKFLOW属性组 - 修复：新增
     */
    _buildWorkflowGroup(data) {
        const workflow = {};
        
        // 从data.WORKFLOW提取
        if (data?.WORKFLOW && typeof data.WORKFLOW === 'object') {
            Object.assign(workflow, data.WORKFLOW);
        }
        
        // 从扁平结构提取（处理大小写不同）
        if (data?.deadLineOperation || data?.deadlineOperation) {
            workflow.deadLineOperation = data.deadLineOperation || data.deadlineOperation;
        }
        if (data?.specialScope || data?.specialSendScope) {
            workflow.specialScope = data.specialScope || data.specialSendScope;
        }
        
        return Object.keys(workflow).length > 0 ? workflow : undefined;
    }

    /**
     * 处理扩展字段
     */
    _handleExtendedFields(data) {
        const extendedFields = [
            'performerType', 'listeners'
        ];
        
        extendedFields.forEach(field => {
            if (data?.[field] !== undefined) {
                this.extendedAttributes[field] = data[field];
            }
        });
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
     * 获取RIGHT属性值
     */
    getRightProperty(propertyName, defaultValue = '') {
        return this.RIGHT?.[propertyName] || defaultValue;
    }

    /**
     * 设置RIGHT属性值
     */
    setRightProperty(propertyName, value) {
        if (!this.RIGHT) {
            this.RIGHT = {};
        }
        this.RIGHT[propertyName] = value;
    }

    /**
     * 获取FORM属性值
     */
    getFormProperty(propertyName, defaultValue = '') {
        return this.FORM?.[propertyName] || defaultValue;
    }

    /**
     * 设置FORM属性值
     */
    setFormProperty(propertyName, value) {
        if (!this.FORM) {
            this.FORM = {};
        }
        this.FORM[propertyName] = value;
    }

    /**
     * 获取SERVICE属性值
     */
    getServiceProperty(propertyName, defaultValue = '') {
        return this.SERVICE?.[propertyName] || defaultValue;
    }

    /**
     * 设置SERVICE属性值
     */
    setServiceProperty(propertyName, value) {
        if (!this.SERVICE) {
            this.SERVICE = {};
        }
        this.SERVICE[propertyName] = value;
    }

    /**
     * 获取WORKFLOW属性值
     */
    getWorkflowProperty(propertyName, defaultValue = '') {
        return this.WORKFLOW?.[propertyName] || defaultValue;
    }

    /**
     * 设置WORKFLOW属性值
     */
    setWorkflowProperty(propertyName, value) {
        if (!this.WORKFLOW) {
            this.WORKFLOW = {};
        }
        this.WORKFLOW[propertyName] = value;
    }

    /**
     * 转换为JSON (用于发送到后端) - 修复：完整输出所有属性，添加错误处理
     */
    toJSON() {
        try {
            const json = {
                // 基本属性
                activityDefId: this.activityDefId,
                name: this.name,
                description: this.description,
                position: this.position,
                activityType: this.activityType,
                activityCategory: this.activityCategory,
                
                // BPD属性 - 修复：添加participantId
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
                
                // 属性组 - 修复：完整输出
                RIGHT: this.RIGHT,
                FORM: this.FORM,
                SERVICE: this.SERVICE,
                WORKFLOW: this.WORKFLOW,
                
                // 块活动属性 - 修复：添加
                startOfBlock: this.startOfBlock,
                endOfBlock: this.endOfBlock,
                participantVisualOrder: this.participantVisualOrder,
                
                // Agent/Scene 配置 - 修复：添加到序列化
                agentConfig: this.agentConfig,
                sceneConfig: this.sceneConfig,
                
                // 扩展属性
                extendedAttributes: this._isEmptyObject(this.extendedAttributes) ? undefined : this.extendedAttributes
            };
            
            // 清理undefined值
            return this._cleanUndefined(json);
        } catch (error) {
            console.error('[ActivityDef.toJSON] Error:', error, this);
            // 返回最小化的对象
            return {
                activityDefId: this.activityDefId || 'unknown',
                name: this.name || '未知活动',
                activityType: this.activityType || 'TASK'
            };
        }
    }

    /**
     * 从JSON创建实例 - 修复：支持完整属性
     */
    static fromJSON(json) {
        return new ActivityDef(json);
    }

    /**
     * 创建开始节点
     */
    static createStart(x, y, name = '开始') {
        return new ActivityDef({
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
        return new ActivityDef({
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
        return new ActivityDef({
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
     * 清理undefined值
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
        return new ActivityDef(json);
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
        
        // 验证RIGHT属性组
        if (this.RIGHT && this.RIGHT.performType) {
            const validPerformTypes = ['SINGLE', 'JOINTSIGN', 'COUNTERSIGN'];
            if (!validPerformTypes.includes(this.RIGHT.performType)) {
                errors.push(`无效的执行类型: ${this.RIGHT.performType}`);
            }
        }
        
        // 验证FORM属性组
        if (this.FORM && this.FORM.formType) {
            const validFormTypes = ['CUSTOM', 'SYSTEM', 'EXTERNAL'];
            if (!validFormTypes.includes(this.FORM.formType)) {
                errors.push(`无效的表单类型: ${this.FORM.formType}`);
            }
        }
        
        // 验证SERVICE属性组
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

// 保持向后兼容
window.ActivityDef = ActivityDef;
