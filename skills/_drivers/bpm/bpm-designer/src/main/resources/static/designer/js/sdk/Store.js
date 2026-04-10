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
        this.api = null;
        this._autoSaveTimer = null;
        this._autoSaveDelay = 1000; // 1秒后自动保存
    }

    setApi(api) {
        this.api = api;
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
            this._triggerAutoSave();
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
        this._triggerAutoSave();
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
        this._triggerAutoSave();
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
        this._triggerAutoSave();
    }

    removeRoute(routeId) {
        if (!this.process) return;
        this.process.removeRoute(routeId);
        this._saveHistory();
        this.dirty = true;
        this._emit('route:remove', routeId);
        this._triggerAutoSave();
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

    updateRoute(routeDef) {
        if (!this.process) return;
        const index = this.process.routes?.findIndex(
            r => r.routeDefId === routeDef.routeDefId
        );
        if (index >= 0) {
            this.process.routes[index] = routeDef;
            this._saveHistory();
            this.dirty = true;
            this._emit('route:update', routeDef);
            this._triggerAutoSave();
        }
    }

    updateProcess(processDef) {
        this.process = processDef;
        this._saveHistory();
        this.dirty = true;
        this._emit('process:update', processDef);
        this._triggerAutoSave();
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
            this._triggerAutoSave();
        }
    }

    redo() {
        if (this.historyIndex < this.history.length - 1) {
            this.historyIndex++;
            const snapshot = this.history[this.historyIndex];
            this.process = new ProcessDef(JSON.parse(snapshot));
            this.dirty = true;
            this._emit('process:change', this.process);
            this._triggerAutoSave();
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

    /**
     * 触发自动保存（防抖）
     */
    _triggerAutoSave() {
        if (this._autoSaveTimer) {
            clearTimeout(this._autoSaveTimer);
        }
        this._autoSaveTimer = setTimeout(() => {
            this._autoSave();
        }, this._autoSaveDelay);
    }

    /**
     * 自动保存到服务端
     */
    async _autoSave() {
        if (!this.api || !this.process) {
            console.log('[Store] Auto-save skipped: no api or process');
            return;
        }

        try {
            console.log('[Store] Auto-saving process...');
            const processData = this.process.toJSON();

            // 调试：打印第一个活动的坐标
            if (processData.activities && processData.activities.length > 0) {
                const firstAct = processData.activities[0];
                console.log('[Store] First activity positionCoord:', firstAct.positionCoord);
            }

            const requestBody = JSON.stringify(processData);
            console.log('[Store] Request body length:', requestBody.length);
            console.log('[Store] Request body preview:', requestBody.substring(0, 500));

            await this._saveWithRetry(processData);
            // 自动保存成功后不清除dirty状态，只触发事件
            this._emit('auto-save', { success: true });
            console.log('[Store] Auto-save completed');
        } catch (error) {
            console.error('[Store] Auto-save failed:', error);
            this._emit('auto-save', { success: false, error: error.message });
        }
    }

    /**
     * 手动保存 - 会清除dirty状态
     */
    async save() {
        if (!this.api || !this.process) {
            throw new Error('No api or process');
        }

        console.log('[Store] Manual saving process...');
        const processData = this.process.toJSON();
        await this._saveWithRetry(processData);

        // 手动保存成功后清除dirty状态
        this.dirty = false;
        this._emit('dirty:change', false);
        console.log('[Store] Manual save completed');
    }

    /**
     * 带重试的保存逻辑
     */
    async _saveWithRetry(processData, maxRetries = 3) {
        let lastError = null;

        for (let attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                console.log(`[Store] Save attempt ${attempt}/${maxRetries}`);
                await this.api.saveProcess(processData);
                return; // 保存成功
            } catch (error) {
                lastError = error;
                console.error(`[Store] Save attempt ${attempt} failed:`, error.message);

                if (attempt < maxRetries) {
                    // 等待后重试（指数退避）
                    const delay = Math.pow(2, attempt - 1) * 1000;
                    console.log(`[Store] Retrying in ${delay}ms...`);
                    await new Promise(resolve => setTimeout(resolve, delay));
                }
            }
        }

        // 所有重试都失败
        throw new Error(`Save failed after ${maxRetries} attempts: ${lastError?.message}`);
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
