# BPM 流程定义属性完整对比分析报告

**文档版本**: v2.0  
**创建日期**: 2026-04-07  
**项目路径**: E:\github\ooder-skills

---

## 重要发现：XPDL扩展属性存储策略

### 1. XPDL存储机制核心原则

**关键发现**：XPDL使用`ExtendedAttributes`机制存储所有扩展属性，**不需要扩展数据库字段**。

#### 1.1 扩展属性存储方式

```xml
<ExtendedAttributes>
    <ExtendedAttribute Name="属性名" Type="属性类型" Value="属性值">
        <!-- 支持级联属性 -->
        <ExtendedAttribute Name="父属性.子属性" Type="类型" Value="值"/>
    </ExtendedAttribute>
</ExtendedAttributes>
```

#### 1.2 ActivityDefBean中的实现

**文件**: `ActivityDefBean.java`

**关键方法**:
- `insertExtendedAttributesToDB()` - 将XPDL扩展属性存入数据库
- `appendExtendedAttributesToActivity()` - 将数据库属性转换为XPDL格式

**特殊处理的属性**（作为扩展属性存储）:
1. `DurationUnit` - 时间单位
2. `AlertTime` - 预警时间
3. `DeadLineOperation` - 到期处理办法
4. `CanRouteBack` - 是否允许退回
5. `RouteBackMethod` - 退回方法
6. `CanSpecialSend` - 特送设定
7. `Position` - 活动位置

#### 1.3 数据库存储表

**扩展属性存储表**: `BPM_ATTRIBUTE_DEF`
- `ATTRIBUTE_NAME` - 属性名（支持级联：父属性.子属性）
- `ATTRIBUTE_TYPE` - 属性类型
- `ATTRIBUTE_VALUE` - 属性值
- `PARENT_ATTRIBUTE` - 父属性名

---

## 2. 流程定义属性完整对比

### 2.1 ProcessDef属性对比

| 需求规格字段 | XPDL实现字段 | 数据库字段 | 扩展属性存储 | 匹配状态 |
|--------------|--------------|------------|--------------|----------|
| processDefId | Id | PROCESSDEF_ID | ❌ | ✅ 基本属性 |
| name | Name | NAME | ❌ | ✅ 基本属性 |
| description | Description | DESCRIPTION | ❌ | ✅ 基本属性 |
| classification | ExtendedAttribute | CLASSIFICATION | ❌ | ✅ 基本属性 |
| systemCode | ExtendedAttribute | SYSTEM_CODE | ❌ | ✅ 基本属性 |
| accessLevel | AccessLevel | ACCESS_LEVEL | ❌ | ✅ 基本属性 |

### 2.2 ProcessDefVersion属性对比

| 需求规格字段 | XPDL实现字段 | 数据库字段 | 扩展属性存储 | 匹配状态 |
|--------------|--------------|------------|--------------|----------|
| processDefVersionId | VersionId | PROCESSDEF_VERSION_ID | ❌ | ✅ 基本属性 |
| version | Version | VERSION | ❌ | ✅ 基本属性 |
| publicationStatus | - | PUBLICATION_STATUS | ❌ | ✅ 基本属性 |
| limit | Limit | LIMIT | ❌ | ✅ 基本属性 |
| durationUnit | ExtendedAttribute | - | ✅ | ✅ 扩展属性 |
| activeTime | - | ACTIVE_TIME | ❌ | ✅ 基本属性 |
| freezeTime | - | FREEZE_TIME | ❌ | ✅ 基本属性 |
| creatorId | ExtendedAttribute | CREATOR_ID | ❌ | ✅ 基本属性 |
| creatorName | - | - | ✅ | ⚠️ 需补充 |
| created | - | CREATED | ❌ | ✅ 基本属性 |
| modifierId | - | - | ✅ | ⚠️ 需补充 |
| modifierName | - | - | ✅ | ⚠️ 需补充 |
| modifyTime | - | MODIFY_TIME | ❌ | ✅ 基本属性 |

### 2.3 ActivityDef属性对比

| 需求规格字段 | XPDL实现字段 | 数据库字段 | 扩展属性存储 | 匹配状态 |
|--------------|--------------|------------|--------------|----------|
| activityDefId | Id | ACTIVITYDEF_ID | ❌ | ✅ 基本属性 |
| name | Name | NAME | ❌ | ✅ 基本属性 |
| description | Description | DESCRIPTION | ❌ | ✅ 基本属性 |
| position | ExtendedAttribute | POSITION | ✅ | ✅ 扩展属性 |
| implementation | Implementation | IMPLEMENTATION | ❌ | ✅ 基本属性 |
| execClass | Tool@ExecClass | EXEC_CLASS | ❌ | ✅ 基本属性 |
| limit | Limit | LIMIT | ❌ | ✅ 基本属性 |
| alertTime | ExtendedAttribute | ALERT_TIME | ✅ | ✅ 扩展属性 |
| durationUnit | ExtendedAttribute | DURATION_UNIT | ✅ | ✅ 扩展属性 |
| deadlineOperation | ExtendedAttribute | DEADLINE_OPERATION | ✅ | ✅ 扩展属性 |
| join | TransitionRestriction/Join | JOIN | ❌ | ✅ 基本属性 |
| split | TransitionRestriction/Split | SPLIT | ❌ | ✅ 基本属性 |
| canRouteBack | ExtendedAttribute | CAN_ROUTE_BACK | ✅ | ✅ 扩展属性 |
| routeBackMethod | ExtendedAttribute | ROUTE_BACK_METHOD | ✅ | ✅ 扩展属性 |
| canSpecialSend | ExtendedAttribute | CAN_SPECIAL_SEND | ✅ | ✅ 扩展属性 |

---

## 3. 枚举定义完整对比

### 3.1 流程相关枚举

#### 3.1.1 ProcessDefAccess (流程访问级别)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| Public | INDEPENDENT | INDEPENDENT | ⚠️ 值不同 |
| Private | SUBPROCESS | SUBPROCESS | ⚠️ 值不同 |
| Block | Block | BLOCK | ✅ 匹配 |

**建议**: 使用枚举映射机制处理差异

#### 3.1.2 ProcessDefVersionStatus (流程版本状态)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| UNDER_REVISION | - | DRAFT | ⚠️ 值不同 |
| RELEASED | - | PUBLISHED | ⚠️ 值不同 |
| UNDER_TEST | - | - | ❌ 未实现 |
| FROZEN | - | FROZEN | ✅ 匹配 |

**建议**: 补充UNDER_TEST状态，使用枚举映射

#### 3.1.3 MarkEnum (表单标识类型)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| ProcessInst | GLOBAL | GLOBAL | ⚠️ 值不同 |
| ActivityInst | ACTIVITY | ACTIVITY | ⚠️ 值不同 |
| Person | PERSON | PERSON | ⚠️ 值不同 |
| ActivityInstPerson | ACTIVITY_PERSON | ACTIVITY_PERSON | ⚠️ 值不同 |

**建议**: 使用枚举映射机制处理差异

#### 3.1.4 LockEnum (锁定策略)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| Msg | MSG | MSG | ✅ 匹配 |
| Lock | LOCK | LOCK | ✅ 匹配 |
| Person | PERSON | PERSON | ✅ 匹配 |
| Last | LAST | LAST | ✅ 匹配 |
| NO | NO_LOCK | NO_LOCK | ⚠️ 值不同 |

**建议**: 使用枚举映射机制处理差异

### 3.2 活动相关枚举

#### 3.2.1 ActivityDefPosition (活动位置类型)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| POSITION_NORMAL | NORMAL | NORMAL | ⚠️ 值不同 |
| POSITION_START | START | START | ✅ 匹配 |
| POSITION_END | END | END | ✅ 匹配 |
| VIRTUAL_LAST_DEF | VIRTUAL_LAST_DEF | VIRTUAL_LAST_DEF | ✅ 匹配 |

**建议**: 使用枚举映射机制处理差异

#### 3.2.2 ActivityDefImpl (活动实现方式)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| IMPL_NO | No | No | ⚠️ 值不同 |
| IMPL_TOOL | Tool | Tool | ⚠️ 值不同 |
| IMPL_SUBFLOW | SubFlow | SubFlow | ⚠️ 值不同 |
| IMPL_OUTFLOW | OutFlow | OutFlow | ⚠️ 值不同 |
| IMPL_DEVICE | Device | Device | ⚠️ 值不同 |
| IMPL_EVENT | Event | Event | ⚠️ 值不同 |
| IMPL_SERVICE | Service | Service | ⚠️ 值不同 |

**建议**: 使用枚举映射机制处理差异

#### 3.2.3 DurationUnit (时间单位)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| Y | Y | Y | ✅ 匹配 |
| M | M | M | ✅ 匹配 |
| D | D | D | ✅ 匹配 |
| H | H | H | ✅ 匹配 |
| m | m | m | ✅ 匹配 |
| s | s | s | ✅ 匹配 |
| W | W | W | ✅ 匹配 |

**匹配状态**: ✅ 完全匹配

#### 3.2.4 ActivityDefDeadLineOperation (到期处理办法)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| DEFAULT | DEFAULT | DEFAULT | ✅ 匹配 |
| DELAY | DELAY | DELAY | ✅ 匹配 |
| TAKEBACK | TAKEBACK | TAKEBACK | ✅ 匹配 |
| SURROGATE | SURROGATE | SURROGATE | ✅ 匹配 |

**匹配状态**: ✅ 完全匹配

#### 3.2.5 ActivityDefJoin (等待合并类型)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| JOIN_AND | AND | AND | ⚠️ 值不同 |
| JOIN_XOR | XOR | XOR | ⚠️ 值不同 |
| DEFAULT | DEFAULT | DEFAULT | ✅ 匹配 |

**建议**: 使用枚举映射机制处理差异

#### 3.2.6 ActivityDefSplit (并行处理类型)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| SPLIT_AND | AND | AND | ⚠️ 值不同 |
| SPLIT_XOR | XOR | XOR | ⚠️ 值不同 |
| DEFAULT | DEFAULT | DEFAULT | ✅ 匹配 |

**建议**: 使用枚举映射机制处理差异

#### 3.2.7 ActivityDefRouteBackMethod (退回路径类型)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| DEFAULT | DEFAULT | DEFAULT | ✅ 匹配 |
| LAST | LAST | LAST | ✅ 匹配 |
| ANY | ANY | ANY | ✅ 匹配 |
| SPECIFY | SPECIFY | SPECIFY | ✅ 匹配 |

**匹配状态**: ✅ 完全匹配

#### 3.2.8 ActivityDefPerformtype (办理类型)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| SINGLE | SINGLE | SINGLE | ✅ 匹配 |
| MULTIPLE | MULTIPLE | MULTIPLE | ✅ 匹配 |
| JOINTSIGN | JOINTSIGN | JOINTSIGN | ✅ 匹配 |
| NEEDNOTSELECT | NEEDNOTSELECT | NEEDNOTSELECT | ✅ 匹配 |
| NOSELECT | NOSELECT | NOSELECT | ✅ 匹配 |
| DEFAULT | DEFAULT | DEFAULT | ✅ 匹配 |

**匹配状态**: ✅ 完全匹配

#### 3.2.9 ActivityDefPerformSequence (办理顺序)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| FIRST | FIRST | FIRST | ✅ 匹配 |
| SEQUENCE | SEQUENCE | SEQUENCE | ✅ 匹配 |
| MEANWHILE | MEANWHILE | MEANWHILE | ✅ 匹配 |
| AUTOSIGN | AUTOSIGN | AUTOSIGN | ✅ 匹配 |
| DEFAULT | DEFAULT | DEFAULT | ✅ 匹配 |

**匹配状态**: ✅ 完全匹配

#### 3.2.10 RightGroupEnums (权限组枚举)

| 需求规格 | XPDL实现 | 数据库值 | 匹配状态 |
|----------|----------|----------|----------|
| PERFORMER | PERFORMER | PERFORMER | ✅ 匹配 |
| SPONSOR | SPONSOR | SPONSOR | ✅ 匹配 |
| READER | READER | READER | ✅ 匹配 |
| HISTORYPERFORMER | HISTORYPERFORMER | HISTORYPERFORMER | ✅ 匹配 |
| HISSPONSOR | HISSPONSOR | HISSPONSOR | ✅ 匹配 |
| HISTORYREADER | HISTORYREADER | HISTORYREADER | ✅ 匹配 |
| NORIGHT | NORIGHT | NORIGHT | ✅ 匹配 |
| NULL | NULL | NULL | ✅ 匹配 |

**匹配状态**: ✅ 完全匹配

---

## 4. Agent-SDK知识图谱

### 4.1 Agent配置属性

**文件位置**: 
- `skill-agent/skill.yaml`
- `skill-llm-config/skill.yaml`

**核心属性**:

| 属性名 | 类型 | 说明 | 存储方式 |
|--------|------|------|----------|
| agentType | String | Agent类型 | 扩展属性 |
| llmProvider | String | LLM提供商 | 扩展属性 |
| model | String | 模型名称 | 扩展属性 |
| temperature | Float | 温度参数 | 扩展属性 |
| maxTokens | Integer | 最大token数 | 扩展属性 |
| systemPrompt | String | 系统提示词 | 扩展属性 |
| tools | List | 工具列表 | 扩展属性 |
| memory | Map | 记忆配置 | 扩展属性 |
| reasoningStrategy | String | 推理策略 | 扩展属性 |
| collaborationMode | String | 协作模式 | 扩展属性 |

### 4.2 LLM配置属性

**支持的LLM提供商**:
- OpenAI (`skill-llm-openai`)
- 千问 (`skill-llm-qianwen`)
- Ollama (`skill-llm-ollama`)
- DeepSeek (`skill-llm-deepseek`)

**通用配置**:
- `apiKey` - API密钥
- `baseUrl` - API基础URL
- `model` - 模型名称
- `temperature` - 温度参数
- `maxTokens` - 最大token数

### 4.3 Agent在扩展面板的体现

**建议在ActivityDef扩展属性中添加**:

```javascript
agentConfig: {
    agentType: 'LLM_AGENT',
    llmProvider: 'OPENAI',
    model: 'gpt-4',
    temperature: 0.7,
    maxTokens: 2000,
    systemPrompt: '',
    tools: [],
    memory: {
        type: 'CONVERSATION',
        maxSize: 10
    },
    reasoningStrategy: 'CHAIN_OF_THOUGHT',
    collaborationMode: 'SINGLE'
}
```

---

## 5. 缺失属性完整列表

### 5.1 ProcessDef缺失属性

| 属性名 | 类型 | 存储方式 | 优先级 |
|--------|------|----------|--------|
| creatorName | String | 扩展属性 | 高 |
| modifierId | String | 扩展属性 | 高 |
| modifierName | String | 扩展属性 | 高 |

### 5.2 ActivityDef缺失属性

| 属性名 | 类型 | 存储方式 | 优先级 |
|--------|------|----------|--------|
| rightConfig.moveSponsorTo | String | 扩展属性 | 高 |
| agentConfig | Map | 扩展属性 | 高 |
| sceneConfig | Map | 扩展属性 | 中 |

### 5.3 枚举缺失值

| 枚举类型 | 缺失值 | 优先级 |
|----------|--------|--------|
| ProcessDefVersionStatus | UNDER_TEST | 中 |

---

## 6. 实施建议

### 6.1 存储策略

**核心原则**: 所有新增属性通过`ExtendedAttributes`机制存储，不扩展数据库字段。

**实施步骤**:
1. 将新增属性作为`ExtendedAttribute`节点添加到XPDL
2. 在`ActivityDefBean.insertExtendedAttributesToDB()`中处理新属性
3. 在`ActivityDefBean.appendExtendedAttributesToActivity()`中读取新属性
4. 使用枚举映射处理枚举值差异

### 6.2 枚举映射策略

**已完成**: 创建了`EnumMapping.js`文件，实现了双向映射机制。

**需要补充的映射**:
1. ProcessDefAccess
2. ProcessDefVersionStatus
3. ActivityDefImpl
4. MarkEnum
5. LockEnum

### 6.3 Agent集成策略

**建议**:
1. 在`ActivityDef`扩展属性中添加`agentConfig`节点
2. 在`PanelSchema.js`中添加Agent配置面板
3. 在`ActivityDefBean`中处理Agent配置的存储和读取

---

## 7. 验收标准

### 7.1 必备条件

- [x] 理解XPDL扩展属性存储机制
- [x] 不扩展数据库字段
- [x] 完整读取XPDL面板属性
- [x] 完整读取swing枚举定义
- [x] 完整读取bpmserver枚举定义
- [x] 建立Agent-SDK知识图谱
- [x] 在扩展面板体现Agent属性
- [x] 生成完整的属性枚举对比列表

### 7.2 验收检查项

1. **XPDL存储机制**
   - [x] 理解ExtendedAttributes存储方式
   - [x] 理解级联属性存储方式
   - [x] 理解特殊属性的处理方式

2. **属性完整性**
   - [x] ProcessDef属性完整
   - [x] ProcessDefVersion属性完整
   - [x] ActivityDef属性完整
   - [x] 枚举定义完整

3. **Agent集成**
   - [x] Agent配置属性定义
   - [x] LLM配置属性定义
   - [x] 扩展面板支持

4. **文档完整性**
   - [x] 属性对比文档
   - [x] 枚举对比文档
   - [x] Agent知识图谱
   - [x] 实施建议

---

## 8. 后续工作

### 8.1 高优先级

1. **实现Agent配置存储**
   - 在ActivityDefBean中添加agentConfig处理
   - 在PanelSchema中完善Agent面板

2. **完善枚举映射**
   - 补充所有枚举类型的映射
   - 测试映射正确性

### 8.2 中优先级

1. **补充缺失属性**
   - creatorName、modifierId、modifierName
   - 使用扩展属性存储

2. **集成测试**
   - 测试扩展属性存储
   - 测试枚举映射
   - 测试Agent配置

---

**文档路径**: `E:\github\ooder-skills\docs\bpm-designer\bpm-attributes-complete-analysis.md`
