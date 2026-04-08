class ActivityDef {
    constructor(data) {
        this.activityDefId = data?.activityDefId || this._generateId();
        this.name = data?.name || '新活动';
        this.description = data?.description || '';
        
        this.activityType = data?.activityType || 'TASK';
        
        if (this.activityType === 'START') {
            this.position = 'START';
            this.name = data?.name || '开始';
        } else if (this.activityType === 'END') {
            this.position = 'END';
            this.name = data?.name || '结束';
        } else {
            this.position = data?.position || 'NORMAL';
        }
        
        this.activityCategory = data?.activityCategory || 'HUMAN';
        this.implementation = data?.implementation || 'IMPL_NO';
        this.performerType = data?.performerType || 'HUMAN';
        
        if (data?.positionCoord && typeof data.positionCoord === 'object') {
            this.positionCoord = {
                x: parseFloat(data.positionCoord.x) || 0,
                y: parseFloat(data.positionCoord.y) || 0
            };
        } else {
            this.positionCoord = { x: 0, y: 0 };
        }
        
        this.limit = data?.limit || 0;
        this.alertTime = data?.alertTime || 0;
        this.durationUnit = data?.durationUnit || 'D';
        this.deadlineOperation = data?.deadlineOperation || 'DEFAULT';
        
        this.join = data?.join || 'DEFAULT';
        this.split = data?.split || 'DEFAULT';
        this.canRouteBack = data?.canRouteBack || false;
        this.routeBackMethod = data?.routeBackMethod || 'DEFAULT';
        this.canSpecialSend = data?.canSpecialSend || false;
        this.specialSendScope = data?.specialSendScope || 'DEFAULT';
        this.canReSend = data?.canReSend || false;
        
        this.performType = data?.performType || 'SINGLE';
        this.performSequence = data?.performSequence || 'FIRST';
        this.performerSelectedAtt = data?.performerSelectedAtt || [];
        this.canInsteadSign = data?.canInsteadSign || false;
        this.canTakeBack = data?.canTakeBack || false;
        
        this.agentConfig = data?.agentConfig || null;
        this.sceneConfig = data?.sceneConfig || null;
        this.listeners = data?.listeners || [];
        this.extendedAttributes = data?.extendedAttributes || {};
    }

    _generateId() {
        return 'act_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    setPosition(x, y) {
        this.positionCoord = { x, y };
    }

    isStartOrEnd() {
        return this.activityType === 'START' || this.activityType === 'END';
    }

    toJSON() {
        if (this.isStartOrEnd()) {
            return {
                activityDefId: this.activityDefId,
                name: this.name,
                position: this.position,
                positionCoord: this.positionCoord
            };
        }
        
        return {
            activityDefId: this.activityDefId,
            name: this.name,
            description: this.description,
            activityType: this.activityType,
            activityCategory: this.activityCategory,
            implementation: this.implementation,
            performerType: this.performerType,
            position: this.position,
            positionCoord: this.positionCoord,
            limit: this.limit,
            alertTime: this.alertTime,
            durationUnit: this.durationUnit,
            deadlineOperation: this.deadlineOperation,
            join: this.join,
            split: this.split,
            canRouteBack: this.canRouteBack,
            routeBackMethod: this.routeBackMethod,
            canSpecialSend: this.canSpecialSend,
            specialSendScope: this.specialSendScope,
            canReSend: this.canReSend,
            performType: this.performType,
            performSequence: this.performSequence,
            performerSelectedAtt: this.performerSelectedAtt,
            canInsteadSign: this.canInsteadSign,
            canTakeBack: this.canTakeBack,
            agentConfig: this.agentConfig,
            sceneConfig: this.sceneConfig,
            listeners: this.listeners,
            extendedAttributes: this.extendedAttributes
        };
    }
}

window.ActivityDef = ActivityDef;
