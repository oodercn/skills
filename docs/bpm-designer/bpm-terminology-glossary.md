# BPM 流程定义术语表

**文档版本**: 1.0  
**创建日期**: 2026-04-08  
**项目路径**: E:\github\ooder-skills\skills\_drivers\bpm\bpm-designer

---

## 📋 一、流程定义术语 (ProcessDef)

### 1.1 基本属性

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 流程ID | processDefId | String | ✅ | 流程定义的唯一标识符，以字母开头，只能包含字母、数字、下划线和连字符 | `leave_approval` |
| 流程名称 | name | String | ✅ | 流程的显示名称，长度1-100字符 | `请假审批流程` |
| 流程描述 | description | String | ❌ | 流程的详细描述，最大500字符 | `员工请假申请审批流程` |
| 流程分类 | classification | Enum | ❌ | 流程的业务分类 | `NORMAL` |
| 系统代码 | systemCode | String | ❌ | 所属系统的代码标识 | `HR_SYSTEM` |
| 访问级别 | accessLevel | Enum | ✅ | 流程的访问权限级别 | `PUBLIC` |
| 版本号 | version | Integer | ❌ | 流程定义的版本号，从0开始 | `1` |
| 发布状态 | publicationStatus | Enum | ❌ | 流程的发布状态 | `DRAFT` |

### 1.2 时限配置

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 时限 | limit | Integer | ❌ | 流程实例的时限数值 | `30` |
| 时长单位 | durationUnit | Enum | ❌ | 时限的时间单位 | `D` |
| 生效时间 | activeTime | String | ❌ | 流程生效的开始时间 | `2026-01-01 00:00:00` |
| 冻结时间 | freezeTime | String | ❌ | 流程冻结的时间 | `2026-12-31 23:59:59` |

### 1.3 审计信息

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 创建人ID | creatorId | String | ❌ | 流程创建者的用户ID | `user001` |
| 创建人名称 | creatorName | String | ❌ | 流程创建者的用户名 | `张三` |
| 创建时间 | createdTime | String | ❌ | 流程创建的时间戳 | `2026-04-08 10:00:00` |
| 修改人ID | modifierId | String | ❌ | 最后修改者的用户ID | `user002` |
| 修改人名称 | modifierName | String | ❌ | 最后修改者的用户名 | `李四` |
| 修改时间 | modifyTime | String | ❌ | 最后修改的时间戳 | `2026-04-08 15:30:00` |

### 1.4 高级配置

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 表单标识 | mark | Enum | ❌ | 表单标识类型 | `ProcessInst` |
| 锁定策略 | lock | Enum | ❌ | 流程锁定策略 | `Lock` |
| 自动保存 | autoSave | Boolean | ❌ | 是否启用自动保存 | `true` |
| 非SQL类型 | noSqlType | Boolean | ❌ | 是否使用非SQL存储 | `false` |
| 关联表名 | tableNames | List | ❌ | 流程关联的数据库表名列表 | `["t_leave", "t_approval"]` |
| 关联模块 | moduleNames | List | ❌ | 流程关联的业务模块列表 | `["hr", "workflow"]` |

### 1.5 扩展属性

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 扩展属性 | extendedAttributes | Map | ❌ | 自定义扩展属性键值对 | `{"customField": "value"}` |

---

## 📋 二、活动定义术语 (ActivityDef)

### 2.1 基本属性

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 活动ID | activityDefId | String | ✅ | 活动定义的唯一标识符 | `submit` |
| 活动名称 | name | String | ✅ | 活动的显示名称 | `提交申请` |
| 活动描述 | description | String | ❌ | 活动的详细描述 | `员工提交请假申请` |
| 活动位置 | position | Enum | ✅ | 活动在流程中的位置类型 | `NORMAL` |
| 活动类型 | activityType | Enum | ✅ | 活动的类型 | `TASK` |
| 活动分类 | activityCategory | Enum | ✅ | 活动的业务分类 | `HUMAN` |
| 实现方式 | implementation | Enum | ✅ | 活动的实现方式 | `IMPL_NO` |
| 执行类 | execClass | String | ❌ | 自定义执行类的全限定名 | `com.example.TaskExecutor` |

### 2.2 位置坐标

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 位置坐标 | positionCoord | Map | ❌ | 活动在画布上的坐标位置 | `{"x": 100, "y": 200}` |

### 2.3 时限配置 (Timing)

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 时限配置 | timing | Map | ❌ | 活动的时限配置对象 | 见下方详细字段 |
| 时限值 | timing.limit | Integer | ❌ | 活动的处理时限数值 | `3` |
| 时长单位 | timing.durationUnit | Enum | ❌ | 时限的时间单位 | `D` |
| 到期处理 | timing.deadlineOperation | Enum | ❌ | 到期后的处理方式 | `DELAY` |

### 2.4 路由配置 (Routing)

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 路由配置 | routing | Map | ❌ | 活动的路由配置对象 | 见下方详细字段 |
| 汇聚类型 | routing.join | Enum | ❌ | 多输入路由的汇聚方式 | `JOIN_AND` |
| 分支类型 | routing.split | Enum | ❌ | 多输出路由的分支方式 | `SPLIT_XOR` |
| 启动模式 | routing.startMode | Enum | ❌ | 活动的启动模式 | `MANUAL` |
| 完成模式 | routing.finishMode | Enum | ❌ | 活动的完成模式 | `MANUAL` |

### 2.5 权限配置 (Right)

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 权限配置 | right | Map | ❌ | 活动的权限配置对象 | 见下方详细字段 |
| 办理类型 | right.performType | Enum | ❌ | 办理人的处理方式 | `SINGLE` |
| 办理顺序 | right.performSequence | Enum | ❌ | 多人办理的顺序 | `SEQUENCE` |
| 办理人公式ID | right.performerSelectedId | String | ❌ | 办理人公式的ID | `manager_formula` |
| 办理人公式 | right.performerSelectedAtt.formula | String | ❌ | 办理人计算公式 | `getManagers(dept)` |
| 公式类型 | right.performerSelectedAtt.formulaType | Enum | ❌ | 公式的类型 | `EXPRESSION` |
| 阅办人公式ID | right.readerSelectedId | String | ❌ | 阅办人公式的ID | `reader_formula` |
| 阅办人公式 | right.readerSelectedAtt.formula | String | ❌ | 阅办人计算公式 | `getReaders(role)` |
| 代签人公式ID | right.insteadSignSelectedId | String | ❌ | 代签人公式的ID | `surrogate_formula` |
| 代签人公式 | right.insteadSignSelectedAtt.formula | String | ❌ | 代签人计算公式 | `getSurrogate(user)` |
| 是否允许代签 | right.canInsteadSign | Boolean | ❌ | 是否允许代签 | `true` |
| 代理人ID | right.surrogateId | String | ❌ | 代理人的ID | `agent001` |
| 代理人名称 | right.surrogateName | String | ❌ | 代理人的名称 | `代理审批人` |
| 是否允许退回 | right.canRouteBack | Boolean | ❌ | 是否允许退回 | `true` |
| 退回路径 | right.routeBackMethod | Enum | ❌ | 退回的路径方式 | `LAST` |
| 是否允许特送 | right.canSpecialSend | Boolean | ❌ | 是否允许特送 | `true` |
| 特送范围 | right.specialSendScope | Enum | ❌ | 特送的范围 | `ALL` |
| 是否允许补发 | right.canReSend | Boolean | ❌ | 是否允许补发 | `true` |
| 是否允许收回 | right.canTakeBack | Boolean | ❌ | 是否允许收回 | `true` |
| 办理后权限转移 | right.movePerformerTo | Enum | ❌ | 办理完成后权限转移目标 | `SPONSOR` |
| 阅办后权限转移 | right.moveReaderTo | Enum | ❌ | 阅办完成后权限转移目标 | `READER` |
| 发送人权限转移 | right.moveSponsorTo | Enum | ❌ | 发送人权限转移目标 | `PERFORMER` |

### 2.6 设备配置 (Device)

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 设备配置 | device | Map | ❌ | IoT设备相关配置 | `{"deviceId": "device001"}` |

### 2.7 服务配置 (Service)

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 服务配置 | service | Map | ❌ | 服务调用相关配置 | `{"url": "/api/service"}` |

### 2.8 事件配置 (Event)

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 事件配置 | event | Map | ❌ | 事件驱动相关配置 | `{"eventType": "MESSAGE"}` |

### 2.9 子流程配置 (Subflow)

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 子流程配置 | subflow | Map | ❌ | 子流程调用相关配置 | `{"processDefId": "sub_process"}` |

### 2.10 工具配置 (Tool)

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 工具配置 | tool | Map | ❌ | 工具调用相关配置 | `{"toolId": "email_sender"}` |

### 2.11 Agent配置 (AgentConfig)

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| Agent配置 | agentConfig | Map | ❌ | AI Agent相关配置 | 见Agent配置详细字段 |
| Agent ID | agentConfig.agentId | String | ❌ | Agent的唯一标识 | `agent001` |
| Agent名称 | agentConfig.agentName | String | ❌ | Agent的显示名称 | `智能审批助手` |
| Agent状态 | agentConfig.status | Enum | ❌ | Agent的运行状态 | `online` |
| Agent角色 | agentConfig.role | Enum | ❌ | Agent的角色类型 | `worker` |
| Agent类型 | agentConfig.agentType | Enum | ❌ | Agent的类型 | `LLM_AGENT` |
| LLM模型 | agentConfig.llmModel | String | ❌ | 使用的LLM模型 | `gpt-4` |
| 提示词模板 | agentConfig.promptTemplate | String | ❌ | Agent的提示词模板 | `你是一个审批助手...` |
| 最大Token数 | agentConfig.maxTokens | Integer | ❌ | 最大生成Token数 | `2048` |
| 温度参数 | agentConfig.temperature | Double | ❌ | 生成温度参数 | `0.7` |

### 2.12 场景配置 (SceneConfig)

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 场景配置 | sceneConfig | Map | ❌ | 场景驱动相关配置 | 见场景配置详细字段 |
| 场景组ID | sceneConfig.sceneGroupId | String | ❌ | 场景组的唯一标识 | `scene_group_001` |
| 场景类型 | sceneConfig.sceneType | Enum | ❌ | 场景的类型 | `AUTO` |
| 场景状态 | sceneConfig.status | Enum | ❌ | 场景的运行状态 | `ENABLED` |
| 知识库ID | sceneConfig.knowledgeBaseId | String | ❌ | 关联的知识库ID | `kb001` |
| 能力列表 | sceneConfig.capabilities | List | ❌ | 场景所需的能力列表 | `["llm-chat", "rag"]` |

### 2.13 扩展属性

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 扩展属性 | extendedAttributes | Map | ❌ | 自定义扩展属性键值对 | `{"customField": "value"}` |

---

## 📋 三、路由定义术语 (RouteDef)

### 3.1 基本属性

| 术语 | 英文 | 类型 | 必填 | 描述 | 示例值 |
|------|------|------|------|------|--------|
| 路由ID | routeDefId | String | ✅ | 路由定义的唯一标识符 | `route_001` |
| 源活动ID | fromActivityDefId | String | ✅ | 路由起始活动的ID | `submit` |
| 目标活动ID | toActivityDefId | String | ✅ | 路由目标活动的ID | `approve` |
| 路由名称 | name | String | ❌ | 路由的显示名称 | `提交到审批` |
| 路由条件 | condition | String | ❌ | 路由的条件表达式 | `amount > 1000` |
| 优先级 | priority | Integer | ❌ | 路由的优先级 | `1` |

---

## 📋 四、枚举类型术语

### 4.1 流程访问级别 (ProcessDefAccess)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `Public` | 公开流程 | Public Process - Independent process |
| `Private` | 私有流程 | Private Process - Subprocess |
| `Block` | 块流程 | Block Process - Block activity |

**后端映射**: `INDEPENDENT` / `SUBPROCESS` / `BLOCK`

### 4.2 流程发布状态 (ProcessDefVersionStatus)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `UNDER_REVISION` | 修订中 | Under Revision - Draft status |
| `RELEASED` | 已发布 | Released - Published status |
| `UNDER_TEST` | 测试中 | Under Test - Testing status |
| `FROZEN` | 已冻结 | Frozen - Frozen status |

**后端映射**: `DRAFT` / `PUBLISHED` / `TESTING` / `FROZEN`

### 4.3 活动类型 (ActivityType)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `TASK` | 用户任务 | User Task - Requires human interaction |
| `SERVICE` | 服务任务 | Service Task - Automated service call |
| `SCRIPT` | 脚本任务 | Script Task - Execute script code |
| `START` | 开始节点 | Start Event - Process start point |
| `END` | 结束节点 | End Event - Process end point |
| `XOR_GATEWAY` | 排他网关 | XOR Gateway - Exclusive branch |
| `AND_GATEWAY` | 并行网关 | AND Gateway - Parallel branch |
| `OR_GATEWAY` | 包容网关 | OR Gateway - Inclusive branch |
| `SUBPROCESS` | 子流程 | Subprocess - Nested process |
| `LLM_TASK` | LLM任务 | LLM Task - AI intelligent processing |

### 4.4 活动分类 (ActivityCategory)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `HUMAN` | 人工活动 | Human Activity - Requires human participation |
| `AGENT` | Agent活动 | Agent Activity - AI agent execution |
| `SCENE` | 场景活动 | Scene Activity - Scene-driven execution |

### 4.5 活动实现方式 (ActivityDefImpl)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `IMPL_NO` | 无实现 | No Implementation - Manual activity |
| `IMPL_TOOL` | 工具实现 | Tool Implementation - Call tool |
| `IMPL_SUBFLOW` | 子流程实现 | Subflow Implementation - Call subprocess |
| `IMPL_OUTFLOW` | 外部流程实现 | Outflow Implementation - Call external process |
| `IMPL_DEVICE` | 设备实现 | Device Implementation - IoT device interaction |
| `IMPL_EVENT` | 事件实现 | Event Implementation - Event-driven |
| `IMPL_SERVICE` | 服务实现 | Service Implementation - Service call |

**后端映射**: `No` / `Tool` / `SubFlow` / `OutFlow` / `Device` / `Event` / `Service`

### 4.6 活动位置 (ActivityDefPosition)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `START` | 开始位置 | Start Position - Process start activity |
| `NORMAL` | 正常位置 | Normal Position - Regular activity |
| `END` | 结束位置 | End Position - Process end activity |
| `VIRTUAL_LAST_DEF` | 虚拟最后定义 | Virtual Last Definition |

**后端映射**: `POSITION_START` / `POSITION_NORMAL` / `POSITION_END` / `VIRTUAL_LAST_DEF`

### 4.7 办理类型 (PerformType)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `SINGLE` | 单人办理 | Single Handler - One person handles |
| `MULTIPLE` | 多人办理 | Multiple Handlers - Multiple people handle |
| `JOINTSIGN` | 会签 | Countersign - Joint approval required |
| `NEEDNOTSELECT` | 无需选择 | No Selection Needed - Auto assignment |
| `NOSELECT` | 不选择 | No Selection - Skip assignment |
| `DEFAULT` | 默认 | Default - Use system default |

### 4.8 办理顺序 (PerformSequence)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `FIRST` | 第一人办理 | First Handler - First person handles |
| `SEQUENCE` | 顺序办理 | Sequential - Handle in order |
| `MEANWHILE` | 同时办理 | Simultaneous - Handle at same time |
| `AUTOSIGN` | 自动签收 | Auto Sign - Automatic assignment |
| `DEFAULT` | 默认 | Default - Use system default |

### 4.9 汇聚类型 (ActivityDefJoin)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `JOIN_AND` | 与汇聚 | AND Join - Wait for all inputs |
| `JOIN_XOR` | 异或汇聚 | XOR Join - Wait for any input |
| `DEFAULT` | 默认 | Default - Use system default |

**后端映射**: `AND` / `XOR` / `DEFAULT`

### 4.10 分支类型 (ActivityDefSplit)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `SPLIT_AND` | 与分支 | AND Split - Parallel execution |
| `SPLIT_XOR` | 异或分支 | XOR Split - Conditional branch |
| `DEFAULT` | 默认 | Default - Use system default |

**后端映射**: `AND` / `XOR` / `DEFAULT`

### 4.11 到期处理 (DeadlineOperation)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `DEFAULT` | 默认处理 | Default - Use system default |
| `DELAY` | 延期处理 | Delay - Extend deadline |
| `TAKEBACK` | 收回处理 | Takeback - Reclaim task |
| `SURROGATE` | 代理处理 | Surrogate - Delegate to proxy |

### 4.12 退回路径 (RouteBackMethod)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `DEFAULT` | 默认 | Default - Use system default |
| `LAST` | 上一环节 | Last Activity - Return to previous |
| `ANY` | 任意环节 | Any Activity - Return to any |
| `SPECIFY` | 指定环节 | Specify Activity - Return to specified |

### 4.13 特送范围 (SpecialSendScope)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `DEFAULT` | 默认 | Default - Use system default |
| `ALL` | 所有人 | All Users - Send to everyone |
| `PERFORMERS` | 办理人 | Performers - Send to handlers |

### 4.14 权限组 (RightGroup)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `PERFORMER` | 办理人 | Performer - Task handler |
| `SPONSOR` | 发起人 | Sponsor - Process initiator |
| `READER` | 阅办人 | Reader - Read-only participant |
| `HISTORYPERFORMER` | 历史办理人 | History Performer - Previous handler |
| `HISSPONSOR` | 历史发起人 | History Sponsor - Previous initiator |
| `HISTORYREADER` | 历史阅办人 | History Reader - Previous reader |
| `NORIGHT` | 无权限 | No Right - No permission |
| `NULL` | 访客组 | Null Group - Guest group |

### 4.15 时间单位 (DurationUnit)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `Y` | 年 | Year |
| `M` | 月 | Month |
| `D` | 天 | Day |
| `H` | 小时 | Hour |
| `m` | 分钟 | Minute |
| `s` | 秒 | Second |
| `W` | 周 | Week |

### 4.16 表单标识类型 (MarkEnum)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `ProcessInst` | 流程实例级 | Process Instance Level - Global form |
| `ActivityInst` | 活动实例级 | Activity Instance Level - Activity form |
| `Person` | 人员级 | Person Level - Personal form |
| `ActivityInstPerson` | 活动人员级 | Activity Person Level - Task form |

**后端映射**: `GLOBAL` / `ACTIVITY` / `PERSON` / `ACTIVITY_PERSON`

### 4.17 锁定策略 (LockEnum)

| 枚举值 | 中文描述 | 英文描述 |
|--------|----------|----------|
| `Msg` | 消息锁定 | Message Lock - Lock with message |
| `Lock` | 锁定 | Lock - Direct lock |
| `Person` | 人员锁定 | Person Lock - Lock to person |
| `Last` | 最后锁定 | Last Lock - Lock to last handler |
| `NO` | 不锁定 | No Lock - No locking |

**后端映射**: `MSG` / `LOCK` / `PERSON` / `LAST` / `NO_LOCK`

---

## 📋 五、扩展属性规范

### 5.1 扩展属性定义规范

扩展属性 (`extendedAttributes`) 是一个键值对映射，用于存储自定义的业务属性。

**命名规范**:
- 键名使用小驼峰命名法：`customField`
- 键名应具有业务含义：`approvalLevel`
- 避免使用保留字段名

**值类型规范**:
- 基本类型：String, Integer, Boolean, Double
- 复杂类型：Map, List
- 日期格式：`yyyy-MM-dd HH:mm:ss`

**示例**:
```json
{
  "extendedAttributes": {
    "approvalLevel": 2,
    "isUrgent": true,
    "customField": "value",
    "metadata": {
      "key1": "value1",
      "key2": "value2"
    },
    "tags": ["tag1", "tag2"]
  }
}
```

### 5.2 常用扩展属性

| 属性名 | 类型 | 描述 | 适用范围 |
|--------|------|------|----------|
| `approvalLevel` | Integer | 审批级别 | Activity |
| `isUrgent` | Boolean | 是否紧急 | Process, Activity |
| `department` | String | 所属部门 | Process |
| `businessType` | String | 业务类型 | Process |
| `notifyUsers` | List | 通知用户列表 | Activity |
| `customActions` | Map | 自定义操作 | Activity |

---

## 📋 六、术语使用指南

### 6.1 术语查找

1. **按分类查找**: 根据术语所属的分类（流程、活动、路由、枚举）查找
2. **按名称查找**: 根据中文或英文名称查找
3. **按类型查找**: 根据字段类型（String, Enum, Map）查找

### 6.2 术语使用规范

1. **命名一致性**: 在代码、文档、界面中使用统一的术语名称
2. **枚举值规范**: 使用枚举值时，确保使用正确的前端值或后端值
3. **扩展属性**: 使用扩展属性时，遵循命名规范和值类型规范

### 6.3 术语更新

当添加新术语时，请：
1. 在本文档中添加术语定义
2. 更新相关的 DTO 类
3. 更新 EnumMapping.js（如果是枚举类型）
4. 更新 PanelSchema.js（如果需要界面配置）

---

**文档生成时间**: 2026-04-08  
**文档路径**: E:\github\ooder-skills\docs\bpm-designer\bpm-terminology-glossary.md
