const PanelSchema = {
    process: {
        tabs: [
            { id: 'basic', name: '基本信息', icon: 'info' },
            { id: 'permission', name: '权限配置', icon: 'lock' },
            { id: 'extended', name: '扩展属性', icon: 'settings' }
        ],
        fields: {
            basic: [
                { type: 'section', title: '基本信息' },
                { name: 'processId', type: 'text', label: '流程ID', required: true, readonly: true },
                { name: 'name', type: 'text', label: '流程名称', required: true },
                { name: 'description', type: 'textarea', label: '描述' },
                { name: 'classification', type: 'select', label: '分类', options: [
                    { value: 'NORMAL', label: '普通流程' },
                    { value: 'URGENT', label: '紧急流程' },
                    { value: 'IMPORTANT', label: '重要流程' }
                ]},
                { name: 'systemCode', type: 'text', label: '系统编码' },
                { name: 'publicationStatus', type: 'select', label: '发布状态', options: [
                    { value: 'DRAFT', label: '草稿' },
                    { value: 'PUBLISHED', label: '已发布' },
                    { value: 'ARCHIVED', label: '已归档' }
                ]},
                { name: 'version', type: 'text', label: '版本', readonly: true },
                
                { type: 'section', title: '时限配置' },
                { name: 'limit', type: 'number', label: '时限', min: 0, unit: '小时' },
                { name: 'durationUnit', type: 'select', label: '时长单位', options: [
                    { value: 'HOUR', label: '小时' },
                    { value: 'DAY', label: '天' },
                    { value: 'WEEK', label: '周' },
                    { value: 'MONTH', label: '月' }
                ]},
                
                { type: 'section', title: '流程控制' },
                { name: 'autostart', type: 'checkbox', label: '自动启动' },
                { name: 'singleton', type: 'checkbox', label: '单例模式' },
                { name: 'validFrom', type: 'text', label: '生效日期' },
                { name: 'validTo', type: 'text', label: '失效日期' }
            ],
            permission: [
                { type: 'section', title: '访问权限' },
                { name: 'access', type: 'select', label: '访问权限', options: [
                    { value: 'PUBLIC', label: '公开' },
                    { value: 'PRIVATE', label: '私有' },
                    { value: 'PROTECTED', label: '受保护' }
                ]},
                { name: 'accessRule', type: 'textarea', label: '访问规则' },
                
                { type: 'section', title: '管理权限' },
                { name: 'manager', type: 'text', label: '管理者' },
                { name: 'managerUnit', type: 'text', label: '管理单位' }
            ],
            extended: [
                { type: 'section', title: '扩展属性' },
                { name: 'extendedAttributes', type: 'keyvalue', label: '扩展属性', addText: '添加属性' }
            ]
        }
    },

    route: {
        tabs: [
            { id: 'basic', name: '基本信息', icon: 'info' },
            { id: 'condition', name: '条件配置', icon: 'filter' }
        ],
        fields: {
            basic: [
                { type: 'section', title: '基本信息' },
                { name: 'id', type: 'text', label: '路由ID', readonly: true },
                { name: 'name', type: 'text', label: '路由名称', required: true },
                { type: 'section', title: '连接信息' },
                { name: 'from', type: 'text', label: '源活动ID', readonly: true },
                { name: 'fromName', type: 'text', label: '源活动名称', readonly: true },
                { name: 'to', type: 'text', label: '目标活动ID', readonly: true },
                { name: 'toName', type: 'text', label: '目标活动名称', readonly: true }
            ],
            condition: [
                { type: 'section', title: '条件配置' },
                { name: 'condition', type: 'textarea', label: '条件表达式', placeholder: '请输入条件表达式，如: ${amount} > 1000' },
                { type: 'section', title: '条件类型' },
                { name: 'conditionType', type: 'select', label: '条件类型', options: [
                    { value: 'EXPRESSION', label: '表达式' },
                    { value: 'SCRIPT', label: '脚本' },
                    { value: 'RULE', label: '规则' }
                ]},
                { name: 'defaultRoute', type: 'checkbox', label: '默认路由' },
                { name: 'priority', type: 'number', label: '优先级', min: 0, max: 100 }
            ]
        }
    },

    getActivitySchema: function(activity) {
        if (!activity || !activity.impl) {
            return null;
        }

        const impl = activity.impl;
        let schema = {
            tabs: [
                { id: 'basic', name: '基本信息', icon: 'info' },
                { id: 'permission', name: '权限配置', icon: 'lock' },
                { id: 'extended', name: '扩展属性', icon: 'settings' }
            ],
            fields: {
                basic: this._getBasicFields(impl),
                permission: this._getPermissionFields(impl),
                extended: [
                    { type: 'section', title: '扩展属性' },
                    { name: 'extendedAttributes', type: 'keyvalue', label: '扩展属性', addText: '添加属性' }
                ]
            }
        };

        if (impl === 'IMPL_AGENT') {
            schema.tabs.push({ id: 'agent', name: 'Agent配置', icon: 'robot' });
            schema.fields.agent = this._getAgentFields();
            schema.tabs.push({ id: 'capability', name: '能力配置', icon: 'cube' });
            schema.fields.capability = this._getCapabilityFields();
        }

        return schema;
    },

    _getBasicFields: function(impl) {
        const fields = [
            { type: 'section', title: '基本信息' },
            { name: 'activityId', type: 'text', label: '活动ID', required: true, readonly: true },
            { name: 'name', type: 'text', label: '活动名称', required: true },
            { name: 'description', type: 'textarea', label: '描述' },
            { name: 'impl', type: 'select', label: '实现类型', options: [
                { value: 'IMPL_NO', label: '无实现（手动活动）' },
                { value: 'IMPL_TOOL', label: '工具' },
                { value: 'IMPL_SUBFLOW', label: '子流程' },
                { value: 'IMPL_OUTFLOW', label: '外部流程' },
                { value: 'IMPL_DEVICE', label: '设备' },
                { value: 'IMPL_EVENT', label: '事件' },
                { value: 'IMPL_SERVICE', label: '服务' },
                { value: 'IMPL_AGENT', label: 'Agent' },
                { value: 'IMPL_ROUTE', label: '路由' },
                { value: 'IMPL_BLOCK', label: '块活动' }
            ]},
            
            { type: 'section', title: '时限配置' },
            { name: 'limit', type: 'number', label: '时限', min: 0, unit: '小时' },
            { name: 'durationUnit', type: 'select', label: '时长单位', options: [
                { value: 'HOUR', label: '小时' },
                { value: 'DAY', label: '天' },
                { value: 'WEEK', label: '周' },
                { value: 'MONTH', label: '月' }
            ]},
            
            { type: 'section', title: '流程控制' },
            { name: 'join', type: 'select', label: '汇聚类型', options: [
                { value: 'JOIN_EXCLUSIVE', label: '排他汇聚' },
                { value: 'JOIN_PARALLEL', label: '并行汇聚' },
                { value: 'JOIN_INCLUSIVE', label: '包含汇聚' }
            ]},
            { name: 'split', type: 'select', label: '分支类型', options: [
                { value: 'SPLIT_EXCLUSIVE', label: '排他分支' },
                { value: 'SPLIT_PARALLEL', label: '并行分支' },
                { value: 'SPLIT_INCLUSIVE', label: '包含分支' }
            ]},
            { name: 'startMode', type: 'select', label: '启动模式', options: [
                { value: 'MANUAL', label: '手动' },
                { value: 'AUTOMATIC', label: '自动' }
            ]},
            { name: 'finishMode', type: 'select', label: '完成模式', options: [
                { value: 'MANUAL', label: '手动' },
                { value: 'AUTOMATIC', label: '自动' }
            ]},
            
            { type: 'section', title: '标识和锁定' },
            { name: 'mark', type: 'select', label: '标识类型', options: [
                { value: 'NONE', label: '无' },
                { value: 'ACTIVITY', label: '活动标识' },
                { value: 'INSTANCE', label: '实例标识' }
            ]},
            { name: 'lock', type: 'select', label: '锁定策略', options: [
                { value: 'NONE', label: '无锁定' },
                { value: 'PESSIMISTIC', label: '悲观锁' },
                { value: 'OPTIMISTIC', label: '乐观锁' }
            ]}
        ];

        if (impl === 'IMPL_TOOL') {
            fields.push(
                { type: 'section', title: '工具配置' },
                { name: 'toolId', type: 'text', label: '工具ID' },
                { name: 'toolName', type: 'text', label: '工具名称' },
                { name: 'toolType', type: 'select', label: '工具类型', options: [
                    { value: 'APPLICATION', label: '应用程序' },
                    { value: 'PROCEDURE', label: '过程' }
                ]},
                { name: 'actualParameters', type: 'list', label: '参数列表', addText: '添加参数', fields: [
                    { name: 'name', label: '参数名' },
                    { name: 'value', label: '参数值' }
                ]}
            );
        }

        if (impl === 'IMPL_SUBFLOW') {
            fields.push(
                { type: 'section', title: '子流程配置' },
                { name: 'subflowProcessId', type: 'text', label: '子流程ID' },
                { name: 'subflowName', type: 'text', label: '子流程名称' },
                { name: 'subflowVersion', type: 'text', label: '子流程版本' },
                { name: 'execution', type: 'select', label: '执行方式', options: [
                    { value: 'SYNCHRONOUS', label: '同步' },
                    { value: 'ASYNCHRONOUS', label: '异步' }
                ]}
            );
        }

        return fields;
    },

    _getPermissionFields: function(impl) {
        const fields = [
            { type: 'section', title: '办理类型配置' },
            { name: 'rightConfig.performType', type: 'select', label: '办理类型', options: [
                { value: 'SINGLE', label: '单人办理' },
                { value: 'MULTIPLE', label: '多人办理' },
                { value: 'JOINTSIGN', label: '会签' },
                { value: 'NEEDNOTSELECT', label: '无需选择' },
                { value: 'NOSELECT', label: '不选择' },
                { value: 'DEFAULT', label: '默认' }
            ]},
            { name: 'rightConfig.performSequence', type: 'select', label: '办理顺序', options: [
                { value: 'FIRST', label: '第一人办理' },
                { value: 'SEQUENCE', label: '顺序办理' },
                { value: 'MEANWHILE', label: '同时办理' },
                { value: 'AUTOSIGN', label: '自动签收' },
                { value: 'DEFAULT', label: '默认' }
            ]},
            
            { type: 'section', title: '办理人配置' },
            { name: 'rightConfig.performerSelectedId', type: 'text', label: '办理人公式ID' },
            { name: 'rightConfig.performerSelectedAtt.formula', type: 'textarea', label: '办理人公式' },
            { name: 'rightConfig.performerSelectedAtt.formulaType', type: 'select', label: '公式类型', options: [
                { value: 'EXPRESSION', label: '表达式' },
                { value: 'SCRIPT', label: '脚本' },
                { value: 'RULE', label: '规则' }
            ]},
            
            { type: 'section', title: '阅办人配置' },
            { name: 'rightConfig.readerSelectedId', type: 'text', label: '阅办人公式ID' },
            { name: 'rightConfig.readerSelectedAtt.formula', type: 'textarea', label: '阅办人公式' },
            { name: 'rightConfig.readerSelectedAtt.formulaType', type: 'select', label: '公式类型', options: [
                { value: 'EXPRESSION', label: '表达式' },
                { value: 'SCRIPT', label: '脚本' },
                { value: 'RULE', label: '规则' }
            ]},
            
            { type: 'section', title: '代签人配置' },
            { name: 'rightConfig.canInsteadSign', type: 'checkbox', label: '是否允许代签' },
            { name: 'rightConfig.insteadSignSelectedId', type: 'text', label: '代签人公式ID' },
            { name: 'rightConfig.insteadSignSelectedAtt.formula', type: 'textarea', label: '代签人公式' },
            { name: 'rightConfig.insteadSignSelectedAtt.formulaType', type: 'select', label: '公式类型', options: [
                { value: 'EXPRESSION', label: '表达式' },
                { value: 'SCRIPT', label: '脚本' },
                { value: 'RULE', label: '规则' }
            ]},
            
            { type: 'section', title: '代理配置' },
            { name: 'rightConfig.surrogateId', type: 'text', label: '代理人ID' },
            { name: 'rightConfig.surrogateName', type: 'text', label: '代理人名称' },
            
            { type: 'section', title: '路由配置' },
            { name: 'rightConfig.canRouteBack', type: 'checkbox', label: '是否允许退回' },
            { name: 'rightConfig.routeBackMethod', type: 'select', label: '退回路径', options: [
                { value: 'DEFAULT', label: '默认' },
                { value: 'LAST', label: '上一环节' },
                { value: 'ANY', label: '任意环节' },
                { value: 'SPECIFY', label: '指定环节' }
            ]},
            { name: 'rightConfig.canSpecialSend', type: 'checkbox', label: '是否允许特送' },
            { name: 'rightConfig.specialSendScope', type: 'select', label: '特送范围', options: [
                { value: 'DEFAULT', label: '默认' },
                { value: 'ALL', label: '所有人' },
                { value: 'PERFORMERS', label: '办理人' }
            ]},
            { name: 'rightConfig.canReSend', type: 'checkbox', label: '是否允许补发' },
            { name: 'rightConfig.canTakeBack', type: 'checkbox', label: '是否允许收回' },
            
            { type: 'section', title: '权限转移配置' },
            { name: 'rightConfig.movePerformerTo', type: 'select', label: '办理后权限转移', options: [
                { value: 'PERFORMER', label: '办理人' },
                { value: 'SPONSOR', label: '发起人' },
                { value: 'READER', label: '阅办人' },
                { value: 'HISTORYPERFORMER', label: '历史办理人' },
                { value: 'HISSPONSOR', label: '历史发起人' },
                { value: 'HISTORYREADER', label: '历史阅办人' },
                { value: 'NORIGHT', label: '无权限' },
                { value: 'NULL', label: '访客组' }
            ]},
            { name: 'rightConfig.moveReaderTo', type: 'select', label: '阅办后权限转移', options: [
                { value: 'PERFORMER', label: '办理人' },
                { value: 'SPONSOR', label: '发起人' },
                { value: 'READER', label: '阅办人' },
                { value: 'HISTORYPERFORMER', label: '历史办理人' },
                { value: 'HISSPONSOR', label: '历史发起人' },
                { value: 'HISTORYREADER', label: '历史阅办人' },
                { value: 'NORIGHT', label: '无权限' },
                { value: 'NULL', label: '访客组' }
            ]},
            { name: 'rightConfig.moveSponsorTo', type: 'select', label: '发送人权限转移', options: [
                { value: 'PERFORMER', label: '办理人' },
                { value: 'SPONSOR', label: '发起人' },
                { value: 'READER', label: '阅办人' },
                { value: 'HISTORYPERFORMER', label: '历史办理人' },
                { value: 'HISSPONSOR', label: '历史发起人' },
                { value: 'HISTORYREADER', label: '历史阅办人' },
                { value: 'NORIGHT', label: '无权限' },
                { value: 'NULL', label: '访客组' }
            ]},
            
            { type: 'section', title: '到期处理配置' },
            { name: 'timing.deadlineOperation', type: 'select', label: '到期处理', options: [
                { value: 'DEFAULT', label: '默认处理' },
                { value: 'DELAY', label: '延期处理' },
                { value: 'TAKEBACK', label: '收回处理' },
                { value: 'SURROGATE', label: '代理处理' }
            ]},
            
            { type: 'section', title: '其他权限设置' },
            { name: 'priority', type: 'number', label: '优先级', min: 0, max: 100 },
            { name: 'skipable', type: 'checkbox', label: '可跳过' },
            { name: 'allowDelegate', type: 'checkbox', label: '允许委托' },
            { name: 'allowTransfer', type: 'checkbox', label: '允许转办' },
            { name: 'allowReject', type: 'checkbox', label: '允许驳回' },
            { name: 'allowWithdraw', type: 'checkbox', label: '允许撤回' }
        ];

        return fields;
    },

    _getAgentFields: function() {
        return [
            { type: 'section', title: '基本配置' },
            { name: 'agentId', type: 'text', label: 'Agent ID', readonly: true },
            { name: 'agentName', type: 'text', label: 'Agent名称', required: true },
            { name: 'agentType', type: 'select', label: 'Agent类型', options: [
                { value: 'LLM_AGENT', label: 'LLM Agent' },
                { value: 'TASK_AGENT', label: 'Task Agent' },
                { value: 'DATA_AGENT', label: 'Data Agent' },
                { value: 'COORDINATOR', label: 'Coordinator' }
            ]},
            { name: 'status', type: 'select', label: '状态', options: [
                { value: 'online', label: '在线' },
                { value: 'offline', label: '离线' },
                { value: 'busy', label: '忙碌' },
                { value: 'error', label: '错误' }
            ]},
            { name: 'role', type: 'select', label: '角色', options: [
                { value: 'worker', label: '工作者' },
                { value: 'coordinator', label: '协调者' },
                { value: 'supervisor', label: '监督者' }
            ]},
            { name: 'version', type: 'text', label: '版本' },
            
            { type: 'section', title: '网络配置' },
            { name: 'ipAddress', type: 'text', label: 'IP地址' },
            { name: 'port', type: 'number', label: '端口', min: 1, max: 65535 },
            { name: 'clusterId', type: 'text', label: '集群ID' },
            { name: 'sceneGroupId', type: 'text', label: '场景组ID' },
            
            { type: 'section', title: 'LLM配置' },
            { name: 'llmProvider', type: 'select', label: 'LLM提供商', options: [
                { value: 'OPENAI', label: 'OpenAI' },
                { value: 'QIANWEN', label: '千问' },
                { value: 'OLLAMA', label: 'Ollama' },
                { value: 'DEEPSEEK', label: 'DeepSeek' }
            ]},
            { name: 'model', type: 'text', label: '模型名称' },
            { name: 'temperature', type: 'number', label: '温度', min: 0, max: 2, step: 0.1 },
            { name: 'maxTokens', type: 'number', label: '最大Token数', min: 1 },
            { name: 'systemPrompt', type: 'textarea', label: '系统提示词' },
            { name: 'userPrompt', type: 'textarea', label: '用户提示词' },
            
            { type: 'section', title: '性能监控' },
            { name: 'maxConcurrency', type: 'number', label: '最大并发数', min: 1, max: 100 },
            { name: 'currentLoad', type: 'number', label: '当前负载', readonly: true },
            { name: 'cpuUsage', type: 'number', label: 'CPU使用率(%)', min: 0, max: 100, readonly: true },
            { name: 'memoryUsage', type: 'number', label: '内存使用率(%)', min: 0, max: 100, readonly: true },
            { name: 'healthStatus', type: 'select', label: '健康状态', options: [
                { value: 'healthy', label: '健康' },
                { value: 'warning', label: '警告' },
                { value: 'critical', label: '严重' },
                { value: 'unknown', label: '未知' }
            ]},
            
            { type: 'section', title: '能力和标签' },
            { name: 'capabilities', type: 'list', label: '能力列表', addText: '添加能力', fields: [
                { name: 'name', label: '能力名称' },
                { name: 'level', label: '能力等级' }
            ]},
            { name: 'tags', type: 'keyvalue', label: '标签', addText: '添加标签' },
            { name: 'extendedConfig', type: 'textarea', label: '扩展配置(JSON)' }
        ];
    },

    _getCapabilityFields: function() {
        return [
            { type: 'section', title: '基本配置' },
            { name: 'capabilityConfig.capabilityId', type: 'text', label: '能力ID', readonly: true },
            { name: 'capabilityConfig.name', type: 'text', label: '能力名称', required: true },
            { name: 'capabilityConfig.description', type: 'textarea', label: '能力描述' },
            { name: 'capabilityConfig.capabilityType', type: 'select', label: '能力类型', options: [
                { value: 'SERVICE', label: '服务能力' },
                { value: 'DRIVER', label: '驱动能力' },
                { value: 'SCENE', label: '场景能力' },
                { value: 'TRIGGER', label: '触发器能力' }
            ]},
            { name: 'capabilityConfig.version', type: 'text', label: '版本' },
            { name: 'capabilityConfig.icon', type: 'text', label: '图标' },
            
            { type: 'section', title: '访问控制' },
            { name: 'capabilityConfig.accessLevel', type: 'select', label: '访问级别', options: [
                { value: 'PUBLIC', label: '公开' },
                { value: 'SCENE', label: '场景级' },
                { value: 'INTERNAL', label: '内部' }
            ]},
            { name: 'capabilityConfig.visibility', type: 'select', label: '可见性', options: [
                { value: 'PUBLIC', label: '公开' },
                { value: 'INTERNAL', label: '内部' },
                { value: 'DEVELOPER', label: '开发者' }
            ]},
            { name: 'capabilityConfig.ownerId', type: 'text', label: '所有者ID' },
            
            { type: 'section', title: '场景支持' },
            { name: 'capabilityConfig.supportedSceneTypes', type: 'list', label: '支持的场景类型', addText: '添加场景类型', fields: [
                { name: 'type', label: '场景类型' }
            ]},
            { name: 'capabilityConfig.sceneType', type: 'select', label: '场景类型', options: [
                { value: 'AUTO', label: '自动' },
                { value: 'MANUAL', label: '手动' }
            ]},
            { name: 'capabilityConfig.dynamicSceneTypes', type: 'checkbox', label: '动态场景类型' },
            
            { type: 'section', title: '连接配置' },
            { name: 'capabilityConfig.connectorType', type: 'select', label: '连接器类型', options: [
                { value: 'REST', label: 'REST API' },
                { value: 'GRPC', label: 'gRPC' },
                { value: 'WEBSOCKET', label: 'WebSocket' },
                { value: 'MQTT', label: 'MQTT' }
            ]},
            { name: 'capabilityConfig.endpoint', type: 'text', label: '端点' },
            { name: 'capabilityConfig.parameters', type: 'list', label: '参数定义', addText: '添加参数', fields: [
                { name: 'name', label: '参数名' },
                { name: 'type', label: '类型', type: 'select', options: [
                    { value: 'STRING', label: '字符串' },
                    { value: 'NUMBER', label: '数字' },
                    { value: 'BOOLEAN', label: '布尔' },
                    { value: 'OBJECT', label: '对象' }
                ]},
                { name: 'required', label: '必需', type: 'checkbox' },
                { name: 'defaultValue', label: '默认值' },
                { name: 'description', label: '描述' }
            ]},
            { name: 'capabilityConfig.returns.type', type: 'select', label: '返回类型', options: [
                { value: 'STRING', label: '字符串' },
                { value: 'NUMBER', label: '数字' },
                { value: 'BOOLEAN', label: '布尔' },
                { value: 'OBJECT', label: '对象' }
            ]},
            
            { type: 'section', title: '状态配置' },
            { name: 'capabilityConfig.status', type: 'select', label: '状态', options: [
                { value: 'REGISTERED', label: '已注册' },
                { value: 'ENABLED', label: '已启用' },
                { value: 'DISABLED', label: '已禁用' },
                { value: 'ERROR', label: '错误' }
            ]},
            { name: 'capabilityConfig.skillId', type: 'text', label: '技能ID' },
            { name: 'capabilityConfig.createTime', type: 'text', label: '创建时间', readonly: true },
            { name: 'capabilityConfig.updateTime', type: 'text', label: '更新时间', readonly: true },
            
            { type: 'section', title: '能力组合' },
            { name: 'capabilityConfig.mainFirst', type: 'checkbox', label: '主要优先' },
            { name: 'capabilityConfig.capabilities', type: 'list', label: '能力列表', addText: '添加能力', fields: [
                { name: 'capabilityId', label: '能力ID' }
            ]},
            { name: 'capabilityConfig.dependencies', type: 'list', label: '依赖', addText: '添加依赖', fields: [
                { name: 'dependency', label: '依赖项' }
            ]},
            { name: 'capabilityConfig.optionalCapabilities', type: 'list', label: '可选能力', addText: '添加可选能力', fields: [
                { name: 'capabilityId', label: '能力ID' }
            ]},
            { name: 'capabilityConfig.collaborativeCapabilities', type: 'list', label: '协作能力', addText: '添加协作能力', fields: [
                { name: 'capabilityId', label: '能力ID' },
                { name: 'required', label: '必需', type: 'checkbox' }
            ]},
            
            { type: 'section', title: '驱动配置' },
            { name: 'capabilityConfig.driverType', type: 'select', label: '驱动类型', options: [
                { value: 'NONE', label: '无' },
                { value: 'TRIGGER', label: '触发器' },
                { value: 'SCHEDULE', label: '定时任务' },
                { value: 'EVENT', label: '事件驱动' }
            ]},
            { name: 'capabilityConfig.driverConditions', type: 'list', label: '驱动条件', addText: '添加驱动条件', fields: [
                { name: 'type', label: '类型' },
                { name: 'value', label: '值' }
            ]},
            
            { type: 'section', title: '分类配置' },
            { name: 'capabilityConfig.skillForm', type: 'select', label: '技能形式', options: [
                { value: 'PROVIDER', label: '提供者' },
                { value: 'SCENE', label: '场景' }
            ]},
            { name: 'capabilityConfig.capabilityCategory', type: 'select', label: '能力分类', options: [
                { value: 'BUSINESS', label: '业务能力' },
                { value: 'SYSTEM', label: '系统能力' },
                { value: 'INFRASTRUCTURE', label: '基础设施能力' }
            ]},
            { name: 'capabilityConfig.businessCategory', type: 'text', label: '业务分类' },
            { name: 'capabilityConfig.subCategory', type: 'text', label: '子分类' },
            { name: 'capabilityConfig.tags', type: 'list', label: '标签', addText: '添加标签', fields: [
                { name: 'tag', label: '标签' }
            ]},
            
            { type: 'section', title: '地址配置' },
            { name: 'capabilityConfig.requiredAddresses', type: 'list', label: '必需地址', addText: '添加必需地址', fields: [
                { name: 'type', label: '类型' },
                { name: 'address', label: '地址' }
            ]},
            { name: 'capabilityConfig.optionalAddresses', type: 'list', label: '可选地址', addText: '添加可选地址', fields: [
                { name: 'type', label: '类型' },
                { name: 'address', label: '地址' }
            ]},
            
            { type: 'section', title: '参与者配置' },
            { name: 'capabilityConfig.participants', type: 'list', label: '参与者', addText: '添加参与者', fields: [
                { name: 'role', label: '角色' },
                { name: 'name', label: '名称' },
                { name: 'userId', label: '用户ID' },
                { name: 'permissions', label: '权限' }
            ]},
            
            { type: 'section', title: '其他配置' },
            { name: 'capabilityConfig.parentSkill', type: 'text', label: '父技能' },
            { name: 'capabilityConfig.parentScene', type: 'text', label: '父场景' },
            { name: 'capabilityConfig.installed', type: 'checkbox', label: '已安装' },
            { name: 'capabilityConfig.businessSemanticsScore', type: 'number', label: '业务语义分数', min: 0, max: 100 },
            { name: 'capabilityConfig.metadata', type: 'keyvalue', label: '元数据', addText: '添加元数据' }
        ];
    }
};

window.PanelSchema = PanelSchema;
