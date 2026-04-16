/**
 * ProcessDef - 流程定义模型 (已修复版本)
 * 支持完整的扩展属性：startNode, endNodes, listeners, rightGroups
 */

class ProcessDef {
    constructor(data) {
        // 基本属性
        this.processDefId = data?.processDefId || this._generateId();
        this.name = data?.name || '新流程';
        this.description = data?.description || '';
        
        // 修复：添加缺失的分类和访问级别
        this.category = data?.category || data?.classification || '办公流程';
        this.classification = this.category;
        this.systemCode = data?.systemCode || 'bpm';
        this.accessLevel = data?.accessLevel || 'PUBLIC';
        
        // 版本信息 (BPD属性) - 修复：添加完整版本字段
        this.version = data?.version || 1;
        this.status = data?.status || data?.state || 'DRAFT';
        this.state = this.status;
        this.creatorName = data?.creatorName || '';
        this.modifierId = data?.modifierId || '';
        this.modifierName = data?.modifierName || '';
        this.modifyTime = data?.modifyTime || data?.updatedTime || null;
        this.createTime = data?.createTime || data?.createdTime || null;
        this.activeTime = data?.activeTime || null;
        this.freezeTime = data?.freezeTime || null;
        
        // 时限配置 - 修复：添加
        this.limit = data?.limit || 0;
        this.durationUnit = data?.durationUnit || 'D';
        
        // 开始节点 (XPDL格式) - 修复：添加
        this.startNode = this._normalizeStartNode(data?.startNode, data?.activities);
        
        // 结束节点列表 (XPDL格式，支持多个) - 修复：添加
        this.endNodes = this._normalizeEndNodes(data?.endNodes, data?.activities);
        
        // 监听器列表 (XML格式) - 修复：添加
        this.listeners = data?.listeners || [];
        
        // 权限组列表 (XML格式) - 修复：添加
        this.rightGroups = data?.rightGroups || [];
        
        // 活动和路由 - 修复：添加错误处理
        this.activities = (data?.activities || []).map(a => {
            try {
                return a instanceof ActivityDef ? a : new ActivityDef(a);
            } catch (error) {
                console.warn('[ProcessDef] Failed to create ActivityDef:', error, a);
                // 返回一个最小化的活动对象
                return new ActivityDef({
                    activityDefId: a?.activityDefId || a?.id || 'unknown',
                    name: a?.name || '未知活动',
                    activityType: a?.activityType || 'TASK'
                });
            }
        });
        this.routes = data?.routes || [];
        
        // 扩展属性 - 修复：将前端特有字段放入extendedAttributes
        this.extendedAttributes = data?.extendedAttributes || {};
        this._handleExtendedFields(data);
    }

    /**
     * 生成唯一ID
     */
    _generateId() {
        return 'proc_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    /**
     * 标准化开始节点
     */
    _normalizeStartNode(startNode, activities) {
        // 如果已有startNode，直接使用
        if (startNode && typeof startNode === 'object') {
            return {
                participantId: startNode.participantId || 'Participant_Start',
                firstActivityId: startNode.firstActivityId || '',
                positionCoord: {
                    x: parseInt(startNode.positionCoord?.x) || 0,
                    y: parseInt(startNode.positionCoord?.y) || 0
                },
                routing: startNode.routing || 'NO_ROUTING'
            };
        }
        
        // 从活动中查找开始节点
        const startActivity = activities?.find(a => 
            a.position === 'START' || a.activityType === 'START'
        );
        
        if (startActivity) {
            return {
                participantId: startActivity.participantId || 'Participant_Start',
                firstActivityId: startActivity.activityDefId,
                positionCoord: startActivity.positionCoord || { x: 50, y: 200 },
                routing: 'NO_ROUTING'
            };
        }
        
        return null;
    }

    /**
     * 标准化结束节点列表
     */
    _normalizeEndNodes(endNodes, activities) {
        // 如果已有endNodes数组，直接使用
        if (endNodes && Array.isArray(endNodes) && endNodes.length > 0) {
            return endNodes.map(node => ({
                participantId: node.participantId || 'Participant_End',
                lastActivityId: node.lastActivityId || '',
                positionCoord: {
                    x: parseInt(node.positionCoord?.x) || 0,
                    y: parseInt(node.positionCoord?.y) || 0
                },
                routing: node.routing || 'NO_ROUTING'
            }));
        }
        
        // 从活动中查找结束节点
        const endActivities = activities?.filter(a => 
            a.position === 'END' || a.activityType === 'END'
        ) || [];
        
        return endActivities.map(act => ({
            participantId: act.participantId || 'Participant_End',
            lastActivityId: act.activityDefId,
            positionCoord: act.positionCoord || { x: 800, y: 200 },
            routing: 'NO_ROUTING'
        }));
    }

    /**
     * 处理扩展字段
     */
    _handleExtendedFields(data) {
        const extendedFields = [
            'formulas', 'parameters', 'activitySets', 'subProcessRefs',
            'agentConfig', 'sceneConfig'
        ];
        
        extendedFields.forEach(field => {
            if (data?.[field] !== undefined) {
                this.extendedAttributes[field] = data[field];
            }
        });
    }

    /**
     * 添加活动 - 修复：同步更新startNode/endNodes
     */
    addActivity(activity) {
        if (!(activity instanceof ActivityDef)) {
            activity = new ActivityDef(activity);
        }
        this.activities.push(activity);
        
        // 如果是开始/结束节点，更新对应的节点配置
        if (activity.position === 'START') {
            this.startNode = {
                participantId: activity.participantId,
                firstActivityId: activity.activityDefId,
                positionCoord: activity.positionCoord,
                routing: 'NO_ROUTING'
            };
        } else if (activity.position === 'END') {
            const existingIndex = this.endNodes.findIndex(
                n => n.lastActivityId === activity.activityDefId
            );
            const endNode = {
                participantId: activity.participantId,
                lastActivityId: activity.activityDefId,
                positionCoord: activity.positionCoord,
                routing: 'NO_ROUTING'
            };
            if (existingIndex >= 0) {
                this.endNodes[existingIndex] = endNode;
            } else {
                this.endNodes.push(endNode);
            }
        }
        
        return activity;
    }

    /**
     * 移除活动 - 修复：同步更新startNode/endNodes
     */
    removeActivity(activityDefId) {
        const index = this.activities.findIndex(a => a.activityDefId === activityDefId);
        if (index >= 0) {
            const activity = this.activities[index];
            this.activities.splice(index, 1);
            
            // 移除相关的路由
            this.routes = this.routes.filter(
                r => r.from !== activityDefId && r.to !== activityDefId &&
                     r.fromActivityDefId !== activityDefId && r.toActivityDefId !== activityDefId
            );
            
            // 更新开始/结束节点
            if (activity.position === 'START') {
                this.startNode = null;
            } else if (activity.position === 'END') {
                this.endNodes = this.endNodes.filter(
                    n => n.lastActivityId !== activityDefId
                );
            }
            
            return true;
        }
        return false;
    }

    /**
     * 获取活动
     */
    getActivity(activityDefId) {
        return this.activities.find(a => a.activityDefId === activityDefId);
    }

    /**
     * 获取开始活动
     */
    getStartActivity() {
        return this.activities.find(a => a.position === 'START');
    }

    /**
     * 获取结束活动列表
     */
    getEndActivities() {
        return this.activities.filter(a => a.position === 'END');
    }

    /**
     * 添加路由
     */
    addRoute(route) {
        this.routes.push({
            routeDefId: route.routeDefId || route.id || this._generateRouteId(),
            id: route.routeDefId || route.id || this._generateRouteId(),
            name: route.name || '',
            description: route.description || '',
            from: route.from || route.fromActivityDefId,
            fromActivityDefId: route.from || route.fromActivityDefId,
            to: route.to || route.toActivityDefId,
            toActivityDefId: route.to || route.toActivityDefId,
            order: route.order || route.routeOrder || 1,
            routeOrder: route.order || route.routeOrder || 1,
            direction: route.direction || route.routeDirection || 'FORWARD',
            routeDirection: route.direction || route.routeDirection || 'FORWARD',
            condition: route.condition || route.routeCondition || '',
            routeCondition: route.condition || route.routeCondition || '',
            conditionType: route.conditionType || route.routeConditionType || 'DEFAULT',
            routeConditionType: route.conditionType || route.routeConditionType || 'DEFAULT',
            routing: route.routing || ''
        });
    }

    /**
     * 生成路由ID
     */
    _generateRouteId() {
        return 'route_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5);
    }

    /**
     * 移除路由
     */
    removeRoute(routeDefId) {
        const index = this.routes.findIndex(r => 
            r.routeDefId === routeDefId || r.id === routeDefId
        );
        if (index >= 0) {
            this.routes.splice(index, 1);
            return true;
        }
        return false;
    }

    /**
     * 添加监听器 - 修复：新增
     */
    addListener(listener) {
        this.listeners.push({
            id: listener.id || listener.listenerId || this._generateListenerId(),
            name: listener.name || listener.listenerName || '',
            event: listener.event || listener.listenerEvent || 'PROCESS_START',
            realizeClass: listener.realizeClass || listener.className || ''
        });
    }

    /**
     * 生成监听器ID
     */
    _generateListenerId() {
        return 'listener_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5);
    }

    /**
     * 移除监听器 - 修复：新增
     */
    removeListener(listenerId) {
        const index = this.listeners.findIndex(l => l.id === listenerId);
        if (index >= 0) {
            this.listeners.splice(index, 1);
            return true;
        }
        return false;
    }

    /**
     * 添加权限组 - 修复：新增
     */
    addRightGroup(rightGroup) {
        this.rightGroups.push({
            id: rightGroup.id || rightGroup.rightGroupId || this._generateRightGroupId(),
            name: rightGroup.name || rightGroup.rightGroupName || '',
            code: rightGroup.code || rightGroup.rightGroupCode || '',
            order: rightGroup.order || this.rightGroups.length + 1,
            defaultGroup: rightGroup.defaultGroup || rightGroup.isDefault || false
        });
        // 重新排序
        this._sortRightGroups();
    }

    /**
     * 生成权限组ID
     */
    _generateRightGroupId() {
        return 'rg_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5);
    }

    /**
     * 移除权限组 - 修复：新增
     */
    removeRightGroup(rightGroupId) {
        const index = this.rightGroups.findIndex(rg => rg.id === rightGroupId);
        if (index >= 0) {
            this.rightGroups.splice(index, 1);
            this._sortRightGroups();
            return true;
        }
        return false;
    }

    /**
     * 排序权限组
     */
    _sortRightGroups() {
        this.rightGroups.sort((a, b) => a.order - b.order);
    }

    /**
     * 获取默认权限组 - 修复：新增
     */
    getDefaultRightGroup() {
        return this.rightGroups.find(rg => rg.defaultGroup);
    }

    /**
     * 更新开始节点坐标 - 修复：新增
     */
    updateStartNodeCoord(x, y) {
        if (this.startNode) {
            this.startNode.positionCoord = { x, y };
        }
    }

    /**
     * 更新结束节点坐标 - 修复：新增
     */
    updateEndNodeCoord(activityDefId, x, y) {
        const endNode = this.endNodes.find(n => n.lastActivityId === activityDefId);
        if (endNode) {
            endNode.positionCoord = { x, y };
        }
    }

    /**
     * 同步活动坐标到节点配置 - 修复：新增
     */
    syncActivityCoordToNode(activity) {
        if (activity.position === 'START' && this.startNode) {
            this.startNode.positionCoord = { ...activity.positionCoord };
        } else if (activity.position === 'END') {
            this.updateEndNodeCoord(activity.activityDefId, 
                activity.positionCoord.x, activity.positionCoord.y);
        }
    }

    /**
     * 转换为JSON (用于发送到后端) - 修复：完整输出所有属性
     */
    toJSON() {
        return {
            // 基本属性
            processDefId: this.processDefId,
            name: this.name,
            description: this.description,
            classification: this.classification,
            category: this.category,
            systemCode: this.systemCode,
            accessLevel: this.accessLevel,
            
            // 版本信息 - 修复：添加完整字段
            version: this.version,
            state: this.state,
            status: this.status,
            creatorName: this.creatorName,
            modifierId: this.modifierId,
            modifierName: this.modifierName,
            modifyTime: this.modifyTime,
            createTime: this.createTime,
            activeTime: this.activeTime,
            freezeTime: this.freezeTime,
            
            // 时限配置 - 修复：添加
            limit: this.limit,
            durationUnit: this.durationUnit,
            
            // 开始/结束节点 - 修复：添加
            startNode: this.startNode,
            endNodes: this.endNodes,
            
            // 监听器和权限组 - 修复：添加
            listeners: this.listeners,
            rightGroups: this.rightGroups,
            
            // 活动和路由 - 修复：确保activity是ActivityDef实例
            // 注意：只过滤 activityType === 'START'/'END' 的虚拟节点
            // 不过滤 position === 'START'/'END' 的真实业务活动（如起草、归档）
            activities: this.activities
                .filter(a => {
                    // 只过滤真正的开始/结束虚拟节点（activityType为START/END）
                    // position为START/END的是真实业务活动，不应过滤
                    const isVirtualNode = a.activityType === 'START' || a.activityType === 'END';
                    return !isVirtualNode;
                })
                .map((a, index) => {
                    try {
                        if (a instanceof ActivityDef) {
                            return a.toJSON();
                        } else if (a && typeof a.toJSON === 'function') {
                            return a.toJSON();
                        } else if (a && typeof a === 'object') {
                            // 如果是普通对象，直接返回
                            return a;
                        } else {
                            console.warn(`[ProcessDef.toJSON] Invalid activity at index ${index}:`, a);
                            return null;
                        }
                    } catch (error) {
                        console.error(`[ProcessDef.toJSON] Error converting activity at index ${index}:`, error, a);
                        return null;
                    }
                }).filter(a => a !== null),
            routes: this.routes,
            
            // 扩展属性
            extendedAttributes: this._isEmptyObject(this.extendedAttributes) 
                ? undefined : this.extendedAttributes
        };
    }

    /**
     * 从JSON创建实例 - 修复：支持完整属性
     */
    static fromJSON(json) {
        return new ProcessDef(json);
    }

    /**
     * 从后端数据创建实例 - 修复：使用DataAdapter
     */
    static fromBackend(backendData) {
        // 如果有DataAdapter，使用它进行转换
        if (window.dataAdapter) {
            const frontendData = window.dataAdapter.fromBackend(backendData);
            return new ProcessDef(frontendData);
        }
        // 否则直接使用数据
        return new ProcessDef(backendData);
    }

    /**
     * 转换为后端格式 - 修复：使用DataAdapter
     */
    toBackend() {
        const json = this.toJSON();
        // 如果有DataAdapter，使用它进行转换
        if (window.dataAdapter) {
            return window.dataAdapter.toBackend(json);
        }
        return json;
    }

    /**
     * 验证流程数据
     */
    validate() {
        const errors = [];
        
        // 基本验证
        if (!this.name || this.name.trim() === '') {
            errors.push('流程名称不能为空');
        }
        
        if (!this.processDefId) {
            errors.push('流程ID不能为空');
        }
        
        // 验证开始节点 - 修复：添加验证
        if (!this.startNode) {
            errors.push('缺少开始节点配置');
        } else {
            if (!this.startNode.firstActivityId) {
                errors.push('开始节点缺少firstActivityId');
            }
        }
        
        // 验证结束节点 - 修复：添加验证
        if (!this.endNodes || this.endNodes.length === 0) {
            errors.push('缺少结束节点配置');
        }
        
        // 验证活动
        if (this.activities.length === 0) {
            errors.push('流程至少需要包含一个活动');
        }
        
        this.activities.forEach((activity, index) => {
            const activityErrors = activity.validate();
            if (activityErrors.length > 0) {
                errors.push(`活动[${index}](${activity.name}): ${activityErrors.join(', ')}`);
            }
        });
        
        // 验证监听器 - 修复：添加验证
        this.listeners.forEach((listener, index) => {
            if (!listener.name) {
                errors.push(`监听器[${index}]: 名称不能为空`);
            }
            if (!listener.realizeClass) {
                errors.push(`监听器[${index}]: 实现类不能为空`);
            }
        });
        
        // 验证权限组 - 修复：添加验证
        this.rightGroups.forEach((rg, index) => {
            if (!rg.name) {
                errors.push(`权限组[${index}]: 名称不能为空`);
            }
            if (!rg.code) {
                errors.push(`权限组[${index}]: 代码不能为空`);
            }
        });
        
        return errors;
    }

    /**
     * 检查是否有验证错误
     */
    isValid() {
        return this.validate().length === 0;
    }

    /**
     * 克隆流程
     */
    clone() {
        const json = this.toJSON();
        json.processDefId = this._generateId();
        json.name = json.name + '_复制';
        json.version = 1;
        json.status = 'DRAFT';
        json.state = 'DRAFT';
        
        // 重新生成活动ID
        const idMapping = {};
        json.activities = json.activities.map(act => {
            const oldId = act.activityDefId;
            const newAct = { ...act };
            newAct.activityDefId = 'act_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
            idMapping[oldId] = newAct.activityDefId;
            return newAct;
        });
        
        // 更新路由中的活动ID
        json.routes = json.routes.map(route => ({
            ...route,
            routeDefId: this._generateRouteId(),
            id: this._generateRouteId(),
            from: idMapping[route.from] || route.from,
            fromActivityDefId: idMapping[route.fromActivityDefId] || route.fromActivityDefId,
            to: idMapping[route.to] || route.to,
            toActivityDefId: idMapping[route.toActivityDefId] || route.toActivityDefId
        }));
        
        // 更新开始/结束节点
        if (json.startNode) {
            json.startNode.firstActivityId = idMapping[json.startNode.firstActivityId] 
                || json.startNode.firstActivityId;
        }
        json.endNodes = json.endNodes.map(node => ({
            ...node,
            lastActivityId: idMapping[node.lastActivityId] || node.lastActivityId
        }));
        
        return new ProcessDef(json);
    }

    /**
     * 获取统计信息
     */
    getStatistics() {
        return {
            activityCount: this.activities.length,
            routeCount: this.routes.length,
            listenerCount: this.listeners.length,
            rightGroupCount: this.rightGroups.length,
            startNodeCount: this.startNode ? 1 : 0,
            endNodeCount: this.endNodes.length
        };
    }

    /**
     * 检查对象是否为空
     */
    _isEmptyObject(obj) {
        return !obj || Object.keys(obj).length === 0;
    }
}

// 保持向后兼容
window.ProcessDef = ProcessDef;
