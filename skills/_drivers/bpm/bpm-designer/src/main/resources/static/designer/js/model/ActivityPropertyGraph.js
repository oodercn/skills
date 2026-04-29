/**
 * 活动属性知识图谱 - Activity Property Knowledge Graph
 * 
 * 定义活动(Activity)节点的完整属性结构，采用树形层级组织
 * 顶级分类：基本信息、执行配置、代理配置
 * 根据节点类型(人工/LLM/任务/事件/混合)进行属性过滤展示
 */

const ActivityPropertyGraph = {
    
    // ==================== 顶级分类定义 ====================
    topCategories: {
        basic: {
            id: 'basic',
            name: '基本信息',
            icon: 'info',
            description: '节点的基本描述和分类信息',
            order: 1
        },
        execution: {
            id: 'execution',
            name: '执行配置',
            icon: 'play',
            description: '节点执行时的控制和处理配置',
            order: 2
        },
        agent: {
            id: 'agent',
            name: '代理配置',
            icon: 'robot',
            description: 'Agent相关的技能和参数配置',
            order: 3
        }
    },

    // ==================== 属性树形结构 ====================
    propertyTree: {
        
        // ===== 基本信息 (Basic Info) =====
        basic: {
            // 1.1 基础标识
            identity: {
                name: '基础标识',
                order: 1,
                properties: [
                    { 
                        key: 'activityDefId', 
                        name: '节点ID', 
                        type: 'text', 
                        readonly: true,
                        description: '系统自动生成的唯一标识'
                    },
                    { 
                        key: 'name', 
                        name: '节点名称', 
                        type: 'text', 
                        required: true,
                        description: '节点的显示名称'
                    },
                    { 
                        key: 'description', 
                        name: '节点描述', 
                        type: 'textarea',
                        description: '节点的详细说明'
                    }
                ]
            },
            
            // 1.2 三维度分类 (所有ACTIVITY类型节点)
            classification: {
                name: '三维度分类',
                order: 2,
                showWhen: { nodeType: ['ACTIVITY', 'NESTING'] },
                properties: [
                    { 
                        key: 'form', 
                        name: '形态', 
                        type: 'select',
                        options: [
                            { value: 'STANDALONE', label: '独立技能', description: '独立的技能执行单元' },
                            { value: 'SCENE', label: '场景', description: '包含独立上下文的业务场景' }
                        ],
                        description: '技能的组织形态'
                    },
                    { 
                        key: 'category', 
                        name: '功能分类', 
                        type: 'select',
                        options: [
                            { value: 'LLM', label: 'LLM', description: '大语言模型相关' },
                            { value: 'FORM', label: '表单', description: '表单交互相关' },
                            { value: 'SERVICE', label: '服务', description: '业务服务调用' },
                            { value: 'WORKFLOW', label: '工作流', description: '流程编排相关' },
                            { value: 'KNOWLEDGE', label: '知识', description: '知识库相关' },
                            { value: 'DATA', label: '数据', description: '数据存储相关' },
                            { value: 'COMM', label: '通讯', description: '消息通讯相关' },
                            { value: 'TOOL', label: '工具', description: '工具调用相关' }
                        ],
                        description: '技能的功能类别'
                    },
                    { 
                        key: 'provider', 
                        name: '提供者', 
                        type: 'select',
                        options: [
                            { value: 'SYSTEM', label: '系统', description: '系统内置' },
                            { value: 'DRIVER', label: '驱动', description: '驱动层提供' },
                            { value: 'BUSINESS', label: '业务', description: '业务层定义' },
                            { value: 'USER', label: '用户', description: '用户自定义' }
                        ],
                        description: '技能的提供来源'
                    }
                ]
            },
            
            // 1.3 执行者定义 (仅ACTIVITY类型)
            performer: {
                name: '执行者',
                order: 3,
                showWhen: { nodeType: ['ACTIVITY'] },
                properties: [
                    { 
                        key: 'performerType', 
                        name: '执行者类型', 
                        type: 'select',
                        options: [
                            { value: 'HUMAN', label: '人工', description: '由真人执行' },
                            { value: 'AGENT', label: 'Agent', description: '由智能体执行' },
                            { value: 'SYSTEM', label: '系统', description: '由系统自动执行' }
                        ],
                        description: '任务的执行主体类型'
                    },
                    { 
                        key: 'performerId', 
                        name: '执行者标识', 
                        type: 'text',
                        description: '具体的执行者ID或角色'
                    },
                    { 
                        key: 'performerName', 
                        name: '执行者名称', 
                        type: 'text',
                        description: '执行者的显示名称'
                    }
                ]
            },
            
            // 1.4 上下文隔离 (仅NESTING类型)
            contextIsolation: {
                name: '上下文隔离',
                order: 4,
                showWhen: { nodeType: ['NESTING'] },
                properties: [
                    { 
                        key: 'isolationLevel', 
                        name: '隔离级别', 
                        type: 'select',
                        options: [
                            { value: 'SHARED', label: '共享', description: '完全共享父上下文' },
                            { value: 'PARTIAL', label: '部分', description: '部分隔离，选择性继承' },
                            { value: 'ISOLATED', label: '完全隔离', description: '完全独立的上下文' }
                        ],
                        description: '子流程/场景的上下文隔离级别'
                    },
                    { 
                        key: 'inheritVariables', 
                        name: '继承变量', 
                        type: 'boolean',
                        description: '是否继承父流程变量'
                    },
                    { 
                        key: 'inheritFormData', 
                        name: '继承表单数据', 
                        type: 'boolean',
                        description: '是否继承父流程表单数据'
                    }
                ]
            }
        },

        // ===== 执行配置 (Execution Config) =====
        execution: {
            // 2.1 执行控制
            control: {
                name: '执行控制',
                order: 1,
                showWhen: { nodeType: ['ACTIVITY'] },
                properties: [
                    { 
                        key: 'async', 
                        name: '异步执行', 
                        type: 'boolean',
                        description: '是否异步执行该节点'
                    },
                    { 
                        key: 'timeout', 
                        name: '超时时间(ms)', 
                        type: 'number',
                        description: '节点执行的超时时间，0表示不超时'
                    },
                    { 
                        key: 'retryCount', 
                        name: '重试次数', 
                        type: 'number',
                        description: '执行失败时的重试次数'
                    },
                    { 
                        key: 'retryInterval', 
                        name: '重试间隔(ms)', 
                        type: 'number',
                        description: '每次重试之间的间隔时间'
                    }
                ]
            },
            
            // 2.2 错误处理
            errorHandling: {
                name: '错误处理',
                order: 2,
                showWhen: { nodeType: ['ACTIVITY'] },
                properties: [
                    { 
                        key: 'onError', 
                        name: '错误处理策略', 
                        type: 'select',
                        options: [
                            { value: 'STOP', label: '停止', description: '终止流程执行' },
                            { value: 'CONTINUE', label: '继续', description: '忽略错误继续执行' },
                            { value: 'RETRY', label: '重试', description: '重试执行' },
                            { value: 'FALLBACK', label: '回退', description: '执行回退逻辑' },
                            { value: 'SKIP', label: '跳过', description: '跳过当前节点' }
                        ],
                        description: '执行出错时的处理策略'
                    },
                    { 
                        key: 'fallbackActivity', 
                        name: '回退节点', 
                        type: 'text',
                        description: '错误回退时跳转的节点ID'
                    },
                    { 
                        key: 'errorHandler', 
                        name: '错误处理器', 
                        type: 'text',
                        description: '自定义错误处理逻辑'
                    }
                ]
            },
            
            // 2.3 触发条件
            trigger: {
                name: '触发条件',
                order: 3,
                showWhen: { nodeType: ['ACTIVITY'] },
                properties: [
                    { 
                        key: 'condition', 
                        name: '执行条件', 
                        type: 'textarea',
                        description: 'OGNL表达式，满足条件才执行'
                    },
                    { 
                        key: 'skipCondition', 
                        name: '跳过条件', 
                        type: 'textarea',
                        description: '满足条件时跳过此节点'
                    },
                    { 
                        key: 'priority', 
                        name: '优先级', 
                        type: 'number',
                        description: '节点执行优先级，数字越小优先级越高'
                    }
                ]
            },
            
            // 2.4 多实例配置
            multiInstance: {
                name: '多实例配置',
                order: 4,
                showWhen: { nodeType: ['ACTIVITY'] },
                properties: [
                    { 
                        key: 'multiInstance', 
                        name: '启用多实例', 
                        type: 'boolean',
                        description: '是否为多实例节点'
                    },
                    { 
                        key: 'collection', 
                        name: '集合表达式', 
                        type: 'text',
                        description: '遍历的集合变量名'
                    },
                    { 
                        key: 'elementVariable', 
                        name: '元素变量', 
                        type: 'text',
                        description: '集合中每个元素的变量名'
                    },
                    { 
                        key: 'completionCondition', 
                        name: '完成条件', 
                        type: 'textarea',
                        description: '多实例完成的条件表达式'
                    },
                    { 
                        key: 'parallel', 
                        name: '并行执行', 
                        type: 'boolean',
                        description: '是否并行执行多实例'
                    }
                ]
            },
            
            // 2.5 历史记录
            history: {
                name: '历史记录',
                order: 5,
                properties: [
                    { 
                        key: 'enableAudit', 
                        name: '启用审计', 
                        type: 'boolean',
                        description: '是否记录审计日志'
                    },
                    { 
                        key: 'retentionDays', 
                        name: '保留天数', 
                        type: 'number',
                        description: '历史记录保留天数'
                    },
                    { 
                        key: 'logLevel', 
                        name: '日志级别', 
                        type: 'select',
                        options: [
                            { value: 'DEBUG', label: '调试' },
                            { value: 'INFO', label: '信息' },
                            { value: 'WARN', label: '警告' },
                            { value: 'ERROR', label: '错误' }
                        ],
                        description: '节点执行日志级别'
                    }
                ]
            }
        },

        // ===== 代理配置 (Agent Config) =====
        agent: {
            // 3.1 技能绑定 (所有Agent类型)
            skill: {
                name: '技能绑定',
                order: 1,
                showWhen: { performerType: ['AGENT', 'HUMAN'] },
                properties: [
                    { 
                        key: 'skillId', 
                        name: '技能ID', 
                        type: 'text',
                        description: '绑定的技能标识'
                    },
                    { 
                        key: 'skillName', 
                        name: '技能名称', 
                        type: 'text',
                        description: '技能的显示名称'
                    },
                    { 
                        key: 'skillVersion', 
                        name: '技能版本', 
                        type: 'text',
                        description: '技能的版本号'
                    },
                    { 
                        key: 'skillParams', 
                        name: '技能参数', 
                        type: 'json',
                        description: '技能执行参数(JSON格式)'
                    }
                ]
            },
            
            // 3.2 LLM配置 (仅LLM Agent)
            llm: {
                name: 'LLM配置',
                order: 2,
                showWhen: { agentType: ['LLM'] },
                properties: [
                    { 
                        key: 'model', 
                        name: '模型', 
                        type: 'select',
                        options: [
                            { value: 'gpt-4', label: 'GPT-4' },
                            { value: 'gpt-3.5', label: 'GPT-3.5' },
                            { value: 'claude', label: 'Claude' },
                            { value: 'local', label: '本地模型' }
                        ],
                        description: '使用的LLM模型'
                    },
                    { 
                        key: 'temperature', 
                        name: '温度', 
                        type: 'number',
                        description: '生成随机性(0-2)'
                    },
                    { 
                        key: 'maxTokens', 
                        name: '最大Token', 
                        type: 'number',
                        description: '最大生成Token数'
                    },
                    { 
                        key: 'systemPrompt', 
                        name: '系统提示词', 
                        type: 'textarea',
                        description: '系统级提示词'
                    },
                    { 
                        key: 'userPrompt', 
                        name: '用户提示词', 
                        type: 'textarea',
                        description: '用户提示词模板'
                    },
                    { 
                        key: 'contextWindow', 
                        name: '上下文窗口', 
                        type: 'number',
                        description: '保留的上下文轮数'
                    }
                ]
            },
            
            // 3.3 知识库配置 (LLM/混合 Agent)
            knowledge: {
                name: '知识库配置',
                order: 3,
                showWhen: { agentType: ['LLM', 'HYBRID'] },
                properties: [
                    { 
                        key: 'knowledgeBaseId', 
                        name: '知识库ID', 
                        type: 'text',
                        description: '关联的知识库标识'
                    },
                    { 
                        key: 'retrievalCount', 
                        name: '检索数量', 
                        type: 'number',
                        description: '检索返回的文档数量'
                    },
                    { 
                        key: 'similarityThreshold', 
                        name: '相似度阈值', 
                        type: 'number',
                        description: '文档匹配的最小相似度(0-1)'
                    },
                    { 
                        key: 'rerankEnabled', 
                        name: '启用重排序', 
                        type: 'boolean',
                        description: '是否对检索结果重排序'
                    }
                ]
            },
            
            // 3.4 工具配置 (任务/混合 Agent)
            tools: {
                name: '工具配置',
                order: 4,
                showWhen: { agentType: ['TASK', 'HYBRID'] },
                properties: [
                    { 
                        key: 'tools', 
                        name: '工具列表', 
                        type: 'multiselect',
                        description: '可用的工具列表'
                    },
                    { 
                        key: 'toolTimeout', 
                        name: '工具超时(ms)', 
                        type: 'number',
                        description: '工具调用的超时时间'
                    },
                    { 
                        key: 'autoToolSelect', 
                        name: '自动工具选择', 
                        type: 'boolean',
                        description: '是否自动选择合适的工具'
                    }
                ]
            },
            
            // 3.5 事件配置 (事件 Agent)
            event: {
                name: '事件配置',
                order: 5,
                showWhen: { agentType: ['EVENT'] },
                properties: [
                    { 
                        key: 'eventType', 
                        name: '事件类型', 
                        type: 'select',
                        options: [
                            { value: 'MESSAGE', label: '消息' },
                            { value: 'SIGNAL', label: '信号' },
                            { value: 'TIMER', label: '定时器' },
                            { value: 'CONDITIONAL', label: '条件' }
                        ],
                        description: '监听的事件类型'
                    },
                    { 
                        key: 'eventName', 
                        name: '事件名称', 
                        type: 'text',
                        description: '具体的事件标识'
                    },
                    { 
                        key: 'eventFilter', 
                        name: '事件过滤', 
                        type: 'textarea',
                        description: '事件过滤条件'
                    }
                ]
            },
            
            // 3.6 输入输出映射
            ioMapping: {
                name: '输入输出映射',
                order: 6,
                showWhen: { nodeType: ['ACTIVITY'] },
                properties: [
                    { 
                        key: 'inputMapping', 
                        name: '输入映射', 
                        type: 'json',
                        description: '输入参数映射规则'
                    },
                    { 
                        key: 'outputMapping', 
                        name: '输出映射', 
                        type: 'json',
                        description: '输出结果映射规则'
                    },
                    { 
                        key: 'resultVariable', 
                        name: '结果变量', 
                        type: 'text',
                        description: '存储结果的变量名'
                    }
                ]
            },
            
            // 3.7 表单配置 (人工任务)
            form: {
                name: '表单配置',
                order: 7,
                showWhen: { performerType: ['HUMAN'] },
                properties: [
                    { 
                        key: 'formId', 
                        name: '表单ID', 
                        type: 'text',
                        description: '关联的表单标识'
                    },
                    { 
                        key: 'formUrl', 
                        name: '表单URL', 
                        type: 'text',
                        description: '表单访问地址'
                    },
                    { 
                        key: 'formData', 
                        name: '表单数据', 
                        type: 'json',
                        description: '表单初始数据'
                    }
                ]
            }
        }
    },

    // ==================== 节点类型过滤器 ====================
    nodeTypeFilters: {
        // 控制节点 (START/END)
        CONTROL: {
            basic: ['identity'],
            execution: [],
            agent: []
        },
        
        // 人工任务
        HUMAN_TASK: {
            basic: ['identity', 'classification', 'performer'],
            execution: ['control', 'errorHandling', 'trigger', 'history'],
            agent: ['skill', 'form', 'ioMapping']
        },
        
        // LLM Agent
        AGENT_LLM: {
            basic: ['identity', 'classification', 'performer'],
            execution: ['control', 'errorHandling', 'trigger', 'multiInstance', 'history'],
            agent: ['skill', 'llm', 'knowledge', 'ioMapping']
        },
        
        // 任务 Agent
        AGENT_TASK: {
            basic: ['identity', 'classification', 'performer'],
            execution: ['control', 'errorHandling', 'trigger', 'multiInstance', 'history'],
            agent: ['skill', 'tools', 'ioMapping']
        },
        
        // 事件 Agent
        AGENT_EVENT: {
            basic: ['identity', 'classification', 'performer'],
            execution: ['control', 'errorHandling', 'history'],
            agent: ['skill', 'event', 'ioMapping']
        },
        
        // 混合 Agent
        AGENT_HYBRID: {
            basic: ['identity', 'classification', 'performer'],
            execution: ['control', 'errorHandling', 'trigger', 'multiInstance', 'history'],
            agent: ['skill', 'llm', 'knowledge', 'tools', 'ioMapping']
        },
        
        // 子流程
        SUBFLOW: {
            basic: ['identity', 'classification', 'contextIsolation'],
            execution: ['control', 'errorHandling', 'history'],
            agent: ['ioMapping']
        },
        
        // 场景
        SCENE: {
            basic: ['identity', 'classification', 'contextIsolation'],
            execution: ['control', 'errorHandling', 'history'],
            agent: ['skill', 'ioMapping']
        },
        
        // 外部流程
        EXTERNAL: {
            basic: ['identity', 'classification', 'contextIsolation'],
            execution: ['control', 'errorHandling', 'history'],
            agent: ['ioMapping']
        }
    },

    // ==================== 方法 ====================
    
    /**
     * 获取节点的完整属性配置
     * @param {string} nodeType - 节点类型
     * @returns {Object} 过滤后的属性树
     */
    getPropertiesForNodeType(nodeType) {
        const filter = this.nodeTypeFilters[nodeType];
        if (!filter) {
            console.warn(`[ActivityPropertyGraph] Unknown node type: ${nodeType}`);
            return this.propertyTree;
        }
        
        const result = {};
        
        for (const [topCategory, sections] of Object.entries(filter)) {
            result[topCategory] = {};
            for (const sectionKey of sections) {
                if (this.propertyTree[topCategory] && this.propertyTree[topCategory][sectionKey]) {
                    result[topCategory][sectionKey] = this.propertyTree[topCategory][sectionKey];
                }
            }
        }
        
        return result;
    },
    
    /**
     * 检查属性是否适用于节点
     * @param {string} nodeType - 节点类型
     * @param {string} topCategory - 顶级分类
     * @param {string} section - 属性分组
     * @returns {boolean}
     */
    isApplicable(nodeType, topCategory, section) {
        const filter = this.nodeTypeFilters[nodeType];
        if (!filter) return false;
        
        return filter[topCategory] && filter[topCategory].includes(section);
    },
    
    /**
     * 获取所有顶级分类
     * @returns {Array}
     */
    getTopCategories() {
        return Object.values(this.topCategories).sort((a, b) => a.order - b.order);
    },
    
    /**
     * 获取属性的完整路径
     * @param {string} topCategory - 顶级分类
     * @param {string} section - 属性分组
     * @param {string} propertyKey - 属性键
     * @returns {Object|null}
     */
    getProperty(topCategory, section, propertyKey) {
        const sectionData = this.propertyTree[topCategory]?.[section];
        if (!sectionData) return null;
        
        return sectionData.properties.find(p => p.key === propertyKey) || null;
    },
    
    /**
     * 获取节点类型的显示名称
     * @param {string} nodeType 
     * @returns {string}
     */
    getNodeTypeLabel(nodeType) {
        const labels = {
            'START': '开始',
            'END': '结束',
            'HUMAN_TASK': '人工任务',
            'AGENT_LLM': 'LLM Agent',
            'AGENT_TASK': '任务 Agent',
            'AGENT_EVENT': '事件 Agent',
            'AGENT_HYBRID': '混合 Agent',
            'SUBFLOW': '子流程',
            'SCENE': '场景',
            'EXTERNAL': '外部流程'
        };
        return labels[nodeType] || nodeType;
    }
};

// 导出
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ActivityPropertyGraph;
} else {
    window.ActivityPropertyGraph = ActivityPropertyGraph;
}
