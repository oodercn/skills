/**
 * PluginDataAdapter - 插件数据适配器
 * 
 * 实现UI数据与后端存储格式的双向转换
 * 参考原有Swing设计中的数据存储规则：
 * - 多值分隔符: ":" (FormulaParameter.DELIMITER_MULTIPLE)
 * - XML格式存储: <itjds:Listeners>, <itjds:RightGroups>
 * - 键值对格式: name=value
 * 
 * @author AI Assistant
 * @version 1.0
 */

class PluginDataAdapter {
    constructor(options = {}) {
        this.options = {
            // 多值分隔符
            delimiter: ':',
            
            // 键值对分隔符
            keyValueDelimiter: '=',
            
            // XML命名空间
            xmlNamespace: 'itjds',
            
            // XML命名空间URI
            xmlNamespaceUri: 'http://www.itjds.com/bpm',
            
            ...options
        };
    }

    // ==================== 序列化方法 ====================

    /**
     * 序列化多值数据
     * 对应Swing: FormulaParameter.DELIMITER_MULTIPLE
     * 
     * @param {Array} values - 值数组 [{id, name}, ...]
     * @returns {string} - 分隔符连接的字符串 "id1:id2:id3"
     */
    serializeMultiValue(values) {
        if (!Array.isArray(values) || values.length === 0) {
            return '';
        }
        
        return values
            .map(v => typeof v === 'string' ? v : (v.id || v.code || v))
            .filter(id => id && id.toString().trim())
            .join(this.options.delimiter);
    }

    /**
     * 反序列化多值数据
     * 
     * @param {string} data - 分隔符连接的字符串
     * @returns {Array} - 值数组
     */
    deserializeMultiValue(data) {
        if (!data || typeof data !== 'string') {
            return [];
        }
        
        return data
            .split(this.options.delimiter)
            .map(id => id.trim())
            .filter(id => id);
    }

    /**
     * 序列化键值对数据
     * 
     * @param {Object} obj - 键值对对象
     * @returns {string} - "key1=value1:key2=value2"
     */
    serializeKeyValue(obj) {
        if (!obj || typeof obj !== 'object') {
            return '';
        }
        
        return Object.entries(obj)
            .map(([key, value]) => `${key}${this.options.keyValueDelimiter}${value}`)
            .join(this.options.delimiter);
    }

    /**
     * 反序列化键值对数据
     * 
     * @param {string} data - 键值对字符串
     * @returns {Object} - 键值对对象
     */
    deserializeKeyValue(data) {
        if (!data || typeof data !== 'string') {
            return {};
        }
        
        const result = {};
        data.split(this.options.delimiter).forEach(pair => {
            const [key, value] = pair.split(this.options.keyValueDelimiter);
            if (key && key.trim()) {
                result[key.trim()] = value ? value.trim() : '';
            }
        });
        return result;
    }

    // ==================== XML序列化方法 ====================

    /**
     * 序列化监听器集合为XML
     * 对应Swing: Listeners.toXPDL()
     * 
     * @param {Array} listeners - 监听器数组
     * @returns {string} - XML字符串
     */
    serializeListenersToXML(listeners) {
        if (!Array.isArray(listeners) || listeners.length === 0) {
            return '';
        }
        
        const ns = this.options.xmlNamespace;
        const items = listeners.map(listener => {
            const attrs = [
                `Id="${this._escapeXml(listener.id || '')}"`,
                `Name="${this._escapeXml(listener.name || '')}"`,
                `ListenerEvent="${this._escapeXml(listener.event || listener.listenerEvent || '')}"`,
                `RealizeClass="${this._escapeXml(listener.realizeClass || '')}"`,
                `ExpressionEventType="${this._escapeXml(listener.expressionEventType || '')}"`,
                `ExpressionListenerType="${this._escapeXml(listener.expressionListenerType || 'Listener')}"`,
                `expressionStr="${this._escapeXml(listener.expression || listener.expressionStr || '')}"`
            ].join(' ');
            
            return `    <${ns}:Listener ${attrs}/>`;
        }).join('\n');
        
        return `<${ns}:Listeners xmlns:${ns}="${this.options.xmlNamespaceUri}">\n${items}\n</${ns}:Listeners>`;
    }

    /**
     * 反序列化XML为监听器集合
     * 
     * @param {string} xml - XML字符串
     * @returns {Array} - 监听器数组
     */
    deserializeListenersFromXML(xml) {
        if (!xml || typeof xml !== 'string') {
            return [];
        }
        
        const listeners = [];
        const regex = /<\w*:Listener\s+([^>]+)\/>/g;
        let match;
        
        while ((match = regex.exec(xml)) !== null) {
            const attrs = this._parseXmlAttributes(match[1]);
            listeners.push({
                id: attrs.Id || attrs.id || '',
                name: attrs.Name || attrs.name || '',
                event: attrs.ListenerEvent || attrs.listenerEvent || '',
                listenerEvent: attrs.ListenerEvent || attrs.listenerEvent || '',
                realizeClass: attrs.RealizeClass || attrs.realizeClass || '',
                expressionEventType: attrs.ExpressionEventType || attrs.expressionEventType || '',
                expressionListenerType: attrs.ExpressionListenerType || attrs.expressionListenerType || 'Listener',
                expression: attrs.expressionStr || attrs.expression || '',
                expressionStr: attrs.expressionStr || attrs.expression || ''
            });
        }
        
        return listeners;
    }

    /**
     * 序列化权限组集合为XML
     * 
     * @param {Array} rightGroups - 权限组数组
     * @returns {string} - XML字符串
     */
    serializeRightGroupsToXML(rightGroups) {
        if (!Array.isArray(rightGroups) || rightGroups.length === 0) {
            return '';
        }
        
        const ns = this.options.xmlNamespace;
        const groups = rightGroups.map(group => {
            const attrs = [
                `Id="${this._escapeXml(group.id || group.groupId || '')}"`,
                `Name="${this._escapeXml(group.name || group.groupName || '')}"`
            ].join(' ');
            
            let membersXml = '';
            if (Array.isArray(group.members) && group.members.length > 0) {
                membersXml = '\n' + group.members.map(member => {
                    const memberAttrs = [
                        `Type="${this._escapeXml(member.type || 'Person')}"`,
                        `Id="${this._escapeXml(member.id || '')}"`
                    ].join(' ');
                    return `        <${ns}:Member ${memberAttrs}/>`;
                }).join('\n') + '\n    ';
            }
            
            return `    <${ns}:RightGroup ${attrs}>${membersXml}</${ns}:RightGroup>`;
        }).join('\n');
        
        return `<${ns}:RightGroups xmlns:${ns}="${this.options.xmlNamespaceUri}">\n${groups}\n</${ns}:RightGroups>`;
    }

    /**
     * 反序列化XML为权限组集合
     * 
     * @param {string} xml - XML字符串
     * @returns {Array} - 权限组数组
     */
    deserializeRightGroupsFromXML(xml) {
        if (!xml || typeof xml !== 'string') {
            return [];
        }
        
        const groups = [];
        const groupRegex = /<\w*:RightGroup\s+([^>]+)>([\s\S]*?)<\/\w*:RightGroup>/g;
        let groupMatch;
        
        while ((groupMatch = groupRegex.exec(xml)) !== null) {
            const attrs = this._parseXmlAttributes(groupMatch[1]);
            const content = groupMatch[2];
            
            const members = [];
            const memberRegex = /<\w*:Member\s+([^>]+)\/>/g;
            let memberMatch;
            
            while ((memberMatch = memberRegex.exec(content)) !== null) {
                const memberAttrs = this._parseXmlAttributes(memberMatch[1]);
                members.push({
                    type: memberAttrs.Type || memberAttrs.type || 'Person',
                    id: memberAttrs.Id || memberAttrs.id || ''
                });
            }
            
            groups.push({
                id: attrs.Id || attrs.id || '',
                groupId: attrs.Id || attrs.id || '',
                name: attrs.Name || attrs.name || '',
                groupName: attrs.Name || attrs.name || '',
                members: members
            });
        }
        
        return groups;
    }

    // ==================== 流程节点数据转换 ====================

    /**
     * 序列化开始节点数据
     * 对应XPDL: StartOfWorkflow = "ParticipantID;FirstActivityID;X;Y;Routing"
     * 
     * @param {Object} startNode - 开始节点对象
     * @returns {string} - XPDL格式字符串
     */
    serializeStartNode(startNode) {
        if (!startNode) return '';
        
        const parts = [
            startNode.participantId || '',
            startNode.firstActivityId || '',
            startNode.x || 0,
            startNode.y || 0,
            startNode.routing || ''
        ];
        
        return parts.join(';');
    }

    /**
     * 反序列化开始节点数据
     * 
     * @param {string} data - XPDL格式字符串
     * @returns {Object} - 开始节点对象
     */
    deserializeStartNode(data) {
        if (!data || typeof data !== 'string') {
            return null;
        }
        
        const parts = data.split(';');
        return {
            participantId: parts[0] || '',
            firstActivityId: parts[1] || '',
            x: parseInt(parts[2]) || 0,
            y: parseInt(parts[3]) || 0,
            routing: parts[4] || ''
        };
    }

    /**
     * 序列化结束节点数据
     * 对应XPDL: EndOfWorkflow = "ParticipantID;ActivityID;X;Y|ParticipantID;ActivityID;X;Y"
     * 
     * @param {Array} endNodes - 结束节点数组
     * @returns {string} - XPDL格式字符串
     */
    serializeEndNodes(endNodes) {
        if (!Array.isArray(endNodes) || endNodes.length === 0) {
            return '';
        }
        
        return endNodes.map(node => {
            const parts = [
                node.participantId || '',
                node.activityId || '',
                node.x || 0,
                node.y || 0
            ];
            return parts.join(';');
        }).join('|');
    }

    /**
     * 反序列化结束节点数据
     * 
     * @param {string} data - XPDL格式字符串
     * @returns {Array} - 结束节点数组
     */
    deserializeEndNodes(data) {
        if (!data || typeof data !== 'string') {
            return [];
        }
        
        return data.split('|').map(nodeStr => {
            const parts = nodeStr.split(';');
            return {
                participantId: parts[0] || '',
                activityId: parts[1] || '',
                x: parseInt(parts[2]) || 0,
                y: parseInt(parts[3]) || 0
            };
        });
    }

    // ==================== JSON数据转换 ====================

    /**
     * 将插件数据转换为后端JSON格式
     * 
     * @param {string} pluginType - 插件类型
     * @param {any} data - 插件数据
     * @returns {Object} - 后端JSON格式
     */
    toBackendJSON(pluginType, data) {
        switch (pluginType) {
            case 'listeners':
                return {
                    attributename: 'Listeners',
                    attributevalue: this.serializeListenersToXML(data),
                    attributetype: 'WORKFLOW'
                };
                
            case 'rightGroups':
                return {
                    attributename: 'RightGroups',
                    attributevalue: this.serializeRightGroupsToXML(data),
                    attributetype: 'RIGHT'
                };
                
            case 'performers':
                return {
                    attributename: 'Performers',
                    attributevalue: this.serializeMultiValue(data),
                    attributetype: 'RIGHT'
                };
                
            case 'departments':
                return {
                    attributename: 'Departments',
                    attributevalue: this.serializeMultiValue(data),
                    attributetype: 'RIGHT'
                };
                
            case 'startNode':
                return {
                    attributename: 'StartOfWorkflow',
                    attributevalue: this.serializeStartNode(data),
                    attributetype: 'WORKFLOW'
                };
                
            case 'endNodes':
                return {
                    attributename: 'EndOfWorkflow',
                    attributevalue: this.serializeEndNodes(data),
                    attributetype: 'WORKFLOW'
                };
                
            case 'expression':
                return {
                    attributename: 'Expression',
                    attributevalue: data,
                    attributetype: 'RIGHT'
                };
                
            default:
                return {
                    attributename: pluginType,
                    attributevalue: typeof data === 'object' ? JSON.stringify(data) : data,
                    attributetype: 'BPD'
                };
        }
    }

    /**
     * 从后端JSON格式转换为插件数据
     * 
     * @param {Object} backendData - 后端JSON数据
     * @returns {Object} - 插件数据
     */
    fromBackendJSON(backendData) {
        const { attributename, attributevalue } = backendData;
        
        switch (attributename) {
            case 'Listeners':
                return {
                    type: 'listeners',
                    data: this.deserializeListenersFromXML(attributevalue)
                };
                
            case 'RightGroups':
                return {
                    type: 'rightGroups',
                    data: this.deserializeRightGroupsFromXML(attributevalue)
                };
                
            case 'Performers':
                return {
                    type: 'performers',
                    data: this.deserializeMultiValue(attributevalue)
                };
                
            case 'Departments':
                return {
                    type: 'departments',
                    data: this.deserializeMultiValue(attributevalue)
                };
                
            case 'StartOfWorkflow':
                return {
                    type: 'startNode',
                    data: this.deserializeStartNode(attributevalue)
                };
                
            case 'EndOfWorkflow':
                return {
                    type: 'endNodes',
                    data: this.deserializeEndNodes(attributevalue)
                };
                
            case 'Expression':
                return {
                    type: 'expression',
                    data: attributevalue
                };
                
            default:
                try {
                    return {
                        type: attributename,
                        data: JSON.parse(attributevalue)
                    };
                } catch {
                    return {
                        type: attributename,
                        data: attributevalue
                    };
                }
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * XML转义
     */
    _escapeXml(str) {
        if (!str) return '';
        return str
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&apos;');
    }

    /**
     * 解析XML属性
     */
    _parseXmlAttributes(attrString) {
        const attrs = {};
        const regex = /(\w+)="([^"]*)"/g;
        let match;
        
        while ((match = regex.exec(attrString)) !== null) {
            attrs[match[1]] = match[2];
        }
        
        return attrs;
    }

    // ==================== 静态实例 ====================

    static getInstance(options) {
        if (!PluginDataAdapter._instance) {
            PluginDataAdapter._instance = new PluginDataAdapter(options);
        }
        return PluginDataAdapter._instance;
    }
}

// 创建全局实例
window.PluginDataAdapter = PluginDataAdapter;
window.pluginDataAdapter = PluginDataAdapter.getInstance();
