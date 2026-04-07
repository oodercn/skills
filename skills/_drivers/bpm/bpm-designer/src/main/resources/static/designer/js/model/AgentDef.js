class AgentDef {
    constructor(data) {
        this.agentId = data?.agentId || this._generateId();
        this.name = data?.name || '新Agent';
        this.agentType = data?.agentType || 'LLM';
        this.scheduleStrategy = data?.scheduleStrategy || 'SEQUENTIAL';
        this.collaborationMode = data?.collaborationMode || 'SOLO';
        this.capabilities = data?.capabilities || [];
        this.llmConfig = data?.llmConfig || new LLMConfig();
        this.northbound = data?.northbound || new NorthboundConfig();
        this.southbound = data?.southbound || new SouthboundConfig();
        this.tools = data?.tools || [];
        this.memory = data?.memory || { enabled: false, maxTokens: 4000 };
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

    toJSON() {
        return {
            agentId: this.agentId,
            name: this.name,
            agentType: this.agentType,
            scheduleStrategy: this.scheduleStrategy,
            collaborationMode: this.collaborationMode,
            capabilities: this.capabilities,
            llmConfig: this.llmConfig,
            northbound: this.northbound,
            southbound: this.southbound,
            tools: this.tools,
            memory: this.memory
        };
    }
}

class LLMConfig {
    constructor(data) {
        this.model = data?.model || 'gpt-4';
        this.temperature = data?.temperature || 0.7;
        this.maxTokens = data?.maxTokens || 2000;
        this.enableFunctionCalling = data?.enableFunctionCalling || false;
        this.enableStreaming = data?.enableStreaming || false;
        this.systemPrompt = data?.systemPrompt || '';
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
window.LLMConfig = LLMConfig;
window.NorthboundConfig = NorthboundConfig;
window.SouthboundConfig = SouthboundConfig;
