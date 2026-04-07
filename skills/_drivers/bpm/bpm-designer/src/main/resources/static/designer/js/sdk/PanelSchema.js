const PanelSchema = {
    process: {
        tabs: [
            { id: 'basic', name: '基本', icon: 'file' },
            { id: 'version', name: '版本', icon: 'history' },
            { id: 'form', name: '表单', icon: 'form' },
            { id: 'listener', name: '监听器', icon: 'listener' }
        ],
        fields: {
            basic: [
                { type: 'section', title: '基本信息' },
                { name: 'processDefId', label: '流程UUID', type: 'text', readonly: true },
                { name: 'name', label: '流程名称', type: 'text', required: true },
                { name: 'description', label: '流程描述', type: 'textarea' },
                { name: 'classification', label: '流程分类', type: 'select', options: [
                    { value: 'HR', label: '人力资源' },
                    { value: 'FINANCE', label: '财务' },
                    { value: 'IT', label: 'IT' },
                    { value: 'OPERATION', label: '运营' }
                ]},
                { name: 'systemCode', label: '所属应用系统', type: 'select', options: [
                    { value: 'OA', label: 'OA系统' },
                    { value: 'CMS', label: 'CMS系统' },
                    { value: 'SP', label: 'SP系统' },
                    { value: 'CUSTOM', label: '自定义' }
                ]},
                { name: 'accessLevel', label: '访问级别', type: 'radio', options: [
                    { value: 'INDEPENDENT', label: '独立启动' },
                    { value: 'SUBPROCESS', label: '子流程' },
                    { value: 'BLOCK', label: '流程块' }
                ]}
            ],
            version: [
                { type: 'section', title: '版本信息' },
                { name: 'version', label: '版本号', type: 'number', readonly: true },
                { name: 'publicationStatus', label: '状态', type: 'select', options: [
                    { value: 'DRAFT', label: '草稿' },
                    { value: 'PUBLISHED', label: '已发布' },
                    { value: 'FROZEN', label: '已冻结' }
                ]},
                { name: 'limit', label: '完成期限', type: 'number', unit: '天' },
                { name: 'durationUnit', label: '时间单位', type: 'select', options: [
                    { value: 'D', label: '天' },
                    { value: 'H', label: '小时' },
                    { value: 'W', label: '工作日' }
                ]},
                { type: 'section', title: '时间信息' },
                { name: 'activeTime', label: '激活时间', type: 'datetime', readonly: true },
                { name: 'freezeTime', label: '冻结时间', type: 'datetime', readonly: true },
                { type: 'section', title: '创建信息' },
                { name: 'creatorName', label: '创建人', type: 'text', readonly: true },
                { name: 'created', label: '创建时间', type: 'datetime', readonly: true },
                { name: 'modifierName', label: '修改人', type: 'text', readonly: true },
                { name: 'modifyTime', label: '修改时间', type: 'datetime', readonly: true }
            ],
            form: [
                { type: 'section', title: '表单配置' },
                { name: 'mark', label: '标识类型', type: 'select', options: [
                    { value: 'GLOBAL', label: '全流程唯一' },
                    { value: 'ACTIVITY', label: '活动唯一' },
                    { value: 'PERSON', label: '办理人唯一' },
                    { value: 'ACTIVITY_PERSON', label: '全过程记录' }
                ]},
                { name: 'lock', label: '锁定策略', type: 'select', options: [
                    { value: 'LOCK', label: '锁定数据' },
                    { value: 'NO_LOCK', label: '不锁定' },
                    { value: 'MSG', label: '通知修改' },
                    { value: 'PERSON', label: '人工合并' },
                    { value: 'LAST', label: '保留最后版本' }
                ]},
                { name: 'autoSave', label: '自动保存', type: 'checkbox' },
                { name: 'noSqlType', label: 'NoSQL类型', type: 'checkbox' },
                { type: 'section', title: '关联配置' },
                { name: 'tableNames', label: '关联表名', type: 'multiselect', options: [
                    { value: 'BPM_PROCESS_INST', label: '流程实例表' },
                    { value: 'BPM_ACTIVITY_INST', label: '活动实例表' },
                    { value: 'BPM_FORM_DATA', label: '表单数据表' }
                ]},
                { name: 'moduleNames', label: '模块名称', type: 'multiselect', options: [
                    { value: 'workflow', label: '工作流模块' },
                    { value: 'form', label: '表单模块' },
                    { value: 'report', label: '报表模块' }
                ]}
            ],
            listener: [
                { type: 'section', title: '监听器配置' },
                { type: 'list', name: 'listeners', addText: '添加监听器', fields: [
                    { name: 'name', label: '名称', type: 'text' },
                    { name: 'event', label: '事件', type: 'select', options: [
                        { value: 'STARTED', label: '流程启动' },
                        { value: 'COMPLETED', label: '流程完成' },
                        { value: 'TERMINATED', label: '流程终止' }
                    ]},
                    { name: 'className', label: '实现类', type: 'text' }
                ]}
            ]
        }
    },
    
    getActivitySchema(activity) {
        const activityType = activity?.activityType || 'TASK';
        const activityCategory = activity?.activityCategory || 'HUMAN';
        const implementation = activity?.implementation || 'IMPL_NO';
        
        const baseTabs = [
            { id: 'basic', name: '基本', icon: 'file' },
            { id: 'timing', name: '时限', icon: 'clock' },
            { id: 'route', name: '路由', icon: 'route' }
        ];
        
        const extensionTabs = [];
        const extensionFields = {};
        
        switch (implementation) {
            case 'IMPL_NO':
                extensionTabs.push({ id: 'right', name: '权限', icon: 'shield' });
                Object.assign(extensionFields, this._getRightFields());
                break;
            case 'IMPL_DEVICE':
                extensionTabs.push({ id: 'device', name: '设备', icon: 'cpu' });
                Object.assign(extensionFields, this._getDeviceFields());
                break;
            case 'IMPL_SERVICE':
                extensionTabs.push({ id: 'service', name: '服务', icon: 'service' });
                Object.assign(extensionFields, this._getServiceFields());
                break;
            case 'IMPL_EVENT':
                extensionTabs.push({ id: 'event', name: '事件', icon: 'event' });
                Object.assign(extensionFields, this._getEventFields());
                break;
            case 'IMPL_SUBFLOW':
            case 'IMPL_OUTFLOW':
                extensionTabs.push({ id: 'subflow', name: '子流程', icon: 'subprocess' });
                Object.assign(extensionFields, this._getSubflowFields());
                break;
            case 'IMPL_TOOL':
                extensionTabs.push({ id: 'tool', name: '工具', icon: 'tool' });
                Object.assign(extensionFields, this._getToolFields());
                break;
        }
        
        if (activityCategory === 'AGENT' || 
            activityType === 'LLM_TASK' || 
            activityType === 'AGENT_TASK' || 
            activityType === 'COORDINATOR') {
            if (!extensionTabs.find(t => t.id === 'agent')) {
                extensionTabs.push({ id: 'agent', name: 'Agent', icon: 'agent' });
                Object.assign(extensionFields, this._getAgentFields());
            }
        }
        
        if (activityCategory === 'SCENE' || 
            activityType === 'SCENE' || 
            activityType === 'SUBPROCESS' || 
            activityType === 'CALL_ACTIVITY' || 
            activityType === 'ACTIVITY_BLOCK') {
            if (!extensionTabs.find(t => t.id === 'scene')) {
                extensionTabs.push({ id: 'scene', name: '场景', icon: 'scene' });
                Object.assign(extensionFields, this._getSceneFields());
            }
        }
        
        const tabs = [...baseTabs, ...extensionTabs];
        
        const fields = {
            basic: [
                { type: 'section', title: '基本信息' },
                { name: 'name', label: '活动名称', type: 'text', required: true },
                { name: 'description', label: '活动描述', type: 'textarea' },
                { name: 'activityType', label: '活动类型', type: 'select', options: [
                    { value: 'TASK', label: '用户任务' },
                    { value: 'SERVICE', label: '服务任务' },
                    { value: 'SCRIPT', label: '脚本任务' },
                    { value: 'LLM_TASK', label: 'LLM任务' },
                    { value: 'AGENT_TASK', label: 'Agent任务' },
                    { value: 'XOR_GATEWAY', label: '排他网关' },
                    { value: 'AND_GATEWAY', label: '并行网关' },
                    { value: 'OR_GATEWAY', label: '包容网关' },
                    { value: 'START', label: '开始' },
                    { value: 'END', label: '结束' }
                ]},
                { name: 'activityCategory', label: '活动分类', type: 'radio', options: [
                    { value: 'HUMAN', label: '人工' },
                    { value: 'AGENT', label: 'Agent' },
                    { value: 'SCENE', label: '场景' }
                ]},
                { type: 'section', title: '实现方式' },
                { name: 'implementation', label: '实现方式', type: 'select', options: [
                    { value: 'IMPL_NO', label: '手动活动' },
                    { value: 'IMPL_TOOL', label: '自动活动' },
                    { value: 'IMPL_SUBFLOW', label: '子流程活动' },
                    { value: 'IMPL_OUTFLOW', label: '跳转流程活动' },
                    { value: 'IMPL_DEVICE', label: '设备活动' },
                    { value: 'IMPL_EVENT', label: '事件活动' },
                    { value: 'IMPL_SERVICE', label: '服务活动' }
                ]},
                { name: 'execClass', label: '执行类', type: 'text', placeholder: '实现类全路径' }
            ],
            timing: [
                { type: 'section', title: '时限设置' },
                { name: 'limit', label: '时间限制', type: 'number', unit: '天' },
                { name: 'alertTime', label: '报警时间', type: 'number', unit: '天' },
                { name: 'durationUnit', label: '时间单位', type: 'select', options: [
                    { value: 'Y', label: '年' },
                    { value: 'M', label: '月' },
                    { value: 'D', label: '天' },
                    { value: 'H', label: '小时' },
                    { value: 'm', label: '分钟' },
                    { value: 's', label: '秒' },
                    { value: 'W', label: '工作日' }
                ]},
                { name: 'deadlineOperation', label: '到期处理', type: 'select', options: [
                    { value: 'DEFAULT', label: '默认处理' },
                    { value: 'DELAY', label: '延期办理' },
                    { value: 'TAKEBACK', label: '自动收回' },
                    { value: 'SURROGATE', label: '代办人自动接收' }
                ]}
            ],
            route: [
                { type: 'section', title: '路由设置' },
                { name: 'join', label: '等待合并', type: 'select', options: [
                    { value: 'DEFAULT', label: '默认' },
                    { value: 'AND', label: 'AND 合并' },
                    { value: 'XOR', label: 'XOR 合并' }
                ]},
                { name: 'split', label: '并行处理', type: 'select', options: [
                    { value: 'DEFAULT', label: '默认' },
                    { value: 'AND', label: 'AND 并行' },
                    { value: 'XOR', label: 'XOR 并行' }
                ]},
                { type: 'section', title: '退回设置' },
                { name: 'canRouteBack', label: '允许退回', type: 'checkbox' },
                { name: 'routeBackMethod', label: '退回路径', type: 'select', options: [
                    { value: 'DEFAULT', label: '默认' },
                    { value: 'LAST', label: '上一活动' },
                    { value: 'ANY', label: '任意历史活动' },
                    { value: 'SPECIFY', label: '条件退回' }
                ]},
                { name: 'canSpecialSend', label: '允许特送', type: 'checkbox' },
                { name: 'specialSendScope', label: '特送范围', type: 'select', options: [
                    { value: 'DEFAULT', label: '默认' },
                    { value: 'ALL', label: '所有人' },
                    { value: 'PERFORMERS', label: '曾经的办理人' }
                ]},
                { name: 'canReSend', label: '允许补发', type: 'checkbox' }
            ],
            ...extensionFields
        };
        
        return { tabs, fields };
    },
    
    _getRightFields() {
        return {
            right: [
                { type: 'section', title: '办理类型' },
                { name: 'rightConfig.performType', label: '办理类型', type: 'select', options: [
                    { value: 'SINGLE', label: '单人办理' },
                    { value: 'MULTIPLE', label: '多人办理' },
                    { value: 'JOINTSIGN', label: '会签办理' },
                    { value: 'NEEDNOTSELECT', label: '无需选择' },
                    { value: 'NOSELECT', label: '不需要选择' }
                ]},
                { name: 'rightConfig.performSequence', label: '办理顺序', type: 'select', options: [
                    { value: 'FIRST', label: '抢占办理' },
                    { value: 'SEQUENCE', label: '顺序办理' },
                    { value: 'MEANWHILE', label: '同时办理' },
                    { value: 'AUTOSIGN', label: '自动签收' }
                ]},
                { type: 'section', title: '办理人配置' },
                { type: 'list', name: 'rightConfig.performerSelectedAtt', addText: '添加办理人', fields: [
                    { name: 'type', label: '类型', type: 'select', options: [
                        { value: 'ROLE', label: '角色' },
                        { value: 'USER', label: '用户' },
                        { value: 'FORMULA', label: '公式' }
                    ]},
                    { name: 'value', label: '值', type: 'text' }
                ]},
                { type: 'section', title: '阅办人配置' },
                { type: 'list', name: 'rightConfig.readerSelectedAtt', addText: '添加阅办人', fields: [
                    { name: 'type', label: '类型', type: 'select', options: [
                        { value: 'ROLE', label: '角色' },
                        { value: 'USER', label: '用户' },
                        { value: 'FORMULA', label: '公式' }
                    ]},
                    { name: 'value', label: '值', type: 'text' }
                ]},
                { type: 'section', title: '权限设置' },
                { name: 'rightConfig.canInsteadSign', label: '允许代签', type: 'checkbox' },
                { name: 'rightConfig.canTakeBack', label: '允许收回', type: 'checkbox' },
                { name: 'rightConfig.movePerformerTo', label: '办理后权限转移', type: 'select', options: [
                    { value: 'PERFORMER', label: '当前办理人' },
                    { value: 'SPONSOR', label: '发起人' },
                    { value: 'READER', label: '读者组' },
                    { value: 'HISTORYPERFORMER', label: '曾经办理人' },
                    { value: 'HISSPONSOR', label: '历史发起人' },
                    { value: 'HISTORYREADER', label: '历史读者' },
                    { value: 'NORIGHT', label: '无权限组' },
                    { value: 'NULL', label: '访客组' }
                ]},
                { type: 'section', title: '办理人公式配置' },
                { name: 'rightConfig.performerSelectedId', label: '办理人公式ID', type: 'text' },
                { name: 'rightConfig.performerSelectedAtt.formula', label: '办理人公式', type: 'textarea', rows: 3 },
                { name: 'rightConfig.performerSelectedAtt.formulaType', label: '公式类型', type: 'select', options: [
                    { value: 'EXPRESSION', label: '表达式' },
                    { value: 'SCRIPT', label: '脚本' },
                    { value: 'RULE', label: '规则' }
                ]},
                { type: 'section', title: '阅办人公式配置' },
                { name: 'rightConfig.readerSelectedId', label: '阅办人公式ID', type: 'text' },
                { name: 'rightConfig.readerSelectedAtt.formula', label: '阅办人公式', type: 'textarea', rows: 3 },
                { name: 'rightConfig.readerSelectedAtt.formulaType', label: '公式类型', type: 'select', options: [
                    { value: 'EXPRESSION', label: '表达式' },
                    { value: 'SCRIPT', label: '脚本' },
                    { value: 'RULE', label: '规则' }
                ]},
                { type: 'section', title: '代签人公式配置' },
                { name: 'rightConfig.insteadSignSelectedId', label: '代签人公式ID', type: 'text' },
                { name: 'rightConfig.insteadSignSelectedAtt.formula', label: '代签人公式', type: 'textarea', rows: 3 },
                { name: 'rightConfig.insteadSignSelectedAtt.formulaType', label: '公式类型', type: 'select', options: [
                    { value: 'EXPRESSION', label: '表达式' },
                    { value: 'SCRIPT', label: '脚本' },
                    { value: 'RULE', label: '规则' }
                ]},
                { type: 'section', title: '代理配置' },
                { name: 'rightConfig.surrogateId', label: '代理人ID', type: 'text' },
                { name: 'rightConfig.surrogateName', label: '代理人名称', type: 'text' },
                { name: 'rightConfig.moveSponsorTo', label: '发送人权限转移', type: 'select', options: [
                    { value: 'SPONSOR', label: '发起人' },
                    { value: 'HISSPONSOR', label: '历史发起人' },
                    { value: 'HISTORYPERFORMER', label: '曾经办理人' },
                    { value: 'NORIGHT', label: '无权限组' },
                    { value: 'NULL', label: '访客组' }
                ]}
            ]
        };
    },
    
    _getDeviceFields() {
        return {
            device: [
                { type: 'section', title: '命令执行配置' },
                { name: 'deviceConfig.commandExecType', label: '命令执行方式', type: 'select', options: [
                    { value: 'SYNC', label: '同步执行' },
                    { value: 'ASYNC', label: '异步执行' }
                ]},
                { name: 'deviceConfig.commandRetry', label: '命令重试方式', type: 'select', options: [
                    { value: 'NONE', label: '不重试' },
                    { value: 'AUTO', label: '自动重试' },
                    { value: 'MANUAL', label: '手动重试' }
                ]},
                { name: 'deviceConfig.commandExecRetryTimes', label: '命令重试次数', type: 'number', min: 0, max: 10 },
                { name: 'deviceConfig.commandDelayTime', label: '命令等待时间', type: 'number', unit: '秒' },
                { name: 'deviceConfig.commandSendTimeout', label: '命令超时等待时间', type: 'number', unit: '秒' },
                { type: 'section', title: '设备执行配置' },
                { name: 'deviceConfig.performSequence', label: '设备执行顺序', type: 'select', options: [
                    { value: 'FIRST', label: '抢占' },
                    { value: 'SEQUENCE', label: '顺序' },
                    { value: 'MEANWHILE', label: '同时' }
                ]},
                { name: 'deviceConfig.performType', label: '设备执行方式', type: 'select', options: [
                    { value: 'SINGLE', label: '单设备' },
                    { value: 'MULTIPLE', label: '多设备' }
                ]},
                { type: 'section', title: '设备端点配置' },
                { name: 'deviceConfig.endpointSelectedId', label: '设备端点ID', type: 'text' },
                { name: 'deviceConfig.commandSelectedId', label: '命令ID', type: 'text' },
                { type: 'section', title: '其他设置' },
                { name: 'deviceConfig.canOffLineSend', label: '允许离线发送', type: 'checkbox' },
                { name: 'deviceConfig.canTakeBack', label: '允许收回命令', type: 'checkbox' },
                { name: 'deviceConfig.canReSend', label: '允许重新发送', type: 'checkbox' }
            ]
        };
    },
    
    _getServiceFields() {
        return {
            service: [
                { type: 'section', title: '服务配置' },
                { name: 'serviceConfig.url', label: '服务URL', type: 'text', placeholder: 'http://...' },
                { name: 'serviceConfig.method', label: 'HTTP方法', type: 'select', options: [
                    { value: 'GET', label: 'GET' },
                    { value: 'POST', label: 'POST' },
                    { value: 'PUT', label: 'PUT' },
                    { value: 'DELETE', label: 'DELETE' }
                ]},
                { name: 'serviceConfig.requestType', label: '请求类型', type: 'select', options: [
                    { value: 'JSON', label: 'JSON' },
                    { value: 'XML', label: 'XML' },
                    { value: 'FORM', label: 'Form表单' }
                ]},
                { name: 'serviceConfig.responseType', label: '响应类型', type: 'select', options: [
                    { value: 'JSON', label: 'JSON' },
                    { value: 'XML', label: 'XML' },
                    { value: 'TEXT', label: '文本' }
                ]},
                { type: 'section', title: '服务参数' },
                { name: 'serviceConfig.serviceParams', label: '服务参数', type: 'textarea', placeholder: 'JSON格式参数...' },
                { name: 'serviceConfig.serviceSelectedID', label: '服务选择ID', type: 'text' }
            ]
        };
    },
    
    _getEventFields() {
        return {
            event: [
                { type: 'section', title: '事件配置' },
                { name: 'eventConfig.deviceEvent', label: '设备事件类型', type: 'select', options: [
                    { value: 'ON_DATA_CHANGE', label: '数据变化' },
                    { value: 'ON_STATUS_CHANGE', label: '状态变化' },
                    { value: 'ON_ALARM', label: '告警事件' },
                    { value: 'ON_TIMER', label: '定时事件' }
                ]},
                { name: 'eventConfig.endpointSelectedId', label: '端点选择ID', type: 'text' },
                { type: 'section', title: '超时配置' },
                { name: 'eventConfig.durationUnit', label: '时间单位', type: 'select', options: [
                    { value: 'D', label: '天' },
                    { value: 'H', label: '小时' },
                    { value: 'm', label: '分钟' },
                    { value: 's', label: '秒' }
                ]},
                { name: 'eventConfig.alertTime', label: '报警时间', type: 'number' },
                { name: 'eventConfig.deadLineOperation', label: '到期处理', type: 'select', options: [
                    { value: 'DEFAULT', label: '默认处理' },
                    { value: 'DELAY', label: '延期办理' },
                    { value: 'TAKEBACK', label: '自动收回' }
                ]},
                { type: 'section', title: '属性配置' },
                { name: 'eventConfig.attributeName', label: '属性名称', type: 'text' }
            ]
        };
    },
    
    _getSubflowFields() {
        return {
            subflow: [
                { type: 'section', title: '子流程配置' },
                { name: 'subflowConfig.subFlowId', label: '子流程ID', type: 'text' },
                { name: 'subflowConfig.iswaitreturn', label: '等待返回', type: 'checkbox' },
                { type: 'section', title: '参数映射' },
                { type: 'list', name: 'subflowConfig.paramMapping', addText: '添加参数映射', fields: [
                    { name: 'source', label: '源参数', type: 'text' },
                    { name: 'target', label: '目标参数', type: 'text' }
                ]}
            ]
        };
    },
    
    _getToolFields() {
        return {
            tool: [
                { type: 'section', title: '工具配置' },
                { name: 'toolConfig.toolId', label: '工具ID', type: 'text' },
                { name: 'toolConfig.toolName', label: '工具名称', type: 'text' },
                { type: 'section', title: '执行配置' },
                { name: 'toolConfig.timeout', label: '超时时间', type: 'number', unit: '秒' },
                { name: 'toolConfig.retryCount', label: '重试次数', type: 'number', min: 0, max: 10 },
                { type: 'section', title: '参数配置' },
                { type: 'list', name: 'toolConfig.params', addText: '添加参数', fields: [
                    { name: 'name', label: '参数名', type: 'text' },
                    { name: 'value', label: '参数值', type: 'text' }
                ]}
            ]
        };
    },
    
    _getAgentFields() {
        return {
            agent: [
                { type: 'section', title: 'Agent 类型' },
                { name: 'agentConfig.agentType', label: '类型', type: 'select', options: [
                    { value: 'LLM', label: 'LLM (大语言模型)' },
                    { value: 'TASK', label: 'TASK (任务执行)' },
                    { value: 'EVENT', label: 'EVENT (事件触发)' },
                    { value: 'HYBRID', label: 'HYBRID (混合模式)' },
                    { value: 'COORDINATOR', label: 'COORDINATOR (协调器)' },
                    { value: 'TOOL', label: 'TOOL (工具调用)' }
                ]},
                { type: 'section', title: '调度策略' },
                { name: 'agentConfig.scheduleStrategy', label: '策略', type: 'select', options: [
                    { value: 'SEQUENTIAL', label: '顺序执行' },
                    { value: 'PARALLEL', label: '并行执行' },
                    { value: 'CONDITIONAL', label: '条件执行' },
                    { value: 'ROUND_ROBIN', label: '轮询执行' },
                    { value: 'PRIORITY', label: '优先级执行' }
                ]},
                { type: 'section', title: '基本配置' },
                { name: 'agentConfig.agentId', label: 'Agent ID', type: 'text', readonly: true },
                { name: 'agentConfig.agentName', label: 'Agent名称', type: 'text', required: true },
                { name: 'agentConfig.agentType', label: 'Agent类型', type: 'select', options: [
                    { value: 'LLM_AGENT', label: 'LLM Agent' },
                    { value: 'TASK_AGENT', label: '任务Agent' },
                    { value: 'DATA_AGENT', label: '数据Agent' },
                    { value: 'COORDINATOR', label: '协调器' }
                ]},
                { name: 'agentConfig.status', label: '状态', type: 'select', options: [
                    { value: 'online', label: '在线' },
                    { value: 'offline', label: '离线' },
                    { value: 'busy', label: '忙碌' },
                    { value: 'error', label: '错误' }
                ]},
                { name: 'agentConfig.role', label: '角色', type: 'select', options: [
                    { value: 'worker', label: '工作节点' },
                    { value: 'coordinator', label: '协调器' },
                    { value: 'supervisor', label: '监督器' }
                ]},
                { name: 'agentConfig.version', label: '版本', type: 'text' },
                { type: 'section', title: '网络配置' },
                { name: 'agentConfig.ipAddress', label: 'IP地址', type: 'text' },
                { name: 'agentConfig.port', label: '端口', type: 'number', min: 1, max: 65535 },
                { name: 'agentConfig.clusterId', label: '集群ID', type: 'text' },
                { name: 'agentConfig.sceneGroupId', label: '场景组ID', type: 'text' },
                { type: 'section', title: '协作配置' },
                { name: 'agentConfig.collaborationMode', label: '协作模式', type: 'select', options: [
                    { value: 'SINGLE', label: '单Agent' },
                    { value: 'HIERARCHICAL', label: '层级模式' },
                    { value: 'PEER', label: '对等模式' },
                    { value: 'DEBATE', label: '辩论模式' },
                    { value: 'VOTING', label: '投票模式' }
                ]},
                { type: 'section', title: 'LLM 配置' },
                { name: 'agentConfig.llmConfig.llmProvider', label: 'LLM提供商', type: 'select', options: [
                    { value: 'OPENAI', label: 'OpenAI' },
                    { value: 'QIANWEN', label: '千问' },
                    { value: 'Ollama', label: 'Ollama' },
                    { value: 'DeepSeek', label: 'DeepSeek' }
                ]},
                { name: 'agentConfig.llmConfig.model', label: '模型', type: 'select', options: [
                    { value: 'gpt-4', label: 'GPT-4' },
                    { value: 'gpt-4-turbo', label: 'GPT-4 Turbo' },
                    { value: 'gpt-3.5-turbo', label: 'GPT-3.5 Turbo' },
                    { value: 'claude-3', label: 'Claude 3' },
                    { value: 'qwen-max', label: '千问-Max' },
                    { value: 'qwen-plus', label: '千问-Plus' },
                    { value: 'llama2', label: 'Llama 2' },
                    { value: 'deepseek-chat', label: 'DeepSeek Chat' }
                ]},
                { name: 'agentConfig.llmConfig.temperature', label: '温度参数', type: 'number', min: 0, max: 2, step: 0.1 },
                { name: 'agentConfig.llmConfig.maxTokens', label: '最大Token数', type: 'number', min: 100, max: 8000 },
                { name: 'agentConfig.llmConfig.baseUrl', label: 'API基础URL', type: 'text' },
                { name: 'agentConfig.llmConfig.apiKey', label: 'API密钥', type: 'password' },
                { type: 'section', title: '系统提示词' },
                { name: 'agentConfig.llmConfig.systemPrompt', label: '系统提示词', type: 'textarea', rows: 5 },
                { type: 'section', title: '推理策略' },
                { name: 'agentConfig.reasoningStrategy', label: '推理策略', type: 'select', options: [
                    { value: 'CHAIN_OF_THOUGHT', label: '思维链' },
                    { value: 'TREE_OF_THOUGHT', label: '思维树' },
                    { value: 'REFLECTION', label: '反思' },
                    { value: 'REACT', label: 'ReAct' }
                ]},
                { type: 'section', title: '记忆配置' },
                { name: 'agentConfig.memory.type', label: '记忆类型', type: 'select', options: [
                    { value: 'CONVERSATION', label: '对话记忆' },
                    { value: 'SUMMARY', label: '摘要记忆' },
                    { value: 'VECTOR', label: '向量记忆' },
                    { value: 'NONE', label: '无记忆' }
                ]},
                { name: 'agentConfig.memory.maxSize', label: '最大记忆数', type: 'number', min: 1, max: 100 },
                { type: 'section', title: '工具配置' },
                { type: 'list', name: 'agentConfig.tools', addText: '添加工具', fields: [
                    { name: 'name', label: '工具名称', type: 'text' },
                    { name: 'description', label: '工具描述', type: 'text' },
                    { name: 'type', label: '工具类型', type: 'select', options: [
                        { value: 'FUNCTION', label: '函数' },
                        { value: 'API', label: 'API' },
                        { value: 'DATABASE', label: '数据库' }
                    ]}
                ]},
                { type: 'section', title: '执行配置' },
                { name: 'agentConfig.execution.maxRetries', label: '最大重试次数', type: 'number', min: 0, max: 10 },
                { name: 'agentConfig.execution.timeout', label: '超时时间(秒)', type: 'number', min: 10, max: 600 },
                { name: 'agentConfig.execution.retryDelay', label: '重试延迟(秒)', type: 'number', min: 1, max: 60 },
                { type: 'section', title: '性能监控' },
                { name: 'agentConfig.maxConcurrency', label: '最大并发数', type: 'number', min: 1, max: 100 },
                { name: 'agentConfig.currentLoad', label: '当前负载', type: 'number', readonly: true },
                { name: 'agentConfig.cpuUsage', label: 'CPU使用率(%)', type: 'number', min: 0, max: 100, readonly: true },
                { name: 'agentConfig.memoryUsage', label: '内存使用率(%)', type: 'number', min: 0, max: 100, readonly: true },
                { name: 'agentConfig.healthStatus', label: '健康状态', type: 'select', options: [
                    { value: 'healthy', label: '健康' },
                    { value: 'warning', label: '警告' },
                    { value: 'critical', label: '严重' },
                    { value: 'unknown', label: '未知' }
                ]},
                { type: 'section', title: '能力和标签' },
                { name: 'agentConfig.capabilities', label: '能力列表', type: 'multiselect', options: [] },
                { name: 'agentConfig.tags', label: '标签', type: 'keyvalue' },
                { name: 'agentConfig.extendedConfig', label: '扩展配置', type: 'json' },
                { name: 'agentConfig.llmConfig.temperature', label: '温度', type: 'number', min: 0, max: 2, step: 0.1 },
                { name: 'agentConfig.llmConfig.maxTokens', label: '最大Token', type: 'number' },
                { name: 'agentConfig.llmConfig.enableFunctionCalling', label: '启用函数调用', type: 'checkbox' },
                { name: 'agentConfig.llmConfig.enableStreaming', label: '启用流式输出', type: 'checkbox' }
            ]
        };
    },
    
    _getSceneFields() {
        return {
            scene: [
                { type: 'section', title: '场景定义' },
                { name: 'sceneConfig.sceneId', label: '场景ID', type: 'text' },
                { name: 'sceneConfig.name', label: '场景名称', type: 'text' },
                { name: 'sceneConfig.sceneType', label: '场景类型', type: 'select', options: [
                    { value: 'FORM', label: '表单场景' },
                    { value: 'LIST', label: '列表场景' },
                    { value: 'DASHBOARD', label: '仪表盘场景' },
                    { value: 'CUSTOM', label: '自定义场景' }
                ]},
                { type: 'section', title: 'PageAgent 配置' },
                { name: 'sceneConfig.pageAgent.agentId', label: 'Agent ID', type: 'text' },
                { name: 'sceneConfig.pageAgent.pageId', label: '页面ID', type: 'text' },
                { name: 'sceneConfig.pageAgent.templatePath', label: '模板路径', type: 'text' },
                { type: 'section', title: '存储配置' },
                { name: 'sceneConfig.storage.type', label: '存储类型', type: 'select', options: [
                    { value: 'VFS', label: 'VFS 文件存储' },
                    { value: 'SQL', label: 'SQL 数据库' },
                    { value: 'HYBRID', label: '混合存储' }
                ]},
                { name: 'sceneConfig.storage.vfsPath', label: 'VFS 路径', type: 'text' }
            ]
        };
    },
    
    activity: {
        tabs: [
            { id: 'basic', name: '基本', icon: 'file' },
            { id: 'timing', name: '时限', icon: 'clock' },
            { id: 'route', name: '路由', icon: 'route' },
            { id: 'right', name: '权限', icon: 'shield' },
            { id: 'agent', name: 'Agent', icon: 'agent' },
            { id: 'scene', name: '场景', icon: 'scene' },
            { id: 'extend', name: '扩展', icon: 'settings' }
        ],
        fields: {}
    },
    
    route: {
        tabs: [
            { id: 'basic', name: '基本', icon: 'route' },
            { id: 'condition', name: '条件', icon: 'script' }
        ],
        fields: {
            basic: [
                { type: 'section', title: '路由信息' },
                { name: 'id', label: '路由ID', type: 'text', readonly: true },
                { name: 'name', label: '路由名称', type: 'text' },
                { type: 'section', title: '连接节点' },
                { name: 'fromName', label: '起始活动', type: 'text', readonly: true },
                { name: 'toName', label: '目标活动', type: 'text', readonly: true },
                { type: 'section', title: '显示设置' },
                { name: 'showLabel', label: '显示标签', type: 'checkbox' },
                { name: 'labelPosition', label: '标签位置', type: 'select', options: [
                    { value: 'middle', label: '中间' },
                    { value: 'start', label: '起始端' },
                    { value: 'end', label: '结束端' }
                ]}
            ],
            condition: [
                { type: 'section', title: '路由条件' },
                { name: 'condition', label: '条件表达式', type: 'textarea', placeholder: '例如: ${status == "approved"}' },
                { name: 'conditionType', label: '条件类型', type: 'select', options: [
                    { value: 'EXPRESSION', label: '表达式' },
                    { value: 'SCRIPT', label: '脚本' },
                    { value: 'RULE', label: '规则' }
                ]},
                { name: 'priority', label: '优先级', type: 'number', min: 0, max: 100 },
                { name: 'isDefault', label: '默认路由', type: 'checkbox' }
            ]
        }
    }
};

window.PanelSchema = PanelSchema;
