class Tree {
    constructor(container, store) {
        this.container = container;
        this.store = store;
        this.api = ApiFactory.get();
        this.data = {
            name: '流程工程',
            expanded: true,
            children: [
                {
                    name: '流程定义',
                    type: 'process-category',
                    id: 'process-category',
                    expanded: true,
                    children: []
                }
            ]
        };
        this.selectedNode = null;
        this.render();
        this._bindEvents();
        this._bindStoreEvents();
        this._loadProcessTree();
    }

    /**
     * 绑定Store事件监听
     */
    _bindStoreEvents() {
        // 监听活动更新事件，更新树节点显示
        this.store.on('activity:update', (activityDef) => {
            console.log('[Tree] Activity updated:', activityDef);
            this._updateActivityNode(activityDef);
        });

        // 监听路由更新事件
        this.store.on('route:update', (routeDef) => {
            console.log('[Tree] Route updated:', routeDef);
            this._updateRouteNode(routeDef);
        });
    }

    /**
     * 更新活动节点显示
     */
    _updateActivityNode(activityDef) {
        if (!activityDef || !activityDef.activityDefId) return;

        // 查找并更新活动节点
        const nodeId = 'act-' + activityDef.activityDefId;
        const nodeEl = this.container.querySelector(`[data-id="${nodeId}"]`);
        if (nodeEl) {
            const nameEl = nodeEl.querySelector('.d-tree-node-name');
            if (nameEl && activityDef.name) {
                nameEl.textContent = activityDef.name;
                console.log('[Tree] Updated activity node name:', activityDef.name);
            }
        }

        // 同时更新树数据
        const updateNodeInData = (nodes) => {
            for (const node of nodes) {
                if (node.activityDefId === activityDef.activityDefId) {
                    node.name = activityDef.name;
                    return true;
                }
                if (node.children) {
                    if (updateNodeInData(node.children)) return true;
                }
            }
            return false;
        };
        updateNodeInData([this.data]);
    }

    /**
     * 更新路由节点显示
     */
    _updateRouteNode(routeDef) {
        if (!routeDef || !routeDef.routeDefId) return;

        // 查找并更新路由节点
        const nodeId = 'route-' + routeDef.routeDefId;
        const nodeEl = this.container.querySelector(`[data-id="${nodeId}"]`);
        if (nodeEl) {
            const nameEl = nodeEl.querySelector('.d-tree-node-name');
            if (nameEl && routeDef.name) {
                nameEl.textContent = routeDef.name;
                console.log('[Tree] Updated route node name:', routeDef.name);
            }
        }
    }

    async _loadProcessTree() {
        console.log('[Tree] Loading process tree...');
        try {
            const response = await this.api.request('GET', '/process/tree');
            console.log('[Tree] Process tree response:', response);
            if (response && response.data) {
                const categoryNode = this.data.children[0];
                categoryNode.children = response.data.map(p => ({
                    id: 'proc-' + p.id,
                    name: p.name,
                    type: 'process',
                    processDefId: p.id,
                    status: p.status,
                    version: p.version,
                    expanded: false,
                    children: []
                }));
                this.render();
                
                this._restoreLastProcess(response.data);
            }
        } catch (e) {
            console.error('[Tree] Failed to load process tree:', e);
        }
    }

    async _restoreLastProcess(processes) {
        const lastProcessId = localStorage.getItem('bpm_last_process');
        let processToOpen = null;
        
        if (lastProcessId) {
            processToOpen = processes.find(p => p.id === lastProcessId || p.processDefId === lastProcessId);
        }
        
        if (!processToOpen && processes.length > 0) {
            processToOpen = processes[0];
        }
        
        if (processToOpen) {
            console.log('[Tree] Restoring last process:', processToOpen.id || processToOpen.processDefId);
            await this._openProcess(processToOpen.id || processToOpen.processDefId);
        }
    }

    async _openProcess(processDefId) {
        try {
            const response = await this.api.getProcess(processDefId, 'latest');
            if (response && response.data) {
                this.store.emit('tree:open-process', response.data);
            }
        } catch (e) {
            console.error('[Tree] Failed to open process:', e);
        }
    }

    async _loadVersions(processDefId) {
        console.log('[Tree] Loading versions for:', processDefId);
        try {
            const response = await this.api.getProcess(processDefId, 'latest');
            if (response && response.data) {
                const process = response.data;
                const processNode = this._findNode(`proc-${processDefId}`);
                if (processNode) {
                    processNode.children = [{
                        id: `ver-${processDefId}-${process.version || 1}`,
                        name: `v${process.version || 1}`,
                        type: 'version',
                        processDefId: processDefId,
                        version: process.version || 1,
                        status: process.status,
                        expanded: false,
                        children: this._buildActivityChildren(process)
                    }];
                    this.render();
                }
            }
        } catch (e) {
            console.error('[Tree] Failed to load versions:', e);
        }
    }

    _buildActivityChildren(process) {
        const children = [];
        
        if (process.activities && process.activities.length > 0) {
            process.activities.forEach(act => {
                children.push({
                    id: 'act-' + act.activityDefId,
                    name: act.name,
                    type: 'activity',
                    activityDefId: act.activityDefId,
                    activityType: act.activityType,
                    activityCategory: act.activityCategory,
                    implementation: act.implementation,
                    children: this._getActivityPanels(act)
                });
            });
        }
        
        return children;
    }

    _getActivityPanels(activity) {
        const panels = [];
        
        if (activity.implementation === 'IMPL_NO') {
            panels.push({
                id: 'panel-right-' + activity.activityDefId,
                name: '权限配置',
                type: 'panel',
                panelType: 'right'
            });
        } else if (activity.implementation === 'IMPL_DEVICE') {
            panels.push({
                id: 'panel-device-' + activity.activityDefId,
                name: '设备配置',
                type: 'panel',
                panelType: 'device'
            });
        } else if (activity.implementation === 'IMPL_SERVICE') {
            panels.push({
                id: 'panel-service-' + activity.activityDefId,
                name: '服务配置',
                type: 'panel',
                panelType: 'service'
            });
        } else if (activity.implementation === 'IMPL_EVENT') {
            panels.push({
                id: 'panel-event-' + activity.activityDefId,
                name: '事件配置',
                type: 'panel',
                panelType: 'event'
            });
        } else if (activity.implementation === 'IMPL_SUBFLOW' || activity.implementation === 'IMPL_OUTFLOW') {
            panels.push({
                id: 'panel-subflow-' + activity.activityDefId,
                name: '子流程配置',
                type: 'panel',
                panelType: 'subflow'
            });
        }
        
        if (activity.activityCategory === 'AGENT' || 
            activity.activityType === 'LLM_TASK' || 
            activity.activityType === 'AGENT_TASK' || 
            activity.activityType === 'COORDINATOR') {
            panels.push({
                id: 'panel-agent-' + activity.activityDefId,
                name: 'Agent配置',
                type: 'panel',
                panelType: 'agent'
            });
        }
        
        if (activity.activityCategory === 'SCENE' || 
            activity.activityType === 'SCENE' || 
            activity.activityType === 'ACTIVITY_BLOCK') {
            panels.push({
                id: 'panel-scene-' + activity.activityDefId,
                name: '场景配置',
                type: 'panel',
                panelType: 'scene'
            });
        }
        
        return panels;
    }

    _findNode(id) {
        const find = (nodes) => {
            for (const node of nodes) {
                if (node.id === id) return node;
                if (node.children) {
                    const found = find(node.children);
                    if (found) return found;
                }
            }
            return null;
        };
        return find([this.data]);
    }

    render() {
        this.container.innerHTML = this._renderNode(this.data, 0);
        if (this.selectedNode) {
            const el = this.container.querySelector(`[data-id="${this.selectedNode}"]`);
            if (el) {
                el.querySelector('.d-tree-node-header')?.classList.add('selected');
            }
        }
    }

    _renderNode(node, level) {
        const hasChildren = node.children && node.children.length > 0;
        const isExpanded = node.expanded !== false;
        
        let iconName = 'file';
        if (node.type === 'process-category' || node.type === 'category') iconName = 'folder';
        else if (node.type === 'process') iconName = 'flow';
        else if (node.type === 'version') iconName = 'file';
        else if (node.type === 'activities-group') iconName = 'folder';
        else if (node.type === 'routes-group') iconName = 'folder';
        else if (node.type === 'activity') iconName = 'activity';
        else if (node.type === 'route') iconName = 'route';
        else if (node.type === 'panel') iconName = 'form';
        else if (hasChildren) iconName = isExpanded ? 'folder-open' : 'folder';

        let statusBadge = '';
        if (node.status) {
            const statusClass = node.status === 'RELEASED' ? 'status-released' : 
                           node.status === 'UNDER_REVISION' ? 'status-draft' : 'status-test';
            statusBadge = `<span class="d-tree-status ${statusClass}">${this._getStatusText(node.status)}</span>`;
        }

        const iconSvg = IconManager.render(iconName, 16, 'tree-icon');

        return `
            <div class="d-tree-node" data-level="${level}" data-type="${node.type || ''}" data-id="${node.id || ''}" 
                 data-process-id="${node.processDefId || ''}" 
                 data-version="${node.version || ''}" data-activity-id="${node.activityDefId || ''}"
                 data-route-id="${node.routeDefId || ''}" data-panel-type="${node.panelType || ''}">
                <div class="d-tree-node-header">
                    ${hasChildren ? `
                        <span class="d-tree-node-toggle ${isExpanded ? 'expanded' : ''}">
                            <svg viewBox="0 0 16 16" width="16" height="16">
                                <path d="M6 4l4 4-4 4" fill="none" stroke="currentColor" stroke-width="1.5"/>
                            </svg>
                        </span>
                    ` : '<span style="width: 18px"></span>'}
                    <span class="d-tree-node-icon">${iconSvg}</span>
                    <span class="d-tree-node-name">${node.name}</span>
                    ${statusBadge}
                </div>
                ${hasChildren ? `
                    <div class="d-tree-node-children ${isExpanded ? '' : 'collapsed'}">
                        ${node.children.map(child => this._renderNode(child, level + 1)).join('')}
                    </div>
                ` : ''}
            </div>
        `;
    }

    _getStatusText(status) {
        const statusMap = {
            'RELEASED': '已发布',
            'UNDER_REVISION': '修订中',
            'UNDER_TEST': '测试中'
        };
        return statusMap[status] || status;
    }

    _bindEvents() {
        this.container.addEventListener('click', async (e) => {
            const header = e.target.closest('.d-tree-node-header');
            if (!header) return;

            const nodeEl = header.closest('.d-tree-node');
            const toggle = header.querySelector('.d-tree-node-toggle');
            const childrenEl = nodeEl.querySelector('.d-tree-node-children');
            const nodeType = nodeEl.dataset.type;
            const nodeId = nodeEl.dataset.id;
            const processId = nodeEl.dataset.processId;

            this._selectNode(nodeId);

            if (toggle && (e.target === toggle || toggle.contains(e.target))) {
                toggle.classList.toggle('expanded');
                if (childrenEl) {
                    childrenEl.classList.toggle('collapsed');
                }
                
                if (nodeType === 'process' && processId && (!childrenEl || !childrenEl.querySelector('.d-tree-node'))) {
                    await this._loadVersions(processId);
                }
            } else if (nodeType === 'process' && processId) {
                const nodeData = this._findNode(nodeId);
                if (nodeData && (!nodeData.children || nodeData.children.length === 0)) {
                    nodeData.expanded = true;
                    await this._loadVersions(processId);
                }
                await this._onOpenProcess(processId, 'latest');
            } else if (nodeType === 'version' && processId) {
                const version = nodeEl.dataset.version;
                await this._onOpenProcess(processId, version);
            } else {
                this._onSelect(nodeEl, nodeType, nodeId);
            }
        });

        this.container.addEventListener('contextmenu', (e) => {
            e.preventDefault();
            e.stopPropagation();
            
            console.log('[Tree] Context menu triggered');
            
            const header = e.target.closest('.d-tree-node-header');
            if (!header) {
                console.log('[Tree] No header found');
                return;
            }

            const nodeEl = header.closest('.d-tree-node');
            const nodeType = nodeEl.dataset.type;
            const nodeId = nodeEl.dataset.id;
            const processId = nodeEl.dataset.processId;
            const version = nodeEl.dataset.version;
            const activityId = nodeEl.dataset.activityId;
            
            console.log('[Tree] Node info:', { nodeType, nodeId, processId, version, activityId });

            this._showContextMenu(e.clientX, e.clientY, nodeType, {
                nodeId,
                processId,
                version,
                activityId,
                nodeEl
            });
        });

        this.container.addEventListener('dblclick', async (e) => {
            const header = e.target.closest('.d-tree-node-header');
            if (!header) return;
            const nodeEl = header.closest('.d-tree-node');
            const nodeType = nodeEl.dataset.type;
            const activityId = nodeEl.dataset.activityId;
            
            if (nodeType === 'activity') {
                this.store.emit('tree:select-activity', activityId);
            } else if (nodeType === 'panel') {
                const panelType = nodeEl.dataset.panelType;
                this.store.emit('tree:open-panel', { panelType });
            }
        });
    }

    _selectNode(nodeId) {
        this.selectedNode = nodeId;
        this.container.querySelectorAll('.d-tree-node-header').forEach(h => {
            h.classList.remove('selected');
        });
        const el = this.container.querySelector(`[data-id="${nodeId}"]`);
        if (el) {
            el.querySelector('.d-tree-node-header')?.classList.add('selected');
        }
    }

    _showContextMenu(x, y, nodeType, data) {
        console.log('[Tree] _showContextMenu called:', { x, y, nodeType, data });
        
        let items = [];

        if (nodeType === 'process-category') {
            items = [
                { label: '新建流程', icon: 'plus', action: () => this._createNewProcess() },
                { divider: true },
                { label: '刷新', icon: 'refresh', action: () => this.refresh() }
            ];
        } else if (nodeType === 'process') {
            items = [
                { label: '打开', icon: 'folder-open', action: () => this._onOpenProcess(data.processId, 'latest') },
                { label: '新建版本', icon: 'plus-circle', action: () => this._createNewVersion(data.processId) },
                { divider: true },
                { label: '重命名', icon: 'edit', action: () => this._renameProcess(data.nodeEl, data.processId) },
                { label: '复制流程', icon: 'copy', action: () => this._copyProcess(data.processId) },
                { label: '删除流程', icon: 'delete', action: () => this._deleteProcess(data.processId) },
                { divider: true },
                { label: '导出YAML', icon: 'download', action: () => this._exportProcess(data.processId) }
            ];
        } else if (nodeType === 'version') {
            items = [
                { label: '打开', icon: 'folder-open', action: () => this._onOpenProcess(data.processId, data.version) },
                { divider: true },
                { label: '激活版本', icon: 'check', action: () => this._activateVersion(data.processId, data.version) },
                { label: '冻结版本', icon: 'lock', action: () => this._freezeVersion(data.processId, data.version) },
                { divider: true },
                { label: '删除版本', icon: 'delete', action: () => this._deleteVersion(data.processId, data.version) }
            ];
        } else if (nodeType === 'activity') {
            items = [
                { label: '定位到画布', icon: 'location', action: () => this._locateActivity(data.activityId) },
                { label: '查看属性', icon: 'setting', action: () => this.store.emit('tree:select-activity', data.activityId) }
            ];
        }
        
        console.log('[Tree] Menu items:', items);

        if (items.length > 0) {
            console.log('[Tree] Calling ContextMenu.show');
            if (typeof ContextMenu !== 'undefined' && ContextMenu.show) {
                ContextMenu.show(x, y, items);
            } else {
                console.error('[Tree] ContextMenu is not defined or show method is missing');
            }
        } else {
            console.log('[Tree] No items for nodeType:', nodeType);
        }
    }

    async _createNewProcess() {
        const name = prompt('请输入流程名称:', '新流程');
        if (!name) return;

        try {
            const process = new ProcessDef({
                processDefId: 'proc_' + Date.now(),
                name: name,
                description: '',
                category: '办公流程',
                accessLevel: 'Public'
            });
            
            this.store.emit('tree:open-process', process);
            await this.refresh();
        } catch (e) {
            console.error('[Tree] Create process failed:', e);
        }
    }

    async _createNewVersion(processId) {
        const description = prompt('请输入版本说明（可选）:');
        
        try {
            const response = await this.api.getProcess(processId, 'latest');
            if (response && response.data) {
                const newVersion = {
                    ...response.data,
                    version: (response.data.version || 1) + 1,
                    status: 'UNDER_REVISION',
                    description: description || response.data.description
                };
                
                this.store.emit('tree:open-process', newVersion);
                await this._loadVersions(processId);
            }
        } catch (e) {
            console.error('[Tree] Create version failed:', e);
        }
    }

    _renameProcess(nodeEl, processId) {
        const nameEl = nodeEl.querySelector('.d-tree-node-name');
        const currentName = nameEl ? nameEl.textContent : '';
        const newName = prompt('请输入新的流程名称:', currentName);
        
        if (newName && newName !== currentName) {
            this.store.emit('tree:rename-process', { processId, newName });
            if (nameEl) {
                nameEl.textContent = newName;
            }
        }
    }

    async _copyProcess(processId) {
        try {
            const response = await this.api.getProcess(processId, 'latest');
            if (response && response.data) {
                const copy = {
                    ...response.data,
                    processDefId: 'proc_' + Date.now(),
                    name: response.data.name + ' (副本)',
                    version: 1,
                    status: 'UNDER_REVISION'
                };
                
                this.store.emit('tree:open-process', copy);
                await this.refresh();
            }
        } catch (e) {
            console.error('[Tree] Copy process failed:', e);
        }
    }

    async _deleteProcess(processId) {
        if (!confirm('确定要删除此流程吗？此操作不可恢复。')) return;
        
        try {
            await this.api.deleteProcess(processId);
            await this.refresh();
        } catch (e) {
            console.error('[Tree] Delete process failed:', e);
        }
    }

    _exportProcess(processId) {
        this.store.emit('tree:export-process', processId);
    }

    async _activateVersion(processId, version) {
        try {
            await this.api.activateVersion(processId, version);
            await this._loadVersions(processId);
        } catch (e) {
            console.error('[Tree] Activate version failed:', e);
        }
    }

    async _freezeVersion(processId, version) {
        try {
            await this.api.freezeVersion(processId, version);
            await this._loadVersions(processId);
        } catch (e) {
            console.error('[Tree] Freeze version failed:', e);
        }
    }

    async _deleteVersion(processId, version) {
        if (!confirm('确定要删除此版本吗？')) return;
        
        try {
            await this.api.deleteVersion(processId, version);
            await this._loadVersions(processId);
        } catch (e) {
            console.error('[Tree] Delete version failed:', e);
        }
    }

    _locateActivity(activityId) {
        this.store.emit('tree:select-activity', activityId);
    }

    _onSelect(nodeEl, type, id) {
        if (type === 'activity') {
            const activityId = nodeEl.dataset.activityId;
            this.store.emit('tree:select-activity', activityId);
        } else if (type === 'route') {
            const routeId = nodeEl.dataset.routeId;
            this.store.emit('tree:select-route', routeId);
        } else if (type === 'panel') {
            const panelType = nodeEl.dataset.panelType;
            this.store.emit('tree:select-panel', panelType);
        }
    }

    async _onOpenProcess(processId, version) {
        console.log('[Tree] Opening process:', processId, version);
        try {
            const response = await this.api.getProcess(processId, version || 'latest');
            if (response && response.data) {
                this.store.emit('tree:open-process', response.data);
            }
        } catch (e) {
            console.error('[Tree] Failed to open process:', e);
        }
    }

    addProcess(process) {
        const categoryNode = this.data.children[0];
        const existingIndex = categoryNode.children.findIndex(c => c.id === `proc-${process.processDefId}`);
        const nodeData = {
            id: 'proc-' + process.processDefId,
            name: process.name,
            type: 'process',
            processDefId: process.processDefId,
            status: process.status,
            version: process.version,
            expanded: false,
            children: []
        };
        
        if (existingIndex >= 0) {
            categoryNode.children[existingIndex] = nodeData;
        } else {
            categoryNode.children.push(nodeData);
        }
        this.render();
    }

    removeProcess(processId) {
        const categoryNode = this.data.children[0];
        categoryNode.children = categoryNode.children.filter(c => c.id !== `proc-${processId}`);
        this.render();
    }

    refresh() {
        this._loadProcessTree();
    }
}

window.Tree = Tree;
