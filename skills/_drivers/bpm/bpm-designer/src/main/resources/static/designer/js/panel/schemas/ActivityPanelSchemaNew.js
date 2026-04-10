/**
 * 活动面板配置 - 新版本
 * 完整的属性映射，确保每个后端属性都有对应的前端界面
 */
const ActivityPanelSchemaNew = {
    name: '活动属性',
    icon: 'activity',
    
    getSchema(activity) {
        if (!activity) return null;
        
        const impl = activity.implementation || 'No';
        const position = activity.position || 'NORMAL';
        const activityType = activity.activityType || 'TASK';
        
        // 开始/结束节点使用简化面板
        if (['START', 'END', 'POSITION_START', 'POSITION_END'].includes(position)) {
            return this._getStartEndSchema(activity);
        }
        
        // 普通活动使用完整面板
        return this._getFullSchema(activity, impl);
    },
    
    /**
     * 开始/结束节点面板
     */
    _getStartEndSchema(activity) {
        return {
            tabs: [
                { id: 'basic', name: '基本信息', icon: 'info' }
            ],
            fields: {
                basic: [
                    { type: 'section', title: '基本信息' },
                    { 
                        name: 'activityDefId', 
                        type: 'text', 
                        label: '活动ID', 
                        required: true, 
                        readonly: true,
                        description: '活动的唯一标识符'
                    },
                    { 
                        name: 'name', 
                        type: 'text', 
                        label: '活动名称', 
                        required: true,
                        description: '显示在流程图上的名称'
                    },
                    { 
                        name: 'description', 
                        type: 'textarea', 
                        label: '描述',
                        description: '活动的详细说明'
                    },
                    { 
                        name: 'position', 
                        type: 'select', 
                        label: '位置类型', 
                        readonly: true,
                        options: [
                            { value: 'START', label: '开始节点' },
                            { value: 'END', label: '结束节点' }
                        ],
                        description: '活动在流程中的位置'
                    },
                    
                    { type: 'section', title: '坐标配置 (BPD)' },
                    { 
                        name: 'positionCoord.x', 
                        type: 'number', 
                        label: 'X坐标', 
                        min: 0, 
                        max: 10000,
                        description: '活动在画布上的X坐标'
                    },
                    { 
                        name: 'positionCoord.y', 
                        type: 'number', 
                        label: 'Y坐标', 
                        min: 0, 
                        max: 10000,
                        description: '活动在画布上的Y坐标'
                    },
                    
                    { type: 'section', title: '参与者配置 (BPD)' },
                    { 
                        name: 'participantId', 
                        type: 'text', 
                        label: '参与者ID',
                        pattern: '^Participant_[a-zA-Z0-9_]+$',
                        description: 'XPDL参与者标识符'
                    }
                ]
            }
        };
    },
    
    /**
     * 完整活动面板
     */
    _getFullSchema(activity, impl) {
        const tabs = [
            { id: 'basic', name: '基本信息', icon: 'info' },
            { id: 'timing', name: '时限配置', icon: 'clock' },
            { id: 'flow', name: '流程控制', icon: 'git-branch' },
            { id: 'right', name: '权限配置', icon: 'lock' },
            { id: 'form', name: '表单配置', icon: 'file-text' }
        ];
        
        // 根据实现方式添加特定TAB
        if (impl === 'Service') {
            tabs.splice(4, 0, { id: 'service', name: '服务配置', icon: 'server' });
        } else if (impl === 'SubFlow') {
            tabs.splice(4, 0, { id: 'subflow', name: '子流程', icon: 'layers' });
        }
        
        return {
            tabs,
            fields: {
                basic: this._getBasicFields(activity),
                timing: this._getTimingFields(activity),
                flow: this._getFlowControlFields(activity),
                right: this._getRightFields(activity),
                form: this._getFormFields(activity),
                service: impl === 'Service' ? this._getServiceFields(activity) : [],
                subflow: impl === 'SubFlow' ? this._getSubflowFields(activity) : []
            }
        };
    },
    
    /**
     * 基本信息字段
     */
    _getBasicFields(activity) {
        return [
            { type: 'section', title: '基本信息' },
            { 
                name: 'activityDefId', 
                type: 'text', 
                label: '活动ID', 
                required: true, 
                readonly: true,
                description: '活动的唯一标识符'
            },
            { 
                name: 'name', 
                type: 'text', 
                label: '活动名称', 
                required: true,
                description: '显示在流程图上的名称'
            },
            { 
                name: 'description', 
                type: 'textarea', 
                label: '描述',
                description: '活动的详细说明'
            },
            { 
                name: 'activityType', 
                type: 'select', 
                label: '活动类型',
                options: [
                    { value: 'TASK', label: '用户任务' },
                    { value: 'SERVICE', label: '服务任务' },
                    { value: 'SCRIPT', label: '脚本任务' },
                    { value: 'XOR_GATEWAY', label: '排他网关' },
                    { value: 'AND_GATEWAY', label: '并行网关' },
                    { value: 'OR_GATEWAY', label: '包容网关' },
                    { value: 'SUBPROCESS', label: '子流程' }
                ],
                description: '活动的业务类型'
            },
            { 
                name: 'implementation', 
                type: 'select', 
                label: '实现方式',
                options: [
                    { value: 'No', label: '无实现' },
                    { value: 'Tool', label: '工具' },
                    { value: 'SubFlow', label: '子流程' },
                    { value: 'Service', label: '服务' },
                    { value: 'Block', label: '块活动' }
                ],
                description: '活动的实现方式'
            },
            
            { type: 'section', title: '坐标配置 (BPD)' },
            { 
                name: 'positionCoord.x', 
                type: 'number', 
                label: 'X坐标', 
                min: 0, 
                max: 10000,
                description: '活动在画布上的X坐标'
            },
            { 
                name: 'positionCoord.y', 
                type: 'number', 
                label: 'Y坐标', 
                min: 0, 
                max: 10000,
                description: '活动在画布上的Y坐标'
            },
            { 
                name: 'participantId', 
                type: 'text', 
                label: '参与者ID',
                pattern: '^Participant_[a-zA-Z0-9_]+$',
                description: 'XPDL参与者标识符'
            }
        ];
    },
    
    /**
     * 时限配置字段
     */
    _getTimingFields(activity) {
        return [
            { type: 'section', title: '时限配置' },
            { 
                name: 'limitTime', 
                type: 'number', 
                label: '时限', 
                min: 0,
                description: '活动的办理时限'
            },
            { 
                name: 'alertTime', 
                type: 'number', 
                label: '预警时间', 
                min: 0,
                description: '到期前预警时间'
            },
            { 
                name: 'durationUnit', 
                type: 'select', 
                label: '时间单位',
                options: [
                    { value: 'M', label: '分钟' },
                    { value: 'H', label: '小时' },
                    { value: 'D', label: '天' },
                    { value: 'W', label: '周' }
                ],
                description: '时限的时间单位'
            },
            
            { type: 'section', title: 'WORKFLOW属性组' },
            { 
                name: 'WORKFLOW.deadLineOperation', 
                type: 'select', 
                label: '到期操作',
                options: [
                    { value: 'NOTIFY', label: '通知' },
                    { value: 'ESCALATE', label: '升级' },
                    { value: 'AUTO_COMPLETE', label: '自动完成' }
                ],
                description: '到期后的处理方式'
            },
            { 
                name: 'WORKFLOW.specialScope', 
                type: 'select', 
                label: '特殊范围',
                options: [
                    { value: 'ALL', label: '全部' },
                    { value: 'DEPARTMENT', label: '部门' },
                    { value: 'GROUP', label: '组' },
                    { value: 'USER', label: '用户' }
                ],
                description: '特殊发送范围'
            }
        ];
    },
    
    /**
     * 流程控制字段
     */
    _getFlowControlFields(activity) {
        return [
            { type: 'section', title: '汇聚配置' },
            { 
                name: 'join', 
                type: 'select', 
                label: '汇聚类型',
                options: [
                    { value: 'XOR', label: '异或汇聚 (XOR)' },
                    { value: 'OR', label: '或汇聚 (OR)' },
                    { value: 'AND', label: '与汇聚 (AND)' }
                ],
                description: '多个输入路由的汇聚方式'
            },
            
            { type: 'section', title: '分支配置' },
            { 
                name: 'split', 
                type: 'select', 
                label: '分支类型',
                options: [
                    { value: 'XOR', label: '异或分支 (XOR)' },
                    { value: 'OR', label: '或分支 (OR)' },
                    { value: 'AND', label: '与分支 (AND)' }
                ],
                description: '多个输出路由的分支方式'
            },
            
            { type: 'section', title: '退回配置' },
            { 
                name: 'canRouteBack', 
                type: 'select', 
                label: '允许退回',
                options: [
                    { value: 'YES', label: '是' },
                    { value: 'NO', label: '否' }
                ],
                description: '是否允许退回'
            },
            { 
                name: 'routeBackMethod', 
                type: 'select', 
                label: '退回方式',
                options: [
                    { value: 'PREV', label: '上一环节' },
                    { value: 'START', label: '开始环节' },
                    { value: 'ANY', label: '任意环节' }
                ],
                description: '退回的目标环节'
            },
            
            { type: 'section', title: '特送配置' },
            { 
                name: 'canSpecialSend', 
                type: 'select', 
                label: '允许特送',
                options: [
                    { value: 'YES', label: '是' },
                    { value: 'NO', label: '否' }
                ],
                description: '是否允许特送'
            },
            { 
                name: 'specialScope', 
                type: 'select', 
                label: '特送范围',
                options: [
                    { value: 'ALL', label: '全部' },
                    { value: 'DEPARTMENT', label: '部门' },
                    { value: 'GROUP', label: '组' },
                    { value: 'USER', label: '用户' }
                ],
                description: '特送的范围'
            }
        ];
    },
    
    /**
     * 权限配置字段 (RIGHT属性组)
     */
    _getRightFields(activity) {
        return [
            { type: 'section', title: '执行配置 (RIGHT)' },
            { 
                name: 'RIGHT.performType', 
                type: 'select', 
                label: '执行类型',
                options: [
                    { value: 'SINGLE', label: '单人执行' },
                    { value: 'JOINTSIGN', label: '会签' },
                    { value: 'COUNTERSIGN', label: '联签' }
                ],
                description: '活动的执行方式'
            },
            { 
                name: 'RIGHT.performSequence', 
                type: 'select', 
                label: '执行顺序',
                options: [
                    { value: 'FIRST', label: '先到先得' },
                    { value: 'MEANWHILE', label: '同时执行' },
                    { value: 'SEQUENCE', label: '顺序执行' }
                ],
                description: '多人执行时的顺序'
            },
            { 
                name: 'RIGHT.specialSendScope', 
                type: 'select', 
                label: '特送范围',
                options: [
                    { value: 'ALL', label: '全部' },
                    { value: 'DEPARTMENT', label: '部门' },
                    { value: 'GROUP', label: '组' },
                    { value: 'USER', label: '用户' }
                ],
                description: '特送范围配置'
            },
            
            { type: 'section', title: '操作权限 (RIGHT)' },
            { 
                name: 'RIGHT.canInsteadSign', 
                type: 'select', 
                label: '允许代签',
                options: [
                    { value: 'YES', label: '是' },
                    { value: 'NO', label: '否' }
                ],
                description: '是否允许代签'
            },
            { 
                name: 'RIGHT.canTakeBack', 
                type: 'select', 
                label: '允许收回',
                options: [
                    { value: 'YES', label: '是' },
                    { value: 'NO', label: '否' }
                ],
                description: '是否允许收回'
            },
            { 
                name: 'RIGHT.canReSend', 
                type: 'select', 
                label: '允许补发',
                options: [
                    { value: 'YES', label: '是' },
                    { value: 'NO', label: '否' }
                ],
                description: '是否允许补发'
            },
            
            { type: 'section', title: '人员选择 (RIGHT)' },
            { 
                name: 'RIGHT.insteadSignSelected', 
                type: 'text', 
                label: '代签人选择ID',
                description: '代签人选择标识'
            },
            { 
                name: 'RIGHT.performerSelectedId', 
                type: 'text', 
                label: '办理人选择ID',
                description: '办理人选择标识'
            },
            { 
                name: 'RIGHT.readerSelectedId', 
                type: 'text', 
                label: '阅办人选择ID',
                description: '阅办人选择标识'
            },
            
            { type: 'section', title: '人员移动 (RIGHT)' },
            { 
                name: 'RIGHT.movePerformerTo', 
                type: 'text', 
                label: '办理人移至权限组',
                pattern: '^rg_[a-zA-Z0-9_]+$',
                description: '办理人移动目标权限组ID'
            },
            { 
                name: 'RIGHT.moveSponsorTo', 
                type: 'text', 
                label: '发起人移至权限组',
                pattern: '^rg_[a-zA-Z0-9_]+$',
                description: '发起人移动目标权限组ID'
            },
            { 
                name: 'RIGHT.moveReaderTo', 
                type: 'text', 
                label: '阅办人移至权限组',
                pattern: '^rg_[a-zA-Z0-9_]+$',
                description: '阅办人移动目标权限组ID'
            },
            
            { type: 'section', title: '代理配置 (RIGHT)' },
            { 
                name: 'RIGHT.surrogateId', 
                type: 'text', 
                label: '代理人ID',
                description: '代理人标识'
            },
            { 
                name: 'RIGHT.surrogateName', 
                type: 'text', 
                label: '代理人名称',
                description: '代理人显示名称'
            }
        ];
    },
    
    /**
     * 表单配置字段 (FORM属性组)
     */
    _getFormFields(activity) {
        return [
            { type: 'section', title: '表单配置 (FORM)' },
            { 
                name: 'FORM.formId', 
                type: 'text', 
                label: '表单ID',
                pattern: '^form_[a-zA-Z0-9_]+$',
                description: '表单唯一标识符'
            },
            { 
                name: 'FORM.formName', 
                type: 'text', 
                label: '表单名称',
                description: '表单显示名称'
            },
            { 
                name: 'FORM.formType', 
                type: 'select', 
                label: '表单类型',
                options: [
                    { value: 'CUSTOM', label: '自定义表单' },
                    { value: 'SYSTEM', label: '系统表单' },
                    { value: 'EXTERNAL', label: '外部表单' }
                ],
                description: '表单的类型'
            },
            { 
                name: 'FORM.formUrl', 
                type: 'text', 
                label: '表单URL',
                description: '外部表单的访问地址'
            }
        ];
    },
    
    /**
     * 服务配置字段 (SERVICE属性组)
     */
    _getServiceFields(activity) {
        return [
            { type: 'section', title: 'HTTP配置 (SERVICE)' },
            { 
                name: 'SERVICE.httpMethod', 
                type: 'select', 
                label: 'HTTP方法',
                options: [
                    { value: 'GET', label: 'GET' },
                    { value: 'POST', label: 'POST' },
                    { value: 'PUT', label: 'PUT' },
                    { value: 'DELETE', label: 'DELETE' },
                    { value: 'PATCH', label: 'PATCH' }
                ],
                description: 'HTTP请求方法'
            },
            { 
                name: 'SERVICE.httpUrl', 
                type: 'text', 
                label: '服务URL',
                description: '服务调用的完整URL'
            },
            
            { type: 'section', title: '数据格式 (SERVICE)' },
            { 
                name: 'SERVICE.httpRequestType', 
                type: 'select', 
                label: '请求格式',
                options: [
                    { value: 'JSON', label: 'JSON' },
                    { value: 'XML', label: 'XML' },
                    { value: 'FORM', label: 'Form Data' },
                    { value: 'TEXT', label: 'Text' }
                ],
                description: '请求体数据格式'
            },
            { 
                name: 'SERVICE.httpResponseType', 
                type: 'select', 
                label: '响应格式',
                options: [
                    { value: 'JSON', label: 'JSON' },
                    { value: 'XML', label: 'XML' },
                    { value: 'TEXT', label: 'Text' }
                ],
                description: '响应数据格式'
            },
            
            { type: 'section', title: '参数配置 (SERVICE)' },
            { 
                name: 'SERVICE.httpServiceParams', 
                type: 'textarea', 
                label: '请求参数',
                description: 'JSON格式的请求参数模板'
            },
            { 
                name: 'SERVICE.serviceSelectedId', 
                type: 'text', 
                label: '服务选择ID',
                description: '服务选择标识'
            }
        ];
    },
    
    /**
     * 子流程配置字段
     */
    _getSubflowFields(activity) {
        return [
            { type: 'section', title: '子流程配置' },
            { 
                name: 'subFlow.processDefId', 
                type: 'text', 
                label: '子流程ID',
                description: '子流程定义ID'
            },
            { 
                name: 'subFlow.version', 
                type: 'number', 
                label: '版本',
                min: 1,
                description: '子流程版本号'
            },
            { 
                name: 'subFlow.async', 
                type: 'checkbox', 
                label: '异步执行',
                description: '是否异步执行子流程'
            },
            { 
                name: 'subFlow.waitComplete', 
                type: 'checkbox', 
                label: '等待完成',
                description: '是否等待子流程完成'
            }
        ];
    }
};

window.ActivityPanelSchemaNew = ActivityPanelSchemaNew;
