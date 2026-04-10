

---

## 4. JS版设计器与Swing版对比

### 4.1 活动类型对比

| 活动类型 | Swing版 | JS版 | 差异说明 |
|----------|---------|------|----------|
| 无实现(No) | ✅ | ✅ IMPL_NO | 一致 |
| 工具(Tool) | ✅ | ✅ IMPL_TOOL | 一致 |
| 子流程(SubFlow) | ✅ | ✅ IMPL_SUBFLOW | 一致 |
| 外部流程(OutFlow) | ✅ | ✅ IMPL_OUTFLOW | 一致 |
| 块活动(Block) | ✅ | ✅ IMPL_BLOCK | 一致 |
| 设备(Device) | ✅ | ✅ IMPL_DEVICE | 一致 |
| 服务(Service) | ✅ | ✅ IMPL_SERVICE | 一致 |
| 事件(Event) | ✅ | ✅ IMPL_EVENT | 一致 |
| **Agent** | ❌ | ✅ IMPL_AGENT | **JS版新增** |
| **场景(Scene)** | ❌ | ✅ IMPL_SCENE | **JS版新增** |
| **路由(Route)** | ❌ | ✅ IMPL_ROUTE | **JS版新增** |

### 4.2 开始/结束节点对比

| 属性 | Swing版 | JS版 | 差异 |
|------|---------|------|------|
| Position | START/END | START/END | 一致 |
| PositionCoord | ✅ | ✅ | 一致 |
| **activityType** | 无 | START/END | **JS版新增** |
| **description** | 无 | 有 | **JS版新增** |

**差异分析**：
- Swing版通过`Position`属性区分开始/结束节点
- JS版新增`activityType`字段，与`Position`冗余
- JS版为开始/结束节点添加了`description`字段

### 4.3 普通活动属性对比

#### 4.3.1 基本属性对比

| 属性 | Swing版 | JS版 | 差异 |
|------|---------|------|------|
| Position | ✅ NORMAL | ✅ NORMAL | 一致 |
| PositionCoord | ✅ | ✅ | 一致 |
| Implementation | ✅ | ✅ implementation | 一致 |
| **activityType** | 无 | ✅ TASK/SERVICE等 | **JS版新增** |
| **activityCategory** | 无 | ✅ HUMAN/AGENT/SCENE | **JS版新增** |
| **performerType** | 无 | ✅ HUMAN/SYSTEM/AGENT | **JS版新增** |

#### 4.3.2 时限属性对比

| 属性 | Swing版 | JS版 | 差异 |
|------|---------|------|------|
| DurationUnit | ✅ Y/M/D/H/m/s/W | ✅ Y/M/D/H/m/s/W | 一致 |
| AlertTime | ✅ | ✅ | 一致 |
| Limit | ✅ | ✅ | 一致 |
| DeadLineOperation | ✅ DEFAULT/NOTIFY/ESCALATE/TERMINATE | ✅ DEFAULT/DELAY/TAKEBACK | **枚举值不同** |

**差异分析**：
- JS版`DeadLineOperation`枚举值简化，缺少`NOTIFY/ESCALATE/TERMINATE`
- JS版新增`DELAY`选项

#### 4.3.3 路由控制属性对比

| 属性 | Swing版 | JS版 | 差异 |
|------|---------|------|------|
| CanRouteBack | ✅ YES/NO | ✅ true/false | **类型不同** |
| RouteBackMethod | ✅ PREV/START/SPECIFIED | ✅ DEFAULT/LAST/ANY/SPECIFY | **枚举值不同** |
| CanSpecialSend | ✅ YES/NO | ✅ true/false | **类型不同** |
| SpecialScope | ✅ ALL/SAME_GROUP/SPECIFIED | ✅ DEFAULT/ALL/PERFORMERS | **枚举值不同** |

**差异分析**：
- Swing版使用字符串`YES/NO`，JS版使用布尔值
- `RouteBackMethod`枚举值差异较大
- `SpecialScope`枚举值差异较大

#### 4.3.4 汇聚分支属性对比

| 属性 | Swing版 | JS版 | 差异 |
|------|---------|------|------|
| Join | ✅ AND/XOR/OR | ✅ DEFAULT/JOIN_AND/JOIN_XOR | **枚举值不同** |
| Split | ✅ AND/XOR/OR | ✅ DEFAULT/SPLIT_AND/SPLIT_XOR | **枚举值不同** |

**差异分析**：
- JS版增加`DEFAULT`选项
- JS版使用`JOIN_`/`SPLIT_`前缀

### 4.4 权限属性对比

| 属性 | Swing版 | JS版 | 差异 |
|------|---------|------|------|
| PerformType | ✅ SINGLE/MULTIPLE/JOINTSIGN | ✅ DEFAULT/SINGLE/MULTIPLE/JOINTSIGN | JS版增加DEFAULT |
| PerformSequence | ✅ FIRST/SEQUENCE/MEANWHILE | ✅ DEFAULT/FIRST/SEQUENCE/MEANWHILE/AUTOSIGN | JS版增加DEFAULT和AUTOSIGN |
| CanInsteadSign | ✅ YES/NO | ✅ true/false | 类型不同 |
| CanTakeBack | ✅ YES/NO | ✅ true/false | 类型不同 |
| CanReSend | ✅ YES/NO | ✅ true/false | 类型不同 |
| **skipable** | 无 | ✅ | **JS版新增** |
| **allowDelegate** | 无 | ✅ | **JS版新增** |
| **allowTransfer** | 无 | ✅ | **JS版新增** |
| **priority** | 无 | ✅ | **JS版新增** |

**差异分析**：
- JS版权限属性使用布尔值替代字符串
- JS版新增多个权限控制字段

---

## 5. 后端实现与前端对比

### 5.1 数据类型映射问题

| 属性 | Swing版(XPDL) | JS版(JSON) | 后端(DB) | 问题 |
|------|---------------|------------|----------|------|
| CanRouteBack | YES/NO | true/false | String | **类型不匹配** |
| CanSpecialSend | YES/NO | true/false | String | **类型不匹配** |
| CanInsteadSign | YES/NO | true/false | String | **类型不匹配** |
| Position | START/END/NORMAL | START/END/NORMAL | String | 一致 |
| Join/Split | AND/XOR/OR | JOIN_XXX/SPLIT_XXX | String | **枚举值不匹配** |

### 5.2 属性存储位置

| 属性组 | Swing版 | JS版 | 后端 | 状态 |
|--------|---------|------|------|------|
| BPD属性 | BPM_PROCESSDEF_PROPERTY | 流程JSON | BPM_PROCESSDEF_PROPERTY | 需统一 |
| WORKFLOW属性 | BPM_PROCESSDEF_PROPERTY | 活动JSON | BPM_PROCESSDEF_PROPERTY | 需统一 |
| RIGHT属性 | BPM_PROCESSDEF_PROPERTY | 活动JSON | BPM_PROCESSDEF_PROPERTY | 需统一 |
| FORM属性 | BPM_PROCESSDEF_PROPERTY | 未实现 | BPM_PROCESSDEF_PROPERTY | JS版缺失 |
| SERVICE属性 | BPM_PROCESSDEF_PROPERTY | 未实现 | BPM_PROCESSDEF_PROPERTY | JS版缺失 |

---

## 6. 问题与建议

### 6.1 主要问题

1. **数据类型不一致**
   - Swing版使用`YES/NO`字符串，JS版使用布尔值
   - 后端数据库存储为字符串，需要统一转换逻辑

2. **枚举值不一致**
   - `RouteBackMethod`、`SpecialScope`、`DeadLineOperation`等枚举值差异大
   - `Join`/`Split`枚举值命名规范不同

3. **属性缺失**
   - JS版缺少FORM属性组完整实现
   - JS版缺少SERVICE属性组完整实现
   - JS版缺少DEVICE属性组完整实现

4. **冗余字段**
   - JS版`activityType`与`Position`字段冗余
   - JS版`activityCategory`与`Implementation`部分冗余

### 6.2 改进建议

#### 6.2.1 数据类型统一

```javascript
// 建议统一使用字符串枚举，与后端保持一致
const YesNoEnum = {
    YES: 'YES',
    NO: 'NO'
};

// 前端展示时转换
function toBoolean(value) {
    return value === 'YES';
}

function fromBoolean(value) {
    return value ? 'YES' : 'NO';
}
```

#### 6.2.2 枚举值统一

| 属性 | 建议统一值 | 说明 |
|------|------------|------|
| RouteBackMethod | PREV/START/SPECIFIED | 采用Swing版 |
| SpecialScope | ALL/SAME_GROUP/SPEC