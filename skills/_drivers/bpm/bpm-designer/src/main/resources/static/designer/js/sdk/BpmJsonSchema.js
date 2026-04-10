/**
 * BPM JSON Schema 定义
 * 严格定义前后端数据交换格式，包含完整的校验规则
 */

const BpmJsonSchema = {
    // ==================== 流程定义 Schema ====================
    ProcessDef: {
        type: 'object',
        required: ['processDefId', 'name', 'activities', 'routes'],
        properties: {
            // 基本信息
            processDefId: { type: 'string', pattern: '^[a-zA-Z0-9_-]+$', minLength: 1, maxLength: 64 },
            name: { type: 'string', minLength: 1, maxLength: 128 },
            description: { type: 'string', maxLength: 512 },
            classification: { type: 'string', enum: ['办公流程', '业务流程', '系统流程', '测试流程'] },
            systemCode: { type: 'string', default: 'bpm' },
            accessLevel: { type: 'string', enum: ['PUBLIC', 'PRIVATE', 'BLOCK'] },
            
            // 版本信息 (BPD属性)
            version: { type: 'integer', minimum: 1 },
            state: { type: 'string', enum: ['DRAFT', 'ACTIVE', 'FROZEN', 'ARCHIVED'] },
            creatorName: { type: 'string', maxLength: 64 },
            modifierId: { type: 'string', maxLength: 64 },
            modifierName: { type: 'string', maxLength: 64 },
            modifyTime: { type: 'string', format: 'date-time' },
            createTime: { type: 'string', format: 'date-time' },
            activeTime: { type: 'string', format: 'date-time' },
            freezeTime: { type: 'string', format: 'date-time' },
            
            // 时限配置
            limit: { type: 'integer', minimum: 0 },
            durationUnit: { type: 'string', enum: ['M', 'H', 'D', 'W'] },
            
            // 开始节点 (XPDL格式)
            startNode: {
                type: 'object',
                required: ['participantId', 'firstActivityId', 'positionCoord'],
                properties: {
                    participantId: { type: 'string', pattern: '^Participant_[a-zA-Z0-9_]+$', maxLength: 64 },
                    firstActivityId: { type: 'string', pattern: '^act_[a-zA-Z0-9_]+$', maxLength: 64 },
                    positionCoord: {
                        type: 'object',
                        required: ['x', 'y'],
                        properties: {
                            x: { type: 'integer', minimum: 0, maximum: 10000 },
                            y: { type: 'integer', minimum: 0, maximum: 10000 }
                        }
                    },
                    routing: { type: 'string', enum: ['NO_ROUTING', 'SIMPLE_ROUTING', 'CONDITIONAL_ROUTING'], default: 'NO_ROUTING' }
                }
            },
            
            // 结束节点列表 (XPDL格式，支持多个)
            endNodes: {
                type: 'array',
                items: {
                    type: 'object',
                    required: ['participantId', 'lastActivityId', 'positionCoord'],
                    properties: {
                        participantId: { type: 'string', pattern: '^Participant_[a-zA-Z0-9_]+$', maxLength: 64 },
                        lastActivityId: { type: 'string', pattern: '^act_[a-zA-Z0-9_]+$', maxLength: 64 },
                        positionCoord: {
                            type: 'object',
                            required: ['x', 'y'],
                            properties: {
                                x: { type: 'integer', minimum: 0, maximum: 10000 },
                                y: { type: 'integer', minimum: 0, maximum: 10000 }
                            }
                        },
                        routing: { type: 'string', enum: ['NO_ROUTING', 'SIMPLE_ROUTING'], default: 'NO_ROUTING' }
                    }
                }
            },
            
            // 监听器列表
            listeners: {
                type: 'array',
                items: {
                    type: 'object',
                    required: ['id', 'name', 'event', 'realizeClass'],
                    properties: {
                        id: { type: 'string', pattern: '^listener_[a-zA-Z0-9_]+$', maxLength: 64 },
                        name: { type: 'string', minLength: 1, maxLength: 128 },
                        event: { 
                            type: 'string', 
                            enum: ['PROCESS_START', 'PROCESS_END', 'ACTIVITY_START', 'ACTIVITY_END', 'ROUTE_TAKE', 'ASSIGNMENT']
                        },
                        realizeClass: { type: 'string', pattern: '^[a-zA-Z0-9_.]+$', maxLength: 256 }
                    }
                }
            },
            
            // 权限组列表
            rightGroups: {
                type: 'array',
                items: {
                    type: 'object',
                    required: ['id', 'name', 'code', 'order'],
                    properties: {
                        id: { type: 'string', pattern: '^rg_[a-zA-Z0-9_]+$', maxLength: 64 },
                        name: { type: 'string', minLength: 1, maxLength: 64 },
                        code: { type: 'string', pattern: '^[A-Z_]+$', maxLength: 32 },
                        order: { type: 'integer', minimum: 1, maximum: 99 },
                        defaultGroup: { type: 'boolean', default: false }
                    }
                }
            },
            
            // 活动列表
            activities: {
                type: 'array',
                items: { $ref: '#/ActivityDef' }
            },
            
            // 路由列表
            routes: {
                type: 'array',
                items: { $ref: '#/RouteDef' }
            }
        }
    },
    
    // ==================== 活动定义 Schema ====================
    ActivityDef: {
        type: 'object',
        required: ['activityDefId', 'name', 'position'],
        properties: {
            // 基本信息
            activityDefId: { type: 'string', pattern: '^act_[a-zA-Z0-9_]+$', maxLength: 64 },
            name: { type: 'string', minLength: 1, maxLength: 128 },
            description: { type: 'string', maxLength: 512 },
            
            // 位置类型
            position: { 
                type: 'string', 
                enum: ['START', 'END', 'NORMAL', 'POSITION_START', 'POSITION_END', 'POSITION_NORMAL']
            },
            
            // 活动类型
            activityType: { 
                type: 'string', 
                enum: ['TASK', 'SERVICE', 'SCRIPT', 'START', 'END', 'XOR_GATEWAY', 'AND_GATEWAY', 'OR_GATEWAY', 'SUBPROCESS', 'LLM_TASK']
            },
            
            // 坐标 (BPD属性)
            positionCoord: {
                type: 'object',
                required: ['x', 'y'],
                properties: {
                    x: { type: 'integer', minimum: 0, maximum: 10000 },
                    y: { type: 'integer', minimum: 0, maximum: 10000 }
                }
            },
            
            // 参与者ID (BPD属性)
            participantId: { type: 'string', pattern: '^Participant_[a-zA-Z0-9_]+$', maxLength: 64 },
            
            // 实现方式
            implementation: { type: 'string', enum: ['No', 'Tool', 'SubFlow', 'OutFlow', 'Device', 'Event', 'Service', 'Agent', 'Route', 'Block'] },
            
            // 时限配置
            limitTime: { type: 'integer', minimum: 0 },
            alertTime: { type: 'integer', minimum: 0 },
            durationUnit: { type: 'string', enum: ['M', 'H', 'D', 'W'] },
            
            // 流程控制
            join: { type: 'string', enum: ['XOR', 'OR', 'AND'] },
            split: { type: 'string', enum: ['XOR', 'OR', 'AND'] },
            
            // 退回配置
            canRouteBack: { type: 'string', enum: ['YES', 'NO'] },
            routeBackMethod: { type: 'string', enum: ['PREV', 'START', 'ANY'] },
            
            // 特送配置
            canSpecialSend: { type: 'string', enum: ['YES', 'NO'] },
            specialScope: { type: 'string', enum: ['ALL', 'DEPARTMENT', 'GROUP', 'USER'] },
            
            // ==================== RIGHT 属性组 ====================
            RIGHT: {
                type: 'object',
                properties: {
                    // 执行配置
                    performType: { type: 'string', enum: ['SINGLE', 'JOINTSIGN', 'COUNTERSIGN'] },
                    performSequence: { type: 'string', enum: ['FIRST', 'MEANWHILE', 'SEQUENCE'] },
                    specialSendScope: { type: 'string', enum: ['ALL', 'DEPARTMENT', 'GROUP', 'USER'] },
                    
                    // 操作权限
                    canInsteadSign: { type: 'string', enum: ['YES', 'NO'] },
                    canTakeBack: { type: 'string', enum: ['YES', 'NO'] },
                    canReSend: { type: 'string', enum: ['YES', 'NO'] },
                    
                    // 人员选择
                    insteadSignSelected: { type: 'string', maxLength: 64 },
                    performerSelectedId: { type: 'string', maxLength: 64 },
                    readerSelectedId: { type: 'string', maxLength: 64 },
                    
                    // 人员移动
                    movePerformerTo: { type: 'string', pattern: '^rg_[a-zA-Z0-9_]+$', maxLength: 64 },
                    moveSponsorTo: { type: 'string', pattern: '^rg_[a-zA-Z0-9_]+$', maxLength: 64 },
                    moveReaderTo: { type: 'string', pattern: '^rg_[a-zA-Z0-9_]+$', maxLength: 64 },
                    
                    // 代理配置
                    surrogateId: { type: 'string', maxLength: 64 },
                    surrogateName: { type: 'string', maxLength: 64 }
                }
            },
            
            // ==================== FORM 属性组 ====================
            FORM: {
                type: 'object',
                properties: {
                    formId: { type: 'string', pattern: '^form_[a-zA-Z0-9_]+$', maxLength: 64 },
                    formName: { type: 'string', minLength: 1, maxLength: 128 },
                    formType: { type: 'string', enum: ['CUSTOM', 'SYSTEM', 'EXTERNAL'] },
                    formUrl: { type: 'string', maxLength: 512 }
                }
            },
            
            // ==================== SERVICE 属性组 ====================
            SERVICE: {
                type: 'object',
                properties: {
                    httpMethod: { type: 'string', enum: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'] },
                    httpUrl: { type: 'string', maxLength: 512 },
                    httpRequestType: { type: 'string', enum: ['JSON', 'XML', 'FORM', 'TEXT'] },
                    httpResponseType: { type: 'string', enum: ['JSON', 'XML', 'TEXT'] },
                    httpServiceParams: { type: 'string', maxLength: 4000 },
                    serviceSelectedId: { type: 'string', maxLength: 64 }
                }
            },
            
            // ==================== WORKFLOW 属性组 ====================
            WORKFLOW: {
                type: 'object',
                properties: {
                    deadLineOperation: { type: 'string', enum: ['NOTIFY', 'ESCALATE', 'AUTO_COMPLETE'] },
                    specialScope: { type: 'string', enum: ['ALL', 'DEPARTMENT', 'GROUP', 'USER'] }
                }
            },
            
            // 块活动特殊属性 (BPD)
            startOfBlock: {
                type: 'object',
                properties: {
                    participantId: { type: 'string', pattern: '^Participant_[a-zA-Z0-9_]+$', maxLength: 64 },
                    firstActivityId: { type: 'string', pattern: '^act_[a-zA-Z0-9_]+$', maxLength: 64 },
                    positionCoord: {
                        type: 'object',
                        properties: {
                            x: { type: 'integer' },
                            y: { type: 'integer' }
                        }
                    },
                    routing: { type: 'string' }
                }
            },
            endOfBlock: {
                type: 'object',
                properties: {
                    participantId: { type: 'string', pattern: '^Participant_[a-zA-Z0-9_]+$', maxLength: 64 },
                    lastActivityId: { type: 'string', pattern: '^act_[a-zA-Z0-9_]+$', maxLength: 64 },
                    positionCoord: {
                        type: 'object',
                        properties: {
                            x: { type: 'integer' },
                            y: { type: 'integer' }
                        }
                    },
                    routing: { type: 'string' }
                }
            },
            participantVisualOrder: { type: 'string', maxLength: 256 }
        }
    },
    
    // ==================== 路由定义 Schema ====================
    RouteDef: {
        type: 'object',
        required: ['routeDefId', 'fromActivityDefId', 'toActivityDefId'],
        properties: {
            routeDefId: { type: 'string', pattern: '^route_[a-zA-Z0-9_]+$', maxLength: 64 },
            name: { type: 'string', maxLength: 128 },
            description: { type: 'string', maxLength: 512 },
            fromActivityDefId: { type: 'string', pattern: '^act_[a-zA-Z0-9_]+$', maxLength: 64 },
            toActivityDefId: { type: 'string', pattern: '^act_[a-zA-Z0-9_]+$', maxLength: 64 },
            routeOrder: { type: 'integer', minimum: 1, maximum: 99 },
            routeDirection: { type: 'string', enum: ['FORWARD', 'BACKWARD', 'LOOP'] },
            routeCondition: { type: 'string', maxLength: 1024 },
            routeConditionType: { type: 'string', enum: ['CONDITION', 'OTHERWISE', 'EXCEPTION', 'DEFAULT'] },
            routing: { type: 'string', maxLength: 64 }
        }
    },
    
    // ==================== 枚举定义 ====================
    Enums: {
        // 活动位置
        ActivityPosition: {
            START: 'POSITION_START',
            END: 'POSITION_END',
            NORMAL: 'POSITION_NORMAL'
        },
        
        // Join/Split 类型
        JoinSplitType: ['XOR', 'OR', 'AND'],
        
        // 执行类型
        PerformType: ['SINGLE', 'JOINTSIGN', 'COUNTERSIGN'],
        
        // 执行顺序
        PerformSequence: ['FIRST', 'MEANWHILE', 'SEQUENCE'],
        
        // 条件类型
        ConditionType: ['CONDITION', 'OTHERWISE', 'EXCEPTION', 'DEFAULT'],
        
        // 路由方向
        RouteDirection: ['FORWARD', 'BACKWARD', 'LOOP'],
        
        // 持续时间单位
        DurationUnit: ['M', 'H', 'D', 'W'],
        
        // 表单类型
        FormType: ['CUSTOM', 'SYSTEM', 'EXTERNAL'],
        
        // HTTP 方法
        HttpMethod: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'],
        
        // HTTP 内容类型
        HttpContentType: ['JSON', 'XML', 'FORM', 'TEXT'],
        
        // 监听器事件类型
        ListenerEvent: ['PROCESS_START', 'PROCESS_END', 'ACTIVITY_START', 'ACTIVITY_END', 'ROUTE_TAKE', 'ASSIGNMENT'],
        
        // 退回方式
        RouteBackMethod: ['PREV', 'START', 'ANY'],
        
        // 特殊发送范围
        SpecialSendScope: ['ALL', 'DEPARTMENT', 'GROUP', 'USER']
    },
    
    // ==================== 校验方法 ====================
    Validator: {
        /**
         * 校验整个流程定义
         */
        validateProcessDef(data) {
            const errors = [];
            
            // 校验必填字段
            const required = BpmJsonSchema.ProcessDef.required;
            for (const field of required) {
                if (data[field] === undefined || data[field] === null) {
                    errors.push({ field, message: `必填字段缺失: ${field}` });
                }
            }
            
            // 校验活动
            if (data.activities) {
                data.activities.forEach((activity, index) => {
                    const activityErrors = this.validateActivityDef(activity);
                    if (activityErrors.length > 0) {
                        errors.push({ field: `activities[${index}]`, errors: activityErrors });
                    }
                });
            }
            
            // 校验路由
            if (data.routes) {
                data.routes.forEach((route, index) => {
                    const routeErrors = this.validateRouteDef(route);
                    if (routeErrors.length > 0) {
                        errors.push({ field: `routes[${index}]`, errors: routeErrors });
                    }
                });
            }
            
            return errors;
        },
        
        /**
         * 校验活动定义
         */
        validateActivityDef(data) {
            const errors = [];
            const schema = BpmJsonSchema.ActivityDef;
            
            // 校验必填字段
            for (const field of schema.required) {
                if (data[field] === undefined || data[field] === null) {
                    errors.push({ field, message: `必填字段缺失: ${field}` });
                }
            }
            
            // 校验属性组
            if (data.RIGHT) {
                errors.push(...this.validateAttributeGroup('RIGHT', data.RIGHT, schema.properties.RIGHT));
            }
            if (data.FORM) {
                errors.push(...this.validateAttributeGroup('FORM', data.FORM, schema.properties.FORM));
            }
            if (data.SERVICE) {
                errors.push(...this.validateAttributeGroup('SERVICE', data.SERVICE, schema.properties.SERVICE));
            }
            if (data.WORKFLOW) {
                errors.push(...this.validateAttributeGroup('WORKFLOW', data.WORKFLOW, schema.properties.WORKFLOW));
            }
            
            return errors;
        },
        
        /**
         * 校验路由定义
         */
        validateRouteDef(data) {
            const errors = [];
            const schema = BpmJsonSchema.RouteDef;
            
            for (const field of schema.required) {
                if (data[field] === undefined || data[field] === null) {
                    errors.push({ field, message: `必填字段缺失: ${field}` });
                }
            }
            
            return errors;
        },
        
        /**
         * 校验属性组
         */
        validateAttributeGroup(groupName, data, schema) {
            const errors = [];
            
            if (schema && schema.properties) {
                for (const [key, value] of Object.entries(data)) {
                    const propSchema = schema.properties[key];
                    if (propSchema) {
                        // 校验枚举值
                        if (propSchema.enum && !propSchema.enum.includes(value)) {
                            errors.push({ 
                                field: `${groupName}.${key}`, 
                                message: `无效的值: ${value}，允许的值: ${propSchema.enum.join(', ')}` 
                            });
                        }
                        // 校验字符串长度
                        if (propSchema.type === 'string' && propSchema.maxLength) {
                            if (value.length > propSchema.maxLength) {
                                errors.push({ 
                                    field: `${groupName}.${key}`, 
                                    message: `长度超过限制: ${value.length} > ${propSchema.maxLength}` 
                                });
                            }
                        }
                    }
                }
            }
            
            return errors;
        },
        
        /**
         * 检查是否有错误
         */
        hasErrors(errors) {
            return errors && errors.length > 0;
        },
        
        /**
         * 格式化错误信息
         */
        formatErrors(errors) {
            if (!errors || errors.length === 0) {
                return '校验通过';
            }
            
            return errors.map(err => {
                if (err.errors) {
                    return `${err.field}: ${this.formatErrors(err.errors)}`;
                }
                return `${err.field}: ${err.message}`;
            }).join('\n');
        }
    },
    
    // ==================== 数据转换方法 ====================
    Transformer: {
        /**
         * 将后端数据转换为前端格式
         */
        fromBackend(backendData) {
            const frontendData = {
                processDefId: backendData.processDefId || backendData.processDef?.processDefId,
                name: backendData.name || backendData.processDef?.name,
                description: backendData.description || backendData.processDef?.description,
                classification: backendData.classification || backendData.processDef?.classification,
                systemCode: backendData.systemCode || backendData.processDef?.systemCode,
                accessLevel: backendData.accessLevel || backendData.processDef?.accessLevel,
                
                // 版本信息
                version: backendData.version || backendData.activeVersion?.version,
                state: backendData.state || backendData.activeVersion?.state,
                creatorName: backendData.creatorName || backendData.activeVersion?.creatorName,
                modifierId: backendData.modifierId || backendData.activeVersion?.modifierId,
                modifierName: backendData.modifierName || backendData.activeVersion?.modifierName,
                modifyTime: backendData.modifyTime || backendData.activeVersion?.modifyTime,
                limit: backendData.limit || backendData.activeVersion?.limit,
                durationUnit: backendData.durationUnit || backendData.activeVersion?.durationUnit,
                
                // 开始/结束节点
                startNode: backendData.startNode,
                endNodes: backendData.endNodes || [],
                
                // 监听器和权限组
                listeners: backendData.listeners || [],
                rightGroups: backendData.rightGroups || [],
                
                // 活动和路由
                activities: (backendData.activities || []).map(act => this.transformActivityFromBackend(act)),
                routes: backendData.routes || []
            };
            
            return frontendData;
        },
        
        /**
         * 将前端数据转换为后端格式
         */
        toBackend(frontendData) {
            const backendData = {
                processDefId: frontendData.processDefId,
                name: frontendData.name,
                description: frontendData.description,
                classification: frontendData.classification,
                systemCode: frontendData.systemCode,
                accessLevel: frontendData.accessLevel,
                
                // 开始/结束节点
                startNode: frontendData.startNode,
                endNodes: frontendData.endNodes,
                
                // 监听器和权限组
                listeners: frontendData.listeners,
                rightGroups: frontendData.rightGroups,
                
                // 活动和路由
                activities: (frontendData.activities || []).map(act => this.transformActivityToBackend(act)),
                routes: frontendData.routes || []
            };
            
            return backendData;
        },
        
        /**
         * 转换活动数据（后端->前端）
         */
        transformActivityFromBackend(activity) {
            return {
                activityDefId: activity.activityDefId,
                name: activity.name,
                description: activity.description,
                position: activity.position,
                activityType: activity.activityType,
                positionCoord: activity.positionCoord,
                participantId: activity.participantId,
                implementation: activity.implementation,
                limitTime: activity.limitTime,
                alertTime: activity.alertTime,
                durationUnit: activity.durationUnit,
                join: activity.join,
                split: activity.split,
                canRouteBack: activity.canRouteBack,
                routeBackMethod: activity.routeBackMethod,
                canSpecialSend: activity.canSpecialSend,
                specialScope: activity.specialScope,
                
                // 属性组
                RIGHT: activity.RIGHT,
                FORM: activity.FORM,
                SERVICE: activity.SERVICE,
                WORKFLOW: activity.WORKFLOW,
                
                // 块活动属性
                startOfBlock: activity.startOfBlock,
                endOfBlock: activity.endOfBlock,
                participantVisualOrder: activity.participantVisualOrder
            };
        },
        
        /**
         * 转换活动数据（前端->后端）
         */
        transformActivityToBackend(activity) {
            return {
                activityDefId: activity.activityDefId,
                name: activity.name,
                description: activity.description,
                position: activity.position,
                activityType: activity.activityType,
                positionCoord: activity.positionCoord,
                participantId: activity.participantId,
                implementation: activity.implementation,
                limitTime: activity.limitTime,
                alertTime: activity.alertTime,
                durationUnit: activity.durationUnit,
                join: activity.join,
                split: activity.split,
                canRouteBack: activity.canRouteBack,
                routeBackMethod: activity.routeBackMethod,
                canSpecialSend: activity.canSpecialSend,
                specialScope: activity.specialScope,
                
                // 属性组
                RIGHT: activity.RIGHT,
                FORM: activity.FORM,
                SERVICE: activity.SERVICE,
                WORKFLOW: activity.WORKFLOW,
                
                // 块活动属性
                startOfBlock: activity.startOfBlock,
                endOfBlock: activity.endOfBlock,
                participantVisualOrder: activity.participantVisualOrder
            };
        }
    }
};

window.BpmJsonSchema = BpmJsonSchema;
