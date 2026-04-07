class SceneDef {
    constructor(data) {
        this.sceneId = data?.sceneId || this._generateId();
        this.name = data?.name || '新场景';
        this.sceneType = data?.sceneType || 'FORM';
        this.pageAgent = data?.pageAgent || new PageAgentConfig();
        this.functionCalling = data?.functionCalling || [];
        this.interactions = data?.interactions || [];
        this.storage = data?.storage || new StorageConfig();
        this.activityBlocks = data?.activityBlocks || [];
    }

    _generateId() {
        return 'scene_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    addInteraction(interaction) {
        this.interactions.push(interaction);
    }

    addFunctionCalling(fc) {
        this.functionCalling.push(fc);
    }

    toJSON() {
        return {
            sceneId: this.sceneId,
            name: this.name,
            sceneType: this.sceneType,
            pageAgent: this.pageAgent,
            functionCalling: this.functionCalling,
            interactions: this.interactions,
            storage: this.storage,
            activityBlocks: this.activityBlocks
        };
    }
}

class PageAgentConfig {
    constructor(data) {
        this.agentId = data?.agentId || '';
        this.pageId = data?.pageId || '';
        this.pageType = data?.pageType || 'form';
        this.templatePath = data?.templatePath || '';
        this.stylePath = data?.stylePath || '';
        this.scriptPath = data?.scriptPath || '';
    }
}

class StorageConfig {
    constructor(data) {
        this.type = data?.type || 'VFS';
        this.vfsPath = data?.vfsPath || '';
        this.sqlTable = data?.sqlTable || '';
    }
}

class InteractionDef {
    constructor(data) {
        this.type = data?.type || 'A2A';
        this.from = data?.from || '';
        this.to = data?.to || '';
        this.messageType = data?.messageType || 'COMMAND';
    }
}

window.SceneDef = SceneDef;
window.PageAgentConfig = PageAgentConfig;
window.StorageConfig = StorageConfig;
window.InteractionDef = InteractionDef;
