/**
 * 活动面板配置 - 多TABS分类展示
 */
const ActivityPanelSchema = {
    name: '活动属性',
    icon: 'activity',
    
    getSchema(activity) {
        if (!activity) return null;
        
        const impl = activity.implementation || 'IMPL_NO';
        const category = activity.activityCategory || 'HUMAN';
        const activityType = activity.activityType || 'TASK';
        
        if (['START', 'END'].includes(activityType)) {
            return this._getStartEndSchema(activity);
        }
        
        if (category === 'AGENT') {
            return this._getAgentSchema(activity);
        }
        
        if (category === 'SCENE') {
            return this._getSceneSchema(activity);
        }
        
        return this._getHumanSchema(activity, impl);
    },
    
    _getStartEndSchema(activity) {
        return {
            tabs: [
                { id: 'basic', name: '基本信息', icon: 'info' }
            ],
            fields: {
                basic: [
                    { type: 'section', title: '基本信息' },
                    { name: 'activityDefId', type: 'text', label: '活动ID', required: true, readonly: true },
                    { name: 'name', type: 'text', label: '活动名称', required: true },
                    { name: 'description', type: 'textarea', label: '描述' },
                    { name: 'activityType', type: 'select', label: '活动类型', options: [
                        { value: 'START', label: '开始节点' },
                        { value: 'END', label: '结束节点' }
                    ]}
                ]
            }
        };
    },
    
    _getHumanSchema(activity, impl) {
        const tabs = [
            { id: 'basic', name: '基本信息', icon: 'info' },
            { id: 'timing', name: '时限配置', icon: 'clock' },
            { id: 'flow', name: '流程控制', icon: 'route' },
            { id: 'performer', name: '办理配置', icon: 'user' },
            { id: 'permission', name: '权限设置', icon: 'lock' },
            { id: 'extended', name: '扩展属性', icon: 'settings' }
        ];
        
        if (impl === 'IMPL_SUBFLOW' || impl === 'IMPL_OUTFLOW') {
            tabs.splice(2, 0, { id: 'subprocess', name: '子流程', icon: 'subprocess' });
        } else if (impl === 'IMPL_SERVICE') {
            tabs.splice(2, 0, { id: 'service', name: '服务配置', icon: 'service' });
        } else if (impl === 'IMPL_TOOL') {
            tabs.splice(2, 0, { id: 'tool', name: '工具配置', icon: 'tool' });
        }
        
        return {
            tabs,
            fields: {
                basic: this._getBasicFields(activity, impl),
                timing: this._getTimingFields(activity),
                flow: this._getFlowControlFields(activity),
                performer: this._getPerformerFields(activity),
                permission: this._getPermissionFields(activity),
                extended: this._getExtendedFields(activity),
                ...(impl === 'IMPL_SUBFLOW' || impl === 'IMPL_OUTFLOW' ? { subprocess: this._getSubprocessFields(activity) } : {}),
                ...(impl === 'IMPL_SERVICE' ? { service: this._getServiceFields(activity) } : {}),
                ...(impl === 'IMPL_TOOL' ? { tool: this._getToolFields(activity) } : {})
            }
        };
    },
    
    _getAgentSchema(activity) {
        return {
            tabs: [
                { id: 'basic', name: '基本信息', icon: 'info' },
                { id: 'agent', name: 'Agent配置', icon: 'robot' },
                { id: 'llm', name: 'LLM配置', icon: 'brain' },
                { id: 'tools', name: '工具配置', icon: 'tool' },
                { id: 'timing', name: '时限配置', icon: 'clock' },
                { id: 'extended', name: '扩展属性', icon: 'settings' }
            ],
            fields: {
                basic: this._getBasicFields(activity, 'IMPL_AGENT'),
                agent: this._getAgentBasicFields(activity),
                llm: this._getLLMFields(activity),
                tools: this._getAgentToolsFields(activity),
                timing: this._getTimingFields(activity),
                extended: this._getExtendedFields(activity)
            }
        };
    },
    
    _getSceneSchema(activity) {
        return {
            tabs: [
                { id: 'basic', name: '基本信息', icon: 'info' },
                { id: 'scene', name: '场景配置', icon: 'scene' },
                { id: 'engine', name: '引擎配置', icon: 'engine' },
                { id: 'params', name: '参数配置', icon: 'code' },
                { id: 'capability', name: '能力配置', icon: 'capability' },
                { id: 'timing', name: '时限配置', icon: 'clock' },
                { id: 'extended', name: '扩展属性', icon: 'settings' }
            ],
            fields: {
                basic: this._getBasicFields(activity, 'IMPL_SCENE'),
                scene: this._getSceneBasicFields(activity),
                engine: this._getSceneEngineFields(activity),
                params: this._getSceneParamsFields(activity),
                capability: this._getSceneCapabilityFields(activity),
                timing: this._getTimingFields(activity),
                extended: this._getExtendedFields(activity)
            }
        };
    },
    
    _getBasicFields(activity, impl) {
        return [
            { type: 'section', title: '基本信息' },
            { name: 'activityDefId', type: 'text', label: '活动ID', required: true, readonly: true },
            { name: 'name', type: 'text', label: '活动名称', required: true },
            { name: 'description', type: 'textarea', label: '描述' },
            { name: 'activityType', type: 'select', label: '活动类型', options: [
                { value: 'TASK', label: '用户任务' },
                { value: 'SERVICE', label: '服务任务' },
                { value: 'SCRIPT', label: '脚本任务' },
                { value: 'SUBPROCESS', label: '子流程' },
                { value: 'LLM_TASK', label: 'LLM任务' }
            ]},
            { type: 'section', title: '分类设置' },
            { name: 'activityCategory', type: 'select', label: '活动分类', options: [
                { value: 'HUMAN', label: '人工活动' },
                { value: 'AGENT', label: 'Agent活动' },
                { value: 'SCENE', label: '场景活动' }
            ]},
            { name: 'implementation', type: 'select', label: '实现方式', options: [
                { value: 'IMPL_NO', label: '无实现' },
                { value: 'IMPL_TOOL', label: '工具' },
                { value: 'IMPL_SUBFLOW', label: '子流程' },
                { value: 'IMPL_OUTFLOW', label: '外部流程' },
                { value: 'IMPL_SERVICE', label: '服务' }
            ]},
            { name: 'position', type: 'select', label: '活动位置', options: [
                { value: 'NORMAL', label: '正常位置' },
                { value: 'START', label: '开始位置' },
                { value: 'END', label: '结束位置' }
            ]}
        ];
    },
    
    _getTimingFields(activity) {
        return [
            { type: 'section', title: '时限设置' },
            { name: 'limit', type: 'number', label: '时限', min: 0 },
            { name: 'durationUnit', type: 'select', label: '时长单位', options: [
                { value: 'Y', label: '年' },
                { value: 'M', label: '月' },
                { value: 'D', label: '天' },
                { value: 'H', label: '小时' },
                { value: 'm', label: '分钟' },
                { value: 's', label: '秒' },
                { value: 'W', label: '周' }
            ]},
            { type: 'section', title: '执行模式' },
            { name: 'startMode', type: 'select', label: '启动模式', options: [
                { value: 'MANUAL', label: '手动' },
                { value: 'AUTOMATIC', label: '自动' }
            ]},
            { name: 'finishMode', type: 'select', label: '完成模式', options: [
                { value: 'MANUAL', label: '手动' },
                { value: 'AUTOMATIC', label: '自动' }
            ]}
        ];
    },
    
    _getFlowControlFields(activity) {
        return [
            { type: 'section', title: '汇聚配置' },
            { name: 'join', type: 'select', label: '汇聚类型', options: [
                { value: 'DEFAULT', label: '默认' },
                { value: 'JOIN_AND', label: '与汇聚', description: '所有输入路由都到达后合并' },
                { value: 'JOIN_XOR', label: '异或汇聚', description: '任意输入路由到达后合并' }
            ]},
            { type: 'section', title: '分支配置' },
            { name: 'split', type: 'select', label: '分支类型', options: [
                { value: 'DEFAULT', label: '默认' },
                { value: 'SPLIT_AND', label: '与分支', description: '所有输出路由并行执行' },
                { value: 'SPLIT_XOR', label: '异或分支', description: '根据条件选择一条路由执行' }
            ]}
        ];
    },
    
    _getPerformerFields(activity) {
        return [
            { type: 'section', title: '办理人设置' },
            { name: 'performerType', type: 'select', label: '办理人类型', options: [
                { value: 'HUMAN', label: '人工' },
                { value: 'SYSTEM', label: '系统' },
                { value: 'AGENT', label: '代理' }
            ]},
            { name: 'performerId', type: 'text', label: '办理人ID' },
            { name: 'performerName', type: 'text', label: '办理人名称' },
            { type: 'section', title: '办理方式' },
            { name: 'performType', type: 'select', label: '办理类型', options: [
                { value: 'DEFAULT', label: '默认' },
                { value: 'SINGLE', label: '单人办理', description: '只需一人办理' },
                { value: 'MULTIPLE', label: '多人办理', description: '多人参与办理' },
                { value: 'JOINTSIGN', label: '会签', description: '多人同时办理' }
            ]},
            { name: 'performSequence', type: 'select', label: '办理顺序', options: [
                { value: 'DEFAULT', label: '默认' },
                { value: 'FIRST', label: '第一人办理', description: '第一人签收后即可办理' },
                { value: 'SEQUENCE', label: '顺序办理', description: '按顺序依次办理' },
                { value: 'MEANWHILE', label: '同时办理', description: '多人同时办理' },
                { value: 'AUTOSIGN', label: '自动签收', description: '自动签收办理' }
            ]},
            { type: 'section', title: '到期处理' },
            { name: 'deadlineOperation', type: 'select', label: '到期处理', options: [
                { value: 'DEFAULT', label: '默认处理' },
                { value: 'DELAY', label: '延期处理', description: '允许延期办理' },
                { value: 'TAKEBACK', label: '收回处理', description: '自动收回任务' },
                { value: 'SURROGATE', label: '代理处理', description: '转交代理人办理' }
            ]},
            { type: 'section', title: '退回配置' },
            { name: 'canRouteBack', type: 'checkbox', label: '允许退回' },
            { name: 'routeBackMethod', type: 'select', label: '退回路径', options: [
                { value: 'DEFAULT', label: '默认' },
                { value: 'LAST', label: '上一环节', description: '退回到上一办理环节' },
                { value: 'ANY', label: '任意环节', description: '退回到任意历史环节' },
                { value: 'SPECIFY', label: '指定环节', description: '退回到指定环节' }
            ]},
            { type: 'section', title: '特送配置' },
            { name: 'canSpecialSend', type: 'checkbox', label: '允许特送' },
            { name: 'specialSendScope', type: 'select', label: '特送范围', options: [
                { value: 'DEFAULT', label: '默认' },
                { value: 'ALL', label: '所有人', description: '特送给所有人' },
                { value: 'PERFORMERS', label: '办理人', description: '特送给办理人' }
            ]}
        ];
    },
    
    _getPermissionFields(activity) {
        return [
            { type: 'section', title: '优先级设置' },
            { name: 'priority', type: 'number', label: '优先级', min: 0, max: 100 },
            { type: 'section', title: '操作权限' },
            { name: 'skipable', type: 'checkbox', label: '可跳过' },
            { name: 'allowDelegate', type: 'checkbox', label: '允许委托' },
            { name: 'allowTransfer', type: 'checkbox', label: '允许转办' },
            { name: 'canInsteadSign', type: 'checkbox', label: '允许代签' },
            { name: 'canTakeBack', type: 'checkbox', label: '允许收回' },
            { name: 'canReSend', type: 'checkbox', label: '允许补发' },
            { type: 'section', title: '权限组配置' },
            { name: 'rightGroup', type: 'select', label: '权限组', options: [
                { value: 'PERFORMER', label: '办理人' },
                { value: 'SPONSOR', label: '发起人' },
                { value: 'READER', label: '阅办人' },
                { value: 'HISTORYPERFORMER', label: '历史办理人' },
                { value: 'HISSPONSOR', label: '历史发起人' },
                { value: 'HISTORYREADER', label: '历史阅办人' },
                { value: 'NORIGHT', label: '无权限' },
                { value: 'NULL', label: '空权限' }
            ]}
        ];
    },
    
    _getSubprocessFields(activity) {
        return [
            { type: 'section', title: '子流程配置' },
            { name: 'subFlow.processDefId', type: 'text', label: '子流程ID', placeholder: 'sub-process-id' },
            { name: 'subFlow.version', type: 'number', label: '版本', min: 1 },
            { name: 'subFlow.async', type: 'checkbox', label: '异步执行' },
            { name: 'subFlow.waitComplete', type: 'checkbox', label: '等待完成' }
        ];
    },
    
    _getServiceFields(activity) {
        return [
            { type: 'section', title: '服务配置' },
            { name: 'service.name', type: 'text', label: '服务名称', placeholder: 'ServiceName' },
            { name: 'service.method', type: 'text', label: '方法名', placeholder: 'methodName' },
            { name: 'service.async', type: 'checkbox', label: '异步调用' }
        ];
    },
    
    _getToolFields(activity) {
        return [
            { type: 'section', title: '工具配置' },
            { name: 'execClass', type: 'text', label: '执行类', placeholder: 'com.example.ToolClass' },
            { name: 'execMethod', type: 'text', label: '执行方法', placeholder: 'execute' }
        ];
    },
    
    _getAgentBasicFields(activity) {
        return [
            { type: 'section', title: 'Agent基本信息' },
            { name: 'agentConfig.agentType', type: 'select', label: 'Agent类型', options: [
                { value: 'LLM_AGENT', label: 'LLM Agent' },
                { value: 'RULE_AGENT', label: '规则Agent' },
                { value: 'HYBRID_AGENT', label: '混合Agent' }
            ]},
            { name: 'agentConfig.agentId', type: 'text', label: 'Agent ID', placeholder: 'agent-001' },
            { name: 'agentConfig.agentName', type: 'text', label: 'Agent名称', placeholder: '智能助手' },
            { name: 'agentConfig.status', type: 'select', label: '状态', options: [
                { value: 'online', label: '在线' },
                { value: 'offline', label: '离线' },
                { value: 'busy', label: '忙碌' }
            ]},
            { name: 'agentConfig.role', type: 'select', label: '角色', options: [
                { value: 'worker', label: '执行者' },
                { value: 'supervisor', label: '监督者' },
                { value: 'coordinator', label: '协调者' }
            ]}
        ];
    },
    
    _getLLMFields(activity) {
        return [
            { type: 'section', title: 'LLM提供商' },
            { name: 'agentConfig.llmProvider', type: 'select', label: 'LLM提供商', options: [
                { value: 'openai', label: 'OpenAI' },
                { value: 'anthropic', label: 'Anthropic' },
                { value: 'local', label: '本地模型' }
            ]},
            { name: 'agentConfig.model', type: 'text', label: '模型名称', placeholder: 'gpt-4' },
            { type: 'section', title: '模型参数' },
            { name: 'agentConfig.temperature', type: 'number', label: '温度', min: 0, max: 2, step: 0.1 },
            { name: 'agentConfig.maxTokens', type: 'number', label: '最大Token', min: 100, max: 32000 },
            { name: 'agentConfig.topP', type: 'number', label: 'Top P', min: 0, max: 1, step: 0.1 }
        ];
    },
    
    _getAgentToolsFields(activity) {
        return [
            { type: 'section', title: '工具配置' },
            { name: 'agentConfig.tools', type: 'textarea', label: '可用工具', placeholder: '["tool1", "tool2"]' },
            { type: 'section', title: '提示词配置' },
            { name: 'agentConfig.systemPrompt', type: 'textarea', label: '系统提示词', placeholder: '你是一个智能助手...' },
            { name: 'agentConfig.userPrompt', type: 'textarea', label: '用户提示词模板', placeholder: '请处理以下任务：{task}' },
            { type: 'section', title: '执行配置' },
            { name: 'agentConfig.maxRetries', type: 'number', label: '最大重试次数', min: 0, max: 10 },
            { name: 'agentConfig.timeout', type: 'number', label: '超时时间(秒)', min: 10, max: 3600 },
            { name: 'agentConfig.autoApprove', type: 'checkbox', label: '自动审批' }
        ];
    },
    
    _getSceneBasicFields(activity) {
        return [
            { type: 'section', title: '场景基本信息' },
            { name: 'sceneConfig.sceneType', type: 'select', label: '场景类型', options: [
                { value: 'AUTO', label: '自动场景' },
                { value: 'MANUAL', label: '手动场景' },
                { value: 'HYBRID', label: '混合场景' }
            ]},
            { name: 'sceneConfig.sceneId', type: 'text', label: '场景ID', placeholder: 'scene-001' },
            { name: 'sceneConfig.sceneName', type: 'text', label: '场景名称', placeholder: '审批场景' },
            { name: 'sceneConfig.status', type: 'select', label: '状态', options: [
                { value: 'ENABLED', label: '启用' },
                { value: 'DISABLED', label: '禁用' },
                { value: 'DRAFT', label: '草稿' }
            ]}
        ];
    },
    
    _getSceneEngineFields(activity) {
        return [
            { type: 'section', title: '引擎配置' },
            { name: 'sceneConfig.engineType', type: 'select', label: '引擎类型', options: [
                { value: 'RULE', label: '规则引擎' },
                { value: 'FLOW', label: '流程引擎' },
                { value: 'AI', label: 'AI引擎' }
            ]},
            { type: 'section', title: '触发配置' },
            { name: 'sceneConfig.triggerType', type: 'select', label: '触发类型', options: [
                { value: 'MANUAL', label: '手动触发' },
                { value: 'SCHEDULE', label: '定时触发' },
                { value: 'EVENT', label: '事件触发' }
            ]},
            { name: 'sceneConfig.cronExpression', type: 'text', label: 'Cron表达式', placeholder: '0 0 9 * * ?' }
        ];
    },
    
    _getSceneParamsFields(activity) {
        return [
            { type: 'section', title: '参数配置' },
            { name: 'sceneConfig.inputParams', type: 'textarea', label: '输入参数', placeholder: '{"param1": "value1"}' },
            { name: 'sceneConfig.outputParams', type: 'textarea', label: '输出参数', placeholder: '{"result": "value"}' }
        ];
    },
    
    _getSceneCapabilityFields(activity) {
        return [
            { type: 'section', title: '能力配置' },
            { name: 'sceneConfig.capabilities', type: 'textarea', label: '可用能力', placeholder: '["capability1", "capability2"]' },
            { name: 'sceneConfig.sceneGroupId', type: 'text', label: '场景组ID', placeholder: 'group-001' },
            { type: 'section', title: '执行配置' },
            { name: 'sceneConfig.timeout', type: 'number', label: '超时时间(秒)', min: 10, max: 3600 },
            { name: 'sceneConfig.retryCount', type: 'number', label: '重试次数', min: 0, max: 10 },
            { name: 'sceneConfig.async', type: 'checkbox', label: '异步执行' }
        ];
    },
    
    _getExtendedFields(activity) {
        return [
            { type: 'section', title: '扩展属性' },
            { name: 'extendedAttributes', type: 'keyvalue', label: '扩展属性', addText: '添加属性' }
        ];
    }
};

window.ActivityPanelSchema = ActivityPanelSchema;
