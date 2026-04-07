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
        this.currentActivity = this.getActivity(activityId);
        console.log('[Store] selectActivity:', activityId, '->', this.currentActivity);
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
        if (!this.process) return;
        this.process.addActivity(activityDef);
        this._saveHistory();
        this.dirty = true;
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
        if (!this.process) return;
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
