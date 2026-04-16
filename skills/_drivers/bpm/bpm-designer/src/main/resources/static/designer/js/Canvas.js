class Canvas {
    constructor(container, store, app = null) {
        this.container = container;
        this.store = store;
        this.app = app;
        this.nodes = new Map();
        this.edges = new Map();
        this.scale = 1;
        this.offsetX = 0;
        this.offsetY = 0;
        this.isDragging = false;
        this.isDrawingEdge = false;
        this.dragNode = null;
        this.dragStartX = 0;
        this.dragStartY = 0;
        this.edgeStart = null;
        this.selectedEdge = null;
        this.selectedNodes = new Set();
        this.isSelecting = false;
        this.selectionBox = null;
        this.selectionStartX = 0;
        this.selectionStartY = 0;
        this.nodeWidth = 120;
        this.nodeHeight = 60;
        this.smallNodeSize = 50;
        this.gridSize = 20;
        this._init();
    }

    _init() {
        this._createSvgLayer();
        this._bindEvents();
        this._bindStoreEvents();
        this._drawGrid();
    }

    /**
     * 绑定Store事件监听
     */
    _bindStoreEvents() {
        // 监听活动更新事件，更新画布上的节点显示
        this.store.on('activity:update', (activityDef) => {
            console.log('[Canvas] Activity updated:', activityDef);
            this._updateNodeDisplay(activityDef);
        });

        // 监听路由更新事件
        this.store.on('route:update', (routeDef) => {
            console.log('[Canvas] Route updated:', routeDef);
            this._updateEdgeDisplay(routeDef);
        });
    }

    /**
     * 更新节点显示
     * 支持更新：名称、位置、类型、实现方式等所有属性
     */
    _updateNodeDisplay(activityDef) {
        if (!activityDef || !activityDef.activityDefId) return;

        const nodeData = this.nodes.get(activityDef.activityDefId);
        if (!nodeData || !nodeData.element) return;

        const nodeEl = nodeData.element;
        const oldActivity = nodeData.activity;

        console.log('[Canvas] Updating node display:', activityDef.activityDefId, 'changes:', Object.keys(activityDef));

        // 1. 更新名称
        if (activityDef.name !== undefined) {
            const nameEl = nodeEl.querySelector('.d-node-name');
            if (nameEl) {
                nameEl.textContent = activityDef.name;
                console.log('[Canvas] Updated node name:', activityDef.name);
            }

            const titleEl = nodeEl.querySelector('.d-node-title');
            if (titleEl) {
                titleEl.textContent = activityDef.name;
            }
        }

        // 2. 更新位置（如果坐标变化）
        if (activityDef.positionCoord !== undefined) {
            const posX = parseFloat(activityDef.positionCoord?.x) || 0;
            const posY = parseFloat(activityDef.positionCoord?.y) || 0;
            nodeEl.style.left = (posX * this.scale + this.offsetX) + 'px';
            nodeEl.style.top = (posY * this.scale + this.offsetY) + 'px';
            console.log('[Canvas] Updated node position:', posX, posY);
        }

        // 3. 更新活动类型（影响样式和图标）
        if (activityDef.activityType !== undefined && oldActivity.activityType !== activityDef.activityType) {
            // 移除旧的类型类
            const oldTypeClass = this._getTypeClass(oldActivity.activityType);
            const oldFillClass = this._getFillClass(oldActivity.activityType);
            nodeEl.classList.remove(oldTypeClass, oldFillClass);

            // 添加新的类型类
            const newTypeClass = this._getTypeClass(activityDef.activityType);
            const newFillClass = this._getFillClass(activityDef.activityType);
            nodeEl.classList.add(newTypeClass, newFillClass);

            // 更新图标
            const iconEl = nodeEl.querySelector('.d-node-icon');
            if (iconEl) {
                iconEl.innerHTML = this._getIconSvg(activityDef.activityType);
            }

            // 更新大小
            const wasSmall = this._isSmallNode(oldActivity.activityType);
            const isSmall = this._isSmallNode(activityDef.activityType);
            if (wasSmall !== isSmall) {
                if (isSmall) {
                    nodeEl.style.width = '';
                    nodeEl.style.height = '';
                    // 隐藏名称
                    const nameEl = nodeEl.querySelector('.d-node-name');
                    if (nameEl) nameEl.remove();
                } else {
                    nodeEl.style.width = this.nodeWidth + 'px';
                    nodeEl.style.height = this.nodeHeight + 'px';
                    // 添加名称
                    const contentEl = nodeEl.querySelector('.d-node-content');
                    if (contentEl && !nodeEl.querySelector('.d-node-name')) {
                        const nameSpan = document.createElement('span');
                        nameSpan.className = 'd-node-name';
                        nameSpan.textContent = activityDef.name || oldActivity.name || '';
                        contentEl.appendChild(nameSpan);
                    }
                }
            }

            // 更新 dataset
            nodeEl.dataset.activityType = activityDef.activityType;
            console.log('[Canvas] Updated node type:', activityDef.activityType);
        }

        // 4. 更新实现方式（可能影响节点外观）
        if (activityDef.implementation !== undefined && oldActivity.implementation !== activityDef.implementation) {
            // 可以在这里添加实现方式特定的样式更新
            console.log('[Canvas] Updated node implementation:', activityDef.implementation);
        }

        // 5. 更新 dataset 中的活动数据引用
        nodeData.activity = { ...oldActivity, ...activityDef };

        // 6. 更新连线（如果位置变化）
        if (activityDef.positionCoord !== undefined) {
            this._updateEdges();
        }
    }

    /**
     * 更新连线显示
     * 当路由属性变化时，重新渲染连线以更新标签、条件等显示
     */
    _updateEdgeDisplay(routeDef) {
        if (!routeDef || !routeDef.routeDefId) return;

        // 更新 edges 中存储的路由数据
        const edgeData = this.edges.get(routeDef.routeDefId);
        if (edgeData) {
            // 合并更新的属性
            Object.assign(edgeData, routeDef);
            console.log('[Canvas] Edge data updated:', routeDef.routeDefId);
        }

        // 重新渲染所有连线以更新显示
        this._renderEdges();
    }

    _createSvgLayer() {
        this.svgLayer = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        this.svgLayer.classList.add('d-canvas-svg');
        this.svgLayer.style.cssText = `
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            pointer-events: none;
            z-index: 1;
        `;
        this.container.appendChild(this.svgLayer);
        
        this.edgeGroup = document.createElementNS('http://www.w3.org/2000/svg', 'g');
        this.svgLayer.appendChild(this.edgeGroup);
        
        this._createArrowMarker();
    }

    _createArrowMarker() {
        const defs = document.createElementNS('http://www.w3.org/2000/svg', 'defs');
        defs.innerHTML = `
            <marker id="arrowhead" markerWidth="10" markerHeight="7" 
                    refX="9" refY="3.5" orient="auto" markerUnits="strokeWidth">
                <polygon points="0 0, 10 3.5, 0 7" fill="var(--text-secondary)"/>
            </marker>
            <marker id="arrowhead-selected" markerWidth="10" markerHeight="7" 
                    refX="9" refY="3.5" orient="auto" markerUnits="strokeWidth">
                <polygon points="0 0, 10 3.5, 0 7" fill="var(--primary)"/>
            </marker>
        `;
        this.svgLayer.appendChild(defs);
    }

    _bindEvents() {
        this.container.addEventListener('dragover', (e) => {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'copy';
        });

        this.container.addEventListener('drop', (e) => {
            e.preventDefault();
            const data = e.dataTransfer.getData('application/json');
            if (data) {
                const item = JSON.parse(data);
                const rect = this.container.getBoundingClientRect();
                const x = (e.clientX - rect.left - this.offsetX) / this.scale;
                const y = (e.clientY - rect.top - this.offsetY) / this.scale;
                
                if (item.elementType === 'ROUTE') {
                    this._showRouteHint(x, y);
                } else {
                    this._createNode(item, x, y);
                }
            }
        });

        this.container.addEventListener('mousedown', (e) => {
            if (e.button === 0) {
                const isOnNode = e.target.closest('.d-node');
                const isOnPort = e.target.classList.contains('d-node-port');
                
                if (!isOnNode && !isOnPort) {
                    this._startSelection(e);
                }
            }
        });

        this.container.addEventListener('wheel', (e) => {
            e.preventDefault();
            const delta = e.deltaY > 0 ? -0.1 : 0.1;
            this._zoom(delta, e.clientX, e.clientY);
        });

    }

    /**
     * 获取当前选中的节点ID列表
     */
    getSelectedNodeIds() {
        return Array.from(this.selectedNodes || []);
    }

    /**
     * 获取当前选中的路由ID列表
     */
    getSelectedEdgeIds() {
        return Array.from(this.selectedEdges || []);
    }

    _startSelection(e) {
        const rect = this.container.getBoundingClientRect();
        this.isSelecting = true;
        this.selectionStartX = e.clientX - rect.left;
        this.selectionStartY = e.clientY - rect.top;
        
        this.selectionBox = document.createElement('div');
        this.selectionBox.className = 'd-selection-box';
        this.selectionBox.style.cssText = `
            position: absolute;
            left: ${this.selectionStartX}px;
            top: ${this.selectionStartY}px;
            width: 0;
            height: 0;
            border: 1px dashed var(--primary);
            background: rgba(var(--primary-rgb), 0.1);
            pointer-events: none;
            z-index: 1000;
        `;
        this.container.appendChild(this.selectionBox);

        const onMouseMove = (e) => {
            if (!this.isSelecting) return;
            
            const currentX = e.clientX - rect.left;
            const currentY = e.clientY - rect.top;
            
            const left = Math.min(this.selectionStartX, currentX);
            const top = Math.min(this.selectionStartY, currentY);
            const width = Math.abs(currentX - this.selectionStartX);
            const height = Math.abs(currentY - this.selectionStartY);
            
            this.selectionBox.style.left = left + 'px';
            this.selectionBox.style.top = top + 'px';
            this.selectionBox.style.width = width + 'px';
            this.selectionBox.style.height = height + 'px';
        };

        const onMouseUp = (e) => {
            if (this.isSelecting) {
                this._finishSelection(e);
            }
            
            this.isSelecting = false;
            if (this.selectionBox) {
                this.selectionBox.remove();
                this.selectionBox = null;
            }
            
            document.removeEventListener('mousemove', onMouseMove);
            document.removeEventListener('mouseup', onMouseUp);
        };

        document.addEventListener('mousemove', onMouseMove);
        document.addEventListener('mouseup', onMouseUp);
    }

    _finishSelection(e) {
        const rect = this.container.getBoundingClientRect();
        const currentX = e.clientX - rect.left;
        const currentY = e.clientY - rect.top;
        
        const left = Math.min(this.selectionStartX, currentX);
        const top = Math.min(this.selectionStartY, currentY);
        const right = Math.max(this.selectionStartX, currentX);
        const bottom = Math.max(this.selectionStartY, currentY);
        
        const movedDistance = Math.abs(currentX - this.selectionStartX) + Math.abs(currentY - this.selectionStartY);
        
        if (movedDistance < 5) {
            this._deselectAll();
            return;
        }
        
        this._deselectAll();
        
        this.nodes.forEach((nodeData, id) => {
            const { element, activity } = nodeData;
            const nodeRect = element.getBoundingClientRect();
            const nodeLeft = nodeRect.left - rect.left;
            const nodeTop = nodeRect.top - rect.top;
            const nodeRight = nodeLeft + nodeRect.width;
            const nodeBottom = nodeTop + nodeRect.height;
            
            if (nodeLeft < right && nodeRight > left && nodeTop < bottom && nodeBottom > top) {
                this.selectedNodes.add(id);
                element.classList.add('d-node-selected');
            }
        });
        
        this.edges.forEach((edgeData, id) => {
            const edgeEl = this.edgeGroup.querySelector(`[data-edge-id="${id}"]`);
            if (edgeEl) {
                const path = edgeEl.querySelector('path');
                if (path) {
                    const bbox = path.getBBox();
                    const edgeLeft = bbox.x;
                    const edgeTop = bbox.y;
                    const edgeRight = bbox.x + bbox.width;
                    const edgeBottom = bbox.y + bbox.height;
                    
                    if (edgeLeft < right && edgeRight > left && edgeTop < bottom && edgeBottom > top) {
                        edgeEl.classList.add('d-edge-selected');
                        if (!this.selectedEdges) this.selectedEdges = new Set();
                        this.selectedEdges.add(id);
                    }
                }
            }
        });
        
        const totalSelected = this.selectedNodes.size + (this.selectedEdges?.size || 0);
        
        if (this.selectedNodes.size === 1 && totalSelected === 1) {
            const id = Array.from(this.selectedNodes)[0];
            this.store.selectActivity(id);
        } else if (this.selectedEdges?.size === 1 && this.selectedNodes.size === 0) {
            const id = Array.from(this.selectedEdges)[0];
            const edge = this.edges.get(id);
            if (edge) {
                this.store.selectRoute(id);
            }
        } else if (totalSelected > 1) {
            this.store.emit('elements:multi-select', {
                nodes: Array.from(this.selectedNodes),
                edges: Array.from(this.selectedEdges || [])
            });
        }
    }

    _deselectAll() {
        this.container.querySelectorAll('.d-node').forEach(n => {
            n.classList.remove('d-node-selected');
        });
        this.edgeGroup.querySelectorAll('.d-edge').forEach(e => {
            e.classList.remove('d-edge-selected');
        });
        this.selectedEdge = null;
        if (this.selectedEdges) this.selectedEdges.clear();
        this.selectedNodes.clear();
        this.store.selectActivity(null);
        this.store.selectRoute(null);
    }

    _showRouteHint(x, y) {
        console.log('[Canvas] Route element dropped - use ports to connect nodes');
    }

    _drawGrid() {
        let grid = this.container.querySelector('.d-canvas-grid');
        if (!grid) {
            grid = document.createElement('div');
            grid.className = 'd-canvas-grid';
            this.container.appendChild(grid);
        }

        grid.style.cssText = `
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-image: 
                linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
                linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
            background-size: ${this.gridSize}px ${this.gridSize}px;
            transform: translate(${this.offsetX}px, ${this.offsetY}px) scale(${this.scale});
            transform-origin: 0 0;
            pointer-events: none;
            z-index: 0;
        `;
    }

    _createNode(item, x, y) {
        const activity = new ActivityDef({
            name: item.name,
            activityType: item.activityType,
            activityCategory: item.activityCategory || item.category || 'HUMAN',
            position: item.position || 'NORMAL',
            positionCoord: { x, y }
        });

        const node = this._renderNode(activity);
        this.container.appendChild(node);
        this.nodes.set(activity.activityDefId, { element: node, activity });

        this.store.addActivity(activity);
        this.store.selectActivity(activity.activityDefId);
    }

    _renderNode(activity) {
        console.log('[Canvas._renderNode] Rendering:', activity.activityDefId, 'type:', activity.activityType, 'coord:', activity.positionCoord);
        
        // 验证必要字段
        if (!activity.activityDefId) {
            console.error('[Canvas._renderNode] Missing activityDefId:', activity);
            return null;
        }
        
        const node = document.createElement('div');
        const typeClass = this._getTypeClass(activity.activityType);
        const fillClass = this._getFillClass(activity.activityType);
        
        node.className = `d-node ${typeClass} ${fillClass}`;
        node.dataset.id = activity.activityDefId;
        node.dataset.activityType = activity.activityType || 'TASK';

        const iconSvg = this._getIconSvg(activity.activityType);
        const isSmall = this._isSmallNode(activity.activityType);
        
        node.innerHTML = `
            <div class="d-node-content">
                <span class="d-node-icon">${iconSvg}</span>
                ${!isSmall ? `<span class="d-node-name">${activity.name}</span>` : ''}
            </div>
            <div class="d-node-ports">
                <div class="d-node-port d-node-port-in" data-port="in" title="输入端口"></div>
                <div class="d-node-port d-node-port-out" data-port="out" title="输出端口"></div>
            </div>
        `;

        const posX = parseFloat(activity.positionCoord?.x) || 0;
        const posY = parseFloat(activity.positionCoord?.y) || 0;
        
        node.style.left = (posX * this.scale + this.offsetX) + 'px';
        node.style.top = (posY * this.scale + this.offsetY) + 'px';
        
        if (!isSmall) {
            node.style.width = this.nodeWidth + 'px';
            node.style.height = this.nodeHeight + 'px';
        }

        this._bindNodeEvents(node, activity);

        return node;
    }

    _isSmallNode(activityType) {
        const smallTypes = ['START', 'END', 'XOR_GATEWAY', 'AND_GATEWAY', 'OR_GATEWAY'];
        return smallTypes.includes(activityType);
    }

    _getTypeClass(activityType) {
        const typeMap = {
            'START': 'd-node-type-start',
            'END': 'd-node-type-end',
            'XOR_GATEWAY': 'd-node-type-gateway',
            'AND_GATEWAY': 'd-node-type-gateway',
            'OR_GATEWAY': 'd-node-type-gateway'
        };
        return typeMap[activityType] || '';
    }

    _getFillClass(activityType) {
        const fillMap = {
            'START': 'd-node-fill-start',
            'END': 'd-node-fill-end',
            'TASK': 'd-node-fill-task',
            'SERVICE': 'd-node-fill-service',
            'SCRIPT': 'd-node-fill-script',
            'XOR_GATEWAY': 'd-node-fill-gateway',
            'AND_GATEWAY': 'd-node-fill-gateway',
            'OR_GATEWAY': 'd-node-fill-gateway',
            'LLM_TASK': 'd-node-fill-llm',
            'AGENT_TASK': 'd-node-fill-agent',
            'COORDINATOR': 'd-node-fill-coordinator',
            'SCENE': 'd-node-fill-scene',
            'SUBPROCESS': 'd-node-fill-subprocess',
            'CALL_ACTIVITY': 'd-node-fill-subprocess',
            'ACTIVITY_BLOCK': 'd-node-fill-block'
        };
        return fillMap[activityType] || 'd-node-fill-task';
    }

    _getIconClass(activityType) {
        const icons = {
            'START': 'start',
            'END': 'end',
            'TASK': 'user',
            'SERVICE': 'service',
            'SCRIPT': 'script',
            'XOR_GATEWAY': 'route',
            'AND_GATEWAY': 'route',
            'OR_GATEWAY': 'route',
            'LLM_TASK': 'brain',
            'AGENT_TASK': 'agent',
            'COORDINATOR': 'team',
            'SCENE': 'grid',
            'SUBPROCESS': 'subprocess',
            'CALL_ACTIVITY': 'external',
            'ACTIVITY_BLOCK': 'block'
        };
        return icons[activityType] || 'activity';
    }

    _getIconSvg(activityType) {
        const iconName = this._getIconClass(activityType);
        return IconManager.get(iconName);
    }

    _getNodeSize(activityType) {
        if (this._isSmallNode(activityType)) {
            return { width: this.smallNodeSize, height: this.smallNodeSize };
        }
        return { width: this.nodeWidth, height: this.nodeHeight };
    }

    _bindNodeEvents(node, activity) {
        node.addEventListener('mousedown', (e) => {
            if (e.target.classList.contains('d-node-port')) {
                e.stopPropagation();
                this._startDrawEdge(e, activity);
            } else {
                e.stopPropagation();
                const ctrlKey = e.ctrlKey || e.metaKey;
                
                if (ctrlKey) {
                    if (this.selectedNodes.has(activity.activityDefId)) {
                        this.selectedNodes.delete(activity.activityDefId);
                        node.classList.remove('d-node-selected');
                    } else {
                        this.selectedNodes.add(activity.activityDefId);
                        node.classList.add('d-node-selected');
                    }
                } else if (!this.selectedNodes.has(activity.activityDefId)) {
                    this._deselectAll();
                    this.selectedNodes.add(activity.activityDefId);
                    node.classList.add('d-node-selected');
                }
                
                if (this.selectedNodes.size === 1) {
                    this.store.selectActivity(activity.activityDefId);
                } else if (this.selectedNodes.size === 0) {
                    this.store.selectActivity(null);
                } else {
                    this.store.emit('nodes:multi-select', Array.from(this.selectedNodes));
                }
                
                this._startDragNode(e, node, activity);
            }
        });

        node.addEventListener('click', (e) => {
            e.stopPropagation();
        });

        node.addEventListener('dblclick', (e) => {
            e.stopPropagation();
            this._editNode(activity);
        });

        node.addEventListener('contextmenu', (e) => {
            e.preventDefault();
            e.stopPropagation();
            
            console.log('[Canvas] Node context menu:', activity.activityDefId);
            
            const items = [
                { label: '编辑', icon: 'edit', action: () => this._editNode(activity) },
                { label: '复制', icon: 'copy', action: () => this._copyNode(activity) },
                { label: '删除', icon: 'delete', action: () => this._deleteNode(activity.activityDefId) },
                { divider: true },
                { label: '属性', icon: 'setting', action: () => this.store.selectActivity(activity.activityDefId) }
            ];
            
            if (activity.implementation === 'IMPL_SUBFLOW' || activity.activityType === 'SUBPROCESS') {
                items.push({ divider: true });
                items.push({ label: '进入子流程', icon: 'subprocess', action: () => this._openSubprocess(activity) });
            }
            
            if (activity.implementation === 'IMPL_OUTFLOW') {
                items.push({ divider: true });
                items.push({ label: '进入外部流程', icon: 'outflow', action: () => this._openOutflow(activity) });
            }
            
            if (activity.activityCategory === 'AGENT') {
                items.push({ divider: true });
                items.push({ label: '配置Agent', icon: 'robot', action: () => this.store.selectActivity(activity.activityDefId) });
            }
            
            if (activity.activityCategory === 'SCENE') {
                items.push({ divider: true });
                items.push({ label: '配置场景', icon: 'scene', action: () => this.store.selectActivity(activity.activityDefId) });
            }
            
            if (typeof window.ContextMenu !== 'undefined' && window.ContextMenu.show) {
                window.ContextMenu.show(e.clientX, e.clientY, items);
            } else {
                console.error('[Canvas] ContextMenu is not defined or show method is missing');
            }
        });
        
        node.addEventListener('dblclick', (e) => {
            e.preventDefault();
            e.stopPropagation();
            
            console.log('[Canvas] Node double click:', activity.activityDefId, activity.implementation, activity.activityType);
            
            if (activity.implementation === 'IMPL_SUBFLOW' || activity.activityType === 'SUBPROCESS') {
                this._openSubprocess(activity);
            } else if (activity.implementation === 'IMPL_OUTFLOW') {
                this._openOutflow(activity);
            } else {
                this._editNode(activity);
            }
        });
    }

    _copyNode(activity) {
        console.log('[Canvas] Copy node:', activity.activityDefId);
    }

    _deleteNode(activityDefId) {
        this.store.removeActivity(activityDefId);
        this.removeNode(activityDefId);
    }

    _openSubprocess(activity) {
        console.log('[Canvas] Opening subprocess for activity:', activity.activityDefId);
        
        const subFlowConfig = activity.subFlow || {};
        const processDefId = subFlowConfig.processDefId;
        
        // 首先检查子流程标签页是否已经存在
        const subTabId = 'sub_' + activity.activityDefId;
        if (this.app.tabManager.tabs.has(subTabId)) {
            console.log('[Canvas] Subprocess tab already exists, activating:', subTabId);
            this.app.tabManager.activateTab(subTabId);
            return;
        }
        
        if (!processDefId) {
            const newProcessDef = {
                processDefId: 'sub_' + Date.now(),
                name: activity.name + ' - 子流程',
                description: '子流程: ' + activity.name,
                activities: [],
                routes: []
            };
            
            this.store.emit('subprocess:open', {
                activityId: activity.activityDefId,
                processDef: newProcessDef,
                parentTabId: this.app.tabManager.activeTabId
            });
        } else {
            this.store.emit('subprocess:open-existing', {
                activityId: activity.activityDefId,
                processDefId: processDefId,
                version: subFlowConfig.version,
                parentTabId: this.app.tabManager.activeTabId
            });
        }
    }

    _openOutflow(activity) {
        console.log('[Canvas] Opening outflow for activity:', activity.activityDefId);
        
        const outFlowConfig = activity.outFlow || {};
        const processDefId = outFlowConfig.processDefId;
        
        if (!processDefId) {
            this.app._toast('请先配置外部流程ID', 'warning');
            this.store.selectActivity(activity.activityDefId);
        } else {
            this.store.emit('outflow:open', {
                activityId: activity.activityDefId,
                processDefId: processDefId,
                parentTabId: this.app.tabManager.activeTabId
            });
        }
    }

    _startDragNode(e, node, activity) {
        e.preventDefault();
        this.isDragging = true;
        this.dragNode = { element: node, activity };
        
        const dragNodes = [];
        if (this.selectedNodes.size > 1 && this.selectedNodes.has(activity.activityDefId)) {
            this.selectedNodes.forEach(id => {
                const nodeData = this.nodes.get(id);
                if (nodeData) {
                    dragNodes.push(nodeData);
                    nodeData.element.classList.add('d-node-dragging');
                }
            });
        } else {
            dragNodes.push({ element: node, activity });
            node.classList.add('d-node-dragging');
        }
        
        const initialLefts = new Map();
        const initialTops = new Map();
        dragNodes.forEach(({ element, activity: act }) => {
            initialLefts.set(act.activityDefId, parseFloat(element.style.left) || 0);
            initialTops.set(act.activityDefId, parseFloat(element.style.top) || 0);
        });
        
        this.dragStartX = e.clientX;
        this.dragStartY = e.clientY;

        const onMouseMove = (e) => {
            if (!this.isDragging) return;
            
            const dx = e.clientX - this.dragStartX;
            const dy = e.clientY - this.dragStartY;
            
            dragNodes.forEach(({ element, activity: act }) => {
                const initialLeft = initialLefts.get(act.activityDefId);
                const initialTop = initialTops.get(act.activityDefId);
                
                const newLeft = initialLeft + dx;
                const newTop = initialTop + dy;
                
                element.style.left = newLeft + 'px';
                element.style.top = newTop + 'px';
                
                act.positionCoord = {
                    x: (newLeft - this.offsetX) / this.scale,
                    y: (newTop - this.offsetY) / this.scale
                };
            });
            
            this._updateEdges();
        };

        const onMouseUp = () => {
            this.isDragging = false;
            this.dragNode = null;
            dragNodes.forEach(({ element, activity: act }) => {
                element.classList.remove('d-node-dragging');
                // 保存坐标变化到store
                this.store.updateActivity(act);
                console.log('[Canvas] Activity position saved:', act.activityDefId, act.positionCoord);
            });
            document.removeEventListener('mousemove', onMouseMove);
            document.removeEventListener('mouseup', onMouseUp);
        };

        document.addEventListener('mousemove', onMouseMove);
        document.addEventListener('mouseup', onMouseUp);
    }

    _startDrawEdge(e, fromActivity) {
        this.isDrawingEdge = true;
        this.edgeStart = fromActivity;

        const rect = this.container.getBoundingClientRect();
        const fromNode = this.nodes.get(fromActivity.activityDefId);
        const nodeSize = this._getNodeSize(fromActivity.activityType);
        
        const fromCoord = fromActivity.positionCoord || { x: 0, y: 0 };
        const startX = (parseFloat(fromCoord.x) || 0) * this.scale + this.offsetX + nodeSize.width * this.scale / 2;
        const startY = (parseFloat(fromCoord.y) || 0) * this.scale + this.offsetY + nodeSize.height * this.scale;
        
        if (isNaN(startX) || isNaN(startY)) {
            console.error('[Canvas] _startDrawEdge: Invalid coordinates', { fromCoord, startX, startY });
            this.isDrawingEdge = false;
            this.edgeStart = null;
            return;
        }

        this.tempEdge = this._createEdgeElement(startX, startY, startX, startY, true);
        this.edgeGroup.appendChild(this.tempEdge);

        const onMouseMove = (e) => {
            if (!this.isDrawingEdge) return;
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            this._updateEdgeElement(this.tempEdge, startX, startY, x, y, true);
        };

        const onMouseUp = (e) => {
            if (this.isDrawingEdge) {
                const target = e.target.closest('.d-node');
                if (target) {
                    const toId = target.dataset.id;
                    if (toId && toId !== fromActivity.activityDefId) {
                        this._createEdge(fromActivity.activityDefId, toId);
                    }
                }
            }
            
            this.isDrawingEdge = false;
            this.edgeStart = null;
            if (this.tempEdge) {
                this.tempEdge.remove();
                this.tempEdge = null;
            }
            
            document.removeEventListener('mousemove', onMouseMove);
            document.removeEventListener('mouseup', onMouseUp);
        };

        document.addEventListener('mousemove', onMouseMove);
        document.addEventListener('mouseup', onMouseUp);
    }

    _createEdgeElement(x1, y1, x2, y2, isTemp = false, edge = null) {
        if (isNaN(x1) || isNaN(y1) || isNaN(x2) || isNaN(y2)) {
            console.error('[Canvas] _createEdgeElement called with NaN:', { x1, y1, x2, y2, edge });
            return document.createElementNS('http://www.w3.org/2000/svg', 'g');
        }
        
        const g = document.createElementNS('http://www.w3.org/2000/svg', 'g');
        g.classList.add('d-edge');
        
        const hitPath = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        hitPath.setAttribute('d', this._getEdgePath(x1, y1, x2, y2));
        hitPath.setAttribute('stroke', 'transparent');
        hitPath.setAttribute('stroke-width', '20');
        hitPath.setAttribute('fill', 'none');
        hitPath.style.pointerEvents = 'stroke';
        hitPath.style.cursor = 'pointer';
        
        const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        path.setAttribute('d', this._getEdgePath(x1, y1, x2, y2));
        path.setAttribute('stroke', 'var(--text-secondary)');
        path.setAttribute('stroke-width', '2');
        path.setAttribute('fill', 'none');
        path.setAttribute('marker-end', 'url(#arrowhead)');
        path.style.pointerEvents = 'none';
        
        g.appendChild(hitPath);
        g.appendChild(path);
        
        if (!isTemp && edge) {
            const midX = (x1 + x2) / 2;
            const midY = (y1 + y2) / 2;
            
            if (edge.name || edge.condition) {
                const labelGroup = document.createElementNS('http://www.w3.org/2000/svg', 'g');
                labelGroup.classList.add('d-edge-label-group');
                
                const labelBg = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
                const labelText = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                
                labelText.classList.add('d-edge-label');
                labelText.setAttribute('x', midX);
                labelText.setAttribute('y', midY - 5);
                labelText.setAttribute('text-anchor', 'middle');
                labelText.textContent = edge.name || '';
                
                const conditionText = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                conditionText.classList.add('d-edge-condition');
                conditionText.setAttribute('x', midX);
                conditionText.setAttribute('y', midY + 10);
                conditionText.setAttribute('text-anchor', 'middle');
                conditionText.textContent = edge.condition || '';
                
                labelGroup.appendChild(labelBg);
                labelGroup.appendChild(labelText);
                if (edge.condition) {
                    labelGroup.appendChild(conditionText);
                }
                
                g.appendChild(labelGroup);
                
                setTimeout(() => {
                    const bbox = labelGroup.getBBox();
                    labelBg.setAttribute('x', bbox.x - 4);
                    labelBg.setAttribute('y', bbox.y - 2);
                    labelBg.setAttribute('width', bbox.width + 8);
                    labelBg.setAttribute('height', bbox.height + 4);
                    labelBg.setAttribute('rx', 3);
                    labelBg.classList.add('d-edge-label-bg');
                }, 0);
            }
            
            if (edge.splitType === 'AND') {
                const indicator = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                indicator.classList.add('d-edge-split-indicator');
                indicator.setAttribute('x', x1 + 20);
                indicator.setAttribute('y', y1);
                indicator.textContent = '⊕';
                g.appendChild(indicator);
            }
            
            if (edge.joinType === 'AND') {
                const indicator = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                indicator.classList.add('d-edge-join-indicator');
                indicator.setAttribute('x', x2 - 20);
                indicator.setAttribute('y', y2);
                indicator.textContent = '⊗';
                g.appendChild(indicator);
            }
        }
        
        if (!isTemp) {
            hitPath.addEventListener('click', (e) => {
                e.stopPropagation();
                this._selectEdge(g);
            });
        }
        
        return g;
    }

    _updateEdgeElement(edge, x1, y1, x2, y2, isTemp = false) {
        if (isNaN(x1) || isNaN(y1) || isNaN(x2) || isNaN(y2)) {
            console.error('[Canvas] _updateEdgeElement called with NaN:', { x1, y1, x2, y2 });
            return;
        }
        const paths = edge.querySelectorAll('path');
        const pathD = this._getEdgePath(x1, y1, x2, y2);
        paths.forEach(path => {
            path.setAttribute('d', pathD);
        });
        
        // 更新标签位置
        if (!isTemp) {
            const midX = (x1 + x2) / 2;
            const midY = (y1 + y2) / 2;
            
            // 更新标签文本位置
            const labelText = edge.querySelector('.d-edge-label');
            if (labelText) {
                labelText.setAttribute('x', midX);
                labelText.setAttribute('y', midY - 5);
            }
            
            // 更新条件文本位置
            const conditionText = edge.querySelector('.d-edge-condition');
            if (conditionText) {
                conditionText.setAttribute('x', midX);
                conditionText.setAttribute('y', midY + 10);
            }
            
            // 更新标签背景位置
            const labelBg = edge.querySelector('.d-edge-label-bg');
            if (labelBg) {
                const labelGroup = edge.querySelector('.d-edge-label-group');
                if (labelGroup) {
                    setTimeout(() => {
                        const bbox = labelGroup.getBBox();
                        labelBg.setAttribute('x', bbox.x - 4);
                        labelBg.setAttribute('y', bbox.y - 2);
                    }, 0);
                }
            }
            
            // 更新split指示器位置
            const splitIndicator = edge.querySelector('.d-edge-split-indicator');
            if (splitIndicator) {
                splitIndicator.setAttribute('x', x1 + 20);
                splitIndicator.setAttribute('y', y1);
            }
            
            // 更新join指示器位置
            const joinIndicator = edge.querySelector('.d-edge-join-indicator');
            if (joinIndicator) {
                joinIndicator.setAttribute('x', x2 - 20);
                joinIndicator.setAttribute('y', y2);
            }
        }
    }

    _getEdgePath(x1, y1, x2, y2) {
        const dy = y2 - y1;
        const controlOffset = Math.min(Math.abs(dy) * 0.5, 80);
        
        const cy1 = y1 + controlOffset;
        const cy2 = y2 - controlOffset;
        return `M ${x1} ${y1} C ${x1} ${cy1}, ${x2} ${cy2}, ${x2} ${y2}`;
    }

    _createEdge(fromId, toId) {
        const edge = {
            id: 'edge_' + Date.now(),
            routeDefId: 'edge_' + Date.now(),
            from: fromId,
            to: toId,
            name: '新路由',
            condition: ''
        };
        
        this.edges.set(edge.id, edge);
        this._renderEdges();
        
        this.store.addRoute({
            routeDefId: edge.id,
            from: fromId,
            to: toId,
            name: '新路由'
        });
    }

    _selectEdge(edgeEl) {
        this._deselectAll();
        
        edgeEl.classList.add('d-edge-selected');
        const path = edgeEl.querySelector('path:not([stroke="transparent"])');
        if (path) {
            path.setAttribute('stroke', 'var(--primary)');
            path.setAttribute('marker-end', 'url(#arrowhead-selected)');
        }
        
        const edgeId = edgeEl.dataset.id;
        const edge = this.edges.get(edgeId);
        this.selectedEdge = edge;
        
        if (!this.selectedEdges) this.selectedEdges = new Set();
        this.selectedEdges.add(edgeId);
        
        if (edge && edge.routeDefId) {
            this.store.selectRoute(edge.routeDefId);
        }
    }

    _renderEdges() {
        this.edgeGroup.innerHTML = '';

        this.edges.forEach((edge, edgeId) => {
            const fromNode = this.nodes.get(edge.from);
            const toNode = this.nodes.get(edge.to);

            if (fromNode && toNode) {
                const fromSize = this._getNodeSize(fromNode.activity.activityType);
                const toSize = this._getNodeSize(toNode.activity.activityType);

                const fromCoord = fromNode.activity.positionCoord || { x: 0, y: 0 };
                const toCoord = toNode.activity.positionCoord || { x: 0, y: 0 };

                // 计算连线起点（from节点底部中心）
                const x1 = (parseFloat(fromCoord.x) || 0) * this.scale + this.offsetX + fromSize.width * this.scale / 2;
                const y1 = (parseFloat(fromCoord.y) || 0) * this.scale + this.offsetY + fromSize.height * this.scale;

                // 计算连线终点（to节点顶部中心）
                const x2 = (parseFloat(toCoord.x) || 0) * this.scale + this.offsetX + toSize.width * this.scale / 2;
                const y2 = (parseFloat(toCoord.y) || 0) * this.scale + this.offsetY;

                if (isNaN(x1) || isNaN(y1) || isNaN(x2) || isNaN(y2)) {
                    console.warn('[Canvas] Invalid edge coordinates:', { edgeId, fromCoord, toCoord, x1, y1, x2, y2 });
                    return;
                }

                const edgeEl = this._createEdgeElement(x1, y1, x2, y2, false, edge);
                edgeEl.dataset.id = edgeId;
                this.edgeGroup.appendChild(edgeEl);
            } else {
                console.warn('[Canvas] Node not found for edge:', edgeId, 'from:', edge.from, 'to:', edge.to);
            }
        });
    }

    _updateEdges() {
        this.edges.forEach((edge, edgeId) => {
            const fromNode = this.nodes.get(edge.from);
            const toNode = this.nodes.get(edge.to);

            if (fromNode && toNode) {
                const fromSize = this._getNodeSize(fromNode.activity.activityType);
                const toSize = this._getNodeSize(toNode.activity.activityType);

                const fromCoord = fromNode.activity.positionCoord || { x: 0, y: 0 };
                const toCoord = toNode.activity.positionCoord || { x: 0, y: 0 };

                // 计算连线起点（from节点底部中心）
                const x1 = (parseFloat(fromCoord.x) || 0) * this.scale + this.offsetX + fromSize.width * this.scale / 2;
                const y1 = (parseFloat(fromCoord.y) || 0) * this.scale + this.offsetY + fromSize.height * this.scale;

                // 计算连线终点（to节点顶部中心）
                const x2 = (parseFloat(toCoord.x) || 0) * this.scale + this.offsetX + toSize.width * this.scale / 2;
                const y2 = (parseFloat(toCoord.y) || 0) * this.scale + this.offsetY;

                const edgeEl = this.edgeGroup.querySelector(`[data-id="${edgeId}"]`);
                if (edgeEl && !isNaN(x1) && !isNaN(y1) && !isNaN(x2) && !isNaN(y2)) {
                    this._updateEdgeElement(edgeEl, x1, y1, x2, y2);
                }
            }
        });
    }

    _selectNode(node, activity, ctrlKey = false) {
        if (ctrlKey) {
            if (this.selectedNodes.has(activity.activityDefId)) {
                this.selectedNodes.delete(activity.activityDefId);
                node.classList.remove('d-node-selected');
            } else {
                this.selectedNodes.add(activity.activityDefId);
                node.classList.add('d-node-selected');
            }
        } else {
            this._deselectAll();
            this.selectedNodes.add(activity.activityDefId);
            node.classList.add('d-node-selected');
        }
        
        if (this.selectedNodes.size === 1) {
            this.store.selectActivity(activity.activityDefId);
        } else if (this.selectedNodes.size === 0) {
            this.store.selectActivity(null);
        } else {
            this.store.emit('nodes:multi-select', Array.from(this.selectedNodes));
        }
    }

    _editNode(activity) {
        console.log('[Canvas] Edit node:', activity);
    }

    _startPan(e) {
        this.isDragging = true;
        this.dragStartX = e.clientX - this.offsetX;
        this.dragStartY = e.clientY - this.offsetY;
        this.container.style.cursor = 'grabbing';

        const onMouseMove = (e) => {
            if (!this.isDragging) return;
            this.offsetX = e.clientX - this.dragStartX;
            this.offsetY = e.clientY - this.dragStartY;
            this._drawGrid();
            this._updateNodesPosition();
        };

        const onMouseUp = () => {
            this.isDragging = false;
            this.container.style.cursor = '';
            document.removeEventListener('mousemove', onMouseMove);
            document.removeEventListener('mouseup', onMouseUp);
        };

        document.addEventListener('mousemove', onMouseMove);
        document.addEventListener('mouseup', onMouseUp);
    }

    _updateNodesPosition() {
        this.nodes.forEach(({ element, activity }) => {
            const coord = activity.positionCoord || { x: 0, y: 0 };
            const x = parseFloat(coord.x) || 0;
            const y = parseFloat(coord.y) || 0;
            element.style.left = (x * this.scale + this.offsetX) + 'px';
            element.style.top = (y * this.scale + this.offsetY) + 'px';
        });
        this._renderEdges();
    }

    _zoom(delta, cx, cy) {
        const oldScale = this.scale;
        this.scale = Math.max(0.25, Math.min(2, this.scale + delta));
        
        const rect = this.container.getBoundingClientRect();
        const x = cx - rect.left;
        const y = cy - rect.top;
        
        this.offsetX = x - (x - this.offsetX) * (this.scale / oldScale);
        this.offsetY = y - (y - this.offsetY) * (this.scale / oldScale);
        
        this._drawGrid();
        this._updateNodesPosition();
        
        const zoomLevel = document.getElementById('zoomLevel');
        if (zoomLevel) {
            zoomLevel.textContent = Math.round(this.scale * 100) + '%';
        }
    }

    zoomIn() {
        this._zoom(0.1, this.container.clientWidth / 2, this.container.clientHeight / 2);
    }

    zoomOut() {
        this._zoom(-0.1, this.container.clientWidth / 2, this.container.clientHeight / 2);
    }

    fitToScreen() {
        if (this.nodes.size === 0) {
            this.scale = 1;
            this.offsetX = 0;
            this.offsetY = 0;
            this._drawGrid();
            return;
        }

        let minX = Infinity, minY = Infinity, maxX = -Infinity, maxY = -Infinity;
        this.nodes.forEach(({ activity }) => {
            const size = this._getNodeSize(activity.activityType);
            const coord = activity.positionCoord || { x: 0, y: 0 };
            const x = parseFloat(coord.x) || 0;
            const y = parseFloat(coord.y) || 0;
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x + size.width);
            maxY = Math.max(maxY, y + size.height);
        });

        const padding = 50;
        const contentWidth = maxX - minX + padding * 2;
        const contentHeight = maxY - minY + padding * 2;
        const containerWidth = this.container.clientWidth;
        const containerHeight = this.container.clientHeight;

        this.scale = Math.min(containerWidth / contentWidth, containerHeight / contentHeight, 1);
        this.offsetX = (containerWidth - contentWidth * this.scale) / 2 - minX * this.scale + padding;
        this.offsetY = (containerHeight - contentHeight * this.scale) / 2 - minY * this.scale + padding;

        this._drawGrid();
        this._updateNodesPosition();
        
        const zoomLevel = document.getElementById('zoomLevel');
        if (zoomLevel) {
            zoomLevel.textContent = Math.round(this.scale * 100) + '%';
        }
    }

    loadProcess(processDef) {
        console.log('[Canvas] Loading process:', processDef);
        this.clear();

        if (!processDef.activities) {
            processDef.activities = [];
        }

        // 确保每个活动都有正确的坐标
        processDef.activities.forEach(activity => {
            if (!activity.positionCoord || typeof activity.positionCoord !== 'object') {
                activity.positionCoord = { x: 100, y: 100 };
            } else {
                // 确保坐标是数字类型
                activity.positionCoord = {
                    x: parseFloat(activity.positionCoord.x) || 0,
                    y: parseFloat(activity.positionCoord.y) || 0
                };
            }
        });

        // 注意：不再自动补充开始/结束节点
        // 开始/结束节点应该由用户自行添加，或者从流程扩展属性中读取
        // 这样可以避免：1)删不掉的节点 2)无限补充的问题

        if (processDef.activities.length === 0) {
            console.warn('[Canvas] No activities to render');
            return;
        }

        // 渲染所有活动节点
        console.log('[Canvas] Rendering', processDef.activities.length, 'activities');
        processDef.activities.forEach((activity, index) => {
            try {
                console.log(`[Canvas] Rendering activity ${index}:`, activity.activityDefId, activity.name, 'type:', activity.activityType, 'coord:', activity.positionCoord);
                const node = this._renderNode(activity);
                if (!node) {
                    console.error(`[Canvas] _renderNode returned null for activity ${index}`);
                    return;
                }
                this.container.appendChild(node);
                this.nodes.set(activity.activityDefId, { element: node, activity });
                console.log(`[Canvas] Activity ${index} rendered successfully`);
            } catch (error) {
                console.error(`[Canvas] Error rendering activity ${index}:`, error, activity);
            }
        });
        console.log('[Canvas] Finished rendering activities, total nodes:', this.nodes.size);

        // 处理路由 - 标准化from/to字段
        if (processDef.routes && processDef.routes.length > 0) {
            processDef.routes.forEach(route => {
                const routeId = route.routeDefId || route.id || 'route_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);

                // 标准化from字段
                let fromId = route.from || route.fromActivityDefId;
                let toId = route.to || route.toActivityDefId;

                // 验证from/to节点是否存在，不存在则跳过此路由
                if (fromId && !this.nodes.has(fromId)) {
                    console.warn(`[Canvas] Route ${routeId} references non-existent from node: ${fromId}, skipping route`);
                    return;
                }
                if (toId && !this.nodes.has(toId)) {
                    console.warn(`[Canvas] Route ${routeId} references non-existent to node: ${toId}, skipping route`);
                    return;
                }

                this.edges.set(routeId, {
                    id: routeId,
                    routeDefId: routeId,
                    from: fromId,
                    to: toId,
                    name: route.name || '路由',
                    condition: route.condition || route.routeCondition || ''
                });
            });
        }

        this._renderEdges();
    }

    removeNode(activityId) {
        const nodeData = this.nodes.get(activityId);
        if (nodeData) {
            nodeData.element.remove();
            this.nodes.delete(activityId);
        }
        
        this.selectedNodes.delete(activityId);
        
        const edgesToRemove = [];
        this.edges.forEach((edge, id) => {
            if (edge.from === activityId || edge.to === activityId) {
                edgesToRemove.push(id);
            }
        });
        edgesToRemove.forEach(id => this.edges.delete(id));
        
        this._renderEdges();
    }

    removeEdge(routeId) {
        const edgeData = this.edges.get(routeId);
        if (edgeData) {
            const edgeEl = this.edgeGroup.querySelector(`[data-id="${routeId}"]`);
            if (edgeEl) {
                edgeEl.remove();
            }
            this.edges.delete(routeId);
        }
        
        if (this.selectedEdges) {
            this.selectedEdges.delete(routeId);
        }
        
        this._renderEdges();
    }

    clear() {
        this.nodes.forEach(({ element }) => element.remove());
        this.nodes.clear();
        this.edges.clear();
        this.selectedNodes.clear();
        this.edgeGroup.innerHTML = '';
    }

    selectNode(activityId) {
        const nodeData = this.nodes.get(activityId);
        if (nodeData) {
            this._selectNode(nodeData.element, nodeData.activity);
        }
    }
}

window.Canvas = Canvas;
