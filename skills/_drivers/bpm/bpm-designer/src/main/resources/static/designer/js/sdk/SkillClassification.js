class SkillClassification {

    static FORM_OPTIONS = [
        { value: 'SCENE', label: '场景Skill', description: '容器型，需要激活，有独立上下文' },
        { value: 'STANDALONE', label: '独立Skill', description: '原子型，可直接调用，无状态' }
    ];

    static CATEGORY_OPTIONS = [
        { value: 'LLM', label: '大模型推理', icon: 'brain', description: 'LLM推理、对话、Function Calling' },
        { value: 'FORM', label: '表单交互', icon: 'form', description: '表单展示、数据采集、审批' },
        { value: 'SERVICE', label: '服务调用', icon: 'service', description: 'API调用、服务集成' },
        { value: 'WORKFLOW', label: '流程编排', icon: 'workflow', description: '子流程、活动块、场景' },
        { value: 'KNOWLEDGE', label: '知识管理', icon: 'knowledge', description: 'RAG检索、知识库、问答' },
        { value: 'DATA', label: '数据处理', icon: 'data', description: '数据存储、查询、同步' },
        { value: 'COMM', label: '通讯服务', icon: 'comm', description: '消息、通知、推送' },
        { value: 'TOOL', label: '工具服务', icon: 'tool', description: '文档处理、报表、转换' }
    ];

    static PROVIDER_OPTIONS = [
        { value: 'SYSTEM', label: '系统内置', description: '核心能力，不可删除', tier: 0 },
        { value: 'DRIVER', label: '驱动适配', description: '第三方平台集成，互斥', tier: 1 },
        { value: 'BUSINESS', label: '业务定制', description: '企业级，可配置', tier: 2 },
        { value: 'USER', label: '用户自定义', description: '个人级，可分享', tier: 3 }
    ];

    static PERFORMER_OPTIONS = [
        { value: 'HUMAN', label: '真人', description: '需要UI交互，人工触发Skill', requiresUI: true },
        { value: 'AGENT', label: '拟人智能体', description: 'Agent，可自主执行Skill', requiresUI: false },
        { value: 'SYSTEM', label: '系统', description: '系统自动执行', requiresUI: false }
    ];

    static ISOLATION_OPTIONS = [
        { value: 'SHARED', label: '共享父上下文', description: '与父流程共享所有变量', nestingTypes: ['BLOCK_NORMAL'] },
        { value: 'PARTIAL', label: '部分隔离', description: '继承部分变量，有独立空间', nestingTypes: ['SUBFLOW'] },
        { value: 'ISOLATED', label: '完全隔离', description: '完全独立的上下文空间', nestingTypes: ['SCENE', 'EXTERNAL'] }
    ];

    static SCENE_TYPE_OPTIONS = [
        { value: 'AUTO', label: '自主场景', description: '自驱动场景，自动运行', hasSelfDrive: true },
        { value: 'TRIGGER', label: '触发场景', description: '触发型场景，需要入口', hasSelfDrive: false },
        { value: 'HYBRID', label: '混合场景', description: '同时支持AUTO和TRIGGER', hasSelfDrive: true }
    ];

    static VISIBILITY_OPTIONS = [
        { value: 'public', label: '公开可见', description: '用户可发现、可激活' },
        { value: 'internal', label: '内部使用', description: '后台运行，用户不可见' }
    ];

    static IMPLEMENTATION_OPTIONS = [
        { value: 'IMPL_NO', label: '手动活动', description: '人工办理', panel: 'right' },
        { value: 'IMPL_TOOL', label: '自动活动', description: '工具执行', panel: 'tool' },
        { value: 'IMPL_SUBFLOW', label: '子流程活动', description: '调用子流程', panel: 'subflow' },
        { value: 'IMPL_OUTFLOW', label: '跳转流程活动', description: '跳转到其他流程', panel: 'outflow' },
        { value: 'IMPL_DEVICE', label: '设备活动', description: '设备命令', panel: 'device' },
        { value: 'IMPL_EVENT', label: '事件活动', description: '事件监听', panel: 'event' },
        { value: 'IMPL_SERVICE', label: '服务活动', description: '服务调用', panel: 'service' }
    ];

    static DURATION_UNIT_OPTIONS = [
        { value: 'Y', label: '年' },
        { value: 'M', label: '月' },
        { value: 'W', label: '周' },
        { value: 'D', label: '天' },
        { value: 'H', label: '小时' },
        { value: 'm', label: '分钟' },
        { value: 's', label: '秒' }
    ];

    static DEADLINE_OPERATION_OPTIONS = [
        { value: 'DEFAULT', label: '默认处理' },
        { value: 'DELAY', label: '延迟处理' },
        { value: 'TAKEBACK', label: '收回' },
        { value: 'SURROGATE', label: '代理' }
    ];

    static PERFORM_TYPE_OPTIONS = [
        { value: 'SINGLE', label: '单人办理' },
        { value: 'MULTIPLE', label: '多人办理' },
        { value: 'JOINTSIGN', label: '会签' },
        { value: 'COUNTERSIGN', label: '或签' },
        { value: 'NEEDNOTSELECT', label: '无需选择' },
        { value: 'NOSELECT', label: '不选择' }
    ];

    static PERFORM_SEQUENCE_OPTIONS = [
        { value: 'FIRST', label: '优先' },
        { value: 'SEQUENCE', label: '顺序' },
        { value: 'MEANWHILE', label: '同时' },
        { value: 'AUTOSIGN', label: '自动签收' }
    ];

    static ROUTE_BACK_METHOD_OPTIONS = [
        { value: 'DEFAULT', label: '默认' },
        { value: 'LAST', label: '上一环节' },
        { value: 'ANY', label: '任意环节' },
        { value: 'SPECIFY', label: '指定环节' }
    ];

    static JOIN_SPLIT_OPTIONS = [
        { value: 'DEFAULT', label: '默认' },
        { value: 'AND', label: 'AND（全部等待）' },
        { value: 'XOR', label: 'XOR（任一触发）' }
    ];

    static AGENT_TYPE_OPTIONS = [
        { value: 'LLM', label: 'LLM Agent', description: '大语言模型驱动' },
        { value: 'TASK', label: '任务Agent', description: '任务执行型' },
        { value: 'EVENT', label: '事件Agent', description: '事件响应型' },
        { value: 'HYBRID', label: '混合Agent', description: '多模式组合' },
        { value: 'COORDINATOR', label: '协调器', description: '多Agent协调' },
        { value: 'TOOL', label: '工具Agent', description: '工具调用型' }
    ];

    static COLLABORATION_MODE_OPTIONS = [
        { value: 'SOLO', label: '独立执行', description: '单个Agent独立完成' },
        { value: 'HIERARCHICAL', label: '层级协作', description: '主从结构协作' },
        { value: 'PEER', label: '对等协作', description: '平级Agent协作' },
        { value: 'DEBATE', label: '辩论模式', description: '多Agent辩论决策' },
        { value: 'VOTING', label: '投票模式', description: '多Agent投票决策' }
    ];

    static SCHEDULE_STRATEGY_OPTIONS = [
        { value: 'SEQUENTIAL', label: '顺序执行' },
        { value: 'PARALLEL', label: '并行执行' },
        { value: 'CONDITIONAL', label: '条件执行' },
        { value: 'ROUND_ROBIN', label: '轮询执行' },
        { value: 'PRIORITY', label: '优先级执行' }
    ];

    static LLM_PROVIDER_OPTIONS = [
        { value: 'deepseek', label: 'DeepSeek' },
        { value: 'openai', label: 'OpenAI' },
        { value: 'qianwen', label: '通义千问' },
        { value: 'volcengine', label: '火山引擎' },
        { value: 'ollama', label: 'Ollama' },
        { value: 'claude', label: 'Claude' }
    ];

    static validateClassification(form, category, provider) {
        const errors = [];
        if (!form) errors.push('形态分类不能为空');
        if (!category) errors.push('功能分类不能为空');
        if (!provider) errors.push('提供者分类不能为空');
        if (form === 'SCENE' && category !== 'WORKFLOW') {
            errors.push('SCENE形态必须绑定WORKFLOW类别');
        }
        return errors;
    }

    static getDefaultIsolationLevel(nestingType) {
        const map = {
            'SUBFLOW': 'PARTIAL',
            'BLOCK_NORMAL': 'SHARED',
            'BLOCK': 'SHARED',
            'SCENE': 'ISOLATED',
            'EXTERNAL': 'ISOLATED'
        };
        return map[nestingType] || 'SHARED';
    }

    static getCategoryDefaults(category) {
        const defaults = {
            'LLM': { 
                llmConfig: { provider: 'deepseek', model: 'deepseek-chat', temperature: 0.7, maxTokens: 4096 },
                agentType: 'LLM'
            },
            'FORM': { 
                formConfig: { formType: 'DYNAMIC', validation: { enabled: true } }
            },
            'SERVICE': { 
                serviceConfig: { serviceType: 'API', timeout: 30000 }
            },
            'WORKFLOW': { 
                workflowConfig: { workflowType: 'SUBFLOW' },
                contextIsolation: { level: 'PARTIAL' }
            },
            'KNOWLEDGE': { 
                knowledgeConfig: { knowledgeType: 'RAG', embedding: { model: 'text-embedding-ada-002' } }
            },
            'DATA': { 
                dataConfig: { dataType: 'STORAGE', storageType: 'LOCAL' }
            },
            'COMM': { 
                commConfig: { commType: 'MESSAGE', channels: ['system'] }
            },
            'TOOL': { 
                toolConfig: { toolType: 'DOCUMENT', supportedFormats: ['pdf', 'doc', 'docx'] }
            }
        };
        return defaults[category] || {};
    }

    static getProviderDefaults(provider) {
        const defaults = {
            'SYSTEM': { core: true, immutable: true, permissions: ['read', 'execute'] },
            'DRIVER': { exclusive: true, tier: 'MEDIUM', healthCheck: { enabled: true } },
            'BUSINESS': { permissions: ['read', 'execute', 'configure'], audit: { enabled: true } },
            'USER': { shared: false, visibility: 'PRIVATE' }
        };
        return defaults[provider] || {};
    }

    static getPerformerDefaults(performerType) {
        const defaults = {
            'HUMAN': { 
                requiresUI: true,
                right: { performType: 'SINGLE', performSequence: 'FIRST' }
            },
            'AGENT': { 
                requiresUI: false,
                agentConfig: { agentType: 'LLM', collaborationMode: 'SOLO' }
            },
            'SYSTEM': { 
                requiresUI: false,
                autoExecute: true
            }
        };
        return defaults[performerType] || {};
    }

    static getApplicablePanels(classification, performer) {
        const panels = ['basic', 'timing', 'routing'];
        
        const category = classification?.category || 'SERVICE';
        const form = classification?.form || 'STANDALONE';
        const performerType = performer?.type || 'HUMAN';

        if (performerType === 'HUMAN') {
            panels.push('right');
        }

        if (performerType === 'AGENT') {
            panels.push('agent');
        }

        if (['FORM', 'WORKFLOW'].includes(category)) {
            panels.push('form');
        }

        if (['SERVICE', 'LLM', 'TOOL'].includes(category)) {
            panels.push('service');
        }

        if (['KNOWLEDGE'].includes(category)) {
            panels.push('knowledge');
        }

        if (['DATA'].includes(category)) {
            panels.push('data');
        }

        if (['COMM'].includes(category)) {
            panels.push('comm');
        }

        if (form === 'SCENE' || category === 'WORKFLOW') {
            panels.push('scene');
        }

        return panels;
    }

    static getCategoryPanelSchema(category) {
        const schemas = {
            'LLM': [
                { name: 'llmConfig.provider', label: 'Provider', type: 'select', options: this.LLM_PROVIDER_OPTIONS, default: 'deepseek' },
                { name: 'llmConfig.model', label: '模型', type: 'text', default: 'deepseek-chat' },
                { name: 'llmConfig.temperature', label: 'Temperature', type: 'slider', min: 0, max: 2, step: 0.1, default: 0.7 },
                { name: 'llmConfig.maxTokens', label: 'Max Tokens', type: 'number', default: 4096 },
                { name: 'llmConfig.systemPrompt', label: '系统提示词', type: 'textarea', rows: 3 },
                { name: 'llmConfig.enableFunctionCalling', label: '启用Function Calling', type: 'checkbox', default: true },
                { name: 'llmConfig.enableStreaming', label: '启用流式输出', type: 'checkbox', default: true }
            ],
            'FORM': [
                { name: 'formConfig.formType', label: '表单类型', type: 'select', options: [
                    { value: 'DYNAMIC', label: '动态表单' },
                    { value: 'STATIC', label: '静态表单' },
                    { value: 'A2UI', label: 'A2UI表单' },
                    { value: 'EXTERNAL', label: '外部表单' }
                ], default: 'DYNAMIC' },
                { name: 'formConfig.formId', label: '表单ID', type: 'text' },
                { name: 'formConfig.formVersion', label: '表单版本', type: 'text' },
                { name: 'formConfig.formUrl', label: '表单URL', type: 'text', showIf: { 'formConfig.formType': 'EXTERNAL' } },
                { name: 'formConfig.readonlyFields', label: '只读字段', type: 'text', placeholder: '逗号分隔' },
                { name: 'formConfig.hiddenFields', label: '隐藏字段', type: 'text', placeholder: '逗号分隔' },
                { name: 'formConfig.enableAudit', label: '启用表单审计', type: 'checkbox', default: false },
                { name: 'formConfig.autoSave', label: '自动保存', type: 'checkbox', default: true }
            ],
            'SERVICE': [
                { name: 'serviceConfig.serviceType', label: '服务类型', type: 'select', options: [
                    { value: 'API', label: 'API调用' },
                    { value: 'TOOL', label: '工具调用' },
                    { value: 'CAPABILITY', label: '能力调用' }
                ], default: 'API' },
                { name: 'serviceConfig.serviceId', label: '服务ID', type: 'text' },
                { name: 'serviceConfig.serviceVersion', label: '服务版本', type: 'text' },
                { name: 'serviceConfig.url', label: '服务URL', type: 'text' },
                { name: 'serviceConfig.method', label: 'HTTP方法', type: 'select', options: [
                    { value: 'GET', label: 'GET' },
                    { value: 'POST', label: 'POST' },
                    { value: 'PUT', label: 'PUT' },
                    { value: 'DELETE', label: 'DELETE' }
                ], default: 'POST' },
                { name: 'serviceConfig.timeout', label: '超时(ms)', type: 'number', default: 30000 },
                { name: 'serviceConfig.invokeType', label: '调用方式', type: 'select', options: [
                    { value: 'SYNC', label: '同步' },
                    { value: 'ASYNC', label: '异步' },
                    { value: 'MQ', label: '消息队列' }
                ], default: 'SYNC' },
                { name: 'serviceConfig.inputMapping', label: '输入映射', type: 'textarea', rows: 2 },
                { name: 'serviceConfig.outputMapping', label: '输出映射', type: 'textarea', rows: 2 }
            ],
            'WORKFLOW': [
                { name: 'workflowConfig.workflowType', label: 'Workflow类型', type: 'radio', options: [
                    { value: 'SUBFLOW', label: '子流程' },
                    { value: 'BLOCK', label: '活动块' },
                    { value: 'SCENE', label: '场景' },
                    { value: 'EXTERNAL', label: '外部流程' }
                ], default: 'SUBFLOW' },
                { name: 'workflowConfig.refProcessId', label: '引用流程', type: 'text' },
                { name: 'workflowConfig.refProcessVersion', label: '流程版本', type: 'text' },
                { name: 'contextIsolation.level', label: '上下文隔离', type: 'select', options: this.ISOLATION_OPTIONS, default: 'PARTIAL' },
                { name: 'contextIsolation.inheritVariables', label: '继承父变量', type: 'checkbox', default: true },
                { name: 'contextIsolation.inheritFormData', label: '继承表单数据', type: 'checkbox', default: true },
                { name: 'workflowConfig.iswaitreturn', label: '等待返回', type: 'checkbox', default: true }
            ],
            'KNOWLEDGE': [
                { name: 'knowledgeConfig.knowledgeType', label: '知识类型', type: 'select', options: [
                    { value: 'RAG', label: 'RAG检索' },
                    { value: 'QA', label: '问答' },
                    { value: 'MANAGEMENT', label: '知识管理' },
                    { value: 'SHARE', label: '知识分享' }
                ], default: 'RAG' },
                { name: 'knowledgeConfig.knowledgeBaseId', label: '知识库ID', type: 'text' },
                { name: 'knowledgeConfig.embedding.model', label: 'Embedding模型', type: 'text', default: 'text-embedding-ada-002' },
                { name: 'knowledgeConfig.topK', label: 'Top K', type: 'number', default: 5 },
                { name: 'knowledgeConfig.scoreThreshold', label: '分数阈值', type: 'number', default: 0.7, min: 0, max: 1, step: 0.1 }
            ],
            'DATA': [
                { name: 'dataConfig.dataType', label: '数据类型', type: 'select', options: [
                    { value: 'STORAGE', label: '存储' },
                    { value: 'QUERY', label: '查询' },
                    { value: 'SYNC', label: '同步' }
                ], default: 'STORAGE' },
                { name: 'dataConfig.storageType', label: '存储类型', type: 'select', options: [
                    { value: 'LOCAL', label: '本地' },
                    { value: 'DATABASE', label: '数据库' },
                    { value: 'OSS', label: 'OSS' },
                    { value: 'MINIO', label: 'MinIO' },
                    { value: 'VFS', label: 'VFS' }
                ], default: 'LOCAL' },
                { name: 'dataConfig.connectionString', label: '连接字符串', type: 'text' },
                { name: 'dataConfig.tableName', label: '表名', type: 'text' }
            ],
            'COMM': [
                { name: 'commConfig.commType', label: '通讯类型', type: 'select', options: [
                    { value: 'MESSAGE', label: '消息' },
                    { value: 'NOTIFICATION', label: '通知' },
                    { value: 'PUSH', label: '推送' },
                    { value: 'EMAIL', label: '邮件' },
                    { value: 'SMS', label: '短信' }
                ], default: 'MESSAGE' },
                { name: 'commConfig.channels', label: '通道', type: 'text', placeholder: '逗号分隔', default: 'system' },
                { name: 'commConfig.templateId', label: '消息模板', type: 'text' }
            ],
            'TOOL': [
                { name: 'toolConfig.toolType', label: '工具类型', type: 'select', options: [
                    { value: 'DOCUMENT', label: '文档处理' },
                    { value: 'REPORT', label: '报表生成' },
                    { value: 'CONVERT', label: '格式转换' },
                    { value: 'SCRIPT', label: '脚本执行' }
                ], default: 'DOCUMENT' },
                { name: 'toolConfig.toolId', label: '工具ID', type: 'text' },
                { name: 'toolConfig.supportedFormats', label: '支持格式', type: 'text', placeholder: 'pdf,doc,docx' },
                { name: 'toolConfig.inputParams', label: '输入参数', type: 'textarea', rows: 2 }
            ]
        };
        return schemas[category] || [];
    }

    static getAgentPanelSchema() {
        return [
            { name: 'agentConfig.agentType', label: 'Agent类型', type: 'select', options: this.AGENT_TYPE_OPTIONS, default: 'LLM' },
            { name: 'agentConfig.collaborationMode', label: '协作模式', type: 'select', options: this.COLLABORATION_MODE_OPTIONS, default: 'SOLO' },
            { name: 'agentConfig.scheduleStrategy', label: '调度策略', type: 'select', options: this.SCHEDULE_STRATEGY_OPTIONS, default: 'SEQUENTIAL' },
            { name: 'agentConfig.maxIterations', label: '最大迭代次数', type: 'number', default: 10 },
            { name: 'agentConfig.timeout', label: '超时时间(s)', type: 'number', default: 300 },
            { name: 'agentConfig.memory.type', label: '记忆类型', type: 'select', options: [
                { value: 'NONE', label: '无记忆' },
                { value: 'CONVERSATION', label: '对话记忆' },
                { value: 'SUMMARY', label: '摘要记忆' },
                { value: 'VECTOR', label: '向量记忆' }
            ], default: 'CONVERSATION' },
            { name: 'agentConfig.memory.maxSize', label: '记忆容量', type: 'number', default: 10 },
            { name: 'agentConfig.reasoningStrategy', label: '推理策略', type: 'select', options: [
                { value: 'CHAIN_OF_THOUGHT', label: '思维链' },
                { value: 'TREE_OF_THOUGHT', label: '思维树' },
                { value: 'REACT', label: 'ReAct' },
                { value: 'PLAN_AND_EXECUTE', label: '计划执行' }
            ], default: 'CHAIN_OF_THOUGHT' }
        ];
    }

    static getScenePanelSchema() {
        return [
            { name: 'sceneConfig.sceneId', label: '场景ID', type: 'text' },
            { name: 'sceneConfig.sceneType', label: '场景类型', type: 'select', options: this.SCENE_TYPE_OPTIONS, default: 'TRIGGER' },
            { name: 'sceneConfig.visibility', label: '可见性', type: 'select', options: this.VISIBILITY_OPTIONS, default: 'public' },
            { name: 'sceneConfig.hasSelfDrive', label: '自驱动', type: 'checkbox', default: false },
            { name: 'sceneConfig.businessSemanticsScore', label: '业务语义评分', type: 'number', min: 1, max: 10, default: 8 },
            { name: 'sceneConfig.triggerCondition', label: '触发条件', type: 'textarea', rows: 2 },
            { name: 'sceneConfig.pageAgent.agentId', label: 'PageAgent ID', type: 'text' },
            { name: 'sceneConfig.pageAgent.pageId', label: '页面ID', type: 'text' },
            { name: 'sceneConfig.storage.type', label: '存储类型', type: 'select', options: [
                { value: 'VFS', label: 'VFS' },
                { value: 'SQL', label: 'SQL' },
                { value: 'HYBRID', label: '混合' }
            ], default: 'VFS' },
            { name: 'sceneConfig.storage.vfsPath', label: 'VFS路径', type: 'text' }
        ];
    }
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = SkillClassification;
} else {
    window.SkillClassification = SkillClassification;
}
