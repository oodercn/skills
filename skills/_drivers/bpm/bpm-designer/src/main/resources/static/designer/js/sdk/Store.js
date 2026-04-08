class Store {
    constructor() {
        this.process = null;
        this.currentActivity = null;
        this.currentRoute = null;
        this.selectedNodes = [];
        this.clipboard = null;
        this.dirty = false;
        this.history = [];
        this.historyIndex = -1;
        this.maxHistory = 50;
        this.listeners = new Map();
    }

    setProcess(processDef) {
        this.process = processDef;
        this.currentActivity = null;
        this.currentRoute = null;
        this.selectedNodes = [];
        this.dirty = false;
        this.history = [];
        this.historyIndex = -1;
        this._saveHistory();
        this._emit('process:change', processDef);
    }

    getProcess() {
        return this.process;
    }

    getActivity(activityId) {
        if (!this.process || !activityId) return null;
        return this.process.getActivity(activityId);
    }

    selectActivity(activityId) {
        console.log('[Store] selectActivity called with id:', activityId);
        console.log('[Store] current process:', this.process ? 'exists' : 'null');
        console.log('[Store] activities count:', this.process?.activities?.length || 0);
        if (this.process && this.process.activities) {
            console.log('[Store] activity ids:', this.process.activities.map(a => a.activityDefId));
        }
        this.currentActivity = this.getActivity(activityId);
        console.log('[Store] selectActivity result:', activityId, '->', this.currentActivity ? this.currentActivity.name : 'null');
        this._emit('activity:select', this.currentActivity);
    }

    updateActivity(activityDef) {
        if (!this.process) return;
        const index = this.process.activities.findIndex(
            a => a.activityDefId === activityDef.activityDefId
        );
        if (index >= 0) {
            this.process.activities[index] = activityDef;
            this._saveHistory();
            this.dirty = true;
            this._emit('activity:update', activityDef);
        }
    }

    addActivity(activityDef) {
        if (!this.process) {
            console.log('[Store] No process exists, creating new process...');
            this.process = new ProcessDef({});
        }
        this.process.addActivity(activityDef);
        this._saveHistory();
        this.dirty = true;
        console.log('[Store] Activity added:', activityDef.activityDefId, 'total activities:', this.process.activities.length);
        this._emit('activity:add', activityDef);
    }

    removeActivity(activityId) {
        if (!this.process) return;
        this.process.removeActivity(activityId);
        if (this.currentActivity?.activityDefId === activityId) {
            this.currentActivity = null;
        }
        this._saveHistory();
        this.dirty = true;
        this._emit('activity:remove', activityId);
    }

    addRoute(routeDef) {
        if (!this.process) {
            console.log('[Store] No process exists, creating new process...');
            this.process = new ProcessDef({});
        }
        this.process.addRoute(routeDef);
        this._saveHistory();
        this.dirty = true;
        this._emit('route:add', routeDef);
    }

    removeRoute(routeId) {
        if (!this.process) return;
        this.process.removeRoute(routeId);
        this._saveHistory();
        this.dirty = true;
        this._emit('route:remove', routeId);
    }

    getRoute(routeId) {
        if (!this.process || !routeId) return null;
        return this.process.routes?.find(r => r.routeDefId === routeId);
    }

    selectRoute(routeId) {
        console.log('[Store] selectRoute called with id:', routeId);
        this.currentRoute = this.getRoute(routeId);
        console.log('[Store] selectRoute result:', routeId, '->', this.currentRoute ? this.currentRoute.name : 'null');
        this._emit('route:select', this.currentRoute);
    }

    _saveHistory() {
        if (this.historyIndex < this.history.length - 1) {
            this.history = this.history.slice(0, this.historyIndex + 1);
        }
        const snapshot = JSON.stringify(this.process?.toJSON() || {});
        this.history.push(snapshot);
        if (this.history.length > this.maxHistory) {
            this.history.shift();
        } else {
            this.historyIndex++;
        }
    }

    undo() {
        if (this.historyIndex > 0) {
            this.historyIndex--;
            const snapshot = this.history[this.historyIndex];
            this.process = new ProcessDef(JSON.parse(snapshot));
            this.dirty = true;
            this._emit('process:change', this.process);
        }
    }

    redo() {
        if (this.historyIndex < this.history.length - 1) {
            this.historyIndex++;
            const snapshot = this.history[this.historyIndex];
            this.process = new ProcessDef(JSON.parse(snapshot));
            this.dirty = true;
            this._emit('process:change', this.process);
        }
    }

    canUndo() {
        return this.historyIndex > 0;
    }

    canRedo() {
        return this.historyIndex < this.history.length - 1;
    }

    setDirty(dirty) {
        this.dirty = dirty;
        this._emit('dirty:change', dirty);
    }

    isDirty() {
        return this.dirty;
    }

    on(event, callback) {
        if (!this.listeners.has(event)) {
            this.listeners.set(event, []);
        }
        this.listeners.get(event).push(callback);
    }

    off(event, callback) {
        if (this.listeners.has(event)) {
            const callbacks = this.listeners.get(event);
            const index = callbacks.indexOf(callback);
            if (index >= 0) {
                callbacks.splice(index, 1);
            }
        }
    }

    emit(event, data) {
        this._emit(event, data);
    }

    _emit(event, data) {
        console.log('[Store] emit:', event, data);
        if (this.listeners.has(event)) {
            this.listeners.get(event).forEach(cb => cb(data));
        }
    }
}

window.Store = Store;
