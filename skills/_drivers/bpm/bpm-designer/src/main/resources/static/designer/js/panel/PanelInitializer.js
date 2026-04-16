/**
 * PanelInitializer - 面板插件初始化器
 * 
 * 负责注册所有面板插件到PanelPluginManager
 * 整合PluginEnvironment，提供完整的插件生命周期管理
 * 
 * 包括：
 * - 活动(Activity)面板插件 - 按活动分类(HUMAN/AGENT/SCENE)条件展示
 * - 流程(Process)面板插件
 * - 路由(Route)面板插件
 * - 外部字典面板插件
 * 
 * @author AI Assistant
 * @version 3.0
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

        if (this.environment && !this.environment.initialized) {
            await this.environment.initialize();
        }

        this._registerActivityPanels();
        this._registerAgentPanels();
        this._registerScenePanels();
        this._registerProcessPanels();
        this._registerExternalDictionaryPanels();
        this._updateConfig();

        this.initialized = true;
        console.log('面板插件初始化完成');

        return this;
    }

    /**
     * 注册活动面板插件 - 人工活动(HUMAN)
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

        const register = (id, plugin, priority) => {
            if (this.environment) {
                this.environment.registerPlugin(id, plugin, priority);
            } else if (this.manager) {
                this.manager.register(id, plugin, priority);
            }
        };

        register('activity-basic', {
            ...BasicPanelPlugin,
            id: 'activity-basic',
            dataConfig: {
                dataKey: 'basic',
                storageFormat: 'json'
            },
            applicable: (activity) => activity && activity.activityDefId
        }, 10);

        register('activity-timing', {
            ...TimingPanelPlugin,
            id: 'activity-timing',
            dataConfig: {
                dataKey: 'timing',
                storageFormat: 'json'
            },
            applicable: (activity) => activity && activity.activityDefId && 
                ['HUMAN', 'AGENT'].includes(activity.activityCategory)
        }, 20);

        register('activity-flow', {
            ...FlowControlPanelPlugin,
            id: 'activity-flow',
            dataConfig: {
                dataKey: 'flow',
                storageFormat: 'json'
            },
            applicable: (activity) => activity && activity.activityDefId && 
                activity.activityCategory === 'HUMAN'
        }, 30);

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
            applicable: (activity) => activity && activity.activityDefId && 
                activity.activityCategory === 'HUMAN'
        }, 40);

        register('activity-form', {
            ...FormPanelPlugin,
            id: 'activity-form',
            dataConfig: {
                dataKey: 'form',
                storageFormat: 'json',
                needRemoteData: true,
                remoteType: 'form'
            },
            applicable: (activity) => activity && activity.activityDefId && 
                activity.activityCategory === 'HUMAN'
        }, 50);

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

        console.log('人工活动面板插件注册完成');
    }

    /**
     * 注册Agent活动面板插件
     */
    _registerAgentPanels() {
        if (typeof AgentPanelPlugins === 'undefined') {
            console.warn('AgentPanelPlugins 未加载');
            return;
        }

        const {
            AgentBasicPanelPlugin,
            LLMPanelPlugin,
            AgentToolsPanelPlugin
        } = AgentPanelPlugins;

        const register = (id, plugin, priority) => {
            if (this.environment) {
                this.environment.registerPlugin(id, plugin, priority);
            } else if (this.manager) {
                this.manager.register(id, plugin, priority);
            }
        };

        register('agent-basic', {
            ...AgentBasicPanelPlugin,
            id: 'agent-basic',
            dataConfig: {
                dataKey: 'agentConfig',
                storageFormat: 'json',
                needRemoteData: true,
                remoteType: 'agentList'
            },
            applicable: (activity) => activity && activity.activityCategory === 'AGENT'
        }, 20);

        register('agent-llm', {
            ...LLMPanelPlugin,
            id: 'agent-llm',
            dataConfig: {
                dataKey: 'llmConfig',
                storageFormat: 'json',
                needRemoteData: true,
                remoteType: 'llmProviders'
            },
            applicable: (activity) => activity && activity.activityCategory === 'AGENT'
        }, 30);

        register('agent-tools', {
            ...AgentToolsPanelPlugin,
            id: 'agent-tools',
            dataConfig: {
                dataKey: 'toolsConfig',
                storageFormat: 'json',
                needRemoteData: true,
                remoteType: 'agentCapabilities'
            },
            applicable: (activity) => activity && activity.activityCategory === 'AGENT'
        }, 40);

        if (typeof AgentPanelPlugins._loadRemoteData === 'function') {
            AgentPanelPlugins._loadRemoteData().then(() => {
                console.log('Agent面板远程数据加载完成');
            }).catch(err => {
                console.warn('Agent面板远程数据加载失败，使用默认值', err);
            });
        }

        console.log('Agent活动面板插件注册完成');
    }

    /**
     * 注册Scene活动面板插件
     */
    _registerScenePanels() {
        if (typeof ScenePanelPlugins === 'undefined') {
            console.warn('ScenePanelPlugins 未加载');
            return;
        }

        const {
            SceneBasicPanelPlugin,
            SceneEnginePanelPlugin,
            SceneParamsPanelPlugin,
            SceneCapabilityPanelPlugin
        } = ScenePanelPlugins;

        const register = (id, plugin, priority) => {
            if (this.environment) {
                this.environment.registerPlugin(id, plugin, priority);
            } else if (this.manager) {
                this.manager.register(id, plugin, priority);
            }
        };

        register('scene-basic', {
            ...SceneBasicPanelPlugin,
            id: 'scene-basic',
            dataConfig: {
                dataKey: 'sceneConfig',
                storageFormat: 'json'
            },
            applicable: (activity) => activity && activity.activityCategory === 'SCENE'
        }, 20);

        register('scene-engine', {
            ...SceneEnginePanelPlugin,
            id: 'scene-engine',
            dataConfig: {
                dataKey: 'engineConfig',
                storageFormat: 'json'
            },
            applicable: (activity) => activity && activity.activityCategory === 'SCENE'
        }, 30);

        register('scene-params', {
            ...SceneParamsPanelPlugin,
            id: 'scene-params',
            dataConfig: {
                dataKey: 'paramsConfig',
                storageFormat: 'json'
            },
            applicable: (activity) => activity && activity.activityCategory === 'SCENE'
        }, 40);

        register('scene-capability', {
            ...SceneCapabilityPanelPlugin,
            id: 'scene-capability',
            dataConfig: {
                dataKey: 'capabilityConfig',
                storageFormat: 'json'
            },
            applicable: (activity) => activity && activity.activityCategory === 'SCENE'
        }, 50);

        console.log('Scene活动面板插件注册完成');
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

        register('process-basic', {
            ...ProcessBasicPanelPlugin,
            id: 'process-basic',
            dataConfig: {
                dataKey: 'basic',
                storageFormat: 'json'
            },
            applicable: (process) => process && process.processDefId
        }, 10);

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

        register('process-variables', {
            ...ProcessVariablesPanelPlugin,
            id: 'process-variables',
            dataConfig: {
                dataKey: 'variables',
                storageFormat: 'json'
            },
            applicable: (process) => process && process.processDefId
        }, 60);

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

        register('dialog-expression', {
            ...ExpressionPanelPlugin,
            id: 'dialog-expression',
            type: 'dialog',
            dataConfig: {
                needRemoteData: true,
                remoteType: 'expressionVariables'
            }
        }, 10);

        register('dialog-performer', {
            ...PerformerSelectionPanelPlugin,
            id: 'dialog-performer',
            type: 'dialog',
            dataConfig: {
                needRemoteData: true,
                remoteType: 'person'
            }
        }, 20);

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
                'activity-agent': [
                    'activity-basic',
                    'agent-basic',
                    'agent-llm',
                    'agent-tools',
                    'activity-timing',
                    'activity-advanced'
                ],
                'activity-scene': [
                    'activity-basic',
                    'scene-basic',
                    'scene-engine',
                    'scene-params',
                    'scene-capability',
                    'activity-timing',
                    'activity-advanced'
                ],
                'route': [
                    'route-basic',
                    'route-condition'
                ]
            },
            
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
                'activity-advanced',
                'agent-basic',
                'agent-llm',
                'agent-tools',
                'scene-basic',
                'scene-engine',
                'scene-params',
                'scene-capability'
            ]
        };

        if (this.manager) {
            this.manager.loadConfig(config);
        }

        console.log('面板配置更新完成');
    }

    /**
     * 根据活动类型获取面板分组
     */
    getActivityGroup(activity) {
        if (!activity) return 'activity';
        
        const category = activity.activityCategory || 'HUMAN';
        
        switch (category) {
            case 'AGENT':
                return 'activity-agent';
            case 'SCENE':
                return 'activity-scene';
            default:
                return 'activity';
        }
    }

    getManager() {
        return this.manager;
    }

    getEnvironment() {
        return this.environment;
    }

    isInitialized() {
        return this.initialized;
    }

    /**
     * 渲染流程面板
     */
    renderProcessPanels(container, process) {
        if (this.environment) {
            return this.environment.renderForModel(container, process, 'process');
        } else if (this.manager) {
            return this.manager.renderForModel(container, process, 'process');
        }
    }

    /**
     * 渲染活动面板 - 根据活动类型自动选择分组
     */
    renderActivityPanels(container, activity) {
        const group = this.getActivityGroup(activity);
        
        if (this.environment) {
            return this.environment.renderForModel(container, activity, group);
        } else if (this.manager) {
            return this.manager.renderForModel(container, activity, group);
        }
    }
}

window.PanelInitializer = PanelInitializer;
