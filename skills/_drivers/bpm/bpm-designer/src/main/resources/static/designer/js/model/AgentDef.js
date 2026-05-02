class AgentDef {
    constructor(data) {
        this.agentId = data?.agentId || this._generateId();
        this.name = data?.name || '新Agent';
        this.agentType = data?.agentType || 'LLM';

        this.performType = data?.performType || 'SINGLE';
        this.performSequence = data?.performSequence || 'FIRST';
        this.canRouteBack = data?.canRouteBack ?? true;
        this.routeBackMethod = data?.routeBackMethod || 'LAST';
        this.canTakeBack = data?.canTakeBack ?? true;
        this.coordinatorId = data?.coordinatorId || null;

        this.llmProvider = data?.llmProvider || null;
        this.llmModel = data?.llmModel || null;
        this.systemPrompt = data?.systemPrompt || '';
        this.temperature = data?.temperature ?? 0.7;
        this.maxTokens = data?.maxTokens ?? 4096;

        this.mcpTools = data?.mcpTools || [];
        this.capabilities = data?.capabilities || [];

        this.coordinationStrategy = data?.coordinationStrategy || 'ROUND_ROBIN';
        this.maxConcurrentTasks = data?.maxConcurrentTasks ?? 5;
        this.escalationEnabled = data?.escalationEnabled ?? false;
        this.escalationTimeout = data?.escalationTimeout ?? 300;

        this.northbound = data?.northbound || new NorthboundConfig();
        this.southbound = data?.southbound || new SouthboundConfig();
        this.memory = data?.memory || { enabled: false, maxTokens: 4000 };

        this.agentGroup = data?.agentGroup || 'PERFORMER';
        this.agentPerformStatus = data?.agentPerformStatus || 'WAITING';
    }

    _generateId() {
        return 'agent_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    addCapability(capability) {
        if (!this.capabilities.includes(capability)) {
            this.capabilities.push(capability);
        }
    }

    removeCapability(capability) {
        this.capabilities = this.capabilities.filter(c => c !== capability);
    }

    addMcpTool(tool) {
        if (!this.mcpTools.includes(tool)) {
            this.mcpTools.push(tool);
        }
    }

    removeMcpTool(tool) {
        this.mcpTools = this.mcpTools.filter(t => t !== tool);
    }

    isCoordinator() {
        return this.agentType === 'COORDINATOR';
    }

    isLLMBased() {
        return ['LLM', 'HYBRID'].includes(this.agentType);
    }

    isToolBased() {
        return ['TASK', 'TOOL', 'HYBRID'].includes(this.agentType);
    }

    static getDefaultClassification(agentType) {
        const map = {
            'LLM':         { form: 'STANDALONE', category: 'LLM',  provider: 'SYSTEM' },
            'TASK':        { form: 'STANDALONE', category: 'SERVICE', provider: 'SYSTEM' },
            'EVENT':       { form: 'STANDALONE', category: 'COMM',  provider: 'SYSTEM' },
            'HYBRID':      { form: 'STANDALONE', category: 'TOOL',  provider: 'SYSTEM' },
            'COORDINATOR': { form: 'STANDALONE', category: 'WORKFLOW', provider: 'SYSTEM' },
            'TOOL':        { form: 'STANDALONE', category: 'TOOL',  provider: 'DRIVER' }
        };
        return map[agentType] || map['LLM'];
    }

    static getAgentTypeOptions() {
        return [
            { value: 'LLM',         label: 'LLM Agent',     description: '大语言模型智能体' },
            { value: 'TASK',        label: '任务Agent',      description: '任务执行智能体' },
            { value: 'EVENT',       label: '事件Agent',      description: '事件监听智能体' },
            { value: 'HYBRID',      label: '混合Agent',      description: 'LLM+工具混合智能体' },
            { value: 'COORDINATOR', label: '协调者Agent',    description: '多Agent协调调度' },
            { value: 'TOOL',        label: '工具Agent',      description: 'MCP工具调用智能体' }
        ];
    }

    static getCoordinationStrategyOptions() {
        return [
            { value: 'ROUND_ROBIN',      label: '轮询',       description: '按顺序轮流分配' },
            { value: 'LEAST_BUSY',       label: '最闲优先',   description: '分配给最空闲的Agent' },
            { value: 'CAPABILITY_MATCH', label: '能力匹配',   description: '根据能力匹配分配' },
            { value: 'MANUAL',           label: '手动指定',   description: '由协调者手动指定' }
        ];
    }

    static getAgentGroupOptions() {
        return [
            { value: 'PERFORMER',        label: '执行者',     description: '当前执行Agent' },
            { value: 'SPONSOR',          label: '发起者',     description: '流程发起Agent' },
            { value: 'MONITOR',          label: '监控者',     description: '监控执行Agent' },
            { value: 'COORDINATOR',      label: '协调者',     description: '协调调度Agent' },
            { value: 'HISTORYPERFORMER', label: '历史执行者', description: '历史执行Agent' },
            { value: 'HISSPONSOR',       label: '历史发起者', description: '历史发起Agent' },
            { value: 'HISTORYMONITOR',   label: '历史监控者', description: '历史监控Agent' },
            { value: 'NORIGHT',          label: '无权限',     description: '无权限Agent' }
        ];
    }

    toJSON() {
        return {
            agentId: this.agentId,
            name: this.name,
            agentType: this.agentType,
            performType: this.performType,
            performSequence: this.performSequence,
            canRouteBack: this.canRouteBack,
            routeBackMethod: this.routeBackMethod,
            canTakeBack: this.canTakeBack,
            coordinatorId: this.coordinatorId,
            llmProvider: this.llmProvider,
            llmModel: this.llmModel,
            systemPrompt: this.systemPrompt,
            temperature: this.temperature,
            maxTokens: this.maxTokens,
            mcpTools: this.mcpTools,
            capabilities: this.capabilities,
            coordinationStrategy: this.coordinationStrategy,
            maxConcurrentTasks: this.maxConcurrentTasks,
            escalationEnabled: this.escalationEnabled,
            escalationTimeout: this.escalationTimeout,
            northbound: this.northbound,
            southbound: this.southbound,
            memory: this.memory,
            agentGroup: this.agentGroup,
            agentPerformStatus: this.agentPerformStatus
        };
    }
}

class NorthboundConfig {
    constructor(data) {
        this.protocol = data?.protocol || 'A2UI';
        this.endpoints = data?.endpoints || [];
    }
}

class SouthboundConfig {
    constructor(data) {
        this.protocol = data?.protocol || 'MCP';
        this.endpoints = data?.endpoints || [];
    }
}

window.AgentDef = AgentDef;
window.NorthboundConfig = NorthboundConfig;
window.SouthboundConfig = SouthboundConfig;
