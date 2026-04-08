/**
 * 路由面板配置
 */
const RoutePanelSchema = {
    name: '路由属性',
    icon: 'route',
    fields: [
        { type: 'section', title: '基本信息' },
        { name: 'routeDefId', type: 'text', label: '路由ID', readonly: true },
        { name: 'name', type: 'text', label: '路由名称', required: true },
        
        { type: 'section', title: '连接信息' },
        { name: 'from', type: 'text', label: '源活动ID', readonly: true },
        { name: 'fromName', type: 'text', label: '源活动名称', readonly: true },
        { name: 'to', type: 'text', label: '目标活动ID', readonly: true },
        { name: 'toName', type: 'text', label: '目标活动名称', readonly: true },
        
        { type: 'section', title: '条件配置' },
        { name: 'condition', type: 'textarea', label: '条件表达式', placeholder: '请输入条件表达式，如: ${amount} > 1000' },
        { name: 'conditionType', type: 'select', label: '条件类型', options: [
            { value: 'EXPRESSION', label: '表达式' },
            { value: 'SCRIPT', label: '脚本' },
            { value: 'RULE', label: '规则' }
        ]},
        { name: 'isDefault', type: 'checkbox', label: '默认路由' },
        { name: 'priority', type: 'number', label: '优先级', min: 0, max: 100 }
    ]
};

window.RoutePanelSchema = RoutePanelSchema;
