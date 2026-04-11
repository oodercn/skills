/**
 * PanelInitializer - 面板插件初始化器
 * 
 * 负责注册所有面板插件到PanelPluginManager
 * 整合PluginEnvironment，提供完整的插件生命周期管理
 * 
 * 包括：
 * - 活动(Activity)面板插件
 * - 流程(Process)面板插件
 * - 路由(Route)面板插件
 * - 外部字典面板插件
 * 
 * @author AI Assistant
 * @version 2.0
 */

class PanelInitializer {
    constructor() {
        this.manager = window.panelPluginManager;
        this.environment = window.pluginEnvironment;
        this.initialized = false;
    }

    /**
     * 初始化所有面板插件
     */
    async initialize() {
        if (this.initialized) {
            console.warn('面板插件已初始化，跳过');
            return this;
        }

        console.log('开始初始化面板插件...');

        // 确保插件环境已初始化
        if (this.environment && !this.environment.initialized) {
            await this.environment.initialize();
        }

        // 注册活动面板插件
        this._registerActivityPanels();

        // 注册流程面板插件
        this._registerProcessPanels();

        // 注册外部字典面板插件
        this._registerExternalDictionaryPanels();

        // 更新配置
        this._updateConfig();

        this.initialized = true;
        console.log('面板插件初始化完成');

        return this;
    }

    /**
     * 注册活动面板插件
     */
    _registerActivityPanels() {
        if (typeof ActivityPanelPlugins === 'undefined') {
            console.warn('ActivityPanelPlugins 未加载');
            return;
        }

        const {
            BasicPanelPlugin,
            TimingPanelPlugin,
            FlowControlPanelPlugin,
            RightPanelPlugin,
            FormPanelPlugin,
            ServicePanelPlugin
        } = ActivityPanelPlugins;

        // 使用环境注册插件，自动注入数据源和适配器
        const register = (id, plugin, priority) => {
            if (this.environment) {
                this.environment.registerPlugin(id, plugin, priority);
            } else if (this.manager) {
                this.manager.register(id, plugin, priority);
            }
        };

        // 基本信息面板
        register('activity-basic', {
            ...BasicPanelPlugin,
            id: 'activity-basic',
            dataConfig: {
                dataKey: 'basic',
                storageFormat: 'json'
            },
            applicable: (activity) => activity && activity.activityDefId
        }, 10);

        // 时限配置面板
        register('activity-timing', {
            ...TimingPanelPlugin,
            id: 'activity-timing',
            dataConfig: {
                dataKey: 'timing',
                storageFormat: 'json'
            },
            applicable: (activity) => activity && activity.activityDefId
        }, 20);

        // 流程控制面板
        register('activity-flow', {
            ...FlowControlPanelPlugin,
            id: 'activity-flow',
            dataConfig: {
                dataKey: 'flow',
                storageFormat: 'json'
            },
            applicable: (activity) => activity && activity.activityDefId
        }, 30);

        // 权限配置面板 - 需要远程数据
        register('activity-right', {
            ...RightPanelPlugin,
            id: 'activity-right',
            dataConfig: {
                dataKey: 'right',
                storageFormat: 'multivalue',
                needRemoteData: true,
                remoteType: 'person',
                adapterType: 'performers'
            },
            applicable: (activity) => activity && activity.activityDefId
        }, 40);

        // 表单配置面板 - 需要远程数据
        register('activity-form', {
            ...FormPanelPlugin,
            id: 'activity-form',
            dataConfig: {
                dataKey: 'form',
                storageFormat: 'json',
                needRemoteData: true,
                remoteType: 'form'
            },
            applicable: (activity) => activity && activity.activityDefId
        }, 50);

        // 服务配置面板 - 需要远程数据
        register('activity-service', {
            ...ServicePanelPlugin,
            id: 'activity-service',
            dataConfig: {
                dataKey: 'service',
                storageFormat: 'json',
                needRemoteData: true,
                remoteType: 'service'
            },
            applicable: (activity) => activity && activity.activityType === 'SERVICE'
        }, 60);

        // 高级属性面板
        if (typeof AdvancedPanelPlugin !== 'undefined') {
            const advancedPlugin = new AdvancedPanelPlugin();
            register('activity-advanced', {
                id: 'activity-advanced',
                name: '高级属性',
                render: (container, activity) => {
                    advancedPlugin.setContainer(container);
                    advancedPlugin.render(activity);
                },
                getData: () => advancedPlugin.getData(),
                dataConfig: {
                    dataKey: 'advanced',
                    storageFormat: 'json'
                },
                applicable: (activity) => activity && activity.activityDefId
            }, 70);
        }

        console.log('活动面板插件注册完成');
    }

    /**
     * 注册流程面板插件
     */
    _registerProcessPanels() {
        if (typeof ProcessPanelPlugins === 'undefined') {
            console.warn('ProcessPanelPlugins 未加载');
            return;
        }

        const {
            ProcessBasicPanelPlugin,
            StartNodePanelPlugin,
            EndNodesPanelPlugin,
            ListenersPanelPlugin,
            RightGroupsPanelPlugin,
            ProcessVariablesPanelPlugin,
            ProcessTimingPanelPlugin
        } = ProcessPanelPlugins;

        const register = (id, plugin, priority) => {
            if (this.environment) {
                this.environment.registerPlugin(id, plugin, priority);
            } else if (this.manager) {
                this.manager.register(id, plugin, priority);
            }
        };

        // 流程基本信息面板
        register('process-basic', {
            ...ProcessBasicPanelPlugin,
            id: 'process-basic',
            dataConfig: {
                dataKey: 'basic',
                storageFormat: 'json'
            },
            applicable: (process) => process && process.processDefId
        }, 10);

        // 开始节点配置面板
        register('process-startNode', {
            ...StartNodePanelPlugin,
            id: 'process-startNode',
            dataConfig: {
                dataKey: 'startNode',
                storageFormat: 'multivalue',
                adapterType: 'startNode'
            },
            applicable: (process) => process && process.processDefId
        }, 20);

        // 结束节点配置面板
        register('process-endNodes', {
            ...EndNodesPanelPlugin,
            id: 'process-endNodes',
            dataConfig: {
                dataKey: 'endNodes',
                storageFormat: 'multivalue',
                adapterType: 'endNodes'
            },
            applicable: (process) => process && process.processDefId
        }, 30);

        // 监听器配置面板 - 使用XML存储
        register('process-listeners', {
            ...ListenersPanelPlugin,
            id: 'process-listeners',
            dataConfig: {
                dataKey: 'listeners',
                storageFormat: 'xml',
                adapterType: 'listeners'
            },
            applicable: (process) => process && process.processDefId
        }, 40);

        // 流程权限组面板 - 使用XML存储
        register('process-rightGroups', {
            ...RightGroupsPanelPlugin,
            id: 'process-rightGroups',
            dataConfig: {
                dataKey: 'rightGroups',
                storageFormat: 'xml',
                adapterType: 'rightGroups'
            },
            applicable: (process) => process && process.processDefId
        }, 50);

        // 流程变量面板
        register('process-variables', {
            ...ProcessVariablesPanelPlugin,
            id: 'process-variables',
            dataConfig: {
                dataKey: 'variables',
                storageFormat: 'json'
            },
            applicable: (process) => process && process.processDefId
        }, 60);

        // 流程定时配置面板
        register('process-timing', {
            ...ProcessTimingPanelPlugin,
            id: 'process-timing',
            dataConfig: {
                dataKey: 'timing',
                storageFormat: 'json'
            },
            applicable: (process) => process && process.processDefId
        }, 70);

        console.log('流程面板插件注册完成');
    }

    /**
     * 注册外部字典面板插件
     */
    _registerExternalDictionaryPanels() {
        if (typeof ExternalDictionaryPlugins === 'undefined') {
            console.warn('ExternalDictionaryPlugins 未加载');
            return;
        }

        const {
            ExpressionPanelPlugin,
            PerformerSelectionPanelPlugin,
            DepartmentSelectionPanelPlugin
        } = ExternalDictionaryPlugins;

        const register = (id, plugin, priority) => {
            if (this.environment) {
                this.environment.registerPlugin(id, plugin, priority);
            } else if (this.manager) {
                this.manager.register(id, plugin, priority);
            }
        };

        // 表达式编辑器 - 需要远程数据
        register('dialog-expression', {
            ...ExpressionPanelPlugin,
            id: 'dialog-expression',
            type: 'dialog',
            dataConfig: {
                needRemoteData: true,
                remoteType: 'expressionVariables'
            }
        }, 10);

        // 办理人选择器 - 需要远程数据
        register('dialog-performer', {
            ...PerformerSelectionPanelPlugin,
            id: 'dialog-performer',
            type: 'dialog',
            dataConfig: {
                needRemoteData: true,
                remoteType: 'person'
            }
        }, 20);

        // 部门选择器 - 需要远程数据
        register('dialog-department', {
            ...DepartmentSelectionPanelPlugin,
            id: 'dialog-department',
            type: 'dialog',
            dataConfig: {
                needRemoteData: true,
                remoteType: 'org'
            }
        }, 30);

        console.log('外部字典面板插件注册完成');
    }

    /**
     * 更新面板管理器配置
     */
    _updateConfig() {
        const config = {
            // 更新面板分组配置
            groups: {
                'process': [
                    'process-basic',
                    'process-startNode', 
                    'process-endNodes',
                    'process-listeners',
                    'process-rightGroups',
                    'process-variables',
                    'process-timing'
                ],
                'activity': [
                    'activity-basic',
                    'activity-timing',
                    'activity-flow',
                    'activity-right',
                    'activity-form',
                    'activity-service',
                    'activity-advanced'
                ],
                'route': [
                    'route-basic',
                    'route-condition'
                ]
            },
            
            // 面板顺序配置
            defaultOrder: [
                'process-basic',
                'process-startNode',
                'process-endNodes',
                'activity-basic',
                'activity-timing',
                'activity-flow',
                'activity-right',
                'activity-form',
                'activity-service',
                'activity-advanced'
            ]
        };

        if (this.manager) {
            this.manager.loadConfig(config);
        }

        console.log('面板配置更新完成');
    }

    /**
     * 获取面板管理器
     */
    getManager() {
        return this.manager;
    }

    /**
     * 获取插件环境
     */
    getEnvironment() {
        return this.environment;
    }

    /**
     * 检查是否已初始化
     */
    isInitialized() {
        return this.initialized;
    }

    /**
     * 渲染流程面板
     * @param {HTMLElement} container - 容器
     * @param {Object} process - 流程对象
     */
    renderProcessPanels(container, process) {
        if (this.environment) {
            return this.environment.renderForModel(container, process, 'process');
        } else if (this.manager) {
            return this.manager.renderForModel(container, process, 'process');
        }
    }

    /**
     * 渲染活动面板
     * @param {HTMLElement} container - 容器
     * @param {Object} activity - 活动对象
     */
    renderActivityPanels(container, activity) {
        if (this.environment) {
            return this.environment.renderForModel(container, activity, 'activity');
        } else if (this.manager) {
            return this.manager.renderForModel(container, activity, 'activity');
        }
    }
}

// 创建全局实例
window.PanelInitializer = PanelInitializer;
window.panelInitializer = new PanelInitializer();

// 页面加载完成后自动初始化
document.addEventListener('DOMContentLoaded', async () => {
    if (window.panelInitializer) {
        await window.panelInitializer.initialize();
    }
});
