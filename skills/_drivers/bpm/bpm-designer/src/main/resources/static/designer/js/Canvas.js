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
     */
    _updateNodeDisplay(activityDef) {
        if (!activityDef || !activityDef.activityDefId) return;

        const nodeData = this.nodes.get(activityDef.activityDefId);
        if (nodeData && nodeData.element) {
            const nodeEl = nodeData.element;
            const nameEl = nodeEl.querySelector('.d-node-name');
            if (nameEl && activityDef.name !== undefined) {
                nameEl.textContent = activityDef.name;
                console.log('[Canvas] Updated node name:', activityDef.name);
            }

            const titleEl = nodeEl.querySelector('.d-node-title');
            if (titleEl && activityDef.name !== undefined) {
                titleEl.textContent = activityDef.name;
            }
        }
    }

    /**
     * 更新连线显示
     */
    _updateEdgeDisplay(routeDef) {
        if (!routeDef || !routeDef.routeDefId) return;

        const edgeEl = this.edges.get(routeDef.routeDefId);
        if (edgeEl && edgeEl.label) {
            // 更新连线标签
            if (routeDef.name !== undefined) {
                edgeEl.label.textContent = routeDef.name;
                console.log('[Canvas] Updated edge label:', routeDef.name);
            }
        }
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
        this.selectedNodes.clear();
        this.store.selectActivity(null);
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
            activityCategory: item.category || 'HUMAN',
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
        const node = document.createElement('div');
        const typeClass = this._getTypeClass(activity.activityType);
        const fillClass = this._getFillClass(activity.activityType);
        
        node.className = `d-node ${typeClass} ${fillClass}`;
        node.dataset.id = activity.activityDefId;
        node.dataset.activityType = activity.activityType;

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
            from: fromId,
            to: toId,
            name: '路由',
            condition: ''
        };
        
        this.edges.set(edge.id, edge);
        this._renderEdges();
        
        this.store.addRoute({
            routeDefId: edge.id,
            from: fromId,
            to: toId
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
        
        if (edge) {
            this.store.emit('route:select', edge);
        }
    }

    _renderEdges() {
        this.edgeGroup.innerHTML = '';
        
        console.log('[Canvas] _renderEdges called, nodes size:', this.nodes.size, 'edges size:', this.edges.size);
        console.log('[Canvas] scale:', this.scale, 'offsetX:', this.offsetX, 'offsetY:', this.offsetY);
        
        this.edges.forEach((edge, edgeId) => {
            const fromNode = this.nodes.get(edge.from);
            const toNode = this.nodes.get(edge.to);
            
            console.log('[Canvas] Processing edge:', edgeId, 'from:', edge.from, 'to:', edge.to);
            console.log('[Canvas] fromNode:', fromNode ? 'found' : 'not found', 'toNode:', toNode ? 'found' : 'not found');
            
            if (fromNode && toNode) {
                const fromSize = this._getNodeSize(fromNode.activity.activityType);
                const toSize = this._getNodeSize(toNode.activity.activityType);
                
                console.log('[Canvas] fromSize:', fromSize, 'toSize:', toSize);
                console.log('[Canvas] fromNode.activity.positionCoord:', fromNode.activity.positionCoord);
                console.log('[Canvas] toNode.activity.positionCoord:', toNode.activity.positionCoord);
                
                const fromCoord = fromNode.activity.positionCoord || { x: 0, y: 0 };
                const toCoord = toNode.activity.positionCoord || { x: 0, y: 0 };
                
                const x1 = (parseFloat(fromCoord.x) || 0) * this.scale + this.offsetX + fromSize.width * this.scale / 2;
                const y1 = (parseFloat(fromCoord.y) || 0) * this.scale + this.offsetY + fromSize.height * this.scale;
                const x2 = (parseFloat(toCoord.x) || 0) * this.scale + this.offsetX + toSize.width * this.scale / 2;
                const y2 = (parseFloat(toCoord.y) || 0) * this.scale + this.offsetY;
                
                console.log('[Canvas] Edge coordinates:', { x1, y1, x2, y2 });
                
                if (isNaN(x1) || isNaN(y1) || isNaN(x2) || isNaN(y2)) {
                    console.warn('[Canvas] Invalid edge coordinates:', { edgeId, fromCoord, toCoord, x1, y1, x2, y2 });
                    return;
                }
                
                const edgeEl = this._createEdgeElement(x1, y1, x2, y2, false, edge);
                edgeEl.dataset.id = edgeId;
                this.edgeGroup.appendChild(edgeEl);
            } else {
                console.warn('[Canvas] Node not found for edge:', edgeId, 'from:', edge.from, 'to:', edge.to);
                console.log('[Canvas] Available nodes:', Array.from(this.nodes.keys()));
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
                
                const x1 = (parseFloat(fromCoord.x) || 0) * this.scale + this.offsetX + fromSize.width * this.scale / 2;
                const y1 = (parseFloat(fromCoord.y) || 0) * this.scale + this.offsetY + fromSize.height * this.scale;
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
        console.log('[Canvas] Activities:', processDef.activities?.length);
        console.log('[Canvas] Routes:', processDef.routes?.length);
        this.clear();
        
        if (!processDef.activities) {
            processDef.activities = [];
        }
        
        const hasStart = processDef.activities.some(a => a.activityType === 'START');
        const hasEnd = processDef.activities.some(a => a.activityType === 'END');
        
        const normalActivities = processDef.activities.filter(a => 
            a.activityType !== 'START' && a.activityType !== 'END'
        );
        
        if (!hasStart && normalActivities.length > 0) {
            const startNode = {
                activityDefId: 'start_' + processDef.processDefId,
                name: '开始',
                position: 'START',
                activityType: 'START',
                positionCoord: { x: 50, y: 200 }
            };
            processDef.activities.unshift(startNode);
            console.log('[Canvas] Auto-added START node');
        }
        
        if (!hasEnd && normalActivities.length > 0) {
            const endNode = {
                activityDefId: 'end_' + processDef.processDefId,
                name: '结束',
                position: 'END',
                activityType: 'END',
                positionCoord: { x: 700, y: 200 }
            };
            processDef.activities.push(endNode);
            console.log('[Canvas] Auto-added END node');
        }
        
        if (processDef.activities.length === 0) {
            console.warn('[Canvas] No activities to render');
            return;
        }
        
        processDef.activities.forEach(activity => {
            console.log('[Canvas] Adding activity:', activity.activityDefId, 'positionCoord:', activity.positionCoord);
            const node = this._renderNode(activity);
            this.container.appendChild(node);
            this.nodes.set(activity.activityDefId, { element: node, activity });
        });
        
        console.log('[Canvas] Nodes map size:', this.nodes.size);
        console.log('[Canvas] Nodes keys:', Array.from(this.nodes.keys()));
        
        if (processDef.routes && processDef.routes.length > 0) {
            processDef.routes.forEach(route => {
                console.log('[Canvas] Adding route:', route.routeDefId, 'from:', route.from, 'to:', route.to);
                this.edges.set(route.routeDefId, {
                    id: route.routeDefId,
                    from: route.from,
                    to: route.to,
                    name: route.name || '路由',
                    condition: route.condition || ''
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
            const edgeEl = this.edgeGroup.querySelector(`[data-edge-id="${routeId}"]`);
            if (edgeEl) {
                edgeEl.remove();
            }
            this.edges.delete(routeId);
        }
        
        if (this.selectedEdges) {
            this.selectedEdges.delete(routeId);
        }
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
