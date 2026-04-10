# BPM 属性匹配检查文档

## 使用说明

本文档用于逐项检查前后端属性的匹配情况。每个属性都需要确认：
1. ✅ 已匹配 - 前后端字段一致，数据类型兼容
2. ⚠️ 不匹配 - 字段名不同或数据类型不兼容，需要映射转换
3. ❌ 缺失 - 前端缺少该字段，需要添加
4. 📝 多余 - 后端没有该字段，前端特有

---

## 一、ProcessDef 级别属性检查

### 1.1 基本属性 (basic)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 1 | processDefId | processDefId | ✅ | string | string | | | |
| 2 | name | name | ✅ | string | string | | | |
| 3 | description | description | ✅ | string | string | | | |
| 4 | classification | category | ⚠️ | string | string | | | 字段名不同 |
| 5 | classification | classification | ❌ | string | - | | | 前端缺失 |
| 6 | systemCode | systemCode | ❌ | string | - | | | 前端缺失 |
| 7 | accessLevel | accessLevel | ❌ | string | - | | | 前端缺失 |

### 1.2 版本信息 (version)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 8 | version | version | ✅ | integer | integer | | | |
| 9 | state | status | ⚠️ | string | string | | | 字段名不同，枚举值需确认 |
| 10 | state | state | ❌ | string | - | | | 前端缺失 |
| 11 | creatorName | creatorName | ❌ | string | - | | | 前端缺失 |
| 12 | modifierId | modifierId | ❌ | string | - | | | 前端缺失 |
| 13 | modifierName | modifierName | ❌ | string | - | | | 前端缺失 |
| 14 | modifyTime | modifyTime | ❌ | datetime | - | | | 前端缺失 |
| 15 | modifyTime | updatedTime | ⚠️ | datetime | datetime | | | 字段名不同 |
| 16 | createTime | createTime | ❌ | datetime | - | | | 前端缺失 |
| 17 | createTime | createdTime | ⚠️ | datetime | datetime | | | 字段名不同 |
| 18 | activeTime | activeTime | ❌ | datetime | - | | | 前端缺失 |
| 19 | freezeTime | freezeTime | ❌ | datetime | - | | | 前端缺失 |

### 1.3 时限配置 (timing)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 20 | limit | limit | ❌ | integer | - | | | 前端缺失 |
| 21 | durationUnit | durationUnit | ❌ | string | - | | | 前端缺失 |

### 1.4 XPDL格式节点 (xpdl)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 22 | startNode | startNode | ❌ | object | - | | | 前端缺失，关键字段 |
| 23 | startNode.participantId | - | ❌ | string | - | | | 前端缺失 |
| 24 | startNode.firstActivityId | - | ❌ | string | - | | | 前端缺失 |
| 25 | startNode.positionCoord | - | ❌ | object | - | | | 前端缺失 |
| 26 | startNode.routing | - | ❌ | string | - | | | 前端缺失 |
| 27 | endNodes | endNodes | ❌ | array | - | | | 前端缺失，关键字段 |
| 28 | endNodes[].participantId | - | ❌ | string | - | | | 前端缺失 |
| 29 | endNodes[].lastActivityId | - | ❌ | string | - | | | 前端缺失 |
| 30 | endNodes[].positionCoord | - | ❌ | object | - | | | 前端缺失 |
| 31 | endNodes[].routing | - | ❌ | string | - | | | 前端缺失 |

### 1.5 XML格式配置 (xml)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 32 | listeners | listeners | ✅ | array | array | | | 需确认结构 |
| 33 | listeners[].id | - | ❓ | string | - | | | 需确认 |
| 34 | listeners[].name | - | ❓ | string | - | | | 需确认 |
| 35 | listeners[].event | - | ❓ | string | - | | | 需确认 |
| 36 | listeners[].realizeClass | - | ❓ | string | - | | | 需确认 |
| 37 | rightGroups | rightGroups | ❌ | array | - | | | 前端缺失，关键字段 |
| 38 | rightGroups[].id | - | ❌ | string | - | | | 前端缺失 |
| 39 | rightGroups[].name | - | ❌ | string | - | | | 前端缺失 |
| 40 | rightGroups[].code | - | ❌ | string | - | | | 前端缺失 |
| 41 | rightGroups[].order | - | ❌ | integer | - | | | 前端缺失 |
| 42 | rightGroups[].defaultGroup | - | ❌ | boolean | - | | | 前端缺失 |

### 1.6 子元素 (children)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 43 | activities | activities | ✅ | array | array | | | 需检查内部字段 |
| 44 | routes | routes | ✅ | array | array | | | 需检查内部字段 |

### 1.7 前端多余字段

| 序号 | 前端字段 | 后端字段 | 状态 | 处理建议 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|--------|----------|------|
| 45 | formulas | - | 📝 | 放入extendedAttributes | | | |
| 46 | parameters | - | 📝 | 放入extendedAttributes | | | |
| 47 | activitySets | - | 📝 | 放入extendedAttributes | | | |
| 48 | subProcessRefs | - | 📝 | 放入extendedAttributes | | | |
| 49 | agentConfig | - | 📝 | 放入extendedAttributes | | | |
| 50 | sceneConfig | - | 📝 | 放入extendedAttributes | | | |

---

## 二、ActivityDef 级别属性检查

### 2.1 基本属性 (basic)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 51 | activityDefId | activityDefId | ✅ | string | string | | | |
| 52 | name | name | ✅ | string | string | | | |
| 53 | description | description | ✅ | string | string | | | |
| 54 | position | position | ✅ | string | string | | | 枚举值需确认 |
| 55 | activityType | activityType | ✅ | string | string | | | 枚举值需确认 |
| 56 | activityType | activityCategory | 📝 | - | string | | | 前端多余字段 |

### 2.2 BPD属性 (bpd)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 57 | positionCoord | positionCoord | ✅ | object | object | | | |
| 58 | positionCoord.x | positionCoord.x | ✅ | integer | integer | | | |
| 59 | positionCoord.y | positionCoord.y | ✅ | integer | integer | | | |
| 60 | participantId | participantId | ❌ | string | - | | | 前端缺失，关键字段 |
| 61 | implementation | implementation | ✅ | string | string | | | |
| 62 | implementation | performerType | 📝 | - | string | | | 前端多余字段 |

### 2.3 时限配置 (timing)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 63 | limitTime | limit | ⚠️ | integer | integer | | | 字段名不同 |
| 64 | limitTime | limitTime | ❌ | integer | - | | | 前端缺失 |
| 65 | alertTime | alertTime | ✅ | integer | integer | | | |
| 66 | durationUnit | durationUnit | ✅ | string | string | | | |

### 2.4 流程控制 (flow)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 67 | join | join | ✅ | string | string | | | |
| 68 | split | split | ✅ | string | string | | | |
| 69 | canRouteBack | canRouteBack | ⚠️ | string(YES/NO) | boolean | | | 类型不匹配 |
| 70 | routeBackMethod | routeBackMethod | ✅ | string | string | | | |
| 71 | canSpecialSend | canSpecialSend | ⚠️ | string(YES/NO) | boolean | | | 类型不匹配 |
| 72 | specialScope | specialSendScope | ⚠️ | string | string | | | 字段名不同 |
| 73 | specialScope | specialScope | ❌ | string | - | | | 前端缺失 |

### 2.5 RIGHT属性组 (attributeGroup)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 74 | RIGHT | RIGHT | ❌ | object | - | | | 前端缺失属性组结构 |
| 75 | RIGHT.performType | performType | ⚠️ | string | string | | | 需组织为属性组 |
| 76 | RIGHT.performSequence | performSequence | ⚠️ | string | string | | | 需组织为属性组 |
| 77 | RIGHT.specialSendScope | - | ❌ | string | - | | | 前端缺失 |
| 78 | RIGHT.canInsteadSign | canInsteadSign | ⚠️ | string(YES/NO) | boolean | | | 类型不匹配 |
| 79 | RIGHT.canTakeBack | canTakeBack | ⚠️ | string(YES/NO) | boolean | | | 类型不匹配 |
| 80 | RIGHT.canReSend | canReSend | ⚠️ | string(YES/NO) | boolean | | | 类型不匹配 |
| 81 | RIGHT.insteadSignSelected | - | ❌ | string | - | | | 前端缺失 |
| 82 | RIGHT.performerSelectedId | performerSelectedAtt | ⚠️ | string | array | | | 类型不匹配 |
| 83 | RIGHT.readerSelectedId | - | ❌ | string | - | | | 前端缺失 |
| 84 | RIGHT.movePerformerTo | - | ❌ | string | - | | | 前端缺失 |
| 85 | RIGHT.moveSponsorTo | - | ❌ | string | - | | | 前端缺失 |
| 86 | RIGHT.moveReaderTo | - | ❌ | string | - | | | 前端缺失 |
| 87 | RIGHT.surrogateId | - | ❌ | string | - | | | 前端缺失 |
| 88 | RIGHT.surrogateName | - | ❌ | string | - | | | 前端缺失 |

### 2.6 FORM属性组 (attributeGroup)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 89 | FORM | FORM | ❌ | object | - | | | 前端缺失整个属性组 |
| 90 | FORM.formId | - | ❌ | string | - | | | 前端缺失 |
| 91 | FORM.formName | - | ❌ | string | - | | | 前端缺失 |
| 92 | FORM.formType | - | ❌ | string | - | | | 前端缺失 |
| 93 | FORM.formUrl | - | ❌ | string | - | | | 前端缺失 |

### 2.7 SERVICE属性组 (attributeGroup)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 94 | SERVICE | SERVICE | ❌ | object | - | | | 前端缺失整个属性组 |
| 95 | SERVICE.httpMethod | - | ❌ | string | - | | | 前端缺失 |
| 96 | SERVICE.httpUrl | - | ❌ | string | - | | | 前端缺失 |
| 97 | SERVICE.httpRequestType | - | ❌ | string | - | | | 前端缺失 |
| 98 | SERVICE.httpResponseType | - | ❌ | string | - | | | 前端缺失 |
| 99 | SERVICE.httpServiceParams | - | ❌ | string | - | | | 前端缺失 |
| 100 | SERVICE.serviceSelectedId | - | ❌ | string | - | | | 前端缺失 |

### 2.8 WORKFLOW属性组 (attributeGroup)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 101 | WORKFLOW | WORKFLOW | ❌ | object | - | | | 前端缺失属性组结构 |
| 102 | WORKFLOW.deadLineOperation | deadlineOperation | ⚠️ | string | string | | | 大小写不同 |
| 103 | WORKFLOW.specialScope | specialSendScope | ⚠️ | string | string | | | 字段名不同 |

### 2.9 块活动属性 (block)

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 104 | startOfBlock | startOfBlock | ❌ | object | - | | | 前端缺失 |
| 105 | endOfBlock | endOfBlock | ❌ | object | - | | | 前端缺失 |
| 106 | participantVisualOrder | participantVisualOrder | ❌ | string | - | | | 前端缺失 |

### 2.10 前端多余字段

| 序号 | 前端字段 | 后端字段 | 状态 | 处理建议 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|--------|----------|------|
| 107 | agentConfig | - | 📝 | 放入extendedAttributes | | | |
| 108 | sceneConfig | - | 📝 | 放入extendedAttributes | | | |
| 109 | listeners | - | 📝 | 放入extendedAttributes | | | |

---

## 三、RouteDef 级别属性检查

| 序号 | 后端字段 | 前端字段 | 状态 | 后端类型 | 前端类型 | 检查人 | 检查日期 | 备注 |
|------|---------|---------|------|---------|---------|--------|----------|------|
| 110 | routeDefId | routeDefId | ✅ | string | string | | | |
| 111 | routeDefId | id | ⚠️ | string | string | | | 字段名不同 |
| 112 | name | name | ✅ | string | string | | | |
| 113 | description | description | ✅ | string | string | | | |
| 114 | fromActivityDefId | from | ⚠️ | string | string | | | 字段名不同 |
| 115 | fromActivityDefId | fromActivityDefId | ✅ | string | string | | | |
| 116 | toActivityDefId | to | ⚠️ | string | string | | | 字段名不同 |
| 117 | toActivityDefId | toActivityDefId | ✅ | string | string | | | |
| 118 | routeOrder | order | ⚠️ | integer | integer | | | 字段名不同 |
| 119 | routeOrder | routeOrder | ✅ | integer | integer | | | |
| 120 | routeDirection | direction | ⚠️ | string | string | | | 字段名不同 |
| 121 | routeDirection | routeDirection | ✅ | string | string | | | |
| 122 | routeCondition | condition | ⚠️ | string | string | | | 字段名不同 |
| 123 | routeCondition | routeCondition | ✅ | string | string | | | |
| 124 | routeConditionType | conditionType | ⚠️ | string | string | | | 字段名不同 |
| 125 | routeConditionType | routeConditionType | ✅ | string | string | | | |
| 126 | routing | routing | ❌ | string | - | | | 前端缺失 |

---

## 四、检查结果汇总

### 4.1 统计信息

| 类别 | 数量 | 占比 |
|------|------|------|
| ✅ 已匹配 | | |
| ⚠️ 不匹配 | | |
| ❌ 缺失 | | |
| 📝 多余 | | |
| **总计** | | 100% |

### 4.2 按优先级统计

| 优先级 | 数量 | 字段列表 |
|--------|------|---------|
| P0 (关键) | | |
| P1 (重要) | | |
| P2 (一般) | | |

### 4.3 按类别统计

| 类别 | 已匹配 | 不匹配 | 缺失 | 总计 |
|------|--------|--------|------|------|
| ProcessDef-basic | | | | |
| ProcessDef-version | | | | |
| ProcessDef-timing | | | | |
| ProcessDef-xpdl | | | | |
| ProcessDef-xml | | | | |
| ActivityDef-basic | | | | |
| ActivityDef-bpd | | | | |
| ActivityDef-timing | | | | |
| ActivityDef-flow | | | | |
| ActivityDef-属性组 | | | | |
| RouteDef | | | | |

---

## 五、处理计划

### 5.1 第一阶段：P0级别修复 (紧急)

| 序号 | 字段 | 处理方案 | 负责人 | 计划完成日期 | 实际完成日期 |
|------|------|---------|--------|-------------|-------------|
| | startNode | 添加对象结构 | | | |
| | endNodes | 添加数组结构 | | | |
| | rightGroups | 添加数组结构 | | | |
| | participantId | 添加字段 | | | |
| | RIGHT属性组 | 重构为属性组 | | | |

### 5.2 第二阶段：P1级别修复 (重要)

| 序号 | 字段 | 处理方案 | 负责人 | 计划完成日期 | 实际完成日期 |
|------|------|---------|--------|-------------|-------------|
| | FORM属性组 | 添加属性组 | | | |
| | SERVICE属性组 | 添加属性组 | | | |
| | 数据类型转换 | 添加转换逻辑 | | | |

### 5.3 第三阶段：P2级别完善 (一般)

| 序号 | 字段 | 处理方案 | 负责人 | 计划完成日期 | 实际完成日期 |
|------|------|---------|--------|-------------|-------------|
| | 版本信息字段 | 添加字段 | | | |
| | 时限配置 | 添加字段 | | | |

---

## 六、验证记录

### 6.1 单元测试验证

| 测试项 | 测试日期 | 测试结果 | 测试人 | 备注 |
|--------|---------|---------|--------|------|
| DataAdapter.toBackend | | | | |
| DataAdapter.fromBackend | | | | |
| PropertyChecker.runFullCheck | | | | |

### 6.2 集成测试验证

| 测试场景 | 测试日期 | 测试结果 | 测试人 | 备注 |
|--------|---------|---------|--------|------|
| 保存流程 | | | | |
| 读取流程 | | | | |
| 完整闭环 | | | | |

### 6.3 回归测试验证

| 测试项 | 测试日期 | 测试结果 | 测试人 | 备注 |
|--------|---------|---------|--------|------|
| 历史数据兼容 | | | | |
| 边界情况 | | | | |
| 性能测试 | | | | |

---

## 七、审计日志

| 日期 | 操作人 | 操作类型 | 内容 | 备注 |
|------|--------|---------|------|------|
| | | 创建文档 | 初始化检查文档 | |
| | | 属性检查 | 完成ProcessDef检查 | |
| | | 属性检查 | 完成ActivityDef检查 | |
| | | 属性检查 | 完成RouteDef检查 | |
| | | 修复完成 | 完成P0级别修复 | |
| | | 验证通过 | 所有属性闭环验证通过 | |

---

## 附录

### A. 状态图例
- ✅ 已匹配：前后端完全一致
- ⚠️ 不匹配：需要映射转换
- ❌ 缺失：前端需要添加
- 📝 多余：后端没有，前端特有
- ❓ 待确认：需要进一步确认

### B. 优先级定义
- P0：关键属性，影响核心功能
- P1：重要属性，影响功能完整性
- P2：一般属性，优化体验

### C. 相关文件
- DataAdapter.js - 数据适配器
- PropertyChecker.js - 属性检查器
- BpmJsonSchema.js - JSON Schema定义
