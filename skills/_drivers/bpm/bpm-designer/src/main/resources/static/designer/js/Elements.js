class Elements {
    constructor(container, store) {
        this.container = container;
        this.store = store;
        
        // 分组定义 - 所有分组默认展开平铺显示
        this.groups = {
            control: {
                name: '控制',
                items: [
                    {
                        key: 'START',
                        name: '开始',
                        icon: 'start',
                        description: '流程开始节点',
                        defaultProps: {
                            nodeType: 'CONTROL',
                            position: 'START',
                            fillClass: 'd-node-fill-start',
                            shapeClass: 'd-node-type-start'
                        }
                    },
                    {
                        key: 'END',
                        name: '结束',
                        icon: 'end',
                        description: '流程结束节点',
                        defaultProps: {
                            nodeType: 'CONTROL',
                            position: 'END',
                            fillClass: 'd-node-fill-end',
                            shapeClass: 'd-node-type-end'
                        }
                    }
                ]
            },
            agent: {
                name: '执行体',
                items: [
                    {
                        key: 'HUMAN_TASK',
                        name: '人工任务',
                        icon: 'user',
                        description: '真人执行Skill',
                        defaultProps: {
                            nodeType: 'ACTIVITY',
                            position: 'NORMAL',
                            fillClass: 'd-node-fill-task',
                            shapeClass: 'd-node-type-task',
                            classification: { form: 'STANDALONE', category: 'FORM', provider: 'SYSTEM' },
                            performer: { type: 'HUMAN' }
                        }
                    },
                    {
                        key: 'AGENT_LLM',
                        name: 'LLM Agent',
                        icon: 'brain',
                        description: '大语言模型智能体',
                        defaultProps: {
                            nodeType: 'ACTIVITY',
                            position: 'NORMAL',
                            fillClass: 'd-node-fill-llm',
                            shapeClass: 'd-node-type-llm',
                            classification: { form: 'STANDALONE', category: 'LLM', provider: 'SYSTEM' },
                            performer: { type: 'AGENT', agentType: 'LLM' }
                        }
                    },
                    {
                        key: 'AGENT_TASK',
                        name: '任务 Agent',
                        icon: 'robot',
                        description: '任务型智能体',
                        defaultProps: {
                            nodeType: 'ACTIVITY',
                            position: 'NORMAL',
                            fillClass: 'd-node-fill-agent',
                            shapeClass: 'd-node-type-agent',
                            classification: { form: 'STANDALONE', category: 'SERVICE', provider: 'SYSTEM' },
                            performer: { type: 'AGENT', agentType: 'TASK' }
                        }
                    },
                    {
                        key: 'AGENT_EVENT',
                        name: '事件 Agent',
                        icon: 'bell',
                        description: '事件驱动型智能体',
                        defaultProps: {
                            nodeType: 'ACTIVITY',
                            position: 'NORMAL',
                            fillClass: 'd-node-fill-event',
                            shapeClass: 'd-node-type-event',
                            classification: { form: 'STANDALONE', category: 'COMM', provider: 'SYSTEM' },
                            performer: { type: 'AGENT', agentType: 'EVENT' }
                        }
                    },
                    {
                        key: 'AGENT_HYBRID',
                        name: '混合 Agent',
                        icon: 'layers',
                        description: '混合型智能体',
                        defaultProps: {
                            nodeType: 'ACTIVITY',
                            position: 'NORMAL',
                            fillClass: 'd-node-fill-hybrid',
                            shapeClass: 'd-node-type-hybrid',
                            classification: { form: 'STANDALONE', category: 'TOOL', provider: 'SYSTEM' },
                            performer: { type: 'AGENT', agentType: 'HYBRID' }
                        }
                    }
                ]
            },
            nesting: {
                name: '嵌套',
                items: [
                    {
                        key: 'SUBFLOW',
                        name: '子流程',
                        icon: 'subprocess',
                        description: '引用子流程',
                        defaultProps: {
                            nodeType: 'NESTING',
                            position: 'NORMAL',
                            fillClass: 'd-node-fill-subprocess',
                            shapeClass: 'd-node-type-subprocess',
                            classification: { form: 'STANDALONE', category: 'WORKFLOW', provider: 'SYSTEM' },
                            nesting: { type: 'SUBFLOW' },
                            contextIsolation: { level: 'PARTIAL' }
                        }
                    },
                    {
                        key: 'SCENE',
                        name: '场景',
                        icon: 'grid',
                        description: '独立上下文的业务场景',
                        defaultProps: {
                            nodeType: 'NESTING',
                            position: 'NORMAL',
                            fillClass: 'd-node-fill-scene',
                            shapeClass: 'd-node-type-scene',
                            classification: { form: 'SCENE', category: 'WORKFLOW', provider: 'SYSTEM' },
                            nesting: { type: 'SCENE' },
                            contextIsolation: { level: 'ISOLATED' }
                        }
                    },
                    {
                        key: 'EXTERNAL',
                        name: '外部流程',
                        icon: 'external',
                        description: '对接外部流程',
                        defaultProps: {
                            nodeType: 'NESTING',
                            position: 'NORMAL',
                            fillClass: 'd-node-fill-external',
                            shapeClass: 'd-node-type-external',
                            classification: { form: 'SCENE', category: 'WORKFLOW', provider: 'DRIVER' },
                            nesting: { type: 'EXTERNAL' },
                            contextIsolation: { level: 'ISOLATED' }
                        }
                    }
                ]
            }
        };
        
        // 构建扁平化的nodeList用于查找
        this.nodeList = this._buildNodeList();
        
        this.propertyTree = this._buildPropertyTree();
        
        this.render();
        this._bindEvents();
    }
    
    _buildNodeList() {
        const list = [];
        for (const groupKey of Object.keys(this.groups)) {
            const group = this.groups[groupKey];
            for (const item of group.items) {
                list.push({ ...item, group: groupKey });
            }
        }
        return list;
    }
    
    _buildPropertyTree() {
        return {
            basic: {
                name: '基础属性',
                level: 'basic',
                properties: [
                    { key: 'nodeId', name: '节点ID', type: 'text', readonly: true },
                    { key: 'name', name: '名称', type: 'text', required: true },
                    { key: 'description', name: '描述', type: 'textarea' },
                    { key: 'nodeType', name: '节点类型', type: 'select', readonly: true, 
                      options: ['CONTROL', 'ACTIVITY', 'NESTING'] }
                ]
            },
            classification: {
                name: '三维度分类',
                level: 'basic',
                showWhen: { 'nodeType': ['ACTIVITY', 'NESTING'] },
                properties: [
                    { key: 'form', name: '形态', type: 'select',
                      options: SkillClassification.FORM_OPTIONS.map(o => ({ value: o.value, label: o.label })) },
                    { key: 'category', name: '功能', type: 'select',
                      options: SkillClassification.CATEGORY_OPTIONS.map(o => ({ value: o.value, label: o.label })) },
                    { key: 'provider', name: '提供者', type: 'select',
                      options: SkillClassification.PROVIDER_OPTIONS.map(o => ({ value: o.value, label: o.label })) }
                ]
            },
            performer: {
                name: '执行者',
                level: 'core',
                showWhen: { 'nodeType': ['ACTIVITY'] },
                properties: [
                    { key: 'type', name: '执行者类型', type: 'select',
                      options: SkillClassification.PERFORMER_OPTIONS.map(o => ({ value: o.value, label: o.label })) }
                ]
            },
            contextIsolation: {
                name: '上下文隔离',
                level: 'core',
                showWhen: { 'nodeType': ['NESTING'] },
                properties: [
                    { key: 'level', name: '隔离级别', type: 'select',
                      options: SkillClassification.ISOLATION_OPTIONS.map(o => ({ value: o.value, label: o.label })) },
                    { key: 'inheritVariables', name: '继承变量', type: 'boolean' },
                    { key: 'inheritFormData', name: '继承表单数据', type: 'boolean' }
                ]
            },
            execution: {
                name: '执行控制',
                level: 'advanced',
                showWhen: { 'nodeType': ['ACTIVITY'] },
                properties: [
                    { key: 'timeout', name: '超时(ms)', type: 'number' },
                    { key: 'async', name: '异步执行', type: 'boolean' },
                    { key: 'onError', name: '错误处理', type: 'select',
                      options: ['CONTINUE', 'STOP', 'RETRY', 'FALLBACK'] }
                ]
            },
            history: {
                name: '历史定义',
                level: 'expert',
                properties: [
                    { key: 'enableAudit', name: '启用审计', type: 'boolean' },
                    { key: 'retentionDays', name: '保留天数', type: 'number' }
                ]
            }
        };
    }
    
    render() {
        this.container.innerHTML = this._renderGroups();
    }
    
    _renderGroups() {
        let html = '<div class="d-element-groups">';
        
        for (const [key, group] of Object.entries(this.groups)) {
            html += `
                <div class="d-element-group" data-group="${key}">
                    <div class="d-element-group-header">
                        <span class="d-element-group-name">${group.name}</span>
                    </div>
                    <div class="d-element-group-content">
            `;
            
            for (const node of group.items) {
                const props = node.defaultProps || {};
                const description = node.description || '';
                html += `
                    <div class="d-element-item ${props.fillClass || ''} ${props.shapeClass || ''}"
                         data-node-type="${node.key}"
                         data-category="${props.nodeType || ''}"
                         data-position="${props.position || 'NORMAL'}"
                         data-fill-class="${props.fillClass || ''}"
                         draggable="true"
                         title="${description}">
                        <div class="d-element-icon">
                            ${IconManager.get(node.icon || 'activity')}
                        </div>
                        <div class="d-element-info">
                            <span class="d-element-name">${node.name}</span>
                        </div>
                    </div>
                `;
            }
            
            html += '</div></div>';
        }
        
        html += '</div>';
        return html;
    }
    
    _bindEvents() {
        // 拖拽事件
        const items = this.container.querySelectorAll('.d-element-item[draggable="true"]');
        items.forEach(item => {
            item.addEventListener('dragstart', (e) => {
                const nodeType = item.dataset.nodeType;
                const node = this.nodeList.find(n => n.key === nodeType);
                
                if (node) {
                    e.dataTransfer.setData('application/json', JSON.stringify({
                        type: nodeType,
                        name: node.name,
                        activityType: node.key,
                        ...node.defaultProps
                    }));
                    e.dataTransfer.effectAllowed = 'copy';
                    item.classList.add('dragging');
                }
            });
            
            item.addEventListener('dragend', () => {
                item.classList.remove('dragging');
            });
        });
    }
    
    getNodeDefinition(nodeType) {
        return this.nodeList.find(n => n.key === nodeType);
    }
    
    getPropertyTree() {
        return this.propertyTree;
    }
    
    getPropertyDefinition(category, key) {
        const cat = this.propertyTree[category];
        if (!cat) return null;
        return cat.properties.find(p => p.key === key);
    }
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = Elements;
} else {
    window.Elements = Elements;
}
