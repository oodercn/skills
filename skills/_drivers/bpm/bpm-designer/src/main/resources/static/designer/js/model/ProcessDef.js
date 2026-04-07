class ProcessDef {
    constructor(data) {
        this.processDefId = data?.processDefId || this._generateId();
        this.name = data?.name || '未命名流程';
        this.description = data?.description || '';
        this.category = data?.category || '';
        this.accessLevel = data?.accessLevel || 'INDEPENDENT';
        this.version = data?.version || 1;
        this.status = data?.status || 'DRAFT';
        
        if (data?.activities && Array.isArray(data.activities)) {
            this.activities = data.activities.map(a => {
                if (a instanceof ActivityDef) return a;
                return new ActivityDef(a);
            });
        } else {
            this.activities = [];
        }
        
        if (data?.routes && Array.isArray(data.routes)) {
            this.routes = data.routes.map(r => {
                if (r instanceof RouteDef) return r;
                return new RouteDef(r);
            });
        } else {
            this.routes = [];
        }
        
        this.listeners = data?.listeners || [];
        this.formulas = data?.formulas || [];
        this.parameters = data?.parameters || [];
        this.extendedAttributes = data?.extendedAttributes || {};
        this.agentConfig = data?.agentConfig || null;
        this.sceneConfig = data?.sceneConfig || null;
        this.createdTime = data?.createdTime || new Date().toISOString();
        this.updatedTime = data?.updatedTime || new Date().toISOString();
    }

    _generateId() {
        return 'proc_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    addActivity(activity) {
        this.activities.push(activity);
        this._updateTime();
    }

    removeActivity(activityId) {
        this.activities = this.activities.filter(a => a.activityDefId !== activityId);
        this.routes = this.routes.filter(r => r.from !== activityId && r.to !== activityId);
        this._updateTime();
    }

    getActivity(activityId) {
        return this.activities.find(a => a.activityDefId === activityId);
    }

    addRoute(route) {
        this.routes.push(route);
        this._updateTime();
    }

    removeRoute(routeId) {
        this.routes = this.routes.filter(r => r.routeDefId !== routeId);
        this._updateTime();
    }

    _updateTime() {
        this.updatedTime = new Date().toISOString();
    }

    toJSON() {
        const activities = this.activities.map(a => {
            if (a.position === 'START' || a.position === 'END' || a.activityType === 'START' || a.activityType === 'END') {
                return {
                    activityDefId: a.activityDefId,
                    name: a.name || (a.position === 'START' || a.activityType === 'START' ? '开始' : '结束'),
                    position: a.position || (a.activityType === 'START' ? 'START' : 'END'),
                    positionCoord: a.positionCoord
                };
            }
            return a.toJSON();
        });
        
        return {
            processDefId: this.processDefId,
            name: this.name,
            description: this.description,
            category: this.category,
            accessLevel: this.accessLevel,
            version: this.version,
            status: this.status,
            activities: activities,
            routes: this.routes.map(r => r.toJSON ? r.toJSON() : r),
            listeners: this.listeners,
            formulas: this.formulas,
            parameters: this.parameters,
            extendedAttributes: this.extendedAttributes,
            agentConfig: this.agentConfig,
            sceneConfig: this.sceneConfig,
            createdTime: this.createdTime,
            updatedTime: this.updatedTime
        };
    }

    toYAML() {
        const lines = [];
        lines.push(`processDefId: ${this.processDefId}`);
        lines.push(`name: ${this.name}`);
        lines.push(`description: ${this.description}`);
        lines.push(`category: ${this.category}`);
        lines.push(`accessLevel: ${this.accessLevel}`);
        lines.push(`version: ${this.version}`);
        lines.push(`status: ${this.status}`);
        lines.push('');
        lines.push('activities:');
        this.activities.forEach(a => {
            lines.push(`  - id: ${a.activityDefId}`);
            lines.push(`    name: ${a.name}`);
            lines.push(`    type: ${a.activityType}`);
            lines.push(`    category: ${a.activityCategory}`);
        });
        lines.push('');
        lines.push('routes:');
        this.routes.forEach(r => {
            lines.push(`  - id: ${r.routeDefId}`);
            lines.push(`    from: ${r.from}`);
            lines.push(`    to: ${r.to}`);
            lines.push(`    condition: ${r.condition || 'none'}`);
        });
        return lines.join('\n');
    }

    static fromYAML(yamlStr) {
        const lines = yamlStr.split('\n');
        const data = { activities: [], routes: [] };
        let currentSection = null;
        let currentItem = null;

        lines.forEach(line => {
            const trimmed = line.trim();
            if (!trimmed) return;

            if (trimmed.startsWith('processDefId:')) {
                data.processDefId = trimmed.split(':')[1].trim();
            } else if (trimmed.startsWith('name:')) {
                data.name = trimmed.split(':').slice(1).join(':').trim();
            } else if (trimmed.startsWith('description:')) {
                data.description = trimmed.split(':').slice(1).join(':').trim();
            } else if (trimmed.startsWith('category:')) {
                data.category = trimmed.split(':')[1].trim();
            } else if (trimmed.startsWith('accessLevel:')) {
                data.accessLevel = trimmed.split(':')[1].trim();
            } else if (trimmed.startsWith('version:')) {
                data.version = parseInt(trimmed.split(':')[1].trim());
            } else if (trimmed.startsWith('status:')) {
                data.status = trimmed.split(':')[1].trim();
            } else if (trimmed === 'activities:') {
                currentSection = 'activities';
            } else if (trimmed === 'routes:') {
                currentSection = 'routes';
            } else if (trimmed.startsWith('- id:') || trimmed.startsWith('id:')) {
                if (currentSection) {
                    if (currentItem) {
                        data[currentSection].push(currentItem);
                    }
                    currentItem = { activityDefId: trimmed.split(':')[1].trim(), routeDefId: trimmed.split(':')[1].trim() };
                }
            } else if (currentItem && trimmed.startsWith('name:')) {
                currentItem.name = trimmed.split(':').slice(1).join(':').trim();
            } else if (currentItem && trimmed.startsWith('type:')) {
                currentItem.activityType = trimmed.split(':')[1].trim();
            } else if (currentItem && trimmed.startsWith('category:')) {
                currentItem.activityCategory = trimmed.split(':')[1].trim();
            } else if (currentItem && trimmed.startsWith('from:')) {
                currentItem.from = trimmed.split(':')[1].trim();
            } else if (currentItem && trimmed.startsWith('to:')) {
                currentItem.to = trimmed.split(':')[1].trim();
            } else if (currentItem && trimmed.startsWith('condition:')) {
                currentItem.condition = trimmed.split(':')[1].trim();
            }
        });

        if (currentItem && currentSection) {
            data[currentSection].push(currentItem);
        }

        return new ProcessDef(data);
    }
}

window.ProcessDef = ProcessDef;
