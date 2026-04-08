/**
 * 流程面板配置
 */
const ProcessPanelSchema = {
    name: '流程属性',
    icon: 'process',
    fields: [
        { type: 'section', title: '基本信息' },
        { name: 'processId', type: 'text', label: '流程ID', required: true, readonly: true },
        { name: 'name', type: 'text', label: '流程名称', required: true },
        { name: 'description', type: 'textarea', label: '描述' },
        { name: 'classification', type: 'select', label: '分类', options: [
            { value: 'NORMAL', label: '普通流程' },
            { value: 'URGENT', label: '紧急流程' },
            { value: 'IMPORTANT', label: '重要流程' }
        ]},
        { name: 'systemCode', type: 'text', label: '系统编码' },
        { name: 'publicationStatus', type: 'select', label: '发布状态', options: [
            { value: 'UNDER_REVISION', label: '修订中' },
            { value: 'RELEASED', label: '已发布' },
            { value: 'UNDER_TEST', label: '测试中' },
            { value: 'FROZEN', label: '已冻结' }
        ]},
        { name: 'version', type: 'text', label: '版本', readonly: true },
        
        { type: 'section', title: '时限配置' },
        { name: 'limit', type: 'number', label: '时限', min: 0 },
        { name: 'durationUnit', type: 'select', label: '时长单位', options: [
            { value: 'Y', label: '年' },
            { value: 'M', label: '月' },
            { value: 'D', label: '天' },
            { value: 'H', label: '小时' },
            { value: 'm', label: '分钟' },
            { value: 's', label: '秒' },
            { value: 'W', label: '周' }
        ]},
        
        { type: 'section', title: '流程控制' },
        { name: 'autostart', type: 'checkbox', label: '自动启动' },
        { name: 'singleton', type: 'checkbox', label: '单例模式' },
        { name: 'validFrom', type: 'text', label: '生效日期' },
        { name: 'validTo', type: 'text', label: '失效日期' },
        
        { type: 'section', title: '表单配置' },
        { name: 'mark', type: 'select', label: '表单标识类型', options: [
            { value: 'ProcessInst', label: '流程实例级', description: '表单在整个流程实例中唯一' },
            { value: 'ActivityInst', label: '活动实例级', description: '表单在每个活动实例中唯一' },
            { value: 'Person', label: '人员级', description: '表单针对每个人员唯一' },
            { value: 'ActivityInstPerson', label: '活动人员级', description: '表单针对每个活动人员唯一' }
        ]},
        { name: 'lock', type: 'select', label: '锁定策略', options: [
            { value: 'NO', label: '不锁定', description: '不进行锁定' },
            { value: 'Msg', label: '消息锁定', description: '消息级别锁定' },
            { value: 'Lock', label: '锁定', description: '标准锁定' },
            { value: 'Person', label: '人员锁定', description: '按人员锁定' },
            { value: 'Last', label: '最后锁定', description: '最后修改者锁定' }
        ]},
        { name: 'autoSave', type: 'checkbox', label: '自动保存' },
        { name: 'noSqlType', type: 'checkbox', label: 'NoSQL类型' },
        
        { type: 'section', title: '访问权限' },
        { name: 'accessLevel', type: 'select', label: '访问级别', options: [
            { value: 'Public', label: '公开流程' },
            { value: 'Private', label: '私有流程' },
            { value: 'Block', label: '块流程' }
        ]},
        { name: 'accessRule', type: 'textarea', label: '访问规则' },
        
        { type: 'section', title: '管理权限' },
        { name: 'manager', type: 'text', label: '管理者' },
        { name: 'managerUnit', type: 'text', label: '管理单位' }
    ]
};

window.ProcessPanelSchema = ProcessPanelSchema;
