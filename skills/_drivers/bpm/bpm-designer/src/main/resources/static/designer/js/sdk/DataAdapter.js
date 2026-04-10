/**
 * 数据适配器 - 全新实现
 * 负责前端数据与后端数据格式的双向转换
 * 确保所有属性正确映射，不丢失任何数据
 */

class DataAdapter {
    constructor() {
        // 转换统计信息
        this.stats = {
            toBackend: { converted: [], ignored: [], errors: [] },
            fromBackend: { converted: [], ignored: [], errors: [] }
        };
        
        // 属性映射表
        this.mappings = this._initMappings();
    }

    /**
     * 初始化属性映射表
     */
    _initMappings() {
        return {
            // ProcessDef 级别映射
            processDef: {
                // 直接映射 (字段名相同)
                direct: [
                    'processDefId', 'name', 'description', 'version',
                    'creatorName', 'modifierId', 'modifierName',
                    'modifyTime', 'createTime', 'activeTime', 'freezeTime',
                    'limit', 'durationUnit'
                ],
                // 字段名不同
                rename: {
                    'status': 'state',           // 前端status -> 后端state
                    'category': 'classification' // 前端category -> 后端classification
                },
                // 前端有但后端可能不识别 (需要放入extendedAttributes)
                extended: [
                    'agentConfig', 'sceneConfig', 'formulas', 
                    'parameters', 'activitySets', 'subProcessRefs',
                    'createdTime', 'updatedTime'
                ],
                // 特殊处理 (复杂对象)
                special: ['startNode', 'endNodes', 'listeners', 'rightGroups', 'activities', 'routes']
            },
            
            // ActivityDef 级别映射
            activityDef: {
                // 直接映射
                direct: [
                    'activityDefId', 'name', 'description',
                    'activityType', 'position', 'positionCoord',
                    'limitTime', 'alertTime', 'durationUnit',
                    'join', 'split'
                ],
                // 字段名不同
                rename: {
                    'limit': 'limitTime',  // 前端limit -> 后端limitTime
                    'deadlineOperation': 'deadLineOperation'  // 大小写不同
                },
                // 数据类型转换 (Boolean -> String YES/NO)
                typeConvert: {
                    'canRouteBack': { type: 'booleanToString', values: { true: 'YES', false: 'NO' } },
                    'canSpecialSend': { type: 'booleanToString', values: { true: 'YES', false: 'NO' } },
                    'canInsteadSign': { type: 'booleanToString', values: { true: 'YES', false: 'NO' } },
                    'canTakeBack': { type: 'booleanToString', values: { true: 'YES', false: 'NO' } },
                    'canReSend': { type: 'booleanToString', values: { true: 'YES', false: 'NO' } }
                },
                // 需要放入属性组的字段
                toAttributeGroups: {
                    'RIGHT': [
                        'performType', 'performSequence', 'specialSendScope',
                        'canInsteadSign', 'canTakeBack', 'canReSend',
                        'insteadSignSelected', 'performerSelectedId', 'readerSelectedId',
                        'movePerformerTo', 'moveSponsorTo', 'moveReaderTo',
                        'surrogateId', 'surrogateName'
                    ],
                    'FORM': ['formId', 'formName', 'formType', 'formUrl'],
                    'SERVICE': [
                        'httpMethod', 'httpUrl', 'httpRequestType',
                        'httpResponseType', 'httpServiceParams', 'serviceSelectedId'
                    ],
                    'WORKFLOW': ['deadLineOperation', 'specialScope']
                },
                // 前端有但后端可能不识别的字段
                extended: [
                    'activityCategory', 'performerType', 'implementation',
                    'agentConfig', 'sceneConfig', 'extendedAttributes'
                ],
                // 特殊字段
                special: ['participantId', 'startOfBlock', 'endOfBlock', 'participantVisualOrder']
            }
        };
    }

    /**
     * 前端数据 -> 后端格式
     */
    toBackend(frontendData) {
        this.stats.toBackend = { converted: [], ignored: [], errors: [] };
        
        try {
            const result = {
                // 基本属性
                processDefId: frontendData.processDefId,
                name: frontendData.name,
                description: frontendData.description || '',
                classification: frontendData.category || frontendData.classification || '办公流程',
                systemCode: frontendData.systemCode || 'bpm',
                accessLevel: frontendData.accessLevel || 'PUBLIC',
                
                // 版本信息
                version: frontendData.version || 1,
                state: this._convertStatus(frontendData.status || frontendData.state),
                creatorName: frontendData.creatorName || '',
                modifierId: frontendData.modifierId || '',
                modifierName: frontendData.modifierName || '',
                modifyTime: frontendData.modifyTime || null,
                
                // 时限配置
                limit: frontendData.limit || 0,
                durationUnit: frontendData.durationUnit || 'D',
                
                // 开始/结束节点 (从活动中提取或直接使用)
                startNode: this._extractStartNode(frontendData),
                endNodes: this._extractEndNodes(frontendData),
                
                // 监听器和权限组
                listeners: this._convertListeners(frontendData.listeners),
                rightGroups: this._convertRightGroups(frontendData.rightGroups),
                
                // 活动列表
                activities: (frontendData.activities || []).map(a => this._convertActivityToBackend(a)),
                
                // 路由列表
                routes: (frontendData.routes || []).map(r => this._convertRouteToBackend(r)),
                
                // 扩展属性 (前端特有但后端不识别的字段)
                extendedAttributes: this._extractExtendedAttributes(frontendData)
            };
            
            // 记录转换统计
            this._logConversion('toBackend', 'processDef', frontendData, result);
            
            return result;
        } catch (error) {
            this.stats.toBackend.errors.push({
                type: 'conversion_error',
                message: error.message,
                stack: error.stack
            });
            throw error;
        }
    }

    /**
     * 后端数据 -> 前端格式
     */
    fromBackend(backendData) {
        this.stats.fromBackend = { converted: [], ignored: [], errors: [] };
        
        try {
            const result = {
                // 基本属性
                processDefId: backendData.processDefId || backendData.processDef?.processDefId,
                name: backendData.name || backendData.processDef?.name,
                description: backendData.description || backendData.processDef?.description,
                category: backendData.classification || backendData.processDef?.classification,
                classification: backendData.classification || backendData.processDef?.classification,
                systemCode: backendData.systemCode || backendData.processDef?.systemCode,
                accessLevel: backendData.accessLevel || backendData.processDef?.accessLevel,
                
                // 版本信息
                version: backendData.version || backendData.activeVersion?.version,
                status: this._convertState(backendData.state || backendData.activeVersion?.state),
                state: backendData.state || backendData.activeVersion?.state,
                creatorName: backendData.creatorName || backendData.activeVersion?.creatorName,
                modifierId: backendData.modifierId || backendData.activeVersion?.modifierId,
                modifierName: backendData.modifierName || backendData.activeVersion?.modifierName,
                modifyTime: backendData.modifyTime || backendData.activeVersion?.modifyTime,
                createTime: backendData.createTime || backendData.activeVersion?.createTime,
                activeTime: backendData.activeTime || backendData.activeVersion?.activeTime,
                freezeTime: backendData.freezeTime || backendData.activeVersion?.freezeTime,
                
                // 时限配置
                limit: backendData.limit || backendData.activeVersion?.limit || 0,
                durationUnit: backendData.durationUnit || backendData.activeVersion?.durationUnit || 'D',
                
                // 开始/结束节点
                startNode: backendData.startNode,
                endNodes: backendData.endNodes || [],
                
                // 监听器和权限组
                listeners: backendData.listeners || [],
                rightGroups: backendData.rightGroups || [],
                
                // 活动列表 (转换属性组为扁平结构)
                activities: (backendData.activities || []).map(a => this._convertActivityFromBackend(a)),
                
                // 路由列表
                routes: (backendData.routes || []).map(r => this._convertRouteFromBackend(r)),
                
                // 保留扩展属性
                extendedAttributes: backendData.extendedAttributes || {}
            };
            
            // 记录转换统计
            this._logConversion('fromBackend', 'processDef', backendData, result);
            
            return result;
        } catch (error) {
            this.stats.fromBackend.errors.push({
                type: 'conversion_error',
                message: error.message,
                stack: error.stack
            });
            throw error;
        }
    }

    /**
     * 转换活动数据 -> 后端格式
     */
    _convertActivityToBackend(activity) {
        const result = {
            // 基本属性
            activityDefId: activity.activityDefId,
            name: activity.name,
            description: activity.description || '',
            position: activity.position || 'NORMAL',
            activityType: activity.activityType || 'TASK',
            
            // BPD属性
            positionCoord: activity.positionCoord || { x: 0, y: 0 },
            participantId: activity.participantId || '',
            implementation: activity.implementation || 'No',
            
            // 时限配置
            limitTime: activity.limitTime || activity.limit || 0,
            alertTime: activity.alertTime || 0,
            durationUnit: activity.durationUnit || 'D',
            
            // 流程控制
            join: activity.join || 'XOR',
            split: activity.split || 'XOR',
            
            // 退回配置 (Boolean -> String)
            canRouteBack: this._booleanToYesNo(activity.canRouteBack),
            routeBackMethod: activity.routeBackMethod || 'PREV',
            
            // 特送配置
            canSpecialSend: this._booleanToYesNo(activity.canSpecialSend),
            specialScope: activity.specialScope || activity.specialSendScope || 'ALL',
            
            // 属性组
            RIGHT: this._buildRightGroup(activity),
            FORM: this._buildFormGroup(activity),
            SERVICE: this._buildServiceGroup(activity),
            WORKFLOW: this._buildWorkflowGroup(activity),
            
            // 块活动属性
            startOfBlock: activity.startOfBlock || null,
            endOfBlock: activity.endOfBlock || null,
            participantVisualOrder: activity.participantVisualOrder || ''
        };
        
        // 清理 undefined 值
        Object.keys(result).forEach(key => {
            if (result[key] === undefined || result[key] === null) {
                delete result[key];
            }
        });
        
        // 清理空属性组
        ['RIGHT', 'FORM', 'SERVICE', 'WORKFLOW'].forEach(group => {
            if (result[group] && Object.keys(result[group]).length === 0) {
                delete result[group];
            }
        });
        
        return result;
    }

    /**
     * 转换活动数据 <- 后端格式
     */
    _convertActivityFromBackend(activity) {
        const result = {
            // 基本属性
            activityDefId: activity.activityDefId,
            name: activity.name,
            description: activity.description,
            position: activity.position,
            activityType: activity.activityType,
            
            // BPD属性
            positionCoord: activity.positionCoord,
            participantId: activity.participantId,
            implementation: activity.implementation,
            
            // 时限配置
            limitTime: activity.limitTime,
            limit: activity.limitTime,  // 前端使用limit
            alertTime: activity.alertTime,
            durationUnit: activity.durationUnit,
            
            // 流程控制
            join: activity.join,
            split: activity.split,
            
            // 退回配置 (String -> Boolean)
            canRouteBack: this._yesNoToBoolean(activity.canRouteBack),
            routeBackMethod: activity.routeBackMethod,
            
            // 特送配置
            canSpecialSend: this._yesNoToBoolean(activity.canSpecialSend),
            specialScope: activity.specialScope,
            specialSendScope: activity.specialScope,  // 前端使用specialSendScope
            
            // 从属性组提取扁平属性
            ...this._extractRightGroup(activity.RIGHT),
            ...this._extractFormGroup(activity.FORM),
            ...this._extractServiceGroup(activity.SERVICE),
            ...this._extractWorkflowGroup(activity.WORKFLOW),
            
            // 块活动属性
            startOfBlock: activity.startOfBlock,
            endOfBlock: activity.endOfBlock,
            participantVisualOrder: activity.participantVisualOrder
        };
        
        // 清理 undefined 值
        Object.keys(result).forEach(key => {
            if (result[key] === undefined) {
                delete result[key];
            }
        });
        
        return result;
    }

    /**
     * 构建 RIGHT 属性组
     */
    _buildRightGroup(activity) {
        const right = {};
        const fields = [
            'performType', 'performSequence', 'specialSendScope',
            'canInsteadSign', 'canTakeBack', 'canReSend',
            'insteadSignSelected', 'performerSelectedId', 'readerSelectedId',
            'movePerformerTo', 'moveSponsorTo', 'moveReaderTo',
            'surrogateId', 'surrogateName'
        ];
        
        fields.forEach(field => {
            if (activity[field] !== undefined && activity[field] !== null && activity[field] !== '') {
                // Boolean 转 String YES/NO
                if (['canInsteadSign', 'canTakeBack', 'canReSend'].includes(field)) {
                    right[field] = this._booleanToYesNo(activity[field]);
                } else {
                    right[field] = activity[field];
                }
            }
        });
        
        // 处理 performerSelectedAtt (Array -> String)
        if (activity.performerSelectedAtt && activity.performerSelectedAtt.length > 0) {
            right.performerSelectedId = activity.performerSelectedAtt[0];
        }
        
        return Object.keys(right).length > 0 ? right : undefined;
    }

    /**
     * 提取 RIGHT 属性组到扁平结构
     */
    _extractRightGroup(right) {
        if (!right) return {};
        
        const result = {};
        Object.keys(right).forEach(key => {
            // String YES/NO 转 Boolean
            if (['canInsteadSign', 'canTakeBack', 'canReSend'].includes(key)) {
                result[key] = this._yesNoToBoolean(right[key]);
            } else {
                result[key] = right[key];
            }
        });
        
        return result;
    }

    /**
     * 构建 FORM 属性组
     */
    _buildFormGroup(activity) {
        const form = {};
        const fields = ['formId', 'formName', 'formType', 'formUrl'];
        
        fields.forEach(field => {
            if (activity[field] !== undefined && activity[field] !== null && activity[field] !== '') {
                form[field] = activity[field];
            }
        });
        
        return Object.keys(form).length > 0 ? form : undefined;
    }

    /**
     * 提取 FORM 属性组到扁平结构
     */
    _extractFormGroup(form) {
        return form || {};
    }

    /**
     * 构建 SERVICE 属性组
     */
    _buildServiceGroup(activity) {
        const service = {};
        const fields = [
            'httpMethod', 'httpUrl', 'httpRequestType',
            'httpResponseType', 'httpServiceParams', 'serviceSelectedId'
        ];
        
        fields.forEach(field => {
            if (activity[field] !== undefined && activity[field] !== null && activity[field] !== '') {
                service[field] = activity[field];
            }
        });
        
        return Object.keys(service).length > 0 ? service : undefined;
    }

    /**
     * 提取 SERVICE 属性组到扁平结构
     */
    _extractServiceGroup(service) {
        return service || {};
    }

    /**
     * 构建 WORKFLOW 属性组
     */
    _buildWorkflowGroup(activity) {
        const workflow = {};
        
        if (activity.deadLineOperation || activity.deadlineOperation) {
            workflow.deadLineOperation = activity.deadLineOperation || activity.deadlineOperation;
        }
        if (activity.specialScope || activity.specialSendScope) {
            workflow.specialScope = activity.specialScope || activity.specialSendScope;
        }
        
        return Object.keys(workflow).length > 0 ? workflow : undefined;
    }

    /**
     * 提取 WORKFLOW 属性组到扁平结构
     */
    _extractWorkflowGroup(workflow) {
        if (!workflow) return {};
        
        return {
            deadLineOperation: workflow.deadLineOperation,
            deadlineOperation: workflow.deadLineOperation,  // 前端使用deadlineOperation
            specialScope: workflow.specialScope,
            specialSendScope: workflow.specialScope
        };
    }

    /**
     * 提取开始节点
     */
    _extractStartNode(frontendData) {
        // 如果前端已有startNode，直接使用
        if (frontendData.startNode) {
            return frontendData.startNode;
        }
        
        // 从活动中查找开始节点
        const startActivity = frontendData.activities?.find(a => 
            a.position === 'START' || a.activityType === 'START'
        );
        
        if (startActivity) {
            return {
                participantId: startActivity.participantId || 'Participant_Start',
                firstActivityId: startActivity.activityDefId,
                positionCoord: startActivity.positionCoord || { x: 50, y: 200 },
                routing: 'NO_ROUTING'
            };
        }
        
        return null;
    }

    /**
     * 提取结束节点
     */
    _extractEndNodes(frontendData) {
        // 如果前端已有endNodes，直接使用
        if (frontendData.endNodes && frontendData.endNodes.length > 0) {
            return frontendData.endNodes;
        }
        
        // 从活动中查找结束节点
        const endActivities = frontendData.activities?.filter(a => 
            a.position === 'END' || a.activityType === 'END'
        ) || [];
        
        return endActivities.map(act => ({
            participantId: act.participantId || 'Participant_End',
            lastActivityId: act.activityDefId,
            positionCoord: act.positionCoord || { x: 800, y: 200 },
            routing: 'NO_ROUTING'
        }));
    }

    /**
     * 转换监听器
     */
    _convertListeners(listeners) {
        if (!listeners || listeners.length === 0) return [];
        
        return listeners.map(l => ({
            id: l.id || l.listenerId || `listener_${Date.now()}_${Math.random().toString(36).substr(2, 5)}`,
            name: l.name || l.listenerName || '',
            event: l.event || l.listenerEvent || 'PROCESS_START',
            realizeClass: l.realizeClass || l.className || ''
        }));
    }

    /**
     * 转换权限组
     */
    _convertRightGroups(rightGroups) {
        if (!rightGroups || rightGroups.length === 0) return [];
        
        return rightGroups.map((rg, index) => ({
            id: rg.id || rg.rightGroupId || `rg_${Date.now()}_${index}`,
            name: rg.name || rg.rightGroupName || '',
            code: rg.code || rg.rightGroupCode || '',
            order: rg.order || index + 1,
            defaultGroup: rg.defaultGroup || rg.isDefault || false
        }));
    }

    /**
     * 转换路由 -> 后端格式
     */
    _convertRouteToBackend(route) {
        return {
            routeDefId: route.routeDefId || route.id || `route_${Date.now()}_${Math.random().toString(36).substr(2, 5)}`,
            name: route.name || '',
            description: route.description || '',
            fromActivityDefId: route.from || route.fromActivityDefId || route.fromActivityId,
            toActivityDefId: route.to || route.toActivityDefId || route.toActivityId,
            routeOrder: route.routeOrder || route.order || 1,
            routeDirection: route.routeDirection || route.direction || 'FORWARD',
            routeCondition: route.routeCondition || route.condition || '',
            routeConditionType: route.routeConditionType || route.conditionType || 'DEFAULT',
            routing: route.routing || ''
        };
    }

    /**
     * 转换路由 <- 后端格式
     */
    _convertRouteFromBackend(route) {
        return {
            routeDefId: route.routeDefId,
            id: route.routeDefId,
            name: route.name,
            description: route.description,
            from: route.fromActivityDefId,
            fromActivityDefId: route.fromActivityDefId,
            to: route.toActivityDefId,
            toActivityDefId: route.toActivityDefId,
            order: route.routeOrder,
            routeOrder: route.routeOrder,
            direction: route.routeDirection,
            routeDirection: route.routeDirection,
            condition: route.routeCondition,
            routeCondition: route.routeCondition,
            conditionType: route.routeConditionType,
            routeConditionType: route.routeConditionType,
            routing: route.routing
        };
    }

    /**
     * 提取扩展属性
     */
    _extractExtendedAttributes(frontendData) {
        const extended = {};
        const extendedFields = [
            'agentConfig', 'sceneConfig', 'formulas', 
            'parameters', 'activitySets', 'subProcessRefs'
        ];
        
        extendedFields.forEach(field => {
            if (frontendData[field] !== undefined && frontendData[field] !== null) {
                extended[field] = frontendData[field];
            }
        });
        
        return Object.keys(extended).length > 0 ? extended : undefined;
    }

    /**
     * 转换状态值
     */
    _convertStatus(status) {
        const mapping = {
            'DRAFT': 'DRAFT',
            'ACTIVE': 'ACTIVE',
            'FROZEN': 'FROZEN',
            'ARCHIVED': 'ARCHIVED'
        };
        return mapping[status] || status || 'DRAFT';
    }

    /**
     * 反向转换状态值
     */
    _convertState(state) {
        return state || 'DRAFT';
    }

    /**
     * Boolean -> YES/NO
     */
    _booleanToYesNo(value) {
        if (typeof value === 'boolean') {
            return value ? 'YES' : 'NO';
        }
        if (typeof value === 'string') {
            return value.toUpperCase() === 'YES' ? 'YES' : 'NO';
        }
        return 'NO';
    }

    /**
     * YES/NO -> Boolean
     */
    _yesNoToBoolean(value) {
        if (typeof value === 'string') {
            return value.toUpperCase() === 'YES';
        }
        return !!value;
    }

    /**
     * 记录转换日志
     */
    _logConversion(direction, type, source, result) {
        const stats = this.stats[direction];
        
        // 记录转换的字段
        Object.keys(result).forEach(key => {
            if (result[key] !== undefined && result[key] !== null) {
                stats.converted.push({
                    field: key,
                    type: typeof result[key],
                    hasValue: true
                });
            }
        });
        
        // 记录被忽略的字段
        Object.keys(source).forEach(key => {
            if (result[key] === undefined && !key.startsWith('_')) {
                stats.ignored.push({
                    field: key,
                    value: source[key]
                });
            }
        });
    }

    /**
     * 获取转换统计
     */
    getStats() {
        return {
            toBackend: {
                ...this.stats.toBackend,
                convertedCount: this.stats.toBackend.converted.length,
                ignoredCount: this.stats.toBackend.ignored.length,
                errorCount: this.stats.toBackend.errors.length
            },
            fromBackend: {
                ...this.stats.fromBackend,
                convertedCount: this.stats.fromBackend.converted.length,
                ignoredCount: this.stats.fromBackend.ignored.length,
                errorCount: this.stats.fromBackend.errors.length
            }
        };
    }

    /**
     * 重置统计
     */
    resetStats() {
        this.stats = {
            toBackend: { converted: [], ignored: [], errors: [] },
            fromBackend: { converted: [], ignored: [], errors: [] }
        };
    }

    /**
     * 验证数据完整性
     */
    validateData(data, direction) {
        const errors = [];
        
        if (direction === 'toBackend') {
            // 验证必填字段
            if (!data.processDefId) errors.push('processDefId 不能为空');
            if (!data.name) errors.push('name 不能为空');
            if (!data.activities || data.activities.length === 0) {
                errors.push('activities 不能为空');
            }
            
            // 验证活动
            data.activities?.forEach((act, index) => {
                if (!act.activityDefId) errors.push(`activities[${index}].activityDefId 不能为空`);
                if (!act.name) errors.push(`activities[${index}].name 不能为空`);
            });
        }
        
        return errors;
    }
}

// 创建全局实例
window.DataAdapter = DataAdapter;
window.dataAdapter = new DataAdapter();
