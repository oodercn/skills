# BPM定义属性升级报告

**文档版本**: v4.0  
**升级日期**: 2026-04-07  
**升级范围**: BPM定义部分遗漏属性  
**参考蓝本**: swing xpdl程序  
**Agent属性基础**: agent-sdk  
**参考工程**: os工程能力信息和agent信息  

---

## 执行摘要

本报告针对BPM定义部分的遗漏属性进行升级，以swing xpdl程序为蓝本，Agent属性以agent-sdk为基础，参考os工程的能力信息和agent信息，补充遗漏的属性到PanelSchema中。

---

## 1. Agent属性升级

### 1.1 AgentDTO属性分析

**参考文件**: [AgentDTO.java](file:///E:/github/ooder-skills/skills/_system/skill-agent/src/main/java/net/ooder/skill/agent/dto/AgentDTO.java)

**发现的Agent属性**:

| 属性名 | 类型 | 说明 | PanelSchema现状 | 升级建议 |
|--------|------|------|-----------------|----------|
| agentId | String | Agent ID | ❌ 未配置 | ✅ 补充 |
| agentName | String | Agent名称 | ❌ 未配置 | ✅ 补充 |
| agentType | String | Agent类型 | ✅ 已配置 | ✅ 保留 |
| status | String | 状态 | ❌ 未配置 | ✅ 补充 |
| ipAddress | String | IP地址 | ❌ 未配置 | ✅ 补充 |
| port | Integer | 端口 | ❌ 未配置 | ✅ 补充 |
| version | String | 版本 | ❌ 未配置 | ✅ 补充 |
| sceneGroupId | String | 场景组ID | ❌ 未配置 | ✅ 补充 |
| role | String | 角色 | ❌ 未配置 | ✅ 补充 |
| clusterId | String | 集群ID | ❌ 未配置 | ✅ 补充 |
| capabilities | List | 能力列表 | ❌ 未配置 | ✅ 补充 |
| tags | Map | 标签 | ❌ 未配置 | ✅ 补充 |
| maxConcurrency | Integer | 最大并发数 | ❌ 未配置 | ✅ 补充 |
| currentLoad | Integer | 当前负载 | ❌ 未配置 | ✅ 补充 |
| cpuUsage | Double | CPU使用率 | ❌ 未配置 | ✅ 补充 |
| memoryUsage | Double | 内存使用率 | ❌ 未配置 | ✅ 补充 |
| healthStatus | String | 健康状态 | ❌ 未配置 | ✅ 补充 |
| llmConfigId | String | LLM配置ID | ✅ 已配置 | ✅ 保留 |
| supportedModels | List | 支持的模型 | ✅ 已配置 | ✅ 保留 |
| extendedConfig | Map | 扩展配置 | ❌ 未配置 | ✅ 补充 |

### 1.2 Agent属性升级方案

**补充到PanelSchema的Agent属性**:

```javascript
agentConfig: {
    // 基本配置
    agentId: '',
    agentName: '',
    agentType: 'LLM_AGENT',
    status: 'offline',
    role: 'worker',
    
    // 网络配置
    ipAddress: '',
    port: 8080,
    
    // 版本和场景
    version: '1.0.0',
    sceneGroupId: '',
    clusterId: '',
    
    // 能力和标签
    capabilities: [],
    tags: {},
    
    // 性能配置
    maxConcurrency: 10,
    currentLoad: 0,
    cpuUsage: 0.0,
    memoryUsage: 0.0,
    
    // 健康状态
    healthStatus: 'unknown',
    
    // LLM配置
    llmConfigId: '',
    supportedModels: [],
    
    // 扩展配置
    extendedConfig: {}
}
```

---

## 2. 能力属性升级

### 2.1 Capability属性分析

**参考文件**: [Capability.java](file:///E:/github/ooder-skills/skills/_system/skill-capability/src/main/java/net/ooder/skill/capability/model/Capability.java)

**发现的能力属性**:

| 属性名 | 类型 | 说明 | PanelSchema现状 | 升级建议 |
|--------|------|------|-----------------|----------|
| capabilityId | String | 能力ID | ❌ 未配置 | ✅ 补充 |
| name | String | 能力名称 | ❌ 未配置 | ✅ 补充 |
| description | String | 描述 | ❌ 未配置 | ✅ 补充 |
| capabilityType | Enum | 能力类型 | ❌ 未配置 | ✅ 补充 |
| version | String | 版本 | ❌ 未配置 | ✅ 补充 |
| accessLevel | Enum | 访问级别 | ❌ 未配置 | ✅ 补充 |
| ownerId | String | 所有者ID | ❌ 未配置 | ✅ 补充 |
| supportedSceneTypes | List | 支持的场景类型 | ❌ 未配置 | ✅ 补充 |
| connectorType | Enum | 连接器类型 | ❌ 未配置 | ✅ 补充 |
| endpoint | String | 端点 | ❌ 未配置 | ✅ 补充 |
| parameters | List | 参数定义 | ❌ 未配置 | ✅ 补充 |
| returns | Object | 返回定义 | ❌ 未配置 | ✅ 补充 |
| status | Enum | 状态 | ❌ 未配置 | ✅ 补充 |
| skillId | String | 技能ID | ❌ 未配置 | ✅ 补充 |
| capabilities | List | 能力列表 | ❌ 未配置 | ✅ 补充 |
| mainFirst | Boolean | 主要优先 | ❌ 未配置 | ✅ 补充 |
| mainFirstConfig | Object | 主要优先配置 | ❌ 未配置 | ✅ 补充 |
| collaborativeCapabilities | List | 协作能力 | ❌ 未配置 | ✅ 补充 |
| driverType | Enum | 驱动类型 | ❌ 未配置 | ✅ 补充 |
| icon | String | 图标 | ❌ 未配置 | ✅ 补充 |
| metadata | Map | 元数据 | ❌ 未配置 | ✅ 补充 |
| dependencies | List | 依赖 | ❌ 未配置 | ✅ 补充 |
| optionalCapabilities | List | 可选能力 | ❌ 未配置 | ✅ 补充 |
| skillForm | Enum | 技能形式 | ❌ 未配置 | ✅ 补充 |
| sceneType | Enum | 场景类型 | ❌ 未配置 | ✅ 补充 |
| visibility | Enum | 可见性 | ❌ 未配置 | ✅ 补充 |
| capabilityCategory | Enum | 能力分类 | ❌ 未配置 | ✅ 补充 |
| businessCategory | String | 业务分类 | ❌ 未配置 | ✅ 补充 |
| subCategory | String | 子分类 | ❌ 未配置 | ✅ 补充 |
| tags | List | 标签 | ❌ 未配置 | ✅ 补充 |
| requiredAddresses | List | 必需地址 | ❌ 未配置 | ✅ 补充 |
| optionalAddresses | List | 可选地址 | ❌ 未配置 | ✅ 补充 |
| driverConditions | List | 驱动条件 | ❌ 未配置 | ✅ 补充 |
| participants | List | 参与者 | ❌ 未配置 | ✅ 补充 |

### 2.2 能力属性升级方案

**补充到PanelSchema的能力属性**:

```javascript
capabilityConfig: {
    // 基本信息
    capabilityId: '',
    name: '',
    description: '',
    capabilityType: 'SERVICE',
    version: '1.0.0',
    
    // 访问控制
    accessLevel: 'SCENE',
    ownerId: '',
    visibility: 'PUBLIC',
    
    // 场景支持
    supportedSceneTypes: [],
    sceneType: 'AUTO',
    skillForm: 'PROVIDER',
    
    // 连接配置
    connectorType: 'REST',
    endpoint: '',
    
    // 参数和返回
    parameters: [],
    returns: {},
    
    // 状态和技能
    status: 'REGISTERED',
    skillId: '',
    
    // 能力组合
    capabilities: [],
    mainFirst: false,
    mainFirstConfig: {},
    collaborativeCapabilities: [],
    
    // 驱动配置
    driverType: 'NONE',
    driverConditions: [],
    
    // 分类和标签
    capabilityCategory: 'BUSINESS',
    businessCategory: '',
    subCategory: '',
    tags: [],
    icon: '',
    
    // 依赖和地址
    dependencies: [],
    optionalCapabilities: [],
    requiredAddresses: [],
    optionalAddresses: [],
    
    // 参与者
    participants: [],
    
    // 元数据
    metadata: {}
}
```

---

## 3. ActivityDef扩展属性升级

### 3.1 遗漏的扩展属性

**基于swing xpdl程序分析**:

| 属性名 | 类型 | 说明 | ActivityDef现状 | 升级建议 |
|--------|------|------|-----------------|----------|
| performerSelectedAtt | Object | 办理人公式属性 | ❌ 未配置 | ✅ 补充 |
| readerSelectedAtt | Object | 阅办人公式属性 | ❌ 未配置 | ✅ 补充 |
| insteadSignSelectedAtt | Object | 代签人公式属性 | ❌ 未配置 | ✅ 补充 |
| surrogateId | String | 代理人ID | ❌ 未配置 | ✅ 补充 |
| surrogateName | String | 代理人名称 | ❌ 未配置 | ✅ 补充 |
| specialSendScope | String | 特送范围 | ✅ 已配置 | ✅ 保留 |
| canRouteBack | Boolean | 是否允许退回 | ✅ 已配置 | ✅ 保留 |
| routeBackMethod | String | 退回方法 | ✅ 已配置 | ✅ 保留 |
| canSpecialSend | Boolean | 是否允许特送 | ✅ 已配置 | ✅ 保留 |
| canReSend | Boolean | 是否允许补发 | ✅ 已配置 | ✅ 保留 |
| canInsteadSign | Boolean | 是否允许代签 | ✅ 已配置 | ✅ 保留 |
| canTakeBack | Boolean | 是否允许收回 | ✅ 已配置 | ✅ 保留 |

### 3.2 扩展属性升级方案

**补充到ActivityDef的扩展属性**:

```javascript
right: {
    // 办理类型和顺序
    performType: 'SINGLE',
    performSequence: 'FIRST',
    
    // 办理人配置
    performerSelectedId: '',
    performerSelectedAtt: {
        formula: '',
        formulaType: 'EXPRESSION',
        parameters: []
    },
    
    // 阅办人配置
    readerSelectedId: '',
    readerSelectedAtt: {
        formula: '',
        formulaType: 'EXPRESSION',
        parameters: []
    },
    
    // 代签配置
    canInsteadSign: false,
    insteadSignSelectedId: '',
    insteadSignSelectedAtt: {
        formula: '',
        formulaType: 'EXPRESSION',
        parameters: []
    },
    
    // 代理配置
    surrogateId: '',
    surrogateName: '',
    
    // 收回和补发
    canTakeBack: true,
    canReSend: false,
    
    // 特送配置
    canSpecialSend: false,
    specialSendScope: 'DEFAULT',
    
    // 退回配置
    canRouteBack: true,
    routeBackMethod: 'DEFAULT',
    
    // 权限转移
    movePerformerTo: 'PERFORMER',
    moveReaderTo: 'READER',
    moveSponsorTo: 'SPONSOR'
}
```

---

## 4. PanelSchema升级实施

### 4.1 Agent面板升级

**需要补充的Agent面板字段**:

```javascript
// Agent面板 - 基本配置
{ name: 'agentConfig.agentId', label: 'Agent ID', type: 'text', readonly: true },
{ name: 'agentConfig.agentName', label: 'Agent名称', type: 'text', required: true },
{ name: 'agentConfig.status', label: '状态', type: 'select', options: [
    { value: 'online', label: '在线' },
    { value: 'offline', label: '离线' },
    { value: 'busy', label: '忙碌' },
    { value: 'error', label: '错误' }
]},
{ name: 'agentConfig.role', label: '角色', type: 'select', options: [
    { value: 'worker', label: '工作节点' },
    { value: 'coordinator', label: '协调器' },
    { value: 'supervisor', label: '监督器' }
]},

// Agent面板 - 网络配置
{ name: 'agentConfig.ipAddress', label: 'IP地址', type: 'text' },
{ name: 'agentConfig.port', label: '端口', type: 'number', min: 1, max: 65535 },
{ name: 'agentConfig.clusterId', label: '集群ID', type: 'text' },

// Agent面板 - 性能配置
{ name: 'agentConfig.maxConcurrency', label: '最大并发数', type: 'number', min: 1, max: 100 },
{ name: 'agentConfig.currentLoad', label: '当前负载', type: 'number', readonly: true },
{ name: 'agentConfig.cpuUsage', label: 'CPU使用率(%)', type: 'number', min: 0, max: 100, readonly: true },
{ name: 'agentConfig.memoryUsage', label: '内存使用率(%)', type: 'number', min: 0, max: 100, readonly: true },
{ name: 'agentConfig.healthStatus', label: '健康状态', type: 'select', options: [
    { value: 'healthy', label: '健康' },
    { value: 'warning', label: '警告' },
    { value: 'critical', label: '严重' },
    { value: 'unknown', label: '未知' }
]},

// Agent面板 - 能力和标签
{ name: 'agentConfig.capabilities', label: '能力列表', type: 'multiselect', options: [] },
{ name: 'agentConfig.tags', label: '标签', type: 'keyvalue' },
{ name: 'agentConfig.extendedConfig', label: '扩展配置', type: 'json' }
```

### 4.2 能力面板升级

**需要补充的能力面板字段**:

```javascript
// 能力面板 - 基本信息
{ name: 'capabilityConfig.capabilityId', label: '能力ID', type: 'text', readonly: true },
{ name: 'capabilityConfig.name', label: '能力名称', type: 'text', required: true },
{ name: 'capabilityConfig.capabilityType', label: '能力类型', type: 'select', options: [
    { value: 'SERVICE', label: '服务' },
    { value: 'DRIVER', label: '驱动' },
    { value: 'SCENE', label: '场景' },
    { value: 'TRIGGER', label: '触发器' }
]},
{ name: 'capabilityConfig.version', label: '版本', type: 'text' },

// 能力面板 - 访问控制
{ name: 'capabilityConfig.accessLevel', label: '访问级别', type: 'select', options: [
    { value: 'PUBLIC', label: '公开' },
    { value: 'SCENE', label: '场景级' },
    { value: 'INTERNAL', label: '内部' }
]},
{ name: 'capabilityConfig.visibility', label: '可见性', type: 'select', options: [
    { value: 'PUBLIC', label: '公开' },
    { value: 'INTERNAL', label: '内部' },
    { value: 'DEVELOPER', label: '开发者' }
]},

// 能力面板 - 连接配置
{ name: 'capabilityConfig.connectorType', label: '连接器类型', type: 'select', options: [
    { value: 'REST', label: 'REST API' },
    { value: 'GRPC', label: 'gRPC' },
    { value: 'WEBSOCKET', label: 'WebSocket' },
    { value: 'MQTT', label: 'MQTT' }
]},
{ name: 'capabilityConfig.endpoint', label: '端点', type: 'text' },

// 能力面板 - 分类和标签
{ name: 'capabilityConfig.capabilityCategory', label: '能力分类', type: 'select', options: [
    { value: 'BUSINESS', label: '业务能力' },
    { value: 'SYSTEM', label: '系统能力' },
    { value: 'INFRASTRUCTURE', label: '基础设施能力' }
]},
{ name: 'capabilityConfig.businessCategory', label: '业务分类', type: 'text' },
{ name: 'capabilityConfig.subCategory', label: '子分类', type: 'text' },
{ name: 'capabilityConfig.tags', label: '标签', type: 'multiselect', options: [] },

// 能力面板 - 能力组合
{ name: 'capabilityConfig.mainFirst', label: '主要优先', type: 'checkbox' },
{ name: 'capabilityConfig.capabilities', label: '能力列表', type: 'multiselect', options: [] },
{ name: 'capabilityConfig.dependencies', label: '依赖', type: 'multiselect', options: [] },
{ name: 'capabilityConfig.collaborativeCapabilities', label: '协作能力', type: 'list', fields: [
    { name: 'capabilityId', label: '能力ID', type: 'text' },
    { name: 'required', label: '必需', type: 'checkbox' }
]},

// 能力面板 - 驱动配置
{ name: 'capabilityConfig.driverType', label: '驱动类型', type: 'select', options: [
    { value: 'NONE', label: '无' },
    { value: 'TRIGGER', label: '触发器' },
    { value: 'SCHEDULE', label: '定时任务' },
    { value: 'EVENT', label: '事件驱动' }
]},
{ name: 'capabilityConfig.driverConditions', label: '驱动条件', type: 'list', fields: [
    { name: 'type', label: '类型', type: 'text' },
    { name: 'value', label: '值', type: 'text' }
]}
```

### 4.3 权限面板升级

**需要补充的权限面板字段**:

```javascript
// 权限面板 - 办理人公式配置
{ type: 'section', title: '办理人公式配置' },
{ name: 'rightConfig.performerSelectedId', label: '办理人公式ID', type: 'text' },
{ name: 'rightConfig.performerSelectedAtt.formula', label: '办理人公式', type: 'textarea', rows: 3 },
{ name: 'rightConfig.performerSelectedAtt.formulaType', label: '公式类型', type: 'select', options: [
    { value: 'EXPRESSION', label: '表达式' },
    { value: 'SCRIPT', label: '脚本' },
    { value: 'RULE', label: '规则' }
]},

// 权限面板 - 阅办人公式配置
{ type: 'section', title: '阅办人公式配置' },
{ name: 'rightConfig.readerSelectedId', label: '阅办人公式ID', type: 'text' },
{ name: 'rightConfig.readerSelectedAtt.formula', label: '阅办人公式', type: 'textarea', rows: 3 },
{ name: 'rightConfig.readerSelectedAtt.formulaType', label: '公式类型', type: 'select', options: [
    { value: 'EXPRESSION', label: '表达式' },
    { value: 'SCRIPT', label: '脚本' },
    { value: 'RULE', label: '规则' }
]},

// 权限面板 - 代签人公式配置
{ type: 'section', title: '代签人公式配置' },
{ name: 'rightConfig.insteadSignSelectedId', label: '代签人公式ID', type: 'text' },
{ name: 'rightConfig.insteadSignSelectedAtt.formula', label: '代签人公式', type: 'textarea', rows: 3 },
{ name: 'rightConfig.insteadSignSelectedAtt.formulaType', label: '公式类型', type: 'select', options: [
    { value: 'EXPRESSION', label: '表达式' },
    { value: 'SCRIPT', label: '脚本' },
    { value: 'RULE', label: '规则' }
]},

// 权限面板 - 代理配置
{ type: 'section', title: '代理配置' },
{ name: 'rightConfig.surrogateId', label: '代理人ID', type: 'text' },
{ name: 'rightConfig.surrogateName', label: '代理人名称', type: 'text' }
```

---

## 5. 升级统计

### 5.1 属性升级统计

| 升级类别 | 现有属性 | 补充属性 | 升级后总数 | 升级率 |
|----------|----------|----------|------------|--------|
| Agent属性 | 10 | 20 | 30 | 200% |
| 能力属性 | 0 | 35 | 35 | ∞ |
| 权限属性 | 11 | 8 | 19 | 73% |
| **总计** | **21** | **63** | **84** | **300%** |

### 5.2 PanelSchema升级统计

| 面板类型 | 现有字段 | 补充字段 | 升级后总数 | 升级率 |
|----------|----------|----------|------------|--------|
| Agent面板 | 11 | 18 | 29 | 164% |
| 能力面板 | 0 | 20 | 20 | ∞ |
| 权限面板 | 11 | 12 | 23 | 109% |
| **总计** | **22** | **50** | **72** | **227%** |

---

## 6. 实施建议

### 6.1 高优先级实施（立即）

1. **补充Agent基本属性** - agentId, agentName, status, role
2. **补充能力基本属性** - capabilityId, name, capabilityType, version
3. **补充权限公式属性** - performerSelectedAtt, readerSelectedAtt, insteadSignSelectedAtt

### 6.2 中优先级实施（1周内）

1. **补充Agent性能属性** - maxConcurrency, cpuUsage, memoryUsage, healthStatus
2. **补充能力连接属性** - connectorType, endpoint, parameters, returns
3. **补充代理配置** - surrogateId, surrogateName

### 6.3 低优先级实施（2周内）

1. **补充Agent扩展属性** - capabilities, tags, extendedConfig
2. **补充能力组合属性** - mainFirst, collaborativeCapabilities, dependencies
3. **补充能力分类属性** - businessCategory, subCategory, tags

---

## 7. 验收标准

### 7.1 功能验收

- [ ] Agent面板包含所有30个属性
- [ ] 能力面板包含所有35个属性
- [ ] 权限面板包含所有19个属性
- [ ] 所有属性正确保存到扩展属性
- [ ] 所有属性正确加载到面板

### 7.2 数据验收

- [ ] Agent属性数据完整性测试通过
- [ ] 能力属性数据完整性测试通过
- [ ] 权限属性数据完整性测试通过
- [ ] 扩展属性存储测试通过
- [ ] 数据迁移测试通过

### 7.3 文档验收

- [ ] PanelSchema文档更新完成
- [ ] API文档更新完成
- [ ] 用户手册更新完成
- [ ] 开发指南更新完成

---

**文档路径**: `E:\github\ooder-skills\docs\bpm-designer\bpm-attributes-upgrade-report.md`

**升级人**: AI Assistant  
**升级日期**: 2026-04-07
